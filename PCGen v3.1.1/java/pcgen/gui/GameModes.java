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
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
class GameModes extends JMenu
{
	private static String in_gameModes;				// Title for campaign JMenu
	private static String in_stdrdCampaign;		// Title for the standard campaign menu item
	private String in_useMode[] = null;				// Tool tips
	private String in_modeName[] = null;			// Text for menu entry
	private String gameFiles[] = null;				// Directory names --> game mode names
	private JRadioButtonMenuItem gameModeNames[] = null;
	private ButtonGroup gameModeGroup = null;
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private AbstractList campaignMenuItems = new ArrayList();
	private AbstractList campaigns = new ArrayList();

	/** Creates new form Options */
	public GameModes()
	{
		try
		{
			jbInit();
			setText(in_gameModes);
			updateMenu();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception
	{
		gameFiles = getGameFilesList();
		if ((gameFiles == null) || (gameFiles.length == 0))
		{
			return;
		}

		getProperties();
		getMenuInfo();
		gameModeNames = new JRadioButtonMenuItem[gameFiles.length];

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
		for (int i = 0; i < gameFiles.length; ++i)
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
							if (((JMenu)anObj).getText().equals(aName))
							{
								mnu = (JMenu)anObj;
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
					gameModeGroup.add((AbstractButton)mnuLevel.add(gameModeNames[i]));
					Utility.setDescription(gameModeNames[i], in_useMode[i]);
					gameModeNames[i].addActionListener(checkBoxHandler);
				}
			}



			// Add any menu items from campaigns which match this game mode
			Iterator campaignIterator = Globals.getCampaignList().iterator();
			boolean firstCampaignEntry = true;
			for (; campaignIterator.hasNext();)
			{
				final Campaign aCamp = (Campaign)campaignIterator.next();
				if (aCamp.getShowInMenu() && aCamp.getGame().equals(gameFiles[i]))
				{
					if (firstSubMenu == null)
					{
						// This game mode only had a single menu item - no sub menus.
						// So create a sub-menu for it, rename the original menu item
						// to 'standard' and move it to this new sub-menu.
						firstSubMenu = new JMenu(in_modeName[i]);
						mnuLevel.remove(gameModeNames[i]);
						mnuLevel.add(firstSubMenu);
						gameModeNames[i].setText(in_stdrdCampaign);
						firstSubMenu.add(gameModeNames[i]);
					}
					if (firstCampaignEntry)
					{
						firstCampaignEntry = false;
						firstSubMenu.addSeparator();
					}
					JRadioButtonMenuItem campaignMenuItem = new JRadioButtonMenuItem(aCamp.getName());
					gameModeGroup.add((AbstractButton)firstSubMenu.add(campaignMenuItem));
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

	public void updateMenu()
	{
		for (int i = 0; i < gameFiles.length; ++i)
		{
			if (Globals.isInGameMode(gameFiles[i]))
			{
				gameModeNames[i].setSelected(true);
				break;
			}
		}

		if (Globals.getRootFrame() != null)
		{
			((PCGen_Frame1)Globals.getRootFrame()).setGameModeTitle();
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
			String tempGameMode = Constants.DND_MODE;
			int campaignNum = -1;

			if (source == null)
			{
				return;
			}


			((JRadioButtonMenuItem)source).requestFocus();
			for (int i = 0; i < gameModeNames.length; ++i)
			{
				if (source == gameModeNames[i])
				{
					tempGameMode = gameFiles[i];
				}
			}
			campaignNum = campaignMenuItems.indexOf(source);
			if (campaignNum >= 0)
			{
				//Globals.errorPrint("Selecting campaign - " + campaigns.get(campaignNum).toString());
				tempGameMode = ((Campaign) campaigns.get(campaignNum)).getGame();
			}


			if (!Globals.getGameMode().equals(tempGameMode))
			{
				Globals.setGameMode(tempGameMode);
				try
				{
					Globals.loadAttributeNames();
				}
				catch (PersistenceLayerException e)
				{
					Globals.errorPrint("Cannot load attribute names", e);
				}
				updateMenu();
				((MainSource)(Globals.getRootFrame()).getBaseTabbedPane().getComponent(0)).changedGameMode();
			}

			// Now we deal with a campaign selection
			if (campaignNum >= 0)
			{
				ArrayList selectedCampaigns = new ArrayList();
				selectedCampaigns.add(campaigns.get(campaignNum));
				try
				{
					PersistenceManager.loadCampaigns(selectedCampaigns);
				}
				catch (PersistenceLayerException e)
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), Constants.s_APPNAME, JOptionPane.WARNING_MESSAGE);
				}
				Globals.sortCampaigns();
				if (getParent() != null && Globals.displayListsHappy())
				{
					PCGen_Frame1 parent = Globals.getRootFrame();
					parent.enableNew(true);
				}

			}

		}
	}

	//
	// Get a list of all the directories in system/gameModes/ that contain a file named statsandchecks.lst and miscinfo.lst
	//
	private String[] getGameFilesList()
	{
		final String aDirectory = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator;
		return new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				try
				{
					final File d = new File(aFile, aString);
					if (d.isDirectory())
					{
						File f = new File(d, "statsandchecks.lst");
						if (f.exists())
						{
							f = new File(d, "miscinfo.lst");
							return f.exists();
						}
						return false;
					}
				}
				catch (Exception e)
				{
					Globals.errorPrint("GameModes.listGameFiles", e);
				}
				return false;
			}
		});
	}

	private boolean getMenuInfo()
	{
		in_useMode = new String[gameFiles.length];
		in_modeName = new String[gameFiles.length];

		final String aDirectory = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator;
		for (int i = 0; i < gameFiles.length; ++i)
		{
			final String infoPath = aDirectory + gameFiles[i] + File.separator + "miscinfo.lst";
			BufferedReader br = null;
			try
			{
				br = new BufferedReader(new FileReader(infoPath));
				try
				{
					for (; br != null;)
					{
						String aLine = br.readLine();
						if (aLine == null)
						{
							break;
						}
						if (aLine.startsWith("MENUENTRY:"))
						{
							in_modeName[i] = aLine.substring(10);
						}
						else if (aLine.startsWith("MENUTOOLTIP:"))
						{
							in_useMode[i] = aLine.substring(12);
						}
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
			catch (IOException ex)
			{
				Globals.errorPrint("GameModes.getMenuInfo: file not found: " + infoPath, ex);
			}
			finally
			{
				try
				{
					if (br != null)
					{
						br.close();
					}
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}

			//
			// Make sure we have default values if the tag wasn't found
			//
			if (in_modeName[i] == null)
			{
				in_modeName[i] = gameFiles[i];
			}
			if (in_useMode[i] == null)
			{
				in_useMode[i] = "";
			}

		}
		return false;
	}

	/**
	 * Resource bundles
	 */
	private void getProperties()
	{
		ResourceBundle gameModesProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			gameModesProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_gameModes = gameModesProperties.getString("in_gameModes");
			in_stdrdCampaign = gameModesProperties.getString("in_standardCampaign");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			gameModesProperties = null;
		}
	}
}
