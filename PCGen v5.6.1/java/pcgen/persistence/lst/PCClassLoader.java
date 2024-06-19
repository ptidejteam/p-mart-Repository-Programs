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
 * $Id: PCClassLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public final class PCClassLoader extends LstObjectFileLoader
{
	/** Creates a new instance of PCClassLoader */
	public PCClassLoader()
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
		PCClass pcClass = (PCClass) target;
		if (pcClass == null)
		{
			pcClass = new PCClass();
		}

		final StringTokenizer colToken =
			new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		int iLevel = 0;
		boolean isNumber = true;
		boolean handled = true;

		if (lstLine.startsWith("SUBCLASS:")
			|| lstLine.startsWith("SUBCLASSLEVEL:"))
		{
			SubClass subClass = null;
			if (lstLine.startsWith("SUBCLASS:"))
			{
				final String n = lstLine.substring(9, lstLine.indexOf("\t"));
				subClass = pcClass.getSubClassNamed(n);
				if (subClass == null)
				{
					subClass = new SubClass();
					subClass.setSourceCampaign(source.getCampaign());
					subClass.setSourceFile(source.getFile());
					pcClass.addSubClass(subClass);
				}
			}
			else
			{
				if (pcClass.getSubClassList() != null
					&& !pcClass.getSubClassList().isEmpty())
				{
					subClass =
						(SubClass) pcClass.getSubClassList().get(
							pcClass.getSubClassList().size() - 1);
					subClass.addToLevelArray(lstLine.substring(14));
					return pcClass;
				}
			}
			if (subClass != null)
			{
				SubClassLoader.parseLine(subClass, lstLine, source);
			}
			return pcClass;
		}

		// loop through all the tokens and parse them
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			if (colString.startsWith("CLASS:"))
			{
				isNumber = false;
				String name = colString.substring(6);
				if( (!name.equals(pcClass.getName())) 
					&& (name.indexOf(".MOD")<0))
				{
					finishObject(pcClass);
					pcClass = new PCClass();
					pcClass.setName(name);
					pcClass.setSourceFile(source.getFile());
					pcClass.setSourceCampaign(source.getCampaign());
				}
			}
			else if (!(pcClass instanceof SubClass) && (isNumber))
			{
				try
				{
					iLevel = Integer.parseInt(colString);
				}
				catch (NumberFormatException nfe)
				{
					// I think we can ignore this, as
					// it's supposed to be the level #
					// but could be almost anything else
					Logging.errorPrint(
						"Non-Numeric Level info '"
							+ colString
							+ "' in "
							+ source.getFile(),
						nfe);
				}
				isNumber = false;
				continue;
			}
			else if (colString.startsWith("ABB:"))
			{
				pcClass.setAbbrev(colString.substring(4));
			}
			else if (colString.startsWith("AC"))
			{
				pcClass.getACList().add(colString.substring(3));
			}
			else if (colString.startsWith("ADDDOMAINS:"))
			{
				pcClass.setAddDomains(iLevel, colString.substring(11), ".");
			}
			else if (colString.startsWith("AGESET:"))
			{
				//pcClass.setAgeSet(Integer.parseInt(colString.substring(7)));
				Logging.errorPrint(
					"AGESET is a deprecated tag in "
						+ source.getFile()
						+ ". This functionality is now handled in biosettings.lst");
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
				if(iLevel > 0)
				{
					pcClass.setCastMap(iLevel, colString.substring(5));
				}
				else
				{
					Logging.errorPrint("CAST tag without level not allowed!");
				}
			}
			else if (colString.startsWith("CASTAS:"))
			{
				pcClass.setCastAs(colString.substring(7));
			}
			else if (colString.startsWith("CHECK"))
			{
				continue;
			}
			else if (colString.startsWith("DEITY:"))
			{
				pcClass.setDeityString(colString.substring(6));
			}
			else if (colString.startsWith("DOMAIN:"))
			{
				pcClass.addDomainList(
					fixParameter(iLevel, colString.substring(7)));
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
				pcClass.addFeatList(iLevel, colString.substring(5));
			}
			else if (colString.startsWith("FEATAUTO:"))
			{
				pcClass.setFeatAutos(iLevel, colString.substring(9));
			}
			else if (colString.startsWith("CRFORMULA:"))
			{
				pcClass.setCRFormula(colString.substring(10));
			}
			else if (colString.startsWith("XPPENALTY:"))
			{
				pcClass.setXPPenalty(colString.substring(10));
			}
			else if (colString.startsWith("ISMONSTER:"))
			{
				pcClass.setMonsterFlag(colString.substring(10));
			}
			else if (colString.equals("HASSUBCLASS"))
			{
				pcClass.setHasSubClass(true);
			}
			else if (colString.startsWith("GOLD:"))
			{
				Logging.errorPrint(
					"GOLD: is a deprecated tag in "
						+ source.getFile()
						+ ". This functionality is now handled in biosettings.lst");
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
				pcClass.addKnown(iLevel, colString.substring(6));
			}
			else if (colString.startsWith("KNOWNSPELLS:"))
			{
				pcClass.addKnownSpellsList(colString.substring(12));
			}
			else if (colString.startsWith("KNOWNSPELLSFROMSPECIALTY:"))
			{
				pcClass.setNumSpellsFromSpecialty(
					Integer.parseInt(colString.substring(25)));
			}
			else if (colString.startsWith("LANGBONUS"))
			{
				pcClass.setLanguageBonus(colString.substring(10));
			}
			else if (colString.startsWith("LEVELSPERFEAT:"))
			{
				pcClass.setLevelsPerFeat(
					new Integer(colString.substring(14)));
			}
			else if (colString.startsWith("MAXLEVEL:"))
			{
				pcClass.setMaxLevel(Integer.parseInt(colString.substring(9)));
			}
			else if (colString.startsWith("MEMORIZE:"))
			{
				pcClass.setMemorizeSpells(
					colString.substring(9).startsWith("Y"));
			}
			else if (colString.startsWith("MODTOSKILLS:"))
			{
				pcClass.setModToSkills(
					!"No".equalsIgnoreCase(colString.substring(12)));
			}
			else if (colString.startsWith("MONSKILL:"))
			{
				pcClass.addBonusList(
					"0|MONSKILLPTS|NUMBER|"
						+ colString.substring(9).trim()
						+ "|PRELEVELMAX:1",
					pcClass);
			}
			else if (colString.startsWith("MULTIPREREQS"))
			{
				pcClass.setMultiPreReqs(true);
			}
			else if (colString.startsWith("PRERACETYPE:"))
			{
				pcClass.setPreRaceType(colString.substring(12));
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
				// TODO: Obselete in 5.7
				Logging.errorPrint("SPECIALS in class files is now deprecated, please use feats instead.");
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
				pcClass.setSkillPoints(
					Integer.parseInt(colString.substring(14)));
			}
			else if (colString.startsWith("SUBCLASS:"))
			{
				pcClass.setSubClassString(
					fixParameter(iLevel, colString.substring(9)));
			}
			else if (colString.startsWith("SUBSA:"))
			{
				Logging.errorPrint(
					"SUBSA is a deprecated tag in "
						+ source.getFile()
						+ ". This functionality is now handled in biosettings.lst");
			}
			else if (colString.startsWith("TEMPLATE:"))
			{
				pcClass.addTemplate(
					fixParameter(iLevel, colString.substring(9)));
			}
			else if (colString.startsWith("UATT:"))
			{
				pcClass.getUattList().add(colString.substring(5));
			}
			else if (colString.startsWith("VFEAT:"))
			{
				pcClass.addVirtualFeat(iLevel, colString.substring(6));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				pcClass.setVisible(
					colString.substring(8).toUpperCase().startsWith("Y"));
			}
			else if (colString.startsWith("WEAPONBONUS"))
			{
				pcClass.setWeaponProfBonus(colString.substring(12));
			}
			else if (colString.startsWith("XTRAFEATS:"))
			{
				pcClass.setInitialFeats(
					Integer.parseInt(colString.substring(10)));
			}
			else if (PObjectLoader.parseTagLevel(pcClass, colString, iLevel))
			{
				continue;
			}
			else
			{
				if (!(pcClass instanceof SubClass))
				{
					Logging.errorPrint(
						"Illegal class info tag '"
							+ colString
							+ "' in "
							+ source.getFile());
				}
			}
			isNumber = false;
		}
		return pcClass;
	}

	private String fixParameter(int aInt, final String colString)
	{
		return new StringBuffer()
			.append(aInt)
			.append("|")
			.append(colString)
			.toString();
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#finishObject(pcgen.core.PObject)
	 */
	protected void finishObject(PObject target)
	{
		if (includeObject(target))
		{
			// This class already exists, so lets
			// compare source files to see if its
			// a duplicate named entry
			final PCClass bClass = Globals.getClassNamed(target.getName());
			if (bClass == null)
			{
				Globals.getClassList().add(target);
			}
			else
			{
				if (!bClass.getSourceFile().equals(target.getSourceFile()))
				{
					// Duplicate loading error
					Logging.errorPrint(
						"WARNING: Duplicate class name: " + target.getName());
					Logging.errorPrint("Original : " + bClass.getSourceFile());
					Logging.errorPrint("Duplicate: " + target.getSourceFile());
					Logging.errorPrint("WARNING: Not loading duplicate");
				}
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#getObjectNamed(java.lang.String)
	 */
	protected PObject getObjectNamed(String baseName)
	{
		return Globals.getClassNamed(baseName);
	}

	/**
	 * @see pcgen.persistence.lst.LstObjectFileLoader#performForget(pcgen.core.PObject)
	 */
	protected void performForget(PObject objToForget)
		throws PersistenceLayerException
	{
		Globals.getClassList().remove(objToForget);
	}

}
