/*
 * File    : PEPieceWriteImpl.java
 * Created : 7 nov. 2003 16:04:47
 * By      : Olivier 
 * 
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
package org.gudy.azureus2.core3.peer;


/**
 * @author Olivier
 * 
 */
public class PEPieceWrite {
  
  public int blockNumber;
  public PEPeer sender;
  public byte[] hash;
  public boolean correct;
  
  public PEPieceWrite(int blockNumber,PEPeer sender, byte[] hash) {
    this(blockNumber,sender,hash,false);
  }
  
  public PEPieceWrite(int blockNumber,PEPeer sender, byte[] hash,boolean correct) {
    this.blockNumber = blockNumber;
    this.sender = sender;
    this.hash = hash;
    this.correct = correct;
  }
  
}
