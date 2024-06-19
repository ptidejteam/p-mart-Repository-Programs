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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
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

	/** What a party template file name starts with.    */
	public final static String s_PARTY_TEMPLATE_START = "psheet";

	/** What a character template file name starts with.    */
	public final static String s_CHARACTER_TEMPLATE_START = "csheet";

	public final static String s_NONESELECTED = "<none selected>";

	public static ArrayList raceList = new ArrayList();
	public static ArrayList classList = new ArrayList();
	public static ArrayList skillList = new ArrayList();
	public static ArrayList featList = new ArrayList();
	public static ArrayList domainList = new ArrayList();
	public static ArrayList deityList = new ArrayList();
	public static ArrayList pcList = new ArrayList();
	public static ArrayList spellList = new ArrayList();
	public static ArrayList specialAbilityList = new ArrayList();
	public static ArrayList subSpecialAbilityList = new ArrayList();
	public static ArrayList weaponProfList = new ArrayList();
	public static ArrayList schoolsList = new ArrayList();
	public static TreeSet languageList = new TreeSet();
	public static PlayerCharacter currentPC = null;
	public static ArrayList colorList = new ArrayList();
	public static ArrayList traitList = new ArrayList();
	public static int hpPct = 100;
	public static ArrayList equipmentList = new ArrayList();
	public static ArrayList specialsList = new ArrayList();
	public static ArrayList loadStrings = new ArrayList();
	public static ArrayList campaignList = new ArrayList();
	public static ArrayList campaignNames = new ArrayList();
	public static TreeSet weaponTypes = new TreeSet();
	public static ArrayList selectedCampaignsList = new ArrayList();
	public static String currentFile = "";
	public static int lineNum = 0;
	public static int excSkillCost = 0;
	public static int intCrossClassSkillCost = 2;
	public static boolean debugMode = false;
	public static Point leftUpperCorner = null;
	public static String dmNotes = "";

	/**
	 * This is <code>true</code> when the campaign data structures are
	 * sorted.
	 */
	private static boolean d_sorted = false;

	/**
	 *  This will be overridden at startup with values read from properties.
	 */
	public static int[] statCost = new int[]
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
	public static boolean starWarsMode = false;
	public static boolean dndMode = true;
	public static boolean weirdWarsMode = false;
	public static boolean deadlandsMode = false;
	public static boolean l5rMode = false;
	public static boolean wheelMode = false;
	public static boolean ignoreLevelCap = false;
	public static boolean purchaseStatMode = false;
	public static int initialStatMin = 3;
	public static int initialStatMax = 18;
	public static boolean ignoreEquipmentCost = false;
	public static boolean unlimitedStatPool = false;
	/**
	 *  0==None, 1=Untrained, 2=all
	 */
	public static int includeSkills = 0;
	public static boolean grimHPMode = false;
	public static boolean grittyACMode = false;
	public static boolean loadCampaignsAtStart = false;
	/**
	 *  0=top 1=left  2=bottom
	 */
	public static int tabPlacement = 0;
	public static int chaTabPlacement = 0;
	/*0=UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
  *1=UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  */
	public static int looknFeel = 0;
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
	public static int rollMethod = 1;
	public static ArrayList chosenCampaignSourcefiles = new ArrayList();
	public static boolean hpMaxAtFirstLevel = true;
  public static final int HP_STANDARD = 0;
  public static final int HP_AUTOMAX = 1;
  public static final int HP_PERCENTAGE = 2;
  public static final int HP_RPGA = 3;
	public static int hpRollMethod = HP_STANDARD;
	static ArrayList indexList = new ArrayList();
	static ArrayList notesList = new ArrayList();
	static HashMap loadedFiles = new HashMap();
	static Hashtable campaignRanks = new Hashtable();
	static String skillReq = "";
	static ArrayList raceFileLines = new ArrayList();
	static ArrayList classFileLines = new ArrayList();
	static ArrayList skillFileLines = new ArrayList();
	static ArrayList featFileLines = new ArrayList();
	static ArrayList deityFileLines = new ArrayList();
	static ArrayList domainFileLines = new ArrayList();
	static ArrayList weaponProfFileLines = new ArrayList();
	static ArrayList equipmentFileLines = new ArrayList();
	static ArrayList classSkillFileLines = new ArrayList();
	static ArrayList classSpellFileLines = new ArrayList();
	static ArrayList spellFileLines = new ArrayList();
	static ArrayList languageLines = new ArrayList();
	static ArrayList reqSkillLines = new ArrayList();
	private static JFrame rootFrame;

	private static String defaultPath = System.getProperty("user.dir");
	private static File templatePath = new File(defaultPath);
	private static File pcgPath = new File(defaultPath);
	private static File htmlOutputPath = new File(defaultPath);
	private static File browserPath = new File("");
	private static String selectedTemplate = templatePath + File.separator + "csheet.html";
	private static SplashScreen splash;
	private static Properties options = new Properties();
	public final static Race s_emptyRace = new Race();


	///////////////////////////////////////////////////////////////////
	// Accessor methods

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
	 * Sets the current template.
	 *
	 * @param  path  a string containing the path to the template
	 */
	public static void setSelectedTemplate(String path)
	{
		selectedTemplate = path;
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
		for (Iterator e = specialsList.iterator(); e.hasNext();)
		{
			SpecialAbility sa = (SpecialAbility)e.next();
			if (adjustment == 1 && sa.name().equalsIgnoreCase(name) && foundIt)
			{
				return sa;
			}
			if (sa.name().equalsIgnoreCase(name) && (sa.desc().equalsIgnoreCase(desc) || desc.equals("")))
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
			if (sa.name().equalsIgnoreCase(name))
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
		while (aTok.hasMoreTokens())
		{
			String cString = aTok.nextToken();
			if (cString.substring(0, 1).equals(aString))
			{
				return new Float(cString.substring(cString.lastIndexOf('|') + 1)).doubleValue();
			}
		}
		return 1.0;
	}


	public static Campaign getCampaignNamed(String aName)
	{
		for (Iterator e = campaignList.iterator(); e.hasNext();)
		{
			Campaign c = (Campaign)e.next();
			if (c.name().equalsIgnoreCase(aName))
			{
				return c;
			}
		}
		return null;
	}


	public static Race getRaceNamed(String aName)
	{
		for (Iterator e = raceList.iterator(); e.hasNext();)
		{
			Race r = (Race)e.next();
			if (r.name().equalsIgnoreCase(aName))
			{
				return r;
			}
		}
		return null;
	}


	public static Race getRaceKeyed(String aKey)
	{
		return (Race)searchPObjectList(raceList, aKey);
	}


	public static PCClass getClassNamed(String aName)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass p = (PCClass)e.next();
			if (p.name().equalsIgnoreCase(aName))
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
		for (Iterator e = currentPC.race().weaponProfs.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			String typeString = aTok.nextToken();
			String wpString = aTok.nextToken();
			WeaponProf aProf = getWeaponProfNamed(wpString);
			if (aProf == null)
			{
				continue;
			}
			if (typeString.equalsIgnoreCase(type))
			{
				aArrayList.add(aProf);
			}
			else
			{
				bArrayList.add(aProf);
			}
		}
		for (Iterator e = weaponProfList.iterator(); e.hasNext();)
		{
			WeaponProf aProf = (WeaponProf)e.next();
			if (bArrayList.contains(aProf))
			{
				continue;
			}
			if (aProf.type().equalsIgnoreCase(type))
			{
				aArrayList.add(aProf);
			}
		}
		return aArrayList;
	}


	public static WeaponProf getWeaponProfNamed(String name)
	{
		for (Iterator e = weaponProfList.iterator(); e.hasNext();)
		{
			WeaponProf aProf = (WeaponProf)e.next();
			if (aProf.name().equalsIgnoreCase(name))
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
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.name().equalsIgnoreCase(name))
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
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			Feat f = (Feat)e.next();
			if (f.name().equalsIgnoreCase(name))
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


	public static Skill getSkillNamed(String name)
	{
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill s = (Skill)e.next();
			if (s.name().equalsIgnoreCase(name))
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
		for (Iterator e = deityList.iterator(); e.hasNext();)
		{
			Deity d = (Deity)e.next();
			if (d.name().equalsIgnoreCase(name))
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
		for (Iterator e = domainList.iterator(); e.hasNext();)
		{
			Domain d = (Domain)e.next();
			if (d.name().equalsIgnoreCase(name))
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
		for (Iterator e = spellList.iterator(); e.hasNext();)
		{
			Spell s = (Spell)e.next();
			if (s.name().equalsIgnoreCase(name))
			{
				return s;
			}
		}
		return null;
	}


	public static Spell getSpellKeyed(String aKey)
	{
		return (Spell)searchPObjectList(spellList, aKey);
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
	 * Clears all lists of game data.
	 */
	public static void emptyLists()
	{
		loadedFiles.clear();
		raceList.clear();
		classList.clear();
		skillList.clear();
		featList.clear();
		equipmentList.clear();
		weaponProfList.clear();
		deityList.clear();
		domainList.clear();
		languageList.clear();
		spellList.clear();
		skillReq = "";
		raceFileLines.clear();
		classFileLines.clear();
		skillFileLines.clear();
		featFileLines.clear();
		deityFileLines.clear();
		domainFileLines.clear();
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
		byte[] inputLine;
		fileName = fileName.replace('\\', File.separatorChar);
		fileName = fileName.replace('/', File.separatorChar);
		File aFile = new File(fileName);
		PObject anObj = null;
		String aString;
		String aLine = "";
		currentFile = fileName;
		lineNum = 0;
		Campaign aCampaign = null;
		try
		{
			FileInputStream aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			aString = new String(inputLine);
			String newlinedelim = new String("\r\n");
			StringTokenizer newlineStr = new StringTokenizer(aString, newlinedelim, false);
			int rowMax = newlineStr.countTokens();
			int rowNum = 0;
			for (rowNum = 0; rowNum < rowMax; rowNum++)
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
				}
				if (((fileType >= 0 && fileType <= 7) || fileType == 11 || fileType == 15) && anObj != null)
				{
					anObj.setSourceFile(aFile.getAbsolutePath());
				}
				if (fileType == 15 && rowNum == 2)
				{
					break;
				}
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



	///////////////////////////////////////////////////////////////////
	//


	public static Float maxLoadForStrengthAndSize(int strength, String size)
	{
		double x = 0.0;
		double dbl = 0.0;
		int y = strength;
		int loadStringSize = loadStrings.size();
		if (strength >= loadStringSize - 2)
		{
			String bString = (String)loadStrings.get(loadStringSize - 1);
			dbl = new Float(bString.substring(bString.lastIndexOf('\t') + 1)).doubleValue();
			for (y = strength; y >= loadStringSize; y -= 10)
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
		return new Float(aFloat.doubleValue() * getLoadMultForSize(currentPC.size()));
	}


	/**
	 * @return 0 = light, 1 = medium, 2 = heavy, 3 = overload
	 */
	public static int loadTypeForStrength(int strength, Float weight)
	{
		double dbl = weight.doubleValue() / maxLoadForStrengthAndSize(strength, currentPC.size()).doubleValue();
		if (dbl <= .333)
		{
			return 0;
		}
		if (dbl <= .666)
		{
			return 1;
		}
		if (dbl <= 1.0)
		{
			return 2;
		}
		return 3;
	}


	public static PObject binarySearchPObject(ArrayList aList, String keyName)
	{
		Object[] pobjArray = (Object[])aList.toArray();
		int lower = 0;
		int upper = pobjArray.length;
		// always one passed last possible match
		while (lower < upper)
		{
			int mid = (lower + upper) / 2;
			PObject obj = (PObject)pobjArray[mid];
			int cmp = keyName.compareTo(obj.keyName());
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
			int lower = 0;
			int upper = pobjArray.length;
			// not presently sorted
			for (int i = upper - 1; i >= 0; --i)
			{
				PObject obj = (PObject)pobjArray[i];
				if (keyName.equals(obj.keyName())) return obj;
			}
		}
		return null;
	}


	public static ArrayList sortPObjectList(ArrayList aList)
	{
		Collections.sort(aList, new PObjectComp());
		return aList;
	}

	private static class PObjectComp implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return ((PObject)o1).keyName().compareTo(((PObject)o2).keyName());
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
		if (raceList.size() == 0 || classList.size() == 0 || skillList.size() == 0 ||
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
			JOptionPane.showMessageDialog(null, "You must select at least one campaign to load.");
			return;
		}

		//Add empty race
		if (!Globals.raceList.contains(Globals.s_emptyRace))
		{
			s_emptyRace.setName(Globals.s_NONESELECTED);
			Globals.raceList.add(s_emptyRace);
		}

		int i = 0;
		for (i = 0; i < aSelectedCampaignsList.size() - 1; i++)
		{
			Campaign aCamp = (Campaign)aSelectedCampaignsList.get(i);
			for (int j = i + 1; j < aSelectedCampaignsList.size(); j++)
			{
				Campaign bCamp = (Campaign)aSelectedCampaignsList.get(j);
				if (bCamp.rank().intValue() < aCamp.rank().intValue())
				{
					aSelectedCampaignsList.set(i, bCamp);
					aSelectedCampaignsList.set(j, aCamp);
					Campaign cCamp = (Campaign)aCamp.clone();
					aCamp = bCamp;
					bCamp = cCamp;
				}
			}
		}

		for (i = 0; i < aSelectedCampaignsList.size(); i++)
		{
			Campaign aCamp = (Campaign)aSelectedCampaignsList.get(i);
			loadCampaignFile(aCamp);
		}
		for (int lineType = 0; lineType < 19; lineType++)
		{
			ArrayList aArrayList = null;
			ArrayList bArrayList = new ArrayList();
			switch (lineType)
			{
				case 0:
					aArrayList = raceFileLines;
					break;
				case 1:
					aArrayList = classFileLines;
					break;
				case 2:
					aArrayList = skillFileLines;
					break;
				case 3:
					aArrayList = featFileLines;
					break;
				case 4:
					aArrayList = domainFileLines;
					break;
				case 5:
					aArrayList = deityFileLines;
					break;
				case 6:
					aArrayList = spellFileLines;
					break;
				case 7:
					aArrayList = weaponProfFileLines;
					break;
				case 8:
					continue;
				case 9:
					continue;
				case 10:
					continue;
				case 11:
					aArrayList = equipmentFileLines;
					break;
				case 12:
					aArrayList = languageLines;
					break;
				case 13:
					continue;
				case 14:
					continue;
				case 15:
					continue;
				case 16:
					aArrayList = classSkillFileLines;
					break;
				case 17:
					aArrayList = classSpellFileLines;
					break;
				case 18:
					aArrayList = reqSkillLines;
					break;
				default:
					aArrayList = null;
					System.out.println("Campaign list corrupt at line: " + i + " no such lineType exists. Stopped parsing campaigns, but not aborting program.");
					return;
			}
			for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
			{
				String aLine = (String)e1.next();
				StringTokenizer aTok = new StringTokenizer(aLine, "|", false);
				int inMode = 0;
				ArrayList cArrayList = new ArrayList();
				String fileName = "";
				while (aTok.hasMoreTokens())
				{
					String cString = aTok.nextToken();
					int openParens = 0;
					int closeParens = 0;
					String dString = cString.substring(1);
					while (dString.lastIndexOf("(") > -1)
					{
						openParens++;
						dString = dString.substring(0, dString.lastIndexOf("("));
					}
					dString = cString;
					while (dString.lastIndexOf(")") > -1)
					{
						closeParens++;
						dString = dString.substring(0, dString.lastIndexOf(")"));
					}
					boolean handled = false;
					if (cString.endsWith(".lst"))
					{
						// load file

						handled = true;
						bArrayList = adds(lineType, bArrayList);
						if (!loadedFiles.containsKey(cString))
						{
							bArrayList = initFile(System.getProperty("user.dir") + File.separatorChar + cString, lineType, bArrayList);
						}
						if (fileName.length() > 0 && !loadedFiles.containsKey(fileName))
						{
							loadedFiles.put(fileName, fileName);
						}
						fileName = cString;
						cArrayList.clear();
					}
					if (cString.startsWith("(EXCLUDE"))
					{
						handled = true;
						fileName = "";
						if (closeParens > openParens)
						{
							cString = cString.substring(0, cString.length() - 1);
						}
						cArrayList.add(cString.substring(9));
						if (closeParens > openParens)
						{
							cString = ")";
						}
						inMode = -1;
					}
					else if (cString.startsWith("(INCLUDE"))
					{
						handled = true;
						fileName = "";
						if (closeParens > openParens)
						{
							cString = cString.substring(0, cString.length() - 1);
						}
						cArrayList.add(cString.substring(9));
						if (closeParens > openParens)
						{
							cString = ")";
						}
						inMode = 1;
					}
					if (cString.endsWith(")") && closeParens > openParens)
					{
						if (handled == false)
						{
							cArrayList.add(cString.substring(0, cString.length() - 1));
						}
						handled = true;
						if (inMode == -1)
						{
							// exclude

							for (int k = bArrayList.size() - 1; k >= 0; k--)
							{
								PObject anObject = (PObject)bArrayList.get(k);
								if (cArrayList.contains(anObject.keyName()))
								{
									bArrayList.remove(k);
								}
							}
						}
						else if (inMode == 1)
						{
							// include

							for (int k = bArrayList.size() - 1; k >= 0; k--)
							{
								PObject anObject = (PObject)bArrayList.get(k);
								if (!cArrayList.contains(anObject))
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
						cArrayList.add(cString);
					}
					else if (handled == false)
					{
						bArrayList.add(cString);
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
			PCClass aClass = new PCClass();
			aClass.setName("Domain");
			classList.add(aClass);
		}
		if (skillReq.length() > 0)
		{
			for (Iterator e1 = skillList.iterator(); e1.hasNext();)
			{
				Skill aSkill = (Skill)e1.next();
				if ((skillReq.equals("UNTRAINED") && aSkill.untrained().startsWith("Y")) ||
					skillReq.equals("ALL"))
				{
					aSkill.isRequired = true;
				}
			}
		}
		if (languageList.size() == 0)
		{
			// add all known languages to language list if it's empty
			for (Iterator e1 = raceList.iterator(); e1.hasNext();)
			{
				Race aRace = (Race)e1.next();
				languageList.addAll(aRace.languageAutos());
				languageList.addAll(aRace.languageBonus());
			}
			languageList.remove("ALL");
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
		for (Iterator it = chosenCampaignSourcefiles.iterator(); it.hasNext(); )
		{
			String s = (String) it.next();
			if (s.equals (aString))
			{
				alreadyChosen = true;
				break;
			}
		}
		if (!alreadyChosen)
			chosenCampaignSourcefiles.add(aString);

		ArrayList aArrayList = initFile(aString, 14, new ArrayList());
		for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
		{
			String aLine = (String)e1.next();
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
			else
			{
				JOptionPane.showMessageDialog(null, "Invalid line: " + aLine + " in " + aString);
			}
		}
	}


	public static void sortCampaigns()
	{
		sortPObjectList(raceList);
		sortPObjectList(classList);
		sortPObjectList(skillList);
		sortPObjectList(featList);
		sortPObjectList(deityList);
		sortPObjectList(domainList);
		sortPObjectList(spellList);
		sortPObjectList(equipmentList);
		sortPObjectList(weaponProfList);
		d_sorted = true;
	}


	private static ArrayList adds(int lineType, ArrayList aArrayList)
	{
		String aClassName = "";
		for (int i = 0; i < aArrayList.size(); i++)
		{
			int j = 0;
			switch (lineType)
			{
				case 0:
					Race aRace = getRaceKeyed(((Race)aArrayList.get(i)).keyName());
					if (aRace == null)
					{
						raceList.add(aArrayList.get(i));
					}
					break;
				case 1:
					PCClass bClass = getClassKeyed(((PCClass)aArrayList.get(i)).keyName());
					if (bClass == null)
					{
						classList.add(aArrayList.get(i));
					}
					break;
				case 2:
					Skill aSkill = getSkillKeyed(((Skill)aArrayList.get(i)).keyName());
					if (aSkill == null)
					{
						skillList.add(aArrayList.get(i));
					}
					break;
				case 3:
					Feat aFeat = getFeatKeyed(((Feat)aArrayList.get(i)).keyName());
					if (aFeat == null)
					{
						featList.add(aArrayList.get(i));
					}
					break;
				case 4:
					Domain aDomain = getDomainKeyed(((Domain)aArrayList.get(i)).keyName());
					if (aDomain == null)
					{
						domainList.add(aArrayList.get(i));
					}
					break;
				case 5:
					Deity aDeity = getDeityKeyed(((Deity)aArrayList.get(i)).keyName());
					if (aDeity == null)
					{
						deityList.add(aArrayList.get(i));
					}
					break;
				case 6:
					Spell aSpell = getSpellKeyed(((Spell)aArrayList.get(i)).keyName());
					if (aSpell == null)
					{
						spellList.add(aArrayList.get(i));
					}
					break;
				case 7:
					WeaponProf wp = getWeaponProfKeyed(((WeaponProf)aArrayList.get(i)).keyName());
					if (wp == null)
					{
						weaponProfList.add(aArrayList.get(i));
					}
					break;
				case 11:
					Equipment eq = getEquipmentKeyed(((Equipment)aArrayList.get(i)).keyName());
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
							aSkill.isRequired = true;
						}
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
			aName = aClass.keyName();
		if (aTok.hasMoreTokens())
		{
			className = aTok.nextToken();
			aTok = new StringTokenizer(className, "|", false);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				final String aStringParen = aString + "(";
				Skill aSkill = getSkillKeyed(aString);
				if (aSkill != null)
				{
					aSkill.classList.add(aName);
				}
				else
				{
					for (Iterator e = skillList.iterator(); e.hasNext();)
					{
						Skill bSkill = (Skill)e.next();
						if (bSkill.keyName().startsWith(aStringParen))
						{
							bSkill.classList.add(aName);
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
				aName = aClass.keyName();
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
					if (aSpell.classLevels().length() > 0)
					{
						aSpell.classLevels = aSpell.classLevels() + ",";
					}
					aSpell.classLevels = aSpell.classLevels() + aName + "," + level;
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
	private static Dimension loadOptions()
	{
		Dimension d = new Dimension(0, 0);
		pcgPath = new File(options.getProperty("pcgen.files.characters", defaultPath));
		templatePath = new File(options.getProperty("pcgen.files.templates", defaultPath));
		htmlOutputPath = new File(options.getProperty("pcgen.files.htmlOutput", defaultPath));
		browserPath = new File(options.getProperty("pcgen.options.browserPath", ""));
		selectedTemplate = options.getProperty("pcgen.files.selectedTemplate", templatePath + File.separator + "csheet.html");
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
		options.setProperty("pcgen.options.dmnotes", dmNotes);
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			statCost[i] = Integer.parseInt(options.getProperty("pcgen.options.statCost." + statValue, "0"));
		}
    hpMaxAtFirstLevel = options.getProperty("pcgen.options.hpMaxAtFirstLevel", "true").equals("true") ? true : false;
    hpRollMethod = Integer.parseInt(options.getProperty("pcgen.options.hpRollMethod", String.valueOf(Globals.HP_STANDARD)));

		Globals.leftUpperCorner = new Point(new Double(options.getProperty("pcgen.options.windowLeftUpperCorner.X", "-1.0")).intValue(),
			new Double(options.getProperty("pcgen.options.windowLeftUpperCorner.Y", "-1.0")).intValue());
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
	private static void saveOptions()
	{
		options.setProperty("pcgen.files.characters", pcgPath.getAbsolutePath());
		options.setProperty("pcgen.files.htmlOutput", htmlOutputPath.getAbsolutePath());
		options.setProperty("pcgen.files.templates", templatePath.getAbsolutePath());
		options.setProperty("pcgen.files.selectedTemplate", selectedTemplate);
		options.setProperty("pcgen.files.chosenCampaignSourcefiles", unSplit(chosenCampaignSourcefiles, ','));
		options.setProperty("pcgen.options.browserPath", browserPath.getAbsolutePath());

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
		options.setProperty("pcgen.options.rollMethod", String.valueOf(rollMethod));
		for (int i = 0; i < statCost.length; i++)
		{
			int statValue = i + 9;
			options.setProperty("pcgen.options.statCost." + statValue, String.valueOf(statCost[i]));
		}
    options.setProperty("pcgen.options.hpMaxAtFirstLevel", hpMaxAtFirstLevel ? "true" : "false");
    options.setProperty("pcgen.options.hpRollMethod", String.valueOf(hpRollMethod));

		if (Globals.leftUpperCorner != null)
		{
			options.setProperty("pcgen.options.windowLeftUpperCorner.X", String.valueOf(Globals.leftUpperCorner.getX()));
			options.setProperty("pcgen.options.windowLeftUpperCorner.Y", String.valueOf(Globals.leftUpperCorner.getY()));
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
