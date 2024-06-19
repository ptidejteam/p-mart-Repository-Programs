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
 * $Id: FeatLoader.java,v 1.1 2006/02/20 23:52:37 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Feat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Delta;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class FeatLoader
{

	/** Creates a new instance of FeatLoader */
	private FeatLoader()
	{
	}

	public static void parseLine(Feat feat, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		String colString = null;
		for (col = 0; col < colMax; col++)
		{
			colString = colToken.nextToken();
			if (PObjectLoader.parseTag(feat, colString))
				continue;
			final int len = colString.length();
			if (col == 0)
			{
				feat.setName(colString);
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
			else if (colString.startsWith("ADD:"))
			{
				feat.setAddString(colString.substring(4));
			}
			else if (colString.startsWith("BONUS"))
			{
				feat.addBonusList(colString.substring(6));
			}
			else if (colString.startsWith("DESC"))
			{
				feat.setDescription(colString.substring(5));
				//Is this like PRESKILl
			}
			else if (colString.startsWith("SKILL:"))
			{
				feat.setSkillNameList(colString.substring(6));
			}
			else if (colString.startsWith(Constants.s_TAG_TYPE))
			{
				feat.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			}
			else if (colString.startsWith("MULT"))
			{
				feat.setMultiples(colString.substring(5));
			}
			else if (colString.startsWith("STACK"))
			{
				feat.setStacks(colString.substring(6));
			}
			else if (colString.startsWith("CHOOSE"))
			{
				feat.setChoiceString(colString.substring(7));
			}
			else if (colString.startsWith("CSKILL"))
			{
				feat.setCSkillList(colString.substring(7));
			}
			else if (colString.startsWith("CCSKILL"))
			{
				feat.setCCSkillList(colString.substring(8));
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
			else if (colString.startsWith("DEFINE"))
			{
				feat.addVariableList("0|" + colString.substring(7));
			}
			else if (colString.startsWith("KEY:"))
			{
				feat.setKeyName(colString.substring(4));
			}
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
			{
				feat.addPreReq(colString);
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				feat.addToQualifyListing(colString.substring(8));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				if (colString.substring(8).startsWith("Export"))
				{
					feat.setVisible(Feat.VISIBILITY_OUTPUT_ONLY);
				}
				else if (colString.substring(8).startsWith("No"))
				{
					feat.setVisible(Feat.VISIBILITY_HIDDEN);
				}
				else if (colString.substring(8).startsWith("Display"))
				{
					feat.setVisible(Feat.VISIBILITY_DISPLAY_ONLY);
				}
				else
				{
					feat.setVisible(Feat.VISIBILITY_DEFAULT);
				}
			}
			else if (colString.startsWith("COST"))
			{
				feat.setCost(Double.parseDouble(colString.substring(5)));
			}
			else
			{
				throw new PersistenceLayerException(sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
