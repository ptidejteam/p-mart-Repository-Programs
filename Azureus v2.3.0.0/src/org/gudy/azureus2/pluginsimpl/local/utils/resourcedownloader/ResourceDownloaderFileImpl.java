/*
 * Created on 29-Nov-2004
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

import java.io.*;

import org.gudy.azureus2.core3.util.AESemaphore;
import org.gudy.azureus2.core3.util.AEThread;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.FileUtil;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;

/**
 * @author parg
 *
 */
public class 
ResourceDownloaderFileImpl
	extends 	ResourceDownloaderBaseImpl
{
	protected boolean					cancelled;
	protected File						file;

	protected Object					result;
	protected AESemaphore				done_sem	= new AESemaphore("RDTimeout");
		
	protected long						size = -2;
	
	public
	ResourceDownloaderFileImpl(
		ResourceDownloaderBaseImpl	_parent,
		File						_file )
	{
		super( _parent );
		
		file		= _file;
	}
	
	public String
	getName()
	{
		return( file.toString());
	}
	
	protected void
	setSize(
		long	size )
	{
	}
	
	public long
	getSize()
	
		throws ResourceDownloaderException
	{	
		return( FileUtil.getFileOrDirectorySize( file ));
	}
	
	
	public ResourceDownloader
	getClone(
		ResourceDownloaderBaseImpl	parent )
	{
		ResourceDownloaderFileImpl c = new ResourceDownloaderFileImpl( getParent(), file );
				
		return( c );
	}
	
	public InputStream
	download()
	
		throws ResourceDownloaderException
	{
		asyncDownload();
		
		done_sem.reserve();
		
		if ( result instanceof ResourceDownloaderException ){
			
			throw((ResourceDownloaderException)result);
		}
		
		return((InputStream)result);	
	}
	
	public void
	asyncDownload()
	{		
		try{
			this_mon.enter();
		
			if ( !cancelled ){
								
				informActivity( getLogIndent() + ( file.isDirectory()?"Processing: ":"Downloading: " ) + getName());
				
				Thread t = new AEThread( "ResourceDownloaderTimeout")
					{
						public void
						runSupport()
						{
							try{
								
									// download of a local dir -> null inputstream
								
								if ( file.isDirectory()){
									
									completed( ResourceDownloaderFileImpl.this, null );

								}else{
								
									completed( ResourceDownloaderFileImpl.this, new FileInputStream( file ));
								}
								
							}catch( Throwable e ){
								
								failed( ResourceDownloaderFileImpl.this, new ResourceDownloaderException( "Failed to read file", e ));
								
								Debug.printStackTrace( e );
							}
						}
					};
				
				t.setDaemon(true);
		
				t.start();
			}
		}finally{
			
			this_mon.exit();
		}
	}
		
	public void
	cancel()
	{
		cancel( new ResourceDownloaderException( "Download cancelled"));
	}
	
	protected void
	cancel(
		ResourceDownloaderException reason )
	{
		setCancelled();
		
		try{
			this_mon.enter();
		
			result	= reason; 
			
			cancelled	= true;
		
			informFailed((ResourceDownloaderException)result );
			
		}finally{
			
			this_mon.exit();
		}
	}	
	
	public boolean
	completed(
		ResourceDownloader	downloader,
		InputStream			data )
	{
		if (informComplete( data )){
			
			result	= data;
			
			done_sem.release();
			
			return( true );
		}
		
		return( false );
	}
	
	public void
	failed(
		ResourceDownloader			downloader,
		ResourceDownloaderException e )
	{
		result		= e;
		
		done_sem.release();
		
		informFailed( e );
	}
}
