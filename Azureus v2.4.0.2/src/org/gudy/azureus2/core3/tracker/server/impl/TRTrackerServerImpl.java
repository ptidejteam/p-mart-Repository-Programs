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
import java.net.URL;
import java.net.URLDecoder;

import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.ipfilter.*;
import org.gudy.azureus2.core3.tracker.server.*;

public abstract class 
TRTrackerServerImpl 
	implements 	TRTrackerServer
{
	public static  int RETRY_MINIMUM_SECS			= 60;
	public static  int RETRY_MINIMUM_MILLIS			= RETRY_MINIMUM_SECS*1000;
	public static  int CLIENT_TIMEOUT_MULTIPLIER	= 3;
	
	public static int TIMEOUT_CHECK 				= RETRY_MINIMUM_MILLIS*CLIENT_TIMEOUT_MULTIPLIER;
	
	public static int		max_peers_to_send			= 0;
	public static boolean	send_peer_ids				= true;
	public static int		announce_cache_period		= TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PERIOD;
	public static int		scrape_cache_period			= TRTrackerServer.DEFAULT_SCRAPE_CACHE_PERIOD;
	public static int		announce_cache_threshold	= TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD;
	public static int		max_seed_retention			= 0;
	public static int		seed_limit					= 0;
	
	public static boolean	all_networks_permitted		= true;
	public static String[]	permitted_networks			= {};
	
		// torrent map is static across all protocol servers
	
	private static Map		torrent_map = new HashMap(); 
	
	protected AEMonitor class_mon 	= new AEMonitor( "TRTrackerServer:class" );


	static{

		COConfigurationManager.addListener(
			new COConfigurationListener()
			{
				public void
				configurationSaved()
				{
					readConfig();
				}
			});
		
		readConfig();
	}

	protected static void
	readConfig()
	{
		send_peer_ids = COConfigurationManager.getBooleanParameter( "Tracker Send Peer IDs", true );
				
		max_peers_to_send = COConfigurationManager.getIntParameter( "Tracker Max Peers Returned" );
				
		scrape_cache_period = COConfigurationManager.getIntParameter( "Tracker Scrape Cache", TRTrackerServer.DEFAULT_SCRAPE_CACHE_PERIOD );
				
		announce_cache_period = COConfigurationManager.getIntParameter( "Tracker Announce Cache", TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PERIOD );
						
		announce_cache_threshold = COConfigurationManager.getIntParameter( "Tracker Announce Cache Min Peers", TRTrackerServer.DEFAULT_ANNOUNCE_CACHE_PEER_THRESHOLD );

		max_seed_retention = COConfigurationManager.getIntParameter( "Tracker Max Seeds Retained", 0 );
		
		seed_limit = COConfigurationManager.getIntParameter( "Tracker Max Seeds", 0 );
		
		List	nets = new ArrayList();
		
		for (int i=0;i<AENetworkClassifier.AT_NETWORKS.length;i++){
			
			String	net = AENetworkClassifier.AT_NETWORKS[i];
			
			boolean	enabled = 
				COConfigurationManager.getBooleanParameter(
						"Tracker Network Selection Default." + net );
			
			if ( enabled ){
				
				nets.add( net );
			}
		}
		
		String[]	s_nets = new String[nets.size()];
		
		nets.toArray(s_nets);
		
		permitted_networks	= s_nets;
		
		all_networks_permitted = s_nets.length == AENetworkClassifier.AT_NETWORKS.length;
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
	
	protected static int
	getMaxSeedRetention()
	{
		return( max_seed_retention );
	}
	
	protected static int
	getSeedLimit()
	{
		return( seed_limit );
	}
	
	protected static boolean
	getAllNetworksSupported()
	{
		return( all_networks_permitted );
	}
	
	protected static String[]
	getPermittedNetworks()
	{
		return( permitted_networks );
	}
	
	protected IpFilter	ip_filter	= IpFilterManagerFactory.getSingleton().getIPFilter();
	
	private long		current_announce_retry_interval;
	private long		current_scrape_retry_interval;
	private long		current_total_clients;
	
	private int		current_min_poll_interval;
	
	private TRTrackerServerStatsImpl	stats = new TRTrackerServerStatsImpl();
		
	private String	name;
	private boolean	web_password_enabled;
	private boolean	web_password_https_only;
	
	private boolean	tracker_password_enabled;
	private String	password_user;
	private byte[]	password_pw;
	private boolean	compact_enabled;
	private boolean	key_enabled;
	

	protected Vector	listeners 			= new Vector();
	private List		auth_listeners		= new ArrayList();
	
	private Vector	request_listeners 	= new Vector();
	
	protected AEMonitor this_mon 	= new AEMonitor( "TRTrackerServer" );

	private COConfigurationListener		config_listener;
	private boolean						destroyed;
	
	public
	TRTrackerServerImpl(
		String		_name )
	{
		name		= _name==null?DEFAULT_NAME:_name;

		config_listener = 
			new COConfigurationListener()
			{
				public void
				configurationSaved()
				{
					readConfigSettings();
				}
			};
		
		COConfigurationManager.addListener( config_listener );

		readConfigSettings();
					
		current_min_poll_interval	= COConfigurationManager.getIntParameter("Tracker Poll Interval Min", DEFAULT_MIN_RETRY_DELAY );
		
		if ( current_min_poll_interval < RETRY_MINIMUM_SECS ){
			
			current_min_poll_interval = RETRY_MINIMUM_SECS;
		}
		
		current_announce_retry_interval = current_min_poll_interval;		

		int	scrape_percentage 		= COConfigurationManager.getIntParameter("Tracker Scrape Retry Percentage", DEFAULT_SCRAPE_RETRY_PERCENTAGE );		
	
		current_scrape_retry_interval	= (current_announce_retry_interval*scrape_percentage)/100;
		
		Thread timer_thread = 
			new AEThread("TrackerServer:timer.loop")
			{
				public void
				runSupport( )
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
		web_password_enabled 		= COConfigurationManager.getBooleanParameter("Tracker Password Enable Web");
		tracker_password_enabled 	= COConfigurationManager.getBooleanParameter("Tracker Password Enable Torrent");

		web_password_https_only		= COConfigurationManager.getBooleanParameter("Tracker Password Web HTTPS Only");
		
		if ( web_password_enabled || tracker_password_enabled ){
			
			password_user	= COConfigurationManager.getStringParameter("Tracker Username", "");
			password_pw		= COConfigurationManager.getByteParameter("Tracker Password", new byte[0]);
		}
		
		compact_enabled = COConfigurationManager.getBooleanParameter("Tracker Compact Enable" );
		
		key_enabled = COConfigurationManager.getBooleanParameter("Tracker Key Enable Server");
	}


	public boolean
	isWebPasswordEnabled()
	{
		return( web_password_enabled || auth_listeners.size() > 0 );
	}
	
	public boolean
	isTrackerPasswordEnabled()
	{
		return( tracker_password_enabled || auth_listeners.size() > 0 );
	}
	
	public boolean
	isWebPasswordHTTPSOnly()
	{
		return( web_password_https_only );
	}
	
	public boolean
	hasExternalAuthorisation()
	{
		return( auth_listeners.size() > 0 );
	}	
	
	public boolean
	hasInternalAuthorisation()
	{
		return( web_password_enabled || tracker_password_enabled );
	}
	
	public boolean
	performExternalAuthorisation(
		URL			resource,
		String		user,
		String		password )
	{
		for (int i=0;i<auth_listeners.size();i++){
			
			try{
				
				if ( ((TRTrackerServerAuthenticationListener)auth_listeners.get(i)).authenticate( resource, user, password )){
					
					return( true );
				}
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}
		
		return( false );
	}
	
	public byte[]
	performExternalAuthorisation(
		URL			resource,
		String		user )
	{
		for (int i=0;i<auth_listeners.size();i++){
			
			try{
				
				byte[] sha_pw =  ((TRTrackerServerAuthenticationListener)auth_listeners.get(i)).authenticate( resource, user );
					
				if ( sha_pw != null ){
					
					return( sha_pw );
				}
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}
		
		return( null );
	}
	
	public String
	getName()
	{
		return( name );
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
	
	public long
	getMinAnnounceRetryInterval()
	{
		return( current_min_poll_interval );
	}
	
	public long
	getAnnounceRetryInterval(
		TRTrackerServerTorrentImpl	torrent )
	{		
		long	clients = current_total_clients;
		
		if ( clients == 0 ){
			
			return( current_announce_retry_interval );
		}
		
		long	res = ( torrent.getPeerCount() * current_announce_retry_interval ) / clients;
		
		if ( res < current_min_poll_interval ){
			
			res = current_min_poll_interval;
		}
		
		return( res );
	}
	
	public long
	getScrapeRetryInterval(
		TRTrackerServerTorrentImpl	torrent )
	{		
		long	clients = current_total_clients;
		
		if ( torrent == null || clients == 0 ){
			
			return( current_scrape_retry_interval );
		}
		
		long	res = ( torrent.getPeerCount() * current_scrape_retry_interval ) / clients;
		
		if ( res < current_min_poll_interval ){
			
			res = current_min_poll_interval;
		}
		
		return( res );
	}
	
	public long
	getMinScrapeRetryInterval()
	{
		return( current_min_poll_interval );
	}
	
	public TRTrackerServerStats
	getStats()
	{
		return( stats );
	}
	
	public void
	updateStats(
		TRTrackerServerTorrentImpl	torrent,
		int							bytes_in,
		int							bytes_out )
	{
		try{
			class_mon.enter();
		
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
		}finally{
			
			class_mon.exit();
		}
	}
	
	protected void
	timerLoop()
	{
		long	time_to_go = TIMEOUT_CHECK;
		
		while( !destroyed ){
			
			try{
				Thread.sleep( RETRY_MINIMUM_MILLIS );
				
				time_to_go -= RETRY_MINIMUM_MILLIS;
				
				// recalc tracker interval every minute
				
				current_min_poll_interval 	= COConfigurationManager.getIntParameter("Tracker Poll Interval Min", DEFAULT_MIN_RETRY_DELAY );
				
				if ( current_min_poll_interval < RETRY_MINIMUM_SECS ){
					
					current_min_poll_interval = RETRY_MINIMUM_SECS;
				}
				
				int	min		= current_min_poll_interval;
				int	max 	= COConfigurationManager.getIntParameter("Tracker Poll Interval Max", DEFAULT_MAX_RETRY_DELAY );
				int	inc_by 	= COConfigurationManager.getIntParameter("Tracker Poll Inc By", DEFAULT_INC_BY );
				int	inc_per = COConfigurationManager.getIntParameter("Tracker Poll Inc Per", DEFAULT_INC_PER );
				
				int	scrape_percentage = COConfigurationManager.getIntParameter("Tracker Scrape Retry Percentage", DEFAULT_SCRAPE_RETRY_PERCENTAGE );
				
				int	retry = min;
				
				int	clients = 0;
				
				try{
					class_mon.enter();
					
					Iterator	it = torrent_map.values().iterator();
					
					while(it.hasNext()){
												
						TRTrackerServerTorrentImpl	t = (TRTrackerServerTorrentImpl)it.next();
						
						clients += t.getPeerCount();
					}
				}finally{
					
					class_mon.exit();
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
				
				current_total_clients	= clients;
				
				// timeout dead clients
				
				if ( time_to_go <= 0 ){
					
					time_to_go = TIMEOUT_CHECK;
					
					try{
						class_mon.enter();
						
						Iterator	it = torrent_map.values().iterator();
						
						while(it.hasNext()){
														
							TRTrackerServerTorrentImpl	t = (TRTrackerServerTorrentImpl)it.next();
							
							t.checkTimeouts();
						}
					}finally{
						
						class_mon.exit();
					}
				}
				
			}catch( InterruptedException e ){
				
				Debug.printStackTrace( e );
			}
			
		}
	}
	
	public TRTrackerServerTorrent
	permit(
		byte[]		_hash,
		boolean		_explicit )
	
		throws TRTrackerServerException
	{
		// System.out.println( "TRTrackerServerImpl::permit( " + _explicit + ")");
		
		HashWrapper	hash = new HashWrapper( _hash );
		
			// don't invoke listeners when synched, deadlock possible
		
		TRTrackerServerTorrentImpl	entry;
		
		try{
			class_mon.enter();
			
			entry = (TRTrackerServerTorrentImpl)torrent_map.get( hash );
			
		}finally{
			
			class_mon.exit();
		}
		
		if ( entry == null ){
			
			for (int i=0;i<listeners.size();i++){
				
				if ( !((TRTrackerServerListener)listeners.elementAt(i)).permitted( _hash, _explicit )){
					
					throw( new TRTrackerServerException( "operation denied"));			
				}
			}
		
			try{
				class_mon.enter();
			
					// double check in-case added in parallel
				
				entry = (TRTrackerServerTorrentImpl)torrent_map.get( hash );
				
				if ( entry == null ){
				
					entry = new TRTrackerServerTorrentImpl( hash );
				
					torrent_map.put( hash, entry );
				}
			}finally{
				
				class_mon.exit();
			}
		}
		
		return( entry );
	}
	
	public void
	deny(
		byte[]		_hash,
		boolean		_explicit )
	
		throws TRTrackerServerException
	{
		// System.out.println( "TRTrackerServerImpl::deny( " + _explicit + ")");
		
		HashWrapper	hash = new HashWrapper( _hash );
		
		for (int i=0;i<listeners.size();i++){
			
			if ( !((TRTrackerServerListener)listeners.elementAt(i)).denied( _hash, _explicit )){				
				
				throw( new TRTrackerServerException( "operation denied"));			
			}
		}

		try{
			class_mon.enter();
			
			TRTrackerServerTorrentImpl	entry = (TRTrackerServerTorrentImpl)torrent_map.get( hash );
	
			if ( entry != null ){
				
				entry.delete();
			}
		
			torrent_map.remove( hash );
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	public TRTrackerServerTorrentImpl
	getTorrent(
		byte[]		hash )
	{
		try{
			class_mon.enter();
		
			return((TRTrackerServerTorrentImpl)torrent_map.get(new HashWrapper(hash)));
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	public TRTrackerServerTorrentImpl[]
	getTorrents()
	{
		try{
			class_mon.enter();
		
			TRTrackerServerTorrentImpl[]	res = new TRTrackerServerTorrentImpl[torrent_map.size()];
			
			torrent_map.values().toArray( res );
			
			return( res );	
		}finally{
			
			class_mon.exit();
		}
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
	
	public void
	addListener(
		TRTrackerServerListener	l )
	{
		try{
			this_mon.enter();
		
			listeners.addElement( l );
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public void
	removeListener(
		TRTrackerServerListener	l )
	{
		try{
			this_mon.enter();
		
			listeners.removeElement(l);
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	
	public void
	addAuthenticationListener(
		TRTrackerServerAuthenticationListener	l )
	{
		auth_listeners.add( l );
	}
	
	public void
	removeAuthenticationListener(
		TRTrackerServerAuthenticationListener	l )
	{
		auth_listeners.remove(l);
	}
	
	public void
	preProcess(
		TRTrackerServerPeer			peer,
		TRTrackerServerTorrent		torrent,
		int							type,
		String						request,
		Map							response )
	
		throws TRTrackerServerException
	{
		if ( request_listeners.size() > 0 ){
			
				// if this is a scrape then we need to patch up stuff as it may be multi-scrape
			
			if ( type == TRTrackerServerRequest.RT_SCRAPE ){
				
				try{
					int	request_pos = 10;
					
					while( true ){
						
						int	p = request.indexOf( "info_hash=", request_pos );
						
						String	bit;
						
						if ( p == -1 ){
						
							if ( request_pos == 10 ){
								
								break;	// only one entry, nothing to do
							}
							
							bit = request.substring( request_pos );
							
						}else{
							
							bit = request.substring( request_pos, p );
						}
														
						int	pos = bit.indexOf('&');
						
						String	hash_str = pos==-1?bit:bit.substring(0,pos);
						
						hash_str = URLDecoder.decode( hash_str, Constants.BYTE_ENCODING );
						
						byte[]	hash = hash_str.getBytes(Constants.BYTE_ENCODING);
						
						if ( Arrays.equals( hash, torrent.getHash().getBytes())){
							
							request = "info_hash=" + bit;
							
							if ( request.endsWith("&")){
								
								request = request.substring(0,request.length()-1);
							}
							
							break;
						}
						
						if ( p == -1 ){
							
							break;
						}
						
						request_pos = p + 10;
					}
				}catch( Throwable e ){
						
					Debug.printStackTrace(e);
				}
			}
			
			TRTrackerServerRequestImpl	req = new TRTrackerServerRequestImpl( this, peer, torrent, type, request, response );
			
			for (int i=0;i<request_listeners.size();i++){
				
				((TRTrackerServerRequestListener)request_listeners.elementAt(i)).preProcess( req );
			}
		}
	}
	
	public void
	postProcess(
		TRTrackerServerPeerImpl		peer,
		TRTrackerServerTorrentImpl	torrent,
		int							type,
		String						request,
		Map							response )
	
		throws TRTrackerServerException
	{
		if ( request_listeners.size() > 0 ){
			
			TRTrackerServerRequestImpl	req = new TRTrackerServerRequestImpl( this, peer, torrent, type, request, response );
			
			for (int i=0;i<request_listeners.size();i++){
				
				((TRTrackerServerRequestListener)request_listeners.elementAt(i)).postProcess( req );
			}
		}
	}
		
	public void
	addRequestListener(
		TRTrackerServerRequestListener	l )
	{
		request_listeners.addElement( l );
	}
	
	public void
	removeRequestListener(
		TRTrackerServerRequestListener	l )
	{
		request_listeners.removeElement(l);
	}
	
	protected void
	destroy()
	{
		destroyed	= true;
		
		COConfigurationManager.removeListener( config_listener );
	}
}
