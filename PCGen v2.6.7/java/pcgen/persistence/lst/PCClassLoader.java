/*
 * PCClassLoader.java
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
 * $Id: PCClassLoader.java,v 1.1 2006/02/20 23:57:40 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.PCClass;
import pcgen.core.SpecialAbility;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class PCClassLoader
{

	/** Creates a new instance of PCClassLoader */
	private PCClassLoader()
	{
	}

	public static void parseLine(PCClass obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = 0;
		int aInt = 0;
		int option = 15;
		if (inputLine.startsWith("CLASS:"))
		{
			if (obj.isNewItem())
			{
				option = 0;
				for (col = 0; col < 20; col++)
					obj.addCastList("0");
				col = 0;
			}
			else
			{
				colToken.nextToken(); // skip the first one
				col = 1;
			}
		}
		for (; colToken.hasMoreTokens(); col++)
		{
			final String colString = colToken.nextToken().trim();
			// existence of : means this file contains no required fields
			if (obj.isNewItem() && col==1 && colString.indexOf(":")>0)
				col=11;
			if (option < 15)
				option = col;
			if (col == 0 && option == 15)
			{
				aInt = Integer.parseInt(colString);
				continue;
			}
			if (option > 10)
			{
				if (colString.startsWith("PRERACETYPE:"))
				{
					obj.setPreRaceType(colString.substring(12));
					continue;
				}
				else if (PObjectLoader.parseTagLevel(obj, colString, aInt))
				{
					continue;
				}
			}
			switch (option)
			{
				case 0:
					obj.setName(colString.substring(6));
					break;
				case 1:
					obj.setAlignments(colString);
					break;
				case 2:
					obj.setHitDie(Integer.parseInt(colString));
					break;
				case 3:
					obj.setSkillPoints(Integer.parseInt(colString));
					break;
				case 4:
					obj.setInitialFeats(Integer.parseInt(colString));
					break;
				case 5:
					obj.setSpellBaseStat(colString);
					break;
				case 6:
					obj.setSpellType(colString);
					break;
				case 7:
					obj.setAttackBonusType(colString);
					break;
				case 8:
					obj.setFortitudeCheckType(colString);
					break;
				case 9:
					obj.setReflexCheckType(colString);
					break;
				case 10:
					obj.setWillCheckType(colString);
					break;
				default:
					if (colString.startsWith("HD:"))
						obj.setHitDie(Integer.parseInt(colString.substring(3)));
					else if (colString.startsWith("STARTSKILLPTS:"))
						obj.setSkillPoints(Integer.parseInt(colString.substring(14)));
					else if (colString.startsWith("XTRAFEATS:"))
						obj.setInitialFeats(Integer.parseInt(colString.substring(10)));
					else if (colString.startsWith("SPELLSTAT:"))
						obj.setSpellBaseStat(colString.substring(10));
					else if (colString.startsWith("SPELLTYPE:"))
						obj.setSpellType(colString.substring(10));
					else if (colString.startsWith("BAB:"))
						obj.setAttackBonusType(colString.substring(4));
					else if (colString.startsWith("FORTITUDECHECK:"))
						obj.setFortitudeCheckType(colString.substring(15));
					else if (colString.startsWith("REFLEXCHECK:"))
						obj.setReflexCheckType(colString.substring(12));
					else if (colString.startsWith("WILLPOWERCHECK:"))
						obj.setWillCheckType(colString.substring(15));
					else if (colString.startsWith("ABB:"))
						obj.setAbbrev(colString.substring(4));
					else if (colString.substring(0, 2).equals("AC"))
						obj.getAcList().add(colString.substring(3));
					else if (colString.startsWith("ADD:"))
						obj.addAddList(aInt, colString.substring(4));
					else if (colString.startsWith("ADDDOMAINS:"))
						obj.setAddDomains(aInt, colString.substring(11), ".");
					else if (colString.startsWith("AGESET:"))
						obj.setAgeSet(Integer.parseInt(colString.substring(7)));
					else if (colString.startsWith("ATTACKCYCLE:"))
						obj.setAttackCycle(colString.substring(12));
					else if (colString.startsWith("CAST:"))
					{
						if (aInt - 1 < obj.getCastList().size())
							obj.getCastList().set(aInt - 1, colString.substring(5));
						else
							obj.addCastList(colString.substring(5));
					}
					else if (colString.startsWith("CASTAS:"))
						obj.setCastAs(colString.substring(7));
					else if (colString.startsWith("DEF:"))
						obj.setDefenseString(colString.substring(4));
					else if (colString.startsWith("DEITY:"))
						obj.setDeityString(colString.substring(6));
					else if (colString.startsWith("DOMAIN:"))
						obj.addDomainList(fixParameter(aInt, colString.substring(7)));
					else if (colString.startsWith("INTMODTOSKILLS:"))
						obj.setIntModToSkills(!colString.substring(15).equals("No"));
					else if (colString.startsWith("FEAT:"))
						obj.addFeatList(aInt, colString.substring(5));
					else if (colString.startsWith("FEATAUTO:"))
						obj.setFeatAutos(aInt, colString.substring(9));
					else if (colString.startsWith("GOLD:"))
						obj.setGoldString(colString.substring(5));
					else if (colString.startsWith("ITEMCREATE:"))
					{
						obj.setItemCreationLevelMultiplier(Double.parseDouble(colString.substring(11)));
					}
					else if (colString.startsWith("KNOWN:"))
					{
						obj.addKnown(aInt, colString.substring(6));
					}
					else if (colString.startsWith("SPECIALTYKNOWN:"))
						obj.getSpecialtyKnownList().add(colString.substring(15));
					else if (colString.startsWith("KNOWNSPELLS:"))
						obj.addKnownSpellsList(colString.substring(12));
					else if (colString.startsWith("KNOWNSPELLSFROMSPECIALTY:"))
						obj.setNumSpellsFromSpecialty(Integer.parseInt(colString.substring(25)));
					else if (colString.startsWith("LANGAUTO"))
						obj.setLanguageAutos(colString.substring(9));
					else if (colString.startsWith("LANGBONUS"))
						obj.setLanguageBonus(colString.substring(10));
					else if (colString.startsWith("LEVELSPERFEAT:"))
						obj.setLevelsPerFeat(Integer.parseInt(colString.substring(14)));
					else if (colString.startsWith("MAXLEVEL:"))
						obj.setMaxLevel(Integer.parseInt(colString.substring(9)));
					else if (colString.startsWith("MEMORIZE:"))
						obj.setMemorizeSpells(colString.endsWith("Y"));
					else if (colString.startsWith("MULTIPREREQS"))
						obj.setMultiPreReqs(true);
					else if (colString.startsWith("QUALIFY:"))
						obj.setQualifyString(colString.substring(8));
					else if (colString.startsWith("PROHIBITED:"))
						obj.setProhibitedString(colString.substring(11));
					else if (colString.startsWith("REP:"))
						obj.setReputationString(colString.substring(4));
					else if (colString.startsWith("EXCLASS:"))
					{
						obj.setExClass(colString.substring(8));
					}
					else if (colString.startsWith("EXCHANGELEVEL:"))
					{
						obj.setLevelExchange(colString.substring(14));
					}
					else if (colString.startsWith("SA") || colString.startsWith("SUBSA"))
					{
						final boolean flag = colString.startsWith("SA");
						int index = 3;
						if (!flag) index = 6;
						final StringTokenizer aTok = new StringTokenizer(colString.substring(index), ",", false);
						while (aTok.hasMoreTokens())
						{
							final SpecialAbility sa = new SpecialAbility();
							SpecialAbilityLoader.parseLine(sa, aTok.nextToken(), sourceFile, lineNum);
							if (flag)
							{
								obj.addSpecialAbilityList(aInt, sa.getName());
							}
							else
							{
								obj.addSubSpecialAbilityList(aInt, sa.getName());
							}
						}
					}
					else if (colString.startsWith("SPECIALS:"))
						obj.setSpecialsString(colString.substring(9));
					else if (colString.startsWith("SUBCLASS:"))
						obj.setSubClassString(fixParameter(aInt, colString.substring(9)));
					else if (colString.startsWith(Constants.s_TAG_TYPE))
					{
						obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
					}
					else if (colString.startsWith("TEMPLATE:"))
						obj.addTemplate(fixParameter(aInt, colString.substring(9)));
					else if (colString.startsWith("UATT:"))
						obj.getUattList().add(colString.substring(5));
					else if (colString.startsWith("UDAM:"))
						obj.getUdamList().add(colString.substring(5));
					else if (colString.startsWith("UMULT:"))
						obj.addUmult(fixParameter(aInt, colString.substring(6)));
					else if (colString.startsWith("VFEAT:"))
						obj.addVFeatList(aInt, colString.substring(6));
					else if (colString.startsWith("VISIBLE:"))
						obj.setVisible(colString.substring(8).toUpperCase().startsWith("Y"));
					else if (colString.startsWith("WEAPONAUTO"))
						obj.setWeaponProfAutos(colString.substring(11));
					else if (colString.startsWith("WEAPONBONUS"))
						obj.setWeaponProfBonus(colString.substring(12));
					else
					{
						throw new PersistenceLayerException("Illegal class info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
					}
					break;
			}
		}
	}

	private static String fixParameter(int aInt, final String colString)
	{
		return new StringBuffer().append(aInt).append("|").append(colString).toString();
	}

}
