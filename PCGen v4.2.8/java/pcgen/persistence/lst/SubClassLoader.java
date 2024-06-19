/*
 * SubClassLoader.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 19, 2002, 10:29 PM
 *
 * $Id: SubClassLoader.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SubClassLoader
{

	/** Creates a new instance of PCClassLoader */
	private SubClassLoader()
	{
	}

	public static void parseLine(SubClass pcClass, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		if (!inputLine.startsWith("SUBCLASS:"))
		{
			return;
		}
		//int col = 0;
		//int aInt = 0;
		//int option = 15;
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);

		for (; colToken.hasMoreTokens(); /** col++ */)
		{
			final String colString = colToken.nextToken().trim();
			if (!colString.startsWith("SUBCLASS:") && PCClassLoader.parseLine(pcClass, colString, sourceFile, lineNum))
			{
				continue;
			}
			else if (colString.startsWith("CHOICE:"))
			{
				pcClass.setChoice(colString.substring(7));
			}
			else if (colString.startsWith("COST:"))
			{
				pcClass.setCost(Integer.parseInt(colString.substring(5)));
			}
			else if (colString.startsWith("SUBCLASS:"))
			{
				pcClass.setName(colString.substring(9));
			}
			else if (colString.startsWith("SUBCLASSLEVEL:"))
			{
				pcClass.addToLevelArray(colString.substring(14));
			}
			else
			{
				throw new PersistenceLayerException("Illegal class info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
	}

}

