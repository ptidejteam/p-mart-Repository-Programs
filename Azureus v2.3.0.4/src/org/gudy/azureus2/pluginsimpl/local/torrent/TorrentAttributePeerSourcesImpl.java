/*
 * Created on 23-Jun-2004
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

package org.gudy.azureus2.pluginsimpl.local.torrent;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.plugins.torrent.*;

import org.gudy.azureus2.core3.peer.PEPeerSource;

public class 
TorrentAttributePeerSourcesImpl
	implements TorrentAttribute
{
	private List	listeners = new ArrayList();
	
	protected
	TorrentAttributePeerSourcesImpl()
	{
	}
	
	public String
	getName()
	{
		return( TA_PEER_SOURCES );
	}
	
	public String[]
	getDefinedValues()
	{
		return( PEPeerSource.PS_SOURCES );
	}
	
	public void
	addDefinedValue(
		String		name )
	{
		throw( new RuntimeException( "not supported" ));
	}
	
	
	public void
	removeDefinedValue(
		String		name )
	{
		throw( new RuntimeException( "not supported" ));
	}
	
	public void
	addTorrentAttributeListener(
		TorrentAttributeListener	l )
	{
		listeners.add( l );
	}
	
	public void
	removeTorrentAttributeListener(
		TorrentAttributeListener	l )
	{
		listeners.remove( l );
	}
}
