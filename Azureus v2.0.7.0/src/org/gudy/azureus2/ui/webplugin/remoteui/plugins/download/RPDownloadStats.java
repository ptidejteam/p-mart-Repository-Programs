/*
 * File    : PRDownloadStats.java
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
RPDownloadStats
	extends		RPObject
	implements 	DownloadStats
{
	protected transient DownloadStats		delegate;

	protected long				downloaded;
	protected int				completed;
	protected String			status;
	
	public static RPDownloadStats
	create(
		DownloadStats		_delegate )
	{
		RPDownloadStats	res =(RPDownloadStats)_lookupLocal( _delegate );
		
		if ( res == null ){
			
			res = new RPDownloadStats( _delegate );
		}
		
		return( res );
	}
	
	protected
	RPDownloadStats(
		DownloadStats		_delegate )
	{
		super( _delegate );
	}
	
	protected void
	_setDelegate(
		Object		_delegate )
	{
		delegate = (DownloadStats)_delegate;
		
		downloaded	= delegate.getDownloaded();
		completed	= delegate.getCompleted();
		status		= delegate.getStatus();
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
	
	public String
	getStatus()
	{
		return( status );
	}	
	
	public String
	getDownloadDirectory()
	{
		notSupported();
		
		return( null );
	}	
	
	public String
	getTargetFileOrDir()
	{
		notSupported();
		
		return( null );
	}	
	
	public String
	getTrackerStatus()
	{
		notSupported();
		
		return( null );
	}
	
	public int
	getCompleted()
	{
		return( completed );
	}
	
	public long
	getDownloaded()
	{
		return( downloaded );
	}
	
	public long
	getUploaded()
	{
		notSupported();
		
		return( 0 );
	}
	
	public long
	getDiscarded()
	{
		notSupported();
		
		return( 0 );
	}
	
	public long
	getDownloadAverage()
	{
		notSupported();
		
		return( 0 );
	}
	
	public long
	getUploadAverage()
	{
		notSupported();
		
		return( 0 );
	}
	
	public long
	getTotalAverage()
	{
		notSupported();
		
		return( 0 );
	}
	
	public String
	getElapsedTime()
	{
		notSupported();
		
		return( null );
	}	
	
	public String
	getETA()
	{
		notSupported();
		
		return( null );
	}
	
	public long
	getHashFails()
	{
		notSupported();
		
		return( 0 );
	}
	
	public int
	getShareRatio()
	{
		notSupported();
		
		return( 0 );
	}
}