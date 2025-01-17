/*
 * Created on 22 Jun 2006
 * Created by Paul Gardner
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.networkmanager.impl;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.gudy.azureus2.core3.util.AEDiagnostics;
import org.gudy.azureus2.core3.util.Debug;

import com.aelitis.azureus.core.networkmanager.EventWaiter;
import com.aelitis.azureus.core.networkmanager.Transport;

public abstract class 
TransportImpl 
	implements Transport
{
	private TransportHelperFilter filter;

	private static final TransportStats stats = AEDiagnostics.TRACE_TCP_TRANSPORT_STATS ? new TransportStats() : null;  

	private ByteBuffer data_already_read = null;

	private volatile EventWaiter read_waiter;
	private volatile EventWaiter write_waiter;
	private volatile boolean is_ready_for_write = false;
	private volatile boolean is_ready_for_read = false;
	private Throwable write_select_failure = null;
	private Throwable read_select_failure = null;

	protected
	TransportImpl()
	{
	}
	
	protected void
	setFilter(
		TransportHelperFilter	_filter )
	{
		filter	= _filter;
	}
	
	protected TransportHelperFilter
	getFilter()
	{
		return( filter );
	}
	
	public void 
	setAlreadyRead( 
		ByteBuffer bytes_already_read )
	{
		if ( data_already_read != null ){
			
			Debug.out( "push back already performed" );
		}
		
		if ( bytes_already_read != null && bytes_already_read.hasRemaining()){

			data_already_read	= bytes_already_read;
			
			is_ready_for_read = true;
		}
	}

	  
	public String 
	getEncryption()
	{ 
		return( filter==null?"":filter.getName()); 
	}
	
	  /**
	   * Is the transport ready to write,
	   * i.e. will a write request result in >0 bytes written.
	   * @return true if the transport is write ready, false if not yet ready
	   */
	
	public boolean 
	isReadyForWrite( 
		EventWaiter waiter ) 
	{
		write_waiter = waiter;
		
		return is_ready_for_write;  
	}
	  
	protected boolean
	readyForWrite(
		boolean	ready )
	{
		if ( ready ){
		  	boolean	progress = !is_ready_for_write;
	        is_ready_for_write = true;
	        EventWaiter ww = write_waiter;
	        if ( ww != null ){
	        	ww.eventOccurred();
	        }
	        return progress;
		}else{
			is_ready_for_write = false;
			return( false );
		}
	}
	  
	protected void
	writeFailed(
		Throwable	msg )
	{
		msg.fillInStackTrace();
		
		write_select_failure = msg;
	
		is_ready_for_write = true;  //set to true so that the next write attempt will throw an exception
	}
	
	  /**
	   * Is the transport ready to read,
	   * i.e. will a read request result in >0 bytes read.
	   * @return true if the transport is read ready, false if not yet ready
	   */
	
	public boolean 
	isReadyForRead( 
		EventWaiter waiter ) 
	{
		read_waiter = waiter;
	
		return is_ready_for_read || data_already_read != null || ( filter != null && filter.hasBufferedRead());  
	}
	    
	protected boolean
	readyForRead(
		boolean	ready )
	{
		if ( ready ){
		   	boolean	progress = !is_ready_for_read;
	        is_ready_for_read = true;
	        EventWaiter rw = read_waiter;
	        if ( rw != null ){
	        	rw.eventOccurred();
	        }
	        return progress;
		}else{
			is_ready_for_read = false;
			return( false );
		}
	}
	  
	protected void
	readFailed(
		Throwable	msg )
	{
		msg.fillInStackTrace();	// msg picked up on another thread - make sure trace is available

		read_select_failure = msg;
	
		is_ready_for_read = true;  //set to true so that the next read attempt will throw an exception
	}
	
	  /**
	   * Write data to the transport from the given buffers.
	   * NOTE: Works like GatheringByteChannel.
	   * @param buffers from which bytes are to be retrieved
	   * @param array_offset offset within the buffer array of the first buffer from which bytes are to be retrieved
	   * @param length maximum number of buffers to be accessed
	   * @return number of bytes written
	   * @throws IOException on write error
	   */
	
	public long 
	write( 
		ByteBuffer[] buffers, 
		int array_offset, 
		int length ) 
	
		throws IOException 
	{
	  	if ( write_select_failure != null ){
	  			  		
	  		throw new IOException( "write_select_failure: " + write_select_failure.getMessage() );
	  	}
	    
	  	if ( filter == null )  return 0;
	  	
	  	long written = filter.write( buffers, array_offset, length );

	  	if ( stats != null )  stats.bytesWritten( (int)written );  //TODO
	       
	  	if ( written < 1 )  requestWriteSelect();
	      
	  	return written;
	}
	  
	  /**
	   * Read data from the transport into the given buffers.
	   * NOTE: Works like ScatteringByteChannel.
	   * @param buffers into which bytes are to be placed
	   * @param array_offset offset within the buffer array of the first buffer into which bytes are to be placed
	   * @param length maximum number of buffers to be accessed
	   * @return number of bytes read
	   * @throws IOException on read error
	   */
	
	public long 
	read( 
		ByteBuffer[] buffers, 
		int array_offset, 
		int length ) 
	
		throws IOException 
	{
		if( read_select_failure != null ) {
	    			
			throw new IOException( "read_select_failure: " + read_select_failure.getMessage() );
	    }  
	    
	    	//insert already-read data into the front of the stream
		
		if ( data_already_read != null ) {
	    
			int inserted = 0;
	      
			for( int i = array_offset; i < (array_offset + length); i++ ) {
				
				ByteBuffer bb = buffers[ i ];
	        
				int orig_limit = data_already_read.limit();
	        
				if( data_already_read.remaining() > bb.remaining() ) {
					
					data_already_read.limit( data_already_read.position() + bb.remaining() ); 
				}
	        
				inserted += data_already_read.remaining();
	        
				bb.put( data_already_read );
	        
				data_already_read.limit( orig_limit );
	        
				if( !data_already_read.hasRemaining() ) {
					
					data_already_read = null;
					
					break;
				}
			}
	      
			if( !buffers[ array_offset + length - 1 ].hasRemaining() ) {  //the last buffer has nothing left to read into normally
	        
				return inserted;  //so return right away, skipping socket read
			}      
	    }
	 
	    long bytes_read = filter.read( buffers, array_offset, length );

	    if( stats != null )  stats.bytesRead( (int)bytes_read );  //TODO
	    
	    if( bytes_read == 0 ){
	    	
	    	requestReadSelect();
	    }
	    
	    return bytes_read;
	}
	
	private void 
	requestWriteSelect() 
	{
		is_ready_for_write = false;
		
		if( filter != null ){
			
		   	filter.getHelper().resumeWriteSelects();
		}
	}
		  
		  
	private void 
	requestReadSelect() 
	{
		is_ready_for_read = false;
		
		if ( filter != null ){
			
		    filter.getHelper().resumeReadSelects();
		}
	} 
	
	public void
	connectedInbound()
	{	   
		registerSelectHandling();
	}
	
	protected void
	connectedOutbound()
	{
		registerSelectHandling();
	}
	
	private void 
	registerSelectHandling() 
	{
		TransportHelperFilter	filter = getFilter();
		
		if( filter == null ) {
			Debug.out( "ERROR: registerSelectHandling():: filter == null" );
			return;
		}
	
		TransportHelper	helper = filter.getHelper();
		
		//read selection
		
		helper.registerForReadSelects(
			new TransportHelper.selectListener()
			{
			   	public boolean 
		    	selectSuccess(
		    		TransportHelper	helper, 
		    		Object 			attachment )
			   	{
			   		return( readyForRead( true ));
			   	}

		        public void 
		        selectFailure(
		        	TransportHelper	helper,
		        	Object 			attachment, 
		        	Throwable 		msg)
		        {
		        	readFailed( msg );
		        }
			},
			null );

		helper.registerForWriteSelects(
				new TransportHelper.selectListener()
				{
				   	public boolean 
			    	selectSuccess(
			    		TransportHelper	helper, 
			    		Object 			attachment )
				   	{
				   		return( readyForWrite( true ));
				   	}

			        public void 
			        selectFailure(
			        	TransportHelper	helper,
			        	Object 			attachment, 
			        	Throwable 		msg)
			        {
			        	writeFailed( msg );
			        }
				},
				null );
	}
}
