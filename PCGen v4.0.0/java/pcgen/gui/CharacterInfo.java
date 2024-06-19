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
 * Last Edited: $Date: 2006/02/21 00:47:12 $
 *
 */

package pcgen.gui;

// This snippet creates a new tabbed panel

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;

/**
 * <code>CharacterInfo</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

class CharacterInfo extends JPanel
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

	public static String in_abilities; // Summary tab needs access
	private static String in_classes;
	private static String in_domains;
	private static String in_descrip;
	public static String in_equipping; // Inventory tab needs access
	private static String in_feats;
	private static String in_inventory;
	//private static String in_misc;
	//private static String in_notes;
	private static String in_preview;
	private static String in_race;
	public static String in_resources; // Inventory tab needs access
	private static String in_spells;
	private static String in_skills;
	private static String in_summary;

	/**
	 * Resource bundles
	 **/
	static
	{
		ResourceBundle characterInfoProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			characterInfoProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_abilities = characterInfoProperties.getString("in_abilities");
			in_classes = characterInfoProperties.getString("in_classes");
			in_descrip = characterInfoProperties.getString("in_descrip");
			in_domains = characterInfoProperties.getString("in_domains");
			in_equipping = characterInfoProperties.getString("in_equipping");
			in_feats = characterInfoProperties.getString("in_feats");
			in_inventory = characterInfoProperties.getString("in_inventory");
			//in_misc = characterInfoProperties.getString("in_misc");
			//in_notes = characterInfoProperties.getString("in_notes");
			in_preview = characterInfoProperties.getString("in_preview");
			in_race = characterInfoProperties.getString("in_race");
			in_resources = characterInfoProperties.getString("in_resources");
			in_spells = characterInfoProperties.getString("in_spells");
			in_skills = characterInfoProperties.getString("in_skills");
//			in_stats = characterInfoProperties.getString("in_stats");
			in_summary = characterInfoProperties.getString("in_summary");
//			in_templates = characterInfoProperties.getString("in_templates");
		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			characterInfoProperties = null;
		}
	}

	public CharacterInfo()
	{
		try
		{
			jbInit();
		}
		catch (Exception e) //This is what jbInit actually throws...
		{
			e.printStackTrace();
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

/* Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
		if (SettingsHandler.isSummaryTabShown())
		{*/
			characterInfoTabbedPane.add(infoSummary, in_summary); //always show summary tab now
//		}
		characterInfoTabbedPane.add(infoRace, in_race);
		if (SettingsHandler.isAbilitiesShownAsATab())
		{
			infoAbilities = new InfoAbilities();
			characterInfoTabbedPane.add(infoAbilities, in_abilities);
		}
		characterInfoTabbedPane.add(infoClasses, in_classes);
		characterInfoTabbedPane.add(infoSkills, in_skills);
		characterInfoTabbedPane.add(infoFeats, in_feats);
		if (Globals.isDndMode())
		{
			characterInfoTabbedPane.add(infoDomains, in_domains);
			characterInfoTabbedPane.add(infoSpells, in_spells);
		}
		else if (Globals.isDeadlandsMode() ||
		  Globals.isWheelMode() ||
		  Globals.isSSd20Mode() ||
		  Globals.isWeirdWarsMode())
		{
			characterInfoTabbedPane.add(infoSpells, in_spells);
		}
		characterInfoTabbedPane.add(infoInventory, in_inventory);
		characterInfoTabbedPane.add(infoDesc, in_descrip);
		//characterInfoTabbedPane.add(infoMisc, in_misc);
		//characterInfoTabbedPane.add(infoNotes, in_notes);
/* Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
		if (SettingsHandler.isPreviewTabShown())
		{
			characterInfoTabbedPane.add(infoPreview, in_preview);
		}*/


		// since our filter icon changes if # of selected filters > 0
		// we register component listeners
		this.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		if (infoAbilities != null)
		{
			infoAbilities.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		}
		infoClasses.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoDesc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoDomains.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
//		infoEquipping.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoFeats.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoInventory.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		//infoMisc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
/* Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
		infoPreview.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());*/
		infoRace.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
//		infoResources.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSkills.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSpells.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSummary.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		//infoNotes.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
	}

	public void setTxtName(String aString)
	{
		infoSummary.txtName.setText(aString);
		infoDesc.txtName.setText(aString);
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

/*	public InfoEquipping infoEquipping()
	{
		return infoEquipping;
	}
*/

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

	public void eqList_Changed(Equipment newEq, boolean purchase, boolean isActive)
	{
		infoInventory.refreshAvailableList(newEq, purchase, isActive);
	}

	public void featList_Changed()
	{
		infoFeats.forceUpdate();
	}

	/**
	 * Retrieves the number of the tab based on its position.
	 * This method accounts for optional tabs. The value
	 * returned will be one of the CHARACTER_TAB constatnts
	 * defined in pcgen.core.Constants.
	 *
	 * @param tabPosition The position of the tab on screen, starting from 0 as the first tab
	 * @returns the tab number, eg Constants.CHARACTER_TAB_ABILITIES
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

	//public void HPTotal_Changed()
	//{
	//	infoClasses.updateHP();
	//}

	/**
	 * Set the currently selected Tab to index
	 **/
	public void setSelectedIndex(int index)
	{
		characterInfoTabbedPane.setSelectedIndex(index);
		characterInfoTabbedPane.getComponentAt(index).requestFocus();
		characterInfoTabbedPane.updateUI();
	}

	/**
	 * return the currently selected Tab index
	 **/
	public int getSelectedIndex()
	{
		return characterInfoTabbedPane.getSelectedIndex();
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
			return (Filterable)c;
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
//		SettingsHandler.storeFilterSettings(infoEquipping);
		SettingsHandler.storeFilterSettings(infoInventory);
		SettingsHandler.storeFilterSettings(infoRace);
		SettingsHandler.storeFilterSettings(infoSkills);
		SettingsHandler.storeFilterSettings(infoSpells);
//		Globals.storeFilterSettings(infoSummary);
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
				if ((c.getName() != null) &&
				  c.getName().equals(filterableName))
				{
					FilterFactory.restoreFilterSettings((Filterable)c);
				}
			}
		}
	}

	/**
	 * update/restore filter settings from globally saved settings
	 *
	 * <br>author: Thomas Behr 07-03-02
	 */
	public void restoreAllFilterSettings()
	{
		FilterFactory.clearFilterCache();
		FilterFactory.restoreFilterSettings(infoClasses);
		FilterFactory.restoreFilterSettings(infoDomains);
//		FilterFactory.restoreFilterSettings(infoEquipping);
		FilterFactory.restoreFilterSettings(infoFeats);
		FilterFactory.restoreFilterSettings(infoInventory);
		FilterFactory.restoreFilterSettings(infoRace);
		FilterFactory.restoreFilterSettings(infoSkills);
		FilterFactory.restoreFilterSettings(infoSpells);
//		FilterFactory.restoreFilterSettings(infoSummary);
	}

}

