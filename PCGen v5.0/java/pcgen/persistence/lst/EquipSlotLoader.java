/*
 * EquipSlotLoader.java
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
 * Created on February 24, 2003, 10:29 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:48 $
 *
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.PersistenceLayerException;

/**
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/
final class EquipSlotLoader
{

	/** Creates a new instance of EquipSlotLoader */
	private EquipSlotLoader()
	{
	}

	public static void parseLine(EquipSlot obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer aTok = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		while (aTok.hasMoreTokens())
		{
			String colString = aTok.nextToken().trim();
			if (colString.startsWith("EQSLOT:"))
			{
				obj.setSlotName(colString.substring(7));
			}
			else if (colString.startsWith("CONTAINS:"))
			{
				final StringTokenizer bTok = new StringTokenizer(colString.substring(9), "=", false);
				if (bTok.countTokens() == 2)
				{
					final String aType = bTok.nextToken();
					final String numString = bTok.nextToken();
					int aNum;
					if (numString.equals("*"))
					{
						aNum = 9999;
					}
					else
					{
						aNum = Integer.parseInt(numString);
					}
					obj.setContainType(aType);
					obj.setContainNum(aNum);
				}
			}
			else if (colString.startsWith("NUMBER:"))
			{
				obj.setSlotNumType(colString.substring(7));
			}
			else
			{
				throw new PersistenceLayerException("Illegal size info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}
}
