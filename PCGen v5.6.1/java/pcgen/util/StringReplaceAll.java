/*
 * StringReplaceAll.java
 * Copyright 2003 (C) Bryan McRoberts <merton.monk@codemonkeypublishing.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on January 15th, 2004, 8:34 AM
 */

package pcgen.util;

/**
 *
 * @author <a href="mailto:merton.monk@codemonkeypublishing.com">Bryan McRoberts (merton_monk)</a>
 * @version $Revision: 1.1 $
 */
public final class StringReplaceAll
{
	public static String replaceAll(String baseString, String lookForString, String replaceWithString)
	{
		while (baseString.indexOf(lookForString)>=0)
		{
			int x = baseString.indexOf(lookForString);
			String temp = baseString.substring(0,x)+replaceWithString+baseString.substring(x+lookForString.length());
			baseString = temp;
		}
		return baseString;
	}
}
