/*
 * File    : IPFilter.java
 * Created : 02-Mar-2004
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

package org.gudy.azureus2.plugins.ipfilter;

/**
 * @author parg
 *
 */

import java.io.File;

public interface 
IPFilter
{
		/**
		 * Gets the file name used for persistent ranges
		 * @return
		 */
	
	public File
	getFile();

		/**
		 * creates a new range but *doesn't* add it to the list. Use the add method
		 * to add it
		 * @param this_session_only	// not persisted if "this_session_only" is true
		 * @return
		 */
	
	public IPRange
	createRange(
		boolean this_session_only );
	
		/**
		 * Adds a range. Only ranges created with "create" above can be added
		 * @param range
		 */
	
	public void
	addRange(
		IPRange		range );
	
		/**
		 * Remove a range
		 * @param range
		 */
	
	public void
	removeRange(
		IPRange		range );
	
		/**
		 * Reloads the ip filter from the config file (obtainable using "getFile")
		 * @throws IPFilterException
		 */
	
	public void
	reload()
	
		throws IPFilterException;
	
		/**
		 * Gets the current set of defined IP ranges
		 * @return
		 */
	
	public IPRange[]
	getRanges();

		/**
		 * Checks an address to see if its in an allowed range
		 * @param IPAddress
		 * @return
		 */
	
	public boolean 
	isInRange(
		String IPAddress );
	
		/**
		 * Gets the current list of blocked addresses
		 * @return
		 */
	
	public IPBlocked[]
	getBlockedIPs();
	
		/**
		 * Explicitly blocks an address
		 * @param IPAddress
		 */
	
	public void 
	block(
		String IPAddress);
	
	
		/**
		 * Test if ipfilter is enabled or not
		 * @return
		 */
	
	public boolean
	isEnabled();
	
		/**
		 * change the enabled status
		 * @param enabled
		 */
	
	public void
	setEnabled(
		boolean	enabled );
	
		/**
		 * saves current setting to file given by getFile
		 * @throws IPFilterException
		 */
	
	public void
	save()
	
		throws IPFilterException;
	
		/**
		 * Marks the IPFilter set as being uptodate
		 *
		 */
	
	public void
	markAsUpToDate();
	
		/**
		 * Gets the last time the filter set was updated or marked as up to date
		 * @return
		 */
	
	public long
	getLastUpdateTime();
}
