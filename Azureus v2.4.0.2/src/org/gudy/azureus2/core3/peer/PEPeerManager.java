/*
 * File    : PEPeerManager
 * Created : 5 Oct. 2003
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

/**
 * @author parg
 * @author MjrTom
 *			2005/Oct/08: pieceAdded => addPiece to simplify new piece-picking, getAvgAvail
 *
 */

import java.util.List;

import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.peer.impl.PEPeerTransport;
import org.gudy.azureus2.core3.peer.util.PeerIdentityDataID;
import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncerResponse;
import org.gudy.azureus2.core3.util.DirectByteBuffer;

import com.aelitis.azureus.core.networkmanager.LimitedRateGroup;
import com.aelitis.azureus.core.peermanager.peerdb.PeerExchangerItem;
import com.aelitis.azureus.core.peermanager.piecepicker.PiecePicker;


public interface 
PEPeerManager 
{
	
	public DiskManager getDiskManager();
	public PiecePicker getPiecePicker();
	
	public void
	start();
		
	public void
	stopAll();

	public byte[]
	getHash();

	public String
	getDisplayName();
	
	public PeerIdentityDataID
	getPeerIdentityDataID();
	
	public byte[]
	getPeerId();

	public int[] getAvailability();

	public int getAvailability(int pieceNumber);
	
	public float getAvgAvail();

	public float getMinAvailability();

	public boolean hasDownloadablePiece();
	
    
    /** @return true if the piece is loaded into the active array
     */
    public boolean      isPieceActive(int pieceNumber);

    /** Often better to use getPiece(pieceNumber)
     */
	public PEPiece[]	getPieces();

    /** @return PEPiece or null if piece not currently active
     */
	public PEPiece		getPiece(int pieceNumber);

    
	public PEPeerManagerStats
	getStats();

	public void
	processTrackerResponse(
		TRTrackerAnnouncerResponse	response );
		
	public int getNbPeers();

	public int getNbSeeds();
	
	public int getPieceLength(int pieceNumber);
		
	public long getRemaining();
	
	public long getETA();

	public String getElapsedTime();
	
	// Time Started in ms
	public long getTimeStarted();

	public long getTimeStartedSeeding();
	
	public void
	addListener(
		PEPeerManagerListener	l );
		
	public void
	removeListener(
		PEPeerManagerListener	l );
  
	public void addPiece(PEPiece piece, int pieceNumber);
  
  public boolean needsMD5CheckOnCompletion(int pieceNumber);
  
  public boolean
  isSuperSeedMode();
  
  public int getNbRemoteConnections();
  
  public long getLastRemoteConnectionTime();
  
  public int
  getMaxNewConnectionsAllowed();
  
  /**
   * Data bytes received.
   * @param l
   */
	public void	dataBytesReceived(	int	l );	
	
  /**
   * Data bytes sent.
   * @param l
   */
	public void	dataBytesSent( int	l );
	
  /**
   * Protocol bytes sent.
   * @param length
   */
  public void protocolBytesSent( int length );
  
  /**
   * Protocol bytes received.
   * @param length
   */
  public void protocolBytesReceived( int length );
  
  
  
	public void
	discarded(
		int		l );		
	
	public PEPeerStats
	createPeerStats();
	
	
	public List
	getPeers();
	
	public void
	addPeer(
		PEPeer	peer );
  
  
  /**
   * Add a new peer, using the default internal PEPeer implementation
   * (like for peers given in announce reply), using the given address
   * and port.
   * @param ip_address of peer to inject
   * @param port of peer to inject
   * @param use_crypto use encrypted transport
   */
  public void addPeer( String ip_address, int port, boolean use_crypto );
  
	
	public void
	removePeer(
		PEPeer	peer );
	
	public void 
	peerAdded(PEPeer pc);

	public void 
	peerRemoved(PEPeer pc);
	
	public DiskManagerReadRequest
	createDiskManagerRequest(
	   int pieceNumber,
	   int offset,
	   int length );
	
	public void
	requestCanceled(
		DiskManagerReadRequest	item );
		
	public boolean 
	checkBlock(
		int 		pieceNumber, 
		int 		offset, 
		DirectByteBuffer 	data );
	
	public void 
	writeBlock(
		int 		pieceNumber, 
		int 		offset, 
		DirectByteBuffer 	data,
		PEPeer 		sender,
        boolean     cancel);		
  
//  public void writeBlockAndCancelOutstanding(int pieceNumber, int offset, DirectByteBuffer data,PEPeer sender);
  
  public boolean isWritten( int piece_number, int offset );

  /**
   * Are we in end-game mode?
   * @return true if in end game mode, false if not
   */
  public boolean isInEndGameMode();
  
  /**
   * Notify the manager that the given peer connection has been closed.
   * @param peer closed
   */
  public void peerConnectionClosed( PEPeerTransport peer );
  
  
  
  /**
   * Register a peer connection for peer exchange handling.
   * NOTE: Creation could fail if the peer is not eligible for peer exchange (like if it's remote port is unknown).
   * @param base_peer exchaning with
   * @return peer database connection item, or null if creation failed
   */
  public PeerExchangerItem createPeerExchangeConnection( PEPeerTransport base_peer );
  
  
  /**
   * Notify that the given peer connection represents our own client.
   * @param self peer
   */
  public void peerVerifiedAsSelf( PEPeerTransport self );
  
  
  /**
   * Get the limited rate group used for upload limiting.
   * @return upload limit group
   */
  public LimitedRateGroup getUploadLimitedRateGroup();
  
  /**
   * Get the limited rate group used for download limiting.
   * @return download limit group
   */
  public LimitedRateGroup getDownloadLimitedRateGroup();
  
  
  /** To retreive arbitrary objects against this object. */
  public Object getData (String key);
  /** To store arbitrary objects against this object. */
  public void setData (String key, Object value);
  
  
  /**
   * Get the average completion percentage of connected peers.
   * @return average percent complete in thousand notation
   */
  public int getAverageCompletionInThousandNotation();

	/**
	 * Locate an existing transport via peer id byte identity.
	 * @param peer_id to look for
	 * @return transport with matching identity, or null if no match is found
	 */
	public PEPeerTransport getTransportFromIdentity( byte[] peer_id );
	
	/**
	 * Locate an existing transport via [IP] Address.
	 * @param peer String to look for
	 * @return PEPeerTransport with matching address String, or null if no match is found
	 */
	public PEPeerTransport getTransportFromAddress(String peer);
	
}
