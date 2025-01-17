/*
 * File    : TRTrackerScraperImpl.java
 * Created : 09-Oct-2003
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
 
package org.gudy.azureus2.core3.tracker.client.classic;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.core3.tracker.client.*;
import org.gudy.azureus2.core3.torrent.*;

public class 
TRTrackerScraperImpl
	implements TRTrackerScraper 
{
	protected static TRTrackerScraperImpl	singleton;
	
	protected TrackerChecker	tracker_checker;
	
	public static synchronized TRTrackerScraperImpl
	create()
	{
		if ( singleton == null ){
		
			singleton =  new TRTrackerScraperImpl();
		}
		
		return( singleton );
	}
	
	protected
	TRTrackerScraperImpl()
	{
		tracker_checker = new TrackerChecker();
	}
	
	public TRTrackerScraperResponse
	scrape(
		TOTorrent		torrent )
	{
		return( tracker_checker.getHashData( torrent ));
	}
		
	public TRTrackerScraperResponse
	scrape(
		TRTrackerClient	tracker_client )
	{
		return( tracker_checker.getHashData( tracker_client ));
	}
	
	public void
	remove(
		TOTorrent		torrent )
	{
		tracker_checker.removeHash( torrent );
	}
		
	public void
	remove(
		TRTrackerClient	tracker_client )
	{
		tracker_checker.removeHash( tracker_client );
	}
		
	public void
	update()
	{
		tracker_checker.update();
	}
}
