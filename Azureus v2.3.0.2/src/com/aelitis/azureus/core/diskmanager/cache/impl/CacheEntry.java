/*
 * Created on 04-Aug-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
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
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.core.diskmanager.cache.impl;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.core3.util.*;

public class 
CacheEntry 
{
	protected static final int	CT_DATA_WRITE		= 0;
	protected static final int	CT_READ_AHEAD		= 1;
	
	protected CacheFileWithCache		file;
	protected DirectByteBuffer	buffer;
	protected long				offset;
	protected int				size;
	protected int				buffer_pos;
	protected int				buffer_limit;
	
	protected boolean			dirty;
	
	protected long				last_used;
	
	protected int				entry_type;
	protected int				usage_count;
	
	protected
	CacheEntry(
		int					_entry_type,
		CacheFileWithCache		_file,
		DirectByteBuffer	_buffer,
		long				_offset,
		int					_size )
	{
		entry_type	= _entry_type;
		file		= _file;
		buffer		= _buffer;
		offset		= _offset;
		size		= _size;
		
		buffer_pos		= buffer.position(DirectByteBuffer.SS_CACHE);
		buffer_limit	= buffer.limit(DirectByteBuffer.SS_CACHE);
		
		if ( size != buffer_limit - buffer_pos ){
			
			Debug.out( "CacheEntry: initial size incorrect - size =" + size + ", pos = " + buffer_pos + ", lim = " + buffer_limit );
		}
		
		dirty		= true;
		last_used	= SystemTime.getCurrentTime();
	}
	
	public CacheFileWithCache
	getFile()
	{	
		return( file );
	}
	
	public long
	getFilePosition()
	{
		return( offset );
	}
	
	public int
	getLength()
	{
		return( size );
	}
	
	public DirectByteBuffer
	getBuffer()
	{
		return( buffer );
	}
	
	public boolean
	isDirty()
	{
		return( dirty );
	}
	
	public void
	setClean()
	{
		dirty	= false;
	}
	
	protected void
	resetBufferPosition()
	{
		buffer.position( DirectByteBuffer.SS_CACHE, buffer_pos );
	}
	
	protected void
	used()
	{
		last_used = SystemTime.getCurrentTime();
		
		usage_count++;
	}
	
	protected long
	getLastUsed()
	{
		return( last_used );
	}
	
	protected int
	getUsageCount()
	{
		return( usage_count );
	}
	
	protected int
	getType()
	{
		return( entry_type );
	}
	
	protected String
	getString()
	{
		return( "[" + offset + " - " + (offset+size-1) + ":" + buffer.position(DirectByteBuffer.SS_CACHE)+"/"+buffer.limit(DirectByteBuffer.SS_CACHE)+"]" );
	}
}
