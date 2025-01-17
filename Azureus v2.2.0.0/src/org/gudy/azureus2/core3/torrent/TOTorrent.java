/*
 * File    : TOTorrent.java
 * Created : 5 Oct. 2003
 * By      : Parg 
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

package org.gudy.azureus2.core3.torrent;

import java.io.*;
import java.net.*;
import java.util.*;

import org.gudy.azureus2.core3.util.*;

public interface 
TOTorrent
{
	public static final String	DEFAULT_IGNORE_FILES	= ".DS_Store;Thumbs.db;desktop.ini";
	
	/**
	 * Get the name of the torrent
	 * @return
	 */
	
	public byte[]
	getName();
	
	/**
	 * A "simple torrent" is one that consists of a single file on its own (i.e. not in a
	 * nested directory). 
	 * @return
	 */	
	
	public boolean
	isSimpleTorrent();
	
	/**
	 * Comment is an optional torrent property
	 * @return
	 */
	
	public byte[]
	getComment();

	public void
	setComment(
		String		comment );
	
	/**
	 * Gets the creation date of the torrent. Optional property, 0 returned if not set
	 * @return
	 */	
	
	public long
	getCreationDate();
	
	public void
	setCreationDate(
		long		date );
	
	public byte[]
	getCreatedBy();
	
	/**
	 * A torrent must have a URL that identifies the tracker. This method returns it. However
	 * an extension to this exists to allow multiple trackers, and their backups, to be defined.
	 * See below
	 * @return
	 */	
	public URL
	getAnnounceURL();

	public void
	setAnnounceURL(
		URL		url );
		
	/**
	 * When a group of sets of trackers is defined their URLs are accessed via this method
	 * @return the group, always present, which may have 0 members
	 */
	
	public TOTorrentAnnounceURLGroup
	getAnnounceURLGroup();  
	 
	 /**
	  * This method provides access to the SHA1 hash values (20 bytes each) that correspond
	  * to the pieces of the torrent.
	  * @return
	  */
	public byte[][]
	getPieces();

	/**
	 * Returns the piece length used for the torrent
	 * @return
	 */
	public long
	getPieceLength();

	public long
	getSize();
	
	/**
	 * A torrent consists of one or more files. These are accessed via this method.
	 * @return
	 */ 
	public TOTorrentFile[]
	getFiles();
	
	 /**
	  * A torrent has a unique SHA1 (20 byte) hash that is computed from some of its contents.
	  * It is used, for example, when contacting a tracker to identify the torrent.
	  * @return
	  * @throws TOTorrentException
	  */
	 
	public byte[]
	getHash()
				
		throws TOTorrentException;

	/**
	 * convenience method to get a wrapped hash for performance purposes
	 * @return
	 * @throws TOTorrentException
	 */
	
	public HashWrapper
	getHashWrapper()
				
		throws TOTorrentException;

	/**
	 * compares two torrents by hash
	 * @param other
	 * @return
	 */
	
	public boolean
	hasSameHashAs(
		TOTorrent		other );
	
	/**
	 * The additional properties are used for holding non-core data for Azureus' own user
	 * @param name		name of the property (e.g. "encoding")
	 * @param value		value. This will be encoded with default encoding
	 */
	
	public void
	setAdditionalStringProperty(
		String		name,
		String		value );
		
	public String
	getAdditionalStringProperty(
		String		name );
		
	public void
	setAdditionalByteArrayProperty(
		String		name,
		byte[]		value );
		
	public byte[]
	getAdditionalByteArrayProperty(
		String		name );
	
	public void
	setAdditionalLongProperty(
		String		name,
		Long		value );
		
	public Long
	getAdditionalLongProperty(
		String		name );
		
	
	public void
	setAdditionalListProperty(
		String		name,
		List		value );
		
	public List
	getAdditionalListProperty(
		String		name );
		
	public void
	setAdditionalMapProperty(
		String		name,
		Map			value );
		
	public Map
	getAdditionalMapProperty(
		String		name );
	
	public Object
	getAdditionalProperty(
		String		name );

	public void
	removeAdditionalProperty(
		String name );
	
	/**
	 * remove all additional properties to clear out the torrent
	 */	
	
	public void
	removeAdditionalProperties();
	
	 /**
	  * This method will serialise a torrent using the standard "b-encoding" mechanism into a file
	  * @param file
	  * @throws TOTorrentException
	  */
	public void
	serialiseToBEncodedFile(
		File		file )
		  
		throws TOTorrentException;

	 /**
	  * This method will serialise a torrent into a Map consistent with that used by the 
	  * "b-encoding" routines defined elsewhere
	  * @return
	  * @throws TOTorrentException
	  */
	public Map
	serialiseToMap()
		  
		throws TOTorrentException;

	/**
	 * This method will serialise a torrent using an XML encoding to a file
	 * @param file
	 * @throws TOTorrentException
	 */
	
   public void
   serialiseToXMLFile(
	   File		file )
		  
	   throws TOTorrentException;

   public AEMonitor
   getMonitor();

	 /**
	  * A diagnostic method for dumping the tracker contents to "stdout"
	  *
	  */
	public void
	print();
}
