/*
 * Created on Jun 17, 2005
 * Created by Alon Rohter
 * Copyright (C) 2005 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.peermanager.messaging;

import java.util.Map;

import org.gudy.azureus2.core3.util.*;


public class MessagingUtil {

  
  /**
   * Convert the given message payload map to a bencoded byte stream.
   * @param payload to convert
   * @return bencoded serialization
   */
  public static DirectByteBuffer convertPayloadToBencodedByteStream( Map payload ) {
    byte[] raw_payload;
    
    try {
      raw_payload = BEncoder.encode( payload );
    }
    catch( Throwable t ) {
      Debug.out( t );
      raw_payload = new byte[0];
    }
    
    DirectByteBuffer buffer = DirectByteBufferPool.getBuffer( DirectByteBuffer.AL_MSG, raw_payload.length );
    buffer.put( DirectByteBuffer.SS_MSG, raw_payload );
    buffer.flip( DirectByteBuffer.SS_MSG );
    
    return buffer;
  }
  
  
  
  /**
   * Convert the given bencoded byte stream into a message map.
   * @param stream to convert
   * @param min_size of stream
   * @param id of message
   * @param version of message
   * @return mapped deserialization
   * @throws MessageException on convertion error
   */
  public static Map convertBencodedByteStreamToPayload( DirectByteBuffer stream, int min_size, String id, byte version ) throws MessageException {
    if( stream == null ) {
      throw new MessageException( "[" +id + ":" +version+ "] decode error: stream == null" );
    }
    
    if( stream.remaining( DirectByteBuffer.SS_MSG ) < min_size ) {
      stream.returnToPool();
      throw new MessageException( "[" +id + ":" +version+ "] decode error: stream.remaining[" +stream.remaining( DirectByteBuffer.SS_MSG )+ "] < " +min_size );
    }

    byte[] raw = new byte[ stream.remaining( DirectByteBuffer.SS_MSG ) ];
    stream.get( DirectByteBuffer.SS_MSG, raw );
    stream.returnToPool();
      
    try {
      return BDecoder.decode( raw );
    }
    catch( Throwable t ) {
      throw new MessageException( "[" +id+ ":" +version+ "] payload stream b-decode error: " +t.getMessage() );
    } 
  }
  
  


  
}
