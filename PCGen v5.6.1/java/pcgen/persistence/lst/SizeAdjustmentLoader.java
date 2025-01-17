/*
 * SizeAdjustmentLoader.java
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
 * $Id: SizeAdjustmentLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.SizeAdjustment;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SizeAdjustmentLoader extends LstLineFileLoader
{

	/** Prevent creation of a new instance of SizeAdjustmentLoader */
	public SizeAdjustmentLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		SizeAdjustment sa = new SizeAdjustment();

		String inputLine = lstLine.trim();
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (colString.startsWith("SIZENAME:"))
			{
				String name = colString.substring(9);
				SizeAdjustment sa2 = SystemCollections.getSizeAdjustmentNamed(name);
				if (sa2 == null)
				{
					sa = new SizeAdjustment();
					sa.setName(name);
					SystemCollections.addToSizeAdjustmentList(sa);
				}
				else
				{
					// continuing to load previously existing SA
					sa = sa2;
				}
			}
			else if (PObjectLoader.parseTag(sa, colString))
			{
				continue;
			}
			else if (colString.startsWith("ABB:"))
			{
				sa.setAbbreviation(colString.substring(4));
			}
			else if (colString.startsWith("ISDEFAULTSIZE:"))
			{
				sa.setIsDefaultSize(colString.endsWith("Y"));
			}
			else
			{
				Logging.errorPrint("Illegal size info '"
					+ lstLine + "' in " + sourceURL.toString());
			}
		}
	}

	/**
	 * @see pcgen.persistence.lst.LstFileLoader#loadLstFile(String)
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		SystemCollections.clearSizeAdjustmentList();
		super.loadLstFile(fileName);
	}

}
