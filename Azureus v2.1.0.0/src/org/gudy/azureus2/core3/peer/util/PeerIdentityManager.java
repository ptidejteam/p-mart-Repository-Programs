/*
 * Created on Mar 18, 2004
 * Created by Alon Rohter
 * Copyright (C) 2004 Alon Rohter, All Rights Reserved.
 * 
 */
package org.gudy.azureus2.core3.peer.util;


import java.util.*;


/**
 * Maintains peer identity information.
 */
public class PeerIdentityManager {

  private static PeerIdentityManager peerIDManager = new PeerIdentityManager();
  private final Map dataIdMap;
  private static int totalIDs = 0;
  
  
  private PeerIdentityManager() {
    this.dataIdMap = new HashMap();
  }
  
  
  private static PeerIdentityManager getInstance() {
    return peerIDManager;
  }
  
  
  private static class DataID {
    private final byte[] dataId;
    private final int hashcode;
    
    private DataID( byte[] _data_id ) {
      this.dataId = _data_id;
      this.hashcode = new String( dataId ).hashCode();
    }
    
    public boolean equals( Object obj ) {
      if (this == obj)  return true;
      if (obj != null && obj instanceof DataID) {
        DataID other = (DataID)obj;
        return Arrays.equals(this.dataId, other.dataId);
      }
      return false;
    }
    
    public int hashCode() {
      return hashcode;
    }
  }
  
  //Main peer identity container.
  //Add new identity items (like pgp key, authentication user/pass, etc)
  //to this class if/when needed.
  private static class PeerIdentity {
    private final byte[] id;
    private final int hashcode;
    
    private PeerIdentity( byte[] _id ) {
      this.id = _id;
      this.hashcode = new String( id ).hashCode();
    }
    
    public boolean equals( Object obj ) {
      if (this == obj)  return true;
      if (obj != null && obj instanceof PeerIdentity) {
        PeerIdentity other = (PeerIdentity)obj;
        return Arrays.equals(this.id, other.id);
      }
      return false;
    }
    
    public int hashCode() {
      return hashcode;
    }
  }
  
  
  /**
   * Add a new peer identity to the manager.
   * @param data_id unique id for the data item associated with this connection
   * @param peer_id unique id for this peer connection
   * @param ip remote peer's ip address
   */
  public static void addIdentity( byte[] data_id, byte[] peer_id, String ip ) {
    Map dataMap = PeerIdentityManager.getInstance().dataIdMap;
    synchronized( dataMap ) {
      DataID dataID = new DataID( data_id );
      if ( !dataMap.containsKey( dataID )) {
        dataMap.put( dataID, new HashMap());
      }
      Map peerMap = (Map)dataMap.get( dataID );
      PeerIdentity peerID = new PeerIdentity( peer_id );
      if ( !peerMap.containsKey( peerID )) {
        peerMap.put( peerID, ip );
        totalIDs++;
      }
    }
  }
  
  
  /**
   * Remove a peer identity from the manager.
   * @param data_id id for the data item associated with this connection
   * @param peer_id id for this peer connection
   */
  public static void removeIdentity( byte[] data_id, byte[] peer_id ) {
    Map dataMap = PeerIdentityManager.getInstance().dataIdMap;
    synchronized( dataMap ) {
      DataID dataID = new DataID( data_id );
      if ( dataMap.containsKey( dataID )) {
        Map peerMap = (Map)dataMap.get( dataID );
        PeerIdentity peerID = new PeerIdentity( peer_id );
        if ( peerMap.containsKey( peerID )) {
          peerMap.remove( peerID );
          totalIDs--;
        }
      }
    }
  }
  

  /**
   * Check if the manager already has the given peer identity.
   * @param data_id id for the data item associated with this connection
   * @param peer_id id for this peer connection
   * @return true if the peer identity is found, false if not found
   */
  public static boolean containsIdentity( byte[] data_id, byte[] peer_id ) {
    Map dataMap = PeerIdentityManager.getInstance().dataIdMap;
    synchronized( dataMap ) {
      DataID dataID = new DataID( data_id );
      if ( dataMap.containsKey( dataID )) {
        Map peerMap = (Map)dataMap.get( dataID );
        PeerIdentity peerID = new PeerIdentity( peer_id );
        if ( peerMap.containsKey( peerID )) {
        	return true;
        }
      }
    }
    return false;
  }
  
  
  /**
   * Get the total number of peer identities managed.
   * @return total number of peers over all data items
   */
  public static int getTotalIdentityCount() {
    return totalIDs;
  }
  
  
  /**
   * Get the total number of peer identities managed for the given data item.
   * @param data_id data item to count over
   * @return total number of peers for this data item
   */
  public static int getIdentityCount( byte[] data_id ) {
    Map dataMap = PeerIdentityManager.getInstance().dataIdMap;
    synchronized( dataMap ) {
      DataID dataID = new DataID( data_id );
      if ( dataMap.containsKey( dataID )) {
        Map peerMap = (Map)dataMap.get( dataID );
        return peerMap.size();
      }
    }
    return 0;
  }
  
  
  /**
   * Check if the given IP address is already present in the manager's
   * peer identity list for the given data item (i.e. check if there is
   * already a peer with that IP address).
   * @param data_id id for the data item associated with this connection
   * @param ip IP address to check for
   * @return true if the IP is found, false if not found
   */
  public static boolean containsIPAddress( byte[] data_id, String ip ) {
    Map dataMap = PeerIdentityManager.getInstance().dataIdMap;
    synchronized( dataMap ) {
      DataID dataID = new DataID( data_id );
      if ( dataMap.containsKey( dataID )) {
        Map peerMap = (Map)dataMap.get( dataID );
        if ( peerMap.containsValue( ip )) {
          return true;
        }
      }
    }
    return false;
  }

}
