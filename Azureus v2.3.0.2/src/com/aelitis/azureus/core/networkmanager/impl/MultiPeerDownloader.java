/*
 * Created on Apr 22, 2005
 * Created by Alon Rohter
 * Copyright (C) 2005 Aelitis, All Rights Reserved.
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

import java.util.*;

import org.gudy.azureus2.core3.util.AEDiagnostics;
import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.Debug;

import com.aelitis.azureus.core.networkmanager.*;


/**
 * 
 */
public class MultiPeerDownloader implements RateControlledEntity {
  
  private volatile ArrayList connections_cow = new ArrayList();  //copied-on-write
  private final AEMonitor connections_mon = new AEMonitor( "MultiPeerDownloader" );
  
  private final RateHandler main_handler;
  private int next_position = 0;
  
  
  /**
   * Create new downloader using the given "global" rate handler to limit all peers managed by this downloader.
   * @param main_handler
   */
  public MultiPeerDownloader( RateHandler main_handler ) {
    this.main_handler = main_handler;
  }
  
  
  /**
   * Add the given connection to the downloader.
   * @param connection to add
   */
  public void addPeerConnection( NetworkConnection connection ) {
    try {  connections_mon.enter();
      //copy-on-write
      ArrayList conn_new = new ArrayList( connections_cow.size() + 1 );
      conn_new.addAll( connections_cow );
      conn_new.add( connection );
      connections_cow = conn_new;
    }
    finally{ connections_mon.exit();  }
  }
  
  
  /**
   * Remove the given connection from the downloader.
   * @param connection to remove
   * @return true if the connection was found and removed, false if not removed
   */
  public boolean removePeerConnection( NetworkConnection connection ) {
    try {  connections_mon.enter();
      //copy-on-write
      ArrayList conn_new = new ArrayList( connections_cow );
      boolean removed = conn_new.remove( connection );
      if( !removed ) return false;
      connections_cow = conn_new;
      return true;
    }
    finally{ connections_mon.exit();  }
  }
  


  
  public boolean canProcess() {
    if( main_handler.getCurrentNumBytesAllowed() < 1/*NetworkManager.getTcpMssSize()*/ )  return false;

    return true;
  }

  
  
  
  public boolean doProcessing() {
    int num_bytes_allowed = main_handler.getCurrentNumBytesAllowed();
    if( num_bytes_allowed < 1 )  return false;

    ArrayList connections = connections_cow;
    int num_checked = 0;
    int num_bytes_remaining = num_bytes_allowed;

    while( num_bytes_remaining > 0 && num_checked < connections.size() ) {
      next_position = next_position >= connections.size() ? 0 : next_position;  //make circular
      
      NetworkConnection connection = (NetworkConnection)connections.get( next_position );
      next_position++;
      num_checked++;
      
      if( connection.getTCPTransport().isReadyForRead() ) {
        int allowed = num_bytes_remaining > NetworkManager.getTcpMssSize() ? NetworkManager.getTcpMssSize() : num_bytes_remaining;
          
        int bytes_read = 0;
          
        try {
          bytes_read = connection.getIncomingMessageQueue().receiveFromTransport( allowed );
        }
        catch( Throwable e ) {
          
          if( AEDiagnostics.TRACE_CONNECTION_DROPS ) {
            if( e.getMessage() == null ) {
              Debug.out( "null read exception message: ", e );
            }
            else {
              if( e.getMessage().indexOf( "end of stream on socket read" ) == -1 &&
                  e.getMessage().indexOf( "An existing connection was forcibly closed by the remote host" ) == -1 &&
                  e.getMessage().indexOf( "Connection reset by peer" ) == -1 &&
                  e.getMessage().indexOf( "An established connection was aborted by the software in your host machine" ) == -1 ) {
                  
                System.out.println( "MP: read exception [" +connection.getTCPTransport().getDescription()+ "]: " +e.getMessage() );
              }
            }
          }

          connection.notifyOfException( e );
        }

        num_bytes_remaining -= bytes_read;
      }
    }
    
    int total_bytes_read = num_bytes_allowed - num_bytes_remaining;
    if( total_bytes_read > 0 ) {
      main_handler.bytesProcessed( total_bytes_read );
      return true;
    }

    return false;  //zero bytes read
  }

  
  public int getPriority() {  return RateControlledEntity.PRIORITY_HIGH;  }
  

}
