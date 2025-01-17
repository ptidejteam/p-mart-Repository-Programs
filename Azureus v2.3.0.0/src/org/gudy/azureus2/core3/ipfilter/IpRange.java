/*
 * File    : IpRange.java
 * Created : 8 oct. 2003 13:02:23
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
public interface 
IpRange
{
	public String
	getDescription();
	
	public void
	setDescription(
		String	str );
		
	public boolean
	isValid();
  
  public boolean
  isSessionOnly();
	
	public String
	getStartIp();
	
	public void
	setStartIp(
		String	str );
		
	public String
	getEndIp();
	
	public void
	setEndIp(
		String	str );
  
  public void
  setSessionOnly(
    boolean sessionOnly );
		
	public boolean isInRange(String ipAddress);
	
	public void checkValid();
	
	public int
	compareStartIpTo(
		IpRange	other );
	
	public int 
	compareEndIpTo(
	    IpRange other );
	
	public int 
	compareDescription(
		IpRange other );
}
