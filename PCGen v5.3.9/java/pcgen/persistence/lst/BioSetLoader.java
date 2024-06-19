/*
 * bioSetLoader.java
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
 * Created on October 10, 2002, 10:29 PM
 *
 * $Id: BioSetLoader.java,v 1.1 2006/02/21 01:16:12 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import pcgen.core.BioSet;
import pcgen.core.Constants;
import pcgen.persistence.SystemLoader;

/**
 *
 * @author  Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
final class BioSetLoader
{

	private static String regionName = Constants.s_NONE;

	/** Creates a new instance of bioSetLoader */
	private BioSetLoader()
	{
	}

	public static void clear()
	{
		regionName = Constants.s_NONE;
	}

	public static void parseLine(BioSet bioSet, int fileType, String inputLine)
	{
		if (bioSet == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		String colString;
		String raceName = "";
		if (inputLine.startsWith("AGESET:"))
		{
			bioSet.addToAgeMap(regionName, inputLine.substring(7));
			return;
		}
		List preReqList = null;
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken();
			if (colString.startsWith("RACENAME:"))
			{
				raceName = colString.substring(9);
			}
			else if (colString.startsWith("REGION:"))
			{
				regionName = colString.substring(7);
			}
			else if (colString.startsWith("PRE") || colString.startsWith("!PRE"))
			{
				if (preReqList == null)
				{
					preReqList = new ArrayList();
				}
				preReqList.add(colString);
			}
			else if (fileType == LstConstants.BIO_TYPE)
			{
				bioSet.addToRaceMap(regionName, raceName, colString);
			}
			else
			{
				String aString = "";
				if (preReqList != null)
				{
					final StringBuffer sBuf = new StringBuffer(100);
					for (int i = 0, x = preReqList.size(); i < x; ++i)
					{
						sBuf.append('[').append(preReqList.get(i)).append(']');
					}
					aString = sBuf.toString();
				}
				bioSet.addToUserMap(regionName, raceName, colString + aString);
			}
		}
	}
}
