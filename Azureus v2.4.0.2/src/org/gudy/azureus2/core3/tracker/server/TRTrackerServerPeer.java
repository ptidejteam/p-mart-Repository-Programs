/*
 * File    : TRTrackerServerPeer.java
 * Created : 31-Oct-2003
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
 
package org.gudy.azureus2.core3.tracker.server;

/**
 * @author parg
 */

public interface 
TRTrackerServerPeer 
{
	public static final byte	NAT_CHECK_UNKNOWN				= 0;
	public static final byte	NAT_CHECK_DISABLED				= 1;
	public static final byte	NAT_CHECK_INITIATED				= 2;
	public static final byte	NAT_CHECK_OK					= 3;
	public static final byte	NAT_CHECK_FAILED				= 4;
	public static final byte	NAT_CHECK_FAILED_AND_REPORTED	= 5;
	
	public long
	getUploaded();
	
	public long
	getDownloaded();
	
	public long
	getAmountLeft();
	
	public String
	getIP();
	
	public String
	getIPRaw();
	
		/**
		 * returns the current NAT status of the peer
		 * @return
		 */
	
	public byte
	getNATStatus();
}
