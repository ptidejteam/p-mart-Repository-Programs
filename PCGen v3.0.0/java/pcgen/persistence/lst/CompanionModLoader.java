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
 *************************************************************************
 *
 * @author Jayme Cox <jaymecox@excite.com>
 * @Created on July 10th, 2002, 3:55 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:02:33 $
 *
 *************************************************************************/

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.character.CompanionMod;
import pcgen.persistence.PersistenceLayerException;

/**
 * Loads the level based Mount and Familiar benefits
 *
 * @author Jayme Cox <jaymecox@excite.com>
 * @version $Revision: 1.1 $
 **/
public class CompanionModLoader
{

	/** Creates a new instance of CompanionModLoader */
	private CompanionModLoader()
	{
	}

	public static void parseLine(CompanionMod aComp, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		String colString = null;
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
				continue;
			if (colString.startsWith("FOLLOWER:"))
			{
				StringTokenizer aTok = new StringTokenizer(colString.substring(9), "=", false);
				String someClasses = aTok.nextToken();
				String aLev = aTok.nextToken();
				aComp.setLevel(Integer.parseInt(aLev));
				StringTokenizer bTok = new StringTokenizer(someClasses, ",", false);
				while (bTok.hasMoreTokens())
				{
					String cN = bTok.nextToken();
					PCClass nc = Globals.getClassNamed(cN);
					if (nc != null)
						aComp.getClassMap().put(cN, aLev);
					else
						Globals.debugPrint("cML:Class nout found: " + cN);
				}
			}
			else if (colString.startsWith("TYPE:"))
			{
				aComp.setType(colString.substring(5));
			}
			else if (colString.startsWith("HD:"))
			{
				aComp.setHitDie(Integer.parseInt(colString.substring(3)));
			}
			else if (colString.startsWith("SWITCHRACE:"))
			{
				StringTokenizer aTok = new StringTokenizer(colString.substring(11), "|", false);
				String currT = aTok.nextToken();
				String toT = aTok.nextToken();
				Globals.getCompanionSwitchRaceMap().put(currT, toT);
			}
			else
			{
				throw new PersistenceLayerException(sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
