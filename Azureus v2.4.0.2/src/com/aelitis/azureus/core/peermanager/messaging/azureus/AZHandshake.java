/*
 * Created on Apr 30, 2004
 * Created by Alon Rohter
 * Copyright (C) 2004, 2005, 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.peermanager.messaging.azureus;

import java.util.*;

import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.peermanager.messaging.Message;
import com.aelitis.azureus.core.peermanager.messaging.MessageException;
import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;




/**
 * AZ handshake message.
 */
public class AZHandshake implements AZMessage {
	
	public static final int HANDSHAKE_TYPE_PLAIN  = 0;
	public static final int HANDSHAKE_TYPE_CRYPTO = 1;
	
	
  private static final byte bss = DirectByteBuffer.SS_MSG;

  private DirectByteBuffer buffer = null;
  private String description = null;
  
  private final byte[] identity;
  private final String client;
  private final String client_version;
  private final String[] avail_ids;
  private final byte[] avail_versions;
  private int tcp_port;
  private int udp_port;
  private final int handshake_type;
  
  
  public AZHandshake( byte[] peer_identity,
                      String _client,
                      String version,
                      int tcp_listen_port,
                      int udp_listen_port,
                      String[] avail_msg_ids,
                      byte[] avail_msg_versions,
                      int _handshake_type ) {
    
    this.identity = peer_identity;
    this.client = _client;
    this.client_version = version;
    this.avail_ids = avail_msg_ids;
    this.avail_versions = avail_msg_versions;
    this.tcp_port = tcp_listen_port;
    this.udp_port = udp_listen_port;
    this.handshake_type = _handshake_type;
    
    //verify given port info is ok
    if( tcp_port < 0 || tcp_port > 65535 ) {
      Debug.out( "given TCP listen port is invalid: " +tcp_port );
      tcp_port = 0;
    }
    
    if( udp_port < 0 || udp_port > 65535 ) {
      Debug.out( "given UDP listen port is invalid: " +udp_port );
      udp_port = 0;
    }
  }

  
  
  public byte[] getIdentity() {  return identity;  }
  
  public String getClient() {  return client;  }
  
  public String getClientVersion() {  return client_version;  }
  
  public String[] getMessageIDs() {  return avail_ids;  }
  
  public byte[] getMessageVersions() {  return avail_versions;  }
  
  public int getTCPListenPort() {  return tcp_port;  }
  public int getUDPListenPort() {  return udp_port;  }
  
  public int getHandshakeType() {  return handshake_type;  }
  
    
  public String getID() {  return AZMessage.ID_AZ_HANDSHAKE;  }
  public byte[] getIDBytes() {  return AZMessage.ID_AZ_HANDSHAKE_BYTES;  }
  
  public String getFeatureID() {  return AZMessage.AZ_FEATURE_ID;  }  
  
  public int getFeatureSubID() { return AZMessage.SUBID_AZ_HANDSHAKE;  }
  
  
  public int getType() {  return Message.TYPE_PROTOCOL_PAYLOAD;  }
    
  public String getDescription() {
    if( description == null ) {
      String msgs_desc = "";
      for( int i=0; i < avail_ids.length; i++ ) {
        String id = avail_ids[ i ];
        byte ver = avail_versions[ i ];
        if( id.equals( getID() ) )  continue;  //skip ourself
        msgs_desc += "[" +id+ ":" +ver+ "]";
      }
      description = getID()+ " from [" +ByteFormatter.nicePrint( identity, true )+ ", " +
      							client+ " " +client_version+ ", TCP/UDP ports " +tcp_port+ "/" +udp_port+
      							", handshake " + (getHandshakeType() == HANDSHAKE_TYPE_PLAIN ? "plain" : "crypto") +
      							"] supports " +msgs_desc;
    }
    
    return description;
  }
  
  
  public DirectByteBuffer[] getData() {
    if( buffer == null ) {
      Map payload_map = new HashMap();
      
      //client info
      payload_map.put( "identity", identity );
      payload_map.put( "client", client );
      payload_map.put( "version", client_version );
      payload_map.put( "tcp_port", new Long( tcp_port ) );
      payload_map.put( "udp_port", new Long( udp_port ) );
      payload_map.put( "handshake_type", new Long( handshake_type ) );
          
      //available message list
      List message_list = new ArrayList();
      for( int i=0; i < avail_ids.length; i++ ) {
        String id = avail_ids[ i ];
        byte ver = avail_versions[ i ];
        
        if( id.equals( getID() ))  continue;  //skip ourself

        Map msg = new HashMap();
        msg.put( "id", id );
        msg.put( "ver", new byte[]{ ver } );
          
        message_list.add( msg );
      }
      payload_map.put( "messages", message_list );

      buffer = MessagingUtil.convertPayloadToBencodedByteStream( payload_map, DirectByteBuffer.AL_MSG_AZ_HAND );

      if( buffer.remaining( bss ) > 1200 )  System.out.println( "Generated AZHandshake size = " +buffer.remaining( bss )+ " bytes" );
    }
    
    return new DirectByteBuffer[]{ buffer };
  }
  
  
  public Message deserialize( DirectByteBuffer data ) throws MessageException {
    Map root = MessagingUtil.convertBencodedByteStreamToPayload( data, 100, getID() );

    byte[] id = (byte[])root.get( "identity" );
    if( id == null )  throw new MessageException( "id == null" );
    if( id.length != 20 )  throw new MessageException( "id.length != 20: " +id.length );
      
    byte[] raw_name = (byte[])root.get( "client" );
    if( raw_name == null )  throw new MessageException( "raw_name == null" );
    String name = new String( raw_name );

    byte[] raw_ver = (byte[])root.get( "version" );
    if( raw_ver == null )  throw new MessageException( "raw_ver == null" );
    String version = new String( raw_ver );

    Long tcp_lport = (Long)root.get( "tcp_port" );
    if( tcp_lport == null ) {  //old handshake
      tcp_lport = new Long( 0 );
    }

    Long udp_lport = (Long)root.get( "udp_port" );
    if( udp_lport == null ) {  //old handshake
      udp_lport = new Long( 0 );
    }

    Long h_type = (Long)root.get( "handshake_type" );
    if( h_type == null ) {  //only 2307+ send type
    	h_type = new Long( HANDSHAKE_TYPE_PLAIN );
    }

    List raw_msgs = (List) root.get("messages");
    if (raw_msgs == null)  throw new MessageException("raw_msgs == null");

    String[] ids = new String[raw_msgs.size()];
    byte[] vers = new byte[raw_msgs.size()];

    int pos = 0;

    for (Iterator i = raw_msgs.iterator(); i.hasNext();) {
      Map msg = (Map) i.next();

      byte[] mid = (byte[]) msg.get("id");
      if (mid == null)  throw new MessageException("mid == null");
      ids[pos] = new String(mid);

      byte[] ver = (byte[]) msg.get("ver");
      if (ver == null)  throw new MessageException("ver == null");
      
      if (ver.length != 1)  throw new MessageException("ver.length != 1");
      vers[pos] = ver[0];

      pos++;
    }

    return new AZHandshake( id, name, version, tcp_lport.intValue(), udp_lport.intValue(), ids, vers, h_type.intValue() );
  }
  
  
  public void destroy() {
    if( buffer != null )  buffer.returnToPool();
  }
}
