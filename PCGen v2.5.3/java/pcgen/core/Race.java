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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.*;
import pcgen.util.GuiFacade;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Race extends PObject
{
	private int[] statMods = new int[Globals.s_ATTRIBLONG.length];
	private boolean[] nonability = new boolean[Globals.s_ATTRIBLONG.length];
	private String favoredClass = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String raceOutputName = "None";
	private String size = "";
	private Integer movement = new Integer(0);
	private String vision = "Normal";
	private String bonusSkillList = "";
	private ArrayList cSkillList = new ArrayList();
	private ArrayList ccSkillList = new ArrayList();
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
	private String mFeatList = "";
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
	private String type = "Humanoid";
	private String[] movementTypes;
	private Integer[] movements;
	private int[] hitDiceAdvancement;
	private boolean unlimitedAdvancement = false;
	private String HitDieLock = "%/1";
	private int hands = 2;
	private int reach = 5;
	private String face = "5 ft. by 5 ft.";
	private String DR = "";
	private int SR = 0;
	private ArrayList templates = new ArrayList();
	private ArrayList templatesAdded = null;
	private String monsterClass = null;
	private int monsterClassLevels = 0;

	public void addTemplate(String template)
	{
		templates.add(template);
	}

	public Integer getHitPointList(int j)
	{
		return hitPointList[j];
	}

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		hitPointList[aLevel] = iRoll;
	}

	public String getRaceOutputName()
	{
		return raceOutputName;
	}

	public void setRaceOutputName(String raceOutputName)
	{
		this.raceOutputName = raceOutputName;
	}

	public ArrayList getWeaponProfs()
	{
		return weaponProfs;
	}

	public int getInitialSkillMultiplier()
	{
		return initialSkillMultiplier;
	}

	public void setInitialSkillMultiplier(int initialSkillMultiplier)
	{
		this.initialSkillMultiplier = initialSkillMultiplier;
	}

	public Integer getInitMod()
	{
		return initMod;
	}

	public void setInitMod(Integer initMod)
	{
		this.initMod = initMod;
	}

	public String getVFeatList()
	{
		return vFeatList;
	}

	public String getFeatList()
	{
		// This was messing up feats by race for several PC races.
		// so a new tag MFEAT has been added.
		// --- arcady 1/18/2002
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault() && !mFeatList.equals(""))
			return featList + "|" + mFeatList;
		else if (Globals.getCurrentPC() != null)
			return featList;
		else
			return "";
	}

	public void setFeatList(String featList)
	{
		this.featList = featList;
	}

	public void setMFeatList(String mFeatList)
	{
		this.mFeatList = mFeatList;
	}

	public void setVFeatList(String vFeatList)
	{
		this.vFeatList = vFeatList;
	}

	public void addToFeatList(String aString)
	{
		featList += ("|" + aString);
	}

	public ArrayList getCSkillList()
	{
		return cSkillList;
	}

	public ArrayList getCcSkillList()
	{
		return ccSkillList;
	}

	public String toString()
	{
		return name;
	}

	public int getStatMod(int index)
	{
		return statMods[index];
	}

	public void setStatMod(int index, int value)
	{
		statMods[index] = value;
	}

	public void setNonAbility(int index, boolean value)
	{
		nonability[index] = value;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}

	public void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
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

	public void setBonusSkillsPerLevel(int i)
	{
		bonusSkillsPerLevel = i;
	}

	public void setCSkillList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			cSkillList.add(aTok.nextToken());
		}
	}

	public void setCcSkillList(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			ccSkillList.add(aTok.nextToken());
		}
	}

	public int getBonusInitialFeats()
	{
		return bonusInitialFeats;
	}

	public void setBonusInitialFeats(int i)
	{
		bonusInitialFeats = i;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public Integer getMovement()
	{
		return movement;
	}

	public void setMovement(Integer anInt)
	{
		movement = new Integer(anInt.toString());
	}

	public String getVision()
	{
		return vision;
	}

	public void setVision(String aString)
	{
		vision = aString;
	}

	public String getBonusSkillList()
	{
		return bonusSkillList;
	}

	public void setBonusSkillList(String aString)
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
			final int anInt = Integer.parseInt(aTok.nextToken());
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
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
			return BAB;
		else
			return 0;
	}

	public void setBAB(int newBAB)
	{
		BAB = newBAB;
	}

	public int getFortSave()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
			return fortSave;
		else
			return 0;
	}

	public void setFortSave(int newFortSave)
	{
		fortSave = newFortSave;
	}

	public int getRefSave()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
			return refSave;
		else
			return 0;
	}

	public void setRefSave(int newRefSave)
	{
		refSave = newRefSave;
	}

	public int getWillSave()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
			return willSave;
		else
			return 0;
	}

	public void setWillSave(int newWillSave)
	{
		willSave = newWillSave;
	}

	public boolean isNonability(int ability)
	{
		if (ability < 0 || ability >= Globals.s_ATTRIBLONG.length)
			return true;
		return nonability[ability];
	}

	public int hitDice()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
			return hitDice;
		else
			return 0;
	}

	public void setHitDice(int newHitDice)
	{
		if (newHitDice < 0)
		{
			GuiFacade.showMessageDialog(null, "Invalid number of hit dice in race " + name, "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		hitDice = newHitDice;
	}

	public int getHitDiceSize()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
			return hitDiceSize;
		else
			return 0;
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
		Globals.getRaceTypes().add(type);
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

	public void setHitDiceAdvancement(int index, int value)
	{
		hitDiceAdvancement[index] = value;
	}

	public int sizesAdvanced(int HD)
	{
		if (hitDiceAdvancement != null)
		{
			for (int x = 0; x < hitDiceAdvancement.length; x++)
			{
				if (HD <= hitDiceAdvancement[x] || hitDiceAdvancement[x] == -1)
					return x;
			}
		}
		return 0;
	}

	public int maxHitDiceAdvancement()
	{
		if ((hitDiceAdvancement != null) && (hitDiceAdvancement.length >= 1))
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

	public void setMovementTypes(String[] movementTypes)
	{
		this.movementTypes = movementTypes;
	}

	/** NOTE: Returns an empty string if the race has no movementtypes defined. */
	public String getMovementType(int x)
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

	public void setMovements(Integer[] movements)
	{
		this.movements = movements;
	}

	/** NOTE: Returns 0 if the race has no movements defined. */
	public Integer getMovement(int x)
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

//	// This is used to let external objects adjust movement. Like Templates.
//	public boolean setMoveRates(String moveType, int moveRatei, int moveFlag)
//	{
//
//		if (moveType.equals("ALL"))
//		{
//			if (moveFlag == 0)
//			{ // set all types of movement to moveRate
//				Integer moveRate = new Integer(moveRatei);
//				for (int i = 0; i < movements.length; i++)
//				{
//					movements[i] = moveRate;
//				}
//			}
//			else
//			{ // add moveRate to all types of movement.
//				for (int i = 0; i < movements.length; i++)
//				{
//					Integer moveRate = new Integer(moveRatei + movements[i].intValue());
//					movements[i] = moveRate;
//				}
//			}
//			return true;
//		}
//		else
//		{
//			if (moveFlag == 0)
//			{ // set movement to moveRate
//				Integer moveRate = new Integer(moveRatei);
//				for (int i = 0; i < movements.length; i++)
//				{
//					if (moveType.equals(movementTypes[i]))
//					{
//						movements[i] = moveRate;
//						return true;
//					}
//				}
//				String[] movementTypesTemp = movementTypes;
//				Integer[] movementsTemp = movements;
//
//				movements = new Integer[movementsTemp.length + 1];
//				movementTypes = new String[movementTypesTemp.length + 1];
//
//				for (int i = 0; i < movementsTemp.length; i++)
//				{
//					movements[i] = movementsTemp[i];
//					movementTypes[i] = movementTypesTemp[i];
//				}
//				movements[movementsTemp.length] = moveRate;
//				movementTypes[movementTypesTemp.length] = moveType;
//
//				return true;
//			}
//			else if (moveFlag == 1)
//			{ // add moveRate to movement.
//				for (int i = 0; i < movements.length; i++)
//				{
//					Integer moveRate = new Integer(moveRatei + movements[i].intValue());
//					if (moveType.equals(movementTypes[i]))
//					{
//						movements[i] = moveRate;
//						return true;
//					}
//				}
//			}
//			else
//			{
//				// set all to base rate, then add local rates.
//				Integer moveRate = new Integer(moveRatei + movements[0].intValue());
//				// for existing types of movement:
//				for (int i = 0; i < movements.length; i++)
//				{
//					if (moveType.equals(movementTypes[i]))
//					{
//						movements[i] = moveRate;
//						return true;
//					}
//				}
//				// if it's a new type of movement:
//				String[] movementTypesTemp = movementTypes;
//				Integer[] movementsTemp = movements;
//
//				movements = new Integer[movementsTemp.length + 1];
//				movementTypes = new String[movementTypesTemp.length + 1];
//
//				for (int i = 0; i < movementsTemp.length; i++)
//				{
//					movements[i] = movementsTemp[i];
//					movementTypes[i] = movementTypesTemp[i];
//				}
//				movements[movementsTemp.length] = moveRate;
//				movementTypes[movementTypesTemp.length] = moveType;
//
//				return true;
//
//			}
//		}
//		return false;
//	}

	public int getHands()
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

	public ArrayList getTemplates(boolean isImporting)
	{
		ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		if (!isImporting)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				String templateName = (String)e.next();
				if (templateName.startsWith("CHOOSE:"))
				{
					for (; ;)
					{
						final String newTemplate = Globals.chooseFromList("Template Choice (" + getName() + ")", templateName.substring(7), null, 1);
						if (newTemplate != null)
						{
							templateName = newTemplate;
							break;
						}
					}
				}
				if (templateName != null)
				{
					newTemplates.add(templateName);
					templatesAdded.add(templateName);
				}
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

	public String getMonsterClass()
	{
		if (Globals.getCurrentPC() != null && !Globals.getCurrentPC().isMonsterDefault())
			return monsterClass;
		else
			return null;
	}

	public void setMonsterClass(String string)
	{
		monsterClass = string;
	}

	public int getMonsterClassLevels()
	{
		if (Globals.getCurrentPC() != null && !Globals.getCurrentPC().isMonsterDefault())
			return monsterClassLevels;
		else
			return 0;
	}

	public void setMonsterClassLevels(int num)
	{
		monsterClassLevels = num;
	}

	public String getAgeString()
	{
		return ageString;
	}

	public void setAgeString(String aString)
	{
		ageString = aString;
	}

	public String getHeightString()
	{
		return heightString;
	}

	public void setHeightString(String aString)
	{
		heightString = aString;
	}

	public String getWeightString()
	{
		return weightString;
	}

	public void setWeightString(String aString)
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
		int total = 0;
		int randomHeight = 0;
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
						final int bonus = anInt.parseInt(aString);
						randomHeight = RollingMethods.roll(diceNum, diceSide);
						total = min + randomHeight + bonus;
						aPC.setHeight(total);
						if (Globals.isHackMasterMode())
						{
							randomHeight = 1;
						}
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
		if (randomHeight > 0)
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
							total = min + (RollingMethods.roll(diceNum, diceSide) * randomHeight);
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
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(ageString, ":", false);

		// For XML migration, there is a separate entry for each class,
		// and the classes are attributes, so users can define new ones
		// but still give them age features.
		int min = 0;
		int diceNum = 0;
		int diceSide = 0;
		int total = 0;
		if (aTok.countTokens() < 2 * i + 3)
		{
			if (!aPC.isImporting())
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
		total = min + RollingMethods.roll(diceNum, diceSide);
		aPC.setAge(total);
	}

	/** return the array of String Auto Language names */
	public ArrayList getLanguageAutos()
	{
		return languageAutos;
	}

	/** return the array of Language Auto Languages */
	public ArrayList getAutoLanguages()
	{
		ArrayList aList = new ArrayList();
		for (Iterator i = languageAutos.iterator(); i.hasNext();)
		{
			Language aLang = Globals.getLanguageNamed(i.next().toString());
			if (aLang != null)
				aList.add(aLang);
		}
		return aList;
	}

	public void setLanguageAutos(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			languageAutos.add(aTok.nextToken());
		}
	}

	public ArrayList getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLanguageBonus(String aString)
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

	public void setWeaponProfAutos(String aString)
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

	public void setWeaponProfBonus(String aString)
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

	public void setSpecialAbilties(String abilities)
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

	public void setStartingAC(Integer startingAC)
	{
		this.startingAC = startingAC;
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

	public void setWeaponProfs(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		final String typeString = aTok.nextToken();
		final String prefix = typeString + "|";
		while (aTok.hasMoreTokens())
		{
			weaponProfs.add(prefix + aTok.nextToken());
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
				int iHp = hitPointList[i].intValue() + iConMod;
				if (iHp < 1)
				{
					iHp = 1;
				}
				total += iHp;
			}
		return total;
	}

	public ArrayList getNaturalWeapons()
	{
		return naturalWeapons;
	}

	public void setNaturalWeapons(List naturalWeapons)
	{
		this.naturalWeapons = (ArrayList)naturalWeapons;
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
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int min = 1 + aPC.getTotalBonusTo("HD", "MIN", true);
		int max = hitDiceSize + aPC.getTotalBonusTo("HD", "MAX", true);

		hitPointList = new Integer[hitDice];
		int roll = 0;
		for (int x = 0; x < hitPointList.length; x++)
		{
			roll = Math.abs(Globals.getRandomInt(max - min + 1)) + min;
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
		aRace.ccSkillList = (ArrayList)ccSkillList.clone();
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
//		aRace.type = type;
		aRace.hitDiceAdvancement = hitDiceAdvancement;
		aRace.hands = hands;
		aRace.reach = reach;
		aRace.face = face;
		aRace.SR = SR;
		aRace.DR = DR;
		aRace.weaponProfs = (ArrayList)weaponProfs.clone();
//		aRace.naturalWeapons = (ArrayList)naturalWeapons.clone();
		return aRace;
	}

	public int getLangNum()
	{
		return langNum;
	}

	public void setLangNum(int langNum)
	{
		this.langNum = langNum;
	}

	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	public boolean isAdvancementUnlimited()
	{
		return unlimitedAdvancement;
	}

	public void setAdvancementUnlimited(boolean unlimitedAdvancement)
	{
		this.unlimitedAdvancement = unlimitedAdvancement;
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
