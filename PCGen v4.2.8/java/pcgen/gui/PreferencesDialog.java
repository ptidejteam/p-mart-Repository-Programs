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
 * $Id: PreferencesDialog.java,v 1.1 2006/02/21 01:00:21 vauchers Exp $
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PointBuyMethod;
import pcgen.core.SettingsHandler;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.util.PropertyFactory;
import pcgen.util.SkinLFResourceChecker;

/**
 *  PCGen preferences dialog
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

final class PreferencesDialog extends JDialog
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
	private FlippingSplitPane splitPane;
	private JScrollPane settingsScroll;
	private JPanel controlPanel;

	// Character Settings
	//private JPanel characterPanel = new JPanel();

	// Abilities
	private JRadioButton abilitiesUserRolledButton;
	private JRadioButton abilitiesAllSameButton;
	private JComboBox abilityScoreCombo;
	private String allSameValue [] = new String[STATMAX - STATMIN + 1];
	private JComboBox abilityPurchaseModeCombo;
	private String pMode [];
	private String pModeMethodName[];
	private JRadioButton abilitiesPurchasedButton;
	private JButton purchaseMode;
	private PurchaseModeFrame pmsFrame = null;

	// "HP Roll Methods"
	private JRadioButton hpAutomax = new JRadioButton(PropertyFactory.getString("in_Prefs_hpAutoMax"));
	private JRadioButton hpStandard = new JRadioButton(PropertyFactory.getString("in_Prefs_hpStandard"));
	private JCheckBox maxHpAtFirstLevel = new JCheckBox();

	// "House Rules"
	private JCheckBox applyLoadPenaltyToACandSkills = new JCheckBox();
	private JCheckBox applyWeightPenaltyToSkills = new JCheckBox();
	private JCheckBox freeClothesAtFirst = new JCheckBox();
	private JCheckBox bypassClassPreReqsCheckbox = new JCheckBox();
	private JCheckBox bypassFeatPreReqsCheckbox = new JCheckBox();
	private JCheckBox bypassMaxSkillRankCheckbox = new JCheckBox();
	private JComboBox crossClassSkillCostCombo = new JComboBox(new String[]{"0  ", "1  ", "2  "});
	private JCheckBox skillIncrementBefore = new JCheckBox();
	private JCheckBox treatInHandAsEquippedForAttacks = new JCheckBox();
	private JCheckBox ignoreLevelCap = new JCheckBox();

	// "Monsters"
	private JCheckBox useMonsterDefault = new JCheckBox();
	private JCheckBox hideMonsterClasses = new JCheckBox();
	private JCheckBox ignoreMonsterHDCap = new JCheckBox();

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
	private JCheckBox useOutputNames = new JCheckBox();
	private JComboBox cmbChoiceMethods = new JComboBox(singleChoiceMethods);

	// Look and Feel
	private JRadioButton laf[];
	private JRadioButton skinnedLookFeel = new JRadioButton();
	private JTextField themepackLabel;
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
	//private JPanel languageChoice = new JPanel(); //("Language");
	private JRadioButton langEng;
	private JRadioButton langGer;
	private JRadioButton langFre;
	private JRadioButton langIt;

	// Location
	private JTextField browserPath;
	private JButton browserPathButton;
	private JButton clearBrowserPathButton;
	private JTextField pcgenCharacterDir;
	private JButton pcgenCharacterDirButton;
	private JTextField pcgenDataDir;
	private JButton pcgenDataDirButton;
	private JTextField pcgenDocsDir;
	private JButton pcgenDocsDirButton;
	private JTextField pcgenSystemDir;
	private JButton pcgenSystemDirButton;

	// Output
	private JTextField outputSheetHTMLDefault;
	private JButton outputSheetHTMLDefaultButton;
	private JTextField outputSheetPDFDefault;
	private JButton outputSheetPDFDefaultButton;
	private JTextField outputSheetEqSet;
	private JButton outputSheetEqSetButton;
	private JComboBox paperType;
	private String paperNames[] = null;
	private JCheckBox removeTempFiles;
	private JCheckBox weaponProfPrintout;

	// Sources
	private JCheckBox campLoad = new JCheckBox();
	private JCheckBox charCampLoad = new JCheckBox();
	private JCheckBox allowOptsInSource = new JCheckBox();
	private JCheckBox saveCustom = new JCheckBox();
	private JCheckBox showOGL = new JCheckBox();
	private JCheckBox showd20 = new JCheckBox();
	private JComboBox sourceOptions = new JComboBox();

	// Listeners
	private PrefsButtonListener prefsButtonHandler = new PrefsButtonListener();

	// Resource strings
	private static String in_dialogTitle = PropertyFactory.getString("in_Prefs_title");
	private static String in_character = PropertyFactory.getString("in_Prefs_character");
	private static String in_abilities = PropertyFactory.getString("in_Prefs_abilities");
	private static String in_abilitiesUserRolledLabel = PropertyFactory.getString("in_Prefs_abilitiesUserRolled");
	private static String in_abilitiesAllSameLabel = PropertyFactory.getString("in_Prefs_abilitiesAllSame");
	private static String in_abilitiesPurchasedLabel = PropertyFactory.getString("in_Prefs_abilitiesPurchased");
	private static String in_purchaseModeConfigLabel = PropertyFactory.getString("in_Prefs_purchaseModeConfig");
	private static String in_hp = PropertyFactory.getString("in_Prefs_hp");
	private static String in_hpAutoMaxLabel = PropertyFactory.getString("in_Prefs_hpAutoMax");
	private static String in_hpStandardLabel = PropertyFactory.getString("in_Prefs_hpStandard");
	private static String in_houseRules = PropertyFactory.getString("in_Prefs_houseRules");
	private static String in_monsters = PropertyFactory.getString("in_Prefs_monsters");
	private static String in_pcgen = PropertyFactory.getString("in_Prefs_pcgen");
	private static String in_appearance = PropertyFactory.getString("in_Prefs_appearance");
	private static String in_color = PropertyFactory.getString("in_Prefs_color");
	private static String in_colorPrereqFail = PropertyFactory.getString("in_Prefs_colorPrereqFail");
	private static String in_colorAutoFeat = PropertyFactory.getString("in_Prefs_colorAutoFeat");
	private static String in_colorVirtFeat = PropertyFactory.getString("in_Prefs_colorVirtFeat");
	private static String in_tabs = PropertyFactory.getString("in_Prefs_tabs");
	private static String in_mainTabPlacement = PropertyFactory.getString("in_Prefs_mainTabPlacement");
	private static String in_charTabPlacement = PropertyFactory.getString("in_Prefs_charTabPlacement");
	private static String in_charTabLabel = PropertyFactory.getString("in_Prefs_charTabLabel");
	private static String in_tabLabelPlain = PropertyFactory.getString("in_Prefs_tabLabelPlain");
	private static String in_tabLabelEpic = PropertyFactory.getString("in_Prefs_tabLabelEpic");
	private static String in_tabLabelRace = PropertyFactory.getString("in_Prefs_tabLabelRace");
	private static String in_tabLabelNetHack = PropertyFactory.getString("in_Prefs_tabLabelNetHack");
	private static String in_tabPosTop = PropertyFactory.getString("in_Prefs_tabPosTop");
	private static String in_tabPosBottom = PropertyFactory.getString("in_Prefs_tabPosBottom");
	private static String in_tabPosLeft = PropertyFactory.getString("in_Prefs_tabPosLeft");
	private static String in_tabPosRight = PropertyFactory.getString("in_Prefs_tabPosRight");
	private static String in_tabAbilities = PropertyFactory.getString("in_Prefs_tabAbilities");
	private static String in_displayOpts = PropertyFactory.getString("in_Prefs_displayOpts");
	private static String in_showToolTips = PropertyFactory.getString("in_Prefs_showToolTips");
	private static String in_showToolBar = PropertyFactory.getString("in_Prefs_showToolBar");
	private static String in_useAutoWaitCursor = PropertyFactory.getString("in_Prefs_useAutoWaitCursor");
	private static String in_useOutputNames = PropertyFactory.getString("in_Prefs_useOutputNames");
	private static String in_singleChoiceOption = PropertyFactory.getString("in_Prefs_singleChoiceOption");
	private static String in_lookAndFeel = PropertyFactory.getString("in_Prefs_lookAndFeel");
	private static String in_skinnedLAF = PropertyFactory.getString("in_Prefs_skinnedLAF");
	private static String in_chooseSkin = PropertyFactory.getString("in_Prefs_chooseSkin");
	private static String in_levelUp = PropertyFactory.getString("in_Prefs_levelUp");
	private static String in_hpWindow = PropertyFactory.getString("in_Prefs_hpWindow");
	private static String in_featWindow = PropertyFactory.getString("in_Prefs_featWindow");
	private static String in_statWindow = PropertyFactory.getString("in_Prefs_statWindow");
	private static String in_equipment = PropertyFactory.getString("in_Prefs_equipment");
	private static String in_allowMetamagic = PropertyFactory.getString("in_Prefs_allowMetamagic");
	private static String in_potionMax = PropertyFactory.getString("in_Prefs_potionMax");
	private static String in_wandMax = PropertyFactory.getString("in_Prefs_wandMax");
	private static String in_anyAutoEquip = PropertyFactory.getString("in_Prefs_anyAutoEquip");
	private static String in_noAutoEquip = PropertyFactory.getString("in_Prefs_noAutoEquip");
	private static String in_autoEquip = PropertyFactory.getString("in_Prefs_autoEquip");
	private static String in_autoEquipRace = PropertyFactory.getString("in_Prefs_autoEquipRace");
	private static String in_autoEquipMasterwork = PropertyFactory.getString("in_Prefs_autoEquipMasterwork");
	private static String in_autoEquipMagic = PropertyFactory.getString("in_Prefs_autoEquipMagic");
	private static String in_autoEquipExotic = PropertyFactory.getString("in_Prefs_autoEquipExotic");
	private static String in_language = PropertyFactory.getString("in_Prefs_language");
	private static String in_langEnglish = PropertyFactory.getString("in_Prefs_langEnglish");
	private static String in_langFrench = PropertyFactory.getString("in_Prefs_langFrench");
	private static String in_langGerman = PropertyFactory.getString("in_Prefs_langGerman");
	private static String in_langItalian = PropertyFactory.getString("in_Prefs_langItalian");
	private static String in_location = PropertyFactory.getString("in_Prefs_location");
	private static String in_browserPath = PropertyFactory.getString("in_Prefs_browserPath");
	private static String in_clearBrowserPath = PropertyFactory.getString("in_Prefs_clearBrowserPath");
	private static String in_output = PropertyFactory.getString("in_Prefs_output");
	private static String in_outputSheetEqSet = PropertyFactory.getString("in_Prefs_templateEqSet");
	private static String in_paperType = PropertyFactory.getString("in_Prefs_paperType");
	private static String in_weaponProfPrintout = PropertyFactory.getString("in_Prefs_weaponProfPrintout");
	private static String in_sources = PropertyFactory.getString("in_Prefs_sources");
	private static String in_autoLoadAtStart = PropertyFactory.getString("in_Prefs_autoLoadAtStart");
	private static String in_autoLoadWithPC = PropertyFactory.getString("in_Prefs_autoLoadWithPC");
	private static String in_saveCustom = PropertyFactory.getString("in_Prefs_saveCustom");
	private static String in_displayOGL = PropertyFactory.getString("in_Prefs_displayOGL");
	private static String in_displayd20 = PropertyFactory.getString("in_Prefs_displayd20");
	private static String in_sourceDisplay = PropertyFactory.getString("in_Prefs_sourceDisplay");
	private static String in_sdLong = PropertyFactory.getString("in_Prefs_sdLong");
	private static String in_sdPage = PropertyFactory.getString("in_Prefs_sdPage");
	private static String in_sdShort = PropertyFactory.getString("in_Prefs_sdShort");
	private static String in_sdWeb = PropertyFactory.getString("in_Prefs_sdWeb");
	private static String in_removeTemp = PropertyFactory.getString("in_Prefs_removeTemp");

	private static String in_cmNone = PropertyFactory.getString("in_Prefs_cmNone");
	private static String in_cmSelect = PropertyFactory.getString("in_Prefs_cmSelect");
	private static String in_cmSelectExit = PropertyFactory.getString("in_Prefs_cmSelectExit");
	private static String[] singleChoiceMethods = {in_cmNone, in_cmSelect, in_cmSelectExit};

	private static String in_choose = "...";

	public static void show(JFrame frame)
	{
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
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
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

		// Build the settings panel
		settingsPanel = new JPanel();
		settingsPanel.setLayout(new CardLayout());

		// Build the selection tree
		characterNode = new DefaultMutableTreeNode(in_character);
		settingsPanel.add(buildEmptyPanel("", PropertyFactory.getString("in_Prefs_charTip")), in_character);

		characterNode.add(new DefaultMutableTreeNode(in_abilities));
		settingsPanel.add(buildAbilitiesPanel(), in_abilities);
		characterNode.add(new DefaultMutableTreeNode(in_hp));
		settingsPanel.add(buildHitPointsPanel(), in_hp);
		characterNode.add(new DefaultMutableTreeNode(in_houseRules));
		settingsPanel.add(buildHouseRulesPanel(), in_houseRules);
		characterNode.add(new DefaultMutableTreeNode(in_monsters));
		settingsPanel.add(buildMonstersPanel(), in_monsters);
		rootNode.add(characterNode);

		appearanceNode = new DefaultMutableTreeNode(in_appearance);
		settingsPanel.add(buildEmptyPanel("", PropertyFactory.getString("in_Prefs_appearanceTip")), in_appearance);

		appearanceNode.add(new DefaultMutableTreeNode(in_color));
		settingsPanel.add(buildColorsPanel(), in_color);
		appearanceNode.add(new DefaultMutableTreeNode(in_displayOpts));
		settingsPanel.add(buildDisplayOptionsPanel(), in_displayOpts);
		appearanceNode.add(new DefaultMutableTreeNode(in_levelUp));
		settingsPanel.add(buildLevelUpPanel(), in_levelUp);
		appearanceNode.add(new DefaultMutableTreeNode(in_lookAndFeel));
		settingsPanel.add(buildLookAndFeelPanel(), in_lookAndFeel);
		appearanceNode.add(new DefaultMutableTreeNode(in_tabs));
		settingsPanel.add(buildTabsAppearancePanel(), in_tabs);
		rootNode.add(appearanceNode);

		pcGenNode = new DefaultMutableTreeNode(in_pcgen);
		settingsPanel.add(buildEmptyPanel("", PropertyFactory.getString("in_Prefs_pcgenTip")), in_pcgen);

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
		/*
		 * <!--
		 *      bug:     TreeView not displaying correctly with Kunststoff LaF
		 *      fix:     need to set a (wide enough) border
		 *      author:  Thomas Behr
		 *      date:    02/10/02
		 * -->
		 */
		if (UIFactory.getLookAndFeelName(SettingsHandler.getLookAndFeel()).equals("Kunststoff"))
		{
			settingsTree.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		}
		else
		{
			settingsTree.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		}
		settingsTree.setRootVisible(false);
		settingsTree.setShowsRootHandles(true);
		settingsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		settingsScroll = new JScrollPane(settingsTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		// Turn off the icons
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		settingsTree.setCellRenderer(renderer);

		// Expand all of the branch nodes
		settingsTree.expandPath(new TreePath(characterNode.getPath()));
		settingsTree.expandPath(new TreePath(pcGenNode.getPath()));
		settingsTree.expandPath(new TreePath(appearanceNode.getPath()));

		// Add the listener which switches panels when a node of the tree is selected
		settingsTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) settingsTree.getLastSelectedPathComponent();

				if (node == null)
				{
					return;
				}

				CardLayout cl = (CardLayout) (settingsPanel.getLayout());
				cl.show(settingsPanel, String.valueOf(node));
			}
		});

		// Build the split pane
		splitPane = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, settingsScroll, settingsPanel);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		// Build the control panel (OK/Cancel buttons)
		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton(PropertyFactory.getString("in_ok"));
		okButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_ok"));
		controlPanel.add(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				okButtonActionPerformed(evt);
			}
		});

		JButton cancelButton = new JButton(PropertyFactory.getString("in_cancel"));
		cancelButton.setMnemonic(PropertyFactory.getMnemonic("in_mn_cancel"));
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
				if (pMode.length == 0)
				{
					abilitiesAllSameButton.setSelected(true);
				}
				else
				{
					abilitiesPurchasedButton.setSelected(true);
				}
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
		for (int i = 0; i < pMode.length; ++i)
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
		hideMonsterClasses.setSelected(SettingsHandler.hideMonsterClasses());
		ignoreMonsterHDCap.setSelected(SettingsHandler.isIgnoreMonsterHDCap());

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
		useOutputNames.setSelected(SettingsHandler.guiUsesOutputName());
		cmbChoiceMethods.setSelectedIndex(SettingsHandler.getSingleChoicePreference());

		// Look and feel
		int crossIndex = UIFactory.indexOfCrossPlatformLookAndFeel();
		if (SettingsHandler.getLookAndFeel() < laf.length)
		{
			laf[SettingsHandler.getLookAndFeel()].setSelected(true);
		}
		else if (SettingsHandler.getLookAndFeel() == laf.length)
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
			{
				skinnedLookFeel.setSelected(true);
			}
			else
			{
				laf[crossIndex].setSelected(true);
			}
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
		potionMaxLevel.setSelectedIndex(SettingsHandler.getMaxPotionSpellLevel() - SPELLLVLMIN);
		wandMaxLevel.setSelectedIndex(SettingsHandler.getMaxWandSpellLevel() - SPELLLVLMIN);
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
		langEng.setSelected(false);
		langFre.setSelected(false);
		langGer.setSelected(false);
		langIt.setSelected(false);
		if (Globals.getLanguage().equals("en"))
		{
			langEng.setSelected(true);
		}
		else if (Globals.getLanguage().equals("fr"))
		{
			langFre.setSelected(true);
		}
		else if (Globals.getLanguage().equals("de"))
		{
			langGer.setSelected(true);
		}
		else if (Globals.getLanguage().equals("it"))
		{
			langIt.setSelected(true);
		}
		else
		{
			// Default to English
			langEng.setSelected(true);
		}

		// Output
		paperType.setSelectedIndex(Globals.getSelectedPaper());
		weaponProfPrintout.setSelected(SettingsHandler.getWeaponProfPrintout());

		// Sources
		campLoad.setSelected(SettingsHandler.isLoadCampaignsAtStart());
		charCampLoad.setSelected(SettingsHandler.isLoadCampaignsWithPC());
		allowOptsInSource.setSelected(SettingsHandler.isOptionAllowedInSources());
		saveCustom.setSelected(SettingsHandler.getSaveCustomEquipment());
		showOGL.setSelected(SettingsHandler.showLicense());
		showd20.setSelected(SettingsHandler.showD20Info());
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
		SettingsHandler.setAllStatsValue(abilityScoreCombo.getSelectedIndex() + STATMIN);
		if (abilityPurchaseModeCombo.getSelectedIndex() > -1)
		{
			SettingsHandler.setPurchaseMethodName(pModeMethodName[abilityPurchaseModeCombo.getSelectedIndex()]);
		}
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
		SettingsHandler.setHideMonsterClasses(hideMonsterClasses.isSelected());
		SettingsHandler.setIgnoreMonsterHDCap(ignoreMonsterHDCap.isSelected());

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
		Utility.handleToolTipShownStateChange();
		SettingsHandler.setToolBarShown(showToolbar.isSelected());
		SettingsHandler.setUseExperimentalCursor(experimentalCursor.isSelected());
		SettingsHandler.setGUIUsesOutputName(useOutputNames.isSelected());
		SettingsHandler.setSingleChoicePreference(cmbChoiceMethods.getSelectedIndex());

		// Look and Feel
		int sourceIndex = 500;
		for (int i = 0; i < laf.length; ++i)
		{
			if (laf[i].isSelected())
			{
				sourceIndex = i;
			}
		}

		if (sourceIndex < laf.length)
		{
			if (SettingsHandler.getLookAndFeel() != sourceIndex)
			{
				SettingsHandler.setLookAndFeel(sourceIndex);
				UIFactory.setLookAndFeel(sourceIndex);
			}
		}
		else if (skinnedLookFeel.isSelected())
		{
			if ((SkinLFResourceChecker.getMissingResourceCount() == 0))
			{
				if (SettingsHandler.getSkinLFThemePack().length() == 0)
				{
					JOptionPane.showMessageDialog(this, PropertyFactory.getString("in_Prefs_noSkinError"), in_pcgen, JOptionPane.WARNING_MESSAGE);
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
						JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_Prefs_skinSetError") + e.toString(), in_pcgen, JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else
			{
				Globals.errorPrint(SkinLFResourceChecker.getMissingResourceMessage());

				String missingLibMsg = "";
				ResourceBundle d_properties;
				try
				{
					d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
					missingLibMsg = d_properties.getString("MissingLibMessage");
				}
				catch (MissingResourceException mrex)
				{
					missingLibMsg = "The skinned look and feel feature requires the download of the above mentioned file(s) " + "(http://sourceforge.net/projects/pcgen/).\n" + "Please download and place in the \"lib\" sub-directory of your PCGen installation.\n" + "You must then restart PCGen for full functionality.";
					Globals.errorPrint("Exception in PreferencesDialog::setOptionsBasedOnControls", mrex);
				}
				finally
				{
					d_properties = null;
				}
				missingLibMsg = missingLibMsg.replace('|', '\n');
				JOptionPane.showMessageDialog(null, SkinLFResourceChecker.getMissingResourceMessage() + '\n' + missingLibMsg, "PCGen", JOptionPane.WARNING_MESSAGE);
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

		// Language
		if (langEng.isSelected())
		{
			Globals.setLanguage("en");
			Globals.setCountry("US");
		}
		else if (langFre.isSelected())
		{
			Globals.setLanguage("fr");
			Globals.setCountry("FR");
		}
		else if (langGer.isSelected())
		{
			Globals.setLanguage("de");
			Globals.setCountry("DE");
		}
		else if (langIt.isSelected())
		{
			Globals.setLanguage("it");
			Globals.setCountry("IT");
		}

		// Output
		Globals.selectPaper((String) paperType.getSelectedItem());
		if ((SettingsHandler.getCleanupTempFiles() != 0) || removeTempFiles.isSelected())
		{
			SettingsHandler.setCleanupTempFiles(removeTempFiles.isSelected() ? 1 : -1);
		}
		if (SettingsHandler.getWeaponProfPrintout() != weaponProfPrintout.isSelected())
		{
			SettingsHandler.setWeaponProfPrintout(weaponProfPrintout.isSelected());
		}

		// Sources
		SettingsHandler.setLoadCampaignsAtStart(campLoad.isSelected());
		SettingsHandler.setLoadCampaignsWithPC(charCampLoad.isSelected());
		SettingsHandler.setOptionAllowedInSources(allowOptsInSource.isSelected());
		SettingsHandler.setSaveCustomEquipment(saveCustom.isSelected());
		SettingsHandler.setShowLicense(showOGL.isSelected());
		SettingsHandler.setShowD20Info(showd20.isSelected());
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
				final Color newColor = JColorChooser.showDialog(Globals.getRootFrame(), PropertyFactory.getString("in_Prefs_colorSelect") + source.getText().toLowerCase(), source.getForeground());
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
				themepackLabel.setText(String.valueOf(SettingsHandler.getSkinLFThemePack()));
			}
			else if (source == browserPathButton)
			{
				Utility.selectDefaultBrowser(getParent());
				browserPath.setText(String.valueOf(SettingsHandler.getBrowserPath()));
			}
			else if (source == clearBrowserPathButton)
			{
				// If none is set, there is nothing to clear
				if (SettingsHandler.getBrowserPath() == null)
				{
					return;
				}
				final int choice = JOptionPane.showConfirmDialog(null, PropertyFactory.getString("in_Prefs_clearBrowserWarn"), PropertyFactory.getString("in_Prefs_clearBrowserTitle"), JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION)
				{
					SettingsHandler.setBrowserPath(null);
				}
				browserPath.setText(String.valueOf(SettingsHandler.getBrowserPath()));
			}
			else if (source == pcgenCharacterDirButton)
			{
				final String dialogTitle = PropertyFactory.getString("in_Prefs_pcgenCharacterDirTitle");
				final File currentPath = SettingsHandler.getPcgPath();
				final JTextField textField = pcgenCharacterDir;
				final File returnFile = askForPath(currentPath, dialogTitle, textField);
				SettingsHandler.setPcgPath(returnFile);
			}
			else if (source == pcgenDataDirButton)
			{
				final String dialogTitle = PropertyFactory.getString("in_Prefs_pcgenDataDirTitle");
				final File currentPath = SettingsHandler.getPccFilesLocation();
				final JTextField textField = pcgenDataDir;
				final File returnFile = askForPath(currentPath, dialogTitle, textField);
				SettingsHandler.setPccFilesLocation(returnFile);
			}
			else if (source == pcgenDocsDirButton)
			{
				final String dialogTitle = PropertyFactory.getString("in_Prefs_pcgenDocsDirTitle");
				final File currentPath = SettingsHandler.getPcgenDocsDir();
				final JTextField textField = pcgenDocsDir;
				final File returnFile = askForPath(currentPath, dialogTitle, textField);
				SettingsHandler.setPcgenDocsDir(returnFile);
			}
			else if (source == pcgenSystemDirButton)
			{
				final String dialogTitle = PropertyFactory.getString("in_Prefs_pcgenSystemDirTitle");
				final File currentPath = SettingsHandler.getPcgenSystemDir();
				final JTextField textField = pcgenSystemDir;
				final File returnFile = askForPath(currentPath, dialogTitle, textField);
				SettingsHandler.setPcgenSystemDir(returnFile);
			}
			else if (source == outputSheetHTMLDefaultButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(PropertyFactory.getString("in_Prefs_outputSheetHTMLDefaultTitle"));
				fc.setCurrentDirectory(new File(SettingsHandler.getHTMLOutputSheetPath()));
				fc.setSelectedFile(new File(SettingsHandler.getSelectedHTMLOutputSheet()));
				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();
					if (newTemplate.isDirectory() || (!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet")))
					{
						JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_Prefs_outputSheetDefaultError"), in_pcgen, JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler.setSelectedHTMLOutputSheet(newTemplate.getAbsolutePath());
							SettingsHandler.setTemplatePath(newTemplate.getParentFile().getParentFile());
						}
						else
						{
							//it must be a psheet
							SettingsHandler.setSelectedPartyTemplate(newTemplate.getAbsolutePath());
							SettingsHandler.setTemplatePath(newTemplate.getParentFile().getParentFile());
						}
					}
				}
				outputSheetHTMLDefault.setText(String.valueOf(SettingsHandler.getSelectedHTMLOutputSheet()));
			}
			else if (source == outputSheetPDFDefaultButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(PropertyFactory.getString("in_Prefs_outputSheetPDFDefaultTitle"));
				fc.setCurrentDirectory(new File(SettingsHandler.getPDFOutputSheetPath()));
				fc.setSelectedFile(new File(SettingsHandler.getSelectedPDFOutputSheet()));
				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();
					if (newTemplate.isDirectory() || (!newTemplate.getName().startsWith("csheet") && !newTemplate.getName().startsWith("psheet")))
					{
						JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_Prefs_outputSheetDefaultError"), in_pcgen, JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						if (newTemplate.getName().startsWith("csheet"))
						{
							SettingsHandler.setSelectedPDFOutputSheet(newTemplate.getAbsolutePath());
							SettingsHandler.setTemplatePath(newTemplate.getParentFile().getParentFile());
						}
						else
						{
							//it must be a psheet
							SettingsHandler.setSelectedPartyTemplate(newTemplate.getAbsolutePath());
							SettingsHandler.setTemplatePath(newTemplate.getParentFile().getParentFile());
						}
					}
				}
				outputSheetPDFDefault.setText(String.valueOf(SettingsHandler.getSelectedPDFOutputSheet()));
			}
			else if (source == outputSheetEqSetButton)
			{
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle(PropertyFactory.getString("in_Prefs_templateEqSetTitle"));
				fc.setCurrentDirectory(SettingsHandler.getTemplatePath());
				fc.setSelectedFile(new File(SettingsHandler.getSelectedEqSetTemplate()));
				if (fc.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION)
				{
					File newTemplate = fc.getSelectedFile();
					if (newTemplate.isDirectory() || !newTemplate.getName().startsWith("eqsheet"))
					{
						JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_Prefs_templateEqSetError"), in_pcgen, JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						//it must be a psheet
						SettingsHandler.setSelectedEqSetTemplate(newTemplate.getAbsolutePath());
						SettingsHandler.setTemplatePath(newTemplate.getParentFile());
					}
				}
				outputSheetEqSet.setText(String.valueOf(SettingsHandler.getSelectedEqSetTemplate()));
			}
		}

		/**
		 * Ask for a path, and return it (possibly return the currentPath.)
		 * @param currentPath when entering the method
		 * @param dialogTitle to show
		 * @param textField to update with the path information
		 * @return A path to the directory.
		 */
		private File askForPath(final File currentPath, final String dialogTitle, final JTextField textField)
		{
			File returnFile = currentPath;
			JFileChooser fc = null;
			if (currentPath == null)
			{
				fc = new JFileChooser();
			}
			else
			{
				fc = new JFileChooser(currentPath);
			}
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setDialogTitle(dialogTitle);
			if (System.getProperty("os.name").startsWith("Mac OS"))
			{
				// On MacOS X, do not traverse file bundles
				fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
			}

			final int returnVal = fc.showOpenDialog(getParent());
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				returnFile = fc.getSelectedFile();
			}
			textField.setText(String.valueOf(returnFile));
			return returnFile;
		}
	}

	private JPanel buildAbilitiesPanel()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JLabel label;
		ButtonGroup exclusiveGroup;
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_abilities);
		JPanel abilityScoresPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		abilityScoresPanel.setBorder(title1);
		abilityScoresPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_abilitiesGenLabel") + ": ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);

		Utility.buildConstraints(c, 1, 1, 2, 1, 0, 0);
		abilitiesUserRolledButton = new JRadioButton(PropertyFactory.getString("in_Prefs_abilitiesUserRolled"));
		gridbag.setConstraints(abilitiesUserRolledButton, c);
		abilityScoresPanel.add(abilitiesUserRolledButton);
		exclusiveGroup.add(abilitiesUserRolledButton);

		Utility.buildConstraints(c, 1, 2, 2, 1, 0, 0);
		abilitiesAllSameButton = new JRadioButton(PropertyFactory.getString("in_Prefs_abilitiesAllSame") + ": ");
		gridbag.setConstraints(abilitiesAllSameButton, c);
		abilityScoresPanel.add(abilitiesAllSameButton);
		exclusiveGroup.add(abilitiesAllSameButton);
		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		Utility.buildConstraints(c, 2, 3, 2, 1, 0, 0);
		for (int i = STATMIN; i <= STATMAX; ++i)
		{
			allSameValue[i - STATMIN] = String.valueOf(i);
		}
		abilityScoreCombo = new JComboBox(allSameValue);
		gridbag.setConstraints(abilityScoreCombo, c);
		abilityScoresPanel.add(abilityScoreCombo);

		Utility.buildConstraints(c, 1, 4, 2, 1, 0, 0);
		abilitiesPurchasedButton = new JRadioButton(PropertyFactory.getString("in_Prefs_abilitiesPurchased") + ": ");
		gridbag.setConstraints(abilitiesPurchasedButton, c);
		abilityScoresPanel.add(abilitiesPurchasedButton);
		exclusiveGroup.add(abilitiesPurchasedButton);
		Utility.buildConstraints(c, 2, 5, 2, 1, 0, 0);
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

		Utility.buildConstraints(c, 1, 6, 1, 1, 0, 0);
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		abilityScoresPanel.add(label);
		Utility.buildConstraints(c, 1, 7, 3, 1, 0, 0);
		purchaseMode = new JButton(PropertyFactory.getString("in_Prefs_purchaseModeConfig"));
		gridbag.setConstraints(purchaseMode, c);
		abilityScoresPanel.add(purchaseMode);
		purchaseMode.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_hp);
		JPanel hitPointsPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		hitPointsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		hitPointsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hpGenLabel") + ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel("  ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);

		Utility.buildConstraints(c, 1, 1, 2, 1, 0, 0);
		gridbag.setConstraints(hpStandard, c);
		hitPointsPanel.add(hpStandard);
		exclusiveGroup.add(hpStandard);

		Utility.buildConstraints(c, 1, 2, 2, 1, 0, 0);
		gridbag.setConstraints(hpAutomax, c);
		hitPointsPanel.add(hpAutomax);
		exclusiveGroup.add(hpAutomax);

		Utility.buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hpMaxAtFirst") + ": ");
		gridbag.setConstraints(label, c);
		hitPointsPanel.add(label);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(maxHpAtFirstLevel, c);
		hitPointsPanel.add(maxHpAtFirstLevel);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_houseRules);
		JPanel houseRulesPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		houseRulesPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		houseRulesPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrLoadPenalty") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(applyLoadPenaltyToACandSkills, c);
		houseRulesPanel.add(applyLoadPenaltyToACandSkills);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrWeightPenalty") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(applyWeightPenaltyToSkills, c);
		houseRulesPanel.add(applyWeightPenaltyToSkills);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrFreeClothing") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(freeClothesAtFirst, c);
		houseRulesPanel.add(freeClothesAtFirst);

		Utility.buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrBypassClassPrereq") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(bypassClassPreReqsCheckbox, c);
		houseRulesPanel.add(bypassClassPreReqsCheckbox);

		Utility.buildConstraints(c, 0, 4, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrBypassFeatPrereq") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		gridbag.setConstraints(bypassFeatPreReqsCheckbox, c);
		houseRulesPanel.add(bypassFeatPreReqsCheckbox);

		Utility.buildConstraints(c, 0, 5, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrBypassMaxSkill") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 5, 1, 1, 0, 0);
		gridbag.setConstraints(bypassMaxSkillRankCheckbox, c);
		houseRulesPanel.add(bypassMaxSkillRankCheckbox);

		Utility.buildConstraints(c, 0, 6, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrCrossSkillCost") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 6, 1, 1, 0, 0);
		gridbag.setConstraints(crossClassSkillCostCombo, c);
		houseRulesPanel.add(crossClassSkillCostCombo);

		Utility.buildConstraints(c, 0, 7, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrIncSkillPreLevel") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 7, 1, 1, 0, 0);
		gridbag.setConstraints(skillIncrementBefore, c);
		houseRulesPanel.add(skillIncrementBefore);

		Utility.buildConstraints(c, 0, 8, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrInHandAsEquip") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 8, 1, 1, 0, 0);
		gridbag.setConstraints(treatInHandAsEquippedForAttacks, c);
		houseRulesPanel.add(treatInHandAsEquippedForAttacks);

		Utility.buildConstraints(c, 0, 9, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hrIgnoreLevelCap") + ": ");
		gridbag.setConstraints(label, c);
		houseRulesPanel.add(label);
		Utility.buildConstraints(c, 2, 9, 1, 1, 0, 0);
		gridbag.setConstraints(ignoreLevelCap, c);
		houseRulesPanel.add(ignoreLevelCap);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_monsters);
		JPanel monstersPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		monstersPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		monstersPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_defaultMonsters") + ": ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(useMonsterDefault, c);
		monstersPanel.add(useMonsterDefault);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_hideMonsterClasses") + ": ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(hideMonsterClasses, c);
		monstersPanel.add(hideMonsterClasses);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_ignoreMonsterHDCap") + ": ");
		gridbag.setConstraints(label, c);
		monstersPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(ignoreMonsterHDCap, c);
		monstersPanel.add(ignoreMonsterHDCap);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		prereqFailColor = new JButton(in_colorPrereqFail);
		gridbag.setConstraints(prereqFailColor, c);
		colorsPanel.add(prereqFailColor);
		prereqFailColor.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		featAutoColor = new JButton(in_colorAutoFeat);
		gridbag.setConstraints(featAutoColor, c);
		colorsPanel.add(featAutoColor);
		featAutoColor.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		featVirtualColor = new JButton(in_colorVirtFeat);
		gridbag.setConstraints(featVirtualColor, c);
		colorsPanel.add(featVirtualColor);
		featVirtualColor.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_mainTabPlacement + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		mainTabPlacementCombo = new JComboBox(new String[]{in_tabPosTop, in_tabPosBottom, in_tabPosLeft, in_tabPosRight});
		gridbag.setConstraints(mainTabPlacementCombo, c);
		tabsPanel.add(mainTabPlacementCombo);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(in_charTabPlacement + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		charTabPlacementCombo = new JComboBox(new String[]{in_tabPosTop, in_tabPosBottom, in_tabPosLeft, in_tabPosRight});
		gridbag.setConstraints(charTabPlacementCombo, c);
		tabsPanel.add(charTabPlacementCombo);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_charTabLabel + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		tabLabelsCombo = new JComboBox(new String[]{in_tabLabelPlain, in_tabLabelEpic, in_tabLabelRace, in_tabLabelNetHack});
		gridbag.setConstraints(tabLabelsCombo, c);
		tabsPanel.add(tabLabelsCombo);

		Utility.buildConstraints(c, 0, 3, 2, 1, 0, 0);
		label = new JLabel(in_tabAbilities + ": ");
		gridbag.setConstraints(label, c);
		tabsPanel.add(label);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		gridbag.setConstraints(displayAbilitiesAsTab, c);
		tabsPanel.add(displayAbilitiesAsTab);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		int line = 0;

		title1.setTitleJustification(TitledBorder.LEFT);
		displayOptsPanel.setBorder(title1);
		gridbag = new GridBagLayout();
		displayOptsPanel.setLayout(gridbag);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, line, 2, 1, 0, 0);
		label = new JLabel(in_showToolTips + ": ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		Utility.buildConstraints(c, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(toolTipTextShown, c);
		displayOptsPanel.add(toolTipTextShown);

		Utility.buildConstraints(c, 0, ++line, 2, 1, 0, 0);
		label = new JLabel(in_showToolBar + ": ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		Utility.buildConstraints(c, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(showToolbar, c);
		displayOptsPanel.add(showToolbar);

		Utility.buildConstraints(c, 0, ++line, 2, 1, 0, 0);
		label = new JLabel(in_useAutoWaitCursor + ": ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		Utility.buildConstraints(c, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(experimentalCursor, c);
		displayOptsPanel.add(experimentalCursor);

		Utility.buildConstraints(c, 0, ++line, 2, 1, 0, 0);
		label = new JLabel(in_useOutputNames + ": ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		Utility.buildConstraints(c, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(useOutputNames, c);
		displayOptsPanel.add(useOutputNames);

		Utility.buildConstraints(c, 0, ++line, 2, 1, 0, 0);
		label = new JLabel(in_singleChoiceOption + ": ");
		gridbag.setConstraints(label, c);
		displayOptsPanel.add(label);
		Utility.buildConstraints(c, 2, line, 1, 1, 0, 0);
		gridbag.setConstraints(cmbChoiceMethods, c);
		displayOptsPanel.add(cmbChoiceMethods);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);

		exclusiveGroup = new ButtonGroup();
		laf = new JRadioButton[UIFactory.getLookAndFeelCount()];
		for (int i = 0; i < laf.length; ++i)
		{
			laf[i] = new JRadioButton();
			laf[i].setText(UIFactory.getLookAndFeelName(i));
			Utility.setDescription(laf[i], UIFactory.getLookAndFeelTooltip(i));
			if (laf[i].getText().charAt(0) != 'C')
			{
				laf[i].setMnemonic(laf[i].getText().charAt(0));
			}
			else
			{
				laf[i].setMnemonic(laf[i].getText().charAt(1));
			}
			Utility.buildConstraints(c, 0, i, 3, 1, 0, 0);
			gridbag.setConstraints(laf[i], c);
			lafPanel.add(laf[i]);
			exclusiveGroup.add(laf[i]);
		}

		skinnedLookFeel.setText(in_skinnedLAF + ": ");
		Utility.setDescription(skinnedLookFeel, PropertyFactory.getString("in_Prefs_skinnedLAFTooltip"));
		skinnedLookFeel.setMnemonic(PropertyFactory.getMnemonic("in_mn_Prefs_skinnedLAF"));
		Utility.buildConstraints(c, 0, laf.length, 3, 1, 0, 0);
		gridbag.setConstraints(skinnedLookFeel, c);
		lafPanel.add(skinnedLookFeel);
		exclusiveGroup.add(skinnedLookFeel);

		Utility.buildConstraints(c, 3, laf.length, 1, 1, 1, 0);
		themepackLabel = new JTextField(SettingsHandler.getSkinLFThemePack());
		themepackLabel.setEditable(false);
		gridbag.setConstraints(themepackLabel, c);
		lafPanel.add(themepackLabel);
		Utility.buildConstraints(c, 4, laf.length, 1, 1, 0, 0);
		themepack = new JButton(in_choose);
		Utility.setDescription(themepack, PropertyFactory.getString("in_Prefs_chooseSkinTooltip"));
		gridbag.setConstraints(themepack, c);
		lafPanel.add(themepack);
		themepack.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 20, 5, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		label = new JLabel(in_hpWindow + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		gridbag.setConstraints(hpDialogShownAtLevelUp, c);
		levelUpPanel.add(hpDialogShownAtLevelUp);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		label = new JLabel(in_featWindow + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		gridbag.setConstraints(featDialogShownAtLevelUp, c);
		levelUpPanel.add(featDialogShownAtLevelUp);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		label = new JLabel(in_statWindow + ": ");
		gridbag.setConstraints(label, c);
		levelUpPanel.add(label);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		gridbag.setConstraints(statDialogShownAtLevelUp, c);
		levelUpPanel.add(statDialogShownAtLevelUp);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(in_allowMetamagic + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 0, 1, 1, 0, 0);
		gridbag.setConstraints(allowMetamagicInEqBuilder, c);
		equipmentPanel.add(allowMetamagicInEqBuilder);

		Utility.buildConstraints(c, 0, 1, 3, 1, 0, 0);
		label = new JLabel(in_potionMax + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		for (int i = SPELLLVLMIN; i <= SPELLLVLMAX; ++i)
		{
			potionSpellLevel[i - SPELLLVLMIN] = String.valueOf(i) + "  ";
		}
		potionMaxLevel = new JComboBox(potionSpellLevel);
		gridbag.setConstraints(potionMaxLevel, c);
		equipmentPanel.add(potionMaxLevel);

		Utility.buildConstraints(c, 0, 2, 3, 1, 0, 0);
		label = new JLabel(in_wandMax + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);
		for (int i = SPELLLVLMIN; i <= SPELLLVLMAX; ++i)
		{
			wandSpellLevel[i - SPELLLVLMIN] = String.valueOf(i) + "  ";
		}
		wandMaxLevel = new JComboBox(wandSpellLevel);
		gridbag.setConstraints(wandMaxLevel, c);
		equipmentPanel.add(wandMaxLevel);

		Utility.buildConstraints(c, 0, 3, 3, 1, 0, 0);
		label = new JLabel(in_anyAutoEquip + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);

		Utility.buildConstraints(c, 0, 4, 2, 1, 0, 0);
		noAutoEquipCreate = new JRadioButton(in_noAutoEquip);
		gridbag.setConstraints(noAutoEquipCreate, c);
		equipmentPanel.add(noAutoEquipCreate);
		exclusiveGroup.add(noAutoEquipCreate);

		Utility.buildConstraints(c, 0, 5, 2, 1, 0, 0);
		autoEquipCreate = new JRadioButton(in_autoEquip + ": ");
		gridbag.setConstraints(autoEquipCreate, c);
		equipmentPanel.add(autoEquipCreate);
		exclusiveGroup.add(autoEquipCreate);

		Utility.buildConstraints(c, 0, 6, 1, 1, 0, 0);
		label = new JLabel("    ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 1, 6, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipRace + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 6, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod1, c);
		equipmentPanel.add(autoMethod1);

		Utility.buildConstraints(c, 1, 7, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipMasterwork + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 7, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod2, c);
		equipmentPanel.add(autoMethod2);

		Utility.buildConstraints(c, 1, 8, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipMagic + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 8, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod3, c);
		equipmentPanel.add(autoMethod3);

		Utility.buildConstraints(c, 1, 9, 2, 1, 0, 0);
		label = new JLabel(in_autoEquipExotic + ": ");
		gridbag.setConstraints(label, c);
		equipmentPanel.add(label);
		Utility.buildConstraints(c, 3, 9, 1, 1, 0, 0);
		gridbag.setConstraints(autoMethod4, c);
		equipmentPanel.add(autoMethod4);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_language);
		JPanel langPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		langPanel.setBorder(title1);
		langPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		Utility.buildConstraints(c, 0, 0, 2, 1, 0, 0);
		langEng = new JRadioButton(in_langEnglish);
		gridbag.setConstraints(langEng, c);
		langPanel.add(langEng);
		exclusiveGroup.add(langEng);

		Utility.buildConstraints(c, 0, 1, 2, 1, 0, 0);
		langFre = new JRadioButton(in_langFrench);
		gridbag.setConstraints(langFre, c);
		langPanel.add(langFre);
		exclusiveGroup.add(langFre);
		//langFre.setEnabled(false);

		Utility.buildConstraints(c, 0, 2, 2, 1, 0, 0);
		langGer = new JRadioButton(in_langGerman);
		gridbag.setConstraints(langGer, c);
		langPanel.add(langGer);
		exclusiveGroup.add(langGer);
		//langGer.setEnabled(false);

		Utility.buildConstraints(c, 0, 3, 2, 1, 0, 0);
		langIt = new JRadioButton(in_langItalian);
		gridbag.setConstraints(langIt, c);
		langPanel.add(langIt);
		exclusiveGroup.add(langIt);
		//langIt.setEnabled(false);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		label = new JLabel(in_browserPath + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		browserPath = new JTextField(String.valueOf(SettingsHandler.getBrowserPath()));
		browserPath.setEditable(false);
		gridbag.setConstraints(browserPath, c);
		locationPanel.add(browserPath);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		browserPathButton = new JButton(in_choose);
		gridbag.setConstraints(browserPathButton, c);
		locationPanel.add(browserPathButton);
		browserPathButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 1, 1, 1, 1, 0, 0);
		clearBrowserPathButton = new JButton(in_clearBrowserPath);
		gridbag.setConstraints(clearBrowserPathButton, c);
		locationPanel.add(clearBrowserPathButton);
		clearBrowserPathButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_pcgenCharacterDir") + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 2, 1, 1, 0, 0);
		pcgenCharacterDir = new JTextField(String.valueOf(SettingsHandler.getPcgPath()));
		pcgenCharacterDir.setEditable(false);
		gridbag.setConstraints(pcgenCharacterDir, c);
		locationPanel.add(pcgenCharacterDir);
		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
		pcgenCharacterDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenCharacterDirButton, c);
		locationPanel.add(pcgenCharacterDirButton);
		pcgenCharacterDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_pcgenDataDir") + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		pcgenDataDir = new JTextField(String.valueOf(SettingsHandler.getPccFilesLocation()));
		pcgenDataDir.setEditable(false);
		gridbag.setConstraints(pcgenDataDir, c);
		locationPanel.add(pcgenDataDir);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		pcgenDataDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenDataDirButton, c);
		locationPanel.add(pcgenDataDirButton);
		pcgenDataDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_pcgenDocsDir") + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		pcgenDocsDir = new JTextField(String.valueOf(SettingsHandler.getPcgenDocsDir()));
		pcgenDocsDir.setEditable(false);
		gridbag.setConstraints(pcgenDocsDir, c);
		locationPanel.add(pcgenDocsDir);
		Utility.buildConstraints(c, 2, 4, 1, 1, 0, 0);
		pcgenDocsDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenDocsDirButton, c);
		locationPanel.add(pcgenDocsDirButton);
		pcgenDocsDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 5, 1, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_pcgenSystemDir") + ": ");
		gridbag.setConstraints(label, c);
		locationPanel.add(label);
		Utility.buildConstraints(c, 1, 5, 1, 1, 0, 0);
		pcgenSystemDir = new JTextField(String.valueOf(SettingsHandler.getPcgenSystemDir()));
		pcgenSystemDir.setEditable(false);
		gridbag.setConstraints(pcgenSystemDir, c);
		locationPanel.add(pcgenSystemDir);
		Utility.buildConstraints(c, 2, 5, 1, 1, 0, 0);
		pcgenSystemDirButton = new JButton(in_choose);
		gridbag.setConstraints(pcgenSystemDirButton, c);
		locationPanel.add(pcgenSystemDirButton);
		pcgenSystemDirButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 20, 3, 1, 1, 1);
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
		Border etched = null;
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, in_output);
		JPanel outputPanel = new JPanel();

		title1.setTitleJustification(TitledBorder.LEFT);
		outputPanel.setBorder(title1);
		outputPanel.setLayout(gridbag);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_outputSheetHTMLDefault") + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		outputSheetHTMLDefault = new JTextField(String.valueOf(SettingsHandler.getSelectedHTMLOutputSheet()));
		outputSheetHTMLDefault.setEditable(false);
		gridbag.setConstraints(outputSheetHTMLDefault, c);
		outputPanel.add(outputSheetHTMLDefault);
		Utility.buildConstraints(c, 2, 0, 1, 1, 0, 0);
		outputSheetHTMLDefaultButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetHTMLDefaultButton, c);
		outputPanel.add(outputSheetHTMLDefaultButton);
		outputSheetHTMLDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_outputSheetPDFDefault") + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 1, 1, 1, 1, 0);
		outputSheetPDFDefault = new JTextField(String.valueOf(SettingsHandler.getSelectedPDFOutputSheet()));
		outputSheetPDFDefault.setEditable(false);
		gridbag.setConstraints(outputSheetPDFDefault, c);
		outputPanel.add(outputSheetPDFDefault);
		Utility.buildConstraints(c, 2, 1, 1, 1, 0, 0);
		outputSheetPDFDefaultButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetPDFDefaultButton, c);
		outputPanel.add(outputSheetPDFDefaultButton);
		outputSheetPDFDefaultButton.addActionListener(prefsButtonHandler);

//		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 0);
//		label = new JLabel(PropertyFactory.getString("in_Prefs_outputSheetTextDefault") + ": ");
//		gridbag.setConstraints(label, c);
//		outputPanel.add(label);
//		Utility.buildConstraints(c, 1, 2, 1, 1, 1, 0);
//		outputSheetTextDefault = new JTextField(String.valueOf(SettingsHandler.getSelectedTextOutputSheet()));
//		outputSheetTextDefault.setEditable(false);
//		gridbag.setConstraints(outputSheetTextDefault, c);
//		outputPanel.add(outputSheetTextDefault);
//		Utility.buildConstraints(c, 2, 2, 1, 1, 0, 0);
//		outputSheetTextDefaultButton = new JButton(in_choose);
//		gridbag.setConstraints(outputSheetTextDefaultButton, c);
//		outputPanel.add(outputSheetTextDefaultButton);
//		outputSheetTextDefaultButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 3, 1, 1, 0, 0);
		label = new JLabel(in_outputSheetEqSet + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 3, 1, 1, 0, 0);
		outputSheetEqSet = new JTextField(String.valueOf(SettingsHandler.getSelectedEqSetTemplate()));
		outputSheetEqSet.setEditable(false);
		gridbag.setConstraints(outputSheetEqSet, c);
		outputPanel.add(outputSheetEqSet);
		Utility.buildConstraints(c, 2, 3, 1, 1, 0, 0);
		outputSheetEqSetButton = new JButton(in_choose);
		gridbag.setConstraints(outputSheetEqSetButton, c);
		outputPanel.add(outputSheetEqSetButton);
		outputSheetEqSetButton.addActionListener(prefsButtonHandler);

		Utility.buildConstraints(c, 0, 4, 1, 1, 0, 0);
		label = new JLabel(in_paperType + ": ");
		gridbag.setConstraints(label, c);
		outputPanel.add(label);
		Utility.buildConstraints(c, 1, 4, 1, 1, 0, 0);
		final int paperCount = Globals.getPaperCount();
		paperNames = new String[paperCount];
		for (int i = 0; i < paperCount; ++i)
		{
			paperNames[i] = Globals.getPaperInfo(i, Constants.PAPERINFO_NAME);
		}
		paperType = new JComboBox(paperNames);
		gridbag.setConstraints(paperType, c);
		outputPanel.add(paperType);

		Utility.buildConstraints(c, 0, 5, 3, 1, 0, 0);
		removeTempFiles = new JCheckBox(in_removeTemp, SettingsHandler.getCleanupTempFiles() > 0);
		gridbag.setConstraints(removeTempFiles, c);
		outputPanel.add(removeTempFiles);

		Utility.buildConstraints(c, 0, 6, 3, 1, 0, 0);
		weaponProfPrintout = new JCheckBox(in_weaponProfPrintout, SettingsHandler.getWeaponProfPrintout());
		gridbag.setConstraints(weaponProfPrintout, c);
		outputPanel.add(weaponProfPrintout);

		Utility.buildConstraints(c, 0, 20, 3, 1, 1, 1);
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
		c.insets = new Insets(2, 2, 2, 2);
		exclusiveGroup = new ButtonGroup();

		Utility.buildConstraints(c, 0, 0, 3, 1, 0, 0);
		label = new JLabel(in_autoLoadAtStart + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 0, 1, 1, 0, 0);
		gridbag.setConstraints(campLoad, c);
		sourcesPanel.add(campLoad);

		Utility.buildConstraints(c, 0, 1, 3, 1, 0, 0);
		label = new JLabel(in_autoLoadWithPC + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 1, 1, 1, 0, 0);
		gridbag.setConstraints(charCampLoad, c);
		sourcesPanel.add(charCampLoad);

		Utility.buildConstraints(c, 0, 2, 3, 1, 0, 0);
		label = new JLabel(PropertyFactory.getString("in_Prefs_allowOptionInSource") + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 2, 1, 1, 0, 0);
		gridbag.setConstraints(allowOptsInSource, c);
		sourcesPanel.add(allowOptsInSource);

		Utility.buildConstraints(c, 0, 3, 3, 1, 0, 0);
		label = new JLabel(in_saveCustom + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 3, 1, 1, 0, 0);
		gridbag.setConstraints(saveCustom, c);
		sourcesPanel.add(saveCustom);

		Utility.buildConstraints(c, 0, 4, 3, 1, 0, 0);
		label = new JLabel(in_displayOGL + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 4, 1, 1, 0, 0);
		gridbag.setConstraints(showOGL, c);
		sourcesPanel.add(showOGL);

		Utility.buildConstraints(c, 0, 5, 3, 1, 0, 0);
		label = new JLabel(in_displayd20 + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 5, 1, 1, 0, 0);
		gridbag.setConstraints(showd20, c);
		sourcesPanel.add(showd20);

		Utility.buildConstraints(c, 0, 6, 3, 1, 0, 0);
		label = new JLabel(in_sourceDisplay + ": ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);
		Utility.buildConstraints(c, 3, 6, 1, 1, 0, 0);
		sourceOptions = new JComboBox(new String[]{in_sdLong, in_sdShort, in_sdPage, in_sdWeb});
		gridbag.setConstraints(sourceOptions, c);
		sourcesPanel.add(sourceOptions);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(" ");
		gridbag.setConstraints(label, c);
		sourcesPanel.add(label);

		return sourcesPanel;
	}

	private JPanel buildEmptyPanel(String title, String messageText)
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
		c.insets = new Insets(2, 2, 2, 2);

		Utility.buildConstraints(c, 5, 20, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		label = new JLabel(messageText, SwingConstants.CENTER);
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
					final int purchaseMethodCount = SettingsHandler.getPurchaseMethodCount();
					pMode = new String[purchaseMethodCount];
					pModeMethodName = new String[purchaseMethodCount];
					final String methodName = SettingsHandler.getPurchaseModeMethodName();
					abilityPurchaseModeCombo.removeAllItems();
					for (int i = 0; i < purchaseMethodCount; ++i)
					{
						final PointBuyMethod pbm = SettingsHandler.getPurhaseMethod(i);
						pMode[i] = pbm.getMethodName() + " (" + pbm.getPoints() + ')';
						pModeMethodName[i] = pbm.getMethodName();
						abilityPurchaseModeCombo.addItem(pMode[i]);
						if (pModeMethodName[i].equals(methodName))
						{
							abilityPurchaseModeCombo.setSelectedIndex(i);
						}

					}
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
		fc.setDialogTitle(PropertyFactory.getString("in_Prefs_chooseSkinDialogTitle"));
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
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_Prefs_notAThemeErrorItem"), in_pcgen, JOptionPane.ERROR_MESSAGE);
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
						SettingsHandler.setLookAndFeel(0);
						UIFactory.setLookAndFeel(0);
						JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_Prefs_skinSetError") + e.toString(), in_pcgen, JOptionPane.ERROR_MESSAGE);
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
