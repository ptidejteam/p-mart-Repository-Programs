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
 */

package pcgen.core;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import pcgen.util.Delta;

/**
 * This is like the top level model container. However,
 * it is build from static methods rather than instantiated.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
public class Globals
{
	/** The extension for a campaign file   */
	public final static String s_PCGEN_CAMPAIGN_EXTENSION = ".pcc";

	/** The extension for a character file   */
	public final static String s_PCGEN_CHARACTER_EXTENSION = ".pcg";

	/** The extension for a party file   */
	public final static String s_PCGEN_PARTY_EXTENSION = ".pcp";

	/** What a party template file name starts with.    */
	public final static String s_PARTY_TEMPLATE_START = "psheet";

	/** What a character template file name starts with.    */
	public final static String s_CHARACTER_TEMPLATE_START = "csheet";

	/** What to show when no race or alignment is selected. */
	public final static String s_NONESELECTED = "<none selected>";

	public final static String s_NONE = "None";

	public final static String s_APPNAME = "PCGen";

	/** An empty race. Duh. */
	public static Race s_EMPTYRACE = null;

	/** How to roll hitpoints. */
	public static final int s_HP_STANDARD = 0;
	public static final int s_HP_AUTOMAX = 1;
	public static final int s_HP_PERCENTAGE = 2;
	public static final int s_HP_LIVING_GREYHAWK = 3;
	public static final int s_HP_LIVING_CITY = 4;

	/** Encumbrance Constants */
	public static final int LIGHT_LOAD = 0;
	public static final int MEDIUM_LOAD = 1;
	public static final int HEAVY_LOAD = 2;
	public static final int OVER_LOAD = 3;

	/** Stat Constants */
	public static final int STRENGTH = 0;
	public static final int DEXTERITY = 1;
	public static final int CONSTITUTION = 2;
	public static final int INTELLIGENCE = 3;
	public static final int WISDOM = 4;
	public static final int CHARISMA = 5;
	/**
	* HackMaster attributes
	*/
	public static final int COMELINESS = 6;
	public static final int HONOR = 7;

	public static String[] s_ATTRIBLONG = null;
	public static String[] s_ATTRIBSHORT = null;
	public static boolean[] s_ATTRIBROLL = null;

	/** Short alignment strings */
	public final static String[] s_ALIGNSHORT = {
		"LG",
		"LN",
		"LE",
		"NG",
		"TN",
		"NE",
		"CG",
		"CN",
		"CE",
		"None",
		"Deity"
	};

	public final static String[] s_ALIGNLONG = new String[]{
		"Lawful Good",
		"Lawful Neutral",
		"Lawful Evil",
		"Neutral Good",
		"Neutral",
		"Neutral Evil",
		"Chaotic Good",
		"Chaotic Neutral",
		"Chaotic Evil",
		Globals.s_NONESELECTED,
		"Deity's"
	};

	/** Size constants */
	public static final int SIZE_F = 0;
	public static final int SIZE_D = 1;
	public static final int SIZE_T = 2;
	public static final int SIZE_S = 3;
	public static final int SIZE_M = 4;
	public static final int SIZE_L = 5;
	public static final int SIZE_H = 6;
	public static final int SIZE_G = 7;
	public static final int SIZE_C = 8;

	public final static String[] s_SIZELONG = {
		"Fine"
		, "Diminutive"
		, "Tiny"
		, "Small"
		, "Medium"
		, "Large"
		, "Huge"
		, "Gigantic"
		, "Colossal"
	};

	public final static String[] s_SIZESHORT = {
		"F"
		, "D"
		, "T"
		, "S"
		, "M"
		, "L"
		, "H"
		, "G"
		, "C"
	};

	public final static char[] s_SIZESHORTCHAR = {
		'F'
		, 'D'
		, 'T'
		, 'S'
		, 'M'
		, 'L'
		, 'H'
		, 'G'
		, 'C'
	};


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

	public final static String s_TAG_TYPE = "TYPE:";

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
  
	private static ArrayList nameList = new ArrayList();
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

	// bsmeister 11/20/2001
	// the DenominationList class is derived from ArrayList
	private static DenominationList denominationList = new DenominationList();

	private static int hpPct = 100;
	private static String currentFile = "";
	private static String currentSource = "";
	private static int lineNum = 0;
	private static int excSkillCost = 0;
	private static int intCrossClassSkillCost = 2;
	private static boolean boolBypassMaxSkillRank = false;
	private static boolean debugMode = false;
	private static Point leftUpperCorner = null;
	private static String dmNotes = "";

	private static ArrayList sourceList = new ArrayList();
	private static ArrayList sourceFileList = new ArrayList();

	private static boolean bAutoGeneration = false;

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

	final public static int RACE_TYPE = 0;
	final public static int CLASS_TYPE = 1;
	final public static int SKILL_TYPE = 2;
	final public static int FEAT_TYPE = 3;
	final public static int DOMAIN_TYPE = 4;
	final public static int DEITY_TYPE = 5;
	final public static int SPELL_TYPE = 6;
	final public static int WEAPONPROF_TYPE = 7;
	final public static int SCHOOLS_TYPE = 8;
	final public static int COLOR_TYPE = 9;
	final public static int TRAIT_TYPE = 10;
	final public static int EQUIPMENT_TYPE = 11;
	final public static int LANGUAGE_TYPE = 12;
	final public static int LOAD_TYPE = 13;
	final public static int SPECIAL_TYPE = 14;
	final public static int CAMPAIGN_TYPE = 15;
	final public static int CLASSSKILL_TYPE = 16;
	final public static int CLASSSPELL_TYPE = 17;
	final public static int REQSKILL_TYPE = 18;
	final public static int TEMPLATE_TYPE = 21;
	final public static int XP_TYPE = 22;
	final public static int NAME_TYPE = 23;
	final public static int BONUS_TYPE = 24;
	final public static int EQMODIFIER_TYPE = 25;
	final public static int SIZEADJUSTMENT_TYPE = 26;
	final public static int STATNAME_TYPE = 27;
  final public static int PHOBIA_TYPE =28;
  final public static int LOCATION_TYPE = 29;
  final public static int INTERESTS_TYPE = 30;
  final public static int PHRASE_TYPE = 31;
  final public static int HAIRSTYLE_TYPE = 32;
  final public static int SPEECH_TYPE = 33;


	/*
	* Define the order in which the file types are ordered so we don't have to
	* keep renumbering them
	*/
	final private static int loadOrder[] = {
		-1
		, STATNAME_TYPE
		, RACE_TYPE
		, CLASS_TYPE
		, SKILL_TYPE
		, FEAT_TYPE
		, DOMAIN_TYPE
		, DEITY_TYPE
		, SPELL_TYPE
		, WEAPONPROF_TYPE
		, SCHOOLS_TYPE
		, COLOR_TYPE
		, TRAIT_TYPE
		, EQMODIFIER_TYPE		// This needs to be loaded before the equipment, so any modifiers will be found
		, EQUIPMENT_TYPE
		, LANGUAGE_TYPE
		, LOAD_TYPE
		, SPECIAL_TYPE
		, CAMPAIGN_TYPE
		, CLASSSKILL_TYPE
		, CLASSSPELL_TYPE
		, REQSKILL_TYPE
		, TEMPLATE_TYPE
		, XP_TYPE
		, NAME_TYPE
		, BONUS_TYPE
		, SIZEADJUSTMENT_TYPE
    , PHOBIA_TYPE
    , LOCATION_TYPE
    , INTERESTS_TYPE
    , PHRASE_TYPE
    , HAIRSTYLE_TYPE
    , SPEECH_TYPE
		, -9999
	};

	/** Various Game Modes */
	private static boolean starWarsMode = false;
	private static boolean dndMode = true;
	private static boolean weirdWarsMode = false;
	private static boolean deadlandsMode = false;
	private static boolean l5rMode = false;
	private static boolean wheelMode = false;
	private static boolean sidewinderMode = false;
	private static boolean hackMasterMode = false;
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
	 */
	private static int rollMethod = 1;

	private static boolean toolTipTextShown = true;
	private static boolean previewTabShown = false;
	private static ArrayList chosenCampaignSourcefiles = new ArrayList();
	private static boolean hpMaxAtFirstLevel = true;
	private static int hpRollMethod = s_HP_STANDARD;
	private static int prereqFailColor = 0;				// 0 = black, 0xFF0000 = red, 0xFFFFFF = white
	private static boolean skillIncrementBefore = true;
	private static HashMap loadedFiles = new HashMap();
	private static String skillReq = "";
	private static ArrayList lstExcludeFiles = new ArrayList();
	private static ArrayList pccFileLines = new ArrayList();
	private static ArrayList raceFileLines = new ArrayList();
	private static ArrayList classFileLines = new ArrayList();
	private static ArrayList skillFileLines = new ArrayList();
	private static ArrayList featFileLines = new ArrayList();
	private static ArrayList deityFileLines = new ArrayList();
	private static ArrayList domainFileLines = new ArrayList();
	private static ArrayList weaponProfFileLines = new ArrayList();
	private static ArrayList equipmentFileLines = new ArrayList();
	private static ArrayList classSkillFileLines = new ArrayList();
	private static ArrayList classSpellFileLines = new ArrayList();
	private static ArrayList spellFileLines = new ArrayList();
	private static ArrayList languageLines = new ArrayList();
	private static ArrayList reqSkillLines = new ArrayList();
	private static ArrayList templateFileLines = new ArrayList();
	private static ArrayList equipmentModifierFileLines = new ArrayList();
	private static JFrame rootFrame;

	private static boolean autogenMasterwork = true;
	private static boolean autogenMagic = true;
	private static boolean autogenRacial = true;
	private static boolean autogenExoticMaterial = true;

	private static String defaultPath = System.getProperty("user.dir");
	private static String defaultPcgPath = defaultPath + File.separator + "characters";
	private static File templatePath = new File(defaultPath + File.separator + "templates");
	private static File pcgPath = new File(defaultPath);
	private static File htmlOutputPath = new File(defaultPath);
	private static String tmpPath = System.getProperty("java.io.tmpdir");
	private static File tempPath = new File(tmpPath);
	/* That browserPath is set to null is intentional. */
	private static File browserPath = null; //Intentional null
	private static String selectedTemplate = templatePath.getAbsolutePath() + File.separator + "csheet.htm";
	private static String selectedPartyTemplate = templatePath.getAbsolutePath() + File.separator + "psheet.htm";
	private static String pccFilesLocation = null;
	private static Properties options = new Properties();
	private static boolean autoFeatsRefundable = false;

	private final static Random random = new Random(System.currentTimeMillis());

	public static final int AUTOGEN_RACIAL = 1;
	public static final int AUTOGEN_MASTERWORK = 2;
	public static final int AUTOGEN_MAGIC = 3;
	public static final int AUTOGEN_EXOTICMATERIAL = 4;

	private static String skinLFThemePack = null;

	public static String getSkinLFThemePack()
	{
		return skinLFThemePack;
	}

	public static void setSkinLFThemePack(String skinLFThemePack)
	{
		Globals.skinLFThemePack = skinLFThemePack;
	}

	public static boolean getAutogen(int idx)
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
		return false;
	}

	public static void setAutogen(int idx, boolean bFlag)
	{
		switch (idx)
		{
			case AUTOGEN_RACIAL:
				autogenRacial = bFlag;
			case AUTOGEN_MASTERWORK:
				autogenMasterwork = bFlag;
			case AUTOGEN_MAGIC:
				autogenMagic = bFlag;
			case AUTOGEN_EXOTICMATERIAL:
				autogenExoticMaterial = bFlag;
			default:
				break;
		}
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

	public static int getRandomInt(final int high)
	{
		return random.nextInt(high);
	}

	public static int getRandomInt()
	{
		return random.nextInt();
	}


	public static String getPccFilesLocation()
	{
		return pccFilesLocation;
	}

	public static void setPccFilesLocation(String systemFilesLocation)
	{
		Globals.pccFilesLocation = systemFilesLocation;
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

	public static boolean isDndMode()
	{
		return dndMode;
	}

	public static void setDndMode(boolean dndMode)
	{
		Globals.dndMode = dndMode;
	}

	public static boolean isStarWarsMode()
	{
		return starWarsMode;
	}

	public static void setStarWarsMode(boolean starWarsMode)
	{
		Globals.starWarsMode = starWarsMode;
	}

	public static boolean isWeirdWarsMode()
	{
		return weirdWarsMode;
	}

	public static void setWeirdWarsMode(boolean weirdWarsMode)
	{
		Globals.weirdWarsMode = weirdWarsMode;
	}

	public static boolean isDeadlandsMode()
	{
		return deadlandsMode;
	}

	public static void setDeadlandsMode(boolean deadlandsMode)
	{
		Globals.deadlandsMode = deadlandsMode;
	}

	public static boolean isL5rMode()
	{
		return l5rMode;
	}

	public static void setL5rMode(boolean l5rMode)
	{
		Globals.l5rMode = l5rMode;
	}

	public static boolean isGrimHPMode()
	{
		return grimHPMode;
	}

	public static void setGrimHPMode(boolean grimHPMode)
	{
		Globals.grimHPMode = grimHPMode;
	}

	public static boolean isWheelMode()
	{
		return wheelMode;
	}

	public static void setWheelMode(boolean aWheelMode)
	{
		wheelMode = aWheelMode;
	}

	public static boolean isSidewinderMode()
	{
		return sidewinderMode;
	}

	public static void setSidewinderMode(boolean aSidewinderMode)
	{
		sidewinderMode = aSidewinderMode;
	}

	public static boolean isHackMasterMode()
	{
		return hackMasterMode;
	}

	public static void setHackMasterMode(boolean aHackMasterMode)
	{
		hackMasterMode = aHackMasterMode;
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

	public static void setLeftUpperCorner(Point leftUpperCorner)
	{
		Globals.leftUpperCorner = leftUpperCorner;
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

	public static boolean isDebugMode()
	{
		return debugMode;
	}

	public static void setDebugMode(boolean debugMode)
	{
		Globals.debugMode = debugMode;
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
	 */
	public static void setRollMethod(int rollMethod)
	{
		Globals.rollMethod = rollMethod;
	}

	public static ArrayList getChosenCampaignSourcefiles()
	{
		return chosenCampaignSourcefiles;
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

	public static ArrayList getLanguageSet()
	{
		return languageList;
	}

	public static ArrayList getColorList()
	{
		return colorList;
	}

	public static ArrayList getNameList()
	{
		return nameList;
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

	public static PlayerCharacter getCurrentPC()
	{
		return currentPC;
	}

	public static void setCurrentPC(PlayerCharacter aCurrentPC)
	{
		currentPC = aCurrentPC;
	}

	public static String getCurrentSource()
	{
		return currentSource;
	}

	public static void setCurrentSource(String aString)
	{
		currentSource = aString;
	}

	public static int saveSource(String src)
	{
		int idx = sourceList.indexOf(src);
		if (idx >= 0)
		{
			return idx;
		}
		sourceList.add(src);
		return sourceList.indexOf(src);
	}

	public static String savedSource(int idx)
	{
		if ((idx >= 0) && (idx < sourceList.size()))
		{
			return (String)sourceList.get(idx) + ", ";
		}
		return "";
	}

	public static int saveSourceFile(String src)
	{
		int idx = sourceFileList.indexOf(src);
		if (idx >= 0)
		{
			return idx;
		}
		sourceFileList.add(src);
		return sourceFileList.indexOf(src);
	}

	public static String savedSourceFile(int idx)
	{
		if ((idx >= 0) && (idx < sourceFileList.size()))
			return (String)sourceFileList.get(idx);
		return "";
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


	public static void setCurrentFile(String fileName)
	{
		currentFile = fileName;
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
		return "gp";
	}

	public static String getLongCurrencyDisplay()
	{
		if (isStarWarsMode())
		{
			return "Credits";
		}
		return "Gold";
	}

	/**
	 * Returns the current root frame.
	 *
	 * @return    the <code>rootFrame</code> property
	 * @author    Matthew Woodard
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
	 * @author        Matthew Woodard
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
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			f = (Feat)e.next();
			if (f.getName().equalsIgnoreCase(name))
			{
				return f;
			}
		}
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

	//
	// Stick a comma between every character of a string
	//
	public static String commaDelimit(String oldString)
	{
		final int oldStringLength = oldString.length();
		StringBuffer newString = new StringBuffer(oldStringLength);
		for (int i = 0; i < oldStringLength; i++)
		{
			if (i != 0)
				newString.append(",");
			newString.append(oldString.charAt(i));
		}
		return newString.toString();
	}

	public static String commaDelimit(ArrayList stringArray)
	{
		StringBuffer newString = new StringBuffer(70);
		for (Iterator e = stringArray.iterator(); e.hasNext();)
		{
			if (newString.length() != 0)
			{
				newString.append(", ");
			}
			newString.append((String)e.next());
		}
		return newString.toString();
	}

	public static int innerMostStringStart(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == '(')
			{
				current++;
				if (current >= hi)
				{
					hi = current;
					index = i;
				}
			}
			else if (aString.charAt(i) == ')')
				current--;
		}
		return index;
	}

	public static int innerMostStringEnd(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == '(')
			{
				current++;
				if (current > hi)
					hi = current;
			}
			else if (aString.charAt(i) == ')')
			{
				if (current == hi)
					index = i;
				current--;
			}
		}
		return index;
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
		loadedFiles.clear();
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
		skillReq = "";
		lstExcludeFiles.clear();
		pccFileLines.clear();
		raceFileLines.clear();
		classFileLines.clear();
		skillFileLines.clear();
		featFileLines.clear();
		deityFileLines.clear();
		domainFileLines.clear();
		templateFileLines.clear();
		weaponProfFileLines.clear();
		equipmentFileLines.clear();
		classSkillFileLines.clear();
		classSpellFileLines.clear();
		spellFileLines.clear();
		reqSkillLines.clear();
		languageLines.clear();
		Equipment.clearEquipmentTypes();
		equipmentModifierFileLines.clear();
		chosenCampaignSourcefiles.clear();
	}

	/**
	 * Loads a file containing game system information and adds details
	 * to an array. Eventually these end up in the various array list
	 * properties of <code>Global</code>.
	 * <p>
	 * Different types of files are determined by the <code>type</code>
	 * parameter. The valid <code>type</code>'s are:
	 *   0 - race,
	 *   1 - class,
	 *   2 - skill,
	 *   3 - feat,
	 *   4 - domain,
	 *   5 - deity,
	 *   6 - spell,
	 *   7 - weapon prof,
	 *   8 - school,
	 *   9 - color,
	 *   10 - trait,
	 *   11 - equipment,
	 *   12 - language,
	 *   13 - carrying load,
	 *   14, 16, 17, 18 - text file,
	 *   15 - campaign,
	 *   20 - pipe ("|") separated list.
	 *   21 - template
	 *   22 - CR to XP conversion values
	 *   23 - name
	 *   24 -
	 *   25 - Equipment modifiers
	 *   26 - Size adjustment
	 * <p>
	 * The file is opened and read. Lines are parsed by an object
	 * of the relevant type (based on <code>type</code> above), and
	 * then added to the array list.
	 *
	 * @param fileName    name of the file to load from
	 * @param fileType    type of the file (see above for types).
	 * @param aList       <code>ArrayList</code> with existing data.
	 *                    The new data is appended to this.
	 * @return <code>aList</code>, with new data appended
	 */
	public static ArrayList initFile(String fileName, int fileType, ArrayList aList)
	{
		byte[] inputLine = null;
		fileName = fileName.replace('\\', File.separatorChar);
		fileName = fileName.replace('/', File.separatorChar);
		File aFile = new File(fileName);
		PObject anObj = null;
		String aString = null;
		String aLine = "";
		currentFile = fileName;
		lineNum = 0;
		try
		{
			FileInputStream aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			aString = new String(inputLine);
			String newlinedelim = new String("\r\n");
			StringTokenizer newlineStr = new StringTokenizer(aString, newlinedelim, false);
			setCurrentSource("");
			while (newlineStr.hasMoreTokens())
			{
				aLine = newlineStr.nextToken();
				++lineNum;
				if (aLine.startsWith("#"))
				{
					continue;
				}
//				if (aLine.startsWith("SOURCE:") && fileType < LOAD_TYPE)
				if (aLine.startsWith("SOURCE:") && fileType != CAMPAIGN_TYPE)
				{
					setCurrentSource(aLine.substring(7));
					continue;
				}
				switch (fileType)
				{
					case RACE_TYPE:
						anObj = new Race();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case CLASS_TYPE:
						if (aLine.startsWith("CLASS:"))
						{
							anObj = new PCClass();
							aList.add(anObj);
						}
						if (anObj != null)
						{
							anObj.parseLine(aLine, aFile, lineNum);
						}
						break;
					case SKILL_TYPE:
						anObj = new Skill();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case FEAT_TYPE:
						anObj = new Feat();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case DOMAIN_TYPE:
						anObj = new Domain();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case DEITY_TYPE:
						anObj = new Deity();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case SPELL_TYPE:
						anObj = new Spell();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case WEAPONPROF_TYPE:
						anObj = new WeaponProf();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case SCHOOLS_TYPE:
						schoolsList.add(aLine);
						break;
					case COLOR_TYPE:
						colorList.add(aLine);
						break;
					case TRAIT_TYPE:
						traitList.add(aLine);
						break;
					case EQUIPMENT_TYPE:
						anObj = new Equipment();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case LANGUAGE_TYPE:
						Language aLang = new Language();
						aLang.parseLine(aLine, aFile, lineNum);
						aList.add(aLang);
						break;
					case LOAD_TYPE:
						loadStrings.add(aLine);
						break;
					case SPECIAL_TYPE:
						SpecialAbility sp = new SpecialAbility();
						sp.parseLine(aLine, aFile, lineNum);
						specialsList.add(sp);
						break;
					case SIZEADJUSTMENT_TYPE:
						SizeAdjustment sadj = new SizeAdjustment();
						sadj.parseLine(aLine, aFile, lineNum);
						sizeAdjustmentList.add(sadj);
						break;
					case STATNAME_TYPE:
						StringTokenizer st = new StringTokenizer(aLine, ":", false);
						try
						{
							if (lineNum == 1)
							{
								int i = Integer.parseInt(st.nextToken());
								if (i > 0)
								{
									s_ATTRIBLONG = new String[i];
									s_ATTRIBSHORT = new String[i];
									s_ATTRIBROLL = new boolean[i];
								}
							}
							else if (st.countTokens() != 3)
							{
								JOptionPane.showMessageDialog(null, "Invalid entry in Stat Name file: " + aLine, s_APPNAME, JOptionPane.ERROR_MESSAGE);
							}
							else
							{
								s_ATTRIBLONG[lineNum - 2] = st.nextToken();					// stat name
								s_ATTRIBSHORT[lineNum - 2] = st.nextToken();					// stat abbreviation
								s_ATTRIBROLL[lineNum - 2] = Integer.parseInt(st.nextToken()) != 0;	// stat rolled
							}
						}
						catch (Exception e)
						{
							JOptionPane.showMessageDialog(null, "Exception in Stat Name file: " + aLine, s_APPNAME, JOptionPane.ERROR_MESSAGE);
						}
						break;
					case -1: // if we're in the process of loading campaigns/sources when
						// another source is loaded via PCC:, then it's fileType=-1
					case CLASSSKILL_TYPE:
					case CLASSSPELL_TYPE:
					case REQSKILL_TYPE:
						aList.add(aLine);
						break;
					case CAMPAIGN_TYPE:
						if (anObj == null)
						{
							anObj = new Campaign();
							campaignList.add(anObj);
						}
						if (anObj != null)
						{
							anObj.parseLine(aLine, aFile, lineNum);
						}
						break;
					case 20:
						StringTokenizer aTok = new StringTokenizer(aLine, "|", false);
						while (aTok.hasMoreTokens())
						{
							aString = aTok.nextToken();
							if (aString.startsWith("ESK:"))
							{
								excSkillCost = Integer.parseInt(aString.substring(4));
							}
							else if (aString.startsWith("CCSC:"))
							{
								intCrossClassSkillCost = Integer.parseInt(aString.substring(5));
							}
							else if (aString.startsWith("X:"))
							{
								StringTokenizer bTok = new StringTokenizer(aString.substring(2), ":", false);
								int x = 0;
								int y = 0;
								int width = 500;
								int height = 200;
								if (bTok.hasMoreTokens())
								{
									x = Integer.parseInt(bTok.nextToken());
								}
								if (bTok.hasMoreTokens())
								{
									y = Integer.parseInt(bTok.nextToken());
								}
								if (bTok.hasMoreTokens())
								{
									width = Integer.parseInt(bTok.nextToken());
								}
								if (bTok.hasMoreTokens())
								{
									height = Integer.parseInt(bTok.nextToken());
								}
								aList.add(new Integer(x));
								aList.add(new Integer(y));
								aList.add(new Integer(height));
								aList.add(new Integer(width));
							}
						}
						break;
					case TEMPLATE_TYPE:
						anObj = new PCTemplate();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case XP_TYPE:
						XPList.add(aLine);
						break;
					case NAME_TYPE:
						nameList.add(aLine);
						break;
					case BONUS_TYPE:
						bonusStackList.add(aLine.toUpperCase());
						break;
					case EQMODIFIER_TYPE:
						anObj = new EquipmentModifier();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
          case PHOBIA_TYPE:
            phobiaList.add(aLine);
            break;
          case LOCATION_TYPE:
            locationList.add(aLine);
            break;
          case INTERESTS_TYPE:
            interestsList.add(aLine);
            break;
          case PHRASE_TYPE:
            phraseList.add(aLine);
            break;
          case HAIRSTYLE_TYPE:
            hairStyleList.add(aLine);
            break;
          case SPEECH_TYPE:
            speechList.add(aLine);
            break;
				}
				//
				// Save the source file in object
				//
				switch (fileType)
				{
					case RACE_TYPE:
					case CLASS_TYPE:
					case SKILL_TYPE:
					case FEAT_TYPE:
					case DOMAIN_TYPE:
					case DEITY_TYPE:
					case SPELL_TYPE:
					case WEAPONPROF_TYPE:
					case EQUIPMENT_TYPE:
					case CAMPAIGN_TYPE:
					case EQMODIFIER_TYPE:
						if (anObj != null)
						{
							anObj.setSourceFile(aFile.getAbsolutePath());
						}
						break;

					case SCHOOLS_TYPE:
					case COLOR_TYPE:
					case TRAIT_TYPE:
					case LANGUAGE_TYPE:
					case LOAD_TYPE:
					case SPECIAL_TYPE:
					case CLASSSKILL_TYPE:
					case CLASSSPELL_TYPE:
					case REQSKILL_TYPE:
					case TEMPLATE_TYPE:
					case XP_TYPE:
					case NAME_TYPE:
					case BONUS_TYPE:
					case SIZEADJUSTMENT_TYPE:
					case STATNAME_TYPE:
          case PHOBIA_TYPE:
          case LOCATION_TYPE:
          case INTERESTS_TYPE:
          case PHRASE_TYPE:
          case HAIRSTYLE_TYPE:
          case SPEECH_TYPE:
						break;
				}
			}
			aStream.close();
		}
		catch (Exception exception)
		{
			if (!fileName.equals("pcgen.ini"))
			{
				aString = "ERROR:" + fileName + "\nerror " + aLine + "\nException type:" + exception.getClass().getName() + "\nMessage:" + exception.getMessage();
				System.out.println(aString);
				JOptionPane.showMessageDialog(null, aString, s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}
		}
		setCurrentSource("");
		return aList;
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

	public static Float maxLoadForStrengthAndSize(int strength, String size)
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
		double dbl = weight.doubleValue() / maxLoadForStrengthAndSize(strength, currentPC.getSize()).doubleValue();
		if (dbl <= .333)
		{
			return Globals.LIGHT_LOAD;
		}
		if (dbl <= .666)
		{
			return Globals.MEDIUM_LOAD;
		}
		if (dbl <= 1.0)
		{
			return Globals.HEAVY_LOAD;
		}
		return Globals.OVER_LOAD;
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
// Why are we sorting by Key instead of Name? If we want a sorted list
// for data that is displayed shouldn't we use the name?
//			return ((PObject)o1).getName().compareTo(((PObject)o2).getName());
			return ((PObject)o1).getKeyName().compareTo(((PObject)o2).getKeyName());
		}
	}


	/**
	 * Create a delimited string for a list.
	 * Awfully similar to unSplit.
	 */
	public static String stringForList(Iterator e, String delim)
	{
		StringBuffer aStrBuf = new StringBuffer(100); //More likely to be true than 16 (the default)
		boolean needDelim = false;
		while (e.hasNext())
		{
			if (needDelim)
			{
				aStrBuf.append(delim);
			}
			else
			{
				needDelim = true;
			}
			aStrBuf.append(e.next().toString());
		}
		return aStrBuf.toString();
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

	public static void loadCampaigns(ArrayList aSelectedCampaignsList)
	{
		d_sorted = false;
		if (aSelectedCampaignsList.size() == 0)
		{
			JOptionPane.showMessageDialog(null, "You must select at least one campaign to load.", s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

//		//Add empty race
//
//		createEmptyRace();

		int i = 0;
		Campaign aCamp = null;
		Campaign bCamp = null;
		Campaign cCamp = null;
		for (i = 0; i < aSelectedCampaignsList.size() - 1; i++)
		{
			aCamp = (Campaign)aSelectedCampaignsList.get(i);
			for (int j = i + 1; j < aSelectedCampaignsList.size(); j++)
			{
				bCamp = (Campaign)aSelectedCampaignsList.get(j);
				if (bCamp.getRank().intValue() < aCamp.getRank().intValue())
				{
					aSelectedCampaignsList.set(i, bCamp);
					aSelectedCampaignsList.set(j, aCamp);
					cCamp = (Campaign)aCamp.clone();
					aCamp = bCamp;
					bCamp = cCamp;
				}
			}
		}

		for (i = 0; i < aSelectedCampaignsList.size(); i++)
		{
			aCamp = (Campaign)aSelectedCampaignsList.get(i);
			loadCampaignFile(aCamp);
		}
		ArrayList lineList = null;
		ArrayList bArrayList = null;
		for (int loadIdx = 0; ; loadIdx++)
		{
			int lineType = loadOrder[loadIdx];
			if (lineType == -9999)
			{
				break;
			}
			lineList = null;
			bArrayList = new ArrayList();
			switch (lineType)
			{
				case -1: // okay - should be 15 (CAMPAIGN_TYPE), but needed to do it at the front and didn't want to bump everything else up one..
					lineList = pccFileLines; // merton_monk  2Nov01
					break;
				case RACE_TYPE:
					lineList = raceFileLines;
					break;
				case CLASS_TYPE:
					lineList = classFileLines;
					break;
				case SKILL_TYPE:
					lineList = skillFileLines;
					break;
				case FEAT_TYPE:
					lineList = featFileLines;
					break;
				case DOMAIN_TYPE:
					lineList = domainFileLines;
					break;
				case DEITY_TYPE:
					lineList = deityFileLines;
					break;
				case SPELL_TYPE:
					lineList = spellFileLines;
					break;
				case WEAPONPROF_TYPE:
					lineList = weaponProfFileLines;
					break;
				case SCHOOLS_TYPE:
					continue;
				case COLOR_TYPE:
					continue;
				case TRAIT_TYPE:
					continue;
				case EQUIPMENT_TYPE:
					lineList = equipmentFileLines;
					break;
				case LANGUAGE_TYPE:
					lineList = languageLines;
					break;
				case LOAD_TYPE:
					continue;
				case SPECIAL_TYPE:
					continue;
				case SIZEADJUSTMENT_TYPE:
					continue;
				case STATNAME_TYPE:
					continue;
				case CAMPAIGN_TYPE: // this is the campaign/source type, but needs to be first, so it's done at -1
					continue;
				case CLASSSKILL_TYPE:
					lineList = classSkillFileLines;
					break;
				case CLASSSPELL_TYPE:
					lineList = classSpellFileLines;
					break;
				case REQSKILL_TYPE:
					lineList = reqSkillLines;
					break;
				case 19:
					continue;
				case 20:
					continue;
				case TEMPLATE_TYPE:
					lineList = templateFileLines;
					break;
				case XP_TYPE:
					continue;
				case NAME_TYPE:
					continue;
				case BONUS_TYPE:
					continue;
				case EQMODIFIER_TYPE:
					lineList = equipmentModifierFileLines;
					break;
        case PHOBIA_TYPE:
          continue;  
        case LOCATION_TYPE:
          continue;
        case INTERESTS_TYPE:
          continue;
        case PHRASE_TYPE:
          continue;
        case HAIRSTYLE_TYPE:
          continue;
        case SPEECH_TYPE:
          continue;
				default:
					lineList = null;
					System.out.println("Campaign list corrupt at line: " + i + " no such lineType exists. Stopped parsing campaigns, but not aborting program.");
					return;
			}
			ArrayList cArrayList = null;
			String fileName = null;
			//This relies on new items being added to the *end* of an ArrayList.
			for (int j = 0; j < lineList.size(); j++)
			{
				final String aLine = (String)lineList.get(j);
				final StringTokenizer lineTokenizer = new StringTokenizer(aLine, "|", false);
				int inMode = 0;
				cArrayList = new ArrayList();
				fileName = "";
				String currentToken = null;
				String dString = null;
				while (lineTokenizer.hasMoreTokens())
				{
					currentToken = lineTokenizer.nextToken();
					int openParens = 0;
					int closeParens = 0;
					dString = currentToken.substring(1);
					while (dString.lastIndexOf("(") > -1)
					{
						openParens++;
						dString = dString.substring(0, dString.lastIndexOf("("));
					}
					dString = currentToken;
					while (dString.lastIndexOf(")") > -1)
					{
						closeParens++;
						dString = dString.substring(0, dString.lastIndexOf(")"));
					}
					boolean handled = false;
					if (currentToken.endsWith(".lst") || currentToken.endsWith(".pcc"))
					{
						// load file
						handled = true;

						//*** to handle LSTEXCLUDE commands 12/12/01
						//skip file if it's on the LSTEXCLUDE list
						boolean loadFile = true; //assume we won't find the file in the exclusion list
						Iterator lstExcludeIter = lstExcludeFiles.iterator();
						while (lstExcludeIter.hasNext())
						{
							String lstFilename = (String)lstExcludeIter.next();
							//if the file were loading is found on the exclusion list...
							if (currentToken.indexOf(lstFilename) > 0)
							{
								loadFile = false;  //...don't load it
								break;  //exit the while since we've already found our target
							}
						}
						//*** to handle LSTEXCLUDE commands 12/12/01

						if (loadFile)
						{
							bArrayList = adds(lineType, bArrayList);
							if (!loadedFiles.containsKey(currentToken))
							{
								bArrayList = initFile(System.getProperty("user.dir") + File.separatorChar + currentToken, lineType, bArrayList);
								if (lineType == -1)
									initCampaignFromList(bArrayList, aLine);
							}
							if (fileName.length() > 0 && !loadedFiles.containsKey(fileName))
							{
								loadedFiles.put(fileName, fileName);
							}
						}
						fileName = currentToken;
						cArrayList.clear();
					}
					if (currentToken.startsWith("(EXCLUDE"))
					{
						handled = true;
						fileName = "";
						if (closeParens > openParens)
						{
							currentToken = currentToken.substring(0, currentToken.length() - 1);
						}
						cArrayList.add(currentToken.substring(9));
						if (closeParens > openParens)
						{
							currentToken = ")";
						}
						inMode = -1;
					}
					else if (currentToken.startsWith("(INCLUDE"))
					{
						handled = true;
						fileName = "";
						if (closeParens > openParens)
						{
							currentToken = currentToken.substring(0, currentToken.length() - 1);
						}
						cArrayList.add(currentToken.substring(9));
						if (closeParens > openParens)
						{
							currentToken = ")";
						}
						inMode = 1;
					}
					if (currentToken.endsWith(")") && closeParens > openParens)
					{
						if (handled == false)
						{
							cArrayList.add(currentToken.substring(0, currentToken.length() - 1));
						}
						handled = true;
						if (inMode == -1)
						{
							// exclude
							PObject anObject = null;
							for (int k = bArrayList.size() - 1; k >= 0; k--)
							{
								anObject = (PObject)bArrayList.get(k);
								if (cArrayList.contains(anObject.getKeyName()))
								{
									bArrayList.remove(k);
								}
							}
						}
						else if (inMode == 1)
						{
							// include
							PObject anObject = null;
							for (int k = bArrayList.size() - 1; k >= 0; k--)
							{
								anObject = (PObject)bArrayList.get(k);
								if (!cArrayList.contains(anObject.getKeyName()))
								{
									bArrayList.remove(k);
								}
							}
						}
						inMode = 0;
						bArrayList = adds(lineType, bArrayList);
					}
					if (handled == false && lineType != 12 && lineType != REQSKILL_TYPE)
					{
						cArrayList.add(currentToken);
					}
					else if (handled == false)
					{
						bArrayList.add(currentToken);
					}
				}
				bArrayList = adds(lineType, bArrayList);
				if (fileName.length() > 0 && !loadedFiles.containsKey(fileName))
				{
					loadedFiles.put(fileName, fileName);
				}
			}
		}

		if (skillReq.length() > 0)
		{
			for (Iterator e1 = skillList.iterator(); e1.hasNext();)
			{
				final Skill aSkill = (Skill)e1.next();
				if ((skillReq.equals("UNTRAINED") && aSkill.untrained().startsWith("Y")) || skillReq.equals("ALL"))
				{
					aSkill.setRequired(true);
				}
			}
		}
		//
		// Check all the weapons to see if they are either Melee or Ranged, to avoid
		// problems when we go to export/preview the character
		//
		if (equipmentList.size() > 0)
		{
			for (Iterator e2 = equipmentList.iterator(); e2.hasNext();)
			{
				final Equipment aEq = (Equipment)e2.next();
				if (aEq.isWeapon() && !aEq.isMelee() && !aEq.isRanged())
				{
					JOptionPane.showMessageDialog(null, "Weapon: " + aEq.getName() + " is neither Melee nor Ranged." + "\n" + s_APPNAME + " cannot calculate \"to hit\" unless one of these is selected." + "\nSource: " + aEq.getSourceFile(), s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		}

//		autoGenerateEquipment();

//
// Test the algorithm for determining the base item from the equipment name. Should load all equipment in order to test properly
//
//		doEquipNameTest();

	}

	/**
	 * Reads the source file for the campaign aCamp and adds the names of files
	 * to be loaded to raceFileLines, classFileLines etc.
	 */

	private static void loadCampaignFile(Campaign aCamp)
	{
		aCamp.setIsLoaded(true);
		String aString = aCamp.getSourceFile();

		boolean alreadyChosen = false;
		for (Iterator it = chosenCampaignSourcefiles.iterator(); it.hasNext();)
		{
			final String s = (String)it.next();
			if (s.equals(aString))
			{
				alreadyChosen = true;
				break;
			}
		}
		if (!alreadyChosen)
			chosenCampaignSourcefiles.add(aString);

		ArrayList aArrayList = initFile(aString, -1, new ArrayList());
		initCampaignFromList(aArrayList, aString);
	}

	// aArrayList contains an array of the lines as returned from initFile
	private static void initCampaignFromList(ArrayList aArrayList, String aString)
	{
		String aLine = null;
		for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
		{
			aLine = (String)e1.next();
			if (aLine.startsWith("CAMPAIGN:"))
			{
				continue;
			}
			if (aLine.startsWith("RANK:"))
			{
				continue;
			}
			if (aLine.startsWith("GAME:"))
			{
				continue;
			}
			if (aLine.startsWith("SOURCE:"))
			{
				continue;
			}

			//*** to handle LSTEXCLUDE commands 12/12/01
			//check here for LST files to exclude from any further loading
			if (aLine.startsWith("LSTEXCLUDE:"))
			{
				String lstList = aLine.substring(11);
				StringTokenizer lstTok = new StringTokenizer(lstList, "|");
				while (lstTok.hasMoreTokens())
				{
					String lstFilename = lstTok.nextToken();
					lstExcludeFiles.add(lstFilename);
				}
				continue;
			}
			//*** to handle LSTEXCLUDE commands 12/12/01

			/* Figure out where the PCC file came from that we're processing, so that
	 we can prepend its path onto any LST file references (or PCC refs, for that matter).
				 If the source line in question already has path info, then don't bother
			*/
			String aSource = "";
			int skipSymbol = 0;
			//if the line doesn't use "@" then it's a relative path, so we need to figure out what that path is
			if (aLine.indexOf("@") == -1)
			{
				int separatorLoc = aString.lastIndexOf(File.separator);
				//just in case aString was composed, rather than from a File object, check to see if the "\" character was used
				if (aString.lastIndexOf("\\") > separatorLoc)
					separatorLoc = aString.lastIndexOf("\\");
				aSource = aString.substring(aString.indexOf("data"), separatorLoc + 1);
			}
			//otherwise, we want to use this line almost as-is, we just need to signal the code below to drop the "@"
			else
				skipSymbol = 1;

			if (aLine.startsWith("PCC:"))
				pccFileLines.add(aSource + aLine.substring(4 + skipSymbol));
			else if (aLine.startsWith("RACE:"))
			{
				raceFileLines.add(aSource + aLine.substring(5 + skipSymbol));
			}
			else if (aLine.startsWith("CLASS:"))
			{
				classFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("SKILL:"))
			{
				skillFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("FEAT:"))
			{
				featFileLines.add(aSource + aLine.substring(5 + skipSymbol));
			}
			else if (aLine.startsWith("DOMAIN:"))
			{
				domainFileLines.add(aSource + aLine.substring(7 + skipSymbol));
			}
			else if (aLine.startsWith("DEITY:"))
			{
				deityFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("SPELL:"))
			{
				spellFileLines.add(aSource + aLine.substring(6 + skipSymbol));
			}
			else if (aLine.startsWith("WEAPONPROF:"))
			{
				weaponProfFileLines.add(aSource + aLine.substring(11 + skipSymbol));
			}
			else if (aLine.startsWith("EQUIPMENT:"))
			{
				equipmentFileLines.add(aSource + aLine.substring(10 + skipSymbol));
			}
			else if (aLine.startsWith("LANGUAGE:"))
			{
				languageLines.add(aSource + aLine.substring(9 + skipSymbol));
			}
			else if (aLine.startsWith("CLASSSKILL:"))
			{
				classSkillFileLines.add(aSource + aLine.substring(11 + skipSymbol));
			}
			else if (aLine.startsWith("CLASSSPELL:"))
			{
				classSpellFileLines.add(aSource + aLine.substring(11 + skipSymbol));
			}
			else if (aLine.startsWith("REQSKILL:"))
			{
				reqSkillLines.add(aLine.substring(9));
			}
			else if (aLine.startsWith("TEMPLATE:"))
			{
				templateFileLines.add(aSource + aLine.substring(9 + skipSymbol));
			}
			else if (aLine.startsWith("EQUIPMOD:"))
			{
				equipmentModifierFileLines.add(aSource + aLine.substring(9 + skipSymbol));
			}
			else if (aLine.startsWith("COINS:"))
			{
				// bsmeister 11/21/2001
				// Include and Exclude functionality, when implemented, will
				// be implemented in the DenominationList class itself.
				denominationList.parseFiles(aSource + aLine.substring(6 + skipSymbol));
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid line: " + aLine + " in " + aString, s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	public static boolean addEquipment(Equipment aEq)
	{
		if (getEquipmentKeyed(aEq.getKeyName()) != null)
		{
			return false;
		}

		if (!aEq.isType("CUSTOM"))
		{
			aEq.rawTypeList().add("CUSTOM");
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


	private static ArrayList adds(int lineType, ArrayList aArrayList)
	{
		String aClassName = "";
		for (int i = 0; i < aArrayList.size(); i++)
		{
			switch (lineType)
			{
				case RACE_TYPE:
					Race race = (Race)aArrayList.get(i);
					raceMap.put(race.getKeyName(), race);
					break;
				case CLASS_TYPE:
					PCClass bClass = getClassKeyed(((PCClass)aArrayList.get(i)).getKeyName());
					if (bClass == null)
					{
						classList.add(aArrayList.get(i));
					}
					break;
				case SKILL_TYPE:
					Skill aSkill = getSkillKeyed(((Skill)aArrayList.get(i)).getKeyName());
					if (aSkill == null)
					{
						skillList.add(aArrayList.get(i));
					}
					break;
				case FEAT_TYPE:
					Feat aFeat = getFeatKeyed(((Feat)aArrayList.get(i)).getKeyName());
					if (aFeat == null)
					{
						featList.add(aArrayList.get(i));
					}
					break;
				case DOMAIN_TYPE:
					Domain aDomain = getDomainKeyed(((Domain)aArrayList.get(i)).getKeyName());
					if (aDomain == null)
					{
						domainList.add(aArrayList.get(i));
					}
					break;
				case DEITY_TYPE:
					Deity aDeity = getDeityKeyed(((Deity)aArrayList.get(i)).getKeyName());
					if (aDeity == null)
					{
						deityList.add(aArrayList.get(i));
					}
					break;
				case SPELL_TYPE:
					Spell spell = (Spell)aArrayList.get(i);
					spellMap.put(spell.getKeyName(), spell);
					break;
				case WEAPONPROF_TYPE:
					WeaponProf wp = getWeaponProfKeyed(((WeaponProf)aArrayList.get(i)).getKeyName());
					if (wp == null)
					{
						weaponProfList.add(aArrayList.get(i));
					}
					break;
				case EQUIPMENT_TYPE:
					Equipment eq = getEquipmentKeyed(((Equipment)aArrayList.get(i)).getKeyName());
					if (eq == null)
					{
						equipmentList.add(aArrayList.get(i));
					}
					break;
				case LANGUAGE_TYPE:
					Language lang = getLanguageNamed(((Language)aArrayList.get(i)).getKeyName());
					if (lang == null)
					{
						languageList.add(aArrayList.get(i));
					}
					break;
				case CLASSSKILL_TYPE:
					parseClassSkillFrom((String)aArrayList.get(i));
					break;
				case CLASSSPELL_TYPE:
					aClassName = parseClassSpellFrom((String)aArrayList.get(i), aClassName);
					break;
				case REQSKILL_TYPE:
					String aString = (String)aArrayList.get(i);
					if (aString.equals("ALL") || aString.equals("UNTRAINED"))
					{
						skillReq = aString;
					}
					else
					{
						aSkill = getSkillKeyed(aString);
						if (aSkill != null)
						{
							aSkill.setRequired(true);
						}
					}
					break;
				case TEMPLATE_TYPE:
					PCTemplate aTemplate = getTemplateKeyed(((PCTemplate)aArrayList.get(i)).getKeyName());
					if (aTemplate == null)
					{
						templateList.add(aArrayList.get(i));
					}
					break;

				case EQMODIFIER_TYPE:
					EquipmentModifier aModifier = getModifierKeyed(((EquipmentModifier)aArrayList.get(i)).getKeyName());
					if (aModifier == null)
					{
						modifierList.add(aArrayList.get(i));
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Duplicate equipment modifier: " + aModifier.getName(), s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					break;
			}
		}
		aArrayList.clear();
		return aArrayList;
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
				if (!eq.getModifiersAllowed() || eq.isArmor() && (eq.getAcMod().intValue() == 0) && ((eqMod != null) && !eqMod.getName().equalsIgnoreCase("MASTERWORK")))
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
					newType = "CUSTOM";
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
	private static void autoGenerateEquipment()
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
//				if (!race.isType("HUMANOID"))
//				{
//System.out.println("Non-humanoid race: " + race.getName());
//					flag = 2;
//				}
				gensizes[iSize] |= flag;
			}


			for (int i = equipmentList.size() - 1; i >= 0; i--)
			{
				final Equipment eq = (Equipment)equipmentList.get(i);
				//
				// Only apply to Armor and Shield
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || !eq.isArmor() && !eq.isShield())
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
//					if ((gensizes[j] & 0x02) != 0)
//					{
//						createItem(eq, j);
//					}
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
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || !eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon())
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
					if (eq.isMagic() || !eq.isMasterwork() || !eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon())
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
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || !eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon())
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
		sortPObjectList(equipmentList);		// Sort the equipment list
	}

	private static void parseClassSkillFrom(String aLine)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		String className = aTok.nextToken();
		PCClass aClass = getClassKeyed(className);
		String aName = className;
		if (aClass != null)
			aName = aClass.getKeyName();
		if (aTok.hasMoreTokens())
		{
			className = aTok.nextToken();
			aTok = new StringTokenizer(className, "|", false);
			String aString = null;
			Skill aSkill = null;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				final String aStringParen = aString + "(";
				aSkill = getSkillKeyed(aString);
				if (aSkill != null)
				{
					aSkill.getClassList().add(aName);
				}
				else
				{
					Skill bSkill = null;
					for (Iterator e = skillList.iterator(); e.hasNext();)
					{
						bSkill = (Skill)e.next();
						if (bSkill.getKeyName().startsWith(aStringParen))
						{
							bSkill.getClassList().add(aName);
						}
					}
				}
			}
		}
	}

	private static String parseClassSpellFrom(String aLine, String aName)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, "\t", false);
		String aString = aTok.nextToken();
		if (aString.startsWith("CLASS"))
		{
			aName = "";
			if (aString.length() > 6)
				aName = aString.substring(6);

			// first look for an actual class

			PObject aClass = (PObject)getClassKeyed(aName);

			// then look for a domain

			if (aClass == null)
				aClass = (PObject)getDomainKeyed(aName);

			// if it's not one of those, leave it since it might be a subclass

			if (aClass != null)
				aName = aClass.getKeyName();
		}
		else if (aTok.hasMoreTokens())
		{
			final int level = Integer.parseInt(aString);
			final String bString = aTok.nextToken();
			final String postFix = aName + "," + level;
			aTok = new StringTokenizer(bString, "|", false);
			while (aTok.hasMoreTokens())
			{
				Spell aSpell = getSpellKeyed(aTok.nextToken().trim());
				if (aSpell != null)
				{
					final String levels = aSpell.getClassLevels();
					if (levels.length() > 0)
					{
						aSpell.setClassLevels(levels + ",");
					}
					aSpell.setClassLevels(aSpell.getClassLevels() + postFix);
				}
			}
		}
		return aName;
	}


	public static void loadAttributeNames()
	{
		String game;
		if (isStarWarsMode())
		{
			game = "StarWars";
		}
		else if (isWeirdWarsMode())
		{
			game = "WeirdWars";
		}
		else if (isDeadlandsMode())
		{
			game = "Deadlands";
		}
		else if (isSidewinderMode())
		{
			game = "Sidewinder";
		}
		else if (isHackMasterMode())
		{
			game = "HackMaster";
		}
		else
		{
			setDndMode(true);
			game = "D&D";
		}
		initFile(System.getProperty("user.dir") + "\\system\\gameModes" + File.separator + game + ".lst", STATNAME_TYPE, new ArrayList());
		createEmptyRace();
	}



	///////////////////////////////////////////////////////////////////

	// Options



	/**
	 * Opens the options.ini file and calls {@link #getOptionsFromProperties}.
	 *
	 * @return  the <code>Dimension</code> from <code>getOptionsFromProperties</code>
	 */
	public static Dimension readOptionsProperties()
	{
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
	static Dimension getOptionsFromProperties()
	{
		Dimension d = new Dimension(0, 0);
		pcgPath = new File(options.getProperty("pcgen.files.characters", defaultPcgPath));
		templatePath = new File(options.getProperty("pcgen.files.templates", defaultPath + File.separator + "templates"));
		htmlOutputPath = new File(options.getProperty("pcgen.files.htmlOutput", defaultPcgPath));
		String tempBrowserPath = options.getProperty("pcgen.options.browserPath", "");
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
		chosenCampaignSourcefiles = new ArrayList(split(options.getProperty("pcgen.files.chosenCampaignSourcefiles", ""), ','));
		pccFilesLocation = options.getProperty("pcgen.options.pccFilesLocation", System.getProperty("user.dir") + File.separator + "data");
		dmNotes = options.getProperty("pcgen.options.dmnotes", "");
		purchaseStatMode = options.getProperty("pcgen.options.purchaseStatMode", "false").equals("true") ? true : false;
		useMonsterDefault = options.getProperty("pcgen.options.useMonsterDefault", "false").equals("true") ? true : false;
		ignoreLevelCap = options.getProperty("pcgen.options.ignoreLevelCap", "false").equals("true") ? true : false;
		starWarsMode = options.getProperty("pcgen.options.starWarsMode", "false").equals("true") ? true : false;
		dndMode = options.getProperty("pcgen.options.dndMode", "false").equals("true") ? true : false;

		//wheelMode = options.getProperty("pcgen.options.wheelMode", "false").equals("true") ? true : false;
		deadlandsMode = options.getProperty("pcgen.options.deadlandsMode", "false").equals("true") ? true : false;
		weirdWarsMode = options.getProperty("pcgen.options.weirdWarsMode", "false").equals("true") ? true : false;
		//l5rMode = options.getProperty("pcgen.options.l5rMode", "false").equals("true") ? true : false;
		sidewinderMode = options.getProperty("pcgen.options.sidewinderMode", "false").equals("true") ? true : false;
		hackMasterMode = options.getProperty("pcgen.options.hackMasterMode", "false").equals("true") ? true : false;

		loadAttributeNames();

		unlimitedStatPool = options.getProperty("pcgen.options.unlimitedStatPool", "false").equals("true") ? true : false;
		ignoreEquipmentCost = options.getProperty("pcgen.options.ignoreEquipmentCost", "false").equals("true") ? true : false;
		grimHPMode = options.getProperty("pcgen.options.grimHPMode", "false").equals("true") ? true : false;
		grittyACMode = options.getProperty("pcgen.options.grittyACMode", "false").equals("true") ? true : false;
		includeSkills = Integer.parseInt(options.getProperty("pcgen.options.includeSkills", "0"));
		excSkillCost = Integer.parseInt(options.getProperty("pcgen.options.excSkillCost", "0"));
		intCrossClassSkillCost = Integer.parseInt(options.getProperty("pcgen.options.intCrossClassSkillCost", "2"));
		initialStatMin = Integer.parseInt(options.getProperty("pcgen.options.initialStatMin", "3"));
		initialStatMax = Integer.parseInt(options.getProperty("pcgen.options.initialStatMax", "18"));
		loadCampaignsAtStart = options.getProperty("pcgen.options.loadCampaignsAtStart", "false").equals("true") ? true : false;
		tabPlacement = Integer.parseInt(options.getProperty("pcgen.options.tabPlacement", "2"));
		chaTabPlacement = Integer.parseInt(options.getProperty("pcgen.options.chaTabPlacement", "0"));
		rollMethod = Integer.parseInt(options.getProperty("pcgen.options.rollMethod", "1"));
		looknFeel = Integer.parseInt(options.getProperty("pcgen.options.looknFeel", "0"));
		skinLFThemePack = options.getProperty("pcgen.options.skinLFThemePack", "");

		autoFeatsRefundable = options.getProperty("pcgen.options.autoFeatsRefundable", "false").equals("true") ? true : false;
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			statCost[i] = Integer.parseInt(options.getProperty("pcgen.options.statCost." + statValue, String.valueOf(statCost[i])));
		}
		hpMaxAtFirstLevel = options.getProperty("pcgen.options.hpMaxAtFirstLevel", "true").equals("true") ? true : false;
		hpRollMethod = Integer.parseInt(options.getProperty("pcgen.options.hpRollMethod", String.valueOf(Globals.s_HP_STANDARD)));
		skillIncrementBefore = options.getProperty("pcgen.options.skillIncrementBefore", "true").equals("true") ? true : false;
		toolTipTextShown = options.getProperty("pcgen.options.toolTipTextShown", "true").equals("true") ? true : false;
		previewTabShown = options.getProperty("pcgen.options.previewTabShown", "true").equals("true") ? true : false;
		Globals.setLeftUpperCorner(new Point(new Double(options.getProperty("pcgen.options.windowLeftUpperCorner.X", "-1.0")).intValue(), new Double(options.getProperty("pcgen.options.windowLeftUpperCorner.Y", "-1.0")).intValue()));
		Double dw = new Double(options.getProperty("pcgen.options.windowWidth", "0"));
		Double dh = new Double(options.getProperty("pcgen.options.windowHeight", "0"));
		if (dw.doubleValue() != 0.0 && dh.doubleValue() != 0.0)
		{
			int width = Integer.parseInt(dw.toString().substring(0, Math.min(dw.toString().length(), dw.toString().lastIndexOf("."))));
			int height = Integer.parseInt(dh.toString().substring(0, Math.min(dh.toString().length(), dh.toString().lastIndexOf("."))));
			d = new Dimension(width, height);
		}

		prereqFailColor = Integer.decode(options.getProperty("pcgen.options.prereqFailColor", "0")).intValue();

		autogenMasterwork = options.getProperty("pcgen.options.autogenMasterwork", "true").equals("true") ? true : false;
		autogenMagic = options.getProperty("pcgen.options.autogenMagic", "true").equals("true") ? true : false;
		autogenRacial = options.getProperty("pcgen.options.autogenRacial", "true").equals("true") ? true : false;
		autogenExoticMaterial = options.getProperty("pcgen.options.autogenExoticMaterial", "true").equals("true") ? true : false;


		String userDir = options.getProperty("pcgen.options.userdir");
		if (userDir != null && userDir.length() > 0)
			System.setProperty("user.dir", userDir);
		return d;
	}

	/**
	 * Opens (options.ini) for writing and calls {@link #setOptionsProperties}.
	 */
	public static void writeOptionsProperties()
	{
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
	static void setOptionsProperties()
	{
		options.setProperty("pcgen.files.characters", pcgPath.getAbsolutePath());
		options.setProperty("pcgen.files.templates", templatePath.getAbsolutePath());
		options.setProperty("pcgen.files.htmlOutput", htmlOutputPath.getAbsolutePath());
		options.setProperty("pcgen.files.selectedTemplate", selectedTemplate);
		options.setProperty("pcgen.files.selectedPartyTemplate", selectedPartyTemplate);
		options.setProperty("pcgen.files.chosenCampaignSourcefiles", unSplit(chosenCampaignSourcefiles, ','));
		if (browserPath != null)
		{
			options.setProperty("pcgen.options.browserPath", browserPath.getAbsolutePath());
		}
		else
		{
			options.setProperty("pcgen.options.browserPath", "");
		}

		options.setProperty("pcgen.options.purchaseStatMode", purchaseStatMode ? "true" : "false");
		options.setProperty("pcgen.options.useMonsterDefault", useMonsterDefault ? "true" : "false");
		options.setProperty("pcgen.options.ignoreLevelCap", ignoreLevelCap ? "true" : "false");
		options.setProperty("pcgen.options.starWarsMode", starWarsMode ? "true" : "false");
		options.setProperty("pcgen.options.weirdWarsMode", weirdWarsMode ? "true" : "false");
		options.setProperty("pcgen.options.deadlandsMode", deadlandsMode ? "true" : "false");
		options.setProperty("pcgen.options.sidewinderMode", sidewinderMode ? "true" : "false");
		options.setProperty("pcgen.options.hackMasterMode", hackMasterMode ? "true" : "false");

		//options.setProperty("pcgen.options.l5rMode", l5rMode ? "true" : "false");

		//options.setProperty("pcgen.options.wheelMode", wheelMode ? "true" : "false");

		options.setProperty("pcgen.options.dndMode", dndMode ? "true" : "false");
		options.setProperty("pcgen.options.unlimitedStatPool", unlimitedStatPool ? "true" : "false");
		options.setProperty("pcgen.options.ignoreEquipmentCost", ignoreEquipmentCost ? "true" : "false");
		options.setProperty("pcgen.options.grimHPMode", grimHPMode ? "true" : "false");
		options.setProperty("pcgen.options.grittyACMode", grittyACMode ? "true" : "false");
		options.setProperty("pcgen.options.includeSkills", String.valueOf(includeSkills));
		options.setProperty("pcgen.options.excSkillCost", String.valueOf(excSkillCost));
		options.setProperty("pcgen.options.intCrossClassSkillCost", String.valueOf(intCrossClassSkillCost));
		options.setProperty("pcgen.options.initialStatMin", String.valueOf(initialStatMin));
		options.setProperty("pcgen.options.initialStatMax", String.valueOf(initialStatMax));
		options.setProperty("pcgen.options.loadCampaignsAtStart", loadCampaignsAtStart ? "true" : "false");
		options.setProperty("pcgen.options.chaTabPlacement", String.valueOf(chaTabPlacement));
		options.setProperty("pcgen.options.tabPlacement", String.valueOf(tabPlacement));
		options.setProperty("pcgen.options.looknFeel", String.valueOf(looknFeel));
		options.setProperty("pcgen.options.skinLFThemePack", skinLFThemePack);
		options.setProperty("pcgen.options.autoFeatsRefundable", autoFeatsRefundable ? "true" : "false");
		options.setProperty("pcgen.options.pccFilesLocation", pccFilesLocation);
		options.setProperty("pcgen.options.dmnotes", dmNotes);

		options.setProperty("pcgen.options.rollMethod", String.valueOf(rollMethod));
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			options.setProperty("pcgen.options.statCost." + statValue, String.valueOf(statCost[i]));
		}
		options.setProperty("pcgen.options.hpMaxAtFirstLevel", hpMaxAtFirstLevel ? "true" : "false");
		options.setProperty("pcgen.options.hpRollMethod", String.valueOf(hpRollMethod));
		options.setProperty("pcgen.options.skillIncrementBefore", skillIncrementBefore ? "true" : "false");
		options.setProperty("pcgen.options.prereqFailColor", "0x" + Integer.toHexString(prereqFailColor));

		options.setProperty("pcgen.options.autogenMasterwork", autogenMasterwork  ? "true" : "false");
		options.setProperty("pcgen.options.autogenMagic", autogenMagic  ? "true" : "false");
		options.setProperty("pcgen.options.autogenRacial", autogenRacial  ? "true" : "false");
		options.setProperty("pcgen.options.autogenExoticMaterial", autogenExoticMaterial  ? "true" : "false");

		options.setProperty("pcgen.options.toolTipTextShown", toolTipTextShown ? "true" : "false");
		options.setProperty("pcgen.options.previewTabShown", previewTabShown ? "true" : "false");

		if (Globals.getLeftUpperCorner() != null)
		{
			options.setProperty("pcgen.options.windowLeftUpperCorner.X", String.valueOf(Globals.getLeftUpperCorner().getX()));
			options.setProperty("pcgen.options.windowLeftUpperCorner.Y", String.valueOf(Globals.getLeftUpperCorner().getY()));
		}
		options.setProperty("pcgen.options.windowWidth", String.valueOf(rootFrame.getSize().getWidth()));
		options.setProperty("pcgen.options.windowHeight", String.valueOf(rootFrame.getSize().getHeight()));
		options.setProperty("pcgen.options.userdir", System.getProperty("user.dir"));
	}


	/**
	 *  Turn a 'separator' separated string into a ArrayList of strings, each
	 *  corresponding to one trimmed 'separator'-separated portion of the original
	 *  string.
	 *
	 * @param  aString    The string to be split
	 * @param  separator  The separator that separates the string.
	 * @return            an ArrayList of Strings
	 */
	private static ArrayList split(String aString, char separator)
	{
		int elems = 1;
		ArrayList result = null;
		int beginIndex = 0;
		int endIndex = 0;

		if (aString.trim().length() == 0)
		{
			return new ArrayList(0);
		}

		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == separator)
			{
				elems++;
			}
		}
		result = new ArrayList(elems);
		for (int i = 0; i < elems; i++)
		{
			endIndex = aString.indexOf(separator, beginIndex);
			if (endIndex == -1)
			{
				endIndex = aString.length();
			}
			result.add(aString.substring(beginIndex, endIndex).trim());

			// Skip separator

			beginIndex = endIndex + 1;
		}
		return result;
	}

	/**
	 *  Reverses the work of split()
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	private static String unSplit(ArrayList strings, char separator)
	{
		StringBuffer result = new StringBuffer(strings.size() * 20); //Better than 16, which is default...
		Iterator iter = strings.iterator();
		while (iter.hasNext())
		{
			String element = (String)iter.next();
			result.append(element);
			if (iter.hasNext())
			{
				result.append(separator).append(" ");
			}
		}
		return result.toString();
	}

	public static String replaceString(String in, String find, String newStr)
	{
		final char[] working = in.toCharArray();
		StringBuffer sb = new StringBuffer(in.length() + newStr.length());
		int startindex = in.indexOf(find);
		if (startindex < 0) return in;
		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; i++)
			{
				sb.append(working[i]);
			}
			currindex = startindex;
			sb.append(newStr);
			currindex += find.length();
			startindex = in.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; i++)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	public static Equipment getEquipmentFromName(String baseName)
	{
		ArrayList modList = new ArrayList();
		ArrayList namList = new ArrayList();
		Equipment eq = null;
		String aName = baseName;
		int i = aName.indexOf('(');

		if (i >= 0)
		{
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i + 1), "/)", false);
			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();
				modList.add(cString);
			}
			aName = aName.substring(0, i).trim();
		}

		//
		// Try removing the last reference to size (if any)
		//
		// This will get non-magical, resized items and unique magic, resized items (eg. Sunswords)
		//
		StringBuffer newName = new StringBuffer(80);
		boolean bSized = false;
		int iSize = SIZE_M;
		for (int idx = modList.size() - 1; idx >= 0; idx--)
		{
			final String aMod = (String)modList.get(idx);
			if (!bSized)
			{
				for (iSize = SIZE_F; iSize <= SIZE_C; iSize++)
				{
					if (aMod.equalsIgnoreCase(s_SIZELONG[iSize]))
					{
						break;
					}
				}
				if (iSize <= SIZE_C)
				{
					bSized = true;
					continue;
				}
			}
			if (newName.length() != 0)
			{
				newName.append('/');
			}
			newName.append(aMod);
		}
		if (bSized)
		{
			if (newName.length() != 0)
			{
				newName.insert(0, " (").append(')');
			}
			eq = getEquipmentKeyed(aName + newName.toString());
			if (eq != null)
			{
				return createItem(eq, iSize);
			}
		}

		//
		// Separate the descriptors int 2 ArrayLists. One containing those descriptors
		// whose names match a modifier name, and the other containing those descriptors which are not
		// possibly modifiers (because they're not in the modifier list).
		//
		if (i >= 0)
		{
			for (i = modList.size() - 1; i >= 0; i--)
			{
				final String cString = (String)modList.get(i);
				if (cString != null && getModifierNamed(cString) == null)
				{
					namList.add(0, cString);	// add to the start as otherwise the list will be reversed
					modList.remove(i);
				}
			}
		}

		//
		// Look for magic (or mighty) bonuses
		//
		i = aName.indexOf('+');
		if (i >= 0)
		{
			int[] bonuses = null;
			int bonusCount = 0;

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

			//
			// Mighty bows suffered a (much-needed) renaming between 2.3.5 and 2.3.6
			// (Long|Short)bow +n (Mighty/Composite) --> (Long|Short)bow (+n Mighty/Composite)
			// (Long|Short)bow +x/+n (Mighty/Composite) --> (Long|Short)bow +x (+n Mighty/Composite)
			//
			for (int idx1 = 0; idx1 < modList.size(); idx1++)
			{
				String aMod = (String)modList.get(idx1);
				if (aMod.equalsIgnoreCase("MIGHTY"))
				{
					if (bonusCount != 0)
					{
						aMod = Delta.toString(bonuses[bonusCount - 1]) + " " + aMod;
						modList.set(idx1, aMod);
						bonusCount -= 1;
					}
				}
			}
		}

		//
		// aName   : name of item minus all descriptors held in () as well as any bonuses
		// namList : list of all descriptors which cannot be modifiers
		// modList : list of all descriptors which *might* be modifiers
		// bonuses : list of all numerical bonuses (bonusCount of them)
		//



		//
		// Try the name with the non-modifier descriptors attached
		//
		newName = new StringBuffer(80);
		if (namList.size() != 0)
		{
			newName.append(" (");
			for (Iterator e = namList.iterator(); e.hasNext();)
			{
				if (newName.length() > 2)
				{
					newName.append('/');
				}
				newName.append((String)e.next());
			}
			newName.append(')');
		}
		eq = getEquipmentKeyed(aName + newName.toString());
		if (eq != null)
		{
			boolean bModified = false;
			eq = (Equipment)eq.clone();
			// Now attempt to add all the modifiers.
			for (Iterator e = modList.iterator(); e.hasNext();)
			{
				final String modName = (String)e.next();
				final EquipmentModifier eqMod = getQualifiedModifierNamed(modName, eq);
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
					System.out.println("Could not find a qualified modifier named: " + modName + " for " + eq.getName() + ":" + eq.typeList());
				}
			}
			if (bModified)
			{
				eq.typeList().add("CUSTOM");
				equipmentList.add(eq);
			}
		}

		//
		// If not found, try the name again, this time omitting the last reference to size
		//
		if (eq == null)
		{
//System.out.println("not found: " + aName + newName.toString());
			newName = new StringBuffer(80);
			bSized = false;
			for (int idx = namList.size() - 1; idx >= 0; idx--)
			{
				final String aMod = (String)namList.get(idx);
				if (!bSized)
				{
					for (iSize = SIZE_F; iSize <= SIZE_C; iSize++)
					{
						if (aMod.equalsIgnoreCase(s_SIZELONG[iSize]))
						{
							break;
						}
					}
					if (iSize <= SIZE_C)
					{
						bSized = true;
						continue;
					}
				}
				if (newName.length() != 0)
				{
					newName.append('/');
				}
				newName.append(aMod);
			}
			if (bSized)
			{
				if (newName.length() != 0)
				{
					newName.insert(0, " (").append(')');
				}
				eq = getEquipmentKeyed(aName + newName.toString());
//if (eq == null)
//{
//System.out.println("not found: " + aName + newName.toString());
//}
			}
		}


		//
		// Try brute force. Find an equipment entry that starts the same as this item and then try to
		// coerce the item to match
		//
//		if (eq == null)
//		{
//			final String baseKey = aName.toUpperCase();
//			for (Iterator e = equipmentList.iterator(); e.hasNext();)
//			{
//				final Equipment eqm = (Equipment)e.next();
//				final String eqKey = eqm.getKeyName().toUpperCase();
//				if (eqKey.startsWith(baseKey))
//				{
//System.out.println( "Possible match: " + eqm.getKeyName() );
//				}
//			}
//		}


//		if (eq == null)
//			System.out.println(aName + ":" + namList + ":" + modList + ":" + bonuses);
		return eq;
	}

	/*
	* Reduce/increase damage for modified size as per DMG p.162
	*/
	public static String adjustDamage(String aDamage, String sBaseSize, String sNewSize)
	{
		final int baseSize = sizeInt(sBaseSize);
		int itemSize = sizeInt(sNewSize);
		rollInfo aRollInfo = getDiceInfo(aDamage);
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
						System.out.println("Unknown die size: " + aRollInfo.sides);
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
						System.out.println("Unknown die size: " + Integer.toString(aRollInfo.sides));
						return aDamage;
				}
				itemSize += 1;
			}
			retValue = Integer.toString(aRollInfo.times) + "d" + Integer.toString(aRollInfo.sides);
		}
		return retValue;
	}

	public static class rollInfo
	{
		int times = 0;
		int sides = 0;
		int modifier = 0;
	}

	public static rollInfo getDiceInfo(String aRollInfo)
	{
		rollInfo aInfo = new rollInfo();
		StringTokenizer st = new StringTokenizer(aRollInfo, "dD +", false);
		aInfo.modifier = 0;
		try
		{
			aInfo.times = Integer.parseInt(st.nextToken());
			if (st.hasMoreTokens())
			{
				aInfo.sides = Integer.parseInt(st.nextToken());
				if (st.hasMoreTokens())
				{
					aInfo.modifier = Integer.parseInt(st.nextToken());
				}
			}
			else
			{
				aInfo.sides = 1;
			}
			return aInfo;
		}
		catch (Exception e)
		{
			return null;
		}
	}
}

