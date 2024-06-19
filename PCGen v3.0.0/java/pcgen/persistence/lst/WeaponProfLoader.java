/*
 * WeaponProfLoader.java
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
 * $Id: WeaponProfLoader.java,v 1.1 2006/02/21 00:02:33 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class WeaponProfLoader
{

	/** Creates a new instance of WeaponProfLoader */
	private WeaponProfLoader()
	{
	}

	/**
	 * Parses the weapon prof line in the lst file.
	 */
	public static void parseLine(WeaponProf obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = 0;
		if (!obj.isNewItem())
		{
			col = 1; // .MOD skips required fields (name in this case)
			colToken.nextToken(); // skip name
		}
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
				continue;
			if (col == 0)
			{
				obj.setName(colString);
			}
			else if (colString.startsWith(Constants.s_TAG_TYPE))
			{
				obj.setWPType(colString.substring(Constants.s_TAG_TYPE.length()));
			}
			else if (colString.startsWith("SIZE"))
			{
				obj.setSize(colString.substring(5));
			}
			else if (colString.startsWith("HANDS"))
			{
				obj.setHands(colString.substring(6));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.setQualifyString(colString.substring(8));
			}
			else
			{
				throw new PersistenceLayerException("Illegal weapon proficiency info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			col++;
		}

	}

}
