/*
 * File    : PEPiece
 * Created : 15-Oct-2003
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

import java.util.List;

/**
 * Represents a Piece and the status of its different chunks (un-requested, requested, downloaded, written).
 * 
 * @author Olivier
 *
 */

public interface 
PEPiece 
{  
  public void setWritten(PEPeer peer,int blocNumber);
 
  public void unmarkBlock(int blocNumber);
 
  public int getAvailability();
   
  public int getPieceNumber();
  
  public int getLength();
  
  public int getNbBlocs();  
 
  public int getCompleted();
 
  public boolean[] getWritten();
  
  public boolean[] getRequested(); 
  
  public void addWrite(PEPieceWrite write);
  
  public List getPieceWrites();
  
  public List getPieceWrites(int blockNumber);
  
  public List getPieceWrites(PEPeer peer);
  
  public void reset();
  
  public PEPeer[] getWriters();
  
  public int getBlockSize(int blockNumber);
  
  public boolean isComplete();
  
  public void setBeingChecked();
  
  public boolean isBeingChecked();
  
  public int getAndMarkBlock();
  
  public void setManager(PEPeerManager manager);
  
  public boolean isWritten(int blockNumber);
  
  public void setBlockWritten(int blockNumber);
  
  public void free();
    
}