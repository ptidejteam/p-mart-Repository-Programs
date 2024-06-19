/*
 * Language.java
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
 * Created on November 18, 2001, 9:15 PM
 */

package pcgen.core;

import java.io.File;
import java.util.StringTokenizer;

/**
 * <code>Language</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class Language extends PObject implements Comparable
{
	public String toString()
	{
		return name;
	}

	public Object clone()
	{
		Language aLang = (Language)super.clone();
		return aLang;
	}

	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		StringTokenizer aTok = new StringTokenizer(inputLine, "\t", false);
		String colString = null;
		int col = 0;
		while (aTok.hasMoreTokens())
		{
			colString = aTok.nextToken();
			if (super.parseTag(colString))
				continue;
			if (col == 0)
				name = colString;
			else if (colString.startsWith(Globals.s_TAG_TYPE))
			{
				setType(colString.substring(Globals.s_TAG_TYPE.length()));
			}
			col++;
		}
	}

	/**
	 * Compares keyName only
	 */
	public int compareTo(Object o1)
	{
		return keyName.compareTo(((WeaponProf)o1).keyName);
	}

	/**
	 * Compares keyName only
	 */
	public boolean equals(Object o1)
	{
		return keyName.equals(((WeaponProf)o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 */
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
