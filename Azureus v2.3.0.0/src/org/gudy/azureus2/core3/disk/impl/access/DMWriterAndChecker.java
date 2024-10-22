/*
 * Created on 31-Jul-2004
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

package org.gudy.azureus2.core3.disk.impl.access;

import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.disk.impl.DiskManagerFileInfoImpl;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

/**
 * @author parg
 *
 */
public interface 
DMWriterAndChecker 
{
	public void
	start();
	
	public void
	stop();
	
	public boolean 
	zeroFile( 
		DiskManagerFileInfoImpl file, 
		long 					length );

	public void 
	checkPiece(
		int 					pieceNumber,
		CheckPieceResultHandler	result_handler,
		Object					user_data )
	
		throws Exception;
	
	public void 
	enqueueCompleteRecheckRequest(
		final DiskManagerCheckRequestListener 	listener,
		final Object							user_data ) ;
	
	public void 
	enqueueCheckRequest(
		int 							pieceNumber,
		DiskManagerCheckRequestListener	listener,
		Object							user_data ); 
	  
	public boolean 
	isChecking();
	
	public boolean 
	checkBlock(
		int 				pieceNumber, 
		int 				offset, 
		DirectByteBuffer 	data);

	
	public boolean 
	checkBlock(
		int 		pieceNumber, 
		int 		offset, 
		int 		length) ;

	public void 
	writeBlock(
		int 							pieceNumber, 
		int 							offset, 
		DirectByteBuffer 				data,
		Object 							user_data,
		DiskManagerWriteRequestListener	listener );
}
