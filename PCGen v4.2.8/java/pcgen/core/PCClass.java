/*
 * PCClass.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * $Id: PCClass.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;
import pcgen.util.GuiFacade;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PCClass extends PObject
{
	private String subClassName = Constants.s_NONE;
	private String subClassString = Constants.s_NONE;
	private String prohibitedString = Constants.s_NONE;
	private int hitDie = 0;
	private int skillPoints = 0;
	private int initialFeats = 0;
	protected String spellBaseStat = Constants.s_NONE;
	private String spellType = Constants.s_NONE;
	private String attackBonusType = "O";
	private ArrayList knownList = new ArrayList();
	private ArrayList specialtyknownList = new ArrayList();
	private ArrayList castList = new ArrayList();
	private ArrayList uattList = new ArrayList();
	private ArrayList acList = new ArrayList();
	private TreeSet languageBonus = new TreeSet();
	private ArrayList featAutos = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
	protected int level = 0;
	private Integer[] hitPointList = new Integer[1];
	private ArrayList featList = new ArrayList();
	private ArrayList vFeatList = new ArrayList();
	private ArrayList domainList = new ArrayList();
	private Integer skillPool = new Integer(0);
	private String specialsString = new String();
	private ArrayList skillList = new ArrayList();
	private String classSkillString = null;
	private ArrayList classSkillList = null;
	private String classSpellString = null;
	private ArrayList classSpellList = null;
	private String defenseString = "15,0";
	private String reputationString = "20";
	private String exClass = "";
	private String levelExchange = "";
	private String abbrev = "";
	private boolean memorizeSpells = true;
	private boolean usesSpellbook = false;
	private int initMod = 0;
	private boolean multiPreReqs = false;
	private String deityString = "ANY";
	private ArrayList specialtyList = new ArrayList();
	private int maxLevel = 20;
	private ArrayList knownSpellsList = new ArrayList();
	private String attackCycle = "";
	private String castAs = "";
	protected int numSpellsFromSpecialty = 0;
	private String preRaceType = null;  //since I don't want this to be counted as making it a prestige class.
	private boolean modToSkills = true; // stat bonus applied to skills per level
	private int levelsPerFeat = 3;
	//private int ageSet = 2;
	private double itemCreationLevelMultiplier = 1.0;
	private final ArrayList templates = new ArrayList();
	private ArrayList templatesAdded = null;
	private ArrayList addDomains = new ArrayList();
	private String stableSpellKey = null;
	private final HashMap castForLevelMap = new HashMap();
	private ArrayList DR = null;
	private ArrayList SR = null;
	private boolean hasSubClass = false;
	private ArrayList subClassList = null; // list of SubClass objects

	public Object clone()
	{

		PCClass aClass = null;
		try
		{
			aClass = (PCClass) super.clone();
			aClass.setSubClassName(getSubClassName());
//			aClass.setSubClassString(getSubClassString());
			aClass.setProhibitedString(getProhibitedString());
			aClass.setHitDie(hitDie);
			aClass.setSkillPoints(skillPoints);
			aClass.setInitialFeats(initialFeats);
			aClass.setSpellBaseStat(spellBaseStat);
			aClass.setSpellType(spellType);
			aClass.setAttackBonusType(attackBonusType);
			aClass.knownList = (ArrayList) knownList.clone();
			aClass.specialtyknownList = (ArrayList) specialtyknownList.clone();
			aClass.castList = (ArrayList) castList.clone();
			aClass.uattList = (ArrayList) uattList.clone();
			aClass.acList = (ArrayList) acList.clone();
			aClass.languageBonus = (TreeSet) languageBonus.clone();
			aClass.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
			aClass.hitPointList = (Integer[]) getHitPointList().clone();
			aClass.featList = (ArrayList) featList.clone();
			aClass.vFeatList = (ArrayList) vFeatList.clone();
			aClass.featAutos = (ArrayList) featAutos.clone();
			aClass.skillList = new ArrayList();
			if (DR != null)
			{
				aClass.DR = (ArrayList) DR.clone();
			}
			aClass.classSkillString = classSkillString;
			aClass.classSkillList = null;
			aClass.classSpellString = classSpellString;
			aClass.classSpellList = null;
			aClass.stableSpellKey = null;

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
			aClass.knownSpellsList = (ArrayList) knownSpellsList.clone();
			aClass.attackCycle = attackCycle;
			aClass.castAs = castAs;
			aClass.preRaceType = preRaceType;
			aClass.modToSkills = modToSkills;
			aClass.levelsPerFeat = levelsPerFeat;
			aClass.initMod = initMod;
			aClass.specialtyList = (ArrayList) specialtyList.clone();
			//aClass.ageSet = ageSet;
			aClass.domainList = (ArrayList) domainList.clone();
			aClass.addDomains = (ArrayList) addDomains.clone();
			aClass.hasSubClass = hasSubClass;
			aClass.subClassList = subClassList;
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			return aClass;
		}
	}

	public void addTemplate(String template)
	{
		templates.add(template);
	}

	public ArrayList getClassSpecialAbilityList()
	{
		final ArrayList aList = new ArrayList();
		final ArrayList abilityList = this.getSpecialAbilityList();

		if (abilityList == null)
		{
			return aList;
		}
		if (!abilityList.isEmpty())
		{
			for (Iterator i = abilityList.iterator(); i.hasNext();)
			{
				SpecialAbility saAbility = (SpecialAbility) i.next();
				String aString = saAbility.toString();
				boolean found = false;
				for (Iterator ii = aList.iterator(); ii.hasNext();)
				{
					if (aString.equals(ii.next()))
					{
						found = true;
						break;
					}
				}
				if (!found && saAbility.pcQualifiesFor(Globals.getCurrentPC()))
				{
					aList.add(aString);
				}
			}
		}

		for (int i = 0, x = aList.size(); i < x; ++i)
		{
			StringTokenizer varTok = new StringTokenizer((String) aList.get(i), "|", false);
			String aString = varTok.nextToken();

			int[] varValue = null;
			int varCount = varTok.countTokens();
			if (varCount != 0)
			{
				varValue = new int[varCount];
				for (int j = 0; j < varCount; ++j)
				{
					final String vString = varTok.nextToken();
					varValue[j] = Globals.getCurrentPC().getVariable(vString, true, true, "", "").intValue();
				}
			}

			StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;
			boolean isZero = false;
			while (varTok.hasMoreTokens())
			{
				final String nextTok = varTok.nextToken();
				if ("%".equals(nextTok))
				{
					if (varCount == 0)
					{
						isZero = true;
					}
					if ((varValue != null) && (varCount < varValue.length))
					{
						final int thisVar = varValue[varCount++];
						isZero &= (thisVar == 0);
						newAbility.append(thisVar);
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
				aList.remove(i);
				--x;
			}
			aList.set(i, newAbility.toString());
		}
		return aList;
	}

	public void setSpecialAbilityList(String aString, int anInt)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		if (specialAbilityList == null)
		{
			specialAbilityList = new ArrayList();
		}
		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				specialAbilityList.clear();
				continue;
			}
			SpecialAbility sa = new SpecialAbility(bString, "PCCLASS=" + name + "|" + anInt);
			addSpecialAbilityToList(sa);
		}
	}

	protected ArrayList addSpecialAbilitiesToList(ArrayList aList)
	{
		if ((specialAbilityList == null) || specialAbilityList.isEmpty())
		{
			return aList;
		}
		final ArrayList bList = new ArrayList();
		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
		{
			final SpecialAbility sa = (SpecialAbility) i.next();
			if (sa.pcQualifiesFor(Globals.getCurrentPC()))
			{
				if (sa.getName().startsWith(".CLEAR"))
				{
					if (".CLEARALL".equals(sa.getName()))
					{
						if (Globals.isDebugMode())
						{
							Globals.debugPrint("Clearing SAs:" + bList);
						}
						bList.clear();
					}
					else if (sa.getName().startsWith(".CLEAR."))
					{
						final String saToRemove = sa.getName().substring(7);
						for (int itIdx = bList.size() - 1; itIdx >= 0 ; --itIdx)
						{
							final String saName = ((SpecialAbility) bList.get(itIdx)).getName();
							if (saName.equals(saToRemove))
							{
								if (Globals.isDebugMode())
								{
									Globals.debugPrint("Removing SA:" + saName);
								}
								bList.remove(itIdx);
							}
							else if (saName.indexOf('(') >= 0)
							{
								if (saName.substring(0, saName.indexOf('(')).trim().equals(saToRemove))
								{
									if (Globals.isDebugMode())
									{
										Globals.debugPrint("Removing SA:" + saName);
									}
									bList.remove(itIdx);
								}
							}
						}
					}
					continue;
				}
				bList.add(sa);
			}
		}
		aList.addAll(bList);
		return aList;
	}

	String makeBonusString(String bonusString, String chooseString)
	{
		return "0|" + super.makeBonusString(bonusString, chooseString);
	}

	private ArrayList getTemplates(boolean flag)
	{
		final ArrayList newTemplates = new ArrayList();
		templatesAdded = new ArrayList();

		for (int x = 0; x < templates.size(); ++x)
		{
			final String template = (String) templates.get(x);
			final StringTokenizer aTok = new StringTokenizer(template, "|", false);
			if (level < Integer.parseInt(aTok.nextToken()))
			{
				continue;
			}
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

	public final boolean getMemorizeSpells()
	{
		return memorizeSpells;
	}

	public final void setMemorizeSpells(boolean memorizeSpells)
	{
		this.memorizeSpells = memorizeSpells;
	}

	final String getDeityString()
	{
		return deityString;
	}

	public final void setDeityString(String deityString)
	{
		this.deityString = deityString;
	}

	public final Collection getSpecialtyList()
	{
		return specialtyList;
	}

	public String getSpecialtyListString()
	{
		final StringBuffer retString = new StringBuffer();
		if (!specialtyList.isEmpty())
		{
			for (Iterator i = specialtyList.iterator(); i.hasNext();)
			{
				if (retString.length() > 0)
				{
					retString.append(',');
				}
				retString.append((String) i.next());
			}
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (!aPC.getCharacterDomainList().isEmpty())
		{
			for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain) i.next();
				if (aCD.getDomain() != null)
				{
					if (retString.length() > 0)
					{
						retString.append(',');
					}
					retString.append(aCD.getDomain().getName());
				}
			}
		}
		return retString.toString();
	}

	public String getSpellKey()
	{
		if (stableSpellKey != null)
		{
			return stableSpellKey;
		}
		if (classSpellList == null)
		{
			chooseClassSpellList();
			if (classSpellList == null)
			{
				stableSpellKey = "CLASS|" + name;
				return stableSpellKey;
			}
		}
		final StringBuffer aBuf = new StringBuffer();
		for (Iterator i = classSpellList.iterator(); i.hasNext();)
		{
			final String aString = i.next().toString();
			if (aBuf.length() > 0)
			{
				aBuf.append('|');
			}
			if (aString.endsWith("(Domain)"))
			{
				aBuf.append("DOMAIN|").append(aString.substring(0, aString.length() - 8));
			}
			else
			{
				aBuf.append("CLASS|").append(aString);
			}
		}
		stableSpellKey = aBuf.toString();
		return stableSpellKey;
	}

	public final String getAbbrev()
	{
		return abbrev;
	}

	public final void setAbbrev(String argAbbrev)
	{
		abbrev = argAbbrev;
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

	public final Integer getSkillPool()
	{
		return skillPool;
	}

	public final void setSkillPool(Integer argSkillPool)
	{
		skillPool = argSkillPool;
	}

	public final double getItemCreationLevelMultiplier()
	{
		return itemCreationLevelMultiplier;
	}

	public final void setItemCreationLevelMultiplier(double argItemCreationLevelMultiplier)
	{
		itemCreationLevelMultiplier = argItemCreationLevelMultiplier;
	}

	public final void setPreRaceType(final String preRaceType)
	{
		this.preRaceType = preRaceType.toUpperCase();
	}

	//public int getAgeSet()
	//{
	//return ageSet;
	//}

	//public void setAgeSet(int ageSet)
	//{
	//this.ageSet = ageSet;
	//}

	public final boolean isVisible()
	{
		return visible;
	}

	public final void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	final boolean multiPreReqs()
	{
		return multiPreReqs;
	}

	public final void setMultiPreReqs(boolean multiPreReqs)
	{
		this.multiPreReqs = multiPreReqs;
	}

	public void addDomainList(String domainItem)
	{
		domainList.add(domainItem);
	}

	/* addDomains is the prestige domains this class has access to */
	public final ArrayList getAddDomains()
	{
		return addDomains;
	}

	public void setAddDomains(int level, String aString, String delimiter)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, delimiter, false);
		final String prefix = Integer.toString(level) + '|';
		while (aTok.hasMoreTokens())
		{
			addDomains.add(prefix + aTok.nextToken());
		}
	}

	public final String toString()
	{
		return name;
	}

	public void setName(String newName)
	{
		super.setName(newName);
		int i = 3;
		if ("".equals(abbrev))
		{
			if (newName.length() < 3)
			{
				i = newName.length();
			}
			abbrev = newName.substring(0, i);
		}
		stableSpellKey = null;
		getSpellKey();
	}

	public final void setCastAs(String aString)
	{
		castAs = aString;
	}

	public final String getCastAs()
	{
		return castAs;
	}

	public String getSubClassName()
	{
		if (subClassName == null)
		{
			subClassName = "";
		}
		return subClassName;
	}

	public String getDisplayClassName()
	{
		if (subClassName.length() > 0 && !subClassName.equals(Constants.s_NONE))
		{
			return subClassName;
		}
		return name;
	}

	public void setSubClassName(String aString)
	{
		subClassName = aString;
		if (!aString.equals(name))
		{
			SubClass a = getSubClassNamed(aString);
			if (a != null)
			{
				inheritAttributesFrom(a);
			}
		}
		stableSpellKey = null;
		getSpellKey();
	}

	public final String getSubClassString()
	{
		return subClassString;
	}

	public final void setSubClassString(String aString)
	{
		subClassString = aString;
	}

	public final void setHasSubClass(boolean arg)
	{
		//TODO Is this right? If so, it should definitely be commented.
		hasSubClass = true;
	}

	public final void addSubClass(SubClass sClass)
	{
		if (subClassList == null)
		{
			subClassList = new ArrayList();
		}
		subClassList.add(sClass);
	}

	public ArrayList getSubClassList()
	{
		return subClassList;
	}

	public final SubClass getSubClassNamed(String arg)
	{
		if (subClassList == null)
		{
			return null;
		}
		for (Iterator i = subClassList.iterator(); i.hasNext();)
		{
			SubClass a = (SubClass) i.next();
			if (a.getName().equals(arg))
			{
				return a;
			}
		}
		return null;
	}

	public final String getProhibitedString()
	{
		return prohibitedString;
	}

	public final void setProhibitedString(String aString)
	{
		prohibitedString = aString;
	}

	boolean isProhibited(Spell aSpell)
	{
		final StringTokenizer aTok = new StringTokenizer(prohibitedString, ",", false);
		if (!aSpell.passesPreReqTests())
		{
			return true;
		}
		while (aTok.hasMoreTokens())
		{
			final String a = aTok.nextToken();
			if (a.equals(aSpell.getSchool()) || a.equals(aSpell.getSubschool()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean isSpecialtySpell(Spell aSpell)
	{
		final ArrayList aList = (ArrayList) getSpecialtyList();
		if (aList == null || aList.size() == 0)
		{
			return false;
		}
		return (aSpell.descriptorListContains(aList) || aSpell.schoolContains(aList) || aSpell.subschoolContains(aList));
	}

	private double getBonusTo(String type, String mname)
	{
		return getBonusTo(type, mname, level);
	}

	public double getBonusTo(String argType, String argMname, int asLevel)
	{
//		Globals.debugPrint("Getting bonus of type: " + type + " for name: " + mname + ", Class " + getDisplayClassName() + " as level " + asLevel);

		double i = 0;
		if (getBonusList().isEmpty())
		{
			return 0;
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();
		final String typePlusMName = new StringBuffer(type).append('.').append(mname).append('.').toString();
		for (Iterator e = bonusList.iterator(); e.hasNext();)
		{
			StringTokenizer breakOnPipes = new StringTokenizer(((String) e.next()).toUpperCase(), "|", false);
			int aLevel = Integer.parseInt(breakOnPipes.nextToken());
			String theType = breakOnPipes.nextToken();
			if (!theType.equals(type))
			{
				continue;
			}
			final String str = breakOnPipes.nextToken();
			StringTokenizer breakOnCommas = new StringTokenizer(str, ",", false);
			while (breakOnCommas.hasMoreTokens())
			{
				String theName = breakOnCommas.nextToken();
				if (aLevel <= asLevel && theName.equals(mname))
				{
					String aString = breakOnPipes.nextToken();
					ArrayList localPreReqList = new ArrayList();
					String bonusType = null;
					while (breakOnPipes.hasMoreTokens())
					{
						final String bString = breakOnPipes.nextToken();
						if (bString.startsWith("PRE") || bString.startsWith("!PRE"))
						{
							localPreReqList.add(bString);
						}
						else if (bString.startsWith("TYPE="))
						{
							bonusType = bString.substring(5);
						}
					}
					// must meet criteria for bonuses before adding them in
					if (passesPreReqTestsForList(localPreReqList))
					{
						final double j = aPC.getVariableValue(aString, "CLASS:" + name).doubleValue();
						i += j;
						aPC.setBonusStackFor(j, typePlusMName + bonusType);
					}
				}
			}
		}
		return i;
	}

	private boolean canBePrestige()
	{
		return passesPreReqTests();
	}

	public boolean passesPreReqTestsForList(PlayerCharacter aPC, PObject aObj, ArrayList anArrayList)
	{
		return (SettingsHandler.isBoolBypassClassPreReqs() || super.passesPreReqTestsForList(aPC, aObj, anArrayList));
	}

	public final int getMaxLevel()
	{
		return maxLevel;
	}

	public final void setMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
	}

	// HITDIE:num --- sets the hit die to num regardless of class.
	// HITDIE:%/num --- divides the classes hit die by num.
	// HITDIE:%*num --- multiplies the classes hit die by num.
	// HITDIE:%+num --- adds num to the classes hit die.
	// HITDIE:%-num --- subtracts num from the classes hit die.
	// HITDIE:%upnum --- moves the hit die num steps up the die size list d4,d6,d8,d10,d12. Stops at d12.
	// HITDIE:%downnum --- moves the hit die num steps down the die size list d4,d6,d8,d10,d12. Stops at d4.
	// Regardless of num it will never allow a hit die below 1.


	public int getHitDie()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final String dieLock = aPC.getRace().getHitDieLock();
		if (dieLock.length() == 0)
		{
			return hitDie;
		}

		final int[] dieSizes = Globals.getDieSizes();
		int diedivide;

		if (dieLock.startsWith("%/"))
		{
			diedivide = Integer.parseInt(dieLock.substring(2));
			if (diedivide <= 0)
			{
				diedivide = 1; // Idiot proof it. Stop Divide by zero errors.
			}
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
			if (diedivide > 4)
			{
				diedivide = 4;
			}
			if (diedivide < 0)
			{
				diedivide = 0;
			}
			for (int i = 3; i <= (7 - diedivide); ++i)
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
			for (int i = 0; i < ((dieSizes.length) - diedivide); ++i)
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
			if (diedivide > 4)
			{
				diedivide = 4;
			}
			if (diedivide < 0)
			{
				diedivide = 0;
			}
			for (int i = (3 + diedivide); i <= 7; ++i)
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
			for (int i = diedivide; i < dieSizes.length; ++i)
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
		if (diedivide <= 0)
		{
			diedivide = 1; // Idiot proof it.
		}
		return diedivide;
	}

	public final void setHitDie(int dice)
	{
		hitDie = dice;
	}

	private int getSkillPoints()
	{
		return skillPoints;
	}

	public final void setSkillPoints(int points)
	{
		skillPoints = points;
	}

	public final void setInitialFeats(int feats)
	{
		initialFeats = feats;
	}

	public final String getSpellBaseStat()
	{
		return spellBaseStat;
	}

	public final void setSpellBaseStat(String baseStat)
	{
		spellBaseStat = baseStat;
	}

	public final void setSpellLevelString(final String aString)
	{
		classSpellString = aString;
	}

	private void chooseClassSpellList()
	{
		// if no entry or no choices, just return
		if (classSpellString == null || level < 1)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(classSpellString, "|", false);
		int amt = 0;
		if (classSpellString.indexOf('|') >= 0)
		{
			amt = Integer.parseInt(aTok.nextToken());
		}
		final ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		if (aList.size() == amt)
		{
			classSpellList = aList;
			return;
		}
		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose list of spells this class will use");
		c.setPool(amt);
		c.setPoolFlag(false);
		c.setAvailableList(aList);
		c.show();
		final List selectedList = c.getSelectedList();
		classSpellList = new ArrayList();
		for (Iterator i = selectedList.iterator(); i.hasNext();)
		{
			final String aString = i.next().toString();
			classSpellList.add(aString);
		}
	}

	public final ArrayList getClassSpellList()
	{
		return classSpellList;
	}

	private void newClassSpellList()
	{
		if (classSpellList == null)
		{
			classSpellList = new ArrayList();
		}
		else
		{
			classSpellList.clear();
		}
	}

	public void addClassSpellList(final String tok)
	{
		if (classSpellList == null)
		{
			newClassSpellList();
		}
		classSpellList.add(tok);
		classSpellString = null;
		stableSpellKey = null;
	}

	public final String getSpellType()
	{
		return spellType;
	}

	public final void setSpellType(String newType)
	{
		spellType = newType;
	}

	public final String getAttackBonusType()
	{
		return attackBonusType;
	}

	public final void setAttackBonusType(String aString)
	{
		attackBonusType = aString;
	}

	public final void setModToSkills(boolean bool)
	{
		modToSkills = bool;
	}

	public void setLevelsPerFeat(int newLevels)
	{
		if (newLevels < 0)
		{
			return;
		}
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

	// if castAs has been set, return knownList from that class
	public Collection getKnownList()
	{
		if ("".equals(castAs))
		{
			return knownList;
		}
		final PCClass aClass = Globals.getClassNamed(castAs);
		if (aClass != null)
		{
			return aClass.getKnownList();
		}
		return knownList;
	}

	public final Collection getSpecialtyKnownList()
	{
		return specialtyknownList;
	}

	private int baseSpellIndex()
	{
		final String spellBaseStat = getSpellBaseStat();
		return "SPELL".equals(spellBaseStat) ? -2 // means base spell stat is based upon spell itself
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
	 * Return number of spells known for a level for a given spellbook.
	 */
	int getKnownForLevel(int pcLevel, int spellLevel, String bookName)
	{
		int total;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) + (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Beginning Total:", total);
		}
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", name, false);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE=" + getSpellType(), false);

		final int index = baseSpellIndex();
		if (index != -2)
		{
			final PCStat aStat = (PCStat) aPC.getStatList().getStats().get(index);
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + aStat.getAbb(), "").intValue();
			if (spellLevel > maxSpellLevel)
			{
				return total;
			}
		}

		if (Globals.isSSd20Mode())
		{

			if ("Intimate Knowledge Spells".equals(bookName))
			{
// TODO: change this to a BONUS: tag on the spellcasting class?
//				total += 20 + ((pcLevel) * 5) + ((aPC.calcStatMod(index)) * 10);
			}
			else
			{
// TODO: change this to a BONUS: tag on the spellcasting class?
//				total += 40 + ((pcLevel) * 10) + ((aPC.calcStatMod(index)) * 10);
			}
			return total;
		}

		boolean psiSpecialty = false;
		if (!getKnownList().isEmpty())
		{
			for (Iterator e = getKnownList().iterator(); e.hasNext();)
			{
				final String aString = (String) e.next();
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
						--x;
					}
				}
				--pcLevel;
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
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Base spells Known:", total);
			Globals.debugPrint("KnownSpecialty:", numSpellsFromSpecialty);
		}
		// if we have known spells (0==no known spells recorded) or a psi specialty.
		if ((total > 0 && spellLevel > 0) && !psiSpecialty)
		{
			// make sure any slots due from specialties (including domains) are added
			total += numSpellsFromSpecialty;
		}
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Total Spells Known:", total);
		}
		return total;
	}

	/**
	 * Return number of speciality spells known for a level for a given spellbook.
	 */
	public int getSpecialtyKnownForLevel(int pcLevel, int spellLevel)
	{
		int total;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS=" + getKeyName() + ";LEVEL=" + spellLevel, true) + (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE=" + getSpellType() + ";LEVEL=" + spellLevel, true);
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Beginning Total:", total);
		}

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", name, false);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE=" + getSpellType(), false);

		final int index = baseSpellIndex();
		if (index != -2)
		{
			final PCStat aStat = (PCStat) aPC.getStatList().getStats().get(index);
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + aStat.getAbb(), "").intValue();
			if (spellLevel > maxSpellLevel)
			{
				return total;
			}
		}

		String aString;
		StringTokenizer aTok;
		int x = spellLevel;
		if (!specialtyknownList.isEmpty())
		{
			for (Iterator e = specialtyknownList.iterator(); e.hasNext();)
			{
				aString = (String) e.next();
				if (pcLevel == 1)
				{
					aTok = new StringTokenizer(aString, ",", false);
					while (aTok.hasMoreTokens())
					{
						String spells = (String) aTok.nextElement();
						final int t = Integer.parseInt(spells);
						if (x == 0)
						{
							total += t;
							break;
						}
						--x;
					}
				}
				--pcLevel;
				if (pcLevel < 1)
				{
					break;
				}
			}
		}
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Base specialty spells Known:", total);
		}
		// Globals.debugPrint("Known Specialty:" + numSpellsFromSpecialty);
		// if we have known spells (0==no known spells recorded) or a psi specialty.
		if (total > 0 && spellLevel > 0)
		{
			// make sure any slots due from specialties (including domains) are added
			total += numSpellsFromSpecialty;
		}
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Total Specialty Spells Known:", total);
		}
		return total;
	}

	/*
	 * if castAs has been set, grab the castList from that class
	 */
	public List getCastList()
	{
		if ("".equals(castAs))
		{
			return castList;
		}
		final PCClass aClass = Globals.getClassNamed(castAs);
		if (aClass != null)
		{
			return aClass.getCastList();
		}
		return castList;
	}

	public void addCastList(String cast)
	{
		castList.add(cast);
	}

	public void setCastList(int index, String cast)
	{
		if (index < castList.size())
		{
			castList.set(index, cast);
		}
		else
		{
			addCastList(cast);
		}
	}

	final int getNumSpellsFromSpecialty()
	{
		return numSpellsFromSpecialty;
	}

	public final void setNumSpellsFromSpecialty(int anInt)
	{
		numSpellsFromSpecialty = anInt;
	}

	public String getBonusCastForLevelString(int pcLevel, int spellLevel, String bookName)
	{
		if (getCastForLevel(pcLevel, spellLevel, bookName) > 0)
		{
			// if this class has a specialty, return +1
			if (specialtyList.size() > 0)
			{
				return "+1";
			}
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC.getCharacterDomainList().isEmpty())
			{
				return "";
			}
			// if the spelllevel is >0 and this class has a characterdomain associated with it, return +1
			if (spellLevel > 0 && "DIVINE".equalsIgnoreCase(spellType))
			{
				for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
				{
					CharacterDomain aCD = (CharacterDomain) i.next();
					if (aCD.isFromPCClass(getName()))
					{
						return "+1";
					}
				}
			}
		}
		return "";
	}

	/**********************************/
	//added by Mario Bonassin 10-14-01
//Spellpoints -
// Seems to be functioning fine
// Not to sure what I can get rid of and still have it work.
	public String getSPForLevelString(int pcLevel)
	{
		int total;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int index = baseSpellIndex();
		int temp2 = 0;
		if (index != -2)
		{
			final PCStat aStat = (PCStat) aPC.getStatList().getStats().get(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
			temp2 += aPC.getStatList().getStatModFor(aStat.getAbb());
		}

		final int temp3 = (temp2 * pcLevel);
		total = stat + temp3;
		final String bString = String.valueOf(total);
		return bString;
	}
	//End of Spellpoints
	/********************************/

	private void getStableCastForLevel()
	{
		//
		// Shouldn't we be using Globals.getLevelInfo().size() instead of 45?
		// Byngl -- November 25, 2002
		//
		for (int i = 0; i < 45; ++i)
		{
			final int s = getCastForLevel(level, i);
			castForLevelMap.put(String.valueOf(i), String.valueOf(s));
		}
	}

	int getCastForLevel(int pcLevel, int spellLevel)
	{
		return getCastForLevel(pcLevel, spellLevel, Globals.getDefaultSpellBook(), true);
	}

	public int getCastForLevel(int pcLevel, int spellLevel, String bookName)
	{
		return getCastForLevel(pcLevel, spellLevel, bookName, true);
	}

	private int getCastForLevel(int pcLevel, int spellLevel, String bookName, boolean includeAdj)
	{
		return getCastForLevel(pcLevel, spellLevel, bookName, includeAdj, true);
	}

	int getCastForLevel(int pcLevel, int spellLevel, String bookName, boolean includeAdj, boolean limitByStat)
	{
		int total;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final String classKeyName = "CLASS=" + getKeyName();
		final String levelSpellLevel = ";LEVEL=" + spellLevel;

		total = (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel, true) + (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE=" + getSpellType() + levelSpellLevel, true) + (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS=Any" + levelSpellLevel, true);

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", name, false);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE=" + getSpellType(), false);

		final int index = baseSpellIndex();
		PCStat aStat;

		if (index != -2)
		{
			aStat = (PCStat) aPC.getStatList().getStats().get(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0)
		{
			statString = Globals.s_ATTRIBSHORT[index];
		}
		final int bonusStat = (int) aPC.getTotalBonusTo("STAT", "CAST=" + statString, true) + (int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT", true);
		;
		if (index > -2 && limitByStat)
		{
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + statString, "").intValue();
			if (maxSpellLevel + bonusStat < spellLevel)
			{
				return total;
			}
		}
		stat += bonusStat;

		// Now we decide whether to adjust the number of slots down the road by adding specialty slots.
		// I reworked this to consider the fact that a lower-level specialty spell can go into
		// this level of specialty slot.  sk4p 13 Dec 2002
		//
		int adj = 0;
		if (includeAdj && !bookName.equals(Globals.getDefaultSpellBook()) && (specialtyList.size() > 0 || aPC.getCharacterDomainList().size() > 0))
		{
			// We need to do this for EVERY spell level up to the one really under consideration, because
			// if there are any specialty spells available BELOW this level, we might wind up using
			// THIS level's slots for them.
			for (int ix = 0; ix <= spellLevel; ix++)
			{
				final ArrayList aList = getCharacterSpell(null, "", ix);
				ArrayList bList = new ArrayList();
				if (!aList.isEmpty())
				{
					if (ix > 0 && "DIVINE".equalsIgnoreCase(spellType))
					{
						for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
						{
							CharacterDomain aCD = (CharacterDomain) i.next();
							if (aCD.isFromPCClass(getName()) && aCD.getDomain() != null)
							{
								bList = Globals.getSpellsIn(ix, "", aCD.getDomain().getName());
							}
						}
					}
					for (Iterator e = aList.iterator(); e.hasNext();)
					{
						int x = -1;
						final CharacterSpell cs = (CharacterSpell) e.next();
						if (!bList.isEmpty())
						{
							if (bList.contains(cs.getSpell()))
							{
								x = 0;
							}
						}
						else
						{
							x = cs.getInfoIndexFor("", ix, 1);
						}
						if (x > -1)
						{
							adj = 1;
							break;
						}
					}
				} // end of what to do if aList is not empty
				if (adj == 1)
					break;
			} // end of looping up to this level looking for specialty spells that can be cast
		} // end of deciding whether there are specialty slots to distribute

		final int temp = spellLevel;

		if (pcLevel > getCastList().size())
		{
			pcLevel = getCastList().size();
		}

		//
		// Multiplier for things like Ring of Wizardry
		//
		int mult = (int) aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName + levelSpellLevel, true) + (int) aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE=" + getSpellType() + levelSpellLevel, true);

		if (mult < 1)
		{
			mult = 1;
		}

		String aString;
		if (!getCastList().isEmpty())
		{
			for (Iterator e = getCastList().iterator(); e.hasNext();)
			{
				aString = (String) e.next();
				if (pcLevel == 1)
				{
					final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
					while (aTok.hasMoreTokens())
					{
						final int t = Integer.parseInt((String) aTok.nextElement());
						if (spellLevel == 0)
						{
							total += (t * mult) + adj;
							Object bonusSpell = Globals.getBonusSpellMap().get(String.valueOf(temp));
							if (bonusSpell != null && !bonusSpell.equals("0|0"))
							{
								final StringTokenizer s = new StringTokenizer(bonusSpell.toString(), "|", false);
								final int base = Integer.parseInt(s.nextToken());
								final int range = Integer.parseInt(s.nextToken());
								if (stat >= base)
									total += Math.max(0, (stat - base + range) / range);
							}
							break;
						}
						--spellLevel;
					}
				}
				--pcLevel;
				if (pcLevel < 1)
				{
					break;
				}
			}
		}
		return total;
	}

	public final Collection getUattList()
	{
		return uattList;
	}

	String getUattForLevel(int aLevel)
	{
		final String aString = "0";
		if (uattList.isEmpty())
		{
			return aString;
		}

		String bString;
		for (Iterator e = uattList.iterator(); e.hasNext();)
		{
			bString = (String) e.next();
			if (aLevel == 1)
			{
				return bString;
			}
			--aLevel;
			if (aLevel < 1)
			{
				break;
			}
		}
		return null;
	}

	private String getUMultForLevel(int aLevel)
	{
		String aString = "0";
		if (umultList == null || umultList.isEmpty())
		{
			return aString;
		}

		String bString;
		for (Iterator e = umultList.iterator(); e.hasNext();)
		{
			bString = (String) e.next();
			int pos = bString.lastIndexOf('|');
			if (pos >= 0 && aLevel <= Integer.parseInt(bString.substring(0, pos)))
			{
				aString = bString.substring(pos + 1);
			}

		}
		return aString;
	}

	String getUdamForLevel(int aLevel, boolean includeCrit, boolean includeStrBonus)
	{
		//
		// Check "Unarmed Strike", then default to "1d3"
		//
		String aDamage;
		if (udamList != null)
		{
			aLevel += (int) Globals.getCurrentPC().getTotalBonusTo("UDAM", "CLASS=" + name, true);
		}
		int iLevel = aLevel;
		final Equipment eq = Globals.getEquipmentKeyed("Unarmed Strike");
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
		aDamage = Globals.adjustDamage(aDamage, Globals.getDefaultSizeAdjustment().getAbbreviation(), Globals.getSizeAdjustmentAtIndex(iSize).getAbbreviation());

		//
		// Check the UDAM list for monk-like damage
		//
		udamList = Globals.getClassNamed(name).getUdamList();
		if (udamList != null && !udamList.isEmpty())
		{
			if (udamList.size() == 1)
			{
				final String aString = udamList.get(0).toString();
				if (aString.startsWith("CLASS="))
				{
					final PCClass aClass = Globals.getClassNamed(aString.substring(6));
					if (aClass != null)
					{
						return aClass.getUdamForLevel(aLevel, includeCrit, includeStrBonus);
					}
					Globals.debugPrint(name + " refers to " + aString.substring(6) + " which isn't loaded.");
					return aDamage;
				}
			}
			if (aLevel > udamList.size())
			{
				iLevel = udamList.size();
			}
			final StringTokenizer aTok = new StringTokenizer(udamList.get(Math.max(iLevel - 1, 0)).toString(), ",", false);
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

		final StringBuffer aString = new StringBuffer(aDamage);
		final int b = (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");
		if (includeStrBonus && b > 0)
		{
			aString.append('+');
		}
		if (includeStrBonus && b != 0)
		{
			aString.append(String.valueOf(b));
		}
		if (includeCrit)
		{
			final String dString = getUMultForLevel(aLevel);
			if (!"0".equals(dString))
			{
				aString.append("(x").append(dString).append(')');
			}
		}
		return aString.toString();
	}

	public final Collection getACList()
	{
		return acList;
	}

	final Set getLanguageBonus()
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

	final Collection getFeatAutos()
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
					final String postFix = "|" + fName.substring(7);
					//remove feat by name, must run through all 20 levels
					for (int i = 0; i < 45; ++i)
					{
						featAutos.remove(i + postFix);
					}
				}
				else // clear em all
				{
					featAutos.clear();
				}
			}
			else
			{
				getFeatAutos().add(prefix + fName);
			}
		}
	}

	public final ArrayList getWeaponProfBonus()
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

	public final int getLevel()
	{
		return level;
	}

	public void setLevel(int newLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int curLevel = level;
		if (newLevel >= 0)
		{
			level = newLevel;
		}
		if (level == 1)
		{
			chooseClassSkillList();
		}

		if ((level == 1) && !aPC.isImporting() && (curLevel == 0))
		{
			checkForSubClass();
			getSpellKey(); // makes sure that the spell key has been generated
		}

//Globals.errorPrint("spells="+knownSpellsList.size()+" import="+aPC.isImporting()+" subclass="+subClassName+" special="+specialtyList.size()+" PCName:"+aPC.getName());
		if ((knownSpellsList.size() > 0) && !aPC.isImporting() && aPC.getAutoSpells())
		{
			final ArrayList cspelllist = Globals.getSpellsIn(-1, getSpellKey(), "");
			if (cspelllist.isEmpty())
			{
				return;
			}
			getStableCastForLevel();
			int maxLevel;
			for (maxLevel = 0; maxLevel < 45; ++maxLevel)
			{
				final String val = castForLevelMap.get(String.valueOf(maxLevel)).toString();
				if (val == null || Integer.parseInt(val) == 0)
				{
					--maxLevel;
					break;
				}
			}
			for (Iterator s = cspelllist.iterator(); s.hasNext();)
			{
				final Spell aSpell = (Spell) s.next();
				final int[] spellLevels = aSpell.levelForKey(getSpellKey());
				for (int si = 0; si < spellLevels.length; ++si)
				{
					final int spellLevel = spellLevels[si];
					if (isAutoKnownSpell(aSpell.getKeyName(), spellLevel, true))
					{
						CharacterSpell cs = getCharacterSpellForSpell(aSpell, this);
						if (cs != null)
						{
							continue; // already know this one
						}
						cs = new CharacterSpell(this, aSpell);
						final String val = castForLevelMap.get(String.valueOf(spellLevel)).toString();
						boolean addIt = false;
						if (val != null)
						{
							addIt = (Integer.parseInt(val) > 0);
						}
						if (addIt == false)
						{
							continue;
						}
						cs.addInfo(spellLevel, 1, Globals.getDefaultSpellBook());
						addCharacterSpell(cs);
					}
				}
			}

			if (!aPC.getCharacterDomainList().isEmpty())
			{
				CharacterDomain aCD;
				for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
				{
					aCD = (CharacterDomain) i.next();
					if (aCD.getDomain() != null && aCD.isFromPCClass(getName()))
					{
						aCD.getDomain().addSpellsToClassForLevels(this, 0, maxLevel);
					}
				}
			}
		}
	}

	public void addLevel(boolean levelMax)
	{
		addLevel(levelMax, false);
	}

	void doPlusLevelMods(int newLevel)
	{
		if (!isMonster())
		{
			changeFeatsForLevel(newLevel, true);
		}
		addAddsForLevel(newLevel);
		changeSpecials();
		addVariablesForLevel(newLevel);
	}

	void doMinusLevelMods(PlayerCharacter aPC, int oldLevel)
	{
		if (!isMonster())
		{
			changeFeatsForLevel(oldLevel, false);
		}
		subAddsForLevel(oldLevel);
		changeSpecials();
		aPC.removeVariable("CLASS:" + getName() + "|" + Integer.toString(oldLevel));
	}

	void addLevel(boolean levelMax, boolean bSilent)
	{
		final int newLevel = level + 1;
		if (isMonster())
		{
			levelMax = false;
		}
		if (newLevel > maxLevel && levelMax)
		{
			if (!bSilent)
			{
				GuiFacade.showMessageDialog(null, "This class cannot be raised above level " + Integer.toString(maxLevel), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			}
			return;
		}

		final PlayerCharacter aPC = Globals.getCurrentPC();
		int total = aPC.getTotalLevels();
		if (total == 0)
		{
			aPC.setFeats(aPC.getInitialFeats());
		}

		setLevel(newLevel);
		final ArrayList templateList = getTemplates(aPC.isImporting());
		for (int x = 0; x < templateList.size(); ++x)
		{
			aPC.addTemplate(Globals.getTemplateNamed((String) templateList.get(x)));
		}
		int dnum = (int) getBonusTo("DOMAIN", "NUMBER", newLevel);

		if (dnum > 0 && !aPC.getCharacterDomainList().isEmpty())
		{
			CharacterDomain aCD;
			for (Iterator i = aPC.getCharacterDomainList().iterator(); i.hasNext();)
			{
				aCD = (CharacterDomain) i.next();
				// if the characterdomain comes from this class, decrement total
				if (aCD.isFromPCClass(getName()))
				{
					--dnum;
				}
			}
		}
		if (levelAbilityList != null && !levelAbilityList.isEmpty())
		{
			for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
			{
				LevelAbility ability = (LevelAbility) e.next();
				if (ability.level() <= newLevel && ability.getList().startsWith("SPELLCASTER"))
				{
					// assume that classes which gain Domains, but also have ADD:SPELLCASTER will actually
					// grant the domains to the selected class, and this will all be handed in LevelAbility
					// rather than here.
					dnum = 0;
					break;
				}
			}
		}
		for (; dnum > 0; --dnum) // character has more domains due this class than currently allocated
		{
			CharacterDomain aCD = new CharacterDomain();
			aCD.setFromPCClass(true);
			aCD.setObjectName(getName());
			aCD.setLevel(newLevel);
			aPC.getCharacterDomainList().add(aCD);
		}
		if (isMonster())
		{
			if (levelsPerFeat != 0)
			{
				// Monster feats are based on EHD, not just total HD.
				// Problem discovered, fixed 03 Dec 2002 by sage_sam
				if (getExtraHD(aPC, aPC.totalHitDice()) % levelsPerFeat == 0)
				{
					aPC.setFeats(aPC.getFeats() + 1);
				}
			}
		}

		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(newLevel);

		rollHP();

		if (!aPC.isImporting())
		{
			modDomainsForLevel(newLevel, true);
		}
		int levelUpStats = 0;
		if (!isMonster() && aPC.getTotalLevels() > total)
		{
			total = aPC.getTotalLevels();
			if (!aPC.isImporting())
			{
				// We do not want to do these
				// calculations a second time when are
				// importing a character.  The feat
				// number and the stat point pool are
				// already saved in the import file.

				//removed for d20/OGL compliance
/*				int minXP = aPC.minXPForECL();
				if (aPC.getXP() < minXP)
				{
					aPC.setXP(minXP);
				}
*/
				final int bonusFeats = Globals.getBonusFeatsForLevel(total);
				if (bonusFeats > 0)
				{
					aPC.setFeats(aPC.getFeats() + bonusFeats);
				}
				final int bonusStats = Globals.getBonusStatsForLevel(total);
				if (bonusStats > 0)
				{
					aPC.setPoolAmount(aPC.getPoolAmount() + bonusStats);
					if (!bSilent && SettingsHandler.getShowStatDialogAtLevelUp())
					{
						levelUpStats = askForStatIncrease(aPC, bonusStats, true);
					}
				}
			}
		}


		// Update Skill Points.  Modified 20 Nov 2002 by sage_sam
		// for bug #629643
		int spMod;
		spMod = recalcSkillPointMod(aPC, total);

		skillPool = new Integer(skillPool.intValue() + spMod);
		aPC.setSkillPoints(spMod + aPC.getSkillPoints());

		if (!aPC.isImporting())
		{
			//
			// Ask for stat increase after skill points have been calculated
			//
			if (levelUpStats > 0)
			{
				askForStatIncrease(aPC, levelUpStats, false);
			}

			if (newLevel == 1)
			{
				makeKitSelection(0); // try for 0-level kits first if adding first level
				makeRegionSelection(0);
			}
			makeKitSelection(newLevel);
			makeRegionSelection(newLevel);
		}

//Globals.debugPrint("addLevel:"+this.getName()+" spMod:"+spMod);

		// this is a monster class, so don't worry about experience
		if (isMonster())
		{
			return;
		}

		//removed for d20/OGL compliance
/*            int minXP = aPC.minXPForECL();
		if (aPC.getXP() < minXP)
		{
			aPC.setXP(minXP);
		}
		else if (aPC.getXP() >= aPC.minXPForNextECL())
		{
			if (!bSilent)
			{
				GuiFacade.showMessageDialog(null, Globals.getLevelUpMessage(), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			}
		}
*/
		//
		// Allow exchange of classes only when assign 1st level
		//
		if ((levelExchange.length() != 0) && (getLevel() == 1) && !aPC.isImporting())
		{
			final StringTokenizer aTok = new StringTokenizer(levelExchange, "|", false);
			if (aTok.countTokens() != 4)
			{
				Globals.debugPrint("levelExhange: invalid token count: ", aTok.countTokens());
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
						final int iLevel = aClass.getLevel();
						if (iLevel >= iMinLevel)
						{
							iMaxDonation = Math.min(Math.min(iMaxDonation, iLevel - iLowest), getMaxLevel() - 1);
							if (iMaxDonation > 0)
							{
								//
								// Build the choice list
								//
								final ArrayList choiceNames = new ArrayList();
								for (int i = 0; i <= iMaxDonation; ++i)
								{
									choiceNames.add(Integer.toString(i));
								}
								//
								// Get number of levels to exchange for this class
								//
								final ChooserInterface c = ChooserFactory.getChooserInstance();
								c.setTitle("Select number of levels to convert from " + sClass + " to " + getName());
								c.setPool(1);
								c.setPoolFlag(false);
								c.setAvailableList(choiceNames);
								c.show();

								final List selectedList = c.getSelectedList();
								int iLevels = 0;
								if (!selectedList.isEmpty())
								{
									iLevels = Integer.parseInt((String) selectedList.get(0));
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
					GuiFacade.showMessageDialog(null, "levelExchange:" + Constants.s_LINE_SEP + exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				}
			}
		}
	}

	public int recalcSkillPointMod(final PlayerCharacter aPC, int total)
	{
		int spMod;
		if (isMonster())
		{
			spMod = getMonsterSkillPointMod(aPC, total);
		}
		else
		{
			spMod = getNonMonsterSkillPointMod(aPC, total);
		}
		return spMod;
	}

	/*
	 * This method calculates skill modifier for a monster character.
	 *
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam
	 * for bug #629643 and updated to fix the bug.
	 */
	private int getMonsterSkillPointMod(final PlayerCharacter aPC, int total)
	{
		int spMod = 0;

		// Set the monster's base skills at the first level
		if (total == 0)
		{
			spMod = (int) aPC.getTotalBonusTo("MONSKILLPTS", "NUMBER", true);
		}
		// This is not the first level added...
		else
		{
			if (getExtraHD(aPC, total) > 0)
			{
				spMod = getSkillPoints();
				spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER", true);
				spMod = updateBaseSkillMod(aPC, spMod);
			}
		}

		if (spMod < 0)
		{
			spMod = 0;
		}

		return spMod;
	}

	/**
	 * This method can be called to determine if the number of extra HD
	 * for purposes of skill points, feats, etc. See MM p. 11
	 * extracted 03 Dec 2002 by sage_sam for bug #646816
	 * @param aPC currently selected PlayerCharacter
	 * @param hdTotal int number of monster HD the character has
	 * @return int number of HD considered "Extra"
	 */
	private int getExtraHD(final PlayerCharacter aPC, final int hdTotal)
	{
		// Determine the EHD modifier based on the size category
		final int sizeInt = Globals.sizeInt(aPC.getRace().getSize());
		int ehdMod;
		switch (sizeInt)
		{
			case 8: // Collossal
				ehdMod = 32;
				break;
			case 7: // Gargantuan
				ehdMod = 16;
				break;
			case 6: // Huge
				ehdMod = 4;
				break;
			case 5: // Large
				ehdMod = 2;
				break;
			default: // Medium and smaller
				ehdMod = 1;
				break;
		}

		// EHD = total HD - base HD for size (min of zero)
		return Math.max(0, hdTotal - ehdMod);
	}

	/*
	 * This method calculates skill modifier for a non-monster character.
	 *
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam
	 * for bug #629643
	 */
	private int getNonMonsterSkillPointMod(final PlayerCharacter aPC, int total)
	{
		int spMod = getSkillPoints();

		spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER", true);

		spMod = updateBaseSkillMod(aPC, spMod);

		if (total == 1)
		{
			if (SettingsHandler.isPurchaseStatMode())
			{
				aPC.setPoolAmount(0);
			}
			spMod *= Math.min(Globals.getSkillMultiplierForLevel(total), aPC.getRace().getInitialSkillMultiplier());
			BioSet.randomize("AGE");
		}
		else
		{
			spMod *= Globals.getSkillMultiplierForLevel(total);
		}
		return spMod;
	}

	void subLevel(boolean bSilent)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			int total = aPC.getTotalLevels();
			if (specialAbilityList != null && !specialAbilityList.isEmpty())
			{
				// remove any choice or SPECIALS: related special abilities the PC no longer qualifies for
				for (int i = specialAbilityList.size() - 1; i >= 0; --i)
				{
					SpecialAbility sa = (SpecialAbility) specialAbilityList.get(i);
					if (sa.getSource().startsWith("PCCLASS|") && !sa.pcQualifiesFor(aPC))
					{
						specialAbilityList.remove(sa);
					}
				}
			}
			int spMod = 0;

			if (!("MONSTER".equalsIgnoreCase(getType())))
			{
				if (isMonster() && !modToSkills)
				{
					spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel();
				}
				else
				{
					spMod = getSkillPoints() + aPC.getRace().getBonusSkillsPerLevel() + (int) aPC.getStatBonusTo("INTSKILLPOINTS", "NUMBER");
				}
				if (spMod < 1)
				{
					spMod = 1;
				}
			}


			// XXX Why is the feat decrementing done twice (here and in
			// subAddsForLevel())? The code works correctly, but I don't know
			// why.
			// Also, the use of instanceof is kinda ugly.
			if (levelAbilityList != null && !levelAbilityList.isEmpty())
			{
				for (Iterator e1 = levelAbilityList.iterator(); e1.hasNext();)
				{
					LevelAbility ability = (LevelAbility) e1.next();
					if (ability.level() == level && ability instanceof LevelAbilityFeat)
					{
						aPC.setFeats(aPC.getFeats() - 1);
					}
				}
			}
			final Integer zeroInt = new Integer(0);
			final int newLevel = level - 1;
			if (level > 0)
			{
				hitPointList[level - 1] = zeroInt;
			}
			setLevel(newLevel);
			if (isMonster())
			{
				if (levelsPerFeat != 0)
				{
					if ((aPC.totalHitDice() + 1) % levelsPerFeat == 0)
					{
						aPC.setFeats(aPC.getFeats() - 1);
					}
				}
			}

			doMinusLevelMods(aPC, newLevel + 1);

			modDomainsForLevel(newLevel, false);
			if (newLevel == 0)
			{
				setSubClassName(Constants.s_NONE);
				//
				// Remove all skills associated with this class
				//
				final ArrayList aSkills = aPC.getSkillList();
				for (int i = 0; i < aSkills.size(); ++i)
				{
					Skill aSkill = (Skill) aSkills.get(i);
					aSkill.setZeroRanks(this);
				}
				spMod = skillPool().intValue();
			}

			if (!isMonster() && total > aPC.getTotalLevels())
			{
				total = aPC.getTotalLevels();
				//removed for d20/OGL compliance
/*				int minXP = aPC.minXPForNextECL();
                        if (aPC.getXP() >= minXP)
				{
					// What does this do!??  --bko FIXME  It looks like you decrement the XP *twice*  Why?
					int less = minXP - 1;
					if (less >= 1)
					{
						--less;
					}
					else
					{
						less = 0;
					}
					aPC.setXP(less);
				}
*/
				if (total % 3 == 2)
				{
					aPC.setFeats(aPC.getFeats() - 1);
				}
				if (total % 4 == 3)
				{
					aPC.setPoolAmount(aPC.getPoolAmount() - 1);
					if (!bSilent && SettingsHandler.getShowStatDialogAtLevelUp())
					{
						if (SettingsHandler.isSkillIncrementBefore())
						{
							//
							// Ask user to select a stat to decrement.
							//
							final StringBuffer sStats = new StringBuffer();
							final StatList statList = aPC.getStatList();
							for (Iterator i = statList.getStats().iterator(); i.hasNext();)
							{
								final PCStat aStat = (PCStat) i.next();
								final int iAdjStat = statList.getTotalStatFor(aStat.getAbb());
								final int iCurStat = statList.getBaseStatFor(aStat.getAbb());
								sStats.append(aStat.getAbb()).append(": ").append(iCurStat);
								if (iCurStat != iAdjStat)
								{
									sStats.append(" adjusted: ").append(iAdjStat);
								}
								sStats.append(" (").append(statList.getStatModFor(aStat.getAbb())).append(")").append("\n");
							}
							final Object selectedValue = JOptionPane.showInputDialog(null, "Choose stat to decrement or select Cancel to decrement stat on the Stat tab." + "\n\n" + "Current Stats:" + "\n" + sStats + "\n", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE, null, Globals.s_ATTRIBLONG, Globals.s_ATTRIBLONG[0]);

							if (selectedValue != null)
							{
								for (Iterator i = statList.getStats().iterator(); i.hasNext();)
								{
									final PCStat aStat = (PCStat) i.next();
									if (aStat.getName().equalsIgnoreCase(selectedValue.toString()))
									{
										aStat.setBaseScore(aStat.getBaseScore() - 1);
										aPC.setPoolAmount(aPC.getPoolAmount() + 1);
										break;
									}
								}
							}
						}
						else
						{
							GuiFacade.showMessageDialog(null, "You lost a stat point due to level decrease. See the Stat tab.", Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
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
				aPC.getWeaponProfList().clear();
			}
			else
			{
				aPC.setSkillPoints(aPC.getSkillPoints() - spMod);
				skillPool = new Integer(skillPool().intValue() - spMod);
			}
			if (getLevel() == 0)
			{
				aPC.getClassList().remove(this);
			}
			aPC.validateCharacterDomains();
		}
		else
		{
			Globals.debugPrint("No current pc in subLevel()? How did this happen?");
			return;
		}
	}

	private void modDomainsForLevel(final int aLevel, final boolean adding)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		// any domains set by level would have already been saved
		// and don't need to be re-set at level up time
		if (aPC.isImporting())
		{
			return;
		}
		int c = 2;
		if (aLevel > 9)
		{
			c = 3;
		}
		if (domainList.isEmpty())
		{
			return;
		}
		for (Iterator i = domainList.iterator(); i.hasNext();)
		{
			final String aString = (String) i.next();
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
					if (!inPreReqs && !"[".equals(bString) && !"|".equals(bString))
					{
						aName = bString;
					}
					d += bString.length();
					if (bTok.hasMoreTokens())
					{
						if ("[".equals(aString.substring(d, d + 1)))
						{
							addNow = false;
						}
					}
					else
					{
						addNow = true;
					}
					if ("[".equals(bString))
					{
						inPreReqs = true;
					}
					else if ("]".equals(bString))
					{ // this ends a PRExxx tag so next time through we can add name
						addNow = true;
						inPreReqs = false;
					}
					if (addNow && !adding)
					{
						int l = aPC.getCharacterDomainIndex(aName);
						if (l > -1)
						{
							aPC.getCharacterDomainList().remove(l);
						}
					}
					else if (adding && addNow && aName.length() > 0)
					{
						int l = aPC.getCharacterDomainIndex(aName);
						int j = aPC.getFirstEmptyCharacterDomain();
						if (l == -1 && j >= 0)
						{
							final Domain aDomain = Globals.getDomainNamed(aName);
							final CharacterDomain aCD = (CharacterDomain) aPC.getCharacterDomainList().get(j);
							if (preReqList.size() == 0 || (aDomain != null && aDomain.passesPreReqTestsForList(preReqList)))
							{
								aCD.setDomain(aDomain);
							}
						}
						preReqList.clear();
						aName = "";
					}
					if (adding && inPreReqs && (bString.startsWith("PRE") || bString.startsWith("!PRE")))
					{
						preReqList.add(bString);
					}
				}
			}
		}
	}

	int memorizedSpellForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final ArrayList aList = getCharacterSpell(null, bookName, aLevel);
		if (aList.isEmpty())
		{
			return m;
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell) i.next();
			if (Globals.isSSd20Mode())
			{
				m += cs.getSpell().getCastingThreshold();
			}
			else
			{
				m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
			}
		}
		return m;
	}

	private void changeSpecials()
	{
		if (specialsString().length() == 0)
		{
			return;
		}
		String className = "";
		Integer adj = new Integer(0);
		String abilityName = "";
		String levelString = "";
		StringTokenizer aTok = new StringTokenizer(specialsString, "|", false);
		final ArrayList saList = new ArrayList();
		if (aTok.hasMoreTokens())
		{
			abilityName = aTok.nextToken();
		}
		if (aTok.hasMoreTokens())
		{
			className = aTok.nextToken();
		}
		if (aTok.hasMoreTokens())
		{
			aTok.nextToken(); // adj will be summed later
		}
		if (aTok.hasMoreTokens())
		{
			levelString = aTok.nextToken();
		}
		// first, remove all special abilities by this name
		Iterator iter;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		// next, determine total 'levels' of ability
		for (iter = aPC.getClassList().iterator(); iter.hasNext();)
		{
			final PCClass aClass = (PCClass) iter.next();
			if (aClass.specialsString().length() > 0 && aClass.specialsString().startsWith(abilityName))
			{
				aTok = new StringTokenizer(aClass.specialsString(), "|", false);
				aTok.nextToken();
				aTok.nextToken();
				if (aTok.hasMoreTokens())
				{
					adj = new Integer(adj.intValue() + Integer.parseInt(aTok.nextToken()) + aClass.getLevel());
				}
				if (aTok.hasMoreTokens())
				{
					levelString = aTok.nextToken(); // need this
				}
			}
		}
		// next add abilities for level based upon levelString
		PCClass aClass = aPC.getClassNamed(className);
		if (aClass == null)
		{
			for (iter = Globals.getClassList().iterator(); iter.hasNext();)
			{
				aClass = (PCClass) iter.next();
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
			for (int i = saList.size() - 1; i >= 0; --i)
			{
				final SpecialAbility sa1 = (SpecialAbility) saList.get(i);
				String sn1 = sa1.getDesc();
				for (int k = 0; k < 10; ++k)
				{
					sn1 = sn1.replace((char) ('0' + k), ' ');
				}
				String sn2;
				for (int j = i - 1; j >= 0; --j)
				{
					final SpecialAbility sa2 = (SpecialAbility) saList.get(j);
					sn2 = sa2.getDesc();
					for (int k = 0; k < 10; ++k)
					{
						sn2 = sn2.replace((char) ('0' + k), ' ');
					}
					if (sn1.equals(sn2))
					{
						saList.remove(j);
						i -= 1;			// Just shifted contents of ArrayList down 1
					}
				}
			}
		}
		if (!saList.isEmpty())
		{
			final String sourceName = "PCCLASS|" + name + "|" + level;
			for (iter = saList.iterator(); iter.hasNext();)
			{
				SpecialAbility sa = (SpecialAbility) iter.next();
				sa.setSource(sourceName);
				if (aClass != null && !aPC.hasSpecialAbility(sa))
				{
					aClass.addSpecialAbilityToList(sa);
				}
			}
		}
	}

	private void addVariablesForLevel(final int aLevel)
	{
		if (getVariableCount() == 0)
		{
			return;
		}
		if (aLevel == 1)
		{
			addVariablesForLevel(0);
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		String aString;
		StringTokenizer aTok;
		String bString;
		final String prefix = "CLASS:" + name + '|';
		for (int i = 0; i < getVariableCount(); ++i)
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

	final Integer[] getHitPointList()
	{
		return hitPointList;
	}

	final void setHitPointList(final Integer[] newList)
	{
		hitPointList = newList;
	}

	int hitPoints(int iConMod)
	{
		int total = 0;
		final int levelValue = level;
		for (int i = 0; i < levelValue && i < hitPointList.length; ++i)
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

	/**
	 * Rolls hp for the current level according to the rules set in options.
	 */
	private void rollHP()
	{
		int roll = 0;
		ArrayList aTempList;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN", true) + (int) aPC.getTotalBonusTo("HD", "MIN;CLASS=" + name, true);
		int max = getHitDie() + (int) aPC.getTotalBonusTo("HD", "MAX", true) + (int) aPC.getTotalBonusTo("HD", "MAX;CLASS=" + name, true);
		// Do we have a template which gives us a different hit die?  Let's find out.
		// sk4p 11 Dec 2002
		aTempList = aPC.getTemplateList();
		if (!aTempList.isEmpty())
		{
			for (Iterator e = aTempList.iterator(); e.hasNext();)
			{
				final PCTemplate template = (PCTemplate) e.next();
				if (template != null)
				{
					if (max < template.getHitDiceSize())
					{
						max = template.getHitDiceSize();
					}
				}
			}
		}

		//Shouldn't really have to be called. I think this should be handled by the level raising code.
		fixHitpointList();

		final int totalLevels = aPC.getTotalLevels();

		if (totalLevels == 0)
		{
			hitPointList[0] = new Integer(0);
		}
		else if (totalLevels == 1 && SettingsHandler.isHPMaxAtFirstLevel())
		{
			roll = max;
		}
		else
		{
			final int hpRollMethod = SettingsHandler.getHPRollMethod();
			switch (hpRollMethod)
			{
				case Constants.s_HP_STANDARD:
/////////////////////////////////////////////////
// Yanked for WotC compliance
				default:
//					roll = Math.abs(Globals.getRandomInt(max - min + 1)) + min;
					if (!aPC.isImporting() && SettingsHandler.getShowHPDialogAtLevelUp())
					{
						final Object[] rollChoices = new Object[max - min + 2];
						rollChoices[0] = Constants.s_NONESELECTED;
						for (int i = min; i <= max; ++i)
						{
							rollChoices[i - min + 1] = new Integer(i);
						}
						for (; ;)
						{
							Object selectedValue = JOptionPane.showInputDialog(null, "Randomly generate a number between " + min + " and " + max + "." + Constants.s_LINE_SEP + "Select it from the box below.", Globals.getGameModeHitPointText() + " for " + Utility.ordinal(getLevel()) + " level of " + getName(), JOptionPane.INFORMATION_MESSAGE, null, rollChoices, null /*rollChoices[(min + max - 1) / 2]*/);

							if ((selectedValue != null) && (selectedValue instanceof Integer))
							{
								roll = ((Integer) selectedValue).intValue();
								break;
							}
						}
					}
/////////////////////////////////////////////////
					break;
				case Constants.s_HP_AUTOMAX:
					roll = max;
					break;
/////////////////////////////////////////////////
// Yanked for WotC compliance
//				case Constants.s_HP_PERCENTAGE:
//					float pct = Globals.getHPPct();
//					float maxFloat = max;
//					float minFloat = min;
//					roll = (int)((pct / 100.0) * (maxFloat - minFloat) + minFloat);
//					break;
//				case Constants.s_HP_LIVING_GREYHAWK:
//					if (totalLevels == 1)
//						roll = max;
//					else
//						roll = (int)Math.floor((max + min) / 2) + 1;
//					break;
//				case Constants.s_HP_LIVING_CITY:
//					if (totalLevels == 1 || totalLevels == 2)
//						roll = max;
//					else
//					{
//						roll = (int)Math.floor(3 * max / 4);
//						// In the bizarre case a class has a max of 1, need to fix that Floor will make that 0 instead.
//						if (roll < min) roll = min;
//					}
//					break;
//				default:
//					Globals.errorPrint("In PCClass.rollHP the hpRollMethod " + hpRollMethod + " is unsupported.");
//					break;
/////////////////////////////////////////////////
			}
		}
		roll += ((int) aPC.getTotalBonusTo("HP", "CURRENTMAXPERLEVEL", true));
		hitPointList[level - 1] = new Integer(roll);
		aPC.setCurrentHP(aPC.hitPoints());
	}

	private void fixHitpointList()
	{
		if (hitPointList.length < level)
		{
			final Integer[] newList = new Integer[level];
			System.arraycopy(hitPointList, 0, newList, 0, hitPointList.length);

			int i = hitPointList.length;
			while (i < newList.length)
			{
				newList[i++] = new Integer(0);
			}
			setHitPointList(newList);
		}
	}

	int baseAttackBonus()
	{
		if (level == 0)
		{
			return 0;
		}
		//final int i = (int) this.getBonusTo("TOHIT", "TOHIT", level) + (int) getBonusTo("COMBAT", "BAB");
		final int i = (int) getBonusTo("COMBAT", "BAB");
		return i;
	}

	private int checkBonus(String checkName, boolean includeBonus)
	{
		for (int i = 0; i < Globals.getCheckList().size(); ++i)
		{
			if (checkName.equalsIgnoreCase(Globals.getCheckList().get(i).toString()))
			{
				int bonus = (int) getBonusTo("CHECKS", "BASE." + checkName);
				if (includeBonus)
				{
					bonus += (int) getBonusTo("CHECKS", checkName);
				}
				return bonus;
			}
		}
		Globals.errorPrint(checkName + " is not a valid Check! Returning -1");
		return -1; // not a valid check
	}

	int checkBonus(int index, boolean includeBonus)
	{
		if (index >= 0 && index < Globals.getCheckList().size())
		{
			return checkBonus(Globals.getCheckList().get(index).toString(), includeBonus);
		}
		Globals.errorPrint("Invalid check index of " + index + ". Returning -1");
		return -1; // not a valid check
	}

	public String classLevelString()
	{
		StringBuffer aString = new StringBuffer();
		if (!getSubClassName().equals(Constants.s_NONE) && !"".equals(getSubClassName()))
		{
			aString.append(getSubClassName());
		}
		else
		{
			aString.append(getName());
		}
		aString = aString.append(' ').append(level);
		return aString.toString();
	}

	public void addFeatList(final int aLevel, final String aFeatList)
	{
		final String aString = aLevel + ":" + aFeatList;
		featList.add(aString);
	}

	/*
	 * This method updates the base skill modifier based on stat
	 * bonus, race bonus, and template bonus.
	 * Created(Extracted from addLevel) 20 Nov 2002 by sage_sam
	 * for bug #629643
	 */
	private int updateBaseSkillMod(final PlayerCharacter aPC, int spMod)
	{
		// skill min is 1, unless class gets 0 skillpoints per level (for second apprentice class)
		final int skillMin = (spMod > 0) ? 1 : 0;

		if (modToSkills)
		{
			spMod += (int) aPC.getStatBonusTo("MODSKILLPOINTS", "NUMBER");
			if (spMod < 1)
			{
				spMod = 1;
			}
		}
		//Race modifiers apply after Intellegence. BUG 577462
		spMod += aPC.getRace().getBonusSkillsPerLevel();
		spMod = Math.max(skillMin, spMod);  //Minimum 1, not sure if bonus skills per
		// level can be < 1, better safe than sorry

		if (!aPC.getTemplateList().isEmpty())
		{
			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate) e.next();
				spMod += aTemplate.getBonusSkillsPerLevel();
			}
		}
		return spMod;
	}

	final Collection vFeatList()
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

	private static String getToken(int tokenNum, final String aList, final String delim)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, delim, false);
		while (aTok.hasMoreElements() && tokenNum >= 0)
		{
			final String aString = aTok.nextToken();
			if (tokenNum == 0)
			{
				return aString;
			}
			--tokenNum;
		}
		return null;
	}

	/**
	 * This method adds or deletes feats for a level.
	 * @param aLevel the level to affect
	 * @param addThem whether to add or remove feats
	 */
	private void changeFeatsForLevel(final int aLevel, final boolean addThem)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || featList.isEmpty())
		{
			return;
		}
		for (Iterator e = featList.iterator(); e.hasNext();)
		{
			final String feats = (String) e.next();
			if (aLevel == Integer.parseInt(getToken(0, feats, ":")))
			{
				final int preFeatCount = aPC.getUsedFeatCount();
				aPC.modFeatsFromList(getToken(1, feats, ":"), addThem, aLevel == 1);
				final int postFeatCount = aPC.getUsedFeatCount();
				//
				// Adjust the feat count by the total number that were given
				//
				aPC.setFeats(aPC.getFeats() + postFeatCount - preFeatCount);

			}
		}
	}

	protected void inheritAttributesFrom(PCClass otherClass)
	{
		if (otherClass.getSpellBaseStat() != null)
		{
			setSpellBaseStat(otherClass.getSpellBaseStat());
		}
		if (otherClass.classSpellString != null)
		{
			classSpellString = otherClass.classSpellString;
		}
		if (otherClass.autoArray != null)
		{
			addAutoArray(otherClass.autoArray);
		}
		if (!otherClass.getBonusList().isEmpty())
		{
			bonusList.addAll(otherClass.getBonusList());
		}
		if (otherClass.variableList != null)
		{
			variableList.addAll(otherClass.variableList);
		}
		if (otherClass.getCSkillList() != null)
		{
			cSkillList = otherClass.getCSkillList();
		}
		if (otherClass.getCcSkillList() != null)
		{
			ccSkillList = otherClass.getCcSkillList();
		}
		if (otherClass.kitString != null)
		{
			setKitString(otherClass.kitString);
		}
		if (otherClass.regionString != null)
		{
			setRegionString(otherClass.regionString);
		}
		if (otherClass.getSpecialAbilityList() != null)
		{
			specialAbilityList.addAll(otherClass.getSpecialAbilityList());
		}
		if (otherClass.DR != null)
		{
			DR = (ArrayList) otherClass.DR.clone();
		}
		if (otherClass.SR != null)
		{
			SR = (ArrayList) otherClass.SR.clone();
		}
		if (otherClass.vision != null)
		{
			vision = otherClass.vision;
		}
		if (otherClass instanceof SubClass)
		{
			((SubClass) otherClass).applyLevelArrayModsTo(this);
		}
	}

	private void checkForSubClass()
	{
		if (!hasSubClass || (subClassList == null) || (subClassList.isEmpty()))
		{
			return;
		}

		final List choiceNames = new ArrayList();
		choiceNames.add("Name\tCost\tOther");
		choiceNames.add("");
		final List removeNames = new ArrayList();
		for (Iterator i = subClassList.iterator(); i.hasNext();)
		{
			final SubClass sc = (SubClass) i.next();
			if (!sc.passesPreReqTests())
			{
				continue;
			}
			boolean added = false;
			StringBuffer buf = new StringBuffer();
			buf.append(sc.getName()).append('\t').append(sc.getCost()).append('\t');
			if (sc.getNumSpellsFromSpecialty() != 0)
			{
				buf.append("SPECIALTY SPELLS:" + sc.getNumSpellsFromSpecialty());
				added = true;
			}
			if (sc.getSpellBaseStat() != null)
			{
				buf.append("SPELL BASE STAT:").append(sc.getSpellBaseStat());
				added = true;
			}
			if (!added)
			{
				buf.append(' ');
			}
			if (sc.getCost() == 0)
			{
				removeNames.add(buf.toString());
			}
			choiceNames.add(buf.toString());
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setMessageText("Make a selection.  The cost column indicates the cost of that selection. " + "If this cost is non-zero, you will be asked to also " + "select items from this list to give up to cover that cost.");
		c.setPool(1);
		c.setPoolFlag(false);
		//c.setCostColumnNumber(1);		// Allow 1 choice, regardless of cost...cost will be applied in second phase
		c.setAvailableList(choiceNames);
		if (choiceNames.size() == 1)
		{
			c.setSelectedList(choiceNames);
		}
		else if (choiceNames.size() != 0)
		{
			c.show();
		}

		List selectedList = c.getSelectedList();
		if (!selectedList.isEmpty())
		{
			setProhibitedString("");
			specialtyList.clear();
			StringTokenizer aTok = new StringTokenizer((String) selectedList.get(0), "\t", false);
			SubClass sc = getSubClassNamed(aTok.nextToken());
			choiceNames.remove(selectedList.get(0));
			choiceNames.removeAll(removeNames);
			setSubClassName(sc.getName());
			if (sc.getChoice().length() > 0)
			{
				specialtyList.add(sc.getChoice());
			}
			if (sc.getCost() != 0)
			{
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setAvailableList(choiceNames);
				c1.setMessageText("Make a selection.  You must make as many selections " + "necessary to cover the cost of your previous selections.");
				c1.setPool(sc.getCost());
				c1.setPoolFlag(true);
				c1.setCostColumnNumber(1);
				c1.setNegativeAllowed(true);
				c1.show();
				selectedList = c1.getSelectedList();
				for (Iterator i = selectedList.iterator(); i.hasNext();)
				{
					aTok = new StringTokenizer((String) i.next(), "\t", false);
					sc = getSubClassNamed(aTok.nextToken());
					if (prohibitedString.length() > 0)
					{
						prohibitedString = prohibitedString.concat(",");
					}
					prohibitedString = prohibitedString.concat(sc.getChoice());
				}
			}

		}
	}

	public final Integer skillPool()
	{
		return skillPool;
	}

	public void setSkillPool(final int i)
	{
		skillPool = new Integer(i);
	}

	private String specialsString()
	{
		return specialsString;
	}

	public final void setSpecialsString(final String aString)
	{
		specialsString = aString;
	}

	public final Collection skillList()
	{
		return skillList;
	}

	public final void setClassSkillString(String aString)
	{
		classSkillString = aString;
	}

	private void chooseClassSkillList()
	{
		// if no entry or no choices, just return
		if (classSkillString == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(classSkillString, "|", false);
		int amt = 0;
		if (classSkillString.indexOf('|') >= 0)
		{
			amt = Integer.parseInt(aTok.nextToken());
		}
		final ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
		if (aList.size() == 1)
		{
			classSkillList = aList;
			return;
		}
		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("Select class whose class-skills this class will inherit");
		c.setPool(amt);
		c.setPoolFlag(false);
		c.setAvailableList(aList);
		c.show();
		final List selectedList = c.getSelectedList();
		classSkillList = new ArrayList();
		for (Iterator i = selectedList.iterator(); i.hasNext();)
		{
			final String aString = i.next().toString();
			classSkillList.add(aString);
		}
	}

	final ArrayList getClassSkillList()
	{
		return classSkillList;
	}

	public final String defenseString()
	{
		return defenseString;
	}

	public final void setDefenseString(final String aString)
	{
		defenseString = aString;
	}

	public Integer defense(final int y)
	{
		final String aString = defenseString;
		int i = 0;
		if (aString.length() > 0 && aString.indexOf(',') >= 0)
		{
			final int k = Integer.parseInt(aString.substring(0, aString.indexOf(',')));
			final int m = Integer.parseInt(aString.substring(aString.lastIndexOf(',') + 1));
			if (y > 0)
			{
				i += m;
			}
			final int levelValue = getLevel();
			switch (k)
			{
/*Best*/			case 0:
					i += 4 + levelValue >> 1;
					break;
/*Middle*/
				case 1:
/*Prestige*/
				case 4:
/*Prestige2*/
				case 5:
					i += 3 + levelValue / 5;
					if (i >= 2)
						i += (levelValue + 3) / 5;
					if (k == 4)
						i -= 2;
					if (k == 5)
						i -= 1;
					break;
/*Low*/
				case 2:
					i += 2 + levelValue / 3;
					break;
/*NPC*/
				case 3:
					i += levelValue / 3;
					break;
/*Prestige5*/
				case 8:
					i += 2 + (((levelValue + 1) + 3) / 3);
					break;
/*Prestige6*/
				case 9:
					i += 2 + ((levelValue + 3) / 3);
					if (levelValue == 8)
						i += 1;
					break;
/*Prestige7*/
				case 10:
					i += 1 + ((levelValue + 1) >> 1);
					break;
/*Prestige8*/
				case 11:
					i += 2 + ((levelValue + 1) >> 1);
					break;
/*Prestige9*/
				case 12:
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
							Globals.errorPrint("In PCClass.defense the levelValue " + levelValue + " is unsupported.");
							break;
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
					i += 1 + ((levelValue + 1) >> 1);
					break;
				case 23:
					i += 2 + ((levelValue + 1) >> 1);
					break;
// New Prestige Classes
				case 24:
					i += ((2 * (levelValue + 3)) / 5);
					break;
				case 25:
					i += 1 + (levelValue >> 1);
					break;
				case 26:
					i += 2 + (levelValue >> 1);
					break;
// New Codes for Spycraft (30 - 32)
				case 30:
					i += 1 + (((levelValue - 1) * 3) / 5);
					break;
				case 31:
					i += ((3 * levelValue) + 4) >> 2;
					if (levelValue == 4)
						i -= 1;
					if (levelValue == 8)
						i -= 1;
					if (levelValue == 17)
						i += 1;
					break;
				case 32:
					i += (2 * ((levelValue - 2) + 3)) / 5;
					break;
					//What to do here?
				default:
					i = 0;
					break;
			}
		}
		i += (int) getBonusTo("CLASS", "DEFENSE", level);
		return new Integer(i);
	}

	public final String getReputationString()
	{
		return reputationString;
	}

	public final void setReputationString(final String aString)
	{
		reputationString = aString;
	}

	public final String getExClass()
	{
		return exClass;
	}

	public final void setExClass(final String aString)
	{
		exClass = aString;
	}

	public final void setLevelExchange(final String aString)
	{
		levelExchange = aString;
	}

	public void addKnownSpellsList(final String aString)
	{
		final StringTokenizer aTok;
		if (aString.startsWith("CLEAR."))
		{
			knownSpellsList.clear();
			if ("CLEAR.".equals(aString))
			{
				return;
			}
			aTok = new StringTokenizer(aString.substring(6), "|", false);
		}
		else
		{
			aTok = new StringTokenizer(aString, "|", false);
		}
		while (aTok.hasMoreTokens())
		{
			knownSpellsList.add(aTok.nextToken());
		}
	}

	private boolean isAutoKnownSpell(final String spellName, final int spellLevel, final boolean useMap)
	{
		if (knownSpellsList.isEmpty())
		{
			return false;
		}
		final Spell aSpell = Globals.getSpellNamed(spellName);
		if (useMap)
		{
			final Object val = castForLevelMap.get(String.valueOf(spellLevel));
			if (val == null || Integer.parseInt(val.toString()) == 0 || aSpell == null)
			{
				return false;
			}
		}
		else if (getCastForLevel(level, spellLevel) == 0 || aSpell == null)
		{
			return false;
		}
		if (isProhibited(aSpell) && !isSpecialtySpell(aSpell))
		{
			return false;
		}
		boolean flag = true;
		// iterate through the KNOWNSPELLS: tag
		for (Iterator e = knownSpellsList.iterator(); e.hasNext();)
		{
			String aString = (String) e.next();
			flag = true;
			final StringTokenizer spellTok = new StringTokenizer(aString, ",", false);
			// must satisfy all elements in a comma delimited list
			while (spellTok.hasMoreTokens() && flag)
			{
				final String bString = spellTok.nextToken();
				// if the argument starts with LEVEL=, compare the level to the desired spellLevel
				if (bString.startsWith("LEVEL="))
				{
					flag = Integer.parseInt(bString.substring(6)) == spellLevel;
				}
				// if it starts with TYPE=, compare it to the spells type list
				else if (bString.startsWith("TYPE="))
				{
					flag = aSpell.isType(bString.substring(5));
				}
				// otherwise it must be the spell's name
				else
				{
					flag = bString.equals(spellName);
				}
			}
			// if we found an entry in KNOWNSPELLS: that is satisfied, we can stop
			if (flag)
			{
				break;
			}
		}
		return flag;
	}

	public boolean isAutoKnownSpell(final String spellName, final int spellLevel)
	{
		return isAutoKnownSpell(spellName, spellLevel, false);
	}

	public final void setAttackCycle(final String aString)
	{
		attackCycle = aString;
	}

	int attackCycle(final int index)
	{
		final StringTokenizer aTok = new StringTokenizer(attackCycle, "|", false);
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (((index == Constants.ATTACKSTRING_MELEE) && "BAB".equals(aString)) || ((index == Constants.ATTACKSTRING_RANGED) && "RAB".equals(aString)) || ((index == Constants.ATTACKSTRING_UNARMED) && "UAB".equals(aString)))
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
		{
			return false;
		}
		if (isMonster() && preRaceType != null && !contains(aPC.getCritterType(), preRaceType))
		// Move the check for type out of race and into PlayerCharacter to make it easier for a template to adjust it.
		{
//			Globals.debugPrint("PreReq:" + aPC.getRace().getName());
//			Globals.debugPrint("PRERACETYPE:" + preRaceType + " RACETYPE:" + aPC.getCritterType() + " false");
			return false;
		}
		if (!canBePrestige())
		{
			return false;
		}
		return true;
	}

	private static boolean contains(String big, String little)
	{
		return big.indexOf(little) >= 0;
	}

	public boolean isPrestige()
	{
		return isType("PRESTIGE");
	}

	boolean isPC()
	{
		return (getMyTypeCount() == 0 || isType("PC"));
	}

	boolean isNPC()
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

	void addInitMod(int initModDelta)
	{
		initMod += initModDelta;
	}

	private static class LevelProperty
	{
		private int level = 0;
		private String property = "";

		LevelProperty()
		{
			super();
		}

		LevelProperty(int argLevel, String argProperty)
		{
			level = argLevel;
			property = argProperty;
		}

		public final int getLevel()
		{
			return level;
		}

		public final String getProperty()
		{
			return property;
		}
	}

	// should be "5|4/-" where 5 = level, 4/- is the DR value
	public void setDR(String drString)
	{
		StringTokenizer aTok = new StringTokenizer(drString, "|", false);
		int lvl = Integer.parseInt(aTok.nextToken());
		drString = aTok.nextToken();
		if (".CLEAR".equals(drString))
		{
			DR = null;
		}
		else
		{
			if (DR == null)
			{
				DR = new ArrayList();
			}
			LevelProperty lp = new LevelProperty(lvl, drString);
			DR.add(lp);
		}
	}

	//
	// Assumption: DR list is sorted by level
	//
	public String getDR()
	{
		LevelProperty lp = null;
		if (DR != null)
		{
			int lvl = level;
			for (int i = 0, x = DR.size(); i < x; ++i)
			{
				if (((LevelProperty) DR.get(i)).getLevel() > lvl)
				{
					break;
				}
				lp = (LevelProperty) DR.get(i);
			}
		}
		if (lp != null)
		{
			return lp.getProperty();
		}
		return null;
	}

	// should be "5|4/-" where 5 = level, 4/- is the SR value
	public void setSR(String srString)
	{
		StringTokenizer aTok = new StringTokenizer(srString, "|", false);
		int lvl = Integer.parseInt(aTok.nextToken());
		srString = aTok.nextToken();
		if (".CLEAR".equals(srString))
		{
			SR = null;
		}
		else
		{
			if (SR == null)
			{
				SR = new ArrayList();
			}
			LevelProperty lp = new LevelProperty(lvl, srString);
			SR.add(lp);
		}
	}

	//
	// Assumption: SR list is sorted by level
	//
	public String getSRFormula()
	{
		LevelProperty lp = null;
		if (SR != null)
		{
			int lvl = level;
			for (int i = 0, x = SR.size(); i < x; ++i)
			{
				if (((LevelProperty) SR.get(i)).getLevel() > lvl)
				{
					break;
				}
				lp = (LevelProperty) SR.get(i);
			}
		}
		if (lp != null)
		{
			return lp.getProperty();
		}
		return null;
	}

	public void addSpells(int level, String line)
	{
		super.addSpells(level, line);
		for (int i = 0; i < spellList.size(); ++i)
		{
			Object obj = spellList.get(i);
			if (!(obj instanceof LevelProperty))
			{
				LevelProperty lp = new LevelProperty(level, obj.toString());
				spellList.set(i, lp);
			}
		}
	}

	/**
	 * Update the name of the required class for all special abilites, DEFINE's, and BONUS's
	 *
	 * @param oldClass The name of the class that should have the special abliities changed
	 * @param newClass The name of the new class for the altered special abilities
	 */
	void fireNameChanged(final String oldClass, final String newClass)
	{
		//
		// This gets called on clone(), so don't traverse the list if the names are the same
		//
		if (oldClass.equals(newClass))
		{
			return;
		}

		//
		// Go through the specialty list (SA) and adjust the class to the new name
		//
		if (specialAbilityList != null)
		{
			for (int idx = specialAbilityList.size() - 1; idx >= 0; --idx)
			{
				SpecialAbility sa = (SpecialAbility) specialAbilityList.get(idx);
				if (sa.getSource().length() != 0)
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("Replacing source on SA:" + sa.getName() + ". " + oldClass + "-->" + newClass);
					}
					sa = new SpecialAbility(sa.getName(), sa.getSource(), sa.getDesc());
					sa.setQualificationClass(oldClass, newClass);
					specialAbilityList.set(idx, sa);
				}
			}
		}

		//
		// Go through the variable list (DEFINE) and adjust the class to the new name
		//
		if (variableList != null)
		{
			for (int idx = variableList.size() - 1; idx >= 0; --idx)
			{
				String var = (String) variableList.get(idx);
				int offs = -1;
				for (;;)
				{
					offs = var.indexOf('=' + oldClass, offs + 1);
					if (offs < 0)
					{
						break;
					}
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("Replacing source in DEFINE:" + var + ". " + oldClass + "-->" + newClass);
					}
					var = var.substring(0, offs + 1) + newClass + var.substring(offs + oldClass.length() + 1);
					variableList.set(idx, var);
				}
			}
		}

		//
		// Go through the bonus list (BONUS) and adjust the class to the new name
		//
		if (bonusList != null)
		{
			for (int idx = bonusList.size() - 1; idx >= 0; --idx)
			{
				String bonus = (String) bonusList.get(idx);
				int offs = -1;
				for (;;)
				{
					offs = bonus.indexOf('=' + oldClass, offs + 1);
					if (offs < 0)
					{
						break;
					}
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("Replacing source in BONUS:" + bonus + ". " + oldClass + "-->" + newClass);
					}
					bonus = bonus.substring(0, offs + 1) + newClass + bonus.substring(offs + oldClass.length() + 1);
					bonusList.set(idx, bonus);
				}
			}
		}
	}

	public ArrayList getSpellList()
	{
		ArrayList spells = new ArrayList();
		LevelProperty lp;
		if (spellList != null)
		{
			for (int i = 0, x = spellList.size(); i < x; ++i)
			{
				lp = (LevelProperty) spellList.get(i);
				if (lp.getLevel() > level)
				{
					break;
				}
				spells.add(lp.getProperty());
			}
		}
		return spells;
	}

	public final void setSpellBookUsed(boolean argUseBook)
	{
		usesSpellbook = argUseBook;
	}

	final boolean getSpellBookUsed()
	{
		return usesSpellbook;
	}

	/**
	 * Added to help deal with lower-level spells prepared in higher-level slots.
	 * BUG [569517]
	 * Works in conjunction with PlayerCharacter method availableSpells()
	 * sk4p 13 Dec 2002
	 */
	int memorizedSpecialtiesForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final ArrayList aList = getCharacterSpell(null, bookName, aLevel);
		if (aList.isEmpty())
		{
			return m;
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell) i.next();
			if (Globals.isSSd20Mode())
			{
				if (cs.isSpecialtySpell())
				{
					m += cs.getSpell().getCastingThreshold();
				}
			}
			else
			{
				if (cs.isSpecialtySpell())
				{
					m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
				}
			}
		}
		return m;
	}

	//
	// Ask user to select a stat to increment. This can happen before skill points
	// are calculated, so an increase to the appropriate stat can give more skill points
	//
	private final int askForStatIncrease(final PlayerCharacter aPC, int statsToChoose, final boolean isPre)
	{
		//
		// If 1st time here (checks for preincrement), then will only ask if want to ask before level up
		// If 2nd time here, will ask if there are any remaining points unassigned.
		// So, hitting cancel on the 1st popup will cause the 2nd popup to ask again.
		// This is to handle cases where the user is adding multiple levels, so the SKILL point total
		// won't be too messed up
		//
		if (isPre)
		{
			if (!SettingsHandler.isSkillIncrementBefore())
			{
				return statsToChoose;
			}
		}

		String extraMsg = "";
		if (isPre)
		{
			extraMsg = "\nRaising a stat here may award more skill points.";
		}
		int iCount = 0;
		for (int ix = 0; ix < statsToChoose; ++ix)
		{
			StringBuffer sStats = new StringBuffer();
			for (Iterator i = aPC.getStatList().getStats().iterator(); i.hasNext();)
			{
				final PCStat aStat = (PCStat) i.next();
				final int iAdjStat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
				final int iCurStat = aPC.getStatList().getBaseStatFor(aStat.getAbb());
				sStats.append(aStat.getAbb()).append(": ").append(iCurStat);
				if (iCurStat != iAdjStat)
				{
					sStats.append(" adjusted: ").append(iAdjStat);
				}
				sStats.append(" (").append(aPC.getStatList().getStatModFor(aStat.getAbb())).append(")\n");
			}
			Object selectedValue = JOptionPane.showInputDialog(null, "Choose stat to increment or select Cancel to increment stat on the Summary tab." + extraMsg + "\n\n" + "Current Stats:\n" + sStats + "\n", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE, null, Globals.s_ATTRIBLONG, Globals.s_ATTRIBLONG[0]);
			if (selectedValue != null)
			{
				for (Iterator i = aPC.getStatList().getStats().iterator(); i.hasNext();)
				{
					final PCStat aStat = (PCStat) i.next();
					if (aStat.getName().equalsIgnoreCase(selectedValue.toString()))
					{
						aPC.saveStatIncrease(aStat.getAbb(), 1, isPre);
						aStat.setBaseScore(aStat.getBaseScore() + 1);
						aPC.setPoolAmount(aPC.getPoolAmount() - 1);
						++iCount;
						break;
					}
				}
			}
		}
		return statsToChoose - iCount;
	}

	void doGlobalUpdate(final String aString)
	{
		//add to global PCClassType list for future filtering
		if (!Globals.getPCClassTypeList().contains(aString))
		{
			Globals.getPCClassTypeList().add(aString);
		}
	}

}
