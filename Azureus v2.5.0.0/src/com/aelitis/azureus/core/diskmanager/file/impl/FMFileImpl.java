/*
 * File    : FMFileImpl.java
 * Created : 12-Feb-2004
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
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.aelitis.azureus.core.diskmanager.file.impl;

/**
 * @author parg
 *
 */

import java.util.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.torrent.TOTorrentFile;
import org.gudy.azureus2.core3.util.*;

import com.aelitis.azureus.core.diskmanager.file.*;

public abstract class 
FMFileImpl
	implements FMFile
{	
	protected static final String		READ_ACCESS_MODE	= "r";
	protected static final String		WRITE_ACCESS_MODE	= "rwd";
	
	private static Map			file_map = new HashMap();
	private static AEMonitor	file_map_mon	= new AEMonitor( "FMFile:map");
	
	static{
		AEDiagnostics.addEvidenceGenerator(
			new AEDiagnosticsEvidenceGenerator()
			{
				public void
				generate(
					IndentWriter		writer )
				{
					generateEvidence( writer );
				}
			});
	}
	
	private FMFileManagerImpl	manager;
	private FMFileOwner			owner;
	private int					access_mode			= FM_READ;
	
	private File				linked_file;
	private String				canonical_path;
	private RandomAccessFile	raf;
	
	private FMFileAccessController		file_access;
	
	private File				created_dirs_leaf;
	private List				created_dirs;
	
	protected AEMonitor			this_mon	= new AEMonitor( "FMFile" );
	
	
	protected
	FMFileImpl(
		FMFileOwner			_owner,
		FMFileManagerImpl	_manager,
		File				_file,
		int					_type )
	
		throws FMFileManagerException
	{
		owner			= _owner;
		manager			= _manager;
		
		linked_file		= manager.getFileLink( owner.getTorrentFile().getTorrent(), _file );
		
		boolean	file_was_created	= false;
		boolean	file_reserved		= false;
		boolean	ok 					= false;
		
		try{
      
			try {
				canonical_path = linked_file.getCanonicalPath();
				
			}catch( IOException ioe ) {
				
				String msg = ioe.getMessage();
				
		        if( msg != null && msg.indexOf( "There are no more files" ) != -1 ) {
					
		          String abs_path = linked_file.getAbsolutePath();
				  
		          String error = "Caught 'There are no more files' exception during file.getCanonicalPath(). " +
		                         "os=[" +Constants.OSName+ "], file.getPath()=[" +linked_file.getPath()+ "], file.getAbsolutePath()=[" +abs_path+ "]. ";
				  
		          Debug.out( error, ioe );
		        }
				
		        throw ioe;
			}
			
			createDirs( linked_file );
	        
			reserveFile();
				
			file_reserved	= true;
			
			file_access = new FMFileAccessController( this, _type );
			
			ok	= true;
			
		}catch( Throwable e ){
			
			if ( file_was_created ){
				
				linked_file.delete();
			}
			
			deleteDirs();
			
			if ( e instanceof FMFileManagerException ){
				
				throw((FMFileManagerException)e);
			}
			
			throw( new FMFileManagerException( "initialisation failed", e ));
			
		}finally{
			
			if ( file_reserved && !ok ){
				
				releaseFile();
			}
		}
	}

	protected FMFileManagerImpl
	getManager()
	{
		return( manager );
	}
	
	public String
	getName()
	{
		return( linked_file.toString());
	}
	
	public boolean
	exists()
	{
		return( linked_file.exists());
	}
	
	public FMFileOwner
	getOwner()
	{
		return( owner );
	}
		
	public void
	setStorageType(
		int		new_type )
	
		throws FMFileManagerException
	{
		try{
			this_mon.enter();

			boolean	was_open = isOpen();
			
			if ( was_open ){
				
				closeSupport( false );
			}
			
			try{
				file_access.setStorageType(  new_type );
				
			}finally{
				
				if ( was_open ){
					
					openSupport( "Re-open after storage type change" );
				}
			}
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public int
	getStorageType()
	{
		return( file_access.getStorageType());
	}
	
	public int
	getAccessMode()
	{
		return( access_mode );
	}
	
	protected void
	setAccessModeSupport(
		int		mode )
	{
		access_mode	= mode;
	}
	
	protected File
	getLinkedFile()
	{
		return( linked_file );
	}
	
	public void
	moveFile(
		File		new_unlinked_file )
	
		throws FMFileManagerException
	{
		try{
			this_mon.enter();
		
			String	new_canonical_path;
			File	new_linked_file	= manager.getFileLink( owner.getTorrentFile().getTorrent(), new_unlinked_file );
			
			try{
        
		        try {
					
		          new_canonical_path = new_linked_file.getCanonicalPath();
				  
	
		        }catch( IOException ioe ) {
					
		          String msg = ioe.getMessage();
				  
		          if( msg != null && msg.indexOf( "There are no more files" ) != -1 ) {
		            String abs_path = new_linked_file.getAbsolutePath();
		            String error = "Caught 'There are no more files' exception during new_file.getCanonicalPath(). " +
		                           "os=[" +Constants.OSName+ "], new_file.getPath()=[" +new_linked_file.getPath()+ "], new_file.getAbsolutePath()=[" +abs_path+ "]. ";
		                           //"new_canonical_path temporarily set to [" +abs_path+ "]";
		            Debug.out( error, ioe );
		          }
		          throw ioe;
		        }
						
			}catch( Throwable e ){
				
				throw( new FMFileManagerException( "getCanonicalPath fails", e ));
			}	
			
			if ( new_linked_file.exists()){
				
				throw( new FMFileManagerException( "moveFile fails - file '" + new_canonical_path + "' already exists"));	
			}
			
			boolean	was_open	= isOpen();
			
			close();	// full close, this will release any slots in the limited file case
			
			createDirs( new_linked_file );
	        
			if ( FileUtil.renameFile( linked_file, new_linked_file )) {
				
				linked_file		= new_linked_file;
				canonical_path	= new_canonical_path;
				
				reserveFile();
				
				if ( was_open ){
					
					ensureOpen( "moveFile target" );	// ensure open will regain slots in limited file case
				}
				
			}else{
			
				try{
					reserveFile();
					
				}catch( FMFileManagerException e ){
					
					Debug.printStackTrace( e );
				}
				
				if ( was_open ){
					
					try{
						ensureOpen( "moveFile recovery" );
						
					}catch( FMFileManagerException e){
						
						Debug.printStackTrace( e );
					}
				}
				
				throw( new FMFileManagerException( "moveFile fails"));
			}	
		}finally{
			
			this_mon.exit();
		}
	}
	
	public void
	ensureOpen(
		String	reason )
	
		throws FMFileManagerException
	{
		try{
			this_mon.enter();
	
			if ( isOpen()){
				
				return;
			}
					  		
			openSupport( reason );

		}finally{
			
			this_mon.exit();
		}
	}

	protected long
	getLengthSupport()
	
		throws FMFileManagerException
	{
		return( file_access.getLength( raf ));
	}
	
	protected void
	setLengthSupport(
		long		length )
	
		throws FMFileManagerException
	{
		file_access.setLength( raf, length );
	}
	
	protected void
	openSupport(
		String	reason )
	
		throws FMFileManagerException
	{
		if ( raf != null ){
			
			throw( new FMFileManagerException( "file already open" ));
		}

		reserveAccess( reason );
		
		try{		
			raf = new RandomAccessFile( linked_file, access_mode==FM_READ?READ_ACCESS_MODE:WRITE_ACCESS_MODE);
			
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
			
			throw( new FMFileManagerException( "open fails", e ));
		}
	}
	
	protected void
	closeSupport(
		boolean		explicit )
	
		throws FMFileManagerException
	{
		FMFileManagerException	flush_exception = null;
		
		try{
			flush();
			
		}catch( FMFileManagerException e ){
		
			flush_exception = e;
		}
		
		if ( raf == null ){
			
				// may have previously been implicitly closed, tidy up if required
			
			if ( explicit ){
				
				releaseFile();
				
				deleteDirs();
			}
		}else{
		
			try{			
				raf.close();
								
			}catch( Throwable e ){
				
				throw( new FMFileManagerException("close fails", e ));
				
			}finally{
				  	  
				raf	= null;
				
				if ( explicit ){
					
					releaseFile();
				}
			}
		}
		
		if ( flush_exception != null ){
			
			throw( flush_exception );
		}
	}
	
	public void
	flush()
	
		throws FMFileManagerException
	{
		file_access.flush();
	}
	
	public void
	delete()
	
		throws FMFileManagerException
	{
		close();
		
		if ( linked_file.exists()){
			
			if ( !linked_file.delete()){
				
				throw( new FMFileManagerException( "Failed to delete '" + linked_file + "'" ));
			}
		}
	}
	
	protected void
	readSupport(
		DirectByteBuffer	buffer,
		long				offset )
	
		throws FMFileManagerException
	{
		file_access.read( raf, buffer, offset );
	}
	
	protected void
	writeSupport(
		DirectByteBuffer		buffer,
		long					position )
	
		throws FMFileManagerException
	{
		writeSupport(new DirectByteBuffer[]{buffer}, position );
	}
	
	protected void
	writeSupport(
		DirectByteBuffer[]		buffers,
		long					position )
	
		throws FMFileManagerException
	{
		file_access.write( raf, buffers, position );
	}
	
	protected boolean
	isOpen()
	{
		return( raf != null );
	}
	
		// file reservation is used to manage the possibility of multiple torrents
		// refering to the same file. Initially introduced to stop a common problem
		// whereby different torrents contain the same files - without 
		// this code the torrents could interfere resulting in all sorts of problems
		// The original behavior was to completely prevent the sharing of files.
		// However, better behaviour is to allow sharing of a file as long as only
		// read access is required.
		// we store a list of owners against each canonical file with a boolean "write" marker
	
	private void
	reserveFile()
	
		throws FMFileManagerException
	{
		try{
			file_map_mon.enter();
			
			// System.out.println( "FMFile::reserveFile:" + canonical_path + "("+ owner.getName() + ")" );
			
			List	owners = (List)file_map.get(canonical_path);
			
			if ( owners == null ){
				
				owners = new ArrayList();
				
				file_map.put( canonical_path, owners );				
			}
			
			for (Iterator it=owners.iterator();it.hasNext();){
				
				Object[]	entry = (Object[])it.next();
			
				if ( owner.getName().equals(((FMFileOwner)entry[0]).getName())){
				
						// already present, start off read-access
					
					Debug.out( "reserve file - entry already present" );
					
					entry[1] = new Boolean( false );
					
					return;	
				}
			}
			
			owners.add( new Object[]{ owner, new Boolean( false ), "<reservation>" });
			
		}finally{
			
			file_map_mon.exit();
		}
	}
	
	private void
	reserveAccess(
		String	reason )
	
		throws FMFileManagerException
	{
		try{
			file_map_mon.enter();
			
			// System.out.println( "FMFile::reserveAccess:" + canonical_path + "("+ owner.getName() + ")" + " [" + (access_mode==FM_WRITE?"write":"read") + "]" );
			
			List	owners = (List)file_map.get( canonical_path );
			
			Object[]	my_entry = null;
					
			if ( owners == null ){
				
				Debug.out( "reserveAccess fail" );
				
				throw( new FMFileManagerException( "File '"+canonical_path+"' has not been reserved (no entries), '" + owner.getName()+"'"));
			}
			
			for (Iterator it=owners.iterator();it.hasNext();){
					
				Object[]	entry = (Object[])it.next();
				
				if ( owner.getName().equals(((FMFileOwner)entry[0]).getName())){
					
					my_entry	= entry;
				}
			}				
			
			if ( my_entry == null ){
				
				Debug.out( "reserveAccess fail" );
				
				throw( new FMFileManagerException( "File '"+canonical_path+"' has not been reserved (not found), '" + owner.getName()+"'"));
			}
		
			my_entry[1] = new Boolean( access_mode==FM_WRITE );
			my_entry[2] = reason;
			
			int	read_access 		= 0;
			int write_access		= 0;
			int	write_access_lax	= 0;
			
			TOTorrentFile	my_torrent_file = owner.getTorrentFile();
			
			String	users = "";
				
			for (Iterator it=owners.iterator();it.hasNext();){
				
				Object[]	entry = (Object[])it.next();
					
				FMFileOwner	this_owner = (FMFileOwner)entry[0];
				
				if (((Boolean)entry[1]).booleanValue()){
										
					write_access++;
					
					TOTorrentFile this_tf = this_owner.getTorrentFile();
					
					if ( my_torrent_file != null && this_tf != null && my_torrent_file.getLength() == this_tf.getLength()){
						
						write_access_lax++;
					}
					
					users += (users.length()==0?"":",") + this_owner.getName() + " [write]";

				}else{
					
					read_access++;
					
					users += (users.length()==0?"":",") + this_owner.getName() + " [read]";
				}
			}

			if ( 	write_access > 1 ||
					( write_access == 1 && read_access > 0 )){
				
					// relax locking if strict is disabled and torrent file is same size
				
				
				if ( !COConfigurationManager.getBooleanParameter( "File.strict.locking" )){
					
					if ( write_access_lax == write_access ){
						
						return;
					}
				}
				
				Debug.out( "reserveAccess fail" );
				
				throw( new FMFileManagerException( "File '"+canonical_path+"' is in use by '" + users +"'"));
			}
			
		}finally{
			
			file_map_mon.exit();
		}
	}
	
	private void
	releaseFile()
	{
		try{
			file_map_mon.enter();
		
			// System.out.println( "FMFile::releaseFile:" + canonical_path + "("+ owner.getName() + ")" );
					
			List	owners = (List)file_map.get( canonical_path );
			
			if ( owners != null ){
				
				for (Iterator it=owners.iterator();it.hasNext();){
					
					Object[]	entry = (Object[])it.next();
					
					if ( owner.getName().equals(((FMFileOwner)entry[0]).getName())){
						
						it.remove();
						
						break;
					}
				}
				
				if ( owners.size() == 0 ){
					
					file_map.remove( canonical_path );
				}
			}
		}finally{
			
			file_map_mon.exit();
		}
	}
	
	protected void
	createDirs(
		File		target )
	
		throws FMFileManagerException
	{
		deleteDirs();
		
		File	parent = target.getParentFile();
		
		if ( !parent.exists()){
			
			List	new_dirs = new ArrayList();
			
			File	current = parent;
			
			while( current != null && !current.exists()){
			
				new_dirs.add( current );
				
				current = current.getParentFile();
			}
			
			created_dirs_leaf	= target;
			created_dirs		= new ArrayList();
			
			if ( parent.mkdirs()){
			
				created_dirs_leaf	= target;
				created_dirs		= new_dirs;

				/*
				for (int i=created_dirs.size()-1;i>=0;i--){
					
					System.out.println( "created " + created_dirs.get(i));
				}
				*/
			}else{
        		throw( new FMFileManagerException( "Failed to create parent directory '" + parent + "'"));	
        	}
        }
	}
	
	protected void
	deleteDirs()
	{
		if ( created_dirs_leaf != null ){
			
				// delete any dirs we created if the target file doesn't exist
			
			if ( !created_dirs_leaf.exists()){
				
				Iterator	it = created_dirs.iterator();
					
				while( it.hasNext()){
					
					File	dir = (File)it.next();
					
					if ( dir.exists() && dir.isDirectory()){
						
						File[]	entries = dir.listFiles();
						
						if ( entries == null || entries.length == 0 ){
							
							// System.out.println( "deleted " + dir );
							
							dir.delete();
							
						}else{
							
							break;
						}
					}else{
						
						break;
					}
				}
			}
	
			created_dirs_leaf	= null;
			created_dirs 		= null;
		}
	}
	
	protected String
	getString()
	{
		File cPath = new File(canonical_path);
		String sPaths;
		if (cPath.equals(linked_file))
			sPaths = "can/link=" + Debug.secretFileName(canonical_path);
		else
			sPaths = "can=" + Debug.secretFileName(canonical_path) + ",link="
					+ Debug.secretFileName(linked_file.toString());
		return sPaths + ",raf=" + raf + ",acc=" + access_mode + ",ctrl = "
				+ file_access.getString();
	}
	
	protected static void
	generateEvidence(
		IndentWriter	writer )
	{
		writer.println( "FMFile Reservations" );
		
		try{
			writer.indent();

			try{
				file_map_mon.enter();
			
				Iterator	it = file_map.keySet().iterator();
				
				while( it.hasNext()){
					
					String	key = (String)it.next();
					
					List	owners = (List)file_map.get(key);
					
					Iterator	it2 = owners.iterator();
					
					String	str = "";
						
					while( it2.hasNext()){
						
						Object[]	entry = (Object[])it2.next();

						FMFileOwner	owner 	= (FMFileOwner)entry[0];
						Boolean		write	= (Boolean)entry[1];
						String		reason	= (String)entry[2];
						
						
						str += (str.length()==0?"":", ") + owner.getName() + "[" + (write.booleanValue()?"write":"read")+ "/" + reason + "]";
					}
					

					writer.println( Debug.secretFileName(key) + " -> " + str );
				}
			}finally{
				
				file_map_mon.exit();
			}
			
			FMFileManagerImpl.generateEvidence( writer );
			
		}finally{
			
			writer.exdent();
		}
	}
}
