/*
 * File    : TRTrackerClientListener.java
 * Created : 03-Nov-2003
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

package org.gudy.azureus2.core3.tracker.client;

/**
 * @author parg
 *
 */
public interface 
TRTrackerClientListener 
{
	/**
	 * This callback indicates if the tracker client has change URL. If "explicit" is true then
	 * this was via a call to the "setTrackerURL" method on TRTrackerClient. If false then the selected
	 * URL has changed because it is a multi-tracker torrent
	 * 
	 * @param explicit as above
	 */
	public void
	urlChanged(
		String		url,
		boolean		explicit );
		
	public void
	urlRefresh();
}
