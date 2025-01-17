/*
 * File    : LoggerChannelImpl.java
 * Created : 28-Dec-2003
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

package org.gudy.azureus2.pluginsimpl.logging;

/**
 * @author parg
 *
 */

import org.gudy.azureus2.plugins.logging.*;
import org.gudy.azureus2.core3.logging.*;

public class 
LoggerChannelImpl 
	implements LoggerChannel
{
	protected String	name;
	
	protected
	LoggerChannelImpl(
		String		_name )
	{
		name	= _name;
	}
		
	public String
	getName()
	{
		return( name );
	}
	
	public void
	log(
		int		log_type,
		String	data )
	{
		data = "[".concat(name).concat("] ").concat(data);
		
		if ( log_type == LT_INFORMATION ){
			
			LGLogger.log( LGLogger.INFORMATION, data );
			
		}else if ( log_type == LT_WARNING ){
				
			LGLogger.log( LGLogger.RECEIVED, data );	// !!!!

		}else if ( log_type == LT_ERROR ){
				
			LGLogger.log( LGLogger.ERROR, data );
		}
	}
	
	public void
	log(
		Throwable 	error )
	{
		LGLogger.log("[".concat(name).concat("]"), error);
	}
	
	public void
	log(
		String		str,
		Throwable 	error )
	{
		LGLogger.log("[".concat(name).concat("] ").concat(str), error);
	}
}
