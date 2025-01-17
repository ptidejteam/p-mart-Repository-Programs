/*
 * Created on 17-Jan-2006
 * Created by Paul Gardner
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.networkmanager.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

public class 
TCPTransportHelperFilterStreamXOR
	extends TCPTransportHelperFilterStream
{
	private byte[]		mask;
	private int			read_position;
	private int			write_position;
	
	protected
	TCPTransportHelperFilterStreamXOR(
		TCPTransportHelper		_transport,
		byte[]					_mask )
	{
		super( _transport );
		
		mask		= _mask;
	}
	
	protected void
	cryptoOut(
		ByteBuffer	source_buffer,
		ByteBuffer	target_buffer )
	
		throws IOException
	{		
		int	rem = source_buffer.remaining();
		
		for (int i=0;i<rem;i++){
			
			byte	b = source_buffer.get();
			
			b = (byte)( b ^ mask[ write_position++ ]);
			
			target_buffer.put( b );
			
			if ( write_position == mask.length  ){
				
				write_position	= 0;
			}
		}
	}
	
	protected void
	cryptoIn(
		ByteBuffer	source_buffer,
		ByteBuffer	target_buffer )
	
		throws IOException
	{		
		int	rem = source_buffer.remaining();
		
		for (int i=0;i<rem;i++){
			
			byte	b = source_buffer.get();
			
			b = (byte)( b ^ mask[ read_position++ ]);
			
			target_buffer.put( b );
			
			if ( read_position == mask.length  ){
				
				read_position	= 0;
			}
		}	
	}
	
	public String
	getName()
	{
		return( "XOR-" + mask.length*8 );
	}
}
