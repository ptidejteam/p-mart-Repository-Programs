/*
 * RaceLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id: RaceLoader.java,v 1.1 2006/02/21 01:07:48 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.Race;
import pcgen.core.Utility;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class RaceLoader
{

	/** Creates a new instance of RaceLoader */
	private RaceLoader()
	{
	}

	public static void parseLine(Race race, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = -1;
		if (!race.isNewItem())
		{
			colToken.nextToken(); // skip name of .MOD race
			col = 20; // just force it past required fields since .MOD doesn't specify them
		}
		while (colToken.hasMoreTokens())
		{
			++col;
			String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(race, colString))
			{
				continue;
			}
			// presence of : in column 1 means no required fields (good!)
			if ((col < 10) && (colString.indexOf(':') >= 0))
			{
				col = 10;
			}

			if (col == 0)
			{
				race.setName(colString);
			}
			else if (colString.startsWith("AC:"))
			{
				race.setStartingAC(new Integer(colString.substring(3)));
			}
			else if (colString.length() > 5 && colString.startsWith("AGE:"))
			{
				//race.setAgeString(colString.substring(4));
				Globals.errorPrint("AGESET is a deprecated tag. This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("AL:"))
			{
				// Pass into PREALIGN instead
				race.addPreReq("PREALIGN:" + Utility.commaDelimit(colString.substring(3)));
				//				alignments = colString.substring(3);
			}
			else if (colString.startsWith("BAB:"))
			{
				race.setBAB(Integer.parseInt(colString.substring(4)));
			}
			else if (colString.startsWith("CHOOSE:LANGAUTO:"))
			{
				race.setChooseLanguageAutos(colString.substring(16));
			}
			else if (colString.startsWith("CR:"))
			{
				String cr = colString.substring(3);
				if (cr.startsWith("1/"))
				{
					cr = "-" + cr.substring(2);
				}
				race.setCR(Integer.parseInt(cr));
			}
			else if (colString.startsWith("FACE:"))
			{
				race.setFace(colString.substring(5));
			}
			else if (colString.startsWith("FAVCLASS:"))
			{
				race.setFavoredClass(colString.substring(9));
			}
			else if (colString.startsWith("FEAT"))
			{
				race.setFeatList(colString.substring(5));
			}
			else if (colString.startsWith("HANDS:"))
			{
				race.setHands(Integer.parseInt(colString.substring(6)));
			}
			else if (colString.length() > 5 && colString.startsWith("HEIGHT:"))
			{
				//race.setHeightString(colString.substring(7));
				Globals.errorPrint("HEIGHT is a deprecated tag. This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("HITDICE:"))
			{
				final StringTokenizer hitdice = new StringTokenizer(colString.substring(8), ",");
				if (hitdice.countTokens() != 2)
				{
					throw new PersistenceLayerException("Illegal racial hit dice format " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
				else
				{
					race.setHitDice(Integer.parseInt(hitdice.nextToken()));
					race.setHitDiceSize(Integer.parseInt(hitdice.nextToken()));
				}
			}
			else if (colString.startsWith("HITDIE:"))			// HitDieLock
			{
				race.setHitDieLock(colString.substring(7));
			}
			else if (colString.startsWith("HITDICEADVANCEMENT:"))
			{
				final StringTokenizer advancement = new StringTokenizer(colString.substring(19), ",");
				String temp;

				int[] hitDiceAdvancement = new int[advancement.countTokens()];
				for (int x = 0; x < hitDiceAdvancement.length; ++x)
				{
					temp = advancement.nextToken();
					if (temp.length() > 0 && temp.charAt(0) == '*')
					{
						race.setAdvancementUnlimited(true);
					}
					if (race.isAdvancementUnlimited())
					{
						hitDiceAdvancement[x] = -1;
					}
					else
					{
						hitDiceAdvancement[x] = Integer.parseInt(temp);
					}
				}
				race.setHitDiceAdvancement(hitDiceAdvancement);
			}
			else if (colString.startsWith("INIT:"))
			{
				race.setInitMod(new Integer(colString.substring(5)));
			}
			else if (colString.startsWith("LANGBONUS:"))
			{
				race.setLanguageBonus(colString.substring(10));
			}
			else if (colString.startsWith("LANGNUM:"))
			{
				race.setLangNum(Integer.parseInt(colString.substring(8)));
			}
			else if (colString.startsWith("LEGS:"))
			{
				race.setLegs(Integer.parseInt(colString.substring(5)));
			}
			else if (colString.startsWith("LEVELADJUSTMENT:"))
			{
				race.setLevelAdjustment(colString.substring(16));
			}
			else if (colString.startsWith("MFEAT"))
			{
				race.setMFeatList(colString.substring(6));
			}
			else if (colString.startsWith("MONSTERCLASS:"))
			{
				StringTokenizer mclass = new StringTokenizer(colString.substring(13), ":");
				if (mclass.countTokens() != 2)
				{
					throw new PersistenceLayerException("Illegal Monster Class info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
				race.setMonsterClass(mclass.nextToken());
				race.setMonsterClassLevels(Integer.parseInt(mclass.nextToken()));
			}
			else if (colString.startsWith("NATURALATTACKS:"))
			{
				// first natural weapon is primary,
				// the rest are secondary;
				// lets try the format:
				// NATURALATTACKS:primary weapon name,num attacks,damage|secondary1 weapon name,num attacks,damage|secondary2.....
				// damage will be of the form XdY+Z or XdY-Z to maintain readability of lst files.
				setNaturalAttacks(race, colString.substring(15), sourceURL, lineNum);
			}
			else if (colString.startsWith("PROF:"))
			{
				race.setWeaponProfs(colString.substring(5));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				race.setQualifyString(colString.substring(8));
			}
			else if (colString.startsWith("RACENAME:"))
			{
				race.setDisplayName(colString.substring(9));
			}
			else if (colString.startsWith("REACH:"))
			{
				race.setReach(Integer.parseInt(colString.substring(6)));
			}
			else if (colString.startsWith("SAVES:"))   //I know there is already a way to add racial bonuses to saves, but this is for races that give base saves.
			{
				//final StringTokenizer saves = new StringTokenizer(colString.substring(6), ",");
/*				if (saves.countTokens() != 3)
					throw new PersistenceLayerException("Illegal number of racial save bonuses " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				else
				{
					race.setCheck1(Integer.parseInt(saves.nextToken()));
					race.setCheck2(Integer.parseInt(saves.nextToken()));
					race.setCheck3(Integer.parseInt(saves.nextToken()));
				}
*/
			}
			else if (colString.startsWith("SIZE:"))
			{
				race.setSize(colString.substring(5));
			}
			else if (colString.startsWith("SKILL:"))
			{
				race.setBonusSkillList(colString.substring(6));
			}
			else if (colString.startsWith("SKILLMULT:"))
			{
				race.setInitialSkillMultiplier(Integer.parseInt(colString.substring(10)));
			}
			else if (colString.startsWith("STARTFEATS:"))
			{
				race.setBonusInitialFeats(Integer.parseInt(colString.substring(11)));
			}
			else if ((col < 7) || colString.startsWith("STATADJ"))
			{
				Globals.debugPrint("RACE: support for " + colString + " has been removed. Use ADD:BONUS|STAT instead");
/*
				int iStat = col - 1;
				if (col > 7)
				{
					iStat = Integer.parseInt(colString.substring(7, 8));
					colString = colString.substring(9);
				}
				if ((iStat >= 0) && (iStat < Globals.s_ATTRIBLONG.length))
				{
					if (colString.equals("*"))
					{
						race.setStatMod(iStat, 0);
						race.setNonAbility(iStat, true);
					}
					else
					{
						race.setStatMod(iStat, Integer.parseInt(colString));
						race.setNonAbility(iStat, false);
					}
				}
*/
			}
			else if (colString.startsWith("TEMPLATE:"))
			{
				race.addTemplate(colString.substring(9));
			}
			//else if (colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	race.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			//}
			else if (colString.startsWith("VFEAT:"))
			{
				race.setVFeatList(colString.substring(6));
			}
			else if (colString.startsWith("WEAPONBONUS:"))
			{
				race.setWeaponProfBonus(colString.substring(12));
			}
			else if (colString.startsWith("WEIGHT"))
			{
				//race.setWeightString(colString.substring(7));
				Globals.errorPrint("WEIGHT is a deprecated tag. This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("XTRASKILLPTSPERLVL:"))
			{
				race.setBonusSkillsPerLevel(Integer.parseInt(colString.substring(19)));
			}
			// these are the required fields which will go away at some point
			else if (col == 7)
			{
				race.setFavoredClass(colString);
			}
			else if (col == 8)
			{
				race.setBonusSkillsPerLevel(Integer.parseInt(colString));
			}
			else if (col == 9)
			{
				race.setBonusInitialFeats(Integer.parseInt(colString));
			}
			else
			{
				throw new PersistenceLayerException("Illegal race info " + sourceURL.toString() +
					":" + Integer.toString(lineNum) + " \"" +
					colString + "\"");
			}
		}

		if ((race.getLevelAdjustment() != 0) && (race.getCR() == 0))
		{
			race.setCR(race.getLevelAdjustment());
		}
	}

	private static void setNaturalAttacks(Race obj, String aString, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		// first natural weapon is primary,
		// the rest are secondary;
		// lets try the format:
		//NATURALATTACKS:primary weapon name,weapon type,num attacks,damage|secondary1 weapon name,weapon type,num attacks,damage|secondary2.....
		// format of these things is exactly as it would be
		// in an equipment lst file
		// Type is of the format Weapon.Natural.Melee.Bludgeoning (eg. a smash)
		// number of attacks is the number of attacks with that weapon at BAB (for primary), or BAB - 5 (for secondary)

		// Currently, this isn't going to work with monk attacks - their unarmed stuff won't be affected.
		final List naturalWeapons = obj.getNaturalWeapons();

		naturalWeapons.clear();
		int sizeInt = Globals.sizeInt(obj.getSize());
		boolean firstWeapon = true;
		final StringTokenizer attackTok = new StringTokenizer(aString, "|", false);
		boolean onlyOne = false;

		// Make a preliminary guess at whether this is an "only" attack
		// Changed by sage_sam for Bug #649325
		if (attackTok.countTokens() == 1)
		{
			onlyOne = true;
		}

		if (Globals.isDebugMode())
		{
			Globals.debugPrint("aString: ", aString);
		}

		final char aChar = Globals.getSizeAdjustmentAtIndex(sizeInt).getAbbreviation().charAt(0);	// This code is going to have problems with 'C' sized creatures, 'cuz they can't have a weapon larger than them

		while (attackTok.hasMoreTokens())
		{
			Equipment anEquip = new Equipment();
			WeaponProf prof = new WeaponProf();

			StringTokenizer aTok = new StringTokenizer(attackTok.nextToken(), ",", false);
			//String eq = aTok.nextToken() + "\tTYPE:" + aTok.nextToken() + "\tWT:0\tCost:0\tSIZE:" +	aChar;
			StringBuffer eq = new StringBuffer();
			final String attackName = aTok.nextToken();
			if (!attackName.equalsIgnoreCase(Constants.s_NONE))
			{
				prof.setTypeInfo(aTok.nextToken());
				eq.append(attackName).append("\tTYPE:").append(prof.getType()).append("\tWT:0\tCost:0\tSIZE:").append(aChar);
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
					Globals.errorPrint(obj.getName() + ": non-numeric value for number of attacks: '" + numAttacks + "'");
				}

				if (bonusAttacks > 0)
				{
					// only attack means one attack with one weapon.
					// sage_sam for Bug #649325
					onlyOne = false;
					eq.append("\tBONUS:WEAPON|ATTACKS|").append(bonusAttacks);
				}
				else if (onlyOne)
				{
					// If it has only one natural attack,
					// treat the attack as with a weapon
					// one size larger than the creature,
					// thus it is wielded "two handed"
					// --> 1.5x str bonus
					if (sizeInt < (Globals.getSizeAdjustmentList().size() - 1))
					{
						++sizeInt;
					}
				}

				eq.append("\tDAMAGE:").append(aTok.nextToken()).append("\tCRITRANGE:1\tCRITMULT:x2");

				// sage_sam 27 Nov 2002 for Bug #586332
				// properly set proficiency name for natural weapons
				eq.append("\tPROFICIENCY:").append(attackName);

				// sage_sam 02 Dec 2002 for Bug #586332
				// allow hands to be required to equip natural weapons, i.e. claws
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
						Globals.errorPrint(obj.getName() + ": non-numeric value for hands required: '" + hString + "'");
					}
				}
				eq.append("\tHANDS:").append(handsRequired);

				if (Globals.isDebugMode())
				{
					Globals.debugPrint("Eq:" + Constants.s_LINE_SEP, eq.toString());
				}

				EquipmentLoader.parseLine(anEquip, eq.toString(), sourceURL, lineNum);
				anEquip.setQty(new Float(1));	//these values need to be locked.
				anEquip.setNumberCarried(new Float(1));
				obj.setWeaponProfAutos(anEquip.getName());
				anEquip.setAttacksProgress(attacksProgress);

				prof.setName(anEquip.getName());
				prof.setKeyName(anEquip.getName());
				Globals.addWeaponProf(prof);

				//anEquip.setIsEquipped(true);	//<-- causes null pointer error (no PC loaded)
				if (firstWeapon)
				{
					anEquip.setModifiedName("Natural/Primary");
				}
				else
				{
					anEquip.setModifiedName("Natural/Secondary");
				}
				anEquip.setOnlyNaturalWeapon(onlyOne);
				naturalWeapons.add(anEquip);
			}
			firstWeapon = false;
		}

		obj.setNaturalWeapons(naturalWeapons);
	}

}
