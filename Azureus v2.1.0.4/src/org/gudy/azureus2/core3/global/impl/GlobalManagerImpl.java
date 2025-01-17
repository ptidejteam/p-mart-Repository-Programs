/*
 * File    : GlobalManagerImpl.java
 * Created : 21-Oct-2003
 * By      : stuff
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

package org.gudy.azureus2.core3.global.impl;

/*
 * Created on 30 juin 2003
 *
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;

import org.gudy.azureus2.core3.global.*;
import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.disk.DiskManager;
import org.gudy.azureus2.core3.download.*;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.tracker.client.*;
import org.gudy.azureus2.core3.tracker.host.*;
import org.gudy.azureus2.core3.torrent.*;
import org.gudy.azureus2.core3.startup.STProgressListener;
import org.gudy.azureus2.core3.stats.*;
import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.category.CategoryManager;
import org.gudy.azureus2.core3.category.Category;

/**
 * @author Olivier
 * 
 */
public class GlobalManagerImpl 
	implements 	GlobalManager, DownloadManagerListener
{
		// GlobalManagerListener support
		// Must be an async listener to support the non-synchronized invocation of
		// listeners when a new listener is added and existing downloads need to be
		// reported
	
	private static final int LDT_MANAGER_ADDED			= 1;
	private static final int LDT_MANAGER_REMOVED		= 2;
	private static final int LDT_DESTROY_INITIATED		= 3;
	private static final int LDT_DESTROYED				= 4;
	
	private ListenerManager	listeners 	= ListenerManager.createAsyncManager(
		"GM:ListenDispatcher",
		new ListenerManagerDispatcher()
		{
			public void
			dispatch(
				Object		_listener,
				int			type,
				Object		value )
			{
				GlobalManagerListener	target = (GlobalManagerListener)_listener;
		
				if ( type == LDT_MANAGER_ADDED ){
					
					target.downloadManagerAdded((DownloadManager)value);
					
				}else if ( type == LDT_MANAGER_REMOVED ){
					
					target.downloadManagerRemoved((DownloadManager)value);
					
				}else if ( type == LDT_DESTROY_INITIATED ){
					
					target.destroyInitiated();
					
				}else if ( type == LDT_DESTROYED ){
					
					target.destroyed();
					
				}
			}
		});
	
		// GlobalManagerDownloadWillBeRemovedListener support
		// Not async (doesn't need to be and can't be anyway coz it has an exception)
	
	private static final int LDT_MANAGER_WBR			= 1;
	
	private ListenerManager	removal_listeners 	= ListenerManager.createManager(
			"GM:DLWBRMListenDispatcher",
			new ListenerManagerDispatcherWithException()
			{
				public void
				dispatchWithException(
					Object		_listener,
					int			type,
					Object		value )
				
					throws GlobalManagerDownloadRemovalVetoException
				{					
					GlobalManagerDownloadWillBeRemovedListener	target = (GlobalManagerDownloadWillBeRemovedListener)_listener;
					
					target.downloadWillBeRemoved((DownloadManager)value);
				}
			});
	
	private List 	managers			= new ArrayList();
	private Map		manager_map			= new HashMap();
	
	private TRHost	tracker_host;
	
	private Checker checker;
	private GlobalManagerStatsImpl	stats;
  private TRTrackerScraper 			trackerScraper;
  private StatsWriterPeriodic		stats_writer;
  /* Whether the GlobalManager is active (false) or stopped (true) */
  private boolean 					isStopped = false;
  private boolean					destroyed;
  private boolean needsSaving = false;
  
  public class Checker extends Thread {
    boolean finished = false;
    int loopFactor;
    private static final int waitTime = 1000;
    // 5 minutes save resume data interval (default)
    private int saveResumeLoopCount = 300000 / waitTime;
    

    public Checker() {
      super("Global Status Checker");
      loopFactor = 0;
      setPriority(Thread.MIN_PRIORITY);
      //determineSaveResumeDataInterval();
    }

    private void determineSaveResumeDataInterval() {
      int saveResumeInterval = COConfigurationManager.getIntParameter("Save Resume Interval", 5);
      if (saveResumeInterval >= 1 && saveResumeInterval <= 90)
        saveResumeLoopCount = saveResumeInterval * 60000 / waitTime;
    }

    public void run() {
      while (!finished) {

      	try{
	        loopFactor++;
	        determineSaveResumeDataInterval();

	        synchronized (managers) {
	          if ((loopFactor % saveResumeLoopCount == 0) || needsSaving) {
	            saveDownloads();
	            needsSaving = false;
	          }

	          for (int i = 0; i < managers.size(); i++) {
	            DownloadManager manager = (DownloadManager) managers.get(i);
	            
             	if (loopFactor % saveResumeLoopCount == 0) {
            		manager.saveResumeData();
	            }
	            /*
	             * seeding rules have been moved to StartStopRulesDefaultPlugin
	             */
	            
            
	            // Handle forced starts here
	            if (manager.getState() == DownloadManager.STATE_READY &&
	                manager.isForceStart()) {
	              manager.startDownload();
	              
	              if (manager.getState() == DownloadManager.STATE_DOWNLOADING) {
	                //set previous hash fails and discarded values
	                manager.getStats().setSavedDiscarded();
	                manager.getStats().setSavedHashFails();
	              }
	            }
	          }
	        }
      	}catch( Throwable e ){
      		
      		e.printStackTrace();
      	}
      	
        try {
          Thread.sleep(waitTime);
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    public void stopIt() {
      finished = true;
    }
  }

  public GlobalManagerImpl(boolean initialiseStarted) {
    this(initialiseStarted, null);
  }

  public GlobalManagerImpl(boolean initialiseStarted, STProgressListener listener)
  {
    //Debug.dumpThreadsLoop("Active threads");
  	
  	LGLogger.initialise();
  	
    stats = new GlobalManagerStatsImpl();
    
    StatsFactory.initialize(this);
           
    if (listener != null)
      listener.reportCurrentTask(MessageText.getString("splash.initializeGM") + ": " +
                            MessageText.getString("splash.loadingTorrents"));
    loadDownloads(listener);
    if (listener != null)
      listener.reportCurrentTask(MessageText.getString("splash.initializeGM"));

    // Initialize scraper after loadDownloads so that we can merge scrapes
    // into one request per tracker
    trackerScraper = TRTrackerScraperFactory.getSingleton();
    
    trackerScraper.addListener(
    	new TRTrackerScraperListener() {
    		public void scrapeReceived(TRTrackerScraperResponse response) {
    			byte[]	hash = response.getHash();
    			
    			if ( response.isValid() ){
    				DownloadManager manager = (DownloadManager)manager_map.get(new HashWrapper(hash));
    				if ( manager != null ) {
    					manager.setTrackerScrapeResponse( response );
    				}
    			}
    		}
    	});
    
    tracker_host = TRHostFactory.getSingleton();
    
    tracker_host.initialise( 
    	new TRHostTorrentFinder()
    	{
    		public TOTorrent
    		lookupTorrent(
    			byte[]		hash )
    		{
    			for (int i=0;i<managers.size();i++){
    				
    				DownloadManager	dm = (DownloadManager)managers.get(i);
    				
    				TOTorrent t = dm.getTorrent();
    				
    				if ( t != null ){
    					
    					try{
    						if ( Arrays.equals( hash, t.getHash())){
    							
    							return( t );
    						}
    					}catch( TOTorrentException e ){
    						
    						e.printStackTrace();
    					}
    				}
    			}
    			
    			return( null );
    		}
    	});
    	
    checker = new Checker();    
    if(initialiseStarted) checker.start();
    
    stats_writer = StatsWriterFactory.createPeriodicDumper( this );
    
    stats_writer.start();
  }

  public DownloadManager addDownloadManager(String fileName, String savePath) {
  	return addDownloadManager(fileName, savePath, DownloadManager.STATE_WAITING, true);
  }
   
  public DownloadManager 
  addDownloadManager(String fileName, String savePath, int initialState ) {
  	return( addDownloadManager(fileName, savePath, initialState, true ));
  }
  	
  /**
   * @return true, if the download was added
   *
   * @author Rene Leonhardt
   */
  public DownloadManager 
  addDownloadManager(String fileName, String savePath, int initialState, boolean persistent ) {
	File torrentDir	= null;
	File fDest		= null;
	
    try {
      File f = new File(fileName);
      
      boolean saveTorrents = persistent&&COConfigurationManager.getBooleanParameter("Save Torrent Files", true);
      if (saveTorrents) torrentDir = new File(COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory"));
      else torrentDir = new File(f.getParent());

      //if the torrent is already in the completed files dir, use this
      //torrent instead of creating a new one in the default dir
      boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done", false);
      String completedDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
      if (moveWhenDone && completedDir.length() > 0) {
        File cFile = new File( completedDir, f.getName() );
        if ( cFile.exists() ) {
          //set the torrentDir to the completedDir
          torrentDir = new File(completedDir);
        }
      }
        
      torrentDir.mkdirs();
      
      fDest = new File(torrentDir, f.getName().replaceAll("%20","."));
      if (fDest.equals(f)) {
        throw new Exception("Same files");
      }
      
      while (fDest.exists()) {
        fDest = new File(torrentDir, "_" + fDest.getName());
      }
      
      fDest.createNewFile();
      
      if ( ! FileUtil.copyFile(f, fDest)) {
        throw new IOException("File copy failed");
      }
      
      String fName = fDest.getCanonicalPath();
      
      DownloadManager new_manager = DownloadManagerFactory.create(this, fName, savePath, initialState, persistent, false );
      
      DownloadManager manager = addDownloadManager(new_manager, true);
      
      	// if a different manager is returned then an existing manager for this torrent
      	// exists and the new one isn't needed (yuck)
      
      if ( manager == null || manager != new_manager ) {
        fDest.delete();
        File backupFile = new File(fName + ".bak");
        if(backupFile.exists())
          backupFile.delete();
      }
      return( manager );
    }
    catch (IOException e) {
      System.out.println( "DownloadManager::addDownloadManager: fails - td = " + torrentDir + ", fd = " + fDest );
      e.printStackTrace();
      DownloadManager manager = DownloadManagerFactory.create(this, fileName, savePath, initialState, persistent, false );
      return addDownloadManager(manager, true);
    }
    catch (Exception e) {
    	// get here on duplicate files, no need to treat as error
      DownloadManager manager = DownloadManagerFactory.create(this, fileName, savePath, initialState, persistent, false );
      return addDownloadManager(manager, true);
    }
  }



   protected DownloadManager addDownloadManager(DownloadManager manager, boolean save) {
    if (!isStopped) {
      synchronized (managers) {
      	
      	int	existing_index = managers.indexOf( manager );
      	
        if (existing_index != -1) {
        	
        	DownloadManager existing = (DownloadManager)managers.get(existing_index);
                	
        	existing.mergeTorrentDetails( manager );
        	
        	return( existing );
        }
        
        boolean isCompleted = manager.getStats().getDownloadCompleted(false) == 1000;
	      if (manager.getPosition() == -1) {
	        int endPosition = 0;
	        for (int i = 0; i < managers.size(); i++) {
	          DownloadManager dm = (DownloadManager) managers.get(i);
	          boolean dmIsCompleted = dm.getStats().getDownloadCompleted(false) == 1000;
	          if (dmIsCompleted == isCompleted)
	            endPosition++;
	        }
	        manager.setPosition(endPosition + 1);
	      }
	      
	      // Even though when the DownloadManager was created, onlySeeding was
	      // most likely set to true for completed torrents (via the Initializer +
	      // readTorrent), there's a chance that the torrent file didn't have the
	      // resume data.  If it didn't, but we marked it as complete in our
	      // downloads config file, we should set to onlySeeding
	      manager.setOnlySeeding(isCompleted);

        managers.add(manager);
        
        TOTorrent	torrent = manager.getTorrent();
        
        if ( torrent != null ){
        	
        	try{
        		manager_map.put( new HashWrapper(torrent.getHash()), manager );
        		
        	}catch( TOTorrentException e ){
        		
        		e.printStackTrace();
        	}
        }

        listeners.dispatch( LDT_MANAGER_ADDED, manager );
        manager.addListener(this);
      }
 
      if (save)
        saveDownloads();
      
      return( manager );
    }
    else {
      LGLogger.log(
        0,
        LGLogger.ERROR,
        LGLogger.ERROR,
        "Tried to add a DownloadManager after shutdown of GlobalManager.");
      return( null );
    }
  }

  public List getDownloadManagers() {
    return managers;
  }
  
  public DownloadManager getDownloadManager(TOTorrent torrent) {
    try {
      return (DownloadManager)manager_map.get(new HashWrapper(torrent.getHash()));
    } catch (TOTorrentException e) {
      return null;
    }
  }

  public void 
  canDownloadManagerBeRemoved(
  	DownloadManager manager) 
  
  	throws GlobalManagerDownloadRemovalVetoException
  {
  	try{
  		removal_listeners.dispatchWithException( LDT_MANAGER_WBR, manager );
  		
  	}catch( Throwable e ){
  		
  		throw((GlobalManagerDownloadRemovalVetoException)e);
  	}
  }
  
  public void 
  removeDownloadManager(
  	DownloadManager manager) 
  
  	throws GlobalManagerDownloadRemovalVetoException
  {
  	canDownloadManagerBeRemoved( manager );
  	
    synchronized (managers){
    	
      managers.remove(manager);
      
      TOTorrent	torrent = manager.getTorrent();
      
      if ( torrent != null ){
      	
      	try{
      		manager_map.remove(new HashWrapper(torrent.getHash()));
      		
      	}catch( TOTorrentException e ){
      		
      		e.printStackTrace();
      	}
      }
    }
    
    fixUpDownloadManagerPositions();
    listeners.dispatch( LDT_MANAGER_REMOVED, manager );
    manager.removeListener(this);
    
    saveDownloads();

    if (manager.getTrackerClient() != null) {

      trackerScraper.remove(manager.getTrackerClient());

    }
    else if (manager.getTorrent() != null) {

      trackerScraper.remove(manager.getTorrent());
    }

    if (manager.getCategory() != null)
      manager.setCategory(null);
  }

  /* Puts GlobalManager in a stopped state.
   * Used when closing down Azureus.
   */
  public void stopAll() {
    if (!isStopped){
    	
    	informDestroyInitiated();
    	
    		// kick off a non-daemon task. This will ensure that we hang around
    		// for at least LINGER_PERIOD to run other non-daemon tasks such as writing
    		// torrent resume data...
    	
    	try{
	    	NonDaemonTaskRunner.run(
	    			new NonDaemonTask()
	    			{
	    				public Object
	    				run()
	    				{	
	    					tracker_host.close();
	    					
	    					return( null );
	    				}
	    			});
    	}catch( Throwable e ){
    		e.printStackTrace();
    	}
    	
      checker.stopIt();
      
      saveDownloads();
      
      stopAllDownloads();

      managers.clear();
      manager_map.clear();
      
      isStopped = true;
      
      stats_writer.stop();
      
      informDestroyed();
    }
  }

  /**
   * Stops all downloads without removing them
   *
   * @author Rene Leonhardt
   */
  public void stopAllDownloads() {
    stopAllDownloads(DownloadManager.STATE_STOPPED);
  }

  public void stopAllDownloads(int stateAfterStopping) {
 
    for (Iterator iter = managers.iterator(); iter.hasNext();) {
      DownloadManager manager = (DownloadManager) iter.next();
      manager.stopIt(stateAfterStopping);
    }
  }
  
  
  /**
   * Starts all downloads
   */
  public void startAllDownloads() {    
    for (Iterator iter = managers.iterator(); iter.hasNext();) {
        DownloadManager manager = (DownloadManager) iter.next();
        manager.startDownloadInitialized(true);
    }
  }
  

  private void loadDownloads(STProgressListener listener) 
  {
  	try{
      int minQueueingShareRatio = COConfigurationManager.getIntParameter("StartStopManager_iFirstPriority_ShareRatio");
      Map map = FileUtil.readResilientConfigFile("downloads.config");
      
      boolean debug = Boolean.getBoolean("debug");
      int numDownloading = 0;
      int numCompleted = 0;

      Iterator iter = null;
      //v2.0.3.0+ vs older mode
      List downloads = (List) map.get("downloads");
      int nbDownloads;
      if (downloads == null) {
        //No downloads entry, then use the old way
        iter = map.values().iterator();
        nbDownloads = map.size();
      }
      else {
        //New way, downloads stored in a list
        iter = downloads.iterator();
        nbDownloads = downloads.size();
      }
      int currentDownload = 0;
      while (iter.hasNext()) {
        currentDownload++;        
        Map mDownload = (Map) iter.next();
        try {
          String fileName = new String((byte[]) mDownload.get("torrent"), Constants.DEFAULT_ENCODING);
          if(listener != null && nbDownloads > 0) {
            listener.reportPercent(100 * currentDownload / nbDownloads);
          }
          if(listener != null) {
            listener.reportCurrentTask(MessageText.getString("splash.loadingTorrent") 
                + " " + currentDownload + " "
                + MessageText.getString("splash.of") + " " + nbDownloads
                + " : " + fileName
                );
          }
          String savePath = new String((byte[]) mDownload.get("path"), Constants.DEFAULT_ENCODING);
          int nbUploads = ((Long) mDownload.get("uploads")).intValue();
          int maxDL = mDownload.get("maxdl")==null?0:((Long) mDownload.get("maxdl")).intValue();
          int state = DownloadManager.STATE_WAITING;
          if (debug)
            state = DownloadManager.STATE_STOPPED;
          else {
            if (mDownload.containsKey("state")) {
              state = ((Long) mDownload.get("state")).intValue();
              if (state != DownloadManager.STATE_STOPPED &&
                  state != DownloadManager.STATE_QUEUED &&
                  state != DownloadManager.STATE_WAITING)
                state = DownloadManager.STATE_QUEUED;
            }
            else {
              int stopped = ((Long) mDownload.get("stopped")).intValue();
              if (stopped == 1)
                state = DownloadManager.STATE_STOPPED;
            } 
          }
          Long lPriority = (Long) mDownload.get("priority");
          Long lDownloaded = (Long) mDownload.get("downloaded");
          Long lUploaded = (Long) mDownload.get("uploaded");
          Long lCompleted = (Long) mDownload.get("completed");
          Long lDiscarded = (Long) mDownload.get("discarded");
          Long lHashFails = (Long) mDownload.get("hashfails");
          Long lForceStart = (Long) mDownload.get("forceStart");
          if (lForceStart == null) {
	          Long lStartStopLocked = (Long) mDownload.get("startStopLocked");
	          if(lStartStopLocked != null) {
	          	lForceStart = lStartStopLocked;
	          }
	        }
          Long lPosition = (Long) mDownload.get("position");
          String sCategory = null;
          if (mDownload.containsKey("category"))
            sCategory = new String((byte[]) mDownload.get("category"), Constants.DEFAULT_ENCODING);
          DownloadManager dm = DownloadManagerFactory.create(this, fileName, savePath, state, true, true );
          DownloadManagerStats stats = dm.getStats();
          stats.setMaxUploads(nbUploads);
          stats.setMaxDownloadKBSpeed( maxDL );
          if (lPriority != null) {
            dm.setPriority(lPriority.intValue());
          }
          if (lCompleted != null) {
            stats.setDownloadCompleted(lCompleted.intValue());
          }
          
          if (lDiscarded != null) {
            stats.saveDiscarded(lDiscarded.longValue());
          }
          if (lHashFails != null) {
            stats.saveHashFails(lHashFails.longValue());
          }
          
          if (sCategory != null) {
            Category cat = CategoryManager.getCategory(sCategory);
            if (cat != null) dm.setCategory(cat);
          }

          boolean bCompleted = stats.getDownloadCompleted(false) == 1000;
          if (bCompleted) 
            ++numCompleted;
          else
            ++numDownloading;
  	      dm.setOnlySeeding(bCompleted);
  	      bCompleted = stats.getDownloadCompleted(false) == 1000;

          if (lDownloaded != null && lUploaded != null) {
            long lUploadedValue = lUploaded.longValue();
            long lDownloadedValue = lDownloaded.longValue();
            if (bCompleted && (lDownloadedValue == 0)) {
              lDownloadedValue = dm.getSize();
              if ((lUploadedValue * 1000) / lDownloadedValue < minQueueingShareRatio)
                lUploadedValue = dm.getSize() * minQueueingShareRatio / 1000;
            }
            stats.setSavedDownloadedUploaded(lDownloadedValue, lUploadedValue);
          }

          if (lPosition != null)
            dm.setPosition(lPosition.intValue());
          else if (stats.getDownloadCompleted(false) < 1000)
            dm.setPosition(bCompleted ? numCompleted : numDownloading);

          Long lSecondsDLing = (Long)mDownload.get("secondsDownloading");
          if (lSecondsDLing != null) {
            stats.setSecondsDownloading(lSecondsDLing.longValue());
          }

          Long lSecondsOnlySeeding = (Long)mDownload.get("secondsOnlySeeding");
          if (lSecondsOnlySeeding != null) {
            stats.setSecondsOnlySeeding(lSecondsOnlySeeding.longValue());
          }
          
          Long already_allocated = (Long)mDownload.get( "allocated" );
          if( already_allocated != null && already_allocated.intValue() == 1 ) {
            dm.setDataAlreadyAllocated( true );
          }
          
          Long creation_time = (Long)mDownload.get( "creationTime" );
          
          if ( creation_time != null ){
          	
          	long	ct = creation_time.longValue();
          	
          	if ( ct < SystemTime.getCurrentTime()){
          	
          		dm.setCreationTime( ct );
          	}
          }
          
          //TODO: remove this try/catch.  should only be needed for those upgrading from previous snapshot
          try {
          	//load file priorities
          	List file_priorities = (List) mDownload.get("file_priorities");
          	if ( file_priorities != null ) dm.setData( "file_priorities", file_priorities );
          }
          catch (Throwable t) { t.printStackTrace(); }

          
          this.addDownloadManager(dm, false);

          if(lForceStart != null) {
            if(lForceStart.intValue() == 1) {
              dm.setForceStart(true);
            }
          }
        }
        catch (UnsupportedEncodingException e1) {
          //Do nothing and process next.
        }
        catch (Throwable e) {
          LGLogger.log("Error while loading downloads.  One download may not have been added to the list.");
          e.printStackTrace();
        }
      }
      // Someone could have mucked with the config file and set weird positions,
      // so fix them up.
      fixUpDownloadManagerPositions();
      LGLogger.log("Loaded " + managers.size() + " torrents");
  	}catch( Throwable e ){
  			// there's been problems with corrupted download files stopping AZ from starting
  			// added this to try and prevent such foolishness
  		
  		e.printStackTrace();
  	}
  }

  private void saveDownloads() 
  {
    //    if(Boolean.getBoolean("debug")) return;

  	synchronized( managers ){
      LGLogger.log("Saving Download List (" + managers.size() + " items)");
	    Map map = new HashMap();
	    List list = new ArrayList(managers.size());
	    for (int i = 0; i < managers.size(); i++) {
	      DownloadManager dm = (DownloadManager) managers.get(i);
	      
	      if ( dm.isPersistent()){
	      	DownloadManagerStats stats = dm.getStats();
		      Map dmMap = new HashMap();
		      dmMap.put("torrent", dm.getTorrentFileName());
		      dmMap.put("path", dm.getFullName());
		      dmMap.put("uploads", new Long(stats.getMaxUploads()));
		      dmMap.put("maxdl", new Long(stats.getMaxDownloadKBSpeed()));
          int state = dm.getState();
          if (dm.getOnlySeeding() && !dm.isForceStart() && 
              state != DownloadManager.STATE_STOPPED) {
            state = DownloadManager.STATE_QUEUED;
          } else if (state == DownloadManager.STATE_ERROR)
            state = DownloadManager.STATE_STOPPED;
          else if (state != DownloadManager.STATE_STOPPED &&
                  state != DownloadManager.STATE_QUEUED &&
                  state != DownloadManager.STATE_WAITING)
            state = DownloadManager.STATE_WAITING;
          dmMap.put("state", new Long(state));
		      dmMap.put("priority", new Long(dm.getPriority()));
		      dmMap.put("position", new Long(dm.getPosition()));
		      dmMap.put("downloaded", new Long(stats.getDownloaded()));
		      dmMap.put("uploaded", new Long(stats.getUploaded()));
		      dmMap.put("completed", new Long(stats.getDownloadCompleted(true)));
		      dmMap.put("discarded", new Long(stats.getDiscarded()));
		      dmMap.put("hashfails", new Long(stats.getHashFails()));
		      dmMap.put("forceStart", new Long(dm.isForceStart() && (dm.getState() != DownloadManager.STATE_CHECKING) ? 1 : 0));
          dmMap.put("secondsDownloading", new Long(stats.getSecondsDownloading()));
          dmMap.put("secondsOnlySeeding", new Long(stats.getSecondsOnlySeeding()));
		      Category category = dm.getCategory();
		      if (category != null && category.getType() == Category.TYPE_USER)
		        dmMap.put("category", category.getName());
          
		      dmMap.put( "creationTime", new Long( dm.getCreationTime()));
		      
		      //save file priorities
          DiskManager disk_manager = dm.getDiskManager();
          if ( disk_manager != null ) disk_manager.storeFilePriorities();
          List file_priorities = (List)dm.getData( "file_priorities" );
          if ( file_priorities != null ) dmMap.put( "file_priorities" , file_priorities );

          dmMap.put( "allocated", new Long( dm.isDataAlreadyAllocated() == true ? 1 : 0 ) );
          
		      list.add(dmMap);
	      }
	    }
	    map.put("downloads", list);
        
	    FileUtil.writeResilientConfigFile("downloads.config", map );
  	}
  }

  /**
   * @return
   */
  public TRTrackerScraper getTrackerScraper() {
    return trackerScraper;
  }

	public GlobalManagerStats
	getStats()
	{
		return( stats );
	}
	
  public int getIndexOf(DownloadManager manager) {
    if (managers != null && manager != null)
      return managers.indexOf(manager);
    return -1;
  }

  public boolean isMoveableUp(DownloadManager manager) {
    if (managers == null)
      return false;

    if ((manager.getStats().getDownloadCompleted(false) == 1000) &&
        (COConfigurationManager.getIntParameter("StartStopManager_iRankType") != 0) &&
        (COConfigurationManager.getBooleanParameter("StartStopManager_bAutoReposition")))
      return false;

    return manager.getPosition() > 1;
  }

  public boolean isMoveableDown(DownloadManager manager) {
    if (managers == null)
      return false;

    boolean isCompleted = manager.getStats().getDownloadCompleted(false) == 1000;

    if (isCompleted &&
        (COConfigurationManager.getIntParameter("StartStopManager_iRankType") != 0) &&
        (COConfigurationManager.getBooleanParameter("StartStopManager_bAutoReposition")))
      return false;

    int numInGroup = 0;
    for (int i = 0; i < managers.size(); i++) {
      DownloadManager dm = (DownloadManager) managers.get(i);
      if ((dm.getStats().getDownloadCompleted(false) == 1000) == isCompleted)
        numInGroup++;
    }
    return manager.getPosition() < numInGroup;
  }

  public void moveUp(DownloadManager manager) {
  	moveTo(manager, manager.getPosition() - 1);
  }

  public void moveDown(DownloadManager manager) {
  	moveTo(manager, manager.getPosition() + 1);
  }

  public void moveTop(DownloadManager[] manager) {
    if (managers != null)
      synchronized (managers) {
      	int newPosition = 1;
        for (int i = 0; i < manager.length; i++)
        	moveTo(manager[i], newPosition++);
      }
  }

  public void moveEnd(DownloadManager[] manager) {
    if (managers != null)
      synchronized (managers) {
        int endPosComplete = 0;
        int endPosIncomplete = 0;
        for (int j = 0; j < managers.size(); j++) {
          DownloadManager dm = (DownloadManager) managers.get(j);
          if (dm.getStats().getDownloadCompleted(false) == 1000)
            endPosComplete++;
          else
            endPosIncomplete++;
        }
        for (int i = manager.length - 1; i >= 0; i--) {
          if (manager[i].getStats().getDownloadCompleted(false) == 1000 && endPosComplete > 0) {
            moveTo(manager[i], endPosComplete--);
          } else if (endPosIncomplete > 0) {
            moveTo(manager[i], endPosIncomplete--);
          }
        }
      }
  }
  
  public void moveTo(DownloadManager manager, int newPosition) {
    if (newPosition < 1)
      return;

    if (managers != null)
      synchronized (managers) {
        int curPosition = manager.getPosition();
        if (newPosition > curPosition) {
          // move [manager] down
          // move everything between [curPosition+1] and [newPosition] up(-) 1
          boolean curCompleted = (manager.getStats().getDownloadCompleted(false) == 1000);
          int numToMove = newPosition - curPosition;
          for (int i = 0; i < managers.size(); i++) {
            DownloadManager dm = (DownloadManager) managers.get(i);
            boolean dmCompleted = (dm.getStats().getDownloadCompleted(false) == 1000);
            if (dmCompleted == curCompleted) {
              int dmPosition = dm.getPosition();
              if ((dmPosition > curPosition) && (dmPosition <= newPosition)) {
                dm.setPosition(dmPosition - 1);
                numToMove--;
                if (numToMove <= 0)
                  break;
              }
            }
          }
          
          manager.setPosition(newPosition);
        }
        else if (newPosition < curPosition && curPosition > 1) {
          // move [manager] up
          // move everything between [newPosition] and [curPosition-1] down(+) 1
          boolean curCompleted = (manager.getStats().getDownloadCompleted(false) == 1000);
          int numToMove = curPosition - newPosition;
  
          for (int i = 0; i < managers.size(); i++) {
            DownloadManager dm = (DownloadManager) managers.get(i);
            boolean dmCompleted = (dm.getStats().getDownloadCompleted(false) == 1000);
            int dmPosition = dm.getPosition();
            if ((dmCompleted == curCompleted) &&
                (dmPosition >= newPosition) &&
                (dmPosition < curPosition)
               ) {
              dm.setPosition(dmPosition + 1);
              numToMove--;
              if (numToMove <= 0)
                break;
            }
          }
          manager.setPosition(newPosition);
        }
      }
  }
	
	public void fixUpDownloadManagerPositions() {
    if (managers != null) {
      synchronized (managers) {
      	int posComplete = 1;
      	int posIncomplete = 1;
		    Collections.sort(managers, new Comparator () {
	          public final int compare (Object a, Object b) {
	            return ((DownloadManager)a).getPosition() - ((DownloadManager)b).getPosition();
	          }
	        } );
        for (int i = 0; i < managers.size(); i++) {
          DownloadManager dm = (DownloadManager) managers.get(i);
          if (dm.getStats().getDownloadCompleted(false) == 1000)
          	dm.setPosition(posComplete++);
         	else
          	dm.setPosition(posIncomplete++);
        }
      }
    }
	}
  
  protected void  informDestroyed() {
  		if ( destroyed )
  		{
  			return;
  		}
  		
  		destroyed = true;
  		
  		/*
		Thread t = new Thread("Azureus: destroy checker")
			{
				public void
				run()
				{
					long	start = SystemTime.getCurrentTime();
							
					while(true){
								
						try{
							Thread.sleep(2500);
						}catch( Throwable e ){
							e.printStackTrace();
						}
								
						if ( SystemTime.getCurrentTime() - start > 10000 ){
									
								// java web start problem here...
								
							// Debug.dumpThreads("Azureus: slow stop - thread dump");
							
							// Debug.killAWTThreads(); doesn't work
						}
					}
				}						
			};
					
		t.setDaemon(true);
				
		t.start();
		*/

  		listeners.dispatch( LDT_DESTROYED, null );
  }
  	
  public void informDestroyInitiated()  {
    listeners.dispatch( LDT_DESTROY_INITIATED, null );		
  }
  	
 	public void
	addListener(
		GlobalManagerListener	listener )
	{
		if ( isStopped ){
				
			listener.destroyed();
				
		}else{			
							
			listeners.addListener(listener);

			// Don't use Dispatch.. async is bad (esp for plugin initialization)
			synchronized( managers ){
				for (int i=0;i<managers.size();i++){
				  listener.downloadManagerAdded((DownloadManager)managers.get(i));
				}	
			}
		}
	}
		
	public void
 	removeListener(
		GlobalManagerListener	listener )
	{			
		listeners.removeListener(listener);
	}
	
	public void
	addDownloadWillBeRemovedListener(
		GlobalManagerDownloadWillBeRemovedListener	l )
	{
		removal_listeners.addListener( l );
	}
	
	public void
	removeDownloadWillBeRemovedListener(
		GlobalManagerDownloadWillBeRemovedListener	l )
	{
		removal_listeners.removeListener( l );
	}
	

  /**
   * @param c the character to be found 
   * @param lastSelectedIndex the highest selection index; -1 to start from the beginning
   * @return index of next item with a name beginning with c, -1 else
   *
   * @author Rene Leonhardt
   */
  public int getNextIndexForCharacter(char c, int lastSelectedIndex) {
    if(c >= '0' && c <= 'z') {
      c = Character.toLowerCase(c);
      if (managers != null) {
        synchronized (managers) {
          if(lastSelectedIndex < 0 || lastSelectedIndex >= managers.size())
            lastSelectedIndex = -1;
          lastSelectedIndex++;
          for (int i = lastSelectedIndex; i < managers.size(); i++) {
            char test = Character.toLowerCase(((DownloadManager) managers.get(i)).getName().charAt(0));
            if(test == c)
              return i;
          }
          for (int i = 0; i < lastSelectedIndex; i++) {
            char test = Character.toLowerCase(((DownloadManager) managers.get(i)).getName().charAt(0));
            if(test == c)
              return i;
          }
        }
      }
    }
    return -1;
  }
  
  public void startChecker()  {
    synchronized( checker ){
      if ( !checker.isAlive()){
        checker.start(); 
      }
    }
  }

  // DownloadManagerListener

  //make sure we update 'downloads.config' on state changes
	public void stateChanged(DownloadManager manager, int state) {
	  needsSaving = true;
	}
		
	public void downloadComplete(DownloadManager manager) { }

  public void completionChanged(DownloadManager manager, boolean bCompleted) { }
  
  public void positionChanged(DownloadManager download, int oldPosition, int newPosition) {
  }
}
