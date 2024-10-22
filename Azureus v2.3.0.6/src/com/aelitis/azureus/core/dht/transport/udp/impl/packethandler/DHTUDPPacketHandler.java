/*
 * Created on 12-Jun-2005
 * Created by Paul Gardner
 * Copyright (C) 2005 Aelitis, All Rights Reserved.
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
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;

import java.net.InetSocketAddress;

import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SystemTime;

import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketReply;
import com.aelitis.azureus.core.dht.transport.udp.impl.DHTUDPPacketRequest;
import com.aelitis.azureus.core.util.bloom.BloomFilter;
import com.aelitis.azureus.core.util.bloom.BloomFilterFactory;
import com.aelitis.net.udp.*;


public class 
DHTUDPPacketHandler 

{
	private int			network;
	
	private PRUDPPacketHandler		packet_handler;
	private DHTUDPRequestHandler	request_handler;
	
	private DHTUDPPacketHandlerStats	stats;
	
	private boolean						test_network_alive	= true;

	private int							BLOOM_FILTER_SIZE		= 5000;
	private static final int			BLOOM_ROTATION_PERIOD	= 2*60*1000; 
	private BloomFilter					bloom1;
	private BloomFilter					bloom2;
	private long						last_bloom_rotation_time;
	
	protected
	DHTUDPPacketHandler( 
		int						_network,
		PRUDPPacketHandler		_packet_handler,
		DHTUDPRequestHandler	_request_handler )
	{
		network			= _network;
		packet_handler	= _packet_handler;
		request_handler	= _request_handler;
		
		bloom1	= BloomFilterFactory.createAddOnly( BLOOM_FILTER_SIZE );
		bloom2	= BloomFilterFactory.createAddOnly( BLOOM_FILTER_SIZE );
		
		stats = new DHTUDPPacketHandlerStats( packet_handler );
	}
	
	public void
	testNetworkAlive(
		boolean		alive )
	{
		test_network_alive	= alive;
	}
	
	protected DHTUDPRequestHandler
	getRequestHandler()
	{
		return( request_handler );
	}
	
	public void
	sendAndReceive(
		DHTUDPPacketRequest					request,
		InetSocketAddress					destination_address,
		final DHTUDPPacketReceiver			receiver,
		long								timeout,
		boolean								low_priority )
	
		throws DHTUDPPacketHandlerException
	{
			// send and receive pair
		
		try{
			request.setNetwork( network );
			
			if ( test_network_alive ){
				
			    long diff = SystemTime.getCurrentTime() - last_bloom_rotation_time;
			    
			    if( diff < 0 || diff > BLOOM_ROTATION_PERIOD ) {
			    
			    	// System.out.println( "bloom rotate: entries = " + bloom1.getEntryCount() + "/" + bloom2.getEntryCount());
			    	
			    	bloom1 = bloom2;
			    	
			    	bloom2 = BloomFilterFactory.createAddOnly( BLOOM_FILTER_SIZE );
			        
			        last_bloom_rotation_time = SystemTime.getCurrentTime();
			    }

			    byte[]	address_bytes = destination_address.getAddress().getAddress();
			    
			    bloom1.add( address_bytes );
			    bloom2.add( address_bytes );
			    
				packet_handler.sendAndReceive( 
					request, 
					destination_address, 
					new PRUDPPacketReceiver()
					{
						public void
						packetReceived(
							PRUDPPacketHandlerRequest	request,
							PRUDPPacket					packet,
							InetSocketAddress			from_address )
						{
							DHTUDPPacketReply	reply = (DHTUDPPacketReply)packet;
							
							stats.packetReceived( reply.getSerialisedSize() );
							
							if ( reply.getNetwork() == network ){
								
								receiver.packetReceived(reply, from_address, request.getElapsedTime());
								
							}else{
								
								Debug.out( "Non-matching network reply received" );
								
								receiver.error( new DHTUDPPacketHandlerException( new Exception( "Non-matching network reply received" )));
							}
						}
			
						public void
						error(
							PRUDPPacketHandlerException	e )
						{
							receiver.error( new DHTUDPPacketHandlerException( e ));
						}
					}, 
					timeout, 
					low_priority );
			}else{
				
				receiver.error( new DHTUDPPacketHandlerException( new Exception( "Test network disabled" )));
			}
			
		}catch( PRUDPPacketHandlerException e ){
			
			throw( new DHTUDPPacketHandlerException(e ));
			
		}finally{
			
			stats.packetSent( request.getSerialisedSize() );
		}
	}
	
	public void
	send(
		DHTUDPPacketRequest			request,
		InetSocketAddress			destination_address )
	
		throws DHTUDPPacketHandlerException

	{
			// one way send (no matching reply expected )
		
		try{
			
			request.setNetwork( network );
			
			if ( test_network_alive ){
				
				packet_handler.send( request, destination_address );
			}
			
		}catch( PRUDPPacketHandlerException e ){
			
			throw( new DHTUDPPacketHandlerException( e ));
			
		}finally{
			
			stats.packetSent( request.getSerialisedSize() );
		}
	}
	
	public void
	send(
		DHTUDPPacketReply			reply,
		InetSocketAddress			destination_address )
	
		throws DHTUDPPacketHandlerException
	{
			// send reply to a request
		
		try{
			reply.setNetwork( network );
			
				// outgoing request
					
			if ( test_network_alive ){
				
				packet_handler.send( reply, destination_address );
			}
				
		}catch( PRUDPPacketHandlerException e ){
			
			throw( new DHTUDPPacketHandlerException( e ));
		
		}finally{
			
			stats.packetSent( reply.getSerialisedSize());
		}	
	}
	
	protected void
	receive(
		DHTUDPPacketRequest	request )
	{
			// incoming request
		
		if ( test_network_alive ){
		
				// an alien request is one that originates from a peer that we haven't recently
				// talked to
			
			boolean	alien = !bloom1.contains( request.getAddress().getAddress().getAddress());
			
			stats.packetReceived( request.getSerialisedSize());
		
			request_handler.process( request, alien );
		}
	}
	
	public void
	setDelays(
		int		send_delay,
		int		receive_delay,
		int		queued_request_timeout )
	{
			// TODO: hmm
		
		packet_handler.setDelays( send_delay, receive_delay, queued_request_timeout );
	}
	
	public DHTUDPPacketHandlerStats
	getStats()
	{
		return( stats );
	}
}
