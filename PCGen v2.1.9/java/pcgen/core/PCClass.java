/*
 * PCClass.java
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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.gui.Chooser;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PCClass extends PObject
{
	private String alignments = new String();
	private String subClassName = "None";
	private String subClassString = "None";
	private String prohibitedString = "None";
	private int hitDie = 0;
	private int skillPoints = 0;
	private int initialFeats = 0;
	private String spellBaseStat = "WIS";
	private String spellType = "Divine";
	private String attackBonusType = new String();
	private String fortitudeCheckType = new String();
	private String reflexCheckType = new String();
	private String willCheckType = new String();
	private ArrayList knownList = new ArrayList();
	private ArrayList castList = new ArrayList();
	private ArrayList uattList = new ArrayList();
	private ArrayList udamList = new ArrayList();
	private ArrayList acList = new ArrayList();
	private TreeSet languageAutos = new TreeSet();
	private TreeSet languageBonus = new TreeSet();
	private ArrayList weaponProfAutos = new ArrayList();
	private ArrayList featAutos = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
	private Integer level = new Integer(0);
	private Integer[] hitPointList = new Integer[1];
	private ArrayList spellList = new ArrayList();
	private ArrayList featList = new ArrayList();
	private ArrayList vFeatList = new ArrayList();
	private ArrayList domainList = new ArrayList();
	private ArrayList addList = new ArrayList();
	private ArrayList levelAbilityList = new ArrayList();
	private ArrayList specialAbilityList = new ArrayList();
	private ArrayList subSpecialAbilityList = new ArrayList();
	private ArrayList umult = new ArrayList();
	private Integer skillPool = new Integer(0);
	private String goldString = new String();
	private String specialsString = new String();
	private ArrayList skillList = new ArrayList();
	private String defenseString = "1,1";
	private String reputationString = "1";
	private String abbrev = new String();
	private boolean memorizeSpells = true;
	private int initMod = 0;
	private boolean multiPreReqs = false;
	private String deityString = "ANY";
	private ArrayList specialtyList = new ArrayList();
	private int maxLevel = 20;
	private ArrayList knownSpellsList = new ArrayList();
	private String attackCycle = "";
	private String castAs = "";
	private int knownSpellsFromSpecialty = 0;
	private String classType = null;
	private String preRaceType = null;  //since I don't want this to be counted as making it a prestige class.
	private boolean intModToSkills = true;
	private int levelsPerFeat = 3;
	private int ageSet = 2;


	public boolean isAPCClass()
	{
		return true;
	}

	public String getReputationString()
	{
		return reputationString;
	}

	public boolean getMemorizeSpells()
	{
		return memorizeSpells;
	}

	public String getDeityString()
	{
		return deityString;
	}

	public Collection getSpecialtyList()
	{
		return specialtyList;
	}

	public String getAbbrev()
	{
		return abbrev;
	}

	public Integer getHitPointList(int aLevel)
	{
		return hitPointList[aLevel];
	}

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		hitPointList[aLevel] = iRoll;
	}

	public void setSpellList(Collection spellList)
	{
		this.spellList = (ArrayList)spellList;
	}

	public Integer getSkillPool()
	{
		return skillPool;
	}

	public void setSkillPool(Integer skillPool)
	{
		this.skillPool = skillPool;
	}

	public Object clone()
	{
		PCClass aClass = (PCClass)super.clone();
		aClass.setSubClassName(getSubClassName());
		aClass.setSubClassString(getSubClassString());
		aClass.setProhibitedString(getProhibitedString());
		aClass.setAlignments(getAlignments());
		aClass.setHitDie(hitDie);
		aClass.setSkillPoints(skillPoints);
		aClass.setInitialFeats(initialFeats);
		aClass.setSpellBaseStat(spellBaseStat);
		aClass.setSpellType(spellType);
		aClass.setAttackBonusType(attackBonusType);
		aClass.setFortitudeCheckType(fortitudeCheckType);
		aClass.setReflexCheckType(reflexCheckType);
		aClass.setWillCheckType(willCheckType);
		aClass.knownList = (ArrayList)knownList.clone();
		aClass.castList = (ArrayList)castList.clone();
		aClass.uattList = (ArrayList)uattList.clone();
		aClass.udamList = (ArrayList)udamList.clone();
		aClass.umult = (ArrayList)umult.clone();
		aClass.acList = (ArrayList)acList.clone();
		aClass.languageAutos = (TreeSet)languageAutos.clone();
		aClass.languageBonus = (TreeSet)languageBonus.clone();
		aClass.weaponProfAutos = (ArrayList)weaponProfAutos.clone();
		aClass.weaponProfBonus = (ArrayList)weaponProfBonus.clone();
		aClass.hitPointList = (Integer[])hitPointList().clone();
		aClass.spellList = (ArrayList)spellList.clone();
		aClass.featList = (ArrayList)featList.clone();
		aClass.vFeatList = (ArrayList)vFeatList.clone();

		aClass.levelAbilityList = (ArrayList)levelAbilityList.clone();
		for (Iterator it = levelAbilityList.iterator(); it.hasNext();)
		{
			LevelAbility ab = (LevelAbility)it.next();
			ab.setOwner(aClass);
		}

		aClass.specialAbilityList = (ArrayList)specialAbilityList.clone();
		aClass.subSpecialAbilityList = (ArrayList)subSpecialAbilityList.clone();
		aClass.setGoldString(goldString);
		aClass.setSpecialsString(specialsString);
		aClass.setDefenseString(defenseString);
		aClass.setReputationString(reputationString);
		aClass.abbrev = abbrev;
		aClass.memorizeSpells = memorizeSpells;
		aClass.multiPreReqs = multiPreReqs;
		aClass.isSpecified = isSpecified;
		aClass.deityString = deityString;
		aClass.maxLevel = maxLevel;
		aClass.knownSpellsList = (ArrayList)knownSpellsList.clone();
		aClass.attackCycle = attackCycle;
		aClass.castAs = castAs;
		aClass.classType = classType;
		aClass.preRaceType = preRaceType;
		aClass.intModToSkills = intModToSkills;
		aClass.levelsPerFeat = levelsPerFeat;
		aClass.initMod = initMod;
		aClass.specialtyList = (ArrayList)specialtyList.clone();
		aClass.ageSet = ageSet;
		aClass.domainList = (ArrayList)domainList.clone();
		return aClass;
	}

	public int getAgeSet()
	{
		return ageSet;
	}

	/**
	 * @return true if the character memorizes spells (wizard, cleric) false if not (sorcerer, bard)
	 */
	public boolean memorizesSpells()
	{
		return memorizeSpells;
	}

	public boolean multiPreReqs()
	{
		return multiPreReqs;
	}

	public ArrayList getDomainList()
	{
		return domainList;
	}

	public String qualifiedNameString()
	{
		String aString = null;
		if (Globals.getCurrentPC() != null && (!allowsAlignment(Globals.getCurrentPC().getAlignment()) || !canBePrestige()))
			aString = "* ";
		else
			aString = "";
		if (aString != null)
		{
			aString = aString.concat(name);
		}
		else
		{
			aString = name;
		}
		return aString;
	}

	public String toString()
	{
		return name;
	}

	public void setName(String newName)
	{
		super.setName(newName);
		int i = 3;
		if (abbrev.equals(""))
		{
			if (newName.length() < 3)
				i = newName.length();
			abbrev = newName.substring(0, i);
		}
	}

	public void setCastAs(String aString)
	{
		castAs = aString;
	}

	public String getCastAs()
	{
		return castAs;
	}

	public String getSubClassName()
	{
		if (subClassName == null)
			subClassName = "";
		return subClassName;
	}

	public String getDisplayClassName()
	{
		if (subClassName.length() > 0 && !subClassName.equals("None"))
			return subClassName;
		return name;
	}

	public void setSubClassName(String aString)
	{
		subClassName = aString;
	}

	public String getSubClassString()
	{
		return subClassString;
	}

	private void setSubClassString(String aString)
	{
		subClassString = aString;
	}

	public String getProhibitedString()
	{
		return prohibitedString;
	}

	public void setProhibitedString(String aString)
	{
		prohibitedString = aString;
	}

	public boolean prohibitedStringContains(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(prohibitedString, ",", false);
		while (aTok.hasMoreTokens())
			if (aTok.nextToken().equals(aString))
				return true;
		return false;
	}

	public boolean prohibitedStringContains(Collection aList)
	{
		for (Iterator e = aList.iterator(); e.hasNext();)
			if (prohibitedStringContains((String)e.next()))
				return true;
		return false;
	}

	public Collection getBonusList()
	{
		return bonusList;
	}

	public int getBonusTo(String type, String mname, int asLevel)
	{
		int i = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();

		String aString = null;
		StringTokenizer aTok = null;
		String aType = null;
		String aName = null;
		StringTokenizer bTok = null;
		type = type.toUpperCase();
		mname = mname.toUpperCase();
		for (Iterator e = bonusList.iterator(); e.hasNext();)
		{
			aString = ((String)e.next()).toUpperCase();
			aTok = new StringTokenizer(aString, "|", false);
			int anInt = Integer.parseInt(aTok.nextToken());
			aType = aTok.nextToken();
			if (!aType.equals(type))
				continue;
			aName = aTok.nextToken();
			bTok = new StringTokenizer(aName, ",", false);
			while (bTok.hasMoreTokens())
			{
				aName = bTok.nextToken();
				if (anInt <= asLevel && aType.equals(type) &&
					aName.equals(mname))
				{
					aString = aTok.nextToken();
					ArrayList preReqList = new ArrayList();
					while (aTok.hasMoreTokens())
					{
						String bString = aTok.nextToken();
						if (bString.startsWith("PRE"))
							preReqList.add(bString);
					}
					// must meet criteria for bonuses before adding them in
					if (passesPreReqTestsForList(preReqList))
						i += aPC.getVariableValue(aString, "", "").intValue();
				}
			}
		}
		return i;
	}

	public int bonusBasedOnStat(String type, String mname, int asLevel)
	{
		int i = -1;
		String aString = null;
		StringTokenizer aTok = null;
		for (Iterator e = bonusList.iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			aTok = new StringTokenizer(aString, "|", false);
			int anInt = Integer.parseInt(aTok.nextToken());
			if (anInt <= asLevel && aTok.nextToken().equals(type) &&
				aTok.nextToken().lastIndexOf(mname) > -1)
			{
				aString = aTok.nextToken();
				if (aString.length() > 2)
					i = (s_STATNAMES.lastIndexOf(aString.substring(0, 3)) + 3) / 3;
				if (i == 0)
					i = -1;
				else
				{
					i--;
					break;
				}
			}
		}
		return i;
	}

	public String getAlignments()
	{
		return alignments;
	}

	private void setAlignments(String newAlign)
	{
		alignments = newAlign;
	}

	public boolean allowsAlignment(int index)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (int i = 0; i < getAlignments().length(); i++)
		{
			if (getAlignments().charAt(i) >= '0' && getAlignments().charAt(i) <= '9' &&
				Integer.parseInt(getAlignments().substring(i, i + 1)) == index)
				return true;
			if (getAlignments().charAt(i) == 'D' && aPC.getDeity() != null &&
				aPC.getDeity().allowsAlignment(index))
				return true;
		}
		return false;
	}

	private boolean canBePrestige()
	{
		return passesPreReqTests();
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public String prestigeString()
	{
		return preReqStrings();
	}

// HitDieLock has a default value of "%/1", so it will divide the hitdie by one which is safe.
// --- arcady
	public int getHitDie()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String dieLock = aPC.getRace().getHitDieLock();
		int dieSizes[] = Globals.getDieSizes();
//		int dieSizes[] = { 4, 6, 8, 10, 12 }; // Hit dice seem to be only valid in these ranges.
		int diedivide = 1;

		if (dieLock.startsWith("%/")) {
			diedivide = Integer.parseInt(dieLock.substring(2));
			if (diedivide <= 0) diedivide = 1; // Idiot proof it. Stop Divide by zero errors.
			diedivide = hitDie/diedivide;
		} else if (dieLock.startsWith("%*")) {
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide = hitDie*diedivide;
		} else if (dieLock.startsWith("%+")) { // possibly redundant with BONUS:HD}MAX|num
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide = hitDie+diedivide;
		} else if (dieLock.startsWith("%-")) { // possibly redundant with BONUS:HD}MAX|num if that will take negative numbers.
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide = hitDie-diedivide;
		} else if (dieLock.startsWith("%up")) {
			int i;
			diedivide = Integer.parseInt(dieLock.substring(3));
			// lock in valid values.
			if (diedivide > 4) diedivide = 4; 
			if (diedivide < 0) diedivide = 0; 
			for (i = 3; i <= (7-diedivide); i++) {
				if (hitDie == dieSizes[i]) {
					i += diedivide;
					return dieSizes[i];
				}
			}
			diedivide = dieSizes[7]; // If they went too high, they get maxed out.
		} else if (dieLock.startsWith("%Hup")) {
			int i;
			diedivide = Integer.parseInt(dieLock.substring(4));
			for (i = 0; i < ((dieSizes.length)-diedivide); i++) {
				if (hitDie == dieSizes[i]) {
					i += diedivide;
					return dieSizes[i];
				}
			}
			diedivide = dieSizes[dieSizes.length]; // If they went too high, they get maxed out.
		} else if (dieLock.startsWith("%down")) {
			int i;
			diedivide = Integer.parseInt(dieLock.substring(5));
			// lock in valid values.
			if (diedivide > 4) diedivide = 4; 
			if (diedivide < 0) diedivide = 0; 
			for (i = (3+diedivide); i <= 7; i++) {
				if (hitDie == dieSizes[i]) {
					i -= diedivide;
					return dieSizes[i];
				}
			}
			diedivide = dieSizes[3]; // Minimum valid if too low.
		} else if (dieLock.startsWith("%Hdown")) {
			int i;
			diedivide = Integer.parseInt(dieLock.substring(5));
			for (i = diedivide; i < dieSizes.length; i++) {
				if (hitDie == dieSizes[i]) {
					i -= diedivide;
					return dieSizes[i];
				}
			}
			diedivide = dieSizes[0]; // floor them if they're too low.
		} else {
			diedivide = Integer.parseInt(dieLock);
		}
		if (diedivide <= 0) diedivide = 1; // Idiot proof it.
		return diedivide;
	}

	private void setHitDie(int dice)
	{
		hitDie = dice;
	}

	public int getSkillPoints()
	{
		return skillPoints;
	}

	private void setSkillPoints(int points)
	{
		skillPoints = points;
	}

	public int getInitialFeats()
	{
		return initialFeats;
	}

	private void setInitialFeats(int feats)
	{
		initialFeats = feats;
	}

	public String getSpellBaseStat()
	{
		return spellBaseStat;
	}

	public void setSpellBaseStat(String baseStat)
	{
		spellBaseStat = baseStat;
	}

	public String getSpellType()
	{
		return spellType;
	}

	private void setSpellType(String newType)
	{
		spellType = newType;
	}

	public String getAttackBonusType()
	{
		return attackBonusType;
	}

	private void setAttackBonusType(String aString)
	{
		attackBonusType = aString;
	}

	public String getFortitudeCheckType()
	{
		return fortitudeCheckType;
	}

	private void setFortitudeCheckType(String aString)
	{
		fortitudeCheckType = aString;
	}

	public String getReflexCheckType()
	{
		return reflexCheckType;
	}

	private void setReflexCheckType(String aString)
	{
		reflexCheckType = aString;
	}

	public String getWillCheckType()
	{
		return willCheckType;
	}

	private void setWillCheckType(String aString)
	{
		willCheckType = aString;
	}

	public boolean getIntModToSkills()
	{
		return intModToSkills;
	}

	public void setIntModToSkills(boolean bool)
	{
		intModToSkills = bool;
	}

	public int getLevelsPerFeat()
	{
		return levelsPerFeat;
	}

	public void setLevelsPerFeat(int newLevels)
	{
		if (newLevels < 0)
			return;
		levelsPerFeat = newLevels;
	}

	public Collection getKnownList()
	{
		return knownList;
	}

	public int baseSpellIndex()
	{
		if (getSpellBaseStat().equals("STR"))
			return Globals.STRENGTH;
		else if (getSpellBaseStat().equals("DEX"))
			return Globals.DEXTERITY;
		else if (getSpellBaseStat().equals("CON"))
			return Globals.CONSTITUTION;
		else if (getSpellBaseStat().equals("INT"))
			return Globals.INTELLIGENCE;
		else if (getSpellBaseStat().equals("WIS"))
			return Globals.WISDOM;
		else if (getSpellBaseStat().equals("CHA"))
			return Globals.CHARISMA;
		else if (getSpellBaseStat().equals("SPELL"))
			return -2; // means base spell stat is based upon spell itself

		return -1;
	}

	public int getKnownForLevel(int pcLevel, int spellLevel)
	{
		int total = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = aPC.getTotalBonusTo("SPELLKNOWN", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
			aPC.getTotalBonusTo("SPELLKNOWN", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		PCClass aClass = null;
		for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
		{
			aClass = (PCClass)e1.next();
			pcLevel += aClass.getBonusTo("PCLEVEL", name, 0);
		}
		int index = baseSpellIndex();
		if (aPC.adjStats(index) < 10 + spellLevel && index != -2)
			return total;
		String aString = null;
		StringTokenizer aTok = null;
		for (Iterator e = knownList.iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (pcLevel == 1)
			{
				aTok = new StringTokenizer(aString, ",", false);
				while (aTok.hasMoreTokens())
				{
					final int t = Integer.parseInt((String)aTok.nextElement());
					if (spellLevel == 0)
					{
						total += t;
						break;
					}
					spellLevel--;
				}
			}
			pcLevel--;
			if (pcLevel < 1)
				break;
		}
		return total;
	}

	public List getCastList()
	{
		return castList;
	}

	public int getKnownSpellsFromSpecialty()
	{
		return knownSpellsFromSpecialty;
	}

	public void setKnownSpellsFromSpecialty(int anInt)
	{
		knownSpellsFromSpecialty = anInt;
	}

	public String getCastForLevelString(int pcLevel, int spellLevel, String bookName)
	{
		int total = 0;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = aPC.getTotalBonusTo("SPELLCAST", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
			aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		stat = aPC.adjStats(baseSpellIndex());
		String statString = "None";

		int index = baseSpellIndex();
		if (index >= 0)
			statString = s_STATNAMES.substring(index * 3, index * 3 + 3);

		stat += aPC.getTotalBonusTo("STAT", "CAST=" + statString, true);
		stat += aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true);

		if (stat < 10 + spellLevel && index != -2)
			return new Integer(total).toString();
		int adj = 0;
		if (specialtyList.size() > 0)
		{
			adj = 1;
		}
		int temp = spellLevel;
		int spells = 0;
		if (name.equals("Domain") && spellLevel == 0)
			return "0";
		PCClass aClass = null;
		for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
		{
			aClass = (PCClass)e1.next();
			pcLevel += aClass.getBonusTo("PCLEVEL", name, 0);
			if (Globals.isDebugMode())
			{
				for (Iterator xi = aClass.getBonusList().iterator(); xi.hasNext();)
				{
					System.out.println(xi.next().toString());
				}
			}
			if (getName().startsWith("Domain") && aClass.getBonusTo("DOMAIN", "NUMBER", aClass.getLevel().intValue()) > 0)
				if (aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel) > spells)
					spells = aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel);
		}
		if (spells > 0)
			return "1";
		if (getName().startsWith("Domain"))
			return "0";
		if (pcLevel > castList.size())
			pcLevel = castList.size();

		//
		// Multiplier for things like Ring of Wizardry
		//
		int mult = aPC.getTotalBonusTo("SPELLCASTMULT", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
			aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);

		if (mult < 1)
		{
			mult = 1;
		}

		String aString = null;
		for (Iterator e = castList.iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (pcLevel == 1)
			{
				StringTokenizer aTok = new StringTokenizer(aString, ",", false);
				while (aTok.hasMoreTokens())
				{
					final int t = Integer.parseInt((String)aTok.nextElement());
					if (spellLevel == 0)
					{
						total += (t * mult);
						if ((stat - 10) / 2 >= temp && temp > 0)
						{
							total += (((stat - 10) / 2 - temp) / 4) + 1;
						}
						break;
					}
					spellLevel--;
				}
			}
			pcLevel--;
			if (pcLevel < 1)
				break;
		}
		String bString = String.valueOf(total);
		if (adj > 0)
			bString = bString + "+" + new Integer(adj).toString();
		return bString;
	}

	public int getCastForLevel(int pcLevel, int spellLevel)
	{
		return getCastForLevel(pcLevel, spellLevel, "Known Spells");
	}

	public int getCastForLevel(int pcLevel, int spellLevel, String bookName)
	{
		int total = 0;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = aPC.getTotalBonusTo("SPELLCAST", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
			aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		stat = aPC.adjStats(baseSpellIndex());
		String statString = "None";

		int index = baseSpellIndex();
		if (index >= 0)
			statString = s_STATNAMES.substring(index * 3, index * 3 + 3);
		stat += aPC.getTotalBonusTo("STAT", "CAST=" + statString, true);
		stat += aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true);
		if (stat < 10 + spellLevel && index != -2)
			return total;
		int adj = 0;
		if (!bookName.equals("Known Spells") && specialtyList.size() > 0)
		{
			Spell aSpell = null;
			for (Iterator e = spellList().iterator(); e.hasNext();)
			{
				aSpell = (Spell)e.next();
				if (aSpell.levelForClass(name) == spellLevel && aSpell.getSpellBooks().contains(bookName))
				{
					if (specialtyList.contains(aSpell.getSchool()))
						adj = 1;
					for (int j = 0; j < aSpell.getDescriptorList().size(); j++)
						if (specialtyList.contains((String)aSpell.getDescriptorList().get(j)))
							adj = 1;
				}
				if (adj != 0)
					break;
			}
		}
		int temp = spellLevel;
		int spells = 0;
		if (name.equals("Domain") && spellLevel == 0)
			return 0;
		PCClass aClass = null;
		for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
		{
			aClass = (PCClass)e1.next();
			pcLevel += aClass.getBonusTo("PCLEVEL", name, 0);
			if (Globals.isDebugMode())
			{
				for (Iterator xi = aClass.getBonusList().iterator(); xi.hasNext();)
				{
					System.out.println(xi.next().toString());
				}
			}
			if (getName().startsWith("Domain") && aClass.getBonusTo("DOMAIN", "NUMBER", aClass.getLevel().intValue()) > 0)
				if (aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel) > spells)
					spells = aClass.getCastForLevel(aClass.getLevel().intValue(), spellLevel);
		}
		if (spells > 0)
			return 1;
		if (getName().startsWith("Domain"))
			return 0;
		if (pcLevel > castList.size())
			pcLevel = castList.size();

		//
		// Multiplier for things like Ring of Wizardry
		//
		int mult = aPC.getTotalBonusTo("SPELLCASTMULT", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
			aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);

		if (mult < 1)
		{
			mult = 1;
		}

		String aString = null;
		for (Iterator e = castList.iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			if (pcLevel == 1)
			{
				final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
				while (aTok.hasMoreTokens())
				{
					final int t = Integer.parseInt((String)aTok.nextElement());
					if (spellLevel == 0)
					{
						total += (t * mult) + adj;
						if ((stat - 10) / 2 >= temp && temp > 0)
						{
							total += (((stat - 10) / 2 - temp) / 4) + 1;
						}
						break;
					}
					spellLevel--;
				}
			}
			pcLevel--;
			if (pcLevel < 1)
				break;
		}
		return total;
	}

	public Collection getUattList()
	{
		return uattList;
	}

	public String getUattForLevel(int aLevel)
	{
		String aString = "0";
		if (getUattList().isEmpty())
			return aString;

		String bString = null;
		for (Iterator e = getUattList().iterator(); e.hasNext();)
		{
			bString = (String)e.next();
			if (aLevel == 1)
				return bString;
			aLevel--;
			if (aLevel < 1)
				break;
		}
		return null;
	}

	public String getUMultForLevel(int aLevel)
	{
		String aString = "0";

		String bString = null;
		for (Iterator e = umult.iterator(); e.hasNext();)
		{
			bString = (String)e.next();
			int pos = bString.lastIndexOf("|");
			if (pos >= 0 && aLevel <= Integer.parseInt(bString.substring(0, pos)))
				aString = bString.substring(pos + 1);

		}
		return aString;
	}

	public Collection getUdamList()
	{
		return udamList;
	}

	public String getUdamForLevel(int aLevel, boolean includeCrit, boolean includeStrBonus)
	{
		String aString;
		int i = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC.getSize().equals("S"))
			aString = "1d2";
		else
		{
			i = 1;
			aString = "1d3";
		}

		String bString = null;
		StringTokenizer aTok = null;
		for (Iterator e = getUdamList().iterator(); e.hasNext();)
		{
			bString = (String)e.next();
			if (aLevel == 1)
			{
				aTok = new StringTokenizer(bString, ",", false);
				String cString = null;
				while (i > -1 && aTok.hasMoreTokens())
				{
					cString = aTok.nextToken();
					if (i == 0)
					{
						aString = cString;
						break;
					}
					i--;
				}
			}
			aLevel--;
			if (aLevel < 1)
				break;
		}
		if (includeStrBonus && aPC.adjStats(Globals.STRENGTH) / 2 > 5)
			aString = aString + "+";
		if (includeStrBonus && aPC.adjStats(Globals.STRENGTH) / 2 != 5)
			aString = aString + new Integer(aPC.calcStatMod(Globals.STRENGTH)).toString();
		if (includeCrit)
		{
			String dString = getUMultForLevel(aLevel);
			if (!dString.equals("0"))
				aString = aString + "(x" + dString + ")";
		}
		return aString;
	}


	public String getMoveForLevel(int aLevel)
	{
		String aString = "0";
		int iAmount; // Amount of Progression Bonus
		int iCount;  // Number of Times to Add Progression Bonus
		int iTotal;  // Total Movement after Progression

		/**
		 * This is a Kludge for determining if SA Exists
		 * Please Change if there is a more efficient way
		 * J. Bennett
		 */
		if (!specialAbilityList.contains(Integer.toString(aLevel) + ":FastMove"))
			return aString;
		int move = Globals.getCurrentPC().getRace().getMovement().intValue();
		iTotal = 0;
		if (aLevel <= 2)
		{
			iCount = 0;
		}
		else
		{
			iCount = aLevel / 3;
		}

		/**
		 * Base Movement of 20 is the exception to the rule
		 * Move Progression is in following pattern: +5, +10, +5, +5, +10 ...
		 */

		if (move == 20)
		{
			int i = 0;
			while (i < iCount)
			{
				if ((i % 5) == 1 || (i % 5) == 4)
					iAmount = 10;
				else
					iAmount = 5;
				iTotal += iAmount;
				i++;
			}
		}
		else
		{
			iTotal += (((move / 15) * 5) * iCount);
		}
		return Integer.toString(iTotal);
	}

	public Collection getAcList()
	{
		return acList;
	}

	public String getACForLevel(int aLevel)
	{
		String aString = "0";
		if (getAcList().isEmpty())
			return aString;

		String bString = null;
		for (Iterator e = getAcList().iterator(); e.hasNext();)
		{
			bString = (String)e.next();
			if (aLevel == 1)
				return bString;
			aLevel--;
			if (aLevel < 1)
				break;
		}
		return aString;
	}

	public Set getLanguageAutos()
	{
		return languageAutos;
	}

	private void setLanguageAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			getLanguageAutos().add(aTok.nextToken());
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	private void setLanguageBonus(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			getLanguageBonus().add(aTok.nextToken());
	}

	public Collection getWeaponProfAutos()
	{
		return weaponProfAutos;
	}

	private void setWeaponProfAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			getWeaponProfAutos().add(aTok.nextToken());
	}

	public Collection getFeatAutos()
	{
		return featAutos;
	}

	private void setFeatAutos(int aLevel, String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			getFeatAutos().add(aLevel + "|" + aTok.nextToken());
	}

	public Collection getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	private void setWeaponProfBonus(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			getWeaponProfBonus().add(aTok.nextToken());
	}

	public Integer getLevel()
	{
		return level;
	}

	public String getType()
	{
		return classType;
	}

	public void setType(String newType)
	{
		if (!newType.equals("NPC") && !newType.equals("Monster"))
		{
			JOptionPane.showMessageDialog(null, "Invalid Class Type in class " + name, "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		classType = newType;
	}

	public void setLevel(Integer newLevel)
	{
		if (newLevel.intValue() >= 0)
			level = newLevel;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (newLevel.intValue() != 0 && knownSpellsList.size() > 0 || getName().startsWith("Domain"))
		{
			Spell aSpell = null;
			for (Iterator e = Globals.getSpellMap().values().iterator(); e.hasNext();)
			{
				aSpell = (Spell)e.next();
				int i = aSpell.levelForClass(name);
				if (i == -1 && name.startsWith("Domain"))
				{
					i = aSpell.levelForClass(aPC.domainClassName());
				}
				if (i >= 0 && isAutoKnownSpell(aSpell.getKeyName(), i))
				{
					boolean addIt = getCastForLevel(getLevel().intValue(), i) > 0;
					Spell bSpell = null;
					for (Iterator e1 = spellList().iterator(); e1.hasNext();)
					{
						bSpell = (Spell)e1.next();
						if (bSpell.getName().equals(aSpell.getName()))
						{
							if (addIt == false)
							{
								e1.remove();
							}
							break;
						}
						bSpell = null;
					}
					if (addIt && bSpell == null)
					{
						Spell newSpell = (Spell)aSpell.clone();
						String className = "";
						if (castAs.length() == 0)
							className = new String(name);
						else
							className = castAs;
						String aString = className + "," + new Integer(i).toString();
						if (className.equals("Domain"))
						{
							aString = "";
							Domain aDomain = null;
							for (Iterator e2 = aPC.getDomainList().iterator(); e2.hasNext();)
							{
								aDomain = (Domain)e2.next();
								if (aSpell.levelForClass(aDomain.getName()) == i)
								{
									if (aString.length() > 0)
										aString = aString + ",";
									aString = aString + aDomain.getName() + "," + new Integer(i).toString();
								}
							}
						}
						newSpell.setClassLevels(aString);
						spellList().add(newSpell);
						if (!newSpell.getSpellBooks().contains("Known Spells"))
							newSpell.addToSpellBook("Known Spells", false);
					}
				}
			}
		}
		if (!getName().startsWith("Domain") && aPC.getDomainMax() > 0)
		{
			PCClass aClass = aPC.getClassNamed("Domain");
			if (aClass != null)
				aClass.setLevel(new Integer(0));
		}
	}

	public void addLevel(boolean levelMax)
	{
		Integer newLevel = new Integer(level.intValue() + 1);
		if (isMonster())
			levelMax = false;
		if (newLevel.intValue() > maxLevel && levelMax)
		{
			JOptionPane.showMessageDialog(null, "This class cannot be raised above level " + new Integer(maxLevel).toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int total = aPC.totalLevels();
		if (total == 0)
			aPC.setFeats(aPC.getRace().getBonusInitialFeats());
		setLevel(newLevel);
		int spMod = 0;
		if (isMonster() && !intModToSkills)
			spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel();
		else
		{
			spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel() + aPC.calcStatMod(Globals.INTELLIGENCE);
			if (spMod < 1)
				spMod = 1;
		}
		if (getBonusTo("DOMAIN", "NUMBER", newLevel.intValue()) > 0)
			aPC.modDomainClass(true);
		if (isMonster())
		{
			if (levelsPerFeat != 0)
				if (aPC.totalHitDice() % levelsPerFeat == 0)
					aPC.setFeats(aPC.getFeats() + 1);
		}
		else
			changeFeatsForLevel(newLevel.intValue(), true);
		addAddsForLevel(newLevel.intValue());
		modDomainsForLevel(newLevel.intValue(),true);
		aPC.changeSpecialAbilitiesForLevel(newLevel.intValue(), true, specialAbilityList());
		changeSubSpecialAbilitiesForLevel(newLevel.intValue(), false);
		changeSpecials();
		addVariablesForLevel(newLevel.intValue());
		if (newLevel.intValue() == 1 && !aPC.isImporting())
			checkForSubClass();
		rollHp();

		if (aPC.isImporting() == false)
		{
			LevelAbility ability = null;
			for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
			{
				ability = (LevelAbility)e.next();
				if (ability.level() == newLevel.intValue() && ability.canProcess())
					ability.process();
			}
		}
		if (!isMonster() && aPC.totalLevels() > total)
		{
			total = aPC.totalLevels();
			if (aPC.isImporting() == false)
			{
				// We do not want to do these calculations a second
				// time when are importing a character.  The feat number
				// and the stat point pool are already saved in the import file.
				if (aPC.getExperience().intValue() < minExpForLevel(total + aPC.levelAdjustment()).intValue())
					aPC.setExperience(minExpForLevel(total + aPC.levelAdjustment()));
				if (total % 3 == 0)
					aPC.setFeats(aPC.getFeats() + 1);
				if (total % 4 == 0)
				{
					aPC.setPoolAmount(aPC.getPoolAmount() + 1);
					JOptionPane.showMessageDialog(null, "You can increment a stat on the Stat tab.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		else
			return;
		if (total == 1)
		{
			if (Globals.isPurchaseStatMode())
				aPC.setPoolAmount(0);
			spMod *= aPC.getRace().getInitialSkillMultiplier();
			aPC.getRace().rollAgeForAgeSet(getAgeSet());
			skillPool = new Integer(spMod);
			aPC.getAutoWeaponProfs();
			aPC.setGold(rollGold().toString());
		}
		else
			skillPool = new Integer(skillPool().intValue() + spMod);
		spMod += aPC.getSkillPoints();
		aPC.setSkillPoints(spMod);
		if (aPC.getExperience().intValue() < minExpForLevel(total + aPC.levelAdjustment()).intValue())
			aPC.setExperience(new Integer(minExpForLevel(total + aPC.levelAdjustment()).intValue()));
		else if (aPC.getExperience().intValue() >= minExpForLevel(total + 1 + aPC.levelAdjustment()).intValue())
			JOptionPane.showMessageDialog(null, "You can advance another level with your experience.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
	}

	public void subLevel()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			int total = aPC.totalLevels();
			int spMod = 0;
			if (isMonster() && !intModToSkills)
				spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel();
			else
				spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel() + aPC.calcStatMod(Globals.INTELLIGENCE);

			// XXX Why is the feat decrementing done twice (here and in
			// subAddsForLevel())? The code works correctly, but I don't know
			// why.
			// Also, the use of instanceof is kinda ugly.
			for (Iterator e1 = levelAbilityList.iterator(); e1.hasNext();)
			{
				LevelAbility ability = (LevelAbility)e1.next();
				if (ability.level() == level.intValue() && ability instanceof LevelAbilityFeat)
					aPC.setFeats(aPC.getFeats() - 1);
			}
			Integer zeroInt = new Integer(0);
			Integer newLevel = new Integer(level.intValue() - 1);
			if (level.intValue() > 0)
			{
				hitPointList[level.intValue() - 1] = zeroInt;
			}
			setLevel(newLevel);
			if (!isMonster())
				changeFeatsForLevel(newLevel.intValue() + 1, false);
			else if (levelsPerFeat != 0)
				if ((aPC.totalHitDice() + 1) % levelsPerFeat == 0)
					aPC.setFeats(aPC.getFeats() - 1);
			subAddsForLevel(newLevel.intValue() + 1);
			modDomainsForLevel(newLevel.intValue()+1,false);
			aPC.changeSpecialAbilitiesForLevel(newLevel.intValue() + 1, false, specialAbilityList());
			changeSubSpecialAbilitiesForLevel(newLevel.intValue() + 1, true);
			changeSpecials();
			aPC.removeVariable("CLASS:" + getName() + "|" + new Integer(level.intValue() + 1).toString());
			if (newLevel.intValue() == 0)
				setSubClassName("None");
			if (newLevel.intValue() == 0 && deityString.length() > 0)
			{
				if (aPC.totalLevels() == 0 || getBonusTo("DOMAIN", "NUMBER", newLevel.intValue()) > 0)
				{
					aPC.setDeity(null);
					aPC.getDomainList().clear();
					aPC.modDomainClass(true);
				}
			}
			if (!isMonster() && total > aPC.totalLevels())
			{
				total = aPC.totalLevels();
				if (aPC.getExperience().intValue() >= minExpForLevel(total + 1 + aPC.levelAdjustment()).intValue())
				{
					int minXP = minExpForLevel(total + 1 + aPC.levelAdjustment()).intValue() - 1;
					if (minXP >= 1)
						minXP--;
					else
						minXP = 0;
					aPC.setExperience(new Integer(minXP));
				}
				if (total % 3 == 2)
					aPC.setFeats(aPC.getFeats() - 1);
				if (total % 4 == 3)
				{
					aPC.setPoolAmount(aPC.getPoolAmount() - 1);
					JOptionPane.showMessageDialog(null, "You lost a stat point due to level decrease. See the Stat tab.", "PCGen", JOptionPane.WARNING_MESSAGE);
				}
			}
			if (!isMonster() && total == 0)
			{
				aPC.setSkillPoints(0);
				aPC.setFeats(0);
				aPC.getSkillList().clear();
				aPC.getFeatList().clear();
				aPC.getWeaponProfList().removeAll(Globals.getWeaponProfList());
			}
			else
			{
				aPC.setSkillPoints(aPC.getSkillPoints() - spMod);
				skillPool = new Integer(skillPool().intValue() - spMod);
			}
			if (getLevel().intValue() == 0)
				aPC.getClassList().remove(this);
		}
		else
		{
			System.out.println("No current pc in subLevel()? How did this happen?");
			return;
		}
	}

	private void modDomainsForLevel(int aLevel, boolean adding)
	{
		PlayerCharacter aPC = Globals.getCurrentPC();
		int c = 2;
		if (aLevel>9)
			c=3;
		for(Iterator i=domainList.iterator();i.hasNext();)
		{
			String aString = (String)i.next();
			StringTokenizer aTok = new StringTokenizer(aString,"|",false);
			int bLevel = Integer.parseInt(aTok.nextToken());
			int d=c;
			if (aLevel==bLevel)
			{
				StringTokenizer bTok = new StringTokenizer(aString.substring(c),"[]|",true);
				ArrayList preReqList = new ArrayList();
				boolean addNow=true;
				String aName="";
				boolean inPreReqs=false;
				while (bTok.hasMoreTokens())
				{
					String bString = bTok.nextToken();
					if (!inPreReqs && !bString.equals("[") && !bString.equals("|"))
						aName=bString;
					d+=bString.length();
					if (bTok.hasMoreTokens()) {
						if (aString.substring(d,d+1).equals("["))
							addNow=false;
					} else
						addNow=true;
					if (bString.equals("[")) {
						inPreReqs=true;
					}
					else if (bString.equals("]")) { // this ends a PRExxx tag so next time through we can add name
						addNow=true;
						inPreReqs=false;
					}
					if (addNow && adding==false)
					{
						int l = aPC.getDomainIndex(aName);
						if (l>-1)
							aPC.setDomainNumber(null,l);
					}
					else if (adding==true && addNow==true && aName.length()>0)
					{
						int l = aPC.getDomainIndex(aName);
						if (l==-1 && aPC.getDomainMax()>aPC.getDomainList().size())
						{
							if (preReqList.size()==0) {
								aPC.addDomainNamed(aName);
							}
							else
							{
								Domain aDomain = Globals.getDomainNamed(aName);
								if (aDomain!=null && aDomain.passesPreReqTestsForList(preReqList)) {
									aPC.addDomainNamed(aName);
								}
							}
						}
						preReqList.clear();
						aName="";
					}
					if (adding==true && inPreReqs && bString.startsWith("PRE"))
						preReqList.add(bString);
				}
			}
		}
	}
	static public Integer minExpForLevel(int aLevel)
	{
		Integer min = new Integer(0);
		for (int i = 1; i < aLevel; i++)
			min = new Integer(min.intValue() + 1000 * i);
		return min;
	}

	public int memorizedSpellForLevelBook(int aLevel, String bookName)
	{
		int i = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		Spell aSpell = null;
		for (Iterator e = spellList().iterator(); e.hasNext();)
		{
			aSpell = (Spell)e.next();
			int j = -1;
			if (aSpell.levelForClass(name) == aLevel ||
				(name.equals("Domain") && aSpell.levelForClass(aPC.domainClassName()) == aLevel))
				j = aSpell.getSpellBooks().indexOf(bookName);
			if (j >= 0)
			{
				Integer anInt = (Integer)aSpell.getTimes().get(j);
				i += anInt.intValue();
			}
		}
		return i;
	}

	private void changeSpecials()
	{
		if (specialsString().length() == 0)
			return;
		String className = "";
		Integer adj = new Integer(0);
		String abilityName = "";
		String levelString = "";
		StringTokenizer aTok = new StringTokenizer(specialsString, "|", false);
		ArrayList saList = new ArrayList();
		if (aTok.hasMoreTokens())
			abilityName = aTok.nextToken();
		if (aTok.hasMoreTokens())
			className = aTok.nextToken();
		if (aTok.hasMoreTokens())
			aTok.nextToken(); // adj will be summed later
		if (aTok.hasMoreTokens())
			levelString = aTok.nextToken();
		// first, remove all special abilities by this name
		Iterator e = null;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String aString = null;
		for (int i = aPC.getSpecialAbilityList().size() - 1; i >= 0; i--)
		{
			aString = (String)aPC.getSpecialAbilityList().get(i);
			if (aString.startsWith(abilityName))
				aPC.getSpecialAbilityList().remove(aString);
		}
		// next, determine total 'levels' of ability
		for (e = aPC.getClassList().iterator(); e.hasNext();)
		{
			PCClass aClass = (PCClass)e.next();
			if (aClass.specialsString().length() > 0 && aClass.specialsString().startsWith(abilityName))
			{
				aTok = new StringTokenizer(aClass.specialsString(), "|", false);
				aTok.nextToken();
				aTok.nextToken();
				if (aTok.hasMoreTokens())
					adj = new Integer(adj.intValue() + Integer.parseInt(aTok.nextToken()) + aClass.getLevel().intValue());
				if (aTok.hasMoreTokens())
					levelString = aTok.nextToken(); // need this
			}
		}
		// next add abilities for level based upon levelString
		PCClass aClass = aPC.getClassNamed(className);
		if (aClass == null)
		{
			for (e = Globals.getClassList().iterator(); e.hasNext();)
			{
				aClass = (PCClass)e.next();
				if (aClass.getName().equals(className))
				{
					aTok = new StringTokenizer(aClass.specialsString(), "|", false);
					aTok.nextToken();
					aTok.nextToken();
					aTok.nextToken();
					levelString = aTok.nextToken(); // required
					break;
				}
				aClass = null;
			}
		}
		if (aClass != null && levelString.length() > 0)
		{
			aTok = new StringTokenizer(levelString, ",", false);
			int i = 0;
			Integer aLevel = new Integer(0);
			while (aTok.hasMoreTokens() && adj.intValue() >= aLevel.intValue())
			{
				aLevel = new Integer(aTok.nextToken());
				if (adj.intValue() >= aLevel.intValue())
					saList.add(Globals.getSpecialsList().get(i++));
			}
		}
		for (int i = saList.size() - 1; i >= 0; i--)
		{
			if (i >= saList.size())
				i = saList.size() - 1;
			if (i < 0)
				break;
			SpecialAbility sa1 = (SpecialAbility)saList.get(i);
			String sn1 = new String(sa1.getDesc());
			for (int k = 0; k < 10; k++)
				sn1 = sn1.replace((char)('0' + k), ' ');
			String sn2 = new String();
			for (int j = i - 1; j >= 0; j--)
			{
				SpecialAbility sa2 = (SpecialAbility)saList.get(j);
				sn2 = new String(sa2.getDesc());
				for (int k = 0; k < 10; k++)
					sn2 = sn2.replace((char)('0' + k), ' ');
				if (sn1.equals(sn2))
					saList.remove(sa2);
			}
		}
		for (e = saList.iterator(); e.hasNext();)
		{
			SpecialAbility sa = (SpecialAbility)e.next();
			aPC.getSpecialAbilityList().add(sa.getName() + " (" + sa.getDesc() + ")");
		}
	}

	private void addVariablesForLevel(int aLevel)
	{
		if (aLevel == 1)
			addVariablesForLevel(0);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String aString = null;
		StringTokenizer aTok = null;
		String bString = null;
		for (Iterator e = variableList.iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			aTok = new StringTokenizer(aString, "|", false);
			bString = aTok.nextToken();
			if (Integer.parseInt(bString) == aLevel)
			{
				aPC.addVariable("CLASS:" + name + "|" + aString);
			}
		}
	}

	public void setHitPointList(Integer[] newList)
	{
		hitPointList = newList;
	}

	public int hitPoints(int iConMod)
	{
		int i,total = 0;
		for (i = 0; i < level.intValue() && i < hitPointList.length; i++)
		{
			if (hitPointList[i] != null)
			{
				total += hitPointList[i].intValue() + iConMod;
			}
		}
		return total;
	}

	public Integer[] hitPointList()
	{
		return hitPointList;
	}

	public void PCG_adjustHpRolls(int increment)
	{
		if (level.intValue() == 0)
			return;
		int i;
		for (i = 0; i < level.intValue(); i++)
		{
			Integer roll;
			roll = new Integer(hitPointList[i].intValue() + increment);
			hitPointList[i] = roll;
		}
	}

	/**
	 * Rolls hp for the current level according to the rules set in options.
	 */
	public void rollHp()
	{
		int roll = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int min = 1 + aPC.getTotalBonusTo("HD", "MIN", true) + aPC.getTotalBonusTo("HD", "MIN;CLASS=" + name, true);
		int max = getHitDie() + aPC.getTotalBonusTo("HD", "MAX", true) + aPC.getTotalBonusTo("HD", "MAX;CLASS=" + name, true);
		//Shouldn't really have to be called. I think this should be handled by the level raising code.
		fixHitpointList();

		int totalLevels = aPC.totalLevels();

		if (totalLevels == 0)
		{
			hitPointList[0] = new Integer(0);
		}
		else if (totalLevels == 1 && Globals.isHpMaxAtFirstLevel())
		{
			roll = max;
		}
		else
		{
			Random roller = new Random();
			switch (Globals.getHpRollMethod())
			{
				case Globals.s_HP_STANDARD:
					roll = Math.abs(roller.nextInt(max - min + 1)) + min;
					break;
				case Globals.s_HP_AUTOMAX:
					roll = max;
					break;
				case Globals.s_HP_PERCENTAGE:
					float pct = new Integer(Globals.getHpPct()).floatValue();
					float maxFloat = new Integer(max).floatValue();
					float minFloat = new Integer(min).floatValue();
					roll = (int)((pct / 100.0) * (maxFloat - minFloat) + minFloat);
					break;
				case Globals.s_HP_RPGA:
					roll = (int)Math.floor((max + min) / 2) + 1;
				default:
			}
		}
		roll += (aPC.getTotalBonusTo("HP", "CURRENTMAXPERLEVEL", true));
		hitPointList[level.intValue() - 1] = new Integer(roll);
		aPC.setCurrentHP(aPC.hitPoints());
	}

	private void fixHitpointList()
	{
		if (hitPointList.length < level.intValue())
		{
			int i;
			Integer[] newList = new Integer[level.intValue()];
			for (i = 0; i < hitPointList.length; ++i)
			{
				newList[i] = hitPointList[i];
			}
			while (i < newList.length)
			{
				newList[i++] = new Integer(0);
			}
			setHitPointList(newList);
		}
	}

	public int baseAttackBonus(int type)
		// type=0 generic; type=1 melee; type=2 ranged; type=3 unarmed; type=4 thrown;
		// type=5 projectile
	{
		int i = 0;
		if (level.intValue() == 0)
			return 0;
		i += this.getBonusTo("TOHIT", "TOHIT", level.intValue());
		if (getAttackBonusType().equals("G"))
			return i + level.intValue();
		else if (getAttackBonusType().equals("M"))
			return i + 3 * (level.intValue()) / 4;
		else if (getAttackBonusType().equals("B"))
			return i + level.intValue() / 2;
		else if (getAttackBonusType().equals("O"))
		{
			String aString = Globals.getCurrentPC().getVariable("BAB", true, false, "CLASS:" + name, "").toString();
			int pos = Math.max(0, aString.lastIndexOf("."));
			return i +
				Integer.parseInt(aString.substring(0, pos));
		}
		return i;
	}

	public int fortitudeCheckBonus()
	{
		if (level.intValue() == 0)
			return 0;
		int i = checkBonus(fortitudeCheckType, "FORTBASE");
		return i;
	}

	public int checkBonus(String bonusType, String defString)
	{
		if (bonusType.equals("G"))
			return 2 + level.intValue() / 2;
		else if (bonusType.equals("B"))
			return level.intValue() / 3;
		else if (bonusType.equals("M"))
			return 1 + level.intValue() / 5 + (3 + level.intValue()) / 5;
		else if (bonusType.equals("O"))
		{
			String aString = Globals.getCurrentPC().getVariable(defString, true, false, "CLASS:" + name, "").toString();
			int pos = Math.max(0, aString.lastIndexOf("."));
			return Integer.parseInt(aString.substring(0, pos));
		}
		return 0;
	}

	public int reflexCheckBonus()
	{
		if (level.intValue() == 0)
			return 0;
		int i = checkBonus(reflexCheckType, "REFLEXBASE");
		return i;
	}

	public int willCheckBonus()
	{
		if (level.intValue() == 0)
			return 0;
		int i = checkBonus(willCheckType, "WILLBASE");
		return i;
	}

	public String classLevelString()
	{
		String aString;
		if (!getSubClassName().equals("None") && !getSubClassName().equals(""))
			aString = new String(getSubClassName());
		else
			aString = new String(getName());
		aString = aString.concat(" " + level.toString());
		return aString;
	}

	public Collection featList()
	{
		return featList;
	}
	// int level e.g. 1
	// featList Shield Proficieny,Armor Proficiency (light)
	public void addFeatList(int aLevel, String aFeatList)
	{
		Integer anInt = new Integer(aLevel);
		String aString = anInt.toString() + ":" + aFeatList;
		featList.add(aString);
	}

	public Collection vFeatList()
	{
		return vFeatList;
	}
	// int level e.g. 1
	// featList Shield Proficieny,Armor Proficiency (light)
	public void addVFeatList(int aLevel, String aFeatList)
	{
		Integer anInt = new Integer(aLevel);
		String aString = anInt.toString() + ":" + aFeatList;
		vFeatList.add(aString);
	}

	private String getToken(int tokenNum, String aList, String delim)
	{
		StringTokenizer aTok = new StringTokenizer(aList, delim, false);
		while (aTok.hasMoreElements() && tokenNum >= 0)
		{
			String aString = aTok.nextToken();
			if (tokenNum == 0)
				return aString;
			tokenNum--;
		}
		return null;
	}

	/**
	 * This method adds or deletes feats for a level.
	 * @param aLevel the level to affect
	 * @param addThem whether to add or remove feats
	 */
	public void changeFeatsForLevel(int aLevel, boolean addThem)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			String feats = (String)e.next();
			if (aLevel == Integer.parseInt(getToken(0, feats, ":")))
			{
				aPC.modFeatsFromList(getToken(1, feats, ":"), addThem, aLevel == 1);
			}
		}
	}

	public void subFeatsForLevel(int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			String feats = (String)e.next();
			if (aLevel == Integer.parseInt(getToken(0, feats, ":")))
			{
				aPC.modFeatsFromList(getToken(1, feats, ":"), false, aLevel == 0);
			}
		}
	}

	public Collection specialAbilityList()
	{
		return specialAbilityList;
	}

	public void addSpecialAbilityList(int aLevel, String aList)
	{
		Integer anInt = new Integer(aLevel);
		String aString = anInt.toString() + ":" + aList;
		specialAbilityList.add(aString);
	}

	public Collection subSpecialAbilityList()
	{
		return subSpecialAbilityList;
	}

	public void addSubSpecialAbilityList(int aLevel, String aList)
	{
		Integer anInt = new Integer(aLevel);
		String aString = anInt.toString() + ":" + aList;
		subSpecialAbilityList.add(aString);
	}

	public void changeSubSpecialAbilitiesForLevel(int aLevel, boolean addIt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return;
		Integer anInt = new Integer(aLevel);
		for (Iterator e = subSpecialAbilityList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			Integer thisInt = new Integer(getToken(0, aString, ":"));
			String aList = getToken(1, aString, ":");
			if (anInt.intValue() == thisInt.intValue())
			{
				StringTokenizer aStrTok = new StringTokenizer(aList, ",", false);
				while (aStrTok.hasMoreTokens())
				{
					String thisString = (String)aStrTok.nextToken();
					if (addIt)
						aPC.getSpecialAbilityList().add(thisString);
					else
						aPC.getSpecialAbilityList().remove(thisString);
				}
			}
		}
	}

	public List spellList()
	{
		return spellList;
	}

	public Spell getSpellNamed(String spellName)
	{
		for (Iterator i = spellList.iterator(); i.hasNext();)
		{
			Spell aSpell = (Spell)i.next();
			if (aSpell.getName().equals(spellName))
				return aSpell;
		}
		return null;
	}

	public int getSpellsInSpecialtyForLevel(int aLevel)
	{
		int retVal = 0;
		Spell aSpell = null;
		for (Iterator i = spellList.iterator(); i.hasNext();)
		{
			aSpell = (Spell)i.next();
			if (aSpell.isInSpecialty(specialtyList) && aSpell.levelForClass(name) == aLevel)
				retVal++;
		}
		return retVal;
	}

	public void addAddList(int aLevel, String aList)
	{
		levelAbilityList.add(LevelAbility.createAbility(this, aLevel, aList));
	}

	public void addAddsForLevel(int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return;
		LevelAbility ability = null;
		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			ability = (LevelAbility)e.next();
			if (ability.level() == aLevel)
				ability.addForLevel();
		}
	}

	public void subAddsForLevel(int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return;
		LevelAbility ability = null;
		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			ability = (LevelAbility)e.next();
			if (ability.level() == aLevel)
				ability.subForLevel();
		}
	}

	private void checkForSubClass()
	{
		if (subClassString.lastIndexOf('|') <= 0)
		{
			return;
		}

		// Tokenize the class
		StringTokenizer aTok = new StringTokenizer(subClassString, "|");

		Integer anInt = new Integer(aTok.nextToken());

		ArrayList choiceNames = new ArrayList();
		ArrayList fileNames = new ArrayList();
		ArrayList choiceNum = new ArrayList();

		boolean showAdditionalChoices = false;
		String choice = null;
		while (aTok.hasMoreTokens())
		{
			choice = aTok.nextToken();
			if (choice.startsWith("FILE="))
			{
				int i = choice.lastIndexOf('.');
				String fileName = choice.substring("FILE=".length());
				fileName = fileName.replace('\\', File.separatorChar);
				fileName = fileName.replace('/', File.separatorChar);
				fileNames.add(fileName);
				choice = choice.substring("FILE=".length(), i);
				showAdditionalChoices = true;
			}
			else
			{
				fileNames.add("NO");
			}

			choiceNames.add(choice);

			if (aTok.hasMoreTokens())
			{
				choiceNum.add(new Integer(aTok.nextToken()));
			}
			else
			{
				choiceNum.add(new Integer(1));
			}
		}

		if (showAdditionalChoices)
		{
			// Show a new chooser
			Chooser c = new Chooser();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setAvailableList(choiceNames);
			if (choiceNames.size() == 1)
				c.setSelectedList(choiceNames);
			else if (choiceNames.size() != 0)
				c.show();

			List selectedList = c.getSelectedList();
			if (!selectedList.isEmpty())
			{
				String theChoice = (String)selectedList.get(0);
				if (theChoice.equals("None"))
				{
					return;
				}

				int i = choiceNames.indexOf(theChoice);

				String fileName = (String)fileNames.get(i);
				if (!fileName.equals("NO"))
				{
					File aFile = new File((String)fileName);

					anInt = new Integer(0);

					try
					{
						// Read the file lines into a list
						List availableList = new ArrayList(10);

						BufferedReader reader = new BufferedReader(new FileReader(aFile));

						String line = reader.readLine();
						while (line != null)
						{
							availableList.add(line.trim());
							line = reader.readLine();
						}

						Chooser c1 = new Chooser();
						c1.setAvailableList(availableList);
						c1.setMessageText("Select an item.  The second column is the cost. " +
							"If this cost is non-zero, you will be asked to also " +
							"select items from this list to give up.");
						c1.setPool(((Integer)choiceNum.get(i)).intValue());
						c1.setPoolFlag(true);
						c1.show();

						setProhibitedString("");
						boolean setNone = true;
						specialtyList.clear();
						String aString = null;
						for (i = 0; i < c1.getSelectedList().size(); i++)
						{
							aString = "";
							int j = 0;
							for (j = 0; j < availableList.size(); j++)
							{
								aString = (String)availableList.get(j);
								if (aString.indexOf('\t') > 0 &&
									aString.substring(0, aString.indexOf('\t')).compareTo
									(c1.getSelectedList().get(i).toString().substring(0, aString.indexOf('\t'))) == 0)
									break;
							}
							StringTokenizer tabTok = new StringTokenizer(aString, "\t", false);
							j = 0;
							setNone = false;
							Integer cost = new Integer(0);
							String bString = null;
							while (tabTok.hasMoreTokens())
							{
								bString = tabTok.nextToken();
								switch (j++)
								{
									case 0:
										specialtyList.add(bString);
										break;
									case 1:
										cost = new Integer(bString);
										break;
									case 2:
										setSubClassName(bString);
										break;
									default:
										if (bString.startsWith("SPELLBASESTAT="))
										{
											setSpellBaseStat(bString.substring(14));
											if (Globals.isDebugMode())
												System.out.println("Base Spell Stat for " + name + " =" + spellBaseStat + ".");
										}
										else if (bString.startsWith("KNOWNSPELLSFROMSPECIALTY="))
										{
											knownSpellsFromSpecialty = Integer.parseInt(bString.substring(25));
											if (Globals.isDebugMode())
												System.out.println(name + " has " + knownSpellsFromSpecialty + " specialtySpells per level");
										}
										else
										{
											if (prohibitedString.length() > 0)
												prohibitedString = prohibitedString.concat(",");
											prohibitedString = prohibitedString + bString;
										}
										cost = new Integer(0);
										break;
								}
							}
							if (cost.intValue() > 0)
							{
								// choose prohibiteds
								// Remove the selected option from the available options
								availableList.remove(aString);
								// Reset the available list
								c1.setAvailableList(availableList);
								// Clear the selected items
								c1.clearSelectedList();
								c1.setPool(cost.intValue());
								c1.setCostColumnNumber(1);
								c1.setMessageText("Now make your prohibited selections. The " +
									"cost must equal the cost of your previous selection.");
								c1.show();

								i = -1;
								if (c1.getSelectedList().size() > 0)
								{
									String cString = null;
									for (i = 0; i < c1.getSelectedList().size(); i++)
									{
										aString = (String)c1.getSelectedList().get(i).toString();
										tabTok = new StringTokenizer(aString, "\t", false);
										cString = tabTok.nextToken();
										if (prohibitedString.length() > 0)
											prohibitedString = prohibitedString.concat(",");
										if (tabTok.countTokens() > 2)
										{
											cString = tabTok.nextToken();
											cString = tabTok.nextToken();
										}
										prohibitedString = prohibitedString.concat(cString.trim());
									}
								}
								else
									setSubClassName("None");
							}
							if (setNone)
								setProhibitedString("None");
						}
					}
					catch (Exception exception)
					{
						JOptionPane.showMessageDialog(null, (String)fileNames.get(i) + " error" + exception.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
				{
					setSubClassName("");
				}
			}
		}
	}

	public Integer skillPool()
	{
		return skillPool;
	}

	public void setSkillPool(int i)
	{
		skillPool = new Integer(i);
	}

	public String goldString()
	{
		return goldString;
	}

	private void setGoldString(String aString)
	{
		goldString = aString;
	}

	public Integer rollGold()
	{
		Random roller = new Random();
		Integer dice = new Integer(4);
		Integer sides = new Integer(4);
		Integer mult = new Integer(10);
		StringTokenizer aTok = new StringTokenizer(goldString, ",", false);
		if (aTok.hasMoreTokens())
			dice = new Integer(aTok.nextToken());
		if (aTok.hasMoreTokens())
			sides = new Integer(aTok.nextToken());
		if (aTok.hasMoreTokens())
			mult = new Integer(aTok.nextToken());
		int total = 0;
		for (int roll = 0; roll < dice.intValue(); roll++)
		{
			int i = roller.nextInt(sides.intValue());
			if (i < 0) i = -i;
			total += i + 1;
		}
		total *= mult.intValue();
		return new Integer(total);
	}

	public String specialsString()
	{
		return specialsString;
	}

	private void setSpecialsString(String aString)
	{
		specialsString = aString;
	}

	public Collection skillList()
	{
		return skillList;
	}

	public String defenseString()
	{
		return defenseString;
	}

	private void setDefenseString(String aString)
	{
		defenseString = aString;
	}

	public Integer defense(int y)
	{
		String aString = defenseString;
		int i = 0;
		if (aString.length() > 0 && aString.indexOf(',') > -1)
		{
			int k = Integer.parseInt(aString.substring(0, aString.indexOf(',')));
			int m = Integer.parseInt(aString.substring(aString.lastIndexOf(',') + 1));
			if (y > 0)
				i += m;
			switch (k)
			{
/*Best*/  case 0:
					i += 4 + getLevel().intValue() / 2;
					break;
/*Middle*/  case 1:
/*Prestige*/case 4:
/*Prestige2*/case 5:
					i += 3 + getLevel().intValue() / 5;
					if (i >= 2)
						i += (getLevel().intValue() + 3) / 5;
					if (k == 4)
						i -= 2;
					if (k == 5)
						i -= 1;
					break;
/*Low*/case 2:
					i += 2 + getLevel().intValue() / 3;
					break;
/*NPC*/case 3:
					i += getLevel().intValue() / 3;
					break;
/*Prestige5*/	case 8:
					i += 2 + (((getLevel().intValue() + 1) + 3) / 3);
					break;
/*Prestige6*/	case 9:
					i += 2 + ((getLevel().intValue() + 3) / 3);
					if (getLevel().intValue() == 8)
						i += 1;
					break;
/*Prestige7*/ 	case 10:
					i += 1 + ((getLevel().intValue() + 1) / 2);
					break;
			}
		}
		i += getBonusTo("CLASS", "DEFENSE", level.intValue());
		return new Integer(i);
	}

	public String reputationString()
	{
		return reputationString;
	}

	private void setReputationString(String aString)
	{
		reputationString = aString;
	}

	public void addKnownSpellsList(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			knownSpellsList.add(aTok.nextToken());
	}

	public boolean isAutoKnownSpell(String spellName, int spellLevel)
	{
		for (Iterator e = knownSpellsList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.startsWith("LEVEL=") && Integer.parseInt(aString.substring(6)) == spellLevel)
				return true;
			if (aString.equals(spellName))
				return true;
		}
		return false;
	}

	private void setAttackCycle(String aString)
	{
		attackCycle = aString;
	}

	public int attackCycle(int index)
	{
		StringTokenizer aTok = new StringTokenizer(attackCycle, "|", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if ((index == 0 && aString.equals("BAB")) || (index == 2 && aString.equals("UAB")))
				return Integer.parseInt(aTok.nextToken());
		}
		return 5;
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		final int colMax = colToken.countTokens();
		int col = 0;
		int aInt = 0;
		if (colMax == 0)
			return;
		int option = 15;
		if (inputLine.startsWith("CLASS:"))
		{
			option = 0;
			for (col = 0; col < 20; col++)
				castList.add("0");
		}
		for (col = 0; col < colMax; col++)
		{
			final String colString = new String(colToken.nextToken());
			if (option < 15)
				option = col;
			if (col == 0 && option == 15)
			{
				aInt = Integer.parseInt(colString);
				continue;
			}
			if (super.parseTagLevel(colString, aInt))
				continue;
			switch (option)
			{
				case 0:
					setName(colString.substring(6));
					break;
				case 1:
					setAlignments(colString);
					break;
				case 2:
					setHitDie(Integer.parseInt(colString));
					break;
				case 3:
					setSkillPoints(Integer.parseInt(colString));
					break;
				case 4:
					setInitialFeats(Integer.parseInt(colString));
					break;
				case 5:
					setSpellBaseStat(colString);
					break;
				case 6:
					setSpellType(colString);
					break;
				case 7:
					setAttackBonusType(colString);
					break;
				case 8:
					setFortitudeCheckType(colString);
					break;
				case 9:
					setReflexCheckType(colString);
					break;
				case 10:
					setWillCheckType(colString);
					break;
				default:
					if (colString.startsWith("ABB:"))
						abbrev = colString.substring(4);
					else if (colString.substring(0, 2).equals("AC"))
						getAcList().add(colString.substring(3));
					else if (colString.startsWith("ADD:"))
						addAddList(aInt, colString.substring(4));
					else if (colString.startsWith("AGESET:"))
						ageSet=Integer.parseInt(colString.substring(7));
					else if (colString.startsWith("ATTACKCYCLE:"))
						setAttackCycle(colString.substring(12));
					else if (colString.startsWith("CAST:"))
						getCastList().set(aInt - 1, colString.substring(5));
					else if (colString.startsWith("CASTAS:"))
						setCastAs(colString.substring(7));
					else if (colString.startsWith("DEFINE:"))
						variableList.add(aInt + "|" + colString.substring(7));
					else if (colString.startsWith("DEF:"))
						setDefenseString(colString.substring(4));
					else if (colString.startsWith("DEITY:"))
						deityString = colString.substring(6);
					else if (colString.startsWith("DOMAIN:"))
						domainList.add(aInt+"|"+colString.substring(7));
					else if (colString.startsWith("INTMODTOSKILLS:"))
						intModToSkills = !colString.substring(15).equals("No");
					else if (colString.startsWith("FEAT:"))
						addFeatList(aInt, colString.substring(5));
					else if (colString.startsWith("FEATAUTO:"))
						setFeatAutos(aInt, colString.substring(9));
					else if (colString.startsWith("GOLD:"))
						setGoldString(colString.substring(5));
					else if (colString.startsWith("KEY:"))
						setKeyName(colString.substring(4));
					else if (colString.startsWith("KNOWN:"))
						getKnownList().add(colString.substring(6));
					else if (colString.startsWith("KNOWNSPELLS:"))
						addKnownSpellsList(colString.substring(12));
					else if (colString.startsWith("KNOWNSPELLSFROMSPECIALTY:"))
						knownSpellsFromSpecialty = Integer.parseInt(colString.substring(25));
					else if (colString.startsWith("LANGAUTO"))
						setLanguageAutos(colString.substring(9));
					else if (colString.startsWith("LANGBONUS"))
						setLanguageBonus(colString.substring(10));
					else if (colString.startsWith("LEVELSPERFEAT:"))
						setLevelsPerFeat(Integer.parseInt(colString.substring(14)));
					else if (colString.startsWith("MAXLEVEL:"))
						maxLevel = Integer.parseInt(colString.substring(9));
					else if (colString.startsWith("MEMORIZE:"))
						memorizeSpells = colString.endsWith("Y");
					else if (colString.startsWith("MULTIPREREQS"))
						multiPreReqs = true;
					else if (colString.startsWith("PRERACETYPE:"))
						preRaceType = colString.substring(12);
					else if (colString.startsWith("PRE") || colString.startsWith("RESTRICT:"))
						preReqArrayList.add(colString);
					else if (colString.startsWith("PROHIBITED:"))
						setProhibitedString(colString.substring(11));
					else if (colString.startsWith("REP:"))
						setReputationString(colString.substring(4));
					else if (colString.startsWith("SA") || colString.startsWith("SUBSA"))
					{
						final boolean flag = colString.startsWith("SA");
						int index = 3;
						if (flag == false) index = 6;
						final StringTokenizer aTok = new StringTokenizer(colString.substring(index), ",", false);
						while (aTok.hasMoreTokens())
						{
							final SpecialAbility sa = new SpecialAbility();
							sa.parseLine(aTok.nextToken(), sourceFile, lineNum);
							if (flag)
							{
								addSpecialAbilityList(aInt, sa.getName());
							}
							else
							{
								addSubSpecialAbilityList(aInt, sa.getName());
							}
						}
					}
					else if (colString.startsWith("SPECIALS:"))
						setSpecialsString(colString.substring(9));
					else if (colString.startsWith("SUBCLASS:"))
						setSubClassString(aInt + "|" + colString.substring(9));
					else if (colString.startsWith("TYPE:"))
						setType(colString.substring(5));
					else if (colString.startsWith("UATT:"))
						getUattList().add(colString.substring(5));
					else if (colString.startsWith("UDAM:"))
						getUdamList().add(colString.substring(5));
					else if (colString.startsWith("UMULT:"))
						umult.add(aInt + "|" + colString.substring(6));
					else if (colString.startsWith("VFEAT:"))
						addVFeatList(aInt, colString.substring(6));
					else if (colString.startsWith("VISIBLE:"))
						visible = colString.substring(8).startsWith("Y");
					else if (colString.startsWith("WEAPONAUTO"))
						setWeaponProfAutos(colString.substring(11));
					else if (colString.startsWith("WEAPONBONUS"))
						setWeaponProfBonus(colString.substring(12));
					else
					{
						JOptionPane.showMessageDialog
							(null, "Illegal class info " +
							sourceFile.getName() + ":" + Integer.toString(lineNum) +
							" \"" + colString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
					}
					break;
			}
		}
	}

	public boolean isQualified()
	{
		if (Globals.getCurrentPC() == null)
			return false;
		if (classType != null && classType.equals("Monster") && preRaceType != null && !preRaceType.equals(Globals.getCurrentPC().getRace().getType()))
			return false;
		if (!allowsAlignment(Globals.getCurrentPC().getAlignment()) || !canBePrestige())
			return false;
		return true;
	}

	public boolean isPrestige()
	{
		if (preReqArrayList.size() == 0)
			return false;
		return true;
	}

	public boolean isPC()
	{
		if (classType == null)
			return true;
		return false;
	}

	public boolean isNPC()
	{
		if (classType != null && classType.equals("NPC"))
			return true;
		return false;
	}

	public boolean isMonster()
	{
		if (classType != null && classType.equals("Monster"))
			return true;
		return false;
	}

	/**
	 * Increases or decreases the initiative modifier by the given value.
	 */

	public void addInitMod(int initModDelta)
	{
		initMod = initMod + initModDelta;
	}

	/**
	 * Returns the initiative modifier.
	 */

	public int initMod()
	{
		return initMod;
	}

}
