/*
 * File    : PEPeerManagerStatsImpl.java
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

package org.gudy.azureus2.core3.peer.impl;

import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.global.GlobalManagerStats;
import org.gudy.azureus2.core3.peer.*;
import org.gudy.azureus2.core3.peer.impl.control.PEPeerControlImpl;
import org.gudy.azureus2.core3.util.*;

public class 
PEPeerManagerStatsImpl 
	implements PEPeerManagerStats
{
	private PEPeerManagerAdapter	adapter;
	
	private long total_data_bytes_received = 0;
	private long total_protocol_bytes_received = 0;
  
	private long total_data_bytes_sent = 0;
	private long total_protocol_bytes_sent = 0;
	  
	private long totalDiscarded;
	private long hash_fail_bytes;

	private int	last_data_received_seconds;
	private int last_data_sent_seconds;
	
	private final Average data_receive_speed = Average.getInstance(1000, 10);  //average over 10s, update every 1s.
	private final Average protocol_receive_speed = Average.getInstance(1000, 10);
  
	private final Average data_send_speed  = Average.getInstance(1000, 10);  //average over 10s, update every 1s.
	private final Average protocol_send_speed  = Average.getInstance(1000, 10);
  
	private final Average overallSpeed = Average.getInstance(5000, 100); //average over 100s, update every 5s



	public 
	PEPeerManagerStatsImpl(
		PEPeerControlImpl	_manager ) 
	{
		adapter	= _manager.getAdapter();
	}
  
	public void discarded(int length) {
	  this.totalDiscarded += length;
	  
	  adapter.discarded( length );
	}

	public void
	hashFailed(
		int		length )
	{
		hash_fail_bytes += length;
	}
	
	public long
	getTotalHashFailBytes()
	{
		return( hash_fail_bytes );
	}
	
	public void dataBytesReceived(int length) {
	  total_data_bytes_received += length;
	  data_receive_speed.addValue(length);
	  
	  if ( length > 0 ){
		  last_data_received_seconds = (int)(SystemTime.getCurrentTime()/1000);
	  }
	  
	  adapter.dataBytesReceived( length );
	}

  public void protocolBytesReceived(int length) {
    total_protocol_bytes_received += length;
    protocol_receive_speed.addValue(length);
    
    adapter.protocolBytesReceived( length );
  }
  
  
	public void dataBytesSent(int length, boolean LAN) {
	  total_data_bytes_sent += length;
	  data_send_speed.addValue(length);  
	  
	  if ( length > 0 ){
		  last_data_sent_seconds = (int)(SystemTime.getCurrentTime()/1000);
	  }

	  adapter.dataBytesSent( length, LAN );
	}
  
  public void protocolBytesSent(int length, boolean LAN) {
    total_protocol_bytes_sent += length;
    protocol_send_speed.addValue(length);
    
 	adapter.protocolBytesSent( length, LAN );
  }
  

	public void haveNewPiece(int pieceLength) {
	  overallSpeed.addValue(pieceLength);
	}

	public long getDataReceiveRate() { 
	  return( data_receive_speed.getAverage());
	}

  public long getProtocolReceiveRate() {
    return protocol_receive_speed.getAverage();
  }
  
  
	public long getDataSendRate() {
	  return( data_send_speed.getAverage());
	}
  
  public long getProtocolSendRate() {
    return protocol_send_speed.getAverage();
  }
  
  
	public long getTotalDiscarded() {
	  return( totalDiscarded );
	}
  
	public void setTotalDiscarded(long total) {
	  this.totalDiscarded = total;
	}

	public long getTotalDataBytesSent() {
	  return total_data_bytes_sent;
	}
  
  public long getTotalProtocolBytesSent() {
    return total_protocol_bytes_sent;
  }
  
	public long getTotalDataBytesReceived() {
	  return total_data_bytes_received;
	}
  
  public long getTotalProtocolBytesReceived() {
    return total_protocol_bytes_received;
  }
  
    
	public long 
	getTotalAverage() 
	{
	  return( overallSpeed.getAverage() + getDataReceiveRate() );
	}
	
	public int 
	getTimeSinceLastDataReceivedInSeconds()
	{ 
		if ( last_data_received_seconds == 0 ){
			
			return( -1 );
		}
		
		int	now = (int)(SystemTime.getCurrentTime()/1000);
		
		if ( now < last_data_received_seconds ){
			
			last_data_received_seconds	= now;
		}
		
		return( now - last_data_received_seconds );
	}
	
	public int 
	getTimeSinceLastDataSentInSeconds()
	{ 
		if ( last_data_sent_seconds == 0 ){
			
			return( -1 );
		}
		
		int	now = (int)(SystemTime.getCurrentTime()/1000);
		
		if ( now < last_data_sent_seconds ){
			
			last_data_sent_seconds	= now;
		}
		
		return( now - last_data_sent_seconds );
	}
}
