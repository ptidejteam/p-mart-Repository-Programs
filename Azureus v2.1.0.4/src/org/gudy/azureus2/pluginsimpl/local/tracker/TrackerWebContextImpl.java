/*
 * File    : TrackerWebContextImpl.java
 * Created : 23-Jan-2004
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

package org.gudy.azureus2.pluginsimpl.local.tracker;

/**
 * @author parg
 *
 */

import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.gudy.azureus2.plugins.tracker.*;
import org.gudy.azureus2.plugins.tracker.web.*;
import org.gudy.azureus2.core3.tracker.server.*;

public class 
TrackerWebContextImpl 
	extends		TrackerWCHelper
	implements 	TRTrackerServerListener
{
	protected TRTrackerServer		server;
	
	protected List					auth_listeners	= new ArrayList();
	
	public 
	TrackerWebContextImpl(
		TrackerImpl	_tracker,
		String		name,
		int			port,
		int			protocol )
	
		throws TrackerException
	{
		setTracker( _tracker );
				
		try{
			
			if ( protocol == Tracker.PR_HTTP ){
				
				server = TRTrackerServerFactory.create( name, TRTrackerServerFactory.PR_TCP, port, false );
				
			}else{
				
				server = TRTrackerServerFactory.createSSL( name, TRTrackerServerFactory.PR_TCP, port, false );
			}
			
			server.addListener( this );
			
		}catch( TRTrackerServerException e ){
			
			throw( new TrackerException("TRTrackerServerFactory failed", e ));
		}
	}
		
	public String
	getName()
	{
		return( server.getName());
	}
	
	public URL[]
	getURLs()
	{
		try{
			return( 
				new URL[]{ 
						new URL(
							(server.isSSL()?"https":"http") + "://" + 
							server.getHost() + ":" + server.getPort() + "/" )});
			
		}catch( MalformedURLException e ){
			
			e.printStackTrace();
			
			return( null );
		}
	}
	
	public int
	getProtocol()
	{
		return( server.isSSL()?Tracker.PR_HTTPS:Tracker.PR_HTTP );
	}
	
	public String
	getHostName()
	{
		return( server.getHost());
	}
	
	public int
	getPort()
	{
		return( server.getPort());
	}
		
	public boolean
	permitted(
		byte[]	hash,
		boolean	explicit )
	{
		return( false );
	}
	
	public boolean
	denied(
		byte[]	hash,
		boolean	explicit )
	{
		return( false );
	}
	
	public boolean
	authenticate(
		URL			resource,
		String		user,
		String		password )
	{
		for (int i=0;i<auth_listeners.size();i++){
			
			try{
				boolean res = ((TrackerAuthenticationListener)auth_listeners.get(i)).authenticate( resource, user, password );
				
				if ( res ){
					
					return(true );
				}
			}catch( Throwable e ){
				
				e.printStackTrace();
			}
		}
		
		return( false );
	}
	
	public byte[]
	authenticate(
		URL			resource,
		String		user )
	{
		for (int i=0;i<auth_listeners.size();i++){
			
			try{
				byte[] res = ((TrackerAuthenticationListener)auth_listeners.get(i)).authenticate( resource, user );
				
				if ( res != null ){
					
					return( res );
				}
			}catch( Throwable e ){
				
				e.printStackTrace();
			}
		}
		
		return( null );
	}
	
	public synchronized void
	addAuthenticationListener(
		TrackerAuthenticationListener	l )
	{	
		auth_listeners.add(l);
		
		if ( auth_listeners.size() == 1 ){
			
			server.addAuthenticationListener( this );
		}
	}
	
	public synchronized void
	removeAuthenticationListener(
		TrackerAuthenticationListener	l )
	{	
		auth_listeners.remove(l);
		
		if ( auth_listeners.size() == 0 ){
				
			server.removeAuthenticationListener( this );
		}
	}
}
