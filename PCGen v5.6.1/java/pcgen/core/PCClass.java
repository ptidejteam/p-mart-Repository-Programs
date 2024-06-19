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
 * $Id: PCClass.java,v 1.1 2006/02/21 01:33:15 vauchers Exp $
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserInterface;
import pcgen.gui.utils.GuiFacade;
import pcgen.util.Logging;
import pcgen.core.prereq.PrereqHandler;
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
	protected String bonusSpellBaseStat = Constants.s_NONE;
	private String spellType = Constants.s_NONE;
	private String attackBonusType = "O";
	private ArrayList specialtyknownList = new ArrayList();
	private ArrayList knownList = new ArrayList();
	private Map castMap = new HashMap();
	private ArrayList uattList = new ArrayList();
	private ArrayList acList = new ArrayList();
	private TreeSet languageBonus = new TreeSet();
	private ArrayList featAutos = new ArrayList();
	private ArrayList weaponProfBonus = new ArrayList();
	protected int level = 0;
	private HashMap hitPointMap = new HashMap();
	private HashMap vFeatMap = new HashMap();
	private ArrayList featList = new ArrayList();
	private ArrayList domainList = new ArrayList();
	private Integer skillPool = new Integer(0);
	private String specialsString = "";
	private List skillList = new LinkedList();
	private String classSkillString = null;
	private List classSkillList = null;
	private String classSpellString = null;
	private List classSpellList = null;
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
	private Map attackCycleMap = new HashMap();
	private String castAs = "";
	protected int numSpellsFromSpecialty = 0;
	private String preRaceType = null;
	private boolean modToSkills = true; // stat bonus applied to skills per level
	private Integer levelsPerFeat = null;
	//private int ageSet = 2;
	private String itemCreationMultiplier = "";
	private final ArrayList templates = new ArrayList();
	private ArrayList templatesAdded = null;
	private ArrayList addDomains = new ArrayList();
	private String stableSpellKey = null;
	private final HashMap castForLevelMap = new HashMap();
	private ArrayList DR = null;
	private ArrayList SR = null;
	private List visionList = null;
	private boolean hasSubClass = false;
	private ArrayList subClassList = null; // list of SubClass objects
	private ArrayList naturalWeapons = null;
	// monsterFlag and XPPenalty can't be boolean, because null has a meaning.
	// Null means use the default (look for the types on class types)
	private String monsterFlag = null; // Valid values are null, "YES", "NO"
	private String XPPenalty = null; // Valid values are null, "YES", "NO"
	private String CRFormula = null; // null or formula
	private int maxCastLevel = -1; // max level CAST: tag is found
	private int maxKnownLevel = -1; // max level KNOWN: tag is found

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
			aClass.setBonusSpellBaseStat(bonusSpellBaseStat);
			aClass.setSpellType(spellType);
			aClass.setAttackBonusType(attackBonusType);
			aClass.specialtyknownList = (ArrayList) specialtyknownList.clone();
			aClass.knownList = (ArrayList) knownList.clone();
			aClass.castMap = new HashMap(castMap);
			aClass.uattList = (ArrayList) uattList.clone();
			aClass.acList = (ArrayList) acList.clone();
			aClass.languageBonus = (TreeSet) languageBonus.clone();
			aClass.weaponProfBonus = (ArrayList) weaponProfBonus.clone();
			aClass.featList = (ArrayList) featList.clone();
			aClass.vFeatMap = new HashMap(vFeatMap);
			aClass.featAutos = (ArrayList) featAutos.clone();
			aClass.skillList = new LinkedList();
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
			aClass.setExClass(exClass);
			aClass.setLevelExchange(levelExchange);
			aClass.maxCastLevel = maxCastLevel;
			aClass.maxKnownLevel = maxKnownLevel;

			aClass.abbrev = abbrev;
			aClass.memorizeSpells = memorizeSpells;
			aClass.multiPreReqs = multiPreReqs;
			aClass.isSpecified = isSpecified;
			aClass.deityString = deityString;
			aClass.maxLevel = maxLevel;
			aClass.knownSpellsList = (ArrayList) knownSpellsList.clone();
			aClass.attackCycle = attackCycle;
			aClass.attackCycleMap = new HashMap(attackCycleMap);
			aClass.castAs = castAs;
			aClass.preRaceType = preRaceType;
			aClass.modToSkills = modToSkills;
			aClass.levelsPerFeat = levelsPerFeat;
			aClass.initMod = initMod;
			aClass.specialtyList = (ArrayList) specialtyList.clone();
			//aClass.ageSet = ageSet;
			aClass.domainList = (ArrayList) domainList.clone();
			aClass.addDomains = (ArrayList) addDomains.clone();
			aClass.hitPointMap = new HashMap(hitPointMap);
			aClass.hasSubClass = hasSubClass;
			aClass.subClassList = subClassList;
			if (naturalWeapons != null)
			{
				aClass.naturalWeapons = (ArrayList) naturalWeapons.clone();
			}
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return aClass;
	}

	public void addTemplate(String template)
	{
		templates.add(template);
	}

	public ArrayList getTemplates()
	{
		return templates;
	}

	public List getClassSpecialAbilityList()
	{
		final List aList = new ArrayList();
		final List formattedList = new ArrayList();
		final List abilityList = this.getSpecialAbilityList();

		//
		// Determine the list of abilities from this class
		// that the character is eligable for
		//
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


		//
		// From the list of allowed SAs, format the output strings
		// to include all of the variables
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
					// Get the value for each variable
					final String vString = varTok.nextToken();
					varValue[j] = Globals.getCurrentPC().getVariable(vString, true, true, "", "").intValue();
				}
			}

			StringBuffer newAbility = new StringBuffer();
			varTok = new StringTokenizer(aString, "%", true);
			varCount = 0;
			boolean isZero = false;
			// Fill in each % with the value of the appropriate token
			while (varTok.hasMoreTokens())
			{
				final String nextTok = varTok.nextToken();
				if ("%".equals(nextTok))
				{
					if (varCount == 0)
					{
						// If this is the first token, then set the count of successfull token replacements to 0
						isZero = true;
					}
					if ((varValue != null) && (varCount < varValue.length))
					{
						final int thisVar = varValue[varCount++];
						// Update isZero if this token has a value of anything other than 0
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

			if (!isZero)
			{
				// If all of the tokens for this ability were 0 then we do not show it,
				// otherwise we add it to the return list.
				formattedList.add(newAbility.toString());
			}
		}
		return formattedList;
	}


	protected List addSpecialAbilitiesToList(List aList)
	{
		List specialAbilityList = getSpecialAbilityList();
		if ((specialAbilityList == null) || specialAbilityList.isEmpty())
		{
			return aList;
		}
		final List bList = new ArrayList();
		for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
		{
			final SpecialAbility sa = (SpecialAbility) i.next();
			if (sa.pcQualifiesFor(Globals.getCurrentPC()))
			{
				if (sa.getName().startsWith(".CLEAR"))
				{
					if (".CLEARALL".equals(sa.getName()))
					{
						bList.clear();
					}
					else if (sa.getName().startsWith(".CLEAR."))
					{
						final String saToRemove = sa.getName().substring(7);
						for (int itIdx = bList.size() - 1; itIdx >= 0; --itIdx)
						{
							final String saName = ((SpecialAbility) bList.get(itIdx)).getName();
							if (saName.equals(saToRemove))
							{
								bList.remove(itIdx);
							}
							else if (saName.indexOf('(') >= 0)
							{
								if (saName.substring(0, saName.indexOf('(')).trim().equals(saToRemove))
								{
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

	/**
	 * <p>This function adds all templates up to the current level to
	 * <code>templatesAdded</code> and returns a list of the names of those
	 * templates.</p>
	 * <p>The function requires that templates be stored in the <code>templates</code> list
	 * as a string in the form LVL|[CHOOSE:]Template|Template|Template...</p>
	 * <p>Passing <code>false</code> to this function results in nothing happening, although
	 * the function still parses all of the template lines, it doesn't add anything
	 * to the class.</p>
	 *
	 * @param flag If false, function returns empty <code>ArrayList</code> (?)
	 * @return A list of templates added by this function
	 */
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
			//The next token will either be a CHOOSE: tag or a template;
			//we handle CHOOSE: tags by retrieving the rest of the string
			final String tString = aTok.nextToken();
			if (tString.startsWith("CHOOSE:") && !flag)
			{
				newTemplates.add(PCTemplate.chooseTemplate(template.substring(template.indexOf("CHOOSE:") + 7)));
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
			}
			else if (!flag)
			{
				newTemplates.add(tString);
				templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
				while (aTok.hasMoreTokens())
				{
					newTemplates.add(aTok.nextToken());
					templatesAdded.add(newTemplates.get(newTemplates.size() - 1));
				}
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

	public final String getDeityString()
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

	public final Integer getSkillPool()
	{
		int returnValue = 0;
		for (int i = 0; i <= level; i++)
		{
			PCLevelInfo pcl = Globals.getCurrentPC().getLevelInfoFor(getKeyName(), i);
			if (pcl != null && pcl.getClassKeyName().equals(getKeyName()))
			{
				returnValue += pcl.getSkillPointsRemaining();
			}
		}
		return new Integer(returnValue);
		}

	public final void setSkillPool(Integer argSkillPool)
	{
		skillPool = argSkillPool;
	}

	public final String getItemCreationMultiplier()
	{
		return itemCreationMultiplier;
	}

	public final void setItemCreationMultiplier(final String argItemCreationMultiplier)
	{
		itemCreationMultiplier = argItemCreationMultiplier;
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

	public final boolean multiPreReqs()
	{
		return multiPreReqs;
	}

	public final void setMultiPreReqs(boolean multiPreReqs)
	{
		this.multiPreReqs = multiPreReqs;
	}

	public final ArrayList getDomainList()
	{
		return domainList;
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
		if (castAs == null || castAs.equals(""))
			return name;
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

	public String getFullDisplayClassName()
	{
		StringBuffer buf = new StringBuffer();

		if (subClassName.length() > 0 && !subClassName.equals(Constants.s_NONE))
		{
			buf.append(subClassName);
		}
		else
		{
			buf.append(name);
		}

		return buf.append(" ").append(level).toString();
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
		hasSubClass = arg;
	}

	public final boolean hasSubClass()
	{
		return hasSubClass;
	}

	public final void addSubClass(SubClass sClass)
	{
		if (subClassList == null)
		{
			subClassList = new ArrayList();
		}
		sClass.setHitPointMap((HashMap)getHitPointMap().clone());
		sClass.setHitDie(hitDie);
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

	public boolean isProhibited(Spell aSpell)
	{
		final StringTokenizer aTok = new StringTokenizer(prohibitedString, ",", false);
		if (!aSpell.passesPreReqToGain())
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
		final List aList = (ArrayList) getSpecialtyList();
		if (aList == null || aList.size() == 0)
		{
			return false;
		}
		return (aSpell.descriptorListContains(aList) || aSpell.schoolContains(aList) || aSpell.subschoolContains(aList));
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 **/
	public double calcBonusFrom(BonusObj aBonus, Object anObj)
	{
		double retVal = 0;
		int iTimes = 1;

		String aType = aBonus.getTypeOfBonus();
		//String aName = aBonus.getBonusInfo();

		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			if (choiceString.startsWith("SALIST|") && choiceString.indexOf("|VAR|") >= 0)
			{
				iTimes = 1;
			}
		}

		String bString = aBonus.toString();

		if (getAssociatedCount() != 0)
		{
			int span = 4;
			int idx = bString.indexOf("%VAR");
			if (idx == -1)
			{
				idx = bString.indexOf("%LIST|");
				span = 5;
			}
			if (idx >= 0)
			{
				final String firstPart = bString.substring(0, idx);
				final String secondPart = bString.substring(idx + span);
				for (int i = 1; i < getAssociatedCount(); ++i)
				{
					String xString = new StringBuffer().append(firstPart).append(getAssociated(i)).append(secondPart).toString();
					retVal += calcPartialBonus(xString, iTimes, aBonus, anObj);
				}
				bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString();
			}
		}

		retVal += calcPartialBonus(bString, iTimes, aBonus, anObj);
		return retVal;
	}

	/**
	 * calcPartialBonus calls appropriate getVariableValue() for a Bonus
	 * @param bString	Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param iTimes	multiply bonus * iTimes
	 * @param aBonus	The bonuse Object used for calcs
	 **/
	private double calcPartialBonus(String bString, int iTimes, BonusObj aBonus, Object anObj)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);
		if (aBonus.getPCLevel() >= 0)
		{
			// discard first token (Level)
			aTok.nextToken();
		}
		aTok.nextToken(); //Is this intended to be thrown away? Why?
		String aList = aTok.nextToken();
		String aVal = aTok.nextToken();

		double iBonus = 0;

		if (aList.equals("ALL"))
		{
			return 0;
		}

		if (anObj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) anObj).getVariableValue(aVal, "CLASS:" + name).doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aVal);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				Logging.errorPrint("PCClass calcPartialBonus NumberFormatException in BONUS: " + bString);
			}
		}

		return iBonus * iTimes;
	}

	/**
	 * Returns a list of BonusObj's which match Type, Name and Level
	 * Will be used when I finish the conversion of PObject to use BonusObj
	 * Please leave! JSC - 10/28/03
	 **/
	public List getBonusListOfType(String aType, String aName, int aLevel)
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if ((aBonus.getTypeOfBonus().indexOf(aType) >= 0) &&
				(aBonus.getBonusInfo().indexOf(aName) >= 0) &&
				(aBonus.getPCLevel() <= aLevel))
			{
				aList.add(aBonus);
			}
		}
		return aList;
	}

	/**
	 * Sets qualified BonusObj's to "active"
	 **/
	public void activateBonuses()
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if ((aBonus.getPCLevel() <= level))
			{
				if (aBonus.hasPreReqs())
				{
					if (passesPreReqToUse(aBonus))
					{
						aBonus.setApplied(true);
					}
					else
					{
						aBonus.setApplied(false);
					}
				}
				else
				{
					aBonus.setApplied(true);
				}
			}
		}
	}

	private double getBonusTo(String type, String mname)
	{
		return getBonusTo(type, mname, level);
	}

	public double getBonusTo(String argType, String argMname, int asLevel)
	{
		double i = 0;
		if (asLevel == 0 || getBonusList().isEmpty())
		{
			return 0;
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();

		final String type = argType.toUpperCase();
		final String mname = argMname.toUpperCase();
		//final String typePlusMName = new StringBuffer(type).append('.').append(mname).append('.').toString();
		for (Iterator e = getBonusList().iterator(); e.hasNext();)
		{
			BonusObj aBonus = (BonusObj) e.next();
			StringTokenizer breakOnPipes = new StringTokenizer(aBonus.toString().toUpperCase(), "|", false);
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
					List localPreReqList = new ArrayList();

					while (breakOnPipes.hasMoreTokens())
					{
						final String bString = breakOnPipes.nextToken();
						if (bString.startsWith("PRE") || bString.startsWith("!PRE"))
						{
							localPreReqList.add(bString);
						}
					}
					// must meet criteria for bonuses before adding them in
					if (passesPreReqToGainForList(localPreReqList))
					{
						final double j = aPC.getVariableValue(aString, "CLASS:" + name).doubleValue();
						i += j;
					}
				}
			}
		}
		return i;
	}

	private boolean canBePrestige()
	{
		return passesPreReqToGain();
	}

	public boolean passesPreReqToGainForList(PlayerCharacter aPC, PObject aObj, List anArrayList)
	{
		return (Globals.checkRule("CLASSPRE") || PrereqHandler.passesPreReqToGainForList(this, aPC, aObj, anArrayList));
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
			return getHitDieUnadjusted();
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

	public final int getHitDieUnadjusted()
	{
		if ("None".equals(subClassName))
		{
			return hitDie;
		}
		else
		{
			SubClass aSubClass = getSubClassNamed(subClassName);
			if (aSubClass != null)
			{
				return aSubClass.getHitDie();
			}
			else
			{
				return hitDie;
			}
		}
	}

	public int getSkillPoints()
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

	public final int getInitialFeats()
	{
		return initialFeats;
	}

	public final String getSpellBaseStat()
	{
		return spellBaseStat;
	}

	public final void setSpellBaseStat(String baseStat)
	{
		spellBaseStat = baseStat;
	}

	/**
	 * Method gets the bonusSpellBaseStat which will be used to determine the
	 * number of bonus spells that a character can cast.
	 *
	 * author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 *
	 * @return String
	 */
	public final String getBonusSpellBaseStat()
	{
		return bonusSpellBaseStat;
	}

	/**
	 * Method sets the bonusSpellBaseStat which will be used to determine the
	 * number of bonus spells that a character can cast.
	 *
	 * author David Wilson <eldiosyeldiablo@users.sourceforge.net>
	 */
	public final void setBonusSpellBaseStat(final String baseStat)
	{
		bonusSpellBaseStat = baseStat;
	}

	public final void setSpellLevelString(final String aString)
	{
		classSpellString = aString;
	}

	public final String getSpellLevelString()
	{
		return classSpellString;
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
		final List aList = new ArrayList();
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

	public final List getClassSpellList()
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

	/*
	 * sets whether stat modifier is applied to skill points
	 * at level-up time
	 */
	public final void setModToSkills(boolean bool)
	{
		modToSkills = bool;
	}

	public final boolean getModToSkills()
	{
		return modToSkills;
	}

	public void setLevelsPerFeat(Integer newLevels)
	{
		if (newLevels!=null || newLevels.intValue() < 0)
		{
			return;
		}
		levelsPerFeat = newLevels;
	}

	public final Integer getLevelsPerFeat()
	{
		if (levelsPerFeat == null)
			levelsPerFeat = new Integer(-1);
		return levelsPerFeat;
	}

	public void addKnown(int iLevel, String aString)
	{
		if (iLevel > maxKnownLevel)
			maxKnownLevel = iLevel;
		// pad to with empty entries
		while (knownList.size() < (iLevel - 1))
		{
			knownList.add("0");
		}

		// Replace existing with new entry
		if (knownList.size() >= iLevel)
		{
			knownList.set(iLevel - 1, aString);
		}
		else
		{
			knownList.add(aString);
		}
	}

	/**
	 * if castAs has been set, return knownList from that class
	 * @return
	 */
	public List getKnownList()
	{
		if ("".equals(castAs) || getName().equals(castAs))
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

	/*
	 * -2 means that the spell itself indicates what stat should be used,
	 * otherwise this method returns an index into the global list of stats for
	 * which stat the bonus spells are based upon.
	 *
	 * @return int
	 */
	public int baseSpellIndex()
	{
		String tmpSpellBaseStat = getBonusSpellBaseStat();
		if (tmpSpellBaseStat.equals(Constants.s_NONE))
		{
			tmpSpellBaseStat = getSpellBaseStat();
		}

		return "SPELL".equals(tmpSpellBaseStat) ? -2 // means base spell stat is based upon spell itself
			: Globals.getStatFromAbbrev(tmpSpellBaseStat);
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

		int total = 0;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", name);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

		if (getCastMap().size()>0 && getNumFromCastList(pcLevel, spellLevel) < 0)
		{
			// Don't know any spells of this level
			return 0;
		}
		if (pcLevel > maxKnownLevel)
			pcLevel = maxKnownLevel;

		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE." + getSpellType() + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any" + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", classKeyName + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "TYPE." + getSpellType() + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLKNOWN", "CLASS.Any" + allSpellLevel);

		final int index = baseSpellIndex();
		PCStat aStat;

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().getStats().size()))
		{
			aStat = (PCStat) aPC.getStatList().getStats().get(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0)
		{
			statString = Globals.s_ATTRIBSHORT[index];
		}

		int bonusStat = (int) aPC.getTotalBonusTo("STAT", "KNOWN." + statString) +
			(int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT") +
			(int) aPC.getTotalBonusTo("STAT", "BASESPELLKNOWNSTAT;CLASS." + name);

		if (index > -2)
		{
			final int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + statString, "").intValue();
			if ((maxSpellLevel + bonusStat) < spellLevel)
			{
				return total;
			}
		}

		stat += bonusStat;

		int mult = (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", classKeyName + levelSpellLevel);
		mult += (int) aPC.getTotalBonusTo("SPELLKNOWNMULT", "TYPE." + getSpellType() + levelSpellLevel);

		if (mult < 1)
		{
			mult = 1;
		}

		boolean psiSpecialty = false;
		if (!getKnownList().isEmpty())
		{
			if (pcLevel > getKnownList().size())
			{
				// doesn't know any spells of this level
				return 0;
			}
			String aString = (String) getKnownList().get(pcLevel - 1);
			StringTokenizer aTok = new StringTokenizer(aString, ",");
			int iCount = 0;
			while (aTok.hasMoreTokens())
			{
				String spells = aTok.nextToken();
				if (iCount == spellLevel)
				{
					if (spells.endsWith("+d"))
					{
						psiSpecialty = true;
						if (spells.length() > 1)
						{
							spells = spells.substring(0, spells.length() - 2);
						}
					}
					final int t = Integer.parseInt(spells);
					total += (t * mult);
					// add Stat based bonus
					Object bonusSpell = Globals.getBonusSpellMap().get(String.valueOf(spellLevel));
					if (Globals.checkRule("BONUSSPELLKNOWN") && (bonusSpell != null) && !bonusSpell.equals("0|0"))
					{
						final StringTokenizer s = new StringTokenizer(bonusSpell.toString(), "|");
						final int base = Integer.parseInt(s.nextToken());
						final int range = Integer.parseInt(s.nextToken());
						if (stat >= base)
						{
							total += Math.max(0, (stat - base + range) / range);
						}
					}
					if (psiSpecialty)
					{
						total += numSpellsFromSpecialty;
					}
				}
				iCount++;
			}
		}

		// if we have known spells (0==no known spells recorded)
		// or a psi specialty.
		if ((total > 0 && spellLevel > 0) && !psiSpecialty)
		{
			// make sure any slots due from specialties
			// (including domains) are added
			total += numSpellsFromSpecialty;
		}

		return total;
	}

	/**
	 * Return number of speciality spells known
	 * for a level for a given spellbook
	 **/
	public int getSpecialtyKnownForLevel(int pcLevel, int spellLevel)
	{
		int total;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		total = (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "CLASS." + getKeyName() + ";LEVEL." + spellLevel);
		total += (int) aPC.getTotalBonusTo("SPECIALTYSPELLKNOWN", "TYPE." + getSpellType() + ";LEVEL." + spellLevel);

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", name);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());

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
					aTok = new StringTokenizer(aString, ",");
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

		// if we have known spells (0==no known spells recorded) or a psi specialty.
		if (total > 0 && spellLevel > 0)
		{
			// make sure any slots due from specialties (including domains) are added
			total += numSpellsFromSpecialty;
		}

		return total;
	}

	/**
	 * Return CAST: string for a level
	 * @return String
	 */
	public String getCastStringForLevel(int aInt)
	{
		if (aInt > maxCastLevel)
			aInt = maxCastLevel;
		String aLevel = String.valueOf(aInt);
		if (getCastMap().containsKey(aLevel))
		{
			return (String) getCastMap().get(aLevel);
		}
		return "";
	}

	/**
	 * if castAs has been set, return castMap from that class
	 * @return List of strings
	 */
	public Map getCastMap()
	{
		if ("".equals(castAs) || getName().equals(castAs))
		{
			return castMap;
		}
		final PCClass aClass = Globals.getClassNamed(castAs);
		if (aClass != null)
		{
			return aClass.getCastMap();
		}
		return castMap;
	}

	public void setCastMap(int index, String cast)
	{
		if (index > maxCastLevel)
			maxCastLevel = index;
		castMap.put(String.valueOf(index), cast);
	}

	public boolean zeroCastSpells()
	{
		for (Iterator e = getCastMap().keySet().iterator(); e.hasNext();)
		{
			String aKey = (String) e.next();
			String aVal = (String) getCastMap().get(aKey);
			StringTokenizer aTok = new StringTokenizer(aVal, ",");
			int numSpells = 0;
			while (aTok.hasMoreTokens())
			{
				String spellNum = aTok.nextToken();
				try
				{
					numSpells = Integer.parseInt(spellNum);
				}
				catch (NumberFormatException nfe)
				{
					// ignore
				}
				if (numSpells > 0)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Get the number of spells this PC can cast based on
	 * Caster Level and desired Spell Level
	 * ex: how many 5th level spells can a 17th level wizard cast?
	 **/
	public int getNumFromCastList(int iCasterLevel, int iSpellLevel)
	{
		int aNum = -1;
		if (iCasterLevel > maxCastLevel)
			iCasterLevel = maxCastLevel;
		if (!getCastMap().containsKey(String.valueOf(iCasterLevel)))
		{
			// can't cast spells!
			return aNum;
		}
		int iCount = 0;
		String aString = getCastStringForLevel(iCasterLevel);

		StringTokenizer aTok = new StringTokenizer(aString, ",");
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
			if (iCount == iSpellLevel)
			{
				try
				{
					aNum = Integer.parseInt(aString);
				}
				catch (NumberFormatException ex)
				{
					// ignore
					aNum = 0;
				}
				return aNum;
			}
			iCount++;
		}
		return aNum;
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

	private void getStableCastForLevel()
	{
		//
		// Shouldn't we be using Globals.getLevelInfo().size() instead of 100?
		// Byngl -- November 25, 2002
		//
		for (int i = 0; i < 100; i++)
		{
			final int s = getCastForLevel(level, i);
			castForLevelMap.put(String.valueOf(i), String.valueOf(s));
		}
	}

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
		return getCastForLevel(pcLevel, spellLevel, bookName, includeAdj, true);
	}

	public int getCastForLevel(int pcLevel, int spellLevel, String bookName, boolean includeAdj, boolean limitByStat)
	{
		int total = 0;
		int stat = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final String classKeyName = "CLASS." + getKeyName();
		final String levelSpellLevel = ";LEVEL." + spellLevel;
		final String allSpellLevel = ";LEVEL.All";

		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", name);
		pcLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + getSpellType());
		if (getNumFromCastList(pcLevel, spellLevel) < 0)
		{
			// can't cast spells of this level
			return 0;
		}

		total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + getSpellType() + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any" + levelSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", classKeyName + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "TYPE." + getSpellType() + allSpellLevel);
		total += (int) aPC.getTotalBonusTo("SPELLCAST", "CLASS.Any" + allSpellLevel);

		final int index = baseSpellIndex();

		PCStat aStat;

		if ((index != -2) && (index >= 0) && (index < aPC.getStatList().getStats().size()))
		{
			aStat = (PCStat) aPC.getStatList().getStats().get(index);
			stat = aPC.getStatList().getTotalStatFor(aStat.getAbb());
		}

		String statString = Constants.s_NONE;

		if (index >= 0)
		{
			statString = Globals.s_ATTRIBSHORT[index];
		}
		int bonusStat = (int) aPC.getTotalBonusTo("STAT", "CAST." + statString) +
			(int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT") +
			(int) aPC.getTotalBonusTo("STAT", "BASESPELLSTAT;CLASS." + name);
		if ((index > -2) && limitByStat)
		{
			int maxSpellLevel = aPC.getVariableValue("MAXLEVELSTAT=" + statString, "").intValue();
			if ((maxSpellLevel + bonusStat) < spellLevel)
			{
				return total;
			}
		}
		stat += bonusStat;

		// Now we decide whether to adjust the number of slots down
		// the road by adding specialty slots.
		// Reworked to consider the fact that a lower-level
		// specialty spell can go into this level of specialty slot
		//
		int adj = 0;
		if (includeAdj && !bookName.equals(Globals.getDefaultSpellBook()) && (specialtyList.size() > 0 || aPC.getCharacterDomainList().size() > 0))
		{
			// We need to do this for EVERY spell level up to the
			// one really under consideration, because if there
			// are any specialty spells available BELOW this level,
			// we might wind up using THIS level's slots for them.
			for (int ix = 0; ix <= spellLevel; ++ix)
			{
				final List aList = getCharacterSpell(null, "", ix);
				List bList = new ArrayList();
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
				{
					break;
				}
			} // end of looping up to this level looking for specialty spells that can be cast
		} // end of deciding whether there are specialty slots to distribute

		//
		// Multiplier for things like Ring of Wizardry
		//
		int mult = (int) aPC.getTotalBonusTo("SPELLCASTMULT", classKeyName + levelSpellLevel);
		mult += (int) aPC.getTotalBonusTo("SPELLCASTMULT", "TYPE." + getSpellType() + levelSpellLevel);

		if (mult < 1)
		{
			mult = 1;
		}

		final int t = getNumFromCastList(pcLevel, spellLevel);

		total += (t * mult) + adj;

		Object bonusSpell = Globals.getBonusSpellMap().get(String.valueOf(spellLevel));
		if ((bonusSpell != null) && !bonusSpell.equals("0|0"))
		{
			final StringTokenizer s = new StringTokenizer(bonusSpell.toString(), "|");
			final int base = Integer.parseInt(s.nextToken());
			final int range = Integer.parseInt(s.nextToken());
			if (stat >= base)
			{
				total += Math.max(0, (stat - base + range) / range);
			}
		}

		return total;
	}

	public final Collection getUattList()
	{
		return uattList;
	}

	public String getUattForLevel(int aLevel)
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
			aLevel += (int) Globals.getCurrentPC().getTotalBonusTo("UDAM", "CLASS." + name);
		}
		int iLevel = aLevel;
		final Equipment eq = EquipmentList.getEquipmentKeyed("Unarmed Strike");
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
		aDamage = Globals.adjustDamage(aDamage, SystemCollections.getDefaultSizeAdjustment().getAbbreviation(), SystemCollections.getSizeAdjustmentAtIndex(iSize).getAbbreviation());

		//
		// Check the UDAM list for monk-like damage
		//
		udamList = Globals.getClassNamed(name).getUdamList();
		if (udamList != null && !udamList.isEmpty())
		{
			if (udamList.size() == 1)
			{
				final String aString = udamList.get(0).toString();
				if (aString.startsWith("CLASS=") || aString.startsWith("CLASS."))
				{
					final PCClass aClass = Globals.getClassNamed(aString.substring(6));
					if (aClass != null)
					{
						return aClass.getUdamForLevel(aLevel, includeCrit, includeStrBonus);
					}

					Logging.errorPrint(name + " refers to " + aString.substring(6) + " which isn't loaded.");

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
		int b = (int) aPC.getStatBonusTo("DAMAGE", "TYPE.MELEE");
		b += (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");
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

	/**
	 * Identical function exists in PCTemplate.java. Refactor. XXX
	 * @param aString
	 */
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

	public final Collection getFeatAutos()
	{
		return featAutos;
	}

	public void setFeatAutos(int aLevel, String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
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

	/**
	 * set the level to arg without impacting spells, hp, or anything else
	 * - use this with great caution only!
	 * Then why is it even here? What is it used for? (JSC 07/21/03)
	 **/
	public final void setLevelWithoutConsequence(int arg)
	{
		level = arg;
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

		if (!aPC.isImporting())
		{
			aPC.calcActiveBonuses();
			aPC.buildSpellLevelMap(newLevel);
		}

		if ((level == 1) && !aPC.isImporting() && (curLevel == 0))
		{
			checkForSubClass();
			getSpellKey();
		}

		if ((knownSpellsList.size() > 0) && !aPC.isImporting() && aPC.getAutoSpells())
		{
			final List cspelllist = Globals.getSpellsIn(-1, getSpellKey(), "");
			if (cspelllist.isEmpty())
			{
				return;
			}
			getStableCastForLevel();
			int _maxLevel;
			for (_maxLevel = 0; _maxLevel < 45; ++_maxLevel)
			{
				final String val = castForLevelMap.get(String.valueOf(_maxLevel)).toString();
				if (val == null || Integer.parseInt(val) == 0)
				{
					--_maxLevel;
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
						if (!addIt)
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
						aCD.getDomain().addSpellsToClassForLevels(this, 0, _maxLevel);
					}
				}
			}
		}
	}

	void doPlusLevelMods(int newLevel)
	{
		if (!isMonster())
		{
			changeFeatsForLevel(newLevel, true);
		}
		changeSpecials();
		addVariablesForLevel(newLevel);

		// moved after changeSpecials and addVariablesForLevel
		// for bug #688564 -- sage_sam, 18 March 2003
		addAddsForLevel(newLevel);
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

	public void addLevel(boolean levelMax)
	{
		addLevel(levelMax, false);
	}

	/**
	 * Adds a level of this class to the current Global PC.
	 *
	 * This method is deeply evil. This instance of the PCClass
	 * has been assigned to a PlayerCharacter, but the only way we can get
	 * from this class back to the PlayerCharacter is to get
	 * the current global character and hope that the caller
	 * is only calling this method on a PCClass embedded within
	 * the current global PC.
	 *
	 * TODO: Split the PlayerCharacter code out of PCClass (i.e. the level
	 * property). Then have a joining class assigned to PlayerCharacter
	 * that maps PCClass and number of levels in the class.
	 *
	 * @param argLevelMax True if we should only allow extra levels if there are still
	 *                    levels in this class to take. (i.e. a lot of prestige classes
	 *                    stop at level 10, so if this is true it would not allow an 11th
	 *                    level of the class to be added
	 * @param bSilent True if we are not to show any dialog boxes about errors or questions.
	 */
	void addLevel(boolean argLevelMax, boolean bSilent)
	{
		// Check to see if we can add a level of this class to the
		// current Global Character		
		final int newLevel = level + 1;
		boolean levelMax = argLevelMax;
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

		// Add the level to the current Global Character
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int total = aPC.getTotalLevels();
		if (total == 0)
		{
			aPC.setFeats(aPC.getInitialFeats());
		}
		setLevel(newLevel);

		// the level has now been added to the character,
		// so now assign the attributes of this class level to the
		// character...		
		final List templateList = getTemplates(aPC.isImporting());
		for (int x = 0; x < templateList.size(); ++x)
		{
			aPC.addTemplateNamed((String) templateList.get(x));
		}

		// Make sure that if this Class adds a new domain that
		// we record where that domain came from
		int dnum = aPC.getMaxCharacterDomains(this) - aPC.getCharacterDomainUsed();
		if (!aPC.hasDomainSource("PCClass", getName(), newLevel))
		{
			if (dnum > 0)
			{
				aPC.addDomainSource("PCClass", getName(), newLevel, dnum);
			}
		}

		aPC.setAutomaticFeatsStable(false);
		doPlusLevelMods(newLevel);

		//Don't roll the hit points if the gui is not being used.
		//This is so GMGen can add classes to a person without pcgen flipping out
		if (Globals.getUseGUI())
		{
			rollHP();
		}

		if (!aPC.isImporting())
		{
			modDomainsForLevel(newLevel, true);
		}
		int levelUpStats = 0;
		
		// Add any bonus feats or stats that will be gained from this level
		// i.e. a bonus feat every 3 levels
		if (aPC.getTotalLevels() > total)
		{
			boolean processBonusStats = true;
			boolean processBonusFeats = true;
			total = aPC.getTotalLevels();
			
			if (isMonster()) 
			{
				// If we have less levels that the races monster levels
				// then we can not give a stat bonus (i.e. an Ogre has 
				// 4 levels of Giant, so it does not get a stat increase at
				// 4th level because that is already taken into account in
				// its racial stat modifiers, but it will get one at 8th
				if (total <= aPC.getRace().getMonsterClassLevels() )
				{
					processBonusStats = false;
				}

				/*
				 * If we are usign default monsters and we have not yet added
				 * all of the racial monster levels then we can not add any feats.
				 * i.e. a default monster Ogre will not get a feat at 1st or 3rd level
				 * because they have already been allocated in the race, but a 
				 * non default monster will get the 2 bonus feats instead.
				 * Both versions of the monster will get one at 6th level. i.e. default 
				 * Ogre with 2 class levels, or no default Ogre with 4 giant levels and 2
				 * class levels. 
				 */
				if (aPC.isMonsterDefault() && total <= aPC.getRace().getMonsterClassLevels() )
				{
					processBonusFeats = false;
				}
			}
			
			if (!aPC.isImporting())
			{	
				// We do not want to do these
				// calculations a second time when are
				// importing a character.  The feat
				// number and the stat point pool are
				// already saved in the import file.
				
				if (processBonusFeats)
				{
					final double bonusFeats = Globals.getBonusFeatsForLevel(total);
					if (bonusFeats > 0)
					{
						aPC.setFeats(aPC.getFeats() + bonusFeats);
					}
				}
				
				
				if (processBonusStats)
				{	
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
		}
		

		// Update Skill Points.  Modified 20 Nov 2002 by sage_sam
		// for bug #629643
		int spMod;
		spMod = recalcSkillPointMod(aPC, total);
		PCLevelInfo pcl = null;
		if (aPC.getLevelInfoSize() > 0)
		{
			pcl = (PCLevelInfo) aPC.getLevelInfo().get(aPC.getLevelInfoSize() - 1);
			if (pcl != null)
			{
				pcl.setSkillPointsGained(spMod);
				pcl.setSkillPointsRemaining(spMod);
				pcl.setLevel(level);
			}
		}
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

			// Make sure any natural weapons are added
			aPC.addNaturalWeapons(this);
		}

		// this is a monster class, so don't worry about experience
		if (isMonster())
		{
			return;
		}

		if (!aPC.isImporting())
		{
			int minxp = aPC.minXPForECL();
			if (aPC.getXP() < minxp)
			{
				aPC.setXP(minxp);
			}
			else if (aPC.getXP() >= aPC.minXPForNextECL())
			{
				if (!bSilent)
				{
					GuiFacade.showMessageDialog(null, SettingsHandler.getGame().getLevelUpMessage(), Constants.s_APPNAME,
					    GuiFacade.INFORMATION_MESSAGE);
				}
			}
		}
		//
		// Allow exchange of classes only when assign 1st level
		//
		if ((levelExchange.length() != 0) && (getLevel() == 1) && !aPC.isImporting())
		{
			final StringTokenizer aTok = new StringTokenizer(levelExchange, "|", false);
			if (aTok.countTokens() != 4)
			{
				Logging.errorPrint("levelExhange: invalid token count: " + aTok.countTokens());
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
								final List choiceNames = new ArrayList();
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
		if (isMonster() && aPC.isMonsterDefault())
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
			spMod = (int) aPC.getTotalBonusTo("MONSKILLPTS", "NUMBER");
		}
		// This is not the first level added...
		else
		{
			if (getExtraHD(aPC, total) > 0)
			{
				spMod = getSkillPoints();
				spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");
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
	private static int getExtraHD(final PlayerCharacter aPC, final int hdTotal)
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

		spMod += (int) aPC.getTotalBonusTo("SKILLPOINTS", "NUMBER");

		spMod = updateBaseSkillMod(aPC, spMod);

		if (total == 1)
		{
			if (SettingsHandler.isPurchaseStatMode())
			{
				aPC.setPoolAmount(0);
			}
			spMod *= Math.min(Globals.getSkillMultiplierForLevel(total), aPC.getRace().getInitialSkillMultiplier());
			Globals.getBioSet().randomize("AGE");
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
			List specialAbilityList = getSpecialAbilityList();
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
			PCLevelInfo pcl = aPC.getLevelInfoFor(name, level);
			if (pcl != null)
			{
				spMod = pcl.getSkillPointsGained();
			}
			else
			{
				Logging.errorPrint("ERROR: could not find class/level info for " + name + "/" + level);
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
				setHitPoint(level - 1, zeroInt);
			}
			aPC.setFeats( aPC.getFeats() - aPC.getBonusFeatsForNewLevel(this) );
			setLevel(newLevel);
/*			if (isMonster())
			{
				if (levelsPerFeat != 0)
				{
					if ((aPC.totalHitDice() + 1) % levelsPerFeat == 0)
					{
						aPC.setFeats(aPC.getFeats() - 1);
					}
				}
			}
*/
			doMinusLevelMods(aPC, newLevel + 1);

			modDomainsForLevel(newLevel, false);
			if (newLevel == 0)
			{
				setSubClassName(Constants.s_NONE);
				//
				// Remove all skills associated with this class
				//
				final List aSkills = aPC.getSkillList();
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
/*				if (total % 3 == 2)
				{
					aPC.setFeats(aPC.getFeats() - 1);
				}*/
				if (total % 4 == 3)
				{
					aPC.setPoolAmount(aPC.getPoolAmount() - 1);
					if (!bSilent && SettingsHandler.getShowStatDialogAtLevelUp())
					{
						if (Globals.checkRule("INTBEFORE"))
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
								sStats.append(" (").append(statList.getStatModFor(aStat.getAbb())).append(')').append('\n');
							}
							final Object selectedValue = GuiFacade.showInputDialog(null, "Choose stat to decrement or select Cancel to decrement stat on the Stat tab." + "\n\n" + "Current Stats:" + "\n" + sStats + "\n", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, Globals.s_ATTRIBLONG, Globals.s_ATTRIBLONG[0]);

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
			// be sure to remove any natural weapons
			aPC.removeNaturalWeapons(this);
		}
		else
		{
			Logging.errorPrint("No current pc in subLevel()? How did this happen?");
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
			final StringTokenizer aTok = new StringTokenizer(aString, "|");
			final int bLevel = Integer.parseInt(aTok.nextToken());
			int d = c;
			if (aLevel == bLevel)
			{
				final StringTokenizer bTok = new StringTokenizer(aString.substring(c), "[]|", true);
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
						if (aPC.getCharacterDomainIndex(aName) == -1)
						{
							Domain aDomain = Globals.getDomainNamed(aName);
							if (aDomain != null)
							{
								aDomain = (Domain) aDomain.clone();
								CharacterDomain aCD = aPC.getNewCharacterDomain(getName());
								aCD.setDomain(aDomain);
								aPC.addCharacterDomain(aCD);
								aDomain.setIsLocked(true);
							}
						}
						aName = "";
					}
				}
			}
		}
	}

	int memorizedSpellForLevelBook(final int aLevel, final String bookName)
	{
		int m = 0;
		final List aList = getCharacterSpell(null, bookName, aLevel);
		if (aList.isEmpty())
		{
			return m;
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell) i.next();
			m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
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
		final List saList = new ArrayList();
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
					if (SystemCollections.getUnmodifiableSpecialsList().size() > i)
					{
						saList.add(SystemCollections.getUnmodifiableSpecialsList().get(i++));
					}
				}
			}
		}
		if (!saList.isEmpty())
		{
			for (int i = saList.size() - 1; i >= 0; --i)
			{
				final SpecialAbility sa1 = (SpecialAbility) saList.get(i);
				String sn1 = sa1.getSADesc();
				for (int k = 0; k < 10; ++k)
				{
					sn1 = sn1.replace((char) ('0' + k), ' ');
				}
				String sn2;
				for (int j = i - 1; j >= 0; --j)
				{
					final SpecialAbility sa2 = (SpecialAbility) saList.get(j);
					sn2 = sa2.getSADesc();
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
				sa.setSASource(sourceName);
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
		final String prefix = "CLASS:" + name + '|';
		for (Iterator i = getVariableIterator(); i.hasNext();)
		{
			Variable v = (Variable) i.next();
			if (v.getLevel() == aLevel)
			{
				aPC.addVariable(prefix + v.getDefinition());
			}
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

	public void setHitPoint(int aLevel, Integer iRoll)
	{
		hitPointMap.put(Integer.toString(aLevel), iRoll);
	}

	public final void setHitPointMap(HashMap newMap)
	{
		hitPointMap.clear();
		hitPointMap.putAll(newMap);
	}

	public final HashMap getHitPointMap()
	{
		return hitPointMap;
	}

	public int hitPoints(int iConMod)
	{
		int total = 0;
		for (int i = 0; i <= getLevel(); ++i)
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
	 * Rolls hp for the current level according to the rules set in options.
	 */
	private void rollHP()
	{
		int roll = 0;
		List aTempList;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int min = 1 + (int) aPC.getTotalBonusTo("HD", "MIN") + (int) aPC.getTotalBonusTo("HD", "MIN;CLASS." + name);
		int max = getHitDie() + (int) aPC.getTotalBonusTo("HD", "MAX") + (int) aPC.getTotalBonusTo("HD", "MAX;CLASS." + name);
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

		final int totalLevels = aPC.getTotalLevels();

		if (totalLevels == 1 && SettingsHandler.isHPMaxAtFirstLevel())
		{
			roll = max;
		}
		else
		{
			if (!aPC.isImporting())
			{
				roll = Globals.rollHP(min, max, getName(), level);
			}
		}
		roll += ((int) aPC.getTotalBonusTo("HP", "CURRENTMAXPERLEVEL"));
		setHitPoint(level - 1, new Integer(roll));
		aPC.setCurrentHP(aPC.hitPoints());
	}

	public int baseAttackBonus()
	{
		if (level == 0)
		{
			return 0;
		}
		//final int i = (int) this.getBonusTo("TOHIT", "TOHIT", level) + (int) getBonusTo("COMBAT", "BAB");
		final int i = (int) getBonusTo("COMBAT", "BAB");
		return i;
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

	public final ArrayList getFeatList()
	{
		return featList;
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

	public List getVirtualFeatList(int aLevel)
	{
		List aList = new LinkedList();
		for (int i = -9; i <= aLevel; i++)
		{
			if (vFeatMap.containsKey(String.valueOf(i)))
			{
				aList.addAll((List)vFeatMap.get(String.valueOf(i)));
			}
		}
		return aList;
	}

	/**
	 * Adds virtual feats to the vFeatMao
	 * @param int level
	 * @param String of feats
	 **/
	public void addVirtualFeat(final int aLevel, final String aString)
	{
		String levelString = String.valueOf(aLevel);
		List vFeatsAtLevel = null;

		if (vFeatMap.containsKey( levelString ))
		{
			vFeatsAtLevel = (List) vFeatMap.get( levelString ); 
		}
		else
		{
			vFeatsAtLevel = new ArrayList();
			vFeatMap.put(levelString, vFeatsAtLevel);
		}
		super.addVirtualFeat(aString, vFeatsAtLevel);
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
				final double preFeatCount = aPC.getUsedFeatCount();
				aPC.modFeatsFromList(getToken(1, feats, ":"), addThem, aLevel == 1);
				final double postFeatCount = aPC.getUsedFeatCount();
				//
				// Adjust the feat count by the total number that were given
				//
				aPC.setFeats(aPC.getFeats() + postFeatCount - preFeatCount);

			}
		}
	}

	private void inheritAttributesFrom(PCClass otherClass)
	{
		if (otherClass.getBonusSpellBaseStat() != null)
		{
			setBonusSpellBaseStat(otherClass.getBonusSpellBaseStat());
		}
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
			getBonusList().addAll(otherClass.getBonusList());
		}
		if (otherClass.getVariableCount() > 0)
		{
			addAllVariablesFrom(otherClass);
		}
		if (otherClass.getCSkillList() != null)
		{
			cSkillList = otherClass.getCSkillList();
		}
		if (otherClass.getCcSkillList() != null)
		{
			ccSkillList = otherClass.getCcSkillList();
		}
//		if (otherClass.kitString != null)
//		{
//			setKitString(otherClass.kitString);
//		}
		if (otherClass.kits != null)
		{
			otherClass.kits = (ArrayList) kits.clone();
		}
		if (otherClass.regionString != null)
		{
			setRegionString(otherClass.regionString);
		}
		if (otherClass.getSpecialAbilityList() != null)
		{
			List specialAbilityList = getSpecialAbilityList();
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
		if (otherClass.naturalWeapons != null)
		{
			naturalWeapons = (ArrayList) otherClass.naturalWeapons.clone();
		}
	}

	private void checkForSubClass()
	{
		if (!hasSubClass || (subClassList == null) || (subClassList.isEmpty()))
		{
			return;
		}

		List choiceNames = new ArrayList();
		List removeNames = new ArrayList();
		buildSubClassChoiceList(choiceNames, removeNames, false);

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setTitle("School Choice (Specialisation)");
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
			choiceNames = new ArrayList();
			removeNames = new ArrayList();
			buildSubClassChoiceList(choiceNames, removeNames, true);
			// Remove the specialist school
			for (Iterator iter = choiceNames.iterator(); iter.hasNext();)
			{
				String element = (String) iter.next();
				if (element.startsWith(sc.getName()))
				{
					choiceNames.remove(element);
					break;
				}
			}
			choiceNames.removeAll(removeNames);
			setSubClassName(sc.getName());
			if (sc.getChoice().length() > 0)
			{
				specialtyList.add(sc.getChoice());
			}
			if (sc.getCost() != 0)
			{
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableList(choiceNames);
				c1.setMessageText(
					"Make a selection.  You must make as many selections "
						+ "necessary to cover the cost of your previous selections.");
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

	/**
	 * Build a list of Sub-Classes for the user to choose from. The two lists
	 * passed in will be populated.
	 *
	 * @param choiceNames The list of sub-classes to choose from.
	 * @param removeNames The list of sub-classes that cannot be chosen
	 * @param useProhibitCost SHould the prohibited cost be used rather
	 *         than the cost of the sub-class.
	 */
	private void buildSubClassChoiceList(
		List choiceNames,
		List removeNames,
		boolean useProhibitCost)
	{
		int displayedCost = 0;

		choiceNames.add("Name\tCost\tOther");
		choiceNames.add("");
		for (Iterator i = subClassList.iterator(); i.hasNext();)
		{
			final SubClass sc = (SubClass) i.next();
			if (!sc.passesPreReqToGain())
			{
				continue;
			}
			if (useProhibitCost)
			{
				displayedCost = sc.getProhibitCost();
			}
			else
			{
				displayedCost = sc.getCost();
			}

			boolean added = false;
			StringBuffer buf = new StringBuffer();
			buf.append(sc.getName()).append('\t').append(displayedCost).append('\t');
			if (sc.getNumSpellsFromSpecialty() != 0)
			{
				buf.append("SPECIALTY SPELLS:").append(sc.getNumSpellsFromSpecialty());
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
			if (displayedCost == 0)
			{
				removeNames.add(buf.toString());
			}
			choiceNames.add(buf.toString());
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

	public String specialsString()
	{
		return specialsString;
	}

	public final void setSpecialsString(final String aString)
	{
		specialsString = aString;
	}

	/**
	 * we over ride the PObject setVision() function to keep
	 * track of what levels this VISION: tag should take effect
	 **/
	public final void setVision(String aString)
	{
		// Class based vision lines are of the form:
		// 1|Darkvision(60'),Lowlight
		if (".CLEAR".equals(aString))
		{
			visionList = null;
			return;
		}
		StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		int lvl = Integer.parseInt(aTok.nextToken());
		String newString = aString.substring(aString.indexOf('|') + 1);
		if (visionList == null)
		{
			visionList = new ArrayList();
		}
		LevelProperty lp = new LevelProperty(lvl, newString);
		visionList.add(lp);
	}

	/**
	 * Here is where we do the real work of setting the vision
	 * information on the PObject
	 **/
	public Map getVision()
	{
		LevelProperty lp = null;
		if (visionList != null)
		{
			for (int i = 0; i < visionList.size(); i++)
			{
				if (((LevelProperty) visionList.get(i)).getLevel() <= level)
				{
					lp = (LevelProperty) visionList.get(i);
					String aString = lp.getProperty();
					super.setVision(aString);
				}
			}
		}
		return super.getVision();
	}

	public void addSkillToList(String aString)
	{
		if (!skillList.contains(aString))
		{
			skillList.add(aString);
		}
	}

	public boolean hasSkill(String aString)
	{
		for (Iterator p = skillList.iterator(); p.hasNext();)
		{
			String aSkillName = p.next().toString();
			if (aSkillName.equalsIgnoreCase(aString))
			{
				return true;
			}
		}
		return false;
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
		final List aList = new ArrayList();
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

	final List getClassSkillList()
	{
		return classSkillList;
	}

	public boolean hasClassSkillList(String aString)
	{
		if ((classSkillList == null) || classSkillList.isEmpty())
		{
			return false;
		}
		for (Iterator i = classSkillList.iterator(); i.hasNext();)
		{
			String aClassName = i.next().toString();
			PCClass aClass = Globals.getClassNamed(aClassName);
			if ((aClass != null) && aClass.hasCSkill(aString))
			{
				return true;
			}
		}
		return false;
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

	public final String getLevelExchange()
	{
		return levelExchange;
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
				if (bString.startsWith("LEVEL=") || bString.startsWith("LEVEL."))
				{
					flag = Integer.parseInt(bString.substring(6)) == spellLevel;
				}
				// if it starts with TYPE=, compare it to the spells type list
				else if (bString.startsWith("TYPE=") || bString.startsWith("TYPE."))
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

	/**
	 * Parse the ATTACKCYCLE: string and build HashMap
	 * Only allowed values in attackCycle are: BAB, RAB or UAB
	 **/
	public final void setAttackCycle(final String aString)
	{
		attackCycle = aString;
		StringTokenizer aTok = new StringTokenizer(attackCycle, "|");
		while (aTok.hasMoreTokens())
		{
			String attackType = aTok.nextToken();
			String aVal = aTok.nextToken();
			attackCycleMap.put(attackType, aVal);
		}
	}

	/* returns the unadjusted unprocessed attackCycle */
	public final String getAttackCycle()
	{
		return attackCycle;
	}

	/**
	 * returns the value at which another attack is gained
	 * attackCycle of 4 means a second attack is gained at a BAB of +5/+1
	 **/
	public int attackCycle(final int index)
	{
		String aKey = null;
		if (index == Constants.ATTACKSTRING_MELEE)
		{
			// Base attack
			aKey = "BAB";
		}
		else if (index == Constants.ATTACKSTRING_RANGED)
		{
			// Ranged attack
			aKey = "RAB";
		}
		else if (index == Constants.ATTACKSTRING_UNARMED)
		{
			// Unarmed attack
			aKey = "UAB";
		}

		String aString = (String)attackCycleMap.get(aKey);
		if (aString != null)
		{
			return Integer.parseInt(aString);
		}
		else
		{
			return SettingsHandler.getGame().getBabAttCyc();
		}
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
		Logging.errorPrint("IsPrestige should be deprecated.");
		return isType("PRESTIGE");
	}

	boolean isPC()
	{
		Logging.errorPrint("IsPC should be deprecated.");
		return (getMyTypeCount() == 0 || isType("PC"));
	}

	boolean isNPC()
	{
		Logging.errorPrint("IsNPC should be deprecated.");
		return isType("NPC");
	}

	public boolean isMonster()
	{
		if (monsterFlag != null)
		{
			return "YES".equals(monsterFlag);
		}
		if (getMyTypeCount() == 0)
		{
			return false;
		}
		for (Iterator i = getMyTypeIterator(); i.hasNext();)
		{
			String aType = (String) i.next();
			ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(aType);
			if (aClassType != null && aClassType.isMonster())
			{
				return true;
			}
		}
		return false;
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
		private int levelProp = 0;
		private String property = "";

		LevelProperty(int argLevel, String argProperty)
		{
			levelProp = argLevel;
			property = argProperty;
		}

		public final int getLevel()
		{
			return levelProp;
		}

		public final String getProperty()
		{
			return property;
		}
	}

	/**
	 * Class section of Natural Attacks
	 * Is just a wrapper to remove the level dependent stuff
	 **/
	public void setNaturalAttacks(PObject obj, String aString)
	{
		final StringTokenizer attackTok = new StringTokenizer(aString, "|", false);
		int lvl = Integer.parseInt(attackTok.nextToken());
		String sNat = attackTok.nextToken();
		LevelProperty lp = new LevelProperty(lvl, sNat);
		if (naturalWeapons == null)
		{
			naturalWeapons = new ArrayList();
		}
		naturalWeapons.add(lp);
	}

	/**
	 * Parses the NATURALATTACK string and adds
	 * a generated Equipment item to wArray
	 **/
	private void parseNaturalAttackString(List wArray, String aString)
	{
		if ((aString == null) || (aString.length() <= 0))
		{
			return;
		}

		final PlayerCharacter aPC = Globals.getCurrentPC();
		String aSize = aPC.getSize();

		boolean firstWeapon = true;
		boolean onlyOne = false;

		final StringTokenizer attackTok = new StringTokenizer(aString, "|", false);
		// Make a preliminary guess at whether this is an "only" attack
		if (attackTok.countTokens() == 1)
		{
			onlyOne = true;
		}

		while (attackTok.hasMoreTokens())
		{

			StringTokenizer aTok = new StringTokenizer(attackTok.nextToken(), ",", false);
			Equipment anEquip = createNaturalWeapon(aTok, aSize);
			if (anEquip != null)
			{
				if (firstWeapon)
				{
					anEquip.setModifiedName("Natural/Primary");
				}
				else
				{
					anEquip.setModifiedName("Natural/Secondary");
				}
				if (onlyOne && anEquip.isOnlyNaturalWeapon())
				{
					anEquip.setOnlyNaturalWeapon(true);
				}
				else
				{
					anEquip.setOnlyNaturalWeapon(false);
				}
				wArray.add(anEquip);
			}
			firstWeapon = false;
		}
	}

	/**
	 * get the Natural Attacks for this level
	 **/
	public List getNaturalWeapons()
	{
		List tempArray = new ArrayList();
		if ((naturalWeapons == null) || (naturalWeapons.isEmpty()))
		{
			return tempArray;
		}
		for (Iterator li = naturalWeapons.iterator(); li.hasNext();)
		{
			LevelProperty lp = (LevelProperty) li.next();
			if (lp.getLevel() <= level)
			{
				parseNaturalAttackString(tempArray, lp.getProperty());
			}
		}
		return tempArray;
	}

	/**
	 * drString should be "5|4/-" where 5 = level, 4/- is the DR value.
	 * @param drString
	 */
	public void setDR(String drString)
	{
		if (".CLEAR".equals(drString))
		{
			DR = null;
			return;
		}
		StringTokenizer aTok = new StringTokenizer(drString, "|", false);
		int lvl = Integer.parseInt(aTok.nextToken());
		String tokenDrString = aTok.nextToken();
		if (".CLEAR".equals(tokenDrString))
		{
			DR = null;
		}
		else
		{
			if (DR == null)
			{
				DR = new ArrayList();
			}
			LevelProperty lp = new LevelProperty(lvl, tokenDrString);
			DR.add(lp);
		}
	}

	/**
	 * Assumption: DR list is sorted by level.
	 * @return
	 */
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

	/**
	 * needed for Class Editor - returns contents of DR(index).
	 * @param index
	 * @param delimiter
	 * @return
	 */
	public String getDRListString(int index, String delimiter)
	{
		if (DR != null && DR.size() > index)
		{
			LevelProperty lp = (LevelProperty) DR.get(index);
			return lp.getLevel() + delimiter + lp.getProperty();
		}
		return null;
	}

	/**
	 * should be "5|4/-" where 5 = level, 4/- is the SR value.
	 * @param srString
	 */
	public void setSR(String srString)
	{
		if (".CLEAR".equals(srString))
		{
			SR = null;
			return;
		}
		StringTokenizer aTok = new StringTokenizer(srString, "|", false);
		int lvl = Integer.parseInt(aTok.nextToken());
		String tokenSrString = aTok.nextToken();
		if (".CLEAR".equals(tokenSrString))
		{
			SR = null;
		}
		else
		{
			if (SR == null)
			{
				SR = new ArrayList();
			}
			LevelProperty lp = new LevelProperty(lvl, tokenSrString);
			SR.add(lp);
		}
	}

	/**
	 * needed for Class Editor - returns contents of SR(index).
	 * @param index
	 * @param delimiter
	 * @return
	 */
	public String getSRListString(int index, String delimiter)
	{
		if (SR != null && SR.size() > index)
		{
			LevelProperty lp = (LevelProperty) SR.get(index);
			return lp.getLevel() + delimiter + lp.getProperty();
		}
		return null;
	}

	/**
	 * Assumption: SR list is sorted by level.
	 * @return
	 */
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
		List specialAbilityList = getSpecialAbilityList();
		if (specialAbilityList != null)
		{
			for (int idx = specialAbilityList.size() - 1; idx >= 0; --idx)
			{
				SpecialAbility sa = (SpecialAbility) specialAbilityList.get(idx);
				if (sa.getSource().length() != 0)
				{
					sa = new SpecialAbility(sa.getName(), sa.getSASource(), sa.getSADesc());
					sa.setQualificationClass(oldClass, newClass);
					specialAbilityList.set(idx, sa);
				}
			}
		}

		//
		// Go through the variable list (DEFINE) and adjust the class to the new name
		//
		if (getVariableCount() > 0)
		{
			for (int idx = getVariableCount() - 1; idx >= 0; --idx)
			{
				String var = getVariableDefinition(idx);
				int offs = -1;
				for (; ;)
				{
					offs = var.indexOf('=' + oldClass, offs + 1);
					if (offs < 0)
					{
						break;
					}

					var = var.substring(0, offs + 1) + newClass + var.substring(offs + oldClass.length() + 1);
					setVariable(idx, var);
				}
			}
		}

		//
		// Go through the bonus list (BONUS) and adjust the class to the new name
		//
		if (getBonusList() != null)
		{
			List tempList = getBonusList();
			for (int idx = tempList.size() - 1; idx >= 0; --idx)
			{
				BonusObj aBonus = (BonusObj) tempList.get(idx);
				String bonus = aBonus.toString();
				int offs = -1;
				for (; ;)
				{
					offs = bonus.indexOf('=' + oldClass, offs + 1);
					if (offs < 0)
					{
						break;
					}

					addBonusList(bonus.substring(0, offs + 1) + newClass + bonus.substring(offs + oldClass.length() + 1));
					removeBonusList(aBonus);
				}
			}
		}
	}

	public final void setSpellBookUsed(boolean argUseBook)
	{
		usesSpellbook = argUseBook;
	}

	public final boolean getSpellBookUsed()
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
		final List aList = getCharacterSpell(null, bookName, aLevel);
		if (aList.isEmpty())
		{
			return m;
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell) i.next();
			if (cs.isSpecialtySpell())
			{
				m += cs.getSpellInfoFor(bookName, aLevel, -1).getTimes();
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
			if (!Globals.checkRule("INTBEFORE"))
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
			Object selectedValue = GuiFacade.showInputDialog(null, "Choose stat to increment or select Cancel to increment stat on the Summary tab." + extraMsg + "\n\n" + "Current Stats:\n" + sStats + "\n", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, Globals.s_ATTRIBLONG, Globals.s_ATTRIBLONG[0]);
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

	protected void doGlobalUpdate(final String aString)
	{
		//add to global PCClassType list for future filtering
		if (!Globals.getPCClassTypeList().contains(aString))
		{
			Globals.getPCClassTypeList().add(aString);
		}
	}

	public String getPCCText()
	{
		final StringBuffer pccTxt = new StringBuffer(200);
		pccTxt.append("CLASS:").append(getName());
		pccTxt.append(super.getPCCText(false));
		pccTxt.append("\tABB:").append(getAbbrev());
		checkAdd(pccTxt, "", "EXCLASS:", exClass);

		checkAdd(pccTxt, "", "EXCHANGELEVEL:", levelExchange);
		if (hasSubClass)
		{
			pccTxt.append("\tHASSUBCLASS:Y");
		}
		pccTxt.append("\tHD:").append(hitDie);
		checkAdd(pccTxt, "ANY", "DEITY:", deityString);
		pccTxt.append("\tATTACKCYCLE:").append(attackCycle);
		checkAdd(pccTxt, "", "CASTAS:", castAs);
		checkAdd(pccTxt, Constants.s_NONE, "PROHIBITED:", prohibitedString);
		checkAdd(pccTxt, Constants.s_NONE, "SPELLSTAT:", spellBaseStat);
		checkAdd(pccTxt, Constants.s_NONE, "SPELLTYPE:", spellType);
		if (usesSpellbook)
		{
			pccTxt.append("\tSPELLBOOK:Y");
		}
		if (skillPoints != 0)
		{
			pccTxt.append("\tSTARTSKILLPTS:").append(skillPoints);
		}
		if (!visible)
		{
			pccTxt.append("\tVISIBLE:N");
		}
		if (initialFeats != 0)
		{
			pccTxt.append("\tXTRAFEATS:").append(initialFeats);
		}
		if (levelsPerFeat != null)
		{
			pccTxt.append("\tLEVELSPERFEAT:").append(levelsPerFeat.intValue());
		}
		if (maxLevel != 20)
		{
			pccTxt.append("\tMAXLEVEL:").append(maxLevel);
		}
		if (!memorizeSpells)
		{
			pccTxt.append("\tMEMORIZE:N");
		}
		if (multiPreReqs)
		{
			pccTxt.append("\tMULTIPREREQS:Y");
		}

		if (!knownSpellsList.isEmpty())
		{
			pccTxt.append("\tKNOWNSPELLS:");
			boolean flag = false;

			for (Iterator e = knownSpellsList.iterator(); e.hasNext();)
			{
				if (flag)
				{
					pccTxt.append('|');
				}
				flag = true;
				pccTxt.append((String) e.next());
			}
		}

		if (itemCreationMultiplier.length() != 0)
		{
			pccTxt.append("\tITEMCREATE:").append(itemCreationMultiplier);
		}

		if (classSpellString != null)
		{
			pccTxt.append("\tSPELLLIST:").append(classSpellString).append('\t');
		}
		checkAdd(pccTxt, "", "SPECIALS:", specialsString);
		checkAdd(pccTxt, "", "SKILLLIST:", classSkillString);

		if (weaponProfBonus.size() != 0)
		{
			pccTxt.append("\tWEAPONBONUS:");
			for (int x = 0; x < weaponProfBonus.size(); ++x)
			{
				if (x != 0)
				{
					pccTxt.append('|');
				}
				pccTxt.append(weaponProfBonus.get(x));
			}
		}

// now all the level-based stuff
		String lineSep = System.getProperty("line.separator");

		if (regionString != null && !regionString.startsWith("0|"))
		{
			int x = regionString.indexOf('|');
			pccTxt.append(lineSep).append(regionString.substring(0, x)).append("\tREGION:").append(regionString.substring(x + 1));
		}

//		if (kitString != null && !kitString.startsWith("0|"))
//		{
//			int x = kitString.indexOf('|');
//			pccTxt.append(lineSep + kitString.substring(0, x)).append("\tKIT:").append(kitString.substring(x + 1));
//		}
		if (kits != null)
		{
			for (int iKit = 0; iKit < kits.size(); ++iKit)
			{
				String kitString = (String) kits.get(iKit);
				int x = kitString.indexOf('|');
				if (x >= 0)
				{
					pccTxt.append(lineSep + kitString.substring(0, x)).append("\tKIT:").append(kitString.substring(x + 1));
				}
			}
		}

		for (int x = 0; x < specialtyknownList.size(); ++x)
		{
			pccTxt.append("\tSPECIALTYKNOWN:").append(specialtyknownList.get(x));
		}

		pccTxt.append(lineSep);

		for (int x = 0; x < castMap.size(); ++x)
		{
			if (castMap.containsKey(String.valueOf(x)))
			{
				String c = (String) castMap.get(String.valueOf(x));
				String l = lineSep + String.valueOf(x + 1) + "\tCAST:";
				checkAdd(pccTxt, "0", l, c);
			}
		}
		for (int x = 0; x < knownList.size(); ++x)
		{
			String c = (String) knownList.get(x);
			String l = lineSep + String.valueOf(x + 1) + "\tKNOWN:";
			checkAdd(pccTxt, "0", l, c);
		}

		if (DR != null)
		{
			for (Iterator li = DR.iterator(); li.hasNext();)
			{
				Object obj = li.next();
				if ((obj instanceof LevelProperty))
				{
					pccTxt.append(lineSep).append(((LevelProperty) obj).getLevel()).append("\tDR:").append(((LevelProperty) obj).getProperty());
				}
			}
		}

		if (SR != null)
		{
			for (Iterator li = SR.iterator(); li.hasNext();)
			{
				Object obj = li.next();
				if ((obj instanceof LevelProperty))
				{
					pccTxt.append(lineSep).append(((LevelProperty) obj).getLevel()).append("\tSR:").append(((LevelProperty) obj).getProperty());
				}
			}
		}

		List spellList = getSpellList();
		if (spellList != null)
		{
			for (Iterator li = spellList.iterator(); li.hasNext();)
			{
				Object obj = li.next();
				if ((obj instanceof LevelProperty))
				{
					pccTxt.append(lineSep).append(((LevelProperty) obj).getLevel()).append("\tSPELL:").append(((LevelProperty) obj).getProperty());
				}
			}
		}

		for (int x = 0; x < templates.size(); ++x)
		{
			String c = (String) templates.get(x);
			int y = c.indexOf('|');
			pccTxt.append(lineSep).append(c.substring(0, y)).append("\tTEMPLATE:").append(c.substring(y + 1));
		}
		for (int x = 0; x < getBonusList().size(); ++x)
		{
			final BonusObj aBonus = (BonusObj) getBonusList().get(x);
			String bonusString = aBonus.toString();
			final int levelEnd = bonusString.indexOf('|');
			final String maybeLevel = bonusString.substring(0, levelEnd);

			pccTxt.append(lineSep);
			if (Utility.isIntegerString(maybeLevel))
			{
				pccTxt.append(maybeLevel);
				bonusString = bonusString.substring(levelEnd + 1);
			}
			else
			{
				pccTxt.append("0");
			}
			pccTxt.append("\tBONUS:").append(bonusString);

		}
		for (int x = 0; x < getVariableCount(); ++x)
		{
			String c = getVariableDefinition(x);
			int y = c.indexOf('|');
			pccTxt.append(lineSep).append(c.substring(0, y)).append("\tDEFINE:").append(c.substring(y + 1));
		}
		if (levelAbilityList != null && !levelAbilityList.isEmpty())
		{
			for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
			{
				LevelAbility ability = (LevelAbility) e.next();
				pccTxt.append(lineSep).append(String.valueOf(ability.level())).append("\tADD:").append(ability.getList());
			}
		}

		List specialAbilityList = getSpecialAbilityList();
		if ((specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for (Iterator se = specialAbilityList.iterator(); se.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) se.next();
				String src = sa.getSource();
				String lev = src.substring(src.lastIndexOf('|') + 1);
				pccTxt.append(lineSep).append(lev).append("\tSA:").append(sa.toString());
			}
		}

		if ((addDomains != null) && (addDomains.size() != 0))
		{
			buildPccText(pccTxt, addDomains.iterator(), "|", "\tADDDOMAINS:", lineSep);
		}

		buildPccText(pccTxt, domainList.iterator(), "|", "\tDOMAIN:", lineSep);

		buildPccText(pccTxt, featList.iterator(), ":", "\tFEAT:", lineSep);

		//buildPccText(pccTxt, vFeatList.iterator(), ":", "\tVFEAT:", lineSep);

		buildPccText(pccTxt, featAutos.iterator(), "|", "\tFEATAUTO:", lineSep);

		if ((uattList != null) && (uattList.size() != 0))
		{
			for (int x = 0; x < uattList.size(); ++x)
			{
				pccTxt.append(lineSep).append(String.valueOf(x + 1)).append("\tUATT:").append((String) uattList.get(x));
			}
		}

		if ((udamList != null) && (udamList.size() != 0))
		{
			for (int x = 0; x < udamList.size(); ++x)
			{
				pccTxt.append(lineSep).append(String.valueOf(x + 1)).append("\tUDAM:").append(udamList.get(x));
			}
		}

		if ((umultList != null) && (umultList.size() != 0))
		{
			buildPccText(pccTxt, umultList.iterator(), "|", "\tUMULT:", lineSep);
		}

		return pccTxt.toString();
	}

	private static void buildPccText(final StringBuffer pccTxt, Iterator listIterator, String separator, String label, String lineSep)
	{
		while (listIterator.hasNext())
		{
			final String listItem = (String) listIterator.next();
			final int sepPos = listItem.indexOf(separator);
			pccTxt.append(lineSep).append(listItem.substring(0, sepPos)).append(label).append(listItem.substring(sepPos + 1));
		}
	}

	private static void checkAdd(StringBuffer txt, String comp, String label, String value)
	{
		if (value != null && !comp.equals(value))
		{
			txt.append('\t').append(label).append(value);
		}
	}

	public int calcCR()
	{
		String wCRFormula = "0";

		if (CRFormula != null)
		{
			wCRFormula = CRFormula;
		}
		else
		{
			for (Iterator i = getMyTypeIterator(); i.hasNext();)
			{
				String aType = (String) i.next();
				ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(aType);
				if (aClassType != null && !"0".equals(aClassType.getCRFormula()))
				{
					wCRFormula = aClassType.getCRFormula();
				}
			}
		}
		return Globals.getCurrentPC().getVariableValue(wCRFormula, "CLASS:" + getName()).intValue();
	}

	public void setMonsterFlag(String monster)
	{
		monsterFlag = monster;
	}

	public void setCRFormula(String argCRFormula)
	{
		CRFormula = argCRFormula;
	}

	public void setXPPenalty(String argXPPenalty)
	{
		XPPenalty = argXPPenalty;
	}

	public boolean hasXPPenalty()
	{
		String wXPPenalty = "YES";
		if (XPPenalty != null)
		{
			return "YES".equals(XPPenalty);
		}
		else
		{
			for (Iterator i = getMyTypeIterator(); i.hasNext();)
			{
				String aType = (String) i.next();
				ClassType aClassType = SettingsHandler.getGame().getClassTypeByName(aType);
				if (aClassType != null && !aClassType.getXPPenalty())
				{
					wXPPenalty = "NO";
				}
			}
		}
		return "YES".equals(wXPPenalty);
	}

	public boolean hasKnownSpells()
	{
		for (int i = 0; i <= 9; i++)
		{
			if (getKnownForLevel(getLevel(), i) > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	
}
