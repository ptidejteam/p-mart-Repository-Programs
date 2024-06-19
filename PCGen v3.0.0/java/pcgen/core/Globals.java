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
 * $Id: Globals.java,v 1.1 2006/02/21 00:02:22 vauchers Exp $
 */
package pcgen.core;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import pcgen.core.character.CompanionMod;
import pcgen.core.spell.Spell;
import pcgen.gui.Chooser;
import pcgen.gui.PCGen_Frame1;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.LstConstants;
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

	public static String[] s_ATTRIBLONG = null;
	public static boolean[] s_ATTRIBROLL = null;
	public static String[] s_ATTRIBSHORT = null;

	/** An empty race. Duh. */
	public static Race s_EMPTYRACE = null;
	public static String s_STATNAMES = ""; //"STRDEXCONINTWISCHA";
	public static Toolkit s_TOOLKIT = Toolkit.getDefaultToolkit();
	private static ArrayList XPList = new ArrayList();
	private static boolean bAutoGeneration = false;
	private static ArrayList bonusStackList = new ArrayList();
	private static ArrayList campaignList = new ArrayList();

	/** we need this set for efficient filter creation */
	private static TreeSet castingTimesSet = new TreeSet();
	private static ArrayList classList = new ArrayList();
	private static ArrayList colorList = new ArrayList();
	private static ArrayList companionModList = new ArrayList();
	private static Map companionSwitchRaceMap = new HashMap();

	/** This is for the Internationalization project. */
	private static String country = "US";
	private static PlayerCharacter currentPC = null;
	private static ArrayList custColumnWidth = new ArrayList();

	/**This is <code>true</code> when the campaign data structures are sorted. */
	private static boolean d_sorted = false;
	private static boolean debugMode = false;

	/** NOTE: The defaultPath is duplicated in LstSystemLoader. */
	private static String defaultPath = System.getProperty("user.dir");
	private static String defaultPcgPath = getDefaultPath() + java.io.File.separator + "characters";
	private static ArrayList deityList = new ArrayList();

	/** the DenominationList class is derived from ArrayList */
	private static DenominationList denominationList = new DenominationList();

	/** we need this set for efficient filter creation */
	private static TreeSet descriptorSet = new TreeSet();
	private static int[] dieSizes = new int[]{1, 2, 3, 4, 6, 8, 10, 12, 20, 100, 1000};
	private static ArrayList domainList = new ArrayList();

	/** we need this set for efficient filter creation */
	private static TreeSet effectTypesSet = new TreeSet();
	private static ArrayList equipmentList = new ArrayList();
	private static ArrayList featList = new ArrayList();
	private static ArrayList hairStyleList = new ArrayList();
	private static int hpPct = 100;
	private static ArrayList interestsList = new ArrayList();

	/** This is for the Internationalization project. */
	private static String language = "en";
	private static ArrayList languageList = new ArrayList();
	private static ArrayList loadStrings = new ArrayList();
	private static ArrayList locationList = new ArrayList();
	private static ArrayList modifierList = new ArrayList();

	/** we need this set for efficient filter creation */
	private static TreeSet pantheonsSet = new TreeSet();
	private static ArrayList paperInfo = new ArrayList();
	private static ArrayList pcList = new ArrayList();
	private static ArrayList pcClassTypeList = new ArrayList(); //any TYPE added to this list is assumed be pre-tokenized
	private static ArrayList phobiaList = new ArrayList();
	private static ArrayList phraseList = new ArrayList();
	private static Map raceMap = new HashMap();

	/** we need this set for efficient filter creation */
	private static TreeSet raceTypesSet = new TreeSet();
	private static final Random random = new Random(System.currentTimeMillis());

	/** we need this set for efficient filter creation */
	private static TreeSet rangesSet = new TreeSet();
	private static PCGen_Frame1 rootFrame;
	private static ArrayList schoolsList = new ArrayList();
	private static int selectedPaper = -1;
	private static ArrayList sizeAdjustmentList = new ArrayList();
	private static ArrayList skillList = new ArrayList();
	private static int sourceDisplay = Constants.SOURCELONG;
	private static ArrayList specialsList = new ArrayList();
	private static ArrayList speechList = new ArrayList();
	private static Map spellMap = new HashMap();
	private static String spellPoints = "0";

	/** we need this set for efficient filter creation */
	private static TreeSet srSet = new TreeSet();

	/** we need this set for efficient filter creation */
	private static TreeSet subschoolsSet = new TreeSet();
	private static ArrayList templateList = new ArrayList();
	private static ArrayList traitList = new ArrayList();

	/**
	 * whether or not the GUI is used (false for command line)
	 */
	private static boolean useGUI = true;
	private static ArrayList weaponProfList = new ArrayList();
	private static TreeSet weaponTypes = new TreeSet();

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

	public static void setAttribLong(String[] s)
	{
		s_ATTRIBLONG = s;
	}

	public static void setAttribLong(int index, String s)
	{
		s_ATTRIBLONG[index] = s;
	}

	public static void setAttribRoll(boolean[] b)
	{
		s_ATTRIBROLL = b;
	}

	public static void setAttribRoll(int index, boolean b)
	{
		s_ATTRIBROLL[index] = b;
	}

	public static void setAttribShort(String[] s)
	{
		s_ATTRIBSHORT = s;
	}

	public static void setAttribShort(int index, String s)
	{
		s_ATTRIBSHORT[index] = s;
	}

	public static void setAutoGeneration(boolean auto)
	{
		setbAutoGeneration(auto);
	}

	public static boolean isAutoGeneration()
	{
		return isbAutoGeneration();
	}

	public static void setBonusStackList(ArrayList bonusStackList)
	{
		Globals.bonusStackList = bonusStackList;
	}

	public static ArrayList getBonusStackList()
	{
		return bonusStackList;
	}

	public static Campaign getCampaignByFilename(String aName)
	{
		return getCampaignByFilename(aName, true);
	}

	public static Campaign getCampaignByFilename(String aName, boolean complainOnError)
	{

		Campaign c = null;

		for (Iterator e = getCampaignList().iterator(); e.hasNext();)
		{
			c = (Campaign)e.next();

			if (c.getSourceFile().equalsIgnoreCase(aName))
			{
				return c;
			}
		}

		if (complainOnError)
		{
			errorPrint("Could not find campaign by filename: " + aName);
		}

		return null;
	}

	public static void setCampaignList(ArrayList campaignList)
	{
		Globals.campaignList = campaignList;
	}

	public static ArrayList getCampaignList()
	{
		return campaignList;
	}

	public static Campaign getCampaignNamed(String aName)
	{

		Campaign c = null;

		for (Iterator e = getCampaignList().iterator(); e.hasNext();)
		{
			c = (Campaign)e.next();

			if (c.getName().equalsIgnoreCase(aName))
			{
				return c;
			}
		}

		errorPrint("Could not find campaign: " + aName);
		return null;
	}

	public static TreeSet getCastingTimes()
	{
		return getCastingTimesSet();
	}

	public static void setCastingTimesSet(TreeSet castingTimesSet)
	{
		Globals.castingTimesSet = castingTimesSet;
	}

	public static PCClass getClassKeyed(String aKey)
	{
		return (PCClass)searchPObjectList(getClassList(), aKey);
	}

	public static void setClassList(ArrayList classList)
	{
		Globals.classList = classList;
	}

	public static ArrayList getClassList()
	{
		return classList;
	}

	public static PCClass getClassNamed(String aName)
	{
		return getClassNamed(aName, getClassList());
	}

	public static PCClass getClassNamed(String aName, List aList)
	{

		PCClass p = null;

		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			p = (PCClass)e.next();

			if (p.getName().equalsIgnoreCase(aName))
			{
				return p;
			}
		}

		return null;
	}

	public static boolean isCoCd20Mode()
	{
		if (SettingsHandler.getGame().equals(Constants.HWNMNBSOL_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setColorList(ArrayList colorList)
	{
		Globals.colorList = colorList;
	}

	public static ArrayList getColorList()
	{
		return colorList;
	}

	public static CompanionMod getCompanionMod(String aString)
	{
		if (aString.length() <= 0)
		{
			return null;
		}

		StringTokenizer aTok = new StringTokenizer(aString.substring(9), "=", false);
		String classes = aTok.nextToken();
		int level = Integer.parseInt(aTok.nextToken());

		for (Iterator e = getCompanionModList().iterator(); e.hasNext();)
		{

			CompanionMod aComp = (CompanionMod)e.next();
			aTok = new StringTokenizer(classes, ",", false);

			while (aTok.hasMoreTokens())
			{

				String cString = aTok.nextToken();

				if (aComp.getLevel(cString) == level)
				{
					return aComp;
				}
			}
		}

		return null;
	}

	public static ArrayList getCompanionModList()
	{
		return companionModList;
	}

	public static String getCompanionSwitch(String aName)
	{
		return (String)getCompanionSwitchRaceMap().get(aName);
	}

	public static Map getCompanionSwitchRaceMap()
	{
		return companionSwitchRaceMap;
	}

	public static void setCountry(String aString)
	{
		country = aString;
	}

	public static String getCountry()
	{
		return country;
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
		if (isSpycraftMode())
		{
			return "pt";
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

	public static void setCurrentPC(PlayerCharacter aCurrentPC)
	{
		currentPC = aCurrentPC;
	}

	public static PlayerCharacter getCurrentPC()
	{
		return currentPC;
	}

	public static void setCustColumnWidth(String fromTab, int col, int value)
	{

		boolean found = false;
		final String cName = fromTab.concat(Integer.toString(col));
		final String addMe = cName.concat("|").concat(Integer.toString(value));

		if (getCustColumnWidth().isEmpty())
		{
			getCustColumnWidth().add(addMe);
		}

		for (int i = 0; i < getCustColumnWidth().size(); i++)
		{

			final StringTokenizer tTok = new StringTokenizer((String)getCustColumnWidth().get(i), "|", false);
			final String tabName = tTok.nextToken();

			if (tabName.equals(cName))
			{
				getCustColumnWidth().set(i, addMe);
				found = true;
			}
		}

		if (!found)
		{
			getCustColumnWidth().add(addMe);
		}
	}

	public static void setCustColumnWidth(ArrayList custColumnWidth)
	{
		Globals.custColumnWidth = custColumnWidth;
	}

	public static int getCustColumnWidth(String fromTab, int col)
	{

		int colSize = 0;
		final String cName = fromTab.concat(Integer.toString(col));

		for (int i = 0; i < getCustColumnWidth().size(); i++)
		{

			final StringTokenizer tTok = new StringTokenizer((String)getCustColumnWidth().get(i), "|", false);
			final String tabName = tTok.nextToken();

			if (tabName.equals(cName))
			{
				colSize = Integer.parseInt(tTok.nextToken());
			}
		}

		return colSize;
	}

	public static ArrayList getCustColumnWidth()
	{
		return custColumnWidth;
	}

	public static BufferedReader getCustomEquipmentReader()
	{
		try
		{
			return new BufferedReader(new FileReader(customEquipmentFilePath()));
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public static BufferedWriter getCustomEquipmentWriter()
	{
		try
		{
			return new BufferedWriter(new FileWriter(customEquipmentFilePath()));
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public static void setD_sorted(boolean d_sorted)
	{
		Globals.d_sorted = d_sorted;
	}

	public static boolean isD_sorted()
	{
		return d_sorted;
	}

	public static boolean isDeadlandsMode()
	{
		if (SettingsHandler.getGame().equals(Constants.DEADLANDS_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
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

	// END Game Modes Section.



	/**
	 * Is someone debugging PCGen?
	 *
	 * @return boolean debugging state
	 */
	public static boolean isDebugMode()
	{
		return debugMode;
	}

	public static void setDefaultPath(String defaultPath)
	{
		Globals.defaultPath = defaultPath;
	}

	public static String getDefaultPath()
	{
		return defaultPath;
	}

	public static void setDefaultPcgPath(String defaultPcgPath)
	{
		Globals.defaultPcgPath = defaultPcgPath;
	}

	public static String getDefaultPcgPath()
	{
		return defaultPcgPath;
	}

	/**
	 * Returns the name of the Default Spell Book.
	 * Usually "Known Spells" but "Full Reference Spells" for Sovereign Stone.
	 */
	public static String getDefaultSpellBook()
	{
		if (isSSd20Mode())
		{
			return "Full Reference Spells";
		}
		else
		{
			return "Known Spells";
		}
	}

	public static Deity getDeityKeyed(String aKey)
	{
		return (Deity)searchPObjectList(getDeityList(), aKey);
	}

	public static void setDeityList(ArrayList deityList)
	{
		Globals.deityList = deityList;
	}

	public static ArrayList getDeityList()
	{
		return deityList;
	}

	public static Deity getDeityNamed(String name)
	{
		return getDeityNamed(name, getDeityList());
	}

	public static Deity getDeityNamed(String name, List aList)
	{

		Deity d = null;

		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			d = (Deity)e.next();

			if (d.getName().equalsIgnoreCase(name))
			{
				return d;
			}
		}

		return null;
	}

	public static void setDenominationList(DenominationList denominationList)
	{
		Globals.denominationList = denominationList;
	}

	public static DenominationList getDenominationList()
	{
		return denominationList;
	}

	public static void setDescriptorSet(TreeSet descriptorSet)
	{
		Globals.descriptorSet = descriptorSet;
	}

	public static TreeSet getDescriptors()
	{
		return getDescriptorSet();
	}

	public static void setDieSizes(int[] dieSizes)
	{
		Globals.dieSizes = dieSizes;
	}

	public static int[] getDieSizes()
	{
		return dieSizes;
	}

	public static boolean isDndMode()
	{
		if (SettingsHandler.getGame().equals(Constants.DND_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Domain getDomainKeyed(String aKey)
	{
		return (Domain)searchPObjectList(getDomainList(), aKey);
	}

	public static void setDomainList(ArrayList domainList)
	{
		Globals.domainList = domainList;
	}

	public static ArrayList getDomainList()
	{
		return domainList;
	}

	public static Domain getDomainNamed(String name)
	{

		Domain d = null;

		for (Iterator e = getDomainList().iterator(); e.hasNext();)
		{
			d = (Domain)e.next();

			if (d.getName().equalsIgnoreCase(name))
			{
				return d;
			}
		}

		return null;
	}

	public static TreeSet getEffectTypes()
	{
		return getEffectTypesSet();
	}

	public static void setEffectTypesSet(TreeSet effectTypesSet)
	{
		Globals.effectTypesSet = effectTypesSet;
	}

	public static TreeSet getEffectTypesSet()
	{
		return effectTypesSet;
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

				for (iSize = Constants.SIZE_F; iSize <= Constants.SIZE_C; iSize++)
				{
					if (cString.equalsIgnoreCase(Constants.s_SIZELONG[iSize]))
					{
						break;
					}
				}

				if (iSize <= Constants.SIZE_C)
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
					namList.add(0, namePart); // add to the start as otherwise the list will be reversed
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


		//debugPrint(aName + ":" + namList + ":" + modList + ":" + sizList);
		String omitString = "";
		String bonusString = "";

		for (; ;)
		{

			final String eqName = aName + bonusString;
			eq = findEquipment(eqName, null, namList, sizList, omitString);

			if (eq != null)
			{
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, sizList, omitString);

			if (eq != null)
			{
				if (sizList.size() > 1) // was used in name, ignore as modifier
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
					errorPrint("Could not find a qualified modifier named: " + namePart + " for " + eq.getName() + ":" + eq.typeList());
					bError = true;
				}
			}

			//
			// Found what appeared to be the base item,
			// but one of the modifiers is not qualified
			// to be attached to the item
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
					errorPrint("Too many sizes in item name, used only 1st of: " + sizList);
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

		return eq;
	}

	public static Equipment getEquipmentKeyed(String aKey)
	{
		return (Equipment)searchPObjectList(getEquipmentList(), aKey);
	}

	public static void setEquipmentList(ArrayList equipmentList)
	{
		Globals.equipmentList = equipmentList;
	}

	public static ArrayList getEquipmentList()
	{
		return equipmentList;
	}

	public static Equipment getEquipmentNamed(String name)
	{
		return getEquipmentNamed(name, getEquipmentList());
	}

	public static Equipment getEquipmentNamed(String name, List aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{

			final Equipment eq = (Equipment)e.next();

			if (eq.getName().equalsIgnoreCase(name) || (eq.getKeyName().equalsIgnoreCase(name)))
			{
				return eq;
			}
		}

		return null;
	}

	public static ArrayList getEquipmentOfType(ArrayList eqList, String desiredTypes, String excludedTypes)
	{

		ArrayList desiredTypeList = Utility.split(desiredTypes, '.');
		ArrayList excludedTypeList = Utility.split(excludedTypes, '.');
		ArrayList typeList = new ArrayList(100);

		for (Iterator e = eqList.iterator(); e.hasNext();)
		{

			final Equipment eq = (Equipment)e.next();
			boolean addIt = true;

			//
			// Must have all of the types in the desired list
			//
			for (Iterator e2 = desiredTypeList.iterator(); e2.hasNext();)
			{
				if (!eq.isType((String)e2.next()))
				{
					addIt = false;
					break;
				}
			}

			if (addIt)
			{

				//
				// Can't have any of the types on the excluded list
				//
				for (Iterator e3 = excludedTypeList.iterator(); e3.hasNext();)
				{
					if (eq.isType((String)e3.next()))
					{
						addIt = false;
						break;
					}
				}
			}

			if (addIt)
			{
				typeList.add(eq);
			}
		}

		return typeList;
	}

	public static boolean isFSd20Mode()
	{
		if (SettingsHandler.getGame().equals(Constants.FADINGSUNSD20_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Feat getFeatKeyed(String aKey)
	{
		return (Feat)searchPObjectList(getFeatList(), aKey);
	}

	public static void setFeatList(ArrayList featList)
	{
		Globals.featList = featList;
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
		return (Feat)getFeatList().get(featNo);
	}

	/**
	 * Convenience method that returns the toString of a feat.
	 */
	public static String getFeatListFeatString(final int featNo)
	{
		return getFeatList().get(featNo).toString();
	}

	public static Feat getFeatNamed(String name)
	{

		final int i = name.indexOf("(");

		if (i > -1)
		{

			final Feat f = getFeatNamed(name.substring(0, i).trim());

			if (f != null)
			{
				return f;
			}
		}

		for (Iterator e = getFeatList().iterator(); e.hasNext();)
		{

			final Feat f = (Feat)e.next();

			if (f.getName().equalsIgnoreCase(name))
			{
				return f;
			}
		}

		return null;
	}

	public static void setGameMode(String gameMode)
	{
		SettingsHandler.setGame(gameMode);
	}

	public static String getGameMode()
	{
		return SettingsHandler.getGame();
	}

	public static void setHPPct(int hpPct)
	{
		Globals.hpPct = hpPct;
	}

	public static int getHPPct()
	{
		return hpPct;
	}

	public static boolean isHackMasterMode()
	{
		if (SettingsHandler.getGame().equals(Constants.HACKMASTER_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setHairStyleList(ArrayList hairStyleList)
	{
		Globals.hairStyleList = hairStyleList;
	}

	public static ArrayList getHairStyleList()
	{
		return hairStyleList;
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

	// BEGIN Game Modes Section.
	public static boolean isInGameMode(String gameMode)
	{
		if (gameMode.equals(""))
		{
			return true;
		}
		else if (gameMode.equalsIgnoreCase(Constants.DEADLANDS_MODE))
		{
			return isDeadlandsMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.DND_MODE))
		{
			return isDndMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.FADINGSUNSD20_MODE))
		{
			return isFSd20Mode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.HACKMASTER_MODE))
		{
			return isHackMasterMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.HWNMNBSOL_MODE))
		{
			return isCoCd20Mode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.L5R_MODE))
		{
			return isL5rMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.SIDEWINDER_MODE))
		{
			return isSidewinderMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.SOVEREIGNSTONED20_MODE))
		{
			return isSSd20Mode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.STARWARS_MODE))
		{
			return isStarWarsMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.SPYCRAFT_MODE))
		{
			return isSpycraftMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.WEIRDWARS_MODE))
		{
			return isWeirdWarsMode();
		}
		else if (gameMode.equalsIgnoreCase(Constants.WHEELOFTIME_MODE))
		{
			return isWheelMode();
		}
		else
		{
			return false;
		}
	}

	public static void setInterestsList(ArrayList interestsList)
	{
		Globals.interestsList = interestsList;
	}

	public static ArrayList getInterestsList()
	{
		return interestsList;
	}

	public static boolean isL5rMode()
	{
		if (SettingsHandler.getGame().equals(Constants.L5R_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setLanguage(String aString)
	{
		language = aString;
	}

	public static String getLanguage()
	{
		return language;
	}

	public static void setLanguageList(ArrayList languageList)
	{
		Globals.languageList = languageList;
	}

	public static ArrayList getLanguageList()
	{
		return languageList;
	}

	public static Language getLanguageNamed(String name)
	{
		for (Iterator i = getLanguageList().iterator(); i.hasNext();)
		{

			Language aLang = (Language)i.next();

			if (aLang.getName().equalsIgnoreCase(name))
			{
				return aLang;
			}

			aLang = null;
		}

		return null;
	}

	public static ArrayList getLanguageSetNames()
	{

		ArrayList aList = new ArrayList();

		for (Iterator i = getLanguageList().iterator(); i.hasNext();)
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

			if (aLang != null && ((aType.startsWith("!") && !aLang.isType(aType)) || aLang.isType(aType)))
			{
				retSet.add(aLang);
			}
		}

		return retSet;
	}

	public static void setLoadStrings(ArrayList loadStrings)
	{
		Globals.loadStrings = loadStrings;
	}

	public static ArrayList getLoadStrings()
	{
		return loadStrings;
	}

	public static List getLoadStringsList()
	{
		return getLoadStrings();
	}

	public static void setLocationList(ArrayList locationList)
	{
		Globals.locationList = locationList;
	}

	public static ArrayList getLocationList()
	{
		return locationList;
	}

	public static String getLongCurrencyDisplay()
	{
		if (isStarWarsMode())
		{
			return "Credits";
		}
		if (isSpycraftMode())
		{
			return "Budget Points";
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

	public static EquipmentModifier getModifierKeyed(String aKey)
	{
		return (EquipmentModifier)searchPObjectList(getModifierList(), aKey);
	}

	public static void setModifierList(ArrayList modifierList)
	{
		Globals.modifierList = modifierList;
	}

	public static ArrayList getModifierList()
	{
		return modifierList;
	}

	public static EquipmentModifier getModifierNamed(String aName)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier)e.next();

			if (aEqMod.getName().equals(aName))
			{
				return aEqMod;
			}
		}

		return null;
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

	public static void setPCList(ArrayList pcList)
	{
		Globals.pcList = pcList;
	}

	public static ArrayList getPCList()
	{
		return pcList;
	}

	public static void setPCClassTypeList(ArrayList pcClassTypeList)
	{
		Globals.pcClassTypeList = pcClassTypeList;
	}

	public static ArrayList getPCClassTypeList()
	{
		return pcClassTypeList;
	}

	public static TreeSet getPantheons()
	{
		return getPantheonsSet();
	}

	public static void setPantheonsSet(TreeSet pantheonsSet)
	{
		Globals.pantheonsSet = pantheonsSet;
	}

	public static TreeSet getPantheonsSet()
	{
		return pantheonsSet;
	}

	public static int getPaperCount()
	{
		return getPaperInfo().size();
	}

	public static void setPaperInfo(ArrayList paperInfo)
	{
		Globals.paperInfo = paperInfo;
	}

	public static String getPaperInfo(int infoType)
	{
		return getPaperInfo(getSelectedPaper(), infoType);
	}

	public static String getPaperInfo(int idx, int infoType)
	{
		if ((idx < 0) || (idx >= getPaperInfo().size()))
		{
			return null;
		}

		final PaperInfo pi = (PaperInfo)getPaperInfo().get(idx);
		return pi.getPaperInfo(infoType);
	}

	public static ArrayList getPaperInfo()
	{
		return paperInfo;
	}

	public static void setPhobiaList(ArrayList phobiaList)
	{
		Globals.phobiaList = phobiaList;
	}

	public static ArrayList getPhobiaList()
	{
		return phobiaList;
	}

	public static void setPhraseList(ArrayList phraseList)
	{
		Globals.phraseList = phraseList;
	}

	public static ArrayList getPhraseList()
	{
		return phraseList;
	}

	public static EquipmentModifier getQualifiedModifierNamed(String aName, ArrayList aType)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
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
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
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

	public static Race getRaceKeyed(String aKey)
	{
		return (Race)getRaceMap().get(aKey);
	}

	public static void setRaceMap(Map raceMap)
	{
		Globals.raceMap = raceMap;
	}

	public static Map getRaceMap()
	{
		return raceMap;
	}

	public static Race getRaceNamed(String aName)
	{
		return (Race)getRaceMap().get(aName);
	}

	public static TreeSet getRaceTypes()
	{
		return getRaceTypesSet();
	}

	public static void setRaceTypesSet(TreeSet raceTypesSet)
	{
		Globals.raceTypesSet = raceTypesSet;
	}

	public static TreeSet getRaceTypesSet()
	{
		return raceTypesSet;
	}

	public static Random getRandom()
	{
		return random;
	}

	/**
	 * Get a random integer between 0 (inclusive) and the given value (exclusive)
	 */
	public static int getRandomInt(final int high)
	{
		return getRandom().nextInt(high);
	}

	public static int getRandomInt()
	{
		return getRandom().nextInt();
	}

	public static TreeSet getRanges()
	{
		return getRangesSet();
	}

	public static void setRangesSet(TreeSet rangesSet)
	{
		Globals.rangesSet = rangesSet;
	}

	public static TreeSet getRangesSet()
	{
		return rangesSet;
	}

	/**
	 * Sets the root frame. The root frame has something to do
	 * with the GUI. ???
	 *
	 * @param  frame  the <code>PCGen_Frame1</code> which is to be root
	 */
	public static void setRootFrame(PCGen_Frame1 frame)
	{
		rootFrame = frame;
	}

	/**
	 * Returns the current root frame.
	 *
	 * @return    the <code>rootFrame</code> property
	 */
	public static PCGen_Frame1 getRootFrame()
	{
		return rootFrame;
	}

	public static TreeSet getSRs()
	{
		return getSrSet();
	}

	public static boolean isSSd20Mode()
	{
		if (SettingsHandler.getGame().equals(Constants.SOVEREIGNSTONED20_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setSchoolsList(ArrayList schoolsList)
	{
		Globals.schoolsList = schoolsList;
	}

	public static ArrayList getSchoolsList()
	{
		return schoolsList;
	}

	public static void setSelectedPaper(int selectedPaper)
	{
		Globals.selectedPaper = selectedPaper;
	}

	public static int getSelectedPaper()
	{
		return selectedPaper;
	}

	public static boolean isSidewinderMode()
	{
		if (SettingsHandler.getGame().equals(Constants.SIDEWINDER_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setSizeAdjustmentList(ArrayList sizeAdjustmentList)
	{
		Globals.sizeAdjustmentList = sizeAdjustmentList;
	}

	public static List getSizeAdjustmentList()
	{
		return sizeAdjustmentList;
	}

	public static Skill getSkillKeyed(String aKey)
	{
		return (Skill)searchPObjectList(getSkillList(), aKey);
	}

	public static void setSkillList(ArrayList skillList)
	{
		Globals.skillList = skillList;
	}

	public static ArrayList getSkillList()
	{
		return skillList;
	}

	public static Skill getSkillNamed(String name)
	{

		Skill s = null;

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			s = (Skill)e.next();

			if (s.getName().equalsIgnoreCase(name))
			{
				return s;
			}
		}

		return null;
	}

	public static void setSorted(boolean sorted)
	{
		setD_sorted(sorted);
	}

	public static void setSourceDisplay(int sourceType)
	{
		sourceDisplay = sourceType;
	}

	public static int getSourceDisplay()
	{
		return sourceDisplay;
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

		for (Iterator e = getSpecialsList().iterator(); e.hasNext();)
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

	public static void setSpecialsList(ArrayList specialsList)
	{
		Globals.specialsList = specialsList;
	}

	public static ArrayList getSpecialsList()
	{
		return specialsList;
	}

	public static void setSpeechList(ArrayList speechList)
	{
		Globals.speechList = speechList;
	}

	public static ArrayList getSpeechList()
	{
		return speechList;
	}

	public static Spell getSpellKeyed(String aKey)
	{
		return (Spell)getSpellMap().get(aKey);
	}

	public static void setSpellMap(Map spellMap)
	{
		Globals.spellMap = spellMap;
	}

	public static Map getSpellMap()
	{
		return spellMap;
	}

	public static Spell getSpellNamed(String name)
	{
		return (Spell)getSpellMap().get(name);
	}

	public static void setSpellPoints(String aString)
	{
		Globals.spellPoints = aString;
	}

	public static String getSpellPoints()
	{
		return spellPoints;
	}

	public static void setSrSet(TreeSet srSet)
	{
		Globals.srSet = srSet;
	}

	public static TreeSet getSrSet()
	{
		return srSet;
	}

	public static boolean isStarWarsMode()
	{
		if (SettingsHandler.getGame().equals(Constants.STARWARS_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean isSpycraftMode()
	{
		if (SettingsHandler.getGame().equals(Constants.SPYCRAFT_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns the index of the requested attribute abbreviation,
	 * The attributes used are loaded from a lst file
	 *
	 * @param attributeAbbreviation to find the index of
	 * @return the index of the attribute
	 *         returns -1 if the attribute is not matched (or null)
	 */
	public static int getStatFromAbbrev(String attributeAbbreviation)
	{
		for (int stat = 0; stat < s_ATTRIBSHORT.length; stat++)
		{
			if (attributeAbbreviation.equalsIgnoreCase(s_ATTRIBSHORT[stat]))
			{
				return stat;
			}
		}

		return -1;
	}

	/**
	 * Returns the index of the requested attribute full name,
	 * The attributes used are loaded from a lst file
	 *
	 * @param attributeFullName to find the index of
	 * @return the index of the attribute
	 *         returns -1 if the attribute is not matched (or null)
	 */
	public static int getStatFromLongName(String attributeFullName)
	{
		for (int stat = 0; stat < s_ATTRIBLONG.length; stat++)
		{
			if (attributeFullName.equalsIgnoreCase(s_ATTRIBLONG[stat]))
			{
				return stat;
			}
		}

		return -1;
	}

	public static TreeSet getSubschools()
	{
		return getSubschoolsSet();
	}

	public static void setSubschoolsSet(TreeSet subschoolsSet)
	{
		Globals.subschoolsSet = subschoolsSet;
	}

	public static TreeSet getSubschoolsSet()
	{
		return subschoolsSet;
	}

	public static PCTemplate getTemplateKeyed(String aKey)
	{
		return (PCTemplate)searchPObjectList(getTemplateList(), aKey);
	}

	public static void setTemplateList(ArrayList templateList)
	{
		Globals.templateList = templateList;
	}

	public static ArrayList getTemplateList()
	{
		return templateList;
	}

	public static PCTemplate getTemplateNamed(String name)
	{

		PCTemplate t = null;

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			t = (PCTemplate)e.next();

			if (t.getName().equalsIgnoreCase(name))
			{
				return t;
			}
		}

		return null;
	}

	public static void setTraitList(ArrayList traitList)
	{
		Globals.traitList = traitList;
	}

	public static ArrayList getTraitList()
	{
		return traitList;
	}

	public static void setUseGUI(boolean aBool)
	{
		useGUI = aBool;
	}

	public static boolean getUseGUI()
	{
		return isUseGUI();
	}

	public static boolean isUseGUI()
	{
		return useGUI;
	}

	/**
	 * @return true if the weapon is light for the specified pc
	 */
	public static boolean isWeaponLightForPC(PlayerCharacter pc, Equipment weapon)
	{
		return ((pc != null) && (weapon != null) && (pc.sizeInt() > Globals.sizeInt(weapon.getSize())));
	}

	/**
	 * @return true if the weapon is one-handed for the specified pc
	 */
	public static boolean isWeaponOneHanded(PlayerCharacter pc, Equipment weapon, WeaponProf wp)
	{
		return isWeaponOneHanded(pc, weapon, wp, false);
	}

	public static boolean isWeaponOneHanded(PlayerCharacter pc, Equipment weapon, WeaponProf wp, boolean baseOnly)
	{
		if ((pc != null) && (weapon != null) && (wp != null) && (wp.getHands() == 1))
		{

			int pcSize = pc.sizeInt();

			if (!baseOnly)
			{
				pcSize += pc.getTotalBonusTo("WEAPONPROF=" + wp.getName(), "PCSIZE", true);
			}

			if (pcSize >= Globals.sizeInt(weapon.getSize()))
			{
				return true;
			}
		}

		return false;
	}

	public static WeaponProf getWeaponProfKeyed(String aKey)
	{
		return (WeaponProf)searchPObjectList(getWeaponProfList(), aKey);
	}

	public static void setWeaponProfList(ArrayList weaponProfList)
	{
		Globals.weaponProfList = weaponProfList;
	}

	public static ArrayList getWeaponProfList()
	{
		return weaponProfList;
	}

	public static WeaponProf getWeaponProfNamed(String name)
	{

		WeaponProf aProf = null;

		for (Iterator e = getWeaponProfList().iterator(); e.hasNext();)
		{
			aProf = (WeaponProf)e.next();

			if (aProf.getName().equalsIgnoreCase(name))
			{
				return aProf;
			}
		}

		return null;
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

		for (Iterator e = getCurrentPC().getRace().getWeaponProfs().iterator(); e.hasNext();)
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

		for (Iterator e = getWeaponProfList().iterator(); e.hasNext();)
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

	/**
	 * @return true if the weapon is too large for the specified pc.
	 */
	public static boolean isWeaponTooLargeForPC(PlayerCharacter pc, Equipment weapon)
	{
		return ((pc != null) && (weapon != null) && (pc.sizeInt() < (Globals.sizeInt(weapon.getSize()) - 1)));
	}

	/**
	 * @return true if the weapon is two-handed for the specified pc
	 */
	public static boolean isWeaponTwoHanded(PlayerCharacter pc, Equipment weapon, WeaponProf wp)
	{
		return isWeaponTwoHanded(pc, weapon, wp, false);
	}

	public static boolean isWeaponTwoHanded(PlayerCharacter pc, Equipment weapon, WeaponProf wp, boolean baseOnly)
	{
		if ((pc != null) && (weapon != null) && (wp != null))
		{

			int pcSize = pc.sizeInt();

			if (!baseOnly)
			{
				pcSize += pc.getTotalBonusTo("WEAPONPROF=" + wp.getName(), "PCSIZE", true);
			}

			if ((pcSize == (Globals.sizeInt(weapon.getSize()) - 1)) || (wp.getHands() == 2))
			{
				return true;
			}
		}

		return false;
	}

	public static void setWeaponTypes(TreeSet weaponTypes)
	{
		Globals.weaponTypes = weaponTypes;
	}

	public static TreeSet getWeaponTypes()
	{
		return weaponTypes;
	}

	/**
	 * Returns an ArrayList of Spell with following criteria:
	 * @param level         (optional, ignored if < 0),
	 * @param className     (optional, ignored if "")
	 * @param domainName    (optional, ignored if "")
	 * at least one of className and domainName must not be ""
	 * @return an ArrayList of Spell
	 */
	public static ArrayList getSpellsIn(final int level, final String className, final String domainName)
	{
		ArrayList aList = new ArrayList();
		for (Iterator i = spellMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = (String)i.next();
			final Spell aSpell = (Spell)spellMap.get(aKey);
			boolean added = false;
			if (!className.equals(""))
			{
				if (aSpell.containsKeyTypeLevel("CLASS", className, level))
				{
					added = true;
					aList.add(aSpell);
				}
			}
			if (!added && !domainName.equals(""))
			{
				if (aSpell.containsKeyTypeLevel("DOMAIN", domainName, level))
				{
					added = true;
					aList.add(aSpell);
				}
			}
		}
		return aList;
	}

	/* Returns the string to use for displaying weight.
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

	public static boolean isWeirdWarsMode()
	{
		if (SettingsHandler.getGame().equals(Constants.WEIRDWARS_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean isWheelMode()
	{
		if (SettingsHandler.getGame().equals(Constants.WHEELOFTIME_MODE))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static void setXPList(ArrayList XPList)
	{
		Globals.XPList = XPList;
	}

	public static List getXPList()
	{
		return XPList;
	}

	public static boolean addEquipment(Equipment aEq)
	{
		if (getEquipmentKeyed(aEq.getKeyName()) != null)
		{
			return false;
		}

		if (!aEq.isType(Constants.s_CUSTOM))
		{
			aEq.addMyType(Constants.s_CUSTOM);
		}

		//
		// Make sure all the equipment types are present in the sorted list
		//
		aEq.getEquipmentTypes().addAll(aEq.typeList());
		getEquipmentList().add(aEq);

		if (!isbAutoGeneration())
		{
			sortPObjectList(getEquipmentList());
		}

		return true;
	}

	/**
	 * Reduce/increase damage for modified size as per DMG p.162
	 */
	public static String adjustDamage(String aDamage, String sBaseSize, String sNewSize)
	{
		if (aDamage.length() == 0)
		{
			return aDamage;
		}

		final int baseSize = sizeInt(sBaseSize);
		int itemSize = sizeInt(sNewSize);
		RollInfo aRollInfo = new RollInfo(aDamage);

		if ((itemSize < 0) || (itemSize == baseSize) || (aRollInfo == null))
		{
			return aDamage;
		}

		//
		// Handle size increase
		//
		while (itemSize > baseSize)
		{
			switch (aRollInfo.sides)
			{

				case 0:
					break;

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
					errorPrint("Size increase, unknown die size: " + Integer.toString(aRollInfo.sides) + ":" + aDamage + ":" + sBaseSize + ":" + sNewSize);
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

				case 0:
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
					debugPrint("Size decrease, unknown die size: " + Integer.toString(aRollInfo.sides) + ":" + aDamage + ":" + sBaseSize + ":" + sNewSize);
					return aDamage;
			}

			itemSize += 1;
		}

		final String retValue = Integer.toString(aRollInfo.times) + "d" + Integer.toString(aRollInfo.sides);
		return retValue;
	}

	public static void appendStatName(String newStat)
	{
		s_STATNAMES += newStat;
	}

	//
	// Generate masterwork, +1 to +5 armor, shields, weapons
	//
	public static void autoGenerateEquipment()
	{
		setbAutoGeneration(true);

		if (SettingsHandler.isAutogenRacial())
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

				gensizes[iSize] |= flag;
			}


			for (int i = getEquipmentList().size() - 1; i >= 0; i--)
			{

				final Equipment eq = (Equipment)getEquipmentList().get(i);

				//
				// Only apply to Armor, Shield and resizable items
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isArmor() && !eq.isShield() && !eq.isType("RESIZABLE")))
				{
					continue;
				}

				for (int j = Constants.SIZE_F; j <= Constants.SIZE_C; j++)
				{
					if (j == Constants.SIZE_M)
					{
						continue;
					}

					if ((gensizes[j] & 0x01) != 0)
					{
						createItem(eq, j);
					}
				}
			}
		}

		if (SettingsHandler.isAutogenMasterwork())
		{
			for (int i = getEquipmentList().size() - 1; i >= 0; i--)
			{

				Equipment eq = (Equipment)getEquipmentList().get(i);

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

		if (SettingsHandler.isAutogenMagic())
		{
			for (int iPlus = 1; iPlus <= 5; iPlus++)
			{

				final String aBonus = Delta.toString(iPlus);

				for (int i = getEquipmentList().size() - 1; i >= 0; i--)
				{

					Equipment eq = (Equipment)getEquipmentList().get(i);

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

		if (SettingsHandler.isAutogenExoticMaterial())
		{
			for (int i = getEquipmentList().size() - 1; i >= 0; i--)
			{

				Equipment eq = (Equipment)getEquipmentList().get(i);

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

		setbAutoGeneration(false);
		sortPObjectList(getEquipmentList()); // Sort the equipment list
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
	 * Works for dnd according to the method noted in the faq. (NOTE: The table in the dnd faq is wrong for speeds 80 and
	 * 90. Calculate it yourself to see...)
	 * Not as sure it works for all other d20 games.
	 * @param load (0 = light, 1 = medium, 2 = heavy, 3 = overload)
	 * @param  unencumberedMove the unencumbered move value
	 * @return encumbered move as an integer
	 */
	public static int calcEncumberedMove(int load, int unencumberedMove)
	{
		return calcEncumberedMove(load, unencumberedMove, SettingsHandler.isApplyLoadPenaltyToACandSkills());
	}

	/**
	 * Works for dnd according to the method noted in the faq. (NOTE: The table in the dnd faq is wrong for speeds 80 and
	 * 90. Calculate it yourself to see...)
	 * Not as sure it works for all other d20 games.
	 * @param load (0 = light, 1 = medium, 2 = heavy, 3 = overload)
	 * @param  unencumberedMove the unencumbered move value
	 * @return encumbered move as an integer
	 */
	public static int calcEncumberedMove(int load, int unencumberedMove, boolean checkLoad)
	{

		int encumberedMove = 0;

		if (checkLoad)
		{
			switch (load)
			{

				case Constants.LIGHT_LOAD:
					encumberedMove = unencumberedMove;
					break;

				case Constants.MEDIUM_LOAD:

					/* deliberately no break */
				case Constants.HEAVY_LOAD:
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

				case Constants.OVER_LOAD:
					encumberedMove = 0;
					break;

				default:
					errorPrint("The load " + load + " is not possible.");
					encumberedMove = 0;
					break;
			}
		}
		else
		{
			encumberedMove = unencumberedMove;
		}

		debugPrint("calcEncumberedMove: " + encumberedMove);
		return encumberedMove;
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
	 * Print information message if PCGen is debugging.
	 *
	 * @param s String information message
	 */
	public static void debugPrint(String s)
	{
		if (!Globals.isDebugMode())
		{
			return;
		}

		System.out.println(s);
	}

	/**
	 * Print information message if PCGen is debugging.
	 *
	 * @param param1 String information message (usually variable)
	 * @param param2 String information message (usually value)
	 */
	public static void debugPrint(String param1, String param2)
	{
		if (!Globals.isDebugMode())
		{
			return;
		}

		System.out.println(param1 + param2);
	}

	public static boolean displayListsHappy()
	{
		if (getRaceMap().size() == 0 || getClassList().size() == 0 || getSkillList().size() == 0 || getFeatList()
			.size() == 0 || getEquipmentList().size() == 0 || getWeaponProfList().size() == 0)
		{
			return false;
		}

		return true;
	}

	/**
	 * Clears all lists of game data.
	 */
	public static void emptyLists()
	{
		getRaceMap().clear();
		createEmptyRace();
		getClassList().clear();
		getSkillList().clear();
		getFeatList().clear();
		getEquipmentList().clear();
		getWeaponProfList().clear();
		getTemplateList().clear();
		getDeityList().clear();
		getDomainList().clear();
		getModifierList().clear();
		getLanguageList().clear();
		getSpellMap().clear();
		Equipment.clearEquipmentTypes();
	}

	/**
	 * Print error message if PCGen is debugging.
	 *
	 * @param s String error message
	 */
	public static void errorPrint(String s)
	{
		if (Globals.isDebugMode())
		{
			s_TOOLKIT.beep();
		}

		System.err.println(s);
	}

	/**
	 * Print error message with a stack trace if PCGen is
	 * debugging.
	 *
	 * @param s String error message
	 * @param ex Exception stack frames
	 */
	public static void errorPrint(String s, Exception ex)
	{
		errorPrint(s);
		ex.printStackTrace(System.err);
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
			{
				bonusLangsb.add(aLang);
			}
		}

		return bonusLangsb;
	}

	public static int handsNeededForWeapon(PlayerCharacter pc, Equipment weapon, WeaponProf wp)
	{
		if (isWeaponOneHanded(pc, weapon, wp))
		{
			return 1;
		}
		else if (isWeaponTwoHanded(pc, weapon, wp))
		{
			return 2;
		}
		else
		{
			return 3;
		}
	}

	public static void initCustColumnWidth(List l)
	{
		getCustColumnWidth().clear();
		getCustColumnWidth().addAll(l);
	}

	public static boolean isbAutoGeneration()
	{
		return bAutoGeneration;
	}

	public static void loadAttributeNames() throws PersistenceLayerException
	{

		// Note that game names cannot contain spaces or non-alphabetical characters. (I.e. only 0-9a-zA-Z)
		//The purpose of this weird construct is to handle non-existing modes, which will instead be set to dnd.
		if (isStarWarsMode())
		{
			SettingsHandler.setGame(Constants.STARWARS_MODE);
		}
		else if (isSpycraftMode())
		{
			SettingsHandler.setGame(Constants.SPYCRAFT_MODE);
		}
		else if (isWeirdWarsMode())
		{
			SettingsHandler.setGame(Constants.WEIRDWARS_MODE);
		}
		else if (isDeadlandsMode())
		{
			SettingsHandler.setGame(Constants.DEADLANDS_MODE);
		}
		else if (isSidewinderMode())
		{
			SettingsHandler.setGame(Constants.SIDEWINDER_MODE);
		}
		else if (isWheelMode())
		{
			SettingsHandler.setGame(Constants.WHEELOFTIME_MODE);
		}
		else if (isFSd20Mode())
		{
			SettingsHandler.setGame(Constants.FADINGSUNSD20_MODE);
		}
		else if (isSSd20Mode())
		{
			SettingsHandler.setGame(Constants.SOVEREIGNSTONED20_MODE);
		}
		else if (isHackMasterMode())
		{
			SettingsHandler.setGame(Constants.HACKMASTER_MODE);
		}
		else if (isCoCd20Mode())
		{
			SettingsHandler.setGame(Constants.HWNMNBSOL_MODE);
		}
		else
		{
			SettingsHandler.setGame(Constants.DND_MODE);
		}

		PersistenceManager.initFile(SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator + SettingsHandler.getGame() + ".lst", LstConstants.STATNAME_TYPE, new ArrayList()); /*STATNAME_TYPE*/

		createEmptyRace();
	}

	/**
	 * @return 0 = light, 1 = medium, 2 = heavy, 3 = overload
	 */
	public static int loadTypeForStrength(int strength, Float weight)
	{

		double dbl = weight.doubleValue() / maxLoadForStrength(strength).doubleValue();

		if (dbl <= .333)
		{
			return Constants.LIGHT_LOAD;
		}

		if (dbl <= .666)
		{
			return Constants.MEDIUM_LOAD;
		}

		if (dbl <= 1.0)
		{
			return Constants.HEAVY_LOAD;
		}

		return Constants.OVER_LOAD;
	}

	/**
	 * Size is taken into account for the currentPC.
	 */
	public static Float maxLoadForStrength(int strength)
	{

		double x = 0.0;
		double dbl = 0.0;
		int y = strength;
		int loadStringSize = getLoadStrings().size();

		if (strength >= loadStringSize - 2) //ok
		{

			final String bString = (String)getLoadStrings().get(loadStringSize - 1); //ok
			dbl = Double.parseDouble(bString.substring(bString.lastIndexOf('\t') + 1));

			for (y = strength; y >= loadStringSize - 2; y -= 10)
			{
				x += 1.0;
			}
		}

		final String aString = (String)getLoadStrings().get(y);
		Float aFloat = new Float(aString.substring(aString.lastIndexOf('\t') + 1));

		if (x > 0)
		{
			aFloat = new Float(aFloat.doubleValue() * Math.pow(dbl, x));
		}

		return new Float(aFloat.doubleValue() * getLoadMultForSize(getCurrentPC().getSize()));
	}

	/**
	 * Return the minimum level for selected class that spell can be cast
	 */
	public static int minCasterLevel(Spell aSpell, PCClass castingClass, boolean allowBonus, int levelAdjustment)
	{

		final int spellLevel = aSpell.levelForKey(castingClass.getSpellKey()) + levelAdjustment;

		if (spellLevel != 9999)
		{
			return minLevelForSpellLevel(castingClass, spellLevel, allowBonus);
		}

		return 9999;
	}

	public static int minLevelForSpellLevel(PCClass castingClass, int spellLevel, boolean allowBonus)
	{

		int minLevel = 9999;
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
					catch (NumberFormatException e)
					{

						//ignore???
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

		return minLevel;
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
		if (isD_sorted())
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

				if (keyName.equals(obj.getKeyName()))
				{
					return obj;
				}
			}
		}

		return null;
	}

	public static boolean selectPaper(String paperName)
	{
		for (int i = 0; i < getPaperInfo().size(); i++)
		{

			final PaperInfo pi = (PaperInfo)getPaperInfo().get(i);

			if (pi.getName().equals(paperName))
			{
				setSelectedPaper(i);
				return true;
			}
		}

		setSelectedPaper(-1);
		return false;
	}

	public static void setbAutoGeneration(boolean bAutoGeneration)
	{
		Globals.bAutoGeneration = bAutoGeneration;
	}

	public static double sizeAdjustmentACModMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "AC");
	}

	public static double sizeAdjustmentCapacityMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "Capacity");
	}

	/**
	 * Return the multiplier for converting from Medium size
	 * to specified size. PHB refers to armor only
	 */
	public static double sizeAdjustmentCostMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "Cost");
	}

	public static double sizeAdjustmentMultiplier(String aSize, ArrayList typeList, String adjustmentType)
	{

		double mult = 1.0;

		for (Iterator e = getSizeAdjustmentList().iterator(); e.hasNext();)
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

	public static double sizeAdjustmentWeightMultiplier(String aSize, ArrayList typeList)
	{
		return sizeAdjustmentMultiplier(aSize, typeList, "Weight");
	}

	public static void sortCampaigns()
	{
		setRaceMap(new TreeMap(getRaceMap()));
		sortPObjectList(getClassList());
		sortPObjectList(getSkillList());
		sortPObjectList(getFeatList());
		sortPObjectList(getDeityList());
		sortPObjectList(getDomainList());
		setSpellMap(new TreeMap(getSpellMap()));
		sortPObjectList(getEquipmentList());
		sortPObjectList(getWeaponProfList());
		sortPObjectList(getTemplateList());
		sortPObjectList(getModifierList());
		setD_sorted(true);
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
			stringsInList = selectedList.get(0) instanceof String;
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

	public static List sortPObjectList(List aList)
	{
		Collections.sort(aList, new PObjectComp());
		return aList;
	}

	public static List sortPObjectListByName(List aList)
	{
		Collections.sort(aList, new PObjectCompByName());
		return aList;
	}

	/**
	 * Sets the system to use metric unit displays
	 */
	public static boolean useMetric()
	{

		// currently Star Wars requires metric measurement
		return isStarWarsMode();
	}

	public static void writeCustomItems()
	{

		//
		// Don't trash the file if user exits before loading custom items
		//
		if (!PersistenceManager.isCustomItemsLoaded() || !SettingsHandler.isSaveCustomInLst())
		{
			return;
		}

		BufferedWriter bw = getCustomEquipmentWriter();

		if (bw == null)
		{
			return;
		}

		try
		{
			bw.write("#This file auto-generated by PCGen. Do not edit manually.");
			bw.newLine();

			for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
			{

				final Equipment aEq = (Equipment)e.next();

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

	public static int xPLevelOffset(int level)
	{

		int offset = 0;
		final StringTokenizer aTok = new StringTokenizer((String)getXPList().get(level), ",", false);

		if (aTok.hasMoreTokens())
		{
			offset = Integer.parseInt(aTok.nextToken().trim());
		}

		return offset;
	}

	public static int xPLevelValue(int level, int cr)
	{

		int i = 0;
		int xp = 666;
		final StringTokenizer aTok = new StringTokenizer((String)getXPList().get(level), ",", false);

		while (aTok.hasMoreTokens() && i++ < cr + 1)
		{
			xp = Integer.parseInt(aTok.nextToken().trim());
		}

		return xp;
	}

	public static final int sizeInt(final String aSize)
	{
		for (int iSize = Constants.SIZE_F; iSize <= Constants.SIZE_C; iSize++)
		{
			if (aSize.startsWith(Constants.s_SIZESHORT[iSize]))
			{
				return iSize;
			}
		}

		return Constants.SIZE_M;
	}

	private static TreeSet getCastingTimesSet()
	{
		return castingTimesSet;
	}

	private static TreeSet getDescriptorSet()
	{
		return descriptorSet;
	}

	private static ArrayList getLanguageNamesFromListOfType(ArrayList langList, String aType)
	{

		ArrayList retSet = new ArrayList();

		for (Iterator i = langList.iterator(); i.hasNext();)
		{
			Language aLang = (Language)i.next();

			if (aLang != null && ((aType.startsWith("!") && !aLang.isType(aType)) || aLang.isType(aType)))
			{
				retSet.add(aLang.getName());
			}
		}
		return retSet;
	}

	private static double getLoadMultForSize(String aString)
	{
		if (getLoadStrings().size() != 0)
		{

			final String bString = (String)getLoadStrings().get(0);
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

	private static void createEmptyRace()
	{
		s_EMPTYRACE = new Race();
		s_EMPTYRACE.setName(Constants.s_NONESELECTED);
		getRaceMap().put(Constants.s_NONESELECTED, s_EMPTYRACE);
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
				if (!eq.getModifiersAllowed() || (eq.isArmor() && (eq.getACMod().intValue() == 0) && ((eqMod != null) && !eqMod
					.getName().equalsIgnoreCase("MASTERWORK"))))
				{
					return null;
				}

				eq = (Equipment)eq.clone();

				if (eq == null)
				{
					errorPrint("could not clone item");
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

				if ((iSize >= Constants.SIZE_F) && (iSize <= Constants.SIZE_C))
				{
					eq.resizeItem(Constants.s_SIZESHORT[iSize]);
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

				if (isbAutoGeneration())
				{
					newType = "AUTO_GEN";
				}
				else
				{
					newType = Constants.s_CUSTOM;
				}

				if (!eq.isType(newType))
				{
					eq.addMyType(newType);
				}

				//
				// Make sure all the equipment types are present in the sorted list
				//
				eq.getEquipmentTypes().addAll(eq.typeList());

				getEquipmentList().add(eq);
				return eq;
			}
			catch (NumberFormatException exception)
			{
				errorPrint("createItem: exception: " + eq.getName());
			}
		}

		return null;
	}

	///////////////////////////////////////////////////////////////////

	// Options
	private static String customEquipmentFilePath()
	{
		return SettingsHandler.getPccFilesLocation().getAbsolutePath() + File.separator + "customsources" + File.separator + "customEquipment.lst";
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

		return eq;
	}


	private static class PObjectComp implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return ((PObject)o1).getKeyName().compareTo(((PObject)o2).getKeyName());
		}
	}


	private static class PObjectCompByName implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return ((PObject)o1).getName().compareTo(((PObject)o2).getName());
		}
	}
}
