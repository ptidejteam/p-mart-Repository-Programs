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
 * $Id: RaceLoader.java,v 1.1 2006/02/20 23:54:41 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.Race;
import pcgen.core.Utility;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class RaceLoader
{

	/** Creates a new instance of RaceLoader */
	private RaceLoader()
	{
	}

	public static void parseLine(Race obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		final int colMax = colToken.countTokens();
		if (colMax == 0)
		{
			return;
		}

		for (int col = 0; col < colMax; col++)
		{
			String colString = (String)colToken.nextToken();
			if (PObjectLoader.parseTag(obj, colString))
				continue;

			if (col == 0)
			{
				obj.setName(colString);
			}
			else if ((col < 7) || colString.startsWith("STATADJ"))
			{
				int iStat = col;
				if (col > 7)
				{
					iStat = Integer.parseInt(colString.substring(7, 8));
					colString = colString.substring(9);
				}
				if ((iStat > 0) && (iStat <= Globals.s_ATTRIBLONG.length))
				{
					iStat -= 1;
					if (colString.equals("*"))
					{
						obj.setStatMod(iStat, 0);
						obj.setNonAbility(iStat, true);
					}
					else
					{
						obj.setStatMod(iStat, Integer.parseInt(colString));
						obj.setNonAbility(iStat, false);
					}
				}
			}
			else if (col == 7)
			{
				obj.setFavoredClass(colString);
			}
			else if (col == 8)
			{
				obj.setBonusSkillsPerLevel(Integer.parseInt(colString));
			}
			else if (col == 9)
			{
				obj.setBonusInitialFeats(Integer.parseInt(colString));
			}
			else if (colString.startsWith("RACENAME:"))
			{
				obj.setRaceOutputName(colString.substring(9));
			}
			else if (colString.startsWith("AC"))
			{
				obj.setStartingAC(new Integer(colString.substring(3)));
			}
			else if (colString.length() > 5 && colString.substring(0, 3).equals("AGE"))
			{
				obj.setAgeString(colString.substring(4));
			}
			else if (colString.startsWith("AL"))
			{
				// Pass into PREALIGN instead
				obj.addPreReq("PREALIGN:" + Utility.commaDelimit(colString.substring(3)));
				//				alignments = colString.substring(3);
			}
			// HitDieLock
			else if (colString.startsWith("HITDIE:"))
			{
				obj.setHitDieLock(colString.substring(7));
			}
			else if (colString.startsWith("BAB"))
			{
				obj.setBAB(Integer.parseInt(colString.substring(4)));
			}
			else if (colString.startsWith("CR"))
			{
				obj.setCR(Integer.parseInt(colString.substring(3)));
			}
			else if (colString.startsWith("CSKILL"))
			{
				obj.setCSkillList(colString.substring(7));
			}
			else if (colString.startsWith("CCSKILL"))
			{
				obj.setCcSkillList(colString.substring(8));
			}
			else if (colString.startsWith("DEFINE"))
				obj.addVariableList("0|" + colString.substring(7));
			else if (colString.startsWith("DR"))
				obj.setDR(colString.substring(3));
			else if (colString.startsWith("FACE"))
				obj.setFace(colString.substring(5));
			else if (colString.startsWith("FEAT"))
			{
				obj.setFeatList(colString.substring(5));
			}
			else if (colString.startsWith("MFEAT"))
			{
				obj.setMFeatList(colString.substring(6));
			}
			else if (colString.startsWith("HANDS"))
				obj.setHands(Integer.parseInt(colString.substring(6)));
			else if (colString.length() > 5 && colString.substring(0, 6).equals("HEIGHT"))
			{
				obj.setHeightString(colString.substring(7));
			}
			else if (colString.startsWith("HITDICE:"))
			{
				final StringTokenizer hitdice = new StringTokenizer(colString.substring(8), ",");
				if (hitdice.countTokens() != 2)
					throw new PersistenceLayerException("Illegal racial hit dice format " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				else
				{
					obj.setHitDice(Integer.parseInt(hitdice.nextToken()));
					obj.setHitDiceSize(Integer.parseInt(hitdice.nextToken()));
				}
			}
			else if (colString.startsWith("HITDICEADVANCEMENT"))
			{
				final StringTokenizer advancement = new StringTokenizer(colString.substring(19), ",");
				String temp = null;

				int[] hitDiceAdvancement = new int[advancement.countTokens()];
				for (int x = 0; x < hitDiceAdvancement.length; x++)
				{
					temp = advancement.nextToken();
					if (temp.startsWith("*"))
						obj.setAdvancementUnlimited(true);
					if (obj.isAdvancementUnlimited())
						hitDiceAdvancement[x] = -1;
					else
						hitDiceAdvancement[x] = Integer.parseInt(temp);
				}
				obj.setHitDiceAdvancement(hitDiceAdvancement);
			}
			else if (colString.startsWith("INIT"))
			{
				obj.setInitMod(new Integer(colString.substring(5)));
			}
			else if (colString.startsWith("KEY:"))
			{
				obj.setKeyName(colString.substring(4));
			}
			else if (colString.startsWith("LANGAUTO"))
			{
				obj.setLanguageAutos(colString.substring(9));
			}
			else if (colString.startsWith("LANGBONUS"))
			{
				obj.setLanguageBonus(colString.substring(10));
			}
			else if (colString.startsWith("LANGNUM"))
			{
				obj.setLangNum(Integer.parseInt(colString.substring(8)));
			}
			else if (colString.startsWith("LEVELADJUSTMENT"))
			{
				obj.setLevelAdjustment(Integer.parseInt(colString.substring(16)));
			}
			else if (colString.startsWith("MONSTERCLASS:"))
			{
				StringTokenizer mclass = new StringTokenizer(colString.substring(13), ":");
				if (mclass.countTokens() != 2)
					throw new PersistenceLayerException("Illegal Monster Class info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				obj.setMonsterClass(mclass.nextToken());
				obj.setMonsterClassLevels(Integer.parseInt(mclass.nextToken()));
			}
			else if (colString.length() > 4 && colString.substring(0, 4).equals("MOVE"))
			{
				final StringTokenizer moves = new StringTokenizer(colString.substring(5), ",");
				Integer[] movements;
				String[] movementTypes;

				if (moves.countTokens() == 1)
				{
					obj.setMovement(new Integer(moves.nextToken()));

					movements = new Integer[1];
					movements[0] = obj.getMovement();

					movementTypes = new String[1];
					movementTypes[0] = "Walk";
				}
				else
				{
					movements = new Integer[moves.countTokens() / 2];
					movementTypes = new String[moves.countTokens() / 2];
					int x = 0;
					while (moves.countTokens() > 1)
					{
						movementTypes[x] = moves.nextToken();
						movements[x] = new Integer(moves.nextToken());
						if (movementTypes[x].equals("Walk"))
							obj.setMovement(movements[x]);
						x++;
					}
				}
				obj.setMovements(movements);
				obj.setMovementTypes(movementTypes);

			}
			else if (colString.startsWith("NATURALATTACKS"))
			{
				// first natural weapon is primary, rest are secondary; lets try the format- NATURALATTACKS:primary weapon name,num attacks,damage|secondary1 weapon name,num attacks,damage|secondary2.....
				// damage will be of the form XdY+Z or XdY-Z to maintain readability of lst files.
				setNaturalAttacks(obj, colString.substring(15), sourceFile, lineNum);
			}
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
			{
				obj.addPreReq(colString);
			}
			else if (colString.startsWith("QUALIFY:"))
				obj.setQualifyString(colString.substring(8));
			else if (colString.startsWith("PROF"))
			{
				obj.setWeaponProfs(colString.substring(5));
			}
			else if (colString.startsWith("REACH"))
				obj.setReach(Integer.parseInt(colString.substring(6)));
			else if (colString.startsWith("SAVES"))   //I know there is already a way to add racial bonuses to saves, but this is for races that give base saves.
			{
				final StringTokenizer saves = new StringTokenizer(colString.substring(6), ",");
				if (saves.countTokens() != 3)
					throw new PersistenceLayerException("Illegal number of racial save bonuses " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				else
				{
					obj.setFortSave(Integer.parseInt(saves.nextToken()));
					obj.setRefSave(Integer.parseInt(saves.nextToken()));
					obj.setWillSave(Integer.parseInt(saves.nextToken()));
				}
			}
			else if (colString.length() > 4 && colString.substring(0, 4).equals("SIZE"))
			{
				obj.setSize(colString.substring(5));
			}
			else if (colString.length() > 6 && colString.substring(0, 6).equals("SKILL:"))
			{
				obj.setBonusSkillList(colString.substring(6));
			}
			else if (colString.startsWith("SKILLMULT:"))
			{
				obj.setInitialSkillMultiplier(Integer.parseInt(colString.substring(10)));
			}
			else if (colString.startsWith("SA:"))
			{
				obj.setSpecialAbilties(colString.substring(3));
			}
			else if (colString.startsWith("SR"))
				obj.setSR(Integer.parseInt(colString.substring(3)));
			else if (colString.startsWith("TEMPLATE:"))
				obj.addTemplate(colString.substring(9));
			else if (colString.startsWith(Constants.s_TAG_TYPE))
			{
				obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			}
			else if (colString.startsWith("VFEAT"))
			{
				obj.setVFeatList(colString.substring(6));
			}
			else if (colString.length() > 6 && colString.substring(0, 6).equals("VISION"))
			{
				obj.setVision(colString.substring(7));
			}
			else if (colString.startsWith("WEAPONAUTO"))
			{
				obj.setWeaponProfAutos(colString.substring(11));
			}
			else if (colString.startsWith("WEAPONBONUS"))
			{
				obj.setWeaponProfBonus(colString.substring(12));
			}
			else if (colString.length() > 5 && colString.substring(0, 6).equals("WEIGHT"))
			{
				obj.setWeightString(colString.substring(7));
			}
			else
			{
				throw new PersistenceLayerException("Illegal race info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" +
					colString + "\"");
			}
		}

		if (obj.getLevelAdjustment() != 0 && obj.getCR() == 0)
			obj.setCR(obj.getLevelAdjustment());
	}

	private static void setNaturalAttacks(Race obj, String aString, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		// first natural weapon is primary, rest are secondary; lets try the format- NATURALATTACKS:primary weapon name,weapon type,num attacks,damage|secondary1 weapon name,weapon type,num attacks,damage|secondary2.....
		// format of these things is exactly as it would be in an equipment lst file
		// Type is of the format Weapon.Natural.Melee.Bludgeoning (eg. a smash)
		// number of attacks is the number of attacks with that weapon at BAB (for primary), or BAB - 5 (for secondary)

		// Currently, this isn't going to work with monk attacks - their unarmed stuff won't be effected.
		List naturalWeapons = (List)obj.getNaturalWeapons();

		naturalWeapons.clear();
		int sizeInt = Globals.sizeInt(obj.getSize());
		boolean firstWeapon = true;
		StringTokenizer attackTok = new StringTokenizer(aString, "|", false);
		boolean onlyOne = false;

		if (attackTok.countTokens() == 1 && sizeInt < Constants.SIZE_C)		// If it has only one natural attack, treat the attack as with a weapon one size larger
		{
			// than the creature, thus it is wielded "two handed" --> 1.5x str bonus
			sizeInt++;
			onlyOne = true;
		}

		Globals.debugPrint("aString: " + aString);

		char aChar = Constants.s_SIZESHORT[sizeInt].charAt(0);			// This code is going to have problems with 'C' sized creatures, 'cuz they can't have a weapon larger than them

		while (attackTok.hasMoreTokens())
		{
			Equipment anEquip = new Equipment();
			StringTokenizer aTok = new StringTokenizer(attackTok.nextToken(), ",", false);
			//String eq = (String)aTok.nextToken() + "\tTYPE:" + (String)aTok.nextToken() + "\tWT:0\tCost:0\tSIZE:" +	aChar;
			StringBuffer eq = new StringBuffer();
			final String attackName = aTok.nextToken();
			if (!attackName.equalsIgnoreCase(Constants.s_NONE))
			{
				eq.append(attackName).append("\tTYPE:").append((String)aTok.nextToken()).append("\tWT:0\tCost:0\tSIZE:").append(aChar);
				String bString = aTok.nextToken();
				boolean attacksProgress = true;
				if (bString.startsWith("*"))
				{
					bString = bString.substring(1);
					attacksProgress = false;
				}

				int bonusAttacks = Integer.parseInt(bString) - 1;

				if (bonusAttacks > 0)
				{
					//eq = eq + "\tBONUS:COMBAT|ATTACKS|" + bonusAttacks.toString();
					eq.append("\tBONUS:COMBAT|ATTACKS|").append(bonusAttacks);
				}
				//eq = eq + "\tDAMAGE:" + (String)aTok.nextToken() +	"\tCRITRANGE:1\tCRITMULT:x2";
				eq.append("\tDAMAGE:").append((String)aTok.nextToken()).append("\tCRITRANGE:1\tCRITMULT:x2");
				//makes some nasty assumptions, but good for the time being
				//BONUS:COMBAT|ATTACKS|# used instead of ATTACKS: because ATTACKS is not yet/properly implemented.
				Globals.debugPrint("Eq:\n" + eq);

				EquipmentLoader.parseLine(anEquip, eq.toString(), sourceFile, lineNum);
				anEquip.setQty(new Float(1));	//these values need to be locked.
				anEquip.setNumberCarried(new Float(1));
				obj.setWeaponProfAutos(anEquip.getName());
				anEquip.setAttacksProgress(attacksProgress);

				//		anEquip.setIsEquipped(true);	//<-- causes null pointer error
				if (firstWeapon)
				{
					anEquip.setModifiedName("Natural/Primary");
					//			if (onlyOne)
					//				anEquip.setHand(4);
					//			else
					//				anEquip.setHand(1);
				}
				else
				{
					anEquip.setModifiedName("Natural/Secondary");
					//			anEquip.setHand(2);
				}
				anEquip.setOnlyNaturalWeapon(onlyOne);
				naturalWeapons.add(anEquip);
			}
			firstWeapon = false;
		}

		obj.setNaturalWeapons(naturalWeapons);
	}

}
