/*
 * File    : PEPeerSocket.java
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
package org.gudy.azureus2.core3.peer;


/**
 * @author Olivier
 *
 */

public interface 
PEPeer 
{
	public final static int CONNECTING 		= 10;
	public final static int HANDSHAKING 	= 20;
	public final static int TRANSFERING 	= 30;
  public final static int CLOSING       = 40;
	public final static int DISCONNECTED 	= 50;
	
	public PEPeerManager
	getManager();
	
	public int getState();	// from above set

	public byte[] getId();

	public String getIp();
 
	/**
	 * Gets the host name for the IP, if possible, IP as string otherwise
   * @return hostname or IP
   */
	public String getIPHostName();
	
	public int getPort();
	
	public boolean[] getAvailable();
 
	public void setSnubbed(boolean b);	// explicit un-snub
  
  /**
   * Is the peer choking me.
   * @return true if I am choked by the peer, false if not
   */
	public boolean isChokingMe();

  /**
   * Am I choking the peer.
   * @return true if the peer is choked, false if not
   */
	public boolean isChokedByMe();

  /**
   * Am I Interested in the peer.
   * @return true if peer is interesting, false if not
   */
	public boolean isInterestingToMe();

  /**
   * Is the peer Interested in me.
   * @return true if the peer is interested in me, false if not
   */
	public boolean isInterestedInMe();

  
	public boolean isSeed();
 
	public boolean isSnubbed();
 
	public PEPeerStats getStats();
 	
	public boolean isIncoming();

  /**
   * Get the peer's torrent completion percentage in thousand-notation,
   * i.e. 53.7% is returned as the value 0537.
   * @return the percentage the peer has complete
   */
	public int getPercentDoneInThousandNotation();

  
	public String getClient();

	public boolean isOptimisticUnchoke();
	
	public void hasSentABadChunk();
	
	public int getNbBadChunks();
	
	public void resetNbBadChunks();
	
	//Used in super-seed mode
	//The lower the better
	public void setUploadHint(int timeToSpread);
	
	public int getUploadHint();
	
	public void setUniqueAnnounce(int uniquePieceNumber);
	
	public int getUniqueAnnounce();
   
  
  /** To retreive arbitrary objects against a peer. */
  public Object getData (String key);
  /** To store arbitrary objects against a peer. */
  public void setData (String key, Object value);
  
}