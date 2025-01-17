/*
 * Created on 11-Jan-2005
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

package com.aelitis.azureus.core.dht.router;

/**
 * @author parg
 *
 */

public interface 
DHTRouterContact 
{
	public byte[]
	getID();
	
	public DHTRouterContactAttachment
	getAttachment();
	
		/**
		 * indicates whether or not a message has been received from, or an operation has
		 * successfully been made to, the contact.
		 * @return
		 */
	
	public boolean
	hasBeenAlive();
	
		/**
		 * Whether or not the contact has failed once or more since last alive (if ever)
		 * @return
		 */
	
	public boolean
	isFailing();
	
		/**
		 * Whether or not the contact's last interaction was successful
		 * @return
		 */
	
	public boolean
	isAlive();
	
		/**
		 * time between first establishing the contact was alive and now, assuming that its
		 * not failing. 0 -> failing
		 * @return
		 */
	
	public long
	getTimeAlive();
	
	public String
	getString();
}
