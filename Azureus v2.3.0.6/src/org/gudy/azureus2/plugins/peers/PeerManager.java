/*
 * File    : PeerManager.java
 * Created : 28-Dec-2003
 * By      : parg
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.gudy.azureus2.plugins.peers;

import org.gudy.azureus2.plugins.download.*;
import org.gudy.azureus2.plugins.disk.*;

/**
 * @author parg
 *
 */
public interface 
PeerManager 
{
	public Download
	getDownload()
	
		throws DownloadException;
	
	public void
	addPeer(
		Peer		peer );
  
  
	  /**
	   * Add a new peer, using the default internal Peer implementation
	   * (like for peers given in announce reply), using the given address
	   * and port.
	   * @param ip_address of peer to inject
	   * @param port of peer to inject
	   */
	
	public void 
	addPeer( 
		String ip_address, 
		int port );
  
	public void
	removePeer(
		Peer		peer );
	
	public Peer[]
	getPeers();
	
	public DiskManager
	getDiskManager();
	
	public PeerManagerStats
	getStats();
	
	public boolean
	isSeeding();
	
	public boolean
	isSuperSeeding();
	
	public PeerStats
	createPeerStats();
	
	public void
	addListener(
		PeerManagerListener	l );
	
	public void
	removeListener(
		PeerManagerListener	l );
}
