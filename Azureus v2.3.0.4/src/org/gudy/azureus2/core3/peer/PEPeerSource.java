/*
 * Created on 13-Feb-2005
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

package org.gudy.azureus2.core3.peer;

import java.util.*;

import org.gudy.azureus2.core3.config.COConfigurationManager;

/**
 * @author parg
 *
 */

public class 
PEPeerSource 
{
		/**
		 * Class to enumerate the sources of peer connections
		 */
	
	
	// DON'T change these constants as they get serialised!!!!
	// (obviously you can add new networks to them).
	// If you add to them remember to update the configuration item default for
	// "Peer Source Selection Default.<name>" and
	
	// outgoing connections - details how the peer was discovered
	
	public static final String	PS_BT_TRACKER		= "Tracker";
	public static final String	PS_DHT				= "DHT";
	public static final String	PS_OTHER_PEER		= "PeerExchange";
	public static final String	PS_PLUGIN			= "Plugin";
	
		// incoming connections, we don't know much about these apart from the fact that they occurred
	
	public static final String	PS_INCOMING			= "Incoming";
	
	public static final String[]
		PS_SOURCES = {
			PS_BT_TRACKER,
			PS_DHT,
			PS_OTHER_PEER,
			PS_PLUGIN,
			//PS_INCOMING,	remove this as we don't currently enforce
	};

	public static String[]
	getPeerSources()
	{
		List	res = new ArrayList();
		
		for (int i=0;i<PS_SOURCES.length;i++){
			
			if ( COConfigurationManager.getBooleanParameter( "Peer Source Selection Default." + PS_SOURCES[i])){
		
				res.add( PS_SOURCES[i]);
			}
		}
	
		String[]	x = new String[res.size()];
	
		res.toArray( x );
	
		return( x );
	}
}
