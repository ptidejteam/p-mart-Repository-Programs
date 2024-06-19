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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:39 $
 *
 */
package pcgen.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSlot;
import pcgen.core.money.DenominationList;
import pcgen.core.spell.Spell;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.GuiFacade;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.persistence.lst.LstConstants;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * This is like the top level model container. However,
 * it is build from static methods rather than instantiated.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 **/
public final class Globals
{

	/** These are changed during normal operation */
	private static PlayerCharacter currentPC = null;
	private static List pcList = new ArrayList();
	public static boolean[] s_ATTRIBROLL = null;
	public static Race s_EMPTYRACE = null;
	public static String[] s_ATTRIBLONG = null;
	public static String[] s_ATTRIBSHORT = null;

	/** This is true when the campaign data structures are sorted. */
	private static boolean d_sorted = false;

	/** These are system constants */
	public static final String javaVersion = System.getProperty("java.version");
	private static String pcGenVersion; // set by static initializer
	public static final int javaVersionMajor = Integer.valueOf(javaVersion.substring(0, javaVersion.indexOf('.'))).intValue();
	public static final int javaVersionMinor = Integer.valueOf(javaVersion.substring(javaVersion.indexOf('.') + 1, javaVersion.lastIndexOf('.'))).intValue();

	/** NOTE: The defaultPath is duplicated in LstSystemLoader. */
	private static final String defaultPath = System.getProperty("user.dir");
	private static final String defaultPcgPath = getDefaultPath() + File.separator + "characters";
	private static final int[] dieSizes = new int[]{1, 2, 3, 4, 6, 8, 10, 12, 20, 100, 1000};

	/** These are for the Internationalization project. */
	private static String language = "en";
	private static String country = "US";

	/** The BioSet used for age calculations */
	private static BioSet bioSet = new BioSet();

	/** these are determined by preferences */
	private static boolean bAutoGeneration = false;
	private static final List custColumnWidth = new ArrayList();
	private static int sourceDisplay = Constants.SOURCELONG;
	private static int selectedPaper = -1;

	/** we need maps for efficient lookups */
	private static Map domainMap = new TreeMap();
	private static SortedMap raceMap = new TreeMap();
	private static Map spellMap = new TreeMap();
	private static Map eqSlotMap = new HashMap();
	private static Map visionMap = new HashMap();

	/** We use lists for efficient iteration */
	private static List armorProfList = new ArrayList();
	private static List campaignList = new ArrayList(85);
	private static List classList = new ArrayList(380);
	private static List companionModList = new ArrayList();
	private static List deityList = new ArrayList(275);
	private static List domainList = new ArrayList(100);
	private static List equipmentList = new ArrayList(4000);
	private static List featList = new ArrayList(1200);
	private static List kitList = new ArrayList();
	private static List languageList = new ArrayList(200);
	private static List modifierList = new ArrayList(230);
	private static List pcClassTypeList = new ArrayList(); //any TYPE added to this list is assumed be pre-tokenized
	private static List skillList = new ArrayList(400);
	private static List templateList = new ArrayList(350);
	private static DenominationList denominationList = new DenominationList(); // derived from ArrayList
	private static SortedSet saSet = new TreeSet();


	/** Weapon proficiency Data storage */
	private static final WeaponProfDataStore weaponProfs = new WeaponProfDataStore();
	/** this is used by the random selction tools */
	private static final Random random = new Random(System.currentTimeMillis());

	/**
	 * The following sets are for efficient filter creation:
	 * <ul>
	 * <li>pantheonsSet</li>
	 * <li>raceTypesSet</li>
	 * <li>subschoolsSet</li>
	 * <li>weaponTypes</li>
	 * </ul>
	 * The following sets are for efficient filter creation as
	 * well as quick loading of the spell editor:
	 * <ul>
	 * <li>castingTimesSet</li>
	 * <li>componentSet</li>
	 * <li>descriptorSet</li>
	 * <li>durationSet</li>
	 * <li>rangesSet</li>
	 * <li>saveInfoSet</li>
	 * <li>srSet</li>
	 * <li>targetSet</li>
	 * </ul>
	 */
	private static SortedSet pantheonsSet = new TreeSet();
	private static SortedSet raceTypesSet = new TreeSet();
	private static SortedSet subschoolsSet = new TreeSet();
	private static SortedSet weaponTypes = new TreeSet();
	//
	// Also for quick loading of spell editor
	//
	private static SortedSet castingTimesSet = new TreeSet();
	private static SortedSet componentSet = new TreeSet();
	private static SortedSet descriptorSet = new TreeSet();
	private static SortedSet durationSet = new TreeSet();
	private static SortedSet rangesSet = new TreeSet();
	private static SortedSet saveInfoSet = new TreeSet();
	private static SortedSet srSet = new TreeSet();
	private static SortedSet targetSet = new TreeSet();
	// end of filter creation sets

	private static javax.swing.JFrame rootFrame;

	private static final StringBuffer section15 = new StringBuffer(30000);
	private static final String spellPoints = "0";

	/**
	 * whether or not the GUI is used (false for command line)
	 */
	private static boolean useGUI = true;

	/** we need maps for efficient lookups */
	private static Map bonusSpellMap = new HashMap(); // key is level of bonus spell, value is "basestatscore|statrange"

	private static final Comparator pObjectComp = new Comparator()
	{
		public int compare(final Object o1, final Object o2)
		{
			return ((PObject) o1).getKeyName().compareToIgnoreCase(((PObject) o2).getKeyName());
		}
	};

	private static final Comparator pObjectNameComp = new Comparator()
	{
		public int compare(final Object o1, final Object o2)
		{
			return ((PObject) o1).getName().compareToIgnoreCase(((PObject) o2).getName());
		}
	};

	static
	{
		ResourceBundle globalProperties;
		try
		{
			globalProperties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			pcGenVersion = globalProperties.getString("VersionNumber");
		}
		catch (MissingResourceException mrex)
		{
			Logging.errorPrint("Can't find the VersionNumber property.", mrex);
		}
		finally
		{
			globalProperties = null;
		}
	}

	public static void setAttribLong(final String[] s)
	{
		s_ATTRIBLONG = s;
	}

	public static void setAttribLong(final int index, final String s)
	{
		s_ATTRIBLONG[index] = s;
	}

	public static void setAttribRoll(final boolean[] b)
	{
		s_ATTRIBROLL = b;
	}

	public static void setAttribRoll(final int index, final boolean b)
	{
		s_ATTRIBROLL[index] = b;
	}

	public static void setAttribShort(final String[] s)
	{
		s_ATTRIBSHORT = s;
	}

	public static void setAttribShort(final int index, final String s)
	{
		s_ATTRIBSHORT[index] = s;
	}

	public static void setAutoGeneration(final boolean auto)
	{
		setbAutoGeneration(auto);
	}

	public static Campaign getCampaignByFilename(final String aName)
	{
		return getCampaignByFilename(aName, true);
	}

	public static Campaign getCampaignByFilename(final String aName, final boolean complainOnError)
	{
		Campaign currCampaign;

		final Iterator campaignIterator = getCampaignList().iterator();
		while (campaignIterator.hasNext())
		{
			currCampaign = (Campaign) campaignIterator.next();
			String aString = currCampaign.getSourceFile();
			if (Utility.isURL(aString) && !Utility.isURL(aName))
			{
				aString = aString.substring(6);
				aString = Utility.replaceAll(aString, "/", "\\");
			}
			if (aString.equalsIgnoreCase(aName))
			{
				return currCampaign;
			}
		}

		if (complainOnError)
		{
			Logging.errorPrint("Could not find campaign by filename: " + aName);
		}

		return null;
	}

	public static List getCampaignList()
	{
		return campaignList;
	}

	public static Campaign getCampaignNamed(final String aName)
	{

		Campaign currCampaign;
		final Iterator e = getCampaignList().iterator();
		while (e.hasNext())
		{
			currCampaign = (Campaign) e.next();

			if (currCampaign.getName().equalsIgnoreCase(aName))
			{
				return currCampaign;
			}
		}

		Logging.errorPrint("Could not find campaign: " + aName);
		return null;
	}

	/**
	 * Store a list of all vision types (such as Darkvision)
	 **/
	public static void putVisionMap(String aKey)
	{
		visionMap.put(aKey, "0");
	}

	public static Map getVisionMap()
	{
		return visionMap;
	}

	public static PCClass getClassKeyed(final String aKey)
	{
		return (PCClass) searchPObjectList(getClassList(), aKey);
	}

	public static List getClassList()
	{
		return classList;
	}

	public static PCClass getClassNamed(final String aName)
	{
		return getClassNamed(aName, getClassList());
	}

	public static PCClass getClassNamed(final String aName, final List aList)
	{

		PCClass currClass;
		final Iterator e = aList.iterator();
		while (e.hasNext())
		{
			currClass = (PCClass) e.next();

			if (currClass.getName().equalsIgnoreCase(aName))
			{
				return currClass;
			}
		}

		return null;
	}

	/**
	 * Return an <b>unmodifiable</b> version of the saSet.
	 * @return
	 */
	public static SortedSet getSASet()
	{
		return saSet;
	}

	// Special Abilities List
	/**
	 * Add to the saList.
	 * @param sa
	 */
	public static void addToSASet(SpecialAbility sa)
	{
		saSet.add(sa);
	}

	/**
	 * Clear out the SA list.
	 */
	public static void clearSASet()
	{
		saSet.clear();
	}



	public static CompanionMod getCompanionMod(final String aString)
	{
		if (aString.length() <= 0)
		{
			return null;
		}

		StringTokenizer aTok = new StringTokenizer(aString.substring(9), "=", false);
		final String classes = aTok.nextToken();
		final int level = Integer.parseInt(aTok.nextToken());
		final Iterator e = getCompanionModList().iterator();
		while (e.hasNext())
		{

			final CompanionMod aComp = (CompanionMod) e.next();
			aTok = new StringTokenizer(classes, ",", false);

			while (aTok.hasMoreTokens())
			{

				final String cString = aTok.nextToken();

				if (aComp.getLevel(cString) == level)
				{
					return aComp;
				}
			}
		}

		return null;
	}

	public static List getCompanionModList()
	{
		return companionModList;
	}

	public static List getKitInfo()
	{
		return kitList;
	}

	public static Kit getKitKeyed(String aKey)
	{
		final Iterator e = kitList.iterator();
		while (e.hasNext())
		{
			final Kit aKit = (Kit) e.next();
			if (aKit.getKeyName().equals(aKey))
			{
				return aKit;
			}
		}
		return null;
	}

	static Kit getKitNamed(String aName)
	{
		final Iterator e = kitList.iterator();
		while (e.hasNext())
		{
			final Kit aKit = (Kit) e.next();
			if (aKit.getName().equals(aName))
			{
				return aKit;
			}
		}
		return null;
	}

	public static void setBioSet(final BioSet aBioSet)
	{
		bioSet = aBioSet;
	}

	public static BioSet getBioSet()
	{
		return bioSet;
	}

	public static void setCountry(final String aString)
	{
		country = aString;
	}

	public static String getCountry()
	{
		return country;
	}

	public static void setCurrentPC(final PlayerCharacter aCurrentPC)
	{
		currentPC = aCurrentPC;
	}

	public static PlayerCharacter getCurrentPC()
	{
		return currentPC;
	}

	public static void setCustColumnWidth(final String fromTab, final int col, final int value)
	{

		boolean found = false;
		final String cName = fromTab.concat(Integer.toString(col));
		final String addMe = cName.concat("|").concat(Integer.toString(value));

		if (getCustColumnWidth().isEmpty())
		{
			getCustColumnWidth().add(addMe);
		}

		final int loopMax = getCustColumnWidth().size();
		for (int i = 0; i < loopMax; ++i)
		{

			final StringTokenizer tTok = new StringTokenizer((String) getCustColumnWidth().get(i), "|", false);
			final String tabName = tTok.nextToken();

			if (cName.equals(tabName))
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

	public static int getCustColumnWidth(final String fromTab, final int col)
	{

		int colSize = 0;
		final String cName = fromTab.concat(Integer.toString(col));

		final int loopMax = getCustColumnWidth().size();
		for (int i = 0; i < loopMax; ++i)
		{

			final StringTokenizer tTok = new StringTokenizer((String) getCustColumnWidth().get(i), "|", false);
			final String tabName = tTok.nextToken();

			if (tabName.equals(cName))
			{
				colSize = Integer.parseInt(tTok.nextToken());
			}
		}

		return colSize;
	}

	static List getCustColumnWidth()
	{
		return custColumnWidth;
	}

	private static void setD_sorted(final boolean d_sorted)
	{
		Globals.d_sorted = d_sorted;
	}

	private static boolean isD_sorted()
	{
		return d_sorted;
	}

	// END Game Modes Section.

	public static String getDefaultPath() {
		return expandRelativePath(defaultPath);
	}

	static String getDefaultPcgPath() {
		return expandRelativePath(defaultPcgPath);
	}

	private static String expandRelativePath(String path) {
		if(path.startsWith("@")) {
			path = System.getProperty("user.dir") + File.separator + path.substring(1);
		}
		return path;
	}

	/**
	 * Get a writable path for storing files
	 * First check to see if it's been set in-program
	 * Then check user home directory
	 * Else use directory pcgen started from
	 **/
	private static String getFilePath(String aString)
	{
		final String fType = SettingsHandler.getFilePaths();
		if (fType == null || fType.equals("pcgen"))
		{
			// we are either running PCGen for the first
			// time or user wants default file locations
			return System.getProperty("user.dir") + File.separator + aString;
		}
		else if (fType.equals("user"))
		{
			// use the users "home" directory + .pcgen
			return System.getProperty("user.home") + File.separator + ".pcgen" + File.separator + aString;
		}
		else
		{
			// use the specified directory
			return fType + File.separator + aString;
		}
	}

	/**
	 * returns the location of the "options.ini" file
	 * which could be one of several locations
	 * depending on the OS and user preferences
	 **/
	static String getOptionsPath()
	{
		String aPath;
		// first see if it was specified on the command line
		aPath = System.getProperty("pcgen.options");
		if (aPath == null)
		{
			aPath = getFilePath("options.ini");
		}
		return expandRelativePath(aPath);
	}

	/**
	 * returns the location of the "filter.ini" file
	 * which could be one of several locations
	 * depending on the OS and user preferences
	 **/
	static String getFilterPath()
	{
		String aPath;
		// first see if it was specified on the command line
		aPath = System.getProperty("pcgen.filter");
		if (aPath == null)
		{
			aPath = getFilePath("filter.ini");
		}
		return expandRelativePath(aPath);
	}

	public static Deity getDeityKeyed(final String aKey)
	{
		return (Deity) searchPObjectList(getDeityList(), aKey);
	}

	public static List getDeityList()
	{
		return deityList;
	}

	public static Deity getDeityNamed(final String name)
	{
		return getDeityNamed(name, getDeityList());
	}

	public static Deity getDeityNamed(final String name, final List aList)
	{

		Deity currDeity;
		final Iterator e = aList.iterator();
		while (e.hasNext())
		{
			currDeity = (Deity) e.next();

			if (currDeity.getName().equalsIgnoreCase(name))
			{
				return currDeity;
			}
		}

		return null;
	}

	public static DenominationList getDenominationList()
	{
		return denominationList;
	}

	static int[] getDieSizes()
	{
		return dieSizes;
	}

	public static Domain getDomainKeyed(final String aKey)
	{
		return (Domain) domainMap.get(aKey);
	}

	public static Map getDomainMap()
	{
		return domainMap;
	}

	public static List getDomainList()
	{
		return domainList;
	}

	public static void addDomain(Domain nextDomain)
	{
		domainMap.put(nextDomain.getKeyName(), nextDomain);
		domainList.add(nextDomain);
	}

	public static Domain getDomainNamed(final String name)
	{
		return (Domain) domainMap.get(name);
	}

	public static Equipment getEquipmentFromName(final String baseName)
	{
		final List modList = new ArrayList();
		final List namList = new ArrayList();
		final List sizList = new ArrayList();
		Equipment eq;
		String aName = baseName;
		int i = aName.indexOf('(');

		// Remove all modifiers from item name and
		// split into "size" and "non-size" lists
		if (i >= 0)
		{

			final StringTokenizer aTok = new StringTokenizer(aName.substring(i + 1), "/)", false);

			while (aTok.hasMoreTokens())
			{

				final String cString = aTok.nextToken();
				int iSize;

				for (iSize = 0; iSize <= SystemCollections.getSizeAdjustmentListSize() - 1; ++iSize)
				{
					if (cString.equalsIgnoreCase(SystemCollections.getSizeAdjustmentAtIndex(iSize).getAbbreviation()))
					{
						break;
					}
				}

				if (iSize <= SystemCollections.getSizeAdjustmentListSize() - 1)
				{
					sizList.add(cString);
				}
				else
				{
					if ("Mighty Composite".equalsIgnoreCase(cString))
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

		// Separate the "non-size" descriptors into 2 Lists.
		// One containing those descriptors whose names match a
		// modifier name, and the other containing those descriptors
		// which are not possibly modifiers
		// (because they're not in the modifier list).
		//
		if (i >= 0)
		{
			for (i = modList.size() - 1; i >= 0; --i)
			{

				final String namePart = (String) modList.get(i);

				if (getModifierNamed(namePart) == null)
				{
					namList.add(0, namePart); // add to the start as otherwise the list will be reversed
					modList.remove(i);
				}
			}
		}

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
		// Mighty bows suffered a (much-needed) renaming
		// (Long|Short)bow +n (Mighty/Composite) --> (Long|Short)bow (+n Mighty/Composite)
		// (Long|Short)bow +x/+n (Mighty/Composite) --> (Long|Short)bow +x (+n Mighty/Composite)
		//
		// Look through the modifier list for MIGHTY,
		// if found add the bonus to the start of the modifier's name
		//
		if (bonusCount > 0)
		{
			for (int idx1 = 0; idx1 < namList.size(); ++idx1)
			{

				String aString = (String) namList.get(idx1);

				if ("Mighty".equalsIgnoreCase(aString))
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

			// If only 1 size then include it in name
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

			// If we haven't found it yet,
			// try stripping Thrown from name
			if (baseName.indexOf("Thrown") >= 0)
			{
				if (omitString.length() == 0)
				{
					omitString = "Thrown";
					continue;
				}
			}

			// Still haven't found it?
			// Try adding bonus to end of name
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
			eq = (Equipment) eq.clone();

			//
			// Now attempt to add all the modifiers.
			//
			for (Iterator e = modList.iterator(); e.hasNext();)
			{

				final String namePart = (String) e.next();
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
					Logging.errorPrint("Could not find a qualified modifier named: " + namePart + " for " + eq.getName() + ":" + eq.typeList());
					bError = true;
				}
			}

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
				eq.resizeItem((String) sizList.get(0));
				bModified = true;

				if (sizList.size() > 1)
				{
					Logging.errorPrint("Too many sizes in item name, used only 1st of: " + sizList);
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

	public static Equipment getEquipmentKeyed(final String aKey)
	{
		return (Equipment) searchPObjectList(getEquipmentList(), aKey);
	}

	public static List getEquipmentList()
	{
		return equipmentList;
	}

	public static Equipment getEquipmentNamed(final String name)
	{
		return getEquipmentNamed(name, getEquipmentList());
	}

	public static Equipment getEquipmentNamed(final String name, final List aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{

			final Equipment eq = (Equipment) e.next();

			if (eq.getName().equalsIgnoreCase(name))
			{
				return eq;
			}
		}

		return null;
	}

	public static List getEquipmentOfType(final List eqList, final String desiredTypes, final String excludedTypes)
	{

		final List desiredTypeList = Utility.split(desiredTypes, '.');
		final List excludedTypeList = Utility.split(excludedTypes, '.');
		final List typeList = new ArrayList(100);

		if (desiredTypeList.size() != 0)
		{
			for (Iterator e = eqList.iterator(); e.hasNext();)
			{

				final Equipment eq = (Equipment) e.next();
				boolean addIt = true;

				//
				// Must have all of the types in the desired list
				//
				for (Iterator e2 = desiredTypeList.iterator(); e2.hasNext();)
				{
					if (!eq.isType((String) e2.next()))
					{
						addIt = false;
						break;
					}
				}

				if (addIt && (excludedTypeList.size() != 0))
				{

					//
					// Can't have any of the types on the excluded list
					//
					for (Iterator e3 = excludedTypeList.iterator(); e3.hasNext();)
					{
						if (eq.isType((String) e3.next()))
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
		}

		return typeList;
	}

	public static Feat getFeatKeyed(final String aKey)
	{
		return (Feat) searchPObjectList(getFeatList(), aKey);
	}

	/**
	 * Gets the list of feats
	 */
	public static List getFeatList()
	{
		return featList;
	}

	/**
	 * Convenience method that returns a feat from the featList.
	 */
	public static Feat getFeatListFeat(final int featNo)
	{
		return (Feat) getFeatList().get(featNo);
	}

	/**
	 * Convenience method that returns the toString of a feat.
	 */
	static String getFeatListFeatString(final int featNo)
	{
		return getFeatList().get(featNo).toString();
	}

	public static Feat getFeatNamed(String name)
	{

		final int i = name.indexOf('(');

		if (i >= 0)
		{

			final Feat f = getFeatNamed(name.substring(0, i).trim());

			if (f != null)
			{
				return f;
			}
		}

		for (Iterator e = getFeatList().iterator(); e.hasNext();)
		{

			final Feat f = (Feat) e.next();

			if (f.getName().equalsIgnoreCase(name))
			{
				return f;
			}
		}

		//
		// Feat not found.
		//
		return null;
	}

	public static void setLanguage(final String aString)
	{
		language = aString;
	}

	public static String getLanguage()
	{
		return language;
	}

	public static List getLanguageList()
	{
		return languageList;
	}

	public static Language getLanguageNamed(final String name)
	{
		for (Iterator i = getLanguageList().iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();
			if (aLang.getName().equalsIgnoreCase(name))
			{
				return aLang;
			}
		}
		return null;
	}

	private static List getLanguageSetNames()
	{
		final List aList = new ArrayList();

		for (Iterator i = getLanguageList().iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();
			aList.add(aLang.getName());
		}

		return aList;
	}

	static List getLanguagesFromListOfType(final List langList, final String aType)
	{

		final List retSet = new ArrayList();

		for (Iterator i = langList.iterator(); i.hasNext();)
		{

			final Language aLang = (Language) i.next();
			if (aLang != null && (aLang.isType(aType) || (aType.length() > 0 && aType.charAt(0) == '!' && !aLang.isType(aType))))
			{
				retSet.add(aLang);
			}
		}

		return retSet;
	}

	public static EquipmentModifier getModifierKeyed(final String aKey)
	{
		return (EquipmentModifier) searchPObjectList(getModifierList(), aKey);
	}

	public static List getModifierList()
	{
		return modifierList;
	}

	private static EquipmentModifier getModifierNamed(final String aName)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();

			if (aEqMod.getName().equals(aName))
			{
				return aEqMod;
			}
		}

		return null;
	}

	public static void setPCList(final List pcList)
	{
		Globals.pcList = pcList;
	}

	public static List getPCList()
	{
		return pcList;
	}

	public static List getPCClassTypeList()
	{
		return pcClassTypeList;
	}

	public static SortedSet getPantheons()
	{
		return getPantheonsSet();
	}

	private static SortedSet getPantheonsSet()
	{
		return pantheonsSet;
	}

	public static int getPaperCount()
	{
		return SystemCollections.getUnmodifiablePaperInfo().size();
	}

	public static String getPaperInfo(final int infoType)
	{
		return getPaperInfo(getSelectedPaper(), infoType);
	}

	public static String getPaperInfo(final int idx, final int infoType)
	{
		if ((idx < 0) || (idx >= SystemCollections.getUnmodifiablePaperInfo().size()))
		{
			return null;
		}

		final PaperInfo pi = (PaperInfo) SystemCollections.getUnmodifiablePaperInfo().get(idx);
		return pi.getPaperInfo(infoType);
	}

	static EquipmentModifier getQualifiedModifierNamed(final String aName, final List aType)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();

			if (aEqMod.getName().equals(aName))
			{
				if (aEqMod.isType("All"))
				{
					return aEqMod;
				}

				for (Iterator e2 = aType.iterator(); e2.hasNext();)
				{
					final String t = (String) e2.next();

					if (aEqMod.isType(t))
					{
						return aEqMod;
					}
				}
			}
		}

		return null;
	}

	private static EquipmentModifier getQualifiedModifierNamed(final String aName, final Equipment eq)
	{
		for (Iterator e = getModifierList().iterator(); e.hasNext();)
		{

			final EquipmentModifier aEqMod = (EquipmentModifier) e.next();
			if (aEqMod.getName().equals(aName))
			{
				for (Iterator e2 = eq.typeList().iterator(); e2.hasNext();)
				{
					final String t = (String) e2.next();

					if (aEqMod.isType(t))
					{
						//
						// Type matches, passes prereqs?
						//
						if (aEqMod.passesPreReqToGain(eq))
						{
							return aEqMod;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Get's Race from raceMap() based on aKey
	 **/
	public static Race getRaceKeyed(final String aKey)
	{
		return (Race) getRaceMap().get(aKey);
	}

	/**
	 * This method gets the race map
	 **/
	public static Map getRaceMap()
	{
		return raceMap;
	}

	/**
	 * Get's Race from raceMap() based on aName
	 **/
	public static Race getRaceNamed(final String aName)
	{
		return (Race) getRaceMap().get(aName);
	}

	/**
	 * This method gets the available race types as a set.
	 */
	public static SortedSet getRaceTypes()
	{
		return raceTypesSet;
	}

	private static Random getRandom()
	{
		return random;
	}

	/**
	 * Get a random integer between 0 (inclusive) and the given value (exclusive)
	 */
	static int getRandomInt(final int high)
	{
		return getRandom().nextInt(high);
	}

	public static int getRandomInt()
	{
		return getRandom().nextInt();
	}

	/**
	 * Sets the root frame
	 * The root frame is the container in which all
	 * other panels, frame etc are placed.
	 *
	 * @param  frame  the <code>PCGen_Frame1</code> which is to be root
	 */
	public static void setRootFrame(javax.swing.JFrame frame)
	{
		rootFrame = frame;
	}

	/**
	 * Returns the current root frame.
	 *
	 * @return    the <code>rootFrame</code> property
	 */
	public static javax.swing.JFrame getRootFrame()
	{
		return rootFrame;
	}

	private static void setSelectedPaper(final int selectedPaper)
	{
		Globals.selectedPaper = selectedPaper;
	}

	public static int getSelectedPaper()
	{
		return selectedPaper;
	}

	public static Map getEquipSlotMap()
	{
		return eqSlotMap;
	}

	public static void setEquipSlotTypeCount(String aString, String aNum)
	{
		getEquipSlotMap().put(aString, aNum);
	}

	/**
	 * returns the # of slots for an equipmentslots Type
	 * The number of slots is define by the NUMSLOTS: line
	 * in system/special/equipmentslots.lst file
	 **/
	public static int getEquipSlotTypeCount(final String aType)
	{
		final String aNum = (String) getEquipSlotMap().get(aType);
		if (aNum != null)
		{
			return Integer.parseInt(aNum);
		}
		else
		{
			return 0;
		}
	}

	public static EquipSlot getEquipSlotByName(final String aName)
	{
		for (Iterator eI = SystemCollections.getUnmodifiableEquipSlotList().iterator(); eI.hasNext();)
		{
			final EquipSlot es = (EquipSlot) eI.next();
			if (es.getSlotName().equals(aName))
			{
				return es;
			}
		}
		return null;
	}

	public static Skill getSkillKeyed(final String aKey)
	{
		return (Skill) searchPObjectList(getSkillList(), aKey);
	}

	public static List getSkillList()
	{
		return skillList;
	}

	public static Skill getSkillNamed(final String name)
	{

		Skill currSkill;

		for (Iterator skillIter = getSkillList().iterator(); skillIter.hasNext();)
		{
			currSkill = (Skill) skillIter.next();

			if (currSkill.getName().equalsIgnoreCase(name))
			{
				return currSkill;
			}
		}

		return null;
	}

	public static void setSorted(final boolean sorted)
	{
		setD_sorted(sorted);
	}

	public static void setSourceDisplay(final int sourceType)
	{
		sourceDisplay = sourceType;
	}

	public static int getSourceDisplay()
	{
		return sourceDisplay;
	}

	public static Spell getSpellKeyed(final String aKey)
	{
		final Object obj = getSpellMap().get(aKey);
		if (obj != null)
		{
			if (obj instanceof Spell)
			{
				return (Spell) getSpellMap().get(aKey);
			}
			if (obj instanceof ArrayList)
			{
				return (Spell) ((ArrayList) obj).get(0);
			}
		}
		return null;
	}

//	private static void setSpellMap(final Map spellMap)
//	{
//		Globals.spellMap = spellMap;
//	}

	public static Map getSpellMap()
	{
		return spellMap;
	}

	public static Spell getSpellNamed(final String name)
	{
		return getSpellKeyed(name);
	}

	public static String getSpellPoints()
	{
		return spellPoints;
	}

	/**
	 * Returns the index of the requested attribute abbreviation,
	 * The attributes used are loaded from a lst file
	 *
	 * @param attributeAbbreviation to find the index of
	 * @return the index of the attribute
	 *         returns -1 if the attribute is not matched (or null)
	 */
	public static int getStatFromAbbrev(final String attributeAbbreviation)
	{
		if (s_ATTRIBSHORT != null)
		{
			for (int stat = 0; stat < s_ATTRIBSHORT.length; ++stat)
			{
				if (attributeAbbreviation.equalsIgnoreCase(s_ATTRIBSHORT[stat]))
				{
					return stat;
				}
			}
		}

		return -1;
	}

	public static SortedSet getSubschools()
	{
		return getSubschoolsSet();
	}

	private static SortedSet getSubschoolsSet()
	{
		return subschoolsSet;
	}

	public static PCTemplate getTemplateKeyed(final String aKey)
	{
		return (PCTemplate) searchPObjectList(getTemplateList(), aKey);
	}

	public static List getTemplateList()
	{
		return templateList;
	}

	public static PCTemplate getTemplateNamed(final String name)
	{

		PCTemplate currTemp;

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			currTemp = (PCTemplate) e.next();

			if (currTemp.getName().equalsIgnoreCase(name))
			{
				return currTemp;
			}
		}

		return null;
	}

	public static void setUseGUI(final boolean aBool)
	{
		useGUI = aBool;
	}

	public static boolean getUseGUI()
	{
		return isUseGUI();
	}

	private static boolean isUseGUI()
	{
		return useGUI;
	}

	/**
	 * @return true if the weapon is light for the specified pc
	 */
	public static boolean isWeaponLightForPC(final PlayerCharacter pc, final Equipment weapon)
	{
		return ((pc != null) && (weapon != null) && (pc.sizeInt() > Globals.sizeInt(weapon.getSize())));
	}

	/**
	 * @return true if the weapon is one-handed for the specified pc
	 */
	public static boolean isWeaponOneHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp)
	{
		return isWeaponOneHanded(pc, weapon, wp, false);
	}

	public static int handsRequired(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp)
	{
		int iHands = wp.getHands();
		if (iHands == WeaponProf.HANDS_SIZEDEPENDENT)
		{
			if (pc.sizeInt() > Globals.sizeInt(weapon.getSize()))
			{
				iHands = 1;
			}
			else
			{
				iHands = 2;
			}
		}
		return iHands;
	}

	public static boolean isWeaponOneHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp, final boolean baseOnly)
	{
		if ((pc != null) && (weapon != null) && (wp != null) && (handsRequired(pc, weapon, wp) == 1))
		{

			int pcSize = pc.sizeInt();

			if (!baseOnly)
			{
				pcSize += pc.getTotalBonusTo("WEAPONPROF=" + wp.getName(), "PCSIZE");
			}

			if (pcSize >= Globals.sizeInt(weapon.getSize()))
			{
				return true;
			}
		}

		return false;
	}

	private static void clearWeaponProfs()
	{
		weaponProfs.clear();
	}

	/**
	 * Searches for an exact key match.
	 * @param aKey
	 * @return
	 */
	public static WeaponProf getWeaponProfKeyed(final String aKey)
	{
		return weaponProfs.getKeyed(aKey);
	}

	public static void addWeaponProf(WeaponProf wp)
	{
		weaponProfs.add(wp);
	}

	public static void removeWeaponProfNamed(String name)
	{
		weaponProfs.removeNamed(name);
	}

	public static List getWeaponProfArrayCopy()
	{
		return weaponProfs.getArrayCopy();
	}

	public static int getWeaponProfSize()
	{
		return weaponProfs.size();
	}

	public static String getWeaponProfNames(String delim, boolean addArrayMarkers)
	{
		return weaponProfs.getNames(delim, addArrayMarkers);
	}

	public static void addUniqueWeaponProfsAsStringTo(List dest)
	{
		weaponProfs.addUniqueAsStringTo(dest);
	}

	public static Collection getAllWeaponProfsOfType(final String type)
	{
		return weaponProfs.getAllOfType(type);
	}

	/**
	 * Searches for an exact name match.
	 * @param name
	 * @return
	 */
	public static WeaponProf getWeaponProfNamed(final String name)
	{
		return weaponProfs.getNamed(name);
	}

	static List getWeaponProfs(final String type)
	{
		final List aList = new ArrayList();
		final List bList = new ArrayList();
		String aString;
		StringTokenizer aTok;
		String typeString;
		String wpString;
		WeaponProf tempProf;

		for (Iterator e = getCurrentPC().getRace().getWeaponProfs().iterator(); e.hasNext();)
		{
			aString = (String) e.next();
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
				aList.add(tempProf);
			}
			else
			{
				bList.add(tempProf);
			}
		}

		WeaponProf tempProf2;
		final Collection weaponProfsOfType = getAllWeaponProfsOfType(type);

		if (weaponProfsOfType == null)
		{
			return aList;
		}
		for (Iterator e = weaponProfsOfType.iterator(); e.hasNext();)
		{
			tempProf2 = (WeaponProf) e.next();

			if (bList.contains(tempProf2))
			{
				continue;
			}
			aList.add(tempProf2);
		}

		return aList;
	}

	public static boolean hasWeaponProfVariableNamed(Collection collectionOfNames, String variableString)
	{
		return weaponProfs.hasVariableNamed(collectionOfNames, variableString);
	}

	/**
	 * @return true if the weapon is too large for the specified pc.
	 */
	public static boolean isWeaponTooLargeForPC(final PlayerCharacter pc, final Equipment weapon)
	{
		return ((pc != null) && (weapon != null) && (pc.sizeInt() < (Globals.sizeInt(weapon.getSize()) - 1)));
	}

	/**
	 * @return true if the weapon is two-handed for the specified pc
	 */
	public static boolean isWeaponTwoHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp)
	{
		return isWeaponTwoHanded(pc, weapon, wp, false);
	}

	private static boolean isWeaponTwoHanded(final PlayerCharacter pc, final Equipment weapon, final WeaponProf wp, final boolean baseOnly)
	{
		if ((pc != null) && (weapon != null) && (wp != null))
		{

			int pcSize = pc.sizeInt();

			if (!baseOnly)
			{
				pcSize += pc.getTotalBonusTo("WEAPONPROF=" + wp.getName(), "PCSIZE");
			}

			if ((pcSize == (Globals.sizeInt(weapon.getSize()) - 1)) || (handsRequired(pc, weapon, wp) == 2))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns an Iterator over the weapontypes
	 * @return The iterator of weapon types
	 */
	public static Iterator getWeaponTypesIterator()
	{
		return weaponTypes.iterator();
	}

	/**
	 * Adds a weapon type
	 * @param weaponType The weapon type to add
	 */
	static void addWeaponType(final String weaponType)
	{
		weaponTypes.add(weaponType);
	}

	/**
	 * Checks if the weapon types include a certain weapon type
	 * @param weaponType The weapon type to look for
	 * @return True if the weapon type exists among the weapon types, otherwise false
	 */
	static boolean weaponTypesContains(final String weaponType)
	{
		return weaponTypes.contains(weaponType.toUpperCase());
	}

	/**
	 * Gets the list of armor profs
	 * @return The list of armor profs
	 */
	private static List getArmorProfList()
	{
		return armorProfList;
	}

	/**
	 * Left alone for a while, will remove in a month unless it is used.
	 * Gets an iterator over the armor proficiencies
	 * @return The iterator over armor proficiencies
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 */
	private static Iterator getArmorProfListIterator()
	{
		return armorProfList.iterator();
	}

	/**
	 * Left alone for a while, will remove in a month unless it is used.
	 * Returns the number of armor proficiencies
	 * @return The number of armor proficiencies
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 */
	public static int getArmorProfListSize()
	{
		return armorProfList.size();
	}


	/**
	 * Returns a List of Spell with following criteria:
	 * @param level         (optional, ignored if < 0),
	 * @param className     (optional, ignored if "")
	 * @param domainName    (optional, ignored if "")
	 * at least one of className and domainName must not be ""
	 * @return a List of Spell
	 */
	public static List getSpellsIn(final int level, final String className, final String domainName)
	{
		final List aList = new ArrayList();
		final StringBuffer aBuf = new StringBuffer();
		String spellType = "";
		if (className.length() > 0)
		{
			final PCClass aClass;
			if (className.indexOf('|') < 0)
			{
				aClass = getClassNamed(className);
				aBuf.append("CLASS|").append(className);
			}
			else
			{
				aClass = getClassNamed(className.substring(className.indexOf("|") + 1));
				aBuf.append(className);
			}
			if (aClass != null)
			{
				spellType = aClass.getSpellType();
			}
		}
		if (domainName.length() > 0)
		{
			if (aBuf.length() > 0)
			{
				aBuf.append('|');
			}
			if (domainName.indexOf('|') < 0)
			{
				aBuf.append("DOMAIN|").append(domainName);
			}
			else
			{
				aBuf.append(domainName);
			}
			spellType = "DIVINE";
		}

		for (Iterator i = spellMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = (String) i.next();
			final Object obj = spellMap.get(aKey);
			if (obj instanceof ArrayList)
			{
				for (Iterator j = ((ArrayList) obj).iterator(); j.hasNext();)
				{
					final Spell aSpell = (Spell) j.next();
					if (aSpell.levelForKeyContains(aBuf.toString(), level) && aSpell.getType().indexOf(spellType.toUpperCase()) >= 0)
					{
						aList.add(aSpell);
					}
				}
			}
			else if (obj instanceof Spell)
			{
				final Spell aSpell = (Spell) obj;
				if (aSpell.levelForKeyContains(aBuf.toString(), level))
				{
					aList.add(aSpell);
				}
			}
		}

		return aList;
	}

	public static boolean addEquipment(final Equipment aEq)
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
		Equipment.getEquipmentTypes().addAll(aEq.typeList());
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
	static String adjustDamage(final String aDamage, final String sBaseSize, final String sNewSize)
	{
		if (aDamage.length() == 0)
		{
			return aDamage;
		}

		return adjustDamage(aDamage, sizeInt(sBaseSize), sizeInt(sNewSize));
	}

	public static String adjustDamage(final String aDamage, final int baseSize, int itemSize)
	{
		final RollInfo aRollInfo = new RollInfo(aDamage);

		if ((itemSize < 0) || (itemSize == baseSize))
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
					Logging.errorPrint("Size increase, unknown die size: " + Integer.toString(aRollInfo.sides) + ":" + aDamage + ":" + Integer.toString(baseSize) + ":" + Integer.toString(itemSize));
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
					Logging.errorPrint("Size decrease, unknown die size: " + Integer.toString(aRollInfo.sides) + ":" + aDamage + ":" + Integer.toString(baseSize) + ":" + Integer.toString(itemSize));
					return aDamage;
			}

			itemSize += 1;
		}

		return Integer.toString(aRollInfo.times) + "d" + Integer.toString(aRollInfo.sides);
	}

	/**
	 * Automatically add equipment types as requested by user.
	 */
	public static void autoGenerateEquipment()
	{
		setbAutoGeneration(true);

		autogenerateRacialEquipment();

		autogenerateMasterWorkEquipment();

		autogenerateMagicEquipment();

		autogenerateExoticMaterialsEquipment();

		setbAutoGeneration(false);

		sortPObjectList(getEquipmentList()); // Sort the equipment list
	}

	private static void autogenerateExoticMaterialsEquipment()
	{
		if (SettingsHandler.isAutogenExoticMaterial())
		{
			for (int i = getEquipmentList().size() - 1; i >= 0; --i)
			{

				final Equipment eq = (Equipment) getEquipmentList().get(i);

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
	}

	private static void autogenerateMagicEquipment()
	{
		if (SettingsHandler.isAutogenMagic())
		{
			for (int iPlus = 1; iPlus <= 5; iPlus++)
			{

				final String aBonus = Delta.toString(iPlus);

				for (int i = getEquipmentList().size() - 1; i >= 0; --i)
				{
					Equipment eq = (Equipment) getEquipmentList().get(i);
					//
					// Only apply to non-magical
					// Armor, Shield and Weapon
					//
					if (eq.isMagic() || eq.isMasterwork() || (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
					{
						continue;
					}
					// Items must be masterwork before
					// you can assign magic to them
					EquipmentModifier eqMod = getQualifiedModifierNamed("Masterwork", eq);
					if (eqMod == null)
					{
						continue;
					}

					eq = (Equipment) eq.clone();
					eq.addEqModifier(eqMod, true);
					if (eq.isWeapon() && eq.isDouble())
					{
						eq.addEqModifier(eqMod, false);
					}

					eqMod = getQualifiedModifierNamed(aBonus, eq);
					createItem(eq, eqMod);
				}
			}
		}
	}

	private static void autogenerateMasterWorkEquipment()
	{
		if (SettingsHandler.isAutogenMasterwork())
		{
			for (int i = getEquipmentList().size() - 1; i >= 0; --i)
			{

				final Equipment eq = (Equipment) getEquipmentList().get(i);

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
	}

	private static void autogenerateRacialEquipment()
	{
		if (SettingsHandler.isAutogenRacial())
		{

			//
			// Go through all loaded races and flag whether or not to make equipment sized for them
			//
			final int[] gensizes = new int[9];
			final List races = new ArrayList(getRaceMap().values());

			for (Iterator e = races.iterator(); e.hasNext();)
			{

				final Race race = (Race) e.next();
				final int iSize = sizeInt(race.getSize());
				final int flag = 1;

				gensizes[iSize] |= flag;
			}
			int x = -1;

			for (int i = getEquipmentList().size() - 1; i >= 0; --i)
			{

				final Equipment eq = (Equipment) getEquipmentList().get(i);

				//
				// Only apply to Armor, Shield and resizable items
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork() || (!eq.isArmor() && !eq.isShield() && !eq.isType("RESIZABLE")))
				{
					continue;
				}

				for (int j = 0; j <= SystemCollections.getSizeAdjustmentListSize() - 1; ++j)
				{
					if (x == -1)
					{
						final SizeAdjustment s = SystemCollections.getSizeAdjustmentAtIndex(j);
						if (s.isDefaultSize())
						{
							x = j;
						}
					}
					if (j == x) // skip over default size
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
	}

	static PObject binarySearchPObject(final List aList, final String keyName)
	{

		final Object[] pobjArray = aList.toArray();
		int lower = 0;
		int upper = pobjArray.length;

		// always one past last possible match
		while (lower < upper)
		{

			final int mid = (lower + upper) / 2;
			final PObject obj = (PObject) pobjArray[mid];
			final int cmp = keyName.compareToIgnoreCase(obj.getKeyName());

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
	 * Works for dnd according to the method noted in the faq.
	 * (NOTE: The table in the dnd faq is wrong for speeds 80 and 90)
	 * Not as sure it works for all other d20 games.
	 * @param load (0 = light, 1 = medium, 2 = heavy, 3 = overload)
	 * @param  unencumberedMove the unencumbered move value
	 * @return encumbered move as an integer
	 */
	static int calcEncumberedMove(final int load, final int unencumberedMove, final boolean checkLoad)
	{
		final int encumberedMove;

		if (checkLoad)
		{
			switch (load)
			{
				case Constants.LIGHT_LOAD:
					encumberedMove = unencumberedMove;
					break;

				case Constants.MEDIUM_LOAD:
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
						encumberedMove = (unencumberedMove / 15) * 10 + unencumberedMove % 15;
					}
					break;

				case Constants.OVER_LOAD:
					encumberedMove = 0;
					break;

				default:
					Logging.errorPrint("The load " + load + " is not possible.");
					encumberedMove = 0;
					break;
			}
		}
		else
		{
			encumberedMove = unencumberedMove;
		}

		return encumberedMove;
	}

	// Methods
	static String chooseFromList(final String title, final String choiceList, final List selectedList, final int pool)
	{

		final StringTokenizer tokens = new StringTokenizer(choiceList, "|");

		if (tokens.countTokens() != 0)
		{

			final List choices = new ArrayList();

			while (tokens.hasMoreTokens())
			{
				choices.add(tokens.nextToken());
			}

			return chooseFromList(title, choices, selectedList, pool);
		}

		return null;
	}

	public static String chooseFromList(final String title, final List choiceList, final List selectedList, final int pool)
	{
		final List justSelectedList = getChoiceFromList(title, choiceList, selectedList, pool);
		if (justSelectedList.size() != 0)
		{
			return (String) justSelectedList.get(0);
		}

		return null;
	}

	static List getChoiceFromList(final String title, final List choiceList, final List selectedList, final int pool)
	{
		final pcgen.gui.utils.ChooserInterface c = pcgen.gui.utils.ChooserFactory.getChooserInstance();
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

		return c.getSelectedList();
	}

	public static boolean displayListsHappy()
	{
		Logging.debugPrint("Number of objects loaded. The following should all be greater than 0:");
		Logging.debugPrint("Races=" + getRaceMap().size());
		Logging.debugPrint("Classes=" + getClassList().size());
		Logging.debugPrint("Skills=" + getSkillList().size());
		Logging.debugPrint("Feats=" + getFeatList().size());
		Logging.debugPrint("Equipment=" + getEquipmentList().size());
		Logging.debugPrint("WeaponProfs=" + getWeaponProfSize());
		//
		// NOTE: If you add something here be sure to update the debug output in pcgen.gui.MainSource in loadCampaigns_actionPerformed
		//
		if ((getRaceMap().size() == 0) || (getClassList().size() == 0) || (getSkillList().size() == 0)
			|| (getFeatList().size() == 0) || (getEquipmentList().size() == 0) || (getWeaponProfSize() == 0)
		/*|| (getArmorProfList().size() == 0)*/)
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
		// These lists do not need cleared; they are tied to game mode
		// alignmentList
		// checkList
		// gameModeList
		// campaignList
		// statList
		// All other lists should be cleared!!!

		//////////////////////////////////////
		// DO NOT CLEAR THESE HERE!!!
		// They only get loaded once.
		//
		//birthplaceList.clear();
		//bonusStackList.clear();
		//cityList.clear();
		//hairStyleList.clear();
		//helpContextFileList.clear();
		//interestsList.clear();
		//locationList.clear();
		//paperInfo.clear();
		//phobiaList.clear();
		//phraseList.clear();
		//schoolsList.clear();
		//sizeAdjustmentList.clear();
		//specialsList.clear();
		//speechList.clear();
		//traitList.clear();
		//////////////////////////////////////

		armorProfList = new ArrayList();
		classList = new ArrayList();
		companionModList = new ArrayList();
		deityList = new ArrayList();
		domainList = new ArrayList();
		equipmentList = new ArrayList();
		featList = new ArrayList();
		kitList = new ArrayList();
		languageList = new ArrayList();
		modifierList = new ArrayList();
		pcClassTypeList = new ArrayList();
		skillList = new ArrayList();
		templateList = new ArrayList();
		saSet = new TreeSet();

		clearWeaponProfs();

		// Clear Maps (not strictly necessary, but done for consistency)
		bonusSpellMap = new HashMap();
		domainMap = new HashMap();
		raceMap = new TreeMap();
		spellMap = new HashMap();
		visionMap = new HashMap();

		// Clear Sets (not strictly necessary, but done for consistency)
		clearSpellSets();
		pantheonsSet = new TreeSet();
		raceTypesSet = new TreeSet();
		subschoolsSet = new TreeSet();
		weaponTypes = new TreeSet();

		// Perform other special cleanup
		createEmptyRace();
		Equipment.clearEquipmentTypes();
		PersistenceManager.emptyLists();
	}

	/**
	 * Takes a SortedSet of language names and extracts the cases
	 * of ALL and TYPE=x and
	 * returns a larger SortedSet of Strings (language names)
	 */
	static SortedSet extractLanguageListNames(final SortedSet langNames)
	{
		final SortedSet newSet = new TreeSet();

		for (Iterator bI = langNames.iterator(); bI.hasNext();)
		{
			final String aLang = (String) bI.next();

			if ("ALL".equals(aLang))
			{
				newSet.addAll(getLanguageSetNames());
			}
			else if (aLang.startsWith("TYPE=") || aLang.startsWith("TYPE."))
			{
				newSet.addAll(getLanguageNamesFromListOfType(getLanguageList(), aLang.substring(5)));
			}
			else
			{
				newSet.add(aLang);
			}
		}
		return newSet;
	}

	static void initCustColumnWidth(final List l)
	{
		getCustColumnWidth().clear();
		getCustColumnWidth().addAll(l);
	}

	private static boolean isbAutoGeneration()
	{
		return bAutoGeneration;
	}

	public static void loadAttributeNames() throws PersistenceLayerException
	{
		if( (s_ATTRIBLONG==null) || (s_ATTRIBSHORT==null) )
		{
			final String modeFilePrefix = SettingsHandler.getPcgenSystemDir() + File.separator + "gameModes" + File.separator + SettingsHandler.getGame().getName() + File.separator;
			// We don't store the array, but the manager uses it...
			PersistenceManager.loadFileIntoList(modeFilePrefix + "statsandchecks.lst", LstConstants.STATNAME_TYPE, new ArrayList(6));
		}

		createEmptyRace();
	}

	/**
	 * @return 0 = light, 1 = medium, 2 = heavy, 3 = overload
	 */
	public static int loadTypeForLoadScore(int loadScoreValue, final Float weight)
	{
		if (loadScoreValue < 0)
		{
			loadScoreValue = 0;
		}
		final double dbl = weight.doubleValue() / maxLoadForLoadScore(loadScoreValue).doubleValue();

		if (dbl <= ((double) 1) / ((double) 3))
		{
			return Constants.LIGHT_LOAD;
		}

		if (dbl <= ((double) 2) / ((double) 3))
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
	 **/
	public static Float maxLoadForLoadScore(final int loadScoreValue)
	{
		double x = 0.0;
		double dbl = 0.0;
		int y = loadScoreValue;
		final int loadStringSize = getLoadStrings().size();

		if (loadScoreValue >= loadStringSize - 2) //ok
		{

			final String bString = (String) getLoadStrings().get(loadStringSize - 1); //ok
			dbl = Double.parseDouble(bString.substring(bString.lastIndexOf('\t') + 1));

			//What does this do???
			for (y = loadScoreValue; y >= loadStringSize - 2; y -= 10)
			{
				x += 1.0;
			}
		}

		final String aString = (String) getLoadStrings().get(y + 1);
		final int beginIndex = aString.lastIndexOf('\t') + 1;
		Float aFloat = new Float(aString.substring(beginIndex));

		if (x > 0)
		{
			aFloat = new Float(aFloat.doubleValue() * Math.pow(dbl, x));
		}

		return new Float(aFloat.doubleValue() * getLoadMultForSize(getCurrentPC()));
	}

	public static int minLevelForSpellLevel(final PCClass castingClass, final int spellLevel, final boolean allowBonus)
	{

		int minLevel = Constants.INVALID_LEVEL;
		final List castList = castingClass.getCastList();

		final int loopMax = castList.size();
		for (int i = 0; i < loopMax; ++i)
		{

			final String castPerDay = castList.get(i).toString();

			if ("0".equals(castPerDay))
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
						//TODO: Should this really be ignored?
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
	 *  This method will find PObject by key name in a sorted list of PObjects.
	 *  The list must be sorted by key name.
	 *
	 * @param  aList    a list of PObject objects.
	 * @param  keyName  the keyname being sought.
	 * @return          a <code>null</code> value indicates the search failed.
	 */
	private static PObject searchPObjectList(final List aList, final String keyName)
	{
		if (isD_sorted())
		{
			return binarySearchPObject(aList, keyName);
		}
		else
		{

			final Object[] pobjArray = aList.toArray();
			final int upper = pobjArray.length;

			// not presently sorted
			PObject obj;

			for (int i = upper - 1; i >= 0; --i)
			{
				obj = (PObject) pobjArray[i];

				if (keyName.equals(obj.getKeyName()))
				{
					return obj;
				}
			}
		}

		return null;
	}

	public static boolean selectPaper(final String paperName)
	{
		for (int i = 0; i < SystemCollections.getUnmodifiablePaperInfo().size(); ++i)
		{

			final PaperInfo pi = (PaperInfo) SystemCollections.getUnmodifiablePaperInfo().get(i);

			if (pi.getName().equals(paperName))
			{
				setSelectedPaper(i);
				return true;
			}
		}

		setSelectedPaper(-1);
		return false;
	}

	/**
	 * Return true if resizing the equipment will have any "noticable" effect
	 *  checks for cost modification, armor bonus, weight, capacity
	 * @param aEq
	 * @param typeList
	 * @return
	 */
	public static boolean canResizeHaveEffect(Equipment aEq, List typeList)
	{
		// cycle through typeList and see if it matches one in the BONUS:ITEMCOST|TYPE=etc on sizeadjustment
		if (typeList == null)
		{
			typeList = aEq.typeList();
		}

		for (int iSize = 0; iSize < SystemCollections.getSizeAdjustmentListSize(); ++iSize)
		{
			final SizeAdjustment sadj = SystemCollections.getSizeAdjustmentAtIndex(iSize);
			if ((!Utility.doublesEqual(sadj.getBonusTo("ITEMCOST", typeList, 1.0), 1.0)) || (aEq.isArmor() || aEq.isShield()
				&& !Utility.doublesEqual(sadj.getBonusTo("ACVALUE", typeList, 1.0), 1.0))
				|| (!Utility.doublesEqual(aEq.getWeightAsDouble(), 0.0)
				&& !Utility.doublesEqual(sadj.getBonusTo("ITEMWEIGHT", typeList, 1.0), 1.0)) || (aEq.isContainer()
				&& !Utility.doublesEqual(sadj.getBonusTo("ITEMCAPACITY", typeList, 1.0), 1.0)))
			{
				return true;
			}
		}

		return false;
	}

	private static void setbAutoGeneration(final boolean bAutoGeneration)
	{
		Globals.bAutoGeneration = bAutoGeneration;
	}

	public static void sortCampaigns()
	{
		sortPObjectList(getClassList());
		sortPObjectList(getSkillList());
		sortPObjectList(getFeatList());
		sortPObjectList(getDeityList());
		sortPObjectList(getDomainList());
		sortPObjectList(getEquipmentList());
		sortPObjectList(getArmorProfList());
		sortPObjectList(getTemplateList());
		sortPObjectList(getModifierList());
		sortPObjectList(getLanguageList());
		setD_sorted(true);
	}

	/**
	 * Sorts chooser lists using the appropriate method, based on the type of the first item in either list.
	 * Not pretty, but it works.
	 * @param availableList
	 * @param selectedList
	 */
	public static void sortChooserLists(final List availableList, final List selectedList)
	{

		final boolean stringsInList;

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

	public static List sortPObjectList(final List aList)
	{
		Collections.sort(aList, pObjectComp);
		return aList;
	}

	public static List sortPObjectListByName(final List aList)
	{
		Collections.sort(aList, pObjectNameComp);
		return aList;
	}

	public static int sizeInt(final String aSize)
	{
		return sizeInt(aSize, 0);
	}

	public static int sizeInt(final String aSize, final int defaultValue)
	{
		for (int iSize = 0; iSize <= SystemCollections.getSizeAdjustmentListSize() - 1; ++iSize)
		{
			if (aSize.startsWith(SystemCollections.getSizeAdjustmentAtIndex(iSize).getAbbreviation()))
			{
				return iSize;
			}
		}

		return defaultValue;
	}

	private static List getLanguageNamesFromListOfType(final List langList, final String aType)
	{
		final List retSet = new ArrayList();

		for (Iterator i = langList.iterator(); i.hasNext();)
		{
			final Language aLang = (Language) i.next();

			if (aLang != null && ((aType.length() > 0 && aType.charAt(0) == '!' && !aLang.isType(aType)) || aLang.isType(aType)))
			{
				retSet.add(aLang.getName());
			}
		}
		return retSet;
	}

	private static double getLoadMultForSize(final PlayerCharacter aPC)
	{
		double mult = 1.0;
		SizeAdjustment sadj = null;

		if (getLoadStrings().size() != 0)
		{
			final String aString = aPC.getSize();
			final String bString = (String) getLoadStrings().get(0);
			final StringTokenizer aTok = new StringTokenizer(bString, ",", false);

			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();
				if (aString.charAt(0) == cString.charAt(0))
				{
					sadj = SystemCollections.getSizeAdjustmentAtIndex(sizeInt(aString));
					mult = Double.parseDouble(cString.substring(cString.lastIndexOf('|') + 1));
					break;
				}
			}
		}
		if (sadj == null)
		{
			sadj = SystemCollections.getDefaultSizeAdjustment();
		}
		mult += sadj.bonusTo("LOADMULT", "TYPE=SIZE");

		return mult;
	}

	private static void createEmptyRace()
	{
		if( s_EMPTYRACE==null )
		{
			s_EMPTYRACE = new Race();
			s_EMPTYRACE.setName(Constants.s_NONESELECTED);
			s_EMPTYRACE.setTypeInfo("HUMANOID");
		}
		getRaceMap().put(Constants.s_NONESELECTED, s_EMPTYRACE);
	}

	private static void createItem(final Equipment eq, final int iSize)
	{
		createItem(eq, null, iSize);
	}

	private static void createItem(final Equipment eq, final EquipmentModifier eqMod)
	{
		createItem(eq, eqMod, -1);
	}

	private static void createItem(Equipment eq, final EquipmentModifier eqMod, final int iSize)
	{
		if (eq == null)
		{
			return;
		}

		try
		{
			// Armor without an armor bonus is an exception
			//
			if (!eq.getModifiersAllowed() || (eq.isArmor() && (eq.getACMod().intValue() == 0) && ((eqMod != null) && !eqMod.getName().equalsIgnoreCase("MASTERWORK"))))
			{
				return;
			}

			eq = (Equipment) eq.clone();

			if (eq == null)
			{
				Logging.errorPrint("could not clone item");
				return;
			}

			if (eqMod != null)
			{
				eq.addEqModifier(eqMod, true);

				if (eq.isWeapon() && eq.isDouble())
				{
					eq.addEqModifier(eqMod, false);
				}
			}

			if ((iSize >= 0) && (iSize <= SystemCollections.getSizeAdjustmentListSize() - 1))
			{
				eq.resizeItem(SystemCollections.getSizeAdjustmentAtIndex(iSize).getName());
			}
			//
			// Change the names, to protect the innocent
			//
			final String sName = eq.nameItemFromModifiers();
			final Equipment eqExists = getEquipmentKeyed(sName);

			if (eqExists != null)
			{
				return;
			}

			final String newType;

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
			Equipment.getEquipmentTypes().addAll(eq.typeList());

			getEquipmentList().add(eq);
		}
		catch (NumberFormatException exception)
		{
			Logging.errorPrint("createItem: exception: " + eq.getName());
		}
	}

	// Options
	private static Equipment findEquipment(final String aName, final List preNameList, final List postNameList, final List sizList, final String omitString)
	{

		final StringBuffer newName = new StringBuffer(80);
		newName.append(" (");

		if (preNameList != null)
		{
			final List nameList = preNameList;
			appendNameParts(nameList, omitString, newName);
		}

		if (sizList != null)
		{
			// Append 1st size if multiple sizes
			//
			if (sizList.size() > 1)
			{
				newName.append((String) sizList.get(0));
			}
		}

		if (postNameList != null)
		{
			appendNameParts(postNameList, omitString, newName);
		}

		if (newName.length() == 2)
		{
			newName.setLength(0);
		}
		else
		{
			newName.append(')');
		}

		final Equipment eq = getEquipmentKeyed(aName + newName);

		return eq;
	}

	/**
	 * Appends name parts to the newName.
	 * @param nameList
	 * @param omitString
	 * @param newName
	 */
	private static void appendNameParts(List nameList, final String omitString, final StringBuffer newName)
	{
		for (Iterator e = nameList.iterator(); e.hasNext();)
		{

			final String namePart = (String) e.next();

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

	public static StringBuffer getSection15()
	{
		return section15;
	}

	public static boolean isInGameMode(final String gameMode)
	{
		if ((gameMode.length() == 0) || ((SettingsHandler.getGame() != null) && gameMode.equalsIgnoreCase(SettingsHandler.getGame().getName())))
		{
			return true;
		}
		return false;
	}

	/**
	 * Returns the string to use for displaying height.
	 */
	public static String getHeightDisplay()
	{
		return SettingsHandler.getGame().getHeightDisplay();
	}

	/**
	 * Returns the string to use for displaying weight.
	 */
	public static String getWeightDisplay()
	{
		return SettingsHandler.getGame().getWeightDisplay();
	}

	/**
	 * Returns the string to use for displaying abbreviated movement rate type.
	 */
	public static String getAbbrMovementDisplay()
	{
		return SettingsHandler.getGame().getAbbrMovementDisplay();
	}

	/**
	 * Returns the string to use for displaying (standard) currency.
	 */
	public static String getCurrencyDisplay()
	{
		return SettingsHandler.getGame().getCurrencyDisplay();
	}

	public static String getLongCurrencyDisplay()
	{
		return SettingsHandler.getGame().getLongCurrencyDisplay();
	}

	/**
	 * Get the long definition for hit points.
	 * @return
	 */

	public static String getGameModeHitPointText()
	{
		return SettingsHandler.getGame().getHPText();
	}

	/**
	 * Get the abbreviation to be used for hit points.
	 * @return
	 */
	public static String getGameModeHPAbbrev()
	{
		return SettingsHandler.getGame().getHPAbbrev();
	}

	/**
	 * Gets the information for Displaying a Variable.
	 * @return
	 */
	public static String getGameModeVariableDisplayText()
	{
		return SettingsHandler.getGame().getVariableDisplayText();
	}

	public static String getGameModeVariableDisplayName()
	{
		return SettingsHandler.getGame().getVariableDisplayName();
	}

	public static String getGameModeVariableDisplay2Text()
	{
		return SettingsHandler.getGame().getVariableDisplay2Text();
	}

	public static String getGameModeVariableDisplay2Name()
	{
		return SettingsHandler.getGame().getVariableDisplay2Name();
	}

	public static String getGameModeVariableDisplay3Text()
	{
		return SettingsHandler.getGame().getVariableDisplay3Text();
	}

	public static String getGameModeVariableDisplay3Name()
	{
		return SettingsHandler.getGame().getVariableDisplay3Name();
	}

	public static String getGameModeAlignmentText()
	{
		return SettingsHandler.getGame().getAlignmentText();
	}

	public static String getGameModeACText()
	{
		return SettingsHandler.getGame().getACText();
	}

	public static String getGameModeACAbbrev()
	{
		return SettingsHandler.getGame().getACAbbrev();
	}

	public static boolean getGameModeShowClassDefense()
	{
		return SettingsHandler.getGame().getShowClassDefense();
	}

	public static String getGameModeAltHPText()
	{
		return SettingsHandler.getGame().getAltHPText();
	}

	public static String getGameModeAltHPAbbrev()
	{
		return SettingsHandler.getGame().getAltHPAbbrev();
	}

	public static String getGameModeDamageResistanceText()
	{
		return SettingsHandler.getGame().getDamageResistanceText();
	}

	public static boolean getGameModeShowSpellTab()
	{
		return SettingsHandler.getGame().getTabShown(Constants.TAB_SPELLS);
	}

	public static String getGameModeSpellRangeFormula(String aRange)
	{
		return SettingsHandler.getGame().getSpellRangeFormula(aRange);
	}

	public static String getGameModeMoveUnit()
	{
		return SettingsHandler.getGame().getMoveUnit();
	}

	/**
	 * Returns the name of the Default Spell Book, or null if there is no default spell book.
	 */
	public static String getDefaultSpellBook()
	{
		String book = null;
		if (SettingsHandler.getGame() != null)
		{
			book = SettingsHandler.getGame().getDefaultSpellBook();
		}
		return book;
	}

	public static String getGameModeBaseSpellDC()
	{
		return SettingsHandler.getGame().getSpellBaseDC();
	}

	/**
	 * returns a List of LevelInfo objects
	 **/
	public static List getLevelInfo()
	{
		return SettingsHandler.getGame().getLevelInfo();
	}

	private static int bonusParsing(Iterator i, int level, int num)
	{
		// should be in format levelnum,rangenum
		final String l = i.next().toString();
		final StringTokenizer aTok = new StringTokenizer(l, "|", false);
		final int startLevel = Integer.parseInt(aTok.nextToken());
		final int rangeLevel = Integer.parseInt(aTok.nextToken());
		if (level == startLevel || (level > startLevel && rangeLevel > 0 && ((level - startLevel) % rangeLevel == 0)))
		{
			++num;
		}
		return num;
	}

	static int getBonusFeatsForLevel(int level)
	{
		int num = 0;
		for (Iterator i = SettingsHandler.getGame().getBonusFeatLevels().iterator(); i.hasNext();)
		{
			num = bonusParsing(i, level, num);
		}
		return num;
	}

	static int getBonusStatsForLevel(int level)
	{
		int num = 0;
		for (Iterator i = SettingsHandler.getGame().getBonusStatLevels().iterator(); i.hasNext();)
		{
			num = bonusParsing(i, level, num);
		}
		return num;
	}

	static int getSkillMultiplierForLevel(int level)
	{
		final List sml = SettingsHandler.getGame().getSkillMultiplierLevels();
		if ((level > sml.size()) || (level <= 0))
		{
			return 1;
		}
		return Integer.parseInt(sml.get(level - 1).toString());
	}

	public static List getLoadStrings()
	{
		return SettingsHandler.getGame().getLoadStrings();
	}

	public static List getGlobalDeityList()
	{
		return SettingsHandler.getGame().getDeityList();
	}

	public static List getAllowedGameModes()
	{
		return SettingsHandler.getGame().getAllowedModes();
	}

	//
	// Spell info
	//
	public static void addSpellCastingTimesSet(final String aString)
	{
		castingTimesSet.add(aString);
	}

	public static SortedSet getCastingTimesSet()
	{
		return castingTimesSet;
	}

	public static void addSpellComponentSet(final String aString)
	{
		componentSet.add(aString);
	}

	public static SortedSet getComponentSet()
	{
		return componentSet;
	}

	public static void addSpellDescriptorSet(final String aString)
	{
		descriptorSet.add(aString);
	}

	public static SortedSet getDescriptorSet()
	{
		return descriptorSet;
	}

	public static void addDurationSet(final String aString)
	{
		durationSet.add(aString);
	}

	public static SortedSet getDurationSet()
	{
		return durationSet;
	}

	public static void addSpellRangesSet(final String aString)
	{
		rangesSet.add(aString);
	}

	public static SortedSet getRangesSet()
	{
		return rangesSet;
	}

	public static void addSpellSaveInfoSet(final String aString)
	{
		saveInfoSet.add(aString);
	}

	public static SortedSet getSaveInfoSet()
	{
		return saveInfoSet;
	}

	public static void addSpellSrSet(final String aString)
	{
		srSet.add(aString);
	}

	public static SortedSet getSrSet()
	{
		return srSet;
	}

	public static void addSpellTargetSet(final String aString)
	{
		targetSet.add(aString);
	}

	public static SortedSet getTargetSet()
	{
		return targetSet;
	}

	private static void clearSpellSets()
	{
		castingTimesSet.clear();
		componentSet.clear();
		descriptorSet.clear();
		durationSet.clear();
		rangesSet.clear();
		saveInfoSet.clear();
		srSet.clear();
		targetSet.clear();
	}

	public static int rollHP(final int min, final int max, final String name, final int level)
	{
		int roll;
		switch (SettingsHandler.getHPRollMethod())
		{
			case Constants.HP_USERROLLED:
				roll = -1;
				break;

			case Constants.HP_AVERAGE:
				// (n+1)/2
				// average roll on a die with an  odd # of sides works out exactly
				// average roll on a die with an even # of sides will have an extra 0.5
				roll = max - min;
				if (((level & 0x01) == 0) && ((roll & 0x01) != 0))
				{
					++roll;
				}
				roll = min + (roll / 2);
				break;

			case Constants.HP_AUTOMAX:
				roll = max;
				break;

			case Constants.HP_PERCENTAGE:
				roll = min - 1 + (int) ((SettingsHandler.getHPPct() * (max - min + 1)) / 100.0);
				break;

			case Constants.HP_STANDARD:
			default:
				roll = Math.abs(Globals.getRandomInt(max - min + 1)) + min;
				break;

				//TODO: Can we put these back now? XXX
//			case Constants.s_HP_LIVING_GREYHAWK:
//				if (totalLevels == 1)
//					roll = max;
//				else
//					roll = (int)Math.floor((max + min) / 2) + 1;
//				break;
//			case Constants.s_HP_LIVING_CITY:
//				if (totalLevels == 1 || totalLevels == 2)
//					roll = max;
//				else
//				{
//					roll = (int)Math.floor(3 * max / 4);
//					// In the bizarre case a class has a max of 1, need to fix that Floor will make that 0 instead.
//					if (roll < min) roll = min;
//				}
//				break;
		}
		if (SettingsHandler.getShowHPDialogAtLevelUp())
		{
			final Object[] rollChoices = new Object[max - min + 2];
			rollChoices[0] = Constants.s_NONESELECTED;
			for (int i = min; i <= max; ++i)
			{
				rollChoices[i - min + 1] = new Integer(i);
			}
			for (; ;)
			{
				//TODO: This must be refactored away. Core shouldn't know about gui.
				final Object selectedValue = GuiFacade.showInputDialog(Globals.getRootFrame(), "Randomly generate a number between " + min + " and " + max + "." + Constants.s_LINE_SEP + "Select it from the box below.", Globals.getGameModeHitPointText() + " for " + Utility.ordinal(level) + " level of " + name, GuiFacade.INFORMATION_MESSAGE, null, rollChoices, new Integer(roll));

				if ((selectedValue != null) && (selectedValue instanceof Integer))
				{
					roll = ((Integer) selectedValue).intValue();
					break;
				}
			}
		}
		return roll;
	}

	public static void executePostExportCommand(String fileName)
	{
		String aString = SettingsHandler.getPostExportCommand();
		int x = 100;
		while (aString.indexOf("%") >= 0)
		{
			final String beforeString = aString.substring(0, aString.indexOf("%"));
			final String afterString = aString.substring(aString.indexOf("%") + 1);
			aString = beforeString + fileName + afterString;
			if (--x <= 0)
			{
				break;
			}
		}
		if (!"".equals(aString))
		{
			try
			{
				Runtime.getRuntime().exec(aString);
			}
			catch (IOException ex)
			{
				Logging.errorPrint("Could not execute " + aString + " after exporting " + fileName, ex);
			}
		}
	}

	public static Map getBonusSpellMap()
	{
		return bonusSpellMap;
	}
}
