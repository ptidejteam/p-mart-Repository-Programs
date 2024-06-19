/*
 * ACCalculator.java
 * Copyright 2001 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 07, 2002, 5:30 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:05:26 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import pcgen.core.character.CompanionMod;

/**
 * <code>ACCalculator</code>.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */
public class ACCalculator
{

	private ArmorClassBonusMap acBonusMap = new ArmorClassBonusMap();

	private String acBonusArmorMax = "";

	private PlayerCharacter aPC;

	/**
	 * Constructor
	 *
	 * <br>author: Thomas Behr 07-02-02
	 */
	public ACCalculator()
	{
		this(Globals.getCurrentPC());
	}

	/**
	 * Constructor
	 * @param argPC
	 *
	 * <br>author: Thomas Behr 07-02-02
	 */
	public ACCalculator(PlayerCharacter argPC)
	{
		setPlayerCharacter(argPC);
	}

	/**
	 * @param argPC
	 *
	 * <br>author: Thomas Behr 09-03-02
	 */
	private void setPlayerCharacter(PlayerCharacter argPC)
	{
		this.aPC = argPC;
	}

	/**
	 * freeing up resources
	 *
	 * <br>author: Thomas Behr 07-02-02
	 */
	public void dispose()
	{
		acBonusMap.clear();
		acBonusMap = null;
		aPC = null;
	}

	/*
 * ###################################################################
 * ac modification methods
 * ###################################################################
 */

	/**
	 * calculate all AC modifiers based on equipped armors
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @return the AC modifier due to equipped armors
	 */
	public int acModFromArmor()
	{
		return _calculateACBonusFromEquipmentByType("ARMOR");
	}

	/**
	 * calculate all AC modifiers based on equipped shields
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @return the AC modifier due to equipped shields
	 */
	public int acModFromShield()
	{
		return _calculateACBonusFromEquipmentByType("SHIELD");
	}

	/**
	 * calculate all AC modifiers based on equipment
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @return the AC modifier due to equipment
	 */
	public int acModFromEquipment()
	{
		return calculateACBonusRestrictiveBySource("EQUIP", true);
	}

	/**
	 * calculate all AC modifiers based on size
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @return the AC modifier due to size
	 */
	public int acModFromSize()
	{
		return aPC.modForSize();
	}

	/**
	 * calculate all AC modifiers based on dexterity, this respects
	 * both load limits and limits set by any equipped equipment
	 *
	 * <br>author: Thomas Behr 01-01-02
	 *
	 * @return the AC modifier due to dexterity
	 */
	public int acModFromDexterity()
	{
		int acmod = aPC.getStatBonusTo("MISC", "MAXDEX");
		// check for load/weight limits
		if (SettingsHandler.isApplyLoadPenaltyToACandSkills())
		{
			int load = Globals.loadTypeForLoadScore(aPC.getVariableValue("LOADSCORE", "").intValue(), aPC.totalWeight());
			int maxDexBonus = Integer.MAX_VALUE;  // Ie, no limit
			switch (load)
			{
				case Constants.MEDIUM_LOAD:
					maxDexBonus = 3;
					break;
				case Constants.HEAVY_LOAD:
					maxDexBonus = 1;
					break;
				case Constants.OVER_LOAD:
					maxDexBonus = 0;
					break;
				case Constants.LIGHT_LOAD:
					break;
				default:
					Globals.errorPrint("In ACCalculator.acModFromDexterity the load " + load + " is not supported.");
					break;
			}
			acmod = Math.min(maxDexBonus, acmod);
		}

		// check for equipment limits
		Equipment eq;
		Iterator it = aPC.getEquipmentList().iterator();
		while (it.hasNext())
		{
			eq = (Equipment)it.next();
			if (eq.isEquipped())
			{
				acmod = Math.min(acmod, eq.getMaxDex().intValue());
			}
		}

		return acmod;
	}

	/**
	 * calculate all AC modifiers based on character class
	 *
	 * <br>author: Thomas Behr 04-01-02, 07-02-02
	 *
	 * @return the AC modifier due to classes
	 */
	public int acModFromClass()
	{
		return calculateACBonusRestrictiveBySource("CLASS", true);
	}

	/**
	 * calculate all AC modifiers based on character abilities
	 *
	 * <br>author: Thomas Behr 01-01-02, 03-01-02, 07-02-02
	 *
	 * @return the AC modifier due to abilities, i.e dex, special abilites
	 */
	public int acModFromAbilities()
	{
		int acmod = 0;

		acmod += acModFromDexterity();
		acmod += calculateACBonusRestrictiveBySource("CLASS.FEAT.SKILL", true);

		return acmod;
	}

	/**
	 * calculate AC modifiers of TYPE=NATURAL based on race and templates
	 *
	 * <br>author: Thomas Behr 03-01-02
	 *
	 * @return the natural AC modifier due to race and templates
	 */
	public int acModFromNaturalBySource()
	{
		Globals.debugPrint("was " + calculateACBonusRestrictiveBySourceByType("RACE.TEMPLATE", true, "NATURAL", true) + " now=" + calculateACBonusRestrictiveBySourceByType("RACE.TEMPLATE", true, "NaturalArmor", true));

		return calculateACBonusRestrictiveBySourceByType("RACE.TEMPLATE", true, "NaturalArmor", true);
	}

	/**
	 * calculate all natural AC modifiers
	 *
	 * <br>author: Thomas Behr 03-01-02, 07-02-01
	 *
	 * @return the natural AC modifier
	 */
	public int acModFromNatural()
	{
		Globals.debugPrint("was " + calculateACBonusByType("NaturalArmor", null) + " now=" + calculateACBonusByType("NaturalArmor", null));

		return calculateACBonusByType("NaturalArmor", null);
	}

	/*
         * ###################################################################
         * ac calculation methods
         * ###################################################################
         */

	/**
	 * calculate all AC modifiers based on equipped equipment types
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @param type   specifying the equipment type (not the bonus type!!!)	  *
	 * @return the AC modifier due to equipment of a type type
	 */
	private int _calculateACBonusFromEquipmentByType(String type)
	{
		int acmod = 0;

		calculateACBonusRestrictiveBySourceByType(null, false, null, false);

		for (Iterator e = aPC.getEquipmentList().iterator(); e.hasNext();)
		{
			Equipment eq = (Equipment)e.next();
			if (eq.isEquipped() && eq.isType(type.toUpperCase()))
			{
				String aType;
				String bonus;
				String aToken;
				StringTokenizer aTok;
				ArrayList aPrereqList = new ArrayList();
				ArrayList bonusList = new ArrayList();
				bonusList.addAll(eq.getBonusList());
				bonusList.addAll(generateEffectiveBoni(eq.getEqModifierList(true)));
				bonusList.addAll(generateEffectiveBoni(eq.getEqModifierList(false)));

				for (Iterator it = bonusList.iterator(); it.hasNext();)
				{
					bonus = (String)it.next();

					if (bonus.indexOf("COMBAT|AC|") > -1)
					{
						aTok = new StringTokenizer(bonus, "|");
						aPrereqList.clear();

						/*
						* since we parse an Equipment bonus list
						* we can't get that special case for PCClass
						* so everything should work fine
						*/
						aTok.nextToken();
						aTok.nextToken();
						int aBonus = aPC.getVariableValue(aTok.nextToken(), "").intValue();

						aType = null;
						while (aTok.hasMoreTokens())
						{
							aToken = aTok.nextToken();
							if (aToken.startsWith("TYPE="))
							{
								aType = aToken.substring(5).toUpperCase();
							}
							else if (aToken.startsWith("PRE"))
							{
								aPrereqList.add(aToken);
							}
						}

						if (eq.passesPreReqTestsForList(aPC, null, aPrereqList))
						{
							if ((aType == null) ||
							  ((aType.indexOf("ARMOR.REPLACE") > -1) &&
							  (acBonusArmorMax.indexOf("REPLACE") > -1))
							  ||
							  ((aType.indexOf("ARMOR.ENHANCEMENT") > -1) &&
							  (acBonusArmorMax.indexOf("ENHANCEMENT") > -1))
							  ||
							  ((aType.indexOf(".") == -1) &&
							  (acBonusArmorMax.equals("")))
							  ||
							  (aType.indexOf(".STACK") > -1))
							{
								acmod += aBonus;
							}
						}
					}
				}
			}
		}

		return acmod;
	}

	/**
	 * calculate AC boni of a specific type
	 *
	 * <br>author: Thomas Behr 04-01-02
	 *
	 * @param  type      bonus type; this is case sensitive!!
	 * @param  pObject   if this is null,
	 *                            the total bonus of the specified type
	 *                            will be calculated,<br>
	 *                            else only the bonus from the specified
	 *                            pObject is considered
	 *
	 * @return the AC modifier due to type type of pObject
	 */
	public int calculateACBonusByType(String type, PObject pObject)
	{
		int acmod = 0;

		// get total bonus of this type
		if (pObject == null)
		{
			acmod += calculateACBonusRestrictiveByType(type.toUpperCase(), true);
		}
		// get bonus of this type from specified PObject
		else
		{
			String src = "";
			int classLevel = 0;
			if (pObject instanceof PCClass)
			{
				final PCClass aClass = (PCClass)pObject;
				classLevel = aClass.getLevel().intValue();
				src = "Class:" + aClass.getName();
			}

			String aType = type.toUpperCase();

			ArrayList aPrereqList = new ArrayList();
			StringTokenizer aTok;
			for (Iterator it = pObject.getBonusList().iterator(); it.hasNext();)
			{
				String bonus = ((String)it.next()).toUpperCase();

				int index = bonus.indexOf("COMBAT|AC|");
				if ((index > -1) && (bonus.indexOf("TYPE=" + aType) > -1))
				{
					int aBonus = 0;

					aTok = new StringTokenizer(bonus, "|");
					aPrereqList.clear();

					// normal case
					if (index == 0)
					{
						aTok.nextToken();
						aTok.nextToken();
						aBonus += aPC.getVariableValue(aTok.nextToken(), src).intValue();
					}
					// PCClass case, e.g. monks with x|COMBAT|AC|WISMAX0
					else
					{
						int requiredLevel = Integer.parseInt(aTok.nextToken());
						aTok.nextToken();
						aTok.nextToken();

						if (requiredLevel - 1 < classLevel)
						{
							aBonus += aPC.getVariableValue(aTok.nextToken(), src).intValue();
						}
					}

					String aToken;
					while (aTok.hasMoreTokens())
					{
						aToken = aTok.nextToken();
						if (aToken.startsWith("PRE"))
						{
							aPrereqList.add(aToken);
						}
					}

					if (pObject.passesPreReqTestsForList(aPC, null, aPrereqList))
					{
						acmod += aBonus;
					}
				}
			}
		}

		return acmod;
	}

	/**
	 * calculate touch AC
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @return the touch AC
	 */
	public int calculateACBonusTouch()
	{
		int acmod = 0;

		acmod += acModFromSize();
		acmod += acModFromDexterity();

		/*
		* PHB, p.119 says
		* "... [touch] AC does not include any armor bonus,
		*  shield bonus, or natural armor bonus.
		*/
		acmod += calculateACBonusRestrictiveByType("ARMOR.NATURALARMOR", false);

		return acmod;
	}

	/**
	 * calculate total AC boni
	 *
	 * <br>author: Thomas Behr 04-02-02
	 *
	 * @return the total AC
	 */
	public int calculateACBonusTotal()
	{
		int acmod = 0;

		acmod += acModFromSize();
		acmod += acModFromDexterity();

		acmod += calculateACBonusRestrictiveByType(null, false);

		return acmod;
	}

	/**
	 * calculate flat-footed AC
	 *
	 * <br>author: Thomas Behr 07-02-02, 05-03-02
	 *
	 * @return the flat-footed AC
	 */
	public int calculateACBonusFlatFooted()
	{
		// check if we may keep our dexterity bonus to AC
		for (Iterator it = aPC.getSpecialAbilityList().iterator(); it.hasNext();)
		{
			if (((SpecialAbility)it.next()).getName().endsWith("Dex bonus to AC)"))
				return calculateACBonusTotal();
		}

		// we obviously do NOT not keep our dexterity bonus to AC,
		// but we must apply dexterity penalties to AC!
		return calculateACBonusRestrictiveByType("NOTFLATFOOTED", false) +
		  Math.min(0, acModFromDexterity()) +
		  acModFromSize();
	}

	/**
	 * calculate AC boni restrictively
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @param  s    a point separated list of bonus sources,
	 *                       e.g. "CLASS.EQUIP"
	 * @param  sb   if sb is true, only the sources in s will be
	 *                       summed in;<br>
	 *                       if b is false, all but the sources in s will be
	 *                       summed in
	 * @param  t    a point separated list of bonus types,
	 *                       e.g. "ARMOR.NATURAL"
	 * @param  tb   if tb is true, only the types in t will be
	 *                       summed in;<br>
	 *                       if b is false, all but the types in t will be
	 *                       summed in
	 *
	 * @return the resulting AC modifier
	 */
	public int calculateACBonusRestrictiveBySourceByType(String s, boolean sb,
	  String t, boolean tb)
	{
		int acmod = 0;

		_extractACBonus();

		String aKey;
		String aType;
		String aSource;
		StringBuffer aBuffer;
		StringTokenizer aTok;
		ArmorClassBonusMap tempMap = new ArmorClassBonusMap();

		Set sourceList = Utility.getSetFromString(s);
		Set typeList = Utility.getSetFromString(t);

		// go through the boni which are stored according to source
		// merge them according to type and stacking rules!
		for (Iterator it = acBonusMap.keySet().iterator(); it.hasNext();)
		{
			aKey = (String)it.next();
			aTok = new StringTokenizer(aKey, ".");
			aSource = aTok.nextToken();
			aType = (aTok.hasMoreTokens()) ? aTok.nextToken() : "";
			aBuffer = new StringBuffer(aType);
			while (aTok.hasMoreTokens())
			{
				aBuffer.append(".").append(aTok.nextToken());
			}

			if (Utility.xnor(sb, sourceList.contains(aSource)) &&
			  Utility.xnor(tb, typeList.contains(aType)))
			{
				if (aBuffer.length() == 0)
				{
					acmod += acBonusMap.get(aKey);
				}
				else
				{
					tempMap.add(aBuffer.toString(), acBonusMap.get(aKey));
				}
			}
		}

		// reset our marker needed to remember max(ARMOR, ARMOR.REPLACE)
		acBonusArmorMax = "";

		// go through the boni which are stored
		// according to type, type.REPLACE, type.STACK
		// add them up considering max(type, type.REPLACE)+type.STACK
		Set seen = new HashSet();
		Iterator it = tempMap.keySet().iterator();
		while (it.hasNext())
		{
			aKey = (String)it.next();

			if (aKey.endsWith("STACK"))
			{
				acmod += tempMap.get(aKey);
			}
			else
			{
				int enhanceVal = 0;
				int replaceVal = 0;
				int stdVal = 0;

				int pos = aKey.indexOf(".");
				if (pos > -1)
				{
					aKey = aKey.substring(0, pos);
				}
				if (seen.contains(aKey))
				{
					continue; // already processed this key
				}
				else
				{
					seen.add(aKey);
				}

				stdVal += tempMap.get(aKey);
				replaceVal += tempMap.get(aKey + "." + "REPLACE");
				enhanceVal += tempMap.get(aKey + "." + "ENHANCEMENT");

				if (aKey.indexOf("ARMOR") == -1)
				{
					acmod += Math.max(replaceVal + enhanceVal, stdVal);
				}
				else
				{
					if (replaceVal + enhanceVal > stdVal)
					{
						acmod += replaceVal + enhanceVal;
						acBonusArmorMax = "ENHANCEMENT|REPLACE";
					}
					else
					{
						acmod += stdVal;
					}
				}
			}
		}

		return acmod;
	}

	/**
	 * calculate AC boni restrictively
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @param  s   a point separated list of bonus sources,
	 *                      e.g. "CLASS.EQUIP"
	 * @param  b   if b is true, only the sources in s will be
	 *                      summed in;<br>
	 *                      if b is false, all but the sources in s will be
	 *                      summed in
	 *
	 * @return the resulting AC modifier
	 */
	private int calculateACBonusRestrictiveBySource(String s, boolean b)
	{
		return calculateACBonusRestrictiveBySourceByType(s, b, null, false);
	}

	/**
	 * calculate AC boni restrictively
	 *
	 * <br>author: Thomas Behr 07-02-02
	 *
	 * @param  t   a point separated list of bonus types,
	 *                      e.g. "ARMOR.NATURAL"
	 * @param  b   if b is true, only the types in t will be
	 *                      summed in;<br>
	 *                      if b is false, all but the types in t will be
	 *                      summed in
	 *
	 * @return the resulting AC modifier
	 */
	private int calculateACBonusRestrictiveByType(String t, boolean b)
	{
		return calculateACBonusRestrictiveBySourceByType(null, false, t, b);
	}

	/*
	* ###################################################################
	* ac bonus extraction methods
	* ###################################################################
	*/

	/**
	 * extract AC boni from a specific Equipment instance's bonusList
	 * and its EquipmentModifiers bonusLists
	 *
	 * <br>author: Thomas Behr 04-02-02
	 *
	 * @param  equip   this instance's bonusList is parsed
	 */
	private void _extractACBonusFromEquipment(Equipment equip)
	{
		_extractACBonusFromList(equip, generateEffectiveBoni(equip.getEqModifierList(true)), "EQUIP");
//                  _extractACBonusFromList(equip, generateEffectiveBoni(equip.getEqModifierList(false)), "EQUIP");
		_extractACBonusFromPObject(equip, "EQUIP");
	}

	/**
	 * extract AC boni from a specific PObject instance's bonusList
	 *
	 * <br>author: Thomas Behr 04-02-02
	 *
	 * @param  pObject   this instance's bonusList is parsed
	 * @param  src       descriptor for splitting AC,
	 *                            needed by export tokens,
	 *                            e.g. "CLASS", "RACE", "TEMPLATE",
	 *                                 "EQUIP", etc.
	 */
	private void _extractACBonusFromPObject(PObject pObject, String src)
	{
		if (pObject instanceof PCClass)
		{
			_extractACBonusFromPCClass((PCClass)pObject);
		}
		else
		{
			_extractACBonusFromList(pObject, pObject.getBonusList(), src);
		}
	}

	/**
	 * extract AC boni from a specific PCClass instance's bonusList
	 *
	 * <br>author: Thomas Behr 04-02-02
	 *
	 * @param  aClass   this instance's bonusList is parsed
	 */
	private void _extractACBonusFromPCClass(PCClass aClass)
	{
		final String src = "CLASS";
		final String varSrc = "CLASS:" + aClass.getName();

		ArrayList aPrereqList = new ArrayList();

		StringTokenizer aTok;
		StringBuffer aBuffer;
		String aKey;
		String aType;
		String aToken;
		int acmod;

		int classLevel = aClass.getLevel().intValue();

		for (Iterator it = aClass.getBonusList().iterator(); it.hasNext();)
		{
			String bonus = (String)it.next();

			int index = bonus.indexOf("COMBAT|AC|");
			if (index > -1)
			{

				aBuffer = new StringBuffer();
				aBuffer.append(src);

				acmod = 0;
				aTok = new StringTokenizer(bonus, "|");
				aPrereqList.clear();

				/*
				* PCClass case, e.g. monks with x|COMBAT|AC|WISMAX0
				* we used to differentiate here between PCClass and other PObject
				* code was moved to _extractACBonusFromList(List bonusList, String src)
				*
				* author: Thomas Behr 12-03-02
				*/
				int requiredLevel = Integer.parseInt(aTok.nextToken());
				aTok.nextToken();
				aTok.nextToken();

				if (requiredLevel - 1 < classLevel)
				{
//                                          acmod += aPC.getVariableValue(aTok.nextToken(), "").intValue();
					acmod += aPC.getVariableValue(aTok.nextToken(), varSrc).intValue();
				}

				aType = null;
				while (aTok.hasMoreTokens())
				{
					aToken = aTok.nextToken();
					if (aToken.startsWith("TYPE="))
					{
						aBuffer.append(".");
						aBuffer.append(aType = aToken.substring(5).toUpperCase());
					}
					else if (aToken.startsWith("PRE"))
					{
						aPrereqList.add(aToken);
					}
				}

				aKey = aBuffer.toString();

				// consider stacking, REPLACE, STACK
				if (acBonusMap.containsKey(aKey))
				{

					if ((aKey.indexOf(".REPLACE") > -1) ||
					  (aKey.indexOf(".STACK") > -1) ||
					  (aKey.indexOf(".ENHANCEMENT") > -1))
					{
						acmod += acBonusMap.get(aKey);
					}
					else if ((aType == null) ||
					  Globals.getBonusStackList().indexOf(aType) > -1)
					{
						acmod += acBonusMap.get(aKey);
					}
					else
					{
						acmod = Math.max(acmod, acBonusMap.get(aKey));
					}
				}

				if (aClass.passesPreReqTestsForList(aPC, null, aPrereqList))
				{
					acBonusMap.put(aKey, acmod);
				}
			}
		}
	}

	/**
	 * extract AC boni from a specific (bonus) list
	 *
	 * <br>author: Thomas Behr 04-02-02
	 *
	 * @param pObject     the PObject instance used to call the
	 *                    <code>passesPreReqTestsForList</code> method
	 * @param bonusList   the list to be parsed
	 * @param src         descriptor for splitting AC,
	 *                    needed by export tokens,
	 *                    e.g. "RACE", "TEMPLATE", "EQUIP", etc.
	 */
	private void _extractACBonusFromList(PObject pObject, List bonusList, String src)
	{
		if (bonusList == null || bonusList.isEmpty())
			return;
		ArrayList aPrereqList = new ArrayList();

		StringTokenizer aTok;
		StringBuffer aBuffer;
		String aKey;
		String aType;
		String aToken;
		int acmod;

		for (Iterator it = bonusList.iterator(); it.hasNext();)
		{
			String bonus = (String)it.next();

			int index = bonus.indexOf("COMBAT|AC|");
			if (index > -1)
			{

				aBuffer = new StringBuffer();
				aBuffer.append(src);

				acmod = 0;
				aTok = new StringTokenizer(bonus, "|");
				aPrereqList.clear();

				/*
				* normal case
				* we used to differentiate here between PCClass and other PObject
				* code was moved to _extractACBonusFromPCClass(PCClass pcClass)
				*
				* author: Thomas Behr 12-03-02
				*/
				aTok.nextToken(); //TODO: Which token gets thrown away here?
				aTok.nextToken(); //TODO: Which token gets thrown away here?
				acmod += aPC.getVariableValue(aTok.nextToken(), "").intValue();

				aType = null;
				while (aTok.hasMoreTokens())
				{
					aToken = aTok.nextToken();
					if (aToken.startsWith("TYPE="))
					{
						aBuffer.append(".");
						aBuffer.append(aType = aToken.substring(5).toUpperCase());
					}
					else if (aToken.startsWith("PRE"))
					{
						aPrereqList.add(aToken);
					}
				}

				aKey = aBuffer.toString();

				// consider stacking, REPLACE, STACK
				if (acBonusMap.containsKey(aKey))
				{

					if (aKey.indexOf(".REPLACE") > -1)
					{
						acmod += acBonusMap.get(aKey);
					}
					else if (aKey.indexOf(".STACK") > -1)
					{
						acmod += acBonusMap.get(aKey);
					}
					else if (aKey.indexOf(".ENHANCEMENT") > -1)
					{
						acmod += acBonusMap.get(aKey);
					}
					else if ((aType == null) ||
					  Globals.getBonusStackList().indexOf(aType) > -1)
					{
						acmod += acBonusMap.get(aKey);
					}
					else
					{
						acmod = Math.max(acmod, acBonusMap.get(aKey));
					}
				}

				if (pObject.passesPreReqTestsForList(aPC, null, aPrereqList))
				{
					acBonusMap.put(aKey, acmod);
				}
			}
		}
	}

	/**
	 * extract all boni of type COMBAT|AC|... into bonusTable
	 *
	 * <br>author: Thomas Behr 04-02-02
	 */
	private void _extractACBonus()
	{
		acBonusMap.clear();

		// get boni due to deity
		if (aPC.getDeity() != null)
		{
			_extractACBonusFromPObject(aPC.getDeity(), "DEITY");
		}

		// get boni due to race
		_extractACBonusFromPObject(aPC.getRace(), "RACE");

		// this is the racial base AC
		final String aKeyRaceNotype = "RACE";
		int acmod = aPC.getRace().getStartingAC().intValue();
		if (acmod > 0)
		{

			acBonusMap.add(aKeyRaceNotype, acmod);
		}

		// this is needed for monster advancement
		// ... MM p.12 right column "Size increases"
		final String aKeyRaceNatural = "RACE.NATURAL";
		acmod = aPC.naturalArmorModForSize();
		if (acmod > 0)
		{

			acBonusMap.add(aKeyRaceNatural, acmod);
		}

		// get boni due to templates
		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
		{
			_extractACBonusFromPObject((PCTemplate)it.next(), "TEMPLATE");
		}

		// get boni due to classes
		final String aKeyClassNotype = "CLASS";
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			final PCClass aClass = (PCClass)it.next();
			final String src = "CLASS:" + aClass.getName();

			_extractACBonusFromPCClass(aClass);
			acmod = aPC.getVariableValue(aClass.getACForLevel(aClass.getLevel().intValue()), src).intValue();
			if (acmod > 0)
			{
				acBonusMap.add(aKeyClassNotype, acmod);
			}
		}

		// get boni due to domains
		for (Iterator it = aPC.getCharacterDomainList().iterator(); it.hasNext();)
		{
			final Domain aDomain = ((CharacterDomain)it.next()).getDomain();
			if (aDomain != null)
				_extractACBonusFromPObject(aDomain, "DOMAIN");
		}

		// get boni due to feats
		for (Iterator it = aPC.aggregateFeatList().iterator(); it.hasNext();)
		{
			final Feat aFeat = (Feat)it.next();
			_extractACBonusFromPObject(aFeat, "FEAT");
		}

		// get boni due to skills
		for (Iterator it = aPC.getSkillList().iterator(); it.hasNext();)
		{
			final Skill aSkill = (Skill)it.next();
			if (aSkill.getRank().intValue() > 0)
			{
				_extractACBonusFromPObject(aSkill, "SKILL");
			}
		}

		// get boni due to equipment
		for (Iterator it = aPC.getEquipmentList().iterator(); it.hasNext();)
		{
			final Equipment aEquip = (Equipment)it.next();
			if (aEquip.isEquipped())
			{
				_extractACBonusFromEquipment(aEquip);
			}
		}

		// get boni due to CompanionMod's
		for (Iterator it = aPC.getCompanionModList().iterator(); it.hasNext();)
		{
			final CompanionMod aComp = (CompanionMod)it.next();
			_extractACBonusFromPObject(aComp, "COMPANION");
		}


		// iterate over all boni and zero out non-stacking ones
		// which occur multiple times
		// at the same time add up stacking boni
		String aKey;
		String aType;
		String keyForMax;
		StringTokenizer aTok;
		Hashtable tempTable = new Hashtable();

		for (Iterator it = acBonusMap.keySet().iterator(); it.hasNext();)
		{
			aKey = (String)it.next();
			aTok = new StringTokenizer(aKey, ".");
			aTok.nextToken();
			if (aTok.hasMoreTokens())
			{
				aType = aTok.nextToken();
			}
			else
			{
				aType = null;
			}

			/*
			 * bug fix
			 * this makes ARMOR.ENHANCEMENT effectively a stacking boni,
			 * but ENHANCEMENT should only occur for customized equipment
			 * so the generateEffectiveBoni(...)-method should take care
			 * of that problem
			 *
			 * author: Thomas Behr 27-03-02
			 */
			// non-stacking boni
			if ((aKey.indexOf(".REPLACE") + aKey.indexOf(".STACK") + aKey.indexOf(".ENHANCEMENT") == -3) &&
			  (aType != null) && (Globals.getBonusStackList().indexOf(aType) == -1))
			{
				keyForMax = (String)tempTable.get(aType);
				if (keyForMax == null)
				{
					tempTable.put(aType, aKey);
				}
				else
				{
					int oldVal = acBonusMap.get(keyForMax);
					int newVal = acBonusMap.get(aKey);
					if (oldVal < newVal)
					{
						//The following line is illegal when using an iterator. As far as I can tell this should work anyway.
						//It might be a bit slower, but at least it won't throw up ConcurrentModificationExceptions all over the place.
						//acBonusMap.remove(keyForMax);
						tempTable.put(aType, aKey);
					}
					else
					{
						it.remove();
					}
				}
			}
		}
	}

	/**
	 * generates a list of effective (AC) boni,
	 * i.e. if more than one EquipmentModifier adds a bonus of
	 * type Armor.ENHANCEMENT only the highest one counts
	 *
	 * <br>author: Thomas Behr 11-03-01
	 *
	 * @param  eqModList   list of EquipmentModifiers to test
	 * @return a list of effective (AC) boni
	 */
	private List generateEffectiveBoni(List eqModList)
	{
		ArrayList effectiveBoni = new ArrayList();
		for (Iterator it = generateEffectiveModifiers(eqModList).iterator(); it.hasNext();)
		{
			effectiveBoni.addAll(((EquipmentModifier)it.next()).getBonusList());
		}
		// now remove all boni of type Armor.ENHANCEMENT, but the maximum
		int max = Integer.MIN_VALUE;
		String bonus;
		String maxBonus = null;
		StringTokenizer aTok;
		for (Iterator it = effectiveBoni.iterator(); it.hasNext();)
		{
			bonus = (String)it.next();
			int nameIndex = bonus.toUpperCase().indexOf("COMBAT|AC|");
			int typeIndex = bonus.toUpperCase().indexOf("TYPE=ARMOR.ENHANCEMENT");
			if (nameIndex > -1)
			{

				if (typeIndex > -1)
				{

					aTok = new StringTokenizer(bonus, "|");

					aTok.nextToken();
					aTok.nextToken();
//                                          int aBonus = Integer.parseInt(aTok.nextToken());
					int aBonus = aPC.getVariableValue(aTok.nextToken(), "").intValue();

					if (aBonus > max)
					{
						max = aBonus;
						maxBonus = bonus;
					}

					it.remove();
				}
			}
			else
			{
				// we don't need to keep non-AC related boni
				it.remove();
			}
		}

		if (maxBonus != null)
		{
			effectiveBoni.add(maxBonus);
		}

		return effectiveBoni;
	}

	/**
	 * generates a list of effective EquipmentModifiers,
	 * i.e. any EquipmentModifiers in the specified list that should be ignored
	 * will be removed
	 *
	 * <br>author: Thomas Behr 11-03-01
	 *
	 * @param  eqModList   list of EquipmentModifiers to test
	 * @return a list of effective EquipmentModifiers
	 */
	private List generateEffectiveModifiers(List eqModList)
	{
		ArrayList effectiveMods = new ArrayList();

		EquipmentModifier aEqMod;
		for (Iterator it = eqModList.iterator(); it.hasNext();)
		{

			aEqMod = (EquipmentModifier)it.next();
			/*
			 * this does not seem to work.
			 * why????
			 */
			if (!willIgnore(aEqMod, eqModList))
			{
				effectiveMods.add(aEqMod);
			}
		}

		return effectiveMods;
	}

	/**
	 * checks, if a specified EquipmentModifier should be ignored
	 *
	 * <br>author: Thomas Behr 11-03-01
	 *
	 * @param  eqMod       key name of an EquipmentModifier
	 * @param  eqModList   list of EquipmentModifiers to test
	 * @return <code>true</code>, if the specified modifier should be ignored;<br>
	 *         <code>false</code>, otherwise
	 */
	private boolean willIgnore(EquipmentModifier eqMod, List eqModList)
	{
		String aEqModKey = eqMod.getKeyName();
		for (Iterator it = eqModList.iterator(); it.hasNext();)
		{
			if (((EquipmentModifier)it.next()).willIgnore(aEqModKey))
			{
				return true;
			}
		}
		return false;
	}
}
