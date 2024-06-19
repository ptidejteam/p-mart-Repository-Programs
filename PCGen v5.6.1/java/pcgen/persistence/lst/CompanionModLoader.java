/*
 * CompanionModLoader.java
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
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:33:26 $
 *
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * Loads the level based Mount and Familiar benefits
 *
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/
final class CompanionModLoader
{

	/** Creates a new instance of CompanionModLoader */
	private CompanionModLoader()
	{
	}

	public static void parseLine(CompanionMod aComp, String inputLine, URL sourceURL, int lineNum)
	{
		if (aComp == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		String colString;
		if (!aComp.isNewItem())
		{
			// .MOD skips required fields
			// FOLLOWER:Classname=Level in this case
			colToken.nextToken();
		}
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(aComp, colString))
			{
				continue;
			}
			if (colString.startsWith("FOLLOWER:"))
			{
				final StringTokenizer aTok = new StringTokenizer(colString.substring(9), "=");
				final String someClasses = aTok.nextToken();
				final String aLev = aTok.nextToken();
				aComp.setLevel(Integer.parseInt(aLev));
				final StringTokenizer bTok = new StringTokenizer(someClasses, ",");
				while (bTok.hasMoreTokens())
				{
					final String cN = bTok.nextToken();
					final PCClass nc = Globals.getClassNamed(cN);
					if (nc != null)
					{
						aComp.getClassMap().put(cN, aLev);
					}
					else
					{
						// Now we accept VARiable names here.
						aComp.getVarMap().put(cN, aLev);
						//Logging.errorPrint("cML:Class not found: " + cN);
					}
				}
			}
			else if (colString.startsWith("HD:"))
			{
				aComp.setHitDie(Integer.parseInt(colString.substring(3)));
			}
			else if (colString.startsWith("SWITCHRACE:"))
			{
				final StringTokenizer aTok = new StringTokenizer(colString.substring(11), "|", false);
				final String currT = aTok.nextToken();
				final String toT = aTok.nextToken();
				aComp.getSwitchRaceMap().put(currT.toUpperCase(), toT.toUpperCase());
			}
			else if (colString.startsWith("MASTERBONUSRACE:"))
			{
				final String fRace = colString.substring(16);
				aComp.getClassMap().put(fRace.toUpperCase(), "1");
			}
			else if (colString.startsWith("USEMASTERSKILL:"))
			{
				aComp.setUseMasterSkill(colString.substring(15).startsWith("Y"));
			}
			else if (colString.startsWith("COPYMASTERHP:"))
			{
				aComp.setCopyMasterHP(colString.substring(13));
			}
			else if (colString.startsWith("COPYMASTERBAB:"))
			{
				aComp.setCopyMasterBAB(colString.substring(14));
			}
			else if (colString.startsWith("COPYMASTERCHECK:"))
			{
				aComp.setCopyMasterCheck(colString.substring(16));
			}
			else
			{
				Logging.errorPrint(sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
