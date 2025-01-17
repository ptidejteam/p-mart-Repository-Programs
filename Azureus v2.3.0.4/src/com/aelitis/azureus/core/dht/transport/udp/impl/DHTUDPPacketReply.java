/*
 * Created on 21-Jan-2005
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.dht.transport.udp.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.gudy.azureus2.core3.util.Debug;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.udp.DHTTransportUDP;
import com.aelitis.azureus.core.dht.transport.udp.impl.packethandler.DHTUDPPacketNetworkHandler;
import com.aelitis.azureus.core.dht.vivaldi.maths.VivaldiPosition;
import com.aelitis.net.udp.PRUDPPacketReply;

/**
 * @author parg
 *
 */

public class 
DHTUDPPacketReply
	extends 	PRUDPPacketReply
	implements 	DHTUDPPacket
{
	public static final int	DHT_HEADER_SIZE	= 
		PRUDPPacketReply.PR_HEADER_SIZE +
		8 +		// con id
		1 +		// ver
		1 +		// net 
		4;		// instance
	
	
	private DHTTransportUDPImpl 	transport;
	
	private long	connection_id;
	private byte	protocol_version;
	private int		network;
	private int		target_instance_id;
	
	private long	skew;
	
	public static final int	VIVALDI_DATA_LENGTH		= VivaldiPosition.FLOAT_ARRAY_SIZE;
	public static final int	VIVALDI_DATA_LENGTH_V1	= 4;
	
	private float[]	vivaldi_data;
	
	public
	DHTUDPPacketReply(
		DHTTransportUDPImpl	_transport,
		int					_type,
		int					_trans_id,
		long				_conn_id,
		DHTTransportContact	_local_contact,
		DHTTransportContact	_remote_contact )
	{
		super( _type, _trans_id );
		
		transport	= _transport;
		
		connection_id	= _conn_id;
		
		protocol_version			= _remote_contact.getProtocolVersion();
		
			// the target might be at a higher protocol version that us, so trim back if necessary
			// as we obviously can't talk a higher version than what we are!
			// this *should* have already been done when we received the corresponding request
			// packet as it modified the originator version accordingly. However, do it here
			// just in case
	
		if ( protocol_version > _transport.getProtocolVersion()){
			
			Debug.out( "Trimming protocol version" );
			
			protocol_version = _transport.getProtocolVersion();
		}
		
		target_instance_id	= _local_contact.getInstanceID();
		
		skew	= _local_contact.getClockSkew();
	}
	
	protected
	DHTUDPPacketReply(
		DHTUDPPacketNetworkHandler		network_handler,
		DataInputStream					is,
		int								type,
		int								trans_id )
	
		throws IOException
	{
		super( type, trans_id );
		
		connection_id 	= is.readLong();
		
		protocol_version			= is.readByte();
					
		if ( protocol_version < DHTTransportUDP.PROTOCOL_VERSION_MIN ){
			
			throw( new IOException( "Invalid DHT protocol version, please update Azureus" ));
		}
		
		if ( protocol_version >= DHTTransportUDP.PROTOCOL_VERSION_NETWORKS ){
			
			network	= is.readInt();
		}

			// we can only get the correct transport after decoding the network...
		
		transport = network_handler.getTransport( this );

		target_instance_id	= is.readInt();
	}
	
	public DHTTransportUDPImpl
	getTransport()
	{
		return( transport );
	}
	
	protected int
	getTargetInstanceID()
	{
		return( target_instance_id );
	}
	
	public long
	getConnectionId()
	{
		return( connection_id );
	}
	
	protected long
	getClockSkew()
	{
		return( skew );
	}
	
	protected byte
	getProtocolVersion()
	{
		return( protocol_version );
	}
	
	public int
	getNetwork()
	{
		return( network );
	}
	
	public void
	setNetwork(
		int		_network )
	{
		network	= _network;
	}
	
	protected float[]
	getVivaldiData()
	{
		return( vivaldi_data );
	}
	
	protected void
	setVivaldiData(
		float[]	data )
	{
		vivaldi_data = data;
	}
	
	public void
	serialise(
		DataOutputStream	os )
	
		throws IOException
	{
		super.serialise(os);
	
			// add to this and you need to adjust HEADER_SIZE above
		
		os.writeLong( connection_id );
		
		os.writeByte( protocol_version );
		
		if ( protocol_version >= DHTTransportUDP.PROTOCOL_VERSION_NETWORKS ){
			
			os.writeInt( network );
		}
		
		os.writeInt( target_instance_id );
	}
	
	public String
	getString()
	{
		return( super.getString() + ",[con="+connection_id+"]");
	}
}