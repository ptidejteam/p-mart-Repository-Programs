/*
 * Created on 03-Aug-2004
 * Created by Paul Gardner
 * Copyright (C) 2004, 2005, 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.diskmanager.cache;

import java.io.File;

import org.gudy.azureus2.core3.torrent.TOTorrentFile;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

/**
 * @author parg
 *
 */
public interface 
CacheFile 
{
	public static final int	CT_LINEAR	= 1;
	public static final int CT_COMPACT	= 2;

	public static final int	CF_READ		= 1;
	public static final int CF_WRITE	= 2;
	
	public static final short CP_NONE		= 0x0000;
	public static final short CP_READ_CACHE	= 0x0001;
	public static final short CP_FLUSH		= 0x0002;
	
	public TOTorrentFile
	getTorrentFile();
	
	public boolean
	exists();
		
	public void
	moveFile(
		File		new_file )
	
		throws CacheFileManagerException;
	
	public void
	setAccessMode(
		int		mode )
	
		throws CacheFileManagerException;
	
	public int
	getAccessMode();
	
	public void
	setStorageType(
		int		type )
	
		throws CacheFileManagerException;
	
	public int
	getStorageType();
	
	public void
	ensureOpen(
		String	reason )

		throws CacheFileManagerException;

	public long
	getLength()
	
		throws CacheFileManagerException;

	public long
	compareLength(
		long	compare_to )
	
		throws CacheFileManagerException;
	
	public void
	setLength(
		long		length )
	
		throws CacheFileManagerException;
	
	public void
	read(
		DirectByteBuffer	buffer,
		long				offset,
		short				policy )
	
		throws CacheFileManagerException;
	
	public void
	write(
		DirectByteBuffer	buffer,
		long				position )
	
		throws CacheFileManagerException;
	
		/**
		 * writes the block to the cache and gives control of the buffer to the cache.
		 * @param buffer
		 * @param position
		 * @throws CacheFileManagerException	write failed and buffer *not* taken - i.e. caller must de-allocate
		 */
	
	public void
	writeAndHandoverBuffer(
		DirectByteBuffer	buffer,
		long				position )
	
		throws CacheFileManagerException;
	
		/**
		 * flushes the cache to disk but retains entries
		 * @throws CacheFileManagerException
		 */
	
	public void
	flushCache()
	
		throws CacheFileManagerException;
	
		/**
		 * flushes the cache and discards entries
		 * @throws CacheFileManagerException
		 */
	
	public void
	clearCache()
	
		throws CacheFileManagerException;

	public void
	close()
	
		throws CacheFileManagerException;
	
	public void
	delete()

		throws CacheFileManagerException;
}
