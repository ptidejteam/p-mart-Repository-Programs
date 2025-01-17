/*
 * Created on 16 May 2006
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

package com.aelitis.azureus.plugins.extseed.impl;

import java.util.List;

import org.gudy.azureus2.plugins.peers.PeerReadRequest;

import com.aelitis.azureus.plugins.extseed.ExternalSeedException;
import com.aelitis.azureus.plugins.extseed.util.ExternalSeedHTTPDownloaderListener;

public class 
ExternalSeedReaderRequest 
	implements ExternalSeedHTTPDownloaderListener
{
	private ExternalSeedReaderImpl	reader;
	
	private List			requests;

	private int		start_piece_number;
	private int		start_piece_offset;
	
	private int		length;
	
	private int					current_request_index = 0;
	private PeerReadRequest		current_request;
	private byte[]				current_buffer;
	
	protected 
	ExternalSeedReaderRequest(
		ExternalSeedReaderImpl	_reader,
		List					_requests )
	{
		reader		= _reader;
		requests	= _requests;
		
		for (int i=0;i<requests.size();i++){
			
			PeerReadRequest	req = (PeerReadRequest)requests.get(i);
			
			if ( i == 0 ){
				
				start_piece_number	= req.getPieceNumber();
				start_piece_offset	= req.getOffset();
			}
			
			length	+= req.getLength();
		}
	}
	
	public int
	getStartPieceNumber()
	{
		return( start_piece_number );
	}
	
	public int
	getStartPieceOffset()
	{
		return( start_piece_offset );
	}
	
	public int
	getLength()
	{
		return( length );
	}
	
	public byte[]
	getBuffer()
	
		throws ExternalSeedException
	{
		if ( current_request_index >= requests.size()){
			
			throw( new ExternalSeedException( "Insufficient buffers to satisfy request" ));
		}
		
		current_request = (PeerReadRequest)requests.get(current_request_index++);
		
		current_buffer = new byte[ current_request.getLength()];
		
		return( current_buffer );
	}
	        	
	public void
	done()
	{
		reader.informComplete( current_request, current_buffer );
	}
	
	public void
	failed()
	{
		for (int i=current_request_index;i<requests.size();i++){
			
			PeerReadRequest	request = (PeerReadRequest)requests.get(i);

			reader.informFailed( request );
		}
	}
}
