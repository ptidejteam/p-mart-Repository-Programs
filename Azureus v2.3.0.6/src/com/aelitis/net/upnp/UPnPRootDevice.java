/*
 * Created on 15-Jun-2004
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

package com.aelitis.net.upnp;

import java.net.URL;
import java.net.InetAddress;


/**
 * @author parg
 *
 */

public interface 
UPnPRootDevice
{
	public UPnP
	getUPnP();
	
	public URL
	getLocation();
	
	public InetAddress
	getLocalAddress();
	
	public String
	getInfo();
	
	public UPnPDevice
	getDevice();
	
	public boolean
	isDestroyed();
	
	public void
	addListener(
		UPnPRootDeviceListener	l );
	
	public void
	removeListener(
		UPnPRootDeviceListener	l );
}
