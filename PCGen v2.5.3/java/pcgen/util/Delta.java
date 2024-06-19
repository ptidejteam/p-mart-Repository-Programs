/*
 * Delta.java
 * Copyright 2001 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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
 * Created on April 28, 2001, 8:34 AM
 */

package pcgen.util;

/**
 * A helper for <code>java.lang.Integer</code> which understands a
 * leading plus sign for string conversion.
 *
 * @author B. K. Oxley (binkley) <binkley@bigfoot.com>
 * @version $Revision: 1.1 $
 * @see java.lang.Integer
 */
public class Delta
{
	/**
	 * Construct a @see java.lang.Integer and strip a leading plus
	 * sign since <code>Integer</code> does not understand it.
	 */
	public static Integer decode(String s)
	{
		if (s.charAt(0) == '+')
			s = s.substring(1);
		return Integer.decode(s);
	}

	/**
	 * Parse a string with an option plus or minus followed by digits
	 * into an int.
	 * @param s a string that may or may not be a valid delta
	 * @exception java.lang.NumberFormatException
	 * This exception is thrown if the string does not match the
	 * required format for a delta.
	 */
	public static int parseInt(String s)
		throws NumberFormatException
	{
		if (s.charAt(0) == '+')
			s = s.substring(1);
		return Integer.parseInt(s);
	}

	/**
	 * Parse a string with an option plus or minus followed by digits
	 * into an int.
	 * @param s a string that may or may not be a valid delta
	 * @param radix the base of the number system being used
	 * @exception java.lang.NumberFormatException
	 * This exception is thrown if the string does not match the
	 * required format for a delta.
	 */
	public static int parseInt(String s, int radix)
		throws NumberFormatException
	{
		if (s.charAt(0) == '+')
			s = s.substring(1);
		return Integer.parseInt(s, radix);
	}

	public static String toString(int v)
	{
		return toString(new Integer(v));
	}

	public static String toString(Integer v)
	{
		if (v.intValue() >= 0)
			return "+" + v.toString();
		return v.toString();
	}
}
