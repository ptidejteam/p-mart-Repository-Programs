/*
 * File    : DownloadStats.java
 * Created : 08-Jan-2004
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
 * This class gives access to various stats associated with the download
 */

public interface 
DownloadStats 
{
	/**
	 * Returns an overall string representing the state of the download
	 * @return
	 */
	
	public String
	getStatus();
	
	/**
	 * Gives access to the directory into which the download is being saved
	 * @return
	 */
	
	public String
	getDownloadDirectory();
	
	/**
	 * Gives access to the target file or directory that the download is being saved to
	 * @return
	 */
	
	public String
	getTargetFileOrDir();
	
	/**
	 * returns an general status string for the tracker
	 * @return
	 */
	
	public String
	getTrackerStatus();
	
	/**
	 * returns a value between 0 and 1000 giving the completion status of the current download
	 * task (e.g. checking, downloading)
	 * @return
	 */
	
	public int
	getCompleted();
	
	/**
	 * Gives the number of bytes downloaded
	 * @return
	 */
	
	public long
	getDownloaded();
	
	/**
	 * Gives the number of bytes uploaded
	 * @return
	 */
	
	public long
	getUploaded();

	/**
	 * Gives the number of bytes discarded
	 * @return
	 */
	
	public long
	getDiscarded();
	
	/**
	 * Gives average number of bytes downloaded in last second 
	 * @return
	 */
	
	public long
	getDownloadAverage();
	
	/**
	 * Gives average number of bytes uploaded in last second 
	 * @return
	 */
	
	public long
	getUploadAverage();
	
	/**
	 * Gives average number of bytes computed for torrent in last second 
	 * @return
	 */
	
	public long
	getTotalAverage();
	
	/**
	 * Gives the elapsed download time as a string
	 * @return
	 */
	
	public String
	getElapsedTime();
	
	/**
	 * Gices the estimated time to completion as a string
	 * @return
	 */
	
	public String
	getETA();

	/**
	 * Gives the number of bytes thrown away due to piece hash check fails
	 * @return
	 */
	
	public long
	getHashFails();
	
	/**
	 * Gives the share ratio of the torrent in 1000ths (i.e. 1000 = share ratio of 1)
	 * @return
	 */
	public int
	getShareRatio();
}
