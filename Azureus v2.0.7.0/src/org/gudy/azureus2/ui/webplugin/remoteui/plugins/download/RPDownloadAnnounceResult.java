/*
 * File    : RPDownloadAnnounceResult.java
 * Created : 30-Jan-2004
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

package org.gudy.azureus2.ui.webplugin.remoteui.plugins.download;

/**
 * @author parg
 *
 */
import org.gudy.azureus2.plugins.download.*;

import org.gudy.azureus2.ui.webplugin.remoteui.plugins.*;


public class 
RPDownloadAnnounceResult
	extends		RPObject
	implements 	DownloadAnnounceResult
{
	protected transient DownloadAnnounceResult		delegate;

	protected int				seed_count;
	protected int				non_seed_count;
	
	public static RPDownloadAnnounceResult
	create(
		DownloadAnnounceResult		_delegate )
	{
		RPDownloadAnnounceResult	res =(RPDownloadAnnounceResult)_lookupLocal( _delegate );
		
		if ( res == null ){
			
			res = new RPDownloadAnnounceResult( _delegate );
		}
		
		return( res );
	}
	
	protected
	RPDownloadAnnounceResult(
		DownloadAnnounceResult		_delegate )
	{
		super( _delegate );
	}
	
	protected void
	_setDelegate(
		Object		_delegate )
	{
		delegate = (DownloadAnnounceResult)_delegate;
		
		seed_count		= delegate.getSeedCount();
		non_seed_count	= delegate.getNonSeedCount();	
	}
	
	public void
	_setLocal()
	
		throws RPException
	{
		_fixupLocal();
	}
	
	public RPReply
	_process(
		RPRequest	request	)
	{
		String	method = request.getMethod();	
		
		throw( new RPException( "Unknown method: " + method ));
	}
	
	
		// ***************************************************
	
	public Download
	getDownload()
	{
		notSupported();

		return( null );
	}
	
	public int
	getResponseType()
	{
		notSupported();

		return( 0 );
	}
	
	public int
	getReportedPeerCount()
	{
		notSupported();

		return( 0 );
	}
	
	public int
	getSeedCount()
	{
		return( seed_count );
	}
	
	public int
	getNonSeedCount()
	{
		return( non_seed_count );
	}
	
	public String
	getError()
	{
		notSupported();

		return( null );
	}
}