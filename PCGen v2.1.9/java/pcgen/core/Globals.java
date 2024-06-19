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
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


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

	/** An empty race. Duh. */
	public final static Race s_EMPTYRACE = new Race();

	/** How to roll hitpoints. */
	public static final int s_HP_STANDARD = 0;
	public static final int s_HP_AUTOMAX = 1;
	public static final int s_HP_PERCENTAGE = 2;
	public static final int s_HP_RPGA = 3;

	/** Encumberance Constants */
	public static final int LIGHT_LOAD = 0;
	public static final int MEDIUM_LOAD = 1;
	public static final int HEAVY_LOAD = 2;
	public static final int OVER_LOAD = 3;

	/** Static Constants */
	public static final int STRENGTH = 0;
	public static final int DEXTERITY = 1;
	public static final int CONSTITUTION = 2;
	public static final int INTELLIGENCE = 3;
	public static final int WISDOM = 4;
	public static final int CHARISMA = 5;

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
	private static TreeSet languageList = new TreeSet();
	private static ArrayList colorList = new ArrayList();
	private static ArrayList traitList = new ArrayList();
	private static ArrayList equipmentList = new ArrayList();
	private static ArrayList specialsList = new ArrayList();
	private static ArrayList loadStrings = new ArrayList();
	private static ArrayList campaignList = new ArrayList();
	private static ArrayList XPList = new ArrayList();
	private static TreeSet weaponTypes = new TreeSet();
	private static PlayerCharacter currentPC = null;

	private static int hpPct = 100;
	private static String currentFile = "";
	private static int lineNum = 0;
	private static int excSkillCost = 0;
	private static int intCrossClassSkillCost = 2;
	private static boolean boolBypassMaxSkillRank = false;
	private static boolean debugMode = false;
	private static Point leftUpperCorner = null;
	private static String dmNotes = "";

	/**
	 * This is <code>true</code> when the campaign data structures are
	 * sorted.
	 */
	private static boolean d_sorted = false;

	/**
	 *  This will be overridden at startup with values read from properties.
	 */
	private static int[] statCost = new int[]
	{

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
	private static int[] dieSizes = new int[]
	{

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
	private static boolean starWarsMode = false;
	private static boolean dndMode = true;
	private static boolean weirdWarsMode = false;
	private static boolean deadlandsMode = false;
	private static boolean l5rMode = false;
	private static boolean wheelMode = false;
	private static boolean ignoreLevelCap = false;
	private static boolean purchaseStatMode = false;
	private static int initialStatMin = 3;
	private static int initialStatMax = 18;
	private static boolean ignoreEquipmentCost = false;
	private static boolean unlimitedStatPool = false;
	/**
	 *  0==None, 1=Untrained, 2=all
	 */
	private static int includeSkills = 1;
	private static boolean grimHPMode = false;
	private static boolean grittyACMode = false;
	private static boolean loadCampaignsAtStart = false;
	/**
	 *  0=top 1=left  2=bottom
	 */
	private static int tabPlacement = 0;
	private static int chaTabPlacement = 0;

	/*0=UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
  *1=UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  */

	private static int looknFeel = 0;
	/**
	 * Method:
	 * 0: One random number
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 */
	private static int rollMethod = 1;

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

	private static boolean toolTipTextShown = true;
	private static boolean previewTabShown = false;
	private static ArrayList chosenCampaignSourcefiles = new ArrayList();
	private static boolean hpMaxAtFirstLevel = true;
	private static int hpRollMethod = s_HP_STANDARD;
	private static HashMap loadedFiles = new HashMap();
	private static String skillReq = "";
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
	private static JFrame rootFrame;

	private static String defaultPath = System.getProperty("user.dir");
	private static File templatePath = new File(defaultPath + File.separator + "templates");
	private static File pcgPath = new File(defaultPath);
	private static File htmlOutputPath = new File(defaultPath);
	/** That browserPath is set to null is intentional. */
	private static File browserPath = null; //Intentional null
	private static String selectedTemplate = templatePath.getAbsolutePath() + File.separator + "csheet.htm";
	private static String selectedPartyTemplate = templatePath.getAbsolutePath() + File.separator + "psheet.htm";
	private static SplashScreen splash;
	private static Properties options = new Properties();
	private static boolean autoFeatsRefundable = false;

	public static int[] getStatCost()
	{
		return statCost;
	}

	public static int[] getDieSizes()
	{
		return dieSizes;
	}

	public static void setStatCost(int[] statCost)
	{
		Globals.statCost = statCost;
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

	public static int getRollMethod()
	{
		return rollMethod;
	}

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

	public static ArrayList getFeatList()
	{
		return featList;
	}

	public static ArrayList getDomainList()
	{
		return domainList;
	}

	public static ArrayList getDeityList()
	{
		return deityList;
	}

	public static ArrayList getPcList()
	{
		return pcList;
	}

	public static Map getSpellMap()
	{
		return spellMap;
	}

/*	public static ArrayList getSpellList()
	{
		return spellList;
	}
	*/

	public static ArrayList getWeaponProfList()
	{
		return weaponProfList;
	}

	public static ArrayList getSchoolsList()
	{
		return schoolsList;
	}

	public static TreeSet getLanguageSet()
	{
		return languageList;
	}

	public static ArrayList getColorList()
	{
		return colorList;
	}

	public static ArrayList getTraitList()
	{
		return traitList;
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

	/** Returns whether 'automatic' class-granted feats can be turned in for other feats
	 *	@return	true if 'automatic' class-granted feats can be turned in for other feats
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
		if (loadStrings.size() == 0)
		{
			return 1.0;
		}
		String bString = (String)loadStrings.get(0);
		StringTokenizer aTok = new StringTokenizer(bString, ",", false);
		String cString = null;
		while (aTok.hasMoreTokens())
		{
			cString = aTok.nextToken();
			if (cString.substring(0, 1).equals(aString))
			{
				return new Float(cString.substring(cString.lastIndexOf('|') + 1)).doubleValue();
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


	public static Equipment getEquipmentNamed(String name)
	{
		Equipment eq = null;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			eq = (Equipment)e.next();
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
		classList.clear();
		skillList.clear();
		featList.clear();
		equipmentList.clear();
		weaponProfList.clear();
		templateList.clear();
		deityList.clear();
		domainList.clear();
		languageList.clear();
		spellMap.clear();
		skillReq = "";
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
	 *   12 - special ability,
	 *   13 - carrying load,
	 *   14, 16, 17, 18 - text file,
	 *   15 - campaign,
	 *   20 - pipe ("|") separated list.
	 *   21 - template
	 *   22 - CR to XP conversion values
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
			while (newlineStr.hasMoreTokens())
			{
				aLine = newlineStr.nextToken();
				++lineNum;
				if (aLine.startsWith("#"))
				{
					continue;
				}
				switch (fileType)
				{
					case 0:
						anObj = new Race();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 1:
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
					case 2:
						anObj = new Skill();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 3:
						anObj = new Feat();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 4:
						anObj = new Domain();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 5:
						anObj = new Deity();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 6:
						anObj = new Spell();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 7:
						anObj = new WeaponProf();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 8:
						schoolsList.add(aLine);
						break;
					case 9:
						colorList.add(aLine);
						break;
					case 10:
						traitList.add(aLine);
						break;
					case 11:
						anObj = new Equipment();
						anObj.parseLine(aLine, aFile, lineNum);
						aList.add(anObj);
						break;
					case 12:
						SpecialAbility sp = new SpecialAbility();
						sp.parseLine(aLine, aFile, lineNum);
						specialsList.add(sp);
						break;
					case 13:
						loadStrings.add(aLine);
						break;
					case 14:
					case 16:
					case 17:
					case 18:
						aList.add(aLine);
						break;
					case 15:
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

							/*
               * if (aString.startsWith("SM:"))
               * {
               * swCheckBox.setChecked(aString.endsWith("Y"));
               * swCheckBox_click(null, null);
               * }
               * else if (aString.startsWith("PM:"))
               * purchaseCheckBox.setChecked(aString.endsWith("Y"));
               * else if (aString.startsWith("PC:"))
               * {
               * StringTokenizer bTok = new StringTokenizer(aString.substring(3),":",false);
               * int count=0;
               * while (bTok.hasMoreTokens() && count<11)
               * pointPurchaseList.getItem(count++).setText(bTok.nextToken());
               * }
               * else
               */

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

							/*
               * else if (aString.substring(1,2).equals(":") && campaignList.contains(aString.substring(2)))
               * {
               * selectedCampaignsList.add(aString.substring(2));
               * int i = campaignList.indexOf(aString.substring(2));
               * if (i>-1) {
               * String bString = (String)campaignNames.get(i);
               * for(int count=0;count<campaignListBox.getItemCount();count++)
               * if (campaignListBox.getItem(count).toString().equals(bString))
               * {
               * campaignListBox.setSelectedIndex(count);
               * break;
               * }
               * }
               * }
               */
						}
						break;
					case 21:
						anObj = new PCTemplate();

						anObj.parseLine(aLine, aFile, lineNum);

						aList.add(anObj);

						break;
					case 22:
						XPList.add(aLine);
						break;
				}
				if (((fileType >= 0 && fileType <= 7) || fileType == 11 || fileType == 15) && anObj != null)
				{
					anObj.setSourceFile(aFile.getAbsolutePath());
				}

				/*       if (fileType == 15 && rowNum == 2)

               {

                 break;

               }	*/
			}
			aStream.close();
		}
		catch (Exception exception)
		{
			if (!fileName.equals("pcgen.ini"))
			{
				System.out.println("ERROR:" + fileName + " error " + aLine +
					" Exception type:" +
					exception.getClass().getName() + " Message:" +
					exception.getMessage());
			}
		}
		return aList;
	}

	public static int xPLevelOffset(int level)
	{
		int offset = 0;
		StringTokenizer aTok = new StringTokenizer((String)XPList.get(level), ",", false);
		if (aTok.hasMoreTokens())
			offset = Integer.parseInt(aTok.nextToken().trim());
		return offset;
	}

	public static int xPLevelValue(int level, int cr)
	{
		int i = 0;
		int xp = 666;
		StringTokenizer aTok = new StringTokenizer((String)XPList.get(level), ",", false);
		while (aTok.hasMoreTokens() && i++ < cr + 1)
		{
			xp = Integer.parseInt(aTok.nextToken().trim());
		}
		return xp;
	}

	public static Float maxLoadForStrengthAndSize(int strength, String size)
	{
		double x = 0.0;
		double dbl = 0.0;
		int y = strength;
		int loadStringSize = loadStrings.size();
		if (strength >= loadStringSize - 2)	//ok
		{
			String bString = (String)loadStrings.get(loadStringSize - 1);	//ok
			dbl = new Float(bString.substring(bString.lastIndexOf('\t') + 1)).doubleValue();	//ok
			for (y = strength; y >= loadStringSize - 2; y -= 10)
			{
				x += 1.0;
			}
		}
		String aString = (String)loadStrings.get(y);
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
		Object[] pobjArray = (Object[])aList.toArray();
		int lower = 0;
		int upper = pobjArray.length;

		// always one passed last possible match

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
	 *  This method will find PObject by key name in a sorted vector of PObjects.
	 *  The vector must be sorted by key name.
	 *
	 * @param  aList    a vector of PObject objects.
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
			Object[] pobjArray = (Object[])aList.toArray();
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


	public static String stringForList(Iterator e, String delim)
	{
		StringBuffer aStrBuf = new StringBuffer();
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
		if (raceMap.size() == 0 || classList.size() == 0 || skillList.size() == 0 ||
			featList.size() == 0 || equipmentList.size() == 0 || weaponProfList.size() == 0)
		{
			return false;
		}
		return true;
	}


	public static void loadCampaigns(ArrayList aSelectedCampaignsList)
	{
		d_sorted = false;
		if (aSelectedCampaignsList.size() == 0)
		{
			JOptionPane.showMessageDialog(null, "You must select at least one campaign to load.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}

		//Add empty race

		if (!Globals.getRaceMap().containsKey(s_NONESELECTED))
		{
			s_EMPTYRACE.setName(Globals.s_NONESELECTED);
			Globals.getRaceMap().put(s_NONESELECTED, s_EMPTYRACE);
		}

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
		for (int lineType = 0; lineType < 22; lineType++)
		{
			lineList = null;
			bArrayList = new ArrayList();
			switch (lineType)
			{
				case 0:
					lineList = raceFileLines;
					break;
				case 1:
					lineList = classFileLines;
					break;
				case 2:
					lineList = skillFileLines;
					break;
				case 3:
					lineList = featFileLines;
					break;
				case 4:
					lineList = domainFileLines;
					break;
				case 5:
					lineList = deityFileLines;
					break;
				case 6:
					lineList = spellFileLines;
					break;
				case 7:
					lineList = weaponProfFileLines;
					break;
				case 8:
					continue;
				case 9:
					continue;
				case 10:
					continue;
				case 11:
					lineList = equipmentFileLines;
					break;
				case 12:
					lineList = languageLines;
					break;
				case 13:
					continue;
				case 14:
					continue;
				case 15:
					continue;
				case 16:
					lineList = classSkillFileLines;
					break;
				case 17:
					lineList = classSpellFileLines;
					break;
				case 18:
					lineList = reqSkillLines;
					break;
				case 19:
					continue;
				case 20:
					continue;
				case 21:
					lineList = templateFileLines;

					break;

				default:
					lineList = null;
					System.out.println("Campaign list corrupt at line: " + i + " no such lineType exists. Stopped parsing campaigns, but not aborting program.");
					return;
			}
			String aLine = null;
			StringTokenizer lineTokenizer = null;
			ArrayList cArrayList = null;
			String fileName = null;
			for (Iterator lineIter = lineList.iterator(); lineIter.hasNext();)
			{
				aLine = (String)lineIter.next();
				lineTokenizer = new StringTokenizer(aLine, "|", false);
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
					if (currentToken.endsWith(".lst"))
					{
						// load file
						handled = true;
						bArrayList = adds(lineType, bArrayList);
						if (!loadedFiles.containsKey(currentToken))
						{
							bArrayList = initFile(System.getProperty("user.dir") + File.separatorChar + currentToken, lineType, bArrayList);
						}
						if (fileName.length() > 0 && !loadedFiles.containsKey(fileName))
						{
							loadedFiles.put(fileName, fileName);
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
					if (handled == false && lineType != 12 && lineType != 18)
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

		// add virtual "Domain" class if any domains are found

		if (domainList.size() > 0 && getClassNamed("Domain") == null)
		{
			PCClass domainClass = new PCClass();
			domainClass.setName("Domain");
			domainClass.setKnownSpellsFromSpecialty(1);
			domainClass.addKnownSpellsList("LEVEL=0|LEVEL=1|LEVEL=2|LEVEL=3|LEVEL=4|LEVEL=5|LEVEL=6|LEVEL=7|LEVEL=8|LEVEL=9");
			classList.add(domainClass);
		}
		if (skillReq.length() > 0)
		{
			Skill aSkill = null;
			for (Iterator e1 = skillList.iterator(); e1.hasNext();)
			{
				aSkill = (Skill)e1.next();
				if ((skillReq.equals("UNTRAINED") && aSkill.untrained().startsWith("Y")) ||
					skillReq.equals("ALL"))
				{
					aSkill.setRequired(true);
				}
			}
		}
		if (languageList.size() == 0)
		{
			// add all known languages to language list if it's empty

			Race aRace = null;
			for (Iterator raceIterator = raceMap.values().iterator(); raceIterator.hasNext();)
			{
				aRace = (Race)raceIterator.next();
				languageList.addAll(aRace.getLanguageAutos());
				languageList.addAll(aRace.getLanguageBonus());
			}
			languageList.remove("ALL");
		}

		//
		// Check all the weapons to see if they are either Melee or Ranged, to avoid
		// problems when we go to export/preview the character
		//
		if (equipmentList.size() > 0)
		{
			Equipment aEq = null;
			for (Iterator e2 = equipmentList.iterator(); e2.hasNext();)
			{
				aEq = (Equipment)e2.next();
				if (aEq.typeStringContains("Weapon") && !aEq.typeStringContains("Melee") && !aEq.typeStringContains("Ranged"))
				{
					JOptionPane.showMessageDialog(null, "Weapon: " + aEq.getName() + " is neither Melee nor Ranged."
						+ "\nPCGen cannot calculate \"to hit\" unless one of these is selected."
//						+ "\nSource: " aEq.getSource()
						, "PCGen", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

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
		String s = null;
		for (Iterator it = chosenCampaignSourcefiles.iterator(); it.hasNext();)
		{
			s = (String)it.next();
			if (s.equals(aString))
			{
				alreadyChosen = true;
				break;
			}
		}
		if (!alreadyChosen)
			chosenCampaignSourcefiles.add(aString);

		ArrayList aArrayList = initFile(aString, 14, new ArrayList());
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
			if (aLine.startsWith("RACE:"))
			{
				raceFileLines.add(aLine.substring(5));
			}
			else if (aLine.startsWith("CLASS:"))
			{
				classFileLines.add(aLine.substring(6));
			}
			else if (aLine.startsWith("SKILL:"))
			{
				skillFileLines.add(aLine.substring(6));
			}
			else if (aLine.startsWith("FEAT:"))
			{
				featFileLines.add(aLine.substring(5));
			}
			else if (aLine.startsWith("DOMAIN:"))
			{
				domainFileLines.add(aLine.substring(7));
			}
			else if (aLine.startsWith("DEITY:"))
			{
				deityFileLines.add(aLine.substring(6));
			}
			else if (aLine.startsWith("SPELL:"))
			{
				spellFileLines.add(aLine.substring(6));
			}
			else if (aLine.startsWith("WEAPONPROF:"))
			{
				weaponProfFileLines.add(aLine.substring(11));
			}
			else if (aLine.startsWith("EQUIPMENT:"))
			{
				equipmentFileLines.add(aLine.substring(10));
			}
			else if (aLine.startsWith("LANGUAGE:"))
			{
				languageLines.add(aLine.substring(9));
			}
			else if (aLine.startsWith("CLASSSKILL:"))
			{
				classSkillFileLines.add(aLine.substring(11));
			}
			else if (aLine.startsWith("CLASSSPELL:"))
			{
				classSpellFileLines.add(aLine.substring(11));
			}
			else if (aLine.startsWith("REQSKILL:"))
			{
				reqSkillLines.add(aLine.substring(9));
			}
			else if (aLine.startsWith("TEMPLATE:"))
			{
				templateFileLines.add(aLine.substring(9));
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid line: " + aLine + " in " + aString, "PCGen", JOptionPane.ERROR_MESSAGE);
			}
		}
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
		d_sorted = true;
	}


	private static ArrayList adds(int lineType, ArrayList aArrayList)
	{
		String aClassName = "";
		for (int i = 0; i < aArrayList.size(); i++)
		{
			switch (lineType)
			{
				case 0:
					Race race = (Race)aArrayList.get(i);
					raceMap.put(race.getKeyName(), race);
					break;
				case 1:
					PCClass bClass = getClassKeyed(((PCClass)aArrayList.get(i)).getKeyName());
					if (bClass == null)
					{
						classList.add(aArrayList.get(i));
					}
					break;
				case 2:
					Skill aSkill = getSkillKeyed(((Skill)aArrayList.get(i)).getKeyName());
					if (aSkill == null)
					{
						skillList.add(aArrayList.get(i));
					}
					break;
				case 3:
					Feat aFeat = getFeatKeyed(((Feat)aArrayList.get(i)).getKeyName());
					if (aFeat == null)
					{
						featList.add(aArrayList.get(i));
					}
					break;
				case 4:
					Domain aDomain = getDomainKeyed(((Domain)aArrayList.get(i)).getKeyName());
					if (aDomain == null)
					{
						domainList.add(aArrayList.get(i));
					}
					break;
				case 5:
					Deity aDeity = getDeityKeyed(((Deity)aArrayList.get(i)).getKeyName());
					if (aDeity == null)
					{
						deityList.add(aArrayList.get(i));
					}
					break;
				case 6:
					Spell spell = (Spell)aArrayList.get(i);
					spellMap.put(spell.getKeyName(), spell);
					break;
				case 7:
					WeaponProf wp = getWeaponProfKeyed(((WeaponProf)aArrayList.get(i)).getKeyName());
					if (wp == null)
					{
						weaponProfList.add(aArrayList.get(i));
					}
					break;
				case 11:
					Equipment eq = getEquipmentKeyed(((Equipment)aArrayList.get(i)).getKeyName());
					if (eq == null)
					{
						equipmentList.add(aArrayList.get(i));
					}
					break;
				case 12:
					languageList.add(aArrayList.get(i));
					break;
				case 16:
					parseClassSkillFrom((String)aArrayList.get(i));
					break;
				case 17:
					aClassName = parseClassSpellFrom((String)aArrayList.get(i), aClassName);
					break;
				case 18:
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
				case 21:
					PCTemplate aTemplate = getTemplateKeyed(((PCTemplate)aArrayList.get(i)).getKeyName());
					if (aTemplate == null)
					{
						templateList.add(aArrayList.get(i));
					}
					break;
			}
		}
		aArrayList.clear();
		return aArrayList;
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
			int level = Integer.parseInt(aString);
			String bString = aTok.nextToken();
			aTok = new StringTokenizer(bString, "|", false);
			while (aTok.hasMoreTokens())
			{
				Spell aSpell = getSpellKeyed(aTok.nextToken().trim());
				if (aSpell != null)
				{
					if (aSpell.getClassLevels().length() > 0)
					{
						aSpell.setClassLevels(aSpell.getClassLevels() + ",");
					}
					aSpell.setClassLevels(aSpell.getClassLevels() + aName + "," + level);
				}
			}
		}
		return aName;
	}





	///////////////////////////////////////////////////////////////////

	// Options



	/**
	 * Opens the options.ini file and calls {@link #loadOptions}.
	 *
	 * @return  the <code>Dimension</code> from <code>loadOptions</code>
	 */
	public static Dimension readOptionsProperties()
	{
		FileInputStream in = null;
		Dimension d = new Dimension(0, 0);
		try
		{
			in = new FileInputStream(System.getProperty("user.dir") + File.separator + "options.ini");
			options.load(in);
			d = loadOptions();
		}
		catch (IOException e)
		{
			//Not an error, it may well be that this file does not exist yet.

			if (debugMode)
				System.out.println("No options file found, will create one when exiting.");
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
	static Dimension loadOptions()
	{
		Dimension d = new Dimension(0, 0);
		pcgPath = new File(options.getProperty("pcgen.files.characters", defaultPath));
		templatePath = new File(options.getProperty("pcgen.files.templates", defaultPath + File.separator + "templates"));
		htmlOutputPath = new File(options.getProperty("pcgen.files.htmlOutput", defaultPath));
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
		dmNotes = options.getProperty("pcgen.options.dmnotes", "");
		purchaseStatMode = options.getProperty("pcgen.options.purchaseStatMode", "false").equals("true") ? true : false;
		ignoreLevelCap = options.getProperty("pcgen.options.ignoreLevelCap", "false").equals("true") ? true : false;
		starWarsMode = options.getProperty("pcgen.options.starWarsMode", "false").equals("true") ? true : false;
		dndMode = options.getProperty("pcgen.options.dndMode", "false").equals("true") ? true : false;

		//wheelMode = options.getProperty("pcgen.options.wheelMode", "false").equals("true") ? true : false;

		//deadlandsMode = options.getProperty("pcgen.options.deadlandsMode", "false").equals("true") ? true : false;

		//weirdWarsMode = options.getProperty("pcgen.options.weirdWarsMode", "false").equals("true") ? true : false;

		//l5rMode = options.getProperty("pcgen.options.l5rMode", "false").equals("true") ? true : false;

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
		chaTabPlacement = Integer.parseInt(options.getProperty("pcgen.options.chaTabPlacement", "2"));
		rollMethod = Integer.parseInt(options.getProperty("pcgen.options.rollMethod", "1"));
		looknFeel = Integer.parseInt(options.getProperty("pcgen.options.looknFeel", "0"));
		autoFeatsRefundable = options.getProperty("pcgen.options.autoFeatsRefundable", "false").equals("true") ? true : false;
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			statCost[i] = Integer.parseInt(options.getProperty("pcgen.options.statCost." + statValue, "0"));
		}
		hpMaxAtFirstLevel = options.getProperty("pcgen.options.hpMaxAtFirstLevel", "true").equals("true") ? true : false;
		hpRollMethod = Integer.parseInt(options.getProperty("pcgen.options.hpRollMethod", String.valueOf(Globals.s_HP_STANDARD)));
		toolTipTextShown = options.getProperty("pcgen.options.toolTipTextShown", "true").equals("true") ? true : false;
		previewTabShown = options.getProperty("pcgen.options.previewTabShown", "true").equals("true") ? true : false;
		Globals.setLeftUpperCorner(new Point(new Double(options.getProperty("pcgen.options.windowLeftUpperCorner.X", "-1.0")).intValue(),
			new Double(options.getProperty("pcgen.options.windowLeftUpperCorner.Y", "-1.0")).intValue()));
		Double dw = new Double(options.getProperty("pcgen.options.windowWidth", "0"));
		Double dh = new Double(options.getProperty("pcgen.options.windowHeight", "0"));
		if (dw.doubleValue() != 0.0 && dh.doubleValue() != 0.0)
		{
			int width = Integer.parseInt(dw.toString().substring(0, Math.min(dw.toString().length(), dw.toString().lastIndexOf("."))));
			int height = Integer.parseInt(dh.toString().substring(0, Math.min(dh.toString().length(), dh.toString().lastIndexOf("."))));
			d = new Dimension(width, height);
		}
		return d;
	}

	/**
	 * Opens (options.ini) for writing and calls {@link #saveOptions}.
	 */
	public static void writeOptionsProperties()
	{
		String header =
			"# Emacs, this is -*- java-properties-generic -*- mode.\r\n" +
			"#\r\n" +
			"# options.ini -- options set in pcgen\r\n" +
			"# Do not edit this file manually.\r\n";

		FileOutputStream out = null;
		try
		{
			saveOptions();
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
	static void saveOptions()
	{
		options.setProperty("pcgen.files.characters", pcgPath.getAbsolutePath());
		options.setProperty("pcgen.files.htmlOutput", htmlOutputPath.getAbsolutePath());
		options.setProperty("pcgen.files.templates", templatePath.getAbsolutePath());
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
		options.setProperty("pcgen.options.ignoreLevelCap", ignoreLevelCap ? "true" : "false");
		options.setProperty("pcgen.options.starWarsMode", starWarsMode ? "true" : "false");

		//options.setProperty("pcgen.options.weirdWarsMode", starWarsMode ? "true" : "false");

		//options.setProperty("pcgen.options.deadlandsMode", starWarsMode ? "true" : "false");

		//options.setProperty("pcgen.options.l5rMode", starWarsMode ? "true" : "false");

		//options.setProperty("pcgen.options.wheelMode", starWarsMode ? "true" : "false");

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
		options.setProperty("pcgen.options.autoFeatsRefundable", autoFeatsRefundable ? "true" : "false");
		options.setProperty("pcgen.options.dmnotes", dmNotes);

		options.setProperty("pcgen.options.rollMethod", String.valueOf(rollMethod));
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			options.setProperty("pcgen.options.statCost." + statValue, String.valueOf(statCost[i]));
		}
		options.setProperty("pcgen.options.hpMaxAtFirstLevel", hpMaxAtFirstLevel ? "true" : "false");
		options.setProperty("pcgen.options.hpRollMethod", String.valueOf(hpRollMethod));
		options.setProperty("pcgen.options.toolTipTextShown", toolTipTextShown ? "true" : "false");
		options.setProperty("pcgen.options.previewTabShown", previewTabShown ? "true" : "false");

		if (Globals.getLeftUpperCorner() != null)
		{
			options.setProperty("pcgen.options.windowLeftUpperCorner.X", String.valueOf(Globals.getLeftUpperCorner().getX()));
			options.setProperty("pcgen.options.windowLeftUpperCorner.Y", String.valueOf(Globals.getLeftUpperCorner().getY()));
		}
		options.setProperty("pcgen.options.windowWidth", String.valueOf(rootFrame.getSize().getWidth()));
		options.setProperty("pcgen.options.windowHeight", String.valueOf(rootFrame.getSize().getHeight()));

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
		StringBuffer result = new StringBuffer();
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
		char[] working = in.toCharArray();
		StringBuffer sb = new StringBuffer();
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




	///////////////////////////////////////////////////////////////////

	// The splash screen



	/**
	 * Ensures that the splash screen is not visible. This should be
	 * called before displaying any dialog boxes or windows at
	 * startup.
	 */
	public static void hideSplashScreen()
	{
		if (splash != null)
		{
			splash.dispose();
			splash = null;
		}
	}

	public static void showSplashScreen()
	{
		splash = new SplashScreen();
	}

	public static void advanceSplashProgress()
	{
		if (splash != null)
			splash.advance();
	}

}

