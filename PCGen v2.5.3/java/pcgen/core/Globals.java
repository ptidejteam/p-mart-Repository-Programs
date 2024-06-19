/*
 * Globals.java
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
 * $Id: Globals.java,v 1.1 2006/02/20 23:54:34 vauchers Exp $
 */

package pcgen.core;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.*;
import pcgen.gui.Chooser;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Delta;

/**
 * This is like the top level model container. However,
 * it is build from static methods rather than instantiated.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
public class Globals implements Constants
{

	/** An empty race. Duh. */
	public static Race s_EMPTYRACE = null;

	public static String[] s_ATTRIBLONG = null;
	public static String[] s_ATTRIBSHORT = null;
	public static boolean[] s_ATTRIBROLL = null;
	public static String s_STATNAMES = ""; //"STRDEXCONINTWISCHA";

	private static Map raceMap = new HashMap();
	private static ArrayList classList = new ArrayList();
	private static ArrayList templateList = new ArrayList();
	private static ArrayList skillList = new ArrayList();
	private static ArrayList featList = new ArrayList();
	private static ArrayList domainList = new ArrayList();
	private static ArrayList deityList = new ArrayList();
	private static ArrayList pcList = new ArrayList();
	private static Map spellMap = new HashMap();
	private static ArrayList weaponProfList = new ArrayList();
	private static ArrayList schoolsList = new ArrayList();
	private static ArrayList languageList = new ArrayList();
	private static ArrayList colorList = new ArrayList();
	private static ArrayList traitList = new ArrayList();
	private static ArrayList phobiaList = new ArrayList();
	private static ArrayList locationList = new ArrayList();
	private static ArrayList interestsList = new ArrayList();
	private static ArrayList phraseList = new ArrayList();
	private static ArrayList hairStyleList = new ArrayList();
	private static ArrayList speechList = new ArrayList();

	private static ArrayList equipmentList = new ArrayList();
	private static ArrayList specialsList = new ArrayList();
	private static ArrayList loadStrings = new ArrayList();
	private static ArrayList campaignList = new ArrayList();
	private static ArrayList XPList = new ArrayList();
	private static ArrayList bonusStackList = new ArrayList();
	private static TreeSet weaponTypes = new TreeSet();
	private static ArrayList modifierList = new ArrayList();
	private static PlayerCharacter currentPC = null;
	private static ArrayList sizeAdjustmentList = new ArrayList();
	private static ArrayList custColumnWidth = new ArrayList();

	/*
	 * we need these sets for efficient filter creation
	 *
	 * author: Thomas Behr 15-02-02
	 */
	private static TreeSet castingTimesSet = new TreeSet();
	private static TreeSet descriptorSet = new TreeSet();
	private static TreeSet effectTypesSet = new TreeSet();
	private static TreeSet pantheonsSet = new TreeSet();
	private static TreeSet raceTypesSet = new TreeSet();
	private static TreeSet rangesSet = new TreeSet();
	private static TreeSet srSet = new TreeSet();
	private static TreeSet subschoolsSet = new TreeSet();

	private static Properties filterSettings = new Properties();

	/*
         * bsmeister 11/20/2001
	 * the DenominationList class is derived from ArrayList
	 */
	private static DenominationList denominationList = new DenominationList();

	private static int hpPct = 100;
	private static int excSkillCost = 0;
	private static int intCrossClassSkillCost = 2;
	private static boolean boolBypassMaxSkillRank = false;
	private static boolean boolBypassFeatPreReqs = false;
	private static boolean boolBypassClassPreReqs = false;
	private static boolean debugMode = false;
	private static Point leftUpperCorner = null;
	private static Point customizerLeftUpperCorner = null;
	private static Dimension customizerDimension = null;
	private static int customizerSplit1 = -1;
	private static int customizerSplit2 = -1;
	private static String dmNotes = "";

	private static boolean inventoryTab_IgnoreCost = false;
	private static int inventoryTab_AvailableListMode = pcgen.gui.InfoInventory.VIEW_TYPE_SUBTYPE_NAME;
	private static int inventoryTab_SelectedListMode = pcgen.gui.InfoInventory.VIEW_NAME;
	private static int skillsTab_AvailableListMode = pcgen.gui.InfoSkills.VIEW_TYPE_NAME;
	private static int skillsTab_SelectedListMode = pcgen.gui.InfoSkills.VIEW_NAME;
	private static int spellsTab_AvailableListMode = pcgen.gui.InfoSpells.VIEW_CLASS;
	private static int spellsTab_SelectedListMode = pcgen.gui.InfoSpells.VIEW_CLASS;
	private static int featTab_AvailableListMode = pcgen.gui.InfoFeats.VIEW_TYPENAME;
	private static int featTab_SelectedListMode = pcgen.gui.InfoFeats.VIEW_NAMEONLY;

	private static boolean bAutoGeneration = false;

	private static int sourceDisplay = SOURCELONG;

	/**
	 * This is <code>true</code> when the campaign data structures are
	 * sorted.
	 */
	private static boolean d_sorted = false;

	/**
	 *  This will be overridden at startup with values read from properties.
	 */
	private static int[] statCost = new int[]{
		1,
		2,
		3,
		4,
		5,
		6,
		8,
		10,
		13,
		16
	};

	private static int[] dieSizes = new int[]{
		1,
		2,
		3,
		4,
		6,
		8,
		10,
		12,
		20,
		100,
		1000
	};



	/** Various Game Modes */
	/* Changed to a single string to make setting it automaticly easier. */
	private static String game = DND_MODE;
	//Stat info
	private static boolean ignoreLevelCap = false;
	private static boolean purchaseStatMode = false;
	private static int initialStatMin = 3;
	private static int initialStatMax = 18;
	private static boolean ignoreEquipmentCost = false;
	private static boolean unlimitedStatPool = false;
	private static boolean useMonsterDefault = true;
	/**
	 *  0==None, 1=Untrained, 2=all
	 */
	private static int includeSkills = 1;
	private static boolean grimHPMode = false;
	private static boolean grittyACMode = false;
	private static boolean loadCampaignsAtStart = false;
	private static boolean loadCampaignsWithPC = true;
	private static boolean saveCustomInLst = false;

	/**
	 *  0=top 1=left  2=bottom 3=right
	 */
	private static int tabPlacement = 0;
	/**
	 *  0=top 1=left  2=bottom 3=right
	 */
	private static int chaTabPlacement = 0;

	/**0=UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	 *1=UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	 *2=skinnable
	 */

	private static int looknFeel = 1;
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
	 */
	private static int rollMethod = 1;
	private static String rollMethodExpression = "roll(4,6,[2,3,4])"; // standard

	private static String[] rollMethodExpressions = {
		"0", // all 0s
		"roll(4,6,[2,3,4])", // 4d6, drop lowest
		"3d6", // 3d6
		"roll(5,6,[3,4,5])", // 5d6, drop 2 lowest
		"roll(4,5,[2,3,4])+3", // 4d6, reroll 1s, drop lowest
		"roll(4,4,[2,3,4])+6", // 4d6, reroll 1s and 2s, drop lowest
		"3d6+5", // 3d6+5
		"roll(5,6,[2,4,5])", // 5d6, drop lowest and middle
		"10", // all 10s
	};

	private static boolean toolTipTextShown = true;
	private static boolean previewTabShown = false;
	private static boolean hpMaxAtFirstLevel = true;
	private static int hpRollMethod = s_HP_STANDARD;
	private static int prereqFailColor = 0xFF0000;			// 0 = black, 0xFF0000 = red, 0xFFFFFF = white
	private static int featAutoColor = 0xB2B200;			// dark yellow
	private static int featVirtualColor = 0xFF00FF;			// magenta
	private static boolean skillIncrementBefore = true;

	private static String spellPoints = "0";
	/*
	 * bsmeister
	 * loads the coin file lines.
	 */

	private static JFrame rootFrame;

	private static boolean autogenMasterwork = false;
	private static boolean autogenMagic = false;
	private static boolean autogenRacial = false;
	private static boolean autogenExoticMaterial = false;
	private static boolean wantToLoadMasterworkAndMagic = false;

	private static ArrayList paperInfo = new ArrayList();
	private static int selectedPaper = -1;

	// NOTE: The defaultPath is duplicated in LstSystemLoader.
	private static String defaultPath = System.getProperty("user.dir");
	private static String defaultPcgPath = defaultPath + File.separator + "characters";

	/* That browserPath is set to null is intentional. */
	private static File browserPath = null; //Intentional null

	private static File templatePath = new File(defaultPath + File.separator + "templates");
	private static File pcgPath = new File(defaultPath);
	private static File htmlOutputPath = new File(defaultPath);
	private static String tmpPath = System.getProperty("java.io.tmpdir");
	private static File tempPath = new File(tmpPath);

	private static String selectedTemplate = templatePath.getAbsolutePath() + File.separator + "csheet.htm";
	private static String selectedPartyTemplate = templatePath.getAbsolutePath() + File.separator + "psheet.htm";
	private static File pccFilesLocation = null;
	private static Properties options = new Properties();
	private static boolean autoFeatsRefundable = false;
	private static boolean treatInHandAsEquippedForAttacks = false;
	private static boolean showToolBar = true;

	private static boolean isROG = false;

	private final static Random random = new Random(System.currentTimeMillis());

	private static String skinLFThemePack = null;

	private static boolean applyWeightPenaltyToSkills = true;
	private static boolean applyLoadPenaltyToACandSkills = true;

	private static int allStatsValue = 10;
	private static boolean maxStartingGold = false;

	// The following if for the Internationalization project.
	private static String language = "en";
	private static String country = "US";

	public static void setAttribLong(String[] s)
	{
		s_ATTRIBLONG = s;
	}

	public static void setAttribLong(int index, String s)
	{
		s_ATTRIBLONG[index] = s;
	}

	public static void setAttribShort(String[] s)
	{
		s_ATTRIBSHORT = s;
	}

	public static void setAttribShort(int index, String s)
	{
		s_ATTRIBSHORT[index] = s;
	}

	public static void setAttribRoll(boolean[] b)
	{
		s_ATTRIBROLL = b;
	}

	public static void setAttribRoll(int index, boolean b)
	{
		s_ATTRIBROLL[index] = b;
	}

	public static void appendStatName(String newStat)
	{
		s_STATNAMES += newStat;
	}

	/**
	 * whether or not the GUI is used (false for command line)
	 */
	private static boolean useGUI = true;

	/**
	 * Where to load the system lst files from.
	 */
	private static File pcgenSystemDir = new File(defaultPath + File.separator + "system");

	public static File getPcgenSystemDir()
	{
		return pcgenSystemDir;
	}

	public static void setPcgenSystemDir(File pcgenSystemDir)
	{
		Globals.pcgenSystemDir = pcgenSystemDir;
	}

	public static void setLanguage(String aString)
	{
		language = aString;
	}

	public static void setCountry(String aString)
	{
		country = aString;
	}

	public static String getCountry()
	{
		return country;
	}

	public static String getLanguage()
	{
		return language;
	}

	public static boolean getInventoryTab_IgnoreCost()
	{
		return inventoryTab_IgnoreCost;
	}

	public static void setInventoryTab_IgnoreCost(boolean ignoreCost)
	{
		inventoryTab_IgnoreCost = ignoreCost;
	}

	public static int getInventoryTab_AvailableListMode()
	{
		return inventoryTab_AvailableListMode;
	}

	public static void setInventoryTab_AvailableListMode(int listMode)
	{
		inventoryTab_AvailableListMode = listMode;
	}

	public static int getInventoryTab_SelectedListMode()
	{
		return inventoryTab_SelectedListMode;
	}

	public static void setInventoryTab_SelectedListMode(int listMode)
	{
		inventoryTab_SelectedListMode = listMode;
	}

	public static int getSkillsTab_AvailableListMode()
	{
		return skillsTab_AvailableListMode;
	}

	public static void setSkillsTab_AvailableListMode(int listMode)
	{
		skillsTab_AvailableListMode = listMode;
	}

	public static int getSkillsTab_SelectedListMode()
	{
		return skillsTab_SelectedListMode;
	}

	public static void setSkillsTab_SelectedListMode(int listMode)
	{
		skillsTab_SelectedListMode = listMode;
	}

	public static int getFeatTab_AvailableListMode()
	{
		return featTab_AvailableListMode;
	}

	public static void setFeatTab_AvailableListMode(int listMode)
	{
		featTab_AvailableListMode = listMode;
	}

	public static int getFeatTab_SelectedListMode()
	{
		return featTab_SelectedListMode;
	}

	public static void setFeatTab_SelectedListMode(int listMode)
	{
		featTab_SelectedListMode = listMode;
	}


	public static int getSpellsTab_AvailableListMode()
	{
		return spellsTab_AvailableListMode;
	}

	public static void setSpellsTab_AvailableListMode(int listMode)
	{
		spellsTab_AvailableListMode = listMode;
	}

	public static int getSpellsTab_SelectedListMode()
	{
		return spellsTab_SelectedListMode;
	}

	public static void setSpellsTab_SelectedListMode(int listMode)
	{
		spellsTab_SelectedListMode = listMode;
	}

	public static boolean getUseGUI()
	{
		return useGUI;
	}

	public static void setUseGUI(boolean aBool)
	{
		useGUI = aBool;
	}

	/**
	 * @return true if the maximum possible amount of starting gold should be given
	 */
	public static boolean isMaxStartingGold()
	{
		return maxStartingGold;
	}

	/**
	 * @param argMaxStartingGold Set to true if the maximum possible amount of starting gold should be given
	 */
	public static void setMaxStartingGold(boolean argMaxStartingGold)
	{
		maxStartingGold = argMaxStartingGold;
	}

	public static int getAllStatsValue()
	{
		return allStatsValue;
	}

	public static void setAllStatsValue(int allStatsValue)
	{
		Globals.allStatsValue = allStatsValue;
	}

	public static boolean isApplyWeightPenaltyToSkills()
	{
		return applyWeightPenaltyToSkills;
	}

	public static void setApplyWeightPenalty(boolean applyWeightPenalty)
	{
		Globals.applyWeightPenaltyToSkills = applyWeightPenalty;
	}

	public static boolean isApplyLoadPenaltyToACandSkills()
	{
		return applyLoadPenaltyToACandSkills;
	}

	public static void setApplyLoadPenaltyToACandSkills(boolean applyLoadPenalty)
	{
		Globals.applyLoadPenaltyToACandSkills = applyLoadPenalty;
	}

	public static String getSkinLFThemePack()
	{
		return skinLFThemePack;
	}

	public static void setSkinLFThemePack(String skinLFThemePack)
	{
		Globals.skinLFThemePack = skinLFThemePack;
	}

	public static boolean wantToLoadMasterworkAndMagic()
	{
		return wantToLoadMasterworkAndMagic;
	}

	public static void setWantToLoadMasterworkAndMagic(boolean bFlag)
	{
		wantToLoadMasterworkAndMagic = bFlag;
	}

	public static boolean getAutogen(int idx)
	{
		if (!wantToLoadMasterworkAndMagic)
		{
			switch (idx)
			{
				case AUTOGEN_RACIAL:
					return autogenRacial;
				case AUTOGEN_MASTERWORK:
					return autogenMasterwork;
				case AUTOGEN_MAGIC:
					return autogenMagic;
				case AUTOGEN_EXOTICMATERIAL:
					return autogenExoticMaterial;
				default:
					break;
			}
		}
		return false;
	}

	public static void setAutogen(int idx, boolean bFlag)
	{
		switch (idx)
		{
			case AUTOGEN_RACIAL:
				autogenRacial = bFlag;
				break;
			case AUTOGEN_MASTERWORK:
				autogenMasterwork = bFlag;
				break;
			case AUTOGEN_MAGIC:
				autogenMagic = bFlag;
				break;
			case AUTOGEN_EXOTICMATERIAL:
				autogenExoticMaterial = bFlag;
				break;
			default:
				break;
		}
	}

	public static int getPaperCount()
	{
		return paperInfo.size();
	}

	public static boolean selectPaper(String paperName)
	{
		for (int i = 0; i < paperInfo.size(); i++)
		{
			final PaperInfo pi = (PaperInfo)paperInfo.get(i);
			if (pi.getName().equals(paperName))
			{
				selectedPaper = i;
				return true;
			}
		}
		selectedPaper = -1;
		return false;
	}

	public static int getPaperIndex()
	{
		return selectedPaper;
	}

	public static List getPaperInfoList()
	{
		return paperInfo;
	}

	public static String getPaperInfo(int infoType)
	{
		return getPaperInfo(selectedPaper, infoType);
	}

	public static String getPaperInfo(int idx, int infoType)
	{
		if ((idx < 0) || (idx >= paperInfo.size()))
		{
			return null;
		}
		final PaperInfo pi = (PaperInfo)paperInfo.get(idx);
		return pi.getPaperInfo(infoType);
	}

	public static boolean getTreatInHandAsEquippedForAttacks()
	{
		return treatInHandAsEquippedForAttacks;
	}

	public static void setTreatInHandAsEquippedForAttacks(boolean treatAsEquipped)
	{
		treatInHandAsEquippedForAttacks = treatAsEquipped;
	}

	public static boolean isToolBarShown()
	{
		return showToolBar;
	}

	public static void setToolBarShown(boolean showToolBar)
	{
		Globals.showToolBar = showToolBar;
	}

	public static boolean isToolTipTextShown()
	{
		return toolTipTextShown;
	}

	public static void setToolTipTextShown(boolean showToolTipText)
	{
		Globals.toolTipTextShown = showToolTipText;
	}

	public static boolean isPreviewTabShown()
	{
		return previewTabShown;
	}

	public static void setPreviewTabShown(boolean showPreviewTab)
	{
		Globals.previewTabShown = showPreviewTab;
	}

	/**
	 * Get a random integer between 0 (inclusive) and the given value (exclusive)
	 */
	public static int getRandomInt(final int high)
	{
		return random.nextInt(high);
	}

	public static int getRandomInt()
	{
		return random.nextInt();
	}

	public static String getDefaultPath()
	{
		return defaultPath;
	}

	/**
	 * Returns the name of the Default Spell Book.
	 * Usually "Known Spells" but "Full Reference Spells" for Sovereign Stone.
	 */
	public static String getDefaultSpellBook()
	{
		if (isSSd20Mode())
			return "Full Reference Spells";
		else
			return "Known Spells";
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
	public static void setPccFilesLocation(File argPccFilesLocation)
	{
		Globals.pccFilesLocation = argPccFilesLocation;
	}

	public static int[] getStatCost()
	{
		return statCost;
	}

	public static int getStatCost(final int stat)
	{
		return statCost[stat];
	}


	public static int[] getDieSizes()
	{
		return dieSizes;
	}

	public static void setStatCost(int stat, int statCost)
	{
		Globals.statCost[stat] = statCost;
	}

	public static int getTabPlacement()
	{

		return tabPlacement;
	}

	public static void setTabPlacement(int tabPlacement)
	{
		Globals.tabPlacement = tabPlacement;
	}

	public static int getInitialStatMin()
	{
		return initialStatMin;
	}

	public static void setInitialStatMin(int initialStatMin)
	{
		Globals.initialStatMin = initialStatMin;
	}

	public static boolean isIgnoreLevelCap()
	{
		return ignoreLevelCap;
	}

	public static void setIgnoreLevelCap(boolean ignoreLevelCap)
	{
		Globals.ignoreLevelCap = ignoreLevelCap;
	}

	public static void setIncludeSkills(int includeSkills)
	{
		Globals.includeSkills = includeSkills;
	}

	public static boolean isEquipmentCostIgnored()
	{
		return ignoreEquipmentCost;
	}

	public static void setEquipmentCostIgnored(boolean ignoreEquipmentCost)
	{
		Globals.ignoreEquipmentCost = ignoreEquipmentCost;
	}

	public static boolean isStatPoolUnlimited()
	{
		return unlimitedStatPool;
	}

	public static void setStatPoolUnlimited(boolean unlimitedStatPool)
	{
		Globals.unlimitedStatPool = unlimitedStatPool;
	}

	public static boolean isPurchaseStatMode()
	{
		return purchaseStatMode;
	}

	public static void setPurchaseStatMode(boolean purchaseStatMode)
	{
		Globals.purchaseStatMode = purchaseStatMode;
	}

	public static boolean isMonsterDefault()
	{
		return useMonsterDefault;
	}

	public static void setMonsterDefault(boolean monsterDefault)
	{
		useMonsterDefault = monsterDefault;
	}

	public static int getIncludeSkills()
	{
		return includeSkills;
	}

	public static String getGame()
	{
		return game;
	}

	public static void setGame(String g)
	{
		game = g;
	}

	// BEGIN Game Modes Section.
	public static boolean isInGameMode(String gameMode)
	{
		if (gameMode.equals(""))
			return true; //if no game mode specified, then always assume we're OK
		else if (gameMode.equalsIgnoreCase(Constants.DEADLANDS_MODE))
			return isDeadlandsMode();
		else if (gameMode.equalsIgnoreCase(Constants.DND_MODE))
			return isDndMode();
		else if (gameMode.equalsIgnoreCase(Constants.FADINGSUNSD20_MODE))
			return isFSd20Mode();
		else if (gameMode.equalsIgnoreCase(Constants.HACKMASTER_MODE))
			return isHackMasterMode();
		else if (gameMode.equalsIgnoreCase(Constants.L5R_MODE))
			return isL5rMode();
		else if (gameMode.equalsIgnoreCase(Constants.SIDEWINDER_MODE))
			return isSidewinderMode();
		else if (gameMode.equalsIgnoreCase(Constants.SOVEREIGNSTONED20_MODE))
			return isSSd20Mode();
		else if (gameMode.equalsIgnoreCase(Constants.STARWARS_MODE))
			return isStarWarsMode();
		else if (gameMode.equalsIgnoreCase(Constants.WEIRDWARS_MODE))
			return isWeirdWarsMode();
		else if (gameMode.equalsIgnoreCase(Constants.WHEELOFTIME_MODE))
			return isWheelMode();
		else
			return false; //gameMode didn't match one of the known modes, so we must not be in that mode
	}

	public static boolean isDndMode()
	{
		if (game.equals(DND_MODE))
			return true;
		else
			return false;
	}

	public static boolean isStarWarsMode()
	{
		if (game.equals(STARWARS_MODE))
			return true;
		else
			return false;
	}

	public static boolean isWeirdWarsMode()
	{
		if (game.equals(WEIRDWARS_MODE))
			return true;
		else
			return false;
	}

	public static boolean isDeadlandsMode()
	{
		if (game.equals(DEADLANDS_MODE))
			return true;
		else
			return false;
	}

	public static boolean isL5rMode()
	{
		if (game.equals(L5R_MODE))
			return true;
		else
			return false;
	}

	public static boolean isWheelMode()
	{
		if (game.equals(WHEELOFTIME_MODE))
			return true;
		else
			return false;
	}

	public static boolean isFSd20Mode()
	{
		if (game.equals(FADINGSUNSD20_MODE))
			return true;
		else
			return false;
	}

	public static boolean isSSd20Mode()
	{
		if (game.equals(SOVEREIGNSTONED20_MODE))
			return true;
		else
			return false;
	}

	public static boolean isSidewinderMode()
	{
		if (game.equals(SIDEWINDER_MODE))
			return true;
		else
			return false;
	}

	public static boolean isHackMasterMode()
	{
		if (game.equals(HACKMASTER_MODE))
			return true;
		else
			return false;
	}

	public final static int sizeInt(final String aSize)
	{
		for (int iSize = SIZE_F; iSize <= SIZE_C; iSize++)
		{
			if (aSize.startsWith(s_SIZESHORT[iSize]))
			{
				return iSize;
			}
		}
		return SIZE_M;
	}


	public static void loadAttributeNames()
	{
		// Note that game names cannot contain spaces or non-alphabetical characters. (I.e. only 0-9a-zA-Z)
		//The purpose of this weird construct is to handle non-existing modes, which will instead be set to dnd.
		if (isStarWarsMode())
		{
			game = STARWARS_MODE;
		}
		else if (isWeirdWarsMode())
		{
			game = WEIRDWARS_MODE;
		}
		else if (isDeadlandsMode())
		{
			game = DEADLANDS_MODE;
		}
		else if (isSidewinderMode())
		{
			game = SIDEWINDER_MODE;
		}
		else if (isWheelMode())
		{
			game = WHEELOFTIME_MODE;
		}
		else if (isFSd20Mode())
		{
			game = FADINGSUNSD20_MODE;
		}
		else if (isSSd20Mode())
		{
			game = SOVEREIGNSTONED20_MODE;
		}
		else if (isHackMasterMode())
		{
			game = HACKMASTER_MODE;
		}
		else
		{
			game = DND_MODE;
		}

		try
		{
			PersistenceManager.initFile(Globals.getPcgenSystemDir() + File.separator + "gameModes" + File.separator + game + ".lst", 27/*STATNAME_TYPE*/, new ArrayList());
		}
		catch (PersistenceLayerException e)
		{
			//LATER: This is not a good way to deal with this exception.
			e.printStackTrace(System.out);
		}
		createEmptyRace();
	}

	public static void setGameMode(String gameMode)
	{
		game = gameMode;
	}

	public static String getGameMode()
	{
		return game;
	}

	// END Game Modes Section.

	public static boolean isGrimHPMode()
	{
		return grimHPMode;
	}

	public static void setGrimHPMode(boolean grimHPMode)
	{
		Globals.grimHPMode = grimHPMode;
	}

	public static boolean isGrittyACMode()
	{
		return grittyACMode;
	}

	public static void setGrittyACMode(boolean grittyACMode)
	{
		Globals.grittyACMode = grittyACMode;
	}

	public static int getInitialStatMax()
	{
		return initialStatMax;
	}

	public static void setInitialStatMax(int initialStatMax)
	{
		Globals.initialStatMax = initialStatMax;
	}

	public static boolean isLoadCampaignsAtStart()
	{
		return loadCampaignsAtStart;
	}

	public static void setLoadCampaignsAtStart(boolean loadCampaignsAtStart)
	{
		Globals.loadCampaignsAtStart = loadCampaignsAtStart;
	}

	public static boolean isLoadCampaignsWithPC()
	{
		return loadCampaignsWithPC;
	}

	public static void setLoadCampaignsWithPC(boolean loadCampaignsWithPC)
	{
		Globals.loadCampaignsWithPC = loadCampaignsWithPC;
	}

	public static boolean getSaveCustomEquipment()
	{
		return saveCustomInLst;
	}

	public static void setSaveCustomEquipment(boolean saveCustomInLst)
	{
		Globals.saveCustomInLst = saveCustomInLst;
	}

	public static int getLooknFeel()
	{
		return looknFeel;
	}

	public static void setLooknFeel(int looknFeel)
	{
		Globals.looknFeel = looknFeel;
	}

	public static int getChaTabPlacement()
	{
		return chaTabPlacement;
	}

	public static void setChaTabPlacement(int chaTabPlacement)
	{
		Globals.chaTabPlacement = chaTabPlacement;
	}

	public static String getDmNotes()
	{
		return dmNotes;
	}

	public static void setDmNotes(String dmNotes)
	{
		Globals.dmNotes = dmNotes;
	}

	public static Point getLeftUpperCorner()
	{
		return leftUpperCorner;
	}

	public static void setLeftUpperCorner(Point argLeftUpperCorner)
	{
		leftUpperCorner = argLeftUpperCorner;
	}

	public static void setCustomizerLeftUpperCorner(Point argLeftUpperCorner)
	{
		customizerLeftUpperCorner = argLeftUpperCorner;
	}

	public static Point getCustomizerLeftUpperCorner()
	{
		return customizerLeftUpperCorner;
	}

	public static void setCustomizerDimension(Dimension d)
	{
		customizerDimension = d;
	}

	public static Dimension getCustomizerDimension()
	{
		return customizerDimension;
	}

	public static void setCustomizerSplit1(int split)
	{
		customizerSplit1 = split;
	}

	public static void setCustomizerSplit2(int split)
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

	public static void setExcSkillCost(int excSkillCost)
	{
		Globals.excSkillCost = excSkillCost;
	}

	public static int getIntCrossClassSkillCost()
	{
		return intCrossClassSkillCost;
	}

	public static void setIntCrossClassSkillCost(int intCrossClassSkillCost)
	{
		Globals.intCrossClassSkillCost = intCrossClassSkillCost;
	}

	public static boolean isBoolBypassMaxSkillRank()
	{
		return boolBypassMaxSkillRank;
	}

	public static void setBoolBypassMaxSkillRank(boolean boolBypassMaxSkillRank)
	{
		Globals.boolBypassMaxSkillRank = boolBypassMaxSkillRank;
	}

	public static boolean isBoolBypassFeatPreReqs()
	{
		return boolBypassFeatPreReqs;
	}

	public static void setBoolBypassFeatPreReqs(boolean boolBypassFeatPreReqs)
	{
		Globals.boolBypassFeatPreReqs = boolBypassFeatPreReqs;
	}

	public static boolean isBoolBypassClassPreReqs()
	{
		return boolBypassClassPreReqs;
	}

	public static void setBoolBypassClassPreReqs(boolean boolBypassClassPreReqs)
	{
		Globals.boolBypassClassPreReqs = boolBypassClassPreReqs;
	}

	/**
	 * Is someone debugging PCGen?
	 *
	 * @return boolean debugging state
	 */
	public static boolean isDebugMode()
	{
		return debugMode;
	}

	/**
	 * Set debugging state: <code>true</code> is on.
	 *
	 * @param debugMode boolean debugging state
	 */
	public static void setDebugMode(boolean debugMode)
	{
		Globals.debugMode = debugMode;
	}

	/**
	 * Print information message if PCGen is debugging.
	 *
	 * @param s String information message
	 */
	public static void debugPrint(String s)
	{
		if (!Globals.debugMode)
			return;
		System.out.println(s);
	}

	/**
	 * Print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 */
	public static void debugErrorPrint(String s)
	{
		if (!Globals.debugMode)
			return;
		System.err.println(s);
	}

	/**
	 * Print error message with a stack trace if PCGen is
	 * debugging.
	 *
	 * @param s String error message
	 * @param ex Exception stack frames
	 */
	public static void debugErrorPrint(String s, Exception ex)
	{
		if (!Globals.debugMode)
			return;
		System.err.println(s);
		ex.printStackTrace(System.err);
	}

	public static int getHpPct()
	{
		return hpPct;
	}

	public static void setHpPct(int hpPct)
	{
		Globals.hpPct = hpPct;
	}

	public static boolean isSkillIncrementBefore()
	{
		return skillIncrementBefore;
	}

	public static void setSkillIncrementBefore(boolean flag)
	{
		skillIncrementBefore = flag;
	}

	public static int getPrereqFailColor()
	{
		return prereqFailColor;
	}

	public static void setPrereqFailColor(int newColor)
	{
		prereqFailColor = newColor & 0x00FFFFFF;
	}

	public static int getFeatAutoColor()
	{
		return featAutoColor;
	}

	public static void setFeatAutoColor(int newColor)
	{
		featAutoColor = newColor & 0x00FFFFFF;
	}

	public static int getFeatVirtualColor()
	{
		return featVirtualColor;
	}

	public static void setFeatVirtualColor(int newColor)
	{
		featVirtualColor = newColor & 0x00FFFFFF;
	}

	public static void setSourceDisplay(int sourceType)
	{
		sourceDisplay = sourceType;
	}

	public static int getSourceDisplay()
	{
		return sourceDisplay;
	}

	public static boolean isHpMaxAtFirstLevel()
	{
		return hpMaxAtFirstLevel;
	}

	public static void setHpMaxAtFirstLevel(boolean hpMaxAtFirstLevel)
	{
		Globals.hpMaxAtFirstLevel = hpMaxAtFirstLevel;
	}

	public static int getHpRollMethod()
	{
		return hpRollMethod;
	}

	public static void setHpRollMethod(int hpRollMethod)
	{
		Globals.hpRollMethod = hpRollMethod;
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
	 * 8: All 10's
	 */
	public static void setRollMethod(int rollMethod)
	{
		Globals.rollMethod = rollMethod;
		Globals.rollMethodExpression =
			getRollMethodExpression(rollMethod);
	}

	public static String getRollMethodExpression()
	{
		return rollMethodExpression;
	}

	public static String getRollMethodExpression(int method)
	{
		return rollMethodExpressions[method];
	}

	public static Map getRaceMap()
	{
		return raceMap;
	}

	public static ArrayList getClassList()
	{
		return classList;
	}

	public static ArrayList getTemplateList()
	{
		return templateList;
	}

	public static ArrayList getSkillList()
	{
		return skillList;
	}

	/**
	 * Gets the list of feats
	 */
	public static ArrayList getFeatList()
	{
		return featList;
	}

	/**
	 * Convenience method that returns a feat from the featList.
	 */
	public static Feat getFeatListFeat(final int featNo)
	{
		return (Feat)featList.get(featNo);
	}

	/**
	 * Convenience method that returns the toString of a feat.
	 */
	public static String getFeatListFeatString(final int featNo)
	{
		return featList.get(featNo).toString();
	}

	public static ArrayList getDomainList()
	{
		return domainList;
	}

	public static ArrayList getDeityList()
	{
		return deityList;
	}

	public static ArrayList getModifierList()
	{
		return modifierList;
	}

	public static ArrayList getPcList()
	{
		return pcList;
	}

	public static Map getSpellMap()
	{
		return spellMap;
	}

	public static ArrayList getWeaponProfList()
	{
		return weaponProfList;
	}

	public static ArrayList getSchoolsList()
	{
		return schoolsList;
	}

	public static ArrayList getColorList()
	{
		return colorList;
	}

	public static ArrayList getTraitList()
	{
		return traitList;
	}

	public static ArrayList getPhobiaList()
	{
		return phobiaList;
	}

	public static ArrayList getLocationList()
	{
		return locationList;
	}

	public static ArrayList getInterestsList()
	{
		return interestsList;
	}

	public static ArrayList getPhraseList()
	{
		return phraseList;
	}

	public static ArrayList getHairStyleList()
	{
		return hairStyleList;
	}

	public static ArrayList getSpeechList()
	{
		return speechList;
	}

	public static ArrayList getEquipmentList()
	{
		return equipmentList;
	}

	public static ArrayList getSpecialsList()
	{
		return specialsList;
	}

	public static ArrayList getCampaignList()
	{
		return campaignList;
	}

	public static DenominationList getDenominationList()
	{
		return denominationList;
	}

	public static TreeSet getWeaponTypes()
	{
		return weaponTypes;
	}

	public static TreeSet getCastingTimes()
	{
		return castingTimesSet;
	}

	public static TreeSet getDescriptors()
	{
		return descriptorSet;
	}

	public static TreeSet getEffectTypes()
	{
		return effectTypesSet;
	}

	public static TreeSet getPantheons()
	{
		return pantheonsSet;
	}

	public static TreeSet getRaceTypes()
	{
		return raceTypesSet;
	}

	public static TreeSet getRanges()
	{
		return rangesSet;
	}

	public static TreeSet getSRs()
	{
		return srSet;
	}

	public static TreeSet getSubschools()
	{
		return subschoolsSet;
	}

	public static PlayerCharacter getCurrentPC()
	{
		return currentPC;
	}

	public static void setCurrentPC(PlayerCharacter aCurrentPC)
	{
		currentPC = aCurrentPC;
	}


	/** Returns whether 'automatic' class-granted feats can be turned in for other feats
	 *  @return true if 'automatic' class-granted feats can be turned in for other feats
	 */
	public static boolean isAutoFeatsRefundable()
	{
		return autoFeatsRefundable;
	}

	/** Sets whether 'automatic' class-granted feats can be turned in for other feats
	 */
	public static void setAutoFeatsRefundable(boolean autoFeatsRefundable)
	{
		Globals.autoFeatsRefundable = autoFeatsRefundable;
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
	 * Sets the path to the character sheet templates.
	 *
	 * @param  path  the <code>File</code> representing the path
	 * @since
	 */
	public static void setTemplatePath(File path)
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
	public static void setPcgPath(File path)
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
	public static void setHtmlOutputPath(File path)
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
	public static void setBrowserPath(File path)
	{
		browserPath = path;
	}

	/**
	 * Returns the current template.
	 *
	 * @return    the <code>selectedTemplate</code> property
	 */
	public static String getSelectedTemplate()
	{
		return selectedTemplate;
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
	 * Sets the current template.
	 *
	 * @param  path  a string containing the path to the template
	 */
	public static void setSelectedTemplate(String path)
	{
		selectedTemplate = path;
	}

	/**
	 * Sets the current party template.
	 *
	 * @param  path  a string containing the path to the template
	 */
	public static void setSelectedPartyTemplate(String path)
	{
		selectedPartyTemplate = path;
	}

	/**
	 * Sets the system to use metric unit displays
	 */
	public static boolean useMetric()
	{
		// currently Star Wars requires metric measurement
		return isStarWarsMode();
	}

	/**
	 * Returns the string to use for displaying weight.
	 *
	 * Not cached to ensure correct value returned.
	 */
	public static String getWeightDisplay()
	{
		if (useMetric())
		{
			return "kgs";
		}
		return "lbs";
	}

	/**
	 * Returns the string to use for displaying height.
	 *
	 * Not cached to ensure correct value returned.
	 */
	public static String getHeightDisplay()
	{
		if (useMetric())
		{
			return "cm";
		}
		return "in";
	}

	/**
	 * Returns the string to use for displaying movement rate type.
	 *
	 * Not cached to ensure correct value returned.
	 */
	public static String getMovementDisplay()
	{
		if (useMetric())
		{
			return "meters";
		}
		return "feet";
	}

	/**
	 * Returns the string to use for displaying abbreivated movement rate type.
	 *
	 * Not cached to ensure correct value returned.
	 */
	public static String getAbbrMovementDisplay()
	{
		if (useMetric())
		{
			return "m";
		}
		return "'";
	}

	/**
	 * Returns the string to use for displaying (standard) currency.
	 *
	 * Not cached to ensure correct value returned.
	 */
	public static String getCurrencyDisplay()
	{
		if (isStarWarsMode())
		{
			return "cr";
		}
		else if (isFSd20Mode())
		{
			return "fb";
		}
		else if (isWheelMode())
		{
			return "mk";
		}
		else if (isSSd20Mode())
		{
			return "ag";
		}
		return "gp";
	}

	public static String getLongCurrencyDisplay()
	{
		if (isStarWarsMode())
		{
			return "Credits";
		}
		else if (isFSd20Mode())
		{
			return "Firebird";
		}
		else if (isWheelMode())
		{
			return "Silver Mark";
		}
		else if (isSSd20Mode())
		{
			return "Argent";
		}
		return "Gold";
	}

	/**
	 * Returns the current root frame.
	 *
	 * @return    the <code>rootFrame</code> property
	 */
	public static JFrame getRootFrame()
	{
		return rootFrame;
	}

	/**
	 * Sets the root frame. The root frame has something to do
	 * with the GUI. ???
	 *
	 * @param  frame  the <code>JFrame</code> which is to be root
	 */
	public static void setRootFrame(JFrame frame)
	{
		rootFrame = frame;
	}





	///////////////////////////////////////////////////////////////////

	// Game system accessors



	/**
	 * Searches for a loaded special ability from the <code>specialsList</code>.
	 * @param name        name of the ability
	 * @param desc        text description of the ability
	 * @param adjustment  is the level change in special ability (-1, 0 , 1)
	 * @return    the <code>SpecialAbility</code> from the list;
	 *            <code>null</code> if not found
	 */
	public static SpecialAbility getSpecialAbility(String name, String desc, int adjustment)
	{
		boolean foundIt = false;
		SpecialAbility prev = null;
		SpecialAbility sa = null;
		for (Iterator e = specialsList.iterator(); e.hasNext();)
		{
			sa = (SpecialAbility)e.next();
			if (adjustment == 1 && sa.getName().equalsIgnoreCase(name) && foundIt)
			{
				return sa;
			}
			if (sa.getName().equalsIgnoreCase(name) && (sa.getDesc().equalsIgnoreCase(desc) || desc.equals("")))
			{
				foundIt = true;
				if (desc.equals("") || adjustment == 0)
				{
					return sa;
				}
				if (adjustment == -1)
				{
					return prev;
				}
			}
			if (sa.getName().equalsIgnoreCase(name))
			{
				prev = sa;
			}
		}
		return null;
	}

	public static List getLoadStringsList()
	{
		return loadStrings;
	}

	public static double getLoadMultForSize(String aString)
	{
		if (loadStrings.size() != 0)
		{
			final String bString = (String)loadStrings.get(0);
			final StringTokenizer aTok = new StringTokenizer(bString, ",", false);
			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();
				if (cString.substring(0, 1).equals(aString))
				{
					return Double.parseDouble(cString.substring(cString.lastIndexOf('|') + 1));
				}
			}
		}
		return 1.0;
	}


	public static Campaign getCampaignNamed(String aName)
	{
		Campaign c = null;
		for (Iterator e = campaignList.iterator(); e.hasNext();)
		{
			c = (Campaign)e.next();
			if (c.getName().equalsIgnoreCase(aName))
			{
				return c;
			}
		}

		System.out.println("Could not find campaign: " + aName);
		return null;
	}

	public static Campaign getCampaignByFilename(String aName) 
	{
		return getCampaignByFilename(aName, true);
	}

	public static Campaign getCampaignByFilename(String aName, boolean complainOnError)
	{
		Campaign c = null;
		for (Iterator e = campaignList.iterator(); e.hasNext();)
		{
			c = (Campaign)e.next();
			if (c.getSourceFile().equalsIgnoreCase(aName))
			{
				return c;
			}
		}

		if (complainOnError) {
			System.out.println("Could not find campaign by filename: " + aName);
		}
		return null;
	}


	public static Race getRaceNamed(String aName)
	{
		return (Race)raceMap.get(aName);
	}


	public static Race getRaceKeyed(String aKey)
	{
		return (Race)raceMap.get(aKey);
	}


	public static PCClass getClassNamed(String aName)
	{
		PCClass p = null;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			p = (PCClass)e.next();
			if (p.getName().equalsIgnoreCase(aName))
			{
				return p;
			}
		}
		return null;
	}


	public static PCClass getClassKeyed(String aKey)
	{
		return (PCClass)searchPObjectList(classList, aKey);
	}


	public static ArrayList getWeaponProfs(String type)
	{
		ArrayList aArrayList = new ArrayList();
		ArrayList bArrayList = new ArrayList();
		String aString = null;
		StringTokenizer aTok = null;
		String typeString = null;
		String wpString = null;
		WeaponProf tempProf = null;
		for (Iterator e = currentPC.getRace().getWeaponProfs().iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			aTok = new StringTokenizer(aString, "|", false);
			typeString = aTok.nextToken();
			wpString = aTok.nextToken();
			tempProf = getWeaponProfNamed(wpString);
			if (tempProf == null)
			{
				continue;
			}
			if (typeString.equalsIgnoreCase(type))
			{
				aArrayList.add(tempProf);
			}
			else
			{
				bArrayList.add(tempProf);
			}
		}

		WeaponProf tempProf2 = null;
		for (Iterator e = weaponProfList.iterator(); e.hasNext();)
		{
			tempProf2 = (WeaponProf)e.next();
			if (bArrayList.contains(tempProf2))
			{
				continue;
			}
			if (tempProf2.getType().equalsIgnoreCase(type))
			{
				aArrayList.add(tempProf2);
			}
		}
		return aArrayList;
	}


	public static WeaponProf getWeaponProfNamed(String name)
	{
		WeaponProf aProf = null;
		for (Iterator e = weaponProfList.iterator(); e.hasNext();)
		{
			aProf = (WeaponProf)e.next();
			if (aProf.getName().equalsIgnoreCase(name))
			{
				return aProf;
			}
		}
		return null;
	}


	public static WeaponProf getWeaponProfKeyed(String aKey)
	{
		return (WeaponProf)searchPObjectList(weaponProfList, aKey);
	}

	public static Language getLanguageNamed(String name)
	{
		for (Iterator i = languageList.iterator(); i.hasNext();)
		{
			Language aLang = (Language)i.next();
			if (aLang.getName().equalsIgnoreCase(name))
				return aLang;
			aLang = null;
		}
		return null;
	}

	public static ArrayList getLanguageSetNames()
	{
		ArrayList aList = new ArrayList();
		for (Iterator i = languageList.iterator(); i.hasNext();)
		{
			Language aLang = (Language)i.next();
			aList.add(aLang.getName());
		}
		return aList;
	}

	public static List getLanguageList()
	{
		return languageList;
	}

	public static ArrayList getLanguagesFromListOfType(ArrayList langList, String aType)
	{
		ArrayList retSet = new ArrayList();
		for (Iterator i = langList.iterator(); i.hasNext();)
		{
			Language aLang = (Language)i.next();
			if (aLang != null &&
				((aType.startsWith("!") && !aLang.isType(aType)) ||
				aLang.isType(aType)))
				retSet.add(aLang);
		}
		return retSet;
	}

	public static ArrayList getLanguageNamesFromListOfType(ArrayList langList, String aType)
	{
		ArrayList retSet = new ArrayList();
		for (Iterator i = langList.iterator(); i.hasNext();)
		{
			Language aLang = (Language)i.next();
			if (aLang != null &&
				((aType.startsWith("!") && !aLang.isType(aType)) ||
				aLang.isType(aType)))
				retSet.add(aLang.getName());
		}
		return retSet;
	}

	/** Takes a SortedSet of languages and extracts the cases of ALL and TYPE=x and returns a larger SortedSet. */
	public static SortedSet extractLanguageList(SortedSet bonusLangs)
	{

		SortedSet bonusLangsb = new TreeSet();
		String aLang = null;
		Iterator e = null;

		for (e = bonusLangs.iterator(); e.hasNext();)
		{
			aLang = (String)e.next();

			if (aLang.equals("ALL"))
			{
				bonusLangsb.addAll(getLanguageSetNames());
			}
			else if (aLang.startsWith("TYPE="))
			{
				String bString = aLang.substring(5);
				ArrayList aSet = new ArrayList();
				aSet = (ArrayList)getLanguageList();
				aSet = getLanguageNamesFromListOfType(aSet, bString);
				bonusLangsb.addAll(aSet);
			}
			else
				bonusLangsb.add(aLang);
		}
		return bonusLangsb;
	}

	public static Equipment getEquipmentNamed(String name)
	{
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment)e.next();
			if (eq.getName().equalsIgnoreCase(name))
			{
				return eq;
			}
		}
		return null;
	}


	public static Equipment getEquipmentKeyed(String aKey)
	{
		return (Equipment)searchPObjectList(equipmentList, aKey);
	}


	public static Feat getFeatNamed(String name)
	{
		Feat f = null;
		final int i = name.indexOf("(");
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			f = (Feat)e.next();
			if (f.getName().equalsIgnoreCase(name))
			{
				return f;
			}
		}
		if (i>-1)
			return getFeatNamed(name.substring(0,i).trim());
		return null;
	}


	public static Feat getFeatKeyed(String aKey)
	{
		return (Feat)searchPObjectList(featList, aKey);
	}

	public static PCTemplate getTemplateNamed(String name)
	{
		PCTemplate t = null;
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			t = (PCTemplate)e.next();
			if (t.getName().equalsIgnoreCase(name))
			{
				return t;
			}
		}
		return null;
	}


	public static PCTemplate getTemplateKeyed(String aKey)
	{
		return (PCTemplate)searchPObjectList(templateList, aKey);
	}

	public static EquipmentModifier getModifierKeyed(String aKey)
	{
		return (EquipmentModifier)searchPObjectList(modifierList, aKey);
	}

	public static EquipmentModifier getModifierNamed(String aName)
	{
		for (Iterator e = modifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			if (aEqMod.getName().equals(aName))
			{
				return aEqMod;
			}
		}
		return null;
	}


	public static EquipmentModifier getQualifiedModifierNamed(String aName, ArrayList aType)
	{
		for (Iterator e = modifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			if (aEqMod.getName().equals(aName))
			{
				for (Iterator e2 = aType.iterator(); e2.hasNext();)
				{
					final String t = (String)e2.next();
					if (aEqMod.isType(t))
					{
						return aEqMod;
					}
				}
			}
		}
		return null;
	}

	public static EquipmentModifier getQualifiedModifierNamed(String aName, Equipment eq)
	{
		for (Iterator e = modifierList.iterator(); e.hasNext();)
		{
			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();
			if (aEqMod.getName().equals(aName))
			{
				for (Iterator e2 = eq.typeList().iterator(); e2.hasNext();)
				{
					final String t = (String)e2.next();
					if (aEqMod.isType(t))
					{
						//
						// Type matches, passes prereqs?
						//
						if (aEqMod.passesPreReqTests(eq))
						{
							return aEqMod;
						}
					}
				}
			}
		}
		return null;
	}


	public static Skill getSkillNamed(String name)
	{
		Skill s = null;
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			s = (Skill)e.next();
			if (s.getName().equalsIgnoreCase(name))
			{
				return s;
			}
		}
		return null;
	}


	public static Skill getSkillKeyed(String aKey)
	{
		return (Skill)searchPObjectList(skillList, aKey);
	}


	public static Deity getDeityNamed(String name)
	{
		Deity d = null;
		for (Iterator e = deityList.iterator(); e.hasNext();)
		{
			d = (Deity)e.next();
			if (d.getName().equalsIgnoreCase(name))
			{
				return d;
			}
		}
		return null;
	}


	public static Deity getDeityKeyed(String aKey)
	{
		return (Deity)searchPObjectList(deityList, aKey);
	}


	public static Domain getDomainNamed(String name)
	{
		Domain d = null;
		for (Iterator e = domainList.iterator(); e.hasNext();)
		{
			d = (Domain)e.next();
			if (d.getName().equalsIgnoreCase(name))
			{
				return d;
			}
		}
		return null;
	}


	public static Domain getDomainKeyed(String aKey)
	{
		return (Domain)searchPObjectList(domainList, aKey);
	}


	public static Spell getSpellNamed(String name)
	{
		return (Spell)spellMap.get(name);
		//I don't think this is actually used for anything worthwhile as the list it gets from is filled with KeyName...
		/*
		Spell s = null;
		for (Iterator e = spellList.iterator(); e.hasNext();)
		{
			s = (Spell)e.next();
			if (s.getName().equalsIgnoreCase(name))
			{
				return s;
			}
		}
		return null;
		*/
	}


	public static Spell getSpellKeyed(String aKey)
	{
		return (Spell)spellMap.get(aKey);
		//return (Spell)searchPObjectList(spellList, aKey);
	}


	/**
	 * Returns the number of experience points needed for level
	 *
	 * @param  level  character level to calculate experience for
	 * @return        The experience points needed
	 */
	public static int minExpForLevel(int level)
	{
		int min = 0;
		for (int i = 1; i < level; i++)
		{
			min = min + 1000 * i;
		}
		return min;
	}

	public static int getCustColumnWidth(String fromTab, int col)
	{
		int colSize = 0;
		final String cName = fromTab.concat(Integer.toString(col));
		for (int i = 0; i < custColumnWidth.size(); i++)
		{
			final StringTokenizer tTok = new StringTokenizer((String)custColumnWidth.get(i), "|", false);
			final String tabName = tTok.nextToken();
			if (tabName.equals(cName))
			{
				colSize = Integer.parseInt(tTok.nextToken());
			}
		}
		return colSize;
	}

	public static void setCustColumnWidth(String fromTab, int col, int value)
	{
		boolean found = false;
		final String cName = fromTab.concat(Integer.toString(col));
		final String addMe = cName.concat("|").concat(Integer.toString(value));
		if (custColumnWidth.isEmpty())
                        custColumnWidth.add(addMe);
		for (int i = 0; i < custColumnWidth.size(); i++)
		{
			final StringTokenizer tTok = new StringTokenizer((String)custColumnWidth.get(i), "|", false);
			final String tabName = tTok.nextToken();
			if (tabName.equals(cName))
			{
				custColumnWidth.set(i, addMe);
				found = true;
			}
		}
		if (!found)
			custColumnWidth.add(addMe);
	}

	public static void initCustColumnWidth(List l)
	{
		custColumnWidth.clear();
		custColumnWidth.addAll(l);
	}


	public static List getSizeAdjustmentList()
	{
		return sizeAdjustmentList;
	}

	public static double sizeAdjustmentMultiplier(String aSize, ArrayList typeList, String adjustmentType)
	{
		double mult = 1.0;
		for (Iterator e = sizeAdjustmentList.iterator(); e.hasNext();)
		{
			SizeAdjustment sadj = (SizeAdjustment)e.next();
			if (sadj.getName().equalsIgnoreCase(adjustmentType))
			{
				if (sadj.isType(typeList))
				{
					mult = sadj.getMultiplier(sizeInt(aSize));
					break;
				}
			}
		}
		return mult;
	}


	private static String levelForClass(Spell aSpell, String className, String realName)
	{
		StringBuffer retString = new StringBuffer();
		retString.append(className).append(",-1");
		PCClass aClass = null;
		aClass = Globals.getClassNamed(realName);
		if (aClass == null)
		{
			aClass = Globals.getClassNamed(className);
		}
		if (aClass != null)
		{
			// if the spell falls into a specialty , accept it (some spells may fall into both
			//   the specialty and prohibited list, so check specialty first)
			if (!aSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), -1) && aSpell.isProhibited(aClass.getProhibitedString()))
			{
				return retString.toString();
			}
		}
		retString = new StringBuffer();
		if (aClass != null)
		{
			className = aClass.getSpellCastingName();
		}
		StringTokenizer aTok = new StringTokenizer(aSpell.getClassLevels(), ",", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (!aString.equalsIgnoreCase(s_NONE))
			{
				if (aTok.hasMoreTokens())
				{
					try
					{
						final int spellLevel = Integer.parseInt(aTok.nextToken());
						if (aString.equals(className) /*|| domainNames.contains(aString.toUpperCase())*/ || (aClass != null && aString.equals(aClass.getSubClassName())))
						{
							if (retString.length() > 0)
							{
								retString.append(",");
							}
							retString.append(aString).append(",").append(String.valueOf(spellLevel));
						}
					}
					catch (NumberFormatException nfe)
					{
						System.out.println("Spell named \"" + aSpell.getName() +
							"\" has bad spell level info: " +
							aSpell.getClassLevels());
					}
				}
				else
				{
					System.out.println("Spell named \"" + aSpell.getName() +
						"\" has bad spell level info: " +
						aSpell.getClassLevels());
				}
			}
		}
		return retString.toString();
	}


	//
	// Return the spell's level for the selected class
	//
	public static int getSpellLevel(Spell aSpell, PCClass castingClass)
	{
		String castAs = castingClass.getCastAs();
		if (castAs.length() == 0)
		{
			castAs = castingClass.getName();
		}
		return getSpellLevel(aSpell, castingClass, castAs);
	}

	public static int getSpellLevel(Spell aSpell, PCClass castingClass, String castAs)
	{
		int spellLevel = 9999;
		if ((aSpell != null) && (castingClass != null) && (castAs != null))
		{
			final String spellLevels = levelForClass(aSpell, castAs, castingClass.getName());
			if (spellLevels != null)
			{
				final int offs = spellLevels.lastIndexOf(castAs + ",");
				if (offs >= 0)
				{
					spellLevel = Integer.parseInt(spellLevels.substring(offs + castAs.length() + 1));
				}
			}
		}
		return spellLevel;
	}

	//
	// Return the minimum level for selected class that spell can be cast
	//
	public static int minCasterLevel(Spell aSpell, PCClass castingClass, String castAs, boolean allowBonus, int levelAdjustment)
	{
		int minLevel = 9999;
		final int spellLevel = getSpellLevel(aSpell, castingClass, castAs) + levelAdjustment;
		if (spellLevel != 9999)
		{
			final List castList = castingClass.getCastList();
			for (int i = 0; i < castList.size(); i++)
			{
				final String castPerDay = castList.get(i).toString();
				if (castPerDay.equals("0"))
				{
					continue;
				}
				final StringTokenizer bTok = new StringTokenizer(castPerDay, ",", false);
				int maxCastable = -1;
				if (allowBonus)
				{
					maxCastable = bTok.countTokens() - 1;
				}
				else
				{
					int j = 0;
					while (bTok.hasMoreTokens())
					{
						try
						{
							if (Integer.parseInt(bTok.nextToken()) != 0)
							{
								maxCastable = j;
							}
						}
						catch (Exception e)
						{
						}
						j += 1;
					}
				}
				if (maxCastable >= spellLevel)
				{
					minLevel = i + 1;
					break;
				}
			}
		}
		return minLevel;
	}

	public static String getSpellPoints()
	{
		return spellPoints;
	}

	public static void setSpellPoints(String aString)
	{
		Globals.spellPoints = aString;
	}

	//
	// Return the multiplier for converting from Medium size
	// to specified size. PHB refers to armor only
	//
	public static double sizeAdjustmentCostMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "Cost");
	}

	public static double sizeAdjustmentAcModMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "AC");
	}

	public static double sizeAdjustmentWeightMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "Weight");
	}


	///////////////////////////////////////////////////////////////////

	// Methods


	public static String chooseFromList(String title, String choiceList, ArrayList selectedList, int pool)
	{
		StringTokenizer tokens = new StringTokenizer(choiceList, "|");
		if (tokens.countTokens() != 0)
		{
			ArrayList choices = new ArrayList();
			while (tokens.hasMoreTokens())
			{
				choices.add(tokens.nextToken());
			}
			return chooseFromList(title, choices, selectedList, pool);
		}
		return null;
	}

	public static String chooseFromList(String title, ArrayList choiceList, ArrayList selectedList, int pool)
	{
		Chooser c = new Chooser();
		c.setPool(pool);
		c.setPoolFlag(false);
		c.setAllowsDups(false);
		c.setTitle(title);
		c.setAvailableList(choiceList);
		if (selectedList != null)
		{
			c.setSelectedList(selectedList);
		}
		c.show();

		List justSelectedList = c.getSelectedList();
		if (justSelectedList.size() != 0)
		{
			return (String)justSelectedList.get(0);
		}
		return null;
	}


	/**
	 * Sorts chooser lists using the appropriate method, based on the type of the first item in either list.
	 * Not pretty, but it works.
	 */

	public static void sortChooserLists(ArrayList availableList, ArrayList selectedList)
	{
		boolean stringsInList;
		if (availableList.size() > 0)
		{
			stringsInList = availableList.get(0) instanceof String;
		}
		else if (selectedList.size() > 0)
		{
			stringsInList = availableList.get(0) instanceof String;
		}
		else
		{
			stringsInList = false;
		}
		if (stringsInList)
		{
			Collections.sort(availableList);
			Collections.sort(selectedList);
		}
		else
		{
			Globals.sortPObjectList(availableList);
			Globals.sortPObjectList(selectedList);
		}
	}


	/**
	 * Clears all lists of game data.
	 */
	public static void emptyLists()
	{
		raceMap.clear();
		createEmptyRace();
		classList.clear();
		skillList.clear();
		featList.clear();
		equipmentList.clear();
		weaponProfList.clear();
		templateList.clear();
		deityList.clear();
		domainList.clear();
		modifierList.clear();
		languageList.clear();
		spellMap.clear();
		Equipment.clearEquipmentTypes();
	}

	public static List getXPList()
	{
		return XPList;
	}

	public static int xPLevelOffset(int level)
	{
		int offset = 0;
		final StringTokenizer aTok = new StringTokenizer((String)XPList.get(level), ",", false);
		if (aTok.hasMoreTokens())
			offset = Integer.parseInt(aTok.nextToken().trim());
		return offset;
	}

	public static int xPLevelValue(int level, int cr)
	{
		int i = 0;
		int xp = 666;
		final StringTokenizer aTok = new StringTokenizer((String)XPList.get(level), ",", false);
		while (aTok.hasMoreTokens() && i++ < cr + 1)
		{
			xp = Integer.parseInt(aTok.nextToken().trim());
		}
		return xp;
	}

	public static ArrayList getBonusStackList()
	{
		return bonusStackList;
	}

	/**
	 * Size is taken into account for the currentPC.
	 */
	public static Float maxLoadForStrength(int strength)
	{
		double x = 0.0;
		double dbl = 0.0;
		int y = strength;
		int loadStringSize = loadStrings.size();
		if (strength >= loadStringSize - 2) //ok
		{
			final String bString = (String)loadStrings.get(loadStringSize - 1); //ok
			dbl = Double.parseDouble(bString.substring(bString.lastIndexOf('\t') + 1));
			for (y = strength; y >= loadStringSize - 2; y -= 10)
			{
				x += 1.0;
			}
		}
		final String aString = (String)loadStrings.get(y);
		Float aFloat = new Float(aString.substring(aString.lastIndexOf('\t') + 1));
		if (x > 0)
		{
			aFloat = new Float(aFloat.doubleValue() * Math.pow(dbl, x));
		}
		return new Float(aFloat.doubleValue() * getLoadMultForSize(currentPC.getSize()));
	}


	/**
	 * @return 0 = light, 1 = medium, 2 = heavy, 3 = overload
	 */
	public static int loadTypeForStrength(int strength, Float weight)
	{
		double dbl = weight.doubleValue() / maxLoadForStrength(strength).doubleValue();
		if (dbl <= .333)
		{
			return LIGHT_LOAD;
		}
		if (dbl <= .666)
		{
			return MEDIUM_LOAD;
		}
		if (dbl <= 1.0)
		{
			return HEAVY_LOAD;
		}
		return OVER_LOAD;
	}

	/**
	 * Works for dnd according to the method noted in the faq. (NOTE: The table in the dnd faq is wrong for speeds 80 and
	 * 90. Calculate it yourself to see...)
	 * Not as sure it works for all other d20 games.
	 * @param load (0 = light, 1 = medium, 2 = heavy, 3 = overload)
	 * @param  unencumberedMove the unencumbered move value
	 * @return encumbered move as an integer
	 */
	public static int calcEncumberedMove(int load, int unencumberedMove)
	{
		return calcEncumberedMove(load, unencumberedMove, Globals.isApplyLoadPenaltyToACandSkills());
	}

	public static int calcEncumberedMove(int load, int unencumberedMove, boolean checkLoad)
	{
		int encumberedMove = 0;
		if (checkLoad)
		{
			switch (load)
			{
				case LIGHT_LOAD:
					encumberedMove = unencumberedMove;
					break;
				case MEDIUM_LOAD:
					/* deliberately no break */
				case HEAVY_LOAD:
					if (unencumberedMove == 5)
					{
						encumberedMove = 5;
					}
					else if (unencumberedMove == 10)
					{
						encumberedMove = 5;
					}
					else
					{
						encumberedMove = (int)(unencumberedMove / 15) * 10 + unencumberedMove % 15;
					}
					break;
				case OVER_LOAD:
					encumberedMove = 0;
					break;
				default:
					debugErrorPrint("The load " + load + " is not possible.");
					encumberedMove = 0;
					break;
			}
		}
		else
		{
			encumberedMove = unencumberedMove;
		}
		debugErrorPrint("calcEncumberedMove: " + encumberedMove);
		return encumberedMove;
	}


	public static PObject binarySearchPObject(ArrayList aList, String keyName)
	{
		final Object[] pobjArray = (Object[])aList.toArray();
		int lower = 0;
		int upper = pobjArray.length;

		// always one past last possible match

		while (lower < upper)
		{
			final int mid = (lower + upper) / 2;
			PObject obj = (PObject)pobjArray[mid];
			int cmp = keyName.compareTo(obj.getKeyName());
			if (cmp == 0)
			{
				return obj;
			}
			else if (cmp > 0)
			{
				lower = mid + 1;
			}
			else
			{
				upper = mid;
			}
		}
		return null;
	}

	/**
	 *  This method will find PObject by key name in a sorted arraylist of PObjects.
	 *  The arraylist must be sorted by key name.
	 *
	 * @param  aList    an arraylist of PObject objects.
	 * @param  keyName  the keyname being sought.
	 * @return          a <code>null</code> value indicates the search failed.
	 */
	public static PObject searchPObjectList(ArrayList aList, String keyName)
	{
		if (d_sorted)
		{
			return binarySearchPObject(aList, keyName);
		}
		else
		{
			final Object[] pobjArray = (Object[])aList.toArray();
			final int upper = pobjArray.length;

			// not presently sorted

			PObject obj = null;
			for (int i = upper - 1; i >= 0; --i)
			{
				obj = (PObject)pobjArray[i];
				if (keyName.equals(obj.getKeyName())) return obj;
			}
		}
		return null;
	}


	public static List sortPObjectList(List aList)
	{
		Collections.sort(aList, new PObjectComp());
		return aList;
	}

	private static class PObjectComp implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return ((PObject)o1).getKeyName().compareTo(((PObject)o2).getKeyName());
		}
	}

	public static List sortPObjectListByName(List aList)
	{
		Collections.sort(aList, new PObjectCompByName());
		return aList;
	}

	private static class PObjectCompByName implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return ((PObject)o1).getName().compareTo(((PObject)o2).getName());
		}
	}


	public static boolean displayListsHappy()
	{
		if (raceMap.size() == 0 || classList.size() == 0 || skillList.size() == 0 || featList.size() == 0 || equipmentList.size() == 0 || weaponProfList.size() == 0)
		{
			return false;
		}
		return true;
	}


	private static void createEmptyRace()
	{
		s_EMPTYRACE = new Race();
		s_EMPTYRACE.setName(s_NONESELECTED);
		getRaceMap().put(s_NONESELECTED, s_EMPTYRACE);
	}

	public static boolean isAutoGeneration()
	{
		return bAutoGeneration;
	}

	public static void setAutoGeneration(boolean auto)
	{
		bAutoGeneration = auto;
	}

	public static boolean addEquipment(Equipment aEq)
	{
		if (getEquipmentKeyed(aEq.getKeyName()) != null)
		{
			return false;
		}

		if (!aEq.isType(Constants.s_CUSTOM))
		{
			aEq.rawTypeList().add(Constants.s_CUSTOM);
		}
		//
		// Make sure all the equipment types are present in the sorted list
		//
		aEq.getEquipmentTypes().addAll(aEq.typeList());
		equipmentList.add(aEq);
		if (!bAutoGeneration)
		{
			sortPObjectList(equipmentList);
		}
		return true;
	}

	public static void setSorted(boolean sorted)
	{
		d_sorted = sorted;
	}

	public static void sortCampaigns()
	{
		raceMap = new TreeMap(raceMap);
		sortPObjectList(classList);
		sortPObjectList(skillList);
		sortPObjectList(featList);
		sortPObjectList(deityList);
		sortPObjectList(domainList);
		spellMap = new TreeMap(spellMap);
		sortPObjectList(equipmentList);
		sortPObjectList(weaponProfList);
		sortPObjectList(templateList);
		sortPObjectList(modifierList);
		d_sorted = true;
	}


	private static Equipment createItem(Equipment eq, int iSize)
	{
		return createItem(eq, null, iSize);
	}

	private static Equipment createItem(Equipment eq, EquipmentModifier eqMod)
	{
		return createItem(eq, eqMod, -1);
	}

	private static Equipment createItem(Equipment eq, EquipmentModifier eqMod, int iSize)
	{
		if (eq != null)
		{
			try
			{
				//
				// Armor without an armor bonus is an exception
				//
				if (!eq.getModifiersAllowed() || (eq.isArmor() && (eq.getAcMod().intValue() == 0) && ((eqMod != null) && !eqMod.getName().equalsIgnoreCase("MASTERWORK"))))
				{
					return null;
				}

				eq = (Equipment)eq.clone();
				if (eq == null)
				{
					System.out.println("could not clone item");
					return null;
				}

				if (eqMod != null)
				{
					eq.addEqModifier(eqMod, true);
					if (eq.isWeapon() && eq.isDouble())
					{
						eq.addEqModifier(eqMod, false);
					}
				}

				if ((iSize >= SIZE_F) && (iSize <= SIZE_C))
				{
					eq.resizeItem(s_SIZESHORT[iSize]);
				}

				//
				// Change the names, to protect the innocent
				//
				final String sName = eq.nameItemFromModifiers();
				final Equipment eqExists = getEquipmentKeyed(sName);
				if (eqExists != null)
				{
					return eqExists;
				}
				String newType;
				if (bAutoGeneration)
				{
					newType = "AUTO_GEN";
				}
				else
				{
					newType = Constants.s_CUSTOM;
				}
				if (!eq.isType(newType))
				{
					eq.rawTypeList().add(newType);
				}
				//
				// Make sure all the equipment types are present in the sorted list
				//
				eq.getEquipmentTypes().addAll(eq.typeList());

				equipmentList.add(eq);
				return eq;
			}
			catch (Exception exception)
			{
				System.out.println("createItem: exception: " + eq.getName());
			}
		}
		return null;
	}

	//
	// Generate masterwork, +1 to +5 armor, shields, weapons
	//
	public static void autoGenerateEquipment()
	{
		bAutoGeneration = true;
		if (autogenRacial)
		{
			//
			// Go through all loaded races and flag whether or not to make equipment sized for them
			//
			int[] gensizes = new int[9];
			ArrayList races = new ArrayList(getRaceMap().values());
			for (Iterator e = races.iterator(); e.hasNext();)
			{
				final Race race = (Race)e.next();
				final int iSize = sizeInt(race.getSize());
				int flag = 1;
				//if (!race.isType("HUMANOID"))
				//{
				//	System.out.println("Non-humanoid race: " + race.getName());
				//	flag = 2;
				//}
				gensizes[iSize] |= flag;
			}


			for (int i = equipmentList.size() - 1; i >= 0; i--)
			{
				final Equipment eq = (Equipment)equipmentList.get(i);
				//
				// Only apply to Armor, Shield and resizable items
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isArmor() && !eq.isShield() && !eq.isType("RESIZABLE")))
				{
					continue;
				}
				for (int j = SIZE_F; j <= SIZE_C; j++)
				{
					if (j == SIZE_M)
					{
						continue;
					}


					if ((gensizes[j] & 0x01) != 0)
					{
						createItem(eq, j);
					}
					//if ((gensizes[j] & 0x02) != 0)
					//{
					//createItem(eq, j);
					//}
				}
			}
		}

		if (autogenMasterwork)
		{
			for (int i = equipmentList.size() - 1; i >= 0; i--)
			{
				Equipment eq = (Equipment)equipmentList.get(i);
				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
				{
					continue;
				}

				final EquipmentModifier eqMasterwork = getQualifiedModifierNamed("Masterwork", eq);
				createItem(eq, eqMasterwork);
			}
		}

		if (autogenMagic)
		{
			for (int iPlus = 1; iPlus <= 5; iPlus++)
			{
				final String aBonus = Delta.toString(iPlus);

				for (int i = equipmentList.size() - 1; i >= 0; i--)
				{
					Equipment eq = (Equipment)equipmentList.get(i);
					//
					// Only apply to non-magical Armor, Shield and Weapon
					//
					if (eq.isMagic() || !eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
					{
						continue;
					}

					final EquipmentModifier eqMod = getQualifiedModifierNamed(aBonus, eq);
					createItem(eq, eqMod);
				}
			}
		}

		if (autogenExoticMaterial)
		{
			for (int i = equipmentList.size() - 1; i >= 0; i--)
			{
				Equipment eq = (Equipment)equipmentList.get(i);
				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
				{
					continue;
				}

				final EquipmentModifier eqDarkwood = getQualifiedModifierNamed("Darkwood", eq);
				final EquipmentModifier eqAdamantine = getQualifiedModifierNamed("Adamantine", eq);
				final EquipmentModifier eqMithral = getQualifiedModifierNamed("Mithral", eq);

				createItem(eq, eqDarkwood);
				createItem(eq, eqAdamantine);
				createItem(eq, eqMithral);
			}
		}
		bAutoGeneration = false;
		sortPObjectList(equipmentList);   // Sort the equipment list
	}

	///////////////////////////////////////////////////////////////////

	// Options



	/**
	 * Opens the filter.ini file for reading
	 *
	 * <br>author: Thomas Behr 10-03-02
	 */
	public static void readFilterSettings()
	{
		FileInputStream in = null;
		try
		{
			in = new FileInputStream(System.getProperty("user.dir") + File.separator + "filter.ini");
			filterSettings.load(in);
		}
		catch (IOException e)
		{
			//Not an error, it may well be that this file does not exist yet.
			debugPrint("No filter settings file found, will create one when exiting.");
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
			in = new FileInputStream(System.getProperty("user.dir") + File.separator + "options.ini");
			options.load(in);
		}
		catch (IOException e)
		{
			//Not an error, it may well be that this file does not exist yet.

			if (debugMode)
				System.out.println("No options file found, will create one when exiting.");
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
	public static boolean getPCGenOption(String optionName, boolean defaultValue)
	{
		final String option = getPCGenOption(optionName, defaultValue ? "true" : "false");
		return option.equalsIgnoreCase("true");
	}

	public static int getPCGenOption(String optionName, int defaultValue)
	{
		return Integer.decode(getPCGenOption(optionName, String.valueOf(defaultValue))).intValue();
	}

	public static Double getPCGenOption(String optionName, double defaultValue)
	{
		return new Double(getPCGenOption(optionName, Double.toString(defaultValue)));
	}

	public static String getPCGenOption(String optionName, String defaultValue)
	{
		//String x = options.getProperty("pcgen.options." + optionName, "getyourcodemonkeysnackshere!");
		//if (x.equals("getyourcodemonkeysnackshere!"))
		//{
		//GuiFacade.showMessageDialog(null, "Could not find pcgen.options." + optionName, s_APPNAME, JOptionPane.ERROR_MESSAGE);
		//}
		//x=options.getProperty("pcgen.options." + optionName, defaultValue);
		//GuiFacade.showMessageDialog(null, "pcgen.options." + optionName + "=" + x, s_APPNAME, JOptionPane.ERROR_MESSAGE);
		return options.getProperty("pcgen.options." + optionName, defaultValue);
	}

	static Dimension getOptionsFromProperties()
	{
		Dimension d = new Dimension(0, 0);
		pcgPath = new File(options.getProperty("pcgen.files.characters", defaultPcgPath));
		templatePath = new File(options.getProperty("pcgen.files.templates", defaultPath + File.separator + "templates"));
		htmlOutputPath = new File(options.getProperty("pcgen.files.htmlOutput", defaultPcgPath));
		String tempBrowserPath = getPCGenOption("browserPath", "");
		if (!tempBrowserPath.equals(""))
		{
			browserPath = new File(tempBrowserPath);
		}
		else
		{
			browserPath = null;
		}
		selectedTemplate = options.getProperty("pcgen.files.selectedTemplate", templatePath.getAbsolutePath() + File.separator + "csheet.htm");
		selectedPartyTemplate = options.getProperty("pcgen.files.selectedPartyTemplate", templatePath.getAbsolutePath() + File.separator + "psheet.htm");
		PersistenceManager.setChosenCampaignSourcefiles(Utility.split(options.getProperty("pcgen.files.chosenCampaignSourcefiles", ""), ','));
		pccFilesLocation = new File(getPCGenOption("pccFilesLocation", System.getProperty("user.dir") + File.separator + "data"));

		pcgenSystemDir = new File(options.getProperty("pcgen.files.pcgenSystemDir", System.getProperty("user.dir") + File.separator + "system"));

		dmNotes = getPCGenOption("dmnotes", "");
		purchaseStatMode = getPCGenOption("purchaseStatMode", false);
		useMonsterDefault = getPCGenOption("useMonsterDefault", false);
		ignoreLevelCap = getPCGenOption("ignoreLevelCap", false);

		game = getPCGenOption("game", DND_MODE);

		loadAttributeNames();

		unlimitedStatPool = getPCGenOption("unlimitedStatPool", false);
		ignoreEquipmentCost = getPCGenOption("ignoreEquipmentCost", false);
		grimHPMode = getPCGenOption("grimHPMode", false);
		grittyACMode = getPCGenOption("grittyACMode", false);
		includeSkills = getPCGenOption("includeSkills", 0);
		excSkillCost = getPCGenOption("excSkillCost", 0);
		intCrossClassSkillCost = getPCGenOption("intCrossClassSkillCost", 2);
		initialStatMin = getPCGenOption("initialStatMin", 3);
		initialStatMax = getPCGenOption("initialStatMax", 18);
		loadCampaignsAtStart = getPCGenOption("loadCampaignsAtStart", false);
		loadCampaignsWithPC = getPCGenOption("loadCampaignsWithPC", true);
		saveCustomInLst = getPCGenOption("saveCustomInLst", false);
		tabPlacement = getPCGenOption("tabPlacement", 2);
		chaTabPlacement = getPCGenOption("chaTabPlacement", 0);
		rollMethod = getPCGenOption("rollMethod", 1);
		rollMethodExpression = getPCGenOption("rollMethodExpression", "roll(4,6,[2,3,4])");
		looknFeel = getPCGenOption("looknFeel", 0);
		skinLFThemePack = getPCGenOption("skinLFThemePack", "");
		setBoolBypassFeatPreReqs(getPCGenOption("boolBypassFeatPreReqs", false));
		setBoolBypassClassPreReqs(getPCGenOption("boolBypassClassPreReqs", false));

		autoFeatsRefundable = getPCGenOption("autoFeatsRefundable", false);
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			statCost[i] = getPCGenOption("statCost." + statValue, statCost[i]);
		}
		hpMaxAtFirstLevel = getPCGenOption("hpMaxAtFirstLevel", true);
		maxStartingGold = getPCGenOption("maxStartingGold", false);
		hpRollMethod = getPCGenOption("hpRollMethod", s_HP_STANDARD);
		skillIncrementBefore = getPCGenOption("skillIncrementBefore", true);
		toolTipTextShown = getPCGenOption("toolTipTextShown", true);
		previewTabShown = getPCGenOption("previewTabShown", true);
		applyWeightPenaltyToSkills = getPCGenOption("applyWeightPenaltyToSkills", true);
		applyLoadPenaltyToACandSkills = getPCGenOption("applyLoadPenaltyToACandSkills", true);

		Globals.setLeftUpperCorner(new Point(getPCGenOption("windowLeftUpperCorner.X", -1.0).intValue(), getPCGenOption("windowLeftUpperCorner.Y", -1.0).intValue()));
		Double dw = getPCGenOption("windowWidth", 0.0);
		Double dh = getPCGenOption("windowHeight", 0.0);
		if (dw.doubleValue() != 0.0 && dh.doubleValue() != 0.0)
		{
			int width = Integer.parseInt(dw.toString().substring(0, Math.min(dw.toString().length(), dw.toString().lastIndexOf("."))));
			int height = Integer.parseInt(dh.toString().substring(0, Math.min(dh.toString().length(), dh.toString().lastIndexOf("."))));
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

		autogenMasterwork = getPCGenOption("autoGenerateMasterwork", false);
		autogenMagic = getPCGenOption("autoGenerateMagic", false);
		autogenRacial = getPCGenOption("autoGenerateRacial", false);
		autogenExoticMaterial = getPCGenOption("autoGenerateExoticMaterial", false);

		isROG = getPCGenOption("isROG", false);
		wantToLoadMasterworkAndMagic = getPCGenOption("loadMasterworkAndMagicFromLst", false);

		inventoryTab_IgnoreCost = getPCGenOption("InventoryTab.ignoreCost", false);
		inventoryTab_AvailableListMode = getPCGenOption("InventoryTab.availableListMode", pcgen.gui.InfoInventory.VIEW_TYPE_SUBTYPE_NAME);
		inventoryTab_SelectedListMode = getPCGenOption("InventoryTab.selectedListMode", pcgen.gui.InfoInventory.VIEW_NAME);
		skillsTab_AvailableListMode = getPCGenOption("SkillsTab.availableListMode", pcgen.gui.InfoSkills.VIEW_TYPE_NAME);
		skillsTab_SelectedListMode = getPCGenOption("SkillsTab.selectedListMode", pcgen.gui.InfoSkills.VIEW_NAME);
		spellsTab_AvailableListMode = getPCGenOption("SpellsTab.availableListMode", pcgen.gui.InfoSpells.VIEW_CLASS);
		spellsTab_SelectedListMode = getPCGenOption("SpellsTab.selectedListMode", pcgen.gui.InfoSpells.VIEW_CLASS);
		featTab_AvailableListMode = getPCGenOption("FeatTab.availableListMode", pcgen.gui.InfoFeats.VIEW_TYPENAME);
		featTab_SelectedListMode = getPCGenOption("FeatTab.selectedListMode", pcgen.gui.InfoFeats.VIEW_NAMEONLY);

		String userDir = getPCGenOption("userdir", "");
		if (userDir != null && userDir.length() > 0)
		{
			System.setProperty("user.dir", userDir);
		}

		allStatsValue = getPCGenOption("allStatsValue", 10);

		treatInHandAsEquippedForAttacks = getPCGenOption("treatInHandAsEquippedForAttacks", false);
		showToolBar = getPCGenOption("showToolBar", true);
		initCustColumnWidth(Utility.split(options.getProperty("pcgen.options.custColumnWidth", ""), ','));


		return d;
	}

	/**
	 * Opens the filter.ini file for writing
	 *
	 * <br>author: Thomas Behr 10-03-02
	 */
	public static void writeFilterSettings()
	{
		String header = "# Emacs, this is -*- java-properties-generic -*- mode.\r\n" +
			"#\r\n" +
			"# filter.ini -- filters set in pcgen\r\n" +
			"# Do not edit this file manually.\r\n";

		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(System.getProperty("user.dir") + File.separator + "filter.ini");
			filterSettings.store(out, header);
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
		for (Iterator it = options.keySet().iterator(); it.hasNext();)
		{
			if (((String)it.next()).startsWith("pcgen.filters."))
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

		String header = "# Emacs, this is -*- java-properties-generic -*- mode.\r\n" + "#\r\n" + "# options.ini -- options set in pcgen\r\n" + "# Do not edit this file manually.\r\n";

		FileOutputStream out = null;
		try
		{
			setOptionsProperties();
			out = new FileOutputStream(System.getProperty("user.dir") + File.separator + "options.ini");
			options.store(out, header);
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
	public static void storeFilterSettings(Filterable filterable)
	{
		String name = filterable.getName();

		if (name == null)
		{
			return;
		}

		filterSettings.setProperty("pcgen.filters." + name + ".mode", Integer.toString(filterable.getFilterMode()));

		filterSettings.setProperty("pcgen.filters." + name + ".available",
			FilterFactory.filterListToString(filterable.getAvailableFilters()));

		filterSettings.setProperty("pcgen.filters." + name + ".selected",
			FilterFactory.filterListToString(filterable.getSelectedFilters()));

		filterSettings.setProperty("pcgen.filters." + name + ".removed",
			FilterFactory.filterListToString(filterable.getRemovedFilters()));
	}

	/**
	 * retrieve filter settings
	 *
	 * <br>author: Thomas Behr 19-02-02
	 *
	 * @param optionName   the name of the property to retrieve
	 */
	public static String retrieveFilterSettings(String optionName)
	{
		return filterSettings.getProperty("pcgen.filters." + optionName,
			options.getProperty("pcgen.filters." + optionName, ""));
	}

	public static void writeCustomItems()
	{
		//
		// Don't trash the file if user exits before loading custom items
		//
		if (!PersistenceManager.isCustomItemsLoaded() || !saveCustomInLst)
		{
			return;
		}

		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(pccFilesLocation.getAbsolutePath() + File.separator + "customsources" + File.separator + "customEquipment.lst"));
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();
			for (Iterator itemIterator = getEquipmentList().iterator(); itemIterator.hasNext();)
			{
				final Equipment aEq = (Equipment)itemIterator.next();
				if (aEq.isType(Constants.s_CUSTOM) && !aEq.isType("AUTO_GEN"))
				{
					aEq.save(bw);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (bw != null)
				{
					bw.close();
				}
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
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
	public static void setPCGenOption(String optionName, boolean optionValue)
	{
		setPCGenOption(optionName, optionValue ? "true" : "false");
	}

	public static void setPCGenOption(String optionName, int optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setPCGenOption(String optionName, double optionValue)
	{
		setPCGenOption(optionName, String.valueOf(optionValue));
	}

	public static void setPCGenOption(String optionName, String optionValue)
	{
		options.setProperty("pcgen.options." + optionName, optionValue);
	}


	static void setOptionsProperties()
	{
		options.setProperty("pcgen.files.characters", pcgPath.getAbsolutePath());
		options.setProperty("pcgen.files.templates", templatePath.getAbsolutePath());
		options.setProperty("pcgen.files.htmlOutput", htmlOutputPath.getAbsolutePath());
		options.setProperty("pcgen.files.selectedTemplate", selectedTemplate);
		options.setProperty("pcgen.files.selectedPartyTemplate", selectedPartyTemplate);
		options.setProperty("pcgen.files.chosenCampaignSourcefiles", Utility.unSplit(PersistenceManager.getChosenCampaignSourcefiles(), ','));
		options.setProperty("pcgen.files.pcgenSystemDir", pcgenSystemDir.getAbsolutePath());

		if (browserPath != null)
		{
			setPCGenOption("browserPath", browserPath.getAbsolutePath());
		}
		else
		{
			setPCGenOption("browserPath", "");
		}

		setPCGenOption("purchaseStatMode", purchaseStatMode);
		setPCGenOption("useMonsterDefault", useMonsterDefault);
		setPCGenOption("ignoreLevelCap", ignoreLevelCap);


		setPCGenOption("game", game);


		setPCGenOption("unlimitedStatPool", unlimitedStatPool);
		setPCGenOption("ignoreEquipmentCost", ignoreEquipmentCost);
		setPCGenOption("grimHPMode", grimHPMode);
		setPCGenOption("grittyACMode", grittyACMode);
		setPCGenOption("includeSkills", includeSkills);
		setPCGenOption("excSkillCost", excSkillCost);
		setPCGenOption("intCrossClassSkillCost", intCrossClassSkillCost);
		setPCGenOption("initialStatMin", initialStatMin);
		setPCGenOption("initialStatMax", initialStatMax);
		setPCGenOption("loadCampaignsAtStart", loadCampaignsAtStart);
		setPCGenOption("loadCampaignsWithPC", loadCampaignsWithPC);
		setPCGenOption("saveCustomInLst", saveCustomInLst);
		setPCGenOption("chaTabPlacement", chaTabPlacement);
		setPCGenOption("tabPlacement", tabPlacement);
		setPCGenOption("looknFeel", looknFeel);
		setPCGenOption("skinLFThemePack", skinLFThemePack);
		setPCGenOption("autoFeatsRefundable", autoFeatsRefundable);
		setPCGenOption("pccFilesLocation", pccFilesLocation.getAbsolutePath());
		setPCGenOption("dmnotes", dmNotes);
		setPCGenOption("boolBypassFeatPreReqs", boolBypassFeatPreReqs);
		setPCGenOption("boolBypassClassPreReqs", boolBypassClassPreReqs);

		setPCGenOption("rollMethod", rollMethod);
		setPCGenOption("rollMethodExpression", rollMethodExpression);
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			setPCGenOption("statCost." + statValue, statCost[i]);
		}
		setPCGenOption("hpMaxAtFirstLevel", hpMaxAtFirstLevel);
		setPCGenOption("maxStartingGold", maxStartingGold);
		setPCGenOption("hpRollMethod", hpRollMethod);
		setPCGenOption("skillIncrementBefore", skillIncrementBefore);
		setPCGenOption("prereqFailColor", "0x" + Integer.toHexString(prereqFailColor));
		setPCGenOption("featAutoColor", "0x" + Integer.toHexString(featAutoColor));
		setPCGenOption("featVirtualColor", "0x" + Integer.toHexString(featVirtualColor));

		setPCGenOption("autoGenerateMasterwork", autogenMasterwork);
		setPCGenOption("autoGenerateMagic", autogenMagic);
		setPCGenOption("autoGenerateRacial", autogenRacial);
		setPCGenOption("autoGenerateExoticMaterial", autogenExoticMaterial);
		setPCGenOption("loadMasterworkAndMagicFromLst", wantToLoadMasterworkAndMagic);

		setPCGenOption("toolTipTextShown", toolTipTextShown);
		setPCGenOption("previewTabShown", previewTabShown);
		setPCGenOption("applyWeightPenaltyToSkills", applyWeightPenaltyToSkills);
		setPCGenOption("applyLoadPenaltyToACandSkills", applyLoadPenaltyToACandSkills);

		if (Globals.getLeftUpperCorner() != null)
		{
			setPCGenOption("windowLeftUpperCorner.X", Globals.getLeftUpperCorner().getX());
			setPCGenOption("windowLeftUpperCorner.Y", Globals.getLeftUpperCorner().getY());
		}
		setPCGenOption("windowWidth", rootFrame.getSize().getWidth());
		setPCGenOption("windowHeight", rootFrame.getSize().getHeight());

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
		setPCGenOption("allStatsValue", allStatsValue);

		final String paperName = getPaperInfo(PAPERINFO_NAME);
		if (paperName != null)
		{
			setPCGenOption("paperName", paperName);
		}
		setPCGenOption("treatInHandAsEquippedForAttacks", treatInHandAsEquippedForAttacks);
		setPCGenOption("showToolBar", showToolBar);

		setPCGenOption("InventoryTab.ignoreCost", inventoryTab_IgnoreCost);
		setPCGenOption("InventoryTab.availableListMode", inventoryTab_AvailableListMode);
		setPCGenOption("InventoryTab.selectedListMode", inventoryTab_SelectedListMode);
		setPCGenOption("SkillsTab.availableListMode", skillsTab_AvailableListMode);
		setPCGenOption("SkillsTab.selectedListMode", skillsTab_SelectedListMode);
		setPCGenOption("SpellsTab.availableListMode", spellsTab_AvailableListMode);
		setPCGenOption("SpellsTab.selectedListMode", spellsTab_SelectedListMode);
		setPCGenOption("FeatTab.availableListMode", featTab_AvailableListMode);
		setPCGenOption("FeatTab.selectedListMode", featTab_SelectedListMode);
		options.setProperty("pcgen.options.custColumnWidth", Utility.unSplit(custColumnWidth, ','));
	}

	private static Equipment findEquipment(String aName, ArrayList preNameList, ArrayList postNameList, ArrayList sizList, String omitString)
	{
		StringBuffer newName = new StringBuffer(80);
		newName.append(" (");
		if (preNameList != null)
		{
			for (Iterator e = preNameList.iterator(); e.hasNext();)
			{
				final String namePart = (String)e.next();
				if ((omitString.length() != 0) && namePart.equals(omitString))
				{
					continue;
				}
				if (newName.length() > 2)
				{
					newName.append('/');
				}
				newName.append(namePart);
			}
		}

		if (sizList != null)
		{
			//
			// Append 1st size if multiple sizes
			//
			if (sizList.size() > 1)
			{
				newName.append((String)sizList.get(0));
			}
		}

		if (postNameList != null)
		{
			for (Iterator e = postNameList.iterator(); e.hasNext();)
			{
				final String namePart = (String)e.next();
				if ((omitString.length() != 0) && namePart.equals(omitString))
				{
					continue;
				}
				if (newName.length() > 2)
				{
					newName.append('/');
				}
				newName.append(namePart);
			}
		}
		if (newName.length() == 2)
		{
			newName.setLength(0);
		}
		else
		{
			newName.append(')');
		}
		final Equipment eq = getEquipmentKeyed(aName + newName.toString());
		//if (eq == null)
		//{
		//	System.out.println("not found: " + aName + newName.toString());
		//}
		return eq;
	}

	public static Equipment getEquipmentFromName(String baseName)
	{
		ArrayList modList = new ArrayList();
		ArrayList namList = new ArrayList();
		ArrayList sizList = new ArrayList();
		Equipment eq = null;
		String aName = baseName;
		int i = aName.indexOf('(');

		//
		// Remove all modifiers from item name and split into "size" and "non-size" lists
		//
		if (i >= 0)
		{
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i + 1), "/)", false);
			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();
				int iSize;
				for (iSize = SIZE_F; iSize <= SIZE_C; iSize++)
				{
					if (cString.equalsIgnoreCase(s_SIZELONG[iSize]))
					{
						break;
					}
				}
				if (iSize <= SIZE_C)
				{
					sizList.add(cString);
				}
				else
				{
					if (cString.equalsIgnoreCase("Mighty Composite"))
					{
						modList.add("Mighty");
						modList.add("Composite");
					}
					else
					{
						modList.add(cString);
					}
				}
			}
			aName = aName.substring(0, i).trim();
		}

		//
		// Separate the "non-size" descriptors int 2 ArrayLists. One containing those descriptors
		// whose names match a modifier name, and the other containing those descriptors which are not
		// possibly modifiers (because they're not in the modifier list).
		//
		if (i >= 0)
		{
			for (i = modList.size() - 1; i >= 0; i--)
			{
				final String namePart = (String)modList.get(i);
				if (getModifierNamed(namePart) == null)
				{
					namList.add(0, namePart);  // add to the start as otherwise the list will be reversed
					modList.remove(i);
				}
			}
		}

		//
		// Look for magic (or mighty) bonuses
		//
		int[] bonuses = null;
		int bonusCount = 0;
		i = aName.indexOf('+');
		if (i >= 0)
		{
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i), "/", false);
			bonusCount = aTok.countTokens();
			bonuses = new int[bonusCount];
			int idx = 0;
			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();
				bonuses[idx++] = Delta.decode(cString).intValue();
			}
			aName = aName.substring(0, i).trim();
		}

		//
		// Mighty bows suffered a (much-needed) renaming between 2.3.5 and 2.3.6
		// (Long|Short)bow +n (Mighty/Composite) --> (Long|Short)bow (+n Mighty/Composite)
		// (Long|Short)bow +x/+n (Mighty/Composite) --> (Long|Short)bow +x (+n Mighty/Composite)
		//
		// Look through the modifier list for MIGHTY, if found add the bonus to the start of the modifier's name
		//
		if (bonusCount > 0)
		{
			for (int idx1 = 0; idx1 < namList.size(); idx1++)
			{
				String aString = (String)namList.get(idx1);
				if (aString.equalsIgnoreCase("Mighty"))
				{
					aString = Delta.toString(bonuses[bonusCount - 1]) + " " + aString;
					namList.set(idx1, aString);
					bonusCount -= 1;
				}
			}
		}

		//
		// aName   : name of item minus all descriptors held in () as well as any bonuses
		// namList : list of all descriptors which cannot be modifiers
		// modList : list of all descriptors which *might* be modifiers
		// sizList : list of all size descriptors
		//


		//System.out.println(aName + ":" + namList + ":" + modList + ":" + sizList);

		String omitString = "";
		String bonusString = "";
		for (; ;)
		{
			final String eqName = aName + bonusString;
			eq = findEquipment(eqName, null, namList, sizList, omitString);
			if (eq != null)
			{
				if (sizList.size() > 1)		// was used in name, ignore as modifier
				{
					sizList.remove(0);
				}
				break;
			}
			eq = findEquipment(eqName, namList, null, sizList, omitString);
			if (eq != null)
			{
				if (sizList.size() > 1)		// was used in name, ignore as modifier
				{
					sizList.remove(0);
				}
				break;
			}
			eq = findEquipment(eqName, namList, null, null, omitString);
			if (eq != null)
			{
				break;
			}

			//
			// If only 1 size then include it in name
			//
			if (sizList.size() == 1)
			{
				eq = findEquipment(eqName, sizList, namList, null, omitString);
				if (eq == null)
				{
					eq = findEquipment(eqName, namList, sizList, null, omitString);
				}
				if (eq != null)
				{
					sizList.clear();
					break;
				}
			}


			//
			// If we haven't found it yet, try stripping Thrown from name (if there)
			//
			if (baseName.indexOf("Thrown") >= 0)
			{
				if (omitString.length() == 0)
				{
					omitString = "Thrown";
					continue;
				}
			}

			//
			// Still haven't found it? Try adding bonus to end of name
			//
			if (bonusCount > 0 && bonuses != null)
			{
				if (bonusString.length() == 0)
				{
					omitString = "";
					bonusString = " " + Delta.toString(bonuses[0]);
					continue;
				}
			}
			break;
		}


		if (eq != null)
		{
			boolean bModified = false;
			boolean bError = false;
			eq = (Equipment)eq.clone();
			//
			// Now attempt to add all the modifiers.
			//
			for (Iterator e = modList.iterator(); e.hasNext();)
			{
				final String namePart = (String)e.next();
				final EquipmentModifier eqMod = getQualifiedModifierNamed(namePart, eq);
				if (eqMod != null)
				{
					eq.addEqModifier(eqMod, true);
					if (eqMod.getAssignToAll() && eq.isDouble())
					{
						eq.addEqModifier(eqMod, false);
						bModified = true;
					}
				}
				else
				{
					System.out.println("Could not find a qualified modifier named: " + namePart + " for " + eq.getName() + ":" + eq.typeList());
					bError = true;
				}
			}
			//
			// Found what appeared to be the base item, but one of the modifiers is not qualified to be attached to the item
			//
			if (bError)
			{
				return null;
			}
			if (sizList.size() != 0)
			{
				eq.resizeItem((String)sizList.get(0));
				bModified = true;
				if (sizList.size() > 1)
				{
					System.out.println("Too many sizes in item name, used only 1st of: " + sizList);
				}
			}

			if (bModified)
			{
				eq.nameItemFromModifiers();
				if (!addEquipment(eq))
				{
					eq = getEquipmentNamed(eq.getName());
				}
			}
		}

		//
		// Try brute force. Find an equipment entry that starts the same as this item and then try to
		// coerce the item to match
		//
		//if (eq == null)
		//{
		//final String baseKey = aName.toUpperCase();
		//for (Iterator e = equipmentList.iterator(); e.hasNext();)
		//{
		//final Equipment eqm = (Equipment)e.next();
		//final String eqKey = eqm.getKeyName().toUpperCase();
		//if (eqKey.startsWith(baseKey))
		//{
		//	System.out.println( "Possible match: " + eqm.getKeyName() );
		//}
		//}
		//}


		//if (eq == null)
		//	System.out.println(aName + ":" + namList + ":" + modList + ":" + bonuses);
		return eq;
	}

	/*
	* Reduce/increase damage for modified size as per DMG p.162
	*/
	public static String adjustDamage(String aDamage, String sBaseSize, String sNewSize)
	{
		final int baseSize = sizeInt(sBaseSize);
		int itemSize = sizeInt(sNewSize);
		RollInfo aRollInfo = new RollInfo(aDamage);
		String retValue = null;
		if ((itemSize < 0) || (itemSize == baseSize) || (aRollInfo == null))
		{
			return aDamage;
		}
		if (aRollInfo != null)
		{
			//
			// Handle size increase
			//
			while (itemSize > baseSize)
			{
				switch (aRollInfo.sides)
				{
					case 1:
						aRollInfo.sides = 2;
						break;
					case 2:
						aRollInfo.sides = 3;
						break;
					case 3:
						aRollInfo.sides = 4;
						break;
					case 4:
						aRollInfo.sides = 6;
						break;
					case 6:
						aRollInfo.sides = 8;
						break;
					case 8:
						aRollInfo.times *= 2;
						aRollInfo.sides = 6;
						break;
					case 10:
						aRollInfo.times *= 2;
						aRollInfo.sides = 6;
						break;
					case 12:
						aRollInfo.times *= 2;
						aRollInfo.sides = 8;
						break;
					case 20:
						aRollInfo.times *= 2;
						aRollInfo.sides = 12;
						break;
					default:
						System.out.println("Size increase, unknown die size: " + Integer.toString(aRollInfo.sides) + ":" + aDamage + ":" + sBaseSize + ":" + sNewSize);
						return aDamage;
				}
				itemSize -= 1;
			}

			//
			// Handle size decrease
			//
			while (itemSize < baseSize)
			{
				switch (aRollInfo.sides)
				{
					case 1:
						aRollInfo.sides = 0;
						aRollInfo.times = 0;
						break;
					case 2:
						aRollInfo.sides = 1;
						break;
					case 3:
						aRollInfo.sides = 2;
						break;
					case 4:
						aRollInfo.sides = 3;
						break;
					case 6:
						aRollInfo.sides = 4;
						break;
					case 8:
						aRollInfo.sides = 6;
						break;
					case 10:
						aRollInfo.sides = 6;
						break;
					case 12:
						aRollInfo.sides = 8;
						break;
					case 20:
						aRollInfo.sides = 6;
						aRollInfo.times *= 2;
						break;
					default:
						System.out.println("Size decrease, unknown die size: " + Integer.toString(aRollInfo.sides) + ":" + aDamage + ":" + sBaseSize + ":" + sNewSize);
						return aDamage;
				}
				itemSize += 1;
			}
			retValue = Integer.toString(aRollInfo.times) + "d" + Integer.toString(aRollInfo.sides);
		}
		return retValue;
	}

	public static boolean isROG()
	{
		return isROG;
	}

	public static int getStatFromAbbrev(String sAbbrev)
	{
		for (int stat = 0; stat < s_ATTRIBSHORT.length; stat++)
		{
			if (sAbbrev.equalsIgnoreCase(s_ATTRIBSHORT[stat]))
			{
				return stat;
			}
		}
		return -1;
	}
}

