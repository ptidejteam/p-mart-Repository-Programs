/*
 * File    : IpFilter.java
 * Created : 1 oct. 2003 12:27:26
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
 
package org.gudy.azureus2.core3.ipfilter;



/**
 * @author Olivier
 * 
 */

import java.util.List;
import java.io.File;


public interface 
IpFilter 
{
	public File getFile();
	
	public void save() throws Exception;
	
	public void 
	reload()
	
		throws Exception;
	
	/**
	 * deprecated and to be removed after 2.0.8.0. Left in to support old SafePeer plugin
	 * version that uses this stuff directly... 
	 * @deprecated
	 * @return
	 */
	public List
	getIpRanges();
	
	public IpRange[] 
	getRanges();

	public boolean 
	isInRange(
		String ipAddress);
  
	public boolean 
	isInRange(
		String ipAddress, 
		String torrent_name);
	
	public boolean 
	isInRange(
		String ipAddress, 
		String torrent_name,
		boolean	loggable );
	
	public IpRange
	createRange(
		boolean sessionOnly);
	
	public void
	addRange(
		IpRange	range );
	
	public void
	removeRange(
		IpRange	range );
	
	public int 
	getNbRanges();
	
	public int 
	getNbIpsBlocked();
	
	public int 
	getNbIpsBlockedAndLoggable();
	
	public BlockedIp[] 
	getBlockedIps();
	
	public void 
	clearBlockedIPs();
	
	public boolean 
	ban(
		String 	ipAddress,
		String	torrent_name );
	
	public int
	getNbBannedIps();
	
	public BannedIp[]
	getBannedIps();
	
	public void
	clearBannedIps();
	
	public boolean
	isEnabled();

	public void
	setEnabled(
		boolean	enabled );
	
	public boolean
	getInRangeAddressesAreAllowed();
	
	public void
	setInRangeAddressesAreAllowed(
		boolean	b );

	public void
	markAsUpToDate();
	
	public long
	getLastUpdateTime();
	
	public long
	getTotalAddressesInRange();
	
	public void
	addListener(
		IPFilterListener	l );
	
	public void
	removeListener(
		IPFilterListener	l );
}
