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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.gui.Chooser;
import pcgen.util.GuiFacade;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PCClass extends PObject
{
	//	private String alignments = new String();
	private String subClassName = "None";
	private String subClassString = "None";
	private String prohibitedString = "None";
	private int hitDie = 0;
	private int skillPoints = 0;
	private int initialFeats = 0;
	private String spellBaseStat = "NONE";
	private String spellType = "NONE";
	private String attackBonusType = "O";
	private String fortitudeCheckType = "O";
	private String reflexCheckType = "O";
	private String willCheckType = "O";
	private ArrayList knownList = new ArrayList();
	private ArrayList specialtyknownList = new ArrayList();
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
	private ArrayList featList = new ArrayList();
	private ArrayList vFeatList = new ArrayList();
	private ArrayList domainList = new ArrayList();
	private ArrayList levelAbilityList = new ArrayList();
	private ArrayList specialAbilityList = new ArrayList();
	private ArrayList subSpecialAbilityList = new ArrayList();
	private ArrayList umult = new ArrayList();
	private Integer skillPool = new Integer(0);
	private String goldString = new String();
	private String specialsString = new String();
	private ArrayList skillList = new ArrayList();
	private String defenseString = "1,1";
	private String reputationString = "0";
	private String exClass = new String();
	private String levelExchange = "";
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
	private int numSpellsFromSpecialty = 0;
	private String preRaceType = null;  //since I don't want this to be counted as making it a prestige class.
	private boolean intModToSkills = true;
	private int levelsPerFeat = 3;
	private int ageSet = 2;
	private double itemCreationLevelMultiplier = 1.0;
	private ArrayList templates = new ArrayList();
	private ArrayList templatesAdded = null;
	private ArrayList addDomains = new ArrayList();

	public Object clone()
	{
		PCClass aClass = (PCClass)super.clone();
		aClass.setSubClassName(getSubClassName());
		aClass.setSubClassString(getSubClassString());
		aClass.setProhibitedString(getProhibitedString());
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
		aClass.specialtyknownList = (ArrayList)specialtyknownList.clone();
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
		aClass.featList = (ArrayList)featList.clone();
		aClass.vFeatList = (ArrayList)vFeatList.clone();
		aClass.skillList = new ArrayList();

		aClass.levelAbilityList = new ArrayList();
		if (!levelAbilityList.isEmpty())
		{
			for (Iterator it = levelAbilityList.iterator(); it.hasNext();)
			{
				LevelAbility ab = (LevelAbility)it.next();
				ab = (LevelAbility)ab.clone();
				ab.setOwner(aClass);
				aClass.levelAbilityList.add(ab);
			}
		}

		aClass.specialAbilityList = (ArrayList)specialAbilityList.clone();
		aClass.subSpecialAbilityList = (ArrayList)subSpecialAbilityList.clone();
		aClass.setGoldString(goldString);
		aClass.setSpecialsString(specialsString);
		aClass.setDefenseString(defenseString);
		aClass.setReputationString(reputationString);
		aClass.setExClass(exClass);
		aClass.setLevelExchange(levelExchange);

		aClass.abbrev = abbrev;
		aClass.memorizeSpells = memorizeSpells;
		aClass.multiPreReqs = multiPreReqs;
		aClass.isSpecified = isSpecified;
		aClass.deityString = deityString;
		aClass.maxLevel = maxLevel;
		aClass.knownSpellsList = (ArrayList)knownSpellsList.clone();
		aClass.attackCycle = attackCycle;
		aClass.castAs = castAs;
		aClass.preRaceType = preRaceType;
		aClass.intModToSkills = intModToSkills;
		aClass.levelsPerFeat = levelsPerFeat;
		aClass.initMod = initMod;
		aClass.specialtyList = (ArrayList)specialtyList.clone();
		aClass.ageSet = ageSet;
		aClass.domainList = (ArrayList)domainList.clone();
		aClass.addDomains = (ArrayList)addDomains.clone();
		return aClass;
	}

	public List getTemplates()
	{
		return templates;
	}

	public void addTemplate(String template)
	{
		templates.add(template);
	}

	public List getUmult()
	{
		return umult;
	}

	public void addUmult(String umult)
	{
		this.umult.add(umult);
	}

	public String makeBonusString(String bonusString, String chooseString)
	{
		return "0|" + super.makeBonusString(bonusString, chooseString);
	}


	public ArrayList getTemplates(boolean flag)
	{
		ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		for (int x = 0; x < templates.size(); x++)
		{
			final String template = (String)templates.get(x);
			final StringTokenizer aTok = new StringTokenizer(template, "|", false);
			if (level.intValue() < Integer.parseInt(aTok.nextToken()))
				continue;
			final String tString = aTok.nextToken();
			if (tString.startsWith("CHOOSE:") && !flag)
			{
				newTemplates.add(PCTemplate.chooseTemplate(tString.substring(7)));
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
			}
			else if (!flag)
			{
				newTemplates.add(tString);
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
			}
		}
		return newTemplates;
	}

	public ArrayList templatesAdded()
	{
		if (templatesAdded == null)
			return new ArrayList();
		return templatesAdded;
	}

	public boolean getMemorizeSpells()
	{
		return memorizeSpells;
	}

	public void setMemorizeSpells(boolean memorizeSpells)
	{
		this.memorizeSpells = memorizeSpells;
	}

	public String getDeityString()
	{
		return deityString;
	}

	public void setDeityString(String deityString)
	{
		this.deityString = deityString;
	}

	public Collection getSpecialtyList()
	{
		return specialtyList;
	}

	public String getSpecialtyListString()
	{
		StringBuffer retString = new StringBuffer();
		if (!specialtyList.isEmpty())
		{
			for (Iterator i = specialtyList.iterator(); i.hasNext();)
			{
				if (retString.length() > 0)
				{
					retString.append(",");
				}
				retString.append((String)i.next());
			}
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (!aPC.getCharacterDomainList().isEmpty())
		{
			for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain)i.next();
				if (aCD.getDomain() != null)
				{
					if (retString.length() > 0)
					{
						retString.append(",");
					}
					retString.append(aCD.getDomain().getName());
				}
			}
		}
		return retString.toString();
	}

	public String getAbbrev()
	{
		return abbrev;
	}

	public void setAbbrev(String abbrev)
	{
		this.abbrev = abbrev;
	}

	public Integer getHitPointList(int aLevel)
	{
		return hitPointList[aLevel];
	}

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		fixHitpointList();
		hitPointList[aLevel] = iRoll;
	}

	public Integer getSkillPool()
	{
		return skillPool;
	}

	public void setSkillPool(Integer skillPool)
	{
		this.skillPool = skillPool;
	}

	public double getItemCreationLevelMultiplier()
	{
		return itemCreationLevelMultiplier;
	}

	public void setItemCreationLevelMultiplier(double itemCreationLevelMultiplier)
	{
		this.itemCreationLevelMultiplier = itemCreationLevelMultiplier;
	}

	public String getPreRaceType()
	{
		return preRaceType;
	}

	public void setPreRaceType(String preRaceType)
	{
		this.preRaceType = preRaceType;
	}

	public int getAgeSet()
	{
		return ageSet;
	}

	public void setAgeSet(int ageSet)
	{
		this.ageSet = ageSet;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
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

	public void setMultiPreReqs(boolean multiPreReqs)
	{
		this.multiPreReqs = multiPreReqs;
	}

	public ArrayList getDomainList()
	{
		return domainList;
	}

	public void addDomainList(String domainItem)
	{
		domainList.add(domainItem);
	}

	/* addDomains is the prestige domains this class has access to */
	public ArrayList getAddDomains()
	{
		return addDomains;
	}

	public void setAddDomains(int level, String aString, String delimiter)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, delimiter, false);
		while (aTok.hasMoreTokens())
			addDomains.add(level + "|" + aTok.nextToken());
	}

	public String qualifiedNameString()
	{
		String aString = null;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null && (!allowsAlignment(aPC.getAlignment()) || !canBePrestige()))
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

	public String getSpellCastingName()
	{
		if (castAs.length() == 0)
			return name;
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

	public void setSubClassString(String aString)
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
		if (aList.isEmpty())
			return false;
		for (Iterator e = aList.iterator(); e.hasNext();)
			if (prohibitedStringContains((String)e.next()))
				return true;
		return false;
	}

	public int getBonusTo(String type, String mname, int asLevel)
	{
//		Globals.debugPrint("Getting bonus of type: " + type + " for name: " + mname + ", Class " + getDisplayClassName() + " as level " + asLevel);

		int i = 0;
		if (getBonusList().isEmpty())
			return 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();

		type = type.toUpperCase();
		mname = mname.toUpperCase();
		final String typePlusMName = new StringBuffer(type).append(".").append(mname).append(".").toString();
		for (Iterator e = bonusList.iterator(); e.hasNext();)
		{
			StringTokenizer breakOnPipes = new StringTokenizer(((String)e.next()).toUpperCase(), "|", false);
			int aLevel = Integer.parseInt(breakOnPipes.nextToken());
			String theType = breakOnPipes.nextToken();
			if (!theType.equals(type))
				continue;
			StringTokenizer breakOnCommas = new StringTokenizer(breakOnPipes.nextToken(), ",", false);
			while (breakOnCommas.hasMoreTokens())
			{
				String theName = breakOnCommas.nextToken();
				if (aLevel <= asLevel && theName.equals(mname))
				{
					String aString = breakOnPipes.nextToken();
					ArrayList preReqList = new ArrayList();
					String bonusType = null;
					while (breakOnPipes.hasMoreTokens())
					{
						final String bString = breakOnPipes.nextToken();
						if (bString.startsWith("PRE") || bString.startsWith("!PRE"))
							preReqList.add(bString);
						else if (bString.startsWith("TYPE="))
							bonusType = bString.substring(5);
					}
					// must meet criteria for bonuses before adding them in
					if (passesPreReqTestsForList(preReqList))
					{
						final int j = aPC.getVariableValue(aString, "").intValue();
						i += j;
						aPC.setBonusStackFor(j, typePlusMName + bonusType);
					}
				}
			}
		}
		return i;
	}

	// returns an array of ints... slots 0-5 are related to the stats and do not stack,
	// slot 6 is for stacking bonuses which accumulate.
	public int[] bonusBasedOnStat(String type, String mname, int asLevel)
	{
		int[] retInt = new int[7];
		int i = 0;
		for (i = 0; i < 7; i++)
			retInt[i] = 0;
		if (getBonusList().isEmpty())
			return retInt;
		String aString = null;
		StringTokenizer aTok = null;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (Iterator e = bonusList.iterator(); e.hasNext();)
		{
			aString = (String)e.next();
			aTok = new StringTokenizer(aString, "|", false);
			int anInt = Integer.parseInt(aTok.nextToken());
			if (anInt <= asLevel && aTok.nextToken().equals(type) &&
				aTok.nextToken().lastIndexOf(mname) > -1)
			{ // we meet the level of bonus, and match on type & name
				aString = aTok.nextToken();
				i = -1;
				// if we're at least 3 characters long see if we have a stat abbreviation in our midst
				if (aString.length() > 2)
					i = (Globals.s_STATNAMES.lastIndexOf(aString.substring(0, 3)) + 3) / 3;
				// if we involve a stat, put that bonus in that slot of the int array, since stat bonuses do not stack
				if (i != -1)
				{
					retInt[i] = aPC.getVariableValue(aString, "").intValue(); // do not stack
				}
				// otherwise dump value in last slot, which can stack
				else
				{
					retInt[6] += aPC.getVariableValue(aString, "").intValue(); // stacks
				}
			}
		}
		return retInt;
	}

	private static final String allAlignments = "012345678";

	public void setAlignments(String newAlign)
	{
		String bString = "";
		if (!newAlign.startsWith(allAlignments))
		{
			bString = Utility.commaDelimit(newAlign);
			int iOffs = bString.indexOf("D");
			if (iOffs >= 0)
			{
				bString = bString.substring(0, iOffs) + "10" + bString.substring(iOffs + 1);
			}
		}

		if (!bString.equals(""))
		{
			addPreReq("PREALIGN:" + bString);
		}
	}

	public boolean allowsAlignment(int index)
	{
		return true;
	}

	public boolean canBePrestige()
	{
		return passesPreReqTests();
	}

	public boolean passesPreReqTestsForList(PlayerCharacter aPC, PObject aObj, ArrayList anArrayList)
	{
		return (Globals.isBoolBypassClassPreReqs() || super.passesPreReqTestsForList(aPC, aObj, anArrayList));
	}

	public int getMaxLevel()
	{
		return maxLevel;
	}

	public void setMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
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

		if (dieLock.startsWith("%/"))
		{
			diedivide = Integer.parseInt(dieLock.substring(2));
			if (diedivide <= 0) diedivide = 1; // Idiot proof it. Stop Divide by zero errors.
			diedivide = hitDie / diedivide;
		}
		else if (dieLock.startsWith("%*"))
		{
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide *= hitDie;
		}
		else if (dieLock.startsWith("%+"))
		{ // possibly redundant with BONUS:HD MAX|num
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide += hitDie;
		}
		else if (dieLock.startsWith("%-"))
		{ // possibly redundant with BONUS:HD MAX|num if that will take negative numbers.
			diedivide = Integer.parseInt(dieLock.substring(2));
			diedivide = hitDie - diedivide;
		}
		else if (dieLock.startsWith("%up"))
		{
			diedivide = Integer.parseInt(dieLock.substring(3));
			// lock in valid values.
			if (diedivide > 4) diedivide = 4;
			if (diedivide < 0) diedivide = 0;
			for (int i = 3; i <= (7 - diedivide); i++)
			{
				if (hitDie == dieSizes[i])
				{
					return dieSizes[i + diedivide];
				}
			}
			diedivide = dieSizes[7]; // If they went too high, they get maxed out.
		}
		else if (dieLock.startsWith("%Hup"))
		{
			diedivide = Integer.parseInt(dieLock.substring(4));
			for (int i = 0; i < ((dieSizes.length) - diedivide); i++)
			{
				if (hitDie == dieSizes[i])
				{
					return dieSizes[i + diedivide];
				}
			}
			diedivide = dieSizes[dieSizes.length]; // If they went too high, they get maxed out.
		}
		else if (dieLock.startsWith("%down"))
		{
			diedivide = Integer.parseInt(dieLock.substring(5));
			// lock in valid values.
			if (diedivide > 4) diedivide = 4;
			if (diedivide < 0) diedivide = 0;
			for (int i = (3 + diedivide); i <= 7; i++)
			{
				if (hitDie == dieSizes[i])
				{
					return dieSizes[i - diedivide];
				}
			}
			diedivide = dieSizes[3]; // Minimum valid if too low.
		}
		else if (dieLock.startsWith("%Hdown"))
		{
			diedivide = Integer.parseInt(dieLock.substring(5));
			for (int i = diedivide; i < dieSizes.length; i++)
			{
				if (hitDie == dieSizes[i])
				{
					return dieSizes[i - diedivide];
				}
			}
			diedivide = dieSizes[0]; // floor them if they're too low.
		}
		else
		{
			diedivide = Integer.parseInt(dieLock);
		}
		if (diedivide <= 0) diedivide = 1; // Idiot proof it.
		return diedivide;
	}

	public void setHitDie(int dice)
	{
		hitDie = dice;
	}

	public int getSkillPoints()
	{
		return skillPoints;
	}

	public void setSkillPoints(int points)
	{
		skillPoints = points;
	}

	public int getInitialFeats()
	{
		return initialFeats;
	}

	public void setInitialFeats(int feats)
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

	public void setSpellType(String newType)
	{
		spellType = newType;
	}

	public String getAttackBonusType()
	{
		return attackBonusType;
	}

	public void setAttackBonusType(String aString)
	{
		attackBonusType = aString;
	}

	public String getFortitudeCheckType()
	{
		return fortitudeCheckType;
	}

	public void setFortitudeCheckType(String aString)
	{
		fortitudeCheckType = aString;
	}

	public String getReflexCheckType()
	{
		return reflexCheckType;
	}

	public void setReflexCheckType(String aString)
	{
		reflexCheckType = aString;
	}

	public String getWillCheckType()
	{
		return willCheckType;
	}

	public void setWillCheckType(String aString)
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

	public void addKnown(int iLevel, String aString)
	{
		for (; knownList.size() < iLevel - 1;)
		{
			knownList.add("");
		}
		knownList.add(aString);
	}

	public Collection getKnownList()
	{
		return knownList;
	}

	public Collection getSpecialtyKnownList()
	{
		return specialtyknownList;
	}

	public int baseSpellIndex()
	{
		String spellBaseStat = getSpellBaseStat();
		return "SPELL".equals(spellBaseStat)
			? -2 // means base spell stat is based upon spell itself
			: Globals.getStatFromAbbrev(spellBaseStat);
	}

	/**
	 * Return number of spells known for a level.
	 */
	public int getKnownForLevel(int pcLevel, int spellLevel)
	{
		return getKnownForLevel(pcLevel, spellLevel, "null");
	}

	/**
	 * Return number of speciality spells known for a level.
	 */
	public int getSpecialtyKnownForLevel(int pcLevel, int spellLevel)
	{
		return getSpecialtyKnownForLevel(pcLevel, spellLevel, "null");
	}

	/**
	 * Return number of spells known for a level for a given spellbook.
	 */
	public int getKnownForLevel(int pcLevel, int spellLevel, String bookName)
	{
		int total = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = aPC.getTotalBonusTo("SPELLKNOWN", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
			aPC.getTotalBonusTo("SPELLKNOWN", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		Globals.debugPrint("Beginning Total:" + total);
		pcLevel += aPC.getTotalBonusTo("PCLEVEL", name, false);
		pcLevel += aPC.getTotalBonusTo("PCLEVEL", "TYPE=" + getSpellType(), false);

		int index = baseSpellIndex();
		if (aPC.adjStats(index) < 10 + spellLevel && index != -2)
			return total;

		if (Globals.isSSd20Mode())
		{

			if (bookName.equals("Intimate Knowledge Spells"))
			{
				total += 20 + ((pcLevel) * 5) + ((aPC.calcStatMod(index)) * 10);
			}
			else
			{
				total += 40 + ((pcLevel) * 10) + ((aPC.calcStatMod(index)) * 10);
			}
			return total;
		}

		boolean psiSpecialty = false;
		if (!knownList.isEmpty())
		{
			for (Iterator e = knownList.iterator(); e.hasNext();)
			{
				final String aString = (String)e.next();
				//
				// If we've run out of entries in known table, then use the last one
				//
				if ((pcLevel > 1) && !e.hasNext())
				{
					pcLevel = 1;
				}

				if (pcLevel == 1)
				{
					final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
					int x = spellLevel;
					while (aTok.hasMoreTokens())
					{
						String spells = aTok.nextToken();
						if (spells.endsWith("+d"))
						{
							psiSpecialty = true;
							if (spells.length() > 1)
							{
								spells = spells.substring(0, spells.length() - 2);
							}
						}
						if (x == 0)
						{
							final int t = Integer.parseInt(spells);
							total += t;
							break;
						}
						x--;
					}
				}
				pcLevel--;
				if (pcLevel < 1)
				{
					if (psiSpecialty)
					{
						total += numSpellsFromSpecialty;
					}
					break;
				}
			}
		}
		Globals.debugPrint("Base spells Known:" + total);
		Globals.debugPrint("KnownSpecialty:" + numSpellsFromSpecialty);
		// if we have known spells (0==no known spells recorded) or a psi specialty.
		if ((total > 0 && spellLevel > 0) && !psiSpecialty)
		{
			// make sure any slots due from specialties (including domains) are added
			total += numSpellsFromSpecialty;
		}
		Globals.debugPrint("Total Spells Known:" + total);
		return total;
	}

	/**
	 * Return number of speciality spells known for a level for a given spellbook.
	 */
	public int getSpecialtyKnownForLevel(int pcLevel, int spellLevel, String bookName)
	{
		int total = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true)
			+ aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		Globals.debugPrint("Beginning Total:" + total);

		pcLevel += aPC.getTotalBonusTo("PCLEVEL", name, false);
		pcLevel += aPC.getTotalBonusTo("PCLEVEL", "TYPE=" + getSpellType(), false);

		int index = baseSpellIndex();
		if (aPC.adjStats(index) < 10 + spellLevel && index != -2)
			return total;

		String aString = null;
		StringTokenizer aTok = null;
		int x = spellLevel;
		if (!specialtyknownList.isEmpty())
		{
			for (Iterator e = specialtyknownList.iterator(); e.hasNext();)
			{
				aString = (String)e.next();
				if (pcLevel == 1)
				{
					aTok = new StringTokenizer(aString, ",", false);
					while (aTok.hasMoreTokens())
					{
						String spells = (String)aTok.nextElement();
						final int t = Integer.parseInt(spells);
						if (x == 0)
						{
							total += t;
							break;
						}
						x--;
					}
				}
				pcLevel--;
				if (pcLevel < 1)
				{
					break;
				}
			}
		}
		Globals.debugPrint("Base specialty spells Known:" + total);
		// Globals.debugPrint("Known Specialty:" + numSpellsFromSpecialty);
		// if we have known spells (0==no known spells recorded) or a psi specialty.
		if (total > 0 && spellLevel > 0)
		{
			// make sure any slots due from specialties (including domains) are added
			total += numSpellsFromSpecialty;
		}
		Globals.debugPrint("Total Specialty Spells Known:" + total);
		return total;
	}

	public List getCastList()
	{
		return castList;
	}

	public void addCastList(String cast)
	{
		castList.add(cast);
	}

	public int getNumSpellsFromSpecialty()
	{
		return numSpellsFromSpecialty;
	}

	public void setNumSpellsFromSpecialty(int anInt)
	{
		numSpellsFromSpecialty = anInt;
	}

	public String getBonusCastForLevelString(int pcLevel, int spellLevel, String bookName)
	{
		if (getCastForLevel(pcLevel, spellLevel, bookName) > 0)
		{
			// if this class has a specialty, return +1
			if (specialtyList.size() > 0)
				return "+1";
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC.getCharacterDomainList().isEmpty())
				return "";
			// if the spelllevel is >0 and this class has a characterdomain associated with it, return +1
			if (spellLevel > 0 && spellType.equalsIgnoreCase("DIVINE"))
			{
				for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
				{
					CharacterDomain aCD = (CharacterDomain)i.next();
					if (aCD.getDomainSource().startsWith("PCClass|" + getName()))
						return "+1";
				}
			}
		}
		return "";
	}
	// removed getCastForLevelString because it was a godforsaken mostly-duplicate of
	// getCastForLevel() except for returning a +1 at the end if the specialistList is
	// non-empty, which is much better done with getBonusCastForLevelString and doesn't
	// duplicate code, and hence, doesn't duplicate work when features/bugs cause
	// code changes.  merton_monk 10/17/01

	/**********************************/
	//added by Mario Bonassin 10-14-01
//Spellpoints -
// Seems to be functioning fine
// Not to sure what I can get rid of and still have it work.
	public String getSPForLevelString(int pcLevel, int spellLevel, String bookName)
	{
		int total = 0;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		//total = aPC.getTotalBonusTo("SPELLCAST", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) +
		//aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		stat = aPC.adjStats(baseSpellIndex());

		int temp2 = aPC.calcStatMod(baseSpellIndex());
		int temp3 = (temp2 * pcLevel);
		total = stat + temp3;
		String bString = String.valueOf(total);
		return bString;
	}
	//End of Spellpoints
	/********************************/
	public int getCastForLevel(int pcLevel, int spellLevel)
	{
		return getCastForLevel(pcLevel, spellLevel, Globals.getDefaultSpellBook(), true);
	}

	public int getCastForLevel(int pcLevel, int spellLevel, String bookName)
	{
		return getCastForLevel(pcLevel, spellLevel, bookName, true);
	}

	public int getCastForLevel(int pcLevel, int spellLevel, String bookName, boolean includeAdj)
	{
		int total = 0;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final String classKeyName = "CLASS=" + getKeyName();
		final String levelSpellLevel = ";LEVEL=" + spellLevel;

		total = aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel, true) +
			aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + getSpellType() + levelSpellLevel, true) +
			aPC.getTotalBonusTo("SPELLCAST", "CLASS=Any" + levelSpellLevel, true);

		pcLevel += aPC.getTotalBonusTo("PCLEVEL", name, false);
		pcLevel += aPC.getTotalBonusTo("PCLEVEL", "TYPE=" + getSpellType(), false);

		stat = aPC.adjStats(baseSpellIndex());
		String statString = "None";

		int index = baseSpellIndex();
		if (index >= 0)
		{
			//statString = Globals.s_STATNAMES.substring(index * 3, index * 3 + 3);
			statString = Globals.s_ATTRIBSHORT[index];
		}
		stat += aPC.getTotalBonusTo("STAT", "CAST=" + statString, true);
		stat += aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true);

		if (stat < 10 + spellLevel && index != -2)
			return total;
		int adj = 0;
		if (includeAdj && !bookName.equals(Globals.getDefaultSpellBook()) && (specialtyList.size() > 0 || aPC.getCharacterDomainList().size() > 0))
		{
			// get all spells of this level
			final ArrayList aList = getCharacterSpell(null, "", spellLevel, null, null);
			if (!aList.isEmpty())
			{
				for (Iterator e = aList.iterator(); e.hasNext();)
				{
					CharacterSpell cs = (CharacterSpell)e.next();
					if (cs.isInSpecialty())
					{
						adj = 1;
						break;
					}
				}
			}
		}
		int temp = spellLevel;

		if (pcLevel > castList.size())
			pcLevel = castList.size();

		//
		// Multiplier for things like Ring of Wizardry
		//
		int mult = aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName + levelSpellLevel, true) +
			aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE=" + getSpellType() + levelSpellLevel, true);

		if (mult < 1)
		{
			mult = 1;
		}

		String aString = null;
		if (!castList.isEmpty())
		{
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
		if (uattList.isEmpty())
			return aString;

		String bString = null;
		for (Iterator e = uattList.iterator(); e.hasNext();)
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
		if (umult.isEmpty())
			return aString;

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

	public ArrayList getUdamList()
	{
		return udamList;
	}

	public String getUdamForLevel(int aLevel, boolean includeCrit, boolean includeStrBonus)
	{
		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;
		int iLevel = aLevel;
		Equipment eq = Globals.getEquipmentKeyed("Unarmed Strike");
		if (eq != null)
		{
			aDamage = eq.getDamage();
		}
		else
		{
			aDamage = "1d3";
		}
		//
		// resize the damage as if it were a weapon
		//
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int iSize = Globals.sizeInt(aPC.getSize());
		aDamage = Globals.adjustDamage(aDamage, Constants.s_SIZESHORT[Constants.SIZE_M], Constants.s_SIZESHORT[iSize]);

		//
		// Check the UDAM list for monk-like damage
		//
		if (!udamList.isEmpty())
		{
			for (Iterator e = getUdamList().iterator(); e.hasNext() && (iLevel > 0); iLevel--)
			{
				final String bString = (String)e.next();
				if (iLevel == 1)
				{
					final StringTokenizer aTok = new StringTokenizer(bString, ",", false);
					while (iSize > -1 && aTok.hasMoreTokens())
					{
						aDamage = aTok.nextToken();
						if (iSize == 0)
						{
							break;
						}	
						iSize -= 1;
					}
				}
			}
		}


		StringBuffer aString = new StringBuffer(aDamage);
		if (includeStrBonus && aPC.adjStats(Constants.STRENGTH) / 2 > 5)
			aString.append("+");
		if (includeStrBonus && aPC.adjStats(Constants.STRENGTH) / 2 != 5)
			aString.append(aPC.calcStatMod(Constants.STRENGTH));
		if (includeCrit)
		{
			final String dString = getUMultForLevel(aLevel);
			if (!dString.equals("0"))
				aString.append("(x").append(dString).append(")");
		}
		return aString.toString();
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
		final int move = Globals.getCurrentPC().getRace().getMovement().intValue();
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
		if (acList.isEmpty())
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

	/** return Set of Strings Language names */
	public Set getLanguageAutos()
	{
		return languageAutos;
	}

	/** return Set of Language objects */
	public Set getAutoLanguages()
	{
		final Set aSet = new TreeSet();
		if (languageAutos.isEmpty())
			return aSet;
		for (Iterator i = languageAutos.iterator(); i.hasNext();)
		{
			final Language aLang = Globals.getLanguageNamed(i.next().toString());
			if (aLang != null)
				aSet.add(aLang);
		}
		return aSet;
	}

	public void setLanguageAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			languageAutos.add(aTok.nextToken());
	}

	public Set getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLanguageBonus(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
			getLanguageBonus().add(aTok.nextToken());
	}

	public Collection getWeaponProfAutos()
	{
		return weaponProfAutos;
	}

	public void setWeaponProfAutos(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			getWeaponProfAutos().add(aTok.nextToken());
		}
	}

	public Collection getFeatAutos()
	{
		return featAutos;
	}

	public void setFeatAutos(int aLevel, String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		final String prefix = aLevel + "|";
		while (aTok.hasMoreTokens())
		{
			final String fName = aTok.nextToken();
			if (fName.startsWith(".CLEAR"))
			{
				if (fName.startsWith(".CLEAR."))
				{
					//remove feat by name, must run through all 20 levels
					for(int i=0; i<20; i++)
						featAutos.remove(i+"|"+fName.substring(7));
				}
				else // clear em all
					featAutos.clear();
			}
			else
				getFeatAutos().add(prefix + fName);
		}
	}

	public ArrayList getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	public void setWeaponProfBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			getWeaponProfBonus().add(aTok.nextToken());
	}

	public Integer getLevel()
	{
		return level;
	}

	public void setLevel(Integer newLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.addRacialSpells();
		aPC.addTemplateSpells();
		if (newLevel.intValue() >= 0)
			level = newLevel;

//System.err.println("spells="+knownSpellsList.size()+" import="+aPC.isImporting()+" subclass="+subClassName+" special="+specialtyList.size()+" PCName:"+aPC.getName());
		if (knownSpellsList.size() > 0 && !aPC.isImporting())
		{
			// don't want to get auto known spells
			if (!aPC.getAutoSpells())
				return;
			// get all spells for this class
			ArrayList dList = aPC.getCharacterDomainList();
			final String pcClassName = "PCClass|" + name;
			for (int x = 0; x < dList.size() + 1; x++)
			{
				PCClass aClass = this;
				final String defaultSpellBookName = Globals.getDefaultSpellBook();
				ArrayList cspelllist = Globals.getCharacterSpell(null, defaultSpellBookName, -1, null, aClass);
				if (x > 0)
				{
					CharacterDomain cDomain = (CharacterDomain)dList.get(x - 1);
					if (cDomain.getDomainSource().startsWith(pcClassName) && (cDomain.getDomain() != null))
					{
						cspelllist = Globals.getCharacterSpell(null, defaultSpellBookName, -1, (PObject)cDomain.getDomain(), null);
					}
					else
					{
						continue;
					}
				}
				if (cspelllist.isEmpty())
					continue;
				Iterator gCS = cspelllist.iterator();

				for (; gCS.hasNext();)
				{
					final CharacterSpell allCS = (CharacterSpell)gCS.next();
					final Spell aSpell = allCS.getSpell();
					final int allCSlevel = allCS.getLevel();

					// make sure not to add prohibited
					// spells from the auto knownspells
					if ((!allCS.isInSpecialty()) &&
						(prohibitedStringContains(aSpell.getSchool()) || prohibitedStringContains(aSpell.getDescriptorList())))
						continue;
					if (allCSlevel >= 0 && isAutoKnownSpell(aSpell.getKeyName(), allCSlevel))
					{
						final boolean addIt = getCastForLevel(level.intValue(), allCSlevel) > 0;
						CharacterSpell cs = getFirstCharacterSpell(aSpell, defaultSpellBookName, allCSlevel, null, aClass);
						;
						if (addIt && cs == null)
						{

							cs = new CharacterSpell(aClass, aClass, aSpell);
							cs.setLevel(allCSlevel);
							addCharacterSpell(cs);
//System.err.println("setLevel: added spell:"+characterSpellList.size()+": "+cs.getSpell().getName());
						}
					}
				}
			}
		}
	}

	public void addLevel(boolean levelMax)
	{
		addLevel(levelMax, false);
	}

	public void doPlusLevelMods(PlayerCharacter aPC, int newLevel)
	{
		if (!isMonster())
			changeFeatsForLevel(newLevel, true);
		addAddsForLevel(newLevel);
		aPC.changeSpecialAbilitiesForLevel(newLevel, true, specialAbilityList());
		changeSubSpecialAbilitiesForLevel(newLevel, false);
		changeSpecials();
		addVariablesForLevel(newLevel);
	}

	public void doMinusLevelMods(PlayerCharacter aPC, int oldLevel)
	{
		if (!isMonster())
			changeFeatsForLevel(oldLevel, false);
		subAddsForLevel(oldLevel);
		aPC.changeSpecialAbilitiesForLevel(oldLevel, false, specialAbilityList());
		changeSubSpecialAbilitiesForLevel(oldLevel, true);
		changeSpecials();
		aPC.removeVariable("CLASS:" + getName() + "|" + new Integer(oldLevel).toString());
	}

	public void addLevel(boolean levelMax, boolean bSilent)
	{
		Integer newLevel = new Integer(level.intValue() + 1);
		if (isMonster())
			levelMax = false;
		if (newLevel.intValue() > maxLevel && levelMax)
		{
			if (!bSilent)
			{
				GuiFacade.showMessageDialog(null, "This class cannot be raised above level " + new Integer(maxLevel).toString(), "PCGen", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		final PlayerCharacter aPC = Globals.getCurrentPC();
		int total = aPC.totalLevels();
		if (total == 0)
			aPC.setFeats(aPC.getInitialFeats());

		setLevel(newLevel);
		ArrayList templateList = getTemplates(aPC.getIsImporting());
		for (int x = 0; x < templateList.size(); x++)
		{
			aPC.addTemplate(Globals.getTemplateNamed((String)templateList.get(x)));
		}
		int dnum = getBonusTo("DOMAIN", "NUMBER", newLevel.intValue());
		if (dnum > 0 && !aPC.getCharacterDomainList().isEmpty())
		{
			for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
			{
				CharacterDomain aCD = (CharacterDomain)i.next();
				// if the characterdomain comes from this class, decrement total
				if (aCD.getDomainSource().startsWith("PCClass|" + getName()))
					dnum--;
			}
		}
		if (!levelAbilityList.isEmpty())
		{
			for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
			{
				LevelAbility ability = (LevelAbility)e.next();
				if (ability.level() <= newLevel.intValue() && ability.getList().startsWith("SPELLCASTER"))
				{
					// assume that classes which gain Domains, but also have ADD:SPELLCASTER will actually
					// grant the domains to the selected class, and this will all be handed in LevelAbility
					// rather than here.
					dnum = 0;
					break;
				}
			}
		}
		final String domainSource = "PCClass|" + getName() + "|" + newLevel.toString();
		for (; dnum > 0; dnum--) // character has more domains due this class than currently allocated
		{
			CharacterDomain aCD = new CharacterDomain();
			aCD.setDomainSource(domainSource);
			aPC.getCharacterDomainList().add(aCD);
		}
		if (isMonster())
		{
			if (levelsPerFeat != 0)
				if (aPC.totalHitDice() % levelsPerFeat == 0)
					aPC.setFeats(aPC.getFeats() + 1);
		}

		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(aPC, newLevel.intValue());

		if (newLevel.intValue() == 1 && !aPC.isImporting())
			checkForSubClass();
		rollHp();

		if (!aPC.isImporting())
		{
			if (!levelAbilityList.isEmpty())
			{
				LevelAbility ability = null;
				for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
				{
					ability = (LevelAbility)e.next();
					if (ability.level() == newLevel.intValue() && ability.canProcess())
						ability.process();
				}
			}
			modDomainsForLevel(newLevel.intValue(), true);
		}
		if (!isMonster() && aPC.totalLevels() > total)
		{
			total = aPC.totalLevels();
			if (!aPC.isImporting())
			{
				// We do not want to do these calculations a second
				// time when are importing a character.  The feat number
				// and the stat point pool are already saved in the import file.
				if (aPC.getExperience().intValue() < Globals.minExpForLevel(total + aPC.levelAdjustment()))
					aPC.setExperience(new Integer(Globals.minExpForLevel(total + aPC.levelAdjustment())));
				if (total % 3 == 0)
					aPC.setFeats(aPC.getFeats() + 1);
				if (total % 4 == 0)
				{
					aPC.setPoolAmount(aPC.getPoolAmount() + 1);
					if (!bSilent)
					{
						if (Globals.isSkillIncrementBefore())
						{
							//
							// Ask user to select a stat to increment. This happens before skill points
							// are calculated, so an increase to INTELLIGENCE can give more skill points
							//
							//String sStats = "";
							StringBuffer sStats = new StringBuffer();
							for (int i = 0; i < 6; i++)
							{
								final int iAdjStat = aPC.adjStats(i);
								final int iCurStat = aPC.getStat(i);
								//sStats += Globals.s_ATTRIBSHORT[i] + ": " + iCurStat;
								sStats.append(Globals.s_ATTRIBSHORT[i]).append(": ").append(iCurStat);
								if (iCurStat != iAdjStat)
								{
									//sStats += " adjusted: " + iAdjStat;
									sStats.append(" adjusted: ").append(iAdjStat);
								}
								//sStats += " (" + aPC.calcStatMod(i) + ")\n";
								sStats.append(" (").append(aPC.calcStatMod(i)).append(")\n");
							}
							Object selectedValue = JOptionPane.showInputDialog(null,
								"Choose stat to increment or select Cancel to increment stat on the Stat tab.\nRaising " +
								Globals.s_ATTRIBLONG[Constants.INTELLIGENCE] + " here may award more skill points.\n\nCurrent Stats:\n" + sStats + "\n",
								"PCGen", JOptionPane.INFORMATION_MESSAGE, null,
								Globals.s_ATTRIBLONG, Globals.s_ATTRIBLONG[0]);

							if (selectedValue != null)
							{
								for (int i = 0; i < 6; i++)
								{
									if (Globals.s_ATTRIBLONG[i].equals((String)selectedValue))
									{
										aPC.setStat(i, aPC.getStat(i) + 1);
										aPC.setPoolAmount(aPC.getPoolAmount() - 1);
										break;
									}
								}
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "You can increment a stat on the Stat tab.", "PCGen", JOptionPane.WARNING_MESSAGE);
						}
					}
				}
			}
		}

		int spMod = 0;
		if (isMonster())
		{
			if (aPC.isMonsterDefault())
				spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel() + aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER", true);
			else
				spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel();
		}
		else
		{
			spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel() + aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER", true);
		}

		if (intModToSkills)
		{
			spMod += aPC.calcStatMod(Constants.INTELLIGENCE);
			if (spMod < 1)
				spMod = 1;
		}

//System.out.println("addLevel:"+this.getName()+" spMod:"+spMod+"  SP:"+getSkillPoints()+"  racebSPLevel:"+aPC.getRace().getBonusSkillsPerLevel()+"  Int:"+aPC.calcStatMod(Constants.INTELLIGENCE)+"  TBT:"+aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER", true));

		if (!aPC.getTemplateList().isEmpty())
		{
			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate)e.next();
				spMod += aTemplate.getBonusSkillsPerLevel();
			}
		}


		if (!isMonster() && total == 1)
		{
			if (Globals.isPurchaseStatMode())
				aPC.setPoolAmount(0);
			spMod *= aPC.getRace().getInitialSkillMultiplier();
			aPC.getRace().rollAgeForAgeSet(getAgeSet());
			skillPool = new Integer(spMod);
			aPC.setGold(rollGold().toString());
		}
		else
		{
			skillPool = new Integer(skillPool().intValue() + spMod);
		}

		spMod += aPC.getSkillPoints();
		aPC.setSkillPoints(spMod);

//System.out.println("addLevel:"+this.getName()+" spMod:"+spMod);

		// this is a monster class, so don't worry about experience
		if (isMonster())
			return;

		if (aPC.getExperience().intValue() < Globals.minExpForLevel(total + aPC.levelAdjustment()))
		{
			aPC.setExperience(new Integer(Globals.minExpForLevel(total + aPC.levelAdjustment())));
		}
		else if (aPC.getExperience().intValue() >= Globals.minExpForLevel(total + 1 + aPC.levelAdjustment()))
		{
			if (!bSilent)
			{
				GuiFacade.showMessageDialog(null, "You can advance another level with your experience.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		//
		// Allow exchange of classes only when assign 1st level
		//
		if ((levelExchange.length() != 0) && (getLevel().intValue() == 1) && !aPC.isImporting())
		{
			final StringTokenizer aTok = new StringTokenizer(levelExchange, "|", false);
			if (aTok.countTokens() != 4)
			{
				System.out.println("levelExhange: invalid token count: " + aTok.countTokens());
			}
			else
			{
				try
				{
					final String sClass = aTok.nextToken();				// Class to get levels from
					final int iMinLevel = Integer.parseInt(aTok.nextToken());	// Minimum level required in donating class
					int iMaxDonation = Integer.parseInt(aTok.nextToken());	// Maximum levels donated from class
					final int iLowest = Integer.parseInt(aTok.nextToken());	// Lowest that donation can lower donating class level to

					final PCClass aClass = aPC.getClassNamed(sClass);
					if (aClass != null)
					{
						final int iLevel = aClass.getLevel().intValue();
						if (iLevel >= iMinLevel)
						{
							iMaxDonation = Math.min(Math.min(iMaxDonation, iLevel - iLowest), getMaxLevel() - 1);
							if (iMaxDonation > 0)
							{
								//
								// Build the choice list
								//
								ArrayList choiceNames = new ArrayList();
								for (int i = 0; i <= iMaxDonation; i++)
								{
									choiceNames.add(new Integer(i).toString());
								}
								//
								// Get number of levels to exchange for this class
								//
								final Chooser c = new Chooser();
								c.setTitle("Select number of levels to convert from " + sClass + " to " + getName());
								c.setPool(1);
								c.setPoolFlag(false);
								c.setAvailableList(choiceNames);
								c.show();

								final List selectedList = c.getSelectedList();
								int iLevels = 0;
								if (!selectedList.isEmpty())
								{
									iLevels = Integer.parseInt((String)selectedList.get(0));
								}
								if (iLevels > 0)
								{
									aPC.giveClassesAway(this, aClass, iLevels);
								}
							}
						}
					}
				}
				catch (NumberFormatException exc)
				{
					GuiFacade.showMessageDialog(null, "levelExchange:\n" + exc.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	public void subLevel()
	{
		subLevel(false);
	}

	public void subLevel(boolean bSilent)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			int total = aPC.totalLevels();
			int spMod = 0;
			PCClass aClass = aPC.getClassNamed(this.toString());

			if (!aClass.getType().equalsIgnoreCase("MONSTER"))
			{
				if (isMonster() && !intModToSkills)
					spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel();
				else
					spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel() + aPC.calcStatMod(Constants.INTELLIGENCE);
				if (spMod < 1)
					spMod = 1;
			}


			// XXX Why is the feat decrementing done twice (here and in
			// subAddsForLevel())? The code works correctly, but I don't know
			// why.
			// Also, the use of instanceof is kinda ugly.
			if (!levelAbilityList.isEmpty())
			{
				for (Iterator e1 = levelAbilityList.iterator(); e1.hasNext();)
				{
					LevelAbility ability = (LevelAbility)e1.next();
					if (ability.level() == level.intValue() && ability instanceof LevelAbilityFeat)
						aPC.setFeats(aPC.getFeats() - 1);
				}
			}
			Integer zeroInt = new Integer(0);
			Integer newLevel = new Integer(level.intValue() - 1);
			if (level.intValue() > 0)
			{
				hitPointList[level.intValue() - 1] = zeroInt;
			}
			setLevel(newLevel);
			if (isMonster())
			{
				if (levelsPerFeat != 0)
					if ((aPC.totalHitDice() + 1) % levelsPerFeat == 0)
						aPC.setFeats(aPC.getFeats() - 1);
			}

			doMinusLevelMods(aPC, newLevel.intValue() + 1);

			modDomainsForLevel(newLevel.intValue(), false);
			if (newLevel.intValue() == 0)
			{
				setSubClassName("None");
				//
				// Remove all skills associated with this class
				//
				ArrayList aSkills = aPC.getSkillList();
				for (int i = 0; i < aSkills.size(); i++)
				{
					Skill aSkill = (Skill)aSkills.get(i);
					aSkill.setZeroRanks(this);
				}
				spMod = skillPool().intValue();
			}


			if (!isMonster() && total > aPC.totalLevels())
			{
				total = aPC.totalLevels();
				if (aPC.getExperience().intValue() >= Globals.minExpForLevel(total + 1 + aPC.levelAdjustment()))
				{
					int minXP = Globals.minExpForLevel(total + 1 + aPC.levelAdjustment()) - 1;
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
					if (!bSilent)
					{
						if (Globals.isSkillIncrementBefore())
						{
							//
							// Ask user to select a stat to decrement.
							//
							//String sStats = "";
							StringBuffer sStats = new StringBuffer();
							for (int i = 0; i < 6; i++)
							{
								final int iAdjStat = aPC.adjStats(i);
								final int iCurStat = aPC.getStat(i);
								//sStats += Globals.s_ATTRIBSHORT[i] + ": " + iCurStat;
								sStats.append(Globals.s_ATTRIBSHORT[i]).append(": ").append(iCurStat);
								if (iCurStat != iAdjStat)
								{
									//sStats += " adjusted: " + iAdjStat;
									sStats.append(" adjusted: ").append(iAdjStat);
								}
								//sStats += " (" + aPC.calcStatMod(i) + ")\n";
								sStats.append(" (").append(aPC.calcStatMod(i)).append(")\n");
							}
							Object selectedValue = JOptionPane.showInputDialog(null,
								"Choose stat to decrement or select Cancel to decrement stat on the Stat tab.\n\nCurrent Stats:\n" + sStats + "\n",
								"PCGen", JOptionPane.INFORMATION_MESSAGE, null,
								Globals.s_ATTRIBLONG, Globals.s_ATTRIBLONG[0]);

							if (selectedValue != null)
							{
								for (int i = 0; i < 6; i++)
								{
									if (Globals.s_ATTRIBLONG[i].equals((String)selectedValue))
									{
										aPC.setStat(i, aPC.getStat(i) - 1);
										aPC.setPoolAmount(aPC.getPoolAmount() + 1);
										break;
									}
								}
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "You lost a stat point due to level decrease. See the Stat tab.", "PCGen", JOptionPane.WARNING_MESSAGE);
						}
					}
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
			aPC.ValidateCharacterDomains();
		}
		else
		{
			System.out.println("No current pc in subLevel()? How did this happen?");
			return;
		}
	}

	public void modDomainsForLevel(final int aLevel, final boolean adding)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		// any domains set by level would have already been saved
		// and don't need to be re-set at level up time
		if (aPC.isImporting())
			return;
		int c = 2;
		if (aLevel > 9)
			c = 3;
		if (domainList.isEmpty())
			return;
		for (Iterator i = domainList.iterator(); i.hasNext();)
		{
			final String aString = (String)i.next();
			final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			final int bLevel = Integer.parseInt(aTok.nextToken());
			int d = c;
			if (aLevel == bLevel)
			{
				final StringTokenizer bTok = new StringTokenizer(aString.substring(c), "[]|", true);
				ArrayList preReqList = new ArrayList();
				boolean addNow = true;
				String aName = "";
				boolean inPreReqs = false;
				while (bTok.hasMoreTokens())
				{
					String bString = bTok.nextToken();
					if (!inPreReqs && !bString.equals("[") && !bString.equals("|"))
						aName = bString;
					d += bString.length();
					if (bTok.hasMoreTokens())
					{
						if (aString.substring(d, d + 1).equals("["))
							addNow = false;
					}
					else
						addNow = true;
					if (bString.equals("["))
					{
						inPreReqs = true;
					}
					else if (bString.equals("]"))
					{ // this ends a PRExxx tag so next time through we can add name
						addNow = true;
						inPreReqs = false;
					}
					if (addNow && !adding)
					{
						int l = aPC.getCharacterDomainIndex(aName);
						if (l > -1)
							aPC.getCharacterDomainList().remove(l);
					}
					else if (adding && addNow && aName.length() > 0)
					{
						int l = aPC.getCharacterDomainIndex(aName);
						int j = aPC.getFirstEmptyCharacterDomain();
						if (l == -1 && j >= 0)
						{
							final Domain aDomain = Globals.getDomainNamed(aName);
							final CharacterDomain aCD = (CharacterDomain)aPC.getCharacterDomainList().get(j);
							if (preReqList.size() == 0 ||
								aDomain != null && aDomain.passesPreReqTestsForList(preReqList))
							{
								aCD.setDomain(aDomain);
							}
						}
						preReqList.clear();
						aName = "";
					}
					if (adding && inPreReqs && (bString.startsWith("PRE") || bString.startsWith("!PRE")))
						preReqList.add(bString);
				}
			}
		}
	}

	public int memorizedSpellForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final ArrayList aList = getCharacterSpell(null, bookName, aLevel, null, null);
		if (aList.isEmpty())
			return m;
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell)i.next();
			if (Globals.isSSd20Mode())
			{
				m += cs.getSpell().getCastingThreshold();
			}
			else
			{
				m += cs.getTimes();
			}
		}
		return m;
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
			final PCClass aClass = (PCClass)e.next();
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
				{
					//
					// Sanity check
					//
					if (Globals.getSpecialsList().size() > i)
					{
						saList.add(Globals.getSpecialsList().get(i++));
					}
				}
			}
		}
		if (!saList.isEmpty())
		{
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
		}
		if (!saList.isEmpty())
		{
			for (e = saList.iterator(); e.hasNext();)
			{
				SpecialAbility sa = (SpecialAbility)e.next();
				aPC.getSpecialAbilityList().add(sa.getName() + " (" + sa.getDesc() + ")");
			}
		}
	}

	private void addVariablesForLevel(final int aLevel)
	{
		if (getVariableCount()==0)
			return;
		if (aLevel == 1)
			addVariablesForLevel(0);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String aString = null;
		StringTokenizer aTok = null;
		String bString = null;
		final String prefix = "CLASS:" + name + "|";
		for (int i=0; i<getVariableCount(); i++)
		{
			aString = getVariable(i);
			aTok = new StringTokenizer(aString, "|", false);
			bString = aTok.nextToken();
			if (Integer.parseInt(bString) == aLevel)
			{
				aPC.addVariable(prefix + aString);
			}
		}
	}

	public Integer[] getHitPointList()
	{
		return hitPointList;
	}

	public void setHitPointList(final Integer[] newList)
	{
		hitPointList = newList;
	}

	public int hitPoints(int iConMod)
	{
		int total = 0;
		final int levelValue = level.intValue();
		for (int i = 0; i < levelValue && i < hitPointList.length; i++)
		{
			if (hitPointList[i] != null)
			{
				int iHp = hitPointList[i].intValue() + iConMod;
				if (iHp < 1)
				{
					iHp = 1;
				}
				total += iHp;
			}
		}
		return total;
	}

	public Integer[] hitPointList()
	{
		return hitPointList;
	}

	public void PCG_adjustHpRolls(final int increment)
	{
		final int levelIntValue = level.intValue();
		if (levelIntValue == 0)
			return;
		for (int i = 0; i < levelIntValue; i++)
		{
			final Integer roll = new Integer(hitPointList[i].intValue() + increment);
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
			switch (Globals.getHpRollMethod())
			{
				case Constants.s_HP_STANDARD:
					roll = Math.abs(Globals.getRandomInt(max - min + 1)) + min;
					break;
				case Constants.s_HP_AUTOMAX:
					roll = max;
					break;
				case Constants.s_HP_PERCENTAGE:
					float pct = (float)Globals.getHpPct();
					float maxFloat = max;
					float minFloat = min;
					roll = (int)((pct / 100.0) * (maxFloat - minFloat) + minFloat);
					break;
				case Constants.s_HP_LIVING_GREYHAWK:
					if (totalLevels == 1)
						roll = max;
					else
						roll = (int)Math.floor((max + min) / 2) + 1;
					break;
				case Constants.s_HP_LIVING_CITY:
					if (totalLevels == 1 || totalLevels == 2)
						roll = max;
					else
					{
						roll = (int)Math.floor(3 * max / 4);
						// In the bizarre case a class has a max of 1, need to fix that Floor will make that 0 instead.
						if (roll < min) roll = min;
					}
					break;
				default:
					//What to do here??
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
			final Integer[] newList = new Integer[level.intValue()];
			System.arraycopy(hitPointList, 0, newList, 0, hitPointList.length);

			int i = hitPointList.length;
			while (i < newList.length)
			{
				newList[i++] = new Integer(0);
			}
			setHitPointList(newList);
		}
	}

	public int baseAttackBonus()
	{
		if (level.intValue() == 0)
		{
			return 0;
		}

		int i = this.getBonusTo("TOHIT", "TOHIT", level.intValue());
		if (getAttackBonusType().equals("G"))
		{
			return i + level.intValue();
		}
		else if (getAttackBonusType().equals("M"))
		{
			return i + 3 * (level.intValue()) / 4;
		}
		else if (getAttackBonusType().equals("B"))
		{
			return i + level.intValue() / 2;
		}
		else if (getAttackBonusType().equals("O"))
		{
			final String aString = Globals.getCurrentPC().getVariable("BAB", true, false, "CLASS:" + name, "").toString();
			final int pos = Math.max(0, aString.lastIndexOf("."));
			return i + Integer.parseInt(aString.substring(0, pos));
		}
		return i;
	}

	public int fortitudeCheckBonus()
	{
		if (level.intValue() == 0)
			return 0;
		return checkBonus(fortitudeCheckType, "FORTBASE");
	}

	public int checkBonus(String bonusType, String defString)
	{
		if (bonusType.equals("G"))
		{
			return 2 + level.intValue() / 2;
		}
		else if (bonusType.equals("B"))
		{
			return level.intValue() / 3;
		}
		else if (bonusType.equals("M"))
		{
			return 1 + level.intValue() / 5 + (3 + level.intValue()) / 5;
		}
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
		return checkBonus(reflexCheckType, "REFLEXBASE");
	}

	public int willCheckBonus()
	{
		if (level.intValue() == 0)
			return 0;
		return checkBonus(willCheckType, "WILLBASE");
	}

	public String classLevelString()
	{
		StringBuffer aString = new StringBuffer();
		if (!getSubClassName().equals("None") && !getSubClassName().equals(""))
		{
			aString.append(getSubClassName());
		}
		else
		{
			aString.append(getName());
		}
		aString = aString.append(" ").append(level.toString());
		return aString.toString();
	}

	public Collection featList()
	{
		return featList;
	}

	// int level e.g. 1
	// featList Shield Proficieny,Armor Proficiency (light)
	public void addFeatList(final int aLevel, final String aFeatList)
	{
		final String aString = aLevel + ":" + aFeatList;
		featList.add(aString);
	}

	public Collection vFeatList()
	{
		return vFeatList;
	}

	// int level e.g. 1
	// featList Shield Proficieny,Armor Proficiency (light)
	public void addVFeatList(final int aLevel, final String aFeatList)
	{
		final String aString = aLevel + ":" + aFeatList;
		vFeatList.add(aString);
	}

	private String getToken(int tokenNum, final String aList, final String delim)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, delim, false);
		while (aTok.hasMoreElements() && tokenNum >= 0)
		{
			final String aString = aTok.nextToken();
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
	public void changeFeatsForLevel(final int aLevel, final boolean addThem)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || featList.isEmpty())
			return;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final String feats = (String)e.next();
			if (aLevel == Integer.parseInt(getToken(0, feats, ":")))
			{
				aPC.modFeatsFromList(getToken(1, feats, ":"), addThem, aLevel == 1);
			}
		}
	}

	public void subFeatsForLevel(final int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || featList.isEmpty())
			return;
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final String feats = (String)e.next();
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

	public void addSpecialAbilityList(final int aLevel, final String aList)
	{
		final String aString = aLevel + ":" + aList;
		specialAbilityList.add(aString);
	}

	public void removeSpecialAbilityList(final int aLevel, final String aList)
	{
		final String aString = aLevel + ":" + aList;
		specialAbilityList.remove(aString);
	}

	public Collection subSpecialAbilityList()
	{
		return subSpecialAbilityList;
	}

	public void addSubSpecialAbilityList(final int aLevel, final String aList)
	{
		final String aString = aLevel + ":" + aList;
		subSpecialAbilityList.add(aString);
	}

	public void changeSubSpecialAbilitiesForLevel(int aLevel, boolean addIt)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || subSpecialAbilityList.isEmpty())
			return;
		for (Iterator e = subSpecialAbilityList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			int thisInt = Integer.parseInt(getToken(0, aString, ":"));
			String aList = getToken(1, aString, ":");
			if (aLevel == thisInt)
			{
				final StringTokenizer aStrTok = new StringTokenizer(aList, ",", false);
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

	public int getSpellsInSpecialtyForLevel(final int aLevel)
	{
		int retVal = 0;
		final ArrayList aList = getCharacterSpell(null, Globals.getDefaultSpellBook(), aLevel, null, null);
		if (aList.isEmpty())
			return retVal;
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell)i.next();
			if (cs.isInSpecialty())
				retVal++;
		}
		return retVal;
	}

	public void addAddList(final int aLevel, final String aList)
	{
		if (aList.startsWith(".CLEAR"))
		{
			if (aList.equals(".CLEAR"))
				levelAbilityList.clear();
			else if (aList.indexOf(".LEVEL") >-1)
			{
				int level = Integer.parseInt(aList.substring(12));
				if (level>=0)
				{
					for (int x = levelAbilityList.size()-1; x>=0; x--)
					{
						final LevelAbility ability = (LevelAbility)levelAbilityList.get(x);
						if (ability.level() == level)
						{
							levelAbilityList.remove(x);
						}
					}
				}
			}
		}
		else
			levelAbilityList.add(LevelAbility.createAbility(this, aLevel, aList));
	}

	public void addAddsForLevel(final int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || levelAbilityList.isEmpty())
			return;
		LevelAbility ability = null;
		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			ability = (LevelAbility)e.next();
			if (ability.level() == aLevel)
			{
				ability.addForLevel();
			}
		}
	}

	public void subAddsForLevel(final int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || levelAbilityList.isEmpty())
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

		aTok.nextToken();

		List choiceNames = new ArrayList();
		List fileNames = new ArrayList();
		List choiceNum = new ArrayList();
		List displayChoices = new ArrayList();

		boolean showAdditionalChoices = false;
		String choice = null;
		while (aTok.hasMoreTokens())
		{
			choice = aTok.nextToken();
			int p = choice.indexOf('(');
			int q = choice.lastIndexOf(')');
			if (q == -1)
				q = choice.length();
			displayChoices.add(choice.substring(p + 1, q));
			if (choice.startsWith("FILE="))
			{
				p -= 5; // account for "FILE=" at front
				int i = choice.lastIndexOf('.');
				String fileName = choice.substring("FILE=".length());
				fileName = fileName.replace('\\', File.separatorChar);
				fileName = fileName.replace('/', File.separatorChar);
				if (p > -1)
					fileName = fileName.substring(0, p);
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
			final Chooser c = new Chooser();
			c.setPool(1);
			c.setPoolFlag(false);
//			c.setAvailableList(choiceNames);
			c.setAvailableList(displayChoices);
//			if (choiceNames.size() == 1)
//				c.setSelectedList(choiceNames);
//			else if (choiceNames.size() != 0)
//				c.show();
			if (displayChoices.size() == 1)
				c.setSelectedList(displayChoices);
			else if (displayChoices.size() != 0)
				c.show();

			List selectedList = c.getSelectedList();
			if (!selectedList.isEmpty())
			{
				final String aChoice = (String)selectedList.get(0);
				int i = displayChoices.indexOf(aChoice);
				final String theChoice = (String)choiceNames.get(i);
				if (theChoice.equals("None"))
				{
					return;
				}

//				int i = choiceNames.indexOf(theChoice);

				String fileName = (String)fileNames.get(i);
				if (!fileName.equals("NO"))
				{
					File aFile = new File(Globals.getPccFilesLocation() + File.separator + fileName);

					try
					{
						// Read the file lines into a list
						List availableList = new ArrayList(10);

						BufferedReader reader = new BufferedReader(new FileReader(aFile));

						String line = reader.readLine();
						while (line != null)
						{
							if (!line.startsWith("#"))
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
//System.err.println("specialtyList clear");
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
//System.err.println("bString="+bString+" j="+j);
								switch (j++)
								{
									case 0:
										specialtyList.add(bString);
//System.err.println("added to specialtyList "+specialtyList.size());
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
											Globals.debugPrint("Base Spell Stat for " + name + " =" + spellBaseStat + ".");
										}
										else if (bString.startsWith("KNOWNSPELLSFROMSPECIALTY="))
										{
											numSpellsFromSpecialty = Integer.parseInt(bString.substring(25));
											Globals.debugPrint(name + " has " + numSpellsFromSpecialty + " specialtySpells per level");
										}
										else
										{
											if (prohibitedString.length() > 0)
												prohibitedString = prohibitedString.concat(",");
											prohibitedString = prohibitedString + bString;
										}
// what????										cost = new Integer(0);
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
					catch (IOException exception)
					{
						GuiFacade.showMessageDialog(null, (String)fileNames.get(i) + " error" + exception.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
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

	public void setSkillPool(final int i)
	{
		skillPool = new Integer(i);
	}

	public String goldString()
	{
		return goldString;
	}

	public void setGoldString(final String aString)
	{
		goldString = aString;
	}

	public Integer rollGold()
	{
		int dice = 4;
		int sides = 4;
		int mult = 10;
		int total = 0;
		final StringTokenizer aTok = new StringTokenizer(goldString, ",", false);
		if (aTok.hasMoreTokens())
			dice = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
			sides = Integer.parseInt(aTok.nextToken());
		if (aTok.hasMoreTokens())
			mult = Integer.parseInt(aTok.nextToken());

		if (Globals.isMaxStartingGold())
		{
			total = dice * sides;
		}
		else
		{
			total = RollingMethods.roll(dice, sides);
		}

		total *= mult;
		return new Integer(total);
	}

	public String specialsString()
	{
		return specialsString;
	}

	public void setSpecialsString(final String aString)
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

	public void setDefenseString(final String aString)
	{
		defenseString = aString;
	}

	public Integer defense(final int y)
	{
		String aString = defenseString;
		int i = 0;
		if (aString.length() > 0 && aString.indexOf(',') > -1)
		{
			int k = Integer.parseInt(aString.substring(0, aString.indexOf(',')));
			int m = Integer.parseInt(aString.substring(aString.lastIndexOf(',') + 1));
			if (y > 0)
				i += m;
			final int levelValue = getLevel().intValue();
			switch (k)
			{
/*Best*/  case 0:
					i += 4 + levelValue / 2;
					break;
/*Middle*/  case 1:
/*Prestige*/case 4:
/*Prestige2*/case 5:
					i += 3 + levelValue / 5;
					if (i >= 2)
						i += (levelValue + 3) / 5;
					if (k == 4)
						i -= 2;
					if (k == 5)
						i -= 1;
					break;
/*Low*/case 2:
					i += 2 + levelValue / 3;
					break;
/*NPC*/case 3:
					i += levelValue / 3;
					break;
/*Prestige5*/	case 8:
					i += 2 + (((levelValue + 1) + 3) / 3);
					break;
/*Prestige6*/	case 9:
					i += 2 + ((levelValue + 3) / 3);
					if (levelValue == 8)
						i += 1;
					break;
/*Prestige7*/ 	case 10:
					i += 1 + ((levelValue + 1) / 2);
					break;
/*Prestige8*/ 	case 11:
					i += 2 + ((levelValue + 1) / 2);
					break;
/*Prestige9*/	case 12:
					switch (levelValue)
					{
						case 1:
							i += 1;
							break;
						case 2:
							i += 2;
							break;
						case 3:
							i += 2;
							break;
						case 4:
							i += 2;
							break;
						case 5:
							i += 3;
							break;
						case 6:
							i += 3;
							break;
						case 7:
							i += 4;
							break;
						case 8:
							i += 4;
							break;
						case 9:
							i += 4;
							break;
						case 10:
							i += 5;
							break;
						default:
							//What to do here?
					}
					break;
// New Codes for SW Revised Core Rulebook (20 - 25)
			case 20:
					i += 2 + ((2 * (levelValue + 3)) / 5);
					break;
			case 21:
					i += 1 + ((2 * (levelValue + 3)) / 5);
					break;
			case 22:
					i += 1 + ((levelValue + 1) / 2);
					break;
			case 23:
					i += 2 + ((levelValue + 1) / 2);
					break;
// New Prestige Classes			
			case 24:
					i += ((2 * (levelValue + 3)) / 5);
					break;
			case 25:
					i += 1 + (levelValue / 2);
					break;
			default:
					//What to do here?
			}
		}
		i += getBonusTo("CLASS", "DEFENSE", level.intValue());
		return new Integer(i);
	}

	public String getReputationString()
	{
		return reputationString;
	}

	public void setReputationString(final String aString)
	{
		reputationString = aString;
	}

	public String getExClass()
	{
		return exClass;
	}

	public void setExClass(final String aString)
	{
		exClass = aString;
	}

	public void setLevelExchange(final String aString)
	{
		levelExchange = aString;
	}

	public String getLevelExchange()
	{
		return levelExchange;
	}

	public void addKnownSpellsList(final String aString)
	{
		StringTokenizer aTok;
		if (aString.startsWith("CLEAR."))
		{
			knownSpellsList.clear();
			if (aString.equals("CLEAR."))
				return;
			aTok = new StringTokenizer(aString.substring(6), "|", false);
		}
		else
			aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
			knownSpellsList.add(aTok.nextToken());
	}

	public boolean isAutoKnownSpell(final String spellName, final int spellLevel)
	{
		if (knownSpellsList.isEmpty())
			return false;
		Spell aSpell = (Spell)Globals.getSpellNamed(spellName);
		if (getCastForLevel(level.intValue(), spellLevel) == 0 || aSpell == null)
			return false;
		for (Iterator e = knownSpellsList.iterator(); e.hasNext();)
		{
			String aString = (String)e.next();

			if (aString.startsWith("LEVEL=") && Integer.parseInt(aString.substring(6)) == spellLevel)
				return true;
			if (aString.equals(spellName))
				return true;
		}
		//
		// get all references to this spell
		//
		ArrayList aList = Globals.getCharacterSpell(aSpell, Globals.getDefaultSpellBook(), -1, null, null);
		if (aList.isEmpty())
			return false;
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			CharacterSpell cs = (CharacterSpell)i.next();
			// if any references to this spell
			// are of this level, return true
			for (Iterator ee = knownSpellsList.iterator(); ee.hasNext();)
			{
				String aString = (String)ee.next();

				if (aString.startsWith("LEVEL=") && Integer.parseInt(aString.substring(6)) == cs.getLevel())
					return true;
				if (aString.equals(cs.getSpell().getName()))
					return true;
			}
		}
		return false;
	}

	public void setAttackCycle(final String aString)
	{
		attackCycle = aString;
	}

	public int attackCycle(final int index)
	{
		StringTokenizer aTok = new StringTokenizer(attackCycle, "|", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (((index == Constants.ATTACKSTRING_MELEE) && aString.equals("BAB")) ||
				((index == Constants.ATTACKSTRING_UNARMED) && aString.equals("UAB")))
			{
				return Integer.parseInt(aTok.nextToken());
			}
		}
		return 5;
	}

	public boolean isQualified()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
			return false;
		if (isMonster() && preRaceType != null && !contains(preRaceType, aPC.getCritterType()))
		// Move the check for type out of race and into PlayerCharacter to make it easier for a template to adjust it.
		{
//			System.out.println("PreReq:" + aPC.getRace().getName());
//			System.out.println("PRERACETYPE:" + preRaceType + " RACETYPE:" + aPC.getCritterType() + " false");
			return false;
		}
		if (!allowsAlignment(aPC.getAlignment()) || !canBePrestige())
			return false;
		return true;
	}

	private boolean contains(String little, String big)
	{
		while (big.length() >= little.length())
		{
			if (big.startsWith(little))
				return true;
			big = big.substring(1);
		}
		return false;
	}

	public boolean isPrestige()
	{
		return isType("PRESTIGE");
	}

	public boolean isPC()
	{
		return (getMyTypeCount()==0 || isType("PC"));
	}

	public boolean isNPC()
	{
		return isType("NPC");
	}

	public boolean isMonster()
	{
		return isType("MONSTER");
	}

	/**
	 * Increases or decreases the initiative modifier by the given value.
	 */

	public void addInitMod(int initModDelta)
	{
		initMod += initModDelta;
	}

	/**
	 * Returns the initiative modifier.
	 */

	public int initMod()
	{
		return initMod;
	}

}
