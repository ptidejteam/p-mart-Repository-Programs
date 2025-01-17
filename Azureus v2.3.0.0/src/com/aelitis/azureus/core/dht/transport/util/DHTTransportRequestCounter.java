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

package com.aelitis.azureus.core.dht.transport.util;

import com.aelitis.azureus.core.dht.transport.DHTTransportContact;
import com.aelitis.azureus.core.dht.transport.DHTTransportFindValueReply;
import com.aelitis.azureus.core.dht.transport.DHTTransportFullStats;
import com.aelitis.azureus.core.dht.transport.DHTTransportRequestHandler;
import com.aelitis.azureus.core.dht.transport.DHTTransportValue;

/**
 * @author parg
 *
 */

public class 
DHTTransportRequestCounter
	implements DHTTransportRequestHandler
{
	private DHTTransportRequestHandler		delegate;
	private DHTTransportStatsImpl			stats;
	
	public
	DHTTransportRequestCounter(
		DHTTransportRequestHandler	_delegate,
		DHTTransportStatsImpl		_stats )
	{
		delegate	= _delegate;
		stats		= _stats;
	}
	
	public void
	pingRequest(
		DHTTransportContact contact )
	{
		stats.pingReceived();
		
		delegate.pingRequest( contact );
	}
	
	public DHTTransportFullStats
	statsRequest(
		DHTTransportContact contact )
	{
		stats.statsReceived();
		
		return( delegate.statsRequest( contact ));
	}
	
	public byte[]
	storeRequest(
		DHTTransportContact 	contact, 
		byte[][]				keys,
		DHTTransportValue[][]	value_sets )
	{
		stats.storeReceived();
		
		return( delegate.storeRequest( contact, keys, value_sets ));
	}
	
	public DHTTransportContact[]
	findNodeRequest(
		DHTTransportContact contact, 
		byte[]				id )
	{
		stats.findNodeReceived();
		
		return( delegate.findNodeRequest( contact, id ));
	}
	
	public DHTTransportFindValueReply
	findValueRequest(
		DHTTransportContact contact, 
		byte[]				key,
		int					max,
		byte				flags )
	{
		stats.findValueReceived();
		
		return( delegate.findValueRequest( contact, key, max, flags ));
	}
		
	public void
	contactImported(
		DHTTransportContact	contact )
	{
		delegate.contactImported( contact );
	}
}
