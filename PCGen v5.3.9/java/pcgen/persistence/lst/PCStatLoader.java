/*
 * PCStatLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on August 12, 2002, 10:29 PM
 *
 * $Id: PCStatLoader.java,v 1.1 2006/02/21 01:16:12 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
final class PCStatLoader
{

	/** Creates a new instance of PCStatLoader */
	private PCStatLoader()
	{
	}

	public static void parseLine(PCStat obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (obj == null)
		{
			return;
		}
	
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		String colString;
		Globals.setAttribRoll(lineNum - 1, true);	// default to rolled
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();

			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}

			//TODO: need to handle Globals.setAttribRoll(new boolean[i])
			if (colString.startsWith("ABB:"))
			{
				obj.setAbb(colString.substring(4));
				Globals.setAttribShort(lineNum - 1, obj.getAbb());
			}
			else if (colString.startsWith("STATMOD:"))
			{
				obj.setStatMod(colString.substring(8));
			}
			else if (colString.startsWith("STATNAME:"))
			{
				obj.setName(colString.substring(9));
				Globals.setAttribLong(lineNum - 1, obj.getName());
			}
			else if (colString.startsWith("STATRANGE:"))
			{
				obj.setStatRange(colString.substring(10));
			}
			else
			{
				throw new PersistenceLayerException("Illegal stat info " + sourceURL.toString() +
					":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
		SystemCollections.addToStatList(obj);
	}
}
