// Emacs, this is -*- java -*- code.

/*
 * DiceExpressionFunctions.java
 * Copyright 2002 (C) B. K. Oxley (binkley) <binkley@bigfoot.com>
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
 * Created on January 28, 2002.
 *
 * $Id: DiceExpressionFunctions.java,v 1.1 2006/02/20 23:52:35 vauchers Exp $
 */

package pcgen.util;

import pcgen.core.RollingMethods;

/**
 * Functions for <code>DiceExpression</code>.  This is separated to
 * make it easier to add new function without needing to touch the
 * parser.
 *
 * All functions need to be <code>public static</code> and take
 * <code>int</code> or <code>int[]</code> arguments and return
 * <code>int</code>.
 *
 * @version 1.0
 * @author B. K. Oxley (binkley) <a href="mailto:binkley@bigfoot.com">binkley@bigfoot.com</a>
 */
public class DiceExpressionFunctions
{
	/**
	 * An int version of <code>Math.pow</code>.
	 *
	 * @param base int base
	 * @param exp int exponent
	 *
	 * @return int base ** exponent
	 */
	public static int pow(int base, int exp)
	{
		if (exp < 0) throw new IllegalArgumentException();

		int result = 1;

		while (exp-- > 0) result *= base;

		return result;
	}

	/**
	 * An int version of <code>Math.max</code>.
	 *
	 * @param a int left value
	 * @param b int right value
	 *
	 * @return greater of a and b, a if equal
	 */
	public static int max(int a, int b)
	{
		return b > a ? b : a;
	}

	/**
	 * An int version of <code>Math.max</code>.
	 *
	 * @param a int left value
	 * @param b int right value
	 *
	 * @return lesser of a and b, a if equal
	 */
	public static int min(int a, int b)
	{
		return b < a ? b : a;
	}

	/**
	 * Roll <var>times</var> <var>sides</var>-sided polyhedral dice,
	 * summing the results.
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 *
	 * @return sum of the dice
	 */
	public static int roll(int times, int sides)
	{
		if (times < 0 || sides < 1)
			throw new IllegalArgumentException();

		return RollingMethods.roll(times, sides);
	}

	/**
	 * Roll <var>times</var> <var>sides</var>-sided polyhedral dice,
	 * sort the dice in ascending order, keep only those listed.
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 * @param keep int[] keep which dice, 1-indexed?
	 *
	 * @return sum of the dice
	 */
	public static int roll(int times, int sides, int[] keep)
	{
		if (times < 0 || sides < 1 || keep == null)
			throw new IllegalArgumentException();

		// Convert from 1-indexed to 0-indexed. Don't change
		// the argument list -- Java lacks the C++ const
		// specifier.
		int[] keep0 = new int[keep.length];

		for (int i = 0; i < keep.length; ++i)
			keep0[i] = keep[i] - 1;

		return RollingMethods.roll(times, sides, keep0);
	}

	/**
	 * Roll <var>times</var> <var>shape</var> bizarre dice.
	 * <var>shape</var> lists the value of each side of the die;
	 * each side is equally likely to occur just as with regular
	 * dice, but are not necessary in regular, increasing order.
	 *
	 * @param times int how many dice to roll?
	 * @param shape int[] what shape dice?
	 *
	 * @return sum of the dice
	 */
	public static int roll(int times, int[] shape)
	{
		return RollingMethods.roll(times, shape);
	}

	/**
	 * Roll <var>times</var> <var>shape</var> bizarre dice, sort
	 * the dice in ascending order, keep only those listed.
	 * <var>shape</var> lists the value of each side of the die;
	 * each side is equally likely to occur just as with regular
	 * dice, but are not necessary in regular, increasing order.
	 *
	 * @param times int how many dice to roll?
	 * @param shape int[] what shape dice?
	 * @param keep int[] keep which dice, 1-indexed?
	 *
	 * @return sum of the dice
	 */
	public static int roll(int times, int[] shape, int[] keep)
	{
		// Convert from 1-indexed to 0-indexed. Don't change
		// the argument list -- Java lacks the C++ const
		// specifier.
		int[] keep0 = new int[keep.length];

		for (int i = 0; i < keep.length; ++i)
			keep0[i] = keep[i] - 1;

		return RollingMethods.roll(times, shape, keep0);
	}
}
