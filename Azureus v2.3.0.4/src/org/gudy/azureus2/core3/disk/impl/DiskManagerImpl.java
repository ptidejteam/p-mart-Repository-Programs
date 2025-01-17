/*
 * Azureus - a Java Bittorrent client
 *
 * This program is free software; you can redistribute it and/or modify
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
 * 
 * Created on Oct 18, 2003
 * Created by Paul Gardner
 * Modified Apr 13, 2004 by Alon Rohter
 * Copyright (C) 2004 Aelitis, All Rights Reserved.
 * 
 */

package org.gudy.azureus2.core3.disk.impl;

import com.aelitis.azureus.core.diskmanager.cache.CacheFileManagerException;
import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.disk.*;
import org.gudy.azureus2.core3.disk.impl.access.DMAccessFactory;
import org.gudy.azureus2.core3.disk.impl.access.DMReader;
import org.gudy.azureus2.core3.disk.impl.access.DMWriterAndChecker;
import org.gudy.azureus2.core3.disk.impl.piecepicker.DMPiecePicker;
import org.gudy.azureus2.core3.disk.impl.piecepicker.DMPiecePickerFactory;
import org.gudy.azureus2.core3.disk.impl.resume.RDResumeHandler;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.internat.LocaleUtil;
import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
import org.gudy.azureus2.core3.internat.LocaleUtilEncodingException;
import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.platform.PlatformManagerFactory;
import org.gudy.azureus2.platform.PlatformManagerCapabilities;
import org.gudy.azureus2.platform.PlatformManager;
import org.gudy.azureus2.platform.PlatformManagerException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * The disk Wrapper.
 * 
 * @author Tdv_VgA
 *
 */
public class 
DiskManagerImpl
	implements DiskManagerHelper 
{  
	private String	dm_name	= "";
	
	private boolean	used	= false;
	
	private boolean started = false;
  
	private int state_set_via_method;
	private String errorMessage = "";

	private int pieceLength;
	private int lastPieceLength;

	private int 		nbPieces;
	private long 		totalLength;
	private int 		percentDone;
	private long 		allocated;
	private long 		remaining;

    
	private	TOTorrent		torrent;


	private DMReader				reader;
	private DMWriterAndChecker		writer_and_checker;
	
	private RDResumeHandler			resume_handler;
	private DMPiecePicker			piece_picker;
	private DiskManagerPieceMapper	piece_mapper;
	
	
	
	private DiskManagerPieceImpl[]	pieces;
	private PieceList[] 			pieceMap;

	private DiskManagerFileInfoImpl[] 	files;
	
    private DownloadManager 			download_manager;

	private boolean alreadyMoved = false;

	private boolean				skipped_file_set_changed;
	private long				skipped_file_set_size;
	private long				skipped_but_downloaded;
	
	
		// DiskManager listeners
	
	private static final int LDT_STATECHANGED			= 1;
	private static final int LDT_PRIOCHANGED			= 2;
	private static final int LDT_PIECE_DONE_CHANGED		= 3;
	
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
					
					if (type == LDT_STATECHANGED){
						
						int params[] = (int[])value;
						
  						listener.stateChanged(params[0], params[1]);
  						
					}else if (type == LDT_PRIOCHANGED) {
						
					    listener.filePriorityChanged();
					    
					}else if (type == LDT_PIECE_DONE_CHANGED) {
						
					    listener.pieceDoneChanged();
					}
				}
			});		
	
	protected AEMonitor	this_mon	= new AEMonitor( "DiskManager" );
	
	public 
	DiskManagerImpl(
		TOTorrent			_torrent, 
		DownloadManager 	_dmanager) 
	{
	    torrent 	= _torrent;
	    download_manager 	= _dmanager;
	 
	    pieces		= new DiskManagerPieceImpl[0];	// in case things go wrong later
	    
	    setState( INITIALIZING );
	    
	    percentDone = 0;
	    
		if ( torrent == null ){
			
			setState( FAULTY );
			
			return;
		}
   
		LocaleUtilDecoder	locale_decoder = null;
		
		try{
			locale_decoder = LocaleUtil.getSingleton().getTorrentEncoding( torrent );

			dm_name	= ByteFormatter.nicePrint(torrent.getHash(),true);
			
		}catch( TOTorrentException e ){
			
			Debug.printStackTrace( e );
			
			this.errorMessage = TorrentUtils.exceptionToText(e) + " (Constructor)";
			
			setState( FAULTY );
			
			return;
			
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
			
			this.errorMessage = e.getMessage() + " (Constructor)";
			
			setState( FAULTY );
			
			return;
		}
		
		piece_mapper	= new DiskManagerPieceMapper( this );

			//build something to hold the filenames/sizes
		
		TOTorrentFile[] torrent_files = torrent.getFiles();

		if ( torrent.isSimpleTorrent()){
			 								
			piece_mapper.buildFileLookupTables( torrent_files[0], download_manager.getTorrentSaveFile());

		}else{

			piece_mapper.buildFileLookupTables( torrent_files, locale_decoder );
		}
		
		if ( getState() == FAULTY){
			
			return;
		}

		totalLength	= piece_mapper.getTotalLength();
		
		remaining 	= totalLength;

		nbPieces 	= torrent.getNumberOfPieces();
		
		pieceLength		 	= (int)torrent.getPieceLength();
		
		lastPieceLength  	= piece_mapper.getLastPieceLength();
		
		pieces		= new DiskManagerPieceImpl[ nbPieces ];
		
		for (int i=0;i<pieces.length;i++){
			
			pieces[i] = new DiskManagerPieceImpl( this, i );
		}
		
		reader 				= DMAccessFactory.createReader(this);
		
		writer_and_checker 	= DMAccessFactory.createWriterAndChecker(this);
		
		resume_handler		= new RDResumeHandler( this, writer_and_checker );
	
		piece_picker		= DMPiecePickerFactory.create( this );
	}

	public void 
	start() 
	{
		if ( used ){
			
			Debug.out( "DiskManager reuse not supported!!!!" );
		}
		
		used	= true;
		
		try{
			this_mon.enter();
		
			if ( started ){
				
				return;
			}
			
			if ( getState() == FAULTY ){
				
				Debug.out( "starting a faulty disk manager");
				
				return;
			}
			
			started = true;
	       
		    Thread init = 
		    	new AEThread("DiskManager:start") 
				{
					public void 
					runSupport() 
					{
						startSupport();
						
						if (DiskManagerImpl.this.getState() == DiskManager.FAULTY){
							
							DiskManagerImpl.this.stop();
						}
					}
				};
				
			init.setPriority(Thread.MIN_PRIORITY);
			
			init.start();
			
		}finally{
			
			this_mon.exit();
		}
	}

	private void 
	startSupport() 
	{		
			//if the data file is already in the completed files dir, we want to use it
		
		boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done", false);
		
		String moveToDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
   
		if ( moveWhenDone && moveToDir.length() > 0 ){
		
				//if the data file already resides in the completed files dir
					
			if ( filesExist( moveToDir )){
				
				alreadyMoved = true;
		
				download_manager.setTorrentSaveDir( moveToDir );
			}
		}

		writer_and_checker.start();
		
		reader.start();
		
			//allocate / check every file

		files = new DiskManagerFileInfoImpl[piece_mapper.getFileList().size()];
      
		int newFiles = allocateFiles();
      
		if ( getState() == FAULTY || !started ){
			
			return;
		}
    
        pieceMap = piece_mapper.constructPieceMap();

		constructFilesPieces();
		
		piece_picker.start();
		
		if ( getState() == FAULTY || !started ){
			
			return;
		}

		resume_handler.start();
		  
		if (newFiles == 0){
			
			resume_handler.checkAllPieces(false);
			
		}else if ( newFiles != files.length ){
			
				//	if not a fresh torrent, check pieces ignoring fast resume data
			
			resume_handler.checkAllPieces(true);
		}
		
			//3.Change State   
		
		setState( READY );
	}

	public void 
	stop() 
	{	
		if ( !started ){
			
			return;
		}
		
		started	= false;
		
    	writer_and_checker.stop();
    	
		reader.stop();
		
		resume_handler.stop();
		
		piece_picker.stop();
		
		if (files != null){
			
			for (int i = 0; i < files.length; i++){
				
				try{
					if (files[i] != null) {
						
						files[i].getCacheFile().close();
					}
				}catch (Exception e){
					
					Debug.printStackTrace( e );
				}
			}
		}
	}

	
	
	
	
	
	public boolean
	filesExist()
	{
		return( filesExist( download_manager.getTorrentSaveDir()));
	}

	protected boolean 
	filesExist(
		String	root_dir )
	{
		if ( !torrent.isSimpleTorrent()){
			
			root_dir += File.separator + download_manager.getTorrentSaveFile();
		}
		
		File	root_dir_file = new File( root_dir );
		
		if ( !root_dir_file.exists()){
			
				// look for something sensible to report
			
		  File current = root_dir_file;
		  
		  while( !current.exists()){
			
		  	File	parent = current.getParentFile();
		  	
		  	if ( parent == null ){
		  		
		  		break;
		  		
		  	}else if ( !parent.exists()){
		  		
		  		current	= parent;
		  		
		  	}else{
		  		
		  		if ( parent.isDirectory()){
		  			
		  			errorMessage = current.toString() + " not found.";
		  			
		  		}else{
		  			
		  			errorMessage = parent.toString() + " is not a directory.";
		  		}
		  		
		  		return( false );
		  	}
		  }
		  
		  errorMessage = current + " not found.";
			  
		  return false;
			  
		}else if ( !root_dir_file.isDirectory()){
			
		  errorMessage = root_dir + " is not a directory.";
			  
		  return false;	
		}
		
		root_dir	+= File.separator;
		
		// System.out.println( "root dir = " + root_dir_file );
		
		List btFileList	= piece_mapper.getFileList();
		
		for (int i = 0; i < btFileList.size(); i++) {
				//get the BtFile
			DiskManagerPieceMapper.fileInfo tempFile = (DiskManagerPieceMapper.fileInfo)btFileList.get(i);
				//get the path
			String tempPath = root_dir + tempFile.getPath();
				//get file name
			String tempName = tempFile.getName();
				//System.out.println( "\ttempPath="+tempPath+",tempName="+tempName );
				//get file length
			long length = tempFile.getLength();

			File f = new File(tempPath, tempName);

			if (!f.exists()){
				
			  errorMessage = f.toString() + " not found.";
			  
			  return false;
			  
			}else{
			
					// use the cache file to ascertain length in case the caching/writing algorithm
					// fiddles with the real length
					// Unfortunately we may be called here BEFORE the disk manager has been 
					// started and hence BEFORE the file info has been setup...
					// Maybe one day we could allocate the file info earlier. However, if we do
					// this then we'll need to handle the "already moved" stuff too...
				
				DiskManagerFileInfoImpl	file_info = tempFile.getFileInfo();
				
				boolean	close_it	= false;
				
				try{
					if ( file_info == null ){
						
						file_info = new DiskManagerFileInfoImpl( this, f, tempFile.getTorrentFile());
		
						close_it	= true;					
					}
					
					try{
							// only test for too big as if incremental creation selected
							// then too small is OK
						
						long	existing_length = file_info.getCacheFile().getLength();
						
						if ( existing_length > length ){
							
							if ( COConfigurationManager.getBooleanParameter("File.truncate.if.too.large")){
								
								file_info.setAccessMode( DiskManagerFileInfo.WRITE );

								file_info.getCacheFile().setLength( length );
								
								Debug.out( "Existing data file length too large [" +existing_length+ ">" +length+ "]: " + f.getAbsolutePath() + ", truncating" );

							}else{

								errorMessage = "Existing data file length too large [" +existing_length+ ">" +length+ "]: " + f.getAbsolutePath();
						  
								return false;
							}
						}
					}finally{
						
						if ( close_it ){
							
							file_info.getCacheFile().close();
						}
					}
				}catch( CacheFileManagerException e ){
				
					errorMessage = Debug.getNestedExceptionMessage(e) + " (filesExist:" + f.toString() + ")";
					
					return( false );
				}
			}
		}
		
		return true;
	}
	
	private int allocateFiles() {
		setState( ALLOCATING );
		allocated = 0;
		int numNewFiles = 0;

		String	root_dir = download_manager.getTorrentSaveDir();
		
		if ( !torrent.isSimpleTorrent()){
			
			root_dir += File.separator + download_manager.getTorrentSaveFile();
		}
		
		root_dir	+= File.separator;	
		
		List btFileList	= piece_mapper.getFileList();
	
		for (int i = 0; i < btFileList.size(); i++) {
			//get the BtFile
			final DiskManagerPieceMapper.fileInfo tempFile = (DiskManagerPieceMapper.fileInfo)btFileList.get(i);
			//get the path
			final String tempPath = root_dir + tempFile.getPath();
			//get file name
			final String tempName = tempFile.getName();
			//get file length
			final long length = tempFile.getLength();

			final File f = new File(tempPath, tempName);

			DiskManagerFileInfoImpl fileInfo;
			
				// ascertain whether or not the file exists here in case the creation of the cache file
				// by DiskManagerFileInfoImpl pre-creates the file if it isn't present
			
			boolean	file_exists	= f.exists();
			
			try{
				fileInfo = new DiskManagerFileInfoImpl( this, f, tempFile.getTorrentFile());
				
			}catch ( CacheFileManagerException e ){
				
				this.errorMessage = Debug.getNestedExceptionMessage(e) + " (allocateFiles:" + f.toString() + ")";
				
				setState( FAULTY );
				
        try{
          tempFile.getFileInfo().getCacheFile().close();
        }
        catch( Throwable t ) {  /*ignore*/ }
        
				return -1;
			}
						
			int separator = tempName.lastIndexOf(".");
			
			if (separator == -1){
				separator = 0;
			}
			
			fileInfo.setExtension(tempName.substring(separator));
			
			//Added for Feature Request
			//[ 807483 ] Prioritize .nfo files in new torrents
			//Implemented a more general way of dealing with it.
			String extensions = COConfigurationManager.getStringParameter("priorityExtensions","");
			if(!extensions.equals("")) {
				boolean bIgnoreCase = COConfigurationManager.getBooleanParameter("priorityExtensionsIgnoreCase");
				StringTokenizer st = new StringTokenizer(extensions,";");
				while(st.hasMoreTokens()) {
					String extension = st.nextToken();
					extension = extension.trim();
					if(!extension.startsWith("."))
						extension = "." + extension;
					boolean bHighPriority = (bIgnoreCase) ? 
														  fileInfo.getExtension().equalsIgnoreCase(extension) : 
														  fileInfo.getExtension().equals(extension);
					if (bHighPriority)
						fileInfo.setPriority(true);
				}
			}
			
			fileInfo.setLength(length);
			fileInfo.setDownloaded(0);
			
			if( file_exists ){
				
			  try {

			  		//make sure the existing file length isn't too large
			  	
			  	long	existing_length = fileInfo.getCacheFile().getLength();
			  	
				if(  existing_length > length ){
				
					if ( COConfigurationManager.getBooleanParameter("File.truncate.if.too.large")){
					
					  	fileInfo.setAccessMode( DiskManagerFileInfo.WRITE );

						fileInfo.getCacheFile().setLength( length );
					
						Debug.out( "Existing data file length too large [" +existing_length+ ">" +length+ "]: " + f.getAbsolutePath() + ", truncating" );

					}else{
					
						this.errorMessage = "Existing data file length too large [" +existing_length+ ">" +length+ "]: " + f.getAbsolutePath();
		          
						setState( FAULTY );
		          
            try {
              fileInfo.getCacheFile().close();
            }
            catch( Throwable t ) {  /*ignore*/ }
            
            
						return -1;
					}
				}
			  	
			  	fileInfo.setAccessMode( DiskManagerFileInfo.READ );
			  	
			  }catch (CacheFileManagerException e) {
			  	
			  	this.errorMessage = Debug.getNestedExceptionMessage(e) + 
											" (allocateFiles existing:" + f.toString() + ")";
			  	setState( FAULTY );
			  	
          try {
            fileInfo.getCacheFile().close();
          }
          catch( Throwable t ) {  /*ignore*/ }
          
			  	return -1;
        }
			  
        allocated += length;
        
      }else {  //we need to allocate it
        
        //make sure it hasn't previously been allocated
        if( download_manager.isDataAlreadyAllocated() ){
        	
          this.errorMessage = "Data file missing: " + f.getAbsolutePath();
          
          setState( FAULTY );
          
          try {
            fileInfo.getCacheFile().close();
          }
          catch( Throwable t ) {  /*ignore*/ }
          
          return -1;
        }
        
        try {
          File directory = new File( tempPath );
          
          if( !directory.exists() ) {
          	
            if( !directory.mkdirs() ) throw new Exception( "directory creation failed: " +directory);
          }
          
          f.getCanonicalPath();  //TEST: throws Exception if filename is not supported by os
          
          fileInfo.setAccessMode( DiskManagerFileInfo.WRITE );
          
          if( COConfigurationManager.getBooleanParameter("Enable incremental file creation") ) {
          	
          	//	do incremental stuff
          	
            fileInfo.getCacheFile().setLength( 0 );
            
          }else {  //fully allocate
            if( COConfigurationManager.getBooleanParameter("Zero New") ) {  //zero fill
              if( !writer_and_checker.zeroFile( fileInfo, length ) ) {
                setState( FAULTY );
                
                try {
                  fileInfo.getCacheFile().close();
                }
                catch( Throwable t ) {  /*ignore*/ }
                
                return -1;
              }
            }else {  //reserve the full file size with the OS file system
            	
              fileInfo.getCacheFile().setLength( length );
              
              allocated += length;
            }
          }
        }catch ( Exception e ) {          
          this.errorMessage = Debug.getNestedExceptionMessage(e)
              + " (allocateFiles new:" + f.toString() + ")";
          
          setState( FAULTY );
          
          try {
            fileInfo.getCacheFile().close();
          }
          catch( Throwable t ) {  /*ignore*/ }
          
          return -1;
        }
        
        numNewFiles++;
      }

			//add the file to the array
			files[i] = fileInfo;

			//setup this files RAF reference
			tempFile.setFileInfo(files[i]);
		}
    
    loadFilePriorities();
    
    download_manager.setDataAlreadyAllocated( true );
    
		return numNewFiles;
	}	
	
  
	public void 
	enqueueReadRequest( 
		DiskManagerReadRequest request, 
		DiskManagerReadRequestListener listener ) 
	{
		reader.enqueueReadRequest( request, listener );
	}


	public int getNumberOfPieces() {
		return nbPieces;
	}

	public int getPercentDone() {
		return percentDone;
	}
	
	public void
	setPercentDone(
		int			num )
	{
		percentDone	= num;
	}
	
	public long getRemaining() {
		return remaining;
	}
	
	public long getRemainingExcludingDND() 
	{
		if ( skipped_file_set_changed ){
			
			DiskManagerFileInfoImpl[]	current_files = files;
			
			if ( current_files != null ){
				
				skipped_file_set_changed	= false;
				
				try{
					this_mon.enter();
					
					skipped_file_set_size	= 0;
					skipped_but_downloaded	= 0;
					
					for (int i=0;i<current_files.length;i++){
						
						DiskManagerFileInfoImpl	file = current_files[i];
						
						if ( file.isSkipped()){
							
							skipped_file_set_size	+= file.getLength();
							skipped_but_downloaded	+= file.getDownloaded();
						}
					}
				}finally{
					
					this_mon.exit();
				}
			}
		}
		
		long rem = ( remaining - ( skipped_file_set_size - skipped_but_downloaded ));
		
		if ( rem < 0 ){
			
			rem	= 0;
		}
		
		return( rem );
	}
	
	public void
	incrementRemaining(
		long	num )
	{
		try{
			this_mon.enter();
		
			remaining	+= num;
			
		}finally{
			
			this_mon.exit();
		}
	}

	public void
	decrementRemaining(
		long	num )
	{
		try{
			this_mon.enter();
		
			remaining	-= num;
			
		}finally{
			
			this_mon.exit();
		}	
	}
	
	public long
	getAllocated()
	{
		return( allocated );
	}
	
	public void
	setAllocated(
		long		num )
	{
		allocated	= num;
	}
	
		// called when status has CHANGED and should only be called by DiskManagerPieceImpl
	
	protected void
	setPieceDone(
		DiskManagerPieceImpl	piece )
	{
		int	piece_length = piece.getPieceNumber()==nbPieces-1?lastPieceLength:pieceLength;
		
		if ( piece.getDone()){
				
			decrementRemaining( piece_length );
			
			computeFilesDone( piece.getPieceNumber());
			
		}else{
				
			incrementRemaining( piece_length );
		}
		
		listeners.dispatch(LDT_PIECE_DONE_CHANGED, null);
	}
	
	public DiskManagerPiece[]
	getPieces()
	{
		return( pieces );
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

	public void
	setState(
		int		_state ) 
	{
		if ( state_set_via_method != _state ){
		  int params[] = {state_set_via_method, _state};
			state_set_via_method = _state;
			
			listeners.dispatch( LDT_STATECHANGED, params);
		}
	}

	public void computeFilesDone(int pieceNumber) {
		for (int i = 0; i < files.length; i++){
			
			DiskManagerFileInfoImpl	this_file = files[i];
			
			PieceList pieceList = pieceMap[pieceNumber];
			//for each piece

			for (int k = 0; k < pieceList.size(); k++) {
				//get the piece and the file 
				PieceMapEntry tempPiece = pieceList.get(k);
				
				if ( this_file == tempPiece.getFile()){
					
					long done = this_file.getDownloaded();
					
					done += tempPiece.getLength();
					
					this_file.setDownloaded(done);
					
					if (	done == this_file.getLength() &&
							this_file.getAccessMode() == DiskManagerFileInfo.WRITE){
						
					
						try{
							this_file.setAccessMode( DiskManagerFileInfo.READ );
							
						}catch (Exception e) {
							
							Debug.printStackTrace( e );
						}
					}
				}
			}
		}
	}
	
	public DiskManagerFileInfo[] getFiles() {
		return files;
	}


	private void 
	constructFilesPieces() 
	{
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void
	setFailed(
		final String		reason )
	{
			/**
			 * need to run this on a separate thread to avoid deadlock with the stopping
			 * process - setFailed tends to be called from within the read/write activities
			 * and stopping these requires this.
			 */
    	new AEThread("DiskManager:setFailed") 
		{
			public void 
			runSupport() 
			{
				errorMessage	= reason;
				
				LGLogger.logUnrepeatableAlert( LGLogger.AT_ERROR, errorMessage );
				

				setState( DiskManager.FAULTY );
									
				DiskManagerImpl.this.stop();
			}
		}.start();

	}

	
	public PieceList
	getPieceList(
		int		piece_number )
	{
		return( pieceMap[piece_number] );
	}
	
	public byte[]
	getPieceHash(
		int	piece_number )
	
		throws TOTorrentException
	{
		return( torrent.getPieces()[ piece_number ]);
	}
	
	public DiskManagerReadRequest
	createReadRequest(
		int pieceNumber,
		int offset,
		int length )
	{
		return( reader.createRequest( pieceNumber, offset, length ));
	}
	
  
	public void 
	enqueueCompleteRecheckRequest(
		final DiskManagerCheckRequestListener 	listener,
		final Object							user_data ) 
	{
	  	writer_and_checker.enqueueCompleteRecheckRequest( listener, user_data );
	}

	public void 
	enqueueCheckRequest(
		int 							pieceNumber,
		DiskManagerCheckRequestListener listener,
		Object							user_data ) 
	{
	  	writer_and_checker.enqueueCheckRequest( pieceNumber, listener, user_data );
	}
	  
	public boolean isChecking() 
	{
	  return ( writer_and_checker.isChecking());
	}
  
	public DirectByteBuffer 
	readBlock(
		int pieceNumber, 
		int offset, 
		int length )
	{
		return( reader.readBlock( pieceNumber, offset, length ));
	}
	
	public void 
	enqueueWriteRequest(
		int 							pieceNumber, 
		int 							offset, 
		DirectByteBuffer 				data,
		Object 							user_data,
		DiskManagerWriteRequestListener	listener )
	{
		writer_and_checker.writeBlock( pieceNumber, offset, data, user_data, listener );
	}
	
	public boolean 
	checkBlockConsistency(
		int pieceNumber, 
		int offset, 
		DirectByteBuffer data) 
	{
		return( writer_and_checker.checkBlock( pieceNumber, offset, data ));
	}
	
	public boolean 
	checkBlockConsistency(
		int pieceNumber, 
		int offset, 
		int length) 
	{
		return( writer_and_checker.checkBlock( pieceNumber, offset, length ));
	}
	
	public void 
	dumpResumeDataToDisk(
		boolean savePartialPieces, 
		boolean force_recheck )
	
		throws Exception
	{			
		resume_handler.dumpResumeDataToDisk( savePartialPieces, force_recheck );
	}
		
  /**
   * Moves files to the CompletedFiles directory.
   * Returns a string path to the new torrent file.
   */
	
  public void 
  downloadEnded() 
  {
    try{
    	this_mon.enter();
    	
	    String fullPath;
	    
	    String subPath;
	    
	    String rPath = download_manager.getTorrentSaveDir();
	    
	    File destDir;
	    
	    //String returnName = "";
	    
	    	// don't move non-persistent files as these aren't managed by us
	    
	    if (!download_manager.isPersistent()){
	    	
	    	return;
	    }
	    
	    //make sure the torrent hasn't already been moved
	
	    	
	  	if (alreadyMoved){
	  		
	  		return;
	  	}
	    	
	  	alreadyMoved = true;	
	 
	    boolean moveWhenDone = COConfigurationManager.getBooleanParameter("Move Completed When Done", false);
	    
	    if (!moveWhenDone){
	    	
	    	return;
	    }
	    
	    String moveToDir = COConfigurationManager.getStringParameter("Completed Files Directory", "");
	    
	    if (moveToDir.length() == 0){
	    	
	    	return;
	    }
	
	    try{
	
	      boolean moveOnlyInDefault = COConfigurationManager.getBooleanParameter("Move Only When In Default Save Dir");
	      
	      if (moveOnlyInDefault) {
	      	
	        String defSaveDir = COConfigurationManager.getStringParameter("Default save path");

	        if (!rPath.equals(defSaveDir)){
	        	
	          LGLogger.log(LGLogger.INFORMATION, "Not moving-on-complete since data is not within default save dir");
	          
	          return;
	        }
	      }
	      
	      	// first of all check that no destination files already exist
	      
	      File[]	new_files 	= new File[files.length];
	      File[]	old_files	= new File[files.length];
	      
	      for (int i=0; i < files.length; i++) {
	          
	          File old_file = files[i].getFile();
	          
	          old_files[i]	= old_file;
	          
	          //get old file's parent path
	          fullPath = old_file.getParent();
	          
	           //compute the file's sub-path off from the default save path
	          subPath = fullPath.substring(fullPath.indexOf(rPath) + rPath.length());
	    
	          //create the destination dir
	          
	          if ( subPath.startsWith( File.separator )){
	          	
	          	subPath = subPath.substring(1);
	          }
	          
	          destDir = new File(moveToDir, subPath);
	     
	          destDir.mkdirs();
	          
	          //create the destination file pointer
	          File newFile = new File(destDir, old_file.getName());
	
	          new_files[i]	= newFile;
	          
	          if (newFile.exists()) {
	          	
	            String msg = "" + old_file.getName() + " already exists in MoveTo destination dir";
	            
	            LGLogger.log(LGLogger.ERROR,msg);
	            
	            LGLogger.logUnrepeatableAlertUsingResource( 
	            		LGLogger.AT_ERROR, "DiskManager.alert.movefileexists", 
	            		new String[]{ old_file.getName() } );
	            
	            Debug.out(msg);
	            
	            return;
	          }
	      }
	      
	      for (int i=0; i < files.length; i++){
	      		 
	          File old_file = files[i].getFile();
	
	          File new_file = new_files[i];
	          
	          try{
	          	
	          	files[i].moveFile( new_file );
	           	
	            files[i].setAccessMode(DiskManagerFileInfo.READ);
	            
	          }catch( CacheFileManagerException e ){
	          	
	            String msg = "Failed to move " + old_file.getName() + " to destination dir";
	            
	            LGLogger.log(LGLogger.ERROR,msg);
	            
	            LGLogger.logUnrepeatableAlertUsingResource( 
	            		LGLogger.AT_ERROR, "DiskManager.alert.movefilefails", 
	            		new String[]{ old_file.toString(),
	            		Debug.getNestedExceptionMessage(e)});
	
	            Debug.out(msg);
	            
	            	// try some recovery by moving any moved files back...
	            
	            for (int j=0;j<i;j++){
	            
	            	try{
	            		files[j].moveFile( old_files[j]);
	
	            		files[j].setAccessMode(DiskManagerFileInfo.READ);
	         		
	            	}catch( CacheFileManagerException f ){
	              
	            		LGLogger.logUnrepeatableAlertUsingResource( 
	                    		LGLogger.AT_ERROR, "DiskManager.alert.movefilerecoveryfails", 
	                    		new String[]{ files[j].toString(),
	                    		Debug.getNestedExceptionMessage(f)} );
	           		
	            	}
	            }
	            
	            return;
	          }
	      }
	      
	      	//remove the old dir
	      
	      File tFile = new File(download_manager.getTorrentSaveDir(), download_manager.getTorrentSaveFile());
	      
	      if (	tFile.isDirectory() && 
	      		!moveToDir.equals(rPath)){
	      	
	      		deleteDataFiles(torrent, download_manager.getTorrentSaveDir(), download_manager.getTorrentSaveFile());
	      }
	        
	      download_manager.setTorrentSaveDir( moveToDir );
	      
	      	//move the torrent file as well
	      
	      boolean moveTorrent = COConfigurationManager.getBooleanParameter("Move Torrent When Done", true);
	      
	      if ( moveTorrent ){
	      	
	          String oldFullName = download_manager.getTorrentFileName();
	          
	          File oldTorrentFile = new File(oldFullName);
	          
	          String oldFileName = oldTorrentFile.getName();
	          
	          File newTorrentFile = new File(moveToDir, oldFileName);
	          
	          if (!newTorrentFile.equals(oldTorrentFile)){
	          	
	          	if ( TorrentUtils.move( oldTorrentFile, newTorrentFile )){
	            	            
	          		download_manager.setTorrentFileName(newTorrentFile.getCanonicalPath());
	          		
	          	}else{
	          
		            String msg = "Failed to move " + oldTorrentFile.toString() + " to " + newTorrentFile.toString();
		            
		            LGLogger.log(LGLogger.ERROR,msg);
		            
		            LGLogger.logUnrepeatableAlertUsingResource( 
		            		LGLogger.AT_ERROR, "DiskManager.alert.movefilefails", 
		            		new String[]{ 	oldTorrentFile.toString(),
		            						newTorrentFile.toString()});
		
		            Debug.out(msg);
	          	}
	          }
	      }
	    }catch( Exception e){
	    	
	    	Debug.printStackTrace( e ); 
	    }	    
    }finally{
    	
    	try{
            boolean resumeEnabled = COConfigurationManager.getBooleanParameter("Use Resume", true);
            
            	//update resume data
            
            if (resumeEnabled){
            	
            	try{
            		dumpResumeDataToDisk(true, false);
            		
            	}catch( Exception e ){
            		
            			// won't go wrong here due to cache write fails as these must have completed
            			// prior to the files being moved. Possible problems with torrent save but
            			// if this fails we can live with it (just means that on restart we'll do
            			// a recheck )
            		
            		Debug.out( "dumpResumeDataToDisk fails" );
            	}
            }
    	}catch( Throwable e ){
    		
    		Debug.printStackTrace(e);
    	}
    	
    	this_mon.exit();
    }
  }
   
    
 
  	public String
	getName()
  	{
  		return( dm_name );
  	}
  
	public TOTorrent
	getTorrent()
	{
		return( torrent );
	}


	public void
	addListener(
			DiskManagerListener	l )
	{
		listeners.addListener( l );

		int params[] = {getState(), getState()};
  		
		listeners.dispatch( l, LDT_STATECHANGED, params);
	}
  
	public void
	removeListener(
		DiskManagerListener	l )
	{
		listeners.removeListener(l);
	}

		  /** Deletes all data files associated with torrent.
		   * Currently, deletes all files, then tries to delete the path recursively
		   * if the paths are empty.  An unexpected result may be that a empty
		   * directory that the user created will be removed.
		   *
		   * TODO: only remove empty directories that are created for the torrent
		   */
  
	public static void 
	deleteDataFiles(
		TOTorrent 	torrent, 
		String		torrent_save_dir,		// enclosing dir, not for deletion 
		String		torrent_save_file ) 	// file or dir for torrent
	{	
		if (torrent == null || torrent_save_file == null ){
	  
			return;
		}
	  	  
		try{
			if (torrent.isSimpleTorrent()){

				FileUtil.deleteWithRecycle(new File( torrent_save_dir, torrent_save_file ));

			}else{

                PlatformManager mgr = PlatformManagerFactory.getPlatformManager();
                if( Constants.isOSX &&
                      torrent_save_file.length() > 0 &&
                      COConfigurationManager.getBooleanParameter("Move Deleted Data To Recycle Bin" ) &&
                      mgr.hasCapability(PlatformManagerCapabilities.RecoverableFileDelete) ) {

                    try
                    {
                        mgr.performRecoverableFileDelete(torrent_save_dir + File.separatorChar + torrent_save_file + File.separatorChar);
                    }
                    catch(PlatformManagerException ex)
                    {
                        deleteDataFileContents( torrent, torrent_save_dir, torrent_save_file );
                    }
                }
                else{
                    deleteDataFileContents(torrent, torrent_save_dir, torrent_save_file);
                }

            }
		}catch( Throwable e ){
		
			Debug.printStackTrace( e );
		}
	}

    private static void deleteDataFileContents(TOTorrent torrent, String torrent_save_dir, String torrent_save_file)
            throws TOTorrentException, UnsupportedEncodingException, LocaleUtilEncodingException
    {
        LocaleUtilDecoder locale_decoder = LocaleUtil.getSingleton().getTorrentEncoding( torrent );

        TOTorrentFile[] files = torrent.getFiles();

        // delete all files, then empty directories

        for (int i=0;i<files.length;i++){

            byte[][]path_comps = files[i].getPathComponents();

            String	path_str = torrent_save_dir + File.separator + torrent_save_file + File.separator;

            for (int j=0;j<path_comps.length;j++){

                try{

                    String comp = locale_decoder.decodeString( path_comps[j] );

                    comp = FileUtil.convertOSSpecificChars( comp );

                    path_str += (j==0?"":File.separator) + comp;

                }catch( UnsupportedEncodingException e ){

                    System.out.println( "file - unsupported encoding!!!!");
                }
            }

            File file = new File(path_str);

            if (file.exists() && !file.isDirectory()){

                try{
                    FileUtil.deleteWithRecycle( file );

                }catch (Exception e){

                    Debug.out(e.toString());
                }
            }
        }

        FileUtil.recursiveEmptyDirDelete(new File( torrent_save_dir, torrent_save_file ));
    }

    protected void
    skippedFileSetChanged()
    {
    	skipped_file_set_changed	= true;
	    listeners.dispatch(LDT_PRIOCHANGED, null);
    }

	protected void priorityChanged() {
	    listeners.dispatch(LDT_PRIOCHANGED, null);
    }
  
  private void 
  loadFilePriorities() 
  {
	  loadFilePriorities( download_manager, files );
  }
  
  private static void 
  loadFilePriorities(
	DownloadManager			download_manager,
	DiskManagerFileInfo[]	files )
  {
  	//  TODO: remove this try/catch.  should only be needed for those upgrading from previous snapshot
    try {
    	if ( files == null ) return;
    	List file_priorities = (List)download_manager.getData( "file_priorities" );
    	if ( file_priorities == null ) return;
    	for (int i=0; i < files.length; i++) {
    		DiskManagerFileInfo file = files[i];
    		if (file == null) return;
    		int priority = ((Long)file_priorities.get( i )).intValue();
    		if ( priority == 0 ) file.setSkipped( true );
    		else if (priority == 1) file.setPriority( true );
    	}
    }
    catch (Throwable t) {Debug.printStackTrace( t );}
  }
  
  public void 
  storeFilePriorities() 
  {
	  storeFilePriorities( download_manager, files );
  }
  
  private static void 
  storeFilePriorities(
	DownloadManager			download_manager,
	DiskManagerFileInfo[]	files )
  {
    if ( files == null ) return;
    List file_priorities = new ArrayList();
    for (int i=0; i < files.length; i++) {
      DiskManagerFileInfo file = files[i];
      if (file == null) return;
      boolean skipped = file.isSkipped();
      boolean priority = file.isPriority();
      int value = -1;
      if ( skipped ) value = 0;
      else if ( priority ) value = 1;
      file_priorities.add( i, new Long(value));            
    }
    download_manager.setData( "file_priorities", file_priorities );
  }
  
  public DownloadManager getDownloadManager() {
    return download_manager;
  }
    
  	public void 
	computePriorityIndicator()
  	{
  		piece_picker.computePriorityIndicator();
  	}
  	
	public int 
	getPieceNumberToDownload(
		boolean[] _piecesRarest)
	{
		return( piece_picker.getPiecenumberToDownload( _piecesRarest ));
	}
	
	public boolean hasDownloadablePiece() {
	    return piece_picker.hasDownloadablePiece();
	}
	
	public static DiskManagerFileInfo[]
	getFileInfoSkeleton(
		final DownloadManager		download_manager )
	{
		TOTorrent	torrent = download_manager.getTorrent();
		
		if ( torrent == null ){
			
			return( new DiskManagerFileInfo[0]);
		}
		
		String	root_dir = download_manager.getTorrentSaveDir();
		
		if ( !torrent.isSimpleTorrent()){
			
			root_dir += File.separator + download_manager.getTorrentSaveFile();
		}
		
		root_dir	+= File.separator;	

		try{
		    LocaleUtilDecoder locale_decoder = LocaleUtil.getSingleton().getTorrentEncoding( torrent );
			
			TOTorrentFile[]	torrent_files = torrent.getFiles();
			
			final DiskManagerFileInfo[]	res = new DiskManagerFileInfo[ torrent_files.length ];
			
			for (int i=0;i<res.length;i++){
			
				final TOTorrentFile	torrent_file	= torrent_files[i];
				
		        byte[][]path_comps = torrent_file.getPathComponents();
	
				String	path_str = root_dir + File.separator;
	
		        for (int j=0;j<path_comps.length;j++){
	
					String comp = locale_decoder.decodeString( path_comps[j] );
	
		            comp = FileUtil.convertOSSpecificChars( comp );
	
		            path_str += (j==0?"":File.separator) + comp;
		        }
				
				final File		data_file	= new File( path_str );
	
				final String	data_name 	= data_file.getName();
				final String	data_path	= data_file.getParent().toString() + File.separator;
							
				int separator = data_name.lastIndexOf(".");
				
				if (separator == -1){
					
					separator = 0;
				}
				
				final String	data_extension	= data_name.substring(separator);
				
				DiskManagerFileInfo	info = 
					new DiskManagerFileInfo()
					{		
						private boolean	priority;
						private boolean	skipped;
						
						public void 
						setPriority(boolean b)
						{
							priority	= b;
							
							storeFilePriorities( download_manager, res );
						}
				
						public void 
						setSkipped(boolean b)
						{
							skipped	= b;
							
							storeFilePriorities( download_manager, res );
						}
				 		 	
						public int 
						getAccessMode()
						{
							return( READ );
						}
						
						public long 
						getDownloaded()
						{
							return( -1 );
						}
						
						public String 
						getExtension()
						{
							return( data_extension );
						}
							
						public int 
						getFirstPieceNumber()
						{
							return( -1 );
						}
					  
						public int 
						getLastPieceNumber()
						{
							return( -1 );
						}
						
						public long 
						getLength()
						{
							return( torrent_file.getLength());
						}
						
						public String 
						getName()
						{
							return( data_name );
						}
						
						public int 
						getNbPieces()
						{
							return( -1 );
						}
							
						public String 
						getPath()
						{
							return( data_path );
						}
						
						public boolean 
						isPriority()
						{
							return( priority );
						}
						
						public boolean 
						isSkipped()
						{
							return( skipped );
						}
						
						public DiskManager 
						getDiskManager()
						{
							return( null );
						}
			
						public DownloadManager 
						getDownloadManager()
						{
							return( download_manager );
						}
	
						public File 
						getFile()
						{
							return( data_file );
						}
						
						public void
						flushCache()
						{
						}
					};
					
				res[i]	= info;
			}
			
			loadFilePriorities( download_manager, res );
			
			return( res );

		}catch( Throwable e ){
			
			Debug.printStackTrace(e);
			
			return( new DiskManagerFileInfo[0]);
	
		}
	}
}