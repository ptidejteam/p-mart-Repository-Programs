/*
 * SpellLoader.java
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
 * $Id: SpellLoader.java,v 1.1 2006/02/20 23:52:37 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Spell;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class SpellLoader
{

	/** Creates a new instance of SpellLoader */
	private SpellLoader()
	{
	}

	public static void parseLine(Spell obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		int i = 0;
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreElements())
		{
			final String colString = colToken.nextToken();
			if (PObjectLoader.parseTag(obj, colString))
				continue;

			final int aLen = colString.length();
			if ((aLen > 11) && colString.startsWith("DESCRIPTOR"))
			{
				obj.addDescriptors(colString.substring(11));
			}
			else if ((aLen > 4) && colString.startsWith("KEY:"))
			{
				obj.setKeyName(colString.substring(4));
			}
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
			{
				obj.addPreReq(colString);
			}
			else if (colString.startsWith("CT:"))
			{
				obj.setCastingThreshold(Integer.parseInt(colString.substring(3)));
			}
			// The tag below is used in Wheel of Time mode to set the level range for
			// a power. Because of the way WoT powers work this is better off in the power
			// than in a classspells.lst
			// arcady - 1/22/2002
			else if (colString.startsWith("LVLRANGE:"))
			{
				final StringTokenizer lvlTok = new StringTokenizer(colString.substring(9), "|", false);
				obj.setMinLVL(Integer.parseInt(lvlTok.nextToken()));
				obj.setMaxLVL(Integer.parseInt(lvlTok.nextToken()));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.addToQualifyListing(colString.substring(8));
			}
			else if (colString.startsWith("STAT:") && (aLen > 5))
			{
				obj.setStat(colString.substring(5));
			}
			else if (colString.startsWith("ITEM:"))
			{
				obj.setCreatableItem(colString.substring(5));
			}
			else if (colString.startsWith("COST:"))
			{
				obj.setCost(colString.substring(5));
			}
			else if (colString.startsWith("XPCOST:"))
			{
				obj.setXpCost(colString.substring(7));
			}
			else if (colString.startsWith("VARIANTS:"))
			{
				obj.setVariants(colString.substring(9));
			}
			else
			{
				i++;
				switch (i)
				{
					case 1:
						obj.setName(colString);
						break;
					case 2:
						obj.setSchool(colString);
						break;
					case 3:
						obj.setSubschool(colString);
						break;
					case 4:
						obj.setComponentList(colString);
						break;
					case 5:
						obj.setCastingTime(colString);
						break;
					case 6:
						obj.setRange(colString);
						break;
					case 7:
						obj.setEffect(colString);
						break;
					case 8:
						obj.setEffectType(colString);
						break;
					case 9:
						obj.setDuration(colString);
						break;
					case 10:
						obj.setSaveInfo(colString);
						break;
					case 11:
						obj.setSR(colString);
						break;
					default:
						throw new PersistenceLayerException("Illegal spell info " +
							sourceFile.getName() + ":" + Integer.toString(lineNum) +
							" \"" + colString + "\"");
				}
			}
		}
		if (i < 11)
		{
			throw new PersistenceLayerException("Expected more fields in " + /*PCGen.currentFile+":"+PCGen.lineNum.toString()+*/ " (" + inputLine + ")");
		}
	}
}
