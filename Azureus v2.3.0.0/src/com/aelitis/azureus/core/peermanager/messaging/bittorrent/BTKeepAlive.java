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

package com.aelitis.azureus.core.peermanager.messaging.bittorrent;


import java.nio.ByteBuffer;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.networkmanager.RawMessage;
import com.aelitis.azureus.core.peermanager.messaging.Message;
import com.aelitis.azureus.core.peermanager.messaging.MessageException;


/**
 * BitTorrent keep-alive message.
 */
public class BTKeepAlive implements BTMessage, RawMessage {
  private DirectByteBuffer[] buffer = null;
  
  
  public BTKeepAlive() {
    /* nothing */    
  }

  
  // message
  public String getID() {  return BTMessage.ID_BT_KEEP_ALIVE;  }
  
  public byte getVersion() {  return BTMessage.BT_DEFAULT_VERSION;  }
  
  public int getType() {  return Message.TYPE_PROTOCOL_PAYLOAD;  }
    
  public String getDescription() {  return BTMessage.ID_BT_KEEP_ALIVE;  }
  
  public DirectByteBuffer[] getData() {  return new DirectByteBuffer[]{};  }

  public Message deserialize( DirectByteBuffer data ) throws MessageException {   
    if( data != null && data.hasRemaining( DirectByteBuffer.SS_MSG ) ) {
      throw new MessageException( "[" +getID() + ":" +getVersion()+ "] decode error: payload not empty" );
    }
    
    if( data != null )  data.returnToPool();
    
    return new BTKeepAlive();
  }
  
  
  // raw message
  public DirectByteBuffer[] getRawData() {
    if( buffer == null ) {
      DirectByteBuffer dbb = new DirectByteBuffer( ByteBuffer.allocate( 4 ) );
      dbb.putInt( DirectByteBuffer.SS_BT, 0 );
      dbb.flip( DirectByteBuffer.SS_BT );
      buffer =  new DirectByteBuffer[]{ dbb };
    }
    return buffer;
  }
  
  public int getPriority() {  return RawMessage.PRIORITY_LOW;  }

  public boolean isNoDelay() {  return false;  }
 
  public Message[] messagesToRemove() {  return null;  }

  public void destroy() {  }

  public Message getBaseMessage() {  return this;  }
  
}
