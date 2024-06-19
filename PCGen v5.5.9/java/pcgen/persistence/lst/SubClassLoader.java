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
 * $Id: SubClassLoader.java,v 1.1 2006/02/21 01:28:23 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;

import pcgen.core.PObject;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;

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

	/**
	 * This method is static so it can be used by PCClassLoader.
	 * @see pcgen.persistence.lst.LstObjectFileLoader#parseLine(pcgen.core.PObject, java.lang.String, pcgen.persistence.lst.CampaignSourceEntry)
	 */
	public static PObject parseLine(
		PObject target,
		String lstLine,
		CampaignSourceEntry source)
		throws PersistenceLayerException
	{
		SubClass pcClass = (SubClass) target;
		if (pcClass == null)
		{
			return pcClass;
		}
		if (!lstLine.startsWith("SUBCLASS:"))
		{
			return pcClass;
		}
		//int col = 0;
		//int aInt = 0;
		//int option = 15;
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (colString.startsWith("CHOICE:"))
			{
				pcClass.setChoice(colString.substring(7));
			}
			else if (colString.startsWith("COST:"))
			{
				pcClass.setCost(Integer.parseInt(colString.substring(5)));
			}
			else if (colString.startsWith("PROHIBITCOST:"))
			{
				pcClass.setProhibitCost(Integer.parseInt(colString.substring(13)));
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
				classLoader.parseLine(pcClass, colString, source);
			}
		}
		return pcClass;
	}

	private static PCClassLoader classLoader = new PCClassLoader();
}

