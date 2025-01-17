/*
 * Created on 08-Oct-2004
 * Created by Paul Gardner
 * Copyright (C) 2004, 2005, 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * AELITIS, SAS au capital de 46,603.30 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.core3.disk;

import com.aelitis.azureus.core.util.Piece;

/**
 * Represents a DiskManager Piece
 *
 * @author parg
 * @author MjrTom
 *			2005/Oct/08: priority handling
 *			2006/Jan/2: refactoring, mostly to base Piece interface
 */

public interface DiskManagerPiece
	extends Piece
{
	public DiskManager	getManager();

	public long			getLastWriteTime();

	public boolean		calcNeeded();
	public void			clearNeeded();
	/** @return true if any file the piece covers is neither Do Not Download nor Delete.
	 * This is not a real-time indicator.  Also, the results are not reliable for pieces that are Done.
	 * Use calcNeeded() for guaranteed correct and up to date results
	 * @see calcNeeded(), clearNeeded(), setNeeded(), setNeeded(boolean)
	 */
	public boolean		isNeeded();
	public void			setNeeded();
	public void			setNeeded(boolean b);

/*
    // a piece is Avail if any other peer in the swarm makes the piece available, independant of if we have it or not
	public boolean		calcAvail();
	public void			clearAvail();
	public boolean		isAvail();
	public void			setAvail();
	public void			setAvail(boolean b);
*/

	// a piece is Requested if there is at least 1 outstanding request on it AND there's no more blocks that need to be requested
	public boolean		calcRequested();
	public void			clearRequested();
	public boolean		isRequested();
	public void			setRequested();
	public void			setRequested(boolean b);

	// a piece is Downloaded if data has been received for every block (without concern for if it's written or checked)  
	public boolean		calcDownloaded();
	public void			clearDownloaded();
	public boolean		isDownloaded();
	public void			setDownloaded();

	// a piece is Written if data has been written to storage for every block (without concern for if it's checked)  
    public boolean      isWritten();
	public int			getNbWritten();
	public boolean		calcWritten();
	public void			clearWritten();
	public void			setWritten();
	/**
	 * @param blockNumber int
	 * @return true if the given blockNumber has already been written to disk
	 */
	public boolean		isWritten(int blockNumber);
	public void			setWritten(int blockNumber);

	// a piece is Checking if a hash check has been setup and the hash check hasn't finalized the result yet
	// this flag is asynch, so be careful, and it's also transitory (comapared to most of the others being kinda sticky)
	public boolean		calcChecking();
	public void			setChecking();
	public void			setChecking(boolean b);
    public void         clearChecking();

	public boolean		calcDone();
	/** @return true when the hash check has passed and the DiskManager has asyncronously updated the Done status.
	 * There is nothing further to be done regarding downloading for pieces that are Done.
	 */
	public boolean		isDone();
	public void			setDone(boolean b);

    /**
     * @return true if a piece is Needed and not Done
     */
    public boolean      isInteresting();

    /** This must not be used to qualify pieces in End Game Mode.
	 * @return true is a piece is Needed but is not fully; Requested, Downloaded, Written, Checking, or Done.
     * Avail isn't checked by this.
	 */
	public boolean		isRequestable();
    
    /** End Game Mode considers a piece to be active that is;
     * Needed and fully Requested, but not fully Downloaded, fully Written, hash Checking, or Done
     * @return true if EGM should count the piece as active
     */
    public boolean      isEGMActive();

    /** End Game Mode ignores pieces that are not Needed, or are fully; 
     * Downlaoded, Written, Checking, or Done
     * @return true if EGM should ignore the piece
     */
    public boolean      isEGMIgnored();
    
    /** This returns a single concise status for the stage of progression the entire piece
     * has reached.  The statuses are defined in Piece.java.
     * @return may be;  PIECE_STATUS_DONE, PIECE_STATUS_CHECKING, PIECE_STATUS_WRITTEN,
     * PIECE_STATUS_DOWNLOADED, PIECE_STATUS_REQUESTED, or if nothing else PIECE_STATUS_NEEDED.
     * A status of 0 means that the piece has not progressed to any stage of completion and is
     * not even needed according to the current priority settings.
     */
    public int          getStatus();
}
