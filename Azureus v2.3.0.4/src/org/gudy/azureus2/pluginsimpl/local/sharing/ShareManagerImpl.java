/*
 * File    : ShareManagerImpl.java
 * Created : 30-Dec-2003
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

package org.gudy.azureus2.pluginsimpl.local.sharing;

/**
 * @author parg
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;

import org.gudy.azureus2.plugins.torrent.*;
import org.gudy.azureus2.pluginsimpl.local.torrent.*;
import org.gudy.azureus2.plugins.sharing.*;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.logging.*;
import org.gudy.azureus2.core3.torrent.*;
import org.gudy.azureus2.core3.tracker.host.*;

public class 
ShareManagerImpl
	implements ShareManager, TOTorrentProgressListener, ParameterListener
{
	public static final String		TORRENT_STORE 		= "shares";
	public static final String		TORRENT_SUBSTORE	= "cache";
	
	public static final int			MAX_FILES_PER_DIR	= 1000;
	public static final int			MAX_DIRS			= 1000;
	
	protected static ShareManagerImpl	singleton;
	private static AEMonitor			class_mon	= new AEMonitor( "ShareManager:class" );

	protected AEMonitor				this_mon	= new AEMonitor( "ShareManager" );

	protected TOTorrentCreator		to_creator;
	
	public static ShareManagerImpl
	getSingleton()
	
		throws ShareException
	{
		try{
			class_mon.enter();
		
			if ( singleton == null ){
				
				singleton = new ShareManagerImpl();
			}
			
			return( singleton );
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	
	protected boolean			initialised;
	protected File				share_dir;
	
	protected URL				announce_url;
	protected ShareConfigImpl	config;
	
	protected Map				shares 		= new HashMap();
	
	protected shareScanner		current_scanner;
	
	protected List				listeners	= new ArrayList();
	
	protected
	ShareManagerImpl()
	
		throws ShareException
	{
		COConfigurationManager.addListener(
			new COConfigurationListener()
			{
				public void
				configurationSaved()
				{
					announce_url	= null;
				}
			});
	}
	
	public void
	initialise()
		throws ShareException
	{
		try{
			this_mon.enter();
		
			if ( !initialised ){
			
				initialised	= true;
				
				share_dir = FileUtil.getUserFile( TORRENT_STORE );
				
				share_dir.mkdirs();
								
				config = new ShareConfigImpl();
				
				try{
					config.suspendSaving();
				
					config.loadConfig(this);
			
					checkConsistency();
					
				}finally{
				
					Iterator it = shares.values().iterator();
					
					while(it.hasNext()){
					
						ShareResourceImpl	resource = (ShareResourceImpl)it.next();
						
						if ( resource.getType() == ShareResource.ST_DIR_CONTENTS ){
				
							for (int i=0;i<listeners.size();i++){
								
								try{
									
									((ShareManagerListener)listeners.get(i)).resourceAdded( resource );
									
								}catch( Throwable e ){
									
									Debug.printStackTrace( e );
								}
							}
						}
					}
					
					config.resumeSaving();
				}
				
				readAZConfig();
			}
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	readAZConfig()
	{
		COConfigurationManager.addParameterListener( "Sharing Rescan Enable", this );	
		
		readAZConfigSupport();
	}
	
	public void
	parameterChanged(
		String	name )
	{
		readAZConfigSupport();
	}
	
	protected void
	readAZConfigSupport()
	{
		try{
			this_mon.enter();
		
			boolean	scan_enabled	= COConfigurationManager.getBooleanParameter( "Sharing Rescan Enable" );
						
			if ( !scan_enabled ){
			
				current_scanner	= null;
				
			}else if ( current_scanner == null ){
				
				current_scanner = new shareScanner();
			}
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected ShareConfigImpl
	getShareConfig()
	{
		return( config );
	}
	
	protected void
	checkConsistency()
	
		throws ShareException
	{
			// copy set for iteration as consistency check can delete resource
		
		Iterator	it = new HashSet(shares.values()).iterator();
		
		while(it.hasNext()){
			
			ShareResourceImpl	resource = (ShareResourceImpl)it.next();
			
			resource.checkConsistency();
		}
	}
	
	protected void
	deserialiseResource(
		Map					map )
	{
		try{
			ShareResourceImpl	new_resource = null;
			
			int	type = ((Long)map.get("type")).intValue();
			
			if ( 	type == ShareResource.ST_FILE ||
					type == ShareResource.ST_DIR ){
				
				new_resource = ShareResourceFileOrDirImpl.deserialiseResource( this, map, type );
				
			}else{
				
				new_resource = ShareResourceDirContentsImpl.deserialiseResource( this, map );
			}
			
			if ( new_resource != null ){
				
				ShareResourceImpl	old_resource = (ShareResourceImpl)shares.get(new_resource.getName());
				
				if ( old_resource != null ){
					
					old_resource.delete(true);
				}
				
				shares.put( new_resource.getName(), new_resource );
				
					// we delay the reporting of dir_contents until all recovery is complete so that
					// the resource reported is initialised correctly
				
				if ( type != ShareResource.ST_DIR_CONTENTS ){
					
					for (int i=0;i<listeners.size();i++){
						
						try{
						
							((ShareManagerListener)listeners.get(i)).resourceAdded( new_resource );
							
						}catch( Throwable e ){
						
							Debug.printStackTrace( e );
						}
					}
				}
			}
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
		}
	}
	
	protected String
	getNewTorrentLocation()
	
		throws ShareException
	{
		Random rand = new Random(SystemTime.getCurrentTime());
		
		for (int i=1;i<=MAX_DIRS;i++){
			
			String	cache_dir_str = share_dir + File.separator + TORRENT_SUBSTORE + i;
			
			File	cache_dir = new File(cache_dir_str);
			
			if ( !cache_dir.exists()){
				
				cache_dir.mkdirs();
			}
			
			if ( cache_dir.listFiles().length < MAX_FILES_PER_DIR ){
				
				for (int j=0;j<MAX_FILES_PER_DIR;j++){
					
					long	file = Math.abs(rand.nextLong());
			
					File	file_name = new File(cache_dir_str + File.separator + file + ".torrent");
					
					if ( !file_name.exists()){
						
							// return path relative to cache_dir to save space 
						
						return( TORRENT_SUBSTORE + i + File.separator + file + ".torrent" );
					}
				}
			}
		}
		
		throw( new ShareException( "ShareManager: Failed to allocate cache file"));
	}
	
	protected void
	writeTorrent(
		ShareItemImpl		item )
	
		throws ShareException
	{
		try{
			item.getTorrent().writeToFile( getTorrentFile(item ));
			
		}catch( TorrentException e ){
			
			throw( new ShareException( "ShareManager: Torrent write fails", e ));
		}
	}
	
	protected void
	readTorrent(
		ShareItemImpl		item )
	
		throws ShareException
	{
		try{
			TOTorrent torrent = TOTorrentFactory.deserialiseFromBEncodedFile( getTorrentFile(item ));
			
			item.setTorrent(new TorrentImpl(torrent));
			
		}catch( TOTorrentException e ){
			
			throw( new ShareException( "ShareManager: Torrent read fails", e ));
		}
	}
	
	protected void
	deleteTorrent(
		ShareItemImpl		item )
	{
		File	torrent_file = getTorrentFile(item);
				
		torrent_file.delete();
	}
	
	protected boolean
	torrentExists(
		ShareItemImpl		item )
	{		
		return( getTorrentFile(item).exists());
	}
	
	protected File
	getTorrentFile(
		ShareItemImpl		item )
	{
		return( new File(share_dir+File.separator+item.getTorrentLocation()));
	}
	
	protected URL
	getAnnounceURL()
	
		throws ShareException
	{
		if ( announce_url == null ){
						
			String	protocol = COConfigurationManager.getStringParameter( "Sharing Protocol", "HTTP" );
			
			int		port;
			String	url_protocol;
			
			if ( protocol.equals( "HTTPS" ) ){
				
				port = COConfigurationManager.getIntParameter("Tracker Port SSL", TRHost.DEFAULT_PORT_SSL );
				
				url_protocol	= "https";
				
			}else if ( protocol.equals( "HTTP" )){
				
				port = COConfigurationManager.getIntParameter("Tracker Port", TRHost.DEFAULT_PORT );
				
				url_protocol 	= "http";
				
			}else if ( protocol.equals( "UDP" )){
				
				port = COConfigurationManager.getIntParameter("Tracker Port", TRHost.DEFAULT_PORT );

				url_protocol	= "udp";
				
			}else{
				
				port 			= -1;
				url_protocol	= null;
			}
			
			try{
				if ( url_protocol == null ){
					
					announce_url	= TorrentUtils.getDecentralisedEmptyURL();

				}else{
					
					String 	tracker_ip 		= COConfigurationManager.getStringParameter("Tracker IP", "");
					
					if ( tracker_ip.length() == 0 ){
						
						throw( new ShareException( "ShareManager: Tracker must be configured"));
					}

					announce_url = new URL( url_protocol + "://" + tracker_ip + ":"+ port + "/announce" );
				}
				
			}catch( MalformedURLException e ){
				
				throw( new ShareException( "ShareManager: Announce URL invalid", e ));
			}
			
		}
		
		return( announce_url );
	}
	
	protected boolean
	getAddHashes()
	{
		return( COConfigurationManager.getBooleanParameter( "Sharing Add Hashes", true ));
	}
	
	public ShareResource[]
	getShares()
	{
		ShareResource[]	res = new ShareResource[shares.size()];
		
		shares.values().toArray( res );
		
		return( res );
	}
	
	protected ShareResourceImpl
	getResource(
		File		file )
	{
		return((ShareResourceImpl)shares.get(file.toString()));
	}
	
	public ShareResource
	getShare(
		File	file_or_dir )
	{
		return( getResource( file_or_dir ));
	}
	
	public ShareResourceFile
	addFile(
		File	file )
	
		throws ShareException, ShareResourceDeletionVetoException
	{
		LGLogger.log( "ShareManager: addFile '" + file.toString()+ "'" );

		try{
			return( (ShareResourceFile)addFileOrDir( file, ShareResource.ST_FILE, false ));
			
		}catch( ShareException e ){
			
			reportError(e);
			
			throw(e);
		}
	}
	
	public ShareResourceFile
	getFile(
		File	file )
	
		throws ShareException
	{
		return( (ShareResourceFile)ShareResourceFileImpl.getResource( this, file ));
	}
	
	public ShareResourceDir
	addDir(
		File	dir )
	
		throws ShareException, ShareResourceDeletionVetoException
	{
		LGLogger.log( "ShareManager: addDir '" + dir.toString()+ "'" );

		try{
			this_mon.enter();
			
			return( (ShareResourceDir)addFileOrDir( dir, ShareResource.ST_DIR, false ));
			
		}catch( ShareException e ){
			
			reportError(e);
			
			throw(e);
			
		}finally{
			
			this_mon.exit();
		}
	}
	
	public ShareResourceDir
	getDir(
		File	file )
	
		throws ShareException
	{
		return( (ShareResourceDir)ShareResourceDirImpl.getResource( this, file ));
	}
	
	protected ShareResource
	addFileOrDir(
		File		file,
		int			type,
		boolean		modified )
	
		throws ShareException, ShareResourceDeletionVetoException
	{
		try{
			this_mon.enter();
		
			String	name = file.toString();
			
			ShareResource	old_resource = (ShareResource)shares.get(name);
			
			if ( old_resource != null ){
		
				old_resource.delete();
			}
			
			ShareResourceImpl new_resource;
			
			if ( type == ShareResource.ST_FILE ){
		
				reportCurrentTask( "Adding file '" + name + "'");
				
				new_resource = new ShareResourceFileImpl( this, file );
				
			}else{
				
				reportCurrentTask( "Adding dir '" + name + "'");
				
				new_resource = new ShareResourceDirImpl( this, file );
			}
			
			shares.put(name, new_resource );
			
			config.saveConfig();
			
			for (int i=0;i<listeners.size();i++){
				
				try{
					
					if ( modified ){
						
						((ShareManagerListener)listeners.get(i)).resourceModified( new_resource );
					
					}else{
						
						((ShareManagerListener)listeners.get(i)).resourceAdded( new_resource );				
					}
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
				}
			}
			
			return( new_resource );
			
		}finally{
			
			this_mon.exit();
		}
	}
	

	
	public ShareResourceDirContents
	addDirContents(
		File		dir,
		boolean		recursive )
	
		throws ShareException, ShareResourceDeletionVetoException
	{
		LGLogger.log( "ShareManager: addDirContents '" + dir.toString()+ "'" );

		try{
			this_mon.enter();
			
			String	name = dir.toString();
			
			reportCurrentTask( "Adding dir contents '" + name + "', recursive = " + recursive );
	
			ShareResource	old_resource = (ShareResource)shares.get( name );
			
			if ( old_resource != null ){
				
				old_resource.delete();
			}

			ShareResourceDirContents new_resource = new ShareResourceDirContentsImpl( this, dir, recursive );
						
			shares.put( name, new_resource );
			
			config.saveConfig();
			
			for (int i=0;i<listeners.size();i++){
				
				try{
					
					((ShareManagerListener)listeners.get(i)).resourceAdded( new_resource );
					
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
				}
			}
			
			return( new_resource );
			
		}catch( ShareException e ){
			
			reportError(e);
			
			throw(e);
			
		}finally{
			
			this_mon.exit();
		}
		
	}	
	
	protected void
	delete(
		ShareResourceImpl	resource )
	
		throws ShareException
	{
		LGLogger.log( "ShareManager: resource '" + resource.getName() + "' deleted" );
		
		try{
			this_mon.enter();
		
			shares.remove(resource.getName());
			
			resource.deleteInternal();
			
			config.saveConfig();
			
			for (int i=0;i<listeners.size();i++){
				
				try{
					
					((ShareManagerListener)listeners.get(i)).resourceDeleted( resource );
					
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
				}
			}
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	scanShares()
	
		throws ShareException
	{
		LGLogger.log( "ShareManager: scanning resources for changes" );

		checkConsistency();
	}
	
		// bit of a hack this, but to do it properly would require extensive rework to decouple the
		// process of saying "share file" and then actually doing it 
	
	protected  void
	setTorrentCreator(
		TOTorrentCreator	_to_creator )
	{
		to_creator	= _to_creator;
	}
	
	public void
	cancelOperation()
	{
		TOTorrentCreator	temp = to_creator;
		
		if ( temp != null ){
			
			temp.cancel();
		}
	}
	
	public void
	reportProgress(
		int		percent_complete )
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				
				((ShareManagerListener)listeners.get(i)).reportProgress( percent_complete );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}		
	}
	
	public void
	reportCurrentTask(
		String	task_description )
	{
		for (int i=0;i<listeners.size();i++){
			
			try{
				
				((ShareManagerListener)listeners.get(i)).reportCurrentTask( task_description );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}			
	}

	protected void
	reportError(
		Throwable e )
	{
		String	message = e.getMessage();
		
		if ( message != null ){
			
			reportCurrentTask( Debug.getNestedExceptionMessage(e));
			
		}else{
			
			reportCurrentTask( e.toString());
		}
	}
	public void
	addListener(
		ShareManagerListener		l )
	{
		listeners.add(l);	
	}
	
	public void
	removeListener(
		ShareManagerListener		l )
	{
		listeners.remove(l);
	}
	
	protected class
	shareScanner
	{
		boolean	run = true;
		
		protected
		shareScanner()
		{
			current_scanner	= this;
			
			Thread t = 
				new AEThread( "ShareManager::scanner" )
				{
					public void
					runSupport()
					{
						while( current_scanner == shareScanner.this ){
						
							try{
								
								int		scan_period		= COConfigurationManager.getIntParameter( "Sharing Rescan Period" );

								if ( scan_period < 1 ){
									
									scan_period	= 1;
								}
								
								Thread.sleep( scan_period * 1000 );
								
								if ( current_scanner == shareScanner.this ){
									
									scanShares();
								}

							}catch( Throwable e ){
								
								Debug.printStackTrace(e);
							}
						}
					}
				};
				
			t.setDaemon(true);
			
			t.start();
			
		}
	}
}
