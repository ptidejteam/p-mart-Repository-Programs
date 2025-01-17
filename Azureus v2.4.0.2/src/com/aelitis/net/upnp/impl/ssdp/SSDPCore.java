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

import org.gudy.azureus2.core3.util.AEMonitor;


import com.aelitis.net.udp.mc.MCGroup;
import com.aelitis.net.udp.mc.MCGroupAdapter;
import com.aelitis.net.udp.mc.MCGroupFactory;
import com.aelitis.net.upnp.*;

/**
 * @author parg
 *
 */

public class 
SSDPCore 
	implements UPnPSSDP, MCGroupAdapter
{		
	private static final String	HTTP_VERSION	= "1.1";
	private static final String	NL				= "\r\n";
	

	private static Map			singletons	= new HashMap();
	private static AEMonitor	class_mon 	= new AEMonitor( "SSDPCore:class" );

	public static SSDPCore
	getSingleton(
		UPnPSSDPAdapter		adapter,
		String				group_address,
		int					group_port,
		int					control_port,
		String[]			selected_interfaces )
	
		throws UPnPException
	{
		try{
			class_mon.enter();
		
			String	key = group_address + ":" + group_port + ":" + control_port;
			
			SSDPCore	singleton = (SSDPCore)singletons.get( key );
			
			if ( singleton == null ){
				
				singleton = new SSDPCore( adapter, group_address, group_port, control_port, selected_interfaces );
				
				singletons.put( key, singleton );
			}
			
			return( singleton );
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	private MCGroup				mc_group;
	
	private UPnPSSDPAdapter		adapter;
	private String				group_address_str;
	private int					group_port;
	private int					control_port;

	private boolean		first_response			= true;
	
	private List			listeners	= new ArrayList();
	
	protected AEMonitor		this_mon	= new AEMonitor( "SSDP" );

	
	public
	SSDPCore(
		UPnPSSDPAdapter		_adapter,
		String				_group_address,
		int					_group_port,
		int					_control_port,
		String[]			_selected_interfaces )
	
		throws UPnPException
	{	
		adapter	= _adapter;

		group_address_str	= _group_address;
		group_port			= _group_port;
		control_port		= _control_port;
		
		try{
			mc_group = MCGroupFactory.getSingleton( this, _group_address, group_port, control_port, _selected_interfaces );
			
		}catch( Throwable e ){
						
			throw( new UPnPException( "Failed to initialise SSDP", e ));
		}
	}
	
	public void
	trace(
		String	str )
	{
		adapter.log( str );
	}
	
	public void
	log(
		Throwable	e )
	{
		adapter.log( e );
	}
	
	public void
	notify(
		String		NT,
		String		NTS )
	{
		/*
		NOTIFY * HTTP/1.1
		HOST: 239.255.255.250:1900
		CACHE-CONTROL: max-age=3600
		LOCATION: http://192.168.0.1:49152/gateway.xml
		NT: urn:schemas-upnp-org:service:WANIPConnection:1
		NTS: ssdp:byebye
		SERVER: Linux/2.4.17_mvl21-malta-mips_fp_le, UPnP/1.0, Intel SDK for UPnP devices /1.2
		USN: uuid:ab5d9077-0710-4373-a4ea-5192c8781666::urn:schemas-upnp-org:service:WANIPConnection:1
		*/
		
		String	str =
			"NOTIFY * HTTP/" + HTTP_VERSION + NL +  
			"HOST: " + group_address_str + ":" + group_port + NL +
			"CACHE-CONTROL: max-age=3600" + NL +
			"LOCATION: http://127.0.0.1:" + control_port + "/" + NL +
			"NT: " + NT + NL + 
			"NTS: " + NTS + NL + 
			"SERVER: Azureus (UPnP/1.0)" + NL +
			"USN: uuid:UUID-Azureus-1234::" + NT + NL + NL; 
		
		sendMC( str );
	}
	
	public void
	search(
		String	user_agent,
		String	ST )
	{
		String	str =
			"M-SEARCH * HTTP/" + HTTP_VERSION + NL +  
			"ST: " + ST + NL +
			"MX: 3" + NL +
			"MAN: \"ssdp:discover\"" + NL + 
			"HOST: " + group_address_str + ":" + group_port + NL +
			(user_agent==null?NL:("USER-AGENT: " + user_agent + NL + NL));
		
		sendMC( str );
	}
	
	protected void
	sendMC(
		String	str )
	{
		byte[]	data = str.getBytes();
		
		try{

			mc_group.sendToGroup( data );
			
		}catch( Throwable e ){
		}
	}
	

	
	public void
	received(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetSocketAddress	originator,
		byte[]				packet_data,
		int					length )
	{
		String	str = new String( packet_data, 0,length );
				
		if ( first_response ){
			
			first_response	= false;
			
			adapter.trace( "UPnP:SSDP: first response:\n" + str );
		}
		
				// example notify event
			/*
			NOTIFY * HTTP/1.1
			HOST: 239.255.255.250:1900
			CACHE-CONTROL: max-age=3600
			LOCATION: http://192.168.0.1:49152/gateway.xml
			NT: urn:schemas-upnp-org:service:WANIPConnection:1
			NTS: ssdp:byebye
			SERVER: Linux/2.4.17_mvl21-malta-mips_fp_le, UPnP/1.0, Intel SDK for UPnP devices /1.2
			USN: uuid:ab5d9077-0710-4373-a4ea-5192c8781666::urn:schemas-upnp-org:service:WANIPConnection:1
			*/
			
		// System.out.println( str );
		
		List	lines = new ArrayList();
		
		int	pos = 0;
		
		while(true){
			
			int	p1 = str.indexOf( NL, pos );
			
			String	line;
			
			if ( p1 == -1 ){
			
				line = str.substring(pos);
			}else{
				
				line = str.substring(pos,p1);
				
				pos	= p1+1;
			}
			
			lines.add( line.trim());
			
			if ( p1 == -1 ){
				
				break;
			}
		}
		
		if ( lines.size() == 0 ){
			
			adapter.trace( "SSDP::receive packet - 0 line reply" );
			
			return;
		}
		
		String	header = (String)lines.get(0);
		
			// Gudy's  Root: http://192.168.0.1:5678/igd.xml, uuid:upnp-InternetGatewayDevice-1_0-12345678900001::upnp:rootdevice, upnp:rootdevice
			// Parg's  Root: http://192.168.0.1:49152/gateway.xml, uuid:824ff22b-8c7d-41c5-a131-44f534e12555::upnp:rootdevice, upnp:rootdevice

		URL		location	= null;
		String	nt			= null;
		String	nts			= null;
		String	st			= null;
		String	al			= null;
		String	user_agent	= null;
		
		for (int i=1;i<lines.size();i++){
			
			String	line = (String)lines.get(i);
			
			int	c_pos = line.indexOf(":");
			
			if ( c_pos == -1 ){
				continue;
			}
			
			String	key	= line.substring( 0, c_pos ).trim().toUpperCase();
			String 	val = line.substring( c_pos+1 ).trim();
			
			if ( key.equals("LOCATION" )){
				
				try{
					location	= new URL( val );
					
				}catch( MalformedURLException e ){
					
					adapter.log( e );
				}			
			}else if ( key.equals( "NT" )){
				
				nt	= val;
				
			}else if ( key.equals( "NTS" )){
				
				nts	= val;
				
			}else if ( key.equals( "ST" )){
				
				st	= val;
				
			}else if ( key.equals( "AL" )){
				
				al	= val;
				
			}else if ( key.equals( "USER-AGENT" )){
				
				user_agent	= val;
			}
		}
			
		if ( header.startsWith("M-SEARCH")){

			if ( st != null ){
				
				/*
				HTTP/1.1 200 OK
				CACHE-CONTROL: max-age=600
				DATE: Tue, 20 Dec 2005 13:07:31 GMT
				EXT:
				LOCATION: http://192.168.1.1:2869/gatedesc.xml
				SERVER: Linux/2.4.17_mvl21-malta-mips_fp_le UPnP/1.0 
				ST: upnp:rootdevice
				USN: uuid:UUID-InternetGatewayDevice-1234::upnp:rootdevice
				*/
				
				String	response = informSearch( network_interface, local_address, originator.getAddress(), user_agent, st );
				
				if ( response != null ){
					
					String	data = 
						"HTTP/1.1 200 OK" + NL +
						"SERVER: Azureus (UPnP/1.0)" + NL +
						"CACHE-CONTROL: max-age=3600" + NL +
						"LOCATION: http://" + local_address.getHostAddress() + ":" + control_port + "/" + NL +
						"ST: " + st + NL + 
						"USN: uuid:UUID-Azureus-1234::" + st + NL + 
						"AL: " + response;
										
					byte[]	data_bytes = data.getBytes();
					
					try{
						mc_group.sendToMember( originator, data_bytes );
						
					}catch( Throwable e ){
						
						adapter.log(e);
					}	
				}
			}else{
				
				adapter.trace( "SSDP::receive M-SEARCH - bad header:" + header );
			}
		}else if ( header.startsWith( "NOTIFY" )){
			
			if ( location != null && nt != null && nts != null ){
			
				informNotify( network_interface, local_address, originator.getAddress(), location, nt, nts );
			}else{
				
				adapter.trace( "SSDP::receive NITOFY - bad header:" + header );
			}
		}else if ( header.startsWith( "HTTP") && header.indexOf( "200") != -1 ){
			
			if ( location != null && st != null ){
		
				informResult( network_interface, local_address, originator.getAddress(), location, st, al  );
				
			}else{
				
				adapter.trace( "SSDP::receive HTTP - bad header:" + header );
			}			
		}else{
			
			adapter.trace( "SSDP::receive packet - bad header:" + header );
		}
	}
	

	protected void
	informResult(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetAddress			originator,
		URL					location,
		String				st,
		String				al )
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				((UPnPSSDPListener)listeners.get(i)).receivedResult(network_interface,local_address,originator,location,st,al);
				
			}catch( Throwable e ){
				
				adapter.log(e);
			}
		}
	}
	
	protected void
	informNotify(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetAddress			originator,
		URL					location,
		String				nt,
		String				nts )
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				((UPnPSSDPListener)listeners.get(i)).receivedNotify(network_interface,local_address,originator,location,nt,nts);
				
			}catch( Throwable e ){
				
				adapter.log(e);
			}
		}
	}
	
	protected String
	informSearch(
		NetworkInterface	network_interface,
		InetAddress			local_address,
		InetAddress			originator,
		String				user_agent,
		String				st )
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				String	res = ((UPnPSSDPListener)listeners.get(i)).receivedSearch(network_interface,local_address,originator,user_agent,st );
				
				if ( res != null ){
					
					return( res );
				}
			}catch( Throwable e ){
				
				adapter.log(e);
			}
		}
		
		return( null );
	}
	
	public void
	addListener(
		UPnPSSDPListener	l )
	{
		listeners.add( l );
	}
	
	public void
	removeListener(
			UPnPSSDPListener	l )
	{
		listeners.remove(l);
	}
}
