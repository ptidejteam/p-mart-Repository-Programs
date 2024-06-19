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
 * $Id: Utility.java,v 1.1 2006/02/21 01:28:27 vauchers Exp $
 */
package pcgen.core.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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

public final class Utility
{
	private Utility()
	{
		super();
	}

	/**
	 * Stick a comma between every character of a string.
	 * @param oldString
	 * @return
	 */
	public static String commaDelimit(String oldString)
	{
		final int oldStringLength = oldString.length();
		final StringBuffer newString = new StringBuffer(oldStringLength);
		for (int i = 0; i < oldStringLength; ++i)
		{
			if (i != 0)
			{
				newString.append(',');
			}
			newString.append(oldString.charAt(i));
		}
		return newString.toString();
	}

	/**
	 * Simple passthrough, calls join(stringArray, ',') to do the work.
	 * @param stringArray
	 * @return
	 */
	public static String commaDelimit(Collection stringArray)
	{
		return join(stringArray, ',');
	}

	public static int innerMostStringStart(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == '(')
			{
				++current;
				if (current >= hi)
				{
					hi = current;
					index = i;
				}
			}
			else if (aString.charAt(i) == ')')
			{
				--current;
			}
		}
		return index;
	}

	public static int innerMostStringEnd(String aString)
	{
		int index = 0;
		int hi = 0;
		int current = 0;
		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == '(')
			{
				++current;
				if (current > hi)
				{
					hi = current;
				}
			}
			else if (aString.charAt(i) == ')')
			{
				if (current == hi)
				{
					index = i;
				}
				--current;
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
	 *  Turn a 'separator' separated string into a ArrayList of strings, each
	 *  corresponding to one trimmed 'separator'-separated portion of the original
	 *  string.
	 *
	 * @param  aString    The string to be split
	 * @param  separator  The separator that separates the string.
	 * @return            a List of Strings
	 */
	public static List split(String aString, char separator)
	{
		int elems = 1;
		int beginIndex = 0;
		int endIndex;

		if (aString.trim().length() == 0)
		{
			return new ArrayList(0);
		}

		for (int i = 0; i < aString.length(); ++i)
		{
			if (aString.charAt(i) == separator)
			{
				++elems;
			}
		}
		final List result = new ArrayList(elems);
		for (int i = 0; i < elems; ++i)
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
	 * Concatenates the List into a StringBuffer using the separator
	 * as the delimitor.
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static StringBuffer joinToStringBuffer(Collection strings, String separator)
	{
		final StringBuffer result = new StringBuffer(strings.size() * 10);
		final Iterator iter = strings.iterator();
		while (iter.hasNext())
		{
			String element = (String) iter.next();
			result.append(element);
			if (iter.hasNext())
			{
				result.append(separator);
			}
		}
		return result;
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor.
	 *
	 * Note the actual delimitor is the separator + " "
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String join(Collection strings, char separator)
	{
		return join(strings, separator + " ");
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor.
	 *
	 * Note the actual delimitor is the separator + " "
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String join(Collection strings, String separator)
	{
		return joinToStringBuffer(strings, separator).toString();
	}

	public static String replaceAll(String in, String find, String newStr)
	{
		final char[] working = in.toCharArray();
		final StringBuffer sb = new StringBuffer(in.length() + newStr.length());
		int startindex = in.indexOf(find);
		if (startindex < 0)
		{
			return in;
		}
		int currindex = 0;

		while (startindex > -1)
		{
			for (int i = currindex; i < startindex; ++i)
			{
				sb.append(working[i]);
			}
			currindex = startindex;
			sb.append(newStr);
			currindex += find.length();
			startindex = in.indexOf(find, currindex);
		}

		for (int i = currindex; i < working.length; ++i)
		{
			sb.append(working[i]);
		}

		return sb.toString();
	}

	public static String unEscapeColons2(String in)
	{
		return replaceAll(in, "&#59;", ":");
	}

	/**
	 * Changes a path to make sure all instances of \ or / are replaced with File.separatorChar.
	 *
	 * @param argFileName The path to be fixed
	 * @return
	 */
	public static String fixFilenamePath(final String argFileName)
	{
		return argFileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
	}

	public static String fileToURL(final String fileName) throws MalformedURLException
	{
		final File aFile = new File(fileName);
		return aFile.toURL().toString();
	}

	public static String fixURL(final String url) throws MalformedURLException
	{
			return new URL(url.replace('\\', '/')).toString();
	}

	public static String fixURLPath(final String pccPath, final String url) throws MalformedURLException
	{
		StringBuffer path = new StringBuffer(url.length());
		String result;
		if (url.startsWith("file:"))
		{
			path.append(pccPath.replace('\\', '/'));
			path.append(url.substring(5).replace('\\', '/'));
			result = new URL("file:" + path.toString()).toString();
		}
		else
		{
			result = new URL(url.replace('\\', '/')).toString();
		}
		return result;
	}

	public static boolean isURL(final String aFile)
	{

		return (aFile.startsWith("http:") || aFile.startsWith("ftp:") || aFile.startsWith("file:"));
	}

	/**
	 * Verifies that a string is all numeric (integer).
	 * @param numString String to check if all numeric [integer]
	 * @return true if the String is numeric, else false
	 */
	public static boolean isIntegerString(String numString)
	{
		boolean result;
		try
		{
			Integer.parseInt(numString);
			result = true;
		}
		catch (NumberFormatException nfe)
		{
			result = false;
		}
		return result;
	}

	public static boolean isNetURL(final String aFile)
	{
		return (aFile.startsWith("http:") || aFile.startsWith("ftp:"));
	}

	/**
	 * Capitalize the first letter of every word in a string
	 * @param aString
	 * @return
	 */
	public static String capitalizeFirstLetter(String aString)
	{
		boolean toUpper = true;
		char[] a = aString.toLowerCase().toCharArray();
		for (int i = 0; i < a.length; ++i)
		{
			if (Character.isWhitespace(a[i]))
			{
				toUpper = true;
			}
			else
			{
				if (toUpper && Character.isLowerCase(a[i]))
				{
					a[i] = Character.toUpperCase(a[i]);
				}
				toUpper = false;
			}
		}
		return new String(a);
	}

	/**
	 * Returns true if the checklist contains any row from targets.
	 * @param checklist The collection to check
	 * @param targets The collection to find in the checklist
	 * @return
	 */
	public static boolean containsAny(Collection checklist, Collection targets)
	{
		for (Iterator i = targets.iterator(); i.hasNext();)
		{
			if (checklist.contains(i.next()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Compare two doubles within a given epsilon.
	 * @param a
	 * @param b
	 * @param eps
	 * @return
	 */
	public static boolean compareDouble(double a, double b, double eps)
	{
		// If the difference is less than epsilon, treat as equal.
		return Math.abs(a - b) < eps;
	}

	/**
	 * Compare two doubles within a given epsilon, using a default epsilon of 0.0001.
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean doublesEqual(double a, double b)
	{
		// If the difference is less than epsilon, treat as equal.
		return compareDouble(a, b, 0.0001);
	}

	/**
	 * Replace this with String.replaceFirst once we switch to jdk 1.4
	 * @param original
	 * @param word
	 * @param replacement
	 * @return
	 */
	public static String replaceFirst(String original, String word, String replacement)
	{
		int start = original.indexOf(word);
		StringBuffer sb = new StringBuffer(50);
		sb.append(original.substring(0, start));
		sb.append(replacement);
		sb.append(original.substring(start + word.length()));

		return sb.toString();
	}

}
