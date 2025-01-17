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

import com.aelitis.azureus.core.dht.impl.DHTLog;


/**
 * @author parg
 *
 */

public class 
DHTUDPPacketData 
	extends DHTUDPPacketRequest
{
	protected static final byte		PT_READ_REQUEST		= 0x00;
	protected static final byte		PT_READ_REPLY		= 0x01;
	protected static final byte		PT_WRITE_REQUEST	= 0x02;
	protected static final byte		PT_WRITE_REPLY		= 0x03;
	
	private byte	packet_type;
	private byte[]	transfer_key;
	private byte[]	key;
	private byte[]	data;
	private int		start_position;
	private int		length;
	private int		total_length;
	
		// assume keys are 20 bytes + 1 len, data len is 2 bytes
	
	public static int	MAX_DATA_SIZE = DHTUDPPacket.PACKET_MAX_BYTES - DHTUDPPacketReply.DHT_HEADER_SIZE -
											1- 21 - 21 - 14;
	
	public
	DHTUDPPacketData(
		long							_connection_id,
		DHTTransportUDPContactImpl		_local_contact,
		DHTTransportUDPContactImpl		_remote_contact )
	{
		super( DHTUDPPacket.ACT_DATA, _connection_id, _local_contact, _remote_contact );
	}
	
	protected
	DHTUDPPacketData(
		DataInputStream		is,
		long				con_id,
		int					trans_id )
	
		throws IOException
	{
		super( is,  DHTUDPPacket.ACT_REQUEST_PING, con_id, trans_id );
		
		packet_type		= is.readByte();
		transfer_key	= DHTUDPUtils.deserialiseByteArray( is, 64 );
		key				= DHTUDPUtils.deserialiseByteArray( is, 64 );
		start_position	= is.readInt();
		length			= is.readInt();
		total_length	= is.readInt();
		data			= DHTUDPUtils.deserialiseByteArray( is, 65535 );
	}
	
	public void
	serialise(
		DataOutputStream	os )
	
		throws IOException
	{
		super.serialise(os);
		
		os.writeByte( packet_type );
		DHTUDPUtils.serialiseByteArray( os, transfer_key, 64 );
		DHTUDPUtils.serialiseByteArray( os, key, 64 );
		os.writeInt( start_position );
		os.writeInt( length );
		os.writeInt( total_length );
		
		if ( data.length > 0 ){
			
			DHTUDPUtils.serialiseByteArray( os, data, start_position, length, 65535 );
			
		}else{
			
			DHTUDPUtils.serialiseByteArray( os, data,  65535 );
		}
	}
	
	public void
	setDetails(
		byte		_packet_type,
		byte[]		_transfer_key,
		byte[]		_key,
		byte[]		_data,
		int			_start_pos,
		int			_length,
		int			_total_length )
	{
		packet_type			= _packet_type;
		transfer_key		= _transfer_key;
		key					= _key;
		data				= _data;
		start_position		= _start_pos;
		length				= _length;
		total_length		= _total_length;
	}
	
	public byte
	getPacketType()
	{
		return( packet_type );
	}
	
	public byte[]
	getTransferKey()
	{
		return( transfer_key );
	}
	
	public byte[]
	getRequestKey()
	{
		return( key );
	}
	
	public byte[]
	getData()
	{
		return( data );
	}
	
	public int
	getStartPosition()
	{
		return( start_position );
	}
	
	public int
	getLength()
	{
		return( length );
	}
	
	public int
	getTotalLength()
	{
		return( total_length );
	}
	
	public String
	getString()
	{
		return( super.getString() + "tk=" + DHTLog.getString2( transfer_key ) + ",rk=" + 
				DHTLog.getString2( key ) + ",data=" + data.length +
				",st=" + start_position + ",len=" + length + ",tot=" + total_length );
	}
}