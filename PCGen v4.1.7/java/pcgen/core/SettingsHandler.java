/*
 * SettingsHandler.java
 * Copyright 2001 (C) Jonas Karlsson
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
 * Created on July 10, 2002, 2:15 PM
 *
 * $Id: SettingsHandler.java,v 1.1 2006/02/21 00:57:42 vauchers Exp $
 */
package pcgen.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.SwingConstants;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.GuiFacade;

/**
 * This class contains all settings-related code moved from Globals.java
 *
 * Should be cleaned up more.
 *
 * @author jujutsunerd
 * @version $Revision: 1.1 $
 */

public final class SettingsHandler
{
	private static int allStatsValue = 10;

	private static boolean abilitiesShownAsTab = false;
	private static boolean applyLoadPenaltyToACandSkills = true;
	private static boolean applyWeightPenaltyToSkills = true;
	private static boolean autoFeatsRefundable = false;
	private static boolean autogenExoticMaterial = false;
	private static boolean autogenMagic = false;
	private static boolean autogenMasterwork = false;
	private static boolean autogenRacial = false;

	//
	// For EqBuilder
	//
	private static int maxPotionSpellLevel = 3;
	private static int maxWandSpellLevel = 4;
	private static boolean allowMetamagicInCustomizer = false;
	private static boolean spellMarketPriceAdjusted = false;

	private static boolean boolBypassClassPreReqs = false;
	private static boolean boolBypassFeatPreReqs = false;
	private static boolean boolBypassMaxSkillRank = false;
	private static final boolean classDisplayedWithName = false;
	/** That browserPath is set to null is intentional. */
	private static File browserPath = null; //Intentional null
	/**
	 *  See @javax.swing.SwingConstants
	 */
	private static int chaTabPlacement = SwingConstants.TOP;
	private static int classTab_AvailableListMode = GuiConstants.INFOCLASS_VIEW_NAME;
	private static int classTab_SelectedListMode = GuiConstants.INFOCLASS_VIEW_NAME;
	private static Dimension customizerDimension = null;
	private static Point customizerLeftUpperCorner = null;
	private static int customizerSplit1 = -1;
	private static int customizerSplit2 = -1;
	private static String dmNotes = "";
	private static int equipTab_AvailableListMode = GuiConstants.INFOEQUIPPING_VIEW_TYPE;
	private static int equipTab_SelectedListMode = GuiConstants.INFOEQUIPPING_VIEW_NAME;
	private static int excSkillCost = 0;
	private static int featAutoColor = 0xB2B200;			// dark yellow
	private static int featTab_AvailableListMode = GuiConstants.INFOFEATS_VIEW_TYPENAME;
	private static int featTab_SelectedListMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;
	private static int featVirtualColor = 0xFF00FF;			// magenta
	private static Properties filterSettings = new Properties();
	private static boolean freeClothesAtFirst = true;
	private static GameMode game = null;		// no point in this, modes not yet loaded...Globals.getGameModeNamed(Constants.DND_MODE);
	private static boolean grimHPMode = false;
	private static boolean grittyACMode = false;
	private static boolean useExperimentalCursor = true;
	private static boolean showD20InfoAtStart = true;
	private static boolean showOGLOnLoad = true;
	private static boolean hpMaxAtFirstLevel = true;
	private static int hpRollMethod = Constants.s_HP_STANDARD;
	private static File htmlOutputPath = new File(Globals.getDefaultPath());
	private static boolean ignoreEquipmentCost = false;
	private static boolean ignoreLevelCap = false;
	/**
	 *  0==None, 1=Untrained, 2=all
	 */
	private static int includeSkills = 1;
	//private static int initialStatMax = 18;
	//private static int initialStatMin = 3;
	private static int intCrossClassSkillCost = 2;
	private static int inventoryTab_AvailableListMode = GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME;
	//private static boolean inventoryTab_IgnoreCost = false;
	private static int inventoryTab_SellRate = 50;
	private static int inventoryTab_BuyRate = 100;
	private static int inventoryTab_SelectedListMode = GuiConstants.INFOINVENTORY_VIEW_NAME;
	private static boolean isROG = false;
	private static Point leftUpperCorner = null;
	private static boolean loadCampaignsAtStart = false;
	private static boolean loadCampaignsWithPC = true;
	private static int looknFeel = 1; // default to System L&F
	private static boolean optionAllowedInSources = true;
	private static Properties options = new Properties();
	private static File pccFilesLocation = null;
	private static File pcgPath = new File(Globals.getDefaultPath());
	private static File pcgenDocsDir = null;
	/**
	 * Where to load the system lst files from.
	 */
	private static File pcgenSystemDir = new File(Globals.getDefaultPath() + File.separator + "system");
	private static File pcgenThemePackDir = new File(Globals.getDefaultPath() + File.separator + "lib" + File.separator + "themes");
	private static int prereqFailColor = 0xFF0000;			// 0 = black, 0xFF0000 = red, 0xFFFFFF = white
	private static boolean previewTabShown = false;
	//private static boolean purchaseStatMode = false;
	private static String purchaseMethodName = "";
/////////////////////////////////////////////////
// Yanked for WotC compliance
//	private static boolean maxStartingGold = false;
	//private static int purchaseAbilityScoreMin = 8;
	//private static int purchaseAbilityScoreMax = 18;
//	/**
//	 *  This will be overridden at startup with values read from properties.
//	 */
	private static int[] abilityScoreCost = null;
//	private static int[] abilityScoreCost = new int[]{
//		0,
//		1,
//		2,
//		3,
//		4,
//		5,
//		6,
//		8,
//		10,
//		13,
//		16
//	};
	private static SortedMap pointBuyStatCosts = null;
	private static ArrayList pointBuyMethods = null;
/////////////////////////////////////////////////

	private static int raceTab_ListMode = GuiConstants.INFORACE_VIEW_NAME;
	private static boolean ranStartingWizard = false;
	/**
	 * Method:
	 * 0: One random number
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * 7: 5d6 Drop lowest and middle FR #458917
	 * 8: All 10's.
	 * 9: Purchase Mode
	 */
	private static int rollMethod = Constants.ROLLINGMETHOD_STANDARD;
	private static String rollMethodExpression = "roll(4,6,[2,3,4])"; // standard
	private static final String[] rollMethodExpressions = {
		"0", // all 0s
		"roll(4,6,[2,3,4])", // 4d6, drop lowest
		"3d6", // 3d6
		"roll(5,6,[3,4,5])", // 5d6, drop 2 lowest
		"roll(4,5,[2,3,4])+3", // 4d6, reroll 1s, drop lowest (actually 4d5 drop lowest +3)
		"roll(4,4,[2,3,4])+6", // 4d6, reroll 1s and 2s, drop lowest (actually 4d4 drop lowest +6)
		"3d6+5", // 3d6+5
		"roll(5,6,[2,4,5])", // 5d6, drop lowest and middle
		"10", // all 10s
		"",
	};
	private static boolean saveCustomInLst = false;

	private static File templatePath = new File(Globals.getDefaultPath() + File.separator + "outputsheets");

	private static String selectedEqSetTemplate = getTemplatePath().getAbsolutePath() + File.separator + "eqsheet.htm";
	private static String selectedPartyTemplate = getTemplatePath().getAbsolutePath() + File.separator + "psheet.htm";
	private static String selectedPDFOutputSheet = getTemplatePath().getAbsolutePath() + File.separator + "htmlxml" + File.separator + "csheetROG.fo";
	private static String selectedHTMLOutputSheet = getTemplatePath().getAbsolutePath() + File.separator + "pdf" + File.separator + "csheetROG.htm";
	private static String selectedTextOutputSheet = getTemplatePath().getAbsolutePath() + File.separator + "text" + File.separator + "csheet.txt";

	private static boolean showFeatDialogAtLevelUp = true;
	private static boolean showHPDialogAtLevelUp = true;
	private static boolean showStatDialogAtLevelUp = true;
	private static boolean showToolBar = true;
	private static boolean skillIncrementBefore = true;
	private static int skillsTab_AvailableListMode = GuiConstants.INFOSKILLS_VIEW_TYPE_NAME;
	private static int skillsTab_SelectedListMode = GuiConstants.INFOSKILLS_VIEW_NAME;
	private static String skinLFThemePack = null;
	private static int spellsTab_AvailableListMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
	private static int spellsTab_SelectedListMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
	private static boolean summaryTabShown = false;

	private static int cleanupTempFiles = 0;

	/**
	 *  See @javax.swing.SwingConstants
	 */
	private static int tabPlacement = SwingConstants.BOTTOM;

	private static final String tmpPath = System.getProperty("java.io.tmpdir");
	private static File tempPath = new File(getTmpPath());

	private static boolean toolTipTextShown = true;
	private static boolean treatInHandAsEquippedForAttacks = false;
	//private static boolean unlimitedStatPool = false;
	private static boolean useMonsterDefault = true;
	private static boolean wantToLoadMasterworkAndMagic = false;

	private static final String filterLocation = System.getProperty("pcgen.filter");
	private static final String optionsLocation = System.getProperty("pcgen.options");

	private static int nameDisplayStyle = Constants.DISPLAY_STYLE_NAME;

	private static int pcgSaveFormat = 2;

	private static boolean hideMonsterClasses = false;

	public static File getPcgenDocsDir()
	{
		return pcgenDocsDir;
	}

	public static void setPcgenDocsDir(File argPcgenDocsDir)
	{
		pcgenDocsDir = argPcgenDocsDir;
	}

	/**
	 * Opens the filter.ini file for reading
	 *
	 * <br>author: Thomas Behr 10-03-02
	 */
	private static void readFilterSettings()
	{
		FileInputStream in = null;
		try
		{
			if (filterLocation == null)
			{
				in = new FileInputStream(System.getProperty("user.dir") + File.separator + "filter.ini");
			}
			else
			{
				in = new FileInputStream(filterLocation);
			}
			getFilterSettings().load(in);
		}
		catch (IOException e)
		{
			//Not an error, it may well be that this file does not exist yet.
			Globals.debugPrint("No filter settings file found, will create one when exiting.");
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Opens the options.ini file and calls {@link #getOptionsFromProperties}.
	 *
	 * @return  the <code>Dimension</code> from <code>getOptionsFromProperties</code>
	 */
	public static Dimension readOptionsProperties()
	{
		readFilterSettings();

		FileInputStream in = null;
		Dimension d = new Dimension(0, 0);
		try
		{
			if (optionsLocation == null)
			{
				in = new FileInputStream(System.getProperty("user.dir") + File.separator + "options.ini");
			}
			else
			{
				in = new FileInputStream(optionsLocation);
			}
			getOptions().load(in);
		}
		catch (IOException e)
		{
			//Not an error, it may well be that this file does not exist yet.

			Globals.debugPrint("No options file found, will create one when exiting.");
		}
		finally
		{
			d = getOptionsFromProperties();
			try
			{
				if (in != null)
				{
					in.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				ex.printStackTrace();
			}
		}
		return d;
	}

	public static void readGUIOptionsProperties()
	{
		setNameDisplayStyle(getPCGenOption("nameDisplayStyle", Constants.DISPLAY_STYLE_NAME));

		// Calling setToolTipTextShown doesn't update menu checkbox state
		// toolTip state change, and menu checkbox state change are
		// handled in gui code pcGenGUI.java just after returning
		// from this method.
		if (((Globals.javaVersionMajor >= 1) && (Globals.javaVersionMinor >= 4)) ||
			(!System.getProperty("os.name").substring(0, 3).equalsIgnoreCase("MAC")))
		//(! System.getProperty("os.name").substring(1,3).equalsIgnoreCase("LIN")))
		{
			setToolTipTextShown(getPCGenOption("toolTipTextShown", isToolTipTextShown()));
			//System.out.println("Java Ver >= 1.4 || OS Name != MAC -- toolTip bug avoidance unnecessary");
		}
		else
		{
			setToolTipTextShown(getPCGenOption("toolTipTextShown", false));
			//System.out.println("Java Ver < 1.4 && OS Name = MAC -- Defaulting toolTips OFF -- MAC/Java 1.3 Bug");
		}
		// Menu stuff
		setOpenRecentPCs(getOpenRecentOption("openRecentPCs"));
		setOpenRecentParties(getOpenRecentOption("openRecentParties"));
	}

	/**
	 * Set most of this objects static properties from the loaded <code>options</code>.
	 * Called by readOptionsProperties. Most of the static properties are
	 * set as a side effect, with the main screen size being returned.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the <code>getProperty</code> method, and that this is
	 * why settings are stored as static properties of <code>Global</code>,
	 * but converted into a <code>Properties</code> object for
	 * storage and retrieval.
	 *
	 * @return the default <code>Dimension</code> to set the screen size to
	 */
	private static boolean getPCGenOption(final String optionName, final boolean defaultValue)
	{
		final String option = getPCGenOption(optionName, defaultValue ? "true" : "false");
		return "true".equalsIgnoreCase(option);
	}

	public static int getPCGenOption(final String optionName, final int defaultValue)
	{
		return Integer.decode(getPCGenOption(optionName, String.valueOf(defaultValue))).intValue();
	}

	private static Double getPCGenOption(final String optionName, final double defaultValue)
	{
		return new Double(getPCGenOption(optionName, Double.toString(defaultValue)));
	}

	public static String getPCGenOption(final String optionName, final String defaultValue)
	{
		return getOptions().getProperty("pcgen.options." + optionName, defaultValue);
	}

	private static String[] getOpenRecentOption(final String optionName)
	{
		final String value = getPCGenOption(optionName, "");
		if (value == null) return new String[0];
		final StringTokenizer tok = new StringTokenizer(value, "|");
		final ArrayList strings = new ArrayList();

		while (tok.hasMoreTokens())
		{
			strings.add(tok.nextToken());
		}

		return (String[]) strings.toArray(new String[0]);
	}

	public static Dimension getOptionsFromProperties()
	{
		Dimension d = new Dimension(0, 0);
		setPcgPath(new File(getOptions().getProperty("pcgen.files.characters", Globals.getDefaultPcgPath())));
		setTemplatePath(new File(getOptions().getProperty("pcgen.files.templates", Globals.getDefaultPath() + File.separator + "outputsheets")));
		setHtmlOutputPath(new File(getOptions().getProperty("pcgen.files.htmlOutput", Globals.getDefaultPcgPath())));
		final String tempBrowserPath = getPCGenOption("browserPath", "");
		if (!"".equals(tempBrowserPath))
		{
			setBrowserPath(new File(tempBrowserPath));
		}
		else
		{
			setBrowserPath(null);
		}
		setSelectedHTMLOutputSheet(getOptions().getProperty("pcgen.files.selectedHTMLOutputSheet", getTemplatePath().getAbsolutePath() + File.separator + "htmlxml" + File.separator + "csheetROG.htm"));
		setSelectedPDFOutputSheet(getOptions().getProperty("pcgen.files.selectedPDFOutputSheet", getTemplatePath().getAbsolutePath() + File.separator + "pdf" + File.separator + "csheetROG.fo"));
		setSelectedTextOutputSheet(getOptions().getProperty("pcgen.files.selectedTextOutputSheet", getTemplatePath().getAbsolutePath() + File.separator + "text" + File.separator + "csheet.txt"));
		setSelectedPartyTemplate(getOptions().getProperty("pcgen.files.selectedPartyTemplate", getTemplatePath().getAbsolutePath() + File.separator + "psheetROG.htm"));
		setSelectedEqSetTemplate(getOptions().getProperty("pcgen.files.selectedEqSetTemplate", getTemplatePath().getAbsolutePath() + File.separator + "eqsheet.htm"));
		PersistenceManager.setChosenCampaignSourcefiles(Utility.split(getOptions().getProperty("pcgen.files.chosenCampaignSourcefiles", ""), ','));
		setPccFilesLocation(new File(getPCGenOption("pccFilesLocation", System.getProperty("user.dir") + File.separator + "data")));

		setPcgenSystemDir(new File(getOptions().getProperty("pcgen.files.pcgenSystemDir", System.getProperty("user.dir") + File.separator + "system")));
		setPcgenThemePackDir(new File(getOptions().getProperty("pcgen.files.pcgenThemePackDir", System.getProperty("user.dir") + File.separator + "lib" + File.separator + "themes")));
		setPcgenDocsDir(new File(getOptions().getProperty("pcgen.files.pcgenDocsDir", System.getProperty("user.dir") + File.separator + "docs")));

		setDmNotes(getPCGenOption("dmnotes", ""));
		//setPurchaseStatMode(getPCGenOption("purchaseStatMode", false));
		purchaseMethodName = getPCGenOption("purchaseMethodName", "");
		setMonsterDefault(getPCGenOption("useMonsterDefault", false));
		setIgnoreLevelCap(getPCGenOption("ignoreLevelCap", false));
		setFreeClothesAtFirst(getPCGenOption("freeClothesAtFirst", true));

		setPcgSaveFormat(getPCGenOption("pcgSaveFormat", 2));

		//setUnlimitedStatPool(getPCGenOption("unlimitedStatPool", false));
		setIgnoreEquipmentCost(getPCGenOption("ignoreEquipmentCost", false));
		setGrimHPMode(getPCGenOption("grimHPMode", false));
		setGrittyACMode(getPCGenOption("grittyACMode", false));
		setUseExperimentalCursor(getPCGenOption("useExperimentalCursor", true));
		setIncludeSkills(getPCGenOption("includeSkills", 0));
		setExcSkillCost(getPCGenOption("excSkillCost", 0));
		setIntCrossClassSkillCost(getPCGenOption("intCrossClassSkillCost", 2));
		//setInitialStatMin(getPCGenOption("initialStatMin", 3));
		//setInitialStatMax(getPCGenOption("initialStatMax", 18));
		setLoadCampaignsAtStart(getPCGenOption("loadCampaignsAtStart", false));
		setLoadCampaignsWithPC(getPCGenOption("loadCampaignsWithPC", true));
		setOptionAllowedInSources(getPCGenOption("optionAllowedInSources", true));
		setSaveCustomInLst(getPCGenOption("saveCustomInLst", false));
		setTabPlacement(getOptionTabPlacement("tabPlacement", SwingConstants.BOTTOM));
		setChaTabPlacement(getOptionTabPlacement("chaTabPlacement", SwingConstants.TOP));
		setRollMethod(getPCGenOption("rollMethod", 0));
		setRollMethodExpression(getPCGenOption("rollMethodExpression", "roll(4,6,[2,3,4])"));
		setLookAndFeel(getPCGenOption("looknFeel", 0));
		setSkinLFThemePack(getPCGenOption("skinLFThemePack", ""));
		setBoolBypassFeatPreReqs(getPCGenOption("boolBypassFeatPreReqs", false));
		setBoolBypassClassPreReqs(getPCGenOption("boolBypassClassPreReqs", false));
		setBoolBypassMaxSkillRank(getPCGenOption("boolBypassMaxSkillRank", false));
		setHideMonsterClasses(getPCGenOption("hideMonsterClasses", false));

		setAutoFeatsRefundable(getPCGenOption("autoFeatsRefundable", false));
/////////////////////////////////////////////////
// Yanked for WotC compliance
//		for (int i = 0; i < abilityScoreCost.length; i++)
//		{
//			int statValue = i + getPurchaseScoreMin();
//			abilityScoreCost[i] = getPCGenOption("abilityScoreCost." + statValue, abilityScoreCost[i]);
//		}
//		setMaxStartingGold(getPCGenOption("maxStartingGold", false));
/////////////////////////////////////////////////
		setHPMaxAtFirstLevel(getPCGenOption("hpMaxAtFirstLevel", true));
		setHPRollMethod(getPCGenOption("hpRollMethod", Constants.s_HP_STANDARD));
		setSkillIncrementBefore(getPCGenOption("skillIncrementBefore", true));
		setPreviewTabShown(getPCGenOption("previewTabShown", true));
		setSummaryTabShown(getPCGenOption("summaryTabShown", true));
		setAbilitiesShownAsATab(getPCGenOption("abilitiesShownAsTab", false));
		setCleanupTempFiles(getPCGenOption("cleanupTempFiles", 0));

		setShowFeatDialogAtLevelUp(getPCGenOption("showFeatDialogAtLevelUp", true));
		setShowHPDialogAtLevelUp(getPCGenOption("showHPDialogAtLevelUp", true));
		setShowStatDialogAtLevelUp(getPCGenOption("showStatDialogAtLevelUp", true));

		setApplyWeightPenaltyToSkills(getPCGenOption("applyWeightPenaltyToSkills", true));
		setApplyLoadPenaltyToACandSkills(getPCGenOption("applyLoadPenaltyToACandSkills", true));

		setLeftUpperCorner(new Point(getPCGenOption("windowLeftUpperCorner.X", -1.0).intValue(), getPCGenOption("windowLeftUpperCorner.Y", -1.0).intValue()));
		Double dw = getPCGenOption("windowWidth", 0.0);
		Double dh = getPCGenOption("windowHeight", 0.0);
		if (dw.doubleValue() != 0.0 && dh.doubleValue() != 0.0)
		{
			final int width = Integer.parseInt(dw.toString().substring(0, Math.min(dw.toString().length(), dw.toString().lastIndexOf("."))));
			final int height = Integer.parseInt(dh.toString().substring(0, Math.min(dh.toString().length(), dh.toString().lastIndexOf("."))));
			d = new Dimension(width, height);
		}

		setCustomizerLeftUpperCorner(new Point(getPCGenOption("customizer.windowLeftUpperCorner.X", -1.0).intValue(), getPCGenOption("customizer.windowLeftUpperCorner.Y", -1.0).intValue()));
		dw = getPCGenOption("customizer.windowWidth", 0.0);
		dh = getPCGenOption("customizer.windowHeight", 0.0);
		if (dw.doubleValue() != 0.0 && dh.doubleValue() != 0.0)
		{
			setCustomizerDimension(new Dimension(dw.intValue(), dh.intValue()));
		}
		setCustomizerSplit1(getPCGenOption("customizer.split1", -1));
		setCustomizerSplit2(getPCGenOption("customizer.split2", -1));

		setPrereqFailColor(getPCGenOption("prereqFailColor", Color.red.getRGB()));
		setFeatAutoColor(getPCGenOption("featAutoColor", Color.yellow.darker().getRGB()));
		setFeatVirtualColor(getPCGenOption("featVirtualColor", Color.magenta.getRGB()));

		setAutogenMasterwork(getPCGenOption("autoGenerateMasterwork", false));
		setAutogenMagic(getPCGenOption("autoGenerateMagic", false));
		setAutogenRacial(getPCGenOption("autoGenerateRacial", false));
		setAutogenExoticMaterial(getPCGenOption("autoGenerateExoticMaterial", false));

		setMaxPotionSpellLevel(getPCGenOption("maxPotionSpellLevel", 3));
		setMaxWandSpellLevel(getPCGenOption("maxWandSpellLevel", 4));
		setMetamagicAllowedInEqBuilder(getPCGenOption("allowMetamagicInCustomizer", false));
		setSpellMarketPriceAdjusted(getPCGenOption("spellMarketPriceAdjusted", false));

		setROG(getPCGenOption("isROG", false));
		setWantToLoadMasterworkAndMagic(getPCGenOption("loadMasterworkAndMagicFromLst", false));

		setEquipTab_AvailableListMode(getPCGenOption("EquipTab.availableListMode", GuiConstants.INFOEQUIPPING_VIEW_TYPE));
		setEquipTab_SelectedListMode(getPCGenOption("EquipTab.selectedListMode", GuiConstants.INFOEQUIPPING_VIEW_NAME));
		setFeatTab_AvailableListMode(getPCGenOption("FeatTab.availableListMode", GuiConstants.INFOFEATS_VIEW_TYPENAME));
		setFeatTab_SelectedListMode(getPCGenOption("FeatTab.selectedListMode", GuiConstants.INFOFEATS_VIEW_NAMEONLY));

		//
		// Read in the buy/sell percentages for the inventory tab. If they are not in the .ini file
		// and ignoreCost is set, then use 0%. Otherwise set buy percentage to 100 and sell to %50
		//
		int buyRate = getPCGenOption("InventoryTab.buyRate", -1);
		int sellRate = getPCGenOption("InventoryTab.sellRate", -1);
		if ((buyRate < 0) || (sellRate < 0))
		{
			if (getPCGenOption("InventoryTab.ignoreCost", false))
			{
				buyRate = 0;
				sellRate = 0;
			}
			else
			{
				buyRate = 100;
				sellRate = 50;
			}
		}
		setInventoryTab_BuyRate(buyRate);
		setInventoryTab_SellRate(sellRate);
		//setInventoryTab_IgnoreCost(getPCGenOption("InventoryTab.ignoreCost", false));

		setInventoryTab_AvailableListMode(getPCGenOption("InventoryTab.availableListMode", GuiConstants.INFOINVENTORY_VIEW_TYPE_SUBTYPE_NAME));
		setInventoryTab_SelectedListMode(getPCGenOption("InventoryTab.selectedListMode", GuiConstants.INFOINVENTORY_VIEW_NAME));
		setRaceTab_ListMode(getPCGenOption("RaceTab.ListMode", GuiConstants.INFORACE_VIEW_NAME));
		setClassTab_AvailableListMode(getPCGenOption("ClassTab.availableListMode", GuiConstants.INFOCLASS_VIEW_NAME));
		setClassTab_SelectedListMode(getPCGenOption("ClassTab.selectedListMode", GuiConstants.INFOCLASS_VIEW_NAME));
		setSkillsTab_AvailableListMode(getPCGenOption("SkillsTab.availableListMode", GuiConstants.INFOSKILLS_VIEW_TYPE_NAME));
		setSkillsTab_SelectedListMode(getPCGenOption("SkillsTab.selectedListMode", GuiConstants.INFOSKILLS_VIEW_NAME));
		setSpellsTab_AvailableListMode(getPCGenOption("SpellsTab.availableListMode", GuiConstants.INFOSPELLS_VIEW_CLASS));
		setSpellsTab_SelectedListMode(getPCGenOption("SpellsTab.selectedListMode", GuiConstants.INFOSPELLS_VIEW_CLASS));

		final String userDir = getPCGenOption("userdir", "");
		if (userDir != null && userDir.length() > 0)
		{
			System.setProperty("user.dir", userDir);
		}

		setAllStatsValue(getPCGenOption("allStatsValue", 10));

		setTreatInHandAsEquippedForAttacks(getPCGenOption("treatInHandAsEquippedForAttacks", false));
		setShowToolBar(getPCGenOption("showToolBar", true));
		Globals.initCustColumnWidth(Utility.split(getOptions().getProperty("pcgen.options.custColumnWidth", ""), ','));

		setRanStartingWizard(getPCGenOption("ranStartingWizard", false));
		showD20InfoAtStart = getPCGenOption("showD20InfoAtStart", true);
		showOGLOnLoad = getPCGenOption("showOGLOnLoad", true);

		Globals.setSourceDisplay(getPCGenOption("sourceDisplay", Constants.SOURCELONG));
		Globals.setLanguage(getPCGenOption("language", "en"));
		Globals.setCountry(getPCGenOption("country", "US"));

		try
		{
			PersistenceManager.initialize();
		}
		catch (PersistenceLayerException e)
		{
			GuiFacade.showMessageDialog(null, e.getMessage(), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
		}
		setGame(getPCGenOption("game", Constants.DND_MODE));

		try
		{
			Globals.loadAttributeNames();
		}
		catch (PersistenceLayerException e)
		{
			Globals.errorPrint("Cannot load attribute names", e);
		}

		return d;
	}

	/**
	 * Opens the filter.ini file for writing
	 *
	 * <br>author: Thomas Behr 10-03-02
	 */
	private static void writeFilterSettings()
	{
		final String header = "# Emacs, this is -*- java-properties-generic -*- mode." + Constants.s_LINE_SEP +
			Constants.s_LINE_SEP +
			"# filter.ini -- filters set in pcgen" + Constants.s_LINE_SEP +
			"# Do not edit this file manually." + Constants.s_LINE_SEP;

		FileOutputStream out = null;
		try
		{
			if (filterLocation == null)
			{
				out = new FileOutputStream(System.getProperty("user.dir") + File.separator + "filter.ini");
			}
			else
			{
				out = new FileOutputStream(filterLocation);
			}
			getFilterSettings().store(out, header);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...
				ex.printStackTrace();
			}
		}

		// remove old filter stuff!
		for (Iterator it = getOptions().keySet().iterator(); it.hasNext();)
		{
			if (((String) it.next()).startsWith("pcgen.filters."))
			{
				it.remove();
			}
		}
	}

	/**
	 * Opens (options.ini) for writing and calls {@link #setOptionsProperties}.
	 */
	public static void writeOptionsProperties()
	{
		writeFilterSettings();

		final String header = "# Emacs, this is -*- java-properties-generic -*- mode." + Constants.s_LINE_SEP + "#" + Constants.s_LINE_SEP + "# options.ini -- options set in pcgen" + Constants.s_LINE_SEP + "# Do not edit this file manually." + Constants.s_LINE_SEP;

		FileOutputStream out = null;
		try
		{
			setOptionsProperties();
			if (optionsLocation == null)
			{
				out = new FileOutputStream(System.getProperty("user.dir") + File.separator + "options.ini");
			}
			else
			{
				out = new FileOutputStream(optionsLocation);
			}
			getOptions().store(out, header);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (out != null)
				{
					out.close();
				}
			}
			catch (IOException ex)
			{
				//Not much to do about it...

				ex.printStackTrace();
			}
		}
	}

	/**
	 * store the filter settings for a given Filterable
	 *
	 * <br>author: Thomas Behr 19-02-02
	 *
	 * @param filterable - the Filterable whose settings
	 *              will be stored
	 */
	public static void storeFilterSettings(final Filterable filterable)
	{
		final String name = filterable.getName();

		if (name == null)
		{
			return;
		}

		getFilterSettings().setProperty("pcgen.filters." + name + ".mode", Integer.toString(filterable.getFilterMode()));

		getFilterSettings().setProperty("pcgen.filters." + name + ".available",
			FilterFactory.filterListToString(filterable.getAvailableFilters()));

		getFilterSettings().setProperty("pcgen.filters." + name + ".selected",
			FilterFactory.filterListToString(filterable.getSelectedFilters()));

		getFilterSettings().setProperty("pcgen.filters." + name + ".removed",

			FilterFactory.filterListToString(filterable.getRemovedFilters()));
	}

	/**
	 * retrieve filter settings
	 *
	 * <br>author: Thomas Behr 19-02-02
	 *
	 * @param optionName   the name of the property to retrieve
	 */
	public static String retrieveFilterSettings(final String optionName)
	{
		return getFilterSettings().getProperty("pcgen.filters." + optionName,
			getOptions().getProperty("pcgen.filters." + optionName, ""));
	}

	/**
	 * Puts all properties into the <code>Properties</code> object,
	 * (<code>options</code>). This is called by
	 * <code>writeOptionsProperties</code>, which then saves the
	 * <code>options</code> into a file.
	 * <p>
	 * I am guessing that named object properties are faster to access
	 * than using the <code>getProperty</code> method, and that this is
	 * why settings are stored as static properties of <code>Global</code>,
	 * but converted into a <code>Properties</code> object for
	 * storage and retrieval.
	 */
	private static void setPCGenOption(final String optionName, final boolean optionValue)
	{
		setPCGenOption(optionName, optionValue ? "true" : "false");
	}

	public static void setPCGenOption(final String optionName, final int optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	private static void setPCGenOption(final String optionName, final double optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setPCGenOption(final String optionName, final String optionValue)
	{
		getOptions().setProperty("pcgen.options." + optionName, optionValue);
	}

	private static void setOpenRecentOption(final String optionName, final String[] strings)
	{
		String value = "";
		if (strings.length > 0)
		{
			value += strings[0];

			for (int i = 1; i < strings.length; ++i)
			{
				value += "|" + strings[i];
			}
		}

		setPCGenOption(optionName, value);
	}

	public static void setOptionsProperties()
	{
		if (getPcgPath() != null)
		{
			getOptions().setProperty("pcgen.files.characters", getPcgPath().getAbsolutePath());
		}
		else
		{
			// hasn't been set properly yet
			getOptions().setProperty("pcgen.files.characters", Globals.getDefaultPath());
		}
		getOptions().setProperty("pcgen.files.templates", getTemplatePath().getAbsolutePath());
		getOptions().setProperty("pcgen.files.htmlOutput", getHtmlOutputPath().getAbsolutePath());
		getOptions().setProperty("pcgen.files.selectedHTMLOutputSheet", getSelectedHTMLOutputSheet());
		getOptions().setProperty("pcgen.files.selectedPDFOutputSheet", getSelectedPDFOutputSheet());
		getOptions().setProperty("pcgen.files.selectedTextOutputSheet", getSelectedTextOutputSheet());
		getOptions().setProperty("pcgen.files.selectedPartyTemplate", getSelectedPartyTemplate());
		getOptions().setProperty("pcgen.files.selectedEqSetTemplate", getSelectedEqSetTemplate());
		getOptions().setProperty("pcgen.files.chosenCampaignSourcefiles", Utility.unSplit(PersistenceManager.getChosenCampaignSourcefiles(), ','));
		getOptions().setProperty("pcgen.files.pcgenDocsDir", getPcgenDocsDir().getAbsolutePath());
		getOptions().setProperty("pcgen.files.pcgenSystemDir", getPcgenSystemDir().getAbsolutePath());
		getOptions().setProperty("pcgen.files.pcgenThemePackDir", getPcgenThemePackDir().getAbsolutePath());

		if (getBrowserPath() != null)
		{
			setPCGenOption("browserPath", getBrowserPath().getAbsolutePath());
		}
		else
		{
			setPCGenOption("browserPath", "");
		}

		if (Globals.getRootFrame() != null)
		{
			setOpenRecentOption("openRecentPCs", Globals.getRootFrame().getOpenRecentPCs());
			setOpenRecentOption("openRecentParties", Globals.getRootFrame().getOpenRecentParties());
		}

		//setPCGenOption("purchaseStatMode", isPurchaseStatMode());
		setPCGenOption("purchaseMethodName", purchaseMethodName);
		setPCGenOption("useMonsterDefault", isUseMonsterDefault());
		setPCGenOption("ignoreLevelCap", isIgnoreLevelCap());
		setPCGenOption("freeClothesAtFirst", isFreeClothesAtFirst());

		setPCGenOption("game", getGame().getName());
		setPCGenOption("pcgSaveFormat", getPcgSaveFormat());


		//setPCGenOption("unlimitedStatPool", isUnlimitedStatPool());
		setPCGenOption("ignoreEquipmentCost", isIgnoreEquipmentCost());
		setPCGenOption("grimHPMode", isGrimHPMode());
		setPCGenOption("grittyACMode", isGrittyACMode());
		setPCGenOption("useExperimentalCursor", getUseExperimentalCursor());
		setPCGenOption("includeSkills", getIncludeSkills());
		setPCGenOption("excSkillCost", getExcSkillCost());
		setPCGenOption("intCrossClassSkillCost", getIntCrossClassSkillCost());
		//setPCGenOption("initialStatMin", getInitialStatMin());
		//setPCGenOption("initialStatMax", getInitialStatMax());
		setPCGenOption("loadCampaignsAtStart", isLoadCampaignsAtStart());
		setPCGenOption("loadCampaignsWithPC", isLoadCampaignsWithPC());
		setPCGenOption("optionAllowedInSources", isOptionAllowedInSources());
		setPCGenOption("saveCustomInLst", isSaveCustomInLst());
		setPCGenOption("chaTabPlacement", convertTabPlacementToString(chaTabPlacement));
		setPCGenOption("tabPlacement", convertTabPlacementToString(tabPlacement));
		setPCGenOption("nameDisplayStyle", getNameDisplayStyle());
		setPCGenOption("looknFeel", getLookAndFeel());
		setPCGenOption("skinLFThemePack", getSkinLFThemePack());
		setPCGenOption("autoFeatsRefundable", isAutoFeatsRefundable());
		setPCGenOption("pccFilesLocation", getPccFilesLocation().getAbsolutePath());
		setPCGenOption("dmnotes", getDmNotes());
		setPCGenOption("boolBypassFeatPreReqs", isBoolBypassFeatPreReqs());
		setPCGenOption("boolBypassClassPreReqs", isBoolBypassClassPreReqs());
		setPCGenOption("boolBypassMaxSkillRank", isBoolBypassMaxSkillRank());
		setPCGenOption("hideMonsterClasses", hideMonsterClasses());

		setPCGenOption("rollMethod", getRollMethod());
		setPCGenOption("rollMethodExpression", getRollMethodExpression());
/////////////////////////////////////////////////
// Yanked for WotC compliance
//		for (int i = 0; i < abilityScoreCost.length; i++)
//		{
//			int statValue = i + getPurchaseScoreMin();
//			setPCGenOption("abilityScoreCost." + statValue, abilityScoreCost[i]);
//		}
//		setPCGenOption("maxStartingGold", isMaxStartingGold());
/////////////////////////////////////////////////
		setPCGenOption("hpMaxAtFirstLevel", isHPMaxAtFirstLevel());
		setPCGenOption("hpRollMethod", getHPRollMethod());
		setPCGenOption("skillIncrementBefore", isSkillIncrementBefore());
		setPCGenOption("prereqFailColor", "0x" + Integer.toHexString(getPrereqFailColor()));
		setPCGenOption("featAutoColor", "0x" + Integer.toHexString(getFeatAutoColor()));
		setPCGenOption("featVirtualColor", "0x" + Integer.toHexString(getFeatVirtualColor()));

		setPCGenOption("autoGenerateMasterwork", isAutogenMasterwork());
		setPCGenOption("autoGenerateMagic", isAutogenMagic());
		setPCGenOption("autoGenerateRacial", isAutogenRacial());
		setPCGenOption("autoGenerateExoticMaterial", isAutogenExoticMaterial());

		setPCGenOption("maxPotionSpellLevel", getMaxPotionSpellLevel());
		setPCGenOption("maxWandSpellLevel", getMaxWandSpellLevel());
		setPCGenOption("allowMetamagicInCustomizer", isMetamagicAllowedInEqBuilder());
		setPCGenOption("spellMarketPriceAdjusted", isSpellMarketPriceAdjusted());

		setPCGenOption("loadMasterworkAndMagicFromLst", isWantToLoadMasterworkAndMagic());

		setPCGenOption("toolTipTextShown", isToolTipTextShown());
		setPCGenOption("previewTabShown", isPreviewTabShown());
		setPCGenOption("summaryTabShown", isSummaryTabShown());
		setPCGenOption("abilitiesShownAsTab", isAbilitiesShownAsATab());
		setPCGenOption("cleanupTempFiles", getCleanupTempFiles());

		setPCGenOption("showFeatDialogAtLevelUp", getShowFeatDialogAtLevelUp());
		setPCGenOption("showHPDialogAtLevelUp", getShowHPDialogAtLevelUp());
		setPCGenOption("showStatDialogAtLevelUp", getShowStatDialogAtLevelUp());

		setPCGenOption("applyWeightPenaltyToSkills", isApplyWeightPenaltyToSkills());
		setPCGenOption("applyLoadPenaltyToACandSkills", isApplyLoadPenaltyToACandSkills());

		if (getLeftUpperCorner() != null)
		{
			setPCGenOption("windowLeftUpperCorner.X", getLeftUpperCorner().getX());
			setPCGenOption("windowLeftUpperCorner.Y", getLeftUpperCorner().getY());
		}
		if (Globals.getRootFrame() != null)
		{
			setPCGenOption("windowWidth", Globals.getRootFrame().getSize().getWidth());
			setPCGenOption("windowHeight", Globals.getRootFrame().getSize().getHeight());
		}

		if (getCustomizerLeftUpperCorner() != null)
		{
			setPCGenOption("customizer.windowLeftUpperCorner.X", getCustomizerLeftUpperCorner().getX());
			setPCGenOption("customizer.windowLeftUpperCorner.Y", getCustomizerLeftUpperCorner().getY());
		}
		if (getCustomizerDimension() != null)
		{
			setPCGenOption("customizer.windowWidth", getCustomizerDimension().getWidth());
			setPCGenOption("customizer.windowHeight", getCustomizerDimension().getHeight());
		}
		setPCGenOption("customizer.split1", getCustomizerSplit1());
		setPCGenOption("customizer.split2", getCustomizerSplit2());

		setPCGenOption("userdir", System.getProperty("user.dir"));
		setPCGenOption("allStatsValue", getAllStatsValue());

		final String paperName = Globals.getPaperInfo(Constants.PAPERINFO_NAME);
		if (paperName != null)
		{
			setPCGenOption("paperName", paperName);
		}
		setPCGenOption("treatInHandAsEquippedForAttacks", isTreatInHandAsEquippedForAttacks());
		setPCGenOption("showToolBar", isShowToolBar());

		setPCGenOption("EquipTab.availableListMode", getEquipTab_AvailableListMode());
		setPCGenOption("EquipTab.selectedListMode", getEquipTab_SelectedListMode());
		setPCGenOption("FeatTab.availableListMode", getFeatTab_AvailableListMode());
		setPCGenOption("FeatTab.selectedListMode", getFeatTab_SelectedListMode());
		//setPCGenOption("InventoryTab.ignoreCost", getInventoryTab_IgnoreCost());
		setPCGenOption("InventoryTab.buyRate", getInventoryTab_BuyRate());
		setPCGenOption("InventoryTab.sellRate", getInventoryTab_SellRate());
		setPCGenOption("InventoryTab.availableListMode", getInventoryTab_AvailableListMode());
		setPCGenOption("InventoryTab.selectedListMode", getInventoryTab_SelectedListMode());
		setPCGenOption("RaceTab.ListMode", getRaceTab_ListMode());
		setPCGenOption("ClassTab.availableListMode", getClassTab_AvailableListMode());
		setPCGenOption("ClassTab.selectedListMode", getClassTab_SelectedListMode());
		setPCGenOption("SkillsTab.availableListMode", getSkillsTab_AvailableListMode());
		setPCGenOption("SkillsTab.selectedListMode", getSkillsTab_SelectedListMode());
		setPCGenOption("SpellsTab.availableListMode", getSpellsTab_AvailableListMode());
		setPCGenOption("SpellsTab.selectedListMode", getSpellsTab_SelectedListMode());
		getOptions().setProperty("pcgen.options.custColumnWidth", Utility.unSplit(Globals.getCustColumnWidth(), ','));
		setPCGenOption("ranStartingWizard", ranStartingWizard);
		setPCGenOption("showOGLOnLoad", showOGLOnLoad);
		setPCGenOption("showD20InfoAtStart", showD20InfoAtStart);
		setPCGenOption("sourceDisplay", Globals.getSourceDisplay());
		setPCGenOption("language", Globals.getLanguage());
		setPCGenOption("country", Globals.getCountry());
	}

	private static Properties getFilterSettings()
	{
		return filterSettings;
	}

	public static int getInventoryTab_AvailableListMode()
	{
		return inventoryTab_AvailableListMode;
	}

	public static void setInventoryTab_AvailableListMode(final int listMode)
	{
		inventoryTab_AvailableListMode = listMode;
	}

	public static int getInventoryTab_SelectedListMode()
	{
		return inventoryTab_SelectedListMode;
	}

	public static void setInventoryTab_SelectedListMode(final int listMode)
	{
		inventoryTab_SelectedListMode = listMode;
	}

	public static int getRaceTab_ListMode()
	{
		return raceTab_ListMode;
	}

	public static void setRaceTab_ListMode(final int listMode)
	{
		raceTab_ListMode = listMode;
	}

	public static int getClassTab_AvailableListMode()
	{
		return classTab_AvailableListMode;
	}

	public static void setClassTab_AvailableListMode(final int listMode)
	{
		classTab_AvailableListMode = listMode;
	}

	public static int getClassTab_SelectedListMode()
	{
		return classTab_SelectedListMode;
	}

	public static void setClassTab_SelectedListMode(final int listMode)
	{
		classTab_SelectedListMode = listMode;
	}

	public static int getSkillsTab_AvailableListMode()
	{
		return skillsTab_AvailableListMode;
	}

	public static void setSkillsTab_AvailableListMode(final int listMode)
	{
		skillsTab_AvailableListMode = listMode;
	}

	public static int getSkillsTab_SelectedListMode()
	{
		return skillsTab_SelectedListMode;
	}

	public static void setSkillsTab_SelectedListMode(final int listMode)
	{
		skillsTab_SelectedListMode = listMode;
	}

	public static int getFeatTab_AvailableListMode()
	{
		return featTab_AvailableListMode;
	}

	public static void setFeatTab_AvailableListMode(final int listMode)
	{
		featTab_AvailableListMode = listMode;
	}

	public static int getFeatTab_SelectedListMode()
	{
		return featTab_SelectedListMode;
	}

	public static void setFeatTab_SelectedListMode(final int listMode)
	{
		featTab_SelectedListMode = listMode;
	}

	public static int getSpellsTab_AvailableListMode()
	{
		return spellsTab_AvailableListMode;
	}

	public static void setSpellsTab_AvailableListMode(final int listMode)
	{
		spellsTab_AvailableListMode = listMode;
	}

	public static int getSpellsTab_SelectedListMode()
	{
		return spellsTab_SelectedListMode;
	}

	public static void setSpellsTab_SelectedListMode(final int listMode)
	{
		spellsTab_SelectedListMode = listMode;
	}

	public static int getEquipTab_AvailableListMode()
	{
		return equipTab_AvailableListMode;
	}

	public static void setEquipTab_AvailableListMode(final int listMode)
	{
		equipTab_AvailableListMode = listMode;
	}

	public static int getEquipTab_SelectedListMode()
	{
		return equipTab_SelectedListMode;
	}

	public static void setEquipTab_SelectedListMode(final int listMode)
	{
		equipTab_SelectedListMode = listMode;
	}

	private static boolean isGrimHPMode()
	{
		return grimHPMode;
	}

	private static void setGrimHPMode(final boolean argGrimHPMode)
	{
		grimHPMode = argGrimHPMode;
	}

	public static boolean getUseExperimentalCursor()
	{
		return useExperimentalCursor;
	}

	public static void setUseExperimentalCursor(final boolean b)
	{
		Globals.getRootFrame().useExperimentalCursor(useExperimentalCursor = b);
	}

	/**
	 * Returns the path to the character sheet templates.
	 *
	 * @return    the <code>templatePath</code> property
	 */
	public static File getTemplatePath()
	{
		return templatePath;
	}

	/**
	 * Returns the path to the HTML output sheets.
	 *
	 * @return    the html subdirectory of the <code>templatePath</code> property
	 */
	public static File getHTMLOutputSheetPath()
	{
		return new File(templatePath.getAbsolutePath() + File.separator + "htmlxml");
	}

	/**
	 * Returns the path to the PDF output sheets.
	 *
	 * @return    the pdf subdirectory of the <code>templatePath</code> property
	 */
	public static File getPDFOutputSheetPath()
	{
		return new File(templatePath.getAbsolutePath() + File.separator + "pdf");
	}

	/**
	 * Returns the path to the text output sheets.
	 *
	 * @return    the text subdirectory of the <code>templatePath</code> property
	 */
	public static File getTextOutputSheetPath()
	{
		return new File(templatePath.getAbsolutePath() + File.separator + "text");
	}

	/**
	 * Sets the path to the character sheet templates.
	 *
	 * @param  path  the <code>File</code> representing the path
	 * @since
	 */
	public static void setTemplatePath(final File path)
	{
		templatePath = path;
	}

	/**
	 * Returns the path to the character files.
	 *
	 * @return    the <code>pcgPath</code> property
	 */
	public static File getPcgPath()
	{
		return pcgPath;
	}

	/**
	 * Sets the path to the character files.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setPcgPath(final File path)
	{
		pcgPath = path;
	}

	/**
	 * Returns the path the html files should be saved to.
	 *
	 * @return    the <code>htmlOutputPath</code> property
	 */
	public static File getHtmlOutputPath()
	{
		return htmlOutputPath;
	}

	/**
	 * Sets the path the html files should be saved to.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setHtmlOutputPath(final File path)
	{
		htmlOutputPath = path;
	}

	/**
	 * Returns the path to the temporary output location (for previews).
	 *
	 * @return    the <code>tempPath</code> property
	 */
	public static File getTempPath()
	{
		return tempPath;
	}

	/**
	 * Returns the external browser path to use.
	 *
	 * @return    the <code>browserPath</code> property
	 */
	public static File getBrowserPath()
	{
		return browserPath;
	}

	/**
	 * Sets the external browser path to use.
	 *
	 * @param  path  the <code>File</code> representing the path
	 */
	public static void setBrowserPath(final File path)
	{
		browserPath = path;
	}

	/**
	 * Returns the current HTML output sheet.
	 *
	 * @return    the <code>selectedPDFOutputSheet</code> property
	 */
	public static String getSelectedHTMLOutputSheet()
	{
		return selectedHTMLOutputSheet;
	}

	/**
	 * Returns the current PDF output sheet.
	 *
	 * @return    the <code>selectedPDFOutputSheet</code> property
	 */
	public static String getSelectedPDFOutputSheet()
	{
		return selectedPDFOutputSheet;
	}

	/**
	 * Returns the current Text output sheet.
	 *
	 * @return    the <code>selectedtextOutputSheet</code> property
	 */
	public static String getSelectedTextOutputSheet()
	{
		return selectedTextOutputSheet;
	}

	/**
	 * Returns the current party template.
	 *
	 * @return    the <code>selectedPartyTemplate</code> property
	 */
	public static String getSelectedPartyTemplate()
	{
		return selectedPartyTemplate;
	}

	/**
	 * Returns the current EquipSet template.
	 *
	 * @return    the <code>selectedEqSetTemplate</code> property
	 */
	public static String getSelectedEqSetTemplate()
	{
		return selectedEqSetTemplate;
	}

	/**
	 * Sets the current template.
	 *
	 * @param  path  a string containing the path to the template
	 * @deprecated Use setSelectedHTMLOutputSheet instead  - 4Nov2002 jdempsey
	 */
	public static void setSelectedTemplate(final String path)
	{
		selectedHTMLOutputSheet = path;
	}

	/**
	 * Sets the current HTML output sheet.
	 *
	 * @param  path  a string containing the path to the HTML output sheet
	 */
	public static void setSelectedHTMLOutputSheet(final String path)
	{
		selectedHTMLOutputSheet = path;
	}

	/**
	 * Sets the current PDF output sheet.
	 *
	 * @param  path  a string containing the path to the PDF output sheet
	 */
	public static void setSelectedPDFOutputSheet(final String path)
	{
		selectedPDFOutputSheet = path;
	}

	/**
	 * Sets the current text output sheet.
	 *
	 * @param  path  a string containing the path to the text output sheet
	 */
	public static void setSelectedTextOutputSheet(final String path)
	{
		selectedTextOutputSheet = path;
	}

	/**
	 * Sets the current party template.
	 *
	 * @param  path  a string containing the path to the template
	 */
	public static void setSelectedPartyTemplate(final String path)
	{
		selectedPartyTemplate = path;
	}

	/**
	 * Sets the current EquipSet template.
	 *
	 * @param  path  a string containing the path to the template
	 */
	public static void setSelectedEqSetTemplate(final String path)
	{
		selectedEqSetTemplate = path;
	}

	private static String getTmpPath()
	{
		return tmpPath;
	}

	public static Properties getOptions()
	{
		return options;
	}

	private static boolean isTreatInHandAsEquippedForAttacks()
	{
		return treatInHandAsEquippedForAttacks;
	}

	private static boolean isShowToolBar()
	{
		return showToolBar;
	}

	private static void setShowToolBar(final boolean argShowToolBar)
	{
		showToolBar = argShowToolBar;
	}

	private static void setROG(final boolean ROG)
	{
		isROG = ROG;
	}

	private static void setPcgSaveFormat(final int argPcgSaveFormat)
	{
		pcgSaveFormat = argPcgSaveFormat;
	}

	public static int getPcgSaveFormat()
	{
		return pcgSaveFormat;
	}

	/**
	 * Where to load the data (lst) files from
	 */
	public static File getPccFilesLocation()
	{
		return pccFilesLocation;
	}

	/**
	 * Where to load the data (lst) files from
	 */
	public static void setPccFilesLocation(final File argPccFilesLocation)
	{
		pccFilesLocation = argPccFilesLocation;
	}

	public static boolean getTreatInHandAsEquippedForAttacks()
	{
		return isTreatInHandAsEquippedForAttacks();
	}

	public static void setTreatInHandAsEquippedForAttacks(final boolean treatAsEquipped)
	{
		treatInHandAsEquippedForAttacks = treatAsEquipped;
	}

	public static boolean isToolBarShown()
	{
		return isShowToolBar();
	}

	public static void setToolBarShown(final boolean showToolBar)
	{
		setShowToolBar(showToolBar);
	}

	/**
	 * Returns whether the feats dialog should be shown at level up.
	 * @return true if the feats dialog should be shown at level up.
	 */
	public static boolean getShowFeatDialogAtLevelUp()
	{
		return showFeatDialogAtLevelUp;
	}

	/**
	 * Sets whether the feats dialog should be shown at level up.
	 * @param argShowFeatDialogAtLevelUp Should the feats dialog be shown at level up?
	 */
	public static void setShowFeatDialogAtLevelUp(final boolean argShowFeatDialogAtLevelUp)
	{
		showFeatDialogAtLevelUp = argShowFeatDialogAtLevelUp;
	}

	/**
	 * Returns whether the hit point dialog should be shown at level up.
	 * @return true if the hit point dialog should be shown at level up.
	 */
	public static boolean getShowHPDialogAtLevelUp()
	{
		return showHPDialogAtLevelUp;
	}

	/**
	 * Sets whether the hit point dialog should be shown at level up.
	 * @param argShowHPDialogAtLevelUp Should the hit point dialog be shown at level up?
	 */
	public static void setShowHPDialogAtLevelUp(final boolean argShowHPDialogAtLevelUp)
	{
		showHPDialogAtLevelUp = argShowHPDialogAtLevelUp;
	}

	/**
	 * Returns whether the Stat dialog should be shown at level up.
	 * @return true if the Stat dialog should be shown at level up.
	 */
	public static boolean getShowStatDialogAtLevelUp()
	{
		return showStatDialogAtLevelUp;
	}

	/**
	 * Sets whether the Stat dialog should be shown at level up.
	 * @param argShowStatDialogAtLevelUp Should the Stat dialog should be shown at level up?
	 */
	public static void setShowStatDialogAtLevelUp(final boolean argShowStatDialogAtLevelUp)
	{
		showStatDialogAtLevelUp = argShowStatDialogAtLevelUp;
	}

	/** Returns whether 'automatic' class-granted feats can be turned in for other feats
	 *  @return true if 'automatic' class-granted feats can be turned in for other feats
	 */
	private static boolean isAutoFeatsRefundable()
	{
		return autoFeatsRefundable;
	}

	/** Sets whether 'automatic' class-granted feats can be turned in for other feats
	 */
	private static void setAutoFeatsRefundable(final boolean argAutoFeatsRefundable)
	{
		autoFeatsRefundable = argAutoFeatsRefundable;
	}

	public static int getAllStatsValue()
	{
		return allStatsValue;
	}

	public static void setAllStatsValue(final int argAllStatsValue)
	{
		allStatsValue = argAllStatsValue;
	}

	/**
	 * I guess only ROG can document this?
	 * @return
	 */
	public static boolean isROG()
	{
		return isROG;
	}

	public static File getPcgenSystemDir()
	{
		return pcgenSystemDir;
	}

	public static void setPcgenSystemDir(final File argPcgenSystemDir)
	{
		pcgenSystemDir = argPcgenSystemDir;
	}

	public static File getPcgenThemePackDir()
	{
		return pcgenThemePackDir;
	}

	private static void setPcgenThemePackDir(final File argPcgenThemePackDir)
	{
		pcgenThemePackDir = argPcgenThemePackDir;
	}

	public static String getDmNotes()
	{
		return dmNotes;
	}

	public static void setDmNotes(final String argDmNotes)
	{
		dmNotes = argDmNotes;
	}

	public static Point getLeftUpperCorner()
	{
		return leftUpperCorner;
	}

	public static void setLeftUpperCorner(final Point argLeftUpperCorner)
	{
		leftUpperCorner = argLeftUpperCorner;
	}

	public static void setCustomizerLeftUpperCorner(final Point argLeftUpperCorner)
	{
		customizerLeftUpperCorner = argLeftUpperCorner;
	}

	public static Point getCustomizerLeftUpperCorner()
	{
		return customizerLeftUpperCorner;
	}

	public static void setCustomizerDimension(final Dimension d)
	{
		customizerDimension = d;
	}

	public static Dimension getCustomizerDimension()
	{
		return customizerDimension;
	}

	public static void setCustomizerSplit1(final int split)
	{
		customizerSplit1 = split;
	}

	public static void setCustomizerSplit2(final int split)
	{
		customizerSplit2 = split;
	}

	public static int getCustomizerSplit1()
	{
		return customizerSplit1;
	}

	public static int getCustomizerSplit2()
	{
		return customizerSplit2;
	}

	public static int getExcSkillCost()
	{
		return excSkillCost;
	}

	public static void setExcSkillCost(final int argExcSkillCost)
	{
		excSkillCost = argExcSkillCost;
	}

	public static int getIntCrossClassSkillCost()
	{
		return intCrossClassSkillCost;
	}

	public static void setIntCrossClassSkillCost(final int argIntCrossClassSkillCost)
	{
		intCrossClassSkillCost = argIntCrossClassSkillCost;
	}

	public static int getTabPlacement()
	{
		return tabPlacement;
	}

	public static void setTabPlacement(final int argTabPlacement)
	{
		tabPlacement = argTabPlacement;
	}

	//public static int getInitialStatMin()
	//{
	//return initialStatMin;
	//}

	//public static void setInitialStatMin(final int argInitialStatMin)
	//{
	//initialStatMin = argInitialStatMin;
	//}

	public static boolean isIgnoreLevelCap()
	{
		return ignoreLevelCap;
	}

	public static void setIgnoreLevelCap(final boolean argIgnoreLevelCap)
	{
		ignoreLevelCap = argIgnoreLevelCap;
	}

	public static void setIncludeSkills(final int argIncludeSkills)
	{
		includeSkills = argIncludeSkills;
	}

	public static boolean isFreeClothesAtFirst()
	{
		return freeClothesAtFirst;

	}

	public static void setFreeClothesAtFirst(final boolean argFreeClothesAtFirst)
	{
		freeClothesAtFirst = argFreeClothesAtFirst;
	}

	public static boolean showD20Info()
	{
		return showD20InfoAtStart;
	}

	public static void setShowD20Info(boolean argShowD20InfoAtStart)
	{
		showD20InfoAtStart = argShowD20InfoAtStart;
	}

	public static boolean showLicense()
	{
		return showOGLOnLoad;
	}

	public static void setShowLicense(final boolean arg)
	{
		showOGLOnLoad = arg;
	}

	private static void setIgnoreEquipmentCost(final boolean argIgnoreEquipmentCost)
	{
		ignoreEquipmentCost = argIgnoreEquipmentCost;
	}

	//public static boolean isStatPoolUnlimited()
	//{
	//return unlimitedStatPool;
	//}

	//public static void setUnlimitedStatPool(final boolean argUnlimitedStatPool)
	//{
	//unlimitedStatPool = argUnlimitedStatPool;
	//}

	public static boolean isMonsterDefault()
	{
		return useMonsterDefault;
	}

	public static void setMonsterDefault(final boolean argMonsterDefault)
	{
		useMonsterDefault = argMonsterDefault;
	}

	public static int getIncludeSkills()
	{
		return includeSkills;
	}

	public static GameMode getGame()
	{
		return game;
	}

	public static void setGame(String g)
	{
		final GameMode newMode = Globals.getGameModeNamed(g);
		if (newMode != null)
		{
			game = newMode;
		}
	}

	//public static int getInitialStatMax()
	//{
	//return initialStatMax;
	//}

	//public static void setInitialStatMax(final int argInitialStatMax)
	//{
	//initialStatMax = argInitialStatMax;
	//}

	private static boolean isGrittyACMode()
	{
		return grittyACMode;
	}

	private static void setGrittyACMode(final boolean argGrittyACMode)
	{
		grittyACMode = argGrittyACMode;
	}

	public static boolean isLoadCampaignsAtStart()
	{
		return loadCampaignsAtStart;
	}

	public static void setLoadCampaignsAtStart(final boolean argLoadCampaignsAtStart)
	{
		loadCampaignsAtStart = argLoadCampaignsAtStart;
	}

	public static boolean isLoadCampaignsWithPC()
	{
		return loadCampaignsWithPC;
	}

	public static void setLoadCampaignsWithPC(final boolean argLoadCampaignsWithPC)
	{
		loadCampaignsWithPC = argLoadCampaignsWithPC;
	}

	public static boolean isOptionAllowedInSources()
	{
		return optionAllowedInSources;
	}

	public static void setOptionAllowedInSources(final boolean argOptionAllowedInSources)
	{
		optionAllowedInSources = argOptionAllowedInSources;
	}

	public static boolean getSaveCustomEquipment()
	{
		return isSaveCustomInLst();
	}

	public static void setSaveCustomEquipment(final boolean saveCustomInLst)
	{
		setSaveCustomInLst(saveCustomInLst);
	}

	public static int getLookAndFeel()
	{
		return looknFeel;
	}

	public static void setLookAndFeel(final int argLookAndFeel)
	{
		looknFeel = argLookAndFeel;
	}

	public static int getChaTabPlacement()
	{
		return chaTabPlacement;
	}

	public static void setChaTabPlacement(final int argChaTabPlacement)
	{
		chaTabPlacement = argChaTabPlacement;
	}

	private static boolean isIgnoreEquipmentCost()
	{
		return ignoreEquipmentCost;
	}

	//public static boolean isUnlimitedStatPool()
	//{
	//return unlimitedStatPool;
	//}

	private static boolean isUseMonsterDefault()
	{
		return useMonsterDefault;
	}

	private static boolean isSaveCustomInLst()
	{
		return saveCustomInLst;
	}

	private static void setSaveCustomInLst(final boolean argSaveCustomInLst)
	{
		saveCustomInLst = argSaveCustomInLst;
	}

	private static void setRollMethodExpression(final String argRollMethodExpression)
	{
		rollMethodExpression = argRollMethodExpression;
	}

	private static String[] getRollMethodExpressions()
	{
		return rollMethodExpressions;
	}

	static boolean isAutogenMasterwork()
	{
		return autogenMasterwork;
	}

	private static void setAutogenMasterwork(final boolean argAutogenMasterwork)
	{
		autogenMasterwork = argAutogenMasterwork;
	}

	static boolean isAutogenMagic()
	{
		return autogenMagic;
	}

	private static void setAutogenMagic(final boolean argAutogenMagic)
	{
		autogenMagic = argAutogenMagic;
	}

	static boolean isAutogenRacial()
	{
		return autogenRacial;
	}

	private static void setAutogenRacial(final boolean argAutogenRacial)
	{
		autogenRacial = argAutogenRacial;
	}

	static boolean isAutogenExoticMaterial()
	{
		return autogenExoticMaterial;
	}

	private static void setAutogenExoticMaterial(final boolean argAutogenExoticMaterial)
	{
		autogenExoticMaterial = argAutogenExoticMaterial;
	}

	private static boolean isWantToLoadMasterworkAndMagic()
	{
		return wantToLoadMasterworkAndMagic;
	}

	public static boolean wantToLoadMasterworkAndMagic()
	{
		return isWantToLoadMasterworkAndMagic();
	}

	public static void setWantToLoadMasterworkAndMagic(final boolean bFlag)
	{
		wantToLoadMasterworkAndMagic = bFlag;
	}

	public static int getMaxPotionSpellLevel()
	{
		return maxPotionSpellLevel;
	}

	public static void setMaxPotionSpellLevel(final int argMaxPotionSpellLevel)
	{
		maxPotionSpellLevel = argMaxPotionSpellLevel;
	}

	public static int getMaxWandSpellLevel()
	{
		return maxWandSpellLevel;
	}

	public static void setMaxWandSpellLevel(final int argMaxWandSpellLevel)
	{
		maxWandSpellLevel = argMaxWandSpellLevel;
	}

	public static boolean isMetamagicAllowedInEqBuilder()
	{
		return allowMetamagicInCustomizer;
	}

	public static void setMetamagicAllowedInEqBuilder(final boolean argAllowMetamagicInCustomizer)
	{
		allowMetamagicInCustomizer = argAllowMetamagicInCustomizer;
	}

	private static boolean isSpellMarketPriceAdjusted()
	{
		return spellMarketPriceAdjusted;
	}

	private static void setSpellMarketPriceAdjusted(final boolean argSpellMarketPriceAdjusted)
	{
		spellMarketPriceAdjusted = argSpellMarketPriceAdjusted;
	}

	public static int getFeatAutoColor()
	{
		return featAutoColor;
	}

	public static void setFeatAutoColor(final int newColor)
	{
		featAutoColor = newColor & 0x00FFFFFF;
	}

	public static int getFeatVirtualColor()
	{
		return featVirtualColor;
	}

	public static void setFeatVirtualColor(final int newColor)
	{
		featVirtualColor = newColor & 0x00FFFFFF;
	}

	public static boolean isHPMaxAtFirstLevel()
	{
		return hpMaxAtFirstLevel;
	}

	public static void setHPMaxAtFirstLevel(final boolean argHpMaxAtFirstLevel)
	{
		hpMaxAtFirstLevel = argHpMaxAtFirstLevel;
	}

	public static int getHPRollMethod()
	{
		return hpRollMethod;
	}

	public static void setHPRollMethod(final int argHpRollMethod)
	{
		hpRollMethod = argHpRollMethod;
	}

	/**
	 * Method:
	 * 0: One random number
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * 7: 5d6 Drop lowest and middle FR #458917
	 * 8: All 10's
	 */
	public static int getRollMethod()
	{
		return rollMethod;
	}

	/**
	 * Method:
	 * 0: One random number
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * 7: 5d6 Drop lowest and middle FR #458917
	 * 8: All same #
	 * 9: Purchase Mode
	 */
	public static void setRollMethod(final int argRollMethod)
	{
		rollMethod = argRollMethod;
		if (argRollMethod != Constants.ROLLINGMETHOD_PURCHASE)
		{
			setPurchaseMethodName("");
		}
		setRollMethodExpression(getRollMethodExpression(rollMethod));
	}

	private static String getRollMethodExpression()
	{
		return rollMethodExpression;
	}

	public static String getRollMethodExpression(final int method)
	{
		return getRollMethodExpressions()[method];
	}

	public static String getSkinLFThemePack()
	{
		return skinLFThemePack;
	}

	public static boolean isBoolBypassFeatPreReqs()
	{
		return boolBypassFeatPreReqs;
	}

	public static void setBoolBypassFeatPreReqs(final boolean argBoolBypassFeatPreReqs)
	{
		boolBypassFeatPreReqs = argBoolBypassFeatPreReqs;
	}

	public static boolean isBoolBypassClassPreReqs()
	{
		return boolBypassClassPreReqs;
	}

	public static void setBoolBypassClassPreReqs(final boolean argBoolBypassClassPreReqs)
	{
		boolBypassClassPreReqs = argBoolBypassClassPreReqs;
	}

	public static boolean isBoolBypassMaxSkillRank()
	{
		return boolBypassMaxSkillRank;
	}

	public static void setBoolBypassMaxSkillRank(final boolean argBoolBypassMaxSkillRank)
	{
		boolBypassMaxSkillRank = argBoolBypassMaxSkillRank;
	}

	public static void setSkinLFThemePack(final String argSkinLFThemePack)
	{
		skinLFThemePack = argSkinLFThemePack;
	}

/////////////////////////////////////////////////
// Yanked for WotC compliance
//	/**
//	 * @return true if the maximum possible amount of starting gold should be given
//	 */
//	public static boolean isMaxStartingGold()
//	{
//		return maxStartingGold;
//	}
//
//	/**
//	 * @param argMaxStartingGold Set to true if the maximum possible amount of starting gold should be given
//	 */
//	public static void setMaxStartingGold(final boolean argMaxStartingGold)
//	{
//		maxStartingGold = argMaxStartingGold;
//	}
/////////////////////////////////////////////////

	public static boolean isApplyWeightPenaltyToSkills()
	{
		return applyWeightPenaltyToSkills;
	}

	public static boolean isApplyLoadPenaltyToACandSkills()
	{
		return applyLoadPenaltyToACandSkills;
	}

	public static void setApplyLoadPenaltyToACandSkills(final boolean argApplyLoadPenalty)
	{
		applyLoadPenaltyToACandSkills = argApplyLoadPenalty;
	}

	public static void setApplyWeightPenaltyToSkills(final boolean argApplyWeightPenaltyToSkills)
	{
		applyWeightPenaltyToSkills = argApplyWeightPenaltyToSkills;
	}

	public static boolean isSkillIncrementBefore()
	{
		return skillIncrementBefore;
	}

	public static void setSkillIncrementBefore(final boolean flag)
	{
		skillIncrementBefore = flag;
	}

	public static int getPrereqFailColor()
	{
		return prereqFailColor;
	}

	public static String getPrereqFailColorAsHtml()
	{
		final StringBuffer rString = new StringBuffer("<font color=");
		if (getPrereqFailColor() != 0)
		{
			rString.append("\"#").append(Integer.toHexString(getPrereqFailColor())).append("\"");
		}
		else
		{
			rString.append("red");
		}
		rString.append('>');
		return rString.toString();
	}

	public static void setPrereqFailColor(final int newColor)
	{
		prereqFailColor = newColor & 0x00FFFFFF;
	}

	public static boolean isToolTipTextShown()
	{
		return toolTipTextShown;
	}

	public static void setToolTipTextShown(final boolean showToolTipText)
	{
		toolTipTextShown = showToolTipText;
		final PCGen_Frame1 frame = Globals.getRootFrame();
		// Guard against load order
		if (frame != null)
		{
			frame.forceUpdate_PlayerTabs();
		}
	}

	private static boolean isSummaryTabShown()
	{
		return summaryTabShown;
	}

	private static void setSummaryTabShown(final boolean showSummaryTab)
	{
		summaryTabShown = showSummaryTab;
	}

	static boolean isPreviewTabShown()
	{
		return previewTabShown;
	}

	static void setPreviewTabShown(final boolean showPreviewTab)
	{
		previewTabShown = showPreviewTab;
	}

	public static boolean isAbilitiesShownAsATab()
	{
		return abilitiesShownAsTab;
	}

	public static void setAbilitiesShownAsATab(final boolean showAbilitiesAsTab)
	{
		abilitiesShownAsTab = showAbilitiesAsTab;
	}

	//public static boolean getInventoryTab_IgnoreCost()
	//{
	//return inventoryTab_IgnoreCost;
	//}

	//public static void setInventoryTab_IgnoreCost(final boolean ignoreCost)
	//{
	//inventoryTab_IgnoreCost = ignoreCost;
	//}

	public static void setInventoryTab_BuyRate(final int argBuyRate)
	{
		inventoryTab_BuyRate = argBuyRate;
	}

	public static int getInventoryTab_BuyRate()
	{
		return inventoryTab_BuyRate;
	}

	public static void setInventoryTab_SellRate(final int argSellRate)
	{
		inventoryTab_SellRate = argSellRate;
	}

	public static int getInventoryTab_SellRate()
	{
		return inventoryTab_SellRate;
	}

	private static int getOptionTabPlacement(final String optionName, final int defaultValue)
	{
		final String aString = getPCGenOption(optionName, convertTabPlacementToString(defaultValue));
		int iVal;
		try
		{
			iVal = Integer.parseInt(aString);
			switch (iVal)
			{
				case SwingConstants.TOP:
				case SwingConstants.LEFT:
				case SwingConstants.BOTTOM:
				case SwingConstants.RIGHT:
					break;
				default:
					iVal = defaultValue;
					break;
			}
		}
		catch (Exception exc)
		{
			if ("TOP".equals(aString))
			{
				iVal = SwingConstants.TOP;
			}
			else if ("LEFT".equals(aString))
			{
				iVal = SwingConstants.LEFT;
			}
			else if ("BOTTOM".equals(aString))
			{
				iVal = SwingConstants.BOTTOM;
			}
			else if ("RIGHT".equals(aString))
			{
				iVal = SwingConstants.RIGHT;
			}
			else
			{
				iVal = defaultValue;
			}
		}
		return iVal;
	}

	private static String convertTabPlacementToString(final int placement)
	{
		switch (placement)
		{
			case SwingConstants.BOTTOM:
				return "BOTTOM";
			case SwingConstants.LEFT:
				return "LEFT";
			case SwingConstants.RIGHT:
				return "RIGHT";
			case SwingConstants.TOP:
			default:
				return "TOP";
		}
	}

	public static boolean getAutogen(final int idx)
	{
		if (!isWantToLoadMasterworkAndMagic())
		{
			switch (idx)
			{
				case Constants.AUTOGEN_RACIAL:
					return isAutogenRacial();
				case Constants.AUTOGEN_MASTERWORK:
					return isAutogenMasterwork();
				case Constants.AUTOGEN_MAGIC:
					return isAutogenMagic();
				case Constants.AUTOGEN_EXOTICMATERIAL:
					return isAutogenExoticMaterial();
				default:
					break;
			}
		}
		return false;
	}

	public static void setAutogen(final int idx, final boolean bFlag)
	{
		switch (idx)
		{
			case Constants.AUTOGEN_RACIAL:
				setAutogenRacial(bFlag);
				break;
			case Constants.AUTOGEN_MASTERWORK:
				setAutogenMasterwork(bFlag);
				break;
			case Constants.AUTOGEN_MAGIC:
				setAutogenMagic(bFlag);
				break;
			case Constants.AUTOGEN_EXOTICMATERIAL:
				setAutogenExoticMaterial(bFlag);
				break;
			default:
				break;
		}
	}

	private static boolean isPurchaseStatModeAllowed()
	{
		if ((pointBuyStatCosts == null) || (pointBuyStatCosts.size() == 0))
		{
			return false;
		}
		return true;
	}

	public static int[] getAbilityScoreCost()
	{
/////////////////////////////////////////////////
// Yanked for WotC compliance
		if (!isPurchaseStatModeAllowed())
		{
			//better to return a Zero length array than null.
			return null;
		}
		//
		// Only build this list once
		//
		if (abilityScoreCost != null)
		{
			return abilityScoreCost;
		}

		abilityScoreCost = new int[getPurchaseScoreMax() - getPurchaseScoreMin() + 1];	// Should be 1 value for each stat in range


		//
		// Run through the keys. If there is a missing value, then use the previous point cost
		//
		int i = 0;
		int lastStat = Integer.MIN_VALUE;
		int lastCost = 0;
		for (Iterator e = pointBuyStatCosts.keySet().iterator(); e.hasNext();)
		{
			final Integer statValue = (Integer) e.next();
			if ((lastStat != Integer.MIN_VALUE) && (lastStat + 1 != statValue.intValue()))
			{
				for (int x = lastStat + 1; x < statValue.intValue(); ++x)
				{
					abilityScoreCost[i++] = lastCost;
				}
			}

			final Integer statCost = (Integer) pointBuyStatCosts.get(statValue);
			lastStat = statValue.intValue();
			lastCost = statCost.intValue();
			abilityScoreCost[i++] = lastCost;
		}
/////////////////////////////////////////////////
		return abilityScoreCost;
	}

/////////////////////////////////////////////////
// Yanked for WotC compliance
//	public static void setAbilityScoreCost(final int[] scores)
//	{
//		abilityScoreCost = scores;
//	}
//
//	public static void setAbilityScoreCost(final int abilityScoreIndex, final int score)
//	{
//		abilityScoreCost[abilityScoreIndex] = score;
//	}
/////////////////////////////////////////////////

	public static int getAbilityScoreCost(final int abilityScoreIndex)
	{
		final int[] asc = getAbilityScoreCost();
		if (asc == null)
		{
			return 0;
		}
		return asc[abilityScoreIndex];
	}

	public static int getNameDisplayStyle()
	{
		return nameDisplayStyle;
	}

	public static void setNameDisplayStyle(final int style)
	{
		nameDisplayStyle = style;
		final PCGen_Frame1 frame = Globals.getRootFrame();
		// Guard against load order
		if (frame != null)
		{
			frame.forceUpdate_PlayerTabs();
		}
	}

	private static void setOpenRecentPCs(final String[] strings)
	{
		Globals.getRootFrame().setOpenRecentPCs(strings);
	}

	private static void setOpenRecentParties(final String[] strings)
	{
		Globals.getRootFrame().setOpenRecentParties(strings);
	}

	private static void setRanStartingWizard(boolean ran)
	{
		SettingsHandler.ranStartingWizard = ran;
	}

	//
	// Clear purchase mode stat costs
	//
	public static void clearPointBuyStatCosts()
	{
		pointBuyStatCosts = null;
		abilityScoreCost = null;
	}

	//
	// Add a stat/cost pair to purchase mode stat costs
	//
	public static void addPointBuyStatCost(final int statValue, final int cost)
	{
		if (pointBuyStatCosts == null)
		{
			pointBuyStatCosts = new TreeMap();
		}

		abilityScoreCost = null;
		pointBuyStatCosts.put(new Integer(statValue), new Integer(cost));
	}

	//
	// Get the lowest stat value in the purchase mode stat table
	//
	public static int getPurchaseScoreMin()
	{
		if (pointBuyStatCosts == null)
		{
			return -1;
		}
		return ((Integer) pointBuyStatCosts.firstKey()).intValue();
	}

	//
	// Get the highest stat value in the purchase mode stat table
	//
	public static int getPurchaseScoreMax()
	{
		if (pointBuyStatCosts == null)
		{
			return -1;
		}
		return ((Integer) pointBuyStatCosts.lastKey()).intValue();
	}

	//
	// Get the highest stat score that can be purchased free
	//
	public static int getPurchaseModeBaseStatScore()
	{
		for (int i = 0, x = getPurchaseScoreMax() - getPurchaseScoreMin() + 1; i < x; ++i)
		{
			if (getAbilityScoreCost(i) == 0)
			{
				return getPurchaseScoreMin() + i;
			}
		}
		return getPurchaseScoreMin() - 1;
	}

	//
	// Add a purchase mode method
	//
	public static void addPurchaseModeMethod(final String methodName, final int points)
	{
		if (getPurchaseMethodByName(methodName) == null)
		{
			if (pointBuyMethods == null)
			{
				pointBuyMethods = new ArrayList();
			}
			final PointBuyMethod pmb = new PointBuyMethod(methodName, points);
			pointBuyMethods.add(pmb);
		}
	}

	//
	// Get the number of user-defined purchase methods
	//
	public static int getPurchaseMethodCount()
	{
		if (pointBuyMethods != null)
		{
			return pointBuyMethods.size();
		}
		return 0;
	}

	//
	// Find a user-defined purchase method by name
	//
	public static PointBuyMethod getPurchaseMethodByName(String methodName)
	{
		if (pointBuyMethods != null)
		{
			for (int idx = 0, x = pointBuyMethods.size(); idx < x; ++idx)
			{
				final PointBuyMethod pbm = (PointBuyMethod) pointBuyMethods.get(idx);
				if (pbm.getMethodName().equalsIgnoreCase(methodName))
				{
					return pbm;
				}
			}
		}
		return null;
	}

	public static void clearPurchaseModeMethods()
	{
		pointBuyMethods = null;
	}

	public static int getPurchaseModeMethodPool()
	{
		if (!isPurchaseStatMode())
		{
			return -1;
		}
		return getPurchaseMethodByName(purchaseMethodName).getPoints();
	}

	public static PointBuyMethod getPurhaseMethod(int idx)
	{
		if ((pointBuyMethods == null) || (idx > pointBuyMethods.size()))
		{
			return null;
		}
		return (PointBuyMethod) pointBuyMethods.get(idx);
	}

	public static void setPurchaseMethodName(String argMethodName)
	{
		if (argMethodName.length() != 0)
		{
			setRollMethod(Constants.ROLLINGMETHOD_PURCHASE);
		}
		purchaseMethodName = argMethodName;
	}

	public static String getPurchaseModeMethodName()
	{
		if (!isPurchaseStatMode())
		{
			return null;
		}
		return purchaseMethodName;
	}

	public static boolean isPurchaseStatMode()
	{
		//
		// Can't have purchase mode if no costs specified
		//
		if ((pointBuyStatCosts == null) || (pointBuyStatCosts.size() == 0) ||
			(getRollMethod() != Constants.ROLLINGMETHOD_PURCHASE) || (purchaseMethodName.length() == 0))
		{
			return false;
		}
		return getPurchaseMethodByName(purchaseMethodName) != null;
	}

	public static void setCleanupTempFiles(int argDoCleanup)
	{
		cleanupTempFiles = argDoCleanup;
	}

	public static int getCleanupTempFiles()
	{
		return cleanupTempFiles;
	}

	public static void setHideMonsterClasses(boolean argHideMonsterClasses)
	{
		hideMonsterClasses = argHideMonsterClasses;
	}

	public static boolean hideMonsterClasses()
	{
		return hideMonsterClasses;
	}
}
