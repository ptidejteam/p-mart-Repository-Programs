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

import org.gudy.azureus2.core3.disk.DiskManagerPiece;

import com.aelitis.azureus.core.util.Piece;

/**
 * Represents a Peer Piece and the status of its different blocks (un-requested, requested, downloaded, written).
 * 
 * @author Olivier
 * @author MjrTom
 *			2005/Oct/08: various changes to support new piece-picking
 *			2006/Jan/2: refactoring, mostly to base Piece interface
 */

public interface PEPiece
	extends Piece
{  
	public PEPeerManager	getManager();
    public DiskManagerPiece getDMPiece();
    public int          getPieceNumber();
    /**
     * @param offset int bytes into piece 
     * @return block int number corresponding to given offset
     */
    public int          getBlockNumber(int offset);

    /** The time the pePiece was [re]created
     */
    public long         getCreationTime();

    /** How many ms since a write to the piece, or since the piece
     * was created if no last write time is known.
     * The return value will be 0 when there's no writes and the piece is new.
     * @return long
     */
    public long         getTimeSinceLastActivity();


	/**
	 * record details of a piece's blocks that have been completed for bad peer detection purposes
	 * @param blockNumber
	 * @param sender
	 * @param hash
	 * @param correct
	 */
	public void 
	addWrite(
		int blockNumber,
		String sender, 
		byte[] hash,
		boolean correct	);

	public int			getNbWritten();

	public int			getAvailability();

	public boolean		hasUnrequestedBlock();
	public int[]		getAndMarkBlocks(PEPeer peer, int nbWanted);
	public boolean		setRequested(PEPeer peer, int blockNumber);
	public void			clearRequested(int blocNumber);
    public boolean      isRequested(int blockNumber);
    /** @deprecated
     * Use clearRequested(int blocNumber) instead 
     * @param blockNumber
     */
    public void         unmarkBlock(int blockNumber);
    
	public int			getNbRequests();
	public int			getNbUnrequested();
//	public int			checkRequests();

	public boolean		isDownloaded(int blockNumber);   
    public void         setDownloaded(int offset);
    public void         clearDownloaded(int offset);

	//A Piece can be reserved by a peer, so that only s/he can
	//contribute to it.
	public String		getReservedBy();
	public void			setReservedBy(String peer);

	/**
	 * @return int ResumePriority (startPriority + resuming adjustments)
	 */
	public int			getResumePriority();
	/**
	 * @param p the Resume Priority to set, for display purposes
	 */
	public void			setResumePriority(int p);

	public String[] 	getWriters();
	public void			setWritten(PEPeer peer, int blockNumber);
	public boolean isWritten();
	public int 			getSpeed();
	public void			setSpeed(int speed);
	public void			incSpeed();
    /**
     * @deprecated
     * This is not good for high speed transfers
     */
	public void			decSpeed();
}