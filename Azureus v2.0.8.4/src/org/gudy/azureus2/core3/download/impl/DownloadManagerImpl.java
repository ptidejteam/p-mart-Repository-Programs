/*
 * File    : DownloadManagerImpl.java
 * Created : 19-Oct-2003
 * By      : parg
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.core3.download.impl;
/*
 * Created on 30 juin 2003
 *
 */
 
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;


import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.internat.*;
import org.gudy.azureus2.core3.peer.*;
import org.gudy.azureus2.core3.tracker.client.*;
import org.gudy.azureus2.core3.torrent.*;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.download.*;
import org.gudy.azureus2.core3.category.Category;

import org.gudy.azureus2.core3.logging.*;

/**
 * @author Olivier
 * 
 */

public class 
DownloadManagerImpl 
	implements DownloadManager
{
		// DownloadManager listeners
	
	private static final int LDT_STATECHANGED		= 1;
	private static final int LDT_DOWNLOADCOMPLETE	= 2;
	private static final int LDT_COMPLETIONCHANGED = 3;
	
	private ListenerManager	listeners 	= ListenerManager.createManager(
			"DMM:ListenDispatcher",
			new ListenerManagerDispatcher()
			{
				public void
				dispatch(
					Object		_listener,
					int			type,
					Object		value )
				{
					DownloadManagerListener	listener = (DownloadManagerListener)_listener;
					
					if ( type == LDT_STATECHANGED ){
						
						listener.stateChanged(DownloadManagerImpl.this, ((Integer)value).intValue());
						
					}else if ( type == LDT_DOWNLOADCOMPLETE ){
						
						listener.downloadComplete(DownloadManagerImpl.this);

					}else if ( type == LDT_COMPLETIONCHANGED ){
						listener.completionChanged(DownloadManagerImpl.this, ((Boolean)value).booleanValue());
					}
				}
			});		
	
		// TrackerListeners
	
	private static final int LDT_TL_ANNOUNCERESULT		= 1;
	private static final int LDT_TL_SCRAPERESULT		= 2;
	
	private ListenerManager	tracker_listeners 	= ListenerManager.createManager(
			"DMM:TrackerListenDispatcher",
			new ListenerManagerDispatcher()
			{
				public void
				dispatch(
					Object		_listener,
					int			type,
					Object		value )
				{
					DownloadManagerTrackerListener	listener = (DownloadManagerTrackerListener)_listener;
					
					if ( type == LDT_TL_ANNOUNCERESULT ){
						
						listener.announceResult((TRTrackerResponse)value);
						
					}else if ( type == LDT_TL_SCRAPERESULT ){
						
						listener.scrapeResult((TRTrackerScraperResponse)value);
					}
				}
			});	

	// PeerListeners
	
	private static final int LDT_PE_PEER_ADDED		= 1;
	private static final int LDT_PE_PEER_REMOVED	= 2;
	private static final int LDT_PE_PIECE_ADDED		= 3;
	private static final int LDT_PE_PIECE_REMOVED	= 4;
	
	private ListenerManager	peer_listeners 	= ListenerManager.createAsyncManager(
			"DMM:PeerListenDispatcher",
			new ListenerManagerDispatcher()
			{
				public void
				dispatch(
					Object		_listener,
					int			type,
					Object		value )
				{
					DownloadManagerPeerListener	listener = (DownloadManagerPeerListener)_listener;
					
					if ( type == LDT_PE_PEER_ADDED ){
						
						listener.peerAdded((PEPeer)value);
						
					}else if ( type == LDT_PE_PEER_REMOVED ){
						
						listener.peerRemoved((PEPeer)value);
						
					}else if ( type == LDT_PE_PIECE_ADDED ){
						
						listener.pieceAdded((PEPiece)value);
						
					}else if ( type == LDT_PE_PIECE_REMOVED ){
						
						listener.pieceRemoved((PEPiece)value);
					}
				}
			});	
	
	private Vector	current_peers 	= new Vector();
	private Vector	current_pieces	= new Vector();
  
	private DownloadManagerStatsImpl	stats;
	
	private boolean		persistent;
	/**
	 * forceStarted torrents can't/shouldn't be automatically stopped
	 */
	private boolean 	forceStarted;
	/**
	 * Only seed this torrent. Never download or allocate<P>
	 * Current Implementation:
	 * - implies that the user completed the download at one point
	 * - Checks if there's Data Missing when torrent is done (or torrent load)
	 *
	 * Perhaps a better name would be "bCompleted"
	 */
	protected boolean onlySeeding;
	
	private int 		state = -1;
	private boolean 	download_ended;
  
	private int prevState = -1;

	private int priority;

	private String errorDetail;

	private GlobalManager globalManager;

	private String torrentFileName;
	private String name;

	private int nbPieces;
	private String savePath;
  
	// Position in Queue
	private int position = -1;
	
	// Category the user assigned torrent to.
	private Category category;
  
	//Used when trackerConnection is not yet created.
	// private String trackerUrl;
  
	private PEPeerServer server;
	private TOTorrent			torrent;
	private String torrent_comment;
	private String torrent_created_by;
  
	private TRTrackerClient 			tracker_client;
	private TRTrackerClientListener	tracker_client_listener;
	
	private DiskManager 			diskManager;
	private DiskManagerListener		disk_manager_listener;
  
	private PEPeerManager 			peerManager;
	private PEPeerManagerListener	peer_manager_listener;
   
	// Only call this with STATE_QUEUED, STATE_WAITING, or STATE_STOPPED unless you know what you are doing
	public 
	DownloadManagerImpl(
		GlobalManager 	_gm, 
		String 			_torrentFileName, 
		String 			_savePath,
		int   			_initialState,
		boolean			_persistent ) 
	{
		persistent	= _persistent;
  	
		stats = new DownloadManagerStatsImpl( this );
  	
		globalManager = _gm;
	
		stats.setMaxUploads( COConfigurationManager.getIntParameter("Max Uploads", 4));
	 
		forceStarted = false;
  
		priority = HIGH_PRIORITY;
	
		torrentFileName = _torrentFileName;
	
		savePath = _savePath;
	
		readTorrent();
	
	  // must be after readTorrent, so that any listeners have a TOTorrent
	  if (state == -1)
  		setState( _initialState );
    
	}

  public void initialize() 
  {
    // If we only want to seed, do a quick check first (before we create the diskManager, which allocates diskspace)
    if (onlySeeding) {
      String errMessage = filesExistErrorMessage();
      if (errMessage != "") {
        errorDetail = MessageText.getString("DownloadManager.error.datamissing") + " " + errMessage; //$NON-NLS-1$
        setState(STATE_ERROR);
        return;
        
        // If the user wants to re-download the missing files, they must
        // do a re-check, which will reset the onlySeeding flag.
      }
    }

	if ( torrent == null ){
		
	  setState( STATE_ERROR );
	  
	  return;
	}
	
	errorDetail = "";
	
	setState( STATE_INITIALIZING );
    
	startServer();
    
	if ( state == STATE_WAITING || state == STATE_ERROR ){
    	
		return;
	}
    
	try{
		if ( tracker_client != null ){
			
			tracker_client.destroy();
		}
		
		tracker_client = TRTrackerClientFactory.create( torrent, server.getPort());
    
    	tracker_client_listener = 
			new TRTrackerClientListener()
			{
				public void
				receivedTrackerResponse(
					 TRTrackerResponse	response	)
				{
					PEPeerManager	pm = peerManager;
					
					if ( pm != null ){
					
						pm.processTrackerResponse( response );
					}
					
					tracker_listeners.dispatch( LDT_TL_ANNOUNCERESULT, response );
				}
			
				 public void
				 urlChanged(
				   String		url,
				   boolean		explicit )
				 {  	
				   if ( explicit ){
			  		
					   checkTracker( true );
				   }
				 }
			  
				 public void
				 urlRefresh()
				 {
				   checkTracker( true );
				 }		
			};
			
		tracker_client.addListener( tracker_client_listener );

		initializeDiskManager();

		setState( STATE_INITIALIZED );
									
	}catch( TRTrackerClientException e ){
		
		e.printStackTrace();
		
		setState( STATE_ERROR );
	}
  }

  public void startDownload() {
	setState( STATE_DOWNLOADING );
	
	PEPeerManager temp = PEPeerManagerFactory.create(this, server, tracker_client, diskManager);

	peer_manager_listener = 	
		new PEPeerManagerListener()
		{
			public void
			stateChanged(
				int	new_state )
			{
			}
		};
		
	temp.addListener( peer_manager_listener );
		
	temp.start();
	
	peerManager = temp;		// delay this so peerManager var not available to other threads until it is started
	
	tracker_client.update( true );
  }

	private void 
	readTorrent()
	{
		name				= torrentFileName;	// default if things go wrong decoding it
		//trackerUrl			= "";
		torrent_comment		= "";
		torrent_created_by	= "";
		nbPieces			= 0;
		
		try {

			 torrent = TorrentUtils.readFromFile( torrentFileName );
			   	
			 LocaleUtilDecoder	locale_decoder = LocaleUtil.getTorrentEncoding( torrent );
			 	
			 name = locale_decoder.decodeString( torrent.getName());
                 
			 name = FileUtil.convertOSSpecificChars( name );
			 
         	 if (torrent.isSimpleTorrent()){
          	
            	File testFile = new File(savePath);
            
            	if (!testFile.isDirectory()){
            		 name = testFile.getName(); 
            	}
          	 }
          
			 //trackerUrl = torrent.getAnnounceURL().toString();
         
			 torrent_comment = locale_decoder.decodeString(torrent.getComment());
         
			if ( torrent_comment == null ){
			   torrent_comment	= "";
			}
			
			torrent_created_by = locale_decoder.decodeString(torrent.getCreatedBy());
         
			if ( torrent_created_by == null ){
				torrent_created_by	= "";
			}
			 
			 nbPieces = torrent.getPieces().length;
			 
			// Fixup the SavePath (again!)
			String path = FileUtil.smartFullName(savePath, name);
      File f = new File(path);
			if (!f.isDirectory())
			  path = f.getParent();

      if (DiskManagerFactory.isTorrentResumeDataComplete(torrent, path)) {
			  stats.setDownloadCompleted(1000);
			  setOnlySeeding(true);
			} else {
			  setOnlySeeding(false);
			}
			 
		}catch( TOTorrentException e ){
		
			nbPieces = 0;
        		
			setState( STATE_ERROR );
 			
			errorDetail = TorrentUtils.exceptionToText( e );
 			
		}catch( UnsupportedEncodingException e ){
		
			nbPieces = 0;
        		
			setState( STATE_ERROR );
			
			errorDetail = MessageText.getString("DownloadManager.error.unsupportedencoding"); //$NON-NLS-1$
		}
	}


  private void startServer() 
  {
  	server = PEPeerServerFactory.create();
  	
	if ( server == null || server.getPort() == 0 ) {
		
			// single port - this situation isn't going to clear easily
		
		errorDetail = MessageText.getString("DownloadManager.error.unabletostartserver"); //$NON-NLS-1$
		
		setState( STATE_ERROR );
	}
  }

  /**
   * @return
   */
  public int getState() {
	if (state != STATE_INITIALIZED)
	  return state;
	if (diskManager == null)
	  return STATE_INITIALIZED;
	int diskManagerState = diskManager.getState();
	if (diskManagerState == DiskManager.INITIALIZING)
	  return STATE_INITIALIZED;
	if (diskManagerState == DiskManager.ALLOCATING)
	  return STATE_ALLOCATING;
	if (diskManagerState == DiskManager.CHECKING)
	  return STATE_CHECKING;
	if (diskManagerState == DiskManager.READY)
	  return STATE_READY;
	if (diskManagerState == DiskManager.FAULTY)
	  return STATE_ERROR;
	return STATE_ERROR;
  }
  
	public boolean getOnlySeeding() {
		return onlySeeding;
	}
	
  public void setOnlySeeding(boolean onlySeeding) {
     //LGLogger.log(getName()+"] setOnlySeeding("+onlySeeding+") was " + this.onlySeeding);
    if (this.onlySeeding != onlySeeding) {
      this.onlySeeding = onlySeeding;

      if (onlySeeding) {
        String errMessage = filesExistErrorMessage();
        if (errMessage != "") {
          setState(STATE_ERROR);
          errorDetail = MessageText.getString("DownloadManager.error.datamissing") + " " + errMessage; //$NON-NLS-1$
        } else {
          // make sure stats always knows we are completed
  			  stats.setDownloadCompleted(1000);
        }
      }

  	  // we are in a new list, move to the top of the list so that we continue seeding
  	  // -1 position means it hasn't been added to the global list.  We shouldn't
  	  // touch it, since it'll get a position once it's adding is complete
      if (globalManager != null && position != -1) {
  		  DownloadManager[] dms = { DownloadManagerImpl.this };
  		  // pretend we are at the bottom of the new list
  		  // so that move top will shift everything down one
  		  position = globalManager.getDownloadManagers().size() + 1;
  		  globalManager.moveTop(dms);
  		  // we left a gap in incomplete list, fixup
        globalManager.fixUpDownloadManagerPositions();
      }
      listeners.dispatch( LDT_COMPLETIONCHANGED, new Boolean( onlySeeding ));
    }
  }
	
	public boolean filesExist() {
	  return (filesExistErrorMessage() == "");
	}

	private String filesExistErrorMessage() {
		String strErrMessage = "";
		// currently can only seed if whole torrent exists
		if (diskManager == null) {
  		DiskManager dm = DiskManagerFactory.createNoStart( torrent, FileUtil.smartFullName(savePath, name));
  		if (!dm.filesExist()) 
  		  strErrMessage = dm.getErrorMessage();
  		dm = null;
  	} else {
  		if (!diskManager.filesExist()) 
  		  strErrMessage = diskManager.getErrorMessage();
  	}

    return strErrMessage;
	}
	
	
  public boolean
  isPersistent()
  {
  	return( persistent );
  }
  
  /**
   * Returns the 'previous' state.
   */
  public int getPrevState() {
    return prevState;
  }
  
  /**
   * Sets the 'previous' state.
   */
  public void setPrevState(int state) {
    this.prevState = state;
  }
  

  public String getName() {
	if (diskManager == null)
	  return name;
	return diskManager.getFileName();
  }	

  public String getErrorDetails() {
	return errorDetail;
  }

  public long getSize() {
	if (diskManager != null)
	  return diskManager.getTotalLength();
  if(torrent != null)
    return torrent.getSize();
  return 0;
  }

  public boolean[] getPiecesStatus() {
	if (peerManager != null)
	  return peerManager.getPiecesStatus();
	if (diskManager != null)
	  return diskManager.getPiecesStatus();
	return new boolean[nbPieces];
  }

  public void stopIt() {
    stopIt(DownloadManager.STATE_STOPPED);
  }

  public void stopIt(final int stateAfterStopping)
  {
    if (state == DownloadManager.STATE_STOPPING)
      return;

  	setState( DownloadManager.STATE_STOPPING );

  	Thread stopThread = new Thread() {
	  public void run()
	  {
	  	try{
	  	
				// kill tracker client first so it doesn't report to peer manager
				// after its been deleted 
				
			if ( tracker_client != null ){
			
				tracker_client.removeListener( tracker_client_listener );
		
				tracker_client.destroy();
				
				tracker_client = null;
			}
			
			if (peerManager != null){
				
			  stats.setSavedDownloadedUploaded( 
					  stats.getSavedDownloaded() + peerManager.getStats().getTotalReceived(),
				 	  stats.getSavedUploaded() + peerManager.getStats().getTotalSent());
	      
			  stats.saveDiscarded(stats.getDiscarded());
			  stats.saveHashFails(stats.getHashFails());
				 	  
			  peerManager.removeListener( peer_manager_listener );
			  
			  peerManager.stopAll(); 
			  
			  peerManager = null; 
			  server	  = null;	// clear down ref
			}      
			
			if (diskManager != null){
				stats.setCompleted(stats.getCompleted());
				stats.setDownloadCompleted(stats.getDownloadCompleted(true));
	      
			  if (diskManager.getState() == DiskManager.READY){
			    diskManager.dumpResumeDataToDisk(true, false);
			  }
	      
			  //update path+name info before termination
			  savePath = diskManager.getPath();
			  name = diskManager.getFileName();
			  
			  diskManager.stopIt();
			  	
			  diskManager.removeListener( disk_manager_listener );
			  
			  diskManager = null;
			}
	  	}finally{
				
	  		setState( stateAfterStopping );                
	  		forceStarted = false;
	  	}
	  }
	};
	stopThread.setDaemon(true);
	stopThread.start();
  }

  public void setState(int _state){
    // note: there is a DIFFERENCE between the state held on the DownloadManager and
    // that reported via getState as getState incorporated DiskManager states when
    // the DownloadManager is INITIALIZED
    if ( state != _state ) {
      state = _state;
      // sometimes, downloadEnded() doesn't get called, so we must check here too
      if (state == STATE_SEEDING)
        setOnlySeeding(true);
      informStateChanged( state );
    }
  }

  public int getNbSeeds() {
	if (peerManager != null)
	  return peerManager.getNbSeeds();
	return 0;
  }

  public int getNbPeers() {
	if (peerManager != null)
	  return peerManager.getNbPeers();
	return 0;
  }

  

  public String getTrackerStatus() {
	if (tracker_client != null)
	  return tracker_client.getStatusString();
	return "";
  }

  	// this is called asynchronously when a response is received
  
  public void
  setTrackerScrapeResponse(
  	TRTrackerScraperResponse	response )
  {
  	tracker_listeners.dispatch( LDT_TL_SCRAPERESULT, response );
  }
  
  public TRTrackerClient 
  getTrackerClient() 
  {
	return( tracker_client );
  }
 
 
  /**
   * @return
   */
  public int getNbPieces() {
	return nbPieces;
  }


  public int getTrackerTime() {
	if (tracker_client != null){
	
	  return tracker_client.getTimeUntilNextUpdate();
	}
	
	return TRTrackerClient.REFRESH_MINIMUM_SECS;
  }

  /**
   * @return
   */
  public TOTorrent
  getTorrent() 
  {
	return( torrent );
  }

  /**
   * @return
   */
  public String getSavePath() {
	if (diskManager != null)
	  return diskManager.getPath();
	return savePath;
  }

  public String getPieceLength() {
	if (diskManager != null)
	  return DisplayFormatters.formatByteCountToKiBEtc(diskManager.getPieceLength());
	return ""; //$NON-NLS-1$
  }

  /**
   * Returns the full path including file/dir name
   */
  public String getFullName() {
	//if diskmanager is already running, use its values
   if (diskManager != null) {
    String path = savePath = diskManager.getPath();
    String fname = name = diskManager.getFileName();
    return FileUtil.smartFullName(path, fname); 
	}
   //otherwise use downloadmanager's values
   else return FileUtil.smartFullName(savePath, name);
  }


  /**
   * @return
   */
  public int getPriority() {
	return priority;
  }

  /**
   * @param i
   */
  public void setPriority(int i) {
	priority = i;
  }

  /**
   * @return
   */
  public String getTorrentFileName() {
	return torrentFileName;
  }

  /**
   * @param string
   */
  public void setTorrentFileName(String string) {
	torrentFileName = string;
  }

  public TRTrackerScraperResponse getTrackerScrapeResponse() {
    TRTrackerScraperResponse r = null;
    if (globalManager != null) {
      if (tracker_client != null)
        r = globalManager.getTrackerScraper().scrape(tracker_client);
      if (r == null && torrent != null)
        r = globalManager.getTrackerScraper().scrape(torrent);
    }
    return r;
  }

  /**
   * @param string
   */
  public void setErrorDetail(String string) {
	errorDetail = string;
  }

  
  /**
   * Stops the current download, then restarts it again.
   */
  public void restartDownload(boolean use_fast_resume) {
    if (!use_fast_resume) {
      //invalidate resume info
      diskManager.dumpResumeDataToDisk(false, true);
      readTorrent();
    }
    
    stopIt();
    
    try {
      while (state != DownloadManager.STATE_STOPPED) Thread.sleep(50);
    } catch (Exception ignore) {/*ignore*/}
    
    initialize();
  }
    
  
  public void startDownloadInitialized(boolean initStoppedDownloads) {
	if (getState() == DownloadManager.STATE_WAITING || initStoppedDownloads && getState() == DownloadManager.STATE_STOPPED) {
	  initialize();
	}
	if (getState() == DownloadManager.STATE_READY) {
	  startDownload();
	}
  }

  /** @retun true, if the other DownloadManager has the same size and hash 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals(Object obj)
  {
		// check for object equivalence first!
  		
	if ( this == obj ){
  		
		return( true );
	}
  	
	if(null != obj && obj instanceof DownloadManager) {
    	
	  DownloadManager other = (DownloadManager) obj;
          
	  TOTorrent t1 = getTorrent();
	  TOTorrent t2 = other.getTorrent();
      
	  if ( t1 == null || t2 == null ){
      	
		return( false );	// broken torrents - treat as different so shown
							// as broken
	  }
      
	  try{
      	
		return( Arrays.equals(t1.getHash(), t2.getHash()));
     
	  }catch( TOTorrentException e ){
      	
			// only get here is serious problem with hashing process
      		
		e.printStackTrace();
	  }
	}
    
	return false;
  }
  
  public void 
  checkTracker() 
  {
  	checkTracker(false);
  }
  
  protected void
  checkTracker(
  	boolean	force )
  {
	if( tracker_client != null)
	tracker_client.update( force );
  }

  /**
   * @return
   */
  public String 
  getTorrentComment() {
	return torrent_comment;
  }
  
  public String 
  getTorrentCreatedBy() {
  	return torrent_created_by;
  }
  
  public long 
  getTorrentCreationDate() {
  	if (torrent==null){
  		return(0);
  	}
  	
  	return( torrent.getCreationDate());
  }
  
  /**
   * @return
   */
  public int getIndex() {
	if(globalManager != null)
	  return globalManager.getIndexOf(this);
	return -1;
  }
  
  public boolean isMoveableUp() {
	if(globalManager != null)
	  return globalManager.isMoveableUp(this);
	return false;
  }
  
  public boolean isMoveableDown() {
	if(globalManager != null)
	  return globalManager.isMoveableDown(this);
	return false;
  }
  
  public void moveUp() {
	if(globalManager != null)
	  globalManager.moveUp(this);
  }
  
  public void moveDown() {
	if(globalManager != null)
	  globalManager.moveDown(this);
  }      
  

	public GlobalManager
	getGlobalManager()
	{
		return( globalManager );
	}
	
  public DiskManager
  getDiskManager()
  {
  	return( diskManager );
  }
  
  public PEPeerManager
  getPeerManager()
  {
  	return( peerManager );
  }

	public void
	addListener(
		DownloadManagerListener	listener )
	{
		listeners.addListener(listener);
						
		if ( download_ended ){
				
			listener.downloadComplete(this);
		}
	}
	
	public void
	removeListener(
		DownloadManagerListener	listener )
	{
		listeners.removeListener(listener);			
	}
	
	protected void
	informStateChanged(
		int		new_state )
	{
		listeners.dispatch( LDT_STATECHANGED, new Integer( new_state ));
	}
	
	protected void
	informDownloadEnded()
	{
		listeners.dispatch( LDT_DOWNLOADCOMPLETE, null );
	}

  public void
  addPeerListener(
	  DownloadManagerPeerListener	listener )
  {
  	synchronized( peer_listeners ){
  		
  		peer_listeners.addListener( listener );
  		
		for (int i=0;i<current_peers.size();i++){
  			
			peer_listeners.dispatch( listener, LDT_PE_PEER_ADDED, current_peers.elementAt(i));
		}
		
		for (int i=0;i<current_pieces.size();i++){
  			
			peer_listeners.dispatch( listener, LDT_PE_PIECE_ADDED, current_pieces.elementAt(i));
		}
  	}
  }
		
  public void
  removePeerListener(
	  DownloadManagerPeerListener	listener )
  {
  	peer_listeners.removeListener( listener );
  }
 

  public void
  addPeer(
	  PEPeer 		peer )
  {
  	synchronized( peer_listeners ){
  		
  		current_peers.addElement( peer );
  		
  		peer_listeners.dispatch( LDT_PE_PEER_ADDED, peer );
	}
  }
		
  public void
  removePeer(
	  PEPeer		peer )
  {
    synchronized( peer_listeners ){
    	
    	current_peers.removeElement( peer );
    	
    	peer_listeners.dispatch( LDT_PE_PEER_REMOVED, peer );
    }
 }
		
  public void
  addPiece(
	  PEPiece 	piece )
  {
  	synchronized( peer_listeners ){
  		
  		current_pieces.addElement( piece );
  		
  		peer_listeners.dispatch( LDT_PE_PIECE_ADDED, piece );
  	}
 }
		
  public void
  removePiece(
	  PEPiece		piece )
  {
  	synchronized( peer_listeners ){
  		
  		current_pieces.removeElement( piece );
  		
  		peer_listeners.dispatch( LDT_PE_PIECE_REMOVED, piece );
  	}
 }

	public DownloadManagerStats
	getStats()
	{
		return( stats );
	}

  public boolean isForceStart() {
    return forceStarted;
  }

  public void setForceStart(boolean forceStart) {
    if (forceStarted != forceStart) {
      forceStarted = forceStart;
      if (forceStarted && 
          (getState() == STATE_STOPPED || getState() == STATE_QUEUED)) {
        // Start it!
        setState(STATE_WAITING);
      }
    }
  }

  /**
   * Is called when a download is finished.
   * Activates alerts for the user.
   *
   * @author Rene Leonhardt
   */
  public void 
  downloadEnded()
  {
    download_ended = true;

    if (getPriority() == HIGH_PRIORITY &&
        COConfigurationManager.getBooleanParameter("Switch Priority", false))
      setPriority(LOW_PRIORITY);

    if (isForceStart()) {
      setForceStart(false);
    }

    setOnlySeeding(true);
	
    informDownloadEnded();
  }

  public void initializeDiskManager() 
  {
  	if(diskManager == null) {
  		diskManager = DiskManagerFactory.create( torrent, FileUtil.smartFullName(savePath, name));
      
  		disk_manager_listener = 
  			new DiskManagerListener()
  			{
  				public void
  				stateChanged(int oldDMState,
  					int		newDMState )
  				{
  					if ( newDMState == DiskManager.FAULTY ){
  						
  						setErrorDetail( diskManager.getErrorMessage());
  						stopIt(STATE_ERROR);
  					}
  					
  					if (oldDMState == DiskManager.CHECKING) {
      				stats.setDownloadCompleted(stats.getDownloadCompleted(true));
  				    DownloadManagerImpl.this.setOnlySeeding(diskManager.getRemaining() == 0);
            }
  					  
  					int	dl_state = getState();
  					
  					if ( dl_state != state ){
  						
  						informStateChanged( dl_state );
  					}
  				}
  			};
  		
  		diskManager.addListener( disk_manager_listener );
  	}
  }
  
  public boolean canForceRecheck() {
    return (state == STATE_STOPPED) ||
           (state == STATE_QUEUED) ||
           (state == STATE_ERROR && diskManager == null);
  }

  public void forceRecheck() {
  	if ( diskManager != null ) {
  		LGLogger.log(0, 0, LGLogger.ERROR, "Trying to force recheck while diskmanager active");
  		return;
  	}
  	
    Thread recheck = new Thread() {
			public void run() {
				int prevState = getState();
				setState(STATE_CHECKING);
      	// remove resume data
		  	torrent.removeAdditionalProperty("resume");
		  	// For extra protection from a plugin stopping a checking torrent,
		  	// fake a forced start
		  	boolean wasForceStarted = forceStarted;
		  	forceStarted = true;
		  	initializeDiskManager();
				while (diskManager != null &&
				       diskManager.getState() != DiskManager.FAULTY &&
				       diskManager.getState() != DiskManager.READY) {
					try {
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				forceStarted = wasForceStarted;
				stats.setDownloadCompleted(stats.getDownloadCompleted(true));
				if (diskManager == null) {
				  LGLogger.log(LGLogger.ERROR, "diskManager destroyed while trying to recheck!");
				  setState(STATE_STOPPED);
				  return;
				}
			  if (diskManager.getState() == DiskManager.READY) {
			    diskManager.dumpResumeDataToDisk(true, false);
					diskManager.stopIt();
				  setOnlySeeding(diskManager.getRemaining() == 0);
					diskManager = null;
					if (prevState == STATE_ERROR)
						setState(STATE_STOPPED);
					else
						setState(prevState);
			  }
			  else { // Faulty
			  	setErrorDetail( diskManager.getErrorMessage());
					diskManager.stopIt();
					setOnlySeeding(false);
					diskManager = null;
					setState(STATE_ERROR);
			  }
			}
		};
		recheck.setPriority(Thread.MIN_PRIORITY);
		recheck.start();
  }
  
  
  public int getHealthStatus() {
    if(peerManager != null && (state == STATE_DOWNLOADING || state == STATE_SEEDING)) {
      int nbSeeds = getNbSeeds();
      int nbPeers = getNbPeers();
      int nbRemotes = peerManager.getNbRemoteConnections();
      int trackerStatus = tracker_client.getLastResponse().getStatus();
      boolean isSeed = (state == STATE_SEEDING);
      
      if( (nbSeeds + nbPeers) == 0) {
        if(isSeed)
          return WEALTH_NO_TRACKER;        
        return WEALTH_KO;        
      }
      if( trackerStatus == TRTrackerResponse.ST_OFFLINE)
        return WEALTH_NO_TRACKER;
      if( nbRemotes == 0 )
        return WEALTH_NO_REMOTE;
      return WEALTH_OK;
    } else {
      return WEALTH_STOPPED;
    }
  }
  
  public int getPosition() {
  	return position;
  }

  public void setPosition(int newPosition) {
    if (newPosition != position) {
//  	  LGLogger.log(getName() + "] setPosition from "+position+" to "+newPosition);
//    	Debug.outStackTrace();
    	position = newPosition;
    }
  }

	public Category getCategory() {
	  return category;
	}
	
	public void setCategory(Category cat) {
	  if (cat == category)
	    return;
	  if (cat != null && cat.getType() != Category.TYPE_USER)
	    cat = null;

	  Category oldCategory = category;
	  category = cat;
	  if (oldCategory != null) {
	    if (oldCategory.getType() == Category.TYPE_USER)
    	  oldCategory.removeManager(this);
  	}
  	if (category != null) {
	    if (category.getType() == Category.TYPE_USER)
    	  category.addManager(this);
   	}
	}

  public void
  addTrackerListener(
  	DownloadManagerTrackerListener	listener )
  {  		
  	tracker_listeners.addListener( listener );
  }
  
  public void
  removeTrackerListener(
  	DownloadManagerTrackerListener	listener )
  {
  		tracker_listeners.removeListener( listener );
  }
  
  public void deleteDataFiles() {
    DiskManagerFactory.deleteDataFiles(torrent, FileUtil.smartPath(savePath, name));
  }
}
