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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id: PlayerCharacter.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.gui.PCGen_Frame1;
import pcgen.io.ExportHandler;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;

/**
 * <code>PlayerCharacter</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class PlayerCharacter extends Object
{
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

	/* Constants for use in getBonus */
	public static final int ATTACKBONUS = 0;
	public static final int MONKBONUS = 4;

	private static final BigDecimal BIG_ONE = new BigDecimal("1.00");
	private String DPoints = "0";
	private int FPoints = 0;
	/*
	* Used to Force Encumberance to a Given type for BASEMOVEMENT
	*
	* Author: Tim Evans 24-07-02
	*/
	private int iForceLoad = -1;

	private int age = 0; // in years
	/** Whether one can trust the most recently calculated aggregateFeatList */
	private boolean aggregateFeatsStable = false;
	private int alignment = 9; // 0 = LG to 8 = CE and 9 is <none selected>
	/** whether to add auto known spells each level */
	private boolean autoKnownSpells = true;
	/** Whether one can trust the most recently calculated automaticFeatList */
	private boolean automaticFeatsStable = false;
	private String bio = new String();
	private final HashMap bonusMap = new HashMap();
	private String catchPhrase = "";
	private final ArrayList characterDomainList = new ArrayList(); // of CharacterDomain
	private ArrayList classList = new ArrayList(); // of Class
	private final ArrayList companionModList = new ArrayList(); // of CompanionMod
	private int costPool = 0;
	private String currentEquipSetName = "";
	private String calcEquipSetId = "0.1";
	private int currentHP = 0;
	private static int decrement;
	private Deity deity = null;
	private String description = new String();
	private String descriptionLst = "EMPTY";
	private boolean dirtyFlag = false;
	private final ArrayList equipSetList = new ArrayList(); // of Equip Sets
	private ArrayList equipmentList = new ArrayList(); // of Equipment
	private ArrayList equipmentMasterList = new ArrayList();
	private int earnedXP = 0;
	private int nextLevelXP = 0;
	private String eyeColor = "";
	private final TreeSet favoredClasses = new TreeSet();
	private final ArrayList featList = new ArrayList(); // of Feat
	private int feats = 0; // pool of feats remaining to distribute
	/** This may be different from character name... */
	private String fileName = "";
	private final ArrayList followerList = new ArrayList(); // of Followers
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
	private final TreeSet languages = new TreeSet();
	private static String lastVariable = null;
	private String location = "";
	private String birthplace = "";
	private static String loopVariable = "";
	private final ArrayList miscList = new ArrayList(3);
	private String[] movementTypes;
	private Integer[] movements;
	private String name = new String();
	private final int nonProficiencyPenalty = -4;
	private final ArrayList notesList = new ArrayList(); // of Notes
	private String phobias = "";
	private String playersName = new String();
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
	private final ArrayList rapidshotWeapons = new ArrayList();
	private final ArrayList skillList = new ArrayList(); // of Skill
	private int skillPoints = 0; // pool of skills remaining to distribute
	private String skinColor = "";
	/** Collections of String (probably should be full objects) */
	private final ArrayList specialAbilityList = new ArrayList();
	private String speechTendency = "";
	private final ArrayList spellBooks = new ArrayList();
	/** Only access this through getStableAggregateFeatList */
	private ArrayList stableAggregateFeatList = null;
	/** Only access this through getStableAutomaticFeatList */
	private ArrayList stableAutomaticFeatList = null;
	/** Only access this through getStableVirtualFeatList */
	private ArrayList stableVirtualFeatList = null;
	private String tabName = new String();
	/**
	 * We don't want this list sorted until after it has been added
	 * to the character.  The reason is that sorting prevents
	 * .CLEAR-TEMPLATES from clearing the OLDER template languages.
	 * ---arcady june 1, 2002
	 */
	private final ArrayList templateAutoLanguages = new ArrayList();
	private final TreeSet templateLanguages = new TreeSet();
	private final ArrayList templateList = new ArrayList(); // of Template
	private String trait1 = "";
	private String trait2 = "";
	private final boolean useMonsterDefault = SettingsHandler.isMonsterDefault();
	private final ArrayList variableList = new ArrayList();
	/** Whether one can trust the most recently calculated virtualFeatList */
	private boolean virtualFeatsStable = false;
	private final TreeSet weaponProfList = new TreeSet();
	private final ArrayList armorProfList = new ArrayList();
	private boolean armorProfListStable = false;
	private int weight = 0; // in pounds
	private final StatList statList = new StatList();
	private String region = null;
	private String subRegion = null;
	private ArrayList pclevelInfo = new ArrayList();

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
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("NPP: " + npp + " nonProficiencyPenalty: " + nonProficiencyPenalty);
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

	/**
	 * Selector
	 *
	 * @return rapid shot weapons
	 */
	public List getRapidShotWeapons()
	{
		return rapidshotWeapons;
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

	public ArrayList getArmorProfList()
	{
		return getArmorProfList(true);
	}

	public void addArmorProfs(ArrayList aList)
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
			if (aProf.startsWith("TYPE="))
			{
				armorProfList.add(0, aProf);
			}
			else
			{
				armorProfList.add(aProf);
			}
		}
	}

	/*
	 * expanded = TRUE means expand any Armor Prof feats to be all the armors associated with that type
	 * expanded = FALSE means just include the Armor Prof feat type (e.g. LIGHT) in the list
	 */
	private ArrayList getArmorProfList(boolean expanded)
	{

		if (armorProfListStable)
		{
			return armorProfList;
		}
		ArrayList autoArmorProfList = getAutoArmorProfList();
		addArmorProfs(autoArmorProfList);
		ArrayList selectedProfList = getSelectedArmorProfList();
		addArmorProfs(selectedProfList);
/*
		//
		// Add character's armor proficiency types
		//
		String temptype;
		if (!featList.isEmpty())
		{
			for (Iterator e1 = featList.iterator(); e1.hasNext();)
			{
				final String myFeatName = ((Feat) e1.next()).getName();
				if (myFeatName.startsWith("Armor Proficiency "))
				{
					final int idxbegin = myFeatName.indexOf('(');
					final int idxend = myFeatName.indexOf(')');
					temptype = myFeatName.substring((idxbegin + 1), idxend);
					addArmorProf(temptype);
					if (expanded)
					{
						for (Iterator e2 = Globals.getEquipmentOfType(Globals.getEquipmentList(), "ARMOR." + temptype, "MAGIC.MASTERWORK").iterator(); e2.hasNext();)
						{
							Equipment eq = (Equipment) e2.next();
							addArmorProf(eq.toString());
						}
					}
				}
			}
		}
*/
		armorProfListStable = true;
		return armorProfList;
	}

	public void setArmorProfListStable(boolean arg)
	{
		armorProfListStable = arg;
	}

	private ArrayList getAutoArmorProfList()
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
			if (!aString.startsWith("TYPE="))
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
				final StringTokenizer aTok = new StringTokenizer(tempQualifyList, "|", false);
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

	int getRawFPoints()
	{
		return FPoints;
	}

	public String getStrFPoints()
	{
		int fpoints = FPoints;
		for (Iterator e = featAutoList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat) e.next();
			final String addString = aFeat.getAddString();
			if (addString.startsWith("FORCEPOINT|"))
			{
				fpoints += Integer.parseInt(addString.substring(11));
			}
		}
		return Integer.toString(fpoints);
	}

	public void setFPoints(String aString)
	{
		try
		{
			setFPoints(Integer.parseInt(aString));
		}
		catch (NumberFormatException nfe)
		{
		}
	}

	void setFPoints(int fpoints)
	{
		if (!sensitiveCheck() && (fpoints > 5))
		{
			fpoints = 5;
		}
		FPoints = fpoints;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("ForcePoints:", FPoints);
		}
		setDirty(true);
	}

	public String getDPoints()
	{
		return DPoints;
	}

	public void setDPoints(String aString)
	{
		DPoints = aString;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Darkside Points:", DPoints);
		}
	}

	public HashMap getBonusMap()
	{
		return bonusMap;
	}

	public void setEquipmentList(ArrayList eqList)
	{
		equipmentList = eqList;
		setDirty(true);
	}

	public ArrayList getEquipmentMasterList()
	{
		return equipmentMasterList;
	}

	public ArrayList getEquipmentList()
	{
		return equipmentList;
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
	public ArrayList getEquipmentListInOutputOrder()
	{
		return sortEquipmentListInOutputOrder(getEquipmentList());
	}
	public ArrayList getEquipmentMasterListInOutputOrder()
	{
		return sortEquipmentListInOutputOrder(getEquipmentMasterList());
	}

	/**
	 * Sorts the provided list of euipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple
	 * items of equipment have the same outputIndex they will be ordered by
	 * name. Note hidden items (outputIndex = -1) are not included in list.
	 *
	 * @param unsortedEquipList An ArrayList of the equipment to be sorted.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private static ArrayList sortEquipmentListInOutputOrder(ArrayList unsortedEquipList)
	{
		final ArrayList sortedList = (ArrayList) unsortedEquipList.clone();
		Collections.sort(sortedList, new Comparator()
		{
			// Comparator will be specific to Skill objects
			public int compare(Object obj1, Object obj2)
			{
				int obj1Index = ((Equipment) obj1).getOutputIndex();
				int obj2Index = ((Equipment) obj2).getOutputIndex();
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
					return ((Equipment) obj1).getName().compareTo(((Equipment) obj2).getName());
				}
			}

			/**
			 * This method isn't used, only implemented to fulfill Comparator interface
			 *
			 * @param obj which it ignores
			 * @return always false
			 */
			public boolean equals(Object obj)
			{
				return false;
			}

			/**
			 * This method isn't used, only implemented to get rid of jlint warning to always
			 * implement hashCode if equals is implemented
			 *
			 * @return always 0
			 */
			public int hashCode()
			{
				return 0;
			}
		});

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

	public Equipment getEquipmentNamed(String aString)
	{
		if (equipmentMasterList.isEmpty())
		{
			return null;
		}
		Equipment match = null;
		for (Iterator e = equipmentMasterList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (aString.equals(eq.getKeyName()) || aString.equals(eq.getName()))
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

	public void equipmentListAddAll(ArrayList aList)
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
		// First we get the EquipSet that is going to be used
		// to calculate everything from
		String calcId = getCalcEquipSetId();
		EquipSet eSet = getEquipSetByIdPath(calcId);

		if (eSet == null)
		{
			Globals.errorPrint("Error: No EquipSet selected for output");
			GuiFacade.showMessageDialog(null,
				"Select an EquipSet from the Inventory->Equipping tab for output",
				Constants.s_APPNAME,
				GuiFacade.ERROR_MESSAGE);
			return;
		}

		// new equipment list
		ArrayList eqList = new ArrayList();
		// set PC's equipmentList to new one
		setEquipmentList(eqList);

		// get all the PC's EquipSet's
		ArrayList equipSetList = getEquipSet();

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(equipSetList);

		// loop through all the EquipSet's and create equipment
		// then set status to equipped and add to PC's eq list
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
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
			Float num = es.getQty();
			StringTokenizer aTok = new StringTokenizer(es.getIdPath(), ".", false);

			// if the eSet.getIdPath() is longer than 3
			// it's inside a container, don't try to equip
			if (aTok.countTokens() > 3)
			{
				eq.setLocation(Equipment.CONTAINED);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			else if (aLoc.startsWith(Constants.S_CARRIED))
			{
				eq.setLocation(Equipment.CARRIED_NEITHER);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}
			else if (aLoc.startsWith(Constants.S_NOTCARRIED))
			{
				eq.setLocation(Equipment.NOT_CARRIED);
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
				}
				else if (aLoc.startsWith(Constants.S_SECONDARY) || aLoc.equals(Constants.S_NATURAL_SECONDARY))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_SECONDARY);
				}
				else if (aLoc.equals(Constants.S_BOTH))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_BOTH);
				}
				else if (aLoc.equals(Constants.S_DOUBLE))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(2);
					eq.setLocation(Equipment.EQUIPPED_TWO_HANDS);
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
				}
				else if (aLoc.equals(Constants.S_RAPIDSHOT))
				{
					eq.setQty(num);
					eq.setNumberCarried(num);
					eq.setNumberEquipped(num.intValue());
					eq.setLocation(Equipment.EQUIPPED_RAPIDSHOT);
				}
			}
			else
			{
				eq.setLocation(Equipment.EQUIPPED_NEITHER);
				eq.setNumberCarried(num);
				eq.setQty(num);
			}

			addEquipment(eq);
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
		}

		// all done!
	}

	public void setCalcEquipSetId(String eqSetId)
	{
		calcEquipSetId = eqSetId;
	}

	public String getCalcEquipSetId()
	{
		return calcEquipSetId;
	}

	public ArrayList getEquipSet()
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
		setDirty(true);
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
		// now find and remove equipment from all EquipSet's
		for (Iterator eSet = equipSetList.iterator(); eSet.hasNext();)
		{
			EquipSet es = (EquipSet) eSet.next();
			Equipment eqI = es.getItem();
			if ((eqI != null) && eq.equals(eqI))
			{
				equipSetList.remove(eSet);
			}
		}
	}

	public ArrayList getFollowerList()
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

		Globals.errorPrint("setMaster:" + mPC.getName());

		//
		// Get total wizard + sorcer levels as they stack like a mother
		// Doh!!
		int mTotalLevel = 0;
		int addHD = 0;
		for (Iterator c = mPC.getClassList().iterator(); c.hasNext();)
		{
			PCClass mClass = (PCClass) c.next();
			if ("WIZARD".equalsIgnoreCase(mClass.getName()) || "SORCERER".equalsIgnoreCase(mClass.getName()))
			{
				++mTotalLevel;
			}
		}
		//System.out.println("setMaster:fTL:" + mTotalLevel);

		for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
		{
			CompanionMod aComp = (CompanionMod) cm.next();
			String aType = aComp.getType();
//System.out.println("setMaster:type-> "+aType+":"+aM.getType());

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

				// This CompanionMod must be for this Class
				// and for the correct level or lower
				if (compLev <= mLev || compLev <= mTotalLevel)
				{
					if (!companionModList.contains(aComp))
					{
						companionModList.add(aComp);
						addHD += aComp.getHitDie();
						//Globals.errorPrint("setMaster:type:" + aType + " level:" + aLevel + " bonus:" + aComp.getBonusListString());
					}
				}
			}
		}
		//
		// if necessary, switch the race type
		final String oldRaceType = race.getType();
		final String newRaceType = Globals.getCompanionSwitch(oldRaceType);
		Globals.errorPrint("setMaster:oldRace:" + oldRaceType + " newRace:" + newRaceType);
		PCClass newClass;

		if ((newRaceType != null) && (newRaceType.length() > 0) && isFamiliarOrMount(followerMaster))
		{
			newClass = Globals.getClassNamed(newRaceType);
			race.setTypeInfo(".CLEAR." + newRaceType);
			setDirty(true);
			// we now have to swap all the old "Race" levels
			// for the new ones
			final PCClass oldClass = getClassNamed(oldRaceType);
			int oldLevel = 0;
			if (oldClass != null)
			{
				oldLevel = oldClass.getLevel();
			}
			if ((oldLevel > 0) && (newClass != null))
			{
				Globals.errorPrint("setMaster:oldClass:" + (oldClass != null ? oldClass.getName() : "null" + "(" + oldLevel + ")"));
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
		if ("FAMILIAR".equalsIgnoreCase(followerMaster.getType()))
		{
			final ArrayList mList = mPC.getSkillList();
			final ArrayList sNameList = new ArrayList();
			// now we have to merge the two lists together and
			// take the higher rank of each skill for the Familiar
			for (Iterator a = getSkillList().iterator(); a.hasNext();)
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
//Globals.errorPrint("rankList: " + fSkill.getRankList().get(0));

						}
					}
					// build a list of all skills a master
					// posesses, but the familiar does not
					if (!hasSkill(mSkill.getName()) && !sNameList.contains(mSkill.getName()))
						sNameList.add(mSkill.getName());
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
				//Globals.errorPrint("skillName: "+skillName+"  sr: "+sr);
				if (newSkill.getChoiceList() != null)
					continue;
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
		final String name = getRace().toString();
		return (name.equals(Constants.s_NONESELECTED) ? "Nothing" : name);
	}

	private String getDisplayClassName()
	{
		final ArrayList classList = getClassList();
		return (classList.isEmpty() ? "Nobody" : ((PCClass) classList.get(classList.size() - 1)).getDisplayClassName());
	}

	public String getDisplayName()
	{
		final String custom = getTabName();

		if (!"".equals(custom))
		{
			return custom;
		}

		final StringBuffer name = new StringBuffer().append(getName());

		switch (SettingsHandler.getNameDisplayStyle())
		{
			case Constants.DISPLAY_STYLE_NAME:
				break;
			case Constants.DISPLAY_STYLE_NAME_CLASS:
				name.append(" the ").append(getDisplayClassName());
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE:
				name.append(" the ").append(getDisplayRaceName());
				break;
			case Constants.DISPLAY_STYLE_NAME_RACE_CLASS:
				name.append(" the ").append(getDisplayRaceName()).append(' ').append(getDisplayClassName());
				break;
			default:
				break; // custom broken
		}

		return name.toString();
	}

	public String getFullDisplayName()
	{
		return new StringBuffer().append(getName()).append(" the ").append(getDisplayRaceName()).append(' ').append(getDisplayClassName()).toString();
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
		return skillPoints;
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
	int getInitialFeats()
	{
		int initFeats = getRace().getBonusInitialFeats();
		final ArrayList aList = getTemplateList();

		if (!aList.isEmpty() && canReassignTemplateFeats())
		{
			for (Iterator e = aList.iterator(); e.hasNext();)
			{
				final PCTemplate template = (PCTemplate) e.next();

				if (template != null)
				{
					initFeats += template.getBonusInitialFeats();
				}
			}
		}
		return initFeats;
	}

	public int getFeats()
	{
		return feats;
	}

	public void setFeats(int argFeats)
	{
		feats = argFeats;
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

	ArrayList getSpecialAbilityList()
	{
		// aList will contain a list of SpecialAbility objects
		ArrayList aList = (ArrayList) specialAbilityList.clone();
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
		//for (Iterator i = featList.iterator(); i.hasNext();)
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

				ArrayList bList = eq.getEqModifierList(true);
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
		final ArrayList aList = getSpecialAbilityList();
		final ArrayList bList = new ArrayList();
		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final Object obj = i.next();
				final SpecialAbility sa = (SpecialAbility) obj;
				if (sa.getDesc() == null || "".equals(sa.getDesc()))
				{
					bList.add(sa.getName());
				}
				else
				{
					bList.add(sa.getName() + " (" + sa.getDesc() + ")");
				}
			}
		}
		return bList;
	}

	/*
	* same as getSpecialAbilityList except if
	* if you have the same ability twice, it only
	* lists it once with (2) at the end.
	*/
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
			StringTokenizer varTok = new StringTokenizer((String) aList.get(i), "|", false);
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

	public TreeSet getLanguagesListNames()
	{
		final TreeSet aSet = getLanguagesList();
		final TreeSet bSet = new TreeSet();
		for (Iterator i = aSet.iterator(); i.hasNext();)
		{
			bSet.add(i.next().toString());
		}
		return bSet;
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

	//public Integer getEarnedXP()
	//{
	//	return new Integer(earnedXP);
	//}

	public int getNextLevelXP()
	{
		return nextLevelXP;
	}

	public void setNextLevelXP(int argNextLevelXP)
	{
		nextLevelXP = argNextLevelXP;
		setDirty(true);
	}

	/**
	 * Returns the number of experience points needed for level
	 *
	 * @param  level  character level to calculate experience for
	 * @return        The experience points needed
	 */
	//removed for OGL/d20 compliance
/*	private static int minXPForLevel(int level)
	{
		ArrayList levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo)levelInfo.get(level - 1)).getMinXP();
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
*/

	public int getXP()
	{
		/* Add the effect of LEVELADJ when showing our
		 * external notion of XP. */
		//return earnedXP + getLAXP(); //replaced for d20/OGL compliance
		return earnedXP;
	}

	private void setEarnedXP(int argEarnedXP)
	{
		earnedXP = argEarnedXP;
		setDirty(true);
	}

	public void setXP(int xp)
	{
		/* Remove the effect of LEVELADJ when storing our
		 * internal notion of experiene. */
		//int realXP = xp - getLAXP(); //replaced for d20/OGL compliance
		int realXP = xp;
		if (realXP < 0)
		{
			Globals.errorPrint("ERROR: too little experience: " + realXP);
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
	 * @return        The maximum allowed skill ranks
	 */
	public static BigDecimal maxClassSkillForLevel(int level)
	{
		final ArrayList levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo) levelInfo.get(level - 1)).getMaxClassSkillRanks();
		}
		else
		{
			// do something sensible if no level info
			return new BigDecimal(0);
		}
	}

	/**
	 * Returns the maximum number of ranks a character can
	 * have in a cross-class skill at the specified level.
	 *
	 * @param  level  character level to get max skill ranks for
	 * @return        The maximum allowed skill ranks
	 */
	public static BigDecimal maxCrossClassSkillForLevel(int level)
	{
		final ArrayList levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo) levelInfo.get(level - 1)).getMaxCrossClassSkillRanks();
		}
		else
		{
			// do something sensible if no level info
			return new BigDecimal(0);
		}
	}

	public ArrayList getMiscList()
	{
		return miscList;
	}

	public ArrayList getSpellBooks()
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

	public int getUsedFeatCount()
	{
		int iCount = 0;
		if (!featList.isEmpty())
		{
			for (Iterator e = featList.iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat) e.next();
				//
				// Don't increment the count for hidden feats so the number
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
					++iCount;
				}
			}
		}
		return iCount;
	}

	/**
	 * This method gets the list of CharacterDomains for the character.
	 * Before returning the list, this method attempts to make sure that
	 * the number of domains in the list is correct for the character based
	 * on its classes and bonuses.
	 *
	 * Modified by dhibbs 11/5/2002
	 */
	public ArrayList getCharacterDomainList()
	{
		// Get the total number of domains there should be
		final int domainAvail = (int) getTotalBonusTo("DOMAIN", "NUMBER", false);

		// Get the total number of domains there currently are
		final int domainCount = characterDomainList.size();

		// If the domain list is too short, try to fix it
		if (domainCount < domainAvail)
		{
			boolean pccDomainFound = false;
			CharacterDomain aCD = null;

			// If the domain list is not empty, find first one with source set
			if (!characterDomainList.isEmpty())
			{
				for (Iterator e = characterDomainList.iterator(); e.hasNext();)
				{
					aCD = (CharacterDomain) e.next();
					pccDomainFound = aCD.isFromPCClass(); // Don't copy feats etc!
					if (pccDomainFound)
					{
						break;
					}
				}
			}

			// Make sure we found something that's safe to copy
			if (pccDomainFound)
			{
				// there should be num characterDomains
				// default them to be from the same source to the first defined domain
				while (characterDomainList.size() < domainAvail)
				{
					// Copy the previous domain origin--don't use clone because that
					// will copy the actual domain as well!
					final CharacterDomain dCopy = new CharacterDomain();
					dCopy.setFromPCClass(true);
					dCopy.setObjectName(aCD.getObjectName());
					dCopy.setLevel(aCD.getLevel());
					characterDomainList.add(dCopy);
				}
			}
		} // End of if (domain list is too short)

		// Else If the domain list is too long, try to fix it
		else if (domainCount > domainAvail)
		{
			// Truncate the domain list to the right size if it is too long
			// (and contains empty domains)
			while (domainAvail < characterDomainList.size())
			{
				final int i = getFirstEmptyCharacterDomain();
				if (i >= 0)
				{
					characterDomainList.remove(i);
				}
				else
				{
					Globals.errorPrint("WARNING:Total domains should be " + domainAvail + "!!!");
					break;
				}
			}
		} // End of if (domain list is toolong)

		return characterDomainList;
	}

	public ArrayList getSkillList()
	{
		return getAllSkillList(false);
	}

	// if checkBonus is true, then search for all skills with a SKILLRANK bonus
	// to include in list as well
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
				if (getTotalBonusTo("SKILLRANK", aSkill.getName(), true) != 0.0)
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
					return ((Skill) obj1).getName().compareTo(((Skill) obj2).getName());
				}
			}

			/**
			 *  this method isn't used, it is only here to satsify the Comparator interface.
			 */
			public boolean equals(Object obj)
			{
				return false;
			}

			/**
			 * This method isn't used, only here to get rid of jlint warning to always
			 * implement hashCode if equals is implemented
			 *
			 * @return always 0
			 */
			public int hashCode()
			{
				return 0;
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
			statList.getStats().add(((PCStat) Globals.getStatList().get(i)).clone());
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
		if (Globals.isSSd20Mode())
		{
			addSpellBook("Quick Reference Spells");
			addSpellBook("Intimate Knowledge Spells");
		}
		else
		{
		}
		populateSkills(SettingsHandler.getIncludeSkills());
		for (Iterator i = Globals.getBonusStackList().iterator(); i.hasNext();)
		{
			bonusMap.put(i.next().toString(), "x");
		}
	}

	/**
	 * freeing up resources
	 * XXX Doesn't actually do anything. What should it do?
	 **/
	public static void dispose()
	{
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
		return Globals.getSizeAdjustmentAtIndex(sizeInt());
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
			// the size of the character
			// with something like BONUS:SIZEMOD|NUMBER|+1
			iSize += (int) getTotalBonusTo("SIZEMOD", "NUMBER", true);

			// Now see if there is a HD advancement in size
			// (Such as for Dragons)

			// This is tricky -- it is unclear if this can
			// be optimized to eliminate all the calls to
			// test.  XXX
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
			if (iSize >= Globals.getSizeAdjustmentList().size())
			{
				iSize = Globals.getSizeAdjustmentList().size() - 1;
			}
		}
		return iSize;
	}

	public PCClass getClassNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			// IgnoreCase needed for class checks in getVariableValue ...  ---arcady 10/6/2001
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
			// IgnoreCase needed for class checks in getVariableValue ...  ---arcady 10/6/2001
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
			if (aClass.getKeyName().equals(aString))
			{
				return aClass;
			}
		}
		return null;
	}

	public PCTemplate getTemplateNamed(String aName)
	{
		for (Iterator templateIter = templateList.iterator(); templateIter.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) templateIter.next();
			if (aTemplate.getName().equals(aName))
			{
				return aTemplate;
			}
		}
		return null;
	}

	public PObject getSpellClassAtIndex(int ix)
	{
		PObject aObject = null;
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			aObject = (PObject) classIter.next();
			if (!aObject.getCharacterSpell(null, "", -1).isEmpty())
			{
				--ix;
			}
			else
			{
				aObject = null;
			}
			if (ix == -1)
			{
				break;
			}
		}
		if (ix == -1 && aObject != null)
		{
			return aObject;
		}
		if (ix == 0 && !race.getCharacterSpell(null, "", -1).isEmpty())
		{
			return race;
		}
		return null;
	}

	private int getSpellClassCount()
	{
		int x = 0;
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PObject aObject = (PObject) classIter.next();
			if (!aObject.getCharacterSpell(null, "", -1).isEmpty())
			{
				++x;
			}
		}
		if (!race.getCharacterSpell(null, "", -1).isEmpty())
		{
			return ++x;
		}
		return x;
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
				final StringTokenizer aTok = new StringTokenizer((String) e.next(), "|", false);
				aTok.nextToken(); //src
				aTok.nextToken(); //subSrc
				if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
				{
					return true;
				}
			}
		}

		//if (!featList.isEmpty())
		if (!aggregateFeatList().isEmpty())
		{
			//for (Iterator e = featList.iterator(); e.hasNext();)
			for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
			{
				final Feat obj = (Feat) e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					final String featVariable = obj.getVariable(i);
					final StringTokenizer aTok = new StringTokenizer(featVariable, "|", false);
					if (aTok.countTokens() > 2)
					{
						aTok.nextToken(); //level
						if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
						{
							return true;
						}
					}
				}
			}
		}

		if (!getSkillList().isEmpty())
		{
			for (Iterator e = getSkillList().iterator(); e.hasNext();)
			{
				final Skill obj = (Skill) e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					String skillVariable = obj.getVariable(i);
					final StringTokenizer aTok = new StringTokenizer(skillVariable, "|", false);
					if (aTok.countTokens() > 2)
					{
						aTok.nextToken(); //level
						if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
						{
							return true;
						}
					}
				}
			}
		}

		if (!equipmentList.isEmpty())
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment obj = (Equipment) e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					final String equipmentVariable = obj.getVariable(i);
					final StringTokenizer aTok = new StringTokenizer(equipmentVariable, "|", false);
					if (aTok.countTokens() > 2)
					{
						aTok.nextToken(); //level
						if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
						{
							return true;
						}
					}
				}
				ArrayList aList = obj.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						for (int i = 0, x = eqMod.getVariableCount(); i < x; ++i)
						{
							final String eqModVariable = obj.getVariable(i);
							final StringTokenizer aTok = new StringTokenizer(eqModVariable, "|", false);
							if (aTok.countTokens() > 2)
							{
								aTok.nextToken(); //level
								if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
								{
									return true;
								}
							}
						}
					}
				}
				aList = obj.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier) e2.next();
						for (int i = 0, x = eqMod.getVariableCount(); i < x; ++i)
						{
							final String eqModVariable = eqMod.getVariable(i);
							final StringTokenizer aTok = new StringTokenizer(eqModVariable, "|", false);
							if (aTok.countTokens() > 2)
							{
								aTok.nextToken(); //level
								if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
								{
									return true;
								}
							}
						}
					}
				}
			}
		}

		if (!templateList.isEmpty())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				final PCTemplate obj = (PCTemplate) e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					final String templateVariable = obj.getVariable(i);
					final StringTokenizer aTok = new StringTokenizer(templateVariable, "|", false);
					if (aTok.countTokens() > 2)
					{
						aTok.nextToken(); //level
						if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
						{
							return true;
						}
					}
				}
			}
		}

		if (!companionModList.isEmpty())
		{
			for (Iterator e = companionModList.iterator(); e.hasNext();)
			{
				final CompanionMod obj = (CompanionMod) e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					final String companionVariable = obj.getVariable(i);
					final StringTokenizer aTok = new StringTokenizer(companionVariable, "|", false);
					if (aTok.countTokens() > 2)
					{
						aTok.nextToken(); //level
						if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
						{
							return true;
						}
					}
				}
			}
		}

		if (deity != null)
		{
			for (int i = 0, x = deity.getVariableCount(); i < x; ++i)
			{
				final String deityVariable = deity.getVariable(i);
				final StringTokenizer aTok = new StringTokenizer(deityVariable, "|", false);
				if (aTok.countTokens() > 2)
				{
					aTok.nextToken(); //level
					if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
					{
						return true;
					}
				}
			}
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				final CharacterDomain cd = (CharacterDomain) e.next();
				if (cd.getDomain() != null)
				{
					for (int i = 0, x = cd.getDomain().getVariableCount(); i < x; ++i)
					{
						final String domainVariable = cd.getDomain().getVariable(i);
						final StringTokenizer aTok = new StringTokenizer(domainVariable, "|", false);
						if (aTok.countTokens() > 2)
						{
							aTok.nextToken(); //level
							if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
							{
								return true;
							}
						}
					}
				}
			}
		}

		if (race != null)
		{
			for (int i = 0, x = race.getVariableCount(); i < x; ++i)
			{
				final String raceVariable = race.getVariable(i);
				final StringTokenizer aTok = new StringTokenizer(raceVariable, "|", false);
				if (aTok.countTokens() > 2)
				{
					aTok.nextToken(); //level
					if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
					{
						return true;
					}
				}
			}
		}

		if (!weaponProfList.isEmpty())
		{
			for (Iterator e = weaponProfList.iterator(); e.hasNext();)
			{
				final String wpName = (String) e.next();
				final WeaponProf obj = Globals.getWeaponProfNamed(wpName);
				if (obj == null)
				{
					Globals.debugPrint("No weapon prof: ", wpName);
					continue;
				}
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					final String weaponProfVariable = obj.getVariable(i);
					final StringTokenizer aTok = new StringTokenizer(weaponProfVariable, "|", false);
					if (aTok.countTokens() > 2)
					{
						aTok.nextToken(); //level
						if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
						{
							return true;
						}
					}
				}
			}
		}
		for (Iterator e = statList.getStats().iterator(); e.hasNext();)
		{
			final PCStat aStat = (PCStat) e.next();
			for (int i = 0, x = aStat.getVariableCount(); i < x; ++i)
			{
				final String aVariable = aStat.getVariable(i);
				final StringTokenizer aTok = new StringTokenizer(aVariable, "|", false);
				if (aTok.countTokens() > 2)
				{
					aTok.nextToken(); //level
					if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
					{
						return true;
					}
				}
			}
		}
		for (Iterator e = Globals.getAlignmentList().iterator(); e.hasNext();)
		{
			final PCAlignment po = (PCAlignment) e.next();
			for (int i = 0, x = po.getVariableCount(); i < x; ++i)
			{
				final String aVariable = po.getVariable(i);
				final StringTokenizer aTok = new StringTokenizer(aVariable, "|", false);
				if (aTok.countTokens() > 2)
				{
					aTok.nextToken(); //level
					if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
					{
						return true;
					}
				}
			}
		}
		return false;
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
				Globals.errorPrint("Avoiding infinite loop in getVariable: repeated lookup of \"" + lastVariable + "\"");
				return new Float(value);
			}
		}

		if (!variableList.isEmpty())
		{
			for (Iterator e = variableList.iterator(); e.hasNext();)
			{
				final String vString = (String) e.next();
				final StringTokenizer aTok = new StringTokenizer(vString, "|", false);
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
							value = value + getVariableValue(sString, src).doubleValue();
						}
						loopValue = 0;
						loopVariable = "";
					}
				}
			}
		}

		//
		// Now check the feats to see if they modify the variable
		//
		//if (!featList.isEmpty())
		if (!aggregateFeatList().isEmpty())
		{
			//for (Iterator oi = featList.iterator(); oi.hasNext();)
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
				ArrayList aList = obj.getEqModifierList(true);
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
				aList = obj.getEqModifierList(false);
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

		for (Iterator e = Globals.getAlignmentList().iterator(); e.hasNext();)
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
			final double i = getTotalBonusTo("VAR", variableString, true);
			value += i;
		}
		return new Float(value);
	}

	private String checkForVariableInList(PObject obj, String variableString, boolean isMax, String matchSrc, String matchSubSrc, boolean found, double value)
	{
		boolean flag = false;
		for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
		{
			final String vString = obj.getVariable(i);
			final StringTokenizer aTok = new StringTokenizer(vString, "|", false);
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
						value = value + getVariableValue(sString, src).doubleValue();
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

	/*
	 */
	public static ArrayList removeEqType(ArrayList aList, String aString)
	{
		final ArrayList aArrayList = new ArrayList();
		Equipment eq;
		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();
			if (!eq.typeStringContains(aString))
			{
				aArrayList.add(eq);
			}
		}
		return aArrayList;
	}

	/*
	 */
	public static ArrayList removeNotEqType(ArrayList aList, String aString)
	{
		final ArrayList aArrayList = new ArrayList();
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

	/*
	 */
	public ArrayList addEqType(ArrayList aList, String aString)
	{
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.typeStringContains(aString))
			{
				aList.add(eq);
			}
		}

		return aList;
	}

	/**
	 * Selector
	 *
	 * @return ???
	 */
	public Integer[] getMovements()
	{
		return movements;
	}

	Integer getMovement(int moveIdx)
	{
		if ((movements != null) && (moveIdx < movements.length))
		{
			return movements[moveIdx];
		}
		else
		{
			return new Integer(0);
		}
	}

	/**
	 * @param moveType - string containing the name of the movement
	 * you want to look up. Eg: Walk, Fly. Case sensitive.
	 *
	 * @return rate of desired move
	 * 0 if the character does not have that move.
	 **/
	private Integer getMovementOfType(String moveType)
	{
		if (movements != null)
		{
			for (int i = 0, x = movements.length; i < x; ++i)
			{
				if (moveType.equals(movementTypes[i]))
				{
					return movements[i];
				}
			}
		}
		return new Integer(0);
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

	public int basemovement(int moveIdx, int iLoad)
	{
		// get racial base movement
		int move = getMovement(moveIdx).intValue();

		if (!Globals.isStarWarsMode() && iLoad >= 0)
		{
			move = Globals.calcEncumberedMove(iLoad, move);
		}
		iForceLoad = iLoad;
		move += (int) getTotalBonusTo("MOVE", "TYPE=" + getMovementType(moveIdx).toUpperCase(), true);
		iForceLoad = -1;
		// always get following bonus
		move += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false);
		switch (iLoad)
		{
			// NOTE: no breaks on purpose!
			// These are cumulative and cascade together!!!!!
			case Constants.LIGHT_LOAD:
				move += (int) getTotalBonusTo("MOVE", "LIGHT", false);
				//No break
			case Constants.MEDIUM_LOAD:
				move += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUM", false);
				//No break
			case Constants.HEAVY_LOAD:
				move += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false);
				break;
			default:
				break;
		}
		return move;
	}

	public int movement(int moveIdx)
	{
		int bonus = 0;

		// get racial base movement
		int move = getMovement(moveIdx).intValue();
		// get a list of all equipped Armor
		final ArrayList aArrayList = getEquipmentOfType("Armor", 1);

		int pcLoad = Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight());

		// If no load or armor, just get class movement rates
		if (!(aArrayList.size() == 0 && pcLoad == 0))
		{
			// pcLoad will equal the greater of
			// encumberance load type or armor type
			for (Iterator a = aArrayList.iterator(); a.hasNext();)
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

				if (Globals.isStarWarsMode())
				{
					final String armorMoveString = armor.moveString();
					final int pos = armorMoveString.lastIndexOf(',');
					final boolean isMedium = "M".equals(getRace().getSize());
					if (isMedium || (pos < 0))
					{
						if (pos < 0)
						{
							move = Math.min(move, Integer.parseInt(armorMoveString));
						}
						else
						{
							move = Math.min(move, Integer.parseInt(armorMoveString.substring(0, pos)));
						}
					}
					else
					{
						move = Math.min(move, Integer.parseInt(armorMoveString.substring(pos + 1)));
					}
				}
			}
		}
		if (!Globals.isStarWarsMode())
		{
			move = Globals.calcEncumberedMove(pcLoad, move);
		}

		move += (int) getTotalBonusTo("MOVE", "TYPE=" + getMovementType(moveIdx).toUpperCase(), true);
		// always get following bonus
		bonus += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false);
		switch (pcLoad)
		{
			// NOTE: no breaks on purpose!
			// These are cumulative and cascade together!!!!!
			case Constants.LIGHT_LOAD:
				bonus += (int) getTotalBonusTo("MOVE", "LIGHT", false);
				//No break

			case Constants.MEDIUM_LOAD:
				bonus += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUM", false);
				//No break

			case Constants.HEAVY_LOAD:
				bonus += (int) getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false);
				break;

			case Constants.OVER_LOAD:
				break;

			default:
				Globals.errorPrint("In PlayerCharacter.movement the load constant " + pcLoad + " is not handled.");
				break;
		}
		move += bonus;
		return move;
	}

	public int initiativeMod()
	{
		final int initmod = (int) getTotalBonusTo("COMBAT", "Initiative", true) + getVariableValue("INITCOMP", "").intValue();
		return initmod;
	}

	private int getNumAttacks()
	{
		return Math.min(Math.max(baseAttackBonus() / 5, 4), 1);
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
		if ((nPC != null) && "FAMILIAR".equalsIgnoreCase(followerMaster.getType()))
		{
			masterBAB = nPC.baseAttackBonus();
			masterTotal = nPC.baseAttackBonus() + bonus;
		}
		//

		int BAB = baseAttackBonus();
		int bonusBAB = bonus + baseAttackBonus();

		final ArrayList ab = new ArrayList(10);
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

		final int combat = (int) getTotalBonusTo("COMBAT", "TOHIT", true);

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
		total = ((Integer) ab.get(attacks)).intValue();
		if (total == 0)
		{
			attacks = 5;
		}

		// FAMILIAR: check to see if the masters BAB is better
		mod = Math.max(mod, masterTotal);
		subTotal = Math.max(subTotal, masterBAB);
		raceBAB = Math.max(raceBAB, masterBAB);

		if (attacks != 5)
		{
			if (total / attacks < subTotal / 5)
			{
				attacks = 5;
				total = subTotal;
			}
			else
			{
				mod -= raceBAB;
				subTotal -= raceBAB;
			}
		}
		while ((attackString.length() == 0) || (total > 0) || (subTotal > 0))
		{
			if (attackString.length() > 0)
			{
				attackString.append('/');
			}

			attackString.append(Delta.toString(mod + combat));
			mod -= attacks;
			total -= attacks;
			subTotal -= attacks;
		}
		return attackString.toString();
	}

	/**
	 * compares old string in form of sides|damage e.g. 2|1d2
	 * to new string in form of damage
	 * return string in form of sides|damage for easier comparison later
	 **/
	private static String getBestUDamString(String oldString, String newString)
	{
		if (newString == null || newString.length() < 2)
		{
			return oldString;
		}
		StringTokenizer aTok = new StringTokenizer(oldString, "|", false);
		int sides = Integer.parseInt(aTok.nextToken());
		String retString = oldString;
		aTok = new StringTokenizer(newString, " dD+-(x)", false);
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

				ArrayList aList = eq.getEqModifierList(true);
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
		// Anyone every heard of constants!?  --bko XXX

		// 0 = LG, 3 = NG, 6 = CG
		// 1 = LN, 4 = TN, 7 = CN
		// 2 = LE, 5 = NE, 8 = CE
		if (bForce || this.race.canBeAlignment(Integer.toString(index)))
		{
			alignment = index;
		}
		else
		{
			if ((bLoading) && (index != Globals.getIndexOfAlignment(Constants.s_NONE)))
			{
				GuiFacade.showMessageDialog(null, "Invalid alignment. Setting to <none selected>", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				alignment = Globals.getIndexOfAlignment(Constants.s_NONE);
			}
			//TODO raise an exception, once I define one. Maybe
			//ArrayIndexOutOfBounds?
		}
	}

	private boolean setMyMoveRates(String moveType, int moveRatei, int moveFlag)
	{

		if ("ALL".equals(moveType))
		{
			if (moveFlag == 0)
			{ // set all types of movement to moveRate
				final Integer moveRate = new Integer(moveRatei);
				for (int i = 0, x = movements.length; i < x; ++i)
				{
					movements[i] = moveRate;
				}
			}
			else
			{ // add moveRate to all types of movement.
				for (int i = 0, x = movements.length; i < x; ++i)
				{
					Integer moveRate = new Integer(moveRatei + movements[i].intValue());
					movements[i] = moveRate;
				}
			}
			return true;
		}
		else
		{
			if (moveFlag == 0)
			{ // set movement to moveRate
				final Integer moveRate = new Integer(moveRatei);
				for (int i = 0, x = movements.length; i < x; ++i)
				{
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						return true;
					}
				}

				//
				// need to add new movement and type
				//
				final String[] movementTypesTemp = movementTypes;
				final Integer[] movementsTemp = movements;

				movements = new Integer[movementsTemp.length + 1];
				movementTypes = new String[movementTypesTemp.length + 1];

				//see comments below, we may have a bug here
				System.arraycopy(movementsTemp, 0, movements, 0, movementsTemp.length);
				System.arraycopy(movementTypesTemp, 0, movementTypes, 0, movementTypesTemp.length);

				movements[movementsTemp.length] = moveRate;
				movementTypes[movementTypesTemp.length] = moveType;

				return true;
			}
			else if (moveFlag == 1)
			{ // add moveRate to movement.
				for (int i = 0, x = movements.length; i < x; ++i)
				{
					Integer moveRate = new Integer(moveRatei + movements[i].intValue());
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						return true;
					}
				}
			}
			else
			{
				// set all to base rate, then add local rates.
				final Integer moveRate = new Integer(moveRatei + movements[0].intValue());
				// for existing types of movement:
				for (int i = 0, x = movements.length; i < x; ++i)
				{
					if (moveType.equals(movementTypes[i]))
					{
						movements[i] = moveRate;
						return true;
					}
				}
				// if it's a new type of movement:
				final String[] movementTypesTemp = movementTypes;
				final Integer[] movementsTemp = movements;


				//why are we adding 1 ??
				movements = new Integer[movementsTemp.length + 1];
				movementTypes = new String[movementTypesTemp.length + 1];

				System.arraycopy(movementsTemp, 0, movements, 0, movementsTemp.length);
				System.arraycopy(movementTypesTemp, 0, movementTypes, 0, movementTypesTemp.length);

				//this overwrites a copied value, we were adding 1 above, should this be length + 1 ????
				movements[movementsTemp.length] = moveRate;
				movementTypes[movementTypesTemp.length] = moveType;

				return true;

			}
		}
		return false;
	}

	//
	// Apply all template movement modifiers to the racial base
	//
	private void adjustMoveRates()
	{
		movements = null;
		movementTypes = null;
		if (getRace() == null)
		{
			return;
		}

		movements = getRace().getMovements();

		if (movements == null)
		{
			return;
		}

		movements = (Integer[]) movements.clone();
		movementTypes = (String[]) getRace().getMovementTypes().clone();

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate) e.next();
			final Integer[] templateMovements = aTemplate.getMovements();
			String[] templateMovementTypes = aTemplate.getMovementTypes();
			final Integer[] movementsMult = aTemplate.getMovementsMult();
			String[] movementMultOp = aTemplate.getMovementMultOp();

			if (templateMovements != null)
			{
				for (int i = 0, x = templateMovements.length; i < x; ++i)
				{

					if (movementsMult[i].intValue() != 0)
					{
						if (templateMovements.length > 1)
						{
							if (movementMultOp[i].length() > 0 && movementMultOp[i].charAt(0) == '*')
							{
								setMyMoveRates(templateMovementTypes[i], getMovementOfType(templateMovementTypes[0]).intValue() * movementsMult[i].intValue(), 0);
							}
							else if (movementMultOp[i].length() > 0 && movementMultOp[i].charAt(0) == '/')
							{
								setMyMoveRates(templateMovementTypes[i], getMovementOfType(templateMovementTypes[0]).intValue() / movementsMult[i].intValue(), 0);
							}
						}
						else
						{
							if (movementMultOp[i].length() > 0 && movementMultOp[i].charAt(0) == '*')
							{
								setMyMoveRates(templateMovementTypes[i], getMovement(0).intValue() * movementsMult[i].intValue(), 0);
							}
							else if (movementMultOp[i].length() > 0 && movementMultOp[i].charAt(0) == '/')
							{
								setMyMoveRates(templateMovementTypes[i], getMovement(0).intValue() / movementsMult[i].intValue(), 0);
							}
						}

					}
					else
						setMyMoveRates(templateMovementTypes[i], templateMovements[i].intValue(), aTemplate.getMoveRatesFlag());
				}
			}
		}
	}

	// take a map of key (vision-type string) and values (
	private HashMap addStringToVisionMap(HashMap visMap, HashMap aMap)
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
				Integer.parseInt(bObj.toString());
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
		HashMap visMap = new HashMap();
		visMap = addStringToVisionMap(visMap, race.getVision());
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

				ArrayList aList = eq.getEqModifierList(true);
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

		final StringBuffer vision = new StringBuffer();
		for (Iterator i = visMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			Object bObj = visMap.get(aKey);
			if (bObj == null)
			{
				Globals.errorPrint("huh?");
				continue;
			}
			int val = Integer.parseInt(bObj.toString()) + (int) getTotalBonusTo("VISION", aKey, true);
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

		/**
		 * bug fix:
		 * do not add (additional) gold on character load
		 *
		 * this is just a quick fix;
		 * actually I do not know how to do this properly
		 * oh well, seems to work --- for now
		 *
		 * author: Thomas Behr 06-01-02
		 */
//		if (addGold)
//		{
//			gold = inTmpl.getGold(gold);
//			removed for OGL compliance reasons (can't randomly roll gold anymore)
//			merton_monk@yahoo.com (Bryan McRoberts) 8/27/02
//		}

		templateAutoLanguages.addAll(inTmpl.getAutoLanguages());
		templateLanguages.addAll(inTmpl.getLanguageBonus());
		getAutoLanguages();

		inTmpl.chooseLanguageAutos(importing);
		if (canReassignTemplateFeats())
		{
			final ArrayList templateFeats = inTmpl.feats(getTotalLevels(), totalHitDice());
			for (int i = 0, x = templateFeats.size(); i < x; ++i)
			{
				modFeatsFromList((String) templateFeats.get(i), true, false);
			}
		}
		else
		{
			setAutomaticFeatsStable(false);
		}

		final ArrayList templates = inTmpl.getTemplates(importing);
		for (int i = 0, x = templates.size(); i < x; ++i)
		{
			addTemplate(Globals.getTemplateNamed((String) templates.get(i)));
		}
		setQualifyListStable(false);
		adjustMoveRates();
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

//		gold = inTmpl.cutGold(gold);
//		removed for OGL compliance reasons (can't randomly roll gold anymore)
//		merton_monk@yahoo.com (Bryan McRoberts) 8/27/02

		if (inTmpl.weaponProfAutos != null)
		{
			weaponProfList.removeAll(inTmpl.getWeaponProfAutos());
		}
		languages.removeAll(inTmpl.getAutoLanguages()); // remove template languages.
		templateAutoLanguages.removeAll(inTmpl.getAutoLanguages()); // remove them from the local listing. Don't clear though in case of multiple templates.

		templateLanguages.removeAll(inTmpl.getLanguageBonus());

		// It is hard to tell if removeTemplate() modifies
		// inTmpl.templatesAdded(), so not safe to optimize
		// the call to .size().  XXX
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
		adjustMoveRates();
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
				Globals.debugPrint(" PlayerCharacter::incrementClassLevel => Not qualified for class " + globalClass.getName());
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
					Globals.errorPrint("PlayerCharacter::incrementClassLevel => " + "Clone of class " + globalClass.getName() + " failed!");
					return;
				}

				// Add the class to the character classes as level 0
				classList.add(pcClassClone);

				// do the following only if adding a level of a class for the first time
				if (mod > 0)
				{
					final Set aSet = pcClassClone.getAutoLanguages();
					languages.addAll(aSet);
				}
			}
			else
			{
				// mod is < 0 and character does not have class.  Return.
				Globals.debugPrint(" PlayerCharacter::incrementClassLevel => Character does not have class " + globalClass.getName());
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
				ArrayList templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice());

				for (int j = 0, y = templateFeats.size(); j < y; ++j)
				{
					modFeatsFromList((String) templateFeats.get(j), true, false);
				}
			}
		}
		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
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

		//
		// Will take destination class over maximum?
		//
		if (toClass.getLevel() + iCount > toClass.getMaxLevel())
		{
			iCount = toClass.getMaxLevel() - toClass.getLevel();
		}

		//
		// Enough levels to move?
		//
		if ((fromClass.getLevel() <= iCount) || (iCount < 1))
		{
			return;
		}

		final int iOldLevel = toClass.getLevel();
		toClass.setLevel(iOldLevel + iCount);
		for (int i = 0; i < iCount; ++i)
		{
			toClass.setHitPoint(iOldLevel + i, fromClass.getHitPointList(fromClass.getLevel() - i - 1));
			fromClass.setHitPoint(fromClass.getLevel() - i - 1, new Integer(0));
		}

		rebuildLists(toClass, fromClass, iCount);

		fromClass.setLevel(fromClass.getLevel() - iCount);
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
				bClass.setHitPointList(aClass.getHitPointList());
				final int idx = classList.indexOf(aClass);
				classList.set(idx, bClass);
			}
			else
			{
				rebuildLists(bClass, aClass, aClass.getLevel());
				bClass.setLevel(bClass.getLevel() + aClass.getLevel());
				for (int i = 0; i < aClass.getLevel(); ++i)
				{
					bClass.setHitPoint(bClass.getLevel() + i + 1, aClass.getHitPointList(i + 1));
				}
				classList.remove(aClass);
			}

			//
			// change all the levelling info to the ex-class as well
			//
			for (int idx = pclevelInfo.size() - 1; idx >= 0; --idx)
			{
				final PCLevelInfo li = (PCLevelInfo) pclevelInfo.get(idx);
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
		}
		return aSkill;
	}

	/**
	 * Calculate the maximum number of ranks the character is allowed to have
	 * in the specified skill.
	 *
	 * @param skillName The name of the skill being checked.
	 * @param aClass The name of the current class in which points are being spent
	 *               - only used to check cross-class skill cost.
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
		maxRanks = maxRanks.add(new BigDecimal((int) getTotalBonusTo("SKILLMAXRANK", skillName, true)));
		return new Float(maxRanks.floatValue());
	}

	/**
	 * Returns the list of names of available feats of given type.
	 * That is, all feats from the global list, which match the
	 * given featType, the character qualifies for, and the
	 * character does not already have.
	 *
	 * @param featType  String category of feat to list.
	 * @return ArrayList of Feats.
	 */
	ArrayList getAvailableFeatNames(String featType)
	{
		final ArrayList aFeatList = new ArrayList();
		final ArrayList globalFeatList = Globals.getFeatList();
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

		total += (int) getTotalBonusTo("HP", "CURRENTMAX", true);
		//
		// now we see if this PC is a Familiar
		final PlayerCharacter nPC = getMasterPC();
		if (nPC == null)
		{
			return total;
		}
		if (!"FAMILIAR".equalsIgnoreCase(followerMaster.getType()))
		{
			return total;
		}
		else
		{
			//
			// In order for the BONUS's to work, PC we want to get the hit points for
			// must be the "current" one.
			//
			final PlayerCharacter curPC = Globals.getCurrentPC();
			Globals.setCurrentPC(nPC);
			final int masterHP = nPC.hitPoints();
			Globals.setCurrentPC(curPC);

			return masterHP / 2;
		}
	}

	public boolean isNonability(int i)
	{
		if (race.isNonability(i))
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
				final StringTokenizer aTok = new StringTokenizer(oldRace.getFeatList(), "|", false);
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aString.endsWith(")") && Globals.getFeatNamed(aString) == null)
					{
						final String featName = aString.substring(0, aString.indexOf('(') - 1);

						final Feat aFeat = Globals.getFeatNamed(featName);
						if (aFeat != null)
						{
							//modFeat(aString, false, aFeat.getName().endsWith("Proficiency"));
							modFeat(aString, true, false);
							setFeats(feats - 1);
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
								//modFeat(featName, false, featName.endsWith("Proficiency"));
								modFeat(aString, true, false);
								setFeats(feats - 1);
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "Removing unknown feat: " + aString, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
						}
					}
				}
			}
			languages.removeAll(oldRace.getAutoLanguages());
			if (oldRace.getWeaponProfAutos() != null)
			{
				weaponProfList.removeAll(oldRace.getWeaponProfAutos());
			}
			if (racialFavoredClass.length() != 0)
			{
				favoredClasses.remove(racialFavoredClass);
			}

			removeNaturalWeapons();
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
			if (!importing)
			{
				BioSet.randomize("AGE.HT.WT");
			}

			// Get existing classes
			final ArrayList existingClasses = new ArrayList(classList);
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
			ArrayList existingLevelInfo = new ArrayList(pclevelInfo);
			pclevelInfo.clear();

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
			pclevelInfo.addAll(existingLevelInfo);

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

			addNaturalWeapons();
			if (canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(), "|", false);
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
							setFeats(feats + 1);
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
								setFeats(feats + 1);
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

			final ArrayList templates = race.getTemplates(importing);
			for (int x = 0; x < templates.size(); ++x)
			{
				addTemplate(Globals.getTemplateNamed((String) templates.get(x)));
			}

			race.chooseLanguageAutos(importing);
		}

		adjustMoveRates();

		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
		if (!importing)
		{
			getSpellList();
			race.globalChecks();
		}
		setDirty(true);
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

				ArrayList aList = eq.getEqModifierList(true);
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
			// spellname|times|book|PRExxx|PRExxx|etc
			final String spellLine = ri.next().toString();
			final StringTokenizer aTok = new StringTokenizer(spellLine, "|", false);
			final String spellName = aTok.nextToken();
			final Spell aSpell = Globals.getSpellNamed(spellName);
			if (aSpell == null)
			{
				return;
			}

			final String castCount = aTok.nextToken();
			int spellLevel = -1;
			int times = 1;
			int slotLevel = 0;
			owner = race;
			if (castCount.startsWith("LEVEL="))
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
				times = Integer.parseInt(castCount);
			}

			final String book = aTok.nextToken();
			if (aTok.hasMoreTokens())
			{
				ArrayList qList = new ArrayList();
				while (aTok.hasMoreTokens())
				{
					qList.add(aTok.nextToken());
				}
				if (!race.passesPreReqTestsForList(qList))
				{
					continue;
				}
			}

			ArrayList sList = owner.getCharacterSpell(aSpell, book, spellLevel);
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

	public int getTotalLevels()
	{
		int totalLevels = 0;

		totalLevels += totalPCLevels();
		totalLevels += totalNPCLevels();

		// Monster hit dice count towards total levels -- was totalMonsterLevels()
		//  sage_sam changed 03 Dec 2002 for Bug #646816
		totalLevels += totalHitDice();
		return totalLevels;
	}

/*	public int minXPForECL()
	{
		return minXPForLevel(getECL());
	}

	public int minXPForNextECL()
	{
		return minXPForLevel(1 + getECL());
	}
*/

	public int totalPCLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			if (aClass.isPC())
			{
				totalLevels += aClass.getLevel();
			}
		}
		return totalLevels;
	}

	public int totalNPCLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			if (aClass.isNPC())
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
		final int NPCLevels = totalNPCLevels();
		if (NPCLevels > 0)
		{
			if (CR == 0)
			{
				CR += NPCLevels - 1;
			}
			else
			{
				if (NPCLevels == 1)
				{
					++CR;
				}
				else
				{
					CR += NPCLevels - 1;
				}
			}
		}

		for (int x = 0; x < templateList.size(); ++x)
		{
			CR += ((PCTemplate) templateList.get(x)).getCR(getTotalLevels(), totalHitDice());
		}

		CR += totalPCLevels();

		final int raceCR = race.getCR();
		if ((raceCR > 0) || (CR == 0))
		{
			CR += raceCR;
		}
		return CR;
	}

	public int getSR()
	{
		return calcSR();
	}

	public int calcSR()
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
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				SR = Math.max(SR, eq.getSR());

				ArrayList aList = eq.getEqModifierList(true);
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
		final int atl = getTotalLevels();
		final int thd = totalHitDice();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate) i.next();
			SR = Math.max(SR, aTemplate.getSR(atl, thd));
		}
		SR += (int) getTotalBonusTo("MISC", "SR", true);
		return SR;
	}

	private HashMap addStringToDRMap(HashMap drMap, String drString)
	{
		if (drString == null || drString.length() == 0)
		{
			return drMap;
		}
		final StringTokenizer aTok = new StringTokenizer(drString, "|", false);
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
		HashMap drMap = new HashMap();
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

				ArrayList aList = eq.getEqModifierList(true);
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
			protectionValue += (int) getTotalBonusTo("DR", symbol + damageType, true);
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
	 *          <code>false</code> otherwise.
	 */
	private boolean hasDeity(String deityName)
	{
		final ArrayList aList = new ArrayList();
		aList.add("PREDEITY:" + deityName);
		return getRace().passesPreReqTestsForList(this, null, aList);
	}

	/**
	 * Check if the characterFeat ArrayList contains the named Feat.
	 *
	 * @param featName String name of the feat to check for.
	 * @return  <code>true</code> if the character has the feat,
	 *          <code>false</code> otherwise.
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
	 *          <code>false</code> otherwise.
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
	 *          <code>false</code> otherwise.
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
	 *          <code>null</code> if not found.
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

	public static Feat getFeatNamedInList(ArrayList aFeatList, String featName)
	{
		return getFeatNamedInList(aFeatList, featName, -1);
	}

	private static Feat getFeatNamedInList(ArrayList aFeatList, String featName, int featType)
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
	 *          <code>null</code> if not found.
	 */
	public Feat getFeatKeyed(String featName)
	{
		final ArrayList aList = aggregateFeatList();
		if (aList.isEmpty())
		{
			return null;
		}
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat) e.next();
			if (aFeat.getKeyName().equals(featName))
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

	boolean isSpellCaster(int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass) e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel() + (int) getTotalBonusTo("PCLEVEL", aClass.getName(), true) >= minLevel)
			{
				return true;
			}
		}
		return false;
	}

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
	 * @param addIt     <code>false</code> means the character must already have
	 *                  the feat (which only makes sense if it allows multiples);
	 *                  <code>true</code> means to add the feat (the only way
	 *                  to add new feats).
	 * @param addAll    <code>false</code> means allow sub-choices;
	 *                  <code>true</code> means no sub-choices, plus if it is
	 *                  a weapon type prof then add the weapon profs.
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
		//
		// See if our choice is not auto or virtual
		//
		Feat aFeat = getFeatNonAggregateNamed(featName);

		final String oldName = featName;
		//
		// if a feat named featName doesn't exist, and featName contains a (blah) descriptor, try removing it.
		//
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

		//
		// aFeat==null means we don't have this feat, so we need to add it
		//
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
				Globals.debugPrint("Feat not found: ", oldName);
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
		int j = (int) (aFeat.getAssociatedCount() * aFeat.getCost()) + feats;
/* JK 2002-07-24 This code is unused. Commenting it out. Will remove next time I come across it.
		String choiceType = "";
		if (aFeat.getChoiceString().lastIndexOf('|') > -1)
		{
			choiceType = aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf('|'));
		}
*/


		// process ADD tags from the feat definition
		if (!addIt)
		{
			aFeat.modAdds(addIt);
		}

		boolean canSetFeats = true;
		//
		// Non-multiple feats that allow choices should not pop up a window and allow removal...should just remove feat
		//
		if (!addIt && !aFeat.isMultiples())
		{
		}
		else
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
			++j;
		}
		else if (addIt && !aFeat.isMultiples())
		{
			--j;
		}
		else
		{
			int associatedListSize = aFeat.getAssociatedCount();
			if (!featList.isEmpty())
			{
				for (Iterator e1 = featList.iterator(); e1.hasNext();)
				{
					final Feat myFeat = (Feat) e1.next();
					if (myFeat.getName().equals(aFeat.getName()))
					{
						associatedListSize = myFeat.getAssociatedCount();
					}
				}
			}

			j -= (int) (associatedListSize * aFeat.getCost());
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
		/*
		 * This is messing up races and templates.
		 * arcady 1/17/2001
		 */
//		if (getTotalLevels() == 0)
//		{
//			featList.clear();
//			return;
//		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
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
							bTok = new StringTokenizer(aString.substring(beginIndex, endIndex), ",", false);
						}
						else
						{
							bTok = new StringTokenizer(aString.substring(beginIndex), ",", false);
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
					return;
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
				{
					Globals.debugPrint("Feat not found in PlayerCharacter.modFeatsFromList: ", aString);
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
						setFeats(getFeats() + 1);
					}
					else
					{
						setFeats(getFeats() - 1);
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
			if (aSkill.getKeyName().equals(skillName))
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
		if (type == 0)
		{
			bonus = race.getBAB();
		}
		else if (type <= Globals.getCheckList().size())
		{
			bonus = race.bonusTo("CHECKS", "BASE." + Globals.getCheckList().get(type - 1).toString());
		}
		if (addBonuses)
		{
			if (type == 0)
			{
				bonus += getTotalBonusTo("TOHIT", "TOHIT", true);
				bonus += getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
			}
			else if (type <= Globals.getCheckList().size())
			{
				bonus += getTotalBonusTo("CHECKS", Globals.getCheckList().get(type - 1).toString(), true);
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
			if (type == 0 || type > Globals.getCheckList().size())
			{
				cBonus += aClass.baseAttackBonus();
			}
			else
			{
				cBonus += aClass.checkBonus(type - 1, false);
			}
		}
		int masterBonus = 0;
		// now we see if this PC is a Familiar/Mount
		final PlayerCharacter nPC = getMasterPC();
		if (type > 0 && type <= Globals.getCheckList().size() && (nPC != null) && isFamiliarOrMount(followerMaster))
		{
			final PlayerCharacter curPC = Globals.getCurrentPC();
			Globals.setCurrentPC(nPC);
			//slarsen - save bonuses
			// It seems to be, so calculate the Masters Save Bonus
			masterBonus = nPC.calculateSaveBonus(type, Globals.getCheckList().get(type - 1).toString(), "BASE");
			Globals.setCurrentPC(curPC);
		}
		cBonus = Math.max(cBonus, masterBonus);
		bonus += cBonus;

		return bonus;
	}

	/**
	 * Should probably read the types from an lst file.
	 * @param followerMaster The follower to check
	 * @return Wether the Follower is a familiar or mount
	 */
	private static boolean isFamiliarOrMount(Follower followerMaster)
	{
		final String type = followerMaster.getType();
		return "FAMILIAR".equalsIgnoreCase(type) || "SPECIAL MOUNT".equalsIgnoreCase(type) || "FIENDISH SERVANT".equalsIgnoreCase(type);
	}

	public int baseAttackBonus()
	{
		return (int) getTotalBonusTo("COMBAT", "BAB", true) /*+ (int) getTotalBonusTo("TOHIT", "TOHIT", true)*/;
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

	/** return -1 if CharacterDomain containing requested domainName is not found,
	 *  otherwise return the index of that CharacterDomain in the characterDomainList
	 */
	int getCharacterDomainIndex(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
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
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return aCD.getDomain();
			}
		}
		return null;
	}

	CharacterDomain getCharacterDomainForDomain(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain) characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
			{
				return aCD;
			}
		}
		return null;
	}

	/*
	 * Why is this method not called indexOfFirstEmptyCharacterDomain()?
	 * Will change this as soon as my IDE is up and running again!
	 *
	 * author: Thomas Behr 07-09-02
	 */
	public int getFirstEmptyCharacterDomain()
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
		final String saDesc = sa.getDesc();
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			final SpecialAbility saFromList = (SpecialAbility) e.next();
			if (saFromList.getName().equals(saName) && saFromList.getDesc().equals(saDesc))
			{
				return true;
			}
		}
		return false;
	}

	public SortedSet getAutoLanguageNames()
	{
		final SortedSet aSet = getAutoLanguages();
		final SortedSet bSet = new TreeSet();
		for (Iterator i = aSet.iterator(); i.hasNext();)
		{
			bSet.add(i.next().toString());
		}
		return bSet;
	}

	public SortedSet getAutoLanguages()
	{
		// find list of all possible langauges
		boolean clearRacials = false;

		final SortedSet autoLangs = new TreeSet();

		// Search for a CLEAR in the list and if found clear all BEFORE but not AFTER it.
		// ---arcady June 1, 2002

		for (Iterator e = templateAutoLanguages.iterator(); e.hasNext();)
		{
			String tempLang = e.next().toString();
			if (".CLEARRACIAL".equals(tempLang))
			{
				clearRacials = true;
				languages.removeAll(getRace().getAutoLanguages());
			}
			else if (".CLEARALL".equals(tempLang) || ".CLEAR".equals(tempLang))
			{
				clearRacials = true;
				autoLangs.clear();
				languages.clear();
			}
			else if (".CLEARTEMPLATES".equals(tempLang))
			{
				autoLangs.clear();
				languages.removeAll(templateAutoLanguages);
			}
			else
			{
				autoLangs.add(tempLang);
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

				ArrayList aList = eq.getEqModifierList(true);
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

		languages.addAll(autoLangs);
		return autoLangs;
	}

	private void removeNaturalWeapons()
	{
		for (Iterator e = getRace().getNaturalWeapons().iterator(); e.hasNext();)
		{
			equipmentList.remove(e.next());
		}
	}

	private void addNaturalWeapons()
	{
		equipmentList.addAll(getRace().getNaturalWeapons());
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
		final TreeSet favored = new TreeSet(favoredClasses);
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
			if (!aList.contains(aClass.getDisplayClassName()) && (!aList.contains(aClass.toString())) && !aClass.isPrestige())
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
					if ((maxClassLevel - (aClass.getLevel())) > 1)
					{
						++xpPenalty;
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

	/** Returns a SortedSet list of bonus languages gained from race, class, and templates. */
	public SortedSet getBonusLanguages(boolean removeKnown)
	{
		final SortedSet bonusLangs = new TreeSet();
		SortedSet bonusLangsb;
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

		bonusLangsb = Globals.extractLanguageList(bonusLangs);

		if (removeKnown)
		{
			bonusLangsb.removeAll(languages);
		}
		return bonusLangsb;
	}

	public void addLanguage(String aString)
	{
		if (!languages.contains(aString))
		{
			languages.add(aString);
		}
	}

	void addFreeLanguage(String aString)
	{
		if (!languages.contains(aString))
		{
			languages.add(aString);
			++freeLangs;
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
			final Skill speakLang = getSkillNamed("Speak Language");
			if (speakLang != null)
			{
				i += speakLang.getTotalRank().intValue();
			}
		}
		if (pcRace != null)
		{
			i += pcRace.getLangNum() + (int) getTotalBonusTo("LANGUAGES", "NUMBER", true);
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

	private SortedSet addWeaponProfsLists(PObject obj, ArrayList aList, SortedSet aSet, ArrayList aFeatList, boolean addIt)
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
				final StringTokenizer bTok = new StringTokenizer(aString.substring(idx + 1), "[]", false);
				ArrayList preReqList = new ArrayList();
				while (bTok.hasMoreTokens())
				{
					preReqList.add(bTok.nextToken());
				}
				aString = aString.substring(0, idx);
				if (preReqList.size() != 0)
				{
					if (!obj.passesPreReqTestsForList(preReqList))
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
				Equipment eq = Globals.getEquipmentNamed(aString);
				if (eq == null)
				{
					StringTokenizer aTok = new StringTokenizer(aString, ".", false);
					String desiredTypes = "Weapon";
					ArrayList listFromWPType = null;
					ArrayList listFromEquipmentType = null;
					while (aTok.hasMoreTokens())
					{
						String bString = aTok.nextToken();
						if (Globals.weaponTypesContains(bString))
						{
							listFromWPType = Globals.getWeaponProfs(bString);
						}
						else
						{
							desiredTypes += "." + bString;
						}
					}
					if (desiredTypes.indexOf(".") > -1)
					{
						listFromEquipmentType = Globals.getEquipmentOfType(Globals.getEquipmentList(), desiredTypes, "");
					}
					ArrayList addWPs = new ArrayList();
					if (listFromWPType != null)
					{
						for (Iterator i = listFromWPType.iterator(); i.hasNext();)
						{
							addWPs.add(i.next().toString());
						}
					}
					if (listFromEquipmentType != null)
					{
						ArrayList bList = new ArrayList();
						for (Iterator i = listFromEquipmentType.iterator(); i.hasNext();)
						{
							String bString = ((Equipment) i.next()).profName();
							bList.add(bString);
						}
						if (listFromWPType == null)
						{
							addWPs.addAll(bList);
						}
						else
						{
							addWPs.retainAll(bList);
						}
					}
					for (Iterator i = addWPs.iterator(); i.hasNext();)
					{
						if (addIt)
						{
							addWeaponProfToList(aFeatList, (String) i.next(), true);
						}
					}

				}
				else
				{
					aSet.add(aString);
					if (addIt)
					{
						addWeaponProfToList(aFeatList, aString, true);
					}
				}
			}
		}
		return aSet;
	}

	private SortedSet getAutoWeaponProfs(ArrayList aFeatList)
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

				ArrayList aList = eq.getEqModifierList(true);
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

	/** Unused. I will comment this out when I come across it next unless this comment has been changed. TODO JK 2003-01-10 */
	private void addFeatProfs(final ArrayList stableList, ArrayList aFeatList, SortedSet results)
	{
		if (stableList != null && !stableList.isEmpty())
		{
			for (Iterator e = stableList.iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat) e.next();
				final String addString = aFeat.getAddString();
				final StringTokenizer aTok = new StringTokenizer(addString, "|", false);
				if (aTok.countTokens() != 2)
				{
					continue;
				}

				final String addType = aTok.nextToken();
				if ("WEAPONPROFS".equals(addType))
				{
					final String addSec = aTok.nextToken();
					if (Globals.weaponTypesContains(addSec))
					{
						Collection weaponProfs=Globals.getAllWeaponProfsOfType(addSec);
						for (Iterator e2 = weaponProfs.iterator(); e2.hasNext();)
						{
							final WeaponProf weaponProf = (WeaponProf) e2.next();
							results.add(weaponProf.getName());
							addWeaponProfToList(aFeatList, weaponProf.getName(), true);
						}
					}
					else
					{
						results.add(addSec);
						addWeaponProfToList(aFeatList, addSec, true);
					}
				}
			}
		}
	}

	public void addWeaponProf(String aString)
	{
		addWeaponProfToList(featList, aString, false);
	}

	private void addWeaponProfToList(ArrayList aFeatList, String aString, boolean isAuto)
	{
		if (aString.startsWith("WEAPONTYPE="))
		{
			final ArrayList weapList = Globals.getEquipmentOfType(Globals.getEquipmentList(), "WEAPON." + aString.substring(11), "");
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
		//
		// Add all weapons of type aString (eg. Simple, Martial, Exotic, Ranged, etc.)
		//
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
					//
					// No need to add to list, if multiples not allowed
					//
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
						if (isAuto && !aFeat.isMultiples() && !"Weapon Proficiency".equals(featName))
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
						if (!"NATURAL".equalsIgnoreCase(wp.getType()))
						{
							Globals.errorPrint("Weaponprof feat not found: " + featName + ":" + aString);
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

	ArrayList aggregateSpellList(String aType, String school, String subschool, int minLevel, int maxLevel)
	{
		final ArrayList aArrayList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			String cName = aClass.getKeyName();
			if (aClass.getCastAs().length() > 0)
			{
				cName = aClass.getCastAs();
			}
			if (Globals.isDebugMode())
			{
				Globals.debugPrint("Cast As:", cName);
			}
			if ("Any".equalsIgnoreCase(aType) || aType.equalsIgnoreCase(aClass.getSpellType()) || aType.equalsIgnoreCase(cName))
			{
				for (int a = minLevel; a <= maxLevel; ++a)
				{
					ArrayList aList = aClass.getCharacterSpell(null, "", a);
					if (aList.isEmpty())
						continue;
					for (Iterator i = aList.iterator(); i.hasNext();)
					{
						final CharacterSpell cs = (CharacterSpell) i.next();
						final Spell aSpell = cs.getSpell();
						if (((school.length() == 0 || school.equals(aSpell.getSchool())) || (subschool.length() == 0 || subschool.equals(aSpell.getSubschool()))))
							aArrayList.add(cs.getSpell());
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
			if (eq.getCarried().compareTo(floatZero) > 0 && !eq.isHeaderParent() && eq.getParent() == null)
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
		BigDecimal totalValue = new BigDecimal("0");
		;
		if (!equipmentList.isEmpty())
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment) e.next();
				if (!eq.isHeaderParent())
				{
					totalValue = totalValue.add(eq.getCost().multiply(new BigDecimal(eq.qty())));
				}
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
	ArrayList getEquipmentOfType(String typeName, int status)
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
	private ArrayList getEquipmentOfType(String typeName, String subtypeName, int status)
	{
		final ArrayList aArrayList = new ArrayList();
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
	public ArrayList getEquipmentOfTypeInOutputOrder(String typeName, int status)
	{
		return sortEquipmentListInOutputOrder(getEquipmentOfType(typeName, status));
	}

	/**
	 * Retrieves a list, sorted in output order, of the character's equipment
	 * matching the type, sub type and status criteria. This list is in
	 * ascending order of the equipment's outputIndex field. If multiple items
	 * of equipment have the same outputIndex they will be ordered by name.
	 * Note hidden items (outputIndex = -1) are not included in this list.
	 *
	 * @param typeName The type of equipment to be selected
	 * @param subtypeName The subtype of equipment to be selected (empty string for no subtype)
	 * @param status The required status: 1 (equipped) 2 (not equipped) 3 (don't care)
	 * @return An ArrayList of the matching equipment objects in output order.
	 */
	public ArrayList getEquipmentOfTypeInOutputOrder(String typeName, String subtypeName, int status)
	{
		return sortEquipmentListInOutputOrder(getEquipmentOfType(typeName, subtypeName, status));
	}

	int handsFull()
	{
		final ArrayList aArrayList = getEquipmentOfType("Weapon", 1);
		final ArrayList bArrayList = getEquipmentOfType("Shield", 1);
		Iterator iter;
		int hands = 0;
		WeaponProf wp;
		Equipment eq;
		if (!aArrayList.isEmpty())
		{
			for (iter = aArrayList.iterator(); iter.hasNext();)
			{
				eq = (Equipment) iter.next();
				if (eq.isEquipped())
				{
					// sage_sam 2 Dec 2002 for natural weapons Bug #586332
					// don't add hands for natural weapons if they don't
					// use them.
					if ((eq.isNatural()) && !(eq.getHands() > 0))
					{
						continue;
					}

					wp = Globals.getWeaponProfNamed(eq.profName());
					if (wp == null)
					{
						GuiFacade.showMessageDialog(null, "No entry in weapons.lst for " + eq.profName() + ". Weapons must be in that file to equip them.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						Globals.debugPrint("Globals: " + Globals.getWeaponProfNames(",",true) + System.getProperty("line.separator") + "Prof: " + eq.profName());
						hands += 3;
					}
					else
					{
						final int hand = eq.getLocation();
						switch (hand)
						{
							case Equipment.NOT_CARRIED:
							case Equipment.EQUIPPED_NEITHER:
							case Equipment.EQUIPPED_RAPIDSHOT:
							case Equipment.CARRIED_NEITHER:
							case Equipment.CONTAINED:
								break;
							case Equipment.EQUIPPED_PRIMARY:
								hands += Math.max(1, eq.getHands());
								break;
							case Equipment.EQUIPPED_SECONDARY:
								hands += Math.max(1, eq.getHands());
								break;
							case Equipment.EQUIPPED_BOTH:
								hands += Math.max(2, eq.getHands());
								break;
							case Equipment.EQUIPPED_TWO_HANDS:
								hands += Math.max(2, eq.getNumberEquipped());
								break;
							default:
								Globals.errorPrint("In PlayerCharacter.handsFull the hand " + hand + " is unsupported.");
								break;

						}
					}
				}
			}
		}

		//
		// Process equipped shield(s)
		//
		if (!bArrayList.isEmpty())
		{
			for (iter = bArrayList.iterator(); iter.hasNext();)
			{
				eq = (Equipment) iter.next();
				if (eq.isEquipped())
				{
					hands += eq.getHands();
				}
			}
		}
		return hands;
	}

	boolean canEquip(String typeName)
	{
		// Get the number of equipment of type <typeName> already equipped
		final ArrayList aArrayList = getEquipmentOfType(typeName, 1);

		// Get the total number of slots for type <typeName>
		final int slotBonus = (int) getTotalBonusTo("SLOTS", typeName, true);

		// The character's hands = racial hands + bonus hands (such as might be granted by a mutation or some such game mechanic)
		final int raceHands = race.getHands() + (int) getTotalBonusTo("SLOTS", "HANDS", true);

		// Rings have special slot logic
		if ("RING".equals(typeName))
		{
			return aArrayList.size() < raceHands + slotBonus;
		}

		// Weapons and shields both occupy the same type of slot -- held in hand
		else if (typeName.equalsIgnoreCase("Weapon") || typeName.equalsIgnoreCase("Shield"))
		{
			final int hands = handsFull();
			if (hands > raceHands)
			{
				GuiFacade.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return false;
			}
			return true;
		}

		// Other types of items are based on the number of slots available for that type
		else
		{
			return aArrayList.size() <= slotBonus;
		}
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
		rapidshotWeapons.clear();
		if (equipmentList.isEmpty())
		{
			return;
		}
		final ArrayList unequippedPrimary = new ArrayList();
		final ArrayList unequippedSecondary = new ArrayList();
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment) e.next();
			if (!eq.isWeapon())
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
			if (eq.getLocation() == Equipment.EQUIPPED_RAPIDSHOT)
			{
				rapidshotWeapons.add(eq);
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

	public double getBonusForMapKey(final String aKey)
	{
		return getBonusForMapKey(aKey, 0);
	}

	private double getBonusForMapKey(final String aKey, final double defaultValue)
	{
		final String val = (String) bonusMap.get(aKey);
		if ((val != null) && !"x".equals(val))
		{
			return Float.parseFloat(val);
		}
		return defaultValue;
	}

	public double getTotalBonusTo(String bonusType, String bonusName, boolean stacks)
	{
		double bonus;
		final String prefix = new StringBuffer(bonusType.toUpperCase()).append('.').append(bonusName.toUpperCase()).append('.').toString();
		bonusType = bonusType.toUpperCase();
		bonusName = bonusName.toUpperCase();
		// go through hashmap and zero out all entries
		// that deal with this bonus request

		for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			if (aKey.startsWith(prefix))
			{
				bonusMap.put(aKey, "x");
			}
		}
		try
		{
			getStatBonusTo(bonusType, bonusName);
			if ("COMBAT".equals(bonusType) || "SKILL".equals(bonusType) || "TOHIT".equals(bonusType))
			{
				getSizeAdjustmentBonusTo(bonusType, bonusName);
			}
			else if ("STAT".equalsIgnoreCase(bonusType))
			{
				getSizeAdjustmentBonusTo(bonusType, bonusName);
				getAgeBonusTo(bonusType, bonusName);
			}
			else if ("CHECKS".equalsIgnoreCase(bonusType))
			{
				getCheckBonusTo(bonusType, bonusName);
			}
			getAlignmentBonusTo(bonusType, bonusName);
			if (!classList.isEmpty())
			{
				getClassBonusTo(bonusType, bonusName);
			}
			if (!companionModList.isEmpty())
			{
				getCompanionModBonusTo(bonusType, bonusName);
			}
			if (!equipmentList.isEmpty())
			{
				getEquipmentBonusTo(bonusType, bonusName, stacks);
			}
			getFeatBonusTo(bonusType, bonusName, stacks);
			if (!templateList.isEmpty())
			{
				getTemplateBonusTo(bonusType, bonusName, stacks);
			}
			if (!characterDomainList.isEmpty())
			{
				getDomainBonusTo(bonusType, bonusName);
			}
			getRace().bonusTo(bonusType, bonusName);
			if (!getSkillList().isEmpty())
			{
				getSkillBonusTo(bonusType, bonusName);
			}
			if (getDeity() != null)
			{
				getDeity().bonusTo(bonusType, bonusName);
			}
			if (bonusType.startsWith("WEAPONPROF="))
			{
				getWeaponProfBonusTo(bonusType.substring(11), bonusName);
			}
			if (bonusType.startsWith("ARMORPROF="))
			{
				getArmorProfBonusTo(bonusType.substring(10), bonusName);
			}
			bonus = sumBonusesFor(prefix);
		}
		catch (NumberFormatException exc)
		{
			Globals.errorPrint("error in getTotalBonusTo " + bonusType + " " + bonusName, exc);
			bonus = 0;
		}

		return bonus;
	}

	private double sumBonusesFor(String prefix)
	{
		double bonus = 0;
		final ArrayList aList = new ArrayList();
		for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			if (aList.contains(aKey))
			{
				continue;
			}
			if (aKey.startsWith(prefix))
			{
				String rString = aKey;
				if (rString.endsWith(".STACK"))
				{
					rString = rString.substring(0, rString.length() - 6);
				}
				else if (rString.endsWith(".REPLACE"))
				{
					rString = rString.substring(0, rString.length() - 8);
				}
				double aBonus = getBonusForMapKey(rString);
				double replaceBonus = getBonusForMapKey(rString + ".REPLACE");
				double stackBonus = getBonusForMapKey(rString + ".STACK");
				aList.add(rString);
				aList.add(rString + ".STACK");
				aList.add(rString + ".REPLACE");
				// allow for negative values (default value of 0 breaks this)
				if ((0.0 == replaceBonus) && (null == bonusMap.get(rString + ".REPLACE")))
				{
					replaceBonus = -99999.0;
				}
				bonus += Math.max(aBonus, replaceBonus) + stackBonus;
			}
		}
		return bonus;
	}

	public String listBonusesFor(String prefix)
	{
		StringBuffer buf = new StringBuffer();
		final ArrayList aList = new ArrayList();
		for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			if (aKey.startsWith(prefix))
			{
				// make a list of any keys that start with .REPLACE
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
					final String reason = aKey.substring(prefix.length() + 1);
					final int b = (int) getBonusForMapKey(aKey);
					if (b == 0)
					{
						continue;
					}
					if (!"NULL".equals(reason))
					{
						buf.append(reason).append(' ');
					}
					buf.append(Delta.toString(b));
				}
			}
		}
		// Now adjust the bonus if the .REPLACE value replaces the value without .REPLACE
		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final String replaceKey = (String) i.next();
				if (replaceKey.length() > 7)
				{
					final String aKey = replaceKey.substring(0, replaceKey.length() - 8);
					final double replaceBonus = getBonusForMapKey(replaceKey);
					double aBonus = getBonusForMapKey(aKey);
					aBonus += getBonusForMapKey(aKey + ".STACK");
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

	/*
	* return bonus total for a specific bonusType.
	* e.g. getBonusDueToType("COMBAT","AC","Armor") to get armor bonuses.
	*/
	public double getBonusDueToType(String mainType, String subType, String bonusType)
	{
		final String typeString = mainType.toUpperCase() + "." + subType.toUpperCase() + "." + bonusType.toUpperCase();

		final double bonus = getBonusForMapKey(typeString, Double.NaN);
		final double replaceBonus = getBonusForMapKey(typeString + ".REPLACE", Double.NaN);
		final double stackBonus = getBonusForMapKey(typeString + ".STACK");
		//
		// Using NaNs in order to be able to get the max between
		// an undefined bonus and a negative 'bonus'.
		//
		if (Double.isNaN(bonus))
		{
			if (Double.isNaN(replaceBonus))
			{
				return stackBonus;
			}
			return replaceBonus + stackBonus;
		}
		else if (Double.isNaN(replaceBonus))
		{
			return bonus + stackBonus;
		}
		return Math.max(bonus, replaceBonus) + stackBonus;
	}

	/**
	 * Caluclates and sets the value of the specified bonus.
	 * NB: Called as part of *calculating* bonuses. A call to
	 * here does not necessarily indicate a change in a bonus.
	 */
	void setBonusStackFor(double bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
			// only specific bonuses can actually be fractional - should define this in external file
			if (!bonusType.startsWith("ITEMWEIGHT") && !bonusType.startsWith("ITEMCOST") && !bonusType.startsWith("ACVALUE") && !bonusType.startsWith("ITEMCAPACITY") && !bonusType.startsWith("LOADMULT"))
			{
				bonus = (double) ((int) bonus); // truncate all but above bonuses
			}
		}

		int index = -2;
		final StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);
		// e.g. "COMBAT.AC.Dodge"
		if (aTok.countTokens() > 2)
		{
			// should be bonus category e.g. "COMBAT"
			String aString = aTok.nextToken(); //XXX Is the intention that the token be thrown away here?
			// should be bonus name e.g. "AC"
			aString = aTok.nextToken();
			// should be bonus type e.g. whatever
			aString = aTok.nextToken();
			if (aString != null && !"null".equalsIgnoreCase(aString))
			{
				index = Globals.getBonusStackList().indexOf(aString); // e.g. Dodge
			}
			if (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken(); // could be .REPLACE or .STACK
				if ("REPLACE".equals(aString) || "STACK".equals(aString))
				{
					index = 0; // REPLACE and STACK both stack with themselves, this lets that happen
				}
			}
		}
		if (index == -1) // meaning, a non-stacking bonus
		{
			final String aKey = (String) bonusMap.get(bonusType);
			if (aKey == null || "x".equals(aKey))
			{
				bonusMap.put(bonusType, String.valueOf(bonus));
			}
			else
			{
				bonusMap.put(bonusType, String.valueOf(Math.max(bonus, Float.parseFloat(aKey))));
			}
		}
		else // stacking bonuses
		{
			if (bonusType == null)
			{
				bonusType = "";
			}
			final String aKey = (String) bonusMap.get(bonusType);
			if (aKey == null || "x".equals(aKey))
			{
				bonusMap.put(bonusType, String.valueOf(bonus));
			}
			else
			{
				bonusMap.put(bonusType, String.valueOf(bonus + Float.parseFloat(aKey)));
			}
		}
	}

	private double getSkillBonusTo(String type, String aName)
	{
		double bonus = 0;
		if (getSkillList().isEmpty())
		{
			return bonus;
		}

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill) e.next();
			// Don't add bonuses included in the skill with
			// the same name as it gets added on elsewhere
			//
			if (!aSkill.getName().equalsIgnoreCase(aName))
			{
				bonus += aSkill.bonusTo(type, aName);
			}
		}
		return bonus;
	}

	private double getClassBonusTo(String type, String aName)
	{
		int bonus = 0;
		if (classList.isEmpty())
		{
			return bonus;
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass) e.next();
			bonus += aClass.getBonusTo(type, aName, aClass.getLevel());
		}
		return bonus;
	}

	public double getStatBonusTo(String type, String aName)
	{
		return statList.getBonusTo(type, aName);
	}

	public double getSizeAdjustmentBonusTo(String type, String aName)
	{
		// force bonus list to get updated
		getSizeAdjustment().bonusTo(type, aName);
		return getBonusDueToType(type, aName, "SIZE");
	}

	private double getAgeBonusTo(String type, String aName)
	{
		return BioSet.getBonusTo(this, type, aName);
	}

	private static double getCheckBonusTo(String type, String aName)
	{
		return getPObjectListBonusTo(Globals.getCheckList(), type, aName);
	}

	private static double getAlignmentBonusTo(String type, String aName)
	{
		return getPObjectListBonusTo(Globals.getAlignmentList(), type, aName);
	}

	private static double getPObjectListBonusTo(ArrayList aList, String type, String aName)
	{
		double bonus = 0;

		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			PObject obj = (PObject) i.next();
			bonus += obj.bonusTo(type, aName);
		}
		return bonus;
	}

	/**
	 * return bonus from CompanionMod list
	 **/
	private double getCompanionModBonusTo(String type, String aName)
	{
		double bonus = 0;
		if (companionModList.isEmpty())
		{
			return bonus;
		}
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod) e.next();
			bonus += cMod.bonusTo(type, aName);
		}
		return bonus;
	}

	private double getDomainBonusTo(String type, String aName)
	{
		double bonus = 0;
		if (characterDomainList.isEmpty())
		{
			return bonus;
		}
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) e.next();
			Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				bonus += aDomain.bonusTo(type, aName);
			}
		}
		return bonus;
	}

	private void mergeBonusMap(HashMap map)
	{
		if (map.isEmpty())
		{
			return;
		}
		for (Iterator i = map.keySet().iterator(); i.hasNext();)
		{
			final String bonusType = i.next().toString();
			setBonusStackFor(Float.parseFloat((String) map.get(bonusType)), bonusType);
		}
		map.clear();
	}

/*	private void mergeBonusMap(HashMap map)
	{
		if (map.isEmpty())
			return;
		for (Iterator i = map.keySet().iterator(); i.hasNext();)
		{
			final String bonusType = i.next().toString();
			double iBonus = Float.parseFloat((String) map.get(bonusType));

			final String aKey = (String) bonusMap.get(bonusType);

			if (aKey != null)
			{
				boolean stacks = true;
				StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);
				if (aTok.countTokens() > 2)
				{
					// bonus category e.g. "COMBAT"
					String aString = aTok.nextToken(); //XXX Is the intention that this token be thrown away here?
					// bonus name e.g. "AC"
					aString = aTok.nextToken();
					// bonus type e.g. whatever
					aString = aTok.nextToken();
					if ((aString != null) && !aString.equalsIgnoreCase("null"))
					{
						stacks = Globals.getBonusStackList().indexOf(aString) >= 0;
					}
				}

				double iCurrent = getBonusForMapKey(bonusType);
				if (!stacks)
				{
					iBonus = Math.max(iBonus, iCurrent);
				}
				else // stacking bonuses
				{
					iBonus += iCurrent;
				}
			}
			bonusMap.put(bonusType, String.valueOf(iBonus));
		}
	}
*/
	private double getEquipmentBonusTo(String type, String aName, boolean stacks)
	{
		double bonus = 0;
		if (equipmentList.isEmpty())
		{
			return bonus;
		}

		Equipment eq;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			eq = (Equipment) e.next();
			if (eq.isEquipped())
			{
				if (stacks)
				{
					bonus += eq.bonusTo(type, aName, true);
					mergeBonusMap(eq.getBonusMap());
					if (eq.isWeapon() && eq.isDouble())
					{
						bonus += eq.bonusTo(type, aName, false);
						mergeBonusMap(eq.getBonusMap());
					}
				}
				else
				{
					bonus = Math.max(bonus, eq.bonusTo(type, aName, true));
					mergeBonusMap(eq.getBonusMap());
					if (eq.isWeapon() && eq.isDouble())
					{
						bonus = Math.max(bonus, eq.bonusTo(type, aName, false));
						mergeBonusMap(eq.getBonusMap());
					}
				}
			}
		}
		return bonus;
	}

	public double getTemplateBonusTo(String type, String aName, boolean subSearch)
	{

		PCTemplate t;
		double i = 0;
		if (templateList.isEmpty())
		{
			return i;
		}
		final Iterator iterator = templateList.iterator();

		for (Iterator e = iterator; e.hasNext();)
		{
			t = (PCTemplate) e.next();

			double j = t.bonusTo(type, aName);
			if (j == 0)
			{
				j = t.bonusTo(type, "LIST");
			}
			int k = Math.max(1, (int) (t.getAssociatedCount() * t.getCost()));
			if (subSearch && t.getAssociatedCount() > 0)
			{
				k = 0;
				for (int f = 0; f < t.getAssociatedCount(); ++f)
				{
					String aString = t.getAssociated(f);
					if (aString.equals(aName))
					{
						++k;
					}
				}
			}
			if (k == 0 && j != 0)
			{
				i += j;
			}
			else
			{
				i += j * k;
			}
		}
		return i;
	}

	public double getFeatBonusTo(String type, String aName, boolean subSearch)
	{
		double i = 0;
		final ArrayList aList = aggregateFeatList();
		if (aList.isEmpty())
		{
			return i;
		}
		final Iterator iterator = aList.iterator();
		for (Iterator e = iterator; e.hasNext();)
		{
			Feat aFeat = (Feat) e.next();
			double j = aFeat.bonusTo(type, aName);
			if (j == 0)
			{
				j = aFeat.bonusTo(type, "LIST");
			}
			int k = Math.max(1, (int) (aFeat.getAssociatedCount() * aFeat.getCost()));
			if (subSearch && aFeat.getAssociatedCount() > 0)
			{
				k = 0;
				for (int f = 0; f < aFeat.getAssociatedCount(); ++f)
				{
					final String aString = aFeat.getAssociated(f);
					if (aString.equals(aName))
					{
						++k;
					}
				}
			}
			if (k == 0 && j != 0)
			{
				i += j;
			}
			else
			{
				i += j * k;
			}
		}
		return i;
	}

	private int getDefense()
	{
		return 0;
	}

//begin breakdown of getDefense info Arknight 08-09-02
//rework by Arknight 01-02-03
//*************************************************
	public int getDefenseTotal()
	{
		return (int) getTotalBonusTo("COMBAT", "AC", true);
	}

	public int flatfootedDefense()
	{
		int i = getDefenseTotal();
		// check if we may keep our dexterity bonus to AC
		for (Iterator it = getSpecialAbilityList().iterator(); it.hasNext();)
		{
			if (((SpecialAbility) it.next()).getName().startsWith("Uncanny Dodge"))
			{
				return i;
			}
		}

		// we obviously do NOT not keep our dexterity bonus to AC,
		// but we must apply dexterity penalties to AC!
		i -= abilityDefense();
		if (abilityDefense() < 0)
		{
			i += Math.min(0, abilityDefense());
		}
		i -= dodgeDefense();
		return i;
	}

	public int touchDefense()
	{
		return baseDefense() + abilityDefense() + sizeDefense() + classDefense() + dodgeDefense() + miscDefense();
	}

	public int abilityDefense()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "ABILITY");
	}

	public int baseDefense()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "BASE");
	}

	public int classDefense()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "CLASSDEFENSE");
	}

	public int dodgeDefense()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "DODGE");
	}

	public int equipmentDefense()
	{
		return ((int) getBonusDueToType("COMBAT", "AC", "EQUIPMENT") + (int) getBonusDueToType("COMBAT", "AC", "ARMOR"));
	}

	public int miscDefense()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "MISC");
	}

	public int naturalDefense()
	{
		return (int) getBonusDueToType("COMBAT", "AC", "NATURALARMOR");
	}

	public int sizeDefense()
	{
		return (int) getSizeAdjustmentBonusTo("COMBAT", "AC");
	}

//**************************************************
	public int woundPoints()
	{
		final int i = (int) getTotalBonusTo("HP", "WOUNDPOINTS", false);
		return i;
	}

	public int reputation()
	{
		int i = (int) getRace().bonusTo("CLASS", "REPUTATION") + (int) getEquipmentBonusTo("CLASS", "REPUTATION", true);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass) e.next();
			final int classLevel = aClass.getLevel();
			i += (int) aClass.getBonusTo("CLASS", "REPUTATION", classLevel);
			String aString = aClass.getReputationString();
			int k = Integer.parseInt(aString);
			switch (k)
			{
				case 0:	/*Best*/
					i += 3 + classLevel >> 1;
					break;
				case 1:	/*MHigh*/
					i += 1 + classLevel >> 1;
					break;
				case 2:	/*MLow*/
					i += classLevel >> 1;
					break;
				case 3:	/*Low*/
					i += classLevel / 3;
					break;
				case 4:	/*NPCH*/
					i += (classLevel + 1) / 3;
					break;
				case 5:	/*NPCL*/
					i += classLevel >> 2;
					break;
				case 6:	/*PHigh*/
					if (classLevel % 3 != 0)
					{
						i += (classLevel - (classLevel / 3));
					}
					break;
				case 8:	/*P v3*/
				case 7:	/*PLow*/
					i += classLevel >> 1;
					break;

				case 9:	/*P v4*/
					if (classLevel % 4 != 0)
					{
						i += (classLevel - (classLevel >> 2));
					}
					break;
				case 10:	/*P v5*/
					switch (classLevel)
					{
						case 1:
						case 2:
						case 5:
						case 6:
						case 9:
						case 10:
							++i;
							break;
						case 3:
						case 4:
						case 7:
						case 8:
							break;
						default:
							Globals.errorPrint("In PlayerCharacter.reputation (reputation type 10) the classLevel " + classLevel + " is unsupported.");
							break;
					}

				case 11:	/*P classlevel/5 + (classlevel +3)/5 Wheel of Time method. */
					int tempLvl = classLevel;
					i += ((tempLvl / 5) + ((tempLvl + 3) / 5));
					break;
				default:
					break;
			}
		}
		final int y = getTotalLevels();
		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat) e.next();
			if (aFeat.getLevelsPerRepIncrease() != 0)
			{
				i += y / aFeat.getLevelsPerRepIncrease();
			}
		}
		i += (int) getFeatBonusTo("CLASS", "REPUTATION", true) + (int) getTemplateBonusTo("CLASS", "REPUTATION", true);
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
				final ArrayList aList = aClass.getCharacterSpell(null, aName, -1);
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
	 */
	public void setAutoSpells(boolean yOn)
	{
		autoKnownSpells = yOn;
		setDirty(true);
	}

	public boolean getAutoSpells()
	{
		return autoKnownSpells;
	}

	/**
	 * acs is the CharacterSpell object containing the spell which is to be modified
	 * aFeatList is the list of feats to be added to the SpellInfo object added to acs
	 * className is the name of the class whose list of characterspells will be modified
	 * bookName is the name of the book for the SpellInfo object
	 * spellLevel is the original (unadjusted) level of the spell not including feat adjustments
	 * adjSpellLevel is the adjustedLevel (including feat adjustments) of this spell, it may
	 * be higher if the user chooses a higher level.
	 * Returns: an empty string on successful completion, otherwise the return value indicates the
	 * reason the add function failed.
	 **/
	public String addSpell(CharacterSpell acs, ArrayList aFeatList, String className, String bookName, int adjSpellLevel, int spellLevel)
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
		final ArrayList sList = aClass.getCharacterSpell(null, bookName, adjSpellLevel);
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
		int known;
		if (Globals.isSSd20Mode())
		{
			known = aClass.getKnownForLevel(aClass.getLevel(), spellLevel, bookName);
		}
		else
		{
			known = aClass.getKnownForLevel(aClass.getLevel(), spellLevel);
		}
		int specialKnown = 0;
		final int cast = aClass.getCastForLevel(aClass.getLevel(), adjSpellLevel, bookName, true, true);
		int castByStat; // how many spells of actual spell's level can be cast without stat limitation
		int castLowLevel;
		if (spellLevel == adjSpellLevel)
		{
			castLowLevel = cast;
		}
		else
		{
			castLowLevel = aClass.getCastForLevel(aClass.getLevel(), spellLevel, bookName, true, true);
		}
		if (cast != 0)
		{
			castByStat = cast;
		}
		else
		{
			castByStat = aClass.getCastForLevel(aClass.getLevel(), adjSpellLevel, bookName, true, false);
		}
		final int listNum = aClass.memorizedSpellForLevelBook(adjSpellLevel, bookName);
		final boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		if (isDefault)
		{
			specialKnown = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), spellLevel);
		}

		// known is the maximun spells that can be known this level
		// listNum is the current spells already memorized this level
		// cast is the number of spells that can be cast at this level
		if (Globals.isSSd20Mode() && !isDefault)
		{
			if ((listNum + aSpell.getCastingThreshold()) > known + specialKnown)
			{
				return "Your maximum space for this category is:" + known + specialKnown;
			}
		}
		// Modified this to use new availableSpells() method so you can "blow" higher-level slots on
		// lower-level spells
		// in re BUG [569517]
		// sk4p 13 Dec 2002
		else if (!aClass.getMemorizeSpells() && !availableSpells(adjSpellLevel, aClass, bookName, true, acs.isSpecialtySpell()))
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
			Globals.errorPrint("Notice: removing " + acs.getSpell().getName() + " even though it is an auto known spell");
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

	private void setStableVirtualFeatList(ArrayList aFeatList)
	{
		stableVirtualFeatList = aFeatList;
		setVirtualFeatsStable(aFeatList != null);
	}

	private ArrayList getStableVirtualFeatList()
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

	public ArrayList vFeatList()
	{
		ArrayList vFeatList = getStableVirtualFeatList();
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
				for (Iterator i = stableVirtualFeatList.iterator() ;i.hasNext();)
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
				classTok = new StringTokenizer((String) e1.next(), ":", false);
				final int level = Integer.parseInt(classTok.nextToken());
				if (level <= aClass.getLevel())
				{
					classTok = new StringTokenizer(classTok.nextToken(), "|", false);
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
									final StringTokenizer aTok = new StringTokenizer(featName.substring(i + 1, j), ",", false);
									while (aTok.hasMoreTokens())
									{
										final String a = aTok.nextToken();
										if (!classFeat.containsAssociated(a))
											classFeat.addAssociated(a);
									}
								}
							}
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
		final StringTokenizer raceTok = new StringTokenizer(getRace().getVFeatList(), "|", false);
		while (raceTok.hasMoreTokens())
		{
			final String featName = raceTok.nextToken();
			vFeatList = addVirtualFeat(featName, vFeatList);
		}

		setStableVirtualFeatList(vFeatList);
		return vFeatList;
	}

	public ArrayList addVirtualFeat(final String featName, ArrayList vFeatList)
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
					final StringTokenizer aTok = new StringTokenizer(featName.substring(i + 1, j), ",", false);
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
			vFeatList.add(aFeat);
			setStableVirtualFeatList(vFeatList);
		}
		return vFeatList;
	}

	private void setStableAutomaticFeatList(ArrayList aFeatList)
	{
		stableAutomaticFeatList = aFeatList;
		setAutomaticFeatsStable(aFeatList != null);
	}

	private ArrayList getStableAutomaticFeatList()
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

	private static void addToAutoFeatList(ArrayList autoFeatList, String featName)
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
				//
				// Hack for toughness-style feats
				//
				if (aFeat.getChoiceString().startsWith("HP|") && (subName.length() == 0))
				{
					subName = "CURRENTMAX";
				}
				if (subName.length() != 0)
				{
					aFeat.addAssociated(subName);
				}
				aFeat.setFeatType(Feat.FEAT_AUTOMATIC);
				autoFeatList.add(aFeat);
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
			//
			// Hack for toughness-style feats
			//
			if (aFeat.getChoiceString().startsWith("HP|") && (subName.length() == 0))
			{
				subName = "CURRENTMAX";
			}
			if (subName.length() != 0)
			{
				if (aFeat.isStacks() || !aFeat.containsAssociated(subName))
				{
					aFeat.addAssociated(subName);
				}
			}
		}
	}

	public ArrayList featAutoList()
	{
		ArrayList autoFeatList = getStableAutomaticFeatList();
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
			final StringTokenizer aTok = new StringTokenizer(race.getFeatList(), "|", false);
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

				final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
				int i;
				try
				{
					i = Integer.parseInt(aTok.nextToken());
				}
				catch (NumberFormatException exc)
				{
					i = 9999;
				}
				if (i > aClass.getLevel())
				{
					continue;
				}

				String autoFeat = aTok.nextToken();
				final int idx = autoFeat.indexOf('[');
				if (idx >= 0)
				{
					final StringTokenizer bTok = new StringTokenizer(autoFeat.substring(idx + 1), "[]", false);
					ArrayList preReqList = new ArrayList();
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
						if (!aClass.passesPreReqTestsForList(preReqList))
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
				ArrayList templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice());
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
								Globals.debugPrint("no '?' in Domain assocatedList entry: ", aString);
							}
						}
						else
						{
							Globals.debugPrint("Domain associatedList contains: ", aString);
						}
					}
					final String domainFeatList = aDomain.getFeatList();
					if (domainFeatList.length() != 0)
					{
						StringTokenizer aTok = new StringTokenizer(domainFeatList, ",", false);
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

	private void setStableAggregateFeatList(ArrayList aFeatList)
	{
		stableAggregateFeatList = aFeatList;
		setAggregateFeatsStable(aFeatList != null);
	}

	private ArrayList getStableAggregateFeatList()
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

	public ArrayList aggregateFeatList()
	{
		ArrayList aggregate = getStableAggregateFeatList();
		//Did we get a valid list? If so, return it.
		if (aggregate != null)
		{
			return aggregate;
		}
		else
		{
			aggregate = new ArrayList();
		}
		final HashMap aHashMap = new HashMap();
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
				aHashMap.put(autoFeat.getName(), aggregateFeat);
			}
		}

		aggregate = new ArrayList();
		aggregate.addAll(aHashMap.values());
		setStableAggregateFeatList(aggregate);
		return aggregate;
	}

	/**
	 * calculate the total racial modifier to save where
	 *
	 * this includes
	 *   racial boni like the standard halfling's +1 on all saves
	 *   template boni like the forgotten realms lightfoot halfling's +1 on all saves
	 *   racial base modifiers for certain monsters
	 *
	 */
	private int calculateSaveBonusRace(int saveIndex)
	{
		int save = 0;
		if (saveIndex - 1 < 0 || saveIndex - 1 >= Globals.getCheckList().size())
		{
			return 0;
		}
		final String sString = Globals.getCheckList().get(saveIndex - 1).toString();
		save += (int) race.bonusTo("CHECKS", "BASE." + sString) + (int) race.bonusTo("CHECKS", sString);
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
	 * If you use unrecognized terminals, their value will amount to 0.
	 * (This means save.BLABLA equals 0
	 *  whereas save.MAGIC.BLABLA equals save.MAGIC)
	 *
	 * <br>author: Thomas Behr 09-03-02
	 *
	 * @param saveIndex     See the appropriate gamemode file
	 * @param saveType      "CHECK1", "CHECK2", or "CHECK3";
	 *                      may not differ from saveIndex!
	 * @param tokenString       tokenString to parse

	 * @return the calculated save bonus
	 */

	public int calculateSaveBonus(int saveIndex, String saveType, String tokenString)
	{
		final StringTokenizer aTok = new StringTokenizer(tokenString, ".");
		final String[] tokens = new String[aTok.countTokens()];
		final int checkIndex = Globals.getIndexOfCheck(saveType) + 1;
		int save = 0;
		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();
			if ("TOTAL".equals(tokens[i]))
			{
				save += getBonus(checkIndex, true);
			}
			else if ("BASE".equals(tokens[i]))
			{
				save += getBonus(checkIndex, false);
			}
			else if ("MISC".equals(tokens[i]))
			{
				save += (int) getTotalBonusTo("CHECKS", saveType, true);
			}
			if ("MAGIC".equals(tokens[i]))
			{
				save += (int) getEquipmentBonusTo("CHECKS", saveType, true);
			}
			else if ("RACE".equals(tokens[i]))
			{
				save += calculateSaveBonusRace(saveIndex);
			}
			else if ("FEATS".equals(tokens[i]))
			{
				save += (int) getFeatBonusTo("CHECKS", saveType, true);
			}
			/**
			 * exclude stuff
			 */
			else if ("NOMAGIC".equals(tokens[i]))
			{
				save -= (int) getEquipmentBonusTo("CHECKS", saveType, true);
			}
			else if ("NORACE".equals(tokens[i]))
			{
				save -= calculateSaveBonusRace(saveIndex);
			}
			else if ("NOFEATS".equals(tokens[i]))
			{
				save -= (int) getFeatBonusTo("CHECKS", saveType, true);
			}
			else if ("NOSTAT".equals(tokens[i]))
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
	 */
	private int countSpellsInBook(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ".");
		final int classNum = Integer.parseInt(aTok.nextToken());
		final int sbookNum = Integer.parseInt(aTok.nextToken());
		final int levelNum = Integer.parseInt(aTok.nextToken());

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
	 * returns the level of the highest spell in a given spellbook.
	 * Yes, divine casters can have a "spellbook"
	 */
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
	 */
	private int countSpellTimes(String aString)
	{
		boolean found = false;
		final StringTokenizer aTok = new StringTokenizer(aString.substring(10), ".", false);
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
				bookName = Globals.getDefaultSpellBook();

			if (!"".equals(bookName))
			{
				SpellInfo si = null;
				if (classNum == -1)
				{
					final ArrayList charSpellList = new ArrayList();
					for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
					{
						final PCClass aClass = (PCClass) iClass.next();
						final ArrayList bList = aClass.getCharacterSpell(null, bookName, -1);
						for (Iterator bi = bList.iterator(); bi.hasNext();)
						{
							final CharacterSpell cs = (CharacterSpell) bi.next();
							if (!charSpellList.contains(cs))
								charSpellList.add(cs);
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
						si = cs.getSpellInfoFor(bookName, -1, -1);
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
		//
		// Make sure the proficiency is set
		//
		String profName = equip.rawProfName();
		if (profName.length() == 0)
		{
			profName = equip.getName();
		}
		eqm.setProfName(profName);
		//
		// In case this is used somewhere it shouldn't be used, set weight and cost to 0
		//
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
	 * Retrieve the list of weapons in output order.
	 *
	 * @return the sorted list of weapons.
	 */
	public ArrayList getExpandedWeaponsInOutputOrder()
	{
		return sortEquipmentListInOutputOrder(getExpandedWeapons());
	}

	/*
	 */
	public ArrayList getExpandedWeapons()
	{
		final ArrayList weapList = getEquipmentOfType("Weapon", 3);
		//
		// Go through the weapons list. If any weapon is both Melee and Ranged, then make
		// 2 weapons for list, one Melee only, the other Ranged and Thrown.
		// For double weapons, if wielded in both hands show attacks for both heads, head 1, and head 2
		// If wielded in 1 hand, then just show the damage by head.
		//
		for (int idx = 0; idx < weapList.size(); ++idx)
		{
			final Equipment equip = (Equipment) weapList.get(idx);
			if (equip.isDouble())
			{
				Equipment eqm = (Equipment) equip.clone();
				eqm.removeType("Double");
				eqm.setName(appendToName(eqm.getName(), "Head 1 only"));	// Add "Head 1 only" to the name of the weapon
				setProf(equip, eqm);
				weapList.add(idx + 1, eqm);

				eqm = (Equipment) equip.clone();
				String altType = eqm.getType(false);
				if (altType.length() != 0)
				{
					eqm.setTypeInfo(".CLEAR." + altType);
				}
				eqm.removeType("Double");
				eqm.setDamage(eqm.getAltDamage());
				eqm.setCritMult(eqm.getAltCritMult());
				eqm.getEqModifierList(true).clear();
				eqm.getEqModifierList(true).addAll(eqm.getEqModifierList(false));
				eqm.setName(appendToName(eqm.getName(), "Head 2 only"));	// Add "Head 2 only" to the name of the weapon
				setProf(equip, eqm);
				weapList.add(idx + 2, eqm);

				//
				// If not wielding with both hands, then get rid of weapon description in which it is, ie. the 1st one
				//
				if (equip.getLocation() != Equipment.EQUIPPED_BOTH)
				{
					weapList.remove(idx);
				}
				// Don't want to skip items just added in case someone has added Throwing modifier to item
			}

			//
			// Leave else here, as otherwise will show attacks for both heads for thrown double weapons when it should only show one
			//
			else if (equip.isMelee() && equip.isRanged() && (equip.getRange().intValue() != 0))
			{
				//
				// Strip off the Ranged portion, set range to 0
				//
				Equipment eqm = (Equipment) equip.clone();
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
				eqm.setTypeInfo("Ranged.Thrown");
				eqm.removeType("Melee");
				eqm.setName(appendToName(eqm.getName(), "Thrown"));	// Add "Thrown" to the name of the weapon
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
	 *  Evaluates a variable for this character. The result is always numeric.
	 *  e.g. getVariableValue("3+CHA","CLASS:Cleric") for Turn Undead
	 *
	 * @param aString The variable to be evaluated
	 * @param aString The source within which the variable is evaluated
	 * @return The value of the variable
	 */
	public Float getVariableValue(String aString, String src)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		while (aString.lastIndexOf('(') >= 0)
		{
			int x = Utility.innerMostStringStart(aString);
			int y = Utility.innerMostStringEnd(aString);
			if (y < x)
			{
				Globals.errorPrint("Missing closing parenthesis: " + aString);
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
						if (val1.doubleValue() != val2.doubleValue())
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
						Globals.debugPrint("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				if (Globals.isDebugMode())
				{
					Globals.debugPrint("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
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
					if (Globals.isDebugMode())
					{
						Globals.debugPrint(valString + " " + loopVariable + " " + loopValue);
					}
					if ("".equals(loopVariable)) // start the loop
					{
						StringTokenizer lTok = new StringTokenizer(valString, "%:", false);
						loopVariable = lTok.nextToken();
						String vString = loopVariable.toString();
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
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("loopVariable=" + loopVariable + " loopValue=" + loopValue);
					}
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
				if (valString.length() > 0 && Globals.getStatFromAbbrev(valString) > -1)
				{
					valString = Integer.toString(statList.getStatModFor(valString));
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("MOD=", valString);
					}
				}
				else if (valString.length() == 8 && Globals.getStatFromAbbrev(valString.substring(0, 3)) > -1 && valString.endsWith(".BASE"))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("STAT=", valString.substring(0, 3));
					}
					valString = Integer.toString(statList.getBaseStatFor(valString.substring(0, 3)));
					if (Globals.isDebugMode())
					{
						Globals.debugPrint(" BASE=", valString);
					}
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
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("SCORE=", valString);
					}
				}
				else if (("CL".equals(valString) || valString.startsWith("CL;BEFORELEVEL="))&& src.startsWith("CLASS:"))
				{
					valString = getClassLevelString(src.substring(6) + valString.substring(2), false);
				}
				else if (valString.startsWith("CL="))
				{
					valString = getClassLevelString(valString.substring(3), false);
				}
				else if (valString.startsWith("CLASSLEVEL="))
				{
					valString = getClassLevelString(valString.substring(11), true);
				}
				else if (valString.startsWith("CLASS="))
				{
					PCClass aClass = null;
					if (valString.length() > 6)
					{
						aClass = getClassNamed(valString.substring(6));
					}
					else
					{
						Globals.errorPrint("Error! Cannot determine CLASS!");
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
					ArrayList aArrayList = getEquipmentOfType("Shield", 1);
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
					valString = String.valueOf(Globals.getCheckList().size());
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
				else if ("COUNT[FEATS]".equals(valString))
				{
					featList.trimToSize();
					valString = Integer.toString(featList.size()).toString();
				}
				else if ("COUNT[VFEATS]".equals(valString))
				{
					featList.trimToSize();
					valString = Integer.toString(vFeatList().size()).toString();
				}
				else if ("COUNT[FEATSAUTO]".equals(valString))
				{
					featList.trimToSize();
					valString = Integer.toString(featAutoList().size()).toString();
				}
				else if ("COUNT[FEATSALL]".equals(valString))
				{
					valString = Integer.toString(aggregateFeatList().size());
				}
				else if (valString.startsWith("COUNT[FEATTYPE=") && valString.endsWith("]"))
				{
					ArrayList featTypes = Utility.split(valString.substring(15, valString.length() - 1), '.');
					int iCount = 0;
					if (!featList.isEmpty())
					{
						for (Iterator e1 = featList.iterator(); e1.hasNext();)
						{
							final Feat aFeat = (Feat) e1.next();
							for (Iterator e2 = featTypes.iterator(); e2.hasNext();)
							{
								final String featType = (String) e2.next();
								if (aFeat.isType(featType))
								{
									iCount += Math.max(1, aFeat.getAssociatedCount());
									break;
								}
							}
						}
					}
					valString = Integer.toString(iCount);
				}
				else if (valString.startsWith("COUNT[FEATNAME=") && valString.endsWith("]"))
				{
					String featName = valString.substring(15, valString.length() - 1);
					int iCount = 0;
					if (!featList.isEmpty())
					{
						for (Iterator e1 = featList.iterator(); e1.hasNext();)
						{
							final Feat aFeat = (Feat) e1.next();
							if (aFeat.getName().equals(featName))
							{
								iCount += Math.max(1, aFeat.getAssociatedCount());
								break;
							}
						}
					}
					valString = Integer.toString(iCount);
				}
				else if (valString.startsWith("COUNT[SPELLSKNOWN") && valString.endsWith("]"))
				{
					int spellCount = countSpellListBook(valString);
					valString = Integer.toString(spellCount);
				}
				else if (valString.startsWith("COUNT[SPELLSINBOOK") && valString.endsWith("]"))
				{
					valString = valString.substring(18);
					valString = valString.substring(0, valString.length() - 1);
					int sbookCount = countSpellsInBook(valString);
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
//					int count = getSpellClassCount();
//					for (int iii = 0; iii < classList.size(); ++iii)
//					{
//						final PCClass aClass = (PCClass)classList.get(iii);
//						if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
//							++count;
//					}
					valString = String.valueOf(getSpellClassCount());
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
					characterDomainList.trimToSize();
					valString = Integer.toString(characterDomainList.size());
				}
				else if (valString.startsWith("COUNT[EQUIPMENT") && valString.endsWith("]"))
				{
					ArrayList aList = new ArrayList();
					if (!equipmentList.isEmpty())
					{
						for (Iterator e = equipmentList.iterator(); e.hasNext();)
						{
							Equipment eq = (Equipment) e.next();
							if (!eq.getHasHeaderParent())
							{
								aList.add(eq);
							}
						}
					}
					if ("COUNT[EQUIPMENT]".equals(valString))
					{
						valString = Integer.toString(aList.size());
					}
					else
					{
						StringTokenizer bTok = new StringTokenizer(valString.substring(16, valString.length() - 1), ".", false);
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
					ArrayList aList = new ArrayList();
					StringTokenizer bTok = new StringTokenizer(valString.substring(13, valString.length() - 1), ".", false);
					String aType = bTok.nextToken();
					if ("Container".equals(aType))
					{
						aList.clear();
						if (!equipmentList.isEmpty())
						{
							for (Iterator e = equipmentList.iterator(); e.hasNext();)
							{
								Equipment eq = (Equipment) e.next();
								if (eq.getHasHeaderParent() || eq.acceptsChildren())
								{
									aList.add(eq);
								}
							}
						}
					}
					else
					{
						if ("weapon".equalsIgnoreCase(aType))
						{
							aList = getExpandedWeapons();
						}
						else if ("ACITEM".equalsIgnoreCase(aType))
						{
							// special check for ACITEM which is realy anything with AC in the bonus section, but is not type SHIELD or ARMOR
							if (!equipmentList.isEmpty())
							{
								for (Iterator e = equipmentList.iterator(); e.hasNext();)
								{
									Equipment eq = (Equipment) e.next();
									if (((eq.getBonusListString()).indexOf("|AC|") >= 0) && !eq.isArmor() && !eq.isShield())
									{
										aList.add(eq);
									}
								}
							}
						}
						else
						{
							aList = this.getEquipmentOfType(aType, 3);
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

					ArrayList aList = new ArrayList();
					if (!equipmentList.isEmpty())
					{
						aList.clear();
						for (Iterator e = equipmentList.iterator(); e.hasNext();)
						{
							Equipment eq = (Equipment) e.next();
							if (eq.getHasHeaderParent() || eq.acceptsChildren())
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
				else if (valString.startsWith("EQTYPE"))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("PlayerCharacter::getVariableValue(" + aString + "," + src + ") : " + valString + System.getProperty("line.separator"));
					}
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
						double a;
						try
						{
							a = Float.parseFloat(valString);
						}
						catch (NumberFormatException exc)
						{
							a = getTotalBonusTo("VAR", valString, true);
						}
						if (a != 0.0)
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
								;
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
						valString = new Integer(getVariableValue(valString.substring(0, valString.length() - 6), "").intValue()).toString();
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
								Globals.errorPrint("In PlayerCharacter.getVariableValue the mode " + mode + " is unsupported.");
								break;
						}
				}
				catch (NumberFormatException exc)
				{
//					GuiFacade.showMessageDialog(null, "Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")", Globals.s_APPNAME, GuiFacade.ERROR_MESSAGE);
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
	 */
	private void rollStats(int method)
	{
		int roll;
/////////////////////////////////////////////////
// Yanked for WotC compliance
//String diceExpression = SettingsHandler.getRollMethodExpression(method);
/////////////////////////////////////////////////

		for (Iterator stat = statList.getStats().iterator(); stat.hasNext();)
		{
			PCStat currentStat = (PCStat) stat.next();
			currentStat.setBaseScore(0);
			if (SettingsHandler.isPurchaseStatMode())
			{
/////////////////////////////////////////////////
// Yanked for WotC compliance
//				currentStat.setBaseScore(8);
				currentStat.setBaseScore(SettingsHandler.getPurchaseModeBaseStatScore());
/////////////////////////////////////////////////
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
/////////////////////////////////////////////////
// Yanked for WotC compliance
//					roll = RollingMethods.roll(diceExpression);
					roll = 10;
/////////////////////////////////////////////////
					break;
			}
			currentStat.setBaseScore(currentStat.getBaseScore() + roll);
		}
		this.setPoolAmount(0);
		this.costPool = 0;
		languages.clear();
		getAutoLanguages();
		setPoolAmount(0);
	}

	private final boolean includeSkill(Skill skill, int level)
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

	private final void addNewSkills(int level)
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

	private final void removeExcessSkills(int level)
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
	public final void populateSkills(int level)
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
			final ArrayList aArrayList = getEquipmentOfType("Shield", 1);
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
		final int load = Constants.LIGHT_LOAD;
		if (SettingsHandler.isApplyLoadPenaltyToACandSkills())
		{
			Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight());
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
				if (old == 100)
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
			bonus += (int) getTotalBonusTo("MISC", "SPELLFAILURE", true);
		}
		else if (isACCheck)
		{
			bonus = Math.min(bonus, old);
			bonus += (int) getTotalBonusTo("MISC", "ACCHECK", true);
		}
		else if (isMaxDex)
		{
			if (used == 0)
			{
				bonus = 100;
			}
			bonus += (int) getTotalBonusTo("MISC", "MAXDEX", true) - statBonus;
			if (bonus < 0)
			{
				bonus = 0;
			}
			else if (bonus > 100)
			{
				bonus = 100;
			}
		}
		return bonus;
	}

	private double getWeaponProfBonusTo(String aType, String aName)
	{
		double bonus = 0;
		if (hasWeaponProfNamed(aType))
		{
			final WeaponProf wp = Globals.getWeaponProfNamed(aType);
			if (wp != null)
			{
				bonus = wp.bonusTo(aType, aName);
			}
		}
		return bonus;
	}

	private double getArmorProfBonusTo(String aType, String aName)
	{
		double bonus = 0;
		if (hasArmorProfType(aType))
		{
			final ArmorProf ap = Globals.getArmorProfType(aType);
			if (ap != null)
			{
				bonus = ap.bonusTo(aType, aName);
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
			if (eqI.getName().equals(eq.getName()) && eqI.getLocation() == eq.getLocation())
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
			if (eqI.getName().equals(eq.getName()) && eqI.getLocation() == eq.getLocation())
			{
				return true;
			}
		}
		return false;
	}

	private boolean sensitiveCheck()
	{
		boolean foundIt = false;
		final ArrayList aFeatList = aggregateFeatList();
		if (aFeatList.isEmpty())
		{
			return false;
		}

		for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
		{
			if (foundIt)
			{
				break;
			}
			Feat aFeat = (Feat) e1.next();
			if ("Force Sensitive".equals(aFeat.getName()))
			{
				foundIt = true;
			}
		}

		return foundIt;
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

	private ArrayList kitList = null;

	public void addKit(Kit aKit)
	{
		if (kitList == null)
		{
			kitList = new ArrayList();
		}
		kitList.add(aKit);
	}

	public ArrayList getKitInfo()
	{
		return kitList;
	}

	public int getLevelInfoSize()
	{
		return pclevelInfo.size();
	}

	public String getLevelInfoClassKeyName(final int idx)
	{
		if ((idx >= 0) && (idx < getLevelInfoSize()))
		{
			return ((PCLevelInfo) pclevelInfo.get(idx)).getClassKeyName();
		}
		return "";
	}

	public int getLevelInfoClassLevel(int idx)
	{
		final String classKeyName = getLevelInfoClassKeyName(idx);
		int lvl = 1;
		for (int i = 0; i < idx; ++i)
		{
			if (getLevelInfoClassKeyName(i).equals(classKeyName))
			{
				++lvl;
			}
		}
		return lvl;
	}

	public void saveLevelInfo(final String classKeyName)
	{
		PCLevelInfo li = new PCLevelInfo(classKeyName);
		pclevelInfo.add(li);
	}

	public void saveStatIncrease(final String statAbb, final int mod, final boolean isPreMod)
	{
		final int idx = getLevelInfoSize() - 1;
		if (idx >= 0)
		{
			((PCLevelInfo) pclevelInfo.get(idx)).addModifiedStat(statAbb, mod, isPreMod);
		}
	}

	public ArrayList getLevelInfoModifiedStats(final int idx, final boolean preMod)
	{
		if (idx < getLevelInfoSize())
		{
			return ((PCLevelInfo) pclevelInfo.get(idx)).getModifiedStats(preMod);
		}
		return null;
	}

	//
	// Get the value of the desired stat at the point just before the character was
	// raised to the next level
	//
	public int getTotalStatAtLevel(final String statAbb, final int level, final boolean includePost, final boolean includeEquipment)
	{
		int curStat = getStatList().getTotalStatFor(statAbb);
		if (!includeEquipment)
		{
			curStat -= getEquipmentBonusTo("STAT", statAbb, true);
		}
		for (int idx = getLevelInfoSize() - 1; idx >= level; --idx)
		{
			final int statLvlAdjust = ((PCLevelInfo) pclevelInfo.get(idx)).getTotalStatMod(statAbb, includePost);
			curStat -= statLvlAdjust;
		}
		return curStat;
	}

	private boolean removeLevelInfo(final String classKeyName)
	{
		for (int idx = pclevelInfo.size() - 1; idx >= 0; --idx)
		{
			final PCLevelInfo li = (PCLevelInfo) pclevelInfo.get(idx);
			if (li.getClassKeyName().equals(classKeyName))
			{
				removeLevelInfo(idx);
				return true;
			}
		}
		return false;
	}

	private void removeLevelInfo(final int idx)
	{
		pclevelInfo.remove(idx);
	}

	/**
	 * availableSpells
	 * sk4p 13 Dec 2002
	 *
	 * For learning or preparing a spell: Are there slots available at this level or higher?
	 * Fixes BUG [569517]
	 *
	 * @param level            the level being checked for availability
	 *        aClass           the class under consideration
	 *        bookName         the name of the spellbook
	 *        knownLearned     "true" if this is learning a spell, "false" if prepping
	 *        isSpecialtySpell "true" iff this is a specialty for the given class
	 * @return                 true or false, a new spell can be added
	 */

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
			for (; (excNon > 0 && lowExcNon < 0);)
			{
				--excNon;
				++lowExcNon;
			}
			for (; (excSpec > 0 && lowExcSpec < 0);)
			{
				--excSpec;
				++lowExcSpec;
			}

			if (!isDivine || knownLearned)
			{
				// If I'm not divine, I can use non-specialty slots of this level
				// to take up the slack of my excess specialty spells from
				// lower levels.
				for (; (excNon > 0 && lowExcSpec < 0);)
				{
					--excNon;
					++lowExcSpec;
				}

				// And I can use non-specialty slots of this level to take
				// up the slack of my excess specialty spells of this level.
				//
				for (; (excNon > 0 && excSpec < 0);)
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
				lowExcSpec = lowExcSpec + excSpec;
			}
			if (excNon < 0)
			{
				lowExcNon = lowExcNon + excNon;
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
			// casts are possible.  Therefore, it's time to break.
			//
			if ((knownLearned && (knownNon + knownSpec == 0)) || (!knownLearned && knownTot == 0))
			{
				//Globals.debugPrint("Stopping search for spell slots at level " + i);
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
			for (; (excNon > 0 && lowExcNon < 0);)
			{
				--excNon;
				++lowExcNon;
			}
			for (; (excNon > 0 && goodExcNon < 0);)
			{
				--excNon;
				++goodExcNon;
			}
			for (; (excSpec > 0 && lowExcSpec < 0);)
			{
				--excSpec;
				++lowExcSpec;
			}
			for (; (excSpec > 0 && goodExcSpec < 0);)
			{
				--excSpec;
				++goodExcSpec;
			}

			if (!isDivine)
			{
				// If I'm not divine, I can use non-specialty slots of this level
				// to take up the slack of my excess specialty spells from
				// lower levels.
				for (; (excNon > 0 && lowExcSpec < 0);)
				{
					--excNon;
					++lowExcSpec;
				}

				// And also for levels sufficiently high for the spell that got me
				// into this mess, but of lower level than the level currently
				// being calculated.
				for (; (excNon > 0 && goodExcSpec < 0);)
				{
					--excNon;
					++goodExcSpec;
				}

				// And finally use non-specialty slots of this level to take
				// up the slack of excess specialty spells of this level.
				//
				for (; (excNon > 0 && excSpec < 0);)
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
				goodExcSpec = goodExcSpec + excSpec;
			}
			if (excNon < 0)
			{
				goodExcNon = goodExcNon + excNon;
			}

			Globals.debugPrint("Cum. at level " + i + " Spec/Non:" + lowExcSpec + ", " + lowExcNon);
		}

		if (available)
		{
			// I got one.  See above.
			Globals.debugPrint("Found a free slot");
		}
		else
		{
			// I've looped through all levels and still didn't find a suitable slot.
			Globals.debugPrint("Found no suitable slots!");
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

	private final String getClassLevelString(String className, final boolean doReplace)
	{
		final int idx = className.indexOf(";BEFORELEVEL=");
		int lvl = 0;
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
			if (lvl > 0 )
			{
				return getLevelBefore(aClass.getKeyName(), lvl);
			}
			return Integer.toString(aClass.getLevel());
		}
		return "0";
	}

	private final String getLevelBefore(final String classKey, final int charLevel)
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
}

