/*
 * Created on 12-Jun-2005
 * Created by Paul Gardner
 * Copyright (C) 2005 Aelitis, All Rights Reserved.
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

package com.aelitis.azureus.core.dht.transport.udp.impl.packethandler;

public class 
DHTUDPPacketHandlerException
	extends Exception
{
	public
	DHTUDPPacketHandlerException(
		String		str )
	{
		super( str );
	}
	
	public
	DHTUDPPacketHandlerException(
		Throwable	cause )
	{
		super( cause.getMessage()==null?cause.toString():cause.getMessage());
	}
	
	public
	DHTUDPPacketHandlerException(
		String		str,
		Throwable	cause )
	{
		super( str, cause );
	}
}
