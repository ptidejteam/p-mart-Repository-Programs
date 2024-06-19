/*
 * LevelLoader.java
 * Copyright 2002 (C) James Dempsey
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
 * Created on August 16, 2002, 10:00 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:07:48 $
 *
 */

package pcgen.persistence.lst;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import pcgen.core.Globals;
import pcgen.core.LevelInfo;

/**
 * <code>LevelLoader</code> loads up the level system file
 * by processing each line passed to it.
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/
final class LevelLoader
{
	private static final String levelTag = "LEVEL:";
	private static final String minXPTag = "MINXP:";
	private static final String maxClassSkillTag = "CSKILLMAX:";
	private static final String maxCrossClassSkillTag = "CCSKILLMAX:";

	/** Creates a new instance of LevelLoader */
	private LevelLoader()
	{
	}

	/**
	 * Parse the line from the level.lst file, populating the
	 * levelInfo object with the info found.
	 *
	 * @param levelInfo  The LevelInfo object to be populated
	 * @param inputLine  The line to be parsed
	 * @param lineNum    The number of the line being parsed.
	 */
	public static void parseLine(LevelInfo levelInfo, String inputLine, int lineNum)
	{
		String aString;

		final StringTokenizer aTok = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
			if (aString.startsWith(levelTag))
			{
				levelInfo.setLevel(Integer.parseInt(aString.substring(levelTag.length())));
			}
			else if (aString.startsWith(maxClassSkillTag))
			{
				levelInfo.setMaxClassSkillRanks(new BigDecimal(aString.substring(maxClassSkillTag.length())));
			}
			else if (aString.startsWith(maxCrossClassSkillTag))
			{
				levelInfo.setMaxCrossClassSkillRanks(new BigDecimal(aString.substring(maxCrossClassSkillTag.length())));
			}
			else if (aString.startsWith(minXPTag))
			{
				levelInfo.setMinXP(Integer.parseInt(aString.substring(minXPTag.length())));
			}
			else
			{
				Globals.errorPrint("LevelLoader got unexpected token of '" + aString + "' at line " + lineNum + ". Token ignored.");
			}
		}
	}
}
