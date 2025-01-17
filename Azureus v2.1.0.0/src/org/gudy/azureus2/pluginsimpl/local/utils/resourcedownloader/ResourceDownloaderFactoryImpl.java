/*
 * Created on 03-May-2004
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

import java.net.URL;
import java.util.*;

import org.gudy.azureus2.plugins.utils.resourcedownloader.*;

import org.gudy.azureus2.core3.logging.*;

public class 
ResourceDownloaderFactoryImpl
	implements ResourceDownloaderFactory
{
	protected static ResourceDownloaderFactoryImpl	singleton = new ResourceDownloaderFactoryImpl();
	
	public static ResourceDownloaderFactory
	getSingleton()
	{
		return( singleton );
	}
	
	public ResourceDownloader
	create(
		URL		url )
	{
		return( new ResourceDownloaderURLImpl( null, url ));
	}
	
	public ResourceDownloader
	create(
		ResourceDownloaderDelayedFactory		factory )
	{
		return( new ResourceDownloaderDelayedImpl( null, factory ));
	}
	
	public ResourceDownloader
	getRetryDownloader(
		ResourceDownloader		downloader,
		int						retry_count )
	{
		ResourceDownloader res = new ResourceDownloaderRetryImpl( null, downloader, retry_count );

		return( res );
	}
	
	public ResourceDownloader
	getTimeoutDownloader(
		ResourceDownloader		downloader,
		int						timeout_millis )
	{
		ResourceDownloader res = new ResourceDownloaderTimeoutImpl( null, downloader, timeout_millis );
		
		return( res );
	}
	
	public ResourceDownloader
	getAlternateDownloader(
		ResourceDownloader[]		downloaders )
	{
		return( getAlternateDownloader( downloaders, -1, false ));
	}
	
	public ResourceDownloader
	getAlternateDownloader(
		ResourceDownloader[]		downloaders,
		int							max_to_try )
	{
		return( getAlternateDownloader( downloaders, max_to_try, false ));
	}
	
	public ResourceDownloader
	getRandomDownloader(
		ResourceDownloader[]		downloaders )
	{
		return( getAlternateDownloader( downloaders, -1, true ));
	}
	
	public ResourceDownloader
	getRandomDownloader(
		ResourceDownloader[]		downloaders,
		int							max_to_try )
	{
		return( getAlternateDownloader( downloaders, max_to_try, true ));
	}
	
	protected ResourceDownloader
	getAlternateDownloader(
		ResourceDownloader[]		downloaders,
		int							max_to_try,
		boolean						random )
	{
		ResourceDownloader res = new ResourceDownloaderAlternateImpl( null, downloaders, max_to_try, random );
				
		return( res );
	}
	
	public ResourceDownloader
	getMetaRefreshDownloader(
		ResourceDownloader			downloader )
	{
		ResourceDownloader res = new ResourceDownloaderMetaRefreshImpl( null, downloader );
				
		return( res );
	}
	
	public ResourceDownloader
	getTorrentDownloader(
		ResourceDownloader			downloader,
		boolean						persistent )
	{
		return( new ResourceDownloaderTorrentImpl( null, downloader, persistent ));
	}
	
	public ResourceDownloader
	getSuffixBasedDownloader(
		ResourceDownloader			_downloader )
	{
		ResourceDownloaderBaseImpl	dl = (ResourceDownloaderBaseImpl)_downloader;
		
		URL	target = null;
		
		while( true ){
			
			List	kids = dl.getChildren();
			
			if ( kids.size() == 0 ){
				
				target = ((ResourceDownloaderURLImpl)dl).getURL();
				
				break;
			}
			
			dl = (ResourceDownloaderBaseImpl)kids.get(0);
		}
		
		if ( target == null ){
			
			LGLogger.log( "ResourceDownloader: suffix based downloader failed to find leaf");
			
			return( _downloader );
		}
		
		if ( target.toString().toLowerCase().endsWith(".torrent")){
			
			return( getTorrentDownloader( _downloader, true ));
			
		}else{
			
			return( _downloader );
		}
	}
}
