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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
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
 * Last Edited: $Date: 2006/02/21 01:33:15 $
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.core.utils.EmptyIterator;
import pcgen.core.utils.Utility;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserInterface;
import pcgen.gui.utils.GuiFacade;
import pcgen.io.PCGIOHandler;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * <code>PObject</code><br>
 * This is the base class for several objects in the PCGen database.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class PObject implements Cloneable, Serializable, Comparable
{
	private static final long serialVersionUID = 1;


//	private static final List emptyBonusList = new ArrayList();
	protected String DR = null; // a string so that we can handle formulas
	protected String SR = null; // a string so that we can handle formulas
	protected ArrayList associatedList = null;
	protected ArrayList bonusList = new ArrayList();
	private ArrayList tempBonusList = new ArrayList();
	protected String choiceString = "";
	protected boolean isSpecified = false;
	protected String keyName = "";
	protected String name = "";
	private String description = "";
	private String tempDesc = "";
	protected String outputName = "";
	private ArrayList specialAbilityList = null;
	private HashMap spellMap;
	protected HashMap spellInfoMap = null, spellLevelMap = null, preReqSpellLevelMap = null;
	protected ArrayList udamList = null;
	protected ArrayList umultList = null;
	protected boolean visible = true;
	protected Map vision = null;
	protected ArrayList weaponProfAutos = null;
	private HashMap changeProfMap = new HashMap();

	protected ArrayList autoArray = null; // AUTO: tag
	private int baseQuantity = 1;
	private HashMap bonusMap = null;
	protected ArrayList cSkillList = null;
	protected ArrayList ccSkillList = null;
	protected String classSkillString = null;
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
	private ArrayList selectedShieldProfs = null;
	private ArrayList selectedWeaponProfBonus = null;
	private String source = "";
	String sourceFile = null;
	private String sourcePage = "";
	private VariableList variableList = null; //new VariableList();
	protected ArrayList levelAbilityList = null;
	protected Campaign sourceCampaign = null;
	protected HashMap sourceMap = new HashMap();
	// virtual feat list
	private List virtualFeatList = new LinkedList();
	// natural weapons list
	private List naturalWeapons = new ArrayList();
	// movement related variables
	private Integer movement;
	private Integer[] movements;
	private String[] movementTypes;
	private Integer[] movementMult;
	private String[] movementMultOp;
	private int moveRatesFlag;
	private int encumberedLoadMoveInt = Constants.LIGHT_LOAD;
	private int encumberedArmorMoveInt = Constants.LIGHT_LOAD;

	/**
	 * return an ArrayList of CharacterSpell with following criteria:
	 * Spell aSpell	 ignored if null
	 * book		 ignored if ""
	 * level	 ignored if < 0
	 * fList	 (ignored if null) Array of Feats
	 */
	public final List getCharacterSpell(Spell aSpell, String book, int level)
	{
		return getCharacterSpell(aSpell, book, level, null);
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

	public List getCharacterSpellList()
	{
		return characterSpellList;
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
	 * add to list of Virtual Feats
	 *
	 * @param aString a | delimited list of feat names
	 */
	public void addVirtualFeat(String aString)
	{
		addVirtualFeat(aString, virtualFeatList);
	}

	public void addVirtualFeat(String aString, List addList)
	{
		// Must be of the form:
		//   Feat1|Feat2|PRExx:abx
		// or
		//   Feat1|Feat2|PREMULT:[PRExxx:abc],[PRExxx:xyz]

		String preString = "";
		List aList = new LinkedList();

		StringTokenizer aTok = new StringTokenizer(aString, "|");
		while (aTok.hasMoreTokens())
		{
			String aPart = aTok.nextToken();
			if (aPart.length() <= 0)
			{
				continue;
			}
			if ((aPart.startsWith("PRE") || aPart.startsWith("!PRE")) && (aPart.indexOf(":") > 0))
			{
				// We have a PRExxx tag!
				preString = aPart;
			}
			else
			{
				// We have a feat name
				Feat aFeat = Globals.getFeatNamed(aPart);
				if (aFeat != null)
				{
					aFeat = (Feat) aFeat.clone();
					aFeat.setFeatType(Feat.FEAT_VIRTUAL);
					//
					// Check for crazy things like:
					//   Weapon Finesse (Claw, Bite)
					// Which means add the Weapon Finesse
					// feat and apply to Claw and Bite
					//
					if (!aFeat.getName().equalsIgnoreCase(aPart))
					{
						final int i = aPart.indexOf('(');
						final int j = aPart.indexOf(')');
						if ((i >= 0) && (j >= 0))
						{
							final StringTokenizer bTok = new StringTokenizer(aPart.substring(i + 1, j), ",");
							while (bTok.hasMoreTokens())
							{
								final String a = bTok.nextToken();
								if (!aFeat.containsAssociated(a))
								{
									aFeat.addAssociated(a);
								}
							}
						}
					}
					aList.add(aFeat);
				}
			}
		}
		if ((preString.length() > 0) && !aList.isEmpty())
		{
			for (Iterator e = aList.iterator(); e.hasNext();)
			{
				Feat aFeat = (Feat) e.next();
				aFeat.addPreReq(preString);
			}
		}
		addList.addAll(aList);
	}

	/**
	 * the list of Virtual Feats
	 *
	 * @return List a list of Feat objects
	 */
	public List getVirtualFeatList()
	{
		return virtualFeatList;
	}

	/**
	 * return Set of Strings of Language names
	 */
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
	 */
	public final Set getAutoLanguages()
	{
		final Set aSet = new TreeSet();
		if (languageAutos == null || languageAutos.isEmpty())
		{
			return aSet;
		}
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
			Logging.errorPrint("Badly formed BaseQty string: " + aString);
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

	public List getBonusList()
	{
		return bonusList;
	}

	public List getBonusListOfType(String aType, String aName)
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if (!aBonus.getTypeOfBonus().startsWith(aType))
			{
				continue;
			}
			if (aBonus.getBonusInfoList().size() > 1)
			{
				StringTokenizer aTok = new StringTokenizer(aBonus.getBonusInfo(), ",");
				while (aTok.hasMoreTokens())
				{
					String aBI = aTok.nextToken();
					if (aBI.equals(aName))
					{
						aList.add(aBonus);
					}
				}
			}
			else if (aBonus.getBonusInfo().equals(aName))
			{
				aList.add(aBonus);
			}
		}
		return aList;
	}

	public void removeBonusList(BonusObj aBonus)
	{
		getBonusList().remove(aBonus);
	}

	public final void addBonusList(final String aString)
	{
		addBonusList(aString, this);
	}

	public final void addBonusList(final String aString, Object obj)
	{
		if (bonusList == null)
		{
			bonusList = new ArrayList();
		}

		BonusObj aBonus = Bonus.newBonus(aString);
		if (aBonus != null)
		{
			aBonus.setCreatorObject(obj);
			bonusList.add(aBonus);
		}
	}

	/**
	 * This function will be required during the continued re-write
	 * of the BonusObj code -- JSC 8/18/03
	 *
	 * @param aBonus
	 */
	public void addBonusList(BonusObj aBonus)
	{
		bonusList.add(aBonus);
	}

	public boolean getBonusListString(String aString)
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if (aBonus.getBonusInfo().equalsIgnoreCase(aString))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * returns all BonusObj's that are "active"
	 */
	public List getActiveBonuses()
	{
		List aList = new LinkedList();
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			if (aBonus.isApplied())
			{
				aList.add(aBonus);
			}
		}
		return aList;
	}

	/**
	 * Sets all the BonusObj's to "active"
	 */
	public void activateBonuses()
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setApplied(false);
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

	public void deactivateBonuses()
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setApplied(false);
		}
	}

	/**
	 * Set's all the BonusObj's to this creator
	 */
	public void ownBonuses()
	{
		for (Iterator ab = getBonusList().iterator(); ab.hasNext();)
		{
			BonusObj aBonus = (BonusObj) ab.next();
			aBonus.setCreatorObject(this);
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
	 * @param bonus     a Number (such as 2)
	 * @param bonusType "COMBAT.AC.Dodge" or "COMBAT.AC.Dodge.STACK"
	 */
	final void setBonusStackFor(double bonus, String bonusType)
	{
		if (bonusType != null)
		{
			bonusType = bonusType.toUpperCase();
		}

		// Default to non-stacking bonuses
		int index = -1;

		final StringTokenizer aTok = new StringTokenizer(bonusType, ".");

		// e.g. "COMBAT.AC.DODGE"
		if ((bonusType != null) && (aTok.countTokens() >= 2))
		{
			String aString;
			// we need to get the 3rd token to see
			// if it should .STACK or .REPLACE
			aTok.nextToken(); //Discard token
			aString = aTok.nextToken();
			// if the 3rd token is "BASE" we have something like
			// CHECKS.BASE.Fortitude
			if (aString.equals("BASE"))
			{
				if (aTok.hasMoreTokens())
				{
					// discard next token (Fortitude)
					aTok.nextToken();
				}
				if (aTok.hasMoreTokens())
				{
					// check for a TYPE
					aString = aTok.nextToken();
				}
				else
				{
					// all BASE type bonuses should stack
					aString = null;
				}
			}
			else
			{
				if (aTok.hasMoreTokens())
				{
					// Type: .DODGE
					aString = aTok.nextToken();
				}
				else
				{
					aString = null;
				}
			}

			if (aString != null)
			{
				index = SystemCollections.getUnmodifiableBonusStackList().indexOf(aString); // e.g. Dodge
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
		if ((bonusType != null) && (bonusType.endsWith(".STACK") || bonusType.endsWith(".REPLACE")))
		{
			index = 1;
		}

		// If it's a negative bonus, it always needs to be added
		if (bonus < 0)
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
	 * Movement related accessors and mutators
	 * #############################################
	 */

	public void setMovementTypes(String[] arrayString)
	{
		movementTypes = arrayString;
	}

	/**
	 * ONLY to be used for cloning
	 *
	 * @return the array
	 */
	protected final String[] getMovementTypes()
	{
		return movementTypes;
	}

	/**
	 * returns an empty string if there are no movement types defined
	 */
	public String getMovementType(int i)
	{
		if ((movementTypes != null) && (i < movementTypes.length))
		{
			return movementTypes[i];
		}
		return "";
	}

	public int getNumberOfMovementTypes()
	{
		return movementTypes != null ? movementTypes.length : 0;
	}

	/**
	 * ONLY to be used for cloning
	 *
	 * @return the array
	 */
	protected final Integer[] getMovements()
	{
		return movements;
	}

	public int getNumberOfMovements()
	{
		return movements != null ? movements.length : 0;
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

	/**
	 * ONLY to be used for cloning
	 *
	 * @return the array
	 */
	protected final Integer[] getMovementMult()
	{
		return movementMult;
	}

	public final Integer getMovementMult(int index)
	{
		return movementMult[index];
	}

	/**
	 * ONLY to be used for cloning
	 *
	 * @return the array
	 */
	protected final String[] getMovementMultOp()
	{
		return movementMultOp;
	}

	public final String getMovementMultOp(int index)
	{
		return movementMultOp[index];
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
					Logging.errorPrint("Badly formed movement string: " + tok);
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

				if ((tok.length() > 0) && ((tok.charAt(0) == '*') || (tok.charAt(0) == '/')))
				{
					movementMult[x] = new Integer(tok.substring(1));
					movementMultOp[x] = tok.substring(0, 1);
					movements[x] = new Integer(0);
				}
				else if (tok.length() > 0)
				{
					movementMult[x] = new Integer(0);
					movementMultOp[x] = "";
					try
					{
						newmove = Integer.parseInt(tok);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Badly formed MOVE token: " + tok);
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

	/**
	 * Used to ignore Encumberance for specified load types
	 **/
	public void setUnencumberedMove(String aString)
	{

		encumberedLoadMoveInt = Constants.LIGHT_LOAD;
		encumberedArmorMoveInt = Constants.LIGHT_LOAD;

		StringTokenizer st = new StringTokenizer(aString, "|");

		while (st.hasMoreTokens())
		{
			String loadString = st.nextToken();
			if (loadString.equalsIgnoreCase("MediumLoad"))
			{
				encumberedLoadMoveInt = Constants.MEDIUM_LOAD;
			}
			else if (loadString.equalsIgnoreCase("HeavyLoad"))
			{
				encumberedLoadMoveInt = Constants.HEAVY_LOAD;
			}
			else if (loadString.equalsIgnoreCase("Overload"))
			{
				encumberedLoadMoveInt = Constants.OVER_LOAD;
			}
			else if (loadString.equalsIgnoreCase("MediumArmor"))
			{
				encumberedArmorMoveInt = Constants.MEDIUM_LOAD;
			}
			else if (loadString.equalsIgnoreCase("HeavyArmor"))
			{
				encumberedArmorMoveInt = Constants.OVER_LOAD;
			}
			else if (loadString.equalsIgnoreCase("LightLoad") || loadString.equalsIgnoreCase("LightArmor"))
			{
				//do nothing, but accept values as valid
			}
			else
			{
				GuiFacade.showMessageDialog(null, "Invalid value of \"" + loadString + "\" for UNENCUMBEREDMOVE in \"" + getName() + "\".",
											"PCGen", GuiFacade.ERROR_MESSAGE);
			}
		}
	}

	public int getEncumberedLoadMove()
	{
		return encumberedLoadMoveInt;
	}

	public int getEncumberedArmorMove()
	{
		return encumberedArmorMoveInt;
	}

	/**
	 * NATURAL WEAPONS CODE
	 * <p/>
	 * first natural weapon is primary,
	 * the rest are secondary;
	 * NATURALATTACKS:primary weapon name,weapon type,num attacks,damage|secondary1 weapon name,weapon type,num attacks,damage|secondary2
	 * format is exactly as it would be in an equipment lst file
	 * Type is of the format Weapon.Natural.Melee.Bludgeoning
	 * number of attacks is the number of attacks with that weapon at BAB (for primary), or BAB - 5 (for secondary)
	 */
	public void setNaturalAttacks(PObject obj, String aString)
	{
		// Currently, this isn't going to work with monk attacks
		// - their unarmed stuff won't be affected.

		String aSize = "M";
		if (obj instanceof PCTemplate)
		{
			aSize = ((PCTemplate) obj).getTemplateSize();
		}
		else if (obj instanceof Race)
		{
			aSize = ((Race) obj).getSize();
		}
		if (aSize == null)
		{
			aSize = "M";
		}

		boolean firstWeapon = true;
		boolean onlyOne = false;

		final StringTokenizer attackTok = new StringTokenizer(aString, "|");

		// Make a preliminary guess at whether this is an "only" attack
		if (attackTok.countTokens() == 1)
		{
			onlyOne = true;
		}

		// This is wrong as we need to replace old natural weapons
		// with "better" ones
		naturalWeapons.clear();

		while (attackTok.hasMoreTokens())
		{

			StringTokenizer aTok = new StringTokenizer(attackTok.nextToken(), ",");
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
				naturalWeapons.add(anEquip);
			}
			firstWeapon = false;
		}
	}

	/**
	 * Create the Natural weapon equipment item
	 * aTok = primary weapon name,weapon type,num attacks,damage
	 * for Example: Tentacle,Weapon.Natural.Melee.Slashing,*4,1d6
	 */
	public Equipment createNaturalWeapon(StringTokenizer aTok, String aSize)
	{
		final String attackName = aTok.nextToken();
		if (attackName.equalsIgnoreCase(Constants.s_NONE))
		{
			return null;
		}

		Equipment anEquip = new Equipment();
		final String profType = aTok.nextToken();

		anEquip.setName(attackName);
		anEquip.setTypeInfo(profType);
		anEquip.setWeight("0");
		anEquip.setSize(aSize, true);

		String numAttacks = aTok.nextToken();
		boolean attacksProgress = true;
		if (numAttacks.length() > 0 && numAttacks.charAt(0) == '*')
		{
			numAttacks = numAttacks.substring(1);
			attacksProgress = false;
		}

		int bonusAttacks = 0;
		try
		{
			bonusAttacks = Integer.parseInt(numAttacks) - 1;
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Non-numeric value for number of attacks: '" + numAttacks + "'");
		}

		if (bonusAttacks > 0)
		{
			anEquip.addBonusList("WEAPON|ATTACKS|" + bonusAttacks);
			anEquip.setOnlyNaturalWeapon(false);
		}
		else
		{
			anEquip.setOnlyNaturalWeapon(true);
		}

		anEquip.setDamage(aTok.nextToken());
		anEquip.setCritRange("1");
		anEquip.setCritMult("x2");
		anEquip.setProfName(attackName);

		// sage_sam 02 Dec 2002 for Bug #586332
		// allow hands to be required to equip natural weapons
		int handsRequired = 0;
		if (aTok.hasMoreTokens())
		{
			final String hString = aTok.nextToken();
			try
			{
				handsRequired = Integer.parseInt(hString);
			}
			catch (NumberFormatException exc)
			{
				Logging.errorPrint("Non-numeric value for hands required: '" + hString + "'");
			}
		}
		anEquip.setSlots(handsRequired);

		//these values need to be locked.
		anEquip.setQty(new Float(1));
		anEquip.setNumberCarried(new Float(1));
		anEquip.setAttacksProgress(attacksProgress);

		setWeaponProfAutos(attackName);

		// Check if the proficiency needs created
		WeaponProf prof = Globals.getWeaponProfKeyed(attackName);
		if (prof == null)
		{
			prof = new WeaponProf();
			prof.setTypeInfo(profType);
			prof.setName(attackName);
			prof.setKeyName(attackName);
			Globals.addWeaponProf(prof);
		}

		return anEquip;
	}

	/**
	 * @return ArrayList of natural weapon equipment items
	 */
	public List getNaturalWeapons()
	{
		return naturalWeapons;
	}

	/**
	 * sets the natural weapon equipment items list
	 */
	public void setNaturalWeapons(List aList)
	{
		naturalWeapons = aList;
	}

	public final void setCSkillList(String aString)
	{
		classSkillString = aString;
		refreshCSkillList();
	}

	public final ArrayList getCSkillList()
	{
		return cSkillList;
	}

	public final void refreshCSkillList()
	{
		if ((classSkillString == null) || (classSkillString.length() <= 0))
		{
			return;
		}
		if (cSkillList == null)
		{
			cSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(classSkillString, "|");
		boolean isClearing = false;
		while (aTok.hasMoreTokens())
		{
			String bString = aTok.nextToken();
			if (bString.startsWith(".CLEAR") || isClearing)
			{
				isClearing = true;
				if (".CLEAR".equals(bString))
				{
					cSkillList.clear();
				}
				else
				{
					if (bString.startsWith(".CLEAR"))
					{
						bString = bString.substring(7);
					}
					if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
					{
						final String typeString = bString.substring(5);
						for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
						{
							final Skill aSkill = (Skill) e1.next();
							boolean toClear = true;
							final StringTokenizer cTok = new StringTokenizer(typeString, ".");
							while (cTok.hasMoreTokens() && toClear)
							{
								if (!aSkill.isType(cTok.nextToken()))
								{
									toClear = false;
								}
							}
							if (toClear)
							{
								cSkillList.remove(aSkill.getName());
							}
						}
					}
					else
					{
						cSkillList.remove(bString);
					}
				}
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
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

	public final ArrayList getCcSkillList()
	{
		return ccSkillList;
	}

	public final void setCcSkillList(String aString)
	{
		if ((aString == null) || (aString.length() <= 0))
		{
			return;
		}

		if (ccSkillList == null)
		{
			ccSkillList = new ArrayList();
		}

		final StringTokenizer aTok = new StringTokenizer(aString, "|");
		boolean isClearing = false;
		while (aTok.hasMoreTokens())
		{
			String bString = aTok.nextToken();
			if (bString.startsWith(".CLEAR") || isClearing)
			{
				isClearing = true;
				if (".CLEAR".equals(bString))
				{
					ccSkillList.clear();
				}
				else
				{
					if (bString.startsWith(".CLEAR"))
					{
						bString = bString.substring(7);
					}
					if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
					{
						final String typeString = bString.substring(5);
						for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
						{
							final Skill aSkill = (Skill) e1.next();
							boolean toClear = true;
							final StringTokenizer cTok = new StringTokenizer(typeString, ".");
							while (cTok.hasMoreTokens() && toClear)
							{
								if (!aSkill.isType(cTok.nextToken()))
								{
									toClear = false;
								}
							}
							if (toClear)
							{
								ccSkillList.remove(aSkill.getName());
							}
						}
					}
					else
					{
						ccSkillList.remove(bString);
					}
				}
			}
			else if (bString.startsWith("TYPE.") || bString.startsWith("TYPE="))
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

	public String getSpellKey()
	{
		return "POBJECT|" + name;
	}

	public void setChoiceString(String aString)
	{
		choiceString = aString;
	}

	public final String getChoiceString()
	{
		return choiceString;
	}

	final void getChoices(String aChoice, List selectedBonusList, List availableList, List selectedList)
	{
		getChoices(aChoice, selectedBonusList, this, availableList, selectedList, true);
	}

	public final void getChoices(String aChoice, List selectedBonusList)
	{

		final List availableList = new ArrayList();
		final List selectedList = new ArrayList();
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

		final StringTokenizer aTok = new StringTokenizer(aString, ",");
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
			else if (bString.startsWith("TYPE=") || bString.startsWith("TYPE."))
			{
				String aType = bString.substring(5);
				List bList = Globals.getLanguageList();
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

	/**
	 * @return the type iterator if available, else an empty iterator (e.g. never null)
	 */
	public Iterator getMyTypeIterator()
	{
		Iterator result = EmptyIterator.EMPTY_ITERATOR;
		if (myTypeList != null)
		{
			result = myTypeList.iterator();
		}
		return result;
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

	public String parseOutputName(String aString)
	{

		int varIndex = aString.indexOf('|');

		if (varIndex <= 0)
		{
			return (aString);
		}

		StringTokenizer varTokenizer = new StringTokenizer(aString, "|");

		String preVarStr = varTokenizer.nextToken();

		ArrayList varArray = new ArrayList();
		ArrayList tokenList = new ArrayList();
		while (varTokenizer.hasMoreElements())
		{
			String token = varTokenizer.nextToken();
			tokenList.add(token.toUpperCase());
			varArray.add(Globals.getCurrentPC().getVariableValue(token, ""));
		}

		StringBuffer result = new StringBuffer();
		int varCount = 0;
		int subIndex = preVarStr.indexOf('%');
		int lastIndex = 0;
		while (subIndex >= 0)
		{
			if (subIndex > 0)
			{
				result.append(preVarStr.substring(lastIndex, subIndex));
			}

			String token = (String) tokenList.get(varCount);
			Float val = (Float) varArray.get(varCount);
			if (token.endsWith(".INTVAL"))
			{
				result.append(String.valueOf(val.intValue()));
			}
			else
			{
				result.append(val.toString());
			}
			lastIndex = subIndex + 1;
			varCount++;
			subIndex = preVarStr.indexOf('%', lastIndex);
		}
		if (preVarStr.length() > lastIndex)
		{
			result.append(preVarStr.substring(lastIndex));
		}

		return (result.toString());

	}

	/**
	 * rephrase parenthetical name components, if appropriate
	 */
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

	/**
	 * This method
	 * @param aSource
	 * @deprecated -- use PObjectLoader parseSource and setSourceMap
	 * 		if you need to update the source info.
	 */
	public final void setSource(String aSource)
	{
		StringTokenizer aTok = new StringTokenizer(aSource, "|");
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

	public final void setSourceMap(Map arg)
	{
		// Don't clear the map, otherwise the SOURCEPAGE:
		// entries on each line will screw it all up

		// It may seem strange to cycle through the map and
		// not let the passed in source override the setting
		// of the already existing source. The only way this
		// happens is if a .MOD is used, but the way the 
		// source loading happens, when a .MOD is loaded
		// after everything else is loaded, the source is
		// set to whatever source was loaded last, which may
		// not be the source of the .MOD.  The SOURCExxx tags
		// on the line should still work to override if necessary.
		for (Iterator i = arg.keySet().iterator(); i.hasNext();)
		{
			String key = (String)i.next();
			if (sourceMap.get(key) == null)
			{
				sourceMap.put(key, arg.get(key));
			}
		}
		// commented out on purpose as explained in comment above!
		// bryan mcroberts 1/19/2004
//		sourceMap.putAll(arg);
	}

	public final String getSourceInForm(int sourceDisplay)
	{
		return returnSourceInForm(sourceDisplay, true);
	}

	private String returnSourceInForm(int sourceDisplay, boolean includePage)
	{
		StringBuffer buf = new StringBuffer();
		Campaign _sourceCampaign = getSourceCampaign();
		String key = null;
		switch (sourceDisplay)
		{
			case Constants.SOURCELONG:
				key = "LONG";
				break;
			case Constants.SOURCESHORT:
				key = "SHORT";
				break;
			case Constants.SOURCEWEB:
				key = "WEB";
				break;
			case Constants.SOURCEPAGE:
				key = "PAGE";
				break;
			default:
				Logging.errorPrint("Unknown source display form in returnSourceInForm: " + sourceDisplay);
				key = "LONG"; //A reasonable default.
				break;
		}
		// get SOURCE for this item with desired key
		String aSource = getSourceWithKey(key);
		if (_sourceCampaign != null)
		{
			// if sourceCampaign object exists, get it's publisher entry for same key
			String arg = _sourceCampaign.getPublisherWithKey(key);
			// returned string starts with publisher entry
			buf = buf.append(arg);
			if (arg.length() > 0)
			{
				buf = buf.append(" - ");
			}
			// if this item's source is null, try to get it from sourceCampaign object
			if (aSource == null)
			{
				aSource = _sourceCampaign.getSourceWithKey(key);
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
		this.sourceFile = sourceFile;
	}

	public final String getSourceFile()
	{
		return sourceFile;
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
	 *
	 * @param aString String of special abilities delimited by pipes
	 * @param level   int level at which the ability is gained
	 */
	public void setSpecialAbilityList(String aString, int level)
	{

		//removed the parsing by ","  All SAs must now use separate SA: tags
		// Tracker #666268 -Lone Jedi
		if (specialAbilityList == null)
		{
			specialAbilityList = new ArrayList();
		}
		StringTokenizer aTok = new StringTokenizer(aString, "|", true);
		if (!aTok.hasMoreTokens())
		{
			return;
		}
		String bString = aTok.nextToken();
		boolean inPreReq = false;
		SpecialAbility sa = new SpecialAbility();
		if (!aString.equals(".CLEAR"))
		{
			specialAbilityList.add(sa);
			Globals.addToSASet(sa);
		}
		while (aTok.hasMoreTokens())
		{
			String cString = aTok.nextToken();
			// if it's a PRExxx: tag, it's a pre-req on previous sa
			if ((cString.startsWith("!PRE") || cString.startsWith("PRE")) && cString.indexOf(":") > 0)
			{
				if (!inPreReq)
				{
					sa.setName(bString);
					bString = "";
				}
				if (inPreReq) // we're already in one pre-req
				{
					sa.addPreReq(bString);
					bString = cString;
				}
				else
				{
					bString += cString;
				}
				inPreReq = true;
			}
			else
			{
				bString += cString;
			}
			if (".CLEAR".equals(cString))
			{
				specialAbilityList.clear();
			}
		}
		if (inPreReq)
		{
			sa.addPreReq(bString);
		}
		else
		{
			sa.setName(bString);
		}
		if (this instanceof PCClass)
		{
			sa.setSASource("PCCLASS=" + name + "|" + level);
		}
	}

	/**
	 * This method gets access to the special ability list
	 */
	public final ArrayList getSpecialAbilityList()
	{
		if (specialAbilityList == null)
		{
			specialAbilityList = new ArrayList();
		}
		return specialAbilityList;
	}

	public final SpecialAbility getSpecialAbilityNamed(String aName)
	{
		if (specialAbilityList != null)
		{
			for (Iterator i = specialAbilityList.iterator(); i.hasNext();)
			{

				final SpecialAbility sa = (SpecialAbility) i.next();
				if (sa.getName().equalsIgnoreCase(aName))
				{
					return sa;
				}
			}
		}
		return null;
	}

	/**
	 * This method gets access to the spell list.
	 */
	public List getSpellList()
	{
		ArrayList aList = new ArrayList();
		if (this instanceof PCClass && spellMap != null)
		{
			int classLevel = ((PCClass) this).getLevel();
			for (Iterator e = spellMap.keySet().iterator(); e.hasNext();)
			{
				String aString = e.next().toString();
				final int level;
				try
				{
					level = Integer.parseInt(aString);
				}
				catch (NumberFormatException nfe)
				{
					Logging.errorPrint("NFE on getSpellList. Shouldn't happen.");
					continue;
				}
				if (classLevel >= level)
				{
					aList.addAll((List) spellMap.get(aString));
				}
			}
			return aList;
		}
		else if (spellMap != null)
		{
			for (Iterator e = spellMap.keySet().iterator(); e.hasNext();)
			{
				String aString = e.next().toString();
				aList.addAll((List) spellMap.get(aString));
			}
			return aList;
		}
		return null;
	}

	public final void clearSpellList()
	{
		spellMap = null;
	}

	static final void getSpellTypeChoices(String aChoice, List availList, List uniqueList)
	{

		final StringTokenizer aTok = new StringTokenizer(aChoice, "|");
		aTok.nextToken(); // should be SPELLLEVEL
		while (aTok.hasMoreTokens())
		{

			String aString = aTok.nextToken();
			while (!aString.startsWith("CLASS=") && !aString.startsWith("CLASS.") && !aString.startsWith("TYPE=") && !aString.startsWith("TYPE.") && aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
			}
			if (!aTok.hasMoreTokens())
			{
				break;
			}

			boolean endIsUnique = false;
// We need the aString acquired above, so we cannot get the next token at it.
//			aString = aTok.nextToken();
			int minLevel = 1;
			try
			{
				minLevel = Integer.parseInt(aTok.nextToken());
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed minLevel token: " + aString);
			}
			String mString = aTok.nextToken();
			if (mString.endsWith(".A"))
			{
				endIsUnique = true;
				mString = mString.substring(0, mString.lastIndexOf(".A"));
			}

			int maxLevel = minLevel;
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aString.startsWith("CLASS=") || aString.startsWith("CLASS."))
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

							String tempString = aClass.getCastStringForLevel(aLevel);
							final StringTokenizer bTok = new StringTokenizer(tempString, ",");
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
			if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
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
							aLevel += (int) aPC.getTotalBonusTo("PCLEVEL", aClass.getName());
							aLevel += (int) aPC.getTotalBonusTo("PCLEVEL", "TYPE." + aString);

							String bString = "0";
							if (aLevel >= 0) // some classes, like "Domain" are level 0, so this index would be -1
							{
								bString = aClass.getCastStringForLevel(aLevel);
							}
							if ("0".equals(bString))
							{
								maxLevel = -1;
							}
							else
							{

								final StringTokenizer bTok = new StringTokenizer(bString, ",");
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
		final StringTokenizer aTok = new StringTokenizer(aString.toUpperCase().trim(), ".");
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

		final StringBuffer aType = new StringBuffer(x * 5);
		for (int i = 0; i < x; ++i)
		{
			aType.append(i == 0 ? "" : ".").append(getMyType(i));
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

	/**
	 * <p>Retrieves the unarmed damage information for this PObject.  This
	 * comes from the <code>UDAM</code> tag, and can be a simple die string
	 * as in <code>1d20</code>, or a list of size-modified data like is
	 * utilized for monk unarmed damage.</p>
	 * 
	 * @param includeCrit Whether or not to include critical multiplier
	 * @param includeStrBonus Whether or not to include strength damage bonus
	 * 
	 * @return A string representing the unarmed damage dice of the object.
	 */
	final String getUdamFor(boolean includeCrit, boolean includeStrBonus)
	{

		// the assumption is that there is only one UDAM: tag for things other than class
		if (udamList == null || udamList.isEmpty())
		{
			return "";
		}

		final StringBuffer aString = new StringBuffer(udamList.get(0).toString());
		final PlayerCharacter aPC = Globals.getCurrentPC();
		//Added to handle sizes for damage, Ross M. Lodge
		int iSize = Globals.sizeInt(aPC.getSize());
		final StringTokenizer aTok =
			new StringTokenizer(
				aString.toString(),
				",",
				false);
		while (iSize > -1 && aTok.hasMoreTokens())
		{
			aString.replace(0,aString.length(),aTok.nextToken());
			if (iSize == 0)
			{
				break;
			}
			iSize -= 1;
		}
		//End added
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
	 * <p/>
	 * not-yet-deprecated This should be replaced by getVariable
	 */
	public final String getVariableDefinition(int i)
	{
		if (variableList != null)
		{
			return variableList.getDefinition(i);
		}
		else
		{
			return null;
		}
	}

	protected final void setVariable(int idx, String var)
	{
		if (variableList == null)
		{
			variableList = new VariableList();
		}
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
		if (variableList != null)
		{
			variableList.clear();
		}
	}

	public final int getVariableCount()
	{
		if (variableList == null)
		{
			return 0;
		}
		return variableList.size();
	}

	/**
	 * Takes a string of the form:
	 * Darkvision (60')|Low-light
	 * and builds a hashMap for this object.
	 * It also adds the type (such as Darkvision) to a Global hashMap
	 */
	public void setVision(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
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

				final StringTokenizer bTok = new StringTokenizer(bString, ",");
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
				StringTokenizer cTok = new StringTokenizer(dString, "(')");
				final String aKey = cTok.nextToken().trim(); //	 e.g. Darkvision
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
				StringTokenizer cTok = new StringTokenizer(dString, "(')");
				final String aKey = cTok.nextToken().trim(); //	 e.g. Darkvision
				Globals.putVisionMap(aKey);
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

	public Map getVision()
	{
		return vision;
	}

	/**
	 * Adds Weapons/Armor/Shield names/types to new Proficiency mapping
	 *
	 * @param aString is a list of equipment and new Profs
	 */
	public void addChangeProf(String aString)
	{
		// aString should be of the format:
		// Name1,TYPE.type1,Name3=Prof1|Name4,Name5=Prof2
		//
		// eg: TYPE.Hammer,Hand Axe=Simple|Urgosh,Waraxe=Martial
		//
		StringTokenizer aTok = new StringTokenizer(aString, "|");
		while (aTok.hasMoreTokens())
		{
			String nPart = aTok.nextToken();
			String newProf = "";
			int indx = nPart.indexOf('=');
			if (indx > 1)
			{
				newProf = nPart.substring(indx + 1);
				nPart = nPart.substring(0, indx);
			}
			else
			{
				Logging.errorPrint("Malformed CHANGEPROF tag: " + nPart);
				continue;
			}
			StringTokenizer bTok = new StringTokenizer(nPart, ",");
			while (bTok.hasMoreTokens())
			{
				String eqString = bTok.nextToken();
				changeProfMap.put(eqString, newProf);
			}
		}
	}

	/**
	 * Get a list of WeaponProf|ProfType strings from changeProfMap
	 */
	public List getChangeProfList()
	{
		List aList = new LinkedList();
		for (Iterator e = changeProfMap.keySet().iterator(); e.hasNext();)
		{
			// aKey will either be:
			//  TYPE.blah
			// or
			//  Weapon Name
			String aKey = e.next().toString();
			// New proficiency type, such as Martial or Simple
			String newProfType = changeProfMap.get(aKey).toString();
			if (aKey.startsWith("TYPE."))
			{
				// need to get all items of this TYPE
				for (Iterator eq = EquipmentList.getEquipmentOfType(EquipmentList.getEquipmentList(), aKey.substring(5), "").iterator(); eq.hasNext();)
				{
					String aName = ((Equipment) eq.next()).profName();
					aList.add(aName + "|" + newProfType);
				}
			}
			else
			{
				Equipment aEq = EquipmentList.getEquipmentNamed(aKey);
				if (aEq == null)
				{
					continue;
				}
				String aName = aEq.profName();
				aList.add(aName + "|" + newProfType);

				// Try for an Exotic version
				String aName2 = aEq.profName(1);
				if (!aName2.equals(aName) && !aName2.equals(aEq.getName())) {
					aList.add(aName2 + "|" + newProfType);
				}
			}
		}
		return aList;
	}

	public final void setWeaponProfAutos(String aString)
	{
		final StringTokenizer aTok = new StringTokenizer(aString, "|");
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
			String preReqTag = null;
			List preReqList = new ArrayList();
			final int j1 = aString.lastIndexOf('[');
			int j2 = aString.lastIndexOf(']');
			if (j2 < j1)
			{
				j2 = tag.length();
			}
			if (j1 >= 0)
			{
				preReqTag = aString.substring(j1 + 1, j2);
				preReqList.add(preReqTag);
				if (!passesPreReqToUseForList(preReqList))
				{
					return;
				}
				aString = aString.substring(0, j1);
			}
			StringTokenizer aTok = new StringTokenizer(aString, "|");
			aTok.nextToken(); // removes tag token
			String tok;
			while (aTok.hasMoreTokens())
			{
				tok = aTok.nextToken();
				if ((tok.startsWith("TYPE=") || tok.startsWith("TYPE.")) && tag.startsWith("WEAPON"))
				{
					StringTokenizer bTok = new StringTokenizer(tok.substring(5), ".");
					List xList = null;
					while (bTok.hasMoreTokens())
					{
						String bString = bTok.nextToken();
						List bList = Globals.getWeaponProfs(bString);
						if (bList.size() == 0)
						{
							bList.addAll(EquipmentList.getEquipmentOfType(EquipmentList.getEquipmentList(), "Weapon." + bString, ""));
						}
						if (xList == null)
						{
							xList = new ArrayList();
							for (Iterator e = bList.iterator(); e.hasNext();)
							{
								Object obj = e.next();
								String wprof;
								if (obj instanceof Equipment)
								{
									wprof = ((Equipment) obj).profName();
								}
								else
								{
									wprof = obj.toString();
								}
								if (!xList.contains(wprof))
								{
									xList.add(wprof);
								}
							}
						}
						else
						{
							List removeList = new ArrayList();
							for (Iterator e = xList.iterator(); e.hasNext();)
							{
								String wprof = (String) e.next();
								boolean contains = false;

								for (Iterator f = bList.iterator(); f.hasNext();)
								{
									Object obj = f.next();
									String wprof2;
									if (obj instanceof Equipment)
									{
										wprof2 = ((Equipment) obj).profName();
									}
									else
									{
										wprof2 = obj.toString();
									}
									if (wprof.equals(wprof2))
									{
										contains = true;
										break;
									}
								}

								if (!contains)
								{
									removeList.add(wprof);
								}
							}
							for (Iterator e = removeList.iterator(); e.hasNext();)
							{
								String wprof = (String) e.next();
								xList.remove(wprof);
							}
						}
					}
					aList.addAll(xList);
				}
				else if ((tok.startsWith("TYPE=") || tok.startsWith("TYPE.")) && tag.startsWith("ARMOR"))
				{
					aList.add(tok);
				}
				else if (tag.startsWith("EQUIP"))
				{
					Equipment aEq = EquipmentList.getEquipmentFromName(tok);
					if (aEq != null)
					{
						Equipment newEq = (Equipment) aEq.clone();
						newEq.setQty(1);
						newEq.setAutomatic(true);
						newEq.setOutputIndex(aList.size());
						aList.add(newEq);
					}
				}
				else if ("%LIST".equals(tok))
				{
					for (Iterator e = getAssociatedList().iterator(); e.hasNext();)
					{
						String wString = (String) e.next();
						aList.add(wString);
					}
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

	protected List addSpecialAbilitiesToList(List aList)
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

	/**
	 * returns all the spells of levelMatch or lower
	 * that pass all the PreReqs
	 */
	public HashMap getSpellMapPassesPrereqs(int levelMatch)
	{
		HashMap tempMap = new HashMap();
		if (spellLevelMap == null)
		{
			return tempMap;
		}
		for (Iterator sm = spellLevelMap.keySet().iterator(); sm.hasNext();)
		{
			final String key = sm.next().toString();
			int levelInt = -1;
			try
			{
				levelInt = Integer.parseInt((String) spellLevelMap.get(key));
			}
			catch (NumberFormatException nfe)
			{
				// ignored
			}
			// levelMatch == -1 means get all spells
			if (((levelMatch == -1) && (levelInt >= 0)) ||
				((levelMatch >= 0) && (levelInt == levelMatch)))
			{
				if ((preReqSpellLevelMap != null) && preReqSpellLevelMap.containsKey(key))
				{
					if (key.startsWith("CLASS|SPELLCASTER"))
					{
						String spellType = key.substring(18);
						spellType = spellType.substring(0, spellType.indexOf("|"));
						if ("ALL".equals(spellType) || Globals.getCurrentPC().isSpellCaster(spellType, 1))
						{
							if (passesPreReqToGainForList((List) preReqSpellLevelMap.get(key)))
							{
								for (Iterator iClass = Globals.getCurrentPC().getClassList().iterator(); iClass.hasNext();)
								{
									final PCClass aClass = (PCClass) iClass.next();
									if (aClass.getSpellType().equals(spellType) || "ALL".equals(spellType))
									{
										tempMap.put(aClass.getSpellKey() + "|" + key.substring(key.lastIndexOf("|") + 1), Integer.toString(levelInt));
									}
								}
							}
						}
					}
					else if (passesPreReqToGainForList((List) preReqSpellLevelMap.get(key)))
					{
						tempMap.put(key, Integer.toString(levelInt));
					}
				}
			}
		}
		return tempMap;
	}

	public HashMap getSpellInfoMapPassesPrereqs(String aKey)
	{
		HashMap tempMap = new HashMap();
		if (spellInfoMap == null)
		{
			return tempMap;
		}

		if (spellInfoMap.containsKey(aKey))
		{
			final String wType = aKey.substring(0, aKey.indexOf("|"));
			String wName = (String) spellInfoMap.get(aKey);
			String spellName = aKey.substring(aKey.indexOf("|") + 1);
			int wLevel = Integer.parseInt(wName.substring(wName.indexOf("|") + 1));
			wName = wName.substring(0, wName.indexOf("|"));
			if ((preReqSpellLevelMap != null) && preReqSpellLevelMap.containsKey(wType + "|" + wName + "|" + spellName))
			{
				if (wName.startsWith("SPELLCASTER"))
				{
					String spellType = wName.substring(12);
					if ("ALL".equals(spellType) || Globals.getCurrentPC().isSpellCaster(spellType, 1))
					{
						if (passesPreReqToGainForList((List) preReqSpellLevelMap.get(wType + "|" + wName + "|" + spellName)))
						{
							for (Iterator iClass = Globals.getCurrentPC().getClassList().iterator(); iClass.hasNext();)
							{
								final PCClass aClass = (PCClass) iClass.next();
								if (aClass.getSpellType().equals(spellType) || "ALL".equals(spellType))
								{
									tempMap.put(aClass.getSpellKey(), new Integer(wLevel));
								}
							}
						}
					}
				}
				else if (passesPreReqToGainForList((List) preReqSpellLevelMap.get(wType + "|" + wName + "|" + spellName)))
				{
					tempMap.put(wType + "|" + wName, new Integer(wLevel));
				}
			}
		}
		return tempMap;
	}

	// SPELLLEVEL:CLASS|Name1,Name2=Level1|Spell1,Spell2,Spell3|Name3=Level2|Spell4,Spell5|PRExxx|PRExxx
	public void addSpellLevel(String line)
	{
		final StringTokenizer aTok = new StringTokenizer(line, "|");
		if (aTok.countTokens() < 3)
		{
			Logging.errorPrint("Badly formed SPELLLEVEL tag1: " + line);
			return;
		}
		final String tagType = aTok.nextToken(); // CLASS or DOMAIN
		List preList = new ArrayList();
		// The 2 lists below should always have the same number of items
		List wNameList = new ArrayList();
		List wSpellList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			final String nameList = aTok.nextToken();
			if (nameList.startsWith("PRE") || nameList.startsWith("!PRE"))
			{
				preList.add(nameList);
				break;
			}
			if (nameList.indexOf("=") < 0)
			{
				Logging.errorPrint("Badly formed SPELLLEVEL tag2: " + line);
				return;
			}
			wNameList.add(nameList);
			if (!aTok.hasMoreTokens())
			{
				Logging.errorPrint("Badly formed SPELLLEVEL tag3: " + line);
				return;
			}
			wSpellList.add(aTok.nextToken());
		}
		while (aTok.hasMoreTokens())
		{
			final String nameList = aTok.nextToken();
			if (nameList.startsWith("PRE") || nameList.startsWith("!PRE"))
			{
				preList.add(nameList);
			}
			else
			{
				Logging.errorPrint("Badly formed SPELLLEVEL PRE tag: " + line);
				return;
			}
		}
		for (Iterator iSpell = wSpellList.iterator(), iName = wNameList.iterator(); iSpell.hasNext() || iName.hasNext();)
		{
			// Check to see if both exists
			if (!(iSpell.hasNext() && iName.hasNext()))
			{
				Logging.errorPrint("Badly formed SPELLLEVEL tag4: " + line);
				return;
			}
			final StringTokenizer bTok = new StringTokenizer((String) iSpell.next(), ",");
			final String classList = (String) iName.next();
			while (bTok.hasMoreTokens())
			{
				final String spellLevel = classList.substring(classList.indexOf("=") + 1);
				final String spellName = bTok.nextToken();
				final StringTokenizer cTok = new StringTokenizer(classList.substring(0, classList.indexOf("=")), ",");
				while (cTok.hasMoreTokens())
				{
					final String className = cTok.nextToken();
					if (spellLevelMap == null)
					{
						spellLevelMap = new HashMap();
						spellInfoMap = new HashMap();
						preReqSpellLevelMap = new HashMap();
					}
					if (className.startsWith("SPELLCASTER."))
					{
						preReqSpellLevelMap.put(tagType + "|" + className + "|" + spellName, preList);
						spellLevelMap.put(tagType + "|" + className + "|" + spellName, spellLevel);
						spellInfoMap.put(tagType + "|" + spellName, className + "|" + spellLevel);
					}
					else if (!spellLevelMap.containsKey(tagType + "|" + className + "|" + spellName))
					{
						preReqSpellLevelMap.put(tagType + "|" + className + "|" + spellName, preList);
						spellLevelMap.put(tagType + "|" + className + "|" + spellName, spellLevel);
						spellInfoMap.put(tagType + "|" + spellName, className + "|" + spellLevel);
					}
				}
			}
		}
	}

	/**
	 * This method created 12 Aug 2003 by sage_sam
	 * for spell refactoring
	 */
	public void addSpells(List aSpellList)
	{
		addSpells(0, aSpellList);
	}

	public void addSpells(int level, List aSpellList)
	{
		String aLevel = Integer.toString(level);
		if (spellMap == null)
		{
			spellMap = new HashMap();
		}

		Iterator iter = aSpellList.iterator();
		while (iter.hasNext())
		{
			Object spell = iter.next();
			if (!spellMap.containsKey(aLevel))
			{
				ArrayList aList = new ArrayList();
				spellMap.put(aLevel, aList);
			}
			ArrayList aList = (ArrayList) spellMap.get(aLevel);
			if (!aList.contains(spell))
			{
				aList.add(spell);
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
			variableList = new VariableList();
		}
		return variableList.getVariableNamesAsUnmodifiableSet();
	}

	/**
	 * Calculate a Bonus given a BonusObj
	 */
	public double calcBonusFrom(BonusObj aBonus, Object anObj)
	{
		int iTimes = 1;

		String aType = aBonus.getTypeOfBonus();

		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			if (choiceString.startsWith("SALIST|") && choiceString.indexOf("|VAR|") >= 0)
			{
				iTimes = 1;
			}
		}

		return calcPartialBonus(iTimes, aBonus, anObj);
	}

	/**
	 * calcPartialBonus calls appropriate getVariableValue() for a Bonus
	 *
	 * @param bString Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param iTimes  multiply bonus * iTimes
	 * @param aBonus  The bonuse Object used for calcs
	 */
	private double calcPartialBonus(int iTimes, BonusObj aBonus, Object anObj)
	{
		String aList = aBonus.getBonusInfo();
		String aVal = aBonus.getValue();

		double iBonus = 0;

		if (aList.equals("ALL"))
		{
			return 0;
		}

		if (aBonus.isValueStatic())
		{
			iBonus = aBonus.getValueAsdouble();
		}
		else if (anObj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) anObj).getVariableValue(aVal, "").doubleValue();
		}
		else if (anObj instanceof Equipment)
		{
			iBonus = ((Equipment) anObj).getVariableValue(aVal, "", "").doubleValue();
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
				Logging.errorPrint("calcPartialBonus NumberFormatException in BONUS: " + aVal);
			}
		}

		return iBonus * iTimes;
	}

	/**
	 * bonusTo() code
	 */
	public double bonusTo(String aType, String aName)
	{
		return bonusTo(aType, aName, Globals.getCurrentPC());
	}

	public double bonusTo(String aType, String aName, Object obj)
	{
		return bonusTo(aType, aName, obj, getBonusList());
	}

	private static boolean dontRecurse = false;

	public double bonusTo(String aType, String aName, Object obj, List aBonusList)
	{
		if (aBonusList == null || aBonusList.size() == 0)
		{
			return 0;
		}

		double retVal = 0;

		aType = aType.toUpperCase();
		aName = aName.toUpperCase();

		final String aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();

		if (!dontRecurse && (this instanceof Feat) && !Globals.checkRule("FEATPRE"))
		{
			// SUCK!  This is horrid, but bonusTo is
			// actually recursive with respect to
			// passesPreReqToGain and there is no other
			// way to do this without decomposing the
			// dependencies.  I am loathe to break working
			// code.  This addresses bug #709677 -- Feats
			// give bonuses even if you no longer qualify
			dontRecurse = true;
			boolean returnZero = false;

			if (!passesPreReqToUse())
			{
				returnZero = true;
			}

			dontRecurse = false;

			if (returnZero)
			{
				return 0;
			}
		}

		int iTimes = 1;

		if ("VAR".equals(aType))
		{
			iTimes = Math.max(1, getAssociatedCount());

			//
			// SALIST will stick BONUS:VAR|...
			// into bonus list so don't multiply
			//
			if (choiceString.startsWith("SALIST|") && choiceString.indexOf("|VAR|") >= 0)
			{
				iTimes = 1;
			}
		}

		for (Iterator b = aBonusList.iterator(); b.hasNext();)
		{
			BonusObj aBonus = (BonusObj) b.next();
			String bString = aBonus.toString().toUpperCase();

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
						String xString = new StringBuffer().append(firstPart).append(getAssociated(i)).append(secondPart).toString().toUpperCase();
						retVal += calcBonus(xString, aType, aName, aTypePlusName, obj, iTimes, aBonus);
					}
					bString = new StringBuffer().append(firstPart).append(getAssociated(0)).append(secondPart).toString().toUpperCase();
				}
			}
			retVal += calcBonus(bString, aType, aName, aTypePlusName, obj, iTimes, aBonus);
		}
		return retVal;
	}

	/**
	 * calcBonus adds together all the bonuses for aType of aName
	 *
	 * @param bString       Either the entire BONUS:COMBAT|AC|2 string or part of a %LIST or %VAR bonus section
	 * @param aType         Such as "COMBAT"
	 * @param aName         Such as "AC"
	 * @param aTypePlusName "COMBAT.AC."
	 * @param obj           The object to get the bonus from
	 * @param iTimes        multiply bonus * iTimes
	 */
	private double calcBonus(String bString, String aType, String aName, String aTypePlusName, Object obj, int iTimes, BonusObj aBonusObj)
	{
		final StringTokenizer aTok = new StringTokenizer(bString, "|");
		if (aTok.countTokens() < 3)
		{
			Logging.errorPrint("Badly formed BONUS:" + bString);
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

		if (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
		}

		double iBonus = 0;
		if (obj instanceof PlayerCharacter)
		{
			iBonus = ((PlayerCharacter) obj).getVariableValue(aString, "").doubleValue();
		}
		else if (obj instanceof Equipment)
		{
			iBonus = ((Equipment) obj).getVariableValue(aString, "", "").doubleValue();
		}
		else
		{
			try
			{
				iBonus = Float.parseFloat(aString);
			}
			catch (NumberFormatException e)
			{
				//Should this be ignored?
				Logging.errorPrint("calcBonus NumberFormatException in BONUS: " + aString, e);
			}
		}

		final List bonusPreReqList = new ArrayList();
		String possibleBonusTypeString = aBonusObj.getTypeString();
		StringTokenizer pTok = new StringTokenizer(aBonusObj.getPrereqString(),"|",false);
		while (pTok.hasMoreTokens())
		{
			final String pString = pTok.nextToken();
			bonusPreReqList.add(pString);
		}

		// must meet criteria before adding any bonuses
		if (obj instanceof PlayerCharacter)
		{
			if (!passesPreReqToGainForList(bonusPreReqList))
			{
				return 0;
			}
		}
		else
		{
			if (!passesPreReqToGainForList((PObject) obj, bonusPreReqList))
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
				bonus += iBonus * iCount;
			}
		}

		String bonusTypeString = null;

		final StringTokenizer bTok = new StringTokenizer(aList, ",");
		if (aList.equalsIgnoreCase("LIST"))
		{
			bTok.nextToken();
		}
		else if (aList.equalsIgnoreCase("ALL"))
		{
			// aTypePlusName looks like: "SKILL.ALL."
			// so we need to reset it to "SKILL.Hide."
			aTypePlusName = new StringBuffer(aType).append('.').append(aName).append('.').toString();
			bonus = iBonus;
			bonusTypeString = possibleBonusTypeString;
		}

		while (bTok.hasMoreTokens())
		{
			if (bTok.nextToken().equalsIgnoreCase(aName))
			{
				bonus += iBonus;
				bonusTypeString = possibleBonusTypeString;
			}
		}

		if (obj instanceof Equipment)
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
	 * public" 'clone ()' method It should be declared to throw
	 * CloneNotSupportedException', but subclasses do not need the "throws"
	 * declaration unless their 'clone ()' method will throw the exception
	 * Thus subclasses can decide to not support 'Cloneable' by implementing
	 * the 'clone ()' method to throw 'CloneNotSupportedException'
	 * If this rule were ignored and the parent did not have the "throws"
	 * declaration then subclasses that should not be cloned would be forced
	 * to implement a trivial 'clone ()' to satisfy inheritance
	 * final" classes implementing 'Cloneable' should not be declared to
	 * throw 'CloneNotSupportedException" because their implementation of
	 * clone ()' should be a fully functional method that will not
	 * throw the exception.
	 */
	public Object clone() throws CloneNotSupportedException
	{
		PObject retVal = (PObject) super.clone();
		retVal.setName(name);
		retVal.description = description;
		retVal.tempDesc = tempDesc;
		retVal.sourceFile = this.sourceFile;
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
		// added 04 Aug 2003 by sage_sam -- bug#765749
		// need to copy map correctly during a clone
		if (sourceMap != null)
		{
			retVal.sourceMap = new HashMap();
			retVal.sourceMap.putAll(this.sourceMap);
		}

		retVal.changeProfMap = new HashMap(changeProfMap);

		if (virtualFeatList != null)
		{
			retVal.virtualFeatList = new LinkedList(virtualFeatList);
		}
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
			retVal.ownBonuses();
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
		if (spellMap != null)
		{
			retVal.spellMap = new HashMap(spellMap);
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
		if (selectedShieldProfs != null)
		{
			retVal.selectedShieldProfs = (ArrayList) selectedShieldProfs.clone();
		}

		// why isn't this cloned if != null?
		// because the saveList is based on user selections (merton_monk@yahoo.com)
		retVal.saveList = null;	    // starts out empty

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
//			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
//		}
		return retVal;
	}

	public int compareTo(Object obj)
	{
		if (obj != null)
		{
			//this should throw a ClassCastException for non-PObjects, like the Comparable interface calls for
			return this.name.compareToIgnoreCase(((PObject) obj).name);
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
		if (associatedList.get(0) instanceof FeatMultipleChoice)
		{
			FeatMultipleChoice fmc;
			for (int i = 0; i < associatedList.size(); ++i)
			{
				fmc = (FeatMultipleChoice) associatedList.get(i);
				String aString = fmc.toString().toUpperCase();
				if (aString.indexOf(associated) >= 0)
				{
					return true;
				}
			}
		}
		else
		{
			for (int i = 0; i < associatedList.size(); ++i)
			{
				String aString = (String) associatedList.get(i);
				if (aString.equalsIgnoreCase(associated))
				{
					return true;
				}
			}
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
			if (aName.equalsIgnoreCase(aString))
			{
				return true;
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
	public final boolean passesPreReqToGain()
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}
		return passesPreReqToGainForList(preReqList);
	}

	//PreReqs for all objects
	public final boolean passesPreReqToUse()
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}
		return passesPreReqToUseForList(preReqList);
	}

	public final boolean passesPreReqToUse(BonusObj aBonus)
	{
		return passesPreReqToUseForList(aBonus.getPrereqList());
	}

	final boolean passesPreReqToGain(PObject p)
	{
		if (getPreReqCount() == 0)
		{
			return true;
		}
		return passesPreReqToGainForList(p, preReqList);
	}

	/**
	 * PreReqs for specified list
	 *
	 * @param argList
	 * @return
	 */
	public final boolean passesPreReqToGainForList(List argList)
	{
		//
		// Nothing to add, so allow it to be added always
		//
		if ((argList == null) || (argList.size() == 0))
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

		return PrereqHandler.passesPreReqToGainForList(this, aPC, null, argList);
	}

	public final boolean passesPreReqToUseForList(List argList)
	{
		//
		// Nothing to add, so allow it to be added always
		//
		if (argList.size() == 0)
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

		if (this instanceof Equipment)
		{
			return PrereqHandler.passesPreReqToUseForList(this, aPC, this, argList);
		}
		else
		{
			return PrereqHandler.passesPreReqToUseForList(this, aPC, null, argList);
		}
	}

	public final boolean passesPreReqToGainForList(PObject aObj, List anArrayList)
	{
		return PrereqHandler.passesPreReqToGainForList(this, Globals.getCurrentPC(), aObj, anArrayList);
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

	final String preReqHTMLStringsForList(PObject aObj, List aList, boolean includeHeader)
	{
		return preReqHTMLStringsForList(null, aObj, aList, includeHeader);
	}

	public final String preReqHTMLStringsForList(PlayerCharacter aPC, PObject aObj, List aList, boolean includeHeader)
	{
		if ((aList == null) || aList.isEmpty())
		{
			return "";
		}

		final StringBuffer pString = new StringBuffer(aList.size() * 20);

		final List newList = new ArrayList();
		int iter = 0;
		final int fontColor = SettingsHandler.getPrereqFailColor();
		for (Iterator e = aList.iterator(); e.hasNext();)
		{

			//String aString = (String)e.next();
			newList.clear();
			newList.add(e.next());
			if (iter++ > 0)
			{
				pString.append(' ');
			}

			String bString = PrereqHandler.preReqStringsForList(newList);

			boolean flag;
			if (aPC != null)
			{
				flag = PrereqHandler.passesPreReqToGainForList(this, aPC, aObj, newList);
			}
			else if (aObj == null)
			{
				flag = passesPreReqToGainForList(newList);
			}
			else
			{
				flag = passesPreReqToGainForList(aObj, newList);
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
			// seems that ALIGN and STAT have problems in
			// HTML display, so wrapping in <font> tag.
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
		return PrereqHandler.preReqStringsForList(preReqList);
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
				Logging.errorPrint("removeSave: Could not find bonus: " + bonusString + " in saveList.");
			}
		}
		else
		{
			Logging.errorPrint("removeSave: Could not find bonus: " + bonusString + " in saveList.");
		}
	}

	public final void removeType(String aString)
	{

		final String typeString = aString.toUpperCase().trim();
		final StringTokenizer aTok = new StringTokenizer(typeString, ".");
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
		if (this instanceof Feat)
		{
			aString = ((Feat) this).getBenefitDescription();
		}
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

	public final void setTempDescription(String aString)
	{
		tempDesc = aString;
	}

	public final String getTempDescription()
	{
		return tempDesc;
	}

	private void getChoices(String aChoice, List selectedBonusList, PObject theObj, List availableList, List selectedList, boolean process)
	{
		if (!choiceString.startsWith("FEAT|") && !choiceString.startsWith("ARMORPROF") && !choiceString.startsWith("SPELLLEVEL") && !aChoice.startsWith("SPELLLEVEL") && !aChoice.startsWith("WEAPONPROF") && !aChoice.startsWith("SHIELDPROF"))
		{
			return;
		}

		if (aChoice.length() == 0)
		{
			aChoice = choiceString;
		}

		StringTokenizer aTok = new StringTokenizer(aChoice, "|");
		aTok.nextToken(); // should be ARMORPROF, SPELLLEVEL or WEAPONPROF

		final PlayerCharacter aPC = Globals.getCurrentPC();
		String tempString = aTok.nextToken();

		int pool = aPC.getVariableValue(tempString, "").intValue();

		boolean dupsAllowed = true;
		String title = "";
		final List otherArrayList = new ArrayList();
		final List cArrayList = new ArrayList();

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
				if ((f != null) && f.passesPreReqToGain())
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

					final StringTokenizer bTok = new StringTokenizer(aString, "[]");
					final String bString = bTok.nextToken();
					adding = true;
					while (bTok.hasMoreTokens())
					{
						otherArrayList.add(bString + "|" + bTok.nextToken());
					}
					aString = bString;
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

						StringTokenizer bTok = new StringTokenizer(weaponList, "|");
						while (bTok.hasMoreTokens())
						{

							String bString = bTok.nextToken();
							availableList.add(bString);
							if (adding)
							{

								final StringTokenizer cTok = new StringTokenizer(cString, "[]");
								cTok.nextToken(); //Read and throw away a token
								while (cTok.hasMoreTokens())
								{
									otherArrayList.add(bString + "|" + cTok.nextToken());
								}
							}
						}
					}
				}
				else if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
				{

					final StringTokenizer bTok = new StringTokenizer(aString.substring(5), ".");
					List typeList = new ArrayList();
					int iSize = -1;
					while (bTok.hasMoreTokens())
					{

						final String bString = bTok.nextToken();
						if (bString.startsWith("SIZE=") || aString.startsWith("SIZE."))
						{
							iSize = Globals.sizeInt(bString.substring(5));
						}
						else
						{
							typeList.add(bString);
						}
					}

					for (Iterator ei = EquipmentList.getEquipmentList().iterator(); ei.hasNext();)
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

									final StringTokenizer cTok = new StringTokenizer(cString, "[]");
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
				if (tempString.startsWith("TYPE=") || tempString.startsWith("TYPE."))
				{
					tempString = tempString.substring(5);
					for (Iterator i = EquipmentList.getEquipmentList().iterator(); i.hasNext();)
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
					Equipment eq = EquipmentList.getEquipmentNamed(tempString);
					if (eq != null && eq.isArmor() && !availableList.contains(eq.profName()))
					{
						availableList.add(eq.profName());
					}
				}
			}
		}
		else if (aChoice.startsWith("SHIELDPROF"))
		{
			if (theObj.getSelectedShieldProfs() != null)
			{
				selectedList.addAll(theObj.getSelectedShieldProfs());
			}
			while (aTok.hasMoreTokens())
			{
				tempString = aTok.nextToken();
				if (tempString.startsWith("TYPE=") || tempString.startsWith("TYPE."))
				{
					tempString = tempString.substring(5);
					for (Iterator i = EquipmentList.getEquipmentList().iterator(); i.hasNext();)
					{
						Equipment eq = (Equipment) i.next();
						if (eq.isShield() && eq.isType(tempString) && !availableList.contains(eq.profName()))
						{
							availableList.add(eq.profName());
						}
					}
				}
				else
				{
					Equipment eq = EquipmentList.getEquipmentNamed(tempString);
					if (eq != null && eq.isShield() && !availableList.contains(eq.profName()))
					{
						availableList.add(eq.profName());
					}
				}
			}
		}

		if (!process || (availableList.size() + selectedList.size() + cArrayList.size() == 0))
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
					aTok = new StringTokenizer(bString, "|");
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
						else if (cString.startsWith("FEAT=") || cString.startsWith("FEAT."))
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
			// make sure the list is built
			aPC.getWeaponProfList();
		}
		else if (aChoice.startsWith("ARMORPROF"))
		{
			theObj.addSelectedArmorProfs(c.getSelectedList());
		}
		else if (aChoice.startsWith("SHIELDPROF"))
		{
			theObj.addSelectedShieldProfs(c.getSelectedList());
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

	public boolean passesPreApplied(PlayerCharacter aPC, PObject anObj)
	{
		if (!aPC.getUseTempMods())
		{
			return false;
		}

		// If anObj is null, use this objects tempBonusList
		if (anObj == null)
		{
			for (Iterator aB = getTempBonusList().iterator(); aB.hasNext();)
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

		// else use the anObj's tempBonusList
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


	private final String preReqHTMLStringsForList(List anArrayList)
	{
		return preReqHTMLStringsForList(null, anArrayList);
	}

	private final String preReqHTMLStringsForList(PObject aObj, List anArrayList)
	{
		return preReqHTMLStringsForList(aObj, anArrayList, true);
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
			Logging.errorPrint("removeBonus: Could not find bonus: " + bonusString + " in bonusList.");
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
			txt.append("\tCCSKILL:").append(Utility.join(ccSkillList, "|"));
		}

		if ((cSkillList != null) && (cSkillList.size() != 0))
		{
			txt.append("\tCSKILL:").append(Utility.join(cSkillList, "|"));
		}

		aString = getChoiceString();
		if ((aString != null) && (aString.length() != 0))
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
			txt.append("\tLANGAUTO:").append(Utility.join(langSet, ","));
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
			txt.append("\tWEAPONAUTO:").append(Utility.join(weaponProfAutos, "|"));
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
			if (!kits.contains(arg))
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
			Logging.errorPrint("Badly formed kitString: " + tok);
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
			Logging.errorPrint("Badly formed number of kit selections attribute: " + tok);
			num = 0;
		}

		List aList = new ArrayList();
		while (aTok.hasMoreTokens())
		{
			String kitName = aTok.nextToken();
			Kit aKit = Globals.getKitNamed(kitName);
			if (aKit.passesPreReqToUse())
				aList.add(kitName);
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
				List thingsToAdd = new ArrayList();
				List warnings = new ArrayList();
				theKit.addKitFeats(thingsToAdd, warnings);
				theKit.addKitProfs(thingsToAdd, warnings);
				theKit.addKitGear(thingsToAdd, warnings);
				theKit.addKitSpells(thingsToAdd, warnings);
				theKit.addKitSkills(thingsToAdd, warnings);
				theKit.processKit(thingsToAdd, iKit);
				aPC.addTemplateNamed(theKit.getTemplateString());
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
		final StringTokenizer aTok = new StringTokenizer(regionString, "|");
		// first element is prelevel - should be 0 for everything but PCClass entries
		String tok = aTok.nextToken();
		int aLevel;
		try
		{
			aLevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed preLevel attribute in makeRegionSelection: " + tok);
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
			Logging.errorPrint("Badly formed number of selection attribute in makeRegionSelection: " + tok);
			num = -1;
		}

		List aList = new ArrayList();
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

	public final void addAutoArray(final List aList)
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

	public final void addSelectedArmorProfs(List aList)
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

	public final void addSelectedShieldProfs(List aList)
	{
		if (selectedShieldProfs == null)
		{
			selectedShieldProfs = new ArrayList();
		}
		for (Iterator i = aList.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();
			if (!selectedShieldProfs.contains(aString))
			{
				selectedShieldProfs.add(aString);
			}
		}
	}

	public final ArrayList getSelectedShieldProfs()
	{
		return selectedShieldProfs;
	}

	public void makeChoices()
	{
		getChoices(choiceString, null);
	}

	public final ArrayList getLevelAbilityList()
	{
		return levelAbilityList;
	}

	public boolean removeLevelAbility(final int aLevel, final String aString)
	{
		for (int x = levelAbilityList.size() - 1; x >= 0; --x)
		{
			final LevelAbility ability = (LevelAbility) levelAbilityList.get(x);
			if ((ability.level() == aLevel) && (ability.getList().equals(aString)))
			{
				levelAbilityList.remove(x);
				return true;
			}
		}
		return false;
	}

	public LevelAbility addAddList(final int aLevel, final String aString)
	{
		if (levelAbilityList == null)
		{
			levelAbilityList = new ArrayList();
		}
		if (aString.startsWith(".CLEAR"))
		{
			if (".CLEAR".equals(aString))
			{
				levelAbilityList.clear();
			}
			else if (aString.indexOf(".LEVEL") >= 0)
			{
				int level;
				try
				{
					level = Integer.parseInt(aString.substring(12));
				}
				catch (NumberFormatException e)
				{
					Logging.errorPrint("Badly formed addAddList attribute: " + aString.substring(12));
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
			LevelAbility la = LevelAbility.createAbility(this, aLevel, aString);
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
					Logging.errorPrint("PObject addAddsForLevel");
					canProcess = false;
					List featList = new ArrayList();
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
		activateBonuses();
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



	/**
	 * This method is for the benefit of the LST editor.
	 * VASTLY overhauled 13 August 2003 by sage_sam
	 * for bug #756599
	 */
	public String getSpellListItemAsString(final int idx)
	{
		final List sList = getSpellList();
		if ((sList == null) || (idx >= sList.size()))
		{
			return null;
		}

		PCSpell spell = (PCSpell) sList.get(idx);
		return spell.getPCCText();
	}

	/**
	 * gets the bonuses to a stat based on the stat Index
	 */
	public int getStatMod(int statIdx)
	{
		final List statList = SystemCollections.getUnmodifiableStatList();
		if ((statIdx < 0) || (statIdx >= statList.size()))
		{
			return 0;
		}

		final String aStat = ((PCStat) statList.get(statIdx)).getAbb();
		return (int) bonusTo("STAT", aStat);
	}

	public void setSourceCampaign(String arg)
	{
		sourceCampaign = Globals.getCampaignByFilename(arg, true);
	}

	public void setSourceCampaign(Campaign arg)
	{
		sourceCampaign = arg;
	}

	/**
	 * This method returns a reference to the Campaign that this object
	 * originated from
	 *
	 * @return Campaign instance referencing the file containing the
	 *         source for this object
	 */
	public Campaign getSourceCampaign()
	{
		return sourceCampaign;
	}

	public static boolean modChoices(PObject obj, List availableList, List selectedList, boolean process)
	{
		availableList.clear();
		selectedList.clear();

		final String aChoiceString = obj.getChoiceString();

		if (aChoiceString.startsWith("WEAPONPROF|") || aChoiceString.startsWith("ARMORPROF|") || aChoiceString.startsWith("SHIELDPROF|"))
		{
			obj.getChoices(aChoiceString, null, availableList, selectedList);
			return false;
		}

		StringTokenizer aTok = new StringTokenizer(aChoiceString, "|");
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if ((aTok.countTokens() < 1) || (aPC == null) || aPC.isImporting())
		{
			return false;
		}

		int numChoices = -1;

		double cost = 1.0;
		Feat aFeat = null;
		boolean stacks = false;
		boolean multiples = false;
		if (obj instanceof Feat)
		{
			aFeat = (Feat) obj;
			cost = aFeat.getCost();
			stacks = aFeat.isStacks();
			multiples = aFeat.isMultiples();
		}

		int i;
		int totalPossibleSelections = (int) ((aPC.getFeats() + obj.getAssociatedCount()) / cost);
		if (cost <= 0)
		{
			totalPossibleSelections = (int) (aPC.getFeats() + obj.getAssociatedCount());
		}
		final List uniqueList = new ArrayList();
		final List aBonusList = new ArrayList();
		final List rootArrayList = new ArrayList();
		String choiceType = aTok.nextToken();
		String choiceSec = obj.getName();
		final ChooserInterface chooser = ChooserFactory.getChooserInstance();
		chooser.setPoolFlag(false);		// user is not required to make any changes
		chooser.setAllowsDups(stacks);		// only stackable feats can be duped
		chooser.setVisible(false);
		Iterator iter;
		String title = "Choices";

		int idxSelected = -1;

		int maxNewSelections = (int) (aPC.getFeats() / cost);
		if (cost <= 0)
		{
			maxNewSelections = (int) (aPC.getFeats());
		}
		int requestedSelections = -1;
		for (; ;)
		{
			if (choiceType.startsWith("COUNT="))
			{
				requestedSelections = aPC.getVariableValue(choiceType.substring(6), "").intValue();
			}
			else if (choiceType.startsWith("NUMCHOICES="))
			{
				numChoices = aPC.getVariableValue(choiceType.substring(11), "").intValue();
			}
			else
			{
				break;
			}
			if (!aTok.hasMoreTokens())
			{
				Logging.errorPrint("not enough tokens: " + aChoiceString);
				return false;
			}
			choiceType = aTok.nextToken();
		}

		if (Globals.weaponTypesContains(choiceType))
		{
			title = choiceType + " Weapon Choice";
			final List tArrayList = Globals.getWeaponProfs(choiceType);
			WeaponProf tempProf;
			for (iter = tArrayList.iterator(); iter.hasNext();)
			{
				tempProf = (WeaponProf) iter.next();
				availableList.add(tempProf.getName());
			}

			obj.addAssociatedTo(selectedList);
			//totalPossibleSelections -= (int)(obj.getAssociatedCount() * cost);
		}
		//
		// CHOOSE:COUNT=1|STAT|Con
		//
		else if ("STAT".equals(choiceType))
		{
			title = "Stat Choice";
			final List excludeList = new ArrayList();
			while (aTok.hasMoreTokens())
			{
				final String sExclude = aTok.nextToken();
				int iStat = Globals.getStatFromAbbrev(sExclude);
				if (iStat >= 0)
				{
					excludeList.add(Globals.s_ATTRIBSHORT[iStat]);
				}
			}
			for (int x = 0; x < Globals.s_ATTRIBSHORT.length; ++x)
			{
				if (!excludeList.contains(Globals.s_ATTRIBSHORT[x]))
				{
					availableList.add(Globals.s_ATTRIBSHORT[x]);
				}
			}

			obj.addAssociatedTo(selectedList);
		}
		else if ("SCHOOLS".equals(choiceType))
		{
			title = "School Choice";
			availableList.addAll(SystemCollections.getUnmodifiableSchoolsList());
			obj.addAssociatedTo(selectedList);
		}
		//
		// Thought: Possible future format
		// CHOOSE: SPELLLIST|#|CLASS=xxx,TYPE=xxx,SPELLBOOK=?
		//
		else if ("SPELLLIST".equals(choiceType))
		{
			if (process && (aFeat != null))
			{
				List aList = new ArrayList();
				aList.add("New");
				FeatMultipleChoice fmc;
				StringBuffer sb = new StringBuffer(100);
				for (int j = 0; j < aFeat.getAssociatedCount(); ++j)
				{
					fmc = (FeatMultipleChoice) aFeat.getAssociatedList().get(j);
					sb.append(aFeat.getName()).append(" (");
					sb.append(fmc.getChoiceCount());
					sb.append(" of ").append(fmc.getMaxChoices()).append(") ");
					for (i = 0; i < fmc.getChoiceCount(); ++i)
					{
						if (i != 0)
						{
							sb.append(',');
						}
						sb.append(fmc.getChoice(i));
					}

					aList.add(sb.toString());
					sb.setLength(0);
				}

				Object selectedValue;
				if (aList.size() > 1)
				{
					selectedValue = GuiFacade.showInputDialog(null, "Please select the instance of the feat you wish to" + Constants.s_LINE_SEP + "modify, or New, from the list below.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE, null, aList.toArray(), aList.get(0));
				}
				else
				{
					selectedValue = aList.get(0);
				}
				if (selectedValue == null)
				{
					return false;
				}
				idxSelected = aList.indexOf(selectedValue) - 1;
			}

			boolean needSpellbook;
			switch (aTok.nextToken().charAt(0))
			{
				case '1':
				case 'Y':
					needSpellbook = true;
					break;

				default:
					needSpellbook = false;
					break;
			}

			title = "Spell Choice";
			PObject aClass;
			List classes = null;
			for (int j = 0; ; ++j)
			{
				aClass = aPC.getSpellClassAtIndex(j);
				if (aClass == null)
				{
					break;
				}
				if ((aClass instanceof PCClass) && ((PCClass) aClass).getSpellBookUsed() == needSpellbook)
				{
					if (classes == null)
					{
						classes = new ArrayList();
					}
					classes.add(aClass);
				}
			}

			//
			// Add all spells from all classes that match the spellbook requirement
			// Allow the number of selections to be the maximum allowed by the classes' spell base stat
			//
			if (classes != null)
			{
				maxNewSelections = 0;
				for (int j = 0; j < classes.size(); ++j)
				{
					aClass = (PObject) classes.get(j);
					final List aList = aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), -1);
					for (iter = aList.iterator(); iter.hasNext();)
					{
						final CharacterSpell cs = (CharacterSpell) iter.next();
						final Spell aSpell = cs.getSpell();
						if (!obj.containsAssociated(aSpell.getKeyName()))
						{
							if (!availableList.contains(aSpell.getName()))
							{
								availableList.add(aSpell.getName());
							}
						}
					}
					i = aPC.getStatList().getStatModFor(((PCClass) aClass).getSpellBaseStat());
					if (i > maxNewSelections)
					{
						maxNewSelections = i;
					}
				}

				//
				// Remove all previously selected items from the available list
				//
				final List assocList = obj.getAssociatedList();
				if (assocList != null)
				{
					for (int j = 0; j < assocList.size(); ++j)
					{
						FeatMultipleChoice fmc = (FeatMultipleChoice) assocList.get(j);
						final List choices = fmc.getChoices();
						if (choices != null)
						{
							for (int k = 0; k < choices.size(); ++k)
							{
								if (j == idxSelected)
								{
									selectedList.add(choices.get(k));
								}
								else
								{
									availableList.remove(choices.get(k));
								}
							}
						}
					}
				}

				//
				// Set up remaining choices for pre-existing selection
				//
				if (idxSelected >= 0)
				{
					FeatMultipleChoice fmc = (FeatMultipleChoice) obj.getAssociatedObject(idxSelected);
					requestedSelections = maxNewSelections = fmc.getMaxChoices();
				}
			}
		}
		else if ("SALIST".equals(choiceType))
		{
			// SALIST:Smite|VAR|%|1
			title = "Special Ability Choice";
			PCGIOHandler.buildSALIST(aChoiceString, availableList, aBonusList);
			obj.addAssociatedTo(selectedList);
		}
		else if ("SKILLS".equals(choiceType))
		{
			title = "Skill Choice";
			for (iter = aPC.getSkillList().iterator(); iter.hasNext();)
			{
				final Skill aSkill = (Skill) iter.next();
				availableList.add(aSkill.getName());
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("CSKILLS".equals(choiceType))
		{
			title = "Skill Choice";
			Skill aSkill;
			for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
			{
				aSkill = (Skill) iter.next();
				if (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1)
				{
					availableList.add(aSkill.getName());
				}
			}
			obj.addAssociatedTo(selectedList);
		}

		// SKILLSNAMEDTOCSKILL --- Make one of the named skills a class skill.
		else if ("SKILLSNAMED".equals(choiceType) || "SKILLSNAMEDTOCSKILL".equals(choiceType) || "SKILLSNAMEDTOCCSKILL".equals(choiceType))
		{
			title = "Skill Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				boolean startsWith = false;
				/* TYPE in chooser
					--- arcady 10/21/2001
				*/
				if (aString.startsWith("TYPE.") || aString.startsWith("TYPE="))
				{
					Skill aSkill;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill) e1.next();
						if (aSkill.isType(aString.substring(5)))
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				if ("ALL".equals(aString))
				{
					Skill aSkill;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill) e1.next();
						availableList.add(aSkill.getName());
					}
				}
				if ("CLASS".equals(aString))
				{
					Skill aSkill;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill) e1.next();
						if (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1)
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				if ("CROSSCLASS".equals(aString))
				{
					Skill aSkill;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill) e1.next();
						if (aSkill.costForPCClassList(aPC.getClassList()).intValue() > 1)
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				if ("EXCLUSIVE".equals(aString))
				{
					Skill aSkill;
					for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
					{
						aSkill = (Skill) e1.next();
						if (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 0)
						{
							availableList.add(aSkill.getName());
						}
					}
				}
				if (aString.endsWith("%"))
				{
					startsWith = true;
					aString = aString.substring(0, aString.length() - 1);
				}
				Skill aSkill;
				for (Iterator e1 = Globals.getSkillList().iterator(); e1.hasNext();)
				{
					aSkill = (Skill) e1.next();
					if (aSkill.getKeyName().equals(aString) || (startsWith && aSkill.getKeyName().startsWith(aString)))
					{
						availableList.add(aSkill.getName());
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("SKILLLIST".equals(choiceType) || "CCSKILLLIST".equals(choiceType) || "NONCLASSSKILLLIST".equals(choiceType))
		{
			title = "Skill Choice";
			if (aTok.hasMoreTokens())
			{
				choiceSec = aTok.nextToken();
			}
			if (choiceSec.length() > 0 && !"LIST".equals(choiceSec))
			{
				aTok = new StringTokenizer(choiceSec, ",");
				while (aTok.hasMoreTokens())
				{
					availableList.add(aTok.nextToken());
				}
			}
			else  // if it was LIST
			{
				Skill aSkill;
				for (iter = Globals.getSkillList().iterator(); iter.hasNext();)
				{
					aSkill = (Skill) iter.next();
					if ("NONCLASSSKILLLIST".equals(choiceType) && (aSkill.costForPCClassList(aPC.getClassList()).intValue() == 1 || aSkill.isExclusive()))
					{
						continue; // builds a list of Cross class skills
					}
					final int rootNameLength = aSkill.getRootName().length();
					if (rootNameLength == 0 || aSkill.getRootName().equals(aSkill.getName())) //all skills have ROOTs now, so go ahead and add it if the name and root are identical
					{
						availableList.add(aSkill.getName());
					}
					final boolean rootArrayContainsRootName = rootArrayList.contains(aSkill.getRootName());
					if (rootNameLength > 0 && !rootArrayContainsRootName)
					{
						rootArrayList.add(aSkill.getRootName());
					}
					if (rootNameLength > 0 && rootArrayContainsRootName)
					{
						availableList.add(aSkill.getName());
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("SPELLLEVEL".equals(choiceType))
		{
			// this will need to be re-worked at some point when I can think
			// of a better way.  This feat is different from the others in that
			// it requires a bonus to be embedded in the choice.  Probably this
			// whole feat methodology needs to be re-thought as its getting a bit
			// bloated - a generic way to embed bonuses could be done to simplify
			// this all tremendously instead of so many special cases.
			final StringTokenizer cTok = new StringTokenizer(aChoiceString, "[]");
			final String choices = cTok.nextToken();
			while (cTok.hasMoreTokens())
			{
				aBonusList.add(cTok.nextToken());
			}

			getSpellTypeChoices(choices, availableList, uniqueList); // get appropriate choices for chooser
			obj.addAssociatedTo(selectedList);

			if (process == false)
				availableList = aBonusList;
		}
		else if ("SPELLS".equals(choiceType))
		{
			obj.addAssociatedTo(selectedList);
			while (aTok.hasMoreTokens())
			{
				String line = aTok.nextToken();
				String domainName = "";
				String className = "";
				if (line.startsWith("DOMAIN=") || line.startsWith("DOMAIN."))
				{
					domainName = line.substring(7);
				}
				else if (line.startsWith("CLASS=") || line.startsWith("CLASS."))
				{
					className = line.substring(7);
				}
				// 20 level cap XXX
				for (int lvl = 0; lvl < 20; ++lvl)
				{
					List aList = Globals.getSpellsIn(lvl, className, domainName);
					availableList.addAll(aList);
				}
			}
		}
		else if ("WEAPONFOCUS".equals(choiceType))
		{
			title = "Weapon Focus Choice";
			final Feat wfFeat = aPC.getFeatNamed("Weapon Focus");
			if (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("TYPE."))
				{
					List aList = wfFeat.getAssociatedList();
					String aType = aString.substring(5);
					for (Iterator e = aList.iterator(); e.hasNext();)
					{
						Object aObj = e.next();
						WeaponProf wp;
						wp = Globals.getWeaponProfNamed(aObj.toString());
						if (wp == null)
						{
							continue;
						}
						Equipment eq;
						eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());
						if (eq == null)
						{
							continue;
						}
						if (eq.isType(aType))
						{
							availableList.add(aObj);
						}
					}
				}
			}
			else
			{
				wfFeat.addAssociatedTo(availableList);
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("WEAPONPROFS".equals(choiceType))
		{
			title = "Weapon Prof Choice";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if ("LIST".equals(aString))
				{
					String bString;
					for (Iterator setIter = aPC.getWeaponProfList().iterator(); setIter.hasNext();)
					{
						bString = (String) setIter.next();
						if (!availableList.contains(bString))
						{
							availableList.add(bString);
						}
					}
				}
				else if (aString.equals("DEITYWEAPON"))
				{
					if (aPC.getDeity() != null)
					{
						String weaponList = aPC.getDeity().getFavoredWeapon();
						if ("ALL".equalsIgnoreCase(weaponList) || "ANY".equalsIgnoreCase(weaponList))
						{
							weaponList = Globals.getWeaponProfNames("|", false);
						}

						StringTokenizer bTok = new StringTokenizer(weaponList, "|");
						while (bTok.hasMoreTokens())
						{

							String bString = bTok.nextToken();
							availableList.add(bString);
						}
					}
				}
				else if (aString.startsWith("Size."))
				{
					if (aPC.sizeInt() >= Globals.sizeInt(aString.substring(5, 6)) && aPC.getWeaponProfList().contains(aString.substring(7)) && !availableList.contains(aString.substring(7)))
					{
						availableList.add(aString.substring(7));
					}
				}
				else if (aString.startsWith("WSize."))
				{
					String bString;
					WeaponProf wp;
					StringTokenizer bTok = new StringTokenizer(aString, ".");
					bTok.nextToken(); // should be WSize
					String sString = bTok.nextToken(); // should be Light, 1 handed, 2 handed choices above
					List typeList = new ArrayList();
					while (bTok.hasMoreTokens()) // any additional constraints
					{
						String dString = bTok.nextToken().toUpperCase();
						typeList.add(dString);
					}
					for (Iterator setIter = aPC.getWeaponProfList().iterator(); setIter.hasNext();)
					{
						bString = (String) setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
						{
							continue;
						}

						//
						// get an Equipment object based on the named WeaponProf
						//
						Equipment eq = EquipmentList.getEquipmentNamed(wp.getName());
						if (eq == null)
						{
							//
							// Sword (Bastard/Exotic), Sword (Bastard/Martial), Katana (Martial), Katana(Exotic)
							//
							int len = 0;
							if (bString.endsWith("Exotic)"))
							{
								len = 7;
							}
							if ((len == 0) && bString.endsWith("Martial)"))
							{
								len = 8;
							}
							if (len != 0)
							{
								if (bString.charAt(bString.length() - len - 1) == '/')
								{
									++len;
								}
								String tempString = bString.substring(0, bString.length() - len) + ")";
								if (tempString.endsWith("()"))
								{
									tempString = tempString.substring(0, tempString.length() - 3).trim();
								}
								eq = EquipmentList.getEquipmentNamed(tempString);

							}
							else
							{
								//
								// Couldn't find equipment with matching name, look for 1st weapon that uses it

								//
								for (Iterator eqIter = EquipmentList.getEquipmentList().iterator(); eqIter.hasNext();)
								{
									final Equipment tempEq = (Equipment) eqIter.next();
									if (tempEq.isWeapon())
									{
										if (tempEq.profName().equals(wp.getName()))
										{
											eq = tempEq;
											break;
										}
									}
								}
							}
						}

						boolean isValid = false; // assume we match unless...
						if (eq != null)
						{
							if (typeList.size() == 0)
							{
								isValid = true;
							}
							else
							{
								//
								// search all the optional type strings, just one match passes the test
								//
								for (Iterator wpi = typeList.iterator(); wpi.hasNext();)
								{
									final String wpString = (String) wpi.next();
									if (eq.isType(wpString))
									{
										isValid = true; // if it contains even one of the TYPE strings, it passes
										break;
									}
								}
							}
						}
						if (!isValid)
						{
							continue;
						}

						if (!availableList.contains(bString))
						{
							if ("Light".equals(sString) && Globals.isWeaponLightForPC(aPC, eq))
							{
								availableList.add(bString);
							}
							if ("1 handed".equals(sString) && Globals.isWeaponOneHanded(aPC, eq, wp))
							{
								availableList.add(bString);
							}
							if ("2 handed".equals(sString) && Globals.isWeaponTwoHanded(aPC, eq, wp))
							{
								availableList.add(bString);
							}
						}
					}
				}
				else if (aString.startsWith("SpellCaster."))
				{
					if (aPC.isSpellCaster(1) && !availableList.contains(aString.substring(12)))
					{
						availableList.add(aString.substring(12));
					}
				}
				else if (aString.startsWith("ADD."))
				{
					if (!availableList.contains(aString.substring(4)))
					{
						availableList.add(aString.substring(4));
					}
				}
				else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE="))
				{
					String sString = aString.substring(5);
					boolean adding = true;
					Iterator setIter = aPC.getWeaponProfList().iterator();
					if (sString.startsWith("Not."))
					{
						sString = sString.substring(4);
						setIter = availableList.iterator();
						adding = false;
					}
					String bString;
					WeaponProf wp;
					Equipment eq;
					while (setIter.hasNext())
					{
						bString = (String) setIter.next();
						wp = Globals.getWeaponProfNamed(bString);
						if (wp == null)
						{
							continue;
						}
						eq = EquipmentList.getEquipmentKeyed(wp.getKeyName());
						if (eq == null)
						{
							if (!wp.isType("Natural"))	//natural weapons are not in the global eq.list
							{
								continue;
							}

							if (adding && !availableList.contains(wp.getName()))
							{
								availableList.add(wp.getName());
							}
						}
						else if (eq.typeStringContains(sString))
						{
							// if this item is of the desired type, add it to the list
							if (adding && !availableList.contains(wp.getName()))
							{
								availableList.add(wp.getName());
							}
							// or try to remove it and reset the iterator since remove cause fits
							else if (!adding && availableList.contains(wp.getName()))
							{
								availableList.remove(wp.getName());
								setIter = availableList.iterator();
							}
						}
						else if (sString.equalsIgnoreCase("LIGHT"))
						{
							// if this item is of the desired type, add it to the list
							if (adding && !availableList.contains(wp.getName()) && Globals.isWeaponLightForPC(aPC, eq))
							{
								availableList.add(wp.getName());
							}
							// or try to remove it and reset the iterator since remove cause fits
							else if (!adding && availableList.contains(wp.getName()) && Globals.isWeaponLightForPC(aPC, eq))
							{
								availableList.remove(wp.getName());
								setIter = availableList.iterator();
							}
						}
					}
				}
				else
				{
					if (aPC.getWeaponProfList().contains(aString) && !availableList.contains(aString))
					{
						availableList.add(aString);
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("HP".equals(choiceType))
		{
			if (aTok.hasMoreTokens())
			{
				choiceSec = aTok.nextToken();
			}
			availableList.add(choiceSec);
			for (int e1 = 0; e1 < obj.getAssociatedCount(); ++e1)
			{
				selectedList.add(choiceSec);
			}
		}
		else if (choiceType.startsWith("FEAT=") || choiceType.startsWith("FEAT."))
		{
			final Feat theFeat = aPC.getFeatNamed(choiceType.substring(5));
			if (theFeat != null)
			{
				theFeat.addAssociatedTo(availableList);
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("FEATLIST".equals(choiceType))
		{
			obj.addAssociatedTo(selectedList);
			String aString;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
				{
					aString = aString.substring(5);
					if (!stacks && availableList.contains(aString))
					{
						continue;
					}
					for (Iterator e1 = aPC.aggregateFeatList().iterator(); e1.hasNext();)
					{
						final Feat theFeat = (Feat) e1.next();
						if (theFeat.isType(aString) && (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
						{
							availableList.add(theFeat.getName());
						}
					}
				}
				else if (aPC.getFeatNamed(aString) != null)
				{
					if (stacks || (!stacks && !availableList.contains(aString)))
					{
						availableList.add(aString);
					}
				}
			}
		}
		else if ("FEATSELECT".equals(choiceType))
		{
			obj.addAssociatedTo(selectedList);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
				{
					aString = aString.substring(5);
					if (!stacks && availableList.contains(aString))
					{
						continue;
					}
					for (int z = 0; z < Globals.getFeatList().size(); ++z)
					{
						final Feat theFeat = Globals.getFeatListFeat(z);
						if (theFeat.isType(aString) && (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
						{
							availableList.add(theFeat.getName());
						}
					}
				}
				else
				{
					Feat theFeat = Globals.getFeatNamed(aString);
					if (theFeat != null)
					{
						String subName = "";
						if (!aString.equalsIgnoreCase(theFeat.getName()))
						{
							subName = aString.substring(theFeat.getName().length());
							aString = theFeat.getName();
							final int idx = subName.indexOf('(');
							if (idx > -1)
							{
								subName = subName.substring(idx + 1);
							}
						}
						if (theFeat.isMultiples())
						{
							//
							// If already have taken the feat, use it so we can remove
							// any choices already selected
							//
							final Feat pcFeat = aPC.getFeatNamed(aString);
							if (pcFeat != null)
							{
								theFeat = pcFeat;
							}

							int percIdx = subName.indexOf('%');
							if (percIdx > -1)
							{
								subName = subName.substring(0, percIdx);
							}
							else if (subName.length() != 0)
							{
								int idx = subName.lastIndexOf(')');
								if (idx > -1)
								{
									subName = subName.substring(0, idx);
								}
							}

							List xavailableList = new ArrayList();	// available list of choices
							List xselectedList = new ArrayList();		// selected list of choices
							theFeat.modChoices(true, xavailableList, xselectedList, false);

							//
							// Remove any that don't match
							//
							if (subName.length() != 0)
							{
								for (int n = xavailableList.size() - 1; n >= 0; --n)
								{
									final String xString = (String) xavailableList.get(n);
									if (!xString.startsWith(subName))
									{
										xavailableList.remove(n);
									}
								}
								//
								// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
								// If you have no ranks in Craft (Basketweaving), the available list will be empty
								//
								// Make sure that the specified feat is available, even though it does not meet the prerequisite
								//
								if ((percIdx == -1) && (xavailableList.size() == 0))
								{
									xavailableList.add(aString + "(" + subName + ")");
								}
							}
							//
							// Remove any already selected
							//
							if (!theFeat.isStacks())
							{
								for (Iterator e = xselectedList.iterator(); e.hasNext();)
								{
									int idx = xavailableList.indexOf(e.next().toString());
									if (idx > -1)
									{
										xavailableList.remove(idx);
									}
								}
							}
							for (Iterator e = xavailableList.iterator(); e.hasNext();)
							{
								availableList.add(aString + "(" + (String) e.next() + ")");
							}
						}
						else
						{
							availableList.add(aString);
						}
					}
				}
			}
		}
		else if ("FEATADD".equals(choiceType))
		{
			title = "Add a Feat";
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
				{
					String featType = aString.substring(5);
					for (int z = 0; z < Globals.getFeatList().size(); ++z)
					{
						final Feat theFeat = Globals.getFeatListFeat(z);
						if (theFeat.isType(featType) && (stacks || (!stacks && !availableList.contains(theFeat.getName()))))
						{
							if (Globals.getFeatNamed(theFeat.getName()).passesPreReqToGain())
							{
								if ((Globals.getFeatNamed(theFeat.getName()).isStacks()) || (aPC.getFeatNamed(theFeat.getName()) == null))
								{
									availableList.add(theFeat.getName());
								}
							}
						}
					}
				}
				else
				{
					StringTokenizer bTok = new StringTokenizer(aString, ",");
					String featName = bTok.nextToken().trim();
					String subName = "";
					aFeat = Globals.getFeatNamed(featName);

					if (aFeat == null)
					{
						Logging.errorPrint("Feat not found: " + featName);
						return false;
					}

					if (!featName.equalsIgnoreCase(aFeat.getName()))
					{
						subName = featName.substring(aFeat.getName().length());
						featName = aFeat.getName();
						int si = subName.indexOf('(');
						if (si > -1)
						{
							subName = subName.substring(si + 1);
						}
					}

					if (aFeat.passesPreReqToGain())
					{
						if (aFeat.isMultiples())
						{
							//
							// If already have taken the feat, use it so we can remove
							// any choices already selected
							//
							final Feat pcFeat = aPC.getFeatNamed(featName);
							if (pcFeat != null)
							{
								aFeat = pcFeat;
							}

							final int percIdx = subName.indexOf('%');
							if (percIdx > -1)
							{
								subName = subName.substring(0, percIdx);
							}
							else if (subName.length() != 0)
							{
								final int idx = subName.lastIndexOf(')');
								if (idx > -1)
								{
									subName = subName.substring(0, idx);
								}
							}

							final List aavailableList = new ArrayList();	// available list of choices
							final List sselectedList = new ArrayList();		// selected list of choices
							aFeat.modChoices(true, availableList, selectedList, false);

							//
							// Remove any that don't match
							//
							if (subName.length() != 0)
							{
								for (int n = aavailableList.size() - 1; n >= 0; --n)
								{
									String bString = (String) aavailableList.get(n);
									if (!bString.startsWith(subName))
									{
										aavailableList.remove(n);
									}
								}
								//
								// Example: ADD:FEAT(Skill Focus(Craft (Basketweaving)))
								// If you have no ranks in Craft (Basketweaving), the available list will be empty
								//
								// Make sure that the specified feat is available, even though it does not meet the prerequisite
								//
								if ((percIdx == -1) && (aavailableList.size() == 0))
								{
									aavailableList.add(subName);
								}
							}
							//
							// Remove any already selected
							//
							if (!aFeat.isStacks())
							{
								for (Iterator e = sselectedList.iterator(); e.hasNext();)
								{
									int idx = aavailableList.indexOf(e.next().toString());
									if (idx > -1)
									{
										aavailableList.remove(idx);
									}
								}
							}
							for (Iterator e = aavailableList.iterator(); e.hasNext();)
							{
								availableList.add(featName + "(" + (String) e.next() + ")");
							}
							return false;
						}
						else if (!aPC.hasFeat(featName) && !aPC.hasFeatAutomatic(featName))
						{
							availableList.add(aString);
						}
					}
				}
			}
			//process == true;
		}
		else if ("SPELLCLASSES".equals(choiceType))
		{
			title = "Spellcaster Classes";
			PCClass aClass;
			for (iter = aPC.getClassList().iterator(); iter.hasNext();)
			{
				aClass = (PCClass) iter.next();
				if (!aClass.getSpellBaseStat().equals(Constants.s_NONE))
				{
					availableList.add(aClass.getName());
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else if ("ARMORTYPE".equals(choiceType))
		{
			title = "Armor Type Choice";
			String temptype;
			for (int z = 0; z < Globals.getFeatList().size(); ++z)
			{
				final Feat theFeat = Globals.getFeatListFeat(z);
				if (theFeat.getName().startsWith("Armor Proficiency ("))
				{
					int idxbegin = theFeat.getName().indexOf("(");
					int idxend = theFeat.getName().indexOf(")");
					temptype = theFeat.getName().substring((idxbegin + 1), idxend);
					if (aPC.getFeatNamed(theFeat.getName()) != null)
					{
						availableList.add(temptype);
					}
				}
			}
			obj.addAssociatedTo(selectedList);
		}
		else
		{
			title = "Selections";
			availableList.add(choiceType);
			String aString;
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (stacks || (!stacks && !availableList.contains(aString)))
				{
					availableList.add(aString);
				}
			}
			obj.addAssociatedTo(selectedList);
		}

		if (!process)
		{
			return false;
		}

		if (requestedSelections < 0)
		{
			requestedSelections = maxNewSelections;
		}
		else
		{
			requestedSelections -= selectedList.size();
			requestedSelections = Math.min(requestedSelections, maxNewSelections);
		}

		final int preSelectedSize = selectedList.size();
		if (numChoices > 0)
		{
			//
			// Make sure that we don't try to make the user choose more selections than are available
			// or we'll be in an infinite loop...
			//
			numChoices = Math.min(numChoices, availableList.size() - preSelectedSize);
			requestedSelections = numChoices;
		}
		chooser.setPool(requestedSelections);

		title = title + " (" + obj.getName() + ')';
		chooser.setTitle(title);
		Globals.sortChooserLists(availableList, selectedList);
		for (; ;)
		{
			chooser.setAvailableList(availableList);
			chooser.setSelectedList(selectedList);
			chooser.show();

			final int selectedSize = chooser.getSelectedList().size() - preSelectedSize;
			if (numChoices > 0)
			{
				if (selectedSize != numChoices)
				{
					GuiFacade.showMessageDialog(null, "You must make " + (numChoices - selectedSize) + " more selection(s).", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
					continue;
				}
			}
			break;
		}

		if ("SPELLLIST".equals(choiceType))
		{
			final double x = aPC.getFeats();
			if (idxSelected >= 0)
			{
				obj.removeAssociated(idxSelected);
				if (chooser.getSelectedList().size() == 0)
				{
					aPC.setFeats(x + 1);
				}
			}
			else if (chooser.getSelectedList().size() != 0)
			{
				aPC.setFeats(x - 1);
			}
		}
		else if ("SALIST".equals(choiceType))
		{
			//
			// remove previous selections from special abilities
			// aBonusList contains all possible selections in form: <displayed info>|<special ability>
			//
			for (int e = 0; e < obj.getAssociatedCount(); ++e)
			{
				String aString = obj.getAssociated(e);
				final String prefix = aString + "|";
				for (int x = 0; x < aBonusList.size(); ++x)
				{
					final String bString = (String) aBonusList.get(x);
					if (bString.startsWith(prefix))
					{
						obj.removeBonus(bString.substring(bString.indexOf('|') + 1), "");
						break;
					}
				}
			}
		}

		if ("SKILLSNAMEDTOCSKILL".equals(choiceType))
		{
			for (iter = aFeat.getCSkillList().iterator(); iter.hasNext();)
			{
				final String tempString = (String) iter.next();
				if (!"LIST".equals(tempString) /*&& !Globals.getFeatNamed(obj.getName()).getCSkillList().contains(tempString)*/)
				{
					final Feat bFeat = Globals.getFeatNamed(obj.getName());
					if (bFeat != null)
					{
						if (bFeat.getCSkillList() != null)
						{
							if (bFeat.getCSkillList().contains(tempString))
							{
								iter.remove();
							}
						}
					}
				}
			}
			aFeat.setCcSkillList(".CLEAR");
		}

		if (!"SPELLLIST".equals(choiceType))
		{
			obj.clearAssociated();
		}

		String objPrefix = "";
		if (obj instanceof Domain)
		{
			objPrefix = choiceType + '?';
		}

		FeatMultipleChoice fmc = null;
		for (i = 0; i < chooser.getSelectedList().size(); ++i)
		{
			final String chosenItem = (String) chooser.getSelectedList().get(i);
			if ("HP".equals(choiceType))
			{
				//obj.addAssociated(objPrefix + "CURRENTMAX");
				obj.addAssociated(objPrefix + chosenItem);
			}
			else if ("SPELLLEVEL".equals(choiceType))
			{
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					String bString = (String) e.next();
					obj.addAssociated(objPrefix + chosenItem);
					obj.applyBonus(bString, chosenItem);
				}
			}
			else if ("SPELLLIST".equals(choiceType))
			{
				if (fmc == null)
				{
					fmc = new FeatMultipleChoice();
					fmc.setMaxChoices(maxNewSelections);
					obj.addAssociated(fmc);
				}
				fmc.addChoice(chosenItem);
			}
			else if ("ARMORTYPE".equals(choiceType))
			{
				for (Iterator e = aBonusList.iterator(); e.hasNext();)
				{
					String bString = (String) e.next();
					obj.addAssociated(objPrefix + chosenItem);
					obj.applyBonus("ARMORPROF=" + bString, chosenItem);
				}
			}
			else if (multiples && !stacks)
			{
				if (!obj.containsAssociated(objPrefix + chosenItem))
				{
					obj.addAssociated(objPrefix + chosenItem);
				}
			}
			else
			{
				final String prefix = chosenItem + "|";
				obj.addAssociated(objPrefix + chosenItem);
				// SALIST: aBonusList contains all possible selections in form: <displayed info>|<special ability>
				for (int x = 0; x < aBonusList.size(); ++x)
				{
					final String bString = (String) aBonusList.get(x);
					if (bString.startsWith(prefix))
					{
						obj.addBonusList(bString.substring(bString.indexOf('|') + 1));
						break;
					}
				}
			}

			if (aFeat != null)
			{
				if ("SKILLLIST".equals(choiceType) || "SKILLSNAMEDTOCSKILL".equals(choiceType) || "NONCLASSSKILLLIST".equals(choiceType))
				{
					if (rootArrayList.contains(chosenItem))
					{
						for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
						{
							final Skill aSkill = (Skill) e2.next();
							if (aSkill.getRootName().equalsIgnoreCase(chosenItem))
							{
								aFeat.setCSkillList(aSkill.getName());
							}
						}
					}
					else
					{
						aFeat.setCSkillList(chosenItem);
					}
				}
				else if ("CCSKILLLIST".equals(choiceType) || "SKILLSNAMEDTOCCSKILL".equals(choiceType))
				{
					if (rootArrayList.contains(chosenItem))
					{
						for (Iterator e2 = Globals.getSkillList().iterator(); e2.hasNext();)
						{
							final Skill aSkill = (Skill) e2.next();
							if (aSkill.getRootName().equalsIgnoreCase(chosenItem))
							{
								aFeat.setCcSkillList(aSkill.getName());
							}
						}
					}
					else
					{
						aFeat.setCcSkillList(chosenItem);
					}
				}
				else if ("FEATADD".equals(choiceType))
				{
					if (!aPC.hasFeat(chosenItem))
					{
						aPC.setFeats(aPC.getFeats() + 1);
					}
					aPC.modFeat(chosenItem, true, false);
				}
			}

			if (Globals.weaponTypesContains(choiceType))
			{
				aPC.addWeaponProf(objPrefix + chosenItem);
			}
		}
		if (!"SPELLLIST".equals(choiceType))
		{
			double featCount = aPC.getFeats();
			if (numChoices > 0)
			{
				if (cost > 0)
				{
					featCount -= cost;
				}
			}
			else
			{
				if (cost > 0)
				{
					featCount = ((totalPossibleSelections - selectedList.size()) * cost);
				}
			}
			aPC.setFeats(featCount);
		}

		//
		// This will get assigned by autofeat (if a feat)
		//
		if (objPrefix.length() != 0)
		{
			aPC.setAutomaticFeatsStable(false);
		}
		return true;
	}

}
