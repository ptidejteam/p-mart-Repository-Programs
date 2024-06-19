/*
 * CharacterInfo.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:48 $
 *
 */

package pcgen.gui;

// This snippet creates a new tabbed panel

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentListener;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.util.PropertyFactory;

/**
 * <code>CharacterInfo</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

final class CharacterInfo extends JPanel
{
	private JTabbedPane characterInfoTabbedPane = new JTabbedPane();

	private InfoAbilities infoAbilities;
	private InfoClasses infoClasses = new InfoClasses();
	private InfoDescription infoDesc = new InfoDescription();
	private InfoDomain infoDomains = new InfoDomain();
	private InfoFeats infoFeats = new InfoFeats();
	private InfoInventory infoInventory = new InfoInventory();
	//private InfoMisc infoMisc = new InfoMisc();
/* Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
	private InfoPreview infoPreview = new InfoPreview();*/
	private InfoRace infoRace = new InfoRace();
	private InfoSpells infoSpells = new InfoSpells();
	private InfoSkills infoSkills = new InfoSkills();
	private InfoSummary infoSummary = new InfoSummary();
	//private InfoNotes infoNotes = new InfoNotes();

	private BorderLayout borderLayout1 = new BorderLayout();

	CharacterInfo()
	{
		setName("");
		try
		{
			jbInit();
		}
		catch (Exception e) //This is what jbInit actually throws...
		{
			Globals.errorPrint(PropertyFactory.getString("in_CIerrorMess"), e);
		}
	}

	private void addTab(Component p, int tabNo)
	{
		pcgen.core.GameMode game = SettingsHandler.getGame();
		if (game.getTabShown(tabNo))
		{
			characterInfoTabbedPane.add(p, game.getTabName(tabNo));
		}
	}

	private void jbInit() throws Exception
	{
		this.setLayout(borderLayout1);
		characterInfoTabbedPane.setPreferredSize(new Dimension(550, 350));
		this.setMinimumSize(new Dimension(550, 350));
		this.setPreferredSize(new Dimension(550, 350));
		this.add(characterInfoTabbedPane, BorderLayout.CENTER);

		characterInfoTabbedPane.setTabPlacement(SettingsHandler.getChaTabPlacement());

		addTab(infoSummary, Constants.TAB_SUMMARY);
		addTab(infoRace, Constants.TAB_RACES);
		if (SettingsHandler.isAbilitiesShownAsATab())
		{
			infoAbilities = new InfoAbilities();
			addTab(infoAbilities, Constants.TAB_ABILITIES);
		}

		addTab(infoClasses, Constants.TAB_CLASSES);
		addTab(infoSkills, Constants.TAB_SKILLS);
		addTab(infoFeats, Constants.TAB_FEATS);
		addTab(infoDomains, Constants.TAB_DOMAINS);
		addTab(infoSpells, Constants.TAB_SPELLS);
		addTab(infoInventory, Constants.TAB_INVENTORY);
		addTab(infoDesc, Constants.TAB_DESCRIPTION);

		/*
		 * Mynex wants these options eliminated, so I'll comment them out
		 * (in the event they ever decide to return)
		 */
//  		if (SettingsHandler.isPreviewTabShown())
//  		{
//  			characterInfoTabbedPane.add(infoPreview, PropertyFactory.getString("in_preview"));
//  		}

		// since our filter icon changes if # of selected filters > 0
		// we register component listeners
		final ComponentListener componentListener = PToolBar.getCurrentInstance().getComponentListener();
		this.addComponentListener(componentListener);
		if (infoAbilities != null)
		{
			infoAbilities.addComponentListener(componentListener);
		}
		infoClasses.addComponentListener(componentListener);
		infoDesc.addComponentListener(componentListener);
		infoDomains.addComponentListener(componentListener);
		infoFeats.addComponentListener(componentListener);
		//infoMisc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());

		/*
		 * Mynex wants these options eliminated, so I'll comment them out
		 * (in the event they ever decide to return)
		 */
//  		infoPreview.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());

		infoRace.addComponentListener(componentListener);
		infoSkills.addComponentListener(componentListener);
		infoSpells.addComponentListener(componentListener);
		infoSummary.addComponentListener(componentListener);
		//infoNotes.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
	}

	public void setTxtName(String aString)
	{
		infoSummary.getPcNameText().setText(aString);
		//infoSummary.txtName.setText(aString);
		infoDesc.getTxtName().setText(aString);
	}

	public InfoSummary infoSummary()
	{
		return infoSummary;
	}

	public InfoAbilities infoAbilities()
	{
		return infoAbilities;
	}

	public InfoRace infoRace()
	{
		return infoRace;
	}

	public InfoClasses infoClasses()
	{
		return infoClasses;
	}

	public InfoDomain infoDomains()
	{
		return infoDomains;
	}

	public InfoFeats infoFeats()
	{
		return infoFeats;
	}

	public InfoInventory infoInventory()
	{
		return infoInventory;
	}

	public InfoSkills infoSkills()
	{
		return infoSkills;
	}

	public InfoSpells infoSpells()
	{
		return infoSpells;
	}

	/**
	 * Retrieves the number of the tab based on its position.
	 * This method accounts for optional tabs. The value
	 * returned will be one of the CHARACTER_TAB constants
	 * defined in pcgen.core.Constants.
	 *
	 * @param tabPosition The position of the tab on screen, starting from 0 as the first tab
	 * @return the tab number, eg Constants.CHARACTER_TAB_ABILITIES
	 */
	public int getTabNumberFromTabPosition(int tabPosition)
	{
		int tabNumber;

		tabNumber = tabPosition;
		if (infoAbilities == null && tabNumber >= Constants.CHARACTER_TAB_ABILITIES)
		{
			tabNumber++;
		}

		return tabNumber;
	}

	/**
	 * return the currently selected Tab index
	 **/
	public int getSelectedIndex()
	{
		return characterInfoTabbedPane.getSelectedIndex();
	}

	public String getKitFilter()
	{
		Component c = characterInfoTabbedPane.getSelectedComponent();
		if (c instanceof FilterAdapterPanel)
		{
			return ((FilterAdapterPanel) c).getKitFilter();
		}
		else if (c instanceof InfoInventory)
		{
			return ((InfoInventory) c).getInfoGear().getKitFilter();
		}
		return "";
	}

	/**
	 * return the Tab index that matches aString
	 **/
	public int indexOfTab(String aString)
	{
		return characterInfoTabbedPane.indexOfTab(aString);
	}

	/**
	 * this method provides access to the tabs for the Filter
	 *
	 * <br>author: Thomas Behr 09-02-02
	 *
	 * @return the selected tab as instance of Filterable
	 */
	public Filterable getSelectedFilterable()
	{
		Component c = characterInfoTabbedPane.getSelectedComponent();
		if (c instanceof Filterable)
		{
			return (Filterable) c;
		}
		return null;
	}

	/**
	 * call this method prior to closing the tab
	 * it will store the most recent filter settings
	 *
	 * <br>author: Thomas Behr 18-02-02
	 */
	public void storeFilterSettings()
	{
		SettingsHandler.storeFilterSettings(infoClasses);
		SettingsHandler.storeFilterSettings(infoDomains);
		SettingsHandler.storeFilterSettings(infoFeats);
		SettingsHandler.storeFilterSettings(infoInventory.getInfoGear());
		SettingsHandler.storeFilterSettings(infoRace);
		SettingsHandler.storeFilterSettings(infoSkills);
		SettingsHandler.storeFilterSettings(infoSpells);
		SettingsHandler.storeFilterSettings(infoSummary);
	}

	/**
	 * update/restore filter settings from globally saved settings
	 *
	 * <br>author: Thomas Behr 24-02-02, 07-03-02
	 *
	 * @param filterableName   the name of the Filterable;<br>
	 *                         if <code>null</code> then filters for all
	 *                         Filterables will be updated, i.e.
	 *                         {@link #restoreAllFilterSettings} will be called
	 */
	public void restoreFilterSettings(String filterableName)
	{
		if (filterableName == null)
		{
			restoreAllFilterSettings();
			return;
		}

		Component c;
		for (int i = 0; i < characterInfoTabbedPane.getTabCount(); i++)
		{
			c = characterInfoTabbedPane.getComponentAt(i);
			if (c instanceof Filterable)
			{
				if ((c.getName() != null) && c.getName().equals(filterableName))
				{
					FilterFactory.restoreFilterSettings((Filterable) c);
				}
			}
		}
	}

	/**
	 * update/restore filter settings from globally saved settings
	 *
	 * <br>author: Thomas Behr 07-03-02
	 */
	private void restoreAllFilterSettings()
	{
		FilterFactory.clearFilterCache();
		FilterFactory.restoreFilterSettings(infoClasses);
		FilterFactory.restoreFilterSettings(infoDomains);
		FilterFactory.restoreFilterSettings(infoFeats);
		FilterFactory.restoreFilterSettings(infoInventory.getInfoGear());
		FilterFactory.restoreFilterSettings(infoRace);
		FilterFactory.restoreFilterSettings(infoSkills);
		FilterFactory.restoreFilterSettings(infoSpells);
		FilterFactory.restoreFilterSettings(infoSummary);
	}

	final Component getActivePane()
	{
		return characterInfoTabbedPane.getSelectedComponent();
	}
}
