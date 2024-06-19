/*
 * RuleCheckLoader.java
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
 * Created on November 22, 2003, 11:59 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:23 $
 *
 */

package pcgen.persistence.lst;

import java.util.StringTokenizer;
import pcgen.core.GameMode;
import pcgen.core.RuleCheck;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 *
 **/
final class RuleCheckLoader
{

	/**
	 * RuleCheckLoader Constructor.
	 **/
	private RuleCheckLoader()
	{
	}

	/**
	 * Parse each line of rules.lst and populate the current gameMode
	 **/
	public static void parseLine(GameMode gameMode, String aLine)
	{
		RuleCheck aRule = new RuleCheck();

		String inputLine = aLine.trim();
		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (colString.startsWith("NAME:"))
			{
				aRule.setName(colString.substring(5));
			}
			else if (colString.startsWith("VAR:"))
			{
				aRule.setVariable(colString.substring(4));
			}
			else if (colString.startsWith("PARM:"))
			{
				aRule.setParameter(colString.substring(5));
			}
			else if (colString.startsWith("DEFAULT:"))
			{
				aRule.setDefault(colString.substring(8));
			}
			else if (colString.startsWith("EXCLUDE:"))
			{
				aRule.setExclude(colString.substring(8));
			}
			else if (colString.startsWith("DESC:"))
			{
				aRule.setDesc(colString.substring(5));
			}
			else
			{
				Logging.errorPrint("Illegal Rule check: " + inputLine);
			}
		}
		// Add the new rule to the current gameMode
		gameMode.addRule(aRule);
	}

}
