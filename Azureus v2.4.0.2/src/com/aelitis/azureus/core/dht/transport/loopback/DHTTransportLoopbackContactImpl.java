/*
 * Created on 12-Jan-2005
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

package com.aelitis.azureus.core.dht.transport.loopback;

import java.io.*;
import java.net.InetSocketAddress;

import com.aelitis.azureus.core.dht.impl.DHTLog;
import com.aelitis.azureus.core.dht.transport.*;
import com.aelitis.azureus.core.dht.vivaldi.maths.VivaldiPosition;

/**
 * @author parg
 *
 */

public class 
DHTTransportLoopbackContactImpl
	implements DHTTransportContact
{
	private DHTTransportLoopbackImpl	transport;
	
	private byte[]		id;
	private int			random_id;
	
	protected
	DHTTransportLoopbackContactImpl(
		DHTTransportLoopbackImpl	_transport,
		byte[]						_id )
	{
		transport	= _transport;
		id			= _id;
	}
	
	public DHTTransport
	getTransport()
	{
		return( transport );
	}
	
	public int
	getInstanceID()
	{
		return( 0 );
	}
	
	public byte
	getProtocolVersion()
	{
		return( 0 );
	}
	public long
	getClockSkew()
	{
		return( 0 );
	}
	
	public int
	getRandomID()
	{
		return( random_id );
	}
	
	public void
	setRandomID(
		int	_random_id )
	{
		random_id	= _random_id;
	}
	
	public boolean
	isValid()
	{
		return( true );
	}
	
	public int
	getMaxFailForLiveCount()
	{
		return( 5 );
	}
	
	public int
	getMaxFailForUnknownCount()
	{
		return( 3 );
	}
	
	public String
	getName()
	{
		return( "" );
	}
	
	public InetSocketAddress
	getAddress()
	{
		return( null );
	}
	
	public boolean
	isAlive(
		long		timeout )
	{
		return( true );
	}

	public void
	sendPing(
		DHTTransportReplyHandler	handler )
	{
		transport.sendPing( this, handler );
	}
		
	public void
	sendKeyBlock(
		DHTTransportReplyHandler	handler, 
		byte[]						request,
		byte[]						signature )
	{
		transport.sendKeyBlock( this, handler, request, signature );
	}
	
	public void
	sendStats(
		DHTTransportReplyHandler	handler )
	{
		transport.sendStats( this, handler );
	}
	
	public void
	sendStore(
		DHTTransportReplyHandler	handler,
		byte[][]					keys,
		DHTTransportValue[][]		value_sets )
	{
		transport.sendStore( this, handler, keys, value_sets );
	}
	
	public void
	sendFindNode(
		DHTTransportReplyHandler	handler,
		byte[]						nid )
	{
		transport.sendFindNode( this, handler, nid );
	}
		
	public void
	sendFindValue(
		DHTTransportReplyHandler	handler,
		byte[]						key,
		int							max,
		byte						flags )
	{
		transport.sendFindValue( this, handler, key, max, flags );
	}
	
	public DHTTransportFullStats
	getStats()
	{
		return( null );
	}
	
	public byte[]
	getID()
	{
		return( id );
	}
	
	public void
	exportContact(
		DataOutputStream	os )
	
		throws IOException
	{
		transport.exportContact( this, os );
	}
	
	public void
	remove()
	{
		transport.removeContact( this );
	}
	
	public VivaldiPosition
	getVivaldiPosition()
	{
		return( null );
	}
	
	public String
	getString()
	{
		return( DHTLog.getString( this ));
	}
}
