/*
 * GameModeLoader.java
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
 * Created on September 22, 2002, 4:33 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:26 $
 *
 */
package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.GameMode;
import pcgen.core.PObject;
import pcgen.core.character.WieldCategory;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * <code>GameModeLoader</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class GameModeLoader
{

	/**
	 * Private constructor added to inhibit instance creation for this utility class.
	 */
	private GameModeLoader()
	{
	}

	public static void parseMiscGameInfoLine(GameMode gameMode, String aLine, File aFile, int lineNum)
	{
		if (gameMode == null)
		{
			return;
		}

		if (aLine.startsWith("ALIGNMENTNAME:"))
		{
			gameMode.setAlignmentText(aLine.substring(14));
		}
		else if (aLine.startsWith("ACNAME:"))
		{
			gameMode.setACText(aLine.substring(7));
		}
		else if (aLine.startsWith("ACABBREV:"))
		{
			gameMode.setACAbbrev(aLine.substring(9));
		}
		else if (aLine.startsWith("ACTYPE:"))
		{
			gameMode.addACType(aLine.substring(7));
		}
		else if (aLine.startsWith("ALLOWEDMODES:"))
		{
			gameMode.setAllowedModes(aLine.substring(13));
		}
		else if (aLine.startsWith("ALTHPNAME:"))
		{
			gameMode.setAltHPText(aLine.substring(10));
		}
		else if (aLine.startsWith("ALTHPABBREV:"))
		{
			gameMode.setAltHPAbbrev(aLine.substring(12));
		}
		else if (aLine.startsWith("BABMAXATT:"))
		{
			gameMode.setBabMaxAtt(Integer.parseInt(aLine.substring(10)));
		}
		else if (aLine.startsWith("BABMAXLVL:"))
		{
			gameMode.setBabMaxLvl(Integer.parseInt(aLine.substring(10)));
		}
		else if (aLine.startsWith("BABATTCYC:"))
		{
			gameMode.setBabAttCyc(Integer.parseInt(aLine.substring(10)));
		}
		else if (aLine.startsWith("BABMINVAL:"))
		{
			gameMode.setBabMinVal(Integer.parseInt(aLine.substring(10)));
		}
//FLP WEAPONSIZEPENALTY3.5
		else if (aLine.startsWith("BASEDICE:"))
		{
			//BASEDICE:1d6	UP:1d8,2d6,3d6,4d6	DOWN:1d4,1d3,1d2,1,0
			parseBaseDiceLine(gameMode, aLine);
		}
//FLP WEAPONSIZEPENALTY3.5
		else if (aLine.startsWith("BONUS_ACFLAT:"))
		{
			gameMode.setAcFlatBonus(aLine.substring(13));
		}
		else if (aLine.startsWith("BONUS_ACTOUCH:"))
		{
			gameMode.setAcTouchBonus(aLine.substring(14));
		}
		else if (aLine.startsWith("BONUSFEATLEVELSTARTINTERVAL:"))
		{
			gameMode.setBonusFeatLevels(aLine.substring(28));
		}
		else if (aLine.startsWith("BONUSSTATLEVELSTARTINTERVAL:"))
		{
			gameMode.setBonusStatLevels(aLine.substring(28));
		}
		else if (aLine.startsWith("CHECKSMAXLVL:"))
		{
			gameMode.setChecksMaxLvl(Integer.parseInt(aLine.substring(13)));
		}
		else if (aLine.startsWith("CLASSTYPE:"))
		{
			gameMode.addClassType(aLine.substring(10));
		}
		else if (aLine.startsWith("CURRENCYUNIT:"))
		{
			gameMode.setCurrencyUnit(aLine.substring(13));
		}
		else if (aLine.startsWith("CURRENCYUNITABBREV:"))
		{
			gameMode.setCurrencyUnitAbbrev(aLine.substring(19));
		}
		else if (aLine.startsWith("DAMAGERESISTANCE:"))
		{
			gameMode.setDamageResistanceText(aLine.substring(17));
		}
		else if (aLine.startsWith("DEFAULTSPELLBOOK:"))
		{
			gameMode.setDefaultSpellBook(aLine.substring(17));
		}
		else if (aLine.startsWith("DEITY:"))
		{
			gameMode.addDeityList(aLine.substring(6));
		}
		else if (aLine.startsWith("DISPLAYORDER:"))
		{
			gameMode.setDisplayOrder(aLine.substring(13));
		}
		else if (aLine.startsWith("DISPLAYVARIABLE1TEXT:"))
		{
			gameMode.setVariableDisplayText(aLine.substring(21));
		}
		else if (aLine.startsWith("DISPLAYVARIABLE1NAME:"))
		{
			gameMode.setVariableDisplayName(aLine.substring(21));
		}
		else if (aLine.startsWith("DISPLAYVARIABLE2TEXT:"))
		{
			gameMode.setVariableDisplay2Text(aLine.substring(21));
		}
		else if (aLine.startsWith("DISPLAYVARIABLE2NAME:"))
		{
			gameMode.setVariableDisplay2Name(aLine.substring(21));
		}
		else if (aLine.startsWith("DISPLAYVARIABLE3TEXT:"))
		{
			gameMode.setVariableDisplay3Text(aLine.substring(21));
		}
		else if (aLine.startsWith("DISPLAYVARIABLE3NAME:"))
		{
			gameMode.setVariableDisplay3Name(aLine.substring(21));
		}
		else if (aLine.startsWith("EQSIZEPENALTY:"))
		{
			parseEqSizePenaltyLine(gameMode, aLine);
		}
		else if (aLine.startsWith("HEIGHTUNITABBREV:"))
		{
			Logging.errorPrint("Deprecated misc. game info " + aFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
		else if (aLine.startsWith("HPNAME:"))
		{
			gameMode.setHPText(aLine.substring(7));
		}
		else if (aLine.startsWith("HPABBREV:"))
		{
			gameMode.setHPAbbrev(aLine.substring(9));
		}
		else if (aLine.startsWith("LEVELMSG:"))
		{
			gameMode.setLevelUpMessage(aLine.substring(9).replace('|', '\n'));
		}
		else if (aLine.startsWith("LEVELMSG2:"))
		{
			gameMode.setLevelDownMessage(aLine.substring(10).replace('|', '\n'));
		}
		else if (aLine.startsWith("MENUENTRY:"))
		{
			gameMode.setModeName(aLine.substring(10));
		}
		else if (aLine.startsWith("MENUTOOLTIP:"))
		{
			gameMode.setModeToolTip(aLine.substring(12));
		}
		else if (aLine.startsWith("MOVEUNIT:"))
		{
			Logging.errorPrint("Deprecated misc. game info " + aFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
		else if (aLine.startsWith("MOVEUNITABBREV:"))
		{
			Logging.errorPrint("Deprecated misc. game info " + aFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
		else if (aLine.startsWith("PLUSCOST:"))
		{
			gameMode.addPlusCalculation(aLine.substring(9));
		}
		else if (aLine.startsWith("SHOWCLASSDEFENSE:"))
		{
			gameMode.setShowClassDefense(aLine.charAt(17) == 'Y');
		}
		else if (aLine.startsWith("SHORTRANGE:"))
		{
			gameMode.setShortRangeDistance(Integer.parseInt(aLine.substring(11)));
		}
		else if (aLine.startsWith("SHOWTAB:"))
		{
			aLine = aLine.substring(8);
			final StringTokenizer aTok = new StringTokenizer(aLine, "|");
			final String tabName = aTok.nextToken();
			final String anArg = aTok.nextToken();
			final int iTab = GameMode.getTabNumber(tabName);
			if (iTab == Constants.TAB_INVALID)
			{
				return;
			}
			gameMode.setTabVisible(iTab, anArg);
		}
		else if (aLine.startsWith("SKILLMULTIPLIER:"))
		{
			gameMode.setSkillMultiplierLevels(aLine.substring(16));
		}
		else if (aLine.startsWith("SPELLBASEDC:"))
		{
			gameMode.setSpellBaseDC(aLine.substring(12));
		}
		else if (aLine.startsWith("SPELLRANGE:"))
		{
			gameMode.setSpellRangeFormula(aLine.substring(11));
		}
		else if (aLine.startsWith("TAB:"))
		{
			//TAB:DOMAINS [tab] VISIBLE:YES [tab] NAME:Domains [tab] CONTEXT:path
			parseTabLine(gameMode, aLine);
		}
		else if (aLine.startsWith("WEAPONCATEGORY:"))
		{
			gameMode.addWeaponCategory(aLine.substring(15));
		}
		else if (aLine.startsWith("WEAPONTYPE:"))
		{
			gameMode.addWeaponType(aLine.substring(11));
		}
		else if (aLine.startsWith("WEIGHTUNITABBREV:"))
		{
			Logging.errorPrint("Deprecated misc. game info " + aFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
		else if (aLine.startsWith("WCSTEPSFORMULA:"))
		{
			gameMode.setWCStepsFormula(aLine.substring(15));
		}
		else if (aLine.startsWith("WIELDCATEGORY:"))
		{
			parseWieldCategoryLine(gameMode, aLine);
		}
		else
		{
			Logging.errorPrint("Illegal misc. game info " + aFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
	}

	/**
	 * TAB:DOMAINS [tab] VISIBLE:YES [tab] NAME:Domains [tab] CONTEXT:path
	 *
	 * @param gameMode
	 * @param aLine
	 */
	private static void parseTabLine(GameMode gameMode, String aLine)
	{
		final StringTokenizer aTok = new StringTokenizer(aLine, SystemLoader.TAB_DELIM);
		int iTab = Constants.TAB_INVALID;
		while (aTok.hasMoreTokens())
		{
			final String aString = aTok.nextToken();
			if (aString.startsWith("CONTEXT:"))
			{
				gameMode.setTabContext(iTab, aString.substring(8));
			}
			else if (aString.startsWith("NAME:"))
			{
				gameMode.setTabName(iTab, aString.substring(5));
			}
			else if (aString.startsWith("TAB:"))
			{
				iTab = GameMode.getTabNumber(aString.substring(4));
				if (iTab == Constants.TAB_INVALID)
				{
					return;
				}
			}
			else if (aString.startsWith("VISIBLE:"))
			{
				gameMode.setTabVisible(iTab, aString.substring(8));
			}
		}
	}

//FLP WEAPONSIZEPENALTY3.5
	private static void parseEqSizePenaltyLine(GameMode gameMode, String aLine)
	{
		PObject eqSizePenaltyObj = new PObject();
		final StringTokenizer colToken = new StringTokenizer(aLine, SystemLoader.TAB_DELIM);
		String colString;
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();
			if (colString.startsWith("EQSIZEPENALTY:"))
			{
				eqSizePenaltyObj.setName(colString.substring(14));
			}
			else if (PObjectLoader.parseTag(eqSizePenaltyObj, colString))
			{
				continue;
			}
			else
			{
				Logging.errorPrint("Illegal Value in EQSIZEPENALTY line: " + colString);
			}
		}
		// Now set the penalty object in this gameMode
		gameMode.setEqSizePenaltyObj(eqSizePenaltyObj);
	}

	private static void parseBaseDiceLine(GameMode gameMode, String aLine)
	{
		StringTokenizer aTok = new StringTokenizer(aLine.substring(9), SystemLoader.TAB_DELIM);
		try
		{
			String baseDice = aTok.nextToken();
			String upString = "";
			String downString = "";
			while (aTok.hasMoreTokens())
			{
				String bString = aTok.nextToken();
				if (bString.startsWith("UP:"))
				{
					upString = bString.substring(3);
				}
				else if (bString.startsWith("DOWN:"))
				{
					downString = bString.substring(5);
				}
				else
				{
					Logging.errorPrint("Invalid sub tag " + bString + " on BASEDICE line");
				}
			}
			if (!gameMode.getDamageUpMap().containsKey(baseDice))
			{
				gameMode.getDamageUpMap().put(baseDice, upString);
				gameMode.getDamageDownMap().put(baseDice, downString);
			}
			else
			{
				Logging.errorPrint("Duplicate BASEDICE: tag on gamemode.");
			}
		}
		catch (Exception e)
		{
			Logging.errorPrint("Problems loading line - " + aLine);
		}
	}

	/**
	 * Parse through a WIELDCATEGORY: line and setup the proper info
	 */
	private static void parseWieldCategoryLine(GameMode gameMode, String aLine)
	{
		StringTokenizer aTok = new StringTokenizer(aLine, SystemLoader.TAB_DELIM);
		WieldCategory wCat = null;
		String preKey = null;
		String preVal = null;
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (aString.startsWith("WIELDCATEGORY:"))
			{
				// This is the name of the category
				String aName = aString.substring(14);
				wCat = gameMode.getWieldCategory(aName);
				if (wCat == null)
				{
					wCat = new WieldCategory(aName);
					gameMode.addWieldCategory(wCat);
				}
			}
			if (wCat == null)
			{
				continue;
			}
			else if (aString.startsWith("DAMAGEMULT:"))
			{
				// The damage multiplier based on
				// number of hands used to wield weapon
				String dString = aString.substring(11);
				// dString is of form:
				// 1=1,2=1.5
				StringTokenizer dTok = new StringTokenizer(dString, ",");
				while (dTok.hasMoreTokens())
				{
					String cString = dTok.nextToken();
					// cString is of form: 2=1.5
					StringTokenizer cTok = new StringTokenizer(cString, "=");
					if (cTok.countTokens() < 2)
					{
						continue;
					}
					String aKey = cTok.nextToken();
					String aVal = cTok.nextToken();
					wCat.addDamageMultMap(aKey, aVal);
				}
			}
			else if (aString.startsWith("FINESSABLE:"))
			{
				// Is this category finessable?
				if (aString.substring(11).startsWith("Y"))
				{
					wCat.setFinessable(true);
				}
			}
			else if (aString.startsWith("HANDS:"))
			{
				// Minimum hands required to wield weapon
				Integer hands = new Integer(aString.substring(6));
				wCat.setHands(hands.intValue());
			}
			else if (aString.startsWith("PREVAR"))
			{
				// a PREVARxx formula used to switch
				// weapon categories based on size
				preKey = aString;
			}
			else if (aString.startsWith("SWITCH:"))
			{
				// If matches PRE, switch category to this
				preVal = aString.substring(7);
			}
			else if (aString.startsWith("SIZEDIFF:"))
			{
				// Number of size categories Object Size diff
				Integer sInt = new Integer(aString.substring(9));
				wCat.setSizeDiff(sInt.intValue());
			}
			// The wield category steps
			else if (aString.startsWith("UP:"))
			{
				String dString = aString.substring(3);
				StringTokenizer dTok = new StringTokenizer(dString, "|");
				int count = 1;
				while (dTok.hasMoreTokens())
				{
					dString = dTok.nextToken();
					wCat.setWCStep(count, dString);
					++count;
				}
			}
			else if (aString.startsWith("DOWN:"))
			{
				String dString = aString.substring(5);
				StringTokenizer dTok = new StringTokenizer(dString, "|");
				int count = -1;
				while (dTok.hasMoreTokens())
				{
					dString = dTok.nextToken();
					wCat.setWCStep(count, dString);
					--count;
				}
			}
			else if (aString.startsWith("ZERO:"))
			{
				String dString = aString.substring(5);
				int count = 0;
				wCat.setWCStep(count, dString);
			}
		}
		if ((wCat != null) && (preVal != null) && (preKey != null))
		{
			wCat.addSwitchMap(preKey, preVal);
		}
	}

}
