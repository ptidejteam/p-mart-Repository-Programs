/*
 * Utility.java
 * Copyright 2002 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on Feb 18, 2002, 5:20:42 PM
 *
 * $Id: Utility.java,v 1.1 2006/02/20 23:52:29 vauchers Exp $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * <code>Utility</code>.
 *
 * Assorted generic-ish functionality moved from Globals and PlayerCharacter (the two biggest classes in the project.)
 * Some of this code seems awfully similar, and should probably be further refactored.
 *
 * @author Jonas Karlsson <pjak@yahoo.com>
 * @version $Revision: 1.1 $
 */

public class Utility
{
	/**
	 * Stick a comma between every character of a string
	 */
	public static String commaDelimit(String oldString)
	{
		final int oldStringLength = oldString.length();
		StringBuffer newString = new StringBuffer(oldStringLength);
		for (int i = 0; i < oldStringLength; i++)
		{
			if (i != 0)
				newString.append(",");
			newString.append(oldString.charAt(i));
		}
		return newString.toString();
	}

	public static String commaDelimit(ArrayList stringArray)
	{
		StringBuffer newString = new StringBuffer(70);
		for (Iterator e = stringArray.iterator(); e.hasNext();)
		{
			if (newString.length() != 0)
			{
				newString.append(", ");
			}
			newString.append((String)e.next());
		}
		return newString.toString();
	}

	public static int innerMostStringStart(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == '(')
			{
				current++;
				if (current >= hi)
				{
					hi = current;
					index = i;
				}
			}
			else if (aString.charAt(i) == ')')
				current--;
		}
		return index;
	}

	public static int innerMostStringEnd(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == '(')
			{
				current++;
				if (current > hi)
					hi = current;
			}
			else if (aString.charAt(i) == ')')
			{
				if (current == hi)
					index = i;
				current--;
			}
		}
		return index;
	}

	public static String ordinal(int iValue)
	{
		String suffix = "th";
		if ((iValue < 4) || (iValue > 20))
		{
			switch (iValue % 10)
			{
				case 1:
					suffix = "st";
					break;
				case 2:
					suffix = "nd";
					break;
				case 3:
					suffix = "rd";
					break;
				default:
					break;
			}
		}
		return Integer.toString(iValue) + suffix;
	}

	/**
	 * Create a delimited string for a list.
	 * Awfully similar to unSplit.
	 */
	public static String stringForList(Iterator e, String delim)
	{
		StringBuffer aStrBuf = new StringBuffer(100); //More likely to be true than 16 (the default)
		boolean needDelim = false;
		while (e.hasNext())
		{
			if (needDelim)
			{
				aStrBuf.append(delim);
			}
			else
			{
				needDelim = true;
			}
			aStrBuf.append(e.next().toString());
		}
		return aStrBuf.toString();
	}

	/**
	 *  Turn a 'separator' separated string into a ArrayList of strings, each
	 *  corresponding to one trimmed 'separator'-separated portion of the original
	 *  string.
	 *
	 * @param  aString    The string to be split
	 * @param  separator  The separator that separates the string.
	 * @return            an ArrayList of Strings
	 */
	public static ArrayList split(String aString, char separator)
	{
		int elems = 1;
		int beginIndex = 0;
		int endIndex = 0;

		if (aString.trim().length() == 0)
		{
			return new ArrayList(0);
		}

		for (int i = 0; i < aString.length(); i++)
		{
			if (aString.charAt(i) == separator)
			{
				elems++;
			}
		}
		ArrayList result = new ArrayList(elems);
		for (int i = 0; i < elems; i++)
		{
			endIndex = aString.indexOf(separator, beginIndex);
			if (endIndex == -1)
			{
				endIndex = aString.length();
			}
			result.add(aString.substring(beginIndex, endIndex).trim());

			// Skip separator

			beginIndex = endIndex + 1;
		}
		return result;
	}

	/**
	 *  Reverses the work of split()
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String unSplit(List strings, char separator)
	{
		StringBuffer result = new StringBuffer(strings.size() * 20); //Better than 16, which is default...
		Iterator iter = strings.iterator();
		while (iter.hasNext())
		{
			String element = (String)iter.next();
			result.append(element);
			if (iter.hasNext())
			{
				result.append(separator).append(" ");
			}
		}
		return result.toString();
	}

	/**
	 *  Reverses the work of split()
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 * @deprecated Use {@link #unSplit(List, char)} instead
	 */
	public static String unSplit(ArrayList strings, char separator)
	{
		return unSplit((List)strings, separator);
	}

	public static String replaceString(String in, String find, String newStr)
	{
		final char[] working = in.toCharArray();
		StringBuffer sb = new StringBuffer(in.length() + newStr.length());
		int startindex = in.indexOf(find);
		if (startindex < 0) return in;
		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; i++)
			{
				sb.append(working[i]);
			}
			currindex = startindex;
			sb.append(newStr);
			currindex += find.length();
			startindex = in.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; i++)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	public static int[] resize(int[] array, int add)
	{
		int[] newarray = new int[array.length + add];
		System.arraycopy(array, 0, newarray, 0, array.length);
		return newarray;
	}

	public static int firstNonDigit(String str, int start)
	{
		final int len = str.length();
		while (start < len && Character.isDigit(str.charAt(start)))
		{
			++start;
		}
		return start;
	}

	public static String escapeColons(String in)
	{
		StringBuffer retStr = new StringBuffer(in.length());
		for (int j = 0; j < in.length(); j++)
		{
			final char charAtJ = in.charAt(j);
			if (charAtJ != ':')
			{
				retStr.append(charAtJ);
			}
			else
			{
				retStr.append("\\").append(charAtJ);
			}
		}
		return retStr.toString();
	}

	public static String escapeColons2(String in)
	{
		return replaceString(in, ":", "&#59;");
	}

	public static String unEscapeColons2(String in)
	{
		return replaceString(in, "&#59;", ":");
	}
}
