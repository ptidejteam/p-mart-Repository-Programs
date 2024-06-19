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
 * Last Edited: $Date: 2006/02/20 23:57:30 $
 *
 */

package pcgen.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.EquipSet;
import pcgen.core.spell.Spell;
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
	// Bryan wanted this to be optional, but if you can reassign racial auto feats,
	// when you reopen the character, you get the feats that were exchanged back
	//
	private boolean canReassignRacialFeats()
	{
		return false;
	}

	private boolean canReassignTemplateFeats()
	{
		return false;
	}

	///////////////////////////////////////
	//attributes

	private static int loopValue = 0;
	private static String loopVariable = "";
	private static int decrement;
	private static BigDecimal BIG_ONE = new BigDecimal("1.00");

	private int currentHP = 0;
	private int nonProficiencyPenalty = -4;
	private boolean importing = false;
	private int alignment = 9; // 0 = LG to 8 = CE and 9 is <none selected>
	private TreeSet weaponProfList = new TreeSet();

	private String name = new String();
	private String playersName = new String();

	private int skillPoints = 0; // pool of skills remaining to distribute
	private int feats = 0; // pool of feats remaining to distribute
	private int[] stats = new int[Globals.s_ATTRIBLONG.length];
	private int remainingPool = 0;
	private int costPool = 0;
	private int poolAmount = 0; // pool of stats remaining to distribute
	private int height = 0; // in inches
	private int weight = 0; // in pounds
	private int age = 0; // in years
	private String gender = "M";
	private String handed = "Right";

	// Collections of String (probably should be full objects)
	private ArrayList specialAbilityList = new ArrayList();

	// We don't want this list sorted until after it has been added to the character
	// The reason is that sorting prevents .CLEAR-TEMPLATES from clearing the OLDER template languages.
	// ---arcady june 1, 2002
	private ArrayList templateAutoLanguages = new ArrayList();
//	private TreeSet templateAutoLanguages = new TreeSet();
	private TreeSet templateLanguages = new TreeSet();
	private TreeSet languages = new TreeSet();
	private TreeSet favoredClasses = new TreeSet();
	private String racialFavoredClass = "";
	private ArrayList miscList = new ArrayList(3);
	private ArrayList spellBooks = new ArrayList();
	private ArrayList variableList = new ArrayList();
	private ArrayList qualifyArrayList = new ArrayList();
	private boolean qualifyListStable = false;
	private HashMap bonusMap = new HashMap();

	private String visionOutput = "Normal";
	private String eyeColor = "";
	private String skinColor = "";
	private String hairColor = "";
	private String hairStyle = "";
	private String speechTendency = "";
	private String phobias = "";
	private String interests = "";
	private String catchPhrase = "";
	private String trait1 = "";
	private String trait2 = "";
	private String residence = "";
	private String location = "";
	private boolean inLabel = false;
	private boolean canWrite = true;
	private BigDecimal gold = new BigDecimal("0.00");
	private String bio = new String();
	private String description = new String();
	private boolean existsOnly = false;
	private boolean noMoreItems = false;
	private boolean checkBefore = false;
	// weather to add auto known spells each level
	private boolean autoKnownSpells = true;
	private Integer experience = new Integer(0);
	private int weaponMod = 0;
	private String[] movementTypes;
	private Integer[] movements;
	private boolean dirtyFlag = false; //Whether the character has changed since last saved.
	private String fileName = ""; //This may be different from character name...
	private String portraitPath = ""; //This may be different from character name...
	private int bonusWeaponChoices = 0;
	private int freeLangs = 0;
	private String FPoints = "0";
	private String DPoints = "0";

	private String csheetTag2 = "\\";
	private HashMap loopVariables = new HashMap();

	/** Only access this through getStableAggregateFeatList */
	private ArrayList stableAggregateFeatList = null;
	private ArrayList stableVirtualFeatList = null;
	private ArrayList stableAutomaticFeatList = null;

	/** Whether one can trust the most recently calculated aggregateFeatList.*/
	private boolean aggregateFeatsStable = false;
	private boolean virtualFeatsStable = false;
	private boolean automaticFeatsStable = false;

	private Race race = null;
	private ArrayList templateList = new ArrayList(); // of Template
	private ArrayList classList = new ArrayList(); // of Class
	private ArrayList featList = new ArrayList(); // of Feat
	private ArrayList characterDomainList = new ArrayList(); // of CharacterDomain

	private Deity deity = null;
	private ArrayList skillList = new ArrayList(); // of Skill
	//private Equipment primaryWeapon = null;
	//private Equipment secondaryWeapon[] = new Equipment[1];
	private ArrayList primaryWeapons = new ArrayList();
	private ArrayList secondaryWeapons = new ArrayList();
	private ArrayList equipmentList = new ArrayList(); // of Equipment
	private ArrayList notesList = new ArrayList();
	private ArrayList equipSetList = new ArrayList(); // of Equip Sets

	private String currentEquipSetName = "";

	private boolean useMonsterDefault = Globals.isMonsterDefault();


	private static final String s_CHECKLOADEDCAMPAIGNS = ". Check loaded campaigns.";

	// Added by ROG
	private String descriptionLst = "EMPTY";

	/*
         * AC calculations moved to ACCalculator
         *
         * author: Thomas Behr 07-02-02
         */
	private ACCalculator acCalculator;

	///////////////////////////////////////////////////////////////////
	// Accessor methods

	/*
	 */
	public int doOffhandMod(int myMod)
	{
		if (((myMod / 2) * 2) == myMod)
		{
			return ((myMod / 2) + getTotalBonusTo("COMBAT", "SECONDARYDAMAGE", true));
		}
		else
		{
			if (getTotalBonusTo("COMBAT", "SECONDARYDAMAGE", true) == 0)
				return (myMod / 2);
			else
				return ((myMod / 2) + getTotalBonusTo("COMBAT", "SECONDARYDAMAGE", true) + 1);
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
	 * <br>author: Thomas Behr 09-03-02
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
	 * <br>author: arcady June 4, 2002
	 *
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
			return npp;
		else
			return nonProficiencyPenalty;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 09-03-02
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
	 * <br>author: Thomas Behr 09-03-02
	 *
	 * @return primary weapons
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
					aDomain.addSelectedWeaponProfBonusTo(wp);
			}
		}
		return wp;
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

	public int getAlignment()
	{
		return alignment;
	}

	public void setCurrentHP(int currentHP)
	{
		this.currentHP = currentHP;
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
	 *
	 * Build on-the-fly so removing templates won't mess up qualify list
	 */
	public ArrayList getQualifyList()
	{
		if (!qualifyListStable)
		{
			qualifyArrayList = new ArrayList();
			for (int i = 0; i < templateList.size(); i++)
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

	public String getFPoints()
	{
		return FPoints;
	}

	public void setFPoints(String aString)
	{
		if (sensitiveCheck())
		{
			FPoints = new String(aString);
		}
		else
		{
			if (Integer.parseInt(aString) > 5)
			{
				FPoints = "5";
			}
			else
			{
				FPoints = new String(aString);
			}
		}
		Globals.debugPrint("ForcePoints:" + FPoints);
	}


	public String getDPoints()
	{
		return DPoints;
	}

	public void setDPoints(String aString)
	{
		DPoints = new String(aString);
		Globals.debugPrint("Darkside Points:" + DPoints);
	}

	public HashMap getBonusMap()
	{
		return bonusMap;
	}

	public void setEquipmentList(ArrayList eqList)
	{
		equipmentList = eqList;
	}

	public ArrayList getEquipmentList()
	{
		return equipmentList;
	}

	public Equipment getEquipmentNamed(String aString)
	{
		if (equipmentList.isEmpty())
			return null;
		Equipment match = null;
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (aString.equals(eq.getKeyName()) ||
				aString.equals(eq.getName()))
				match = eq;
		}
		if (match != null)
			return match;
		return null;
	}

	public void equipmentListAddAll(ArrayList aList)
	{
		if (aList.isEmpty())
			return;
		equipmentList.addAll(aList);
	}

	public ArrayList getEquipSet()
	{
		return equipSetList;
	}

	public EquipSet getEquipSetByName(String aName)
	{
		if (equipSetList.isEmpty())
			return null;
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet eSet = (EquipSet)e.next();
			if (eSet.getName().equals(aName))
				return eSet;
		}
		return null;
	}

	public EquipSet getEquipSetByIdPath(String id)
	{
		if (equipSetList.isEmpty())
			return null;
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet eSet = (EquipSet)e.next();
			if (eSet.getIdPath().equals(id))
				return eSet;
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
			return false;
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
	 * <br>author: Thomas Behr 08-03-02
	 *
	 * Build on-the-fly so removing templates won't mess up subrace
	 *
	 * @return character subrace
	 */
	public String getSubRace()
	{
		String subRace = Constants.s_NONE;
		for (int i = 0; i < templateList.size(); i++)
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
	 * <br>author: Thomas Behr 08-03-02
	 *
	 * Build on-the-fly so removing templates won't mess up region
	 *
	 * @return character region
	 */
	public String getRegion()
	{
		String region = Constants.s_NONE;
		for (int i = 0; i < templateList.size(); i++)
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
	 * <br>author: Thomas Behr 08-03-02
	 *
	 * Build on-the-fly so removing templates won't mess up sub region
	 *
	 * @return character sub region
	 */
	public String getSubRegion()
	{
		String subregion = Constants.s_NONE;
		for (int i = 0; i < templateList.size(); i++)
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
	 * <br>author: arcady may 28 2002
	 *
	 * Build on-the-fly so removing templates won't mess up region
	 *
	 * @return character region
	 */
	public String getFullRegion()
	{
		String subregion = getSubRegion();
		String tempRegName = getRegion();

		if (!subregion.equals(Constants.s_NONE))
			tempRegName += " (" + subregion + ")";
		return tempRegName;
	}

	/**
	 * Selector
	 *
	 * <br>author: arcady May 31 2002
	 *
	 * Build on-the-fly so removing templates won't mess up height
	 *
	 * @return character Height from templates
	 */
	public String getTHeight()
	{
		String tHeight = Constants.s_NONE;
		for (int i = 0; i < templateList.size(); i++)
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
	 * <br>author: arcady May 31 2002
	 *
	 * Build on-the-fly so removing templates won't mess up height
	 *
	 * @return character Weight from templates
	 */
	public String getTWeight()
	{
		String tWeight = Constants.s_NONE;
		for (int i = 0; i < templateList.size(); i++)
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
	 * <br>author: arcady May 31 2002
	 *
	 * Build on-the-fly so removing templates won't mess up height
	 *
	 * @return character Age from templates
	 */
	public String getTAge()
	{
		String tAge = Constants.s_NONE;
		for (int i = 0; i < templateList.size(); i++)
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
	 * <br>author: Thomas Behr 18-04-02
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
	 * <br>author: Thomas Behr 18-04-02
	 *
	 * @param newPortraitPath   the path to the portrait file
	 */
	public void setPortraitPath(String newPortraitPath)
	{
		portraitPath = newPortraitPath;
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
	}

	public String getPlayersName()
	{
		return playersName;
	}

	public void setPlayersName(String aString)
	{
		playersName = aString;
	}

	//public int getBonusWeaponChoices()
	//{
	//	if (bonusWeaponChoices == 0)
	//		getBonusWeaponProfs();
	//	return bonusWeaponChoices;
	//}

	public int getSkillPoints()
	{
		return skillPoints;
	}

	public void setSkillPoints(int anInt)
	{
		skillPoints = anInt;
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
					initFeats += template.getBonusInitialFeats();
			}
		}
		return initFeats;
	}

	public int getFeats()
	{
		//if (totalLevels() == 0)
		//	return getInitialFeats();
		//else
		return feats;
	}

	public void setFeats(int argFeats)
	{
		feats = argFeats;
	}

	public int getCurrentHP()
	{
		return currentHP;
	}

	public int[] getStats()
	{
		return stats;
	}

	public int getStat(int idx)
	{
		int stat = 0;
		if ((idx >= 0) && (idx < Globals.s_ATTRIBLONG.length))
		{
			if ((idx == Constants.HONOR) && Globals.isHackMasterMode())
			{
				for (idx = 0; idx < Globals.s_ATTRIBLONG.length; idx++)
				{
					if (idx != Constants.HONOR)
					{
						stat += stats[idx];
					}
				}
				//
				// round to nearest int: stat/7 + 1/2 --> [stat + 7/2]/7 --> [2*stat + 7]/14
				//
				stat = ((2 * stat) + (Globals.s_ATTRIBLONG.length - 1)) / (2 * (Globals.s_ATTRIBLONG.length - 1));
			}
			else
			{
				int x = getVariableValue("LOCK."+Globals.s_ATTRIBSHORT[idx], "").intValue();
				if (x!=0)
					return x;
				x = getVariableValue("BASE."+Globals.s_ATTRIBSHORT[idx], "").intValue();
				if (x!=0)
					return x;
				return stats[idx];
			}
		}
		return stat;
	}

	public void setStats(int[] intArray)
	{
		stats = intArray;
	}

	public void setStat(int idx, int stat)
	{
		if ((idx >= 0) && (idx < Globals.s_ATTRIBLONG.length))
			stats[idx] = stat;
	}

	public int getPoolAmount()
	{
		return poolAmount;
	}

	public void setPoolAmount(int anInt)
	{
		poolAmount = anInt;
	}

	public int getCostPool()
	{
		return costPool;
	}

	public void setCostPool(int i)
	{
		costPool = i;
	}

	public int getRemainingPool()
	{
		return remainingPool;
	}

	public void setRemainingPool(int pool)
	{
		remainingPool = pool;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int i)
	{
		age = i;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int i)
	{
		height = i;
	}

	public int getWeight()
	{
		return weight;
	}

	public void setWeight(int i)
	{
		weight = i;
	}

	public String getTGender()
	{
		String tGender = Constants.s_NONE;
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
			String aString;
			aString = aTemplate.getGenderLock();
			if (!aString.equals(Constants.s_NONE))
				tGender = aString;
		}
		return tGender;
	}

	public String getGender()
	{
		String tGender = getTGender();

		if (tGender.equals(Constants.s_NONE))
			return gender;
		else
			return tGender;
	}

	public void setGender(String aString)
	{
		String tGender = getTGender();
		if (tGender.equals(Constants.s_NONE))
			gender = aString;
		else
			gender = tGender;
	}

	public ArrayList getSpecialAbilityList()
	{
		return specialAbilityList;
	}

	/*
         * same as getSpecialAbilityList except if
         * if you have the same ability twice, it only
         * lists it once with (2) at the end.
         */
	public ArrayList getSpecialAbilityTimesList()
	{
		ArrayList aList = new ArrayList();
		int[] times = new int[specialAbilityList.size()];
		Arrays.fill(times, 0);

		if (!specialAbilityList.isEmpty())
		{
			for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
			{
				String aString = (String)i.next();
				boolean found = false;
				int idx = 0;
				for (Iterator ii = aList.iterator(); ii.hasNext();)
				{
					if (aString.equals((String)ii.next()))
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

		if (!templateList.isEmpty())
		{
			for (int y = 0; y < templateList.size(); y++)
			{
				ArrayList SAs = (ArrayList)((PCTemplate)templateList.get(y)).getSAs(totalLevels(), totalHitDice(), getSize());
				times = Utility.resize(times, SAs.size());
				for (int z = 0; z < SAs.size(); z++)
				{
					String aString = (String)SAs.get(z);
					boolean found = false;
					int idx = 0;
					if (!aList.isEmpty())
					{
						for (Iterator ii = aList.iterator(); ii.hasNext();)
						{
							if (aString.equals((String)ii.next()))
							{
								found = true;
								break;
							}
							idx++;
						}
					}
					if (!found)
						aList.add(aString);
					times[idx] += 1;
				}
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
		for (int idx = 0; idx < aList.size(); idx++)
		{
			StringTokenizer varTok = new StringTokenizer((String)aList.get(idx), "|", false);
			String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();
			if (varCount != 0)
			{
				varValue = new int[varCount];
				for (int i = 0; i < varCount; i++)
				{
					final String vString = varTok.nextToken();
					varValue[i] = getVariableValue(vString, "").intValue();
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
				times[idx] = 0;
			}
			if (times[idx] > 1)
			{
				newAbility.append(" (").append(Integer.toString(times[idx])).append(')');
			}
			aList.set(idx, newAbility.toString());
		}


//			String aString = (String)aList.get(idx);
//			final int pos_pipe = aString.lastIndexOf("|");
//			final int pos_perc = aString.lastIndexOf("%");
//			if (pos_pipe >= 0 && pos_perc == -1)
//			{
//				System.out.println("Bad SA: tag '" + aString + "'");
//				// The Archer Class' use of the ADD:WEAPONBONUS(TOHIT|2+((CL=Archer)/5) tag is causing this to pop up. It shouldn't.
//				// GuiFacade.showMessageDialog(null, "Bad SA: tag '" + aString + "'", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
//				continue;
//			}
//			if (pos_pipe >= 0)
//			{
//				final int sInt = getVariableValue(aString.substring(pos_pipe + 1), "").intValue();
//				final StringTokenizer aTok = new StringTokenizer(aString.substring(0, pos_pipe), "%", true);
//				final StringBuffer newAbility = new StringBuffer();
//				while (aTok.hasMoreTokens())
//				{
//					final String nextTok = aTok.nextToken();
//					if (nextTok.equals("%"))
//						newAbility.append(Integer.toString(sInt));
//					else
//						newAbility.append(nextTok);
//				}
//				aString = newAbility.toString();
//				aList.set(idx, aString);
//				if (sInt == 0)
//				{
//					times[idx] = 0;
//				}
//			}
//			if (times[idx] > 1)
//			{
//				aList.set(idx, aString + " (" + times[idx] + ")");
//			}
//		}

		//
		// Remove any abilities whose occurance is 0 after calculating expression
		//
		for (int idx = aList.size() - 1; idx >= 0; idx--)
		{
			if (times[idx] == 0)
			{
				aList.remove(idx);
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
	}

	public TreeSet getLanguagesList()
	{
		return languages;
	}

	public String getEyeColor()
	{
		return eyeColor;
	}

	public void setEyeColor(String aString)
	{
		eyeColor = aString;
	}

	public String getSkinColor()
	{
		return skinColor;
	}

	public void setSkinColor(String aString)
	{
		skinColor = aString;
	}

	public String getHairColor()
	{
		return hairColor;
	}

	public void setHairColor(String aString)
	{
		hairColor = aString;
	}

	public String getHairStyle()
	{
		return hairStyle;
	}

	public void setHairStyle(String aString)
	{
		hairStyle = aString;
	}

	public String getSpeechTendency()
	{
		return speechTendency;
	}

	public void setSpeechTendency(String aString)
	{
		speechTendency = aString;
	}

	public String getPhobias()
	{
		return phobias;
	}

	public void setPhobias(String aString)
	{
		phobias = aString;
	}

	public String getInterests()
	{
		return interests;
	}

	public void setInterests(String aString)
	{
		interests = aString;
	}

	public String getCatchPhrase()
	{
		return catchPhrase;
	}

	public void setCatchPhrase(String aString)
	{
		catchPhrase = aString;
	}

	public String getTrait1()
	{
		return trait1;
	}

	public void setTrait1(String aString)
	{
		trait1 = aString;
	}

	public String getTrait2()
	{
		return trait2;
	}

	public void setTrait2(String aString)
	{
		trait2 = aString;
	}

	public String getResidence()
	{
		return residence;
	}

	public void setResidence(String aString)
	{
		residence = aString;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String aString)
	{
		location = aString;
	}

	public BigDecimal getGold()
	{
		return gold;
	}

	public void setGold(String aString)
	{
		gold = new BigDecimal(aString);
	}

	public String getBio()
	{
		return bio;
	}

	public void setBio(String aString)
	{
		bio = aString;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String aString)
	{
		description = aString;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 09-03-02
	 *
	 * @return description lst
	 */
	public String getDescriptionLst()
	{
		return descriptionLst;
	}

	public Integer getExperience()
	{
		return experience;
	}

	public void setExperience(Integer anInt)
	{
		//experience = new Integer(anInt.toString());
		//The above line is unneccessary as Integers are immutable
		experience = anInt;
	}

	public ArrayList getMiscList()
	{
		return miscList;
	}

	public ArrayList getSpellBooks()
	{
		return spellBooks;
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
		if (featList.isEmpty())
			return 0;
		int iCount = 0;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			int subfeatCount = aFeat.getAssociatedCount();
			if (subfeatCount > 1)
			{
				iCount += subfeatCount;
			}
			else
			{
				iCount += 1;
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
					break;
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
				characterDomainList.remove(i);
			else
			{
				System.err.println("WARNING:Total domains should be " + num + "!!!");
				break;
			}
		}
		return characterDomainList;
	}

	public ArrayList getSkillList()
	{
		return skillList;
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
		for (int i = 0; i < Globals.s_ATTRIBLONG.length; i++)
			stats[i] = 0;
		setRace((Race)Globals.getRaceMap().get(Constants.s_NONESELECTED));
		setName("");
		skillPoints = 0;
		feats = 0;
		rollStats(Globals.getRollMethod());
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
		populateSkills(Globals.getIncludeSkills());
		for (Iterator i = Globals.getBonusStackList().iterator(); i.hasNext();)
		{
			bonusMap.put((String)i.next(), "0");
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
	}

	/**
	 * freeing up resources
	 *
	 * <br>author: Thomas Behr 07-02-02
	 */
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
			critterType.append(race.getType());
		else
			critterType.append("Humanoid");

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
			for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
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

	private int getModifierForSize(String modName)
	{
		ArrayList aList = new ArrayList();
		aList.add("size");
		//
		// check for BONUS:ESIZE|NUMBER|-1 to see if we should
		// decrease or increase AC and HIDE modifiers
		// sizeMod can be either positive or negative number
		//
		int sizeMod = getTotalBonusTo("ESIZE", "NUMBER", true);
		if ((modName.equals("AcMod") || modName.equals("HideMod")) && (sizeMod != 0))
		{
			int iSize = sizeInt() + sizeMod;
			//
			// iSize must not be smaller than 0 or larger than 8
			//
			if (iSize < 0)
				iSize = 0;
			else if (iSize > 8)
				iSize = 8;
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
		return getModifierForSize("AcMod");
	}

	public int grappleModForSize()
	{
		return getModifierForSize("GrappleMod");
	}

	private int hideModForSize()
	{
		return getModifierForSize("HideMod");
	}

	private int getModifierForSizeIncrease(String modName)
	{
		ArrayList aList = new ArrayList();
		aList.add("size");
		int rSize = racialSizeInt();
		int iSize = sizeInt();
		int mod = 0;

		for (int x = rSize; x < iSize; x++)
		{
			mod += (int)Globals.sizeAdjustmentMultiplier(Constants.s_SIZESHORT[x], aList, modName);
		}

		return mod;
	}

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

	public int naturalArmorModForSize()
	{
		return getModifierForSizeIncrease("NaturalAcIncrease");
	}


	public PCClass getClassNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.getName().equalsIgnoreCase(aString))
			// IgnoreCase needed for class checks in getVariableValue ...  ---arcady 10/6/2001
				return aClass;
		}
		return null;
	}

	private PCClass getClassDisplayNamed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			if (aClass.getDisplayClassName().equalsIgnoreCase(aString))
			// IgnoreCase needed for class checks in getVariableValue ...  ---arcady 10/6/2001
				return aClass;
		}
		return null;
	}

	public PCClass getClassKeyed(String aString)
	{
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			final PCClass aClass = (PCClass)classIter.next();
			if (aClass.getKeyName().equals(aString))
				return aClass;
		}
		return null;
	}

	public PCTemplate getTemplateKeyed(String aString)
	{
		for (Iterator templateIter = templateList.iterator(); templateIter.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)templateIter.next();
			if (aTemplate.getKeyName().equals(aString))
				return aTemplate;
		}
		return null;
	}

	public PCTemplate getTemplateNamed(String aName)
	{
		for (Iterator templateIter = templateList.iterator(); templateIter.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)templateIter.next();
			if (aTemplate.getName().equals(aName))
				return aTemplate;
		}
		return null;
	}

	public PObject getSpellClassAtIndex(int ix)
	{
		PObject aObject = null;
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			aObject = (PObject)classIter.next();
			if (aObject.getFirstCharacterSpell(null, "", -1, null, null) != null)
				ix--;
			else
				aObject = null;
			if (ix == -1)
				break;
		}
		if (ix == -1 && aObject != null)
			return aObject;
		if (ix == 0 && race.getFirstCharacterSpell(null, "", -1, null, null) != null)
			return race;
		return null;
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
				for (int i = 0; i < obj.getVariableCount(); i++)
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

		if (!skillList.isEmpty())
		{
			for (Iterator e = skillList.iterator(); e.hasNext();)
			{
				final Skill obj = (Skill)e.next();
				for (int i = 0; i < obj.getVariableCount(); i++)
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
				for (int i = 0; i < obj.getVariableCount(); i++)
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
						for (int i = 0; i < eqMod.getVariableCount(); i++)
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
						for (int i = 0; i < eqMod.getVariableCount(); i++)
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
				for (int i = 0; i < obj.getVariableCount(); i++)
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

		if (deity != null)
		{
			for (int i = 0; i < deity.getVariableCount(); i++)
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
					for (int i = 0; i < cd.getDomain().getVariableCount(); i++)
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
			for (int i = 0; i < race.getVariableCount(); i++)
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
				final WeaponProf obj = Globals.getWeaponProfNamed((String)e.next());
				for (int i = 0; i < obj.getVariableCount(); i++)
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
		return false;
	}

	public Float getVariable(String variableString, boolean isMax, boolean includeBonus, String matchSrc, String matchSubSrc)
	{
		Float value = new Float(0.0);
		boolean found = false;
		if (!variableList.isEmpty())
		{
			for (Iterator e = variableList.iterator(); e.hasNext();)
			{
				final String vString = (String)e.next();
				final StringTokenizer aTok = new StringTokenizer(vString, "|", false);
				final String src = aTok.nextToken();
				if (matchSrc.length() > 0 && !src.equals(matchSrc))
					continue;

				final String subSrc = aTok.nextToken();
				if (matchSubSrc.length() > 0 && !subSrc.equals(matchSubSrc))
					continue;

				final String nString = aTok.nextToken();
				if (nString.equals(variableString))
				{
					final String sString = aTok.nextToken();
					final Float newValue = getVariableValue(sString, src);
					if (!found)
						value = newValue;
					else if (isMax)
						value = new Float(Math.max(value.doubleValue(), newValue.doubleValue()));
					else
						value = new Float(Math.min(value.doubleValue(), newValue.doubleValue()));
					found = true;
					if (!loopVariable.equals(""))
					{
						while (loopValue > decrement)
						{
							loopValue -= decrement;
							value = new Float(value.doubleValue() + getVariableValue(sString, src).doubleValue());
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
				String S = checkForVariableInList(obj, variableString, isMax, includeBonus, "", "", found, value);
				if (S.length() > 0)
				{
					found = true;
					value = new Float(S);
				}
			}
		}

		if (!skillList.isEmpty())
		{
			for (Iterator oi = skillList.iterator(); oi.hasNext();)
			{
				final Skill obj = (Skill)oi.next();
				String S = checkForVariableInList(obj, variableString, isMax, includeBonus, "", "", found, value);
				if (S.length() > 0)
				{
					found = true;
					value = new Float(S);
				}
			}
		}

		if (!equipmentList.isEmpty())
		{
			for (Iterator oi = equipmentList.iterator(); oi.hasNext();)
			{
				final Equipment obj = (Equipment)oi.next();
				String eS = checkForVariableInList(obj, variableString, isMax, includeBonus, "", "", found, value);
				if (eS.length() > 0)
				{
					found = true;
					value = new Float(eS);
				}
				ArrayList aList = obj.getEqModifierList(true);
				if (!aList.isEmpty())
				{
					for (Iterator el = aList.iterator(); el.hasNext();)
					{
						final EquipmentModifier em = (EquipmentModifier)el.next();
						String S = checkForVariableInList(em, variableString, isMax, includeBonus, "", "", found, value);
						if (S.length() > 0)
						{
							found = true;
							value = new Float(S);
						}
					}
				}
				aList = obj.getEqModifierList(false);
				if (!aList.isEmpty())
				{
					for (Iterator el = aList.iterator(); el.hasNext();)
					{
						final EquipmentModifier em = (EquipmentModifier)el.next();
						String S = checkForVariableInList(em, variableString, isMax, includeBonus, "", "", found, value);
						if (S.length() > 0)
						{
							found = true;
							value = new Float(S);
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
				String S = checkForVariableInList(obj, variableString, isMax, includeBonus, "", "", found, value);
				if (S.length() > 0)
				{
					found = true;
					value = new Float(S);
				}
			}
		}

		if (race != null)
		{
			String S = checkForVariableInList(race, variableString, isMax, includeBonus, "", "", found, value);
			if (S.length() > 0)
			{
				found = true;
				value = new Float(S);
			}
		}

		if (deity != null)
		{
			String S = checkForVariableInList(deity, variableString, isMax, includeBonus, "", "", found, value);
			if (S.length() > 0)
			{
				found = true;
				value = new Float(S);
			}
		}

		if (!characterDomainList.isEmpty())
		{
			for (Iterator oi = characterDomainList.iterator(); oi.hasNext();)
			{
				final CharacterDomain obj = (CharacterDomain)oi.next();
				if (obj.getDomain() == null)
					continue;
				String S = checkForVariableInList(obj.getDomain(), variableString, isMax, includeBonus, "", "", found, value);
				if (S.length() > 0)
				{
					found = true;
					value = new Float(S);
				}
			}
		}

		if (!weaponProfList.isEmpty())
		{
			for (Iterator oi = weaponProfList.iterator(); oi.hasNext();)
			{
				final WeaponProf obj = Globals.getWeaponProfNamed((String)oi.next());
				String S = checkForVariableInList(obj, variableString, isMax, includeBonus, "", "", found, value);
				if (S.length() > 0)
				{
					found = true;
					value = new Float(S);
				}
			}
		}

		if (!found)
			value = getVariableValue(variableString, "");

		if (includeBonus)
		{
			int i = getTotalBonusTo("VAR", variableString, true);
			value = new Float(value.doubleValue() + i);
		}
		return value;
	}

	public String checkForVariableInList(PObject obj, String variableString, boolean isMax, boolean includeBonus, String matchSrc, String matchSubSrc, boolean found, Float value)
	{
		boolean flag = false;
		for (int i = 0; i < obj.getVariableCount(); i++)
		{
			final String vString = obj.getVariable(i);
			final StringTokenizer aTok = new StringTokenizer(vString, "|", false);
			final String src = aTok.nextToken();
			if (matchSrc.length() > 0 && !src.equals(matchSrc))
				continue;

			if (matchSubSrc.length() > 0 || matchSrc.length() > 0)
			{
				final String subSrc = aTok.nextToken();
				if (matchSubSrc.length() > 0 && !subSrc.equals(matchSubSrc))
					continue;
			}
			if (!aTok.hasMoreTokens())
				continue;

			final String nString = aTok.nextToken();
			if (!aTok.hasMoreTokens())
				continue;

			if (nString.equals(variableString))
			{
				final String sString = aTok.nextToken();
				final Float newValue = getVariableValue(sString, src);
				if (!found)
					value = newValue;
				else if (isMax)
					value = new Float(Math.max(value.doubleValue(), newValue.doubleValue()));
				else
					value = new Float(Math.min(value.doubleValue(), newValue.doubleValue()));
				found = true;
				flag = true;
				if (!loopVariable.equals(""))
				{
					while (loopValue > decrement)
					{
						loopValue -= decrement;
						value = new Float(value.doubleValue() + getVariableValue(sString, src).doubleValue());
					}
					loopValue = 0;
					loopVariable = "";
				}
			}
		}
		if (flag)
			return value.toString();
		else
			return ""; // signifies that the variable was found in this list
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
				aArrayList.add(eq);
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
				aArrayList.add(eq);
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
	 * <br>author: Thomas Behr 12-04-02
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

	public int movement(int moveIdx)
	{
		int bonus = 0;

		// get racial base movement
		int move = getMovement(moveIdx).intValue();
		// get a list of all equipped Armor
		ArrayList aArrayList = getEquipmentOfType("Armor", 1);

		int pcLoad = Globals.loadTypeForStrength(adjStats(Constants.STRENGTH), totalWeight());

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
				// The following doesn't make sense. We already
				// have the pcload due to weight and are getting
				// the larger value compared to armor. Why set
				// the load to OVER_LOAD?
				//else
				//	pcLoad = Constants.OVER_LOAD;

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
			case Constants.MEDIUM_LOAD:
				bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUM", false);
			case Constants.HEAVY_LOAD:
				bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false);
		}
		move += bonus;
		return move;
	}

	/**
	 * returns the base AC due to selected race
	 *
	 * <br>author: Thomas Behr 07-02-02
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

	public int calcStatMod(int stat)
	{
		return (adjStats(stat) / 2) - 5;
	}

	public int calcUnAdjStatMod(int stat)
	{
		return (unAdjStats(stat) / 2) - 5;
	}

	public int initiativeMod()
	{
		int initmod = calcStatMod(Constants.DEXTERITY) + getRace().getInitMod().intValue();
		initmod += getTotalBonusTo("COMBAT", "Initiative", true);

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			initmod += aClass.initMod();
		}
		return initmod;
	}

	public String getAttackString(int index)
	{
		return getAttackString(index, 0);
	}

	public String getAttackString(int index, int bonus)
	{
		// 0 = melee; 1 = ranged; 2 = unarmed
		ArrayList ab = new ArrayList(10);
		int total = 0;
		int mod = getTotalBonusTo("TOHIT", "TOHIT", false) + bonus + getRace().getBAB();  //modForSize() removed from this because it doesn't affect the number of attacks.
		int attacks = 1;
		int subTotal = getRace().getBAB();
		int maxCycle = 0;
		StringBuffer attackString = new StringBuffer();
		for (total = 0; total < 10; total++)
			ab.add(new Integer(0));
		total = 0;
		int combat = getTotalBonusTo("COMBAT", "TOHIT", true);

		int nonTotal = getRace().getBAB();
		for (int i = 0; i < classList.size(); i++)
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
				nonTotal += b;
		}
		for (int x = 2; x < 10; x++)
			if (((Integer)ab.get(x)).intValue() > ((Integer)ab.get(attacks)).intValue())
				attacks = x;
		total = ((Integer)ab.get(attacks)).intValue();
		if (total == 0)
			attacks = 5;
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

	public String getUnarmedDamageString(boolean includeCrit, boolean includeStrBonus)
	{
		int i = 2;
		String retString = "1d2";
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			final String aString = aClass.getUdamForLevel(aClass.getLevel().intValue(), includeCrit, includeStrBonus);
			final StringTokenizer aTok = new StringTokenizer(aString, " dD+-(x)", false);
			int sides = 0;
			if (aTok.countTokens() > 1)
			{
				aTok.nextToken();
				sides = Integer.parseInt(aTok.nextToken());
			}
			if (sides > i)
			{
				i = sides;
				retString = aString;
			}
		}
		return retString;
	}

	public void setAlignment(int index, boolean bLoading)
	{
		setAlignment(index, bLoading, false);
	}

	public void setAlignment(int index, boolean bLoading, boolean bForce)
	{
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
				GuiFacade.showMessageDialog(null, "Invalid alignment. Setting to <none selected>", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
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
				for (int i = 0; i < movements.length; i++)
				{
					movements[i] = moveRate;
				}
			}
			else
			{ // add moveRate to all types of movement.
				for (int i = 0; i < movements.length; i++)
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
				for (int i = 0; i < movements.length; i++)
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

				for (int i = 0; i < movementsTemp.length; i++)
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
				for (int i = 0; i < movements.length; i++)
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
				for (int i = 0; i < movements.length; i++)
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

				for (int i = 0; i < movementsTemp.length; i++)
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

			if (templateMovements != null)
			{
				for (int i = 0; i < templateMovements.length; i++)
				{
					setMyMoveRates(templateMovementTypes[i], templateMovements[i].intValue(), aTemplate.getMoveRatesFlag());
				}
			}
		}
	}

	public String getVision()
	{
		return visionOutput;
	}

	/*
	 */
	public void doVision(String tmplVision, int flag)
	{
		if (flag == 2 || visionOutput.equals("Normal") || visionOutput.equals(tmplVision))
		{
			visionOutput = tmplVision;
		}
		else if (flag == 1 && !tmplVision.equals(Constants.s_NONE))
		{
			visionOutput += ", " + tmplVision;
		}
		else
		{
			StringTokenizer aaTok = new StringTokenizer(visionOutput, ", ", false);
			StringBuffer tempBuffer = new StringBuffer();
			while (aaTok.hasMoreTokens())
			{
				String aaaString = aaTok.nextToken();
				if (!aaaString.equals(tmplVision))
				{
					if (tempBuffer.length() == 0)
					{
						tempBuffer.append(aaaString);
					}
					else
					{
						if (aaaString.startsWith("("))
							tempBuffer.append(" ").append(aaaString);
						else
							tempBuffer.append(", ").append(aaaString);
					}
				}
			}
			visionOutput = tempBuffer.toString();
		}
		if (visionOutput.equals("") || visionOutput.equals(Constants.s_NONE))
		{
			visionOutput = "Normal";
		}
	}

	public void addTemplate(PCTemplate inTmpl)
	{
		addTemplate(inTmpl, true);
	}

	public void addTemplate(PCTemplate inTmpl, boolean addGold)
	{
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
		if (addGold)
		{
			gold = inTmpl.getGold(gold);
		}

		templateAutoLanguages.addAll(inTmpl.getLanguageAutos());
		templateLanguages.addAll(inTmpl.getLanguageBonus());
		getAutoLanguages();

		doVision(inTmpl.getVision(), inTmpl.getVisionFlag());

		inTmpl.chooseLanguageAutos(importing);
		if (canReassignTemplateFeats())
		{
			ArrayList templateFeats = inTmpl.feats(totalLevels(), totalHitDice(), getSize());
			int tFeats = templateFeats.size();
			for (int x = 0; x < tFeats; x++)
			{
				modFeatsFromList((String)templateFeats.get(x), true, false);
			}
		}
		else
		{
			setAutomaticFeatsStable(false);
		}

		ArrayList templates = inTmpl.getTemplates(importing);
		for (int y = 0; y < templates.size(); y++)
		{
			addTemplate(Globals.getTemplateNamed((String)templates.get(y)));
		}
		setQualifyListStable(false);
		adjustMoveRates();
		addTemplateSpells();
	}

	public void removeTemplate(PCTemplate inTmpl)
	{
		if (inTmpl == null)
			return;

		gold = inTmpl.cutGold(gold);
		weaponProfList.removeAll(inTmpl.weaponProfAutos());

		languages.removeAll(inTmpl.getLanguageAutos()); // remove template languages.
		templateAutoLanguages.removeAll(inTmpl.getLanguageAutos()); // remove them from the local listing. Don't clear though in case of multiple templates.

		templateLanguages.removeAll(inTmpl.getLanguageBonus());
		doVision(inTmpl.getVision(), 0);

		for (int y = 0; y < inTmpl.templatesAdded().size(); y++)
		{
			removeTemplate(getTemplateNamed((String)inTmpl.templatesAdded().get(y)));
		}
		for (int x = 0; x < templateList.size(); x++)
		{
			if (((PCTemplate)templateList.get(x)).getName().equals(inTmpl.getName()))
			{
				templateList.remove(x);
				break;
			}
		}
		if (!canReassignTemplateFeats())
		{
			setAutomaticFeatsStable(false);
		}
		setQualifyListStable(false);
		adjustMoveRates();
		// removing template spells
/// TODO
		for (int ri = inTmpl.getCharacterSpellCount() - 1; ri >= 0; ri--)
		{
//			removeCharacterSpell(inTmpl.getCharacterSpell(ri));
//			inTmpl.removeCharacterSpell(inTmpl.getCharacterSpell(ri));
		}
		inTmpl.clearCharacterSpells();
//		ArrayList aList = inTmpl.getCharacterSpellList();
	}

	public void incrementClassLevel(int mod, PCClass aClass)
	{
		incrementClassLevel(mod, aClass, false);
	}

	private void incrementClassLevel(int mod, PCClass aClass, boolean bSilent)
	{
		if (mod > 0)
		{
			if (!aClass.isQualified())
			{
				return;
			}
			if (aClass.isMonster() && !race.isAdvancementUnlimited() && totalHitDice() >= race.maxHitDiceAdvancement() && !bSilent)
			{
				GuiFacade.showMessageDialog(null, "Cannot increase Monster Hit Dice for this character beyond " + race.maxHitDiceAdvancement() + ". This characters current number of Monster Hit Dice is " + totalHitDice(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
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
				Set aSet = bClass.getLanguageAutos();
				languages.addAll(aSet);
				//getAutoWeaponProfs();		//does this fix it?
			}
		}
		if (bClass == null)
			return;
		if (mod > 0)
		{
			for (int i = 0; i < mod; i++)
				bClass.addLevel(false, bSilent);
		}
		else if (mod < 0)
		{
			for (int i = 0; i < -mod; i++)
				bClass.subLevel(bSilent);
		}
		if (canReassignTemplateFeats())
		{
			for (int y = 0; y < templateList.size(); y++)
			{
				PCTemplate aTemplate = (PCTemplate)templateList.get(y);
				ArrayList templateFeats = aTemplate.feats(totalLevels(), totalHitDice(), getSize());

				for (int x = 0; x < templateFeats.size(); x++)
					modFeatsFromList((String)templateFeats.get(x), true, false);
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
		for (int i = 0; i < iCount; i++)
		{
			fromClass.doMinusLevelMods(this, fromLevel - i);
			toClass.doPlusLevelMods(this, toLevel + i + 1);
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
			iCount = toClass.getMaxLevel() - toClass.getLevel().intValue();

		//
		// Enough levels to move?
		//
		if ((fromClass.getLevel().intValue() <= iCount) || (iCount < 1))
			return;

		int iOldLevel = toClass.getLevel().intValue();
		toClass.setLevel(new Integer(iOldLevel + iCount));
		for (int i = 0; i < iCount; i++)
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
			return;

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
				for (int i = 0; i < aClass.getLevel().intValue(); i++)
				{
					bClass.setHitPoint(bClass.getLevel().intValue() + i + 1, aClass.getHitPointList(i + 1));
				}
				classList.remove(aClass);
			}


			//
			// Find all skills associated with old class and link them to new class
			//
			for (Iterator e = skillList.iterator(); e.hasNext();)
			{
				Skill aSkill = (Skill)e.next();
				aSkill.replaceClassRank(aClass.getName(), exClass);
			}
			bClass.setSkillPool(aClass.getSkillPool());
		}
		catch (NumberFormatException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}


	}

	public Skill addSkill(Skill addSkill)
	{
		Skill aSkill = null;
		//
		// First, check to see if skill is already in list
		//
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			aSkill = (Skill)e.next();
			if (aSkill.getKeyName().equals(addSkill.getKeyName()))
				return aSkill;
		}

		//
		// Skill not found, add to list
		//
		aSkill = (Skill)addSkill.clone();
		skillList.add(aSkill);
		return aSkill;
	}

	public Float getMaxRank(String skillName, PCClass aClass)
	{
		double i = totalLevels() + 3.0;
		if (Globals.isMonsterDefault())
		{
			i += totalHitDice();
		}
		Skill aSkill = Globals.getSkillNamed(skillName);
		if (aSkill.isExclusive().startsWith("Y"))
		{
			i = 3.0;
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				final PCClass bClass = (PCClass)e.next();
				if (aSkill.isClassSkill(bClass))
				{
					i += bClass.getLevel().doubleValue();
				}
			}
			if (Globals.isMonsterDefault())
			{
				i += totalHitDice();
			}
			if (Math.abs(i - (3.0)) < Double.MIN_VALUE * 2)
			{
				if (Globals.isMonsterDefault())
				{
					i = (totalLevels() + totalHitDice() + 3.0) / 2.0;
				}
				else
				{
					i = (totalLevels() + 3.0) / 2.0;
				}
			}
		}
		else if (!aSkill.isClassSkill(classList) && (aSkill.costForPCClass(aClass).intValue() == 1))
			i = (int)(i / 2.0);
		else if (!aSkill.isClassSkill(classList))
			i /= 2.0;
		i += getTotalBonusTo("SKILLMAXRANK",skillName,true);
		return new Float(i);
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
		for (int index = 0; index < globalFeatListSize; index++)
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

	private void pcgAdjustHpRolls(int increment)
	{
		if (race.hitDice() != 0)
			race.PCG_adjustHpRolls(increment);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			aClass.PCG_adjustHpRolls(increment);
		}
	}

	public int hitPoints()
	{
		int iConMod = calcStatMod(Constants.CONSTITUTION);

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
		return total;
	}

	public int adjStats(int stat)
	{
		if (stat < 0 || stat >= Globals.s_ATTRIBLONG.length)
		{
			return 0;
		}
		if (isNonability(stat))
		{
			return 10;
		}

		int x = getVariableValue("LOCK."+Globals.s_ATTRIBSHORT[stat], "").intValue();
		if (x!=0)
			return x;
				
		int total = getStat(stat);
		final String statName = Globals.s_ATTRIBSHORT[stat];
		total += getTotalBonusTo("STAT", statName, false);

		if (getRace() != null)
		{
			total += getRace().getStatMod(stat);
		}
		for (int template = 0; template < templateList.size(); template++)
		{
			total += ((PCTemplate)templateList.get(template)).getStatMod(stat);
		}

		if (stat == Constants.STRENGTH)
		{
			total += strModForSize();
		}
		else if (stat == Constants.DEXTERITY)
		{
			total += dexModForSize();
		}
		else if (stat == Constants.CONSTITUTION)
		{
			total += conModForSize();
		}

		return total;
	}

	public int unAdjStats(int stat)
	{
		if (stat < 0 || stat >= Globals.s_ATTRIBLONG.length)
		{
			return 0;
		}
		if (isNonability(stat))
		{
			return 10;
		}

		int total = getStat(stat);

		if (getRace() != null)
		{
			total += getRace().getStatMod(stat);
		}
		for (int template = 0; template < templateList.size(); template++)
		{
			total += ((PCTemplate)templateList.get(template)).getStatMod(stat);
		}

		if (stat == Constants.STRENGTH)
		{
			total += strModForSize();
		}
		else if (stat == Constants.DEXTERITY)
		{
			total += dexModForSize();
		}
		else if (stat == Constants.CONSTITUTION)
		{
			total += conModForSize();
		}

		return total;
	}

	public boolean isNonability(int i)
	{
		if (race.isNonability(i))
			return true;
		for (int x = 0; x < templateList.size(); x++)
			if (((PCTemplate)templateList.get(x)).isNonAbility(i))
				return true;
		return false;
	}

	/**
	 * Changes the race of the character. First it removes the
	 * current Race, and any bonus attributes (e.g. feats), then
	 * add the new Race.
	 */
	public void setRace(Race aRace)
	{
		// remove current race attributes
		if (getRace() != null)
		{
			// removing racial spells
//			ArrayList aList = getRace().getCharacterSpellList();
			getRace().clearCharacterSpells();
			if (canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(), "|", false);
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aString.endsWith(")") && Globals.getFeatNamed(aString) == null)
					{
						final String featName = aString.substring(0, aString.indexOf("(") - 1);

						final Feat aFeat = Globals.getFeatNamed(featName);
						if (aFeat != null)
						{
							modFeat(aString, false, aFeat.getName().endsWith("Proficiency"));
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
								modFeat(featName, false, featName.endsWith("Proficiency"));
								setFeats(feats - 1);
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "Removing unknown feat: " + aString, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
			getRace().removeSpecialAbilitiesForRace();
			languages.removeAll(getRace().getLanguageAutos());
			weaponProfList.removeAll(getRace().weaponProfAutos());
			if (racialFavoredClass.length() != 0)
			{
				favoredClasses.remove(racialFavoredClass);
			}

			removeNaturalWeapons();
			for (int x = 0; x < race.templatesAdded().size(); x++)
			{
				removeTemplate(getTemplateNamed((String)race.templatesAdded().get(x)));
			}
			if (race.getMonsterClass() != null && race.getMonsterClassLevels() != 0)
			{
				PCClass mclass = Globals.getClassNamed(race.getMonsterClass());
				incrementClassLevel(race.getMonsterClassLevels() * -1, mclass, true);
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
			getRace().addSpecialAbilitiesForRace();
			// visionOutput = getRace().getVision();
			doVision(getRace().getVision(), 2);
			addNaturalWeapons();
			if (canReassignRacialFeats())
			{
				final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(), "|", false);
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (aString.endsWith(")") && Globals.getFeatNamed(aString) == null)
					{
						final String featName = aString.substring(0, aString.indexOf("(") - 1);	//I think we want the first instance of it, in case of Weapon Focus(Longbow (Composite))

						final Feat aFeat = Globals.getFeatNamed(featName);
						if (aFeat != null)
						{
							setFeats(feats + 1);
							modFeat(aString, true, aFeat.getName().endsWith("Proficiency"));
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
								modFeat(featName, true, featName.endsWith("Proficiency"));
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "Adding unknown feat: " + aString, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
			getAutoLanguages();
			//getAutoWeaponProfs();
			getRacialFavoredClasses();


			if (!importing && !dirtyFlag)
			{
				race.rollHeightWeight();
			}
			ArrayList templates = race.getTemplates(importing);
			for (int x = 0; x < templates.size(); x++)
			{
				addTemplate(Globals.getTemplateNamed((String)templates.get(x)));
			}
			if (!importing && race.getMonsterClass() != null && race.getMonsterClassLevels() != 0)
			{
				PCClass mclass = Globals.getClassNamed(race.getMonsterClass());
				incrementClassLevel(race.getMonsterClassLevels(), mclass, true);
			}
			addRacialSpells();
			race.chooseLanguageAutos(importing);
		}

		adjustMoveRates();

		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
	}

	public void addRacialSpells()
	{
		if (race == null)
			return;
		for (Iterator ri = race.getSpellList().iterator(); ri.hasNext();)
		{
			// spellname|times|book|PRExxx|PRExxx|etc
			String spellList = ri.next().toString();
			final StringTokenizer aTok = new StringTokenizer(spellList, "|", false);
			String spellName = aTok.nextToken();
			Spell aSpell = Globals.getSpellNamed(spellName);
			if (aSpell == null)
				return;
			int times = Integer.parseInt(aTok.nextToken());
			String book = aTok.nextToken();
			if (aTok.hasMoreTokens())
			{
				ArrayList qList = new ArrayList();
				while (aTok.hasMoreTokens())
					qList.add(aTok.nextToken());
				if (!race.passesPreReqTestsForList(qList))
					continue;
			}
			CharacterSpell cs = race.getFirstCharacterSpell(aSpell, book, -1, null, null);
			if (cs == null)
			{
				cs = new CharacterSpell(race, race, aSpell);
				cs.setTimes(times);
				cs.setSpellBook(book);
				addSpellBook(book);
				race.addCharacterSpell(cs);
			}
		}
	}

	public void addTemplateSpells()
	{

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();

			for (Iterator ri = aTemplate.getSpellList().iterator(); ri.hasNext();)
			{
				// spellname|times|book|PRExxx|PRExxx|etc
				String spellList = ri.next().toString();
				final StringTokenizer aTok = new StringTokenizer(spellList, "|", false);
				String spellName = aTok.nextToken();
				Spell aSpell = Globals.getSpellNamed(spellName);
				if (aSpell == null)
					return;
				int times = Integer.parseInt(aTok.nextToken());
				String book = aTok.nextToken();
				if (aTok.hasMoreTokens())
				{
					ArrayList qList = new ArrayList();
					while (aTok.hasMoreTokens())
						qList.add(aTok.nextToken());
					if (!aTemplate.passesPreReqTestsForList(qList) || !race.passesPreReqTestsForList(qList))
						continue;
				}
				CharacterSpell cs = race.getFirstCharacterSpell(aSpell, book, -1, null, null);
				if (cs == null)
				{
					cs = new CharacterSpell(aTemplate, aTemplate, aSpell);
					cs.setTimes(times);
					cs.setSpellBook(book);
					addSpellBook(book);
					race.addCharacterSpell(cs);
				}
			}
		}
	}

	public void changeSpecialAbilitiesForLevel(int level, boolean addIt, Collection aArrayList)
	{

		if (aArrayList.isEmpty())
			return;
		for (Iterator e = aArrayList.iterator(); e.hasNext();)
		{
			final String aString = (String)e.next();
			final StringTokenizer aTok = new StringTokenizer(aString, ":", false);
			final int thisInt = Integer.parseInt(aTok.nextToken());
			final String aList = aTok.nextToken();
			if (level == thisInt)
			{
				final StringTokenizer aStrTok = new StringTokenizer(aList, ",", false);
				while (aStrTok.hasMoreTokens())
				{
					final String thisString = aStrTok.nextToken();
					if (aString.indexOf('%') > -1)
					{
						changeSpecialAbilityNamed(thisString, addIt);
					}
					else
					{
						if (thisString.endsWith("(SPECIALS)"))
						{
							final int adjustment = (addIt?1:-1);
							final int leftParen = thisString.lastIndexOf('(');
							final String aName = thisString.substring(0, leftParen).trim();
							String aDesc = new String();
							String bString = "";
							String eString = new String();
							for (Iterator e1 = getSpecialAbilityList().iterator(); e1.hasNext();)
							{
								bString = (String)e1.next();
								if (bString.startsWith(aName))
								{
									aDesc = bString.substring(bString.lastIndexOf('(') + 1, bString.length() - 1);
									eString = bString;
								}
							}
							final SpecialAbility sa = Globals.getSpecialAbility(aName, aDesc, adjustment);
							String cString = "1";
							String dString = "2";
							if (sa != null)
							{
								final String saName = sa.getName() + " (" + sa.getDesc() + ")";
								if (!addIt && !aDesc.equals(""))
								{
									cString = new String(eString);
									dString = new String(saName);
									for (int i = 0; i < 10; i++)
									{
										cString = cString.replace((char)('0' + i), ' ');
										dString = dString.replace((char)('0' + i), ' ');
									}
									if (addIt && cString.equals(dString))
									{
										getSpecialAbilityList().remove(eString);
									}
								}
								if (!hasSpecialAbility(saName))
								{
									getSpecialAbilityList().add(saName);
								}
							}
						}
						else if (addIt)
							getSpecialAbilityList().add(thisString);
						else
							getSpecialAbilityList().remove(thisString);
					}
				}
			}
		}
	}

	private void changeSpecialAbilityNamed(String aString, boolean addIt)
	{
		try
		{
			StringTokenizer aTok = new StringTokenizer(aString, "%|", false);
			String bString = aTok.nextToken();
			String cString = "";
			int pos = aString.lastIndexOf("|");

			//
			// If this is an expression, then we won't replace it here, we'll do it when the special list is retrieved
			//
			if (!Character.isDigit(aString.charAt(pos + 1)))
			{
				if (addIt)
				{
					if (!getSpecialAbilityList().contains(aString))
						getSpecialAbilityList().add(aString);
				}
				else
				{
					int idx = getSpecialAbilityList().indexOf(aString);
					if (idx >= 0)
						getSpecialAbilityList().remove(idx);
				}
				return;
			}

			int sInt = Integer.parseInt(aString.substring(pos + 1));
			Iterator e = getSpecialAbilityList().iterator();
			while (e.hasNext())
			{
				cString = (String)e.next();
				if (cString.startsWith(bString))
				{
					final int nonDigit = Utility.firstNonDigit(cString, bString.length());
					if (nonDigit > bString.length())
					{
						int anInt = Integer.parseInt(cString.substring(bString.length(), nonDigit));
						if (addIt)
							sInt += anInt;
						else
							sInt = anInt - sInt;
						e.remove();				// remove the current element
						break;
					}
				}
			}

			//
			// Only add in if result is non-zero
			//
			if (sInt != 0)
			{
				e = null;
				aTok = new StringTokenizer(aString.substring(0, pos), "%", true);
				StringBuffer newAbility = new StringBuffer();
				while (aTok.hasMoreTokens())
				{
					final String nextTok = aTok.nextToken();
					if (nextTok.equals("%"))
						newAbility.append(Integer.toString(sInt));
					else
						newAbility.append(nextTok);
				}
				getSpecialAbilityList().add(newAbility.toString());
			}
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("Trapped number format exception for: '" +
				aString + "' check LST files.");
		}
	}

	public int totalLevels()
	{
		int totalLevels = 0;

/*
int i = 0;
//totalLevels = race.hitDice();
for (Iterator e = classList.iterator(); e.hasNext();)
{
final PCClass aClass = (PCClass)e.next();
if (!aClass.isMonster())
totalLevels += aClass.getLevel().intValue();
}
*/
		totalLevels += totalPCLevels();
		totalLevels += totalNPCLevels();
		totalLevels += totalMonsterLevels();

		return totalLevels;
	}

	public int totalPCLevels()
	{
		int totalLevels = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			if (aClass.isPC())
				totalLevels += aClass.getLevel().intValue();
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
				totalLevels += aClass.getLevel().intValue();
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
				totalLevels += aClass.getLevel().intValue();
		}
		return totalLevels;
	}

	public int totalHitDice()
	{
		return race.hitDice() + totalMonsterLevels();
	}

	public int calcCR()
	{
		int CR = race.getCR();
		final int rhd = race.hitDice();
		float hitDieRatio = (float)1.0;
		if (rhd > 0)
			hitDieRatio = (float)totalHitDice() / rhd;
		while (hitDieRatio >= 2)
		{
			CR += 2;
			hitDieRatio /= 2;
		}
		if (hitDieRatio >= 1.5)
			CR += 1;
		CR += totalPCLevels();
		final int NPCLevels = totalNPCLevels();
		if (NPCLevels == 1)
			CR += 1;
		else if (NPCLevels != 0)
			CR += NPCLevels - 1;
		for (int x = 0; x < templateList.size(); x++)
			CR += ((PCTemplate)templateList.get(x)).getCR(totalLevels(), totalHitDice(), getSize());
		return CR;
	}

	public int calcSR()
	{
		int SR = race.getSR();
		for (int x = 0; x < templateList.size(); x++)
		{
			final int templateSR = ((PCTemplate)templateList.get(x)).getSR(totalLevels(), totalHitDice(), getSize());
			if (templateSR > SR)
			{
				SR = templateSR;
			}
		}
		int baseSR = getTotalBonusTo("VAR", "BASESR", false);
		if (baseSR > SR)
		{
			SR = baseSR;
		}

		SR += getTotalBonusTo("VAR", "SR", true);
		return SR;
	}

	public String calcDR()
	{
		StringBuffer DR = new StringBuffer().append(race.getDR());
		String fDR = DR.toString();

		if (DR.length() > 2)
			DR.append(",");

		for (int x = 0; x < templateList.size(); x++)
		{
			DR.append(((PCTemplate)templateList.get(x)).getDR(totalLevels(), totalHitDice(), getSize())).append(",");
			fDR = PCTemplate.addDR(DR.toString());
		}
		if (fDR.endsWith(","))
			fDR = fDR.substring(0, (fDR.length() - 1));
		else if (fDR.startsWith(","))
			fDR = fDR.substring(1, fDR.length());

		return fDR;
	}

	public int levelAdjustment()
	{
		int LA = race.getLevelAdjustment();
		for (int x = 0; x < templateList.size(); x++)
			LA += ((PCTemplate)templateList.get(x)).getLevelAdjustment();
		return LA;
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
					classStringBuffer.append(" ");
				x++;
				if (abbreviations)
					classStringBuffer = classStringBuffer.append(aClass.getAbbrev()).append(" ").append(aClass.getLevel().toString());
				else
					classStringBuffer = classStringBuffer.append(aClass.classLevelString());
			}
		}
		return classStringBuffer.toString();
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
			return true;
		else
			return false;
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
			return null;
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat.getKeyName().equals(featName))
				return aFeat;
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
		return aFeat.canBeSelectedBy(this);
	}

	public boolean isSpellCaster(int minLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass)e1.next();
			if (!aClass.getSpellType().equals(Constants.s_NONE) && aClass.getLevel().intValue() >= minLevel)
				return true;
		}
		return false;
	}

	public boolean isSpellCastermax(int maxLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass)e1.next();
			if (!aClass.getSpellType().equals(Constants.s_NONE) && aClass.getLevel().intValue() <= maxLevel)
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
				Globals.debugPrint("Feat not found: " + oldName);
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
		String choiceType = "";
		if (aFeat.getChoiceString().lastIndexOf('|') > -1)
			choiceType = aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf('|'));


		//final Feat autoFeat = getFeatAutomaticNamed(aFeat.getName());

		// for weapon prof feats, set the associated list to the weapon prof names
		//if (Globals.getWeaponTypes().contains(choiceType))
		//{
		//	Set weaponProfs = getWeaponProfs(choiceType);
		//	int iCount = 0;
		//	for (Iterator setIter = weaponProfs.iterator(); setIter.hasNext();)
		//	{
		//		final WeaponProf aProf = (WeaponProf)setIter.next();
		//		if ((autoFeat == null) || !autoFeat.containsAssociated(aProf.getName()))
		//		{
		//			if (!aFeat.containsAssociated(aProf.getName()))
		//			{
		//				aFeat.addAssociatedList(aProf.getName());
		//				iCount += 1;
		//			}
		//		}
		//	}
		//	j += iCount - (int)(aFeat.getAssociatedCount() * aFeat.getCost());
		//}

		// process ADD tags from the feat definition
		if (!addIt)
		{
			aFeat.modAdds(addIt);
		}

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
					aFeat.modChoices(addIt);
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
					Globals.getWeaponTypes().contains(aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf("|"))))
				{
					addWeaponProfToList(featList, aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf("|")), false);
				}
			}
		}

		if (aFeat.isMultiples() && !addAll)
			retVal = (aFeat.getAssociatedCount() > 0)? 1 : 0;

		// process ADD tags from the feat definition
		if (addIt)
			aFeat.modAdds(addIt);

		// if no sub choices made (i.e. all of them removed in Chooser box),
		// then remove the Feat
		boolean removed = false;
		if (retVal == 0)
		{
			removed = featList.remove(aFeat);
		}

		if (!addIt && !aFeat.isMultiples() && removed)
		{
			j++;
		}
		else if (addIt && !aFeat.isMultiples())
		{
			j--;
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
		if (!addAll && !aFeat.getName().equals("Spell Mastery"))
			setFeats(j);

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
//                  if (totalLevels() == 0)
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
					// could not find Feat
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
					// add the Feat found, as a CharacterFeat
					aFeat = (Feat)aFeat.clone();
					addFeat(aFeat);
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
					System.out.println("Feat not found in PlayerCharacter.modFeatsFromList: " + aString);
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
				if (aFeat.getName().endsWith("Weapon Proficiency"))
				{
					for (int e = 0; e < aFeat.getAssociatedCount(); e++)
					{
						String wprof = aFeat.getAssociated(e);
						WeaponProf wp = Globals.getWeaponProfNamed(wprof);
						if (wp != null)
						{
							addWeaponProfToList(featList, wprof, false);
						}
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
		}
		setAutomaticFeatsStable(false);
	}

	public boolean hasSkill(String skillName)
	{
		return (getSkillNamed(skillName) != null);
	}

	public Skill getSkillNamed(String skillName)
	{
		if (skillList.isEmpty())
			return null;
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			if (aSkill.getName().equalsIgnoreCase(skillName))
				return aSkill;
		}
		return null;
	}

	public Skill getSkillKeyed(String skillName)
	{
		if (skillList.isEmpty())
			return null;
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			if (aSkill.getKeyName().equals(skillName))
				return aSkill;
		}
		return null;
	}

	/**
	 * type 0 = attack bonus; 1 = fort; 2 = reflex; 3 = will; 4 = Monk
	 */
	public int getBonus(int type, boolean addBonuses)
	{
		int bonus = 0;
		switch (type)
		{
			case 0:
				bonus = race.getBAB();
				break;
			case 1:
				bonus = race.getFortSave();
				break;
			case 2:
				bonus = race.getRefSave();
				break;
			case 3:
				bonus = race.getWillSave();
				break;
			default:
				//What should I do here?
		}
		if (addBonuses)
		{
			switch (type)
			{
				case 0:
					bonus += getFeatBonusTo("TOHIT", "TOHIT", false);
					bonus += getRace().bonusTo("TOHIT", "TOHIT");
					bonus += getEquipmentBonusTo("TOHIT", "TOHIT", false);
					bonus += getTemplateBonusTo("TOHIT", "TOHIT", false);
					bonus += modForSize();
					break;
				case 1:
					bonus += getTotalBonusTo("CHECKS", "Fortitude", true);
					break;
				case 2:
					bonus += getTotalBonusTo("CHECKS", "Reflex", true);
					break;
				case 3:
					bonus += getTotalBonusTo("CHECKS", "Willpower", true);
					break;
				case 4:
					bonus += modForSize();
					break;
				default:
					//What should I do here?
			}
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			switch (type)
			{
				case 0:
				case 4:
					bonus += aClass.baseAttackBonus();
					break;
				case 1:
					bonus += aClass.fortitudeCheckBonus();
					break;
				case 2:
					bonus += aClass.reflexCheckBonus();
					break;
				case 3:
					bonus += aClass.willCheckBonus();
					break;
//				case 4: if (aClass.name().equals("Monk"))
//					bonus += aClass.baseAttackBonus();
//					break;
				default:
					//What should I do here?
			}
		}
		return bonus;
	}

	public int baseAttackBonus()
	{
		int bonus = getRace().getBAB();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			bonus += aClass.baseAttackBonus();
		}
		return bonus;
	}

	public void ValidateCharacterDomains()
	{
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
		for (int i = 0; i < characterDomainList.size(); i++)
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
		for (int i = 0; i < characterDomainList.size(); i++)
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
		for (int i = 0; i < characterDomainList.size(); i++)
		{
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null && aDomain.getName().equalsIgnoreCase(domainName))
				return aCD;
		}
		return null;
	}

	public int getFirstEmptyCharacterDomain()
	{
		for (int i = 0; i < characterDomainList.size(); i++)
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
			alignment,
			race.getName(),
			gender);
	}

	public boolean setDeity(Deity aDeity)
	{
		if (!canSelectDeity(aDeity))
			return false;
		changeDeity(false);
		deity = aDeity;
		changeDeity(true);
		return true;
	}

	public boolean hasSpecialAbility(String abilityName)
	{
		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
			if (e.next().toString().equalsIgnoreCase(abilityName))
				return true;
		return false;
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
				languages.removeAll(getRace().getLanguageAutos());
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
		// autoLangs.addAll(templateAutoLanguages);

		if (!clearRacials)
			autoLangs.addAll(getRace().getLanguageAutos());

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			autoLangs.addAll(aClass.getLanguageAutos());
		}
		languages.addAll(autoLangs);
		return autoLangs;
	}

/*
	public SortedSet getAutoLanguages()
	{
		// find list of all possible langauges
		SortedSet autoLangs = new TreeSet();
		autoLangs.addAll(getRace().getLanguageAutos());
		autoLangs.addAll(templateAutoLanguages);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			autoLangs.addAll(aClass.getLanguageAutos());
		}
		languages.addAll(autoLangs);
		return autoLangs;
	}
*/

	private void removeNaturalWeapons()
	{
		for (Iterator e = getRace().getNaturalWeapons().iterator(); e.hasNext();)
		{
			equipmentList.remove((Equipment)e.next());
		}
	}

	public void addNaturalWeapons()
	{
		equipmentList.addAll(getRacialNaturalWeapons());
//		for (Iterator e = getRace().getNaturalWeapons().iterator(); e.hasNext();)
//		{
//			final Equipment anEquip = (Equipment)e.next();
//			equipmentList.add(anEquip);
//		}
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
		for (int i = 0; i < templateList.size(); i++)
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

	public Float multiclassXpMultiplier()
	{
		SortedSet unfavoredClasses = new TreeSet();
		SortedSet aList = new TreeSet(favoredClasses);
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
			aList.remove("Any");
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
		return new Float(xpMultiplier);
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
	 * know.  This includes extra languages from intelligence, speak
	 * language skill, and race.
	 */
	public int languageNum()
	{
		return languageNum(true);
	}

	public int languageNum(boolean includeSpeakLanguage)
	{
		int i = calcStatMod(Constants.INTELLIGENCE);
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

	private SortedSet getAutoWeaponProfs(ArrayList aFeatList)
	{
		SortedSet results = new TreeSet();
		final Race aRace = getRace();
		if (aRace != null)
		{
			for (Iterator e = aRace.weaponProfAutos().iterator(); e.hasNext();)
			{
				final String aString = (String)e.next();
				if (Globals.getWeaponTypes().contains(aString))
				{
					for (Iterator e1 = Globals.getWeaponProfList().iterator(); e1.hasNext();)
					{
						final WeaponProf aProf = (WeaponProf)e1.next();
						if (aProf.getType().equalsIgnoreCase(aString))
						{
							results.add(aProf.getName());
							addWeaponProfToList(aFeatList, aProf.getName(), true);
						}
					}
				}
				else
				{
					results.add(aString);
					addWeaponProfToList(aFeatList, aString, true);
				}
			}
			//
			// Add race-granted selected BONUS weapon proficiencies
			//
			for (int i = 0; i < aRace.getSelectedWeaponProfBonusCount(); i++)
			{
				String aString = aRace.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}

		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)e.next();
			for (Iterator et = aTemplate.weaponProfAutos().iterator(); et.hasNext();)
			{
				final String aString = (String)et.next();
				if (Globals.getWeaponTypes().contains(aString))
				{
					for (Iterator e1 = Globals.getWeaponProfList().iterator(); e1.hasNext();)
					{
						final WeaponProf aProf = (WeaponProf)e1.next();
						if (aProf.getType().equalsIgnoreCase(aString))
						{
							results.add(aProf.getName());
							addWeaponProfToList(aFeatList, aProf.getName(), true);
						}
					}
				}
				else
				{
					results.add(aString);
					addWeaponProfToList(aFeatList, aString, true);
				}
			}
			//
			// Add template-granted selected BONUS weapon proficiencies
			//
			for (int i = 0; i < aTemplate.getSelectedWeaponProfBonusCount(); i++)
			{
				final String aString = aTemplate.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			final String sizeString = "FDTSMLHGC";
			for (Iterator e1 = aClass.getWeaponProfAutos().iterator(); e1.hasNext();)
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
						if (!aClass.passesPreReqTestsForList(preReqList))
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
					for (int i = 0; i < eString.length(); i++)
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
					if (Globals.getWeaponTypes().contains(aString))
					{
						for (Iterator e2 = Globals.getWeaponProfList().iterator(); e2.hasNext();)
						{
							final WeaponProf aProf = (WeaponProf)e2.next();
							if (aProf.getType().equalsIgnoreCase(aString))
							{
								results.add(aProf.getName());
								addWeaponProfToList(aFeatList, aProf.getName(), true);
							}
						}
					}
					else
					{
						results.add(aString);
						addWeaponProfToList(aFeatList, aString, true);
					}
				}
			}
			//
			// Add class-granted selected BONUS weapon proficiencies
			//
			for (int i = 0; i < aClass.getSelectedWeaponProfBonusCount(); i++)
			{
				final String aString = aClass.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}

		//
		// Add feat-granted selected BONUS weapon proficiencies
		//
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			for (int i = 0; i < aFeat.getSelectedWeaponProfBonusCount(); i++)
			{
				final String aString = aFeat.getSelectedWeaponProfBonus(i);
				results.add(aString);
				addWeaponProfToList(aFeatList, aString, true);
			}
		}

		//
		// Add domain-granted selected BONUS weapon proficiencies
		//
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain)e.next();
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				for (int i = 0; i < aDomain.getSelectedWeaponProfBonusCount(); i++)
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
//		if (getStableAggregateFeatList() != null)
//		{
//			for (Iterator e = getStableAggregateFeatList().iterator(); e.hasNext();)
//			{
//				final Feat aFeat = (Feat)e.next();
//				final String addString = aFeat.getAddString();
//				final StringTokenizer aTok = new StringTokenizer(addString, "|", false);
//				if (aTok.countTokens() != 2)
//				{
//					continue;
//				}
//
//				final String addType = new String(aTok.nextToken());
//				if (addType.equals("WEAPONPROFS"))
//				{
//					final String addSec = new String(aTok.nextToken());
//					if (Globals.getWeaponTypes().contains(addSec))
//					{
//						for (Iterator e2 = Globals.getWeaponProfList().iterator(); e2.hasNext();)
//						{
//							final WeaponProf aProf = (WeaponProf)e2.next();
//							if (aProf.getType().equalsIgnoreCase(addSec))
//							{
//								results.add(aProf.getName());
//								addWeaponProfToList(aFeatList, aProf.getName(), true);
//							}
//						}
//					}
//					else
//					{
//						results.add(addSec);
//						addWeaponProfToList(aFeatList, addSec, true);
//					}
//				}
//			}
//			setAggregateFeatsStable(false);
//		}

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
					if (Globals.getWeaponTypes().contains(addSec))
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

	//public SortedSet getBonusWeaponProfs()
	//{
	//	SortedSet results = new TreeSet(getRace().getWeaponProfBonus());
	//	bonusWeaponChoices = 0;
	//	if (results.size() > 0)
	//		bonusWeaponChoices = 1;
	//	for (Iterator e = classList.iterator(); e.hasNext();)
	//	{
	//		final PCClass aClass = (PCClass)e.next();
	//		if (results.addAll(aClass.getWeaponProfBonus()))
	//			bonusWeaponChoices++;
	//	}
	//	return results;
	//}


	public void addWeaponProf(String aString)
	{
		addWeaponProfToList(featList, aString, false);
	}

	private void addWeaponProfToList(ArrayList aFeatList, String aString, boolean isAuto)
	{
		//
		// Add all weapons of type aString (eg. Simple, Martial, Exotic, Ranged, etc.)
		//
		if (Globals.getWeaponTypes().contains(aString))
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
						Globals.debugPrint("Weaponprof feat not found: " + featName + ":" + aString);
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

	//public int weaponProfNum()
	//{
	//	int i = 0;
	//	Iterator e = null;
	//	SortedSet currentProf = (SortedSet)getWeaponProfList().clone();
	//	//SortedSet autoProfs = getAutoWeaponProfs();
	//	Collection raceProfs = getRace().getWeaponProfBonus();
	//	if (raceProfs.size() > 0)
	//	{
	//		for (e = raceProfs.iterator(); e.hasNext();)
	//		{
	//			String aString = (String)e.next();
	//			if (currentProf.contains(aString))
	//			{
	//				currentProf.remove(aString);
	//				if (!autoProfs.contains(aString))
	//				{
	//					i--;
	//					break;
	//				}
	//			}
	//		}
	//		i++;
	//	}
	//	for (Iterator e2 = classList.iterator(); e2.hasNext();)
	//	{
	//		PCClass aClass = (PCClass)e2.next();
	//		raceProfs = aClass.getWeaponProfBonus();
	//		for (e = raceProfs.iterator(); e.hasNext();)
	//		{
	//			String aString = (String)e.next();
	//			if (currentProf.contains(aString))
	//			{
	//				currentProf.remove(aString);
	//				i--;
	//				break;
	//			}
	//		}
	//		if (raceProfs.size() > 0)
	//			i++;
	//	}
	//	return i;
	//}

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
			Globals.debugPrint("Cast As:" + cName);
			if (aType.equals("Any") || aType.equals(aClass.getSpellType()))
			{
				for (int a = minLevel; a <= maxLevel; a++)
				{
					ArrayList aList = aClass.getCharacterSpell(null, "", a, null, null);
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
			return new Float(totalValue);
		for (Iterator e = equipmentList.iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (!eq.isHeaderParent())
				totalValue += eq.getCost().floatValue() * eq.qty().floatValue();
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
				if (aTok.nextToken().equals("ARMOR"))
					break;
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
	 *  status: 1 (equipped) 2 (not equipped) 3 (none)
	 */
	public ArrayList getEquipmentOfType(String typeName, int status)
	{
		return getEquipmentOfType(typeName, "", status);
	}


	/**
	 *  status: 1 (equipped) 2 (not equipped) 3 (none)
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
						GuiFacade.showMessageDialog(null, "No entry in weapons.lst for " + eq.profName() + ". Weapons must be in that file to equip them.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						Globals.debugPrint("Globals: " + Globals.getWeaponProfList() + "\n" + "Prof: " + eq.profName());
						hands += 3;
					}
					else
					{
/*					if (wp.isOneHanded())
					{
						hands += Math.max(1, eq.hands);
					}
					if (wp.isTwoHanded())
					{
						hands += Math.max(2, eq.hands);
					}
*/
						switch (eq.getHand())
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
				GuiFacade.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			return true;
		}
		else
			return aArrayList.size() == 0;
	}

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
						//hands = 2 * wp.handsNeeded();
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
				for (int y = 0; y < eq.getNumberEquipped() - 1; y++)
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

		if (Globals.getTreatInHandAsEquippedForAttacks())
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

	public int getTotalBonusTo(String bonusType, String bonusName, boolean stacks)
	{
		int bonus = 0;
		final String prefix = new StringBuffer(bonusType.toUpperCase()).append(".").append(bonusName.toUpperCase()).append(".").toString();
		// go through hashmap and zero out all entries that deal with this bonus request
		for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
		{
			String aKey = i.next().toString();
			if (aKey.startsWith(prefix))
			{
				bonusMap.put(aKey, "0");
			}
		}
		try
		{
			if (!classList.isEmpty())
				getClassBonusTo(bonusType, bonusName);
			if (!equipmentList.isEmpty())
				getEquipmentBonusTo(bonusType, bonusName, stacks);
			getFeatBonusTo(bonusType, bonusName, stacks);
			if (!templateList.isEmpty())
				getTemplateBonusTo(bonusType, bonusName, stacks);
			if (!characterDomainList.isEmpty())
				getDomainBonusTo(bonusType, bonusName);
			getRace().bonusTo(bonusType, bonusName);
			if (!skillList.isEmpty())
				getSkillBonusTo(bonusType, bonusName);
			if (getDeity() != null)
				getDeity().bonusTo(bonusType, bonusName);
			if (bonusType.startsWith("WEAPONPROF="))
				getWeaponProfBonusTo(bonusType.substring(11), bonusName);
			ArrayList aList = new ArrayList();
			for (Iterator i = bonusMap.keySet().iterator(); i.hasNext();)
			{
				final String aKey = i.next().toString();
				if (aKey.startsWith(prefix))
				{
					// make a list of any keys that start with .REPLACE
					if (aKey.endsWith(".REPLACE"))
						aList.add(aKey);
					bonus += Integer.parseInt((String)bonusMap.get(aKey));
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
						String aString = (String)bonusMap.get(replaceKey);
						final int replaceBonus = Integer.parseInt(aString);
						int aBonus = 0;
						aString = (String)bonusMap.get(aKey);
						if (aString != null)
							aBonus = Integer.parseInt(aString);
						aString = (String)bonusMap.get(aKey + ".STACK");
						if (aString != null)
							aBonus += Integer.parseInt(aString);
						bonus -= Math.min(aBonus, replaceBonus);
					}
				}
			}
		}
		catch (NumberFormatException exc)
		{
			System.out.println("error in getTotalBonusTo " + bonusType + " " + bonusName);
			exc.printStackTrace();
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
		int bonus = 0;
		int replaceBonus = 0;
		int stackBonus = 0;
		final String typeString = mainType.toUpperCase() + "." + subType.toUpperCase() + "." + bonusType.toUpperCase();
		String aString = (String)bonusMap.get(typeString);
		if (aString != null)
			bonus = Integer.parseInt(aString);
		aString = (String)bonusMap.get(typeString + ".REPLACE");
		if (aString != null)
			replaceBonus = Integer.parseInt(aString);
		aString = (String)bonusMap.get(typeString + ".STACK");
		if (aString != null)
			stackBonus = Integer.parseInt(aString);
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
			String aString = aTok.nextToken(); // should be bonus category e.g. "COMBAT"
			aString = aTok.nextToken(); // should be bonus name e.g. "AC"
			aString = aTok.nextToken(); // should be bonus type e.g. whatever
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
			if (aKey == null)
				bonusMap.put(bonusType, String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(Math.max(bonus, Integer.parseInt(aKey))));
		}
		else // stacking bonuses
		{
			if (bonusType == null)
				bonusType = "";
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey == null)
				bonusMap.put(bonusType, String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(bonus + Integer.parseInt(aKey)));
		}
	}

	public int getSkillBonusTo(String type, String aName)
	{
		int bonus = 0;
		if (skillList.isEmpty())
			return bonus;
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			Skill aSkill = (Skill)e.next();
			// If you get a bonus, you get it even if your ranks
			// in that skill are 0
			// So this check should not be here.....
			//
			// if (aSkill.getRank().intValue() > 0)
			// {
			//
			// Don't add bonuses included in the skill with
			// the same name as it gets added on elsewhere
			//
			if (!aSkill.getName().equals(aName))
			{
				bonus += aSkill.bonusTo(type, aName);
			}
			// }
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
					String aString = aTok.nextToken();	// should be bonus category e.g. "COMBAT"
					aString = aTok.nextToken(); 		// should be bonus name e.g. "AC"
					aString = aTok.nextToken();		// should be bonus type e.g. whatever
					if ((aString != null) && !aString.equalsIgnoreCase("null"))
					{
						stacks = Globals.getBonusStackList().indexOf(aString) >= 0;
					}
				}

				int iCurrent = Integer.parseInt(aKey);
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
				for (int f = 0; f < t.getAssociatedCount(); f++)
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
				for (int f = 0; f < aFeat.getAssociatedCount(); f++)
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
		if (!equipmentList.isEmpty())
		{
			for (Iterator e = equipmentList.iterator(); e.hasNext();)
			{
				final Equipment eq = (Equipment)e.next();
				if (eq.isEquipped() && eq.isArmor())
				{
					return new Integer(totalAC());
				}
			}
		}
		int y = 0;
		int i = modForSize() + getRace().getStartingAC().intValue() + naturalArmorModForSize() + calcStatMod(Constants.DEXTERITY);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			i += aClass.defense(y).intValue();
			y++;
		}
		i += getFeatBonusTo("CLASS", "DEFENSE", true) + getTemplateBonusTo("CLASS", "DEFENSE", true) + getRace().bonusTo("CLASS", "DEFENSE");
		return new Integer(i);
	}

	public Integer woundPoints()
	{
		int i = adjStats(Constants.CONSTITUTION);
		i += getTotalBonusTo("HP", "WOUNDPOINTS", false);
		return new Integer(i);
	}

	public Integer reputation()
	{
		int i = getRace().bonusTo("CLASS", "REPUTATION");
		i += getEquipmentBonusTo("CLASS", "REPUTATION", true);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			i += aClass.getBonusTo("CLASS", "REPUTATION", aClass.getLevel().intValue());
			String aString = aClass.getReputationString();
			int k = Integer.parseInt(aString);
			switch (k)
			{
				case 0:	/*Best*/
					i += 3 + aClass.getLevel().intValue() / 2;
					break;
				case 1:	/*MHigh*/
					i += 1 + aClass.getLevel().intValue() / 2;
					break;
				case 2:	/*MLow*/
					i += aClass.getLevel().intValue() / 2;
					break;
				case 3:	/*Low*/
					i += aClass.getLevel().intValue() / 3;
					break;
				case 4:	/*NPCH*/
					i += (aClass.getLevel().intValue() + 1) / 3;
					break;
				case 5:	/*NPCL*/
					i += aClass.getLevel().intValue() / 4;
					break;
				case 6:	/*PHigh*/
					if (aClass.getLevel().intValue() % 3 != 0) i += (aClass.getLevel().intValue() - (aClass.getLevel().intValue() / 3));
					break;
				case 8:	/*P v3*/
				case 7:	/*PLow*/
					i += aClass.getLevel().intValue() / 2;
					break;

				case 9:	/*P v4*/
					if (aClass.getLevel().intValue() % 4 != 0) i += (aClass.getLevel().intValue() - (aClass.getLevel().intValue() / 4));
					break;
				case 10:	/*P v5*/
					switch (aClass.getLevel().intValue())
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
							//What should I do here?
					}

				case 11:	/*P classlevel/5 + (classlevel +3)/5 Wheel of Time method. */
					int tempLvl = aClass.getLevel().intValue();
					i += ((tempLvl / 5) + ((tempLvl + 3) / 5));
					break;
				default:
					//What should I do here?
			}
		}
		int y = totalLevels();
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
				final ArrayList aList = aClass.getCharacterSpell(null, aName, -1, null, null);
				for (int j = aList.size() - 1; j >= 0; j--)
					aClass.removeCharacterSpell((CharacterSpell)aList.get(j));
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
	}

	public boolean getAutoSpells()
	{
		return autoKnownSpells;
	}

	public boolean isSpellProhibited(CharacterSpell acs, String className)
	{
		if (acs == null) return true;
		PCClass aClass = null;
		final int spellLevel = acs.getLevel();
		int adjSpellLevel = acs.getAdjustedLevel();
		Spell aSpell = acs.getSpell();

		if (className != null)
		{
			aClass = getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")).trim());
		}
		if (aClass == null) return true;

		final CharacterSpell cs = Globals.getFirstCharacterSpell(aSpell, Globals.getDefaultSpellBook(), -1, null, aClass);
		if (cs != null && !cs.isInSpecialty() &&
			(aClass.prohibitedStringContains(aSpell.getSchool()) || aClass.prohibitedStringContains(aSpell.getDescriptorList())))
			return true;

		return false;
	}

	/**
	 * return value indicates whether or not a spell was added or not
	 **/
	public String addSpell(CharacterSpell acs, ArrayList aFeatList, String className, String bookName)
	{
		if (acs == null)
			return "Invalid parameter to add spell";
		PCClass aClass = null;
		final int spellLevel = acs.getLevel();
		int adjSpellLevel = acs.getAdjustedLevel();
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
		if (spellLevel == 0 && aClass.getSpellType().equalsIgnoreCase("Divine"))
			numSpellsFromSpecialty = 0;

		// all the exists checks are done.
		// let's create the CharacterSpell object

		CharacterSpell aCSpell = new CharacterSpell(acs.getOwner(), aClass, aSpell);
		aCSpell.setSpellBook(bookName);
		aCSpell.setLevel(spellLevel);
		if (aFeatList.size() > 0)
			aCSpell.setFeatList(aFeatList);
		adjSpellLevel = aCSpell.getAdjustedLevel();

		// now determine how many specialtySpells
		// of this level for this class in this book
		int spellsFromSpecialty = 0;
		// first we check this spell being added
		if (aCSpell.isInSpecialty())
			spellsFromSpecialty++;
		// now all the rest of the already known spells
		ArrayList sList = aClass.getCharacterSpell(null, bookName, adjSpellLevel, null, null);
		if (!sList.isEmpty())
		{
			for (Iterator i = sList.iterator(); i.hasNext();)
			{
				final CharacterSpell cs = (CharacterSpell)i.next();
				if (cs.isInSpecialty())
					spellsFromSpecialty++;
			}
		}

		// don't allow adding spells which are prohibited
		// But if a spell is both prohibited and in a specialty
		// which can be the case for some spells, then allow it.
		final CharacterSpell cs = Globals.getFirstCharacterSpell(aSpell, Globals.getDefaultSpellBook(), -1, null, aClass);
		if (cs != null && !cs.isInSpecialty() &&
			(aClass.prohibitedStringContains(aSpell.getSchool()) || aClass.prohibitedStringContains(aSpell.getDescriptorList())))
			return acs.getSpell().getName() + " is prohibited.";

		// Now let's see if they should be able to add this spell

		// first check for known/cast/threshold
		int known = 0;
		if (Globals.isSSd20Mode())
			known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel, bookName);
		else
			known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel);
		int specialKnown = 0;
		int cast = aClass.getCastForLevel(aClass.getLevel().intValue(), adjSpellLevel, bookName);
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
			if (listNum - spellsFromSpecialty >= known && !aCSpell.isInSpecialty())
				return "Your remaining slot(s) must come from your specialty";
		}
		else if (aClass.getMemorizeSpells() && !isDefault && listNum >= cast - numSpellsFromSpecialty)
		{
			if (listNum >= cast)
				return "You can only prepare " + cast + " total for level " + adjSpellLevel;
			if (!aCSpell.isInSpecialty() && ((listNum - spellsFromSpecialty) >= (cast - numSpellsFromSpecialty)))
				return "Your remaining slots must come from your specialty or domain";
		}

		// determine if this spell already exists
		// for this character in this book at this level
		final CharacterSpell lcs = aClass.getFirstCharacterSpell(aSpell, bookName, adjSpellLevel, null, null);

		if (lcs != null)
		{
			// ok, we already known this spell, so if they are
			// trying to add it to the default spellBook, barf
			// otherwise increment the number of times memorized
			if (isDefault)
				return "The Known Spells spellbook contains all spells of this level that you know. You cannot place spells in multiple times.";
			else
				lcs.adjustTimes(1);
		}
		else
		{
			aClass.addCharacterSpell(aCSpell);
		}
		return "";
	}

	/**
	 * return value indicates whether or not a spell was deleted or not
	 **/
	public String delSpell(CharacterSpell acs, PCClass aClass, String bookName)
	{
		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (aClass == null)
			return "Error: Class is null";

		boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		// yes, you can remove spells from the default spellbook,
		// but they will just get added back in when the character
		// is re-loaded. But, allow them to do it anyway, just in case
		// there is some wierd spell that keeps getting loaded by
		// accident (or is saved in the .pcg file)
		if (isDefault && aClass.isAutoKnownSpell(acs.getSpell().getName(), acs.getLevel()))
		{
			System.err.println("Notice: removing " + acs.getSpell().getName() + " even though it is an auto known spell");
		}
		List aList = aClass.getCharacterSpell(acs.getSpell(), bookName, acs.getAdjustedLevel(), acs.getOwner(), null);
		for (int i = (aList.size() - 1); i >= 0; i--)
		{
			CharacterSpell cs = (CharacterSpell)aList.get(i);
			cs.adjustTimes(-1);
			if (cs.getTimes() <= 0)
				aClass.removeCharacterSpell(cs);
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
				GuiFacade.showMessageDialog(null, "Adding unknown feat: " + featName, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
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

		//String aString = null;
		//String subName = null;
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
				catch (NumberFormatException	 exc)
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
				ArrayList templateFeats = aTemplate.feats(totalLevels(), totalHitDice(), getSize());
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
								Globals.debugPrint("no '?' in Domain assocatedList entry: " + aString);
							}
						}
						else
						{
							Globals.debugPrint("Domain associatedList contains: " + aString);
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
	 *   fortitude = 1
	 *   reflex = 2
	 *   willpower = 3
	 *
	 */
	private int _calculateSaveBonusClass(int saveIndex)
	{
		int save = 0;
		for (Iterator it = classList.iterator(); it.hasNext();)
		{
			PCClass aClass = (PCClass)it.next();

			if (saveIndex == 1)
			{
				save += aClass.fortitudeCheckBonus();
			}
			else if (saveIndex == 2)
			{
				save += aClass.reflexCheckBonus();
			}
			else if (saveIndex == 3)
			{
				save += aClass.willCheckBonus();
			}
		}

		return save;
	}

	/**
	 * calculate the total racial modifier to save where
	 *   fortitude = 1
	 *   reflex = 2
	 *   willpower = 3
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
		if (saveIndex == 1)
		{
			save += race.getFortSave() +
				getRace().bonusTo("CHECKS", "Fortitude") +
				getTemplateBonusTo("CHECKS", "Fortitude", true);

		}
		else if (saveIndex == 2)
		{
			save += race.getRefSave() +
				getRace().bonusTo("CHECKS", "Reflex") +
				getTemplateBonusTo("CHECKS", "Reflex", true);
		}
		else if (saveIndex == 3)
		{
			save += race.getWillSave() +
				getRace().bonusTo("CHECKS", "Willpower") +
				getTemplateBonusTo("CHECKS", "Willpower", true);
		}

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
	 *   save    := "FORTITUDE"|"REFLEX"|"WILL"
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
	 */
	private int _calculateSaveBonus(int saveIndex, String saveType, int saveAbility, String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ".");
		String[] tokens = new String[aTok.countTokens()];
		for (int i = 0; aTok.hasMoreTokens(); i++)
		{
			tokens[i] = aTok.nextToken();
		}
		int i = 1;

		int save = 0;
		if (tokens.length > i)
		{

			if (tokens[i].equals("TOTAL"))
			{
				save += getBonus(saveIndex, true) + calcStatMod(saveAbility);
			}
			else if (tokens[i].equals(".BASE"))
			{
				save += _calculateSaveBonusClass(saveIndex);
			}
			/**
			 * this includes:
			 *  class boni (e.g. paladin)
			 *  domain boni (e.g. mysticism)
			 *  luck boni (e.g. fugitive's luck for Song n Silence Outlaw of the Crimson Road)
			 *  magical boni (e.g. magical equipment or spells)
			 *  racial boni (e.g. monster)
			 */
			else if (tokens[i].equals("MISC"))
			{
				save += getTotalBonusTo("CHECKS", saveType, true);
			}

		}
		else
		{
			save = getBonus(saveIndex, true);
		}

		for (; i < tokens.length; i++)
		{

			/**
			 * include stuff
			 */
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
	 *   save    := "FORTITUDE"|"REFLEX"|"WILL"
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
	 * @param saveIndex     fortitude = 1, reflex = 2, or willpower = 3
	 * @param saveType      "Fortitude", "Reflex", or "Willpower";
	 *                      may not differ from saveIndex!
	 * @param saveAbility   Constants.CONSTITUTION, Constants.DEXTERITY, or Constants.WISDOM
	 * @param aString       tokenString to parse

	 * @return the calculated save bonus
	 */
	public int calculateSaveBonus(int saveIndex, String saveType, int saveAbility, String aString)
	{
		return _calculateSaveBonus(saveIndex, saveType, saveAbility, aString);
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
				List aList = aObject.getCharacterSpell(null, Globals.getDefaultSpellBook(), levelNum, null, null);
				spellCount = aList.size();
			}
		}
		return spellCount;
	}

	/*
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
			List aList = aObject.getCharacterSpell(null, bookName, levelNum, null, null);
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
				CharacterSpell cs = null;
				if (classNum == -1)
				{
					ArrayList charSpellList = new ArrayList();
					for (Iterator iClass = getClassList().iterator(); iClass.hasNext();)
					{
						final PCClass aClass = (PCClass)iClass.next();
						for (int cSpell = 0; cSpell < aClass.getCharacterSpellCount(); cSpell++)
						{
							cs = aClass.getCharacterSpell(cSpell);
							if (!charSpellList.contains(cs))
								charSpellList.add(cs);
						}
					}
					Collections.sort(charSpellList);
					if (spellNumber < charSpellList.size())
					{
						cs = (CharacterSpell)charSpellList.get(spellNumber);
						found = true;
					}
				}
				else if (aObject != null)
				{
					List charSpells = aObject.getCharacterSpell(null, bookName, spellLevel, null, null);
					if (spellNumber < charSpells.size())
					{
						cs = (CharacterSpell)charSpells.get(spellNumber);
						found = true;
					}
				}
				if (found && (cs != null))
				{
					return cs.getTimes();
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
		for (int idx = 0; idx < weapList.size(); idx++)
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
				// NOTE: Cannot use getPrimaryWeapons().indexOf(equip), as equals() is overridden in Equipment.java
				//
				//int iPrimary = getPrimaryWeapons().indexOf(equip);
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
				// NOTE: Cannot use getSecondaryWeapons().indexOf(equip), as equals() is overridden in Equipment.java
				//
				//int iSecondary = getSecondaryWeapons().indexOf(equip);
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
					//getPrimaryWeapons().add(eqm);
					getSecondaryWeapons().add(++iSecondary, eqm);
				}
			}
		}
		return weapList;
	}

	/**
	 *  e.g. getVariableValue("3+CHA","CLASS:Cleric","1") for Turn Undead
	 */
	public Float getVariableValue(String aString, String src)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		while (aString.lastIndexOf("(") > -1)
		{
			int x = Utility.innerMostStringStart(aString);
			int y = Utility.innerMostStringEnd(aString);
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
						comp = 1;
					else if (cString.equals("LTEQ"))
						comp = 2;
					else if (cString.equals("EQ"))
						comp = 3;
					else if (cString.equals("GT"))
						comp = 4;
					else if (cString.equals("GTEQ"))
						comp = 5;
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
					bString += cString;
			}
			if (val1 != null && val2 != null && valt != null)
			{
				valf = getVariableValue(bString, src);
				total = valt;
				switch (comp)
				{
					case 1:	// LT
						if (val1.doubleValue() >= val2.doubleValue())
							total = valf;
						break;
					case 2:	// LTEQ
						if (val1.doubleValue() > val2.doubleValue())
							total = valf;
						break;
					case 3:	// EQ
						if (val1.doubleValue() != val2.doubleValue())
							total = valf;
						break;
					case 4:	// GT
						if (val1.doubleValue() <= val2.doubleValue())
							total = valf;
						break;
					case 5:	// GTEQ
						if (val1.doubleValue() < val2.doubleValue())
							total = valf;
						break;
					default:
						System.out.println("ERROR - badly formed statement:" +
							aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				Globals.debugPrint("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
				return total;
			}
		}
		for (int i = 0; i < aString.length(); i++)
		{
			valString += aString.substring(i, i + 1);
			if (i == aString.length() - 1 || delimiter.lastIndexOf(aString.charAt(i)) > -1 ||
				(valString.length() > 3 && (valString.endsWith("MIN") || valString.endsWith("MAX") || valString.endsWith("REQ"))))
			{
				if (delimiter.lastIndexOf(aString.charAt(i)) > -1)
					valString = valString.substring(0, valString.length() - 1);
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
							decrement = Integer.parseInt(lTok.nextToken());
						loopValue = 0;
						if (hasVariable(loopVariable))
						{
							loopValue = getVariable(loopVariable, true, true, "", "").intValue();
							loopVariable = vString;
						}
					}
					if (loopValue == 0)
						loopVariable = "";
					valString = new Integer(loopValue).toString();
					Globals.debugPrint("loopVariable=" + loopVariable + " loopValue=" + loopValue);
				}
				if (valString.equals("SPELLBASESTATSCORE"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getSpellBaseStat() + "SCORE";
						if (valString.equals("SPELLSCORE"))
							valString = "10";
					}
					else
						valString = "0";
				}
				if (valString.equals("SPELLBASESTAT"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
					{
						valString = aClass.getSpellBaseStat();
						if (valString.equals("SPELL"))
							valString = "0";
					}
					else
						valString = "0";
				}
				if (valString.length() > 0 && Globals.getStatFromAbbrev(valString) > -1)
				{
					final int stat = Globals.getStatFromAbbrev(valString);
					valString = Integer.toString(calcStatMod(stat));
					Globals.debugPrint("MOD=" + valString);
				}
				else if (valString.length() == 8 && Globals.getStatFromAbbrev(valString.substring(0, 3)) > -1 && valString.endsWith(".BASE"))
				{
					Globals.debugPrint("STAT=" + valString.substring(0, 3));
					final int stat = Globals.getStatFromAbbrev(valString.substring(0, 3));
					valString = Integer.toString(calcUnAdjStatMod(stat));
					Globals.debugPrint(" BASE=" + valString);
				}
				else if ((valString.length() >= 8) && valString.substring(3).startsWith("SCORE"))
				{
					final int stat = Globals.getStatFromAbbrev(valString.substring(0, 3));
					if (valString.endsWith(".BASE"))
					{
						valString = Integer.toString(unAdjStats(stat));
					}
					else
					{
						valString = Integer.toString(adjStats(stat));
					}
					Globals.debugPrint("SCORE=" + valString);
				}
//				else if (valString.length() == 8 && Globals.getStatFromAbbrev(valString.substring(0, 3)) > -1 && valString.endsWith("SCORE"))
//				{
//					final int stat = Globals.getStatFromAbbrev(valString.substring(0, 3));
//					valString = Integer.toString(adjStats(stat));
//					Globals.debugPrint("SCORE=" + valString);
//				}
				else if (valString.startsWith("CL="))
				{
					PCClass aClass = null;
					if (valString.length() > 3)
						aClass = getClassNamed(valString.substring(3));
					else
						System.out.println("Error! Cannot determine CL!");
					if (aClass != null)
						valString = aClass.getLevel().toString();
					else
						valString = "0";
				}
				else if (valString.startsWith("CLASS="))
				{
					PCClass aClass = null;
					if (valString.length() > 6)
						aClass = getClassNamed(valString.substring(6));
					else
						System.out.println("Error! Cannot determine CLASS!");
					if (aClass != null)
						valString = "1";
					else
						valString = "0";
				}
				else if (valString.startsWith("CLASSLEVEL="))
				{
					valString = valString.substring(11).replace('{', '(').replace('}', ')');
					final PCClass aClass = getClassNamed(valString);
					if (aClass != null)
						valString = aClass.getLevel().toString();
					else
						valString = "0";
				}
				else if (valString.equals("TL"))
				{
					valString = new Integer(totalLevels()).toString();
				}
				else if (valString.equals("HD"))
				{
					valString = new Integer(totalHitDice()).toString();
				}
				else if (valString.equals("SHIELDACHECK"))
				{
					ArrayList aArrayList = getEquipmentOfType("Shield", 1);
					if (aArrayList.size() > 0)
						valString = ((Equipment)aArrayList.get(0)).acCheck().toString();
					else
						valString = "0";
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
					valString = String.valueOf(Globals.loadTypeForStrength(adjStats(Constants.STRENGTH), totalWeight()));
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
				else if (valString.equals("COUNT[STATS]"))
				{
					valString = new Integer(Globals.s_ATTRIBLONG.length).toString();
				}
				else if (valString.equals("COUNT[SKILLS]"))
				{
					skillList.trimToSize();
					valString = new Integer(getSkillList().size()).toString();
				}
				else if (valString.equals("COUNT[FEATS]"))
				{
					featList.trimToSize();
					valString = new Integer(featList.size()).toString();
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
					valString = new Integer(iCount).toString();
				}
				else if (valString.startsWith("COUNT[SPELLSKNOWN") && valString.endsWith("]"))
				{
					int spellCount = countSpellListBook(valString);
					valString = new Integer(spellCount).toString();
				}
				else if (valString.startsWith("COUNT[SPELLSINBOOK") && valString.endsWith("]"))
				{
					valString = valString.substring(18);
					valString = valString.substring(0, valString.length() - 1);
					int sbookCount = countSpellsInBook(valString);
					valString = new Integer(sbookCount).toString();
				}
				else if (valString.startsWith("COUNT[SPELLTIMES") && valString.endsWith("]"))
				{
					valString = valString.substring(6);
					valString = valString.substring(0, valString.length() - 1);
					valString = String.valueOf(countSpellTimes(valString));
				}
				else if (valString.startsWith("COUNT[SPELLBOOKS") && valString.endsWith("]"))
				{
					valString = new Integer(getSpellBooks().size()).toString();
				}
				else if (valString.equals("COUNT[SPELLCLASSES]"))
				{
					int count = 0;
					for (int iii = 0; iii < classList.size(); iii++)
					{
						final PCClass aClass = (PCClass)classList.get(iii);
						if (!aClass.getSpellType().equals(Constants.s_NONE))
							count++;
					}
					valString = String.valueOf(count);
				}
				else if (valString.equals("COUNT[CLASSES]"))
				{
					classList.trimToSize();
					valString = new Integer(classList.size()).toString();
				}
				else if (valString.equals("COUNT[DOMAINS]"))
				{
					characterDomainList.trimToSize();
					valString = new Integer(characterDomainList.size()).toString();
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
						valString = Integer.toString(aList.size());
					else
					{
						StringTokenizer bTok = new StringTokenizer(valString.substring(16, valString.length() - 1), ".", false);
						while (bTok.hasMoreTokens())	//should be ok, assumes last two fields are # and a Param
						{
							String bString = bTok.nextToken();
							if (bString.equalsIgnoreCase("NOT"))
								aList = new ArrayList(removeEqType(aList, bTok.nextToken()));
							else if (bString.equalsIgnoreCase("ADD"))
								aList = new ArrayList(addEqType(aList, bTok.nextToken()));
							else if (bString.equalsIgnoreCase("IS"))
								aList = new ArrayList(removeNotEqType(aList, bTok.nextToken()));
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
										aList.add(eq);
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
							aList = new ArrayList(removeEqType(aList, bTok.nextToken()));
						else if (bString.equalsIgnoreCase("ADD"))
							aList = new ArrayList(addEqType(aList, bTok.nextToken()));
						else if (bString.equalsIgnoreCase("IS"))
							aList = new ArrayList(removeNotEqType(aList, bTok.nextToken()));
						else if (bString.equalsIgnoreCase("EQUIPPED") || bString.equalsIgnoreCase("NOTEQUIPPED"))
						{
							final boolean eFlag = bString.equalsIgnoreCase("EQUIPPED");
							for (int ix = aList.size() - 1; ix >= 0; ix--)
							{
								Equipment anEquip = (Equipment)aList.get(ix);
								if (anEquip.isEquipped() != eFlag)
									aList.remove(anEquip);
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
				else if (valString.equals("CL") && src.startsWith("CLASS:"))
				{
					PCClass aClass = getClassNamed(src.substring(6));
					if (aClass != null)
						valString = aClass.getLevel().toString();
					else
						valString = "0";
				}
				else if (valString.startsWith("EQTYPE"))
				{
					Globals.debugPrint("PlayerCharacter::getVariableValue(" +
						aString + "," + src + ") : " + valString + "\n");
					valString = (new pcgen.io.ExportHandler(null)).replaceTokenEq(valString);
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

				else if (valString.length() > 0)
				{
					if (hasVariable(valString))
						valString = getVariable(valString, true, true, "", "").toString();
					else
					{
						double a = 0;
						try
						{
							a = new Float(valString).doubleValue();
						}
						catch (NumberFormatException exc)
						{
							a = getTotalBonusTo("VAR", valString, true);
						}
						if (a != 0.0)
							valString = new Float(a).toString();
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
//						nextMode = 0;
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
						nextMode = 0;
					else if (aString.charAt(i) == '-')
						nextMode = 1;
					else if (aString.charAt(i) == '*')
						nextMode = 2;
					else if (aString.charAt(i) == '/')
						nextMode = 3;
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
								//What to do here?
						}
				}
				catch (NumberFormatException exc)
				{
//					GuiFacade.showMessageDialog(null, "Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")", Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
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
				total = new Float(Math.min(total.doubleValue(), total1.doubleValue()));
			if (endMode % 10 == 2)
				total = new Float(Math.max(total.doubleValue(), total1.doubleValue()));
			if (endMode % 10 == 3)
			{
				if (total1.doubleValue() < total.doubleValue())
					total = new Float(0.0);
				else
					total = total1;
			}
		}
		if (endMode / 10 > 0)
			total = new Float(total.intValue());

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
		int stat = 0;
		int roll = 0;
		int[] currentStats = stats;
		String diceExpression =
			Globals.getRollMethodExpression(method);

		for (stat = 0; stat < Globals.s_ATTRIBLONG.length; stat++)
		{
			if (!Globals.s_ATTRIBROLL[stat])
			{
				continue;
			}

			currentStats[stat] = 0;
			if (Globals.isPurchaseStatMode())
			{
				currentStats[stat] = 8;
				continue;
			}
			switch (method)
			{
				case 0:
					roll = 0;
					break;
				case 8:
					roll = Globals.getAllStatsValue();
					break;
				default:
					roll = RollingMethods.roll(diceExpression);
					break;
			}
			currentStats[stat] = currentStats[stat] + roll;
		}
		this.setPoolAmount(0);
		this.costPool = 0;
		languages.clear();
		getAutoLanguages();
		setPoolAmount(0);
	}

	private final boolean includeSkill(Skill skill, int level)
	{
		return (level == 2) || skill.isRequired() ||
			(skill.getTotalRank().floatValue() > 0) ||
			((level == 1) && skill.getUntrained().startsWith("Y"));
	}

	private final void addNewSkills(int level)
	{
		List addItems = new LinkedList();
		Iterator skillIter = Globals.getSkillList().iterator();
		Skill aSkill = null;
		while (skillIter.hasNext())
		{
			aSkill = (Skill)skillIter.next();
			if (includeSkill(aSkill, level) &&
				(Globals.binarySearchPObject(skillList, aSkill.getKeyName()) ==
				null))
			{
				addItems.add(aSkill.clone());
			}
		}
		skillList.addAll(addItems);
	}

	private final void removeExcessSkills(int level)
	{
		Iterator skillIter = skillList.iterator();
		Skill skill = null;
		while (skillIter.hasNext())
		{
			skill = (Skill)skillIter.next();
			if (!includeSkill(skill, level))
				skillIter.remove();
		}
	}

	/*
	 */
	public final void populateSkills(int level)
	{
		Globals.sortPObjectList(skillList);
		removeExcessSkills(level);
		addNewSkills(level);
	}

	private int acAbilityMod()
	{
		int acmod = calcStatMod(Constants.DEXTERITY);
		PCClass aClass = null;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			aClass = (PCClass)e.next();
			acmod += Integer.parseInt(aClass.getACForLevel(aClass.getLevel().intValue()));
		}
		int max = modToFromEquipment("MAXDEX");
		if (acmod > max)
			acmod = max;
		acmod += getTotalBonusTo("COMBAT", "AC", true);
		return acmod;
	}

	private int acSizeMod()
	{
		int acmod = modForSize();
		return acmod;
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
				bonus += eq.acCheck().intValue();
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
		weaponMod = bonus;
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
			bonus = calcStatMod(Constants.DEXTERITY);
		}
		int load = Constants.LIGHT_LOAD;
		if (Globals.isApplyLoadPenaltyToACandSkills())
			Globals.loadTypeForStrength(adjStats(Constants.STRENGTH), totalWeight());

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
				continue;
			if (typeName.equals("AC"))
				bonus += eq.getAcMod().intValue();
			else if (typeName.equals("ACCHECK"))
				bonus += eq.acCheck().intValue();
			else if (typeName.equals("SPELLFAILURE"))
				bonus += eq.spellFailure().intValue();
			else if (typeName.equals("MAXDEX"))
			{
				old = eq.getMaxDex().intValue();
				if (old == 100)
					continue;
				if (used == 0 || bonus > old)
					bonus = old;
				used = 1;
			}
		}
		if (typeName.equals("SPELLFAILURE"))
			bonus += getTotalBonusTo("MISC", "SPELLFAILURE", true);
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
				bonus = wp.bonusTo(aType, aName);
		}
		return bonus;
	}

	private void setDeity(String aString)
	{
		boolean bFound = false;
		for (int i = 0; i < Globals.getDeityList().size(); i++)
		{
			if (Globals.getDeityList().get(i).toString().equals(aString))
			{
				setDeity((Deity)Globals.getDeityList().get(i));
				bFound = true;
				break;
			}
		}
		if (!bFound)
		{
			if (!aString.equals(Constants.s_NONE))
			{
				setDeity(Constants.s_NONE);
				GuiFacade.showMessageDialog(null, "Deity not found: " + aString + s_CHECKLOADEDCAMPAIGNS, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void changeDeity(boolean addIt)
	{
		if (deity != null && getDeity().getSpecialAbility().length() > 0)
		{
			ArrayList aArrayList = new ArrayList();
			final StringTokenizer aTok = new StringTokenizer(getDeity().getSpecialAbility(), "|", false);
			while (aTok.hasMoreTokens())
				aArrayList.add(aTok.nextToken());
			for (int i = 0; i < 20; i++)
				changeSpecialAbilitiesForLevel(i, addIt, aArrayList);
		}
	}

	/*
	 * returns true if Equipment is in the primary weapon list
	 */
	public boolean isPrimaryWeapon(Equipment eq)
	{
		if (eq == null)
			return false;
		for (Iterator e = primaryWeapons.iterator(); e.hasNext();)
		{
			Equipment eqI = (Equipment)e.next();
			if (eqI.getName().equals(eq.getName()) &&
				eqI.getHand() == eq.getHand())
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
			return false;
		for (Iterator e = secondaryWeapons.iterator(); e.hasNext();)
		{
			Equipment eqI = (Equipment)e.next();
			if (eqI.getName().equals(eq.getName()) &&
				eqI.getHand() == eq.getHand())
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
			return false;

		for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
		{
			if (foundIt)
				break;
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

	public void setClassList(java.util.ArrayList classList)
	{
		this.classList = classList;
	}


}

