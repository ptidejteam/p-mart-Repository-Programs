/*
 * File    : TorrentDownloader2Factory.java
 * Created : 27-Feb-2004
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

package org.gudy.azureus2.plugins.utils.resourcedownloader;

/**
 * @author parg
 *
 */

import java.net.URL;

public interface 
ResourceDownloaderFactory 
{
		/**
		 * creates a basic downloader. current url must be http or https
		 * @param url
		 * @return
		 */
	
	public ResourceDownloader
	create(
		URL		url );
		
		/**
		 * creates a downloader that will be asked to create a ResourceDownloader
		 * when required. Useful when used in combination with an alternate downloader
		 * so that time isn't wasted creating downloaders for subsequent possibilities
		 * if the first one succeeds
		 * @param factory
		 * @return
		 */
	
	public ResourceDownloader
	create(
		ResourceDownloaderDelayedFactory	factory );

		/**
		 * gets a downloader that will retry a number of times before failing
		 * @param downloader
		 * @param retry_count
		 * @return
		 */
	
	public ResourceDownloader
	getRetryDownloader(
		ResourceDownloader		downloader,
		int						retry_count );

	
		/**
		 * gets a downloader that will timeout after a given period
		 * @param downloader
		 * @param timeout_millis
		 * @return
		 */
	
	public ResourceDownloader
	getTimeoutDownloader(
		ResourceDownloader		downloader,
		int						timeout_millis );

	
		/**
		 * gets a downloader that will cycle through a list of downloaders until
		 * a download succeeds
		 * @param downloaders
		 * @return
		 */
	
	public ResourceDownloader
	getAlternateDownloader(
		ResourceDownloader[]		downloaders );
	
	public ResourceDownloader
	getAlternateDownloader(
		ResourceDownloader[]		downloaders,
		int							max_to_try );
	
		/**
		 * an alternative downloader that randomises the downloaders
		 * @param downloaders
		 * @return
		 */
	
	public ResourceDownloader
	getRandomDownloader(
		ResourceDownloader[]		downloaders );
	
	public ResourceDownloader
	getRandomDownloader(
		ResourceDownloader[]		downloaders,
		int							max_to_try );
	
		/**
		 * gets a downloader that will automatically follow META refresh tags
		 * Will only do a single level of indirection
		 * @param downloader
		 * @return
		 */
	
	public ResourceDownloader
	getMetaRefreshDownloader(
		ResourceDownloader			downloader );

		/**
		 * Given a downloader that will download a torrent, this will download
		 * the torrent data itself. Note that the torrent MUST contain only a 
		 * single file (although a future enhancement may return a ZIP input stream
		 * for multi-file torrents)  
		 * @param downloader
		 * @param persistent whether or not the d/l will be retained over az stop/start
		 * @return
		 */
	
	public ResourceDownloader
	getTorrentDownloader(
		ResourceDownloader			downloader,
		boolean						persistent );
	
		/**
		 * Returns a downloader that does something sensible based on the url suffix.
		 * In particular will return a torrent downloader if the URL ends with ".torrent"
		 * The decision is made based on a random child downloader, so don't mix URL 
		 * suffixes below this point in the hierarchy
		 * @param url
		 * @return
		 */
	
	public ResourceDownloader
	getSuffixBasedDownloader(
		ResourceDownloader			downloader );
}
