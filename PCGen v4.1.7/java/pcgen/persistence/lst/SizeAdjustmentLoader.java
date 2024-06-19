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
 * $Id: SizeAdjustmentLoader.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SizeAdjustmentLoader
{

	/** Creates a new instance of SizeAdjustmentLoader */
	private SizeAdjustmentLoader()
	{
	}

	public static void parseLine(SizeAdjustment obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		while (colToken.hasMoreTokens())
		{
			String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}
			if (colString.startsWith("SIZENAME:"))
			{
				obj.setName(colString.substring(9));
			}
			else if (colString.startsWith("ABB:"))
			{
				obj.setAbb(colString.substring(4));
			}
			else if (colString.startsWith("ISDEFAULTSIZE:"))
				obj.setIsDefaultSize(colString.endsWith("Y"));
			else
			{
				throw new PersistenceLayerException("Illegal size info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" +
					colString + "\"");
			}
		}
	}
}
