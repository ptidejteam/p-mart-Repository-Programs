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
/*
  -- Moved to the preferences dialog
	private static final int STATMIN = 3;
	private static final int STATMAX = 18;

	private static PurchaseModeFrame pmsFrame = null;

	//private JCheckBoxMenuItem grittyAC = new JCheckBoxMenuItem("Gritty AC mode");
	//private JCheckBoxMenuItem grittyHP = new JCheckBoxMenuItem("Gritty HP mode");
	private CheckBoxListener checkBoxHandler = new CheckBoxListener();
	private JMenu mnuCrossClassSkillCost = new JMenu("Cross-class Skill Cost");
	private JCheckBoxMenuItem mnuCCSC0 = new JCheckBoxMenuItem("0");
	private JCheckBoxMenuItem mnuCCSC1 = new JCheckBoxMenuItem("1");
	private JCheckBoxMenuItem mnuCCSC2 = new JCheckBoxMenuItem("2");
	private JCheckBoxMenuItem mnuBypassMaxSkillRank = new JCheckBoxMenuItem("Bypass maximum skill rank");
	private JCheckBoxMenuItem mnuBypassFeatPreReqs = new JCheckBoxMenuItem("Bypass feat prerequisites");
	private JCheckBoxMenuItem mnuBypassClassPreReqs = new JCheckBoxMenuItem("Bypass class prerequisites");

	private StatRollMethodListener statRollMethodHandler = new StatRollMethodListener();
	private static AllStatsListener allStatsHandler = new AllStatsListener();
	private static JMenuItem purchaseMode = new JMenuItem("Purchase mode configuration...");
	private JCheckBoxMenuItem useMonsterDefault = new JCheckBoxMenuItem("Use default monsters");

	private static JMenu rollMethods = new JMenu("Stat Roll Methods");
	private static JMenu purchaseMethods = new JMenu("Purchase Mode Methods");
/////////////////////////////////////////////////
// Yanked for WotC compliance
	private static JRadioButtonMenuItem method1 = new JRadioButtonMenuItem("User Rolled");
//	private static JRadioButtonMenuItem method1 = new JRadioButtonMenuItem("4d6 drop lowest (Standard)");
//	private static JRadioButtonMenuItem method2 = new JRadioButtonMenuItem("3d6");
//	private static JRadioButtonMenuItem method3 = new JRadioButtonMenuItem("5d6 drop two lowest");
//	private static JRadioButtonMenuItem method7 = new JRadioButtonMenuItem("5d6 drop lowest and middle");
//	private static JRadioButtonMenuItem method4 = new JRadioButtonMenuItem("4d6 drop lowest, reroll 1's");
//	private static JRadioButtonMenuItem method5 = new JRadioButtonMenuItem("4d6 drop lowest, reroll 1's and 2's");
//	private static JRadioButtonMenuItem method6 = new JRadioButtonMenuItem("3d6+5");
/////////////////////////////////////////////////
	private static JMenu method8 = new JMenu("All the same");
	private static JRadioButtonMenuItem method8_ [] = null;
	private static JRadioButtonMenuItem pmodeButton [] = null;

	private static ButtonGroup statAssignmentGroup = null;

	private static JMenu potionMaxLevel = new JMenu("Potion maximum spell level");
	private static JMenu wandMaxLevel = new JMenu("Wand maximum spell level");
	private JCheckBoxMenuItem allowMetamagicInEqBuilder = new JCheckBoxMenuItem("Allow Metamagic feats on potions, wands, etc.");

	private static JRadioButtonMenuItem wandSpellLevel[] = null;
	private static JRadioButtonMenuItem potionSpellLevel[] = null;

	private JCheckBoxMenuItem maxHpAtFirstLevel = new JCheckBoxMenuItem("Maximum HP at first level");
	private JCheckBoxMenuItem skillIncrementBefore = new JCheckBoxMenuItem("Increment skills before leveling");
	private JCheckBoxMenuItem freeClothesAtFirst = new JCheckBoxMenuItem("Ask for free clothing at first level");
	private JMenu hpRollMethods = new JMenu("HP Roll Methods");
	private JRadioButtonMenuItem hpAutomax = new JRadioButtonMenuItem("Always maximum");
/////////////////////////////////////////////////
// Yanked for WotC compliance
//	private JCheckBoxMenuItem maxStartingGold = new JCheckBoxMenuItem("Maximum starting gold");
//	private JRadioButtonMenuItem hpStandard = new JRadioButtonMenuItem("Standard");
	private JRadioButtonMenuItem hpStandard = new JRadioButtonMenuItem("User Rolled");
//	private JRadioButtonMenuItem hpPercentage = new JRadioButtonMenuItem("Percentage");
//	private JRadioButtonMenuItem hpLivingCity = new JRadioButtonMenuItem("Living City");
//	private JRadioButtonMenuItem hpLivingGreyhawk = new JRadioButtonMenuItem("Living Greyhawk");
/////////////////////////////////////////////////
	private JCheckBoxMenuItem toolTipTextShown = new JCheckBoxMenuItem("Show tooltip text");

// Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
//	private JCheckBoxMenuItem previewTabShown = new JCheckBoxMenuItem("<html>Show <b>Preview</b> tab</html>");
//	private JCheckBoxMenuItem summaryTabShown = new JCheckBoxMenuItem("<html>Show <b>Summary</b> tab</html>");

	private JMenu autoCreateMethods = new JMenu("Equipment Auto Creation");
	private JCheckBoxMenuItem autoMethod0 = new JCheckBoxMenuItem("Load from lst files only");
	private JCheckBoxMenuItem autoMethod1 = new JCheckBoxMenuItem("Racially resized armor, shields and clothing");
	private JCheckBoxMenuItem autoMethod2 = new JCheckBoxMenuItem("Masterwork");
	private JCheckBoxMenuItem autoMethod3 = new JCheckBoxMenuItem("Magic (+1 to +5)");
	private JCheckBoxMenuItem autoMethod4 = new JCheckBoxMenuItem("Exotic materials");	//"Adamantine/Darkwood/Mithral");
	private JCheckBoxMenuItem applyWeightPenaltyToSkills = new JCheckBoxMenuItem("Apply weight penalty to skills");
	private JCheckBoxMenuItem applyLoadPenaltyToACandSkills = new JCheckBoxMenuItem("Apply load penalty to AC and skills");
	private JCheckBoxMenuItem treatInHandAsEquippedForAttacks = new JCheckBoxMenuItem("Treat weapons in hand as equipped for attacks");
	private JCheckBoxMenuItem ignoreLevelCap = new JCheckBoxMenuItem("Ignore level cap");

	private JCheckBoxMenuItem campLoad = new JCheckBoxMenuItem("Autoload sources at start");
	private JCheckBoxMenuItem charCampLoad = new JCheckBoxMenuItem("Autoload sources with PC");
	private JCheckBoxMenuItem saveCustom = new JCheckBoxMenuItem("Save custom equipment to customEquipment.lst");
	private JCheckBoxMenuItem showOGL = new JCheckBoxMenuItem("Display Open Game License at start");
	private JMenuItem browserPath = new JMenuItem("Browser path...");
	private JMenuItem clearBrowserPath = new JMenuItem("Clear browser path");
	private JMenuItem pcgenDataDir = new JMenuItem("Pcgen data directory...");
	private JMenuItem pcgenSystemDir = new JMenuItem("Pcgen system directory...");

	private JMenuItem templateDefault = new JMenuItem("Default export template...");
	private JMenuItem templateEqSet = new JMenuItem("Default EquipSet template...");
	private JMenu mainTabPlacement = new JMenu("Main Tab Placement");
	private JMenu chaTabPlacement = new JMenu("Character Tab Placement");
	private JRadioButtonMenuItem mainTabTop;
	private JRadioButtonMenuItem mainTabBottom;
	private JRadioButtonMenuItem mainTabLeft;
	private JRadioButtonMenuItem mainTabRight;
	private JRadioButtonMenuItem chaTabTop;
	private JRadioButtonMenuItem chaTabBottom;
	private JRadioButtonMenuItem chaTabLeft;
	private JRadioButtonMenuItem chaTabRight;
	private JMenu languageChoice = new JMenu("Language");
	private JRadioButtonMenuItem langEng = new JRadioButtonMenuItem("English");
	private JRadioButtonMenuItem langGer = new JRadioButtonMenuItem("German");
	private JRadioButtonMenuItem langFre = new JRadioButtonMenuItem("French");
	private ChaTabCheckBoxListener chaTabCheckBoxHandler = new ChaTabCheckBoxListener();
	private MainTabCheckBoxListener mainTabCheckBoxHandler = new MainTabCheckBoxListener();
	private LookFeelCheckBoxListener lookFeelCheckBoxHandler = new LookFeelCheckBoxListener();
	private LangCheckBoxListener langCheckBoxHandler = new LangCheckBoxListener();
	private JMenu sourceOptions = new JMenu("Source Display");
	private JRadioButtonMenuItem sourceLong = new JRadioButtonMenuItem("Long");
	private JRadioButtonMenuItem sourcePage = new JRadioButtonMenuItem("Page");
	private JRadioButtonMenuItem sourceShort = new JRadioButtonMenuItem("Short");
	private JRadioButtonMenuItem sourceWeb = new JRadioButtonMenuItem("Web");

	private JMenu looknfeel = new JMenu("GUI Look and Feel");
	private JRadioButtonMenuItem laf[];
	private JRadioButtonMenuItem skinnedLookFeel = new JRadioButtonMenuItem("Skinned");
	private JMenuItem themepack = new JMenuItem("Choose a skin...");
	private JMenu chooseColors = new JMenu("Colors");
	private JMenuItem prereqFailColor = new JMenuItem("Prerequisites not met");
	private JMenuItem featAutoColor = new JMenuItem("Automatic feat");
	private JMenuItem featVirtualColor = new JMenuItem("Virtual feat");
	private ColorListener colorListener = new ColorListener();
	private JCheckBoxMenuItem showToolbar = new JCheckBoxMenuItem("Toolbar");
	private JRadioButtonMenuItem displayPlainName = new JRadioButtonMenuItem("New1 (Plain style)");
	private JRadioButtonMenuItem displayEpicName = new JRadioButtonMenuItem("New1 the Barbarian (Epic style)");
	private JRadioButtonMenuItem displayRaceName = new JRadioButtonMenuItem("New1 the Human (Race style)");
	private JRadioButtonMenuItem displayNetHackName = new JRadioButtonMenuItem("New1 the Human Barbarian (NetHack style)");

	private PaperTypeListener paperTypeHandler = new PaperTypeListener();
	private JMenu paperType = new JMenu("Paper Type");
	private JRadioButtonMenuItem paperNames[] = null;

	private JCheckBoxMenuItem experimentalCursor;
*/
	private PrefsMenuListener prefsMenuHandler = new PrefsMenuListener();

	/** Creates new form Options */
	public Options()
	{
		setText("Settings");
		setMnemonic('S');
		Utility.setDescription(this, "View or change PCGen settings such as type of campaign");
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
/*
  -- Moved to the preferences dialog

	private void addStatsMenu(JMenu parent)
	{
		JMenu statsMenu = (JMenu)parent.add(Utility.createMenu("Ability scores", 'A', "Settings for ability scores", null, true));

		statsMenu.add(purchaseMethods);
		purchaseMethods.setMnemonic('P');

		initializePurchaseModeMethods();

		statsMenu.add(rollMethods);
		rollMethods.setMnemonic('S');

/////////////////////////////////////////////////
// Yanked for WotC compliance
//
		rollMethods.add(method1);
//		Utility.setDescription(method1, "Roll 4d6 drop lowest (Standard)");
		Utility.setDescription(method1, "User Rolled");
		method1.addActionListener(statRollMethodHandler);
//
//		rollMethods.add(method2);
//		Utility.setDescription(method2, "Roll 3d6");
//		method2.addActionListener(statRollMethodHandler);
//
//		rollMethods.add(method6);
//		Utility.setDescription(method6, "Roll 3d6+5");
//		method6.addActionListener(statRollMethodHandler);
//
//		rollMethods.add(method4);
//		Utility.setDescription(method4, "Roll 4d6 drop lowest and reroll 1's");
//		method4.addActionListener(statRollMethodHandler);
//
//		rollMethods.add(method5);
//		Utility.setDescription(method5, "Roll 4d6 drop lowest reroll 1's and 2's");
//		method5.addActionListener(statRollMethodHandler);
//
//		rollMethods.add(method7);
//		Utility.setDescription(method7, "Roll 5d6 drop lowest and middle");
//		method7.addActionListener(statRollMethodHandler);
//
//		rollMethods.add(method3);
//		Utility.setDescription(method3, "Roll 5d6 drop two lowest");
//		method3.addActionListener(statRollMethodHandler);
/////////////////////////////////////////////////
		rollMethods.add(method8);
		Utility.setDescription(method8, "All the same");

		initializeStatRollingMethod8();

/////////////////////////////////////////////////
// Yanked for WotC compliance
		switch (SettingsHandler.getRollMethod())
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				method1.setSelected(true);
				break;
//			case 0:
//				break;  // XXX --bko
//			case 1:
//				method1.setSelected(true);
//				break;
//			case 2:
//				method2.setSelected(true);
//				break;
//			case 3:
//				method3.setSelected(true);
//				break;
//			case 4:
//				method4.setSelected(true);
//				break;
//			case 5:
//				method5.setSelected(true);
//				break;
//			case 6:
//				method6.setSelected(true);
//				break;
//			case 7:
//				method7.setSelected(true);
//				break;
//			case 8:
//				//Handled in initializeStatRollingMethod8
//				break;
			default:
				break;
		}
/////////////////////////////////////////////////
		updateStatMethodGroup();
	}

	//
	// Make sure there is one stat assignment is selected
	//
	public static void checkForSelectedStatMode()
	{
		if ((statAssignmentGroup != null) && (statAssignmentGroup.getSelection() == null))
		{
			method1.setSelected(true);
			SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_STANDARD);
			Globals.getRootFrame().forceUpdate_InfoAbilities();
		}
	}

	//
	// Put all the stat-assignment methods into one big-ass group so that only
	// one can be set at any moment.
	//
	private static void updateStatMethodGroup()
	{
		statAssignmentGroup = new ButtonGroup();

		if (rollMethods != null)
		{
			for (int i = 0, x = rollMethods.getItemCount(); i < x; ++i)
			{
				final Object mnuItem = rollMethods.getItem(i);
				if (mnuItem instanceof JRadioButtonMenuItem)
				{
					statAssignmentGroup.add((AbstractButton)mnuItem);
				}
			}
		}

		if (method8 != null)
		{
			for (int i = 0, x = method8.getItemCount(); i < x; ++i)
			{
				final Object mnuItem = method8.getItem(i);
				if (mnuItem instanceof JRadioButtonMenuItem)
				{
					statAssignmentGroup.add((AbstractButton)mnuItem);
				}
			}
		}

		if (purchaseMethods != null)
		{
			for (int i = 0, x = purchaseMethods.getItemCount(); i < x; ++i)
			{
				final Object mnuItem = purchaseMethods.getItem(i);
				if (mnuItem instanceof JRadioButtonMenuItem)
				{
					statAssignmentGroup.add((AbstractButton)mnuItem);
				}
			}
		}
	}

	private void addHPMenu(JMenu parent)
	{
/////////////////////////////////////////////////
// Yanked for WotC compliance
		JMenu hpMenu = (JMenu)parent.add(Utility.createMenu("Hit points", 'P', "Settings for point point rolls", null, true));

//		hpMenu.add(hpRollMethods);
//		hpRollMethods.setMnemonic('H');

		ButtonGroup hpRollMethodsGroup = new ButtonGroup();

//		hpRollMethodsGroup.add((AbstractButton)hpRollMethods.add(hpStandard));
//		Utility.setDescription(hpStandard, "Standard");
		hpRollMethodsGroup.add((AbstractButton)hpMenu.add(hpStandard));
		Utility.setDescription(hpStandard, "User Rolled");
		hpStandard.addActionListener(checkBoxHandler);

//		hpRollMethodsGroup.add((AbstractButton)hpRollMethods.add(hpAutomax));
		hpRollMethodsGroup.add((AbstractButton)hpMenu.add(hpAutomax));
		Utility.setDescription(hpAutomax, "Always maximum");
		hpAutomax.addActionListener(checkBoxHandler);

//		hpRollMethodsGroup.add((AbstractButton)hpRollMethods.add(hpPercentage));
//		Utility.setDescription(hpPercentage, "Percentage");
//		hpPercentage.addActionListener(checkBoxHandler);
//
//		hpRollMethodsGroup.add((AbstractButton)hpRollMethods.add(hpLivingCity));
//		Utility.setDescription(hpLivingCity, "First two levels max, 3/4 thereafter");
//		hpLivingCity.addActionListener(checkBoxHandler);
//
//		hpRollMethodsGroup.add((AbstractButton)hpRollMethods.add(hpLivingGreyhawk));
//		Utility.setDescription(hpLivingGreyhawk, "First level max, 1/2 + 1 thereafter");
//		hpLivingGreyhawk.addActionListener(checkBoxHandler);

		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.s_HP_STANDARD:
			default:
				hpStandard.setSelected(true);
				break;
			case Constants.s_HP_AUTOMAX:
				hpAutomax.setSelected(true);
				break;
//			case Constants.s_HP_PERCENTAGE:
//				hpPercentage.setSelected(true);
//				break;
//			case Constants.s_HP_LIVING_CITY:
//				hpLivingCity.setSelected(true);
//				break;
//			case Constants.s_HP_LIVING_GREYHAWK:
//				hpLivingGreyhawk.setSelected(true);
//				break;
		}

		hpMenu.add(maxHpAtFirstLevel);
		Utility.setDescription(maxHpAtFirstLevel, "Should characters get maximum hit points at first level?");
		maxHpAtFirstLevel.addActionListener(checkBoxHandler);
		maxHpAtFirstLevel.setSelected(SettingsHandler.isHPMaxAtFirstLevel());
		maxHpAtFirstLevel.setMnemonic('M');
	}

	private void addHouseMenu(JMenu parent)
	{
		JMenu houseMenu = (JMenu)parent.add(Utility.createMenu("House rules", 'H', "Settings for home-grown rules", null, true));

		houseMenu.add(applyLoadPenaltyToACandSkills);
		Utility.setDescription(applyLoadPenaltyToACandSkills, "Whether to give ACCheck penalties for medium and higher loads.");
		applyLoadPenaltyToACandSkills.addActionListener(checkBoxHandler);
		applyLoadPenaltyToACandSkills.setSelected(SettingsHandler.isApplyLoadPenaltyToACandSkills());
		applyLoadPenaltyToACandSkills.setMnemonic('L');

		houseMenu.add(applyWeightPenaltyToSkills);
		Utility.setDescription(applyWeightPenaltyToSkills, "Whether to give a penalty of -1 per 5 lbs encumbrance to affected skills (e.g. Swim)");
		applyWeightPenaltyToSkills.addActionListener(checkBoxHandler);
		applyWeightPenaltyToSkills.setSelected(SettingsHandler.isApplyWeightPenaltyToSkills());
		applyWeightPenaltyToSkills.setMnemonic('A');

		houseMenu.add(freeClothesAtFirst);
		Utility.setDescription(freeClothesAtFirst, "Set to ask for free clothing selection when take first non-monster level");
		freeClothesAtFirst.addActionListener(checkBoxHandler);
		freeClothesAtFirst.setSelected(SettingsHandler.isFreeClothesAtFirst());
		freeClothesAtFirst.setMnemonic('k');

		houseMenu.add(mnuBypassClassPreReqs);
		mnuBypassClassPreReqs.setMnemonic('p');
		mnuBypassClassPreReqs.addActionListener(checkBoxHandler);
		mnuBypassClassPreReqs.setSelected(SettingsHandler.isBoolBypassClassPreReqs());

		houseMenu.add(mnuBypassFeatPreReqs);
		mnuBypassFeatPreReqs.setMnemonic('F');
		mnuBypassFeatPreReqs.addActionListener(checkBoxHandler);
		mnuBypassFeatPreReqs.setSelected(SettingsHandler.isBoolBypassFeatPreReqs());

		houseMenu.add(mnuBypassMaxSkillRank);
		mnuBypassMaxSkillRank.setMnemonic('B');
		mnuBypassMaxSkillRank.addActionListener(checkBoxHandler);
		mnuBypassMaxSkillRank.setSelected(SettingsHandler.isBoolBypassMaxSkillRank());

		houseMenu.add(mnuCrossClassSkillCost);
		mnuCrossClassSkillCost.setMnemonic('C');
		mnuCrossClassSkillCost.add(mnuCCSC0);
		mnuCCSC0.setMnemonic('0');
		mnuCrossClassSkillCost.add(mnuCCSC1);
		mnuCCSC1.setMnemonic('1');
		mnuCrossClassSkillCost.add(mnuCCSC2);
		mnuCCSC2.setMnemonic('2');
		mnuCCSC0.addActionListener(checkBoxHandler);
		mnuCCSC1.addActionListener(checkBoxHandler);
		mnuCCSC2.addActionListener(checkBoxHandler);

		switch (SettingsHandler.getIntCrossClassSkillCost())
		{
			case 0:
				mnuCCSC0.setSelected(true);
				break;
			case 1:
				mnuCCSC1.setSelected(true);
				break;
			case 2:
				mnuCCSC2.setSelected(true);
				break;
			default:    //  The checkbox menu doesn't support other values, but it's non-fatal
				break;
		}

		houseMenu.add(skillIncrementBefore);
		Utility.setDescription(skillIncrementBefore, "Set whether skills can be incremented before skill points calculated");
		skillIncrementBefore.addActionListener(checkBoxHandler);
		skillIncrementBefore.setSelected(SettingsHandler.isSkillIncrementBefore());
		skillIncrementBefore.setMnemonic('I');

/////////////////////////////////////////////////
// Yanked for WotC compliance
//		houseMenu.add(maxStartingGold);
//		Utility.setDescription(maxStartingGold, "Set whether starting gold should be maximized");
//		maxStartingGold.addActionListener(checkBoxHandler);
//		maxStartingGold.setSelected(SettingsHandler.isMaxStartingGold());
//		maxStartingGold.setMnemonic('C');
/////////////////////////////////////////////////

		houseMenu.add(treatInHandAsEquippedForAttacks);
		//Utility.setDescription(treatInHandAsEquippedForAttacks, "");
		treatInHandAsEquippedForAttacks.addActionListener(checkBoxHandler);
		treatInHandAsEquippedForAttacks.setSelected(SettingsHandler.getTreatInHandAsEquippedForAttacks());
		treatInHandAsEquippedForAttacks.setMnemonic('E');

		//grittyAC.setEnabled(false);
		//grittyHP.setEnabled(false);

		//houseMenu.add(grittyAC);
		//grittyAC.addActionListener(checkBoxHandler);
		//houseMenu.add(grittyHP);
		//grittyHP.addActionListener(checkBoxHandler);

		houseMenu.add(ignoreLevelCap);
		ignoreLevelCap.addActionListener(checkBoxHandler);
		ignoreLevelCap.setSelected(SettingsHandler.isIgnoreLevelCap());
		ignoreLevelCap.setMnemonic('g');
	}

	private void addEquipMenu(JMenu parent)
	{
		JMenu equipMenu = (JMenu)parent.add(Utility.createMenu("Equipment", 'E', "Settings for equipment", null, true));

		equipMenu.add(allowMetamagicInEqBuilder);
		Utility.setDescription(allowMetamagicInEqBuilder, "Allow Metamagic feats on potions, wands, etc.");
		allowMetamagicInEqBuilder.addActionListener(checkBoxHandler);
		allowMetamagicInEqBuilder.setSelected(SettingsHandler.isMetamagicAllowedInEqBuilder());
		allowMetamagicInEqBuilder.setMnemonic('M');

		wandSpellLevel = new JRadioButtonMenuItem[10];
		potionSpellLevel = new JRadioButtonMenuItem[10];

		for (int i = 0; i < 10; ++i)
		{
			wandSpellLevel[i] = new JRadioButtonMenuItem();
			potionSpellLevel[i] = new JRadioButtonMenuItem();

			final String aString = Integer.toString(i);

			wandSpellLevel[i].setText(aString);
			potionSpellLevel[i].setText(aString);
		}
		wandSpellLevel[SettingsHandler.getMaxWandSpellLevel()].setSelected(true);
		potionSpellLevel[SettingsHandler.getMaxPotionSpellLevel()].setSelected(true);

		equipMenu.add(autoCreateMethods);
		autoCreateMethods.setMnemonic('C');

		final boolean isSelected = SettingsHandler.wantToLoadMasterworkAndMagic();
		SettingsHandler.setWantToLoadMasterworkAndMagic(false);		// Turn off temporarily so we get current setting

		autoCreateMethods.add(autoMethod0);
		autoMethod0.addActionListener(checkBoxHandler);
		autoMethod0.setSelected(isSelected);
		Utility.setDescription(autoMethod0, "Load from lst files (ignores #; at start of lines)");

		autoCreateMethods.add(autoMethod4);
		autoMethod4.addActionListener(checkBoxHandler);
		autoMethod4.setSelected(pcgen.core.SettingsHandler.getAutogen(Constants.AUTOGEN_EXOTICMATERIAL));
		//Utility.setDescription(autoMethod4, "Create darkwood (wooden), adamantine and mithral (metal) items");
		Utility.setDescription(autoMethod4, "Create items using exotic materials");

		autoCreateMethods.add(autoMethod3);
		autoMethod3.addActionListener(checkBoxHandler);
		autoMethod3.setSelected(pcgen.core.SettingsHandler.getAutogen(Constants.AUTOGEN_MAGIC));
		Utility.setDescription(autoMethod3, "Create +1 to +5 ammo, armor, shields, and weapons");

		autoCreateMethods.add(autoMethod2);
		autoMethod2.addActionListener(checkBoxHandler);
		autoMethod2.setSelected(pcgen.core.SettingsHandler.getAutogen(Constants.AUTOGEN_MASTERWORK));
		Utility.setDescription(autoMethod2, "Create masterwork ammo, armor, shields, and weapons");

		autoCreateMethods.add(autoMethod1);
		autoMethod1.addActionListener(checkBoxHandler);
		autoMethod1.setSelected(pcgen.core.SettingsHandler.getAutogen(Constants.AUTOGEN_RACIAL));
		Utility.setDescription(autoMethod1, "Create racially sized armor, shields, clothing (resizable) for all sizes in race list");

		// Disable other options if 1st is selected
		if (isSelected)
		{
			SettingsHandler.setWantToLoadMasterworkAndMagic(true);
			autoMethod1.setEnabled(!isSelected);
			autoMethod2.setEnabled(!isSelected);
			autoMethod3.setEnabled(!isSelected);
			autoMethod4.setEnabled(!isSelected);
		}

		ButtonGroup potionMethodsGroup = new ButtonGroup();

		equipMenu.add(potionMaxLevel);
		potionMaxLevel.setMnemonic('P');
		for (int i = 0; i < 10; i++)
		{
			potionMethodsGroup.add((AbstractButton)potionMaxLevel.add(potionSpellLevel[i]));
			potionSpellLevel[i].addActionListener(checkBoxHandler);
		}

		ButtonGroup wandMethodsGroup = new ButtonGroup();

		equipMenu.add(wandMaxLevel);
		wandMaxLevel.setMnemonic('W');
		for (int i = 0; i < 10; i++)
		{
			wandMethodsGroup.add((AbstractButton)wandMaxLevel.add(wandSpellLevel[i]));
			wandSpellLevel[i].addActionListener(checkBoxHandler);
		}
	}

	private void addMonsterDefault(JMenu parent)
	{
		parent.add(useMonsterDefault);
		Utility.setDescription(useMonsterDefault, "Use default monster feats and skills");
		useMonsterDefault.addActionListener(checkBoxHandler);
		useMonsterDefault.setSelected(SettingsHandler.isMonsterDefault());
		useMonsterDefault.setMnemonic('D');
	}

	private void addCharMenu(JMenu parent)
	{
		JMenu charMenu = (JMenu)parent.add(Utility.createMenu("Character", 'H', "Settings for character creation", null, true));

		addStatsMenu(charMenu); // ability scores
		addHPMenu(charMenu); // hit points
		addHouseMenu(charMenu); // house rules
		addMonsterDefault(charMenu); // use default monsters
	}
*/

/*
  -- Moved to the preferences dialog

	private void addLAFMenu(JMenu parent)
	{
		JMenu lafMenu = (JMenu)parent.add(Utility.createMenu("Appearance", 'A', "Look, feel, touch", null, true));

		lafMenu.add(chooseColors);
		Utility.setDescription(chooseColors, "Select colors");
		chooseColors.setMnemonic('o');
		chooseColors.add(prereqFailColor);
		chooseColors.add(featAutoColor);
		chooseColors.add(featVirtualColor);
		prereqFailColor.setForeground(new Color(SettingsHandler.getPrereqFailColor()));
		prereqFailColor.addActionListener(colorListener);
		featAutoColor.setForeground(new Color(SettingsHandler.getFeatAutoColor()));
		featAutoColor.addActionListener(colorListener);
		featVirtualColor.setForeground(new Color(SettingsHandler.getFeatVirtualColor()));
		featVirtualColor.addActionListener(colorListener);
		lafMenu.add(chaTabPlacement);
		Utility.setDescription(chaTabPlacement, "Select where the character specific tabs should be (won't affect currently loaded characters)");
		ButtonGroup chaTabPlacementGroup = new ButtonGroup();
		chaTabPlacement.setMnemonic('H');

		chaTabPlacement.add(chaTabTop = Utility.createRadioButtonMenuItem(chaTabPlacementGroup, "Top", chaTabCheckBoxHandler, null, 'T', null, "Display character tabs along the top", "Up16.gif", true));
		chaTabPlacement.add(chaTabBottom = Utility.createRadioButtonMenuItem(chaTabPlacementGroup, "Bottom", chaTabCheckBoxHandler, null, 'B', null, "Display character tabs along the bottom", "Down16.gif", true));
		chaTabPlacement.add(chaTabLeft = Utility.createRadioButtonMenuItem(chaTabPlacementGroup, "Left", chaTabCheckBoxHandler, null, 'L', null, "Display character tabs along the left", "Back16.gif", true));
		chaTabPlacement.add(chaTabRight = Utility.createRadioButtonMenuItem(chaTabPlacementGroup, "Right", chaTabCheckBoxHandler, null, 'R', null, "Display character tabs along the right", "Forward16.gif", true));

		switch (SettingsHandler.getChaTabPlacement())
		{
			case SwingConstants.TOP:
				chaTabTop.setSelected(true);
				break;
			case SwingConstants.LEFT:
				chaTabLeft.setSelected(true);
				break;
			case SwingConstants.BOTTOM:
				chaTabBottom.setSelected(true);
				break;
			case SwingConstants.RIGHT:
				chaTabRight.setSelected(true);
				break;
		}

		lafMenu.add(mainTabPlacement);
		Utility.setDescription(mainTabPlacement, "Select where the tabs should be");
		ButtonGroup mainTabPlacementGroup = new ButtonGroup();
		mainTabPlacement.setMnemonic('M');

		mainTabPlacement.add(mainTabTop = Utility.createRadioButtonMenuItem(mainTabPlacementGroup, "Top", mainTabCheckBoxHandler, null, 'T', null, "Display character tabs along the top", "Up16.gif", true));
		mainTabPlacement.add(mainTabBottom = Utility.createRadioButtonMenuItem(mainTabPlacementGroup, "Bottom", mainTabCheckBoxHandler, null, 'B', null, "Display character tabs along the bottom", "Down16.gif", true));
		mainTabPlacement.add(mainTabLeft = Utility.createRadioButtonMenuItem(mainTabPlacementGroup, "Left", mainTabCheckBoxHandler, null, 'L', null, "Display character tabs along the left", "Back16.gif", true));
		mainTabPlacement.add(mainTabRight = Utility.createRadioButtonMenuItem(mainTabPlacementGroup, "Right", mainTabCheckBoxHandler, null, 'R', null, "Display character tabs along the right", "Forward16.gif", true));

		switch (SettingsHandler.getTabPlacement())
		{
			case SwingConstants.TOP:
				mainTabTop.setSelected(true);
				break;
			case SwingConstants.LEFT:
				mainTabLeft.setSelected(true);
				break;
			case SwingConstants.BOTTOM:
				mainTabBottom.setSelected(true);
				break;
			case SwingConstants.RIGHT:
				mainTabRight.setSelected(true);
				break;
		}

/ * Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
		lafMenu.add(previewTabShown);
		Utility.setDescription(previewTabShown, "<html>Show <b>Preview</b> tab in character's tabs</html>");
		previewTabShown.addActionListener(checkBoxHandler);
		previewTabShown.setSelected(SettingsHandler.isPreviewTabShown());
		previewTabShown.setMnemonic('R');

		lafMenu.add(summaryTabShown);
		Utility.setDescription(summaryTabShown, "<html>Show <b>Summary</b> tab in character's tabs</html>");
		summaryTabShown.addActionListener(checkBoxHandler);
		summaryTabShown.setSelected(SettingsHandler.isSummaryTabShown());
		summaryTabShown.setMnemonic('S');* /

		lafMenu.add(toolTipTextShown);
		Utility.setDescription(toolTipTextShown, "Show tooltips over tables");
		toolTipTextShown.addActionListener(checkBoxHandler);
		toolTipTextShown.setSelected(SettingsHandler.isToolTipTextShown());
		Utility.setToolTipTextShown(toolTipTextShown);
		toolTipTextShown.setMnemonic('T');

		JMenu labelMenu = (JMenu)lafMenu.add(Utility.createMenu("Tab Labels", 'T', "How to display characters", null, true));
		ButtonGroup labelGroup = new ButtonGroup();
		labelGroup.add((AbstractButton)labelMenu.add(displayPlainName));
		displayPlainName.addActionListener(checkBoxHandler);
		displayPlainName.setMnemonic('P');
		labelGroup.add((AbstractButton)labelMenu.add(displayEpicName));
		displayEpicName.addActionListener(checkBoxHandler);
		displayPlainName.setMnemonic('E');
		labelGroup.add((AbstractButton)labelMenu.add(displayRaceName));
		displayRaceName.addActionListener(checkBoxHandler);
		displayRaceName.setMnemonic('R');
		labelGroup.add((AbstractButton)labelMenu.add(displayNetHackName));
		displayNetHackName.addActionListener(checkBoxHandler);
		displayNetHackName.setMnemonic('N');

		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				displayPlainName.setSelected(true);
				break;
			case Constants.DISPLAY_STYLE_NAME_CLASS:
				displayEpicName.setSelected(true);
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE:
				displayRaceName.setSelected(true);
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				displayNetHackName.setSelected(true);
				break;
			default:
				displayPlainName.setSelected(true); // custome broken
		}

		lafMenu.add(showToolbar);
		Utility.setDescription(showToolbar, "Hide/Show the Tool Bar");
		showToolbar.addActionListener(checkBoxHandler);
		showToolbar.setSelected(SettingsHandler.isToolBarShown());
		showToolbar.setMnemonic('b');

		lafMenu.addSeparator();

		lafMenu.add(looknfeel);
		looknfeel.setMnemonic('G');
		laf = new JRadioButtonMenuItem[UIFactory.getLookAndFeelCount()];
		ButtonGroup lafGroup = new ButtonGroup();
		for (int i = 0; i < laf.length; i++)
		{
			laf[i] = new JRadioButtonMenuItem();
			laf[i].setText(UIFactory.getLookAndFeelName(i));
			Utility.setDescription(laf[i], UIFactory.getLookAndFeelTooltip(i));
			laf[i].setMnemonic(laf[i].getText().charAt(0));
			laf[i].addActionListener(lookFeelCheckBoxHandler);
			lafGroup.add((AbstractButton)looknfeel.add(laf[i]));
		}
		int crossIndex = UIFactory.indexOfCrossPlatformLookAndFeel();

		lafGroup.add((AbstractButton)looknfeel.add(skinnedLookFeel));
		Utility.setDescription(skinnedLookFeel, "Sets the look to that of the select themepack (see next option), or to cross platform if there is no themepack selected.");
		skinnedLookFeel.setMnemonic('K');
		skinnedLookFeel.addActionListener(lookFeelCheckBoxHandler);
		if (SettingsHandler.getLookAndFeel() < laf.length)
		{
			laf[SettingsHandler.getLookAndFeel()].setSelected(true);
		}
		else if (SettingsHandler.getLookAndFeel() == laf.length)
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
				skinnedLookFeel.setSelected(true);
			else
				laf[crossIndex].setSelected(true);
		}
		else
		{
			laf[crossIndex].setSelected(true);
		}

		looknfeel.add(themepack);
		Utility.setDescription(themepack, "Change the skin themepack (see http://javootoo.l2fprod.com/plaf/skinlf/index.php for additional skins.)");
		themepack.addActionListener(checkBoxHandler);
		themepack.setMnemonic('T');

		experimentalCursor = new JCheckBoxMenuItem();
		experimentalCursor.setMnemonic('X');
		Utility.setDescription(experimentalCursor, "Warning: may cause excess flakiness");
		experimentalCursor.setText("Use wait cursor");
		experimentalCursor.setSelected(SettingsHandler.getUseExperimentalCursor());
		experimentalCursor.addActionListener(
		  new ActionListener()
		  {
			  public void actionPerformed(ActionEvent e)
			  {
				  SettingsHandler.setUseExperimentalCursor(experimentalCursor.isSelected());
			  }
		  });
		lafMenu.add(experimentalCursor);
	}

	private void addSourceMenu(JMenu parent)
	{
		JMenu sourceMenu = (JMenu)parent.add(Utility.createMenu("Sources", 'S', "Settings how sources are found and used", null, true));

		sourceMenu.add(campLoad);
		Utility.setDescription(campLoad, "Load selected source materials on start");
		campLoad.addActionListener(checkBoxHandler);
		campLoad.setSelected(SettingsHandler.isLoadCampaignsAtStart());
		campLoad.setMnemonic('S');

		sourceMenu.add(charCampLoad);
		Utility.setDescription(charCampLoad, "Load source materials with PC");
		charCampLoad.addActionListener(checkBoxHandler);
		charCampLoad.setSelected(SettingsHandler.isLoadCampaignsWithPC());
		charCampLoad.setMnemonic('P');

		sourceMenu.add(saveCustom);
		Utility.setDescription(saveCustom, "Save custom equipment");
		saveCustom.addActionListener(checkBoxHandler);
		saveCustom.setSelected(SettingsHandler.getSaveCustomEquipment());
		saveCustom.setMnemonic('E');

		sourceMenu.add(showOGL);
		Utility.setDescription(showOGL, "Display OGL at Start. This setting will reset to selected each time you upgrade PCGen");
		showOGL.addActionListener(checkBoxHandler);
		showOGL.setSelected(SettingsHandler.showLicense());
		showOGL.setMnemonic('L');

		sourceMenu.add(sourceOptions);
		ButtonGroup sourceOptionsGroup = new ButtonGroup();
		sourceOptionsGroup.add((AbstractButton)sourceOptions.add(sourceLong));
		Utility.setDescription(sourceLong, "Display Long Source Format");
		sourceLong.addActionListener(checkBoxHandler);
		sourceLong.setSelected(true);
		sourceOptionsGroup.add((AbstractButton)sourceOptions.add(sourcePage));
		Utility.setDescription(sourcePage, "Display Source Pages Only");
		sourcePage.addActionListener(checkBoxHandler);

		sourceOptionsGroup.add((AbstractButton)sourceOptions.add(sourceShort));
		Utility.setDescription(sourceShort, "Display Short Source Format");
		sourceShort.addActionListener(checkBoxHandler);

		sourceOptionsGroup.add((AbstractButton)sourceOptions.add(sourceWeb));
		Utility.setDescription(sourceWeb, "Display Source URL");
		sourceWeb.addActionListener(checkBoxHandler);
	}

	private void addPathMenu(JMenu parent)
	{
		JMenu pathMenu = (JMenu)parent.add(Utility.createMenu("Locations", 'L', "Paths and where to find things", null, true));

		pathMenu.add(browserPath);
		Utility.setDescription(browserPath, "Change external browser path");
		browserPath.addActionListener(checkBoxHandler);
		browserPath.setMnemonic('B');

		pathMenu.add(clearBrowserPath);
		Utility.setDescription(clearBrowserPath, "Reset the browser path to the system default");
		clearBrowserPath.addActionListener(checkBoxHandler);
		clearBrowserPath.setMnemonic('C');

		pathMenu.add(templateEqSet);
		Utility.setDescription(templateEqSet, "Change default EquipSet export template");
		templateEqSet.addActionListener(checkBoxHandler);
		templateEqSet.setMnemonic('Q');

		pathMenu.add(templateDefault);
		Utility.setDescription(templateDefault, "Change default PC export template");
		templateDefault.addActionListener(checkBoxHandler);
		templateDefault.setMnemonic('X');

		pathMenu.addSeparator();

		pathMenu.add(pcgenDataDir);
		Utility.setDescription(pcgenDataDir, "Change pcgen data directory - ONLY DO THIS IF YOU KNOW WHAT YOU ARE DOING");
		pcgenDataDir.addActionListener(checkBoxHandler);
		//Deliberately no mnemonic. This should not be too easy.
		// pcgenDataDir.setMnemonic('D');

		pathMenu.add(pcgenSystemDir);
		Utility.setDescription(pcgenSystemDir, "Change pcgen system directory - ONLY DO THIS IF YOU KNOW WHAT YOU ARE DOING");
		pcgenSystemDir.addActionListener(checkBoxHandler);
		//Deliberately no mnemonic. This should not be too easy.
		// pcgenSystemDir.setMnemonic('S');
	}

	private void addPaperMenu(JMenu parent)
	{
		final int paperCount = Globals.getPaperCount();
		if (paperCount > 0)
		{
			final int currentSelection = Globals.getSelectedPaper();
			parent.add(paperType);
			ButtonGroup paperTypeGroup = new ButtonGroup();
			paperNames = new JRadioButtonMenuItem[paperCount];
			for (int i = 0; i < paperCount; i++)
			{
				paperNames[i] = new JRadioButtonMenuItem();
				paperNames[i].setText(Globals.getPaperInfo(i, Constants.PAPERINFO_NAME));
				String tip = Globals.getPaperInfo(i, Constants.PAPERINFO_WIDTH) + " x " + Globals.getPaperInfo(i, Constants.PAPERINFO_HEIGHT);
				Utility.setDescription(paperNames[i], tip);
				if (i == currentSelection)
				{
					paperNames[i].setSelected(true);
				}
				paperNames[i].addActionListener(paperTypeHandler);
				paperTypeGroup.add((AbstractButton)paperType.add(paperNames[i]));
			}
		}
	}

	private void addLangMenu(JMenu parent)
	{
		parent.add(languageChoice);
		Utility.setDescription(languageChoice, "Select Language");
		languageChoice.add(langEng);
		langEng.addActionListener(langCheckBoxHandler);
		languageChoice.add(langGer);
		langGer.addActionListener(langCheckBoxHandler);
		languageChoice.add(langFre);
		langFre.addActionListener(langCheckBoxHandler);
		langEng.setMnemonic('E');
		langGer.setMnemonic('G');
		langFre.setMnemonic('F');
		langGer.setEnabled(false);
		langFre.setEnabled(false);
		langEng.setSelected(true);
	}

	private void addConfigMenu(JMenu parent)
	{
		JMenu configMenu = (JMenu)parent.add(Utility.createMenu("PCGen", 'P', "Settings for PCGen", null, true));

		addLAFMenu(configMenu);		// appearance
		//addEquipMenu(configMenu);	// equipment
		//addLangMenu(configMenu);	// language
		//addPathMenu(configMenu);	// locations
		//addPaperMenu(configMenu);	// paper type
		//addSourceMenu(configMenu);	// sources
	}
*/
	private void addCampMenu(JMenu parent)
	{
		//Modes menu
		GameModes modesMenu = new GameModes();
		modesMenu.setMnemonic('C'); // renamed Campaign
		parent.add(modesMenu);

	}

	private void addPreferencesMenu(JMenu parent)
	{
		parent.add(Utility.createMenuItem("Preferences...", prefsMenuHandler, null, 'P', null, PropertyFactory.getString("in_preferencesTip"), "Preferences16.gif", true));
	}

	private void jbInit() throws Exception
	{
		addCampMenu(this);	// campaigns
		//addCharMenu(this);	// characters -- Moved to the preferences dialog
		//addConfigMenu(this);	// pcgen -- Moved to the preferences dialog
		addPreferencesMenu(this); // Preferences dialog

		//checkForSelectedStatMode(); -- Moved to the preferences dialog
	}

/*
  -- Moved to the preferences dialog

	private final class ColorListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final JMenuItem source = (JMenuItem)actionEvent.getSource();
			final Color newColor = JColorChooser.showDialog(Globals.getRootFrame(), "Select color for " + source.getText().toLowerCase(), source.getForeground());
			if (newColor != null)
			{
				source.setForeground(newColor);
				if (source == prereqFailColor)
				{
					SettingsHandler.setPrereqFailColor(newColor.getRGB());
				}
				else if (source == featAutoColor)
				{
					SettingsHandler.setFeatAutoColor(newColor.getRGB());
				}
				else if (source == featVirtualColor)
				{
					SettingsHandler.setFeatVirtualColor(newColor.getRGB());
				}
			}
		}
	}

	private final class ChaTabCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();

			if (source == chaTabBottom)
			{
				SettingsHandler.setChaTabPlacement(SwingConstants.BOTTOM);
			}
			else if (source == chaTabLeft)
			{
				SettingsHandler.setChaTabPlacement(SwingConstants.LEFT);
			}
			else if (source == chaTabRight)
			{
				SettingsHandler.setChaTabPlacement(SwingConstants.RIGHT);
			}
			else if (source == chaTabTop)
			{
				SettingsHandler.setChaTabPlacement(SwingConstants.TOP);
			}
			updateUI();
		}
	}

	private final class LangCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();

			if (source == langEng)
			{
				Globals.setLanguage("en");
				Globals.setCountry("US");
			}
			else if (source == langFre)
			{
				Globals.setLanguage("fr");
				Globals.setCountry("FR");
			}
			else if (source == langGer)
			{
				Globals.setLanguage("de");
				Globals.setCountry("DE");
			}
			updateUI();
		}
	}

	private final class LookFeelCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();
			int sourceIndex = laf.length;

			// grab these states here so we can put them back
			// if the user pick Skinned L&F without skinlf.jar installed

			/**
			 * author: Thomas Behr 06-01-02
			 * /
			boolean restore[] = new boolean[laf.length];
			for (int i = 0; i < laf.length; i++)
			{
				restore[i] = laf[i].isSelected();
				if (source.equals(laf[i]))
				{
					sourceIndex = i;
				}
			}
			skinnedLookFeel.setSelected(false);

			if (sourceIndex < laf.length)
			{
				SettingsHandler.setLookAndFeel(sourceIndex);
				UIFactory.setLookAndFeel(sourceIndex);
			}
			else if (source == skinnedLookFeel)
			{
				if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
				{
					if (SettingsHandler.getSkinLFThemePack().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please pick a skinned theme first.", "PCGen", JOptionPane.WARNING_MESSAGE);
					}
					else
					{
						SettingsHandler.setLookAndFeel(laf.length);
						try
						{
							SkinManager.applySkin();
						}
						catch (Exception e) //This is what applySkin actually throws...
						{
							SettingsHandler.setLookAndFeel(sourceIndex);
							UIFactory.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
							JOptionPane.showMessageDialog(null, "There was a problem setting the skinned look and feel.\n" + "I'll use the System one instead.\nError: " + e.toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				else
				{
					Globals.errorPrint(SkinLFResourceChecker.getMissingResourceMessage());

					String missingLibMsg;
					ResourceBundle d_properties;
					try
					{
						d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
						missingLibMsg = d_properties.getString("MissingLibMessage");
					}
					catch (MissingResourceException mrex)
					{
						missingLibMsg = "This feature requires the download of the above mentioned file(s) " +
						  "(http://sourceforge.net/projects/pcgen/).\n" +
						  "Please download and place in the \"lib\" sub-directory of your PCGen installation.\n" +
						  "You must then restart PCGen for full functionality.";
						mrex.printStackTrace();
					}
					finally
					{
						d_properties = null;
					}
					missingLibMsg = missingLibMsg.replace('|', '\n');
					JOptionPane.showMessageDialog(null,
					  SkinLFResourceChecker.getMissingResourceMessage() +
					  "\n" + missingLibMsg,
					  "PCGen",
					  JOptionPane.WARNING_MESSAGE);

					for (int i = 0; i < laf.length; i++)
					{
						laf[i].setSelected(restore[i]);
					}
				}
			}

			updateUI();
		}
	}

	public static void setTabPlacements(int placement)
	{
		SettingsHandler.setTabPlacement(placement);
		Globals.getRootFrame().getBaseTabbedPane().setTabPlacement(placement);
//		PreferencesDialog.setTabPlacement(placement);
	}

	private final class MainTabCheckBoxListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			final Object source = actionEvent.getSource();

			if (source == mainTabBottom)
			{
				setTabPlacements(SwingConstants.BOTTOM);
			}
			else if (source == mainTabLeft)
			{
				setTabPlacements(SwingConstants.LEFT);
			}
			else if (source == mainTabRight)
			{
				setTabPlacements(SwingConstants.RIGHT);
			}
			else if (source == mainTabTop)
			{
				setTabPlacements(SwingConstants.TOP);
			}
			updateUI();
		}
	}
*/

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
			statusBar.updateUI();

			PreferencesDialog.show(Globals.getRootFrame());

			statusBar.setText(oldStatus);
		}
	}

	/*
	  -- Moved to the preferences dialog
	public static void initializePurchaseModeMethods()
	{
		final int rollMethod = SettingsHandler.getRollMethod();
		final String methodName = SettingsHandler.getPurchaseModeMethodName();

		final int purchaseMethodCount = SettingsHandler.getPurchaseMethodCount();
		purchaseMethods.removeAll();
		if (purchaseMethodCount != 0)
		{
			pmodeButton = new JRadioButtonMenuItem[purchaseMethodCount];
			final boolean bAllowed = SettingsHandler.isPurchaseStatModeAllowed();
			for (int i = 0; i < purchaseMethodCount; ++i)
			{
				final PointBuyMethod pbm = SettingsHandler.getPurhaseMethod(i);
				pmodeButton[i] = new JRadioButtonMenuItem(pbm.getMethodName() + " (" + pbm.getPoints() + ")");
				purchaseMethods.add(pmodeButton[i]);
				if ((rollMethod == Constants.ROLLINGMETHOD_PURCHASE) && (pbm.getMethodName().equalsIgnoreCase(methodName)))
				{
					pmodeButton[i].setSelected(true);
				}
				pmodeButton[i].setEnabled(bAllowed);
				pmodeButton[i].addActionListener(allStatsHandler);
			}
			purchaseMethods.addSeparator();
		}
		purchaseMethods.add(purchaseMode);
		Utility.setDescription(purchaseMode, "Change stat costs for point system");
		purchaseMode.addActionListener(allStatsHandler);
		purchaseMode.setSelected(SettingsHandler.isPurchaseStatMode());
		purchaseMode.setMnemonic('P');
		updateStatMethodGroup();
	}

	public static void initializeStatRollingMethod8()
	{
		method8_ = new JRadioButtonMenuItem[STATMAX - STATMIN + 1];

		method8.removeAll();

		for (int i = 0; i < method8_.length; i++)
		{
			method8_[i] = new JRadioButtonMenuItem(Integer.toString(i + STATMIN));
			method8.add(method8_[i]);
			Utility.setDescription(method8_[i], "All " + (i + STATMIN) + "'s");
			method8_[i].addActionListener(allStatsHandler);
		}

		final int i = Math.min(STATMAX, SettingsHandler.getAllStatsValue());
		SettingsHandler.setAllStatsValue(i);
		if (SettingsHandler.getRollMethod() == Constants.ROLLINGMETHOD_ALLSAME)
		{
			method8_[i - STATMIN].setSelected(true);
		}
		updateStatMethodGroup();
	}

	/**
	 * This class is used to respond to stat roll method selection
	 * /
	private final class StatRollMethodListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();
/////////////////////////////////////////////////
// Yanked for WotC compliance
//			int rollMethod = 0;
//
			if (source == method1)
			{
				//rollMethod = 1;
				SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_STANDARD);
				Globals.getRootFrame().forceUpdate_InfoAbilities();
			}
//			else if (source == method2)
//			{
//				rollMethod = 2;
//			}
//			else if (source == method3)
//			{
//				rollMethod = 3;
//			}
//			else if (source == method4)
//			{
//				rollMethod = 4;
//			}
//			else if (source == method5)
//			{
//				rollMethod = 5;
//			}
//			else if (source == method6)
//			{
//				rollMethod = 6;
//			}
//			else if (source == method7)
//			{
//				rollMethod = 7;
//			}
//			SettingsHandler.setRollMethod(rollMethod);
/////////////////////////////////////////////////
		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 * /
	private static final class AllStatsListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();

			//
			// Configuring of purchase modes, adding/removing purchase methods
			//
			if (source == purchaseMode)
			{
				ShowPurchaseModeConfiguration();
			}
			else
			{
				//
				// Check if selecting a user-defined purchase method
				//
				if(pmodeButton != null) //This only hides the symptom
				{
					for (int i = 0; i < pmodeButton.length; ++i)
					{
						if (pmodeButton[i] == source)
						{
							String methodName = pmodeButton[i].getText();
							final int idx = methodName.lastIndexOf(" (");
							if (idx >= 0)
							{
								SettingsHandler.setPurchaseMethodName(methodName.substring(0, idx));
								//Globals.getRootFrame().forceUpdate_InfoAbilities();
								Globals.getRootFrame().forceUpdate_InfoSummary();
							}
							return;
						}
					}
				}

				//
				// Check to see if selecting "set all stats to #"
				//
				for (int i = 0; i < method8_.length; ++i)
				{
					if (method8_[i] != source)
					{
						continue;
					}

					SettingsHandler.setAllStatsValue(i + STATMIN);
				}
				SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_ALLSAME);
				Globals.getRootFrame().forceUpdate_InfoAbilities();
			}

		}
	}

	/**
	 * This class is used to respond to clicks on the check boxes.
	 * /
	private final class CheckBoxListener implements ActionListener
	{

		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();

			//if (source == grittyHP)
			//	Globals.setGrimHPMode(grittyHP.isSelected());
			//else if (source == grittyAC)
			//	Globals.setGrittyACMode(grittyAC.isSelected());
			if (source == mnuCCSC0)
			{
				SettingsHandler.setIntCrossClassSkillCost(0);
				mnuCCSC1.setSelected(false);
				mnuCCSC2.setSelected(false);
			}
			else if (source == mnuCCSC1)
			{
				SettingsHandler.setIntCrossClassSkillCost(1);
				mnuCCSC0.setSelected(false);
				mnuCCSC2.setSelected(false);
			}
			else if (source == mnuCCSC2)
			{
				SettingsHandler.setIntCrossClassSkillCost(2);
				mnuCCSC0.setSelected(false);
				mnuCCSC1.setSelected(false);
			}
			else if (source == mnuBypassFeatPreReqs)
			{
				SettingsHandler.setBoolBypassFeatPreReqs(mnuBypassFeatPreReqs.isSelected());
			}
			else if (source == mnuBypassClassPreReqs)
			{
				SettingsHandler.setBoolBypassClassPreReqs(mnuBypassClassPreReqs.isSelected());
			}
			else if (source == mnuBypassMaxSkillRank)
			{
				SettingsHandler.setBoolBypassMaxSkillRank(mnuBypassMaxSkillRank.isSelected());
			}
			else if (source == campLoad)
			{
				SettingsHandler.setLoadCampaignsAtStart(campLoad.isSelected());
			}
			else if (source == charCampLoad)
			{
				SettingsHandler.setLoadCampaignsWithPC(charCampLoad.isSelected());
			}
			else if (source == saveCustom)
			{
				SettingsHandler.setSaveCustomEquipment(saveCustom.isSelected());
			}
			else if (source == showOGL)
			{
				SettingsHandler.setShowLicense(showOGL.isSelected());
			}
			else if (source == browserPath)
			{
				Utility.selectDefaultBrowser(getParent().getParent());	//ugly, but it works
			}
			else if (source == clearBrowserPath)
			{
				// If none is set, there is nothing to clear
				if (SettingsHandler.getBrowserPath() == null) return;
				final int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the default browser?", "Clear default browser", JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION)
				{
					SettingsHandler.setBrowserPath(null);
				}
			}
			else if (source == pcgenDataDir)
			{
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Find the new pcgen data directory .");
				if (System.getProperty("os.name").startsWith("Mac OS"))
				{
					// On MacOS X, do not traverse file bundles
					fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
				}

				if (SettingsHandler.getPccFilesLocation() == null)
				{
					//No action, as we have no idea what a good default would be...
				}
				else
				{
					fc.setCurrentDirectory(SettingsHandler.getPccFilesLocation());
				}
				final int returnVal = fc.showOpenDialog(getParent().getParent()); //ugly, but it works
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					final File file = fc.getSelectedFile();
					SettingsHandler.setPccFilesLocation(file);
				}
			}
			else if (source == pcgenSystemDir)
			{
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setDialogTitle("Find the new pcgen system directory.");
				if (System.getProperty("os.name").startsWith("Mac OS"))
				{
					// On MacOS X, do not traverse file bundles
					fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
				}

				if (SettingsHandler.getPcgenSystemDir() == null)
				{
					//No action, as we have no idea what a good default would be...
				}
				else
				{
					fc.setCurrentDirectory(SettingsHandler.getPcgenSystemDir());
				}
				final int returnVal = fc.showOpenDialog(getParent().getParent()); //ugly, but it works
				if (returnVal == JFileChooser.APPROVE_OPTION)
				{
					final File file = fc.getSelectedFile();
					SettingsHandler.setPcgenSystemDir(file);
				}
			}
			else if (source == templateDefault)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Find and select your default PC export template.");
				fc.setCurrentDirectory(SettingsHandler.getTemplatePath());
				fc.setSelectedFile(new File(SettingsHandler.getSelectedTemplate()));
				if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
				{
					File newTemplate = fc.getSelectedFile();
					if (newTemplate.isDirectory() || (!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet")))
					{
						JOptionPane.showMessageDialog(null, "Item selected does not appear to be a template file.  Please try again.", "PCGen", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler.setSelectedTemplate(newTemplate.getAbsolutePath());
							SettingsHandler.setTemplatePath(newTemplate.getParentFile());
						}
						else
						{
							//it must be a psheet
							SettingsHandler.setSelectedPartyTemplate(newTemplate.getAbsolutePath());
							SettingsHandler.setTemplatePath(newTemplate.getParentFile());
						}
					}
				}
			}
			else if (source == templateEqSet)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Find and select your default EquipSet export template.");
				fc.setCurrentDirectory(SettingsHandler.getTemplatePath());
				fc.setSelectedFile(new File(SettingsHandler.getSelectedEqSetTemplate()));
				if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
				{
					File newTemplate = fc.getSelectedFile();
					if (newTemplate.isDirectory() || !newTemplate.getName().startsWith("eqsheet"))
					{
						JOptionPane.showMessageDialog(null, "Item selected does not appear to be a EquipSet template file.  Please try again.", "PCGen", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						//it must be a psheet
						SettingsHandler.setSelectedEqSetTemplate(newTemplate.getAbsolutePath());
						SettingsHandler.setTemplatePath(newTemplate.getParentFile());
					}
				}
			}
			else if (source == sourceLong)
			{
				Globals.setSourceDisplay(Constants.SOURCELONG);
			}
			else if (source == sourceShort)
			{
				Globals.setSourceDisplay(Constants.SOURCESHORT);
			}
			else if (source == sourcePage)
			{
				Globals.setSourceDisplay(Constants.SOURCEPAGE);
			}
			else if (source == sourceWeb)
			{
				Globals.setSourceDisplay(Constants.SOURCEWEB);
			}
			else if (source == themepack)
			{
				JFileChooser fc = new JFileChooser(SettingsHandler.getPcgenThemePackDir());
				fc.setDialogTitle("Find and select your default l2fprod themepack.");
				String theme = SettingsHandler.getSkinLFThemePack();
				if (theme.length() > 0)
				{
					fc.setCurrentDirectory(new File(SettingsHandler.getSkinLFThemePack()));
					fc.setSelectedFile(new File(SettingsHandler.getSkinLFThemePack()));
				}

				fc.addChoosableFileFilter(new ThemePackFilter());
				if (fc.showOpenDialog(getParent().getParent()) == JFileChooser.APPROVE_OPTION) //ugly, but it works
				{
					File newTheme = fc.getSelectedFile();
					if (newTheme.isDirectory() || (!newTheme.getName().endsWith("themepack.zip")))
					{
						JOptionPane.showMessageDialog(null, "Item selected does not appear to be a themepack file.  Please try again.", "PCGen", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						SettingsHandler.setSkinLFThemePack(newTheme.getAbsolutePath());
						if (SettingsHandler.getLookAndFeel() == laf.length)
						{
							try
							{
								SkinManager.applySkin();
							}
							catch (Exception e) //This is what applySkin actually throws...
							{
								//I can't think of anything better to do.
								UIFactory.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
								JOptionPane.showMessageDialog(null, "There was a problem setting the skinned look and feel.\n" +
								  "The look and feel has been reset to cross-platform.\nError: " + e.toString(),
								  "PCGen", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
			else if (source == showToolbar)
			{
				SettingsHandler.setToolBarShown(showToolbar.isSelected());
				((PCGen_Frame1)Globals.getRootFrame()).showToolBar();
			}
			//else if (source == purchaseMode)
			//{
			//	ShowPurchaseModeConfiguration();
			//}
			else if (source == useMonsterDefault)
			{
				SettingsHandler.setMonsterDefault(useMonsterDefault.isSelected());
			}
			else if (source == maxHpAtFirstLevel)
			{
				SettingsHandler.setHPMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());
			}
/////////////////////////////////////////////////
// Yanked for WotC compliance
//			else if (source == maxStartingGold)
//			{
//				SettingsHandler.setMaxStartingGold(maxStartingGold.isSelected());
//			}
/////////////////////////////////////////////////
			else if (source == skillIncrementBefore)
			{
				SettingsHandler.setSkillIncrementBefore(skillIncrementBefore.isSelected());
			}
			else if (source == freeClothesAtFirst)
			{
				SettingsHandler.setFreeClothesAtFirst(freeClothesAtFirst.isSelected());
			}
			else if (source == hpStandard)
			{
				SettingsHandler.setHPRollMethod(Constants.s_HP_STANDARD);
			}
			else if (source == hpAutomax)
			{
				SettingsHandler.setHPRollMethod(Constants.s_HP_AUTOMAX);
			}
/////////////////////////////////////////////////
// Yanked for WotC compliance
//			else if (source == hpPercentage)
//			{
//				SettingsHandler.setHPRollMethod(Constants.s_HP_PERCENTAGE);
//			}
//			else if (source == hpLivingCity)
//			{
//				SettingsHandler.setHPRollMethod(Constants.s_HP_LIVING_CITY);
//			}
//			else if (source == hpLivingGreyhawk)
//			{
//				SettingsHandler.setHPRollMethod(Constants.s_HP_LIVING_GREYHAWK);
//			}
/////////////////////////////////////////////////
			else if (source == toolTipTextShown)
			{
				SettingsHandler.setToolTipTextShown(toolTipTextShown.isSelected());
				Utility.handleToolTipShownStateChange();
				Globals.getRootFrame().forceUpdate_PlayerTabs();
			}
/* Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
			else if (source == summaryTabShown)
			{
				SettingsHandler.setSummaryTabShown(summaryTabShown.isSelected());
			}
			else if (source == previewTabShown)
			{
				SettingsHandler.setPreviewTabShown(previewTabShown.isSelected());
			}* /

			else if (source == autoMethod0)
			{
				final boolean isSelected = ((JCheckBoxMenuItem)source).isSelected();
				SettingsHandler.setWantToLoadMasterworkAndMagic(isSelected);
				autoMethod1.setEnabled(!isSelected);
				autoMethod2.setEnabled(!isSelected);
				autoMethod3.setEnabled(!isSelected);
				autoMethod4.setEnabled(!isSelected);
			}
			else if (source == autoMethod1)
			{
				pcgen.core.SettingsHandler.setAutogen(Constants.AUTOGEN_RACIAL, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == autoMethod2)
			{
				pcgen.core.SettingsHandler.setAutogen(Constants.AUTOGEN_MASTERWORK, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == autoMethod3)
			{
				pcgen.core.SettingsHandler.setAutogen(Constants.AUTOGEN_MAGIC, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == autoMethod4)
			{
				pcgen.core.SettingsHandler.setAutogen(Constants.AUTOGEN_EXOTICMATERIAL, ((JCheckBoxMenuItem)source).isSelected());
			}
			else if (source == applyWeightPenaltyToSkills)
			{
				SettingsHandler.setApplyWeightPenalty(applyWeightPenaltyToSkills.isSelected());
			}
			else if (source == applyLoadPenaltyToACandSkills)
			{
				SettingsHandler.setApplyLoadPenaltyToACandSkills(applyLoadPenaltyToACandSkills.isSelected());
			}
			else if (source == treatInHandAsEquippedForAttacks)
			{
				SettingsHandler.setTreatInHandAsEquippedForAttacks(treatInHandAsEquippedForAttacks.isSelected());
			}
			else if (source == ignoreLevelCap)
			{
				SettingsHandler.setIgnoreLevelCap(ignoreLevelCap.isSelected());
			}
			else if (source == displayPlainName)
			{
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME);
			}
			else if (source == displayEpicName)
			{
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_CLASS);
			}
			else if (source == displayRaceName)
			{
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE);
			}
			else if (source == displayNetHackName)
			{
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE_CLASS);
			}
			else if (source == allowMetamagicInEqBuilder)
			{
				SettingsHandler.setMetamagicAllowedInEqBuilder(allowMetamagicInEqBuilder.isSelected());
			}
			else
			{
				for (int i = 0; i < 10; i++)
				{
					if (source == potionSpellLevel[i])
					{
						SettingsHandler.setMaxPotionSpellLevel(i);
						return;
					}
					else if (source == wandSpellLevel[i])
					{
						SettingsHandler.setMaxWandSpellLevel(i);
						return;
					}
				}
				Globals.errorPrint("Unknown menu item...");
			}
		}
	}

	/**
	 * This class is used to respond to clicks on the paper types.
	 * /
	private final class PaperTypeListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			Object source = actionEvent.getSource();

			if (paperNames != null)
			{
				for (int i = 0; i < paperNames.length; i++)
				{
					if (source == paperNames[i])
					{
						// Attempt to choose paper, turn off selection if cannot select.
						if (!Globals.selectPaper(paperNames[i].getText()))
						{
							paperNames[i].setSelected(false);
						}

					}

				}
			}
		}
	}

	final class ThemePackFilter extends FileFilter
	{

		// Accept all directories and themepack.zip files.
		public boolean accept(File f)
		{
			if (f.isDirectory())
			{
				return true;
			}

			if (f.getName().endsWith("themepack.zip"))
			{
				return true;
			}

			return false;
		}

		// The description of this filter
		public String getDescription()
		{
			return "Themepacks (*themepack.zip)";
		}
	}

	private static void ShowPurchaseModeConfiguration()
	{
		//Create and display purchasemodestats popup frame.
		if (pmsFrame == null)
		{
			//pmsFrame = new PurchaseModeStats();
			pmsFrame = new PurchaseModeFrame();

			// add a listener to know when the window has closed
			pmsFrame.addWindowListener(new WindowAdapter()
			{
				public void windowClosed(WindowEvent e)
				{
					// free resources
					pmsFrame = null;
				}
			});
		}

		Utility.centerDialog(pmsFrame);

		// ensure the frame is visible (in case user selects menu item again).
		pmsFrame.setVisible(true);
	}
*/
}
