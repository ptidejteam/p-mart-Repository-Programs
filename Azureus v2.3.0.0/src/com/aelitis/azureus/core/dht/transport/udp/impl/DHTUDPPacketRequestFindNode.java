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


/**
 * @author parg
 *
 */

public class 
DHTUDPPacketRequestFindNode 
	extends DHTUDPPacketRequest
{
	private byte[]		id;
	
	public
	DHTUDPPacketRequestFindNode(
		long							_connection_id,
		DHTTransportUDPContactImpl		_local_contact,
		DHTTransportUDPContactImpl		_remote_contact )
	{
		super( DHTUDPPacket.ACT_REQUEST_FIND_NODE, _connection_id, _local_contact, _remote_contact );
	}
	
	protected
	DHTUDPPacketRequestFindNode(
		DataInputStream		is,
		long				con_id,
		int					trans_id )
	
		throws IOException
	{
		super( is,  DHTUDPPacket.ACT_REQUEST_FIND_NODE, con_id, trans_id );
		
		id = DHTUDPUtils.deserialiseByteArray( is, 64 );
	}
	
	public void
	serialise(
		DataOutputStream	os )
	
		throws IOException
	{
		super.serialise(os);
		
		DHTUDPUtils.serialiseByteArray( os, id, 64 );
	}
	
	protected void
	setID(
		byte[]		_id )
	{
		id	= _id;
	}
	
	protected byte[]
	getID()
	{
		return( id );
	}
	
	public String
	getString()
	{
		return( super.getString());
	}
}