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
import javax.swing.JOptionPane;

/**
 * <code>PlayerCharacter</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PlayerCharacter extends Object
{

	///////////////////////////////////////
	//attributes

	private static int loopValue = 0;
	private static String loopVariable = "";
	private static int decrement;
	private static BigDecimal BIG_ONE = new BigDecimal("1.00");

	private static HashMap s_sizes = new HashMap();
	private final static int[][] size_array = {
		/* sizeInt, sizeMod, grappleSizeMod/-hideSizeMod */
		{0, 8, -16},		// Fine
		{1, 4, -12},		// Diminutive
		{2, 2, -8},			// Tiny
		{3, 1, -4},			// Small
		{4, 0, 0},			// Medium
		{5, -1, 4},			// Large
		{6, -2, 8},			// Huge
		{7, -4, 12},		// Gargantuan
		{8, -8, 16},		// Colossal
	};

//
// 1:
// hit points are no longer written with the CON modifier.
// 2:
// skills are saved by class
//
	private int PcgWriteVersion = 2;	// Version of file we will write
	private int PcgReadVersion;		// Version of file being read

	private int currentHP = 0;
	private boolean importing = false;
	private int alignment = 9; // 0 = LG to 8 = CE and 9 is <none selected>
	private TreeSet weaponProfList = new TreeSet();

	private String name = new String();
	private String playersName = new String();

	private int skillPoints = 0; // pool of skills remaining to distribute
	private int feats = 0; // pool of feats remaining to distribute
	private int[] stats = new int[6];
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
	private TreeSet languages = new TreeSet();
	private TreeSet favoredClasses = new TreeSet();
	private ArrayList miscList = new ArrayList(3);
	private ArrayList spellBooks = new ArrayList();
	private ArrayList variableList = new ArrayList();
	private ArrayList qualifyArrayList = new ArrayList();
	private HashMap bonusMap = new HashMap();

	private String eyeColor = "";
	private String skinColor = "";
	private String hairColor = "";
	private String hairLength = "";
	private String speechTendency = "";
	private String phobias = "";
	private String interests = "";
	private String catchPhrase = "";
	private String trait1 = "";
	private String trait2 = "";
	private String residence = "";
	private String location = "";
	private String subRace = "None";
	private boolean inLabel = false;
	private boolean canWrite = true;
	private BigDecimal gold = new BigDecimal("0.00");
	private String bio = new String();
	private String description = new String();
	private boolean existsOnly = false;
	private boolean noMoreItems = false;
	private boolean checkBefore = false;
	private Integer experience = new Integer(0);
	private String statNames = "STRDEXCONINTWISCHA";
	private int weaponMod = 0;
	private int initiative = 0;
	private String[] movementTypes;
	private Integer[] movements;
	private boolean dirtyFlag = false; //Whether the character has changed since last saved.
	private String fileName = ""; //This may be different from character name...
	private int bonusWeaponChoices = 0;
	private HashMap myAdditionsToGlobalEq = new HashMap(); //contains pointers to the Headerparents of equipment break-outs
	private String FPoints = "0";
	private String DPoints = "0";

	private String csheetTag2 = "\\";

	/** Only access this through getStableAggregateFeatList */
	private ArrayList stableAggregateFeatList = null;
	private ArrayList stableVirtualFeatList = null;
	private ArrayList stableAutomaticFeatList = null;

	/** Whether one can trust the most recently calculated aggregateFeatList.*/
	private boolean aggregateFeatsStable = false;
	private boolean virtualFeatsStable = false;
	private boolean automaticFeatsStable = false;

	///////////////////////////////////////////////////////////////////
	// Accessor methods

	public boolean isAggregateFeatsStable()
	{
		return aggregateFeatsStable;
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

	public TreeSet getWeaponProfList()
	{
		return weaponProfList;
	}

	public int getAlignment()
	{
		return alignment;
	}

	public boolean isImporting()
	{
		return importing;
	}

	public void setCurrentHP(int currentHP)
	{
		this.currentHP = currentHP;
	}

	public boolean checkQualifyList(String qualifierItem) {
	
		if (qualifyArrayList.contains(qualifierItem)) return true;
		else return false;
	}
	
	public ArrayList getQualifyList() {
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
		if (Globals.isDebugMode())
			System.out.println("ForcePoints:" + FPoints);
	}


	public String getDPoints()
	{
		return DPoints;
	}

	public void setDPoints(String aString)
	{
		DPoints = new String(aString);
		if (Globals.isDebugMode())
			System.out.println("Darkside Points:" + DPoints);
	}

	public HashMap GetBonusMap()
	{
		return bonusMap;
	}

	private String lastLineParsed = "";
	private String lastFnCalled = "";

	public void equipmentListAdd(Equipment anEquip)
	{
		equipmentList.put(anEquip.getKeyName(), anEquip);
	}

	public void equipmentListRemove(Equipment anEquip)
	{
		equipmentList.remove(anEquip.getKeyName());
	}

	public void setSubRace(String newSubRace)
	{
		subRace = newSubRace;
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

	public int getBonusWeaponChoices()
	{
		return bonusWeaponChoices;
	}

	public int getSkillPoints()
	{
		return skillPoints;
	}

	public void setSkillPoints(int anInt)
	{
		skillPoints = anInt;
	}

	public int getFeats()
	{
		return feats;
	}

	public void setFeats(int anInt)
	{
		feats = anInt;
	}

	public int getCurrentHP()
	{
		return currentHP;
	}

	public int[] getStats()
	{
		return stats;
	}

	public void setStats(int intArray[])
	{
		stats = intArray;
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

	/* same as getSpecialAbilityList except if
	   if you have the same ability twice, it only
	   lists it once with (2) at the end. */
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
			times = resize(times, SAs.size());
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
		for (int idx = 0; idx < aList.size(); idx++)
		{
			if (times[idx] > 1)
			{
				aList.set(idx, aList.get(idx) + " (" + times[idx] + ")");
			}
		}
		return aList;
	}

	private int[] resize(int[] array, int add)
	{
		int[] newarray = new int[array.length + add];
		for (int x = 0; x < array.length; x++)
			newarray[x] = array[x];
		return newarray;
	}

	public String getHanded()

	{
		return handed;
	}

	public void setHanded(String aString)
	{
		handed = aString;
	}

	public SortedSet getLanguagesList()
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

	public String getHairLength()
	{
		return hairLength;
	}

	public void setHairLength(String aString)
	{
		hairLength = aString;
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

	public Integer getExperience()
	{
		return experience;
	}

	public void setExperience(Integer anInt)
	{
		experience = new Integer(anInt.toString());
	}

	public ArrayList getMiscList()
	{
		return miscList;
	}

	public ArrayList getSpellBooks()
	{
		return spellBooks;
	}

	///////////////////////////////////////
	//associations

	private Race race = null;
	private ArrayList templateList = new ArrayList(); // of Template
	private ArrayList classList = new ArrayList(); // of Class
	private ArrayList featList = new ArrayList(); // of Feat
	private ArrayList domainList = new ArrayList(); // of Domain

	private Deity deity = null;
	private ArrayList skillList = new ArrayList(); // of Skill
	private Equipment primaryWeapon = null;
	private Equipment secondaryWeapon[] = new Equipment[1];
	private TreeMap equipmentList = new TreeMap(); // of Equipment

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

	public ArrayList getDomainList()
	{
		return domainList;
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
		s_sizes.put("F", size_array[0]);
		s_sizes.put("D", size_array[1]);
		s_sizes.put("T", size_array[2]);
		s_sizes.put("S", size_array[3]);
		s_sizes.put("M", size_array[4]);
		s_sizes.put("L", size_array[5]);
		s_sizes.put("H", size_array[6]);
		s_sizes.put("G", size_array[7]);
		s_sizes.put("C", size_array[8]);

		Globals.setCurrentPC(this);
		for (int i = 0; i < 6; i++)
			stats[i] = 0;
		setRace((Race)Globals.getRaceMap().get(Globals.s_NONESELECTED));
		setName("");
		skillPoints = 0;
		feats = 0;
		rollStats(Globals.getRollMethod());
		miscList.add("");
		miscList.add("");
		miscList.add("");
		addSpellBook("Known Spells");
		populateSkills(Globals.getIncludeSkills());
		for(Iterator i=Globals.GetBonusStackList().iterator();i.hasNext();)
		{
			bonusMap.put((String)i.next(),"0");
		}

	}

	public String getSize()
	{
		if (race == null)
			return "M";
		String size = race.getSize();
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
				size = "T";
			else if (size.equals("T"))
				size = "S";
			else if (size.equals("S"))
				size = "M";
			else if (size.equals("M"))
				size = "L";
			else if (size.equals("L"))
				size = "H";
			else if (size.equals("H"))
				size = "G";
			else if (size.equals("G"))
				size = "C";
			else
				break;
		}
		if (size.equals(""))
			return "M";
		return size;
	}

	/** NOTE: Returns 0 if size() is bad... */
	public int sizeInt()
	{
		String key = getSize();
		return sizeIntForSize(key);
	}

	public int sizeIntForSize(String key)
	{
		return intForSize(key);
	}

	public int modForSize()
	{
		return ((int[])s_sizes.get(getSize()))[1];
	}

	public int grappleModForSize()
	{
		return ((int[])s_sizes.get(getSize()))[2];
	}

	public int hideModForSize()
	{
		return -grappleModForSize();
	}

	public int strModForSize()
	{
		String size = race.getSize();
		int strmod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
			{
				strmod += 2;
				size = "T";
			}
			else if (size.equals("T"))
			{
				strmod += 4;
				size = "S";
			}
			else if (size.equals("S"))
			{
				strmod += 4;
				size = "M";
			}
			else if (size.equals("M"))
			{
				strmod += 8;
				size = "L";
			}
			else if (size.equals("L"))
			{
				strmod += 8;
				size = "H";
			}
			else if (size.equals("H"))
			{
				strmod += 8;
				size = "G";
			}
			else if (size.equals("G"))
			{
				strmod += 8;
				size = "C";
			}
			else
				break;
		}
		return strmod;
	}

	public int conModForSize()
	{
		String size = race.getSize();
		int conmod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
				size = "T";
			else if (size.equals("T"))
				size = "S";
			else if (size.equals("S"))
			{
				conmod += 2;
				size = "M";
			}
			else if (size.equals("M"))
			{
				conmod += 4;
				size = "L";
			}
			else if (size.equals("L"))
			{
				conmod += 4;
				size = "H";
			}
			else if (size.equals("H"))
			{
				conmod += 4;
				size = "G";
			}
			else if (size.equals("G"))
			{
				conmod += 4;
				size = "C";
			}
			else
				break;
		}
		return conmod;
	}

	public int dexModForSize()
	{
		String size = race.getSize();
		int dexmod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
			{
				dexmod -= 2;
				size = "D";
			}
			else if (size.equals("D"))
			{
				dexmod -= 2;
				size = "T";
			}
			else if (size.equals("T"))
			{
				dexmod -= 2;
				size = "S";
			}
			else if (size.equals("S"))
			{
				dexmod -= 2;
				size = "M";
			}
			else if (size.equals("M"))
			{
				dexmod -= 2;
				size = "L";
			}
			else if (size.equals("L"))
			{
				dexmod -= 2;
				size = "H";
			}
			else if (size.equals("H"))
				break;
			else if (size.equals("G"))
				break;
			else
				break;
		}
		return dexmod;
	}

	public int naturalArmorModForSize()
	{
		String size = race.getSize();
		int naturalarmormod = 0;
		for (int x = 0; x < race.sizesAdvanced(totalHitDice()); x++)
		{
			if (size.equals("F"))
				size = "D";
			else if (size.equals("D"))
				size = "T";
			else if (size.equals("T"))
				size = "S";
			else if (size.equals("S"))
				size = "M";
			else if (size.equals("M"))
			{
				naturalarmormod += 2;
				size = "L";
			}
			else if (size.equals("L"))
			{
				naturalarmormod += 3;
				size = "H";
			}
			else if (size.equals("H"))
			{
				naturalarmormod += 4;
				size = "G";
			}
			else if (size.equals("G"))
			{
				naturalarmormod += 5;
				size = "C";
			}
			else
				break;
		}
		return naturalarmormod;
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
			PCClass aClass = (PCClass)e.next();
			if (aClass.getDisplayClassName().equalsIgnoreCase(aString))
			// IgnoreCase needed for class checks in getVariableValue ...  ---arcady 10/6/2001
				return aClass;
		}
		return null;
	}

	public PCClass getClassKeyed(String aString)
	{
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.getKeyName().equals(aString))
				return aClass;
		}
		return null;
	}

	public PCTemplate getTemplateKeyed(String aString)
	{
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)e.next();
			if (aTemplate.getKeyName().equals(aString))
				return aTemplate;
		}
		return null;
	}

	public PCTemplate getTemplateNamed(String aName)
	{
		for (Iterator e = templateList.iterator(); e.hasNext();)
		{
			PCTemplate aTemplate = (PCTemplate)e.next();
			if (aTemplate.getName().equals(aName))
				return aTemplate;
		}
		return null;
	}

	public PCClass getSpellClassAtIndex(int ix)
	{
		PCClass aClass = null;
		for (Iterator i = classList.iterator(); i.hasNext();)
		{
			aClass = (PCClass)i.next();
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
			String aString = (String)e.next();
			if (aString.startsWith(variableString))
			{
				e.remove();
			}
		}
	}

	public boolean hasVariable(String variableString)
	{
		StringTokenizer aTok;
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			aTok = new StringTokenizer((String)e.next(), "|", false);
			aTok.nextToken(); //src
			aTok.nextToken(); //subSrc
			if (((String)aTok.nextToken()).equalsIgnoreCase(variableString)) //nString
				return true;
		}

		return false;
	}

	public Float getVariable(String variableString, boolean isMax, boolean includeBonus, String matchSrc, String matchSubSrc)
	{
		Float value = new Float(0.0);
		int found = 0;
		String vString = null;
		StringTokenizer aTok = null;
		String src = null;
		String subSrc = null;
		String nString = null;
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			vString = (String)e.next();
			aTok = new StringTokenizer(vString, "|", false);
			src = aTok.nextToken();
			if (matchSrc.length() > 0 && !src.equals(matchSrc))
				continue;
			subSrc = aTok.nextToken();
			if (matchSubSrc.length() > 0 && !subSrc.equals(matchSubSrc))
				continue;
			nString = aTok.nextToken();
			if (nString.equals(variableString))
			{
				String sString = aTok.nextToken();
				Float newValue = getVariableValue(sString, src, subSrc);
				if (found == 0)
					value = newValue;
				else if (isMax)
					value = new Float(Math.max(value.doubleValue(), newValue.doubleValue()));
				else
					value = new Float(Math.min(value.doubleValue(), newValue.doubleValue()));
				found = 1;
				if (!loopVariable.equals(""))
				{
					while (loopValue > decrement)
					{
						loopValue -= decrement;
						value = new Float(value.doubleValue() + getVariableValue(sString, src, subSrc).doubleValue());
					}
					loopValue = 0;
					loopVariable = "";
				}
			}
		}
		if (includeBonus)
		{
			int i = getTotalBonusTo("VAR", variableString, true);
			value = new Float(value.doubleValue() + i);
		}
		return value;
	}

	private ArrayList removeEqType(ArrayList aList, String aString)
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

	private ArrayList removeNotEqType(ArrayList aList, String aString)
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

	private ArrayList addEqType(ArrayList aList, String aString)
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


	public int movement(int x)
	{
		int move = getRace().getMovement(x).intValue(); // get racial base movement
		boolean isMedium = getRace().getMovement(x).intValue() == 30;
		ArrayList aArrayList = getEquipmentOfType("Armor", 1); // get a list of all equipped Armor
		Equipment eq = null;
		Iterator e = null;
		int bonus = 0;
		int i = 0;
		i = Globals.loadTypeForStrength(adjStats(Globals.STRENGTH), totalWeight());
		switch (i)
		{
			case 1:
			case 2:
				move -= (move / 15) * 5; // can we just divide move/3 here?
				break;
			case 3:
				move = 0;
		}
		if (aArrayList.size() == 0 && i == 0) // assume any armor or load cancels MOVE:
		{
			for (e = classList.iterator(); e.hasNext();)
			{
				PCClass aClass = (PCClass)e.next();
				move += Integer.parseInt(aClass.getMoveForLevel(aClass.getLevel().intValue())); // this movement is cumulative
			}
		}
		else // assume BONUS:MOVE can be applied to less than label armor
		{
			String loads = "LIGHT MEDIUMHEAVY OVERLOAD";
			// i will equal the greater of encumberance load type or armor type
			for (e = aArrayList.iterator(); e.hasNext();)
			{
				Equipment e1 = (Equipment)e.next();
				if (e1.isHeavy())
					i = 2;
				else if (e1.isMedium())
					i = Math.max(i, 1);
				else if (e1.isLight())
					i = Math.max(i, 0);
				else
					i = 3;
				int pos = Math.max(0, e1.moveString().lastIndexOf(","));
				if (pos > 0)
				{
					if (isMedium)
						move = Math.min(move, Integer.parseInt(e1.moveString().substring(0, pos)));
					else
						move = Math.min(move, Integer.parseInt(e1.moveString().substring(pos + 1)));
				}
			}
		}
		bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVYOVERLOAD", false); // always get this bonus
		switch (i)
		{
			// NOTE: no breaks on pupose! These are cumulative and cascade together!!!!!
			case 0:
				bonus += getTotalBonusTo("MOVE", "LIGHT", false);
			case 1:
				bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUM", false);
			case 2:
				bonus += getTotalBonusTo("MOVE", "LIGHTMEDIUMHEAVY", false);
		}
		move += bonus;
		return move;
	}


	public int totalAC()
	{
		return acMod() + getRace().getStartingAC().intValue() + naturalArmorModForSize() + modToFromEquipment("AC");
	}

	public int flatFootedAC()
	{
		int i = totalAC();
		int dexBonus = calcStatMod(Globals.DEXTERITY);
		int maxDex = modToFromEquipment("MAXDEX");

		for (Iterator e = getSpecialAbilityList().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.endsWith("Dex bonus to AC)"))
				return i;
		}
		if (maxDex < dexBonus)
		{
			dexBonus = maxDex;
		}
		return i - dexBonus;
	}

	public int acModFromShield()
	{
		int bonus = 0;

		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() && eq.isShield())
			{
				bonus += eq.getAcMod().intValue();
			}
		}
		return bonus;
	}

	public int acModFromArmor()
	{
		int bonus = 0;

		for (Iterator mapIter = equipmentList.values().iterator(); mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() && eq.isArmor())
			{
				bonus += eq.getAcMod().intValue();
			}
		}
		return bonus;
	}

	public int acMod()
	{
		int acmod = modForSize();
		int max = modToFromEquipment("MAXDEX");
		int ab = calcStatMod(Globals.DEXTERITY);
		if (ab > max)
			ab = max;
		acmod += ab;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			final int level = aClass.getLevel().intValue();
			acmod += Integer.parseInt(aClass.getACForLevel(level));
		}
		for (int x = 0; x < templateList.size(); x++)
			acmod += ((PCTemplate)templateList.get(x)).getNaturalArmor(totalLevels(), totalHitDice(), getSize());
		acmod += getTotalBonusTo("COMBAT", "AC", true);
		return acmod;
	}

	public int calcStatMod(int stat)
	{
		return (adjStats(stat) / 2) - 5;
	}

	public int initiativeMod()
	{
		int initmod = calcStatMod(Globals.DEXTERITY) + getRace().getInitMod().intValue() + getRace().bonusTo("COMBAT", "Initiative");
		initmod += getFeatBonusTo("COMBAT", "Initiative", true);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			initmod += aClass.initMod();
			initmod += aClass.getBonusTo("COMBAT", "Initiative", aClass.getLevel().intValue());
		}
		int bonus = getEquipmentBonusTo("COMBAT", "Initiative", false);
		initmod += bonus;
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
		int nonTotal = getRace().getBAB();
		for (int i = 0; i < classList.size(); i++)
		{
			PCClass aClass = (PCClass)classList.get(i);
			int b = aClass.baseAttackBonus(index);
			int c = aClass.attackCycle(index);
			int d = ((Integer)ab.get(c)).intValue() + b;
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
				attackString.append("/");
			if (mod > 0)
				attackString.append("+");
			attackString.append(mod);
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
		// 0 = LG, 3 = NG, 6 = CG
		// 1 = LN, 4 = TN, 7 = CN
		// 2 = LE, 5 = NE, 8 = CE
		if (this.race.canBeAlignment(Integer.toString(index)))
		{
			alignment = index;
		}
		else
		{
			if ((bLoading) && (index != 9))
			{
				JOptionPane.showMessageDialog(null, "Invalid alignment. Setting to <none selected>", "PCGen", JOptionPane.INFORMATION_MESSAGE);
				alignment = 9;
			}
			//TODO raise an exception, once I define one. Maybe
			//ArrayIndexOutOfBounds?
		}
	}

	// 		getRace().setMoveRates(String moveType, int moveRatei, int moveFlag)
	private void setMoveRates(PCTemplate inTmpl) {
		movementTypes = inTmpl.getMovementTypes();
		movements = inTmpl.getMovements();

		if(movements != null)
		{
			for( int i = 0; i < movements.length; i++) {
				boolean movesetter = getRace().setMoveRates(movementTypes[i], movements[i].intValue(), inTmpl.getMoveRatesFlag());
			}
		}
	}
	
	public void addToQualifyList(PObject fromThere) {
		String tempQualifyList = fromThere.getQualifyString();
		StringTokenizer aaTok = new StringTokenizer(tempQualifyList, "|", false);

		while (aaTok.hasMoreTokens())
			qualifyArrayList.add(aaTok.nextToken());
	}

	public void cutFromQualifyList(PObject fromThere) {
		String tempQualifyList = fromThere.getQualifyString();
		StringTokenizer aaTok = new StringTokenizer(tempQualifyList, "|", false);

		while (aaTok.hasMoreTokens()) {
			String aaaString = aaTok.nextToken();
			if(qualifyArrayList.contains(aaaString)) {
				qualifyArrayList.remove(aaaString);
			}
		}
	}

	public void addTemplate(PCTemplate inTmpl)

	{
		templateList.add(inTmpl);

		subRace = inTmpl.getSubRace();
		favoredClasses.add(inTmpl.getFavoredClass());
		setMoveRates(inTmpl);
		addToQualifyList(inTmpl);

		ArrayList templateFeats = inTmpl.feats(totalLevels(), totalHitDice(), getSize());
		int tFeats = templateFeats.size();
		for (int x = 0; x < tFeats; x++)
			modFeatsFromList((String)templateFeats.get(x), true, false);
		ArrayList templates = inTmpl.getTemplates();
		for (int y = 0; y < templates.size(); y++)
			addTemplate(Globals.getTemplateNamed((String)templates.get(y)));
	}


	public void removeTemplate(PCTemplate inTmpl)
	{
		if (inTmpl == null)
			return;
		if (subRace.equals(inTmpl.getSubRace())) subRace = "None";
		favoredClasses.remove(inTmpl.getFavoredClass());
		cutFromQualifyList(inTmpl);
		
		for (int y = 0; y < inTmpl.templatesAdded().size(); y++)
			removeTemplate(getTemplateNamed((String)inTmpl.templatesAdded().get(y)));
		for (int x = 0; x < templateList.size(); x++)
			if (((PCTemplate)templateList.get(x)).getName().equals(inTmpl.getName()))
			{
				templateList.remove(x);
				return;
			}
	}

	public void incrementClassLevel(int mod, PCClass aClass)
	{
		if (mod > 0)
		{
			if (!aClass.isQualified())
			{
				return;
			}
			if (aClass.isMonster() && totalHitDice() >= race.maxHitDiceAdvancement())
			{
				JOptionPane.showMessageDialog(null, "Cannot increase Monster Hit Dice for this character beyond " + race.maxHitDiceAdvancement() + ". This characters current number of Monster Hit Dice is " + totalHitDice(), "PCGen", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		PCClass bClass = getClassNamed(aClass.getName());
		if (bClass == null && mod > 0)
		{
			bClass = (PCClass)aClass.clone();
			classList.add(bClass);
			languages.addAll(bClass.getLanguageAutos());
			getAutoWeaponProfs();		//does this fix it?
		}
		if (bClass == null)
			return;
		if (mod > 0)
		{
			for (int i = 0; i < mod; i++)
				bClass.addLevel(false);
		}
		else if (mod < 0)
		{
			for (int i = 0; i < -mod; i++)
				bClass.subLevel();
		}
		if (bClass.getBonusTo("DOMAIN", "NUMBER", bClass.getLevel().intValue()) > 0)
		{
			bClass = getClassNamed("Domain");
			if (bClass != null)
				bClass.setLevel(new Integer(0));
		}
		ArrayList templateFeats = null;
		PCTemplate aTemplate = null;
		for (int y = 0; y < templateList.size(); y++)
		{
			aTemplate = (PCTemplate)templateList.get(y);
			templateFeats = aTemplate.feats(totalLevels(), totalHitDice(), getSize());

			for (int x = 0; x < templateFeats.size(); x++)
				modFeatsFromList((String)templateFeats.get(x), true, false);
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

	private void PCG_adjustHpRolls(int increment)
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
		final int iConMod = calcStatMod(Globals.CONSTITUTION);

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

	public int getTotalBonusTo(String bonusType, String bonusName, boolean stacks)
	{
		int bonus = 0;
		for(Iterator i =bonusMap.keySet().iterator(); i.hasNext();)
                {
                    String aKey = i.next().toString();
                    bonusMap.put(aKey,"0");
                }
		try
		{
			getClassBonusTo(bonusType, bonusName);
			getEquipmentBonusTo(bonusType, bonusName, stacks);
			getFeatBonusTo(bonusType, bonusName, stacks);
			getDomainBonusTo(bonusType, bonusName);
			getRace().bonusTo(bonusType, bonusName);

			if (getDeity() != null)
			{
				getDeity().bonusTo(bonusType, bonusName);
			}

			if (bonusType.startsWith("WEAPONPROF="))
			{
				getWeaponProfBonusTo(bonusType.substring(11), bonusName);
			}
//			System.out.println(bonusType+","+bonusName);
                  ArrayList aList = new ArrayList(bonusMap.values());
//			ArrayList bList = new ArrayList(bonusMap.keySet());
			for(int i=0;i<aList.size();i++)
			{
				String aString = aList.get(i).toString();
//				String bString = bList.get(i).toString();
				bonus += Integer.parseInt(aString);
//				if (bonusName.equals("STR"))
//					System.out.println("BONUS="+bonus+" type="+bString);
			}
		}
		catch (Exception exc)
		{
			System.out.println("error in getTotalBonusTo " + bonusType + " " + bonusName);
		}

		return bonus;
	}

	public void SetBonusStackFor(int bonus, String bonusType)
	{
		int index=-2;
		if (bonusType!=null)
			index = Globals.GetBonusStackList().indexOf(bonusType);
		if (index==-1) // meaning, a non-stacking bonus
		{
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey==null)
				bonusMap.put(bonusType,String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(Math.max(bonus, Integer.parseInt(aKey))));
		}
		else // stacking bonuses
		{
			if (bonusType==null)
				bonusType="";
			String aKey = (String)bonusMap.get(bonusType);
			if (aKey==null)
				bonusMap.put(bonusType,String.valueOf(bonus));
			else
				bonusMap.put(bonusType, String.valueOf(bonus+Integer.parseInt(aKey)));
		}
	}

	public int adjStats(int stat)
	{
		if (stat < Globals.STRENGTH || stat > Globals.CHARISMA)
		{
			return 0;
		}
		if (isNonability(stat))
		{
			return 10;
		}

		int total = getStats()[stat];
		final String statName = statNames.substring(stat * 3, stat * 3 + 3);
		total += getTotalBonusTo("STAT", statName, false);

		if (getRace() != null)
		{
			total += getRace().getStatMod(stat);
		}
		for (int template = 0; template < templateList.size(); template++)
		{
			total += ((PCTemplate)templateList.get(template)).getStatMod(stat);
		}

		if (stat == Globals.STRENGTH)
		{
			total += strModForSize();
		}
		else if (stat == Globals.DEXTERITY)
		{
			total += dexModForSize();
		}
		else if (stat == Globals.CONSTITUTION)
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
						JOptionPane.showMessageDialog(null, "Removing unknown feat: " + aString, "PCGen", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			getRace().removeSpecialAbilitiesForRace();
			languages.removeAll(getRace().getLanguageAutos());
			weaponProfList.removeAll(getRace().weaponProfAutos());
			favoredClasses.remove(getRace().getFavoredClass());

			removeNaturalWeapons();
			for (int x = 0; x < race.templatesAdded().size(); x++)
			{
				removeTemplate(getTemplateNamed((String)race.templatesAdded().get(x)));
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
			getNaturalWeapons();
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
//						if (!this.hasFeat(aFeat.name()))  //Feats of this sort should be stackable.
//						{
						setFeats(feats + 1);
						modFeat(aString, true, aFeat.getName().endsWith("Proficiency"));

//						}
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
						JOptionPane.showMessageDialog(null, "Adding unknown feat: " + aString, "PCGen", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			getAutoLanguages();
			getAutoWeaponProfs();
			getRacialFavoredClasses();
			
			if (!importing && !dirtyFlag)
			{
				race.rollHeightWeight();
			}
			ArrayList templates = race.getTemplates();
			for (int x = 0; x < templates.size(); x++)
			{
				addTemplate(Globals.getTemplateNamed((String)templates.get(x)));
			}
		}
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
							if ((sa != null || addIt == false) && !aDesc.equals(""))
							{
								if (sa != null)
								{
									cString = new String(eString);
									dString = new String(sa.getName() + " (" + sa.getDesc() + ")");
									for (int i = 0; i < 10; i++)
									{
										cString = cString.replace((char)('0' + i), ' ');
										dString = dString.replace((char)('0' + i), ' ');
									}
								}
								if (addIt == false || (addIt && cString.equals(dString)))
									getSpecialAbilityList().remove(eString);
							}
							if (sa != null && !hasSpecialAbility(sa.getName() + " (" + sa.getDesc() + ")"))
								getSpecialAbilityList().add(sa.getName() + " (" + sa.getDesc() + ")");
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
			int sInt = Integer.parseInt(aString.substring(pos + 1));
			Iterator e = getSpecialAbilityList().iterator();
			while (e.hasNext())
			{
				cString = (String)e.next();
				if (cString.startsWith(bString))
				{
					final int nonDigit = firstNonDigit(cString, bString.length());
					if (nonDigit > bString.length())
					{
						int anInt =
							Integer.parseInt(cString.substring(bString.length(), nonDigit));
						if (addIt)
							sInt = anInt + sInt;
						else
							sInt = anInt - sInt;
						e.remove();       // remove the current element
						break;
					}
				}
			}
			e = null;
			aTok = new
				StringTokenizer(aString.substring(0, pos),
					"%", true);
			StringBuffer newAbility = new StringBuffer();
			while (aTok.hasMoreTokens())
			{
				String nextTok = aTok.nextToken();
				if (nextTok.equals("%"))
					newAbility.append(Integer.toString(sInt));
				else
					newAbility.append(nextTok);
			}
			getSpecialAbilityList().add(newAbility.toString());
		}
		catch (NumberFormatException nfe)
		{
			System.out.println("Trapped number format exception for: '" +
				aString + "' check LST files.");
		}
	}

	public int totalLevels()
	{
		int i = 0;
		int totalLevels = 0;
//totalLevels = race.hitDice();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			if (!aClass.isMonster())
				totalLevels += aClass.getLevel().intValue();
		}
		return totalLevels;
	}

	public int totalPCLevels()
	{
		int i = 0;
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
		int i = 0;
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
		int i = 0;
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
		float hitDieRatio = (float)totalHitDice() / race.hitDice();
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
		String DR = race.getDR();
		for (int x = 0; x < templateList.size(); x++)
			if (!((PCTemplate)templateList.get(x)).getDR(totalLevels(), totalHitDice(), getSize()).equals(""))
				DR += "," + ((PCTemplate)templateList.get(x)).getDR(totalLevels(), totalHitDice(), getSize());
		return PCTemplate.addDR(DR);
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
		StringBuffer classStringBuffer = new StringBuffer();
		int i, x = 0;
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
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
				return true;
		}
		return false;
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
		for (Iterator e = vFeatList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
				return true;
		}
		return false;
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
		for (Iterator e = featAutoList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
				return true;
		}
		return false;
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
		for (Iterator e = aggregateFeatList().iterator(); e.hasNext();)
		{
			final Feat aFeat = (Feat)e.next();
			if (aFeat.getName().equalsIgnoreCase(featName))
				return aFeat;
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
			if (!aClass.getSpellType().equals("None") && aClass.getLevel().intValue() >= minLevel)
				return true;
		}
		return false;
	}

	public boolean isSpellCastermax(int maxLevel)
	{
		for (Iterator e1 = classList.iterator(); e1.hasNext();)
		{
			final PCClass aClass = (PCClass)e1.next();
			if (!aClass.getSpellType().equals("None") && aClass.getLevel().intValue() <= maxLevel)
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
		int i = 0, retVal = addIt?1:0;
		String subName = "";
		// if a feat named featName doesn't exist, and featName contains a (blah) descriptor, try removing it.
		Feat aFeat = getFeatNamed(featName);
		String oldName = featName;
		if (aFeat == null && featName.endsWith(")"))
		{
			subName = featName.substring(featName.indexOf("(") + 1, featName.lastIndexOf(")")); //we want what is inside the outermost parens.
			featName = featName.substring(0, featName.indexOf("(") - 1);
			aFeat = getFeatNamed(featName);
		}

		if ((addIt) && (aFeat == null))
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
				aFeat = (Feat)aFeat.clone();
			else
			{
				return retVal;
			}
			featList.add(aFeat);
		}


		// could not find feat:
		// if addIt is false, this means character does not have feat
		// if addIt is true, this means no global feat exists
		if (aFeat == null)
		{
			return retVal;
		}

		// how many sub-choices to make
		int j = (int)(aFeat.associatedList().size() * aFeat.getCost()) + feats;
		String choiceType = "";
		if (aFeat.getChoiceString().lastIndexOf('|') > -1)
			choiceType = aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf('|'));

		// for weapon prof feats, set the associated list to the weapon prof names
		if (Globals.getWeaponTypes().contains(choiceType))
		{

			Set weaponProfs = getWeaponProfs(choiceType);
			j += weaponProfs.size() - (int)(aFeat.associatedList().size() * aFeat.getCost());
			for (Iterator setIter = weaponProfs.iterator(); setIter.hasNext();)
			{
				WeaponProf aProf = (WeaponProf)setIter.next();
				if (!aFeat.associatedList().contains(aProf.getName()))
					aFeat.associatedList().add(aProf.getName());
			}
		}

		// process ADD tags from the feat definition
		if (addIt == false)
			aFeat.modAdds(addIt);

		if (addAll == false)
		{
			if (subName == "")// Allow sub-choices
				aFeat.modChoices(addIt);
			else
			{
				if (addIt && !aFeat.associatedList().contains(subName))
					aFeat.associatedList().add(subName);
				else if (!addIt && aFeat.associatedList().contains(subName))
				{
					aFeat.associatedList().remove(subName);
				}
			}
		}
		else
		{
			if (aFeat.getChoiceString().lastIndexOf("|") > -1 &&
				Globals.getWeaponTypes().contains(aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf("|"))))
				addWeaponProf(aFeat.getChoiceString().substring(0, aFeat.getChoiceString().lastIndexOf("|")));
		}


		if (aFeat.isMultiples() && addAll == false)
			retVal = (aFeat.associatedList().size() > 0)? 1:0;

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
			j++;
		else if (addIt && !aFeat.isMultiples())
			j--;
		else
		{
			int associatedListSize = 0;
			for (Iterator e1 = aggregateFeatList().iterator(); e1.hasNext();)
			{
				Feat myFeat = (Feat)e1.next();
				if (myFeat.getName().equals(aFeat.getName()))
					associatedListSize = myFeat.associatedList().size();
			}

			j -= (int)(associatedListSize * aFeat.getCost());
		}
		if (addAll == false && !aFeat.getName().equals("Spell Mastery"))
			setFeats(j);
		return retVal;
	}

	/**
	 * Add multiple feats from a String list separated by commas.
	 */
	public void modFeatsFromList(String aList, boolean addIt, boolean all)
	{
		if (totalLevels() == 0)
		{
			featList.clear();
			return;
		}
		StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			Feat aFeat = getFeatNamed(aString);
			StringTokenizer bTok = null;
			if (aFeat == null)
			{
				// does not already have feat
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
				{
					// could not find Feat
					bTok = new StringTokenizer(aString, "()", true);
					String bString = bTok.nextToken();
					bTok = new StringTokenizer(aString.substring(bString.length() + 1, aString.lastIndexOf(")")), ",", false);
					aString = bString.replace('(', ' ').replace(')', ' ').trim();
				}
				else
				{
					// add the Feat found, as a CharacterFeat
					aFeat = (Feat)aFeat.clone();
					featList.add(aFeat);
				}
			}
			if (aFeat == null)
			{
				// if we still haven't found it, try a different string
				if (addIt == false)
					return;
				aFeat = Globals.getFeatNamed(aString);
				if (aFeat == null)
					return;
				aFeat = (Feat)aFeat.clone();
				featList.add(aFeat);
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
								aFeat.associatedList().add(wp.getName());
							else
								aFeat.associatedList().remove(wp.getName());
						}
					}
					else
					{
						if (addIt)
							aFeat.associatedList().add(aString);
						else
							aFeat.associatedList().remove(aString);
					}
				}
				if (aFeat.getName().endsWith("Weapon Proficiency"))
				{
					for (Iterator e = aFeat.associatedList().iterator(); e.hasNext();)
					{
						String wprof = (String)e.next();
						WeaponProf wp = Globals.getWeaponProfNamed(wprof);
						if (wp != null)
							addWeaponProf(wprof);
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

	public int getBonus(int type, boolean addBonuses)
	{
		// 0 = attack bonus; 1 = fort; 2 = reflex; 3 = will; 4 = Monk
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
					bonus += aClass.baseAttackBonus(0);
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
//      case 4: if (aClass.name().equals("Monk"))
//            bonus+=aClass.baseAttackBonus(0);
//          break;
			}
		}
		return bonus;
	}

	public int baseAttackBonus(int type)
	{
		int bonus = getRace().getBAB();
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			bonus += aClass.baseAttackBonus(type);
		}
		return bonus;
	}

	public int getDomainMax()
	{
		int i = getTotalBonusTo("DOMAIN", "NUMBER", false);
		return i;
	}

	public void setDomainNumber(Domain aDomain, int index)
	{
		if (index < 0)
			return;
		int i = getDomainMax();
		if (index >= i)
		{
			JOptionPane.showMessageDialog(null, "This character can only have " + new Integer(i) + " domains.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		if (domainList.size() <= index)
			domainList.add(aDomain);
		else
		{
			domainList.set(index, aDomain);
		}
		modDomainClass(true);
		PCClass aClass = getClassNamed("Domain");
		if (aClass != null)
		{
			aClass.setLevel(new Integer(0));
		}
	}

	public void addDomainNamed(String domainName)
	{
		Domain aDomain = Globals.getDomainNamed(domainName);
		if (aDomain != null)
			domainList.add(aDomain);
	}

	public Domain getDomainNamed(String domainName)
	{
		for (int i = 0; i < domainList.size(); i++)
		{
			Domain aDomain = (Domain)domainList.get(i);
			if (aDomain.getName().equals(domainName))
				return aDomain;
		}
		return null;
	}

	public int getDomainIndex(String domainName)
	{
		for (int i = 0; i < domainList.size(); i++)
		{
			Domain aDomain = (Domain)domainList.get(i);
			if (aDomain.getName().equals(domainName))
				return i;
		}
		return -1;
	}

	public void addDomainKeyed(String domainName)
	{
		Domain aDomain = Globals.getDomainKeyed(domainName);
		if (aDomain != null)
			domainList.add(aDomain);
	}

	public void modDomainClass(boolean addIt)
	{
		boolean flag = false;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.getName().startsWith("Domain"))
			{
				if (addIt == false)
					e.remove();
				flag = true;
				break;
			}
		}
		if (addIt && flag == false)
		{
			PCClass aClass = Globals.getClassNamed("Domain");
			if (aClass != null)
			{
				aClass = (PCClass)aClass.clone();
				aClass.setLevel(new Integer(0));
				classList.add(aClass);
			}
		}
	}

	public String domainClassName()
	{
		StringBuffer aString = new StringBuffer("Domain (");
		for (int i = 0; i < domainList.size(); i++)
		{
			if (i > 0)
				aString.append(",");
			aString.append(((Domain)domainList.get(i)).getName());
		}
		aString.append(")");
		return aString.toString();
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
		SortedSet autoLangs = new TreeSet();
		autoLangs.addAll(getRace().getLanguageAutos());
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
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
			Equipment anEquip = (Equipment)e.next();
			equipmentList.put(anEquip.getKeyName(), anEquip);
		}
	}

	public SortedSet getRacialFavoredClasses()
	{
		favoredClasses.add(getRace().getFavoredClass());
		return favoredClasses;
	}

	public boolean addFavoredClass(String aString)
	{
		if (!favoredClasses.contains(aString))
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

	public SortedSet getFavoredClass()
	{
		return favoredClasses;
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
			PCClass aClass = (PCClass)e.next();
			if (!aList.contains(aClass.getDisplayClassName()) && (!aList.contains(aClass.toString())) &&
				!aClass.getName().startsWith("Domain") && !aClass.isPrestige())
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

				PCClass aClass = (PCClass)getClassDisplayNamed((String)e.next());
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


	public SortedSet getBonusLanguages()
	{
		SortedSet bonusLangs = new TreeSet();
		String aLang = null;
		Iterator e = null;
		for (e = getRace().getLanguageBonus().iterator(); e.hasNext();)
		{
			aLang = (String)e.next();
			if (aLang.equals("ALL"))
			{
				bonusLangs.addAll(Globals.getLanguageSet());
			}
			else
			{
				if (Globals.getLanguageSet().contains(aLang))
				{
					bonusLangs.add(aLang);
				}
			}
		}

		Collection classBonusLangs = null;
		for (e = classList.iterator(); e.hasNext();)
		{
			classBonusLangs = ((PCClass)e.next()).getLanguageBonus();
			if (Globals.getLanguageSet().containsAll(classBonusLangs))
			{
				bonusLangs.addAll(classBonusLangs);
			}
			else //Slow method kept, just in case...
			{
				for (Iterator e1 = classBonusLangs.iterator(); e1.hasNext();)
				{
					aLang = (String)e1.next();
					if (Globals.getLanguageSet().contains(aLang))
					{
						bonusLangs.add(aLang);
					}
				}
			}
		}
		bonusLangs.removeAll(languages);
		return bonusLangs;
	}

	public void addLanguage(String aString, boolean filter)
	{
		if (!filter || Globals.getLanguageSet().contains(aString))
		{
			languages.add(aString);
		}
	}

	/**
	 * Return the total number of languages that the player character can
	 * know.  This includes extra languages from intelligence, speak
	 * language skill, and race.
	 */
	public int languageNum()
	{
		int i = adjStats(Globals.INTELLIGENCE) / 2 - 5;
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
		return i;
	}

	public TreeSet getWeaponProfs(String type)
	{
		TreeSet result = new TreeSet();
		SortedSet alreadySeen = new TreeSet();
		for (Iterator e = getRace().getWeaponProfs().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			String typeString = aTok.nextToken();
			if (typeString.equals(type))
			{
				String wpString = aTok.nextToken();
				WeaponProf aProf = Globals.getWeaponProfNamed(wpString);
				if (aProf != null)
				{
					if (getWeaponProfList().contains(aProf.getName()))
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
			WeaponProf aProf = (WeaponProf)e.next();
			if (aProf.getType().equalsIgnoreCase(type) &&
				!alreadySeen.contains(aProf) &&
				getWeaponProfList().contains(aProf.getName()))
				result.add(aProf);
		}
		return result;
	}

	public SortedSet getAutoWeaponProfs()
	{
		SortedSet results = new TreeSet();
		Iterator e = null;
		for (e = getRace().weaponProfAutos().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (Globals.getWeaponTypes().contains(aString))
			{
				for (Iterator e1 = Globals.getWeaponProfList().iterator();
						 e1.hasNext();)
				{
					WeaponProf aProf = (WeaponProf)e1.next();
					if (aProf.getType().equalsIgnoreCase(aString))
					{
						results.add(aProf.getName());
						addWeaponProf(aProf.getName());
					}
				}
			}
			else
			{
				results.add(aString);
				addWeaponProf(aString);
			}
		}

		for (e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			final String sizeString = "FDTSMLHGC";
			for (Iterator e1 = aClass.getWeaponProfAutos().iterator(); e1.hasNext();)
			{
				String aString = (String)e1.next();
				int x = aString.lastIndexOf(",");
				boolean flag = (x == -1);
				if (flag == false && race != null)
				{
					String eString = aString.substring(x + 1);
					int s = sizeInt();
					for (int i = 0; i < eString.length(); i++)
						if (sizeString.lastIndexOf(eString.charAt(i)) == s)
							flag = true;
					aString = aString.substring(0, x);
				}
				if (flag == true)
				{
					if (Globals.getWeaponTypes().contains(aString))
					{
						for (Iterator e2 = Globals.getWeaponProfList().iterator();
								 e2.hasNext();)
						{
							WeaponProf aProf = (WeaponProf)e2.next();
							if (aProf.getType().equalsIgnoreCase(aString))
							{
								results.add(aProf.getName());
								addWeaponProf(aProf.getName());
							}
						}
					}
					else
					{
						results.add(aString);
						addWeaponProf(aString);
					}
				}
			}
		}
		return results;
	}

	public SortedSet getBonusWeaponProfs()
	{
		SortedSet results = new TreeSet(getRace().getWeaponProfBonus());
		bonusWeaponChoices = 0;
		if (results.size() > 0)
			bonusWeaponChoices = 1;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (results.addAll(aClass.getWeaponProfBonus()))
				bonusWeaponChoices++;
		}
		return results;
	}

	public void addWeaponProf(String aString)
	{
		if (Globals.getWeaponTypes().contains(aString))
		{
			for (Iterator e = Globals.getWeaponProfList().iterator(); e.hasNext();)
			{
				WeaponProf aProf = (WeaponProf)e.next();
				if (aProf.getType().equalsIgnoreCase(aString))
					addWeaponProf(aProf.getName());
			}
			return;
		}
		WeaponProf wp = Globals.getWeaponProfNamed(aString);
		if (wp != null)
		{
			Feat aFeat = getFeatNamed(wp.getType() + " Weapon Proficiency");
			if (aFeat != null)
			{
				if (!aFeat.associatedList().contains(aString))
				{
					aFeat.associatedList().add(aString);
				}
			}
			else
			{
				aFeat = Globals.getFeatNamed(wp.getType() + " Weapon Proficiency");
				if (aFeat != null)
				{
					aFeat = (Feat)aFeat.clone();
					aFeat.associatedList().add(aString);
					//This line removed to handle automatic feats. JKJK
					//featList.add(aFeat);
				}
			}
		}
		getWeaponProfList().add(aString);
	}

	public int weaponProfNum()
	{
		int i = 0;
		Iterator e = null;
		Iterator e1 = null;
		SortedSet currentProf = (SortedSet)getWeaponProfList().clone();
		SortedSet autoProfs = getAutoWeaponProfs();
		Collection raceProfs = getRace().getWeaponProfBonus();
		if (raceProfs.size() > 0)
		{
			for (e = raceProfs.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				if (currentProf.contains(aString))
				{
					currentProf.remove(aString);
					if (!autoProfs.contains(aString))
					{
						i--;
						break;
					}
				}
			}
			i++;
		}
		for (Iterator e2 = classList.iterator(); e2.hasNext();)
		{
			PCClass aClass = (PCClass)e2.next();
			raceProfs = aClass.getWeaponProfBonus();
			for (e = raceProfs.iterator(); e.hasNext();)
			{
				String aString = (String)e.next();
				if (currentProf.contains(aString))
				{
					currentProf.remove(aString);
					i--;
					break;
				}
			}
			if (raceProfs.size() > 0)
				i++;
		}
		return i;
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
			if (Globals.isDebugMode())
				System.out.println("Cast As:" + cName);
			if (aType.equals("Any") || aType.equals(aClass.getSpellType()))
				for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
				{
					Spell aSpell = (Spell)e1.next();
					if (((school.length() == 0 || school.equals(aSpell.getSchool())) ||
						(subschool.length() == 0 || subschool.equals(aSpell.getSubschool()))) &&
						aSpell.levelForClass(cName, aClass.getName()) >= minLevel && aSpell.levelForClass(cName, aClass.getName()) <= maxLevel)
					{
						if (Globals.isDebugMode())
							System.out.println(school + "==" + aSpell.getSchool() + " " + minLevel + "==" + aSpell.levelForClass(cName, aClass.getName()));
						aArrayList.add(aSpell);
					}
				}
		}
		return aArrayList;
	}

	public Float totalWeight()
	{
		float totalWeight = 0;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			// Loop through the list of top
			if (eq.getCarried().compareTo(new Float(0)) > 0 && !eq.isHeaderParent() && eq.getParent() == null)
			{
				if (eq.getChildCount() > 0)
					totalWeight += eq.weight().floatValue() + eq.getContainedWeight().floatValue();
				else
					totalWeight += eq.weight().floatValue() * eq.getCarried().floatValue();
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
			return (wp != null && getWeaponProfList().contains(wp.getName()));
		}
		else if (eq.isArmor())
		{
			String aString = eq.typeString();
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

	// status: 1 (equipped) 2 (not equipped) 3 (none)
	public ArrayList getEquipmentOfType(String typeName, int status)
	{
		ArrayList aArrayList = new ArrayList();
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.typeStringContains(typeName) && (status == 3 ||
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
					JOptionPane.showMessageDialog(null, "No entry in weapons.lst for " + eq.profName() + ". Weapons must be in that file to equip them.", "PCGen", JOptionPane.ERROR_MESSAGE);
					if (Globals.isDebugMode())
					{
						System.out.println("Globals: " + Globals.getWeaponProfList());
						System.out.println("Prof: " + eq.profName());
					}
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
						case 0:
							break;
						case 1:
							hands += Math.max(1, eq.getHands());
							break;
						case 2:
							hands += Math.max(1, eq.getHands());
							break;
						case 3:
							hands += Math.max(2, eq.getHands());
							break;
						case 4:
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
			return aArrayList.size() < race.GetHands() + getTotalBonusTo("RING", "NUMBER", true);
		else if (typeName.equals("Weapon") || typeName.equals("Shield"))
		{
			int hands = handsFull();
			if (hands > race.GetHands())
			{
				JOptionPane.showMessageDialog(null, "Your hands are too full. Check weapons/shields already equipped.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
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
		int i = 0;
		primaryWeapon = null;
		secondaryWeapon = new Equipment[race.GetHands() - 1];
		int x = 0;
		int y = 0;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() == false)
				continue;
			if (!eq.typeStringContains("Weapon"))
				continue;
			if (eq.getHand() == Equipment.PRIMARY_HAND
				|| (eq.getHand() == Equipment.BOTH_HANDS && primaryWeapon == null)
				|| eq.getHand() == Equipment.TWOWEAPON_HANDS)
				primaryWeapon = eq;
			else if (eq.getHand() == Equipment.BOTH_HANDS && primaryWeapon != null)
				secondaryWeapon[x++] = eq;
			if (eq.getHand() == Equipment.SECONDARY_HAND)
				secondaryWeapon[x++] = eq;
			if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
				for (y = 0; y < eq.getNumberEquipped() - 1; y++)
					secondaryWeapon[x++] = eq;
		}
	}

	public int getClassBonusTo(String type, String aName)
	{
		int bonus = 0;
		int[] statBonus = new int[7];
		for (int i = 0; i < 7; i++)
			statBonus[i] = 0; // 0-5 are stat bonuses STR to CHA; 6 is stackable bonuses
		int x = 0,j = 0;
		if (aName.equals("AC"))
		{
			ArrayList aArrayList = getEquipmentOfType("Armor", 1);
			// if we have any equipped suits of armor, bonus is set to 0
			for (int i = aArrayList.size() - 1; i >= 0; i--)
			{
				Equipment eq = (Equipment)aArrayList.get(i);
				if (!eq.typeStringContains("SUIT"))
					break;
				aArrayList.remove(i);
			}
			if (aArrayList.size() > 0)
				return 0; // we have an equipped suit of armor
		}
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			// special case for "AC" which is hardcoded to not allow stacking of stat based boni
			if (aName.equals("AC"))
			{
				int[] bonii = aClass.bonusBasedOnStat(type, aName, aClass.getLevel().intValue());
				// slots 0-5 don't stack, so just take the max of previous slot and new slot
				for (j = 0; j < 6; j++)
				{
					statBonus[j] = Math.max(statBonus[j], bonii[j]);
				}
				// slot 6 does stack, so just add them
				statBonus[6] += bonii[6];
			}
			else
				bonus += aClass.getBonusTo(type, aName, aClass.getLevel().intValue());
		}
		if (aName.equals("AC"))
		{
			int tempVal = 0;
			for (int i = 0; i < 7; i++)
				tempVal += statBonus[i];
			bonus+=tempVal;
			SetBonusStackFor(tempVal,null); // this value stacks
		}
		return bonus;
	}

	public int getDomainBonusTo(String type, String aName)
	{
		int bonus = 0;
		for (Iterator e = domainList.iterator(); e.hasNext();)
		{
			bonus += ((Domain)e.next()).bonusTo(type, aName);
		}
		return bonus;
	}

	public int getEquipmentBonusTo(String type, String aName, boolean stacks)
	{
		int bonus = 0;
		if (stacks == false)
			bonus = -999;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped())
			{
				if (stacks)
					bonus += eq.bonusTo(type, aName);
				else
					bonus = Math.max(bonus, eq.bonusTo(type, aName));
			}
		}
		if (bonus == -999)
			bonus = 0;
		return bonus;
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
			int k = Math.max(1, (int)(aFeat.associatedList().size() * aFeat.getCost()));
			if (subSearch && aFeat.associatedList().size() > 0)
			{
				k = 0;
				for (Iterator f = aFeat.associatedList().iterator(); f.hasNext();)
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
		int i = 0;
		int y = 0;
		for (Iterator mapIter = equipmentList.values().iterator();
				 mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isArmor())
				return new Integer(totalAC());
		}
		i = getRace().getStartingAC().intValue() + naturalArmorModForSize() + calcStatMod(Globals.DEXTERITY);
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			i += aClass.defense(y).intValue();
			y++;
		}
		i += getFeatBonusTo("CLASS", "DEFENSE", true) + getRace().bonusTo("CLASS", "DEFENSE");
		return new Integer(i);
	}

	public Integer woundPoints()
	{
		int i = adjStats(Globals.CONSTITUTION);
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
/*Best*/  case 0:
					i += 3 + aClass.getLevel().intValue() / 2;
					break;
/*MHigh*/  case 1:
					i += 1 + aClass.getLevel().intValue() / 2;
					break;
/*MLow*/  case 2:
					i += aClass.getLevel().intValue() / 2;
					break;
/*Low*/    case 3:
					i += aClass.getLevel().intValue() / 3;
					break;
/*NPCH*/  case 4:
					i += (aClass.getLevel().intValue() + 1) / 3;
					break;
/*NPCL*/  case 5:
					i += aClass.getLevel().intValue() / 4;
					break;
/*PHigh*/  case 6:
					if (aClass.getLevel().intValue() % 3 != 0) i++;
					break;
/*PLow*/  case 7:
					i += aClass.getLevel().intValue() / 2;
					break;
/*P v3*/  case 8:
					if (aClass.getLevel().intValue() % 2 == 0) i++;
					break;
/*P v4*/  case 9:
					if (aClass.getLevel().intValue() % 4 != 0) i++;
					break;
/*P v5*/  case 10:
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
		if (aName.length() > 0 && !aName.equals("Known Spells") && spellBooks.contains(aName))
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
		if (bookName.equals("Known Spells"))
			aSpell = (Spell)Globals.getSpellNamed(spellName);
		else
			aSpell = aClass.getSpellNamed(spellName);
		if (aSpell == null)
			return "Could not find " + spellName + " for " + className;
		if (aFeatList != null)
			for (Iterator i = aFeatList.iterator(); i.hasNext();)
			{
				Feat aFeat = (Feat)i.next();
				spellLevel += aFeat.getAddSpellLevel();
			}
		int known = aClass.getKnownForLevel(aClass.getLevel().intValue(), spellLevel);
		int cast = aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel, bookName);
		boolean isDefault = bookName.equals("Known Spells");
		if (aClass.getMemorizeSpells() && !isDefault &&
			aClass.memorizedSpellForLevelBook(spellLevel, bookName) >= cast)
			return "You cannot memorize any additional spells in this list.";
		if (!aSpell.isInSpecialty(aClass.getSpecialtyList()) && (aClass.prohibitedStringContains(aSpell.getSchool()) || aClass.prohibitedStringContains(aSpell.getDescriptorList())))
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
			if (aClass.prohibitedStringContains(bSpell.getSchool()))
				return "This spell is prohibited.";
			bSpell.addToSpellBook(bookName, !isDefault);
			bSpell.selectSpellBook(bookName);
			addIt = false;
		}
		if (addIt)
		{
			if (aClass.memorizedSpellForLevelBook(spellLevel, bookName) < known ||
				(known == 0 && cast > 0) ||
				(aClass.getMemorizeSpells() && isDefault))
			{
				if (aClass.getKnownSpellsFromSpecialty() > 0)
				{
					if (!aSpell.isInSpecialty(aClass.getSpecialtyList()))
					{
						int num = aClass.getSpellsInSpecialtyForLevel(spellLevel);
						if (num < aClass.getKnownSpellsFromSpecialty())
							return "First " + aClass.getKnownSpellsFromSpecialty() + " spells known must come from specialty (" + aClass.getSpecialtyList().toString() + ")";
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

		boolean isDefault = bookName.equals("Known Spells");
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
						classFeat = Globals.getFeatNamed(classTok.nextToken());
						if (classFeat != null)
							vFeatList.add(classFeat);
					}
				}
			}
		}
		final StringTokenizer raceTok = new StringTokenizer(getRace().getVFeatList(), "|", false);
		Feat raceFeat = null;
		while (raceTok.hasMoreTokens())
		{
			raceFeat = Globals.getFeatNamed(raceTok.nextToken());
			if (raceFeat != null)
				vFeatList.add(raceFeat);
		}

//		Globals.sortPObjectList(aArrayList);
		setStableVirtualFeatList(vFeatList);
		return vFeatList;
	}

	private void setStableAutomaticFeatList(ArrayList aFeatList)
	{
		stableAutomaticFeatList = aFeatList;
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
		String aString = null;
		String subName = null;
		for (Iterator e = classList.iterator(); e.hasNext();)
		{
			final PCClass aClass = (PCClass)e.next();
			for (Iterator e1 = aClass.getFeatAutos().iterator(); e1.hasNext();)
			{
				aString = (String)e1.next();
				if (aString.lastIndexOf("|") == -1)
					continue;
				final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
				final int i = Integer.parseInt(aTok.nextToken());
				if (i > aClass.getLevel().intValue())
					continue;
				aString = aTok.nextToken();
				Feat aFeat = Globals.getFeatNamed(aString);
				Feat cFeat = null;
				if (aFeat == null && aString.endsWith(")"))
				{
					subName = aString.substring(aString.indexOf("(") + 1, aString.lastIndexOf(")")); //we want what is inside the outermost parens.
					aString = aString.substring(0, aString.indexOf("(") - 1);
					aFeat = Globals.getFeatNamed(aString);

					if (aFeat != null)
					{
						cFeat = (Feat)aFeat.clone();
						cFeat.associatedList().add(subName);
					}

				}
				else if (aFeat != null)
					cFeat = (Feat)aFeat.clone();

				if (cFeat != null)
					autoFeatList.add(cFeat);
			}
		}

//		Globals.sortPObjectList(aArrayList);
		setStableAutomaticFeatList(autoFeatList);
		return autoFeatList;
	}

	private void setStableAggregateFeatList(ArrayList aFeatList)
	{
		stableAggregateFeatList = aFeatList;
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
			Feat aFeat = (Feat)e.next();
			if (!aHashMap.containsKey(aFeat.getKeyName()))
				aHashMap.put(aFeat.getKeyName(), aFeat);
			else if (aFeat.isMultiples())
			{
				Feat bFeat = (Feat)aHashMap.get(aFeat.getKeyName());
				for (Iterator e1 = aFeat.associatedList().iterator(); e1.hasNext();)
				{
					Object anObject = e1.next();
					if (!bFeat.associatedList().contains(anObject))
						bFeat.associatedList().add(anObject);
				}
			}
		}
		for (Iterator e = featAutoList().iterator(); e.hasNext();)
		{
			Feat aFeat = (Feat)e.next();
			if (!aHashMap.containsKey(aFeat.getKeyName()))
				aHashMap.put(aFeat.getName(), aFeat);
			else if (aFeat.isMultiples())
			{
				Feat bFeat = (Feat)aHashMap.get(aFeat.getKeyName());
				for (Iterator e1 = aFeat.associatedList().iterator(); e1.hasNext();)
				{
					Object anObject = e1.next();
					if (!bFeat.associatedList().contains(anObject))
						bFeat.associatedList().add(anObject);
				}
			}
		}

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
		setDirty(false);
		return true;
	}

	public boolean load(BufferedReader input)
	{
		FileAccess fa = new FileAccess();
		importing = true;
		String aLine = "";
		try
		{
			StringTokenizer aTok = null;
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
			handleDeityLine(fa, input, deityLine);

			lastFnCalled = "loadClassSpellLine";
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				loadClassSpellLine(e, fa, input);
			}

			lastFnCalled = "loadLanguagesLine";
			loadLanguagesLine(fa, input);

			lastFnCalled = "loadWeaponProfLine";
			loadWeaponProfLine(fa, input);

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
			for (Iterator e = classList.iterator(); e.hasNext(); e.next())
			{
				loadClassesSkillLine(fa, input);
			}

			lastFnCalled = "loadExperienceAndMiscLine";
			loadExperienceAndMiscLine(fa, input);

			lastFnCalled = "loadClassSpecialtyAndSaveLines";
			loadClassSpecialtyAndSaveLines(fa, input);

			lastFnCalled = "loadTemplateLine";
			loadTemplateLine(fa, input);

			//
			// Need to adjust for older versions of PCG files here
			//

			if (PcgReadVersion < 1)
			{
				int conMod = calcStatMod(Globals.CONSTITUTION);
				PCG_adjustHpRolls(-conMod);
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
				JOptionPane.showMessageDialog(null, "Fixed illegal value in hit points. Current character hit points: " + this.hitPoints() + " not " + oldHp, "PCGen", JOptionPane.ERROR_MESSAGE);
			}

		}
		catch (Exception es)
		{
			es.printStackTrace();
			JOptionPane.showMessageDialog(null, "Problem with line:" + lastLineParsed + "\r\nin: " + lastFnCalled, "PCGen", JOptionPane.ERROR_MESSAGE);
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
			String lastLineParsed = br.readLine();
			StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
			String sVersion = aTok.nextToken();
			//if the pcg file starts with VERSION data then lets process it
			if (sVersion.equals("VERSION"))
			{
				sVersion = aTok.nextToken();
				aTok = new StringTokenizer(sVersion,".",false);
				iVersion=0;
				while(aTok.hasMoreTokens())
					iVersion = iVersion*10 + Integer.parseInt(aTok.nextToken());
			}
			else
			{
				//this is an old .pcg file (no campaign data) so just reset the input stream
				br.reset();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Could not load campaign data from character file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		if (Globals.isDebugMode())
		{
			System.out.println("PCG Version: " + iVersion);
		}

		return iVersion;
	}


	public boolean print(File aFile, BufferedWriter output)
	{
		Feat aFeat = null;
		Feat bFeat = null;
		FileInputStream aStream = null;
		populateSkills(Globals.getIncludeSkills());
		try
		{
			aStream = new FileInputStream(aFile);
			int length = (int)aFile.length();
			byte[] inputLine = new byte[length];
			aStream.read(inputLine, 0, length);
			String aString = new String(inputLine);
			StringTokenizer aTok = new StringTokenizer(aString, "\r\n", false);
			FileAccess fa = new FileAccess();
			boolean flag = true;
			skillList = (ArrayList)Globals.sortPObjectList(skillList);
			featList = (ArrayList)Globals.sortPObjectList(featList);
			for (Iterator e = classList.iterator(); e.hasNext();)
			{
				PCClass aClass = (PCClass)e.next();
				aClass.setSpellList((ArrayList)Globals.sortPObjectList(aClass.spellList()));
			}
			determinePrimaryOffWeapon();
			PCClass aClass = getClassNamed("Ranger");
			modFromArmorOnWeaponRolls();
			if (aClass != null && (getFeatNamed("Ambidexterity") == null || getFeatNamed("Two-Weapon Fighting") == null))
			{
				if (getFeatNamed("Ambidexterity") == null)
					aFeat = Globals.getFeatNamed("Ambidexterity");
				if (getFeatNamed("Two-Weapon Fighting") == null)
					bFeat = Globals.getFeatNamed("Two-Weapon Fighting");
				for (Iterator mapIter = equipmentList.values().iterator();
						 mapIter.hasNext();)
				{
					Equipment eq = (Equipment)mapIter.next();
					if (eq.isArmor() && eq.isEquipped() && (eq.isHeavy() || eq.isMedium()))
					{
						aFeat = null;
						bFeat = null;
						break;
					}
				}
				if (aFeat != null)
					featList.add(aFeat);
				if (bFeat != null)
					featList.add(bFeat);
			}
			boolean inPipe = false;
			String tokString = "";
			while (aTok.hasMoreTokens())
			{
				String aLine = aTok.nextToken();
				if (!inPipe && aLine.lastIndexOf("|") == -1)
				{
					replaceToken(aLine, output);
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
					int count = bTok.countTokens();
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
		}
		catch (Exception exc)
		{
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
		if (aFeat != null)
			featList.remove(aFeat);
		if (bFeat != null)
			featList.remove(bFeat);

		csheetTag2 = "\\";
		return true;
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
				if (aString.substring(1).startsWith("WEAPON"))
				{
					ArrayList aArrayList = getEquipmentOfType("Weapon", 3);
					if (Integer.parseInt(aString.substring(aString.length() - 1)) >= aArrayList.size())
						canWrite = false;
					return 0;
				}
				if (aString.substring(1).startsWith("DOMAIN"))
				{
					canWrite = (Integer.parseInt(aString.substring(7)) <= domainList.size());
					return 0;
				}
				if (aString.substring(1).startsWith("SPELLLISTBOOK"))
				{
					return replaceTokenSpellListBook(aString);
				}
				if (aString.substring(1).startsWith("VAR."))
				{
					int cmp = 0;
					StringTokenizer aTok = new StringTokenizer(aString.substring(5), ".", false);
					String varName = aTok.nextToken();
					String bString = "EQ";
					if (aTok.hasMoreTokens())
						bString = aTok.nextToken();
					String value = "0";
					if (aTok.hasMoreTokens())
						value = aTok.nextToken();
					Float varval = getVariable(varName, true, true, "", "");
					Float valval = getVariableValue(value, "", "");
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
				fa.write(output, java.text.DateFormat.getDateInstance().format(new Date()));
			else if (aString.equals("EXPORT.TIME"))
				fa.write(output, java.text.DateFormat.getTimeInstance().format(new Date()));
			else if (aString.equals("BIO"))
				fa.write(output, bio);
			else if (aString.equals("DESC"))
				fa.write(output, description);
			else if (aString.equals("NAME"))
				fa.write(output, getName());
			else if (aString.equals("RACE")) {
				String tempRaceName = getRace().getRaceOutputName();
				if(tempRaceName.equals("None")) tempRaceName = getRace().getName();

				if (subRace.equals("None")) {
					fa.write(output, tempRaceName);
				} else {
					fa.write(output, tempRaceName + " (" + subRace + ")");
				}
			}
			else if (aString.equals("AGE"))
				fa.write(output, new Integer(age).toString());
			else if (aString.equals("HEIGHT"))
				fa.write(output, new Integer(height / 12).toString() + "' " + new Integer(height % 12).toString() + " inches");
			else if (aString.equals("HEIGHT.FOOTPART"))
				fa.write(output, new Integer(height / 12).toString());
			else if (aString.equals("HEIGHT.INCHPART"))
				fa.write(output, new Integer(height % 12).toString());
			else if (aString.equals("WEIGHT"))
				fa.write(output, new Integer(weight).toString() + " pounds");
			else if (aString.equals("WEIGHT.NOUNIT"))
				fa.write(output, new Integer(weight).toString());
			else if (aString.equals("COLOR.EYE"))
				fa.write(output, getEyeColor());
			else if (aString.equals("COLOR.HAIR"))
				fa.write(output, getHairColor());
			else if (aString.equals("COLOR.SKIN"))
				fa.write(output, getSkinColor());
			else if (aString.equals("LENGTH.HAIR"))
				fa.write(output, getHairLength());
			else if (aString.equals("PERSONALITY1"))
				fa.write(output, getTrait1());
			else if (aString.equals("PERSONALITY2"))
				fa.write(output, getTrait2());
			else if (aString.equals("SPEECHTENDENCY"))
				fa.write(output, getSpeechTendency());
			else if (aString.equals("CATCHPHRASE"))
				fa.write(output, getCatchPhrase());
			else if (aString.equals("RESIDENCE"))
				fa.write(output, getResidence());
			else if (aString.equals("LOCATION"))
				fa.write(output, getLocation());
			else if (aString.equals("PHOBIAS"))
				fa.write(output, getPhobias());
			else if (aString.equals("INTERESTS"))
				fa.write(output, getInterests());
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
				fa.write(output, calcDR());
			else if (aString.equals("ALIGNMENT"))
			{
				replaceTokenAlignment(fa, output);
			}
			else if (aString.equals("ALIGNMENT.SHORT"))
			{
				replaceTokenAlignmentShort(fa, output);
			}
			else if (aString.equals("GENDER"))
				fa.write(output, gender);
			else if (aString.equals("HANDED"))
				fa.write(output, handed);
			else if (aString.equals("PROHIBITEDLIST"))
			{
				replaceTokenProhibitedList(fa, output);
			}
			else if (aString.startsWith("TEMPLATE"))
			{
				/* added by Emily Smirle (Syndaryl) */

				if (aString.equals("TEMPLATELIST"))
					for (Iterator e = templateList.iterator(); e.hasNext();)
					{
						PCTemplate aTemplate = (PCTemplate)e.next();

						fa.write(output, aTemplate.toString());
						if (e.hasNext())
							fa.write(output, ", ");
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

						if (i == 0)
						{
							if (aLabel.equals("NAME"))
								fa.write(output, aTemplate.toString());
							else if (aLabel.equals("STRMOD"))
							{
								if (aTemplate.isNonAbility(0))
									fa.write(output, "*");
								else
									fa.write(output, new Integer(aTemplate.getStatMod(0)).toString());
							}
							else if (aLabel.equals("DEXMOD"))
								if (aTemplate.isNonAbility(1))
									fa.write(output, "*");
								else
									fa.write(output, new Integer(aTemplate.getStatMod(1)).toString());
							else if (aLabel.equals("CONMOD"))
								if (aTemplate.isNonAbility(2))
									fa.write(output, "*");
								else
									fa.write(output, new Integer(aTemplate.getStatMod(2)).toString());
							else if (aLabel.equals("INTMOD"))
								if (aTemplate.isNonAbility(3))
									fa.write(output, "*");
								else
									fa.write(output, new Integer(aTemplate.getStatMod(3)).toString());
							else if (aLabel.equals("WISMOD"))
								if (aTemplate.isNonAbility(4))
									fa.write(output, "*");
								else
									fa.write(output, new Integer(aTemplate.getStatMod(4)).toString());
							else if (aLabel.equals("CHAMOD"))
								if (aTemplate.isNonAbility(5))
									fa.write(output, "*");
								else
									fa.write(output, new Integer(aTemplate.getStatMod(5)).toString());

							break;
							/* TODO: SA subtag, FEAT subtag, SR and DR subtag ... */
						}
					}
				}
				/* TODO: find COUNT code and add COUNT(TEMPLATES) option */
				/* end added by Emily Smirle (Syndaryl) */
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
					i = (int)aString.charAt(8) - 48;
					cmp = 2;
				}
				else
					i = (int)aString.charAt(5) - 48;
				if (aString.endsWith("LEVEL"))
					cmp = 3;
				len = 0;
				int classSize = classList.size();
				if (getClassNamed("Domain") != null)
					classSize--;
				if (classSize <= i && existsOnly)
				{
					noMoreItems = true;
					return 0;
				}
				for (Iterator e = classList.iterator(); e.hasNext();)
				{
					PCClass aClass = (PCClass)e.next();
					if (aClass.getName().equals("Domain"))
						continue;
					if (cmp == 1 && y++ > 0)
						fa.write(output, " ");
					if (aClass.getLevel().intValue() > 0)
						i--;
					if (i == -1 || cmp == 1)
					{
						len = 1;
						if (cmp < 2)
						{
							if (aClass.getSubClassName().equals("None") || aClass.getSubClassName().equals(""))
								fa.write(output, aClass.getName());
							else
								fa.write(output, aClass.getSubClassName());
						}
						if (cmp == 1 || cmp == 3)
							fa.write(output, aClass.getLevel().toString());
						if (cmp == 2)
							fa.write(output, aClass.getAbbrev());
						if (cmp != 1)
							break;
					}
				}
				System.out.println("");
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
				fa.write(output, aInt.toString() + "%");
			}
			else if (aString.equals("EXP.PENALTY"))
			{
				Float aFloat = new Float(multiclassXpMultiplier().floatValue() * 100.0);
				Integer aInt = new Integer(100 - aFloat.intValue());
				fa.write(output, aInt.toString() + "%");
			}
			else if (aString.equals("FAVOREDLIST"))
			{
				int y = 0;
				int favoredSize = favoredClasses.size();

				if (favoredSize <= 0 && existsOnly)
				{
					noMoreItems = true;
					return 0;
				}
				for (Iterator e = favoredClasses.iterator(); e.hasNext();)
				{
					if (y++ > 0)
						fa.write(output, ", ");
					String favoredString = (String)e.next();
					fa.write(output, favoredString);
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
				replaceTokenWIll(aString, fa, output);
			}
			else if (aString.equals("TOTALAC"))
			{
				fa.write(output, Integer.toString(totalAC()));
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
				writeToken(acAbilityMod, fa, output);
			}
			else if (aString.equals("ACSIZEMOD") || aString.equals("SIZEMOD"))
			{
				final int acSizeMod = acSizeMod();
				writeToken(acSizeMod, fa, output);
			}
			else if (aString.equals("SHIELD.AC"))
			{
				final int shieldAC = acModFromShield();
				writeToken(shieldAC, fa, output);
			}
			else if (aString.equals("ARMOR.AC"))
			{
				final int armorAC = acModFromArmor();
				writeToken(armorAC, fa, output);
			}
			else if (aString.equals("EQUIP.AC"))
			{
				final int equipAC = modToFromEquipment("AC");
				writeToken(equipAC, fa, output);
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
			else if (aString.equals("INITIATIVEBONUS"))
			{
				final int initiativeBonus = initiativeMod() - calcStatMod(Globals.DEXTERITY);
				writeToken(initiativeBonus, fa, output);
			}
			else if (aString.startsWith("MOVEMENT"))
			{
				if (aString.length() > 9)
				{
					aString = aString.substring(9);
					StringTokenizer aTok = new StringTokenizer(aString, ".", false);
					String moveType = (String)aTok.nextToken();
					aString = "RATE";
					if (aTok.hasMoreTokens())
						aString = ((String)aTok.nextToken()).toUpperCase();

					for (int x = 0; x < getRace().getMovements().length; x++)
					{
						if (race.getMovementType(x).toUpperCase().equals(moveType.toUpperCase()))
						{
							// Output choices for Move types contained in here, only RATE currently Defined
							if (aString.equals("RATE"))
								fa.write(output, "" + movement(x) + "'");
						}
					}
				}
				else
				{
					fa.write(output, race.getMovementType(0) + " " + movement(0) + "'");
					for (int x = 1; x < getRace().getMovements().length; x++)
						fa.write(output, ", " + race.getMovementType(x) + " " + movement(x) + "'");
				}
			}
			else if (aString.equals("SIZE"))
			{
				fa.write(output, getSize());
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
				if (domainList.size() > (int)aString.charAt(6) - 49)
					aDomain = (Domain)domainList.get((int)aString.charAt(6) - 49);
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
				fa.write(output, getAttackString(0));
			}
			else if (aString.equals("ATTACK.MELEE.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus(1)).toString());
			}
			else if (aString.equals("ATTACK.RANGED"))
			{
				fa.write(output, getAttackString(1));
			}
			else if (aString.equals("ATTACK.RANGED.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus(2)).toString());
			}
			else if (aString.equals("ATTACK.UNARMED"))
			{
				fa.write(output, getAttackString(2));
			}
			else if (aString.equals("ATTACK.UNARMED.BASE"))
			{
				fa.write(output, new Integer(baseAttackBonus(3)).toString());
			}
			else if (aString.equals("ATTACK.MELEE.TOTAL"))
			{
				fa.write(output, getAttackString(0, calcStatMod(Globals.STRENGTH) + modForSize()));
			}
			else if (aString.equals("ATTACK.RANGED.TOTAL"))
			{
				fa.write(output, getAttackString(1, calcStatMod(Globals.DEXTERITY) + modForSize()));
			}
			else if (aString.equals("ATTACK.UNARMED.TOTAL"))
			{
				fa.write(output, getAttackString(2, calcStatMod(Globals.STRENGTH) + modForSize()));
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
			else if (aString.startsWith("SPELLLIST"))
			//SPELLLISTCAST0.0 KNOWN0.0 BOOK0.0 TYPE0
			{
				int cmp = 0;
				if (aString.substring(9, 13).equals("TYPE"))
					cmp = 3;
				else if (aString.substring(9, 13).equals("BOOK"))
					cmp = 2;
				else if (aString.substring(9, 14).equals("KNOWN"))
					cmp = 1;
				else if (aString.substring(9, 13).equals("CAST"))
					cmp = 0;
				else if (aString.substring(9, 14).equals("CLASS"))
					cmp = 4;
				else if (aString.substring(9, 11).equals("DC"))
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
					int stat = -1;
					if (aClass.getSpellBaseStat().length() > 2)
						stat = statNames.lastIndexOf(aClass.getSpellBaseStat());
					if (stat >= 0)
						stat = stat / 3;
					int knownNum = 0;
					int spellNum = aClass.spellList().size();
					String castNum = String.valueOf(aClass.getCastForLevel(aClass.getLevel().intValue(), level, "Known Spells")) +
						aClass.getBonusCastForLevelString(aClass.getLevel().intValue(), level, "Known Spells");
					knownNum = aClass.getKnownForLevel(aClass.getLevel().intValue(), level);
					spellNum = aClass.spellList().size();
					String cString = aClass.getKeyName();
					if (aClass.getCastAs().length() > 0)
						cString = aClass.getCastAs();
					if (cString.startsWith("Domain"))
						cString = domainClassName();
					if (spellNum == 0)
						return 0;
					switch (cmp)
					{
						case 0:
							fa.write(output, castNum);
							break;
						case 1:
							fa.write(output, Integer.toString(knownNum));
							break;
						case 2:
							Spell sp = null;
							for (Iterator se = aClass.spellList().iterator(); se.hasNext();)
							{
								sp = (Spell)se.next();
								aString = cString;
								if (sp.levelForClass(aString, aClass.getName()) == level)
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
								statString = statNames.substring(stat * 3, stat * 3 + 3);
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
				replaceTokenWeaponProfs(fa, output);
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
				replaceTokenEq(aString, fa, output);
			}
			else if (aString.equals("TOTAL.WEIGHT"))
			{
				Float totalWeight = totalWeight();
				fa.write(output, totalWeight.toString() + " lbs");
			}
			else if (aString.equals("TOTAL.VALUE"))
			{
				Float totalValue = totalValue();
				fa.write(output, totalValue.toString() + " gp");
			}
			else if (aString.equals("TOTAL.CAPACITY"))
			{
				fa.write(output, Globals.maxLoadForStrengthAndSize(adjStats(Globals.STRENGTH), getSize()).toString());
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
						fa.write(output, (String)stringList.get(i) + aString);
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
				fa.write(output, playersName);
			else if (aString.equals("VISION"))
				fa.write(output, getRace().getVision());
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
			else if (aString.startsWith("IIF("))
			{
				replaceTokenIIF(aString, fa, output);
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
			JOptionPane.showMessageDialog(null, "Error replacing " + aString, "PCGen", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}


	private void replaceTokenIIF(String aString, FileAccess fa, BufferedWriter output)
	{
		int iParenCount = 0;
		String aExpr = null;
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
			if (aT[0].startsWith("HASFEAT:"))
			{
				aT[0] = aT[0].substring(8).trim();
				if (getFeatNamed(aT[0]) == null)
				{
					iStart = 2;		// false
				}
				else
				{
					iStart = 1;		// true
				}
			}
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
			fa.write(output, bString);
		}
	}

	private void replaceTokenWeight(String aString, FileAccess fa, BufferedWriter output)
	{
		int i = 1;
		if (aString.endsWith("MEDIUM"))
			i = 2;
		else if (aString.endsWith("HEAVY"))
			i = 3;
		fa.write(output, new Float(i * Globals.maxLoadForStrengthAndSize(adjStats(Globals.STRENGTH), getSize()).intValue() / 3).toString());
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
		int i = Globals.loadTypeForStrength(adjStats(Globals.STRENGTH), totalWeight());
		switch (i)
		{
			case 0:
				fa.write(output, "Light");
				return;
			case 1:
				fa.write(output, "Medium");
				return;
			case 2:
				fa.write(output, "Heavy");
				return;
			default:
				fa.write(output, "Overload");
				return;
		}
	}

	private void replaceTokenEq(String aString, FileAccess fa, BufferedWriter output)
	{
		Collection tempList = getEquipmentList().values();
		ArrayList aList = new ArrayList();

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
		int i = 0;
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
					if (anEquip.getHasHeaderParent() || anEquip.getChildCount() > 0)
					{
						aList.add(anEquip);
					}
				}

			}
			else
				aList = this.getEquipmentOfType(aType, 3);
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
				fa.write(output, eq.longName());
			}
			else if (tempString.equals("NAME"))
				fa.write(output, eq.getName());
			else if (tempString.equals("WT"))
				fa.write(output, eq.weight().toString());
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
				fa.write(output, eq.getContainerContentsString());
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
				fa.write(output, eq.typeString());
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
				fa.write(output, eq.getAltCrit());
			else if (tempString.equals("RANGE"))
				fa.write(output, eq.getRange().toString());
			else if (tempString.equals("ATTACKS"))
				fa.write(output, eq.getAttacks().toString());
			else if (tempString.equals("PROF"))
				fa.write(output, eq.profName());
			else if (tempString.equals("SPROP"))
			{
				fa.write(output, eq.getSpecialProperties());
			}
		}
	}

	private void replaceTokenWeaponProfs(FileAccess fa, BufferedWriter output)
	{
		int c = 0;
		for (Iterator setIter = getWeaponProfList().iterator(); setIter.hasNext();)
		{
			if (c > 0)
				fa.write(output, "; ");
			fa.write(output, (String)setIter.next());
			c++;
		}
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
				fa.write(output, aSet.toArray()[e].toString());
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
				fa.write(output, (String)setIter.next());
				c++;
			}
		}
	}

	private int replaceTokenSpecialAbility(String aString, FileAccess fa, BufferedWriter output)
	{
		int len;
		int specialability = Integer.parseInt(aString.substring(14, aString.length()));
		if (specialability >= getSpecialAbilityTimesList().size() && existsOnly)
			noMoreItems = true;
		len = getSpecialAbilityTimesList().size();
		if (specialability >= 0 && specialability < len)
			fa.write(output, getSpecialAbilityTimesList().get(specialability).toString());
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
		Skill aSkill = null;
		for (Iterator e = getSkillList().iterator(); e.hasNext();)
		{
			aSkill = (Skill)e.next();
			int modSkill = -1;
			if (aSkill.keyStat().compareToIgnoreCase("none") != 0)
				modSkill = aSkill.modifier().intValue() - calcStatMod(aSkill.statIndex(aSkill.keyStat()));
			if (aSkill.getTotalRank().intValue() > 0 || modSkill > 0)
			{
				int temp = aSkill.modifier().intValue() + aSkill.getTotalRank().intValue();
				if (i > 0)
					fa.write(output, ", ");
				fa.write(output, aSkill.getName() + " +" + Integer.toString(temp));
				i++;

			}
		}
	}

	private void writeToken(final int acSizeMod, FileAccess fa, BufferedWriter output)
	{
		if (acSizeMod > 0)
			fa.write(output, "+");
		fa.write(output, Integer.toString(acSizeMod));
	}

	private void replaceTokenWIll(String aString, FileAccess fa, BufferedWriter output)
	{
		int will = 0;
		if (aString.endsWith(".TOTAL"))
			will = getBonus(3, true) + calcStatMod(Globals.WISDOM);
		else if (aString.endsWith(".BASE"))
			will = getBonus(3, false);
		else if (aString.endsWith(".RACE"))
			will = getRace().bonusTo("CHECKS", "Willpower");
		else if (aString.endsWith(".MAGIC"))
			will = getBonus(3, true) - getBonus(3, false) - getRace().bonusTo("CHECKS", "Willpower");
		else
			will = getBonus(3, true);
		if (will > 0)
			fa.write(output, "+");
		fa.write(output, Integer.toString(will));
	}

	private void replaceTokenFortitude(String aString, FileAccess fa, BufferedWriter output)
	{
		int fortitude = 0;
		if (aString.endsWith(".TOTAL"))
			fortitude = getBonus(1, true) + calcStatMod(Globals.CONSTITUTION);
		else if (aString.endsWith(".BASE"))
			fortitude = getBonus(1, false);
		else if (aString.endsWith(".RACE"))
			fortitude = getRace().bonusTo("CHECKS", "Fortitude");
		else if (aString.endsWith(".MAGIC"))
			fortitude = getBonus(1, true) - getBonus(1, false) - getRace().bonusTo("CHECKS", "Fortitude");
		else
			fortitude = getBonus(1, true);
		if (fortitude > 0)
			fa.write(output, "+");
		fa.write(output, Integer.toString(fortitude));
	}

	private void replaceTokenReflex(String aString, FileAccess fa, BufferedWriter output)
	{
		int reflexBonus = 0;
		if (aString.endsWith(".TOTAL"))
			reflexBonus = getBonus(2, true) + calcStatMod(Globals.DEXTERITY);
		else if (aString.endsWith(".BASE"))
			reflexBonus = getBonus(2, false);
		else if (aString.endsWith(".RACE"))
			reflexBonus = getRace().bonusTo("CHECKS", "Reflex");
		else if (aString.endsWith(".MAGIC"))
			reflexBonus = getBonus(2, true) - getBonus(2, false) - getRace().bonusTo("CHECKS", "Reflex");
		else
			reflexBonus = getBonus(2, true);
		if (reflexBonus > 0)
			fa.write(output, "+");
		fa.write(output, Integer.toString(reflexBonus));
	}

	private void replaceTokenProhibitedList(FileAccess fa, BufferedWriter output)
	{
		for (Iterator iter = classList.iterator(); iter.hasNext();)
		{
			PCClass aClass = (PCClass)iter.next();
			if (aClass.getLevel().intValue() > 0)
			{
				if (!aClass.getProhibitedString().equals("None"))
					fa.write(output, aClass.getProhibitedString());
			}
		}
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
					Float aFloat = getVariableValue(bString, "", "");
					cMin = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					break;
				case 1:
					aFloat = getVariableValue(bString, "", "");
					cMax = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					break;
				case 2:
					aFloat = getVariableValue(bString, "", "");
					cStep = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
					if (aString.startsWith("DFOR."))
					{
						isDFor = true;
						bString = aTok.nextToken();
						aFloat = getVariableValue(bString, "", "");
						cStepLineMax = Integer.parseInt(aFloat.toString().substring(0, aFloat.toString().lastIndexOf(".")));
						bString = aTok.nextToken();
						aFloat = getVariableValue(bString, "", "");
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
						int cInt = iNow + new Integer(eString.substring(1)).intValue();
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
		int i = 0;
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
				fa.write(output, eq.longName());
			}
			else if (tempString.equals("NAME"))
				fa.write(output, eq.getName());
			else if (tempString.equals("WT"))
			{
				if (eq.getChildCount() == 0)
					fa.write(output, eq.weight().toString());
				else
					fa.write(output, (new Float(eq.getContainedWeight().floatValue() + eq.weight().floatValue())).toString());
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
				fa.write(output, eq.getContainerContentsString());
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
				fa.write(output, eq.typeString());
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
				fa.write(output, eq.getAltCrit());
			else if (tempString.equals("RANGE"))
				fa.write(output, eq.getRange().toString());
			else if (tempString.equals("ATTACKS"))
				fa.write(output, eq.getAttacks().toString());
			else if (tempString.equals("PROF"))
				fa.write(output, eq.profName());
			else if (tempString.equals("SPROP"))
			{
				fa.write(output, eq.getSpecialProperties());
			}
		}
	}

	private void replaceTokenWeapon(String aString, FileAccess fa, BufferedWriter output)
	{
		int weapon = 0;
		if (aString.substring(6, 7).equals("P"))
			weapon = -1; // primary
		else if (aString.substring(6, 7).equals("O"))
			weapon = -2; // off-hand
		else if (aString.substring(6, 7).equals("H"))
			weapon = -3; // unarmed
		else
			weapon = Integer.parseInt(aString.substring(6, aString.lastIndexOf('.')));
		int i = 0;
		StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		aString = aTok.nextToken();
		aString = aTok.nextToken();
		Equipment eq = null;
		if (weapon == -1)
		{
			i = -1;
			eq = primaryWeapon;
		}
		else if (weapon == -2)
		{
			i = -2;
			eq = secondaryWeapon[0];
		}
		else if (weapon == -3)
		{
			i = -3;
			eq = getEquipmentNamed("Unarmed Strike");
		}
		else
		{
			ArrayList aArrayList = getEquipmentOfType("Weapon", 3);
			if (weapon < aArrayList.size())
				eq = (Equipment)aArrayList.get(weapon);
			if (weapon == aArrayList.size() - 1 && existsOnly)
				noMoreItems = true;
		}
		if (eq != null)
		{
			boolean isDouble = (eq.getHand() == Equipment.BOTH_HANDS &&
				eq.typeStringContains("DOUBLE"));
			int index = 0;
			Integer bInt = new Integer(0);
			if (aString.startsWith("NAME"))
			{
				if (eq.isEquipped())
					fa.write(output, "*");
				fa.write(output, eq.getName());
			}
			else if (aString.startsWith("LONGNAME"))
			{
				if (eq.isEquipped())
					fa.write(output, "*");
				fa.write(output, eq.longName());
			}
			else if (aString.startsWith("ATTACKS"))
				fa.write(output, eq.getAttacks().toString());
			else if (aString.startsWith("CRIT"))
			{
				int mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITRANGEMULT", true);
				int critrange = Integer.parseInt(eq.getCritRange());
				if (mult > 0)
					critrange *= mult;
				critrange = 21 - (critrange + getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITRANGEADD", true));
				fa.write(output, String.valueOf(critrange));
				if (critrange < 20)
					fa.write(output, "-20");
			}
			else if (aString.startsWith("MULT"))
			{
				int mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITMULTADD", true);
				bInt = new Integer(eq.getCritMult().substring(1));
				bInt = new Integer(bInt.intValue() + mult);
				fa.write(output, bInt.toString());
				if (isDouble && eq.getAltCrit().length() > 0)
				{
					mult = getTotalBonusTo("WEAPONPROF=" + eq.profName(), "CRITMULTADD", true);
					bInt = new Integer(eq.getAltCrit().substring(1));
					bInt = new Integer(bInt.intValue() + mult);
					fa.write(output, "/" + bInt.toString());
				}
			}
			else if (aString.startsWith("RANGE"))
				fa.write(output, eq.getRange().toString() + "'");
			else if (aString.startsWith("TYPE"))
			{
				if (eq.typeStringContains("BLUDGEONING"))
					fa.write(output, "B");
				if (eq.typeStringContains("PIERCING"))
					fa.write(output, "P");
				if (eq.typeStringContains("SLASHING"))
					fa.write(output, "S");
			}
			else if (aString.startsWith("HIT") || aString.startsWith("TOTALHIT"))
			{
				String mString = getAttackString(0, calcStatMod(Globals.STRENGTH));
				String rString = getAttackString(0, calcStatMod(Globals.DEXTERITY));
				if (eq.typeStringContains("MONK"))
				{
					String m1String = getAttackString(2, calcStatMod(Globals.STRENGTH));
					if (m1String.length() > mString.length())
						mString = m1String;
					else if (m1String.length() == mString.length() && !mString.equals(m1String))
					{
						StringTokenizer mTok = new StringTokenizer(mString, "+/", false);
						StringTokenizer m1Tok = new StringTokenizer(m1String, "+/", false);
						String msString = mTok.nextToken();
						String m1sString = m1Tok.nextToken();
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
					if (eq.getHand() != Equipment.TWOWEAPON_HANDS && isSecondaryWeapon(eq) && primaryWeapon != null)
						index = -10;
					else if (isSecondaryWeapon(eq) && primaryWeapon == null)
						index = -4;
					else if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS || (secondaryWeapon != null && secondaryWeapon[0] != null))
						index = -6;
					if (isDouble || (eq.getHand() == Equipment.TWOWEAPON_HANDS && Globals.getWeaponProfNamed(eq.profName()).isLight()) ||
						(primaryWeapon != null && secondaryWeapon[0] != null && Globals.getWeaponProfNamed(secondaryWeapon[0].profName()).isLight()))
						index += 2;
					if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS ||
						(primaryWeapon != null && isSecondaryWeapon(eq)))
						secondaryBonus = getTotalBonusTo("COMBAT", "TOHIT-SECONDARY", true);
					if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS ||
						(secondaryWeapon[0] != null && isPrimaryWeapon(eq)))
						primaryBonus = getTotalBonusTo("COMBAT", "TOHIT-PRIMARY", true);
				}

				WeaponProf wp = null;
				wp = Globals.getWeaponProfNamed(eq.profName());

				index += modForSize();	//include the size bonus/penalty since it is no longer added elsewhere

				index += primaryBonus;
				for (Iterator ei = eq.typeArrayList().iterator(); ei.hasNext();)
				{
					String tString = ei.next().toString();
					index += getTotalBonusTo("TOHIT", "TYPE=" + tString, true);
				}
				if (!isDouble && eq.getHand() != Equipment.TWOWEAPON_HANDS)
					index += secondaryBonus;
				index += eq.bonusTo("WEAPON", "TOHIT");
				if (wp == null || !getWeaponProfList().contains(wp.getName()))
					index -= 4; // non-proficiency penalty
				if (wp != null)
				{
					if (Globals.isDebugMode())
						System.out.println(wp.getName() + " " + getTotalBonusTo("WEAPONPROF=" + wp.getName(), "TOHIT", true));
					index += getTotalBonusTo("WEAPONPROF=" + wp.getName(), "TOHIT", true);
				}
				Integer numInt = new Integer(-1);
				if (aString.startsWith("TOTALHIT") && weapon > -1)
				{
					if (!aString.endsWith("TOTALHIT"))
						numInt = new Integer(aString.substring(8));
				}
				int k = index;
				bInt = new Integer(index + weaponMod);
				StringTokenizer zTok = null;
				if (eq.typeStringContains("MELEE"))
					zTok = new StringTokenizer(mString, "+/", false);
				else if (eq.typeStringContains("RANGED"))
					zTok = new StringTokenizer(rString, "+/", false);
				int count = 0;
				int x = 0;
				int max = 1 + getTotalBonusTo("COMBAT", "SECONDARYATTACKS", true);
				int extra_attacks = eq.bonusTo("COMBAT", "ATTACKS");  // BONUS:COMBAT|ATTACKS|* represent extra attacks at BaB
				// such as from a weapon of 'Speed'
				if (primaryWeapon == null)
					max = 100;
				if (!eq.isAttacksProgress())
					numInt = new Integer(0);

				//
				// Trap this to avoid infinite loop
				//
				if (!eq.typeStringContains("Melee") && !eq.typeStringContains("Ranged"))
				{
					fa.write(output, "???");
					return;
				}

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
					if (numInt.intValue() < 0)
					{
						if (count > 0)
							fa.write(output, "/");
						if (index + bInt.intValue() > 0)
							fa.write(output, "+");
						fa.write(output, new Integer(bInt.intValue() + index).toString());

						// Here we handle extra attacks provided by the BONUS:COMBAT|ATTACKS|* tag
						// These are at the characters BaB
						while (extra_attacks-- > 0)
						{
							fa.write(output, "/");
							if (index + bInt.intValue() > 0)
								fa.write(output, "+");
							fa.write(output, new Integer(bInt.intValue() + index).toString());
						}

						if (x == 0 && (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS) ||
							(x < max && eq.getHand() == Equipment.TWOWEAPON_HANDS))
						{
							fa.write(output, "/");
							if (index - primaryBonus + bInt.intValue() + secondaryBonus - 4 > 0)
								fa.write(output, "+");
							fa.write(output, new Integer(index - primaryBonus + bInt.intValue() + secondaryBonus - 4).toString());
						}
						count++;
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

				if (weapon == -1 && primaryWeapon.equals(secondaryWeapon[0]))
				{
					if (aString.equals("TOTALHIT"))
					{
						StringTokenizer bTok = null;
						if (eq.typeStringContains("MELEE"))
							bTok = new StringTokenizer(mString, "/", false);
						else if (eq.typeStringContains("RANGED"))
							bTok = new StringTokenizer(rString, "/", false);
						if (bTok != null)
							k += Integer.parseInt(bTok.nextToken());
					}
					bInt = new Integer(k);
					fa.write(output, "/");
					if (k > 0)
						fa.write(output, "+");
					fa.write(output, bInt.toString());
				}
			}
			else if (aString.startsWith("CATEGORY"))
			{
				if (eq.typeStringContains("SIMPLE"))
					fa.write(output, "SIMPLE");
				else if (eq.typeStringContains("MARTIAL"))
					fa.write(output, "MARTIAL");
				else if (eq.typeStringContains("EXOTIC"))
					fa.write(output, "EXOTIC");
				else
					fa.write(output, "NON-STANDARD");
				fa.write(output, "-");
				if (eq.typeStringContains("MELEE"))
					fa.write(output, "MELEE");
				else if (eq.typeStringContains("RANGED"))
					fa.write(output, "RANGED");
				else
					fa.write(output, "NON-STANDARD");
			}
			else if (aString.startsWith("HAND"))
				fa.write(output, Equipment.getHandName(eq.getHand()));
			else if (aString.startsWith("MAGICDAMAGE"))
			{
				final int magicdamage = eq.bonusTo("WEAPON", "DAMAGE") + eq.bonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE");
				if (magicdamage > 0)
					fa.write(output, "+");
				fa.write(output, Integer.toString(magicdamage));
			}
			else if (aString.startsWith("MAGICHIT"))
			{
				final int magichit = eq.bonusTo("WEAPON", "TOHIT") + eq.bonusTo("WEAPONPROF=" + eq.profName(), "TOHIT");
				if (magichit > 0)
					fa.write(output, "+");
				fa.write(output, Integer.toString(magichit));
			}
			else if (aString.startsWith("FEAT"))
			{
				final int featBonus = getFeatBonusTo("WEAPON", "TOHIT", true) + getFeatBonusTo("WEAPONPROF=" + eq.profName(), "TOHIT", true);
				if (featBonus > 0)
					fa.write(output, "+");
				fa.write(output, Integer.toString(featBonus));
			}
			else if (aString.endsWith("DAMAGE"))
			{
				String bString = new String(eq.getDamage());
				int bonus = 0;
				if (eq.typeStringContains("MONK") && eq.typeStringContains("UNARMED"))
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
				bInt = new Integer(0);
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
					if (eq.typeStringContains("MELEE") || eq.typeStringContains("THROWN"))
					{
						if (isSecondaryWeapon(eq) && eq != primaryWeapon)
							bInt = new Integer(bInt.intValue()
								+ (calcStatMod(Globals.STRENGTH)) / 2);
						else
							bInt = new Integer(bInt.intValue()
								+ calcStatMod(Globals.STRENGTH));
					}
					bonus = eq.bonusTo("WEAPON", "DAMAGE");
					for (Iterator ei = eq.typeArrayList().iterator(); ei.hasNext();)
						bonus += getTotalBonusTo("DAMAGE", "TYPE=" + ei.next().toString(), true);
					WeaponProf wp = Globals.getWeaponProfNamed(eq.profName());
					if (!isDouble && eq.typeStringContains("MELEE") &&
						wp != null && adjStats(Globals.STRENGTH) / 2 > 5 &&
						eq.getHand() == Equipment.BOTH_HANDS)
					{
						if (wp.isOneHanded() && !wp.isLight())
							bonus += (calcStatMod(Globals.STRENGTH)) / 2;
						if (wp.isTwoHanded())
							bonus += (calcStatMod(Globals.STRENGTH)) / 2;
					}
					if (wp != null && adjStats(Globals.STRENGTH) / 2 > 5
						&& eq.typeStringContains("MELEE"))
						if (wp.getType().equals("Natural") && eq.isOnlyNaturalWeapon()
							&& eq.modifiedName().endsWith("Primary"))
							bonus += (calcStatMod(Globals.STRENGTH)) / 2;
					bInt = new Integer(bInt.intValue() + bonus + getTotalBonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE", true));
					bString = bString.substring(0, index);
				}
				fa.write(output, bString);
				if (bInt.intValue() > 0)
					fa.write(output, "+");
				if (bInt.intValue() != 0)
					fa.write(output, bInt.toString());
				if (isDouble || eq.getHand() == Equipment.TWOWEAPON_HANDS)
				{
					bonus = eq.bonusTo("WEAPON", "DAMAGE");
					for (Iterator ei = eq.typeArrayList().iterator(); ei.hasNext();)
						bonus += getTotalBonusTo("DAMAGE", "TYPE=" + ei.next().toString(), true);
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
					else if (eq.getHand() == Equipment.TWOWEAPON_HANDS)
						bInt = new Integer(bInt.intValue() - (calcStatMod(Globals.STRENGTH)));
					bonus += (calcStatMod(Globals.STRENGTH)) / 2; // only get half strength bonus
					bInt = new Integer(bInt.intValue() + bonus + getTotalBonusTo("WEAPONPROF=" + eq.profName(), "DAMAGE", true));
					fa.write(output, "/");
					fa.write(output, bString);
					if (bInt.intValue() > 0)
						fa.write(output, "+");
					if (bInt.intValue() != 0)
						fa.write(output, bInt.toString());
				}
			}
			else if (aString.startsWith("SIZE"))
			{
				fa.write(output, eq.getSize());
			}
			else if (aString.startsWith("SPROP"))
			{
				fa.write(output, eq.getSpecialProperties());
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
				if (temp > 0)
					fa.write(output, "+");
				fa.write(output, Integer.toString(temp));
			}
		}
		return len;
	}

	private int replaceTokenArmor(String aString, int len, FileAccess fa, BufferedWriter output)
	{
		final int armor = Integer.parseInt(aString.substring(5, aString.lastIndexOf('.')));
		int i = 0;
		StringTokenizer aTok = new StringTokenizer(aString, ".", false);
		String tempString = aTok.nextToken();
		tempString = aTok.nextToken();
		ArrayList aArrayList = getEquipmentOfType("Armor", 3);
		ArrayList bArrayList = getEquipmentOfType("Shield", 3);
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
			if (tempString.startsWith("NAME"))
			{
				if (eq.isEquipped())
					fa.write(output, "*");
				fa.write(output, eq.getName());
			}
			else if (tempString.startsWith("TOTALAC"))
			{
				if (eq.getAcMod().intValue() > 0)
					fa.write(output, "+");
				fa.write(output, eq.getAcMod().toString());
			}
			else if (tempString.startsWith("BASEAC"))
			{
				if (eq.getAcMod().intValue() > 0)
					fa.write(output, "+");
				fa.write(output, eq.getAcMod().toString());
			}
			else if (tempString.startsWith("MAXDEX"))
			{
				int iMax = eq.getMaxDex().intValue();
				if (iMax != 100)
				{
					if (eq.getMaxDex().intValue() > 0)
					{
						fa.write(output, "+");
					}
					fa.write(output, eq.getMaxDex().toString());
				}
			}
			else if (tempString.startsWith("ACCHECK"))
			{
				if (eq.acCheck().intValue() > 0)
					fa.write(output, "+");
				fa.write(output, eq.acCheck().toString());
			}
			else if (tempString.startsWith("SPELLFAIL"))
			{
				fa.write(output, eq.spellFailure().toString());
			}
			else if (tempString.startsWith("MOVE"))
			{
				aTok = new StringTokenizer(eq.moveString(), ",", false);
				tempString = "";
				if ((getSize().equals("M") || getSize().equals("S")) &&
					aTok.countTokens() > 0)
				{
					tempString = aTok.nextToken();
					if (getSize().equals("S") && aTok.countTokens() > 1)
						tempString = aTok.nextToken();
				}
				fa.write(output, tempString);
			}
			else if (tempString.startsWith("SPROP"))
			{
				fa.write(output, eq.getSpecialProperties());
			}
		}
		return len;
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
		if (aClass == null && existsOnly)
			noMoreItems = true;
		if (aClass != null)
		{
			String bookName = "";
			int i = 0;
			if (bookNum == -1 || aClass.getMemorizeSpells() == false)
			{
				bookName = "Known Spells";
				i = getSpellBooks().size();
			}
			for (; i < getSpellBooks().size(); i++)
			{
				bookName = (String)getSpellBooks().get(i);
				if (!bookName.equals("Known Spells"))
					bookNum--;
				if (bookNum == -1)
					break;
				bookName = "";
			}
			if (aClass.getMemorizeSpells() == false)
				bookName = "Known Spells";
			if (!bookName.equals(""))
			{
				Spell aSpell = null;
				int j = spellNumber;
				boolean moreSpells = false;
				for (i = 0; i < aClass.spellList().size(); i++)
				{
					aSpell = (Spell)aClass.spellList().get(i);
					String classString = aClass.getKeyName();
					if (aClass.getCastAs().length() > 0)
						classString = aClass.getCastAs();
					if (classString.equals("Domain"))
						classString = domainClassName();
					if (aSpell.getSpellBooks().contains(bookName))
					{
						if (aSpell.levelForClass(classString, aClass.getName()) >= spellLevel)
							moreSpells = true;
						if (aSpell.levelForClass(classString, aClass.getName()) == spellLevel)
							spellNumber--;
					}
					if (spellNumber == -1)
						break;
				}
				if (inLabel && moreSpells == false && checkBefore)
					canWrite = false;
				if (spellNumber == -1 && aSpell != null)
				{
					Spell bSpell = (Spell)Globals.getSpellMap().get(aSpell.getKeyName());
					if (aLabel.equals("NAME"))
						fa.write(output, aSpell.getName());
					else if (aLabel.equals("TIMES"))
						fa.write(output, aSpell.timesForSpellBook(bookName).toString());
					else if (bSpell != null)
					{
						if (aLabel.equals("RANGE"))
							fa.write(output, bSpell.getRange());
						else if (aLabel.equals("COMPONENTS"))
							fa.write(output, bSpell.getComponentList());
						else if (aLabel.equals("CASTINGTIME"))
							fa.write(output, bSpell.getCastingTime());
						else if (aLabel.equals("DURATION"))
							fa.write(output, bSpell.getDuration());
						else if (aLabel.equals("EFFECT"))
							fa.write(output, bSpell.getEffect());
						else if (aLabel.equals("EFFECTTYPE"))
							fa.write(output, bSpell.getEffectType());
						else if (aLabel.equals("SAVEINFO"))
							fa.write(output, bSpell.getSaveInfo());
						else if (aLabel.equals("SCHOOL"))
							fa.write(output, bSpell.getSchool());
						else if (aLabel.equals("SOURCE"))
							fa.write(output, bSpell.getSource());
						else if (aLabel.equals("SUBSCHOOL"))
							fa.write(output, bSpell.getSubschool());
						else if (aLabel.equals("SR"))
							fa.write(output, bSpell.getSR());
						else if (aLabel.startsWith("BONUSSPELL"))
						{
							String sString = "*";
							if (aLabel.length() > 10)
								sString = aLabel.substring(10);
							if (bSpell.isInSpecialty(aClass.getSpecialtyList()))
								fa.write(output, sString);
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
		int skill = 0;
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
			int i = new Integer(fString.substring(9)).intValue();
			fString = aTok.nextToken();
			ArrayList skillSubset = new ArrayList();

			Skill bSkill = null;
			for (Iterator iter = skillList.iterator(); iter.hasNext();)
			{
				bSkill = (Skill)iter.next();
				if (bSkill.hasType(fString))
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
			int i = Integer.parseInt(fString.substring(5));
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
			if ((cmp == 5 || cmp == 6) && aSkill.keyStat().equals("None"))
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
		int dot = aString.lastIndexOf(".");
		int classNum = new Integer(aString.substring(14, dot)).intValue();
		int levelNum = new Integer(aString.substring(dot + 1)).intValue();
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
			if (bString.startsWith("Domain"))
				bString = domainClassName();
			Spell aSpell = null;
			for (Iterator e1 = aClass.spellList().iterator(); e1.hasNext();)
			{
				aSpell = (Spell)e1.next();
				if (aSpell.levelForClass(bString, aClass.getName()) == levelNum)
				{
					canWrite = true;
					break;
				}
			}
		}
		return 0;
	}

	private void replaceTokenAlignmentShort(FileAccess fa, BufferedWriter output)
	{
		final String alString = Globals.s_ALIGNSHORT[alignment];
		fa.write(output, alString);
	}

	private void replaceTokenAlignment(FileAccess fa, BufferedWriter output)
	{
		final String alString = Globals.s_ALIGNLONG[alignment];
		fa.write(output, alString);
	}

	private void printFeat(int numberPos, String aString, ArrayList anArrayList, FileAccess fa, BufferedWriter output)
	{
		int len = anArrayList.size();
		int j = aString.lastIndexOf(".");
		int i = -1;
		if (j == -1)
			i = new Integer(aString.substring(numberPos)).intValue();
		else
			i = new Integer(aString.substring(numberPos, j)).intValue();
		if (len <= i && existsOnly)
			noMoreItems = true;
		Globals.sortPObjectList(anArrayList);
		Feat aFeat = null;
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			aFeat = (Feat)e.next();
			if (i == 0 && (aFeat.isVisible() == 1 || aFeat.isVisible() == 2))
			{
				if (aString.endsWith(".DESC"))
					fa.write(output, aFeat.getDescription());
				else
					fa.write(output, aFeat.qualifiedName());
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
				fa.write(output, aFeat.qualifiedName());
			i++;
		}
	}
	// e.g. getVariableValue("3+CHA","CLASS:Cleric","1") for Turn Undead
	public Float getVariableValue(String aString, String src, String subSrc)
	{
		Float total = new Float(0.0);
		Float total1 = null;
		while (aString.lastIndexOf("(") > -1)
		{
			int x = innerMostStringStart(aString);
			int y = innerMostStringEnd(aString);
			String bString = aString.substring(x + 1, y);
			aString = aString.substring(0, x) + getVariableValue(bString, src, subSrc) + aString.substring(y + 1);
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
					val1 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc); // truncat final . character
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
					else if (cString.equals("GTEQ")) comp = 5;
				}
				else if (cString.equals("THEN"))
				{
					val2 = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else if (cString.equals("ELSE"))
				{
					valt = getVariableValue(bString.substring(0, bString.length() - 1), src, subSrc); // truncat final . character
					aTok.nextToken(); // discard next . character
					bString = "";
				}
				else
					bString = bString + cString;
			}
			if (val1 != null && val2 != null && valt != null)
			{
				valf = getVariableValue(bString, src, subSrc);
				total = valt;
				switch (comp)
				{
					case 1:
						if (val1.doubleValue() >= val2.doubleValue())
							total = valf;
						break;
					case 2:
						if (val1.doubleValue() > val2.doubleValue())
							total = valf;
						break;
					case 3:
						if (val1.doubleValue() != val2.doubleValue())
							total = valf;
						break;
					case 4:
						if (val1.doubleValue() <= val2.doubleValue())
							total = valf;
						break;
					case 5:
						if (val1.doubleValue() < val2.doubleValue())
							total = valf;
						break;
					default:
						System.out.println("ERROR - badly formed statement:" + aString + ":" + val1.toString() + ":" + val2.toString() + ":" + comp);
						return new Float(0.0);
				}
				if (Globals.isDebugMode())
					System.out.println("val1=" + val1 + " val2=" + val2 + " valt=" + valt + " valf=" + valf + " total=" + total);
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
					if (Globals.isDebugMode())
						System.out.println(valString + " " + loopVariable + " " + loopValue);
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
					if (Globals.isDebugMode())
						System.out.println("loopVariable=" + loopVariable + " loopValue=" + loopValue);
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
				if (valString.length() > 0 && statNames.lastIndexOf(valString) > -1)
				{
					int stat = statNames.lastIndexOf(valString) / 3;
					valString = new Integer(calcStatMod(stat)).toString();
					if (Globals.isDebugMode())
						System.out.println("MOD=" + valString);
				}
				else if (valString.length() == 8 && statNames.lastIndexOf(valString.substring(0, 3)) > -1
					&& valString.endsWith("SCORE"))
				{
					int stat = statNames.lastIndexOf(valString.substring(0, 3)) / 3;
					valString = new Integer(adjStats(stat)).toString();
					if (Globals.isDebugMode())
						System.out.println("SCORE=" + valString);
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
				else if (valString.equals("COUNT[CLASSES]"))
				{
					classList.trimToSize();
					valString = new Integer(classList.size()).toString();
				}
				else if (valString.equals("COUNT[DOMAINS]"))
				{
					domainList.trimToSize();
					valString = new Integer(domainList.size()).toString();
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
							if (anEquip.getHasHeaderParent() || anEquip.getChildCount() > 0)
							{
								aList.add(anEquip);
							}
						}

					}
					else
						aList = this.getEquipmentOfType(aType, 3);

					while (bTok.hasMoreTokens())
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
							if (anEquip.getHasHeaderParent() || anEquip.getChildCount() > 0)
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
						valString = new Integer(getVariableValue(valString.substring(0, valString.length() - 6), "", "").intValue()).toString();
					}
					if (valString.endsWith(".INTVAL"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 7), "", "").toString();
//            nextMode = 0;
						endMode += 10;
					}
					if (valString.endsWith("MIN"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "").toString();
						nextMode = 0;
						endMode += 1;
					}
					else if (valString.endsWith("MAX"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "").toString();
						nextMode = 0;
						endMode += 2;
					}
					else if (valString.endsWith("REQ"))
					{
						valString = getVariableValue(valString.substring(0, valString.length() - 3), "", "").toString();
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
								total = new Float(total.doubleValue() + new Float(valString).doubleValue());
								break;
							case 1:
								total = new Float(total.doubleValue() - new Float(valString).doubleValue());
								break;
							case 2:
								total = new Float(total.doubleValue() * new Float(valString).doubleValue());
								break;
							case 3:
								total = new Float(total.doubleValue() / new Float(valString).doubleValue());
								break;
						}
				}
				catch (Exception exc)
				{
					JOptionPane.showMessageDialog(null, "Math error determining value for " + aString + " " + src + " " + subSrc + "(" + valString + ")", "PCGen", JOptionPane.ERROR_MESSAGE);
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
			total = new Float(new Integer(total.intValue()).toString());
		return total;
	}

	public int innerMostStringStart(String aString)
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

	public int innerMostStringEnd(String aString)
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

	/** <code>rollStats</code> roll 6 random stats. Will need to be changed if we
	 * ever go to using a truely variable number of stats. Also, at the moment, it
	 * rolls using 4d6 drop lowest method. Other methods can be implemented by
	 * changing the argument to something other than 0.
	 * Method:
	 * 1: 4d6 Drop Lowest.
	 * 2: 3d6
	 * 3: 5d6 Drop 2 Lowest
	 * 4: 4d6 reroll 1's drop lowest
	 * 5: 4d6 reroll 1's and 2's drop lowest
	 * 6: 3d6 +5
	 * @param method the method to be used for rolling.
	 */
	public void rollStats(int method)
	{
		int dice, stat = 0;
		int low, roll = 0;
		///Random roller = new Random();
		int[] currentStats = stats;
		for (stat = 0; stat < 6; stat++)
		{
			//low = 6;
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
				case 1:
					roll = RollingMethods.roll(4, 6, true);
					break;
				case 2:
					roll = RollingMethods.roll(3, 6);
					break;
				case 3:
					roll = RollingMethods.roll(5, 6, true);
					break;
				case 4:
					roll = RollingMethods.roll(4, 6, true, 1);
					break;
				case 5:
					roll = RollingMethods.roll(4, 6, true, 2);
					break;
				case 6:
					roll = RollingMethods.roll(3, 6, 5);
					break;
			}
			currentStats[stat] = currentStats[stat] + roll;
			// The following line should never happen.
			//if currentstats[stat] < 3) currentStat[stat] = 3;
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

	final private void populateSkills(int level)
	{
		Globals.sortPObjectList(skillList);
		removeExcessSkills(level);
		addNewSkills(level);
	}

	private int acAbilityMod()
	{
		int acmod = calcStatMod(Globals.DEXTERITY);
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

	private int modFromArmorOnWeaponRolls()
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

	private int modToFromEquipment(String typeName)
	{
		int bonus = 0;
		int used = 0;
		int old = 0;
		if (typeName.equals("MAXDEX"))
			bonus = calcStatMod(Globals.DEXTERITY);
		int hold = -1;
		int i = 0;
		i = Globals.loadTypeForStrength(adjStats(Globals.STRENGTH), totalWeight());
		if (i == 1 && typeName.equals("ACCHECK"))
		{
			old = -3;
		}
		else if (i == 2 && typeName.equals("ACCHECK"))
		{
			old = -6;
		}
		else if (i == 1 && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 3;
		}
		else if (i == 2 && typeName.equals("MAXDEX"))
		{
			used = 1;
			bonus = 1;
		}
		else if (i == 3 && typeName.equals("MAXDEX"))
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

	private int intForSize(String key)
	{
		if (key != null && s_sizes.containsKey(key))
		{
			return ((int[])s_sizes.get(key))[0];
		}
		else
		{
			//Not really the right thing to do, but...
			return 0;
		}
	}

	private int getWeaponProfBonusTo(String aType, String aName)
	{
		int bonus = 0;
		if (getWeaponProfList().contains(aType))
			bonus = Globals.getWeaponProfNamed(aType).bonusTo(aType, aName);
		return bonus;
	}

	private void setRace(String aString)
	{
		Race race = (Race)Globals.getRaceMap().get(aString);
		setRace(race);
	}

	private int firstNonDigit(String str, int start)
	{
		final int len = str.length();
		while (start < len && Character.isDigit(str.charAt(start)))
		{
			++start;
		}
		return start;
	}

	private void setDeity(String aString)
	{
		for (int i = 0; i < Globals.getDeityList().size(); i++)
			if (Globals.getDeityList().get(i).toString().equals(aString))
				setDeity((Deity)Globals.getDeityList().get(i));
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

	private boolean isPrimaryWeapon(Equipment eq)
	{
		return (eq != null && (eq == primaryWeapon || eq.getHand() == Equipment.PRIMARY_HAND || eq.getHand() == Equipment.TWOWEAPON_HANDS));
	}

	private boolean isSecondaryWeapon(Equipment eq)
	{
		if (eq == null || eq.getHand() != Equipment.SECONDARY_HAND)
			return false;
		for (int x = 0; x < secondaryWeapon.length; x++)
			if (eq == secondaryWeapon[x])
				return true;
		return false;
	}

	private ArrayList getLineForMiscList(int index)
	{
		ArrayList aArrayList = new ArrayList();
		final StringTokenizer aTok = new StringTokenizer((String)getMiscList().get(index), "\r\n", false);
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

	private String escapeColons(String in)
	{
		StringBuffer retStr = new StringBuffer();
		for (int j = 0; j < in.length(); j++)
		{
			final char charAtJ = in.charAt(j);
			if (charAtJ != ':')
			{
				retStr.append(charAtJ);
			}
			else
			{
				retStr.append("\\").append(charAtJ);
			}
		}
		return retStr.toString();
	}

	private String escapeColons2(String in)
	{
		return Globals.replaceString(in, ":", "&#59;");
	}

	private String unEscapeColons2(String in)
	{
		return Globals.replaceString(in, "&#59;", ":");
	}

	private void saveExperienceAndMiscListLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, getExperience().toString() + ":");
		for (int i = 0; i < 3; i++)
		{
			fa.write(output, escapeColons(getMiscList().get(i).toString()) + " :");
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
		fa.write(output, getGold().toString() + ":" + escapeColons(bio) + " :" + escapeColons(description) + " :");
		fa.newLine(output);
	}

	private void saveEquipmentLine(FileAccess fa, BufferedWriter output)
	{
		Equipment eq = null;
		for (Iterator setIter = equipmentList.values().iterator();
				 setIter.hasNext();)
		{
			eq = (Equipment)setIter.next();
			if (!eq.getHasHeaderParent())
			{
				fa.write(output, eq.getKeyName() + " :" + eq.qty().toString() + ":");
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
		fa.write(output, escapeColons2(eyeColor) + " :" + escapeColons2(skinColor) + " :" + escapeColons2(hairColor) + " :" +
			escapeColons2(hairLength) + " :" + escapeColons2(speechTendency) + " :" + escapeColons2(phobias) + " :" +
			escapeColons2(interests) + " :" + escapeColons2(trait1) + " :" + escapeColons2(trait2) + " :" + escapeColons2(catchPhrase) +
			" :" + escapeColons2(location) + " :" + escapeColons2(residence) + " :");
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
		for (Iterator setIter = getWeaponProfList().iterator(); setIter.hasNext();)
		{
			fa.write(output, setIter.next() + ":");
		}
		fa.newLine(output);
	}

	private void saveLanguagesLine(FileAccess fa, BufferedWriter output)
	{
		for (Iterator setIter = getLanguagesList().iterator(); setIter.hasNext();)
		{
			fa.write(output, setIter.next() + ":");
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
			for (int j = 0; j < this.getRace().hitDice(); j++)
			{
				fa.write(output, ":" + this.getRace().getHitPointList(j).toString());
			}
		fa.newLine(output);
	}

	private void saveDeityLine(FileAccess fa, BufferedWriter output)
	{
		if (deity != null)
			fa.write(output, getDeity().getKeyName() + ":");
		for (int i = 0; i < domainList.size(); i++)
			fa.write(output, ((Domain)domainList.get(i)).getKeyName() + ":");
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
				fa.write(output, aRanks.size() + ":" );
				for(int i = 0; i < aRanks.size(); i++ )
				{
					fa.write(output, (String)aRanks.get(i) + ":" );
				}

				for (int i = 0; i < aSkill.getAssociatedList().size(); i++)
					fa.write(output, aSkill.getAssociatedList().get(i).toString() + ":");
			}
		}
		fa.newLine(output);
	}

	private void saveFeatsLine(FileAccess fa, BufferedWriter output)
	{
		final ArrayList aggregatedFeats = aggregateFeatList();
		Globals.sortPObjectList(aggregatedFeats);
		Feat aFeat = null;
		for (Iterator iter = aggregatedFeats.iterator(); iter.hasNext();)
		{
			aFeat = (Feat)iter.next();
			fa.write(output, aFeat.toString());
			for (Iterator saveFeatsIter = aFeat.saveList.iterator(); saveFeatsIter.hasNext();)
				fa.write(output, "[" + saveFeatsIter.next().toString());
			fa.write(output, ":" + String.valueOf(aFeat.associatedList().size()) + ":");

			for (Iterator f = aFeat.associatedList().iterator(); f.hasNext();)
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
		for (int i = 0; i < 6; i++)
		{
			fa.write(output, String.valueOf(getStats()[i]) + ":");
		}
		fa.write(output, String.valueOf(getPoolAmount()) + ":" + String.valueOf(costPool));
		fa.newLine(output);
	}

	private void saveNameLine(FileAccess fa, BufferedWriter output)
	{
		fa.write(output, name + ":" + playersName);
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
			if (((PCTemplate)templateList.get(x)).isVisible())
				fa.write(output, ((PCTemplate)templateList.get(x)).getName() + ":");
		fa.newLine(output);
	}

	private String loadClassSpellLine(Iterator e, FileAccess fa, BufferedReader input)
	{
		StringTokenizer classSpellTokenizer;
		String spellName;
		int k;
		String aString;
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
					k = aSpell.levelForClass(className, aClass.getName());
					aString = className + "," + Integer.toString(k);
					if (className.equals("Domain"))
					{
						aString = "";
						for (Iterator s1 = domainList.iterator(); s1.hasNext();)
						{
							Domain aDomain = (Domain)s1.next();
							k = aSpell.levelForClass(aDomain.getKeyName());
							if (k >= 0)
							{
								if (aString.length() > 0)
									aString = aString + ",";
								aString = aString + aDomain.getKeyName() + "," + new Integer(k).toString();
							}
						}
					}
					aSpell.setClassLevels(aString);
					aClass.spellList().add(aSpell);
				}
			}
			if (aSpell != null && bTok.countTokens() == 0)
			{
				aSpell.addToSpellBook("Known Spells", false);
				addSpellBook("Known Spells");
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
				if (Globals.isDebugMode())
					System.out.println("Line " + i + ": " + cString);
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
/* added 08/27/01 by TNC */
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
					if (cString.startsWith("BONUS"))
					{
						aClass.getBonusList().add(cString.substring(6));
						if (cString.lastIndexOf("|PCLEVEL|") > -1)
						{
							StringTokenizer cTok = new StringTokenizer(cString.substring(cString.lastIndexOf("PCLEVEL")), "|", false);
							cTok.nextToken(); // should be PCLEVEL
							if (cTok.hasMoreTokens())
								specialAbilityList.add("Bonus Caster Level for " + cTok.nextToken());
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
			while (k > 0 && lastLineParsed.charAt(k - 1) == '\\')
				k = lastLineParsed.indexOf(':', k + 1);
			if (k < 0 || lastLineParsed.charAt(k - 1) == '\\') k = -1;
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
					k = lastLineParsed.indexOf(':', k + 1);
				cString = cString.concat(lastLineParsed.substring(0, k));
				if (Globals.isDebugMode())
					System.out.println("Line " + i + ": " + cString);
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
			eq = (Equipment)Globals.getEquipmentNamed(aName);
			if (eq == null)
			{
				eq = new Equipment();
				bFound = false;
			}
			else
				bFound = true;

			eq = (Equipment)eq.clone();
			eq.setQty(aTok.nextToken());
			if (eq.qty().floatValue() > 1 && eq.acceptsChildren())  //hack to see if it is a headerParent instead of a normal container
			{
				int origQty = eq.qty().intValue();
				eq.setIsHeaderParent(true);

				StringTokenizer cTok = new StringTokenizer(aTok.nextToken(), "|", false);

				if (cTok.countTokens() == 1)
				{
					if (Globals.isDebugMode())
						System.out.println("Correct Path " + cTok.countTokens());

					boolean firstEquipped = cTok.nextToken().equals("Y");
					int numberCarried = (parseCarried(new Float(origQty), aTok.nextToken())).intValue();

					Equipment aHChild = null;
					for (int i = 0; i < origQty; i++)
					{
						aHChild = eq.createHeaderParent();
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
					Equipment aHChild = null;
					StringTokenizer bTok = null;
					for (int i = 0; i < origQty; i++)
					{
						aHChild = eq.createHeaderParent();
						aHChild.clearHeaderChildren();
						aHChild.setIsEquipped(cTok.nextToken().equals("Y"));
						bTok = new StringTokenizer(cTok.nextToken(), "@", false);
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
				eq.setNumberEquipped(Integer.parseInt(aTok.nextToken()));

			if (bFound)
			{
				equipmentList.put(eq.getKeyName(), eq);
				equipmentList.putAll(headerChildren);
			}
			else
			{
				System.out.println("Equipment \"" + aName + "\" not found.");
			}
		}
		//now insert parent/child relationships
		Equipment aParent = null;
		for (Iterator e = containers.keySet().iterator(); e.hasNext();)
		{
			aName = (String)e.next();
			eq = (Equipment)equipmentList.get(aName);
			aParent = (Equipment)equipmentList.get((String)containers.get(aName));
			aParent.insertChild(eq);
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
			aString = unEscapeColons2(aString);
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
					setHairLength(aString);
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

	private void loadWeaponProfLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			addWeaponProf(aTok.nextToken());
		}
	}

	private void loadLanguagesLine(FileAccess fa, BufferedReader input)
	{
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens())
		{
			addLanguage(aTok.nextToken(), false);
		}
	}

	private void loadRaceLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		String raceName = null;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":");
		int l = 0;
		Integer[] hitPointList = null;
		int x = 0;
		while (aTok.hasMoreElements())
		{
			raceName = (String)aTok.nextElement();
			if (i > 0 && i < 5)
			{
				l = Integer.parseInt(raceName);
			}
			switch (i++)
			{
				case 0:
					setRace(Globals.getRaceKeyed(raceName));
					if (this.getRace().hitDice() != 0)
						hitPointList = new Integer[this.getRace().hitDice()];
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
					setGender(raceName);
					break;
				case 6:
					setHanded(raceName);
					break;
				default:
					l = Integer.parseInt(raceName);
					hitPointList[x++] = new Integer(l);
					if (x == this.getRace().hitDice())
					{
						this.getRace().setHitPointList(hitPointList);
						return;
					}
					break;
			}
		}
	}

	private void loadTemplateLine(FileAccess fa, BufferedReader input)
	{
		String string = fa.readLine(input);
		if (string == null)
		{
			return;
		}
		else
		{
			if (string.startsWith("TEMPLATE:"))
				string = string.substring(9);
			final StringTokenizer tokens = new StringTokenizer(string, ":");
			PCTemplate template = null;
			while (tokens.hasMoreTokens())
			{
				template = Globals.getTemplateNamed(tokens.nextToken());
				if (template != null)
					addTemplate(template);
			}
		}
	}

	private void handleDeityLine(FileAccess fa, BufferedReader input, String deityLine)
	{
		int i = 0;
		final StringTokenizer deityTokenizer = new StringTokenizer(deityLine, ":", false);
		String deityName = null;
		while (deityTokenizer.hasMoreElements())
		{
			deityName = (String)deityTokenizer.nextElement();
			switch (i++)
			{
				case 0:
					setDeity(deityName);
					break;
				default:
					addDomainKeyed(deityName);
					if (domainList.size() == i)
						((Domain)domainList.get(i - 1)).setIsLocked(true);
					break;
			}
		}

	}

//	private void ShareSkill(Skill aSkill,Float aRank,ArrayList aRankList)
//	{
//		//
//		// Handle the simple case separately
//		//
//		if (classList.size() == 0)
//		{
//			if (aRank.doubleValue() != 0.0)
//			{
//				System.out.println( "Unable to add skill: " + aSkill.getName() + " (" + aRank.toString() + "). No classes.");
//			}
//			return;
//		}
//		else if (classList.size() == 1)
//		{
//			PCClass aClass = (PCClass)classList.get(0);
//			aRankList.add(aClass.getName() + ":" + aRank.toString());
//			return;
//		}
//
//		//
//		// This works only if INTELLIGENCE has not been modified
//		//
//		int aSkillPts[] = new int[classList.size()];
//		int classCount = 0;
//		for (Iterator e = classList.iterator(); e.hasNext();)
//		{
//			PCClass aClass = (PCClass)e.next();
//			if (aClass.getName().equals("Domain"))
//				continue;
//
//			int spInit;
//			int spLevel = aClass.getSkillPoints() + getRace().getBonusSkillsPerLevel();
//System.out.println("getSkillPoints=" + aClass.getSkillPoints() + " getBonusSkillsPerLevel=" + getRace().getBonusSkillsPerLevel());
//			if (!aClass.isMonster() || aClass.getIntModToSkills())
//				spLevel += calcStatMod(Globals.INTELLIGENCE);
//			if (spLevel < 1)
//				spLevel = 1;
//
//			//
//			// If 1st class, then need to add initial skill points
//			//
//			if (classCount == 0)
//				spInit = spLevel * (getRace().getInitialSkillMultiplier() - 1);
//			else
//				spInit = 0;
//
//System.out.println("spLevel=" + spLevel + " spInit=" + spInit + " getLevel=" + aClass.getLevel().intValue());
//			aSkillPts[classCount] = (spLevel * aClass.getLevel().intValue()) + spInit;
//System.out.println(classCount + ". aClass: " + aClass.getName() + "  pts=" + aSkillPts[classCount] );
//			classCount += 1;
//		}
//System.out.println("skillPoints=" + this.getSkillPoints());
//	}


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
				final Integer iCount =  new Integer((String)skillTokenizer.nextElement());
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
//				//
//				// For older versions need to split the skill into classes
//				//
//				if (PcgReadVersion < 2)
//				{
//					ShareSkill(aSkill, aFloat, aRankList);
//				}

				for (int i = 0; i < aRankList.size(); i++)
				{
					String bRank = (String)aRankList.get(i);
					int iOffs = bRank.indexOf(':');
					Float fRank = new Float(bRank.substring(iOffs+1));
					PCClass aClass = getClassKeyed(bRank.substring(0,iOffs));
					if ((aClass != null) || bRank.substring(0,iOffs).equals("None"))
					{
//						int iCost = aSkill.costForPCClass(aClass).intValue();
//						Float f2 = new Float(iCost * fRank.doubleValue());
//						Integer iRank = new Integer(aClass.getSkillPool().intValue() + f2.intValue());
//System.out.println( "iCost=" + iCost + "  fRank=" + fRank + "  f2=" + f2 + "  iRank=" + iRank );
//						aClass.setSkillPool(iRank);

						bRank = aSkill.modRanks(fRank.doubleValue(), aClass, true);
						if (bRank.length() != 0)
							System.out.println("loadSkillsLine: " + bRank);
					}
					else
					{
						System.out.println("Class not found: " + bRank.substring(0,iOffs));
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
			if ((aFeat != null) && !hasFeatAutomatic(aName)) // featAutoList().contains(aFeat)))
			{
				aFeat = (Feat)aFeat.clone();
				modFeat(aFeat.getKeyName(), true, !aFeat.isMultiples());
				if (aFeat.isMultiples() && aFeat.associatedList().size() == 0 && getFeatKeyed(aFeat.getKeyName()) == null)
					featList.add(aFeat);
				aFeat = getFeatKeyed(aFeat.getKeyName());
				while (bTok.hasMoreTokens())
				{
					aString = bTok.nextToken();
					if (aString.startsWith("BONUS") && aString.length() > 6)
						aFeat.bonusList.add(aString.substring(6));
					aFeat.saveList.add(aString);
				}
			}
			else
				aFeat = new Feat();
			for (int j = 0; j < l; j++)
			{
				aString = aTok.nextToken();
				if ((aFeat.isMultiples() && aFeat.isStacks()) || !aFeat.associatedList().contains(aString))
					aFeat.associatedList().add(aString);
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
		while (aTok.hasMoreTokens())
		{
			if (getNext)
				aName = aTok.nextToken();
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

			if (aClass == null)
			{
				JOptionPane.showMessageDialog(null, "Class not found: " + aName + ". Check loaded campaigns.", "PCGen", JOptionPane.ERROR_MESSAGE);
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
				int iHp = new Integer(aTok.nextToken()).intValue();
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
				String statString = "STRDEXCONINTWISCHA";
				if ((statString.lastIndexOf(aString.toUpperCase()) > -1) || aString.equalsIgnoreCase("None") || aString.equalsIgnoreCase("Any"))
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
	}

	private void loadStatsLine(FileAccess fa, BufferedReader input)
	{
		int i = 0;
		lastLineParsed = fa.readLine(input);
		final StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
		while (aTok.hasMoreTokens() && i < 6)
		{
			getStats()[i++] = Integer.parseInt(aTok.nextToken());
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
		name = aTok.nextToken();
		if (aTok.hasMoreTokens())
			playersName = aTok.nextToken();
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
}

