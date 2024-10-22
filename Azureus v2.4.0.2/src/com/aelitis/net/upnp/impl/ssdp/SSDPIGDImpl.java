/*
 * Created on 14-Jun-2004
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

package com.aelitis.net.upnp.impl.ssdp;

import java.net.*;
import java.util.*;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.net.upnp.*;
import com.aelitis.net.upnp.impl.*;

/**
 * @author parg
 *
 */

public class 
SSDPIGDImpl 
	implements SSDPIGD, UPnPSSDPListener
{
	private UPnPImpl		upnp;
	private SSDPCore		ssdp_core;
	
	private boolean			first_result			= true;
	private long			last_explicit_search	= 0;
			
	private List			listeners	= new ArrayList();
	
	protected AEMonitor		this_mon	= new AEMonitor( "SSDP" );

	
	public
	SSDPIGDImpl(
		UPnPImpl		_upnp,
		String[]		_selected_interfaces )
	
		throws UPnPException
	{	
		upnp	= _upnp;
		
		ssdp_core	= 
			SSDPCore.getSingleton( 
				upnp.getAdapter(),
				UPnPSSDP.SSDP_GROUP_ADDRESS,
				UPnPSSDP.SSDP_GROUP_PORT,
				UPnPSSDP.SSDP_CONTROL_PORT,
				_selected_interfaces );
		
		ssdp_core.addListener( this );
	}
	
	public void
	start()
	
		throws UPnPException
	{
		try{	
			upnp.getAdapter().createThread(
					"SSDP:queryLoop",
					new AERunnable()
					{
						public void
						runSupport()
						{
							queryLoop();
						}	
					});
			
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
			
			throw( new UPnPException( "Failed to initialise SSDP", e ));
		}
	}
	

	
	public void
	searchNow()
	{
		long	now = SystemTime.getCurrentTime();
		
		if ( now - last_explicit_search < 10000 ){
			
			return;
		}
		
		last_explicit_search	= now;
		
		search();
	}
	
	protected void
	queryLoop()
	{
		while(true){
			
			try{
				search();
				
				Thread.sleep( 60000 );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
			
		}
	}
	
	protected void
	search()
	{
		ssdp_core.search( null, "upnp:rootdevice" );
	}
	
	
	public void
	receivedResult(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetAddress			originator,
		URL					location,
		String				st,
		String				al )
	{
		try{
			this_mon.enter();

			if ( st.equalsIgnoreCase( "upnp:rootdevice" )){
				
				gotRoot( network_interface, local_address, location );
			}
		}finally{
			
			first_result	= false;
			
			this_mon.exit();
		}
	}
	
	public void
	receivedNotify(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetAddress			originator,
		URL					location,
		String				nt,
		String				nts )
	{
		try{
			this_mon.enter();

			if ( nt.indexOf( "upnp:rootdevice" ) != -1 ){
				
				if ( nts.indexOf("alive") != -1 ){
					
						// alive can be reported on any interface
					
					try{

						InetAddress	dev = InetAddress.getByName( location.getHost());
						
						byte[]	dev_bytes = dev.getAddress();
														
						boolean[]	dev_bits = bytesToBits( dev_bytes );
						
							// try and work out what bind address this location corresponds to
						
						NetworkInterface	best_ni 	= null;
						InetAddress			best_addr	= null;
						
						int	best_prefix	= 0;
						
						Enumeration network_interfaces = NetworkInterface.getNetworkInterfaces();
						
						while (network_interfaces.hasMoreElements()){
							
							NetworkInterface this_ni = (NetworkInterface)network_interfaces.nextElement();
													
							Enumeration ni_addresses = this_ni.getInetAddresses();
							
							while (ni_addresses.hasMoreElements()){
								
								InetAddress this_address = (InetAddress)ni_addresses.nextElement();
								
								byte[]	this_bytes = this_address.getAddress();
								
								if ( dev_bytes.length == this_bytes.length ){
									
									boolean[]	this_bits = bytesToBits( this_bytes );

									for (int i=0;i<this_bits.length;i++){
										
										if ( dev_bits[i] != this_bits[i] ){
											
											break;
										}
										
										if ( i > best_prefix ){
											
											best_prefix	= i;
											
											best_ni		= this_ni;
											best_addr	= this_address;
										}
									}
								}
							}
						}
						
						if ( best_ni != null ){
							
							if ( first_result ){
								
								upnp.log( location + " -> " + best_ni.getDisplayName() + "/" + best_addr + " (prefix=" + (best_prefix + 1 ) + ")");
							}
							
							gotRoot( best_ni, best_addr, location );
							
						}else{
							
							gotAlive( location );
						}
					}catch( Throwable e ){
						
						gotAlive( location );
					}
				}else if ( nts.indexOf( "byebye") != -1 ){
						
					lostRoot( local_address, location );
				}
			}
		}finally{

			first_result	= false;
			
			this_mon.exit();
		}
	}

	public String
	receivedSearch(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetAddress			originator,
		String				user_agent,
		String				ST )
	{
		// not interested, loopback or other search
		
		return( null );
	}
	
	
	protected boolean[]
	bytesToBits(
		byte[]	bytes )
	{
		boolean[]	res = new boolean[bytes.length*8];
		
		for (int i=0;i<bytes.length;i++){
			
			byte	b = bytes[i];
			
			for (int j=0;j<8;j++){
				
				res[i*8+j] = (b&(byte)(0x01<<(7-j))) != 0;
			}
		}
				
		return( res );
	}
	
	protected void
	gotRoot(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		URL					location )
	{
		for (int i=0;i<listeners.size();i++){
			
			((SSDPIGDListener)listeners.get(i)).rootDiscovered( network_interface, local_address, location );
		}
	}

	protected void
	gotAlive(
		URL		location )
	{
		for (int i=0;i<listeners.size();i++){
			
			((SSDPIGDListener)listeners.get(i)).rootAlive( location );
		}
	}
	protected void
	lostRoot(
		InetAddress	local_address,
		URL		location )
	{
		for (int i=0;i<listeners.size();i++){
			
			((SSDPIGDListener)listeners.get(i)).rootLost( local_address, location );
		}
	}
	
	public void
	addListener(
		SSDPIGDListener	l )
	{
		listeners.add( l );
	}
	
	public void
	removeListener(
		SSDPIGDListener	l )
	{
		listeners.remove(l);
	}
}
