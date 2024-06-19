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
 * $Id: SpellLoader.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SpellLoader
{

	/** Creates a new instance of SpellLoader */
	private SpellLoader()
	{
	}

	public static void parseLine(Spell obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		int i = 0;
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		if (!obj.isNewItem())
		{
			i = 12; // .MOD skip required fields
			colToken.nextToken();
		}
		while (colToken.hasMoreElements())
		{
			final String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}

			final int aLen = colString.length();
			if (colString.startsWith("CASTTIME:"))
			{
				obj.setCastingTime(colString.substring(9));
			}
			else if (colString.startsWith("CLASSES:"))
			{
				obj.setLevelList("CLASS", colString.substring(8));
			}
			else if (colString.startsWith("COMPS:"))
			{
				obj.setComponentList(colString.substring(6));
			}
			else if (colString.startsWith("COST:"))
			{
				obj.setCost(colString.substring(5));
			}
			//else if (colString.startsWith("DESC:"))
			//{
			//	obj.setDescription(colString.substring(5));
			//}
			else if (colString.startsWith("DOMAINS:"))
			{
				obj.setLevelList("DOMAIN", colString.substring(8));
			}
			else if (colString.startsWith("EFFECTS:"))
			{
				Globals.errorPrint("EFFECTS: tag deprecated - use DESC: instead in " + sourceFile.toString());
				obj.setDescription(colString.substring(8));
			}
			else if (colString.startsWith("EFFECTTYPE:"))
			{
				Globals.errorPrint("EFFECTTYPE: tag deprecated - use TARGETAREA: instead in " + sourceFile.toString());
				obj.setTarget(colString.substring(11));
			}
			else if (colString.startsWith("CT:"))
			{
				obj.setCastingThreshold(Integer.parseInt(colString.substring(3)));
			}
			else if ((aLen > 11) && colString.startsWith("DESCRIPTOR"))
			{
				obj.addDescriptors(colString.substring(11));
			}
			else if (colString.startsWith("DURATION:"))
			{
				obj.setDuration(colString.substring(9));
			}
			else if (colString.startsWith("ITEM:"))
			{
				obj.setCreatableItem(colString.substring(5));
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
				obj.setQualifyString(colString.substring(8));
			}
			else if (colString.startsWith("RANGE:"))
			{
				obj.setRange(colString.substring(6));
			}
			else if (colString.startsWith("SAVEINFO:"))
			{
				obj.setSaveInfo(colString.substring(9));
			}
			else if (colString.startsWith("SCHOOL:"))
			{
				obj.setSchool(colString.substring(7));
			}
			else if (colString.startsWith("SPELLLEVEL:"))
			{
				Globals.errorPrint("Warning: tag 'SPELLLEVEL' has been deprecated. Use CLASSES or DOMAINS tag instead.");
				//obj.setLevelString(colString.substring(11));
				final StringTokenizer slTok = new StringTokenizer(colString.substring(11), "|", false);
				while (slTok.countTokens() >= 3)
				{
					final String typeString = slTok.nextToken();
					final String mainString = slTok.nextToken();
					obj.setLevelInfo(typeString + "|" + mainString, slTok.nextToken());
				}
			}
			else if (colString.startsWith("SPELLRES:"))
			{
				obj.setSpellResistance(colString.substring(9));
			}
			else if (colString.startsWith("SUBSCHOOL:"))
			{
				obj.setSubschool(colString.substring(10));
			}
			else if (colString.startsWith("TARGETAREA:"))
			{
				obj.setTarget(colString.substring(11));
			}
			else if (colString.startsWith("STAT:") && (aLen > 5))
			{
				obj.setStat(colString.substring(5));
			}
			//else if (colString.startsWith(Constants.s_TAG_TYPE))
			//{
			//	obj.addType(colString.substring(5));
			//}
			else if (colString.startsWith("VARIANTS:"))
			{
				obj.setVariants(colString.substring(9));
			}
			else if (colString.startsWith("XPCOST:"))
			{
				obj.setXPCost(colString.substring(7));
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
						obj.setDescription(colString);
						break;
					case 8:
						obj.setTarget(colString);
						break;
					case 9:
						obj.setDuration(colString);
						break;
					case 10:
						obj.setSaveInfo(colString);
						break;
					case 11:
						obj.setSpellResistance(colString);
						break;
					default:
						throw new PersistenceLayerException("Illegal spell info " +
							sourceFile.getName() + ":" + Integer.toString(lineNum) +
							" \"" + colString + "\"");
				}
			}
		}
	}
}
