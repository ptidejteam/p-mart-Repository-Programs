/*
 * FeatLoader.java
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
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:10:59 $
 *
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Feat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Delta;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class FeatLoader
{

	/** Creates a new instance of FeatLoader */
	private FeatLoader()
	{
	}

	public static void parseLine(Feat feat, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM, false);
		int col = 0;
		String colString;
		if (!feat.isNewItem())
		{
			col = 1; // .MOD skips required fields (name in this case)
			colToken.nextToken();
		}
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();
			final int len = colString.length();
			if (col == 0)
			{
				feat.setName(colString);
			}
			//
			// moved this after name assignment so feats named
			// PRExxx don't parse the name as a prerequisite
			//
			else if (PObjectLoader.parseTag(feat, colString))
			{
				continue;
			}
			else if (colString.startsWith("ADD:"))
			{
				feat.setAddString(colString.substring(4));
			}
			else if ((len > 14) && colString.startsWith("ADDSPELLLEVEL:"))
			{
				try
				{
					feat.setAddSpellLevel(Delta.parseInt(colString.substring(14)));
				}
				catch (NumberFormatException nfe)
				{
					throw new PersistenceLayerException("Bad addSpellLevel " + colString);
				}
			}
			//else if (colString.startsWith("BONUS"))
			//{
			//	feat.addBonusList(colString.substring(6));
			//}
			//else if (colString.startsWith("CHOOSE"))
			//{
			//	feat.setChoiceString(colString.substring(7));
			//}
			//else if (colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	feat.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			//}
			else if (colString.startsWith("COST"))
			{
				feat.setCost(Double.parseDouble(colString.substring(5)));
			}
			//else if (colString.startsWith("DESC"))
			//{
			//	feat.setDescription(colString.substring(5));
			//	//Is this like PRESKILl
			//}
			else if (colString.startsWith("MULT"))
			{
				feat.setMultiples(colString.substring(5));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				feat.setQualifyString(colString.substring(8));
			}
			else if (colString.startsWith("REP"))
			{
				try
				{
					feat.setLevelsPerRepIncrease(Delta.decode(colString.substring(4)));
				}
				catch (NumberFormatException nfe)
				{
					throw new PersistenceLayerException("Bad level per value " + colString);
				}
			}
			else if (colString.startsWith("STACK"))
			{
				feat.setStacks(colString.substring(6));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				final String visType = colString.substring(8).toUpperCase();
				if (visType.startsWith("EXPORT"))
				{
					feat.setVisible(Feat.VISIBILITY_OUTPUT_ONLY);
				}
				else if (visType.startsWith("NO"))
				{
					feat.setVisible(Feat.VISIBILITY_HIDDEN);
				}
				else if (visType.startsWith("DISPLAY"))
				{
					feat.setVisible(Feat.VISIBILITY_DISPLAY_ONLY);
				}
				else
				{
					feat.setVisible(Feat.VISIBILITY_DEFAULT);
				}
			}
			//else if (colString.startsWith("WPROFTYPE:"))
			//{
			//	feat.setWeaponProfType(colString.substring(12));
			//}
			else
			{
				throw new PersistenceLayerException(sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			++col;
		}
	}
}
