/*
 * Created on Feb 9, 2005
 * Created by Alon Rohter
 * Copyright (C) 2004-2005 Aelitis, All Rights Reserved.
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

package org.gudy.azureus2.plugins.network;

import java.net.InetSocketAddress;

import org.gudy.azureus2.plugins.messaging.*;


/**
 * Manages connections.
 */
public interface 
ConnectionManager 
{
	public static final int	NAT_UNKNOWN			= 0;
	public static final int	NAT_OK				= 1;
	public static final int	NAT_PROBABLY_OK		= 2;
	public static final int	NAT_BAD				= 3;

  /**
   * Create a new unconnected remote connection (for outbound-initiated connections).
   * @param remote_address to connect to
   * @return not yet established connection
   */
  public Connection createConnection( InetSocketAddress remote_address, MessageStreamEncoder encoder, MessageStreamDecoder decoder );
  
  	/** 
  	 * Returns the current view on whether or not we are inwardly connectable via our listener port
  	 * @return
  	 */
  
  public int
  getNATStatus();
  
}
