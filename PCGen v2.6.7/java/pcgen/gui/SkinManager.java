package pcgen.gui;

/*
 * SkinManager.java
 * Copyright 2001 (C) Jason Buchanan
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
 * Created on January 3, 2002
 */

import javax.swing.UIManager;
import pcgen.core.Globals;

public class SkinManager
{
	public static void applySkin() throws Exception
	{
		try
		{
			com.l2fprod.gui.plaf.skin.SkinLookAndFeel.setSkin(com.l2fprod.gui.plaf.skin.SkinLookAndFeel.loadThemePack(Globals.getSkinLFThemePack()));
			com.l2fprod.gui.plaf.skin.SkinLookAndFeel lnf = new com.l2fprod.gui.plaf.skin.SkinLookAndFeel();
			UIManager.setLookAndFeel(lnf);
			javax.swing.SwingUtilities.updateComponentTreeUI(Globals.getRootFrame());
		}
		catch (Exception e)
		{
			throw e;
		}
	}
}