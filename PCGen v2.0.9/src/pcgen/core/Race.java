/*
 * Race.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Race extends PObject
{
	int[] statMods = new int[6];
	boolean[] nonability = new boolean[6];
	String favoredClass = "";
	int bonusSkillsPerLevel = 0;
	int bonusInitialFeats = 0;
	String size = "";
	Integer movement = new Integer(0);
	String vision = "Normal";
	String bonusSkillList = "";
	ArrayList cSkillList = new ArrayList();
	String ageString = "";
	String heightString = "";
	String weightString = "";
	ArrayList languageAutos = new ArrayList();
	ArrayList languageBonus = new ArrayList();
	ArrayList weaponProfAutos = new ArrayList();
	ArrayList weaponProfBonus = new ArrayList();
	ArrayList specialAbilities = new ArrayList();
	String featList = "";
	String vFeatList = "";
	Integer startingAC = new Integer(10);
	Integer naturalAC = new Integer(0);
	Integer initMod = new Integer(0);
	int langNum = 0;
	int initialSkillMultiplier = 4;
	String alignments = "";
	ArrayList weaponProfs = new ArrayList();
	int levelAdjustment = 0;
	int CR = 0;
	int BAB = 0;
	int fortSave = 0;
	int refSave = 0;
	int willSave = 0;
	int hitDice = 0;
	int hitDiceSize = 0;
	Integer[] hitPointList;
	String type = "";
	String[] movementTypes;
	Integer[] movements;
	int[] hitDiceAdvancement;    int hands = 2;    int reach = 5;    String face = "5 ft. by 5 ft.";

	public String toString()
	{
		return name;
	}

	public int statMod(int index)
	{
		return statMods[index];
	}

	private void setStatMod(int index, int value)
	{
		statMods[index] = value;
	}

	public String favoredClass()
	{
		return favoredClass;
	}

	private void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
	}

	private void setFavoredClass(char[] aClass)
	{
		String newClass = new String(aClass);
		setFavoredClass(newClass);
	}

	public boolean canBeAlignment(String aString)
	{
		return (alignments.length() == 0 || alignments.lastIndexOf(aString) > -1);
	}

	public int bonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	private void setBonusSkillsPerLevel(int i)
	{
		bonusSkillsPerLevel = i;
	}

	private void setCSkillList(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			cSkillList.add(aTok.nextToken());
		}
	}

	public int bonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	private void setBonusInitialFeats(int i)
	{
		bonusInitialFeats = i;
	}

	public String size()
	{
		return size;
	}

	public Integer movement()
	{
		return movement;
	}

	private void setMovement(Integer anInt)
	{
		movement = new Integer(anInt.toString());
	}

	public String vision()
	{
		return vision;
	}

	private void setVision(String aString)
	{
		vision = aString;
	}

	public String bonusSkillList()
	{
		return bonusSkillList;
	}

	private void setBonusSkillList(String aString)
	{
		bonusSkillList = aString;
	}

	public int bonusForSkill(String skillName)
	{
		if (bonusSkillList().length() == 0)
		{
			return 0;
		}
		StringTokenizer aTok = new StringTokenizer(bonusSkillList, "=", false);
		while (aTok.hasMoreTokens())
		{
			String skillList = (String)aTok.nextToken();
			Integer anInt = new Integer(aTok.nextToken());
			StringTokenizer bTok = new StringTokenizer(skillList, ",", false);
			while (bTok.hasMoreTokens())
			{
				String aSkill = (String)bTok.nextToken();
				if (aSkill.equals(skillName))
					return anInt.intValue();
			}
		}
		return 0;
	}

	public int LevelAdjustment()
	{
		return levelAdjustment;
	}

	public void setLevelAdjustment(int newLevelAdjustment)
	{
		levelAdjustment = newLevelAdjustment;
	}

	public int CR()
	{
		return CR;
	}

	public void setCR(int newCR)
	{
		CR = newCR;
	}

	public int BAB()
	{
		return BAB;
	}

	public void setBAB(int newBAB)
	{
		BAB = newBAB;
	}

	public int FortSave()
	{
		return fortSave;
	}

	public void setFortSave(int newFortSave)
	{
		fortSave = newFortSave;
	}

	public int RefSave()
	{
		return refSave;
	}

	public void setRefSave(int newRefSave)
	{
		refSave = newRefSave;
	}

	public int WillSave()
	{
		return willSave;
	}

	public void setWillSave(int newWillSave)
	{
		willSave = newWillSave;
	}

	public boolean isNonability(int ability)
	{
		return nonability[ability];
	}

	public int hitDice()
	{
		return hitDice;
	}

	public void setHitDice(int newHitDice)
	{
		if (newHitDice < 0)
		{
			JOptionPane.showMessageDialog(null, "Invalid number of hit dice in race " + name);
			return;
		}
		hitDice = newHitDice;
	}

	public int hitDiceSize()
	{
		return hitDiceSize;
	}

	public void setHitDiceSize(int newHitDiceSize)
	{
		hitDiceSize = newHitDiceSize;
	}

	public String type()
	{
		return type;
	}

	public void setType(String newType)
	{
		type = newType;
	}

	public int[] HitDiceAdvancement()
	{
		return hitDiceAdvancement;
	}

	public int HitDiceAdvancement(int x)
	{
		return hitDiceAdvancement[x];
	}

	public void setHitDiceAdvancement(int[] advancement)
	{
		hitDiceAdvancement = advancement;
	}

	public int sizesAdvanced(int HD)
	{
		for (int x = 0; hitDiceAdvancement != null && x < hitDiceAdvancement.length; x++)
		{
			if (HD <= hitDiceAdvancement[x])
				return x;
		}
		return 0;
	}

	public int maxHitDiceAdvancement()
	{
		return hitDiceAdvancement[hitDiceAdvancement.length - 1];
	}

	public String[] movementTypes()
	{
		return movementTypes;
	}

	/** NOTE: Returns an empty string if the race has no movementtypes defined. */
	public String movementType(int x)
	{
		if (movementTypes != null && x < movementTypes.length)
		{
			return movementTypes[x];
		}
		else
		{
			return "";
		}
	}

	public Integer[] movements()
	{
		return movements;
	}

	/** NOTE: Returns 0 if the race has no movements defined. */
	public Integer movement(int x)
	{
		if (movements != null && x < movements.length)
		{
			return movements[x];
		}
		else
		{
			return new Integer(0);
		}
	}

    public int hands()
    {
        return hands;
    }

    public void setHands(int newHands)
    {
        hands = newHands;
    }      public int reach()    {        return reach;    }
        public void setReach(int newReach)
    {
        reach = newReach;
    }
    
    public String face()
    {
        return face;
    }        public void setFace(String newFace)
    {
        face = newFace;
    }    
	public String ageString()
	{
		return ageString;
	}

	private void setAgeString(String aString)
	{
		ageString = aString;
	}

	public String heightString()
	{
		return heightString;
	}

	private void setHeightString(String aString)
	{
		heightString = aString;
	}

	public String weightString()
	{
		return weightString;
	}

	private void setWeightString(String aString)
	{
		weightString = aString;
	}

	public void rollHeightWeight()
	{
		if (Globals.currentPC == null)
		{
			return;
		}
		boolean startCounting = false;
		StringTokenizer aTok = new StringTokenizer(heightString, ":", false);
		int i = -1;
		int min = 0;
		int diceNum = 0;
		int diceSide = 0;
		int bonus = 0;
		int total = 0;
		Random aRand = new Random();
		while (aTok.hasMoreElements())
		{
			String aString = (String)aTok.nextElement();
			Integer anInt = new Integer(0);
			if (aString.equals(Globals.currentPC.gender()))
			{
				startCounting = true;
			}
			if (i >= 0)
			{
				switch (i)
				{
					case 0:
						min = anInt.parseInt(aString);
						break;
					case 1:
						diceNum = anInt.parseInt(aString);
						break;
					case 2:
						diceSide = anInt.parseInt(aString);
						break;
					case 3:
						bonus = anInt.parseInt(aString);
						total = min;
						while (diceNum > 0)
						{
							int roll = aRand.nextInt(diceSide);
							if (roll < 0) roll = -roll;
							total += roll + 1;
							diceNum--;
						}
						total += bonus;
						Globals.currentPC.setHeight(total);
						bonus = total - min - bonus;
						break;
				}
			}
			if (startCounting == true)
			{
				i++;
			}
		}
		aTok = new StringTokenizer(weightString, ":", false);
		startCounting = false;
		i = -1;
		if (bonus > 0)
		{
			while (aTok.hasMoreElements())
			{
				String aString = (String)aTok.nextElement();
				Integer anInt = new Integer(0);
				if (aString.equals(Globals.currentPC.gender()))
				{
					startCounting = true;
				}
				if (i >= 0)
				{
					switch (i)
					{
						case 0:
							min = anInt.parseInt(aString);
							break;
						case 1:
							diceNum = anInt.parseInt(aString);
							break;
						case 2:
							diceSide = anInt.parseInt(aString);
							total = min;
							while (diceNum > 0)
							{
								int roll = aRand.nextInt(diceSide);
								if (roll < 0) roll = -roll;
								total += (roll + 1) * bonus;
								diceNum--;
							}
							Globals.currentPC.setWeight(total);
							break;
					}
				}
				if (startCounting == true)
				{
					i++;
				}
			}
		}
	}

	public void rollAgeForClass(String className)
	{
		if (Globals.currentPC == null)
		{
			return;
		}
		boolean startCounting = false;
		StringTokenizer aTok = new StringTokenizer(ageString, ":", false);

		// For XML migration, there is a separate entry for each class,
		// and the classes are attributes, so users can define new ones
		// but still give them age features.
		int i = 2;
		if (className.equals("Barbarian") || className.equals("Rogue") ||
			className.equals("Sorcerer"))
		{
			i = 0;
		}
		else if (className.equals("Bard") || className.equals("Fighter") ||
			className.equals("Paladin") || className.equals("Ranger"))
		{
			i = 1;
		}
		int min = 0;
		int diceNum = 0;
		int diceSide = 0;
		int total = 0;
		Random aRand = new Random();
		if (aTok.countTokens() < 7)
		{
			return;
		}
		Integer anInt = new Integer(0);
		min = anInt.parseInt(aTok.nextToken());
		while (i > 0)
		{
			aTok.nextToken();
			aTok.nextToken();
			i--;
		}
		diceNum = anInt.parseInt(aTok.nextToken());
		diceSide = anInt.parseInt(aTok.nextToken());
		total = min;
		while (diceNum > 0)
		{
			int roll = aRand.nextInt(diceSide);
			if (roll < 0) roll = -roll;
			total += roll + 1;
			diceNum--;
		}
		Globals.currentPC.setAge(total);
	}

	public ArrayList languageAutos()
	{
		return languageAutos;
	}

	private void setLanguageAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			languageAutos().add(aTok.nextToken());
		}
	}

	public ArrayList languageBonus()
	{
		return languageBonus;
	}

	private void setLanguageBonus(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			languageBonus().add(aTok.nextToken());
		}
	}

	public ArrayList weaponProfAutos()
	{
		return weaponProfAutos;
	}

	private void setWeaponProfAutos(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			weaponProfAutos().add(aTok.nextToken());
		}
	}

	public ArrayList weaponProfBonus()
	{
		return weaponProfBonus;
	}

	private void setWeaponProfBonus(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			weaponProfBonus().add(aTok.nextToken());
		}
	}

	public ArrayList specialAbilities()
	{
		return specialAbilities;
	}

	private void setSpecialAbilties(String abilities)
	{
		StringTokenizer aTok = new StringTokenizer(abilities, ",", false);
		while (aTok.hasMoreTokens())
		{
			addSpecialAbility(aTok.nextToken());
		}
	}

	private void addSpecialAbility(String abilityName)
	{
		boolean addIt = true;
		for (Iterator e = specialAbilities().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.equals(abilityName))
			{
				addIt = false;
				break;
			}
		}
		if (addIt == true)
		{
			specialAbilities.add(abilityName);
		}
	}

	public void addSpecialAbilitiesForRace()
	{
		if (Globals.currentPC == null)
		{
			return;
		}
		for (Iterator e = specialAbilities().iterator(); e.hasNext();)
		{
			String thisSa = (String)e.next();
			if (!Globals.currentPC.specialAbilityList().contains(thisSa))
			{
				Globals.currentPC.specialAbilityList().add(thisSa);
			}
		}
	}

	public void removeSpecialAbilitiesForRace()
	{
		if (Globals.currentPC == null)
		{
			return;
		}
		for (Iterator e = specialAbilities().iterator(); e.hasNext();)
		{
			String thisSa = (String)e.next();
			if (Globals.currentPC.specialAbilityList().contains(thisSa))
			{
				Globals.currentPC.specialAbilityList().remove(thisSa);
			}
		}
	}

	public Integer startingAC()
	{
		return startingAC;
	}

	public boolean hasFeat(String featName)
	{
		StringTokenizer aTok = new StringTokenizer(featList, "|", false);
		while (aTok.hasMoreTokens())
		{
			if (featName.equals(aTok.nextToken()))
			{
				return true;
			}
		}
		return false;
	}

	private void setWeaponProfs(String aString)
	{
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		String typeString = aTok.nextToken();
		while (aTok.hasMoreTokens())
		{
			weaponProfs.add(typeString + "|" + aTok.nextToken());
		}
	}

	public void setHitPointList(Integer[] newList)
	{
		hitPointList = newList;
	}

	public int hitPoints()
	{
		int i,total = 0;
		for (i = 0; i < hitDice && i < hitPointList.length; i++)
			if (hitPointList[i] != null)
				total += hitPointList[i].intValue();
		return total;
	}

	public Integer[] hitPointList()
	{
		return hitPointList;
	}

	public void adjustHpRolls(int increment)
	{
		if (hitDice == 0)
			return;
		int i;
		for (i = 0; i < hitDice; i++)
		{
			Integer roll;
			int a = hitPointList[i].intValue() + increment;
			if (a > 1)
				roll = new Integer(hitPointList[i].intValue() + increment);
			else
				roll = new Integer(1);
			hitPointList[i] = roll;
		}
	}

	public void rollHp()
	{
		if (hitDice == 0)
			hitPointList[0] = new Integer(0);
		int hp = 0;
		int pct = Globals.hpPct;
		int min = 1 + Globals.currentPC.getTotalBonusTo("HD", "MIN", true);
		int max = hitDiceSize + Globals.currentPC.getTotalBonusTo("HD", "MAX", true);

		hp = max * pct / 100 + (Globals.currentPC.adjStats(2) / 2) - 5;
		Integer maxHp = new Integer(hp);
		hitPointList = new Integer[hitDice];
		Random roller = new Random();
		int roll = 0;
		for (int x = 0; x < hitPointList.length; x++)
		{
			roll = Math.abs(roller.nextInt(max - min + 1)) + min;
			roll += (Globals.currentPC.adjStats(2) / 2) - 5;
			if (roll < 2)
				roll = 1;
			hitPointList[x] = new Integer(roll);
		}
		Globals.currentPC.currentHP = Globals.currentPC.hitPoints();
	}

	public Object clone()
	{
		Race aRace = (Race)super.clone();
		aRace.statMods = statMods;
		aRace.nonability = nonability;
		aRace.favoredClass = new String(favoredClass);
		aRace.bonusSkillsPerLevel = bonusSkillsPerLevel;
		aRace.bonusInitialFeats = bonusInitialFeats;
		aRace.size = new String(size);
		aRace.movement = new Integer(movement().intValue());
		aRace.vision = new String(vision);
		aRace.bonusSkillList = new String(bonusSkillList);
		aRace.cSkillList = (ArrayList)cSkillList.clone();
		aRace.ageString = new String(ageString);
		aRace.heightString = new String(heightString);
		aRace.weightString = new String(weightString);
		aRace.languageAutos = (ArrayList)languageAutos.clone();
		aRace.languageBonus = (ArrayList)languageBonus.clone();
		aRace.weaponProfAutos = (ArrayList)weaponProfAutos.clone();
		aRace.weaponProfBonus = (ArrayList)weaponProfBonus.clone();
		aRace.specialAbilities = (ArrayList)specialAbilities.clone();
		aRace.featList = new String(featList);
		aRace.vFeatList = new String(vFeatList);
		aRace.startingAC = new Integer(startingAC.intValue());
		aRace.naturalAC = new Integer(naturalAC.intValue());
		aRace.initMod = new Integer(initMod.intValue());
		aRace.langNum = langNum;
		aRace.initialSkillMultiplier = initialSkillMultiplier;
		aRace.isSpecified = isSpecified;
		aRace.visible = visible;
		aRace.alignments = alignments;
		aRace.levelAdjustment = levelAdjustment;
		aRace.CR = CR;
		aRace.BAB = BAB;
		aRace.fortSave = fortSave;
		aRace.refSave = refSave;
		aRace.willSave = willSave;
		aRace.hitDice = hitDice;
		aRace.hitDiceSize = hitDiceSize;
		aRace.hitPointList = hitPointList;
		aRace.type = type;
		aRace.hitDiceAdvancement = hitDiceAdvancement;
        aRace.hands = hands;        aRace.reach = reach;        aRace.face = face;
		aRace.weaponProfs = (ArrayList)weaponProfs.clone();
		return aRace;
	}

	public int langNum()
	{
		return langNum;
	}

	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		String tabdelim = "\t";
		StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		int colMax = colToken.countTokens();
		int col = 0;
		Integer anInt = new Integer(0);
		if (colMax == 0)
		{
			return;
		}
		for (col = 0; col < colMax; col++)
		{
			String aString = (String)colToken.nextToken();
			if (col == 0)
			{
				setName(aString);
			}
			else if (col < 7)
			{
				if (aString.equals("*"))
				{
					setStatMod(col - 1, 0);
					nonability[col - 1] = true;
				}
				else
				{
					setStatMod(col - 1, anInt.parseInt(aString));
					nonability[col - 1] = false;
				}
			}
			else if (col == 7)
			{
				setFavoredClass(aString);
			}
			else if (col == 8)
			{
				setBonusSkillsPerLevel(anInt.parseInt(aString));
			}
			else if (col == 9)
			{
				setBonusInitialFeats(anInt.parseInt(aString));
			}
			else if (aString.length() > 4 && aString.substring(0, 4).equals("SIZE"))
			{
				size = aString.substring(5);
			}
			else if (aString.length() > 6 && aString.substring(0, 6).equals("VISION"))
			{
				setVision(aString.substring(7));
			}
			else if (aString.length() > 4 && aString.substring(0, 4).equals("MOVE"))
			{
				StringTokenizer moves = new StringTokenizer(aString.substring(5), ",");
				if (moves.countTokens() == 1)
				{
					setMovement(new Integer(moves.nextToken()));
					movements = new Integer[1];
					movements[0] = movement();
					movementTypes = new String[1];
					movementTypes[0] = "Walk";
				}
				else
				{
					movements = new Integer[moves.countTokens() / 2];
					movementTypes = new String[moves.countTokens() / 2];
					int x = 0;
					while (moves.countTokens() > 1)
					{
						movementTypes[x] = moves.nextToken();
						movements[x] = new Integer(moves.nextToken());
						if (movementTypes[x].equals("Walk"))
							setMovement(movements[x]);                        x++;
					}
				}
			}
			else if (aString.length() > 5 && aString.substring(0, 5).equals("SKILL"))
			{
				setBonusSkillList(aString.substring(6));
			}
			else if (aString.length() > 5 && aString.substring(0, 6).equals("HEIGHT"))
			{
				setHeightString(aString.substring(7));
			}
			else if (aString.length() > 5 && aString.substring(0, 6).equals("WEIGHT"))
			{
				setWeightString(aString.substring(7));
			}
			else if (aString.length() > 5 && aString.substring(0, 3).equals("AGE"))
			{
				setAgeString(aString.substring(4));
			}
			else if (aString.startsWith("LANGAUTO"))
			{
				setLanguageAutos(aString.substring(9));
			}
			else if (aString.startsWith("LANGBONUS"))
			{
				setLanguageBonus(aString.substring(10));
			}
			else if (aString.startsWith("LANGNUM"))
			{
				langNum = Integer.parseInt(aString.substring(8));
			}
			else if (aString.startsWith("WEAPONAUTO"))
			{
				setWeaponProfAutos(aString.substring(11));
			}
			else if (aString.startsWith("WEAPONBONUS"))
			{
				setWeaponProfBonus(aString.substring(12));
			}
			else if (aString.startsWith("SA:"))
			{
				setSpecialAbilties(aString.substring(3));
			}
			else if (aString.startsWith("AC"))
			{
				startingAC = new Integer(aString.substring(3));
			}
			else if (aString.startsWith("INIT"))
			{
				initMod = new Integer(aString.substring(5));
			}
			else if (aString.startsWith("BONUS"))
			{
				addBonusList(aString.substring(6));
			}
			else if (aString.startsWith("SKILLMULT"))
			{
				initialSkillMultiplier = Integer.parseInt(aString.substring(10));
			}
			else if (aString.startsWith("FEAT"))
			{
				featList = aString.substring(5);
			}
			else if (aString.startsWith("VFEAT"))
			{
				vFeatList = aString.substring(6);
			}
			else if (aString.startsWith("CSKILL"))
			{
				setCSkillList(aString.substring(7));
			}
			else if (aString.startsWith("AL"))
			{
				alignments = aString.substring(3);
			}
			else if (aString.startsWith("PROF"))
			{
				setWeaponProfs(aString.substring(5));
			}
			else if (aString.startsWith("LEVELADJUSTMENT"))
			{
				levelAdjustment = Integer.parseInt(aString.substring(16));
			}
			else if (aString.startsWith("CR"))
			{
				CR = Integer.parseInt(aString.substring(3));
			}
			else if (aString.startsWith("SAVES"))   //I know there is already a way to add racial bonuses to saves, but this is for races that give base saves.
			{
				StringTokenizer saves = new StringTokenizer(aString.substring(6), ",");
				if (saves.countTokens() != 3)
					JOptionPane.showMessageDialog(null, "Illegal number of racial save bonuses " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aString + "\"");
				else
				{
					fortSave = Integer.parseInt(saves.nextToken());
					refSave = Integer.parseInt(saves.nextToken());
					willSave = Integer.parseInt(saves.nextToken());
				}
			}
			else if (aString.startsWith("BAB"))
			{
				BAB = Integer.parseInt(aString.substring(4));
			}
			else if (aString.startsWith("HITDICE:"))
			{
				StringTokenizer hitdice = new StringTokenizer(aString.substring(8), ",");
				if (hitdice.countTokens() != 2)
					JOptionPane.showMessageDialog(null, "Illegal racial hit dice format " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aString + "\"");
				else
				{
					hitDice = Integer.parseInt(hitdice.nextToken());
					hitDiceSize = Integer.parseInt(hitdice.nextToken());
				}
			}
			else if (aString.startsWith("TYPE"))
			{
				type = aString.substring(5);
			}
			else if (aString.startsWith("HITDICEADVANCEMENT"))
			{
				StringTokenizer advancement = new StringTokenizer(aString.substring(19), ",");
				hitDiceAdvancement = new int[advancement.countTokens()];
				for (int x = 0; x < hitDiceAdvancement.length; x++)
					hitDiceAdvancement[x] = Integer.parseInt(advancement.nextToken());
			}
            else if (aString.startsWith("HANDS"))
              hands = Integer.parseInt(aString.substring(6));            else if (aString.startsWith("NATURALATTACKS"));
            else if (aString.startsWith("SR"));
            else if (aString.startsWith("DR"));
            else if (aString.startsWith("REACH"))
              reach = Integer.parseInt(aString.substring(6));            else if (aString.startsWith("FACE"))              face = aString.substring(5);
            else if (aString.startsWith("DEFINE"))
			{
				variableList.add("0|" + aString.substring(7));
			}
			else if (aString.startsWith("KEY:"))
			{
				setKeyName(aString.substring(4));
			}
			else if (aString.startsWith("PRE"))
			{
				preReqArrayList.add(aString);
			}
			else
			{
				JOptionPane.showMessageDialog
					(null, "Illegal race info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" +
					aString + "\"");
			}
		}
		if (levelAdjustment != 0 && CR == 0)
			CR = levelAdjustment;
	}

	/** <code>getAlignments</code> returns the string of alignment variables as
	 * it's stored in the .lst file. This is a quick workaround to avoid
	 * querying the class if each of the 9 alignments is allowed, using
	 * <code>canBeAlignment()</code>. That is probably the better practice, in
	 * the long run.
	 */
	public String getAlignments()
	{
		return alignments;
	}

	/**
	 * Overridden to only consider the race's name.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Race)
		{
			if (((Race)obj).name().equals(name()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Overridden to only consider the race's name.
	 */
	public int hashCode()
	{
		return name().hashCode();
	}
}
