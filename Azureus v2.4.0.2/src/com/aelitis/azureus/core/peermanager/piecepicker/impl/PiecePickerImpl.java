/*
 * Created by Joseph Bridgewater
 * Created on Jan 2, 2006
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.peermanager.piecepicker.impl;

import java.util.*;

import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.peer.*;
import org.gudy.azureus2.core3.peer.impl.*;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.peermanager.piecepicker.*;
import com.aelitis.azureus.core.peermanager.piecepicker.priority.PiecePriorityShaper;
import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
import com.aelitis.azureus.core.peermanager.unchoker.UnchokerUtil;

/**
 * @author MjrTom
 * 
 */

public class PiecePickerImpl
	implements PiecePicker
{
	private static final LogIDs LOGID = LogIDs.PIECES;

    /** min ms for recalculating availability - reducing this has serious ramifications */
    private static final long TIME_MIN_AVAILABILITY	=974;
    /** min ms for recalculating base priorities */
    private static final long TIME_MIN_PRIORITIES	=999;
    /** min ms for forced availability rebuild */
    private static final long TIME_AVAIL_REBUILD	=5*60*1000; 

	// The following are added to the base User setting based priorities (for all inspected pieces)
    /** user select prioritize first/last */
	private static final int PRIORITY_W_FIRSTLAST	=1300;
    /** min # pieces in file for first/last prioritization */
    private static final long FIRST_PIECE_MIN_NB	=4;
    /** user sets file as "High" */
    private static final int PRIORITY_W_FILE		=1000;
    /** Additional boost for more completed High priority */
    private static final int PRIORITY_W_COMPLETION	=1000;
    
    /** Additional boost for globally rarest piece */
    private static final int PRIORITY_W_RAREST		=1300;
    /** boost for rarity */
    private static final int PRIORITY_W_RARE		=2300;

	// The following are only used when resuming already running pieces
    /** priority boost due to being too old */
    private static final int PRIORITY_W_AGE		=900;
    /** ms a block is expected to complete in */
    private static final int PRIORITY_DW_AGE		=60 *1000;
    /** ms since last write */
    private static final int PRIORITY_DW_STALE		=120 *1000;
    /** finish pieces already almost done */
    private static final int PRIORITY_W_PIECE_DONE	=900;
    /** keep working on same piece */
    private static final int PRIORITY_W_SAME_PIECE	=700;

    /** Min number of requests sent to a peer */
    private static final int REQUESTS_MIN	=2;
    /** Max number of request sent to a peer */
    private static final int REQUESTS_MAX	=256;
	/** Default number of requests sent to a peer, (for each X B/s another request will be used) */
	private static final int SLOPE_REQUESTS	=4 *1024;
	
	private static final long END_GAME_MODE_SIZE_TRIGGER	=20 *1024 *1024;
	private static final long END_GAME_MODE_TIMEOUT			=60 *END_GAME_MODE_SIZE_TRIGGER /16384;
	
	protected static volatile boolean	firstPiecePriority	=COConfigurationManager.getBooleanParameter("Prioritize First Piece", false);
	protected static volatile boolean	completionPriority	=COConfigurationManager.getBooleanParameter("Prioritize Most Completed Files", false);
    /** event # of user settings controlling priority changes */
    protected static volatile long		paramPriorityChange =Long.MIN_VALUE;

	private final DiskManager			diskManager;
	private final PEPeerControl			peerControl;
	private final PiecePriorityShaper	priorityShaper;
	
	private final DiskManagerListenerImpl	diskManagerListener;
	
	protected final Map					peerListeners;
	private final PEPeerManagerListener	peerManagerListener;
	
	protected final int			nbPieces;
	protected final DiskManagerPiece[]	dmPieces;

	protected final AEMonitor availabilityMon = new AEMonitor("PiecePicker:avail");
	private final AEMonitor endGameModeChunks_mon =new AEMonitor("PiecePicker:EGM");

	protected volatile int	nbPiecesDone;
	
    /** asyncronously updated availability */
    protected volatile int[]	availabilityAsynch;
    /** indicates availability needs to be recomputed due to detected drift */
    protected volatile long		availabilityDrift;
	
    /** periodically updated consistent view of availability for calculating */
    protected volatile int[]	availability;
	
	private long			time_last_avail;
	protected volatile long	availabilityChange;
	private volatile long	availabilityComputeChange;
	private long			time_last_rebuild;
	
	private float		globalAvail;
	private float		globalAvgAvail;
	private int			nbRarestActive;
	private int			globalMin;
	/**
	 * The rarest availability level of pieces that we affirmatively want to try to request from others soonest
	 * ie; our prime targets for requesting rarest pieces
	 */
	private volatile int		globalMinOthers;
	
    /** event # of user file priority settings changes */
    protected volatile long		filePriorityChange;
	
    /** last user parameter settings event # when priority bases were calculated */
    private volatile long		priorityParamChange;
    /** last user priority event # when priority bases were calculated */
    private volatile long		priorityFileChange;
    /** last availability event # when priority bases were calculated */
    private volatile long		priorityAvailChange;
	
    /** time that base priorities were last computed */
    private long				timeLastPriorities;
	
    /** the priority for starting each piece/base priority for resuming */
    private int[]				startPriorities;
	
	protected volatile boolean	hasNeededUndonePiece;
	protected volatile long		neededUndonePieceChange;
	
	/** A flag to indicate when we're in endgame mode */
	private volatile boolean	endGameMode;
	private volatile boolean	endGameModeAbandoned;
	private volatile long		timeEndGameModeEntered;
	/** The list of chunks needing to be downloaded (the mechanism change when entering end-game mode) */
	private List 				endGameModeChunks;
	
	static
	{
		class ParameterListenerImpl
			implements ParameterListener
		{
			public void parameterChanged(String parameterName)
			{
				if (parameterName.equals("Prioritize Most Completed Files"))
				{
					completionPriority =COConfigurationManager.getBooleanParameter(parameterName, false);
					paramPriorityChange++;	// this is a user's priority change event
				} else if (parameterName.equals("Prioritize First Piece"))
				{
					firstPiecePriority =COConfigurationManager.getBooleanParameter(parameterName, false);
					paramPriorityChange++;	// this is a user's priority change event
			    }
		    }
		}

		ParameterListenerImpl	parameterListener =new ParameterListenerImpl();;

		COConfigurationManager.addParameterListener("Prioritize Most Completed Files", parameterListener);
		COConfigurationManager.addAndFireParameterListener("Prioritize First Piece", parameterListener);

	}
	
	
	public PiecePickerImpl(final PEPeerControl pc)
	{
		// class administration first
		priorityShaper =null;	//PiecePriorityShaperFactory.create(this);
		peerControl	= pc;
		diskManager = peerControl.getDiskManager();
		dmPieces =diskManager.getPieces();

 		nbPieces =diskManager.getNbPieces();
		nbPiecesDone =0;
		
		
		// now do stuff related to availability
		availability =new int[nbPieces];  //always needed
		
		hasNeededUndonePiece =false;
		neededUndonePieceChange =Long.MIN_VALUE;
		
		// ensure all periodic calculaters perform operations at least once
		time_last_avail =Long.MIN_VALUE;
		availabilityChange =Long.MIN_VALUE +1;
		availabilityComputeChange =Long.MIN_VALUE;
		availabilityDrift =nbPieces;
		
		// initialize each piece; on going changes will use event driven tracking
		for (int i =0; i <nbPieces; i++)
		{
			if (dmPieces[i].isDone()){
				availability[i]++;
				nbPiecesDone++;
			}else{
				hasNeededUndonePiece |=dmPieces[i].calcNeeded();
			}
		}
		if (hasNeededUndonePiece)
			neededUndonePieceChange++;
		
		updateAvailability();
		
		// with availability charged and primed, ready for peer messages
		peerListeners =new HashMap();
		peerManagerListener =new PEPeerManagerListenerImpl();
		peerControl.addListener(peerManagerListener);
		
		
		// now do stuff related to starting/continuing pieces
//		startPriorities =new long[nbPieces];    //allocate on demand
		filePriorityChange =Long.MIN_VALUE;
		
		priorityParamChange =Long.MIN_VALUE;
		priorityFileChange =Long.MIN_VALUE;
		priorityAvailChange =Long.MIN_VALUE;
		
		timeLastPriorities =Long.MIN_VALUE;
		
		endGameMode =false;
		endGameModeAbandoned =false;
		timeEndGameModeEntered =0;
		
//		computeBasePriorities();
		
		// with priorities charged and primed, ready for dm messages
		diskManagerListener =new DiskManagerListenerImpl();
		diskManager.addListener(diskManagerListener);
	}
	

    public void addHavePiece(final int pieceNumber)
	{
		try
		{	availabilityMon.enter();
			if ( availabilityAsynch == null ){
				availabilityAsynch = (int[])availability.clone();
			}
			++availabilityAsynch[pieceNumber];
			availabilityChange++;
		} finally {availabilityMon.exit();}
	}
	
    /**
     * This methd will compute the pieces' overall availability (including ourself)
     * and the _globalMinOthers & _globalAvail
     */
    public void updateAvailability()
    {
        final long now =SystemTime.getCurrentTime();
        if (now >=time_last_avail &&now <time_last_avail +TIME_MIN_AVAILABILITY)
            return;
        if (availabilityDrift >0 || now < time_last_rebuild ||  (now - time_last_rebuild) > TIME_AVAIL_REBUILD ){
            try
            {	availabilityMon.enter();
                
                time_last_rebuild	= now;
                int[]	new_availability = recomputeAvailability();
                
                if (Constants.isCVSVersion())
                {
                    final int[]   old_availability =availabilityAsynch ==null ?availability :availabilityAsynch;
                    int	    errors	= 0;
                    
                    for (int i=0;i<new_availability.length;i++){
                        if ( new_availability[i] != old_availability[i]){
                            errors++;
                        }
                    }
                    if (errors >0 &&errors !=nbPieces)
                    {
                        if (Logger.isEnabled())
                            Logger.log(new LogEvent(peerControl, LOGID, LogEvent.LT_ERROR,
                                "updateAvailability(): availability rebuild errors = " +errors
                            ));
                    }
                }
                
                availabilityAsynch	= new_availability;
                
                availabilityDrift =0;
                availabilityChange++;
            } finally {availabilityMon.exit();}

        } else if (availabilityComputeChange >=availabilityChange){
            return;
        }

        try
        {	availabilityMon.enter();
            time_last_avail =now;
            availabilityComputeChange =availabilityChange;
    
            // take a snapshot of availabilityAsynch
            if ( availabilityAsynch != null ){
                availability 		= availabilityAsynch;
                availabilityAsynch	= null;
            }
        } finally {availabilityMon.exit();}

        int i;
        int allMin =Integer.MAX_VALUE;
        int rarestMin =Integer.MAX_VALUE;
        for (i =0; i <nbPieces; i++)
        {
            final int avail =availability[i];
            final DiskManagerPiece dmPiece =dmPieces[i];
            if (avail >0 &&avail <rarestMin &&dmPiece.isRequestable()) 
                rarestMin =avail;	// most important targets for near future requests from others

            if (avail <allMin)
                allMin =avail;
        }
        // copy updated local variables into globals
        globalMin =allMin;
        globalMinOthers =rarestMin;

        int total =0;
        int rarestActive =0;
        long totalAvail =0;
        for (i =0; i <nbPieces; i++ )
        {
            final int avail =availability[i];
            final DiskManagerPiece dmPiece =dmPieces[i];
            if (avail >0)
            {
                if (avail >allMin)
                    total++;
                if (avail <=rarestMin &&dmPiece.isRequestable() &&peerControl.isPieceActive(i))
                    rarestActive++;
                totalAvail +=avail;
            }
        }
        // copy updated local variables into globals
        globalAvail =(total /(float) nbPieces) +allMin;
        nbRarestActive =rarestActive;
        globalAvgAvail =totalAvail /(float)(nbPieces)
        /(1 +peerControl.getNbSeeds() +peerControl.getNbPeers());
    }
	
	private int[] recomputeAvailability()
	{
	    if (availabilityDrift >0 &&availabilityDrift !=nbPieces &&Logger.isEnabled())
	        Logger.log(new LogEvent(diskManager.getTorrent(), LOGID, LogEvent.LT_INFORMATION,
	            "Recomputing availabiliy. Drift=" +availabilityDrift +":" +peerControl.getDisplayName()));
	    final List	peerTransports =peerControl.getPeers();
	    
	    int[]	newAvailability = new int[nbPieces];
	    int j;
	    int i;
	    // first our pieces
	    for (j =0; j <nbPieces; j++)
	        newAvailability[j] =dmPieces[j].isDone() ?1 :0;
	    //for all peers
	    for (i =0; i <peerTransports.size(); i++)
	    {	//get the peer connection
	        final PEPeerTransport pt =(PEPeerTransport)peerTransports.get(i);
	        if (pt !=null &&pt.getPeerState() ==PEPeer.TRANSFERING)
	        {
	            //cycle trhough the pieces they actually have
	            final BitFlags peerHavePieces =pt.getAvailable();
	            if (peerHavePieces !=null &&peerHavePieces.nbSet >0)
	            {
	                for (j =peerHavePieces.start; j <=peerHavePieces.end; j++)
	                {
	                    if (peerHavePieces.flags[j])
	                        ++newAvailability[j];
	                }
	            }
	        }
	    }
	    return newAvailability;
	}
	
	
	public int[] getAvailability()
	{
		return availability;
	}

	public int getAvailability(final int pieceNumber)
	{
		return availability[pieceNumber];
	}
	
	//this only gets called when the My Torrents view is displayed
	public float getMinAvailability()
	{
		return globalAvail;
	}

	public float getAvgAvail()
	{
		return globalAvgAvail;
	}


	/**
	 * Early-outs when finds a downloadable piece
	 * Either way sets hasNeededUndonePiece and neededUndonePieceChange if necessary 
	 */
	protected void checkDownloadablePiece()
	{
		for (int i =0; i <nbPieces; i++)
		{
			if (dmPieces[i].isInteresting())
			{
				if (!hasNeededUndonePiece)
				{
					hasNeededUndonePiece =true;
					neededUndonePieceChange++;
				}
				return;
			}
		}
		if (hasNeededUndonePiece)
		{
			hasNeededUndonePiece =false;
			neededUndonePieceChange++;
		}
	}

	/**
	 * one reason requests don't stem from the individual peers is so the connections can be
	 * sorted by best uploaders, providing some ooprtunity to download the most important
	 * (ie; rarest and/or highest priority) pieces faster and more reliably
	 */
	public boolean checkDownloadPossible()
	{
		if (!hasNeededUndonePiece)
			return false;

		final List peer_transports =peerControl.getPeers();
        final int peerTransportsSize =peer_transports.size();
        final List bestUploaders =new ArrayList();

		final long[] upRates =new long[peerTransportsSize];
//		Arrays.fill(upRates, -1);

		for (int i =0; i <peerTransportsSize; i++)
		{
			final PEPeerTransport pt =(PEPeerTransport) peer_transports.get(i);
			if (pt.isDownloadPossible())
			{
				final long upRate =pt.getStats().getSmoothDataReceiveRate();
				UnchokerUtil.updateLargestValueFirstSort(upRate, upRates, pt, bestUploaders, 0);
			}
		}

		checkEndGameMode();

		computeBasePriorities();
		
		for (int i =0; i <bestUploaders.size(); i++)
		{
			// get a connection
			final PEPeerTransport pt =(PEPeerTransport) bestUploaders.get(i);
			// can we transfer something?
			if (pt.isDownloadPossible())
			{
				// If request queue is too low, enqueue another request
				int found =1;
				int maxRequests;
                
                if (!pt.isSnubbed())
                {
                    if (!endGameMode)
                    {
                        maxRequests =REQUESTS_MIN +(int) (pt.getStats().getDataReceiveRate() /SLOPE_REQUESTS);
                        if (maxRequests >REQUESTS_MAX ||maxRequests <0)
                            maxRequests =REQUESTS_MAX;
                    } else
                        maxRequests =2;
                } else
                    maxRequests =1;

				// Only loop when 3/5 of the queue is empty, in order to make more consecutive requests,
				// and improve cache efficiency
				if (pt.getNbRequests() <=(maxRequests *3) /5)
				{
					while (found >0 &&pt.isDownloadPossible() &&pt.getNbRequests() <maxRequests)
					{   // is there anything else to download?
                        if (!endGameMode)
                            found =findPieceToDownload(pt, maxRequests);
                        else
                            found =findPieceInEndGameMode(pt, maxRequests);
					}
				}
			}
		}
		return true;
	}
	
    /** This computes the base priority for all pieces that need requesting if there's
     * been any availability change or user priority setting changes since the last
     * call, which will be most of the time since availability changes so dynamicaly
     * It will change startPriorities[] (unless there was nothing to do)
     */
    private void computeBasePriorities()
    {
        final long now =SystemTime.getCurrentTime();
        if (startPriorities !=null &&((now >timeLastPriorities &&now <time_last_avail +TIME_MIN_PRIORITIES)
            ||(priorityParamChange >=paramPriorityChange &&priorityFileChange >=filePriorityChange
                &&priorityAvailChange >=availabilityChange)))
            return;     // *somehow* nothing changed, so nothing to do
        
            // store the latest change indicators before we start making dependent calculations so that a
            // further change while computing stuff doesn't get lost
        
        priorityParamChange =paramPriorityChange;
        priorityFileChange =filePriorityChange;
        priorityAvailChange =availabilityChange;
        timeLastPriorities =now;
        
        boolean         changedPriority =false;
        boolean         foundPieceToDownload =false;
        final int[]		newPriorities   =new int[nbPieces];
        try
        {
            final boolean rarestOverride =getRarestOverride();
            // calculate all base (starting) priorities for all pieces needing requesting
            for (int i =0; i <nbPieces; i++)
            {
                final int avail =availability[i];
                DiskManagerPiece dmPiece =dmPieces[i];
                if (dmPiece.isDone())
                    continue;   // nothing to do for pieces not needing requesting
                
                int startPriority =Integer.MIN_VALUE;
                int priority =Integer.MIN_VALUE;
                
                final DMPieceList pieceList =diskManager.getPieceList(dmPiece.getPieceNumber());
                for (int j =0; j <pieceList.size(); j++)
                {
                    final DiskManagerFileInfoImpl fileInfo =pieceList.get(j).getFile();
                    final long length =fileInfo.getLength();
                    final long downloaded =fileInfo.getDownloaded();
                    if (length >0 &&downloaded <length &&!fileInfo.isSkipped())
                    {
                        priority =0;
                        // user option "prioritize first and last piece"
                        // TODO: should prioritize ~10% to ~%25 from edges of file
                        if (firstPiecePriority &&fileInfo.getNbPieces() >FIRST_PIECE_MIN_NB)
                        {
                            if (i ==fileInfo.getFirstPieceNumber() ||i ==fileInfo.getLastPieceNumber())
                                priority +=PRIORITY_W_FIRSTLAST;
                        }
                        // if the file is high-priority
                        // startPriority +=(1000 *fileInfo.getPriority()) /255;
                        if (fileInfo.isPriority())
                        {
                            priority +=PRIORITY_W_FILE;
                            if (completionPriority)
                            {
                                final long percent =(1000 *downloaded) /length;
                                if (percent >=900)
                                    priority +=(PRIORITY_W_COMPLETION *downloaded) /diskManager.getTotalLength();
                            }
                        }
                        if (priority >startPriority)
                            startPriority =priority;
                    }
                }
                
                if (startPriority >=0)
                {
                    dmPiece.setNeeded();
                    foundPieceToDownload =true;
                    if (avail >0)
                    {   // boost priority for rarity
                        startPriority +=(peerControl.getNbPeers() +peerControl.getNbSeeds()) -avail;
//                        startPriority +=(PRIORITY_W_RARE +peerControl.getNbPeers()) /avail;
//                        // Boost priority even a little more if it's a globally rarest piece
//                        if (!rarestOverride &&avail <=globalMinOthers)
//                            startPriority +=PRIORITY_W_RAREST /avail;
                    }
                } else
                {
                    dmPiece.clearNeeded();
                }
                
                newPriorities[i] =startPriority;
                changedPriority =true;
            }
        } catch (Throwable e)
        {
            Debug.printStackTrace(e);
        }
                
        if (foundPieceToDownload)
        {
            if (!hasNeededUndonePiece)
            {
                hasNeededUndonePiece =true;
                neededUndonePieceChange++;
            }
        } else if (hasNeededUndonePiece)
        {
            hasNeededUndonePiece =false;
            neededUndonePieceChange++;
        }
        
        if (changedPriority)
            startPriorities =newPriorities;
    }
    
    private boolean getRarestOverride()
    {
        final int nbSeeds =peerControl.getNbSeeds();
        final int nbPeers =peerControl.getNbPeers();
        // Dont seek rarest under a few circumstances, so that other factors work better
        // never seek rarest when bootstrapping torrent
        boolean rarestOverride =nbPiecesDone <4 ||endGameMode ||(nbRarestActive >=(nbSeeds +nbPeers) &&globalMinOthers >1);
        if (!rarestOverride &&nbRarestActive >1 &&globalMinOthers >1)
        {
            // if already getting some rarest, dont get more if swarm is healthy or too many pieces running
            rarestOverride =globalMinOthers >globalMin
            	||(globalMinOthers >=(2 *nbSeeds) &&(2 *globalMinOthers) >=nbPeers);
            // Interest in Rarest pieces (compared to user priority settings) could be influenced by several factors;
            // less demand closer to 0% and 100% of torrent completion/farther from 50% of torrent completion
            // less demand closer to 0% and 100% of peers interestd in us/farther from 50% of peers interested in us
            // less demand the more pieces are in progress (compared to swarm size)
            // less demand the farther ahead from absolute global minimum we're at already
            // less demand the healthier a swarm is (rarity compared to # seeds and # peers)
        }
        return rarestOverride;
    }
    
	/**
	 * @param pc
	 *            the PeerConnection we're working on
	 * @return true if a request was assigned, false otherwise
	 */
	protected int findPieceToDownload(final PEPeerTransport pt, final int nbWanted)
	{
		final int pieceNumber =getRequestCandidate(pt);
		if (pieceNumber <0)
        {   // probaly should have found something since chose to try; probably not interested anymore
            // (or maybe Needed but not Done pieces are otherwise not requestable)
            pt.checkInterested();
			return 0;
        }

		int peerSpeed =(int) pt.getStats().getDataReceiveRate() /1000;

		PEPiece pePiece =peerControl.getPiece(pieceNumber);
		if (pePiece ==null)
        {   //create piece manually
			pePiece =new PEPieceImpl(pt.getManager(), dmPieces[pieceNumber], peerSpeed >>1);

			// Assign the created piece to the pieces array.
			peerControl.addPiece(pePiece, pieceNumber);
            if (startPriorities !=null)
                pePiece.setResumePriority(startPriorities[pieceNumber]);
			if (availability[pieceNumber] <=globalMinOthers)
				nbRarestActive++;
		}

		final int[] blocksFound =pePiece.getAndMarkBlocks(pt, nbWanted);
		final int blockNumber =blocksFound[0];
		final int nbBlocks =blocksFound[1];

		if (nbBlocks <=0)
			return 0;

		int requested =0;
		// really try to send the request to the peer
		for (int i =0; i <nbBlocks; i++)
		{
			final int thisBlock =blockNumber +i;
			if (pt.request(pieceNumber, thisBlock *DiskManager.BLOCK_SIZE, pePiece.getBlockSize(thisBlock)))
			{
				requested++;
				pt.setLastPiece(pieceNumber);
				// Up the speed on this piece?
				if (peerSpeed >pePiece.getSpeed())
					pePiece.incSpeed();
				// have requested a block
			}
		}
		return requested;
	}

    // set FORCE_PIECE if trying to diagnose piece problems and only want to d/l a specific piece from a torrent
    private static final int    FORCE_PIECE =-1;

    /**
     * This method is the downloading core. It decides, for a given peer,
     * which block should be requested. Here is the overall algorithm :
     * 0. If there a FORCED_PIECE or reserved piece, that will be started/resumed if possible
     * 1. Scan all the active pieces and find the rarest piece (and highest priority among equally rarest)
     *  that can possibly be continued by this peer, if any
     * 2. While scanning the active pieces, develop a list of equally highest priority pieces
     *  (and equally rarest among those) as candidates for starting a new piece
     * 3. If it can't find any piece, this means all pieces are
     *  already downloaded/full requested
     * 4. Returns int[] pieceNumber, blockNumber if a request to be made is found,
     *  or null if none could be found
     * @param pc PEPeerTransport to work with
     * 
     * @return int with pieceNumberto be requested or -1 if no request could be found
     */
    private int getRequestCandidate(final PEPeerTransport pt)
    {
        if (pt ==null ||pt.getPeerState() !=PEPeer.TRANSFERING)
            return -1;
        final BitFlags  peerHavePieces =pt.getAvailable();
        if (peerHavePieces ==null ||peerHavePieces.nbSet <=0)
            return -1;
        
        // piece number and its block number that we'll try to DL
        int pieceNumber;                // will be set to the piece # we want to resume

        if (FORCE_PIECE >=0 &&FORCE_PIECE <nbPieces)
            pieceNumber =FORCE_PIECE;
        else
            pieceNumber =pt.getReservedPieceNumber();

        // If there's a piece Reserved to this peer or a FORCE_PIECE, start/resume it and only it (if possible)
        if (pieceNumber >=0)
        {
            final DiskManagerPiece dmPiece =dmPieces[pieceNumber];
            if (peerHavePieces.flags[pieceNumber] &&dmPiece.isRequestable())
                return pieceNumber;
            return -1; // this is an odd case that maybe should be handled better, but checkers might fully handle it
        }

        final int       peerSpeed =(int) pt.getStats().getDataReceiveRate() /1000;  // how many KB/s has the peer has been sending
        final int       lastPiece =pt.getLastPiece();
        final boolean   rarestOverride =getRarestOverride();

        long        resumeMinAvail =Long.MAX_VALUE;
        int         resumeMaxPriority =Integer.MIN_VALUE;
        boolean     resumeIsRarest =false; // can the peer continuea piece with lowest avail of all pieces we want

        BitFlags    startCandidates =null;
        int         startMaxPriority =Integer.MIN_VALUE;
        int         startMinAvail =Integer.MAX_VALUE;
        boolean     startIsRarest =false;

        int         priority;   // aggregate priority of piece under inspection (start priority or resume priority for pieces to be resumed)
        int         avail =0;   // the swarm-wide availability level of the piece under inspection
        long        staleness;  // how long since there's been a write to the resume piece under inspection
        long        pieceAge;   // how long since the PEPiece first started downloading (requesting, actually)
        
        final int	startI =peerHavePieces.start;
        final int	endI =peerHavePieces.end;
        int         i;

        final long  now =SystemTime.getCurrentTime();
        // Try to continue a piece already loaded, according to priority
        for (i =startI; i <=endI; i++)
        {
            // is the piece available from this peer?
            if (peerHavePieces.flags[i])
            {
                priority =startPriorities[i];
                if (priority >=0)
                {
                    final DiskManagerPiece dmPiece =dmPieces[i];
                    // is the piece: Needed, not fully: Requested, Downloaded, Written, hash-Checking or Done?
                    if (dmPiece.isRequestable())
                    {
                        avail =availability[i];
                        if (avail ==0)
                        {   // maybe we didn't know we could get it before
                            availability[i] =1;    // but the peer says s/he has it
                            avail =1;
                        }
                        
                        // is the piece active
                        final PEPiece pePiece =peerControl.getPiece(i);
                        if (pePiece !=null)
                        {
                            // How many requests can still be made on this piece?
                            final int freeReqs =pePiece.getNbUnrequested();
                            if (freeReqs <=0)
                            {
                                dmPiece.setRequested();
                                continue;
                            }
                            
                            // Don't touch pieces reserved for others
                            final String peerReserved =pePiece.getReservedBy();
                            if (peerReserved !=null)
                            {
                                if (!peerReserved.equals(pt.getIp()))
                                    continue;   //reserved to somebody else
                                // the peer forgot this is reserved to him; re-associate it
                                pt.setReservedPieceNumber(i);
                                return i;
                            }
                            
                            final int pieceSpeed =pePiece.getSpeed();
                            // Snubbed peers or peers slower than the piece can only request on the piece if;
                            // they're the sole source OR
                            // it's the same as the last piece they were on AND there's enough free blocks
                            // TODO: instead of 3, should count how many peers are snubbed and use that
                            if (avail >1 &&(i !=lastPiece ||freeReqs <3 ||pieceSpeed -1 >=freeReqs *peerSpeed))
                            {
                                // if the peer is snubbed or slow, don't request this piece
                                if (pt.isSnubbed() ||peerSpeed <pieceSpeed)
                                    continue;
                            }
                            if (avail <=resumeMinAvail)
                            {
                                priority +=pieceSpeed;
                                
                                priority +=(i ==lastPiece) ?PRIORITY_W_SAME_PIECE :0;
                                // Adjust priority for purpose of continuing pieces
                                // how long since last written to (if written to)
                                priority +=pePiece.getTimeSinceLastActivity() /PRIORITY_DW_STALE;
                                // how long since piece was started
                                pieceAge =now -pePiece.getCreationTime();
                                if (pieceAge >0)
                                    priority +=PRIORITY_W_AGE *pieceAge /(PRIORITY_DW_AGE *dmPiece.getNbBlocks());
                                // how much is already written to disk
                                priority +=(PRIORITY_W_PIECE_DONE *dmPiece.getNbWritten()) /dmPiece.getNbBlocks();
                                
                                pePiece.setResumePriority(priority);  // this is only for display
                                
                                if (avail <resumeMinAvail &&(!rarestOverride ||priority >=resumeMaxPriority)
                                    ||(priority >resumeMaxPriority &&(!resumeIsRarest ||rarestOverride)))
                                {   // this piece seems like best choice for resuming
                                    // Verify it's still possible to get a block to request from this piece
                                    if (pePiece.hasUnrequestedBlock())
                                    {   // change the different variables to reflect interest in this block
                                        pieceNumber =i;
                                        resumeMinAvail =avail;
                                        resumeMaxPriority =priority;
                                        resumeIsRarest =avail <=globalMinOthers; // only going to try to resume one
                                    }
                                }
                            }
                        } else if (avail <=globalMinOthers &&!rarestOverride) 
                        {   // rarest pieces only from now on
                            if (!startIsRarest)
                            {   // 1st rarest piece
                                if (startCandidates ==null)
                                    startCandidates =new BitFlags(nbPieces);
                                startMaxPriority =priority;
                                startMinAvail =avail;
                                startIsRarest =avail <=globalMinOthers;
                                startCandidates.setOnly(i); // clear the non-rarest bits in favor of only rarest
                            } else if (priority >startMaxPriority)
                            {   // continuing rarest, higher priority level
                                if (startCandidates ==null)
                                    startCandidates =new BitFlags(nbPieces);
                                startMaxPriority =priority;
                                startCandidates.setOnly(i);
                            } else if (priority ==startMaxPriority)
                            {   // continuing rares, same priority level
                                startCandidates.setEnd(i);
                            }
                        } else if (!startIsRarest ||rarestOverride)
                        {   // not doing rarest pieces
                            if (priority >startMaxPriority)
                            {   // new priority level
                                if (startCandidates ==null)
                                    startCandidates =new BitFlags(nbPieces);
                                startMaxPriority =priority;
                                startMinAvail =avail;
                                startIsRarest =avail <=globalMinOthers;
                                startCandidates.setOnly(i);
                            } else if (priority ==startMaxPriority)
                            {   // continuing same priority level
                                if (avail <startMinAvail)
                                {   // same priority, new availability level
                                    startMinAvail =avail;
                                    startIsRarest =avail <=globalMinOthers;
                                    startCandidates.setOnly(i);
                                } else if (avail ==startMinAvail)
                                {   // same priority level, same availability level
                                    startCandidates.setEnd(i);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // can & should or must resume a piece?
        if (pieceNumber >=0 &&(resumeIsRarest ||!startIsRarest ||rarestOverride ||startCandidates ==null ||startCandidates.nbSet <=0))
            return pieceNumber;

// this would allow more non-rarest pieces to be resumed so they get completed so they can be re-shared,
// which can make us intersting to more peers, and generally improve the speed of the swarm,
// however, it can sometimes be hard to get the rarest pieces, such as when a holder unchokes very infrequently
// so for now we go back to always getting rarest (nearly complete pieces should complete soon enough, just less soon)
//        final boolean resumeIsBetter;
//        if (globalMinOthers >0)   // shouldn't get to here if 0, but better to be safe 
//            resumeIsBetter =(resumeMaxPriority /resumeMinAvail) >(startMaxPriority /globalMinOthers);
//        else
//            resumeIsBetter =false;
//        
//        if (Logger.isEnabled())
//            Logger.log(new LogEvent(new Object[] {pt, peerControl}, LOGID, 
//                "Start/resume choice; piece #:" +pieceNumber +" resumeIsBetter:" +resumeIsBetter
//                +" globalMinOthers=" +globalMinOthers
//                +" startMaxPriority=" +startMaxPriority +" startMinAvail=" +startMinAvail
//                +" resumeMaxPriority=" +resumeMaxPriority +" resumeMinAvail=" +resumeMinAvail
//                +" : " +pt));
//        
//        if (resumeIsBetter)
//            return pieceNumber;
        
        // start a new piece; select piece from start candidates bitfield
        return getPieceToStart(pt, startCandidates);
    }
    
    
    /** @param pt PEPeer the piece is to be chosen for
     * @param startCandidates BitFlags of potential candidates to choose from
     * @return int the piece number that was chosen to be started. Note it's possible for
     * the chosen piece to have been started already (by another thread).
     * This method considers that potential to not be relevant.
     */
	protected int getPieceToStart(final PEPeer pt, final BitFlags startCandidates)
	{
		if (startCandidates ==null ||startCandidates.nbSet <=0)
			return -1;
        if (startCandidates.nbSet ==1)
            return startCandidates.start;
		
		final int direction =RandomUtils.generateRandomPlusMinus1();
        final int startI;
        if (direction ==1)
            startI =startCandidates.start;
        else
            startI =startCandidates.end;
		
		// randomly select a bit flag to be the one
        final int targetNb =RandomUtils.generateRandomIntUpto(startCandidates.nbSet);
        
        // figure out the piece number of that selected bitflag
        int foundNb =-1;
		for (int i =startI; i <=startCandidates.end &&i >=startCandidates.start; i +=direction)
		{
			// is piece flagged
			if (startCandidates.flags[i])
            {
                foundNb++;
                if (foundNb >=targetNb)
                    return i;
            }
		}
		return -1;
	}

	public boolean hasDownloadablePiece()
	{
		return hasNeededUndonePiece;
	}

	public long getNeededUndonePieceChange()
	{
		return neededUndonePieceChange;
	}

    
    private void checkEndGameMode()
    {
        if (peerControl.getNbSeeds() +peerControl.getNbPeers() <3)
            return;
        final long now =SystemTime.getCurrentTime();
        // We can't come back from end-game mode
        if (endGameMode ||endGameModeAbandoned)
        {
            if (!endGameModeAbandoned)
            {
                if (now -timeEndGameModeEntered >END_GAME_MODE_TIMEOUT)
                {
                    endGameModeAbandoned =true;
                    clearEndGameChunks();
                    
                    if (Logger.isEnabled())
                        Logger.log(new LogEvent(diskManager.getTorrent(), LOGID, "Abandoning end-game mode: "
                            +peerControl.getDisplayName()));
                }
            }
            return;
        }

        int active_pieces =0;

        for (int i =0; i <nbPieces; i++)
        {
            final DiskManagerPiece dmPiece =dmPieces[i];
            // If the piece isn't even Needed, or doesn't need more downloading, simply continue
            if (dmPiece.isEGMIgnored())
                continue;
            // If the piece is being downloaded (fully requested), count it and continue
            if (dmPiece.isEGMActive())
            {
                active_pieces++;
                continue;
            }

            // Else, some piece is Needed, not downloaded/fully requested; this isn't end game mode
            return;
        }

        // only flick into end-game mode if < trigger size left
        if (active_pieces *diskManager.getPieceLength() <=END_GAME_MODE_SIZE_TRIGGER)
        {
            timeEndGameModeEntered =now;
            endGameMode =true;
            computeEndGameModeChunks();
            if (Logger.isEnabled())
                Logger.log(new LogEvent(diskManager.getTorrent(), LOGID, "Entering end-game mode: "
                    +peerControl.getDisplayName()));
            // System.out.println("End-Game Mode activated");
        }
    }
    
    private void computeEndGameModeChunks()
    {
        PEPiece[] _pieces =peerControl.getPieces();
        if (_pieces ==null)
            return;

        endGameModeChunks =new ArrayList();
        try
        {
            endGameModeChunks_mon.enter();

            for (int i =0; i <nbPieces; i++ )
            {
                DiskManagerPiece dmPiece =dmPieces[i];
                // Pieces not Needed or not needing more downloading are of no interest
                if (!dmPiece.isInteresting())
                    continue;
                
                PEPiece pePiece =_pieces[i];
                if (pePiece ==null)
                    continue;

                boolean written[] =dmPiece.getWritten();
                if (written ==null)
                {
                    if (!dmPiece.isDone())
                    {
                        for (int j =0; j <pePiece.getNbBlocks(); j++ )
                        {
                            endGameModeChunks.add(new EndGameModeChunk(pePiece, j));
                        }
                    }
                } else
                {
                    for (int j =0; j <written.length; j++ )
                    {
                        if (!written[j])
                            endGameModeChunks.add(new EndGameModeChunk(pePiece, j));
                    }
                }
            }
        } finally
        {
            endGameModeChunks_mon.exit();
        }
    }

    public boolean isInEndGameMode()
	{
		return endGameMode;
	}
	
    /** adds every block from the piece to the list of chuncks to be selected for egm requesting
     * 
     */ 
	public void addEndGameChunks(final PEPiece pePiece)
	{
		if (!endGameMode)
			return;
		try
		{
			endGameModeChunks_mon.enter();
			int nbChunks =pePiece.getNbBlocks();
			for (int i =0; i <nbChunks; i++ )
			{
				endGameModeChunks.add(new EndGameModeChunk(pePiece, i));
			}
		} finally
		{
			endGameModeChunks_mon.exit();
		}
	}

    /** adds blocks from the piece that are neither downloaded nor written to the list
     * of  chuncks to be selected for egm requesting
     */ 
	public void addEndGameBlocks(final PEPiece pePiece)
	{
		if (!endGameMode ||pePiece ==null)
			return;
		final DiskManagerPiece dmPiece =pePiece.getDMPiece();
		final int nbChunks =pePiece.getNbBlocks();
		try
		{
			endGameModeChunks_mon.enter();
			for (int i =0; i <nbChunks; i++ )
			{
				if (!pePiece.isDownloaded(i) &&!dmPiece.isWritten(i))
                    endGameModeChunks.add(new EndGameModeChunk(pePiece, i));
			}
		} finally
		{
			endGameModeChunks_mon.exit();
		}
	}

    protected int findPieceInEndGameMode(final PEPeerTransport pt, final int wants)
    {
        if (pt ==null ||wants <=0 ||pt.getPeerState() !=PEPeer.TRANSFERING)
            return 0;
        // Ok, we try one, if it doesn't work, we'll try another next time
        try
        {
            endGameModeChunks_mon.enter();

            final int nbChunks =endGameModeChunks.size();
            if (nbChunks >0)
            {
                final int random =RandomUtils.generateRandomIntUpto(nbChunks);
                final EndGameModeChunk chunk =(EndGameModeChunk) endGameModeChunks.get(random);
                final int pieceNumber =chunk.getPieceNumber();
                if (dmPieces[pieceNumber].isWritten(chunk.getBlockNumber()))
                {
                    endGameModeChunks.remove(chunk);
                    return 0;
                }
                if (pt.isPieceAvailable(pieceNumber)
                    &&peerControl.isPieceActive(pieceNumber)
                    &&pt.request(pieceNumber, chunk.getOffset(), chunk.getLength()))
                {
                    final PEPiece pePiece =peerControl.getPiece(pieceNumber);
                    pePiece.setRequested(pt, chunk.getBlockNumber());
                    pt.setLastPiece(pieceNumber);
                    return 1;
                }
            }
        } finally
        {
            endGameModeChunks_mon.exit();
        }
        return 0;
    }
    
	public void removeFromEndGameModeChunks(final int pieceNumber, final int offset)
	{
		try
		{
			endGameModeChunks_mon.enter();

			Iterator iter =endGameModeChunks.iterator();
			while (iter.hasNext())
			{
				EndGameModeChunk chunk =(EndGameModeChunk) iter.next();
				if (chunk.equals(pieceNumber, offset))
					iter.remove();
			}
		} finally
		{
			endGameModeChunks_mon.exit();
		}
	}
	
	public void clearEndGameChunks()
	{
		if (!endGameMode)
			return;
		try
		{
			endGameModeChunks_mon.enter();
			endGameModeChunks.clear();
			endGameMode =false;
		} finally
		{
			endGameModeChunks_mon.exit();
		}
	}
	
	
	/**
	 * An instance of this listener is registered with peerControl
	 * Through this, we learn of peers joining and leaving
	 * and attach/detach listeners to them
	 */
	private class PEPeerManagerListenerImpl
		implements PEPeerManagerListener
	{
		public void peerAdded(final PEPeerManager manager, PEPeer peer )
		{
			PEPeerListenerImpl peerListener;
			peerListener =(PEPeerListenerImpl)peerListeners.get(peer);
			if (peerListener ==null)
			{
				peerListener =new PEPeerListenerImpl();
				peerListeners.put(peer, peerListener);
			}
			peer.addListener(peerListener);
		}
		
		public void peerRemoved(final PEPeerManager manager, PEPeer peer)
		{
			// remove this listener from list of listeners and from the peer
			final PEPeerListenerImpl peerListener =(PEPeerListenerImpl)peerListeners.remove(peer);
			peer.removeListener(peerListener);
		}
	}
	
	/**
	 * An instance of this listener is registered with each peer
	 */
	private class PEPeerListenerImpl
		implements PEPeerListener
	{
		public void stateChanged(PEPeer peer, final int newState)
		{
            /*
			switch (newState)
			{
				case PEPeer.CONNECTING:
					return;
				
				case PEPeer.HANDSHAKING:
					return;
				
				case PEPeer.TRANSFERING:
					return;
				
				case PEPeer.CLOSING:
					return;
				
				case PEPeer.DISCONNECTED:
					return;
			}
            */
		}
		
		public void sentBadChunk(final PEPeer peer, final int piece_num, final int total_bad_chunks )
		{
			/* nothing to do here */
		}
		
		public void addAvailability(final PEPeer peer, final BitFlags peerHavePieces)
		{
			if (peerHavePieces ==null ||peerHavePieces.nbSet <=0)
				return;
			try
			{	availabilityMon.enter();
				if ( availabilityAsynch == null ){
					availabilityAsynch = (int[])availability.clone();
				}
				for (int i =peerHavePieces.start; i <=peerHavePieces.end; i++)
				{
					if ( peerHavePieces.flags[i] ){
					++availabilityAsynch[i];
				}
				}
				availabilityChange++;
			} finally {availabilityMon.exit();}
		}

        /**
         * Takes away the given pieces from global availability
         * @param PEPeer peer this is about
         * @param peerHasPieces BitFlags of the pieces
         */
		public void removeAvailability(final PEPeer peer, final BitFlags peerHavePieces)
		{
			if (peerHavePieces ==null ||peerHavePieces.nbSet <=0)
				return;
			try
			{	availabilityMon.enter();
				if (availabilityAsynch ==null)
                {
                    availabilityAsynch =(int[]) availability.clone();
                }
                for (int i =peerHavePieces.start; i <=peerHavePieces.end; i++)
                {
                    if (peerHavePieces.flags[i])
                    {
                        if (availabilityAsynch[i] >(dmPieces[i].isDone() ?1 :0))
                            --availabilityAsynch[i];
                        else
                            availabilityDrift++;
                    }
                }
                availabilityChange++;
			} finally {availabilityMon.exit();}
		}
	}
	
	/**
	 * An instance of this listener is registered with peerControl
	 * @author MjrTom
	 */
	private class DiskManagerListenerImpl
		implements DiskManagerListener
	{
		public void stateChanged(int oldState, int newState)
		{
			//starting torrent
		}

		public void filePriorityChanged(DiskManagerFileInfo file)
		{
			// record that user-based priorities changed
			filePriorityChange++;	// this is a user's priority change event
			
			// only need to re-calc Needed on file's pieces; priority is calculated seperatly
			boolean foundPieceToDownload =false;
			// if didn't have anything to do before, now only need to check if we need
			// to DL from this file, but if had something to do before,
			// must rescan all pieces to see if now nothing to do
			final int startI;
			final int endI;
			if (hasNeededUndonePiece)
			{
				startI =0;
				endI =nbPieces;
			} else
			{
				startI =file.getFirstPieceNumber();
				endI =file.getLastPieceNumber() +1;
			}
			for (int i =startI; i <endI; i++)
			{
				final DiskManagerPiece dmPiece =dmPieces[i];
				if (!dmPiece.isDone())
					foundPieceToDownload |=dmPiece.calcNeeded();
			}
			if (foundPieceToDownload)
			{
                if (!hasNeededUndonePiece)
                {
                    hasNeededUndonePiece =true;
                    neededUndonePieceChange++;
                }
			} else if (hasNeededUndonePiece)
            {
                hasNeededUndonePiece =false;
                neededUndonePieceChange++;
            }
		}
		
		
		public void pieceDoneChanged(DiskManagerPiece dmPiece)
		{
			int pieceNumber =dmPiece.getPieceNumber();
			if (dmPiece.isDone())
			{
				addHavePiece(pieceNumber);
				nbPiecesDone++;
                if (nbPiecesDone >=nbPieces)
                    checkDownloadablePiece();
			}else
			{
                try
                {   availabilityMon.enter();
                    if ( availabilityAsynch == null ){
                        availabilityAsynch = (int[])availability.clone();
                    }
                    if (availabilityAsynch[pieceNumber] >0)
                        --availabilityAsynch[pieceNumber];
                    else
                        availabilityDrift++;
                    availabilityChange++;
                } finally {availabilityMon.exit();}
				nbPiecesDone--;
				if (dmPiece.calcNeeded() &&!hasNeededUndonePiece)
				{
					hasNeededUndonePiece =true;
					neededUndonePieceChange++;
				}
			}
		}

		public void fileAccessModeChanged(DiskManagerFileInfo file, int old_mode, int new_mode)
		{
			//file done (write to read)
			//starting to upload from the file (read to write)
		}
	}
	
}
