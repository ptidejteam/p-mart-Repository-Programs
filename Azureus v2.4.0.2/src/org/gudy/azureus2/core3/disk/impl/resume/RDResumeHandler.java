/*
 * Created on 31-Jul-2004
 * Created by Paul Gardner
 * Copyright (C) 2004, 2005, 2006 Aelitis, All Rights Reserved.
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

package org.gudy.azureus2.core3.disk.impl.resume;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.util.*;

import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.download.*;
import org.gudy.azureus2.core3.disk.impl.*;
import org.gudy.azureus2.core3.disk.impl.access.*;
import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMapEntry;
import org.gudy.azureus2.core3.disk.*;

import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;

/**
 * @author parg
 *
 */
public class 
RDResumeHandler
{
	private static final LogIDs LOGID = LogIDs.DISK;

	private static final byte		PIECE_NOT_DONE			= 0;
	private static final byte		PIECE_DONE				= 1;
	private static final byte		PIECE_RECHECK_REQUIRED	= 2;
	private static final byte		PIECE_STARTED			= 3;
		
	private static boolean	use_fast_resume;
	private static boolean	use_fast_resume_recheck_all;
	
	static{
	    	
		COConfigurationManager.addAndFireParameterListeners(
			new String[]{ 
					"Use Resume", 
					"On Resume Recheck All" },
			new ParameterListener() {
	    	    public void 
				parameterChanged( 
					String  str ) 
	    	    {
	    	    	use_fast_resume				= COConfigurationManager.getBooleanParameter("Use Resume");
	    	    	use_fast_resume_recheck_all	= COConfigurationManager.getBooleanParameter("On Resume Recheck All");
	    	    }
	    	 });
	}
	
	private DiskManagerImpl		disk_manager;
	private DMChecker			checker;
		
	private boolean				started;
	private boolean				stopped;
	private boolean				bStoppedMidCheck;
	

	public 
	RDResumeHandler(
		DiskManagerImpl		_disk_manager,
		DMChecker			_writer_and_checker )
	{
		disk_manager		= _disk_manager;
		checker				= _writer_and_checker;
	}
	
	public void
	start()
	{
		if ( started ){
			
			Debug.out( "RDResumeHandler: reuse not supported" );	
		}
		
		started	= true;
	}
	
	public void
	stop()
	{	
		stopped	= true;
	}
	
	public void 
	checkAllPieces(
		boolean newfiles ) 
	{
		//long	start = System.currentTimeMillis();
				
		DiskManagerRecheckInstance	recheck_inst = disk_manager.getRecheckScheduler().register( disk_manager, false );

		try{			
			disk_manager.setState( DiskManager.CHECKING );
					
			
			boolean resumeEnabled = use_fast_resume;
			
				//disable fast resume if a new file was created
			
			if ( newfiles ){
				
				resumeEnabled = false;
			}
			
			boolean	resume_data_complete = false;
			
			
			final AESemaphore	pending_checks_sem 	= new AESemaphore( "RD:PendingChecks" );
			int					pending_check_num	= 0;

			DiskManagerPiece[]	pieces	= disk_manager.getPieces();
			
			if ( resumeEnabled ){
				
				boolean resumeValid = false;
				
				byte[] resume_pieces = null;
				
				Map partialPieces = null;
				
				Map	resume_data = getResumeData();							
				
				if ( resume_data != null ){
					
					try {
						
						resume_pieces = (byte[])resume_data.get("resume data");
						
						if ( resume_pieces != null ){
							
							if ( resume_pieces.length != pieces.length ){
							
								Debug.out( "Resume data array length mismatch: " + resume_pieces.length + "/" + pieces.length );
								
								resume_pieces	= null;
							}
						}
						
						partialPieces = (Map)resume_data.get("blocks");
						
						resumeValid = ((Long)resume_data.get("valid")).intValue() == 1;
						
							// if the torrent download is complete we don't need to invalidate the
							// resume data
						
						if ( isTorrentResumeDataComplete( disk_manager.getDownloadManager(), resume_data )){
							
							resume_data_complete	= true;
									
						}else{
							
								// set it so that if we crash the NOT_DONE pieces will be
								// rechecked
							
							resume_data.put("valid", new Long(0));
							
							saveResumeData( resume_data );
						}
						
					}catch(Exception ignore){
						
						// ignore.printStackTrace();
					}	
				}
								
				if ( resume_pieces == null ){
					
					resumeValid	= false;
					
					resume_pieces	= new byte[pieces.length];
					
					Arrays.fill( resume_pieces, PIECE_RECHECK_REQUIRED );
				}
				
					// calculate the current file sizes up front for performance reasons
				
				DiskManagerFileInfo[]	files = disk_manager.getFiles();
				
				Map	file_sizes = new HashMap();
				
				for (int i=0;i<files.length;i++){
					
					try{
						Long	len = new Long(((DiskManagerFileInfoImpl)files[i]).getCacheFile().getLength());
					
						file_sizes.put( files[i], len );
						
					}catch( CacheFileManagerException e ){
						
						Debug.printStackTrace(e);
					}
				}
	
				boolean	recheck_all	= use_fast_resume_recheck_all;
				
				if ( !recheck_all ){
					
						// override if not much left undone
					
					long	total_not_done = 0;
					
					int	piece_size = disk_manager.getPieceLength();
					
					for (int i = 0; i < pieces.length; i++){
						
						if ( resume_pieces[i] != PIECE_DONE ){
							
							total_not_done	+= piece_size;
						}
					}
					
					if ( total_not_done < 64*1024*1024 ){
						
						recheck_all	= true;
					}
				}
				
				for (int i = 0; i < pieces.length; i++){
					
					DiskManagerPiece	dm_piece	= pieces[i];
					
					disk_manager.setPercentDone(((i + 1) * 1000) / disk_manager.getNbPieces() );
					
					byte	piece_state = resume_pieces[i];
					
						// valid resume data means that the resume array correctly represents
						// the state of pieces on disk, be they done or not
					
					if ( piece_state == PIECE_DONE ){
					
							// at least check that file sizes are OK for this piece to be valid
						
						DMPieceList list = disk_manager.getPieceList(i);
						
						for (int j=0;j<list.size();j++){
							
							DMPieceMapEntry	entry = list.get(j);
							
							Long	file_size 		= (Long)file_sizes.get(entry.getFile());
							
							if ( file_size == null ){
								
								piece_state	= PIECE_NOT_DONE;
								
								if (Logger.isEnabled())
									Logger.log(new LogEvent(disk_manager, LOGID,
											LogEvent.LT_WARNING, "Piece #" + i
													+ ": file is missing, " + "fails re-check."));

								break;
							}
							
							long	expected_size 	= entry.getOffset() + entry.getLength();
							
							if ( file_size.longValue() < expected_size ){
								
								piece_state	= PIECE_NOT_DONE;
								
								if (Logger.isEnabled())
									Logger.log(new LogEvent(disk_manager, LOGID,
											LogEvent.LT_WARNING, "Piece #" + i
													+ ": file is too small, fails re-check. File size = "
													+ file_size + ", piece needs " + expected_size));

								break;
							}
						}
					}
					
					if ( piece_state == PIECE_DONE ){
						
						dm_piece.setDone( true );
						
					}else if ( piece_state == PIECE_NOT_DONE && !recheck_all ){
						
							// if the piece isn't done and we haven't been asked to recheck all pieces
							// on restart (only started pieces) then just set as not done 
																	
					}else{
						
							// We only need to recheck pieces that are marked as not-ok
							// if the resume data is invalid or explicit recheck needed
						
						if ( piece_state == PIECE_RECHECK_REQUIRED || !resumeValid ){
												
							while( !stopped ){
									
								if ( recheck_inst.getPermission()){
									
									break;
								}
							}
							
							if ( stopped ){
								
									// we only flag as stopped mid-check if the stop action has prevented
									// a hash check from occurring
								
								bStoppedMidCheck	= true;
								
								break;
								
							}else{
								
								try{	
									DiskManagerCheckRequest	request = disk_manager.createCheckRequest( i, null );
									
									request.setLowPriority( true );
									
									checker.enqueueCheckRequest(
										request,
										new DiskManagerCheckRequestListener()
										{
											public void 
											checkCompleted( 
												DiskManagerCheckRequest 	request,
												boolean						passed )
											{
												complete();
											}
											 
											public void
											checkCancelled(
												DiskManagerCheckRequest		request )
											{
												complete();
											}
											
											public void 
											checkFailed( 
												DiskManagerCheckRequest 	request, 
												Throwable		 			cause )
											{
												complete();
											}
											
											protected void
											complete()
											{
												pending_checks_sem.release();
											}
										});
									
									pending_check_num++;
									
								}catch( Throwable e ){
								
									Debug.printStackTrace(e);
								}
							}
						}
					}
				}
				
				while( pending_check_num > 0 ){
					
					pending_checks_sem.reserve();
					
					pending_check_num--;
				}
				
				if ( partialPieces != null ){
														
					Iterator iter = partialPieces.entrySet().iterator();
					
					while (iter.hasNext()) {
						
						Map.Entry key = (Map.Entry)iter.next();
						
						int pieceNumber = Integer.parseInt((String)key.getKey());
							
						DiskManagerPiece	dm_piece = pieces[ pieceNumber ];
						
						if ( !dm_piece.isDone()){
							
							List blocks = (List)partialPieces.get(key.getKey());
							
							Iterator iterBlock = blocks.iterator();
							
							while (iterBlock.hasNext()) {
								
								dm_piece.setWritten(((Long)iterBlock.next()).intValue());
							}
						}
					}
				}
			}else{
				
					// resume not enabled, recheck everything
				
				for (int i = 0; i < pieces.length; i++){
					
					while( ! stopped ){
						
						if ( recheck_inst.getPermission()){
							
							break;
						}
					}
					
					if ( stopped ){
						
						bStoppedMidCheck = true;
						
						break;
					}
										
					disk_manager.setPercentDone(((i + 1) * 1000) / disk_manager.getNbPieces() );						
						
					try{
						DiskManagerCheckRequest	request = disk_manager.createCheckRequest( i, null );
						
						request.setLowPriority( true );

						checker.enqueueCheckRequest(
								request, 
								new DiskManagerCheckRequestListener()
								{
									public void 
									checkCompleted( 
										DiskManagerCheckRequest 	request,
										boolean						passed )
									{
										complete();
									}
									 
									public void
									checkCancelled(
										DiskManagerCheckRequest		request )
									{
										complete();
									}
									
									public void 
									checkFailed( 
										DiskManagerCheckRequest 	request, 
										Throwable		 			cause )
									{
										complete();
									}
									
									protected void
									complete()
									{
										pending_checks_sem.release();
									}
								});
						
						pending_check_num++;
						
					}catch( Throwable e ){
					
						Debug.printStackTrace(e);
					}
				}
				
				while( pending_check_num > 0 ){
					
					pending_checks_sem.reserve();
					
					pending_check_num--;
				}
			}
			
				//dump the newly built resume data to the disk/torrent
			
			if ( !( stopped || resume_data_complete )){
				
				try{
					saveResumeData( true );
					
				}catch( Exception e ){
					
					Debug.out( "Failed to dump initial resume data to disk" );
					
					Debug.printStackTrace( e );
				}
			}
		}catch( Throwable e ){
			
				// if something went wrong then log and continue. 
			
			Debug.printStackTrace(e);
			
		}finally{
			
			recheck_inst.unregister();
       		
			// System.out.println( "Check of '" + disk_manager.getDownloadManager().getDisplayName() + "' completed in " + (System.currentTimeMillis() - start));
		}
	}
	
	public void 
	saveResumeData(
		boolean interim_save ) 	// data is marked as "invalid" if this is true to enable checking on pieces on crash restart
	
		throws Exception
	{		
			// if file caching is enabled then this is an important time to ensure that the cache is
			// flushed as we are going to record details about the accuracy of written data.
			// First build the resume map from the data (as updates can still be goin on)
			// Then, flush the cache. This means that on a successful flush the built resume
			// data matches at least the valid state of the data
			// Then update the torrent
		
		DiskManagerFileInfo[]	files = disk_manager.getFiles();
		
		if ( !use_fast_resume ){
			
				// flush cache even if resume disable
			
			for (int i=0;i<files.length;i++){
				
				files[i].flushCache();
			}
			
			return;
		}

		boolean	was_complete = isTorrentResumeDataComplete( disk_manager.getDownloadManager());
		
		DiskManagerPiece[] pieces	= disk_manager.getPieces();

			//build the piece byte[]
		
		byte[] resume_pieces = new byte[pieces.length];
		
		for (int i = 0; i < resume_pieces.length; i++) {
	  	
			DiskManagerPiece piece = pieces[i];

		  	if ( piece.isDone()){
		  		
				resume_pieces[i] = PIECE_DONE;
		  		
		  	}else if ( piece.getNbWritten() > 0 ){
		  		
		  		resume_pieces[i] = PIECE_STARTED;
		  		
		  	}else{
		  	
				resume_pieces[i] = PIECE_NOT_DONE;
		  	}
		}
		
		Map	resume_data = new HashMap();
	  	  
		resume_data.put( "resume data", resume_pieces );
		
		Map partialPieces = new HashMap();
		  		  		      
		for (int i = 0; i < pieces.length; i++) {
			
			DiskManagerPiece piece = pieces[i];
			
				// save the partial pieces for any pieces that have not yet been completed
				// and are in-progress (i.e. have at least one block downloaded)
			
			boolean[] downloaded = piece.getWritten();

			if (( !piece.isDone()) && piece.getNbWritten() > 0 && downloaded != null ){
				
				
				List blocks = new ArrayList();
				
				for (int j = 0; j < downloaded.length; j++) {
					
					if (downloaded[j]){
						
						blocks.add(new Long(j));
					}
				}
      
				partialPieces.put("" + i, blocks);
			}
		}
		
		resume_data.put("blocks", partialPieces);
		
		long lValid = 0;
		
		if ( !( interim_save || bStoppedMidCheck )){
			
			lValid = 1;
		}
		
		resume_data.put("valid", new Long(lValid));
		
		for (int i=0;i<files.length;i++){
			
			files[i].flushCache();
		}
		
	  		// OK, we've got valid resume data and flushed the cache
	  
		boolean	is_complete = isTorrentResumeDataComplete( disk_manager.getDownloadManager(), resume_data );
	
		if ( was_complete && is_complete ){
	 
	  		// no change, no point in writing
	  		  	
		}else{
	  	
			saveResumeData( resume_data );
		}
	}
	
	protected Map
	getResumeData()
	{
		return( getResumeData( disk_manager.getDownloadManager()));
	}
	
	protected static Map
	getResumeData(
		DownloadManager		download_manager)
	{
		DownloadManagerState download_manager_state = download_manager.getDownloadState();
		
		Map resume_map = download_manager_state.getResumeData();
		
		if ( resume_map != null ){
						
			Map	resume_data = (Map)resume_map.get( "data" );
			
			return( resume_data );
			
		}else{
			
			return( null );
		}
	}

	protected void
	saveResumeData(
		Map		resume_data )
	{
		saveResumeData( disk_manager.getDownloadManager().getDownloadState(), resume_data );
	}
	
	protected static void
	saveResumeData(
		DownloadManagerState		download_manager_state,
		Map							resume_data )
	{		
		Map	resume_map = new HashMap();
		
		resume_map.put( "data", resume_data );
		
		download_manager_state.setResumeData( resume_map );
		
		download_manager_state.save();
	}
	
	
	public static void
	setTorrentResumeDataComplete(
		DownloadManagerState	download_manager_state )
	{
		TOTorrent	torrent = download_manager_state.getTorrent();
		
		int	piece_count = torrent.getNumberOfPieces();
		
		byte[] resume_pieces = new byte[piece_count];
		
		Arrays.fill( resume_pieces, PIECE_DONE );

		Map resume_data = new HashMap();
			
		resume_data.put( "resume data", resume_pieces );
		
		Map partialPieces = new HashMap();
		
		resume_data.put("blocks", partialPieces );
		
		resume_data.put("valid", new Long(1));	
	
		saveResumeData( download_manager_state, resume_data );
	}
	
	protected static int
	clearResumeDataSupport(
		DownloadManager			download_manager,
		DiskManagerFileInfo		file,
		boolean					recheck,
		boolean					ignore_first_and_last )
	{
		DownloadManagerState	download_manager_state = download_manager.getDownloadState();
		
		Map resume_data = getResumeData( download_manager );

		if ( resume_data == null ){
			
			return(0);
		}

		int	pieces_cleared	= 0;
		
			// TODO: we could be a bit smarter with the first and last pieces regarding
			// partial blocks where the piece spans the file bounaries.
		
			// clear any affected pieces
		
		byte[]	resume_pieces = (byte[])resume_data.get("resume data");
		
		int	first_piece = file.getFirstPieceNumber();
		int last_piece	= file.getLastPieceNumber();
		
		if ( ignore_first_and_last ){
			
			first_piece++;
			
			last_piece--;
		}
		
		if ( resume_pieces != null ){
			
			for (int i=first_piece;i<=last_piece;i++){
				
				if ( i >= resume_pieces.length ){
					
					break;
				}
						
				if ( resume_pieces[i] == PIECE_DONE ){
					
					pieces_cleared++;
				}
				
				resume_pieces[i] = recheck?PIECE_RECHECK_REQUIRED:PIECE_NOT_DONE;
			}
		}
			// clear any affected partial pieces
		
		Map	partial_pieces = (Map)resume_data.get("blocks");
		
		if ( partial_pieces != null ){
			
			Iterator iter = partial_pieces.keySet().iterator();
		
			while (iter.hasNext()) {
			
				int piece_number = Integer.parseInt((String)iter.next());
	
				if ( piece_number >= first_piece && piece_number <= last_piece ){
					
					iter.remove();
				}
			}
		}
					
			// either way we're valid as 
			//    1) clear -> pieces are set as not done
			//	  2) recheck -> pieces are set as "recheck" and will be checked on restart
		
		resume_data.put( "valid", new Long(1));	
		
		saveResumeData( download_manager_state, resume_data );
		
		return( pieces_cleared );
	}
	
	public static int
	storageTypeChanged(
		DownloadManager			download_manager,
		DiskManagerFileInfo		file )
	{
		return( clearResumeDataSupport(  download_manager, file, false, true ));
	}
	
	public static void
	clearResumeData(
		DownloadManager			download_manager,
		DiskManagerFileInfo		file )
	{
		clearResumeDataSupport( download_manager, file, false, false );
	}
	
	public static void
	recheckFile(
		DownloadManager			download_manager,
		DiskManagerFileInfo		file )
	{
		clearResumeDataSupport( download_manager, file, true, false );
	}
	
	public static void
	setTorrentResumeDataNearlyComplete(
		DownloadManagerState	download_manager_state )
	{
			// backwards compatability, resume data key is the dir
		
		TOTorrent	torrent = download_manager_state.getTorrent();
		
		long	piece_count = torrent.getNumberOfPieces();
		
		byte[] resume_pieces = new byte[(int)piece_count];
		
		Arrays.fill( resume_pieces, PIECE_DONE );

			// randomly clear some pieces
		
		for (int i=0;i<3;i++){
			
			int	piece_num = (int)(Math.random()*piece_count);
						
			resume_pieces[piece_num]= PIECE_RECHECK_REQUIRED;
		}
		
		Map resumeMap = new HashMap();
								
		resumeMap.put( "resume data", resume_pieces);
		
		Map partialPieces = new HashMap();
		
		resumeMap.put("blocks", partialPieces);
		
		resumeMap.put("valid", new Long(0));	// recheck the not-done pieces
	
		saveResumeData(download_manager_state,resumeMap);
	}
	
	public static boolean
	isTorrentResumeDataComplete(
		DownloadManager			download_manager )
	{				
			// backwards compatability, resume data key is the dir
		
		Map	resume_data = getResumeData( download_manager );
		
		return( isTorrentResumeDataComplete( download_manager, resume_data ));
	}
	
	public static boolean
	isTorrentResumeDataComplete(
		DownloadManager		download_manager, 
		Map					resume_data )
	{
		try{
			int	piece_count = download_manager.getDownloadState().getTorrent().getNumberOfPieces();
							
			if ( resume_data != null ){
				
				byte[] 	pieces 	= (byte[])resume_data.get("resume data");
				Map		blocks	= (Map)resume_data.get("blocks");
				boolean	valid	= ((Long)resume_data.get("valid")).intValue() == 1;
				
					// any partial pieced -> not complete
				
				if ( blocks == null || blocks.size() > 0 ){
					
					return( false );
				}
				
				if ( valid && pieces != null && pieces.length == piece_count ){
					
					for (int i=0;i<pieces.length;i++){

						if ( pieces[i] != PIECE_DONE ){
							
								// missing piece or recheck outstanding
							
							return( false );
						}
					}
					
					return( true );
				}
			}
		}catch( Throwable e ){
		
			Debug.printStackTrace( e );
		}	
		
		return( false );
	}
}
