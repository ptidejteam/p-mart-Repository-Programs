/*
 * Created on 15-Dec-2005
 * Created by Paul Gardner
 * Copyright (C) 2005, 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.plugins.extseed.impl.getright;

import java.io.File;
import java.net.URL;
import java.util.*;

import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
import org.gudy.azureus2.plugins.download.Download;
import org.gudy.azureus2.plugins.torrent.Torrent;

import com.aelitis.azureus.plugins.extseed.ExternalSeedPlugin;
import com.aelitis.azureus.plugins.extseed.ExternalSeedReader;
import com.aelitis.azureus.plugins.extseed.ExternalSeedReaderFactory;

public class 
ExternalSeedReaderFactoryGetRight
	implements ExternalSeedReaderFactory
{
	public
	ExternalSeedReaderFactoryGetRight()
	{
	}
	
	public ExternalSeedReader[]
  	getSeedReaders(
  		ExternalSeedPlugin		plugin,
  		Download				download )
	{		
		Torrent	torrent = download.getTorrent();
		
		try{
			Object	obj = torrent.getAdditionalProperty( "url-list" );
			
			if ( obj instanceof List ){
				
				List	urls = (List)obj;

				List	readers = new ArrayList();
				
				for (int i=0;i<urls.size();i++){
					
					try{
						URL	url = new URL(new String((byte[])urls.get(i)));
						
						String	protocol = url.getProtocol().toLowerCase();
						
						plugin.log( download.getName() + ": GR found seed: " + url );
						
						if ( protocol.equals( "http" )){
							
							readers.add( new ExternalSeedReaderGetRight(plugin,torrent, url));
							
						}else{
							
							plugin.log( download.getName() + ": GR unsupported protocol: " + url );
						}
					}catch( Throwable e ){
						
						e.printStackTrace();
					}
				}
				
				ExternalSeedReader[]	res = new ExternalSeedReader[ readers.size() ];
				
				readers.toArray( res );
				
				return( res );
			}
		}catch( Throwable e ){
			
			e.printStackTrace();
		}
		
		return( new ExternalSeedReader[0] );
	}
	
	public static void
	main(
		String[]	args )
	{
		try{
			File file = new File  ( "C:\\temp\\test.torrent");
			
			TOTorrent	torrent = TOTorrentFactory.deserialiseFromBEncodedFile( file );
			
			Map	map = torrent.serialiseToMap();
			
			List	urls = new ArrayList();
			
			urls.add( "http://192.168.1.2:8080/test.dat" );
			
			map.put( "url-list", urls);
			
			torrent = TOTorrentFactory.deserialiseFromMap( map );
			
			torrent.serialiseToBEncodedFile( file );
			
		}catch( Throwable e ){
			
			e.printStackTrace();
		}
	}
}
