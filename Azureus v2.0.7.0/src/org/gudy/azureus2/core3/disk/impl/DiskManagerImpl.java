/*
 * File    : DiskManagerImpl.java
 * Created : 18-Oct-2003
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

package org.gudy.azureus2.core3.disk.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;


import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.internat.*;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.peer.*;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.util.SHA1Hasher;

/**
 * 
 * The disk Wrapper.
 * 
 * @author Tdv_VgA
 *
 */
public class 
DiskManagerImpl
	implements DiskManager, ParameterListener 
{  
  
	private int state_set_via_method;
	private String errorMessage = "";

	private int pieceLength;
	private int lastPieceLength;

	//  private int[] _priorityPieces;

	private byte[][] piecesHash;
	private int nbPieces;
	private long totalLength;
	private boolean pieceDone[];
	private int percentDone;
	private long allocated;

	private long remaining;

	private String path;
	private String fileName = "";
    
	private	TOTorrent		torrent;

	private ByteBuffer allocateAndTestBuffer;

	private Vector writeQueue;
	private Vector checkQueue;
	private Vector readQueue;

	private DiskWriteThread writeThread;
	private DiskReadThread readThread;

	private String rootPath = null;

	//The map that associate
	private PieceList[] pieceMap;
	private int pieceCompletion[];
	private BitSet[] priorityLists;
	//private int[][] priorityLists;

	private DiskManagerFileInfoImpl[] files;

	//long[] filesDone;
	//RandomAccessFile[] fileArray;

	private PEPeerManager manager;
	private SHA1Hasher hasher;
  private Md5Hasher md5;
  private ByteBuffer md5Result;
	private boolean bContinue = true;
	private PEPiece[] pieces;
	private boolean alreadyMoved = false;

	// DiskManager listeners
	
	private static final int LDT_STATECHANGED		= 1;
	
	private ListenerManager	listeners 	= ListenerManager.createManager(
			"DiskM:ListenDispatcher",
			new ListenerManagerDispatcher()
			{
				public void
				dispatch(
					Object		_listener,
					int			type,
					Object		value )
				{
					DiskManagerListener	listener = (DiskManagerListener)_listener;
					
					listener.stateChanged(((Integer)value).intValue());
				}
			});		
	
  private static boolean useFastResume = COConfigurationManager.getBooleanParameter("Use Resume", true);
  private static boolean firstPiecePriority = COConfigurationManager.getBooleanParameter("Prioritize First Piece", false);
  
	public DiskManagerImpl(TOTorrent	_torrent, String path) {
		setState( INITIALIZING );
		this.percentDone = 0;
		this.torrent = _torrent;
      this.path = path;

		md5 = new Md5Hasher();
		md5Result = ByteBuffer.allocate(16);
    
		try {
			hasher = new SHA1Hasher();
		} catch (NoSuchAlgorithmException ignore) {/*ignore*/}
    
    // add configuration parameter listeners
    COConfigurationManager.addParameterListener("Use Resume", this);
    COConfigurationManager.addParameterListener("Prioritize First Piece", this);
    
    Thread init = new Thread() {
			public void run() {
				initialize();
				if (getState() == DiskManager.FAULTY) {
					stopIt();
				}
			}
		};
		init.setPriority(Thread.MIN_PRIORITY);
		init.start();
	}

	private void initialize() {

		pieceLength = (int)torrent.getPieceLength();

		piecesHash = torrent.getPieces();
        
		nbPieces = piecesHash.length;

		//  create the pieces map
		pieceMap = new PieceList[nbPieces];
		pieceCompletion = new int[nbPieces];
		priorityLists = new BitSet[100];
		//    priorityLists = new int[10][nbPieces + 1];

		// the piece numbers for getPiecenumberToDownload
		//    _priorityPieces = new int[nbPieces + 1];

		pieceDone = new boolean[nbPieces];
		
		LocaleUtilDecoder	locale_decoder = null;
		
		try{
		
			locale_decoder = LocaleUtil.getTorrentEncoding( torrent );
	
			fileName = "";
		
			File f = new File(path);
			
			if (f.isDirectory()) {
				fileName = locale_decoder.decodeString( torrent.getName());
			} else {
			  fileName = f.getName();
			  path = f.getParent();
			}
		}catch( TOTorrentException e ){
			this.errorMessage = TorrentUtils.exceptionToText(e);
			setState( FAULTY );
			return;
		}catch( UnsupportedEncodingException e ){
			this.errorMessage = e.getMessage();
			setState( FAULTY );
			return;
		}
      
		//if the data file is already in the completed files dir, we want to use it
		boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done", false);
		String completedDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
   
		if (moveWhenDone && completedDir.length() > 0) {
		  //if the data file already resides in the completed files dir
		  if (new File(completedDir, fileName).exists()) {
		    //set the completed dir as the save path
		    this.path = FileUtil.smartPath(completedDir, fileName);
		    alreadyMoved = true;
		  }
		}

		//build something to hold the filenames/sizes
		ArrayList btFileList = new ArrayList();

		//Create the ByteBuffer for checking (size : pieceLength)
    allocateAndTestBuffer = ByteBufferPool.getFreeBuffer(pieceLength);
    
		allocateAndTestBuffer.limit(pieceLength);
		for (int i = 0; i < allocateAndTestBuffer.limit(); i++) {
			allocateAndTestBuffer.put((byte)0);
		}
		allocateAndTestBuffer.position(0);

		//Create the new Queue
		writeQueue = new Vector();
		checkQueue = new Vector();
		readQueue = new Vector();
		writeThread = new DiskWriteThread();
		writeThread.start();
		readThread = new DiskReadThread();
		readThread.start();

		//2. Distinguish between simple file

		TOTorrentFile[] torrent_files = torrent.getFiles();

		if ( torrent.isSimpleTorrent()){
        	
        	
			totalLength = torrent_files[0].getLength();
            
			rootPath = "";
            
			btFileList.add(new BtFile("", fileName, totalLength));
		} else {
			//define a variable to keep track of what piece we're on
			//      int currentPiece = 0;

			final char separator = System.getProperty("file.separator").charAt(0);

			//get the root
			rootPath = fileName + separator;

			//:: Directory patch 08062003 - Tyler
			//check for a user selecting the full path
			String fullPath = path + separator;
			int fullPathIndex = fullPath.lastIndexOf(rootPath);
			if (fullPathIndex >= 0 && fullPathIndex == (fullPath.length() - rootPath.length())) {
				rootPath = ""; //null out rootPath
			}

			buildFileLookupTables( torrent_files, btFileList, locale_decoder, separator);

			if (getState() == FAULTY)
				return;
		}

		remaining = totalLength;
		lastPieceLength = (int) (totalLength - ((long) (nbPieces - 1) * (long)pieceLength));

		//we now have a list of files and their lengths
		//allocate / check every file
		//fileArray = new RandomAccessFile[btFileList.size()];
		files = new DiskManagerFileInfoImpl[btFileList.size()];
      
		int newFiles = this.allocateFiles(rootPath, btFileList);
      
		if (getState() == FAULTY) return;
    
      path = FileUtil.smartPath(path, fileName);

		constructPieceMap(btFileList);

		constructFilesPieces();

		//check all pieces if no new files were created
		if (newFiles == 0) checkAllPieces(false);
		//if not a fresh torrent, check pieces ignoring fast resume data
		else if (newFiles != btFileList.size()) checkAllPieces(true);
    
		//3.Change State   
		setState( READY );
	}

	// no changes made here, just refactored the code out from initialize() - Moti
	private void constructPieceMap(ArrayList btFileList) {
		//for every piece, except the last one
		//add files to the piece list until we have built enough space to hold the piece
		//see how much space is available in the file
		//if the space available isnt 0
		//add the file to the piece->file mapping list
		//if there is enough space available, stop  

		//fix for 1 piece torrents
		if (totalLength < pieceLength) {
			pieceLength = (int)totalLength; //ok to convert
		}

		long fileOffset = 0;
		int currentFile = 0;
		for (int i = 0;(1 == nbPieces && i < nbPieces) || i < nbPieces - 1; i++) {
			ArrayList pieceToFileList = new ArrayList();
			int usedSpace = 0;
			while (pieceLength > usedSpace) {
				BtFile tempFile = (BtFile)btFileList.get(currentFile);
				long length = tempFile.getLength();

				//get the available space
				long availableSpace = length - fileOffset;

				PieceMapEntry tempPieceEntry = null;

				//how much space do we need to use?                               
				if (availableSpace < (pieceLength - usedSpace)) {
					//use the rest of the file's space
						tempPieceEntry =
							new PieceMapEntry(tempFile.getFileInfo(), fileOffset, (int)availableSpace //safe to convert here
	);

					//update the used space
					usedSpace += availableSpace;
					//update the file offset
					fileOffset = 0;
					//move the the next file
					currentFile++;
				} else //we don't need to use the whole file
					{
					tempPieceEntry = new PieceMapEntry(tempFile.getFileInfo(), fileOffset, pieceLength - usedSpace);

					//update the file offset
					fileOffset += pieceLength - usedSpace;
					//udate the used space
					usedSpace += pieceLength - usedSpace;
				}

				//add the temp pieceEntry to the piece list
				pieceToFileList.add(tempPieceEntry);
			}

			//add the list to the map
			pieceMap[i] = PieceList.convert(pieceToFileList);
		}

		//take care of final piece if there was more than 1 piece in the torrent
		if (nbPieces > 1) {
			pieceMap[nbPieces - 1] =
				PieceList.convert(this.buildPieceToFileList(btFileList, currentFile, fileOffset, lastPieceLength));
		}
	}

	// refactored out of initialize() - Moti
	private void 
	buildFileLookupTables(
		TOTorrentFile[]	torrent_files, 
		ArrayList btFileList, 
		LocaleUtilDecoder locale_decoder, 
		final char separator) {
 
		 //for each file
         
		for (int i = 0; i < torrent_files.length; i++) {
        	
			long fileLength = buildFileLookupTable(torrent_files[i], btFileList, locale_decoder, separator);

			if (getState() == FAULTY)
				return;

			//increment the global length 
			totalLength += fileLength;
		}
	}

	/**
	 * Builds the path stored in fileDictionay, saving it in btFileList
	 * @param fileDictionay
	 * @param btFileList
	 * @param localeUtil
	 * @param separator
	 * @return the length of the file as stored in fileDictionay
	 */
	// refactored out of initialize() - Moti
	// code further refactored for readibility
	private long 
	buildFileLookupTable(
		TOTorrentFile		torrent_file, 
		ArrayList 			btFileList, 
		LocaleUtilDecoder 	locale_decoder, 
		final char 			separator) 
	{
		long fileLength  = torrent_file.getLength();

		//build the path
        
		byte[][]	path_components = torrent_file.getPathComponents();

		/* replaced the following two calls:
		StringBuffer pathBuffer = new StringBuffer(256);
		pathBuffer.setLength(0);
		*/
		StringBuffer pathBuffer = new StringBuffer(0);

	    try{

			int lastIndex = path_components.length - 1;
			for (int j = 0; j < lastIndex; j++) {
				//attach every element        
				pathBuffer.append(locale_decoder.decodeString( path_components[j]));
				pathBuffer.append(separator);
			}
	
			//no, then we must be a part of the path
			//add the file entry to the file holder list         
			btFileList.add(
				new BtFile(
					pathBuffer.toString(),
					locale_decoder.decodeString(path_components[lastIndex]),
					fileLength));
		}catch( UnsupportedEncodingException e ){
			this.errorMessage = e.getMessage();
			setState( FAULTY );
		}
 
		return fileLength;
	}

  
	private List buildPieceToFileList(List btFileList, int currentFile, long fileOffset, int pieceSize) {
		ArrayList pieceToFileList = new ArrayList();
		int usedSpace = 0;
		while (pieceSize > usedSpace) {
			BtFile tempFile = (BtFile)btFileList.get(currentFile);
			long length = tempFile.getLength();

			//get the available space
			long availableSpace = length - fileOffset;

			PieceMapEntry tempPieceEntry = null;

			//how much space do we need to use?                               
			if (availableSpace < (pieceLength - usedSpace)) {
				//use the rest of the file's space
				tempPieceEntry = new PieceMapEntry(tempFile.getFileInfo(), fileOffset, (int)availableSpace);

				//update the used space
				usedSpace += availableSpace;
				//update the file offset
				fileOffset = 0;
				//move the the next file
				currentFile++;
			} else //we don't need to use the whole file
				{
				tempPieceEntry = new PieceMapEntry(tempFile.getFileInfo(), fileOffset, pieceSize - usedSpace);

				//update the file offset
				fileOffset += pieceLength - usedSpace;
				//udate the used space
				usedSpace += pieceLength - usedSpace;
			}

			//add the temp pieceEntry to the piece list
			pieceToFileList.add(tempPieceEntry);
		}

		return pieceToFileList;
	}

	private static class FlyWeightInteger {
		private static Vector array = new Vector(1024);

		public static synchronized Integer getInteger(int value) {
			Integer tmp = null;
			if (value >= array.size()) {
				array.setSize(value + 256);
			} else {
				tmp = (Integer)array.get(value);
			}
			if (tmp == null) {
				tmp = new Integer(value);
				array.set(value, tmp);
			}
			return tmp;

		}
	}

	private static class BtFile {
		private DiskManagerFileInfoImpl _file;
		private String _path;
		private String _name;
		private String _originalName = null;
		private long _length;
		private static final String[] unsupportedChars = { "[\\/:?*]" };
		// 0 = Windows: \ / : ? * and any other Unicode letters ('?')

		public BtFile(String path, String name, long length) {
			_path = path;
			_length = length;
			_name = name;

			String newName = name.replace('"', '\'');

			if (System.getProperty("os.name").startsWith("Windows")) {
				newName = newName.replaceAll(unsupportedChars[0], "_");
			}

			if (!name.equals(newName)) {
				_name = newName;
				_originalName = name;
			}

		}
		public long getLength() {
			return _length;
		}
		public String getPath() {
			return _path;
		}
		public boolean isNameOriginal() {
			return _originalName == null;
		}
		public String getOriginalName() {
			return _originalName == null ? _name : _originalName;
		}
		public String getName() {
			return _name;
		}
		public DiskManagerFileInfoImpl getFileInfo() {
			return _file;
		}
		public void setFileInfo(DiskManagerFileInfoImpl file) {
			_file = file;
		}
	}

	public class QueueElement {
		private int pieceNumber;
		private int offset;
		private ByteBuffer data;
    private PEPeer sender; 

		public QueueElement(int pieceNumber, int offset, ByteBuffer data,PEPeer sender) {
			this.pieceNumber = pieceNumber;
			this.offset = offset;
			this.data = data;
      this.sender = sender;
		}  

		public int getPieceNumber() {
			return this.pieceNumber;
		}

		public int getOffset() {
			return this.offset;
		}

		public ByteBuffer getData() {
			return this.data;
		}
    
    public PEPeer getSender() {
      return this.sender;
	}
	}

	public class DiskReadThread extends Thread {
		private boolean bContinue = true;

		public DiskReadThread() {
			super("Disk Reader");
			setDaemon(true);
		}

		public void run() {
			while (bContinue) {
				while (readQueue.size() != 0) {
					DiskManagerDataQueueItemImpl item = (DiskManagerDataQueueItemImpl)readQueue.remove(0);
					DiskManagerRequest request = item.getRequest();

					// temporary fix for bug 784306
					ByteBuffer buffer = readBlock(request.getPieceNumber(), request.getOffset(), request.getLength());
					if (buffer != null) {
						item.setBuffer(buffer);
					} else {
					  item.setLoading(false);
					  LGLogger.log(LGLogger.ERROR,"Failed loading piece " + 
					      item.getRequest().getPieceNumber() + ":" +
					  		item.getRequest().getOffset() + "->" +
					  		(item.getRequest().getOffset() + item.getRequest().getLength()));
					  System.out.println("Read Error");
					}
				}
				try {
					Thread.sleep(15);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void stopIt() {
			this.bContinue = false;
			while (readQueue.size() != 0) {
				DiskManagerDataQueueItemImpl item = (DiskManagerDataQueueItemImpl)readQueue.remove(0);
				item.setLoading(false);
			}
		}
	}

	public class DiskWriteThread extends Thread {
		private boolean bContinue = true;

		public DiskWriteThread() {
			super("Disk Writer & Checker");
			setDaemon(true);
		}

		public void run() {
         int count;
         long sleepTime;

			while (bContinue) {
            
            count = 0;
            sleepTime = 1000;
            
				if (writeQueue.size() > 64) sleepTime = 20;

            //allow up to 64 blocks to be written at once
            while (writeQueue.size() != 0 && count < 64) {
					QueueElement elt = (QueueElement)writeQueue.remove(0);
					//Do not allow to write in a piece marked as done.
					int pieceNumber = elt.getPieceNumber();					
					if(!pieceDone[pieceNumber]) {
					  dumpBlockToDisk(elt);
					  manager.blockWritten(elt.getPieceNumber(), elt.getOffset(),elt.getSender());
					} else {
					  ByteBufferPool.freeBuffer(elt.getData());
					  elt.data = null;
					}

               count++;
				}
            
            count = 0;

            if (checkQueue.size() > 10) sleepTime = 20;

            //allow up to 10 piece checks at once
				while (checkQueue.size() != 0 && count < 10) {
				  QueueElement elt = (QueueElement)checkQueue.remove(0);
				  boolean correct = checkPiece(elt.getPieceNumber());
					
				  if(!correct) {
				    MD5CheckPiece(elt.getPieceNumber(),false);
				    LGLogger.log(0, 0, LGLogger.ERROR, "Piece " + elt.getPieceNumber() + " failed hash check.");
				  }
				  else {
				    LGLogger.log(0, 0, LGLogger.INFORMATION, "Piece " + elt.getPieceNumber() + " passed hash check.");
				    if(manager.needsMD5CheckOnCompletion(elt.getPieceNumber())) {
				      MD5CheckPiece(elt.getPieceNumber(),true);
				    }
				  }

				  manager.asyncPieceChecked(elt.getPieceNumber(), correct);
              count++;
				}
            				
				try {
                Thread.sleep(sleepTime);
            } catch (Exception e) { e.printStackTrace(); }
			}
		}

		public void stopIt() {
			this.bContinue = false;
			while (writeQueue.size() != 0) {
				QueueElement elt = (QueueElement)writeQueue.remove(0);
				ByteBufferPool.freeBuffer(elt.data);
				elt.data = null;
			}
		}
       
	}

	private int allocateFiles(String rootPath, List fileList) {
		setState( ALLOCATING );
		allocated = 0;
		int numNewFiles = 0;
		String basePath = path + System.getProperty("file.separator") + rootPath;
		
		for (int i = 0; i < fileList.size(); i++) {
			//get the BtFile
			BtFile tempFile = (BtFile)fileList.get(i);
			//get the path
			String tempPath = basePath + tempFile.getPath();
			//get file name
			String tempName = tempFile.getName();
			//get file length
			long length = tempFile.getLength();

			File f = new File(tempPath, tempName);

			RandomAccessFile raf = null;

			boolean incremental = COConfigurationManager.getBooleanParameter("Enable incremental file creation", false);
			boolean preZero = COConfigurationManager.getBooleanParameter("Zero New", false);
         boolean bCreateFile = false;
			
			if (!f.exists()) {
			  bCreateFile = true;
			}
			else if (f.length() != length) {
				if (!incremental || f.length() > length ) bCreateFile = true;
			}
			
			if (bCreateFile) {
				//File doesn't exist
			   numNewFiles++;
				
				try {
					buildDirectoryStructure(tempPath);
					//test: throws Exception if filename is not supported by os
					f.getCanonicalPath();
					//create the new file
					raf = new RandomAccessFile(f, "rwd");
					//if we don't want incremental file creation, pre-allocate file
					if (!incremental) raf.setLength(length);
					
					
					//if we want to pre-fill file with zeros
					if (preZero) {
					  //pre-allocate
					  raf.setLength(length);
					  //and zero
					  boolean ok = zeroFile(raf);
					  if (!ok) {
					    setState( FAULTY );
					    return -1;
					  }
					}
					

				} catch (Exception e) {
					try {
							
						if ( raf != null ){
						
							raf.close();
						}
					} catch (IOException ex) { ex.printStackTrace(); }
					this.errorMessage = e.getMessage();
					setState( FAULTY );
					return -1;
				}
        
			//the file already exists
			} else {               
				try {
					raf = new RandomAccessFile(f, "rwd");
				} catch (FileNotFoundException e) {
					this.errorMessage = e.getMessage();
					setState( FAULTY );
					return -1;
				}
				allocated += length;
			}

			//add the file to the array

			DiskManagerFileInfoImpl fileInfo = new DiskManagerFileInfoImpl();
			fileInfo.setPath(tempPath);
			fileInfo.setName(tempName);
			int separator = tempName.lastIndexOf(".");
			if (separator == -1)
				separator = 0;
			fileInfo.setExtension(tempName.substring(separator));
            
			//Added for Feature Request
			//[ 807483 ] Prioritize .nfo files in new torrents
			//Implemented a more general way of dealing with it.
			String extensions = COConfigurationManager.getStringParameter("priorityExtensions","");
			if(!extensions.equals("")) {
				StringTokenizer st = new StringTokenizer(extensions,";");
				while(st.hasMoreTokens()) {
				  String extension = st.nextToken();
				  extension = extension.trim();
				  if(!extension.startsWith("."))
					extension = "." + extension;
				  if(fileInfo.getExtension().equals(extension)) {
					fileInfo.setPriority(true);
				  }                    
				}
			}
            
            
			fileInfo.setLength(length);
			fileInfo.setDownloaded(0);
			fileInfo.setFile(f);
			fileInfo.setRaf(raf);
			fileInfo.setAccessmode(DiskManagerFileInfo.WRITE);
			files[i] = fileInfo;

			//setup this files RAF reference
			tempFile.setFileInfo(files[i]);
		}
		return numNewFiles;
	}


  private boolean zeroFile(RandomAccessFile file) {
		FileChannel fc = file.getChannel();
		long length;
		try {
			length = file.length();
		} catch (IOException e) {
			this.errorMessage = e.getMessage();
			setState( FAULTY );
			return false;
		}
		long written = 0;
		synchronized (file) {
			try {
				fc.position(0);
				while (written < length && bContinue) {
					allocateAndTestBuffer.limit(allocateAndTestBuffer.capacity());
					if ((length - written) < allocateAndTestBuffer.remaining())
						allocateAndTestBuffer.limit((int) (length - written));
					int deltaWriten = fc.write(allocateAndTestBuffer);
					allocateAndTestBuffer.position(0);
					written += deltaWriten;
					allocated += deltaWriten;
					percentDone = (int) ((allocated * 1000) / totalLength);
				}
				if (!bContinue) {
				   fc.close();
				   return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        return true;
	}
  

	private void buildDirectoryStructure(String file) {
		File tempFile = new File(file);
		tempFile.mkdirs();
	}

  
   public void aSyncCheckPiece(int pieceNumber) {
		checkQueue.add(new QueueElement(pieceNumber, 0, null, null));
	}
  

	public synchronized boolean checkPiece(int pieceNumber) {
        
      if (this.bContinue == false) return false;

		allocateAndTestBuffer.position(0);

		int length = pieceNumber < nbPieces - 1 ? pieceLength : lastPieceLength;

		allocateAndTestBuffer.limit(length);

		//get the piece list
		PieceList pieceList = pieceMap[pieceNumber];

		//for each piece
		for (int i = 0; i < pieceList.size(); i++) {
			//get the piece and the file 
			PieceMapEntry tempPiece = pieceList.get(i);
            
         FileChannel fc = tempPiece.getFile().getRaf().getChannel();
            
			synchronized (fc) {
				//grab it's data and return it
				try {
                    
					if (fc.isOpen()) {
					   //if the file is large enough
                  //if (fc.size() >= tempPiece.getOffset()) {
						if (fc.size() >= (tempPiece.getOffset() + tempPiece.getLength())) {
							fc.position(tempPiece.getOffset());
							fc.read(allocateAndTestBuffer);
						} else {
						   //too small, can't be a complete piece
							allocateAndTestBuffer.clear();
							pieceDone[pieceNumber] = false;
							return false;
						}
					}
               else {
               	Debug.out("file channel is not open");
               }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			allocateAndTestBuffer.position(0);

			byte[] testHash = hasher.calculateHash(allocateAndTestBuffer);
			int i = 0;
			for (i = 0; i < 20; i++) {
				if (testHash[i] != piecesHash[pieceNumber][i])
					break;
			}
			if (i >= 20) {
				//mark the piece as done..
				if (!pieceDone[pieceNumber]) {
					pieceDone[pieceNumber] = true;
					remaining -= length;
					computeFilesDone(pieceNumber);
				}
				return true;
			}
			if(pieceDone[pieceNumber]) {
			  pieceDone[pieceNumber] = false;
			  remaining += length;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

		// RESUME DATA STUFF STARTS.....
	
	private void checkAllPieces(boolean newfiles) {
		setState( CHECKING );
		int startPos = 0;
		
		boolean resumeEnabled = useFastResume;
		//disable fast resume if a new file was created
		if (newfiles) resumeEnabled = false;
		
		boolean	resume_data_complete = false;
		
		if (resumeEnabled) {
			boolean resumeValid = false;
			byte[] resumeArray = null;
			Map partialPieces = null;
			Map resumeMap = torrent.getAdditionalMapProperty("resume");
			
			if (resumeMap != null) {
				
				// see bug 869749 for explanation of this mangling
				
				/*
				System.out.println( "Resume map");
				
				Iterator it = resumeMap.keySet().iterator();
				
				while( it.hasNext()){
					
					System.out.println( "\tmap:" + ByteFormatter.nicePrint((String)it.next()));
				}
				*/
				
				String mangled_path;
				
				try{
					mangled_path = new String(path.getBytes(Constants.DEFAULT_ENCODING),Constants.BYTE_ENCODING);
					
					// System.out.println( "resume: path = " + ByteFormatter.nicePrint(path )+ ", mangled_path = " + ByteFormatter.nicePrint(mangled_path));
					
				}catch( Throwable e ){
					
					e.printStackTrace();
					
					mangled_path = this.path;
				}
				
				Map resumeDirectory = (Map)resumeMap.get(mangled_path);
				
				if ( resumeDirectory == null ){
					
						// unfortunately, if the torrent hasn't been saved and restored then the
						// mangling with not yet have taken place. So we have to also try the 
						// original key (see 878015)
					
					resumeDirectory = (Map)resumeMap.get(path);
				}
				
				if ( resumeDirectory != null ){
					
					try {
						
						resumeArray = (byte[])resumeDirectory.get("resume data");
						partialPieces = (Map)resumeDirectory.get("blocks");
						resumeValid = ((Long)resumeDirectory.get("valid")).intValue() == 1;
						
							// if the torrent download is complete we don't need to invalidate the
							// resume data
						
						if ( isTorrentResumeDataComplete( torrent, path )){
							
							resume_data_complete	= true;
									
						}else{
							
							resumeDirectory.put("valid", new Long(0));
							
							saveTorrent();
						}
						
					}catch(Exception ignore){
						/* ignore */ 
					}
					
				}else{
					
					// System.out.println( "resume dir not found");
				}
			}
			
			if (resumeEnabled && (resumeArray != null) && (resumeArray.length <= pieceDone.length)) {
				startPos = resumeArray.length;
				for (int i = 0; i < resumeArray.length && bContinue; i++) { //parse the array
					percentDone = ((i + 1) * 1000) / nbPieces;
					//mark the pieces
					if (resumeArray[i] == 0) {
						if (!resumeValid) pieceDone[i] = checkPiece(i);
					}
					else {
						computeFilesDone(i);
						pieceDone[i] = true;
						if (i < nbPieces - 1) {
							remaining -= pieceLength;
						}
						if (i == nbPieces - 1) {
							remaining -= lastPieceLength;
						}
					}
				}
				
				if (partialPieces != null && resumeValid) {
					pieces = new PEPiece[nbPieces];
					Iterator iter = partialPieces.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry key = (Map.Entry)iter.next();
						int pieceNumber = Integer.parseInt((String)key.getKey());
						PEPiece piece;
						if (pieceNumber < nbPieces - 1)
							piece = PEPieceFactory.create(manager, getPieceLength(), pieceNumber);
						else
							piece = PEPieceFactory.create(manager, getLastPieceLength(), pieceNumber);
						List blocks = (List)partialPieces.get(key.getKey());
						Iterator iterBlock = blocks.iterator();
						while (iterBlock.hasNext()) {
							piece.setWritten(null,((Long)iterBlock.next()).intValue());
						}
						pieces[pieceNumber] = piece;
					}
				}
			}
		}
		
		for (int i = startPos; i < nbPieces && bContinue; i++) {
			percentDone = ((i + 1) * 1000) / nbPieces;
			checkPiece(i);
		}
		
			//dump the newly built resume data to the disk/torrent
		
		if (bContinue && resumeEnabled && !resume_data_complete){
			
			dumpResumeDataToDisk(false, false);
		}
	}
	
	public void 
	dumpResumeDataToDisk(
		boolean savePartialPieces, 
		boolean invalidate )
	{
		if(!useFastResume)
		  return;
    
		boolean	was_complete = isTorrentResumeDataComplete( torrent, path );
		
		//build the piece byte[] 
		byte[] resumeData = new byte[pieceDone.length];
		for (int i = 0; i < resumeData.length; i++) {
		  if (invalidate) resumeData[i] = (byte)0;
		  else resumeData[i] = pieceDone[i] ? (byte)1 : (byte)0;
		}

		//Attach the resume data
		Map resumeMap = new HashMap();
		torrent.setAdditionalMapProperty("resume", resumeMap);

	  Map resumeDirectory = new HashMap();
	  
	  	// We *really* shouldn't be using a localised string as a Map key (see bug 869749)
	  	// currently fixed by mangling such that decode works
	  
	  // System.out.println( "writing resume data: key = " + ByteFormatter.nicePrint(path));
	  
	  resumeMap.put(path, resumeDirectory);
	  
	  resumeDirectory.put("resume data", resumeData);
	  Map partialPieces = new HashMap();
	
	  if (savePartialPieces  && !invalidate) {
	    if (pieces == null && manager != null)
			pieces = manager.getPieces();
	    if(pieces != null) {
	      for (int i = 0; i < pieces.length; i++) {
	        PEPiece piece = pieces[i];
	        if (piece != null && piece.getCompleted() > 0) {
	          boolean[] downloaded = piece.getWritten();
	          List blocks = new ArrayList();
	          for (int j = 0; j < downloaded.length; j++) {
	            if (downloaded[j])
	              blocks.add(new Long(j));
	          }
	          partialPieces.put("" + i, blocks);
	        }
	      }
	      resumeDirectory.put("blocks", partialPieces);
	    }
	    resumeDirectory.put("valid", new Long(1));
	  } else {
	    resumeDirectory.put("valid", new Long(0));
	  }
		
	  boolean	is_complete = isTorrentResumeDataComplete( torrent, path );
	  
	  if ( was_complete && is_complete ){
	 
	  		// no change, no point in writing
	  		  	
	  }else{
	  	
	  	saveTorrent();
	  }
	}

	public static void
	setTorrentResumeDataComplete(
		TOTorrent	torrent,
		String		data_dir )
	{
		int	piece_count = torrent.getPieces().length;
		
		byte[] resumeData = new byte[piece_count];
		
		for (int i = 0; i < resumeData.length; i++) {
			
			resumeData[i] = (byte)1;
		}

		Map resumeMap = new HashMap();
		
		torrent.setAdditionalMapProperty("resume", resumeMap);

		Map resumeDirectory = new HashMap();
		
		// We *really* shouldn't be using a localised string as a Map key (see bug 869749)
		// currently fixed by mangling such that decode works
		
		resumeMap.put(data_dir, resumeDirectory);
		
		resumeDirectory.put("resume data", resumeData);
		
		Map partialPieces = new HashMap();
		
		resumeDirectory.put("blocks", partialPieces);
		
		resumeDirectory.put("valid", new Long(1));	
	}
	
	public static boolean
	isTorrentResumeDataComplete(
		TOTorrent	torrent,
		String		data_dir )
	{
		try{
			int	piece_count = torrent.getPieces().length;
		
			Map resumeMap = torrent.getAdditionalMapProperty("resume");
		
			if (resumeMap != null) {
			
					// see bug 869749 for explanation of this mangling
				
				String mangled_path;
				
				try{
					mangled_path = new String(data_dir.getBytes(Constants.DEFAULT_ENCODING),Constants.BYTE_ENCODING);
									
				}catch( Throwable e ){
					
					e.printStackTrace();
					
					mangled_path = data_dir;
				}
				
				Map resumeDirectory = (Map)resumeMap.get(mangled_path);
				
				if ( resumeDirectory == null ){
					
					// unfortunately, if the torrent hasn't been saved and restored then the
					// mangling with not yet have taken place. So we have to also try the 
					// original key (see 878015)
					
					resumeDirectory = (Map)resumeMap.get(data_dir);
				}
				
				if (resumeDirectory != null) {
										
					byte[] 	resume_data =  (byte[])resumeDirectory.get("resume data");
					Map		blocks		= (Map)resumeDirectory.get("blocks");
					boolean	valid		= ((Long)resumeDirectory.get("valid")).intValue() == 1;
					
						// any partial pieced -> not complete
					if ( blocks == null || blocks.size() > 0 ){
						
						return( false );
					}
					
					if ( valid && resume_data.length == piece_count ){
						
						for (int i=0;i<resume_data.length;i++){
	
							if ( resume_data[i] == 0 ){
								
									// missing piece
								
								return( false );
							}
						}
						
						return( true );
					}
				}
			}
		}catch( Throwable e ){
		
			e.printStackTrace();
		}	
		
		return( false );
	}
	
		// RESUME DATA STUFF ENDS
	
	private void 
	saveTorrent() 
	{
		try{
			TorrentUtils.writeToFile( torrent );
						
		} catch (TOTorrentException e) {
			
			e.printStackTrace();
		}
	}

	public void enqueueReadRequest(DiskManagerDataQueueItem item) {
		readQueue.add(item);
		((DiskManagerDataQueueItemImpl)item).setLoading( true );
	}

	public ByteBuffer readBlock(int pieceNumber, int offset, int length) {

		ByteBuffer buffer = ByteBufferPool.getFreeBuffer(length+13);

		if (buffer == null) { // Fix for bug #804874
			System.out.println("DiskManager::readBlock:: ByteBufferPool returned null buffer");
			return null;
		}

		buffer.position(0);
		buffer.limit(length + 13);
		buffer.putInt(length + 9);
		buffer.put((byte)7);
		buffer.putInt(pieceNumber);
		buffer.putInt(offset);

		long previousFilesLength = 0;
		int currentFile = 0;
		PieceList pieceList = pieceMap[pieceNumber];

		// temporary fix for bug 784306
		if (pieceList.size() == 0) {
			System.out.println("no pieceList entries for " + pieceNumber);
			return buffer;
		}

		long fileOffset = pieceList.get(0).getOffset();
		while (currentFile < pieceList.size() && pieceList.getCumulativeLengthToPiece(currentFile) < offset) {
			previousFilesLength = pieceList.getCumulativeLengthToPiece(currentFile);
			currentFile++;
			fileOffset = 0;
		}

		// update the offset (we're in the middle of a file)
		fileOffset += offset - previousFilesLength;
		// noError is only used for error reporting, it could probably be removed
		boolean noError = true;
		while (buffer.hasRemaining()
			&& currentFile < pieceList.size()
			&& (noError = readFileInfoIntoBuffer(pieceList.get(currentFile).getFile(), buffer, fileOffset))) {

			currentFile++;
			fileOffset = 0;
		}

		if (!noError) {
			// continue the error report
			//PieceMapEntry tempPiece = pieceList.get(currentFile);
			//System.out.println("ERROR IN READ BLOCK (CONTINUATION FROM READ FILE INFO INTO BUFFER): *Debug Information*");
			//System.out.println("BufferLimit: " + buffer.limit());
			//System.out.println("BufferRemaining: " + buffer.remaining());
			//System.out.println("PieceNumber: " + pieceNumber);
			//System.out.println("Offset: " + fileOffset);
			//System.out.println("Length  " + length);
			//System.out.println("PieceLength: " + tempPiece.getLength());
			//System.out.println("PieceOffset: " + tempPiece.getOffset());
			//System.out.println("TotalNumPieces(this.nbPieces): " + this.nbPieces);
		}

		buffer.position(0);
		return buffer;
	}

	// refactored out of readBlock() - Moti
	// reads a file into a buffer, returns true when no error, otherwise false.
	private boolean readFileInfoIntoBuffer(DiskManagerFileInfoImpl file, ByteBuffer buffer, long offset) {
		  FileChannel fc = file.getRaf().getChannel();
        
        synchronized (fc) {
      
			if (!fc.isOpen()) {
			  Debug.out("FileChannel is closed: " + file.getFile().getAbsolutePath());
			  return false;
			}

			long fcposition = 0;
			long fcsize = 0;

			try {
				fc.position(offset);
				while (fc.position() < (fc.size() - 1) && buffer.hasRemaining()) {
					fcposition = fc.position();
					fcsize = fc.size();
					fc.read(buffer);
				}
				return true;
			} catch (Exception e) {	
				e.printStackTrace();
				return false;
			}
		}
	}

	public void writeBlock(int pieceNumber, int offset, ByteBuffer data,PEPeer sender) {
		writeQueue.add(new QueueElement(pieceNumber, offset, data,sender));
	}

	public boolean checkBlock(int pieceNumber, int offset, ByteBuffer data) {
		if (pieceNumber < 0)
			return false;
		if (pieceNumber >= this.nbPieces)
			return false;
		int length = this.pieceLength;
		if (pieceNumber == nbPieces - 1)
			length = this.lastPieceLength;
		if (offset < 0)
			return false;
		if (offset > length)
			return false;
		int size = data.remaining();
		if (offset + size > length)
			return false;
		return true;
	}

	public boolean checkBlock(int pieceNumber, int offset, int length) {
		if (length > 65536) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: length="+length+" > 65536");
		  return false;
		}
		if (pieceNumber < 0) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: pieceNumber="+pieceNumber+" < 0");
		  return false;
      }
		if (pieceNumber >= this.nbPieces) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: pieceNumber="+pieceNumber+" >= this.nbPieces="+this.nbPieces);
		  return false;
      }
		int pLength = this.pieceLength;
		if (pieceNumber == this.nbPieces - 1)
			pLength = this.lastPieceLength;
		if (offset < 0) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: offset="+offset+" < 0");
		  return false;
		}
		if (offset > pLength) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: offset="+offset+" > pLength="+pLength);
		  return false;
		}
		if (offset + length > pLength) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: offset="+offset+" + length="+length+" > pLength="+pLength);
		  return false;
		}
		if(!this.pieceDone[pieceNumber]) {
		  LGLogger.log(0, 0, LGLogger.ERROR, "CHECKBLOCK: pieceNumber="+pieceNumber+" not done");
		  return false;
		}
		return true;
	}

	private void dumpBlockToDisk(QueueElement e) {
		int pieceNumber = e.getPieceNumber();
		int offset = e.getOffset();
		ByteBuffer buffer = e.getData();
       
      if (buffer == null) {
        Debug.out("buffer is null");
      }

		int previousFilesLength = 0;
		int currentFile = 0;
		PieceList pieceList = pieceMap[pieceNumber];
		PieceMapEntry tempPiece = pieceList.get(currentFile);
		long fileOffset = tempPiece.getOffset();
		while ((previousFilesLength + tempPiece.getLength()) < offset) {
			previousFilesLength += tempPiece.getLength();
			currentFile++;
			fileOffset = 0;
			tempPiece = pieceList.get(currentFile);
		}

		//Now tempPiece points to the first file that contains data for this block
		while (buffer.hasRemaining()) {
			tempPiece = pieceList.get(currentFile);
            
         FileChannel fc = tempPiece.getFile().getRaf().getChannel();
            
			synchronized (fc) {
				try {
					if (fc.isOpen()) {
						fc.position(fileOffset + (offset - previousFilesLength));
					   int realLimit = buffer.limit();
					   
					   long limit = buffer.position() + ((tempPiece.getFile().getLength() - tempPiece.getOffset()) - (offset - previousFilesLength));
          
					   if (limit < realLimit) {
					    	buffer.limit((int)limit);
					   }

					   if (buffer.position() < buffer.limit()) {
					   	fc.write(buffer);
					   }
					   else { 
					   	Debug.out("buffer.position [" +buffer.position()+ "] is not < buffer.limit [" +buffer.limit()+ "]");
					   }
                    
					   buffer.limit(realLimit);
					}
               else {
               	Debug.out("file channel is not open !");
               }
                    
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
            
			currentFile++;
			fileOffset = 0;
			previousFilesLength = offset;
		}

		ByteBufferPool.freeBuffer(buffer);
		buffer = null;
	}
    

	public int getPiecesNumber() {
		return nbPieces;
	}

	public boolean[] getPiecesStatus() {
		return pieceDone;
	}

	public int getPercentDone() {
		return percentDone;
	}

	public long getRemaining() {
		return remaining;
	}

	public int getPieceLength() {
		return pieceLength;
	}

	public long getTotalLength() {
		return totalLength;
	}

	public int getLastPieceLength() {
		return lastPieceLength;
	}

	public int getState() {
		return state_set_via_method;
	}

	protected void
	setState(
		int		_state ) 
	{
		if ( state_set_via_method != _state ){
			
			state_set_via_method = _state;
			
			listeners.dispatch( LDT_STATECHANGED, new Integer(_state));
		}
	}
	
	public String getFileName() {
		return fileName;
	}


	public void setPeerManager(PEPeerManager manager) {
		this.manager = manager;
	}

	public void stopIt() {
        
    this.bContinue = false; 

		// remove configuration parameter listeners
	 COConfigurationManager.removeParameterListener("Use Resume", this);
    COConfigurationManager.removeParameterListener("Prioritize First Piece", this);
		
		if (writeThread != null)
			writeThread.stopIt();
		if (readThread != null)
			readThread.stopIt();
		
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				try {
					if (files[i] != null) {
						RandomAccessFile raf = files[i].getRaf();
                  synchronized( raf.getChannel() ) {
                    raf.close();
                  }
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    
    ByteBufferPool.freeBuffer(allocateAndTestBuffer);
    allocateAndTestBuffer = null;

	}


	public void computeFilesDone(int pieceNumber) {
		for (int i = 0; i < files.length; i++) {
			RandomAccessFile raf = files[i].getRaf();
			PieceList pieceList = pieceMap[pieceNumber];
			//for each piece

			for (int k = 0; k < pieceList.size(); k++) {
				//get the piece and the file 
				PieceMapEntry tempPiece = pieceList.get(k);
				if (raf == tempPiece.getFile().getRaf()) {
					long done = files[i].getDownloaded();
					done += tempPiece.getLength();
					files[i].setDownloaded(done);
					if (done == files[i].getLength())
						try {
							synchronized (files[i]) {
							  RandomAccessFile newRaf = new RandomAccessFile(files[i].getFile(), "r");
								files[i].setRaf(newRaf);
								raf.close();
								files[i].setAccessmode(DiskManagerFileInfo.READ);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}
	}

	public String[][] getFilesStatus() {
		String[][] result = new String[files.length][2];
		for (int i = 0; i < result.length; i++) {
			result[i][0] = files[i].getFile().getAbsolutePath();
			RandomAccessFile raf = files[i].getRaf();
			result[i][1] = "";
			long length = files[i].getLength();
			long done = 0;
			for (int j = 0; j < nbPieces; j++) {
				if (!pieceDone[j])
					continue;
				//get the piece list
				PieceList pieceList = pieceMap[j];
				//for each piece

				for (int k = 0; k < pieceList.size(); k++) {
					//get the piece and the file 
					PieceMapEntry tempPiece = pieceList.get(k);
					if (raf == tempPiece.getFile().getRaf()) {
						done += tempPiece.getLength();
					}
				}
			}
			int percent = 1000;
			if (length != 0)
				percent = (int) ((1000 * done) / length);
			result[i][1] = done + "/" + length + " : " + (percent / 10) + "." + (percent % 10) + " % ";
		}
		return result;
	}

	/**
	 * @return
	 */
	public DiskManagerFileInfo[] getFiles() {
		return files;
	}

	public void computePriorityIndicator() {
		for (int i = 0; i < pieceCompletion.length; i++) {
		  
		   //if the piece is already complete, skip computation
		   if (pieceDone[i]) {
		     pieceCompletion[i] = -1;
		     continue;
		   }
      
			PieceList pieceList = pieceMap[i];
			int completion = -1;
			
			for (int k = 0; k < pieceList.size(); k++) {
				//get the piece and the file 
				DiskManagerFileInfoImpl fileInfo = (pieceList.get(k)).getFile();
				
				//If the file isn't skipped
				if(fileInfo.isSkipped()) {
					continue;
				}

				//if this is the first piece of the file
				if (firstPiecePriority && i == fileInfo.getFirstPieceNumber()) {
				  if (fileInfo.isPriority()) completion = 99;
				  else completion = 97;
				}
        
        //if the file is high-priority
				else if (fileInfo.isPriority()) {
				  completion = 98;
				}
				
				//If the file is started but not completed
				else {
				  int percent = 0;
				  if (fileInfo.getLength() != 0) {
				    percent = (int) ((fileInfo.getDownloaded() * 100) / fileInfo.getLength());
				  }
				  if (percent < 100) {
				    completion = percent;
				  }
				}
			}
      
			pieceCompletion[i] = completion;
		}

		for (int i = 0; i < priorityLists.length; i++) {
			BitSet list = priorityLists[i];
			if (list == null) {
				list = new BitSet(pieceCompletion.length);
			} else {
				list.clear();
			}
			priorityLists[i]=list;
		}
		
		int priority;
		for (int j = 0; j < pieceCompletion.length; j++) {
			priority = pieceCompletion[j];
			if (priority >= 0) {
				priorityLists[priority].set(j);
			}
		}
	}

	private void constructFilesPieces() {
		for (int i = 0; i < pieceMap.length; i++) {
			PieceList pieceList = pieceMap[i];
			//for each piece

			for (int j = 0; j < pieceList.size(); j++) {
				//get the piece and the file 
				DiskManagerFileInfoImpl fileInfo = (pieceList.get(j)).getFile();
				if (fileInfo.getFirstPieceNumber() == -1)
					fileInfo.setFirstPieceNumber(i);
				fileInfo.setNbPieces(fileInfo.getNbPieces() + 1);
			}
		}
	}

	/**
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return
	 */
	public String getErrorMessage() {
		return errorMessage;
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
	/**
	 * @return
	 */
	public PEPiece[] getPieces() {
		return pieces;
	}
	
	public DiskManagerRequest
	createRequest(
		int pieceNumber,
		int offset,
		int length )
	{
		return( new DiskManagerRequestImpl( pieceNumber, offset, length ));
	}
	
	public DiskManagerDataQueueItem
	createDataQueueItem(
		DiskManagerRequest	request )
	{
		return( new DiskManagerDataQueueItemImpl( request ));
	}
  
  
  /**
   * Moves files to the CompletedFiles directory.
   * Returns a string path to the new torrent file.
   */
  public String moveCompletedFiles() {
    String fullPath;
    String subPath;
    String rPath = path;
    File destDir;
    String returnName = "";
    
    //make sure the torrent hasn't already been moved
    synchronized(this) {
      if (alreadyMoved) return returnName;
      alreadyMoved = true;
    }
    
    boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done", false);
    if (!moveWhenDone) return returnName;
    
    String moveToDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
    if (moveToDir.length() == 0) return returnName;

    try {
      for (int i=0; i < files.length; i++) {
        synchronized (files[i]) {
          //get old file pointer
          File oldFile = files[i].getFile();
          
          //make sure the 'path' var isn't refering to multi-file torrent dir
          if (rPath.endsWith(fileName)) {
            File fTest = new File(rPath);
            if(fTest.isDirectory()) rPath = fTest.getParent();
          }
          
          //get old file's parent path
          fullPath = oldFile.getParent();
          
          boolean moveOnlyInDefault = COConfigurationManager.getBooleanParameter("Move Only When In Default Save Dir", true);
          if (moveOnlyInDefault) {
            String defSaveDir = COConfigurationManager.getStringParameter("Default save path", "");
            if (!rPath.equals(defSaveDir)) {
              LGLogger.log(LGLogger.INFORMATION, "Not moving-on-complete since data is not within default save dir");
              return returnName;
            }
          }
          
          //compute the file's sub-path off from the default save path
          subPath = fullPath.substring(fullPath.indexOf(rPath) + rPath.length());
    
          //create the destination dir
          destDir = new File(moveToDir + subPath);
     
          destDir.mkdirs();
          
          //create the destination file pointer
          File newFile = new File(destDir, oldFile.getName());

          if (newFile.exists()) {
            String msg = "" + oldFile.getName() + " already exists in MoveTo destination dir";
            LGLogger.log(LGLogger.ERROR,msg);
            Debug.out(msg);
            return returnName;
          }
          
          //close the currently open stream so we can move the file
          files[i].getRaf().close();
          
          //move the file ~ rename old file pointer to new file pointer
          if (oldFile.renameTo(newFile)) {
            //open the stream from the new file
            RandomAccessFile newRaf = new RandomAccessFile(newFile, "r");
            //set new pointers
            files[i].setRaf(newRaf);
            files[i].setAccessmode(DiskManagerFileInfo.READ);
            files[i].setFile(newFile);
            files[i].setPath(newFile.getParentFile().getAbsolutePath() + System.getProperty("file.separator"));
          }
          else {
            String msg = "Failed to move " + oldFile.getName() + " to destination dir";
            LGLogger.log(LGLogger.ERROR,msg);
            Debug.out(msg);
            return returnName;
          }
        }
      }
      
      //remove the old dir
      File tFile = new File(rPath, fileName);
      if (tFile.isDirectory() && (!moveToDir.equals(rPath))) FileUtil.recursiveDirDelete(tFile);

      
      //update internal path
      path = FileUtil.smartPath(moveToDir, fileName);
      
      //move the torrent file as well
      boolean moveTorrent = COConfigurationManager.getBooleanParameter("Move Torrent When Done", true);
      if (moveTorrent) {
        synchronized (torrent) {
          String oldFullName = torrent.getAdditionalStringProperty("torrent filename");
          File oldTorrentFile = new File(oldFullName);
          String oldFileName = oldTorrentFile.getName();
          File newTorrentFile = new File(moveToDir, oldFileName);
          if (!newTorrentFile.equals(oldTorrentFile)) {
            //save torrent to new file
            torrent.serialiseToBEncodedFile(newTorrentFile);
            //remove old torrent file
            oldTorrentFile.delete();
            //update torrent meta-info to point to new torrent file
            torrent.setAdditionalStringProperty("torrent filename", newTorrentFile.getCanonicalPath());
            returnName = newTorrentFile.getCanonicalPath();
          }
        }
      }
    } catch (Exception e) { e.printStackTrace(); }

    return returnName;
  }
   
    
  private byte[] computeMd5Hash(ByteBuffer buffer) {
    md5.reset();
    int position = buffer.position();
    md5.update(buffer);
    buffer.position(position);
    md5Result.position(0);
    md5.finalDigest(md5Result);
    byte[] result = new byte[16];
    md5Result.position(0);
    for(int i = 0 ; i < result.length ; i++) {
      result[i] = md5Result.get();
    }    
    return result;    
  }
  
  private void MD5CheckPiece(int pieceNumber,boolean correct) {
    PEPiece piece = manager.getPieces()[pieceNumber];
    if(piece == null) {
      return;
    }
    PEPeer[] writers = piece.getWriters();
    int offset = 0;
    for(int i = 0 ; i < writers.length ; i++) {
      int length = piece.getBlockSize(i);
      PEPeer peer = writers[i];
      if(peer != null) {
        ByteBuffer buffer = readBlock(pieceNumber,offset,length);
        buffer.position(13);
        byte[] hash = computeMd5Hash(buffer);
        ByteBufferPool.freeBuffer(buffer);
        buffer = null;
        piece.addWrite(i,peer,hash,correct);        
      }
      offset += length;
    }        
  }
  
  public boolean isChecking() {
    if (checkQueue.size() == 0) return false;
    else return true;
  }


  /**
   * @param parameterName the name of the parameter that has changed
   * @see org.gudy.azureus2.core3.config.ParameterListener#parameterChanged(java.lang.String)
   */
  public void parameterChanged(String parameterName) {
    useFastResume = COConfigurationManager.getBooleanParameter("Use Resume", true);
    firstPiecePriority = COConfigurationManager.getBooleanParameter("Prioritize First Piece", false);
  }

  public void
  addListener(
  	DiskManagerListener	l )
  {
 	listeners.addListener( l );
  		
  	listeners.dispatch( l, LDT_STATECHANGED, new Integer(getState()));
  }
  
  public void
  removeListener(
  	DiskManagerListener	l )
  {
  	listeners.removeListener(l);
  }
}