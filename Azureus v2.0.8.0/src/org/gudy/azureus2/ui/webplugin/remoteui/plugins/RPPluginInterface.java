/*
 * File    : RPPluginInterface.java
 * Created : 28-Jan-2004
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

package org.gudy.azureus2.ui.webplugin.remoteui.plugins;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.plugins.*;
import org.gudy.azureus2.plugins.sharing.ShareManager;
import org.gudy.azureus2.plugins.sharing.ShareException;
import org.gudy.azureus2.plugins.tracker.Tracker;
import org.gudy.azureus2.plugins.logging.Logger;
import org.gudy.azureus2.plugins.ipfilter.IPFilter;
import org.gudy.azureus2.plugins.torrent.TorrentManager;
import org.gudy.azureus2.plugins.download.DownloadManager;
import org.gudy.azureus2.plugins.peers.protocol.PeerProtocolManager;
import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.Parameter;
import org.gudy.azureus2.plugins.ui.config.PluginConfigUIFactory;
import org.gudy.azureus2.plugins.ui.tables.peers.PluginPeerItemFactory;
import org.gudy.azureus2.plugins.ui.tables.mytorrents.PluginMyTorrentsItemFactory;

import org.gudy.azureus2.ui.webplugin.remoteui.plugins.download.*;
import org.gudy.azureus2.ui.webplugin.remoteui.plugins.torrent.*;

public class 
RPPluginInterface
	extends		RPObject
	implements 	PluginInterface
{
	protected static long	connection_id_next	= new Random().nextLong();
	
	protected transient PluginInterface		delegate;
	protected transient long				request_id_next;
	
	public static RPPluginInterface
	create(
		PluginInterface		_delegate )
	{
		RPPluginInterface	res =(RPPluginInterface)_lookupLocal( _delegate );
		
		if ( res == null ){
			
			res = new RPPluginInterface( _delegate );
		}
		
		return( res );
	}	
	
	protected long	_connection_id;
	
	protected
	RPPluginInterface(
		PluginInterface		_delegate )
	{
		super( _delegate );
		
		synchronized( RPPluginInterface.class ){
			
			_connection_id = connection_id_next++;
		}
	}
	
	protected long
	_getConectionId()
	{
		return( _connection_id );
	}
	
	protected synchronized long
	_getNextRequestId()
	{
		return( request_id_next++ );
	}
	
	protected void
	_setDelegate(
		Object		_delegate )
	{
		delegate = (PluginInterface)_delegate;
	}
	
	public Object
	_setLocal()
	
		throws RPException
	{
		return( _fixupLocal());
	}
	
	
	public RPReply
	_process(
		RPRequest	request	)
	{
		String	method = request.getMethod();
		
		if ( method.equals( "getPluginProperties")){
			
			return( new RPReply( delegate.getPluginProperties()));
			
		}else if ( method.equals( "getDownloadManager")){
			
			return( new RPReply( RPDownloadManager.create(delegate.getDownloadManager())));
		
		}else if ( method.equals( "getTorrentManager")){
			
			return( new RPReply( RPTorrentManager.create(delegate.getTorrentManager())));
		
		}else if ( method.equals( "getPluginconfig")){
				
			return( new RPReply( RPPluginConfig.create(delegate.getPluginconfig())));
		}
		
		throw( new RPException( "Unknown method: " + method ));
	}
	
	
	public void 
	addView(PluginView view)
	{
		notSupported();
	}
	
	public void addConfigUIParameters(Parameter[] parameters, String displayName)
	{
		notSupported();
	}
	
	public void addColumnToPeersTable(String columnName,PluginPeerItemFactory factory)
	{
		notSupported();
	}

    public void addColumnToMyTorrentsTable(String columnName, PluginMyTorrentsItemFactory factory)
    {
    	notSupported();
    }

	public void addConfigSection(ConfigSection tab)
	{
		notSupported();
	}
	
	public Tracker getTracker()
	{
		notSupported();
		
		return( null );
	}
	
	public Logger getLogger()
	{
		notSupported();
		
		return( null );
	}
	
	public IPFilter getIPFilter()
	{
		notSupported();
		
		return( null );
	}
	
	public DownloadManager
	getDownloadManager()
	{
		RPDownloadManager	res = (RPDownloadManager)dispatcher.dispatch( new RPRequest( this, "getDownloadManager", null )).getResponse();
	
		res._setRemote( dispatcher );
		
		return( res );
	}
	
	
	public PeerProtocolManager
	getPeerProtocolManager()
	{
		notSupported();
		
		return( null );
	}
	
	public ShareManager
	getShareManager()
	
		throws ShareException
	{
		notSupported();
		
		return( null );
	}
	
	 public TorrentManager
	 getTorrentManager()
	 {
		RPTorrentManager	res = (RPTorrentManager)dispatcher.dispatch( new RPRequest( this, "getTorrentManager", null )).getResponse();
		
		res._setRemote( dispatcher );
			
		return( res );
	 }
	 
	/**
	 * @deprecated
	 */
	
	public void openTorrentFile(String fileName)
	{
		notSupported();
	}
	
	/**
	 * @deprecated
	 */
	
	public void openTorrentURL(String url)
	{
		notSupported();
	}
	
	public Properties getPluginProperties()
	{
		return((Properties)dispatcher.dispatch( new RPRequest( this, "getPluginProperties", null )).getResponse());
	}
	
	public String getPluginDirectoryName()
	{
		notSupported();
		
		return( null );
	}
	
	public PluginConfig getPluginconfig()
	{
		RPPluginConfig	res = (RPPluginConfig)dispatcher.dispatch( new RPRequest( this, "getPluginconfig", null )).getResponse();
		
		res._setRemote( dispatcher );
			
		return( res );
	}
	
	public PluginConfigUIFactory getPluginConfigUIFactory()
	{
		notSupported();
		
		return( null );
	}
	
	public ClassLoader
	getPluginClassLoader()
	{
		notSupported();
		
		return( null );
	}
	
	public void
	addListener(
			PluginListener	l )
	{
		notSupported();
	}
	
	public void
	removeListener(
			PluginListener	l )	
	{
		notSupported();
	}
	
	public void
	addEventListener(
		PluginEventListener	l )
	{
		notSupported();
	}
	
	public void
	removeEventListener(
		PluginEventListener	l )	
	{
		notSupported();
	}
}
