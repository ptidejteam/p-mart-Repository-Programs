/*
 * PCCheckLoader.java
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
 * $Id: PCCheckLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.core.PObject;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
final class PCCheckLoader extends LstLineFileLoader
{

	/** Creates a new instance of PCCheckLoader */
	public PCCheckLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		PObject obj = new PObject();

		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		String colString;
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}

			if (colString.startsWith("CHECKNAME:"))
			{
				obj.setName(colString.substring(10));
				addIfNew(obj);
			}
			else
			{
				Logging.errorPrint(
					"Illegal check info '"
						+ lstLine
						+ "' in "
						+ sourceURL.toString());
			}
		}
	}

	private static void addIfNew(PObject obj)
	{
		final Iterator iter = SystemCollections.getUnmodifiableCheckList().iterator();
		while (iter.hasNext())
		{
			final PObject testObj = (PObject) iter.next();
			if (testObj.getName().equals(obj.getName()))
			{
				return; //we already have this object in our list, so just return
			}
		}
		SystemCollections.addToCheckList(obj);
	}

}
