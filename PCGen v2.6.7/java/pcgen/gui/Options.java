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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;
import pcgen.core.Constants;
import pcgen.core.Globals;

/**
 * Provide a panel with most of the rule configuration options for a campaign.
 * This lots of unrelated entries.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
public class Options extends JMenu
{
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private StatRollMethodListener statRollMethodHandler = new StatRollMethodListener();
	private static AllStatsListener allStatsHandler = new AllStatsListener();
	private JMenuItem purchaseMode = new JMenuItem("Purchase Mode...");
	private JCheckBoxMenuItem useMonsterDefault = new JCheckBoxMenuItem("Use Default Monsters");

	private JMenu rollMethods = new JMenu("Stat Roll Methods");
	private static JCheckBoxMenuItem method1 = new JCheckBoxMenuItem("4d6 drop lowest (Standard)");
	private static JCheckBoxMenuItem method2 = new JCheckBoxMenuItem("3d6");
	private static JCheckBoxMenuItem method3 = new JCheckBoxMenuItem("5d6 drop two lowest");
	private static JCheckBoxMenuItem method7 = new JCheckBoxMenuItem("5d6 drop lowest and middle");
	private static JCheckBoxMenuItem method4 = new JCheckBoxMenuItem("4d6 drop lowest, reroll 1's");
	private static JCheckBoxMenuItem method5 = new JCheckBoxMenuItem("4d6 drop lowest, reroll 1's and 2's");
	private static JCheckBoxMenuItem method6 = new JCheckBoxMenuItem("3d6+5");
	private static JMenu method8 = new JMenu("All the same");
	private static JCheckBoxMenuItem method8_ [] = null;

	private JCheckBoxMenuItem maxHpAtFirstLevel = new JCheckBoxMenuItem("Max Hp at first level");
	private JCheckBoxMenuItem maxStartingGold = new JCheckBoxMenuItem("Max Starting Gold");
	private JCheckBoxMenuItem skillIncrementBefore = new JCheckBoxMenuItem("Skill Increment before leveling");
	private JCheckBoxMenuItem freeClothesAtFirst = new JCheckBoxMenuItem("Ask for free clothing at first level");
	private JMenu hpRollMethods = new JMenu("HP Roll Methods");
	private JCheckBoxMenuItem hpStandard = new JCheckBoxMenuItem("Standard");
	private JCheckBoxMenuItem hpAutomax = new JCheckBoxMenuItem("Always maximum");
	private JCheckBoxMenuItem hpPercentage = new JCheckBoxMenuItem("Percentage");
	private JCheckBoxMenuItem hpLivingCity = new JCheckBoxMenuItem("Living City");
	private JCheckBoxMenuItem hpLivingGreyhawk = new JCheckBoxMenuItem("Living Greyhawk");
	private JCheckBoxMenuItem toolTipTextShown = new JCheckBoxMenuItem("Show Tooltip text");
	private JCheckBoxMenuItem previewTabShown = new JCheckBoxMenuItem("Show Preview Tab");

	private JMenu autoCreateMethods = new JMenu("Equipment Auto Creation");
	private JCheckBoxMenuItem autoMethod0 = new JCheckBoxMenuItem("Load from lst files only");
	private JCheckBoxMenuItem autoMethod1 = new JCheckBoxMenuItem("Racially resized armor, shields and clothing");
	private JCheckBoxMenuItem autoMethod2 = new JCheckBoxMenuItem("Masterwork");
	private JCheckBoxMenuItem autoMethod3 = new JCheckBoxMenuItem("Magic (+1 to +5)");
	private JCheckBoxMenuItem autoMethod4 = new JCheckBoxMenuItem("Adamantine/Darkwood/Mithral");
	private JCheckBoxMenuItem applyWeightPenaltyToSkills = new JCheckBoxMenuItem("Apply Weight Penalty to Skills");
	private JCheckBoxMenuItem applyLoadPenaltyToACandSkills = new JCheckBoxMenuItem("Apply Load Penalty to AC and Skills");
	private JCheckBoxMenuItem treatInHandAsEquippedForAttacks = new JCheckBoxMenuItem("Treat In Hand Weapons as Equipped for Attacks");

	private JMenu sourceOptions = new JMenu("Source Display");
	private JCheckBoxMenuItem sourceLong = new JCheckBoxMenuItem("Long");
	private JCheckBoxMenuItem sourcePage = new JCheckBoxMenuItem("Page");
	private JCheckBoxMenuItem sourceShort = new JCheckBoxMenuItem("Short");
	private JCheckBoxMenuItem sourceWeb = new JCheckBoxMenuItem("Web");


	/** Creates new form Options */
	public Options()
	{
		setText("Options");
		Utility.maybeSetIcon(this, "Preferences16.gif");
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
		this.add(purchaseMode);
		Utility.setDescription(purchaseMode, "Change stat costs for point system");
		purchaseMode.addActionListener(checkBoxHandler);
		purchaseMode.setSelected(Globals.isPurchaseStatMode());
		purchaseMode.setMnemonic('P');

		this.add(useMonsterDefault);
		Utility.setDescription(useMonsterDefault, "Use Default Monster Feats and Skills");
		useMonsterDefault.addActionListener(checkBoxHandler);
		useMonsterDefault.setSelected(Globals.isMonsterDefault());
		useMonsterDefault.setMnemonic('D');

		this.add(rollMethods);
		rollMethods.setMnemonic('S');

		rollMethods.add(method1);
		Utility.setDescription(method1, "Roll 4d6 drop lowest (Standard)");
		method1.addActionListener(statRollMethodHandler);

		rollMethods.add(method2);
		Utility.setDescription(method2, "Roll 3d6");
		method2.addActionListener(statRollMethodHandler);

		rollMethods.add(method6);
		Utility.setDescription(method6, "Roll 3d6+5");
		method6.addActionListener(statRollMethodHandler);

		rollMethods.add(method4);
		Utility.setDescription(method4, "Roll 4d6 drop lowest and reroll 1's");
		method4.addActionListener(statRollMethodHandler);

		rollMethods.add(method5);
		Utility.setDescription(method5, "Roll 4d6 drop lowest reroll 1's and 2's");
		method5.addActionListener(statRollMethodHandler);

		rollMethods.add(method7);
		Utility.setDescription(method7, "Roll 5d6 drop lowest and middle");
		method7.addActionListener(statRollMethodHandler);

		rollMethods.add(method3);
		Utility.setDescription(method3, "Roll 5d6 drop two lowest");
		method3.addActionListener(statRollMethodHandler);

		rollMethods.add(method8);
		Utility.setDescription(method8, "All the same");

		initializeStatRollingMethod8();

		switch (Globals.getRollMethod())
		{
			case 0:
				return;
			case 1:
				method1.setSelected(true);
				break;
			case 2:
				method2.setSelected(true);
				break;
			case 3:
				method3.setSelected(true);
				break;
			case 4:
				method4.setSelected(true);
				break;
			case 5:
				method5.setSelected(true);
				break;
			case 6:
				method6.setSelected(true);
				break;
			case 7:
				method7.setSelected(true);
				break;
			case 8:
				//Handled in initializeStatRollingMethod8
				break;
		}

		this.add(maxHpAtFirstLevel);
		Utility.setDescription(maxHpAtFirstLevel, "Set whether the first level should have max hitpoints");
		maxHpAtFirstLevel.addActionListener(checkBoxHandler);
		maxHpAtFirstLevel.setSelected(Globals.isHpMaxAtFirstLevel());
		maxHpAtFirstLevel.setMnemonic('M');

		this.add(maxStartingGold);
		Utility.setDescription(maxStartingGold, "Set whether starting gold should be maximized");
		maxStartingGold.addActionListener(checkBoxHandler);
		maxStartingGold.setSelected(Globals.isMaxStartingGold());
		maxStartingGold.setMnemonic('C');

		this.add(hpRollMethods);
		hpRollMethods.setMnemonic('H');

		hpRollMethods.add(hpStandard);
		Utility.setDescription(hpStandard, "Standard");
		hpStandard.addActionListener(checkBoxHandler);

		hpRollMethods.add(hpAutomax);
		Utility.setDescription(hpAutomax, "Always maximum");
		hpAutomax.addActionListener(checkBoxHandler);

		hpRollMethods.add(hpPercentage);
		Utility.setDescription(hpPercentage, "Percentage");
		hpPercentage.addActionListener(checkBoxHandler);

		hpRollMethods.add(hpLivingCity);
		Utility.setDescription(hpLivingCity, "First two levels max, 3/4 thereafter");
		hpLivingCity.addActionListener(checkBoxHandler);

		hpRollMethods.add(hpLivingGreyhawk);
		Utility.setDescription(hpLivingGreyhawk, "First level max, 1/2 + 1 thereafter");
		hpLivingGreyhawk.addActionListener(checkBoxHandler);

		this.add(toolTipTextShown);
		Utility.setDescription(toolTipTextShown, "Show tooltips over tables");
		toolTipTextShown.addActionListener(checkBoxHandler);
		toolTipTextShown.setSelected(Globals.isToolTipTextShown());
		toolTipTextShown.setMnemonic('T');

		this.add(previewTabShown);
		Utility.setDescription(previewTabShown, "Show Preview tab in character's tabs");
		previewTabShown.addActionListener(checkBoxHandler);
		previewTabShown.setSelected(Globals.isPreviewTabShown());
		previewTabShown.setMnemonic('r');

		this.add(skillIncrementBefore);
		Utility.setDescription(skillIncrementBefore, "Set whether skills can be incremented before skill points calculated");
		skillIncrementBefore.addActionListener(checkBoxHandler);
		skillIncrementBefore.setSelected(Globals.isSkillIncrementBefore());
		skillIncrementBefore.setMnemonic('I');

		this.add(freeClothesAtFirst);
		Utility.setDescription(freeClothesAtFirst, "Set to ask for free clothing selection when take first non-monster level");
		freeClothesAtFirst.addActionListener(checkBoxHandler);
		freeClothesAtFirst.setSelected(Globals.isFreeClothesAtFirst());
		freeClothesAtFirst.setMnemonic('f');

		switch (Globals.getHpRollMethod())
		{
			case Constants.s_HP_STANDARD:
				hpStandard.setSelected(true);
				break;
			case Constants.s_HP_AUTOMAX:
				hpAutomax.setSelected(true);
				break;
			case Constants.s_HP_PERCENTAGE:
				hpPercentage.setSelected(true);
				break;
			case Constants.s_HP_LIVING_CITY:
				hpLivingCity.setSelected(true);
				break;
			case Constants.s_HP_LIVING_GREYHAWK:
				hpLivingGreyhawk.setSelected(true);
				break;
		}


		this.add(autoCreateMethods);
		autoCreateMethods.setMnemonic('C');

		final boolean isSelected = Globals.wantToLoadMasterworkAndMagic();
		Globals.setWantToLoadMasterworkAndMagic(false);		// Turn off temporarily so we get current setting

		autoCreateMethods.add(autoMethod0);
		autoMethod0.addActionListener(checkBoxHandler);
		autoMethod0.setSelected(isSelected);
		Utility.setDescription(autoMethod0, "Load from lst files (ignores #; at start of lines)");

		autoCreateMethods.add(autoMethod1);
		autoMethod1.addActionListener(checkBoxHandler);
		autoMethod1.setSelected(Globals.getAutogen(Constants.AUTOGEN_RACIAL));
		Utility.setDescription(autoMethod1, "Create racially sized armor, shields, clothing (resizable) for all sizes in race list");

		autoCreateMethods.add(autoMethod2);
		autoMethod2.addActionListener(checkBoxHandler);
		autoMethod2.setSelected(Globals.getAutogen(Constants.AUTOGEN_MASTERWORK));
		Utility.setDescription(autoMethod2, "Create masterwork ammo, armor, shields, and weapons");

		autoCreateMethods.add(autoMethod3);
		autoMethod3.addActionListener(checkBoxHandler);
		autoMethod3.setSelected(Globals.getAutogen(Constants.AUTOGEN_MAGIC));
		Utility.setDescription(autoMethod3, "Create +1 to +5 ammo, armor, shields, and weapons");

		autoCreateMethods.add(autoMethod4);
		autoMethod4.addActionListener(checkBoxHandler);
		autoMethod4.setSelected(Globals.getAutogen(Constants.AUTOGEN_EXOTICMATERIAL));
		Utility.setDescription(autoMethod4, "Create darkwood (wooden), adamantine and mithral (metal) items");

		//
		// Disable other options if 1st is selected
		//
		if (isSelected)
		{
			Globals.setWantToLoadMasterworkAndMagic(true);
			autoMethod1.setEnabled(!isSelected);
			autoMethod2.setEnabled(!isSelected);
			autoMethod3.setEnabled(!isSelected);
			autoMethod4.setEnabled(!isSelected);
		}

		this.add(applyWeightPenaltyToSkills);
		Utility.setDescription(applyWeightPenaltyToSkills, "Whether to give a penalty of -1 per 5 lbs encumbrance to affected skills (e.g. Swim)");
		applyWeightPenaltyToSkills.addActionListener(checkBoxHandler);
		applyWeightPenaltyToSkills.setSelected(Globals.isApplyWeightPenaltyToSkills());
		applyWeightPenaltyToSkills.setMnemonic('A');

		this.add(applyLoadPenaltyToACandSkills);
		Utility.setDescription(applyLoadPenaltyToACandSkills, "Whether to give ACCheck penalties for medium and higher loads.");
		applyLoadPenaltyToACandSkills.addActionListener(checkBoxHandler);
		applyLoadPenaltyToACandSkills.setSelected(Globals.isApplyLoadPenaltyToACandSkills());
		applyLoadPenaltyToACandSkills.setMnemonic('L');

		this.add(treatInHandAsEquippedForAttacks);
		//Utility.setDescription(treatInHandAsEquippedForAttacks, "");
		treatInHandAsEquippedForAttacks.addActionListener(checkBoxHandler);
		treatInHandAsEquippedForAttacks.setSelected(Globals.getTreatInHandAsEquippedForAttacks());
		treatInHandAsEquippedForAttacks.setMnemonic('E');

		this.add(sourceOptions);
		sourceOptions.add(sourceLong);
		Utility.setDescription(sourceLong, "Display Long Source Format");
		sourceLong.addActionListener(checkBoxHandler);
		sourceLong.setSelected(true);
		sourceOptions.add(sourcePage);
		Utility.setDescription(sourcePage, "Display Source Pages Only");
		sourcePage.addActionListener(checkBoxHandler);

		sourceOptions.add(sourceShort);
		Utility.setDescription(sourceShort, "Display Short Source Format");
		sourceShort.addActionListener(checkBoxHandler);

		sourceOptions.add(sourceWeb);
		Utility.setDescription(sourceWeb, "Display Source URL");
		sourceWeb.addActionListener(checkBoxHandler);


	}

	public static void initializeStatRollingMethod8()
	{
		method8_ = new JCheckBoxMenuItem[Globals.getInitialStatMax() - Globals.getInitialStatMin() + 1];

		method8.removeAll();

		for (int i = 0; i < method8_.length; i++)
		{
			method8_[i] = new JCheckBoxMenuItem(Integer.toString(i + Globals.getInitialStatMin()));
			method8.add(method8_[i]);
			Utility.setDescription(method8_[i], "All " + (i + Globals.getInitialStatMin()) + "'s");
			method8_[i].addActionListener(allStatsHandler);
		}

		final int i = Math.min(Globals.getInitialStatMax(), Globals.getAllStatsValue());
		Globals.setAllStatsValue(i);
		if (Globals.getRollMethod() == 8)
			method8_[i - Globals.getInitialStatMin()].setSelected(true);
	}

	/**
	 * This class is used to respond to stat roll method selection
	 */
	private final class StatRollMethodListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();

			method1.setSelected(false);
			method2.setSelected(false);
			method3.setSelected(false);
			method4.setSelected(false);
			method5.setSelected(false);
			method6.setSelected(false);
			method7.setSelected(false);

			for (int i = 0; i < method8_.length; i++)
			{
				method8_[i].setSelected(false);
			}

			if (source == method1)
			{
				method1.setSelected(true);
				Globals.setRollMethod(1);
			}
			else if (source == method2)
			{
				method2.setSelected(true);
				Globals.setRollMethod(2);
			}
			else if (source == method3)
			{
				method3.setSelected(true);
				Globals.setRollMethod(3);
			}
			else if (source == method4)
			{
				method4.setSelected(true);
				Globals.setRollMethod(4);
			}
			else if (source == method5)
			{
				method5.setSelected(true);
				Globals.setRollMethod(5);
			}
			else if (source == method6)
			{
				method6.setSelected(true);
				Globals.setRollMethod(6);
			}
			else if (source == method7)
			{
				method7.setSelected(true);
				Globals.setRollMethod(7);
			}
		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private static final class AllStatsListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			method1.setSelected(false);
			method2.setSelected(false);
			method3.setSelected(false);
			method4.setSelected(false);
			method5.setSelected(false);
			method6.setSelected(false);
			method7.setSelected(false);

			Object source = actionEvent.getSource();
			boolean selected = false;
			for (int i = 0; i < method8_.length; i++)
			{
				if (source == method8_[i])
				{
					selected = true;
					Globals.setAllStatsValue(i + Globals.getInitialStatMin());
				}
				else
				{
					selected = false;
				}
				Globals.setRollMethod(8);
				method8_[i].setSelected(selected);
			}
		}
	}


	/**
	 * This class is used to respond to clicks on the check boxes.
	 */
	private final class CheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();
			if (source == purchaseMode)
			{
				//Create and display purchasemodestats popup frame.
				PurchaseModeStats frame = new PurchaseModeStats();
			}
			else if (source == useMonsterDefault)
			{
				Globals.setMonsterDefault(useMonsterDefault.isSelected());
			}
			else if (source == maxHpAtFirstLevel)
			{
				Globals.setHpMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());
			}
			else if (source == maxStartingGold)
			{
				Globals.setMaxStartingGold(maxStartingGold.isSelected());
			}
			else if (source == skillIncrementBefore)
			{
				Globals.setSkillIncrementBefore(skillIncrementBefore.isSelected());
			}
			else if (source == freeClothesAtFirst)
			{
				Globals.setFreeClothesAtFirst(freeClothesAtFirst.isSelected());
			}
			else if (source == hpStandard)
			{
				hpAutomax.setSelected(false);
				hpPercentage.setSelected(false);
				hpLivingCity.setSelected(false);
				hpLivingGreyhawk.setSelected(false);
				Globals.setHpRollMethod(Constants.s_HP_STANDARD);
			}
			else if (source == hpAutomax)
			{
				hpStandard.setSelected(false);
				hpPercentage.setSelected(false);
				hpLivingCity.setSelected(false);
				hpLivingGreyhawk.setSelected(false);
				Globals.setHpRollMethod(Constants.s_HP_AUTOMAX);
			}
			else if (source == hpPercentage)
			{
				hpStandard.setSelected(false);
				hpAutomax.setSelected(false);
				hpLivingCity.setSelected(false);
				Globals.setHpRollMethod(Constants.s_HP_PERCENTAGE);
			}
			else if (source == hpLivingCity)
			{
				hpStandard.setSelected(false);
				hpAutomax.setSelected(false);
				hpPercentage.setSelected(false);
				hpLivingGreyhawk.setSelected(false);
				Globals.setHpRollMethod(Constants.s_HP_LIVING_CITY);
			}
			else if (source == hpLivingGreyhawk)
			{
				hpStandard.setSelected(false);
				hpAutomax.setSelected(false);
				hpPercentage.setSelected(false);
				hpLivingCity.setSelected(false);
				Globals.setHpRollMethod(Constants.s_HP_LIVING_GREYHAWK);
			}
			else if (source == toolTipTextShown)
			{
				Globals.setToolTipTextShown(toolTipTextShown.isSelected());
				Utility.handleToolTipShownStateChange();
			}
			else if (source == previewTabShown)
			{
				Globals.setPreviewTabShown(previewTabShown.isSelected());
			}
			else if (source == autoMethod0)
			{
				final boolean isSelected = ((JCheckBoxMenuItem)source).isSelected();
				Globals.setWantToLoadMasterworkAndMagic(isSelected);
				autoMethod1.setEnabled(!isSelected);
				autoMethod2.setEnabled(!isSelected);
				autoMethod3.setEnabled(!isSelected);
				autoMethod4.setEnabled(!isSelected);
			}
			else if (source == autoMethod1)
			{
				Globals.setAutogen(Constants.AUTOGEN_RACIAL, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == autoMethod2)
			{
				Globals.setAutogen(Constants.AUTOGEN_MASTERWORK, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == autoMethod3)
			{
				Globals.setAutogen(Constants.AUTOGEN_MAGIC, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == autoMethod4)
			{
				Globals.setAutogen(Constants.AUTOGEN_EXOTICMATERIAL, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == applyWeightPenaltyToSkills)
			{
				Globals.setApplyWeightPenalty(applyWeightPenaltyToSkills.isSelected());
			}
			else if (source == applyLoadPenaltyToACandSkills)
			{
				Globals.setApplyLoadPenaltyToACandSkills(applyLoadPenaltyToACandSkills.isSelected());
			}
			else if (source == treatInHandAsEquippedForAttacks)
			{
				Globals.setTreatInHandAsEquippedForAttacks(treatInHandAsEquippedForAttacks.isSelected());
			}
			else if (source == sourceLong)
			{
				Globals.setSourceDisplay(Constants.SOURCELONG);
				sourceShort.setSelected(false);
				sourcePage.setSelected(false);
				sourceWeb.setSelected(false);
			}
			else if (source == sourceShort)
			{
				Globals.setSourceDisplay(Constants.SOURCESHORT);
				sourceLong.setSelected(false);
				sourcePage.setSelected(false);
				sourceWeb.setSelected(false);
			}
			else if (source == sourcePage)
			{
				Globals.setSourceDisplay(Constants.SOURCEPAGE);
				sourceShort.setSelected(false);
				sourceLong.setSelected(false);
				sourceWeb.setSelected(false);
			}
			else if (source == sourceWeb)
			{
				sourceShort.setSelected(false);
				sourcePage.setSelected(false);
				sourceLong.setSelected(false);
				Globals.setSourceDisplay(Constants.SOURCEWEB);
			}
		}
	}

	private class PurchaseModeStats extends JFrame
	{
		JScrollPane statCostArea;
		JTableEx statCostTable = new JTableEx();
		StatTableModel statTableModel = new StatTableModel();

		public PurchaseModeStats()
		{
			super("Purchase Mode");
			ClassLoader loader = this.getClass().getClassLoader();
			Toolkit kit = Toolkit.getDefaultToolkit();
			// according to the API, the following should *ALWAYS* use '/'
			Image img =
				kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
			loader = null;
			this.setIconImage(img);

			Utility.centerFrame(this, true);

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());
			statCostTable.setModel(statTableModel);
			statCostArea = new JScrollPane(statCostTable);
			statCostArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			statCostArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			statCostArea.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
			statCostArea.setBackground(new Color(255, 255, 255));
			panel.add(statCostArea, BorderLayout.CENTER);
			Container contentPane = getContentPane();
			contentPane.add(panel);
			this.setVisible(true);
		}
	}

	/**
	 * This class is the model for the stat cost table.
	 */
	private final class StatTableModel extends AbstractTableModel
	{
		private final String[] d_columnNames = new String[]
		{
			"Cost", "Stat"
		};
		private final Class[] d_types = new Class[]
		{
			Integer.class, Integer.class
		};
		private final boolean[] d_canEdit = new boolean[]
		{
			true, false
		};

		public String getColumnName(int columnIndex)
		{
			return d_columnNames[columnIndex];
		}

		public Class getColumnClass(int columnIndex)
		{
			return d_types[columnIndex];
		}

		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return d_canEdit[columnIndex];
		}

		public final int getRowCount()
		{
			return Globals.getStatCost().length;
		}

		public final int getColumnCount()
		{
			return d_columnNames.length;
		}

		public final Object getValueAt(int rowIndex, int columnIndex)
		{
			if (columnIndex == 0)
				return new Integer(Globals.getStatCost(rowIndex));
			else if (columnIndex == 1)
				return new Integer(9 + rowIndex);
			else
				throw new ArrayIndexOutOfBoundsException(columnIndex);
		}

		public final void setValueAt(Object newValue,
			int rowIndex,
			int columnIndex)
		{
			if (newValue instanceof Integer)
			{
				if (columnIndex == 0)
				{
					Globals.setStatCost(rowIndex, ((Integer)newValue).intValue());
					fireTableCellUpdated(rowIndex, columnIndex);
				}
				else
				{
					throw new ArrayIndexOutOfBoundsException(columnIndex);
				}
			}
		}
	}
}
