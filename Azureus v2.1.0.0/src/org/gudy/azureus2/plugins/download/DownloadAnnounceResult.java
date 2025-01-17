/*
 * File    : DownloadAnnounceResult.java
 * Created : 12-Jan-2004
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

package org.gudy.azureus2.plugins.download;

/**
 * @author parg
 * This class represents the results of an "announce" made to a tracker.
 */

public interface 
DownloadAnnounceResult 
{
	public static final int	RT_SUCCESS	= 1;
	public static final int RT_ERROR	= 2;

	/**
	 * Gives access to the Download associated with this announce result
	 * @return
	 */
	
	public Download
	getDownload();
	
	/**
	 * The response may represent a successful or failed announce
	 * @return	either RT_SUCCESS or RT_ERROR
	 */
	
	public int
	getResponseType();
	
	/**
	 * For RT_SUCCESS this gives the number of peers returned by the tracker
	 * @return
	 */
	
	public int
	getReportedPeerCount();	// number returned by the announce
	
	/**
	 * This method gives the number of seeds we know about (and may have received from a succession
	 * of announces)
	 * @return
	 */
	
	public int
	getSeedCount();			// seeds we know about
	
	/**
	 * This method gives the number of non-seeds we know about
	 * @return
	 */
	
	public int
	getNonSeedCount();		// non-seeds we know about
	
	/**
	 * For RT_ERROR this gives error details
	 * @return
	 */
	
	public String
	getError();
}
