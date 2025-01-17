/*
 * Created on Nov 12, 2003
 * Created by nolar
 *
 */
package org.gudy.azureus2.core3.util;

import org.gudy.azureus2.core3.internat.MessageText;

/**
 * Used for identifying clients by their peerID.
 * 
 * @author Nolar
 */
public class Identification {
  
  
  /**
   * Decodes the given peerID, returning an identification string.
   */  
  public static String decode(byte[] peerID) {
    final boolean DEBUG_ALL = false;
    final boolean DEBUG_UNKNOWN = false;
    
    try {
      if (DEBUG_ALL) System.out.println(new String(peerID, 0, 20, Constants.BYTE_ENCODING));

      String shadow = new String(peerID, 0, 1, Constants.BYTE_ENCODING);
      if (shadow.equals("S")) {
        
        if (peerID[8] == (byte)45) {
          String version = new String(peerID, 1, 3, Constants.BYTE_ENCODING);
          String name = "Shadow ";
          for (int i = 0; i < 2; i++) {
            name = name.concat(version.charAt(i) + ".");
          }
          name = name + version.charAt(2);
          return name;
        }
        
        if (peerID[8] == (byte)0) {  // is next Burst version still using this?
          String name = "Shadow ";
          for (int i = 1; i < 3; i++) {
            name = name.concat(String.valueOf(peerID[i]) + ".");
          }
          name = name + String.valueOf(peerID[3]);
          return name;
        }
      }
      
      
      String azureus = new String(peerID, 1, 2, Constants.BYTE_ENCODING);
      if (azureus.equals("AZ")) {
        String version = new String(peerID, 3, 4, Constants.BYTE_ENCODING);
        String name = "Azureus ";
        for (int i = 0; i < 3; i++) {
          name = name.concat(version.charAt(i) + ".");
        }
        name = name + version.charAt(3);
        return name;
      }
      
      
      String old_azureus = new String(peerID, 5, 7, Constants.BYTE_ENCODING);
      if (old_azureus.equals("Azureus")) return "Azureus 2.0.3.2";
      
      
      String upnp = new String(peerID, 0, 1, Constants.BYTE_ENCODING);
      if (upnp.equals("U")) {
        if (peerID[8] == (byte)45) {
          String version = new String(peerID, 1, 3, Constants.BYTE_ENCODING);
          String name = "UPnP ";
          for (int i = 0; i < 2; i++) {
            name = name.concat(version.charAt(i) + ".");
          }
          name = name + version.charAt(2);
          return name;
        }  
      }
      
      
      String xantorrent = new String(peerID, 0, 10, Constants.BYTE_ENCODING);
      if (xantorrent.equals("DansClient")) return "XanTorrent";
      
      
      String btfans = new String(peerID, 4, 6, Constants.BYTE_ENCODING);
      if (btfans.equals("btfans")) return "BitComet"; // "BitComet"? or "SimpleBT"? 
      
      String turbobt = new String(peerID, 0, 7, Constants.BYTE_ENCODING);
      if (turbobt.equals("turbobt")) return "TurboBT";
      
      
      boolean allZero = true;
      for (int i = 0; i < 12; i++) {
        if (peerID[i] != (byte)0) { allZero = false; break; }
      }
      
      if ((allZero) && (peerID[12] == (byte)97) && (peerID[13] == (byte)97)) {
        return "Experimental 3.2.1b2";
      }
      if ((allZero) && (peerID[12] == (byte)0) && (peerID[13] == (byte)0)) {
        return "Experimental 3.1";
      }
      if (allZero) return MessageText.getString("PeerSocket.generic");
      
    }
    catch (Exception ignore) {/*ignore*/}
    
    if (DEBUG_UNKNOWN && !DEBUG_ALL) {
      try {
        System.out.println(new String(peerID, 0, 20, Constants.BYTE_ENCODING));
      } catch (Exception ignore) {/*ignore*/} 
    }
    
    if (DEBUG_UNKNOWN || DEBUG_ALL) {
      for (int i=0; i < 19; i++) {
        System.out.print(i+"=" + peerID[i] + " ");
      }
      System.out.println("19=" + peerID[19]);
      System.out.println();
    }
    
    return MessageText.getString("PeerSocket.unknown");
  }

}
