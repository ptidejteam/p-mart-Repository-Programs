/*
 * Created on 29-Jul-2004
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

package org.gudy.azureus2.core3.tracker.server.impl;

/**
 * @author parg
 *
 */

import java.util.*;
import java.net.*;

import org.gudy.azureus2.core3.tracker.server.*;

import org.gudy.azureus2.core3.config.*;
import org.gudy.azureus2.core3.util.*;

public class 
TRTrackerServerNATChecker 
{
	protected static TRTrackerServerNATChecker		singleton	= new TRTrackerServerNATChecker();
	
	protected static final int THREAD_POOL_SIZE		= 32;
	protected static final int CHECK_QUEUE_LIMIT	= 2048; 
	
	protected static int check_timeout		= TRTrackerServer.DEFAULT_NAT_CHECK_SECS*1000;
	
	protected static TRTrackerServerNATChecker
	getSingleton()
	{
		return( singleton );
	}
	
	protected boolean		enabled;
	protected ThreadPool	thread_pool;
	
	protected List			check_queue		= new ArrayList();
	protected AESemaphore	check_queue_sem	= new AESemaphore("TracerServerNATChecker");
	protected AEMonitor		check_queue_mon	= new AEMonitor( "TRTrackerServerNATChecker:Q" );

	protected AEMonitor 	this_mon 		= new AEMonitor( "TRTrackerServerNATChecker" );

	protected
	TRTrackerServerNATChecker()
	{
		final String	enable_param 	= "Tracker NAT Check Enable";
		final String	timeout_param	= "Tracker NAT Check Timeout";
		
		final String[]	params = { enable_param, timeout_param };
		
		for (int i=0;i<params.length;i++){
			
			COConfigurationManager.addParameterListener(
				params[i],
				new ParameterListener()
				{
					public void 
					parameterChanged(
						String parameter_name)
					{
						checkConfig( enable_param, timeout_param );
					}
				});
		}
		
		checkConfig( enable_param, timeout_param );
	}
	
	protected boolean
	isEnabled()
	{
		return( enabled );
	}
	
	protected void
	checkConfig(
		String	enable_param,
		String	timeout_param )
	{
		try{
			this_mon.enter();
		
			enabled = COConfigurationManager.getBooleanParameter( enable_param );
			
			check_timeout = COConfigurationManager.getIntParameter( timeout_param ) * 1000;
			
			if ( check_timeout < 1000 ){
				
				Debug.out( "NAT check timeout too small - " + check_timeout );
				
				check_timeout	= 1000;
			}
			
			if ( thread_pool == null ){
				
				thread_pool	= new ThreadPool("Tracker NAT Checker", THREAD_POOL_SIZE );
				
				thread_pool.setExecutionLimit( check_timeout );
				
				Thread	dispatcher_thread = 
					new AEThread( "Tracker NAT Checker Dispatcher" )
					{
						public void
						runSupport()
						{
							while(true){
								
								check_queue_sem.reserve();
								
								ThreadPoolTask	task;
								
								try{
									check_queue_mon.enter();
									
									task = (ThreadPoolTask)check_queue.remove(0);
								}finally{
									
									check_queue_mon.exit();
								}
								
								try{
									thread_pool.run( task );
									
								}catch( Throwable e ){
									
									Debug.printStackTrace( e );
								}
							}
						}
					};
					
				dispatcher_thread.setDaemon( true );
				
				dispatcher_thread.start();
				
			}else{
				
				thread_pool.setExecutionLimit( check_timeout );
			}
		}finally{
			
			this_mon.exit();
		}
	}

	protected boolean
	addNATCheckRequest(
		final String								host,
		final int									port,
		final TRTrackerServerNatCheckerListener		listener )
	{		
		if ((!enabled) || thread_pool == null ){
			
			return( false );
		}
		
		try{
			check_queue_mon.enter();
			
			if ( check_queue.size() > CHECK_QUEUE_LIMIT ){
				
				Debug.out( "NAT Check queue size too large, check skipped" );
				
				listener.NATCheckComplete( true );
				
			}else{
				
				check_queue.add(
					new ThreadPoolTask()
					{
						protected	Socket	socket;
						
						public void
						runSupport()
						{
							boolean	ok = false;
														
							try{
								InetSocketAddress address = new InetSocketAddress( host, port );
								
								socket = new Socket();
																
								socket.connect( address, check_timeout );
							
								ok	= true;
								
								socket.close();
								
								socket	= null;
								
							}catch( Throwable e ){
								
							}finally{
																
								listener.NATCheckComplete( ok );
								
								if ( socket != null ){
									
									try{
										socket.close();
										
									}catch( Throwable e ){
									}
								}
							}
						}
						
						public void
						interruptTask()
						{
							if ( socket != null ){
								
								try{
									socket.close();
									
								}catch( Throwable e ){
								}
							}					
						}
					});
				
				check_queue_sem.release();
			}
		}finally{
			
			check_queue_mon.exit();
		}
		
		return( true );
	}
}
