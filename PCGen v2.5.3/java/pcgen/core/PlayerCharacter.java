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
 */

package pcgen.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.*;
import pcgen.util.Delta;
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

	/*
         * 1:
         * hit points are no longer written with the CON modifier.
         * 2:
         * skills are saved by class
         */
//	private int PcgWriteVersion = 2;	// Version of file we will write
	private int PcgReadVersion;		// Version of file being read

	private int currentHP = 0;
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
	private TreeSet templateAutoLanguages = new TreeSet();
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
	private Integer experience = new Integer(0);
	private int weaponMod = 0;
	private String[] movementTypes;
	private Integer[] movements;
	private boolean dirtyFlag = false; //Whether the character has changed since last saved.
	private String fileName = ""; //This may be different from character name...
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

	/** Used only for legacy (pre 2.3.0) Domain class pcg files */
	private int ignoreDomainClassLine = 0;

	private String lastLineParsed = "";
	private String lastFnCalled = "";

	private Race race = null;
	private ArrayList templateList = new ArrayList(); // of Template
	private ArrayList classList = new ArrayList(); // of Class
	private ArrayList featList = new ArrayList(); // of Feat
	private ArrayList characterDomainList = new ArrayList(); // of CharacterDomain
	private ArrayList allSpells = new ArrayList(); // of Spell

	private Deity deity = null;
	private ArrayList skillList = new ArrayList(); // of Skill
	//private Equipment primaryWeapon = null;
	//private Equipment secondaryWeapon[] = new Equipment[1];
	private ArrayList primaryWeapons = new ArrayList();
	private ArrayList secondaryWeapons = new ArrayList();
	private TreeMap equipmentList = new TreeMap(); // of Equipment

	private boolean useMonsterDefault = Globals.isMonsterDefault();
	private ArrayList notesList = new ArrayList();


	private final static String s_CHECKLOADEDCAMPAIGNS = ". Check loaded campaigns.";

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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 08-03-02
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
			wp.addAll(race.getSelectedWeaponProfBonus());
		}
		//
		// Add any selected class bonus weapons
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			wp.addAll(aClass.getSelectedWeaponProfBonus());
		}

		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain)e.next();
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				wp.addAll(aDomain.getSelectedWeaponProfBonus());
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

		if (getQualifyList().contains(qualifierItem))
			return true;
		else
			return false;
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

	public void equipmentListAdd(Equipment anEquip)
	{
		equipmentList.put(anEquip.getKeyName(), anEquip);
	}

	public void equipmentListRemove(Equipment anEquip)
	{
		equipmentList.remove(anEquip.getKeyName());
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

		if (canReassignTemplateFeats())
		{
		for (Iterator e = getTemplateList().iterator(); e.hasNext();)
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
				stat = stats[idx];
			}
		}
		return stat;
	}

	public void setStats(int intArray[])
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

	public String getGender()
	{
		return gender;
	}

	public void setGender(String aString)
	{
		gender = aString;
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
		for (int x = 0; x < times.length; x++)
			times[x] = 0;

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

		for (int y = 0; y < templateList.size(); y++)
		{
			ArrayList SAs = (ArrayList)((PCTemplate)templateList.get(y)).getSAs(totalLevels(), totalHitDice(), getSize());
			times = Utility.resize(times, SAs.size());
			for (int z = 0; z < SAs.size(); z++)
			{
				String aString = (String)SAs.get(z);
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

			int [] varValue = null;
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
		int iCount = 0;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			int subfeatCount = aFeat.getAssociatedList().size();
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
		return characterDomainList;
	}

	/**
	 * Selector
	 *
	 * <br>author: Thomas Behr 09-03-02
	 *
	 * @return all spells
	 */
	public ArrayList getAllSpells()
	{
		return allSpells;
	}

	public ArrayList getSkillList()
	{
		return skillList;
	}

	public Deity getDeity()
	{
		return deity;
	}

	public TreeMap getEquipmentList()
	{
		return equipmentList;
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

		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)e.next();
			String aType = aTemplate.getType();

			if (!aType.equals(""))
			{
				critterType.append("|").append(aType);
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

	public int hideModForSize()
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

	public int conModForSize()
	{
		return getModifierForSizeIncrease("ConIncrease");
	}

	public int dexModForSize()
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

	public PCClass getClassDisplayNamed(String aString)
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

	public PCClass getSpellClassAtIndex(int ix)
	{
		PCClass aClass = null;
		for (Iterator classIter = classList.iterator(); classIter.hasNext();)
		{
			aClass = (PCClass)classIter.next();
			if (aClass.spellList().size() > 0)
				ix--;
			else
				aClass = null;
			if (ix == -1)
				break;
		}
		if (ix == -1 && aClass != null && aClass.spellList().size() > 0)
			return aClass;
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
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			final StringTokenizer aTok = new StringTokenizer((String)e.next(), "|", false);
			aTok.nextToken(); //src
			aTok.nextToken(); //subSrc
			if (((String)aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
			{
				return true;
			}
		}
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			for (Iterator e2 = aFeat.getVariableList().iterator(); e2.hasNext();)
			{
				final String aString = (String)e2.next();
				final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
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

	public Float getVariable(String variableString, boolean isMax, boolean includeBonus, String matchSrc, String matchSubSrc)
	{
		Float value = new Float(0.0);
		boolean found = false;
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

		//
		// Now check the feats to see if they modify the variable
		//
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			for (Iterator e2 = aFeat.getVariableList().iterator(); e2.hasNext();)
			{
				final StringTokenizer aTok = new StringTokenizer((String)e2.next(), "|", false);
				if (aTok.countTokens() > 2)
				{
					aTok.nextToken(); //level (should always be 0 atm)
					if ((aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
					{
						final String sString = aTok.nextToken();
						final Float newValue = getVariableValue(sString, "");
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
								value = new Float(value.doubleValue() + getVariableValue(sString, "").doubleValue());
							}
							loopValue = 0;
							loopVariable = "";
						}
					}
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

	/*
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 09-03-02
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 09-03-02
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 09-03-02
	 */
	public ArrayList addEqType(ArrayList aList, String aString)
	{
		TreeSet aSet = new TreeSet(aList);

		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.typeStringContains(aString))
				aSet.add(eq);
		}

		return new ArrayList(aSet);
	}

	private Integer getMovement(int moveIdx)
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
		int move = getMovement(moveIdx).intValue(); // get racial base movement
		ArrayList aArrayList = getEquipmentOfType("Armor", 1); // get a list of all equipped Armor
		Iterator armors = null;
		int bonus = 0;
		int pcLoad = Globals.loadTypeForStrength(adjStats(Constants.STRENGTH), totalWeight());

		if (aArrayList.size() == 0 && pcLoad == 0) // assume any armor or load cancels MOVE:
		{
			for (armors = classList.iterator(); armors.hasNext();)
			{
				PCClass aClass = (PCClass)armors.next();
				move += Integer.parseInt(aClass.getMoveForLevel(aClass.getLevel().intValue())); // this movement is cumulative
			}
		}
		else // assume BONUS:MOVE can be applied to less than label armor
		{
			// pcLoad will equal the greater of encumberance load type or armor type
			for (armors = aArrayList.iterator(); armors.hasNext();)
			{
				Equipment armor = (Equipment)armors.next();
				if (armor.isHeavy())
					pcLoad = Math.max(pcLoad, Constants.HEAVY_LOAD);
				else if (armor.isMedium())
					pcLoad = Math.max(pcLoad, Constants.MEDIUM_LOAD);
				else if (armor.isLight())
					pcLoad = Math.max(pcLoad, Constants.LIGHT_LOAD);
				else
					pcLoad = Constants.OVER_LOAD;

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
		bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false); // always get this bonus
		switch (pcLoad)
		{
			// NOTE: no breaks on purpose! These are cumulative and cascade together!!!!!
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

	/**
	 * @deprecated Use new {@link ACCalculator} class instead
	 * <br>author: Thomas Behr 07-02-01
	 */
	public int acMod()
	{
		int acmod = makeAcMod();
                /*
                 * since we removed all NATURALARMOR tags
                 * this method does not exist anymore
                 *
                 * author: Thomas Behr 23-03-02
                 */
//  		for (int x = 0; x < templateList.size(); x++)
//  			acmod += ((PCTemplate)templateList.get(x)).getNaturalArmor(totalLevels(), totalHitDice(), getSize());
		return acmod;
	}

	/**
	 * @deprecated Use new {@link ACCalculator} class instead
	 * <br>author: Thomas Behr 07-02-01
	 */
	public int makeAcMod()
	{
		int acmod = modForSize();
		int max = modToFromEquipment("MAXDEX");
		int ab = calcStatMod(Constants.DEXTERITY);
		if (ab > max)
			ab = max;
		acmod += ab;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			final int level = aClass.getLevel().intValue();
			acmod += Integer.parseInt(aClass.getACForLevel(level));
		}
//		acmod += getTotalBonusTo("COMBAT", "AC", true);  now included in equipmentMod!
		return acmod;
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
				attackString.append('/');
			if (mod > 0)
				attackString.append('+');
			attackString.append(mod+combat);
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 08-03-02
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
	}

	public void incrementClassLevel(int mod, PCClass aClass)
	{
		incrementClassLevel(mod, aClass, false);
	}

	public void incrementClassLevel(int mod, PCClass aClass, boolean bSilent)
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
		catch (Exception exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}


	}

//	public void setClassLevel(PCClass aClass)
//	{
//		if (!aClass.isQualified())
//			return;
//		PCClass bClass = getClassNamed(aClass.getName());
//		if (bClass == null)
//		{
//			bClass = (PCClass)aClass.clone();
//			classList.add(bClass);
//		}
//		bClass.setLevel(aClass.getLevel());
//		ArrayList templateFeats = null;
//		PCTemplate aTemplate = null;
//		for (int y = 0; y < templateList.size(); y++)
//		{
//			aTemplate = (PCTemplate)templateList.get(y);
//			templateFeats = aTemplate.feats(totalLevels(), totalHitDice(), getSize());
//			for (int x = 0; x < templateFeats.size(); x++)
//				modFeatsFromList((String)templateFeats.get(x), false, false);
//		}
//	}

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
		double i = (double)(totalLevels() + 3.0 + totalHitDice());
		Skill aSkill = Globals.getSkillNamed(skillName);
		if (aSkill.isExclusive().startsWith("Y"))
		{
			i = 3.0 + totalHitDice();
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				final PCClass bClass = (PCClass)e.next();
				if (aSkill.isClassSkill(bClass))
					i += bClass.getLevel().doubleValue();
			}
			if (i == 3.0 + totalHitDice())
				i = (totalLevels() + 3.0 + totalHitDice()) / 2.0;
		}
		else if (!aSkill.isClassSkill(classList) && (aSkill.costForPCClass(aClass).intValue() == 1))
			i = (int)(i / 2.0);
		else if (!aSkill.isClassSkill(classList))
			i = i / 2.0;
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
		final int iConMod = calcStatMod(Constants.CONSTITUTION);

		int total = 0;
		if (race.hitDice() != 0)
			total = race.calcHitPoints(iConMod);
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
		if (race != null)
		{
			if (canReassignRacialFeats())
			{
			final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(), "|", false);
			while (aTok.hasMoreTokens())
			{
				final String aString = (String)aTok.nextToken();
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
			getNaturalWeapons();
			if (canReassignRacialFeats())
			{
			final StringTokenizer aTok = new StringTokenizer(getRace().getFeatList(), "|", false);
			while (aTok.hasMoreTokens())
			{
				final String aString = (String)aTok.nextToken();
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
		}

		adjustMoveRates();

		setAggregateFeatsStable(false);
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
	}

	public void changeSpecialAbilitiesForLevel(int level, boolean addIt, Collection aArrayList)
	{

		if (Globals.getCurrentPC() == null)
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
					final String thisString = (String)aStrTok.nextToken();
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
							final String saName = sa.getName() + " (" + sa.getDesc() + ")";
							if (sa != null)
							{
								if ((addIt == false) && !aDesc.equals(""))
								{
									cString = new String(eString);
									dString = new String(saName);
									for (int i = 0; i < 10; i++)
									{
										cString = cString.replace((char)('0' + i), ' ');
										dString = dString.replace((char)('0' + i), ' ');
									}
									if (addIt == false || (addIt && cString.equals(dString)))
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

	public void changeSpecialAbilityNamed(String aString, boolean addIt)
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
							sInt = anInt + sInt;
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
		/*int i = 0;
//totalLevels = race.hitDice();
for (Iterator e = classList.iterator(); e.hasNext();)
{
final PCClass aClass = (PCClass)e.next();
if (!aClass.isMonster())
totalLevels += aClass.getLevel().intValue();
}*/
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
		if (rhd>0)
			hitDieRatio = (float)totalHitDice()/rhd;
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
			if (((PCTemplate)templateList.get(x)).getSR(totalLevels(), totalHitDice(), getSize()) > SR)
				SR = ((PCTemplate)templateList.get(x)).getSR(totalLevels(), totalHitDice(), getSize());
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

	public Feat getFeatAutomaticNamed(String featName)
	{
		return getFeatNamedInList(featAutoList(), featName);
	}

	public Feat getFeatNonAggregateNamed(String featName)
	{
		return getFeatNamedInList(featList, featName);
	}


	public Feat getFeatNamedInList(ArrayList aFeatList, String featName)
	{
		for (Iterator e = aFeatList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
			{
				return aFeat;
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
		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
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
			subName = featName.substring(featName.indexOf("(") + 1, featName.lastIndexOf(")")); //we want what is inside the outermost parens.
			featName = featName.substring(0, featName.indexOf("(") - 1);
			aFeat = getFeatNonAggregateNamed(featName);
		}

		//
		// aFeat==null means we don't have this feat, so we need to add it
		//
		if (addIt && (aFeat == null))
		{
			// adding feat for first time
			aFeat = (Feat)Globals.getFeatNamed(featName);
			if (aFeat == null)
			{
				aFeat = (Feat)Globals.getFeatNamed(oldName);
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
		int j = (int)(aFeat.getAssociatedList().size() * aFeat.getCost()) + feats;
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
		//		if ((autoFeat == null) || !autoFeat.getAssociatedList().contains(aProf.getName()))
		//		{
		//			if (!aFeat.getAssociatedList().contains(aProf.getName()))
		//			{
		//				aFeat.getAssociatedList().add(aProf.getName());
		//				iCount += 1;
		//			}
		//		}
		//	}
		//	j += iCount - (int)(aFeat.getAssociatedList().size() * aFeat.getCost());
		//}

		// process ADD tags from the feat definition
		if (addIt == false)
			aFeat.modAdds(addIt);

		if (addAll == false)
		{
			if (subName.equals(""))
			{
				// Allow sub-choices
				aFeat.modChoices(addIt);
			}
			else
			{
				if (addIt && !aFeat.getAssociatedList().contains(subName))
				{
					aFeat.getAssociatedList().add(subName);
				}
				else if (!addIt && aFeat.getAssociatedList().contains(subName))
				{
					aFeat.getAssociatedList().remove(subName);
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

		if (aFeat.isMultiples() && (addAll == false))
			retVal = (aFeat.getAssociatedList().size() > 0)? 1 : 0;

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
			int associatedListSize = aFeat.getAssociatedList().size();
			for (Iterator e1 = featList.iterator(); e1.hasNext();)
			{
				final Feat myFeat = (Feat)e1.next();
				if (myFeat.getName().equals(aFeat.getName()))
				{
					associatedListSize = myFeat.getAssociatedList().size();
				}
			}

			j -= (int)(associatedListSize * aFeat.getCost());
		}
		if ((addAll == false) && !aFeat.getName().equals("Spell Mastery"))
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
			if (aFeat == null)
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
				if (addIt == false)
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
								aFeat.getAssociatedList().add(wp.getName());
							else
								aFeat.getAssociatedList().remove(wp.getName());
						}
					}
					else
					{
						if (addIt)
							aFeat.getAssociatedList().add(aString);
						else
							aFeat.getAssociatedList().remove(aString);
					}
				}
				if (aFeat.getName().endsWith("Weapon Proficiency"))
				{
					for (Iterator e = aFeat.getAssociatedList().iterator(); e.hasNext();)
					{
						String wprof = (String)e.next();
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
				if (all == false && aFeat.isMultiples() == false)
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

	private void removeNaturalWeapons()
	{
		for (Iterator e = getRace().getNaturalWeapons().iterator(); e.hasNext();)
		{
			equipmentList.remove(((Equipment)e.next()).getKeyName());
		}
	}

	public void getNaturalWeapons()
	{
		for (Iterator e = getRace().getNaturalWeapons().iterator(); e.hasNext();)
		{
			final Equipment anEquip = (Equipment)e.next();
			equipmentList.put(anEquip.getKeyName(), anEquip);
		}
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
				final PCClass aClass = (PCClass)getClassDisplayNamed((String)e.next());
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
	public SortedSet getBonusLanguages()
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

		bonusLangsb.removeAll(languages);
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
		int i = adjStats(Constants.INTELLIGENCE) / 2 - 5;
		Skill speakLang = getSkillNamed("Speak Language");
		Race pcRace = getRace();
		if (i < 0)
			i = 0;
		if (speakLang != null)
		{
			i += speakLang.getTotalRank().intValue();
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

	public SortedSet getAutoWeaponProfs(ArrayList aFeatList)
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
			for (Iterator e = aRace.getSelectedWeaponProfBonus().iterator(); e.hasNext();)
			{
				final String aString = (String)e.next();
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
		}

		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			final String sizeString = "FDTSMLHGC";
			for (Iterator e1 = aClass.getWeaponProfAutos().iterator(); e1.hasNext();)
			{
				String aString = (String)e1.next();
				final int lastComma = aString.lastIndexOf(",");
				boolean flag = (lastComma == -1);
				if ((flag == false) && (race != null))
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
				if (flag == true)
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
			for (Iterator e1 = aClass.getSelectedWeaponProfBonus().iterator(); e1.hasNext();)
			{
				final String aString = (String)e1.next();
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
			for (Iterator e1 = aFeat.getSelectedWeaponProfBonus().iterator(); e1.hasNext();)
			{
				final String aString = (String)e1.next();
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
				for (Iterator e1 = aDomain.getSelectedWeaponProfBonus().iterator(); e1.hasNext();)
				{
					final String aString = (String)e1.next();
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
		if (stableList != null)
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

	public void addWeaponProfToList(ArrayList aFeatList, String aString, boolean isAuto)
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
			for (;;)
			{
				Feat aFeat = getFeatNamedInList(aFeatList, featName);
				if (aFeat != null)
				{
					//
					// No need to add to list, if multiples not allowed
					//
					if (aFeat.isMultiples())
					{
						if (!aFeat.getAssociatedList().contains(aString))
						{
							aFeat.getAssociatedList().add(aString);
							java.util.Collections.sort(aFeat.getAssociatedList());
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
						aFeat.getAssociatedList().add(aString);
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
				for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
				{
					Spell aSpell = (Spell)e1.next();
					if (((school.length() == 0 || school.equals(aSpell.getSchool())) ||
						(subschool.length() == 0 || subschool.equals(aSpell.getSubschool()))))
					{
						String levelString = aSpell.levelForClass(cName, aClass.getName());
						StringTokenizer aTok = new StringTokenizer(levelString, ",", false);
						while (aTok.hasMoreTokens())
						{
							aTok.nextToken();
							int aLevel = Integer.parseInt(aTok.nextToken());
							if (aLevel >= minLevel && aLevel <= maxLevel)
							{
								Globals.debugPrint(school + "==" + aSpell.getSchool() + " " + minLevel + "==" + aSpell.levelForClass(cName, aClass.getName()));
								aArrayList.add(aSpell);
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

		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
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
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (!eq.isHeaderParent())
				totalValue += eq.getCost().floatValue() * eq.qty().floatValue();
		}
		return new Float(totalValue);
	}

	public Equipment getEquipmentNamed(String aString)
	{
		Equipment match = (Equipment)equipmentList.get(aString);
		if ((match != null) && aString.equals(match.getName()))
			return match;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.getName().equals(aString))
				return eq;
		}
		return null;
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
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
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
		for (e = bArrayList.iterator(); e.hasNext();)
		{
			eq = (Equipment)e.next();
			if (eq.isEquipped())
				hands += eq.getHands();
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
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq1 = (Equipment)mapIter.next();
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
						hands = 2 * wp.handsNeeded();
					else
						hands = 2;
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
		//primaryWeapon = null;
		//secondaryWeapon = new Equipment[race.getHands() - 1];
		primaryWeapons.clear();
		secondaryWeapons.clear();
		ArrayList unequippedPrimary = new ArrayList();
		ArrayList unequippedSecondary = new ArrayList();
		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			final Equipment eq = (Equipment)mapIter.next();
			if (!eq.isWeapon())
			{
				continue;
			}
			//if (!eq.isEquipped())
			//{
			//	continue;
			//}
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

			//if (eq.getHand() == Equipment.PRIMARY_HAND
			//	|| (eq.getHand() == Equipment.BOTH_HANDS && primaryWeapon == null)
			//	|| eq.getHand() == Equipment.TWOWEAPON_HANDS)
			//	primaryWeapon = eq;
			//else if (eq.getHand() == Equipment.BOTH_HANDS && primaryWeapon != null)
			//	secondaryWeapon[x++] = eq;
			//if (eq.getHand() == Equipment.SECONDARY_HAND)
			//	secondaryWeapon[x++] = eq;
			//if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
			//	for (y = 0; y < eq.getNumberEquipped() - 1; y++)
			//		secondaryWeapon[x++] = eq;

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
		final String prefix = bonusType.toUpperCase() + "." + bonusName.toUpperCase() + ".";
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
			getClassBonusTo(bonusType, bonusName);
			getEquipmentBonusTo(bonusType, bonusName, stacks);
			getFeatBonusTo(bonusType, bonusName, stacks);
			getTemplateBonusTo(bonusType, bonusName, stacks);
			getDomainBonusTo(bonusType, bonusName);
			getRace().bonusTo(bonusType, bonusName);
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
		catch (Exception exc)
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

	public int getClassBonusTo(String type, String aName)
	{
		int bonus = 0;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			bonus += aClass.getBonusTo(type, aName, aClass.getLevel().intValue());
		}
		return bonus;
	}

	public int getDomainBonusTo(String type, String aName)
	{
		int bonus = 0;
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain)e.next();
			Domain aDomain = aCD.getDomain();
			if (aDomain != null)
				bonus += aDomain.bonusTo(type, aName);
		}
		return bonus;
	}

	private void mergeBonusMap(HashMap map, boolean stacks)
	{
		for (Iterator i = map.keySet().iterator(); i.hasNext();)
		{
			final String bonusType = i.next().toString();
			int iBonus = Integer.parseInt((String)map.get(bonusType));

			final String aKey = (String)bonusMap.get(bonusType);
			if (aKey != null)
			{
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

	public int getEquipmentBonusTo(String type, String aName, boolean stacks)
	{
		int bonus = 0;

		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped())
			{
				if (stacks)
				{
					bonus += eq.bonusTo(type, aName, true);
					mergeBonusMap(eq.getBonusMap(), true);
					bonus += eq.bonusTo(type, aName, false);
					mergeBonusMap(eq.getBonusMap(), true);
				}
				else
				{
					bonus = Math.max(bonus, eq.bonusTo(type, aName, true));
					mergeBonusMap(eq.getBonusMap(), false);
					bonus = Math.max(bonus, eq.bonusTo(type, aName, false));
					mergeBonusMap(eq.getBonusMap(), false);
				}
			}
		}
		return bonus;
	}

	public int getTemplateBonusTo(String type, String aName, boolean subSearch)
	{

		PCTemplate t = null;
		int i = 0;
		final Iterator iterator = templateList.iterator();

		for (Iterator e = iterator; e.hasNext();)
		{
			t = (PCTemplate)e.next();

			int j = t.bonusTo(type, aName);
			if (j == 0)
				j = t.bonusTo(type, "LIST");
			int k = Math.max(1, (int)(t.getAssociatedList().size() * t.getCost()));
			if (subSearch && t.getAssociatedList().size() > 0)
			{
				k = 0;
				for (Iterator f = t.getAssociatedList().iterator(); f.hasNext();)
				{
					String aString = f.next().toString();
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
		final Iterator iterator = aggregateFeatList().iterator();
		for (Iterator e = iterator; e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			int j = aFeat.bonusTo(type, aName);
			if (j == 0)
				j = aFeat.bonusTo(type, "LIST");
			int k = Math.max(1, (int)(aFeat.getAssociatedList().size() * aFeat.getCost()));
			if (subSearch && aFeat.getAssociatedList().size() > 0)
			{
				k = 0;
				for (Iterator f = aFeat.getAssociatedList().iterator(); f.hasNext();)
				{
					String aString = f.next().toString();
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
		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			final Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() && eq.isArmor())
			{
				return new Integer(totalAC());
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
					}

				case 11:	/*P classlevel/5 + (classlevel +3)/5 Wheel of Time method. */
					int tempLvl = aClass.getLevel().intValue();
					i += ((tempLvl / 5) + ((tempLvl + 3) / 5));
					break;
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

	/** return value indicates if book was actually added or not */
	public boolean addSpellBook(String aName)
	{
		if (aName.length() > 0 && !spellBooks.contains(aName))
		{
			spellBooks.add(aName);
			return true;
		}
		return false;
	}

	/** return value indicates whether or not a book was actually removed */
	public boolean delSpellBook(String aName)
	{
		if (aName.length() > 0 && !aName.equals(Globals.getDefaultSpellBook()) && spellBooks.contains(aName))
		{
			spellBooks.remove(aName);
			for (Iterator i = classList.iterator(); i.hasNext();)
			{
				PCClass aClass = (PCClass)i.next();
				for (Iterator ii = aClass.spellList().iterator(); ii.hasNext();)
				{
					Spell aSpell = (Spell)ii.next();
					aSpell.removeFromSpellBook(aName);
				}
			}
			return true;
		}
		return false;
	}

	public String addSpell(String className, int spellLevel, ArrayList aFeatList, String spellName, String bookName)
	{
		PCClass aClass = null;
		Spell aSpell = null;
		if (spellName == null || spellName.length() == 0)
			return "Invalid spell name.";
		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (className != null)
		{
			aClass = (PCClass)getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")).trim());
		}
		if (aClass == null)
			return "No class named " + className;
		aSpell = aClass.getSpellNamed(spellName);
		if (aSpell == null && bookName.equals(Globals.getDefaultSpellBook()))
			aSpell = (Spell)Globals.getSpellNamed(spellName);
		if (aSpell == null)
			return "Could not find " + spellName + " for " + className;
		if (aFeatList != null)
			for (Iterator i = aFeatList.iterator(); i.hasNext();)
			{
				Feat aFeat = (Feat)i.next();
				spellLevel += aFeat.getAddSpellLevel();
			}
		int known = 0;
		if (Globals.isSSd20Mode())
			known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel, bookName);
		else
			known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel) + aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel);
		int cast = aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel, bookName);
		int listNum = aClass.memorizedSpellForLevelBook(spellLevel, bookName);
		boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		if (Globals.isSSd20Mode() && !isDefault)
		{
			if ((listNum + aSpell.getCastingThreshold()) > known)
				return "Your maximum space for this category is:" + known;
		}
		else if ((!aClass.getMemorizeSpells() && listNum >= known) ||
			(aClass.getMemorizeSpells() && !isDefault && listNum >= cast))
			return "You cannot put any additional spells in this list.";
		if (!aSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), -1) && (aClass.prohibitedStringContains(aSpell.getSchool()) || aClass.prohibitedStringContains(aSpell.getDescriptorList())))
		{
			return spellName + " is prohibited.";
		}
		boolean addIt = true;
		Spell bSpell = aClass.getSpellNamed(aSpell.getName());
		if (bSpell != null)
		{
			if (isDefault)
				return "The Known Spells spellbook contains all spells of this level that you know. You " +
					"cannot place spells in multiple times.";

			if (cast >= 0 && known == 0 && listNum < aClass.getKnownSpellsFromSpecialty() &&
				!bSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), -1) &&
				(!aClass.getSpellType().equalsIgnoreCase("Divine") || spellLevel > 0))
				return "First " + aClass.getKnownSpellsFromSpecialty() + " spells in book must come from specialty (" + aClass.getSpecialtyListString() + ")";

			if (cast >= 0 && listNum < aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel) && !bSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), -1) && (!aClass.getSpellType().equalsIgnoreCase("Divine") || spellLevel > 0))
				return "First " + aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel) + " spells in book must come from specialty (" + aClass.getSpecialtyListString() + ")";

			if (aClass.prohibitedStringContains(bSpell.getSchool()))
				return "This spell is prohibited.";
			bSpell.addToSpellBook(bookName, !isDefault);
			bSpell.selectSpellBook(bookName);
			addIt = false;
		}
		if (addIt)
		{
			if (listNum < known ||
				(known == 0 && cast > 0) ||
				(aClass.getMemorizeSpells() && isDefault))
			{
				if (aClass.getKnownSpellsFromSpecialty() > 0)
				{
					if (!aSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), -1))
					{
						int num = aClass.getSpellsInSpecialtyForLevel(spellLevel);
						if (num < aClass.getKnownSpellsFromSpecialty())
							return "First " + aClass.getKnownSpellsFromSpecialty() + " spells known must come from specialty (" + aClass.getSpecialtyList().toString() + ")";
					}
				}

				if (aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel) > 0)
				{
					if (!aSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), -1))
					{
						int num = aClass.getSpellsInSpecialtyForLevel(spellLevel);
						if (num < aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel))
							return "First " + aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), spellLevel) + " spells known must come from specialty (" + aClass.getSpecialtyList().toString() + ")";
					}
				}
				Spell newSpell = (Spell)aSpell.clone();
				if (aClass.getCastAs().length() > 0)
					className = aClass.getCastAs();
				String aString = className + "," + spellLevel;
				newSpell.setClassLevels(aString);
				aClass.spellList().add(newSpell);
				newSpell.addToSpellBook(bookName, !isDefault);
				newSpell.selectSpellBook(bookName);
			}
			else
				return "You cannot memorize any more spells in this book for this level.";
		}
		return "";
	}

	public String delSpell(String className, int spellLevel, ArrayList aFeatList, String spellName, String bookName)
	{
		PCClass aClass = null;
		Spell aSpell = null;
		if (spellName == null || spellName.length() == 0)
			return "Invalid spell name.";
		if (bookName == null || bookName.length() == 0)
			return "Invalid spell book name.";
		if (className != null)
		{
			aClass = (PCClass)getClassNamed(className);
			if (aClass == null && className.lastIndexOf("(") > -1)
				aClass = getClassNamed(className.substring(0, className.lastIndexOf("(")).trim());
		}
		if (aClass == null)
			return "No class named " + className;
		aSpell = aClass.getSpellNamed(spellName);
		if (aSpell == null)
			return "Could not find " + spellName + " for " + className;
		if (aFeatList != null)
			for (Iterator i = aFeatList.iterator(); i.hasNext();)
			{
				Feat aFeat = (Feat)i.next();
				spellLevel += aFeat.getAddSpellLevel();
			}

		boolean isDefault = bookName.equals(Globals.getDefaultSpellBook());
		if (isDefault && aClass.isAutoKnownSpell(aSpell.getName(), spellLevel))
		{
			return aSpell.getName() + " cannot be removed from " + bookName;
		}
		aSpell.removeFromSpellBook(bookName);
		if (aSpell.getSpellBooks().size() == 0)
			aClass.spellList().remove(aSpell);
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
							classFeat.setFeatType(Feat.FEAT_VIRTUAL);
							if (!classFeat.getName().equalsIgnoreCase(featName))
							{
								final int i = featName.indexOf("(");
								final int j = featName.indexOf(")");
								if (i>-1 && j>-1)
								{
									final StringTokenizer aTok = new StringTokenizer(featName.substring(i+1,j),",",false);
									while(aTok.hasMoreTokens()) {
										final String a = aTok.nextToken();
										if (!classFeat.getAssociatedList().contains(a))
											classFeat.getAssociatedList().add(a);
									}
								}
							}
							vFeatList.add(classFeat);
						}
					}
				}
			}
		}
		for (Iterator e = equipmentList.values().iterator(); e.hasNext();)
		{
			final Equipment aE = (Equipment)e.next();
			if (aE.isEquipped())
			{
				for (Iterator e1 = aE.getVFeatList().iterator(); e1.hasNext();)
				{
					final String featName = e1.next().toString();
					final Feat aFeat = Globals.getFeatNamed(featName);
					if (aFeat != null)
					{
						aFeat.setFeatType(Feat.FEAT_VIRTUAL);
						if (!aFeat.getName().equalsIgnoreCase(featName))
						{
							final int i = featName.indexOf("(");
							final int j = featName.indexOf(")");
							if (i>-1 && j>-1)
							{
								final StringTokenizer aTok = new StringTokenizer(featName.substring(i+1,j),",",false);
								while(aTok.hasMoreTokens())
								{
									final String a = aTok.nextToken();
									if (!aFeat.getAssociatedList().contains(a))
										aFeat.getAssociatedList().add(a);
								}
							}
						}
						vFeatList.add(aFeat);
					}
				}
			}
		}
		final StringTokenizer raceTok = new StringTokenizer(getRace().getVFeatList(), "|", false);
		while (raceTok.hasMoreTokens())
		{
			final String featName = raceTok.nextToken();
			final Feat aFeat = Globals.getFeatNamed(featName);
			if (aFeat != null)
			{
				aFeat.setFeatType(Feat.FEAT_VIRTUAL);
				if (!aFeat.getName().equalsIgnoreCase(featName))
				{
					final int i = featName.indexOf("(");
					final int j = featName.indexOf(")");
					if (i>-1 && j>-1)
					{
						final StringTokenizer aTok = new StringTokenizer(featName.substring(i+1,j),",",false);
						while(aTok.hasMoreTokens())
						{
							final String a = aTok.nextToken();
							if (!aFeat.getAssociatedList().contains(a))
								aFeat.getAssociatedList().add(a);
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
		if ( featName.endsWith(")"))
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
					aFeat.getAssociatedList().add(subName);
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
				if (aFeat.isStacks() || !aFeat.getAssociatedList().contains(subName))
				{
					aFeat.getAssociatedList().add(subName);
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
				String aString = (String)e1.next();
				if (aString.lastIndexOf("|") == -1)
				{
					continue;
				}

				final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
				final int i = Integer.parseInt(aTok.nextToken());
				if (i > aClass.getLevel().intValue())
				{
					continue;
				}

				addToAutoFeatList(autoFeatList, aTok.nextToken());
			}
		}

		if (!canReassignTemplateFeats())
		{
			for (Iterator e = templateList.iterator(); e.hasNext();)
			{
				setStableAutomaticFeatList(autoFeatList);

				final PCTemplate aTemplate = (PCTemplate)e.next();
				ArrayList templateFeats = aTemplate.feats(totalLevels(), totalHitDice(), getSize());
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
		for (Iterator e = ((ArrayList)featList.clone()).iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (aFeat != null)
			{
				aHashMap.put(aFeat.getKeyName(), aFeat);
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
				for (Iterator e1 = virtualFeat.getAssociatedList().iterator(); e1.hasNext();)
				{
					Object anObject = e1.next();
					if (aggregateFeat.isStacks() || !aggregateFeat.getAssociatedList().contains(anObject))
					{
						aggregateFeat.getAssociatedList().add(anObject);
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
				for (Iterator e1 = autoFeat.getAssociatedList().iterator(); e1.hasNext();)
				{
					Object anObject = e1.next();
					if (aggregateFeat.isStacks() || !aggregateFeat.getAssociatedList().contains(anObject))
					{
						aggregateFeat.getAssociatedList().add(anObject);
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

	public boolean save(BufferedWriter output)
	{
		FileAccess fa = new FileAccess();

		saveCampaignLine(fa, output);
		saveVersionLine(fa, output);
		saveNameLine(fa, output);
		saveStatsLine(fa, output);
		saveClassesLine(fa, output);
		saveFeatsLine(fa, output);
		saveSkillsLine(fa, output);
		saveDeityLine(fa, output);
		saveRaceLine(fa, output);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			saveClassSpellLine(e, fa, output);
		}
		saveLanguagesLine(fa, output);
		saveWeaponProfsLine(fa, output);
		saveUnusedPointsLine(fa, output);
		saveMiscLine(fa, output);
		saveEquipmentLine(fa, output);
		saveGoldBioDescriptionLine(fa, output);
//		saveForceLine(fa, output);
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			saveClassesSkillLine(e1, fa, output);
		}
		saveExperienceAndMiscListLine(fa, output);
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			saveClassSpecialtyAndSaveLine(e1, fa, output);
		}
		saveTemplateLine(fa, output);
		saveNotes(fa, output);
		setDirty(false);
		return true;
	}

	public boolean load(BufferedReader input)
	{
		FileAccess fa = new FileAccess();
		importing = true;
		try
		{
			lastFnCalled = "loadPcgVersion";
			PcgReadVersion = loadPcgVersion(input);

			lastFnCalled = "loadNameLine";
			loadNameLine(fa, input);

			lastFnCalled = "loadStatsLine";
			loadStatsLine(fa, input);

			lastFnCalled = "loadClassesLine";
			loadClassesLine(fa, input);

			lastFnCalled = "loadFeatsLine";
			loadFeatsLine(fa, input);

			lastFnCalled = "readLine";
			String SkillsLine = fa.readLine(input);

			//Note, the following order is neccessary, for historical reasons...
			String deityLine = fa.readLine(input);

			lastFnCalled = "loadRaceLine";
			loadRaceLine(fa, input);

			lastFnCalled = "handleSkillsLine";
			handleSkillsLine(SkillsLine);

			lastFnCalled = "handleDeityLine";
			handleDeityLine(deityLine);

			lastFnCalled = "loadClassSpellLine";
			int dx = 0;
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				if (++dx == ignoreDomainClassLine)
					fa.readLine(input);
				else
					loadClassSpellLine(e, fa, input);
			}

			if (++dx == ignoreDomainClassLine)
				fa.readLine(input);
			lastFnCalled = "loadLanguagesLine";
			loadLanguagesLine(fa, input);

			lastFnCalled = "readLine";
			String weaponProfLine = fa.readLine(input);

			lastFnCalled = "loadUnusedPointsLine";
			loadUnusedPointsLine(fa, input);

			lastFnCalled = "loadMiscLine";
			loadMiscLine(fa, input);

			lastFnCalled = "loadEquipmentLine";
			loadEquipmentLine(fa, input);

			lastFnCalled = "loadGoldBioDescriptionLine";
			loadGoldBioDescriptionLine(fa, input);

//			lastFnCalled = "loadForceLine";
//			loadForceLine(fa, input);

			lastFnCalled = "loadClassesSkillLine";
			dx = 0;
			for (Iterator e = classList.iterator(); e.hasNext(); e.next())
			{
				if (++dx == ignoreDomainClassLine)
					fa.readLine(input);
				loadClassesSkillLine(fa, input);
			}
			if (++dx == ignoreDomainClassLine)
				fa.readLine(input);

			lastFnCalled = "loadExperienceAndMiscLine";
			loadExperienceAndMiscLine(fa, input);

			lastFnCalled = "loadClassSpecialtyAndSaveLines";
			loadClassSpecialtyAndSaveLines(fa, input);

			lastFnCalled = "loadTemplateLine";
			loadTemplateLine(fa, input);

			//
			// This needs to be called after the templates are loaded
			//
			lastFnCalled = "handleWeaponProfLine";
			handleWeaponProfLine(weaponProfLine);

			lastFnCalled = "loadNotes";
			loadNotes(fa, input);

			//
			// Need to adjust for older versions of PCG files here
			//

			if (PcgReadVersion < 1)
			{
				int conMod = calcStatMod(Constants.CONSTITUTION);
				pcgAdjustHpRolls(-conMod);
			}

			//
			// Hit point sanity check
			//
			boolean bFixMade = false;
			Race aRace = this.getRace();
			PCClass aClass;
			int iSides;
			int iRoll;
			int oldHp = this.hitPoints();
			if (aRace.hitDice() != 0)
			{
				iSides = aRace.getHitDiceSize();
				for (int i = 0; i < aRace.hitDice(); i++)
				{
					iRoll = aRace.getHitPointList(i).intValue();
					if (iRoll > iSides)
					{
						aRace.setHitPoint(i, new Integer(iSides));
						bFixMade = true;
					}
					if (iRoll < 1)
					{
						aRace.setHitPoint(i, new Integer(1));
						bFixMade = true;
					}
				}
			}


			for (Iterator e = featList.iterator(); e.hasNext();)
			{
				Feat aFeat = (Feat)e.next();
				if (aFeat.getChoiceString().startsWith("SALIST|"))
				{
					ArrayList aAvailable = new ArrayList();
					ArrayList aBonus = new ArrayList();
					aFeat.buildSALIST(aFeat.getChoiceString(), aAvailable, aBonus);
					for (int i = 0; i < aFeat.associatedList.size(); i++)
					{
						String aString = (String)aFeat.associatedList.get(i);
						final String prefix = aString + "|";
						boolean bLoop = true;
						for (; ;)
						{
							int x;
							for (x = 0; x < aBonus.size(); x++)
							{
								final String bString = (String)aBonus.get(x);
								if (bString.startsWith(prefix))
								{
									aFeat.addBonusList(bString.substring(bString.indexOf('|') + 1));
									break;
								}
							}
							if ((x < aBonus.size()) || !bLoop)
								break;

							bLoop = false;		// Avoid infinite loops at all costs!

							//
							// Do direct replacement if only 1 choice
							//
							if (aBonus.size() == 1)
							{
								aString = (String)aBonus.get(0);
								aString = aString.substring(0, aString.indexOf('|'));
							}
							else
							{
								for (; ;)
								{
									Object selectedValue = JOptionPane.showInputDialog(null,
										aFeat.getName() + " has been modified and PCGen is unable to determine your previous selection(s).\n\n" +
										"This box will pop up once for each time you have taken the feat.",
										Constants.s_APPNAME,
										JOptionPane.INFORMATION_MESSAGE,
										null,
										aAvailable.toArray(),
										aAvailable.get(0));
									if (selectedValue != null)
									{
										aString = (String)selectedValue;
										break;
									}
									GuiFacade.showMessageDialog(null, "You MUST make a selection", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								}
							}
							aFeat.associatedList.set(i, aString);
						}
					}
				}
				else if (aFeat.getChoiceString().startsWith("NONCLASSSKILLLIST|"))
				{
					// This is intended to address the problem of the Cosmopolitan feat not re-applying
					// classskill stauts to selected skills when the PC is reloaded
					// If this results in odd behavior for the only other feat I've seen that uses NONCLASSSKILLLIST
					// (Reincarnated from Ravenloft) I haven't seen it
					// Lone Jedi (Feb. 5, 2002)
					Skill aSkill = null;
					for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
					{
						aSkill = (Skill)e2.next();
						for (Iterator e3 = aFeat.getAssociatedList().iterator(); e3.hasNext();)
						{
							final String skillString = (String)e3.next();
							if (aSkill.getRootName().equals(skillString))
								aFeat.getCSkillList().add(aSkill.getName());
						}
					}
				}
			}

			if (this.getClassList() != null)
			{
				for (Iterator e = this.getClassList().iterator(); e.hasNext();)
				{
					aClass = (PCClass)e.next();
					//
					// Ignore if no levels
					//
					if (aClass.getLevel().intValue() < 1)
					{
						continue;
					}

					//
					// Walk through the levels for this class
					//
					iSides = aClass.getHitDie();
					for (int i = 0; i < aClass.getLevel().intValue(); i++)
					{
						iRoll = aClass.getHitPointList(i).intValue();
						if (iRoll > iSides)
						{
							aClass.setHitPoint(i, new Integer(iSides));
							bFixMade = true;
						}
						if (iRoll < 1)
						{
							aClass.setHitPoint(i, new Integer(1));
							bFixMade = true;
						}
					}
				}
			}
			if (bFixMade)
			{
				GuiFacade.showMessageDialog(null, "Fixed illegal value in hit points. Current character hit points: " + this.hitPoints() + " not " + oldHp, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}

		}
		catch (Exception es)
		{
			es.printStackTrace();
			GuiFacade.showMessageDialog(null, "Problem with line:" + lastLineParsed + "\r\nin: " + lastFnCalled + "\nMessage:" + es.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		// sometimes another class, feat, item, whatever can affect what spells or whatever
		// would have been available for a class, so this simply lets the level advancement routine
		// take into account all the details known about a character now that the import is
		// completed. The level isn't affected.  merton_monk@yahoo.com 2/15/2002
		for (Iterator ci = classList.iterator(); ci.hasNext();)
		{
			final PCClass aClass = (PCClass)ci.next();
			aClass.setLevel(aClass.getLevel());
		}
		importing = false;
		return true;
	}

	/** loadPcgVersion() should return 220 if string is 2.2.0 - this assumes a decimal release (no characters). */
	private int loadPcgVersion(BufferedReader br)
	{
		int iVersion = -1;
		try
		{
			br.mark(1024);  //set a mark so we can reset in the event of an old .pcg file
			String lastPcgLineParsed = br.readLine();
			StringTokenizer aTok = new StringTokenizer(lastPcgLineParsed, ":", false);
			String sVersion = aTok.nextToken();
			//if the pcg file starts with VERSION data then lets process it
			if (sVersion.equals("VERSION"))
			{
				sVersion = aTok.nextToken();
				aTok = new StringTokenizer(sVersion, ".", false);
				iVersion = 0;
				while (aTok.hasMoreTokens())
					iVersion = iVersion * 10 + Integer.parseInt(aTok.nextToken());
			}
			else
			{
				//this is an old .pcg file (no campaign data) so just reset the input stream
				br.reset();
			}
		}
		catch (IOException e)
		{
			GuiFacade.showMessageDialog(null, "Could not load campaign data from character file.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		Globals.debugPrint("PCG Version: " + iVersion);

		return iVersion;
	}


	public boolean print(File aFile, BufferedWriter output)
	{
		FileAccess.setCurrentOutputFilter(aFile.getName());
		FileInputStream aStream = null;
		populateSkills(Globals.getIncludeSkills());
		try
		{
			aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			byte[] inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			String aString = new String(inputLine);
			StringTokenizer aTok = new StringTokenizer(aString, "\r\n", true);
			FileAccess fa = new FileAccess();
			skillList = (ArrayList)Globals.sortPObjectList(skillList);
			featList = (ArrayList)Globals.sortPObjectList(featList);
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				PCClass aClass = (PCClass)e.next();
				aClass.setSpellList((ArrayList)Globals.sortPObjectList(aClass.spellList()));
			}
			determinePrimaryOffWeapon();
			modFromArmorOnWeaponRolls();

			FORNode root = parseFORs(aTok);
			loopVariables.put(null, "0");
			existsOnly = false;
			noMoreItems = false;
			loopFOR(root, 0, 0, 1, output, fa);
			loopVariables.clear();
		}
		catch (Exception exc)
		{
			System.err.println("Error in PlayerCharacter::print");
			exc.printStackTrace();
		}
		finally
		{
			if (aStream != null)
			{
				try
				{
					aStream.close();
				}
				catch (IOException ioe)
				{
					//Should this be ignored?
				}
			}
		}
		csheetTag2 = "\\";
		return true;
	}

	private FORNode parseFORs(StringTokenizer tokens)
	{
		FORNode root = new FORNode(null, "0", "0", "1", true);
		String line;
		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				if (newFor.countTokens() > 1)
				{
					newFor.nextToken();
					if (newFor.nextToken().startsWith("%"))
						root.addChild(parseFORs(line, tokens));
					else
						root.addChild(line);
				}
				else
				{
					root.addChild(line);
				}
			}
			else
			{
				if (!line.equals("\r"))
					root.addChild(line);
			}
		}
		return root;
	}

	private FORNode parseFORs(String forLine, StringTokenizer tokens)
	{
		StringTokenizer forVars = new StringTokenizer(forLine, ",");
		forVars.nextToken();
		String var = forVars.nextToken();
		String min = forVars.nextToken();
		String max = forVars.nextToken();
		String step = forVars.nextToken();
		String eTest = forVars.nextToken();
		boolean exists = false;
		if ((eTest.charAt(0) == '1') || (eTest.charAt(0) == '2'))
			exists = true;
		FORNode node = new FORNode(var, min, max, step, exists);
		String line;
		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();
				if (newFor.nextToken().startsWith("%"))
					node.addChild(parseFORs(line, tokens));
				else
					node.addChild(line);
			}
			else if (line.startsWith("|IIF(") && line.lastIndexOf(",") == -1)
			{
				String expr = line.substring(5, line.lastIndexOf(")"));
				node.addChild(parseIIFs(expr, tokens));
			}
			else if (line.startsWith("|ENDFOR|"))
				return node;
			else
				node.addChild(line);

		}
		return node;
	}

	private IIFNode parseIIFs(String expr, StringTokenizer tokens)
	{
		IIFNode node = new IIFNode(expr);
		String line;
		boolean childrenType = true;
		while (tokens.hasMoreTokens())
		{
			line = tokens.nextToken();
			if (line.startsWith("|FOR"))
			{
				StringTokenizer newFor = new StringTokenizer(line, ",");
				newFor.nextToken();
				if (newFor.nextToken().startsWith("%"))
				{
					if (childrenType)
						node.addTrueChild(parseFORs(line, tokens));
					else
						node.addFalseChild(parseFORs(line, tokens));
				}
				else
				{
					if (childrenType)
						node.addTrueChild(line);
					else
						node.addFalseChild(line);
				}
			}
			else if (line.startsWith("|IIF(") && line.lastIndexOf(",") == -1)
			{
				String newExpr = line.substring(5, line.lastIndexOf(")"));
				if (childrenType)
					node.addTrueChild(parseIIFs(newExpr, tokens));
				else
					node.addFalseChild(parseIIFs(newExpr, tokens));
			}
			else if (line.startsWith("|ELSE|"))
				childrenType = false;
			else if (line.startsWith("|ENDIF|"))
				return node;
			else
			{
				if (childrenType)
					node.addTrueChild(line);
				else
					node.addFalseChild(line);
			}
		}
		return node;
	}

	private void loopFOR(FORNode node, int min, int max, int step, BufferedWriter output, FileAccess fa)
	{
		for (int x = min; x <= max; x += step)
		{
			loopVariables.put(node.var(), new Integer(x));
			for (int y = 0; y < node.children().size(); y++)
			{
				if (node.children().get(y) instanceof FORNode)
				{
					FORNode nextFor = (FORNode)node.children().get(y);
					loopVariables.put(nextFor.var(), new Integer(0));
					existsOnly = nextFor.exists();
					String minString = nextFor.min();
					String maxString = nextFor.max();
					String stepString = nextFor.step();
					String fString;
					String rString;
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
							continue;
						fString = anObject.toString();
						rString = loopVariables.get(fString).toString();
						minString = Utility.replaceString(minString, fString, rString);
						maxString = Utility.replaceString(maxString, fString, rString);
						stepString = Utility.replaceString(stepString, fString, rString);
					}
					loopFOR(nextFor, getVariableValue(minString, "").intValue(), getVariableValue(maxString, "").intValue(), getVariableValue(stepString, "").intValue(), output, fa);
					existsOnly = node.exists();
					loopVariables.remove(nextFor.var());
				}
				else if (node.children().get(y) instanceof IIFNode)
				{
					evaluateIIF((IIFNode)node.children().get(y), output, fa);
				}
				else
				{
					String lineString = (String)node.children().get(y);
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
							continue;
						String fString = anObject.toString();
						String rString = loopVariables.get(fString).toString();
						lineString = Utility.replaceString(lineString, fString, rString);
					}

					noMoreItems = false;
					replaceLine(lineString, output, fa);

					// break out of loop if no more items
					if (existsOnly && noMoreItems)
						x = max + 1;
				}
			}
		}
	}

	private void evaluateIIF(IIFNode node, BufferedWriter output, FileAccess fa)
	{
		if (evaluateExpression(node.expr()))
		{
			for (int y = 0; y < node.trueChildren().size(); y++)
			{
				if (node.trueChildren().get(y) instanceof FORNode)
				{
					FORNode nextFor = (FORNode)node.trueChildren().get(y);
					loopVariables.put(nextFor.var(), new Integer(0));
					existsOnly = nextFor.exists();
					String minString = nextFor.min();
					String maxString = nextFor.max();
					String stepString = nextFor.step();
					String fString;
					String rString;
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
							continue;
						fString = anObject.toString();
						rString = loopVariables.get(fString).toString();
						minString = Utility.replaceString(minString, fString, rString);
						maxString = Utility.replaceString(maxString, fString, rString);
						stepString = Utility.replaceString(stepString, fString, rString);
					}
					loopFOR(nextFor, getVariableValue(minString, "").intValue(), getVariableValue(maxString, "").intValue(), getVariableValue(stepString, "").intValue(), output, fa);
					existsOnly = nextFor.exists();
					loopVariables.remove(nextFor.var());
				}
				else if (node.trueChildren().get(y) instanceof IIFNode)
				{
					evaluateIIF((IIFNode)node.trueChildren().get(y), output, fa);
				}
				else
				{
					String lineString = (String)node.trueChildren().get(y);
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
							continue;
						String fString = anObject.toString();
						String rString = loopVariables.get(fString).toString();
						lineString = Utility.replaceString(lineString, fString, rString);
					}
					replaceLine(lineString, output, fa);
				}
			}
		}
		else
		{
			for (int y = 0; y < node.falseChildren().size(); y++)
			{
				if (node.falseChildren().get(y) instanceof FORNode)
				{
					FORNode nextFor = (FORNode)node.falseChildren().get(y);
					loopVariables.put(nextFor.var(), new Integer(0));
					existsOnly = nextFor.exists();
					String minString = nextFor.min();
					String maxString = nextFor.max();
					String stepString = nextFor.step();
					String fString;
					String rString;
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
							continue;
						fString = anObject.toString();
						rString = loopVariables.get(fString).toString();
						minString = Utility.replaceString(minString, fString, rString);
						maxString = Utility.replaceString(maxString, fString, rString);
						stepString = Utility.replaceString(stepString, fString, rString);
					}
					loopFOR(nextFor, getVariableValue(minString, "").intValue(), getVariableValue(maxString, "").intValue(), getVariableValue(stepString, "").intValue(), output, fa);
					existsOnly = nextFor.exists();
					loopVariables.remove(nextFor.var());
				}
				else if (node.falseChildren().get(y) instanceof IIFNode)
				{
					evaluateIIF((IIFNode)node.falseChildren().get(y), output, fa);
				}
				else
				{
					String lineString = (String)node.falseChildren().get(y);
					for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
					{
						Object anObject = ivar.next();
						if (anObject == null)
							continue;
						String fString = anObject.toString();
						String rString = loopVariables.get(fString).toString();
						lineString = Utility.replaceString(lineString, fString, rString);
					}
					replaceLine(lineString, output, fa);
				}
			}
		}
	}

	public boolean evaluateExpression(String expr)
	{
		for (Iterator ivar = loopVariables.keySet().iterator(); ivar.hasNext();)
		{
			Object anObject = ivar.next();
			if (anObject == null)
				continue;
			String fString = anObject.toString();
			String rString = loopVariables.get(fString).toString();
			expr = Utility.replaceString(expr, fString, rString);
		}

		if (expr.startsWith("HASFEAT:"))
		{
			expr = expr.substring(8).trim();
			if (getFeatNamed(expr) == null)
				return false;
			else
				return true;
		}
		if (expr.startsWith("HASVAR:"))
		{
			return hasVariable(expr.substring(7).trim());
		}
		if (expr.startsWith("HASEQUIP:"))
		{
			expr = expr.substring(9).trim();
			if (getEquipmentNamed(expr) == null)
				return false;
			else
				return true;
		}
		if (expr.startsWith("EVEN:"))
		{
			int i = 0;
			try
			{
				i = Integer.parseInt(expr.substring(5).trim());
			}
			catch (Exception exc)
			{
				System.out.println("EVEN:" + i);
				return true;
			}
			if (i % 2 == 0)
				return true;
			else
				return false;
		}
		if (expr.endsWith("UNTRAINED"))
		{
			StringTokenizer aTok = new StringTokenizer(expr, ".");
			String fString = aTok.nextToken();
			Skill aSkill = null;
			if (fString.length() > 5)
			{
				int i = Integer.parseInt(fString.substring(5));
				if (i <= getSkillList().size() - 1)
					aSkill = (Skill)getSkillList().get(i);
			}
			if (aSkill == null)
				return false;
			else if (aSkill.untrained().startsWith("Y"))
				return true;
			return false;
		}
		return false;
	}

	private void generateContainerList(ArrayList anArray)
	{
		Collection tempList = getEquipmentList().values();
		int equipmentLocation = anArray.size() - 1;
		Equipment anEquip = (Equipment)anArray.get(equipmentLocation);
		Equipment myEquip = null;
		for (Iterator locIter = tempList.iterator(); locIter.hasNext();)
		{
			myEquip = (Equipment)locIter.next();
			if (anEquip.getD_containedEquipment().contains(myEquip))
			{
				anArray.add(myEquip);
				if (myEquip.getChildCount() > 0)
					generateContainerList(anArray);
			}
		}
	}

	public void replaceLine(String aLine, BufferedWriter output, FileAccess fa)
	{
		boolean inPipe = false;
		boolean flag = true;
		String tokString = "";

		if (!inPipe && aLine.lastIndexOf("|") == -1)
		{
			if (!aLine.equals("\n"))
				replaceToken(aLine, output);
			else
				fa.newLine(output);
		}
		else if ((inPipe && aLine.lastIndexOf("|") == -1) || (!inPipe && aLine.lastIndexOf("|") == 0))
		{
			tokString = tokString + aLine.substring(aLine.lastIndexOf("|") + 1);
			inPipe = true;
		}
		else
		{
			if (inPipe == false && aLine.charAt(0) == '|')
				inPipe = true;
			StringTokenizer bTok = new StringTokenizer(aLine, "|", false);
			flag = bTok.countTokens() == 1;
			while (bTok.hasMoreTokens())
			{
				String bString = bTok.nextToken();
				if (!inPipe)
					replaceToken(bString, output);
				else
				{
					if (bTok.hasMoreTokens() || flag || (inPipe && !bTok.hasMoreTokens() && aLine.charAt(aLine.length() - 1) == '|'))
					{
						replaceToken(tokString + bString, output);
						tokString = "";
					}
					else
						tokString = tokString + bString;
				}
				if (bTok.hasMoreTokens() || flag)
					inPipe = !inPipe;
			}

			if (inPipe && aLine.charAt(aLine.length() - 1) == '|')
				inPipe = false;
			if (!inPipe)
				fa.newLine(output);
		}
	}

	public int replaceToken(String aString, BufferedWriter output)
	{
		try
		{
			FileAccess fa = new FileAccess();
			int len = 1;
			if (!aString.startsWith("%") && !canWrite)
				return 0;
			if (aString.equals("%"))
			{
				inLabel = false;
				canWrite = true;
				return 0;
			}
			if (aString.startsWith("%") && aString.length() > 1 &&
				aString.lastIndexOf('<') == -1 && aString.lastIndexOf('>') == -1)
			{
				boolean found = false;
				canWrite = true;
				if (aString.substring(1).startsWith("GAMEMODE:"))
				{
					if (aString.substring(10).endsWith(Globals.getGameMode()))
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("REGION"))
				{
					if (getRegion().equals(Constants.s_NONE))
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("PROHIBITEDLIST"))
				{
					for (Iterator iter = classList.iterator(); iter.hasNext();)
					{
						PCClass aClass = (PCClass)iter.next();
						if (aClass.getLevel().intValue() > 0)
						{
							if (!aClass.getProhibitedString().equals(Constants.s_NONE))
								return 0;
						}
					}
					canWrite = false;
					return 0;
				}

				if (aString.substring(1).equals("CATCHPHRASE"))
				{
					if (getCatchPhrase().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getCatchPhrase()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("LOCATION"))
				{
					if (getLocation().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getLocation()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("RESIDENCE"))
				{
					if (getResidence().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getResidence()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("PHOBIAS"))
				{
					if (getPhobias().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getPhobias()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("INTERESTS"))
				{
					if (getInterests().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getInterests()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("SPEECHTENDENCY"))
				{
					if (getSpeechTendency().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getSpeechTendency()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("PERSONALITY1"))
				{
					if (getTrait1().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getTrait1()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("PERSONALITY2"))
				{
					if (getTrait1().equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getTrait2()).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("COMPANIONS"))
				{
					if (getMiscList().get(1).equals(Constants.s_NONE))
						canWrite = false;
					else if (((String)getMiscList().get(1)).trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("DESC"))
				{
					if (description.equals(Constants.s_NONE))
						canWrite = false;
					else if (description.trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("BIO"))
				{
					if (bio.equals(Constants.s_NONE))
						canWrite = false;
					else if (bio.trim().length() == 0)
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).equals("SUBREGION"))
				{
					if (getSubRegion().equals(Constants.s_NONE))
						canWrite = false;
					return 0;
				}

				if (aString.substring(1).startsWith("ARMOR.ITEM"))
				{
					ArrayList aArrayList = new ArrayList();
					for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
					{
						Equipment eq = (Equipment)mapIter.next();

						for (Iterator it = eq.getBonusList().iterator(); it.hasNext();)
						{
							if (((String)it.next()).indexOf("|AC|") != -1)
							{
								if (!eq.isType("ARMOR") && !eq.isType("SHIELD"))
								{
									aArrayList.add(eq);
									// break;
								}
							}
						}
					}
					if (Integer.parseInt(aString.substring(aString.length() - 1)) >= aArrayList.size())
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).startsWith("ARMOR.SHIELD"))
				{
					ArrayList aArrayList = getEquipmentOfType("SHIELD", 3);
					if (Integer.parseInt(aString.substring(aString.length() - 1)) >= aArrayList.size())
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).startsWith("ARMOR"))
				{
					ArrayList aArrayList = getEquipmentOfType("ARMOR", 3);
					if (Integer.parseInt(aString.substring(aString.length() - 1)) >= aArrayList.size())
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).startsWith("WEAPON"))
				{
					ArrayList aArrayList = getExpandedWeapons();
					if (Integer.parseInt(aString.substring(aString.length() - 1)) >= aArrayList.size())
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).startsWith("DOMAIN"))
				{
					canWrite = (Integer.parseInt(aString.substring(7)) <= characterDomainList.size());
					return 0;
				}
				if (aString.substring(1).startsWith("SPELLLISTBOOK"))
				{
					return replaceTokenSpellListBook(aString);
				}
				if (aString.substring(1).startsWith("VAR."))
				{
					StringTokenizer aTok = new StringTokenizer(aString.substring(5), ".", false);
					String varName = aTok.nextToken();
					String bString = "EQ";
					if (aTok.hasMoreTokens())
						bString = aTok.nextToken();
					String value = "0";
					if (aTok.hasMoreTokens())
						value = aTok.nextToken();
					Float varval = getVariable(varName, true, true, "", "");
					Float valval = getVariableValue(value, "");
					if (bString.equals("GTEQ"))
						canWrite = varval.doubleValue() >= valval.doubleValue();
					else if (bString.equals("GT"))
						canWrite = varval.doubleValue() > valval.doubleValue();
					else if (bString.equals("LTEQ"))
						canWrite = varval.doubleValue() <= valval.doubleValue();
					else if (bString.equals("LT"))
						canWrite = varval.doubleValue() < valval.doubleValue();
					else if (bString.equals("NEQ"))
						canWrite = varval.doubleValue() != valval.doubleValue();
					else
						canWrite = varval.doubleValue() == valval.doubleValue();
					return 0;
				}
				StringTokenizer aTok = new StringTokenizer(aString.substring(1), ",", false);
				while (aTok.hasMoreTokens())
				{
					String cString = aTok.nextToken();
					StringTokenizer bTok = new StringTokenizer(cString, "=", false);
					String bString = bTok.nextToken();
					int i = 0;
					if (bTok.hasMoreTokens())
					{
						i = Integer.parseInt(bTok.nextToken());
					}

					PCClass aClass = null;
					PCClass bClass = null;
					for (Iterator e = Globals.getClassList().iterator(); e.hasNext();)
					{
						bClass = (PCClass)e.next();
						if (bClass.getName().equals(bString))
							break;
						bClass = null;
					}
					found = bClass != null;
					aClass = getClassNamed(bString);
					if (bClass != null && aClass != null)
						canWrite = (aClass.getLevel().intValue() >= i);
					else if (bClass != null && aClass == null)
						canWrite = false;
					else if (bString.startsWith("SPELLLISTCLASS"))
					{
						found = true;
						aClass = getSpellClassAtIndex(Integer.parseInt(bString.substring(14)));
						canWrite = (aClass != null);
					}
				}
				if (found)
				{
					inLabel = true;
					return 0;
				}
			}
			if (aString.startsWith("FOR.") || aString.startsWith("DFOR."))
			{
				existsOnly = false;
				noMoreItems = false;
				checkBefore = false;
				replaceTokenForDfor(aString, fa, output);
				existsOnly = false;
				noMoreItems = false;
				return 0;
			}
			if (aString.startsWith("CSHEETTAG2."))
			{
				csheetTag2 = aString.substring(11, 12);
				return 0;
			}
			if (aString.startsWith("STAT"))
			{
				len = replaceTokenStat(aString, fa, output);
			}
			else if (aString.equals("EXPORT.DATE"))
			{
				fa.write(output, java.text.DateFormat.getDateInstance().format(new Date()));
			}
			else if (aString.equals("EXPORT.TIME"))
			{
				fa.write(output, java.text.DateFormat.getTimeInstance().format(new Date()));
			}
			else if (aString.equals("EXPORT.VERSION"))
			{
				try
				{
					final ResourceBundle d_properties = ResourceBundle.getBundle("pcgen/gui/PCGenProp");
					fa.write(output, d_properties.getString("VersionNumber"));
				}
				catch (java.util.MissingResourceException mre)
				{
				}
			}
			else if (aString.startsWith("PAPERINFO."))
			{
				String oString = aString;
				aString = aString.substring(10);
				int infoType = -1;
				if (aString.startsWith("NAME"))
				{
					infoType = Constants.PAPERINFO_NAME;
				}
				else if (aString.startsWith("HEIGHT"))
				{
					infoType = Constants.PAPERINFO_HEIGHT;
				}
				else if (aString.startsWith("WIDTH"))
				{
					infoType = Constants.PAPERINFO_WIDTH;
				}
				else if (aString.startsWith("MARGIN"))
				{
					aString = aString.substring(6);
					if (aString.startsWith("TOP"))
					{
						infoType = Constants.PAPERINFO_TOPMARGIN;
					}
					else if (aString.startsWith("BOTTOM"))
					{
						infoType = Constants.PAPERINFO_BOTTOMMARGIN;
					}
					else if (aString.startsWith("LEFT"))
					{
						infoType = Constants.PAPERINFO_LEFTMARGIN;
					}
					else if (aString.startsWith("RIGHT"))
					{
						infoType = Constants.PAPERINFO_RIGHTMARGIN;
					}
				}
				if (infoType >= 0)
				{
					int offs = aString.indexOf('=');
					String info = Globals.getPaperInfo(infoType);
					if (info == null)
					{
						if (offs >= 0)
						{
							oString = aString.substring(offs + 1);
						}
					}
					else
					{
						oString = info;
					}
				}
				fa.write(output, oString);
			}
			else if (aString.startsWith("BIO"))
			{
				int i;
				int k = aString.lastIndexOf(',');
				if (k > -1)
					aString = aString.substring(k + 1);
				else
					aString = "";

				ArrayList stringList = getLineForBio();
				for (i = 0; i < stringList.size(); i++)
				{
					fa.encodeWrite(output, (String)stringList.get(i));
					if (i < stringList.size() - 1)
						fa.write(output, aString);

				}
			}
			else if (aString.startsWith("DESC"))
			{
				int i;
				int k = aString.lastIndexOf(',');
				if (k > -1)
					aString = aString.substring(k + 1);
				else
					aString = "";

				ArrayList stringList = getLineForDesc();
				for (i = 0; i < stringList.size(); i++)
				{
					fa.encodeWrite(output, (String)stringList.get(i));
					if (i < stringList.size() - 1)
						fa.write(output, aString);

				}

			}
			else if (aString.equals("NAME"))
				fa.encodeWrite(output, getName());
			else if (aString.equals("RACE"))
			{
				String tempRaceName = getRace().getRaceOutputName();
				if (tempRaceName.equals(Constants.s_NONE)) tempRaceName = getRace().getName();

				if (getSubRace().equals(Constants.s_NONE))
				{
					fa.encodeWrite(output, tempRaceName);
				}
				else
				{
					fa.encodeWrite(output, tempRaceName + " (" + getSubRace() + ")");
				}
			}
			else if (aString.equals("AGE"))
				fa.write(output, new Integer(age).toString());
			else if (aString.equals("HEIGHT"))
			{
				if (Globals.useMetric())
				{
					fa.encodeWrite(output, new Integer(height).toString() + " " + Globals.getHeightDisplay());
				}
				else
				{
					fa.encodeWrite(output, new Integer(height / 12).toString() + "' " + new Integer(height % 12).toString() + "\"");
				}
			}
			else if (aString.equals("HEIGHT.FOOTPART"))
				fa.write(output, new Integer(height / 12).toString());
			else if (aString.equals("HEIGHT.INCHPART"))
				fa.write(output, new Integer(height % 12).toString());
			else if (aString.equals("WEIGHT"))
			{
				fa.encodeWrite(output, new Integer(weight).toString() + " " + Globals.getWeightDisplay());
			}
			else if (aString.equals("WEIGHT.NOUNIT"))
				fa.write(output, new Integer(weight).toString());
			else if (aString.equals("COLOR.EYE"))
				fa.encodeWrite(output, getEyeColor());
			else if (aString.equals("COLOR.HAIR"))
				fa.encodeWrite(output, getHairColor());
			else if (aString.equals("COLOR.SKIN"))
				fa.encodeWrite(output, getSkinColor());
			else if (aString.equals("LENGTH.HAIR"))
				fa.encodeWrite(output, getHairStyle());
			else if (aString.equals("PERSONALITY1"))
				fa.encodeWrite(output, getTrait1());
			else if (aString.equals("PERSONALITY2"))
				fa.encodeWrite(output, getTrait2());
			else if (aString.equals("SPEECHTENDENCY"))
				fa.encodeWrite(output, getSpeechTendency());
			else if (aString.equals("CATCHPHRASE"))
				fa.encodeWrite(output, getCatchPhrase());
			else if (aString.equals("RESIDENCE"))
				fa.encodeWrite(output, getResidence());
			else if (aString.equals("LOCATION"))
				fa.encodeWrite(output, getLocation());
			else if (aString.equals("REGION"))
				fa.encodeWrite(output, getRegion());
			else if (aString.equals("SUBREGION"))
				fa.encodeWrite(output, getSubRegion());
			else if (aString.equals("PHOBIAS"))
				fa.encodeWrite(output, getPhobias());
			else if (aString.equals("INTERESTS"))
				fa.encodeWrite(output, getInterests());
			else if (aString.equals("TOTALLEVELS"))
				fa.write(output, new Integer(totalLevels()).toString());
			else if (aString.equals("CR"))
				fa.write(output, "" + calcCR());
			else if (aString.equals("FACE"))
				fa.write(output, race.getFace());
			else if (aString.equals("REACH"))
				fa.write(output, String.valueOf(race.getReach()));
			else if (aString.equals("SR"))
				fa.write(output, "" + calcSR());
			else if (aString.equals("DR"))
				fa.write(output, "" + calcDR());
			else if (aString.equals("ALIGNMENT"))
			{
				replaceTokenAlignment(fa, output);
			}
			else if (aString.equals("ALIGNMENT.SHORT"))
			{
				replaceTokenAlignmentShort(fa, output);
			}
			else if (aString.startsWith("BONUSLIST."))
			{
				final StringTokenizer bTok = new StringTokenizer(aString.substring(10), ".", false);
				String bonusString = "";
				String subString = "";
				String typeSeparator = " ";
				String delim = ", ";
				if (bTok.hasMoreTokens())
					bonusString = bTok.nextToken();
				if (bTok.hasMoreTokens())
					subString = bTok.nextToken();
				if (bTok.hasMoreTokens())
					typeSeparator = bTok.nextToken();
				if (bTok.hasMoreTokens())
					delim = bTok.nextToken();
				final int typeLen = bonusString.length() + subString.length() + 2;
				if (subString.length() > 0 && bonusString.length() > 0)
				{
					final int total = getTotalBonusTo(bonusString, subString, true);
					if (typeSeparator.equals("TOTAL"))
					{
						fa.write(output, String.valueOf(total));
						return 1;
					}
					boolean needDelim = false;
					for (Iterator bi = bonusMap.keySet().iterator(); bi.hasNext();)
					{
						String aKey = bi.next().toString();
						if (aKey.startsWith(bonusString + "." + subString + "."))
						{
							if (needDelim)
								fa.write(output, delim);
							if (aKey.length() > typeLen)
							{
								fa.write(output, aKey.substring(typeLen));
							}
							else
								fa.write(output, "None");
							fa.write(output, typeSeparator);
							fa.write(output, (String)bonusMap.get(aKey));
							needDelim = true;
						}
					}
				}
			}
			else if (aString.equals("GENDER") || aString.equals("GENDER.SHORT"))
				fa.write(output, gender);
			else if (aString.equals("GENDER.LONG"))
				if (gender.equals("M"))
					fa.write(output, "Male");
				else if (gender.equals("F"))
					fa.write(output, "Female");
				else
					fa.write(output, gender);
			else if (aString.equals("HANDED"))
				fa.write(output, handed);
			else if (aString.equals("PROHIBITEDLIST"))
			{
				int i;
				int k = aString.lastIndexOf(',');
				if (k > -1)
					aString = aString.substring(k + 1);
				else
					aString = ", ";

				ArrayList stringList = new ArrayList();
				for (Iterator iter = classList.iterator(); iter.hasNext();)
				{
					PCClass aClass = (PCClass)iter.next();
					if (aClass.getLevel().intValue() > 0)
					{
						if (!aClass.getProhibitedString().equals(Constants.s_NONE))
							stringList.add(aClass.getProhibitedString());
					}
				}
				for (i = 0; i < stringList.size(); i++)
				{
					fa.write(output, (String)stringList.get(i));
					if (i < stringList.size() - 1)
						fa.write(output, aString);

				}
			}
			else if (aString.startsWith("TEMPLATE"))
			{

				boolean lastflag = false;

				if (aString.equals("TEMPLATELIST"))
					for (Iterator e = templateList.iterator(); e.hasNext();)
					{
						PCTemplate aTemplate = (PCTemplate)e.next();

						if (aTemplate.isVisible() == 1 || aTemplate.isVisible() == 2)
						{
							if (lastflag)
								fa.write(output, ", ");
							fa.encodeWrite(output, aTemplate.toString());
							lastflag = true;
						}
					}
				else
				{
					/* TEMPLATE%.subtag stuff handled in here*/
					int i = (int)aString.charAt(8) - 48;

					StringTokenizer aTok = new StringTokenizer(aString.substring(9), ".", false);
					String aLabel = "NAME";  /*default subtag is NAME*/
					if (aTok.hasMoreTokens())
						aLabel = aTok.nextToken();
					for (Iterator e = templateList.iterator(); e.hasNext();)
					{
						PCTemplate aTemplate = (PCTemplate)e.next();
						i--;

						if (i == 0 && (aTemplate.isVisible() == 1 || aTemplate.isVisible() == 2))
						// Invisible tags cannot be called normally but have special tags
						// for creating output.
						// --- arcady.
						{
							if (aLabel.equals("NAME"))
								fa.write(output, aTemplate.toString());
							else if (aLabel.equals("SA"))
							{
								fa.write(output, aTemplate.getSAs(totalLevels(), totalHitDice(), getSize()).toString());
							}
							else if (aLabel.equals("FEAT"))
							{
								fa.write(output, aTemplate.feats(totalLevels(), totalHitDice(), getSize()).toString());
							}
							else if (aLabel.equals("SR"))
							{
								fa.write(output, Integer.toString(aTemplate.getSR(totalLevels(), totalHitDice(), getSize())));
							}
							else if (aLabel.equals("CR"))
							{
								fa.write(output, Integer.toString(aTemplate.getCR(totalLevels(), totalHitDice(), getSize())));
							}
							else
							{
								for (int iMod = 0; iMod < Globals.s_ATTRIBSHORT.length; iMod++)
								{
									final String modName = Globals.s_ATTRIBSHORT[iMod] + "MOD";
									if (aLabel.equals(modName))
									{
										if (aTemplate.isNonAbility(iMod))
											fa.write(output, "*");
										else
											fa.write(output, Integer.toString(aTemplate.getStatMod(iMod)));
										break;
									}
								}
							}
							break;
							/* TODO: DR subtag ... */
						}
					}
				}
				/* TODO: find COUNT code and add COUNT(TEMPLATES) option */
			}
			else if (aString.startsWith("CLASS"))
			{
				int i = 0;
				int y = 0;
				int cmp = 0;
				if (aString.equals("CLASSLIST"))
					cmp = 1;
				else if (aString.lastIndexOf("ABB") > -1)
				{
					i = Integer.parseInt(aString.substring(8));
					cmp = 2;
				}
				else
					i = (int)aString.charAt(5) - 48;
				if (aString.endsWith("LEVEL"))
					cmp = 3;
				len = 0;
				int classSize = classList.size();
				if (classSize <= i && existsOnly)
				{
					noMoreItems = true;
					return 0;
				}
				for (Iterator e = classList.iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					if (cmp == 1 && y++ > 0)
						fa.write(output, " ");
					if (aClass.getLevel().intValue() > 0)
						i--;
					if (i == -1 || cmp == 1)
					{
						len = 1;
						if (cmp < 2)
						{
							if (aClass.getSubClassName().equals(Constants.s_NONE) || aClass.getSubClassName().equals(""))
								fa.encodeWrite(output, aClass.getName());
							else
								fa.encodeWrite(output, aClass.getSubClassName());
						}
						if (cmp == 1 || cmp == 3)
							fa.write(output, aClass.getLevel().toString());
						if (cmp == 2)
							fa.encodeWrite(output, aClass.getAbbrev());
						if (cmp != 1)
							break;
					}
				}
				System.out.println("");
			}
			else if (aString.equals("HITDICE"))
			{
				String del = "";
				if (race.hitDice() > 0)
				{
					fa.write(output, "(" + Integer.toString(race.hitDice()) + "d" + Integer.toString(race.getHitDiceSize()) + ")");
					del = "+";
				}

				PCClass aClass;
				String aaClassLevel;
				Integer aClassHitDie;
				String aaCLassHitDie;

				for (Iterator it = classList.iterator(); it.hasNext();)
				{

					aClass = (PCClass)it.next();
					aaClassLevel = aClass.getLevel().toString();
					aClassHitDie = new Integer(aClass.getHitDie());
					aaCLassHitDie = aClassHitDie.toString();

					fa.write(output, del + "(" + aaClassLevel + "d" + aaCLassHitDie + ")");
					del = "+";
				}

				//
				// Get CON bonus contribution to hitpoint total
				//
				int temp = calcStatMod(Constants.CONSTITUTION);
				temp *= (totalLevels() + race.hitDice());
				//
				// Add in feat bonus
				//
				temp += getTotalBonusTo("HP", "CURRENTMAX", true);
				if (temp != 0)
				{
					fa.write(output, Delta.toString(temp));
				}

			}
			else if (aString.equals("EXP.CURRENT"))
			{
				fa.write(output, getExperience().toString());
			}
			else if (aString.equals("EXP.NEXT"))
			{
				fa.write(output, new Integer(Globals.minExpForLevel(totalLevels() + 1 + levelAdjustment())).toString());
			}
			else if (aString.equals("EXP.FACTOR"))
			{
				Float aFloat = new Float(multiclassXpMultiplier().floatValue() * 100.0);
				Integer aInt = new Integer(aFloat.intValue());
				fa.encodeWrite(output, aInt.toString() + "%");
			}
			else if (aString.equals("EXP.PENALTY"))
			{
				Float aFloat = new Float(multiclassXpMultiplier().floatValue() * 100.0);
				Integer aInt = new Integer(100 - aFloat.intValue());
				fa.encodeWrite(output, aInt.toString() + "%");
			}
			else if (aString.equals("FAVOREDLIST"))
			{
				final int favoredSize = favoredClasses.size();

				if (favoredSize <= 0 && existsOnly)
				{
					noMoreItems = true;
					return 0;
				}
				boolean firstPass = true;

				for (Iterator e = favoredClasses.iterator(); e.hasNext();)
				{
					// separator only on second and beyond iterations
					if (!firstPass)
					{
						fa.write(output, ", ");
					}
					final String favoredString = (String)e.next();
					fa.write(output, favoredString);
					firstPass = false;
				}
			}
			else if (aString.startsWith("REFLEX"))
			{
				replaceTokenReflex(aString, fa, output);
			}
			else if (aString.startsWith("FORTITUDE"))
			{
				replaceTokenFortitude(aString, fa, output);
			}
			else if (aString.startsWith("WILL"))
			{
				replaceTokenWill(aString, fa, output);
			}
			else if (aString.equals("TOTALAC"))
			{
				fa.write(output, Integer.toString(totalAC()));
			}
			else if (aString.equals("TOUCHAC"))
			{
				fa.write(output, Integer.toString(touchAC()));
			}
			else if (aString.equals("FLATAC"))
			{
				fa.write(output, Integer.toString(flatFootedAC()));
			}
			else if (aString.equals("BASEAC"))
			{
				final int baseac = getRace().getStartingAC().intValue() + naturalArmorModForSize();
				writeToken(baseac, fa, output);
			}
			else if (aString.equals("ACMOD"))
			{
				final int acmod = acMod();
				writeToken(acmod, fa, output);
			}
			else if (aString.equals("ACABILITYMOD"))
			{
				final int acAbilityMod = acAbilityMod();
				final int itemBonus = getBonusDueToType("COMBAT", "AC", "Armor");
				writeToken(acAbilityMod - itemBonus, fa, output);
			}
			else if (aString.equals("ACSIZEMOD") || aString.equals("SIZEMOD"))
			{
				final int acSizeMod = acSizeMod();
				writeToken(acSizeMod, fa, output);
			}
			else if (aString.equals("SHIELD.AC"))
			{
				final int shieldAC = acCalculator.acModFromShield();
				writeToken(shieldAC, fa, output);
			}
			else if (aString.equals("ARMOR.AC"))
			{
				final int armorAC = acCalculator.acModFromArmor();
				writeToken(armorAC, fa, output);
			}
			else if (aString.equals("EQUIP.AC"))
			{
				final int equipAC = modToFromEquipment("AC");
				writeToken(equipAC, fa, output);
			}
			else if (aString.startsWith("AC."))
			{
				replaceTokenAC(aString, fa, output);
			}
			else if (aString.equals("MAXDEX") || aString.equals("ACCHECK") ||
				aString.equals("SPELLFAILURE"))
			{
				final int mod = modToFromEquipment(aString);
				if (mod > 0 && !aString.equals("SPELLFAILURE"))
					fa.write(output, "+");
				fa.write(output, Integer.toString(mod));
			}
			else if (aString.equals("INITIATIVEMOD"))
			{
				final int initiativeMod = initiativeMod();
				writeToken(initiativeMod, fa, output);
			}
			else if (aString.equals("INITIATIVEMISC"))
			{
				final int initiativeMisc = initiativeMod() - calcStatMod(Constants.DEXTERITY);
				writeToken(initiativeMisc, fa, output);
			}
			else if (aString.equals("INITIATIVEBONUS"))
			{
				final int initiativeBonus = initiativeMod() - calcStatMod(Constants.DEXTERITY);
				writeToken(initiativeBonus, fa, output);
			}
			else if (aString.startsWith("MOVEMENT"))
			{
				if ((race != null) && !race.equals(Globals.s_EMPTYRACE))
				{
					if (aString.length() > 9)
					{
						aString = aString.substring(9);
						StringTokenizer aTok = new StringTokenizer(aString, ".", false);
						String moveType = (String)aTok.nextToken();
						aString = "RATE";
						if (aTok.hasMoreTokens())
						{
							aString = ((String)aTok.nextToken()).toUpperCase();
						}

						for (int x = 0; x < movements.length; x++)
						{
							if (getMovementType(x).toUpperCase().equals(moveType.toUpperCase()))
							{
								// Output choices for Move types contained in here, only RATE currently Defined
								if (aString.equals("RATE"))
								{
									fa.encodeWrite(output, "" + movement(x) + Globals.getAbbrMovementDisplay());
								}
							}
						}
					}
					else
					{
						fa.encodeWrite(output, getMovementType(0) + " " + movement(0) + Globals.getAbbrMovementDisplay());
						for (int x = 1; x < movements.length; x++)
						{
							fa.encodeWrite(output, ", " + getMovementType(x) + " " + movement(x) + Globals.getAbbrMovementDisplay());
						}
					}
				}
			}
			else if (aString.startsWith("MOVE")) /* format : MOVE% prints out movename/move pair. MOVE%.NAME and MOVE%.RATE produce the appropriate parts.*/
			{
				if ((race != null) && !race.equals(Globals.s_EMPTYRACE))
				{
					StringTokenizer aTok = new StringTokenizer(aString, ".");
					String fString = aTok.nextToken();
					int moveIndex = Integer.parseInt(fString.substring(4));

					if (aTok.hasMoreTokens())
					{
						fString = aTok.nextToken();
						if (fString.equals("NAME"))
						{
							fa.write(output, getMovementType(moveIndex));
						}
						else if (fString.equals("RATE"))
						{
							fa.encodeWrite(output, movement(moveIndex) + Globals.getAbbrMovementDisplay());
						}
					}
					else
					{
						fa.encodeWrite(output, getMovementType(moveIndex) + " " + movement(moveIndex) + Globals.getAbbrMovementDisplay());
					}
				}
			}
			else if (aString.equals("SIZE"))
			{
				fa.write(output, getSize());
			}
			else if (aString.equals("SIZELONG"))
			{
				fa.write(output, Constants.s_SIZELONG[sizeInt()]);
			}
			else if (aString.equals("TYPE"))
			{
				fa.write(output, getCritterType());
			}
			else if (aString.startsWith("FEATALLLIST"))
			{
				printFeatList(aString.substring(11), aggregateFeatList(), fa, output);
			}
			else if (aString.startsWith("FEATAUTOLIST"))
			{
				printFeatList(aString.substring(12), featAutoList(), fa, output);
			}
			else if (aString.startsWith("FEATLIST"))
			{
				printFeatList(aString.substring(8), featList, fa, output);
			}
			else if (aString.startsWith("VFEATLIST"))
			{
				printFeatList(aString.substring(9), vFeatList(), fa, output);
			}
			else if (aString.startsWith("FEATALL"))
			{
				printFeat(7, aString, aggregateFeatList(), fa, output);
			}
			else if (aString.startsWith("FEATAUTO"))
			{
				printFeat(8, aString, featAutoList(), fa, output);
			}
			else if (aString.startsWith("FEAT"))
			{
				printFeat(4, aString, featList, fa, output);
			}
			else if (aString.startsWith("VFEAT"))
			{
				printFeat(5, aString, vFeatList(), fa, output);
			}
			else if (aString.equals("SKILLLISTMODS"))
			{
				replaceTokenSkillListMods(fa, output);
			}
			else if (aString.startsWith("SKILL"))
			{
				len = replaceTokenSkill(aString, len, fa, output);
			}
			else if (aString.equals("DEITY"))
			{
				if (getDeity() != null)
					fa.write(output, getDeity().getName());
				else
					len = 0;
			}
			else if (aString.startsWith("DOMAIN"))
			{
				boolean flag = aString.endsWith("POWER");
				Domain aDomain = null;
				if (characterDomainList.size() > (int)aString.charAt(6) - 49)
					aDomain = ((CharacterDomain)characterDomainList.get((int)aString.charAt(6) - 49)).getDomain();
				if (aDomain == null)
				{
					if (existsOnly)
						noMoreItems = true;
					return 0;
				}
				else if (flag)
					fa.write(output, aDomain.getGrantedPower());
				else
					fa.write(output, aDomain.getName());
			}
			else if (aString.startsWith("SPECIALLIST"))
			{
				len = replaceTokenSpecialList(aString, fa, output);
			}
			else if (aString.startsWith("SPECIALABILITY"))
			{
				len = replaceTokenSpecialAbility(aString, fa, output);
			}
			else if (aString.equals("ATTACK.MELEE"))
			{
				fa.write(output, getAttackString(Constants.ATTACKSTRING_MELEE));
			}
			else if (aString.equals("ATTACK.MELEE.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus()).toString());
			}
			else if (aString.equals("ATTACK.MELEE.TOTAL"))
			{
				fa.write(output, getAttackString(Constants.ATTACKSTRING_MELEE, calcStatMod(Constants.STRENGTH) + modForSize()));
			}
			else if (aString.equals("ATTACK.RANGED"))
			{
				fa.write(output, getAttackString(Constants.ATTACKSTRING_RANGED));
			}
			else if (aString.equals("ATTACK.RANGED.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus()).toString());
			}
			else if (aString.equals("ATTACK.RANGED.TOTAL"))
			{
				fa.write(output, getAttackString(Constants.ATTACKSTRING_RANGED, calcStatMod(Constants.DEXTERITY) + modForSize()));
			}
			else if (aString.equals("ATTACK.UNARMED"))
			{
				fa.write(output, getAttackString(Constants.ATTACKSTRING_UNARMED));
			}
			else if (aString.equals("ATTACK.UNARMED.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus()).toString());
			}
			else if (aString.equals("ATTACK.UNARMED.TOTAL"))
			{
				fa.write(output, getAttackString(Constants.ATTACKSTRING_UNARMED, calcStatMod(Constants.STRENGTH) + modForSize()));
			}
			else if (aString.startsWith("DAMAGE.UNARMED"))
			{
				fa.write(output, getUnarmedDamageString(false, true));
			}
			// SPELLMEMx.x.x.x.LABEL classNum.bookNum.level.spellnumber
			// LABEL is TIMES,NAME,RANGE,etc. if not supplied it defaults to NAME
			else if (aString.startsWith("SPELLMEM"))
			{
				replaceTokenSpellMem(aString, fa, output);
			}
			else if (aString.startsWith("SPELLBOOKNAME"))
			{
				int bookNum = Integer.parseInt(aString.substring(13));

				fa.write(output, (String)getSpellBooks().get(bookNum));
			}
			else if (aString.equals("SPELLPOINTS"))
			{
				fa.write(output, Globals.getSpellPoints());
			}
			else if (aString.startsWith("SPELLLIST"))
			//SPELLLISTCAST0.0 KNOWN0.0 BOOK0.0 TYPE0
			//0123456789   --> compare starting at position 9
			{
				int cmp = 0;
				if (aString.regionMatches(9,"TYPE",0,4))
					cmp = 3;
				else if (aString.regionMatches(9,"BOOK",0,4))
					cmp = 2;
				else if (aString.regionMatches(9,"KNOWN",0,5))
					cmp = 1;
				else if (aString.regionMatches(9,"CAST",0,4))
					cmp = 0;
				else if (aString.regionMatches(9,"CLASS",0,5))
					cmp = 4;
				else if (aString.regionMatches(9,"DCSTAT",0,6))
					cmp = 6;
				else if (aString.regionMatches(9,"DC",0,2))
					cmp = 5;
				else
					cmp = -1;
				int i = 13;
				if (cmp == 1 || cmp == 4)
					i = 14;
				else if (cmp == 5)
					i = 11;

				int level = 0;
				if (cmp != 3 && cmp != 4)
				{
					level = Integer.parseInt(aString.substring(i + 2, i + 3));
				}
				i = Integer.parseInt(aString.substring(i, i + 1)); // class index
				int y = 0;
				PCClass aClass = getSpellClassAtIndex(i);
				if (aClass != null)
				{
					final int stat = Globals.getStatFromAbbrev(aClass.getSpellBaseStat());
					int knownNum = 0;
					int specialtyNum = 0;
					int spellNum = aClass.spellList().size();
					String castNum = String.valueOf(aClass.getCastForLevel(aClass.getLevel().intValue(), level, Globals.getDefaultSpellBook())) +
						aClass.getBonusCastForLevelString(aClass.getLevel().intValue(), level, Globals.getDefaultSpellBook());
					knownNum = aClass.getKnownForLevel(aClass.getLevel().intValue(), level);
					specialtyNum = aClass.getSpecialtyKnownForLevel(aClass.getLevel().intValue(), level);
					spellNum = aClass.spellList().size();
					String cString = aClass.getKeyName();
					if (aClass.getCastAs().length() > 0)
						cString = aClass.getCastAs();
					if (spellNum == 0)
						return 0;
					switch (cmp)
					{
						case 0:
							fa.write(output, castNum);
							break;
						case 1:
							String mytemp = Integer.toString(knownNum);
							if (specialtyNum > 0)
								mytemp += "+" + specialtyNum;
							fa.write(output, mytemp);
							break;
						case 2:
							Spell sp = null;
							for (Iterator se = aClass.spellList().iterator(); se.hasNext();)
							{
								sp = (Spell)se.next();
								aString = cString;
								if (sp.levelForClass(aString, aClass.getName()).lastIndexOf("," + String.valueOf(level)) > -1)
								{
									if (y++ > 0)
										fa.write(output, ", ");
									fa.write(output, sp.getName());
								}
								if (y == 0 && existsOnly)
									noMoreItems = true;
							}
							break;
						case 3:
							fa.write(output, aClass.getSpellType());
							break;
						case 4:
							if (aString.endsWith("LEVEL"))
								fa.write(output, String.valueOf(aClass.getLevel().intValue() + getTotalBonusTo("PCLEVEL", aClass.getName(), true)));
							else
								fa.write(output, aClass.getName());
							break;
						case 5:
							String statString = "";
							int a = 0;
							if (stat >= 0)
							{
								statString = Globals.s_ATTRIBSHORT[stat];
								a = calcStatMod(stat);
								if (statString.equals(aClass.getSpellBaseStat()))
									a += getTotalBonusTo("STAT", "BASESPELLSTAT", true) / 2;
								a += getTotalBonusTo("STAT", "CAST=" + statString, true) / 2;
							}
							fa.write(output, new Integer(10 +
								getTotalBonusTo("STAT", aClass.getName(), true) / 2 +
								getTotalBonusTo("SPELL", "DC", true) +
								level + a).toString());
							break;
						case 6:
							fa.write(output,aClass.getSpellBaseStat());
							break;
					}
				}
			}
			else if (aString.equals("HP"))
				fa.write(output, new Integer(hitPoints()).toString());
			else if (aString.startsWith("LANGUAGES"))
			{
				replaceTokenLanguages(aString, fa, output);
			}
			else if (aString.equals("WEAPONPROFS"))
			{

				int i;
				int k = aString.lastIndexOf(',');
				if (k > -1)
					aString = aString.substring(k + 1);
				else
					aString = ", ";

				SortedSet stringList = getWeaponProfList();

				for (i = 0; i < stringList.size(); i++)
				{
					fa.write(output, (String)stringList.toArray()[i]);
					if (i < stringList.size() - 1)
						fa.write(output, aString);

				}
			}
			else if (aString.startsWith("ARMOR"))
			{
				len = replaceTokenArmor(aString, len, fa, output);
			}
			else if (aString.startsWith("WEAPON"))
			{
				replaceTokenWeapon(aString, fa, output);
			}
			else if (aString.startsWith("EQCONTAINER"))
			{
				replaceTokenEqContainer(aString, fa, output);
			}
			else if (aString.startsWith("EQ"))
			{
				aString = replaceTokenEq(aString);
				fa.encodeWrite(output, aString);
			}
			else if (aString.equals("TOTAL.WEIGHT"))
			{
				Float totalWeight = totalWeight();
				fa.encodeWrite(output, totalWeight.toString() + " " + Globals.getWeightDisplay());
			}
			else if (aString.equals("TOTAL.VALUE"))
			{
				Float totalValue = totalValue();
				fa.encodeWrite(output, totalValue.toString() + " " + Globals.getCurrencyDisplay());
			}
			else if (aString.equals("TOTAL.CAPACITY"))
			{
				fa.write(output, Globals.maxLoadForStrength(adjStats(Constants.STRENGTH)).toString());
			}
			else if (aString.equals("TOTAL.LOAD"))
			{
				replaceTokenTotalLoad(fa, output);
			}
			else if (aString.startsWith("MISC."))
			{
				int i = -1;
				if (aString.substring(5).startsWith("FUNDS"))
					i = 0;
				else if (aString.substring(5).startsWith("COMPANIONS"))
					i = 1;
				else if (aString.substring(5).startsWith("MAGIC"))
					i = 2;

				/** What does this code do????*/
				//
				// This is what:
				// for tags like in this FOR loops, will insert info after the ',' at end of each item
				// |MISC.MAGIC,</fo:block><fo:block font-size="7pt">|
				int k = aString.lastIndexOf(',');
				if (k > -1)
					aString = aString.substring(k + 1);
				else
					aString = "";
				/** It doesn't seem to have any effect...*/

				if (i >= 0)
				{
					ArrayList stringList = getLineForMiscList(i);
					for (i = 0; i < stringList.size(); i++)
					{
						fa.encodeWrite(output, (String)stringList.get(i));
						fa.write(output, aString);
					}
				}
			}
			else if (aString.equals("GOLD"))
				fa.write(output, getGold().toString());
			else if (aString.equals("DEFENSE"))
				fa.write(output, defense().toString());
			else if (aString.startsWith("DEFENSE.CLASS"))
			{
				final int defenseclass = Integer.parseInt(aString.substring(13));
				if (defenseclass >= classList.size() && existsOnly)
					noMoreItems = true;
				if (defenseclass >= classList.size())
					return 0;
				PCClass aClass = (PCClass)classList.get(defenseclass);
				fa.write(output, aClass.defense(defenseclass).toString());
			}
			else if (aString.equals("DEFENSE.CTOTAL"))
			{
				replaceTokenDefenseCtotal(fa, output);
			}
			else if (aString.equals("FORCEPOINTS"))
				fa.write(output, getFPoints());
			else if (aString.equals("DSIDEPOINTS"))
				fa.write(output, getDPoints());
			else if (aString.equals("WOUNDPOINTS"))
				fa.write(output, woundPoints().toString());
			else if (aString.equals("REPUTATION"))
				fa.write(output, reputation().toString());
			else if (aString.equals("POOL.CURRENT"))
				fa.write(output, new Integer(poolAmount).toString());
			else if (aString.equals("POOL.COST"))
				fa.write(output, new Integer(costPool).toString());
			else if (aString.equals("PLAYERNAME"))
				fa.encodeWrite(output, playersName);
			else if (aString.equals("VISION"))
			{
				doVision(visionOutput, 2);
				// A little redundant but seems needed based on some errors I was seeing.
				fa.encodeWrite(output, visionOutput);
			}
			else if (aString.startsWith("WEIGHT."))
			{
				replaceTokenWeight(aString, fa, output);
			}
			else if (aString.equals("RACE.ABILITYLIST"))
			{
				replaceTokenRaceAbilityList(fa, output);
			}
			else if (aString.startsWith("VAR."))
			{
				replaceTokenVar(aString, fa, output);
			}
			// else if (aString.startsWith("IIF("))
			// {
			// 	replaceTokenIIF(aString, fa, output);
			// }
			else if (aString.startsWith("OIF("))
			{
				replaceTokenIIF(aString, fa, output);
			}
			else if (aString.startsWith("DIR."))
			{
				replaceTokenDir(aString, fa, output);
			}
			else
			{
				len = aString.trim().length();
				fa.write(output, aString);
			}
			return len;
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			GuiFacade.showMessageDialog(null, "Error replacing " + aString, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}


	private void replaceTokenIIF(String aString, FileAccess fa, BufferedWriter output)
	{
		int iParenCount = 0;
		String aT[] = new String[3];
		int i;
		int iParamCount = 0;
		int iStart = 4;

		// IIF(expr,truepart,falsepart)
		// {|IIF(HASFEAT:Armor Proficiency (Light),}{\f14\fs18\lang1033\cgrid0\'fd}{\fs18 ,}{\f14\fs18\lang1033\cgrid0 \'a8}{\fs18 )|}

		for (i = iStart; i < aString.length(); i++)
		{
			if (iParamCount == 3)
			{
				break;
			}

			switch (aString.charAt(i))
			{
				case '(':
					iParenCount += 1;
					break;

				case ')':
					iParenCount -= 1;
					if (iParenCount == -1)
					{
						if (iParamCount == 2)
						{
							aT[iParamCount++] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							System.out.println("IIF: not enough parameters");
						}
					}
					break;

				case ',':
					if (iParenCount == 0)
					{

						if (iParamCount < 2)
						{
							aT[iParamCount] = aString.substring(iStart, i).trim();
							iStart = i + 1;
						}
						else
						{
							System.out.println("IIF: too many parameters");
						}
						iParamCount += 1;
					}
					break;

				default:
					break;
			}
		}

		if (iParamCount != 3)
		{
			System.out.print("IIF: invalid parameter count: " + iParamCount);
		}
		else
		{
			aString = aString.substring(iStart);

			iStart = 2;
			if (evaluateExpression(aT[0]))
				iStart = 1;
			fa.write(output, aT[iStart]);
		}

		if (aString.length() > 0)
		{
			System.out.println("IIF: extra characters on line: " + aString);
			fa.write(output, aString);
		}
	}


	private void replaceTokenVar(String aString, FileAccess fa, BufferedWriter output)
	{
		boolean isMin = aString.lastIndexOf(".MINVAL") > -1;
		int index = aString.length();
		if (aString.lastIndexOf(".INTVAL") > -1)
			index = aString.lastIndexOf(".INTVAL");
		if (aString.lastIndexOf(".MINVAL") > -1)
			index = Math.min(index, aString.lastIndexOf(".MINVAL"));
		Float val = getVariable(aString.substring(4, index), !isMin, true, "", "");
		if (val.doubleValue() > 0.0)
			fa.write(output, "+");
		if (aString.lastIndexOf(".INTVAL") > -1)
		{
			final int pos = val.toString().lastIndexOf(".");
			fa.write(output, val.toString().substring(0, pos));
		}
		else
		{
			fa.write(output, val.toString());
		}
	}

	private void replaceTokenRaceAbilityList(FileAccess fa, BufferedWriter output)
	{
		int i = 0;
		String bString = null;
		for (Iterator e = getRace().getSpecialAbilities().iterator(); e.hasNext();)
		{
			bString = (String)e.next();
			if (i++ > 0)
				fa.write(output, ", ");
			fa.encodeWrite(output, bString);
		}
	}

	private void replaceTokenDir(String aString, FileAccess fa, BufferedWriter output)
	{
		if (aString.endsWith("PCGEN"))
			fa.write(output, Globals.getPcgenSystemDir().getAbsolutePath());
		else if (aString.endsWith("TEMPLATES"))
			fa.write(output, Globals.getTemplatePath().getAbsolutePath());
		else if (aString.endsWith("PCG"))
			fa.write(output, Globals.getPcgPath().getAbsolutePath());
		else if (aString.endsWith("HTML"))
			fa.write(output, Globals.getHtmlOutputPath().getAbsolutePath());
		else if (aString.endsWith("TEMP"))
			fa.write(output, Globals.getTempPath().getAbsolutePath());
		else
		{
			System.out.println("DIR: Unknown Dir: " + aString);
			fa.write(output, aString);
		}
	}

	private void replaceTokenWeight(String aString, FileAccess fa, BufferedWriter output)
	{
		int i = 1;
		if (aString.endsWith("MEDIUM"))
			i = 2;
		else if (aString.endsWith("HEAVY"))
			i = 3;
		fa.write(output, new Float(i * Globals.maxLoadForStrength(adjStats(Constants.STRENGTH)).intValue() / 3).toString());
	}

	private void replaceTokenDefenseCtotal(FileAccess fa, BufferedWriter output)
	{
		int j = -1;
		int total = 0;
		int x = -1;
		String myString = null;
		PCClass myClass = null;
		for (j = 0; j < classList.size(); j++)
		{
			myClass = (PCClass)classList.get(j);
			myString = myClass.defense(j).toString();
			x = Integer.parseInt(myString);
			total += x;
		}
		fa.write(output, String.valueOf(total));
	}

	private void replaceTokenTotalLoad(FileAccess fa, BufferedWriter output)
	{
		int load = Globals.loadTypeForStrength(adjStats(Constants.STRENGTH), totalWeight());
		switch (load)
		{
			case Constants.LIGHT_LOAD:
				fa.write(output, "Light");
				return;
			case Constants.MEDIUM_LOAD:
				fa.write(output, "Medium");
				return;
			case Constants.HEAVY_LOAD:
				fa.write(output, "Heavy");
				return;
			case Constants.OVER_LOAD:
				fa.write(output, "Overload");
				return;
			default:
				fa.write(output, "Unknown");
				Globals.debugPrint("Unknown load constant detected in PlayerCharacter.replaceTokenTotalLoad, the constant was " + load + ".");
		}
	}

	private String replaceTokenEq(String aString)
	{
		Collection tempList = getEquipmentList().values();
		ArrayList aList = new ArrayList();
		String retString = "";

		Equipment someEquip = null;
		for (Iterator locIter = tempList.iterator(); locIter.hasNext();)
		{
			someEquip = (Equipment)locIter.next();
			if (!someEquip.getHasHeaderParent())
			{
				aList.add(someEquip);
			}
		}

		StringTokenizer aTok = null;
		String aType = null;
		if (aString.startsWith("EQTYPE"))
		{
			aTok = new StringTokenizer(aString.substring(6), ".", false);
			aType = (String)aTok.nextToken();
			if (aType.equals("Container"))
			{
				aList.clear();
				Equipment anEquip = null;
				for (Iterator locIter = tempList.iterator(); locIter.hasNext();)
				{
					anEquip = (Equipment)locIter.next();
					if (anEquip.getHasHeaderParent() || anEquip.acceptsChildren())
					{
						aList.add(anEquip);
					}
				}

			}
			else
			{
				aList = this.getEquipmentOfType(aType, 3);
			}
		}
		else
		{
			aTok = new StringTokenizer(aString.substring(2), ".", false);
		}


		//Begin Not code...
		while (aTok.countTokens() > 2)	//should be ok, assumes last two fields are # and a Param
		{
			String bString = (String)aTok.nextToken();
			if (bString.equalsIgnoreCase("NOT"))
				aList = new ArrayList(removeEqType(aList, aTok.nextToken()));
			else if (bString.equalsIgnoreCase("ADD"))
				aList = new ArrayList(addEqType(aList, aTok.nextToken()));
			else if (bString.equalsIgnoreCase("IS"))
				aList = new ArrayList(removeNotEqType(aList, aTok.nextToken()));
		}
		//End Not code... essentiall you add a NOT.type into either the EQTYPE or EQ tokens...
		//Thus to get all EQ except coins:  EQ.NOT:Coin.%.LONGNAME  inside a for loop.  You can use more than 1 NOT,
		//but each needs to be prepended by a NOT  ie.  NOT:Coin.NOT.Gem  using the ADD:Type keyword adds that type into the list
		//for example EQTYPE.Coin.ADD.Gem.%.Longname includes all Coins and all Gems.
		//stuff added in will not be in alphabetical order... I don't think I'll document it until that is the case.

		final int temp = Integer.parseInt(aTok.nextToken());
		String tempString = aTok.nextToken();
		Equipment eq = null;
		Iterator setIter = null;
		if (temp >= 0 && temp < aList.size())
		{
			setIter = aList.iterator();
			for (int count = temp; count > 0; --count, setIter.next()) ;
			eq = (Equipment)setIter.next();
		}
		if (existsOnly && (temp < 0 || temp >= aList.size() - 1))
			noMoreItems = true;
		if (eq != null)
		{
			if (tempString.equals("LONGNAME"))
			{
				retString = (eq.longName());
			}
			else if (tempString.equals("NAME"))
				retString = (eq.getName());
			else if (tempString.equals("WT"))
				retString = (eq.getWeight().toString());
			else if (tempString.equals("TOTALWT"))
				retString = (Float.toString(eq.qty().floatValue() * eq.getWeight().floatValue()));
			else if (tempString.equals("COST"))
				retString = (eq.getCost().toString());
			else if (tempString.equals("QTY"))
				retString = (eq.qty().toString());
			else if (tempString.equals("EQUIPPED") && eq.isEquipped())
				retString = ("Y");
			else if (tempString.equals("EQUIPPED") && !eq.isEquipped())
				retString = ("N");
			else if (tempString.equals("CARRIED"))
			{
				retString = (String.valueOf(eq.numberCarried()));
			}
			else if (tempString.equals("CONTENTS"))
				retString = (eq.getContainerContentsString());
			else if (tempString.equals("LOCATION"))
				retString = (eq.getParentName());
			else if (tempString.equals("ACMOD"))
				retString = (eq.getAcMod().toString());
			else if (tempString.equals("MAXDEX"))
				retString = (eq.getMaxDex().toString());
			else if (tempString.equals("ACCHECK"))
				retString = (eq.acCheck().toString());
			else if (tempString.equals("MOVE"))
				retString = (eq.moveString());
			else if (tempString.equals("TYPE"))
				retString = (eq.getType());
			else if (tempString.startsWith("TYPE") && tempString.length() > 4)
			{
				int x = Integer.parseInt(tempString.substring(4));
				retString = (eq.typeIndex(x));
			}
			else if (tempString.equals("SPELLFAILURE"))
				retString = (eq.spellFailure().toString());
			else if (tempString.equals("SIZE"))
				retString = (eq.getSize());
			else if (tempString.equals("SIZELONG"))
			{
			}
			else if (tempString.equals("DAMAGE"))
				retString = (eq.getDamage());
			else if (tempString.equals("CRITRANGE"))
				retString = (eq.getCritRange());
			else if (tempString.equals("CRITMULT"))
				retString = (eq.getCritMult());
			else if (tempString.equals("ALTDAMAGE"))
				retString = (eq.getAltDamage());
			else if (tempString.equals("ALTCRIT"))
				retString = (eq.getAltCritMult());
			else if (tempString.equals("RANGE"))
				retString = (eq.getRange().toString());
			else if (tempString.equals("ATTACKS"))
				retString = (eq.getAttacks().toString());
			else if (tempString.equals("PROF"))
				retString = (eq.profName());
			else if (tempString.equals("SPROP"))
			{
				retString = (eq.getSpecialProperties());
			}
		}
		return retString;
	}

	private void replaceTokenLanguages(String aString, FileAccess fa, BufferedWriter output)
	{
		if (aString.length() > 9)
		{
			int e = -1;
			SortedSet aSet = getLanguagesList();
			e = Integer.parseInt(aString.substring(9));
			if (e >= 0 && e < aSet.size())
			{
				fa.encodeWrite(output, aSet.toArray()[e].toString());
			}
			else if (existsOnly)
				noMoreItems = true;
		}
		else
		{
			int c = 0;
			for (Iterator setIter = getLanguagesList().iterator(); setIter.hasNext();)
			{
				if (c > 0)
					fa.write(output, ", ");
				fa.encodeWrite(output, (String)setIter.next().toString());
				c++;
			}
		}
	}

	private int replaceTokenSpecialAbility(String aString, FileAccess fa, BufferedWriter output)
	{
		int len;
		int specialability;
		String sDelim = "\r\n";
		if (aString.indexOf(".DESCRIPTION.") > -1)
			sDelim = aString.substring(aString.indexOf(".DESCRIPTION.") + 13);

		if (aString.indexOf(".DESCRIPTION") > -1)
		{
			specialability = Integer.parseInt(aString.substring(14, aString.indexOf(".DESCRIPTION")));
		}
		else
			specialability = Integer.parseInt(aString.substring(14, aString.length()));
		if (specialability >= getSpecialAbilityTimesList().size() && existsOnly)
			noMoreItems = true;
		len = getSpecialAbilityTimesList().size();
		if (specialability >= 0 && specialability < len)
			if (aString.indexOf(".DESCRIPTION") > -1)
				replaceWithDelimiter(fa, output, getItemDescription("SA", getSpecialAbilityTimesList().get(specialability).toString(), ""), sDelim);
			else
				fa.encodeWrite(output, getSpecialAbilityTimesList().get(specialability).toString());
		return len;
	}

	private int replaceTokenSpecialList(String aString, FileAccess fa, BufferedWriter output)
	{
		int len;
		String delim = aString.substring(11);
		if (delim.equals(""))
			delim = ",";
		int i = 0;
		len = getSpecialAbilityTimesList().size();
		for (Iterator e = getSpecialAbilityTimesList().iterator(); e.hasNext();)
		{
			if (i++ > 0)
				fa.write(output, delim);
			fa.write(output, (String)e.next());
		}
		return len;
	}

	private void replaceTokenSkillListMods(FileAccess fa, BufferedWriter output)
	{
		int i = 0;
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			final Skill aSkill = (Skill)e.next();
			int modSkill = -1;
			if (aSkill.keyStat().compareToIgnoreCase(Constants.s_NONE) != 0)
			{
				modSkill = aSkill.modifier().intValue() - calcStatMod(aSkill.statIndex(aSkill.keyStat()));
			}
			if (aSkill.getTotalRank().intValue() > 0 || modSkill > 0)
			{
				final int temp = aSkill.modifier().intValue() + aSkill.getTotalRank().intValue();
				if (i > 0)
					fa.write(output, ", ");
				fa.write(output, aSkill.getName() + " +" + Integer.toString(temp));
				i++;
			}
		}
	}

	private void writeToken(final int acSizeMod, FileAccess fa, BufferedWriter output)
	{
		fa.write(output, Delta.toString(acSizeMod));
	}

	/**
	 * AC related stuff
	 * possible tokens are
	 *   AC.FLAT
	 *   AC.TOUCH
	 *   AC.TOTAL
	 *   AC.BASE
	 *   AC.MISC
	 *   AC.list
	 *   AC.TOTAL.list
	 *   AC.BASE.list
	 *   AC.MISC.list
	 * where
	 *   list    := ((include|exclude)del)*(include|exclude)
	 *   include := "ABILITY"|"ARMOR"|"CLASS"|"DEFLECTION"|"DEX"|
	 *              "DODGE"|"EQUIP"|"NATURAL"|"SHIELD"|"SIZE"
	 *   exclude := "NOABILITY"|"NOARMOR"|"NOCLASS"|"NODEFLECTION"|
	 *              "NODEX"|"NODODGE"|"NOEQUIP"|"NONATURAL"|"NOSHIELD"|"NOSIZE"
	 *   del     := "."
	 * given as regular expression
	 *
	 * "include"-s will add the appropriate modifier
	 * "exclude"-s will subtract the appropriate modifier
	 *
	 * (This means AC.EQUIP.NOEQUIP equals 0
	 *  whereas AC.SHIELD.SHIELD equals 2 times the shield bonus)
	 *
	 * If you use unrecognized terminals, their value will amount to 0.
	 * (This means AC.BLABLA equals 0
	 *  whereas AC.SHIELD.BLABLA equals AC.SHIELD)
	 *
	 * TODO:
	 *  "NATURAL_BYTYPE" or "NATURAL_MAGIC" (if that's possible or makes sense at all)
	 *
	 *
	 * updated for new AC calculations
	 *
	 * author: Thomas Behr 07-02-01
	 */
	private void replaceTokenAC(String aString, FileAccess fa, BufferedWriter output)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ".");
		String tokens[] = new String[aTok.countTokens()];
		for (int i = 0; aTok.hasMoreTokens(); i++)
		{
			tokens[i] = aTok.nextToken();
		}
		int i = 1;

		int ac = 0;
		if (tokens.length > i)
		{

			if (tokens[i].equals("FLAT"))
			{
				fa.write(output, Integer.toString(flatFootedAC()));
				return;
			}
			else if (tokens[i].equals("TOUCH"))
			{
				fa.write(output, Integer.toString(touchAC()));
				return;
			}
			else if (tokens[i].equals("TOTAL"))
			{
				ac += totalAC();
			}
			else if (tokens[i].equals("BASE"))
			{
//  				ac += getRace().getStartingAC().intValue()
//  					+ naturalArmorModForSize();
				ac += baseAC();
			}
			/**
			 * this includes everthing but AC.BASE
			 */
			else if (tokens[i].equals("MISC"))
			{
//  				ac += acMod() + modToFromEquipment("AC");
				ac += totalAC() - baseAC();
			}

		}

		for (; i < tokens.length; i++)
		{

			/**
			 * this does discern between "include" and "exclude"
			 */
			int mult = 1;
			if (tokens[i].startsWith("NO"))
			{
				mult = -1;
				tokens[i] = tokens[i].substring(2);
			}

			if (tokens[i].equals("ABILITY"))
			{
				ac += acCalculator.acModFromAbilities() * mult;
			}
			else if (tokens[i].equals("ARMOR"))
			{
				ac += acCalculator.acModFromArmor() * mult;
			}
			else if (tokens[i].equals("CLASS"))
			{
				ac += acCalculator.acModFromClass() * mult;
			}
			else if (tokens[i].equals("DEFLECTION"))
			{
				ac += acCalculator.calculateACBonusByType("Deflection", null) * mult;
			}
			else if (tokens[i].equals("DEX"))
			{
				ac += acCalculator.acModFromDexterity() * mult;
			}
			else if (tokens[i].equals("DODGE"))
			{
				ac += acCalculator.calculateACBonusByType("Dodge", null) * mult;
			}
			else if (tokens[i].equals("EQUIP"))
			{
//  				ac += (modToFromEquipment("AC")
//  					+ getEquipmentBonusTo("COMBAT", "AC", true)) * mult;
				ac += acCalculator.acModFromEquipment() * mult;
			}
			else if (tokens[i].equals("NATURAL"))
			{
				ac += acCalculator.acModFromNatural() * mult;
			}
			else if (tokens[i].equals("NATURAL_BYSOURCE"))
			{
				ac += acCalculator.acModFromNaturalBySource() * mult;
			}
			else if (tokens[i].equals("SHIELD"))
			{
				ac += acCalculator.acModFromShield() * mult;
			}
			else if (tokens[i].equals("SIZE"))
			{
//  				ac += acSizeMod() * mult;
				ac += acCalculator.acModFromSize() * mult;
			}
		}

		fa.write(output, Integer.toString(ac));
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
		String tokens[] = new String[aTok.countTokens()];
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
			else if (tokens[i].equals("BASE"))
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

	private void replaceTokenFortitude(String aString, FileAccess fa, BufferedWriter output)
	{
		fa.write(output,
			Delta.toString(_calculateSaveBonus(1, "Fortitude",
				Constants.CONSTITUTION,
				aString)));
	}

	private void replaceTokenReflex(String aString, FileAccess fa, BufferedWriter output)
	{
		fa.write(output,
			Delta.toString(_calculateSaveBonus(2, "Reflex",
				Constants.DEXTERITY,
				aString)));
	}

	private void replaceTokenWill(String aString, FileAccess fa, BufferedWriter output)
	{
		fa.write(output,
			Delta.toString(_calculateSaveBonus(3, "Willpower",
				Constants.WISDOM,
				aString)));
	}


	private void replaceTokenForDfor(String aString, FileAccess fa, BufferedWriter output)
	{
		int x = 0;
		int i = 0;
		StringTokenizer aTok;
		if (aString.startsWith("DFOR."))
			aTok = new StringTokenizer(aString.substring(5), ",", false);
		else
			aTok = new StringTokenizer(aString.substring(4), ",", false);
		int cMin = 0;
		int cMax = 100;
		int cStep = 1;
		int cStepLine = 0;
		int cStepLineMax = 0;
		String cString = "";
		String cStartLineString = "";
		String cEndLineString = "";
		String bString = null;
		boolean isDFor = false;
		while (aTok.hasMoreTokens())
		{
			bString = aTok.nextToken();
			switch (i++)
			{
				case 0:
					Float aFloat = getVariableValue(bString, "");
					cMin = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					break;
				case 1:
					aFloat = getVariableValue(bString, "");
					cMax = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					break;
				case 2:
					aFloat = getVariableValue(bString, "");
					cStep = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					if (aString.startsWith("DFOR."))
					{
						isDFor = true;
						bString = aTok.nextToken();
						aFloat = getVariableValue(bString, "");
						cStepLineMax = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
						bString = aTok.nextToken();
						aFloat = getVariableValue(bString, "");
						cStepLine = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					}
					break;
				case 3:
					cString = bString;
					break;
				case 4:
					cStartLineString = bString;
					break;
				case 5:
					cEndLineString = bString;
					break;
				case 6:
					existsOnly = !bString.equals("0");
					if (bString.equals("2")) checkBefore = true;
					break;
			}
		}
		if (cEndLineString.equals("COMMA")) cEndLineString = ",";
		int iStart = cMin;
		int iNow = iStart;
		if (!isDFor)
			cStepLine = 1;
		while (iStart < cMax)
		{
			if (x++ == 0)
				fa.write(output, cStartLineString);
			iNow = iStart;
			if (!isDFor)
				cStepLineMax = iNow + cStep;
			if ((cStepLineMax > cMax) && !isDFor)
				cStepLineMax = cMax;
			while (iNow < cStepLineMax || (isDFor && iNow < cMax))
			{
				aTok = new StringTokenizer(cString, csheetTag2, false);
				int j = 0;
				while (aTok.hasMoreTokens())
				{
					String eString = aTok.nextToken();
					int index = eString.lastIndexOf('%');
					if (index < eString.length() - 1 && eString.charAt(index + 1) != '.')
						index = -1;
					String fString = "";
					String gString = "";
					String hString = eString;
					if (index > -1)
					{
						fString = eString.substring(0, index);
						if (index + 1 < eString.length())
							gString = eString.substring(index + 1);
						hString = fString + new Integer(iNow).toString() + gString;
					}
					if (eString.equals("%0") || eString.equals("%1"))
					{
						final int cInt = iNow + Integer.parseInt(eString.substring(1));
						fa.write(output, new Integer(cInt).toString());
					}
					else
					{
						replaceToken(hString, output);
					}
					if (checkBefore && noMoreItems)
					{
						iNow = cMax;
						iStart = cMax;
						if (j == 0)
							existsOnly = false;
						break;
					}
					j++;
				}
				iNow += cStepLine;
				if (cStepLine == 0)
					break;
			}
			if (cStepLine > 0 || (cStepLine == 0 && x == cStep) || (existsOnly == noMoreItems))
			{
				fa.write(output, cEndLineString);
				fa.newLine(output);
				x = 0;
				if (existsOnly && noMoreItems)
					return;
			}
			iStart += cStep;
		}
	}

	private void replaceTokenEqContainer(String aString, FileAccess fa, BufferedWriter output)
	{
		Collection tempList = getEquipmentList().values();
		ArrayList aList = new ArrayList();
		String indentSymbol = "\t";
		if (aString.startsWith("EQCONTAINERW"))
			indentSymbol = "&nbsp&nbsp";

		for (Iterator locIter = tempList.iterator(); locIter.hasNext();)
		{
			Equipment anEquip = (Equipment)locIter.next();
			if (!anEquip.isHeaderParent() && anEquip.getChildCount() > 0 && anEquip.getUberParent() == anEquip)
			{
				aList.add(anEquip);
				generateContainerList(aList);
			}
		}

		StringTokenizer aTok = null;
		aTok = new StringTokenizer(aString.substring(12), ".", false);
		final int eqcontainer = Integer.parseInt(aTok.nextToken());
		String tempString = aTok.nextToken();
		Equipment eq = null;
		if (eqcontainer >= 0 && eqcontainer < aList.size())
		{
			Iterator setIter = aList.iterator();
			for (int count = eqcontainer; count > 0; --count, setIter.next()) ;
			eq = (Equipment)setIter.next();
		}
		if (existsOnly && (eqcontainer < 0 || eqcontainer >= aList.size() - 1))
			noMoreItems = true;
		if (eq != null)
		{
			if (tempString.equals("LONGNAME"))
			{
				int depth = eq.itemDepth();
				while (depth > 0)
				{
					fa.write(output, indentSymbol);
					depth--;
				}
				fa.encodeWrite(output, eq.longName());
			}
			else if (tempString.equals("NAME"))
				fa.encodeWrite(output, eq.getName());
			else if (tempString.equals("WT"))
			{
				if (eq.getChildCount() == 0)
					fa.write(output, eq.getWeight().toString());
				else
					fa.write(output, (new Float(eq.getContainedWeight().floatValue() + eq.getWeight().floatValue())).toString());
			}
			else if (tempString.equals("COST"))
				fa.write(output, eq.getCost().toString());
			else if (tempString.equals("QTY"))
				fa.write(output, eq.qty().toString());
			else if (tempString.equals("EQUIPPED") && eq.isEquipped())
				fa.write(output, "Y");
			else if (tempString.equals("EQUIPPED") && !eq.isEquipped())
				fa.write(output, "N");
			else if (tempString.equals("CARRIED"))
			{
				fa.write(output, String.valueOf(eq.numberCarried()));
			}
			else if (tempString.equals("CONTENTS"))
				fa.encodeWrite(output, eq.getContainerContentsString());
			else if (tempString.equals("LOCATION"))
				fa.write(output, eq.getParentName());
			else if (tempString.equals("ACMOD"))
				fa.write(output, eq.getAcMod().toString());
			else if (tempString.equals("MAXDEX"))
				fa.write(output, eq.getMaxDex().toString());
			else if (tempString.equals("ACCHECK"))
				fa.write(output, eq.acCheck().toString());
			else if (tempString.equals("MOVE"))
				fa.write(output, eq.moveString());
			else if (tempString.equals("TYPE"))
				fa.write(output, eq.getType());
			else if (tempString.startsWith("TYPE") && tempString.length() > 4)
			{
				int x = Integer.parseInt(tempString.substring(4));
				fa.write(output, eq.typeIndex(x));
			}
			else if (tempString.equals("SPELLFAILURE"))
				fa.write(output, eq.spellFailure().toString());
			else if (tempString.equals("SIZE"))
				fa.write(output, eq.getSize());
			else if (tempString.equals("DAMAGE"))
				fa.write(output, eq.getDamage());
			else if (tempString.equals("CRITRANGE"))
				fa.write(output, eq.getCritRange());
			else if (tempString.equals("CRITMULT"))
				fa.write(output, eq.getCritMult());
			else if (tempString.equals("ALTDAMAGE"))
				fa.write(output, eq.getAltDamage());
			else if (tempString.equals("ALTCRIT"))
				fa.write(output, eq.getAltCritMult());
			else if (tempString.equals("RANGE"))
				fa.write(output, eq.getRange().toString());
			else if (tempString.equals("ATTACKS"))
				fa.write(output, eq.getAttacks().toString());
			else if (tempString.equals("PROF"))
				fa.encodeWrite(output, eq.profName());
			else if (tempString.equals("SPROP"))
			{
				fa.encodeWrite(output, eq.getSpecialProperties());
			}
		}
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

	private ArrayList getExpandedWeapons()
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
			else if (equip.isMelee() && equip.isRanged())
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
				// NOTE: Cannot use primaryWeapons.indexOf(equip), as equals() is overridden in Equipment.java
				//
				//int iPrimary = primaryWeapons.indexOf(equip);
				int iPrimary;
				for (iPrimary = primaryWeapons.size() - 1; iPrimary >= 0; iPrimary--)
				{
					final Equipment teq = (Equipment)primaryWeapons.get(iPrimary);
					if (teq.equalTo(equip))
					{
						break;
					}
				}
				if (iPrimary >= 0)
				{
					primaryWeapons.set(iPrimary, eqm);
				}

				//
				// Replace any secondary weapons
				// NOTE: Cannot use secondaryWeapons.indexOf(equip), as equals() is overridden in Equipment.java
				//
				//int iSecondary = secondaryWeapons.indexOf(equip);
				int iSecondary;
				for (iSecondary = secondaryWeapons.size() - 1; iSecondary >= 0; iSecondary--)
				{
					final Equipment teq = (Equipment)secondaryWeapons.get(iSecondary);
					if (teq.equalTo(equip))
					{
						break;
					}
				}
				if (iSecondary >= 0)
				{
					secondaryWeapons.set(iSecondary, eqm);
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
					primaryWeapons.add(++iPrimary, eqm);
				}
				else if (iSecondary >= 0)
				{
					//primaryWeapons.add(eqm);
					secondaryWeapons.add(++iSecondary, eqm);
				}
			}
		}
		return weapList;
	}


	private void replaceTokenWeapon(String aString, FileAccess fa, BufferedWriter output)
	{
		// WEAPONP.
		// WEAPONO.
		// WEAPONH.
		// WEAPONx.
		int weapon = 0;
		if (aString.substring(6, 7).equals("P"))
			weapon = -1; // primary
		else if (aString.substring(6, 7).equals("O"))
			weapon = -2; // off-hand
		else if (aString.substring(6, 7).equals("H"))
			weapon = -3; // unarmed
		else
			weapon = Integer.parseInt(aString.substring(6, aString.lastIndexOf('.')));

		StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		aString = aTok.nextToken();		// Ignore Weaponx
		aString = aTok.nextToken();
		Equipment eq = null;
		if (weapon == -1)
		{
			//eq = primaryWeapon;
			if (!primaryWeapons.isEmpty())
			{
				eq = (Equipment)primaryWeapons.get(0);
			}
		}
		else if (weapon == -2)
		{
			//eq = secondaryWeapon[0];
			if (!secondaryWeapons.isEmpty())
			{
				eq = (Equipment)secondaryWeapons.get(0);
			}
		}
		else if (weapon == -3)
		{
			eq = getEquipmentNamed("Unarmed Strike");
		}
		else
		{
			ArrayList aArrayList = getExpandedWeapons();

			if (weapon < aArrayList.size())
				eq = (Equipment)aArrayList.get(weapon);
			if (weapon == aArrayList.size() - 1 && existsOnly)
				noMoreItems = true;
		}

		if (eq != null)
		{
			boolean isDouble = (eq.getHand() == Equipment.BOTH_HANDS && eq.isDouble());
			int index = 0;
			if (aString.startsWith("NAME"))
			{
				if (eq.isEquipped())
					fa.write(output, "*");
				fa.encodeWrite(output, eq.getName());
			}
			else if (aString.startsWith("LONGNAME"))
			{
				if (eq.isEquipped())
					fa.write(output, "*");
				fa.encodeWrite(output, eq.longName());
			}
			else if (aString.startsWith("ATTACKS"))
			{
				fa.write(output, eq.getAttacks().toString());
			}
			else if (aString.startsWith("CRIT"))
			{
				final int rawCritRange = eq.getRawCritRange();

				// see if the weapon has any crit range
				if (rawCritRange == 0)
				{
					// no crit range!
					fa.write(output, "none");
				}
				else
				{
					final int dbl = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITRANGEDOUBLE", true);
					final int iAdd = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITRANGEADD", true);

					int eqDbl = eq.getCritRangeDouble(true) + dbl;
					int critrange = eq.getRawCritRange() * (eqDbl + 1);
					critrange = 21 - (critrange + iAdd + eq.getCritRangeAdd(true));

					fa.write(output, String.valueOf(critrange));
					if (critrange < 20)
					{
						fa.write(output, "-20");
					}

					if (isDouble && eq.getAltCritRange().length() > 0)
					{
						eqDbl = eq.getCritRangeDouble(false) + dbl;
						int altCritRange = eq.getRawCritRange() * (eqDbl + 1);
						altCritRange = 21 - (altCritRange + iAdd + eq.getCritRangeAdd(false));

						if (altCritRange != critrange)
						{
							fa.write(output, "/" + String.valueOf(altCritRange));
							if (altCritRange < 20)
							{
								fa.write(output, "-20");
							}
						}
					}
				}
			}
			else if (aString.startsWith("MULT"))
			{
				int mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITMULTADD", true);
				int critMult;
				try
				{
					critMult = Integer.parseInt(eq.getCritMult().substring(1));
				}
				catch (Exception e)
				{
					critMult = 0;
				}

				final String totMult = String.valueOf((critMult + mult));
				fa.write(output, totMult);
				final int altCrit = eq.getAltCritMultiplier();
				if (isDouble && (altCrit > 0))
				{
					final String totCrit = String.valueOf(altCrit + mult);
					fa.write(output, "/" + totCrit);
				}
			}
			else if (aString.startsWith("RANGE"))
				fa.encodeWrite(output, eq.getRange().toString() + Globals.getAbbrMovementDisplay());
			else if (aString.startsWith("TYPE"))
			{
				if (eq.isBludgeoning())
					fa.write(output, "B");
				if (eq.isPiercing())
					fa.write(output, "P");
				if (eq.isSlashing())
					fa.write(output, "S");
				if (eq.isDouble())
				{
					fa.write(output, "/");
					if (eq.isBludgeoning(false))
						fa.write(output, "B");
					if (eq.isPiercing(false))
						fa.write(output, "P");
					if (eq.isSlashing(false))
						fa.write(output, "S");
				}
			}
			else if (aString.startsWith("HIT") || aString.startsWith("TOTALHIT"))
			{
				String mString = getAttackString(Constants.ATTACKSTRING_MELEE, calcStatMod(Constants.STRENGTH));
				String rString = getAttackString(Constants.ATTACKSTRING_MELEE, calcStatMod(Constants.DEXTERITY));
				if (eq.isMonk())
				{
					String m1String = getAttackString(Constants.ATTACKSTRING_UNARMED, calcStatMod(Constants.STRENGTH));
					if (m1String.length() > mString.length())
						mString = m1String;
					else if (m1String.length() == mString.length() && !mString.equals(m1String))
					{
						final StringTokenizer mTok = new StringTokenizer(mString, "+/", false);
						final StringTokenizer m1Tok = new StringTokenizer(m1String, "+/", false);
						final String msString = mTok.nextToken();
						final String m1sString = m1Tok.nextToken();
						if (Integer.parseInt(m1sString) >= Integer.parseInt(msString))
							mString = m1String;
					}
				}
				index = 0;
				int secondaryBonus = 0;
				int primaryBonus = 0;
				if (eq.isNatural())
				{
					if (eq.modifiedName().endsWith("Secondary"))	//all secondary natural weapons attack at BAB -5
						index = -5;
				}
				else if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS || isPrimaryWeapon(eq) || isSecondaryWeapon(eq))
				{
					//if ((eq.getHand() != Equipment.TWOWEAPON_HANDS) && isSecondaryWeapon(eq) && primaryWeapon != null)
					if ((eq.getHand() != Equipment.TWOWEAPON_HANDS) && isSecondaryWeapon(eq) && !primaryWeapons.isEmpty())
					{
						index = -10;
					}
					else if (isSecondaryWeapon(eq) && primaryWeapons.isEmpty())
					{
						index = -4;
					}
					//else if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS || (secondaryWeapon != null && secondaryWeapon[0] != null))
					else if (isDouble || (eq.getHand() == Equipment.TWOWEAPON_HANDS) || !secondaryWeapons.isEmpty())
					{
						index = -6;
					}

					if (isDouble || (eq.getHand() == Equipment.TWOWEAPON_HANDS && Globals.getWeaponProfNamed(eq.profName()).isLight()) ||
						//(primaryWeapon != null && secondaryWeapon[0] != null && Globals.getWeaponProfNamed(secondaryWeapon[0].profName()).isLight()))
						(!primaryWeapons.isEmpty() && !secondaryWeapons.isEmpty() && Globals.getWeaponProfNamed(((Equipment)secondaryWeapons.get(0)).profName()).isLight()))
						index += 2;
					if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS ||
						//((primaryWeapon != null) && isSecondaryWeapon(eq)))
						(!primaryWeapons.isEmpty() && isSecondaryWeapon(eq)))
						secondaryBonus = getTotalBonusTo("COMBAT", "TOHIT-SECONDARY", true);
					if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS ||
						//(secondaryWeapon[0] != null && isPrimaryWeapon(eq)))
						(!secondaryWeapons.isEmpty() && isPrimaryWeapon(eq)))
						primaryBonus = getTotalBonusTo("COMBAT", "TOHIT-PRIMARY", true);
				}

				WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());

				index += modForSize();	//include the size bonus/penalty since it is no longer added elsewhere

				index += primaryBonus;
				for (Iterator ei = eq.typeArrayList().iterator(); ei.hasNext();)
				{
					final String tString = ei.next().toString();
					index += getTotalBonusTo("TOHIT", "TYPE=" + tString, true);
				}

				//
				// This fixes Weapon Finesse breaking here on thrown weapons
				// BONUS:WEAPONPROF=%LIST|TOHIT|(STRMAXDEX+SHIELDACHECK)-STR|TYPE=NotRanged
				//
				// Dagger yields following:
				// WEAPONPROF=DAGGER.TOHIT.NOTRANGED:n
				//
				if ((wp != null) && eq.isRanged())
				{
//					index -= getTotalBonusTo("WEAPONPROF=" + wp.getName(), "TOHIT.NotRanged", true);
					final String aKey = "WEAPONPROF=" + wp.getName().toUpperCase() + ".TOHIT.NOTRANGED";
					String aBonus = (String)bonusMap.get(aKey);
					if (aBonus != null)
					{
						index -= Integer.parseInt(aBonus);
					}
				}

				if (!isDouble && eq.getHand() != Equipment.TWOWEAPON_HANDS)
					index += secondaryBonus;
				if (!eq.isNatural() && ((wp == null) || !hasWeaponProfNamed(wp.getName())))
				{
					index -= 4; // non-proficiency penalty
				}
				if (wp != null)
				{
					Globals.debugPrint(wp.getName() + " " + getTotalBonusTo("WEAPONPROF=" + wp.getName(), "TOHIT", true));
					index += getTotalBonusTo("WEAPONPROF=" + wp.getName(), "TOHIT", true);
				}
				Integer numInt = new Integer(-1);
				if (aString.startsWith("TOTALHIT") && weapon > -1)
				{
					if (!aString.endsWith("TOTALHIT"))
						numInt = new Integer(aString.substring(8));
				}

				Integer bInt = new Integer(index + weaponMod);
				index += eq.getBonusToHit(true);
				int k = index;

				StringTokenizer zTok = null;
				if (eq.isMelee())
					zTok = new StringTokenizer(mString, "+/", false);
				else if (eq.isRanged())
					zTok = new StringTokenizer(rString, "+/", false);
				int x = 0;
				int max = 1 + getTotalBonusTo("COMBAT", "SECONDARYATTACKS", true);
				int extra_attacks = eq.bonusTo("COMBAT", "ATTACKS");  // BONUS:COMBAT|ATTACKS|* represent extra attacks at BaB
				// such as from a weapon of 'Speed'
				//if (primaryWeapon == null)
				if (primaryWeapons.isEmpty())
					max = 100;
				if (!eq.isAttacksProgress())
					numInt = new Integer(0);

				//
				// Trap this to avoid infinite loop
				//
				if (!eq.isMelee() && !eq.isRanged())
				{
					fa.write(output, "???");
					return;
				}


				StringBuffer primaryAttack = new StringBuffer(20);
				StringBuffer secondaryAttack = new StringBuffer(20);
				do
				{
					index = 0;
					if (isSecondaryWeapon(eq) && x >= max)
						break;
					if (zTok != null)
					{
						if (zTok.hasMoreTokens())
							index = Integer.parseInt(zTok.nextToken());
						else
							break;
					}
					numInt = new Integer(numInt.intValue() - 1);
					//
					// Found the correct attack, then output the attack's "to hit"
					//
					if (numInt.intValue() < 0)
					{
						final int iAtt = bInt.intValue() + index + eq.getBonusToHit(true);
						if (primaryAttack.length() != 0)
						{
							primaryAttack.append('/');
						}
						primaryAttack.append(Delta.toString(iAtt));

						//
						// Here we handle extra attacks provided by the BONUS:COMBAT|ATTACKS|* tag
						// These are at the characters BaB
						//
						while (extra_attacks-- > 0)
						{
							primaryAttack.append("/" + Delta.toString(iAtt));
						}


						if (eq.isNatural())
						{
							break;
						}

//						if (x == 0 && (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS) ||
//							(x < max && eq.getHand() == Equipment.TWOWEAPON_HANDS))
						if ((x < max) && ((isDouble && eq.getHand() == Equipment.BOTH_HANDS) || (eq.getHand() == Equipment.TWOWEAPON_HANDS)))
						{
							if (secondaryAttack.length() != 0)
							{
								secondaryAttack.append('/');
							}
							final int iAtt2 = index - primaryBonus + bInt.intValue() + secondaryBonus - 4 + eq.getBonusToHit(!isDouble);
							secondaryAttack.append(Delta.toString(iAtt2));
						}
					}
					if (numInt.intValue() < -1)
						numInt = new Integer(-1);
					else if (numInt.intValue() == -1)
						numInt = new Integer(-2);
					x++;
					//
					// Just in case we are looping forever
					//
					if (x > 100)
					{
						break;
					}

				} while (numInt.intValue() >= -1);
				fa.write(output, primaryAttack.toString());
				if (secondaryAttack.length() != 0)
				{
					fa.write(output, ";" + secondaryAttack.toString());
				}

				//if (weapon == -1 && primaryWeapon.equals(secondaryWeapon[0]))
				if (weapon == -1 && primaryWeapons.get(0).equals(secondaryWeapons.get(0)))
				{
					if (aString.equals("TOTALHIT"))
					{
						StringTokenizer bTok = null;
						if (eq.isMelee())
							bTok = new StringTokenizer(mString, "/", false);
						else if (eq.isRanged())
							bTok = new StringTokenizer(rString, "/", false);
						if (bTok != null)
							k += Integer.parseInt(bTok.nextToken());
					}
					fa.write(output, "/" + Delta.toString(k));
				}
			}
			else if (aString.startsWith("CATEGORY"))
			{
				if (eq.isSimple())
					fa.write(output, "Simple");
				else if (eq.isMartial())
					fa.write(output, "Martial");
				else if (eq.isExotic())
					fa.write(output, "Exotic");
				else if (eq.isNatural())
					fa.write(output, "Natural");
				else
					fa.write(output, "Non-Standard");
				fa.write(output, "-");
				if (eq.isMelee())
					fa.write(output, "Melee");
				else if (eq.isRanged())
					fa.write(output, "Ranged");
				else
					fa.write(output, "Non-Standard");
			}
			else if (aString.startsWith("HAND"))
				fa.write(output, Equipment.getHandName(eq.getHand()));
			else if (aString.startsWith("MAGICDAMAGE"))
			{
//				final int magicdamage = eq.bonusTo("WEAPON", "DAMAGE") + eq.bonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE");
				final int magicdamage = eq.getBonusToDamage(true) + eq.bonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE");
				fa.write(output, Delta.toString(magicdamage));
			}
			else if (aString.startsWith("MAGICHIT"))
			{
//				final int magichit = eq.bonusTo("WEAPON", "TOHIT") + eq.bonusTo("WEAPONPROF=" + eq.profName(), "TOHIT");
				final int magichit = eq.getBonusToHit(true) + eq.bonusTo("WEAPONPROF=" + eq.profName(), "TOHIT");
				fa.write(output, Delta.toString(magichit));
			}
			else if (aString.startsWith("FEAT"))
			{
				final int featBonus = getFeatBonusTo("WEAPON", "TOHIT", true) + getFeatBonusTo("WEAPONPROF=" + eq.profName(), "TOHIT", true);
				fa.write(output, Delta.toString(featBonus));
			}
			else if (aString.startsWith("TEMPLATE"))
			{
				final int featBonus = getTemplateBonusTo("WEAPON", "TOHIT", true) + getTemplateBonusTo("WEAPONPROF=" + eq.profName(), "TOHIT", true);
				fa.write(output, Delta.toString(featBonus));
			}
			else if (aString.endsWith("DAMAGE"))
			{
				String bString = new String(eq.getDamage());
				int bonus = 0;
				final int strMod = calcStatMod(Constants.STRENGTH);
				int weaponProfBonus=0, eqbonus=0;
				if (eq.isMonk() && eq.isUnarmed())
				{
					String cString = getUnarmedDamageString(false, false);
					StringTokenizer bTok = new StringTokenizer(bString, " d+-", false);
					bTok.nextToken();
					String b1String = bTok.nextToken();
					bTok = new StringTokenizer(cString, " d+-", false);
					bTok.nextToken();
					String c1String = bTok.nextToken();
					if (Integer.parseInt(b1String) < Integer.parseInt(c1String))
						bString = cString;
				}
				Integer bInt = new Integer(0);
				if (!aString.startsWith("BASE"))
				{
					for (index = 0; index < bString.length(); index++)
					{
						if (bString.charAt(index) == '+')
						{
							bInt = new Integer(bString.substring(index + 1));
							break;
						}
						else if (bString.charAt(index) == '-')
						{
							bInt = new Integer(bString.substring(index));
							break;
						}
					}

					if (eq.isMelee() || eq.isThrown())
					{
						if (isSecondaryWeapon(eq) && (primaryWeapons.indexOf(eq) == -1) && (strMod > 0))
						{
							bInt = new Integer(bInt.intValue() + doOffhandMod(strMod));
						}
						else
						{
							bInt = new Integer(bInt.intValue() + strMod);
						}
					}
					eqbonus = eq.getBonusToDamage(true);
					for (Iterator ei = eq.typeArrayList().iterator(); ei.hasNext();)
						bonus += getTotalBonusTo("DAMAGE", "TYPE=" + ei.next().toString(), true);
					WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
					if (!isDouble && eq.isMelee() && (wp != null) && (strMod > 0) && (eq.getHand() == Equipment.BOTH_HANDS))
					{
						if (wp.isOneHanded() && !wp.isLight())
							bonus += doOffhandMod(strMod);
						if (wp.isTwoHanded())
							bonus += doOffhandMod(strMod);
					}
					if (wp != null && (strMod > 0) && eq.isMelee())
					{
						if (eq.isNatural() && eq.isOnlyNaturalWeapon() && eq.modifiedName().endsWith("Primary"))
						{
							bonus += doOffhandMod(strMod);
						}
					}
					weaponProfBonus = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE", true);
					bInt = new Integer(bInt.intValue() + bonus + weaponProfBonus + eqbonus);
					bString = bString.substring(0, index);
				}
				if (!bString.equalsIgnoreCase("0d0"))
				{
					fa.write(output, bString);
					if (bInt.intValue() != 0)
					{
						fa.write(output, Delta.toString(bInt));
					}
				}
				else
				{
					fa.write(output, "0");
				}

				if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS)
				{
					bInt = new Integer(bInt.intValue()-eqbonus);
					// eq.getBonusToDamage(false) returns the eq bonus for the secondary head,
					// which for Double weapons is the right thing to do here, but for two-weapons
					// mode we still want to use the primary eqbonus (which is already set properly)
					if (isDouble)
						eqbonus = eq.getBonusToDamage(false);
					if (isDouble && eq.getAltDamage().length() > 0)
					{
						bInt = new Integer(0);
						bString = new String(eq.getAltDamage());
						if (bString.lastIndexOf("-") > -1)
						{
							bInt = new Integer(bString.substring(bString.lastIndexOf("-")));
							bString = bString.substring(0, bString.lastIndexOf("-"));
						}
						else if (bString.lastIndexOf("+") > -1)
						{
							bInt = new Integer(bString.substring(bString.lastIndexOf("+") + 1));
							bString = bString.substring(0, bString.lastIndexOf("+"));
						}
					}
					else
					{
						weaponProfBonus=0;
						bonus=0;
						if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
						{
							bInt = new Integer(bInt.intValue() - strMod);
						}
					}

					if (strMod > 0)
					{
						bonus += doOffhandMod(strMod);	// only get half strength bonus
					}
					else
					{
						bonus += strMod;
					}
					bInt = new Integer(bInt.intValue() + bonus + weaponProfBonus + eqbonus);
					fa.write(output, "/");
					if (!bString.equalsIgnoreCase("0d0"))
					{
						fa.write(output, bString);
						if (bInt.intValue() > 0)
							fa.write(output, "+");
						if (bInt.intValue() != 0)
							fa.write(output, bInt.toString());
					}
					else
					{
						fa.write(output, "0");
					}
				}
			}
			else if (aString.startsWith("SIZE"))
			{
				fa.write(output, eq.getSize());
			}
			else if (aString.startsWith("SPROP"))
			{
				fa.encodeWrite(output, eq.getSpecialProperties());
			}
			else if (aString.startsWith("REACH"))
			{
				fa.write(output, "" + race.getReach() + eq.getReach());
			}
		}
		else if (existsOnly)
			noMoreItems = true;
	}

	private int replaceTokenStat(String aString, FileAccess fa, BufferedWriter output)
	{
		int len;
		int i = (int)aString.charAt(4) - 48;
		boolean x = aString.length() > 5;
		len = 1;
		if (x == false)
		{
			if (isNonability(i))
				fa.write(output, "--");
			else
				fa.write(output, new Integer(adjStats(i)).toString());
		}
		else
		{
			if (isNonability(i))
				fa.write(output, "0");
			else
			{
				int temp = calcStatMod(i);
				fa.write(output, Delta.toString(temp));
			}
		}
		return len;
	}


	private void _writeArmorProperty(Equipment eq, String property, FileAccess fa, BufferedWriter output)
	{
		if (property.startsWith("NAME"))
		{
			if (eq.isEquipped())
				fa.write(output, "*");
			fa.encodeWrite(output, eq.getName());
		}
		else if (property.startsWith("TOTALAC"))
		{
			// adjustments for new equipment modifier
			// EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
//  			fa.write(output, Delta.toString(eq.getAcMod()));
			fa.write(output, Delta.toString(eq.bonusTo("COMBAT", "AC", true)) + "");
		}
		else if (property.startsWith("BASEAC"))
		{
			// adjustments for new equipment modifier
			// EQMARMOR|AC|x|TYPE=ENHANCEMENT changed to COMBAT|AC|x|TYPE=Armor.ENHANCEMENT
//  			fa.write(output, Delta.toString(eq.getAcMod()));
			fa.write(output, Delta.toString(eq.bonusTo("COMBAT", "AC")) + "");
		}
		else if (property.startsWith("ACBONUS"))
		{
			fa.write(output, Delta.toString(eq.bonusTo("COMBAT", "AC", true)) + "");
		}
		else if (property.startsWith("MAXDEX"))
		{
			int iMax = eq.getMaxDex().intValue();
			if (iMax != 100)
			{
				fa.write(output, Delta.toString(iMax));
			}
		}
		else if (property.startsWith("ACCHECK"))
		{
			fa.write(output, Delta.toString(eq.acCheck()));
		}
		else if (property.startsWith("SPELLFAIL"))
		{
			fa.write(output, eq.spellFailure().toString());
		}
		else if (property.startsWith("MOVE"))
		{
			StringTokenizer aTok = new StringTokenizer(eq.moveString(), ",", false);
			String tempString = "";
			if ((getSize().equals("M") || getSize().equals("S")) &&
				aTok.countTokens() > 0)
			{
				tempString = aTok.nextToken();
				if (getSize().equals("S") && aTok.countTokens() > 1)
					tempString = aTok.nextToken();
			}
			fa.write(output, tempString);
		}
		else if (property.startsWith("SPROP"))
		{
			fa.encodeWrite(output, eq.getSpecialProperties());
		}
		else if (property.startsWith("TYPE"))
		{
			String typeString = "";
			if (eq.isLight())
			{
				typeString = "Light";
			}
			else if (eq.isMedium())
			{
				typeString = "Medium";
			}
			else if (eq.isHeavy())
			{
				typeString = "Heavy";
			}
			else if (eq.isShield())
			{
				typeString = "Shield";
			}
			else if (eq.isExtra())
			{
				typeString = "Extra";
			}
			fa.write(output, typeString);
		}
		else if (property.startsWith("WT"))
		{
			fa.write(output, eq.getWeight() + "");
		}
	}

	/**
	 * select various stuff, that improves AC
	 */
	private int _replaceTokenArmorVarious(int index, String type, String subtype, String property, int equipped,
		int len, FileAccess fa, BufferedWriter output)
	{
		Equipment eq;
		ArrayList aArrayList = new ArrayList();
		for (Iterator mapIter = getEquipmentOfType(type, subtype, equipped).iterator();
				 mapIter.hasNext();)
		{
			eq = (Equipment)mapIter.next();
			if (eq.getAcMod().intValue() > 0)
			{
				aArrayList.add(eq);
			}
			else
			{

				for (Iterator it = eq.getBonusList().iterator(); it.hasNext();)
				{
					if (((String)it.next()).indexOf("|AC|") != -1)
					{
						aArrayList.add(eq);
						break;
					}
				}
			}
		}

		if (index >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (index < aArrayList.size())
		{
			eq = (Equipment)aArrayList.get(index);
			_writeArmorProperty(eq, property, fa, output);
		}
		return len;
	}

	/**
	 * select items, which improve AC but are not type ARMOR
	 */
	private int _replaceTokenArmorItem(int item, String subtype, String property, int equipped,
		int len, FileAccess fa, BufferedWriter output)
	{

		// select all pieces of equipment of status==equipped
		// filter all AC relevant stuff
		ArrayList aArrayList = new ArrayList();
		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();

			if ((subtype.equals("") || eq.isType(subtype)) &&
				((equipped == 3) ||
				(equipped == 2 && !eq.isEquipped()) ||
				(equipped == 1 && eq.isEquipped())))
			{
				for (Iterator it = eq.getBonusList().iterator(); it.hasNext();)
				{
					if ((((String)it.next()).indexOf("|AC|") != -1) &&
						(!eq.isType("ARMOR") && !eq.isType("SHIELD")))
					{
						aArrayList.add(eq);
					}
				}
			}
		}

		if (item >= aArrayList.size())
		{
			len = 0;
			noMoreItems = true;
		}
		if (item < aArrayList.size())
		{
			Equipment eq = (Equipment)aArrayList.get(item);
			_writeArmorProperty(eq, property, fa, output);
		}

		return len;
	}

	/**
	 * select shields
	 */
	private int _replaceTokenArmorShield(int shield, String subtype, String property, int equipped,
		int len, FileAccess fa, BufferedWriter output)
	{
		ArrayList aArrayList = getEquipmentOfType("Shield", subtype, equipped);
		if (shield >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (shield < aArrayList.size())
		{
			Equipment eq = (Equipment)aArrayList.get(shield);
			_writeArmorProperty(eq, property, fa, output);
		}
		return len;
	}

	/**
	 * select shirts
	 */
	private int _replaceTokenArmorShirt(int shirt, String subtype, String property, int equipped,
		int len, FileAccess fa, BufferedWriter output)
	{
		ArrayList aArrayList = getEquipmentOfType("Shirt", subtype, equipped);
		if (shirt >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (shirt < aArrayList.size())
		{
			Equipment eq = (Equipment)aArrayList.get(shirt);
			_writeArmorProperty(eq, property, fa, output);
		}
		return len;
	}

	/**
	 * select suits
	 */
	private int _replaceTokenArmorSuit(int suit, String subtype, String property, int equipped,
		int len, FileAccess fa, BufferedWriter output)
	{
		ArrayList aArrayList = getEquipmentOfType("Suit", subtype, equipped);
		//
		// Temporary hack until someone gets around to fixing it properly
		//
//  		aArrayList.addAll(getEquipmentOfType("Shirt", subtype, equipped));
		if (suit >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (suit < aArrayList.size())
		{
			Equipment eq = (Equipment)aArrayList.get(suit);
			_writeArmorProperty(eq, property, fa, output);
		}
		return len;
	}

	/**
	 * select suits + shields
	 */
	private int _replaceTokenArmor(int armor, String property, int equipped,
		int len, FileAccess fa, BufferedWriter output)
	{
		ArrayList aArrayList = getEquipmentOfType("Armor", equipped);
		ArrayList bArrayList = getEquipmentOfType("Shield", equipped);
		for (Iterator e = bArrayList.iterator(); e.hasNext();)
			aArrayList.add(e.next());

		if (armor >= aArrayList.size() - 1 && existsOnly)
		{
			len = 0;
			noMoreItems = true;
		}
		if (armor < aArrayList.size())
		{
			Equipment eq = (Equipment)aArrayList.get(armor);
			_writeArmorProperty(eq, property, fa, output);
		}
		return len;
	}

	/**
	 * select armor related equipment
	 * possible tokens are:
	 *
	 * ARMORx.property
	 * ARMOR.ALLx.property
	 * ARMOR.EQUIPPEDx.property
	 * ARMOR.NOT_EQUIPPEDx.property
	 * ARMOR.SUIT.ALLx.property
	 * ARMOR.SUIT.EQUIPPEDx.property
	 * ARMOR.SUIT.NOT_EQUIPPEDx.property
	 * ARMOR.SUIT.subtype.ALLx.property
	 * ARMOR.SUIT.subtype.EQUIPPEDx.property
	 * ARMOR.SUIT.subtype.NOT_EQUIPPEDx.property
	 * ARMOR.SHIRT.ALLx.property
	 * ARMOR.SHIRT.EQUIPPEDx.property
	 * ARMOR.SHIRT.NOT_EQUIPPEDx.property
	 * ARMOR.SHIRT.subtype.ALLx.property
	 * ARMOR.SHIRT.subtype.EQUIPPEDx.property
	 * ARMOR.SHIRT.subtype.NOT_EQUIPPEDx.property
	 * ARMOR.SHIELD.ALLx.property
	 * ARMOR.SHIELD.EQUIPPEDx.property
	 * ARMOR.SHIELD.NOT_EQUIPPEDx.property
	 * ARMOR.SHIELD.subtype.ALLx.property
	 * ARMOR.SHIELD.subtype.EQUIPPEDx.property
	 * ARMOR.SHIELD.subtype.NOT_EQUIPPEDx.property
	 * ARMOR.ITEM.ALLx.property
	 * ARMOR.ITEM.EQUIPPEDx.property
	 * ARMOR.ITEM.NOT_EQUIPPEDx.property
	 * ARMOR.ITEM.subtype.ALLx.property
	 * ARMOR.ITEM.subtype.EQUIPPEDx.property
	 * ARMOR.ITEM.subtype.NOT_EQUIPPEDx.property
	 * ARMOR.type.ALLx.property
	 * ARMOR.type.EQUIPPEDx.property
	 * ARMOR.type.NOT_EQUIPPEDx.property
	 * ARMOR.type.subtype.ALLx.property
	 * ARMOR.type.subtype.EQUIPPEDx.property
	 * ARMOR.type.subtype.NOT_EQUIPPEDx.property
	 */
	private int replaceTokenArmor(String aString, int len, FileAccess fa, BufferedWriter output)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ".");
		String tokens[] = new String[aTok.countTokens()];
		for (int i = 0; aTok.hasMoreTokens(); i++)
		{
			tokens[i] = aTok.nextToken();
		}

		String property = null;
		if (tokens.length > 0)
			property = tokens[tokens.length - 1];
		String subtype = "";

		int equipped = 3;
		int index = 0;

		/**
		 * ARMORx.property
		 */
		if (tokens.length == 2)
		{
			index = Integer.parseInt(tokens[0].substring(5));
			return _replaceTokenArmor(index, property, 3, len, fa, output);
		}
		/**
		 * ARMOR.ALLx.property
		 * ARMOR.EQUIPPEDx.property
		 * ARMOR.NOT_EQUIPPEDx.property
		 */
		else if (tokens.length == 3)
		{
			if (tokens[1].startsWith("ALL"))
			{
				index = Integer.parseInt(tokens[1].substring(3));
				equipped = 3;
			}
			else if (tokens[1].startsWith("EQUIPPED"))
			{
				index = Integer.parseInt(tokens[1].substring(8));
				equipped = 1;
			}
			else if (tokens[1].startsWith("NOT_EQUIPPED"))
			{
				index = Integer.parseInt(tokens[1].substring(12));
				equipped = 2;
			}
			return _replaceTokenArmor(index, tokens[2], equipped, len, fa, output);
		}
		else if ((tokens.length == 4) || (tokens.length == 5))
		{

			if (tokens[tokens.length - 2].startsWith("ALL"))
			{
				index = Integer.parseInt(tokens[tokens.length - 2].substring(3));
				equipped = 3;
			}
			else if (tokens[tokens.length - 2].startsWith("EQUIPPED"))
			{
				index = Integer.parseInt(tokens[tokens.length - 2].substring(8));
				equipped = 1;
			}
			else if (tokens[tokens.length - 2].startsWith("NOT_EQUIPPED"))
			{
				index = Integer.parseInt(tokens[tokens.length - 2].substring(12));
				equipped = 2;
			}

			if (tokens.length == 5)
			{
				subtype = tokens[2];
			}

			/**
			 * ARMOR.SUIT.ALLx.property
			 * ARMOR.SUIT.EQUIPPEDx.property
			 * ARMOR.SUIT.NOT_EQUIPPEDx.property
			 * ARMOR.SUIT.subtype.ALLx.property
			 * ARMOR.SUIT.subtype.EQUIPPEDx.property
			 * ARMOR.SUIT.subtype.NOT_EQUIPPEDx.property
			 */
			if (tokens[1].equals("SUIT"))
			{
				return _replaceTokenArmorSuit(index, subtype, property,
					equipped, len, fa, output);
			}
			/**
			 * ARMOR.SHIRT.ALLx.property
			 * ARMOR.SHIRT.EQUIPPEDx.property
			 * ARMOR.SHIRT.NOT_EQUIPPEDx.property
			 * ARMOR.SHIRT.subtype.ALLx.property
			 * ARMOR.SHIRT.subtype.EQUIPPEDx.property
			 * ARMOR.SHIRT.subtype.NOT_EQUIPPEDx.property
			 */
			if (tokens[1].equals("SHIRT"))
			{
				return _replaceTokenArmorShirt(index, subtype, property,
					equipped, len, fa, output);
			}
			/**
			 * ARMOR.SHIELD.ALLx.property
			 * ARMOR.SHIELD.EQUIPPEDx.property
			 * ARMOR.SHIELD.NOT_EQUIPPEDx.property
			 * ARMOR.SHIELD.subtype.ALLx.property
			 * ARMOR.SHIELD.subtype.EQUIPPEDx.property
			 * ARMOR.SHIELD.subtype.NOT_EQUIPPEDx.property
			 */
			else if (tokens[1].equals("SHIELD"))
			{
				return _replaceTokenArmorShield(index, subtype, property,
					equipped, len, fa, output);
			}
			/**
			 * ARMOR.ITEM.ALLx.property
			 * ARMOR.ITEM.EQUIPPEDx.property
			 * ARMOR.ITEM.NOT_EQUIPPEDx.property
			 * ARMOR.ITEM.subtype.ALLx.property
			 * ARMOR.ITEM.subtype.EQUIPPEDx.property
			 * ARMOR.ITEM.subtype.NOT_EQUIPPEDx.property
			 */
			else if (tokens[1].equals("ITEM"))
			{
				return _replaceTokenArmorItem(index, subtype, property,
					equipped, len, fa, output);
			}
			/**
			 * ARMOR.type.ALLx.property
			 * ARMOR.type.EQUIPPEDx.property
			 * ARMOR.type.NOT_EQUIPPEDx.property
			 * ARMOR.type.subtype.ALLx.property
			 * ARMOR.type.subtype.EQUIPPEDx.property
			 * ARMOR.type.subtype.NOT_EQUIPPEDx.property
			 */
			else
			{
				return _replaceTokenArmorVarious(index, tokens[1], subtype, property,
					equipped, len, fa, output);
			}
		}

		return 0;
	}

	private void replaceTokenSpellMem(String aString, FileAccess fa, BufferedWriter output)
	{
		StringTokenizer aTok = new StringTokenizer(aString.substring(8), ".", false);
		int classNum = Integer.parseInt(aTok.nextToken());
		int bookNum = Integer.parseInt(aTok.nextToken());
		int spellLevel = Integer.parseInt(aTok.nextToken());
		int spellNumber = Integer.parseInt(aTok.nextToken());
		String aLabel = "NAME";
		if (aTok.hasMoreTokens())
			aLabel = aTok.nextToken();
		String altLabel = "";
		if (aTok.hasMoreTokens())
			altLabel = aTok.nextToken();
		PCClass aClass = getSpellClassAtIndex(classNum);
		if (aClass == null && existsOnly && classNum != -1)
			noMoreItems = true;

		String bookName = Globals.getDefaultSpellBook();

		if (bookNum >= 0)
			bookName = (String)getSpellBooks().get(bookNum);

		if (aClass != null || classNum == -1)
		{
			int i = 0;
			if (classNum == -1)
				bookName = Globals.getDefaultSpellBook();

			if (!bookName.equals(""))
			{
				Spell aSpell = null;
				boolean moreSpells = false;
				if (classNum == -1)
				{
					if (allSpells.size() == 0)
					{
						for (i = 0; i < getClassList().size(); i++)
						{
							int k = 0;
							for (k = 0; k < ((PCClass)getClassList().get(i)).spellList().size(); k++)
							{
								boolean addIt = true;
								int m = 0;
								for (m = 0; m < allSpells.size(); m++)
									if (((Spell)((PCClass)getClassList().get(i)).spellList().get(k)).getKeyName().equals(((Spell)allSpells.get(m)).getKeyName()))
									{
										addIt = false;
										break;
									}

								if (addIt)
									allSpells.add((Spell)((PCClass)getClassList().get(i)).spellList().get(k));
							}

						}
						Globals.sortPObjectList(allSpells);
					}
					if (spellNumber < allSpells.size())
					{
						aSpell = (Spell)allSpells.get(spellNumber);
						spellNumber = -1;
					}
//					System.out.println(Spells.size());

				}
				else
				{
					final int spellListSize = (aClass != null) ? aClass.spellList().size() : 0;
					if (aClass != null) /* To keep jlint happy */
						for (i = 0; i < spellListSize; i++)
						{
							aSpell = (Spell)aClass.spellList().get(i);
							String classString = aClass.getKeyName();
							if (aClass.getCastAs().length() > 0)
								classString = aClass.getCastAs();
							if (aSpell.getSpellBooks().contains(bookName))
							{
								// A level of -1 returns a list of all spells for that class
								if (spellLevel == -1)
									spellNumber--;
								else
								{
									String levelString = aSpell.levelForClass(classString, aClass.getName());
									StringTokenizer sTok = new StringTokenizer(levelString, ",", false);
									while (sTok.hasMoreTokens())
									{
										sTok.nextToken();
										int aLevel = Integer.parseInt(sTok.nextToken());
										if (aLevel >= spellLevel)
											moreSpells = true;
										if (aLevel == spellLevel)
										{
											spellNumber--;
											break;
										}
									}
								}
							}
							if (spellNumber == -1)
								break;
						}
				}
				if (inLabel && moreSpells == false && checkBefore)
					canWrite = false;
				if (spellNumber == -1 && aSpell != null)
				{
					Spell bSpell = (Spell)Globals.getSpellMap().get(aSpell.getKeyName());
					if (aLabel.equals("NAME"))
						fa.encodeWrite(output, aSpell.getName());
					else if (aLabel.equals("TIMES"))
						fa.write(output, aSpell.timesForSpellBook(bookName).toString());
					else if (bSpell != null)
					{
						if (aLabel.equals("RANGE"))
							fa.encodeWrite(output, bSpell.getRange());
						else if (aLabel.equals("COMPONENTS"))
							fa.encodeWrite(output, bSpell.getComponentList());
						else if (aLabel.equals("CASTINGTIME"))
							fa.encodeWrite(output, bSpell.getCastingTime());
						else if (aLabel.equals("DURATION"))
							fa.encodeWrite(output, bSpell.getDuration());
						else if (aLabel.equals("EFFECT"))
							fa.encodeWrite(output, bSpell.getEffect());
						else if (aLabel.equals("EFFECTTYPE"))
							fa.encodeWrite(output, bSpell.getEffectType());
						else if (aLabel.equals("SAVEINFO"))
							fa.encodeWrite(output, bSpell.getSaveInfo());
						else if (aLabel.equals("SCHOOL"))
							fa.encodeWrite(output, bSpell.getSchool());
						else if (aLabel.equals("SOURCE"))
							fa.encodeWrite(output, bSpell.getSource());
						else if (aLabel.equals("SOURCESHORT"))
							fa.encodeWrite(output, bSpell.getSourceShort());
						else if (aLabel.equals("SOURCEPAGE"))
							fa.encodeWrite(output, bSpell.getSourcePage());
						else if (aLabel.equals("SUBSCHOOL"))
							fa.encodeWrite(output, bSpell.getSubschool());
						else if (aLabel.equals("DESCRIPTOR"))
							fa.encodeWrite(output, bSpell.descriptor());
						else if (aLabel.equals("FULLSCHOOL"))
						{
							String aTemp = bSpell.getSchool();
							if ((bSpell.getSubschool().length() > 0) && (!bSpell.getSubschool().trim().toUpperCase().equals("NONE")))
								aTemp += " (" + bSpell.getSubschool() + ")";
							if (bSpell.descriptor().length() > 0)
								aTemp += " [" + bSpell.descriptor() + "]";
							fa.encodeWrite(output, aTemp);
						}
						else if (aLabel.equals("SR"))
							fa.encodeWrite(output, bSpell.getSR());
						else if (aLabel.startsWith("DESCRIPTION"))
						{
							// System.out.println(altLabel);
							String sString = getItemDescription("SPELL", aSpell.getName(), bSpell.getEffect());
							if (altLabel.length() > 0)
								replaceWithDelimiter(fa, output, sString, altLabel);
							else
								fa.encodeWrite(output, sString);
						}
						else if (aLabel.startsWith("BONUSSPELL"))
						{
							ArrayList dList = new ArrayList();
							String sString = "*";
							if (aLabel.length() > 10)
								sString = aLabel.substring(10);
							if (aClass != null && bSpell.isInSpecialty(aClass.getSpecialtyList(), aClass.getName(), spellLevel))
							{
								for (Iterator ip = getCharacterDomainList().iterator(); ip.hasNext();)
								{
									CharacterDomain aCD = (CharacterDomain)ip.next();
									if (aCD != null && aCD.getDomain() != null && aCD.getDomainSource().startsWith("PCClass|" + aClass.getName()))
										dList.add(aCD.getDomain().getName());
								}
								String pString = bSpell.getDomainString(" [", ",", "]", spellLevel, dList);
								if (pString.length() > 0)
									sString = pString;
								fa.write(output, sString);
							}
							else
								fa.write(output, altLabel);
						}
					}
				}
				else if (existsOnly)
					noMoreItems = true;
			}
			else if (existsOnly)
				noMoreItems = true;
		}
	}

	private int replaceTokenSkill(String aString, int len, FileAccess fa, BufferedWriter output)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ".");
		String fString = aTok.nextToken();
		Skill aSkill = null;

		if (fString.startsWith("SKILLSUBSET"))	// Matches skills known which start with the second dotted field ie. SKILLSUBSET%.Read Language.NAME
		{
			int i = Integer.parseInt(fString.substring(11));
			fString = aTok.nextToken();
			ArrayList skillSubset = new ArrayList();

			for (Iterator iter = skillList.iterator(); iter.hasNext();)
			{
				Skill bSkill = (Skill)iter.next();
				if (bSkill.getName().toUpperCase().startsWith(fString.toUpperCase()))
					skillSubset.add(bSkill);
			}

			if (i >= skillSubset.size() - 1 && existsOnly)
				noMoreItems = true;
			if (i > skillSubset.size() - 1)
				len = 0;
			else
				aSkill = (Skill)skillSubset.get(i);

		}
		if (fString.startsWith("SKILLTYPE"))	// Matches skills known which start with the second dotted field ie. SKILLSUBSET%.Read Language.NAME
		{
			final int i = Integer.parseInt(fString.substring(9));
			fString = aTok.nextToken();
			ArrayList skillSubset = new ArrayList();

			for (Iterator iter = skillList.iterator(); iter.hasNext();)
			{
				final Skill bSkill = (Skill)iter.next();
				if (bSkill.isType(fString))
					skillSubset.add(bSkill);
			}

			if (i >= skillSubset.size() - 1 && existsOnly)
				noMoreItems = true;
			if (i > skillSubset.size() - 1)
				len = 0;
			else
				aSkill = (Skill)skillSubset.get(i);

		}
		else if (fString.length() > 5)
		{
			final int i = Integer.parseInt(fString.substring(5));
			if (i >= getSkillList().size() - 1 && existsOnly)
				noMoreItems = true;
			if (i > getSkillList().size() - 1)
				len = 0;
			else
				aSkill = (Skill)getSkillList().get(i);
		}
		else
		{
			fString = aTok.nextToken();
			aSkill = this.getSkillNamed(fString);
			if (aSkill == null)
				aSkill = Globals.getSkillNamed(fString);
		}

		int cmp = 0;
		if (aString.endsWith(".TOTAL"))
			cmp = 1;
		else if (aString.endsWith(".RANK"))
			cmp = 2;
		else if (aString.endsWith(".MOD"))
			cmp = 3;
		else if (aString.endsWith(".ABILITY"))
			cmp = 4;
		else if (aString.endsWith(".ABMOD"))
			cmp = 5;
		else if (aString.endsWith(".MISC"))
			cmp = 6;
		else if (aString.endsWith(".UNTRAINED"))
			cmp = 7;
		else if (aString.endsWith(".EXCLUSIVE"))
			cmp = 8;
		if (aSkill != null)
		{
			if ((cmp == 5 || cmp == 6) && aSkill.keyStat().equalsIgnoreCase(Constants.s_NONE))
				fa.write(output, "n/a");
			else
				switch (cmp)
				{
					case 0:
						fa.write(output, aSkill.qualifiedName());
						return len;
					case 1:
						fa.write(output, new Integer(aSkill.getTotalRank().intValue() + aSkill.modifier().intValue()).toString());
						return len;
					case 2:
						fa.write(output, aSkill.getTotalRank().toString());
						return len;
					case 3:
						fa.write(output, aSkill.modifier().toString());
						return len;
					case 4:
						fa.write(output, aSkill.keyStat());
						return len;
					case 5:
						fa.write(output, new Integer(calcStatMod(aSkill.statIndex(aSkill.keyStat()))).toString());
						return len;
					case 6:
						fa.write(output, new Integer(aSkill.modifier().intValue() - calcStatMod(aSkill.statIndex(aSkill.keyStat()))).toString());
						return len;
					case 7:
						fa.write(output, aSkill.untrained());
						return len;
					case 8:
						fa.write(output, aSkill.isExclusive());
						return len;
				}
		}
		return len;
	}

	private int replaceTokenSpellListBook(String aString)
	{
		final int dot = aString.lastIndexOf(".");
		int classNum = Integer.parseInt(aString.substring(14, dot));
		int levelNum = Integer.parseInt(aString.substring(dot + 1));
		canWrite = false;

		//	Change by OdGregg 2001-08-06
		//
		//	if (classNum < 0 || classNum >= classList.size())
		//		return 0;
		//	PCClass aClass = (PCClass)classList.get(classNum);

		//	Line added
		PCClass aClass = getSpellClassAtIndex(classNum);

		if (aClass != null)
		{
			String bString = aClass.getKeyName();
			if (aClass.getCastAs().length() > 0)
				bString = aClass.getCastAs();
			Spell aSpell = null;
			for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
			{
				aSpell = (Spell)e1.next();
				if (aSpell.levelForClass(bString, aClass.getName()).lastIndexOf("," + String.valueOf(levelNum)) > -1)
				{
					canWrite = true;
					break;
				}
			}
		}
		return 0;
	}

	private int countSpellListBook(String aString)
	{
		final int dot = aString.lastIndexOf(".");
//		System.out.println(aString);
//		System.out.println(aString.substring(17, dot));
//		System.out.println(aString.substring(dot + 1, aString.length() - 1));
		int spellCount = 0;
		if (dot < 0)
		{
			int i = 0;
			for (i = 0; i < getClassList().size(); i++)
			{
				int k = 0;
				for (k = 0; k < ((PCClass)getClassList().get(i)).spellList().size(); k++)
				{
					boolean addIt = true;
					int m = 0;
					for (m = 0; m < allSpells.size(); m++)
						if (((Spell)((PCClass)getClassList().get(i)).spellList().get(k)).getKeyName().equals(((Spell)allSpells.get(m)).getKeyName()))
						{
							addIt = false;
							break;
						}

					if (addIt)
						allSpells.add((Spell)((PCClass)getClassList().get(i)).spellList().get(k));
				}

			}
			Globals.sortPObjectList(allSpells);
			spellCount = allSpells.size();

		}
		else
		{
			int classNum = Integer.parseInt(aString.substring(17, dot));
			int levelNum = Integer.parseInt(aString.substring(dot + 1, aString.length() - 1));

			PCClass aClass = getSpellClassAtIndex(classNum);
			if (aClass != null)
			{
				String bString = aClass.getKeyName();
				if (aClass.getCastAs().length() > 0)
					bString = aClass.getCastAs();
				Spell aSpell = null;
				for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
				{
					aSpell = (Spell)e1.next();
					if (aSpell.levelForClass(bString, aClass.getName()).lastIndexOf("," + String.valueOf(levelNum)) > -1)
					{
						spellCount++;
					}
				}
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
		String bookName = (String)getSpellBooks().get(sbookNum);
		PCClass aClass = getSpellClassAtIndex(classNum);
		if (aClass != null)
		{
			String bString = aClass.getKeyName();
			if (aClass.getCastAs().length() > 0)
				bString = aClass.getCastAs();
			Spell aSpell = null;
			for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
			{
				aSpell = (Spell)e1.next();
				if ((aSpell.levelForClass(bString, aClass.getName()).lastIndexOf("," + String.valueOf(levelNum)) > -1) && aSpell.getSpellBooks().contains(bookName))
				{
					spellCount++;
				}
			}
		}
		return spellCount;
	}


	private void replaceTokenAlignmentShort(FileAccess fa, BufferedWriter output)
	{
		final String alString = Constants.s_ALIGNSHORT[alignment];
		fa.write(output, alString);
	}

	private void replaceTokenAlignment(FileAccess fa, BufferedWriter output)
	{
		final String alString = Constants.s_ALIGNLONG[alignment];
		fa.write(output, alString);
	}

	private void printFeat(int numberPos, String aString, ArrayList anArrayList, FileAccess fa, BufferedWriter output)
	{
		int len = anArrayList.size();
		int j = aString.lastIndexOf(".");
		String sDelim = "\r\n";
		if (aString.indexOf(".DESCRIPTION.") > -1)
		{
			j = aString.lastIndexOf(".DESCRIPTION");
			sDelim = aString.substring(aString.indexOf(".DESCRIPTION.") + 13);
		}
		int i = -1;
		if (j == -1)
			i = Integer.parseInt(aString.substring(numberPos));
		else
			i = Integer.parseInt(aString.substring(numberPos, j));
		if (len <= i && existsOnly)
			noMoreItems = true;
		Globals.sortPObjectList(anArrayList);
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (i == 0 && (aFeat.isVisible() == 1 || aFeat.isVisible() == 2))
			{
				if (aString.endsWith(".DESC"))
					fa.encodeWrite(output, aFeat.getDescription());
				else if (aString.indexOf(".DESCRIPTION") > -1)
					replaceWithDelimiter(fa, output, getItemDescription("FEAT", aFeat.getName(), aFeat.getDescription()), sDelim);
				else
					fa.encodeWrite(output, aFeat.qualifiedName());
			}
			i--;
		}
	}

	private void printFeatList(String delim, ArrayList aArrayList, FileAccess fa, BufferedWriter output)
	{
		if (delim.equals(""))
			delim = ",";
		int i = 0;
		Globals.sortPObjectList(aArrayList);
		Feat aFeat = null;
		for (Iterator e = aArrayList.iterator(); e.hasNext();)
		{
			if (i > 0 && (aFeat.isVisible() == 1 || aFeat.isVisible() == 2))
				fa.write(output, delim);
			aFeat = (Feat)e.next();
			if (aFeat.isVisible() == 1 || aFeat.isVisible() == 2)
				fa.encodeWrite(output, aFeat.qualifiedName());
			i++;
		}
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
					bString = bString + cString;
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
			valString = valString + aString.substring(i, i + 1);
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
				else if (valString.length() == 8 && Globals.getStatFromAbbrev(valString.substring(0, 3)) > -1 && valString.endsWith("SCORE"))
				{
					final int stat = Globals.getStatFromAbbrev(valString.substring(0, 3));
					valString = Integer.toString(adjStats(stat));
					Globals.debugPrint("SCORE=" + valString);
				}
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
					valString = valString.substring(11).replace('{','(').replace('}', ')');
					final PCClass aClass = getClassNamed(valString);
					if (aClass != null)
						valString = aClass.getLevel().toString();
					else
						valString = "0";
				}
				else if (valString.equals("TL"))
					valString = new Integer(totalLevels()).toString();
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
				else if (valString.startsWith("COUNT[SPELLSKNOWN") && valString.endsWith("]"))
				{
					int spellCount = countSpellListBook(valString);
					valString = new Integer(spellCount).toString();
				}
				else if (valString.startsWith("COUNT[SPELLSINBOOK") && valString.endsWith("]"))
				{
					valString = valString.substring(18);
					valString = valString.substring(0,valString.length() - 1);
					int sbookCount = countSpellsInBook(valString);
					valString = new Integer(sbookCount).toString();
				}
				else if (valString.startsWith("COUNT[SPELLBOOKS") && valString.endsWith("]"))
				{
					valString = new Integer(getSpellBooks().size()).toString();
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
					//	valString = Integer.toString(equipmentList.size());  //Needs to not count header children to restore original functionality

					ArrayList aList = new ArrayList();

					for (Iterator locIter = getEquipmentList().values().iterator(); locIter.hasNext();)
					{
						Equipment anEquip = (Equipment)locIter.next();
						if (!anEquip.getHasHeaderParent())
						{
							aList.add(anEquip);
						}
					}
					if (valString.equals("COUNT[EQUIPMENT]"))
						valString = Integer.toString(aList.size());
					else
					{
						StringTokenizer bTok = new StringTokenizer(valString.substring(16, valString.length() - 1), ".", false);
						while (bTok.hasMoreTokens())	//should be ok, assumes last two fields are # and a Param
						{
							String bString = (String)bTok.nextToken();
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
					String aType = (String)bTok.nextToken();
					if (aType.equals("Container"))
					{
						aList.clear();
						for (Iterator locIter = getEquipmentList().values().iterator(); locIter.hasNext();)
						{
							Equipment anEquip = (Equipment)locIter.next();
							if (anEquip.getHasHeaderParent() || anEquip.acceptsChildren())
							{
								aList.add(anEquip);
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
							for (Iterator mapIter = getEquipmentList().values().iterator(); mapIter.hasNext();)
							{
								Equipment eq = (Equipment)mapIter.next();

								for (Iterator it = eq.getBonusList().iterator(); it.hasNext();)
								{
									if ((((String)it.next()).indexOf("|AC|") != -1) && (!eq.isType("ARMOR") && !eq.isType("SHIELD")))
										aList.add(eq);
								}
							}

						}
						// end new code
						else
						{
							aList = this.getEquipmentOfType(aType, 3);
						}
					}

					while (bTok.hasMoreTokens())
					{
						String bString = (String)bTok.nextToken();
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
					{
						aList.clear();
						for (Iterator locIter = getEquipmentList().values().iterator(); locIter.hasNext();)
						{
							Equipment anEquip = (Equipment)locIter.next();
							if (anEquip.getHasHeaderParent() || anEquip.acceptsChildren())
							{
								aList.add(anEquip);
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
					valString = replaceTokenEq(valString);
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
						}
				}
				catch (Exception exc)
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

	/** <code>rollStats</code> roll Globals.s_ATTRIBLONG.length random stats.
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

	final private boolean includeSkill(Skill skill, int level)
	{
		return (level == 2) || skill.isRequired() ||
			(skill.getTotalRank().floatValue() > 0) ||
			((level == 1) && skill.untrained().startsWith("Y"));
	}

	final private void addNewSkills(int level)
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

	final private void removeExcessSkills(int level)
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 08-03-02
	 */
	final public void populateSkills(int level)
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 08-03-02
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 09-03-02
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
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() == false)
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
			bonus = Math.min(bonus, old);
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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 09-03-02
	 */
	public boolean isPrimaryWeapon(Equipment eq)
	{
//return (eq != null && (eq == primaryWeapon || eq.getHand() == Equipment.PRIMARY_HAND || eq.getHand() == Equipment.TWOWEAPON_HANDS));
		if (eq == null)
		{
			return false;
		}
		return primaryWeapons.contains(eq);
	}

	/*
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 09-03-02
	 */
	public boolean isSecondaryWeapon(Equipment eq)
	{
		if (eq == null)
		{
			return false;
		}
		return secondaryWeapons.contains(eq);
//if (eq == null || eq.getHand() != Equipment.SECONDARY_HAND)
//	return false;
//for (int x = 0; x < secondaryWeapon.length; x++)
//	if (eq == secondaryWeapon[x])
//		return true;
//return false;
	}

	private ArrayList getLineForMiscList(int index)
	{
		ArrayList aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer((String)getMiscList().get(index), "\r\n", false);
		while (aTok.hasMoreTokens())
			aArrayList.add(aTok.nextToken());
		return aArrayList;
	}

	private ArrayList getLineForDesc()
	{
		ArrayList aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(description, "\r\n", false);
		while (aTok.hasMoreTokens())
			aArrayList.add(aTok.nextToken());
		return aArrayList;
	}

	private ArrayList getLineForBio()
	{
		ArrayList aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer(bio, "\r\n", false);
		while (aTok.hasMoreTokens())
			aArrayList.add(aTok.nextToken());
		return aArrayList;
	}

	private void saveClassSpecialtyAndSaveLine(Iterator e1, FileAccess fa, BufferedWriter output)
	{
		final PCClass aClass = (PCClass)e1.next();
		fa.write(output, aClass.getKeyName() + ":");
		for (Iterator e2 = aClass.getSpecialtyList().iterator(); e2.hasNext();)
			fa.write(output, "SPECIAL" + (String)e2.next() + ":");
		for (int i = 0; i < aClass.saveList.size(); i++)
		{
			String bString = (String)aClass.saveList.get(i);
			fa.write(output, bString + ":");
		}
		fa.newLine(output);
	}

	private void saveExperienceAndMiscListLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, getExperience().toString() + ":");
		for (int i = 0; i < 3; i++)
		{
			fa.write(output, Utility.escapeColons(getMiscList().get(i).toString()) + " :");
		}
		fa.write(output, getFPoints() + ":" + getDPoints() + ":");
		fa.newLine(output);
	}

	private void saveClassesSkillLine(Iterator e1, FileAccess fa, BufferedWriter output)
	{
		final PCClass aClass = (PCClass)e1.next();
		fa.write(output, aClass.getKeyName() + ":");
		for (Iterator e = aClass.skillList().iterator(); e.hasNext();)
		{
			fa.write(output, e.next() + " :");
		}
		fa.newLine(output);
	}

	private void saveGoldBioDescriptionLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, getGold().toString() + ":" + Utility.escapeColons(bio) + " :" + Utility.escapeColons(description) + " :");
		fa.newLine(output);
	}

	private void saveEquipmentLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = equipmentList.values().iterator(); setIter.hasNext();)
		{
			final Equipment eq = (Equipment)setIter.next();
			if (!eq.getHasHeaderParent())
			{

				final String eqMod1 = eq.getEqModifierString(true);			// key1.key2|assoc1|assoc2.key3.key4
				final String eqMod2 = eq.getEqModifierString(false);			// key1.key2|assoc1|assoc2.key3.key4
				String sProp = eq.getRawSpecialProperties();
				final boolean bSameName = eq.getName().equals(eq.getItemNameFromModifiers());

				if (bSameName && eq.getKeyName().equals(eq.getBaseItemName()))
				{
					sProp = "";
				}

				if (!bSameName || !eq.getSize().equals(eq.getBaseSize()) || (eqMod1.length() != 0) || (eqMod2.length() != 0) || (sProp.length() != 0))
				{
					//
					// Save customized name if differs from generated name
					//
					if ((!bSameName) || (sProp.length() != 0))
					{
						fa.write(output, eq.getName() + ";");
					}

					String eqSize = eq.getSize();
					if (eqSize.length() == 0)
					{
						eqSize = Constants.s_SIZESHORT[Constants.SIZE_M];
					}
					fa.write(output, eq.getBaseItemName() + ";" + eqSize + ";");
					if (eqMod1.length() != 0)
					{
						fa.write(output, eqMod1);
					}
					else
					{
						fa.write(output, Constants.s_NONE);
					}
					fa.write(output, ";");
					if (eqMod2.length() != 0)
					{
						fa.write(output, eqMod2);
					}
					else
					{
						fa.write(output, Constants.s_NONE);
					}
					if (sProp.length() != 0)
					{
						fa.write(output, ";" + sProp);
					}
				}
				else
				{
					fa.write(output, eq.getBaseItemName());
				}

				fa.write(output, " :" + eq.qty().toString() + ":");
				if (!eq.isHeaderParent())
				{
					if (eq.isEquipped())
						fa.write(output, "Y:");
					else
						fa.write(output, "N:");
				}
				else
				{
					Equipment aHChild = null;
					for (Iterator hchIter = eq.getHeaderChildren().iterator(); hchIter.hasNext();)
					{
						aHChild = (Equipment)hchIter.next();
						if (aHChild.isEquipped())
							fa.write(output, "Y|");
						else
							fa.write(output, "N|");

						if (aHChild.getCarried().compareTo(new Float(0)) > 0)
						{
							if (aHChild.getParent() != null)
								fa.write(output, aHChild.getCarried() + "@" + (String)((Equipment)aHChild.getParent()).getKeyName());
							else
								fa.write(output, aHChild.getCarried().toString());
						}
						else
							fa.write(output, "N");

						if (hchIter.hasNext())
							fa.write(output, "|");
						else
							fa.write(output, ":");

					}

				}
				if (eq.getCarried().compareTo(new Float(0)) > 0)
				{
					if (eq.getParent() != null)
						fa.write(output, eq.getCarried() + "@" + (String)((Equipment)eq.getParent()).getKeyName() + ":");
					else
						fa.write(output, eq.getCarried() + ":");
				}
				else
					fa.write(output, "N:");

				fa.write(output, Equipment.getHandName(eq.getHand()) + ":");
				if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
					fa.write(output, eq.getNumberEquipped() + ":");
			}
		}
		fa.newLine(output);
	}

	private void saveMiscLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, Utility.escapeColons2(eyeColor) + " :" + Utility.escapeColons2(skinColor) + " :" + Utility.escapeColons2(hairColor) + " :" +
			Utility.escapeColons2(hairStyle) + " :" + Utility.escapeColons2(speechTendency) + " :" + Utility.escapeColons2(phobias) + " :" +
			Utility.escapeColons2(interests) + " :" + Utility.escapeColons2(trait1) + " :" + Utility.escapeColons2(trait2) + " :" + Utility.escapeColons2(catchPhrase) +
			" :" + Utility.escapeColons2(location) + " :" + Utility.escapeColons2(residence) + " :");
		fa.newLine(output);
	}

	private void saveUnusedPointsLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, String.valueOf(this.getSkillPoints()) + ":");
		fa.write(output, String.valueOf(this.getFeats()));
		fa.newLine(output);
	}

	private void saveWeaponProfsLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = weaponProfList.iterator(); setIter.hasNext();)
		{
			fa.write(output, setIter.next() + ":");
		}

		//
		// Save any selected racial bonus weapons
		//
		if (race != null)
		{
			final ArrayList wp = race.getSelectedWeaponProfBonus();
			for (int i = 0; i < wp.size(); i++)
			{
				if (i == 0)
				{
					fa.write(output, "RACE=" + race.getName() + ":");
				}
				fa.write(output, (String)wp.get(i) + ":");
			}
		}

		//
		// Save any selected class bonus weapons
		//
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			final ArrayList wp = aClass.getSelectedWeaponProfBonus();
			for (int i = 0; i < wp.size(); i++)
			{
				if (i == 0)
				{
					fa.write(output, "CLASS=" + aClass.getName() + ":");
				}
				fa.write(output, (String)wp.get(i) + ":");
			}
		}

		//
		// Save any selected domain bonus weapons
		//
		for (Iterator e = characterDomainList.iterator(); e.hasNext();)
		{
			final CharacterDomain aCD = (CharacterDomain)e.next();
			final Domain aDomain = aCD.getDomain();
			if (aDomain != null)
			{
				final ArrayList wp = aDomain.getSelectedWeaponProfBonus();
				for (int i = 0; i < wp.size(); i++)
				{
					if (i == 0)
					{
						fa.write(output, "DOMAIN=" + aDomain.getName() + ":");
					}
					fa.write(output, (String)wp.get(i) + ":");
				}
			}
		}

		fa.newLine(output);
	}

	private void saveLanguagesLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = getLanguagesList().iterator(); setIter.hasNext();)
		{
			fa.write(output, setIter.next().toString() + ":");
		}
		fa.newLine(output);
	}

	private void saveClassSpellLine(Iterator e, FileAccess fa, BufferedWriter output)
	{
		final PCClass aClass = (PCClass)e.next();
		Spell aSpell = null;
		for (Iterator s = aClass.spellList().iterator(); s.hasNext();)
		{
			aSpell = (Spell)s.next();
			fa.write(output, aSpell.getKeyName());
			for (int j = 0; j < aSpell.getSpellBooks().size(); j++)
				fa.write(output, "|" + aSpell.getSpellBooks().get(j).toString() + "|" +
					aSpell.getTimes().get(j).toString());
			fa.write(output, ":");
		}
		fa.newLine(output);
	}

	private void saveRaceLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, this.getRace().getKeyName() + ":");
		fa.write(output, String.valueOf(alignment) + ":");
		fa.write(output, String.valueOf(height) + ":");
		fa.write(output, String.valueOf(weight) + ":");
		fa.write(output, String.valueOf(age) + ":");
		fa.write(output, gender + ":");
		fa.write(output, getHanded());
		if (this.getRace().hitDice() != 0)
		{
			for (int j = 0; j < this.getRace().hitDice(); j++)
			{
				fa.write(output, ":" + this.getRace().getHitPointList(j).toString());
			}
		}
		fa.newLine(output);
	}

	private void saveDeityLine(FileAccess fa, BufferedWriter output)
	{
		String aName = Constants.s_NONE;
		if (deity != null)
		{
			aName = getDeity().getKeyName();
		}
		fa.write(output, aName + ":");

		for (int i = 0; i < characterDomainList.size(); i++)
		{
			final CharacterDomain aCD = (CharacterDomain)characterDomainList.get(i);
			aName = Constants.s_NONE;
			if (aCD != null)
			{
				final Domain aDomain = aCD.getDomain();
				if (aDomain != null)
				{
					aName = aDomain.getKeyName() + "=" + aCD.getDomainSource();
				}
			}
			fa.write(output, aName + ":");
		}
		fa.newLine(output);
	}

	private void saveSkillsLine(FileAccess fa, BufferedWriter output)
	{
		Skill aSkill = null;
		for (Iterator e = skillList.iterator(); e.hasNext();)
		{
			aSkill = (Skill)e.next();

			//
			// Only save skills with a Rank
			//
			final Float aRank = aSkill.getRank();
			if (aRank.doubleValue() != 0.0)
			{
				fa.write(output, aSkill.getKeyName() + ":" + aSkill.getRank().toString() + ":");

				ArrayList aRanks = aSkill.getRankList();
				fa.write(output, aRanks.size() + ":");
				for (int i = 0; i < aRanks.size(); i++)
				{
					fa.write(output, (String)aRanks.get(i) + ":");
				}

				for (int i = 0; i < aSkill.getAssociatedList().size(); i++)
					fa.write(output, aSkill.getAssociatedList().get(i).toString() + ":");
			}
		}
		fa.newLine(output);
	}

	/* Iterate through a characters feat list and save the associated choices with it.
* featList = ArrayList of feats a character has chosen
* virtualFeatList = ArrayList of feats a character has virtually
* automaticFeatList = ArrayList of feats a character has automatically via FEATAUTO: tags
* aggregatedFeatList = ArrayList which combines all 3 of the above lists.
* we only want to save choices here, since when we load a character we assume anything
* listed is a chosen feat.  The virtual and automatic feats will get re-applied as the
* character is recreated on import, so those don't need to be saved. The obvious flaw
* in this is how to save choices due to a virtual or automatic feat, but that can be
* avoided for now if we simply say that we don't support multiple-feats in a virtual or
* automatic fashion. This will need to be addressed at some point, but is low priority.
* merton_monk@yahoo.com (Bryan McRoberts) 2-5-2002
*/
	private void saveFeatsLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator iter = featList.iterator(); iter.hasNext();)
		{
			final Feat aFeat = (Feat)iter.next();
			fa.write(output, aFeat.toString());
			for (Iterator saveFeatsIter = aFeat.saveList.iterator(); saveFeatsIter.hasNext();)
				fa.write(output, "[" + saveFeatsIter.next().toString());
			fa.write(output, ":" + String.valueOf(aFeat.getAssociatedList().size()) + ":");

			for (Iterator f = aFeat.getAssociatedList().iterator(); f.hasNext();)
				fa.write(output, f.next().toString() + ":");
		}
		fa.newLine(output);
	}

	private void saveClassesLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			fa.write(output, aClass.getKeyName() + ":" + aClass.getSubClassName() + " :" + aClass.getProhibitedString() + " :");
			fa.write(output, aClass.getLevel().toString() + ":");
			for (int j = 0; j < aClass.getLevel().intValue(); j++)
			{
				fa.write(output, aClass.getHitPointList(j).toString() + ":");
			}
			fa.write(output, aClass.skillPool().toString() + ":");
			fa.write(output, aClass.getSpellBaseStat() + ":");
		}
		fa.newLine(output);
	}

	private void saveStatsLine(FileAccess fa, BufferedWriter output)
	{
		if (Globals.s_ATTRIBLONG.length != 6)
		{
			fa.write(output, "STATS:" + String.valueOf(Globals.s_ATTRIBLONG.length) + ":");
		}
		for (int i = 0; i < Globals.s_ATTRIBLONG.length; i++)
		{
			fa.write(output, String.valueOf(getStat(i)) + ":");
		}
		fa.write(output, String.valueOf(getPoolAmount()) + ":" + String.valueOf(costPool));
		fa.newLine(output);
	}

	private void saveNameLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, Utility.escapeColons2(name) + ":" + Utility.escapeColons2(playersName));
		fa.newLine(output);
	}

	/** modified this function to output the version number as displayed in pcgenprop.properties
	 *   instead of a simple int. This will record the version more accurately. merton_monk 10/24/01
	 */
	private void saveVersionLine(FileAccess fa, BufferedWriter output)
	{
		ResourceBundle d_properties;
		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/gui/PCGenProp");
			fa.write(output, "VERSION:");
			fa.write(output, d_properties.getString("VersionNumber"));
			fa.newLine(output);
		}
		catch (java.util.MissingResourceException mre)
		{
			d_properties = null;
		}

	}

	private void saveCampaignLine(FileAccess fa, BufferedWriter output)
	{
		Iterator campIter = Globals.getCampaignList().iterator();
		fa.write(output, "CAMPAIGNS:");
		Campaign aCamp = null;
		while (campIter.hasNext())
		{
			aCamp = (Campaign)campIter.next();
			if (aCamp.isLoaded())
			{
				fa.write(output, aCamp.getName() + ":");
			}
		}
		fa.newLine(output);
	}

	private void saveTemplateLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, "TEMPLATE:");
		for (int x = 0; x < templateList.size(); x++)
//			if (((PCTemplate)templateList.get(x)).isVisible())
// Very bad results if we don't write the invisible templates to the pcg file. :)
			fa.write(output, ((PCTemplate)templateList.get(x)).getName() + ":");
		fa.newLine(output);
	}

	private String loadClassSpellLine(Iterator e, FileAccess fa, BufferedReader input)
	{
		StringTokenizer classSpellTokenizer;
		String spellName;
		PCClass aClass = (PCClass)e.next();
		lastLineParsed = fa.readLine(input);
		classSpellTokenizer = new StringTokenizer(lastLineParsed, ":", false);

		String spellLine = null;
		StringTokenizer bTok = null;
		Spell aSpell = null;
		while (classSpellTokenizer.hasMoreTokens())
		{
			spellLine = (String)classSpellTokenizer.nextToken();
			bTok = new StringTokenizer(spellLine, "|", false);
			spellName = (String)bTok.nextToken();
			aSpell = aClass.getSpellNamed(spellName);
			if (aSpell == null)
			{
				aSpell = (Spell)Globals.getSpellNamed(spellName);
				if (aSpell != null)
				{
					aSpell = (Spell)aSpell.clone();
					String className = new String(aClass.getKeyName());
					if (aClass.getCastAs().length() > 0)
						className = aClass.getCastAs();
					String levelString = aSpell.levelForClass(className, aClass.getName());
					aSpell.setClassLevels(levelString);
					aClass.spellList().add(aSpell);
				}
			}
			if (aSpell != null && bTok.countTokens() == 0)
			{
				aSpell.addToSpellBook(Globals.getDefaultSpellBook(), false);
				addSpellBook(Globals.getDefaultSpellBook());
			}
			if (aSpell != null)
			{
				String bookName = null;
				while (bTok.hasMoreTokens())
				{
					bookName = bTok.nextToken();
					addSpellBook(bookName);
					if (aSpell.getSpellBooks().contains(bookName))
					{
						if (bTok.hasMoreTokens())
							bTok.nextToken(); // this book already exists, so burn the token
					}
					else
					{
						aSpell.getSpellBooks().add(bookName);
						if (bTok.hasMoreTokens())
							aSpell.getTimes().add(new Integer(bTok.nextToken()));
						else
							aSpell.getTimes().add(new Integer(1));
					}
				}
			}
		}
		return lastLineParsed;
	}

	private void loadExperienceAndMiscLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		int k;
		String cString = "";
		boolean nextLine = true;
		while (i <= 5)
		{
			if (nextLine)
				lastLineParsed = fa.readLine(input);
			k = lastLineParsed.indexOf(':');
			while (k > 0 && lastLineParsed.charAt(k - 1) == '\\')
				k = lastLineParsed.indexOf(':', k + 1);
			if (k < 0 || lastLineParsed.charAt(k - 1) == '\\') k = -1;
			if (k == -1)
			{
				cString = cString.concat(lastLineParsed);
				cString = cString.concat("\r\n");
				nextLine = true;
				//EOL so don't try 4 or 5, it'll break old PCG files
				if (i > 3)
					break;
			}
			else
			{
				k = lastLineParsed.indexOf(':');
				while (lastLineParsed.charAt(k - 1) == '\\')
					k = lastLineParsed.indexOf(':', k + 1);
				cString = cString.concat(lastLineParsed.substring(0, k));
				Globals.debugPrint("Line " + i + ": " + cString);
				switch (i)
				{
					case 0:
						setExperience(new Integer(cString));
						break;
					case 1:
					case 2:
					case 3:
						String tempStr = "";
						for (int j = 0; j < cString.length(); j++)
						{
							if (cString.charAt(j) != '\\')
								tempStr += cString.charAt(j);
							else
							{
								if (j + 1 < cString.length() && cString.charAt(j + 1) != ':')
									tempStr += "\\";
							}
						}
						getMiscList().set(i - 1, tempStr.trim());
						break;
					case 4:
						setFPoints(cString);
						break;
					case 5:
						setDPoints(cString);
						break;
						/*************************/
				}
				i++;
				if (i < 6)
					lastLineParsed = lastLineParsed.substring(k + 1);
				cString = "";
				nextLine = false;
			}
		}
	}

	private void loadClassSpecialtyAndSaveLines(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		StringTokenizer aTok;
		String cString;
		while (i < classList.size())
		{
			lastLineParsed = fa.readLine(input);
			if (lastLineParsed == null) return;
			aTok = new StringTokenizer(lastLineParsed, ":", false);
			String bString = aTok.nextToken();
			PCClass aClass = getClassKeyed(bString);
			i++;
			if (aClass == null || aClass.getKeyName().equals("Domain"))
				continue;
			while (aTok.hasMoreTokens())
			{
				cString = aTok.nextToken();
				if (cString.startsWith("SPECIAL"))
					aClass.getSpecialtyList().add(cString.substring(7));
				else
				{
					// This no longer needs to be saved in the PCG file. Need to strip it from older versions of
					// save files. Gets handled differently in class
					if (cString.equals("Smite Evil"))
						continue;
					if (cString.startsWith("BONUS"))
					{
						aClass.getBonusList().add(cString.substring(6));
						if (cString.lastIndexOf("|PCLEVEL|") > -1)
						{
							StringTokenizer cTok = new StringTokenizer(cString.substring(cString.lastIndexOf("PCLEVEL")), "|", false);
							cTok.nextToken(); // should be PCLEVEL
							if (cTok.hasMoreTokens())
							{
								specialAbilityList.add("Bonus Caster Level for " + cTok.nextToken());
							}
						}
					}
					else if (!specialAbilityList.contains(cString))
						specialAbilityList.add(cString);
					if (!aClass.saveList.contains(cString) || cString.startsWith("BONUS"))
						aClass.saveList.add(cString);
				}
			}
		}
	}

	private void loadClassesSkillLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		String aString = aTok.nextToken();
		PCClass aClass = (PCClass)getClassKeyed(aString);
		if (aClass == null)
		{
			return; //Is this right? Shouldn't an exception be thrown instead?
		}
		while (aTok.hasMoreTokens())
		{
			if (aClass != null)
			{
				aClass.skillList().add(aTok.nextToken().trim());
			}
		}
	}

	private void loadGoldBioDescriptionLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		int k;
		String cString = "";
		boolean nextLine = true;
		while (i <= 2)
		{
			if (nextLine)
				lastLineParsed = fa.readLine(input);
			k = lastLineParsed.indexOf(':');
			while ((k > 0) && lastLineParsed.charAt(k - 1) == '\\')
			{
				k = lastLineParsed.indexOf(':', k + 1);
			}

			if ((k < 0) || lastLineParsed.charAt(k - 1) == '\\')
			{
				k = -1;
			}

			if (k == -1)
			{
				cString = cString.concat(lastLineParsed);
				cString = cString.concat("\r\n");
				nextLine = true;
			}
			else
			{
				k = lastLineParsed.indexOf(':');
				while (lastLineParsed.charAt(k - 1) == '\\')
				{
					k = lastLineParsed.indexOf(':', k + 1);
				}
				cString = cString.concat(lastLineParsed.substring(0, k));
				Globals.debugPrint("Line " + i + ": " + cString);
				String tempStr = "";
				for (int j = 0; j < cString.length(); j++)
				{
					if (cString.charAt(j) != '\\')
					{
						tempStr += cString.charAt(j);
					}
					else
					{
						if (j + 1 < cString.length() && cString.charAt(j + 1) != ':')
						{
							tempStr += "\\";
						}
					}
				}
				switch (i)
				{
					case 0:
						setGold(tempStr);
						break;
					case 1:
						setBio(tempStr);
						break;
					case 2:
						setDescription(tempStr);
						break;
				}
				i++;
				if (i < 4)
					lastLineParsed = lastLineParsed.substring(k + 1);
				cString = "";
				nextLine = false;
			}
		}
	}

	private void loadEquipmentLine(FileAccess fa, BufferedReader input)
	{
		String aName;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		Equipment eq = null;
		HashMap containers = new HashMap();
		boolean bFound;

		HashMap headerChildren = null;
		while (aTok.hasMoreTokens())
		{
			headerChildren = new HashMap();
			aName = aTok.nextToken().trim();

			final StringTokenizer anTok = new StringTokenizer(aName, ";");
			String sized = "";
			String head1 = "";
			String head2 = "";
			String customName = "";
			String customProp = "";
			int tokenCount = anTok.countTokens();
			if ((tokenCount >= 4) && (tokenCount <= 6))
			{
				//
				// baseName;size;head1;head2
				// name;baseName;size;head1;head2
				// name;baseName;size;head1;head2;sprop
				//
				if (tokenCount >= 5)
				{
					customName = anTok.nextToken();
				}
				String baseName = anTok.nextToken();
				sized = anTok.nextToken();
				head1 = anTok.nextToken();
				head2 = anTok.nextToken();
				aName = baseName;
				if (tokenCount == 6)
				{
					customProp = anTok.nextToken();
				}
			}

			eq = (Equipment)Globals.getEquipmentKeyed(aName);
			if (eq == null)
			{
				eq = Globals.getEquipmentFromName(aName);		// Try to strip the modifiers off the item
			}

			bFound = true;
			if (eq == null)
			{
				eq = new Equipment();		// dummy container to stuff equipment info into
				bFound = false;
			}
			else
			{
				eq = (Equipment)eq.clone();
			}

			if (customProp.length() != 0)
			{
				eq.setSpecialProperties(customProp);
			}

			eq.addEqModifiers(head1, true);
			eq.addEqModifiers(head2, false);
			if (((sized.length() != 0) && !eq.getSize().equals(sized)) || (eq.getEqModifierList(true).size() + eq.getEqModifierList(false).size() != 0) || (customProp.length() != 0))
			{
				if (sized.length() == 0)
				{
					sized = eq.getSize();
				}
				eq.resizeItem(sized);
				eq.nameItemFromModifiers();
			}
			//
			// If item doesn't exist, add it to the equipment list
			//
			if (bFound)
			{
				if (customName.length() > 0)
				{
					eq.setName(customName);
				}
				Globals.addEquipment((Equipment)eq.clone());
			}


			eq.setQty(aTok.nextToken());
			if (eq.qty().floatValue() > 1 && eq.acceptsChildren())  //hack to see if it is a headerParent instead of a normal container
			{
				final int origQty = eq.qty().intValue();
				eq.setIsHeaderParent(true);
				final StringTokenizer cTok = new StringTokenizer(aTok.nextToken(), "|", false);
				if (cTok.countTokens() == 1)
				{
					Globals.debugPrint("Correct Path " + cTok.countTokens());

					final boolean firstEquipped = cTok.nextToken().equals("Y");
					int numberCarried = (parseCarried(new Float(origQty), aTok.nextToken())).intValue();

					for (int i = 0; i < origQty; i++)
					{
						final Equipment aHChild = eq.createHeaderParent();
						aHChild.clearHeaderChildren();
						if (i == 0)
							aHChild.setIsEquipped(firstEquipped);
						if (numberCarried-- > 0)
							aHChild.setCarried(new Float(0));
						headerChildren.put(aHChild.getKeyName(), aHChild);
					}
				}
				else
				{
					for (int i = 0; i < origQty; i++)
					{
						final Equipment aHChild = eq.createHeaderParent();
						aHChild.clearHeaderChildren();
						aHChild.setIsEquipped(cTok.nextToken().equals("Y"));
						final StringTokenizer bTok = new StringTokenizer(cTok.nextToken(), "@", false);
						aHChild.setCarried(parseCarried(aHChild.qty(), bTok.nextToken()));
						if (bTok.hasMoreTokens())
							containers.put(aHChild.getKeyName(), (String)bTok.nextToken());
						headerChildren.put(aHChild.getKeyName(), aHChild);
					}
					aTok.nextToken();
				}
			}
			else
			{
				eq.setIsEquipped(aTok.nextToken().equals("Y"));
				final StringTokenizer bTok = new StringTokenizer(aTok.nextToken(), "@", false);
				eq.setCarried(parseCarried(eq.qty(), bTok.nextToken()));
				if (bTok.hasMoreTokens())
					containers.put(eq.getKeyName(), (String)bTok.nextToken());
			}

			eq.setHand(Equipment.getHandNum(aTok.nextToken()));
			if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
			{
				eq.setNumberEquipped(Integer.parseInt(aTok.nextToken()));
			}

			if (bFound)
			{
				equipmentList.put(eq.getKeyName(), eq);
				equipmentList.putAll(headerChildren);
			}
			else
			{
				//
				// Only show message if not natural weapon
				//
				if (customName.indexOf("Natural/") < 0)
				{
					GuiFacade.showMessageDialog(null, "Equipment not found: " + aName + " (" + eq.qty() + ")" + s_CHECKLOADEDCAMPAIGNS, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		//now insert parent/child relationships
		Equipment aParent = null;
		for (Iterator e = containers.keySet().iterator(); e.hasNext();)
		{
			aName = (String)e.next();
			eq = (Equipment)equipmentList.get(aName);
			if (eq != null)
			{
				aParent = (Equipment)equipmentList.get((String)containers.get(aName));
				aParent.insertChild(eq);
			}
		}
	}

	private void loadMiscLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		String aString;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		;
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken().trim();
			aString = Utility.unEscapeColons2(aString);
			i++;
			switch (i)
			{
				case 1:
					setEyeColor(aString);
					break;
				case 2:
					setSkinColor(aString);
					break;
				case 3:
					setHairColor(aString);
					break;
				case 4:
					setHairStyle(aString);
					break;
				case 5:
					setSpeechTendency(aString);
					break;
				case 6:
					setPhobias(aString);
					break;
				case 7:
					setInterests(aString);
					break;
				case 8:
					setTrait1(aString);
					break;
				case 9:
					setTrait2(aString);
					break;
				case 10:
					setCatchPhrase(aString);
					break;
				case 11:
					setLocation(aString);
					break;
				case 12:
					setResidence(aString);
					break;
			}
		}
	}

	private void loadUnusedPointsLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		setSkillPoints(Integer.parseInt(aTok.nextToken()));
		setFeats(Integer.parseInt(aTok.nextToken()));
	}

	private void handleWeaponProfLine(String profLine)
	{
		int iState = 0;
		lastLineParsed = profLine;
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		Race aRace = null;
		PCClass aClass = null;
		Domain aDomain = null;

		ArrayList myProfs = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (aString.startsWith("RACE="))
			{
				iState = 1;
				aRace = getRace();
				continue;
			}
			else if (aString.startsWith("CLASS="))
			{
				iState = 2;
				aString = aString.substring(6);
				aClass = getClassNamed(aString);
				continue;
			}
			else if (aString.startsWith("DOMAIN="))
			{
				iState = 3;
				aString = aString.substring(7);
				aDomain = getCharacterDomainNamed(aString);
				continue;
			}

			switch (iState)
			{
				case 1:
					if (aRace != null)
					{
						aRace.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				case 2:
					if (aClass != null)
					{
						aClass.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				case 3:
					if (aDomain != null)
					{
						aDomain.getSelectedWeaponProfBonus().add(aString);
					}
					break;

				default:
					myProfs.add(aString);
					//addWeaponProfToList(featList, aString, false);
					break;
			}
		}


		setAutomaticFeatsStable(false);
		featAutoList();		// populate profs array with automatic profs
		ArrayList nonproficient = new ArrayList();
		for (Iterator e = myProfs.iterator(); e.hasNext();)
		{
			final String aString = (String)e.next();
			if (!hasWeaponProfNamed(aString))
			{
				nonproficient.add(aString);
			}
		}


		//
		// For some reason, character had a proficiency that they should not have. Inform
		// the user that they no longer have the proficiency.
		//
		if (nonproficient.size() != 0)
		{
			String s = nonproficient.toString();
			s = s.substring(1, s.length() - 1);
			GuiFacade.showMessageDialog(null, "No longer proficient with following weapon(s):\n" + s, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void loadLanguagesLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			addLanguage(aTok.nextToken());
		}
	}

	private void loadRaceLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":");
		int l = 0;
		Integer[] hitPointList = null;
		int x = 0;
		Race aRace = null;
		String raceName = "";
		while (aTok.hasMoreElements())
		{
			String aString = (String)aTok.nextElement();
			if (i > 0 && i < 5)
			{
				l = Integer.parseInt(aString);
			}
			switch (i++)
			{
				case 0:
					aRace = Globals.getRaceKeyed(aString);
					raceName = aString;
					if (aRace != null)
					{
						setRace(aRace);
						if (aRace.hitDice() != 0)
						{
							hitPointList = new Integer[aRace.hitDice()];
						}
					}
					else
					{
						GuiFacade.showMessageDialog(null, "Race not found: " + aString + s_CHECKLOADEDCAMPAIGNS, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					}
					break;
				case 1:
					setAlignment(l, true);
					break;
				case 2:
					setHeight(l);
					break;
				case 3:
					setWeight(l);
					break;
				case 4:
					setAge(l);
					break;
				case 5:
					setGender(aString);
					break;
				case 6:
					setHanded(aString);
					break;
				default:
					if (hitPointList != null)
					{
						l = Integer.parseInt(aString);
						hitPointList[x++] = new Integer(l);
						if (aRace != null)
						{
							if (x == aRace.hitDice())
							{
								getRace().setHitPointList(hitPointList);
								return;
							}
						}
					}
					else
					{
						x += 1;
						if (x == 1)
						{
							GuiFacade.showMessageDialog(null, "Saved race (" + raceName + ") no longer has a HITDICE tag.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						}
					}
					break;
			}
		}
	}

	private void loadTemplateLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		if (lastLineParsed == null)
		{
			return;
		}
		else
		{
			if (lastLineParsed.startsWith("TEMPLATE:"))
			{
				lastLineParsed = lastLineParsed.substring(9);
			}
			final StringTokenizer tokens = new StringTokenizer(lastLineParsed, ":");
			while (tokens.hasMoreTokens())
			{
				final PCTemplate template = Globals.getTemplateNamed(tokens.nextToken());
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
				if (template != null)
				{
					addTemplate(template, false);
				}
			}
		}
	}

	private void loadNotes(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		boolean flag = (lastLineParsed != null && lastLineParsed.startsWith("NOTES:"));
		NoteItem anItem = null;
		while (flag == true)
		{
			if (lastLineParsed.startsWith("NOTES:"))
			{
				final StringTokenizer aTok = new StringTokenizer(lastLineParsed.substring(6), ":", false);
				final int id_value = Integer.parseInt(aTok.nextToken());
				final int id_parent = Integer.parseInt(aTok.nextToken());
				final String id_name = aTok.nextToken();
				final String id_text = aTok.nextToken();
				anItem = new NoteItem(id_value, id_parent, id_name, id_text);
				notesList.add(anItem);
			}
			else
				anItem.setValue(anItem.getValue() + System.getProperty("line.separator") + lastLineParsed);
			lastLineParsed = fa.readLine(input);
			flag = (lastLineParsed != null && !lastLineParsed.equals(":ENDNOTES:"));
		}
		return;
	}

	private void saveNotes(FileAccess fa, BufferedWriter output)
	{
		for (Iterator i = notesList.iterator(); i.hasNext();)
		{
			NoteItem ni = (NoteItem)i.next();
			fa.write(output, "NOTES:" + ni.getId() + ":" + ni.getParentId() + ":" + ni.getName() + ":" + ni.getValue());
			fa.newLine(output);
		}
	}

	private void handleDeityLine(String deityLine)
	{
		int i = 0;
		lastLineParsed = deityLine;
		final StringTokenizer deityTokenizer = new StringTokenizer(deityLine, ":", false);
		String aString = null;
		while (deityTokenizer.hasMoreElements())
		{
			aString = (String)deityTokenizer.nextElement();
			switch (i++)
			{
				case 0:
					setDeity(aString);
					break;
				default:
					int j = getFirstEmptyCharacterDomain();
					if (j == -1)
					{
						CharacterDomain aCD = new CharacterDomain();
						characterDomainList.add(aCD);
						j = characterDomainList.size() - 1;
					}
					if (j >= 0)
					{
						final StringTokenizer cdTok = new StringTokenizer(aString, "=", false);
						final String domainName = cdTok.nextToken();
						CharacterDomain aCD = (CharacterDomain)characterDomainList.get(j);
						Domain aDomain = Globals.getDomainKeyed(domainName);
						if (aDomain != null)
						{
							aDomain = (Domain)aDomain.clone();
							aCD.setDomain(aDomain);
							if (cdTok.hasMoreTokens())
							{
								String sSource = cdTok.nextToken();
								aCD.setDomainSource(sSource);
							}
							aDomain.setIsLocked(true);
						}
						else
						{
							if (!domainName.equals(Constants.s_NONE))
							{
								GuiFacade.showMessageDialog(null, "Domain not found: " + aString + s_CHECKLOADEDCAMPAIGNS, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					break;
			}
		}

	}

	private void handleSkillsLine(String skillsLine)
	{
		String skillName;
		ArrayList aRankList;

		lastLineParsed = skillsLine;
		final StringTokenizer skillTokenizer = new StringTokenizer(lastLineParsed, ":", false);
		while (skillTokenizer.hasMoreElements())
		{
			skillName = (String)skillTokenizer.nextElement();
			if (!skillTokenizer.hasMoreTokens())
				return;

			final Float aFloat = new Float((String)skillTokenizer.nextElement());

			//
			// If newer version, then we can determine which skill belongs to which class as it
			// is saved in the PCG file
			//
			aRankList = new ArrayList();
			if (PcgReadVersion >= 2)
			{
				final Integer iCount = new Integer((String)skillTokenizer.nextElement());
				for (int i = 0; i < iCount.intValue(); i++)
				{
					aRankList.add((String)skillTokenizer.nextElement() + ":" + (String)skillTokenizer.nextElement());
				}
			}


			//
			// Locate the skill in question, add to list if not already there
			//
			Skill aSkill = this.getSkillKeyed(skillName);
			if (aSkill == null)
			{
				for (int i = 0; i < Globals.getSkillList().size(); i++)
					if (skillName.equals(Globals.getSkillList().get(i).toString()))
					{
						aSkill = (Skill)Globals.getSkillList().get(i);
						aSkill = (Skill)aSkill.clone();
						skillList.add(aSkill);
						break;
					}
			}

			if (aSkill != null)
			{
				for (int i = 0; i < aRankList.size(); i++)
				{
					String bRank = (String)aRankList.get(i);
					int iOffs = bRank.indexOf(':');
					Float fRank = new Float(bRank.substring(iOffs + 1));
					PCClass aClass = getClassKeyed(bRank.substring(0, iOffs));
					if ((aClass != null) || bRank.substring(0, iOffs).equals(Constants.s_NONE))
					{
						bRank = aSkill.modRanks(fRank.doubleValue(), aClass, true);
						if (bRank.length() != 0)
							System.out.println("loadSkillsLine: " + bRank);
					}
					else
					{
						System.out.println("Class not found: " + bRank.substring(0, iOffs));
					}
				}

				if (PcgReadVersion < 2)
				{
					final String bRank = aSkill.modRanks(aFloat.doubleValue(), null, true);
					if (bRank.length() != 0)
						System.out.println("loadSkillsLine: " + bRank);
				}

				if (aSkill.choiceList().size() > 0 && aFloat.intValue() > 0)
				{
					for (int i = 0; i < aFloat.intValue(); i++)
						aSkill.getAssociatedList().add(skillTokenizer.nextToken());
				}
			}
			else
			{
				System.out.println("Skill not found: " + skillName);
				if (aFloat.doubleValue() != 0.0)
				{
					GuiFacade.showMessageDialog(null, "Ranked skill not found: " + skillName + "(" + aFloat + ")" + s_CHECKLOADEDCAMPAIGNS, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}

	private void loadFeatsLine(FileAccess fa, BufferedReader input)
	{
		String aName = null;
		String aString = null;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			aName = aTok.nextToken();
			final int l = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(aName, "[", false);

			aName = bTok.nextToken();
			Feat aFeat = Globals.getFeatKeyed(aName);
			if ((aFeat != null) && !hasFeatAutomatic(aName))
			{
				aFeat = (Feat)aFeat.clone();
				modFeat(aFeat.getKeyName(), true, !aFeat.isMultiples());
				if (aFeat.isMultiples() && aFeat.getAssociatedList().size() == 0 && getFeatKeyed(aFeat.getKeyName()) == null)
				{
					addFeat(aFeat);
				}
				aFeat = getFeatKeyed(aFeat.getKeyName());
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
					if (aString.startsWith("BONUS") && aString.length() > 6)
					{
						aFeat.bonusList.add(aString.substring(6));
					}
					aFeat.saveList.add(aString);
				}
			}
			else
			{
				aFeat = new Feat();
			}

			for (int j = 0; j < l; j++)
			{
				aString = aTok.nextToken();
				if ((aFeat.isMultiples() && aFeat.isStacks()) || !aFeat.getAssociatedList().contains(aString))
				{
					aFeat.getAssociatedList().add(aString);
				}
			}
		}
	}

	private void loadClassesLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		String aName = null;
		boolean getNext = true;
		String aString = "";
		int x = 0;
		while (aTok.hasMoreTokens())
		{
			if (getNext)
			{
				x++;
				aName = aTok.nextToken();
			}
			else
				aName = aString;
			getNext = true;
			if (!aTok.hasMoreTokens())
				break;
			boolean needCopy = true;
			PCClass aClass = getClassKeyed(aName);
			if (aClass == null)
			{
				aClass = Globals.getClassKeyed(aName);
			}
			else
			{
				needCopy = false;
			}

			if (aClass == null && aName.equalsIgnoreCase("Domain"))
			{
				System.out.println("Domain class found and ignored. Please check character to verify conversion is successful.");
				ignoreDomainClassLine = x;
			}
			else if (aClass == null)
			{
				GuiFacade.showMessageDialog(null, "Class not found: " + aName + s_CHECKLOADEDCAMPAIGNS, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}

			// ClassName:SubClassName:ProhibitedString:Level:[hp1:[hp2:...[hpn:]]]skillPool:SpellBaseStat:

			//
			// If the class wasn't found we will parse through the data anyway, but just toss it
			//
			String subClassName = aTok.nextToken().trim();
			String prohibitedString = aTok.nextToken().trim();
			int l = Integer.parseInt(aTok.nextToken());
			if (aClass != null)
			{
				if (needCopy)
				{
					aClass = (PCClass)aClass.clone();
					classList.add(aClass);
				}
				aClass.setSubClassName(subClassName);
				aClass.setProhibitedString(prohibitedString);
			}

			//
			// NOTE: race is not yet set here, so skillpool calculated in addLevel will be out by
			// racial intelligence adjustment and BonusSkillsPerLevel, but we're just going to trash
			// the calculated value in the next step anyway
			//
			for (int k = 0; k < l; k++)
			{
				int iHp = Integer.parseInt(aTok.nextToken());
				if (aClass != null)
				{
					aClass.addLevel(false);
					aClass.hitPointList()[k] = new Integer(iHp);
				}
			}
			Integer skillPool = new Integer(aTok.nextToken());
			if (aClass != null)
			{
				aClass.setSkillPool(skillPool);
			}

			if (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if ((Globals.getStatFromAbbrev(aString.toUpperCase()) > -1) || aString.equalsIgnoreCase(Constants.s_NONE) || aString.equalsIgnoreCase("Any") || aString.equalsIgnoreCase("SPELL"))
				{
					if (aClass != null)
					{
						aClass.setSpellBaseStat(aString);
					}
				}
				else
				{
					getNext = false;
				}
			}
		}
		currentHP = hitPoints();
		setAutomaticFeatsStable(false);
		setVirtualFeatsStable(false);
		setAggregateFeatsStable(false);
	}

	private void loadStatsLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);

		//
		// Check for different STAT counts
		//
		int statCount = 6;
		if (lastLineParsed.startsWith("STATS:"))
		{
			aTok.nextToken();			// ignore "STATS:"
			statCount = Integer.parseInt(aTok.nextToken());
		}
		if (statCount != Globals.s_ATTRIBLONG.length)
		{
			GuiFacade.showMessageDialog(null, "Number of Stats for character is " + statCount +
				". PCGen is currently using " + Globals.s_ATTRIBLONG.length + ". Cannot load character."
				, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}


		while (aTok.hasMoreTokens() && i < Globals.s_ATTRIBLONG.length)
		{
			setStat(i++, Integer.parseInt(aTok.nextToken()));
		}
		if (aTok.hasMoreTokens())
			poolAmount = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
			costPool = Integer.parseInt(aTok.nextToken());
	}

	private void loadNameLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		name = Utility.unEscapeColons2(aTok.nextToken());
		if (aTok.hasMoreTokens())
			playersName = Utility.unEscapeColons2(aTok.nextToken());
	}

	private Float parseCarried(Float qty, String aName)
	{
		float carried = 0.0F;
		if (aName.equals("Y"))
		{
			carried = qty.floatValue();
		}
		else if (aName.equals("N"))
		{
			carried = 0.0F;
		}
		else
		{
			try
			{
				carried = Float.parseFloat(aName);
			}
			catch (Exception e)
			{
				carried = 0.0F;
			}
		}
		return new Float(carried);
	}


	private boolean sensitiveCheck()
	{
		boolean foundIt = false;
		ArrayList aFeatList = (ArrayList)Globals.getCurrentPC().aggregateFeatList();

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
	 * changed signature from "private" to "public"
	 * due to refactoring of export
	 * author: Thomas Behr 08-03-02
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
					catch (Exception exception)
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

	private String getItemDescription(String sType, String sKey, String sAlt)
	{
		if (Globals.isROG())
		{
			if (descriptionLst.equals("EMPTY"))
			{
				loadDescriptionFilesInDirectory("descriptions");
			}
			String aDescription = sAlt;
			String aSearch = sType.toUpperCase() + ":" + sKey + "\r\n";
			int pos = descriptionLst.indexOf(aSearch);
			if (pos > -1)
			{
				aDescription = descriptionLst.substring(pos + aSearch.length());
				aDescription = aDescription.substring(0, aDescription.indexOf("####") - 1).trim();
			}
			return aDescription;
		}
		else
			return sAlt;
	}

	private void replaceWithDelimiter(FileAccess fa, BufferedWriter output, String sString, String sDelim)
	{
		final StringTokenizer bTok = new StringTokenizer(sString, "\r\n", false);
		while (bTok.hasMoreTokens())
		{
			fa.encodeWrite(output, bTok.nextToken());
			if (bTok.hasMoreTokens())
			{
				fa.write(output, sDelim);
			}
		}
	}
}

