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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:42 $
 *
 */

package pcgen.core;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.ClassBonus;
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
 **/
public class PObject extends Object implements Cloneable, Serializable, Comparable
{
	private static final long serialVersionUID = 1;

	private static final int COMPARETYPE_UNKNOWN = -1;
	private static final int COMPARETYPE_GT = 0;
	private static final int COMPARETYPE_GTEQ = 1;
	private static final int COMPARETYPE_LT = 2;
	private static final int COMPARETYPE_LTEQ = 3;
	private static final int COMPARETYPE_NEQ = 4;
	private static final int COMPARETYPE_EQ = 5;

	private static final ArrayList emptyBonusList = new ArrayList();
	protected String DR = null; // a string so that we can handle formulas
	protected String SR = null; // a string so that we can handle formulas
	protected ArrayList associatedList = null;
	protected ArrayList bonusList = null;
	protected ArrayList tempBonusList = new ArrayList();
	protected String choiceString = "";
	protected boolean isSpecified = false;
	protected String keyName = "";
	protected String name = "";
	protected String description = "";
	protected String outputName = "";
	protected ArrayList specialAbilityList = null;
	protected ArrayList spellList = null;
	protected ArrayList udamList = null;
	protected ArrayList umultList = null;
	protected boolean visible = true;
	protected HashMap vision = null; // now a String available globally
	protected ArrayList weaponProfAutos = null;

	protected ArrayList autoArray = null; // AUTO: tag
	private int baseQuantity = 1;
	private HashMap bonusMap = null;
	protected ArrayList cSkillList = null;
	protected ArrayList ccSkillList = null;
	private ArrayList characterSpellList = null;
	private boolean isApplied = false; // has this bonus been applied?
	private boolean isNewItem = true;
	//protected String kitString = null;
	protected ArrayList kits = null;
	private TreeSet languageAutos = null;
	private ArrayList myTypeList = null;
	private boolean nameIsPI = false; // name is Product Identity
	private boolean descIsPI = false; // description is Product Identity
	private ArrayList preReqList = null;
	private String qualifyString = "alwaysValid";
	protected String regionString = null;
	private ArrayList saveList = null;
	private ArrayList selectedArmorProfs = null;
	private ArrayList selectedWeaponProfBonus = null;
	private String source = "";
	private int sourceFileIndex = -1;
	private int sourceIndex = -1;
	private String sourcePage = "";
	private VariableList variableList = null;
	protected ArrayList levelAbilityList = null;
	protected Campaign sourceCampaign = null;
	protected HashMap sourceMap = new HashMap();
	// movement related variables
	private Integer movement;
	private Integer[] movements;
	private String[] movementTypes;
	private Integer[] movementMult;
	private String[] movementMultOp;
	private int moveRatesFlag;

	/**
	 * return an ArrayList of CharacterSpell with following criteria:
	 * Spell aSpell  (ignored if null),
	 * book          (ignored if ""),
	 * level         (ignored if < 0),
	 * fList         (ignored if null) Array of Feats
	 */
	public final ArrayList getCharacterSpell(Spell aSpell, String book, int level)
	{
		return getCharacterSpell(aSpell, book, level, null);
	}

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

	public final boolean getNameIsPI()
	{
		return nameIsPI;
	}

	public final void setNameIsPI(boolean a)
	{
		nameIsPI = a;
	}

	public final boolean getDescIsPI()
	{
		return descIsPI;
	}

	public final void setDescIsPI(boolean a)
	{
		descIsPI = a;
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

	/**
	 * return Set of Strings of Language names
	 **/
	public final Set getAutoLanguageNames()
	{
		final Set aSet = getAutoLanguages();
		final Set bSet = new TreeSet();
		for (Iterator i = aSet.iterator(); i.hasNext();)
		{
			bSet.add(i.next().toString());
		}
		return bSet;
	}

	/**
	 * return Set of Language objects
	 **/
	public final Set getAutoLanguages()
	{
		final Set aSet = new TreeSet();
		if (languageAutos == null || languageAutos.isEmpty())
		{
			return aSet;
		}
		/*TODO This is never used I will remove it the next time I come across it unless this is changed. JK 2003-03-22
		for (Iterator i = languageAutos.iterator(); i.hasNext();)
		{
			final String langString = i.next().toString();
		} */
		aSet.addAll(languageAutos);
		return aSet;
	}

	public final void setBaseQty(String aString)
	{
		try
		{
			baseQuantity = Integer.parseInt(aString);
		}
		catch (NumberFormatException e)
		{
			baseQuantity = 0;
			Globals.errorPrint("Badly formed BaseQty string: " + aString);
		}
	}

	public final int getBaseQty()
	{
		return baseQuantity;
	}

	public ArrayList getTempBonusList()
	{
		return tempBonusList;
	}

	public void resetTempBonusList()
	{
		tempBonusList = new ArrayList();
	}

	public void addTempBonus(BonusObj aBonus)
	{
		getTempBonusList().add(aBonus);
	}

	public void removeTempBonus(BonusObj aBonus)
	{
		getTempBonusList().remove(aBonus);
	}

	public ArrayList getBonusList()
	{
		if (bonusList != null)
		{
			return bonusList;
		}
		return emptyBonusList;
	}

	public final void addBonusList(final String aString)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList();
		}
		if (SettingsHandler.validateBonuses())
		{
			addBonus(aString);
		}
		bonusList.add(aString);
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

	public HashMap getBonusMap()
	{
		if (bonusMap == null)
		{
			bonusMap = new HashMap();
		}
		return bonusMap;
	}

	public void putBonusMap(String aKey, String aVal)
	{
		getBonusMap().put(aKey, aVal);
	}

	/**
	 * @param bonus       a Number (such as 2)
	 * @param bonusType  "COMBAT.AC.Dodge" or "COMBAT.AC.Dodge.STACK"
	 **/
	final void setBonusStackFor(double bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
		}

		// Default to non-stacking bonuses
		int index = -1;

		final StringTokenizer aTok = new StringTokenizer(bonusType, ".", false);

		// e.g. "COMBAT.AC.DODGE"
		if ((bonusType != null) && (aTok.countTokens() > 2))
		{
			// we need to get the 3rd token to see
			// if it should .STACK or .REPLACE
			String aString = aTok.nextToken();
			aString = aTok.nextToken();
			aString = aTok.nextToken(); // Type: .DODGE

			if (aString != null)
			{
				index = Globals.getBonusStackList().indexOf(aString); // e.g. Dodge
			}
			//
			// un-named (or un-TYPE'd) bonus should stack
			if (aString == null)
			{
				index = 1;
			}
			else if (aString.equals("NULL"))
			{
				index = 1;
			}
		}

		// .STACK means stack
		// .REPLACE stacks with other .REPLACE bonuses
		if (bonusType.endsWith(".STACK") || bonusType.endsWith(".REPLACE"))
		{
			index = 1;
		}

		if (index == -1) // a non-stacking bonus
		{
			final String aVal = (String) getBonusMap().get(bonusType);
			if (aVal == null)
			{
				putBonusMap(bonusType, String.valueOf(bonus));
			}
			else
			{
				putBonusMap(bonusType, String.valueOf(Math.max(bonus, Float.parseFloat(aVal))));
			}
		}
		else // a stacking bonus
		{
			if (bonusType == null)
			{
				bonusType = "";
			}
			//else if (bonusType.endsWith(".STACK"))
			else if (bonusType.endsWith(".REPLACE.STACK"))
			{
				// Check for the special case of:
				// COMBAT.AC.Armor.REPLACE.STACK
				// and remove the .STACK
				bonusType = bonusType.substring(0, bonusType.length() - 6);
			}

			final String aVal = (String) getBonusMap().get(bonusType);
			if (aVal == null)
			{
				putBonusMap(bonusType, String.valueOf(bonus));
			}
			else
			{
				putBonusMap(bonusType, String.valueOf(bonus + Float.parseFloat(aVal)));
			}
		}
	}

	/**
	 * #############################################
	 *    Movement related accessors and mutators
	 * #############################################
	 **/

	public void setMovementTypes(String[] arrayString)
	{
		movementTypes = arrayString;
	}

	public final String[] getMovementTypes()
	{
		return movementTypes;
	}

	/**
	 * returns an empty string if there are no movement types defined
	 **/
	public String getMovementType(int i)
	{
		if ((movementTypes != null) && (i < movementTypes.length))
		{
			return movementTypes[i];
		}
		return "";
	}

	public final Integer[] getMovements()
	{
		return movements;
	}

	public final Integer[] getMovementMult()
	{
		return movementMult;
	}

	public final String[] getMovementMultOp()
	{
		return movementMultOp;
	}

	public final int getMoveRatesFlag()
	{
		return moveRatesFlag;
	}

	public final void setMoveRatesFlag(int i)
	{
		moveRatesFlag = i;
	}

	public void setMoveRates(String moveparse)
	{
		final StringTokenizer moves = new StringTokenizer(moveparse, ",");
		String tok;
		int newmove = 0;
		if (moves.countTokens() == 1)
		{
			tok = moves.nextToken();

			Globals.debugPrint("single option in ", this.toString());
			if ((tok.length() > 0) && ((tok.charAt(0) == '*') || (tok.charAt(0) == '/')))
			{
				setMovement(newmove);
				movements = new Integer[1];
				movements[0] = getMovement();
				movementTypes = new String[1];
				movementMult = new Integer[1];
				movementMult[0] = new Integer(tok.substring(1));
				movementMultOp = new String[1];
				movementMultOp[0] = tok.substring(0, 1);
			}
			else if (tok.length() > 0)
			{
				try
				{
					newmove = Integer.parseInt(tok);
				}
				catch (NumberFormatException e)
				{
					Globals.errorPrint("Badly formed movement string: " + tok);
					newmove = 0;
				}
				setMovement(newmove);
				movements = new Integer[1];
				movements[0] = getMovement();
				movementTypes = new String[1];
				movementMult = new Integer[1];
				movementMult[0] = new Integer(0);
				movementMultOp = new String[1];
				movementMultOp[0] = "";
			}
			movementTypes[0] = "Walk";
		}
		else
		{
			final int arraySize = moves.countTokens() / 2;
			movements = new Integer[arraySize];
			movementTypes = new String[arraySize];
			movementMult = new Integer[arraySize];
			movementMultOp = new String[arraySize];

			int x = 0;
			while (moves.countTokens() > 1)
			{
				movementTypes[x] = moves.nextToken(); // "Walk"
				movementMult[x] = new Integer(0);
				movementMultOp[x] = "";

				tok = moves.nextToken();

				Globals.debugPrint("multiple option in " + this.toString() + ":" + movementTypes[x] + " " + tok);

				if ((tok.length() > 0) && ((tok.charAt(0) == '*') || (tok.charAt(0) == '/')))
				{
					Globals.debugPrint("* or /");
					movementMult[x] = new Integer(tok.substring(1));
					movementMultOp[x] = tok.substring(0, 1);
					movements[x] = new Integer(0);
				}
				else if (tok.length() > 0)
				{
					Globals.debugPrint("normal");
					movementMult[x] = new Integer(0);
					movementMultOp[x] = "";
					try
					{
						newmove = Integer.parseInt(tok);
					}
					catch (NumberFormatException e)
					{	
						Globals.errorPrint("Badly formed PRESTAT token: " + tok);	
						newmove = 0;		
					}
					movements[x] = new Integer(newmove);
					if ("Walk".equals(movementTypes[x]))
					{
						setMovement(movements[x]);
					}
				}
				x++;
			}
		}
	}

	public final Integer getMovement()
	{
		return movement;
	}

	public final Integer getMovement(int i)
	{
		if ((movements != null) && (i < movements.length))
		{
			return movements[i];
		}
		return new Integer(0);
	}

	public void setMovement(Integer anInt)
	{
		movement = new Integer(anInt.toString());
	}

	public void setMovement(int anInt)
	{
		movement = new Integer(anInt);
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

				Skill aSkill;
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

				Skill aSkill;
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

	public void setDR(String drString)
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

	public final void addLanguageAutos(String aString)
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
			else if ("ALL".equals(bString))
			{
				languageAutos.addAll(Globals.getLanguageList());
			}
			else if (bString.startsWith("TYPE="))
			{
				String aType = bString.substring(5);
				ArrayList bList = Globals.getLanguageList();
				bList = Globals.getLanguagesFromListOfType(bList, aType);
				languageAutos.addAll(bList);
			}
			else
			{
				final Language aLang = Globals.getLanguageNamed(bString);
				if (aLang != null)
				{
					languageAutos.add(aLang);
				}
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

	public Iterator getMyTypeIterator()
	{
		if (myTypeList == null)
		{
			return null;
		}
		return myTypeList.iterator();
	}

	void fireNameChanged(final String oldName, final String newName)
	{
	}

	public void setName(String aString)
	{
		if (!aString.endsWith(".MOD"))
		{
			fireNameChanged(name, aString);
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

	/**
	 * Have the bonuses associated with this object been applied
	 **/
	public boolean getApplied()
	{
		return isApplied;
	}

	public void setApplied(boolean aBool)
	{
		isApplied = aBool;
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

	public final String getQualifyString()
	{
		return qualifyString;
	}

	public void setSR(String newSR)
	{
		if (".CLEAR".equals(newSR))
		{
			SR = null;
		}
		else
		{
			SR = newSR;
		}
	}

	public String getSRFormula()
	{
		return SR;
	}

	protected int getSR()
	{
		final String srFormula = getSRFormula();
		//if there's a current PC, go ahead and evaluate the formula
		if ((srFormula != null) && (Globals.getCurrentPC() != null))
		{
			return Globals.getCurrentPC().getVariableValue(srFormula, "").intValue();
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

	public final String getSourceWithKey(String key)
	{
		return (String) sourceMap.get(key);
	}

	public final void setSource(String aSource)
	{
		StringTokenizer aTok = new StringTokenizer(aSource, "|", false);
		while (aTok.hasMoreTokens())
		{
			String arg = aTok.nextToken();
			if (arg.equals(".CLEAR"))
			{
				sourceMap.clear();
				continue;
			}
			String key = arg.substring(6, arg.indexOf(":"));
			String val = arg.substring(arg.indexOf(":") + 1);
			sourceMap.put(key, val);
		}
	}

	public final void setSourceMap(HashMap arg)
	{
		sourceMap.clear();
		sourceMap.putAll(arg);
	}

	public final String getSourceInForm(int sourceDisplay)
	{
		return returnSourceInForm(sourceDisplay, true);
	}

	private String returnSourceInForm(int sourceDisplay, boolean includePage)
	{
		StringBuffer buf = new StringBuffer();
		Campaign sourceCampaign = getSourceCampaign();
		String key = "LONG";
		switch (sourceDisplay)
		{
			case Constants.SOURCESHORT:
				key = "SHORT";
				break;
			case Constants.SOURCEWEB:
				key = "WEB";
				break;
			case Constants.SOURCEPAGE:
				key = "PAGE";
				break;
		}
		// get SOURCE for this item with desired key
		String aSource = getSourceWithKey(key);
		if (sourceCampaign != null)
		{
			// if sourceCampaign object exists, get it's publisher entry for same key
			String arg = sourceCampaign.getPublisherWithKey(key);
			// returned string starts with publisher entry
			buf = buf.append(arg);
			if (arg.length() > 0)
			{
				buf = buf.append(" - ");
			}
			// if this item's source is null, try to get it from sourceCampaign object
			if (aSource == null)
			{
				aSource = sourceCampaign.getSourceWithKey(key);
			}
		}

		// append the source entry to the return string
		if (aSource != null)
		{
			buf = buf.append(aSource);
		}

		// if the page is desired, append that entry to the returned string
		if (includePage && sourceDisplay != Constants.SOURCEWEB)
		{
			aSource = getSourceWithKey("PAGE");
			if (aSource != null)
			{
				buf = buf.append(", ").append(aSource);
			}
		}
		return buf.toString();
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
		return returnSourceInForm(Constants.SOURCEPAGE, false);
	}

	public final String getSourceShort(int maxNumberofChars)
	{
		String shortString = returnSourceInForm(Constants.SOURCESHORT, false);

		// When I say short, I mean short!
		if (shortString.length() > maxNumberofChars)
		{
			shortString = shortString.substring(0, maxNumberofChars);
		}
		return shortString;
	}

	public final String getSourceWeb()
	{
		return returnSourceInForm(Constants.SOURCEWEB, false);
	}

	/**
	 * This method sets the special abilities granted by this [object].
	 * For efficiency, avoid calling this method except from I/O routines.
	 * @param aString String of special abilities delimited by pipes
	 * @param level int level at which the ability is gained
	 */
	public void setSpecialAbilityList(String aString, int level)
	{

		//removed the parsing by ","  All SAs must now use separate SA: tags
		// Tracker #666268 -Lone Jedi
		if (specialAbilityList == null)
		{
			specialAbilityList = new ArrayList();
		}
		if (".CLEAR".equals(aString))
		{
			specialAbilityList.clear();
			return;
		}

		SpecialAbility sa = new SpecialAbility(aString);
		specialAbilityList.add(sa);
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

	public ArrayList getSpellList()
	{
		return spellList;
	}

	public final void clearSpellList()
	{
		spellList = null;
	}

	static final void getSpellTypeChoices(String aChoice, ArrayList availList, ArrayList uniqueList)
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
			aString = aTok.nextToken();
			int minLevel = 1;
			try
			{
				minLevel = Integer.parseInt(aString);
			}
			catch (NumberFormatException e)
			{
				Globals.errorPrint("Badly formed minLevel token: " + aString);	
			}
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
						final int aLevel = aClass.getLevel() - 1;
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
						//mString = mString.substring(0, i) + new Integer(j).toString() + bString;
						mString = mString.substring(0, i) + String.valueOf(j) + bString;
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

							int aLevel = aClass.getLevel() - 1;
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

	protected void doGlobalUpdate(final String aString)
	{
	}

	public void setTypeInfo(final String aString)
	{
		boolean bRemove = false;
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ".", false);
		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();
			if (bRemove)
			{
				removeMyType(aType);
				bRemove = false;
			}
			else if ("REMOVE".equals(aType))
			{
				bRemove = true;
			}
			else if ("CLEAR".equals(aType))
			{
				clearMyType();
			}
			else if (!containsMyType(aType))
			{
				doGlobalUpdate(aType);
				addMyType(aType);
			}
		}
	}

	public String getType()
	{
		int x = getMyTypeCount();
		if (x == 0)
		{
			return "";
		}

		final StringBuffer aType = new StringBuffer(getMyTypeCount() * 5); //Just a guess.

		aType.append(getMyType(0));

		for (int i = 1; i < x; ++i)
		{
			aType.append('.');
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
		String myType;
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

	public final ArrayList getUdamList()
	{
		return udamList;
	}

	/**
	 * This gets an unmodifiable representation of a variable
	 *
	 */
	public final Iterator getVariableIterator()
	{
		if (variableList == null)
		{
			return EmptyIterator.EMPTY_ITERATOR;
		}
		return variableList.iterator();
	}

	/**
	 * This gets the entire definition for a variable, | values and all
	 *
	 * not-yet-deprecated This should be replaced by getVariable
	 */
	public final String getVariableDefinition(int i)
	{
		return variableList.getDefinition(i);
	}

	protected final void setVariable(int idx, String var)
	{
		variableList.set(idx, var);
	}

	protected final void addAllVariablesFrom(PObject other)
	{
		if (other.getVariableCount() > 0)
		{
			if (variableList == null)
			{
				variableList = new VariableList();
			}
			variableList.addAll(other.variableList);
		}
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

	public void setVision(String aString)
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
			if (cString.startsWith(".CLEAR.") || "2".equals(cString))
			{
				if (vision == null)
				{
					continue;
				}
				if (cString.startsWith(".CLEAR."))
				{
					// Strip off the .CLEAR.
					dString = dString.substring(7);
				}
				Object aKey = vision.get(dString);
				if (aKey != null)
				{
					vision.remove(dString);
				}
			}
			else if (cString.startsWith(".SET.") || "0".equals(cString))
			{
				if (vision == null)
				{
					vision = new HashMap();
				}
				vision.clear();
				if (cString.startsWith(".SET."))
				{
					// Strip off the .SET.
					dString = dString.substring(5);
				}
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

				// expecting value in form of: Darkvision (60')
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

	public HashMap getVision()
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
				if (!weaponProfAutos.contains(bString))
				{
					weaponProfAutos.add(bString);
				}
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

	public final ArrayList getAutoArray()
	{
		return autoArray;
	}

	// e.g. tag = "ARMORPROF", aList is list of armor proficiencies
	final void addAutoTagsToList(String tag, AbstractCollection aList)
	{
		if (autoArray == null)
		{
			return;
		}
		for (Iterator i = autoArray.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();
			if (!aString.startsWith(tag))
			{
				continue;
			}
			StringTokenizer aTok = new StringTokenizer(aString, "|", false);
			aTok.nextToken(); // removes tag token
			String tok;
			while (aTok.hasMoreTokens())
			{
				tok = aTok.nextToken();
				//if (tok.startsWith("TYPE="))
				if (tok.startsWith("TYPE=") && tag.startsWith("WEAPON"))
				{
					String desiredTypes = "Weapon";
					ArrayList listFromWPType = null;
					ArrayList listFromEquipmentType = null;
					StringTokenizer bTok = new StringTokenizer(tok, ".", false);
					while (bTok.hasMoreTokens())
					{
						String bString = bTok.nextToken();
						if (bString.startsWith("TYPE="))
						{
							bString = bString.substring(5);
						}
						if (Globals.weaponTypesContains(bString))
						{
							listFromWPType = Globals.getWeaponProfs(bString);
						}
						else
						{
							desiredTypes += "." + bString;
						}
					}
					if (desiredTypes.indexOf(".") > -1)
					{
						listFromEquipmentType = Globals.getEquipmentOfType(Globals.getEquipmentList(), desiredTypes, "");
					}
					ArrayList addWPs = new ArrayList();
					if (listFromWPType != null)
					{
						for (Iterator li = listFromWPType.iterator(); li.hasNext();)
						{
							addWPs.add(li.next().toString());
						}
					}
					if (listFromEquipmentType != null)
					{
						ArrayList bList = new ArrayList();
						for (Iterator li = listFromEquipmentType.iterator(); li.hasNext();)
						{
							String bString = ((Equipment) li.next()).profName();
							bList.add(bString);
						}
						if (listFromWPType == null)
						{
							addWPs.addAll(bList);
						}
						else
						{
							addWPs.retainAll(bList);
						}
					}
					aList.addAll(addWPs);
				}
				else
				{
					// add tok to list
					aList.add(tok);
				}
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

	public void addSpells(int level, String line)
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
		StringBuffer spellBuf;
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

	//public final void addTypeInfo(final String aString)
	//{
	//	final String typeString = aString.toUpperCase().trim();
	//	final StringTokenizer aTok = new StringTokenizer(typeString, ".", false);
	//	while (aTok.hasMoreTokens())
	//	{
	//		final String aType = aTok.nextToken();
	//		if (!containsMyType(aType))
	//		{
	//			addMyType(aType);
	//		}
	//	}
	//}

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

	public final ArrayList getUmultList()
	{
		return umultList;
	}

	public final void addVariable(String entry)
	{
		if (variableList == null)
		{
			variableList = new VariableList();
		}
		variableList.add(entry);
	}

	public final boolean hasVariableNamed(String variableName)
	{
		if (variableList == null)
		{
			return false;
		}
		return variableList.hasVariableNamed(variableName);
	}

	public final Set getVariableNamesAsUnmodifiableSet()
	{
		if (variableList == null)
		{
			return null;
		}
		return variableList.getVariableNamesAsUnmodifiableSet();
	}

	public final double bonusTo(String aType, String aName)
	{
		return bonusTo(aType, aName, Globals.getCurrentPC());
	}

	double bonusTo(String aType, String aName, Object obj)
	{
		return bonusTo(aType, aName, obj, getBonusList());
	}

	final double bonusTo(String aType, String aName, Object obj, ArrayList aBonusList)
	{
		if (aBonusList == null || aBonusList.size() == 0)
		{
			return 0;
		}

		double retVal = 0;
		int iTimes = 1;

		if ("VAR".equals(aType))
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

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final String aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();

		for (Iterator b = aBonusList.iterator(); b.hasNext();)
		{
			String bString = ((String) b.next());

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
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes);
					}
					bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString();
				}
			}
			retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes);
		}
		return retVal;
	}

	/**
	 * calcBonus adds together all the bonuses for aType of aName
	 * @param bString	Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param aType		Such as "COMBAT"
	 * @param aName		Such as "AC"
	 * @param aTypePlusName		"COMBAT.AC."
	 * @param obj		The object to get the bonus from
	 * @param iTimes	multiply bonus * iTimes
	 **/
	private double calcBonus(String bString, String aType, String aName, String aTypePlusName, Object obj, int iTimes)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|", false);
		if (aTok.countTokens() < 3)
		{
			Globals.errorPrint("Badly formed BONUS:" + bString);
			return 0;
		}

		String aString = aTok.nextToken();

		if ((!aString.equalsIgnoreCase(aType) && !aString.endsWith("%LIST")) ||
			(aString.endsWith("%LIST") && (numberInList(aType) == 0)) ||
			(aName.equals("ALL")))
		{
			return 0;
		}

		final String aList = aTok.nextToken();
		if (!aList.equals("LIST") && !aList.equals("ALL") && aList.toUpperCase().indexOf(aName.toUpperCase()) < 0)
		{
			return 0;
		}

		if (aList.equals("ALL") &&
			((aName.indexOf("STAT=") >= 0) || (aName.indexOf("TYPE=") >= 0) || (aName.indexOf("LIST") >= 0) || (aName.indexOf("VAR") >= 0)))
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
				Globals.errorPrint("NumberFormatException in BONUS: " + bString, e);
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
		if (obj instanceof PlayerCharacter)
		{
			if (!passesPreReqTestsForList(preReqList))
			{
				return 0;
			}
		}
		else
		{
			if (!passesPreReqTestsForList((PObject) obj, preReqList))
			{
				return 0;
			}
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

		String bonusTypeString = null;

		final StringTokenizer bTok = new StringTokenizer(aList, ",", false);
		if (aList.equalsIgnoreCase("LIST"))
		{
			bTok.nextToken();
		}
		else if (aList.equalsIgnoreCase("ALL"))
		{
			// aTypePlusName looks like: "SKILL.ALL."
			// so we need to reset it to "SKILL.Hide."
			aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();
			bonus = aBonus;
			bonusTypeString = possibleBonusTypeString;
		}

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
		// The "ALL" subtag is used to build the stacking bonusMap
		// not to get a bonus value, so just return
		if (aList.equals("ALL"))
		{
			return 0;
		}

		return bonus * iTimes;
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

	/**
	 * if a class implements the Cloneable interface then it should have a
	 * public" 'clone ()' method. It should be declared to throw
	 * CloneNotSupportedException', but subclasses do not need the "throws"
	 * declaration unless their 'clone ()' method will throw the exception.
	 * Thus subclasses can decide to not support 'Cloneable' by implementing
	 * the 'clone ()' method to throw 'CloneNotSupportedException'.
	 * If this rule were ignored and the parent did not have the "throws"
	 * declaration then subclasses that should not be cloned would be forced
	 * to implement a trivial 'clone ()' to satisfy inheritance.
	 * final" classes implementing 'Cloneable' should not be declared to
	 * throw 'CloneNotSupportedException" because their implementation of
	 * clone ()' should be a fully functional method that will not
	 * throw the exception.
	 **/
	public Object clone() throws CloneNotSupportedException
	{

		PObject retVal = (PObject) super.clone();
		retVal.setName(name);
		retVal.description = description;
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
		if (tempBonusList != null)
		{
			retVal.tempBonusList = (ArrayList) tempBonusList.clone();
		}
		if (variableList != null)
		{
			retVal.variableList = (VariableList) variableList.clone();
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
		if (bonusMap != null)
		{
			retVal.bonusMap = new HashMap(bonusMap);
		}
		if (autoArray != null)
		{
			retVal.autoArray = (ArrayList) autoArray.clone();
		}
		if (kits != null)
		{
			retVal.kits = (ArrayList) kits.clone();
		}
		if (selectedArmorProfs != null)
		{
			retVal.selectedArmorProfs = (ArrayList) selectedArmorProfs.clone();
		}

		// why isn't this cloned if != null?
		// because the saveList is based on user selections (merton_monk@yahoo.com)
		retVal.saveList = null;     // starts out empty

		//
		// Should these be cloned as well?
		//
		// no, because they won't change,
		// they're loaded from static list files
		//
		retVal.languageAutos = languageAutos;
		retVal.vision = vision;
		//retVal.kitString = kitString;
		retVal.regionString = regionString;
		retVal.moveRatesFlag = moveRatesFlag;
		retVal.movement = movement;
		retVal.movements = movements;
		retVal.movementTypes = movementTypes;
		retVal.movementMult = movementMult;
		retVal.movementMultOp = movementMultOp;

		if (levelAbilityList != null && !levelAbilityList.isEmpty())
		{
			retVal.levelAbilityList = new ArrayList();
			for (Iterator it = levelAbilityList.iterator(); it.hasNext();)
			{
				LevelAbility ab = (LevelAbility) it.next();
				ab = (LevelAbility) ab.clone();
				ab.setOwner(retVal);
				retVal.levelAbilityList.add(ab);
			}
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

		String aString;
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
	public final boolean passesPreReqTestsForList(ArrayList anArrayList)
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
	}

	public final boolean passesPreReqTestsForList(PObject aObj, ArrayList anArrayList)
	{
		return passesPreReqTestsForList(Globals.getCurrentPC(), aObj, anArrayList);
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
	 *   <li> PREBASESIZEEQ
	 *   <li> PREBASESIZEGT
	 *   <li> PREBASESIZEGTEQ
	 *   <li> PREBASESIZELT
	 *   <li> PREBASESIZELTEQ
	 *   <li> PREBASESIZENEQ
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
	 *
	 *   <li> PRESTAT
	 *   <li> PRESTATEQ
	 *   <li> PRESTATGT
	 *   <li> PRESTATGTEQ
	 *   <li> PRESTATLT
	 *   <li> PRESTATLTEQ
	 *   <li> PRESTATNEQ
	 *
	 *   <li> PRESA
	 *
	 *   <li> PRESREQ
	 *   <li> PRESRGT
	 *   <li> PRESRGTEQ
	 *   <li> PRESRLT
	 *   <li> PRESRLTEQ
	 *   <li> PRESRNEQ
	 *
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
		boolean invertFlag; // Invert return value for !PRExxx tags

		ArrayList aFeatList = new ArrayList();
		if (aPC != null)
		{
			aFeatList = aPC.aggregateFeatList();
		}

		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PreReq:", name);
		}

		String aType;
		String aList;
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
				if ("PREAPPLY".equals(aType))
				{
					flag = passesPrereqApplied(aPC, aObj);
				}
				else if ("PREFEAT".equals(aType))
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
					flag = passesPrereqClass(aList, aPC);
				}
				else if ("PRESUBCLASS".equals(aType))
				{
					final StringTokenizer dTok = new StringTokenizer(aList, ",", false);
					final String tok = dTok.nextToken();
					int num = 0;
					try
					{
						num = Integer.parseInt(tok); // number we must match
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PRESUBCLASS attribute: " + tok);
					}
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
					int preLevel;
					try
					{
						preLevel = Integer.parseInt(aList);
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PRELEVEL attribute: " + aList);
//						Assuming a 1 default
						preLevel = 1;
					}
					
					flag = (aPC.getTotalLevels() >= preLevel);
				}
				else if ("PRELEVELMAX".equals(aType))
				{
					int preLevelmax;
					try
					{
						preLevelmax = Integer.parseInt(aList);
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PRELEVELMAX attribute: " + aList);
						preLevelmax = 0;
					}
					flag = (aPC.getTotalLevels() <= preLevelmax);
				}
				else if ("PREHD".equals(aType))
				{
					flag = passesPrereqHD(aList, aPC);
				}
				else if ("PREHP".equals(aType))
				{
					int preHitPoints;
					try
					{
						preHitPoints = Integer.parseInt(aList);
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PREHP attribute: " + aList);
						preHitPoints = 0;
					}	
					flag = (aPC.hitPoints() >= preHitPoints);
					
				}
				else if ("PREFORCEPTS".equals(aType))
				{
					int preFPoints;
					try
					{
						preFPoints = Integer.parseInt(aList);
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PREFORCEPTS attribute: " + aList);
						preFPoints = 0;
					}
					final int myfhold = aPC.getRawFPoints();
					flag = (myfhold >= preFPoints);
				}
				else if ("PREDSIDEPTS".equals(aType))
				{
					int preDPoints;
					try
					{
						preDPoints = Integer.parseInt(aList);
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PREDSIDEPTS/preDPoints attribute: " + aList);
						preDPoints = 0;
					}
					
					int mydhold;
					try
					{
						mydhold = Integer.parseInt(aPC.getDPoints());
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PREDSIDEPTS/mydhold attribute: " + aPC.getDPoints());
						mydhold = 0;
					}
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
					flag = false;
					//int att = (int) aPC.getBonus(0, true);
					//att -= (int) aPC.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
					final int att = aPC.baseAttackBonus();
					try
					{
						final int anInt = Integer.parseInt(aList);
						flag = att >= anInt;
					}
					catch (Exception exc)
					{
						Globals.errorPrint("Badly formed PREATT attribute: " + aList);
					}
				}
				//else if ("PREREPUTATION".equals(aType))
				//{
				//	final int anInt = Integer.parseInt(aList);
				//	String rep = String.valueOf(aPC.reputation());
				//	int repf = Integer.parseInt(rep);
				//	flag = repf >= anInt;
				//}
				//else if ("PREREPUTATIONLTEQ".equals(aType))
				//{
				//	final int anInt = Integer.parseInt(aList);
				//	final String rep = String.valueOf(aPC.reputation());
				//	final int repf = Integer.parseInt(rep);
				//	flag = repf <= anInt;
				//}
				else if ("PREUATT".equals(aType))
				{
					flag = passesPrereqPreUAtt(aList, aPC);
				}
				//else if ("PRESTAT".equals(aType))
				else if (aType.startsWith("PRESTAT"))
				{
					flag = passesPrereqStat(aType.substring(7), aList, aPC);
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
					String tok = dTok.nextToken();
					int num;
					try
					{
						num = Integer.parseInt(tok); // number we must match
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PRECHECK attribute: " + tok);
						num = 0;
					}
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
					try
					{
						flag = doComparison(aType.substring(8), aPC.getRace().getHands(), Integer.parseInt(aList), aPC, "HANDS");
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PREHANDS attribute: " + aList);
					}
				}
				//
				// PRELEGSEQ, PRELEGSGT, PRELEGSGTEQ, PRELEGSLT, PRELEGSLTEQ, PRELEGSNEQ
				//
				else if (aType.startsWith("PRELEGS"))
				{
					try
					{
						flag = doComparison(aType.substring(7), aPC.getRace().getLegs(), Integer.parseInt(aList), aPC, "LEGS");
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PRELEGS attribute: " + aList);
					}
				}
				//
				// PRESIZEEQ, PRESIZEGT, PRESIZEGTEQ, PRESIZELT, PRESIZELTEQ, PRESIZENEQ
				//
				else if (aType.startsWith("PRESIZE"))
				{
					flag = doComparison(aType.substring(7), aPC.sizeInt(), Globals.sizeInt(aList));
				}
				//
				// PRESREQ, PRESRGT, PRESRGTEQ, PRESRLT, PRESRLTEQ, PRESRNEQ
				//
				else if (aType.startsWith("PRESR"))
				{
					try 
					{
						flag = doComparison(aType.substring(5), aPC.calcSR(false), Integer.parseInt(aList));
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PRESR attribute: " + aList);
					}
				}
				//
				// PREBASESIZEEQ, PREBASESIZEGT, PREBASESIZEGTEQ, PREBASESIZELT, PREBASESIZELTEQ, PREBASESIZENEQ
				//
				else if (aType.startsWith("PREBASESIZE"))
				{
					flag = false;
					if ((aPC.getRace() != null) && !aPC.getRace().equals(Globals.s_EMPTYRACE))
					{
						final int iSize = Globals.sizeInt(aList, -1);
						if (iSize < 0)
						{
							Globals.errorPrint("Invalid size '" + aList + "' in PREBASESIZE");
						}
						else
						{
							flag = doComparison(aType.substring(11), aPC.racialSizeInt(), iSize);
						}
					}
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

			final StringTokenizer aTok = new StringTokenizer(bString, "&<>", true);
			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();
				if (aString.equals("<"))
				{
					pString.append("&lt;");
				}
				else if (aString.equals(">"))
				{
					pString.append("&gt;");
				}
				else if (aString.equals("&"))
				{
					pString.append("&amp;");
				}
				else
				{
					pString.append(aString);
				}
			}

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

	public String piString()
	{
		return piString(true);
	}

	// in some cases, we need a PI-formatted string to place within a pre-existing <html> tag
	public String piSubString()
	{
		return piString(false);
	}

	private String piString(boolean useHeader)
	{
		String aString = toString();
		if (SettingsHandler.guiUsesOutputName())
		{
			aString = getOutputName();
		}
		if (nameIsPI)
		{
			final StringBuffer sb = new StringBuffer(aString.length() + 30);
			if (useHeader)
			{
				sb.append("<html>");
			}
			sb.append("<b><i>").append(aString).append("</i></b>");
			if (useHeader)
			{
				sb.append("</html>");
			}
			return sb.toString();
		}
		return aString;
	}

	public String piDescString()
	{
		return piDescString(true);
	}

	// in some cases, we need a PI-formatted string to place within a pre-existing <html> tag
	public String piDescSubString()
	{
		return piDescString(false);
	}

	private String piDescString(boolean useHeader)
	{
		String aString = description;
		if (descIsPI)
		{
			final StringBuffer sb = new StringBuffer(aString.length() + 30);
			if (useHeader)
			{
				sb.append("<html>");
			}
			sb.append("<b><i>").append(aString).append("</i></b>");
			if (useHeader)
			{
				sb.append("</html>");
			}
			return sb.toString();
		}
		return aString;
	}

	public final void setDescription(String a)
	{
		description = a;
	}

	public final String getDescription()
	{
		return description;
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
			{
				selectedList.addAll(associatedList);
			}
			while (aTok.hasMoreTokens())
			{
				tempString = aTok.nextToken();
				final Feat f = Globals.getFeatNamed(tempString);
				if ((f != null) && f.passesPreReqTests())
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

						String weaponList = aPC.getDeity().getFavoredWeapon();
						if ("ALL".equalsIgnoreCase(weaponList) || "ANY".equalsIgnoreCase(weaponList))
						{
							weaponList = Globals.getWeaponProfNames("|", false);
						}

						StringTokenizer bTok = new StringTokenizer(weaponList, "|", false);
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

						boolean bOk = true;
						for (Iterator ti = typeList.iterator(); ti.hasNext();)
						{
							if (!aEq.isType((String) ti.next()))
							{
								bOk = false;
								break;
							}
							if (iSize >= 0)
							{
								bOk &= Globals.sizeInt(aEq.getSize()) == iSize;
							}
							if (bOk)
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
			{
				selectedList.addAll(theObj.getSelectedArmorProfs());
			}
			while (aTok.hasMoreTokens())
			{
				tempString = aTok.nextToken();
				if (tempString.startsWith("TYPE="))
				{
					tempString = tempString.substring(5);
					for (Iterator i = Globals.getEquipmentList().iterator(); i.hasNext();)
					{
						Equipment eq = (Equipment) i.next();
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
					{
						availableList.add(eq.profName());
					}
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
			for (Iterator i = c.getSelectedList().iterator(); i.hasNext();)
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

			String featName;
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

	private boolean passesPrereqApplied(PlayerCharacter aPC, PObject anObj)
	{
		if (anObj == null)
		{
			for (Iterator aB = aPC.getTempBonusList().iterator(); aB.hasNext();)
			{
				BonusObj aBonus = (BonusObj) aB.next();
				Object abT = aBonus.getTargetObject();
				if (abT instanceof PlayerCharacter)
				{
					PlayerCharacter bPC = (PlayerCharacter) abT;
					if (aBonus.isApplied() && (bPC == aPC))
					{
						return true;
					}
				}
			}
			return false;
		}

		for (Iterator aB = anObj.getTempBonusList().iterator(); aB.hasNext();)
		{
			BonusObj aBonus = (BonusObj) aB.next();
			Object abT = aBonus.getTargetObject();
			if (abT instanceof Equipment)
			{
				Equipment aTarget = (Equipment) abT;
				if (aBonus.isApplied() && aTarget.equals(anObj))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean passesPreAlign(String aList, PlayerCharacter aPC)
	{

		boolean flag;
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("PREALIGN");
		}

		//
		// If game mode doesn't support alignment, then pass the prereq
		//
		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			return true;
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
			flag = (("Y".equals(aString) && (aPC.getDeity() != null)) || ("N".equals(aString) && (aPC.getDeity() == null)) || ((aPC.getDeity() != null) && aPC.getDeity().getName().equalsIgnoreCase(aString)));
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
			String aString;
			String tok = aTok.nextToken();
			try
			{
				aString = Globals.getShortAlignmentAtIndex(Integer.parseInt(tok));
			}
			catch (NumberFormatException e)
			{
				Globals.errorPrint("Badly formed PRESR attribute: " + tok);
				aString = "";
			}
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PRESR attribute: " + tok);
			number = 0;
		}
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREDOMAIN attribute: " + tok);
			number = 0;
		}
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREITEM attribute: " + tok);
			number = 0;
		}
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
		String tok = aTok.nextToken();
		flag = false;
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREARMORTYPE attribute: " + tok);
			number = 0;
		}
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
		String tok = aTok.nextToken();
		flag = false;
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREEQUIP attribute: " + tok);
			number = 0;
		}
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREEQUIPPRIMARY attribute: " + tok);
			number = 0;
		}
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
						if (eq.getLocation() == Equipment.EQUIPPED_PRIMARY)
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
							if (eq.getLocation() == Equipment.EQUIPPED_PRIMARY)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.getLocation() == Equipment.EQUIPPED_PRIMARY)
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREEQUIPSECONDARY attribute: " + tok);
			number = 0;
		}
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
						if (eq.getLocation() == Equipment.EQUIPPED_SECONDARY)
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
							if (eq.getLocation() == Equipment.EQUIPPED_SECONDARY)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.getLocation() == Equipment.EQUIPPED_SECONDARY)
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREEQUIPBOTH attribute: " + tok);
			number = 0;
		}
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
						if (eq.getLocation() == Equipment.EQUIPPED_BOTH)
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
							if (eq.getLocation() == Equipment.EQUIPPED_BOTH)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.getLocation() == Equipment.EQUIPPED_BOTH)
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
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PREEQUIPTWOWEAPON attribute: " + tok);
			number = 0;
		}
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
						if (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)
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
							if (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equals(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.getLocation() == Equipment.EQUIPPED_TWO_HANDS)
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
		String tok = aTok.nextToken();
		int storedValue;
		try
		{
			storedValue = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PRELANG attribute: " + tok);
			storedValue = 0;
		}
		number = storedValue;
		while (aTok.hasMoreTokens() && number > 0)
		{

			String aString = aTok.nextToken();
			Language aLang = Globals.getLanguageNamed(aString);
			if (aLang != null)
			{
				if (aPC.getLanguagesList().contains(aLang))
				{
					--number;
				}
			}
			else if (!aString.equals("ANY"))
			{
				Globals.errorPrint("PRELANG: The language " + aString + " does not exist.");
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
				String tok = movereq.nextToken();
				int moveAmount;
				try
				{
					moveAmount = Integer.parseInt(tok);
				}
				catch (NumberFormatException e)
				{
					Globals.errorPrint("Badly formed Premove attribute: " + tok);
					moveAmount = 0;
				}

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
								String e1String;
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

		int number = 0;
		final boolean flag;

		// e.g. PRESPELL:3,
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PreSpell attribute: " + tok);
		}

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
		PCClass aClass;
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

		int number;
		final boolean flag;

		// e.g. PRESPELLSCHOOL:Divination,7,1
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final String school = aTok.nextToken();
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PreSpellSchool attribute: " + tok);
			number = 0;
		}

		tok = aTok.nextToken();
		int minlevel;
		try
		{
			minlevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PreSpellSchool attribute: " + tok);
			minlevel = 1;
		}
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

		int number;
		final boolean flag;

		// e.g. PRESPELLSCHOOLSUB:Shadow,7,1
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final String subSchool = aTok.nextToken();
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PreSpellSchoolSub attribute: " + tok);
			number = 0;
		}

		tok = aTok.nextToken();
		int minlevel;
		try
		{
			minlevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed PreSpellSchoolSub attribute: " + tok);
			minlevel = 0;
		}
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
	private static boolean passesPrereqClass(String aList, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		String aString;
		int number = 1;
		if (aTok.countTokens() > 1)
		{
			aString = aTok.nextToken();
			
			try
			{
				number = Integer.parseInt(aString);
			}
			catch (Exception exc)
			{
				Globals.errorPrint("Badly formed passesPrereqClass attribute: " + aString);
			}
		}

		aString = aTok.nextToken();

		ArrayList classesToTest = new ArrayList();
		for (; ;)
		{
			int i = aString.indexOf('=');
			if (i >= 0)
			{
				classesToTest.add(aString.substring(0, i).toUpperCase());
				
				int preClass;
				try
				{
					preClass = Integer.parseInt(aString.substring(i + 1));
				}
				catch (NumberFormatException e)
				{
					Globals.errorPrint("Badly formed passesPrereqClass attribute: " + aString.substring(i + 1));
					preClass = 0;
				}

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
						if ((aClass != null) && (aClass.getLevel() >= preClass))
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
		int preClass;
		try
		{
			preClass = Integer.parseInt(aList.substring(i + 1));
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed passesPrereqClassLevelMax attribute: " + aList.substring(i + 1));
			preClass = 0;
		}
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
				if (aClass != null && aClass.getLevel() <= preClass)
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
		String tok = aTok.nextToken();
		int preHDMin;
		try
		{
			preHDMin = (aTok.hasMoreTokens()) ? Integer.parseInt(tok) : Integer.MIN_VALUE;
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed passesPrereqClass/preHDMin attribute: " + tok);
			preHDMin = 1;
		}
		
		tok = aTok.nextToken();
		int preHDMax;
		try
		{
			preHDMax = (aTok.hasMoreTokens()) ? Integer.parseInt(tok) : Integer.MAX_VALUE;
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed passesPrereqClass/preHDMax attribute: " + tok);
			preHDMax = 1;
		}

		final int hitDice = aPC.totalHitDice();
		flag = (hitDice >= preHDMin) && (hitDice <= preHDMax);
		return flag;
	}

	private static boolean passesPrereqPreUAtt(String aList, PlayerCharacter aPC)
	{

		final boolean flag;
		int requiredValue;
		try
		{
			requiredValue = Integer.parseInt(aList);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed passesPrereqPreUAtt attribute: " + aList);
			requiredValue = 0;
		}
		int att = 0;
		if (!aPC.getClassList().isEmpty())
		{
			for (Iterator e2 = aPC.getClassList().iterator(); e2.hasNext();)
			{

				final PCClass aClass = (PCClass) e2.next();
				String s = aClass.getUattForLevel(aClass.getLevel());
				if (s.length() == 0 || "0".equals(s))
				{
					att = Math.max(att, aClass.baseAttackBonus());
				}
				else
				{

					final StringTokenizer bTok = new StringTokenizer(s, ",", false);
					s = bTok.nextToken();
					try
					{
						att = Math.max(att, Integer.parseInt(s));
					}
					catch (NumberFormatException e)
					{
						Globals.errorPrint("Badly formed passesPrereqPreUAtt attribute: " + s);
					}
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
		int ranks;
		if (i >= 0)
		{
			try
			{
				ranks = Integer.parseInt(aList.substring(i + 1));
			}
			catch (NumberFormatException e)
			{
				Globals.errorPrint("Badly formed passesPrereqSkill attribute: " + aList.substring(i + 1));
				ranks = 0;
			}
		}
		else
		{
			Globals.errorPrint("passesPrereqSkill: bad prereq \"" + aList + "\"");
			return false;
		}
		final StringTokenizer aTok = new StringTokenizer(aList.substring(0, i), ",", false);

		// the number of skills which must match
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (Exception exc)
		{
			Globals.errorPrint("Badly formed passesPrereqSkill/number of skills attribute: " + tok);
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

			String skillName;
			String subName;
			final boolean isType = aString.startsWith("TYPE.");
			if (isType)
			{
				aString = aString.substring(5).toUpperCase();
			}
			int j;
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
		int ranks;
		try
		{
			ranks = Integer.parseInt(aList.substring(i + 1));
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed passesPrereqSkillTot attribute: " + aList.substring(i + 1));
			ranks = 0;
		}
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

			String skillName;
			String subName;
			boolean isType = aString.startsWith("TYPE.");
			int j;
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
			Skill aSkill;
			for (int si = 0; si < sList.size(); ++si)
			{
				if ((foundIt && !isType && (j < 0)) || (ranks <= 0))
				{
					break;
				}
				aSkill = (Skill) sList.get(si);
				if (!isType && (aSkill.getName().equals(skillName) || aSkill.getName().equals(aString) || (j >= 0 && aSkill.getName().startsWith(aString.substring(0, j)))))
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

	/**
	 * Method checks to see if the current character meets the
	 * prerequisite number of spells at the required level and also
	 * checks to see if the character can actually cast it.
	 *
	 * PRESPELLTYPE:<type>{|<type2>},<number>,<min level>
	 *
	 * If two types are passes in then only one type is needed
	 * to return as true.
	 *
	 * @param aList
	 * @param aPC
	 * @return boolean
	 */
	private boolean passesPrereqSpellType(String aList, PlayerCharacter aPC)
	{
		final StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		final String typeList = aTok.nextToken();
		if (aTok.countTokens() == 2)
		{
			String tok = aTok.nextToken();
			int number;
			try
			{
				number = Integer.parseInt(tok);
			}
			catch (NumberFormatException e)
			{
				Globals.errorPrint("Badly formed passesPrereqSpellType/number attribute: " + tok);
				number = 0;
			}
			tok = aTok.nextToken();
			int minlevel;
			try
			{
				minlevel = Integer.parseInt(tok);
			}
			catch (NumberFormatException e)
			{
				Globals.errorPrint("Badly formed passesPrereqSpellType/minLevel attribute: " + tok);
				minlevel = 0;
			}

			final StringTokenizer bTok = new StringTokenizer(typeList, "|", false);
			ArrayList aArrayList;

			//Go through types
			while (bTok.hasMoreTokens())
			{
				final String castingType = bTok.nextToken();

				//Perform a quick check to see if the character even has the required
				//	spell level in their spell book
				aArrayList = aPC.aggregateSpellList(castingType, "", "", minlevel, minlevel);
				if (aArrayList.size() >= number)
				{
					// Make sure character can actually cast spells of this level
					if (aPC.canCastSpellTypeLevel(castingType, minlevel, number))
					{
						return true;
					}
					//Else let the while loop goto the next spell type
				}
			}
		}
		else
		{
			Globals.debugPrint(getName(), ":badly formed PRESPELLTYPE: " + aList);
		}
		return false;
	}

	//
	// PRESTAT  (same as PRESTATGTEQ for backwards compatibility)
	// PRESTATEQ
	// PRESTATGT
	// PRESTATGTEQ
	// PRESTATLT
	// PRESTATLTEQ
	// PRESTATNEQ
	//
	private final boolean passesPrereqStat(String compType, final String aList, final PlayerCharacter aPC)
	{
		StringTokenizer aTok = new StringTokenizer(aList, ",", false);
		String myTok = aTok.nextToken();
		int matchesNeeded = 1;

		try
		{
			matchesNeeded = Integer.parseInt(myTok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed passesPrereqStat attribute: " + myTok);
		}
		
		myTok = aTok.nextToken();

		if (compType.length() == 0)
		{
			compType = "GTEQ";
		}

		//
		// Allow both comma and pipe as delimeters for now.
		// NOTE: pipe-delimited PRExxx break BONUS prerequisites
		//
		aTok = new StringTokenizer(myTok, ",|", false);
		while (aTok.hasMoreTokens())
		{
			myTok = aTok.nextToken();
			try
			{
				final int iStat = aPC.getStatList().getTotalStatFor(myTok.substring(0, 3));
				final int iVal = Integer.parseInt(myTok.substring(myTok.lastIndexOf('=') + 1));
				if (doComparison(compType, iStat, iVal))
				{
					if (--matchesNeeded <= 0)
					{
						return true;
					}
				}
			}
			catch (Exception e)
			{
				Globals.errorPrint("Badly formed PRESTAT token: " + e);
			}
		}
		return false;
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

		StringTokenizer aTok;
		String aType;
		String aList;
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
			//else if ("PRESTAT".equals(aType))
			else if (aType.startsWith("PRESTAT"))
			{
				String comp = aType.substring(7);
				if (comp.length() == 0)
				{
					comp = "GTEQ";
				}
				switch (getComparisonType(comp))
				{
					case COMPARETYPE_UNKNOWN:
					default:
						comp = "??";
						break;

					case COMPARETYPE_EQ:
						comp = "=";
						break;

					case COMPARETYPE_LT:
						comp = "<";
						break;

					case COMPARETYPE_LTEQ:
						comp = "<=";
						break;

					case COMPARETYPE_GT:
						comp = ">";
						break;

					case COMPARETYPE_GTEQ:
						comp = ">=";
						break;

					case COMPARETYPE_NEQ:
						comp = "!=";
						break;
				}
				pString.append("Stat:");
				aTok = new StringTokenizer(aList, "=", true);
				while (aTok.hasMoreTokens())
				{
					final String t = aTok.nextToken();
					if (t.equals("="))
					{
						pString.append(comp);
					}
					else
					{
						pString.append(t);
					}
				}
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

					String tok = aTok.nextToken();
					int raceNumber;
					try
					{
						raceNumber = Integer.parseInt(tok);
					}
					catch (NumberFormatException nfe)
					{
						Globals.errorPrint("Badly formed PREDEITYALIGN attribute: " + tok);
						raceNumber = -1;
					}

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
							try
							{
								alignNumber = Integer.parseInt(preAlign.substring(idx + 1, preAlign.length() - 1));
							}
							catch (NumberFormatException nfe)
							{
								Globals.errorPrint("Badly formed PREALIGN/alignNumber attribute: " + preAlign.substring(idx + 1, preAlign.length() - 1));
								alignNumber = -1;
							}

						}
						else
						{
							try
							{
								alignNumber = Integer.parseInt(preAlign);
							}
							catch (NumberFormatException nfe)
							{
								Globals.errorPrint("Badly formed PREALIGN/alignNumber attribute: " + preAlign);
								alignNumber = -1;
							}

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

		int index = -1;
		if (getBonusList() != null)
		{
			index = getBonusList().indexOf(bonusString);
		}
		if (index >= 0)
		{
			getBonusList().remove(index);
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

	/**
	 * Made public on 10 Dec 2002 by sage_sam for pcgen.core.spell.Spell
	 * to fix warning on improper override
	 */
	public String getPCCText()
	{
		return getPCCText(true);
	}

	protected String getPCCText(boolean saveName)
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

		if (outputName != null
			&& outputName.length() > 0
			&& !outputName.equals(getName()))
		{
			txt.append("\tOUTPUTNAME:").append(outputName);
		}

		aString = getDescription();
		if (aString.length() != 0)
		{
			txt.append("\tDESC:").append(pcgen.io.EntityEncoder.encode(aString));
			if (getDescIsPI())
			{
				txt.append("\tDESCISPI:Yes");
			}
		}
		if (!getName().equals(getKeyName()))
		{
			txt.append("\tKEY:").append(getKeyName());
		}

		if ((autoArray != null) && (autoArray.size() != 0))
		{
			for (e = autoArray.iterator(); e.hasNext();)
			{
				txt.append("\tAUTO:").append(e.next().toString());
			}
		}

		if (!(this instanceof PCClass) && getBonusList().size() != 0)
		{
			for (e = getBonusList().iterator(); e.hasNext();)
			{
				txt.append("\tBONUS:").append(e.next().toString());
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

		aString = getChoiceString();
		if (aString.length() != 0)
		{
			txt.append("\tCHOOSE:").append(aString);
		}

		int iCount = getVariableCount();
		if (!(this instanceof PCClass) && iCount != 0)
		{
			for (int i = 0; i < iCount; ++i)
			{
				aString = getVariableDefinition(i);
				if (aString.startsWith("-9|"))
				{
					aString = aString.substring(3);
				}
				txt.append("\tDEFINE:").append(aString);
			}
		}

		if (!(this instanceof PCClass) && (DR != null) && (DR.length() != 0))
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

		if (!(this instanceof PCClass) && (specialAbilityList != null) && (specialAbilityList.size() != 0))
		{
			for (e = specialAbilityList.iterator(); e.hasNext();)
			{
				final SpecialAbility sa = (SpecialAbility) e.next();
				txt.append("\tSA:").append(sa.toString());
			}
		}

		aString = getQualifyString();
		if (!aString.equals("alwaysValid"))
		{
			txt.append("\tQUALIFY:").append(aString);
		}

		if (!(this instanceof PCClass))
		{
			for (iCount = 0; ; ++iCount)
			{
				aString = getSpellListItemAsString(iCount);
				if (aString == null)
				{
					break;
				}
				txt.append("\tSPELL:").append(aString);
			}
		}

		if (!(this instanceof PCClass) && (SR != null) && (SR.length() != 0))
		{
			txt.append("\tSR:").append(SR);
		}

		if ((vision != null) && (vision.size() != 0))
		{
			StringBuffer sb = new StringBuffer();
			for (e = vision.keySet().iterator(); e.hasNext();)
			{
				String key = (String) e.next();
				String val = (String) vision.get(key);
				if (val.length() > 0 && !"0".equals(val))
				{
					if (sb.length() > 0)
					{
						sb.append('|');
					}
					sb.append(key).append(" (");
					sb.append(val).append("')");
				}
			}
			if (sb.length() > 0)
			{
				txt.append("\tVISION:").append(sb.toString());
			}
		}

		if ((weaponProfAutos != null) && (weaponProfAutos.size() != 0))
		{
			txt.append("\tWEAPONAUTO:").append(Utility.unSplit(weaponProfAutos, "|"));
		}

		if (getMyTypeCount() != 0)
		{
			txt.append('\t').append(Constants.s_TAG_TYPE).append(getType());
		}

		aString = getSourcePage();
		if (aString.length() != 0)
		{
			txt.append("\tSOURCEPAGE:").append(aString);
		}

		if (regionString != null && regionString.startsWith("0|"))
		{
			txt.append("\tREGION:").append(regionString.substring(2));
		}

//		if (kitString != null && kitString.startsWith("0|"))
//		{
//			txt.append("\tKIT:").append(kitString.substring(2));
//		}
		if (kits != null)
		{
			for (int iKit = 0; iKit < kits.size(); ++iKit)
			{
				aString = (String) kits.get(iKit);
				if (aString.startsWith("0|"))
				{
					txt.append("\tKIT:").append(aString.substring(2));
				}
			}
		}

		return txt.toString();
	}

	// should be in the form of #|KIT1|KIT2|KIT3|etc
	public final void setKitString(final String arg)
	{
		//kitString = arg;
		if (arg.equals(".CLEAR"))
		{
			kits = null;
		}
		else
		{
			if (kits == null)
			{
				kits = new ArrayList();
			}
			kits.add(arg);
		}
	}

	public final String getKitString(final int idx)
	{
//		return kitString;
		if ((kits == null) || (idx < 0) || (idx >= kits.size()))
		{
			return null;
		}
		return (String) kits.get(idx);
	}

	public final void makeKitSelection()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		aPC.setArmorProfListStable(false);
		makeKitSelection(0);
	}

	final void makeKitSelection(int arg)
	{
		//if (kitString == null)
		String kitString;
		for (int iKit = 0; ; ++iKit)
		{
			kitString = getKitString(iKit);
			if (kitString == null)
			{
				break;
			}
			makeKitSelections(arg, kitString, iKit);
		}
	}

	private final void makeKitSelections(final int arg, final String kitString, final int iKit)
	{
		final StringTokenizer aTok = new StringTokenizer(kitString, "|", false);
		// first element is prelevel - should be 0 for everything but PCClass entries
		String tok = aTok.nextToken();
		int aLevel;
		try
		{
			aLevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed kitString: " + tok);
			aLevel = 0;
		}

		if (aLevel > arg)
		{
			return;
		}
		tok = aTok.nextToken();
		int num;
		try
		{
			num = Integer.parseInt(tok); // number of kit selections
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed number of kit selections attribute: " + tok);
			num = 0;	
		}

		ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
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
				if (theKit == null || (aPC.getKitInfo() != null && aPC.getKitInfo().indexOf(theKit) >= 0))
				{
					continue;
				}
				ArrayList thingsToAdd = new ArrayList();
				ArrayList warnings = new ArrayList();
				theKit.addKitFeats(thingsToAdd, warnings);
				theKit.addKitProfs(thingsToAdd, warnings);
				theKit.addKitGear(thingsToAdd, warnings);
				theKit.addKitSpells(thingsToAdd, warnings);
				theKit.addKitSkills(thingsToAdd, warnings);
				theKit.processKit(thingsToAdd, iKit);
			}
		}
	}

	public final void setRegionString(final String arg)
	{
		regionString = arg;
	}

	public final String getRegionString()
	{
		return regionString;
	}

	final void makeRegionSelection()
	{
		makeRegionSelection(0);
	}

	final void makeRegionSelection(int arg)
	{
		if (regionString == null)
		{
			return;
		}
		final StringTokenizer aTok = new StringTokenizer(regionString, "|", false);
		// first element is prelevel - should be 0 for everything but PCClass entries
		String tok = aTok.nextToken();
		int aLevel;
		try
		{
			aLevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed preLevel attribute in makeRegionSelection: " + tok);
			aLevel = 0;
		}

		if (aLevel > arg)
		{
			return;
		}
		tok = aTok.nextToken();
		int num;
		try
		{
			num = Integer.parseInt(tok); // number of selections
		}
		catch (NumberFormatException e)
		{
			Globals.errorPrint("Badly formed number of selection attribute in makeRegionSelection: " + tok);
			num = -1;	
		}

		ArrayList aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			aList.add(aTok.nextToken());
		}
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
				{
					continue;
				}
				aPC.setRegion(aString);
			}
		}
	}

	public final void clearAutoList()
	{
		autoArray = null;
	}

	public final void addAutoArray(final ArrayList aList)
	{
		if (autoArray == null)
		{
			autoArray = new ArrayList();
		}
		autoArray.addAll(aList);
	}

	public final void addAutoArray(final String arg)
	{
		if (arg == null)
		{
			if (autoArray != null)
			{
				autoArray.clear();
			}
			return;
		}
		if (autoArray == null)
		{
			autoArray = new ArrayList();
		}
		autoArray.add(arg);
	}

	public final void addSelectedArmorProfs(ArrayList aList)
	{
		if (selectedArmorProfs == null)
		{
			selectedArmorProfs = new ArrayList();
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();
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

	public final ArrayList getLevelAbilityList()
	{
		return levelAbilityList;
	}

	public LevelAbility addAddList(String aList)
	{
		return addAddList(0, aList);
	}

	public LevelAbility addAddList(final int aLevel, final String aList)
	{
		if (levelAbilityList == null)
		{
			levelAbilityList = new ArrayList();
		}
		if (aList.startsWith(".CLEAR"))
		{
			if (".CLEAR".equals(aList))
			{
				levelAbilityList.clear();
			}
			else if (aList.indexOf(".LEVEL") >= 0)
			{
				int level;
				try
				{
					level = Integer.parseInt(aList.substring(12));
				}
				catch (NumberFormatException e)
				{
					Globals.errorPrint("Badly formed addAddList attribute: " + aList.substring(12));
					level = -1;
				}

				if (level >= 0)
				{
					for (int x = levelAbilityList.size() - 1; x >= 0; --x)
					{
						final LevelAbility ability = (LevelAbility) levelAbilityList.get(x);
						if (ability.level() == level)
						{
							levelAbilityList.remove(x);
						}
					}
				}
			}
		}
		else
		{
			LevelAbility la = LevelAbility.createAbility(this, aLevel, aList);
			levelAbilityList.add(la);
			return la;
		}
		return null;
	}

	protected void addAddsForLevel(final int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || levelAbilityList == null || levelAbilityList.isEmpty() || aPC.isImporting())
		{
			return;
		}
		LevelAbility ability;
		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			ability = (LevelAbility) e.next();
			ability.setOwner(this);
			if (!(this instanceof PCClass) || (ability.level() == aLevel && ability.canProcess()))
			{
				boolean canProcess = true;
				if ((ability instanceof LevelAbilityFeat) && !SettingsHandler.getShowFeatDialogAtLevelUp())
				{
					//
					// Check the list of feats for at least one that is hidden or for output only
					// Show the popup if there is one
					//
					canProcess = false;
					ArrayList featList = new ArrayList();
					ability.process(featList);
					for (Iterator fe = featList.iterator(); fe.hasNext();)
					{
						final Feat aFeat = Globals.getFeatNamed((String) fe.next());
						if (aFeat != null)
						{
							switch (aFeat.isVisible())
							{
								case Feat.VISIBILITY_HIDDEN:
								case Feat.VISIBILITY_OUTPUT_ONLY:
									canProcess = true;
									break;

								default:
									continue;
							}
							break;
						}
					}

				}

				if (canProcess)
				{
					ability.process();
				}
				else
				{
					aPC.setFeats(aPC.getFeats() + 1);	// need to add 1 feat to total available
				}
			}
		}
	}

	protected void globalChecks()
	{
		globalChecks(false);
	}

	protected void globalChecks(boolean flag)
	{
		makeKitSelection();
		makeRegionSelection();
		if (flag)
		{
			makeChoices();
		}
		if (this instanceof PCClass)
		{
			addAddsForLevel(((PCClass) this).level);
		}
		else
		{
			addAddsForLevel(0);
		}
	}

	protected void subAddsForLevel(final int aLevel)
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null || levelAbilityList == null || levelAbilityList.isEmpty())
		{
			return;
		}
		LevelAbility ability;
		for (Iterator e = levelAbilityList.iterator(); e.hasNext();)
		{
			ability = (LevelAbility) e.next();
			if (ability.level() == aLevel)
			{
				ability.subForLevel();
			}
		}
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

		switch (getComparisonType(comparison))
		{
			case COMPARETYPE_EQ:
				return iVal1 == iVal2;

			case COMPARETYPE_LT:
				return iVal1 < iVal2;

			case COMPARETYPE_LTEQ:
				return iVal1 <= iVal2;

			case COMPARETYPE_GT:
				return iVal1 > iVal2;

			case COMPARETYPE_GTEQ:
				return iVal1 >= iVal2;

			case COMPARETYPE_NEQ:
				return iVal1 != iVal2;

			default:
				break;
		}
		Globals.debugPrint("Prereq failed, unknown comparison: " + comparison);
		return false;
	}

	private static final int getComparisonType(final String aString)
	{
		if ("EQ".equals(aString))
		{
			return COMPARETYPE_EQ;
		}
		else if ("LT".equals(aString))
		{
			return COMPARETYPE_LT;
		}
		else if ("LTEQ".equals(aString))
		{
			return COMPARETYPE_LTEQ;
		}
		else if ("GT".equals(aString))
		{
			return COMPARETYPE_GT;
		}
		else if ("GTEQ".equals(aString))
		{
			return COMPARETYPE_GTEQ;
		}
		else if ("NEQ".equals(aString))
		{
			return COMPARETYPE_NEQ;
		}
		return COMPARETYPE_UNKNOWN;
	}

	public String getSpellListItemAsString(final int idx)
	{
		final ArrayList sList = getSpellList();
		if ((sList == null) || (idx >= sList.size()))
		{
			return null;
		}

		String aString = sList.get(idx).toString();
		final StringTokenizer aTok = new StringTokenizer(aString, "|", false);
		if (aTok.countTokens() == 4)
		{
			aString = aTok.nextToken() + "|" + aTok.nextToken() + "|" + aTok.nextToken() + "[" + aTok.nextToken() + "]";
		}
		return aString;
	}

	/**
	 * gets the bonuses to a stat based on the stat Index
	 **/
	public int getStatMod(int statIdx)
	{
		final ArrayList statList = Globals.getStatList();
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return 0;
		}

		final String aStat = ((PCStat) statList.get(statIdx)).getAbb();
		return (int) bonusTo("STAT", aStat);
	}


////////////////////
// Please leave in - Byngl
	private Object addBonus(String aString)
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
						aBonus = new ClassBonus(level, aBonus);
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
				aBonus = Bonus.newBonus(aString);
			}
		}
		if (aBonus != null)
		{
			if (!aString.equalsIgnoreCase(aBonus.toString()))
			{
				Globals.errorPrint("Name: " + getName() + "\nSource:" + getSource() + "\n Warning, parsed BONUS differs\n LST  =" + aString + "\n PCGen=" + aBonus.toString());
			}
		}
		else
		{
			Globals.errorPrint("Name: " + getName() + "\nSource:" + getSource() + "\n Error in BONUS: " + aString);
		}
		return aBonus;
	}

	public void setSourceCampaign(Campaign arg)
	{
		sourceCampaign = arg;
	}

	public Campaign getSourceCampaign()
	{
		if (this instanceof Campaign)
		{
			return (Campaign) this;
		}
		return sourceCampaign;
	}

}

