/*
 * DeityLoader.java
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
 * $Id: DeityLoader.java,v 1.1 2006/02/20 23:57:40 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Deity;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class DeityLoader
{

	/** Creates a new instance of DeityLoader */
	private DeityLoader()
	{
	}

	public static void parseLine(Deity deity, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		StringTokenizer colToken =
			new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = 0;
		if (!deity.isNewItem())
		{
			col = 6; // .MOD skip required fields (5 of them in this case)
			colToken.nextToken(); // skip name
		}
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(deity, colString))
				continue;
			final int colLen = colString.length();
			if ((colLen > 5) && colString.startsWith("RACE:"))
			{
				deity.setRaceList(colString.substring(5));
			}
			/*
			 * freq #465977
			 * author: Thomas Behr 05-03-02
			 */
			else if ((colLen > 9) && colString.startsWith("PANTHEON:"))
			{
				deity.setPantheonList(colString.substring(9));
			}
			else if ((colLen > 6) && colString.startsWith("ALIGN:"))
			{
				deity.setDeityAlignment(colString.substring(6));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				deity.setQualifyString(colString.substring(8));
			}
			else if ((colLen > 3) && colString.startsWith("SA:"))
			{
				deity.addSpecialAbility(colString.substring(3));
			}
			else if (colString.startsWith("DOMAINS:"))
				deity.setDomainList(colString.substring(8));
			else if (colString.startsWith("FOLLOWERALIGN:"))
				deity.setFollowerAlignments(colString.substring(14));
			else if (colString.startsWith("DESC:"))
				deity.setDescription(colString.substring(5));
			else if (colString.startsWith("SYMBOL:"))
				deity.setHolyItem(colString.substring(7));
			else if (colString.startsWith("DEITYWEAP:"))
				deity.setFavoredWeapon(colString.substring(10));
			else if (col>=0 && col<6)
			{
				switch(col)
				{
					case 0: deity.setName(colString);
						break;
					case 1: deity.setDomainList(colString);
						break;
					case 2: deity.setFollowerAlignments(colString);
						break;
					case 3: deity.setDescription(colString);
						break;
					case 4: deity.setHolyItem(colString);
						break;
					case 5: deity.setFavoredWeapon(colString);
						break;
				}
			col++;
			}
			else
			{
				throw new PersistenceLayerException("Illegal deity info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
