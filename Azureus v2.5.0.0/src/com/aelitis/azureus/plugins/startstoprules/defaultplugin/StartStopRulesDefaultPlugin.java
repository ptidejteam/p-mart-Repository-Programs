/*
 * File    : StartStopRulesDefaultPlugin.java
 * Created : 12-Jan-2004
 * By      : TuxPaper
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

package com.aelitis.azureus.plugins.startstoprules.defaultplugin;


import java.util.*;

import org.gudy.azureus2.core3.config.COConfigurationListener;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.util.*;


import org.gudy.azureus2.plugins.Plugin;
import org.gudy.azureus2.plugins.PluginConfig;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.PluginListener;
import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
import org.gudy.azureus2.plugins.download.*;
import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
import org.gudy.azureus2.plugins.ui.UIInstance;
import org.gudy.azureus2.plugins.ui.UIManagerListener;
import org.gudy.azureus2.plugins.ui.menus.MenuItem;
import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
import org.gudy.azureus2.plugins.ui.tables.TableColumn;
import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
import org.gudy.azureus2.plugins.ui.tables.TableManager;
import org.gudy.azureus2.plugins.ui.tables.TableRow;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;

import com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt.StartStopRulesDefaultPluginSWTUI;

/** Handles Starting and Stopping of torrents.
 *
 * TODO: RANK_TIMED is quite a hack and is spread all over.  It needs to be
 *       redone, probably with a timer on each seeding torrent which triggers
 *       when time is up and it needs to stop.
 *       
 * BUG: When "AutoStart 0 Peers" is on, and minSpeedForActivelySeeding is 
 *      enabled, the 0 peer torrents will continuously switch from seeding to 
 *      queued, probably due to the connection attempt registering speed.
 *      This might be fixed by the "wait XX ms before switching active state"
 *      code.
 *      
 * Other Notes:
 * "CD" is often used to refer to "Seed" or "Seeding", because "C" sounds like
 * "See"
 */
public class StartStopRulesDefaultPlugin
       implements Plugin, COConfigurationListener, AEDiagnosticsEvidenceGenerator
{
  // for debugging
  private static final String sStates = " WPRDS.XEQ";

  /** Do not rank completed torrents */  
  public static final int RANK_NONE = 0;
  /** Rank completed torrents using Seeds:Peer Ratio */  
  public static final int RANK_SPRATIO = 1;
  /** Rank completed torrents using Seed Count method */  
  public static final int RANK_SEEDCOUNT = 2;
  /** Rank completed torrents using a timed rotation of minTimeAlive */
  public static final int RANK_TIMED = 3;
  
  /** 
   * Force at least one check every period of time (in ms).  
   * Used in ChangeFlagCheckerTask 
   */
  private static final int FORCE_CHECK_PERIOD	= 60000;

  /** 
   * Check for non triggerable changes ever period of time (in ms) 
   */
  private static final int CHECK_FOR_GROSS_CHANGE_PERIOD = 30000;

  /** 
   * Interval in ms between checks to see if the {@link #somethingChanged} 
   * flag changed 
   */
  private static final int PROCESS_CHECK_PERIOD	= 500;
  
  /** Wait xx ms before starting completed torrents (so scrapes can come in) */
  private static final int MIN_SEEDING_STARTUP_WAIT = 20000;
  /** Wait at least xx ms for first scrape, before starting completed torrents */
  private static final int MIN_FIRST_SCRAPE_WAIT = 90000;
  
  // Core/Plugin classes
  private AEMonitor		this_mon	= new AEMonitor( "StartStopRules" );
  private PluginInterface     pi;
  protected PluginConfig      plugin_config;
  private DownloadManager     download_manager;
  protected LoggerChannel     log;

  /** Used only for RANK_TIMED. Recalculate ranks on a timer */
  private RecalcSeedingRanksTask recalcSeedingRanksTask;

  /** Map to relate downloadData to a Download */  
  private static Map downloadDataMap = AEMonitor.getSynchronisedMap(new HashMap());

  private volatile boolean         closingDown;
  private volatile boolean         somethingChanged;
  
  private Set ranksToRecalc = new HashSet();

  /** When rules class started.  Used for initial waiting logic */
  private long startedOn;

  // Config Settings
  /** Whether Debug Info is written to the log and tooltip */
  protected boolean bDebugLog;
	/** Ranking System to use.  One of RANK_* constants */
  private int iRankType = -1;
  
  private int minSpeedForActiveSeeding;
  // count x peers as a full copy, but..
  private int numPeersAsFullCopy;
  // don't count x peers as a full copy if seeds below
  private int iFakeFullCopySeedStart;
  private int _maxActive;
  private boolean _maxActiveWhenSeedingEnabled;
  private int _maxActiveWhenSeeding;
  
  private int maxDownloads;

  private boolean bAutoReposition;
  private long minTimeAlive;
  
  private boolean bAutoStart0Peers;
  private int iMaxUploadSpeed;
  
  private static boolean bAlreadyInitialized = false;

  // UI
  private TableColumn seedingRankColumn;
  // UI
  private TableContextMenuItem debugMenuItem = null;
  
  private boolean bSWTUI = false;
  
  TorrentAttribute torrentAttributeContent = null;

  public void initialize(PluginInterface _plugin_interface) {
		if (bAlreadyInitialized) {
			System.err.println("StartStopRulesDefaultPlugin Already initialized!!");
		} else {
			bAlreadyInitialized = true;
		}

		AEDiagnostics.addEvidenceGenerator(this);

		startedOn = SystemTime.getCurrentTime();

		pi = _plugin_interface;
		torrentAttributeContent = pi.getTorrentManager().getPluginAttribute(TorrentAttribute.TA_CONTENT_MAP);		
		download_manager = pi.getDownloadManager();

		pi.getPluginProperties().setProperty("plugin.version", "1.0");
		pi.getPluginProperties().setProperty("plugin.name", "Start/Stop Rules");

		pi.addListener(new PluginListener() {
			public void initializationComplete() {
				// CPU Intensive, delay until a little after all plugin initializations
				// XXX Would be better if we could delay it until UI is done,
				//     but there may be no UI..
				new DelayedEvent("StartStop:initComp", 12000, new AERunnable() {
					public void runSupport() {
						download_manager.addListener(new StartStopDMListener());
						SimpleTimer.addPeriodicEvent("StartStop:gross", CHECK_FOR_GROSS_CHANGE_PERIOD,
								new ChangeCheckerTimerTask());
						SimpleTimer.addPeriodicEvent("StartStop:check", PROCESS_CHECK_PERIOD,
								new ChangeFlagCheckerTask());
					}
				});
			}

			public void closedownInitiated() {
				closingDown = true;

				// we don't want to go off recalculating stuff when config is saved
				// on closedown
				COConfigurationManager.removeListener(StartStopRulesDefaultPlugin.this);
			}

			public void closedownComplete() { /* not implemented */
			}
		});

		log = pi.getLogger().getChannel("StartStopRules");
		log.log(LoggerChannel.LT_INFORMATION,
				"Default StartStopRules Plugin Initialisation");

		COConfigurationManager.addListener(this);

		plugin_config = pi.getPluginconfig();

		try {
			pi.getUIManager().addUIListener(new UIManagerListener() {
				public void UIAttached(UIInstance instance) {
					TableManager tm = pi.getUIManager().getTableManager();
					seedingRankColumn = tm.createColumn(
							TableManager.TABLE_MYTORRENTS_COMPLETE, "SeedingRank");
					seedingRankColumn.initialize(TableColumn.ALIGN_TRAIL,
							TableColumn.POSITION_LAST, 80, TableColumn.INTERVAL_LIVE);

					SeedingRankColumnListener columnListener = new SeedingRankColumnListener(
							downloadDataMap, plugin_config);
					seedingRankColumn.addCellRefreshListener(columnListener);
					tm.addColumn(seedingRankColumn);

					if (instance instanceof UISWTInstance) {
						bSWTUI = true;
						new StartStopRulesDefaultPluginSWTUI(pi);
					}
				}

				public void UIDetached(UIInstance instance) {

				}
			});
		} catch (Throwable e) {
			Debug.printStackTrace(e);
		}
		reloadConfigParams();
	}
  
  public static DefaultRankCalculator getRankCalculator(Download dl) {
  	return (DefaultRankCalculator)downloadDataMap.get(dl);
  }
  
  private void recalcAllSeedingRanks(boolean force) {
  	if ( closingDown ){
  		return;
  	}
  	
  	try{
  		this_mon.enter();
  	
	    DefaultRankCalculator[] dlDataArray = 
	      (DefaultRankCalculator[])downloadDataMap.values().toArray(new DefaultRankCalculator[0]);
	
	    // Check Group #1: Ones that always should run since they set things
	    for (int i = 0; i < dlDataArray.length; i++) {
	      if (force)
	        dlDataArray[i].getDownloadObject().setSeedingRank(0);
	      dlDataArray[i].recalcSeedingRank();
	    }
  	}finally{
  		
  		this_mon.exit();
  	}
  }

  
  /** A simple timer task to recalculate all seeding ranks.
   */
  private class RecalcSeedingRanksTask implements TimerEventPerformer 
  {
  	boolean bCancel = false;
  	
  	public void perform(TimerEvent event) {
  		if (bCancel) {
  			event.cancel();
  			return;
  		}
      // System.out.println("RecalcAllSeedingRanks");
      recalcAllSeedingRanks(false);
    }

		/**
		 * 
		 */
		public void cancel() {
			bCancel = true;
		}
  }

  /** This class check if the somethingChanged flag and call process() when
   * its set.  This allows pooling of changes, thus cutting down on the number
   * of sucessive process() calls.
   */
  private class ChangeFlagCheckerTask implements TimerEventPerformer
  {
  	long	last_process_time = 0;
	
  	public void perform(TimerEvent event) {
      if (closingDown)
        return;

      long	now = SystemTime.getCurrentTime();

      if ( 	now < last_process_time  ||
      		now - last_process_time >= FORCE_CHECK_PERIOD ){
      	
      	somethingChanged	= true;
      }
      		
      if (somethingChanged) {
      	
        try {
        	last_process_time	= now;
        	
        	process();
        	
        } catch( Exception e ) {
        	
        	Debug.printStackTrace( e );
        }
      }
    }
  }
  
  /** Listen to Download changes and recalc SR if needed 
   */
  private class StartStopDownloadListener implements DownloadListener
  {
    public void stateChanged(Download download, int old_state, int new_state) {
      DefaultRankCalculator dlData = (DefaultRankCalculator)downloadDataMap.get(download);

      if (dlData != null) {
        // force a SR recalc, so that it gets position properly next process()
        requestProcessCycle(dlData);
        if (bDebugLog) 
        	log.log(dlData.dl.getTorrent(), LoggerChannel.LT_INFORMATION,
							"somethingChanged: stateChange from " + sStates.charAt(old_state)
									+ " (" + old_state + ") to " + sStates.charAt(new_state)
									+ " (" + new_state + ")");
      }
    }

    public void positionChanged(Download download, 
                                int oldPosition, int newPosition) {
      DefaultRankCalculator dlData = (DefaultRankCalculator)downloadDataMap.get(download);
      if (dlData != null) {
      	requestProcessCycle(dlData);
        if (bDebugLog) 
          log.log(dlData.dl.getTorrent(), LoggerChannel.LT_INFORMATION,
							"somethingChanged: positionChanged from " + oldPosition + " to "
									+ newPosition);
      }
    }
  }

  /** Update SeedingRank when a new scrape result comes in. 
   */
  private class StartStopDMTrackerListener implements DownloadTrackerListener
  {
  	public void scrapeResult( DownloadScrapeResult result ) {
  		Download dl = result.getDownload();

  		// Skip if error (which happens when listener is first added and the
  		// torrent isn't scraped yet)
  		if (result.getResponseType() == DownloadScrapeResult.RT_ERROR) {
        if (bDebugLog)
					log.log(dl.getTorrent(), LoggerChannel.LT_INFORMATION,
							"Ignored somethingChanged: new scrapeResult (RT_ERROR)");
  			return;
 			}

      DefaultRankCalculator dlData = (DefaultRankCalculator)downloadDataMap.get(dl);
      if (dlData != null) {
      	requestProcessCycle(dlData);
        if (bDebugLog)
					log.log(dl.getTorrent(), LoggerChannel.LT_INFORMATION,
							"somethingChanged: new scrapeResult S:" + result.getSeedCount()
									+ ";P:" + result.getNonSeedCount());
      }
  	}
  	
  	public void announceResult( DownloadAnnounceResult result ) {
  		// Announces are useless to us.  Even if the announce contains seed/peer
  		// count, they are not stored in the DownloadAnnounceResult.  Instead,
  		// they are passed off to the DownloadScrapeResult, and a scrapeResult
  		// is triggered
  	}
  }

  private class StartStopDownloadActivationListener implements DownloadActivationListener
  {
		public boolean activationRequested(DownloadActivationEvent event) {
			//System.out.println("StartStop: activation request: count = "
			//		+ event.getActivationCount());
			Download download = event.getDownload();
      DefaultRankCalculator dlData = (DefaultRankCalculator) downloadDataMap.get(download);
			// ok to be null
			requestProcessCycle(dlData);
			
			if (download.isComplete()) {
				// quick and dirty check: keep connection if scrape peer count is 0
				// there's a (good?) chance we'll start in the next process cycle 
		    DownloadScrapeResult sr = event.getDownload().getLastScrapeResult();
	      int numPeers = sr.getNonSeedCount();
	      if (numPeers <= 0) {
	      	return true;
	      }
			}

			return false;
		}
	}
  
  /* Create/Remove downloadData object when download gets added/removed.
   * RecalcSeedingRank & process if necessary.
   */
  private class StartStopDMListener implements DownloadManagerListener
  {
    private DownloadTrackerListener download_tracker_listener;
    private DownloadListener        download_listener;
    private DownloadActivationListener	download_activation_listener;

    public StartStopDMListener() {
      download_tracker_listener = new StartStopDMTrackerListener();
      download_listener = new StartStopDownloadListener();
      download_activation_listener = new StartStopDownloadActivationListener();
    }

    public void downloadAdded( Download  download )
    {
      DefaultRankCalculator dlData = null;
      if (downloadDataMap.containsKey(download)) {
        dlData = (DefaultRankCalculator)downloadDataMap.get(download);
      } else {
        dlData = new DefaultRankCalculator(StartStopRulesDefaultPlugin.this, download);
        downloadDataMap.put( download, dlData );
        download.addListener( download_listener );
        download.addTrackerListener( download_tracker_listener, false );
        download.addActivationListener( download_activation_listener );
      }

      if (dlData != null) {
      	requestProcessCycle(dlData);
        if (bDebugLog) 
          log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION,
                  "somethingChanged: downloadAdded, state: "
									+ sStates.charAt(download.getState()));
      }
    }

    public void downloadRemoved( Download  download )
    {
      download.removeListener( download_listener );
      download.removeTrackerListener( download_tracker_listener );
      download.removeActivationListener( download_activation_listener );
      
      if (downloadDataMap.containsKey(download)) {
        downloadDataMap.remove(download);
      }

    	requestProcessCycle(null);
      if (bDebugLog) 
        log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION,
                "somethingChanged: downloadRemoved");
    }
  }

  private long changeCheckCount = 0;
  private long changeCheckTotalMS = 0;
  private long changeCheckMaxMS = 0;

  private class ChangeCheckerTimerTask implements TimerEventPerformer {
  	long lLastRunTime = 0;

  	public void perform(TimerEvent event) {
  		long now = 0;

      // make sure process isn't running and stop it from running while we do stuff
      try{
       	this_mon.enter();

       	now = SystemTime.getCurrentTime();
    	
      	//System.out.println(SystemTime.getCurrentTime() - lLastRunTime);
      	if ( now > lLastRunTime && now - lLastRunTime < 1000){
      		
      		return;
      	}
      	
      	lLastRunTime = now;
      	
        DefaultRankCalculator[] dlDataArray = 
          (DefaultRankCalculator[])downloadDataMap.values().toArray(new DefaultRankCalculator[0]);

        int iNumDLing = 0;
        int iNumCDing = 0;
        for (int i = 0; i < dlDataArray.length; i++) {
          if (dlDataArray[i].changeChecker()) {
          	requestProcessCycle(dlDataArray[i]);
          }

          // Check DLs for change in activeness (speed threshold)
          // (The call sets somethingChanged it was changed)
          if (dlDataArray[i].getActivelyDownloading())
            iNumDLing++;

          // Check Seeders for change in activeness (speed threshold)
          // (The call sets somethingChanged it was changed)
          if (dlDataArray[i].getActivelySeeding()) {
            iNumCDing++;
          }
        }

        int iMaxSeeders = calcMaxSeeders(iNumDLing);
        if (iNumCDing > iMaxSeeders) {
        	requestProcessCycle(null);
            if (bDebugLog) 
              log.log(LoggerChannel.LT_INFORMATION,
                      "somethingChanged: More Seeding than limit");
        }

      }finally{
      	if (now > 0) {
	      	changeCheckCount++;
	      	long timeTaken = (SystemTime.getCurrentTime() - now);
	      	changeCheckTotalMS += timeTaken;
	      	if (timeTaken > changeCheckMaxMS) {
	      		changeCheckMaxMS = timeTaken;
	      	}
      	}

      	this_mon.exit();
      }
    }
  }

  // ConfigurationListener
  public void configurationSaved() {
    reloadConfigParams();
  }

  private void reloadConfigParams() {
  	try{
  		this_mon.enter();
  	
	    int iNewRankType = plugin_config.getIntParameter("StartStopManager_iRankType");
	    minSpeedForActiveSeeding = plugin_config.getIntParameter("StartStopManager_iMinSpeedForActiveSeeding");
	    _maxActive = plugin_config.getIntParameter("max active torrents");
	    _maxActiveWhenSeedingEnabled = plugin_config.getBooleanParameter("StartStopManager_bMaxActiveTorrentsWhenSeedingEnabled");
	    _maxActiveWhenSeeding		= plugin_config.getIntParameter("StartStopManager_iMaxActiveTorrentsWhenSeeding");
	    
	    maxDownloads = plugin_config.getIntParameter("max downloads");
	    numPeersAsFullCopy = plugin_config.getIntParameter("StartStopManager_iNumPeersAsFullCopy");
	    iFakeFullCopySeedStart = plugin_config.getIntParameter("StartStopManager_iFakeFullCopySeedStart");
	    bAutoReposition = plugin_config.getBooleanParameter("StartStopManager_bAutoReposition");
	    minTimeAlive = plugin_config.getIntParameter("StartStopManager_iMinSeedingTime") * 1000;
	    bDebugLog = plugin_config.getBooleanParameter("StartStopManager_bDebugLog");
	
	    bAutoStart0Peers = plugin_config.getBooleanParameter("StartStopManager_bAutoStart0Peers");
	    iMaxUploadSpeed = plugin_config.getIntParameter("Max Upload Speed KBs",0);
	
	    boolean	move_top = plugin_config.getBooleanParameter( "StartStopManager_bNewSeedsMoveTop" );
	    plugin_config.setBooleanParameter( PluginConfig.CORE_PARAM_BOOLEAN_NEW_SEEDS_START_AT_TOP, move_top );
	    
	    if (iNewRankType != iRankType) {
	      iRankType = iNewRankType;
	      
	      // shorten recalc for timed rank type, since the calculation is fast and we want to stop on the second
	      if (iRankType == RANK_TIMED) {
	        if (recalcSeedingRanksTask == null) {
	          recalcSeedingRanksTask = new RecalcSeedingRanksTask();
	          SimpleTimer.addPeriodicEvent("StartStop:recalcSR", 1000, recalcSeedingRanksTask);
	        }
	      } else if (recalcSeedingRanksTask != null) {
	        recalcSeedingRanksTask.cancel();
	        recalcSeedingRanksTask = null;
	      }
	    }

/*	    
	    // limit _maxActive and maxDownloads based on TheColonel's specs
	    
	    // maxActive = max_upload_speed / (slots_per_torrent * min_speed_per_peer)
	    if (_maxActive > 0) {
		    int iSlotsPerTorrent = plugin_config.getIntParameter("Max Uploads");
		    // TODO: Track upload speed, storing the max upload speed over a minute
		    //        and use that for "unlimited" setting, or huge settings (like 200)
		    if (iSlotsPerTorrent > 0) {
			    int iMinSpeedPerPeer = 3; // for now.  TODO: config value
			    int _maxActiveLimit = iMaxUploadSpeed / (iSlotsPerTorrent * iMinSpeedPerPeer);
			    if (_maxActive > _maxActiveLimit) {
			    	_maxActive = _maxActiveLimit;
			    	plugin_config.setIntParameter(PluginConfig.CORE_PARAM_INT_MAX_ACTIVE, _maxActive);
			    }
		    }

		    if (maxDownloads > _maxActive) {
		    	maxDownloads = _maxActive;
		    	plugin_config.setIntParameter(PluginConfig.CORE_PARAM_INT_MAX_DOWNLOADS, maxDownloads);
		    }
	    }
*/

	    // force a recalc on all downloads by setting SR to 0, scheduling
	    // a recalc on next process, and requsting a process cycle
	    
	    Collection allDownloads = downloadDataMap.values();
	    DefaultRankCalculator[] dlDataArray = 
	      (DefaultRankCalculator[])allDownloads.toArray(new DefaultRankCalculator[0]);
	    for (int i = 0; i < dlDataArray.length; i++) {
        dlDataArray[i].getDownloadObject().setSeedingRank(0);
	    }
	    ranksToRecalc.addAll(allDownloads);
	    requestProcessCycle(null);

			if (bDebugLog) {
				log.log(LoggerChannel.LT_INFORMATION,
								"somethingChanged: config reload");
				try {
					if (debugMenuItem == null) {
						final String DEBUG_MENU_ID = "StartStopRules.menu.viewDebug";
						MenuItemListener menuListener = new MenuItemListener() {
							public void selected(MenuItem menu, Object target) {
								if (!(target instanceof TableRow))
									return;

								TableRow tr = (TableRow) target;
								Object ds = tr.getDataSource();

								if (!(ds instanceof Download))
									return;

								DefaultRankCalculator dlData = (DefaultRankCalculator) downloadDataMap
										.get(ds);

								if (dlData != null) {
									if (bSWTUI)
										StartStopRulesDefaultPluginSWTUI.openDebugWindow(dlData);
									else
										pi.getUIManager().showTextMessage(
												null,
												null,
												"FP:\n" + dlData.sExplainFP + "\n" + "SR:"
														+ dlData.sExplainSR + "\n" + "TRACE:\n"
														+ dlData.sTrace);
								}
							}
						};
						TableManager tm = pi.getUIManager().getTableManager();
						TableContextMenuItem menu;
						menu = tm.addContextMenuItem(
								TableManager.TABLE_MYTORRENTS_COMPLETE, DEBUG_MENU_ID);
						menu.addListener(menuListener);
						menu = tm.addContextMenuItem(
								TableManager.TABLE_MYTORRENTS_INCOMPLETE, DEBUG_MENU_ID);
						menu.addListener(menuListener);
					}
				} catch (Throwable t) {
					Debug.printStackTrace(t);
				}
			}

		} finally {
			this_mon.exit();
		}
  }
  
  private int calcMaxSeeders(int iDLs) {
    // XXX put in subtraction logic here
	  int	maxActive = getMaxActive();
    return (maxActive == 0) ? 99999 : maxActive - iDLs;
  }

  protected int getMaxActive() {
		if (!_maxActiveWhenSeedingEnabled)
			return (_maxActive);

		if (download_manager.isSeedingOnly()) {

			if (_maxActiveWhenSeeding <= _maxActive)
				return (_maxActiveWhenSeeding);

			// danger here if we are in a position where allowing more to start when seeding
			// allows a non-seeding download to start (looping occurs)

			Download[] downloads = download_manager.getDownloads();

			boolean danger = false;

			for (int i = 0; i < downloads.length && !danger; i++) {

				Download download = downloads[i];

				int state = download.getState();

				if (state == Download.ST_DOWNLOADING || state == Download.ST_SEEDING
						|| state == Download.ST_STOPPED || state == Download.ST_STOPPING
						|| state == Download.ST_ERROR) {

					// not interesting, they can't potentially cause trouble

				} else {

					// look for incomplete files

					DiskManagerFileInfo[] files = download.getDiskManagerFileInfo();

					for (int j = 0; j < files.length; j++) {

						DiskManagerFileInfo file = files[j];

						if ((!file.isSkipped()) && file.getDownloaded() != file.getLength()) {

							danger = true;

							break;
						}
					}
				}
			}

			if (!danger)
				return (_maxActiveWhenSeeding);
		}
		
		return (_maxActive);
	}
  
  private class TotalsStats {
		// total Forced Seeding doesn't include stalled torrents
		int forcedSeeding = 0;

		int forcedSeedingNonFP = 0;

		int waitingToSeed = 0;

		int waitingToDL = 0;

		int downloading = 0;

		int activelyDLing = 0;

		int activelyCDing = 0;

		int complete = 0;

		int incompleteQueued = 0;

		int firstPriority = 0;

		int stalledSeeders = 0;

		int stalledFPSeeders = 0;

		/**
		 * Indicate whether it's ok to start seeding.
		 * <p>
		 * Seeding can start right away when there's no auto-ranking or we are on
		 * timed ranking. Otherwise, we wait until one of the following happens:
		 * <ul>
		 * <li>Any non-stopped/errored torrent gets a scrape result AND it's after
		 *   {@link #MIN_SEEDING_STARTUP_WAIT}
		 * <li>All scrape results come in for completed, non-stopped/errored torrent
		 * <li>Any completed non-stopped/errored torrent is FP
		 * <li>Any torrent has 0 seeds (which, in most cases means it's the highest
		 *   rank)
		 * </ul>
		 * <p>
		 * If none of the above happen, then after {@link #MIN_FIRST_SCRAPE_WAIT}, 
		 * the flag will turned on.
		 */
		// not a total :)
		boolean bOkToStartSeeding;

		int maxSeeders;

		int maxActive;

		int maxTorrents;

		/**
		 * Default Constructor
		 * 
		 * @param dlDataArray list of download data (rank calculators) objects
		 *                     to base calculations on.
		 */
		public TotalsStats(DefaultRankCalculator[] dlDataArray) {
			bOkToStartSeeding = (iRankType == RANK_NONE) || (iRankType == RANK_TIMED)
					|| (SystemTime.getCurrentTime() - startedOn > MIN_FIRST_SCRAPE_WAIT);

			// count the # of ok scrapes when !bOkToStartSeeding, and flip to true
			// if all scrapes for non-stopped/errored completes are okay.
			int totalOKScrapes = 0;
			
			// - Build a SeedingRank list for sorting
			// - Build Count Totals
			// - Do anything that doesn't need to be done in Queued order
			for (int i = 0; i < dlDataArray.length; i++) {
				DefaultRankCalculator dlData = dlDataArray[i];
				if (dlData == null) {
					continue;
				}

				Download download = dlData.getDownloadObject();
				int state = download.getState();
				
				// No stats colllected on error or stopped torrents
				if (state == Download.ST_ERROR || state == Download.ST_STOPPED) {
					continue;
				}

				boolean completed = download.isComplete();
				boolean bIsFirstP = false;
				
				// Count forced seedings as using a slot
				// Don't count forced downloading as using a slot
				if (!completed && download.isForceStart())
					continue;

				if (completed) {
					// Only used when !bOkToStartSeeding.. set only to make compiler happy
					boolean bScrapeOk = true;
					if (!bOkToStartSeeding) {
						bScrapeOk = scrapeResultOk(download);
						if (calcSeedsNoUs(download) == 0 && bScrapeOk)
							bOkToStartSeeding = true;
						else if ((download.getSeedingRank() > 0)
							&& (state == Download.ST_QUEUED || state == Download.ST_READY)
							&& (SystemTime.getCurrentTime() - startedOn > MIN_SEEDING_STARTUP_WAIT))
							bOkToStartSeeding = true;
					}

					complete++;
					
					if (!bOkToStartSeeding && bScrapeOk)
						totalOKScrapes++;

					if (dlData.isFirstPriority()) {
						if (!bOkToStartSeeding)
							bOkToStartSeeding = true;

						firstPriority++;
						bIsFirstP = true;
					}

					if (dlData.getActivelySeeding()) {
						activelyCDing++;
						if (download.isForceStart()) {
							forcedSeeding++;
							if (!bIsFirstP)
								forcedSeedingNonFP++;
						}
					} else if (state == Download.ST_SEEDING) {
						if (bIsFirstP) {
							stalledFPSeeders++;
						}

						stalledSeeders++;
					}

					if (state == Download.ST_READY || state == Download.ST_WAITING
							|| state == Download.ST_PREPARING) {
						waitingToSeed++;
					}

				} else { // !completed
					if (state == Download.ST_DOWNLOADING) {
						downloading++;

						if (dlData.getActivelyDownloading())
							activelyDLing++;
					}

					if (state == Download.ST_READY || state == Download.ST_WAITING
							|| state == Download.ST_PREPARING) {
						waitingToDL++;
					} else if (state == Download.ST_QUEUED) {
						incompleteQueued++;
					} //if state
				} // if completionLevel
			} // for

			if (!bOkToStartSeeding && totalOKScrapes == complete)
				bOkToStartSeeding = true;
				
			maxSeeders = calcMaxSeeders(activelyDLing + waitingToDL);
			maxActive = getMaxActive();

			if (maxActive == 0) {
				maxTorrents = 9999;
			} else if (iMaxUploadSpeed == 0) {
				maxTorrents = maxActive + 4;
			} else {
				// Don't allow more "seeding/downloading" torrents than there is enough
				// bandwidth for.  There needs to be enough bandwidth for at least
				// each torrent to get to its minSpeedForActiveSeeding  
				// (we buffer it at 2x just to be safe).
				int minSpeedPerActive = (minSpeedForActiveSeeding * 2) / 1024;
				// Even more buffering/limiting.  Limit to enough bandwidth for
				// each torrent to have potentially 3kbps.
				if (minSpeedPerActive < 3)
					minSpeedPerActive = 3;
				maxTorrents = (iMaxUploadSpeed / minSpeedPerActive);
				// Allow user to do stupid things like have more slots than their 
				// upload speed can handle
				if (maxTorrents < maxActive)
					maxTorrents = maxActive;
				//System.out.println("maxTorrents = " + maxTorrents + " = " + iMaxUploadSpeed + " / " + minSpeedPerActive);
				//System.out.println("totalTorrents = " + (activeSeedingCount + totalStalledSeeders + totalDownloading));
			}
		} // constructor
	}

	/**
	 * Running totals and stuff that gets used during a process run.
	 * Split into class so complete/incomplete can be seperated into functions
	 */
	public class ProcessVars {
		int numWaitingOrSeeding; // Running Count

		int numWaitingOrDLing; // Running Count

		/**
		 * store whether there's a torrent higher in the list that is queued
		 * We don't want to start a torrent lower in the list if there's a higherQueued
		 */
		boolean higherCDtoStart;
		
		boolean higherDLtoStart;

		/**
		 * Tracks the position we should be at in the Completed torrents list
		 * Updates position.
		 */
		int posComplete;

		boolean bStopAndQueued;
	}

  private long processCount = 0;
  private long processTotalMS = 0;
  private long processMaxMS = 0;
  private long processLastComplete = 0;
  private long processTotalGap = 0;
  private long processTotalRecalcs = 0;
  private long processTotalZeroRecalcs = 0;
	protected void process() {
		long now = 0;
		try {
			this_mon.enter();

			now = SystemTime.getCurrentTime();

			somethingChanged = false;
			Object[] recalcArray = ranksToRecalc.toArray();
			ranksToRecalc.clear();
			for (int i = 0; i < recalcArray.length; i++) {
				DefaultRankCalculator rankObj = (DefaultRankCalculator) recalcArray[i];
				if (bDebugLog) {
					long oldSR = rankObj.dl.getSeedingRank();
					rankObj.recalcSeedingRank();
					String s = "recalc seeding rank.  old/new=" + oldSR + "/"
							+ rankObj.dl.getSeedingRank();
					log.log(rankObj.dl.getTorrent(), LoggerChannel.LT_INFORMATION, s);
				} else {
					rankObj.recalcSeedingRank();
				}
			}
			processTotalRecalcs += recalcArray.length;
			if (recalcArray.length == 0) {
				processTotalZeroRecalcs++;
			}

			// pull the data into a local array, so we don't have to lock/synchronize
			DefaultRankCalculator[] dlDataArray;
			dlDataArray = (DefaultRankCalculator[]) downloadDataMap.values().toArray(
					new DefaultRankCalculator[downloadDataMap.size()]);

			TotalsStats totals = new TotalsStats(dlDataArray);

			String[] mainDebugEntries = null;
			if (bDebugLog) {
				log.log(LoggerChannel.LT_INFORMATION, ">>process()");
				mainDebugEntries = new String[] {
						"ok2Start=" + boolDebug(totals.bOkToStartSeeding),
						"tFrcdCding=" + totals.forcedSeeding,
						"actvCDs=" + totals.activelyCDing,
						"tW8tingToCd=" + totals.waitingToSeed,
						"tDLing=" + totals.downloading,
						"actvDLs=" + totals.activelyDLing,
						"tW8tingToDL=" + totals.waitingToDL,
						"tCom=" + totals.complete,
						"tIncQd=" + totals.incompleteQueued,
						"mxCdrs=" + totals.maxSeeders,
						"tFP=" + totals.firstPriority,
						"maxT=" + totals.maxTorrents };
			}

			// Sort
			Arrays.sort(dlDataArray);
			
			ProcessVars vars = new ProcessVars();

			// pre-included Forced Start torrents so a torrent "above" it doesn't 
			// start (since normally it would start and assume the torrent below it
			// would stop)
			vars.numWaitingOrSeeding = totals.forcedSeeding; // Running Count
			vars.numWaitingOrDLing = 0; // Running Count
			vars.higherCDtoStart = false;
			vars.higherDLtoStart = false;
			vars.posComplete = 0;

			// Loop 2 of 2:
			// - Start/Stop torrents based on criteria

			for (int i = 0; i < dlDataArray.length; i++) {
				DefaultRankCalculator dlData = dlDataArray[i];
				Download download = dlData.getDownloadObject();
				vars.bStopAndQueued = false;
				dlData.sTrace = "";

				// Initialize STATE_WAITING torrents
				if ((download.getState() == Download.ST_WAITING)) {
					try {
						download.initialize();
					} catch (Exception ignore) {
						/*ignore*/
					}
					if (bDebugLog && download.getState() == Download.ST_WAITING) {
						dlData.sTrace += "still in waiting state after initialize!\n";
					}
				}

				if (bAutoReposition && (iRankType != RANK_NONE)
						&& download.isComplete()
						&& (totals.bOkToStartSeeding || totals.firstPriority > 0))
					download.setPosition(++vars.posComplete);

				int state = download.getState();

				// Never do anything to stopped entries
				if (state == Download.ST_STOPPING || state == Download.ST_STOPPED
						|| state == Download.ST_ERROR) {
					continue;
				}

				// Handle incomplete DLs
				if (!download.isComplete()) {
					handleInCompleteDownload(dlData, vars, totals);
				} else {
					handleCompletedDownload(dlDataArray, dlData, vars, totals);
				}
			} // Loop 2/2 (Start/Stopping)

			if (bDebugLog) {
				String[] mainDebugEntries2 = new String[] {
						"ok2Start=" + boolDebug(totals.bOkToStartSeeding),
						"tFrcdCding=" + totals.forcedSeeding,
						"actvCDs=" + totals.activelyCDing,
						"tW8tingToCd=" + totals.waitingToSeed,
						"tDLing=" + totals.downloading,
						"actvDLs=" + totals.activelyDLing,
						"tW8tingToDL=" + totals.waitingToDL,
						"tCom=" + totals.complete,
						"tIncQd=" + totals.incompleteQueued,
						"mxCdrs=" + totals.maxSeeders,
						"tFP=" + totals.firstPriority,
						"maxT=" + totals.maxTorrents };
				printDebugChanges("<<process() ", mainDebugEntries, mainDebugEntries2,
						"", "", true, null);
			}
		} finally {
    	if (now > 0) {
      	processCount++;
      	long timeTaken = (SystemTime.getCurrentTime() - now);
      	processTotalMS += timeTaken;
      	if (timeTaken > processMaxMS) {
      		processMaxMS = timeTaken;
      	}
      	if (processLastComplete > 0) {
      		processTotalGap += (now - processLastComplete);
      	}
      	processLastComplete = now;
    	}

			this_mon.exit();
		}
	} // process()

	/**
	 * @param dlData
	 * @param vars 
	 * @param totals 
	 */
	private void handleInCompleteDownload(DefaultRankCalculator dlData,
			ProcessVars vars, TotalsStats totals) {
		Download download = dlData.dl;
		int state = download.getState();

		if (download.isForceStart()) {
			if (bDebugLog) {
				String s = "isForceStart.. rules skipped";
				log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
				dlData.sTrace += s + "\n";
			}
			return;
		}

		// Don't mess with preparing torrents.  they could be in the 
		// middle of resume-data building, or file allocating.
		if (state == Download.ST_PREPARING) {
			vars.numWaitingOrDLing++;
			if (bDebugLog) {
				String s = "ST_PREPARING.. rules skipped. numW8tngorDLing="
						+ vars.numWaitingOrDLing;
				log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
				dlData.sTrace += s + "\n";
			}
			return;
		}

		int maxDLs = 0;
		int DLmax = 0;
		if (totals.maxActive == 0) {
			maxDLs = maxDownloads;
		} else {
			DLmax = totals.stalledFPSeeders + totals.maxActive - totals.firstPriority
					- totals.forcedSeedingNonFP;
			maxDLs = (DLmax <= 0) ? 0 : maxDownloads - DLmax <= 0 ? maxDownloads
					: DLmax;
		}

		if (bDebugLog) {
			String s = ">> DL state=" + sStates.charAt(download.getState())
					+ ";shareRatio=" + download.getStats().getShareRatio()
					+ ";numW8tngorDLing=" + vars.numWaitingOrDLing + ";maxCDrs="
					+ totals.maxSeeders + ";forced=" + boolDebug(download.isForceStart())
					+ ";actvDLs=" + totals.activelyDLing + ";maxDLs=" + maxDLs
					+ ";ActDLing=" + boolDebug(dlData.getActivelyDownloading())
					+ ";hgherQd=" + boolDebug(vars.higherDLtoStart)
					+ ";isCmplt="+boolDebug(download.isComplete());
			log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
			dlData.sTrace += s + "\n";
		}

		// must use fresh getActivelyDownloading() in case state changed to 
		// downloading
		if ((state == Download.ST_DOWNLOADING && dlData.getActivelyDownloading())
				|| state == Download.ST_READY || state == Download.ST_WAITING
				|| state == Download.ST_PREPARING) {
			vars.numWaitingOrDLing++;
		}

		if (state == Download.ST_READY || state == Download.ST_DOWNLOADING
				|| state == Download.ST_WAITING) {

			boolean bActivelyDownloading = dlData.getActivelyDownloading();

			// Stop torrent if over limit
			boolean bOverLimit = vars.numWaitingOrDLing > maxDLs
					|| (vars.numWaitingOrDLing >= maxDLs && vars.higherDLtoStart);

			boolean bDownloading = state == Download.ST_DOWNLOADING; 
			
			if ((maxDownloads != 0)
					&& bOverLimit
					&& (bActivelyDownloading || !bDownloading || (bDownloading
							&& totals.maxActive != 0 && !bActivelyDownloading && totals.activelyCDing
							+ totals.activelyDLing >= totals.maxActive))) {
				try {
					if (bDebugLog) {
						String s = "   stopAndQueue: " + vars.numWaitingOrDLing
								+ " waiting or downloading, when limit is " + maxDownloads;
						log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
						dlData.sTrace += s + "\n";
					}
					download.stopAndQueue();

					// reduce counts
					vars.numWaitingOrDLing--;
					if (state == Download.ST_DOWNLOADING) {
						totals.downloading--;
						if (bActivelyDownloading)
							totals.activelyDLing--;
					} else {
						totals.waitingToDL--;
					}
					totals.maxSeeders = calcMaxSeeders(totals.activelyDLing
							+ totals.waitingToDL);
				} catch (Exception ignore) {
					/*ignore*/
				}

				state = download.getState();

			} else {
				String s = "NOT queuing: ";
				if (maxDownloads == 0) {
					s += "maxDownloads = " + maxDownloads;
				} else if (!bOverLimit) {
					s += "not over limit.  numWaitingOrDLing("
						+ vars.numWaitingOrDLing + ") <= maxDLs("
						+ maxDLs + ")";
				} else if (!bActivelyDownloading || bDownloading) {
					s += "not actively downloading";
				} else if (totals.maxActive == 0) {
					s += "unlimited active allowed (set)";
				} else {
					s += "# active(" + (totals.activelyCDing + totals.activelyDLing)
							+ ") < maxActive(" + totals.maxActive + ")";
				}
				log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
				dlData.sTrace += s + "\n";
			}
		}

		if (state == Download.ST_READY) {
			if ((maxDownloads == 0) || (totals.activelyDLing < maxDLs)) {
				try {
					if (bDebugLog) {
						String s = "   start: READY && activelyDLing (" + totals.activelyDLing
								+ ") < maxDLs (" + maxDownloads + ")";
						log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
						dlData.sTrace += s + "\n";
					}
					download.start();

					// adjust counts
					totals.waitingToDL--;
					totals.activelyDLing++;
					totals.maxSeeders = calcMaxSeeders(totals.activelyDLing
							+ totals.waitingToDL);
				} catch (Exception ignore) {
					/*ignore*/
				}
				state = download.getState();
			}
		}

		if (state == Download.ST_QUEUED) {
			if ((maxDownloads == 0) || (vars.numWaitingOrDLing < maxDLs)) {
				try {
					if (bDebugLog) {
						String s = "   restart: QUEUED && numWaitingOrDLing ("
								+ vars.numWaitingOrDLing + ") < maxDLS (" + maxDLs + ")";
						log.log(LoggerChannel.LT_INFORMATION, s);
						dlData.sTrace += s + "\n";
					}
					download.restart();

					// increase counts
					vars.numWaitingOrDLing++;
					totals.waitingToDL++;
					totals.maxSeeders = calcMaxSeeders(totals.activelyDLing
							+ totals.waitingToDL);
				} catch (Exception ignore) {/*ignore*/
				}
				state = download.getState();
			}
		}

		state = download.getState();
		if (download.getSeedingRank() >= 0
				&& (state == Download.ST_QUEUED || state == Download.ST_READY
						|| state == Download.ST_WAITING || state == Download.ST_PREPARING)) {
			vars.higherDLtoStart = true;
		}

		if (bDebugLog) {
			String s = "<< DL state=" + sStates.charAt(download.getState())
					+ ";shareRatio=" + download.getStats().getShareRatio()
					+ ";numW8tngorDLing=" + vars.numWaitingOrDLing + ";maxCDrs="
					+ totals.maxSeeders + ";forced=" + boolDebug(download.isForceStart())
					+ ";actvDLs=" + totals.activelyDLing 
					+ ";hgherQd=" + boolDebug(vars.higherDLtoStart)
					+ ";ActDLing=" + boolDebug(dlData.getActivelyDownloading());
			log.log(download.getTorrent(), LoggerChannel.LT_INFORMATION, s);
			dlData.sTrace += s + "\n";
		}
	}

	/**
	 * Process Completed (Seeding) downloads, starting and stopping as needed
	 * 
	 * @param dlDataArray All download data (rank objects) we handle
	 * @param dlData Current download data (rank object) we are processing
	 * @param vars Running calculations
	 * @param totals Summary values used in logic
	 */
	private void handleCompletedDownload(DefaultRankCalculator[] dlDataArray,
			DefaultRankCalculator dlData, ProcessVars vars, TotalsStats totals) {
		if (!totals.bOkToStartSeeding)
			return;

		Download download = dlData.dl;
		int state = download.getState();

		String[] debugEntries = null;
		String sDebugLine = "";
		// Queuing process:
		// 1) Torrent is Queued (Stopped)
		// 2) Slot becomes available
		// 3) Queued Torrent changes to Waiting
		// 4) Waiting Torrent changes to Ready
		// 5) Ready torrent changes to Seeding (with startDownload)
		// 6) Trigger stops Seeding torrent
		//    a) Queue Ranking drops
		//    b) User pressed stop
		//    c) other
		// 7) Seeding Torrent changes to Queued.  Go to step 1.

		int numPeers = calcPeersNoUs(download);
		boolean isFP = false;

		if (bDebugLog) {
			isFP = dlData.isFirstPriority();
			debugEntries = new String[] { "CD state=" + sStates.charAt(state),
					"shareR=" + download.getStats().getShareRatio(),
					"nWorCDing=" + vars.numWaitingOrSeeding,
					"nWorDLing=" + vars.numWaitingOrDLing,
					"sr=" + download.getSeedingRank(),
					"hgherQd=" + boolDebug(vars.higherCDtoStart),
					"maxCDrs=" + totals.maxSeeders,
					"FP=" + boolDebug(isFP),
					"nActCDing=" + totals.activelyCDing,
					"ActCDing=" + boolDebug(dlData.getActivelySeeding()) };
		}

		try {
			boolean bScrapeOk = scrapeResultOk(download);
			
			// Ignore rules and other auto-starting rules do not apply when 
			// bAutoStart0Peers and peers == 0. So, handle starting 0 peers 
			// right at the beginning, and loop early
			if (bAutoStart0Peers && numPeers == 0 && bScrapeOk) {
				if (state == Download.ST_QUEUED) {
					try {
						if (bDebugLog)
							sDebugLine += "\nrestart() 0Peers";
						download.restart(); // set to Waiting
						totals.waitingToSeed++;
						vars.numWaitingOrSeeding++;
	
						state = download.getState();
						if (state == Download.ST_READY) {
							if (bDebugLog)
								sDebugLine += "\nstart(); 0Peers";
							download.start();
							totals.activelyCDing++;
						}
					} catch (Exception ignore) {/*ignore*/
					}
				}
				if (state == Download.ST_READY) {
					try {
						if (bDebugLog)
							sDebugLine += "\nstart(); 0Peers";
						download.start();
						totals.activelyCDing++;
						vars.numWaitingOrSeeding++;
					} catch (Exception ignore) {/*ignore*/
					}
				}
				return;
			}
			
			if (bDebugLog && bAutoStart0Peers && numPeers == 0 && !bScrapeOk 
					&& (state == Download.ST_QUEUED || state == Download.ST_READY)) {
				sDebugLine += "\n  NOT starting 0 Peer torrent because scrape isn't ok";
			}
			
	
			if (!bDebugLog) {
				// In debug mode, we already calculated FP
				isFP = dlData.isFirstPriority();
			}

			boolean bActivelySeeding = dlData.getActivelySeeding();
			boolean okToQueue = (state == Download.ST_READY || state == Download.ST_SEEDING)
					&& (!isFP || (isFP && ((totals.maxActive != 0 && vars.numWaitingOrSeeding >= totals.maxSeeders))))
					//&& (!isFP || (isFP && ((vars.numWaitingOrSeeding >= totals.maxSeeders) || (!bActivelySeeding && (vars.numWaitingOrSeeding + totals.totalStalledSeeders) >= totals.maxSeeders))) )
					&& (!download.isForceStart());
			int rank = download.getSeedingRank();
	
	
			// in RANK_TIMED mode, we use minTimeAlive for rotation time, so
			// skip check
			// XXX do we want changes to take effect immediately  ?
			if (okToQueue && (state == Download.ST_SEEDING) && iRankType != RANK_TIMED) {
				long timeAlive = (SystemTime.getCurrentTime() - download.getStats()
						.getTimeStarted());
				okToQueue = (timeAlive >= minTimeAlive);

				if (!okToQueue && bDebugLog)
					sDebugLine += "\n  Torrent can't be stopped yet, timeAlive("
							+ timeAlive + ") < minTimeAlive(" + minTimeAlive + ")";
			}
	
			if (state != Download.ST_QUEUED
					&& // Short circuit.
					(state == Download.ST_READY || state == Download.ST_WAITING
							|| state == Download.ST_PREPARING ||
					// Forced Start torrents are pre-included in count
					(state == Download.ST_SEEDING && bActivelySeeding && !download
							.isForceStart()))) {
				vars.numWaitingOrSeeding++;
				if (bDebugLog)
					sDebugLine += "\n  Torrent is waiting or seeding";
			}
			
			// Note: First Priority are sorted to the top, 
			//       so they will always start first
	
			// XXX Change to waiting if queued and we have an open slot
			if (!okToQueue
					&& (state == Download.ST_QUEUED)
					&& (totals.maxActive == 0 || vars.numWaitingOrSeeding < totals.maxSeeders)
					//&& (totals.maxActive == 0 || (activeSeedingCount + activeDLCount) < totals.maxActive) &&
					&& (rank >= DefaultRankCalculator.SR_IGNORED_LESS_THAN) && !vars.higherCDtoStart) {
				try {
					if (bDebugLog)
						sDebugLine += "\n  restart: ok2Q=" + okToQueue
								+ "; QUEUED && numWaitingOrSeeding( " + vars.numWaitingOrSeeding
								+ ") < maxSeeders (" + totals.maxSeeders + ")";

					download.restart(); // set to Waiting
					okToQueue = false;
					totals.waitingToSeed++;
					vars.numWaitingOrSeeding++;
					if (iRankType == RANK_TIMED)
						dlData.recalcSeedingRank();
				} catch (Exception ignore) {/*ignore*/
				}
				state = download.getState();
			} else if (bDebugLog && state == Download.ST_QUEUED) {
				sDebugLine += "\n  NOT restarting:";
				if (rank < DefaultRankCalculator.SR_IGNORED_LESS_THAN)
					sDebugLine += " torrent is being ignored";
				else if (vars.higherCDtoStart)
					sDebugLine += " a torrent with a higher rank is queued or starting";
				else {
					if (okToQueue)
						sDebugLine += " no starting of okToQueue'd;";
					
					if (vars.numWaitingOrSeeding >= totals.maxSeeders)
						sDebugLine += " at limit, numWaitingOrSeeding("
								+ vars.numWaitingOrSeeding + ") >= maxSeeders("
								+ totals.maxSeeders + ")";
				}
			}
	
			boolean bForceStop = false;
			// Start download if ready and slot is available
			if (state == Download.ST_READY && totals.activelyCDing < totals.maxSeeders) {
	
				if (rank >= DefaultRankCalculator.SR_IGNORED_LESS_THAN || download.isForceStart()) {
					try {
						if (bDebugLog)
							sDebugLine += "\n  start: READY && total activelyCDing("
									+ totals.activelyCDing + ") < maxSeeders("
									+ totals.maxSeeders + ")";

						download.start();
						okToQueue = false;
					} catch (Exception ignore) {
						/*ignore*/
					}
					state = download.getState();
					totals.activelyCDing++;
					bActivelySeeding = true;
					vars.numWaitingOrSeeding++;
				} else if (okToQueue) {
					// In between switching from STATE_WAITING and STATE_READY,
					// and ignore rule was met, so move it back to Queued
					bForceStop = true;
				}
			}
	
			// if there's more torrents waiting/seeding than our max, or if
			// there's a higher ranked torrent queued, stop this one
	    if (okToQueue || bForceStop) {
	    	
				boolean okToStop = bForceStop;
				if (!okToStop) {
					// break up the logic into variables to make more readable
					boolean bOverLimit = vars.numWaitingOrSeeding > totals.maxSeeders
							|| (vars.numWaitingOrSeeding >= totals.maxSeeders && vars.higherCDtoStart);
					boolean bSeeding = state == Download.ST_SEEDING;

					// not checking AND (at limit of seeders OR rank is set to ignore) AND
					// (Actively Seeding OR StartingUp OR Seeding a non-active download) 
					okToStop = !download.isChecking()
							&& (bOverLimit || rank < DefaultRankCalculator.SR_IGNORED_LESS_THAN)
							&& (bActivelySeeding || !bSeeding || (!bActivelySeeding && bSeeding));
					
					if (bDebugLog) {
						if (okToStop) {
							sDebugLine += "\n  stopAndQueue: ";
							if (bOverLimit) {
								if (vars.higherCDtoStart)
									sDebugLine += "higherQueued (it should be seeding instead of this one)";
								else
									sDebugLine += "over limit";
							} else if (rank < DefaultRankCalculator.SR_IGNORED_LESS_THAN)
								sDebugLine += "ignoreRule met";
							
							sDebugLine += " && ";
							if (bActivelySeeding)
								sDebugLine += "activelySeeding";
							else if (!bSeeding)
								sDebugLine += "not SEEDING";
							else if (!bActivelySeeding && bSeeding)
								sDebugLine += "SEEDING, but not actively";
						}
					} else {
						sDebugLine += "\n  NOT queuing: ";
						if (download.isChecking())
							sDebugLine += "can't auto-queue a checking torrent";
						else if (!bOverLimit)
							sDebugLine += "not over limit.  numWaitingOrSeeding("
									+ vars.numWaitingOrSeeding + ") <= maxSeeders("
									+ totals.maxSeeders + ")";
						else
							sDebugLine += "bActivelySeeding="+bActivelySeeding + ";bSeeding" + bSeeding;
					}
				} else {
					if (bDebugLog)
						sDebugLine += "\n  Forcing a stop..";
				}
	
				if (okToStop) {
					try {
						if (state == Download.ST_READY)
							totals.waitingToSeed--;
	
						download.stopAndQueue();
						vars.bStopAndQueued = true;
						// okToQueue only allows READY and SEEDING state.. and in both cases
						// we have to reduce counts
						if (bActivelySeeding) {
							totals.activelyCDing--;
							bActivelySeeding = false;
							vars.numWaitingOrSeeding--;
						}
						// force stop allows READY states in here, so adjust counts 
						if (state == Download.ST_READY)
							totals.waitingToSeed--;
					} catch (Exception ignore) {
						/*ignore*/
					}
	
					state = download.getState();
				}
			}
	
			// move completed timed rank types to bottom of the list
			if (vars.bStopAndQueued && iRankType == RANK_TIMED) {
				for (int j = 0; j < dlDataArray.length; j++) {
					Download dl = dlDataArray[j].getDownloadObject();
					int sr = dl.getSeedingRank();
					if (sr > 0 && sr < DefaultRankCalculator.SR_TIMED_QUEUED_ENDS_AT) {
						// Move everyone up
						// We always start by setting SR to SR_TIMED_QUEUED_ENDS_AT - position
						// then, the torrent with the biggest starts seeding which is
						// (SR_TIMED_QUEUED_ENDS_AT - 1), leaving a gap.
						// when it's time to stop the torrent, move everyone up, and put 
						// us at the end
						dl.setSeedingRank(sr + 1);
					}
				}
				rank = DefaultRankCalculator.SR_TIMED_QUEUED_ENDS_AT - totals.complete;
				download.setSeedingRank(rank);
			}
	
			state = download.getState();
			if (rank >= 0
					&& (state == Download.ST_QUEUED || state == Download.ST_READY
							|| state == Download.ST_WAITING || state == Download.ST_PREPARING)) {
				vars.higherCDtoStart = true;
			}

		} finally {
			if (bDebugLog) {
				String[] debugEntries2 = new String[] {
						"CD state=" + sStates.charAt(download.getState()),
						"shareR=" + download.getStats().getShareRatio(),
						"nWorCDing=" + vars.numWaitingOrSeeding,
						"nWorDLing=" + vars.numWaitingOrDLing,
						"sr=" + download.getSeedingRank(),
						"hgherQd=" + boolDebug(vars.higherCDtoStart),
						"maxCDrs=" + totals.maxSeeders,
						"FP=" + boolDebug(isFP),
						"nActCDing=" + totals.activelyCDing,
						"ActCDing=" + boolDebug(dlData.getActivelySeeding()) };
				printDebugChanges("", debugEntries, debugEntries2, sDebugLine, "  ",
						true, dlData);
			}
		}
	}

	private String boolDebug(boolean b) {
  	return b ? "Y" : "N";
  }
  
  private void printDebugChanges(String sPrefixFirstLine, 
                                 String[] oldEntries, 
                                 String[] newEntries,
                                 String sDebugLine,
                                 String sPrefix, 
                                 boolean bAlwaysPrintNoChangeLine,
                                 DefaultRankCalculator dlData) {
      boolean bAnyChanged = false;
      String sDebugLineNoChange = sPrefixFirstLine;
      String sDebugLineOld = "";
      String sDebugLineNew = "";
      for (int j = 0; j < oldEntries.length; j++) {
        if (oldEntries[j].equals(newEntries[j]))
          sDebugLineNoChange += oldEntries[j] + ";";
        else {
          sDebugLineOld += oldEntries[j] + ";";
          sDebugLineNew += newEntries[j] + ";";
          bAnyChanged = true;
        }
      }
      String sDebugLineOut = ((bAlwaysPrintNoChangeLine || bAnyChanged) ? sDebugLineNoChange : "") +
                             (bAnyChanged ? "\nOld:"+sDebugLineOld+"\nNew:"+sDebugLineNew : "") + 
                             sDebugLine;
      if (!sDebugLineOut.equals("")) {
        String[] lines = sDebugLineOut.split("\n");
        for (int i = 0; i < lines.length; i++) {
          String s = sPrefix + ((i>0)?"  ":"") + lines[i];
          if (dlData == null) {
          	log.log(LoggerChannel.LT_INFORMATION, s);
          } else {
          	log.log(dlData.dl.getTorrent(), LoggerChannel.LT_INFORMATION, s);
          	dlData.sTrace += s + "\n";
          }
        }
      }
  }

  /**
   * Get # of peers not including us
   *
   */
  public int calcPeersNoUs(Download download) {
    int numPeers = 0;
    DownloadScrapeResult sr = download.getLastScrapeResult();
    if (sr.getScrapeStartTime() > 0) {
      numPeers = sr.getNonSeedCount();
      // If we've scraped after we started downloading
      // Remove ourselves from count
      if ((numPeers > 0) &&
          (download.getState() == Download.ST_DOWNLOADING) &&
          (sr.getScrapeStartTime() > download.getStats().getTimeStarted()))
        numPeers--;
    }
    if (numPeers == 0) {
    	// Fallback to the # of peers we know of
      DownloadAnnounceResult ar = download.getLastAnnounceResult();
      if (ar != null && ar.getResponseType() == DownloadAnnounceResult.RT_SUCCESS)
        numPeers = ar.getNonSeedCount();
      
      if (numPeers == 0) {
      	DownloadActivationEvent activationState = download.getActivationState();
      	if (activationState != null) {
      		numPeers = activationState.getActivationCount();
      	}
      }
    }
    return numPeers;
  }
  
  private boolean scrapeResultOk(Download download) {
    DownloadScrapeResult sr = download.getLastScrapeResult();
    return (sr.getResponseType() == DownloadScrapeResult.RT_SUCCESS);
  }

  /** Get # of seeds, not including us, AND including fake full copies
   * 
   * @param download Download to get # of seeds for
   * @return seed count
   */
  public int calcSeedsNoUs(Download download) {
  	return calcSeedsNoUs(download, calcPeersNoUs(download));
  }

  /** Get # of seeds, not including us, AND including fake full copies
   * 
   * @param download Download to get # of seeds for
   * @param numPeers # peers we know of, required to calculate Fake Full Copies
   * @return seed count
   */
  public int calcSeedsNoUs(Download download, int numPeers) {
    int numSeeds = 0;
    DownloadScrapeResult sr = download.getLastScrapeResult();
    if (sr.getScrapeStartTime() > 0) {
      long seedingStartedOn = download.getStats().getTimeStartedSeeding();
      numSeeds = sr.getSeedCount();
      // If we've scraped after we started seeding
      // Remove ourselves from count
      if ((numSeeds > 0) &&
          (seedingStartedOn > 0) &&
          (download.getState() == Download.ST_SEEDING) &&
          (sr.getScrapeStartTime() > seedingStartedOn))
        numSeeds--;
    }
    if (numSeeds == 0) {
    	// Fallback to the # of seeds we know of
      DownloadAnnounceResult ar = download.getLastAnnounceResult();
      if (ar != null && ar.getResponseType() == DownloadAnnounceResult.RT_SUCCESS)
        numSeeds = ar.getSeedCount();
    }

	  if (numPeersAsFullCopy != 0 && numSeeds >= iFakeFullCopySeedStart)
      numSeeds += numPeers / numPeersAsFullCopy;

    return numSeeds;
  }
  
  /**
   * Request that the startstop rules process.  Used when it's known that
   * something has changed that will effect torrent's state/position/rank.
   */
  private long processMergeCount = 0;
  public void requestProcessCycle(DefaultRankCalculator rankToRecalc) {
		if (rankToRecalc != null) {
			try {
				this_mon.enter();

				ranksToRecalc.add(rankToRecalc);
			} finally {
				this_mon.exit();
			}
		}

		if (somethingChanged) {
			processMergeCount++;
		} else {
			somethingChanged = true;
		}
	}

	public void generate(IndentWriter writer) {
		writer.println("StartStopRules Manager");

		try {
			writer.indent();
			writer.println("Started " + (SystemTime.getCurrentTime() - startedOn)
					+ "ms ago");
			writer.println("downloadDataMap size = " + downloadDataMap.size());
			if (changeCheckCount > 0) {
				writer.println("changeCheck CPU ms: avg="
						+ (changeCheckTotalMS / changeCheckCount) + "; max = "
						+ changeCheckMaxMS);
			}

			if (processCount > 0) {
				writer.println("# process cycles: " + processCount);
				
				writer.println("process CPU ms: avg=" + (processTotalMS / processCount)
						+ "; max = " + processMaxMS);
				if (processCount > 1) {
					writer.println("process avg gap: "
							+ (processTotalGap / ((processCount - 1))) + "ms");
				}
				writer.println("Avg # recalcs per process cycle: "
						+ (processTotalRecalcs / processCount));
				if (processTotalZeroRecalcs > 0) {
					writer.println("# process cycle with 0 recalcs: "
							+ processTotalZeroRecalcs);
				}
			}

		} catch (Exception e) {
			// ignore
		} finally {
			writer.exdent();
		}
	}
} // class

