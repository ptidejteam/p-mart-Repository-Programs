/*
 *  Variable.java
 *  Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author Scott Ellsworth
 */

package pcgen.core;

import java.util.StringTokenizer;
import pcgen.util.Logging;

/**
 * A Variable is something that alters a computed variable within pcgen
 * Examples include stats, saves, and base attack bonuses.
 * @author Scott Ellsworth
 * @version $Revision: 1.1 $
 */
public class Variable
{
	private String definition;
	private int level;
	private String name;
	private String upperName;
	private String value;

	public Variable(String inDefinition)
	{
		definition = inDefinition;
		StringTokenizer stringTokenizer = new StringTokenizer(definition, "|", false);

		// If this variable definition only has two tokens, then it is a level independent variable, so add on the -9
		if (stringTokenizer.countTokens() == 2)
		{

			definition = "-9|" + inDefinition;
			stringTokenizer = new StringTokenizer(definition, "|", false);

		}
		else if (stringTokenizer.countTokens() != 3)
		{

			Logging.errorPrint("ERROR: variable " + definition + " does not have two or three pipe-separated values");

		}
		String levelString = stringTokenizer.nextToken();
		level = Integer.parseInt(levelString);
		name = stringTokenizer.nextToken();
		upperName = name.toUpperCase();
		value = stringTokenizer.nextToken();
	}

	public String getDefinition()
	{
		return definition;
	}

	public int getLevel()
	{
		return level;
	}

	public String getUpperName()
	{
		return upperName;
	}

	public String getName()
	{
		return name;
	}
}
