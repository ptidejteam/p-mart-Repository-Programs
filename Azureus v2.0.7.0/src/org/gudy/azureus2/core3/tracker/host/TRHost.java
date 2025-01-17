/*
 * File    : TRHost.java
 * Created : 24-Oct-2003
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
 
package org.gudy.azureus2.core3.tracker.host;

/**
 * @author parg
 */

import org.gudy.azureus2.core3.torrent.*;
import org.gudy.azureus2.core3.tracker.server.*;

public interface 
TRHost
{
	public static final int DEFAULT_MIN_RETRY_DELAY 	= TRTrackerServer.DEFAULT_MIN_RETRY_DELAY;
	public static final int DEFAULT_MAX_RETRY_DELAY 	= TRTrackerServer.DEFAULT_MAX_RETRY_DELAY;
	public static final int DEFAULT_INC_BY				= TRTrackerServer.DEFAULT_INC_BY;
	public static final int DEFAULT_INC_PER			 	= TRTrackerServer.DEFAULT_INC_PER;
	public static final int DEFAULT_PORT 				= TRTrackerServer.DEFAULT_TRACKER_PORT;
	public static final int DEFAULT_PORT_SSL			= TRTrackerServer.DEFAULT_TRACKER_PORT_SSL;

	public void
	initialise(
		TRHostTorrentFinder	finder );
		
	public TRHostTorrent
	hostTorrent(
		TOTorrent		torrent )
	
		throws TRHostException;
	
	public TRHostTorrent
	hostTorrent(
		TOTorrent		torrent,
		boolean			persistent )
	
		throws TRHostException;
	
	public TRHostTorrent
	publishTorrent(
		TOTorrent		torrent )
		
		throws TRHostException;
				
	public TRHostTorrent[]
	getTorrents();
	
	public void
	addListener(
		TRHostListener	l );
		
	public void
	removeListener(
		TRHostListener	l );
}
