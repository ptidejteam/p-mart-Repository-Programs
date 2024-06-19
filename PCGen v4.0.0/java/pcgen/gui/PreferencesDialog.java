/*
 * PreferencesDialog.java
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
 * $Id: PreferencesDialog.java,v 1.1 2006/02/21 00:47:12 vauchers Exp $
 */

package pcgen.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PointBuyMethod;
import pcgen.core.SettingsHandler;
import pcgen.util.SkinLFResourceChecker;

/**
 *  PCGen preferences dialog
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

class PreferencesDialog extends JDialog
{
	//
	// Used to create the entries for the "set all stats to" menu
	private static final int STATMIN = 3;
	private static final int STATMAX = 18;

	// Used to create the entries for the max spell level combos
	private static final int SPELLLVLMIN = 0;
	private static final int SPELLLVLMAX = 9;

	private DefaultTreeModel settingsModel;
	private JTree settingsTree;
	private JPanel settingsPanel;
	private JScrollPane settingsScroll;
	private JPanel controlPanel;

	// Character Settings
	private JPanel characterPanel = new JPanel();

	// Abilities
	private JRadioButton abilitiesUserRolledButton = new JRadioButton("User Rolled");
	private JRadioButton abilitiesAllSameButton = new JRadioButton("All the Same");
	private JComboBox abilityScoreCombo;
	private String allSameValue [] = new String[STATMAX - STATMIN + 1];
	private JComboBox abilityPurchaseModeCombo;
	private String pMode [];
	private String pModeMethodName[];
	private JRadioButton abilitiesPurchasedButton = new JRadioButton("Purchase Mode");
	private JButton purchaseMode = new JButton("Purchase Mode Configuration...");
	private PurchaseModeFrame pmsFrame = null;

	// "HP Roll Methods"
	private JRadioButton hpAutomax = new JRadioButton("Always Maximum");
	private JRadioButton hpStandard = new JRadioButton("User Rolled");
	private JCheckBox maxHpAtFirstLevel = new JCheckBox();

	// "House Rules"
	private JCheckBox applyLoadPenaltyToACandSkills = new JCheckBox();
	private JCheckBox applyWeightPenaltyToSkills = new JCheckBox();
	private JCheckBox freeClothesAtFirst = new JCheckBox();
	private JCheckBox bypassClassPreReqsCheckbox = new JCheckBox();
	private JCheckBox bypassFeatPreReqsCheckbox = new JCheckBox();
	private JCheckBox bypassMaxSkillRankCheckbox = new JCheckBox();
	private JComboBox crossClassSkillCostCombo = new JComboBox(new String[] {"0  ", "1  ", "2  "});
	private JCheckBox skillIncrementBefore = new JCheckBox();
	private JCheckBox treatInHandAsEquippedForAttacks = new JCheckBox();
	private JCheckBox ignoreLevelCap = new JCheckBox();

	// "Monsters"
	private JCheckBox useMonsterDefault = new JCheckBox();


	// Colors
	private JButton prereqFailColor;
	private JButton featAutoColor;
	private JButton featVirtualColor;

	// Tab Options
	private JComboBox mainTabPlacementCombo;
	private JComboBox charTabPlacementCombo;
	private JComboBox tabLabelsCombo;
	private JCheckBox displayAbilitiesAsTab = new JCheckBox();
// Mynex wants these options eliminated, so I'll comment them out (in the event they ever decide to return)
//	private JCheckBox previewTabShown = new JCheckBox("<html>Show <b>Preview</b> tab</html>");
//	private JCheckBox summaryTabShown = new JCheckBox("<html>Show <b>Summary</b> tab</html>");

	// Displayed
	private JCheckBox toolTipTextShown = new JCheckBox();
	private JCheckBox showToolbar = new JCheckBox();
	private JCheckBox experimentalCursor = new JCheckBox();

	// Look and Feel
	private JRadioButton laf[];
	private JRadioButton skinnedLookFeel = new JRadioButton();
	private JButton themepack;

	// Level Up
	private JCheckBox hpDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox featDialogShownAtLevelUp = new JCheckBox();
	private JCheckBox statDialogShownAtLevelUp = new JCheckBox();

	// Equipment
	private JCheckBox allowMetamagicInEqBuilder = new JCheckBox();
	private JComboBox potionMaxLevel = new JComboBox();
	private static String potionSpellLevel[] = new String[SPELLLVLMAX - SPELLLVLMIN + 1];
	private JComboBox wandMaxLevel = new JComboBox();
	private static String wandSpellLevel[] = new String[SPELLLVLMAX - SPELLLVLMIN + 1];
	private JRadioButton noAutoEquipCreate;
	private JRadioButton autoEquipCreate;
	private JCheckBox autoMethod1 = new JCheckBox();
	private JCheckBox autoMethod2 = new JCheckBox();
	private JCheckBox autoMethod3 = new JCheckBox();
	private JCheckBox autoMethod4 = new JCheckBox();

	// Language
	private JPanel languageChoice = new JPanel(); //("Language");
	private JRadioButton langEng;
	private JRadioButton langGer;
	private JRadioButton langFre;

	// Location
	private JButton browserPath;
	private JButton clearBrowserPath;
	private JButton pcgenDataDir;
	private JButton pcgenSystemDir;

	// Output
	private JButton templateDefault;
	private JButton templateEqSet;
	private JComboBox paperType;
	private String paperNames[] = null;

	// Sources
	private JCheckBox campLoad = new JCheckBox();
	private JCheckBox charCampLoad = new JCheckBox();
	private JCheckBox saveCustom = new JCheckBox();
	private JCheckBox showOGL = new JCheckBox();
	private JComboBox sourceOptions = new JComboBox();



	// Listeners
	private PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();
	//private ColorListener colorListener = new ColorListener();

	// Resource strings
	private static String in_dialogTitle;
	private static String in_abilitiesUserRolledLabel;
	private static String in_abilitiesAllSameLabel;
	private static String in_abilitiesPurchasedLabel;
	private static String in_purchaseModeConfigLabel;
	private static String in_hpAutoMaxLabel;
	private static String in_hpStandardLabel;
	private static String in_color;
	private static String in_colorPrereqFail;
	private static String in_colorAutoFeat;
	private static String in_colorVirtFeat;
	private static String in_tabs;
	private static String in_mainTabPlacement;
	private static String in_charTabPlacement;
	private static String in_charTabLabel;
	private static String in_tabLabelPlain;
	private static String in_tabLabelEpic;
	private static String in_tabLabelRace;
	private static String in_tabLabelNetHack;
	private static String in_tabPosTop;
	private static String in_tabPosBottom;
	private static String in_tabPosLeft;
	private static String in_tabPosRight;
	private static String in_tabAbilities;
	private static String in_displayOpts;
	private static String in_showToolTips;
	private static String in_showToolBar;
	private static String in_useAutoWaitCursor;
	private static String in_lookAndFeel;
	private static String in_skinnedLAF;
	private static String in_chooseSkin;
	private static String in_levelUp;
	private static String in_hpWindow;
	private static String in_featWindow;
	private static String in_statWindow;
	private static String in_equipment;
	private static String in_allowMetamagic;
	private static String in_potionMax;
	private static String in_wandMax;
	private static String in_anyAutoEquip;
	private static String in_noAutoEquip;
	private static String in_autoEquip;
	private static String in_autoEquipRace;
	private static String in_autoEquipMasterwork;
	private static String in_autoEquipMagic;
	private static String in_autoEquipExotic;
	private static String in_language;
	private static String in_langEnglish;
	private static String in_langGerman;
	private static String in_langFrench;
	private static String in_location;
	private static String in_browserPath;
	private static String in_clearBrowserPath;
	private static String in_pcgenDataDir;
	private static String in_pcgenSystemDir;
	private static String in_output;
	private static String in_templateDefault;
	private static String in_templateEqSet;
	private static String in_paperType;
	private static String in_sources;
	private static String in_autoLoadAtStart;
	private static String in_autoLoadWithPC;
	private static String in_saveCustom;
	private static String in_displayOGL;
	private static String in_sourceDisplay;
	private static String in_sdLong;
	private static String in_sdPage;
	private static String in_sdShort;
	private static String in_sdWeb;

	/**
	 * Resource bundles
	 */
	static
	{
		ResourceBundle prefsDialogProperties;
		Locale currentLocale = new Locale(Globals.getLanguage(), Globals.getCountry());
		try
		{
			prefsDialogProperties = ResourceBundle.getBundle("pcgen/gui/prop/LanguageBundle", currentLocale);
			in_dialogTitle = prefsDialogProperties.getString("in_Prefs_title");
			in_abilitiesUserRolledLabel = prefsDialogProperties.getString("in_Prefs_abilitiesUserRolled");
			in_abilitiesAllSameLabel = prefsDialogProperties.getString("in_Prefs_abilitiesAllSame");
			in_abilitiesPurchasedLabel = prefsDialogProperties.getString("in_Prefs_abilitiesPurchased");
			in_purchaseModeConfigLabel = prefsDialogProperties.getString("in_Prefs_purchaseModeConfig");
			in_hpAutoMaxLabel = prefsDialogProperties.getString("in_Prefs_hpAutoMax");
			in_hpStandardLabel = prefsDialogProperties.getString("in_Prefs_hpStandard");
			in_color = prefsDialogProperties.getString("in_Prefs_color");
			in_colorPrereqFail = prefsDialogProperties.getString("in_Prefs_colorPrereqFail");
			in_colorAutoFeat = prefsDialogProperties.getString("in_Prefs_colorAutoFeat");
			in_colorVirtFeat = prefsDialogProperties.getString("in_Prefs_colorVirtFeat");
			in_tabs = prefsDialogProperties.getString("in_Prefs_tabs");
			in_mainTabPlacement = prefsDialogProperties.getString("in_Prefs_mainTabPlacement");
			in_charTabPlacement = prefsDialogProperties.getString("in_Prefs_charTabPlacement");
			in_charTabLabel = prefsDialogProperties.getString("in_Prefs_charTabLabel");
			in_tabLabelPlain = prefsDialogProperties.getString("in_Prefs_tabLabelPlain");
			in_tabLabelEpic = prefsDialogProperties.getString("in_Prefs_tabLabelEpic");
			in_tabLabelRace = prefsDialogProperties.getString("in_Prefs_tabLabelRace");
			in_tabLabelNetHack = prefsDialogProperties.getString("in_Prefs_tabLabelNetHack");
			in_tabPosTop = prefsDialogProperties.getString("in_Prefs_tabPosTop");
			in_tabPosBottom = prefsDialogProperties.getString("in_Prefs_tabPosBottom");
			in_tabPosLeft = prefsDialogProperties.getString("in_Prefs_tabPosLeft");
			in_tabPosRight = prefsDialogProperties.getString("in_Prefs_tabPosRight");
			in_tabAbilities = prefsDialogProperties.getString("in_Prefs_tabAbilities");
			in_displayOpts = prefsDialogProperties.getString("in_Prefs_displayOpts");
			in_showToolTips = prefsDialogProperties.getString("in_Prefs_showToolTips");
			in_showToolBar = prefsDialogProperties.getString("in_Prefs_showToolBar");
			in_useAutoWaitCursor = prefsDialogProperties.getString("in_Prefs_useAutoWaitCursor");
			in_lookAndFeel = prefsDialogProperties.getString("in_Prefs_lookAndFeel");
			in_skinnedLAF = prefsDialogProperties.getString("in_Prefs_skinnedLAF");
			in_chooseSkin = prefsDialogProperties.getString("in_Prefs_chooseSkin");
			in_levelUp = prefsDialogProperties.getString("in_Prefs_levelUp");
			in_hpWindow = prefsDialogProperties.getString("in_Prefs_hpWindow");
			in_featWindow = prefsDialogProperties.getString("in_Prefs_featWindow");
			in_statWindow = prefsDialogProperties.getString("in_Prefs_statWindow");
			in_equipment = prefsDialogProperties.getString("in_Prefs_equipment");
			in_allowMetamagic = prefsDialogProperties.getString("in_Prefs_allowMetamagic");
			in_potionMax = prefsDialogProperties.getString("in_Prefs_potionMax");
			in_wandMax = prefsDialogProperties.getString("in_Prefs_wandMax");
			in_anyAutoEquip = prefsDialogProperties.getString("in_Prefs_anyAutoEquip");
			in_noAutoEquip = prefsDialogProperties.getString("in_Prefs_noAutoEquip");
			in_autoEquip = prefsDialogProperties.getString("in_Prefs_autoEquip");
			in_autoEquipRace = prefsDialogProperties.getString("in_Prefs_autoEquipRace");
			in_autoEquipMasterwork = prefsDialogProperties.getString("in_Prefs_autoEquipMasterwork");
			in_autoEquipMagic = prefsDialogProperties.getString("in_Prefs_autoEquipMagic");
			in_autoEquipExotic = prefsDialogProperties.getString("in_Prefs_autoEquipExotic");
			in_language = prefsDialogProperties.getString("in_Prefs_language");
			in_langEnglish = prefsDialogProperties.getString("in_Prefs_langEnglish");
			in_langGerman = prefsDialogProperties.getString("in_Prefs_langGerman");
			in_langFrench = prefsDialogProperties.getString("in_Prefs_langFrench");
			in_location = prefsDialogProperties.getString("in_Prefs_location");
			in_browserPath = prefsDialogProperties.getString("in_Prefs_browserPath");
			in_clearBrowserPath = prefsDialogProperties.getString("in_Prefs_clearBrowserPath");
			in_pcgenDataDir = prefsDialogProperties.getString("in_Prefs_pcgenDataDir");
			in_pcgenSystemDir = prefsDialogProperties.getString("in_Prefs_pcgenSystemDir");
			in_output = prefsDialogProperties.getString("in_Prefs_output");
			in_templateDefault = prefsDialogProperties.getString("in_Prefs_templateDefault");
			in_templateEqSet = prefsDialogProperties.getString("in_Prefs_templateEqSet");
			in_paperType = prefsDialogProperties.getString("in_Prefs_paperType");
			in_sources = prefsDialogProperties.getString("in_Prefs_sources");
			in_autoLoadAtStart = prefsDialogProperties.getString("in_Prefs_autoLoadAtStart");
			in_autoLoadWithPC = prefsDialogProperties.getString("in_Prefs_autoLoadWithPC");
			in_saveCustom = prefsDialogProperties.getString("in_Prefs_saveCustom");
			in_displayOGL = prefsDialogProperties.getString("in_Prefs_displayOGL");
			in_sourceDisplay = prefsDialogProperties.getString("in_Prefs_sourceDisplay");
			in_sdLong = prefsDialogProperties.getString("in_Prefs_sdLong");
			in_sdPage = prefsDialogProperties.getString("in_Prefs_sdPage");
			in_sdShort = prefsDialogProperties.getString("in_Prefs_sdShort");
			in_sdWeb = prefsDialogProperties.getString("in_Prefs_sdWeb");

		}
		catch (MissingResourceException mrex)
		{
			mrex.printStackTrace();
		}
		finally
		{
			prefsDialogProperties = null;
		}
	}

	public static void show(JFrame frame)
	{
		int result;
		PreferencesDialog prefsDialog;

		prefsDialog = new PreferencesDialog(frame, true);

		Utility.centerDialog(prefsDialog);

		prefsDialog.show();
	}

	private PreferencesDialog(JFrame parent, boolean modal)
	{
		super(parent, in_dialogTitle, modal);

		buildSettingsTreeAndPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(settingsScroll, BorderLayout.WEST);
		this.getContentPane().add(settingsPanel, BorderLayout.CENTER);
		this.getContentPane().add(controlPanel, BorderLayout.SOUTH);

		applyOptionValuesToControls();
		settingsTree.setSelectionRow(1);

		pack();
	}

	private void buildSettingsTreeAndPanel()
	{
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
		DefaultMutableTreeNode characterNode;
		DefaultMutableTreeNode pcGenNode;
		DefaultMutableTreeNode appearanceNode;
		DefaultMutableTreeNode bNode;
		JPanel panel;

		// Build the settings panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new CardLayout());

		// Build the selection tree
		characterNode = new DefaultMutableTreeNode("Character");

		characterNode.add(new DefaultMutableTreeNode("Abilities"));
		settingsPanel.add(buildAbilitiesPanel(), "Abilities");

		characterNode.add(new DefaultMutableTreeNode("Hit Points"));
		settingsPanel.add(buildHitPointsPanel(), "Hit Points");

		characterNode.add(new DefaultMutableTreeNode("House Rules"));
		settingsPanel.add(buildHouseRulesPanel(), "House Rules");
		characterNode.add(new DefaultMutableTreeNode("Monsters"));
		settingsPanel.add(buildMonstersPanel(), "Monsters");
		rootNode.add(characterNode);
		pcGenNode = new DefaultMutableTreeNode("PCGen");
		appearanceNode = new DefaultMutableTreeNode("Appearance");
		appearanceNode.add(new DefaultMutableTreeNode(in_color));
		settingsPanel.add(buildColorsPanel(), in_color);
		appearanceNode.add(new DefaultMutableTreeNode(in_tabs));
		settingsPanel.add(buildTabsAppearancePanel(), in_tabs);
		appearanceNode.add(new DefaultMutableTreeNode(in_displayOpts));
		settingsPanel.add(buildDisplayOptionsPanel(), in_displayOpts);
		appearanceNode.add(new DefaultMutableTreeNode(in_lookAndFeel));
		settingsPanel.add(buildLookAndFeelPanel(), in_lookAndFeel);
		appearanceNode.add(new DefaultMutableTreeNode(in_levelUp));
		settingsPanel.add(buildLevelUpPanel(), in_levelUp);
		pcGenNode.add(appearanceNode);
		pcGenNode.add(new DefaultMutableTreeNode(in_equipment));
		settingsPanel.add(buildEquipmentPanel(), in_equipment);
		pcGenNode.add(new DefaultMutableTreeNode(in_language));
		settingsPanel.add(buildLanguagePanel(), in_language);
		pcGenNode.add(new DefaultMutableTreeNode(in_location));
		settingsPanel.add(buildLocationPanel(), in_location);
		pcGenNode.add(new DefaultMutableTreeNode(in_output));
		settingsPanel.add(buildOutputPanel(), in_output);
		pcGenNode.add(new DefaultMutableTreeNode(in_sources));
		settingsPanel.add(buildSourcesPanel(), in_sources);
		rootNode.add(pcGenNode);

		settingsModel = new DefaultTreeModel(rootNode);
		settingsTree = new JTree(settingsModel);
		settingsTree.setRootVisible(false);
		settingsTree.setShowsRootHandles(true);
		settingsScroll = new JScrollPane(settingsTree);

		// Expand all of the branch nodes
		settingsTree.expandPath(new TreePath(characterNode.getPath()));
		settingsTree.expandPath(new TreePath(pcGenNode.getPath()));
		settingsTree.expandPath(new TreePath(appearanceNode.getPath()));

		// Add the listener which switches panels when the tree is clicked.
		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = settingsTree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = settingsTree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						CardLayout cl = (CardLayout)(settingsPanel.getLayout());
						Globals.debugPrint("Preferences switching to : " + String.valueOf(selPath.getLastPathComponent()));
						cl.show(settingsPanel, String.valueOf(selPath.getLastPathComponent()));
					}
				}
			}
		};
		settingsTree.addMouseListener(ml);

		// TODO: Add a listener to react to a change of node via the keyboard

		// Build the control panel (OK/Cancel buttons)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton("OK");
		okButton.setMnemonic('O');
		controlPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed(evt);
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic('C');
		controlPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				cancelButtonActionPerformed(evt);
			}
		});
	}

	private void cancelButtonActionPerformed(ActionEvent evt)
	{
		setVisible(false);
		this.dispose();
	}

	private void okButtonActionPerformed(ActionEvent evt)
	{
		setOptionsBasedOnControls();
		setVisible(false);
		this.dispose();
	}

	private void applyOptionValuesToControls()
	{
		// Abilities
		final int rollMethod = SettingsHandler.getRollMethod();
		switch (rollMethod)
		{
			case Constants.ROLLINGMETHOD_STANDARD:
				abilitiesUserRolledButton.setSelected(true);
				break;

			case Constants.ROLLINGMETHOD_ALLSAME:
				abilitiesAllSameButton.setSelected(true);
				break;

			case Constants.ROLLINGMETHOD_PURCHASE:
				abilitiesPurchasedButton.setSelected(true);
				break;

			default:
				// We force the system to have one of these selected.
				abilitiesUserRolledButton.setSelected(true);
				SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_STANDARD);
				break;
		}

		final int allStatsValue = Math.min(STATMAX, SettingsHandler.getAllStatsValue());
		SettingsHandler.setAllStatsValue(allStatsValue);
		abilityScoreCombo.setSelectedIndex(allStatsValue - STATMIN);

		final String methodName = SettingsHandler.getPurchaseModeMethodName();
		for (int i=0;i<pMode.length;i++)
		{
			if (pModeMethodName[i].equals(methodName))
			{
				abilityPurchaseModeCombo.setSelectedIndex(i);
			}
		}

		// Hit Points
		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.s_HP_STANDARD:
			default:
				hpStandard.setSelected(true);
				break;
			case Constants.s_HP_AUTOMAX:
				hpAutomax.setSelected(true);
				break;
		}
		maxHpAtFirstLevel.setSelected(SettingsHandler.isHPMaxAtFirstLevel());

		// House Rules
		applyLoadPenaltyToACandSkills.setSelected(SettingsHandler.isApplyLoadPenaltyToACandSkills());
		applyWeightPenaltyToSkills.setSelected(SettingsHandler.isApplyWeightPenaltyToSkills());
		freeClothesAtFirst.setSelected(SettingsHandler.isFreeClothesAtFirst());
		bypassClassPreReqsCheckbox.setSelected(SettingsHandler.isBoolBypassClassPreReqs());
		bypassFeatPreReqsCheckbox.setSelected(SettingsHandler.isBoolBypassFeatPreReqs());
		bypassMaxSkillRankCheckbox.setSelected(SettingsHandler.isBoolBypassMaxSkillRank());
		crossClassSkillCostCombo.setSelectedIndex(SettingsHandler.getIntCrossClassSkillCost());
		skillIncrementBefore.setSelected(SettingsHandler.isSkillIncrementBefore());
		treatInHandAsEquippedForAttacks.setSelected(SettingsHandler.getTreatInHandAsEquippedForAttacks());
		ignoreLevelCap.setSelected(SettingsHandler.isIgnoreLevelCap());

		// Monsters
		useMonsterDefault.setSelected(SettingsHandler.isMonsterDefault());

		// Colors
		prereqFailColor.setForeground(new Color(SettingsHandler.getPrereqFailColor()));
		featAutoColor.setForeground(new Color(SettingsHandler.getFeatAutoColor()));
		featVirtualColor.setForeground(new Color(SettingsHandler.getFeatVirtualColor()));

		// Tab options
		switch (SettingsHandler.getTabPlacement())
		{
			case SwingConstants.TOP:
				mainTabPlacementCombo.setSelectedIndex(0);
				break;
			case SwingConstants.BOTTOM:
				mainTabPlacementCombo.setSelectedIndex(1);
				break;
			case SwingConstants.LEFT:
				mainTabPlacementCombo.setSelectedIndex(2);
				break;
			case SwingConstants.RIGHT:
				mainTabPlacementCombo.setSelectedIndex(3);
				break;
		}
		switch (SettingsHandler.getChaTabPlacement())
		{
			case SwingConstants.TOP:
				charTabPlacementCombo.setSelectedIndex(0);
				break;
			case SwingConstants.BOTTOM:
				charTabPlacementCombo.setSelectedIndex(1);
				break;
			case SwingConstants.LEFT:
				charTabPlacementCombo.setSelectedIndex(2);
				break;
			case SwingConstants.RIGHT:
				charTabPlacementCombo.setSelectedIndex(3);
				break;
		}
		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				tabLabelsCombo.setSelectedIndex(0);
				break;
			case Constants.DISPLAY_STYLE_NAME_CLASS:
				tabLabelsCombo.setSelectedIndex(1);
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE:
				tabLabelsCombo.setSelectedIndex(2);
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				tabLabelsCombo.setSelectedIndex(3);
				break;
		}
		displayAbilitiesAsTab.setSelected(SettingsHandler.isAbilitiesShownAsATab());

		// Display options
		toolTipTextShown.setSelected(SettingsHandler.isToolTipTextShown());
		showToolbar.setSelected(SettingsHandler.isToolBarShown());
		experimentalCursor.setSelected(SettingsHandler.getUseExperimentalCursor());

		// Look and feel
		int crossIndex = UIFactory.indexOfCrossPlatformLookAndFeel();
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

		// Level up
		hpDialogShownAtLevelUp.setSelected(SettingsHandler.getShowHPDialogAtLevelUp());
		featDialogShownAtLevelUp.setSelected(SettingsHandler.getShowFeatDialogAtLevelUp());
		statDialogShownAtLevelUp.setSelected(SettingsHandler.getShowStatDialogAtLevelUp());

		// Equipment
		allowMetamagicInEqBuilder.setSelected(SettingsHandler.isMetamagicAllowedInEqBuilder());
		potionMaxLevel.setSelectedIndex(SettingsHandler.getMaxPotionSpellLevel()-SPELLLVLMIN);
		wandMaxLevel.setSelectedIndex(SettingsHandler.getMaxWandSpellLevel()-SPELLLVLMIN);
		if (SettingsHandler.wantToLoadMasterworkAndMagic())
		{
			noAutoEquipCreate.setSelected(true);
		}
		else
		{
			autoEquipCreate.setSelected(true);
		}
		SettingsHandler.setWantToLoadMasterworkAndMagic(false);		// Turn off temporarily so we get current setting
		autoMethod1.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_RACIAL));
		autoMethod2.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_MASTERWORK));
		autoMethod3.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_MAGIC));
		autoMethod4.setSelected(SettingsHandler.getAutogen(Constants.AUTOGEN_EXOTICMATERIAL));
		SettingsHandler.setWantToLoadMasterworkAndMagic(noAutoEquipCreate.isSelected());		// Reset its state now we are done

		// Language
		langEng.setSelected(true);

		// Output
		paperType.setSelectedIndex(Globals.getSelectedPaper());

		// Sources
		campLoad.setSelected(SettingsHandler.isLoadCampaignsAtStart());
		charCampLoad.setSelected(SettingsHandler.isLoadCampaignsWithPC());
		saveCustom.setSelected(SettingsHandler.getSaveCustomEquipment());
		showOGL.setSelected(SettingsHandler.showLicense());
		switch (Globals.getSourceDisplay())
		{
			case Constants.SOURCELONG:
				sourceOptions.setSelectedIndex(0);
				break;
			case Constants.SOURCESHORT:
				sourceOptions.setSelectedIndex(1);
				break;
			case Constants.SOURCEPAGE:
				sourceOptions.setSelectedIndex(2);
				break;
			case Constants.SOURCEWEB:
				sourceOptions.setSelectedIndex(3);
				break;
		}
	}

	private void setOptionsBasedOnControls()
	{
		// Abilities
		SettingsHandler.setAllStatsValue(abilityScoreCombo.getSelectedIndex()+STATMIN);
		SettingsHandler.setPurchaseMethodName(pModeMethodName[abilityPurchaseModeCombo.getSelectedIndex()]);
		if (abilitiesUserRolledButton.isSelected())
		{
			SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_STANDARD);
		}
		else if (abilitiesAllSameButton.isSelected())
		{
			SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_ALLSAME);
		}
		else if (abilitiesPurchasedButton.isSelected())
		{
			SettingsHandler.setRollMethod(Constants.ROLLINGMETHOD_PURCHASE);
		}

		// Hit points
		if (hpStandard.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.s_HP_STANDARD);
		}
		else if (hpAutomax.isSelected())
		{
			SettingsHandler.setHPRollMethod(Constants.s_HP_AUTOMAX);
		}
		SettingsHandler.setHPMaxAtFirstLevel(maxHpAtFirstLevel.isSelected());

		// House Rules
		SettingsHandler.setApplyLoadPenaltyToACandSkills(applyLoadPenaltyToACandSkills.isSelected());
		SettingsHandler.setApplyWeightPenaltyToSkills(applyWeightPenaltyToSkills.isSelected());
		SettingsHandler.setFreeClothesAtFirst(freeClothesAtFirst.isSelected());
		SettingsHandler.setBoolBypassClassPreReqs(bypassClassPreReqsCheckbox.isSelected());
		SettingsHandler.setBoolBypassFeatPreReqs(bypassFeatPreReqsCheckbox.isSelected());
		SettingsHandler.setBoolBypassMaxSkillRank(bypassMaxSkillRankCheckbox.isSelected());
		SettingsHandler.setIntCrossClassSkillCost(crossClassSkillCostCombo.getSelectedIndex());
		SettingsHandler.setSkillIncrementBefore(skillIncrementBefore.isSelected());
		SettingsHandler.setTreatInHandAsEquippedForAttacks(treatInHandAsEquippedForAttacks.isSelected());
		SettingsHandler.setIgnoreLevelCap(ignoreLevelCap.isSelected());

		// Monsters
		SettingsHandler.setMonsterDefault(useMonsterDefault.isSelected());

		// Tab Options
		switch (mainTabPlacementCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setTabPlacement(SwingConstants.TOP);
				break;
			case 1:
				SettingsHandler.setTabPlacement(SwingConstants.BOTTOM);
				break;
			case 2:
				SettingsHandler.setTabPlacement(SwingConstants.LEFT);
				break;
			case 3:
				SettingsHandler.setTabPlacement(SwingConstants.RIGHT);
				break;
		}
		switch (charTabPlacementCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setChaTabPlacement(SwingConstants.TOP);
				break;
			case 1:
				SettingsHandler.setChaTabPlacement(SwingConstants.BOTTOM);
				break;
			case 2:
				SettingsHandler.setChaTabPlacement(SwingConstants.LEFT);
				break;
			case 3:
				SettingsHandler.setChaTabPlacement(SwingConstants.RIGHT);
				break;
		}
		switch (tabLabelsCombo.getSelectedIndex())
		{
			case 0:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME);
				break;
			case 1:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_CLASS);
				break;
			case 2:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE);
				break;
			case 3:
				SettingsHandler.setNameDisplayStyle(Constants.DISPLAY_STYLE_NAME_RACE_CLASS);
				break;
		}
		SettingsHandler.setAbilitiesShownAsATab(displayAbilitiesAsTab.isSelected());

		// Display Options
		SettingsHandler.setToolTipTextShown(toolTipTextShown.isSelected());
		SettingsHandler.setToolBarShown(showToolbar.isSelected());
		SettingsHandler.setUseExperimentalCursor(experimentalCursor.isSelected());

		// Look and Feel
		int sourceIndex = 500;
		for (int i = 0; i < laf.length; i++)
		{
			if (laf[i].isSelected())
			{
				sourceIndex = i;
			}
		}

		if (sourceIndex < laf.length)
		{
			SettingsHandler.setLookAndFeel(sourceIndex);
			UIFactory.setLookAndFeel(sourceIndex);
		}
		else if (skinnedLookFeel.isSelected())
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
			{
				if (SettingsHandler.getSkinLFThemePack().length() == 0)
				{
					JOptionPane.showMessageDialog(this, "Please pick a skinned theme before selecting the Skinned look and Feel.", "PCGen", JOptionPane.WARNING_MESSAGE);
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
						SettingsHandler.setLookAndFeel(0);
						UIFactory.setLookAndFeel(0);
						JOptionPane.showMessageDialog(null, "There was a problem setting the skinned look and feel.\n" + "The System one will be used instead.\nError: " + e.toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
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
					missingLibMsg = "The skinned look and feel feature requires the download of the above mentioned file(s) " +
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
			}
		}

		// Level up
		SettingsHandler.setShowHPDialogAtLevelUp(hpDialogShownAtLevelUp.isSelected());
		SettingsHandler.setShowFeatDialogAtLevelUp(featDialogShownAtLevelUp.isSelected());
		SettingsHandler.setShowStatDialogAtLevelUp(statDialogShownAtLevelUp.isSelected());

		// Equipment
		SettingsHandler.setMetamagicAllowedInEqBuilder(allowMetamagicInEqBuilder.isSelected());
		SettingsHandler.setMaxPotionSpellLevel(potionMaxLevel.getSelectedIndex() + SPELLLVLMIN);
		SettingsHandler.setMaxWandSpellLevel(wandMaxLevel.getSelectedIndex() + SPELLLVLMIN);
		SettingsHandler.setWantToLoadMasterworkAndMagic(false); // Turn it off temporarily so we can set the values
		SettingsHandler.setAutogen(Constants.AUTOGEN_RACIAL, autoMethod1.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_MASTERWORK, autoMethod2.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_MAGIC, autoMethod3.isSelected());
		SettingsHandler.setAutogen(Constants.AUTOGEN_EXOTICMATERIAL, autoMethod4.isSelected());
		SettingsHandler.setWantToLoadMasterworkAndMagic(noAutoEquipCreate.isSelected()); // Now set it properly

		// Language - no setable options

		// Output
		Globals.selectPaper((String) paperType.getSelectedItem());

		// Sources
		SettingsHandler.setLoadCampaignsAtStart(campLoad.isSelected());
		SettingsHandler.setLoadCampaignsWithPC(charCampLoad.isSelected());
		SettingsHandler.setSaveCustomEquipment(saveCustom.isSelected());
		SettingsHandler.setShowLicense(showOGL.isSelected());
		switch (sourceOptions.getSelectedIndex())
		{
			case 0:
				Globals.setSourceDisplay(Constants.SOURCELONG);
				break;
			case 1:
				Globals.setSourceDisplay(Constants.SOURCESHORT);
				break;
			case 2:
				Globals.setSourceDisplay(Constants.SOURCEPAGE);
				break;
			case 3:
				Globals.setSourceDisplay(Constants.SOURCEWEB);
				break;
		}

		// Now get any panels affected to refresh
		Globals.getRootFrame().forceUpdate_InfoAbilities();
		Globals.getRootFrame().forceUpdate_InfoSummary();
		Globals.getRootFrame().forceUpdate_PlayerTabs();
	}

	private final class PrefsButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent actionEvent)
		{
			JButton source = (JButton) actionEvent.getSource();

			if (source == purchaseMode)
			{
				showPurchaseModeConfiguration();
			}
			else if (source == prereqFailColor || source == featAutoColor || source == featVirtualColor)
			{
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
			else if (source == themepack)
			{
				selectThemePack();
			}
			else if (source == browserPath)
			{
				Utility.selectDefaultBrowser(getParent());
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
				final int returnVal = fc.showOpenDialog(getParent());
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
				final int returnVal = fc.showOpenDialog(getParent());
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
				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
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
				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
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
		}


	}

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	private JPanel buildAbilitiesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Abilities");
		JPanel abilityScoresPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		abilityScoresPanel.setBorder(title1);
		abilityScoresPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);

		exclusiveGroup = new ButtonGroup();
		buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel("Ability Score Generation: ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);

		buildConstraints(c, 1, 1, 2, 1, 0, 0);
		gridbag.setConstraints(abilitiesUserRolledButton, c);
		abilityScoresPanel.add(abilitiesUserRolledButton);
		exclusiveGroup.add(abilitiesUserRolledButton);

		buildConstraints(c, 1, 2, 2, 1, 0, 0);
		gridbag.setConstraints(abilitiesAllSameButton, c);
		abilityScoresPanel.add(abilitiesAllSameButton);
		exclusiveGroup.add(abilitiesAllSameButton);
		buildConstraints(c, 1, 3, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		buildConstraints(c, 2, 3, 2, 1, 0, 0);
		for (int i = STATMIN;i <= STATMAX;i++)
			allSameValue[i-STATMIN] = String.valueOf(i);
		abilityScoreCombo = new JComboBox(allSameValue);
		gridbag.setConstraints(abilityScoreCombo, c);
		abilityScoresPanel.add(abilityScoreCombo);

		buildConstraints(c, 1, 4, 2, 1, 0, 0);
		gridbag.setConstraints(abilitiesPurchasedButton, c);
		abilityScoresPanel.add(abilitiesPurchasedButton);
		exclusiveGroup.add(abilitiesPurchasedButton);
		buildConstraints(c, 2, 5, 2, 1, 0, 0);
		final int purchaseMethodCount = SettingsHandler.getPurchaseMethodCount();
		pMode = new String[purchaseMethodCount];
		pModeMethodName = new String[purchaseMethodCount];
		for (int i = 0; i < purchaseMethodCount; ++i)
		{
			final PointBuyMethod pbm = SettingsHandler.getPurhaseMethod(i);
			pMode[i] = pbm.getMethodName() + " (" + pbm.getPoints() + ")";
			pModeMethodName[i] = pbm.getMethodName();
		}
		abilityPurchaseModeCombo = new JComboBox(pMode);
		gridbag.setConstraints(abilityPurchaseModeCombo, c);
		abilityScoresPanel.add(abilityPurchaseModeCombo);

		buildConstraints(c, 1, 6, 1, 1, 0, 0);
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		buildConstraints(c, 1, 7, 3, 1, 0, 0);
		gridbag.setConstraints(purchaseMode, c);
		abilityScoresPanel.add(purchaseMode);
		purchaseMode.addActionListener(prefsButtonHandler);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);

		return abilityScoresPanel;
	}

	private JPanel buildHitPointsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Hit Points");
		JPanel hitPointsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		hitPointsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		hitPointsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);

		exclusiveGroup = new ButtonGroup();
		buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel("Hit Point Generation: ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		buildConstraints(c, 1, 1, 2, 1, 0, 0);
		gridbag.setConstraints(hpStandard, c);
		hitPointsPanel.add(hpStandard);
		exclusiveGroup.add(hpStandard);

		buildConstraints(c, 1, 2, 2, 1, 0, 0);
		gridbag.setConstraints(hpAutomax, c);
		hitPointsPanel.add(hpAutomax);
		exclusiveGroup.add(hpAutomax);

		buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel("Maximum HP at First Level");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(maxHpAtFirstLevel, c);
		hitPointsPanel.add(maxHpAtFirstLevel);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		return hitPointsPanel;
	}

	private JPanel buildHouseRulesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "House Rules");
		JPanel houseRulesPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		houseRulesPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		houseRulesPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel("Apply Load Penalty to AC and Skills");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(applyLoadPenaltyToACandSkills, c);
		houseRulesPanel.add(applyLoadPenaltyToACandSkills);

		buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel("Apply Weight Penalty to Skills");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(applyWeightPenaltyToSkills, c);
		houseRulesPanel.add(applyWeightPenaltyToSkills);

		buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel("Ask For Free Clothing at First Level");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(freeClothesAtFirst, c);
		houseRulesPanel.add(freeClothesAtFirst);

		buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel("Bypass Class Prerequisites");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(bypassClassPreReqsCheckbox, c);
		houseRulesPanel.add(bypassClassPreReqsCheckbox);

		buildConstraints(c, 0, 4, 2, 1, 0, 0);
		label = new JLabel("Bypass Feat Prerequisites");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 4, 1, 1, 0, 0);
		gridbag.setConstraints(bypassFeatPreReqsCheckbox, c);
		houseRulesPanel.add(bypassFeatPreReqsCheckbox);

		buildConstraints(c, 0, 5, 2, 1, 0, 0);
		label = new JLabel("Bypass Maximum Skill Rank");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 5, 1, 1, 0, 0);
		gridbag.setConstraints(bypassMaxSkillRankCheckbox, c);
		houseRulesPanel.add(bypassMaxSkillRankCheckbox);

		buildConstraints(c, 0, 6, 2, 1, 0, 0);
		label = new JLabel("Cross-class Skill Cost");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 6, 1, 1, 0, 0);
		gridbag.setConstraints(crossClassSkillCostCombo, c);
		houseRulesPanel.add(crossClassSkillCostCombo);

		buildConstraints(c, 0, 7, 2, 1, 0, 0);
		label = new JLabel("Increment Skills Before Leveling");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 7, 1, 1, 0, 0);
		gridbag.setConstraints(skillIncrementBefore, c);
		houseRulesPanel.add(skillIncrementBefore);

		buildConstraints(c, 0, 8, 2, 1, 0, 0);
		label = new JLabel("Treat Weapons In Hand As Equipped For Attacks");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 8, 1, 1, 0, 0);
		gridbag.setConstraints(treatInHandAsEquippedForAttacks, c);
		houseRulesPanel.add(treatInHandAsEquippedForAttacks);

		buildConstraints(c, 0, 9, 2, 1, 0, 0);
		label = new JLabel("Ignore Level Cap");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		buildConstraints(c, 2, 9, 1, 1, 0, 0);
		gridbag.setConstraints(ignoreLevelCap, c);
		houseRulesPanel.add(ignoreLevelCap);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);

		return houseRulesPanel;
	}

	private JPanel buildMonstersPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Monsters");
		JPanel monstersPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		monstersPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		monstersPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);


		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel("Use Default Monsters");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(useMonsterDefault, c);
		monstersPanel.add(useMonsterDefault);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);

		return monstersPanel;
	}

	private JPanel buildColorsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_color);
		JPanel colorsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		colorsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		colorsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 1, 1, 0, 0);
		prereqFailColor = new JButton(in_colorPrereqFail);
		gridbag.setConstraints(prereqFailColor, c);
		colorsPanel.add(prereqFailColor);
		prereqFailColor.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		featAutoColor = new JButton(in_colorAutoFeat);
		gridbag.setConstraints(featAutoColor, c);
		colorsPanel.add(featAutoColor);
		featAutoColor.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 2, 1, 1, 0, 0);
		featVirtualColor = new JButton(in_colorVirtFeat);
		gridbag.setConstraints(featVirtualColor, c);
		colorsPanel.add(featVirtualColor);
		featVirtualColor.addActionListener(prefsButtonHandler);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		colorsPanel.add(label);

		return colorsPanel;
	}

	private JPanel buildTabsAppearancePanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_tabs);
		JPanel tabsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		tabsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		tabsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_mainTabPlacement);
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		buildConstraints(c, 2, 0, 1, 1, 0, 0);
		mainTabPlacementCombo = new JComboBox(new String[] {in_tabPosTop, in_tabPosBottom, in_tabPosLeft, in_tabPosRight});
		gridbag.setConstraints(mainTabPlacementCombo, c);
		tabsPanel.add(mainTabPlacementCombo);

		buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(in_charTabPlacement);
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		buildConstraints(c, 2, 1, 1, 1, 0, 0);
		charTabPlacementCombo = new JComboBox(new String[] {in_tabPosTop, in_tabPosBottom, in_tabPosLeft, in_tabPosRight});
		gridbag.setConstraints(charTabPlacementCombo, c);
		tabsPanel.add(charTabPlacementCombo);

		buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_charTabLabel);
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		buildConstraints(c, 2, 2, 1, 1, 0, 0);
		tabLabelsCombo = new JComboBox(new String[] {in_tabLabelPlain, in_tabLabelEpic, in_tabLabelRace, in_tabLabelNetHack} );
		gridbag.setConstraints(tabLabelsCombo, c);
		tabsPanel.add(tabLabelsCombo);

		buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel(in_tabAbilities);
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(displayAbilitiesAsTab, c);
		tabsPanel.add(displayAbilitiesAsTab);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);

		return tabsPanel;
	}

	private JPanel buildDisplayOptionsPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_displayOpts);
		JPanel displayOptsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		displayOptsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		displayOptsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_showToolTips);
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(toolTipTextShown, c);
		displayOptsPanel.add(toolTipTextShown);

		buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(in_showToolBar);
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(showToolbar, c);
		displayOptsPanel.add(showToolbar);

		buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_useAutoWaitCursor);
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(experimentalCursor, c);
		displayOptsPanel.add(experimentalCursor);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);

		return displayOptsPanel;
	}

	private JPanel buildLookAndFeelPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_lookAndFeel);
		JPanel lafPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		lafPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		lafPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		exclusiveGroup = new ButtonGroup();
		laf = new JRadioButton[UIFactory.getLookAndFeelCount()];
		for (int i = 0; i < laf.length; i++)
		{
			laf[i] = new JRadioButton();
			laf[i].setText(UIFactory.getLookAndFeelName(i));
			Utility.setDescription(laf[i], UIFactory.getLookAndFeelTooltip(i));
			laf[i].setMnemonic(laf[i].getText().charAt(0));
			buildConstraints(c, 0, i, 3, 1, 0, 0);
			gridbag.setConstraints(laf[i], c);
			lafPanel.add(laf[i]);
			exclusiveGroup.add(laf[i]);
		}

		skinnedLookFeel.setText(in_skinnedLAF);
		Utility.setDescription(skinnedLookFeel, "Sets the look to that of the selected themepack, or to cross platform if there is no themepack selected.");
		skinnedLookFeel.setMnemonic('K');
		buildConstraints(c, 0, laf.length, 3, 1, 0, 0);
		gridbag.setConstraints(skinnedLookFeel, c);
		lafPanel.add(skinnedLookFeel);
		exclusiveGroup.add(skinnedLookFeel);

		buildConstraints(c, 0, laf.length+1, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		lafPanel.add(label);
		buildConstraints(c, 1, laf.length+1, 2, 1, 0, 0);
		themepack = new JButton(in_chooseSkin);
		Utility.setDescription(themepack, "Change the skin themepack (see http://javootoo.l2fprod.com/plaf/skinlf/index.php for additional skins.)");
		gridbag.setConstraints(themepack, c);
		lafPanel.add(themepack);
		themepack.addActionListener(prefsButtonHandler);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		lafPanel.add(label);

		return lafPanel;
	}

	private JPanel buildLevelUpPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_levelUp);
		JPanel levelUpPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		levelUpPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		levelUpPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_hpWindow);
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(hpDialogShownAtLevelUp, c);
		levelUpPanel.add(hpDialogShownAtLevelUp);

		buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(in_featWindow);
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(featDialogShownAtLevelUp, c);
		levelUpPanel.add(featDialogShownAtLevelUp);

		buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_statWindow);
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(statDialogShownAtLevelUp, c);
		levelUpPanel.add(statDialogShownAtLevelUp);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);

		return levelUpPanel;
	}

	private JPanel buildEquipmentPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_equipment);
		JPanel equipmentPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		equipmentPanel.setBorder(title1);
		equipmentPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);
		exclusiveGroup = new ButtonGroup();

		buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(in_allowMetamagic);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 0, 1, 1, 0, 0);
		gridbag.setConstraints(allowMetamagicInEqBuilder, c);
		equipmentPanel.add(allowMetamagicInEqBuilder);

		buildConstraints(c, 0, 1, 3, 1, 0, 0);
		label = new JLabel(in_potionMax);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 1, 1, 1, 0, 0);
		buildConstraints(c, 3, 1, 1, 1, 0, 0);
		for (int i = SPELLLVLMIN;i <= SPELLLVLMAX;i++)
			potionSpellLevel[i-SPELLLVLMIN] = String.valueOf(i) + "  ";
		potionMaxLevel = new JComboBox(potionSpellLevel);
		gridbag.setConstraints(potionMaxLevel, c);
		equipmentPanel.add(potionMaxLevel);

		buildConstraints(c, 0, 2, 3, 1, 0, 0);
		label = new JLabel(in_wandMax);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 2, 1, 1, 0, 0);
		for (int i = SPELLLVLMIN;i <= SPELLLVLMAX;i++)
			wandSpellLevel[i-SPELLLVLMIN] = String.valueOf(i) + "  ";
		wandMaxLevel = new JComboBox(wandSpellLevel);
		gridbag.setConstraints(wandMaxLevel, c);
		equipmentPanel.add(wandMaxLevel);

		buildConstraints(c, 0, 3, 3, 1, 0, 0);
		label = new JLabel(in_anyAutoEquip);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);

		buildConstraints(c, 0, 4, 2, 1, 0, 0);
		noAutoEquipCreate = new JRadioButton(in_noAutoEquip);
		gridbag.setConstraints(noAutoEquipCreate, c);
		equipmentPanel.add(noAutoEquipCreate);
		exclusiveGroup.add(noAutoEquipCreate);

		buildConstraints(c, 0, 5, 2, 1, 0, 0);
		autoEquipCreate = new JRadioButton(in_autoEquip);
		gridbag.setConstraints(autoEquipCreate, c);
		equipmentPanel.add(autoEquipCreate);
		exclusiveGroup.add(autoEquipCreate);
		buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);

		buildConstraints(c, 1, 6, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipRace);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 6, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod1, c);
		equipmentPanel.add(autoMethod1);

		buildConstraints(c, 1, 7, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipMasterwork);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 7, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod2, c);
		equipmentPanel.add(autoMethod2);

		buildConstraints(c, 1, 8, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipMagic);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 8, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod3, c);
		equipmentPanel.add(autoMethod3);

		buildConstraints(c, 1, 9, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipExotic);
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		buildConstraints(c, 3, 9, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod4, c);
		equipmentPanel.add(autoMethod4);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);

		return equipmentPanel;
	}

	private JPanel buildLanguagePanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_equipment);
		JPanel langPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		langPanel.setBorder(title1);
		langPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);
		exclusiveGroup = new ButtonGroup();

		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		langEng = new JRadioButton(in_langEnglish);
		gridbag.setConstraints(langEng, c);
		langPanel.add(langEng);
		exclusiveGroup.add(langEng);

		buildConstraints(c, 0, 1, 2, 1, 0, 0);
		langGer = new JRadioButton(in_langGerman);
		gridbag.setConstraints(langGer, c);
		langPanel.add(langGer);
		exclusiveGroup.add(langGer);
		langGer.setEnabled(false);

		buildConstraints(c, 0, 2, 2, 1, 0, 0);
		langFre = new JRadioButton(in_langFrench);
		gridbag.setConstraints(langFre, c);
		langPanel.add(langFre);
		exclusiveGroup.add(langFre);
		langFre.setEnabled(false);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		langPanel.add(label);

		return langPanel;
	}

	private JPanel buildLocationPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_location);
		JPanel locationPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		locationPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		locationPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 1, 1, 0, 0);
		browserPath = new JButton(in_browserPath);
		gridbag.setConstraints(browserPath, c);
		locationPanel.add(browserPath);
		browserPath.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 1, 1, 1, 0, 0);
		clearBrowserPath = new JButton(in_clearBrowserPath);
		gridbag.setConstraints(clearBrowserPath, c);
		locationPanel.add(clearBrowserPath);
		clearBrowserPath.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 2, 1, 1, 0, 0);
		pcgenDataDir = new JButton(in_pcgenDataDir);
		gridbag.setConstraints(pcgenDataDir, c);
		locationPanel.add(pcgenDataDir);
		pcgenDataDir.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 3, 1, 1, 0, 0);
		pcgenSystemDir = new JButton(in_pcgenSystemDir);
		gridbag.setConstraints(pcgenSystemDir, c);
		locationPanel.add(pcgenSystemDir);
		pcgenSystemDir.addActionListener(prefsButtonHandler);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);

		return locationPanel;
	}

	private JPanel buildOutputPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_output);
		JPanel outputPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		outputPanel.setBorder(title1);
		outputPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 0, 0, 2, 1, 0, 0);
		templateDefault = new JButton(in_templateDefault);
		gridbag.setConstraints(templateDefault, c);
		outputPanel.add(templateDefault);
		templateDefault.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 1, 2, 1, 0, 0);
		templateEqSet = new JButton(in_templateEqSet);
		gridbag.setConstraints(templateEqSet, c);
		outputPanel.add(templateEqSet);
		templateEqSet.addActionListener(prefsButtonHandler);

		buildConstraints(c, 0, 2, 1, 1, 0, 0);
		label = new JLabel(in_paperType);
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		buildConstraints(c, 1, 2, 1, 1, 0, 0);
		final int paperCount = Globals.getPaperCount();
		paperNames = new String[paperCount];
		for (int i = 0; i < paperCount; i++)
		{
			paperNames[i] = Globals.getPaperInfo(i, Constants.PAPERINFO_NAME);
		}
		paperType = new JComboBox(paperNames);
		gridbag.setConstraints(paperType, c);
		outputPanel.add(paperType);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);

		return outputPanel;
	}

	private JPanel buildSourcesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_equipment);
		JPanel sourcesPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		sourcesPanel.setBorder(title1);
		sourcesPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2,2,2,2);
		exclusiveGroup = new ButtonGroup();

		buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(in_autoLoadAtStart);
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		buildConstraints(c, 3, 0, 1, 1, 0, 0);
		gridbag.setConstraints(campLoad, c);
		sourcesPanel.add(campLoad);

		buildConstraints(c, 0, 1, 3, 1, 0, 0);
		label = new JLabel(in_autoLoadWithPC);
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		buildConstraints(c, 3, 1, 1, 1, 0, 0);
		gridbag.setConstraints(charCampLoad, c);
		sourcesPanel.add(charCampLoad);

		buildConstraints(c, 0, 2, 3, 1, 0, 0);
		label = new JLabel(in_saveCustom);
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		buildConstraints(c, 3, 2, 1, 1, 0, 0);
		gridbag.setConstraints(saveCustom, c);
		sourcesPanel.add(saveCustom);

		buildConstraints(c, 0, 3, 3, 1, 0, 0);
		label = new JLabel(in_displayOGL);
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		buildConstraints(c, 3, 3, 1, 1, 0, 0);
		gridbag.setConstraints(showOGL, c);
		sourcesPanel.add(showOGL);

		buildConstraints(c, 0, 4, 3, 1, 0, 0);
		label = new JLabel(in_sourceDisplay);
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		buildConstraints(c, 3, 4, 1, 1, 0, 0);
		sourceOptions = new JComboBox(new String[] {in_sdLong, in_sdPage, in_sdShort, in_sdWeb});
		gridbag.setConstraints(sourceOptions, c);
		sourcesPanel.add(sourceOptions);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);

		return sourcesPanel;
	}


	private JPanel buildPlaceHolderPanel(String title)
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		JPanel panel = new JPanel();
	  Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, title);

		title1.setTitleJustification(TitledBorder.LEFT);
		panel.setBorder(title1);
		gridbag = new GridBagLayout();
		panel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(2,2,2,2);

		buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel("Coming Soon", SwingConstants.CENTER);
		gridbag.setConstraints(label, c);
		panel.add(label);

		return panel;
	}


	private void showPurchaseModeConfiguration()
	{
		//Create and display purchasemodestats popup frame.
		if (pmsFrame == null)
		{
			pmsFrame = new PurchaseModeFrame(this);

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

	private void selectThemePack()
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

}
