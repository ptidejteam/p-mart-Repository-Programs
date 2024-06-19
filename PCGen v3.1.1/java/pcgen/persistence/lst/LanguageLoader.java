/*
 * LanguageLoader.java
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
 * $Id: LanguageLoader.java,v 1.1 2006/02/21 00:05:41 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Language;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
class LanguageLoader
{

	/** Creates a new instance of LanguageLoader */
	private LanguageLoader()
	{
	}

	public static void parseLine(Language obj, String inputLine, File sourceFile, int lineNum)
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);

		int col = 0;
		final int beginIndex = Constants.s_TAG_TYPE.length();
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken();
			if (PObjectLoader.parseTag(obj, colString))
				continue;
			if (col == 0)
				obj.setName(colString);
			else if (colString.startsWith(Constants.s_TAG_TYPE))
			{
				obj.setType(colString.substring(beginIndex));
			}
			col++;
		}
	}

}
