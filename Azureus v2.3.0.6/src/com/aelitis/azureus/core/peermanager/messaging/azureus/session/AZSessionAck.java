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

package com.aelitis.azureus.core.peermanager.messaging.azureus.session;

import java.util.*;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.peermanager.messaging.*;
import com.aelitis.azureus.core.peermanager.messaging.azureus.AZMessage;



/**
 * Sent as reply to torrent session initiation request.
 */
public class AZSessionAck implements AZMessage {
  private DirectByteBuffer buffer = null;
  private String description = null;
  
  private final int session_id;
  private final byte[] infohash;
  private final Map session_info;
  

  public AZSessionAck( byte[] infohash, int local_session_id, Map session_info ) {
    this.session_id = local_session_id;
    this.infohash = infohash;
    this.session_info = session_info;
  }
  
  
  public int getSessionID(){  return session_id;  }
  public byte[] getInfoHash() {  return infohash;  }
  public Map getSessionInfo() {  return session_info;  }
  
    
  public String getID() {  return AZMessage.ID_AZ_SESSION_ACK;  }
  
  public byte getVersion() {  return AZMessage.AZ_DEFAULT_VERSION;  }
  
  public int getType() {  return Message.TYPE_PROTOCOL_PAYLOAD;  }
    
  
  public String getDescription() {
    if( description == null ) {
      description = getID()+ " session id " +session_id+ " for infohash " +ByteFormatter.nicePrint( infohash, true );
    }
    return description;
  }
  
  
  public DirectByteBuffer[] getData() {
    if( buffer == null ) {
      Map payload_map = new HashMap();
      
      payload_map.put( "session_id", new Long(session_id) );
      payload_map.put( "infohash", infohash );
      payload_map.put( "info", session_info );
      
      buffer = MessagingUtil.convertPayloadToBencodedByteStream( payload_map, DirectByteBuffer.AL_MSG );
    }
    
    return new DirectByteBuffer[]{ buffer };
  }
  
  
  public Message deserialize( DirectByteBuffer data ) throws MessageException {    
    Map root = MessagingUtil.convertBencodedByteStreamToPayload( data, 20, getID(), getVersion() );

    Long id = (Long)root.get( "session_id" );
    if( id == null ) throw new MessageException( "id == null" );
    int sid = id.intValue();
    
    byte[] hash = (byte[])root.get( "infohash" );
    if( hash == null )  throw new MessageException( "hash == null" );
    if( hash.length != 20 )  throw new MessageException( "hash.length != 20: " +hash.length );
    
    Map info = (Map)root.get( "info" );
    
    return new AZSessionAck( hash, sid, info );
  }
  
  
  public void destroy() {
    if( buffer != null )  buffer.returnToPool();
  }
  
}
