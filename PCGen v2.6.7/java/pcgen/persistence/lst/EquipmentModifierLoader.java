/*
 * EquipmentModifierLoader.java
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
 * $Id: EquipmentModifierLoader.java,v 1.1 2006/02/20 23:57:40 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class EquipmentModifierLoader
{

	/** Creates a new instance of EquipmentModifierLoader */
	private EquipmentModifierLoader()
	{
	}

	public static void parseLine(EquipmentModifier obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{

		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = -1;

		if (!obj.isNewItem())
		{
			col = 1; // just force it past required fields since .MOD doesn't specify them
			colToken.nextToken(); // skip name
		}


		while(colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			if (PObjectLoader.parseTag(obj, colString))
				continue;
			col++;

			if (col == 0)
			{
				obj.setName(colString);
			}
			else if (colString.startsWith("CHOOSE:"))
			{
				obj.setChoiceString(colString.substring(7));
			}
			else if (colString.startsWith("ARMORTYPE:"))
			{
				obj.setArmorType(colString.substring(10));
			}
			else if (colString.startsWith("ASSIGNTOALL:"))
			{
				obj.setAssignment(colString.substring(12));
			}
			else if (colString.startsWith("BONUS:"))
			{
				obj.addBonusList(colString.substring(6));
			}
			else if (colString.startsWith("ITYPE:"))
			{
				obj.setItemType(colString.substring(6));
			}
			else if (colString.startsWith(Constants.s_TAG_TYPE))
			{
				obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			}
			else if (colString.startsWith("NAMEOPT:"))
			{
				obj.setNamingOption(colString.substring(8));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				obj.setVisible(colString.substring(8).toUpperCase());
			}
			else if (colString.startsWith("COST:"))
			{
				obj.setCost(colString.substring(5));
			}
			else if (colString.startsWith("COSTPRE:"))
			{
				obj.setPreCost(colString.substring(8));
			}
			else if (colString.startsWith("REPLACES:"))
			{
				obj.setReplacement(colString.substring(9));
			}
			else if (colString.startsWith("IGNORES:"))
			{
				obj.setIgnores(colString.substring(8));
			}
			else if (colString.startsWith("PLUS:"))
			{
				obj.setPlus(colString.substring(5));
			}
			else if (colString.startsWith("SA:"))
			{
				obj.setSpecialAbilties(colString.substring(3));
			}
			else if (colString.startsWith("ADDPROF:"))
			{
				obj.setProficiency(colString.substring(8));
			}
			else if (colString.startsWith("CHARGES:"))
			{
				obj.setChargeInfo(colString.substring(8));
			}
			else
			{
				throw new PersistenceLayerException("Illegal equipment modifier info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}

}
