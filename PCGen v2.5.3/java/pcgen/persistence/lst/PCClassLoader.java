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
 * $Id: PCClassLoader.java,v 1.1 2006/02/20 23:54:41 vauchers Exp $
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
			final String colString = new String(colToken.nextToken());
			if (option < 15)
				option = col;
			if (col == 0 && option == 15)
			{
				aInt = Integer.parseInt(colString);
				continue;
			}
			if (PObjectLoader.parseTagLevel(obj, colString, aInt))
				continue;
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
					if (colString.startsWith("ABB:"))
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
					else if (colString.startsWith("DEFINE:"))
						obj.addVariableList(aInt + "|" + colString.substring(7));
					else if (colString.startsWith("DEF:"))
						obj.setDefenseString(colString.substring(4));
					else if (colString.startsWith("DEITY:"))
						obj.setDeityString(colString.substring(6));
					else if (colString.startsWith("DOMAIN:"))
						obj.addDomainList(aInt + "|" + colString.substring(7));
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
					else if (colString.startsWith("KEY:"))
						obj.setKeyName(colString.substring(4));
					else if (colString.startsWith("KNOWN:"))
						obj.getKnownList().add(colString.substring(6));
					else if (colString.startsWith("SPECIALTYKNOWN:"))
						obj.getSpecialtyKnownList().add(colString.substring(15));
					else if (colString.startsWith("KNOWNSPELLS:"))
						obj.addKnownSpellsList(colString.substring(12));
					else if (colString.startsWith("KNOWNSPELLSFROMSPECIALTY:"))
						obj.setKnownSpellsFromSpecialty(Integer.parseInt(colString.substring(25)));
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
					else if (colString.startsWith("PRERACETYPE:"))
						obj.setPreRaceType(colString.substring(12));
					else if (colString.startsWith("PRE") || colString.startsWith("!PRE") || colString.startsWith("RESTRICT:"))
						obj.addPreReq(colString);
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
						if (flag == false) index = 6;
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
						obj.setSubClassString(aInt + "|" + colString.substring(9));
					else if (colString.startsWith(Constants.s_TAG_TYPE))
					{
						obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
					}
					else if (colString.startsWith("TEMPLATE:"))
						obj.addTemplate(aInt + "|" + colString.substring(9));
					else if (colString.startsWith("UATT:"))
						obj.getUattList().add(colString.substring(5));
					else if (colString.startsWith("UDAM:"))
						obj.getUdamList().add(colString.substring(5));
					else if (colString.startsWith("UMULT:"))
						obj.addUmult(aInt + "|" + colString.substring(6));
					else if (colString.startsWith("VFEAT:"))
						obj.addVFeatList(aInt, colString.substring(6));
					else if (colString.startsWith("VISIBLE:"))
						obj.setVisible(colString.substring(8).startsWith("Y"));
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

/*
	public static void parseLine(Feat feat, String inputLine, File sourceFile, int lineNum) {
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		String aCol = null;
		for (col = 0; col < colMax; col++) {
			aCol = colToken.nextToken();
			if (PObjectLoader.parseTag(feat, aCol))
				continue;
			final int len = aCol.length();
			if (col == 0) {
				feat.setName(aCol);
			} else if ((len > 14) && aCol.startsWith("ADDSPELLLEVEL:")) {
				try {
					feat.setAddSpellLevel(Delta.parseInt(aCol.substring(14)));
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Bad addSpellLevel " + aCol, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					// LATER: This should throw an exception that is caught in the front end.
					// throw new PersistenceLayerException("Bad addSpellLevel " + aCol);
				}
			} else if (aCol.startsWith("ADD:")) {
				feat.setAddString(aCol.substring(4));
			} else if (aCol.startsWith("BONUS")) {
				feat.addBonusList(aCol.substring(6));
			} else if (aCol.startsWith("DESC")) {
				feat.setDescription(aCol.substring(5));
				//Is this like PRESKILl
			} else if (aCol.startsWith("SKILL:")) {
				feat.setSkillNameList(aCol.substring(6));
			} else if (aCol.startsWith(Constants.s_TAG_TYPE)) {
				feat.setType(aCol.substring(Constants.s_TAG_TYPE.length()));
			} else if (aCol.startsWith("MULT")) {
				feat.setMultiples(aCol.substring(5));
			} else if (aCol.startsWith("STACK")) {
				feat.setStacks(aCol.substring(6));
			} else if (aCol.startsWith("CHOOSE")) {
				feat.setChoiceString(aCol.substring(7));
			} else if (aCol.startsWith("CSKILL")) {
				feat.setCSkillList(aCol.substring(7));
			} else if (aCol.startsWith("CCSKILL")) {
				feat.setCCSkillList(aCol.substring(8));
			} else if (aCol.startsWith("REP")) {
				try {
					feat.setLevelsPerRepIncrease(Delta.decode(aCol.substring(4)));
				}
				catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Bad level per value " + aCol, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					// LATER: This should throw an exception that is caught in the front end.
					// throw new PersistenceLayerException("Bad level per value " + aCol);
				}
			} else if (aCol.startsWith("DEFINE")) {
				feat.addVariableList("0|" + aCol.substring(7));
			} else if (aCol.startsWith("KEY:")) {
				feat.setKeyName(aCol.substring(4));
			} else if (aCol.startsWith("PRE") || aCol.startsWith("!PRE")) {
				feat.addPreReq(aCol);
			} else if (aCol.startsWith("QUALIFY:")) {
				//					JOptionPane.showMessageDialog(null, "This is:" + this, Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				//				System.out.println("This is:" + this);
				feat.addToQualifyListing(aCol.substring(8));
			} else if (aCol.startsWith("VISIBLE:")) {
				if (aCol.substring(8).startsWith("Export")) {
					feat.setVisible(Feat.VISIBILITY_OUTPUT_ONLY);
				}
				else if (aCol.substring(8).startsWith("No")) {
					feat.setVisible(Feat.VISIBILITY_HIDDEN);
				}
				else if (aCol.substring(8).startsWith("Display")) {
					feat.setVisible(Feat.VISIBILITY_DISPLAY_ONLY);
				}
				else {
					feat.setVisible(Feat.VISIBILITY_DEFAULT);
				}
			} else if (aCol.startsWith("COST")) {
				feat.setCost(Double.parseDouble(aCol.substring(5)));
			} else {
				JOptionPane.showMessageDialog
				(null, "Illegal feat info " +
				sourceFile.getName() + ":" + Integer.toString(lineNum) +
				" \"" + aCol + "\"", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);

				// LATER: This should throw an exception that is caught in the front end.
				// throw new PersistenceLayerException(sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aCol + "\"");
			}
		}
	}
 */
}
