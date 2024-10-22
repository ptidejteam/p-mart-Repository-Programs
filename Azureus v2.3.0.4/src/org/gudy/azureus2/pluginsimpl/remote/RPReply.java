/*
 * File    : RPReply.java
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

package org.gudy.azureus2.pluginsimpl.remote;

import java.io.Serializable;

/**
 * @author parg
 *
 */

import java.util.*;

public class 
RPReply 
	implements Serializable
{
	public Object	response;
	
	transient protected Map		properties	= new HashMap();
	
	public
	RPReply(
		Object		_response )
	{
		response	= _response;
	}
	
	public Object
	getResponse()
	
		throws RPException
	{
		if ( response instanceof RPException ){
			
			throw((RPException)response);
			
		}else if ( response instanceof Throwable ){
			
			throw( new RPException("RPReply: exception occurred", (Throwable)response ));
		}
		
		return( response );
	}
	
	public void
	setProperty(
		String		name,
		String		value )
	{
		properties.put( name, value );
	}
	
	public Map
	getProperties()
	{
		return( properties );
	}
}
