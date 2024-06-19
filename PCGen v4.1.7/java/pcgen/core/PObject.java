/*
 * PObject.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id: PObject.java,v 1.1 2006/02/21 00:57:42 vauchers Exp $
 */
package pcgen.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.gui.ChooserFactory;
import pcgen.gui.ChooserInterface;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Delta;
import pcgen.util.GuiFacade;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PObject extends Object implements Cloneable, Serializable, Comparable
{
	public final CharacterSpell getCharacterSpellForSpell(Spell aSpell, PObject anOwner)
	{
		if (aSpell == null || characterSpellList == null)
		{
			return null;
		}
		for (Iterator i = characterSpellList.iterator(); i.hasNext();)
		{
			final CharacterSpell cs = (CharacterSpell) i.next();
			final Spell bSpell = cs.getSpell();
			if (aSpell.equals(bSpell) && (anOwner == null || cs.getOwner().equals(anOwner)))
			{
				return cs;
			}
		}
		return null;
	}

	//
	// return an ArrayList of CharacterSpell with following criteria:
	// Spell aSpell  (ignored if null),
	// book          (ignored if ""),
	// level         (ignored if < 0),
	// fList         (ignored if null) Array of Feats
	//

	public final ArrayList getCharacterSpell(Spell aSpell, String book, int level)
	{
		return getCharacterSpell(aSpell, book, level, null);
	}

	private static final long serialVersionUID = 1;

	private static final ArrayList emptyBonusList = new ArrayList();
	protected String DR = null; // now a string so that we can handle formula
	protected String SR = null; //now a string so that we can handle formula
	protected ArrayList associatedList = null;
	protected ArrayList bonusList = null;
	protected String choiceString = "";
	protected boolean isSpecified = false;
	protected String keyName = "";
	protected String name = "";
	protected String outputName = "";
	protected ArrayList specialAbilityList = null;
	protected ArrayList spellList = null;
	protected ArrayList udamList = null;
	protected ArrayList umultList = null;
	protected boolean visible = true;
	protected HashMap vision = null; // now a String available globally
	protected ArrayList weaponProfAutos = null;

	private int baseQuantity = 1;
	private HashMap bonusMap = null;
	private ArrayList cSkillList = null;
	private ArrayList ccSkillList = null;
	private ArrayList characterSpellList = null;
	private boolean isNewItem = true;
	private TreeSet languageAutos = null;
	private ArrayList myTypeList = null;
	private ArrayList preReqList = null;
	private String qualifyString = "alwaysValid";
	private ArrayList saveList = null;
	private ArrayList selectedWeaponProfBonus = null;
	private String source = "";
	private int sourceFileIndex = -1;
	private int sourceIndex = -1;
	private String sourcePage = "";
	private ArrayList variableList = null;
	private boolean nameIsPI = false; // name is Product Identity
	private String kitString = null;
	private String regionString = null;
	private String autoString = null; // AUTO: tag
	public ArrayList selectedArmorProfs = null;

	public final boolean getNameIsPI()
	{
		return nameIsPI;
	}

	public final void setNameIsPI(boolean a)
	{
		nameIsPI = a;
	}

	public final void setAssociated(int index, String aString)
	{
		associatedList.set(index, aString);
	}

	public final String getAssociated(int idx)
	{
		return getAssociated(idx, false);
	}

	public final Object getAssociatedObject(int idx)
	{
		return associatedList.get(idx);
	}

	public final String getAssociated(int idx, boolean expand)
	{
		if (expand && (associatedList.get(0) instanceof FeatMultipleChoice))
		{
			FeatMultipleChoice fmc;
			int iCount;
			for (int i = 0; i < associatedList.size(); ++i)
			{
				fmc = (FeatMultipleChoice) associatedList.get(i);
				iCount = fmc.getChoiceCount();
				if (idx < iCount)
				{
					return fmc.getChoice(idx);
				}
				idx -= iCount;
			}
			return "";
		}
		return associatedList.get(idx).toString();
	}

	public final int getAssociatedCount()
	{
		return getAssociatedCount(false);
	}

	public final int getAssociatedCount(boolean expand)
	{
		if (associatedList == null)
		{
			return 0;
		}

		if (expand && (associatedList.get(0) instanceof FeatMultipleChoice))
		{
			int iCount = 0;
			for (int i = 0; i < associatedList.size(); ++i)
			{
				iCount += ((FeatMultipleChoice) associatedList.get(i)).getChoiceCount();
			}
			return iCount;
		}
		return associatedList.size();
	}

	/** return Set of Strings of Language names */
	private final Set getAutoLanguageNames()
	{

		final Set aSet = getAutoLanguages();
		for (Iterator i = aSet.iterator(); i.hasNext();)
		{
			aSet.add(i.next().toString());
		}
		return aSet;
	}

	/** return Set of Language objects */
	public final Set getAutoLanguages()
	{

		final Set aSet = new TreeSet();
		if (languageAutos == null || languageAutos.isEmpty())
		{
			return aSet;
		}

		for (Iterator i = languageAutos.iterator(); i.hasNext();)
		{

			final String aLang = i.next().toString();
			if ("ALL".equals(aLang))
			{
				aSet.addAll(Globals.getLanguageList());
			}
			else if (aLang.startsWith("TYPE="))
			{

				String bString = aLang.substring(5);
				ArrayList bList = new ArrayList();
				bList = Globals.getLanguageList();
				bList = Globals.getLanguagesFromListOfType(bList, bString);
				aSet.addAll(bList);
			}
			else
			{

				final Language bLang = Globals.getLanguageNamed(aLang);
				if (bLang != null)
				{
					aSet.add(aLang);

				}
			}
		}
		return aSet;
	}

	public final void setBaseQty(String aString)
	{
		baseQuantity = Integer.parseInt(aString);
	}

	final int getBaseQty()
	{
		return baseQuantity;
	}

	public ArrayList getBonusList()
	{
		if (bonusList != null)
		{
			return bonusList;
		}
		return emptyBonusList;
	}

	public String getBonusListString()
	{

		final String s = getBonusList().toString();

		if ("[]".equals(s))
		{
			return "";
		}

		// Don't display the surrounding brackets.
		else
		{
			return s.substring(1, s.length() - 1);
		}
	}

	final HashMap getBonusMap()
	{
		if (bonusMap == null)
		{
			bonusMap = new HashMap();
		}
		return bonusMap;
	}

	final void setBonusStackFor(double bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
		}

		int index = -2;
		final StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);

		// e.g. "COMBAT.AC.Dodge"
		if (aTok.countTokens() > 2 && !bonusType.endsWith(".STACK"))
		{

			String aString = aTok.nextToken(); // should be bonus category e.g. "COMBAT"
			aString = aTok.nextToken(); // should be bonus name e.g. "AC"
			aString = aTok.nextToken(); // should be bonus type e.g. whatever
			if (aString != null && !"null".equalsIgnoreCase(aString))
			{
				index = Globals.getBonusStackList().indexOf(aString); // e.g. Dodge
			}
		}

		if (index == -1) // meaning, a non-stacking bonus
		{

			final String aKey = (String) getBonusMap().get(bonusType);
			if (aKey == null)
			{
				getBonusMap().put(bonusType, String.valueOf(bonus));
			}
			else
			{
				getBonusMap().put(bonusType, String.valueOf(Math.max(bonus, Float.parseFloat(aKey))));
			}
		}
		else // stacking bonuses
		{
			if (bonusType == null)
			{
				bonusType = "";
			}
			else if (bonusType.endsWith(".STACK"))
			{
				bonusType = bonusType.substring(0, bonusType.length() - 6);
			}

			final String aKey = (String) getBonusMap().get(bonusType);
			if (aKey == null)
			{
				getBonusMap().put(bonusType, String.valueOf(bonus));
			}
			else
			{
				getBonusMap().put(bonusType, String.valueOf(bonus + Float.parseFloat(aKey)));
			}
		}
	}

	public final void setCSkillList(String aString)
	{
		if (cSkillList == null)
		{
			cSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{

			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				cSkillList.clear();
			}
			else if (bString.startsWith("TYPE."))
			{

				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					if (aSkill.isType(bString.substring(5)))
					{
						cSkillList.add(aSkill.getName());
					}
				}
			}
			else
			{
				cSkillList.add(bString);
			}
		}
	}

	public final ArrayList getCSkillList()
	{
		return cSkillList;
	}

	public final void setCcSkillList(String aString)
	{
		if (ccSkillList == null)
		{
			ccSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{

			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				ccSkillList.clear();
			}
			else if (bString.startsWith("TYPE."))
			{

				Skill aSkill = null;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					if (aSkill.isType(bString.substring(5)))
					{
						ccSkillList.add(aSkill.getName());
					}
				}
			}
			else
			{
				ccSkillList.add(bString);
			}
		}
	}

	public final ArrayList getCcSkillList()
	{
		return ccSkillList;
	}

	public final ArrayList getCharacterSpell(Spell aSpell, String book, int level, ArrayList fList)
	{

		final ArrayList aList = new ArrayList();
		if (getCharacterSpellCount() == 0)
		{
			return aList;
		}

		for (Iterator i = characterSpellList.iterator(); i.hasNext();)
		{
			CharacterSpell cs = (CharacterSpell) i.next();
			if (aSpell == null || cs.getSpell().equals(aSpell))
			{
				SpellInfo si = cs.getSpellInfoFor(book, level, -1, fList);
				if (si != null)
				{
					aList.add(cs);
				}
			}
		}
		return aList;
	}

	public final int getCharacterSpellCount()
	{
		if (characterSpellList == null)
		{
			return 0;
		}
		return characterSpellList.size();
	}

	public String getSpellKey()
	{
		return "POBJECT|" + name;
	}

	public void setChoiceString(String choiceString)
	{
		this.choiceString = choiceString;
	}

	public final String getChoiceString()
	{
		return choiceString;
	}

	final void getChoices(String aChoice, ArrayList selectedBonusList, ArrayList availableList, ArrayList selectedList)
	{
		getChoices(aChoice, selectedBonusList, this, availableList, selectedList, true);
	}

	public final void getChoices(String aChoice, ArrayList selectedBonusList)
	{

		final ArrayList availableList = new ArrayList();
		final ArrayList selectedList = new ArrayList();
		getChoices(aChoice, selectedBonusList, this, availableList, selectedList, true);
	}

	public void setDR(int anInt, String drString)
	{
		if (".CLEAR".equals(drString))
		{
			DR = null;
		}
		else if (DR == null || DR.length() == 0)
		{
			DR = drString;
		}
		else
		{
			//
			// Is this right? Shouldn't it be DR += ... ?
			// -Byngl
			//
			DR = '|' + drString;
		}
	}

	public String getDR()
	{
		return DR;
	}

	public final void setKeyName(String aString)
	{
		keyName = aString;
	}

	public final String getKeyName()
	{
		return keyName;
	}

	public final void setLanguageAutos(String aString)
	{
		if (languageAutos == null)
		{
			languageAutos = new TreeSet();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, ",", false);
		while (aTok.hasMoreTokens())
		{

			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				languageAutos.clear();
			}
			else
			{
				languageAutos.add(bString);
			}
		}
	}

	public String getMyType(int i)
	{
		if (i < getMyTypeCount())
		{
			return (String) myTypeList.get(i);
		}
		return null;
	}

	public int getMyTypeCount()
	{
		if (myTypeList == null)
		{
			return 0;
		}
		return myTypeList.size();
	}

	public void setName(String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			name = aString;
			keyName = aString;
		}
	}

	public String getName()
	{
		return name;
	}

	public final void setOutputName(String aString)
	{
		String newName = aString;

		//process the intended output name, replacing [NAME] token

		if (newName.indexOf("[NAME]") >= 0)
		{
			final StringBuffer sb = new StringBuffer(newName.substring(0, newName.indexOf("[NAME]")));
			//and rephrasing parenthetical name components
			sb.append(getPreFormatedOutputName());
			sb.append(newName.substring(newName.indexOf("[NAME]") + 6));
			newName = sb.toString();
		}

		outputName = newName;
	}

	//rephrase parenthetical name components, if appropriate
	private String getPreFormatedOutputName()
	{
		//if there are no () to pull from, just return the name
		if ((name.indexOf('(') < 0) || (name.indexOf(')') < 0))
		{
			return name;
		}

		//we just take from the first ( to the first ), typically there should only be one of each
		final String subName = name.substring(name.indexOf('(') + 1, name.indexOf(')')); //the stuff inside the ()
		final StringTokenizer tok = new StringTokenizer(subName, "/");
		final StringBuffer newNameBuff = new StringBuffer();
		while (tok.hasMoreTokens())
		{
			//build this new string from right to left
			newNameBuff.insert(0, tok.nextToken());
			if (tok.hasMoreTokens())
			{
				newNameBuff.insert(0, " ");
			}
		}
		return newNameBuff.toString();
	}

	public final String getOutputName()
	{
		// if no OutputName has been defined, just return the regular name
		if (outputName.length() == 0)
		{
			return name;
		}
		return outputName;
	}

	public final void setNewItem(boolean newItem)
	{
		this.isNewItem = newItem;
	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////
	public final boolean isNewItem()
	{
		return isNewItem;
	}

	final void setPreReq(int index, String aString)
	{
		preReqList.set(index, aString);
	}

	public final ArrayList getPreReqList()
	{
		return preReqList;
	}

	public final String getPreReq(int i)
	{
		return (String) preReqList.get(i);
	}

	public final int getPreReqCount()
	{
		if (preReqList == null)
		{
			return 0;
		}
		return preReqList.size();
	}

	public final void setQualifyString(String aString)
	{
		qualifyString = aString;
	}

	final String getQualifyString()
	{
		return qualifyString;
	}

	public final void setSR(String newSR)
	{
		SR = newSR;
	}

	public final String getSRFormula()
	{
		return SR;
	}

	protected int getSR()
	{

		//if there's a current PC, go ahead and evaluate the formula
		if (SR != null && Globals.getCurrentPC() != null)
		{
			return Globals.getCurrentPC().getVariableValue(SR, "").intValue();
		}
		return 0;
	}

	public final String getSave(int i)
	{
		return (String) saveList.get(i);
	}

	/**
	 * Return the number of saves in the list
	 * author: Scott Ellsworth 20020601
	 *
	 * @return save list
	 */
	public final int getSaveCount()
	{
		if (saveList == null)
		{
			return 0;
		}
		return saveList.size();
	}

	public final String getSelectedWeaponProfBonus(int i)
	{
		return (String) selectedWeaponProfBonus.get(i);
	}

	public final int getSelectedWeaponProfBonusCount()
	{
		if (selectedWeaponProfBonus == null)
		{
			return 0;
		}
		return selectedWeaponProfBonus.size();
	}

	public final void setSource(int srcIdx, String aSource)
	{
		this.sourceIndex = srcIdx;
		setSource(aSource);
	}

	public final void setSource(final String aSource)
	{
		if (".CLEAR".equals(aSource))
		{
			source = "";
		}
		else
		{
			final StringBuffer src = new StringBuffer(source);
			if (src.length() > 0)
			{
				src.append('|');
			}
			source = src.append(aSource).toString();
		}
	}

	public final String getSourceInForm(int sourceDisplay)
	{
		return returnSourceInForm(sourceDisplay, true);
	}

	private String returnSourceInForm(int sourceDisplay, boolean includePage)
	{
		if ("".equals(source) && (sourceIndex < 0))
		{
			return PersistenceManager.savedSourceFile(sourceFileIndex);
		}
		String bigString = PersistenceManager.savedSource(sourceIndex);
		if (bigString.endsWith(", "))
		{
			bigString = bigString.substring(0, bigString.length() - 2);
		}
		if (bigString.length() > 0)
		{
			bigString += "|";
		}
		bigString += source;

		final StringBuffer sString = new StringBuffer();
		final StringTokenizer aTok = new StringTokenizer(bigString, "|", false);
		String pageString = "";
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.startsWith("SOURCEPAGE:"))
			{
				pageString = aString.substring(11);
			}
			switch (sourceDisplay)
			{

				case Constants.SOURCELONG:
					if (aString.startsWith("SOURCELONG:") || aString.startsWith("SOURCE:"))
					{
						if (sString.length() > 0)
						{
							sString.append(' ');
						}
						if (aString.startsWith("SOURCELONG:"))
						{
							sString.append(aString.substring(11));
						}
						else if (sString.length() == 0)
						{
							sString.append(aString.substring(7));
						}
						else
						{
							pageString = aString.substring(7);
						}
					}
					break;

				case Constants.SOURCESHORT:
					if (aString.startsWith("SOURCESHORT:") || aString.startsWith("SOURCE:"))
					{
						if (sString.length() > 0)
						{
							sString.append(' ');
						}
						if (aString.startsWith("SOURCESHORT:"))
						{
							sString.append(aString.substring(12));
						}
						else if (sString.length() == 0)
						{
							sString.append(aString.substring(7));
						}
					}
					break;
				case Constants.SOURCEWEB:
					if (aString.startsWith("SOURCEWEB:") || aString.startsWith("SOURCE:"))
					{
						if (sString.length() > 0)
						{
							sString.append(' ');
						}
						if (aString.startsWith("SOURCEWEB:"))
						{
							sString.append(aString.substring(10));
						}
						else if (sString.length() == 0)
						{
							sString.append(aString.substring(7));
						}
					}
					break;
				default: // this is for Page-only display
//					Globals.errorPrint("In PObject.getSource the sourceDisplay " + sourceDisplay + " is unsupported.");
					break;
			}
		}
		if (includePage && pageString.length() > 0 && sourceDisplay != Constants.SOURCEWEB)
		{
			if (sString.length() > 0)
			{
				sString.append(", ").append(pageString);
			}
			else
			{
				return pageString;
			}
		}
		return sString.toString();
	}

	public String getSource()
	{
		return getSourceInForm(Globals.getSourceDisplay());
	}

	public final void setSourceFile(String sourceFile)
	{
		sourceFileIndex = PersistenceManager.saveSourceFile(sourceFile);
	}

	public final String getSourceFile()
	{
		return PersistenceManager.savedSourceFile(sourceFileIndex);
	}

	public final String getSourceNoPage()
	{
		return returnSourceInForm(Constants.SOURCELONG, false);
	}

	public final String getSourcePage()
	{

		String pageString = "";
		if (source.startsWith("SOURCEPAGE:"))
		{
			pageString = source.substring(11);
		}
		return pageString;
	}

	public final String getSourceShort()
	{
		if ("".equals(source) && (sourceIndex < 0))
		{
			return PersistenceManager.savedSourceFile(sourceFileIndex);
		}

		String bigString = PersistenceManager.savedSource(sourceIndex);
		if (bigString.endsWith(", "))
		{
			bigString = bigString.substring(0, bigString.length() - 2);
		}
		if (bigString.length() > 0)
		{
			bigString += "|";
		}
		bigString += source;

		final StringTokenizer aTok = new StringTokenizer(bigString, "|", false);
		String shortString = "";
		final StringBuffer sString = new StringBuffer();
		while (aTok.hasMoreTokens())
		{

			final String aString = aTok.nextToken();
			if (aString.startsWith("SOURCESHORT:") || aString.startsWith("SOURCE:"))
			{
				if (sString.length() > 0)
				{
					sString.append(' ');
				}
				if (aString.startsWith("SOURCESHORT:"))
				{
					sString.append(aString.substring(12));
				}
				else if (sString.length() == 0)
				{
					sString.append(aString.substring(7));
				}
			}
		}
		shortString = sString.toString();

		// When I say short, I mean short!
		// Truncate at 8 chars
		if (shortString.length() > 8)
		{
			shortString = shortString.substring(0, 8);
		}
		return shortString;
	}

	public final String getSourceWeb()
	{
		if ("".equals(source) && (sourceIndex < 0))
		{
			return PersistenceManager.savedSourceFile(sourceFileIndex);
		}

		String bigString = PersistenceManager.savedSource(sourceIndex);
		if (bigString.endsWith(", "))
		{
			bigString = bigString.substring(0, bigString.length() - 2);
		}
		if (bigString.length() > 0)
		{
			bigString += "|";
		}
		bigString += source;

		final StringBuffer sString = new StringBuffer();
		final StringTokenizer aTok = new StringTokenizer(bigString, "|", false);
		while (aTok.hasMoreTokens())
		{

			final String aString = aTok.nextToken();
			if (aString.startsWith("SOURCEWEB:"))
			{
				sString.append(aString.substring(10));
				break;
			}
		}
		return sString.toString();
	}

	/**
	 * This method sets the special abilities granted by this [object].
	 * For efficiency, avoid calling this method except from I/O routines.
	 * @param aString String of special abilities delimited by pipes
	 * @param level int level at which the ability is gained
	 */
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

			SpecialAbility sa = new SpecialAbility(bString);
			specialAbilityList.add(sa);
		}
	}

	public final ArrayList getSpecialAbilityList()
	{
		return specialAbilityList;
	}

	public final SpecialAbility getSpecialAbilityNamed(String aName)
	{
		if (specialAbilityList != null)
		{
			for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
			{

				final SpecialAbility sa = (SpecialAbility) i.next();
				if (sa.getName().equals(aName))
				{
					return sa;
				}
			}
		}
		return null;
	}

	final ArrayList getSpellList()
	{
		return spellList;
	}

	static void getSpellTypeChoices(String aChoice, ArrayList availList, ArrayList uniqueList)
	{

		final StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
		aTok.nextToken(); // should be SPELLLEVEL
		while (aTok.hasMoreTokens())
		{

			String aString = aTok.nextToken();
			while (!aString.startsWith("CLASS=") && !aString.startsWith("TYPE=") && aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
			}
			if (!aTok.hasMoreTokens())
			{
				break;
			}

			boolean endIsUnique = false;
			final int minLevel = Integer.parseInt(aTok.nextToken());
			String mString = aTok.nextToken();
			if (mString.endsWith(".A"))
			{
				endIsUnique = true;
				mString = mString.substring(0, mString.lastIndexOf(".A"));
			}

			int maxLevel = minLevel;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aString.startsWith("CLASS="))
			{

				final PCClass aClass = aPC.getClassKeyed(aString.substring(6));
				int i = 0;
				while (i < mString.length())
				{
					if ((mString.length() > 7 + i) && "MAXLEVEL".equals(mString.substring(i, i + 8)))
					{

						int j = -1;
						final int aLevel = aClass.getLevel().intValue() - 1;
						if (aLevel >= 0)
						{ // some classes, like "Domain" are level 0, so this index would be -1

							String tempString = aClass.getCastList().get(aLevel).toString();
							final StringTokenizer bTok = new StringTokenizer(tempString, ",", false);
							j = bTok.countTokens() - 1;
						}

						String bString = "";
						if (mString.length() > i + 8)
						{
							bString = mString.substring(i + 8);
						}
						mString = mString.substring(0, i) + new Integer(j).toString() + bString;
						--i; // back up one since we just did a replacement
					}
					++i;
				}
				maxLevel = aPC.getVariableValue(mString, "").intValue();
				if (aClass != null)
				{

					final String prefix = aClass.getName() + " ";
					for (int j = minLevel; j <= maxLevel; ++j)
					{

						final String bString = prefix + j;
						if (!availList.contains(bString))
						{
							availList.add(bString);
						}
						if (j == maxLevel && endIsUnique)
						{
							uniqueList.add(bString);
						}
					}
				}
			}
			if (aString.startsWith("TYPE="))
			{
				aString = aString.substring(5);
				for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
				{

					final PCClass aClass = (PCClass) e.next();
					if (aClass.getSpellType().equals(aString))
					{
						if (mString.startsWith("MAXLEVEL"))
						{

							int aLevel = aClass.getLevel().intValue() - 1;
							for (Iterator e1 = aPC.getClassList().iterator(); e1.hasNext();)
							{

								final PCClass bClass = (PCClass) e1.next();
								aLevel += (int) bClass.getBonusTo("PCLEVEL", aClass.getName(), 0);
							}

							String bString = "0";
							if (aLevel >= 0) // some classes, like "Domain" are level 0, so this index would be -1
							{
								bString = aClass.getCastList().get(aLevel).toString();
							}
							if ("0".equals(bString))
							{
								maxLevel = -1;
							}
							else
							{

								final StringTokenizer bTok = new StringTokenizer(bString, ",", false);
								maxLevel = bTok.countTokens() - 1;
							}
							if (mString.length() > 8)
							{
								mString = mString.substring(8);
								maxLevel += Delta.decode(mString).intValue();
							}
						}

						final String prefix = aClass.getName() + " ";
						for (int i = minLevel; i <= maxLevel; ++i)
						{

							final String bString = prefix + i;
							if (!availList.contains(bString))
							{
								availList.add(bString);
							}
							if (i == maxLevel && endIsUnique)
							{
								uniqueList.add(bString);
							}
						}
					}
				}
			}
		}
	}

	public void setType(String aString)
	{
		clearMyType();
		addType(aString);
	}

	public String getType()
	{

		final StringBuffer aType = new StringBuffer(getMyTypeCount() * 5); //Just a guess.
		for (int i = 0; i < getMyTypeCount(); ++i)
		{
			if (i != 0)
			{
				aType.append('.');
			}
			aType.append(getMyType(i));
		}
		return aType.toString();
	}

	/**
	 * If aType begins with an '!' the '!' will be removed before checking the type.
	 *
	 * @param aType
	 * @return Whether the item is of this type
	 */
	public boolean isType(String aType)
	{
		String myType = null;
		if (aType.length() > 0 && aType.charAt(0) == '!')
		{
			myType = aType.substring(1).toUpperCase();
		}
		else
		{
			myType = aType.toUpperCase();
		}

		return containsMyType(myType);
	}

	final String getUdamFor(boolean includeCrit, boolean includeStrBonus)
	{

		// the assumption is that there is only one UDAM: tag for things other than class
		if (udamList == null || udamList.isEmpty())
		{
			return "";
		}

		final StringBuffer aString = new StringBuffer(udamList.get(0).toString());
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final int b = (int) aPC.getStatBonusTo("DAMAGE", "TYPE=MELEE");
		if (includeStrBonus && b > 0)
		{
			aString.append('+');
		}
		if (includeStrBonus && b != 0)
		{
			aString.append(String.valueOf(b));
		}
		if (includeCrit && umultList != null && !umultList.isEmpty())
		{

			final String dString = umultList.get(0).toString();
			if (!"0".equals(dString))
			{
				aString.append("(x").append(dString).append(')');
			}
		}
		return aString.toString();
	}

	final ArrayList getUdamList()
	{
		return udamList;
	}

	public final String getVariable(int i)
	{
		return (String) variableList.get(i);
	}

	public final void clearVariableList()
	{
		variableList = null;
	}

	public final int getVariableCount()
	{
		if (variableList == null)
		{
			return 0;
		}
		return variableList.size();
	}

	public final void setVision(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		while (aTok.hasMoreTokens())
		{
			final String bString = aTok.nextToken();

			// This is a hack to fix a specific bug.  It is
			// unintelligent.  FIXME XXX
			if (".CLEAR".equals(bString))
			{
				vision = null;
				continue;
			}

			String cString;
			String dString;
			if (bString.indexOf(',') < 0)
			{
				cString = bString;
				dString = bString;
			}
			else
			{

				final StringTokenizer bTok = new StringTokenizer(bString, ",", false);
				cString = bTok.nextToken();
				dString = bTok.nextToken();
			}
			if (".CLEAR".equals(cString) || "2".equals(cString))
			{
				if (vision == null)
				{
					continue;
				}

				Object aKey = vision.get(dString);
				if (aKey != null)
				{
					vision.remove(dString);
				}
			}
			else if (".SET".equals(cString) || "0".equals(cString))
			{
				if (vision == null)
				{
					vision = new HashMap();
				}
				vision.clear();

				// expecting value in form of Darkvision (60')
				StringTokenizer cTok = new StringTokenizer(dString, "(')", false);
				final String aKey = cTok.nextToken().trim(); //  e.g. Darkvision
				final String aVal = cTok.nextToken(); // e.g. 60
				vision.put(aKey, aVal);
			}
			else
			{
				if (vision == null)
				{
					vision = new HashMap();
				}

				// expecting value in form of Darkvision (60')
				StringTokenizer cTok = new StringTokenizer(dString, "(')", false);
				final String aKey = cTok.nextToken().trim(); //  e.g. Darkvision
				String aVal = "0";
				if (cTok.hasMoreTokens())
				{
					aVal = cTok.nextToken(); // e.g. 60
				}

				final Object bObj = vision.get(aKey);
				if (bObj == null)
				{
					vision.put(aKey, aVal);
				}
				else
				{

					final PlayerCharacter aPC = Globals.getCurrentPC();
					if (aPC != null)
					{

						final int b = aPC.getVariableValue(bObj.toString(), "").intValue();
						if (b < aPC.getVariableValue(aVal, "").intValue())
						{
							vision.put(aKey, aVal);
						}
					}
					else
					{
						vision.put(aKey, aVal);
					}
				}
			}
		}
	}

	final HashMap getVision()
	{
		return vision;
	}

	public final void setWeaponProfAutos(String aString)
	{

		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (weaponProfAutos == null)
		{
			weaponProfAutos = new ArrayList();
		}
		while (aTok.hasMoreTokens())
		{

			final String bString = aTok.nextToken();
			if (".CLEAR".equals(bString))
			{
				weaponProfAutos.clear();
			}
			else
			{
				weaponProfAutos.add(bString);
			}
		}
	}

	public final ArrayList getWeaponProfAutos()
	{
		return weaponProfAutos;
	}

	public final void addAllToAssociated(Collection collection)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList();
		}
		associatedList.addAll(collection);
	}

	final void addAutoArmorProfsToList(ArrayList armorList)
	{
		if (autoString == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(autoString, "|", false);
		boolean isArmor = false;
		String tok;
		while (aTok.hasMoreTokens())
		{
			tok = aTok.nextToken();
			if (tok.equals("ARMORPROF"))
			{
				isArmor = true;
				continue;
			}
			if (tok.equals("WEAPONPROF"))
			{
				isArmor = false;
				continue;
			}
			if (!isArmor)
			{
				continue;
			}
			if (tok.startsWith("TYPE="))
			{
				tok = tok.substring(5);
				for (Iterator e2 = Globals.getEquipmentOfType(Globals.getEquipmentList(), "ARMOR."+tok, "MAGIC.MASTERWORK").iterator(); e2.hasNext(); )
				{
					Equipment eq = (Equipment)e2.next();
					armorList.add(eq.toString());
				}
			}
			else
			{
				// add tok to list
				armorList.add(tok);
			}
		}
	}

	public final void addAssociated(String aString)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList();
		}
		associatedList.add(aString);
	}

	public final void addAssociated(FeatMultipleChoice aFeatChoices)
	{
		if (associatedList == null)
		{
			associatedList = new ArrayList();
		}
		associatedList.add(aFeatChoices);
	}

	public final void addAssociatedTo(Collection collection)
	{
		if (associatedList != null)
		{
			collection.addAll(associatedList);
		}
	}

	final ArrayList getAssociatedList()
	{
		return associatedList;
	}

	public final void addBonusList(String aString)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList();
		}
		//addBonus(aString);
		bonusList.add(aString);
	}

	public final void addCharacterSpell(CharacterSpell spell)
	{
		if (characterSpellList == null)
		{
			characterSpellList = new ArrayList();
		}
		characterSpellList.add(spell);
	}

	final void addMyType(String myType)
	{
		if (myTypeList == null)
		{
			myTypeList = new ArrayList();
		}
		myTypeList.add(myType);
	}

	final void addMyTypeTo(Collection collection)
	{
		if (myTypeList != null)
		{
			collection.addAll(myTypeList);
		}
	}

	public final void addPreReq(String preReq)
	{
		if ("PRE:.CLEAR".equals(preReq))
		{
			preReqList = null;
		}
		else
		{
			if (preReqList == null)
			{
				preReqList = new ArrayList();
			}
			preReqList.add(preReq);
		}
	}

	final void addPreReqTo(Collection collection)
	{
		if (preReqList != null)
		{
			collection.addAll(preReqList);
		}
	}

	public final void addSave(String aString)
	{
		if (saveList == null)
		{
			saveList = new ArrayList();
		}
		saveList.add(aString);
	}

	public final void addSelectedWeaponProfBonus(String entry)
	{
		if (selectedWeaponProfBonus == null)
		{
			selectedWeaponProfBonus = new ArrayList();
		}
		selectedWeaponProfBonus.add(entry);
	}

	final void addSelectedWeaponProfBonusTo(Collection collection)
	{
		if (selectedWeaponProfBonus != null)
		{
			collection.addAll(selectedWeaponProfBonus);
		}
	}

	protected ArrayList addSpecialAbilitiesToList(ArrayList aList)
	{
		if (specialAbilityList != null)
		{
			for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
			{
				aList.add(i.next());
			}
		}
		return aList;
	}

	public final void addSpecialAbilityToList(SpecialAbility sa)
	{
		if (specialAbilityList == null)
		{
			specialAbilityList = new ArrayList();
		}
		specialAbilityList.add(sa);
	}

	public final void addSpells(String line)
	{
		if (spellList == null)
		{
			spellList = new ArrayList();
		}

		String preTag = "";
		final int i = line.lastIndexOf('[');
		int j = line.lastIndexOf(']');
		if (j < i)
		{
			j = line.length();
		}
		if (i >= 0)
		{
			preTag = line.substring(i + 1, j);
			line = line.substring(0, i);
		}

		final StringTokenizer aTok = new StringTokenizer(line, "|", false);
		StringBuffer spellBuf = null;
		while (aTok.hasMoreTokens())
		{

			final String aName = aTok.nextToken();
			final String times = (aTok.hasMoreTokens()) ? aTok.nextToken() : "1";
			final String book = (aTok.hasMoreTokens()) ? aTok.nextToken() : "Innate";
			spellBuf = new StringBuffer(aName).append('|').append(times).append('|').append(book);
			if (preTag.length() > 0)
			{
				spellBuf.append('|').append(preTag);
			}
			spellList.add(spellBuf.toString());
		}
	}

	public final void addType(String aString)
	{

		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		while (aTok.hasMoreTokens())
		{

			final String aType = aTok.nextToken();
			if (!containsMyType(aType))
			{
				addMyType(aType);
			}
		}
	}

	public final void addUdamList(String addString)
	{
		if (udamList == null)
		{
			udamList = new ArrayList();
		}
		if (".CLEAR".equals(addString))
		{
			udamList.clear();
		}
		else
		{
			udamList.add(addString);
		}
	}

	public final void addUmult(String mult)
	{
		if (umultList == null)
		{
			umultList = new ArrayList();
		}
		if (".CLEAR".equals(mult))
		{
			umultList.clear();
		}
		else
		{
			umultList.add(mult);
		}
	}

	public final void addVariable(String entry)
	{
		if (variableList == null)
		{
			variableList = new ArrayList();
		}
		variableList.add(entry);
	}

	public final double bonusTo(String aType, String aName)
	{
		return bonusTo(aType, aName, Globals.getCurrentPC());
	}

	double bonusTo(String aType, String aName, Object obj)
	{
		return bonusTo(aType, aName, obj, bonusList);
	}

	final double bonusTo(String aType, String aName, Object obj, ArrayList aBonusList)
	{
		if (aBonusList == null || aBonusList.size() == 0)
		{
			return 0;
		}

		double retVal = 0;
		int iTimes = 1;

		if ("VAR".equals(aType) || "HP".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			//
			// SALIST will stick BONUS:VAR|... into bonus list so don't multiply
			//
			if (choiceString.startsWith("SALIST|") && choiceString.indexOf("|VAR|") >= 0)
			{
				iTimes = 1;
			}
		}
		else
		{
			;
		}

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final String aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();

		for (Iterator b = aBonusList.iterator(); b.hasNext();)
		{

			String bString = ((String) b.next());

			if (getAssociatedCount() != 0)
			{

				final int idx = bString.indexOf("%VAR");
				if (idx >= 0)
				{

					final String firstPart = bString.substring(0, idx);
					final String secondPart = bString.substring(idx + 4);
					for (int i = 1; i < getAssociatedCount(); ++i)
					{

						String xString = new StringBuffer().append(firstPart).append(getAssociated(i)).append(secondPart)
							.toString();
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes);
					}
					bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart)
						.toString();
				}
			}
			retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes);
		}
		return retVal;
	}

	public final void clearAssociated()
	{
		associatedList = null;
	}

	final void clearCharacterSpells()
	{
		if (characterSpellList != null && !characterSpellList.isEmpty())
		{
			characterSpellList.clear();
		}
	}

	//if a class implements the Cloneable interface then it should have a
	//"public" 'clone ()' method. It should be declared to throw
	//'CloneNotSupportedException', but subclasses do not need the "throws"
	//declaration unless their 'clone ()' method will throw the exception.
	//Thus subclasses can decide to not support 'Cloneable' by implementing
	//the 'clone ()' method to throw 'CloneNotSupportedException'.
	//If this rule were ignored and the parent did not have the "throws"
	//declaration, then subclasses that should not be cloned would be forced
	//to implement a trivial 'clone ()' to satisfy inheritance.
	//"final" classes implementing 'Cloneable' should not be declared to
	//throw 'CloneNotSupportedException" because their implementation of
	//'clone ()' should be a fully functional method that will not
	//throw the exception.

	public Object clone() throws CloneNotSupportedException
	{

		PObject retVal = null;
		retVal = (PObject) super.clone();
		retVal.setName(name);
		retVal.sourceFileIndex = sourceFileIndex;
		retVal.sourceIndex = sourceIndex;
		retVal.visible = visible;
		retVal.source = source;
		retVal.sourcePage = sourcePage;
		retVal.qualifyString = qualifyString;
		retVal.setKeyName(keyName);
		retVal.choiceString = choiceString;
		retVal.isSpecified = isSpecified;
		retVal.baseQuantity = baseQuantity;
		retVal.SR = SR;
		retVal.DR = DR;
		if (preReqList != null)
		{
			retVal.preReqList = (ArrayList) preReqList.clone();
		}
		if (associatedList != null)
		{
			retVal.associatedList = (ArrayList) associatedList.clone();
		}
		if (myTypeList != null)
		{
			retVal.myTypeList = (ArrayList) myTypeList.clone();
		}
		if (selectedWeaponProfBonus != null)
		{
			retVal.selectedWeaponProfBonus = (ArrayList) selectedWeaponProfBonus.clone();
		}
		if (characterSpellList != null)
		{
			retVal.characterSpellList = (ArrayList) characterSpellList.clone();
		}
		if (udamList != null)
		{
			retVal.udamList = (ArrayList) udamList.clone();
		}
		if (umultList != null)
		{
			retVal.umultList = (ArrayList) umultList.clone();
		}
		if (bonusList != null)
		{
			retVal.bonusList = (ArrayList) bonusList.clone();
		}
		if (variableList != null)
		{
			retVal.variableList = (ArrayList) variableList.clone();
		}
		if (cSkillList != null)
		{
			retVal.cSkillList = (ArrayList) cSkillList.clone();
		}
		if (ccSkillList != null)
		{
			retVal.ccSkillList = (ArrayList) ccSkillList.clone();
		}
		if (spellList != null)
		{
			retVal.spellList = (ArrayList) spellList.clone();
		}
		if (weaponProfAutos != null)
		{
			retVal.weaponProfAutos = (ArrayList) weaponProfAutos.clone();
		}
		if (specialAbilityList != null)
		{
			retVal.specialAbilityList = (ArrayList) specialAbilityList.clone();
		}

		// why isn't this cloned if != null?
		// because the saveList is based upon user selections (merton_monk@yahoo.com)
		retVal.saveList = null; // starts out empty

		//
		// Should these be cloned as well?
		//
		// no, because they won't change, they're loaded from list files (merton_monk@yahoo.com)
		retVal.bonusMap = bonusMap;
		retVal.languageAutos = languageAutos;
		retVal.vision = vision;
		retVal.kitString = kitString;
		retVal.regionString = regionString;
		retVal.autoString = autoString;
		if (selectedArmorProfs != null)
		{
			retVal.selectedArmorProfs = (ArrayList) selectedArmorProfs.clone();
		}

//		}
//		catch (CloneNotSupportedException exc)
//		{
//			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
//		}
		return retVal;
	}

	public int compareTo(Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.name.compareTo(((PObject) obj).name);
		}
		else
		{
			return 1;
		}
	}

	public final boolean containsAssociated(String associated)
	{
		if (associatedList == null)
		{
			return false;
		}
		return associatedList.contains(associated);
	}

	public final boolean containsSave(String save)
	{
		if (saveList == null)
		{
			return false;
		}
		return saveList.contains(save);
	}

	final boolean hasCCSkill(String aName)
	{
		if (ccSkillList == null || ccSkillList.isEmpty())
		{
			return false;
		}
		if (ccSkillList.contains(aName))
		{
			return true;
		}

		String aString = null;
		for (Iterator e = getCcSkillList().iterator(); e.hasNext();)
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

	final boolean hasCSkill(String aName)
	{
		if (cSkillList == null || cSkillList.isEmpty())
		{
			return false;
		}
		if (cSkillList.contains(aName))
		{
			return true;
		}
		if (cSkillList.contains("LIST"))
		{

			String aString = null;
			for (int e = 0; e < getAssociatedCount(); ++e)
			{
				aString = getAssociated(e);
				if (aName.startsWith(aString) || aString.startsWith(aName))
				{
					return true;
				}
			}
		}

		String aString = null;
		for (Iterator e = cSkillList.iterator(); e.hasNext();)
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

	// matchType==0 means equals
	// matchType==1 means starts with
	// matchType==2 means ends with
	public final boolean hasPreReqOf(int matchType, String matchString)
	{
		if (getPreReqCount() == 0)
		{
			return false;
		}
		for (int i = 0; i < getPreReqCount(); ++i)
		{

			String aString = getPreReq(i);
			if ((matchType == 0 && aString.equalsIgnoreCase(matchString)) || (matchType == 1 && aString.startsWith(matchString)) || (matchType == 2 && aString.endsWith(matchString)))
			{
				return true;
			}
		}
		return false;
	}

	String makeBonusString(String bonusString, String chooseString)
	{

		// assumption is that the chooseString is in the form class/type[space]level
		int i = chooseString.lastIndexOf(' ');
		String classString = "";
		String levelString = "";
		if (bonusString.startsWith("BONUS:"))
		{
			bonusString = bonusString.substring(6);
		}

		final boolean lockIt = bonusString.endsWith(".LOCK");
		if (lockIt)
		{
			bonusString = bonusString.substring(0, bonusString.lastIndexOf(".LOCK"));
		}

		if (i >= 0)
		{
			classString = chooseString.substring(0, i);
			if (i < chooseString.length())
			{
				levelString = chooseString.substring(i + 1);
			}
		}
		while (bonusString.lastIndexOf("TYPE=%") >= 0)
		{
			i = bonusString.lastIndexOf("TYPE=%");
			bonusString = bonusString.substring(0, i + 5) + classString + bonusString.substring(i + 6);
		}
		while (bonusString.lastIndexOf("CLASS=%") >= 0)
		{
			i = bonusString.lastIndexOf("CLASS=%");
			bonusString = bonusString.substring(0, i + 6) + classString + bonusString.substring(i + 7);
		}
		while (bonusString.lastIndexOf("LEVEL=%") >= 0)
		{
			i = bonusString.lastIndexOf("LEVEL=%");
			bonusString = bonusString.substring(0, i + 6) + levelString + bonusString.substring(i + 7);
		}
		if (lockIt)
		{
			i = bonusString.lastIndexOf('|');

			final Float val = Globals.getCurrentPC().getVariableValue(bonusString.substring(i + 1), "");
			bonusString = bonusString.substring(0, i) + "|" + val;
		}
		return bonusString;
	}

	int numberInList(String aType)
	{
		return 0;
	}

	//PreReqs for all objects
	public final boolean passesPreReqTests()
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}
		return passesPreReqTestsForList(preReqList);
	}

	final boolean passesPreReqTests(PObject p)
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}
		return passesPreReqTestsForList(p, preReqList);
	}

	//PreReqs for specified list
	final boolean passesPreReqTestsForList(ArrayList anArrayList)
	{

		//
		// Nothing to add, so allow it to be added always
		//
		if (anArrayList.size() == 0)
		{
			return true;
		}

		final PlayerCharacter aPC = Globals.getCurrentPC();

		if (aPC == null)
		{
			return false;
		}
		if (aPC.getClassList().size() == 0)
		{

			final PCClass aClass = Globals.getClassNamed(name);
			if (aClass != null && aClass.equals(this) && aClass.multiPreReqs())
			{
				return true;
			}
		}
		return passesPreReqTestsForList(aPC, null, anArrayList);

//		return passesPreReqTestsForList(null, anArrayList);
	}

	/**
	 * misc prereq tags:
	 * <ul>
	 *   <li> RESTRICT
	 * </ul>
	 *
	 * possible PRExxx tags:
	 * <ul>
	 *   <li> PREALIGN
	 *
	 *   <li> PRECLASS
	 *   <li> PRECLASSLEVELMAX
	 *   <li> PRELEVEL
	 *   <li> PRELEVELMAX
	 *   <li> PREHD
	 *   <li> PREHP
	 *
	 *   <li> PREDEITY
	 *   <li> PREDEITYALIGN
	 *   <li> PREDEITYDOMAIN
	 *   <li> PREDOMAIN
	 *
	 *   <li> PRECHECK
	 *   <li> PRECHECKBASE
	 *
	 *   <li> PREHANDSEQ
	 *   <li> PREHANDSGT
	 *   <li> PREHANDSGTEQ
	 *   <li> PREHANDSLT
	 *   <li> PREHANDSLTEQ
	 *   <li> PREHANDSNEQ
	 *
	 *   <li> PRELEGSEQ
	 *   <li> PRELEGSGT
	 *   <li> PRELEGSGTEQ
	 *   <li> PRELEGSLT
	 *   <li> PRELEGSLTEQ
	 *   <li> PRELEGSNEQ
	 *
	 *   <li> PRESIZEEQ
	 *   <li> PRESIZEGT
	 *   <li> PRESIZEGTEQ
	 *   <li> PRESIZELT
	 *   <li> PRESIZELTEQ
	 *   <li> PRESIZENEQ
	 *
	 *   <li> PRESKILL
	 *   <li> PRESKILLMULT
	 *   <li> PRESKILLTOT
	 *
	 *   <li> PRESPELL
	 *   <li> PRESPELLCAST
	 *   <li> PRESPELLTYPE
	 *   <li> PRESPELLSCHOOL
	 *   <li> PRESPELLSCHOOLSUB
	 *
	 *   <li> PREATT
	 *   <li> PREUATT
	 *
	 *   <li> PRECITY
	 *   <li> PREBIRTHPLACE
	 *   <li> PREREGION
	 *   <li> PREGENDER
	 *   <li> PREFEAT
	 *   <li> PREITEM
	 *   <li> PREEQUIP
	 *   <li> PREEQUIPPRIMARY
	 *   <li> PREEQUIPSECONDARY
	 *   <li> PREEQUIPBOTH
	 *   <li> PREEQUIPTWOWEAPON
	 *   <li> PREARMORTYPE
	 *   <li> PRELANG
	 *   <li> PREMOVE
	 *   <li> PRERACE
	 *   <li> PRETEMPLATE
	 *   <li> PRESTAT
	 *   <li> PRESA
	 *   <li> PRETYPE
	 *   <li> PREVAR
	 *   <li> PREWEAPONPROF
	 *
	 *   <li> PREFORCEPTS
	 *   <li> PREDSIDEPTS
	 *
	 *   <li> PREREPUTATION
	 *   <li> PREREPUTATIONLTEQ
	 * </ul>
	 */
	public boolean passesPreReqTestsForList(PlayerCharacter aPC, PObject aObj, ArrayList anArrayList)
	{

		boolean qValue = false; // Qualify overide testing.
		boolean qualifyValue = false; // Qualify overide testing.

		if ((anArrayList == null) || anArrayList.isEmpty())
		{
			return true;
		}

		boolean flag = false;
		boolean invertFlag = false; // Invert return value for !PRExxx tags

		ArrayList aFeatList = new ArrayList();
		if (aPC != null)
		{
			aFeatList = aPC.aggregateFeatList();
		}

		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PreReq:", name);
		}

		String aType = "";
		String aList = "";
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{
			flag = false;
			invertFlag = false;

			final String preString = (String) e.next();
			final StringTokenizer aaTok = new StringTokenizer(preString, ":", false);
			aType = aaTok.nextToken();
			if (aaTok.hasMoreTokens())
			{
				aList = aaTok.nextToken();
			}
			else
			{
				aList = "";
			}

			//check for inverse prereqs.  They start with a "!"
			if (aType.length() > 0 && aType.charAt(0) == '!')
			{
				invertFlag = true;
				aType = aType.substring(1);
			}

			// This adds 'Q' onto the PRExxx syntax. Which overrides QUALIFY.
			// Why do this? It allows some prereqs to be over-ridden but not all.
			// Which is needed for things like regional feats.
			if ("Q".equals(aList))
			{
				qValue = true;
				if (Globals.isDebugMode())
				{
					Globals.debugPrint("aList: ", aList);
				}
			}
			if ("Q".equals(aList) && aaTok.hasMoreTokens())
			{
				aList = aaTok.nextToken();
			}

			if (aPC != null)
			{
				if (aPC.checkQualifyList(this.getName()) && !qValue)
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("In Qualify list:" + this + " -> Qualify list:" + aPC.getQualifyList());
					}
					qualifyValue = true;
				}
				else
				{
					Globals.debugPrint("Not In Qualify list:", this.toString());
				}
			}

			// e.g. PRETYPE:Thrown|Melee,Ranged
			if ("PRETYPE".equals(aType))
			{

				final StringTokenizer aTok = new StringTokenizer(aList, ",|", true);
				int iLogicType = 0; // AND
				flag = true;
				if (aObj != null)
				{
					while (aTok.hasMoreTokens())
					{

						String aString = aTok.nextToken();
						if (",".equals(aString))
						{
							iLogicType = 0;
						}
						else if ("|".equals(aString))
						{
							iLogicType = 1;
						}
						else
						{

							boolean bIsType;
							boolean bInvert = false;
							if (aString.length() > 0 && aString.charAt(0) == '[' && aString.endsWith("]"))
							{
								aString = aString.substring(1, aString.length() - 1);
								bInvert = true;
							}

							if (aObj instanceof Equipment)
							{
								bIsType = ((Equipment) aObj).isPreType(aString);
							}
							else
							{
								bIsType = aObj.isType(aString);
							}
							if (bInvert)
							{
								bIsType = !bIsType;
							}

							if (iLogicType == 0)
							{
								flag &= bIsType;
							}
							else
							{
								flag |= bIsType;
							}
						}
					}
				}
			}

			if (aPC != null)
			{
				if ("PREFEAT".equals(aType))
				{
					StringTokenizer aTok = new StringTokenizer(aList, "|");
					aList = aTok.nextToken();
					flag = aPassesPreFeat(aTok, aList, aFeatList);
				}
				else if ("PRESKILL".equals(aType) || "PRESKILLMULT".equals(aType))
				{
					flag = passesPrereqSkill(aList, aPC);
				}
				else if ("PRESKILLTOT".equals(aType))
				{
					flag = passesPrereqSkillTot(aList, aPC);
				}
				else if ("PRECLASS".equals(aType))
				{
					flag = passesPrereqClass(aList, aPC, flag);
				}
				else if ("PRESUBCLASS".equals(aType))
				{
					final StringTokenizer dTok = new StringTokenizer(aList, ",", false);
					int num = Integer.parseInt(dTok.nextToken()); // number we must match
					while (dTok.hasMoreTokens())
					{
						final String thisClass = dTok.nextToken();
						for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
						{
							final PCClass aClass = (PCClass) it.next();
							final String subClassName = aClass.getSubClassName();
							if (subClassName.length() != 0)
							{
								if (thisClass.equalsIgnoreCase(subClassName))
								{
									--num;
									break;
								}
							}
						}
					}
					if (num <= 0)
					{
						flag = true;
					}
				}

				// If any class is over in level this should return false.
				else if ("PRECLASSLEVELMAX".equals(aType))
				{
					flag = passesPrereqClassLevelMax(aList, aPC);
				}
				else if ("PREDEFAULTMONSTER".equals(aType))
				{
					flag = (aPC.isMonsterDefault() == ("Y".equalsIgnoreCase(aList)));
				}
				else if ("PRELEVEL".equals(aType))
				{
					final int preLevel = Integer.parseInt(aList);
					flag = (aPC.getTotalLevels() >= preLevel);
				}
				else if ("PRELEVELMAX".equals(aType))
				{
					final int preLevelmax = Integer.parseInt(aList);
					flag = (aPC.getTotalLevels() <= preLevelmax);
				}
				else if ("PREHD".equals(aType))
				{
					flag = passesPrereqHD(aList, aPC);
				}
				else if ("PREHP".equals(aType))
				{
					final int preHitPoints = Integer.parseInt(aList);
					flag = (aPC.hitPoints() >= preHitPoints);
				}
				else if ("PREFORCEPTS".equals(aType))
				{
					final int preFPoints = Integer.parseInt(aList);
					final int myfhold = aPC.getRawFPoints();
					flag = (myfhold >= preFPoints);
				}
				else if ("PREDSIDEPTS".equals(aType))
				{
					final int preDPoints = Integer.parseInt(aList);
					final int mydhold = Integer.parseInt(aPC.getDPoints());
					flag = (mydhold >= preDPoints);
				}
				else if ("RESTRICT".equals(aType))
				{
					flag = passesRestrict(aList, flag, aPC);
				}
				else if ("PRERACE".equals(aType))
				{
					flag = passesPrereqRace(aList, aPC, flag);
				}
				else if ("PRETEMPLATE".equals(aType))
				{
					flag = passesPrereqTemplate(aPC, aList, flag);
				}
				else if ("PREATT".equals(aType))
				{
					final int anInt = Integer.parseInt(aList);
					int att = (int) aPC.getBonus(0, true);
					att -= (int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
					flag = att >= anInt;
				}
				else if ("PREREPUTATION".equals(aType))
				{
					final int anInt = Integer.parseInt(aList);
					String rep = String.valueOf(aPC.reputation());
					int repf = Integer.parseInt(rep);
					flag = repf >= anInt;
				}
				else if ("PREREPUTATIONLTEQ".equals(aType))
				{
					final int anInt = Integer.parseInt(aList);
					final String rep = String.valueOf(aPC.reputation());
					final int repf = Integer.parseInt(rep);
					flag = repf <= anInt;
				}
				else if ("PREUATT".equals(aType))
				{
					flag = passesPrereqPreUAtt(aList, aPC);
				}
				else if ("PRESTAT".equals(aType))
				{
					flag = passesPrereqStat(aList, aPC, flag);
				}
				else if ("PRESPELLTYPE".equals(aType))
				{
					flag = passesPrereqSpellType(aList, aPC);
				}
				else if ("PRESPELLSCHOOL".equals(aType))
				{
					flag = passesPreSpellSchool(aList, aPC);
				}
				else if ("PRESPELLSCHOOLSUB".equals(aType))
				{
					flag = passesPreSpellSchoolSub(aList, aPC);
				}
				else if ("PRESPELL".equals(aType))
				{
					flag = passesPreSpell(aList, aPC);
				}
				else if ("PRESPELLBOOK".equals(aType))
				{
					flag = false;
					for (Iterator ee = aPC.getClassList().iterator(); !flag && ee.hasNext();)
					{
						flag |= aList.startsWith(((PCClass) ee.next()).getSpellBookUsed() ? "Y" : "N");
					}
				}
				else if ("PRESPELLCAST".equals(aType))
				{
					flag = passesPreSpellCast(aList, aPC, flag);
				}
				else if ("PRESA".equals(aType))
				{
					flag = passesPreSA(aList, aPC);
				}
				else if ("PRELANG".equals(aType))
				{
					flag = passesPreLang(aList, aPC);
				}
				else if ("PREWEAPONPROF".equals(aType))
				{
					flag = passesPreWeaponProf(aList, aPC);
				}
				else if ("PREITEM".equals(aType))
				{
					flag = passesPreItem(aList, aPC);
				}
				else if ("PREEQUIP".equals(aType))
				{
					flag = passesPreEquip(aList, aPC);
				}
				else if ("PREEQUIPPRIMARY".equals(aType))
				{
					flag = passesPreEquipPrimary(aList, aPC);
				}
				else if ("PREEQUIPSECONDARY".equals(aType))
				{
					flag = passesPreEquipSecondary(aList, aPC);
				}
				else if ("PREEQUIPBOTH".equals(aType))
				{
					flag = passesPreEquipBothHands(aList, aPC);
				}
				else if ("PREEQUIPTWOWEAPON".equals(aType))
				{
					flag = passesPreEquipTwoWeapon(aList, aPC);
				}
				else if ("PREARMORTYPE".equals(aType))
				{
					flag = passesPreArmorType(aList, aPC);
				}
				else if (aType.startsWith("PREVAR"))
				{
					flag = passesPreVar(aList, aType, aPC);
				}
				else if ("PREGENDER".equals(aType))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("PREGENDER");
					}
					flag = aPC.getGender().startsWith(aList);
				}
				else if ("PRECITY".equals(aType))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("PRECITY");
					}
					flag = aPC.getResidence().equalsIgnoreCase(aList);
				}
				else if ("PREBIRTHPLACE".equals(aType))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("PREBIRTHPLACE");
					}
					flag = aPC.getBirthplace().equalsIgnoreCase(aList);
				}
				else if ("PREREGION".equals(aType))
				{
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("PREREGION");
					}
					//if the PCs full region starts with aList, then we don't care about subregion
					//but if the lst specifies a subregion then this'll attempt to match it, too
					flag = aPC.getFullRegion().startsWith(aList);
				}
				else if ("PREDEITY".equals(aType))
				{
					flag = passesPreDeity(aList, aPC);
				}
				else if ("PREDEITYDOMAIN".equals(aType)) //for Mynex
				{
					flag = passesPreDeityDomain(aList, aPC);
				}
				else if ("PREDEITYALIGN".equals(aType))
				{
					flag = passesPreDeityAlign(aList, aPC);
				}
				else if ("PREALIGN".equals(aType))
				{
					flag = passesPreAlign(aList, aPC);
				}
				else if (aType.startsWith("PRECHECK"))
				{
					// PRECHECK:x,check=val,check=val same with PRECHECKBASE:
					boolean isBase = aType.endsWith("BASE");
					final StringTokenizer dTok = new StringTokenizer(aList, ",=", false);
					int num = Integer.parseInt(dTok.nextToken()); // number we must match
					while (dTok.hasMoreTokens())
					{
						final String checkName = dTok.nextToken();
						final int val = aPC.getVariableValue(dTok.nextToken(), "").intValue();
						final int ci = Globals.getIndexOfCheck(checkName);
						if (ci < 0)
						{
							continue;
						}
						if ((int) aPC.getBonus(ci + 1, !isBase) >= val)
						{
							--num;
						}
					}
					flag = (num <= 0);
				}
				else if ("PREDOMAIN".equals(aType))
				{
					flag = passesPreDomain(aList, aPC);
				}
				else if ("PREMOVE".equals(aType))
				{
					flag = passesPreMove(aPC, flag, aList);
				}
				//
				// PREHANDSEQ, PREHANDSGT, PREHANDSGTEQ, PREHANDSLT, PREHANDSLTEQ, PREHANDSNEQ
				//
				else if (aType.startsWith("PREHANDS"))
				{
					flag = doComparison(aType.substring(8), aPC.getRace().getHands(), Integer.parseInt(aList), aPC, "HANDS");
				}
				//
				// PRELEGSEQ, PRELEGSGT, PRELEGSGTEQ, PRELEGSLT, PRELEGSLTEQ, PRELEGSNEQ
				//
				else if (aType.startsWith("PRELEGS"))
				{
					flag = doComparison(aType.substring(7), aPC.getRace().getLegs(), Integer.parseInt(aList), aPC, "LEGS");
				}
				//
				// PRESIZEEQ, PRESIZEGT, PRESIZEGTEQ, PRESIZELT, PRESIZELTEQ, PRESIZENEQ
				//
				else if (aType.startsWith("PRESIZE"))
				{
					flag = doComparison(aType.substring(7), aPC.sizeInt(), aPC.sizeIntForSize(aList));
				}
				else if ("PRETEXT".equals(aType))
				{
					flag = true;
				}
				else if (!"PRETYPE".equals(aType))
				{
					Globals.debugPrint("Prereq failed, unknown tag: ", aType);
					flag = false;
				}
			}

			if (qValue)
			{
				qualifyValue = false;
			}
			qValue = false;

			if (invertFlag)
			{
				flag = !flag;
			}
			if (!flag && !qualifyValue)
			{
				return flag;
			}
		}
		if (qualifyValue)
		{
			flag = true;
		}
		return flag;
	}

	public final String preReqHTMLStrings()
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}
		return preReqHTMLStringsForList(preReqList);
	}

	public String preReqHTMLStrings(boolean includeHeader)
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}
		return preReqHTMLStringsForList(null, preReqList, includeHeader);
	}

	public final String preReqHTMLStrings(PObject p)
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}
		return preReqHTMLStringsForList(p, preReqList);
	}

	final String preReqHTMLStringsForList(PObject aObj, ArrayList anArrayList, boolean includeHeader)
	{
		return preReqHTMLStringsForList(null, aObj, anArrayList, includeHeader);
	}

	public final String preReqHTMLStringsForList(PlayerCharacter aPC, PObject aObj, ArrayList anArrayList, boolean includeHeader)
	{
		if ((anArrayList == null) || anArrayList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(anArrayList.size() * 20);

		final ArrayList newList = new ArrayList();
		int iter = 0;
		final int fontColor = SettingsHandler.getPrereqFailColor();
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{

			//String aString = (String)e.next();
			newList.clear();
			newList.add(e.next());
			if (iter++ > 0)
			{
				pString.append(' ');
			}

			String bString = preReqStringsForList(newList);

			boolean flag;
			if (aPC != null)
			{
				flag = passesPreReqTestsForList(aPC, aObj, newList);
			}
			else if (aObj == null)
			{
				flag = passesPreReqTestsForList(newList);
			}
			else
			{
				flag = passesPreReqTestsForList(aObj, newList);
			}

			if (!flag)
			{
				if (fontColor != 0)
				{
					pString.append("<font color=\"#").append(Integer.toHexString(fontColor)).append("\">");
				}
				pString.append("<i>");
			}

			pString.append(bString);

			if (!flag)
			{
				pString.append("</i>");
				if (fontColor != 0)
				{
					pString.append("</font>");
				}
			}
		}

		if (pString.toString().indexOf('<') >= 0)
		{
			// seems that ALIGN and STAT have problems in HTML display, so wrapping in <font> tag.
			pString.insert(0, "<font>");
			pString.append("</font>");
			if (includeHeader)
			{
				if (pString.toString().indexOf('<') >= 0)
				{
					pString.insert(0, "<html>");
					pString.append("</html>");
				}
			}
		}
		return pString.toString();
	}

	///Creates the requirement string for printing.
	public final String preReqStrings()
	{
		if (getPreReqCount() == 0)
		{
			return "";
		}
		return preReqStringsForList(preReqList);
	}

	final Object removeAssociated(int i)
	{
		if (associatedList == null)
		{
			throw new IndexOutOfBoundsException("size is 0, i=" + i);
		}
		return associatedList.remove(i);
	}

	public final boolean removeAssociated(String associated)
	{
		if (associatedList == null)
		{
			return false;
		}

		final boolean ret = associatedList.remove(associated);
		if (associatedList.size() == 0)
		{
			associatedList = null;
		}
		return ret;
	}

	final boolean removeCharacterSpell(CharacterSpell spell)
	{
		if (characterSpellList == null)
		{
			return false;
		}
		return characterSpellList.remove(spell);
	}

	public final void removeSave(String bonusString)
	{
		if (saveList != null)
		{

			final int index = saveList.indexOf(bonusString);
			if (index >= 0)
			{
				saveList.remove(index);
			}
			else
			{
				Globals.errorPrint("removeBonus: Could not find bonus: " + bonusString + " in saveList.");
			}
		}
		else
		{
			Globals.errorPrint("removeBonus: Could not find bonus: " + bonusString + " in saveList.");
		}
	}

	public final void removeType(String aString)
	{

		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
		while (aTok.hasMoreTokens())
		{

			final String aType = aTok.nextToken();
			removeMyType(aType);
		}
	}

	final void sortAssociated()
	{
		if (associatedList != null)
		{
			Collections.sort(associatedList);
		}
	}

	public final void sortCharacterSpellList()
	{
		if (characterSpellList != null)
		{
			Collections.sort(characterSpellList);
		}
	}

	public String toString()
	{
		return name;
	}

	public final String piString()
	{
		if (nameIsPI)
			return "<html><b><i>" + toString() + "</i></b></html>";
		else
			return toString();
	}

	// in some cases, we need a PI-formatted string to place within a pre-existing <html> tag
	public final String piSubString()
	{
		if (nameIsPI)
			return "<b><i>" + toString() + "</i></b>";
		else
			return toString();
	}

	private void getChoices(String aChoice, ArrayList selectedBonusList, PObject theObj, ArrayList availableList, ArrayList selectedList, boolean process)
	{
		if (!choiceString.startsWith("FEAT|") && !choiceString.startsWith("ARMORPROF") && !choiceString.startsWith("SPELLLEVEL") && !aChoice.startsWith("SPELLLEVEL") && !aChoice.startsWith("WEAPONPROF"))
		{
			return;
		}

		if (aChoice.length() == 0)
		{
			aChoice = choiceString;
		}

		StringTokenizer aTok = new StringTokenizer(aChoice, "|", false);
		aTok.nextToken(); // should be ARMORPROF, SPELLLEVEL or WEAPONPROF

		final PlayerCharacter aPC = Globals.getCurrentPC();
		String tempString = aTok.nextToken();

		int pool = aPC.getVariableValue(tempString, "").intValue();

		boolean dupsAllowed = true;
		String title = "";
		final ArrayList otherArrayList = new ArrayList();
		final ArrayList cArrayList = new ArrayList();

		if (aChoice.startsWith("FEAT"))
		{
			if (associatedList != null)
				selectedList.addAll(associatedList);
			while(aTok.hasMoreTokens())
			{
				tempString = aTok.nextToken();
				Feat f = Globals.getFeatNamed(tempString);
				if (f != null && f.passesPreReqTests())
				{
					availableList.add(tempString);
					if (aPC.hasFeat(tempString) && !selectedList.contains(tempString))
					{
						selectedList.add(tempString);
					}
				}
			}
		}
		else if (aChoice.startsWith("SPELLLEVEL"))
		{
			getSpellTypeChoices(aChoice, availableList, cArrayList); // get appropriate choices for chooser
		}
		else if (aChoice.startsWith("WEAPONPROF")) // determine appropriate choices for chooser
		{
			dupsAllowed = false;
			title = "Weapon Choice(s)";
			theObj.addSelectedWeaponProfBonusTo(selectedList);
			while (aTok.hasMoreTokens())
			{

				String aString = aTok.nextToken();
				boolean adding = false;
				String cString = aString;
				if (aString.lastIndexOf('[') >= 0)
				{

					final StringTokenizer bTok = new StringTokenizer(aString, "[]", false);
					final String bString = bTok.nextToken();
					adding = true;
					while (bTok.hasMoreTokens())
					{
						otherArrayList.add(bString + "|" + bTok.nextToken());
					}
					aString = bString;
					if (Globals.isDebugMode())
					{
						Globals.debugPrint("bString=", bString);
					}
				}
				if ("DEITYWEAPON".equals(aString))
				{
					if (aPC.getDeity() != null)
					{

						StringBuffer weaponList = new StringBuffer(aPC.getDeity().getFavoredWeapon());
						if ("ALL".equalsIgnoreCase(weaponList.toString()) || "ANY".equalsIgnoreCase(weaponList.toString()))
						{
							weaponList.setLength(0);
							for (Iterator wi = Globals.getWeaponProfList().iterator(); wi.hasNext();)
							{
								if (weaponList.length() > 0)
								{
									weaponList.append('|');
								}
								weaponList.append(wi.next().toString());
							}
						}

						StringTokenizer bTok = new StringTokenizer(weaponList.toString(), "|", false);
						while (bTok.hasMoreTokens())
						{

							String bString = bTok.nextToken();
							availableList.add(bString);
							if (adding)
							{

								final StringTokenizer cTok = new StringTokenizer(cString, "[]", false);
								cTok.nextToken(); //Read and throw away a token
								while (cTok.hasMoreTokens())
								{
									otherArrayList.add(bString + "|" + cTok.nextToken());
								}
							}
						}
					}
				}
				else if (aString.startsWith("TYPE="))
				{

					final StringTokenizer bTok = new StringTokenizer(aString.substring(5), ".", false);
					ArrayList typeList = new ArrayList();
					int iSize = -1;
					while (bTok.hasMoreTokens())
					{

						final String bString = bTok.nextToken();
						if (bString.startsWith("SIZE="))
						{
							iSize = Globals.sizeInt(bString.substring(5));
						}
						else
						{
							typeList.add(bString);
						}
					}

					for (Iterator ei = Globals.getEquipmentList().iterator(); ei.hasNext();)
					{

						final Equipment aEq = (Equipment) ei.next();
						if (!aEq.isWeapon())
						{
							continue;
						}

						boolean bOkay = true;
						for (Iterator ti = typeList.iterator(); ti.hasNext();)
						{
							if (!aEq.isType((String) ti.next()))
							{
								bOkay = false;
								break;
							}
							if (iSize >= 0)
							{
								bOkay &= Globals.sizeInt(aEq.getSize()) == iSize;
							}
							if (bOkay)
							{

								WeaponProf wp = Globals.getWeaponProfNamed(aEq.profName());
								if ((wp != null) && !availableList.contains(wp.getName()))
								{

									final String bString = wp.getName();
									availableList.add(bString);

									final StringTokenizer cTok = new StringTokenizer(cString, "[]", false);
									if (cTok.hasMoreTokens())
									{
										cTok.nextToken(); //Read and throw away a token
										while (cTok.hasMoreTokens())
										{
											otherArrayList.add(bString + "|" + cTok.nextToken());
										}
									}
								}
							}
						}
					}
				}
				else
				{
					availableList.add(aString);
				}
			}
		}
		else if (aChoice.startsWith("ARMORPROF"))
		{
			if (theObj.getSelectedArmorProfs() != null)
				selectedList.addAll(theObj.getSelectedArmorProfs());
			while(aTok.hasMoreTokens())
			{
				tempString = aTok.nextToken();
				if (tempString.startsWith("TYPE="))
				{
					tempString = tempString.substring(5);
					for (Iterator i = Globals.getEquipmentList().iterator(); i.hasNext(); )
					{
						Equipment eq = (Equipment)i.next();
						if (eq.isArmor() && eq.isType(tempString) && !availableList.contains(eq.profName()))
						{
							availableList.add(eq.profName());
						}
					}
				}
				else
				{
					Equipment eq = Globals.getEquipmentNamed(tempString);
					if (eq != null && eq.isArmor() && !availableList.contains(eq.profName()))
						availableList.add(eq.profName());
				}
			}
		}

		if (!process)
		{
			return;
		}

		final ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setAllowsDups(dupsAllowed);
		if (title.length() != 0)
		{
			c.setTitle(title);
		}

		Globals.sortChooserLists(availableList, selectedList);
		c.setAvailableList(availableList);
		c.setSelectedList(selectedList);
		c.setUniqueList(cArrayList);

		pool -= c.getSelectedList().size();

		c.setPool(Math.max(0, pool));
		c.setPoolFlag(false); // Allow cancel as clicking the x will cancel anyways

		c.show();


		// Currently:
		// WEAPONPROF|x|choice1[WEAPONPROF][FEAT=xxx]|choice2[WEAPONPROF][FEAT=xxx]|...|choicen[WEAPONPROF][FEAT=xxx][FEAT=yyy]...
		//
		// wouldn't this be better?
		// WEAPONPROF|x|choice1|choice2|...|choicen[WEAPONPROF][FEAT=xxx][FEAT=yyy]...
		if (aChoice.startsWith("WEAPONPROF"))
		{
			theObj.clearSelectedWeaponProfBonus();
			aPC.setAutomaticFeatsStable(false);
			for (int index = 0; index < c.getSelectedList().size(); ++index)
			{
				if (otherArrayList.isEmpty())
				{
					continue;
				}

				String aString = (String) c.getSelectedList().get(index);
				for (Iterator e = otherArrayList.iterator(); e.hasNext();)
				{

					String bString = (String) e.next();
					aTok = new StringTokenizer(bString, "|", false);
					if (aTok.nextToken().equals(aString))
					{

						String cString = aTok.nextToken();
						if (cString.startsWith("WEAPONPROF"))
						{
							theObj.addSelectedWeaponProfBonus(aString);
						}

						//
						// TODO: This needs to be added to the automatic feat list
						//
						else if (cString.startsWith("FEAT="))
						{
							if (theObj instanceof Domain)
							{
								theObj.clearAssociated();

								Feat aFeat = (Feat) Globals.getFeatKeyed(cString.substring(5)).clone();
								if (aFeat != null)
								{
									theObj.addAssociated("FEAT?" + aFeat.getName() + "(" + aString + ")");
								}
							}
							else
							{

								Feat aFeat = aPC.getFeatNamed(cString.substring(5));
								if (aFeat == null)
								{
									aFeat = (Feat) Globals.getFeatKeyed(cString.substring(5)).clone();
									if (aFeat != null)
									{
										aPC.addFeat(aFeat);
									}
								}
								if ((aFeat != null) && !aFeat.containsAssociated(aString))
								{
									aFeat.addAssociated(aString);
								}
							}
						}
					}
				}
			}
		}
		else if (aChoice.startsWith("ARMORPROF"))
		{
			theObj.addSelectedArmorProfs(c.getSelectedList());
		}
		else if (aChoice.startsWith("FEAT"))
		{
			for (Iterator i = c.getSelectedList().iterator(); i.hasNext(); )
			{
				tempString = (String) i.next();
				aPC.modFeat(tempString, true, false);
				theObj.addAssociated(tempString);
			}
		}
		else
		{
			for (int index = 0; index < c.getSelectedList().size(); ++index)
			{

				final String aString = (String) c.getSelectedList().get(index);
				if (selectedBonusList.isEmpty())
				{
					continue;
				}
				for (Iterator e = selectedBonusList.iterator(); e.hasNext();)
				{

					final String bString = (String) e.next();
					applyBonus(bString, aString);
				}
			}
		}
		return;
	}

	private static boolean aPassesPreFeat(StringTokenizer aTok, String aList, ArrayList aFeatList)
	{

		int number;
		final boolean flag;
		boolean countMults = false;
		if (aTok.hasMoreTokens())
		{
			countMults = "CHECKMULT".equals(aTok.nextToken());
		}
		aTok = new StringTokenizer(aList, ",");

		// the number of feats which must match
		try
		{
			number = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exceptn)
		{
			Globals.errorPrint("Exception in PREFEAT:" + aList + Constants.s_LINE_SEP + "Assuming 1 required", exceptn);
			number = 1;
			aTok = new StringTokenizer(aList, ",");
		}
		while (aTok.hasMoreTokens() && number > 0)
		{

			String aString = aTok.nextToken();
			StringTokenizer bTok = new StringTokenizer(aString, "(", false);
			String pString = bTok.nextToken();
			int i = -1;
			if (pString.length() != aString.length())
			{
				i = pString.length(); // begin of subchoices
			}

			String featName = null;
			String subName = null;
			int j = -1;
			boolean isType = aString.startsWith("TYPE=");
			if (i >= 0)
			{
				featName = aString.substring(0, i).trim();
				subName = aString.substring(i + 1, aString.length() - 1);
				j = subName.lastIndexOf('%');
				if (j >= 0)
				{
					subName = subName.substring(0, j);
				}
			}
			else
			{
				featName = aString;
			}

			boolean foundIt = false;
			if (!aFeatList.isEmpty())
			{
				for (Iterator e1 = aFeatList.iterator(); e1.hasNext();)
				{
					if (foundIt && !isType || (number <= 0))
					{
						break;
					}

					Feat aFeat = (Feat) e1.next();
					if ((!isType && (aFeat.getName().equalsIgnoreCase(featName) || aFeat.getName().equalsIgnoreCase(aString))) || (isType && aFeat.isType(featName.substring(5))))
					{
						if (subName != null && (aFeat.getName().equalsIgnoreCase(aString) || aFeat.containsAssociated(subName)) || subName == null)
						{
							--number;
							if (aFeat.isMultiples() && countMults)
							{
								number -= (aFeat.getAssociatedCount() - 1);
							}
							foundIt = true;
						}
						else if ((subName != null) && (j >= 0)) // search for match
						{
							for (int k = 0; k < aFeat.getAssociatedCount(); ++k)
							{

								String fString = aFeat.getAssociated(k);
								if (fString.startsWith(subName.substring(0, j)))
								{
									--number;
									foundIt = true;
									if (!countMults)
									{
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		flag = (number <= 0);
		return flag;
	}

	final void applyBonus(String bonusString, String chooseString)
	{
		bonusString = makeBonusString(bonusString, chooseString);
		addBonusList(bonusString);
		addSave("BONUS|" + bonusString);
	}

	private double calcBonus(String bString, String aType, String aName, String aTypePlusName, Object obj, int iTimes)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);
		if (aTok.countTokens() < 3)
		{
			Globals.errorPrint("Badly formed BONUS:" + bString);
			return 0;
		}

		String aString = aTok.nextToken();

		if ((!aString.equalsIgnoreCase(aType) && !aString.endsWith("%LIST")) || (aString.endsWith("%LIST") && (numberInList(aType) == 0)))
		{
			return 0;
		}

		final String aList = aTok.nextToken();
		//if (!aList.equals("LIST") && aList.indexOf(aName)==-1)
		if (!"LIST".equals(aList) && aList.toUpperCase().indexOf(aName.toUpperCase()) < 0)
		{
			return 0;
		}
		aString = aTok.nextToken();

		double aBonus = 0;
		if (obj instanceof PlayerCharacter)
		{
			aBonus = ((PlayerCharacter) obj).getVariableValue(aString, "").doubleValue();
		}
		else if (obj instanceof Equipment)
		{
			aBonus = ((Equipment) obj).getVariableValue(aString, "", "").doubleValue();
		}
		else
		{
			try
			{
				aBonus = Float.parseFloat(aString);
			}
			catch (NumberFormatException e)
			{

				//Should this be ignored?
				Globals.errorPrint("NumberFormatException in BONUS:" + bString, e);
			}
		}

		final ArrayList preReqList = new ArrayList();
		String possibleBonusTypeString = null;
		while (aTok.hasMoreTokens())
		{

			final String pString = aTok.nextToken();
			if (pString.startsWith("PRE") || pString.startsWith("!PRE"))
			{
				preReqList.add(pString);
			}
			else if (pString.startsWith("TYPE=") || pString.startsWith("STAT="))
			{
				possibleBonusTypeString = pString.substring(5);
			}
		}

		// must meet criteria before adding any bonuses
		if (!passesPreReqTestsForList(preReqList))
		{
			return 0;
		}

		double bonus = 0;
		if ("LIST".equalsIgnoreCase(aList))
		{
			final int iCount = numberInList(aName);
			if (iCount != 0)
			{
				bonus += aBonus * iCount;
			}
		}

		final StringTokenizer bTok = new StringTokenizer(aList, ",", false);
		if ("LIST".equalsIgnoreCase(aList))
		{
			bTok.nextToken();
		}

		String bonusTypeString = null;
		while (bTok.hasMoreTokens())
		{
			if (bTok.nextToken().equalsIgnoreCase(aName))
			{
				bonus += aBonus;
				bonusTypeString = possibleBonusTypeString;
			}
		}

		if (obj instanceof PlayerCharacter)
		{
			((PlayerCharacter) obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}
		else if (obj instanceof Equipment)
		{
			((Equipment) obj).setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}
		else
		{
			setBonusStackFor(bonus * iTimes, aTypePlusName + bonusTypeString);
		}

		return bonus * iTimes;
	}

	private void clearMyType()
	{
		myTypeList = null;
	}

	private void clearSelectedWeaponProfBonus()
	{
		selectedWeaponProfBonus = null;
	}

	private boolean containsMyType(String myType)
	{
		if (myTypeList == null)
		{
			return false;
		}
		return myTypeList.contains(myType);
	}

	private static boolean passesPreAlign(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREALIGN");
		}

		String bList = aList;
		flag = false;

		final int alignment = aPC.getAlignment();
		final String alString = String.valueOf(alignment);
		for (; ;)
		{

			// PREALIGN:[VARDEFINED=SuneLG=0],3,6,7
			int idxStart = bList.indexOf('[');
			if (idxStart < 0)
			{
				break;
			}

			int idxEnd = bList.indexOf(']', idxStart);
			if (idxEnd < 0)
			{
				break;
			}

			final String subPre = bList.substring(idxStart + 1, idxEnd);
			final StringTokenizer pTok = new StringTokenizer(subPre, "=", false);
			if (pTok.countTokens() != 3)
			{
				break;
			}

			final String cond = pTok.nextToken();
			final String vName = pTok.nextToken();
			final String condAlignment = pTok.nextToken();
			boolean hasCond = false;
			if ("VARDEFINED".equals(cond))
			{
				if (aPC.hasVariable(vName))
				{
					hasCond = true;
				}
			}

			if (hasCond)
			{
				bList = bList.substring(0, idxStart) + condAlignment + bList.substring(idxEnd + 1);
			}
			else
			{
				bList = bList.substring(0, idxStart) + bList.substring(idxEnd + 1);
			}
			if (bList.length() > 0 && bList.charAt(0) == ',')
			{
				bList = bList.substring(1);
			}
		}
		flag = (bList.lastIndexOf(alString) >= 0);
		if (!flag && (bList.lastIndexOf("10") >= 0) && (aPC.getDeity() != null))
		{
			flag = Globals.getShortAlignmentAtIndex(aPC.getAlignment()).equals(aPC.getDeity().getAlignment());
//			flag = aPC.getDeity().allowsAlignment(alignment);
		}
		return flag;
	}

	private static boolean passesPreDeity(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREDEITY");
		}

		//
		// PREDEITY:[Y|N|deity1,deity2,...,dietyn]
		//
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			flag = (("Y".equals(aString) && (aPC.getDeity() != null)) ||
				("N".equals(aString) && (aPC.getDeity() == null)) ||
				((aPC.getDeity() != null) && aPC.getDeity().getName().equalsIgnoreCase(aString)));
			if (flag)
			{
				break;
			}
		}
		return flag;
	}

	private static boolean passesPreDeityAlign(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREDEITYALIGN");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		while (aTok.hasMoreTokens())
		{

			final String aString = Globals.getShortAlignmentAtIndex(Integer.parseInt(aTok.nextToken()));
			if (aPC.getDeity() != null)
			{
				flag = (aPC.getDeity().getAlignment().equals(aString));
				if (flag)
				{
					break;
				}
			}
		}
		return flag;
	}

	private static boolean passesPreDeityDomain(String aList, PlayerCharacter aPC)
	{

		int number;
		final boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREDEITYDOMAIN");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens() && number > 0)
		{

			final String bString = aTok.nextToken();
			if (aPC.getDeity() != null)
			{
				if (aPC.getDeity().hasDomainNamed(bString.trim()))
				{
					--number;
				}
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreDomain(String aList, PlayerCharacter aPC)
	{

		int number;
		final boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREDOMAIN");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens() && number > 0)
		{

			final String bString = aTok.nextToken();
			if (aPC.getCharacterDomainNamed(bString) != null)
			{
				--number;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreItem(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREITEM");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			ArrayList typeList = null;
			if (aString.startsWith("TYPE="))
			{
				typeList = Utility.split(aString.substring(5), '.');
			}
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{
				final Equipment eq = (Equipment) e1.next();
				if (typeList != null)
				{
					boolean bMatches = true;
					for (int i = 0, x = typeList.size(); i < x; ++i)
					{
						if (!eq.isType((String) typeList.get(i)))
						{
							bMatches = false;
							break;
						}
					}
					if (bMatches)
					{
						--number;
						break;
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if (eq.getName().startsWith(aString.substring(0, aString.indexOf('%'))))
						{
							--number;
							break;
						}
					}
					else if (eq.getName().equals(aString)) //just a straight String compare
					{
						--number;
						break;
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreArmorType(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREARMORTYPE");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{
				if (Globals.isDebugMode())
				{
					Globals.debugPrint("PREARMORTYPE A1");
				}
				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE="))
				{
					if ((eq.getType().indexOf("ARMOR." + aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						--number;
						break;
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							--number;
							break;
						}
					}
					else if (aString.indexOf("LIST") >= 0)
					{
						if (Globals.isDebugMode())
						{
							Globals.debugPrint("PREARMORTYPE A2");
						}

						for (Iterator e2 = aPC.getArmorProfList().iterator(); e2.hasNext();)
						{
							String aprof = (String) e2.next();
							aprof = "ARMOR." + aprof;
							if (Globals.isDebugMode())
							{
								Globals.debugPrint("PREARMORTYPE ", aprof);
							}

							if ((eq.getType().indexOf(aprof) >= 0) && eq.isEquipped())
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						--number;
						break;
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreEquip(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREEQUIP");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{

				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE="))
				{
					if ((eq.getType().indexOf(aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						--number;
						break;
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							--number;
							break;
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						--number;
						break;
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreEquipPrimary(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREEQUIPPRIMARY");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{

				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE="))
				{
					if ((eq.getType().indexOf(aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						if (eq.whatHand() == Equipment.PRIMARY_HAND)
						{
							--number;
							break;
						}
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							if (eq.whatHand() == Equipment.PRIMARY_HAND)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.whatHand() == Equipment.PRIMARY_HAND)
						{
							--number;
							break;
						}
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreEquipSecondary(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREEQUIPSECONDARY");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{

				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE="))
				{
					if ((eq.getType().indexOf(aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						if (eq.whatHand() == Equipment.SECONDARY_HAND)
						{
							--number;
							break;
						}
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							if (eq.whatHand() == Equipment.SECONDARY_HAND)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.whatHand() == Equipment.SECONDARY_HAND)
						{
							--number;
							break;
						}
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreEquipBothHands(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREEQUIPBOTH");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{

				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE="))
				{
					if ((eq.getType().indexOf(aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						if (eq.whatHand() == Equipment.BOTH_HANDS)
						{
							--number;
							break;
						}
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							if (eq.whatHand() == Equipment.BOTH_HANDS)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.whatHand() == Equipment.BOTH_HANDS)
						{
							--number;
							break;
						}
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreEquipTwoWeapon(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int number;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREEQUIPTWOWEAPON");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		number = Integer.parseInt(aTok.nextToken());
		while (aTok.hasMoreTokens())
		{
			if (aPC.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = aPC.getEquipmentList().iterator(); e1.hasNext();)
			{

				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE="))
				{
					if ((eq.getType().indexOf(aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						if (eq.whatHand() == Equipment.TWOWEAPON_HANDS)
						{
							--number;
							break;
						}
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							if (eq.whatHand() == Equipment.TWOWEAPON_HANDS)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.whatHand() == Equipment.TWOWEAPON_HANDS)
						{
							--number;
							break;
						}
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreLang(String aList, PlayerCharacter aPC)
	{

		int number;
		final boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PRELANG");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final int storedValue = Integer.parseInt(aTok.nextToken());
		number = storedValue;
		while (aTok.hasMoreTokens() && number > 0)
		{

			String aString = aTok.nextToken();
			if (aPC.getLanguagesList().contains(aString))
			{
				--number;
			}
		}
		if (aList.lastIndexOf("ANY") >= 0)
		{
			flag = storedValue <= aPC.getLanguagesList().size();
		}
		else
		{
			flag = (number == 0);
		}
		return flag;
	}

	private static boolean passesPreMove(PlayerCharacter aPC, boolean flag, String aList)
	{
		if (aPC == null || aPC.getRace() == null || aPC.getRace().getMovementTypes() == null)
		{
			flag = false;
		}
		else
		{

			final StringTokenizer movereqs = new StringTokenizer(aList, ",");
			while (movereqs.hasMoreTokens())
			{
				flag = false;

				final StringTokenizer movereq = new StringTokenizer(movereqs.nextToken(), "=");
				if (movereq.countTokens() < 2)
				{
					continue;
				}

				final String moveType = movereq.nextToken();
				final int moveAmount = Integer.parseInt(movereq.nextToken());

				for (int x = 0; x < aPC.getMovements().length; ++x)
				{

					if (moveType.equals(aPC.getMovementType(x)) && aPC.getMovement(x).intValue() >= moveAmount)
					{
						flag = true;
						break;
					}
				}
				if (!flag)
				{
					break;
				}
			}
		}
		return flag;
	}

	private final boolean passesPreReqTestsForList(PObject aObj, ArrayList anArrayList)
	{
		return passesPreReqTestsForList(Globals.getCurrentPC(), aObj, anArrayList);
	}

	private static boolean passesPreSA(String aList, PlayerCharacter aPC)
	{

		int number;
		final boolean flag;
		StringTokenizer aTok = new StringTokenizer(aList, ",", false);

		// wrap this in a try catch to make sure
		// that the first Token is a number
		try
		{
			number = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exceptn)
		{
			Globals.errorPrint("Exception in PRESA:" + aList + Constants.s_LINE_SEP + "Assuming 1 required", exceptn);
			number = 1;
			aTok = new StringTokenizer(aList, ",");
		}
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("number of PreSA to Match: ", number);
		}
		while (aTok.hasMoreTokens())
		{

			final String aString = aTok.nextToken();
			boolean bFound = false;
			if (!aPC.getSpecialAbilityList().isEmpty())
			{
				for (Iterator e1 = aPC.getSpecialAbilityList().iterator(); e1.hasNext();)
				{
					//final String e1String = ((SpecialAbility)e1.next()).getName();
					final Object obj = e1.next();
					String e1String = ((SpecialAbility) obj).getName();
					if (e1String.startsWith(aString))
					{
						--number;
						bFound = true;
						break;
					}
				}
			}

			//
			// Now check any templates
			//
			if (!bFound)
			{
				if (!aPC.getTemplateList().isEmpty())
				{
					for (Iterator e1 = aPC.getTemplateList().iterator(); e1.hasNext();)
					{

						final PCTemplate aTempl = (PCTemplate) e1.next();
						final ArrayList SAs = aTempl.getSpecialAbilityList(aPC.getTotalLevels(), aPC.totalHitDice());

						if (SAs != null)
						{
							for (Iterator e2 = SAs.iterator(); e2.hasNext();)
							{
								final Object obj = e2.next();
								String e1String = "";
								if (obj instanceof String)
								{
									e1String = (String) obj;
								}
								else
								{
									e1String = ((SpecialAbility) obj).getName();
								}
								if (e1String.startsWith(aString))
								{
									--number;
									break;
								}
							}
						}
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreSpell(String aList, PlayerCharacter aPC)
	{

		int number;
		final boolean flag;

		// e.g. PRESPELL:3,
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		number = Integer.parseInt(aTok.nextToken());

		final ArrayList aArrayList = aPC.aggregateSpellList("Any", "", "", 0, 20);

		//Needs to add domain spells as well
		for (Iterator domains = aPC.getCharacterDomainList().iterator(); domains.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) domains.next();
			if ((aCD != null) && (aCD.getDomain() != null))
			{
				aArrayList.addAll(Globals.getSpellsIn(-1, "", aCD.getDomain().toString()));
			}
		}

		while (aTok.hasMoreTokens())
		{

			String bString = aTok.nextToken();
			if (!aArrayList.isEmpty())
			{
				for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
				{

					final Spell aSpell = (Spell) e1.next();
					if (aSpell.getName().equals(bString))
					{
						--number;
						break;
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;
	}

	private static boolean passesPreSpellCast(String aList, PlayerCharacter aPC, boolean flag)
	{

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final ArrayList classList = (ArrayList) aPC.getClassList().clone();
		PCClass aClass = null;
		while (aTok.hasMoreTokens())
		{

			String aString = aTok.nextToken();
			if (aString.startsWith("MEMORIZE"))
			{
				if (!classList.isEmpty())
				{
					for (Iterator e1 = classList.iterator(); e1.hasNext();)
					{
						aClass = (PCClass) e1.next();
						if ((aClass.getMemorizeSpells() && aString.endsWith("N")) || (!aClass.getMemorizeSpells() && aString.endsWith("Y")))
						{
							e1.remove();
						}
					}
				}
			}
			else if (aString.startsWith("TYPE"))
			{
				if (!classList.isEmpty())
				{
					for (Iterator e1 = classList.iterator(); e1.hasNext();)
					{
						aClass = (PCClass) e1.next();
						if (aString.substring(5).lastIndexOf(aClass.getSpellType()) < 0)
						{
							e1.remove();
						}
					}
				}
			}
			flag = classList.size() > 0;
			if (!flag)
			{
				break;
			}
		}
		return flag;
	}

	private static boolean passesPreSpellSchool(String aList, PlayerCharacter aPC)
	{

		final int number;
		final boolean flag;

		// e.g. PRESPELLSCHOOL:Divination,7,1
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final String school = aTok.nextToken();
		number = Integer.parseInt(aTok.nextToken());

		final int minlevel = Integer.parseInt(aTok.nextToken());
		final ArrayList aArrayList = aPC.aggregateSpellList("Any", school, "A", minlevel, 20);
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("Spells=", aArrayList.size());
		}
		flag = (aArrayList.size() >= number);
		return flag;
	}

	private static boolean passesPreSpellSchoolSub(String aList, PlayerCharacter aPC)
	{

		final int number;
		final boolean flag;

		// e.g. PRESPELLSCHOOLSUB:Shadow,7,1
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final String subSchool = aTok.nextToken();
		number = Integer.parseInt(aTok.nextToken());

		final int minlevel = Integer.parseInt(aTok.nextToken());
		final ArrayList aArrayList = aPC.aggregateSpellList("Any", "A", subSchool, minlevel, 20);
		flag = (aArrayList.size() >= number);
		return flag;
	}

	private static boolean passesPreVar(String aList, String aType, PlayerCharacter aPC)
	{

		boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREVAR");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = true;

		int i = 0;
		if (aType.endsWith("GT"))
		{
			i = 0;
		}
		else if (aType.endsWith("GTEQ"))
		{
			i = 1;
		}
		else if (aType.endsWith("LT"))
		{
			i = 2;
		}
		else if (aType.endsWith("LTEQ"))
		{
			i = 3;
		}
		else if (aType.endsWith("NEQ"))
		{
			i = 4;
		}
		else if (aType.endsWith("EQ"))
		{
			i = 5;
		}
		while (aTok.hasMoreTokens() && flag)
		{

			final String varName = aTok.nextToken();
			String valString = "0";
			if (aTok.hasMoreTokens())
			{
				valString = aTok.nextToken();
			}

			final float aFloat = aPC.getVariableValue(valString, "").floatValue();
			final float bFloat;
			if ((!SettingsHandler.isApplyLoadPenaltyToACandSkills()) && ("ENCUMBERANCE".equals(varName)))
			{
				bFloat = 0;
			}
			else
			{
				bFloat = aPC.getVariable(varName, true, true, "", "").floatValue();
			}
			switch (i)
			{

				case 0:
					flag = (aFloat < bFloat);
					break;

				case 1:
					flag = (aFloat <= bFloat);
					break;

				case 2:
					flag = (aFloat > bFloat);
					break;

				case 3:
					flag = (aFloat >= bFloat);
					break;

				case 4:
					flag = (aFloat != bFloat);
					break;

				case 5:
					flag = (aFloat == bFloat);
					break;

				default:
					Globals.errorPrint("In PObject.passesPrereqTestForLists the prevar type " + i + " is unsupported.");
					break;
			}
		}
		return flag;
	}

	private static boolean passesPreWeaponProf(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREWEAPONPROF");
		}

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		flag = false;
		while (aTok.hasMoreTokens())
		{

			String aString = aTok.nextToken();
			final boolean hasIt = !aString.startsWith("[");
			if (!hasIt)
			{
				aString = aString.substring(1, Math.max(aString.length() - 1, aString.lastIndexOf(']')));
			}
			if ("DEITYWEAPON".equals(aString) && aPC.getDeity() != null)
			{
				for (Iterator weapIter = Utility.split(aPC.getDeity().getFavoredWeapon(), '|').iterator(); !flag && weapIter.hasNext();)
				{
					flag = aPC.hasWeaponProfNamed((String) weapIter.next());
				}
			}
			else
			{
				flag = aPC.hasWeaponProfNamed(aString);
			}
			if (!hasIt)
			{
				flag = !flag;
			}
		}
		return flag;
	}

	//
	// PRECLASS:xxx,yyy,zzz=n
	// PRECLASS:Spellcaster=5
	// PRECLASS:Spellcaster.Arcane=5
	// PRECLASS:Cleric,Sorceror,Wizard=5
	//
	// PRECLASS:1,Cleric=7,Wizard=5,Sorcerer=6
	//
	private static boolean passesPrereqClass(String aList, PlayerCharacter aPC, boolean flag)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		int number = 1;
		String aString = aTok.nextToken();
		try
		{
			number = Integer.parseInt(aString);
			aString = aTok.nextToken();
		}
		catch (Exception exc)
		{
		}

		ArrayList classesToTest = new ArrayList();
		for (; ;)
		{
			int i = aString.indexOf('=');
			if (i >= 0)
			{
				classesToTest.add(aString.substring(0, i).toUpperCase());
				int preClass = Integer.parseInt(aString.substring(i + 1));

				for (i = 0; i < classesToTest.size(); ++i)
				{
					boolean passes = false;
					aString = (String) classesToTest.get(i);
					if ("SPELLCASTER".equals(aString))
					{
						if (aPC.isSpellCaster(preClass))
						{
							passes = true;
						}
					}
					else if (aString.startsWith("SPELLCASTER."))
					{
						if (aPC.isSpellCaster(aString.substring(12), preClass))
						{
							passes = true;
						}
					}
					else
					{
						final PCClass aClass = aPC.getClassNamed(aString);
						if ((aClass != null) && (aClass.getLevel().intValue() >= preClass))
						{
							passes = true;
						}
					}
					if (Globals.isDebugMode())
					{
						Globals.debugPrint(aString + "==" + Integer.toString(preClass) + " " + passes);
					}
					if (passes)
					{
						if (--number == 0)
						{
							return true;
						}
					}
				}
				classesToTest.clear();
			}
			else
			{
				classesToTest.add(aString.toUpperCase());
			}

			if (!aTok.hasMoreTokens())
			{
				break;
			}
			aString = aTok.nextToken().toUpperCase();
		}
		return false;
	}

	private static boolean passesPrereqClassLevelMax(String aList, PlayerCharacter aPC)
	{

		final boolean flag;
		final int i = aList.lastIndexOf('=');
		boolean oneOver = false;
		final int preClass = Integer.parseInt(aList.substring(i + 1));
		final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);
		while (aTok.hasMoreTokens())
		{

			String aString = aTok.nextToken();
			if ("Spellcaster".equals(aString))
			{
				oneOver = aPC.isSpellCastermax(preClass);
			}
			else
			{

				PCClass aClass = aPC.getClassNamed(aString);
				if (aClass != null && aClass.getLevel().intValue() <= preClass)
				{
					oneOver = true;
				}
			}
		}
		flag = oneOver;
		return flag;
	}

	private static boolean passesPrereqHD(String aList, PlayerCharacter aPC)
	{

		boolean flag;

		/*
		 * either PREHD:xxx+ or PREHD:xxx-yyy
		 * with xxx being the minimum requirement
		 * and yyy being the maximum requirement
		 *
		 * author: Thomas Behr 13-03-02
		 */
		final StringTokenizer aTok = new StringTokenizer(aList, "+-");
		final int preHDMin = (aTok.hasMoreTokens()) ? Integer.parseInt(aTok.nextToken()) : Integer.MIN_VALUE;
		final int preHDMax = (aTok.hasMoreTokens()) ? Integer.parseInt(aTok.nextToken()) : Integer.MAX_VALUE;
		;

		final int hitDice = aPC.totalHitDice();
		flag = (hitDice >= preHDMin) && (hitDice <= preHDMax);
		return flag;
	}

	private static boolean passesPrereqPreUAtt(String aList, PlayerCharacter aPC)
	{

		final boolean flag;
		final int requiredValue = Integer.parseInt(aList);
		int att = 0;
		if (!aPC.getClassList().isEmpty())
		{
			for (Iterator e2 = aPC.getClassList().iterator(); e2.hasNext();)
			{

				final PCClass aClass = (PCClass) e2.next();
				String s = aClass.getUattForLevel(aClass.getLevel().intValue());
				if (s.length() == 0 || "0".equals(s))
				{
					att = Math.max(att, aClass.baseAttackBonus());
				}
				else
				{

					final StringTokenizer bTok = new StringTokenizer(s, ",", false);
					s = bTok.nextToken();
					att = Math.max(att, Integer.parseInt(s));
				}
			}
		}
		flag = att >= requiredValue;
		return flag;
	}

	private static boolean passesPrereqRace(String aList, PlayerCharacter aPC, boolean flag)
	{

		final StringTokenizer aTok = new StringTokenizer(aList, "|", false);
		while (aTok.hasMoreTokens())
		{

			final String aString = aTok.nextToken();
			final int min = Math.min(aString.length(), aPC.getRace().getName().length());
			final String tString = aString.substring(0, min);
			final String rString = aPC.getRace().getName().substring(0, min);
			if (aString.length() > 0 && aString.charAt(0) == '[' && aString.endsWith("]"))
			{
				flag = !rString.equalsIgnoreCase(aString.substring(1, min - 1));
			}
			else
			{
				flag = rString.equalsIgnoreCase(tString);
			}
			if (!aString.startsWith("[") && flag)
			{
				break;
			}
			if (aString.length() > 0 && aString.charAt(0) == '[' && !flag)
			{
				break;
			}
		}
		return flag;
	}

	private static boolean passesPrereqSkill(String aList, PlayerCharacter aPC)
	{

		int number;
		boolean flag;
		int i = aList.lastIndexOf('=');
		int ranks = 0;
		if (i >= 0)
		{
			ranks = Integer.parseInt(aList.substring(i + 1));
		}
		else
		{
			Globals.errorPrint("passesPrereqSkill: bad prereq \"" + aList + "\"");
			return false;
		}
		final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);

		// the number of skills which must match
		try
		{
			number = Integer.parseInt(aTok.nextToken());
		}
		catch (Exception exc)
		{
			number = 1;
		}

		final ArrayList sList = (ArrayList) aPC.getSkillList().clone();
		final ArrayList tList = new ArrayList();
		while (aTok.hasMoreTokens() && number > 0)
		{
			String aString = aTok.nextToken();
			final StringTokenizer bTok = new StringTokenizer(aString, "(", false);
			final String pString = bTok.nextToken();
			i = -1;
			if (pString.length() != aString.length())
			{
				i = pString.length();
			}

			String skillName = null;
			String subName = null;
			final boolean isType = aString.startsWith("TYPE.");
			if (isType)
			{
				aString = aString.substring(5).toUpperCase();
			}
			int j = -1;
			if (i >= 0)
			{
				j = -1;
				skillName = aString.substring(0, i);
				subName = aString.substring(i + 1, aString.length() - 1);
				j = subName.lastIndexOf('%');
				if (j >= 0)
				{
					subName = subName.substring(0, j);
				}
			}
			else
			{
				skillName = aString;
				j = aString.lastIndexOf('%');
			}

			boolean foundIt = false;
			Skill aSkill = null;
			for (Iterator e1 = sList.iterator(); e1.hasNext();)
			{
				if (foundIt && !isType || (number <= 0))
				{
					break;
				}
				aSkill = (Skill) e1.next();

				String aSkillName = aSkill.getName();
				if (!isType && (aSkillName.equals(skillName) || aSkillName.equals(aString) || (j >= 0 && aSkillName.startsWith(aString.substring(0, j)))))
				{
					if (aSkill.getTotalRank().intValue() < ranks || tList.contains(aSkillName))
					{
						aSkill = null;
						continue;
					}
					else if ((j >= 0) && aSkillName.startsWith(aString.substring(0, j)))
					{
						break;
					}
					else if ((j < 0) && aSkillName.equals(aString))
					{
						break;
					}
				}
				else if (isType)
				{
					if ((aSkill.getTotalRank().intValue() < ranks) || tList.contains(aSkillName))
					{
						aSkill = null;
						continue;
					}

					if (j >= 0)
					{
						final int maxCount = aSkill.getMyTypeCount();
						int k;
						for (k = 0; k < maxCount; ++k)
						{
							if (aSkill.getMyType(k).startsWith(aString))
							{
								break;
							}
						}
						if (k < maxCount)
						{
							break;
						}
					}
					else if (aSkill.isType(aString))
					{
						break;
					}
				}
				aSkill = null;
			}
			if ((aSkill != null) && (j >= 0))
			{
				sList.remove(aSkill);
			}
			flag = (aSkill != null);
			if (flag)
			{
				if (aSkill != null)
				{
					tList.add(aSkill.getName());
				}
				--number;
				foundIt = true;
			}
		}
		flag = (number == 0);
		return flag;
	}

	private static boolean passesPrereqSkillTot(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		int i = aList.lastIndexOf('=');
		int ranks = Integer.parseInt(aList.substring(i + 1));
		final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);

		// the number of feats which must match
		// number = Integer.parseInt(aTok.nextToken());
		final ArrayList sList = (ArrayList) aPC.getSkillList().clone();
		final ArrayList tList = new ArrayList();
		while (aTok.hasMoreTokens() && ranks > 0)
		{

			String aString = aTok.nextToken();
			StringTokenizer bTok = new StringTokenizer(aString, "(", false);
			String pString = bTok.nextToken();
			i = -1;
			if (pString.length() != aString.length())
			{
				i = pString.length();
			}

			String skillName = null;
			String subName = null;
			boolean isType = aString.startsWith("TYPE.");
			int j = -1;
			if (i >= 0)
			{
				j = -1;
				skillName = aString.substring(0, i);
				subName = aString.substring(i + 1, aString.length() - 1);
				j = subName.lastIndexOf('%');
				if (j >= 0)
				{
					subName = subName.substring(0, j);
				}
			}
			else
			{
				skillName = aString;
				j = aString.lastIndexOf('%');
			}

			boolean foundIt = false;
			Skill aSkill = null;
			for (int si = 0; si < sList.size(); ++si)
			{
				if ((foundIt && !isType && (j < 0)) || (ranks <= 0))
				{
					break;
				}
				aSkill = (Skill) sList.get(si);
				if (!isType && (aSkill.getName().equals(skillName) || aSkill.getName().equals(aString) || (j >= 0 && aSkill
					.getName().startsWith(aString.substring(0, j)))))
				{
					if (tList.contains(aSkill.getName()))
					{
						aSkill = null;
						continue;
					}
					if ((j >= 0) && aSkill.getName().startsWith(aString.substring(0, j)))
					{
						foundIt = true;
					}
					else if ((j < 0) && aSkill.getName().equals(aString))
					{
						foundIt = true;
					}
					if (!foundIt)
					{
						aSkill = null;
					}
				}
				else if ((isType && (aSkill.getType().indexOf(skillName.substring(5)) >= 0)))
				{
					foundIt = false;
					if (tList.contains(aSkill.getName()))
					{
						aSkill = null;
						continue;
					}
					if ((j >= 0) && aSkill.getType().startsWith(aString.substring(5, j)))
					{
						foundIt = true;
					}
					else if ((j < 0) && (aSkill.getType().indexOf(aString.substring(5)) >= 0))
					{
						foundIt = true;
					}
					if (!foundIt)
					{
						aSkill = null;
					}
				}
				if ((aSkill != null) && (j >= 0))
				{
					sList.remove(aSkill);
					--si; // to adjust for incrementer
				}
				flag = (aSkill != null);
				if (flag)
				{
					if (aSkill != null) //Only here to shut jlint up
					{
						tList.add(aSkill.getName());
						ranks -= aSkill.getTotalRank().intValue();
					}
				}
			}
		}
		flag = (ranks <= 0);
		return flag;
	}

	//
	// PRESPELLTYPE:<type>{|<type2>},<number>,<min level>
	//
	private boolean passesPrereqSpellType(String aList, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final String typeList = aTok.nextToken();
		if (aTok.countTokens() == 2)
		{
			final int number = Integer.parseInt(aTok.nextToken());
			final int minlevel = Integer.parseInt(aTok.nextToken());
			final StringTokenizer bTok = new StringTokenizer(typeList, "|", false);
			ArrayList aArrayList;
			while (bTok.hasMoreTokens())
			{
				aArrayList = aPC.aggregateSpellList(bTok.nextToken(), "", "", minlevel, 20);
				if (aArrayList.size() >= number)
				{
					//
					// Need to make sure can actually cast spells of this level
					//
					return true;
				}
			}
		}
		else
		{
			Globals.debugPrint(getName(), ":badly formed PRESPELLTYPE: " + aList);
		}
		return false;
	}

	private static boolean passesPrereqStat(String aList, PlayerCharacter aPC, boolean flag)
	{
		StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		String myTok = aTok.nextToken();
		Integer terms;
		int i, n = 0;
		boolean accumulator = true;
		boolean[] valid;
		terms = new Integer(1);

		try
		{
			terms = Integer.valueOf(myTok);
			myTok = aTok.nextToken();
		}
		catch (NumberFormatException e)
		{
		}

		aTok = new StringTokenizer(myTok, "|", false);

		valid = new boolean[aTok.countTokens()];

		while (aTok.hasMoreTokens())
		{
			myTok = aTok.nextToken();
			i = aPC.getStatList().getTotalStatFor(myTok.substring(0, 3));
			valid[n++] = i >= Integer.parseInt(myTok.substring(myTok.lastIndexOf('=') + 1));
		}
		if (terms.intValue() < valid.length)
		{
			Globals.debugPrint("OR");
			accumulator = false;
			for (n = 0; n < valid.length; ++n)
			{
				accumulator = (accumulator || valid[n]);
			}
		}
		else
		{
			Globals.debugPrint("AND");
			for (n = 0; n < valid.length; ++n)
			{
				Globals.debugPrint("valid:" + valid[n]);
				if (!valid[n])
				{
					return false;
				}
			}
			return true;
		}
		return accumulator;
	}

	private static boolean passesPrereqTemplate(PlayerCharacter aPC, String aList, boolean flag)
	{
		if (!aPC.getTemplateList().isEmpty())
		{

			final StringTokenizer aTok = new StringTokenizer(aList, "|", false);
			while (aTok.hasMoreTokens())
			{

				String templateName = aTok.nextToken();
				final int wildCard = templateName.indexOf('%');
				if (wildCard >= 0) //handle wildcards (always assume they end the line)
				{
					templateName = templateName.substring(0, wildCard);
					for (Iterator templates = aPC.getTemplateList().iterator(); templates.hasNext();)
					{

						final PCTemplate aTemplate = (PCTemplate) templates.next();
						if (aTemplate.getName().startsWith(templateName))
						{
							flag = true;
							break;
						}
					}
				}
				else
				{
					flag = (aPC.getTemplateNamed(templateName) != null);
				}
				if (flag)
				{
					break;
				}
			}
		}
		return flag;
	}

	private static boolean passesRestrict(String aList, boolean flag, PlayerCharacter aPC)
	{

		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		while (aTok.hasMoreTokens() && !flag)
		{

			final String aString = aTok.nextToken();
			final PCClass aClass = aPC.getClassNamed(aString);
			flag = (aClass == null);
		}
		return flag;
	}

	private final String preReqHTMLStringsForList(ArrayList anArrayList)
	{
		return preReqHTMLStringsForList(null, anArrayList);
	}

	private final String preReqHTMLStringsForList(PObject aObj, ArrayList anArrayList)
	{
		return preReqHTMLStringsForList(aObj, anArrayList, true);
	}

	public static final String preReqStringsForList(ArrayList anArrayList)
	{
		if (anArrayList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(anArrayList.size() * 20);

		StringTokenizer aTok = null;
		String aType = null;
		String aList = null;
		for (Iterator e = anArrayList.iterator(); e.hasNext();)
		{

			String aString = (String) e.next();
			aTok = new StringTokenizer(aString, ":", false);
			aType = aTok.nextToken();
			aList = aTok.nextToken();

			int i = 0;
			if (pString.length() > 0)
			{
				pString.append("  ");
			}
			if (aType.length() > 0 && aType.charAt(0) == '!')
			{
				pString.append('!');
				aType = aType.substring(1);
			}
			if ("PRECLASS".equals(aType))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("CLASS:");
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
					{
						pString.append(',');
					}
					pString.append(aTok.nextToken());
				}
			}
			else if ("PREATT".equals(aType))
			{
				pString.append("ATT=");
				pString.append(aList);
			}
			else if ("PREUATT".equals(aType))
			{
				pString.append("UATT=");
				pString.append(aList);
			}
			else if ("PRESTAT".equals(aType))
			{
				pString.append("STAT:").append(aType).append('=').append(aList);
			}
			else if ("PREDEITYDOMAIN".equals(aType))
			{
				pString.append("Deity Domain=");
				pString.append(aList);
			}
			else if ("PREDEITYALIGN".equals(aType))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("Deity Alignment:");
				while (aTok.hasMoreTokens())
				{

					final int raceNumber = Integer.parseInt(aTok.nextToken());
					if ((raceNumber >= 0) && (raceNumber < Globals.getAlignmentList().size()))
					{
						if (i++ > 0)
						{
							pString.append(',');
						}
						pString.append(Globals.getShortAlignmentAtIndex(raceNumber));
					}
					else
					{
						GuiFacade.showMessageDialog(null, "Invalid alignment: " + raceNumber, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			else if ("PREALIGN".equals(aType))
			{
				aTok = new StringTokenizer(aList, ",", false);
				pString.append("Alignment:");
				while (aTok.hasMoreTokens())
				{

					int alignNumber;
					int idx = -1;
					String preAlign = aTok.nextToken();

					//
					// Check for [blah=blah=#],#,#,#,#
					//
					try
					{
						if (preAlign.length() > 0 && preAlign.charAt(0) == '[' && preAlign.endsWith("]"))
						{
							idx = preAlign.lastIndexOf('=');
							alignNumber = Integer.parseInt(preAlign.substring(idx + 1, preAlign.length() - 1));
						}
						else
						{
							alignNumber = Integer.parseInt(preAlign);
						}
						if ((alignNumber >= 0) && (alignNumber < Globals.getAlignmentList().size()))
						{
							if (i++ > 0)
							{
								pString.append(',');
							}
							if (idx >= 0)
							{
								pString.append(preAlign.substring(0, idx + 1));
							}
							pString.append(Globals.getShortAlignmentAtIndex(alignNumber));
							if (idx >= 0)
							{
								pString.append(']');
							}
						}
						else
						{

							String msg = "Invalid alignment: " + alignNumber;
							Globals.errorPrint(msg);
							GuiFacade.showMessageDialog(null, msg, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
						}
					}
					catch (Exception exc)
					{

						String msg = "Invalid alignment: " + preAlign;
						Globals.errorPrint(msg, exc);
						GuiFacade.showMessageDialog(null, msg, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
			else
			{
				pString.append(aType.substring(3)).append(':');
				aTok = new StringTokenizer(aList, ",", false);
				while (aTok.hasMoreTokens())
				{
					if (i++ > 0)
					{
						pString.append(',');
					}
					pString.append(aTok.nextToken());
				}
			}
		}
		return pString.toString();
	}

	final void removeBonus(String bonusString, String chooseString)
	{
		bonusString = makeBonusString(bonusString, chooseString);

		final int index = -1;
		if (bonusList != null)
		{
			bonusList.indexOf(bonusString);
		}
		if (index >= 0)
		{
			bonusList.remove(index);
		}
		else
		{
			Globals.errorPrint("removeBonus: Could not find bonus: " + bonusString + " in bonusList.");
		}
		removeSave("BONUS|" + bonusString);
	}

	private void removeMyType(String myType)
	{
		myTypeList.remove(myType);
	}

	final String getPCCText(boolean saveName)
	{
		Iterator e;
		String aString;
		final StringBuffer txt = new StringBuffer(200);
		if (saveName)
		{
			txt.append(getName());
		}

		if (getNameIsPI())
		{
			txt.append("\tNAMEISPI:Y");
		}

		if (!getName().equals(getKeyName()))
		{
			txt.append("\tKEY:").append(getKeyName());
		}

		if (getBonusList().size() != 0)
		{
			for (e = getBonusList().iterator(); e.hasNext();)
			{
				aString = (String) e.next();
				txt.append("\tBONUS:").append(aString);
			}
		}

		if ((ccSkillList != null) && (ccSkillList.size() != 0))
		{
			txt.append("\tCCSKILL:").append(Utility.unSplit(ccSkillList, "|"));
		}

		if ((cSkillList != null) && (cSkillList.size() != 0))
		{
			txt.append("\tCSKILL:").append(Utility.unSplit(cSkillList, "|"));
		}

		int iCount = getVariableCount();
		if (iCount != 0)
		{
			for (int i = 0; i < iCount; ++i)
			{
				aString = getVariable(i);
				if (aString.startsWith("-9|"))
				{
					aString = aString.substring(3);
				}
				txt.append("\tDEFINE:").append(aString);
			}
		}

		if ((DR != null) && (DR.length() != 0))
		{
			txt.append("\tDR:").append(DR);
		}

		final Set langSet = getAutoLanguageNames();
		if (langSet.size() != 0)
		{
			txt.append("\tLANGAUTO:").append(Utility.unSplit(langSet, ","));
		}

		iCount = getPreReqCount();
		if (iCount != 0)
		{
			for (int i = 0; i < iCount; ++i)
			{
				txt.append('\t').append(getPreReq(i));
			}
		}

		if ((specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for (e = specialAbilityList.iterator(); e.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) e.next();
				txt.append("\tSA:").append(sa.toString());
			}
		}

		if ((SR != null) && (SR.length() != 0))
		{
			txt.append("\tSR:").append(SR);
		}

		if ((weaponProfAutos != null) && (weaponProfAutos.size() != 0))
		{
			txt.append("\tWEAPONAUTO:").append(Utility.unSplit(weaponProfAutos, "|"));
		}

		if (getMyTypeCount() != 0)
		{
			txt.append("\tTYPE:").append(getType());
		}

		aString = getSourcePage();
		if (aString.length() != 0)
		{
			txt.append("\tSOURCEPAGE:").append(aString);
		}

		return txt.toString();
	}

	// should be in the form of #|KIT1|KIT2|KIT3|etc
	public final void setKitString(final String arg)
	{
		kitString = arg;
	}

	public final void makeKitSelection()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setArmorProfListStable(false);
		makeKitSelection(0);
	}

	final void makeKitSelection(int arg)
	{
		if (kitString == null)
			return;
		final StringTokenizer aTok = new StringTokenizer(kitString, "|", false);
		// first element is prelevel - should be 0 for everything but PCClass entries
		final int aLevel = Integer.parseInt(aTok.nextToken());
		if (aLevel > arg)
			return;
		final int num = Integer.parseInt(aTok.nextToken()); // number of kit selections
		ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
			aList.add(aTok.nextToken());
		final PlayerCharacter aPC = Globals.getCurrentPC();

		if (num != aList.size())
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setTitle("Kit Selection");
			c.setPool(num);
			c.setPoolFlag(false);
			c.setAvailableList(aList);
			c.show();
			aList = c.getSelectedList();
		}
		if (aList.size() > 0)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final String aString = (String) i.next();
				final Kit theKit = Globals.getKitNamed(aString);
				if (theKit == null ||
					(aPC.getKitInfo() != null && aPC.getKitInfo().indexOf(theKit) >= 0))
					continue;
				ArrayList thingsToAdd = new ArrayList();
				ArrayList warnings = new ArrayList();
				theKit.addKitFeats(thingsToAdd, warnings);
				theKit.addKitProfs(thingsToAdd, warnings);
				theKit.addKitGear(thingsToAdd, warnings);
				theKit.addKitSpells(thingsToAdd, warnings);
				theKit.addKitSkills(thingsToAdd, warnings);
				theKit.processKit(thingsToAdd);
			}
		}
	}

	public final void setRegionString(final String arg)
	{
		regionString = arg;
	}

	final void makeRegionSelection()
	{
		makeRegionSelection(0);
	}

	final void makeRegionSelection(int arg)
	{
		if (regionString == null)
			return;
		final StringTokenizer aTok = new StringTokenizer(regionString, "|", false);
		// first element is prelevel - should be 0 for everything but PCClass entries
		final int aLevel = Integer.parseInt(aTok.nextToken());
		if (aLevel > arg)
			return;
		final int num = Integer.parseInt(aTok.nextToken()); // number of selections
		ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
			aList.add(aTok.nextToken());
		final PlayerCharacter aPC = Globals.getCurrentPC();

		if (num != aList.size())
		{
			final ChooserInterface c = ChooserFactory.getChooserInstance();
			c.setTitle("Region Selection");
			c.setPool(num);
			c.setPoolFlag(false);
			c.setAvailableList(aList);
			c.show();
			aList = c.getSelectedList();
		}
		if (aList.size() > 0)
		{
			for (Iterator i = aList.iterator(); i.hasNext();)
			{
				final String aString = (String) i.next();
				if (aPC.getRegion().equalsIgnoreCase(aString))
					continue;
				aPC.setRegion(aString);
			}
		}
	}

	public final void setAutoString(final String arg)
	{
		if (autoString == null || arg == null)
			autoString = arg; // if either is null, take that value
		else
			autoString = autoString + "|" + arg; // otherwise append it after a pipe
	}

	public final void addSelectedArmorProfs(ArrayList aList)
	{
		if (selectedArmorProfs == null)
		{
			selectedArmorProfs = new ArrayList();
		}
		for (Iterator i = aList.iterator(); i.hasNext(); )
		{
			String aString = (String)i.next();
			if (!selectedArmorProfs.contains(aString))
			{
				selectedArmorProfs.add(aString);
			}
		}
	}

	public final ArrayList getSelectedArmorProfs()
	{
		return selectedArmorProfs;
	}

	public void makeChoices()
	{
		getChoices(choiceString, null);
	}

	private boolean doComparison(String comparison, int iVal1, int iVal2)
	{
		return doComparison(comparison, iVal1, iVal2, null, null);
	}

	private static boolean doComparison(String comparison, int iVal1, int iVal2, PlayerCharacter aPC, String tag)
	{
		if ((aPC != null) && (tag != null))
		{
			iVal1 += (int) aPC.getTotalBonusTo("SLOTS", tag, true);
		}

		if ("EQ".equals(comparison))
		{
			return iVal1 == iVal2;
		}
		else if ("LT".equals(comparison))
		{
			return iVal1 < iVal2;
		}
		else if ("LTEQ".equals(comparison))
		{
			return iVal1 <= iVal2;
		}
		else if ("GT".equals(comparison))
		{
			return iVal1 > iVal2;
		}
		else if ("GTEQ".equals(comparison))
		{
			return iVal1 >= iVal2;
		}
		else if ("NEQ".equals(comparison))
		{
			return iVal1 != iVal2;
		}
		Globals.debugPrint("Prereq failed, unknown comparison: " + comparison);
		return false;
	}

	private static void readObject (ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}


/*	private Object addBonus(String aString)
	{
		Object aBonus = null;
		if (this instanceof PCClass)
		{
			int idx = aString.indexOf('|');
			if (idx >= 0)
			{
				try
				{
					int level = Integer.parseInt(aString.substring(0, idx));
					aBonus = Globals.getBonusFromPool(aString);
					if (aBonus == null)
					{
						aBonus = new ClassBonus(level, aString.substring(idx + 1));
					}
					else
					{
						aBonus = new ClassBonus(level, (Bonus)aBonus);
					}
				}
				catch (Exception exc)
				{
					System.err.println("Missing level in class BONUS tag: " + aString);
				}
			}
		}
		else
		{
			aBonus = Globals.getBonusFromPool(aString);
			if (aBonus == null)
			{
				aBonus = new Bonus(aString);
			}
		}
		if (aBonus != null)
		{
if (!aString.equalsIgnoreCase(aBonus.toString()))
{
System.err.println("1=" + aString);
System.err.println("2=" + aBonus.toString());
}
		}
		return aBonus;
	}
*/
}
