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
 * $Id: PCClassLoader.java,v 1.1 2006/02/21 01:13:22 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public final class PCClassLoader
{

	/** Creates a new instance of PCClassLoader */
	private PCClassLoader()
	{
	}

	public static boolean parseLine(PCClass pcClass, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		int col = 0;
		int aInt = 0;
		int option = 15;
		boolean handled = true;
		if (inputLine.startsWith("SUBCLASS:") || inputLine.startsWith("SUBCLASSLEVEL:"))
		{
			SubClass a = null;
			if (inputLine.startsWith("SUBCLASS:"))
			{
				final String n = inputLine.substring(9, inputLine.indexOf("\t"));
				a = pcClass.getSubClassNamed(n);
				if (a == null)
				{
					a = new SubClass();
					pcClass.addSubClass(a);
				}
			}
			else
			{
				if (pcClass.getSubClassList() != null && !pcClass.getSubClassList().isEmpty())
				{
					a = (SubClass) pcClass.getSubClassList().get(pcClass.getSubClassList().size() - 1);
					a.addToLevelArray(inputLine.substring(14));
					return true;
				}
			}
			if (a != null)
			{
				SubClassLoader.parseLine(a, inputLine, sourceURL, lineNum);
			}
			return true;
		}
		if (inputLine.startsWith("CLASS:"))
		{
			if (pcClass.isNewItem())
			{
				option = 0;
				for (col = 0; col < 20; col++)
				{
					pcClass.addCastList("0");
				}
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
			if (pcClass.isNewItem() && col > 0 && col < 11 && colString.indexOf(":") > 0)
			{
				col = 11;
			}
			if (option < 15)
			{
				option = col;
			}
			if (!(pcClass instanceof SubClass) && col == 0 && option == 15)
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
				else if (colString.startsWith("MONSKILL:"))
				{
					pcClass.addBonusList("0|MONSKILLPTS|NUMBER|" + colString.substring(9).trim() + "|PRELEVELMAX:1");
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
//					pcClass.setAlignments(colString);
//					removed - should use PREALIGN:
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
					else if ("AC".equals(colString.substring(0, 2)))
					{
						pcClass.getACList().add(colString.substring(3));
					}
					else if (colString.startsWith("ADDDOMAINS:"))
					{
						pcClass.setAddDomains(aInt, colString.substring(11), ".");
					}
					else if (colString.startsWith("AGESET:"))
					{
						//pcClass.setAgeSet(Integer.parseInt(colString.substring(7)));
						Logging.errorPrint("AGESET is a deprecated tag. This functionality is now handled in biosettings.lst");
					}
					else if (colString.startsWith("ATTACKCYCLE:"))
					{
						pcClass.setAttackCycle(colString.substring(12));
					}
					else if (colString.startsWith("BAB:"))
					{
						pcClass.setAttackBonusType(colString.substring(4));
					}
					else if (colString.startsWith("BONUSSPELLSTAT:"))
					{
						pcClass.setBonusSpellBaseStat(colString.substring(15));
					}
					else if (colString.startsWith("CAST:"))
					{
						pcClass.setCastList(aInt - 1, colString.substring(5));
					}
					else if (colString.startsWith("CASTAS:"))
					{
						pcClass.setCastAs(colString.substring(7));
					}
					else if (colString.startsWith("CHECK"))
					{
						continue;
					}
					//else if (colString.startsWith(Constants.s_TAG_TYPE))
					//{
					//	String userType = colString.substring(Constants.s_TAG_TYPE.length());
					//	pcClass.setType(userType);
					//	//add to global PCClassType list for future filtering
					//	StringTokenizer tok = new StringTokenizer(userType, ".", false);
					//	while (tok.hasMoreTokens())
					//	{
					//		String subType = tok.nextToken();
					//		if (!Globals.getPCClassTypeList().contains(subType))
					//		{
					//			Globals.getPCClassTypeList().add(subType);
					//		}
					//	}
					//}
					//else if (colString.startsWith("DEF:"))
					//{
					//	pcClass.setDefenseString(colString.substring(4));
					//}
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
					else if (colString.equals("HASSUBCLASS"))
					{
						pcClass.setHasSubClass(true);
					}
					else if (colString.startsWith("GOLD:"))
					{
						Logging.errorPrint("GOLD: tag in " + sourceURL.toString() + " no longer supported due to OGL compliance");
					}
					else if (colString.startsWith("HASSUBCLASS:"))
					{
						pcClass.setHasSubClass(colString.substring(12).startsWith("Y"));
					}
					else if (colString.startsWith("HD:"))
					{
						pcClass.setHitDie(Integer.parseInt(colString.substring(3)));
					}
					else if (colString.startsWith("ITEMCREATE:"))
					{
						pcClass.setItemCreationMultiplier(colString.substring(11));
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
						pcClass.setModToSkills(!"No".equalsIgnoreCase(colString.substring(12)));
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
					else if (colString.startsWith("SPELLBOOK:"))
					{
						pcClass.setSpellBookUsed(colString.charAt(10) == 'Y');
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
						Logging.debugPrint("SUBSA: tag in " + sourceURL.toString() + " ignored");
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
						handled = false;
						if (!(pcClass instanceof SubClass))
						{
							throw new PersistenceLayerException("Illegal class info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
						}
					}
					break;
			}
		}
		return handled;
	}

	private static String fixParameter(int aInt, final String colString)
	{
		return new StringBuffer().append(aInt).append("|").append(colString).toString();
	}

}
