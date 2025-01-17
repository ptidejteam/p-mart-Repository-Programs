/*
 * File    : PEPeerManagerStats.java
 * Created : 05-Nov-2003
 * By      : parg
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


public interface 
PEPeerManagerStats 
{
  
	public void discarded(int length);
	public void hashFailed(int length);
	  
	public void dataBytesReceived(int length);
	public void protocolBytesReceived(int length);
  
	public void dataBytesSent(int length);
	public void protocolBytesSent(int length);
  
  	public void haveNewPiece(int pieceLength);
  
	public long getDataReceiveRate();
	public long getProtocolReceiveRate();
  
	public long getDataSendRate();
	public long getProtocolSendRate();
   
	public long getTotalDataBytesSent();
	public long getTotalProtocolBytesSent();
  
  	public long getTotalDataBytesReceived();
  	public long getTotalProtocolBytesReceived();
  
	public long getTotalAverage();

	public long getTotalHashFailBytes();
	public long getTotalDiscarded();

}
