/*
 * Created on 21-May-2004
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

package org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader;

/**
 * @author parg
 *
 */

import java.io.*;

import org.gudy.azureus2.core3.config.COConfigurationManager;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentException;
import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
import org.gudy.azureus2.core3.util.*;
import org.gudy.azureus2.plugins.utils.resourcedownloader.*;
import org.gudy.azureus2.plugins.download.*;
import org.gudy.azureus2.pluginsimpl.local.torrent.*;
import org.gudy.azureus2.pluginsimpl.local.tracker.TrackerWebPageResponseImpl;
import org.gudy.azureus2.pluginsimpl.local.*;

public class 
ResourceDownloaderTorrentImpl 	
	extends 	ResourceDownloaderBaseImpl
	implements	ResourceDownloaderListener
{
	public static final int	MAX_FOLLOWS = 1;
	
	protected ResourceDownloaderBaseImpl		delegate;
	protected boolean							persistent;
	protected File								download_dir;
	
	protected long						size	= -2;
	
		// this + clones *share* the torrent object to avoid downloading more than once
	
	protected TOTorrent[]				torrent_holder	 = new TOTorrent[1];
	
	protected DownloadManager			download_manager;
	protected Download					download;
	
	protected boolean					cancelled;
	protected ResourceDownloader		current_downloader;
	protected Object					result;
	protected AESemaphore				done_sem	= new AESemaphore("RDTorrent");
			
	public
	ResourceDownloaderTorrentImpl(
		ResourceDownloaderBaseImpl	_parent,
		ResourceDownloader			_delegate,
		boolean						_persistent,
		File						_download_dir )
	{
		super( _parent );
		
		persistent		= _persistent;
		download_dir	= _download_dir;
		delegate		= (ResourceDownloaderBaseImpl)_delegate;
		
		delegate.setParent( this );
		
		download_manager = PluginInitializer.getDefaultInterface().getDownloadManager();
	}
	
	public String
	getName()
	{
		return( delegate.getName() + ": torrent" );
	}
	
	public long
	getSize()
	
		throws ResourceDownloaderException
	{	
		if ( size == -2 ){
			
			try{
				size = getSizeSupport();
				
			}finally{
				
				if ( size == -2 ){
					
					size = -1;
				}
				
				setSize( size );
			}
		}
		

		return( size );
	}
	
	protected void
	setSize(
		long		l )
	{
		size	= l;
		
		if ( size >= 0 ){
			
			delegate.setSize( size );
		}
	}
	
	protected void
	setProperty(
		String	name,
		Object	value )
	{
		setPropertySupport( name, value );
		
		delegate.setProperty( name, value );
	}
	
	protected long
	getSizeSupport()
	
		throws ResourceDownloaderException
	{
		try{
			if ( torrent_holder[0] == null ){
				
				ResourceDownloader	x = delegate.getClone( this );
			
				addReportListener( x );
			
				InputStream	is = x.download();
				
				try{
					torrent_holder[0] = TOTorrentFactory.deserialiseFromBEncodedInputStream( is );
					
				}finally{
					
					try{
						is.close();
						
					}catch( IOException e ){
					}
				}
				
				if( !torrent_holder[0].isSimpleTorrent()){
					
					throw( new ResourceDownloaderException( "Only simple torrents supported" ));
				}
			}
			
			try{
				String	file_str = new String( torrent_holder[0].getName());
				
				int	pos = file_str.lastIndexOf( "." );
				
				String	file_type;
				
				if ( pos != -1 ){
				
					file_type = file_str.substring(pos+1);
					
				}else{
					
					file_type = null;
				}
				
				setProperty( 	ResourceDownloader.PR_STRING_CONTENT_TYPE,
								TrackerWebPageResponseImpl.guessContentTypeFromFileType( file_type ));
				
			}catch( Throwable e ){
				
				Debug.printStackTrace(e);
			}
			
			return( torrent_holder[0].getSize());
			
		}catch( TOTorrentException e ){
			
			throw( new ResourceDownloaderException( "Torrent deserialisation failed", e ));
		}
	}	
	
	protected void
	setSizeAndTorrent(
		long			_size,
		TOTorrent[]		_torrent_holder )
	{
		size			= _size;
		torrent_holder	= _torrent_holder;
	}
	
	public ResourceDownloaderBaseImpl
	getClone(
		ResourceDownloaderBaseImpl	parent )
	{
		ResourceDownloaderTorrentImpl c = new ResourceDownloaderTorrentImpl( parent, delegate.getClone( this ), persistent, download_dir );
		
		c.setSizeAndTorrent( size, torrent_holder );
		
		c.setProperties( this );

		return( c );
	}
	
	public InputStream
	download()
	
		throws ResourceDownloaderException
	{
		asyncDownload();
		
		done_sem.reserve();
		
		if ( result instanceof InputStream ){
			
			return((InputStream)result);
		}
		
		throw((ResourceDownloaderException)result);
	}
	
	public void
	asyncDownload()
	{
		try{
			this_mon.enter();
		
			if ( cancelled ){
				
				done_sem.release();
				
				informFailed((ResourceDownloaderException)result);
				
			}else{
	
				if ( torrent_holder[0] == null ){
					
					current_downloader = delegate.getClone( this );
					
					informActivity( getLogIndent() + "Downloading: " + getName());
		
					current_downloader.addListener( this );
					
					current_downloader.asyncDownload();
					
				}else{
					
					downloadTorrent();
				}
			}
		}finally{
			
			this_mon.exit();
		}
	}
	
	protected void
	downloadTorrent()
	{
		try{
			informActivity( getLogIndent() + "Downloading: " + new String( torrent_holder[0].getName(), Constants.DEFAULT_ENCODING ));
			
				// we *don't* want this temporary file to be deleted automatically as we're
				// going to use it across Azureus restarts to hold the download data and
				// to seed it afterwards. Therefore we don't use AETemporaryFileHandler!!!!
			
			final File	torrent_file 	= File.createTempFile("AZU", null );
			
			if ( download_dir != null && !download_dir.exists()){
				
				download_dir.mkdirs();
			}
			
			final File	data_dir		= download_dir==null?torrent_file.getParentFile():download_dir;
			
			torrent_holder[0].serialiseToBEncodedFile( torrent_file );
						
			if ( persistent ){
				
				download = download_manager.addDownload( new TorrentImpl(torrent_holder[0]), torrent_file, data_dir );
				
			}else{
				
				download = download_manager.addNonPersistentDownload( new TorrentImpl(torrent_holder[0]), torrent_file, data_dir );
			}
			
			download.setPosition(1);				
			
			download.setForceStart( true );
			
			download_manager.addListener(
				new DownloadManagerListener()
				{
					public void
					downloadAdded(
						Download	download )
					{					
					}
					
					public void
					downloadRemoved(
						Download	_download )
					{
						if ( download == _download ){
							
							ResourceDownloaderTorrentImpl.this.downloadRemoved( torrent_file, data_dir );
						}
					}
				});
						
			download.addListener(
				new DownloadListener()
				{
					public void
					stateChanged(
						Download		download,
						int				old_state,
						int				new_state )
					{		
						// System.out.println( "state change:" + old_state + "->" + new_state );
						
						if ( new_state == Download.ST_SEEDING ){
							
							download.removeListener( this );
							
							downloadSucceeded( download, torrent_file, data_dir );
						}
					}

					public void
					positionChanged(
						Download	download, 
						int 		oldPosition,
						int 		newPosition )
					{
					}
				});
				
			Thread	t = 
				new AEThread( "RDTorrent percentage checker")
				{
					public void
					runSupport()
					{
						int	last_percentage = 0;
						
						while( result == null ){
														
							int	this_percentage = download.getStats().getCompleted()/10;
							
							long	total	= torrent_holder[0].getSize();
														
							if ( this_percentage != last_percentage ){
								
								reportPercentComplete( ResourceDownloaderTorrentImpl.this, this_percentage );
							
								last_percentage = this_percentage;
							}
							
							try{
								Thread.sleep(1000);
								
							}catch( Throwable e ){
								
								Debug.printStackTrace( e );
							}
						}
					}
				};
				
			t.setDaemon( true );
			
			t.start();
			
				// its possible that the d/l has already occurred and it is seeding!
			
			if ( download.getState() == Download.ST_SEEDING ){
				
				downloadSucceeded( download, torrent_file, data_dir );
			}
		}catch( Throwable e ){
			
			failed( this, new ResourceDownloaderException( "Torrent download failed", e ));
		}
	}
	
	protected void
	downloadSucceeded(
		Download	download,
		File		torrent_file,
		File		data_dir )
	{
		reportActivity( "Torrent download complete" );
		
			// assumption is that this is a SIMPLE torrent
		
			// unfortunately by the time we get here the data might have been
			// moved via the "move on complete" options
		
		File	target_file = 
			new File( data_dir,	new String(torrent_holder[0].getFiles()[0].getPathComponents()[0]));
		
		if ( !target_file.exists()){
	
			if ( COConfigurationManager.getBooleanParameter("Move Completed When Done", false)){
				
				File	moved_target_file = 
					new File( 
							COConfigurationManager.getStringParameter("Completed Files Directory", ""),
							new String(torrent_holder[0].getFiles()[0].getPathComponents()[0]));
				
					// hmm, explicit target location and its moved, copy it back :)
				
				try{
					if ( download_dir != null && moved_target_file.exists()){
						
						FileUtil.copyFile( moved_target_file, target_file );
					}
					
				}catch( Throwable e ){
					
					Debug.printStackTrace(e);
				}
				
					// carry on and use the moved one 
				
				target_file	= moved_target_file;
			}
			
			if ( !target_file.exists()){

					// not sure why we don't just use the save path and avoid all the crap above
				
				File	actual_target_file = new File(download.getSavePath());
				
				try{
					if ( download_dir != null && actual_target_file.exists()){
						
						FileUtil.copyFile( actual_target_file, target_file );
					}
					
				}catch( Throwable e ){
					
					Debug.printStackTrace(e);
				}
				
				target_file	= actual_target_file;
			}
		}

		try{
			if ( !target_file.exists()){
				
				throw( new Exception( "File '" + target_file.toString() + "' not found" ));
			}
			
			InputStream	data = new FileInputStream( target_file );
			
			informComplete( data );
				
			result	= data;
				
			done_sem.release();
			
		}catch( Throwable e ){
			
			Debug.printStackTrace( e );
			
			failed( this, new ResourceDownloaderException( "Failed to read downloaded torrent data: " + e.getMessage(), e ));
		}
	}
	
	protected void
	downloadRemoved(
		File		torrent_file,
		File		data_dir )
	{
		reportActivity( "Torrent removed" );

		if (!( result instanceof InputStream )){
			
			failed( this, new ResourceDownloaderException( "Download did not complete" ));
		}
	}
	
	public void
	cancel()
	{
		setCancelled();
		
		try{
			this_mon.enter();
		
			result	= new ResourceDownloaderException( "Download cancelled");
			
			cancelled	= true;
			
			informFailed((ResourceDownloaderException)result );
			
			done_sem.release();
			
			if ( current_downloader != null ){
				
				current_downloader.cancel();
			}
		}finally{
			
			this_mon.exit();
		}
	}	
	
	public boolean
	completed(
		ResourceDownloader	downloader,
		InputStream			data )
	{
		try{			
			torrent_holder[0] = TOTorrentFactory.deserialiseFromBEncodedInputStream( data );
			
			if( torrent_holder[0].isSimpleTorrent()){
				
				downloadTorrent();
				
			}else{
				
				failed( this, new ResourceDownloaderException( "Only simple torrents supported" ));
			}
			
		}catch( TOTorrentException e ){
			
			failed( downloader, new ResourceDownloaderException( "Torrent deserialisation failed", e ));
			
		}finally{
			
			try{
				data.close();
				
			}catch( IOException e ){
			}
		}
		
		return( true );
	}
	
	public void
	failed(
		ResourceDownloader			downloader,
		ResourceDownloaderException e )
	{
		result		= e;
		
		done_sem.release();

		informFailed(e);
	}
	
	public void
	reportPercentComplete(
		ResourceDownloader	downloader,
		int					percentage )
	{
		if ( downloader == this ){
			
			informPercentDone( percentage );
		}
	}
}
