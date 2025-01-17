/*
 * Created on 20-Dec-2005
 * Created by Paul Gardner
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

package com.aelitis.azureus.core.instancemanager.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.*;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.logging.LogEvent;
import org.gudy.azureus2.core3.logging.LogIDs;
import org.gudy.azureus2.core3.logging.Logger;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.AESemaphore;
import org.gudy.azureus2.core3.util.AEThread;
import org.gudy.azureus2.core3.util.BDecoder;
import org.gudy.azureus2.core3.util.BEncoder;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SHA1Simple;
import org.gudy.azureus2.core3.util.SimpleTimer;
import org.gudy.azureus2.core3.util.SystemTime;
import org.gudy.azureus2.core3.util.TimerEvent;
import org.gudy.azureus2.core3.util.TimerEventPerformer;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.pluginsimpl.local.download.DownloadManagerImpl;

import com.aelitis.azureus.core.AzureusCore;
import com.aelitis.azureus.core.AzureusCoreLifecycleAdapter;
import com.aelitis.azureus.core.instancemanager.AZInstance;
import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
import com.aelitis.azureus.core.instancemanager.AZInstanceManagerListener;
import com.aelitis.azureus.core.instancemanager.AZInstanceTracked;
import com.aelitis.net.udp.mc.MCGroup;
import com.aelitis.net.udp.mc.MCGroupAdapter;
import com.aelitis.net.udp.mc.MCGroupFactory;

public class 
AZInstanceManagerImpl 
	implements AZInstanceManager, MCGroupAdapter
{
	private static final LogIDs LOGID = LogIDs.NET;
	
	private String				MC_GROUP_ADDRESS 	= "239.255.067.250";	// 239.255.000.000-239.255.255.255 
	private int					MC_GROUP_PORT		= 16680;				//
	private int					MC_CONTROL_PORT		= 0;

	private static final int	MT_VERSION		= 1;
	
	private static final int	MT_ALIVE		= 1;
	private static final int	MT_BYE			= 2;
	private static final int	MT_REQUEST		= 3;
	private static final int	MT_REPLY		= 4;
	
	private static final int	MT_REQUEST_SEARCH	= 1;
	private static final int	MT_REQUEST_TRACK	= 2;
	
	private static final long	ALIVE_PERIOD	= 30*60*1000;
	
	private static AZInstanceManagerImpl	singleton;
	
	private List	listeners	= new ArrayList();
	
	private static AEMonitor	class_mon = new AEMonitor( "AZInstanceManager:class" );
	
	public static AZInstanceManager
	getSingleton(
		AzureusCore	core )
	{
		try{
			class_mon.enter();
			
			if ( singleton == null ){
				
				singleton = new AZInstanceManagerImpl( core );
			}
		}finally{
			
			class_mon.exit();
		}
		
		return( singleton );
	}
	
	private AzureusCore	core;
	private MCGroup	 	mc_group;
	private long		search_id_next;
	private List		requests = new ArrayList();
	
	private AZMyInstanceImpl		my_instance;
	private Map						other_instances	= new HashMap();
	
	private volatile Map			tcp_lan_to_ext	= new HashMap();
	private volatile Map			udp_lan_to_ext	= new HashMap();
	private volatile Map			tcp_ext_to_lan	= new HashMap();
	private volatile Map			udp_ext_to_lan	= new HashMap();
	
	private volatile Set			lan_addresses	= new HashSet();
	private volatile Set			ext_addresses	= new HashSet();
	
	private AESemaphore	initial_search_sem	= new AESemaphore( "AZInstanceManager:initialSearch" );
	
	private AEMonitor	this_mon = new AEMonitor( "AZInstanceManager" );

	protected
	AZInstanceManagerImpl(
		AzureusCore	_core )
	{
		core			= _core;
		
		my_instance	= new AZMyInstanceImpl( core, this );
		
		new AZPortClashHandler( this );
	}
	
	public void
	initialize()
	{		
		try{
			mc_group = 
				MCGroupFactory.getSingleton(
					this,
					MC_GROUP_ADDRESS,
					MC_GROUP_PORT,
					MC_CONTROL_PORT,
					null );
					
			core.addLifecycleListener(
				new AzureusCoreLifecycleAdapter()
				{
					public void
					stopping(
						AzureusCore		core )
					{
						sendByeBye();
					}
				});
			
			SimpleTimer.addPeriodicEvent(
				ALIVE_PERIOD,
				new TimerEventPerformer()
				{
					public void
					perform(
						TimerEvent	event )
					{
						checkTimeouts();
													
						sendAlive();				
					}
				});
		
		}catch( Throwable e ){
			
			initial_search_sem.releaseForever();
			
			Debug.printStackTrace(e);
		}
		
		new AEThread( "AZInstanceManager:initialSearch", true )
		{
			public void
			runSupport()
			{
				try{
					search();
					
						// pick up our own details as soon as we can
					
					addAddresses( my_instance );
					
				}finally{
					
					initial_search_sem.releaseForever();
				}
			}
		}.start();
	}
	
	public void
	trace(
		String	str )
	{
		if ( Logger.isEnabled()){
				
			Logger.log(new LogEvent( LOGID, str )); 
		}
	}
	
	public void
	log(
		Throwable e )
	{
		Debug.printStackTrace(e);
	}
	
	public boolean
	isInitialized()
	{
		return( initial_search_sem.isReleasedForever());
	}
	
	protected void
	sendAlive()
	{
		sendMessage( MT_ALIVE );
	}
	
	protected void
	sendByeBye()
	{
		sendMessage( MT_BYE );
	}
	
	protected void
	sendMessage(
		int		type )
	{
		sendMessage( type, null );
	}
	
	protected void
	sendMessage(
		int		type,
		Map		body )
	{
		sendMessage( type, body, null );
	}
	
	protected void
	sendMessage(
		int					type,
		Map					body,
		InetSocketAddress	member )
	{
		Map	map = new HashMap();
		
		map.put( "ver", new Long(MT_VERSION));
		map.put( "type", new Long(type));
		
		Map	originator = new HashMap();
		
		map.put( "orig", originator );
		
		my_instance.encode( originator );
		
		if ( body != null ){
			
			map.put( "body", body );
		}
		
		try{
			byte[]	data = BEncoder.encode( map );
			
			if ( member == null ){
				
				mc_group.sendToGroup( data );
				
			}else{
				
				mc_group.sendToMember( member, data );
			}
		}catch( Throwable e ){
			
		}
	}

	public void
	received(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetSocketAddress	originator,
		byte[]				data,
		int					length )
	{
		try{
			Map	map = BDecoder.decode(new BufferedInputStream(new ByteArrayInputStream( data, 0, length )));
			
			long	version = ((Long)map.get( "ver" )).longValue();
			long	type	= ((Long)map.get( "type" )).longValue();
			
			AZOtherInstanceImpl	instance = AZOtherInstanceImpl.decode( originator.getAddress(), (Map)map.get( "orig" ));
			
			if ( type == MT_ALIVE ){
				
				checkAdd( instance );
				
			}else if ( type == MT_BYE ){
				
				checkRemove( instance );
				
			}else{
				
				checkAdd( instance );
				
				Map	body = (Map)map.get( "body" );
				
				if ( type == MT_REQUEST ){
					
					String	originator_id	= instance.getID();
					
					if ( !originator_id.equals( my_instance.getID())){
						
						Map	reply = requestReceived( instance, body );
					
						if ( reply != null ){
						
							reply.put( "oid", originator_id.getBytes());
							reply.put( "rid", body.get( "rid" ));
							
							sendMessage( MT_REPLY, reply, originator );
						}
					}
				}else if ( 	type == MT_REPLY ){
					
					String	originator_id	= new String((byte[])body.get( "oid" ));
					
					if ( originator_id.equals( my_instance.getID())){
						
						long req_id = ((Long)body.get("rid")).longValue();
						
						try{
							this_mon.enter();
							
							for (int i=0;i<requests.size();i++){
								
								request	req = (request)requests.get(i);
								
								if ( req.getID() == req_id ){
									
									req.addReply( instance, body );
								}
							}
						}finally{
							
							this_mon.exit();
						}
					}
				}
			}
		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
		}
	}
	

	protected Map
	requestReceived(
		AZInstance		instance,
		Map				body )
	{
		// System.out.println( "received result: " + ST + "/" + AL );
		

		long	type = ((Long)body.get( "type")).longValue();
		
		if ( type == MT_REQUEST_SEARCH ){
			
			return( new HashMap());
			
		}else if ( type == MT_REQUEST_TRACK ){
							
			byte[]	hash = (byte[])body.get( "hash" );
			
			boolean	seed = ((Long)body.get( "seed" )).intValue() == 1;
			
			List	dms = core.getGlobalManager().getDownloadManagers();
			
			Iterator	it = dms.iterator();
			
			DownloadManager	matching_dm = null;
			
			try{
				while( it.hasNext()){
					
					DownloadManager	dm = (DownloadManager)it.next();
					
					TOTorrent	torrent = dm.getTorrent();
					
					if ( torrent == null ){
						
						continue;
					}
					
					byte[]	sha1_hash = (byte[])dm.getData( "AZInstanceManager::sha1_hash" );
					
					if ( sha1_hash == null ){			

						sha1_hash	= new SHA1Simple().calculateHash( torrent.getHash());
						
						dm.setData( "AZInstanceManager::sha1_hash", sha1_hash );
					}
					
					if ( Arrays.equals( hash, sha1_hash )){
						
						matching_dm	= dm;
						
						break;
					}
				}
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
			
			if ( matching_dm == null ){
				
				return( null );
			}
			
			int	dm_state = matching_dm.getState();
			
			if ( dm_state == DownloadManager.STATE_ERROR || dm_state == DownloadManager.STATE_STOPPED ){
				
				return( null );
			}
							
			try{		
				informTracked( 
					new trackedInstance( instance, DownloadManagerImpl.getDownloadStatic( matching_dm ), seed ));
					
			}catch( Throwable e ){
					
				Debug.printStackTrace(e);
			}
			
			Map	reply = new HashMap();
			
			reply.put( "seed", new Long( matching_dm.isDownloadComplete()?1:0));		
			
			return( reply );
			
		}else{
			
			return( null );
		}
	}
	

	protected AZOtherInstanceImpl
	checkAdd(
		AZOtherInstanceImpl	inst )
	{
		if ( inst.getID().equals( my_instance.getID())){
			
			return( inst );
		}
		
		boolean	added 	= false;
		boolean	changed	= false;
		
		try{
			this_mon.enter();
			
			AZOtherInstanceImpl	existing = (AZOtherInstanceImpl)other_instances.get( inst.getID());
			
			if ( existing == null ){
				
				added	= true;
			
				other_instances.put( inst.getID(), inst );
								
			}else{
								
				changed = existing.update( inst );

				inst	= existing;
			}
		}finally{
			
			this_mon.exit();
		}
		
		if ( added ){
			
			informAdded( inst );
			
		}else if ( changed ){
			
			informChanged( inst );
		}
		
		return( inst );
	}
	
	protected void
	checkRemove(
		AZOtherInstanceImpl	inst )
	{
		if ( inst.getID().equals( my_instance.getID())){
			
			return;
		}
		
		boolean	removed = false;
		
		try{
			this_mon.enter();
			
			removed = other_instances.remove( inst.getID()) != null;
			
		}finally{
			
			this_mon.exit();
		}
		
		if ( removed ){
			
			informRemoved( inst );
		}
	}
	
	public AZInstance
	getMyInstance()
	{
		return( my_instance );
	}
	
	protected void
	search()
	{
		sendRequest( MT_REQUEST_SEARCH );
	}
	
	public AZInstance[]
	getOtherInstances()
	{
		initial_search_sem.reserve();
		
		try{
			this_mon.enter();

			return((AZInstance[])other_instances.values().toArray( new AZInstance[other_instances.size()]));
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	addAddresses(
		AZInstance	inst )
	{
		InetAddress	internal_address 	= inst.getInternalAddress();
		InetAddress	external_address	= inst.getExternalAddress();
		int			tcp					= inst.getTrackerClientPort();
		int			udp					= inst.getDHTPort();
		
		modifyAddresses( internal_address, external_address, tcp, udp, true );
	}
	
	protected void
	removeAddresses(
		AZOtherInstanceImpl	inst )
	{
		List		internal_addresses 	= inst.getInternalAddresses();
		InetAddress	external_address	= inst.getExternalAddress();
		int			tcp					= inst.getTrackerClientPort();
		int			udp					= inst.getDHTPort();
		
		for (int i=0;i<internal_addresses.size();i++){
			
			modifyAddresses( (InetAddress)internal_addresses.get(i), external_address, tcp, udp, false );
		}
	}
	
	protected void
	modifyAddresses(
		InetAddress		internal_address,
		InetAddress		external_address,
		int				tcp,
		int				udp,
		boolean			add )	
	{
		if ( internal_address.isAnyLocalAddress()){
			
			try{
				internal_address = InetAddress.getLocalHost();
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
		
		try{
			this_mon.enter();
 
			InetSocketAddress	int_tcp = new InetSocketAddress(internal_address, tcp);
			InetSocketAddress	ext_tcp = new InetSocketAddress(external_address, tcp);
			InetSocketAddress	int_udp = new InetSocketAddress(internal_address, udp);
			InetSocketAddress	ext_udp = new InetSocketAddress(external_address, udp);
			
				// not the most efficient code in the world this... will need rev
			
			tcp_ext_to_lan = modifyAddress( tcp_ext_to_lan, ext_tcp, int_tcp, add );
			tcp_lan_to_ext = modifyAddress( tcp_lan_to_ext, int_tcp, ext_tcp, add );
			udp_ext_to_lan = modifyAddress( udp_ext_to_lan, ext_udp, int_udp, add );
			udp_lan_to_ext = modifyAddress( udp_lan_to_ext, int_udp, ext_udp, add );
	
			if ( !lan_addresses.contains( internal_address )){
				
				Set	new_lan_addresses = new HashSet( lan_addresses );
				
				new_lan_addresses.add( internal_address );
				
				lan_addresses	= new_lan_addresses;
			}
			
			if ( !ext_addresses.contains( external_address )){
				
				Set	new_ext_addresses = new HashSet( ext_addresses );
				
				new_ext_addresses.add( external_address );
				
				ext_addresses	= new_ext_addresses;
			}
		}finally{
			
			this_mon.exit();
		}
	}
		
	protected Map
	modifyAddress(
		Map					map,
		InetSocketAddress	key,
		InetSocketAddress	value,
		boolean				add )
	{
		// System.out.println( "ModAddress: " + key + " -> " + value + " - " + (add?"add":"remove"));
		
		InetSocketAddress	old_value = (InetSocketAddress)map.get(key);

		boolean	same = old_value != null && old_value.equals( value );
		
		Map	new_map = map;
		
		if ( add ){
			
			if ( !same ){
				
				new_map	= new HashMap( map );
	
				new_map.put( key, value );
			}
		}else{
			
			if ( same ){
				
				new_map	= new HashMap( map );
				
				new_map.remove( key );
			}
		}	
		
		return( new_map );
	}
	
	public InetSocketAddress
	getLANAddress(
		InetSocketAddress	external_address,
		boolean				is_tcp )
	{
		Map	map = is_tcp?tcp_ext_to_lan:udp_ext_to_lan;
		
		if ( map.size() == 0 ){
			
			return( null );
		}
		
		return((InetSocketAddress)map.get( external_address ));
	}
	
	public InetSocketAddress
	getExternalAddress(
		InetSocketAddress	lan_address,
		boolean				is_tcp )
	{
		Map	map = is_tcp?tcp_lan_to_ext:udp_lan_to_ext;
		
		if ( map.size() == 0 ){
			
			return( null );
		}
		
		return((InetSocketAddress)map.get( lan_address ));	
	}
	
	public boolean
	isLANAddress(
		InetAddress			address )
	{
		if ( address.isLoopbackAddress()){
			
			return( true );
		}
		
		if ( lan_addresses.contains( address )){
			
			return( true );
		}
		
		return( false );
	}
	
	public boolean
	isExternalAddress(
		InetAddress			address )
	{
		return( ext_addresses.contains( address ));
	}
	
	public AZInstanceTracked[]
	track(
		Download		download )
	{
		if ( mc_group == null || download.getTorrent() == null || getOtherInstances().length == 0 ){
			
			return( new AZInstanceTracked[0]);
		}
		
		Map	body = new HashMap();
		
		body.put( "hash", new SHA1Simple().calculateHash(download.getTorrent().getHash()));
		
		body.put( "seed", new Long( download.isComplete()?1:0 ));
		
		Map	replies = sendRequest( MT_REQUEST_TRACK, body ); 
				
		AZInstanceTracked[]	res = new AZInstanceTracked[replies.size()];
		
		Iterator	it = replies.entrySet().iterator();
		
		int	pos = 0;
		
		while( it.hasNext()){
			
			Map.Entry	entry = (Map.Entry)it.next();
			
			AZInstance	inst 	= (AZInstance)entry.getKey();
			Map			reply	= (Map)entry.getValue();
	
			boolean	seed = ((Long)reply.get( "seed" )).intValue() == 1;
	
			res[ pos++ ] = new trackedInstance( inst, download, seed );
		}
		
		return( res );
	}
	
	protected void
	checkTimeouts()
	{
		long	now = SystemTime.getCurrentTime();
	
		List	removed = new ArrayList();
		
		try{
			this_mon.enter();

			Iterator	it = other_instances.values().iterator();
			
			while( it.hasNext()){
				
				AZOtherInstanceImpl	inst = (AZOtherInstanceImpl)it.next();
	
				if ( now - inst.getAliveTime() > ALIVE_PERIOD * 2.5 ){
					
					removed.add( inst );
					
					it.remove();
				}
			}
		}finally{
			
			this_mon.exit();
		}
		
		for (int i=0;i<removed.size();i++){
			
			AZOtherInstanceImpl	inst = (AZOtherInstanceImpl)removed.get(i);
			
			informRemoved( inst );
		}
	}
	
	protected void
	informRemoved(
		AZOtherInstanceImpl	inst )
	{
		removeAddresses( inst );
		
		for (int i=0;i<listeners.size();i++){
			
			try{
				((AZInstanceManagerListener)listeners.get(i)).instanceLost( inst );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	protected void
	informAdded(
		AZInstance	inst )
	{
		addAddresses( inst );

		for (int i=0;i<listeners.size();i++){
			
			try{
				((AZInstanceManagerListener)listeners.get(i)).instanceFound( inst );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	protected void
	informChanged(
		AZInstance	inst )
	{
		addAddresses( inst );
		
		if ( inst == my_instance ){
			
			sendAlive();
		}
		
		for (int i=0;i<listeners.size();i++){
			
			try{
				((AZInstanceManagerListener)listeners.get(i)).instanceChanged( inst );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	protected void
	informTracked(
		AZInstanceTracked	inst )
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				((AZInstanceManagerListener)listeners.get(i)).instanceTracked( inst );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	protected Map
	sendRequest(
		int		type )
	{
		return( new request( type, new HashMap()).getReplies());
	}
	
	protected Map
	sendRequest(
		int		type,
		Map		body )
	{
		return( new request( type, body ).getReplies());
	}
	
	protected class
	request
	{
		private long	id;
		
		private Set	reply_instances	= new HashSet();
		
		private Map	replies			= new HashMap();
		
		protected
		request(
			int			type,
			Map			body  )
		{
			try{
				this_mon.enter();

				id	= search_id_next++;
						
				requests.add( this );
	
			}finally{
				
				this_mon.exit();
			}
			
			body.put( "type", new Long( type ));
			
			body.put( "rid", new Long( id ));
			
			sendMessage( MT_REQUEST, body );
		}
		
		protected long
		getID()
		{
			return( id );
		}
		
		protected void
		addReply(
			AZInstance	instance,
			Map			body )
		{
			try{
				this_mon.enter();
				
				if ( !reply_instances.contains( instance.getID())){
						
					reply_instances.add( instance.getID());
					
					replies.put( instance, body );
				}
						
			}finally{
				
				this_mon.exit();
			}
		}
		
		protected Map
		getReplies()
		{
			try{
				Thread.sleep( 2500 );
				
			}catch( Throwable e ){
				
			}
			
			try{
				this_mon.enter();

				requests.remove( this );
				
				return( replies );	
				
			}finally{
				
				this_mon.exit();
			}
		}
	}

	public void
	addListener(
		AZInstanceManagerListener	l )
	{
		listeners.add( l );
	}
	
	public void
	removeListener(
		AZInstanceManagerListener	l )
	{
		listeners.remove( l );
	}
	
	protected static class
	trackedInstance
		implements AZInstanceTracked
	{
		private AZInstance		instance;
		private Download		download;
		private boolean			seed;
		
		protected
		trackedInstance(
			AZInstance		_instance,
			Download		_download,
			boolean			_seed )
		{
			instance		= _instance;
			download		= _download;
			seed			= _seed;
		}
		public AZInstance
		getInstance()
		{
			return( instance );
		}
		
		public Download
		getDownload()
		{
			return( download );
		}
		
		public boolean
		isSeed()
		{
			return( seed );
		}
	}
}
