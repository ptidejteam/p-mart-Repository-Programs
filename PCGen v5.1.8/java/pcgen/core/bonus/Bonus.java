/*
 * Bonus.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on December 13, 2002, 9:19 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:10:54 $
 *
 */

package pcgen.core.bonus;

import java.util.StringTokenizer;

/**
 * <code>Bonus</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 **/

public class Bonus
{

	static final String[] bonusTags =
		{
			"ACVALUE",
			"CHECKS",
			"COMBAT",
			"DAMAGE",
			"DC",
			"DOMAIN",
			"DR",
			"EQM",
			"EQMARMOR",
			"EQMWEAPON",
			"HD",
			"HP",
			"ITEMCAPACITY",
			"ITEMCOST",
			"ITEMWEIGHT",
			"LANG",
			"LANGUAGES",
			"LOADMULT",
			"MISC",
			"MODSKILLPOINTS",
			"MONSKILLPTS",
			"MOVE",
			"MOVEADD",
			"MOVEMULT",
			"PCLEVEL",
			"POSTMOVEADD",
			"POSTRANGEADD",
			"RANGEADD",
			"RANGEMULT",
			"SIZEMOD",
			"SKILL",
			"SKILLPOINTS",
			"SKILLRANK",
			"SLOTS",
			"SPECIALTYSPELLKNOWN",
			"SPELL",
			"SPELLCAST", // CLASS=<classname>;LEVEL=<level> OR TYPE=<type>;LEVEL=<level>
			"SPELLCASTMULT", // CLASS=<classname>;LEVEL=<level> OR TYPE=<type>;LEVEL=<level>
			"SPELLKNOWN", // CLASS=<classname>;LEVEL=<level> OR TYPE=<type>;LEVEL=<level>
			"STAT",
			"TOHIT",
			"UDAM",
			"VAR",
			"WEAPON",
			"WEAPONPROF="
		};

	static final int BONUS_UNDEFINED = -1;
	private static final int BONUS_ACVALUE = 0;
	private static final int BONUS_CHECKS = 1;
	private static final int BONUS_COMBAT = 2;
	private static final int BONUS_DAMAGE = 3;
	private static final int BONUS_DC = 4;
	private static final int BONUS_DOMAIN = 5;
	private static final int BONUS_DR = 6;
	private static final int BONUS_EQM = 7;
	private static final int BONUS_EQMARMOR = 8;
	private static final int BONUS_EQMWEAPON = 9;
	private static final int BONUS_HD = 10;
	private static final int BONUS_HP = 11;
	private static final int BONUS_ITEMCAPACITY = 12;
	private static final int BONUS_ITEMCOST = 13;
	private static final int BONUS_ITEMWEIGHT = 14;
	private static final int BONUS_LANG = 15;
	private static final int BONUS_LANGUAGES = 16;
	private static final int BONUS_LOADMULT = 17;
	private static final int BONUS_MISC = 18;
	private static final int BONUS_MODSKILLPOINTS = 19;
	private static final int BONUS_MONSKILLPTS = 20;
	private static final int BONUS_MOVE = 21;
	private static final int BONUS_MOVEADD = 22;
	private static final int BONUS_MOVEMULT = 23;
	private static final int BONUS_PCLEVEL = 24;
	private static final int BONUS_POSTMOVEADD = 25;
	private static final int BONUS_POSTRANGEADD = 26;
	private static final int BONUS_RANGEADD = 27;
	private static final int BONUS_RANGEMULT = 28;
	private static final int BONUS_SIZEMOD = 29;
	private static final int BONUS_SKILL = 30;
	private static final int BONUS_SKILLPOINTS = 31;
	private static final int BONUS_SKILLRANK = 32;
	private static final int BONUS_SLOTS = 33;
	private static final int BONUS_SPECIALTYSPELLKNOWN = 34;
	private static final int BONUS_SPELL = 35;
	private static final int BONUS_SPELLCAST = 36;
	private static final int BONUS_SPELLCASTMULT = 37;
	private static final int BONUS_SPELLKNOWN = 38;
	private static final int BONUS_STAT = 39;
	private static final int BONUS_TOHIT = 40;
	private static final int BONUS_UDAM = 41;
	private static final int BONUS_VAR = 42;
	private static final int BONUS_WEAPON = 43;
	private static final int BONUS_WEAPONPROF = 44;

	public static BonusObj newBonus(final String bonusString)
	{
		int typeOfBonus = BONUS_UNDEFINED;
		StringTokenizer aTok = new StringTokenizer(bonusString, "|", false);
		final String bonusName = aTok.nextToken();
		int equalOffset = -1;
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (bonusName.equals(bonusTags[i]))
			{
				typeOfBonus = i;
				break;
			}
			if (bonusTags[i].endsWith("="))
			{
				if (bonusName.startsWith(bonusTags[i]))
				{
					equalOffset = bonusTags[i].length();
					typeOfBonus = i;
					break;
				}
			}
		}

		if (typeOfBonus == BONUS_UNDEFINED)
		{
			System.err.println("Unrecognized bonus: " + bonusString);
			return null;
		}

		final String bonusInfo = aTok.nextToken();
		String bValue = null;
		if (aTok.hasMoreTokens())
		{
			bValue = aTok.nextToken();
		}

		BonusObj aBonus = null;
		switch (typeOfBonus)
		{
			case BONUS_ACVALUE:
				aBonus = new ACValue();
				break;

			case BONUS_CHECKS:
				aBonus = new Checks();
				break;

			case BONUS_COMBAT:
				aBonus = new Combat();
				break;

			case BONUS_DAMAGE:
				aBonus = new Damage();
				break;

			case BONUS_DC:
				aBonus = new DC();
				break;

			case BONUS_DOMAIN:
				aBonus = new Domain();
				break;

			case BONUS_DR:
				aBonus = new DR();
				break;

			case BONUS_EQM:
				aBonus = new Eqm();
				break;

			case BONUS_EQMARMOR:
				aBonus = new EqmArmor();
				break;

			case BONUS_EQMWEAPON:
				aBonus = new EqmWeapon();
				break;

			case BONUS_HD:
				aBonus = new HD();
				break;

			case BONUS_HP:
				aBonus = new HP();
				break;

			case BONUS_ITEMCAPACITY:
				aBonus = new ItemCapacity();
				break;

			case BONUS_ITEMCOST:
				aBonus = new ItemCost();
				break;

			case BONUS_ITEMWEIGHT:
				aBonus = new ItemWeight();
				break;

			case BONUS_LANG:
				aBonus = new Lang();
				break;

			case BONUS_LANGUAGES:
				aBonus = new Languages();
				break;

			case BONUS_LOADMULT:
				aBonus = new LoadMult();
				break;

			case BONUS_MISC:
				aBonus = new Misc();
				break;

			case BONUS_MODSKILLPOINTS:
				aBonus = new ModSkillPoints();
				break;

			case BONUS_MONSKILLPTS:
				aBonus = new MonSkillPts();
				break;

			case BONUS_MOVE:
			case BONUS_MOVEADD:
			case BONUS_MOVEMULT:
			case BONUS_POSTMOVEADD:
				aBonus = new Move();
				break;

			case BONUS_PCLEVEL:
				aBonus = new PCLevel();
				break;

			case BONUS_POSTRANGEADD:
				aBonus = new PostRangeAdd();
				break;

			case BONUS_RANGEADD:
			case BONUS_RANGEMULT:
				aBonus = new RangeMult();
				break;

			case BONUS_SIZEMOD:
				aBonus = new SizeMod();
				break;

			case BONUS_SKILL:
				aBonus = new Skill();
				break;

			case BONUS_SKILLPOINTS:
				aBonus = new SkillPoints();
				break;

			case BONUS_SKILLRANK:
				aBonus = new SkillRank();
				break;

			case BONUS_SLOTS:
				aBonus = new Slots();
				break;

			case BONUS_SPELL:
				aBonus = new Spell();
				break;

			case BONUS_SPELLCAST:
				aBonus = new SpellCast();
				break;

			case BONUS_SPELLCASTMULT:
				aBonus = new SpellCastMult();
				break;

			case BONUS_SPELLKNOWN:
				aBonus = new SpellKnown();
				break;

			case BONUS_STAT:
				aBonus = new Stat();
				break;

			case BONUS_TOHIT:
				aBonus = new ToHit();
				break;

			case BONUS_UDAM:
				aBonus = new UDam();
				break;

			case BONUS_VAR:
				aBonus = new Var();
				break;

			case BONUS_WEAPON:
				aBonus = new Weapon();
				break;

			case BONUS_WEAPONPROF:
				aBonus = new WeaponProf();
				break;

			default:
				break;
		}

		if (aBonus != null)
		{
			aBonus.setTypeOfBonus(typeOfBonus);
			aBonus.setValue(bValue);

			while (aTok.hasMoreTokens())
			{
				final String aString = aTok.nextToken();
				if (aString.startsWith("!PRE") || aString.startsWith("PRE"))
				{
					aBonus.addPreReq(aString);
				}
				else if (aString.startsWith("TYPE="))
				{
					aBonus.addType(aString.substring(5));
				}
			}

			if (equalOffset >= 0)
			{
				aBonus.setVariable(bonusName.substring(equalOffset));
			}

			aTok = new StringTokenizer(bonusInfo, ",", false);
			while (aTok.hasMoreTokens())
			{
				aBonus.parseToken(aTok.nextToken());
			}
		}
		return aBonus;
	}

}
