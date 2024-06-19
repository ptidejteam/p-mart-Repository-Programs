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
 * Last Edited: $Date: 2006/02/21 01:33:26 $
 *
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.SystemCollections;
import pcgen.core.character.EquipSlot;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/
final class EquipSlotLoader extends LstLineFileLoader
{
	/** Creates a new instance of EquipSlotLoader */
	public EquipSlotLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		String inputLine = lstLine.trim();
		final EquipSlot eqSlot = new EquipSlot();

		final StringTokenizer aTok = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		while (aTok.hasMoreTokens())
		{
			final String colString = aTok.nextToken().trim();
			if (lstLine.startsWith("NUMSLOTS:"))
			{
				final StringTokenizer eTok = new StringTokenizer(lstLine.substring(9), SystemLoader.TAB_DELIM);
				while (eTok.hasMoreTokens())
				{
					// parse the default number of each type
					final String cString = eTok.nextToken().trim();
					final StringTokenizer cTok = new StringTokenizer(cString, ":");
					if (cTok.countTokens() == 2)
					{
						final String eqSlotType = cTok.nextToken();
						final String aNum = cTok.nextToken();
						Globals.setEquipSlotTypeCount(eqSlotType, aNum);
					}
				}
			}
			else if (colString.startsWith("EQSLOT:"))
			{
				eqSlot.setSlotName(colString.substring(7));
			}
			else if (colString.startsWith("CONTAINS:"))
			{
				final StringTokenizer bTok = new StringTokenizer(colString.substring(9), "=");
				if (bTok.countTokens() == 2)
				{
					final String aType = bTok.nextToken();
					final String numString = bTok.nextToken();
					final int aNum;
					if (numString.equals("*"))
					{
						aNum = 9999;
					}
					else
					{
						aNum = Integer.parseInt(numString);
					}
					eqSlot.setContainType(aType);
					eqSlot.setContainNum(aNum);
				}
			}
			else if (colString.startsWith("NUMBER:"))
			{
				eqSlot.setSlotNumType(colString.substring(7));
			}
			else
			{
				Logging.errorPrint("Illegal slot info '" + lstLine + "' in " + sourceURL.toString());
			}
		}

		SystemCollections.addToEquipSlotsList(eqSlot);
	}

	/**
	 * @see pcgen.persistence.lst.LstFileLoader#loadLstFile(String)
	 */
	public void loadLstFile(String source) throws PersistenceLayerException
	{
		SystemCollections.clearEquipSlotsList();
		super.loadLstFile(source);
	}

}
