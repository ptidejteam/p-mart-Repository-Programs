/*
 * Options.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 24, 2001, 10:06 PM
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JMenu;
import pcgen.core.Globals;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
final class Options extends JMenu // extends Preferences
{
	//
	// Used to create the entries for the "set all stats to" menu
	private PrefsMenuListener prefsMenuHandler = new PrefsMenuListener();

	/** Creates new form Options */
	public Options()
	{
		setText(PropertyFactory.getString("in_mnuSettings"));
		setMnemonic(PropertyFactory.getMnemonic("in_mn_mnuSettings"));
		Utility.setDescription(this, PropertyFactory.getString("in_mnuSettingsTip"));

		IconUtilitities.maybeSetIcon(this, "Preferences16.gif");

		try
		{
			jbInit();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error while initing form", e);
		}
	}

	private void addCampMenu(JMenu parent)
	{
		//Modes menu
		GameModes modesMenu = new GameModes();
		parent.add(modesMenu);

	}

	private void addPreferencesMenu(JMenu parent)
	{
		parent.add(Utility.createMenuItem("mnuSettingsPreferences", prefsMenuHandler, null, null, "Preferences16.gif", true));
	}

	private void jbInit() throws Exception
	{
		addCampMenu(this);	// campaigns
		addPreferencesMenu(this); // Preferences dialog
	}

	/**
	 * Show the preferences pane.
	 */
	private final class PrefsMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			JLabel statusBar = PCGen_Frame1.getStatusBar();
			String oldStatus = statusBar.getText();
			statusBar.setText("Preferences...");
			statusBar.revalidate();

			PreferencesDialog.show(Globals.getRootFrame());

			statusBar.setText(oldStatus);
		}
	}

}
