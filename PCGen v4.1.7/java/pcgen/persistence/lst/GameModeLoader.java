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
 * $Id: GameModeLoader.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */
package pcgen.persistence.lst;

import java.io.File;
import pcgen.core.GameMode;
import pcgen.persistence.PersistenceLayerException;

/**
 *  <code>GameModeLoader</code>.
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */

final class GameModeLoader
{
	public static void parseMiscGameInfoLine(GameMode gameMode, String aLine, File aFile, int lineNum) throws PersistenceLayerException
	{
		if (aLine.startsWith("ALIGNMENTNAME:"))
		{
			gameMode.setAlignmentText(aLine.substring(14));
		}
		else if (aLine.startsWith("BONUSFEATLEVELSTARTINTERVAL:"))
		{
			gameMode.setBonusFeatLevels(aLine.substring(28));
		}
		else if (aLine.startsWith("BONUSSTATLEVELSTARTINTERVAL:"))
		{
			gameMode.setBonusStatLevels(aLine.substring(28));
		}
		else if (aLine.startsWith("CURRENCYUNIT:"))
		{
			gameMode.setCurrencyUnit(aLine.substring(13));
		}
		else if (aLine.startsWith("CURRENCYUNITABBREV:"))
		{
			gameMode.setCurrencyUnitAbbrev(aLine.substring(19));
		}
		else if (aLine.startsWith("DEFAULTSPELLBOOK:"))
		{
			gameMode.setDefaultSpellBook(aLine.substring(17));
		}
		else if (aLine.startsWith("DEFENSENAME:"))
		{
			gameMode.setDefenseText(aLine.substring(12));
		}
		else if (aLine.startsWith("DEITY:"))
		{
			gameMode.addDeityList(aLine.substring(6));
		}
		else if (aLine.startsWith("HEIGHTUNITABBREV:"))
		{
			gameMode.setHeightAbbrev(aLine.substring(17));
		}
		else if (aLine.startsWith("HPABBREV:"))
		{
			gameMode.setHPAbbrev(aLine.substring(9));
		}
		else if (aLine.startsWith("HPNAME:"))
		{
			gameMode.setHPText(aLine.substring(7));
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
			gameMode.setMoveUnit(aLine.substring(9));
		}
		else if (aLine.startsWith("MOVEUNITABBREV:"))
		{
			gameMode.setMoveUnitAbbrev(aLine.substring(15));
		}
		else if (aLine.startsWith("REPUTATIONNAME:"))
		{
			gameMode.setReputationText(aLine.substring(15));
		}
		else if (aLine.startsWith("SHOWTAB:"))
		{
			aLine = aLine.substring(8);
			if (aLine.startsWith("SPELLS|"))
			{
				gameMode.setShowSpellTab(aLine.charAt(7) == 'Y');
			}
			else if (aLine.startsWith("DOMAINS|"))
			{
				gameMode.setShowDomainTab(aLine.charAt(8) == 'Y');
			}
		}
		else if (aLine.startsWith("SKILLMULTIPLIER:"))
		{
			gameMode.setSkillMultiplierLevels(aLine.substring(16));
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
			gameMode.setWeightUnitAbbrev(aLine.substring(17));
		}
		else if (aLine.startsWith("WOUNDPOINTSNAME:"))
		{
			gameMode.setWoundPointsText(aLine.substring(16));
		}
		else
		{
			throw new PersistenceLayerException("Illegal misc. game info " + aFile.getName() + ":" + Integer.toString(lineNum) + " \"" + aLine + "\"");
		}
	}
}
