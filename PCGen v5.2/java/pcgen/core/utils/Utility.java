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
 * $Id: Utility.java,v 1.1 2006/02/21 01:13:37 vauchers Exp $
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
	 * Stick a comma between every character of a string
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
	 * Simple passthrough
	 * Calls unSplit(stringArray, ',') to do the work
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
		final ArrayList result = new ArrayList(elems);
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
	 * as the delimitor
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
	 * as the delimitor
	 *
	 * Note the actual delimitor is the separator + " "
	 *
	 * @param  strings    An ArrayList of strings
	 * @param  separator  The separating character
	 * @return            A 'separator' separated String
	 */
	public static String join(Collection strings, char separator)
	{
		return join(strings, separator + " ").toString();
	}

	/**
	 * Concatenates the List into a String using the separator
	 * as the delimitor
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

	public static String escapeColons(String in)
	{
		final StringBuffer retStr = new StringBuffer(in.length());
		for (int j = 0; j < in.length(); ++j)
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
		return replaceAll(in, ":", "&#59;");
	}

	public static String unEscapeColons2(String in)
	{
		return replaceAll(in, "&#59;", ":");
	}

	/**
	 * Changes a path to make sure all instances of \ or / are replaced with File.separatorChar
	 *
	 * @param argFileName The path to be fixed
	 * @return
	 */
	public static String fixFilenamePath(final String argFileName)
	{
		return argFileName.replace('/', File.separatorChar).replace('\\', File.separatorChar);
//		final int length = argFileName.length();
//		StringBuffer fileName = new StringBuffer(length);
//		for (int i = 0; i < length; ++i)
//		{
//			final char curChar = argFileName.charAt(i);
//			if (curChar == '\\' || curChar == '/')
//			{
//				fileName.append(File.separatorChar);
//			}
//			else
//			{
//				fileName.append(curChar);
//			}
//		}
//		return fileName.toString();
	}

	public static String fileToURL(final String fileName) throws MalformedURLException
	{
		final File aFile = new File(fileName);
		return aFile.toURL().toString();
	}

	public static String fixURLPath(final String pccPath, final String url) throws MalformedURLException
	{
		StringBuffer retString = new StringBuffer();
		if (url.startsWith("file:"))
		{
			retString.append(pccPath.replace('\\', '/'));
			retString.append(url.substring(5).replace('\\', '/'));
			return new URL("file:/" + retString.toString()).toString();
		}
		else
		{
			return new URL(url.replace('\\', '/')).toString();
		}
	}

	public static boolean isURL(final String aFile)
	{

		return (aFile.startsWith("http:") || aFile.startsWith("ftp:") || aFile.startsWith("file:"));
	}

	public static boolean isNetURL(final String aFile)
	{
		return(aFile.startsWith("http:") || aFile.startsWith("ftp:"));
	}

	/** java doesn't provide an xnor operaton, so we roll our own */

	public static boolean xnor(boolean a, boolean b)
	{
		return (a && b) || (!a && !b);
	}

	/**
	 * Add all iterated items to a collection.
	 *
	 * @param c The <code>Collection</code>
	 * @param it The <code>Iterator</code>
	 */
	public static void collectionAddIterator(Collection c, Iterator it)
	{
		while (it.hasNext())
		{
			c.add(it.next());
		}
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

}
