/*
 * PreferencesDialog.java @(#) $Id: NewWeaponInfoDialog.java,v 1.5
 * 2002/07/08 17:19:28 binkley Exp $
 *
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on July 8th, 2002.
 *
 */

package pcgen.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import pcgen.core.SettingsHandler;

/**
 *  ???
 *
 * @author B. K. Oxley (binkley) <binkley@alumni.rice.edu>
 * @version $Revision: 1.1 $
 */


public class PreferencesDialog
{
	private static int tabPlacement;

	public static void setTabPlacement(int placement)
	{
		tabbedPane.setTabPlacement(tabPlacement = placement);
	}

	private static JTabbedPane createTabs()
	{
		JTabbedPane tabbedPane = new JTabbedPane();
		tabPlacement = SettingsHandler.getTabPlacement();

		tabbedPane.add
			("Preferences", new JScrollPane(new JPanel()));
		tabbedPane.add
			("Options", new JScrollPane(new JPanel()));

		return tabbedPane;
	}

	private static JTabbedPane tabbedPane = createTabs();

	public static int show(JFrame frame)
	{
		return JOptionPane.showConfirmDialog
			(frame, tabbedPane, "Preferences",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
	}
}
