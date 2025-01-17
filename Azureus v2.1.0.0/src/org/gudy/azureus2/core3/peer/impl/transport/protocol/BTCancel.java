/*
 * Created on Apr 30, 2004
 * Created by Alon Rohter
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

package org.gudy.azureus2.core3.peer.impl.transport.protocol;

import java.nio.ByteBuffer;

import org.gudy.azureus2.core3.util.*;

/**
 * BitTorrent cancel message.
 */
public class BTCancel implements BTMessage {
  
  private final DirectByteBuffer buffer;
  private final int piece_number;
  private final int piece_offset;
  private final int length;
  
  public BTCancel( int piece_number, int piece_offset, int length ) {
    this.piece_number = piece_number;
    this.piece_offset = piece_offset;
    this.length = length;
    buffer = new DirectByteBuffer( ByteBuffer.allocate( 17 ) );
    
    buffer.buff.putInt( 13 );
    buffer.buff.put( (byte)8 );
    buffer.buff.putInt( piece_number );
    buffer.buff.putInt( piece_offset );
    buffer.buff.putInt( length );
    buffer.buff.position( 0 );
    buffer.buff.limit( 17 );
  }
  
  public int getType() {  return BTMessage.BT_CANCEL;  }
  
  public DirectByteBuffer getPayload() {  return buffer;  }
  
  public String getDescription() {
    return "Cancel piece #" + piece_number + ": " + piece_offset + "->" + (piece_offset + length);
  }
  
  
}
