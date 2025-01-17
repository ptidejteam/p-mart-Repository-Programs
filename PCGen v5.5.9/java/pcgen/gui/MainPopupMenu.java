/**
 * MainPopupMenu.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:08 $
 *
 **/

package pcgen.gui;

import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.Utility;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 1.1 $
 */

final class MainPopupMenu extends JPopupMenu
{
	private JMenuItem pleaseLoadItem;
	JMenuItem newItem;

	public MainPopupMenu(FrameActionListener frameActionListener)
	{
		pleaseLoadItem = Utility.createMenuItem("Please load campaigns", null, "mainPopupMenu.pleaseLoad", (char) 0, null, "You must load one or more campaigns before creating new characters", null, false);
		newItem = Utility.createMenuItem("New", frameActionListener.newPopupActionListener, "mainPopupMenu.new", 'N', null, "Create a new character", "New16.gif", true);
	}

	public void show(Component invoker, int x, int y)
	{
		if (Globals.displayListsHappy())
		{
			remove(pleaseLoadItem);
			add(newItem);
			super.show(invoker, x, y);
		}

		else if (!SettingsHandler.isExpertGUI())
		{
			remove(newItem);
			add(pleaseLoadItem);
			super.show(invoker, x, y);
		}
	}
}
