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
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;

/**
 * <code>CharacterInfo</code> creates a new tabbed panel.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class CharacterInfo extends JPanel
{
	private JTabbedPane characterInfoTabbedPane = new JTabbedPane();

	private InfoAbilities infoAbilities = new InfoAbilities();
	private InfoClasses infoClasses = new InfoClasses();
	private InfoDescription infoDesc = new InfoDescription();
	private InfoDomain infoDomains = new InfoDomain();
	private InfoEquipping infoEquipping = new InfoEquipping();
	private InfoFeats infoFeats = new InfoFeats();
	private InfoInventory infoInventory = new InfoInventory();
	private InfoMisc infoMisc = new InfoMisc();
	private InfoPreview infoPreview = new InfoPreview();
	private InfoRace infoRace = new InfoRace();
	private InfoSpells infoSpells = new InfoSpells();
	private InfoSkills infoSkills = new InfoSkills();
	private InfoNotes infoNotes = new InfoNotes();

	private BorderLayout borderLayout1 = new BorderLayout();

	private static String in_descrip;
	private static String in_spells;
	private static String in_preview;
	private static String in_misc;
	// private static String in_companions;
	private static String in_inventory;
	private static String in_abilities;
	private static String in_race;
	// private static String in_templates;
	private static String in_classes;
	private static String in_feats;
	private static String in_skills;
	private static String in_domains;
	private static String in_notes;
	private static String in_equipping;

	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle characterInfoProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			characterInfoProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
//			in_stats = characterInfoProperties.getString("in_stats");
			in_abilities = characterInfoProperties.getString("in_abilities");
//			in_templates = characterInfoProperties.getString("in_templates");
			in_skills = characterInfoProperties.getString("in_skills");
			in_feats = characterInfoProperties.getString("in_feats");
			in_domains = characterInfoProperties.getString("in_domains");
			in_spells = characterInfoProperties.getString("in_spells");
			in_inventory = characterInfoProperties.getString("in_inventory");
			in_classes = characterInfoProperties.getString("in_classes");
			in_race = characterInfoProperties.getString("in_race");
			in_descrip = characterInfoProperties.getString("in_descrip");
			in_misc = characterInfoProperties.getString("in_misc");
			in_preview = characterInfoProperties.getString("in_preview");
			// in_companions = characterInfoProperties.getString("in_companions");
			in_notes = characterInfoProperties.getString("in_notes");
			in_equipping = characterInfoProperties.getString("in_equipping");
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
		int x = 0;

		if (Globals.getChaTabPlacement() == 0)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.TOP);
		else if (Globals.getChaTabPlacement() == 1)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.LEFT);
		else if (Globals.getChaTabPlacement() == 2)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		else if (Globals.getChaTabPlacement() == 3)
			characterInfoTabbedPane.setTabPlacement(JTabbedPane.RIGHT);

		characterInfoTabbedPane.add(infoRace, in_race, x++);
		characterInfoTabbedPane.add(infoAbilities, in_abilities, x++);
		characterInfoTabbedPane.add(infoClasses, in_classes, x++);
		characterInfoTabbedPane.add(infoSkills, in_skills, x++);
		characterInfoTabbedPane.add(infoFeats, in_feats, x++);
		if (Globals.isDndMode())
		{
			characterInfoTabbedPane.add(infoDomains, in_domains, x++); // should be 5
			characterInfoTabbedPane.add(infoSpells, in_spells, x++); // should be 6
		}
		characterInfoTabbedPane.add(infoInventory, in_inventory, x++);
		characterInfoTabbedPane.add(infoDesc, in_descrip, x++);
		characterInfoTabbedPane.add(infoMisc, in_misc, x++);
		if (Globals.isPreviewTabShown())
		{
			characterInfoTabbedPane.add(infoPreview, in_preview, x++);
		}
		characterInfoTabbedPane.add(infoNotes, in_notes, x++);
		characterInfoTabbedPane.add(infoEquipping, in_equipping, x++);


		// since out filter icon changes according to # selected filters > 0
		// we register component listeners
		this.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoAbilities.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoClasses.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoDesc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoDomains.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoEquipping.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoFeats.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoInventory.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoMisc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoPreview.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoRace.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSkills.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSpells.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
	}

	public void setTxtName(String aString)
	{
		infoDesc.txtName.setText(aString);
	}

	public InfoClasses infoClasses()
	{
		return infoClasses;
	}

	public InfoDomain infoDomains()
	{
		return infoDomains;
	}

	public InfoEquipping infoEquipping()
	{
		return infoEquipping;
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

	public void eqList_Changed(Equipment newEq, boolean purchase, boolean isActive)
	{
		infoInventory.refreshAvailableList(newEq, purchase, isActive);
	}

	public void featList_Changed()
	{
		infoFeats.forceUpdate();
	}

	//public void hpTotal_Changed()
	//{
	//	infoClasses.updateHp();
	//}

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
		Globals.storeFilterSettings(infoClasses);
		Globals.storeFilterSettings(infoDomains);
		Globals.storeFilterSettings(infoFeats);
		Globals.storeFilterSettings(infoEquipping);
		Globals.storeFilterSettings(infoInventory);
		Globals.storeFilterSettings(infoRace);
		Globals.storeFilterSettings(infoSkills);
		Globals.storeFilterSettings(infoSpells);
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
		FilterFactory.restoreFilterSettings(infoEquipping);
		FilterFactory.restoreFilterSettings(infoFeats);
		FilterFactory.restoreFilterSettings(infoInventory);
		FilterFactory.restoreFilterSettings(infoRace);
		FilterFactory.restoreFilterSettings(infoSkills);
		FilterFactory.restoreFilterSettings(infoSpells);
	}

}

