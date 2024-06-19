/*
 * PCAlignmentLoader.java
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
 * Created on September 24, 2002, 10:29 PM
 *
 * $Id: PCAlignmentLoader.java,v 1.1 2006/02/21 01:07:48 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
final class PCAlignmentLoader
{

	/** Creates a new instance of PCAlignmentLoader */
	private PCAlignmentLoader()
	{
	}

	public static void parseLine(PCAlignment obj, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		String colString;
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
			{
				continue;
			}

			if (colString.startsWith("ALIGNMENTNAME:"))
			{
				obj.setName(colString.substring(14));
			}
			else if (colString.startsWith("ABB:"))
			{ // keyName is used for Abbreviated name
				obj.setKeyName(colString.substring(4));
			}
			else if (colString.startsWith("VALIDFORDEITY:"))
			{
				obj.setValidForDeity(colString.charAt(14) == 'Y');
			}
			else if (colString.startsWith("VALIDFORFOLLOWER:"))
			{
				obj.setValidForFollower(colString.charAt(17) == 'Y');
			}
			else
			{
				throw new PersistenceLayerException("Illegal alignment info " + sourceURL.toString() +
					":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
		}
		final Iterator iter = Globals.getAlignmentList().iterator();
		while (iter.hasNext())
		{
			PCAlignment testObj = (PCAlignment) iter.next();
			if (testObj.getName().equals(obj.getName()) || testObj.getKeyName().equals(obj.getKeyName()))
			{
				return; //we already have this object in our list, so just return
			}
		}
		Globals.getAlignmentList().add(obj);
	}
}
