/*
 * Created on 18-Sep-2004
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

package org.gudy.azureus2.core3.util;

/**
 * @author parg
 *
 */
public class 
AESemaphore 
	extends AEMonSem
{
	protected int		dont_wait	= 0;

	protected int		total_reserve	= 0;
	protected int		total_release	= 0;

	protected boolean	released_forever	= false;

	protected Thread	latest_waiter;
	
	public
	AESemaphore(
		String		_name )
	{
		this( _name, 0 );
	}

	public
	AESemaphore(
		String		_name,
		int			count )
	{
		super( _name, false );
		
		dont_wait		= count;
		total_release	= count;
	}

	public void
	reserve()
	{
		reserve(0);
	}
	
	public boolean
	reserve(
		long	millis )
	{
		return( reserveSupport( millis, 1 ) == 1 );
	}
	
	public int
	reserveSet(
		int	max_to_reserve )
	{
		return( reserveSupport( 0, max_to_reserve));
	}
	
	protected int
	reserveSupport(
		long	millis,
		int		max_to_reserve )
	{
		if ( DEBUG ){
			
			super.debugEntry();
		}
		
		synchronized(this){

			entry_count++;
			
			//System.out.println( name + "::reserve");
			
			if ( released_forever ){

				return(1);
			}

			if ( dont_wait == 0 ){

				try{
					waiting++;

					latest_waiter	= Thread.currentThread();
					
					if ( waiting > 1 ){
						
						// System.out.println( "AESemaphore: " + name + " contended" );
					}

					if ( millis == 0 ){
						
						wait();
						
					}else{
						
						wait(millis);
					}
					
					if ( total_reserve == total_release ){
							
						waiting--;
						
						return( 0 );
					}
						
					total_reserve++;

					return( 1 );

				}catch( Throwable e ){

					waiting--;

					Debug.out( "**** semaphore operation interrupted ****" );

					throw( new RuntimeException("Semaphore: operation interrupted" ));
					
				}finally{
					
					latest_waiter = null;
				}
			}else{
				int	num_to_get = max_to_reserve>dont_wait?dont_wait:max_to_reserve;
				
				dont_wait -= num_to_get;

				total_reserve += num_to_get;
				
				return( num_to_get );
			}
		}
	}
	
	public void
	release()
	{
		try{
			synchronized(this){
				//System.out.println( name + "::release");
	
				total_release++;
	
				if ( waiting != 0 ){
	
					waiting--;
	
					notify();
	
				}else{
					dont_wait++;
				}
			}
		}finally{
			
			if ( DEBUG ){
				
				debugExit();
			}
		}
	}

	public void
	releaseAllWaiters()
	{
		int	x	= waiting;

		for ( int i=0;i<x;i++ ){

			release();
		}
	}

	public void
	releaseForever()
	{
		synchronized(this){

			releaseAllWaiters();

			released_forever	= true;
		}
	}
	
	public int
	getValue()
	{
		return( dont_wait - waiting );
	}
}
