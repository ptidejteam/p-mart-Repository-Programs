/*
 * File    : ListenerManager.java
 * Created : 15-Jan-2004
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

package org.gudy.azureus2.core3.util;

/**
 * @author parg
 *
 */

/**
 * This class exists to support the invocation of listeners while *not* synchronized.
 * This is important as in general it is a bad idea to invoke an "external" component
 * whilst holding a lock on something as unexpected deadlocks can result.
 * It has been introduced to reduce the likelyhood of such deadlocks
 */

import java.util.*;


public class 
ListenerManager
{
	public static ListenerManager
	createManager(
		String							name,
		ListenerManagerDispatcher		target )
	{
		return( new ListenerManager( name, target, false ));
	}
	
	public static ListenerManager
	createAsyncManager(
		String							name,
		ListenerManagerDispatcher		target )
	{
		return( new ListenerManager( name, target, true ));
	}
	
	
	protected String	name;
	
	protected ListenerManagerDispatcher					target;
	protected ListenerManagerDispatcherWithException	target_with_exception;
	
	protected boolean	async;
	protected Thread	async_thread;
	
	protected List		listeners		= new ArrayList();
	
	protected List			dispatch_queue;
	protected AESemaphore	dispatch_sem;
	
	protected
	ListenerManager(
		String							_name,
		ListenerManagerDispatcher		_target,
		boolean							_async )
	{
		name	= _name;
		target	= _target;
		async	= _async;
		
		if ( target instanceof ListenerManagerDispatcherWithException ){
			
			target_with_exception = (ListenerManagerDispatcherWithException)target;
		}
		
		if ( async ){
			
			dispatch_sem	= new AESemaphore("ListenerManager::"+name);
			dispatch_queue 	= new LinkedList();
			
			if ( target_with_exception != null ){
				
				throw( new RuntimeException( "Can't have an async manager with exceptions!"));
			}
		}
	}
	
	public void
	addListener(
		Object		listener )
	{
		synchronized( this ){
			
			ArrayList	new_listeners	= new ArrayList( listeners );
			
			new_listeners.add( listener );
			
			listeners	= new_listeners;
			
			if ( async && async_thread == null ){
				
				async_thread = new AEThread( name )
					{
						public void
						runSupport()
						{
							dispatchLoop();
						}
					};
					
				async_thread.setDaemon( true );
				
				async_thread.start();
			}
		}
	}
	
	public void
	removeListener(
		Object		listener )
	{
		synchronized( this ){
			
			ArrayList	new_listeners = new ArrayList( listeners );
			
			new_listeners.remove( listener );
			
			listeners	= new_listeners;
			
			if ( async && listeners.size() == 0 ){
				
				async_thread = null;
				
					// try and wake up the thread so it kills itself
				
				dispatch_sem.release();
			}
		}
	}
	
	public void
	clear()
	{
		synchronized( this ){
									
			listeners	= new ArrayList();
			
			if ( async ){
				
				async_thread = null;
				
					// try and wake up the thread so it kills itself
				
				dispatch_sem.release();
			}
		}
	}
	
	public List
	getListenersCopy()
	{
			// we can just return the listeners as we copy on update
				
		return( listeners );
	}
	
	public void
	dispatch(
		int		type,
		Object	value )
	{
		dispatch( type, value, false );
	}
	
	public void
	dispatch(
		int			type,
		Object		value,
		boolean		blocking )
	{
		if ( async ){
			
			AESemaphore	sem = null;
			
			if ( blocking ){
				
				sem = new AESemaphore( "ListenerManager:blocker");
			}
			
			synchronized( this ){
				
					// if there's nobody listening then no point in queueing 
				
				if ( listeners.size() == 0 ){
						
					return;
				}
				
					// listeners are "copy on write" updated, hence we grab a reference to the 
					// current listeners here. Any subsequent change won't affect our listeners
												
				dispatch_queue.add(new Object[]{listeners, new Integer(type), value, sem });
			}
			
			dispatch_sem.release();
			
			if ( sem != null ){
				
				sem.reserve();
			}
		}else{
			
			if ( target_with_exception != null ){
				
				throw( new RuntimeException( "call dispatchWithException, not dispatch"));
			}
			
			List	listeners_ref;
			
			synchronized( this ){
				
				listeners_ref = listeners;				
			}	
			
			try{
				dispatchInternal( listeners_ref, type, value );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}
	}	
	
	public void
	dispatchWithException(
		int		type,
		Object	value )
	
		throws Throwable
	{
		List	listeners_ref;
		
		synchronized( this ){
			
			listeners_ref = listeners;			
		}
		
		dispatchInternal( listeners_ref, type, value );
	}
	
	public void
	dispatch(
		Object	listener,
		int		type,
		Object	value )
	{
		dispatch( listener, type, value, false );
	}
	
	public void
	dispatch(
		Object	listener,
		int		type,
		Object	value,
		boolean	blocking )
	{
		if ( async ){
			
			AESemaphore	sem = null;
			
			if ( blocking ){
				
				sem = new AESemaphore( "ListenerManager:blocker");
			}
	
			synchronized( this ){
								
					// 5 entries to denote single listener
				
				dispatch_queue.add(new Object[]{ listener, new Integer(type), value, sem, null });
				
				if ( async_thread == null ){
					
					async_thread = new AEThread( name )
						{
							public void
							runSupport()
							{
								dispatchLoop();
							}
						};
						
					async_thread.setDaemon( true );
					
					async_thread.start();
				}
			}
			
			dispatch_sem.release();
	
			if ( sem != null ){
				
				sem.reserve();
			}
		}else{
			
			if ( target_with_exception != null ){
				
				throw( new RuntimeException( "call dispatchWithException, not dispatch"));
			}
			
			try{
				target.dispatch( listener, type, value );
				
			}catch( Throwable e ){
				
				Debug.printStackTrace( e );
			}
		}
	}

	protected void
	dispatchInternal(
		List		listeners_ref,
		int			type,
		Object		value )
	
		throws Throwable
	{		
		for (int i=0;i<listeners_ref.size();i++){
		
			
			if ( target_with_exception != null ){
					
				// System.out.println( name + ":dispatchWithException" );
				
					// DON'T catch and handle exceptions here are they are permitted to
					// occur!
				
				target_with_exception.dispatchWithException( listeners_ref.get(i), type, value );
					
			}else{
			
				try{
						// System.out.println( name + ":dispatch" );
						
					target.dispatch( listeners_ref.get(i), type, value );
					
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
				}
			}
		}
	}
	
	protected void
	dispatchInternal(
		Object		listener,
		int			type,
		Object		value )
	
		throws Throwable
	{		
		if ( target_with_exception != null ){
				
				// System.out.println( name + ":dispatchWithException" );
				
				// DON'T catch and handle exceptions here are they are permitted to
				// occur!

			target_with_exception.dispatchWithException( listener, type, value );
				
		}else{
			try{
				
					// System.out.println( name + ":dispatch" );
				
				target.dispatch( listener, type, value );
			
			}catch( Throwable e ){
					
				Debug.printStackTrace( e );
			}
		}
	}
	
	public void
	dispatchLoop()
	{
		// System.out.println( "ListenerManager::dispatch thread '" + Thread.currentThread() + "' starts");
		
		while(true){
			
			dispatch_sem.reserve();
			
			Object[] data = null;
			
			synchronized( this ){
				
				if ( async_thread != Thread.currentThread()){
					
						// we've been asked to close. this sem reservation must be
						// "returned" to the pool in case it represents a valid  entry
						// to be picked up by another thread
					
					dispatch_sem.release();
					
					break;
				}
				
				if ( dispatch_queue.size() > 0 ){
					
					data = (Object[])dispatch_queue.remove(0);
				}
			}
			
			if ( data != null ){
			
				try{						
					if ( data.length == 4 ){
					
						dispatchInternal((List)data[0], ((Integer)data[1]).intValue(), data[2] );
						
					}else{
						
						dispatchInternal( data[0], ((Integer)data[1]).intValue(), data[2] );
					}
					
				}catch( Throwable e ){
					
					Debug.printStackTrace( e );
					
				}finally{
					
					if ( data[3] != null ){
						
						((AESemaphore)data[3]).release();
					}
				}
			}
		}
		
		// System.out.println( "ListenerManager::dispatch thread '" + Thread.currentThread() + "' ends");
	}
	
	public static void
	dispatchWithTimeout(
		List							_listeners,
		final ListenerManagerDispatcher	_dispatcher,
		long							_timeout )
	{
		final List	listeners = new ArrayList( _listeners );
		
		final boolean[]	completed = new boolean[listeners.size()];
		
		final AESemaphore	timeout_sem = new AESemaphore("ListenerManager:dwt:timeout");
		
		for (int i=0;i<listeners.size();i++){
			
			final int f_i	= i;
						
			new AEThread( "ListenerManager:dwt:dispatcher", true ){
				public void
				runSupport()
				{
					try{
						_dispatcher.dispatch( listeners.get(f_i), -1, null );
						
					}catch( Throwable e ){
						
						Debug.printStackTrace(e);
						
					}finally{
						
						completed[f_i]	= true;
						
						timeout_sem.release();
					}
				}
			}.start();
		}
		
		boolean	timeout_occurred = false;
		
		for (int i=0;i<listeners.size() ;i++){
			
			if ( _timeout <= 0 ){
				
				timeout_occurred	= true;
				
				break;
			}
			
			long start = SystemTime.getCurrentTime();
			
			if ( !timeout_sem.reserve( _timeout )){
				
				timeout_occurred	= true;
				
				break;
			}
			
			long end = SystemTime.getCurrentTime();

			if ( end > start ){

				_timeout = _timeout - ( end - start );
			}
		}
		
		if ( timeout_occurred ){
			
			String	str = "";
			
			for (int i=0;i<completed.length;i++){
			
				if ( !completed[i] ){
					
					str += (str.length()==0?"":",") + listeners.get(i);
				}
			}
			
			if ( str.length() > 0 ){
				
				Debug.out( "Listener dispatch timeout: failed = " + str );
			}
		}
	}
}

