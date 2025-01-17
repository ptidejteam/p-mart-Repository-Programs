/*
 * Created on 22 juil. 2003
 *
 */
package org.gudy.azureus2.core3.util;

import java.util.Arrays;

/**
 * @author Olivier
 * 
 */
public class HashWrapper {
  
  private byte[] hash;
  
  public HashWrapper(byte[] hash) {
	this.hash = new byte[hash.length];
	System.arraycopy(hash,0,this.hash,0,this.hash.length);
  }
  
  public HashWrapper(byte[] hash, int offset,int length) {
	 this.hash = new byte[length];
	 System.arraycopy(hash,offset,this.hash,0,length);
   }
  
  public boolean equals(Object o) {
    if(! (o instanceof HashWrapper))
      return false;
    
    byte[] otherHash = ((HashWrapper)o).getHash();
	return Arrays.equals(hash, otherHash);	
  }
  
  public byte[] getHash() {
    return this.hash;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    String str = null;
    try {    
      str = new String(hash);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return str.hashCode();
  }

}
