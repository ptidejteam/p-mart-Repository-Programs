/*
 * Created on Feb 15, 2005
 * Created by Alon Rohter
 * Copyright (C) 2004-2005 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.plugins.jpc.cache.impl.messaging;

import java.nio.ByteBuffer;

import org.gudy.azureus2.plugins.messaging.Message;
import org.gudy.azureus2.plugins.messaging.MessageException;

/**
 *
 */
public class JPCBye implements JPCCacheMessage {
  private final String description;
  private final ByteBuffer buffer;
  private final String reason;
  
  public JPCBye( String reason ) {
    this.reason = reason;
    description = getID()+ " because " +reason;    
    buffer = ByteBuffer.wrap( reason.getBytes() );
  }
  
  
  public String getReason() {  return reason;  }
  
  
  public String getID() {  return JPCCacheMessage.ID_JPC_BYE;  }

  public byte getVersion() {  return JPCCacheMessage.JPC_DEFAULT_VERSION;  }

  public int getType() {  return Message.TYPE_PROTOCOL_PAYLOAD;  }
    
  public String getDescription() {  return description;  }
  
  public ByteBuffer[] getPayload() {  return new ByteBuffer[] { buffer };  }
    
  public void destroy() { /*nothing*/ }
    
  
  
  public Message create( ByteBuffer data ) throws MessageException {
    if( data == null ) {
      throw new MessageException( "[" +getID() + ":" +getVersion()+ "] decode error: data == null" );
    }
    
    if( data.remaining() < 1 ) {
      throw new MessageException( "[" +getID() + ":" +getVersion()+ "] decode error: payload.remaining[" +data.remaining()+ "] < 1" );
    }
    
    byte[] rsn = new byte[ data.remaining() ];
    data.get( rsn );
    
    return new JPCBye( new String( rsn ) );
  }
    
  
  
}
