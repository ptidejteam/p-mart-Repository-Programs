/*
 * File    : TRTrackerServerFactoryImpl.java
 * Created : 13-Dec-2003
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

package org.gudy.azureus2.core3.tracker.server.impl;

/**
 * @author parg
 *
 */

import java.util.*;

import org.gudy.azureus2.core3.tracker.server.*;
import org.gudy.azureus2.core3.tracker.server.impl.tcp.*;
import org.gudy.azureus2.core3.tracker.server.impl.udp.*;
import org.gudy.azureus2.core3.util.AEMonitor;

public class 
TRTrackerServerFactoryImpl 
{
	protected static List		servers		= new ArrayList();
	protected static List		listeners 	= new ArrayList();
	protected static AEMonitor 	class_mon 	= new AEMonitor( "TRTrackerServerFactory" );

	public static TRTrackerServer
	create(
		String		name,
		int			protocol,
		int			port,
		boolean		ssl,
		boolean		apply_ip_filter )
	
		throws TRTrackerServerException
	{
		try{
			class_mon.enter();
		
			TRTrackerServerImpl	server;
			
			if ( protocol == TRTrackerServerFactory.PR_TCP ){
				
				server = new TRTrackerServerTCP( name, port, ssl, apply_ip_filter );
				
			}else{
				
				if ( ssl ){
					
					throw( new TRTrackerServerException( "TRTrackerServerFactory: UDP doesn't support SSL"));
				}
				
				server = new TRTrackerServerUDP( name, port );
			}
			
			servers.add( server );
			
			for (int i=0;i<listeners.size();i++){
				
				((TRTrackerServerFactoryListener)listeners.get(i)).serverCreated( server );
			}
			
			return( server );
			
		}finally{
			
			class_mon.exit();
		}
	}
	
	public static void
	addListener(
		TRTrackerServerFactoryListener	l )
	{
		try{
			class_mon.enter();
		
			listeners.add( l );
			
			for (int i=0;i<servers.size();i++){
				
				l.serverCreated((TRTrackerServer)servers.get(i));
			}
		}finally{
			
			class_mon.exit();
		}
	}	
	
	public static void
	removeListener(
		TRTrackerServerFactoryListener	l )
	{
		try{
			class_mon.enter();
		
			TRTrackerServerFactoryImpl.removeListener( l );
			
		}finally{
			
			class_mon.exit();
		}
	}
}
