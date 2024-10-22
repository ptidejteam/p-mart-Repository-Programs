/*
 * Created on 15-Mar-2006
 * Created by Paul Gardner
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.dht.speed.impl;

import java.util.*;

import org.gudy.azureus2.core3.util.AESemaphore;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.plugins.PluginInterface;
import org.gudy.azureus2.plugins.utils.UTTimer;
import org.gudy.azureus2.plugins.utils.UTTimerEvent;
import org.gudy.azureus2.plugins.utils.UTTimerEventPerformer;

import com.aelitis.azureus.core.dht.DHT;
import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPosition;
import com.aelitis.azureus.core.dht.netcoords.DHTNetworkPositionManager;
import com.aelitis.azureus.core.dht.speed.DHTSpeedTester;
import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContact;
import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterContactListener;
import com.aelitis.azureus.core.dht.speed.DHTSpeedTesterListener;
import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportReplyHandlerAdapter;
import com.aelitis.azureus.core.util.CopyOnWriteList;
import com.aelitis.azureus.core.util.bloom.BloomFilter;
import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;

public class 
DHTSpeedTesterImpl 
	implements DHTSpeedTester
{
	private static final long PING_TIMEOUT	= 5000;
	
	private PluginInterface		plugin_interface;
	private DHT					dht;
	private int					contact_num;
	
	private BloomFilter			tried_bloom;
	
	private LinkedList			pending_contacts 	= new LinkedList();
	private List				active_pings		= new ArrayList();
	
	private List			new_listeners	= new ArrayList();
	private CopyOnWriteList	listeners 		= new CopyOnWriteList();
	
	public 
	DHTSpeedTesterImpl(
		DHT		_dht )
	{
		dht			= _dht;
		
		plugin_interface	= dht.getLogger().getPluginInterface();
		
		UTTimer	timer = plugin_interface.getUtilities().createTimer(
				"DHTSpeedTester:finder", true );
		
		timer.addPeriodicEvent( 
				5000,
				new UTTimerEventPerformer()
				{
					public void 
					perform(
						UTTimerEvent event) 
					{
						findContacts();	
					}
				});
		
		timer.addPeriodicEvent( 
				1000,
				new UTTimerEventPerformer()
				{
					int	tick_count;
					
					public void 
					perform(
						UTTimerEvent event) 
					{
						try{
							pingContacts( tick_count );
							
						}finally{
							
							tick_count++;
						}
					}
				});
	}
	
	public int
	getContactNumber()
	{
		return( contact_num );
	}
	
	public void
	setContactNumber(
		int		number )
	{
		contact_num	= number;
	}
	
	protected void
	findContacts()
	{
		DHTTransportContact[]	reachables = dht.getTransport().getReachableContacts();
		
		for (int i=0;i<reachables.length;i++){
			
			DHTTransportContact	contact = reachables[i];
			
			byte[]	address = contact.getAddress().getAddress().getAddress();
			
			if ( tried_bloom == null || tried_bloom.getEntryCount() > 500 ){
				
				tried_bloom = BloomFilterFactory.createAddOnly( 4096 );
			}
			
			if ( !tried_bloom.contains( address )){
				
				tried_bloom.add( address );
				
				synchronized( pending_contacts ){
					
					potentialPing	ping = 
						new potentialPing( 
								contact,
								DHTNetworkPositionManager.estimateRTT( contact.getNetworkPositions(), dht.getTransport().getLocalContact().getNetworkPositions()));
									
					pending_contacts.add( 0, ping );
					
					if ( pending_contacts.size() > 60 ){
						
						pending_contacts.removeLast();
					}
				}
			}
		}
	}
	
	protected void
	pingContacts(
		int		tick_count )
	{
		List	copy = null;
		
		synchronized( new_listeners ){
		
			if ( new_listeners.size() > 0 ){
				
				copy = new ArrayList( new_listeners );
				
				new_listeners.clear();
			}
		}
		
		if ( copy != null ){
			
			for (int i=0;i<copy.size();i++){
			
				DHTSpeedTesterListener	listener = (DHTSpeedTesterListener)copy.get(i);
				
				listeners.add( listener );
				
				for ( int j=0;j<active_pings.size();j++){
					
					activePing	ping = (activePing)active_pings.get(j);
					
					if ( ping.isInformedAlive()){
						
						try{
						
							listener.contactAdded( ping );
							
						}catch( Throwable e ){
							
							Debug.printStackTrace(e);
						}
					}
				}
			}
		}
		
		Iterator	pit = active_pings.iterator();
		
		pingInstanceSet	ping_set = new pingInstanceSet( true );
		
		while( pit.hasNext()){
			
			activePing ping = (activePing)pit.next();
			
			if ( ping.update( ping_set, tick_count )){
				
				if ( !ping.isInformedAlive()){
					
					ping.setInformedAlive();
					
					Iterator	it = listeners.iterator();
					
					while( it.hasNext()){
						
						try{
							((DHTSpeedTesterListener)it.next()).contactAdded( ping );
							
						}catch( Throwable e ){
							
							Debug.printStackTrace(e);
						}
					}
				}
			}
			
			if ( ping.isDead()){
				
				pit.remove();
				
				ping.informDead();
			}
		}
		
		ping_set.setFull();
		
			// we try and keep three active pings running so we can spot overall trends in ping time
			// each active ping is selected from the best rtt from the current 3 best three rtt estimates
		
		int	num_active = active_pings.size();
		
		if ( num_active < contact_num ){

			Set	pc = new TreeSet(
					new Comparator()
					{
						public int
						compare(
							Object	o1,
							Object	o2 )
						{
							potentialPing	p1 = (potentialPing)o1;
							potentialPing	p2 = (potentialPing)o2;
							
							return( p1.getRTT() - p2.getRTT());
						}				
					});
			
			synchronized( pending_contacts ){
				
				pc.addAll( pending_contacts );
			}
			
			Iterator	it = pc.iterator();
			
			if ( pc.size() >= 3){
				
					// find best candidates
				
				List	pps = new ArrayList();
				
				for (int i=0;i<3;i++){
					
					potentialPing	pp = (potentialPing)it.next();
					
					pps.add( pp );
					
					it.remove();
					
					synchronized( pending_contacts ){
						
						pending_contacts.remove( pp );
					}
				}
				
				active_pings.add( new activePing( pps ));
			}
		}else if ( num_active > contact_num ){
			
			for (int i=0;i<num_active-contact_num;i++){
				
				((activePing)active_pings.get(i)).destroy();
			}
		}
	}
	
	protected void
	informResults(
		DHTSpeedTesterContact[]		contacts,
		int[]						rtts )
	{
		Iterator	it = listeners.iterator();
		
		while( it.hasNext()){
			
			try{
				((DHTSpeedTesterListener)it.next()).resultGroup( contacts, rtts );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
		}
	}
	
	public void
	addListener(
		DHTSpeedTesterListener	listener )
	{
		synchronized( new_listeners ){
			
			new_listeners.add( listener );
		}
	}
	
	public void
	removeListener(
		DHTSpeedTesterListener	listener )
	{
		listeners.remove( listener );
	}
	
	protected class
	potentialPing
	{
		private DHTTransportContact		contact;
		private int						rtt;
		
		protected
		potentialPing(
			DHTTransportContact	_contact,
			float				_rtt )
		{
			contact	= _contact;
			rtt		= (int)(Float.isNaN(rtt)?1000.0:_rtt);
		}
		
		protected DHTTransportContact
		getContact()
		{
			return( contact );
		}
		
		protected int
		getRTT()
		{
			return( rtt );
		}
	}
	
	protected class
	activePing
		implements DHTSpeedTesterContact
	{
		private boolean		running;
		private boolean		dead;
		private boolean		informed_alive;
				
		private int					outstanding;
		private int					best_ping		= Integer.MAX_VALUE;
		private DHTTransportContact	best_pingee;
		private int					consec_fails;
		private int					total_ok;
		private int					total_fails;
		
		private int					period	= 5;
		private CopyOnWriteList		listeners = new CopyOnWriteList();
		
		protected
		activePing(
			List	candidates )
		{
			String	str  = "";
			
			pingInstanceSet	ping_set = new pingInstanceSet( false );
			
			synchronized( this ){
				
				for (int i=0;i<candidates.size();i++){
					
					potentialPing	pp = (potentialPing)candidates.get(i);
				
					str += (i==0?"":",") + pp.getContact().getString() + "/" + pp.getRTT();
					
					ping( ping_set, pp.getContact());
				}
			}
		}
		
		protected boolean
		update(
			pingInstanceSet		ping_set,
			int					tick_count )
		{
			synchronized( this ){
				
				if ( dead || !running || outstanding > 0 ){
					
					return( false );
				}
				
				if ( best_pingee == null ){
					
					dead	= true;
					
					return( false );
				}					
			}
			
			if ( tick_count % period == 0 ){
				
				ping( ping_set, best_pingee );
			}
			
			return( true );
		}
		
		protected void
		ping(
			pingInstanceSet		ping_set,
			DHTTransportContact	contact )
		{
			final pingInstance	pi = new pingInstance( ping_set );
			
			outstanding++;

			try{
				contact.sendImmediatePing(
					new DHTTransportReplyHandlerAdapter()
					{
						public void
						pingReply(
							DHTTransportContact contact )
						{
							int	rtt = getElapsed();
							
							try{
								synchronized( activePing.this ){
																
									outstanding--;
									
									if ( !running ){									
										
										if ( rtt < best_ping ){
											
											best_pingee = contact;
											best_ping	= rtt;
										}
									
										if ( outstanding == 0 ){
											
											running = true;
										}
									}else{
										
										total_ok++;
										
										consec_fails	= 0;
									}
								}
								
								Iterator	it = listeners.iterator();
								
								while( it.hasNext()){
									
									try{
										((DHTSpeedTesterContactListener)it.next()).ping( activePing.this, getElapsed());
										
									}catch( Throwable e ){
										
										Debug.printStackTrace(e);
									}
								}
							}finally{
								
								pi.setResult( activePing.this, rtt );
							}
							// System.out.println( "    " + contact.getString() + ": " + getElapsed() + ", " + contact.getVivaldiPosition().estimateRTT( dht.getTransport().getLocalContact().getVivaldiPosition().getCoordinates()));					
						}
						
						public void
						failed(
							DHTTransportContact 	contact,
							Throwable				error )
						{
							try{
								synchronized( activePing.this ){
									
									outstanding--;
									
									if ( !running ){
		
										if ( outstanding == 0 ){
											
											running = true;
										}
									}else{
										
										consec_fails++;
										total_fails++;
										
										if ( consec_fails == 3 ){
											
											dead	= true;
											
										}else if ( 	total_ok > 10 && total_fails > 0 && 
													total_ok / total_fails < 1 ){
											
												// failing too often
											
											dead	= true;
											
										}else if ( total_ok > 100 ){
											
											total_ok	= 0;
											total_fails	= 0;
										}
									}
								}
								
								if ( !dead ){
									
									Iterator	it = listeners.iterator();
									
									while( it.hasNext()){
										
										try{
											((DHTSpeedTesterContactListener)it.next()).pingFailed( activePing.this );
											
										}catch( Throwable e ){
											
											Debug.printStackTrace(e);
										}
									}
								}
								// System.out.println( "    " + contact.getString() + ": failed" );
							}finally{
								
								pi.setResult( activePing.this, -1 );
							}
						}
					},
				PING_TIMEOUT );
				
			}catch( Throwable e ){
			
				pi.setResult( this, -1 );
				
				dead	= true;
				
				outstanding--;
				
				Debug.printStackTrace(e);
			}
		}
		
		public void
		destroy()
		{
			dead = true;
		}
		
		protected boolean
		isDead()
		{
			return( dead );
		}
		
		protected boolean
		isInformedAlive()
		{
			return( informed_alive );
		}
		
		protected void
		setInformedAlive()
		{
			informed_alive	= true;
		}
		
		protected void
		informDead()
		{
			if ( informed_alive ){
				
				Iterator	it = listeners.iterator();
				
				while( it.hasNext()){
					
					try{
						((DHTSpeedTesterContactListener)it.next()).contactDied( this );
						
					}catch( Throwable e ){
						
						Debug.printStackTrace(e);
					}
				}
			}
		}
		
		public DHTTransportContact
		getContact()
		{
			return( best_pingee );
		}
		
		public int
		getPingPeriod()
		{
			return( period );
		}
		
		public void
		setPingPeriod(
			int		_period )
		{
			period	= _period;
		}
		
		public void
		addListener(
			DHTSpeedTesterContactListener	listener )
		{
			listeners.add( listener );
		}
		
		public void
		removeListener(
			DHTSpeedTesterContactListener	listener )
		{
			listeners.remove( listener );
		}
	}
	
	protected class
	pingInstance
	{
		private activePing			contact;
		private pingInstanceSet		set;
		private int					result;
		
		protected
		pingInstance(
			pingInstanceSet		_set )
		{
			set	= _set;
			
			set.add( this );
		}
		
		protected activePing
		getContact()
		{
			return( contact );
		}
		
		protected int
		getResult()
		{
			return( result );
		}
		
		protected void
		setResult(
			activePing	_contact,
			int			_result )
		{
			contact	= _contact;
			result	= _result;
			
			set.complete( this );
		}
	}
	
	protected class
	pingInstanceSet
	{
		private boolean		active;
		private int			instances;
		private boolean		full;
		
		List	results = new ArrayList();
		
		protected 
		pingInstanceSet(
			boolean	_active )
		{
			active	= _active;
		}
		
		protected void
		add(
			pingInstance	instance )
		{
			synchronized( this ){
				
				instances++;
			}
		}
		
		protected void
		setFull()
		{
			synchronized( this ){

				full	= true;
				
				if ( results.size() == instances ){
					
					sendResult();
				}
			}
		}
		
		protected void
		complete(
			pingInstance	instance )
		{
			synchronized( this ){
							
				results.add( instance );
				
				if ( results.size() == instances && full ){
					
					sendResult();
				}
			}
		}
		
		protected void
		sendResult()
		{
			if ( active && results.size() > 0 ){
				
				DHTSpeedTesterContact[]	contacts 	= new DHTSpeedTesterContact[results.size()];
				int[]					rtts		= new int[contacts.length];
				
				for (int i=0;i<contacts.length;i++){
					
					pingInstance	pi = (pingInstance)results.get(i);
					
					contacts[i] = pi.getContact();
					rtts[i]		= pi.getResult();
				}
				
				DHTSpeedTesterImpl.this.informResults( contacts, rtts );
			}
		}
	}
}
