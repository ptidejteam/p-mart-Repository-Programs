/*
 * RollingMethods.java
 * Copyright 2001 (C) Mario Bonassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id: RollingMethods.java,v 1.1 2006/02/21 01:16:13 vauchers Exp $
 */

package pcgen.core;

import java.util.Arrays;
import pcgen.util.DiceExpression;
import pcgen.util.Logging;
import pcgen.util.ParseException;

/**
 * <code>RollingMethods</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 1.1 $
 */
public final class RollingMethods
{
	/**
	 * Roll <var>times</var> number of dice with <var>sides</var>
	 * shape.
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 *
	 * @return int dice total
	 */
	public static int roll(int times, int sides)
	{
		return roll(times, sides, 0, times, 0);
	}

	/**
	 * Roll die with <var>sides</var> shape rolling above
	 * <var>reroll</var>.
	 *
	 * @param sides int what shape dice?
	 * @param reroll int minimum roll
	 *
	 * @return int dice result
	 */
	private static int rollAbove(int sides, int reroll)
	{
		int roll;

		// Is this optimization valid?	The claim is that
		// rolling a restricted range of the dice (e.g.,
		// dropping 1's and 2's, say, from a 6-sider) is
		// equivalent to rolling a smaller die + bonus (e.g.,
		// d4+2, which yields [3..6] as above).	 --bko
		roll = roll(sides - reroll) + reroll;

		return roll;
	}

	/**
	 * One random number between 1 and <var>sides</var>, good, for
	 * example, for rolling percentage dice.
	 *
	 * @param sides int what shape die?
	 *
	 * @return int die roll
	 */
	public static int roll(int sides)
	{
		return Globals.getRandomInt(sides) + 1;
	}

	private static int roll(int times, int sides, int modifier, int keepCount, int reroll)
	{
		final int[] dieRoll = new int[times];
		for (int i = 0; i < times; ++i)
		{
			dieRoll[i] = rollAbove(sides, reroll);
		}

		if (Logging.isDebugMode())
		{
			final StringBuffer rollString = new StringBuffer(times << 2);
			for (int i = 0; i < times; ++i)
			{
				if (rollString.length() != 0)
				{
					rollString.append(" + ");
				}
				rollString.append(dieRoll[i]);
			}
		}

		if (keepCount > times)
		{
			keepCount = times;
		}

		//
		// Now keep the highest
		//
		int total = 0;
		for (int j = 0; j < keepCount; ++j)
		{
			int highIdx = 0;
			for (int i = 1; i < times; ++i)
			{
				if (dieRoll[i] > dieRoll[highIdx])
				{
					highIdx = i;
				}
			}
			total += dieRoll[highIdx];
			dieRoll[highIdx] = 0; // so we don't use it again
		}

		return total + modifier;
	}

	/**
	 * Roll <var>times</var> dice with <var>sides</var> shape,
	 * sort them, and return the sum of only those listed in
	 * <var>keep</var> (0-indexed).
	 *
	 * @param times int how many dice to roll?
	 * @param sides int what shape dice?
	 * @param keep int[] which dice to keep (0-indexed)?
	 *
	 * @return int dice total
	 */
	public static int roll(int times, int sides, int[] keep)
	{
		final int[] rolls = new int[times];
		while (--times >= 0)
		{
			rolls[times] = Globals.getRandomInt(sides);
		}
		java.util.Arrays.sort(rolls);

		int total = keep.length; // keep the +1 at the end
		for (int i = 0; i < keep.length; ++i)
		{
			total += rolls[keep[i]]; // 0-indexed
		}

		return total;
	}

	/**
	 * Takes many forms including "2d6-2" and returns the result.
	 * Whitespace is ignored; case insensitive.  Most simple math
	 * operations (including exponentiation) is supported.
	 * Functions builtin include max, min, roll.  Add new
	 * functions to DiceExpressionFunctions.
	 *
	 * @see pcgen.util.DiceExpression
	 *
	 * @param method String formatted string representing dice roll
	 *
	 * @return int dice total
	 */
	static int roll(String method)
	{
		final DiceExpression parser = new DiceExpression(method);
		int r = 0;

		try
		{
			r = parser.rollDice();
			if (r == DiceExpression.BAD_VALUE)
			{
				r = 0;
			}
		}
		catch (ParseException ex)
		{
			Logging.errorPrint("Bad dice: " + method + ": " + ex);
		}

		return r;
	}

	/**
	 * Roll <var>times</var> bizarre dice.
	 *
	 * @param times int how many dice to roll?
	 * @param shape int[] array of values of sides of die
	 *
	 * @return what the die says
	 */
	public static int roll(int times, int[] shape)
	{
		int total = 0;
		while (--times >= 0)
		{
			total += shape[Globals.getRandomInt(shape.length)];
		}

		return total;
	}

	/**
	 * Roll <var>times</var> bizarre dice, keeping
	 * <var>keep</keep> of them in ascending order.
	 *
	 * @param times int how many dice to roll?
	 * @param shape int[] array of values of sides of die
	 * @param keep int[] which dice to keep
	 *
	 * @return what the die says
	 */
	public static int roll(int times, int[] shape, int[] keep)
	{
		final int[] rolls = new int[times];
		while (--times >= 0)
		{
			rolls[times] = shape[Globals.getRandomInt(shape.length)];
		}
		Arrays.sort(rolls);

		int total = 0;
		for (int i = 0; i < keep.length; ++i)
		{
			total += rolls[keep[i]]; // 0-indexed
		}

		return total;
	}
}
