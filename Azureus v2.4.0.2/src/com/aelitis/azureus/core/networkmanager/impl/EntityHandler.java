/*
 * Created on Sep 23, 2004
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

import java.util.*;

import org.gudy.azureus2.core3.util.AEMonitor;
import org.gudy.azureus2.core3.util.Debug;

import com.aelitis.azureus.core.networkmanager.*;


/**
 * Manages transfer entities on behalf of peer connections.
 * Each entity handler has a global pool which manages all
 * connections by default.  Connections can also be "upgraded"
 * to a higher connection control level, i.e. each connection
 * has its own specialized entity for performance purposes.
 */
public class EntityHandler {
  private final HashMap upgraded_connections = new HashMap();
  private final AEMonitor lock = new AEMonitor( "EntityHandler" );
  private final MultiPeerUploader global_uploader;
  private final MultiPeerDownloader global_downloader;
  private boolean global_registered = false;
  private final int handler_type;
  
  
  /**
   * Create a new entity handler using the given rate handler.
   * @param type read or write type handler
   * @param rate_handler global max rate handler
   */
  public EntityHandler( int type, RateHandler rate_handler ) {
    this.handler_type = type;
    if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
      global_uploader = new MultiPeerUploader( rate_handler );
      global_downloader = null;
    }
    else {  //download type
      global_downloader = new MultiPeerDownloader( rate_handler );
      global_uploader = null;
    }
  }
  

  
  /**
   * Register a peer connection for management by the handler.
   * @param connection to add to the global pool
   */
  public void registerPeerConnection( NetworkConnection connection ) {
    try {  lock.enter();
      if( !global_registered ) {
        if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
          NetworkManager.getSingleton().addWriteEntity( global_uploader );  //register global upload entity
        }
        else {
          NetworkManager.getSingleton().addReadEntity( global_downloader );  //register global download entity
        }
        
        global_registered = true;
      }
    }
    finally {  lock.exit();  }
    
    if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
      global_uploader.addPeerConnection( connection );
    }
    else {
      global_downloader.addPeerConnection( connection );
    }
  }
  
  
  /**
   * Remove a peer connection from the entity handler.
   * @param connection to cancel
   */
  public void cancelPeerConnection( NetworkConnection connection ) {
    if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
      if( !global_uploader.removePeerConnection( connection ) ) {  //if not found in the pool entity
        SinglePeerUploader upload_entity = (SinglePeerUploader)upgraded_connections.remove( connection );  //check for it in the upgraded list
        if( upload_entity != null ) {
          NetworkManager.getSingleton().removeWriteEntity( upload_entity );  //cancel from write processing
        }
      }
    }
    else {
      if( !global_downloader.removePeerConnection( connection ) ) {  //if not found in the pool entity
        SinglePeerDownloader download_entity = (SinglePeerDownloader)upgraded_connections.remove( connection );  //check for it in the upgraded list
        if( download_entity != null ) {
          NetworkManager.getSingleton().removeReadEntity( download_entity );  //cancel from read processing
        }
      }
    }

  }
  
  
  /**
   * Upgrade a peer connection from the general pool to its own high-speed entity.
   * @param connection to upgrade from global management
   * @param handler individual connection rate handler
   */
  public void upgradePeerConnection( NetworkConnection connection, RateHandler handler ) {   
    try {  lock.enter();
      if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
        SinglePeerUploader upload_entity = new SinglePeerUploader( connection, handler );
        if( !global_uploader.removePeerConnection( connection ) ) {  //remove it from the general upload pool
          Debug.out( "upgradePeerConnection:: upload entity not found/removed !" );
        }
        NetworkManager.getSingleton().addWriteEntity( upload_entity );  //register it for write processing
        upgraded_connections.put( connection, upload_entity );  //add it to the upgraded list
      }
      else {
        SinglePeerDownloader download_entity = new SinglePeerDownloader( connection, handler );
        if( !global_downloader.removePeerConnection( connection ) ) {  //remove it from the general upload pool
          Debug.out( "upgradePeerConnection:: download entity not found/removed !" );
        }
        NetworkManager.getSingleton().addReadEntity( download_entity );  //register it for read processing
        upgraded_connections.put( connection, download_entity );  //add it to the upgraded list
      }
    }
    finally {  lock.exit();  }
  }
  
  
  /**
   * Downgrade (return) a peer connection back into the general pool.
   * @param connection to downgrade back into the global entity
   */
  public void downgradePeerConnection( NetworkConnection connection ) {
    try {  lock.enter();
      if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
        SinglePeerUploader upload_entity = (SinglePeerUploader)upgraded_connections.remove( connection );  //remove from the upgraded list  
        if( upload_entity != null ) {
          NetworkManager.getSingleton().removeWriteEntity( upload_entity );  //cancel from write processing
        }
        else {
          Debug.out( "upload_entity == null" );
        }
        global_uploader.addPeerConnection( connection );  //move back to the general pool
      }
      else {
        SinglePeerDownloader download_entity = (SinglePeerDownloader)upgraded_connections.remove( connection );  //remove from the upgraded list  
        if( download_entity != null ) {
          NetworkManager.getSingleton().removeReadEntity( download_entity );  //cancel from read processing
        }
        else {
          Debug.out( "download_entity == null" );
        }
        global_downloader.addPeerConnection( connection );  //move back to the general pool
      } 
    }
    finally {  lock.exit();  }
  }

  
  /**
   * Is the general pool entity in need of a transfer op.
   * NOTE: Because the general pool is backed by a MultiPeer entity,
   * it requires at least MSS available bytes before it will/can perform
   * a successful transfer.  This method allows higher-level bandwidth allocation to
   * determine if it should reserve the necessary MSS bytes for the general pool's needs.
   * @return true of it has data to transfer, false if not
   */
  /*
  public boolean isGeneralPoolReserveNeeded() {
    if( handler_type == TransferProcessor.TYPE_UPLOAD ) {
      return global_uploader.hasWriteDataAvailable();
    }
    return global_downloader.hasReadDataAvailable();
  }
  */
  
}
