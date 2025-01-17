/*
 * File    : PEPeerImpl.java
 * Created : 16-Oct-2003
 * By      : Olivier
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

package org.gudy.azureus2.core3.peer.impl.transport;

/**
 * @author parg
 *
 */

import java.util.Arrays;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.peer.impl.PEPeerControl;

public class 
PEPeerImpl
{
	protected PEPeerControl manager;
	protected byte[] id;
	protected String ip;
	protected int port;

	public PEPeerImpl(PEPeerControl manager, byte[] id, String ip, int port) {
		this.manager = manager;
		this.ip = ip;
		this.port = port;
	 	this.id = id;
	}


	/**
	 * @return
	 */
	public byte[] getId() {
	  return id;
	}

	/**
	 * @return
	 */
	public String getIp() {
	  return ip;
	}

	/**
	 * @return
	 */
	public PEPeerControl getManager() {
	  return manager;
	}

	/**
	 * @return
	 */
	public int getPort() {
	  return port;
	}
  
	public boolean equals(Object o) {
		if (!(o instanceof PEPeerImpl))
		  return false;
		PEPeerImpl p = (PEPeerImpl) o;
		//At least the same instance is equal to itself :p
		if (this == p)
		  return true;
		if (!(p.ip).equals(this.ip))
		  return false;		
		//same ip, we'll check peerId
		byte[] otherId;
		if (this.id == null || (otherId = p.getId()) == null)
		  return false;
    
		//same ip check for config to know if we allow
		//multiple peers from same ip
		if(!COConfigurationManager.getBooleanParameter("Allow Same IP Peers",false))
		  return true;
		
		
		return Arrays.equals(this.id, otherId);
	  }

}
