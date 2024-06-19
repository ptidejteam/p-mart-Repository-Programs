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
 * $Id: PCClassLoader.java,v 1.1 2006/02/21 00:47:22 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
class PCClassLoader
{

	/** Creates a new instance of PCClassLoader */
	private PCClassLoader()
	{
	}

	public static void parseLine(PCClass pcClass, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = 0;
		int aInt = 0;
		int option = 15;
		if (inputLine.startsWith("CLASS:"))
		{
			if (pcClass.isNewItem())
			{
				option = 0;
				for (col = 0; col < 20; col++)
					pcClass.addCastList("0");
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
			if (pcClass.isNewItem() && col>0 && col < 11 && colString.indexOf(":") > 0)
				col = 11;
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
					pcClass.setPreRaceType(colString.substring(12));
					continue;
				}
				else if (PObjectLoader.parseTagLevel(pcClass, colString, aInt))
				{
					continue;
				}
			}
			switch (option)
			{
				case 0:
					pcClass.setName(colString.substring(6));
					break;
				case 1:
					pcClass.setAlignments(colString);
					break;
				case 2:
					pcClass.setHitDie(Integer.parseInt(colString));
					break;
				case 3:
					pcClass.setSkillPoints(Integer.parseInt(colString));
					break;
				case 4:
					pcClass.setInitialFeats(Integer.parseInt(colString));
					break;
				case 5:
					pcClass.setSpellBaseStat(colString);
					break;
				case 6:
					pcClass.setSpellType(colString);
					break;
				case 7:
					pcClass.setAttackBonusType(colString);
					break;
				case 8:
//					pcClass.setCheck1Type(colString);
					break;
				case 9:
//					pcClass.setCheck2Type(colString);
					break;
				case 10:
//					pcClass.setCheck3Type(colString);
					break;
				default:
					if (colString.startsWith("ABB:"))
					{
						pcClass.setAbbrev(colString.substring(4));
					}
					else if (colString.substring(0, 2).equals("AC"))
					{
						pcClass.getACList().add(colString.substring(3));
					}
					else if (colString.startsWith("ADD:"))
					{
						pcClass.addAddList(aInt, colString.substring(4));
					}
					else if (colString.startsWith("ADDDOMAINS:"))
					{
						pcClass.setAddDomains(aInt, colString.substring(11), ".");
					}
					else if (colString.startsWith("AGESET:"))
					{
						pcClass.setAgeSet(Integer.parseInt(colString.substring(7)));
					}
					else if (colString.startsWith("ATTACKCYCLE:"))
					{
						pcClass.setAttackCycle(colString.substring(12));
					}
					else if (colString.startsWith("BAB:"))
					{
						pcClass.setAttackBonusType(colString.substring(4));
					}
					else if (colString.startsWith("CAST:"))
					{
						if (aInt - 1 < pcClass.getCastList().size())
							pcClass.getCastList().set(aInt - 1, colString.substring(5));
						else
							pcClass.addCastList(colString.substring(5));
					}
					else if (colString.startsWith("CASTAS:"))
					{
						pcClass.setCastAs(colString.substring(7));
					}
					else if (colString.startsWith("CHECK"))
					{
						continue;
					}
					else if (colString.startsWith(Constants.s_TAG_TYPE))
					{
						String userType = colString.substring(Constants.s_TAG_TYPE.length());
						pcClass.setType(userType);
						//add to global PCClassType list for future filtering
						StringTokenizer tok = new StringTokenizer(userType, ".", false);
						while (tok.hasMoreTokens())
						{
							String subType = tok.nextToken();
							if (!Globals.getPCClassTypeList().contains(subType))
								Globals.getPCClassTypeList().add(subType);
						}
					}
					else if (colString.startsWith("DEF:"))
					{
						pcClass.setDefenseString(colString.substring(4));
					}
					else if (colString.startsWith("DEITY:"))
					{
						pcClass.setDeityString(colString.substring(6));
					}
					else if (colString.startsWith("DOMAIN:"))
					{
						pcClass.addDomainList(fixParameter(aInt, colString.substring(7)));
					}
					else if (colString.startsWith("EXCHANGELEVEL:"))
					{
						pcClass.setLevelExchange(colString.substring(14));
					}
					else if (colString.startsWith("EXCLASS:"))
					{
						pcClass.setExClass(colString.substring(8));
					}
					else if (colString.startsWith("FEAT:"))
					{
						pcClass.addFeatList(aInt, colString.substring(5));
					}
					else if (colString.startsWith("FEATAUTO:"))
					{
						pcClass.setFeatAutos(aInt, colString.substring(9));
					}
					else if (colString.startsWith("GOLD:"))
					{
						Globals.errorPrint("GOLD: tag in "+sourceFile.getName()+" no longer supported due to OGL compliance");
					}
					else if (colString.startsWith("HD:"))
					{
						pcClass.setHitDie(Integer.parseInt(colString.substring(3)));
					}
					else if (colString.startsWith("ITEMCREATE:"))
					{
						pcClass.setItemCreationLevelMultiplier(Double.parseDouble(colString.substring(11)));
					}
					else if (colString.startsWith("KNOWN:"))
					{
						pcClass.addKnown(aInt, colString.substring(6));
					}
					else if (colString.startsWith("KNOWNSPELLS:"))
					{
						pcClass.addKnownSpellsList(colString.substring(12));
					}
					else if (colString.startsWith("KNOWNSPELLSFROMSPECIALTY:"))
					{
						pcClass.setNumSpellsFromSpecialty(Integer.parseInt(colString.substring(25)));
					}
					else if (colString.startsWith("LANGBONUS"))
					{
						pcClass.setLanguageBonus(colString.substring(10));
					}
					else if (colString.startsWith("LEVELSPERFEAT:"))
					{
						pcClass.setLevelsPerFeat(Integer.parseInt(colString.substring(14)));
					}
					else if (colString.startsWith("MAXLEVEL:"))
					{
						pcClass.setMaxLevel(Integer.parseInt(colString.substring(9)));
					}
					else if (colString.startsWith("MEMORIZE:"))
					{
						pcClass.setMemorizeSpells(colString.endsWith("Y"));
					}
					else if (colString.startsWith("MODTOSKILLS:"))
					{
						pcClass.setModToSkills(!colString.substring(12).equalsIgnoreCase("No"));
					}
					else if (colString.startsWith("MULTIPREREQS"))
					{
						pcClass.setMultiPreReqs(true);
					}
					else if (colString.startsWith("PROHIBITED:"))
					{
						pcClass.setProhibitedString(colString.substring(11));
					}
					else if (colString.startsWith("QUALIFY:"))
					{
						pcClass.setQualifyString(colString.substring(8));
					}
					else if (colString.startsWith("REP:"))
					{
						pcClass.setReputationString(colString.substring(4));
					}
					else if (colString.startsWith("SKILLLIST:"))
					{
						pcClass.setClassSkillString(colString.substring(10));
					}
					else if (colString.startsWith("SPECIALS:"))
					{
						pcClass.setSpecialsString(colString.substring(9));
					}
					else if (colString.startsWith("SPECIALTYKNOWN:"))
					{
						pcClass.getSpecialtyKnownList().add(colString.substring(15));
					}
					else if (colString.startsWith("SPELLLIST:"))
					{
						pcClass.setSpellLevelString(colString.substring(10));
					}
					else if (colString.startsWith("SPELLSTAT:"))
					{
						pcClass.setSpellBaseStat(colString.substring(10));
					}
					else if (colString.startsWith("SPELLTYPE:"))
					{
						pcClass.setSpellType(colString.substring(10));
					}
					else if (colString.startsWith("STARTSKILLPTS:"))
					{
						pcClass.setSkillPoints(Integer.parseInt(colString.substring(14)));
					}
					else if (colString.startsWith("SUBCLASS:"))
					{
						pcClass.setSubClassString(fixParameter(aInt, colString.substring(9)));
					}
					else if (colString.startsWith("SUBSA:"))
					{
						Globals.debugPrint("SUBSA: tag in " + sourceFile.getName() + " ignored");
					}
					else if (colString.startsWith("TEMPLATE:"))
					{
						pcClass.addTemplate(fixParameter(aInt, colString.substring(9)));
					}
					else if (colString.startsWith("UATT:"))
					{
						pcClass.getUattList().add(colString.substring(5));
					}
					else if (colString.startsWith("VFEAT:"))
					{
						pcClass.addVFeatList(aInt, colString.substring(6));
					}
					else if (colString.startsWith("VISIBLE:"))
					{
						pcClass.setVisible(colString.substring(8).toUpperCase().startsWith("Y"));
					}
					else if (colString.startsWith("WEAPONBONUS"))
					{
						pcClass.setWeaponProfBonus(colString.substring(12));
					}
					else if (colString.startsWith("XTRAFEATS:"))
					{
						pcClass.setInitialFeats(Integer.parseInt(colString.substring(10)));
					}
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
