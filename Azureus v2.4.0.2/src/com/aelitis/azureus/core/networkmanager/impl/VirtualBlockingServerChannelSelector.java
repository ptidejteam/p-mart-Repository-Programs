/*
 * Created on Dec 4, 2004
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

package com.aelitis.azureus.core.networkmanager.impl;

import java.net.*;
import java.nio.channels.*;

import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.networkmanager.VirtualServerChannelSelector;



/**
 * Virtual server socket channel for listening and accepting incoming connections.
 */
public class VirtualBlockingServerChannelSelector
	implements VirtualServerChannelSelector{
	private static final LogIDs LOGID = LogIDs.NWMAN;
  private ServerSocketChannel server_channel = null;
  private final InetSocketAddress bind_address;
  private final int receive_buffer_size;
  private final SelectListener listener;
  
  protected AEMonitor	this_mon	= new AEMonitor( "VirtualServerChannelSelector" );

  private long last_accept_time;
  
  
  /**
   * Create a new server listening on the given address and reporting to the given listener.
   * @param bind_address ip+port to listen on
   * @param so_rcvbuf_size new socket receive buffer size
   * @param listener to notify of incoming connections
   */
  public VirtualBlockingServerChannelSelector( InetSocketAddress _bind_address, int so_rcvbuf_size, SelectListener listener ) {
    this.bind_address = _bind_address;
    this.receive_buffer_size = so_rcvbuf_size;
    this.listener = listener;
  }
  
  
  /**
   * Start the server and begin accepting incoming connections.
   * 
   */
  public void start() {
  	try{
  		this_mon.enter();
  	
	    if( !isRunning() ) {
	      try {
	        server_channel = ServerSocketChannel.open();
	        
	        server_channel.socket().setReuseAddress( true );
	        if( receive_buffer_size > 0 )  server_channel.socket().setReceiveBufferSize( receive_buffer_size );
	        
	        server_channel.socket().bind( bind_address, 1024 );
	        
	        if (Logger.isEnabled()) 	Logger.log(new LogEvent(LOGID, "TCP incoming server socket "	+ bind_address));
	        
	        AEThread accept_thread = new AEThread( "VServerSelector:port" + bind_address.getPort() ) {
	          public void runSupport() {
	            accept_loop();
	          }
	        };
	        accept_thread.setDaemon( true );
	        accept_thread.start();  
	      }
	      catch( Throwable t ) {
	      	Debug.out( t );
	      	Logger.log(new LogAlert(LogAlert.UNREPEATABLE,	"ERROR, unable to bind TCP incoming server socket to " +bind_address.getPort(), t));
	      }
	      
	      last_accept_time = SystemTime.getCurrentTime();  //init to now
	    }
  	}finally{
  		
  		this_mon.exit();
  	} 	
  }
  
  
  /**
   * Stop the server.
   */
  public void stop() {
  	try{
  		this_mon.enter();

	    if( server_channel != null ) {
	      try {
	        server_channel.close();
	        server_channel = null;
	      }
	      catch( Throwable t ) {  Debug.out( t );  }
	    }
  	}finally{
  		
  		this_mon.exit();
  	}
  }
  
  
  protected void accept_loop() {
    while( isRunning() ) {
      try {
        SocketChannel client_channel = server_channel.accept();
        last_accept_time = SystemTime.getCurrentTime();
        client_channel.configureBlocking( false );
        listener.newConnectionAccepted( server_channel, client_channel );
      }
      catch( AsynchronousCloseException e ) {
        /* is thrown when stop() is called */
      }
      catch( Throwable t ) {
        Debug.out( t );
        try {  Thread.sleep( 500 );  }catch( Exception e ) {  e.printStackTrace();  }
      }
      
    }
  }
  
  
  /**
   * Is this selector actively running
   * @return true if enabled, false if not running
   */
  public boolean isRunning() {
  	if( server_channel != null && server_channel.isOpen() )  return true;
  	return false;
  }
  
  
  public InetAddress getBoundToAddress() {
  	if( server_channel != null ) {
  		return server_channel.socket().getInetAddress();
  	}
  	return null;
  }
  
  
  public long getTimeOfLastAccept() {
  	return last_accept_time;
  }
}
