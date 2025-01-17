/*
 * File    : ShareManager.java
 * Created : 30-Dec-2003
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

package org.gudy.azureus2.plugins.sharing;

/**
 * @author parg
 *
 */

import java.io.File;

public interface 
ShareManager 
{
	public void
	initialise()
	
		throws ShareException;
	
	public ShareResource[]
	getShares();
	
		/**
		 * returns null if share not defined
		 * @param file_or_dir
		 * @return
		 */
	
	public ShareResource
	getShare(
		File	file_or_dir );
	
	public ShareResourceFile
	addFile(
		File	file )
	
		throws ShareException, ShareResourceDeletionVetoException;
	
	public ShareResourceDir
	addDir(
		File	dir )
	
		throws ShareException, ShareResourceDeletionVetoException;
	
	public ShareResourceDirContents
	addDirContents(
		File	dir,
		boolean	recursive )
	
		throws ShareException, ShareResourceDeletionVetoException;
	
	/**
	 * adding shares can take a long time due to the torrent creation process. The current
	 * activity can be interrupted by calling this function, in which case the original 
	 * activity will fail with a ShareException
	 */
	
	public void
	cancelOperation();
	
	public void
	addListener(
		ShareManagerListener	listener );
	
	public void
	removeListener(
		ShareManagerListener	listener );
}
