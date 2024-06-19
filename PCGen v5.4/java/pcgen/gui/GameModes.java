/*
 * GameModes.java
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
 *
 * August 23, 2002 -- Byngl
 * Major overhaul to check system/gameModes and use properties to build menu
 *
 */
package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
final class GameModes extends JMenu
{
	static final long serialVersionUID = -6751569845505079621L;
	private static String in_stdrdCampaign = PropertyFactory.getString("in_stdrdCampaign");		// Title for the standard campaign menu item
	private String in_useMode[] = null;				// Tool tips
	private String in_modeName[] = null;			// Text for menu entry
	private JRadioButtonMenuItem gameModeNames[] = null;
	private ButtonGroup gameModeGroup = null;
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private AbstractList campaignMenuItems = new ArrayList();
	private AbstractList campaigns = new ArrayList();

	/** Creates new form Options */
	GameModes()
	{
		try
		{
			jbInit();
			setText(PropertyFactory.getString("in_mnuSettingsCampaign"));
			setMnemonic(PropertyFactory.getMnemonic("in_mn_mnuSettingsCampaign"));
			Utility.setDescription(this, PropertyFactory.getString("in_mnuSettingsCampaignTip"));
			updateMenu();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Exception while initing the form", e);
		}
	}

	private void jbInit() throws Exception
	{
		final int gameModeCount = SystemCollections.getUnmodifiableGameModeList().size();

		if (SystemCollections.getUnmodifiableGameModeList().size() == 0)
		{
			return;
		}

		getMenuInfo(gameModeCount);
		gameModeNames = new JRadioButtonMenuItem[gameModeCount];

		//
		// In order for a game mode to show up, there must be a directory in system/gameModes
		// which contains statsandchecks.lst and miscinfo.lst
		//
		// Format of lines in system/gameModes/[blah]/miscinfo.lst:
		//
		// Text displayed on menu.
		// gamemode1  MENUENTRY:D&D|Standard
		// gamemode2  MENUENTRY:D&D|More Options|Even More Options|No More Options
		// gamemode3  MENUENTRY:D&D|~Non-Standard
		//
		// Creates:
		//  D&D --> Standard
		//          More Options --> Even More Options --> No More Options
		//          ------------
		//          Non-Standard
		//
		gameModeGroup = new ButtonGroup();
		for (int i = 0; i < in_modeName.length; ++i)
		{
			final StringTokenizer aTok = new StringTokenizer(in_modeName[i], "|", false);
			JMenu mnuLevel = this;
			JMenu firstSubMenu = null;
			while (aTok.hasMoreTokens())
			{
				String aName = aTok.nextToken();
				//
				// Add a separator if the name starts with '~'
				//
				if (aName.charAt(0) == '~')
				{
					mnuLevel.addSeparator();
					aName = aName.substring(1);
				}

				//
				// If there are more tokens, then add a JMenu with this description
				// unless one already exists.
				//
				if (aTok.hasMoreTokens())
				{
					//
					// Look for a JMenu with the same description
					//
					JMenu mnu = null;
					for (int j = 0; j < mnuLevel.getItemCount(); ++j)
					{
						Object anObj = mnuLevel.getItem(j);
						if (anObj instanceof JMenu)
						{
							if (((JMenu) anObj).getText().equals(aName))
							{
								mnu = (JMenu) anObj;
								break;
							}
						}
					}
					//
					// Not found, add one
					//
					if (mnu == null)
					{
						mnu = new JMenu(aName);
						mnuLevel.add(mnu);
					}
					if (firstSubMenu == null)
					{
						firstSubMenu = mnu;
					}
					mnuLevel = mnu;
				}
				//
				// Reached the end of the list. Add a game mode description here
				//
				else
				{
					gameModeNames[i] = new JRadioButtonMenuItem(aName, false);
					gameModeGroup.add(mnuLevel.add(gameModeNames[i]));
					Utility.setDescription(gameModeNames[i], in_useMode[i]);
					gameModeNames[i].addActionListener(checkBoxHandler);
				}
			}



			// Add any menu items from campaigns which match this game mode
			Iterator campaignIterator = Globals.getCampaignList().iterator();
			boolean firstCampaignEntry = true;
			while (campaignIterator.hasNext())
			{
				final Campaign aCamp = (Campaign) campaignIterator.next();
				if (aCamp.canShowInMenu() && aCamp.getGame().equals(((GameMode) SystemCollections.getUnmodifiableGameModeList().get(i)).getName()))
				{
					if (firstSubMenu == null)
					{
						// This game mode only had a single menu item - no sub menus.
						// So create a sub-menu for it, rename the original menu item
						// to 'standard' and move it to this new sub-menu.
						firstSubMenu = new JMenu(gameModeNames[i].getText());
						mnuLevel.remove(gameModeNames[i]);
						mnuLevel.add(firstSubMenu);
						Utility.setTextAndMnemonic(firstSubMenu, firstSubMenu.getText());
						gameModeNames[i].setText(in_stdrdCampaign);
						firstSubMenu.add(gameModeNames[i]);
					}
					if (firstCampaignEntry)
					{
						firstCampaignEntry = false;
						firstSubMenu.addSeparator();
					}
					JRadioButtonMenuItem campaignMenuItem = new JRadioButtonMenuItem(aCamp.getName());
					gameModeGroup.add(firstSubMenu.add(campaignMenuItem));
					Utility.setDescription(campaignMenuItem, aCamp.getInfoText());
					campaigns.add(aCamp);
					campaignMenuItems.add(campaignMenuItem);
					campaignMenuItem.addActionListener(checkBoxHandler);
				}
			}
		}

		//
		// Look for &'s in the menu text...translate into mnemonic. NOTE "&&" translates to "&"
		//
		for (int i = 0; i < gameModeNames.length; ++i)
		{
			Utility.setTextAndMnemonic(gameModeNames[i], gameModeNames[i].getText());
		}

	}

	private void updateMenu()
	{
		boolean bFound = false;
		for (; ;)
		{
			for (int i = 0; i < in_modeName.length; ++i)
			{
				if (Globals.isInGameMode(((GameMode) SystemCollections.getUnmodifiableGameModeList().get(i)).getName()))
				{
					gameModeNames[i].setSelected(true);
					bFound = true;
					break;
				}
			}

			//
			// If couldn't find game mode, then attempt to set it to DnD mode
			//
			if (bFound)
			{
				break;
			}
			SettingsHandler.setGame(Constants.e3_MODE);
			bFound = true;
		}

		if (Globals.getRootFrame() != null)
		{
			PCGen_Frame1.getInst().setGameModeTitle();
		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			String tempGameMode = Constants.e3_MODE;
			int campaignNum = -1;

			if (source == null)
			{
				return;
			}

			((JRadioButtonMenuItem) source).requestFocus();
			for (int i = 0; i < gameModeNames.length; ++i)
			{
				if (source == gameModeNames[i])
				{
					tempGameMode = ((GameMode) SystemCollections.getUnmodifiableGameModeList().get(i)).getName();
				}
			}
			campaignNum = campaignMenuItems.indexOf(source);
			if (campaignNum >= 0)
			{
				tempGameMode = ((Campaign) campaigns.get(campaignNum)).getGame();
			}

			if (!Globals.isInGameMode(tempGameMode))
			{
				SettingsHandler.setGame(tempGameMode);
				try
				{
					Globals.loadAttributeNames();
				}
				catch (PersistenceLayerException e)
				{
					Logging.errorPrint("Cannot load attribute names", e);
				}
				updateMenu();
				((MainSource) PCGen_Frame1.getBaseTabbedPane().getComponent(0)).changedGameMode();
			}

			// Now we deal with a campaign selection
			if (campaignNum >= 0)
			{
				List selectedCampaigns = new ArrayList();
				selectedCampaigns.add(campaigns.get(campaignNum));
				try
				{
					PCGen_Frame1.getInst().closeAllPCs();
					if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
					{
						GuiFacade.showMessageDialog(Globals.getRootFrame(), PropertyFactory.getString("in_campaignChangeError"), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
						return;
					}

					// Unload the existing campaigns and load our selected campaign
					Globals.emptyLists();
					PersistenceManager.emptyLists();
					PersistenceManager.setChosenCampaignSourcefiles(new ArrayList());
					for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
					{
						Campaign aCamp = (Campaign) it.next();
						aCamp.setIsLoaded(false);
					}
					PersistenceManager.loadCampaigns(selectedCampaigns);
				}
				catch (PersistenceLayerException e)
				{
					GuiFacade.showMessageDialog(null, e.getMessage(), Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
				}
				Globals.sortCampaigns();
				pcgen.gui.PCGen_Frame1.getInst().getMainSource().updateLoadedCampaignsUI();
				if ((getParent() != null) && Globals.displayListsHappy())
				{
					PCGen_Frame1 parent = pcgen.gui.PCGen_Frame1.getInst();
					parent.enableNew(true);
					parent.enableLstEditors(true);
				}
			}

		}
	}

	private void getMenuInfo(int gameModeCount)
	{
		in_useMode = new String[gameModeCount];
		in_modeName = new String[gameModeCount];

		for (int i = 0; i < gameModeCount; ++i)
		{
			final GameMode gameMode = (GameMode) SystemCollections.getUnmodifiableGameModeList().get(i);
			in_modeName[i] = gameMode.getMenuEntry();
			in_useMode[i] = gameMode.getMenuToolTip();
		}
	}

}
