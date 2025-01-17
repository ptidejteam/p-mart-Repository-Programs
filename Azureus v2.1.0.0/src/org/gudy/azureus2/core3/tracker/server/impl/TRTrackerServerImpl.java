/*
 * File    : TRTrackerServerImpl.java
 * Created : 19-Jan-2004
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

package org.gudy.azureus2.core3.tracker.server.impl;

/**
 * @author parg
 *
 */


import java.util.*;

import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.ipfilter.*;
import org.gudy.azureus2.core3.tracker.server.*;

public abstract class 
TRTrackerServerImpl 
	implements 	TRTrackerServer
{
	protected static final int RETRY_MINIMUM_SECS			= 60;
	protected static final int RETRY_MINIMUM_MILLIS			= RETRY_MINIMUM_SECS*1000;
	protected static final int CLIENT_TIMEOUT_MULTIPLIER	= 3;
	
	protected static final int TIMEOUT_CHECK 				= RETRY_MINIMUM_MILLIS*CLIENT_TIMEOUT_MULTIPLIER;
	
	protected static int		max_peers_to_send			= 0;
	protected static boolean	send_peer_ids				= true;
	protected static int		announce_cache_period		= TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PERIOD;
	protected static int		scrape_cache_period			= TRTrackerServer.DEFAULT_SCRAPE_CACHE_PERIOD;
	protected static int		announce_cache_threshold	= TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD;
	
	static{
		final String	send_ids_param = "Tracker Send Peer IDs";
		
		send_peer_ids = COConfigurationManager.getBooleanParameter( send_ids_param, true );
		
		COConfigurationManager.addParameterListener(
				send_ids_param,
				new ParameterListener()
				{
					public void
					parameterChanged(
							String	value )
					{
						send_peer_ids = COConfigurationManager.getBooleanParameter( send_ids_param, true );
					}
				});
		
		final String	max_peers_param = "Tracker Max Peers Returned";
		
		max_peers_to_send = COConfigurationManager.getIntParameter( max_peers_param, 0 );
		
		COConfigurationManager.addParameterListener(
				max_peers_param,
				new ParameterListener()
				{
					public void
					parameterChanged(
						String	value )
					{
						max_peers_to_send = COConfigurationManager.getIntParameter( max_peers_param, 0 );
					}
				});
		
		final String	scrape_cache_param = "Tracker Scrape Cache";
		
		scrape_cache_period = COConfigurationManager.getIntParameter( scrape_cache_param, TRTrackerServer.DEFAULT_SCRAPE_CACHE_PERIOD );
		
		COConfigurationManager.addParameterListener(
				scrape_cache_param,
				new ParameterListener()
				{
					public void
					parameterChanged(
						String	value )
					{
						scrape_cache_period = COConfigurationManager.getIntParameter( scrape_cache_param, TRTrackerServer.DEFAULT_SCRAPE_CACHE_PERIOD );
					}
				});
		
		final String	announce_cache_param = "Tracker Announce Cache";
		
		announce_cache_period = COConfigurationManager.getIntParameter( announce_cache_param, TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PERIOD );
		
		COConfigurationManager.addParameterListener(
				announce_cache_param,
				new ParameterListener()
				{
					public void
					parameterChanged(
						String	value )
					{
						announce_cache_period = COConfigurationManager.getIntParameter( announce_cache_param, TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PERIOD );
					}
				});
		
		final String	announce_threshold_param = "Tracker Announce Cache Min Peers";
		
		announce_cache_threshold = COConfigurationManager.getIntParameter( announce_threshold_param, TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD );
		
		COConfigurationManager.addParameterListener(
				announce_threshold_param,
				new ParameterListener()
				{
					public void
					parameterChanged(
						String	value )
					{
						announce_cache_threshold = COConfigurationManager.getIntParameter( announce_threshold_param, TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD );
					}
				});
	}
	
	protected static boolean
	getSendPeerIds()
	{
		return( send_peer_ids );
	}
	
	protected static int
	getMaxPeersToSend()
	{
		return( max_peers_to_send );
	}
	
	protected static int
	getScrapeCachePeriod()
	{
		return( scrape_cache_period );
	}
	
	protected static int
	getAnnounceCachePeriod()
	{
		return( announce_cache_period );
	}
	
	protected static int
	getAnnounceCachePeerThreshold()
	{
		return( announce_cache_threshold );
	}
	
	protected IpFilter	ip_filter	= IpFilter.getInstance();
	
	protected Map		torrent_map = new HashMap(); 
	
	protected int		current_announce_retry_interval;
	protected int		current_scrape_retry_interval;
	
	protected TRTrackerServerStatsImpl	stats = new TRTrackerServerStatsImpl();
	
	protected boolean	web_password_enabled;
	protected boolean	tracker_password_enabled;
	protected String	password_user;
	protected byte[]	password_pw;
	protected boolean	compact_enabled;
	protected boolean	key_enabled;
	

	protected Vector	listeners 			= new Vector();
		
	
	protected
	TRTrackerServerImpl()
	{
		COConfigurationManager.addListener(
				new COConfigurationListener()
				{
					public void
					configurationSaved()
					{
						readConfigSettings();
					}
				});
				
		readConfigSettings();
					
		int	min 				= COConfigurationManager.getIntParameter("Tracker Poll Interval Min", DEFAULT_MIN_RETRY_DELAY );
		int	scrape_percentage 	= COConfigurationManager.getIntParameter("Tracker Scrape Retry Percentage", DEFAULT_SCRAPE_RETRY_PERCENTAGE );
		
		current_announce_retry_interval = min;
		
		current_scrape_retry_interval	= (current_announce_retry_interval*scrape_percentage)/100;
		
		Thread timer_thread = 
			new Thread("TrackerServer:timer.loop")
			{
				public void
				run( )
				{
					timerLoop();
				}
			};
		
		timer_thread.setDaemon( true );
		
		timer_thread.start();
	}
	
	protected void
	readConfigSettings()
	{		
		web_password_enabled 		= COConfigurationManager.getBooleanParameter("Tracker Password Enable Web", false);
		tracker_password_enabled 	= COConfigurationManager.getBooleanParameter("Tracker Password Enable Torrent", false);

		if ( web_password_enabled || tracker_password_enabled ){
			
			password_user	= COConfigurationManager.getStringParameter("Tracker Username", "");
			password_pw		= COConfigurationManager.getByteParameter("Tracker Password", new byte[0]);
		}
		
		compact_enabled = COConfigurationManager.getBooleanParameter("Tracker Compact Enable", true );
		
		key_enabled = COConfigurationManager.getBooleanParameter("Tracker Key Enable", true );
	}

	public boolean
	isWebPasswordEnabled()
	{
		return( web_password_enabled );
	}
	
	public boolean
	isTrackerPasswordEnabled()
	{
		return( tracker_password_enabled );
	}
	
	public boolean
	isCompactEnabled()
	{
		return( compact_enabled );
	}
	public boolean
	isKeyEnabled()
	{
		return( key_enabled );
	}
	
	public String
	getUsername()
	{
		return( password_user );
	}
	
	public byte[]
	getPassword()
	{
		return( password_pw );
	}
	
	public int
	getAnnounceRetryInterval()
	{		
		return( current_announce_retry_interval );
	}
	
	public int
	getScrapeRetryInterval()
	{		
		return( current_scrape_retry_interval );
	}
	
	public TRTrackerServerStats
	getStats()
	{
		return( stats );
	}
	
	public synchronized void
	updateStats(
		TRTrackerServerTorrentImpl	torrent,
		int							bytes_in,
		int							bytes_out )
	{
		stats.update( bytes_in, bytes_out );
		
		if ( torrent != null ){
			
			torrent.updateXferStats( bytes_in, bytes_out );
			
		}else{
			
			int	num = torrent_map.size();
			
			if ( num > 0 ){
			
					// full scrape or error - spread the reported bytes across the torrents
			
				int	ave_in	= bytes_in/num;
				int	ave_out	= bytes_out/num;
				
				int	rem_in 	= bytes_in-(ave_in*num);
				int rem_out	= bytes_out-(ave_out*num);
				
				Iterator	it = torrent_map.values().iterator();
			
				while(it.hasNext()){
								
					TRTrackerServerTorrentImpl	this_torrent = (TRTrackerServerTorrentImpl)it.next();
					
					if ( it.hasNext()){
						
						this_torrent.updateXferStats( ave_in, ave_out );
						
					}else{
						
						this_torrent.updateXferStats( ave_in+rem_in, ave_out+rem_out );
						
					}
				}
			}
		}
	}
	
	protected void
	timerLoop()
	{
		long	time_to_go = TIMEOUT_CHECK;
		
		while(true){
			
			try{
				Thread.sleep( RETRY_MINIMUM_MILLIS );
				
				time_to_go -= RETRY_MINIMUM_MILLIS;
				
				// recalc tracker interval every minute
				
				int	min 	= COConfigurationManager.getIntParameter("Tracker Poll Interval Min", DEFAULT_MIN_RETRY_DELAY );
				int	max 	= COConfigurationManager.getIntParameter("Tracker Poll Interval Max", DEFAULT_MAX_RETRY_DELAY );
				int	inc_by 	= COConfigurationManager.getIntParameter("Tracker Poll Inc By", DEFAULT_INC_BY );
				int	inc_per = COConfigurationManager.getIntParameter("Tracker Poll Inc Per", DEFAULT_INC_PER );
				
				int	scrape_percentage = COConfigurationManager.getIntParameter("Tracker Scrape Retry Percentage", DEFAULT_SCRAPE_RETRY_PERCENTAGE );
				
				int	retry = min;
				
				int	clients = 0;
				
				synchronized(this){
					
					Iterator	it = torrent_map.values().iterator();
					
					while(it.hasNext()){
						
						Map	temp = new HashMap();
						
						// this triggers timeouts...
						
						TRTrackerServerTorrentImpl	t = (TRTrackerServerTorrentImpl)it.next();
						
						clients += t.getPeers().length;
					}
				}
				
				if ( inc_by > 0 && inc_per > 0 ){
					
					retry += inc_by * (clients/inc_per);
				}
				
				if ( max > 0 && retry > max ){
					
					retry = max;
				}
				
				if ( retry < RETRY_MINIMUM_SECS ){
					
					retry = RETRY_MINIMUM_SECS;
				}
				
				current_announce_retry_interval = retry;
				
				current_scrape_retry_interval	= (current_announce_retry_interval*scrape_percentage)/100;
				
				// timeout dead clients
				
				if ( time_to_go <= 0 ){
					
					time_to_go = TIMEOUT_CHECK;
					
					synchronized(this){
						
						Iterator	it = torrent_map.values().iterator();
						
						while(it.hasNext()){
														
							TRTrackerServerTorrentImpl	t = (TRTrackerServerTorrentImpl)it.next();
							
							t.checkTimeouts();
						}
					}
				}
				
			}catch( InterruptedException e ){
				
				e.printStackTrace();
			}
			
		}
	}
	
	public synchronized void
	permit(
		byte[]		_hash,
		boolean		_explicit )
	
		throws TRTrackerServerException
	{
		// System.out.println( "TRTrackerServerImpl::permit( " + _explicit + ")");
		
		HashWrapper	hash = new HashWrapper( _hash );
		
		TRTrackerServerTorrentImpl	entry = (TRTrackerServerTorrentImpl)torrent_map.get( hash );
		
		if ( entry == null ){
			
			for (int i=0;i<listeners.size();i++){
				
				if ( !((TRTrackerServerListener)listeners.elementAt(i)).permitted( _hash, _explicit )){
					
					throw( new TRTrackerServerException( "operation denied"));			
				}
			}
			
			entry = new TRTrackerServerTorrentImpl( this, hash );
			
			torrent_map.put( hash, entry );
		}
	}
	
	public synchronized void
	deny(
		byte[]		_hash,
		boolean		_explicit )
	
		throws TRTrackerServerException
	{
		// System.out.println( "TRTrackerServerImpl::deny( " + _explicit + ")");
		
		HashWrapper	hash = new HashWrapper( _hash );
		
		torrent_map.remove( hash );

		for (int i=0;i<listeners.size();i++){
			
			if ( !((TRTrackerServerListener)listeners.elementAt(i)).denied( _hash, _explicit )){				
				
				throw( new TRTrackerServerException( "operation denied"));			
			}
		}
	}
	
	public TRTrackerServerTorrentImpl
	getTorrent(
		byte[]		hash )
	{
		return((TRTrackerServerTorrentImpl)torrent_map.get(new HashWrapper(hash)));
	}
	
	public synchronized TRTrackerServerTorrentImpl[]
	getTorrents()
	{
		TRTrackerServerTorrentImpl[]	res = new TRTrackerServerTorrentImpl[torrent_map.size()];
		
		torrent_map.values().toArray( res );
		
		return( res );	
	}
	
	public TRTrackerServerTorrentStats
	getStats(
		byte[]		hash )
	{
		TRTrackerServerTorrentImpl	torrent = getTorrent( hash );
		
		if ( torrent == null ){
			
			return( null );
		}
		
		return( torrent.getStats());
	}	
	
	public TRTrackerServerPeer[]
	getPeers(
		byte[]		hash )
	{
		TRTrackerServerTorrentImpl	torrent = getTorrent( hash );
		
		if ( torrent == null ){
			
			return( null );
		}
		
		return( torrent.getPeers());
	}
	
	public synchronized void
	addListener(
			TRTrackerServerListener	l )
	{
		listeners.addElement( l );
	}
	
	public synchronized void
	removeListener(
			TRTrackerServerListener	l )
	{
		listeners.removeElement(l);
	}
}
