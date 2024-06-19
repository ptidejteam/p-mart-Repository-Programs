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

import java.awt.*;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
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

	private InfoRace infoRace = new InfoRace();
	private InfoAbilities infoAbilities = new InfoAbilities();
	// private InfoStats infoStats = new InfoStats();
	private InfoClasses infoClasses = new InfoClasses();
	// private InfoTemplates infoTemplates = new InfoTemplates();
	private InfoSkills infoSkills = new InfoSkills();
	private InfoFeats infoFeats = new InfoFeats();
	private InfoDomain infoDomains = new InfoDomain();
	private InfoSpells infoSpells = new InfoSpells();
	// private InfoTraits infoTraits = new InfoTraits();
	private InfoInventory infoInventory = new InfoInventory();
	private InfoDescription infoDesc = new InfoDescription();
	// JPanel infoItems = new InfoItems();
	// JPanel infoBuying = new InfoBuying();
	// private JPanel infoEquip = new InfoEquipment();
	// private JPanel infoCompanions = new InfoCompanions();
	private InfoMisc infoMisc = new InfoMisc();
	private InfoPreview infoPreview = new InfoPreview();
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

	/**
	 * Resrouce bundles
	 */
	static
	{
		ResourceBundle characterInfoProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			characterInfoProperties = ResourceBundle.getBundle("pcgen/gui/properities/LanguageBundle", currentLocale);
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

	/*
	 * Needs to be non-static to address bug #495558
	 * (multiple character sheets lose name field).
	 *
	 * removed txtName since zebulon's description tab is done.
	 * code remains as comments, might be we'll need it yet again ;-)
	 *
	 * author: Thomas Behr 03-01-02
	 */
//          public JTextField txtName = new JTextField();
//          private JLabel lblName = new JLabel();
//          private JButton randName = new JButton();
//          private JPanel panelNorth = new JPanel();

	public CharacterInfo()
	{
		try
		{
			jbInit();
		}
		catch (Exception e)
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

//		characterInfoTabbedPane.add(infoStats, in_stats, x++);
		characterInfoTabbedPane.add(infoRace, in_race, x++);
		characterInfoTabbedPane.add(infoAbilities, in_abilities, x++);
		characterInfoTabbedPane.add(infoClasses, in_classes, x++);
//		if (Globals.getTemplateList().size() != 0)
//		characterInfoTabbedPane.add(infoTemplates, in_templates, x++);
//		characterInfoTabbedPane.add(infoAbilities, in_abilities, x++);
		characterInfoTabbedPane.add(infoSkills, in_skills, x++);
		characterInfoTabbedPane.add(infoFeats, in_feats, x++);
		if (Globals.isDndMode())
		{
			characterInfoTabbedPane.add(infoDomains, in_domains, x++); // should be 5
			characterInfoTabbedPane.add(infoSpells, in_spells, x++); // should be 6
		}
		// characterInfoTabbedPane.add(infoEquip, "Equipment", x++);
		characterInfoTabbedPane.add(infoInventory, in_inventory, x++);
		// characterInfoTabbedPane.add(infoCompanions, in_companions, x++);
		// characterInfoTabbedPane.add(infoItems, "Items",x++);
		// characterInfoTabbedPane.add(infoBuying, "Buying",x++);
		characterInfoTabbedPane.add(infoDesc, in_descrip, x++);
		characterInfoTabbedPane.add(infoMisc, in_misc, x++);
		if (Globals.isPreviewTabShown())
		{
//  			InfoPreview infoPreview = new InfoPreview();
			characterInfoTabbedPane.add(infoPreview, in_preview, x++);
		}
		characterInfoTabbedPane.add(infoNotes, in_notes, x++);


		// since out filter icon changes according to # selected filters > 0
		// we register component listeners
		this.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoDesc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoAbilities.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoClasses.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		// infoCompanions.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoDomains.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoFeats.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoInventory.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoMisc.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoPreview.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSkills.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoSpells.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
//		infoStats.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
//		infoTemplates.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
		infoRace.addComponentListener(PToolBar.getCurrentInstance().getComponentListener());
	}

	public void setTxtName(String aString)
	{
//  		txtName.setText(aString);
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
		Globals.storeFilterSettings(infoInventory);
		Globals.storeFilterSettings(infoSkills);
		Globals.storeFilterSettings(infoSpells);
//		Globals.storeFilterSettings(infoStats);
//		Globals.storeFilterSettings(infoTemplates);
		Globals.storeFilterSettings(infoRace);
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
		FilterFactory.restoreFilterSettings(infoFeats);
		FilterFactory.restoreFilterSettings(infoInventory);
		FilterFactory.restoreFilterSettings(infoSkills);
		FilterFactory.restoreFilterSettings(infoSpells);
		FilterFactory.restoreFilterSettings(infoRace);
	}

}

