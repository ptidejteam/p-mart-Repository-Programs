/*
 * Created on Jul 19, 2004
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

package com.aelitis.azureus.core.peermanager.utils;

import java.util.*;

import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.networkmanager.OutgoingMessageQueue;
import com.aelitis.azureus.core.peermanager.messaging.*;
import com.aelitis.azureus.core.peermanager.messaging.bittorrent.*;


/**
 * Front-end manager for handling requested outgoing bittorrent Piece messages.
 * Peers often make piece requests in batch, with multiple requests always
 * outstanding, all of which won't necessarily be honored (i.e. we choke them),
 * so we don't want to waste time reading in the piece data from disk ahead
 * of time for all the requests. Thus, we only want to perform read-aheads for a
 * small subset of the requested data at any given time, which is what this handler
 * does, before passing the messages onto the outgoing message queue for transmission.
 */
public class OutgoingBTPieceMessageHandler {
  private final OutgoingMessageQueue outgoing_message_queue;
  private final DiskManager disk_manager;
  
  private final LinkedList requests = new LinkedList();
  private final ArrayList	loading_messages = new ArrayList();
  private final HashMap  queued_messages = new HashMap();
  
  private final AEMonitor	lock_mon	= new AEMonitor( "OutgoingBTPieceMessageHandler:lock");
  private boolean destroyed = false;
  private int request_read_ahead = 2;


  
  
  
  
  private final DiskManagerReadRequestListener read_req_listener = new DiskManagerReadRequestListener() {
    public void readCompleted( DiskManagerReadRequest request, DirectByteBuffer data ) {
      try{
      	lock_mon.enter();

      	if( !loading_messages.contains( request ) || destroyed ) { //was canceled
      	  data.returnToPool();
      	  return;
      	}
      	loading_messages.remove( request );

        BTPiece msg = new BTPiece( request.getPieceNumber(), request.getOffset(), data );
        queued_messages.put( msg, request );

        outgoing_message_queue.addMessage( msg, true );    
      }
      finally{
      	lock_mon.exit();
      }

      outgoing_message_queue.doListenerNotifications();
    }
    
    public void 
    readFailed( 
    	DiskManagerReadRequest 	request, 
  		Throwable		 		cause )
    {
        try{
          	lock_mon.enter();

          	if( !loading_messages.contains( request ) || destroyed ) { //was canceled
          	  return;
          	}
          	loading_messages.remove( request );
          	
          }
          finally{
          	lock_mon.exit();
          }
    }
  };
  
  
  private final OutgoingMessageQueue.MessageQueueListener sent_message_listener = new OutgoingMessageQueue.MessageQueueListener() {
    public boolean messageAdded( Message message ) {   return true;   }
    
    public void messageSent( Message message ) {
      if( message.getID().equals( BTMessage.ID_BT_PIECE ) ) {
        try{
          lock_mon.enter();

          queued_messages.remove( message );
   
        }finally{
          lock_mon.exit();
        }
        
        doReadAheadLoads();
      }
    }
    public void messageQueued( Message message ) {/*nothing*/}
    public void messageRemoved( Message message ) {/*nothing*/}
    public void protocolBytesSent( int byte_count ) {/*ignore*/}
    public void dataBytesSent( int byte_count ) {/*ignore*/}
  };
  
  
  
  /**
   * Create a new handler for outbound piece messages,
   * reading piece data from the given disk manager
   * and transmitting the messages out the given message queue.
   * @param disk_manager
   * @param outgoing_message_q
   */
  public OutgoingBTPieceMessageHandler( DiskManager disk_manager, OutgoingMessageQueue outgoing_message_q ) {
    this.disk_manager = disk_manager;
    this.outgoing_message_queue = outgoing_message_q;
    outgoing_message_queue.registerQueueListener( sent_message_listener );
  }
  
  
  /**
   * Register a new piece data request.
   * @param piece_number
   * @param piece_offset
   * @param length
   */
  public void addPieceRequest( int piece_number, int piece_offset, int length ) {
    if( destroyed )  return;
         
    DiskManagerReadRequest dmr = disk_manager.createReadRequest( piece_number, piece_offset, length );

    try{
      lock_mon.enter();
    
      requests.addLast( dmr );
      
    }finally{
      lock_mon.exit();
    }
    
    doReadAheadLoads();
  }
  
  
  /**
   * Remove an outstanding piece data request.
   * @param piece_number
   * @param piece_offset
   * @param length
   */
  public void removePieceRequest( int piece_number, int piece_offset, int length ) {
  	if( destroyed )  return;
  	
    DiskManagerReadRequest dmr = disk_manager.createReadRequest( piece_number, piece_offset, length );
    
    try{
      lock_mon.enter();
    
      if( requests.contains( dmr ) ) {
        requests.remove( dmr );
        return;
      }
      
      if( loading_messages.contains( dmr ) ) {
        loading_messages.remove( dmr );
        return;
      }
      
      
      for( Iterator i = queued_messages.entrySet().iterator(); i.hasNext(); ) {
        Map.Entry entry = (Map.Entry)i.next();
        if( entry.getValue().equals( dmr ) ) {  //it's already been queued
          BTPiece msg = (BTPiece)entry.getKey();
          if( outgoing_message_queue.removeMessage( msg, true ) ) {
            i.remove();
          }
          break;  //do manual listener notify
        }
      }
    }
    finally{
      lock_mon.exit();
    }
    
    outgoing_message_queue.doListenerNotifications();
  }
  
  
  
  /**
   * Remove all outstanding piece data requests.
   */
  public void removeAllPieceRequests() {
  	if( destroyed )  return;
  	
    try{
      lock_mon.enter();
      
      String before_trace = outgoing_message_queue.getQueueTrace();  //TODO      
      
      int num_queued = queued_messages.size();
      int num_removed = 0;
      
      for( Iterator i = queued_messages.keySet().iterator(); i.hasNext(); ) {
        BTPiece msg = (BTPiece)i.next();
        if( outgoing_message_queue.removeMessage( msg, true ) ) {
          i.remove();
          num_removed++;
        }
      }
      
      if( num_removed < num_queued -2 ) {
        Debug.out( "num_removed[" +num_removed+ "] < num_queued[" +num_queued+ "]:\nBEFORE:\n" +before_trace+ "\nAFTER:\n" +outgoing_message_queue.getQueueTrace() );		
      }
      
      requests.clear();
      loading_messages.clear();
    }
    finally{
      lock_mon.exit();
    }
    
    outgoing_message_queue.doListenerNotifications();
  }
      

  
  public void setRequestReadAhead( int num_to_read_ahead ) {
    request_read_ahead = num_to_read_ahead;
  }
  
  
  
  public void destroy() {
    try{
      lock_mon.enter();
  
      removeAllPieceRequests();
      
      queued_messages.clear();
      
      destroyed = true;
    }
    finally{
      lock_mon.exit();
    }
  }
  
  
  private void doReadAheadLoads() {
  	List	to_submit = null;
  	try{
  		lock_mon.enter();
		
  		while( loading_messages.size() + queued_messages.size() < request_read_ahead && !requests.isEmpty() && !destroyed ) {
  			DiskManagerReadRequest dmr = (DiskManagerReadRequest)requests.removeFirst();
  			loading_messages.add( dmr );  			
  			if( to_submit == null )  to_submit = new ArrayList();
  			to_submit.add( dmr );
  		}	
    }finally{
    	lock_mon.exit();
    }
    
    if ( to_submit != null ){
    	for (int i=0;i<to_submit.size();i++){
    		disk_manager.enqueueReadRequest( (DiskManagerReadRequest)to_submit.get(i), read_req_listener );
    	}
    }
  }

  /**
	 * Get a list of piece numbers being requested
	 *  
	 * @return list of Long values
	 */
	public int[] getRequestedPieceNumbers() {
		if( destroyed )  return new int[0];
		
		/** Cheap hack to reduce (but not remove all) the # of duplicate entries */
		int iLastNumber = -1;
		int pos = 0;		
		int[] pieceNumbers;
	
		try {
			lock_mon.enter();			

			// allocate max size needed (we'll shrink it later)
			pieceNumbers = new int[queued_messages.size()	+ loading_messages.size() + requests.size()];

			for (Iterator iter = queued_messages.keySet().iterator(); iter.hasNext();) {
				BTPiece msg = (BTPiece) iter.next();
				if (iLastNumber != msg.getPieceNumber()) {
					iLastNumber = msg.getPieceNumber();
					pieceNumbers[pos++] = iLastNumber;
				}
			}

			for (Iterator iter = loading_messages.iterator(); iter.hasNext();) {
				DiskManagerReadRequest dmr = (DiskManagerReadRequest) iter.next();
				if (iLastNumber != dmr.getPieceNumber()) {
					iLastNumber = dmr.getPieceNumber();
					pieceNumbers[pos++] = iLastNumber;
				}
			}

			for (Iterator iter = requests.iterator(); iter.hasNext();) {
				DiskManagerReadRequest dmr = (DiskManagerReadRequest) iter.next();
				if (iLastNumber != dmr.getPieceNumber()) {
					iLastNumber = dmr.getPieceNumber();
					pieceNumbers[pos++] = iLastNumber;
				}
			}
			
		} finally {
			lock_mon.exit();
		}

		int[] trimmed = new int[pos];
		System.arraycopy(pieceNumbers, 0, trimmed, 0, pos);

		return trimmed;		
	}
	
}
