/*
 * File    : LGLogger.java
 * Created : 5 Oct. 2003
 * By      : Parg 
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
 
package org.gudy.azureus2.core3.logging;

/**
 * @author stuff
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

import org.gudy.azureus2.core.Logger;	// TODO: sort this

public class 
LGLogger 
{
	public static final int INFORMATION 	= 0;
	public static final int RECEIVED 		= 1;
	public static final int SENT 			= 2;
	public static final int ERROR 			= 3;

	public static void 
	log(
		int componentId, 
		int event, 
		int color, 
		String text ) 
	{
		Logger.getLogger().log(componentId,event,color,text );
	}	
	
	public static void 
	log(
		int 		componentId, 
		int 		event, 
		String		text, 
		Throwable	e )
	{
		Logger.getLogger().log(componentId,event,ERROR,text + " ('" + e.toString() + "')" );
	}
}
