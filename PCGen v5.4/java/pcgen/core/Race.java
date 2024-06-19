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
 * $Id: Race.java,v 1.1 2006/02/21 01:18:39 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserInterface;
import pcgen.gui.utils.GuiFacade;

/**
 * <code>Race</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Race extends PObject
{

	private String favoredClass = "";
	private int bonusSkillsPerLevel = 0;
	private int bonusInitialFeats = 0;
	private String displayName = "None";
	private String size = "";
	private String bonusSkillList = "";
	private String ageString = "";
	private String heightString = "";
	private String weightString = "";
	private String chooseLanguageAutos = "";
	private ArrayList languageBonus = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
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
	private HashMap hitPointMap = new HashMap();
	//private String type = "Humanoid";
	private int[] hitDiceAdvancement;
	private boolean unlimitedAdvancement = false;
	private String HitDieLock = "";
	private int hands = 2;
	private int legs = 2;
	private int reach = 5;
	//private String face = "5 ft. by 5 ft.";
	private String face = "";
	private List templates = new ArrayList();
	private List templatesAdded = null;
	private String monsterClass = null;
	private int monsterClassLevels = 0;
	private ArrayList monCSkillList = null;
	private ArrayList monCCSkillList = null;

	{
		//setTypeInfo("Humanoid");
		vision = new HashMap();
		vision.put("Normal", "0");
	}

	public void addTemplate(String template)
	{
		if (".CLEAR".equals(template))
		{
			templates.clear();
		}
		else
		{
			templates.add(template);
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

	public int getInitialSkillMultiplier()
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

	public String getVFeatList()
	{
		return vFeatList;
	}

	public String getFeatList()
	{
		return getFeatList(true);
	}

	public String getFeatList(final boolean checkPC)
	{
		// This was messing up feats by race for several PC races.
		// so a new tag MFEAT has been added.
		// --- arcady 1/18/2002
		if (checkPC && Globals.getCurrentPC() != null &&
			Globals.getCurrentPC().isMonsterDefault() && !"".equals(mFeatList))
		{
			return featList + "|" + mFeatList;
		}
		else if (!checkPC || Globals.getCurrentPC() != null)
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

	public String getMFeatList()
	{
		return mFeatList;
	}

	public void setMFeatList(String mFeatList)
	{
		this.mFeatList = mFeatList;
	}

	public void setVFeatList(String vFeatList)
	{
		this.vFeatList = vFeatList;
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
			StringTokenizer aTok;
			String aType;
			String aList;
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

	public int getBonusSkillsPerLevel()
	{
		return bonusSkillsPerLevel;
	}

	public void setBonusSkillsPerLevel(int i)
	{
		bonusSkillsPerLevel = i;
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
		final StringTokenizer aTok = new StringTokenizer(bonusSkillList, "=");
		while (aTok.hasMoreTokens())
		{
			final String skillList = aTok.nextToken();
			final int anInt = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(skillList, ",", false);
			while (bTok.hasMoreTokens())
			{
				final String aSkill = bTok.nextToken();
				if (aSkill.equals(skillName))
				{
					return anInt;
				}
			}
		}
		return 0;
	}

	public int getLevelAdjustment()
	{
		int lvlAdjust;

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

	public String getLevelAdjustmentFormula()
	{
		return levelAdjustment;
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
			// "BAB" not being used on races any more; instead using a BONUS tag.
			// This will fix a bug this causes for default monsters.  Bug #647163
			// sage_sam 03 Dec 2002
			if (BAB == 0)
			{
				BAB = (int) bonusTo("COMBAT", "BAB");
			}
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

	public boolean isNonAbility(int statIdx)
	{
		final List statList = SystemCollections.getUnmodifiableStatList();
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return true;
		}

		final String aStat = "|LOCK." + ((PCStat) statList.get(statIdx)).getAbb() + "|10";
		for (int i = 0, x = getVariableCount(); i < x; ++i)
		{
			final String varString = getVariableDefinition(i);
			if (varString.endsWith(aStat))
			{
				return true;
			}
		}
		return false;
	}

	public int hitDice()
	{
		return hitDice(true);
	}

	public int hitDice(final boolean checkPC)
	{
		if (!checkPC || (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault()))
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
			GuiFacade.showMessageDialog(null, "Invalid number of hit dice in race " + name, "PCGen", GuiFacade.ERROR_MESSAGE);
			return;
		}
		hitDice = newHitDice;
	}

	public int getHitDiceSize()
	{
		return getHitDiceSize(true);
	}

	public int getHitDiceSize(final boolean checkPC)
	{
		if (!checkPC || (Globals.getCurrentPC() != null && Globals.getCurrentPC().isMonsterDefault()))
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

	//public String getType()
	//{
	//	return type;
	//}

	//public boolean isType(final String argType)
	//{
	//	return type.equalsIgnoreCase(argType);
	//}

	protected void doGlobalUpdate(final String aString)
	{
		Globals.getRaceTypes().add(aString);
	}

	//public void setType(String newType)
	//{
	//	type = newType;
	//	Globals.getRaceTypes().add(type);
	//}

	String getHitDieLock()
	{
		return HitDieLock;
	}

	public void setHitDieLock(String newHitDieLock)
	{
		HitDieLock = newHitDieLock;
	}

//	public int[] getHitDiceAdvancement()
//	{
//		return hitDiceAdvancement;
//	}

	public int getHitDiceAdvancement(int index)
	{
		return hitDiceAdvancement[index];
	}

	public int getNumberOfHitDiceAdvancements()
	{
		return hitDiceAdvancement != null ? hitDiceAdvancement.length : 0;
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

	public Map getVisionTable()
	{
		return vision;
	}

	public void setVisionTable(Map visionTable)
	{
		vision = visionTable;
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

	public int getLegs()
	{
		return legs;
	}

	/**
	 * Made public for use on equipping tab -- bug 586332
	 * sage_sam, 22 Nov 2002
	 */
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

	protected int getSR()
	{
		int intSR;

		//if there's a current PC, go ahead and evaluate the formula
		if (SR != null && Globals.getCurrentPC() != null)
		{
			return Globals.getCurrentPC().getVariableValue(SR, "").intValue();
		}
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

	/**
	 * Method getTemplateList. Returns an array list containing the raw
	 * templates granted by this race. This includes CHOOSE: strings
	 * which list templates a user will be asked to choose from.
	 *
	 * @return ArrayList of granted templates
	 */
	public List getTemplateList()
	{
		return templates;
	}

	List getTemplates(boolean isImporting)
	{
		final List newTemplates = new ArrayList();
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
				if (templateName.length() != 0)
				{
					newTemplates.add(templateName);
					templatesAdded.add(templateName);
				}
			}
		}
		return newTemplates;
	}

	List templatesAdded()
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
		return getMonsterClassLevels(true);
	}

	public int getMonsterClassLevels(final boolean checkPC)
	{
		if (!checkPC || Globals.getCurrentPC() != null && !Globals.getCurrentPC().isMonsterDefault())
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

	/** Adds one chosen language.
	 * Identical method in PCTemplate.java. Refactor. XXX
	 */
	void chooseLanguageAutos(boolean flag)
	{
		if (!flag && !"".equals(chooseLanguageAutos))
		{
			final StringTokenizer tokens = new StringTokenizer(chooseLanguageAutos, "|", false);
			List selectedList; // selected list of choices
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
			list = Globals.extractLanguageListNames(list);
			c.setAvailableList(new ArrayList(list));
			c.show();
			selectedList = c.getSelectedList();
			if ((selectedList != null) && (selectedList.size() != 0))
			{
				aPC.addFreeLanguage((String) selectedList.get(0));
			}
		}
	}

	public String getChooseLanguageAutos()
	{
		return this.chooseLanguageAutos;
	}

	public void setChooseLanguageAutos(String chooseLanguageAutos)
	{
		this.chooseLanguageAutos = chooseLanguageAutos;
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
			String token = aTok.nextToken();
			if (".CLEAR".equals(token))
			{
				getLanguageBonus().clear();
			}
			else
			{
				final Language aLang = Globals.getLanguageNamed(token);
				if (aLang != null)
				{
					getLanguageBonus().add(aLang);
				}
			}
		}
	}

	public ArrayList getWeaponProfBonus()
	{
		return weaponProfBonus;
	}

	public void setWeaponProfBonus(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		while (aTok.hasMoreTokens())
		{
			getWeaponProfBonus().add(aTok.nextToken());
		}
	}

	public void setStartingAC(Integer anInt)
	{
		startingAC = anInt;
	}

	public void setWeaponProfs(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		final String typeString = aTok.nextToken();
		final String prefix = typeString + "|";
		while (aTok.hasMoreTokens())
		{
			weaponProfs.add(prefix + aTok.nextToken());
		}
	}

	public Integer getHitPoint(int j)
	{
		Integer aHP = (Integer) hitPointMap.get(Integer.toString(j));
		if (aHP == null)
		{
			return new Integer(0);
		}
		return aHP;
	}

	public int getHitPointMapSize()
	{
		return hitPointMap.size();
	}

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		hitPointMap.put(Integer.toString(aLevel), iRoll);
	}

	public void setHitPointMap(HashMap newMap)
	{
		hitPointMap.clear();
		hitPointMap.putAll(newMap);
	}

	/**
	 * Used by GMGen.
	 * TODO: Remove for version 5.4
	 *
	 *@param hps Array of Hit Points
	 *@deprecated this methos has been replaced by {@link #setHitPointMap}
	 */
	public void setHitPointList(Integer[] hps)
	{
		hitPointMap.clear();
		for(int i = 0; i < hps.length; i++)
		{
			setHitPoint(i, hps[i]);
		}
	}

	int calcHitPoints(int iConMod)
	{
		int total = 0;
		for (int i = 0; i <= hitDice; i++)
		{
			if (getHitPoint(i).intValue() > 0)
			{
				int iHp = getHitPoint(i).intValue() + iConMod;
				if (iHp < 1)
				{
					iHp = 1;
				}
				total += iHp;
			}
		}
		return total;
	}

	/**
	 * TODO: Note that this code does *not* work like that in PCClass
	 * Does it need to be?
	 **/
	public void rollHP()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();

		if (!aPC.isImporting())
		{
			final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN");
			final int max = hitDiceSize + (int) aPC.getTotalBonusTo("HD", "MAX");
			for (int x = 0; x < hitDice; ++x)
			{
				setHitPoint(x, new Integer(Globals.rollHP(min, max, getName(), x + 1)));
			}
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
			//aRace.movement = new Integer(getMovement().intValue());
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
			aRace.hitPointMap = new HashMap(hitPointMap);
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
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return aRace;
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

	/**
	 * Produce a tailored PCC output, used for saving custom races.
	 * @see pcgen.core.PObject#getPCCText()
	 */
	public String getPCCText()
	{
		// 29 July 2003 : sage_sam corrected order
		final StringBuffer txt = new StringBuffer(super.getPCCText());

		if (favoredClass != null && favoredClass.length() > 0)
		{
			txt.append("\tFAVCLASS:").append(favoredClass);
		}
		if (bonusInitialFeats != 0)
		{
			txt.append("\tSTARTFEATS:").append(bonusInitialFeats);
		}
		if (size != null && size.length() > 0)
		{
			txt.append("\tSIZE:").append(size);
		}
		if (getMovements() != null && getMovements().length > 0)
		{
			txt.append("\tMOVE:");
			for (int index = 0; index < getMovements().length; index++)
			{
				if (index > 0)
				{
					txt.append(',');
				}
				txt.append(getMovementTypes()[index]).append(",");
				txt.append(getMovements()[index]);
			}
		}
		if (reach != 5)
		{
			txt.append("\tREACH:").append(reach);
		}

		if (chooseLanguageAutos != null && chooseLanguageAutos.length() > 0)
		{
			txt.append("\tCHOOSE:LANGAUTO:").append(chooseLanguageAutos);
		}
		if (languageBonus != null && !languageBonus.isEmpty())
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = languageBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append(',');
				}
				buffer.append(e.next().toString());
			}
			txt.append("\tLANGBONUS:").append(buffer.toString());
		}
		if (weaponProfBonus != null && weaponProfBonus.size() > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = weaponProfBonus.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}
				buffer.append((String) e.next());
			}
			txt.append("\tWEAPONBONUS:").append(buffer.toString());
		}
		if (mFeatList != null && mFeatList.length() > 0)
		{
			txt.append("\tMFEAT:").append(mFeatList);
		}

		if (legs != 2)
		{
			txt.append("\tLEGS:").append(legs);
		}
		if (hands != 2)
		{
			txt.append("\tHANDS:").append(hands);
		}
		if ((getNaturalWeapons() != null) && (getNaturalWeapons().size() > 0))
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = getNaturalWeapons().iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}
				Equipment natEquip = (Equipment) e.next();
				String eqName = natEquip.getName();
				int index = eqName.indexOf(" (Natural/Primary)");
				if (index >= 0)
				{
					eqName = eqName.substring(0, index) + eqName.substring(index + " (Natural/Primary)".length());
				}
				index = eqName.indexOf(" (Natural/Secondary)");
				if (index >= 0)
				{
					eqName = eqName.substring(0, index) + eqName.substring(index + " (Natural/Secondary)".length());
				}
				buffer.append(eqName).append(',');
				buffer.append(natEquip.getType(false)).append(',');
				if (!natEquip.isAttacksProgress())
				{
					buffer.append('*');
				}
				buffer.append((int) natEquip.bonusTo("WEAPON", "ATTACKS", true) + 1).append(',');
				buffer.append(natEquip.getDamage());
			}
			txt.append("\tNATURALATTACKS:").append(buffer.toString());
		}

		if (initialSkillMultiplier != 4)
		{
			txt.append("\tSKILLMULT:").append(initialSkillMultiplier);
		}
		if (monsterClass != null)
		{
			txt.append("\tMONSTERCLASS:").append(monsterClass);
			txt.append(':').append(monsterClassLevels);
		}

		if (templates != null && templates.size() > 0)
		{
			for (Iterator e = templates.iterator(); e.hasNext();)
			{
				txt.append("\tTEMPLATE:").append((String) e.next());
			}
		}
		if (hitDiceAdvancement != null && hitDiceAdvancement.length > 0)
		{
			txt.append("\tHITDICEADVANCEMENT:");
			for (int index = 0; index < hitDiceAdvancement.length; index++)
			{
				if (index > 0)
				{
					txt.append(',');
				}
				if (hitDiceAdvancement[index] == -1 && isAdvancementUnlimited())
				{
					txt.append('*');
				}
				else
				{
					txt.append(hitDiceAdvancement[index]);
				}
			}
		}
		if (CR != 0)
		{
			txt.append("\tCR:");
			if (CR < 0)
			{
				txt.append("1/").append(-CR);
			}
			else
			{
				txt.append(CR);
			}
		}

		if (startingAC.intValue() != 10)
		{
			txt.append("\tAC:").append(startingAC.toString());
		}
/*
		if (ageString != null && !Constants.s_NONE.equals(ageString) && ageString.length() > 0)
		{
			txt.append("\tAGE:").append(ageString);
		}
		if (BAB != 0)
		{
			txt.append("\tBAB:").append(BAB);
		}
*/
		if (face != null && face.length() > 0)
		{
			txt.append("\tFACE:").append(face);
		}
		if (featList != null && featList.length() > 0)
		{
			txt.append("\tFEAT:").append(featList);
		}
		if (hitDice != 0 || hitDiceSize != 0)
		{
			txt.append("\tHITDICE:").append(hitDice).append(',').append(hitDiceSize);
		}

		if (initMod.intValue() != 0)
		{
			txt.append("\tINIT:").append(initMod.toString());
		}
		if (langNum != 0)
		{
			txt.append("\tLANGNUM:").append(langNum);
		}
		if (!"0".equals(levelAdjustment))
		{
			txt.append("\tLEVELADJUSTMENT:").append(levelAdjustment);
		}

		if (weaponProfs != null && weaponProfs.size() > 0)
		{
			StringBuffer buffer = new StringBuffer();
			for (Iterator e = weaponProfs.iterator(); e.hasNext();)
			{
				if (buffer.length() != 0)
				{
					buffer.append('|');
				}
				buffer.append((String) e.next());
			}
			txt.append("\tPROF:").append(buffer.toString());
		}
		if (!"alwaysValid".equals(getQualifyString()))
		{
			txt.append("\tQUALIFY:").append(getQualifyString());
		}
		if (!Constants.s_NONE.equals(displayName))
		{
			txt.append("\tRACENAME:").append(displayName);
		}
		if (bonusSkillList != null && bonusSkillList.length() > 0)
		{
			txt.append("\tSKILL:").append(bonusSkillList);
		}
		if (vFeatList != null && vFeatList.length() > 0)
		{
			txt.append("\tVFEAT:").append(vFeatList);
		}
		if (bonusSkillsPerLevel != 0)
		{
			txt.append("\tXTRASKILLPTSPERLVL:").append(bonusSkillsPerLevel);
		}
		txt.append(super.getPCCText(false));
		return txt.toString();
	}

	public void setMonCSkillList(String aString)
	{
		if (monCSkillList == null)
		{
			monCSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{

			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				monCSkillList.clear();
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
			{

				Skill aSkill;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					if (aSkill.isType(bString.substring(5)))
					{
						monCSkillList.add(aSkill.getName());
					}
				}
			}
			else
			{
				monCSkillList.add(bString);
			}
		}
	}

	public void setMonCCSkillList(String aString)
	{
		if (monCCSkillList == null)
		{
			monCCSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{

			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				monCCSkillList.clear();
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
			{

				Skill aSkill;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					if (aSkill.isType(bString.substring(5)))
					{
						monCCSkillList.add(aSkill.getName());
					}
				}
			}
			else
			{
				monCCSkillList.add(bString);
			}
		}
	}

	boolean hasMonsterCSkill(String aName)
	{
		if (monCSkillList == null || monCSkillList.isEmpty())
		{
			return false;
		}
		if (monCSkillList.contains(aName))
		{
			return true;
		}
		if (monCSkillList.contains("LIST"))
		{

			String aString;
			for (int e = 0; e < getAssociatedCount(); ++e)
			{
				aString = getAssociated(e);
				if (aName.startsWith(aString) || aString.startsWith(aName))
				{
					return true;
				}
			}
		}

		String aString;
		for (Iterator e = monCSkillList.iterator(); e.hasNext();)
		{
			aString = (String) e.next();
			if (aString.lastIndexOf('%') >= 0)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
				{
					return true;
				}
			}
		}
		return false;
	}

	boolean hasMonsterCCSkill(String aName)
	{
		if (monCCSkillList == null || monCCSkillList.isEmpty())
		{
			return false;
		}
		if (monCCSkillList.contains(aName))
		{
			return true;
		}

		String aString;
		for (Iterator e = monCCSkillList.iterator(); e.hasNext();)
		{
			aString = (String) e.next();
			if (aString.lastIndexOf('%') >= 0)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
				{
					return true;
				}
			}
		}
		return false;
	}

}
