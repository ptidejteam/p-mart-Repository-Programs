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

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.net.udp.PRUDPPacketReply;

/**
 * @author parg
 *
 */

public class 
DHTUDPPacketReply
	extends PRUDPPacketReply
{
	public static final int	DHT_HEADER_SIZE	= PRUDPPacketReply.PR_HEADER_SIZE + 13;
	
	
	private long	connection_id;
	private byte	version;
	private int		target_instance_id;
	
	private long	skew;
	
	public
	DHTUDPPacketReply(
		int					_type,
		int					_trans_id,
		long				_conn_id,
		DHTTransportContact	_local_contact,
		DHTTransportContact	_remote_contact )
	{
		super( _type, _trans_id );
		
		connection_id	= _conn_id;
		
		version			= _remote_contact.getProtocolVersion();
		
		target_instance_id	= _local_contact.getInstanceID();
		
		skew	= _local_contact.getClockSkew();
	}
	
	protected
	DHTUDPPacketReply(
		DataInputStream		_is,
		int					_type,
		int					_trans_id )
	
		throws IOException
	{
		super( _type, _trans_id );
		
		connection_id 	= _is.readLong();
		
		version			= _is.readByte();
			
		DHTUDPPacket.checkVersion( version );
	
		target_instance_id	= _is.readInt();
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
	getVersion()
	{
		return( version );
	}
	
	public void
	serialise(
		DataOutputStream	os )
	
		throws IOException
	{
		super.serialise(os);
	
			// add to this and you need to adjust HEADER_SIZE above
		
		os.writeLong( connection_id );
		
		os.writeByte( version );
		
		os.writeInt( target_instance_id );
	}
	
	public String
	getString()
	{
		return( super.getString() + ",[con="+connection_id+"]");
	}
}