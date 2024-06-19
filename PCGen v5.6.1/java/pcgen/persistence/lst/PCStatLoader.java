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
 * $Id: PCStatLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.PCStat;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
final class PCStatLoader extends LstLineFileLoader
{

	/** Creates a new instance of PCStatLoader */
	public PCStatLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		PCStat stat = new PCStat();

		final StringTokenizer colToken =
			new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		String colString;
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();

			if (colString.startsWith("ABB:"))
			{
				stat.setAbb(colString.substring(4));
			}
			else if (colString.startsWith("STATMOD:"))
			{
				stat.setStatMod(colString.substring(8));
			}
			else if (colString.startsWith("STATNAME:"))
			{
				stat.setName(colString.substring(9));
			}
			else if (colString.startsWith("STATRANGE:"))
			{
				stat.setStatRange(colString.substring(10));
			}
			else if (PObjectLoader.parseTag(stat, colString))
			{
				continue;
			}
			else if (colString.startsWith("PENALTYVAR:"))
			{
				stat.setPenaltyVar(colString.substring(11));
			}
			else
			{
				Logging.errorPrint(
					"Illegal stat info '"
						+ lstLine
						+ "' in "
						+ sourceURL.toString());
			}
		}

		SystemCollections.addToStatList(stat);
	}
}
