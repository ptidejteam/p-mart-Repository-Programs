/*
 * Created on May 8, 2004
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


package com.aelitis.azureus.core.networkmanager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

import com.aelitis.azureus.core.peermanager.messages.ProtocolMessage;


/**
 * Priority-based outbound peer message queue.
 */
public class OutgoingMessageQueue {
  private final LinkedList 		queue		= new LinkedList();
  private final AEMonitor	queue_mon	= new AEMonitor( "OutgoingMessageQueue:queue" );

  private final ArrayList delayed_notifications = new ArrayList();
  private final AEMonitor delayed_notifications_mon = new AEMonitor( "OutgoingMessageQueue:DN" );

  private volatile ArrayList listeners 		= new ArrayList();  //copied-on-write
  private final AEMonitor listeners_mon		= new AEMonitor( "OutgoingMessageQueue:L");
  
  private int total_size = 0;
  private ProtocolMessage urgent_message = null;
  private boolean destroyed = false;
  
  
  /**
   * Create a new message queue.
   */
  protected OutgoingMessageQueue() {
    //nothing
  }
  

  /**
   * Destroy this queue; i.e. perform cleanup actions.
   */
  protected void destroy() {
    destroyed = true;
    try{
      queue_mon.enter();
    
      while( !queue.isEmpty() ) {
      	((ProtocolMessage)queue.remove( 0 )).destroy();
      }
    }finally{
      queue_mon.exit();
    }
    total_size = 0;
  }
  
  
  /**
   * Get the total number of bytes ready to be transported.
   * @return total bytes remaining
   */
  public int getTotalSize() {  return total_size;  }
  
  
  /**
   * Whether or not an urgent message (one that needs an immediate send, i.e. a no-delay message) is queued.
   * @return true if there's a message tagged for immediate write
   */
  public boolean hasUrgentMessage() {  return urgent_message == null ? false : true;  }
  
  
  /**
   * Add a message to the message queue.
   * NOTE: Allows for manual listener notification at some later time,
   * using doListenerNotifications(), instead of notifying immediately
   * from within this method.  This is useful if you want to invoke
   * listeners outside of some greater synchronized block to avoid
   * deadlock.
   * @param message message to add
   * @param manual_listener_notify true for manual notification, false for automatic
   */
  public void addMessage( ProtocolMessage message, boolean manual_listener_notify ) {
    
    if( destroyed ) {  //queue is shutdown, drop any added messages
      message.destroy();
      return;
    }
    
    removeMessagesOfType( message.typesToRemove(), manual_listener_notify );
    try{
      queue_mon.enter();
    
      int pos = 0;
      for( Iterator i = queue.iterator(); i.hasNext(); ) {
        ProtocolMessage msg = (ProtocolMessage)i.next();
        if( message.getPriority() > msg.getPriority() 
            && msg.getPayload().position(DirectByteBuffer.SS_NET) == 0 ) {  //but don't insert in front of a half-sent message
          break;
        }
        pos++;
      }
      if( message.isNoDelay() ) {
        urgent_message = message;
      }
      queue.add( pos, message );
      total_size += message.getPayload().remaining(DirectByteBuffer.SS_NET);
    }finally{
      queue_mon.exit();
    }
    
    if( manual_listener_notify ) {  //register listener event for later, manual notification
      NotificationItem item = new NotificationItem( NotificationItem.MESSAGE_ADDED );
      item.message = message;
      try {
        delayed_notifications_mon.enter();
        
        delayed_notifications.add( item );
      }
      finally {
        delayed_notifications_mon.exit();
      }
    }
    else { //do listener notification now
      ArrayList listeners_ref = listeners;
    
      for( int i=0; i < listeners_ref.size(); i++ ) {
        MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
        listener.messageAdded( message );
      }
    }
  }
  
  
  /**
   * Remove all messages of the given types from the queue.
   * NOTE: Allows for manual listener notification at some later time,
   * using doListenerNotifications(), instead of notifying immediately
   * from within this method.  This is useful if you want to invoke
   * listeners outside of some greater synchronized block to avoid
   * deadlock.
   * @param message_types type to remove
   * @param manual_listener_notify true for manual notification, false for automatic
   */
  public void removeMessagesOfType( int[] message_types, boolean manual_listener_notify ) {
    if( message_types == null ) return;
    
    ArrayList messages_removed = null;
    
    try{
      queue_mon.enter();
    
      for( Iterator i = queue.iterator(); i.hasNext(); ) {
        ProtocolMessage msg = (ProtocolMessage)i.next();
        for( int t=0; t < message_types.length; t++ ) {
        	if( msg.getType() == message_types[ t ] && msg.getPayload().position(DirectByteBuffer.SS_NET) == 0 ) {   //dont remove a half-sent message
            if( msg == urgent_message ) urgent_message = null;            
            total_size -= msg.getPayload().remaining(DirectByteBuffer.SS_NET);
            if( manual_listener_notify ) {
              NotificationItem item = new NotificationItem( NotificationItem.MESSAGE_REMOVED );
              item.message = msg;
              try {
                delayed_notifications_mon.enter();
                
                delayed_notifications.add( item );
              }
              finally {
                delayed_notifications_mon.exit();
              }
            }
            else {
              if ( messages_removed == null ){
              	messages_removed = new ArrayList();
              }
              messages_removed.add( msg );
            }
        		i.remove();
            break;
        	}
        }
      }
    }finally{
      queue_mon.exit();
    }

    if( !manual_listener_notify && messages_removed != null ) {
      //do listener notifications now
      ArrayList listeners_ref = listeners;
        
      for( int x=0; x < messages_removed.size(); x++ ) {
        ProtocolMessage msg = (ProtocolMessage)messages_removed.get( x );
        
        for( int i=0; i < listeners_ref.size(); i++ ) {
          MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
          listener.messageRemoved( msg );
        }
        msg.destroy();
      }
    }
  }
  
  
  /**
   * Remove a particular message from the queue.
   * NOTE: Only the original message found in the queue will be destroyed upon removal,
   * which may not necessarily be the one passed as the method parameter,
   * as some messages override equals() (i.e. BTRequest messages) instead of using reference
   * equality, and could be a completely different object, and would need to be destroyed
   * manually.
   * NOTE: Allows for manual listener notification at some later time,
   * using doListenerNotifications(), instead of notifying immediately
   * from within this method.  This is useful if you want to invoke
   * listeners outside of some greater synchronized block to avoid
   * deadlock.
   * @param message to remove
   * @param manual_listener_notify true for manual notification, false for automatic
   * @return true if the message was removed, false otherwise
   */
  public boolean removeMessage( ProtocolMessage message, boolean manual_listener_notify ) {
    ProtocolMessage msg_removed = null;
    
    try{
      queue_mon.enter();
    
      int index = queue.indexOf( message );
      if( index != -1 ) {
        ProtocolMessage msg = (ProtocolMessage)queue.get( index );
        if( msg.getPayload().position(DirectByteBuffer.SS_NET) == 0 ) {  //dont remove a half-sent message
          if( msg == urgent_message ) urgent_message = null;  
          total_size -= msg.getPayload().remaining(DirectByteBuffer.SS_NET);
          queue.remove( index );
          msg_removed = msg;
        }
      }
    }finally{
      queue_mon.exit();
    }
    
    
    if( msg_removed != null ) {
      if( manual_listener_notify ) { //delayed manual notification
        NotificationItem item = new NotificationItem( NotificationItem.MESSAGE_REMOVED );
        item.message = msg_removed;
        try {
          delayed_notifications_mon.enter();
          
          delayed_notifications.add( item );
        }
        finally {
          delayed_notifications_mon.exit();
        }
      }
      else {   //do listener notification now
        ArrayList listeners_ref = listeners;
      
        for( int i=0; i < listeners_ref.size(); i++ ) {
          MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
          listener.messageRemoved( msg_removed );
        }
        msg_removed.destroy();
      }
      return true;
    }
    
    return false;
  }
  
  
  /**
   * Deliver (write) message(s) data to the given transport.
   * 
   * NOTE: Allows for manual listener notification at some later time,
   * using doListenerNotifications(), instead of notifying immediately
   * from within this method.  This is useful if you want to invoke
   * listeners outside of some greater synchronized block to avoid
   * deadlock.
   * @param transport to transmit over 
   * @param max_bytes maximum number of bytes to deliver
   * @param manual_listener_notify true for manual notification, false for automatic
   * @return number of bytes delivered
   * @throws IOException
   */
  protected int deliverToTransport( Transport transport, int max_bytes, boolean manual_listener_notify ) throws IOException {    
    if( max_bytes < 1 ) {
      Debug.out( "max_bytes < 1: " +max_bytes );
      return 0;
    }
    
    int data_written = 0;
    int protocol_written = 0;
    
    ArrayList messages_sent = null;
    
    try{
      queue_mon.enter();

    	if( !queue.isEmpty() ) {
        ByteBuffer[] buffers = new ByteBuffer[ queue.size() ];
        int[] starting_pos = new int[ queue.size() ];
        int pos = 0;
    		int total_sofar = 0;
        while( total_sofar < max_bytes && pos < buffers.length ) {
          buffers[ pos ] = ((ProtocolMessage)queue.get( pos )).getPayload().getBuffer(DirectByteBuffer.SS_NET);
          total_sofar += buffers[ pos ].remaining();
          starting_pos[ pos ] = buffers[ pos ].position();
          pos++;
    		}
        pos--; //remove last while loop auto-increment
    		int orig_limit = buffers[ pos ].limit();
    		if( total_sofar > max_bytes ) {
    			buffers[ pos ].limit( orig_limit - (total_sofar - max_bytes) );
    		}
             
        transport.write( buffers, 0, pos + 1 );

        buffers[ pos ].limit( orig_limit );
        pos = 0;
        while( !queue.isEmpty() ) {
          ProtocolMessage msg = (ProtocolMessage)queue.get( 0 );
          ByteBuffer bb = msg.getPayload().getBuffer(DirectByteBuffer.SS_NET);
          if( !bb.hasRemaining() ) {
            if( msg == urgent_message ) urgent_message = null;
            
            int bytes_written = bb.limit() - starting_pos[ pos ];
            total_size -= bytes_written;
            
            if( msg.isDataMessage() ) {
              data_written += bytes_written;
            }
            else {
              protocol_written += bytes_written;
            }
            
            queue.remove( 0 );
            if( manual_listener_notify ) {
              NotificationItem item = new NotificationItem( NotificationItem.MESSAGE_SENT );
              item.message = msg;
              item.transport = transport;
              try {
                delayed_notifications_mon.enter();
                
                delayed_notifications.add( item );
              }
              finally {
                delayed_notifications_mon.exit();
              }
            }
            else {
              if ( messages_sent == null ){
              	
              	messages_sent = new ArrayList();
              }
              
              messages_sent.add( msg );
            }
          }
          else {           
            int bytes_written = (bb.limit() - bb.remaining()) - starting_pos[ pos ];
            total_size -= bytes_written;
            
            if( msg.isDataMessage() ) {
              data_written += bytes_written;
            }
            else {
              protocol_written += bytes_written;
            }
            
            break;
          }
          pos++;
        }
    	}
    }finally{
      queue_mon.exit();
    }
    
    if( data_written + protocol_written > 0 ) {
      if( manual_listener_notify ) {
        
        if( data_written > 0 ) {  //data bytes notify
          NotificationItem item = new NotificationItem( NotificationItem.DATA_BYTES_SENT );
          item.byte_count = data_written;
          try {
            delayed_notifications_mon.enter();
            
            delayed_notifications.add( item );
          }
          finally {
            delayed_notifications_mon.exit();
          }
        }

        if( protocol_written > 0 ) {  //protocol bytes notify
          NotificationItem item = new NotificationItem( NotificationItem.PROTOCOL_BYTES_SENT );
          item.byte_count = protocol_written;
          try {
            delayed_notifications_mon.enter();
            
            delayed_notifications.add( item );
          }
          finally {
            delayed_notifications_mon.exit();
          }
        }
      }
      else {  //do listener notification now
        ArrayList listeners_ref = listeners;
        
        int num_listeners = listeners_ref.size();
        for( int i=0; i < num_listeners; i++ ) {
          MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );

          if( data_written > 0 )  listener.dataBytesSent( data_written );
          if( protocol_written > 0 )  listener.protocolBytesSent( protocol_written );
          
          if ( messages_sent != null ){
          	
	          for( int x=0; x < messages_sent.size(); x++ ) {
	            ProtocolMessage msg = (ProtocolMessage)messages_sent.get( x );
	
	            listener.messageSent( msg );
	            
	            if( i == num_listeners - 1 ) {  //the last listener notification, so destroy
	              LGLogger.log( LGLogger.CORE_NETWORK, "Sent " +msg.getDescription()+ " message to " + transport.getDescription() );
	              msg.destroy();
	            }
	          }
          }
        }
      }
    }
    
    return data_written + protocol_written;
  }
  
  
  /**
   * Manually send any unsent listener notifications.
   */
  public void doListenerNotifications() {
    ArrayList notifications_copy;
    try {
      delayed_notifications_mon.enter();
      
      if( delayed_notifications.size() == 0 )  return;
      notifications_copy = new ArrayList( delayed_notifications );
      delayed_notifications.clear();
    }
    finally {
      delayed_notifications_mon.exit();
    }
    
    ArrayList listeners_ref = listeners;
    
    for( int j=0; j < notifications_copy.size(); j++ ) {  //for each notification
      NotificationItem item = (NotificationItem)notifications_copy.get( j );

      switch( item.type ) {
        case NotificationItem.MESSAGE_ADDED:
          for( int i=0; i < listeners_ref.size(); i++ ) {  //for each listener
            MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
            listener.messageAdded( item.message );
          }
          break;
          
        case NotificationItem.MESSAGE_REMOVED:
          for( int i=0; i < listeners_ref.size(); i++ ) {  //for each listener
            MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
            listener.messageRemoved( item.message );
          }
          item.message.destroy();
          break;
          
        case NotificationItem.MESSAGE_SENT:
          for( int i=0; i < listeners_ref.size(); i++ ) {  //for each listener
            MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
            listener.messageSent( item.message );
          }
          LGLogger.log( LGLogger.CORE_NETWORK, "Sent " +item.message.getDescription()+ " message to " + item.transport.getDescription() );
          item.message.destroy();
          break;
          
        case NotificationItem.PROTOCOL_BYTES_SENT:
          for( int i=0; i < listeners_ref.size(); i++ ) {  //for each listener
            MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
            listener.protocolBytesSent( item.byte_count );
          }
          break;
          
        case NotificationItem.DATA_BYTES_SENT:
          for( int i=0; i < listeners_ref.size(); i++ ) {  //for each listener
            MessageQueueListener listener = (MessageQueueListener)listeners_ref.get( i );
            listener.dataBytesSent( item.byte_count );
          }
          break;
          
        default:
          Debug.out( "NotificationItem.type unknown :" + item.type );
      }
    }
  }
  

  /////////////////////////////////////////////////////////////////
  
  /**
   * Receive notification when a new message is added to the queue.
   */
  public interface MessageQueueListener {
    /**
     * The given message has just been queued for sending out the transport.
     * @param message queued
     */
    public void messageAdded( ProtocolMessage message );
    
    /**
     * The given message has just been forcibly removed from the queue,
     * i.e. it was *not* sent out the transport.
     * @param message removed
     */
    public void messageRemoved( ProtocolMessage message );
    
    /**
     * The given message has been completely sent out through the transport.
     * @param message sent
     */
    public void messageSent( ProtocolMessage message );
    
    /**
     * The given number of protocol (overhead) bytes has been written to the transport.
     * @param byte_count number of protocol bytes
     */
    public void protocolBytesSent( int byte_count );
    
    
    /**
     * The given number of (piece) data bytes has been written to the transport.
     * @param byte_count number of data bytes
     */
    public void dataBytesSent( int byte_count );
  }
  

  
  /**
   * Add a listener to be notified of queue events.
   * @param listener
   */
  public void registerQueueListener( MessageQueueListener listener ) {
    try{  listeners_mon.enter();
      //copy-on-write
      ArrayList new_list = new ArrayList( listeners.size() + 1 );
      new_list.addAll( listeners );
      new_list.add( listener );
      listeners = new_list;
    }
    finally{  listeners_mon.exit();  }
  }
  
  
  /**
   * Cancel queue event notification listener.
   * @param listener
   */
  public void cancelQueueListener( MessageQueueListener listener ) {
    try{  listeners_mon.enter();
      //copy-on-write
      ArrayList new_list = new ArrayList( listeners );
      new_list.remove( listener );
      listeners = new_list;
    }
    finally{  listeners_mon.exit();  }
  }

  
  private static class NotificationItem {
    private static final int MESSAGE_ADDED        = 0;
    private static final int MESSAGE_REMOVED      = 1;
    private static final int MESSAGE_SENT         = 2;
    private static final int DATA_BYTES_SENT      = 3;
    private static final int PROTOCOL_BYTES_SENT  = 4;
    private final int type;
    private ProtocolMessage message;
    private Transport transport;
    private int byte_count = 0;
    private NotificationItem( int notification_type ) {
      type = notification_type;
    }
  }

}
