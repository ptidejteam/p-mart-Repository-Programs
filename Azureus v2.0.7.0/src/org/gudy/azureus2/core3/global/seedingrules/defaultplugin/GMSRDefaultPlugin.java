/*
 * File    : GMSRDefaultPlugin.java
 * Created : 12-Jan-2004
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

package org.gudy.azureus2.core3.global.seedingrules.defaultplugin;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.download.*;
import org.gudy.azureus2.plugins.logging.*;

public class 
GMSRDefaultPlugin 
	implements Plugin
{	
	public static final long			MIN_AUTO_START_PERIOD = 30*1000;
	
	protected PluginInterface			plugin_interface;
	protected PluginConfig				plugin_config;
	protected DownloadManager			download_manager;
	protected DownloadListener			download_listener;
	protected DownloadTrackerListener	download_tracker_listener;
	
	protected Map						download_data = new HashMap();
	
	protected boolean					closing_down;
	protected boolean					something_changed;
	
	protected LoggerChannel				log;
	
	public void 
	initialize(
		PluginInterface _plugin_interface )
	{
		plugin_interface	= _plugin_interface;
		
		plugin_interface.addListener(
			new PluginListener()
			{
				public void
				initializationComplete()
				{
				}
				
				public void
				closedownInitiated()
				{
					synchronized( GMSRDefaultPlugin.this ){
						
						closing_down	= true;
					}
				}
				
				public void
				closedownComplete()
				{
				}
			});
			
		log = plugin_interface.getLogger().getChannel("SeedingRules");
		
		log.log( LoggerChannel.LT_INFORMATION, "Default Seeding Rules Plugin Initialisation" );
		
		plugin_config	= plugin_interface.getPluginconfig();
		
		download_manager = plugin_interface.getDownloadManager();
		
		download_listener	= 
			new DownloadListener()
			{
				public void
				stateChanged(
					Download		download,
					int				old_state,
					int				new_state )
				{
					synchronized( GMSRDefaultPlugin.this ){
						
						something_changed = true;
					}
				}
			};
		
		download_tracker_listener	= 
			new DownloadTrackerListener()
			{
				public void
				scrapeResult(
					DownloadScrapeResult result )
				{
					synchronized( GMSRDefaultPlugin.this ){
						
						something_changed = true;
					}
				}
				
				public void
				announceResult(
					DownloadAnnounceResult	result )
				{
				}								
			};

		download_manager.addListener(
				new DownloadManagerListener()
				{
					public void
					downloadAdded(
						Download	download )
					{
						download_data.put( download, new downloadData());
						
						download.addListener( download_listener );
						
						download.addTrackerListener( download_tracker_listener );
						
						synchronized( GMSRDefaultPlugin.this ){
							
							something_changed = true;
						}
					}
					
					public void
					downloadRemoved(
						Download	download )
					{
						download.removeListener( download_listener );
						
						download.removeTrackerListener( download_tracker_listener );
				
						download_data.remove( download );
						
						synchronized( GMSRDefaultPlugin.this ){
							
							something_changed = true;
						}
					}					
				});
				
			// initial implementation loops - change to event driven maybe although
			// the current rules permit loops under certain circumstances.....
		
		Thread	t = new Thread("GMSRDefaultPlugin")
			{
				public void
				run()
				{
					while(true){
						try{
							synchronized( GMSRDefaultPlugin.this ){
								
								if ( closing_down ){
									
									log.log( LoggerChannel.LT_INFORMATION, "System Closing - processing stopped" );
									
									break;
								}
								
								something_changed = false;
							}
							
							process();
							
						}catch( Throwable e ){
							
							e.printStackTrace();
						}
					
						try{
							int	sleep_period = 1000;
							
							synchronized( GMSRDefaultPlugin.this ){
								
								if ( something_changed ){
									
									sleep_period = 100;
								}
							}
														
							Thread.sleep(sleep_period);
							
						}catch( InterruptedException e ){
							
							e.printStackTrace();
						}
					}
				}
			};
		
		t.setDaemon(true);
		
		t.start();
	}
	
	protected void
	process()
	{
		long	process_time = System.currentTimeMillis();
		
		Download[]	downloads = download_manager.getDownloads();
		
		int	started		= 0;
		int	downloading	= 0;
		
		for (int i=0;i<downloads.length;i++){
			
			Download	download = downloads[i];
						
			downloadData	dl_data = (downloadData)download_data.get( download );
			
			if ( dl_data == null ){
				
				continue;
			}
			
			int	state = download.getState();
			
			if ( state == Download.ST_DOWNLOADING){
				
				started++;
				
				downloading++;
				
			}else if ( state == Download.ST_SEEDING){
				
				started++;

					//First condition to be met to be able to stop a torrent is that the number of seeds
					//	Is greater than the minimal set, if any.
				
				boolean mayStop = false;
				
				DownloadScrapeResult sr = download.getLastScrapeResult();
			
					// this params corresponds to "start seeding if there is less than <n> seeds
					// 0 = "never start". it overrides all other stuff about stopping
				
				int nbMinSeeds = plugin_config.getIntParameter("Start Num Peers", 0);
				
				if ( nbMinSeeds == 0 ){
					
						// no minimum number of seeds, ok to stop if other conditions hold 
					
					mayStop	= true;
					
				}else{
					
						// note that scrape results are cached for some time, so we
						// shouldn't end up thrashing in a start/stop loop because the results
						// of us starting/stopping don't get reported until next scrape.	
					
					if ( sr.getResponseType() == DownloadScrapeResult.RT_SUCCESS ){
						
							// start if < n means we can possibly stop if >= n.
							// However, we assume that the reported seed cound includes
							// ourselves, so we only want to possibly stop if there are more
							// seeds than our limit (i.e. use ">" not ">=" below)
						
						if (sr.getSeedCount() > nbMinSeeds) {
							
							mayStop = true;
						}
					}else{
						
						// no valid data available to we have to assume we need to keep seeding
						// and leave mayStop = false
					}
				}
	
					//Checks if any condition to stop seeding is met
				
				int minShareRatio = 1000 * plugin_config.getIntParameter("Stop Ratio", 0);
				
				int shareRatio = download.getStats().getShareRatio();
				
					//0 means unlimited
				
				// System.out.println( "["+i+"] min = " + minShareRatio + ", share = " + shareRatio + ", may = " + mayStop + ", minSeeds = " + nbMinSeeds + ", scrape = " + sr.getResponseType() + ", sr-seeds = " + sr.getSeedCount());
				
				boolean	download_stopped = false;
				
				if (minShareRatio != 0 && ( shareRatio == -1 || shareRatio > minShareRatio ) && mayStop && ! download.isStartStopLocked()){
					
					try{
						log.log( LoggerChannel.LT_INFORMATION, "Stop ["+i+"]: stop ratio" );
						
						download.stop();
				
						download_stopped	= true;
						
					}catch( DownloadException e ){
						
						e.printStackTrace();
					}
				}

				if ( !download_stopped ){
					
					int minSeedsPerPeersRatio = plugin_config.getIntParameter("Stop Peers Ratio", 0);
					
						//0 means never stop
					
					if (mayStop && minSeedsPerPeersRatio != 0) {
						
						if ( sr.getResponseType() == DownloadScrapeResult.RT_SUCCESS) {
							
							int nbPeers = sr.getNonSeedCount();
							
							int nbSeeds = sr.getSeedCount();
							
							//If there are no seeds, avoid / by 0
							
							if (nbSeeds != 0) {
								
								int ratio = nbPeers / nbSeeds;
								
									//Added a test over the shareRatio greater than 500
									//Avoids disconnecting too early, even with many peers
								
								if (ratio < minSeedsPerPeersRatio && (shareRatio > 500 || shareRatio == -1) && ! download.isStartStopLocked()){
									
									try{
										log.log( LoggerChannel.LT_INFORMATION, "Stop ["+i+"]: min seeds per peer" );
										
										download.stop();
										
									}catch( DownloadException e ){
										
										e.printStackTrace();
									}
								}
							}
						}
					}
				}
				
			}else if ( state == Download.ST_STOPPED && download.getStats().getCompleted() == 1000){
				
					// check that we didn't auto-start this recently
				
				if ( process_time - dl_data.getLastAutoStartTime() > MIN_AUTO_START_PERIOD ){
					
					boolean download_started = false;
					
					//Checks if any condition to start seeding is met
					
					int nbMinSeeds = plugin_config.getIntParameter("Start Num Peers", 0);
					
					int minSeedsPerPeersRatio = plugin_config.getIntParameter("Start Peers Ratio", 0);
					
					//0 means never start
					
					if ( minSeedsPerPeersRatio != 0 && ! download.isStartStopLocked()){
						
						DownloadScrapeResult sr = download.getLastScrapeResult();
						
						if ( sr.getResponseType() == DownloadScrapeResult.RT_SUCCESS ){
							
							int nbPeers = sr.getNonSeedCount();
							
							int nbSeeds = sr.getSeedCount();
							
								//If there are no seeds, avoid / by 0
							
							if (nbPeers != 0){
								
								try{							
							
									if (nbSeeds != 0){
										
										int ratio = nbPeers / nbSeeds;
										
										if (ratio >= minSeedsPerPeersRatio){
											
											log.log( LoggerChannel.LT_INFORMATION, "Start ["+i+"]: min seeds per peer (ratio)" );
											
											download.restart();
											
											download_started = true;
											
										}
									}else{
											//No seeds, at least 1 peer, let's start download.
										
										log.log( LoggerChannel.LT_INFORMATION, "Start ["+i+"]: min seeds per peer (no seeds, >=1 peer)" );
										
										download.restart();
										
										download_started	= true;
									}
								}catch( DownloadException e ){
									
									e.printStackTrace();
								}
							}
						}
					}
					
					if ( !download_started ){
						
						if (nbMinSeeds > 0 && ! download.isStartStopLocked()) {
							
							DownloadScrapeResult sr = download.getLastScrapeResult();
							
							if ( sr.getResponseType() == DownloadScrapeResult.RT_SUCCESS ){
								
								int nbSeeds = sr.getSeedCount();
								
								if (nbSeeds < nbMinSeeds){
									
									try{
										log.log( LoggerChannel.LT_INFORMATION, "Start ["+i+"]: seeds < min seeds" );
										
										download.restart();
										
										download_started	= true;
										
									}catch( DownloadException e ){
										
										e.printStackTrace();
									}
								}
							}
						}
					}
				
					if ( download_started ){
						
						dl_data.setLastAutoStartTime( process_time );
					}
				}
			}
		}

			// regrab the state here as it might/will have changed
		
		boolean alreadyOneAllocatingOrChecking = false;
		
		for (int i=0;i<downloads.length;i++){
			
			Download	download = downloads[i];
			
			int	state = download.getState();
			
			if ( state == Download.ST_PREPARING ){
				
				alreadyOneAllocatingOrChecking = true;
			}
		}
			
		for (int i=0;i<downloads.length;i++){
			
			Download	download = downloads[i];
						
			int	state = download.getState();
			
			if ( state == Download.ST_WAITING && !alreadyOneAllocatingOrChecking ){
					
				try{
					log.log( LoggerChannel.LT_INFORMATION, "Initialize ["+i+"]: state waiting" );
					
					download.initialize();
					
					alreadyOneAllocatingOrChecking	= true;
					
				}catch( DownloadException e ){
					
					e.printStackTrace();
				}
			}
				
			int nbMax = plugin_config.getIntParameter("max active torrents", 4);
			
			int nbMaxDownloads = plugin_config.getIntParameter("max downloads", 4);
			
			if ( 	download.getState() == Download.ST_READY &&
					((nbMax == 0) || (started < nbMax)) &&
					(download.getStats().getCompleted() == 1000 || ((nbMaxDownloads == 0) || (downloading < nbMaxDownloads)))){
					
				try{
					log.log( LoggerChannel.LT_INFORMATION, "Start ["+i+"]: < max downloads running" );
					
					download.start();
							
					started++;
					
					if (download.getStats().getCompleted() != 1000){
						
						downloading++;
					}
				}catch( DownloadException e ){
					
					e.printStackTrace();
					
				}
			}

			if (	download.getState() == Download.ST_SEEDING &&
					(! download.isPriorityLocked()) &&
					download.getPriority() == Download.PR_HIGH_PRIORITY &&
					plugin_config.getBooleanParameter("Switch Priority", false)){
	
				log.log( LoggerChannel.LT_INFORMATION, "Priorty ["+i+"]: switch to low priority" );
				
				download.setPriority(Download.PR_LOW_PRIORITY);
			}

			if (	download.getState() == Download.ST_ERROR &&
					download.getErrorStateDetails() != null && 
					download.getErrorStateDetails().equals("File Not Found")){
			
				try{
					log.log( LoggerChannel.LT_INFORMATION, "Remove ["+i+"]: file not found" );
					
					download.remove();
					
				}catch( DownloadRemovalVetoException e ){
					
					e.printStackTrace();
					
				}catch( DownloadException e ){
					
					e.printStackTrace();
				}
			}		
		}
	}
	
	protected class
	downloadData
	{
		protected long	last_auto_start_time;
		
		public long
		getLastAutoStartTime()
		{
			return( last_auto_start_time );
		}
		
		public void
		setLastAutoStartTime(
			long			time )
		{
			last_auto_start_time		= time;
		}
	}
}
