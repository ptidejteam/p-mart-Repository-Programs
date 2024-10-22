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

package com.aelitis.azureus.core.peermanager.messaging.azureus;

import java.util.*;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.peermanager.messaging.*;



/**
 * This is a helper class for creating messages with a Map'd beencode-able payload.
 */
public class AZGenericMapPayload implements AZMessage {
  private DirectByteBuffer buffer = null;

  private final String type_id;
  private final Map msg_map;
  
  
  /**
   * Create a new AZ message with the given message type id, with the given bencode-able map payload.
   * @param message_type of message
   * @param message payload (to be bencoded)
   */
  public AZGenericMapPayload( String message_type, Map message ) {
    this.type_id = message_type;
    this.msg_map = message;
  }
  
    
  public String getID() {  return type_id;  }
  
  public byte getVersion() {  return AZMessage.AZ_DEFAULT_VERSION;  }
  
  public int getType() {  return Message.TYPE_PROTOCOL_PAYLOAD;  }
 
  public Map getMapPayload() {  return msg_map;  }
  
 
  public String getDescription() {   return getID();  }
  
  
  public DirectByteBuffer[] getData() {
    if( buffer == null ) {
      buffer = MessagingUtil.convertPayloadToBencodedByteStream( msg_map, DirectByteBuffer.AL_MSG );
    } 
    return new DirectByteBuffer[]{ buffer };
  }
  
  
  public Message deserialize( DirectByteBuffer data ) throws MessageException {
    Map payload = MessagingUtil.convertBencodedByteStreamToPayload( data, 1, getID(), getVersion() );
    return new AZGenericMapPayload( getID(), payload );
  }
  
  
  public void destroy() {
    if( buffer != null )  buffer.returnToPool();
  }
  
}
