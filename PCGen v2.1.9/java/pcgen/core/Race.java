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
	public static final String s_SIZESTRING = "FDTSMLHGC";
	private int[] statMods = new int[6];
	private boolean[] nonability = new boolean[6];
	private String favoredClass = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String size = "";
	private Integer movement = new Integer(0);
	private String vision = "Normal";
	private String bonusSkillList = "";
	private ArrayList cSkillList = new ArrayList();
	private String ageString = "";
	private String heightString = "";
	private String weightString = "";
	private ArrayList languageAutos = new ArrayList();
	private ArrayList languageBonus = new ArrayList();
	private ArrayList weaponProfAutos = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
	private ArrayList specialAbilities = new ArrayList();
	private ArrayList naturalWeapons = new ArrayList();
	private String featList = "";
	private String vFeatList = "";
	private Integer startingAC = new Integer(10);
	private Integer naturalAC = new Integer(0);
	private Integer initMod = new Integer(0);
	private int langNum = 0;
	private int initialSkillMultiplier = 4;
//	private String alignments = "";
	private ArrayList weaponProfs = new ArrayList();
	private int levelAdjustment = 0;
	private int CR = 0;
	private int BAB = 0;
	private int fortSave = 0;
	private int refSave = 0;
	private int willSave = 0;
	private int hitDice = 0;
	private int hitDiceSize = 0;
	private Integer[] hitPointList;
	private String type = "";
	private String[] movementTypes;
	private Integer[] movements;
	private int[] hitDiceAdvancement;
	private String HitDieLock = "%/1";
	private int hands = 2;
	private int reach = 5;
	private String face = "5 ft. by 5 ft.";
	private String DR = "";
	private int SR = 0;
	private ArrayList templates = new ArrayList();

	public Integer getHitPointList(int j)
	{
		return hitPointList[j];
	}

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		hitPointList[aLevel] = iRoll;
	}

	public ArrayList getWeaponProfs()
	{
		return weaponProfs;
	}

	public int getInitialSkillMultiplier()
	{
		return initialSkillMultiplier;
	}

	public Integer getInitMod()
	{
		return initMod;
	}

	public String getVFeatList()
	{
		return vFeatList;
	}

	public String getFeatList()
	{
		return featList;
	}

	public ArrayList getCSkillList()
	{
		return cSkillList;
	}

	public String toString()
	{
		return name;
	}

	public int getStatMod(int index)
	{
		return statMods[index];
	}

	private void setStatMod(int index, int value)
	{
		statMods[index] = value;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}

	private void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
	}

	private void setFavoredClass(char[] aClass)
	{
		setFavoredClass(new String(aClass));
	}

	public boolean canBeAlignment(String aString)
	{
		if (preReqArrayList.size() != 0)
		{
			StringTokenizer aTok = null;
			String aType = null;
			String aList = null;
			for (Iterator e = preReqArrayList.iterator(); e.hasNext();)
			{
				aTok = new StringTokenizer((String)e.next(), ":", false);
				aType = aTok.nextToken();
				aList = aTok.nextToken();
				if (aType.equals("PREALIGN"))
				{
					return aList.lastIndexOf(aString) > -1;
				}
			}
		}
		return true;
//		return (alignments.length() == 0 || aList.lastIndexOf(aString) > -1);
	}

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	private void setBonusSkillsPerLevel(int i)
	{
		bonusSkillsPerLevel = i;
	}

	private void setCSkillList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			cSkillList.add(aTok.nextToken());
		}
	}

	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	private void setBonusInitialFeats(int i)
	{
		bonusInitialFeats = i;
	}

	public String getSize()
	{
		return size;
	}

	public Integer getMovement()
	{
		return movement;
	}

	private void setMovement(Integer anInt)
	{
		movement = new Integer(anInt.toString());
	}

	public String getVision()
	{
		return vision;
	}

	private void setVision(String aString)
	{
		vision = aString;
	}

	public String getBonusSkillList()
	{
		return bonusSkillList;
	}

	private void setBonusSkillList(String aString)
	{
		bonusSkillList = aString;
	}

	public int bonusForSkill(String skillName)
	{
		if (getBonusSkillList().length() == 0)
		{
			return 0;
		}
		final StringTokenizer aTok = new StringTokenizer(bonusSkillList, "=", false);
		while (aTok.hasMoreTokens())
		{
			final String skillList = (String)aTok.nextToken();
			int anInt = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(skillList, ",", false);
			while (bTok.hasMoreTokens())
			{
				final String aSkill = (String)bTok.nextToken();
				if (aSkill.equals(skillName))
					return anInt;
			}
		}
		return 0;
	}

	public int getLevelAdjustment()
	{
		return levelAdjustment;
	}

	public void setLevelAdjustment(int newLevelAdjustment)
	{
		levelAdjustment = newLevelAdjustment;
	}

	public int getCR()
	{
		return CR;
	}

	public void setCR(int newCR)
	{
		CR = newCR;
	}

	public int getBAB()
	{
		return BAB;
	}

	public void setBAB(int newBAB)
	{
		BAB = newBAB;
	}

	public int getFortSave()
	{
		return fortSave;
	}

	public void setFortSave(int newFortSave)
	{
		fortSave = newFortSave;
	}

	public int getRefSave()
	{
		return refSave;
	}

	public void setRefSave(int newRefSave)
	{
		refSave = newRefSave;
	}

	public int getWillSave()
	{
		return willSave;
	}

	public void setWillSave(int newWillSave)
	{
		willSave = newWillSave;
	}

	public boolean isNonability(int ability)
	{
		if (ability < 0 || ability >= 6)
			return true;
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
			JOptionPane.showMessageDialog(null, "Invalid number of hit dice in race " + name, "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		hitDice = newHitDice;
	}

	public int getHitDiceSize()
	{
		return hitDiceSize;
	}

	public void setHitDiceSize(int newHitDiceSize)
	{
		hitDiceSize = newHitDiceSize;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String newType)
	{
		type = newType;
	}

	public String getHitDieLock()
	{
		return HitDieLock;
	}

	public void setHitDieLock(String newHitDieLock)
	{
		HitDieLock = newHitDieLock;
	}

	public int[] getHitDiceAdvancement()
	{
		if (hitDiceAdvancement == null)
			return new int[0];
		return hitDiceAdvancement;
	}

	public int getHitDiceAdvancement(int x)
	{
		if (hitDiceAdvancement == null)
			return 0;
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
		if ((hitDiceAdvancement != null) && (hitDiceAdvancement.length != 0))
		{
			return hitDiceAdvancement[hitDiceAdvancement.length - 1];
		}
		else
		{
			return 0;
		}
	}

	public String[] getMovementTypes()
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

	public Integer[] getMovements()
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

	public int GetHands()
	{
		return hands;
	}

	public void setHands(int newHands)
	{
		hands = newHands;
	}

	public int getReach()
	{
		return reach;
	}

	public void setReach(int newReach)
	{
		reach = newReach;
	}

	public String getFace()
	{
		return face;
	}

	public void setFace(String newFace)
	{
		face = newFace;
	}

	public String getDR()
	{
		return DR;
	}

	public void setDR(String newDR)
	{
		DR = newDR;
	}

	public int getSR()
	{
		return SR;
	}

	public void setSR(int newSR)
	{
		SR = newSR;
	}

	public ArrayList getTemplates()
	{
		return templates;
	}

	public String getAgeString()
	{
		return ageString;
	}

	private void setAgeString(String aString)
	{
		ageString = aString;
	}

	public String getHeightString()
	{
		return heightString;
	}

	private void setHeightString(String aString)
	{
		heightString = aString;
	}

	public String getWeightString()
	{
		return weightString;
	}

	private void setWeightString(String aString)
	{
		weightString = aString;
	}

	public void rollHeightWeight()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
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
		final Random aRand = new Random();
		while (aTok.hasMoreElements())
		{
			final String aString = (String)aTok.nextElement();
			Integer anInt = new Integer(0);
			if (aString.equals(aPC.getGender()))
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
						aPC.setHeight(total);
						bonus = total - min - bonus;
						break;
				}
			}
			if (startCounting)
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
				if (aString.equals(aPC.getGender()))
				{
					startCounting = true;
				}
				if (i >= 0)
				{
					switch (i)
					{
						case 0:
							min = Integer.parseInt(aString);
							break;
						case 1:
							diceNum = Integer.parseInt(aString);
							break;
						case 2:
							diceSide = Integer.parseInt(aString);
							total = min;
							while (diceNum > 0)
							{
								int roll = aRand.nextInt(diceSide);
								if (roll < 0) roll = -roll;
								total += (roll + 1) * bonus;
								diceNum--;
							}
							aPC.setWeight(total);
							break;
					}
				}
				if (startCounting)
				{
					i++;
				}
			}
		}
	}

	public void rollAgeForAgeSet(int i)
	{
		if (Globals.getCurrentPC() == null)
		{
			return;
		}
		boolean startCounting = false;
		final StringTokenizer aTok = new StringTokenizer(ageString, ":", false);

		// For XML migration, there is a separate entry for each class,
		// and the classes are attributes, so users can define new ones
		// but still give them age features.
		int min = 0;
		int diceNum = 0;
		int diceSide = 0;
		int total = 0;
		Random aRand = new Random();
		if (aTok.countTokens() < 2 * i + 3)
		{
			System.out.println("Not enough parameters in " + getName() + "'s definition file to determine age. index=" + i);
			return;
		}
		min = Integer.parseInt(aTok.nextToken());
		while (i > 0)
		{
			aTok.nextToken();
			aTok.nextToken();
			i--;
		}
		diceNum = Integer.parseInt(aTok.nextToken());
		diceSide = Integer.parseInt(aTok.nextToken());
		total = min;
		while (diceNum > 0)
		{
			int roll = aRand.nextInt(diceSide);
			if (roll < 0) roll = -roll;
			total += roll + 1;
			diceNum--;
		}
		Globals.getCurrentPC().setAge(total);
	}

	public ArrayList getLanguageAutos()
	{
		return languageAutos;
	}

	private void setLanguageAutos(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			getLanguageAutos().add(aTok.nextToken());
		}
	}

	public ArrayList getLanguageBonus()
	{
		return languageBonus;
	}

	private void setLanguageBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			getLanguageBonus().add(aTok.nextToken());
		}
	}

	public ArrayList weaponProfAutos()
	{
		return weaponProfAutos;
	}

	private void setWeaponProfAutos(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			weaponProfAutos().add(aTok.nextToken());
		}
	}

	public ArrayList getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	private void setWeaponProfBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			getWeaponProfBonus().add(aTok.nextToken());
		}
	}

	public ArrayList getSpecialAbilities()
	{
		return specialAbilities;
	}

	private void setSpecialAbilties(String abilities)
	{
		final StringTokenizer aTok = new StringTokenizer(abilities, ",", false);
		while (aTok.hasMoreTokens())
		{
			addSpecialAbility(aTok.nextToken());
		}
	}

	private void addSpecialAbility(String abilityName)
	{
		boolean addIt = true;
		for (Iterator e = getSpecialAbilities().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.equals(abilityName))
			{
				addIt = false;
				break;
			}
		}
		if (addIt)
		{
			specialAbilities.add(abilityName);
		}
	}

	public void addSpecialAbilitiesForRace()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		for (Iterator e = getSpecialAbilities().iterator(); e.hasNext();)
		{
			final String thisSa = (String)e.next();
			if (!aPC.getSpecialAbilityList().contains(thisSa))
			{
				aPC.getSpecialAbilityList().add(thisSa);
			}
		}
	}

	public void removeSpecialAbilitiesForRace()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		for (Iterator e = getSpecialAbilities().iterator(); e.hasNext();)
		{
			final String thisSa = (String)e.next();
			if (aPC.getSpecialAbilityList().contains(thisSa))
			{
				aPC.getSpecialAbilityList().remove(thisSa);
			}
		}
	}

	public Integer getStartingAC()
	{
		return startingAC;
	}

	public boolean hasFeat(String featName)
	{
		final StringTokenizer aTok = new StringTokenizer(featList, "|", false);
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
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		final String typeString = aTok.nextToken();
		while (aTok.hasMoreTokens())
		{
			weaponProfs.add(typeString + "|" + aTok.nextToken());
		}
	}

	public void setHitPointList(Integer[] newList)
	{
		hitPointList = newList;
	}

	public int calcHitPoints(int iConMod)
	{
		int total = 0;
		for (int i = 0; i < hitDice && i < hitPointList.length; i++)
			if (hitPointList[i] != null)
			{
				total += hitPointList[i].intValue() + iConMod;
			}
		return total;
	}

	private void setNaturalAttacks(String aString, File sourceFile, int lineNum)
	{
		// first natural weapon is primary, rest are secondary; lets try the format- NATURALATTACKS:primary weapon name,weapon type,num attacks,damage|secondary1 weapon name,weapon type,num attacks,damage|secondary2.....
		// format of these things is exactly as it would be in an equipment lst file
		// Type is of the format Weapon.Natural.Melee.Bludgeoning (eg. a smash)
		// number of attacks is the number of attacks with that weapon at BAB (for primary), or BAB - 5 (for secondary)

		// Currently, this isn't going to work with monk attacks - their unarmed stuff won't be effected.

		naturalWeapons.clear();
		int sizeInt = s_SIZESTRING.lastIndexOf(getSize().charAt(0));
		boolean firstWeapon = true;
		StringTokenizer attackTok = new StringTokenizer(aString, "|", false);
		boolean onlyOne = false;

		if (attackTok.countTokens() == 1 && sizeInt < 8)		// If it has only one natural attack, treat the attack as with a weapon one size larger
		{
			// than the creature, thus it is wielded "two handed" --> 1.5x str bonus
			sizeInt++;
			onlyOne = true;
		}

//		if (Globals.getDebugMode())
//				System.out.println("aString: " + aString);



		char aChar = s_SIZESTRING.charAt(sizeInt);				// This code is going to have problems with 'C' sized creatures, 'cuz they can't have a weapon larger than them

		while (attackTok.hasMoreTokens())
		{
			Equipment anEquip = new Equipment();
			StringTokenizer aTok = new StringTokenizer(attackTok.nextToken(), ",", false);
			String eq = (String)aTok.nextToken() + "\tTYPE:" + (String)aTok.nextToken() + "\tWT:0\tCost:0\tSIZE:" +
				aChar;
			String bString = (String)aTok.nextToken();
			boolean attacksProgress = true;
			if (bString.startsWith("*"))
			{
				bString = bString.substring(1);
				attacksProgress = false;
			}

			Integer bonusAttacks = new Integer(bString);
			bonusAttacks = new Integer(bonusAttacks.intValue() - 1);

			if (bonusAttacks.intValue() > 0)
				eq = eq + "\tBONUS:COMBAT|ATTACKS|" + bonusAttacks.toString();
			eq = eq + "\tDAMAGE:" + (String)aTok.nextToken() +
				"\tCRITRANGE:1\tCRITMULT:x2";		//makes some nasty assumptions, but good for the time being
			//BONUS:COMBAT|ATTACKS|# used instead of ATTACKS: because ATTACKS is not yet/properly implemented.
//		if (Globals.getDebugMode())
//				System.out.println("Eq:\n" + eq);

			anEquip.parseLine(eq, sourceFile, lineNum);
			anEquip.setQty(new Float(1));	//these values need to be locked.
			anEquip.setNumberCarried(new Float(1));
			setWeaponProfAutos(anEquip.getName());
			anEquip.setAttacksProgress(attacksProgress);

			//		anEquip.setIsEquipped(true);	//<-- causes null pointer error
			if (firstWeapon)
			{
				anEquip.setModifiedName("Natural/Primary");
				//			if (onlyOne)
				//				anEquip.setHand(4);
				//			else
				//				anEquip.setHand(1);
			}
			else
			{
				anEquip.setModifiedName("Natural/Secondary");
				//			anEquip.setHand(2);
			}
			anEquip.setOnlyNaturalWeapon(onlyOne);
			firstWeapon = false;
			naturalWeapons.add(anEquip);
		}

	}

	public ArrayList getNaturalWeapons()
	{
		return naturalWeapons;
	}


	public Integer[] getHitPointList()
	{
		return hitPointList;
	}

	public void PCG_adjustHpRolls(int increment)
	{
		if (hitDice == 0)
			return;
		for (int i = 0; i < hitDice; i++)
		{
			Integer roll;
			roll = new Integer(hitPointList[i].intValue() + increment);
			hitPointList[i] = roll;
		}
	}

	/** Note that this code does *not* work like that in PCClass. Is this correct? */
	public void rollHp()
	{
		if (hitDice == 0)
			hitPointList[0] = new Integer(0);
		int hp = 0;
		int pct = Globals.getHpPct();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int min = 1 + aPC.getTotalBonusTo("HD", "MIN", true);
		int max = hitDiceSize + aPC.getTotalBonusTo("HD", "MAX", true);

		hp = max * pct / 100 + aPC.calcStatMod(Globals.CONSTITUTION);
		Integer maxHp = new Integer(hp);
		hitPointList = new Integer[hitDice];
		Random roller = new Random();
		int roll = 0;
		for (int x = 0; x < hitPointList.length; x++)
		{
			roll = Math.abs(roller.nextInt(max - min + 1)) + min;
			hitPointList[x] = new Integer(roll);
		}
		aPC.setCurrentHP(aPC.hitPoints());
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
		aRace.movement = new Integer(getMovement().intValue());
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
//		aRace.alignments = alignments;
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
		aRace.hands = hands;
		aRace.reach = reach;
		aRace.face = face;
		aRace.SR = SR;
		aRace.DR = DR;
		aRace.weaponProfs = (ArrayList)weaponProfs.clone();
		return aRace;
	}

	public int getLangNum()
	{
		return langNum;
	}

	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		final int colMax = colToken.countTokens();
		if (colMax == 0)
		{
			return;
		}
		for (int col = 0; col < colMax; col++)
		{
			final String aString = (String)colToken.nextToken();
			if (super.parseTag(aString))
				continue;
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
					setStatMod(col - 1, Integer.parseInt(aString));
					nonability[col - 1] = false;
				}
			}
			else if (col == 7)
			{
				setFavoredClass(aString);
			}
			else if (col == 8)
			{
				setBonusSkillsPerLevel(Integer.parseInt(aString));
			}
			else if (col == 9)
			{
				setBonusInitialFeats(Integer.parseInt(aString));
			}
			else if (aString.startsWith("AC"))
			{
				startingAC = new Integer(aString.substring(3));
			}
			else if (aString.length() > 5 && aString.substring(0, 3).equals("AGE"))
			{
				setAgeString(aString.substring(4));
			}
			else if (aString.startsWith("AL"))
			{
// Pass into PREALIGN instead
				preReqArrayList.add("PREALIGN:" + aString.substring(3));
//				alignments = aString.substring(3);
			}
// HitDieLock
			else if (aString.startsWith("HITDIE:"))
			{
				setHitDieLock(aString.substring(7));
			}
			else if (aString.startsWith("BAB"))
			{
				BAB = Integer.parseInt(aString.substring(4));
			}
			else if (aString.startsWith("CR"))
			{
				CR = Integer.parseInt(aString.substring(3));
			}
			else if (aString.startsWith("CSKILL"))
			{
				setCSkillList(aString.substring(7));
			}
			else if (aString.startsWith("DEFINE"))
				variableList.add("0|" + aString.substring(7));
			else if (aString.startsWith("DR"))
				DR = aString.substring(3);
			else if (aString.startsWith("FACE"))
				face = aString.substring(5);
			else if (aString.startsWith("FEAT"))
			{
				featList = aString.substring(5);
			}
			else if (aString.startsWith("HANDS"))
				hands = Integer.parseInt(aString.substring(6));
			else if (aString.length() > 5 && aString.substring(0, 6).equals("HEIGHT"))
			{
				setHeightString(aString.substring(7));
			}
			else if (aString.startsWith("HITDICE:"))
			{
				final StringTokenizer hitdice = new StringTokenizer(aString.substring(8), ",");
				if (hitdice.countTokens() != 2)
					JOptionPane.showMessageDialog(null, "Illegal racial hit dice format " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
				else
				{
					hitDice = Integer.parseInt(hitdice.nextToken());
					hitDiceSize = Integer.parseInt(hitdice.nextToken());
				}
			}
			else if (aString.startsWith("HITDICEADVANCEMENT"))
			{
				final StringTokenizer advancement = new StringTokenizer(aString.substring(19), ",");
				hitDiceAdvancement = new int[advancement.countTokens()];
				for (int x = 0; x < hitDiceAdvancement.length; x++)
					hitDiceAdvancement[x] = Integer.parseInt(advancement.nextToken());
			}
			else if (aString.startsWith("INIT"))
			{
				initMod = new Integer(aString.substring(5));
			}
			else if (aString.startsWith("KEY:"))
			{
				setKeyName(aString.substring(4));
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
			else if (aString.startsWith("LEVELADJUSTMENT"))
			{
				levelAdjustment = Integer.parseInt(aString.substring(16));
			}
			else if (aString.length() > 4 && aString.substring(0, 4).equals("MOVE"))
			{
				final StringTokenizer moves = new StringTokenizer(aString.substring(5), ",");
				if (moves.countTokens() == 1)
				{
					setMovement(new Integer(moves.nextToken()));
					movements = new Integer[1];
					movements[0] = getMovement();
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
							setMovement(movements[x]);
						x++;
					}
				}
			}
			else if (aString.startsWith("NATURALATTACKS"))
			{
				// first natural weapon is primary, rest are secondary; lets try the format- NATURALATTACKS:primary weapon name,num attacks,damage|secondary1 weapon name,num attacks,damage|secondary2.....
				// damage will be of the form XdY+Z or XdY-Z to maintain readability of lst files.
				setNaturalAttacks(aString.substring(15), sourceFile, lineNum);
			}
			else if (aString.startsWith("PRE"))
			{
				preReqArrayList.add(aString);
			}
			else if (aString.startsWith("PROF"))
			{
				setWeaponProfs(aString.substring(5));
			}
			else if (aString.startsWith("REACH"))
				reach = Integer.parseInt(aString.substring(6));
			else if (aString.startsWith("SAVES"))   //I know there is already a way to add racial bonuses to saves, but this is for races that give base saves.
			{
				final StringTokenizer saves = new StringTokenizer(aString.substring(6), ",");
				if (saves.countTokens() != 3)
					JOptionPane.showMessageDialog(null, "Illegal number of racial save bonuses " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
				else
				{
					fortSave = Integer.parseInt(saves.nextToken());
					refSave = Integer.parseInt(saves.nextToken());
					willSave = Integer.parseInt(saves.nextToken());
				}
			}
			else if (aString.length() > 4 && aString.substring(0, 4).equals("SIZE"))
			{
				size = aString.substring(5);
			}
			else if (aString.length() > 6 && aString.substring(0, 6).equals("SKILL:"))
			{
				setBonusSkillList(aString.substring(6));
			}
			else if (aString.startsWith("SKILLMULT:"))
			{
				initialSkillMultiplier = Integer.parseInt(aString.substring(10));
			}
			else if (aString.startsWith("SA:"))
			{
				setSpecialAbilties(aString.substring(3));
			}
			else if (aString.startsWith("SR"))
				SR = Integer.parseInt(aString.substring(3));
			else if (aString.startsWith("TEMPLATE:"))
				templates.add(aString.substring(9));
			else if (aString.startsWith("TYPE"))
			{
				type = aString.substring(5);
			}
			else if (aString.startsWith("VFEAT"))
			{
				vFeatList = aString.substring(6);
			}
			else if (aString.length() > 6 && aString.substring(0, 6).equals("VISION"))
			{
				setVision(aString.substring(7));
			}
			else if (aString.startsWith("WEAPONAUTO"))
			{
				setWeaponProfAutos(aString.substring(11));
			}
			else if (aString.startsWith("WEAPONBONUS"))
			{
				setWeaponProfBonus(aString.substring(12));
			}
			else if (aString.length() > 5 && aString.substring(0, 6).equals("WEIGHT"))
			{
				setWeightString(aString.substring(7));
			}
			else
			{
				JOptionPane.showMessageDialog
					(null, "Illegal race info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" +
					aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
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
//	public String getAlignments()
//	{
//		return alignments;
//	}

	/**
	 * Overridden to only consider the race's name.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Race)
		{
			if (((Race)obj).getName().equals(getName()))
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
		return getName().hashCode();
	}
}
