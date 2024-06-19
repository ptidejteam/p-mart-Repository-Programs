/*
 * KitLoader.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 1:39 PM
 *
 * $Id: KitLoader.java,v 1.1 2006/02/21 01:10:59 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Kit;
import pcgen.core.kit.KitFeat;
import pcgen.core.kit.KitGear;
import pcgen.core.kit.KitProf;
import pcgen.core.kit.KitSchool;
import pcgen.core.kit.KitSkill;
import pcgen.core.kit.KitSpells;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * ???
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
final class KitLoader
{

	/** Creates a new instance of KitLoader */
	private KitLoader()
	{
	}

	public static void parseLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (inputLine.startsWith("STARTPACK:"))
		{
			parseNameLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("FEAT:"))
		{
			parseFeatLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("GEAR:"))
		{
			parseGearLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("PROF:"))
		{
			parseProfLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("SCHOOL:"))
		{
			parseSchoolLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("SKILL:"))
		{
			parseSkillLine(obj, inputLine, sourceURL, lineNum);
		}
		else if (inputLine.startsWith("SPELLS:"))
		{
			parseSpellsLine(obj, inputLine, sourceURL, lineNum);
		}
		else
		{
			throw new PersistenceLayerException("Unknown kit info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + inputLine + "\"");
		}
	}

	private static void parseNameLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (PObjectLoader.parseTag(obj, colString))
			{
				//TODO: What is this?
			}
			else if (colString.startsWith("STARTPACK:"))
			{
				obj.setName(colString.substring(10));
			}
			else if (colString.startsWith("EQUIPBUY:"))
			{
				obj.setBuyRate(colString.substring(9));
			}
			else if (colString.startsWith("EQUIPSELL:"))
			{
				obj.setSellRate(colString.substring(10));
			}
			else
			{
				throw new PersistenceLayerException("Unknown KitPack info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}

	private static void parseProfLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		KitProf kProf = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("PROF:"))
			{
				if (kProf == null)
				{
					kProf = new KitProf(colString.substring(5));
				}
				else
				{
					Logging.errorPrint("Ignoring second PROF tag \"" + colString + "\" in Kit.parseProfLine");
				}
			}
			else
			{
				if (kProf == null)
				{
					Logging.errorPrint("Cannot process tag, missing PROF tag." + Constants.s_LINE_SEP + colString);
					continue;
				}
				if (colString.startsWith("COUNT:"))
				{
					kProf.setChoiceCount(colString.substring(6));
				}
				else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
				{
					kProf.addPreReq(colString);
				}
				else if (colString.startsWith("RACIAL:"))
				{
					kProf.setRacialProf(colString.charAt(7) == 'Y');
				}
				else
				{
					throw new PersistenceLayerException("Unknown KitProf info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		obj.addProf(kProf);
	}

	private static void parseFeatLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		KitFeat kFeat = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("FEAT:"))
			{
				if (kFeat == null)
				{
					kFeat = new KitFeat(colString.substring(5));
				}
				else
				{
					Logging.errorPrint("Ignoring second FEAT tag \"" + colString + "\" in Kit.parseFeatLine");
				}
			}
			else
			{
				if (kFeat == null)
				{
					Logging.errorPrint("Cannot process tag, missing FEAT tag." + Constants.s_LINE_SEP + colString);
					continue;
				}
				if (colString.startsWith("FREE:"))
				{
					kFeat.setFree(colString.charAt(5) == 'Y');
				}
				else if (colString.startsWith("COUNT:"))
				{
					kFeat.setChoiceCount(colString.substring(6));
				}
				else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
				{
					kFeat.addPreReq(colString);
				}
				else
				{
					throw new PersistenceLayerException("Unknown KitFeat info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		obj.addFeat(kFeat);
	}

	private static void parseGearLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		KitGear kGear = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("GEAR:"))
			{
				if (kGear == null)
				{
					kGear = new KitGear(colString.substring(5));
				}
				else
				{
					Logging.errorPrint("Ignoring second GEAR tag \"" + colString + "\" in Kit.parseGearLine");
				}
			}
			else
			{
				if (kGear == null)
				{
					Logging.errorPrint("Cannot process tag, missing GEAR tag." + Constants.s_LINE_SEP + colString);
					continue;
				}

				if (colString.startsWith("EQMOD:"))
				{
					kGear.addEqMod(colString.substring(6));
				}
				else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
				{
					kGear.addPreReq(colString);
				}
				else if (colString.startsWith("QTY:"))
				{
					kGear.setQty(colString.substring(4));
				}
				else if (colString.startsWith("MAXCOST:"))
				{
					kGear.setMaxCost(colString.substring(8));
				}
				else if (colString.startsWith("SPROP:") || colString.startsWith("LEVEL:"))
				{
					Logging.errorPrint("unhandled parsed object in KitLoader.parseGearLine: " + colString);
				}
				else
				{
					throw new PersistenceLayerException("Unknown KitGear info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		obj.addGear(kGear);
	}

	private static void parseSkillLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		KitSkill kSkill = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("SKILL:"))
			{
				if (kSkill == null)
				{
					kSkill = new KitSkill(colString.substring(6));
				}
				else
				{
					Logging.errorPrint("Ignoring second SKILL tag \"" + colString + "\" in Kit.parseSkillLine");
				}
			}
			else
			{
				if (kSkill == null)
				{
					Logging.errorPrint("Cannot process tag, missing SKILL tag." + Constants.s_LINE_SEP + colString);
					continue;
				}
				if (colString.startsWith("RANK:"))
				{
					kSkill.setRank(colString.substring(5));
				}
				else if (colString.startsWith("FREE:"))
				{
					kSkill.setFree(colString.substring(5).startsWith("Y"));
				}
				else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
				{
					kSkill.addPreReq(colString);
				}
				else
				{
					throw new PersistenceLayerException("Unknown KitSkill info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		obj.addSkill(kSkill);
	}

	private static void parseSpellsLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final KitSpells kSpells = new KitSpells();
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("SPELLS:"))
			{
				final StringTokenizer aTok = new StringTokenizer(colString.substring(7), "|", false);
				while (aTok.hasMoreTokens())
				{
					kSpells.addSpell(aTok.nextToken());
				}
			}
			else if (colString.startsWith("COUNT:"))
			{
				kSpells.setCountFormula(colString.substring(6));
			}
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
			{
				kSpells.addPreReq(colString);
			}
			else
			{
				throw new PersistenceLayerException("Unknown KitSpells info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
		obj.addSpells(kSpells);
	}

	private static void parseSchoolLine(Kit obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		KitSchool kSchool = null;
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (colString.startsWith("SCHOOL:"))
			{
				if (kSchool == null)
				{
					kSchool = new KitSchool(colString.substring(7));
				}
				else
				{
					Logging.errorPrint("Ignoring second SCHOOL tag \"" + colString + "\" in Kit.parseSchoolLine");
				}
			}
			else
			{
				if (kSchool == null)
				{
					Logging.errorPrint("Cannot process tag, missing SCHOOL tag." + Constants.s_LINE_SEP + colString);
					continue;
				}
				if (colString.startsWith("PROHIBITED:"))
				{
					final StringTokenizer aTok = new StringTokenizer(colString.substring(11), "|");
					while (aTok.hasMoreTokens())
					{
						kSchool.addProhibited(aTok.nextToken());
					}
				}
				else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
				{
					kSchool.addPreReq(colString);
				}
				else
				{
					throw new PersistenceLayerException("Unknown KitSchool info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
			}
		}
		obj.setSchool(kSchool);
	}

}
