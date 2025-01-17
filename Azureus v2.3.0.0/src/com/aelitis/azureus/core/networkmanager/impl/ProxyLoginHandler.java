/*
 * Created on Feb 1, 2005
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

package com.aelitis.azureus.core.networkmanager.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.networkmanager.*;
import com.aelitis.azureus.core.proxy.AEProxyFactory;


/**
 * Handles the process of proxy login/authentication/setup.
 */
public class ProxyLoginHandler {  
  
  public static final InetSocketAddress SOCKS_SERVER_ADDRESS;
  private static final String socks_version;
  private static final String socks_user;
  private static final String socks_password;
  

  static {
    boolean socks_same = COConfigurationManager.getBooleanParameter( "Proxy.Data.Same" );
    String socks_host = COConfigurationManager.getStringParameter( socks_same ? "Proxy.Host" : "Proxy.Data.Host" );
    int socks_port = 0;
    try{
      socks_port = Integer.parseInt( COConfigurationManager.getStringParameter( socks_same ? "Proxy.Port" : "Proxy.Data.Port" ) );
    }
    catch( Throwable e ){  Debug.printStackTrace(e);  }
    
    SOCKS_SERVER_ADDRESS = new InetSocketAddress( socks_host, socks_port );
    
    socks_version = COConfigurationManager.getStringParameter( "Proxy.Data.SOCKS.version" );
    socks_user  = COConfigurationManager.getStringParameter( socks_same ? "Proxy.Username" : "Proxy.Data.Username" );
    socks_password = COConfigurationManager.getStringParameter( socks_same ? "Proxy.Password" : "Proxy.Data.Password" );
  }
  
  
  private final TCPTransport proxy_connection;
  private final InetSocketAddress remote_address;  
  private final ProxyListener proxy_listener;
  
  private final String mapped_ip;
  private int socks5_handshake_phase = 0;
  private int socks5_address_length;
  
  private long read_start_time = 0;
  

  
  /**
   * Do proxy login.
   * @param proxy_connection transport connected to proxy server
   * @param remote_address address to proxy to
   * @param listener for proxy login success or faulure
   */
  public ProxyLoginHandler( TCPTransport proxy_connection, InetSocketAddress remote_address, ProxyListener listener ) {
    this.proxy_connection = proxy_connection;
    this.remote_address = remote_address;
    this.proxy_listener = listener;
       
    if ( remote_address.isUnresolved() || remote_address.getAddress() == null ){
      // deal with long "hostnames" that we get for, e.g., I2P destinations
    	mapped_ip = AEProxyFactory.getAddressMapper().internalise( remote_address.getHostName() ); 
    }
    else{
    	mapped_ip = remote_address.getAddress().getHostName();
    }
    
    if( socks_version.equals( "V4" ) ) {
      try{
        doSocks4Login( createSocks4Message() );
      }
      catch( Throwable t ) {
        Debug.out( t );
        proxy_listener.connectFailure( t );
      }
    }
    else if( socks_version.equals( "V4a" ) ) {
      try{
        doSocks4Login( createSocks4aMessage() );
      }
      catch( Throwable t ) {
        Debug.out( t );
        proxy_listener.connectFailure( t );
      }
    }
    else {  //"V5"
      doSocks5Login();
    }
    
  }


  private void doSocks4Login( final ByteBuffer[] data ) {
    try {
      //register for read ops
      NetworkManager.getSingleton().getReadSelector().register( proxy_connection.getSocketChannel(), new VirtualChannelSelector.VirtualSelectorListener() {
        public boolean selectSuccess( VirtualChannelSelector selector, SocketChannel sc,Object attachment ) {
          try {
            boolean finished = readMessage( data[1] );
            
            if( finished ) {
              NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
              parseSocks4Reply( data[1] );  //will throw exception on error
              proxy_listener.connectSuccess();
            }
            else {
              NetworkManager.getSingleton().getReadSelector().resumeSelects( proxy_connection.getSocketChannel() );  //resume read ops
            }
          }
          catch( Throwable t ) {
            Debug.out( t );
            NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
            proxy_listener.connectFailure( t );
          }
          return true;
        }
        
        public void selectFailure( VirtualChannelSelector selector, SocketChannel sc,Object attachment, Throwable msg ) {
          Debug.out( msg );
          NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
          proxy_listener.connectFailure( msg );
        }
      }, null );

      sendMessage( data[0] );
    }
    catch( Throwable t ) {
      Debug.out( t );
      NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
      proxy_listener.connectFailure( t );
    }
  }
  
  
  
  private void doSocks5Login() {
    try {
      final ArrayList data = new ArrayList(2);
      
      ByteBuffer[] header = createSocks5Message();
      data.add( header[0] );  //message
      data.add( header[1] );  //reply buff
      
      //register for read ops
      NetworkManager.getSingleton().getReadSelector().register( proxy_connection.getSocketChannel(), new VirtualChannelSelector.VirtualSelectorListener() {
        public boolean selectSuccess( VirtualChannelSelector selector, SocketChannel sc,Object attachment ) {
          try {
            boolean finished = readMessage( (ByteBuffer)data.get(1) );  
            
            if( finished ) {
              boolean done = parseSocks5Reply( (ByteBuffer)data.get(1) );  //will throw exception on error

              if( done ) {
                NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
                proxy_listener.connectSuccess();
              }
              else {
                ByteBuffer[] raw = createSocks5Message();
                data.set( 0, raw[0] );
                data.set( 1, raw[1] );                
                
                if( raw[0] != null )  sendMessage( raw[0] );
                NetworkManager.getSingleton().getReadSelector().resumeSelects( proxy_connection.getSocketChannel() );  //resume read ops
              }
            }
            else {
              NetworkManager.getSingleton().getReadSelector().resumeSelects( proxy_connection.getSocketChannel() );  //resume read ops
            }
          }
          catch( Throwable t ) {
            Debug.out( t );
            NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
            proxy_listener.connectFailure( t );
          }
          return true;
        }
        
        public void selectFailure( VirtualChannelSelector selector, SocketChannel sc,Object attachment, Throwable msg ) {
          Debug.out( msg );
          NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
          proxy_listener.connectFailure( msg );
        }
      }, null );

      sendMessage( (ByteBuffer)data.get(0) );  //send initial handshake to get things started
    }
    catch( Throwable t ) {
      Debug.out( t );
      NetworkManager.getSingleton().getReadSelector().cancel( proxy_connection.getSocketChannel() );
      proxy_listener.connectFailure( t );
    }
  }
  
  
  
  
  private void parseSocks4Reply( ByteBuffer reply ) throws IOException {
      byte ver = reply.get();
      byte resp = reply.get();

      if( ver != 0 || resp != 90 ) {
        throw new IOException( "SOCKS4(a) connection declined [" +ver+ "/" + resp + "]" );
      }
  }
  
  
  
  

  private void sendMessage( ByteBuffer msg ) throws IOException {
    long start_time = SystemTime.getCurrentTime();
    
    while( msg.hasRemaining() ) {
      if( proxy_connection.write( new ByteBuffer[]{ msg }, 0, 1 ) < 1 ) {
        if( SystemTime.getCurrentTime() - start_time > 30*1000 ) {
          String error = "proxy message send timed out after 30sec";
          Debug.out( error );
          throw new IOException( error );
        }
        
        try {   Thread.sleep( 10 );   }catch( Throwable t ) {t.printStackTrace();}
      }
    }
  }
  
  
  
  private boolean readMessage( ByteBuffer msg ) throws IOException {
    if( read_start_time == 0 )  read_start_time = SystemTime.getCurrentTime();
    
    proxy_connection.read( new ByteBuffer[]{ msg }, 0, 1 );
    
    if( !msg.hasRemaining() ) {
      msg.position( 0 );
      read_start_time = 0;  //reset for next round
      return true;
    }

    if( SystemTime.getCurrentTime() - read_start_time > 30*1000 ) {
      String error = "proxy message read timed out after 30sec";
      Debug.out( error );
      throw new IOException( error );
    }
    
    return false;
  }
  
  
  
  
  private ByteBuffer[] createSocks4Message() throws Exception {
    ByteBuffer handshake = ByteBuffer.allocate( 256 + mapped_ip.length() );

    handshake.put( (byte)4 ); // socks 4(a)
    handshake.put( (byte)1 ); // command = CONNECT
    handshake.putShort( (short)remote_address.getPort() );

    byte[] ip_bytes = HostNameToIPResolver.syncResolve( remote_address.getAddress().getHostAddress() ).getAddress();

    handshake.put( ip_bytes[ 0 ] );
    handshake.put( ip_bytes[ 1 ] );
    handshake.put( ip_bytes[ 2 ] );
    handshake.put( ip_bytes[ 3 ] );
    
    if( socks_user.length() > 0 ) {
      handshake.put( socks_user.getBytes() );
    }

    handshake.put( (byte)0 );
    
    handshake.flip();
   
    return new ByteBuffer[] { handshake, ByteBuffer.allocate( 8 ) };
  }
  
  
  
  private ByteBuffer[] createSocks4aMessage() {
    ByteBuffer handshake = ByteBuffer.allocate( 256 + mapped_ip.length() );

    handshake.put( (byte)4 ); // socks 4(a)
    handshake.put( (byte)1 ); // command = CONNECT
    handshake.putShort( (short)remote_address.getPort() ); // port
    handshake.put( (byte)0 );
    handshake.put( (byte)0 );
    handshake.put( (byte)0 );
    handshake.put( (byte)1 ); // indicates socks 4a

    if( socks_user.length() > 0 ) {
      handshake.put( socks_user.getBytes() );
    }

    handshake.put( (byte)0 );
    handshake.put( mapped_ip.getBytes() );
    handshake.put( (byte)0 );

    handshake.flip();
     
    return new ByteBuffer[] { handshake, ByteBuffer.allocate( 8 ) };
  }
  
  
   
  
  private ByteBuffer[] createSocks5Message() {
    ByteBuffer handshake = ByteBuffer.allocate( 256 + mapped_ip.length() );

    if( socks5_handshake_phase == 0 ) {  // say hello
      //System.out.println( "socks5 write phase 0" );
      
      handshake.put( (byte)5 ); // socks 5
      handshake.put( (byte)2 ); // 2 methods
      handshake.put( (byte)0 ); // no auth
      handshake.put( (byte)2 ); // user/pw

      handshake.flip();
      socks5_handshake_phase = 1;

      return new ByteBuffer[] { handshake, ByteBuffer.allocate( 2 ) };
    }
    
    if( socks5_handshake_phase == 1 ) {  // user/password auth
      //System.out.println( "socks5 write phase 1" );
      
      handshake.put( (byte)1 ); // user/pw version
      handshake.put( (byte)socks_user.length() ); // user length
      handshake.put( socks_user.getBytes() );
      handshake.put( (byte)socks_password.length() ); // password length
      handshake.put( socks_password.getBytes() );

      handshake.flip();
      socks5_handshake_phase = 2;

      return new ByteBuffer[] { handshake, ByteBuffer.allocate( 2 ) };
    }
    
    if( socks5_handshake_phase == 2 ) {  // request
      //System.out.println( "socks5 write phase 2" );
      
      handshake.put( (byte)5 ); // version
      handshake.put( (byte)1 ); // connect
      handshake.put( (byte)0 ); // reserved

      // use the maped ip for dns resolution so we don't leak the
      // actual address if this is a secure one (e.g. I2P one)
      try {
        byte[] ip_bytes = HostNameToIPResolver.syncResolve( mapped_ip ).getAddress();

        handshake.put( (byte)1 ); // IP4

        handshake.put( ip_bytes[ 0 ] );
        handshake.put( ip_bytes[ 1 ] );
        handshake.put( ip_bytes[ 2 ] );
        handshake.put( ip_bytes[ 3 ] );

      }
      catch (Throwable e) {
        handshake.put( (byte)3 );  // address type = domain name
        handshake.put( (byte)mapped_ip.length() );  // address type = domain name
        handshake.put( mapped_ip.getBytes() );
      }

      handshake.putShort( (short)remote_address.getPort() ); // port
      
      handshake.flip();
      socks5_handshake_phase = 3;

      return new ByteBuffer[] { handshake, ByteBuffer.allocate( 5 ) };
    }
    
    //System.out.println( "socks5 write phase 3..." );
    
    //reply has to be processed in two parts as it has variable length component at the end
    //socks5_handshake_phase == 3, part two
    socks5_handshake_phase = 4;
    return new ByteBuffer[] { null, ByteBuffer.allocate( socks5_address_length ) };    
  }
  
  
  
  private boolean parseSocks5Reply( ByteBuffer reply ) throws IOException {
    if( socks5_handshake_phase == 1 ) { // reply from hello
      //System.out.println( "socks5 read phase 1" );
      
      reply.get();  // version byte
      byte method = reply.get();

      if( method != 0 && method != 2 ) {
        throw new IOException( "SOCKS5 no valid method [" + method + "]" );
      }

      // no auth -> go to request phase
      if( method == 0 ) {
        socks5_handshake_phase = 2;
      }
      
      return false;
    }
    
    if( socks5_handshake_phase == 2 ) {  // reply from auth
      //System.out.println( "socks5 read phase 2" );
      
      reply.get();  // version byte
      byte status = reply.get();

      if( status != 0 ) {
        throw new IOException( "SOCKS authentication fails [" +status+ "]" );
      }

      return false;
    }
    
    
    if( socks5_handshake_phase == 3 ) {   // reply from request, first part
      //System.out.println( "socks5 read phase 3" );
      
      reply.get();  // version byte
      byte rep = reply.get();

      if( rep != 0 ) {
        String error_msgs[] = {
            "",
            "General SOCKS server failure",
            "connection not allowed by ruleset",
            "Network unreachable",
            "Host unreachable",
            "Connection refused",
            "TTL expired",
            "Command not supported",
            "Address type not supported" };
        String error_msg = rep < error_msgs.length ? error_msgs[ rep ] : "Unknown error";
        throw new IOException( "SOCKS request failure [" + error_msg + "/" + rep + "]" );
      }

      reply.get();  // reserved byte
      byte atype = reply.get();
      byte first_address_byte = reply.get();

      if( atype == 1 ) {
        socks5_address_length = 3; // already read one
      }
      else if( atype == 3 ) {
        socks5_address_length = first_address_byte;  // domain name, first byte gives length of remainder
      }
      else {
        socks5_address_length = 15; // already read one
      }

      socks5_address_length += 2;  // 2 bytes for port
      return false;
    }
    
    //System.out.println( "socks5 read phase 4..." );
    
    //socks5_handshake_phase 4
    //reply from request, last part
    return true;  //only done AFTER last part of request reply has been read from stream
  }
  
  

  
  public interface ProxyListener {
    /**
     * The proxied connection attempt succeeded.
     */
    public void connectSuccess() ;
    
    /**
     * The proxied connection attempt failed.
     * @param failure_msg failure reason
     */
    public void connectFailure( Throwable failure_msg );
  }
  
}
