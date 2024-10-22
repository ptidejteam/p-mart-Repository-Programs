/*
 * File    : TRTrackerClientUtils.java
 * Created : 29-Feb-2004
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

package org.gudy.azureus2.core3.tracker.util;

/**
 * @author parg
 *
 */

import java.util.*;
import java.net.*;
import java.io.*;

import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.tracker.client.impl.bt.TRTrackerBTAnnouncerImpl;
import org.gudy.azureus2.core3.tracker.host.TRHost;
import org.gudy.azureus2.core3.util.AENetworkClassifier;
import org.gudy.azureus2.core3.util.AEThread;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SystemTime;

import com.aelitis.azureus.core.util.CopyOnWriteList;


public class 
TRTrackerUtils
{
	// author of MakeTorrent has requested we blacklist his site
	// as people keep embedding it as a tracker in torrents

	private static String[]	BLACKLISTED_HOSTS	=  
		{ "krypt.dyndns.org" };

	private static int[]		BLACKLISTED_PORTS	= 
		{ 81 };

	private static String		tracker_ip;
	
	private static Map			override_map;
	
	private static String		bind_ip;
	
	private static String		ports_for_url;
	
	private static CopyOnWriteList		listeners = new CopyOnWriteList();
	
	private static Thread		listener_thread;
	
	static{
		COConfigurationManager.addAndFireParameterListeners(
			new String[]
			{	"Proxy.Data.Enable",
				"Proxy.Data.SOCKS.inform",
				"TCP.Announce.Port",
				"network.transport.encrypted.use.crypto.port",
				"network.transport.encrypted.require",
				"network.transport.encrypted.fallback.incoming",
				"TCP.Listen.Port",
				"UDP.Listen.Port" },
			new ParameterListener()
			{
				public void 
				parameterChanged(
					String parameterName )
				{
					boolean socks_peer_inform	= 	
						COConfigurationManager.getBooleanParameter("Proxy.Data.Enable", false)&&
						COConfigurationManager.getBooleanParameter("Proxy.Data.SOCKS.inform", true );
					
		  				// we currently don't support incoming connections when SOCKs proxying
			  		
			 		int	tcp_port_num;
			 		int	udp_port_num;
			 		  		
			  		if ( socks_peer_inform ){
			  			
			  			tcp_port_num	= 0;
			  			udp_port_num	= 0;
			  		}else{
			  		
			 			tcp_port_num	= COConfigurationManager.getIntParameter( "TCP.Listen.Port" );
			 			udp_port_num	= COConfigurationManager.getIntParameter( "UDP.Listen.Port" );		
			  		}
			  		
			  		String portOverride = COConfigurationManager.getStringParameter("TCP.Announce.Port","");
			  		
			  		if(! portOverride.equals("")) {
			  		  
			  			try{
			  				tcp_port_num = Integer.parseInt( portOverride );
			  				
			  			}catch( Throwable e ){
			  				
			  				Debug.printStackTrace(e);
			  			}
			  		}
			  		  		
			 		String port = "";
			 		
			 		boolean require_crypto = COConfigurationManager.getBooleanParameter( "network.transport.encrypted.require");
			 		
			   		if ( require_crypto ){
			  			
			  			port += "&requirecrypto=1";
			  			
			  		}else{
			  			
						port += "&supportcrypto=1"; 
			  		}
			 		  
			 		if ( 	require_crypto &&
			 				(!COConfigurationManager.getBooleanParameter( "network.transport.encrypted.fallback.incoming") ) &&
			 				COConfigurationManager.getBooleanParameter( "network.transport.encrypted.use.crypto.port" )){
			 			
			 			port += "&port=0&cryptoport=" + tcp_port_num;
			 			
			 		}else{
			 
			 			port += "&port=" + tcp_port_num;
			 		}
						
					port += "&azudp=" + udp_port_num;
			 		
			  		  	//  BitComet extension for no incoming connections
			  		
			  		if ( tcp_port_num == 0 ){
			  			
			  			port += "&hide=1";
			  		}	
			  
			  		if ( ports_for_url != null && !ports_for_url.equals( port )){
			  			
			  			synchronized( listeners ){
			  				
			  					// back off for a bit to prevent multiple config changes from causing
			  					// multiple firings
			  				
			  				if ( listener_thread == null ){
			  					
			  					listener_thread = 
			  						new AEThread( "TRTrackerUtils:listener", true )
			  						{
			  							public void
			  							runSupport()
			  							{
			  								try{
			  									Thread.sleep(30000);
			  									
			  								}catch( Throwable e ){
			  								}
			  								
			  								synchronized( listeners ){
			  										
			  									listener_thread = null;
			  								}
			  								
			  								for (Iterator it=listeners.iterator();it.hasNext();){
			  									
			  									try{
			  										((TRTrackerUtilsListener)it.next()).announceDetailsChanged();
			  										
			  									}catch( Throwable e ){
			  										
			  										Debug.printStackTrace( e );
			  									}
			  								}
			  							}
			  						};
			  					
			  					listener_thread.start();
			  				}
			  			}
			  		}
			  		
			  		ports_for_url = port;
				}
			});
	}
			           
	private static Map	az_trackers = COConfigurationManager.getMapParameter( "Tracker Client AZ Instances", new HashMap());
	

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

	static void
	readConfig()
	{
		tracker_ip 		= COConfigurationManager.getStringParameter("Tracker IP", "");
	
		String override_ips		= COConfigurationManager.getStringParameter("Override Ip", "");
		
		StringTokenizer	tok = new StringTokenizer( override_ips, ";" );
		
		Map	new_override_map = new HashMap();
		
		while( tok.hasMoreTokens()){
			
			String	ip = tok.nextToken().trim();
			
			if ( ip.length() > 0 ){
				
				new_override_map.put( AENetworkClassifier.categoriseAddress( ip ), ip );
			}
		}
		
		override_map	= new_override_map;
		
		bind_ip 		= COConfigurationManager.getStringParameter("Bind IP", "");
	}
	
	public static boolean
	isHosting(
		URL		url_in )
	{
		return( tracker_ip.length() > 0  &&
				url_in.getHost().equalsIgnoreCase( tracker_ip ));
	}
	
	public static String
	getTrackerIP()
	{
		return( tracker_ip );
	}
	
	public static boolean
	isTrackerEnabled()
	{
		return( getAnnounceURLs().length > 0 );
	}
	
	public static URL[][]
	getAnnounceURLs()
	{
		String	tracker_host = COConfigurationManager.getStringParameter( "Tracker IP", "" );

		List	urls = new ArrayList();
				
		if ( tracker_host.length() > 0 ){
			
			if ( COConfigurationManager.getBooleanParameter( "Tracker Port Enable", false )){
										
				int port = COConfigurationManager.getIntParameter("Tracker Port", TRHost.DEFAULT_PORT );
				
				try{
					List	l = new ArrayList();
					
					l.add( new URL( "http://" + tracker_host + ":" + port + "/announce" ));
					
					List	ports = stringToPorts( COConfigurationManager.getStringParameter("Tracker Port Backups" ));
					
					for (int i=0;i<ports.size();i++){
						
						l.add( new URL( "http://" + tracker_host + ":" + ((Integer)ports.get(i)).intValue() + "/announce" ));
					}

					urls.add( l );
					
				}catch( MalformedURLException e ){
					
					Debug.printStackTrace( e );
				}
			}
			
			if ( COConfigurationManager.getBooleanParameter( "Tracker Port SSL Enable", false )){
				
				int port = COConfigurationManager.getIntParameter("Tracker Port SSL", TRHost.DEFAULT_PORT_SSL );
				
				try{
					List	l = new ArrayList();
					
					l.add( new URL( "https://" + tracker_host + ":" + port + "/announce" ));
					
					List	ports = stringToPorts( COConfigurationManager.getStringParameter("Tracker Port SSL Backups" ));
					
					for (int i=0;i<ports.size();i++){
						
						l.add( new URL( "https://" + tracker_host + ":" + ((Integer)ports.get(i)).intValue() + "/announce" ));
					}

					urls.add( l );
					

				}catch( MalformedURLException e ){
				
					Debug.printStackTrace( e );
				}
			}
			
			if ( COConfigurationManager.getBooleanParameter( "Tracker Port UDP Enable" )){
				
				int port = COConfigurationManager.getIntParameter("Tracker Port", TRHost.DEFAULT_PORT );
				
				boolean	auth = COConfigurationManager.getBooleanParameter( "Tracker Password Enable Torrent" );
					
				try{
					List	l = new ArrayList();
					
					l.add( new URL( "udp://" + tracker_host + ":" + port + "/announce" +
										(auth?"?auth":"" )));
				
					urls.add( l );
					
				}catch( MalformedURLException e ){
				
					Debug.printStackTrace( e );
				}
			}
		}
		
		URL[][]	res = new URL[urls.size()][];
		
		for (int i=0;i<urls.size();i++){
			
			List	l = (List)urls.get(i);
			
			URL[]	u = new URL[l.size()];
			
			l.toArray( u );
			
			res[i] = u;
		}
				
		return( res );		
	}
	
	protected static List
	stringToPorts(
		String	str )
	{
		str = str.replace(',', ';' );
		
		StringTokenizer	tok = new StringTokenizer( str, ";" );
		
		List	res = new ArrayList();
		
		while( tok.hasMoreTokens()){
			
			try{
				res.add( new Integer( tok.nextToken().trim()));
				
			}catch( Throwable e ){
				
				Debug.out("Invalid port entry in '" + str + "'", e);
			}
		}
		
		return( res );
	}
	
	public static URL
	adjustURLForHosting(
		URL		url_in )
	{
		if ( isHosting( url_in )){
					
			String	url = url_in.getProtocol() + "://";
	
			if ( bind_ip.length() < 7 ){
					
				url += "127.0.0.1";
					
			}else{
					
				url += bind_ip;
			}		
			
			int	port = url_in.getPort();
			
			if ( port != -1 ){
				
				url += ":" + url_in.getPort();
			}
			
			url += url_in.getPath();
			
			String query = url_in.getQuery();
			
			if ( query != null ){
				
				url += "?" + query;
			}
							
			try{
				return( new URL( url ));
				
			}catch( MalformedURLException e ){
				
				Debug.printStackTrace( e );
			}
		}
		
		return( url_in );
	}
	
	public static String
	adjustHostFromHosting(
		String		host_in )
	{
		if ( tracker_ip.length() > 0 ){
				
			String	address_type = AENetworkClassifier.categoriseAddress( host_in );
			
			String	target_ip = (String)override_map.get( address_type );
			
			if ( target_ip == null ){
				
				target_ip	= tracker_ip;
			}
			
			if ( host_in.equals( "127.0.0.1")){
				
				//System.out.println( "adjustHostFromHosting: " + host_in + " -> " + tracker_ip );
				
				return( target_ip );
			}
			
			if ( host_in.equals( bind_ip )){
				
				//System.out.println( "adjustHostFromHosting: " + host_in +  " -> " + tracker_ip );

				return( target_ip );
			}
		}
		
		return( host_in );
	}
	
	public static boolean
	isLoopback(
		String	host )
	{
		return( host.equals( "127.0.0.1")  || host.equals( bind_ip ));
	}
	
	
	public static void
	checkForBlacklistedURLs(
		URL		url )
	
		throws IOException
	{
		for (int i=0;i<BLACKLISTED_HOSTS.length;i++){
 			
 			if ( 	url.getHost().equalsIgnoreCase( BLACKLISTED_HOSTS[i] ) &&
 					url.getPort() == BLACKLISTED_PORTS[i] ){
 		
 				throw( new IOException( "http://" + BLACKLISTED_HOSTS[i] +
 						":" + BLACKLISTED_PORTS[i] + "/ is not a tracker" ));
 			}
 		}
	}
	
	public static Map
	mergeResponseCache(
		Map		map1,
		Map		map2 )
	{
		return( TRTrackerBTAnnouncerImpl.mergeResponseCache( map1, map2 ));
	}
		
 	public static String
	getPortsForURL()
  	{

  		return( ports_for_url );
  	}
 	
 	public static boolean
 	isAZTracker(
 		URL		tracker_url )
 	{
 	   synchronized( az_trackers ){
 	    	
 	    	return( az_trackers.containsKey( tracker_url.getHost() + ":" + tracker_url.getPort()));
 	    }
 	}
 	
	public static void
 	setAZTracker(
 		URL		tracker_url,
 		boolean	az_tracker )
	{
		String	key = tracker_url.getHost() + ":" + tracker_url.getPort();
		
		synchronized( az_trackers ){
			
			boolean	changed = false;
			
			if ( az_trackers.get( key ) == null ){
			
				if ( az_tracker ){
					
					az_trackers.put( key, new Long( SystemTime.getCurrentTime()));
					
					changed	= true;
				}
			}else{
				
				if ( !az_tracker ){
					
					az_trackers.remove( key );
					
					changed = true;
				}
			}
			
			if ( changed ){
				
				COConfigurationManager.setParameter( "Tracker Client AZ Instances", az_trackers );
			}
		}
	}
	
	public static void
	addListener(
		TRTrackerUtilsListener	l )
	{
		listeners.add( l );
	}
	
	public static void
	removeListener(
		TRTrackerUtilsListener	l )
	{
		listeners.remove( l );
	}
}
