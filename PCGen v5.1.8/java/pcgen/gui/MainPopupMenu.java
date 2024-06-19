/**
 * MainPopupMenu.java
 * Copyright 2002 (C) ???
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:10:54 $
 *
 **/

package pcgen.gui;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import pcgen.gui.utils.Utility;

/**
 * @author ???
 * @version $Revision: 1.1 $
 */

final class MainPopupMenu extends JPopupMenu
{
	JMenuItem newItem;

	public MainPopupMenu(FrameActionListener frameActionListener)
	{
		add(newItem = Utility.createMenuItem("New", frameActionListener.newPopupActionListener, "mainPopupMenu.new", 'N', null, "Create a new character", "New16.gif", true));
	}
}
