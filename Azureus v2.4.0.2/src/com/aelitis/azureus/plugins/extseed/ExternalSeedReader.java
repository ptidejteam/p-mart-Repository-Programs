/*
 * Created on 15-Dec-2005
 * Created by Paul Gardner
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.plugins.extseed;

import java.util.List;

import org.gudy.azureus2.plugins.peers.PeerManager;
import org.gudy.azureus2.plugins.peers.PeerReadRequest;
import org.gudy.azureus2.plugins.torrent.Torrent;

public interface 
ExternalSeedReader 
{
	public Torrent
	getTorrent();
	
	public String
	getName();
	
	public String
	getStatus();
	
	public boolean
	isPermanentlyUnavailable();
	
	public String
	getIP();
	
	public int
	getPort();
	
	public boolean
	isActive();
	
	public boolean
	checkActivation(
		PeerManager		peer_manager );
	
	public void
	addRequest(
		PeerReadRequest	request );
	
	public void
	cancelRequest(
		PeerReadRequest	request );
	
	public void
	cancelAllRequests();
	
	public int
	getRequestCount();
	
	public List
	getExpiredRequests();
	
	public List
	getRequests();
	
	public void
	deactivate(
		String	reason );
	
	public void
	addListener(
		ExternalSeedReaderListener	l );
	
	public void
	removeListener(
		ExternalSeedReaderListener	l );
}
