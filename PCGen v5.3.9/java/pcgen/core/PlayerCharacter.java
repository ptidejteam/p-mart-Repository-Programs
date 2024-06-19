/*
 * PlayerCharacter.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
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
 * Last Edited: $Date: 2006/02/21 01:16:13 $
 *
 */

package pcgen.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.Utility;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.utils.GuiFacade;
import pcgen.io.ExportHandler;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * <code>PlayerCharacter</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class PlayerCharacter extends Object
{
	// Constants for use in getBonus
	public static final int ATTACKBONUS = 0;
	public static final int MONKBONUS = 4;

	private static final BigDecimal BIG_ONE = new BigDecimal("1.00");
	private String DPoints = "0";
	private int FPoints = 0;
	// Used to Force Encumberance to a Given type for BASEMOVEMENT
	private int iForceLoad = -1;

	private int age = 0; // in years
	// Whether one can trust the most recently calculated aggregateFeatList
	private boolean aggregateFeatsStable = false;
	private int alignment = 9; // 0 = LG to 8 = CE and 9 is <none selected>
	// whether to add auto known spells each level
	private boolean autoKnownSpells = true;
	// should we also load companions on master load?
	private boolean autoLoadCompanion = false;
	/** Should we sort the gear automatically? */
	private boolean autoSortGear = true;
	/** Should we sort the skills automatically? */
	private boolean autoSortSkills = true;
	// Should temp mods/bonuses be used/saved?
	private boolean useTempMods = false;
	// Whether one can trust the most recently calculated automaticFeatList
	private boolean automaticFeatsStable = false;
	private String bio = "";
	private Map bonusMap = new HashMap();
	private Map activeBonusMap = new HashMap();
	// Temoprary Bonuses
	private ArrayList tempBonusList = new ArrayList();
	private List tempBonusItemList = new ArrayList();
	private List activeBonusList = new LinkedList();
	private List processedBonusList = new LinkedList();
	private String catchPhrase = "";
	private HashMap domainSourceMap = new HashMap(); // source of granted domains
	private final List characterDomainList = new ArrayList(); // of CharacterDomain
	private ArrayList classList = new ArrayList(); // of Class
	private final ArrayList companionModList = new ArrayList(); // of CompanionMod
	private int costPool = 0;
	private String currentEquipSetName = "";
	private String calcEquipSetId = "0.1";
	private int currentHP = 0;
	private static int decrement;
	private Deity deity = null;
	private String description = "";
	private String descriptionLst = "EMPTY";
	private boolean dirtyFlag = false;
	private final List equipSetList = new ArrayList(); // of Equip Sets
	private List equipmentList = new ArrayList(); // of Equipment
	private List equipmentMasterList = new ArrayList();
	private int earnedXP = 0;
	private String eyeColor = "";
	private final SortedSet favoredClasses = new TreeSet();
	private final ArrayList featList = new ArrayList(); // of Feat
	private double feats = 0; // pool of feats remaining to distribute
	/** This may be different from character name... */
	private String fileName = "";
	private final List followerList = new ArrayList(); // of Followers
	private Follower followerMaster = null; // Who is the master now?
	private int freeLangs = 0;
	private String gender = "Male";
	private BigDecimal gold = new BigDecimal("0.00");
	private String hairColor = "";
	private String hairStyle = "";
	private String handed = "Right";
	private int height = 0; // in inches
	private boolean importing = false;
	private static int loopValue = 0;
	private String interests = "";
	private List kitList = null;
	private final TreeSet languages = new TreeSet();
	private static String lastVariable = null;
	private String location = "";
	private String birthplace = "";
	private static String loopVariable = "";
	private final ArrayList miscList = new ArrayList(3);
	private Integer[] movements = new Integer[0];
	private String[] movementTypes = new String[0];
	private Integer[] movementMult = new Integer[0];
	private String[] movementMultOp = new String[0];
	private String name = "";
	private static final int nonProficiencyPenalty = -4;
	private final ArrayList notesList = new ArrayList(); // of Notes
	private String phobias = "";
	private String playersName = "";
	private int poolAmount = 0; // pool of stats remaining to distribute
	/** This may be different from file name... */
	private String portraitPath = "";
	private final ArrayList primaryWeapons = new ArrayList();
	private ArrayList qualifyArrayList = new ArrayList();
	private boolean qualifyListStable = false;
	private Race race = null;
	private String racialFavoredClass = "";
	private String residence = "";
	private final ArrayList secondaryWeapons = new ArrayList();
	private final ArrayList skillList = new ArrayList(); // of Skill
	private int skillPoints = 0; // pool of skills remaining to distribute
	private String skinColor = "";
	// Collections of String (probably should be full objects)
	private final ArrayList specialAbilityList = new ArrayList();
	private String speechTendency = "";
	private final List spellBooks = new ArrayList();

	private HashMap spellLevelMap = new HashMap();
	private HashMap spellInfoMap = new HashMap();
	private int spellLevelTemp = 0;
	private List stableAggregateFeatList = null;
	private List stableAutomaticFeatList = null;
	private List stableVirtualFeatList = null;
	private String tabName = "";
	//
	// We don't want this list sorted until after it has been added
	// to the character,  The reason is that sorting prevents
	// .CLEAR-TEMPLATES from clearing the OLDER template languages.
	private final List templateAutoLanguages = new ArrayList();
	private final SortedSet templateLanguages = new TreeSet();
	private final ArrayList templateList = new ArrayList(); // of Template
	private String trait1 = "";
	private String trait2 = "";
	private final boolean useMonsterDefault = SettingsHandler.isMonsterDefault();
	private final ArrayList variableList = new ArrayList();
	private HashSet variableSet = new HashSet();
	// Whether one can trust the most recently calculated virtualFeatList
	private boolean virtualFeatsStable = false;
	private final TreeSet weaponProfList = new TreeSet();
	private final ArrayList armorProfList = new ArrayList();
	private boolean armorProfListStable = false;
	private int weight = 0; // in pounds
	private final StatList statList = new StatList();
	private String region = null;
	private String subRegion = null;
	private List pcLevelInfo = new ArrayList();
	// output sheet locations
	private String outputSheetHTML = "";
	private String outputSheetPDF = "";

	/**
	 * #####################################################
	 **/

	//
	// Bryan wanted this to be optional, but if you can reassign
	// racial auto feats, when you reopen the character, you get
	// the feats that were exchanged back
	//
	private static boolean canReassignRacialFeats()
	{
		return false;
	}

	private static boolean canReassignTemplateFeats()
	{
		return false;
	}

	/**
	 * Location of HTML Output Sheet
	 **/
	public String getSelectedCharacterHTMLOutputSheet()
	{
		return outputSheetHTML;
	}

	public void setSelectedCharacterHTMLOutputSheet(String aString)
	{
		outputSheetHTML = aString;
	}

	/**
	 * Location of PDF Output Sheet
	 **/
	public String getSelectedCharacterPDFOutputSheet()
	{
		return outputSheetPDF;
	}

	public void setSelectedCharacterPDFOutputSheet(String aString)
	{
		outputSheetPDF = aString;
	}

	boolean isMonsterDefault()
	{
		return useMonsterDefault;
	}

	public boolean isAggregateFeatsStable()
	{
		return automaticFeatsStable && virtualFeatsStable && aggregateFeatsStable;
	}

	public void setAggregateFeatsStable(boolean stable)
	{
		aggregateFeatsStable = stable;
	}

	private boolean isVirtualFeatsStable()
	{
		return virtualFeatsStable;
	}

	public void setVirtualFeatsStable(boolean stable)
	{
		virtualFeatsStable = stable;
	}

	private boolean isAutomaticFeatsStable()
	{
		return automaticFeatsStable;
	}

	public void setAutomaticFeatsStable(boolean stable)
	{
		automaticFeatsStable = stable;
	}

	public void addFeat(Feat aFeat)
	{
		if (featList.contains(aFeat))
		{
			Logging.errorPrint("Adding duplicate feat: "+aFeat.getName());
		}
		featList.add(aFeat);
		setAggregateFeatsStable(false);
	}

	/**
	 * @return nonProficiencyPenalty. Searches templates first.
	 */
	public int getNonProficiencyPenalty()
	{
		int npp = -4;
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			npp = aTemplate.getNonProficiencyPenalty();
		}

		if (npp != -4 && npp <= 0)
		{
			return npp;
		}
		else
		{
			return nonProficiencyPenalty;
		}
	}

	/**
	 * Selector
	 *
	 * @return primary weapons
	 */
	public List getPrimaryWeapons()
	{
		return primaryWeapons;
	}

	/**
	 * Selector
	 *
	 * @return secondary weapons
	 */
	public List getSecondaryWeapons()
	{
		return secondaryWeapons;
	}

	public TreeSet getWeaponProfList()
	{
		final TreeSet wp = new TreeSet(weaponProfList);
		//
		// Add any selected racial bonus weapons
		//
		if (race != null)
		{
			race.addSelectedWeaponProfBonusTo(wp);
		}
		//
		// Add any selected class bonus weapons
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			aClass.addSelectedWeaponProfBonusTo(wp);
		}

		// Add any selected template bonus weapons
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			aTemplate.addSelectedWeaponProfBonusTo(wp);
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain) e.next();
				final Domain aDomain = aCD.getDomain();
				if (aDomain != null)
				{
					aDomain.addSelectedWeaponProfBonusTo(wp);
				}
			}
		}
		return wp;
	}

	public void addArmorProfs(List aList)
	{
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			addArmorProf((String) i.next());
		}
	}

	public void addArmorProf(String aProf)
	{
		if (!armorProfList.contains(aProf))
		{
			//
			// Insert all types at the head of the list
			//
			if (aProf.startsWith("TYPE=") || aProf.startsWith("TYPE."))
			{
				armorProfList.add(0, aProf);
			}
			else
			{
				armorProfList.add(aProf);
			}
		}
	}

	protected ArrayList getArmorProfList()
	{
		if (armorProfListStable)
		{
			return armorProfList;
		}
		List autoArmorProfList = getAutoArmorProfList();
		addArmorProfs(autoArmorProfList);
		List selectedProfList = getSelectedArmorProfList();
		addArmorProfs(selectedProfList);
		armorProfListStable = true;
		return armorProfList;
	}

	public void setArmorProfListStable(boolean arg)
	{
		armorProfListStable = arg;
	}

	private List getAutoArmorProfList()
	{
		ArrayList aList = new ArrayList();
		if (deity != null)
		{
			deity.addAutoTagsToList("ARMORPROF", aList);
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			aClass.addAutoTagsToList("ARMORPROF", aList);
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			aFeat.addAutoTagsToList("ARMORPROF", aList);
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			aSkill.addAutoTagsToList("ARMORPROF", aList);
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				aCD.getDomain().addAutoTagsToList("ARMORPROF", aList);
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				eq.addAutoTagsToList("ARMORPROF", aList);
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			aTemplate.addAutoTagsToList("ARMORPROF", aList);
		}
		return aList;
	}

	private ArrayList getSelectedArmorProfList()
	{
		ArrayList aList = new ArrayList();
		if (deity != null && deity.getSelectedArmorProfs() != null)
		{
			aList.addAll(deity.getSelectedArmorProfs());
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			if (aClass.getSelectedArmorProfs() != null)
			{
				aList.addAll(aClass.getSelectedArmorProfs());
			}
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			if (aFeat.getSelectedArmorProfs() != null)
			{
				aList.addAll(aFeat.getSelectedArmorProfs());
			}
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			if (aSkill.getSelectedArmorProfs() != null)
			{
				aList.addAll(aSkill.getSelectedArmorProfs());
			}
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null && aCD.getDomain().getSelectedArmorProfs() != null)
			{
				aList.addAll(aCD.getDomain().getSelectedArmorProfs());
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				if (eq.getSelectedArmorProfs() != null)
				{
					aList.addAll(eq.getSelectedArmorProfs());
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			if (aTemplate.getSelectedArmorProfs() != null)
			{
				aList.addAll(aTemplate.getSelectedArmorProfs());
			}
		}
		return aList;
	}

	private boolean isProficientWithArmor(Equipment eq)
	{
		final ArrayList aList = getArmorProfList();
		//
		// First, check to see if fits into any TYPE granted
		//
		for (int i = 0; i < aList.size(); ++i)
		{
			final String aString = aList.get(i).toString();
			if (!aString.startsWith("TYPE=") && !aString.startsWith("TYPE."))
			{
				break;
			}
			if (eq.isType(aString.substring(5)))
			{
				return true;
			}
		}

		return aList.contains(eq.profName());
	}

	public boolean hasWeaponProfNamed(String aName)
	{
		for (Iterator i = getWeaponProfList().iterator(); i.hasNext();)
		{
			if (aName.equalsIgnoreCase((String) i.next()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Left alone for a while, will remove in a month unless it is used.
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 * @param aName
	 * @return
	 */
	private boolean hasArmorProfType(String aName)
	{
		for (Iterator i = getArmorProfList().iterator(); i.hasNext();)
		{
			if (aName.equalsIgnoreCase((String) i.next()))
			{
				return true;
			}
		}
		return false;
	}

	public int getAlignment()
	{
		return alignment;
	}

	public void setCurrentHP(int currentHP)
	{
		this.currentHP = currentHP;
		setDirty(true);
	}

	boolean checkQualifyList(String qualifierItem)
	{
		return getQualifyList().contains(qualifierItem);
	}

	private void setQualifyListStable(boolean state)
	{
		qualifyListStable = state;
	}

	/*
	 * Build on-the-fly so removing templates won't mess up qualify list
	 */
	ArrayList getQualifyList()
	{
		if (!qualifyListStable)
		{
			qualifyArrayList = new ArrayList();
			for (int i = 0, x = templateList.size(); i < x; ++i)
			{
				final PCTemplate template = (PCTemplate) templateList.get(i);
				final String tempQualifyList = template.getQualifyString();
				final StringTokenizer aTok = new StringTokenizer(tempQualifyList, "|");
				while (aTok.hasMoreTokens())
				{
					final String qualifier = aTok.nextToken();
					if (!qualifyArrayList.contains(qualifier))
					{
						qualifyArrayList.add(qualifier);
					}
				}
			}
			setQualifyListStable(true);
		}
		return qualifyArrayList;
	}

	/**
	 * Temporary Bonuses
	 **/
	public List getTempBonusItemList()
	{
		return tempBonusItemList;
	}

	public void addTempBonusItemList(Equipment aEq)
	{
		getTempBonusItemList().add(aEq);
	}

	public void removeTempBonusItemList(Equipment aEq)
	{
		getTempBonusItemList().remove(aEq);
	}

	public ArrayList getTempBonusList()
	{
		return tempBonusList;
	}

	/**
	 * Adds a "temporary" bonus
	 **/
	public void addTempBonus(BonusObj aBonus)
	{
		getTempBonusList().add(aBonus);
	}

	/**
	 * Removes a "temporary" bonus
	 **/
	public void removeTempBonus(BonusObj aBonus)
	{
		getTempBonusList().remove(aBonus);
	}

	/**
	 * get bonus "applied" to the target Object
	 * @return	BonusObj
	 *
	 * This will be used when I expand the functionality of
	 * the TempBonus tab. Please leave -- JSC 08/08/03
	 **/
	public BonusObj getTempBonus(Object aTarget)
	{
		for (Iterator i = getTempBonusList().iterator(); i.hasNext();)
		{
			final BonusObj aBonus = (BonusObj) i.next();
			final Object aVal = aBonus.getTargetObject();
			if (aVal == aTarget)
			{
				return aBonus;
			}
		}
		return null;
	}

	/**
	 * Active BonusObj's
	 **/
	private List getActiveBonusList()
	{
		return activeBonusList;
	}

	private void addToActiveBonusList(BonusObj aBonus)
	{
		activeBonusList.add(aBonus);
	}

	/**
	 * @param aBonus
	 *
	 * This will be used when I expand the functionality of
	 * the TempBonus tab. Please leave -- JSC 08/08/03
	 **/
	public void removeActiveBonus(BonusObj aBonus)
	{
		activeBonusList.remove(aBonus);
	}

	private void clearActiveBonuses()
	{
		activeBonusList.clear();
	}

	public Map getActiveBonusMap()
	{
		return activeBonusMap;
	}

	public void putActiveBonusMap(String aKey, String aVal)
	{
		activeBonusMap.put(aKey, aVal);
	}

	private void clearActiveBonusMap()
	{
		activeBonusMap.clear();
	}

	/**
	 * Used to create the Bonus HashMap from all active bonuses
	 *
	 * @return	 a List of BONUS strings
	 **/
	private List getStringListFromBonus(BonusObj aBonus, PObject anObj)
	{
		List aList = new LinkedList();

		String bInfoString = aBonus.getBonusInfo();
		StringTokenizer aTok = new StringTokenizer(bInfoString, ",");
		while (aTok.hasMoreTokens())
		{
			String bonusInfo = (String) aTok.nextToken();

			// First check for ARMORPROF= or WEAPONPROF=
			if ((aBonus.getTypeOfBonus().indexOf("PROF=") > 0) && (anObj.getAssociatedCount() > 0))
			{
				for (int i = 0; i < anObj.getAssociatedCount(); ++i)
				{
					StringBuffer ab = new StringBuffer();
					ab.append(aBonus.getTypeOfBonus());
					ab.append(anObj.getAssociated(i)).append('.');
					ab.append(bonusInfo);
					if (aBonus.getTypeString().length() > 0)
					{
						ab.append('.').append(aBonus.getTypeString());
					}
					aList.add(ab.toString().toUpperCase());
				}
			}
			else if (aBonus.getTypeOfBonus().indexOf("PROF=") > 0)
			{
				StringBuffer ab = new StringBuffer();
				ab.append(aBonus.getTypeOfBonus());
				ab.append(aBonus.getVariable()).append('.');
				ab.append(bonusInfo);
				if (aBonus.getTypeString().length() > 0)
				{
					ab.append('.').append(aBonus.getTypeString());
				}
				aList.add(ab.toString().toUpperCase());
			}
			// Expand the %LIST or %CHOOSE or %VAR part of a bonus
			else if ((anObj.getAssociatedCount() > 0))
			{
				for (int i = 0; i < anObj.getAssociatedCount(); ++i)
				{
					StringBuffer ab = new StringBuffer();
					ab.append(aBonus.getTypeOfBonus()).append('.');
					if (bonusInfo.equals("LIST"))
					{
						ab.append(anObj.getAssociated(i));
					}
					else if ((anObj instanceof EquipmentModifier) && (anObj.getChoiceString().length() > 0))
					{
						ab.append(bonusInfo);
					}
					else
					{
						ab.append(bonusInfo).append('.');
						ab.append(anObj.getAssociated(i));
					}
					if (aBonus.getTypeString().length() > 0)
					{
						ab.append('.').append(aBonus.getTypeString());
					}
					aList.add(ab.toString().toUpperCase());
				}
			}
			else
			{
				StringBuffer ab = new StringBuffer();
				ab.append(aBonus.getTypeOfBonus()).append('.');
				ab.append(bonusInfo);
				if (aBonus.getTypeString().length() > 0)
				{
					ab.append('.').append(aBonus.getTypeString());
				}
				aList.add(ab.toString().toUpperCase());
			}
		}
		return aList;
	}

	/**
	 * Build the bonus HashMap from all active BonusObj's
	 **/
	private void buildActiveBonusMap()
	{
		clearActiveBonusMap();
		processedBonusList.clear();
		//
		// Because there are BONUS: statments that are interdependent
		// We do a first pass of just the "static" bonuses
		for (Iterator b = getActiveBonusList().iterator(); b.hasNext();)
		{
			BonusObj aBonus = (BonusObj) b.next();
			if (!aBonus.isValueStatic())
			{
				continue;
			}
			PObject anObj = (PObject) aBonus.getCreatorObject();
			if (anObj == null)
			{
				continue;
			}
			// Keep track of which bonuses have been calculated
			processedBonusList.add(aBonus);

			for (Iterator as = getStringListFromBonus(aBonus, anObj).iterator(); as.hasNext();)
			{
				String bString = (String) as.next();
				double iBonus = aBonus.getValueAsdouble();
				setActiveBonusStack(iBonus, bString);
				Logging.debugPrint("BONUS: "+anObj.getName()+" : "+iBonus+" : "+bString);
			}
		}
		//
		// Now we do all the BonusObj's that require calculations
		for (Iterator b = getActiveBonusList().iterator(); b.hasNext();)
		{
			BonusObj aBonus = (BonusObj) b.next();
			if (processedBonusList.contains(aBonus))
			{
				continue;
			}
			PObject anObj = (PObject) aBonus.getCreatorObject();
			if (anObj == null)
			{
				continue;
			}
			processBonus(aBonus);
		}
	}

	/**
	 * - Get's a list of dependencies from aBonus
	 * - Finds all active bonuses that add to those dependencies and
	 *   have not been processed and recursivly calls itself
	 * - Once recursed in, it adds the computed bonus to activeBonusMap
	 **/
	private void processBonus(BonusObj aBonus)
	{
		List aList = new LinkedList();

		// Go through all bonuses and check to see if they add to
		// aBonus's dependencies and have not already been processed
		for (Iterator ab = getActiveBonusList().iterator(); ab.hasNext();)
		{
			BonusObj newBonus = (BonusObj) ab.next();
			if (processedBonusList.contains(newBonus))
			{
				continue;
			}
			if (aBonus.getDependsOn(newBonus.getBonusInfo()))
			{
				aList.add(newBonus);
			}
		}
		// go through all the BonusObj's that aBonus depends on
		// and process them first
		for (Iterator ab = aList.iterator(); ab.hasNext();)
		{
			BonusObj newBonus = (BonusObj) ab.next();
			// recursivly call itself
			processBonus(newBonus);
		}

		// Double check that it hasn't been processed yet
		if (processedBonusList.contains(aBonus))
		{
			return;
		}

		// Add to processed list
		processedBonusList.add(aBonus);

		PObject anObj = (PObject) aBonus.getCreatorObject();
		if (anObj == null)
		{
			return;
		}
		// calculate bonus and add to activeBonusMap
		for (Iterator as = getStringListFromBonus(aBonus, anObj).iterator(); as.hasNext();)
		{
			String bString = (String) as.next();
			double iBonus = anObj.calcBonusFrom(aBonus, this);
			setActiveBonusStack(iBonus, bString);
			Logging.debugPrint("BONUS: "+anObj.getName()+" : "+iBonus+" : "+bString);
		}
	}

	/**
	 * Figures out if a bonus should stack based on type,
	 * then adds it to the activeBonus HashMap.
	 * @param bonus
	 * @param bonusType
	 **/
	void setActiveBonusStack(double bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
			// only specific bonuses can actually be fractional
			// -> TODO should define this in external file
			if (!bonusType.startsWith("ITEMWEIGHT") && !bonusType.startsWith("ITEMCOST") && !bonusType.startsWith("ACVALUE") && !bonusType.startsWith("ITEMCAPACITY") && !bonusType.startsWith("LOADMULT"))
			{
				bonus = (double) ((int) bonus); // truncate all but above bonuses
			}
		}
		else
		{
			return;
		}

		// default to non-stacking bonuses
		int index = -1;

		final StringTokenizer aTok = new StringTokenizer(bonusType, ".");
		// e.g. "COMBAT.AC.Dodge"
		if ((bonusType != null) && (aTok.countTokens() >= 2))
		{
			// need to get the 3rd token to see
			// if it should .STACK or .REPLACE
			String aString = aTok.nextToken();
			aString = aTok.nextToken();
			// if the 3rd token is "BASE" we have something like:
			// CHECKS.BASE.Fortitude
			if (aString.equals("BASE"))
			{
				if (aTok.hasMoreTokens())
				{
					// discard next token (Fortitude)
					aString = aTok.nextToken();
				}
				if (aTok.hasMoreTokens())
				{
					// check for a TYPE
					aString = aTok.nextToken();
				}
				else
				{
					// all BASE type bonuses should stack
					aString = null;
				}
			}
			else
			{
				if (aTok.hasMoreTokens())
				{
					// Type: .Dodge
					aString = aTok.nextToken();
				}
				else
				{
					aString = null;
				}
			}

			if (aString != null)
			{
				index = SystemCollections.getUnmodifiableBonusStackList().indexOf(aString); // e.g. Dodge
			}
			//
			// un-named (or un-TYPE'd) bonuses stack
			else if (aString == null)
			{
				index = 1;
			}
			else if (aString.equals("NULL"))
			{
				index = 1;
			}
		}

		// .STACK means stack with everything
		// .REPLACE means stack with other .REPLACE
		if (bonusType != null && (bonusType.endsWith(".STACK") || bonusType.endsWith(".REPLACE")))
		{
			index = 1;
		}

		// If it's a negative bonus, it always needs to be added
		if (bonus < 0)
		{
			index = 1;
		}

		if (index == -1) // a non-stacking bonus
		{
			final String aVal = (String) getActiveBonusMap().get(bonusType);
			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus));
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(Math.max(bonus, Float.parseFloat(aVal))));
			}
		}
		else // a stacking bonus
		{
			if (bonusType == null)
			{
				bonusType = "";
			}

			final String aVal = (String) getActiveBonusMap().get(bonusType);
			if (aVal == null)
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus));
			}
			else
			{
				putActiveBonusMap(bonusType, String.valueOf(bonus + Float.parseFloat(aVal)));
			}
		}
	}

	/**
	 * @return Total bonus for prefix from the activeBonus HashMap
	 **/
	private double sumActiveBonusMap(String prefix)
	{
		double bonus = 0;
		prefix = prefix.toUpperCase();
		final List aList = new LinkedList();
		for (Iterator i = getActiveBonusMap().keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			if (aList.contains(aKey))
			{
				continue;
			}
			if (aKey.startsWith(prefix))
			{
				String rString = aKey;

				// rString could be something like:
				// COMBAT.AC.Armor.REPLACE.STACK
				//
				if (rString.endsWith(".STACK"))
				{
					rString = rString.substring(0, rString.length() - 6);
				}
				else if (rString.endsWith(".REPLACE"))
				{
					rString = rString.substring(0, rString.length() - 8);
				}

				if ((rString.length() > prefix.length()) &&
					!rString.startsWith(prefix + "."))
				{
					continue;
				}

				aList.add(rString);
				aList.add(rString + ".STACK");
				aList.add(rString + ".REPLACE");

				double aBonus = getActiveBonusForMapKey(rString, Double.NaN);
				double replaceBonus = getActiveBonusForMapKey(rString + ".REPLACE", Double.NaN);
				double stackBonus = getActiveBonusForMapKey(rString + ".STACK", 0);
				//
				// Using NaNs in order to be able to get the max
				// between an undefined bonus and a negative
				//
				if (Double.isNaN(aBonus)) // no bonusKey
				{
					if (!Double.isNaN(replaceBonus))
					{
						// no bonusKey, but there
						// is a replaceKey
						bonus += replaceBonus;
					}
				}
				else if (Double.isNaN(replaceBonus))
				{
					// is a bonusKey and no replaceKey
					bonus += aBonus;
				}
				else
				{
					// is a bonusKey and a replaceKey
					bonus += Math.max(aBonus, replaceBonus);
				}
				// always add stackBonus
				bonus += stackBonus;
			}
		}
		return bonus;
	}

	/**
	 * Searches the activeBonus HashMap for aKey
	 * @return defaultValue if aKey not found
	 **/
	private double getActiveBonusForMapKey(String aKey, double defaultValue)
	{
		aKey = aKey.toUpperCase();
		String regVal = (String) getActiveBonusMap().get(aKey);

		if (regVal != null)
		{
			return Float.parseFloat(regVal);
		}
		return defaultValue;
	}

	/**
	 * Adds the List to activeBonuses if it passes RereqToUse Test
	 **/
	private void addListToActiveBonuses(List aList)
	{
		activeBonusList.addAll(aList);
	}

	/**
	 * Compute total bonus from a List of BonusObj's
	 **/
	public double calcBonusFromList(List aList)
	{
		double iBonus = 0;
		if (aList.isEmpty())
		{
			return iBonus;
		}
		for (Iterator b = aList.iterator(); b.hasNext();)
		{
			BonusObj aBonus = (BonusObj) b.next();
			PObject anObj = (PObject) aBonus.getCreatorObject();
			if (anObj == null)
			{
				continue;
			}
			iBonus += anObj.calcBonusFrom(aBonus, this);
		}
		return iBonus;
	}

	/**
	 * Compute total bonus from a List of BonusObj's
	 * Use cost of bonus to adjust total bonus up or down
	 **/
	private double calcBonusWithCostFromList(List aList, boolean subSearch)
	{
		double totalBonus = 0;
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			BonusObj aBonus = (BonusObj) e.next();
			PObject anObj = (PObject) aBonus.getCreatorObject();
			if (anObj == null)
			{
				continue;
			}
			double iBonus = 0;
			if (aBonus.hasPreReqs())
			{
				if (anObj.passesPreReqToUse())
				{
					iBonus = anObj.calcBonusFrom(aBonus, this);
				}
			}
			else
			{
				iBonus = anObj.calcBonusFrom(aBonus, this);
			}

			int k = Math.max(1, (int) (anObj.getAssociatedCount() * ((HasCost) anObj).getCost()));
			if (subSearch && anObj.getAssociatedCount() > 0)
			{
				k = 0;
				for (int f = 0; f < anObj.getAssociatedCount(); ++f)
				{
					final String aString = anObj.getAssociated(f);
					if (aString.equalsIgnoreCase(aBonus.getBonusInfo()))
					{
						++k;
					}
				}
			}
			if (k == 0 && !Utility.doublesEqual(iBonus, 0))
			{
				totalBonus += iBonus;
			}
			else
			{
				totalBonus += iBonus * k;
			}
		}
		return totalBonus;
	}

	/**
	 * ###################################################
	 *   Functions that get all the "active" bonuses and
	 *   add them to the activeBonusList
	 * ###################################################
	 **/

	private void calcAlignmentBonuses()
	{
		if (SystemCollections.getUnmodifiableAlignmentList().isEmpty())
		{
			return;
		}
		for (Iterator e = SystemCollections.getUnmodifiableAlignmentList().iterator(); e.hasNext();)
		{
			final PObject anObj = (PObject) e.next();
			anObj.activateBonuses();
			List tempList = anObj.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	private void calcAgeBonuses()
	{
		String ageSetLine = Globals.getBioSet().getAgeSetLine(this);
		if (ageSetLine == null)
		{
			return;
		}
		List tempList = new LinkedList();
		StringTokenizer aTok = new StringTokenizer(ageSetLine, "\t");
		aTok.nextToken(); // name of ageSet, eg: Middle Aged
		while (aTok.hasMoreTokens())
		{
			final String b = aTok.nextToken();
			if (b.startsWith("BONUS:"))
			{
				BonusObj aBonus = Bonus.newBonus(b.substring(6));
				if (aBonus != null)
				{
					aBonus.setCreatorObject(Globals.getBioSet());
					aBonus.setApplied(true);
					tempList.add(aBonus);
				}
			}
		}
		if (!tempList.isEmpty())
		{
			addListToActiveBonuses(tempList);
		}
	}

	/**
	 * Currently unused
	 * But needed when ArmorProf's get converted to BonusObj's
	 * isntead of just a List of String
	 **/
	private void calcArmorProfBonuses()
	{
		if (getArmorProfList().isEmpty())
		{
			return;
		}
		for (Iterator e = getArmorProfList().iterator(); e.hasNext();)
		{
			ArmorProf ap = (ArmorProf) e.next();
			ap.activateBonuses();
			List tempList = ap.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	private void calcCheckBonuses()
	{
		if (SystemCollections.getUnmodifiableCheckList().isEmpty())
		{
			return;
		}
		for (Iterator e = SystemCollections.getUnmodifiableCheckList().iterator(); e.hasNext();)
		{
			final PObject anObj = (PObject) e.next();
			anObj.activateBonuses();
			List tempList = anObj.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	private void calcClassBonuses()
	{
		if (classList.isEmpty())
		{
			return;
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			aClass.activateBonuses();
			List tempList = aClass.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	private void calcCompanionModBonuses()
	{
		if (companionModList.isEmpty())
		{
			return;
		}
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			cMod.activateBonuses();
			List tempList = cMod.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	private void calcDeityBonuses()
	{
		if (getDeity() == null)
		{
			return;
		}
		getDeity().activateBonuses();
		List tempList = getDeity().getActiveBonuses();
		if (!tempList.isEmpty())
		{
			addListToActiveBonuses(tempList);
		}
	}

	private void calcDomainBonuses()
	{
		if (characterDomainList.isEmpty())
		{
			return;
		}
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) e.next();
			Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				aDomain.activateBonuses();
				List tempList = aDomain.getActiveBonuses();
				if (!tempList.isEmpty())
				{
					addListToActiveBonuses(tempList);
				}
			}
		}
	}

	private void calcEquipmentBonuses()
	{
		if (getEquipmentList().isEmpty())
		{
			return;
		}
		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				eq.activateBonuses();
				List tempList = eq.getActiveBonuses();
				if (!tempList.isEmpty())
				{
					addListToActiveBonuses(tempList);
				}
			}
		}
	}

	private void calcFeatBonuses()
	{
		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat) e.next();
			aFeat.activateBonuses();
			List tempList = aFeat.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	private void calcRaceBonuses()
	{
		if (getRace() == null)
		{
			return;
		}
		getRace().activateBonuses();
		List tempList = getRace().getActiveBonuses();
		if (!tempList.isEmpty())
		{
			addListToActiveBonuses(tempList);
		}
	}

	private void calcSkillBonuses()
	{
		if (getSkillList().isEmpty())
		{
			return;
		}
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill) e.next();
			aSkill.activateBonuses();
			List tempList = aSkill.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	public void calcStatBonuses()
	{
		for (Iterator e = statList.getStats().iterator(); e.hasNext();)
		{
			PCStat aStat = (PCStat) e.next();
			aStat.activateBonuses();
			List tempList = aStat.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	public void calcSizeAdjustmentBonuses()
	{
		getSizeAdjustment().activateBonuses();
		List tempList = getSizeAdjustment().getActiveBonuses();
		if (!tempList.isEmpty())
		{
			addListToActiveBonuses(tempList);
		}
	}

	private void calcTempBonuses()
	{
		if (getTempBonusList().isEmpty())
		{
			return;
		}
		List tempList = new LinkedList(getTempBonusList());
		addListToActiveBonuses(tempList);
	}

	private void calcTemplateBonuses()
	{
		if (getTemplateList().isEmpty())
		{
			return;
		}
		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			PCTemplate aTemp = (PCTemplate) e.next();
			aTemp.activateBonuses();
			List tempList = aTemp.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	/**
	 * Currently unused
	 * But needed when WeaponProf's get converted to BonusObj's
	 * isntead of just a List of String
	 **/
	private void calcWeaponProfBonuses()
	{
		if (getWeaponProfList().isEmpty())
		{
			return;
		}
		for (Iterator e = getWeaponProfList().iterator(); e.hasNext();)
		{
			WeaponProf wp = (WeaponProf) e.next();
			wp.activateBonuses();
			List tempList = wp.getActiveBonuses();
			if (!tempList.isEmpty())
			{
				addListToActiveBonuses(tempList);
			}
		}
	}

	/**
	 * List of Equipment objects
	 **/
	public void setEquipmentList(List eqList)
	{
		equipmentList = eqList;
	}

	public List getEquipmentMasterList()
	{
		ArrayList aList = new ArrayList(equipmentMasterList);
		if (deity != null)
		{
			deity.addAutoTagsToList("EQUIP", aList);
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			aClass.addAutoTagsToList("EQUIP", aList);
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			aFeat.addAutoTagsToList("EQUIP", aList);
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			aSkill.addAutoTagsToList("EQUIP", aList);
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				aCD.getDomain().addAutoTagsToList("EQUIP", aList);
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				eq.addAutoTagsToList("EQUIP", aList);
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			aTemplate.addAutoTagsToList("EQUIP", aList);
		}
		return (List) aList;
	}

	public List getEquipmentList()
	{
		return equipmentList;
	}

	public void addLocalEquipment(Equipment eq)
	{
		equipmentList.add(eq);
	}

	public void removeLocalEquipment(Equipment eq)
	{
		equipmentList.remove(eq);
	}

	public void addEquipment(Equipment eq)
	{
		equipmentList.add(eq);
		if (!equipmentMasterList.contains(eq))
		{
			equipmentMasterList.add(eq);
		}
	}

	public void removeEquipment(Equipment eq)
	{
		equipmentList.remove(eq);
		equipmentMasterList.remove(eq);
	}

	/**
	 * Retrieves a list of the character's equipment in output order. This
	 * is in ascending order of the equipment's outputIndex field.
	 * If multiple items of equipment have the same outputIndex they will
	 * be ordered by name. Note hidden items (outputIndex = -1) are not
	 * included in this list.
	 *
	 * @return An ArrayList of the equipment objects in output order.
	 */
	public List getEquipmentListInOutputOrder()
	{
		return sortEquipmentList(getEquipmentList());
	}

	public List getEquipmentListInOutputOrder(int merge)
	{
		return sortEquipmentList(getEquipmentList(), merge);
	}

	public List getEquipmentMasterListInOutputOrder()
	{
		return mergeEquipmentList(getEquipmentMasterList(), Constants.MERGE_NONE);
	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple
	 * items of equipment have the same outputIndex they will be ordered by
	 * name. Note hidden items (outputIndex = -1) are not included in list.
	 *
	 * @param unsortedEquipList An ArrayList of the equipment to be sorted.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List sortEquipmentList(List unsortedEquipList)
	{
		return sortEquipmentList(unsortedEquipList, Constants.MERGE_ALL);

	}

	/**
	 * Sorts the provided list of equipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple
	 * items of equipment have the same outputIndex they will be ordered by
	 * name. Note hidden items (outputIndex = -1) are not included in list.
	 *
	 * @param unsortedEquipList An ArrayList of the equipment to be sorted.
	 * @param merge How to merge.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private List sortEquipmentList(List unsortedEquipList, int merge)
	{
		if (unsortedEquipList.isEmpty())
		{
			return unsortedEquipList;
		}

		final List sortedList;

		// Merge list for duplicates
		// The sorting is done during the Merge
		sortedList = mergeEquipmentList(unsortedEquipList, merge);

		// Remove the hidden items from the list
		for (Iterator i = sortedList.iterator(); i.hasNext();)
		{
			final Equipment item = (Equipment) i.next();
			if (item.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	public static List mergeEquipmentList(List aList, int merge)
	{
		Collections.sort(aList, new Comparator()
		{
			public int compare(Object obj1, Object obj2)
			{
				int e1 = ((Equipment) obj1).getOutputIndex();
				int obj2Index = ((Equipment) obj2).getOutputIndex();
				// Force unset items (index of 0) to appear at the end
				if (e1 == 0)
				{
					e1 = 999;
				}
				if (obj2Index == 0)
				{
					obj2Index = 999;
				}

				if (e1 > obj2Index)
				{
					return 1;
				}
				else if (e1 < obj2Index)
				{
					return -1;
				}
				else
				{
					if (((Equipment) obj1).getName().compareToIgnoreCase(((Equipment) obj2).getName()) == 0)
					{
						return ((Equipment) obj1).getParentName().compareToIgnoreCase(((Equipment) obj2).getParentName());
					}
					else
					{
						return ((Equipment) obj1).getName().compareToIgnoreCase(((Equipment) obj2).getName());
					}
				}
			}

			public boolean equals(Object obj)
			{
				return false;
			}

			public int hashCode()
			{
				return 0;
			}
		});

		// no merging, just sorting
		if (merge == Constants.MERGE_NONE)
		{
			return aList;
		}

		ArrayList eq1List = new ArrayList();
		ArrayList eq2List = new ArrayList();
		ArrayList mergeList = new ArrayList();

		// create a temporary list to merge with
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			Equipment tempEquip = (Equipment) e.next();
			Equipment eq = (Equipment) tempEquip.clone();
			eq1List.add(eq);
			eq2List.add(eq);
		}

		// merge like equipment within same container
		if (merge == Constants.MERGE_LOCATION)
		{
			for (Iterator e = eq1List.iterator(); e.hasNext();)
			{
				Equipment eq1 = (Equipment) e.next();
				double eQty = eq1.qty();
				boolean found = false;
				for (int i = 0; i < eq2List.size(); i++)
				{
					Equipment eq2 = (Equipment) eq2List.get(i);
					if (eq1 == eq2)
					{
						eq2List.remove(eq2);
						found = true;
						i--;
					}
					else if (eq1.isContainer())
					{
						// no container merge
						continue;
					}
					else if (eq1.isType("TEMPORARY") || eq2.isType("TEMPORARY"))
					{
						// Temporary Bonus generated equipment must not merge
						continue;
					}
					else if (eq1.getName().equals(eq2.getName()) && (eq1.getLocation() == eq2.getLocation()) && eq1.getParentName().equals(eq2.getParentName()))
					{
						eq2List.remove(eq2);
						eQty += eq2.qty();
						found = true;
						i--;
					}
				}
				if (found)
				{
					eq1.setQty(eQty);
					mergeList.add(eq1);
				}
			}
			return mergeList;
		}

		// merge all like equipment together
		if (merge == Constants.MERGE_ALL)
		{
			for (Iterator e1 = eq1List.iterator(); e1.hasNext();)
			{
				Equipment eq1 = (Equipment) e1.next();
				double eQty = 0.0;
				boolean found = false;
				for (int i = 0; i < eq2List.size(); i++)
				{
					Equipment eq2 = (Equipment) eq2List.get(i);
					if (eq1.getName().equals(eq2.getName()))
					{
						if (eq1.isContainer())
						{
							// no container merge
							found = true;
						}
						else if (eq1.isType("TEMPORARY") || eq2.isType("TEMPORARY"))
						{
							// Temporary Bonus generated equipment must not merge
							found = true;
						}
						else
						{
							eq2List.remove(eq2);
							eQty += eq2.qty();
							found = true;
							i--;
						}
					}
				}
				if (eQty <= 0.0)
				{
					eQty = eq1.qty();
				}
				if (found)
				{
					eq1.setQty(eQty);
					mergeList.add(eq1);
				}
			}
			return mergeList;
		}

		return null;
	}

	public Equipment getEquipmentNamed(String aString)
	{
		return getEquipmentNamed(aString, getEquipmentMasterList());
	}

	public Equipment getEquipmentNamed(String aString, List aList)
	{
		if (aList.isEmpty())
		{
			return null;
		}
		Equipment match = null;
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (aString.equalsIgnoreCase(eq.getName()))
			{
				match = eq;
			}
		}
		if (match != null)
		{
			return match;
		}
		return null;
	}

	public void equipmentListAddAll(List aList)
	{
		if (aList.isEmpty())
		{
			return;
		}
		equipmentList.addAll(aList);
		equipmentMasterList.addAll(aList);
	}

	/**
	 * Set's current equipmentList to selected output EquipSet
	 * then loops through all the equipment and sets the correct
	 * status of each (equipped, carried, etc)
	 **/
	public void setCalcEquipmentList()
	{
		setCalcEquipmentList(false);
	}

	public void setCalcEquipmentList(boolean useTempBonuses)
	{
		// First we get the EquipSet that is going to be used
		// to calculate everything from
		String calcId = getCalcEquipSetId();
		EquipSet eSet = getEquipSetByIdPath(calcId);

		if (eSet == null)
		{
			Logging.errorPrint("Error: No EquipSet selected for output");
			return;
		}

		// new equipment list
		List eqList = new ArrayList();
		// set PC's equipmentList to new one
		setEquipmentList(eqList);

		// get all the PC's EquipSet's
		List pcEquipSetList = getEquipSet();

		if (pcEquipSetList.isEmpty())
		{
			return;
		}

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(pcEquipSetList);

		// loop through all the EquipSet's and create equipment
		// then set status to equipped and add to PC's eq list
		for (Iterator e = pcEquipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getParentIdPath().startsWith(calcId))
			{
				continue;
			}

			Equipment eqI = es.getItem();
			if (eqI == null)
			{
				continue;
			}

			Equipment eq = es.getItem();
			String aLoc = es.getName();
			String aNote = es.getNote();
			Float num = es.getQty();
			StringTokenizer aTok = new StringTokenizer(es.getIdPath(), ".");

			// if the eSet.getIdPath() is longer than 3
			// it's inside a container, don't try to equip
			if (aTok.countTokens() > 3)
			{
				eq.setLocation(Equipment.CONTAINED);
				eq.setIsEquipped(false);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			else if (aLoc.startsWith(Constants.S_CARRIED))
			{
				eq.setLocation(Equipment.CARRIED_NEITHER);
				eq.setIsEquipped(false);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			else if (aLoc.startsWith(Constants.S_NOTCARRIED))
			{
				eq.setLocation(Equipment.NOT_CARRIED);
				eq.setIsEquipped(false);
				eq.setNumberCarried(new Float(0));
				eq.setQty(num);
			}
			else if (eq.isWeapon())
			{
				if (aLoc.equals(Constants.S_PRIMARY) || aLoc.equals(Constants.S_NATURAL_PRIMARY))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_PRIMARY);
					eq.setIsEquipped(true);
				}
				else if (aLoc.startsWith(Constants.S_SECONDARY) || aLoc.equals(Constants.S_NATURAL_SECONDARY))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_SECONDARY);
					eq.setIsEquipped(true);
				}
				else if (aLoc.equals(Constants.S_BOTH))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_BOTH);
					eq.setIsEquipped(true);
				}
				else if (aLoc.equals(Constants.S_DOUBLE))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(2);
					eq.setLocation(Equipment.EQUIPPED_TWO_HANDS);
					eq.setIsEquipped(true);
				}
				else if (aLoc.equals(Constants.S_UNARMED))
				{
					eq.setLocation(Equipment.EQUIPPED_NEITHER);
					eq.setNumberEquipped(num.intValue());
				}
				else if (aLoc.equals(Constants.S_TWOWEAPONS))
				{
					if (num.doubleValue() < 2.0)
					{
						num = new Float(2.0);
					}
					es.setQty(num);
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(2);
					eq.setLocation(Equipment.EQUIPPED_TWO_HANDS);
					eq.setIsEquipped(true);
				}
			}
			else
			{
				eq.setLocation(Equipment.EQUIPPED_NEITHER);
				eq.setIsEquipped(true);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			if ((aNote != null) && (aNote.length() > 0))
			{
				eq.setNote(aNote);
			}

			addLocalEquipment(eq);
		}

		// loop through all equipment and make sure that
		// containers contents are updated
		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isContainer())
			{
				eq.updateContainerContentsString();
			}
			// also make sure the masterList output order is
			// preserved as this equipmentList is a modified
			// clone of the original
			Equipment anEquip = getEquipmentNamed(eq.getName());
			if (anEquip != null)
			{
				eq.setOutputIndex(anEquip.getOutputIndex());
			}
		}
		// if temporary bonuses, read the bonus equipList
		if (useTempBonuses)
		{
			for (Iterator e = getTempBonusItemList().iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment) e.next();
				// make sure that this EquipSet is the one
				// this temporary bonus item comes from
				// to make sure we keep them together
				Equipment anEquip = getEquipmentNamed(eq.getName(), getEquipmentList());
				if (anEquip != null)
				{
					eq.setQty(anEquip.getQty());
					eq.setNumberCarried(anEquip.getCarried());
					if (anEquip.isEquipped())
					{
						if (eq.isWeapon())
						{
							eq.setSlots(0);
							eq.setCost("0");
							eq.setWeight("0");
							eq.setLocation(anEquip.getLocation());
						}
						else
						{
							// replace the orig item
							// with the bonus item
							eq.setLocation(anEquip.getLocation());
							removeLocalEquipment(anEquip);
							anEquip.setIsEquipped(false);
							anEquip.setLocation(Equipment.NOT_CARRIED);
							anEquip.setNumberCarried(new Float(0));
						}
						eq.setIsEquipped(true);
						eq.setNumberEquipped(1);
					}
					else
					{
						eq.setCost("0");
						eq.setWeight("0");
						eq.setLocation(Equipment.EQUIPPED_TEMPBONUS);
						eq.setIsEquipped(false);
					}
					// Adding this type to be correctly treated by Merge
					eq.setTypeInfo("TEMPORARY");
					addLocalEquipment(eq);
				}
			}
		}

		// all done!
	}

	public void setCalcEquipSetId(String eqSetId)
	{
		calcEquipSetId = eqSetId;
	}

	public String getCalcEquipSetId()
	{
		if (equipSetList.isEmpty())
		{
			return calcEquipSetId;
		}
		if (getEquipSetByIdPath(calcEquipSetId) == null)
		{
			// PC does not have that equipset ID
			// so we need to find one they do have
			for (Iterator e = equipSetList.iterator(); e.hasNext();)
			{
				EquipSet eSet = (EquipSet) e.next();
				if (eSet.getParentIdPath().equals("0"))
				{
					calcEquipSetId = eSet.getIdPath();
					return calcEquipSetId;
				}
			}
		}
		return calcEquipSetId;
	}

	public List getEquipSet()
	{
		return equipSetList;
	}

	public EquipSet getEquipSetByName(String aName)
	{
		if (equipSetList.isEmpty())
		{
			return null;
		}
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet eSet = (EquipSet) e.next();
			if (eSet.getName().equals(aName))
			{
				return eSet;
			}
		}
		return null;
	}

	public EquipSet getEquipSetByIdPath(String id)
	{

		if (equipSetList.isEmpty())
		{
			return null;
		}
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet eSet = (EquipSet) e.next();
			if (eSet.getIdPath().equals(id))
			{
				return eSet;
			}
		}
		return null;
	}

	public void setCurrentEquipSetName(String aName)
	{
		currentEquipSetName = aName;
	}

	public String getCurrentEquipSetName()
	{
		return currentEquipSetName;
	}

	public void addEquipSet(EquipSet set)
	{
		equipSetList.add(set);
	}

	public boolean delEquipSet(EquipSet eSet)
	{
		if (equipSetList.isEmpty())
		{
			return false;
		}
		boolean found = false;
		final String pid = eSet.getIdPath();

		// first remove this EquipSet
		equipSetList.remove(eSet);

		// now find and remove all it's children
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().startsWith(pid))
			{
				e.remove();
				found = true;
			}
		}
		return found;
	}

	public void delEquipSetItem(Equipment eq)
	{
		if (equipSetList.isEmpty())
		{
			return;
		}
		final List tmpList = new ArrayList();
		// now find and remove equipment from all EquipSet's
		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			EquipSet es = (EquipSet) eSet.next();
			Equipment eqI = es.getItem();
			if ((eqI != null) && eq.equals(eqI))
			{
				tmpList.add(es);
			}
		}
		for (Iterator eSet = tmpList.iterator(); eSet.hasNext();)
		{
			EquipSet es = (EquipSet) eSet.next();
			delEquipSet(es);
		}
	}

	/**
	 * replaces oldItem with newItem in all EquipSets
	 **/
	public void updateEquipSetItem(Equipment oldItem, Equipment newItem)
	{
		if (equipSetList.isEmpty())
		{
			return;
		}
		final List tmpList = new ArrayList();

		// find all oldItem EquipSet's
		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			EquipSet es = (EquipSet) eSet.next();
			Equipment eqI = es.getItem();
			if ((eqI != null) && oldItem.equals(eqI))
			{
				tmpList.add(es);
			}
		}
		for (Iterator eSet = tmpList.iterator(); eSet.hasNext();)
		{
			EquipSet es = (EquipSet) eSet.next();
			es.setValue(newItem.getName());
			es.setItem(newItem);
		}
	}

	/**
	 * gets the total weight in an EquipSet
	 **/
	public String getEquipSetWeight(String idPath)
	{
		if (equipSetList.isEmpty())
		{
			return "";
		}
		double totalWeight = 0;
		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			EquipSet es = (EquipSet) eSet.next();
			if (!es.getIdPath().startsWith(idPath))
			{
				continue;
			}
			Equipment eqI = es.getItem();
			if (eqI != null)
			{
				if ((eqI.getCarried().floatValue() > 0.0f) && (eqI.getParent() == null))
				{
					if (eqI.getChildCount() > 0)
					{
						totalWeight += eqI.getWeightAsDouble() + eqI.getContainedWeight().floatValue();
					}
					else
					{
						totalWeight += eqI.getWeightAsDouble() * eqI.getCarried().floatValue();
					}
				}
			}
		}
		BigDecimal d = new BigDecimal(String.valueOf(totalWeight));
		return BigDecimalHelper.formatBigDecimal(d, 2).toString();
	}

	public void setCalcFollowerBonus()
	{
		List aList = getFollowerList();
		if (aList.isEmpty())
		{
			return;
		}
		for (Iterator fm = aList.iterator(); fm.hasNext();)
		{
			Follower aF = (Follower) fm.next();
			final String rType = aF.getType().toUpperCase();
			final String rName = aF.getRace().toUpperCase();
			for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
			{
				CompanionMod aComp = (CompanionMod) cm.next();
				String aType = aComp.getType().toUpperCase();
				int iRace = aComp.getLevel(rName);
				if (aType.equals(rType) && (iRace == 1))
				{
					// Found race and type of follower
					// so add bonus to the master
					companionModList.add(aComp);
					aComp.activateBonuses();
				}
			}
		}
	}

	public List getFollowerList()
	{
		return followerList;
	}

	public void addFollower(Follower aFollower)
	{
		followerList.add(aFollower);
	}

	public void delFollower(Follower aFollower)
	{
		followerList.remove(aFollower);
	}

	/**
	 * Get the Follower object that is the "master" for this object
	 **/
	public Follower getMaster()
	{
		return followerMaster;
	}

	/**
	 * Get the PlayerCharacter that is the "master" for this object
	 **/
	public PlayerCharacter getMasterPC()
	{
		if (followerMaster == null)
		{
			return null;
		}
		for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
		{
			PlayerCharacter nPC = (PlayerCharacter) p.next();
			if (followerMaster.getFileName().equals(nPC.getFileName()))
			{
				return nPC;
			}
		}
		// could not find a filename match, let's try the Name
		for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
		{
			PlayerCharacter nPC = (PlayerCharacter) p.next();
			if (followerMaster.getName().equals(nPC.getName()))
			{
				return nPC;
			}
		}
		// no Name and no FileName match, so must not be loaded
		return null;
	}

	/**
	 * Set the master for this object
	 * also set the level dependent stats based on the masters level
	 * and info contained in the companionModList Array
	 * such as HitDie, SR, BONUS, SA, etc
	 **/
	public void setMaster(Follower aM)
	{
		followerMaster = aM;
		final PlayerCharacter mPC = getMasterPC();
		if (mPC == null)
		{
			return;
		}

		// make sure masters Name and fileName are correct
		if (!aM.getFileName().equals(mPC.getFileName()))
		{
			aM.setFileName(mPC.getFileName());
			setDirty(true);
		}
		if (!aM.getName().equals(mPC.getName()))
		{
			aM.setName(mPC.getName());
			setDirty(true);
		}

		//
		// Get total wizard + sorcer levels as they stack like a mother
		// Doh!!
		int mTotalLevel = 0;
		int addHD = 0;
		for (Iterator c = mPC.getClassList().iterator(); c.hasNext();)
		{
			PCClass mClass = (PCClass) c.next();
			boolean found = false;
			for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
			{
				CompanionMod aComp = (CompanionMod) cm.next();
				String aType = aComp.getType().toUpperCase();
				if (!(aType.equalsIgnoreCase(aM.getType())))
				{
					continue;
				}
				if ((aComp.getLevel(mClass.getName()) > 0) && !found)
				{
					mTotalLevel += mClass.getLevel();
					found = true;
				}
			}
		}

		// Clear the companionModList so we can add everything to it
		companionModList.clear();

		String newRaceType = "";
		final String oldRaceType = race.getType();

		// New way of doing this. Through VARs on the Master
		for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
		{
			CompanionMod aComp = (CompanionMod) cm.next();
			String aType = aComp.getType().toUpperCase();
			if (!(aType.equalsIgnoreCase(aM.getType())))
			{
				continue;
			}
			for (Iterator iType = aComp.getVarMap().keySet().iterator(); iType.hasNext();)
			{
				final String varName = (String) iType.next();
				if (mPC.getVariableValue(varName, "").intValue() >= aComp.getLevel(varName))
				{
					if (!companionModList.contains(aComp))
					{
						companionModList.add(aComp);
						aComp.activateBonuses();
						addHD += aComp.getHitDie();
						// if necessary, switch
						// the race type
						if (aComp.getCompanionSwitch(oldRaceType) != null)
						{
							newRaceType = aComp.getCompanionSwitch(oldRaceType);
						}
					}
				}
			}
		}

		// Old way of doing this. Through Class levels
		for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
		{
			CompanionMod aComp = (CompanionMod) cm.next();
			String aType = aComp.getType().toUpperCase();

			// This CompanionMod must be for this type of follower
			if (!(aType.equalsIgnoreCase(aM.getType())))
			{
				continue;
			}

			// Check all the masters classes
			for (Iterator c = mPC.getClassList().iterator(); c.hasNext();)
			{
				PCClass mClass = (PCClass) c.next();
				int mLev = mClass.getLevel();
				int compLev = aComp.getLevel(mClass.getName());

				if (compLev < 0)
				{
					continue;
				}

				// This CompanionMod must be for this Class
				// and for the correct level or lower
				if (compLev <= mLev || compLev <= mTotalLevel)
				{
					if (!companionModList.contains(aComp))
					{
						companionModList.add(aComp);
						aComp.activateBonuses();
						addHD += aComp.getHitDie();
						// if necessary, switch
						// the race type
						if (aComp.getCompanionSwitch(oldRaceType) != null)
						{
							newRaceType = aComp.getCompanionSwitch(oldRaceType);
						}
					}
				}
			}
		}

		PCClass newClass;

		if ((newRaceType != null) && (newRaceType.length() > 0))
		{
			newClass = Globals.getClassNamed(newRaceType);
			race.setTypeInfo(".CLEAR." + newRaceType);
			setDirty(true);
			// we now have to swap all the old "Race" levels
			final PCClass oldClass = getClassNamed(oldRaceType);
			int oldLevel = 0;
			if (oldClass != null)
			{
				oldLevel = oldClass.getLevel();
			}
			if ((oldLevel > 0) && (newClass != null))
			{
				// turn oldLevel negative
				final int negLevel = oldLevel * -1;
				// yes, it's weird that incrementClassLevel
				// can be called with a negative value
				incrementClassLevel(negLevel, oldClass, true);
				// now add levels back in the new class
				incrementClassLevel(oldLevel, newClass, true);
			}
		}
		//
		// Add additional HD if required
		newClass = Globals.getClassNamed(race.getType());
		final int usedHD = followerMaster.getUsedHD();
		addHD -= usedHD;
		if ((newClass != null) && (addHD > 0))
		{
			// set the new HD (but only do it once!)
			incrementClassLevel(addHD, newClass, true);
			followerMaster.setUsedHD(addHD);
			setDirty(true);
		}
		// If it's a familiar, we need to change it's Skills
		if (getUseMasterSkill())
		{
			final List mList = mPC.getSkillList();
			final List sNameList = new ArrayList();
			// now we have to merge the two lists together and
			// take the higher rank of each skill for the Familiar
			for (Iterator a = getAllSkillList(true).iterator(); a.hasNext();)
			{
				Skill fSkill = (Skill) a.next();
				for (Iterator b = mList.iterator(); b.hasNext();)
				{
					Skill mSkill = (Skill) b.next();
					// first check to see if familiar
					// already has ranks in the skill
					if (mSkill.getName().equals(fSkill.getName()))
					{
						// need higher rank of the two
						if (mSkill.getRank().intValue() > fSkill.getRank().intValue())
						{
							// first zero current
							fSkill.setZeroRanks(newClass);
							fSkill.modRanks(mSkill.getRank().doubleValue(), newClass, true);
						}
					}
					// build a list of all skills a master
					// posesses, but the familiar does not
					if (!hasSkill(mSkill.getName()) && !sNameList.contains(mSkill.getName()))
					{
						sNameList.add(mSkill.getName());
					}
				}
			}
			// now add all the skills only the master has
			for (Iterator sn = sNameList.iterator(); sn.hasNext();)
			{
				String skillName = (String) sn.next();
				// familiar doesn't have skill,
				// but master does, so add it
				Skill newSkill = (Skill) Globals.getSkillNamed(skillName).clone();
				double sr = mPC.getSkillNamed(skillName).getRank().doubleValue();
				if (newSkill.getChoiceString() != null && newSkill.getChoiceString().length() > 0)
				{
					continue;
				}
				newSkill.modRanks(sr, newClass, true);
				getSkillList().add(newSkill);
			}
		}
	}

	public ArrayList getNotesList()
	{
		return notesList;
	}

	public void addNotesItem(NoteItem item)
	{
		notesList.add(item);
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up subrace
	 *
	 * @return character subrace
	 */
	public String getSubRace()
	{
		String subRace = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
			final String tempSubRace = template.getSubRace();
			if (!tempSubRace.equals(Constants.s_NONE))
			{
				subRace = tempSubRace;
			}
		}
		return subRace;
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up region
	 *
	 * @return character region
	 */

	public String getRegion()
	{
		return getRegion(true);
	}

	public String getRegion(boolean useTemplates)
	{
		if (region != null || !useTemplates)
		{
			return region; // character's region trumps any from templates
		}
		String r = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
			final String tempRegion = template.getRegion();
			if (!tempRegion.equals(Constants.s_NONE))
			{
				r = tempRegion;
			}
		}
		return r;
	}

	public void setRegion(String arg)
	{
		region = arg;
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up sub region
	 *
	 * @return character sub region
	 */

	public String getSubRegion()
	{
		return getSubRegion(true);
	}

	private String getSubRegion(boolean useTemplates)
	{
		if (subRegion != null || !useTemplates)
		{
			return subRegion; // character's subregion trumps any from templates
		}
		String s = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
			final String tempSubRegion = template.getSubRegion();
			if (!tempSubRegion.equals(Constants.s_NONE))
			{
				s = tempSubRegion;
			}
		}
		return s;
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up region
	 *
	 * @return character region
	 */
	public String getFullRegion()
	{
		final String sub = getSubRegion();
		final StringBuffer tempRegName = new StringBuffer().append(getRegion());

		if (!sub.equals(Constants.s_NONE))
		{
			tempRegName.append(" (").append(sub).append(')');
		}
		return tempRegName.toString();
	}

	/**
	 * Selector
	 * Gets the path to the portrait of the character.
	 *
	 * @return the path to the portrait file
	 */
	public String getPortraitPath()
	{
		return portraitPath;
	}

	/**
	 * Selector
	 * Sets the path to the portrait of the character.
	 *
	 * @param newPortraitPath   the path to the portrait file
	 */
	public void setPortraitPath(String newPortraitPath)
	{
		portraitPath = newPortraitPath;
		setDirty(true);
	}

	/** Gets the filename of the character. */
	public String getFileName()
	{
		return fileName;
	}

	/** Sets the filename of the character. */
	public void setFileName(String newFileName)
	{
		fileName = newFileName;
	}

	/** Gets whether the character has been changed since last saved. */
	public boolean isDirty()
	{
		return dirtyFlag;
	}

	/** Sets the character changed since last save. */
	public void setDirty(boolean dirtyState)
	{
		if (dirtyFlag != dirtyState)
		{
			dirtyFlag = dirtyState;
			// Some items enable/disable depending on PC dirtiness
			PCGen_Frame1.enableDisableMenuItems();
		}
	}

	/** Gets whether the character has been changed since last saved. */
	public boolean wasEverSaved()
	{
		return !"".equals(getFileName());
	}

	/** @return true if character is currently being read from file. */
	boolean isImporting()
	{
		return importing;
	}

	public void setImporting(boolean newIsImporting)
	{
		importing = newIsImporting;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String aString)
	{
		name = aString;
		setDirty(true);
	}

	private String getDisplayRaceName()
	{
		final String raceName = getRace().toString();
		return (raceName.equals(Constants.s_NONESELECTED) ? "Nothing" : raceName);
	}

	private String getDisplayClassName()
	{
		return (classList.isEmpty() ? "Nobody" : ((PCClass) classList.get(classList.size() - 1)).getDisplayClassName());
	}

	private String getFullDisplayClassName()
	{
		if (classList.isEmpty())
		{
			return "Nobody";
		}

		StringBuffer buf = new StringBuffer();
		Iterator it = classList.iterator();

		buf.append(((PCClass) it.next()).getFullDisplayClassName());

		while (it.hasNext())
		{
			buf.append("/").append(((PCClass) it.next()).getFullDisplayClassName());
		}

		return buf.toString();
	}

	public String getDisplayName()
	{
		final String custom = getTabName();

		if (!"".equals(custom))
		{
			return custom;
		}

		final StringBuffer displayName = new StringBuffer().append(getName());

		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				break;
			case Constants.DISPLAY_STYLE_NAME_CLASS:
				displayName.append(" the ").append(getDisplayClassName());
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE:
				displayName.append(" the ").append(getDisplayRaceName());
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				displayName.append(" the ").append(getDisplayRaceName()).append(' ').append(getDisplayClassName());
				break;
			case Constants.DISPLAY_STYLE_NAME_FULL:
				return getFullDisplayName();
			default:
				break; // custom broken
		}

		return displayName.toString();
	}

	private String getOrdinal(int cardinal)
	{
		switch (cardinal)
		{
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	public String getFullDisplayName()
	{
		int levels = getTotalLevels();

		// If you aren't multi-classed, don't display redundant class level information in addition to the total PC level
		return new StringBuffer().append(getName()).append(" the ").append(levels).append(getOrdinal(levels)).append(" level ").append(getDisplayRaceName()).append(' ').append(classList.size() < 2 ? getDisplayClassName() : getFullDisplayClassName()).toString();
	}

	public String getTabName()
	{
		return tabName;
	}

	public void setTabName(String aString)
	{
		tabName = aString;
		setDirty(true);
		PCGen_Frame1.forceUpdate_PlayerTabs();
	}

	public String getPlayersName()
	{
		return playersName;
	}

	public void setPlayersName(String aString)
	{
		playersName = aString;
		setDirty(true);
	}

	public int getSkillPoints()
	{
		int returnValue = 0;
		// First compute gained points, and then remove the already spent ones.
		// We can't use Remaining points because the level may be removed, and then we have
		// to display this as -x on the "Total Skill Points" field
		for (Iterator e = getLevelInfo().iterator(); e.hasNext(); )
		{
			PCLevelInfo pcli = (PCLevelInfo) e.next();
			returnValue += pcli.getSkillPointsGained();
		}
		for (Iterator e = getSkillList().iterator(); e.hasNext(); )
		{
			Skill aSkill = (Skill) e.next();
			for (int idx = 0; idx < aSkill.getRankList().size(); idx++)
			{
				String bSkill = (String) aSkill.getRankList().get(idx);
				final int iOffs = bSkill.indexOf(':');
				final double curRank = Double.parseDouble(bSkill.substring(iOffs + 1));
				final double cost = (double) aSkill.costForPCClass(getClassKeyed(bSkill.substring(0,iOffs))).intValue();
				returnValue -= (int) (cost * curRank);
			}
		}
		
		return returnValue;
	}

	public void setSkillPoints(int anInt)
	{
		skillPoints = anInt;
		setDirty(true);
	}

	/**
	 * 0-level feat count (racial, templates, etc.), excluding any
	 * feats from leveling.
	 *
	 * @return count of initial, non-leveling feats
	 */
	public double getInitialFeats()
	{
		double initFeats = (double) getRace().getBonusInitialFeats();
		final List aList = getTemplateList();

		if (!aList.isEmpty() && canReassignTemplateFeats())
		{
			for (Iterator e = aList.iterator(); e.hasNext();)
			{
				final PCTemplate template = (PCTemplate) e.next();

				if (template != null)
				{
					initFeats += (double) template.getBonusInitialFeats();
				}
			}
		}
		return initFeats;
	}

	public double getFeats()
	{
		return feats;
	}

	public void setFeats(double arg)
	{
		feats = arg;
		setDirty(true);
	}

	public int getPoolAmount()
	{
		return poolAmount;
	}

	public void setPoolAmount(int anInt)
	{
		poolAmount = anInt;
		setDirty(true);
	}

	public int getCostPool()
	{
		return costPool;
	}

	public void setCostPool(int i)
	{
		costPool = i;
		setDirty(true);
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int i)
	{
		age = i;
		setDirty(true);
		calcActiveBonuses();
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int i)
	{
		height = i;
		setDirty(true);
	}

	public int getWeight()
	{
		return weight;
	}

	public void setWeight(int i)
	{
		weight = i;
		setDirty(true);
	}

	private String findTemplateGender()
	{
		String templateGender = Constants.s_NONE;
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			String aString = aTemplate.getGenderLock();
			if (!aString.equals(Constants.s_NONE))
			{
				templateGender = aString;
			}
		}
		return templateGender;
	}

	public String getGender()
	{
		final String tGender = findTemplateGender();

		return tGender.equals(Constants.s_NONE) ? gender : tGender;
	}

	public void setGender(String argGender)
	{
		final String templateGender = findTemplateGender();
		if (templateGender.equals(Constants.s_NONE))
		{
			gender = argGender;
		}
		else
		{
			gender = templateGender;
		}
		setDirty(true);
	}

	public List getSpecialAbilityList()
	{
		// aList will contain a list of SpecialAbility objects
		List aList = (ArrayList) specialAbilityList.clone();
		if (race != null)
		{
			aList = race.addSpecialAbilitiesToList(aList);
		}
		if (deity != null)
		{
			aList = deity.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			aList = aClass.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			aList = aFeat.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			aList = aSkill.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				aList = aCD.getDomain().addSpecialAbilitiesToList(aList);
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				aList = eq.addSpecialAbilitiesToList(aList);

				List bList = eq.getEqModifierList(true);
				if (!bList.isEmpty())
				{
					for (Iterator e2 = bList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						aList = eqMod.addSpecialAbilitiesToList(aList);
					}
				}
				bList = eq.getEqModifierList(false);
				if (!bList.isEmpty())
				{
					for (Iterator e2 = bList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						aList = eqMod.addSpecialAbilitiesToList(aList);
					}
				}
			}
		}
		final int atl = getTotalLevels();
		final int thd = totalHitDice();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			final PCTemplate bTemplate = Globals.getTemplateNamed(aTemplate.getName());
			aList = bTemplate.addSpecialAbilitiesToList(aList, atl, thd);
		}
		for (Iterator i = companionModList.iterator(); i.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) i.next();
			aList = cMod.addSpecialAbilitiesToList(aList);
		}
		Collections.sort(aList);
		return aList;
	}

	public ArrayList getSpecialAbilityListStrings()
	{
		final List aList = getSpecialAbilityList();
		final ArrayList bList = new ArrayList();
		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final Object obj = i.next();
				final SpecialAbility sa = (SpecialAbility) obj;
				if (sa.getSADesc() == null || "".equals(sa.getSADesc()))
				{
					bList.add(sa.getName());
				}
				else
				{
					bList.add(sa.getName() + " (" + sa.getSADesc() + ")");
				}
			}
		}
		return bList;
	}

	/**
	 * same as getSpecialAbilityList except if
	 * if you have the same ability twice, it only
	 * lists it once with (2) at the end.
	 **/
	public ArrayList getSpecialAbilityTimesList()
	{
		final ArrayList aList = new ArrayList();
		final ArrayList abilityList = getSpecialAbilityListStrings();
		final int[] times = new int[abilityList.size()];
		Arrays.fill(times, 0);
		if (!abilityList.isEmpty())
		{
			for (Iterator i = abilityList.iterator(); i.hasNext();)
			{
				String aString = (String) i.next();
				boolean found = false;
				int idx = 0;
				for (Iterator ii = aList.iterator(); ii.hasNext();)
				{
					if (aString.equals(ii.next()))
					{
						found = true;
						break;
					}
					++idx;
				}
				if (!found)
				{
					aList.add(aString);
				}
				times[idx] += 1;
			}
		}

		//
		// Get values for all variables
		//
		// Expected format(s):
		// blah
		// blah %blah|var1
		// blah %blah%blah|var1|var2
		//
		for (int i = 0, x = aList.size(); i < x; ++i)
		{
			StringTokenizer varTok = new StringTokenizer((String) aList.get(i), "|");
			if (!varTok.hasMoreTokens())
			{
				continue;
			}
			String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();
			boolean isZero = false;
			if (varCount != 0)
			{
				isZero = true;
				varValue = new int[varCount];
				for (int j = 0; j < varCount; ++j)
				{
					final String vString = varTok.nextToken();
					varValue[j] = getVariable(vString, true, true, "", "").intValue();
					if (varValue[j] != 0)
					{
						isZero = false;
					}
				}
			}

			StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;
			while (varTok.hasMoreTokens())
			{
				final String nextTok = varTok.nextToken();
				if ("%".equals(nextTok))
				{
					if ((varValue != null) && (varCount < varValue.length))
					{
						newAbility.append(varValue[varCount++]);
					}
					else
					{
						newAbility.append('%');
					}
				}
				else
				{
					newAbility.append(nextTok);
				}
			}
			if (isZero)
			{
				times[i] = 0;
			}
			//
			// Is there a good reason to display this other than for debugging purposes?
			// -Byngl Dec 28, 2002
			//
			//if (times[i] > 1)
			//{
			//newAbility.append(" (").append(times[i]).append(')');
			//}
			aList.set(i, newAbility.toString());
		}


		//
		// Remove any abilities whose occurance is 0 after calculating expression
		//
		for (int i = aList.size() - 1; i >= 0; --i)
		{
			if (times[i] == 0)
			{
				aList.remove(i);
			}
		}
		return aList;
	}

	public String getHanded()
	{
		return handed;
	}

	public void setHanded(String aString)
	{
		handed = aString;
		setDirty(true);
	}

	public TreeSet getLanguagesList()
	{
		return languages;
	}

	public String getLanguagesListNames()
	{
		final TreeSet aSet = getLanguagesList();
		StringBuffer b = new StringBuffer();
		for (Iterator i = aSet.iterator(); i.hasNext();)
		{
			if (b.length() > 0)
			{
				b.append(", ");
			}
			b.append(i.next().toString());
		}
		return b.toString();
	}

	public String getEyeColor()
	{
		return eyeColor;
	}

	public void setEyeColor(String aString)
	{
		eyeColor = aString;
		setDirty(true);
	}

	public String getSkinColor()
	{
		return skinColor;
	}

	public void setSkinColor(String aString)
	{
		skinColor = aString;
		setDirty(true);
	}

	public String getHairColor()
	{
		return hairColor;
	}

	public void setHairColor(String aString)
	{
		hairColor = aString;
		setDirty(true);
	}

	public String getHairStyle()
	{
		return hairStyle;
	}

	public void setHairStyle(String aString)
	{
		hairStyle = aString;
		setDirty(true);
	}

	public String getSpeechTendency()
	{
		return speechTendency;
	}

	public void setSpeechTendency(String aString)
	{
		speechTendency = aString;
		setDirty(true);
	}

	public String getPhobias()
	{
		return phobias;
	}

	public void setPhobias(String aString)
	{
		phobias = aString;
		setDirty(true);
	}

	public String getInterests()
	{
		return interests;
	}

	public void setInterests(String aString)
	{
		interests = aString;
		setDirty(true);
	}

	public String getCatchPhrase()
	{
		return catchPhrase;
	}

	public void setCatchPhrase(String aString)
	{
		catchPhrase = aString;
		setDirty(true);
	}

	public String getTrait1()
	{
		return trait1;
	}

	public void setTrait1(String aString)
	{
		trait1 = aString;
		setDirty(true);
	}

	public String getTrait2()
	{
		return trait2;
	}

	public void setTrait2(String aString)
	{
		trait2 = aString;
		setDirty(true);
	}

	public String getResidence()
	{
		return residence;
	}

	public void setResidence(String aString)
	{
		residence = aString;
		setDirty(true);
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String aString)
	{
		location = aString;
		setDirty(true);
	}

	public String getBirthplace()
	{
		return birthplace;
	}

	public void setBirthplace(String aString)
	{
		birthplace = aString;
		setDirty(true);
	}

	public BigDecimal getGold()
	{
		return gold;
	}

	public void setGold(String aString)
	{
		gold = new BigDecimal(aString);
		setDirty(true);
	}

	public String getBio()
	{
		return bio;
	}

	public void setBio(String aString)
	{
		bio = aString;
		setDirty(true);
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String aString)
	{
		description = aString;
		setDirty(true);
	}

	/**
	 * Selector
	 *
	 * @return description lst
	 */
	public String getDescriptionLst()
	{
		return descriptionLst;
	}

	/**
	 * Returns the number of experience points needed for level
	 *
	 * @param  level  character level to calculate experience for
	 * @return	  The experience points needed
	 */
	private static int minXPForLevel(int level)
	{
		List levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo) levelInfo.get(level - 1)).getMinXP();
		}
		else
		{
			// do something sensible if no level info
			return 0;
		}
	}

	private int getLAXP()
	{
		// Why +1?  Adjustments are deltas, not absolute
		// levels, so are not subject to the "back off one"
		// element of the * algorightm in minXPForLevel.  This
		// still means that levelAdjustment of 0 gives you 0
		// XP, but we need LA of 1 to give us 1,000 XP.
		return minXPForLevel(getLevelAdjustment() + 1);
	}

	public int getXP()
	{
		// Add the effect of LEVELADJ when
		// showing our external notion of XP.
		return earnedXP + getLAXP();
	}

	private void setEarnedXP(int argEarnedXP)
	{
		earnedXP = argEarnedXP;
		setDirty(true);
	}

	public void setXP(int xp)
	{
		// Remove the effect of LEVELADJ when storing our
		// internal notion of experiene
		int realXP = xp - getLAXP();
		if (realXP < 0)
		{
			Logging.errorPrint("ERROR: too little experience: " + realXP);
			realXP = 0;
		}
		setEarnedXP(realXP);
	}

	/**
	 * Returns the maximum number of ranks a character can
	 * have in a class skill at the specified level.
	 *
	 * Should this be moved to PCClass?
	 *
	 *
	 * @param  level  character level to get max skill ranks for
	 * @return	  The maximum allowed skill ranks
	 */
	public static BigDecimal maxClassSkillForLevel(int level)
	{
		final List levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo) levelInfo.get(level - 1)).getMaxClassSkillRanks();
		}
		else
		{
			// do something sensible if no level info
			return BigDecimalHelper.ZERO;
		}
	}

	/**
	 * Returns the maximum number of ranks a character can

	 * have in a cross-class skill at the specified level.
	 *
	 * @param  level  character level to get max skill ranks for
	 * @return	  The maximum allowed skill ranks
	 */
	public static BigDecimal maxCrossClassSkillForLevel(int level)
	{
		final List levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo) levelInfo.get(level - 1)).getMaxCrossClassSkillRanks();
		}
		else
		{
			// do something sensible if no level info
			return BigDecimalHelper.ZERO;
		}
	}

	public ArrayList getMiscList()
	{
		return miscList;
	}

	public List getSpellBooks()
	{
		return spellBooks;
	}

	public StatList getStatList()
	{
		return statList;
	}

	public Race getRace()
	{
		return race;
	}

	public ArrayList getTemplateList()
	{
		return templateList;
	}

	public ArrayList getClassList()
	{
		return classList;
	}

	public ArrayList getFeatList()
	{
		return featList;
	}

	public double getUsedFeatCount()
	{
		double iCount = 0;
		if (!featList.isEmpty())
		{
			for (Iterator e = featList.iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat) e.next();
				//
				// Don't increment the count for
				// hidden feats so the number
				// displayed matches this number
				//
				//if (aFeat.isVisible() == Feat.VISIBILITY_HIDDEN)
				//{
				//	continue;
				//}
				final int subfeatCount = aFeat.getAssociatedCount();
				if (subfeatCount > 1)
				{
					iCount += subfeatCount;
				}
				else
				{
					iCount += aFeat.getCost();
				}
			}
		}
		return iCount;
	}

	public ArrayList getSkillList()
	{
		return getAllSkillList(false);
	}

	/**
	 *if checkBonus is true, then search for all skills with a SKILLRANK bonus
	 * to include in list as well
	 * @param checkBonus
	 * @return
	 */
	public ArrayList getAllSkillList(boolean checkBonus)
	{
		if (!checkBonus)
		{
			return skillList;
		}
		for (Iterator i = Globals.getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			if (!hasSkill(aSkill.getName()))
			{
				if (!Utility.doublesEqual(getTotalBonusTo("SKILLRANK", aSkill.getName()), 0.0))
				{
					addSkill(aSkill);
				}
			}
		}
		return skillList;
	}

	/**
	 * Retrieves a list of the character's skills in output order. This is in
	 * ascending order of the skill's outputIndex field. If skills have the
	 * same outputIndex they will be ordered by name. Note hidden skills
	 * (outputIndex = -1) are not included in this list.
	 *
	 * @return An ArrayList of the skill objects in output order.
	 */
	public ArrayList getSkillListInOutputOrder()
	{
		final ArrayList sortedList = (ArrayList) getSkillList().clone();
		Collections.sort(sortedList, new Comparator()
		{
			/**
			 *  Comparator will be specific to Skill objects
			 */
			public int compare(Object obj1, Object obj2)
			{
				int obj1Index = ((Skill) obj1).getOutputIndex();
				int obj2Index = ((Skill) obj2).getOutputIndex();
				// Force unset items (index of 0) to appear at the end
				if (obj1Index == 0)
				{
					obj1Index = 999;
				}
				if (obj2Index == 0)
				{
					obj2Index = 999;
				}

				if (obj1Index > obj2Index)
				{
					return 1;
				}
				else if (obj1Index < obj2Index)
				{
					return -1;
				}
				else
				{
					return ((Skill) obj1).getName().compareToIgnoreCase(((Skill) obj2).getName());
				}
			}
		});

		// Remove the hidden skills from the list
		for (Iterator i = sortedList.iterator(); i.hasNext();)
		{
			final Skill bSkill = (Skill) i.next();
			if (bSkill.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	public Deity getDeity()
	{
		return deity;
	}

	///////////////////////////////////////
	//operations

	public PlayerCharacter()
	{
		Globals.setCurrentPC(this);
		for (int i = 0, x = Globals.s_ATTRIBLONG.length; i < x; ++i)
		{
			statList.getStats().add(((PCStat) SystemCollections.getUnmodifiableStatList().get(i)).clone());
		}
		setRace((Race) Globals.getRaceMap().get(Constants.s_NONESELECTED));
		setName("");
		skillPoints = 0;
		feats = 0;
		rollStats(SettingsHandler.getRollMethod());
		miscList.add("");
		miscList.add("");
		miscList.add("");
		addSpellBook(Globals.getDefaultSpellBook());
		addSpellBook("Innate");
		populateSkills(SettingsHandler.getIncludeSkills());
	}

	public String getCritterType()
	{
		final StringBuffer critterType = new StringBuffer();

		//Not too sure about this if, but that's what the previous code implied...
		if (race != null)
		{
			critterType.append(race.getType());
		}
		else
		{
			critterType.append("Humanoid");
		}

		if (!templateList.isEmpty())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				PCTemplate aTemplate = (PCTemplate) e.next();
				String aType = aTemplate.getType();

				if (!"".equals(aType))
				{
					critterType.append('|').append(aType);
				}
			}
		}

		return critterType.toString();
	}

	public String getSize()
	{
		final SizeAdjustment sa = getSizeAdjustment();
		if (sa != null)
		{
			return sa.getAbbreviation();
		}
		return " ";
	}

	private SizeAdjustment getSizeAdjustment()
	{
		SizeAdjustment sa = SystemCollections.getSizeAdjustmentAtIndex(sizeInt());
		return sa;
	}

	int racialSizeInt()
	{
		int iSize = 0;
		if (race != null)
		{
			// get the base size for the race
			iSize = Globals.sizeInt(race.getSize());

			// now check and see if a template has set the
			// size of the character in question
			// with something like SIZE:L
			if (!templateList.isEmpty())
			{
				for (Iterator e = getTemplateList().iterator(); e.hasNext();)
				{
					final PCTemplate template = (PCTemplate) e.next();
					final String templateSize = template.getTemplateSize();
					if (templateSize.length() != 0)
					{
						iSize = Globals.sizeInt(templateSize);
					}
				}
			}
		}
		return iSize;
	}

	public int sizeInt()
	{
		int iSize = racialSizeInt();
		if (race != null)
		{
			// Now check and see if a class has modified
			// the size of the character with something like:
			// BONUS:SIZEMOD|NUMBER|+1
			iSize += (int) getTotalBonusTo("SIZEMOD", "NUMBER");

			// Now see if there is a HD advancement in size
			// (Such as for Dragons)
			for (int i = 0; i < race.sizesAdvanced(totalHitDice()); ++i)
			{
				++iSize;
			}

			//
			// Must still be between 0 and 8
			//
			if (iSize < 0)
			{
				iSize = 0;
			}
			if (iSize >= SystemCollections.getSizeAdjustmentListSize())
			{
				iSize = SystemCollections.getSizeAdjustmentListSize() - 1;
			}
		}
		return iSize;
	}

	public PCClass getClassNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			if (aClass.getName().equalsIgnoreCase(aString))
			{
				return aClass;
			}
		}
		return null;
	}

	private PCClass getClassDisplayNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			if (aClass.getDisplayClassName().equalsIgnoreCase(aString))
			{
				return aClass;
			}
		}
		return null;
	}

	public PCClass getClassKeyed(String aString)
	{
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PCClass aClass = (PCClass) classIter.next();
			if (aClass.getKeyName().equalsIgnoreCase(aString))
			{
				return aClass;
			}
		}
		return null;
	}

	/**
	 * Get the template named aName from this PC
	 * @return	PC template or null if not found
	 **/
	public PCTemplate getTemplateNamed(String aName)
	{
		for (Iterator ti = templateList.iterator(); ti.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) ti.next();
			if (aTemplate.getName().equalsIgnoreCase(aName))
			{
				return aTemplate;
			}
		}
		return null;
	}

	/**
	 * a temporary placeholder used for computing the DC of a spell
	 * Set from within Spell.java before the getVariableValue() call
	 **/
	public void setSpellLevelTemp(int i)
	{
		spellLevelTemp = i;
	}

	public int getSpellLevelTemp()
	{
		return spellLevelTemp;
	}

	/**
	 * Returns the Spell Stat bonus for a class
	 **/
	public int getBaseSpellStatBonus(PCClass aClass)
	{
		if (aClass == null)
		{
			return 0;
		}
		int baseSpellStat = 0;
		String statString = aClass.getSpellBaseStat();
		if (!statString.equals(Constants.s_NONE))
		{
			baseSpellStat = getStatList().getStatModFor(statString);
			baseSpellStat += (int) getTotalBonusTo("STAT", "BASESPELLSTAT");
			baseSpellStat += (int) getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS." + aClass.getName());
			baseSpellStat += (int) getTotalBonusTo("STAT", "CAST." + statString);
		}
		return baseSpellStat;
	}

	public PObject getSpellClassAtIndex(int ix)
	{
		List aList = getSpellClassList();
		if (ix >= 0 && ix < aList.size())
		{
			return (PObject) aList.get(ix);
		}
		return null;
	}

	private List getSpellClassList()
	{
		List aList = new ArrayList();

		if (!race.getCharacterSpell(null, "", -1).isEmpty())
		{
			aList.add(race);
		}
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PObject aObject = (PObject) classIter.next();
			if (!aObject.getCharacterSpell(null, "", -1).isEmpty())
			{
				aList.add(aObject);
			}
			else if (aObject instanceof PCClass)
			{
				if (!((PCClass) aObject).getSpellType().equalsIgnoreCase("None"))
				{
					aList.add(aObject);
				}
			}
		}
		return aList;
	}

	private int getSpellClassCount()
	{
		return getSpellClassList().size();
	}

	void addVariable(String variableString)
	{
		variableList.add(variableString);
	}

	void removeVariable(String variableString)
	{
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			final String aString = (String) e.next();
			if (aString.startsWith(variableString))
			{
				e.remove();
			}
		}
	}

	boolean hasVariable(String variableString)
	{
		if (!variableList.isEmpty())
		{
			for (Iterator e = variableList.iterator(); e.hasNext();)
			{
				final StringTokenizer aTok = new StringTokenizer((String) e.next(), "|");
				aTok.nextToken(); //src
				aTok.nextToken(); //subSrc
				if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
				{
					return true;
				}
			}
		}
		if (Globals.hasWeaponProfVariableNamed(weaponProfList, variableString))
		{
			return true;
		}
		return variableSet.contains(variableString.toUpperCase());
	}

	public void buildVariableSet()
	{
		variableSet.clear();

		// Go through all objects that could add a VAR
		// and build the HashSet

		if (!aggregateFeatList().isEmpty())
		{
			for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
			{
				final Feat obj = (Feat) e.next();
				variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
			}
		}

		if (!getSkillList().isEmpty())
		{
			for (Iterator e = getSkillList().iterator(); e.hasNext();)
			{
				final Skill obj = (Skill) e.next();
				variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
			}
		}

		if (!equipmentList.isEmpty())
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment obj = (Equipment) e.next();
				variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
				for (Iterator e2 = obj.getEqModifierIterator(true); e2.hasNext();)
				{
					final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
					variableSet.addAll(eqMod.getVariableNamesAsUnmodifiableSet());
				}
				for (Iterator e2 = obj.getEqModifierIterator(false); e2.hasNext();)
				{
					final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
					variableSet.addAll(eqMod.getVariableNamesAsUnmodifiableSet());
				}
			}
		}

		if (!templateList.isEmpty())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				final PCTemplate obj = (PCTemplate) e.next();
				variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
			}
		}

		if (!companionModList.isEmpty())
		{
			for (Iterator e = companionModList.iterator(); e.hasNext();)
			{
				final CompanionMod obj = (CompanionMod) e.next();
				variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
			}
		}

		if (deity != null)
		{
			variableSet.addAll(deity.getVariableNamesAsUnmodifiableSet());
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				final CharacterDomain cd = (CharacterDomain) e.next();
				variableSet.addAll(cd.getVariableNamesAsUnmodifiableSet());
			}
		}

		if (race != null)
		{
			variableSet.addAll(race.getVariableNamesAsUnmodifiableSet());
		}

		for (Iterator e = statList.getStats().iterator(); e.hasNext();)
		{
			final PCStat obj = (PCStat) e.next();
			variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
		}
		for (Iterator e = SystemCollections.getUnmodifiableAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment obj = (PCAlignment) e.next();
			variableSet.addAll(obj.getVariableNamesAsUnmodifiableSet());
		}
	}

	/**
	 * Should probably be refactored to return a String instead.
	 * @param variableString
	 * @param isMax
	 * @param includeBonus
	 * @param matchSrc
	 * @param matchSubSrc
	 * @return
	 */
	public Float getVariable(String variableString, boolean isMax, boolean includeBonus, String matchSrc, String matchSubSrc)
	{
		double value = 0.0;
		boolean found = false;
		if (lastVariable != null)
		{
			if (lastVariable.equals(variableString))
			{
				Logging.errorPrint("Avoiding infinite loop in getVariable: repeated lookup of \"" + lastVariable + "\"");
				return new Float(value);
			}
		}

		if (!variableList.isEmpty())
		{
			for (Iterator e = variableList.iterator(); e.hasNext();)
			{
				final String vString = (String) e.next();
				final StringTokenizer aTok = new StringTokenizer(vString, "|");
				final String src = aTok.nextToken();
				if (matchSrc.length() > 0 && !src.equals(matchSrc))
				{
					continue;
				}

				final String subSrc = aTok.nextToken();
				if (matchSubSrc.length() > 0 && !subSrc.equals(matchSubSrc))
				{
					continue;
				}

				final String nString = aTok.nextToken();
				if (nString.equalsIgnoreCase(variableString))
				{
					final String sString = aTok.nextToken();
					final Float newValue = getVariableValue(sString, src);
					if (!found)
					{
						value = newValue.doubleValue();
					}
					else if (isMax)
					{
						value = Math.max(value, newValue.doubleValue());
					}
					else
					{
						value = Math.min(value, newValue.doubleValue());
					}
					found = true;
					if (!"".equals(loopVariable))
					{
						while (loopValue > decrement)
						{
							loopValue -= decrement;
							value += getVariableValue(sString, src).doubleValue();
						}
						loopValue = 0;
						loopVariable = "";
					}
				}
			}
		}

		// Now check the feats to see if they modify the variable
		if (!aggregateFeatList().isEmpty())
		{
			for (Iterator oi = aggregateFeatList().iterator(); oi.hasNext();)
			{
				final Feat obj = (Feat) oi.next();
				final String varInList = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
				if (varInList.length() > 0)
				{
					found = true;
					value = Float.parseFloat(varInList);
				}
			}
		}

		if (!getSkillList().isEmpty())
		{
			for (Iterator oi = getSkillList().iterator(); oi.hasNext();)
			{
				final Skill obj = (Skill) oi.next();
				String varInList = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
				if (varInList.length() > 0)
				{
					found = true;
					value = Float.parseFloat(varInList);
				}
			}
		}

		if (!equipmentList.isEmpty())
		{
			for (Iterator oi = equipmentList.iterator(); oi.hasNext();)
			{
				final Equipment obj = (Equipment) oi.next();
				String eS = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
				if (eS.length() > 0)
				{
					found = true;
					value = Float.parseFloat(eS);
				}
				List aList = obj.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator el = aList.iterator(); el.hasNext();)
					{
						final EquipmentModifier em = (EquipmentModifier) el.next();
						String varInList = checkForVariableInList(em, variableString, isMax, "", "", found, value);
						if (varInList.length() > 0)
						{
							found = true;
							value = Float.parseFloat(varInList);
						}
					}
				}
				List aList2 = obj.getEqModifierList(false);
				if (!aList2.isEmpty())
				{
					for (Iterator el = aList2.iterator(); el.hasNext();)
					{
						final EquipmentModifier em = (EquipmentModifier) el.next();
						String varInList = checkForVariableInList(em, variableString, isMax, "", "", found, value);
						if (varInList.length() > 0)
						{
							found = true;
							value = Float.parseFloat(varInList);
						}
					}
				}
			}
		}

		if (!templateList.isEmpty())
		{
			for (Iterator oi = templateList.iterator(); oi.hasNext();)
			{
				final PCTemplate obj = (PCTemplate) oi.next();
				String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
				if (aString.length() > 0)
				{
					found = true;
					value = Float.parseFloat(aString);
				}
			}
		}

		if (!companionModList.isEmpty())
		{
			for (Iterator oi = companionModList.iterator(); oi.hasNext();)
			{
				final CompanionMod obj = (CompanionMod) oi.next();
				String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
				if (aString.length() > 0)
				{
					found = true;
					value = Float.parseFloat(aString);
				}
			}
		}

		if (race != null)
		{
			final String aString = checkForVariableInList(race, variableString, isMax, "", "", found, value);
			if (aString.length() > 0)
			{
				found = true;
				value = Float.parseFloat(aString);
			}
		}

		if (deity != null)
		{
			final String aString = checkForVariableInList(deity, variableString, isMax, "", "", found, value);
			if (aString.length() > 0)
			{
				found = true;
				value = Float.parseFloat(aString);
			}
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator oi = characterDomainList.iterator(); oi.hasNext();)
			{
				final CharacterDomain obj = (CharacterDomain) oi.next();
				if (obj.getDomain() == null)
				{
					continue;
				}
				String aString = checkForVariableInList(obj.getDomain(), variableString, isMax, "", "", found, value);
				if (aString.length() > 0)
				{
					found = true;
					value = Float.parseFloat(aString);
				}
			}
		}

		if (!weaponProfList.isEmpty())
		{
			for (Iterator oi = weaponProfList.iterator(); oi.hasNext();)
			{
				final WeaponProf obj = Globals.getWeaponProfNamed((String) oi.next());
				if (obj == null)
				{
					continue;
				}
				String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
				if (aString.length() > 0)
				{
					found = true;
					value = Float.parseFloat(aString);
				}
			}
		}
		for (Iterator e = statList.getStats().iterator(); e.hasNext();)
		{
			final PCStat obj = (PCStat) e.next();
			String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
			if (aString.length() > 0)
			{
				found = true;
				value = Float.parseFloat(aString);
			}
		}

		for (Iterator e = SystemCollections.getUnmodifiableAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment obj = (PCAlignment) e.next();
			String aString = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
			if (aString.length() > 0)
			{
				found = true;
				value = Float.parseFloat(aString);
			}
		}

		if (!found)
		{
			lastVariable = variableString;
			value = getVariableValue(variableString, "").floatValue();
			lastVariable = null;
		}

		if (includeBonus)
		{
			final double i = getTotalBonusTo("VAR", variableString);
			value += i;
		}
		return new Float(value);
	}

	private String checkForVariableInList(PObject obj, String variableString, boolean isMax, String matchSrc, String matchSubSrc, boolean found, double value)
	{
		boolean flag = false;
		for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
		{
			final String vString = obj.getVariableDefinition(i);
			final StringTokenizer aTok = new StringTokenizer(vString, "|");
			final String src = aTok.nextToken();
			if (matchSrc.length() > 0 && !src.equals(matchSrc))
			{
				continue;
			}

			if (matchSubSrc.length() > 0 || matchSrc.length() > 0)
			{
				final String subSrc = aTok.nextToken();
				if (matchSubSrc.length() > 0 && !subSrc.equals(matchSubSrc))
				{
					continue;
				}
			}
			if (!aTok.hasMoreTokens())
			{
				continue;
			}

			final String nString = aTok.nextToken();
			if (!aTok.hasMoreTokens())
			{
				continue;
			}

			if (nString.equalsIgnoreCase(variableString))
			{
				final String sString = aTok.nextToken();
				final Float newValue = getVariableValue(sString, src);
				if (!found)
				{
					value = newValue.floatValue();
				}
				else if (isMax)
				{
					value = Math.max(value, newValue.doubleValue());
				}
				else
				{
					value = Math.min(value, newValue.doubleValue());
				}
				found = true;
				flag = true;
				if (!"".equals(loopVariable))
				{
					while (loopValue > decrement)
					{
						loopValue -= decrement;
						value += getVariableValue(sString, src).doubleValue();
					}
					loopValue = 0;
					loopVariable = "";
				}
			}
		}
		if (flag)
		{
			return value + "";
		}
		else
		{
			return ""; // signifies that the variable was found in this list
		}
	}

	/**
	 * removes equipment of type aString from aList
	 **/
	public static List removeEqType(List aList, String aString)
	{
		final List aArrayList = new ArrayList();
		Equipment eq;
		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();
			if (aString.equalsIgnoreCase("CONTAINED") && (eq.getParent() != null))
			{
				continue;
			}
			if (!eq.typeStringContains(aString))
			{
				aArrayList.add(eq);
			}
		}
		return aArrayList;
	}

	/**
	 * Returns only the equipment of type aString from aList
	 **/
	public static List removeNotEqType(List aList, String aString)
	{
		final List aArrayList = new ArrayList();
		Equipment eq;
		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();
			if (eq.typeStringContains(aString))
			{
				aArrayList.add(eq);
			}
		}
		return aArrayList;
	}

	/**
	 * returns all equipment (from the equipmentList) of type aString
	 **/
	public List addEqType(List aList, String aString)
	{
		for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.typeStringContains(aString))
			{
				aList.add(eq);
			}
			else if (aString.equalsIgnoreCase("CONTAINED") && (eq.getParent() != null))
			{
				aList.add(eq);
			}
		}

		return aList;
	}

	/**
	 * an array of movement speeds
	 * @return	array of Integer movement speeds
	 **/
	private Integer[] getMovements()
	{
		return movements;
	}

	public int getNumberOfMovements()
	{
		return movements != null ? movements.length : 0;
	}

	/**
	 * @return the integer movement speed for Idx
	 **/
	Integer getMovement(int moveIdx)
	{
		if ((getMovements() != null) && (moveIdx < movements.length))
		{
			return movements[moveIdx];
		}
		else
		{
			return new Integer(0);
		}
	}

	/**
	 * @return the integer movement speed multiplier for Idx
	 **/
	Integer getMovementMult(int moveIdx)
	{
		if ((getMovements() != null) && (moveIdx < movementMult.length))
		{
			return movementMult[moveIdx];
		}
		else
		{
			return new Integer(0);
		}
	}

	public String getMovementType(int moveIdx)
	{
		if ((movementTypes != null) && (moveIdx < movementTypes.length))
		{
			return movementTypes[moveIdx];
		}
		else
		{
			return "";
		}
	}

	/**
	 * get the base MOVE: plus any bonuses from BONUS:MOVE additions
	 * does NOT take into account Armor restrictions to movement
	 * and does NOT take into account load carried
	 **/
	public int basemovement(int moveIdx, int iLoad)
	{
		// get base movement
		int move = getMovement(moveIdx).intValue();

		if (iLoad >= 0)
		{
			move = Globals.calcEncumberedMove(iLoad, move, false);
		}
		iForceLoad = iLoad;
		move += (int) getTotalBonusTo("MOVE", "TYPE." + getMovementType(moveIdx).toUpperCase());
		iForceLoad = -1;

		// always get following bonus
		move += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD");
		switch (iLoad)
		{
			// NOTE: LIGHT, MEDIUM and HEAVY loads are cumulative
			// and so have no breaks between them
			case Constants.LIGHT_LOAD:
				move += (int) getTotalBonusTo("MOVE", "LIGHT");
				//No break
			case Constants.MEDIUM_LOAD:
				move += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUM");
				//No break
			case Constants.HEAVY_LOAD:
				move += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY");
				break;
			default:
				break;
		}
		return move;
	}

	/**
	 * get the base MOVE: plus any bonuses from BONUS:MOVE additions
	 * takes into account Armor restrictions to movement and load carried
	 **/
	public int movement(int moveIdx)
	{
		int bonus = 0;

		// get base movement
		int move = getMovement(moveIdx).intValue();

		// get a list of all equipped Armor
		final List aList = getEquipmentOfType("Armor", 1);

		int pcLoad = Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight());

		if (aList.size() > 0)
		{
			// pcLoad will equal the greater of
			// encumberance load type or armor type
			for (Iterator a = aList.iterator(); a.hasNext();)
			{
				Equipment armor = (Equipment) a.next();
				if (armor.isHeavy())
				{
					pcLoad = Math.max(pcLoad, Constants.HEAVY_LOAD);
				}
				else if (armor.isMedium())
				{
					pcLoad = Math.max(pcLoad, Constants.MEDIUM_LOAD);
				}
				else if (armor.isLight())
				{
					pcLoad = Math.max(pcLoad, Constants.LIGHT_LOAD);
				}

			}
		}
		move = Globals.calcEncumberedMove(pcLoad, move, true);

		// This is old syntax which needs to be removed eventualy
		// JSC - 03/28/2003
		move += (int) getTotalBonusTo("MOVE", "TYPE." + getMovementType(moveIdx).toUpperCase());

		// First get the MOVEADD bonus
		move += (int) getTotalBonusTo("MOVEADD", "TYPE." + getMovementType(moveIdx).toUpperCase());
		// also check for special case of TYPE=ALL
		move += (int) getTotalBonusTo("MOVEADD", "TYPE.ALL");

		int calcMove = move;
		// now we apply any multipliers to the BASE move + MOVEADD move
		// First we get possible multipliers/divisors from the MOVE:
		// MOVEA: and MOVECLONE: tags
		if (getMovementMult(moveIdx).intValue() > 0)
		{
			calcMove = calcMoveMult(move, moveIdx);
		}
		// Now we get the BONUS:MOVEMULT multipliers
		double moveMult = getTotalBonusTo("MOVEMULT", "TYPE." + getMovementType(moveIdx).toUpperCase());
		// also check for special case of TYPE=ALL
		moveMult += getTotalBonusTo("MOVEMULT", "TYPE=ALL");
		if (moveMult > 0)
		{
			calcMove = (int) (calcMove * moveMult);
		}

		int postMove = move;
		// now add on any POSTMOVE bonuses
		postMove += (int) getTotalBonusTo("POSTMOVEADD", "TYPE." + getMovementType(moveIdx).toUpperCase());
		// also check for special case of TYPE=ALL
		postMove += (int) getTotalBonusTo("POSTMOVEADD", "TYPE=ALL");

		// because POSTMOVE is magical movement which should not be
		// multiplied by magial items, etc, we now see which is larger,
		// (baseMove + postMove)  or  (baseMove * moveMultiplier)
		// and keep the larger one, discarding the other

		move = Math.max(calcMove, postMove);

		// always get following bonus
		bonus += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD");
		switch (pcLoad)
		{
			// NOTE: LIGHT, MEDIUM and HEAVY loads are cumulative
			// and so have no breaks between them
			case Constants.LIGHT_LOAD:
				bonus += (int) getTotalBonusTo("MOVE", "LIGHT");
				//No break
			case Constants.MEDIUM_LOAD:
				bonus += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUM");
				//No break
			case Constants.HEAVY_LOAD:
				bonus += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY");
				break; // break here
			case Constants.OVER_LOAD:
				break; // this is handled elsewhere

			default:
				Logging.errorPrint("In PlayerCharacter.movement the load constant " + pcLoad + " is not handled.");
				break;
		}
		move += bonus;

		return move;
	}

	public int calcMoveMult(final int move, final int index)
	{
		int iMove = 0;
		if (movementMultOp[index].charAt(0) == '*')
		{
			iMove = move * movementMult[index].intValue();
		}
		else if (movementMultOp[index].charAt(0) == '/')
		{
			iMove = move / movementMult[index].intValue();
		}
		if (iMove > 0)
		{
			return iMove;
		}
		return move;
	}

	public int initiativeMod()
	{
		final int initmod = (int) getTotalBonusTo("COMBAT", "Initiative") + getVariableValue("INITCOMP", "").intValue();
		return initmod;
	}

	private int getNumAttacks()
	{
		return Math.min(Math.max(baseAttackBonus() / 5, 4), 1);
	}

	private int getTotalClassLevels()
	{
		int total = 0;
		for (int i = 0; i < classList.size(); i++)
		{
			final PCClass aClass = (PCClass) classList.get(i);
			total += aClass.getLevel();
		}
		return total;
	}

	private HashMap getTotalLevelHashMap()
	{
		HashMap lvlMap = new HashMap();
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass) i.next();
			lvlMap.put(aClass.getName(), String.valueOf(aClass.getLevel()));
		}
		return lvlMap;
	}

	private Map getLevelHashMap(int maxLevel)
	{
		Map lvlMap = new HashMap();
		for (int i = 0; i < getLevelInfoSize(); ++i)
		{
			final String classKeyName = getLevelInfoClassKeyName(i);
			String val = (String) lvlMap.get(classKeyName);
			if (val == null)
			{
				val = "0";
			}
			val = String.valueOf(Integer.parseInt(val) + 1);
			lvlMap.put(classKeyName, val);
			maxLevel--;
			if (maxLevel <= 0)
			{
				break;
			}
		}
		return lvlMap;
	}

	private void setClassLevelsBrazenlyTo(Map lvlMap)
	{
		// set class levels to classname,lvl pair
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass) i.next();
			String lvl = (String) lvlMap.get(aClass.getName());
			if (lvl == null)
			{
				lvl = "0";
			}
			aClass.setLevelWithoutConsequence(Integer.parseInt(lvl));
		}
	}

	public String getAttackString(int index)
	{
		// index: 0 = melee; 1 = ranged; 2 = unarmed
		return getAttackString(index, 0);
	}

	public String getAttackString(int index, int bonus)
	{
		// index: 0 = melee; 1 = ranged; 2 = unarmed

		// now we see if this PC is a Familiar
		// initialize to some large negative number
		int masterBAB = -9999;
		int masterTotal = -9999;
		final PlayerCharacter nPC = getMasterPC();

		int totalClassLevels = getTotalClassLevels();
		Map totalLvlMap = null;
		Map classLvlMap = null;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap = getLevelHashMap(SettingsHandler.getGame().getBabMaxLvl());
			setClassLevelsBrazenlyTo(classLvlMap); // insure class-levels total is below some value (e.g. 20)
		}

		if ((nPC != null) && getCopyMasterBAB().length() > 0)
		{
			masterBAB = nPC.baseAttackBonus();
			String copyMasterBAB = getCopyMasterBAB();
			while (true)
			{
				int x = copyMasterBAB.indexOf("MASTER");
				if (x == -1)
				{
					break;
				}
				final String leftString = copyMasterBAB.substring(0, x);
				final String rightString = copyMasterBAB.substring(x + 6);
				copyMasterBAB = leftString + Integer.toString(masterBAB) + rightString;
			}
			masterBAB = getVariableValue(copyMasterBAB, "").intValue();
			masterTotal = masterBAB + bonus;
		}

		int BAB = baseAttackBonus();
		int bonusBAB = bonus + baseAttackBonus();

		final List ab = new ArrayList(10);
		int total;
		int attacks = 1;
		int mod = bonusBAB;
		int subTotal = BAB;
		int raceBAB = getRace().getBAB();
		final StringBuffer attackString = new StringBuffer();

		for (total = 0; total < 10; ++total)
		{
			ab.add(new Integer(0));
		}

		for (int i = 0; i < classList.size(); ++i)
		{
			final PCClass aClass = (PCClass) classList.get(i);
			final int b = aClass.baseAttackBonus();
			final int c = aClass.attackCycle(index);
			final int d = ((Integer) ab.get(c)).intValue() + b;
			ab.set(c, new Integer(d));
			if (c != 3)
			{
				raceBAB += b;
			}
		}
		for (int i = 2; i < 10; ++i)
		{
			if (((Integer) ab.get(i)).intValue() > ((Integer) ab.get(attacks)).intValue())
			{
				attacks = i;
			}
		}

		if (totalLvlMap != null) // restore class levels to original value if altered
		{
			setClassLevelsBrazenlyTo(totalLvlMap);
		}

		total = ((Integer) ab.get(attacks)).intValue();
		int defaultAttackRange = SettingsHandler.getGame().getBabAttCyc(); // cut-off before multiple attacks (e.g. 5)
		if (total == 0)
		{
			attacks = defaultAttackRange;
		}

		// FAMILIAR: check to see if the masters BAB is better
		mod = Math.max(mod, masterTotal);
		subTotal = Math.max(subTotal, masterBAB);
		raceBAB = Math.max(raceBAB, masterBAB);

		if (attacks != defaultAttackRange)
		{
			if (total / attacks < subTotal / defaultAttackRange)
			{
				attacks = defaultAttackRange;
				total = subTotal;
			}
			else
			{
				mod -= raceBAB;
				subTotal -= raceBAB;
			}
		}
		int totalAttacks = SettingsHandler.getGame().getBabMaxAtt();
		int minMultipleAttackBab = SettingsHandler.getGame().getBabMinVal();
		while ((attackString.length() == 0) || (total >= minMultipleAttackBab) || (subTotal >= minMultipleAttackBab) && totalAttacks > 0)
		{
			if (attackString.length() > 0)
			{
				attackString.append('/');
			}

			attackString.append(Delta.toString(mod));
			mod -= attacks;
			total -= attacks;
			subTotal -= attacks;
			totalAttacks--;
		}

		return attackString.toString();
	}

	/**
	 * compares old string in form of sides|damage e.g: 2|1d2
	 * to new string in form of damage
	 * return string in form of sides|damage for easier comparison later
	 **/
	private static String getBestUDamString(String oldString, String newString)
	{
		if (newString == null || newString.length() < 2)
		{
			return oldString;
		}
		StringTokenizer aTok = new StringTokenizer(oldString, "|");
		int sides = Integer.parseInt(aTok.nextToken());
		String retString = oldString;
		aTok = new StringTokenizer(newString, " dD+-(x)");
		if (aTok.countTokens() > 1)
		{
			aTok.nextToken();
			final int i = Integer.parseInt(aTok.nextToken());
			if (sides < i)
			{
				sides = i;
				retString = sides + "|" + newString;
			}
		}
		return retString;
	}

	public String getUnarmedDamageString(boolean includeCrit, boolean includeStrBonus)
	{
		String retString = "2|1d2";
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			retString = getBestUDamString(retString, aClass.getUdamForLevel(aClass.getLevel(), includeCrit, includeStrBonus));
		}
		retString = getBestUDamString(retString, race.getUdamFor(includeCrit, includeStrBonus));
		if (deity != null)
		{
			retString = getBestUDamString(retString, deity.getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat) i.next();
			retString = getBestUDamString(retString, aFeat.getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill) i.next();
			retString = getBestUDamString(retString, aSkill.getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				retString = getBestUDamString(retString, aCD.getDomain().getUdamFor(includeCrit, includeStrBonus));
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				retString = getBestUDamString(retString, eq.getUdamFor(includeCrit, includeStrBonus));

				List aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						retString = getBestUDamString(retString, eqMod.getUdamFor(includeCrit, includeStrBonus));
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						retString = getBestUDamString(retString, eqMod.getUdamFor(includeCrit, includeStrBonus));
					}
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate) i.next();
			retString = getBestUDamString(retString, aTemplate.getUdamFor(includeCrit, includeStrBonus));
		}
		// string is in form sides|damage, just return damage portion
		return retString.substring(retString.indexOf('|') + 1);
	}

	public void setAlignment(int index, boolean bLoading)
	{
		setAlignment(index, bLoading, false);
		setDirty(true);
	}

	public void setAlignment(int index, boolean bLoading, boolean bForce)
	{
		// Anyone every heard of constants!?

		// 0 = LG, 3 = NG, 6 = CG
		// 1 = LN, 4 = TN, 7 = CN
		// 2 = LE, 5 = NE, 8 = CE
		if (bForce || this.race.canBeAlignment(Integer.toString(index)))
		{
			alignment = index;
		}
		else
		{
			if ((bLoading) && (index != SystemCollections.getIndexOfAlignment(Constants.s_NONE)))
			{
				GuiFacade.showMessageDialog(null, "Invalid alignment. Setting to <none selected>", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				alignment = SystemCollections.getIndexOfAlignment(Constants.s_NONE);
			}
			//TODO raise an exception, once I define one. Maybe
			//ArrayIndexOutOfBounds?
		}
	}

	/**
	 * recalculate all the move rates and modifiers
	 **/
	public void adjustMoveRates()
	{
		movements = null;
		movementTypes = null;
		movementMult = null;
		movementMultOp = null;

		if ((getRace() == null) || (getRace().getMovements() == null))
		{
			return;
		}

		movements = (Integer[]) getRace().getMovements().clone();
		movementTypes = (String[]) getRace().getMovementTypes().clone();
		movementMult = (Integer[]) getRace().getMovementMult().clone();
		movementMultOp = (String[]) getRace().getMovementMultOp().clone();
		// template
		if (!getTemplateList().isEmpty())
		{
			setMoveFromList(getTemplateList());
		}
		// class
		if (!getClassList().isEmpty())
		{
			setMoveFromList(getClassList());
		}
		// feat
		if (!aggregateFeatList().isEmpty())
		{
			setMoveFromList(aggregateFeatList());
		}
		// equipment
		if (!getEquipmentList().isEmpty())
		{
			setMoveFromList(getEquipmentList());
			for (Iterator e = getEquipmentList().iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment) e.next();
				if (eq.isEquipped())
				{
					List bList = eq.getEqModifierList(true);
					setMoveFromList(bList);
					bList = eq.getEqModifierList(false);
					setMoveFromList(bList);
				}
			}
		}
		// domain
		/*
		if (!characterDomainList.isEmpty())
		{
			setMoveFromList(characterDomainList());
		}
		*/
		// tempmods
		if (!getTempBonusList().isEmpty() && getUseTempMods())
		{
			setMoveFromList(getTempBonusList());
		}

	}

	private void setMoveFromList(List aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			final Object anObj = e.next();
			if (!(anObj instanceof PObject))
			{
				continue;
			}
			final PObject pObj = (PObject) anObj;

			if (pObj.getNumberOfMovements() < 1)
			{
				continue;
			}

			for (int i = 0; i < pObj.getNumberOfMovements(); i++)
			{
				setMyMoveRates(pObj.getMovementType(i), pObj.getMovement(i).intValue(), pObj.getMovementMult(i), pObj.getMovementMultOp(i), pObj.getMoveRatesFlag());
			}
		}
	}

	/**
	 * sets up the movement arrays
	 * creates them if they do not exist
	 **/
	private void setMyMoveRates(String moveType, int anInt, Integer moveMult, String multOp, int moveFlag)
	{
		//
		// NOTE: can not use getMovements() accessor as it calls
		// this function, so use the variable: movements
		//

		Integer moveRate = new Integer(anInt);

		// The ALL type can only be applied to existing movement
		// so just loop and add or set as appropriate
		if ("ALL".equals(moveType))
		{
			if (moveFlag == 0)
			{ // set all types of movement to moveRate
				for (int i = 0; i < movements.length; i++)
				{
					moveRate = new Integer(anInt);
					movements[i] = moveRate;
				}
			}
			else
			{ // add moveRate to all types of movement.
				for (int i = 0; i < movements.length; i++)
				{
					moveRate = new Integer(anInt + movements[i].intValue());
					movements[i] = moveRate;
				}
			}
		}
		else
		{
			if (moveFlag == 0)
			{ // set movement to moveRate
				moveRate = new Integer(anInt);
				for (int i = 0; i < movements.length; i++)
				{
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						movementMult[i] = moveMult;
						movementMultOp[i] = multOp;
						return;
					}
				}
				increaseMoveArray(moveRate, moveType, moveMult, multOp);
			}
			else if (moveFlag == 1)
			{ // add moveRate to movement.
				for (int i = 0; i < movements.length; i++)
				{
					moveRate = new Integer(anInt + movements[i].intValue());
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						movementMult[i] = moveMult;
						movementMultOp[i] = multOp;
						return;
					}
					increaseMoveArray(moveRate, moveType, moveMult, multOp);
				}
			}
			else
			{ // get base movement, then add moveRate
				moveRate = new Integer(anInt + movements[0].intValue());
				// for existing types of movement:
				for (int i = 0; i < movements.length; i++)
				{
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						movementMult[i] = moveMult;
						movementMultOp[i] = multOp;
						return;
					}
				}
				increaseMoveArray(moveRate, moveType, moveMult, multOp);
			}

		}
	}

	private void increaseMoveArray(Integer moveRate, String moveType, Integer moveMult, String multOp)
	{
		// could not find an existing one so
		// need to add new item to array
		//
		final Integer[] tempMove = movements;
		final String[] tempType = movementTypes;
		final Integer[] tempMult = movementMult;
		final String[] tempMultOp = movementMultOp;

		// now increase the size of the array by one

		movements = new Integer[tempMove.length + 1];
		movementTypes = new String[tempMove.length + 1];
		movementMult = new Integer[tempMove.length + 1];
		movementMultOp = new String[tempMove.length + 1];

		System.arraycopy(tempMove, 0, movements, 0, tempMove.length);
		System.arraycopy(tempType, 0, movementTypes, 0, tempMove.length);
		System.arraycopy(tempMult, 0, movementMult, 0, tempMove.length);
		System.arraycopy(tempMultOp, 0, movementMultOp, 0, tempMove.length);
		// the size is larger, but arrays start at 0
		// so an array length=3 would have 0, 1, 2 as the targets
		movements[tempMove.length] = moveRate;
		movementTypes[tempMove.length] = moveType;
		movementMult[tempMove.length] = moveMult;
		movementMultOp[tempMove.length] = multOp;
	}

	/**
	 * create a map of key (vision-type string) and values (int)
	 **/
	private Map addStringToVisionMap(Map visMap, Map aMap)
	{
		if (aMap == null || aMap.size() == 0)
		{
			return visMap;
		}
		for (Iterator i = aMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final String aVal = aMap.get(aKey).toString();
			final Object bObj = visMap.get(aKey);
			int b = 0;
			if (bObj != null)
			{
				b = getVariableValue(bObj.toString(), "").intValue();
			}
			int a = getVariableValue(aVal, "").intValue();
			if (a >= b)
			{
				visMap.put(aKey, String.valueOf(a));
			}
		}
		return visMap;
	}

	public String getVision()
	{
		Map visMap = new HashMap();
		if (race != null)
		{
			visMap = addStringToVisionMap(visMap, race.getVision());
		}
		if (deity != null)
		{
			visMap = addStringToVisionMap(visMap, deity.getVision());
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass) i.next();
			visMap = addStringToVisionMap(visMap, aClass.getVision());
		}
		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat) i.next();
			visMap = addStringToVisionMap(visMap, aFeat.getVision());
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill) i.next();
			visMap = addStringToVisionMap(visMap, aSkill.getVision());
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				visMap = addStringToVisionMap(visMap, aCD.getDomain().getVision());
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				visMap = addStringToVisionMap(visMap, eq.getVision());
				List aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						visMap = addStringToVisionMap(visMap, eqMod.getVision());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						visMap = addStringToVisionMap(visMap, eqMod.getVision());
					}
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate) i.next();
			visMap = addStringToVisionMap(visMap, aTemplate.getVision());
		}

		// parse through the global list of vision tags and see
		// if this PC has any BONUS:VISION tags which will create
		// a new visionMap entry
		for (Iterator i = Globals.getVisionMap().keySet().iterator(); i.hasNext();)
		{
			String aKey = (String) i.next();
			int aVal = (int) getTotalBonusTo("VISION", aKey);
			if (aVal > 0)
			{
				HashMap newMap = new HashMap();
				newMap.put(aKey, String.valueOf(aVal));
				visMap = addStringToVisionMap(visMap, newMap);
			}
		}
		//
		// Now build the string for all the vision types
		//
		final StringBuffer vision = new StringBuffer();
		for (Iterator i = visMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			Object bObj = visMap.get(aKey);
			if (bObj == null)
			{
				continue;
			}
			int val = Integer.parseInt(bObj.toString());
			if (vision.length() > 0)
			{
				vision.append(", ");
			}
			vision.append(aKey);
			if (val > 0)
			{
				vision.append(" (").append(val).append("')");
			}
		}
		return vision.toString();
	}

	public void addTemplate(PCTemplate inTmpl)
	{
		if (inTmpl == null)
		{
			return;
		}

		// don't allow multiple copies of template
		if (templateList.contains(inTmpl))
		{
			return;
		}
		templateList.add(inTmpl);

		calcActiveBonuses();

		templateAutoLanguages.addAll(inTmpl.getAutoLanguages());
		templateLanguages.addAll(inTmpl.getLanguageBonus());
		getAutoLanguages();
		addNaturalWeapons(inTmpl);

		inTmpl.chooseLanguageAutos(importing);
		if (canReassignTemplateFeats())
		{
			final List templateFeats = inTmpl.feats(getTotalLevels(), totalHitDice());
			for (int i = 0, x = templateFeats.size(); i < x; ++i)
			{
				modFeatsFromList((String) templateFeats.get(i), true, false);
			}
		}
		else
		{
			setAutomaticFeatsStable(false);
		}

		final List templates = inTmpl.getTemplates(importing);
		for (int i = 0, x = templates.size(); i < x; ++i)
		{
			addTemplate(Globals.getTemplateNamed((String) templates.get(i)));
		}
		setQualifyListStable(false);
		if (!importing)
		{
			getSpellList();
			inTmpl.globalChecks();
		}
	}

	public void removeTemplate(PCTemplate inTmpl)
	{
		if (inTmpl == null)
		{
			return;
		}

		if (inTmpl.weaponProfAutos != null)
		{
			weaponProfList.removeAll(inTmpl.getWeaponProfAutos());
		}
		getLanguagesList().removeAll(inTmpl.getAutoLanguages()); // remove template languages.
		templateAutoLanguages.removeAll(inTmpl.getAutoLanguages()); // remove them from the local listing. Don't clear though in case of multiple templates.

		templateLanguages.removeAll(inTmpl.getLanguageBonus());
		removeNaturalWeapons(inTmpl);

		// It is hard to tell if removeTemplate() modifies
		// inTmpl.templatesAdded(), so not safe to optimize
		// the call to .size().	 XXX
		for (int i = 0; i < inTmpl.templatesAdded().size(); ++i)
		{
			removeTemplate(getTemplateNamed((String) inTmpl.templatesAdded().get(i)));
		}
		for (int i = 0; i < templateList.size(); ++i)
		{
			if (((PCTemplate) templateList.get(i)).getName().equals(inTmpl.getName()))
			{
				templateList.remove(i);
				break;
			}
		}
		if (!canReassignTemplateFeats())
		{
			setAutomaticFeatsStable(false);
		}
		setQualifyListStable(false);
		// re-evaluate non-spellcaster spell lists
		getSpellList();
	}

	public void incrementClassLevel(int mod, PCClass aClass)
	{
		incrementClassLevel(mod, aClass, false);
	}

	private void incrementClassLevel(final int mod, final PCClass globalClass, final boolean bSilent)
	{
		// If not importing, load the spell list
		if (!importing)
		{
			getSpellList();
		}

		// Make sure the character qualifies for the class if adding it
		if (mod > 0)
		{
			if (!globalClass.isQualified())
			{
				return;
			}
			if (globalClass.isMonster() && !SettingsHandler.isIgnoreMonsterHDCap() && !race.isAdvancementUnlimited() && totalHitDice() + mod > race.maxHitDiceAdvancement() && !bSilent)
			{
				GuiFacade.showMessageDialog(null, "Cannot increase Monster Hit Dice for this character beyond " + race.maxHitDiceAdvancement() + ". This character's current number of Monster Hit Dice is " + totalHitDice(), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return;
			}
		}

		// Check if the character already has the class.
		PCClass pcClassClone = getClassNamed(globalClass.getName());

		// If the character did not already have the class...
		if (pcClassClone == null)
		{
			// add the class even if setting to level 0
			if (mod >= 0)
			{
				// Get a clone of the class so we don't modify the globals!
				pcClassClone = (PCClass) globalClass.clone();

				// Make sure the clone was successful
				if (pcClassClone == null)
				{
					Logging.errorPrint("PlayerCharacter::incrementClassLevel => " + "Clone of class " + globalClass.getName() + " failed!");
					return;
				}

				// Add the class to the character classes as level 0
				classList.add(pcClassClone);

				// do the following only if adding a level of a class for the first time
				if (mod > 0)
				{
					final Set aSet = pcClassClone.getAutoLanguages();
					getLanguagesList().addAll(aSet);
				}
			}
			else
			{
				// mod is < 0 and character does not have class.  Return.
				return;
			}
		}

		// Add or remove levels as needed
		if (mod > 0)
		{
			for (int i = 0; i < mod; ++i)
			{
				saveLevelInfo(pcClassClone.getKeyName());
				pcClassClone.addLevel(false, bSilent);
			}
		}
		else if (mod < 0)
		{
			for (int i = 0; i < -mod; ++i)
			{
				pcClassClone.subLevel(bSilent);
				removeLevelInfo(pcClassClone.getKeyName());
			}
		}

		// Handle any feat changes as a result of level changes
		if (canReassignTemplateFeats())
		{
			for (int i = 0, x = templateList.size(); i < x; ++i)
			{
				PCTemplate aTemplate = (PCTemplate) templateList.get(i);
				List templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice());

				for (int j = 0, y = templateFeats.size(); j < y; ++j)
				{
					modFeatsFromList((String) templateFeats.get(j), true, false);
				}
			}
		}
		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
		calcActiveBonuses();
		setDirty(true);
	}

	private void rebuildLists(PCClass toClass, PCClass fromClass, int iCount)
	{
		final int fromLevel = fromClass.getLevel();
		final int toLevel = toClass.getLevel();
		for (int i = 0; i < iCount; ++i)
		{
			fromClass.doMinusLevelMods(this, fromLevel - i);
			toClass.doPlusLevelMods(toLevel + i + 1);
		}
	}

	void giveClassesAway(PCClass toClass, PCClass fromClass, int iCount)
	{
		if ((toClass == null) || (fromClass == null))
		{
			return;
		}

		// Will take destination class over maximum?
		if (toClass.getLevel() + iCount > toClass.getMaxLevel())
		{
			iCount = toClass.getMaxLevel() - toClass.getLevel();
		}

		// Enough levels to move?
		if ((fromClass.getLevel() <= iCount) || (iCount < 1))
		{
			return;
		}

		final int iFromLevel = fromClass.getLevel() - iCount;
		final int iToLevel = toClass.getLevel();

		toClass.setLevel(iToLevel + iCount);
		for (int i = 0; i < iCount; ++i)
		{
			toClass.setHitPoint(iToLevel + i, fromClass.getHitPoint(iFromLevel + i));
			fromClass.setHitPoint(iFromLevel + i, new Integer(0));
		}

		rebuildLists(toClass, fromClass, iCount);

		fromClass.setLevel(iFromLevel);

		// first, change the toClass current PCLevelInfo level
		for (Iterator li = pcLevelInfo.iterator(); li.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) li.next();
			if (pcl.getClassKeyName().equals(toClass.getKeyName()))
			{
				int iTo = pcl.getLevel() + toClass.getLevel() - iToLevel;
				pcl.setLevel(iTo);
			}
		}
		// change old class PCLevelInfo to the new class
		for (Iterator li = pcLevelInfo.iterator(); li.hasNext();)
		{
			final PCLevelInfo pcl = (PCLevelInfo) li.next();
			if (pcl.getClassKeyName().equals(fromClass.getKeyName()) && (pcl.getLevel() > iFromLevel))
			{
				int iFrom = pcl.getLevel() - iFromLevel;
				pcl.setClassKeyName(toClass.getKeyName());
				pcl.setLevel(iFrom);
			}
		}

		/*
		// get skills associated with old class and link to new class
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill) e.next();
			aSkill.replaceClassRank(fromClass.getName(), toClass.getName());
		}
		toClass.setSkillPool(fromClass.getSkillPool());
		*/
	}

	public void makeIntoExClass(PCClass aClass)
	{
		final String exClass = aClass.getExClass();
		if (exClass.length() == 0)
		{
			return;
		}

		try
		{
			PCClass bClass = getClassNamed(exClass);
			if (bClass == null)
			{
				bClass = Globals.getClassNamed(exClass);
				if (bClass == null)
				{
					return;
				}

				bClass = (PCClass) bClass.clone();

				rebuildLists(bClass, aClass, aClass.getLevel());

				bClass.setLevel(aClass.getLevel());
				bClass.setHitPointMap(aClass.getHitPointMap());
				final int idx = classList.indexOf(aClass);
				classList.set(idx, bClass);
			}
			else
			{
				rebuildLists(bClass, aClass, aClass.getLevel());
				bClass.setLevel(bClass.getLevel() + aClass.getLevel());
				for (int i = 0; i < aClass.getLevel(); ++i)
				{
					bClass.setHitPoint(bClass.getLevel() + i + 1, aClass.getHitPoint(i + 1));
				}
				classList.remove(aClass);
			}

			//
			// change all the levelling info to the ex-class as well
			//
			for (int idx = pcLevelInfo.size() - 1; idx >= 0; --idx)
			{
				final PCLevelInfo li = (PCLevelInfo) pcLevelInfo.get(idx);
				if (li.getClassKeyName().equals(aClass.getKeyName()))
				{
					li.setClassKeyName(bClass.getKeyName());
				}
			}

			//
			// Find all skills associated with old class and link them to new class
			//
			for (Iterator e = getSkillList().iterator(); e.hasNext();)
			{
				Skill aSkill = (Skill) e.next();
				aSkill.replaceClassRank(aClass.getName(), exClass);
			}
			bClass.setSkillPool(aClass.getSkillPool());
		}
		catch (NumberFormatException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}

	}

	public Skill addSkill(Skill addSkill)
	{
		Skill aSkill;
		//
		// First, check to see if skill is already in list
		//
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			aSkill = (Skill) e.next();
			if (aSkill.getKeyName().equals(addSkill.getKeyName()))
			{
				return aSkill;
			}
		}

		//
		// Skill not found, add to list
		//
		aSkill = (Skill) addSkill.clone();
		getSkillList().add(aSkill);
		if (!importing)
		{
			aSkill.globalChecks();
			calcActiveBonuses();
		}
		return aSkill;
	}

	/**
	 * Calculate the maximum number of ranks the character is allowed to have
	 * in the specified skill.
	 *
	 * @param skillName The name of the skill being checked.
	 * @param aClass The name of the current class in which points are being spent
	 *		 - only used to check cross-class skill cost.
	 */
	public Float getMaxRank(String skillName, PCClass aClass)
	{
		int levelForSkillPurposes = getTotalLevels();
		BigDecimal maxRanks;

		if (SettingsHandler.isMonsterDefault())
		{
			levelForSkillPurposes += totalHitDice();
		}

		final Skill aSkill = Globals.getSkillNamed(skillName);
		if (aSkill.isExclusive())
		{
			// Exclusive skills only count levels in classes which give access to the skill
			levelForSkillPurposes = 0;
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				final PCClass bClass = (PCClass) e.next();
				if (aSkill.isClassSkill(bClass))
				{
					levelForSkillPurposes += bClass.getLevel();
				}
			}
			if (SettingsHandler.isMonsterDefault())
			{
				levelForSkillPurposes += totalHitDice();
			}
			if (levelForSkillPurposes == 0)
			{
				// No classes qualify for this exclusive skill, so treat it as a cross-class skill
				// This does not seem right to me! JD
				if (SettingsHandler.isMonsterDefault())
				{
					levelForSkillPurposes = (getTotalLevels() + totalHitDice());
				}
				else
				{
					levelForSkillPurposes = (getTotalLevels());
				}
				maxRanks = maxCrossClassSkillForLevel(levelForSkillPurposes);
			}
			else
			{
				maxRanks = maxClassSkillForLevel(levelForSkillPurposes);
			}
		}
		else if (!aSkill.isClassSkill(classList) && (aSkill.costForPCClass(aClass).intValue() == 1))
		{
			// Cross class skill - but as cost is 1 only return a whole number
			maxRanks = new BigDecimal(maxCrossClassSkillForLevel(levelForSkillPurposes).intValue()); // This was (int) (i/2.0) previously
		}
		else if (!aSkill.isClassSkill(classList))
		{
			// Cross class skill
			maxRanks = maxCrossClassSkillForLevel(levelForSkillPurposes);
		}
		else
		{
			// Class skill
			maxRanks = maxClassSkillForLevel(levelForSkillPurposes);
		}
		maxRanks = maxRanks.add(new BigDecimal((int) getTotalBonusTo("SKILLMAXRANK", skillName)));
		return new Float(maxRanks.floatValue());
	}

	/**
	 * Returns the list of names of available feats of given type.
	 * That is, all feats from the global list, which match the
	 * given featType, the character qualifies for, and the
	 * character does not already have.
	 *
	 * @param featType  String category of feat to list.
	 * @return List of Feats.
	 */
	List getAvailableFeatNames(String featType)
	{
		final List aFeatList = new ArrayList();
		final List globalFeatList = Globals.getFeatList();
		final int globalFeatListSize = globalFeatList.size();
		for (int index = 0; index < globalFeatListSize; ++index)
		{
			final Feat aFeat = (Feat) globalFeatList.get(index);
			if (aFeat.matchesType(featType) && qualifiesForFeat(aFeat.getKeyName()) && ((!hasFeat(aFeat.getName()) && !hasFeatAutomatic(aFeat.getName())) || aFeat.isMultiples()))
			{
				aFeatList.add(aFeat.getKeyName());
			}
		}
		return aFeatList;
	}

	public int hitPoints()
	{
		final double iConMod = getStatBonusTo("HP", "BONUS");

		int total = 0;
		if (race.hitDice() != 0)
		{
			total = race.calcHitPoints((int) iConMod);
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			total += aClass.hitPoints((int) iConMod);
		}

		total += (int) getTotalBonusTo("HP", "CURRENTMAX");
		//
		// now we see if this PC is a Familiar
		final PlayerCharacter nPC = getMasterPC();
		if (nPC == null)
		{
			return total;
		}
		if (getCopyMasterHP().length() == 0)
		{
			return total;
		}
		else
		{
			//
			// In order for the BONUS's to work, the PC we want
			// to get the hit points for must be the "current" one.
			//
			final PlayerCharacter curPC = Globals.getCurrentPC();
			Globals.setCurrentPC(nPC);
			int masterHP = nPC.hitPoints();
			Globals.setCurrentPC(curPC);

			String copyMasterHP = getCopyMasterHP();
			while (true)
			{
				int x = copyMasterHP.indexOf("MASTER");
				if (x == -1)
				{
					break;
				}
				final String leftString = copyMasterHP.substring(0, x);
				final String rightString = copyMasterHP.substring(x + 6);
				copyMasterHP = leftString + Integer.toString(masterHP) + rightString;
			}
			masterHP = getVariableValue(copyMasterHP, "").intValue();
			return masterHP;
		}
	}

	public boolean isNonAbility(int i)
	{
		if (race.isNonAbility(i))
		{
			return true;
		}
		for (int x = 0; x < templateList.size(); ++x)
		{
			if (((PCTemplate) templateList.get(x)).isNonAbility(i))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Changes the race of the character. First it removes the
	 * current Race, and any bonus attributes (e.g. feats), then
	 * add the new Race.
	 */
	public void setRace(Race aRace)
	{
		final Race oldRace = getRace();
		// remove current race attributes
		if (oldRace != null)
		{
			oldRace.clearCharacterSpells();
			if (canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(oldRace.getFeatList(), "|");
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aString.endsWith(")") && Globals.getFeatNamed(aString) == null)
					{
						final String featName = aString.substring(0, aString.indexOf('(') - 1);

						final Feat aFeat = Globals.getFeatNamed(featName);
						if (aFeat != null)
						{
							modFeat(aString, true, false);
							setFeats(feats - aFeat.getCost());
						}
					}
					else
					{
						final Feat aFeat = Globals.getFeatNamed(aString);
						if (aFeat != null)
						{
							final String featName = aFeat.getName();
							if ((hasFeat(featName) || hasFeatAutomatic(featName)))
							{
								modFeat(aString, true, false);
								setFeats(feats - aFeat.getCost());
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "Removing unknown feat: " + aString, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
						}
					}
				}
			}
			getLanguagesList().removeAll(oldRace.getAutoLanguages());
			if (oldRace.getWeaponProfAutos() != null)
			{
				weaponProfList.removeAll(oldRace.getWeaponProfAutos());
			}
			if (racialFavoredClass.length() != 0)
			{
				favoredClasses.remove(racialFavoredClass);
			}

			removeNaturalWeapons(race);
			for (int x = 0; x < race.templatesAdded().size(); ++x)
			{
				removeTemplate(getTemplateNamed((String) race.templatesAdded().get(x)));
			}
			if (race.getMonsterClass() != null && race.getMonsterClassLevels() != 0)
			{
				final PCClass mclass = Globals.getClassNamed(race.getMonsterClass());
				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels() * -1, mclass, true);
				}
			}
		}

		// add new race attributes
		race = null;
		if (aRace != null)
		{
			race = (Race) aRace.clone();
		}
		if (race != null)
		{
			race.activateBonuses();

			if (!importing)
			{
				Globals.getBioSet().randomize("AGE.HT.WT");
			}

			// Get existing classes
			final List existingClasses = new ArrayList(classList);
			classList.clear();

			//
			// Remove all saved monster level information
			//
			for (int i = getLevelInfoSize() - 1; i >= 0; --i)
			{
				final String classKeyName = getLevelInfoClassKeyName(i);
				final PCClass aClass = Globals.getClassKeyed(classKeyName);
				if ((aClass == null) || aClass.isMonster())
				{
					removeLevelInfo(i);
				}
			}
			List existingLevelInfo = new ArrayList(pcLevelInfo);
			pcLevelInfo.clear();

			skillPoints = 0;

			// Make sure monster classes are added first
			if (!importing && (race.getMonsterClass() != null) && (race.getMonsterClassLevels() != 0))
			{
				final PCClass mclass = Globals.getClassNamed(race.getMonsterClass());
				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels(), mclass, true);
				}
			}
			pcLevelInfo.addAll(existingLevelInfo);

			//
			// If user has chosen a class before choosing a race,
			// we need to tweak the number of skill points and feats
			//
			if (!importing && ((oldRace == null) || (existingClasses.size() != 0)))
			{
				setFeats(feats + race.getBonusInitialFeats());
				int spMod = 0;
				int totalLevels = this.getTotalLevels();
				final Integer zero = new Integer(0);
				for (int i = 0; i < existingClasses.size(); ++i)
				{
					final PCClass aClass = (PCClass) existingClasses.get(i);
					//
					// Don't add monster classes back in. This will possibly mess up feats earned by level
					// ?Possibly convert to mclass if not null?
					//
					if (!aClass.isMonster())
					{
						classList.add(aClass);
						final int cLevels = aClass.getLevel();
						//aClass.setLevel(0);
						aClass.setSkillPool(zero);
						int cMod = 0;
						for (int j = 0; j < cLevels; ++j)
						{
							cMod += aClass.recalcSkillPointMod(this, ++totalLevels);
						}
						spMod += cMod;
						aClass.setSkillPool(new Integer(cMod));
						skillPoints += spMod;
					}
				}
			}

			addNaturalWeapons(race);
			if (canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(), "|");
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aString.endsWith(")") && Globals.getFeatNamed(aString) == null)
					{
						// we want the first instance of it, in case of Weapon Focus(Longbow (Composite))
						final String featName = aString.substring(0, aString.indexOf('(') - 1);

						final Feat aFeat = Globals.getFeatNamed(featName);
						if (aFeat != null)
						{
							setFeats(feats + aFeat.getCost());
							//modFeat(aString, true, aFeat.getName().endsWith("Proficiency"));
							modFeat(aString, true, true);
						}
					}
					else
					{
						final Feat aFeat = Globals.getFeatNamed(aString);
						if (aFeat != null)
						{
							final String featName = aFeat.getName();
							if ((!this.hasFeat(featName) && !this.hasFeatAutomatic(featName)))
							{
								setFeats(feats + aFeat.getCost());
								//modFeat(featName, true, featName.endsWith("Proficiency"));
								modFeat(aString, true, true);
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "Adding unknown feat: " + aString, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
						}
					}
				}
			}
			getAutoLanguages();
			getRacialFavoredClasses();

			final List templates = race.getTemplates(importing);
			for (int x = 0; x < templates.size(); ++x)
			{
				addTemplate(Globals.getTemplateNamed((String) templates.get(x)));
			}

			race.chooseLanguageAutos(importing);
		}

		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
		if (!importing)
		{
			getSpellList();
			race.globalChecks();
			adjustMoveRates();
			calcActiveBonuses();
		}
		setDirty(true);
	}

	public String getSpellRange(Spell aSpell, String aName, SpellInfo si)
	{
		String aRange = aSpell.getRange();
		String aSpellClass = "CLASS:" + aName;
		int iRange = 0;
		String aString = Globals.getGameModeSpellRangeFormula(aRange.toUpperCase());
		if (aRange.equalsIgnoreCase("CLOSE") && (aString == null))
		{
			aString = "((CL/2)*5)+25";
		}
		else if (aRange.equalsIgnoreCase("MEDIUM") && (aString == null))
		{
			aString = "(CL*10)+100";
		}
		else if (aRange.equalsIgnoreCase("LONG") && (aString == null))
		{
			aString = "(CL*40)+400";
		}
		if (aString != null)
		{
			List metaFeats = si.getFeatList();
			iRange = getVariableValue(aString, aSpellClass).intValue();
			if ((metaFeats != null) && !metaFeats.isEmpty())
			{
				for (Iterator e = metaFeats.iterator(); e.hasNext();)
				{
					Feat aFeat = (Feat) e.next();
					iRange += (int) aFeat.bonusTo("SPELL", "RANGE");
					int iMult = (int) aFeat.bonusTo("SPELL", "RANGEMULT");
					if (iMult > 0)
					{
						iRange = (iRange * iMult);
					}
				}
			}
			aRange += " (";
			aRange += String.valueOf(iRange) + " ";
			aRange += Globals.getGameModeMoveUnit() + ")";
		}
		return aRange;
	}

	public int getFirstSpellLevel(Spell aSpell)
	{
		int anInt = 0;
		for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
		{
			final PCClass aClass = (PCClass) iClass.next();
			String aKey = aClass.getSpellKey();
			int temp = aSpell.getFirstLevelForKey(aKey);
			anInt = Math.min(anInt, temp);
		}
		return anInt;
	}

	public void getSpellList()
	{
		// all non-spellcaster spells are added to race
		// so return if it's null
		if (race == null)
		{
			return;
		}
		race.clearCharacterSpells();
		addSpells(race);
		if (deity != null)
		{
			addSpells(deity);
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			addSpells(aClass);
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			addSpells(aFeat);
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			addSpells(aSkill);
		}
		// Domains are skipped - it's assumed that their spells are added to the first divine spellcasting

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				addSpells(eq);

				List aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						addSpells(eqMod);
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						addSpells(eqMod);
					}
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			addSpells(aTemplate);
		}
	}

	private void addSpells(PObject obj)
	{
		if ((race == null) || (obj == null) || (obj.getSpellList() == null) || obj.getSpellList().isEmpty())
		{
			return;
		}
		PObject owner;

		for (Iterator ri = obj.getSpellList().iterator(); ri.hasNext();)
		{
			PCSpell pcSpell = (PCSpell)ri.next();
			final String spellName = pcSpell.getName();
			final Spell aSpell = Globals.getSpellNamed(spellName);
			if (aSpell == null)
			{
				return;
			}

			final String castCount = pcSpell.getTimesPerDay();
			int spellLevel = -1;
			int times = 1;
			int slotLevel = 0;
			owner = race;
			if (castCount.startsWith("LEVEL=") || castCount.startsWith("LEVEL."))
			{
				spellLevel = Integer.parseInt(castCount.substring(6));
				slotLevel = spellLevel;
				if (obj instanceof PCClass)
				{
					owner = obj;
				}
			}
			else
			{
				times = getVariableValue(castCount, "").intValue();
			}

			final String book = pcSpell.getSpellbook();
			if(race.passesPreReqToGainForList(pcSpell.getPreReqList()))
			{
				List sList = owner.getCharacterSpell(aSpell, book, spellLevel);
				if (!sList.isEmpty())
				{
					continue;
				}
				final CharacterSpell cs = new CharacterSpell(owner, aSpell);
				cs.addInfo(slotLevel, times, book);
				addSpellBook(book);
				owner.addCharacterSpell(cs);
			}
		}
	}

	public int minXPForECL()
	{
		return minXPForLevel(getECL());
	}

	public int minXPForNextECL()
	{
		return minXPForLevel(getECL() + 1);
	}

	public int getECL()
	{
		int totalLevels = 0;
		totalLevels += totalNonMonsterLevels();
		totalLevels += totalHitDice();
		totalLevels += getLevelAdjustment();
		return totalLevels;
	}

	public int getLevelAdjustment()
	{
		int levelAdj = race.getLevelAdjustment();
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			PCTemplate aT = (PCTemplate) e.next();
			levelAdj += aT.getLevelAdjustment();
		}
		return levelAdj;
	}

	public int getTotalLevels()
	{
		int totalLevels = 0;

		totalLevels += totalNonMonsterLevels();

		// Monster hit dice count towards total levels -- was totalMonsterLevels()
		//  sage_sam changed 03 Dec 2002 for Bug #646816
		totalLevels += totalHitDice();
		return totalLevels;
	}

	public int getTotalPlayerLevels()
	{
		int totalLevels = 0;

		totalLevels += totalNonMonsterLevels();

		return totalLevels;
	}

// Removed. As we are un-hardcoding "PC", this had to go
//	public int totalPCLevels()
//	{
//		int totalLevels = 0;
//		for (Iterator e = classList.iterator(); e.hasNext();)
//		{
//			final PCClass aClass = (PCClass) e.next();
//			if (aClass.isPC())
//			{
//				totalLevels += aClass.getLevel();
//			}
//		}
//		return totalLevels;
//	}

// Removed. As we are un-hardcoding "NPC", this had to go
//	public int totalNPCLevels()
//	{
//		int totalLevels = 0;
//		for (Iterator e = classList.iterator(); e.hasNext();)
//		{
//			final PCClass aClass = (PCClass) e.next();
//			if (aClass.isNPC())
//			{
//				totalLevels += aClass.getLevel();
//			}
//		}
//		return totalLevels;
//	}

	public int totalNonMonsterLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			if (!aClass.isMonster())
			{
				totalLevels += aClass.getLevel();
			}
		}
		return totalLevels;
	}

	private int totalMonsterLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			if (aClass.isMonster())
			{
				totalLevels += aClass.getLevel();
			}
		}
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			totalLevels += cMod.getHitDie();
		}
		return totalLevels;
	}

	public int totalHitDice()
	{
		return race.hitDice() + totalMonsterLevels();
	}

	public int calcCR()
	{
		int CR = 0;
		final int rhd = race.hitDice();
		if (rhd > 0)
		{
			float hitDieRatio = (float) totalHitDice() / rhd;
			while (hitDieRatio >= 2)
			{
				CR += 2;
				hitDieRatio /= 2;
			}
			if (hitDieRatio >= 1.5)
			{
				++CR;
			}
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			CR += aClass.calcCR();
		}

		for (int x = 0; x < templateList.size(); ++x)
		{
			CR += ((PCTemplate) templateList.get(x)).getCR(getTotalLevels(), totalHitDice());
		}

		final int raceCR = race.getCR();
		if ((raceCR > 0) || (CR == 0))
		{
			CR += raceCR;
		}
		return CR;
	}

	public int getSR()
	{
		return calcSR(true);
	}

	int calcSR(boolean includeEquipment)
	{
		int SR = race.getSR();
		if (deity != null)
		{
			SR = Math.max(SR, deity.getSR());
		}
		for (Iterator i = companionModList.iterator(); i.hasNext();)
		{
			CompanionMod cMod = (CompanionMod) i.next();
			SR = Math.max(SR, cMod.getSR());
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass) i.next();
			SR = Math.max(SR, aClass.getSR());
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat) i.next();
			SR = Math.max(SR, aFeat.getSR());
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill) i.next();
			SR = Math.max(SR, aSkill.getSR());
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				SR = Math.max(aCD.getDomain().getSR(), SR);
			}
		}
		if (includeEquipment)
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment) e.next();
				if (eq.isEquipped())
				{
					SR = Math.max(SR, eq.getSR());

					List aList = eq.getEqModifierList(true);
					if (!aList.isEmpty())
					{
						for (Iterator e2 = aList.iterator(); e2.hasNext();)
						{
							final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
							SR = Math.max(SR, eqMod.getSR());
						}
					}
					aList = eq.getEqModifierList(false);
					if (!aList.isEmpty())
					{
						for (Iterator e2 = aList.iterator(); e2.hasNext();)
						{
							final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
							SR = Math.max(SR, eqMod.getSR());
						}
					}
				}
			}
		}
		final int atl = getTotalLevels();
		final int thd = totalHitDice();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate) i.next();
			SR = Math.max(SR, aTemplate.getSR(atl, thd));
		}
		SR += (int) getTotalBonusTo("MISC", "SR");
		//
		// This would make more sense to just not add in the first place...
		//
		if (!includeEquipment)
		{
			SR -= (int) getEquipmentBonusTo("MISC", "SR");
		}
		return SR;
	}

	private Map addStringToDRMap(Map drMap, String drString)
	{
		if (drString == null || drString.length() == 0)
		{
			return drMap;
		}
		final StringTokenizer aTok = new StringTokenizer(drString, "|");
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			int x = aString.indexOf('/');
			String key;
			String val;
			if (x > 0 && x < aString.length())
			{
				val = aString.substring(0, x);
				key = aString.substring(x + 1);
				// some DR: are DR:val/key and others are DR:val/+key,
				// so remove the + to make them equivalent
				if (key.length() > 0 && key.charAt(0) == '+')
				{
					key = key.substring(1);
				}
				int y = 0;
				Object obj = drMap.get(key);
				if (obj != null)
				{
					y = Integer.parseInt(obj.toString());
				}
				int z = getVariableValue(val, "").intValue();
				if (z > y)
				{
					drMap.put(key, String.valueOf(z));
				}
			}
		}
		return drMap;
	}

	public String calcDR()
	{
		Map drMap = new HashMap();
		drMap = addStringToDRMap(drMap, race.getDR());
		if (deity != null)
		{
			drMap = addStringToDRMap(drMap, deity.getDR());
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			drMap = addStringToDRMap(drMap, aClass.getDR());
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			drMap = addStringToDRMap(drMap, aFeat.getDR());
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			drMap = addStringToDRMap(drMap, aSkill.getDR());
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				drMap = addStringToDRMap(drMap, aCD.getDomain().getDR());
			}
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				drMap = addStringToDRMap(drMap, eq.getDR());

				List aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						drMap = addStringToDRMap(drMap, eqMod.getDR());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						drMap = addStringToDRMap(drMap, eqMod.getDR());
					}
				}
			}
		}
		final int atl = getTotalLevels();
		final int thd = totalHitDice();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) i.next();
			drMap = addStringToDRMap(drMap, aTemplate.getDR(atl, thd));
		}
		final StringBuffer DR = new StringBuffer();
		for (Iterator i = drMap.keySet().iterator(); i.hasNext();)
		{
			final String damageType = i.next().toString();
			String symbol = "";
			int protectionValue = Integer.parseInt(drMap.get(damageType).toString());
			int damageTypeAsInteger = 0;
			try
			{
				damageTypeAsInteger = Integer.parseInt(damageType);
			}
			catch (NumberFormatException e)
			{
				; //Do nothing, the damage type is some kind of special value like 'Silver'
			}
			if (damageTypeAsInteger > 0)
			{
				symbol = "+";
			}
			//
			// For some reason '+1' is coming out simply as '1', so need to tack on the
			// '+' again
			//
			protectionValue += (int) getTotalBonusTo("DR", symbol + damageType);
			if (DR.length() > 0)
			{
				DR.append(';');
			}
			DR.append(protectionValue).append('/').append(symbol).append(damageType);
		}
		return DR.toString();
	}

	/**
	 * Check if the character has the named Deity.
	 *
	 * @param deityName String name of the deity to check for.
	 * @return  <code>true</code> if the character has the Deity,
	 *	    <code>false</code> otherwise.
	 */
	private boolean hasDeity(String deityName)
	{
		final ArrayList aList = new ArrayList();
		aList.add("PREDEITY:" + deityName);
		return getRace().passesPreReqToGainForList(this, null, aList);
	}

	/**
	 * Check if the characterFeat ArrayList contains the named Feat.
	 *
	 * @param featName String name of the feat to check for.
	 * @return  <code>true</code> if the character has the feat,
	 *	    <code>false</code> otherwise.
	 */
	public boolean hasFeat(String featName)
	{
		return getFeatNamedInList(featList, featName) != null;
	}

	/**
	 * Check if the character has the feat 'virtually'
	 *
	 * @param featName String name of the feat to check for.
	 * @return  <code>true</code> if the character has the feat,
	 *	    <code>false</code> otherwise.
	 */
	public boolean hasFeatVirtual(String featName)
	{
		return getFeatNamedInList(vFeatList(), featName) != null;
	}

	/**
	 * Check if the character has the feat 'automatically'
	 *
	 * @param featName String name of the feat to check for.
	 * @return  <code>true</code> if the character has the feat,
	 *	    <code>false</code> otherwise.
	 */
	public boolean hasFeatAutomatic(String featName)
	{
		return getFeatNamedInList(featAutoList(), featName) != null;
	}

	/**
	 * Returns the Feat definition of a feat possessed by the character.
	 *
	 * @param featName String name of the feat to check for.
	 * @return  the Feat (not the CharacterFeat) searched for,
	 *	    <code>null</code> if not found.
	 */
	public Feat getFeatNamed(String featName)
	{
		return getFeatNamedInList(aggregateFeatList(), featName);
	}

	public Feat getFeatNamed(String featName, int featType)
	{
		return getFeatNamedInList(aggregateFeatList(), featName, featType);
	}

	public Feat getFeatAutomaticNamed(String featName)
	{
		return getFeatNamedInList(featAutoList(), featName);
	}

	public Feat getFeatNonAggregateNamed(String featName)
	{
		return getFeatNamedInList(featList, featName);
	}

	public static Feat getFeatNamedInList(List aFeatList, String featName)
	{
		return getFeatNamedInList(aFeatList, featName, -1);
	}

	private static Feat getFeatNamedInList(List aFeatList, String featName, int featType)
	{
		if (aFeatList.isEmpty())
		{
			return null;
		}
		for (Iterator e = aFeatList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat) e.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
			{
				if ((featType == -1) || (aFeat.getFeatType() == featType))
				{
					return aFeat;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the Feat definition searching by key (not name).
	 *
	 * @param featName String name of the feat to check for.
	 * @return  the Feat (not the CharacterFeat) searched for,
	 *	    <code>null</code> if not found.
	 */
	public Feat getFeatKeyed(String featName)
	{
		final List aList = aggregateFeatList();
		if (aList.isEmpty())
		{
			return null;
		}
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat) e.next();
			if (aFeat.getKeyName().equalsIgnoreCase(featName))
			{
				return aFeat;
			}
		}
		return null;
	}

	static boolean qualifiesForFeat(String featName)
	{
		final Feat aFeat = Globals.getFeatNamed(featName);
		if (aFeat != null)
		{
			return qualifiesForFeat(aFeat);
		}
		return false;
	}

	private static boolean qualifiesForFeat(Feat aFeat)
	{
		return aFeat.canBeSelectedBy();
	}

	/**
	 * Method will go through the list of classes that the PC has
	 * and see if they can cast spells of desired type at desired
	 * <b>spell level</b>.
	 *
	 * @param spellType Spell type to check for
	 * @param spellLevel Desired spell level
	 * @param minNumSpells Minimum number of spells at the desired spell level
	 * @return boolean
	 *
	 * author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 */
	boolean canCastSpellTypeLevel(final String spellType, final int spellLevel, final int minNumSpells)
	{
		Iterator iter = classList.iterator();
		while (iter.hasNext())
		{
			final PCClass aClass = (PCClass) iter.next();

			// Check for Constants.s_NONE just incase
			// a programmer sends in a "" string
			if (aClass.getSpellType().equalsIgnoreCase(spellType) &&
				!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
			{
				// See if the character can cast
				// at the required spell level
				if (aClass.getCastForLevel(aClass.getLevel(), spellLevel) >= minNumSpells)
				{
					return true;
				}
				// If they don't memorize spells and don't have
				// a CastList then they use something funky
				// like Power Points (psionic)
				if (!aClass.getMemorizeSpells() && aClass.zeroCastList())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster and of the desired caster level.
	 *
	 * @param minLevel
	 * @return boolean
	 */
	boolean isSpellCaster(int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel() + (int) getTotalBonusTo("PCLEVEL", aClass.getName()) >= minLevel)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Method will go through the list of classes that the player character has
	 * and see if they are a spell caster of the desired type and of the
	 * desired caster level.
	 *
	 * @param spellType
	 * @param minLevel
	 * @return boolean
	 */
	boolean isSpellCaster(String spellType, int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel() >= minLevel && aClass.getSpellType().equalsIgnoreCase(spellType))
			{
				return true;
			}
		}
		return false;
	}

	boolean isSpellCastermax(int maxLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel() <= maxLevel)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a Feat to a character, allowing sub-choices if necessary.
	 *
	 * @param featName  String name of the Feat to add.
	 * @param addIt	    <code>false</code> means the character must already have
	 *		    the feat (which only makes sense if it allows multiples);
	 *		    <code>true</code> means to add the feat (the only way
	 *		    to add new feats).
	 * @param addAll    <code>false</code> means allow sub-choices;
	 *		    <code>true</code> means no sub-choices, plus if it is
	 *		    a weapon type prof then add the weapon profs.
	 * @return Integer 1 or 0
	 */
	public int modFeat(String featName, boolean addIt, boolean addAll)
	{
		if (!importing)
		{
			getSpellList();
		}
		int retVal = addIt ? 1 : 0;
		String subName = "";

		// See if our choice is not auto or virtual
		Feat aFeat = getFeatNonAggregateNamed(featName);

		final String oldName = featName;

		// if a feat named featName doesn't exist, and featName
		// contains a (blah) descriptor, try removing it.
		if ((aFeat == null) && featName.endsWith(")"))
		{
			final int idx = featName.indexOf('(');
			subName = featName.substring(idx + 1, featName.lastIndexOf(')')); //we want what is inside the outermost parens.
			featName = featName.substring(0, idx).trim();
			aFeat = getFeatNonAggregateNamed(featName);
			if (addAll && (aFeat != null) && (subName.length() != 0))
			{
				addAll = false;
			}
		}

		// aFeat==null means we don't have this feat,
		// so we need to add it
		if (addIt && (aFeat == null))
		{
			// adding feat for first time
			aFeat = Globals.getFeatNamed(featName);
			if (aFeat == null)
			{
				aFeat = Globals.getFeatNamed(oldName);
				if (aFeat != null)
				{
					featName = oldName;
					subName = "";
				}
			}
			if (aFeat != null)
			{
				aFeat = (Feat) aFeat.clone();
			}
			else
			{
				Logging.errorPrint("Feat not found: " + oldName);
				return retVal;
			}
			addFeat(aFeat);
		}

		// could not find feat:
		// if addIt is false, this means character does not have feat
		// if addIt is true, this means no global feat exists
		if (aFeat == null)
		{
			return retVal;
		}

		// how many sub-choices to make
		double j = (aFeat.getAssociatedCount() * aFeat.getCost()) + feats;
		// process ADD tags from the feat definition
		if (!addIt)
		{
			aFeat.modAdds(addIt);
		}

		boolean canSetFeats = true;

		if (addIt || aFeat.isMultiples())
		{
			if (!addAll)
			{
				if ("".equals(subName))
				{
					// Allow sub-choices
					canSetFeats = !aFeat.modChoices();
				}
				else
				{
					if (addIt && (aFeat.isStacks() || !aFeat.containsAssociated(subName)))
					{
						aFeat.addAssociated(subName);
					}
					else if (!addIt && aFeat.containsAssociated(subName))
					{
						aFeat.removeAssociated(subName);
					}
				}
			}
			else
			{
				if (aFeat.getChoiceString().lastIndexOf('|') >= 0 && Globals.weaponTypesContains(aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf('|'))))
				{
					addWeaponProfToList(featList, aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf('|')), false);
				}
			}
		}

		if (aFeat.isMultiples() && !addAll)
		{
			retVal = (aFeat.getAssociatedCount() > 0) ? 1 : 0;
		}

		// process ADD tags from the feat definition
		if (addIt)
		{
			aFeat.modAdds(addIt);
		}

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;
		if (retVal == 0)
		{
			removed = featList.remove(aFeat);
		}

		if (!addIt && !aFeat.isMultiples() && removed)
		{
			j += aFeat.getCost();
		}
		else if (addIt && !aFeat.isMultiples())
		{
			j -= aFeat.getCost();
		}
		else
		{
			int associatedListSize = aFeat.getAssociatedCount();
			if (!featList.isEmpty())
			{
				for (Iterator e1 = featList.iterator(); e1.hasNext();)
				{
					final Feat myFeat = (Feat) e1.next();
					if (myFeat.getName().equalsIgnoreCase(aFeat.getName()))
					{
						associatedListSize = myFeat.getAssociatedCount();
					}
				}
			}

			j -= (associatedListSize * aFeat.getCost());
		}

		if (!addAll && canSetFeats)
		{
			setFeats(j);
		}

		setAutomaticFeatsStable(false);
		if (addIt && !importing)
		{
			aFeat.globalChecks(false);
		}

		return retVal;
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 */
	void modFeatsFromList(String aList, boolean addIt, boolean all)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",");
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			Feat aFeat = getFeatNamed(aString);
			StringTokenizer bTok = null;
			if (aFeat != null)
			{
				continue;
			}
			else
			{
				// does not already have feat
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
				{
					// could not find Feat, try trimming off contents of parenthesis
					bTok = new StringTokenizer(aString, "()", true);
					final String bString = bTok.nextToken();
					final int beginIndex = bString.length() + 1;
					final int endIndex = aString.lastIndexOf(')');
					if (beginIndex <= aString.length())
					{
						if (endIndex >= beginIndex)
						{
							bTok = new StringTokenizer(aString.substring(beginIndex, endIndex), ",");
						}
						else
						{
							bTok = new StringTokenizer(aString.substring(beginIndex), ",");
						}
					}
					else
					{
						bTok = null;
					}
					aString = bString.replace('(', ' ').replace(')', ' ').trim();
				}
				else
				{
					final Feat bFeat = getFeatNamed(aFeat.getName());
					if (bFeat != null)
					{
						aFeat = bFeat;
					}
					else
					{
						// add the Feat found, as a CharacterFeat
						aFeat = (Feat) aFeat.clone();
						addFeat(aFeat);
					}
				}
			}
			if (aFeat == null)
			{
				// if we still haven't found it, try a different string
				if (!addIt)
				{
					return;
				}
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
				{
					Logging.errorPrint("Feat not found in PlayerCharacter.modFeatsFromList: " + aString);
					return;
				}
				aFeat = (Feat) aFeat.clone();
				addFeat(aFeat);
			}
			if (bTok != null && bTok.hasMoreTokens())
			{
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
					if ("DEITYWEAPON".equals(aString))
					{
						WeaponProf wp = null;
						if (getDeity() != null)
						{
							wp = Globals.getWeaponProfNamed(getDeity().getFavoredWeapon());
						}
						if (wp != null)
						{
							if (addIt)
							{
								aFeat.addAssociated(wp.getName());
							}
							else
							{
								aFeat.removeAssociated(wp.getName());
							}
						}
					}
					else
					{
						if (addIt)
						{
							aFeat.addAssociated(aString);
						}
						else
						{
							aFeat.removeAssociated(aString);
						}
					}
				}
			}
			else
			{
				if (!all && !aFeat.isMultiples())
				{
					if (addIt)
					{
						setFeats(getFeats() + aFeat.getCost());
					}
					else
					{
						setFeats(getFeats() - aFeat.getCost());
					}
				}
				modFeat(aString, addIt, all);
			}
			if (aFeat.getName().endsWith("Weapon Proficiency"))
			{
				for (int e = 0; e < aFeat.getAssociatedCount(); ++e)
				{
					final String wprof = aFeat.getAssociated(e);
					WeaponProf wp = Globals.getWeaponProfNamed(wprof);
					if (wp != null)
					{
						addWeaponProfToList(featList, wprof, false);
					}
				}
			}
		}
		setAutomaticFeatsStable(false);
	}

	private boolean hasSkill(String skillName)
	{
		return (getSkillNamed(skillName) != null);
	}

	public Skill getSkillNamed(String skillName)
	{
		if (getSkillList().isEmpty())
		{
			return null;
		}
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill) e.next();
			if (aSkill.getName().equalsIgnoreCase(skillName))
			{
				return aSkill;
			}
		}
		return null;
	}

	public Skill getSkillKeyed(String skillName)
	{
		if (getSkillList().isEmpty())
		{
			return null;
		}
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill) e.next();
			if (aSkill.getKeyName().equalsIgnoreCase(skillName))
			{
				return aSkill;
			}
		}
		return null;
	}

	/**
	 * type 0 = attack bonus; 1 = check1; 2 = check2; 3 = check3; etc, last one is = Unarmed
	 */
	public double getBonus(int type, boolean addBonuses)
	{
		double bonus = 0;
		int totalClassLevels = 0;
		Map totalLvlMap = null;
		Map classLvlMap = null;

		if (type == 0)
		{
			bonus = race.getBAB();
		}
		else if (type <= SystemCollections.getUnmodifiableCheckList().size())
		{
			totalClassLevels = getTotalClassLevels();
			if (totalClassLevels > SettingsHandler.getGame().getChecksMaxLvl())
			{
				totalLvlMap = getTotalLevelHashMap();
				classLvlMap = getLevelHashMap(SettingsHandler.getGame().getChecksMaxLvl());
				setClassLevelsBrazenlyTo(classLvlMap); // insure class-levels total is below some value (e.g. 20)
			}
			bonus = getTotalBonusTo("CHECKS", "BASE." + SystemCollections.getUnmodifiableCheckList().get(type - 1).toString());
			//
			// now we see if this PC is a Familiar/Mount
			final PlayerCharacter nPC = getMasterPC();
			if ((nPC != null) && getCopyMasterCheck().length() > 0)
			{
				int masterBonus = 0;
				final PlayerCharacter curPC = Globals.getCurrentPC();
				Globals.setCurrentPC(nPC);
				// calculate the Masters Save Bonus
				masterBonus = nPC.calculateSaveBonus(type, SystemCollections.getUnmodifiableCheckList().get(type - 1).toString(), "BASE");
				Globals.setCurrentPC(curPC);
				String copyMasterCheck = getCopyMasterCheck();
				while (true)
				{
					int x = copyMasterCheck.indexOf("MASTER");
					if (x == -1)
					{
						break;
					}
					final String leftString = copyMasterCheck.substring(0, x);
					final String rightString = copyMasterCheck.substring(x + 6);
					copyMasterCheck = leftString + Integer.toString(masterBonus) + rightString;
				}
				masterBonus = getVariableValue(copyMasterCheck, "").intValue();
				// use masters save if better
				bonus = Math.max(bonus, masterBonus);
			}
			if (totalLvlMap != null)
			{
				setClassLevelsBrazenlyTo(totalLvlMap);
			}
		}

		if (addBonuses)
		{
			if (type == 0)
			{
				bonus += getTotalBonusTo("TOHIT", "TOHIT");
				bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
			}
			else if (type <= SystemCollections.getUnmodifiableCheckList().size())
			{
				bonus += getTotalBonusTo("CHECKS", SystemCollections.getUnmodifiableCheckList().get(type - 1).toString());
			}
			else
			{
				bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
			}
		}

		int cBonus = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			if (type == 0 || type > SystemCollections.getUnmodifiableCheckList().size())
			{
				cBonus += aClass.baseAttackBonus();
			}
		}
		bonus += cBonus;

		return bonus;
	}

	/**
	 * @return	Total base attack bonus as an int
	 **/
	public int baseAttackBonus()
	{
		final PlayerCharacter nPC = getMasterPC();
		if ((nPC != null) && getCopyMasterBAB().length() > 0)
		{
			int masterBAB = nPC.baseAttackBonus();
			String copyMasterBAB = getCopyMasterBAB();
			while (true)
			{
				int x = copyMasterBAB.indexOf("MASTER");
				if (x == -1)
				{
					break;
				}
				final String leftString = copyMasterBAB.substring(0, x);
				final String rightString = copyMasterBAB.substring(x + 6);
				copyMasterBAB = leftString + Integer.toString(masterBAB) + rightString;
			}
			masterBAB = getVariableValue(copyMasterBAB, "").intValue();
			return masterBAB;
		}
		int totalClassLevels = getTotalClassLevels();
		Map totalLvlMap = null;
		Map classLvlMap = null;

		if (totalClassLevels > SettingsHandler.getGame().getBabMaxLvl())
		{
			totalLvlMap = getTotalLevelHashMap();
			classLvlMap = getLevelHashMap(SettingsHandler.getGame().getBabMaxLvl());
			// insure total class-levels below some value (e.g. 20)
			setClassLevelsBrazenlyTo(classLvlMap);
		}
		int bab = (int) getTotalBonusTo("COMBAT", "BAB");
		if (totalLvlMap != null)
		{
			setClassLevelsBrazenlyTo(totalLvlMap);
		}
		return bab;
	}

	/**
	 * Sets the source of granted domains
	 **/
	public void addDomainSource(String aType, String aName, int aLevel, int dNum)
	{
		String aString = aType + "|" + aName + "|" + aLevel;
		String sNum = Integer.toString(dNum);
		domainSourceMap.put(aString, sNum);
	}

	/**
	 * For now, just return the first source with more than 0 domains
	 **/
	public String getDomainSource()
	{
		for (Iterator i = domainSourceMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			String aVal = domainSourceMap.get(aKey).toString();
			int aNum = Integer.parseInt(aVal);
			if (aNum > 0)
			{
				return aKey;
			}
		}
		return "";
	}

	/**
	 * @return characterDomainList
	 **/
	public List getCharacterDomainList()
	{
		return characterDomainList;
	}

	/**
	 * @return the number of Character Domains used
	 **/
	public int getCharacterDomainUsed()
	{
		return characterDomainList.size();
	}

	/**
	 * @return the number of Character Domains possible
	 **/
	public int getMaxCharacterDomains()
	{
		return (int) getTotalBonusTo("DOMAIN", "NUMBER");
	}

	/**
	 * adds CharacterDomain to list
	 **/
	public void addCharacterDomain(CharacterDomain aCD)
	{
		if ((aCD != null) &&
			!characterDomainList.contains(aCD) &&
			(aCD.getDomain() != null))
		{
			characterDomainList.add(aCD);
		}
	}

	/**
	 * gets first domain with remaining slots, creates an CharacterDomain
	 * object, sets all the correct info and returns it
	 **/
	public CharacterDomain getNewCharacterDomain()
	{
		String sDom = getDomainSource();
		if (sDom.length() > 0)
		{
			StringTokenizer aTok = new StringTokenizer(sDom, "|");
			String aType = aTok.nextToken();
			String aName = aTok.nextToken();
			int aLevel = Integer.parseInt(aTok.nextToken());
			CharacterDomain aCD = new CharacterDomain();
			if (aType.equalsIgnoreCase("PCClass"))
			{
				aCD.setFromPCClass(true);
			}
			else
			{
				aCD.setFromPCClass(true);
			}
			aCD.setObjectName(aName);
			aCD.setLevel(aLevel);
			return aCD;
		}
		return null;
	}

	/**
	 * Removes a CharacterDomain
	 **/
	public void removeCharacterDomain(CharacterDomain aCD)
	{
		if (!characterDomainList.isEmpty())
		{
			characterDomainList.remove(aCD);
		}
	}

	void validateCharacterDomains()
	{
		if (!importing)
		{
			getSpellList();
		}
		for (int i = characterDomainList.size() - 1; i >= 0; --i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			if (!aCD.isDomainValidFor(this))
			{
				characterDomainList.remove(aCD);
			}
		}
	}

	/**
	 * return the index of CharacterDomain matching domainName
	 * else return -1
	 **/
	int getCharacterDomainIndex(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if ((aDomain != null) && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return i;
			}
		}
		return -1;
	}

	public Domain getCharacterDomainNamed(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if ((aDomain != null) && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return aCD.getDomain();
			}
		}
		return null;
	}

	public CharacterDomain getCharacterDomainForDomain(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if ((aDomain != null) && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return aCD;
			}
		}
		return null;
	}

	/**
	 * Why are we doing this? Just add the domain!
	 **/
	public int indexOfFirstEmptyCharacterDomain()
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			if (aCD.getDomain() == null)
			{
				return i;
			}
		}
		return -1;
	}

	public boolean canSelectDeity(Deity aDeity)
	{
		if (aDeity == null)
		{
			// deity = null;  Removed 11/3/2002 - dhibbs
			return false;
		}
		return aDeity.canBeSelectedBy(classList, alignment);
	}

	public boolean setDeity(Deity aDeity)
	{
		if (!canSelectDeity(aDeity))
		{
			return false;
		}
		deity = aDeity;
		if (!importing)
		{
			getSpellList();
			deity.globalChecks();
		}
		calcActiveBonuses();
		return true;
	}

	public boolean hasSpecialAbility(String abilityName)
	{
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			if (((SpecialAbility) e.next()).getName().equalsIgnoreCase(abilityName))
			{
				return true;
			}
		}
		return false;
	}

	boolean hasSpecialAbility(SpecialAbility sa)
	{
		final String saName = sa.getName();
		final String saDesc = sa.getSADesc();
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			final SpecialAbility saFromList = (SpecialAbility) e.next();
			if (saFromList.getName().equalsIgnoreCase(saName) && saFromList.getSADesc().equalsIgnoreCase(saDesc))
			{
				return true;
			}
		}
		return false;
	}

	public SortedSet getAutoLanguages()
	{
		// find list of all possible langauges
		boolean clearRacials = false;

		final SortedSet autoLangs = new TreeSet();

		// Search for a CLEAR in the list and
		// if found clear all BEFORE but not AFTER it.
		// ---arcady June 1, 2002

		for (Iterator e = templateAutoLanguages.iterator(); e.hasNext();)
		{
			Language aLang = (Language) e.next();
			String aString = aLang.toString();
			if (".CLEARRACIAL".equals(aString))
			{
				clearRacials = true;
				getLanguagesList().removeAll(getRace().getAutoLanguages());
			}
			else if (".CLEARALL".equals(aString) || ".CLEAR".equals(aString))
			{
				clearRacials = true;
				autoLangs.clear();
				getLanguagesList().clear();
			}
			else if (".CLEARTEMPLATES".equals(aString))
			{
				autoLangs.clear();
				getLanguagesList().removeAll(templateAutoLanguages);
			}
			else
			{
				autoLangs.add(aLang);
			}
		}
		if (!clearRacials)
		{
			autoLangs.addAll(getRace().getAutoLanguages());
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			autoLangs.addAll(aClass.getAutoLanguages());
		}

		if (deity != null)
		{
			autoLangs.addAll(deity.getAutoLanguages());
		}

		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			final Feat aFeat = (Feat) i.next();
			autoLangs.addAll(aFeat.getAutoLanguages());
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill) i.next();
			autoLangs.addAll(aSkill.getAutoLanguages());
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) i.next();
			if (aCD.getDomain() != null)
			{
				autoLangs.addAll(aCD.getDomain().getAutoLanguages());
			}
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				autoLangs.addAll(eq.getAutoLanguages());

				List aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						autoLangs.addAll(eqMod.getAutoLanguages());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						autoLangs.addAll(eqMod.getAutoLanguages());
					}
				}
			}
		}

		getLanguagesList().addAll(autoLangs);
		return autoLangs;
	}

	public void removeNaturalWeapons(PObject obj)
	{
		for (Iterator e = obj.getNaturalWeapons().iterator(); e.hasNext();)
		{
			// Need to make sure weapons are removed from
			// equip sets as well, or they will get added back
			// to the character.  sage_sam 20 March 2003
			final Equipment weapon = (Equipment) e.next();
			removeEquipment(weapon);
			delEquipSetItem(weapon);
		}
	}

	public void addNaturalWeapons(PObject obj)
	{
		equipmentListAddAll(obj.getNaturalWeapons());
	}

	private SortedSet getRacialFavoredClasses()
	{
		racialFavoredClass = getRace().getFavoredClass();
		if (racialFavoredClass.startsWith("CHOOSE:"))
		{
			for (; ;)
			{
				String classChoice = Globals.chooseFromList("Select favored class", racialFavoredClass.substring(7), null, 1);
				if (classChoice != null)
				{
					racialFavoredClass = classChoice;
					break;
				}
			}
		}
		if (!addFavoredClass(racialFavoredClass))
		{
			racialFavoredClass = "";
		}
		return favoredClasses;
	}

	boolean addFavoredClass(String aString)
	{
		if ((aString.length() != 0) && !favoredClasses.contains(aString))
		{
			favoredClasses.add(aString);
			return true;
		}
		return false;
	}

	boolean removeFavoredClass(String aString)
	{
		if (favoredClasses.contains(aString))
		{
			favoredClasses.remove(aString);
			return true;
		}
		return false;
	}

	/*
	 * renaming to standard convetion
	 * due to refactoring of export
	 *
	 * Build on-the-fly so removing templates doesn't mess up favored list
	 *
	 * author: Thomas Behr 08-03-02
	 */
	public SortedSet getFavoredClasses()
	{
		final SortedSet favored = new TreeSet(favoredClasses);
		for (int i = 0; i < templateList.size(); ++i)
		{
			final PCTemplate template = (PCTemplate) templateList.get(i);
			final String favoredClass = template.getFavoredClass();
			if ((favoredClass.length() != 0) && !favored.contains(favoredClass))
			{
				favored.add(favoredClass);
			}
		}
		return favored;
	}

	public double multiclassXPMultiplier()
	{
		final SortedSet unfavoredClasses = new TreeSet();
		final SortedSet aList = getFavoredClasses();
		boolean hasAny = false;
		String maxClass = "";
		String secondClass = "";
		int maxClassLevel = 0;
		int secondClassLevel = 0;
		int xpPenalty = 0;
		double xpMultiplier = 1.0;

		if (aList.contains("Any"))
		{
			hasAny = true;
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			if (!aList.contains(aClass.getDisplayClassName()) && (!aList.contains(aClass.toString())) && aClass.hasXPPenalty())
			{
				unfavoredClasses.add(aClass.getDisplayClassName());
				if (aClass.getLevel() > maxClassLevel)
				{
					if (hasAny)
					{
						secondClassLevel = maxClassLevel;
						secondClass = maxClass;
					}
					maxClassLevel = aClass.getLevel();
					maxClass = aClass.getDisplayClassName();
				}
				else if ((aClass.getLevel() > secondClassLevel) && (hasAny))
				{
					secondClassLevel = aClass.getLevel();
					secondClass = aClass.getDisplayClassName();
				}
			}
		}

		if ((hasAny) && (secondClassLevel > 0))
		{
			maxClassLevel = secondClassLevel;
			unfavoredClasses.remove(maxClass);
			maxClass = secondClass;
		}

		if (maxClassLevel > 0)
		{
			unfavoredClasses.remove(maxClass);

			for (Iterator e = unfavoredClasses.iterator(); e.hasNext();)
			{
				final PCClass aClass = getClassDisplayNamed((String) e.next());
				if (aClass != null)
				{
					if ((maxClassLevel - (aClass.getLevel())) > 1)
					{
						++xpPenalty;
					}
				}
			}

			xpMultiplier = 1.0 - (xpPenalty * 0.2);
			if (xpMultiplier < 0)
			{
				xpMultiplier = 0;
			}
		}
		return xpMultiplier;
	}

	/**
	 * Left alone for a while, will remove in a month unless it is used.
	 * Returns a SortedSet list of bonus languages
	 * gained from race, class, and templates.
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 **/
	public SortedSet getBonusLanguages(boolean removeKnown)
	{
		final SortedSet bonusLangs = new TreeSet();
		// Two of them to avoid ConcurrentModificationException
		Iterator e;

		bonusLangs.addAll(getRace().getLanguageBonus());
		bonusLangs.addAll(templateLanguages); // add from templates. sloppy?

		Collection classBonusLangs;
		for (e = classList.iterator(); e.hasNext();)
		{
			classBonusLangs = ((PCClass) e.next()).getLanguageBonus();
			bonusLangs.addAll(classBonusLangs);
		}

		// extractLanguageListNames takes a list of string as an
		// argument. But bonusLangs array contains Language Objects
		//bonusLangsb = Globals.extractLanguageListNames(bonusLangs);

		if (removeKnown)
		{
			bonusLangs.removeAll(getLanguagesList());
		}
		return bonusLangs;
	}

	public void addLanguage(String aString)
	{
		final Language aLang = Globals.getLanguageNamed(aString);
		if (aLang != null)
		{
			if (!getLanguagesList().contains(aLang))
			{
				getLanguagesList().add(aLang);
			}
		}
	}

	void addFreeLanguage(String aString)
	{
		final Language aLang = Globals.getLanguageNamed(aString);
		if (aLang != null)
		{
			if (!getLanguagesList().contains(aLang))
			{
				getLanguagesList().add(aLang);
				++freeLangs;
			}
		}
	}

	public int languageNum(boolean includeSpeakLanguage)
	{
		int i = (int) getStatBonusTo("LANG", "BONUS");
		final Race pcRace = getRace();
		if (i < 0)
		{
			i = 0;
		}
		if (includeSpeakLanguage)
		{
			for (Iterator a = getSkillList().iterator(); a.hasNext();)
			{
				Skill aSkill = (Skill) a.next();
				if (aSkill.getChoiceString().indexOf("Language") >= 0)
				{
					i += aSkill.getTotalRank().intValue();
				}
			}
		}
		if (pcRace != null)
		{
			i += pcRace.getLangNum() + (int) getTotalBonusTo("LANGUAGES", "NUMBER");
		}

		//
		// Check all classes for ADD:LANGUAGE
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			final int classLevel = aClass.getLevel();
			final ArrayList levelAbilityList = aClass.getLevelAbilityList();
			if (levelAbilityList != null)
			{
				for (int x = levelAbilityList.size() - 1; x >= 0; --x)
				{
					final Object obj = levelAbilityList.get(x);
					if (obj instanceof LevelAbilityLanguage)
					{
						if (classLevel >= ((LevelAbilityLanguage) obj).level())
						{
							++i;
						}
					}
				}
			}
		}

		i += freeLangs;
		return i;
	}

	private SortedSet addWeaponProfsLists(PObject obj, List aList, SortedSet aSet, List aFeatList, boolean addIt)
	{
		if (aList == null || aList.isEmpty())
		{
			return aSet;
		}
		final String sizeString = "FDTSMLHGC";
		for (Iterator e1 = aList.iterator(); e1.hasNext();)
		{
			String aString = (String) e1.next();
			final int idx = aString.indexOf('[');
			if (idx >= 0)
			{
				final StringTokenizer bTok = new StringTokenizer(aString.substring(idx + 1), "[]");
				List preReqList = new ArrayList();
				while (bTok.hasMoreTokens())
				{
					preReqList.add(bTok.nextToken());
				}
				aString = aString.substring(0, idx);
				if (preReqList.size() != 0)
				{
					if (!obj.passesPreReqToGainForList(preReqList))
					{
						continue;
					}
				}
			}

			final int lastComma = aString.lastIndexOf(',');
			boolean flag = (lastComma < 0);
			if (!flag && (race != null))
			{
				final String eString = aString.substring(lastComma + 1);
				final int s = sizeInt();
				for (int i = 0; i < eString.length(); ++i)
				{
					if (sizeString.lastIndexOf(eString.charAt(i)) == s)
					{
						flag = true;
						break;
					}
				}
				aString = aString.substring(0, lastComma);
			}
			if (flag)
			{
				// 1.	Look for an exact equipment match

				// Can this be done?
				Equipment eq = Globals.getEquipmentKeyed(aString);
				//Equipment eq = Globals.getEquipmentNamed(aString);
				if (eq != null)
				{
					// Found an exact equipment match; use it
					if (addIt)
					{
						aSet.add(aString);
						addWeaponProfToList(aFeatList, aString, true);
					}
				}
				else // No exact equipment match found.
				{
					// Set up a place to store located profs
					List addWPs = new ArrayList();

					// Check for type separators.
					boolean dotsFound = aString.indexOf(".") >= 0;

					// 2.  If no dots found, try to find a weapon proficiency specification
					boolean loadedByProfs = false;
					if (!dotsFound)
					{
						// Look for an exact proficiency match
						WeaponProf prof = Globals.getWeaponProfKeyed(aString);
						if (prof != null)
						{
							addWPs.add(aString);
							loadedByProfs = true;
						}
						// Look for proficiency type matches
						else
						{
							Collection listFromWPType = Globals.getAllWeaponProfsOfType(aString);
							if ((listFromWPType != null) && (!listFromWPType.isEmpty()))
							{
								for (Iterator i = listFromWPType.iterator(); i.hasNext();)
								{
									addWPs.add(i.next().toString());
								}
								loadedByProfs = true;
							}
						}
					}

					// 3.  If dots found (or no profs found), assume weapon types
					if (dotsFound || !loadedByProfs)
					{

						String desiredTypes = "Weapon." + aString;
						List listFromEquipmentType = Globals.getEquipmentOfType(Globals.getEquipmentList(), desiredTypes, "");

						if ((listFromEquipmentType != null) && (!listFromEquipmentType.isEmpty()))
						{
							for (Iterator i = listFromEquipmentType.iterator(); i.hasNext();)
							{
								String bString = ((Equipment) i.next()).profName();
								addWPs.add(bString);
							}
						}
					}

					// Add the located weapon profs to the prof list
					for (Iterator i = addWPs.iterator(); i.hasNext();)
					{
						if (addIt)
						{
							aSet.add(aString);
							addWeaponProfToList(aFeatList, (String) i.next(), true);
						}
					}

				} // end else( No exact equipment match found )

			} // end if(flag)

		} // end for()

		// return result set
		return aSet;
	}

	private SortedSet getAutoWeaponProfs(List aFeatList)
	{
		SortedSet results = new TreeSet();
		final Race aRace = getRace();
		//
		// Add race-grantedweapon proficiencies
		//
		if (aRace != null)
		{
			results = addWeaponProfsLists(aRace, aRace.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aRace.getSelectedWeaponProfBonusCount(); ++i)
			{
				String aString = aRace.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
			aRace.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
		}

		//
		// Add template-granted weapon proficiencies
		//
		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			results = addWeaponProfsLists(aTemplate, aTemplate.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aTemplate.getSelectedWeaponProfBonusCount(); ++i)
			{
				final String aString = aTemplate.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
			aTemplate.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
		}

		//
		// Add class-granted weapon proficiencies
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			results = addWeaponProfsLists(aClass, aClass.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aClass.getSelectedWeaponProfBonusCount(); ++i)
			{
				final String aString = aClass.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
			aClass.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
		}

		//
		// Add feat-granted weapon proficiencies
		//
		setAggregateFeatsStable(false);
		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat) e.next();
			results = addWeaponProfsLists(aFeat, aFeat.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aFeat.getSelectedWeaponProfBonusCount(); ++i)
			{
				final String aString = aFeat.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
			aFeat.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
		}
		//
		// Add skill-granted weapon proficiencies
		//
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill) e.next();
			results = addWeaponProfsLists(aSkill, aSkill.getWeaponProfAutos(), results, aFeatList, true);
			aSkill.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
		}
		//
		// Add equipment-granted weapon proficiencies
		//
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				results = addWeaponProfsLists(eq, eq.getWeaponProfAutos(), results, aFeatList, true);
				eq.addAutoTagsToList("WEAPONPROF", (TreeSet) results);

				List aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						results = addWeaponProfsLists(eqMod, eqMod.getWeaponProfAutos(), results, aFeatList, true);
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						results = addWeaponProfsLists(eqMod, eqMod.getWeaponProfAutos(), results, aFeatList, true);
					}
				}
			}
		}
		//
		// Add deity-granted weapon proficiencies
		//
		if (deity != null)
		{
			results = addWeaponProfsLists(deity, deity.getWeaponProfAutos(), results, aFeatList, true);
			deity.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
		}

		//
		// Add domain-granted weapon proficiencies
		//
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain) e.next();
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				results = addWeaponProfsLists(aDomain, aDomain.getWeaponProfAutos(), results, aFeatList, true);
				for (int i = 0; i < aDomain.getSelectedWeaponProfBonusCount(); ++i)
				{
					final String aString = aDomain.getSelectedWeaponProfBonus(i);
					results.add(aString);
					addWeaponProfToList(aFeatList, aString, true);
				}
				aDomain.addAutoTagsToList("WEAPONPROF", (TreeSet) results);
			}
		}

		//
		// Parse though aggregate feat list, looking for any feats that grant weapon proficiencies
		//
//		addFeatProfs(getStableAggregateFeatList(), aFeatList, results);
//		addFeatProfs(getStableAutomaticFeatList(), aFeatList, results);
		weaponProfList.addAll(results);
		return results;
	}

	public void addWeaponProf(String aString)
	{
		addWeaponProfToList(featList, aString, false);
	}

	private void addWeaponProfToList(List aFeatList, String aString, boolean isAuto)
	{
		if (aString.startsWith("WEAPONTYPE=") || aString.startsWith("WEAPONTYPE."))
		{
			final List weapList = Globals.getEquipmentOfType(Globals.getEquipmentList(), "WEAPON." + aString.substring(11), "");
			if (weapList.size() != 0)
			{
				for (Iterator e = weapList.iterator(); e.hasNext();)
				{
					final Equipment weap = (Equipment) e.next();
					final WeaponProf aProf = Globals.getWeaponProfNamed(weap.profName());
					if (aProf != null)
					{
						addWeaponProfToList(aFeatList, aProf.getName(), isAuto);
					}
				}
			}
			return;
		}
		// Add all weapons of type aString
		// (eg: Simple, Martial, Exotic, Ranged, etc.)
		else if (Globals.weaponTypesContains(aString))
		{
			Collection weaponProfs = Globals.getAllWeaponProfsOfType(aString);
			for (Iterator e = weaponProfs.iterator(); e.hasNext();)
			{
				final WeaponProf weaponProf = (WeaponProf) e.next();
				addWeaponProfToList(aFeatList, weaponProf.getName(), isAuto);
			}
			return;
		}

		final WeaponProf wp = Globals.getWeaponProfNamed(aString);
		if (wp != null)
		{
			String featName = wp.getType() + " Weapon Proficiency";
			for (; ;)
			{
				Feat aFeat = getFeatNamedInList(aFeatList, featName);
				if (aFeat != null)
				{
					// No need to add to list,
					// if multiples not allowed
					if (aFeat.isMultiples())
					{
						if (!aFeat.containsAssociated(aString))
						{
							aFeat.addAssociated(aString);
							aFeat.sortAssociated();
						}
					}
				}
				else
				{
					aFeat = Globals.getFeatNamed(featName);
					if (aFeat != null)
					{
						if (isAuto && !aFeat.isMultiples() && !"Weapon Proficiency".equalsIgnoreCase(featName))
						{
							//
							// Only use catch-all if haven't taken feat that supercedes it
							//
							if (getFeatNamedInList(featList, featName) == null)
							{
								featName = "Weapon Proficiency";
								continue;
							}
							break;	// Don't add auto-feat
						}

						aFeat = (Feat) aFeat.clone();
						aFeat.addAssociated(aString);
						if (isAuto)
						{
							aFeat.setFeatType(Feat.FEAT_AUTOMATIC);
						}
						aFeatList.add(aFeat);
					}
					else
					{
						if (!wp.isType("NATURAL"))
						{
							Logging.errorPrint("Weaponprof feat not found: " + featName + ":" + aString);
						}
					}
				}
				break;
			}
		}

		if (!weaponProfList.contains(aString))
		{
			weaponProfList.add(aString);
		}
	}

	public List aggregateSpellList(String aType, String school, String subschool, int minLevel, int maxLevel)
	{
		final List aArrayList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			String cName = aClass.getKeyName();
			if (aClass.getCastAs().length() > 0)
			{
				cName = aClass.getCastAs();
			}

			if ("Any".equalsIgnoreCase(aType) || aType.equalsIgnoreCase(aClass.getSpellType()) || aType.equalsIgnoreCase(cName))
			{
				for (int a = minLevel; a <= maxLevel; a++)
				{
					List aList = aClass.getCharacterSpell(null, "", a);
					if (aList.isEmpty())
					{
						continue;
					}
					for (Iterator i = aList.iterator(); i.hasNext();)
					{
						final CharacterSpell cs = (CharacterSpell) i.next();
						final Spell aSpell = cs.getSpell();
						if (((school.length() == 0 || school.equalsIgnoreCase(aSpell.getSchool())) || (subschool.length() == 0 || subschool.equalsIgnoreCase(aSpell.getSubschool()))))
						{
							aArrayList.add(cs.getSpell());
						}
					}
				}
			}
		}
		return aArrayList;
	}

	public Float totalWeight()
	{
		float totalWeight = 0;
		final Float floatZero = new Float(0);
		boolean firstClothing = true;
		if (equipmentList.isEmpty())
		{
			return floatZero;
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			// Loop through the list of top
			if (eq.getCarried().compareTo(floatZero) > 0 && eq.getParent() == null)
			{
				if (eq.getChildCount() > 0)
				{
					totalWeight += eq.getWeightAsDouble() + eq.getContainedWeight().floatValue();
				}
				else
				{
					if (firstClothing && eq.isEquipped() && eq.isType("CLOTHING"))
					{
						//The first equipped set of clothing should have a weight of 0. Feature #437410
						firstClothing = false;
						totalWeight += eq.getWeightAsDouble() * Math.max(eq.getCarried().floatValue() - 1, 0);
					}
					else
					{
						totalWeight += eq.getWeightAsDouble() * eq.getCarried().floatValue();
					}
				}
			}
		}
		return new Float(totalWeight);
	}

	public BigDecimal totalValue()
	{
		BigDecimal totalValue = BigDecimalHelper.ZERO;
		if (!getEquipmentMasterList().isEmpty())
		{
			for (Iterator e = getEquipmentMasterList().iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment) e.next();
				totalValue = totalValue.add(eq.getCost().multiply(new BigDecimal(eq.qty())));
			}
		}
		return totalValue;
	}

	public boolean isProficientWith(Equipment eq)
	{
		if (eq.isWeapon())
		{
			final WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
			return eq.isNatural() || ((wp != null) && hasWeaponProfNamed(wp.getName()));
		}
		else if (eq.isArmor())
		{
			return isProficientWithArmor(eq);
		}
		else if (eq.isShield())
		{
			return hasFeat(Constants.s_ShieldProficiency) || hasFeatAutomatic(Constants.s_ShieldProficiency);
		}
		return false;
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching
	 * the supplied type and status criteria.
	 *
	 * @param typeName The type of equipment to be selected
	 * @param status The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	List getEquipmentOfType(String typeName, int status)
	{
		return getEquipmentOfType(typeName, "", status);
	}

	/**
	 * Retrieves an unsorted list of the character's equipment matching
	 * the supplied type, sub type and status criteria.
	 *
	 * @param typeName The type of equipment to be selected
	 * @param subtypeName The subtype of equipment to be selected (empty string for no subtype)
	 * @param status The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects.
	 */
	public List getEquipmentOfType(String typeName, String subtypeName, int status)
	{
		final List aArrayList = new ArrayList();
		if (equipmentList.isEmpty())
		{
			return aArrayList;
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.typeStringContains(typeName) && ("".equals(subtypeName) || eq.typeStringContains(subtypeName)) && (status == 3 || (status == 2 && !eq.isEquipped()) || (status == 1 && eq.isEquipped())))
			{
				aArrayList.add(eq);
			}
		}
		return aArrayList;
	}

	/**
	 * Retrieves a list, sorted in output order, of the character's equipment
	 * matching the supplied type and status criteria. This list is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name.
	 * Note hidden items (outputIndex = -1) are not included in this list.
	 *
	 * @param typeName The type of equipment to be selected
	 * @param status The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects in output order.
	 */
	public List getEquipmentOfTypeInOutputOrder(String typeName, int status)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status), Constants.MERGE_ALL);
	}

	/**
	 * @param typeName The type of equipment to be selected
	 * @param status The required status
	 * @param merge What type of merge for like equipment
	 * @return An ArrayList of equipment objects
	 **/
	public List getEquipmentOfTypeInOutputOrder(String typeName, int status, int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, status), merge);
	}

	/**
	 * @param typeName The type of equipment to be selected
	 * @param subtypeName The subtype of equipment to be selected
	 * @param status The required status
	 * @param merge What sort of merging should occur
	 * @return An ArrayList of equipment objects
	 */
	public List getEquipmentOfTypeInOutputOrder(String typeName, String subtypeName, int status, int merge)
	{
		return sortEquipmentList(getEquipmentOfType(typeName, subtypeName, status), Constants.MERGE_ALL);
	}

	public void adjustGold(double delta)
	{
		//I don't really like this hack, but setScale just won't work right...
		gold = new BigDecimal(gold.doubleValue() + delta).divide(BIG_ONE, 2, BigDecimal.ROUND_HALF_EVEN);
	}

	public void determinePrimaryOffWeapon()
	{
		primaryWeapons.clear();
		secondaryWeapons.clear();
		if (equipmentList.isEmpty())
		{
			return;
		}
		final List unequippedPrimary = new ArrayList();
		final List unequippedSecondary = new ArrayList();
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (!eq.isWeapon() || (eq.getSlots() < 1))
			{
				continue;
			}
			boolean isEquipped = eq.isEquipped();
			if ((eq.getLocation() == Equipment.EQUIPPED_PRIMARY) || ((eq.getLocation() == Equipment.EQUIPPED_BOTH) && primaryWeapons.isEmpty()) || (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
			{
				if (isEquipped)
				{
					primaryWeapons.add(eq);
				}
				else
				{
					unequippedPrimary.add(eq);
				}
			}
			else if ((eq.getLocation() == Equipment.EQUIPPED_BOTH) && !primaryWeapons.isEmpty())
			{
				if (isEquipped)
				{
					secondaryWeapons.add(eq);
				}
				else
				{
					unequippedSecondary.add(eq);
				}
			}
			if (eq.getLocation() == Equipment.EQUIPPED_SECONDARY)
			{
				if (isEquipped)
				{
					secondaryWeapons.add(eq);
				}
				else
				{
					unequippedSecondary.add(eq);
				}
			}
			if (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)
			{
				for (int y = 0; y < eq.getNumberEquipped() - 1; ++y)
				{
					if (isEquipped)
					{
						secondaryWeapons.add(eq);
					}
					else
					{
						unequippedSecondary.add(eq);
					}
				}
			}
		}

		if (SettingsHandler.getTreatInHandAsEquippedForAttacks())
		{
			if (unequippedPrimary.size() != 0)
			{
				primaryWeapons.addAll(unequippedPrimary);
			}
			if (unequippedSecondary.size() != 0)
			{
				secondaryWeapons.addAll(unequippedSecondary);
			}
		}
	}

	/**
	 * Get the total bonus from Stats, Size, Age, Alignment, Classes,
	 * companions, Equipment, Feats, Templates, Domains, Races, etc
	 * This value is taken from an already populated HashMap for speed
	 *
	 * @param bonusType		Type of bonus ("COMBAT" or "SKILL")
	 * @param bonusName		Name of bonus ("AC"	or "Hide");
	 **/
	public double getTotalBonusTo(String bonusType, String bonusName)
	{
		String prefix = new StringBuffer(bonusType.toUpperCase()).append('.').append(bonusName.toUpperCase()).toString();
		return sumActiveBonusMap(prefix);
	}

	/**
	 * Creates the activeBonusList which is used to calculate all
	 * the bonuses to a PC
	 **/
	public void calcActiveBonuses()
	{
		if (isImporting() || (race == null))
		{
			return;
		}

		// build the Variable HashSet
		buildVariableSet();

		// First we clear the current list
		clearActiveBonuses();

		// walk through all the possible bonus granters
		// to build up the active list of bonuses
		//
		calcStatBonuses();
		calcSizeAdjustmentBonuses();
		calcAgeBonuses();
		calcCheckBonuses();
		calcAlignmentBonuses();
		calcFeatBonuses();
		calcClassBonuses();
		calcCompanionModBonuses();
		calcEquipmentBonuses();
		calcTemplateBonuses();
		calcDomainBonuses();
		calcRaceBonuses();
		calcDeityBonuses();
		calcSkillBonuses();
		//calcWeaponProfBonuses();
		//calcArmorProfBonuses();
		if (getUseTempMods())
		{
			calcTempBonuses();
		}
		//
		// Now build the activeBonusMap from all the bonuses
		//
		buildActiveBonusMap();
	}

	/**
	 * Lists all the tokens that match prefix with associated values
	 **/
	public String listBonusesFor(String prefix)
	{
		StringBuffer buf = new StringBuffer();
		final List aList = new ArrayList();
		for (Iterator i = getActiveBonusMap().keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			if (aKey.startsWith(prefix))
			{
				// make a list of keys that end with .REPLACE
				if (aKey.endsWith(".REPLACE"))
				{
					aList.add(aKey);
				}
				else
				{
					if (buf.length() > 0)
					{
						buf.append(", ");
					}
					String reason = "";
					if (aKey.length() > prefix.length())
					{
						reason = aKey.substring(prefix.length() + 1);
					}
					final int b = (int) getActiveBonusForMapKey(aKey, 0);
					if (b == 0)
					{
						continue;
					}
					if (!"NULL".equals(reason) && (reason.length() > 0))
					{
						buf.append(reason).append(' ');
					}
					buf.append(Delta.toString(b));
				}
			}
		}
		// Now adjust the bonus if the .REPLACE value
		// replaces the value without .REPLACE
		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final String replaceKey = (String) i.next();
				if (replaceKey.length() > 7)
				{
					final String aKey = replaceKey.substring(0, replaceKey.length() - 8);
					final double replaceBonus = getActiveBonusForMapKey(replaceKey, 0);
					double aBonus = getActiveBonusForMapKey(aKey, 0);
					aBonus += getActiveBonusForMapKey(aKey + ".STACK", 0);
					final int b = (int) Math.max(aBonus, replaceBonus);
					if (b == 0)
					{
						continue;
					}
					if (buf.length() > 0)
					{
						buf.append(", ");
					}
					final String reason = aKey.substring(prefix.length() + 1);
					if (!"NULL".equals(reason))
					{
						buf.append(reason).append(' ');
					}
					buf.append(Delta.toString(b));
				}
			}
		}
		return buf.toString();
	}

	/**
	 * return bonus total for a specific bonusType
	 * e.g: getBonusDueToType("COMBAT","AC","Armor") to get armor bonuses
	 **/
	public double getBonusDueToType(String mainType, String subType, String bonusType)
	{
		final String typeString = mainType + "." + subType + "." + bonusType;
		return sumActiveBonusMap(typeString);
	}

	/**
	 * Calculates total bonus from all stats
	 **/
	public double getStatBonusTo(String aType, String aName)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		List aList = statList.getBonusListOfType(aType, aName);
		return calcBonusFromList(aList);
	}

	/**
	 * Calculates total bonus from Size adjustments
	 **/
	public double getSizeAdjustmentBonusTo(String aType, String aName)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		return getBonusDueToType(aType, aName, "SIZE");
	}

	/**
	 * Calculates total bonus from Checks
	 **/
	private double getCheckBonusTo(String aType, String aName)
	{
		double bonus = 0;
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		List aList = SystemCollections.getUnmodifiableCheckList();
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			PObject obj = (PObject) i.next();
			List tempList = obj.getBonusListOfType(aType, aName);
			if (!tempList.isEmpty())
			{
				bonus += calcBonusFromList(tempList);
			}
		}
		return bonus;
	}

	/**
	 * return bonus from a Race
	 **/
	public double getRaceBonusTo(String aType, String aName)
	{
		if (getRace() == null)
		{
			return 0;
		}
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		List tempList = getRace().getBonusListOfType(aType, aName);
		return calcBonusFromList(tempList);
	}

	/**
	 * return bonus from Temporary Bonuses
	 **/
	public double getTempBonusTo(String aType, String aName)
	{
		double bonus = 0;
		if (getTempBonusList().isEmpty())
		{
			return bonus;
		}
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		for (Iterator b = getTempBonusList().iterator(); b.hasNext();)
		{
			BonusObj aBonus = (BonusObj) b.next();
			String bString = aBonus.toString();
			if ((bString.indexOf(aType) < 0) || (bString.indexOf(aName) < 0))
			{
				continue;
			}
			Object tarObj = aBonus.getTargetObject();
			Object creObj = aBonus.getCreatorObject();
			if (creObj == null || tarObj == null)
			{
				continue;
			}
			if (!(creObj instanceof PObject) || !(tarObj instanceof PlayerCharacter))
			{
				continue;
			}
			PlayerCharacter bPC = (PlayerCharacter) tarObj;
			if (bPC != Globals.getCurrentPC())
			{
				continue;
			}
			final PObject aCreator = (PObject) creObj;
			bonus += aCreator.calcBonusFrom(aBonus, this);
		}
		return bonus;
	}

	/**
	 * Parses through all Equipment items and calculates total Bonus
	 **/
	private double getEquipmentBonusTo(String aType, String aName)
	{
		double bonus = 0;
		if (equipmentList.isEmpty())
		{
			return bonus;
		}
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				List tempList = eq.getBonusListOfType(aType, aName, true);
				if (eq.isWeapon() && eq.isDouble())
				{
					tempList.addAll(eq.getBonusListOfType(aType, aName, false));
				}
				bonus += calcBonusFromList(tempList);
			}
		}
		return bonus;
	}

	/**
	 * Parses through all templates to calc total bonus
	 **/
	public double getTemplateBonusTo(String aType, String aName, boolean subSearch)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		return getPObjectWithCostBonusTo(templateList, aType, aName, subSearch);
	}

	/**
	 * Calculates total bonus from Feats
	 **/
	public double getFeatBonusTo(String aType, String aName, boolean subSearch)
	{
		aType = aType.toUpperCase();
		aName = aName.toUpperCase();
		return getPObjectWithCostBonusTo(aggregateFeatList(), aType, aName, subSearch);
	}

	/**
	 * Returns a bonus.
	 * @param aList
	 * @param aType
	 * @param aName
	 * @param subSearch
	 * @return
	 */
	private double getPObjectWithCostBonusTo(List aList, String aType, String aName, boolean subSearch)
	{
		double iBonus = 0;
		if (aList.isEmpty())
		{
			return iBonus;
		}
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			PObject anObj = (PObject) e.next();
			List tempList = anObj.getBonusListOfType(aType, aName);
			iBonus += calcBonusWithCostFromList(tempList, subSearch);
		}
		return iBonus;
	}

	/**
	 * begin breakdown of getAC info Arknight 08-09-02
	 * rework by Arknight 01-02-03
	 **/
	public int getACTotal()
	{
		return (int) getTotalBonusTo("COMBAT", "AC");
	}

	public String flatfootedAC()
	{
		String aString = SettingsHandler.getGame().getAcFlatBonus();
		if (aString.length() != 0)
		{
			return ExportHandler.calcBonusValue(this, "BONUS." + aString);
		}
		return "";
	}

	public String touchAC()
	{
		String aString = SettingsHandler.getGame().getAcTouchBonus();
		if (aString.length() != 0)
		{
			return ExportHandler.calcBonusValue(this, "BONUS." + aString);
		}
		return "";
	}

	public int abilityAC()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "ABILITY");
	}

	public int baseAC()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "BASE");
	}

	public int classAC()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "CLASSDEFENSE");
	}

	public int dodgeAC()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "DODGE");
	}

	public int equipmentAC()
	{
		return ((int) getBonusDueToType("COMBAT", "AC", "EQUIPMENT") + (int) getBonusDueToType("COMBAT", "AC", "ARMOR"));
	}

	public int miscAC()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "MISC");
	}

	public int naturalAC()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "NATURALARMOR");
	}

	public int sizeAC()
	{
		return (int) getSizeAdjustmentBonusTo("COMBAT", "AC");
	}

	public int altHP()
	{
		final int i = (int) getTotalBonusTo("HP", "ALTHP");
		return i;
	}

	/**
	 * return value indicates if book was actually added or not
	 **/
	public boolean addSpellBook(String aName)
	{
		if (aName.length() > 0 && !spellBooks.contains(aName))
		{
			spellBooks.add(aName);
			return true;
		}
		return false;
	}

	/**
	 * return value indicates whether or not a book was actually removed
	 **/
	public boolean delSpellBook(String aName)
	{
		if (aName.length() > 0 && !aName.equals(Globals.getDefaultSpellBook()) && spellBooks.contains(aName))
		{
			spellBooks.remove(aName);
			for (Iterator i = classList.iterator(); i.hasNext();)
			{
				final PCClass aClass = (PCClass) i.next();
				final List aList = aClass.getCharacterSpell(null, aName, -1);
				for (int j = aList.size() - 1; j >= 0; --j)
				{
					final CharacterSpell cs = (CharacterSpell) aList.get(j);
					cs.removeSpellInfo(cs.getSpellInfoFor(aName, -1, -1));
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * whether we should add auto known spells at level up
	 **/
	public void setAutoSpells(boolean aBool)
	{
		autoKnownSpells = aBool;
	}

	public boolean getAutoSpells()
	{
		return autoKnownSpells;
	}

	/**
	 * whether we should load companions on master load
	 **/
	public void setLoadCompanion(boolean aBool)
	{
		autoLoadCompanion = aBool;
	}

	public boolean getLoadCompanion()
	{
		return autoLoadCompanion;
	}

	/**
	 * whether we should use/save Temporary bonuses
	 **/
	public void setUseTempMods(boolean aBool)
	{
		useTempMods = aBool;
	}

	public boolean getUseTempMods()
	{
		return useTempMods;
	}

	/**
	 * acs is the CharacterSpell object containing the spell which is to be modified
	 * @param aFeatList is the list of feats to be added to the SpellInfo object added to acs
	 * @param className is the name of the class whose list of characterspells will be modified
	 * @param bookName is the name of the book for the SpellInfo object
	 * @param spellLevel is the original (unadjusted) level of the spell not including feat adjustments
	 * @param adjSpellLevel is the adjustedLevel (including feat adjustments) of this spell, it may be higher if the user chooses a higher level.
	 * @return an empty string on successful completion, otherwise
	 * the return value indicates the reason the add function failed.
	 **/
	public String addSpell(CharacterSpell acs, List aFeatList, String className, String bookName, int adjSpellLevel, int spellLevel)
	{
		if (acs == null)
		{
			return "Invalid parameter to add spell";
		}
		PCClass aClass = null;
		final Spell aSpell = acs.getSpell();

		if (bookName == null || bookName.length() == 0)
		{
			return "Invalid spell book name.";
		}
		if (className != null)
		{
			aClass = getClassNamed(className);
			if (aClass == null && className.lastIndexOf('(') >= 0)
			{
				aClass = getClassNamed(className.substring(0, className.lastIndexOf('(')).trim());
			}
		}
		if (aClass == null)
		{
			return "No class named " + className;
		}
		if (!aClass.getMemorizeSpells() && !bookName.equals(Globals.getDefaultSpellBook()))
		{
			return aClass.getName() + " can only add to " + Globals.getDefaultSpellBook();
		}
		int numSpellsFromSpecialty = aClass.getNumSpellsFromSpecialty();
		// Divine spellcasters get no bonus spells at level 0
		// TODO: allow classes to define how many bonus spells they get each level!
		if (spellLevel == 0 && "Divine".equalsIgnoreCase(aClass.getSpellType()))
		{
			numSpellsFromSpecialty = 0;
		}

		// all the exists checks are done.
		// now determine how many specialtySpells
		// of this level for this class in this book
		int spellsFromSpecialty = 0;
		// first we check this spell being added
		if (acs.isSpecialtySpell())
		{
			++spellsFromSpecialty;
		}
		// now all the rest of the already known spells
		final List sList = aClass.getCharacterSpell(null, bookName, adjSpellLevel);
		if (!sList.isEmpty())
		{
			for (Iterator i = sList.iterator(); i.hasNext();)
			{
				final CharacterSpell cs = (CharacterSpell) i.next();
				if (!cs.equals(acs) && cs.isSpecialtySpell())
				{
					++spellsFromSpecialty;
				}
			}
		}

		// don't allow adding spells which are prohibited
		// But if a spell is both prohibited and in a specialty
		// which can be the case for some spells, then allow it.
		if (!acs.isSpecialtySpell() && aClass.isProhibited(aSpell))
		{
			return acs.getSpell().getName() + " is prohibited.";
		}

		// Now let's see if they should be able to add this spell

		// first check for known/cast/threshold
		int known = aClass.getKnownForLevel(aClass.getLevel(), spellLevel);
		int specialKnown = 0;
		final int cast = aClass.getCastForLevel(aClass.getLevel(), adjSpellLevel, bookName, true, true);
		final int listNum = aClass.memorizedSpellForLevelBook(adjSpellLevel, bookName);
		final boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		if (isDefault)
		{
			specialKnown = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), spellLevel);
		}

		// known is the maximun spells that can be known this level
		// listNum is the current spells already memorized this level
		// cast is the number of spells that can be cast at this level
		// Modified this to use new availableSpells() method so you can "blow" higher-level slots on
		// lower-level spells
		// in re BUG [569517]
		// sk4p 13 Dec 2002
		if (!aClass.getMemorizeSpells() && !availableSpells(adjSpellLevel, aClass, bookName, true, acs.isSpecialtySpell()))
		{
			// If this were a specialty spell, would there be room?
			//
			if (!acs.isSpecialtySpell() && availableSpells(adjSpellLevel, aClass, bookName, true, true))
			{
				return "Your remaining slot(s) must be filled with your specialty";
			}
			else
			{
				return "You can only learn " + (known + specialKnown) + " spells for level " + adjSpellLevel + "\nand there are no higher-level slots available";
			}
		}

		else if (aClass.getMemorizeSpells() && !isDefault && !availableSpells(adjSpellLevel, aClass, bookName, false, acs.isSpecialtySpell()))
		{
			if (!acs.isSpecialtySpell() && availableSpells(adjSpellLevel, aClass, bookName, false, true))
			{
				return "Your remaining slot(s) must be filled with your specialty or domain";
			}
			else
			{
				return "You can only prepare " + cast + " spells for level " + adjSpellLevel + "\nand there are no higher-level slots available";
			}
		}

		// determine if this spell already exists
		// for this character in this book at this level
		SpellInfo si = null;
		final boolean isEmpty = (aClass.getCharacterSpell(acs.getSpell(), bookName, adjSpellLevel).isEmpty());
		if (!isEmpty)
		{
			si = acs.getSpellInfoFor(bookName, adjSpellLevel, -1, aFeatList);
		}

		if (si != null)
		{
			// ok, we already known this spell, so if they are
			// trying to add it to the default spellBook, barf
			// otherwise increment the number of times memorized
			if (isDefault)
			{
				return "The Known Spells spellbook contains all spells of this level that you know. You cannot place spells in multiple times.";
			}
			else
			{
				si.setTimes(si.getTimes() + 1);
			}
		}
		else
		{
			if (isEmpty)
			{
				acs = new CharacterSpell(acs.getOwner(), acs.getSpell());
				aClass.addCharacterSpell(acs);
			}
			si = acs.addInfo(adjSpellLevel, 1, bookName, aFeatList);
		}
		return "";
	}

	/**
	 * return value indicates whether or not a spell was deleted or not
	 **/
	public static String delSpell(SpellInfo si, PCClass aClass, String bookName)
	{
		if (bookName == null || bookName.length() == 0)
		{
			return "Invalid spell book name.";
		}
		if (aClass == null)
		{
			return "Error: Class is null";
		}
		final CharacterSpell acs = si.getOwner();

		final boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		// yes, you can remove spells from the default spellbook,
		// but they will just get added back in when the character
		// is re-loaded. But, allow them to do it anyway, just in case
		// there is some wierd spell that keeps getting loaded by
		// accident (or is saved in the .pcg file)
		if (isDefault && aClass.isAutoKnownSpell(acs.getSpell().getName(), si.getActualLevel()))
		{
			Logging.errorPrint("Notice: removing " + acs.getSpell().getName() + " even though it is an auto known spell");
		}
		si.setTimes(si.getTimes() - 1);
		if (si.getTimes() <= 0)
		{
			acs.removeSpellInfo(si);
		}
		si = acs.getSpellInfoFor("", -1, -1, null);
		if (si == null)
		{
			aClass.removeCharacterSpell(acs);
		}
		return "";
	}

	private void setStableVirtualFeatList(List aFeatList)
	{
		stableVirtualFeatList = aFeatList;
		setVirtualFeatsStable(aFeatList != null);
	}

	private List getStableVirtualFeatList()
	{
		if (isVirtualFeatsStable())
		{
			return stableVirtualFeatList;
		}
		else
		{
			return null;
		}
	}

	public List vFeatList()
	{
		List vFeatList = getStableVirtualFeatList();
		//Did we get a valid list? If so, return it.
		if (vFeatList != null)
		{
			return vFeatList;
		}
		else
		{
			vFeatList = new ArrayList();
			if (stableVirtualFeatList != null)
			{
				for (Iterator i = stableVirtualFeatList.iterator(); i.hasNext();)
				{
					Feat aFeat = (Feat) i.next();
					if (aFeat.needsSaving())
					{
						vFeatList.add(aFeat);
					}
				}
			}
		}

		PCClass aClass;
		Feat classFeat;
		StringTokenizer classTok;

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			aClass = (PCClass) e.next();
			for (Iterator e1 = aClass.vFeatList().iterator(); e1.hasNext();)
			{
				classTok = new StringTokenizer((String) e1.next(), ":");
				final int level = Integer.parseInt(classTok.nextToken());
				if (level <= aClass.getLevel())
				{
					classTok = new StringTokenizer(classTok.nextToken(), "|");
					while (classTok.hasMoreTokens())
					{
						final String featName = classTok.nextToken();
						classFeat = Globals.getFeatNamed(featName);
						if (classFeat != null)

						{
							classFeat = (Feat) classFeat.clone();
							classFeat.setFeatType(Feat.FEAT_VIRTUAL);
							if (!classFeat.getName().equalsIgnoreCase(featName))
							{
								final int i = featName.indexOf('(');
								final int j = featName.indexOf(')');
								if ((i >= 0) && (j >= 0))
								{
									final StringTokenizer aTok = new StringTokenizer(featName.substring(i + 1, j), ",");
									while (aTok.hasMoreTokens())
									{
										final String a = aTok.nextToken();
										if (!classFeat.containsAssociated(a))
										{
											classFeat.addAssociated(a);
										}
									}
								}
							}
							//classFeat.activateBonuses();
							vFeatList.add(classFeat);
						}
					}
				}
			}
		}
		if (!equipmentList.isEmpty())
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment aE = (Equipment) e.next();
				if (aE.isEquipped() && aE.hasVFeats())
				{
					for (Iterator e1 = aE.getVFeatList().iterator(); e1.hasNext();)
					{
						final String featName = e1.next().toString();
						vFeatList = addVirtualFeat(featName, vFeatList);
					}
				}
			}
		}
		final StringTokenizer raceTok = new StringTokenizer(getRace().getVFeatList(), "|");
		while (raceTok.hasMoreTokens())
		{
			final String featName = raceTok.nextToken();
			vFeatList = addVirtualFeat(featName, vFeatList);
		}
		// companion virtual feats granted to master
		if (!getFollowerList().isEmpty())
		{
			for (Iterator fm = getFollowerList().iterator(); fm.hasNext();)
			{
				final Follower aF = (Follower) fm.next();
				final String rType = aF.getType().toUpperCase();
				final String rName = aF.getRace().toUpperCase();
				for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
				{
					final CompanionMod aComp = (CompanionMod) cm.next();
					final String aType = aComp.getType().toUpperCase();
					final int iRace = aComp.getLevel(rName);
					if (aType.equals(rType) && (iRace == 1) && (aComp.getVFeatList() != null))
					{
						for (Iterator v1 = aComp.getVFeatList().iterator(); v1.hasNext();)
						{
							final String featName = v1.next().toString();
							vFeatList = addVirtualFeat(featName, vFeatList);
						}
					}
				}
			}
		}

		setStableVirtualFeatList(vFeatList);
		return vFeatList;
	}

	public List addVirtualFeat(final String featName, List vFeatList)
	{
		Feat aFeat = Globals.getFeatNamed(featName);
		if (aFeat != null)
		{
			aFeat = (Feat) aFeat.clone();
			aFeat.setFeatType(Feat.FEAT_VIRTUAL);
			if (!aFeat.getName().equalsIgnoreCase(featName))
			{
				final int i = featName.indexOf('(');
				final int j = featName.indexOf(')');
				if ((i >= 0) && (j >= 0))
				{
					final StringTokenizer aTok = new StringTokenizer(featName.substring(i + 1, j), ",");
					while (aTok.hasMoreTokens())
					{
						final String a = aTok.nextToken();
						if (!aFeat.containsAssociated(a))
						{
							aFeat.addAssociated(a);
						}
					}
				}
			}
			//aFeat.activateBonuses();
			vFeatList.add(aFeat);
			setStableVirtualFeatList(vFeatList);
		}
		return vFeatList;
	}

	private void setStableAutomaticFeatList(List aFeatList)
	{
		stableAutomaticFeatList = aFeatList;
		setAutomaticFeatsStable(aFeatList != null);
	}

	private List getStableAutomaticFeatList()
	{
		if (isAutomaticFeatsStable())
		{
			return stableAutomaticFeatList;
		}
		else
		{
			return null;
		}
	}

	private static void addToAutoFeatList(List autoFeatList, String featName)
	{
		String altName = "";
		String subName = "";
		if (featName.endsWith(")"))
		{
			subName = featName.substring(featName.indexOf('(') + 1, featName.lastIndexOf(')')); //we want what is inside the outermost parens.
			altName = featName.substring(0, featName.indexOf('(') - 1);
		}

		Feat aFeat = getFeatNamedInList(autoFeatList, featName);
		if ((aFeat == null) && (altName.length() != 0))
		{
			aFeat = getFeatNamedInList(autoFeatList, altName);
		}
		//
		// Don't already have feat, find global definition, clone copy, attach sub-type (if any) and add
		//
		if (aFeat == null)
		{
			aFeat = Globals.getFeatNamed(featName);
			if ((aFeat == null) && (altName.length() != 0))
			{
				aFeat = Globals.getFeatNamed(altName);
			}
			if (aFeat != null)
			{
				aFeat = (Feat) aFeat.clone();

				if (subName.length() != 0)
				{
					aFeat.addAssociated(subName);
				}
				aFeat.setFeatType(Feat.FEAT_AUTOMATIC);
				autoFeatList.add(aFeat);
				//aFeat.activateBonuses();
			}
			else
			{
				GuiFacade.showMessageDialog(null, "Adding unknown feat: " + featName, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			}
		}
		//
		// Already have feat, add sub-type (if any)
		//
		else
		{
			if (subName.length() != 0)
			{
				if (aFeat.isStacks() && (aFeat.isMultiples() || !aFeat.containsAssociated(subName)))
				{
					aFeat.addAssociated(subName);
				}
			}
		}
	}

	public List featAutoList()
	{
		List autoFeatList = getStableAutomaticFeatList();
		//Did we get a valid list? If so, return it.
		if (autoFeatList != null)
		{
			return autoFeatList;
		}
		else
		{
			autoFeatList = new ArrayList();
		}

		//
		// add racial feats
		//
		if ((race != null) && !canReassignRacialFeats())
		{
			final StringTokenizer aTok = new StringTokenizer(race.getFeatList(), "|");
			while (aTok.hasMoreTokens())
			{
				addToAutoFeatList(autoFeatList, aTok.nextToken());
			}
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			for (Iterator e1 = aClass.getFeatAutos().iterator(); e1.hasNext();)
			{
				//
				// PCClass object have auto feats stored in format:
				// lvl|feat_name
				//
				String aString = (String) e1.next();
				if (aString.indexOf('|') < 1)
				{
					continue;
				}

				final StringTokenizer aTok = new StringTokenizer(aString, "|");
				int i;
				try
				{
					i = Integer.parseInt(aTok.nextToken());
				}
				catch (NumberFormatException exc)
				{
					i = 9999; //TODO: Replace magic value with an appropriate constant. Constants.INVALID_LEVEL perhaps?
				}
				if (i > aClass.getLevel())
				{
					continue;
				}

				String autoFeat = aTok.nextToken();
				final int idx = autoFeat.indexOf('[');
				if (idx >= 0)
				{
					final StringTokenizer bTok = new StringTokenizer(autoFeat.substring(idx + 1), "[]");
					List preReqList = new ArrayList();
					while (bTok.hasMoreTokens())
					{
						preReqList.add(bTok.nextToken());
					}
					autoFeat = autoFeat.substring(0, idx);
					if (preReqList.size() != 0)
					{
						//
						// To avoid possible infinite loop
						//
						if (!isAutomaticFeatsStable())
						{
							setStableAutomaticFeatList(autoFeatList);
						}
						if (!aClass.passesPreReqToGainForList(preReqList))
						{
							continue;
						}
					}
				}
				addToAutoFeatList(autoFeatList, autoFeat);
			}
		}

		if (!canReassignTemplateFeats() && !templateList.isEmpty())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				setStableAutomaticFeatList(autoFeatList);

				final PCTemplate aTemplate = (PCTemplate) e.next();
				List templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice());
				if (!templateFeats.isEmpty())
				{
					for (Iterator e2 = templateFeats.iterator(); e2.hasNext();)
					{
						final String aString = (String) e2.next();
						final StringTokenizer aTok = new StringTokenizer(aString, ",");
						while (aTok.hasMoreTokens())
						{
							addToAutoFeatList(autoFeatList, aTok.nextToken());
						}
					}
				}
			}
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain) e.next();
				final Domain aDomain = aCD.getDomain();
				if (aDomain != null)
				{
					for (int e2 = 0; e2 < aDomain.getAssociatedCount(); ++e2)
					{
						final String aString = aDomain.getAssociated(e2);
						if (aString.startsWith("FEAT"))
						{
							int idx = aString.indexOf('?');
							if (idx > -1)
							{
								addToAutoFeatList(autoFeatList, aString.substring(idx + 1));
							}
							else
							{
								Logging.errorPrint("no '?' in Domain assocatedList entry: " + aString);
							}
						}
					}
					final String domainFeatList = aDomain.getFeatList();
					if (domainFeatList.length() != 0)
					{
						StringTokenizer aTok = new StringTokenizer(domainFeatList, ",");
						while (aTok.hasMoreTokens())
						{
							final String featName = aTok.nextToken();
							addToAutoFeatList(autoFeatList, featName);
						}
					}
				}
			}
		}

		//
		// Need to save current as stable as getAutoWeaponProfs() needs it
		//
		setStableAutomaticFeatList(autoFeatList);
		getAutoWeaponProfs(autoFeatList);
		setStableAutomaticFeatList(autoFeatList);

		return autoFeatList;
	}

	private void setStableAggregateFeatList(List aFeatList)
	{
		stableAggregateFeatList = aFeatList;
		setAggregateFeatsStable(aFeatList != null);
	}

	private List getStableAggregateFeatList()
	{
		if (isAggregateFeatsStable())
		{
			return stableAggregateFeatList;
		}
		else
		{
			return null;
		}
	}

	public List aggregateVisibleFeatList()
	{
		List tempFeatList = new ArrayList();
		for (Iterator e1 = aggregateFeatList().iterator(); e1.hasNext();)
		{
			final Feat aFeat = (Feat) e1.next();
			if (aFeat.isVisible() == 1 || aFeat.isVisible() == 2)
			{
				tempFeatList.add(aFeat);
			}
		}
		return tempFeatList;
	}

	public List aggregateFeatList()
	{
		List aggregate = getStableAggregateFeatList();
		//Did we get a valid list? If so, return it.
		if (aggregate != null)
		{
			return aggregate;
		}
		else
		{
			aggregate = new ArrayList();
		}
		final Map aHashMap = new HashMap();
		if (!featList.isEmpty())
		{
			for (Iterator e = ((ArrayList) featList.clone()).iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat) e.next();
				if (aFeat != null)
				{
					aHashMap.put(aFeat.getKeyName(), aFeat);
				}
			}
		}

		for (Iterator e = vFeatList().iterator(); e.hasNext();)
		{
			Feat virtualFeat = (Feat) e.next();
			if (!aHashMap.containsKey(virtualFeat.getKeyName()))
			{
				aHashMap.put(virtualFeat.getKeyName(), virtualFeat);
				//virtualFeat.activateBonuses();
			}
			else if (virtualFeat.isMultiples())
			{
				Feat aggregateFeat = (Feat) aHashMap.get(virtualFeat.getKeyName());
				aggregateFeat = (Feat) aggregateFeat.clone();
				for (int e1 = 0; e1 < virtualFeat.getAssociatedCount(); ++e1)
				{
					String aString = virtualFeat.getAssociated(e1);
					if (aggregateFeat.isStacks() || !aggregateFeat.containsAssociated(aString))
					{
						aggregateFeat.addAssociated(aString);
					}
				}
				//aggregateFeat.activateBonuses();
				aHashMap.put(virtualFeat.getName(), aggregateFeat);
			}
		}

		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);

		for (Iterator e = featAutoList().iterator(); e.hasNext();)
		{
			final Feat autoFeat = (Feat) e.next();
			if (!aHashMap.containsKey(autoFeat.getKeyName()))
			{
				aHashMap.put(autoFeat.getName(), autoFeat);
				//autoFeat.activateBonuses();
			}
			else if (autoFeat.isMultiples())
			{
				Feat aggregateFeat = (Feat) aHashMap.get(autoFeat.getKeyName());
				aggregateFeat = (Feat) aggregateFeat.clone();
				for (int e1 = 0; e1 < autoFeat.getAssociatedCount(); ++e1)
				{
					String aString = autoFeat.getAssociated(e1);
					if (aggregateFeat.isStacks() || !aggregateFeat.containsAssociated(aString))
					{
						aggregateFeat.addAssociated(aString);
					}
				}
				//aggregateFeat.activateBonuses();
				aHashMap.put(autoFeat.getName(), aggregateFeat);
			}
		}

		aggregate = new ArrayList();
		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);
		return aggregate;
	}

	/**
	 * calculate the total racial modifier to save:
	 *   racial boni like the standard halfling's +1 on all saves
	 *   template boni like the lightfoot halfling's +1 on all saves
	 *   racial base modifiers for certain monsters
	 **/
	private int calculateSaveBonusRace(int saveIndex)
	{
		int save = 0;
		if (saveIndex - 1 < 0 || saveIndex - 1 >= SystemCollections.getUnmodifiableCheckList().size())
		{
			return 0;
		}
		final String sString = SystemCollections.getUnmodifiableCheckList().get(saveIndex - 1).toString();
		save = (int) race.bonusTo("CHECKS", "BASE." + sString);
		save += (int) race.bonusTo("CHECKS", sString);
		return save;
	}

	/**
	 * calculate different kinds of boni to saves
	 * possible tokens are
	 *   save
	 *   save.TOTAL
	 *   save.BASE
	 *   save.MISC
	 *   save.list
	 *   save.TOTAL.list
	 *   save.BASE.list
	 *   save.MISC.list
	 * where
	 *   save    := "CHECK1"|"CHECK2"|"CHECK3"
	 *   list    := ((include|exclude)del)*(include|exclude)
	 *   include := "FEATS"|"MAGIC"|"RACE"
	 *   exclude := "NOFEATS"|"NOMAGIC"|"NORACE"|"NOSTAT"
	 *   del     := "."
	 * given as regular expression
	 *
	 * "include"-s will add the appropriate modifier
	 * "exclude"-s will subtract the appropriate modifier
	 *
	 * (This means save.MAGIC.NOMAGIC equals 0
	 *  whereas save.RACE.RACE equals 2 times the racial bonus)
	 *
	 * If you use unrecognized terminals, their value will amount to 0
	 * This means save.BLABLA equals 0
	 * whereas save.MAGIC.BLABLA equals save.MAGIC
	 *
	 * <br>author: Thomas Behr 09-03-02
	 *
	 * @param saveIndex	See the appropriate gamemode file
	 * @param saveType	"CHECK1", "CHECK2", or "CHECK3";
	 *			may not differ from saveIndex!
	 * @param tokenString	    tokenString to parse

	 * @return the calculated save bonus
	 */

	public int calculateSaveBonus(int saveIndex, String saveType, String tokenString)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenString, ".");
		final String[] tokens = new String[aTok.countTokens()];
		final int checkIndex = SystemCollections.getIndexOfCheck(saveType) + 1;
		int save = 0;
		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();
			if ("TOTAL".equals(tokens[i]))
			{
				save += (int) getBonus(checkIndex, true);
			}
			else if ("BASE".equals(tokens[i]))
			{
				save += (int) getBonus(checkIndex, false);
			}
			else if ("MISC".equals(tokens[i]))
			{
				save += (int) getTotalBonusTo("CHECKS", saveType);
			}
			if ("MAGIC".equals(tokens[i]))
			{
				save += (int) getEquipmentBonusTo("CHECKS", saveType);
			}
			if ("RACE".equals(tokens[i]))
			{
				save += calculateSaveBonusRace(saveIndex);
			}
			if ("FEATS".equals(tokens[i]))
			{
				save += (int) getFeatBonusTo("CHECKS", saveType, true);
			}
			if ("STATMOD".equals(tokens[i]))
			{
				save += (int) getCheckBonusTo("CHECKS", saveType);
			}
			/**
			 * exclude stuff
			 **/
			if ("NOMAGIC".equals(tokens[i]))
			{
				save -= (int) getEquipmentBonusTo("CHECKS", saveType);
			}
			if ("NORACE".equals(tokens[i]))
			{
				save -= calculateSaveBonusRace(saveIndex);
			}
			if ("NOFEATS".equals(tokens[i]))
			{
				save -= (int) getFeatBonusTo("CHECKS", saveType, true);
			}
			if ("NOSTAT".equals(tokens[i]) || "NOSTATMOD".equals(tokens[i]))
			{
				save -= (int) getCheckBonusTo("CHECKS", saveType);
			}
		}

		return save;
	}

	/**
	 * returns the number of spells based on class, level and spellbook
	 **/
	private int countSpellListBook(String aString)
	{
		final int dot = aString.lastIndexOf('.');
		int spellCount = 0;
		if (dot < 0)
		{
			for (Iterator iClass = classList.iterator(); iClass.hasNext();)
			{
				final PCClass aClass = (PCClass) iClass.next();
				spellCount += aClass.getCharacterSpellCount();
			}
		}
		else
		{
			final int classNum = Integer.parseInt(aString.substring(17, dot));
			final int levelNum = Integer.parseInt(aString.substring(dot + 1, aString.length() - 1));

			final PObject aObject = getSpellClassAtIndex(classNum);
			if (aObject != null)
			{
				final List aList = aObject.getCharacterSpell(null, Globals.getDefaultSpellBook(), levelNum);
				spellCount = aList.size();
			}
		}
		return spellCount;
	}

	/**
	 * Counts the number of spells inside a spellbook
	 * Yes, divine casters can have a "spellbook"
	 **/
	private int countSpellsInBook(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int sbookNum = Integer.parseInt(aTok.nextToken());
		final int levelNum;
		if (aTok.hasMoreTokens())
		{
			levelNum = Integer.parseInt(aTok.nextToken());
		}
		else
		{
			levelNum = -1;
		}

		String bookName = Globals.getDefaultSpellBook();
		if (sbookNum > 0)
		{
			bookName = (String) getSpellBooks().get(sbookNum);
		}
		final PObject aObject = getSpellClassAtIndex(classNum);
		if (aObject != null)
		{
			final List aList = aObject.getCharacterSpell(null, bookName, levelNum);
			return aList.size();
		}
		return 0;
	}

	/**
	 * returns the level of the highest spell in a given spellbook
	 * Yes, divine casters can have a "spellbook"
	 **/
	private int countSpellLevelsInBook(String aString)
	{
		int levelNum = 0;

		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int sbookNum = Integer.parseInt(aTok.nextToken());

		String bookName = Globals.getDefaultSpellBook();
		if (sbookNum > 0)
		{
			bookName = (String) getSpellBooks().get(sbookNum);
		}
		final PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			for (levelNum = 0; levelNum >= 0; ++levelNum)
			{
				List aList = aObject.getCharacterSpell(null, bookName, levelNum);
				if (aList.size() < 1)
				{
					break;
				}
			}
		}
		return levelNum;
	}

	/**
	 *  returns the number of times a spell is memorized
	 *  Tag looks like: SPELLTIMES%class.%book.%level.%spell
	 *  aString looks like: SPELLTIMES2.-1.4.15
	 *  heavily stolen from replaceTokenSpellMem in ExportHandler.java
	 **/
	private int countSpellTimes(String aString)
	{
		boolean found = false;
		final StringTokenizer aTok = new StringTokenizer(aString.substring(10), ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int bookNum = Integer.parseInt(aTok.nextToken());
		final int spellLevel = Integer.parseInt(aTok.nextToken());
		final int spellNumber = Integer.parseInt(aTok.nextToken());

		final PObject aObject = getSpellClassAtIndex(classNum);
		String bookName = Globals.getDefaultSpellBook();
		if (bookNum > 0)
		{
			bookName = (String) getSpellBooks().get(bookNum);
		}

		if (aObject != null || classNum == -1)
		{
			if (classNum == -1)
			{
				bookName = Globals.getDefaultSpellBook();
			}

			if (!"".equals(bookName))
			{
				SpellInfo si = null;
				if (classNum == -1)
				{
					final List charSpellList = new ArrayList();
					for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
					{
						final PCClass aClass = (PCClass) iClass.next();
						final List bList = aClass.getCharacterSpell(null, bookName, -1);
						for (Iterator bi = bList.iterator(); bi.hasNext();)
						{
							final CharacterSpell cs = (CharacterSpell) bi.next();
							if (!charSpellList.contains(cs))
							{
								charSpellList.add(cs);
							}
						}
					}
					Collections.sort(charSpellList);
					if (spellNumber < charSpellList.size())
					{
						final CharacterSpell cs = (CharacterSpell) charSpellList.get(spellNumber);
						si = cs.getSpellInfoFor(bookName, -1, -1);
						found = true;
					}
				}
				else if (aObject != null)
				{
					final List charSpells = aObject.getCharacterSpell(null, bookName, spellLevel);
					if (spellNumber < charSpells.size())
					{
						final CharacterSpell cs = (CharacterSpell) charSpells.get(spellNumber);
						si = cs.getSpellInfoFor(bookName, spellLevel, -1);
						found = true;
					}
				}
				if (found && (si != null))
				{
					return si.getTimes();
				}
			}
		}
		return 0;
	}

	private static void setProf(Equipment equip, Equipment eqm)
	{
		// Make sure the proficiency is set
		String profName = equip.rawProfName();
		if (profName.length() == 0)
		{
			profName = equip.getName();
		}
		eqm.setProfName(profName);
		// In case this is used somewhere it shouldn't be used,
		// set weight and cost to 0
		eqm.setWeight("0");
		eqm.setCost("0");
	}

	private static String appendToName(String aName, String aString)
	{
		final StringBuffer aBuf = new StringBuffer(aName);
		final int iLen = aBuf.length() - 1;
		if (aBuf.charAt(iLen) == ')')
		{
			aBuf.setCharAt(iLen, '/');
		}
		else
		{
			aBuf.append(" (");
		}
		aBuf.append(aString);
		aBuf.append(')');
		return aBuf.toString();
	}

	/**
	 * Retrieve the expanded list of weapons
	 * Expanded weapons include: double weapons and melee+ranged weapons
	 * Output order is assumed
	 * Merge of like equipment depends on the passed in int
	 * @return the sorted list of weapons.
	 **/
	public List getExpandedWeapons(int merge)
	{
		final List weapList = sortEquipmentList(getEquipmentOfType("Weapon", 3), merge);
		//
		// If any weapon is both Melee and Ranged, then make 2 weapons
		// for list, one Melee only, the other Ranged and Thrown.
		// For double weapons, if wielded in two hands show attacks
		// for both heads, head 1 and head 2 else
		// if wielded in 1 hand, just show damage by head
		//
		for (int idx = 0; idx < weapList.size(); ++idx)
		{
			final Equipment equip = (Equipment) weapList.get(idx);
			if (equip.isDouble() && (equip.getLocation() == Equipment.EQUIPPED_TWO_HANDS))
			{
				Equipment eqm = (Equipment) equip.clone();
				eqm.removeType("Double");
				eqm.setTypeInfo("Head1");
				// Add "Head 1 only" to the name of the weapon
				eqm.setName(appendToName(eqm.getName(), "Head 1 only"));
				if (eqm.getOutputName().indexOf("Head 1 only") < 0)
				{
					eqm.setOutputName(appendToName(eqm.getOutputName(), "Head 1 only"));
				}
				setProf(equip, eqm);
				weapList.add(idx + 1, eqm);

				eqm = (Equipment) equip.clone();
				String altType = eqm.getType(false);
				if (altType.length() != 0)
				{
					eqm.setTypeInfo(".CLEAR." + altType);
				}
				eqm.removeType("Double");
				eqm.setTypeInfo("Head2");
				eqm.setDamage(eqm.getAltDamage());
				eqm.setCritMult(eqm.getAltCritMult());
				eqm.setCritRange(Integer.toString(eqm.getRawCritRange(false)));
				eqm.getEqModifierList(true).clear();
				eqm.getEqModifierList(true).addAll(eqm.getEqModifierList(false));
				// Add "Head 2 only" to the name of the weapon
				eqm.setName(appendToName(eqm.getName(), "Head 2 only"));
				if (eqm.getOutputName().indexOf("Head 2 only") < 0)
				{
					eqm.setOutputName(appendToName(eqm.getOutputName(), "Head 2 only"));
				}
				setProf(equip, eqm);
				weapList.add(idx + 2, eqm);
			}
			//
			// Leave else here, as otherwise will show attacks
			// for both heads for thrown double weapons when
			// it should only show one
			//
			else if (equip.isMelee() && equip.isRanged() && (equip.getRange().intValue() != 0))
			{
				//
				// Strip off the Ranged portion, set range to 0
				//
				Equipment eqm = (Equipment) equip.clone();
				eqm.setTypeInfo("Both");
				eqm.removeType("Ranged.Thrown");
				eqm.setRange("0");
				setProf(equip, eqm);
				weapList.set(idx, eqm);
				//
				// Replace any primary weapons
				int iPrimary;
				for (iPrimary = getPrimaryWeapons().size() - 1; iPrimary >= 0; --iPrimary)
				{
					final Equipment teq = (Equipment) getPrimaryWeapons().get(iPrimary);
					if (teq.equalTo(equip))
					{
						break;
					}
				}
				if (iPrimary >= 0)
				{
					getPrimaryWeapons().set(iPrimary, eqm);
				}

				//
				// Replace any secondary weapons
				int iSecondary;
				for (iSecondary = getSecondaryWeapons().size() - 1; iSecondary >= 0; --iSecondary)
				{
					final Equipment teq = (Equipment) getSecondaryWeapons().get(iSecondary);
					if (teq.equalTo(equip))
					{
						break;
					}
				}
				if (iSecondary >= 0)
				{
					getSecondaryWeapons().set(iSecondary, eqm);
				}

				//
				// Add thrown portion, strip Melee
				//
				eqm = (Equipment) equip.clone();
				eqm.setTypeInfo("Ranged.Thrown.Both");
				eqm.removeType("Melee");
				// Add "Thrown" to the name of the weapon
				eqm.setName(appendToName(eqm.getName(), "Thrown"));
				if (eqm.getOutputName().indexOf("Thrown") < 0)
				{
					eqm.setOutputName(appendToName(eqm.getOutputName(), "Thrown"));
				}
				setProf(equip, eqm);
				weapList.add(++idx, eqm);

				if (iPrimary >= 0)
				{
					getPrimaryWeapons().add(++iPrimary, eqm);
				}
				else if (iSecondary >= 0)
				{
					getSecondaryWeapons().add(++iSecondary, eqm);
				}
			}
		}
		return weapList;
	}

	/**
	 * Evaluates a variable for this character
	 * e.g: getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 *
	 * @param aString The variable to be evaluated
	 * @param aString The source within which the variable is evaluated
	 * @return The value of the variable
	 **/
	public Float getVariableValue(String aString, String src)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		aString = aString.toUpperCase();
		src = src.toUpperCase();

		while (aString.lastIndexOf('(') >= 0)
		{
			int x = Utility.innerMostStringStart(aString);
			int y = Utility.innerMostStringEnd(aString);
			if (y < x)
			{
				Logging.errorPrint("Missing closing parenthesis: " + aString);
				return total;
			}
			String bString = aString.substring(x + 1, y);
			aString = aString.substring(0, x) + getVariableValue(bString, src) + aString.substring(y + 1);
		}
		final String delimiter = "+-/*";
		String valString = "";
		int mode = 0; //0=plus, 1=minus, 2=mult, 3=div
		int nextMode = 0;
		int endMode = 0; //1,11=min, 2,12=max, 3,13=req, 10 = int

		if (aString.startsWith(".IF."))
		{
			final StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			String bString = "";
			Float val1 = null; // first value
			Float val2 = null; // other value in comparison
			Float valt = null; // value if comparison is true
			Float valf; // value if comparison is false
			int comp = 0;
			while (aTok.hasMoreTokens())
			{
				String cString = aTok.nextToken();
				if ("GT".equals(cString) || "GTEQ".equals(cString) || "EQ".equals(cString) || "LTEQ".equals(cString) || "LT".equals(cString))
				{
					val1 = getVariableValue(bString.substring(0, bString.length() - 1), src); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
					if ("LT".equals(cString))
					{
						comp = 1;
					}
					else if ("LTEQ".equals(cString))
					{
						comp = 2;
					}
					else if ("EQ".equals(cString))
					{
						comp = 3;
					}
					else if ("GT".equals(cString))
					{
						comp = 4;
					}
					else if ("GTEQ".equals(cString))
					{
						comp = 5;
					}
				}
				else if ("THEN".equals(cString))
				{
					val2 = getVariableValue(bString.substring(0, bString.length() - 1), src); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else if ("ELSE".equals(cString))
				{
					valt = getVariableValue(bString.substring(0, bString.length() - 1), src); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else
				{
					bString += cString;
				}
			}
			if (val1 != null && val2 != null && valt != null)
			{
				valf = getVariableValue(bString, src);
				total = valt;
				switch (comp)
				{
					case 1:	// LT
						if (val1.doubleValue() >= val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 2:	// LTEQ
						if (val1.doubleValue() > val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 3:	// EQ
						if (!Utility.doublesEqual(val1.doubleValue(), val2.doubleValue()))
						{
							total = valf;
						}
						break;
					case 4:	// GT
						if (val1.doubleValue() <= val2.doubleValue())
						{
							total = valf;
						}
						break;
					case 5:	// GTEQ
						if (val1.doubleValue() < val2.doubleValue())
						{
							total = valf;
						}
						break;
					default:
						Logging.errorPrint("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}

				return total;
			}
		}
		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);
			if (i == aString.length() - 1 || delimiter.lastIndexOf(aString.charAt(i)) > -1 || (valString.length() > 3 && (valString.endsWith("MIN") || (!valString.startsWith("MODEQUIP") && valString.endsWith("MAX")) || valString.endsWith("REQ"))))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}
				if (valString.length() > 2 && valString.charAt(0) == '%' && valString.endsWith("%"))
				{
					if ("".equals(loopVariable)) // start the loop
					{
						StringTokenizer lTok = new StringTokenizer(valString, "%:");
						loopVariable = lTok.nextToken();
						String vString = loopVariable;
						decrement = 1;
						if (lTok.hasMoreTokens())
						{
							decrement = Integer.parseInt(lTok.nextToken());
						}
						loopValue = 0;
						if (hasVariable(loopVariable))
						{
							loopValue = getVariable(loopVariable, true, true, "", "").intValue();
							loopVariable = vString;
						}
					}
					if (loopValue == 0)
					{
						loopVariable = "";
					}
					valString = Integer.toString(loopValue);
				}
				if ("SCORE".equals(valString) && src.startsWith("STAT:"))
				{
					valString = String.valueOf(statList.getTotalStatFor(src.substring(5)));
				}
				if ("SPELLBASESTATSCORE".equals(valString))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getSpellBaseStat() + "SCORE";
						if ("SPELLSCORE".equals(valString))
						{
							valString = "10";
						}
					}
					else
					{
						valString = "0";
					}
				}
				if ("SPELLBASESTAT".equals(valString))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getSpellBaseStat();
						if ("SPELL".equals(valString))
						{
							valString = "0";
						}
					}
					else
					{
						valString = "0";
					}
				}
				if ("BASESPELLSTAT".equals(valString))
				{
					PCClass aClass = null;
					// if there's no class, then assume a basespellstat modifier of 0
					if (src.length() < 7)
					{
						valString = "0";
					}
					else
					{
						// src should be CLASS. something
						aClass = getClassNamed(src.substring(6));
					}
					if (aClass != null)
					{
						valString = String.valueOf(getBaseSpellStatBonus(aClass));
					}
				}
				if ("SPELLLEVEL".equals(valString))
				{
					valString = String.valueOf(getSpellLevelTemp());
				}
				if (valString.length() > 0 && Globals.getStatFromAbbrev(valString) > -1)
				{
					// TODO: JSC -- more testing!
					// Big Changes here!
					final int iX = Globals.getStatFromAbbrev(valString);
					final int statNum = statList.getTotalStatFor(valString);
					final int statMod = statList.getModForNumber(statNum, iX);
					valString = Integer.toString(statMod);
					//valString = Integer.toString(statList.getStatModFor(valString));
				}
				else if (valString.length() == 8 && Globals.getStatFromAbbrev(valString.substring(0, 3)) > -1 && valString.endsWith(".BASE"))
				{
					valString = Integer.toString(statList.getBaseStatFor(valString.substring(0, 3)));
				}
				else if ((valString.length() >= 8) && valString.substring(3).startsWith("SCORE"))
				{
					if (valString.endsWith(".BASE"))
					{
						valString = Integer.toString(statList.getBaseStatFor(valString.substring(0, 3)));
					}
					else
					{
						valString = Integer.toString(statList.getTotalStatFor(valString.substring(0, 3)));
					}
				}
				else if ("CASTERLEVEL".equals(valString) && src.startsWith("CLASS:"))
				{
					int iLev = (int) getTotalBonusTo("PCLEVEL", src.substring(6));
					iLev += Integer.parseInt(getClassLevelString(src.substring(6), false));
					valString = Integer.toString(iLev);
				}
				else if (("CL".equals(valString) || valString.startsWith("CL;BEFORELEVEL=") || valString.startsWith("CL;BEFORELEVEL.")) && src.startsWith("CLASS:"))
				{
					valString = getClassLevelString(src.substring(6) + valString.substring(2), false);
				}
				else if (valString.startsWith("CL=") || valString.startsWith("CL."))
				{
					valString = getClassLevelString(valString.substring(3), false);
				}
				else if ("BAB".equals(valString))
				{
					valString = Integer.toString(baseAttackBonus());
				}
				else if (valString.startsWith("CLASSLEVEL=") || valString.startsWith("CLASSLEVEL."))
				{
					valString = getClassLevelString(valString.substring(11), true);
				}
				else if (valString.startsWith("CLASS=") || valString.startsWith("CLASS."))
				{
					PCClass aClass = null;
					if (valString.length() > 6)
					{
						aClass = getClassNamed(valString.substring(6));
					}
					else
					{
						Logging.errorPrint("Error! Cannot determine CLASS!");
					}
					if (aClass != null)
					{
						valString = "1";
					}
					else
					{
						valString = "0";
					}
				}
				else if ("TL".equals(valString))
				{
					valString = Integer.toString(getTotalLevels());
				}
				else if ("HD".equals(valString))
				{
					// check companionModList?
					valString = Integer.toString(totalHitDice());
				}
				else if ("SHIELDACHECK".equals(valString))
				{
					List aArrayList = getEquipmentOfType("Shield", 1);
					if (aArrayList.size() > 0)
					{
						valString = ((Equipment) aArrayList.get(0)).acCheck().toString();
					}
					else
					{
						valString = "0";
					}
				}
				else if ("SIZE".equals(valString))
				{
					valString = String.valueOf(sizeInt());
				}
				else if ("SIZEMOD".equals(valString))
				{
					valString = String.valueOf((int) getSizeAdjustmentBonusTo("COMBAT", "AC"));
				}
				else if ("ENCUMBERANCE".equals(valString))
				{
					if (iForceLoad == -1)
					{
						valString = String.valueOf(Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight()));
					}
					else
					{
						valString = String.valueOf(iForceLoad);
					}
				}
				else if ("MOVEBASE".equals(valString))
				{
					valString = getRace().getMovement().toString();
				}
				else if ("COUNT[ATTACKS]".equals(valString))
				{
					valString = Integer.toString(getNumAttacks());
				}
				else if ("COUNT[CHECKS]".equals(valString))
				{
					valString = String.valueOf(SystemCollections.getUnmodifiableCheckList().size());
				}
				else if ("COUNT[FOLLOWERS]".equals(valString))
				{
					valString = Integer.toString(getFollowerList().size());
				}
				else if ("COUNT[STATS]".equals(valString))
				{
					valString = Integer.toString(Globals.s_ATTRIBLONG.length);
				}
				else if ("COUNT[SKILLS]".equals(valString))
				{
					getSkillList().trimToSize();
					valString = Integer.toString(getSkillList().size());
				}
				else if ("COUNT[FEATS.ALL]".equals(valString))
				{
					featList.trimToSize();
					valString = Integer.toString(featList.size());
				}
				else if ("COUNT[FEATS.HIDDEN]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(featList, false, true));
				}
				else if ("COUNT[FEATS]".equals(valString) || "COUNT[FEATS.VISIBLE]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(featList, true, false));
				}
				else if ("COUNT[VFEATS.ALL]".equals(valString))
				{
					valString = Integer.toString(vFeatList().size());
				}
				else if ("COUNT[VFEATS.HIDDEN]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(vFeatList(), false, true));
				}
				else if ("COUNT[VFEATS]".equals(valString) || "COUNT[VFEATS.VISIBLE]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(vFeatList(), true, false));
				}
				else if ("COUNT[FEATSAUTO.ALL]".equals(valString))
				{
					valString = Integer.toString(featAutoList().size());
				}
				else if ("COUNT[FEATSAUTO]".equals(valString) || "COUNT[FEATSAUTO.VISIBLE]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(featAutoList(), true, false));
				}
				else if ("COUNT[FEATSAUTO.HIDDEN]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(featAutoList(), false, true));
				}
				else if ("COUNT[FEATSALL]".equals(valString) || "COUNT[FEATSALL.VISIBLE]".equals(valString))
				{
					valString = Integer.toString(aggregateVisibleFeatList().size());
				}
				else if ("COUNT[FEATSALL.ALL]".equals(valString))
				{
					valString = Integer.toString(aggregateFeatList().size());
				}
				else if ("COUNT[FEATSALL.HIDDEN]".equals(valString))
				{
					valString = Integer.toString(countVisibleFeats(aggregateFeatList(), false, true));
				}
				else if ((valString.startsWith("COUNT[FEATTYPE=") || valString.startsWith("COUNT[FEATTYPE.")) && valString.endsWith(".ALL]"))
				{
					List featTypes = Utility.split(valString.substring(15, valString.length() - 5), '.');
					valString = Integer.toString(countVisibleFeatTypes(aggregateFeatList(), featTypes, true, true));
				}
				else if ((valString.startsWith("COUNT[FEATTYPE=")|| valString.startsWith("COUNT[FEATTYPE.")) && valString.endsWith(".HIDDEN]"))
				{
					List featTypes = Utility.split(valString.substring(15, valString.length() - 8), '.');
					valString = Integer.toString(countVisibleFeatTypes(aggregateFeatList(), featTypes, false, true));
				}
				else if ((valString.startsWith("COUNT[FEATTYPE=")|| valString.startsWith("COUNT[FEATTYPE.")) && valString.endsWith(".VISIBLE]"))
				{
					List featTypes = Utility.split(valString.substring(15, valString.length() - 9), '.');
					valString = Integer.toString(countVisibleFeatTypes(aggregateFeatList(), featTypes, true, false));
				}
				else if ((valString.startsWith("COUNT[FEATTYPE=") || valString.startsWith("COUNT[FEATTYPE.")) && valString.endsWith("]"))
				{
					List featTypes = Utility.split(valString.substring(15, valString.length() - 1), '.');
					valString = Integer.toString(countVisibleFeatTypes(aggregateFeatList(), featTypes, true, false));
				}
				else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME.")) && valString.endsWith(".ALL]"))
				{
					String featName = valString.substring(15, valString.length() - 5);
					valString = Integer.toString(countVisibleFeatNames(aggregateFeatList(), featName, true, true));
				}
				else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME.")) && valString.endsWith(".HIDDEN]"))
				{
					String featName = valString.substring(15, valString.length() - 8);
					valString = Integer.toString(countVisibleFeatNames(aggregateFeatList(), featName, false, true));
				}
				else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME.")) && valString.endsWith(".VISIBLE]"))
				{
					String featName = valString.substring(15, valString.length() - 9);
					valString = Integer.toString(countVisibleFeatNames(aggregateFeatList(), featName, true, false));
				}
				else if ((valString.startsWith("COUNT[FEATNAME=") || valString.startsWith("COUNT[FEATNAME.")) && valString.endsWith("]"))
				{
					String featName = valString.substring(15, valString.length() - 1);
					valString = Integer.toString(countVisibleFeatNames(aggregateFeatList(), featName, true, false));
				}
				else if (valString.startsWith("COUNT[SPELLSKNOWN") && valString.endsWith("]"))
				{
					int spellCount = 0;
					if (SettingsHandler.getPrintSpellsWithPC())
					{
						spellCount = countSpellListBook(valString);
					}
					valString = Integer.toString(spellCount);
				}
				else if (valString.startsWith("COUNT[SPELLSINBOOK") && valString.endsWith("]"))
				{
					valString = valString.substring(18);
					valString = valString.substring(0, valString.length() - 1);
					int sbookCount = 0;
					if (SettingsHandler.getPrintSpellsWithPC())
					{
						sbookCount = countSpellsInBook(valString);
					}
					valString = Integer.toString(sbookCount);
				}
				else if (valString.startsWith("COUNT[SPELLSLEVELSINBOOK") && valString.endsWith("]"))
				{
					valString = valString.substring(24);
					valString = valString.substring(0, valString.length() - 1);
					int sbookCount = countSpellLevelsInBook(valString);
					valString = Integer.toString(sbookCount);
				}
				else if (valString.startsWith("COUNT[SPELLTIMES") && valString.endsWith("]"))
				{
					valString = valString.substring(6);
					valString = valString.substring(0, valString.length() - 1);
					valString = String.valueOf(countSpellTimes(valString));
				}
				else if (valString.startsWith("COUNT[SPELLBOOKS") && valString.endsWith("]"))
				{
					valString = Integer.toString(getSpellBooks().size());
				}
				else if ("COUNT[SPELLCLASSES]".equals(valString))
				{
					valString = String.valueOf(getSpellClassCount());
				}
				else if ("COUNT[SPELLRACE]".equals(valString))
				{
					PObject aSpellRace = getSpellClassAtIndex(0);
					valString = (aSpellRace instanceof Race) ? "1" : "0";
				}
				else if ("COUNT[CLASSES]".equals(valString))
				{
					classList.trimToSize();
					int iCount = classList.size();
					if (SettingsHandler.hideMonsterClasses())
					{
						for (Iterator ee = classList.iterator(); ee.hasNext();)
						{
							final PCClass aClass = (PCClass) ee.next();
							if (aClass.isMonster())
							{
								--iCount;
							}
						}
					}
					valString = Integer.toString(iCount);
				}
				else if ("COUNT[DOMAINS]".equals(valString))
				{
					valString = Integer.toString(characterDomainList.size());
				}
				else if (valString.startsWith("COUNT[EQUIPMENT") && valString.endsWith("]"))
				{
					int merge = Constants.MERGE_ALL;
					// check to see how we are merging
					if (valString.indexOf("MERGENONE") > 0)
					{
						merge = Constants.MERGE_NONE;
					}
					else if (valString.indexOf("MERGELOC") > 0)
					{
						merge = Constants.MERGE_LOCATION;
					}

					ArrayList aList = new ArrayList();
					if (!getEquipmentListInOutputOrder(merge).isEmpty())
					{
						for (Iterator e = getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
						{
							Equipment eq = (Equipment) e.next();
							aList.add(eq);
						}
					}
					if ("COUNT[EQUIPMENT]".equals(valString))
					{
						valString = Integer.toString(aList.size());
					}
					else
					{
						StringTokenizer bTok = new StringTokenizer(valString.substring(16, valString.length() - 1), ".");
						while (bTok.hasMoreTokens())	//should be ok, assumes last two fields are # and a Param
						{
							String bString = bTok.nextToken();
							if ("NOT".equalsIgnoreCase(bString))
							{
								aList = new ArrayList(removeEqType(aList, bTok.nextToken()));
							}
							else if ("ADD".equalsIgnoreCase(bString))
							{
								aList = new ArrayList(addEqType(aList, bTok.nextToken()));
							}
							else if ("IS".equalsIgnoreCase(bString))
							{
								aList = new ArrayList(removeNotEqType(aList, bTok.nextToken()));
							}
						}
						valString = Integer.toString(aList.size());

					}
					aList.clear();
				}
				else if (valString.startsWith("COUNT[EQTYPE.") && valString.endsWith("]"))
				{
					int merge = Constants.MERGE_ALL;
					List aList = new ArrayList();
					StringTokenizer bTok = new StringTokenizer(valString.substring(13, valString.length() - 1), ".");
					String aType = bTok.nextToken();

					// check to see how we are merging equipment
					if ("MERGENONE".equals(aType))
					{
						merge = Constants.MERGE_NONE;
						aType = bTok.nextToken();
					}
					else if ("MERGELOC".equals(aType))
					{
						merge = Constants.MERGE_LOCATION;
						aType = bTok.nextToken();
					}

					if ("CONTAINER".equals(aType))
					{
						aList.clear();
						if (!getEquipmentListInOutputOrder(merge).isEmpty())
						{
							for (Iterator e = getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
							{
								Equipment eq = (Equipment) e.next();
								if (eq.acceptsChildren())
								{
									aList.add(eq);
								}
							}
						}
					}
					else
					{
						if ("WEAPON".equalsIgnoreCase(aType))
						{
							aList = getExpandedWeapons(merge);
						}
						else if ("ACITEM".equalsIgnoreCase(aType))
						{
							// special check for ACITEM
							// which is realy anything
							// with AC in the bonus section,
							// but is not type SHIELD or ARMOR
							if (!getEquipmentListInOutputOrder(merge).isEmpty())
							{
								for (Iterator e = getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
								{
									Equipment eq = (Equipment) e.next();
									if (eq.getBonusListString("AC") && !eq.isArmor() && !eq.isShield())
									{
										aList.add(eq);
									}
								}
							}
						}
						else
						{
							aList = getEquipmentOfTypeInOutputOrder(aType, 3, merge);
						}
					}

					while (bTok.hasMoreTokens())
					{
						String bString = bTok.nextToken();
						if ("NOT".equalsIgnoreCase(bString))
						{
							aList = new ArrayList(removeEqType(aList, bTok.nextToken()));
						}
						else if ("ADD".equalsIgnoreCase(bString))
						{
							aList = new ArrayList(addEqType(aList, bTok.nextToken()));
						}
						else if ("IS".equalsIgnoreCase(bString))
						{
							aList = new ArrayList(removeNotEqType(aList, bTok.nextToken()));
						}
						else if ("EQUIPPED".equalsIgnoreCase(bString) || "NOTEQUIPPED".equalsIgnoreCase(bString))
						{
							final boolean eFlag = "EQUIPPED".equalsIgnoreCase(bString);
							for (int ix = aList.size() - 1; ix >= 0; --ix)
							{
								Equipment anEquip = (Equipment) aList.get(ix);
								if (anEquip.isEquipped() != eFlag)
								{
									aList.remove(anEquip);
								}
							}
						}
					}

					valString = Integer.toString(aList.size());
					aList.clear();
				}
				else if ("COUNT[CONTAINERS]".equals(valString))
				{

					int merge = Constants.MERGE_ALL;

					ArrayList aList = new ArrayList();
					if (!getEquipmentListInOutputOrder(merge).isEmpty())
					{
						aList.clear();
						for (Iterator e = getEquipmentListInOutputOrder(merge).iterator(); e.hasNext();)
						{
							Equipment eq = (Equipment) e.next();
							if (eq.acceptsChildren())
							{
								aList.add(eq);
							}
						}

					}
					valString = Integer.toString(aList.size());
					aList.clear();
				}
				else if ("COUNT[SA]".equals(valString))
				{
					specialAbilityList.trimToSize();
					valString = String.valueOf(getSpecialAbilityTimesList().size());
				}
				else if ("COUNT[TEMPLATES]".equals(valString))
				{
					templateList.trimToSize();
					valString = String.valueOf(this.getTemplateList().size());
				}
				else if ("COUNT[VISIBLETEMPLATES]".equals(valString))
				{
					int count = 0;
					int visibility;
					for (Iterator it = this.getTemplateList().iterator(); it.hasNext();)
					{
						visibility = ((PCTemplate) it.next()).isVisible();
						if ((visibility == PCTemplate.VISIBILITY_DEFAULT) || (visibility == PCTemplate.VISIBILITY_OUTPUT_ONLY))
						{
							++count;
						}
					}
					valString = Integer.toString(count);
				}
				else if ("COUNT[LANGUAGES]".equals(valString))
				{
					valString = Integer.toString(getLanguagesList().size());
				}
				else if ("COUNT[NOTES]".equals(valString))
				{
					valString = Integer.toString(getNotesList().size());
				}
				else if (valString.startsWith("COUNT[FOLLOWERTYPE.") && valString.endsWith("]"))
				{
					if (valString.indexOf(".") == valString.lastIndexOf("."))
					{
						// This covers COUNT[FOLLOWERTYPE.Animal Companions] syntax
						int countFollower = 0;
						String bString = valString.substring(19);
						bString = bString.substring(0, bString.length() - 1);
						for (Iterator iter = getFollowerList().iterator(); iter.hasNext();)
						{
							Follower aFollower = (Follower) iter.next();
							if (aFollower.getType().equalsIgnoreCase(bString))
							{
								++countFollower;
							}
						}
						valString = String.valueOf(countFollower);
					}
					else
					{
						// This will do COUNT[FOLLOWERTYPE.Animal Companions.0.xxx],
						// returning the same as COUNT[xxx] if applied to the right follower
						final List followers = getFollowerList();
						if (!followers.isEmpty())
						{
							StringTokenizer aTok = new StringTokenizer(valString, "[]");
							aTok.nextToken(); // Remove the COUNT
							aString = aTok.nextToken();
							aTok = new StringTokenizer(aString, ".");
							aTok.nextToken();	// FOLLOWERTYPE
							String typeString = aTok.nextToken();
							String restString = "";
							int followerIndex = -1;
							if (aTok.hasMoreTokens())
							{
								restString = aTok.nextToken();
								// When removing old token syntax, remove the catch code
								followerIndex = Integer.parseInt(restString);
								restString = "";

								while (aTok.hasMoreTokens())
								{
									restString = restString + "." + aTok.nextToken();
								}
								if (restString.indexOf(".") == 0)
								{
									restString = restString.substring(1);
								}
							}
							restString = "COUNT[" + restString + "]";
							ArrayList aList = new ArrayList();
							for (int x = followers.size() - 1; x >= 0; --x)
							{
								final Follower fol = (Follower) followers.get(x);
								if (fol.getType().equalsIgnoreCase(typeString))
								{
									aList.add(fol);
								}
							}
							if (followerIndex < aList.size())
							{
								if (aList.get(followerIndex) instanceof Follower)
								{
									final Follower aF = (Follower) aList.get(followerIndex);
									PlayerCharacter currentPC;
									for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
									{
										PlayerCharacter nPC = (PlayerCharacter) p.next();
										if (aF.getFileName().equals(nPC.getFileName()))
										{
											currentPC = Globals.getCurrentPC();
											Globals.setCurrentPC(nPC);
											valString = nPC.getVariableValue(restString, "").toString();
											Globals.setCurrentPC(currentPC);
										}
									}
								}
							}
						}
					}
				}
				else if (valString.startsWith("EQTYPE"))
				{
					valString = ExportHandler.returnReplacedTokenEq(this, valString);
				}
				else if (valString.startsWith("VARDEFINED:"))
				{
					if (hasVariable(valString.substring(11).trim()))
					{
						valString = "1";
					}
					else
					{
						valString = "0";
					}
				}
				else if (valString.startsWith("HASFEAT:"))
				{
					valString = valString.substring(8).trim();
					if (hasFeat(valString))
					{
						valString = "1";
					}
					else
					{
						valString = "0";
					}
				}
				else if (valString.startsWith("HASDEITY:"))
				{
					valString = valString.substring(9).trim();
					if (hasDeity(valString))
					{
						valString = "1";
					}
					else
					{
						valString = "0";
					}
				}
				else if (valString.startsWith("MODEQUIP"))
				{
					valString = String.valueOf(modToFromEquipment(valString.substring(8)));
				}
				else if (valString.length() > 0)
				{
					if (hasVariable(valString))
					{
						valString = getVariable(valString, true, true, "", "").toString();
					}
					else
					{
						double a = 0.0;
						try
						{
							a = Float.parseFloat(valString);
						}
						catch (NumberFormatException exc)
						{
							a = getTotalBonusTo("VAR", valString);
						}
						if (!Utility.doublesEqual(a, 0.0))
						{
							valString = String.valueOf(a);
						}
						else
						{
							StringWriter sWriter = new StringWriter();
							BufferedWriter aWriter = new BufferedWriter(sWriter);
							final ExportHandler aExport = new ExportHandler(new File(""));
							aExport.replaceTokenSkipMath(this, valString, aWriter);
							sWriter.flush();
							try
							{
								aWriter.flush();
							}
							catch (IOException e)
							{
								Logging.errorPrint("Couldn't flush the StringWriter used in PlayerCharacter.getVariableValue.", e);
							}
							final String bString = sWriter.toString();
							try
							{
								// Float values
								valString = String.valueOf(Float.parseFloat(bString));
							}
							catch (NumberFormatException e)
							{
								// String values
								valString = bString;
							}
						}
					}
				}
				if (i < aString.length())
				{
					if (valString.endsWith(".TRUNC"))
					{
						valString = String.valueOf(getVariableValue(valString.substring(0, valString.length() - 6), "").intValue());
					}
					if (valString.endsWith(".INTVAL"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 7), "").toString();
						endMode += 10;
					}
					if (valString.endsWith("MIN"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "").toString();
						nextMode = 0;
						++endMode;
					}
					else if (valString.endsWith("MAX"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "").toString();
						nextMode = 0;
						endMode += 2;
					}
					else if (valString.endsWith("REQ"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "").toString();
						nextMode = 0;
						endMode += 3;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '+')
					{
						nextMode = 0;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '-')
					{
						nextMode = 1;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '*')
					{
						nextMode = 2;
					}
					else if (aString.length() > 0 && aString.charAt(i) == '/')
					{
						nextMode = 3;
					}
				}
				try
				{
					if (valString.length() > 0)
					{
						switch (mode)
						{
							case 0:
								total = new Float(total.doubleValue() + Double.parseDouble(valString));
								break;
							case 1:
								total = new Float(total.doubleValue() - Double.parseDouble(valString));
								break;
							case 2:
								total = new Float(total.doubleValue() * Double.parseDouble(valString));
								break;
							case 3:
								total = new Float(total.doubleValue() / Double.parseDouble(valString));
								break;
							default:
								Logging.errorPrint("In PlayerCharacter.getVariableValue the mode " + mode + " is unsupported.");
								break;
						}
					}
				}
				catch (NumberFormatException exc)
				{
					// Don't care, as it's just zero
					//Logging.debugPrint("Will use default for total: " + total, exc);
				}
				mode = nextMode;
				nextMode = 0;
				valString = "";
				if (total1 == null && endMode % 10 != 0)
				{
					total1 = total;
					total = new Float(0.0);
				}
			}
		}
		if (total1 != null)
		{
			if (endMode % 10 == 1)
			{
				total = new Float(Math.min(total.doubleValue(), total1.doubleValue()));
			}
			if (endMode % 10 == 2)
			{
				total = new Float(Math.max(total.doubleValue(), total1.doubleValue()));
			}
			if (endMode % 10 == 3)
			{
				if (total1.doubleValue() < total.doubleValue())
				{
					total = new Float(0.0);
				}
				else
				{
					total = total1;
				}
			}
		}
		if (endMode / 10 > 0)
		{
			total = new Float(total.intValue());
		}

		return total;
	}

	/** <code>rollStats</code> roll Globals.s_ATTRIBLONG.length random stats
	 * Method:
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * 7: 5d6 Drop lowest and middle as per FR #458917
	 * @param method the method to be used for rolling.
	 **/
	private void rollStats(int method)
	{
		int roll;
		for (Iterator stat = statList.getStats().iterator(); stat.hasNext();)
		{
			PCStat currentStat = (PCStat) stat.next();
			currentStat.setBaseScore(0);
			if (SettingsHandler.isPurchaseStatMode())
			{
				currentStat.setBaseScore(SettingsHandler.getPurchaseModeBaseStatScore());
				continue;
			}
			switch (method)
			{
				case 0:
					roll = 0;
					break;
				case 8:
					roll = SettingsHandler.getAllStatsValue();
					break;
				default:
					roll = 10;
					break;
			}
			roll += currentStat.getBaseScore();
			if (roll < currentStat.getMinValue())
			{
				roll = currentStat.getMinValue();
			}
			if (roll > currentStat.getMaxValue())
			{
				roll = currentStat.getMaxValue();
			}
			currentStat.setBaseScore(roll);
		}
		this.setPoolAmount(0);
		this.costPool = 0;
		getLanguagesList().clear();
		getAutoLanguages();
		setPoolAmount(0);
	}

	private boolean includeSkill(Skill skill, int level)
	{
		boolean UntrainedExclusiveClass = false;
		String tempSkill = skill.getUntrained();
		if (tempSkill.length() > 0 && tempSkill.charAt(0) == 'Y' && skill.isExclusive())
		{
			if (skill.isClassSkill(classList))
			{
				UntrainedExclusiveClass = true;
			}
		}

		return (level == 2) || skill.isRequired() || (skill.getTotalRank().floatValue() > 0) || ((level == 1) && tempSkill.length() > 0 && tempSkill.charAt(0) == 'Y' && !skill.isExclusive()) || ((level == 1) && UntrainedExclusiveClass);
	}

	private void addNewSkills(int level)
	{
		final List addItems = new LinkedList();
		final Iterator skillIter = Globals.getSkillList().iterator();
		Skill aSkill;
		while (skillIter.hasNext())
		{
			aSkill = (Skill) skillIter.next();
			if (includeSkill(aSkill, level) && (Globals.binarySearchPObject(getSkillList(), aSkill.getKeyName()) == null))
			{
				addItems.add(aSkill.clone());
			}
		}
		getSkillList().addAll(addItems);
	}

	private void removeExcessSkills(int level)
	{
		final Iterator skillIter = getSkillList().iterator();
		Skill skill;
		while (skillIter.hasNext())
		{
			skill = (Skill) skillIter.next();
			if (!includeSkill(skill, level))
			{
				skillIter.remove();
			}
		}
	}

	/*
	 */
	public void populateSkills(int level)
	{
		Globals.sortPObjectList(getSkillList());
		removeExcessSkills(level);
		addNewSkills(level);
	}

	/*
	 */
	public int modFromArmorOnWeaponRolls()
	{
		int bonus = 0;
		Equipment eq;
		for (Iterator e = getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
		{
			eq = (Equipment) e.next();
			if (!isProficientWith(eq))
			{
				bonus += eq.acCheck().intValue();
			}
		}
		if (!hasFeat(Constants.s_ShieldProficiency) && !hasFeatAutomatic(Constants.s_ShieldProficiency))
		{
			final List aArrayList = getEquipmentOfType("Shield", 1);
			if (aArrayList.size() > 0)
			{
				final Equipment eq2 = (Equipment) aArrayList.get(0);
				bonus += eq2.acCheck().intValue();
			}
		}
		return bonus;
	}

	/*
	 */
	public int modToFromEquipment(String typeName)
	{
		int bonus = 0;
		int statBonus = 0;
		int used = 0;
		int old = 0;
		boolean isMaxDex = false;
		boolean isACCheck = false;
		boolean isSpellFailure = false;
		boolean isAC = false;
		if ("MAXDEX".equals(typeName))
		{
			isMaxDex = true;
		}
		else if ("ACCHECK".equals(typeName))
		{
			isACCheck = true;
		}
		else if ("SPELLFAILURE".equals(typeName))
		{
			isSpellFailure = true;
		}
		else if ("AC".equals(typeName))
		{
			isAC = true;
		}

		if (isMaxDex)
		{
			statBonus = (int) getStatBonusTo("MISC", "MAXDEX");
			bonus = statBonus;
		}
		int load = Constants.LIGHT_LOAD;
		if (SettingsHandler.isApplyLoadPenaltyToACandSkills())
		{
			load = Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight());
		}

		if ((load == Constants.MEDIUM_LOAD) && isACCheck)
		{
			old = -3;
		}
		else if ((load == Constants.HEAVY_LOAD) && isACCheck)
		{
			old = -6;
		}
		else if ((load == Constants.MEDIUM_LOAD) && isMaxDex)
		{
			used = 1;
			bonus = 3;
		}
		else if ((load == Constants.HEAVY_LOAD) && isMaxDex)
		{
			used = 1;
			bonus = 1;
		}
		else if ((load == Constants.OVER_LOAD) && isMaxDex)
		{
			used = 1;
			bonus = 0;
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (!eq.isEquipped())
			{
				continue;
			}
			if (isAC)
			{
				bonus += eq.getACMod().intValue();
			}
			else if (isACCheck)
			{
				bonus += eq.acCheck().intValue();
			}
			else if (isSpellFailure)
			{
				bonus += eq.spellFailure().intValue();
			}
			else if (isMaxDex)
			{
				old = eq.getMaxDex().intValue();
				if (old == Constants.MAX_MAXDEX)
				{
					continue;
				}
				if ((used == 0) || (bonus > old))
				{
					bonus = old;
				}
				used = 1;
			}
		}
		if (isSpellFailure)
		{
			bonus += (int) getTotalBonusTo("MISC", "SPELLFAILURE");
		}
		else if (isACCheck)
		{
			bonus = Math.min(bonus, old);
			bonus += (int) getTotalBonusTo("MISC", "ACCHECK");
		}
		else if (isMaxDex)
		{
			if (used == 0)
			{
				bonus = Constants.MAX_MAXDEX;
			}
			bonus += (int) getTotalBonusTo("MISC", "MAXDEX") - statBonus;
			if (bonus < 0)
			{
				bonus = 0;
			}
			else if (bonus > Constants.MAX_MAXDEX)
			{
				bonus = Constants.MAX_MAXDEX;
			}
		}
		return bonus;
	}

	/*
	 * returns true if Equipment is in the primary weapon list
	 */
	public boolean isPrimaryWeapon(Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}
		for (Iterator e = primaryWeapons.iterator(); e.hasNext();)
		{
			Equipment eqI = (Equipment) e.next();
			if (eqI.getName().equalsIgnoreCase(eq.getName()) && eqI.getLocation() == eq.getLocation())
			{
				return true;
			}
		}
		return false;
	}

	/*
	 * returns true if Equipment is in the secondary weapon list
	 */
	public boolean isSecondaryWeapon(Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}
		for (Iterator e = secondaryWeapons.iterator(); e.hasNext();)
		{
			Equipment eqI = (Equipment) e.next();
			if (eqI.getName().equalsIgnoreCase(eq.getName()) && eqI.getLocation() == eq.getLocation())
			{
				return true;
			}
		}
		return false;
	}

	/*
	 */
	public boolean loadDescriptionFilesInDirectory(String aDirectory)
	{
		new File(aDirectory).list(new FilenameFilter()
		{
			public boolean accept(File aFile, String aString)
			{
				if (aString.endsWith(".lst"))
				{
					try
					{
						final File descriptionFile = new File(aFile.getPath() + File.separator + aString);
						if (descriptionFile.exists())
						{
							final char[] inputLine;
							//final BufferedReader descriptionReader = new BufferedReader(new FileReader(descriptionFile));
							final BufferedReader descriptionReader = new BufferedReader(new InputStreamReader(new FileInputStream(descriptionFile), "UTF-8"));
							final int length = (int) descriptionFile.length();
							inputLine = new char[length];
							descriptionReader.read(inputLine, 0, length);
							descriptionLst += new String(inputLine);
						}
					}
					catch (IOException exception)
					{
						//TODO: Should this really be ignored???
					}

				}
				else if (aFile.isDirectory())
				{
					loadDescriptionFilesInDirectory(aFile.getPath() + File.separator + aString);
				}
				return false;
			}
		});
		return false;
	}

	public void addKit(Kit aKit)
	{
		if (kitList == null)
		{
			kitList = new ArrayList();
		}
		kitList.add(aKit);
	}

	public List getKitInfo()
	{
		return kitList;
	}

	public int getLevelInfoSize()
	{
		return pcLevelInfo.size();
	}

	public List getLevelInfo()
	{
		return pcLevelInfo;
	}

	public String getLevelInfoClassKeyName(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return ((PCLevelInfo) pcLevelInfo.get(idx)).getClassKeyName();
		}
		return "";
	}

	public int getLevelInfoClassLevel(int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return ((PCLevelInfo) pcLevelInfo.get(idx)).getLevel();
		}
		return 0;
	}

	public PCLevelInfo saveLevelInfo(final String classKeyName)
	{
		PCLevelInfo li = new PCLevelInfo(classKeyName);
		pcLevelInfo.add(li);
		return li;
	}

	public void saveStatIncrease(final String statAbb, final int mod, final boolean isPreMod)
	{
		final int idx = getLevelInfoSize() - 1;
		if (idx >= 0)
		{
			((PCLevelInfo) pcLevelInfo.get(idx)).addModifiedStat(statAbb, mod, isPreMod);
		}
		setDirty(true);
	}

	/**
	 * Get the value of the desired stat at the point just before
	 * the character was raised to the next level
	 **/
	public int getTotalStatAtLevel(final String statAbb, final int level, final boolean includePost, final boolean includeEquipment)
	{
		int curStat = getStatList().getTotalStatFor(statAbb);
		if (!includeEquipment)
		{
			curStat -= getEquipmentBonusTo("STAT", statAbb);
		}
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust = ((PCLevelInfo) pcLevelInfo.get(idx)).getTotalStatMod(statAbb, includePost);
			curStat -= statLvlAdjust;
		}
		return curStat;
	}

	private boolean removeLevelInfo(final String classKeyName)
	{
		for (int idx = pcLevelInfo.size() - 1; idx >= 0; --idx)
		{
			final PCLevelInfo li = (PCLevelInfo) pcLevelInfo.get(idx);
			if (li.getClassKeyName().equals(classKeyName))
			{
				removeLevelInfo(idx);
				return true;
			}
		}
		return false;
	}

	public PCLevelInfo getLevelInfoFor(String className, int level)
	{
		for (Iterator i = pcLevelInfo.iterator(); i.hasNext();)
		{
			PCLevelInfo pcl = (PCLevelInfo) i.next();
			if (pcl.getClassKeyName().equals(className))
			{
				level--;
			}
			if (level <= 0)
			{
				return pcl;
			}
		}
		return null;
	}

	private void removeLevelInfo(final int idx)
	{
		pcLevelInfo.remove(idx);
	}

	/**
	 * availableSpells
	 * sk4p 13 Dec 2002
	 *
	 * For learning or preparing a spell: Are there slots available at this level or higher?
	 * Fixes BUG [569517]
	 *
	 * @param level		   the level being checked for availability
	 *	  aClass	   the class under consideration
	 *	  bookName	   the name of the spellbook
	 *	  knownLearned	   "true" if this is learning a spell, "false" if prepping
	 *	  isSpecialtySpell "true" iff this is a specialty for the given class
	 * @return		   true or false, a new spell can be added
	 **/
	private boolean availableSpells(int level, PCClass aClass, String bookName, boolean knownLearned, boolean isSpecialtySpell)
	{
		boolean available = false;
		boolean isDivine = ("Divine".equalsIgnoreCase(aClass.getSpellType()));
		int knownTot, knownNon, knownSpec, i;
		int memTot, memNon, memSpec;
		//int excTot
		int excNon, excSpec;
		int lowExcSpec = 0, lowExcNon = 0;
		int goodExcSpec = 0, goodExcNon = 0;
		for (i = 0; i < level; ++i)
		{
			// Get the number of castable slots
			if (knownLearned)
			{
				knownNon = aClass.getKnownForLevel(aClass.getLevel(), i, bookName);
				knownSpec = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), i);
				knownTot = knownNon + knownSpec; // for completeness
			}
			else
			{
				// Get the number of castable slots
				knownTot = aClass.getCastForLevel(aClass.getLevel(), i, bookName, true, true);
				knownNon = aClass.getCastForLevel(aClass.getLevel(), i, bookName, false, true);
				knownSpec = knownTot - knownNon;
			}

			// Now get the number of spells memorized, total and specialties
			memTot = aClass.memorizedSpellForLevelBook(i, bookName);
			memSpec = aClass.memorizedSpecialtiesForLevelBook(i, bookName);
			memNon = memTot - memSpec;

			// Excess castings
			excSpec = knownSpec - memSpec;
			excNon = knownNon - memNon;

			// Now we spend these slots making up any deficits in lower levels
			//
			while (excNon > 0 && lowExcNon < 0)
			{
				--excNon;
				++lowExcNon;
			}
			while (excSpec > 0 && lowExcSpec < 0)
			{
				--excSpec;
				++lowExcSpec;
			}

			if (!isDivine || knownLearned)
			{
				// If I'm not divine, I can use non-specialty slots of this level
				// to take up the slack of my excess specialty spells from
				// lower levels.
				while (excNon > 0 && lowExcSpec < 0)
				{
					--excNon;
					++lowExcSpec;
				}

				// And I can use non-specialty slots of this level to take
				// up the slack of my excess specialty spells of this level.
				//
				while (excNon > 0 && excSpec < 0)
				{
					--excNon;
					++excSpec;
				}
			}

			// Now, if there are slots left over, I don't add them to the running totals.
			// Spell slots of this level won't help me at the next level.
			// Deficits, however, will have to be made up at the next level.
			//
			if (excSpec < 0)
			{
				lowExcSpec += excSpec;
			}
			if (excNon < 0)
			{
				lowExcNon += excNon;
			}

		}

		for (i = level; ; ++i)
		{
			if (knownLearned)
			{
				knownNon = aClass.getKnownForLevel(aClass.getLevel(), i, bookName);
				knownSpec = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), i);
				knownTot = knownNon + knownSpec; // for completeness
			}
			else
			{
				// Get the number of castable slots
				knownTot = aClass.getCastForLevel(aClass.getLevel(), i, bookName, true, true);
				knownNon = aClass.getCastForLevel(aClass.getLevel(), i, bookName, false, true);
				knownSpec = knownTot - knownNon;
			}

			// At the level currently being looped through, if the number of casts
			// is zero, that means we have reached a level beyond which no higher-level
			// casts are possible.	Therefore, it's time to break.
			//
			if ((knownLearned && (knownNon + knownSpec == 0)) || (!knownLearned && knownTot == 0))
			{
				break;
			}

			// Now get the number of spells memorized, total and specialties
			memTot = aClass.memorizedSpellForLevelBook(i, bookName);
			memSpec = aClass.memorizedSpecialtiesForLevelBook(i, bookName);
			memNon = memTot - memSpec;

			// Excess castings
			excSpec = knownSpec - memSpec;
			excNon = knownNon - memNon;

			// Now we spend these slots making up any deficits in lower levels
			//
			while (excNon > 0 && lowExcNon < 0)
			{
				--excNon;
				++lowExcNon;
			}
			while (excNon > 0 && goodExcNon < 0)
			{
				--excNon;
				++goodExcNon;
			}
			while (excSpec > 0 && lowExcSpec < 0)
			{
				--excSpec;
				++lowExcSpec;
			}
			while (excSpec > 0 && goodExcSpec < 0)
			{
				--excSpec;
				++goodExcSpec;
			}

			if (!isDivine)
			{
				// If I'm not divine, I can use non-specialty slots of this level
				// to take up the slack of my excess specialty spells from
				// lower levels.
				while (excNon > 0 && lowExcSpec < 0)
				{
					--excNon;
					++lowExcSpec;
				}

				// And also for levels sufficiently high for the spell that got me
				// into this mess, but of lower level than the level currently
				// being calculated.
				while (excNon > 0 && goodExcSpec < 0)
				{
					--excNon;
					++goodExcSpec;
				}

				// And finally use non-specialty slots of this level to take
				// up the slack of excess specialty spells of this level.
				//
				while (excNon > 0 && excSpec < 0)
				{
					--excNon;
					++excSpec;
				}

			}

			// Right now, if there are slots left over at this level,
			// it means that there are slots left to add the spell that started
			// all of this.

			if (isDivine)
			{
				if (isSpecialtySpell && excSpec > 0)
				{
					available = true;
				}
				if (!isSpecialtySpell && excNon > 0)
				{
					available = true;
				}
			}
			else
			{
				if (!isSpecialtySpell && excNon > 0)
				{
					available = true;
				}
				if (isSpecialtySpell && (excNon > 0 || excSpec > 0))
				{
					available = true;
				}
			}

			// If we found a slot, we need look no further.
			if (available)
			{
				break;
			}

			// Now, if there are slots left over, I don't add them to the running totals.
			// Spell slots of this level won't help me at the next level.
			// Deficits, however, will have to be made up at the next level.
			//
			if (excSpec < 0)
			{
				goodExcSpec += excSpec;
			}
			if (excNon < 0)
			{
				goodExcNon += excNon;
			}
		}

		return available;
	}

	public int getTwoHandDamageDivisor()
	{
		int div = getVariableValue("TWOHANDDAMAGEDIVISOR", "").intValue();
		if (div == 0)
		{
			div = 2;
		}
		return div;
	}

	private String getClassLevelString(String className, final boolean doReplace)
	{
		int lvl = 0;
		int idx = className.indexOf(";BEFORELEVEL=");
		if (idx < 0)
		{
			idx = className.indexOf(";BEFORELEVEL.");
		}
		if (idx > 0)
		{
			lvl = Integer.parseInt(className.substring(idx + 13));
			className = className.substring(0, idx);
		}
		if (doReplace)
		{
			className = className.replace('{', '(').replace('}', ')');
		}
		final PCClass aClass = getClassNamed(className);
		if (aClass != null)
		{
			if (lvl > 0)
			{
				return getLevelBefore(aClass.getKeyName(), lvl);
			}
			return Integer.toString(aClass.getLevel());
		}
		return "0";
	}

	private String getLevelBefore(final String classKey, final int charLevel)
	{
		String thisClassKey;
		int lvl = 0;
		for (int idx = 0; idx < charLevel; ++idx)
		{
			thisClassKey = getLevelInfoClassKeyName(idx);
			if (thisClassKey.length() == 0)
			{
				break;
			}
			if (thisClassKey.equals(classKey))
			{
				++lvl;
			}
		}
		return Integer.toString(lvl);
	}

	/**
	 * This function takes a list of feats and returns the number
	 * of visible, or hidden feats that are in the list
	 * The visible flag determines if the result should be
	 * the number of hidden feats, or the number of visible feats
	 *
	 * @param argFeatList The list of feats to look through.
	 * @param countVisible Look for visible feats (true) or hidden feats (false)
	 * @return An int containing the number of feats in the list
	 **/
	private int countVisibleFeats(List argFeatList, boolean countVisible, boolean countHidden)
	{
		Iterator itr = argFeatList.iterator();
		int count = 0;
		while (itr.hasNext())
		{
			Feat feat = (Feat) itr.next();
			count += countVisibleFeat(feat, countVisible, countHidden, true);
		}
		return count;
	}

	private int countVisibleFeatTypes(List featsList, List featTypesList, boolean countVisible, boolean countHidden)
	{
		int count = 0;
		for (Iterator e1 = featsList.iterator(); e1.hasNext();)
		{
			final Feat aFeat = (Feat) e1.next();
			// for each feat, look to see if it has any of the required types.
			for (Iterator e2 = featTypesList.iterator(); e2.hasNext();)
			{
				final String featType = (String) e2.next();
				if (aFeat.isType(featType))
				{
					count += countVisibleFeat(aFeat, countVisible, countHidden, false);
					break;
				}
			}
		}
		return count;
	}

	private int countVisibleFeat(Feat feat, boolean countVisible, boolean countHidden, boolean onceOnly)
	{
		int visibility = feat.isVisible();
		int count = 0;
		if (countVisible)
		{
			if (visibility != Feat.VISIBILITY_DISPLAY_ONLY && visibility != Feat.VISIBILITY_HIDDEN)
			{
				if (onceOnly)
				{
					count++;
				}
				else
				{
					count += Math.max(1, feat.getAssociatedCount());
				}
			}
		}
		if (countHidden)
		{
			if (visibility == Feat.VISIBILITY_DISPLAY_ONLY || visibility == Feat.VISIBILITY_HIDDEN)
			{
				if (onceOnly)
				{
					count++;
				}
				else
				{
					count += Math.max(1, feat.getAssociatedCount());
				}
			}
		}
		return count;
	}

	private int countVisibleFeatNames(List argFeatList, String featName, boolean countVisible, boolean countHidden)
	{
		int count = 0;
		for (Iterator e1 = argFeatList.iterator(); e1.hasNext();)
		{
			final Feat aFeat = (Feat) e1.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
			{
				count += countVisibleFeat(aFeat, countVisible, countHidden, false);
				break;
			}
		}
		return count;
	}

	public boolean getSpellLevelforKey(String key, int levelMatch)
	{
		buildSpellLevelMap(levelMatch);
		if (!spellLevelMap.containsKey(key))
		{
			return false;
		}
		int levelInt = -1;
		try
		{
			levelInt = Integer.parseInt((String) spellLevelMap.get(key));
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		if (levelMatch == levelInt)
		{
			return true;
		}
		return false;
	}

	public void buildSpellLevelMap(int levelMatch)
	{
		Iterator e;
		spellLevelMap.clear();
		if (!classList.isEmpty())
		{
			e = classList.iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (!companionModList.isEmpty())
		{
			e = companionModList.iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (!equipmentList.isEmpty())
		{
			e = equipmentList.iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (!aggregateFeatList().isEmpty())
		{
			e = aggregateFeatList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (!templateList.isEmpty())
		{
			e = templateList.iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (!characterDomainList.isEmpty())
		{
			e = characterDomainList.iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (!getSkillList().isEmpty())
		{
			e = getSkillList().iterator();
			buildSpellLevelMap(levelMatch, e);
		}
		if (getRace() != null)
		{
			spellLevelMap.putAll(getRace().getSpellMapPassesPrereqs(levelMatch));
		}
		if (getDeity() != null)
		{
			spellLevelMap.putAll(getDeity().getSpellMapPassesPrereqs(levelMatch));
		}
	}

	private void buildSpellLevelMap(int levelMatch, Iterator e)
	{
		for (; e.hasNext();)
		{
			Object obj = e.next();
			if (obj instanceof CharacterDomain)
			{
				obj = ((CharacterDomain) obj).getDomain();
			}
			if (!(obj instanceof PObject))
			{
				continue;
			}
			final PObject pObj = (PObject) obj;
			spellLevelMap.putAll(pObj.getSpellMapPassesPrereqs(levelMatch));
		}
	}

	/**
	 * Returns the autoSortGear.
	 * @return boolean
	 */
	public boolean isAutoSortGear()
	{
		return autoSortGear;
	}

	/**
	 * Returns the autoSortSkills.
	 * @return boolean
	 */
	public boolean isAutoSortSkills()
	{
		return autoSortSkills;
	}

	/**
	 * Sets the autoSortGear.
	 * @param autoSortGear The autoSortGear to set
	 */
	public void setAutoSortGear(boolean autoSortGear)
	{
		this.autoSortGear = autoSortGear;
	}

	/**
	 * Sets the autoSortSkills.
	 * @param autoSortSkills The autoSortSkills to set
	 */
	public void setAutoSortSkills(boolean autoSortSkills)
	{
		this.autoSortSkills = autoSortSkills;
	}

	public void buildSpellInfoMap(String key)
	{
		Iterator e;
		spellInfoMap.clear();
		if (!classList.isEmpty())
		{
			e = classList.iterator();
			buildSpellInfoMap(key, e);
		}
		if (!companionModList.isEmpty())
		{
			e = companionModList.iterator();
			buildSpellInfoMap(key, e);
		}
		if (!equipmentList.isEmpty())
		{
			e = equipmentList.iterator();
			buildSpellInfoMap(key, e);
		}
		if (!aggregateFeatList().isEmpty())
		{
			e = aggregateFeatList().iterator();
			buildSpellInfoMap(key, e);
		}
		if (!templateList.isEmpty())
		{
			e = templateList.iterator();
			buildSpellInfoMap(key, e);
		}
		if (!characterDomainList.isEmpty())
		{
			e = characterDomainList.iterator();
			buildSpellInfoMap(key, e);
		}
		if (!getSkillList().isEmpty())
		{
			e = getSkillList().iterator();
			buildSpellInfoMap(key, e);
		}
		if (getRace() != null)
		{
			spellInfoMap.putAll(getRace().getSpellInfoMapPassesPrereqs(key));
		}
		if (getDeity() != null)
		{
			spellInfoMap.putAll(getDeity().getSpellInfoMapPassesPrereqs(key));
		}
	}

	private void buildSpellInfoMap(String key, Iterator e)
	{
		for (; e.hasNext();)
		{
			Object obj = e.next();
			if (obj instanceof CharacterDomain)
			{
				obj = ((CharacterDomain) obj).getDomain();
			}
			if (!(obj instanceof PObject))
			{
				continue;
			}
			final PObject pObj = (PObject) obj;
			spellInfoMap.putAll(pObj.getSpellInfoMapPassesPrereqs(key));
		}
	}
	
	public Map getSpellInfoMap(String key)
	{
		buildSpellInfoMap(key);
		return spellInfoMap;
	}
	
	public List getCompanionModList()
	{
		return companionModList;
	}

	public boolean getUseMasterSkill()
	{
		for (Iterator e = companionModList.iterator(); e.hasNext(); )
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			if (cMod.getUseMasterSkill())
			{
				return true;
			}
		}
		return false;
	}

	public String getCopyMasterBAB()
	{
		String masterBAB = "";
		for (Iterator e = companionModList.iterator(); e.hasNext(); )
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			if (cMod.getCopyMasterBAB() != null)
			{
				masterBAB = cMod.getCopyMasterBAB();
			}
		}
		return masterBAB;
	}

	public String getCopyMasterHP()
	{
		String masterHP = "";
		for (Iterator e = companionModList.iterator(); e.hasNext(); )
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			if (cMod.getCopyMasterHP() != null)
			{
				masterHP = cMod.getCopyMasterHP();
			}
		}
		return masterHP;
	}

	public String getCopyMasterCheck()
	{
		String masterCheck = "";
		for (Iterator e = companionModList.iterator(); e.hasNext(); )
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			if (cMod.getCopyMasterCheck() != null)
			{
				masterCheck = cMod.getCopyMasterCheck();
			}
		}
		return masterCheck;
	}
}
