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
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>ACCalculator</code>.
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */
public class ACCalculator
{

	private Hashtable acBonusTable = new Hashtable();

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
	 *
	 * <br>author: Thomas Behr 07-02-02
	 */
	public ACCalculator(PlayerCharacter aPC)
	{
		setPlayerCharacter(aPC);
	}

	/**
	 * @param aPC
	 *
	 * <br>author: Thomas Behr 09-03-02
	 */
	public void setPlayerCharacter(PlayerCharacter aPC)
	{
		this.aPC = aPC;
	}

	/**
	 * freeing up resources
	 *
	 * <br>author: Thomas Behr 07-02-02
	 */
	public void dispose()
	{
		acBonusTable.clear();
		acBonusTable = null;
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
		/*
		 * updated for new AC calculations
		 * author: Thomas Behr 07-02-01
		 */
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
	 * calculate all AC modifiers based on dexterity
	 *
	 * <br>author: Thomas Behr 01-01-02
         *
         * @return the AC modifier due to dexterity
	 */
	public int acModFromDexterity()
	{
		int acmod = 0;
		acmod = aPC.calcStatMod(Constants.DEXTERITY);

		// check for load/weight limits
		if (Globals.isApplyLoadPenaltyToACandSkills())
		{
			int load = Globals.loadTypeForStrength(aPC.adjStats(Constants.STRENGTH), aPC.totalWeight());
			if (load == Constants.MEDIUM_LOAD)
			{
				if (acmod > 3)
				{
					acmod = 3;
				}
			}
			else if (load == Constants.HEAVY_LOAD)
			{
				if (acmod > 1)
				{
					acmod = 1;
				}
			}
			else if (load == Constants.OVER_LOAD)
			{
				if (acmod > 0)
				{
					acmod = 0;
				}
			}
		}

		// check for equipment limits
		Equipment eq;
		for (Iterator it = aPC.getEquipmentList().values().iterator(); it.hasNext();)
		{
			eq = (Equipment)it.next();

			if (eq.isEquipped() == true)
			{

				int max = eq.getMaxDex().intValue();
				if (acmod > max)
				{
					acmod = max;
				}
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
		/*
		 * updated for new AC calculations
		 * author: Thomas Behr 07-02-01
		 */
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
		/*
		 * updated for new AC calculations
		 * author: Thomas Behr 07-02-01
		 */
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
		return calculateACBonusRestrictiveBySourceByType("RACE.TEMPLATE", true, "NATURAL", true);
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
		/*
		 * updated for new AC calculations
		 * author: Thomas Behr 07-02-01
		 */
		return calculateACBonusByType("NATURAL", null);
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
	 * @param type   specifying the equipment type (not the bonus type!!!)
         *
         * @return the AC modifier due to equipment of a type type
	 */
	private int _calculateACBonusFromEquipmentByType(String type)
	{
		int acmod = 0;

		calculateACBonusRestrictiveBySourceByType(null, false, null, false);

		for (Iterator mapIter = aPC.getEquipmentList().values().iterator(); mapIter.hasNext();)
		{
			Equipment eq = (Equipment)mapIter.next();
			if (eq.isEquipped() && eq.isType(type.toUpperCase()))
			{
				String aType;
				String bonus;
				StringTokenizer aTok;

				ArrayList bonusList = new ArrayList();
				bonusList.addAll(eq.getBonusList());
                                bonusList.addAll(generateEffectiveBoni(eq.getEqModifierList(true)));
                                bonusList.addAll(generateEffectiveBoni(eq.getEqModifierList(false)));

				for (Iterator it = bonusList.iterator(); it.hasNext();)
				{
					bonus = (String)it.next();

					int index = bonus.indexOf("COMBAT|AC|");
					if (index > -1)
					{
						aTok = new StringTokenizer(bonus, "|");

						/*
						 * since we parse an Equipment bonus list
						 * we can't get that special case for PCClass
						 * so everything should work fine
						 */
						aTok.nextToken();
						aTok.nextToken();
						int aBonus = Integer.parseInt(aTok.nextToken());

						aType = null;
						while (aTok.hasMoreTokens())
						{
							aType = aTok.nextToken();
							if (aType.startsWith("TYPE="))
							{
								aType = aType.substring(5).toUpperCase();
								break;
							}
							else
							{
								aType = null;
							}
						}

						if (aType != null)
						{

							if (((aType.indexOf("ARMOR.REPLACE") > -1) &&
								(acBonusArmorMax.indexOf("REPLACE") > -1)) ||
								((aType.indexOf("ARMOR.ENHANCEMENT") > -1) &&
								(acBonusArmorMax.indexOf("ENHANCEMENT") > -1)) ||
								((aType.indexOf(".") == -1) &&
								(acBonusArmorMax.equals(""))) ||
								(aType.indexOf(".STACK") > -1))
							{
								acmod += aBonus;
							}

						}
						else
						{
							acmod += aBonus;
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
			/*
			 * updated for new AC calculations
			 * author: Thomas Behr 07-02-01
			 */
			acmod += calculateACBonusRestrictiveByType(type.toUpperCase(), true);
		}
		// get bonus of this type from specified PObject
		else
		{
			int classLevel = (pObject instanceof PCClass) ? ((PCClass)pObject).getLevel().intValue() : 0;
			String aType = type.toUpperCase();

			StringTokenizer aTok;
			for (Iterator it = pObject.getBonusList().iterator(); it.hasNext();)
			{
				String bonus = ((String)it.next()).toUpperCase();

				int index = bonus.indexOf("COMBAT|AC|");
				if ((index > -1) && (bonus.indexOf("TYPE=" + aType) > -1))
				{
					acmod = 0;
					aTok = new StringTokenizer(bonus, "|");

					// normal case
					if (index == 0)
					{
						aTok.nextToken();
						aTok.nextToken();
						acmod += Integer.parseInt(aTok.nextToken());
					}
					// PCClass case, e.g. monks with x|COMBAT|AC|WISMAX0
					else
					{
						int requiredLevel = Integer.parseInt(aTok.nextToken());
						aTok.nextToken();
						aTok.nextToken();

						if (requiredLevel - 1 < classLevel)
						{
							acmod += aPC.getVariableValue(aTok.nextToken(), "").intValue();
						}
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
		acmod += calculateACBonusRestrictiveByType("ARMOR.NATURAL", false);

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
			if (((String)it.next()).endsWith("Dex bonus to AC)"))
				return calculateACBonusTotal();
		}

		// we obviously do NOT not keep our dexterity bonus to AC,
                // but we must apply dexterity penalties to AC!
		return calculateACBonusRestrictiveByType("NOTFLATFOOTED", false) + Math.min(0, acModFromDexterity());
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

		String del;
		String aKey;
		String aType;
		String aSource;
		String keyForTotal;
		String[] tokens;
		StringBuffer aBuffer;
		StringTokenizer aTok;
		Hashtable tempTable = new Hashtable();

		ArrayList sourceList = new ArrayList();
		ArrayList typeList = new ArrayList();
		// parse source
		if (s != null)
		{
			aTok = new StringTokenizer(s, ".");
			while (aTok.hasMoreTokens())
			{
				sourceList.add(aTok.nextToken());
			}
		}
		// parse type
		if (t != null)
		{
			aTok = new StringTokenizer(t, ".");
			while (aTok.hasMoreTokens())
			{
				typeList.add(aTok.nextToken());
			}
		}

		// go through the boni which are stored according to source
		// merge them according to type and stacking rules!
		for (Iterator it = acBonusTable.keySet().iterator(); it.hasNext();)
		{
			aKey = (String)it.next();

			del = "";
			aBuffer = new StringBuffer();
			aTok = new StringTokenizer(aKey, ".");
			tokens = new String[aTok.countTokens()];

			tokens[0] = aTok.nextToken();
			for (int i = 1; i < tokens.length; i++)
			{
				tokens[i] = aTok.nextToken();
				aBuffer.append(del).append(tokens[i]);
				del = ".";
			}

			aSource = tokens[0];
			if (tokens.length > 1)
			{
				aType = tokens[1];
			}
			else
			{
				aType = "";
			}

			if (((!sb && !sourceList.contains(aSource)) || (sb && sourceList.contains(aSource))) &&
				((!tb && !typeList.contains(aType)) || (tb && typeList.contains(aType))))
			{

				if (aBuffer.length() == 0)
				{
					acmod += ((Integer)acBonusTable.get(aKey)).intValue();
				}
				else
				{

					keyForTotal = aBuffer.toString();
					if (tempTable.get(keyForTotal) == null)
					{
						tempTable.put(keyForTotal, acBonusTable.get(aKey));
					}
					else
					{
						int newVal = 0;
						newVal += ((Integer)acBonusTable.get(aKey)).intValue();
						newVal += ((Integer)tempTable.get(keyForTotal)).intValue();
						tempTable.put(keyForTotal, new Integer(newVal));
					}
				}
			}
		}

		// reset our marker needed to remember max(ARMOR, ARMOR.REPLACE)
		acBonusArmorMax = "";

		// go through the boni which are stored
		// according to type, type.REPLACE, type.STACK
		// add them up considering max(type, type.REPLACE)+type.STACK
		ArrayList keys = new ArrayList(tempTable.keySet());
		for (Iterator it = keys.iterator(); it.hasNext();)
		{
			aKey = (String)it.next();

			if (aKey.endsWith("STACK"))
			{
				acmod += ((Integer)tempTable.get(aKey)).intValue();
			}
			else
			{
				int enhanceVal = 0;
				int replaceVal = 0;
				int stdVal = 0;

				aTok = new StringTokenizer(aKey, ".");
				aKey = aTok.nextToken();

				if (tempTable.get(aKey) != null)
				{
					stdVal += ((Integer)tempTable.get(aKey)).intValue();
					tempTable.remove(aKey);
				}

//  				aKey = aKey + "." + "REPLACE";

				if (tempTable.get(aKey + "." + "REPLACE") != null)
				{
					replaceVal += ((Integer)tempTable.get(aKey + "." + "REPLACE")).intValue();
					tempTable.remove(aKey + "." + "REPLACE");
				}

				if (tempTable.get(aKey + "." + "ENHANCEMENT") != null)
				{
					enhanceVal += ((Integer)tempTable.get(aKey + "." + "ENHANCEMENT")).intValue();
					tempTable.remove(aKey + "." + "ENHANCEMENT");
				}

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
	public int calculateACBonusRestrictiveBySource(String s, boolean b)
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
	public int calculateACBonusRestrictiveByType(String t, boolean b)
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
	 * @param  src     descriptor for splitting AC,
	 *                            needed by export tokens,
	 *                            e.g. "CLASS", "RACE", "TEMPLATE",
	 *                                 "EQUIP", etc.
	 */
	private void _extractACBonusFromEquipment(Equipment equip)
	{
                _extractACBonusFromList(generateEffectiveBoni(equip.getEqModifierList(true)), "EQUIP");
//                  _extractACBonusFromList(generateEffectiveBoni(equip.getEqModifierList(false)), "EQUIP");
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
                if (pObject instanceof PCClass) {
                        _extractACBonusFromPCClass((PCClass)pObject);
                } else {
                        _extractACBonusFromList(pObject.getBonusList(), src);
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
                
		StringTokenizer aTok;
		StringBuffer aBuffer;
		String aKey;
		String aType;
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
                                        acmod += aPC.getVariableValue(aTok.nextToken(), "").intValue();
                                }

				aType = null;
				while (aTok.hasMoreTokens())
				{
					aType = aTok.nextToken();
					if (aType.startsWith("TYPE="))
					{
						aBuffer.append(".");
						aBuffer.append(aType = aType.substring(5).toUpperCase());
						break;
					}
					else
					{
						aType = null;
					}
				}

				aKey = aBuffer.toString();

				// consider stacking, REPLACE, STACK
				if (acBonusTable.get(aKey) != null)
				{

					if (aKey.indexOf(".REPLACE") > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else if (aKey.indexOf(".STACK") > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else if (aKey.indexOf(".ENHANCEMENT") > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else if ((aType == null) ||
						Globals.getBonusStackList().indexOf(aType) > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else
					{
						acmod = Math.max(acmod, ((Integer)acBonusTable.get(aKey)).intValue());
					}
				}

				acBonusTable.put(aKey, new Integer(acmod));
			}
		}
	}

	/**
	 * extract AC boni from a specific (bonus) list
	 *
	 * <br>author: Thomas Behr 04-02-02
	 *
	 * @param bonusList   the list to beparsed
	 * @param src         descriptor for splitting AC,
	 *                    needed by export tokens,
	 *                    e.g. "RACE", "TEMPLATE", "EQUIP", etc.
	 */
	private void _extractACBonusFromList(List bonusList, String src)
	{
		StringTokenizer aTok;
		StringBuffer aBuffer;
		String aKey;
		String aType;
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

				/*
                                 * normal case
                                 * we used to differentiate here between PCClass and other PObject
                                 * code was moved to _extractACBonusFromPCClass(PCClass pcClass)
                                 *
                                 * author: Thomas Behr 12-03-02
                                 */
                                aTok.nextToken();
                                aTok.nextToken();
                                acmod += Integer.parseInt(aTok.nextToken());

				aType = null;
				while (aTok.hasMoreTokens())
				{
					aType = aTok.nextToken();
					if (aType.startsWith("TYPE="))
					{
						aBuffer.append(".");
						aBuffer.append(aType = aType.substring(5).toUpperCase());
						break;
					}
					else
					{
						aType = null;
					}
				}

				aKey = aBuffer.toString();

				// consider stacking, REPLACE, STACK
				if (acBonusTable.get(aKey) != null)
				{

					if (aKey.indexOf(".REPLACE") > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else if (aKey.indexOf(".STACK") > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else if (aKey.indexOf(".ENHANCEMENT") > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else if ((aType == null) ||
						Globals.getBonusStackList().indexOf(aType) > -1)
					{
						acmod += ((Integer)acBonusTable.get(aKey)).intValue();
					}
					else
					{
						acmod = Math.max(acmod, ((Integer)acBonusTable.get(aKey)).intValue());
					}
				}

				acBonusTable.put(aKey, new Integer(acmod));
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
		acBonusTable.clear();
		int acmod = 0;

		// get boni due to deity
		if (aPC.getDeity() != null)
		{
			_extractACBonusFromPObject(aPC.getDeity(), "DEITY");
		}

		// get boni due to race
		_extractACBonusFromPObject(aPC.getRace(), "RACE");

		// this is the racial base AC
		final String aKeyRaceNotype = "RACE";
		acmod = aPC.getRace().getStartingAC().intValue();
		if (acmod > 0)
		{

			if (acBonusTable.get(aKeyRaceNotype) == null)
			{
				acBonusTable.put(aKeyRaceNotype, new Integer(acmod));
			}
			else
			{
				acmod += ((Integer)acBonusTable.get(aKeyRaceNotype)).intValue();
				acBonusTable.put(aKeyRaceNotype, new Integer(acmod));
			}
		}

		// this is needed for monster advancement
		// ... MM p.12 right column "Size increases"
		final String aKeyRaceNatural = "RACE.NATURAL";
		acmod = aPC.naturalArmorModForSize();
		if (acmod > 0)
		{

			if (acBonusTable.get(aKeyRaceNatural) == null)
			{
				acBonusTable.put(aKeyRaceNatural, new Integer(acmod));
			}
			else
			{
				acmod += ((Integer)acBonusTable.get(aKeyRaceNatural)).intValue();
				acBonusTable.put(aKeyRaceNatural, new Integer(acmod));
			}
		}

		// get boni due to templates
		final String aKeyTemplateNatural = "TEMPLATE.NATURAL";
		for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
		{
			final PCTemplate aTemplate = (PCTemplate)it.next();
//                          _extractACBonusFromPObject( aTemplate , "TEMPLATE" );
			acmod = aTemplate.getNaturalArmor(aPC.totalLevels(), aPC.totalHitDice(), aPC.getSize());
			if (acmod > 0)
			{

				if (acBonusTable.get(aKeyTemplateNatural) == null)
				{
					acBonusTable.put(aKeyTemplateNatural, new Integer(acmod));
				}
				else
				{
					acmod += ((Integer)acBonusTable.get(aKeyTemplateNatural)).intValue();
					acBonusTable.put(aKeyTemplateNatural, new Integer(acmod));
				}
			}
		}

		// get boni due to classes
		final String aKeyClassNotype = "CLASS";
		for (Iterator it = aPC.getClassList().iterator(); it.hasNext();)
		{
			final PCClass aClass = (PCClass)it.next();
//  			_extractACBonusFromPObject(aClass, "CLASS");
			_extractACBonusFromPCClass(aClass);
			acmod = Integer.parseInt(aClass.getACForLevel(aClass.getLevel().intValue()));
			if (acmod > 0)
			{

				if (acBonusTable.get(aKeyClassNotype) == null)
				{
					acBonusTable.put(aKeyClassNotype, new Integer(acmod));
				}
				else
				{
					acmod += ((Integer)acBonusTable.get(aKeyClassNotype)).intValue();
					acBonusTable.put(aKeyClassNotype, new Integer(acmod));
				}
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
		for (Iterator it = aPC.getEquipmentList().values().iterator(); it.hasNext();)
		{
			final Equipment aEquip = (Equipment)it.next();
			if (aEquip.isEquipped())
			{
//  				_extractACBonusFromPObject(aEquip, "EQUIP");
				_extractACBonusFromEquipment(aEquip);
			}
		}

		// iterate over all boni and zero out non-stacking ones
		// which occur multiple times
		// at the same time add up stacking boni
		String aKey;
		String aType;
		String keyForMax;
		StringTokenizer aTok;
		Hashtable tempTable = new Hashtable();

		ArrayList keys = new ArrayList(acBonusTable.keySet());
		for (Iterator it = keys.iterator(); it.hasNext();)
		{
			aKey = it.next().toString();
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

			// non-stacking boni
			if ((aKey.indexOf(".REPLACE") + aKey.indexOf(".STACK") == -2) &&
				(aType != null) && (Globals.getBonusStackList().indexOf(aType) == -1))
			{

				keyForMax = (String)tempTable.get(aType);
				if (keyForMax == null)
				{
					tempTable.put(aType, aKey);
				}
				else
				{
					int oldVal = ((Integer)acBonusTable.get(keyForMax)).intValue();
					int newVal = ((Integer)acBonusTable.get(aKey)).intValue();
					if (oldVal < newVal)
					{
						acBonusTable.remove(keyForMax);
						tempTable.put(aType, aKey);
					}
					else
					{
						acBonusTable.remove(aKey);
					}
				}
			}
		}
	}

        /**
         * generates a list of effective (AC) boni,
         * i.e. more than one EquipmentModifiers add a bonus of
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
                        if (nameIndex > -1) {
                                
                                if (typeIndex > -1) {

                                        aTok = new StringTokenizer(bonus, "|");
                                
                                        aTok.nextToken();
                                        aTok.nextToken();
                                        int aBonus = Integer.parseInt(aTok.nextToken());
                                        
                                        if (aBonus > max) {
                                                max = aBonus;
                                                maxBonus = bonus;
                                        }

                                        it.remove();
                                }
                        } else {
                                // we don't need to keep non-AC related boni
                                it.remove();
                        }
                }
                
                if (maxBonus != null) {
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
                for (Iterator it = eqModList.iterator(); it.hasNext();) {

                        aEqMod = (EquipmentModifier)it.next();
                        /*
                         * this does not seem to work.
                         * why????
                         */
                        if (!willIgnore(aEqMod, eqModList)) {
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
