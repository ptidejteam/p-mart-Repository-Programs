/*
 * Created on 12-Jan-2005
 * Created by Paul Gardner
 * Copyright (C) 2004, 2005, 2006 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.dht.router.impl;

import org.gudy.azureus2.core3.util.SystemTime;

import com.aelitis.azureus.core.dht.impl.DHTLog;
import com.aelitis.azureus.core.dht.router.DHTRouterContact;
import com.aelitis.azureus.core.dht.router.DHTRouterContactAttachment;

/**
 * @author parg
 *
 */

public class 
DHTRouterContactImpl
	implements DHTRouterContact
{
	private byte[]							node_id;
	private DHTRouterContactAttachment		attachment;
	
	private boolean		has_been_alive;
	private boolean		ping_outstanding;
	private int			fail_count;
	private long		first_alive_time;
	private long		first_fail_or_last_alive_time;
	private long		last_added_time;
	
	protected
	DHTRouterContactImpl(
		byte[]							_node_id,
		DHTRouterContactAttachment		_attachment,
		boolean							_has_been_alive )
	{
		node_id			= _node_id;
		attachment		= _attachment;
		has_been_alive	= _has_been_alive;
		
		attachment.setRouterContact( this );
	}
	
	public byte[]
	getID()
	{
		return(node_id );
	}

	public DHTRouterContactAttachment
	getAttachment()
	{
		return( attachment );
	}
	
	protected void
	setAttachment(
		DHTRouterContactAttachment	_attachment )
	{
		attachment	= _attachment;
	}
	
	public void
	setAlive()
	{
		fail_count							= 0;
		first_fail_or_last_alive_time		= SystemTime.getCurrentTime();
		has_been_alive						= true;
		
		if ( first_alive_time == 0 ){
			
			first_alive_time = first_fail_or_last_alive_time;
		}
	}
	
	public boolean
	hasBeenAlive()
	{
		return( has_been_alive );
	}
	
	public boolean
	isAlive()
	{
		return( has_been_alive && fail_count == 0 );
	}
	
	public boolean
	isFailing()
	{
		return( fail_count > 0 );
	}
	
	public long
	getTimeAlive()
	{
		if ( fail_count > 0 || first_alive_time == 0 ){
			
			return( 0 );
		}
		
		return( SystemTime.getCurrentTime() - first_alive_time );
	}
	
	protected boolean
	setFailed()
	{
		fail_count++;
		
		if ( fail_count == 1 ){
			
			first_fail_or_last_alive_time = SystemTime.getCurrentTime();
		}
		
		return( hasFailed());
	}
	
	protected boolean
	hasFailed()
	{
		if ( has_been_alive ){
			
			return( fail_count >= attachment.getMaxFailForLiveCount());
			
		}else{
			
			return( fail_count >= attachment.getMaxFailForUnknownCount());
		}
	}
	
	protected long
	getFirstFailTime()
	{
		return( fail_count==0?0:first_fail_or_last_alive_time );
	}
	
	protected long
	getLastAliveTime()
	{
		return( fail_count==0?first_fail_or_last_alive_time:0 );
	}
	
	protected long
	getFirstFailOrLastAliveTime()
	{
		return( first_fail_or_last_alive_time );
	}
	
	protected long
	getFirstAliveTime()
	{
		return( first_alive_time );
	}
	
	protected long
	getLastAddedTime()
	{
		return( last_added_time );
	}
	
	protected void
	setLastAddedTime(
		long	l )
	{
		last_added_time	= l;
	}
	
	protected void
	setPingOutstanding(
		boolean	b )
	{
		ping_outstanding = b;
	}
	
	protected boolean
	getPingOutstanding()
	{
		return( ping_outstanding );
	}
	
	public String
	getString()
	{
		return( DHTLog.getString2(node_id) + "[hba=" + (has_been_alive?"Y":"N" ) + 
				",bad=" + fail_count +
				",OK=" + getTimeAlive() + "]");
	}
}
