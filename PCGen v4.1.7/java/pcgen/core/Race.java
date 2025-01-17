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
 *
 * $Id: Race.java,v 1.1 2006/02/21 00:57:42 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Race extends PObject
{
	/** Adds one chosen language. */
	void chooseLanguageAutos(boolean flag)
	{
		if (!flag && !"".equals(chooseLanguageAutos))
		{
			final StringTokenizer tokens = new StringTokenizer(chooseLanguageAutos, "|", false);
			List selectedList = new ArrayList(); // selected list of choices
			final PlayerCharacter aPC = Globals.getCurrentPC();

			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setPool(1);
			c.setPoolFlag(false);
			c.setTitle("Pick a Language: ");
			SortedSet list = new TreeSet();
			while (tokens.hasMoreTokens())
			{
				list.add(tokens.nextToken());
			}
			list = Globals.extractLanguageList(list);
			c.setAvailableList(new ArrayList(list));
			c.show();
			selectedList = c.getSelectedList();
			aPC.addFreeLanguage((String) selectedList.get(0));
		}
	}

	private String favoredClass = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String displayName = "None";
	private String size = "";
	private Integer movement = new Integer(0);
	private String bonusSkillList = "";
	private String ageString = "";
	private String heightString = "";
	private String weightString = "";
	private String chooseLanguageAutos = "";
	private ArrayList languageBonus = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
	private ArrayList naturalWeapons = new ArrayList();
	private String featList = "";
	private String mFeatList = "";
	private String vFeatList = "";
	private Integer startingAC = new Integer(10);
	private Integer naturalAC = new Integer(0);
	private Integer initMod = new Integer(0);
	private int langNum = 0;
	private int initialSkillMultiplier = 4;
	private ArrayList weaponProfs = new ArrayList();
	private String levelAdjustment = "0"; //now a string so that we can handle formulae
	private int CR = 0;
	private int BAB = 0;
	private int hitDice = 0;
	private int hitDiceSize = 0;
	private Integer[] hitPointList;
	private String type = "Humanoid";
	private String[] movementTypes;
	private Integer[] movements;
	private int[] hitDiceAdvancement;
	private boolean unlimitedAdvancement = false;
	private String HitDieLock = "";
	private int hands = 2;
	private int legs = 2;
	private int reach = 5;
	private String face = "5 ft. by 5 ft.";
	private ArrayList templates = new ArrayList();
	private ArrayList templatesAdded = null;
	private String monsterClass = null;
	private int monsterClassLevels = 0;

	{
		vision = new HashMap();
		vision.put("Normal", "0");
	}

	public void addTemplate(String template)
	{
		templates.add(template);
	}

	public Integer getHitPointList(int j)
	{
		if (hitPointList != null)
		{
			return hitPointList[j];
		}
		return new Integer(0);
	}

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		if (hitPointList != null)
		{
			hitPointList[aLevel] = iRoll;
		}
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	ArrayList getWeaponProfs()
	{
		return weaponProfs;
	}

	int getInitialSkillMultiplier()
	{
		return initialSkillMultiplier;
	}

	public void setInitialSkillMultiplier(int initialSkillMultiplier)
	{
		this.initialSkillMultiplier = initialSkillMultiplier;
	}

	public void setInitMod(Integer initMod)
	{
		this.initMod = initMod;
	}

	String getVFeatList()
	{
		return vFeatList;
	}

	String getFeatList()
	{
		// This was messing up feats by race for several PC races.
		// so a new tag MFEAT has been added.
		// --- arcady 1/18/2002
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault() && !"".equals(mFeatList))
		{
			return featList + "|" + mFeatList;
		}
		else if (Globals.getCurrentPC() != null)
		{
			return featList;
		}
		else
		{
			return "";
		}
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

	public String toString()
	{
		return name;
	}

	public int getStatMod(int statIdx)
	{
		final ArrayList statList = Globals.getStatList();
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return 0;
		}

		final String aStat = "STAT|" + ((PCStat) statList.get(statIdx)).getAbb() + "|";
		for (Iterator e = getBonusList().iterator(); e.hasNext();)
		{
			final String bonusString = (String) e.next();
			if (bonusString.startsWith(aStat))
			{
				return Delta.decode(bonusString.substring(aStat.length())).intValue();
			}
		}
		return 0;
	}

	public String getFavoredClass()
	{
		return favoredClass;
	}

	public void setFavoredClass(String newClass)
	{
		favoredClass = newClass;
	}

	boolean canBeAlignment(String aString)
	{
		if (getPreReqCount() != 0)
		{
			StringTokenizer aTok = null;
			String aType = null;
			String aList = null;
			for (int e = 0; e < getPreReqCount(); e++)
			{
				aTok = new StringTokenizer(getPreReq(e), ":", false);
				aType = aTok.nextToken();
				aList = aTok.nextToken();
				if ("PREALIGN".equals(aType))
				{
					return aList.lastIndexOf(aString) > -1;
				}
			}
		}
		return true;
	}

	int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setBonusSkillsPerLevel(int i)
	{
		bonusSkillsPerLevel = i;
	}

	int getBonusInitialFeats()
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

	private String getBonusSkillList()
	{
		return bonusSkillList;
	}

	public void setBonusSkillList(String aString)
	{
		bonusSkillList = aString;
	}

	int bonusForSkill(String skillName)
	{
		if (getBonusSkillList().length() == 0)
		{
			return 0;
		}
		final StringTokenizer aTok = new StringTokenizer(bonusSkillList, "=", false);
		while (aTok.hasMoreTokens())
		{
			final String skillList = aTok.nextToken();
			final int anInt = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(skillList, ",", false);
			while (bTok.hasMoreTokens())
			{
				final String aSkill = bTok.nextToken();
				if (aSkill.equals(skillName))
					return anInt;
			}
		}
		return 0;
	}

	public int getLevelAdjustment()
	{
		int lvlAdjust = 0;

		//if there's a current PC, go ahead and evaluate the formula
		if (Globals.getCurrentPC() != null)
		{
			return Globals.getCurrentPC().getVariableValue(levelAdjustment, "").intValue();
		}
		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			lvlAdjust = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			lvlAdjust = 0;
		}
		return lvlAdjust;
	}

	public void setLevelAdjustment(String newLevelAdjustment)
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

	int getBAB()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
		{
			return BAB;
		}
		else
		{
			return 0;
		}
	}

	public void setBAB(int newBAB)
	{
		BAB = newBAB;
	}

	public boolean isNonability(int statIdx)
	{
		final ArrayList statList = Globals.getStatList();
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + ((PCStat) statList.get(statIdx)).getAbb() + "|10";
		for (int i = 0, x = getVariableCount(); i < x; ++i)
		{
			final String varString = getVariable(i);
			if (varString.endsWith(aStat))
			{
				return true;
			}
		}
		return false;
	}

	public int hitDice()
	{
		if (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault())
		{
			return hitDice;
		}
		else
		{
			return 0;
		}
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
		{
			return hitDiceSize;
		}
		else
		{
			return 0;
		}
	}

	public void setHitDiceSize(int newHitDiceSize)
	{
		hitDiceSize = newHitDiceSize;
	}

	public String getType()
	{
		return type;
	}

	public boolean isType(final String argType)
	{
		return type.equalsIgnoreCase(argType);
	}

	public void setType(String newType)
	{
		type = newType;
		Globals.getRaceTypes().add(type);
	}

	String getHitDieLock()
	{
		return HitDieLock;
	}

	public void setHitDieLock(String newHitDieLock)
	{
		HitDieLock = newHitDieLock;
	}

	public void setHitDiceAdvancement(int[] advancement)
	{
		hitDiceAdvancement = advancement;
	}

	int sizesAdvanced(int HD)
	{
		if (hitDiceAdvancement != null)
		{
			for (int x = 0; x < hitDiceAdvancement.length; x++)
			{
				if (HD <= hitDiceAdvancement[x] || hitDiceAdvancement[x] == -1)
				{
					return x;
				}
			}
		}
		return 0;
	}

	int maxHitDiceAdvancement()
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

	public boolean hasAdvancement()
	{
		return hitDiceAdvancement != null;
	}

	String[] getMovementTypes()
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

	public String getDisplayVision()
	{
		if (vision == null)
		{
			return "";
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return "";
		}
		final StringBuffer vis = new StringBuffer(25);
		for (Iterator i = vision.keySet().iterator(); i.hasNext();)
		{
			final String aKey = i.next().toString();
			final String aVal = vision.get(aKey).toString();
			final int val = aPC.getVariableValue(aVal, "").intValue();
			if (vis.length() > 0)
			{
				vis.append(';');
			}
			vis.append(aKey);
			if (val != 0)
			{
				vis.append(" (").append(val).append("')");
			}
		}
		return vis.toString();
	}

	public void setLegs(int argLegs)
	{
		legs = argLegs;
	}

	int getLegs()
	{
		return legs;
	}

	int getHands()
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

	protected int getSR()
	{
		int intSR = 0;

		//if there's a current PC, go ahead and evaluate the formula
		if (SR != null && Globals.getCurrentPC() != null)
			return Globals.getCurrentPC().getVariableValue(SR, "").intValue();
		//otherwise do what we can
		try
		{
			//try to convert the string to an int to return
			intSR = Integer.parseInt(levelAdjustment);
		}
		catch (NumberFormatException nfe)
		{
			//if the parseInt failed then just punt... return 0
			intSR = 0;
		}
		return intSR;
	}

	ArrayList getTemplates(boolean isImporting)
	{
		final ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		if (!isImporting)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				String templateName = (String) e.next();
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

	ArrayList templatesAdded()
	{
		if (templatesAdded == null)
		{
			return new ArrayList();
		}
		return templatesAdded;
	}

	String getMonsterClass()
	{
		return getMonsterClass(true);
	}

	public String getMonsterClass(final boolean checkPC)
	{
		if (!checkPC || ((Globals.getCurrentPC() != null) && !Globals.getCurrentPC().isMonsterDefault()))
		{
			return monsterClass;
		}
		else
		{
			return null;
		}
	}

	public void setMonsterClass(String string)
	{
		monsterClass = string;
	}

	public int getMonsterClassLevels()
	{
		if (Globals.getCurrentPC() != null && !Globals.getCurrentPC().isMonsterDefault())
		{
			return monsterClassLevels;
		}
		else
		{
			return 0;
		}
	}

	public void setMonsterClassLevels(int num)
	{
		monsterClassLevels = num;
	}

	public void setAgeString(String aString)
	{
		ageString = aString;
	}

	public void setHeightString(String aString)
	{
		heightString = aString;
	}

	public void setWeightString(String aString)
	{
		weightString = aString;
	}

	static void rollAgeForAgeSet()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		BioSet.randomize("AGE");

/*		String bString = aPC.findTemplateAge();

		if (bString.equals(Constants.s_NONE))
			bString = ageString;

		final StringTokenizer aTok = new StringTokenizer(bString, ":", false);

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
				Globals.errorPrint("Not enough parameters in " + getName() + "'s definition file to determine age. index=" + i);
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
*/
	}

	public void setChooseLanguageAutos(String chooseLanguageAutos)
	{
		this.chooseLanguageAutos = chooseLanguageAutos;
	}

	ArrayList getLanguageBonus()
	{
		return languageBonus;
	}

	public void setLanguageBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{
			String token = aTok.nextToken();
			if (".CLEAR".equals(token))
			{
				getLanguageBonus().clear();
			}
			else
			{
				getLanguageBonus().add(token);
			}
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

	Integer getStartingAC()
	{
		return startingAC;
	}

	public void setStartingAC(Integer startingAC)
	{
		this.startingAC = startingAC;
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

	int calcHitPoints(int iConMod)
	{
		int total = 0;
		if (hitPointList != null)
		{
			for (int i = 0; (i < hitDice) && (i < hitPointList.length); i++)
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
		}
		return total;
	}

	public ArrayList getNaturalWeapons()
	{
		return naturalWeapons;
	}

	public void setNaturalWeapons(List naturalWeapons)
	{
		this.naturalWeapons = (ArrayList) naturalWeapons;
	}

	public Integer[] getHitPointList()
	{
		return hitPointList;
	}

	/** Note that this code does *not* work like that in PCClass. Is this correct? */
	public void rollHP()
	{
		if (hitDice == 0)
		{
			hitPointList[0] = new Integer(0);
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN", true);
		final int max = hitDiceSize + (int) aPC.getTotalBonusTo("HD", "MAX", true);

		hitPointList = new Integer[hitDice];
		int roll = 0;
/////////////////////////////////////////////////
// Yanked for WotC compliance
		//
		// Generate the list of valid rolls
		//
		final Object[] rollChoices = new Object[max - min + 2];
		rollChoices[0] = Constants.s_NONESELECTED;
		for (int i = min; i <= max; ++i)
		{
			rollChoices[i - min + 1] = new Integer(i);
		}

		final int hpRollMethod = SettingsHandler.getHPRollMethod();
/////////////////////////////////////////////////
		for (int x = 0; x < hitPointList.length; x++)
		{
/////////////////////////////////////////////////
// Yanked for WotC compliance
//			roll = Math.abs(Globals.getRandomInt(max - min + 1)) + min;
			switch (hpRollMethod)
			{
				default:
					for (; ;)
					{
						Object selectedValue = JOptionPane.showInputDialog(
							null,
							"Randomly generate a number between " + min + " and " + max + "." + Constants.s_LINE_SEP + "Select it from the box below.",
							Globals.getGameModeHPAbbrev() + " for " + Utility.ordinal(x + 1) + " hit die of " + getName(),
							JOptionPane.INFORMATION_MESSAGE,
							null,
							rollChoices,
							null /*rollChoices[(min + max - 1) / 2]*/);

						if ((selectedValue != null) && (selectedValue instanceof Integer))
						{
							roll = ((Integer) selectedValue).intValue();
							break;
						}
					}
					break;

				case Constants.s_HP_AUTOMAX:
					roll = max;
					break;
			}
/////////////////////////////////////////////////
			hitPointList[x] = new Integer(roll);
		}
		aPC.setCurrentHP(aPC.hitPoints());
	}

	public Object clone()
	{
		Race aRace = null;
		try
		{
			aRace = (Race) super.clone();
			aRace.favoredClass = favoredClass;
			aRace.bonusSkillsPerLevel = bonusSkillsPerLevel;
			aRace.bonusInitialFeats = bonusInitialFeats;
			aRace.size = size;
			aRace.movement = new Integer(getMovement().intValue());
			aRace.bonusSkillList = bonusSkillList;
			aRace.ageString = ageString;
			aRace.heightString = heightString;
			aRace.weightString = weightString;
			aRace.languageBonus = (ArrayList) languageBonus.clone();
			aRace.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
			aRace.featList = featList;
			aRace.vFeatList = vFeatList;
			aRace.startingAC = new Integer(startingAC.intValue());
			aRace.naturalAC = new Integer(naturalAC.intValue());
			aRace.initMod = new Integer(initMod.intValue());
			aRace.langNum = langNum;
			aRace.initialSkillMultiplier = initialSkillMultiplier;
			aRace.isSpecified = isSpecified;
			aRace.visible = visible;
			aRace.levelAdjustment = levelAdjustment;
			aRace.CR = CR;
			aRace.BAB = BAB;
			aRace.hitDice = hitDice;
			aRace.hitDiceSize = hitDiceSize;
			aRace.hitPointList = hitPointList;
			aRace.hitDiceAdvancement = hitDiceAdvancement;
			aRace.hands = hands;
			aRace.reach = reach;
			aRace.face = face;
			aRace.SR = SR;
			aRace.DR = DR;
			aRace.weaponProfs = (ArrayList) weaponProfs.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return aRace;
		}
	}

	int getLangNum()
	{
		return langNum;
	}

	public void setLangNum(int langNum)
	{
		this.langNum = langNum;
	}

	public boolean isAdvancementUnlimited()
	{
		return unlimitedAdvancement;
	}

	public void setAdvancementUnlimited(boolean unlimitedAdvancement)
	{
		this.unlimitedAdvancement = unlimitedAdvancement;
	}

	/**
	 * Overridden to only consider the race's name.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Race)
		{
			if (((Race) obj).getName().equals(getName()))
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
