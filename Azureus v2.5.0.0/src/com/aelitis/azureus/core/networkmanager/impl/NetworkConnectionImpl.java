/*
 * Created on Jul 29, 2004
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


import java.nio.ByteBuffer;

import org.gudy.azureus2.core3.util.AddressUtils;
import org.gudy.azureus2.core3.util.Debug;


import com.aelitis.azureus.core.networkmanager.*;
import com.aelitis.azureus.core.peermanager.messaging.MessageStreamDecoder;
import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;



/**
 *
 */
public class NetworkConnectionImpl implements NetworkConnection {
  private final ConnectionEndpoint	connection_endpoint;
  
  private boolean connect_with_crypto;
  private boolean allow_fallback;
  private byte[] shared_secret;
  
  private ConnectionListener connection_listener;
  private boolean 	is_connected;
  private byte		is_lan_local	= AddressUtils.LAN_LOCAL_MAYBE;

  private final OutgoingMessageQueue outgoing_message_queue;
  private final IncomingMessageQueue incoming_message_queue;
  
  private Transport	transport;
  
  private volatile ConnectionAttempt	connection_attempt;
  private volatile boolean				closed;
  
  
  /**
   * Constructor for new OUTbound connection.
   * The connection is not yet established upon instantiation; use connect() to do so.
   * @param _remote_address to connect to
   * @param encoder default message stream encoder to use for the outgoing queue
   * @param decoder default message stream decoder to use for the incoming queue
   */
  public NetworkConnectionImpl( 
		  		ConnectionEndpoint _target, MessageStreamEncoder encoder, 
		  		MessageStreamDecoder decoder, boolean _connect_with_crypto, boolean _allow_fallback,
		  		byte[] _shared_secret ) 
  {
	connection_endpoint	= _target;
    connect_with_crypto	= _connect_with_crypto;
    allow_fallback = _allow_fallback;
    shared_secret = _shared_secret;
    
    
    is_connected = false;
    outgoing_message_queue = new OutgoingMessageQueue( encoder );
    incoming_message_queue = new IncomingMessageQueue( decoder, this );
  }
  
  
  /**
   * Constructor for new INbound connection.
   * The connection is assumed to be already established, by the given already-connected channel.
   * @param _remote_channel connected by
   * @param data_already_read bytestream already read during routing
   * @param encoder default message stream encoder to use for the outgoing queue
   * @param decoder default message stream decoder to use for the incoming queue
   */
  public NetworkConnectionImpl( Transport _transport, MessageStreamEncoder encoder, MessageStreamDecoder decoder ) {
    transport = _transport;
    connection_endpoint = transport.getTransportEndpoint().getProtocolEndpoint().getConnectionEndpoint();
    is_connected = true;
    outgoing_message_queue = new OutgoingMessageQueue( encoder );
    outgoing_message_queue.setTransport( transport );
    incoming_message_queue = new IncomingMessageQueue( decoder, this );
  }
  

  public ConnectionEndpoint
  getEndpoint()
  {
	  return( connection_endpoint );
  }
  
  public void connect( ConnectionListener listener ) {
	  connect( null, listener );
  }
  
  public void connect( ByteBuffer initial_outbound_data, ConnectionListener listener ) {
    this.connection_listener = listener;
    
    if( is_connected ){
    	
      connection_listener.connectStarted();
      
      connection_listener.connectSuccess( initial_outbound_data );
      
      return;
    }
    
    if ( connection_attempt != null ){
    	
    	Debug.out( "Connection attempt already active" );
    	
    	listener.connectFailure( new Throwable( "Connection attempt already active" ));
    	
    	return;
    }
    
    connection_attempt = 
    	connection_endpoint.connectOutbound( 
    			connect_with_crypto, 
    			allow_fallback, 
    			shared_secret, 
    			initial_outbound_data,
    			new Transport.ConnectListener() {
			      public void connectAttemptStarted() {
			        connection_listener.connectStarted();
			      }
			      
			      public void connectSuccess( Transport	_transport, ByteBuffer remaining_initial_data ) {
			        is_connected = true;
			        transport	= _transport;
			        outgoing_message_queue.setTransport( transport );
			        connection_listener.connectSuccess( remaining_initial_data );
			        connection_attempt	= null;
			      }
			      
			      public void connectFailure( Throwable failure_msg ) {
			        is_connected = false;
			        connection_listener.connectFailure( failure_msg );
			      }
			    });
    
    if ( closed ){
    	
    	ConnectionAttempt	ca = connection_attempt;
    	
    	if ( ca != null ){
    		
    		ca.abandon();
    	}
    }
  }
  

  
  public void close() {
  	NetworkManager.getSingleton().stopTransferProcessing( this );   
  	closed	= true;
    if ( connection_attempt != null ){
    	connection_attempt.abandon();
    }
    if ( transport != null ){
    	transport.close( "Tidy close" );
    }
    incoming_message_queue.destroy();
    outgoing_message_queue.destroy();  
    is_connected = false;
  }
  

  public void notifyOfException( Throwable error ) {
    if( connection_listener != null ) {
      connection_listener.exceptionThrown( error );
    }
    else {
      Debug.out( "notifyOfException():: connection_listener == null for exception: " +error.getMessage() );
    }
  }
  

  public OutgoingMessageQueue getOutgoingMessageQueue() {  return outgoing_message_queue;  }

  public IncomingMessageQueue getIncomingMessageQueue() {  return incoming_message_queue;  }
  

  public void startMessageProcessing( LimitedRateGroup upload_group, LimitedRateGroup download_group ) {
  	NetworkManager.getSingleton().startTransferProcessing( this, upload_group, download_group );
  }
  
  
  public void enableEnhancedMessageProcessing( boolean enable ) {
    if( enable ) {
    	NetworkManager.getSingleton().upgradeTransferProcessing( this );
    }
    else {
      NetworkManager.getSingleton().downgradeTransferProcessing( this );
    }
  }
  

  public Transport getTransport() {  return transport;  }
  
  public int
  getMssSize()
  {
	  if ( transport == null ){
		  
		  return( NetworkManager.getMinMssSize());
		  
	  }else{
		  
		  return( transport.getMssSize());
	  }
  }
  
  public String toString() {
    return( transport==null?connection_endpoint.getDescription():transport.getDescription() );
  }


	public boolean isConnected() {
		return is_connected;
	}
  
	
	public boolean isLANLocal() {
		if ( is_lan_local == AddressUtils.LAN_LOCAL_MAYBE ){
			
			is_lan_local = AddressUtils.isLANLocalAddress( connection_endpoint.getNotionalAddress());
		}
		return( is_lan_local == AddressUtils.LAN_LOCAL_YES );
	}
	
}
