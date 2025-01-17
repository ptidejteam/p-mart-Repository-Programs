/*
 * Created on 01-Aug-2004
 * Created by Paul Gardner
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
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
 * AELITIS, SARL au capital de 30,000 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package org.gudy.azureus2.core3.disk.impl.piecepicker.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.disk.impl.*;
import org.gudy.azureus2.core3.disk.impl.piecepicker.*;
import org.gudy.azureus2.core3.util.SystemTime;

/**
 * @author parg
 *
 */

public class 
DMPiecePickerImpl
	implements DMPiecePicker, ParameterListener
{
	private static final long PRIORITY_COMPUTE_MIN	= 30*60*1000;
	
	private DiskManagerHelper		disk_manager;
	
	private boolean firstPiecePriority = COConfigurationManager.getBooleanParameter("Prioritize First Piece", false);
	private boolean completionPriority = COConfigurationManager.getBooleanParameter("Prioritize Most Completed Files", false);
	private int		nbPieces;
	private int 	pieceCompletion[];
	
	private boolean	has_piece_to_download;
	
	private volatile boolean	compute_priority_recalc_outstanding;
	
	private long	last_priority_computation;
	
	private BitSet[] priorityLists;
	
	//private int[][] priorityLists;

	private MyDiskManagerListener myDiskManListener;
	
	public
	DMPiecePickerImpl(
		DiskManagerHelper		_disk_manager )
	{
		disk_manager	= _disk_manager;
		
		nbPieces	= disk_manager.getNumberOfPieces();
		
		myDiskManListener = new MyDiskManagerListener();
	}
	
	/**
	 * An instance of this listener is registered at disk_manager.
	 * It updates the value returned by hasDownloadablePiece to reflect
	 * changes in file/piece priority values.
	 * @author Balazs Poka
	 */
	private class MyDiskManagerListener implements DiskManagerListener {

        public void stateChanged(int oldState, int newState) {
        }

        public void filePriorityChanged() {
            
        	compute_priority_recalc_outstanding	= true;
        }


    	public void
    	pieceDoneChanged()
    	{
    		compute_priority_recalc_outstanding	= true;
    	}
	}
	
	public void
	start()
	{
		COConfigurationManager.addParameterListener("Prioritize First Piece", this);
		COConfigurationManager.addParameterListener("Prioritize Most Completed Files", this);
    
		disk_manager.addListener(myDiskManListener);
		
		pieceCompletion = new int[nbPieces];
		
		has_piece_to_download	= true;
		
		priorityLists = new BitSet[100];
		
		//    priorityLists = new int[10][nbPieces + 1];

		// the piece numbers for getPiecenumberToDownload
		//    _priorityPieces = new int[nbPieces + 1];

	}
	
	public void
	stop()
	{
		COConfigurationManager.removeParameterListener("Prioritize First Piece", this);
		COConfigurationManager.removeParameterListener("Prioritize Most Completed Files", this);
		disk_manager.removeListener(myDiskManListener);
	}
	
	public void 
	parameterChanged(
		String parameterName ) 
	{
	   firstPiecePriority = COConfigurationManager.getBooleanParameter("Prioritize First Piece", false);
	   completionPriority = COConfigurationManager.getBooleanParameter("Prioritize Most Completed Files", false);
	}
	
	public void 
	computePriorityIndicator() 
	{
			// this has been changed to be driven by explicit changes in file priority
			// and piece status rather than calculating every time.
			// however, it is unsynchronised so there is a very small chance that we'll
			// miss a recalc indication, hence the timer
		
		long	now = SystemTime.getCurrentTime();
		
		if ( 	compute_priority_recalc_outstanding ||
				now - last_priority_computation >= PRIORITY_COMPUTE_MIN ||
				now < last_priority_computation ){	// clock changed
						
			last_priority_computation			= now;
			compute_priority_recalc_outstanding	= false;
			
			DiskManagerPiece[]	pieces	= disk_manager.getPieces();
			
			for (int i = 0; i < pieceCompletion.length; i++) {
			  
			   //if the piece is already complete, skip computation
			   if (pieces[i].getDone()) {
			     pieceCompletion[i] = -1;
			     continue;
			   }
	      
				PieceList pieceList = disk_manager.getPieceList(i);
				int completion = -1;
				
				int size=pieceList.size();
				for (int k = 0; k < size; k++) {
					//get the piece and the file 
					DiskManagerFileInfoImpl fileInfo = (pieceList.get(k)).getFile();
					
					//If the file isn't skipped
					if(fileInfo.isSkipped()) {
						continue;
					}
	
					//if this is the first or last piece of the file
					if( firstPiecePriority && (i == fileInfo.getFirstPieceNumber() || i == fileInfo.getLastPieceNumber() ) ) {
					  if (fileInfo.isPriority()) completion = 99;
					  else if (completion < 97) completion = 97;
					}
	        
					//if the file is high-priority
					else if (fileInfo.isPriority()) {
					  if (completion < 98) completion = 98;
					}
					
					//If the file is started but not completed
					else if(completionPriority) {
					  int percent = 0;
					  if (fileInfo.getLength() != 0) {
					    percent = (int) ((fileInfo.getDownloaded() * 100) / fileInfo.getLength());
					  }
					  //if percent is less than 100 AND higher than current completion level
					  if (percent < 100 && completion < percent) {
					    completion = percent;
					  }
					}
					else {
					  if(completion < 0) completion = 0;
					}
				}
	      
				pieceCompletion[i] = completion;
			}
	
			// this clears and resizes all priorityLists to the
			// length of pieceCompletion
			for (int i = 0; i < priorityLists.length; i++) {
				BitSet list = priorityLists[i];
				if (list == null) {
					list = new BitSet(pieceCompletion.length);
				} else {
					list.clear();
				}
				priorityLists[i]=list;
			}
			
		
			has_piece_to_download	= false;
			
				// for all pieces, set the priority bits accordingly
			
			for (int i = 0; i < pieceCompletion.length; i++) {
				
				int priority = pieceCompletion[i];
				
				if ( priority >= 0 ){
					
					has_piece_to_download	= true;
					
					priorityLists[priority].set(i);
				}
			}
		}
	}
	
	 /*
	// searches from 0 to searchLength-1
    public static int binarySearch(int[] a, int key, int searchLength) {
		int low = 0;
		int high = searchLength - 1;

		while (low <= high) {
			int mid = (low + high) >> 1;
			int midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid; // key found
		}
		return - (low + 1); // key not found.
	}
  */

	public int getPiecenumberToDownload(boolean[] _piecesRarest) {
		//Added patch so that we try to complete most advanced files first.
		List _pieces = new ArrayList();
    
		for (int i = 99; i >= 0; i--) {

		  if (priorityLists[i].isEmpty()) {
		    //nothing is set for this priority, so skip
		    continue;
		  }
		  
		  //Switch comments to enable sequential piece picking.
		  //int k = 0;
		  //for (int j = 0; j < nbPieces && k < 50; j++) {
      
		  for (int j = 0; j < nbPieces ; j++) {
		    if (_piecesRarest[j] && priorityLists[i].get(j)) {
		      _pieces.add( FlyWeightInteger.getInteger(j) );
		      //k++;
		    }
		  }
		  
		  if (_pieces.size() != 0) {
				break;
		  }
		}

		if (_pieces.size() == 0) {
		  return -1;
		}

		return ((Integer)_pieces.get((int) (Math.random() * _pieces.size()))).intValue();
	}

	public boolean
	hasDownloadablePiece() 
	{
		return( has_piece_to_download );
	}  

	/*
	  public int getPiecenumberToDownload(boolean[] _piecesRarest) {
		int pieceNumber;
		//Added patch so that we try to complete most advanced files first.
		_priorityPieces[nbPieces] = 0;
		for (int i = priorityLists.length - 1; i >= 0; i--) {
		  for (int j = 0; j < nbPieces; j++) {
			if (_piecesRarest[j] && binarySearch(priorityLists[i], j, priorityLists[i][nbPieces]) >= 0) {
			  _priorityPieces[_priorityPieces[nbPieces]++] = j;
			}
		  }
		  if (_priorityPieces[nbPieces] != 0)
			break;
		}
      
		if (_priorityPieces[nbPieces] == 0)
		  System.out.println("Size 0");
      
		int nPiece = (int) (Math.random() * _priorityPieces[nbPieces]);
		pieceNumber = _priorityPieces[nPiece];
		return pieceNumber;
	  }
	*/
	
	private static class FlyWeightInteger {
	    private static Integer[] array = new Integer[1024];

	    final static Integer getInteger(final int value) {
	      Integer tmp = null;
	      
	      if (value >= array.length) {
	        Integer[] arrayNew = new Integer[value + 256];
	        System.arraycopy(array, 0, arrayNew, 0, array.length);
	        array = arrayNew;
	      }
	      else {
	        tmp = array[value];
	      }
	      
	      if (tmp == null) {
	        tmp = new Integer(value);
	        array[value] = tmp;
	      }
	      
	      return tmp;
	    }
		}
	  
	  
}
