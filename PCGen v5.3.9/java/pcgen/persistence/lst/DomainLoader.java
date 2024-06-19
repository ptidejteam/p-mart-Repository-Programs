/*
 * DomainLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id: DomainLoader.java,v 1.1 2006/02/21 01:16:12 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class DomainLoader
{

	/** Creates a new instance of DomainLoader */
	private DomainLoader()
	{
	}

	public static void parseLine(Domain obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (obj == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		int col = 0;
		if (!obj.isNewItem())
		{
			col = 2; // .MOD skip required fields
			colToken.nextToken();
		}
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}
			final int aLen = colString.length();
			if (col == 0)
			{
				obj.setName(colString);
			}
			//else if (colString.startsWith("DESC:"))
			//{
			//	obj.setGrantedPower(colString.substring(5));
			//}
			else if ((aLen > 6) && colString.startsWith("SKILL"))
			{
				Logging.errorPrint("SKILL: support for " + sourceURL.toString() + " has been removed. Use CSKILL: instead");
			}
			else if ((aLen > 5) && colString.startsWith("FEAT:"))
			{
				obj.setFeatList(colString.substring(5));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.setQualifyString(colString.substring(8));
			}
			else if (col == 1)
			{
				//obj.setGrantedPower(colString);
				obj.setDescription(pcgen.io.EntityEncoder.decode(colString));
			}
			else
			{
				throw new PersistenceLayerException("Illegal obj info " + sourceURL.toString() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			++col;
		}
	}
}
