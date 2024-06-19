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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:23 $
 *
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class RaceLoader extends LstObjectFileLoader
{
	/** Creates a new instance of RaceLoader */
	public RaceLoader()
	{
		super();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public PObject parseLine(
		PObject target,
		String lstLine,
		CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		Race race = (Race) target;
		if (race == null)
		{
			race = new Race();
		}

		final StringTokenizer colToken =
			new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int col = -1;
		while (colToken.hasMoreTokens())
		{
			++col;
			final String colString = colToken.nextToken().trim();

			// presence of : in column 1 means no required fields (good!)
			if ((col < 10) && (colString.indexOf(':') >= 0))
			{
				col = 10;
			}

			if (col == 0)
			{
				race.setName(colString);
				race.setSourceCampaign(source.getCampaign());
				race.setSourceFile(source.getFile());
			}
			else if (colString.startsWith("AC:"))
			{
				race.setStartingAC(new Integer(colString.substring(3)));
			}
			else if (colString.length() > 5 && colString.startsWith("AGE:"))
			{
				//race.setAgeString(colString.substring(4));
				Logging.errorPrint(
					"AGESET is a deprecated tag in "
						+ source.getFile()
						+ ". This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("AL:"))
			{
				// Pass into PREALIGN instead
				race.addPreReq(
					"PREALIGN:" + Utility.commaDelimit(colString.substring(3)));
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
				Logging.errorPrint(
					"HEIGHT is a deprecated tag in "
						+ source.getFile()
						+ ". This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("HITDICE:"))
			{
				final StringTokenizer hitdice =
					new StringTokenizer(colString.substring(8), ",");
				if (hitdice.countTokens() != 2)
				{
					Logging.errorPrint(
						"Illegal racial hit dice format '"
						+ colString
						+ "' in "
						+ source.getFile() );
				}
				else
				{
					race.setHitDice(Integer.parseInt(hitdice.nextToken()));
					race.setHitDiceSize(Integer.parseInt(hitdice.nextToken()));
				}
			}
			else if (colString.startsWith("HITDIE:")) // HitDieLock
			{
				race.setHitDieLock(colString.substring(7));
			}
			else if (colString.startsWith("HITDICEADVANCEMENT:"))
			{
				final StringTokenizer advancement =
					new StringTokenizer(colString.substring(19), ",");
				String temp;

				final int[] hitDiceAdvancement =
					new int[advancement.countTokens()];
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
			else if (colString.startsWith("MONCSKILL:"))
			{
				race.setMonCSkillList(colString.substring(10));
			}
			else if (colString.startsWith("MONCCSKILL:"))
			{
				race.setMonCCSkillList(colString.substring(11));
			}
			else if (colString.startsWith("MONSTERCLASS:"))
			{
				final StringTokenizer mclass =
					new StringTokenizer(colString.substring(13), ":");
				if (mclass.countTokens() != 2)
				{
					Logging.errorPrint(
						"Illegal Monster Class info '"
						+ colString
						+ "' in "
						+ source.getFile() );
				}
				race.setMonsterClass(mclass.nextToken());
				race.setMonsterClassLevels(
					Integer.parseInt(mclass.nextToken()));
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
			else if (
				colString.startsWith(
					"SAVES:")) //I know there is already a way to add racial bonuses to saves, but this is for races that give base saves.
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
				race.setInitialSkillMultiplier(
					Integer.parseInt(colString.substring(10)));
			}
			else if (colString.startsWith("STARTFEATS:"))
			{
				race.setBonusInitialFeats(
					Integer.parseInt(colString.substring(11)));
			}
			else if ((col < 7) || colString.startsWith("STATADJ"))
			{
				Logging.debugPrint(
					"RACE: support for "
						+ colString
						+ " has been removed. Use ADD:BONUS|STAT instead");
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
			/*
			else if (colString.startsWith("VFEAT:"))
			{
				race.setVFeatList(colString.substring(6));
			}
			*/
			else if (colString.startsWith("WEAPONBONUS:"))
			{
				race.setWeaponProfBonus(colString.substring(12));
			}
			else if (colString.startsWith("WEIGHT"))
			{
				//race.setWeightString(colString.substring(7));
				Logging.errorPrint(
					"WEIGHT is a deprecated tag in "
						+ source.getFile()
						+ ". This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("XTRASKILLPTSPERLVL:"))
			{
				race.setBonusSkillsPerLevel(
					Integer.parseInt(colString.substring(19)));
			}
			else if (PObjectLoader.parseTag(race, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint(
					"Illegal race tag '"
					+ colString
					+ "' in "
					+ source.getFile() );
			}
		}

		if ((race.getLevelAdjustment() != 0) && (race.getCR() == 0))
		{
			race.setCR(race.getLevelAdjustment());
		}
		
		finishObject(race);
		return null;
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if( includeObject(target) )
		{
			Globals.getRaceMap().put(target.getKeyName(), target);
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getRaceNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getRaceMap().remove(objToForget.getName());
	}

}
