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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:47:07 $
 *
 */

package pcgen.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.EquipSet;
import pcgen.core.character.Follower;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.io.ExportHandler;
import pcgen.util.GuiFacade;

/**
 * <code>PlayerCharacter</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PlayerCharacter extends Object
{
	//
	// Bryan wanted this to be optional, but if you can reassign
	// racial auto feats, when you reopen the character, you get
	// the feats that were exchanged back
	//
	private boolean canReassignRacialFeats()
	{
		return false;
	}

	private boolean canReassignTemplateFeats()
	{
		return false;
	}

	/* Constants for use in getBonus */
	public static final int ATTACKBONUS = 0;
	public static final int CHECK1BONUS = 1;
	public static final int CHECK2BONUS = 2;
	public static final int CHECK3BONUS = 3;
	public static final int MONKBONUS = 4;
	public int twoHandDamageDivisor = 2;

	private static BigDecimal BIG_ONE = new BigDecimal("1.00");
	private String DPoints = "0";
	private int FPoints = 0;
	/*
	* Used to Force Encumberance to a Given type for BASEMOVEMENT
	*
	* Author: Tim Evans 24-07-02
	*/
	private int iForceLoad = -1;
	/*
	* AC calculations moved to ACCalculator
	*
	* author: Thomas Behr 07-02-02
	*/
	private ACCalculator acCalculator;
	private int age = 0; // in years
	/** Whether one can trust the most recently calculated aggregateFeatList */
	private boolean aggregateFeatsStable = false;
	private int alignment = 9; // 0 = LG to 8 = CE and 9 is <none selected>
	/** whether to add auto known spells each level */
	private boolean autoKnownSpells = true;
	/** Whether one can trust the most recently calculated automaticFeatList */
	private boolean automaticFeatsStable = false;
	private String bio = new String();
	private HashMap bonusMap = new HashMap();
	private boolean canWrite = true; //Doesn't appear to be used.
	private String catchPhrase = "";
	private ArrayList characterDomainList = new ArrayList(); // of CharacterDomain
	private ArrayList classList = new ArrayList(); // of Class
	private ArrayList companionModList = new ArrayList(); // of CompanionMod
	private int costPool = 0;
	private String currentEquipSetName = "";
	private int currentHP = 0;
	private static int decrement;
	private Deity deity = null;
	private String description = new String();
	private String descriptionLst = "EMPTY";
	private boolean dirtyFlag = false;
	private ArrayList equipSetList = new ArrayList(); // of Equip Sets
	private ArrayList equipmentList = new ArrayList(); // of Equipment
	private int earnedXP = 0;
	private String eyeColor = "";
	private TreeSet favoredClasses = new TreeSet();
	private ArrayList featList = new ArrayList(); // of Feat
	private int feats = 0; // pool of feats remaining to distribute
	/** This may be different from character name... */
	private String fileName = "";
	private ArrayList followerList = new ArrayList(); // of Followers
	private Follower followerMaster = null; // Who is the master now?
	private int freeLangs = 0;
	private String gender = "M";
	private BigDecimal gold = new BigDecimal("0.00");
	private String hairColor = "";
	private String hairStyle = "";
	private String handed = "Right";
	private int height = 0; // in inches
	private boolean importing = false;
	private static int loopValue = 0;
	private String interests = "";
	private TreeSet languages = new TreeSet();
	private static String lastVariable = null;
	private String location = "";
	private static String loopVariable = "";
	private ArrayList miscList = new ArrayList(3);
	private String[] movementTypes;
	private Integer[] movements;
	private String name = new String();
	private int nonProficiencyPenalty = -4;
	private ArrayList notesList = new ArrayList(); // of Notes
	private String phobias = "";
	private String playersName = new String();
	private int poolAmount = 0; // pool of stats remaining to distribute
	/** This may be different from file name... */
	private String portraitPath = "";
	private ArrayList primaryWeapons = new ArrayList();
	private ArrayList qualifyArrayList = new ArrayList();
	private boolean qualifyListStable = false;
	private Race race = null;
	private String racialFavoredClass = "";
	private int remainingPool = 0;
	private String residence = "";
	private ArrayList secondaryWeapons = new ArrayList();
	private ArrayList skillList = new ArrayList(); // of Skill
	private int skillPoints = 0; // pool of skills remaining to distribute
	private String skinColor = "";
	/** Collections of String (probably should be full objects) */
	private ArrayList specialAbilityList = new ArrayList();
	private String speechTendency = "";
	private ArrayList spellBooks = new ArrayList();
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
	private ArrayList templateAutoLanguages = new ArrayList();
	private TreeSet templateLanguages = new TreeSet();
	private ArrayList templateList = new ArrayList(); // of Template
	private String trait1 = "";
	private String trait2 = "";
	private boolean useMonsterDefault = SettingsHandler.isMonsterDefault();
	private ArrayList variableList = new ArrayList();
	/** Whether one can trust the most recently calculated virtualFeatList */
	private boolean virtualFeatsStable = false;
	private TreeSet weaponProfList = new TreeSet();
	private TreeSet armorProfList = new TreeSet();
	private int weight = 0; // in pounds
	private StatList statList = new StatList();

	public int doOffhandMod(int myMod)
	{
		if (((myMod / twoHandDamageDivisor) * twoHandDamageDivisor) == myMod)
		{
			return ((myMod / twoHandDamageDivisor) + getTotalBonusTo("COMBAT", "SECONDARYDAMAGE", true));
		}
		else
		{
			if (getTotalBonusTo("COMBAT", "SECONDARYDAMAGE", true) == 0)
				return (myMod / twoHandDamageDivisor);
			else
				return ((myMod / twoHandDamageDivisor) + getTotalBonusTo("COMBAT", "SECONDARYDAMAGE", true) + 1);
		}
	}

	public boolean isMonsterDefault()
	{
		return useMonsterDefault;
	}

	public boolean isAggregateFeatsStable()
	{
		return automaticFeatsStable && aggregateFeatsStable;
	}

	public void setAggregateFeatsStable(boolean stable)
	{
		aggregateFeatsStable = stable;
	}

	public boolean isVirtualFeatsStable()
	{
		return virtualFeatsStable;
	}

	public void setVirtualFeatsStable(boolean stable)
	{
		virtualFeatsStable = stable;
	}

	public boolean isAutomaticFeatsStable()
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
	 * Selector
	 *
	 * @return AC calculator
	 */
	public ACCalculator getACCalculator()
	{
		return acCalculator;
	}

	public void setNonProficiencyPenalty(int npp)
	{
		if (npp <= 0)
			nonProficiencyPenalty = npp;
	}

	/**
	 * @return nonProficiencyPenalty. Searches templates first.
	 */
	public int getNonProficiencyPenalty()
	{
		int npp = -4;
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
			npp = aTemplate.getNonProficiencyPenalty();
		}
		Globals.debugPrint("NPP: " + npp + " nonProficiencyPenalty: " + nonProficiencyPenalty);
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
		TreeSet wp = new TreeSet(weaponProfList);
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
			final PCClass aClass = (PCClass)e.next();
			aClass.addSelectedWeaponProfBonusTo(wp);
		}

		// Add any selected template bonus weapons
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
			aTemplate.addSelectedWeaponProfBonusTo(wp);
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain)e.next();
				final Domain aDomain = aCD.getDomain();
				if (aDomain != null)
				{
					aDomain.addSelectedWeaponProfBonusTo(wp);
				}
			}
		}
		return wp;
	}

	public TreeSet getArmorProfList()
	{
		TreeSet ap = new TreeSet(armorProfList);
		//
		// Add character's armor proficiency types
		//
		String temptype = "";
		if (!featList.isEmpty())
		{
			for (Iterator e1 = featList.iterator(); e1.hasNext();)
			{
				final String myFeatName = ((Feat)e1.next()).getName();
				if (myFeatName.startsWith("Armor Proficiency "))
				{
					final int idxbegin = myFeatName.indexOf("(");
					final int idxend = myFeatName.indexOf(")");
					temptype = myFeatName.substring((idxbegin + 1), idxend);
					ap.add(temptype);
				}
			}
		}
		return ap;
	}

	public boolean hasWeaponProfNamed(String aName)
	{
		for (Iterator i = getWeaponProfList().iterator(); i.hasNext();)
		{
			if (aName.equalsIgnoreCase((String)i.next()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasArmorProfType(String aName)
	{
		for (Iterator i = getArmorProfList().iterator(); i.hasNext();)
		{
			if (aName.equalsIgnoreCase((String)i.next()))
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

	public boolean checkQualifyList(String qualifierItem)
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
	public ArrayList getQualifyList()
	{
		if (!qualifyListStable)
		{
			qualifyArrayList = new ArrayList();
			for (int i = 0, x = templateList.size(); i < x; ++i)
			{
				final PCTemplate template = (PCTemplate)templateList.get(i);
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

	public int getRawFPoints()
	{
		return FPoints;
	}

	public String getStrFPoints()
	{
		int fpoints = FPoints;
		for (Iterator e = featAutoList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
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

	public void setFPoints(int fpoints)
	{
		if (!sensitiveCheck() && (fpoints > 5))
		{
			fpoints = 5;
		}
		FPoints = fpoints;
		Globals.debugPrint("ForcePoints:", FPoints);
		setDirty(true);
	}

	public String getDPoints()
	{
		return DPoints;
	}

	public void setDPoints(String aString)
	{
		DPoints = new String(aString);
		Globals.debugPrint("Darkside Points:", DPoints);
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

	public ArrayList getEquipmentList()
	{
		return equipmentList;
	}

	public void addEquipment(Equipment eq)
	{
		equipmentList.add(eq);
	}

	public void delEquipment(Equipment eq)
	{
		equipmentList.remove(eq);
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

	/**
	 * Sorts the provided list of euipment in output order. This is in
	 * ascending order of the equipment's outputIndex field. If multiple
	 * items of equipment have the same outputIndex they will be ordered by
	 * name. Note hidden items (outputIndex = -1) are not included in list.
	 *
	 * @param unsortedEquipList An ArrayList of the equipment to be sorted.
	 * @return An ArrayList of the equipment objects in output order.
	 */
	private ArrayList sortEquipmentListInOutputOrder(ArrayList unsortedEquipList)
	{
		ArrayList sortedList = (ArrayList)unsortedEquipList.clone();
		Collections.sort(sortedList, new Comparator()
		{
			// Comparator will be specific to Skill objects
			public int compare(Object obj1, Object obj2)
			{
				int obj1Index = ((Equipment)obj1).getOutputIndex();
				int obj2Index = ((Equipment)obj2).getOutputIndex();
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
					return ((Equipment)obj1).getName().compareTo(((Equipment)obj2).getName());
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
			final Equipment item = (Equipment)i.next();
			if (item.getOutputIndex() == -1)
			{
				i.remove();
			}
		}

		return sortedList;
	}

	public Equipment getEquipmentNamed(String aString)
	{
		if (equipmentList.isEmpty())
			return null;
		Equipment match = null;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
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
			EquipSet eSet = (EquipSet)e.next();
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
			EquipSet eSet = (EquipSet)e.next();
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
		String pid = eSet.getIdPath();

		// first remove this EquipSet
		equipSetList.remove(eSet);

		// now find and remove all it's children
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet)e.next();
			if (es.getParentIdPath().startsWith(pid))
			{
				e.remove();
				found = true;
			}
		}
		return found;
	}

	public ArrayList getCompanionModList()
	{
		return companionModList;
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
			PlayerCharacter nPC = (PlayerCharacter)p.next();
			if (followerMaster.getFileName().equals(nPC.getFileName()))
			{
				return nPC;
			}
		}
		// could not find a filename match, let's try the Name
		for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
		{
			PlayerCharacter nPC = (PlayerCharacter)p.next();
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
		PlayerCharacter mPC = getMasterPC();
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
			PCClass mClass = (PCClass)c.next();
			if (mClass.getName().equalsIgnoreCase("WIZARD") || mClass.getName().equalsIgnoreCase("SORCERER"))
			{
				mTotalLevel++;
			}
		}
		//System.out.println("setMaster:fTL:" + mTotalLevel);

		for (Iterator cm = Globals.getCompanionModList().iterator(); cm.hasNext();)
		{
			CompanionMod aComp = (CompanionMod)cm.next();
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
				PCClass mClass = (PCClass)c.next();
				int mLev = mClass.getLevel().intValue();
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
		PCClass newClass = null;

		if ((newRaceType != null) && (newRaceType.length() > 0) && isFamiliarOrMount(followerMaster))
		{
			newClass = Globals.getClassNamed(newRaceType);
			race.setType(newRaceType);
			setDirty(true);
			// we now have to swap all the old "Race" levels
			// for the new ones
			PCClass oldClass = getClassNamed(oldRaceType);
			int oldLevel = 0;
			if (oldClass != null)
			{
				oldLevel = oldClass.getLevel().intValue();
			}
			if ((oldLevel > 0) && (newClass != null))
			{
				Globals.errorPrint("setMaster:oldClass:" + oldClass.getName() + "(" + oldLevel + ")");
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
		if (followerMaster.getType().equalsIgnoreCase("FAMILIAR"))
		{
			ArrayList mList = mPC.getSkillList();
			ArrayList sNameList = new ArrayList();
			// now we have to merge the two lists together and
			// take the higher rank of each skill for the Familiar
			for (Iterator a = getSkillList().iterator(); a.hasNext();)
			{
				Skill fSkill = (Skill)a.next();
				for (Iterator b = mList.iterator(); b.hasNext();)
				{
					Skill mSkill = (Skill)b.next();
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
				String skillName = (String)sn.next();
				// familiar doesn't have skill,
				// but master does, so add it
				Skill newSkill = (Skill)Globals.getSkillNamed(skillName).clone();
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
			final PCTemplate template = (PCTemplate)templateList.get(i);
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
		String region = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate)templateList.get(i);
			final String tempRegion = template.getRegion();
			if (!tempRegion.equals(Constants.s_NONE))
			{
				region = tempRegion;
			}
		}
		return region;
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
		String subregion = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate)templateList.get(i);
			final String tempSubRegion = template.getSubRegion();
			if (!tempSubRegion.equals(Constants.s_NONE))
			{
				subregion = tempSubRegion;
			}
		}
		return subregion;
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
		String subregion = getSubRegion();
		StringBuffer tempRegName = new StringBuffer().append(getRegion());

		if (!subregion.equals(Constants.s_NONE))
		{
			tempRegName.append(" (").append(subregion).append(")");
		}
		return tempRegName.toString();
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up height
	 *
	 * @return character Height from templates
	 */
	public String findTemplateHeight()
	{
		String tHeight = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate)templateList.get(i);
			final String tempTHeight = template.getHeightString();

			if (!tempTHeight.equals(Constants.s_NONE))
			{
				tHeight = tempTHeight;
			}
		}
		return tHeight;
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up height
	 *
	 * @return character Weight from templates
	 */
	public String findTemplateWeight()
	{
		String tWeight = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate)templateList.get(i);
			final String tempTWeight = template.getWeightString();

			if (!tempTWeight.equals(Constants.s_NONE))
			{
				tWeight = tempTWeight;
			}
		}
		return tWeight;
	}

	/**
	 * Selector
	 *
	 * Build on-the-fly so removing templates won't mess up height
	 *
	 * @return character Age from templates
	 */
	public String findTemplateAge()
	{
		String tAge = Constants.s_NONE;
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			final PCTemplate template = (PCTemplate)templateList.get(i);
			final String tempTAge = template.getAgeString();

			if (!tempTAge.equals(Constants.s_NONE))
			{
				tAge = tempTAge;
			}
		}
		return tAge;
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
		dirtyFlag = dirtyState;
		// Some items enable/disable depending on PC dirtiness
		Globals.getRootFrame().enableMenuItems();
	}

	/** Gets whether the character has been changed since last saved. */
	public boolean wasEverSaved()
	{
		return !getFileName().equals("");
	}

	/** @return true if character is currently being read from file. */
	public boolean getIsImporting()
	{
		return importing;
	}

	/** @return true if character is currently being read from file. */
	public boolean isImporting()
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

	public String getDisplayRaceName()
	{
		String name = getRace().toString();
		return (name.equals(Constants.s_NONESELECTED) ? "Nothing" : name);
	}

	public String getDisplayClassName()
	{
		final ArrayList classList = getClassList();
		return (classList.isEmpty() ? "Nobody" : ((PCClass)classList.get(classList.size() - 1)).getDisplayClassName());
	}

	public String getDisplayName()
	{
		String custom = getTabName();

		if (!custom.equals(""))
		{
			return custom;
		}

		StringBuffer name = new StringBuffer().append(getName());

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
				name.append(" the ").append(getDisplayRaceName()).append(" ").append(getDisplayClassName());
				break;
			default:
				break; // custom broken
		}

		return name.toString();
	}

	public String getFullDisplayName()
	{
		return new StringBuffer().append(getName()).append(" the ").append(getDisplayRaceName()).append(" ").append(getDisplayClassName()).toString();
	}

	public String getTabName()
	{
		return tabName;
	}

	public void setTabName(String aString)
	{
		tabName = aString;
		setDirty(true);
		Globals.getRootFrame().forceUpdate_PlayerTabs();
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
	public int getInitialFeats()
	{
		int initFeats = getRace().getBonusInitialFeats();
		ArrayList aList = getTemplateList();

		if (!aList.isEmpty() && canReassignTemplateFeats())
		{
			for (Iterator e = aList.iterator(); e.hasNext();)
			{
				final PCTemplate template = (PCTemplate)e.next();

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

	public int getCurrentHP()
	{
		return currentHP;
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

	public int getRemainingPool()
	{
		return remainingPool;
	}

	public void setRemainingPool(int pool)
	{
		remainingPool = pool;
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

	public String findTemplateGender()
	{
		String templateGender = Constants.s_NONE;
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
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
		String tGender = findTemplateGender();

		if (tGender.equals(Constants.s_NONE))
			return gender;
		else
			return tGender;
	}

	public void setGender(String argGender)
	{
		String templateGender = findTemplateGender();
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

	public void addSpecialAbilityToList(SpecialAbility sa)
	{
		specialAbilityList.add(sa);
	}

	public ArrayList getSpecialAbilityList()
	{
		// aList will contain a list of SpecialAbility objects
		ArrayList aList = (ArrayList)specialAbilityList.clone();
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
			PCClass aClass = (PCClass)i.next();
			aList = aClass.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			aList = aFeat.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			aList = aSkill.addSpecialAbilitiesToList(aList);
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)i.next();
			if (aCD.getDomain() != null)
				aList = aCD.getDomain().addSpecialAbilitiesToList(aList);
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				aList = eq.addSpecialAbilitiesToList(aList);

				ArrayList bList = eq.getEqModifierList(true);
				if (!bList.isEmpty())
				{
					for (Iterator e2 = bList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						aList = eqMod.addSpecialAbilitiesToList(aList);
					}
				}
				bList = eq.getEqModifierList(false);
				if (!bList.isEmpty())
				{
					for (Iterator e2 = bList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						aList = eqMod.addSpecialAbilitiesToList(aList);
					}
				}
			}
		}
		int atl = getTotalLevels();
		int thd = totalHitDice();
		String sz = getSize();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)i.next();
			PCTemplate bTemplate = Globals.getTemplateNamed(aTemplate.getName());
			aList = bTemplate.addSpecialAbilitiesToList(aList, atl, thd, sz);
		}
		for (Iterator i = companionModList.iterator(); i.hasNext();)
		{
			CompanionMod cMod = (CompanionMod)i.next();
			aList = cMod.addSpecialAbilitiesToList(aList);
		}
		Collections.sort(aList);
		return aList;
	}

	public ArrayList getSpecialAbilityListStrings()
	{
		ArrayList aList = getSpecialAbilityList();
		ArrayList bList = new ArrayList();
		if (!aList.isEmpty())
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final Object obj = i.next();
				final SpecialAbility sa = (SpecialAbility)obj;
				if (sa.getDesc() == null || sa.getDesc().equals(""))
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
		ArrayList aList = new ArrayList();
		ArrayList abilityList = getSpecialAbilityListStrings();
		int[] times = new int[abilityList.size()];
		Arrays.fill(times, 0);
		if (!abilityList.isEmpty())
		{
			for (Iterator i = abilityList.iterator(); i.hasNext();)
			{
				String aString = (String)i.next();
				boolean found = false;
				int idx = 0;
				for (Iterator ii = aList.iterator(); ii.hasNext();)
				{
					if (aString.equals(ii.next()))
					{
						found = true;
						break;
					}
					idx++;
				}
				if (!found)
					aList.add(aString);
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
			StringTokenizer varTok = new StringTokenizer((String)aList.get(i), "|", false);
			String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();
			if (varCount != 0)
			{
				varValue = new int[varCount];
				for (int j = 0; j < varCount; ++j)
				{
					final String vString = varTok.nextToken();
					varValue[j] = getVariable(vString, true, true, "", "").intValue();
				}
			}

			StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;
			boolean isZero = false;
			while (varTok.hasMoreTokens())
			{
				final String nextTok = varTok.nextToken();
				if (nextTok.equals("%"))
				{
					if (varCount == 0)
					{
						isZero = true;
					}
					if ((varValue != null) && (varCount < varValue.length))
					{
						final int thisVar = varValue[varCount++];
						isZero &= (thisVar == 0);
						newAbility.append(Integer.toString(thisVar));
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
			if (times[i] > 1)
			{
				newAbility.append(" (").append(Integer.toString(times[i])).append(')');
			}
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
		TreeSet bSet = new TreeSet();
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

	public Integer getEarnedXP()
	{
		return new Integer(earnedXP);
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

	public void setEarnedXP(int earnedXP)
	{
		this.earnedXP = earnedXP;
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
	 * @param  level  character level to get max skill ranks for
	 * @return        The maximum allowed skill ranks
	 */
	public static BigDecimal maxClassSkillForLevel(int level)
	{
		ArrayList levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo)levelInfo.get(level - 1)).getMaxClassSkillRanks();
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
		ArrayList levelInfo = Globals.getLevelInfo();
		if (level > 0 && level <= levelInfo.size())
		{
			return ((LevelInfo)levelInfo.get(level - 1)).getMaxCrossClassSkillRanks();
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
				final Feat aFeat = (Feat)e.next();
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

	public ArrayList getCharacterDomainList()
	{
		int num = getTotalBonusTo("DOMAIN", "NUMBER", false);
		String dsource = "";
		if (!characterDomainList.isEmpty())
		{
			for (Iterator e = characterDomainList.iterator(); e.hasNext();)
			{
				CharacterDomain aCD = (CharacterDomain)e.next();
				dsource = aCD.getDomainSource();
				if (dsource.length() > 1)
				{
					break;
				}
			}
		}
		while (characterDomainList != null && dsource.length() > 0 && characterDomainList.size() < num)
		{
			CharacterDomain aCD = new CharacterDomain();
			aCD.setDomainSource(dsource);
			characterDomainList.add(aCD);
		}
		while (characterDomainList != null && num < characterDomainList.size())
		{
			int i = getFirstEmptyCharacterDomain();
			if (i >= 0)
			{
				characterDomainList.remove(i);
			}
			else
			{
				Globals.errorPrint("WARNING:Total domains should be " + num + "!!!");
				break;
			}
		}
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
			return skillList;
		for (Iterator i = Globals.getSkillList().iterator(); i.hasNext();)
		{
			final Skill aSkill = (Skill)i.next();
			if (!hasSkill(aSkill.getName()))
			{
				if (getTotalBonusTo("SKILLRANK", aSkill.getName(), true) != 0)
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
		ArrayList sortedList = (ArrayList)getSkillList().clone();
		Collections.sort(sortedList, new Comparator()
		{
			/**
			 *  Comparator will be specific to Skill objects
 			 */
			public int compare(Object obj1, Object obj2)
			{
				int obj1Index = ((Skill)obj1).getOutputIndex();
				int obj2Index = ((Skill)obj2).getOutputIndex();
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
					return ((Skill)obj1).getName().compareTo(((Skill)obj2).getName());
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
			final Skill bSkill = (Skill)i.next();
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
			statList.getStats().add(((PCStat)Globals.getStatList().get(i)).clone());
		}
		setRace((Race)Globals.getRaceMap().get(Constants.s_NONESELECTED));
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
			bonusMap.put((String)i.next(), "x");
		}

	/*
	* here we produce a double reference
	* gc will not/cannot remove such objects
	* in order to prevent memory leaks,
	* we introduce a dispose method,
	* which must be called manually!
	*
	* author: Thomas Behr 07-02-02
	*/
		acCalculator = new ACCalculator(this);
		twoHandDamageDivisor = getVariableValue("TWOHANDDAMAGEDIVISOR", "").intValue();
	}

	/**
	 * freeing up resources
	 **/
	public void dispose()
	{
		acCalculator.dispose();
		acCalculator = null;
	}

	public String getCritterType()
	{
		StringBuffer critterType = new StringBuffer();

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
				PCTemplate aTemplate = (PCTemplate)e.next();
				String aType = aTemplate.getType();

				if (!aType.equals(""))
				{
					critterType.append("|").append(aType);
				}
			}
		}

		return critterType.toString();
	}

	public String getSize()
	{
		String size = Constants.s_SIZESHORT[sizeInt()];
		return size;
	}

	public int racialSizeInt()
	{
		int iSize = Constants.SIZE_M;
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
					final PCTemplate template = (PCTemplate)e.next();
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
			// Now check and see if a class has modiefied
			// the size of the character
			// with something like BONUS:SIZEMOD|NUMBER|+1
			iSize += getTotalBonusTo("SIZEMOD", "NUMBER", true);

			// Now see if there is a HD advancement in size
			// (Such as for Dragons)

			// This is tricky -- it is unclear if this can
			// be optimized to eliminate all the calls to
			// test.  XXX
			for (int i = 0; i < race.sizesAdvanced(totalHitDice()); ++i)
			{
				iSize += 1;
			}

			//
			// Must still be between 0 and 8
			//
			if (iSize < 0)
			{
				iSize = 0;
			}
			if (iSize >= Constants.s_SIZESHORT.length)
			{
				iSize = Constants.s_SIZESHORT.length - 1;
			}
		}
		return iSize;
	}

	public int sizeIntForSize(String key)
	{
		return Globals.sizeInt(key);
	}

	public int getModifierForSize(String modName)
	{
		ArrayList aList = new ArrayList();
		aList.add("size");
		//
		// check for BONUS:ESIZE|NUMBER|-1 to see if we should
		// decrease or increase AC and HIDE modifiers
		// sizeMod can be either positive or negative number
		//
		int sizeMod = getTotalBonusTo("ESIZE", "NUMBER", true);
		if ((modName.equals("ACMod") || modName.equals("HideMod")) && (sizeMod != 0))
		{
			int iSize = sizeInt() + sizeMod;
			//
			// iSize must not be smaller than 0 or larger than 8
			//
			if (iSize < 0)
			{
				iSize = 0;
			}
			else if (iSize > 8)
			{
				iSize = 8;
			}
			if (iSize >= Constants.s_SIZESHORT.length)
			{
				iSize = Constants.s_SIZESHORT.length - 1;
			}
			String eSize = Constants.s_SIZESHORT[iSize];
			return (int)Globals.sizeAdjustmentMultiplier(eSize, aList, modName);
		}
		else
		{
			return (int)Globals.sizeAdjustmentMultiplier(getSize(), aList, modName);
		}
	}

	public int modForSize()
	{
		return getModifierForSize("ACMod");
	}

	public int grappleModForSize()
	{
		return getModifierForSize("GrappleMod");
	}

	private int hideModForSize()
	{
		return getModifierForSize("HideMod");
	}

	public int getModifierForSizeIncrease(String modName)
	{
		ArrayList aList = new ArrayList();
		aList.add("size");
		int rSize = racialSizeInt();
		int iSize = sizeInt();
		int mod = 0;

		for (int i = rSize; i < iSize; ++i)
		{
			mod += (int)Globals.sizeAdjustmentMultiplier(Constants.s_SIZESHORT[i], aList, modName);
		}

		return mod;
	}

/*
	Unused. Will remove the next time I come across them. JK020825

	public int strModForSize()
	{
		return getModifierForSizeIncrease("StrIncrease");
	}

	private int conModForSize()
	{
		return getModifierForSizeIncrease("ConIncrease");
	}

	private int dexModForSize()
	{
		return getModifierForSizeIncrease("DexIncrease");
	}
  */

	public int naturalArmorModForSize()
	{
		return getModifierForSizeIncrease("NaturalACIncrease");
	}

	public PCClass getClassNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
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
			final PCClass aClass = (PCClass)e.next();
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
			final PCClass aClass = (PCClass)classIter.next();
			if (aClass.getKeyName().equals(aString))
			{
				return aClass;
			}
		}
		return null;
	}

	public PCTemplate getTemplateKeyed(String aString)
	{
		for (Iterator templateIter = templateList.iterator(); templateIter.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)templateIter.next();
			if (aTemplate.getKeyName().equals(aString))
			{
				return aTemplate;
			}
		}
		return null;
	}

	public PCTemplate getTemplateNamed(String aName)
	{
		for (Iterator templateIter = templateList.iterator(); templateIter.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)templateIter.next();
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
			aObject = (PObject)classIter.next();
			if (!aObject.getCharacterSpell(null, "", -1).isEmpty())
			{
				ix--;
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

	public int getSpellClassCount()
	{
		int x = 0;
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PObject aObject = (PObject)classIter.next();
			if (!aObject.getCharacterSpell(null, "", -1).isEmpty())
			{
				x++;
			}
		}
		if (!race.getCharacterSpell(null, "", -1).isEmpty())
		{
			return x++;
		}
		return x;
	}

	public void addVariable(String variableString)
	{
		variableList.add(variableString);
	}

	public void removeVariable(String variableString)
	{
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			final String aString = (String)e.next();
			if (aString.startsWith(variableString))
			{
				e.remove();
			}
		}
	}

	public boolean hasVariable(String variableString)
	{
		if (!variableList.isEmpty())
		{
			for (Iterator e = variableList.iterator(); e.hasNext();)
			{
				final StringTokenizer aTok = new StringTokenizer((String)e.next(), "|", false);
				aTok.nextToken(); //src
				aTok.nextToken(); //subSrc
				if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
				{
					return true;
				}
			}
		}

		if (!featList.isEmpty())
		{
			for (Iterator e = featList.iterator(); e.hasNext();)
			{
				final Feat obj = (Feat)e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					String featVariable = obj.getVariable(i);
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
				final Skill obj = (Skill)e.next();
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
				final Equipment obj = (Equipment)e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					String equipmentVariable = obj.getVariable(i);
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
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						for (int i = 0, x = eqMod.getVariableCount(); i < x; ++i)
						{
							String eqModVariable = obj.getVariable(i);
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
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						for (int i = 0, x = eqMod.getVariableCount(); i < x; ++i)
						{
							String eqModVariable = eqMod.getVariable(i);
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
				final PCTemplate obj = (PCTemplate)e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					String templateVariable = obj.getVariable(i);
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
				final CompanionMod obj = (CompanionMod)e.next();
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					String companionVariable = obj.getVariable(i);
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
				String deityVariable = deity.getVariable(i);
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
				CharacterDomain cd = (CharacterDomain)e.next();
				if (cd.getDomain() != null)
				{
					for (int i = 0, x = cd.getDomain().getVariableCount(); i < x; ++i)
					{
						String domainVariable = cd.getDomain().getVariable(i);
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
				String raceVariable = race.getVariable(i);
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
				final String wpName = (String)e.next();
				final WeaponProf obj = Globals.getWeaponProfNamed(wpName);
				if (obj == null)
				{
					Globals.debugPrint("No weapon prof: ", wpName);
					continue;
				}
				for (int i = 0, x = obj.getVariableCount(); i < x; ++i)
				{
					String weaponProfVariable = obj.getVariable(i);
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
			final PCStat aStat = (PCStat)e.next();
			for (int i = 0, x = aStat.getVariableCount(); i < x; ++i)
			{
				String aVariable = aStat.getVariable(i);
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
				final String vString = (String)e.next();
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
				if (nString.equals(variableString))
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
					if (!loopVariable.equals(""))
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
		if (!featList.isEmpty())
		{
			for (Iterator oi = featList.iterator(); oi.hasNext();)
			{
				final Feat obj = (Feat)oi.next();
				String varInList = checkForVariableInList(obj, variableString, isMax, "", "", found, value);
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
				final Skill obj = (Skill)oi.next();
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
				final Equipment obj = (Equipment)oi.next();
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
						final EquipmentModifier em = (EquipmentModifier)el.next();
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
						final EquipmentModifier em = (EquipmentModifier)el.next();
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
				final PCTemplate obj = (PCTemplate)oi.next();
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
				final CompanionMod obj = (CompanionMod)oi.next();
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
			String aString = checkForVariableInList(race, variableString, isMax, "", "", found, value);
			if (aString.length() > 0)
			{
				found = true;
				value = Float.parseFloat(aString);
			}
		}

		if (deity != null)
		{
			String aString = checkForVariableInList(deity, variableString, isMax, "", "", found, value);
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
				final CharacterDomain obj = (CharacterDomain)oi.next();
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
				final WeaponProf obj = Globals.getWeaponProfNamed((String)oi.next());
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
			final PCStat obj = (PCStat)e.next();
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
			int i = getTotalBonusTo("VAR", variableString, true);
			value = value + i;
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

			if (nString.equals(variableString))
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
				if (!loopVariable.equals(""))
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
	public ArrayList removeEqType(ArrayList aList, String aString)
	{
		ArrayList aArrayList = new ArrayList();
		Equipment eq = null;
		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment)mapIter.next();
			if (!eq.typeStringContains(aString))
			{
				aArrayList.add(eq);
			}
		}
		return aArrayList;
	}

	/*
	 */
	public ArrayList removeNotEqType(ArrayList aList, String aString)
	{
		ArrayList aArrayList = new ArrayList();
		Equipment eq = null;
		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment)mapIter.next();
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
			Equipment eq = (Equipment)e.next();
			if (eq.typeStringContains(aString))
				aList.add(eq);
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

	public Integer getMovement(int moveIdx)
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
	public Integer getMovementOfType(String moveType)
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

		if (iLoad == 0)
		{
			for (Iterator c = classList.iterator(); c.hasNext();)
			{
				PCClass aClass = (PCClass)c.next();
				// this movement is cumulative
				move += Integer.parseInt(aClass.getMoveForLevel(aClass.getLevel().intValue()));
			}
		}
		if (!Globals.isStarWarsMode() && iLoad >= 0)
		{
			move = Globals.calcEncumberedMove(iLoad, move);
		}
		iForceLoad = iLoad;
		move += getTotalBonusTo("MOVE", "TYPE=" + getMovementType(moveIdx).toUpperCase(), true);
		iForceLoad = -1;
		// always get following bonus
		move += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false);
		switch (iLoad)
		{
			// NOTE: no breaks on purpose!
			// These are cumulative and cascade together!!!!!
			case Constants.LIGHT_LOAD:
				move += getTotalBonusTo("MOVE", "LIGHT", false);
				//No break
			case Constants.MEDIUM_LOAD:
				move += getTotalBonusTo("MOVE", "LIGHTMEDIUM", false);
				//No break
			case Constants.HEAVY_LOAD:
				move += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false);
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
		ArrayList aArrayList = getEquipmentOfType("Armor", 1);

		int pcLoad = Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight());

		// If no load or armor, just get class movement rates
		if (aArrayList.size() == 0 && pcLoad == 0)
		{
			for (Iterator c = classList.iterator(); c.hasNext();)
			{
				PCClass aClass = (PCClass)c.next();
				// this movement is cumulative
				move += Integer.parseInt(aClass.getMoveForLevel(aClass.getLevel().intValue()));
			}
		}
		else
		{
			// pcLoad will equal the greater of
			// encumberance load type or armor type
			for (Iterator a = aArrayList.iterator(); a.hasNext();)
			{
				Equipment armor = (Equipment)a.next();
				if (armor.isHeavy())
					pcLoad = Math.max(pcLoad, Constants.HEAVY_LOAD);
				else if (armor.isMedium())
					pcLoad = Math.max(pcLoad, Constants.MEDIUM_LOAD);
				else if (armor.isLight())
					pcLoad = Math.max(pcLoad, Constants.LIGHT_LOAD);

				if (Globals.isStarWarsMode())
				{
					final String armorMoveString = armor.moveString();
					final int pos = armorMoveString.lastIndexOf(",");
					final boolean isMedium = getRace().getSize().equals(Constants.s_SIZESHORT[Constants.SIZE_M]);
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

		move += getTotalBonusTo("MOVE", "TYPE=" + getMovementType(moveIdx).toUpperCase(), true);
		// always get following bonus
		bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false);
		switch (pcLoad)
		{
			// NOTE: no breaks on purpose!
			// These are cumulative and cascade together!!!!!
			case Constants.LIGHT_LOAD:
				bonus += getTotalBonusTo("MOVE", "LIGHT", false);
				//No break
			case Constants.MEDIUM_LOAD:
				bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUM", false);
				//No break
			case Constants.HEAVY_LOAD:
				bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false);
				break;
			default:
				Globals.errorPrint("In PlayerCharacter.movement the load constant " + pcLoad + " is not handled.");
				break;
		}
		move += bonus;
		return move;
	}

	/**
	 * returns the base AC due to selected race
	 *
	 * @return the base AC
	 */
	public int baseAC()
	{
		/*
		* naturalArmorModForSize() does not belong to base AC,
		* it is a natural armor bonus after all
		*
		* author: Thomas Behr 07-02-02
		*/
		return getRace().getStartingAC().intValue();
	}

	/**
	 * returns the total AC
	 *
	 * @return the total AC
	 */
	public int totalAC()
	{
		return acCalculator.calculateACBonusTotal();
	}

	/**
	 * returns the flat footed AC
	 *
	 * @return the flat footed AC
	 */
	public int flatFootedAC()
	{
		return acCalculator.calculateACBonusFlatFooted();
	}

	/**
	 * returns the touch AC
	 *
	 * @return the touch AC
	 */
	public int touchAC()
	{
		/*
                 * PHB, p.119 says
                 * "... [touch] AC does not include any armor bonus,
                 *  shield bonus, or natural armor bonus."
                 */
		return acCalculator.calculateACBonusTouch();
	}

	public int initiativeMod()
	{
		//int initmod = getTotalBonusTo("COMBAT", "Initiative", true);
		final int initmod = getTotalBonusTo("COMBAT", "Initiative", true) + getVariableValue("INITCOMP", "").intValue();
		return initmod;
	}

	public int getNumAttacks(){
		return (int) Math.min(Math.max(baseAttackBonus()/5,4),1);
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
		PlayerCharacter nPC = getMasterPC();
		if ((nPC != null) && followerMaster.getType().equalsIgnoreCase("FAMILIAR"))
		{
			masterBAB = nPC.baseAttackBonus();
			masterTotal = nPC.baseAttackBonus() + bonus;
		}
		//

		ArrayList ab = new ArrayList(10);
		int total = 0;
		// modForSize() removed as it doesn't add to number of attacks
		int mod = getTotalBonusTo("TOHIT", "TOHIT", false) + bonus + getRace().getBAB();
		int attacks = 1;
		int subTotal = getRace().getBAB();
		int maxCycle = 0;
		StringBuffer attackString = new StringBuffer();
		for (total = 0; total < 10; ++total)
		{
			ab.add(new Integer(0));
		}
		total = 0;
		int combat = getTotalBonusTo("COMBAT", "TOHIT", true);
		int nonTotal = getRace().getBAB();

		for (int i = 0, x = classList.size(); i < x; ++i)
		{
			final PCClass aClass = (PCClass)classList.get(i);
			final int b = aClass.baseAttackBonus();
			final int c = aClass.attackCycle(index);
			final int d = ((Integer)ab.get(c)).intValue() + b;
			maxCycle = Math.max(maxCycle, d);
			ab.set(c, new Integer(d));
			mod += b;
			subTotal += b;
			if (c != 3)
			{
				nonTotal += b;
			}
		}
		for (int i = 2; i < 10; ++i)
		{
			if (((Integer)ab.get(i)).intValue() > ((Integer)ab.get(attacks)).intValue())
			{
				attacks = i;
			}
		}
		total = ((Integer)ab.get(attacks)).intValue();
		if (total == 0)
		{
			attacks = 5;
		}
		// FAMILIAR: check to see if the masters BAB is better
		mod = Math.max(mod, masterTotal);
		subTotal = Math.max(subTotal, masterBAB);
		nonTotal = Math.max(nonTotal, masterBAB);

		if (attacks != 5)
		{
			if (total / attacks < subTotal / 5)
			{
				attacks = 5;
				total = subTotal;
			}
			else
			{
				mod -= nonTotal;
				subTotal -= nonTotal;
			}
		}
		while (attackString.length() == 0 || total > 0 || subTotal > 0)
		{
			if (attackString.length() > 0)
			{
				attackString.append('/');
			}
			if (mod > -1)
			{
				attackString.append('+');
			}

			attackString.append(mod + combat);
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
	private String getBestUDamString(String oldString, String newString)
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
			int i = Integer.parseInt(aTok.nextToken());
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
			final PCClass aClass = (PCClass)e.next();
			retString = getBestUDamString(retString, aClass.getUdamForLevel(aClass.getLevel().intValue(), includeCrit, includeStrBonus));
		}
		retString = getBestUDamString(retString, race.getUdamFor(includeCrit, includeStrBonus));
		if (deity != null)
		{
			retString = getBestUDamString(retString, deity.getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			retString = getBestUDamString(retString, aFeat.getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			retString = getBestUDamString(retString, aSkill.getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)i.next();
			if (aCD.getDomain() != null)
				retString = getBestUDamString(retString, aCD.getDomain().getUdamFor(includeCrit, includeStrBonus));
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				retString = getBestUDamString(retString, eq.getUdamFor(includeCrit, includeStrBonus));

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						retString = getBestUDamString(retString, eqMod.getUdamFor(includeCrit, includeStrBonus));
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						retString = getBestUDamString(retString, eqMod.getUdamFor(includeCrit, includeStrBonus));
					}
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)i.next();
			retString = getBestUDamString(retString, aTemplate.getUdamFor(includeCrit, includeStrBonus));
		}
		// string is in form sides|damage, just return damage portion
		return retString.substring(retString.indexOf("|") + 1);
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
			if ((bLoading) && (index != Constants.ALIGNMENT_NONE))
			{
				GuiFacade.showMessageDialog(null, "Invalid alignment. Setting to <none selected>", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				alignment = Constants.ALIGNMENT_NONE;
			}
			//TODO raise an exception, once I define one. Maybe
			//ArrayIndexOutOfBounds?
		}
	}

	public boolean setMyMoveRates(String moveType, int moveRatei, int moveFlag)
	{

		if (moveType.equals("ALL"))
		{
			if (moveFlag == 0)
			{ // set all types of movement to moveRate
				Integer moveRate = new Integer(moveRatei);
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
				Integer moveRate = new Integer(moveRatei);
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
				String[] movementTypesTemp = movementTypes;
				Integer[] movementsTemp = movements;

				movements = new Integer[movementsTemp.length + 1];
				movementTypes = new String[movementTypesTemp.length + 1];

				for (int i = 0, x = movementsTemp.length; i < x; ++i)
				{
					movements[i] = movementsTemp[i];
					movementTypes[i] = movementTypesTemp[i];
				}
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
				Integer moveRate = new Integer(moveRatei + movements[0].intValue());
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
				String[] movementTypesTemp = movementTypes;
				Integer[] movementsTemp = movements;

				movements = new Integer[movementsTemp.length + 1];
				movementTypes = new String[movementTypesTemp.length + 1];

				for (int i = 0, x = movementsTemp.length; i < x; ++i)
				{
					movements[i] = movementsTemp[i];
					movementTypes[i] = movementTypesTemp[i];
				}
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

		movements = (Integer[])movements.clone();
		movementTypes = (String[])getRace().getMovementTypes().clone();

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
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
							if (movementMultOp[i].startsWith("*"))
								setMyMoveRates(templateMovementTypes[i], getMovementOfType(templateMovementTypes[0]).intValue() * movementsMult[i].intValue(), 0);
							else if (movementMultOp[i].startsWith("/"))
								setMyMoveRates(templateMovementTypes[i], getMovementOfType(templateMovementTypes[0]).intValue() / movementsMult[i].intValue(), 0);
						}
						else
						{
							if (movementMultOp[i].startsWith("*"))
								setMyMoveRates(templateMovementTypes[i], getMovement(0).intValue() * movementsMult[i].intValue(), 0);
							else if (movementMultOp[i].startsWith("/"))
								setMyMoveRates(templateMovementTypes[i], getMovement(0).intValue() / movementsMult[i].intValue(), 0);
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
			return visMap;
		for (Iterator i = aMap.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final String aVal = aMap.get(aKey).toString();
			final Object bObj = visMap.get(aKey);
			int b = 0;
			if (bObj != null)
				Integer.parseInt(bObj.toString());
			int a = getVariableValue(aVal, "").intValue();
			if (a >= b)
				visMap.put(aKey, String.valueOf(a));
		}
		return visMap;
	}

	public String getVision()
	{
		HashMap visMap = new HashMap();
		visMap = addStringToVisionMap(visMap, race.getVision());
		if (deity != null)
			visMap = addStringToVisionMap(visMap, deity.getVision());
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass)i.next();
			visMap = addStringToVisionMap(visMap, aClass.getVision());
		}
		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			visMap = addStringToVisionMap(visMap, aFeat.getVision());
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			visMap = addStringToVisionMap(visMap, aSkill.getVision());
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)i.next();
			if (aCD.getDomain() != null)
				visMap = addStringToVisionMap(visMap, aCD.getDomain().getVision());
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				visMap = addStringToVisionMap(visMap, eq.getVision());

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						visMap = addStringToVisionMap(visMap, eqMod.getVision());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						visMap = addStringToVisionMap(visMap, eqMod.getVision());
					}
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)i.next();
			visMap = addStringToVisionMap(visMap, aTemplate.getVision());
		}

		StringBuffer vision = new StringBuffer();
		for (Iterator i = visMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			Object bObj = visMap.get(aKey);
			if (bObj == null)
			{
				Globals.errorPrint("huh?");
				continue;
			}
			int val = Integer.parseInt(bObj.toString()) + getTotalBonusTo("VISION", aKey, true);
			if (vision.length() > 0)
			{
				vision.append(",");
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
			ArrayList templateFeats = inTmpl.feats(getTotalLevels(), totalHitDice());
			for (int i = 0, x = templateFeats.size(); i < x; ++i)
			{
				modFeatsFromList((String)templateFeats.get(i), true, false);
			}
		}
		else
		{
			setAutomaticFeatsStable(false);
		}

		ArrayList templates = inTmpl.getTemplates(importing);
		for (int i = 0, x = templates.size(); i < x; ++i)
		{
			addTemplate(Globals.getTemplateNamed((String)templates.get(i)));
		}
		setQualifyListStable(false);
		adjustMoveRates();
		if (!importing)
		{
			getSpellList();
		}
	}

	public void removeTemplate(PCTemplate inTmpl)
	{
		if (inTmpl == null)
			return;

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
			removeTemplate(getTemplateNamed((String)inTmpl.templatesAdded().get(i)));
		}
		for (int i = 0; i < templateList.size(); ++i)
		{
			if (((PCTemplate)templateList.get(i)).getName().equals(inTmpl.getName()))
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

	private void incrementClassLevel(int mod, PCClass aClass, boolean bSilent)
	{
		if (!importing)
			getSpellList();
		if (mod > 0)
		{
			if (!aClass.isQualified())
			{
				return;
			}
			if (aClass.isMonster() && !race.isAdvancementUnlimited() && totalHitDice() >= race.maxHitDiceAdvancement() && !bSilent)
			{
				GuiFacade.showMessageDialog(null, "Cannot increase Monster Hit Dice for this character beyond " + race.maxHitDiceAdvancement() + ". This characters current number of Monster Hit Dice is " + totalHitDice(), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return;
			}
		}

		PCClass bClass = getClassNamed(aClass.getName());
		if (bClass == null && mod >= 0)
		{
			// add the class even if setting to level 0
			bClass = (PCClass)aClass.clone();
			classList.add(bClass);
			// do the following only if adding a level
			if (mod > 0)
			{
				Set aSet = bClass.getAutoLanguages();
				languages.addAll(aSet);
			}
		}
		if (bClass == null)
		{
			return;
		}

		if (mod > 0)
		{
			for (int i = 0; i < mod; ++i)
			{
				bClass.addLevel(false, bSilent);
			}
		}
		else if (mod < 0)
		{
			for (int i = 0; i < -mod; ++i)
			{
				bClass.subLevel(bSilent);
			}
		}
		if (canReassignTemplateFeats())
		{
			for (int i = 0, x = templateList.size(); i < x; ++i)
			{
				PCTemplate aTemplate = (PCTemplate)templateList.get(i);
				ArrayList templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice());

				for (int j = 0, y = templateFeats.size(); j < y; ++j)
				{
					modFeatsFromList((String)templateFeats.get(j), true, false);
				}
			}
		}
		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
	}

	private void rebuildLists(PCClass toClass, PCClass fromClass, int iCount)
	{
		int fromLevel = fromClass.getLevel().intValue();
		int toLevel = toClass.getLevel().intValue();
		for (int i = 0; i < iCount; ++i)
		{
			fromClass.doMinusLevelMods(this, fromLevel - i);
			toClass.doPlusLevelMods(toLevel + i + 1);
		}
	}

	public void giveClassesAway(String toClass, String fromClass, int iCount)
	{
		giveClassesAway(getClassNamed(toClass), getClassNamed(fromClass), iCount);
	}

	public void giveClassesAway(PCClass toClass, PCClass fromClass, int iCount)
	{
		if ((toClass == null) || (fromClass == null))
			return;

		//
		// Will take destination class over maximum?
		//
		if (toClass.getLevel().intValue() + iCount > toClass.getMaxLevel())
		{
			iCount = toClass.getMaxLevel() - toClass.getLevel().intValue();
		}

		//
		// Enough levels to move?
		//
		if ((fromClass.getLevel().intValue() <= iCount) || (iCount < 1))
		{
			return;
		}

		int iOldLevel = toClass.getLevel().intValue();
		toClass.setLevel(new Integer(iOldLevel + iCount));
		for (int i = 0; i < iCount; ++i)
		{
			toClass.setHitPoint(iOldLevel + i, fromClass.getHitPointList(fromClass.getLevel().intValue() - i - 1));
			fromClass.setHitPoint(fromClass.getLevel().intValue() - i - 1, new Integer(0));
		}

		rebuildLists(toClass, fromClass, iCount);

		fromClass.setLevel(new Integer(fromClass.getLevel().intValue() - iCount));
	}

	public void makeIntoExClass(PCClass aClass)
	{
		String exClass = aClass.getExClass();
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

				bClass = (PCClass)bClass.clone();

				rebuildLists(bClass, aClass, aClass.getLevel().intValue());

				bClass.setLevel(aClass.getLevel());
				bClass.setHitPointList(aClass.getHitPointList());
				int idx = classList.indexOf(aClass);
				classList.set(idx, bClass);
			}
			else
			{
				rebuildLists(bClass, aClass, aClass.getLevel().intValue());
				bClass.setLevel(new Integer(bClass.getLevel().intValue() + aClass.getLevel().intValue()));
				for (int i = 0; i < aClass.getLevel().intValue(); ++i)
				{
					bClass.setHitPoint(bClass.getLevel().intValue() + i + 1, aClass.getHitPointList(i + 1));
				}
				classList.remove(aClass);
			}


			//
			// Find all skills associated with old class and link them to new class
			//
			for (Iterator e = getSkillList().iterator(); e.hasNext();)
			{
				Skill aSkill = (Skill)e.next();
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
		Skill aSkill = null;
		//
		// First, check to see if skill is already in list
		//
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			aSkill = (Skill)e.next();
			if (aSkill.getKeyName().equals(addSkill.getKeyName()))
			{
				return aSkill;
			}
		}

		//
		// Skill not found, add to list
		//
		aSkill = (Skill)addSkill.clone();
		getSkillList().add(aSkill);
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
		BigDecimal maxRanks = new BigDecimal(0.0);

		if (SettingsHandler.isMonsterDefault())
		{
			levelForSkillPurposes += totalHitDice();
		}

		Skill aSkill = Globals.getSkillNamed(skillName);
		if (aSkill.isExclusive().startsWith("Y"))
		{
			// Exclusive skills only count levels in classes which give access to the skill
			levelForSkillPurposes = 0;
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				final PCClass bClass = (PCClass)e.next();
				if (aSkill.isClassSkill(bClass))
				{
					levelForSkillPurposes += bClass.getLevel().intValue();
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
		maxRanks = maxRanks.add(new BigDecimal(getTotalBonusTo("SKILLMAXRANK", skillName, true)));
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
	public ArrayList getAvailableFeatNames(String featType)
	{
		ArrayList aFeatList = new ArrayList();
		final ArrayList globalFeatList = Globals.getFeatList();
		final int globalFeatListSize = globalFeatList.size();
		for (int index = 0; index < globalFeatListSize; ++index)
		{
			final Feat aFeat = (Feat)globalFeatList.get(index);
			if (aFeat.matchesType(featType) &&
			  qualifiesForFeat(aFeat.getKeyName()) &&
			  ((!hasFeat(aFeat.getName()) && !hasFeatAutomatic(aFeat.getName())) ||
			  aFeat.isMultiples()))
			{
				aFeatList.add(aFeat.getKeyName());
			}
		}
		return aFeatList;
	}

	public int hitPoints()
	{
		int iConMod = getStatBonusTo("HP", "BONUS");

		int total = 0;
		if (race.hitDice() != 0)
		{
			total = race.calcHitPoints(iConMod);
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			total += aClass.hitPoints(iConMod);
		}
		total += getTotalBonusTo("HP", "CURRENTMAX", true);
		//
		// now we see if this PC is a Familiar
		PlayerCharacter nPC = getMasterPC();
		if (nPC == null)
		{
			return total;
		}
		if (!followerMaster.getType().equalsIgnoreCase("FAMILIAR"))
		{
			return total;
		}
		// It seems it is, so calculate the correct HP
		return (int)Math.floor(nPC.hitPoints() / 2);
	}

	public boolean isNonability(int i)
	{
		if (race.isNonability(i))
		{
			return true;
		}
		for (int x = 0; x < templateList.size(); ++x)
		{
			if (((PCTemplate)templateList.get(x)).isNonAbility(i))
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
						final String featName = aString.substring(0, aString.indexOf("(") - 1);

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
				removeTemplate(getTemplateNamed((String)race.templatesAdded().get(x)));
			}
			if (race.getMonsterClass() != null && race.getMonsterClassLevels() != 0)
			{
				PCClass mclass = Globals.getClassNamed(race.getMonsterClass());
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
			race = (Race)aRace.clone();
		}
		if (race != null)
		{
			//
			// If user has chosen a class before choosing a race,
			// we need to tweak the number of skill points and feats
			//
			if (!importing && ((oldRace == null) || (oldRace.getName().equals(Globals.s_EMPTYRACE.getName()))) && (classList.size() != 0))
			{
				setFeats(feats + race.getBonusInitialFeats());
				int bonusSkillPoints = race.getBonusSkillsPerLevel();
				int spMod = 0;
				for (int i = 0; i < classList.size(); ++i)
				{
					final PCClass aClass = (PCClass)classList.get(i);
					int skillPoints = 0;
					if (i == 0)
					{
						skillPoints = (race.getInitialSkillMultiplier() - 1) * bonusSkillPoints;
						race.rollAgeForAgeSet(aClass.getAgeSet());
					}
					skillPoints += aClass.getLevel().intValue() * bonusSkillPoints;
					spMod += skillPoints;
					aClass.setSkillPool(new Integer(aClass.getSkillPool().intValue() + skillPoints));
				}
				setSkillPoints(getSkillPoints() + spMod);
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
						final String featName = aString.substring(0, aString.indexOf("(") - 1);

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

			if (!importing && !dirtyFlag)
			{
				race.rollHeightWeight();
			}
			ArrayList templates = race.getTemplates(importing);
			for (int x = 0; x < templates.size(); ++x)
			{
				addTemplate(Globals.getTemplateNamed((String)templates.get(x)));
			}
			if (!importing && race.getMonsterClass() != null && race.getMonsterClassLevels() != 0)
			{
				PCClass mclass = Globals.getClassNamed(race.getMonsterClass());
				if (mclass != null)
				{
					incrementClassLevel(race.getMonsterClassLevels(), mclass, true);
				}
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
			PCClass aClass = (PCClass)i.next();
			addSpells(aClass);
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			addSpells(aFeat);
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			addSpells(aSkill);
		}
		// Domains are skipped - it's assumed that their spells are added to the first divine spellcasting

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				addSpells(eq);

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						addSpells(eqMod);
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						addSpells(eqMod);
					}
				}
			}
		}
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)i.next();
			addSpells(aTemplate);
		}
	}

	public void addSpells(PObject obj)
	{
		if (race == null || obj == null || obj.getSpellList() == null || obj.getSpellList().isEmpty())
			return;
		for (Iterator ri = obj.getSpellList().iterator(); ri.hasNext();)
		{
			// spellname|times|book|PRExxx|PRExxx|etc
			String spellLine = ri.next().toString();
			final StringTokenizer aTok = new StringTokenizer(spellLine, "|", false);
			String spellName = aTok.nextToken();
			Spell aSpell = Globals.getSpellNamed(spellName);
			if (aSpell == null)
			{
				return;
			}
			int times = Integer.parseInt(aTok.nextToken());
			String book = aTok.nextToken();
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
			ArrayList sList = race.getCharacterSpell(aSpell, book, -1);
			if (!sList.isEmpty())
			{
				continue;
			}
			CharacterSpell cs = new CharacterSpell(race, aSpell);
			cs.addInfo(0, times, book);
			addSpellBook(book);
			race.addCharacterSpell(cs);
		}
	}

	public int getTotalLevels()
	{
		int totalLevels = 0;

		totalLevels += totalPCLevels();
		totalLevels += totalNPCLevels();
		totalLevels += totalMonsterLevels();

		return totalLevels;
	}

	/**
	 * Check the DMG text on monster PCs -- you don't count their
	 * race towards their XP, you count their level adjustment.
	 */
	public int getECL()
	{
		int totalLevels = 0;

		totalLevels += totalPCLevels();
		totalLevels += totalNPCLevels();
		totalLevels += getLevelAdjustment();

		return totalLevels;
	}

      //removed for OGL/d20 compliance
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
			final PCClass aClass = (PCClass)e.next();
			if (aClass.isPC())
			{
				totalLevels += aClass.getLevel().intValue();
			}
		}
		return totalLevels;
	}

	public int totalNPCLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			if (aClass.isNPC())
			{
				totalLevels += aClass.getLevel().intValue();
			}
		}
		return totalLevels;
	}

	public int totalMonsterLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			if (aClass.isMonster())
			{
				totalLevels += aClass.getLevel().intValue();
			}
		}
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod)e.next();
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
			float hitDieRatio = (float)totalHitDice() / rhd;
			while (hitDieRatio >= 2)
			{
				CR += 2;
				hitDieRatio /= 2;
			}
			if (hitDieRatio >= 1.5)
			{
				CR += 1;
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
					CR += 1;
				}
				else
				{
					CR += NPCLevels - 1;
				}
			}
		}

		for (int x = 0; x < templateList.size(); ++x)
		{
			CR += ((PCTemplate)templateList.get(x)).getCR(getTotalLevels(), totalHitDice());
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
			CompanionMod cMod = (CompanionMod)i.next();
			SR = Math.max(SR, cMod.getSR());
		}
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			PCClass aClass = (PCClass)i.next();
			SR = Math.max(SR, aClass.getSR());
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			SR = Math.max(SR, aFeat.getSR());
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			SR = Math.max(SR, aSkill.getSR());
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)i.next();
			if (aCD.getDomain() != null)
				SR = Math.max(aCD.getDomain().getSR(), SR);
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				SR = Math.max(SR, eq.getSR());

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						SR = Math.max(SR, eqMod.getSR());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						SR = Math.max(SR, eqMod.getSR());
					}
				}
			}
		}
		int atl = getTotalLevels();
		int thd = totalHitDice();
		String sz = getSize();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)i.next();
			SR = Math.max(SR, aTemplate.getSR(atl, thd));
		}
		SR += getTotalBonusTo("MISC", "SR", true);
		return SR;
	}

	private HashMap addStringToDRMap(HashMap drMap, String drString)
	{
		if (drString == null || drString.length() == 0)
		{
			return drMap;
		}
		StringTokenizer aTok = new StringTokenizer(drString, "|", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			int x = aString.indexOf("/");
			String key = "";
			String val = "";
			if (x > 0 && x < aString.length())
			{
				val = aString.substring(0, x);
				key = aString.substring(x + 1);
				// some DR: are DR:val/key and others are DR:val/+key,
				// so remove the + to make them equivalent
				if (key.startsWith("+"))
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
			PCClass aClass = (PCClass)i.next();
			drMap = addStringToDRMap(drMap, aClass.getDR());
		}
		for (Iterator i = aggregateFeatList().iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			drMap = addStringToDRMap(drMap, aFeat.getDR());
		}
		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			drMap = addStringToDRMap(drMap, aSkill.getDR());
		}
		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)i.next();
			if (aCD.getDomain() != null)
				drMap = addStringToDRMap(drMap, aCD.getDomain().getDR());
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				drMap = addStringToDRMap(drMap, eq.getDR());

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						drMap = addStringToDRMap(drMap, eqMod.getDR());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						drMap = addStringToDRMap(drMap, eqMod.getDR());
					}
				}
			}
		}
		int atl = getTotalLevels();
		int thd = totalHitDice();
		String sz = getSize();
		for (Iterator i = templateList.iterator(); i.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)i.next();
			drMap = addStringToDRMap(drMap, aTemplate.getDR(atl, thd));
		}
		StringBuffer DR = new StringBuffer();
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
				symbol = "+";
			protectionValue += getTotalBonusTo("DR", damageType, true);
			if (DR.length() > 0)
				DR.append(";");
			DR.append(symbol).append(damageType).append("/").append(protectionValue);
		}
		return DR.toString();
	}

	public int getLevelAdjustment()
	{
		int levelAdj = race.getLevelAdjustment();
		for (int i = 0, x = templateList.size(); i < x; ++i)
		{
			levelAdj += ((PCTemplate)templateList.get(i)).getLevelAdjustment();
		}
		return levelAdj;
	}

	public String classString(boolean abbreviations)
	{
		StringBuffer classStringBuffer = new StringBuffer(classList.size() * 7);
		int x = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)(e.next());
			if (aClass.getLevel().intValue() > 0)
			{
				if (x != 0)
				{
					classStringBuffer.append(" ");
				}
				x++;
				if (abbreviations)
				{
					classStringBuffer = classStringBuffer.append(aClass.getAbbrev()).append(" ").append(aClass.getLevel().toString());
				}
				else
				{
					classStringBuffer = classStringBuffer.append(aClass.classLevelString());
				}
			}
		}
		return classStringBuffer.toString();
	}

	/**
	 * Check if the character has the named Deity.
	 *
	 * @param deityName String name of the deity to check for.
	 * @return  <code>true</code> if the character has the Deity,
	 *          <code>false</code> otherwise.
	 */
	public boolean hasDeity(String deityName)
	{
		ArrayList aList = new ArrayList();
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
	 * Check if the character has the feat at all (i.e. 'for real', 'virtually', or 'automatically')
	 *
	 * @param featName String name of the feat to check for.
	 * @return  <code>true</code> if the character has the feat,
	 *          <code>false</code> otherwise.
	 */
	public boolean hasFeatAtAll(String featName)
	{
		if (hasFeat(featName) || hasFeatAutomatic(featName) || hasFeatVirtual(featName))
		{
			return true;
		}
		else
		{
			return false;
		}
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

	private Feat getFeatNamedInList(ArrayList aFeatList, String featName)
	{
		return getFeatNamedInList(aFeatList, featName, -1);
	}

	private Feat getFeatNamedInList(ArrayList aFeatList, String featName, int featType)
	{
		if (aFeatList.isEmpty())
		{
			return null;
		}
		for (Iterator e = aFeatList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
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
		ArrayList aList = aggregateFeatList();
		if (aList.isEmpty())
		{
			return null;
		}
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.getKeyName().equals(featName))
			{
				return aFeat;
			}
		}
		return null;
	}

	public boolean qualifiesForFeat(String featName)
	{
		final Feat aFeat = Globals.getFeatNamed(featName);
		if (aFeat != null)
			return qualifiesForFeat(aFeat);
		return false;
	}

	public boolean qualifiesForFeat(Feat aFeat)
	{
		return aFeat.canBeSelectedBy();
	}

	public boolean isSpellCaster(int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass)e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel().intValue() >= minLevel)
				return true;
		}
		return false;
	}

	public boolean isSpellCaster(String spellType, int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass)e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel().intValue() >= minLevel && aClass.getSpellType().equalsIgnoreCase(spellType))
				return true;
		}
		return false;
	}

	public boolean isSpellCastermax(int maxLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass)e1.next();
			if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE) && aClass.getLevel().intValue() <= maxLevel)
				return true;
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
			getSpellList();
		int retVal = addIt ? 1 : 0;
		String subName = "";
		//
		// See if our choice is not auto or virtual
		//
		Feat aFeat = getFeatNonAggregateNamed(featName);

		String oldName = featName;
		//
		// if a feat named featName doesn't exist, and featName contains a (blah) descriptor, try removing it.
		//
		if ((aFeat == null) && featName.endsWith(")"))
		{
			int idx = featName.indexOf("(");
			subName = featName.substring(idx + 1, featName.lastIndexOf(")")); //we want what is inside the outermost parens.
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
				aFeat = (Feat)aFeat.clone();
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
		int j = (int)(aFeat.getAssociatedCount() * aFeat.getCost()) + feats;
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
				if (subName.equals(""))
				{
					// Allow sub-choices
					canSetFeats = !aFeat.modChoices(addIt);
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
				if (aFeat.getChoiceString().lastIndexOf("|") > -1 &&
				  Globals.weaponTypesContains(aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf("|"))))
				{
					addWeaponProfToList(featList, aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf("|")), false);
				}
			}
		}

		if (aFeat.isMultiples() && !addAll)
		{
			retVal = (aFeat.getAssociatedCount() > 0)? 1 : 0;
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
					final Feat myFeat = (Feat)e1.next();
					if (myFeat.getName().equals(aFeat.getName()))
					{
						associatedListSize = myFeat.getAssociatedCount();
					}
				}
			}

			j -= (int)(associatedListSize * aFeat.getCost());
		}

		if (!addAll && canSetFeats)
		{
			setFeats(j);
		}

		setAutomaticFeatsStable(false);

		return retVal;
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 */
	public void modFeatsFromList(String aList, boolean addIt, boolean all)
	{
		/*
                 * This is messing up races and templates.
                 * arcady 1/17/2001
                 */
//                  if (getTotalLevels() == 0)
//                  {
//                          featList.clear();
//                          return;
//                  }

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
					final int endIndex = aString.lastIndexOf(")");
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
						aFeat = (Feat)aFeat.clone();
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
				aFeat = (Feat)aFeat.clone();
				addFeat(aFeat);
			}
			if (bTok != null && bTok.hasMoreTokens())
			{
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
					if (aString.equals("DEITYWEAPON"))
					{
						WeaponProf wp = null;
						if (getDeity() != null)
							wp = Globals.getWeaponProfNamed(getDeity().getFavoredWeapon());
						if (wp != null)
						{
							if (addIt)
								aFeat.addAssociated(wp.getName());
							else
								aFeat.removeAssociated(wp.getName());
						}
					}
					else
					{
						if (addIt)
							aFeat.addAssociated(aString);
						else
							aFeat.removeAssociated(aString);
					}
				}
			}
			else
			{
				if (!all && !aFeat.isMultiples())
				{
					if (addIt)
						setFeats(getFeats() + 1);
					else
						setFeats(getFeats() - 1);
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

	public boolean hasSkill(String skillName)
	{
		return (getSkillNamed(skillName) != null);
	}

	public Skill getSkillNamed(String skillName)
	{
		if (getSkillList().isEmpty())
			return null;
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			if (aSkill.getName().equalsIgnoreCase(skillName))
				return aSkill;
		}
		return null;
	}

	public Skill getSkillKeyed(String skillName)
	{
		if (getSkillList().isEmpty())
			return null;
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			if (aSkill.getKeyName().equals(skillName))
				return aSkill;
		}
		return null;
	}

	/**
	 * type 0 = attack bonus; 1 = check1; 2 = check2; 3 = check3; etc, last one is = Monk
	 */
	public int getBonus(int type, boolean addBonuses)
	{
		int bonus = 0;
		if (type == 0)
			bonus = race.getBAB();
		else if (type <= Globals.getCheckList().size())
		{
			bonus = race.bonusTo("CHECKS", "BASE." + Globals.getCheckList().get(type - 1).toString());
		}
		if (addBonuses)
		{
			if (type == 0)
			{
				bonus += getTotalBonusTo("TOHIT", "TOHIT", true);
//				bonus += getFeatBonusTo("TOHIT", "TOHIT", false);
//				bonus += getRace().bonusTo("TOHIT", "TOHIT");
//				bonus += getEquipmentBonusTo("TOHIT", "TOHIT", false);
//				bonus += getTemplateBonusTo("TOHIT", "TOHIT", false);
//				bonus += getCompanionModBonusTo("TOHIT", "TOHIT");
				bonus += modForSize();
			}
			else if (type <= Globals.getCheckList().size())
			{
				bonus += getTotalBonusTo("CHECKS", Globals.getCheckList().get(type - 1).toString(), true);
			}
			else
				bonus += modForSize();
		}
		int cBonus = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (type == 0 || type > Globals.getCheckList().size())
			{
				cBonus += aClass.baseAttackBonus();
			}
			else
			{
				cBonus += aClass.checkBonus(type - 1, true);
			}
		}
		int masterBonus = 0;
		// now we see if this PC is a Familiar/Mount
		PlayerCharacter nPC = getMasterPC();
		if (type > 0 && type <= Globals.getCheckList().size() && (nPC != null) && isFamiliarOrMount(followerMaster))
		{
			// It seems to be, so calculate the Masters Save Bonus
			masterBonus = nPC.calculateSaveBonus(type, Globals.getCheckList().get(type - 1).toString(), "BASE");
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
	private boolean isFamiliarOrMount(Follower followerMaster)
	{
		final String type = followerMaster.getType();
		return type.equalsIgnoreCase("FAMILIAR") ||
				  type.equalsIgnoreCase("SPECIAL MOUNT") ||
				  type.equalsIgnoreCase("FIENDISH SERVANT");
	}

	public int baseAttackBonus()
	{
		return getTotalBonusTo("COMBAT", "BAB", true) + getTotalBonusTo("TOHIT", "TOHIT", true);
/*		int bonus = getRace().getBAB();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			bonus += aClass.baseAttackBonus();
		}
		return bonus;
*/
	}

	public void ValidateCharacterDomains()
	{
		if (!importing)
			getSpellList();
		for (int i = characterDomainList.size() - 1; i >= 0; i--)
		{
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			if (!aCD.isDomainValidFor(this))
				characterDomainList.remove(aCD);
		}
	}

	/** return -1 if CharacterDomain containing requested domainName is not found,
	 *  otherwise return the index of that CharacterDomain in the characterDomainList
	 */
	public int getCharacterDomainIndex(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
				return i;
		}
		return -1;
	}

	public Domain getCharacterDomainNamed(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
				return aCD.getDomain();
		}
		return null;
	}

	public CharacterDomain getCharacterDomainForDomain(String domainName)
	{
		for (int i = 0; i < characterDomainList.size(); ++i)
		{
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
				return aCD;
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
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			if (aCD.getDomain() == null)
				return i;
		}
		return -1;
	}

	public boolean canSelectDeity(Deity aDeity)
	{
		if (aDeity == null)
		{
			deity = null;
			return false;
		}
		return aDeity.canBeSelectedBy(classList,
		  alignment
		);
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
		}
		return true;
	}

	public boolean hasSpecialAbility(String abilityName)
	{
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			if (((SpecialAbility)e.next()).getName().equalsIgnoreCase(abilityName))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasSpecialAbility(SpecialAbility sa)
	{
		final String saName = sa.getName();
		final String saDesc = sa.getDesc();
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			final SpecialAbility saFromList = (SpecialAbility)e.next();
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
		SortedSet bSet = new TreeSet();
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

		SortedSet autoLangs = new TreeSet();

		// Search for a CLEAR in the list and if found clear all BEFORE but not AFTER it.
		// ---arcady June 1, 2002

		for (Iterator e = templateAutoLanguages.iterator(); e.hasNext();)
		{
			String tempLang = e.next().toString();
			if (tempLang.equals(".CLEARRACIAL"))
			{
				clearRacials = true;
				languages.removeAll(getRace().getAutoLanguages());
			}
			else if (tempLang.equals(".CLEARALL") || tempLang.equals(".CLEAR"))
			{
				clearRacials = true;
				autoLangs.clear();
				languages.clear();
			}
			else if (tempLang.equals(".CLEARTEMPLATES"))
			{
				autoLangs.clear();
				languages.removeAll(templateAutoLanguages);
			}
			else
				autoLangs.add(tempLang);
		}
		if (!clearRacials)
			autoLangs.addAll(getRace().getAutoLanguages());

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			autoLangs.addAll(aClass.getAutoLanguages());
		}

		if (deity != null)
			autoLangs.addAll(deity.getAutoLanguages());

		for (Iterator i = featList.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat)i.next();
			autoLangs.addAll(aFeat.getAutoLanguages());
		}

		for (Iterator i = getSkillList().iterator(); i.hasNext();)
		{
			Skill aSkill = (Skill)i.next();
			autoLangs.addAll(aSkill.getAutoLanguages());
		}

		for (Iterator i = characterDomainList.iterator(); i.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)i.next();
			if (aCD.getDomain() != null)
				autoLangs.addAll(aCD.getDomain().getAutoLanguages());
		}

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				autoLangs.addAll(eq.getAutoLanguages());

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						autoLangs.addAll(eqMod.getAutoLanguages());
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
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

	public void addNaturalWeapons()
	{
		equipmentList.addAll(getRacialNaturalWeapons());
	}

	private ArrayList getRacialNaturalWeapons()
	{
		ArrayList naturalWeapons = new ArrayList();
		for (Iterator e = getRace().getNaturalWeapons().iterator(); e.hasNext();)
		{
			final Equipment anEquip = (Equipment)e.next();
			naturalWeapons.add(anEquip);
		}
		return naturalWeapons;
	}

	public SortedSet getRacialFavoredClasses()
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

	public boolean addFavoredClass(String aString)
	{
		if ((aString.length() != 0) && !favoredClasses.contains(aString))
		{
			favoredClasses.add(aString);
			return true;
		}
		return false;
	}

	public boolean removeFavoredClass(String aString)
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
		TreeSet favored = new TreeSet(favoredClasses);
		for (int i = 0; i < templateList.size(); ++i)
		{
			final PCTemplate template = (PCTemplate)templateList.get(i);
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
		SortedSet unfavoredClasses = new TreeSet();
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
			final PCClass aClass = (PCClass)e.next();
			if (!aList.contains(aClass.getDisplayClassName()) && (!aList.contains(aClass.toString())) &&
			  !aClass.isPrestige())
			{
				unfavoredClasses.add(aClass.getDisplayClassName());
				if (aClass.getLevel().intValue() > maxClassLevel)
				{
					if (hasAny)
					{
						secondClassLevel = maxClassLevel;
						secondClass = maxClass;
					}
					maxClassLevel = aClass.getLevel().intValue();
					maxClass = aClass.getDisplayClassName();
				}
				else if ((aClass.getLevel().intValue() > secondClassLevel) && (hasAny))
				{
					secondClassLevel = aClass.getLevel().intValue();
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
				final PCClass aClass = getClassDisplayNamed((String)e.next());
				if (aClass != null)
					if ((maxClassLevel - (aClass.getLevel().intValue())) > 1)
						xpPenalty++;
			}

			xpMultiplier = 1.0 - (xpPenalty * 0.2);
			if (xpMultiplier < 0)
				xpMultiplier = 0;
		}
		return xpMultiplier;
	}

	/** Returns a SortedSet list of bonus languages gained from race, class, and templates. */
	public SortedSet getBonusLanguages(boolean removeKnown)
	{
		SortedSet bonusLangs = new TreeSet();
		SortedSet bonusLangsb = new TreeSet();
		// Two of them to avoid ConcurrentModificationException
		Iterator e = null;

		bonusLangs.addAll(getRace().getLanguageBonus());
		bonusLangs.addAll(templateLanguages); // add from templates. sloppy?

		Collection classBonusLangs = null;
		for (e = classList.iterator(); e.hasNext();)
		{
			classBonusLangs = ((PCClass)e.next()).getLanguageBonus();
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
			languages.add(aString);
	}

	public void addFreeLanguage(String aString)
	{
		if (!languages.contains(aString))
		{
			languages.add(aString);
			freeLangs++;
		}
	}

	/**
	 * Return the total number of languages that the player character can
	 * know.  This includes extra languages from high stats, speak
	 * language skill, and race.
	 */
	public int languageNum()
	{
		return languageNum(true);
	}

	public int languageNum(boolean includeSpeakLanguage)
	{
		int i = getStatBonusTo("LANG", "BONUS");
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
			i += pcRace.getLangNum() + getTotalBonusTo("LANGUAGES", "NUMBER", true);
		}

		//
		// Check all classes for ADD:LANGUAGE
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			final int classLevel = aClass.getLevel().intValue();
			final ArrayList levelAbilityList = aClass.getLevelAbilityList();
			for (int x = levelAbilityList.size() - 1; x >= 0; x--)
			{
				final Object obj = levelAbilityList.get(x);
				if (obj instanceof LevelAbilityLanguage)
				{
					if (classLevel >= ((LevelAbilityLanguage)obj).level())
					{
						i += 1;
					}
				}
			}
		}

		i += freeLangs;
		return i;
	}

	public TreeSet getWeaponProfs(String type)
	{
		TreeSet result = new TreeSet();
		SortedSet alreadySeen = new TreeSet();
		for (Iterator e = getRace().getWeaponProfs().iterator(); e.hasNext();)
		{
			final String aString = (String)e.next();
			final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			final String typeString = aTok.nextToken();
			if (typeString.equals(type))
			{
				final String wpString = aTok.nextToken();
				final WeaponProf aProf = Globals.getWeaponProfNamed(wpString);
				if (aProf != null)
				{
					if (hasWeaponProfNamed(aProf.getName()))
					{
						result.add(aProf);
					}
					else
					{
						alreadySeen.add(aProf);
					}
				}
			}
		}
		for (Iterator e = Globals.getWeaponProfList().iterator(); e.hasNext();)
		{
			final WeaponProf aProf = (WeaponProf)e.next();
			if (aProf.getType().equalsIgnoreCase(type) &&
			  !alreadySeen.contains(aProf) &&
			  hasWeaponProfNamed(aProf.getName()))
				result.add(aProf);
		}
		return result;
	}

	private SortedSet addWeaponProfsLists(PObject obj, ArrayList aList, SortedSet aSet, ArrayList aFeatList, boolean addIt)
	{
		if (aList == null || aList.isEmpty())
			return aSet;
		final String sizeString = "FDTSMLHGC";
		for (Iterator e1 = aList.iterator(); e1.hasNext();)
		{
			String aString = (String)e1.next();
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

			final int lastComma = aString.lastIndexOf(",");
			boolean flag = (lastComma == -1);
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
				if (Globals.weaponTypesContains(aString))
				{
					for (Iterator e2 = Globals.getWeaponProfList().iterator(); e2.hasNext();)
					{
						final WeaponProf aProf = (WeaponProf)e2.next();
						if (aProf.getType().equalsIgnoreCase(aString))
						{
							aSet.add(aProf.getName());
							if (addIt)
							{
								addWeaponProfToList(aFeatList, aProf.getName(), true);
							}
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
		}

		//
		// Add template-granted weapon proficiencies
		//
		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
			results = addWeaponProfsLists(aTemplate, aTemplate.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aTemplate.getSelectedWeaponProfBonusCount(); ++i)
			{
				final String aString = aTemplate.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}

		//
		// Add class-granted weapon proficiencies
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			results = addWeaponProfsLists(aClass, aClass.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aClass.getSelectedWeaponProfBonusCount(); ++i)
			{
				final String aString = aClass.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}

		//
		// Add feat-granted weapon proficiencies
		//
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			results = addWeaponProfsLists(aFeat, aFeat.getWeaponProfAutos(), results, aFeatList, true);
			for (int i = 0; i < aFeat.getSelectedWeaponProfBonusCount(); ++i)
			{
				final String aString = aFeat.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}
		//
		// Add skill-granted weapon proficiencies
		//
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill)e.next();
			results = addWeaponProfsLists(aSkill, aSkill.getWeaponProfAutos(), results, aFeatList, true);
		}
		//
		// Add equipment-granted weapon proficiencies
		//
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				results = addWeaponProfsLists(eq, eq.getWeaponProfAutos(), results, aFeatList, true);

				ArrayList aList = eq.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
						results = addWeaponProfsLists(eqMod, eqMod.getWeaponProfAutos(), results, aFeatList, true);
					}
				}
				aList = eq.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator e2 = aList.iterator(); e2.hasNext();)
					{
						final EquipmentModifier eqMod = (EquipmentModifier)e2.next();
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
		}

		//
		// Add domain-granted weapon proficiencies
		//
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain)e.next();
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
			}
		}

		//
		// Parse though aggregate feat list, looking for any feats that grant weapon proficiencies
		//
		addFeatProfs(getStableAggregateFeatList(), aFeatList, results);
		addFeatProfs(getStableAutomaticFeatList(), aFeatList, results);
		return results;
	}

	private void addFeatProfs(final ArrayList stableList, ArrayList aFeatList, SortedSet results)
	{
		if (stableList != null && !stableList.isEmpty())
		{
			for (Iterator e = stableList.iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat)e.next();
				final String addString = aFeat.getAddString();
				final StringTokenizer aTok = new StringTokenizer(addString, "|", false);
				if (aTok.countTokens() != 2)
				{
					continue;
				}

				final String addType = new String(aTok.nextToken());
				if (addType.equals("WEAPONPROFS"))
				{
					final String addSec = new String(aTok.nextToken());
					if (Globals.weaponTypesContains(addSec))
					{
						for (Iterator e2 = Globals.getWeaponProfList().iterator(); e2.hasNext();)
						{
							final WeaponProf aProf = (WeaponProf)e2.next();
							if (aProf.getType().equalsIgnoreCase(addSec))
							{
								results.add(aProf.getName());
								addWeaponProfToList(aFeatList, aProf.getName(), true);
							}
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
				for (Iterator e = weapList.iterator(); e.hasNext(); )
				{
					final Equipment weap = (Equipment)e.next();
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
			for (Iterator e = Globals.getWeaponProfList().iterator(); e.hasNext();)
			{
				final WeaponProf aProf = (WeaponProf)e.next();
				if (aProf.getType().equalsIgnoreCase(aString))
				{
					addWeaponProfToList(aFeatList, aProf.getName(), isAuto);
				}
			}
			return;
		}

		WeaponProf wp = Globals.getWeaponProfNamed(aString);
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
						if (isAuto && !aFeat.isMultiples() && !featName.equals("Weapon Proficiency"))
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

						aFeat = (Feat)aFeat.clone();
						aFeat.addAssociated(aString);
						if (isAuto)
						{
							aFeat.setFeatType(Feat.FEAT_AUTOMATIC);
						}
						aFeatList.add(aFeat);
					}
					else
					{
						Globals.errorPrint("Weaponprof feat not found: " + featName + ":" + aString);
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

	public ArrayList aggregateSpellList(String aType, String school, String subschool, int minLevel)
	{
		return aggregateSpellList(aType, school, subschool, minLevel, minLevel);
	}

	public ArrayList aggregateSpellList(String aType, String school, String subschool, int minLevel, int maxLevel)
	{
		ArrayList aArrayList = new ArrayList();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			String cName = aClass.getKeyName();
			if (aClass.getCastAs().length() > 0)
				cName = aClass.getCastAs();
			Globals.debugPrint("Cast As:", cName);
			if (aType.equalsIgnoreCase("Any") || aType.equalsIgnoreCase(aClass.getSpellType()))
			{
				for (int a = minLevel; a <= maxLevel; ++a)
				{
					ArrayList aList = aClass.getCharacterSpell(null, "", a);
					if (aList.isEmpty())
						continue;
					for (Iterator i = aList.iterator(); i.hasNext();)
					{
						final CharacterSpell cs = (CharacterSpell)i.next();
						final Spell aSpell = cs.getSpell();
						if (((school.length() == 0 || school.equals(aSpell.getSchool())) ||
						  (subschool.length() == 0 || subschool.equals(aSpell.getSubschool()))))
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
			return floatZero;

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			// Loop through the list of top
			if (eq.getCarried().compareTo(floatZero) > 0 && !eq.isHeaderParent() && eq.getParent() == null)
			{
				if (eq.getChildCount() > 0)
				{
					totalWeight += eq.getWeight().floatValue() + eq.getContainedWeight().floatValue();
				}
				else
				{
					if (firstClothing && eq.isEquipped() && eq.getType().indexOf("CLOTHING") != -1)
					{
						//The first equipped set of clothing should have a weight of 0. Feature #437410
						firstClothing = false;
						totalWeight += eq.getWeight().floatValue() * Math.max(eq.getCarried().floatValue() - 1, 0);
					}
					else
					{
						totalWeight += eq.getWeight().floatValue() * eq.getCarried().floatValue();
					}
				}
			}
		}
		return new Float(totalWeight);
	}

	public Float totalValue()
	{
		float totalValue = 0;
		if (equipmentList.isEmpty())
		{
			return new Float(totalValue);
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (!eq.isHeaderParent())
			{
				totalValue += eq.getCost().floatValue() * eq.qty().floatValue();
			}
		}
		return new Float(totalValue);
	}

	public boolean isProficientWith(Equipment eq)
	{
		if (eq.isWeapon())
		{
			WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
			return eq.isNatural() || ((wp != null) && hasWeaponProfNamed(wp.getName()));
		}
		else if (eq.isArmor())
		{
			String aString = eq.getType();
			StringTokenizer aTok = new StringTokenizer(aString, ".", false);
			while (aTok.hasMoreTokens())
			{
				if (aTok.nextToken().equals("ARMOR"))
				{
					break;
				}
			}
			if (aTok.hasMoreTokens())
			{
				String aName = aTok.nextToken().toLowerCase();
				boolean flag = hasFeat("Armor Proficiency (" + aName + ")") || hasFeatAutomatic("Armor Proficiency (" + aName + ")");
				return flag;
			}
		}
		else if (eq.isShield())
		{
			return hasFeat("Shield Proficiency") || hasFeatAutomatic("Shield Proficiency");
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
	public ArrayList getEquipmentOfType(String typeName, int status)
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
	public ArrayList getEquipmentOfType(String typeName, String subtypeName, int status)
	{
		ArrayList aArrayList = new ArrayList();
		if (equipmentList.isEmpty())
			return aArrayList;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.typeStringContains(typeName) &&
			  (subtypeName.equals("") || eq.typeStringContains(subtypeName)) &&
			  (status == 3 ||
			  (status == 2 && !eq.isEquipped()) ||
			  (status == 1 && eq.isEquipped())))
				aArrayList.add(eq);
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

	public int handsFull()
	{
		ArrayList aArrayList = getEquipmentOfType("Weapon", 1);
		ArrayList bArrayList = getEquipmentOfType("Shield", 1);
		Iterator e = null;
		int hands = 0;
		WeaponProf wp = null;
		Equipment eq = null;
		if (!aArrayList.isEmpty())
		{
			for (e = aArrayList.iterator(); e.hasNext();)
			{
				eq = (Equipment)e.next();
				if (eq.isEquipped())
				{
					wp = Globals.getWeaponProfNamed(eq.profName());
					if (wp == null)
					{
						GuiFacade.showMessageDialog(null, "No entry in weapons.lst for " + eq.profName() + ". Weapons must be in that file to equip them.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						Globals.debugPrint("Globals: " + Globals.getWeaponProfList() + "\n" + "Prof: " + eq.profName());
						hands += 3;
					}
					else
					{
						final int hand = eq.getHand();
						switch (hand)
						{
							case Equipment.NEITHER_HAND:
								break;
							case Equipment.PRIMARY_HAND:
								hands += Math.max(1, eq.getHands());
								break;
							case Equipment.SECONDARY_HAND:
								hands += Math.max(1, eq.getHands());
								break;
							case Equipment.BOTH_HANDS:
								hands += Math.max(2, eq.getHands());
								break;
							case Equipment.TWOWEAPON_HANDS:
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
		if (!bArrayList.isEmpty())
		{
			for (e = bArrayList.iterator(); e.hasNext();)
			{
				eq = (Equipment)e.next();
				if (eq.isEquipped())
					hands += eq.getHands();
			}
		}
		return hands;
	}

	public boolean canEquip(String typeName)
	{
		ArrayList aArrayList = getEquipmentOfType(typeName, 1);

		if (typeName.equals("RING"))
			return aArrayList.size() < race.getHands() + getTotalBonusTo("RING", "NUMBER", true);
		else if (typeName.equals("Weapon") || typeName.equals("Shield"))
		{
			int hands = handsFull();
			if (hands > race.getHands())
			{
				GuiFacade.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return false;
			}
			return true;
		}
		else
			return aArrayList.size() == 0;
	}

	/** Not in use 2002-07-14 */
	public int handsTakenExceptFor(Equipment eq)
	{
		int hands = 3; //0=Primary;1=Off-Hand;2=Both;3=Neither
		if (equipmentList.isEmpty())
			return hands;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq1 = (Equipment)e.next();
			if (!eq1.equals(eq))
			{
				if (eq1.getHand() == Equipment.PRIMARY_HAND)
				{
					if (hands == 3)
						hands = 0;
					else if (hands == 1)
						hands = 2;
				}
				else if (eq1.getHand() == Equipment.SECONDARY_HAND)
				{
					if (hands == 3)
						hands = 1;
					else if (hands == 0)
						hands = 2;
				}
				else if (eq1.getHand() == Equipment.BOTH_HANDS)
					hands = 2;
				else if (eq1.getHand() == Equipment.TWOWEAPON_HANDS)
				{
					WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
					if (wp != null)
					{
						hands = 2 * Globals.handsNeededForWeapon(this, eq, wp);
					}
					else
					{
						hands = 2;
					}
				}
			}
		}
		return hands;
	}

	public void adjustGold(float delta)
	{
		//I don't really like this hack, but setScale just won't work right...
		gold = new BigDecimal(gold.floatValue() + delta).divide(BIG_ONE, 2, BigDecimal.ROUND_HALF_EVEN);
	}

	public void determinePrimaryOffWeapon()
	{
		primaryWeapons.clear();
		secondaryWeapons.clear();
		if (equipmentList.isEmpty())
			return;
		ArrayList unequippedPrimary = new ArrayList();
		ArrayList unequippedSecondary = new ArrayList();
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment)e.next();
			if (!eq.isWeapon())
			{
				continue;
			}
			boolean isEquipped = eq.isEquipped();
			if ((eq.getHand() == Equipment.PRIMARY_HAND)
			  || ((eq.getHand() == Equipment.BOTH_HANDS) && primaryWeapons.isEmpty())
			  || (eq.getHand() == Equipment.TWOWEAPON_HANDS))
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
			else if ((eq.getHand() == Equipment.BOTH_HANDS) && !primaryWeapons.isEmpty())
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
			if (eq.getHand() == Equipment.SECONDARY_HAND)
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
			if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
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

	public int getBonusForMapKey(String aKey)
	{
		String val = (String)bonusMap.get(aKey);
		if (val != null && !val.equals("x"))
			return Integer.parseInt(val);
		return 0;
	}

	public int getTotalBonusTo(String bonusType, String bonusName, boolean stacks)
	{
		int bonus = 0;
		final String prefix = new StringBuffer(bonusType.toUpperCase()).append(".").append(bonusName.toUpperCase()).append(".").toString();
		bonusType=bonusType.toUpperCase();
		bonusName=bonusName.toUpperCase();
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
			if (bonusType.equalsIgnoreCase("CHECKS"))
				getCheckBonusTo(bonusType, bonusName);
			if (!classList.isEmpty())
				getClassBonusTo(bonusType, bonusName);
			if (!companionModList.isEmpty())
				getCompanionModBonusTo(bonusType, bonusName);
			if (!equipmentList.isEmpty())
				getEquipmentBonusTo(bonusType, bonusName, stacks);
			getFeatBonusTo(bonusType, bonusName, stacks);
			if (!templateList.isEmpty())
				getTemplateBonusTo(bonusType, bonusName, stacks);
			if (!characterDomainList.isEmpty())
				getDomainBonusTo(bonusType, bonusName);
			getRace().bonusTo(bonusType, bonusName);
			if (!getSkillList().isEmpty())
				getSkillBonusTo(bonusType, bonusName);
			if (getDeity() != null)
				getDeity().bonusTo(bonusType, bonusName);
			if (bonusType.startsWith("WEAPONPROF="))
				getWeaponProfBonusTo(bonusType.substring(11), bonusName);
			if (bonusType.startsWith("ARMORPROF="))
				getArmorProfBonusTo(bonusType.substring(10), bonusName);
			ArrayList aList = new ArrayList();
			for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
			{
				final String aKey = i.next().toString();
				if (aKey.startsWith(prefix))
				{
					// make a list of any keys that start with .REPLACE
					if (aKey.endsWith(".REPLACE"))
						aList.add(aKey);
					bonus += getBonusForMapKey(aKey);
				}
			}
			// Now adjust the bonus if the .REPLACE value replaces the value without .REPLACE
			if (!aList.isEmpty())
			{
				for (Iterator i = aList.iterator(); i.hasNext();)
				{
					final String replaceKey = (String)i.next();
					if (replaceKey.length() > 7)
					{
						final String aKey = replaceKey.substring(0, replaceKey.length() - 8);
						final int replaceBonus = getBonusForMapKey(replaceKey);
						int aBonus = getBonusForMapKey(aKey);
						aBonus += getBonusForMapKey(aKey + ".STACK");
						bonus -= Math.min(aBonus, replaceBonus);
					}
				}
			}
		}
		catch (NumberFormatException exc)
		{
			Globals.errorPrint("error in getTotalBonusTo " + bonusType + " " + bonusName, exc);
			bonus = 0;
		}

		return bonus;
	}

	/*
	* return bonus total for a specific bonusType.
	* e.g. getBonusDueToType("COMBAT","AC","Armor") to get armor bonuses.
	*/
	public int getBonusDueToType(String mainType, String subType, String bonusType)
	{
		final String typeString = mainType.toUpperCase() + "." + subType.toUpperCase() + "." + bonusType.toUpperCase();
		int bonus = getBonusForMapKey(typeString);
		int replaceBonus = getBonusForMapKey(typeString + ".REPLACE");
		int stackBonus = getBonusForMapKey(typeString + ".STACK");
		return Math.max(bonus, replaceBonus) + stackBonus;
	}

	public void setBonusStackFor(int bonus, String bonusType)
	{
		if (bonusType != null)
			bonusType = bonusType.toUpperCase();

		int index = -2;
		StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);
		// e.g. "COMBAT.AC.Dodge"
		if (aTok.countTokens() > 2)
		{
			// should be bonus category e.g. "COMBAT"
			String aString = aTok.nextToken();
			// should be bonus name e.g. "AC"
			aString = aTok.nextToken();
			// should be bonus type e.g. whatever
			aString = aTok.nextToken();
			if (aString != null && !aString.equalsIgnoreCase("null"))
				index = Globals.getBonusStackList().indexOf(aString); // e.g. Dodge
			if (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken(); // could be .REPLACE or .STACK
				if (aString.equals("REPLACE") || aString.equals("STACK"))
					index = 0; // REPLACE and STACK both stack with themselves, this lets that happen
			}
		}
		if (index == -1) // meaning, a non-stacking bonus
		{
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey == null || aKey.equals("x"))
				bonusMap.put(bonusType, String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(Math.max(bonus, Integer.parseInt(aKey))));
		}
		else // stacking bonuses
		{
			if (bonusType == null)
				bonusType = "";
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey == null || aKey.equals("x"))
				bonusMap.put(bonusType, String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(bonus + Integer.parseInt(aKey)));
		}
		setDirty(true);
	}

	public int getSkillBonusTo(String type, String aName)
	{
		int bonus = 0;
		if (getSkillList().isEmpty())
			return bonus;

		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
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

	private int getClassBonusTo(String type, String aName)
	{
		int bonus = 0;
		if (classList.isEmpty())
			return bonus;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			bonus += aClass.getBonusTo(type, aName, aClass.getLevel().intValue());
		}
		return bonus;
	}

	public int getStatBonusTo(String type, String aName)
	{
		return statList.getBonusTo(type, aName);
	}

	public int getCheckBonusTo(String type, String aName)
	{
		int bonus = 0;

		for (Iterator i = Globals.getCheckList().iterator(); i.hasNext();)
		{
			PObject obj = (PObject)i.next();
			bonus += obj.bonusTo(type, aName);
		}
		return bonus;
	}

	/**
	 * return bonus from CompanionMod list
	 **/
	private int getCompanionModBonusTo(String type, String aName)
	{
		int bonus = 0;
		if (companionModList.isEmpty())
			return bonus;
		for (Iterator e = companionModList.iterator(); e.hasNext();)
		{
			final CompanionMod cMod = (CompanionMod)e.next();
			bonus += cMod.bonusTo(type, aName);
		}
		return bonus;
	}

	private int getDomainBonusTo(String type, String aName)
	{
		int bonus = 0;
		if (characterDomainList.isEmpty())
			return bonus;
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)e.next();
			Domain aDomain = aCD.getDomain();
			if (aDomain != null)
				bonus += aDomain.bonusTo(type, aName);
		}
		return bonus;
	}

	private void mergeBonusMap(HashMap map)
	{
		if (map.isEmpty())
			return;
		for (Iterator i = map.keySet().iterator(); i.hasNext();)
		{
			final String bonusType = i.next().toString();
			int iBonus = Integer.parseInt((String)map.get(bonusType));

			final String aKey = (String)bonusMap.get(bonusType);

			if (aKey != null)
			{
				boolean stacks = true;
				StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);
				if (aTok.countTokens() > 2)
				{
					// bonus category e.g. "COMBAT"
					String aString = aTok.nextToken();
					// bonus name e.g. "AC"
					aString = aTok.nextToken();
					// bonus type e.g. whatever
					aString = aTok.nextToken();
					if ((aString != null) && !aString.equalsIgnoreCase("null"))
					{
						stacks = Globals.getBonusStackList().indexOf(aString) >= 0;
					}
				}

				int iCurrent = getBonusForMapKey(bonusType);
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

	private int getEquipmentBonusTo(String type, String aName, boolean stacks)
	{
		int bonus = 0;
		if (equipmentList.isEmpty())
			return bonus;

		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped())
			{
				if (stacks)
				{
					bonus += eq.bonusTo(type, aName, true);
					mergeBonusMap(eq.getBonusMap());
					bonus += eq.bonusTo(type, aName, false);
					mergeBonusMap(eq.getBonusMap());
				}
				else
				{
					bonus = Math.max(bonus, eq.bonusTo(type, aName, true));
					mergeBonusMap(eq.getBonusMap());
					bonus = Math.max(bonus, eq.bonusTo(type, aName, false));
					mergeBonusMap(eq.getBonusMap());
				}
			}
		}
		return bonus;
	}

	public int getTemplateBonusTo(String type, String aName, boolean subSearch)
	{

		PCTemplate t = null;
		int i = 0;
		if (templateList.isEmpty())
			return i;
		final Iterator iterator = templateList.iterator();

		for (Iterator e = iterator; e.hasNext();)
		{
			t = (PCTemplate)e.next();

			int j = t.bonusTo(type, aName);
			if (j == 0)
				j = t.bonusTo(type, "LIST");
			int k = Math.max(1, (int)(t.getAssociatedCount() * t.getCost()));
			if (subSearch && t.getAssociatedCount() > 0)
			{
				k = 0;
				for (int f = 0; f < t.getAssociatedCount(); ++f)
				{
					String aString = t.getAssociated(f);
					if (aString.equals(aName))
						k++;
				}
			}
			if (k == 0 && j != 0)
				i += j;
			else
				i += j * k;
		}
		return i;
	}

	public int getFeatBonusTo(String type, String aName, boolean subSearch)
	{
		int i = 0;
		ArrayList aList = aggregateFeatList();
		if (aList.isEmpty())
			return i;
		final Iterator iterator = aList.iterator();
		for (Iterator e = iterator; e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			int j = aFeat.bonusTo(type, aName);
			if (j == 0)
				j = aFeat.bonusTo(type, "LIST");
			int k = Math.max(1, (int)(aFeat.getAssociatedCount() * aFeat.getCost()));
			if (subSearch && aFeat.getAssociatedCount() > 0)
			{
				k = 0;
				for (int f = 0; f < aFeat.getAssociatedCount(); ++f)
				{
					String aString = aFeat.getAssociated(f);
					if (aString.equals(aName))
						k++;
				}
			}
			if (k == 0 && j != 0)
				i += j;
			else
				i += j * k;
		}
		return i;
	}

	public Integer defense()
	{
		//if (!equipmentList.isEmpty())
		//{
		//	return armorDefense();
		//}
		int i = 0;
		i += armorDefense().intValue();
		i += sizeDefense().intValue() + raceDefense().intValue();
		i += naturalDefense().intValue() + dexterityDefense().intValue();
		i += classDefense().intValue();
		i += classDefenseBonus().intValue();
		i += miscDefense().intValue();
		return new Integer(i);
	}

//begin breakdown of defense info Arknight 08-09-02
//*************************************************
	public Integer DefenseTotal()
	{
		return defense();
	}

	public Integer miscDefense()
	{
		return new Integer(getTotalBonusTo("COMBAT", "DEFENSE", true));
	}

	public Integer armorDefense()
	{
		final int i = 0;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			final Equipment eq = (Equipment)e.next();
			if (eq.isEquipped() && eq.isArmor())
			{
				if (eq.eDR().intValue() == 0) //Assume Armor defense
				{
					int armorvalue = 0;
					int armorvalue1 = getACCalculator().acModFromArmor();
					int armorvalue2 = getACCalculator().acModFromShield();
					armorvalue += armorvalue1 + armorvalue2;
					return new Integer(armorvalue);
				}
				else
				{
					return new Integer(eq.getDefBonus().intValue());
				}
			}
		}
		return new Integer(i);
	}

	public Integer classDefense()
	{
		int y = 0;
		int i = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			i += aClass.defense(y).intValue();
			y++;
		}
		return new Integer(i);
	}

	public Integer classDefenseBonus()
	{
		int i = 0;
		i += getFeatBonusTo("CLASS", "DEFENSE", true) + getTemplateBonusTo("CLASS", "DEFENSE", true) + getRace().bonusTo("CLASS", "DEFENSE");
		return new Integer(i);
	}

	public Integer sizeDefense()
	{
		int i = 0;
		i += modForSize();
		return new Integer(i);
	}

	public Integer raceDefense()
	{
		int i = 0;
		i += getRace().getStartingAC().intValue();
		return new Integer(i);
	}

	public Integer naturalDefense()
	{
		int i = 0;
		i += naturalArmorModForSize();
		return new Integer(i);
	}

	public Integer dexterityDefense()
	{
		int i = 0;
		i += acCalculator.acModFromDexterity();
		return new Integer(i);
	}

	public Integer flatfootedDefense()
	{
		int i = defense().intValue();
		// check if we may keep our dexterity bonus to AC
		for (Iterator it = getSpecialAbilityList().iterator(); it.hasNext();)
		{
			if (((SpecialAbility)it.next()).getName().endsWith("Dex bonus to AC)"))
				return new Integer(i);
		}

		// we obviously do NOT not keep our dexterity bonus to AC,
		// but we must apply dexterity penalties to AC!
		i -= dexterityDefense().intValue();
		if (dexterityDefense().intValue() < 0)
		{
			i += Math.min(0, dexterityDefense().intValue());
		}
		return new Integer(i);
	}

	public Integer touchDefense()
	{
		int i = 0;
		i += sizeDefense().intValue() + raceDefense().intValue() + dexterityDefense().intValue();
		i += classDefense().intValue();
		i += classDefenseBonus().intValue();
		return new Integer(i);
	}
//**************************************************
	public Integer woundPoints()
	{
		int i = getTotalBonusTo("HP", "WOUNDPOINTS", false);
		return new Integer(i);
	}

	public Integer reputation()
	{
		int i = getRace().bonusTo("CLASS", "REPUTATION");
		i += getEquipmentBonusTo("CLASS", "REPUTATION", true);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			final int classLevel = aClass.getLevel().intValue();
			i += aClass.getBonusTo("CLASS", "REPUTATION", classLevel);
			String aString = aClass.getReputationString();
			int k = Integer.parseInt(aString);
			switch (k)
			{
				case 0:	/*Best*/
					i += 3 + classLevel / 2;
					break;
				case 1:	/*MHigh*/
					i += 1 + classLevel / 2;
					break;
				case 2:	/*MLow*/
					i += classLevel / 2;
					break;
				case 3:	/*Low*/
					i += classLevel / 3;
					break;
				case 4:	/*NPCH*/
					i += (classLevel + 1) / 3;
					break;
				case 5:	/*NPCL*/
					i += classLevel / 4;
					break;
				case 6:	/*PHigh*/
					if (classLevel % 3 != 0) i += (classLevel - (classLevel / 3));
					break;
				case 8:	/*P v3*/
				case 7:	/*PLow*/
					i += classLevel / 2;
					break;

				case 9:	/*P v4*/
					if (classLevel % 4 != 0) i += (classLevel - (classLevel / 4));
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
							i++;
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
					i += 0;
					break;
			}
		}
		int y = getTotalLevels();
		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.getLevelsPerRepIncrease().intValue() != 0)
				i += y / aFeat.getLevelsPerRepIncrease().intValue();
		}
		i += getFeatBonusTo("CLASS", "REPUTATION", true);
		i += getTemplateBonusTo("CLASS", "REPUTATION", true);
		return new Integer(i);
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
				PCClass aClass = (PCClass)i.next();
				final ArrayList aList = aClass.getCharacterSpell(null, aName, -1);
				for (int j = aList.size() - 1; j >= 0; j--)
				{
					CharacterSpell cs = (CharacterSpell)aList.get(j);
					cs.removeSpellInfo(cs.getSpellInfoFor(aName, -1, -1));
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * weather we should add auto known spells at level up
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

	public boolean isSpellProhibited(CharacterSpell acs, String className)
	{
		if (acs == null) return true;
		PCClass aClass = null;
		Spell aSpell = acs.getSpell();

		if (className != null)
		{
			aClass = getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")).trim());
		}
		if (aClass == null) return true;

		if (!acs.isSpecialtySpell() && aClass.isProhibited(aSpell))
			return true;

		return false;
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
	public String addSpell(CharacterSpell acs, ArrayList aFeatList, String className, String bookName,
	                       int adjSpellLevel, int spellLevel)
	{
		if (acs == null)
			return "Invalid parameter to add spell";
		PCClass aClass = null;
		Spell aSpell = acs.getSpell();

		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (className != null)
		{
			aClass = getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")).trim());
		}
		if (aClass == null)
			return "No class named " + className;
		if (!aClass.getMemorizeSpells() && !bookName.equals(Globals.getDefaultSpellBook()))
		{
			return aClass.getName() + " can only add to " + Globals.getDefaultSpellBook();
		}
		int numSpellsFromSpecialty = aClass.getNumSpellsFromSpecialty();
		// Divine spellcasters get no bonus spells at level 0
		// TODO: allow classes to define how many bonus spells they get each level!
		if (spellLevel == 0 && aClass.getSpellType().equalsIgnoreCase("Divine"))
			numSpellsFromSpecialty = 0;

		// all the exists checks are done.
		// now determine how many specialtySpells
		// of this level for this class in this book
		int spellsFromSpecialty = 0;
		// first we check this spell being added
		if (acs.isSpecialtySpell())
			spellsFromSpecialty++;
		// now all the rest of the already known spells
		ArrayList sList = aClass.getCharacterSpell(null, bookName, adjSpellLevel);
		if (!sList.isEmpty())
		{
			for (Iterator i = sList.iterator(); i.hasNext();)
			{
				final CharacterSpell cs = (CharacterSpell)i.next();
				if (cs != acs && cs.isSpecialtySpell())
					spellsFromSpecialty++;
			}
		}

		// don't allow adding spells which are prohibited
		// But if a spell is both prohibited and in a specialty
		// which can be the case for some spells, then allow it.
		if (!acs.isSpecialtySpell() && aClass.isProhibited(aSpell))
			return acs.getSpell().getName() + " is prohibited.";

		// Now let's see if they should be able to add this spell

		// first check for known/cast/threshold
		int known = 0;
		if (Globals.isSSd20Mode())
			known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel, bookName);
		else
			known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel);
		int specialKnown = 0;
		int cast = aClass.getCastForLevel(aClass.getLevel().intValue(), adjSpellLevel, bookName, true, true);
		int castByStat = 0; // how many spells of actual spell's level can be cast without stat limitation
		int castLowLevel = 0;
		if (spellLevel == adjSpellLevel)
			castLowLevel = cast;
		else
			castLowLevel = aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel, bookName, true, true);
		if (cast != 0)
			castByStat = cast;
		else
			castByStat = aClass.getCastForLevel(aClass.getLevel().intValue(), adjSpellLevel, bookName, true, false);
		int listNum = aClass.memorizedSpellForLevelBook(adjSpellLevel, bookName);
		boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		if (isDefault)
			specialKnown = aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel);

		// known is the maximun spells that can be known this level
		// listNum is the current spells already memorized this level
		// cast is the number of spells that can be cast at this level
		if (Globals.isSSd20Mode() && !isDefault)
		{
			if ((listNum + aSpell.getCastingThreshold()) > known + specialKnown)
				return "Your maximum space for this category is:" + known + specialKnown;
		}
		else if (!aClass.getMemorizeSpells() && listNum >= known)
		{
			if (listNum >= known + specialKnown)
				return "You only know " + (known + specialKnown) + " spells for level " + adjSpellLevel;
			if (listNum - spellsFromSpecialty >= known && !acs.isSpecialtySpell())
				return "Your remaining slot(s) must come from your specialty";
		}
		else if (aClass.getMemorizeSpells() && !isDefault && listNum >= cast - numSpellsFromSpecialty)
		{
			if (cast > 0 || castLowLevel == 0 || listNum >= castByStat - numSpellsFromSpecialty)
			{
				if (listNum >= cast)
					return "You can only prepare " + cast + " total for level " + adjSpellLevel;
				if (!acs.isSpecialtySpell() && ((listNum - spellsFromSpecialty) >= (cast - numSpellsFromSpecialty)))
					return "Your remaining slots must come from your specialty or domain";
			}
		}

		// determine if this spell already exists
		// for this character in this book at this level
		SpellInfo si = null;
		boolean isEmpty = (aClass.getCharacterSpell(acs.getSpell(), bookName, adjSpellLevel).isEmpty());
		if (!isEmpty)
			si = acs.getSpellInfoFor(bookName, adjSpellLevel, -1, aFeatList);

		if (si != null)
		{
			// ok, we already known this spell, so if they are
			// trying to add it to the default spellBook, barf
			// otherwise increment the number of times memorized
			if (isDefault)
				return "The Known Spells spellbook contains all spells of this level that you know. You cannot place spells in multiple times.";
			else
				si.setTimes(si.getTimes() + 1);
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
	public String delSpell(SpellInfo si, PCClass aClass, String bookName)
	{
		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (aClass == null)
			return "Error: Class is null";
		final CharacterSpell acs = si.getOwner();

		boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
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
			acs.removeSpellInfo(si);
		si = acs.getSpellInfoFor("", -1, -1, null);
		if (si == null)
			aClass.removeCharacterSpell(acs);
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
		}

		PCClass aClass = null;
		Feat classFeat = null;
		StringTokenizer classTok = null;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			aClass = (PCClass)e.next();
			for (Iterator e1 = aClass.vFeatList().iterator(); e1.hasNext();)
			{
				classTok = new StringTokenizer((String)e1.next(), ":", false);
				final int level = Integer.parseInt(classTok.nextToken());
				if (level <= aClass.getLevel().intValue())
				{
					classTok = new StringTokenizer(classTok.nextToken(), "|", false);
					while (classTok.hasMoreTokens())
					{
						final String featName = classTok.nextToken();
						classFeat = Globals.getFeatNamed(featName);
						if (classFeat != null)
						{
							classFeat = (Feat)classFeat.clone();
							classFeat.setFeatType(Feat.FEAT_VIRTUAL);
							if (!classFeat.getName().equalsIgnoreCase(featName))
							{
								final int i = featName.indexOf("(");
								final int j = featName.indexOf(")");
								if (i > -1 && j > -1)
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
				final Equipment aE = (Equipment)e.next();
				if (aE.isEquipped())
				{
					for (Iterator e1 = aE.getVFeatList().iterator(); e1.hasNext();)
					{
						final String featName = e1.next().toString();
						Feat aFeat = Globals.getFeatNamed(featName);
						if (aFeat != null)
						{
							aFeat = (Feat)aFeat.clone();
							aFeat.setFeatType(Feat.FEAT_VIRTUAL);
							if (!aFeat.getName().equalsIgnoreCase(featName))
							{
								final int i = featName.indexOf("(");
								final int j = featName.indexOf(")");
								if (i > -1 && j > -1)
								{
									final StringTokenizer aTok = new StringTokenizer(featName.substring(i + 1, j), ",", false);
									while (aTok.hasMoreTokens())
									{
										final String a = aTok.nextToken();
										if (!aFeat.containsAssociated(a))
											aFeat.addAssociated(a);
									}
								}
							}
							vFeatList.add(aFeat);
						}
					}
				}
			}
		}
		final StringTokenizer raceTok = new StringTokenizer(getRace().getVFeatList(), "|", false);
		while (raceTok.hasMoreTokens())
		{
			final String featName = raceTok.nextToken();
			Feat aFeat = Globals.getFeatNamed(featName);
			if (aFeat != null)
			{
				aFeat = (Feat)aFeat.clone();
				aFeat.setFeatType(Feat.FEAT_VIRTUAL);
				if (!aFeat.getName().equalsIgnoreCase(featName))
				{
					final int i = featName.indexOf("(");
					final int j = featName.indexOf(")");
					if (i > -1 && j > -1)
					{
						final StringTokenizer aTok = new StringTokenizer(featName.substring(i + 1, j), ",", false);
						while (aTok.hasMoreTokens())
						{
							final String a = aTok.nextToken();
							if (!aFeat.containsAssociated(a))
								aFeat.addAssociated(a);
						}
					}
				}
				vFeatList.add(aFeat);
			}
		}

		setStableVirtualFeatList(vFeatList);
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

	private void addToAutoFeatList(ArrayList autoFeatList, String featName)
	{
		String altName = "";
		String subName = "";
		if (featName.endsWith(")"))
		{
			subName = featName.substring(featName.indexOf("(") + 1, featName.lastIndexOf(")")); //we want what is inside the outermost parens.
			altName = featName.substring(0, featName.indexOf("(") - 1);
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
				aFeat = (Feat)aFeat.clone();
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
			final PCClass aClass = (PCClass)e.next();
			for (Iterator e1 = aClass.getFeatAutos().iterator(); e1.hasNext();)
			{
				//
				// PCClass object have auto feats stored in format:
				// lvl|feat_name
				//
				String aString = (String)e1.next();
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
				if (i > aClass.getLevel().intValue())
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

				final PCTemplate aTemplate = (PCTemplate)e.next();
				ArrayList templateFeats = aTemplate.feats(getTotalLevels(), totalHitDice());
				if (!templateFeats.isEmpty())
				{
					for (Iterator e2 = templateFeats.iterator(); e2.hasNext();)
					{
						final String aString = (String)e2.next();
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
				final CharacterDomain aCD = (CharacterDomain)e.next();
				final Domain aDomain = aCD.getDomain();
				if (aDomain != null)
				{
					for (int e2 = 0; e2 < aDomain.getAssociatedCount(); e2++)
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
		HashMap aHashMap = new HashMap();
		if (!featList.isEmpty())
		{
			for (Iterator e = ((ArrayList)featList.clone()).iterator(); e.hasNext();)
			{
				final Feat aFeat = (Feat)e.next();
				if (aFeat != null)
				{
					aHashMap.put(aFeat.getKeyName(), aFeat);
				}
			}
		}

		for (Iterator e = vFeatList().iterator(); e.hasNext();)
		{
			Feat virtualFeat = (Feat)e.next();
			if (!aHashMap.containsKey(virtualFeat.getKeyName()))
			{
				aHashMap.put(virtualFeat.getKeyName(), virtualFeat);
			}
			else if (virtualFeat.isMultiples())
			{
				Feat aggregateFeat = (Feat)aHashMap.get(virtualFeat.getKeyName());
				aggregateFeat = (Feat)aggregateFeat.clone();
				for (int e1 = 0; e1 < virtualFeat.getAssociatedCount(); e1++)
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
			final Feat autoFeat = (Feat)e.next();
			if (!aHashMap.containsKey(autoFeat.getKeyName()))
			{
				aHashMap.put(autoFeat.getName(), autoFeat);
			}
			else if (autoFeat.isMultiples())
			{
				Feat aggregateFeat = (Feat)aHashMap.get(autoFeat.getKeyName());
				aggregateFeat = (Feat)aggregateFeat.clone();
				for (int e1 = 0; e1 < autoFeat.getAssociatedCount(); e1++)
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
	 * calculate the total class modifier to save where
	 * Unused. I will remove the next time I come across it.
	 */
	/*
	private int _calculateSaveBonusClass(int saveIndex)
	{
		int save = 0;
		for (Iterator it = classList.iterator(); it.hasNext();)
		{
			PCClass aClass = (PCClass)it.next();
			save += aClass.checkBonus(saveIndex - 1, true);
		}
		int masterSave = 0;
		// now we see if this PC is a Familiar
		PlayerCharacter nPC = getMasterPC();
		if ((nPC != null) && isFamiliarOrMount(followerMaster))
		{
			// It seems to be, so calculate the Masters baseSave
			masterSave = nPC.calculateSaveBonus(saveIndex, Globals.getCheckList().get(saveIndex - 1).toString(), "BASE");
		}
		//
		save = Math.max(save, masterSave);

		return save;
	}
	*/

	/**
	 * calculate the total racial modifier to save where
	 *
	 * this includes
	 *   racial boni like the standard halfling's +1 on all saves
	 *   template boni like the forgotten realms lightfoot halfling's +1 on all saves
	 *   racial base modifiers for certain monsters
	 *
	 */
	private int _calculateSaveBonusRace(int saveIndex)
	{
		int save = 0;
		if (saveIndex - 1 < 0 || saveIndex - 1 >= Globals.getCheckList().size())
			return 0;
		final String sString = Globals.getCheckList().get(saveIndex - 1).toString();
		save += race.bonusTo("CHECKS", "BASE." + sString) + race.bonusTo("CHECKS", sString);
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
	 *   exclude := "NOFEATS"|"NOMAGIC"|"NORACE"
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
		StringTokenizer aTok = new StringTokenizer(tokenString, ".");
		String[] tokens = new String[aTok.countTokens()];
		final int checkIndex = Globals.getIndexOfCheck(saveType)+1;
		int save = 0;
		for (int i = 0; aTok.hasMoreTokens(); ++i)
		{
			tokens[i] = aTok.nextToken();
			if (tokens[i].equals("TOTAL"))
			{
				save += getBonus(checkIndex , true);
			}
			else if (tokens[i].equals("BASE"))
			{
				save += getBonus(checkIndex , false);
			}
			else if (tokens[i].equals("MISC"))
			{
				save += getTotalBonusTo("CHECKS", saveType, true);
			}
			if (tokens[i].equals("MAGIC"))
			{
				save += getEquipmentBonusTo("CHECKS", saveType, true);
			}
			else if (tokens[i].equals("RACE"))
			{
				save += _calculateSaveBonusRace(saveIndex);
			}
			else if (tokens[i].equals("FEATS"))
			{
				save += getFeatBonusTo("CHECKS", saveType, true);
			}
			/**
			 * exclude stuff
			 */
			else if (tokens[i].equals("NOMAGIC"))
			{
				save -= getEquipmentBonusTo("CHECKS", saveType, true);
			}
			else if (tokens[i].equals("NORACE"))
			{
				save -= _calculateSaveBonusRace(saveIndex);
			}
			else if (tokens[i].equals("NOFEATS"))
			{
				save -= getFeatBonusTo("CHECKS", saveType, true);
			}
		}

		return save;
	}

	/**
	 * returns the number of spells based on class, level and spellbook
	 **/
	private int countSpellListBook(String aString)
	{
		final int dot = aString.lastIndexOf(".");
		int spellCount = 0;
		if (dot < 0)
		{
			for (Iterator iClass = classList.iterator(); iClass.hasNext();)
			{
				final PCClass aClass = (PCClass)iClass.next();
				spellCount += aClass.getCharacterSpellCount();
			}
		}
		else
		{
			int classNum = Integer.parseInt(aString.substring(17, dot));
			int levelNum = Integer.parseInt(aString.substring(dot + 1, aString.length() - 1));

			PObject aObject = getSpellClassAtIndex(classNum);
			if (aObject != null)
			{
				List aList = aObject.getCharacterSpell(null, Globals.getDefaultSpellBook(), levelNum);
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
		int spellCount = 0;

		StringTokenizer aTok = new StringTokenizer(aString, ".");
		int classNum = Integer.parseInt(aTok.nextToken());
		int sbookNum = Integer.parseInt(aTok.nextToken());
		int levelNum = Integer.parseInt(aTok.nextToken());

		String bookName = Globals.getDefaultSpellBook();
		if (sbookNum > 0)
			bookName = (String)getSpellBooks().get(sbookNum);
		PObject aObject = getSpellClassAtIndex(classNum);

		if (aObject != null)
		{
			List aList = aObject.getCharacterSpell(null, bookName, levelNum);
			spellCount = aList.size();
			if (spellCount > 0)
			{
				canWrite = true;
			}
		}
		return spellCount;
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
		StringTokenizer aTok = new StringTokenizer(aString.substring(10), ".", false);
		int classNum = Integer.parseInt(aTok.nextToken());
		int bookNum = Integer.parseInt(aTok.nextToken());
		int spellLevel = Integer.parseInt(aTok.nextToken());
		int spellNumber = Integer.parseInt(aTok.nextToken());

		PObject aObject = getSpellClassAtIndex(classNum);
		String bookName = Globals.getDefaultSpellBook();
		if (bookNum > 0)
			bookName = (String)getSpellBooks().get(bookNum);

		if (aObject != null || classNum == -1)
		{
			if (classNum == -1)
				bookName = Globals.getDefaultSpellBook();

			if (!bookName.equals(""))
			{
				SpellInfo si = null;
				if (classNum == -1)
				{
					ArrayList charSpellList = new ArrayList();
					for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
					{
						final PCClass aClass = (PCClass)iClass.next();
						final ArrayList bList = aClass.getCharacterSpell(null, bookName, -1);
						for (Iterator bi = bList.iterator(); bi.hasNext();)
						{
							final CharacterSpell cs = (CharacterSpell)bi.next();
							if (!charSpellList.contains(cs))
								charSpellList.add(cs);
						}
					}
					Collections.sort(charSpellList);
					if (spellNumber < charSpellList.size())
					{
						final CharacterSpell cs = (CharacterSpell)charSpellList.get(spellNumber);
						si = cs.getSpellInfoFor(bookName, -1, -1);
						found = true;
					}
				}
				else if (aObject != null)
				{
					List charSpells = aObject.getCharacterSpell(null, bookName, spellLevel);
					if (spellNumber < charSpells.size())
					{
						final CharacterSpell cs = (CharacterSpell)charSpells.get(spellNumber);
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

	private void setProf(Equipment equip, Equipment eqm)
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

	private String appendToName(String aName, String aString)
	{
		StringBuffer aBuf = new StringBuffer(aName);
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
		ArrayList weapList = getEquipmentOfType("Weapon", 3);
		//
		// Go through the weapons list. If any weapon is both Melee and Ranged, then make
		// 2 weapons for list, one Melee only, the other Ranged and Thrown.
		// For double weapons, if wielded in both hands show attacks for both heads, head 1, and head 2
		// If wielded in 1 hand, then just show the damage by head.
		//
		for (int idx = 0; idx < weapList.size(); ++idx)
		{
			final Equipment equip = (Equipment)weapList.get(idx);
			if (equip.isDouble())
			{
				Equipment eqm = (Equipment)equip.clone();
				eqm.removeType("Double");
				eqm.setName(appendToName(eqm.getName(), "Head 1 only"));	// Add "Head 1 only" to the name of the weapon
				setProf(equip, eqm);
				weapList.add(idx + 1, eqm);

				eqm = (Equipment)equip.clone();
				String altType = eqm.getType(false);
				if (altType.length() != 0)
				{
					eqm.setType(altType);
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
				if (equip.whatHand() != Equipment.BOTH_HANDS)
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
				Equipment eqm = (Equipment)equip.clone();
				eqm.removeType("Ranged.Thrown");
				eqm.setRange("0");
				setProf(equip, eqm);
				weapList.set(idx, eqm);
				//
				// Replace any primary weapons
				int iPrimary;
				for (iPrimary = getPrimaryWeapons().size() - 1; iPrimary >= 0; iPrimary--)
				{
					final Equipment teq = (Equipment)getPrimaryWeapons().get(iPrimary);
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
				for (iSecondary = getSecondaryWeapons().size() - 1; iSecondary >= 0; iSecondary--)
				{
					final Equipment teq = (Equipment)getSecondaryWeapons().get(iSecondary);
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
				eqm = (Equipment)equip.clone();
				eqm.addType("Ranged.Thrown");
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
		while (aString.lastIndexOf("(") > -1)
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
		String delimiter = "+-/*";
		String valString = "";
		int mode = 0; //0=plus, 1=minus, 2=mult, 3=div
		int nextMode = 0;
		int endMode = 0; //1,11=min, 2,12=max, 3,13=req, 10 = int

		if (aString.startsWith(".IF."))
		{
			StringTokenizer aTok = new StringTokenizer(aString.substring(4), ".", true);
			String bString = "";
			Float val1 = null; // first value
			Float val2 = null; // other value in comparison
			Float valt = null; // value if comparison is true
			Float valf = null; // value if comparison is false
			int comp = 0;
			while (aTok.hasMoreTokens())
			{
				String cString = aTok.nextToken();
				if (cString.equals("GT") || cString.equals("GTEQ") || cString.equals("EQ") || cString.equals("LTEQ") || cString.equals("LT"))
				{
					val1 = getVariableValue(bString.substring(0, bString.length() - 1), src); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
					if (cString.equals("LT"))
					{
						comp = 1;
					}
					else if (cString.equals("LTEQ"))
					{
						comp = 2;
					}
					else if (cString.equals("EQ"))
					{
						comp = 3;
					}
					else if (cString.equals("GT"))
					{
						comp = 4;
					}
					else if (cString.equals("GTEQ"))
					{
						comp = 5;
					}
				}
				else if (cString.equals("THEN"))
				{
					val2 = getVariableValue(bString.substring(0, bString.length() - 1), src); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else if (cString.equals("ELSE"))
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
						Globals.debugPrint("ERROR - badly formed statement:" +
						  aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				Globals.debugPrint("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
				return total;
			}
		}
		for (int i = 0; i < aString.length(); ++i)
		{
			valString += aString.substring(i, i + 1);
			if (i == aString.length() - 1 || delimiter.lastIndexOf(aString.charAt(i)) > -1 ||
			  (valString.length() > 3 && (valString.endsWith("MIN") || valString.endsWith("MAX") || valString.endsWith("REQ"))))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
				{
					valString = valString.substring(0, valString.length() - 1);
				}
				if (valString.length() > 2 && valString.startsWith("%") && valString.endsWith("%"))
				{
					Globals.debugPrint(valString + " " + loopVariable + " " + loopValue);
					if (loopVariable.equals("")) // start the loop
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
					valString = new Integer(loopValue).toString();
					Globals.debugPrint("loopVariable=" + loopVariable + " loopValue=" + loopValue);
				}
				if (valString.equals("SCORE") && src.startsWith("STAT:"))
				{
					valString = String.valueOf(statList.getTotalStatFor(src.substring(5)));
				}
				if (valString.equals("SPELLBASESTATSCORE"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getSpellBaseStat() + "SCORE";
						if (valString.equals("SPELLSCORE"))
						{
							valString = "10";
						}
					}
					else
					{
						valString = "0";
					}
				}
				if (valString.equals("SPELLBASESTAT"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getSpellBaseStat();
						if (valString.equals("SPELL"))
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
					Globals.debugPrint("MOD=", valString);
				}
				else if (valString.length() == 8 && Globals.getStatFromAbbrev(valString.substring(0, 3)) > -1 && valString.endsWith(".BASE"))
				{
					Globals.debugPrint("STAT=", valString.substring(0, 3));
					valString = Integer.toString(statList.getBaseStatFor(valString.substring(0, 3)));
					Globals.debugPrint(" BASE=", valString);
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
					Globals.debugPrint("SCORE=", valString);
				}
				else if (valString.startsWith("CL="))
				{
					PCClass aClass = null;
					if (valString.length() > 3)
					{
						aClass = getClassNamed(valString.substring(3));
					}
					else
					{
						Globals.debugPrint("Error! Cannot determine CL!");
					}
					if (aClass != null)
					{
						valString = aClass.getLevel().toString();
					}
					else
					{
						valString = "0";
					}
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
				else if (valString.startsWith("CLASSLEVEL="))
				{
					valString = valString.substring(11).replace('{', '(').replace('}', ')');
					final PCClass aClass = getClassNamed(valString);
					if (aClass != null)
					{
						valString = aClass.getLevel().toString();
					}
					else
					{
						valString = "0";
					}
				}
				else if (valString.equals("TL"))
				{
					valString = Integer.toString(getTotalLevels());
				}
				else if (valString.equals("HD"))
				{
					// check companionModList?
					valString = Integer.toString(totalHitDice());
				}
				else if (valString.equals("SHIELDACHECK"))
				{
					ArrayList aArrayList = getEquipmentOfType("Shield", 1);
					if (aArrayList.size() > 0)
					{
						valString = ((Equipment)aArrayList.get(0)).acCheck().toString();
					}
					else
					{
						valString = "0";
					}
				}
				else if (valString.equals("SIZE"))
				{
					valString = String.valueOf(sizeInt());
				}
				else if (valString.equals("SIZEMOD"))
				{
					valString = String.valueOf(modForSize());
				}
				else if (valString.equals("ENCUMBERANCE"))
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
				else if (valString.equals("GRAPPLESIZEMOD"))
				{
					valString = String.valueOf(grappleModForSize());
				}
				else if (valString.equals("HIDESIZEMOD"))
				{
					valString = String.valueOf(hideModForSize());
				}
				else if (valString.equals("MOVEBASE"))
				{
					valString = getRace().getMovement().toString();
				}
				else if (valString.equals("COUNT[ATTACKS]"))
				{
					valString = Integer.toString(getNumAttacks());
				}
				else if (valString.equals("COUNT[CHECKS]"))
				{
					valString = String.valueOf(Globals.getCheckList().size());
				}
				else if (valString.equals("COUNT[FOLLOWERS]"))
				{
					valString = Integer.toString(getFollowerList().size());
				}
				else if (valString.equals("COUNT[STATS]"))
				{
					valString = Integer.toString(Globals.s_ATTRIBLONG.length);
				}
				else if (valString.equals("COUNT[SKILLS]"))
				{
					getSkillList().trimToSize();
					valString = Integer.toString(getSkillList().size());
				}
				else if (valString.equals("COUNT[FEATS]"))
				{
					featList.trimToSize();
					valString = Integer.toString(featList.size()).toString();
				}
				else if (valString.equals("COUNT[FEATSALL]"))
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
							final Feat aFeat = (Feat)e1.next();
							for (Iterator e2 = featTypes.iterator(); e2.hasNext();)
							{
								final String featType = (String)e2.next();
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
				else if (valString.equals("COUNT[SPELLCLASSES]"))
				{
//					int count = getSpellClassCount();
//					for (int iii = 0; iii < classList.size(); ++iii)
//					{
//						final PCClass aClass = (PCClass)classList.get(iii);
//						if (!aClass.getSpellType().equalsIgnoreCase(Constants.s_NONE))
//							count++;
//					}
					valString = String.valueOf(getSpellClassCount());
				}
				else if (valString.equals("COUNT[CLASSES]"))
				{
					classList.trimToSize();
					valString = Integer.toString(classList.size());
				}
				else if (valString.equals("COUNT[DOMAINS]"))
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
							Equipment eq = (Equipment)e.next();
							if (!eq.getHasHeaderParent())
							{
								aList.add(eq);
							}
						}
					}
					if (valString.equals("COUNT[EQUIPMENT]"))
					{
						valString = Integer.toString(aList.size());
					}
					else
					{
						StringTokenizer bTok = new StringTokenizer(valString.substring(16, valString.length() - 1), ".", false);
						while (bTok.hasMoreTokens())	//should be ok, assumes last two fields are # and a Param
						{
							String bString = bTok.nextToken();
							if (bString.equalsIgnoreCase("NOT"))
							{
								aList = new ArrayList(removeEqType(aList, bTok.nextToken()));
							}
							else if (bString.equalsIgnoreCase("ADD"))
							{
								aList = new ArrayList(addEqType(aList, bTok.nextToken()));
							}
							else if (bString.equalsIgnoreCase("IS"))
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
					if (aType.equals("Container"))
					{
						aList.clear();
						if (!equipmentList.isEmpty())
						{
							for (Iterator e = equipmentList.iterator(); e.hasNext();)
							{
								Equipment eq = (Equipment)e.next();
								if (eq.getHasHeaderParent() || eq.acceptsChildren())
								{
									aList.add(eq);
								}
							}
						}
					}
					else
					{
						if (aType.equalsIgnoreCase("weapon"))
						{
							aList = getExpandedWeapons();
						}
						else if (aType.equalsIgnoreCase("ACITEM"))
						{
							// special check for ACITEM which is realy anything with AC in the bonus section, but is not type SHIELD or ARMOR
							if (!equipmentList.isEmpty())
							{
								for (Iterator e = equipmentList.iterator(); e.hasNext();)
								{
									Equipment eq = (Equipment)e.next();
									if (((eq.getBonusListString()).indexOf("|AC|") != -1) && !eq.isType("ARMOR") && !eq.isType("SHIELD"))
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
						if (bString.equalsIgnoreCase("NOT"))
						{
							aList = new ArrayList(removeEqType(aList, bTok.nextToken()));
						}
						else if (bString.equalsIgnoreCase("ADD"))
						{
							aList = new ArrayList(addEqType(aList, bTok.nextToken()));
						}
						else if (bString.equalsIgnoreCase("IS"))
						{
							aList = new ArrayList(removeNotEqType(aList, bTok.nextToken()));
						}
						else if (bString.equalsIgnoreCase("EQUIPPED") || bString.equalsIgnoreCase("NOTEQUIPPED"))
						{
							final boolean eFlag = bString.equalsIgnoreCase("EQUIPPED");
							for (int ix = aList.size() - 1; ix >= 0; ix--)
							{
								Equipment anEquip = (Equipment)aList.get(ix);
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
				else if (valString.equals("COUNT[CONTAINERS]"))
				{

					ArrayList aList = new ArrayList();
					if (!equipmentList.isEmpty())
					{
						aList.clear();
						for (Iterator e = equipmentList.iterator(); e.hasNext();)
						{
							Equipment eq = (Equipment)e.next();
							if (eq.getHasHeaderParent() || eq.acceptsChildren())
							{
								aList.add(eq);
							}
						}

					}
					valString = Integer.toString(aList.size());
					aList.clear();
				}
				else if (valString.equals("COUNT[SA]"))
				{
					specialAbilityList.trimToSize();
					valString = new Integer(getSpecialAbilityTimesList().size()).toString();
				}
				else if (valString.equals("COUNT[LANGUAGES]"))
				{
					valString = Integer.toString(getLanguagesList().size());
				}
				else if (valString.equals("COUNT[NOTES]"))
				{
					valString = Integer.toString(getNotesList().size());
				}
				else if (valString.equals("CL") && src.startsWith("CLASS:"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getLevel().toString();
					}
					else
					{
						valString = "0";
					}
				}
				else if (valString.startsWith("EQTYPE"))
				{
					Globals.debugPrint("PlayerCharacter::getVariableValue(" + aString + "," + src + ") : " + valString + "\n");
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

				else if (valString.length() > 0)
				{
					if (hasVariable(valString))
					{
						valString = getVariable(valString, true, true, "", "").toString();
					}
					else
					{
						double a = 0;
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
						endMode += 1;
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
					else if (aString.charAt(i) == '+')
					{
						nextMode = 0;
					}
					else if (aString.charAt(i) == '-')
					{
						nextMode = 1;
					}
					else if (aString.charAt(i) == '*')
					{
						nextMode = 2;
					}
					else if (aString.charAt(i) == '/')
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
	public void rollStats(int method)
	{
		int roll = 0;
/////////////////////////////////////////////////
// Yanked for WotC compliance
//String diceExpression = SettingsHandler.getRollMethodExpression(method);
/////////////////////////////////////////////////

		for (Iterator stat = statList.getStats().iterator(); stat.hasNext();)
		{
			PCStat currentStat = (PCStat)stat.next();
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
		if (skill.getUntrained().startsWith("Y") && skill.isExclusive().startsWith("Y"))
		{
			if (skill.isClassSkill(classList))
			{
				UntrainedExclusiveClass = true;
			}
		}

		return (level == 2) || skill.isRequired() ||
		  (skill.getTotalRank().floatValue() > 0) ||
		  ((level == 1) && skill.getUntrained().startsWith("Y") &&
		  !skill.isExclusive().startsWith("Y")) ||
		  ((level == 1) && UntrainedExclusiveClass);
	}

	private final void addNewSkills(int level)
	{
		List addItems = new LinkedList();
		Iterator skillIter = Globals.getSkillList().iterator();
		Skill aSkill = null;
		while (skillIter.hasNext())
		{
			aSkill = (Skill)skillIter.next();
			if (includeSkill(aSkill, level) && (Globals.binarySearchPObject(getSkillList(), aSkill.getKeyName()) == null))
			{
				addItems.add(aSkill.clone());
			}
		}
		getSkillList().addAll(addItems);
	}

	private final void removeExcessSkills(int level)
	{
		Iterator skillIter = getSkillList().iterator();
		Skill skill = null;
		while (skillIter.hasNext())
		{
			skill = (Skill)skillIter.next();
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
		Equipment eq = null;
		for (Iterator e = getEquipmentOfType("Armor", 1).iterator(); e.hasNext();)
		{
			eq = (Equipment)e.next();
			if (!isProficientWith(eq))
			{
				bonus += eq.acCheck().intValue();
			}
		}
		if (!hasFeat("Shield Proficiency") && !hasFeatAutomatic("Shield Proficiency"))
		{
			ArrayList aArrayList = getEquipmentOfType("Shield", 1);
			if (aArrayList.size() > 0)
			{
				Equipment eq2 = (Equipment)aArrayList.get(0);
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
		int used = 0;
		int old = 0;
		if (typeName.equals("MAXDEX"))
		{
			bonus = getStatBonusTo("MISC", "MAXDEX");
		}
		int load = Constants.LIGHT_LOAD;
		if (SettingsHandler.isApplyLoadPenaltyToACandSkills())
			Globals.loadTypeForLoadScore(getVariableValue("LOADSCORE", "").intValue(), totalWeight());

		if (load == Constants.MEDIUM_LOAD && typeName.equals("ACCHECK"))
		{
			old = -3;
		}
		else if (load == Constants.HEAVY_LOAD && typeName.equals("ACCHECK"))
		{
			old = -6;
		}
		else if (load == Constants.MEDIUM_LOAD && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 3;
		}
		else if (load == Constants.HEAVY_LOAD && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 1;
		}
		else if (load == Constants.OVER_LOAD && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 0;
		}
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (!eq.isEquipped())
			{
				continue;
			}
			if (typeName.equals("AC"))
			{
				bonus += eq.getACMod().intValue();
			}
			else if (typeName.equals("ACCHECK"))
			{
				bonus += eq.acCheck().intValue();
			}
			else if (typeName.equals("SPELLFAILURE"))
			{
				bonus += eq.spellFailure().intValue();
			}
			else if (typeName.equals("MAXDEX"))
			{
				old = eq.getMaxDex().intValue();
				if (old == 100)
				{
					continue;
				}
				if (used == 0 || bonus > old)
				{
					bonus = old;
				}
				used = 1;
			}
		}
		if (typeName.equals("SPELLFAILURE"))
    {
			bonus += getTotalBonusTo("MISC", "SPELLFAILURE", true);
    }
		if (typeName.equals("ACCHECK"))
		{
			bonus = Math.min(bonus, old);
			bonus += getTotalBonusTo("MISC", "ACCHECK", true);
		}
		if (typeName.equals("MAXDEX"))
		{
			bonus += getTotalBonusTo("MISC", "MAXDEX", true);
			if (bonus < 0)
				bonus = 0;
		}
		return bonus;
	}

	private int getWeaponProfBonusTo(String aType, String aName)
	{
		int bonus = 0;
		if (hasWeaponProfNamed(aType))
		{
			WeaponProf wp = Globals.getWeaponProfNamed(aType);
			if (wp != null)
			{
				bonus = wp.bonusTo(aType, aName);
			}
		}
		return bonus;
	}

	private int getArmorProfBonusTo(String aType, String aName)
	{
		int bonus = 0;
		if (hasArmorProfType(aType))
		{
			ArmorProf ap = Globals.getArmorProfType(aType);
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
			Equipment eqI = (Equipment)e.next();
			if (eqI.getName().equals(eq.getName()) &&  eqI.getHand() == eq.getHand())
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
			Equipment eqI = (Equipment)e.next();
			if (eqI.getName().equals(eq.getName()) && eqI.getHand() == eq.getHand())
			{
				return true;
			}
		}
		return false;
	}

	private boolean sensitiveCheck()
	{
		boolean foundIt = false;
		ArrayList aFeatList = aggregateFeatList();
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
			Feat aFeat = (Feat)e1.next();
			if (aFeat.getName().equals("Force Sensitive"))
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
						File descriptionFile = new File(aFile.getPath() + File.separator + aString);
						if (descriptionFile.exists())
						{
							byte[] inputLine;
							FileInputStream aStream = new FileInputStream(descriptionFile);
							int length = (int)descriptionFile.length();
							inputLine = new byte[length];
							aStream.read(inputLine, 0, length);
							descriptionLst += new String(inputLine);
						}
					}
					catch (IOException exception)
					{
					}

				}
				else if (aFile.isDirectory())
					loadDescriptionFilesInDirectory(aFile.getPath() + File.separator + aString);
				return false;
			}
		});
		return false;
	}

	public void setClassList(ArrayList classList)
	{
		this.classList = classList;
	}
}

