/*
 * Created on 19-Apr-2004
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

package org.gudy.azureus2.plugins.ui.menus;

/** Menu item access for the UI.
 *
 * @author parg (Original ContextMenuItem code)
 * @author TuxPaper (Generic-izing, commenting)
 */
public interface MenuItem 
{
  /** Retrieve the resource key ("name") of this menu item
   *
   * @return resource key for this menu
   */
	public String
	getResourceKey();
	
	/** Retrieve the parent menu's resource key.
	 *
	 * @return parent menu's resource key, or null if no parent
	 *
	public Menu
	getParent();
   */
	
	/** Add a Selection Listener for this menu item
	 *
	 * @param l listener to be notified when user has selected menu item.
	 */
	public void
	addListener(
		MenuItemListener	l );
	
	/**
   * Remove a Selection Listener from this menu item
   *
   * @param l listener to remove
   */
	public void
	removeListener(
		MenuItemListener	l );
}
