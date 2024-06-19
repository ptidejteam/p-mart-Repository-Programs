/*
 * RollingMethods.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.core;


/**
 * <code>RollingMethods</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class RollingMethods
{

	/*
	 * this is just one random number between 1 and sides, good for %
	 */
	public static int roll(int sides)
	{
		return Globals.getRandomInt(sides) + 1;
	}


	/*
	 * this is the basic 2d6 function
	 */
	public static int roll(int times, int sides)
	{
		int total = 0;
		for (int rolls = 0; rolls < times; rolls++)
		{
			total += Globals.getRandomInt(sides) + 1;
			//System.out.println("each " +total);
		}
		return total;
	}

	private static int rollAbove(int sides, int reroll)
	{
		int roll = 0;

		do {
			roll = Globals.getRandomInt(sides) + 1;
		} while (roll <= reroll);

		return roll;
	}

	// Return only certain elements of a roll.
	public static int roll(int times, int sides, int[] keep,
		int reroll)
	{
		int[] rolls = new int[times];

		for (int i = 0; i < times; ++i) {
			rolls[i] = rollAbove (sides, reroll);
		}

		java.util.Arrays.sort (rolls);

		int total = 0;

		for (int i = 0; i < keep.length; ++i) {
			total += rolls[keep[i]];
		}

		return total;
	}

	//
	// roll <times>d<sides>, keeping the highest <keepCount>, adding <modifier> to total, reroll
	// any rolls less that or equal to <reroll>
	//
	public static int roll(int times, int sides, int modifier, int keepCount, int reroll)
	{
		int[] dieRoll = new int[times];
		for (int i = 0; i < times; i++) {
			dieRoll[i] = rollAbove (sides, reroll);
		}

		if (Globals.isDebugMode())
		{
			StringBuffer rollString = new StringBuffer(times * 4);
			for (int i = 0; i < times; i++)
			{
				if (rollString.length() != 0)
				{
					rollString.append(" + ");
				}
				rollString.append(dieRoll[i]);
			}
			System.out.println("rolls: " + rollString.toString());
		}

		if (keepCount > times)
		{
			keepCount = times;
		}

		//
		// Now keep the highest
		//
		int total = 0;
		for (int j = 0; j < keepCount; j++)
		{
			int highIdx = 0;
			for (int i = 1; i < times; i++)
			{
				if (dieRoll[i] > dieRoll[highIdx])
				{
					highIdx = i;
				}
			}
			total += dieRoll[highIdx];
			dieRoll[highIdx] = 0;		// so we don't use it again
		}
		if (Globals.isDebugMode())
		{
			System.out.println("total: " + total);
		}
		return total + modifier;
	}

	/*
	 *  #d# +/- #  ie 4d6 +1
	 *
	 */
	public static int roll(int times, int sides, int modifier)
	{
		return roll(times, sides, modifier, times, 0);
	}

	/*
	 *  takes a string in the form of "2d6 -2" and returns the result.
	 *  the form may use either 'd' or 'D' and '+' or '-'.
	 *  there must be a space between the sides and +/- sign.  like '#d# +#'
	 */
	public static int roll(String method)
	{
		Globals.rollInfo dieInfo = Globals.getDiceInfo(method);
		if (dieInfo != null)
		{
			return roll(dieInfo.times, dieInfo.sides, dieInfo.modifier, dieInfo.times, 0);
		}
		return 0;
	}
}
