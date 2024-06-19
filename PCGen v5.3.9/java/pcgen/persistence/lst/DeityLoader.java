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
 * $Id: DeityLoader.java,v 1.1 2006/02/21 01:16:12 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Deity;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;

/**
 *
 * ???
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class DeityLoader
{

	/** Creates a new instance of DeityLoader */
	private DeityLoader()
	{
	}

	public static void parseLine(Deity deity, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (deity == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
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
			{
				continue;
			}

			final int colLen = colString.length();
			if ((colLen > 6) && colString.startsWith("ALIGN:"))
			{
				deity.setAlignment(colString.substring(6));
			}
			//else if (colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	deity.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			//}
			else if (colString.startsWith("DEITYWEAP:"))
			{
				deity.setFavoredWeapon(colString.substring(10));
			}
			//else if (colString.startsWith("DESC:"))
			//{
			//	deity.setDescription(pcgen.io.EntityEncoder.decode(colString.substring(5)));
			//}
			else if (colString.startsWith("DOMAINS:"))
			{
				deity.setDomainListString(colString.substring(8));
			}
			else if (colString.startsWith("FOLLOWERALIGN:"))
			{
				deity.setFollowerAlignments(colString.substring(14));
			}
			else if ((colLen > 9) && colString.startsWith("PANTHEON:"))
			{
				deity.setPantheonList(colString.substring(9));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				deity.setQualifyString(colString.substring(8));
			}
			else if ((colLen > 5) && colString.startsWith("RACE:"))
			{
				deity.setRaceList(colString.substring(5));
			}
			else if (colString.startsWith("SYMBOL:"))
			{
				deity.setHolyItem(colString.substring(7));
			}
			else if (col >= 0 && col < 6)
			{
				switch (col)
				{
					case 0:
						deity.setName(colString);
						break;
					case 1:
						deity.setDomainListString(colString);
						break;
					case 2:
						deity.setFollowerAlignments(colString);
						break;
					case 3:
						deity.setDescription(colString);
						break;
					case 4:
						deity.setHolyItem(colString);
						break;
					case 5:
						deity.setFavoredWeapon(colString);
						break;
					default:
						throw new PersistenceLayerException("Illegal deity info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
				}
				col++;
			}
			else
			{
				throw new PersistenceLayerException("Illegal deity info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
