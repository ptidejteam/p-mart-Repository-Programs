/*
 * File    : PEPeerTransport
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
 
  /*
 * Created on 4 juil. 2003
 *
 */
package org.gudy.azureus2.core3.peer.impl;

import java.util.List;

import org.gudy.azureus2.core3.peer.*;
import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.download.DownloadManager;

public interface
PEPeerTransport
	extends PEPeer
{
	public static final int HIGH_PRIORITY	= DownloadManager.HIGH_PRIORITY;
  	
	/**
	 * Fake transports are created to permit equivalence testing prior to adding to 
	 * the active transport set. This method exists to convert such fake transports into
	 * real transports if it is found they are new
	 * @return
	 */
	
	public PEPeerTransport
	getRealTransport();
	
	public int
	processRead();
  
    public int
    processWrite();
	
	public void
	sendChoke();
	
	public void
	sendUnChoke();
	
	public void
	sendHave(
		int		piece );
		
	public void
	sendCancel(
		DiskManagerRequest	request );
	
  /**
   * 
   * @param pieceNumber
   * @param pieceOffset
   * @param pieceLength
   * @return true is the piece is really requested
   */
	public boolean 
	request(
		int pieceNumber, 
		int pieceOffset, 
		int pieceLength );

	public void
	closeAll(
      String reason,
	  boolean closedOnError,
	  boolean attemptReconnect);
			
	public boolean
	isReadyToRequest();
		
	public boolean
	transferAvailable();
	
	public List
	getExpiredRequests();
  		
	public int
	getNbRequests();
	
	public int
	getPercentDone();
	
	public PEPeerControl
	getControl();
  
    public int getReadSleepTime();
    public int getWriteSleepTime();
    public long getLastReadTime();
    public long getLastWriteTime();
  
    public void setReadSleepTime(int time);
    public void setWriteSleepTime(int time);
    public void setLastReadTime(long time);
    public void setLastWriteTime(long time);
  
  
}