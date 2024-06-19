/*
 * RollInfo.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 *
 * $Id: RollInfo.java,v 1.1 2006/02/21 01:00:27 vauchers Exp $
 */

package pcgen.core;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 * <code>RollInfo</code>.
 *
 * Structure representing dice rolls
 *
 * @author ???
 * @version $Revision: 1.1 $
 */

final class RollInfo
{
	/** Number of dice to roll. */
	protected int times = 0;
	/** What shape dice to roll. */
	protected int sides = 0;
	/** Amount to add to the final roll. */
	private int modifier = 0;
	/** Which specific rolls to keep after rolls have been sorted
	 * in ascending order.  <code>null</code> means to keep all
	 * rolls.  Example, [1,3] means to keep the first and third
	 * lowest rolls, which would be {true, false true} for 3 dice.
	 * keepTop and keepBottom are implemented as special kinds of
	 * this array. */
	private boolean[] keepList = null;
	/** Rerolls rolls below this amount. */
	private int rerollBelow = Integer.MIN_VALUE;
	/** Rerolls rolls above this amount. */
	private int rerollAbove = Integer.MAX_VALUE;
	/** Total result never less than this. */
	private int totalFloor = Integer.MIN_VALUE;
	/** Total result never greater than this. */
	private int totalCeiling = Integer.MAX_VALUE;

	/** Construct a blank <code>RollInfo</code>. */
	RollInfo()
	{
	}

	/**
	 * Construct a <code>RollInfo</code> from a string.  The
	 * rules:<ol>
	 *
	 * <li>Optional positive integer, <var>times</var>.</li>
	 *
	 * <li>Literal 'd' followed by positive integer,
	 * <var>sides</var>.</li>
	 *
	 * <li>Optional literal '/' followed by positive integer,
	 * <var>keepTop</var>, or literal '\' followed by positive
	 * integer, <var>keepBottom</var>, or literal '|' followed by
	 * comma-separated list of postitive integers,
	 * <var>keepList</var> (1-indexed after dice have been
	 * sorted).</li>
	 *
	 * <li>Optional literal 'm' (minimum) followed by positive
	 * integer, <var>rerollAbove</var>, or literal 'M' (maximum)
	 * followed by postive integer, <var>rerollBelow</var>.</li>
	 *
	 * <li>Optional literal '+' or '-' followed by positive
	 * integer, <var>modifier</var>.</li>
	 *
	 * <li>Optional literal 't' followed by positive integer,
	 * <var>totalFloor</var>, or literal 'T' followed by a
	 * positive *integer, <var>totalCeiling</var>.</li>
	 *
	 * </ol> Unlike previous versions of this method, it is
	 * <strong>case-sensitive</strong> with respect to the
	 * alphabetic characters, e.g., only <code>d</code>
	 * (lower-case) is now valid, not also <code>D</code>
	 * (upper-case).  This is to accomodate the expanded ways to
	 * roll.
	 *
	 * @param rollString String compact representation of dice rolls
	 *
	 */
	RollInfo(String rollString)
	{
		// To really do this right, we change the token string
		// as we go along so that we maintain parser state by
		// means of the tokens rather than something more
		// explicit.  In truth, this is an ideal application
		// of flex and friends for a "mini-language" whose
		// statements evaluate to dice rolls.  Too much LISP
		// on the brain.  --bko
		final StringTokenizer st = new StringTokenizer(rollString, " ", true);

		try
		{
			String tok = st.nextToken("d");

			if ("d".equals(tok))
				times = 1;
			else
			{
				times = Integer.parseInt(tok);
				if (st.hasMoreTokens())
				{
					tok = st.nextToken("d"); // discard the 'd'
					if (!"d".equals(tok))
					{
						Globals.errorPrint("Bad roll parsing in '" + rollString + "': missing 'd'");
						return;
					}
				}
				else
				{
					sides = 1;
					return;
				}
			}

			String parseChars = "/\\|mM+-tT";
			sides = Integer.parseInt(st.nextToken(parseChars));

			if (sides < 1)
			{
				Globals.errorPrint("Bad roll parsing in '" + rollString + "': sides < 1: " + sides);
				return;
			}

			while (st.hasMoreTokens())
			{
				tok = st.nextToken(parseChars);
				switch (tok.charAt(0))
				{
					case '/':
						parseChars = "mM+-tT";
						int keepTop = Integer.parseInt(st.nextToken(parseChars));
						if (keepTop > times)
						{
							Globals.errorPrint("Bad keepTop in '" + rollString + "': times: " + times + "; keepTop: " + keepTop);
							return;
						}
						keepList = new boolean[times];
						// Rely on fact boolean is false by default.  --bko
						for (int i = times - keepTop; i < times; ++i)
						{
							keepList[i] = true;
						}
						break;
					case '\\':
						parseChars = "mM+-tT";
						int keepBottom = Integer.parseInt(st.nextToken(parseChars));
						if (keepBottom > times)
						{
							Globals.errorPrint("Bad keepBottom in '" + rollString + "': times: " + times + "; keepBottom: " + keepBottom);
							return;
						}
						keepList = new boolean[times];
						// Rely on fact boolean is false by default.  --bko
						for (int i = 0; i < keepBottom; ++i)
						{
							keepList[i] = true;
						}
						break;
					case '|':
						parseChars = "mM+-tT";
						tok = st.nextToken(parseChars);
						keepList = new boolean[times];
						StringTokenizer keepSt = new StringTokenizer(tok, ",");
						while (keepSt.hasMoreTokens())
						{
							keepList[Integer.parseInt(keepSt.nextToken(",")) - 1] = true;
						}
						break;
					case 'm':
						parseChars = "M+-tT";
						rerollBelow = Integer.parseInt(st.nextToken(parseChars));
						break;
					case 'M':
						parseChars = "m+-tT";
						rerollAbove = Integer.parseInt(st.nextToken(parseChars));
						break;
					case '+':
						parseChars = "tT";
						modifier = Integer.parseInt(st.nextToken(" "));
						break;
					case '-':
						parseChars = "tT";
						modifier = -Integer.parseInt(st.nextToken(" "));
						break;
					case 't':
						parseChars = "T";
						totalFloor = Integer.parseInt(st.nextToken(" "));
						break;
					case 'T':
						parseChars = "t";
						totalCeiling = Integer.parseInt(st.nextToken(" "));
						break;
					default:
						Globals.errorPrint("Bizarre dice parser error in '" + rollString + "': not a valid delimiter");
				}
			}
		}

		catch (NumberFormatException ex)
		{
			Globals.errorPrint("Bad roll string in '" + rollString + "': " + ex, ex);
		}
	}

	public String toString()
	{
		final StringBuffer buf = new StringBuffer();
		if (times > 1)
		{
			buf.append(times);
		}

		buf.append("d").append(sides);

		while (keepList != null) // let break work
		{
			int p = 0, i = 0;

			for (i = 0; i < times; ++i)
			{
				if (keepList[i])
				{
					break;
				}
			}
			if (i == times) // all false
			{
				Globals.errorPrint("Bad rolls: nothing to keep!");
				return null;

			}

			// Note the ordering: by testing for bottom
			// first, we can also test if all the dice are
			// all to be kept, and drop the
			// top/bottom/list specification completely.

			// First test for bottom
			for (i = 0; i < times; ++i)
			{
				if (!keepList[i])
				{
					break;
				}
			}
			if (i == times)
			{
				break; // all true
			}
			p = i;
			for (; i < times; ++i)
			{
				if (keepList[i])
				{
					break;
				}
			}
			if (p > 0 && i == times)
			{
				buf.append("\\").append(p);
				break;
			}

			// Second test for top
			for (i = 0; i < times; ++i)
			{
				if (keepList[i])
				{
					break;
				}
			}

			p = i;
			for (; i < times; ++i)
			{
				if (!keepList[i])
				{
					break;
				}
			}
			if (p > 0 && i == times)
			{
				buf.append("/").append((times - p));
				break;
			}

			// Finally, we have a list
			buf.append("|");
			boolean first = true;
			for (i = 0; i < times; ++i)
			{
				if (!keepList[i])
				{
					continue;
				}
				if (first)
				{
					first = false;
				}
				else
				{
					buf.append(",");
				}
				buf.append(i + 1);
			}
		}

		if (rerollBelow != Integer.MIN_VALUE)
		{
			buf.append("m" + rerollBelow);
		}
		if (rerollAbove != Integer.MAX_VALUE)
		{
			buf.append("M" + rerollAbove);
		}

		if (modifier > 0)
		{
			buf.append("+").append(modifier);
		}
		else if (modifier < 0)
		{
			buf.append("-").append(-modifier);
		}

		if (totalFloor != Integer.MIN_VALUE)
		{
			buf.append("t").append(totalFloor);
		}
		if (totalCeiling != Integer.MAX_VALUE)
		{
			buf.append("T").append(totalCeiling);
		}

		return buf.toString();
	}

	// An alternative parser, ala the red dragon book.
	private final class CIS
	{
		private StringTokenizer st;

		private Stack stack = new Stack();

		static final int NONE = -1;
		static final int NUM = 256;
		static final int DONE = 267;

		private int tokenval = NONE;
		private int lookahead;

		private int result;

		private int scan()
		{
			while (st.hasMoreTokens())
			{
				String tok = st.nextToken();
				int t = tok.charAt(0);

				switch (t)
				{
					case ' ':
					case '\t':
					case '\n':
						continue;

					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						try
						{
							tokenval = Integer.parseInt(tok);
						}
						catch (NumberFormatException ex)
						{
							Globals.errorPrint("not looking at a number: " + ex, ex);
						}
						return NUM;

					default:
						tokenval = NONE;
						return t;
				}
			}

			return DONE;
		}

		private void parse()
		{
			lookahead = scan();

			while (lookahead != DONE)
			{
				expr();
			}
		}

		private void expr()
		{
			term();

			while (true)
			{
				switch (lookahead)
				{
					case '+':
					case '-':
						int t = lookahead;
						match(lookahead);
						term();
						emit(t, NONE);
						continue;

					default:
						return;
				}
			}
		}

		private void term()
		{
			roll();

			while (true)
			{
				switch (lookahead)
				{
					case '*':
					case '/':
						int t = lookahead;
						match(lookahead);
						roll();
						emit(t, NONE);
						continue;

					default:
						return;
				}
			}
		}

		private void roll()
		{
			factor();

			while (true)
			{
				switch (lookahead)
				{
					case 'd':
						int t = lookahead;
						match(lookahead);
						factor();
						emit(t, 'd'); // lowercase is canonical
						continue;

					default:
						return;
				}
			}
		}

		private void factor()
		{
			switch (lookahead)
			{
				case '(':
					match('(');
					expr();
					match(')');
					break;

				case NUM:
					emit(NUM, tokenval);
					match(NUM);
					break;

				default:
					error("syntax error: " + lookahead);
			}
		}

		private void match(int t)
		{
			if (lookahead == t)
				lookahead = scan();

			else
			{
				final char t1 = (char) lookahead;
				final char t2 = (char) t;

				error("syntax error: " + lookahead + " (" + t1 + ") != " + t + " (" + t2 + ")");
			}
		}

		private void emit(int t, int tval)
		{
			final char c;
			final int a;
			final int b;

			switch (t)
			{
				case '+':
					c = (char) t;
					b = ((Integer) stack.pop()).intValue();
					a = ((Integer) stack.pop()).intValue();
					result = a + b;
					stack.push(new Integer(result));
					break;

				case '-':
					c = (char) t;
					b = ((Integer) stack.pop()).intValue();
					a = ((Integer) stack.pop()).intValue();
					result = a - b;
					stack.push(new Integer(result));
					break;

				case '*':
					c = (char) t;
					b = ((Integer) stack.pop()).intValue();
					a = ((Integer) stack.pop()).intValue();
					result = a * b;
					stack.push(new Integer(result));
					break;

				case '/':
					c = (char) t;
					b = ((Integer) stack.pop()).intValue();
					a = ((Integer) stack.pop()).intValue();
					result = a / b;
					stack.push(new Integer(result));
					break;

				case '%':
					c = (char) t;
					b = ((Integer) stack.pop()).intValue();
					a = ((Integer) stack.pop()).intValue();
					result = a % b;
					stack.push(new Integer(result));
					break;

				case 'd':
					c = (char) t;
					b = ((Integer) stack.pop()).intValue();
					a = ((Integer) stack.pop()).intValue();
					result = RollingMethods.roll(a, b);
					stack.push(new Integer(result));
					break;

				case NUM:
					stack.push(new Integer(tval));
					break;

				default:
					//System.out.println("token " + t + ", value " + tval);
					break;
			}
		}

		private void error(String s)
		{
			Globals.errorPrint(s);
			System.exit(1);
		}
	}

	/**
	 * Roll the dice.  UNIMPLEMENTED FOR NOW!
	 *
	 * @return int the results
	 */
	private static int roll()
	{
		final int result = 0;

		return result;
	}

	// Boy, does this need testing!
	public static void main(String[] args)
	{
		Globals.setDebugMode(true);
		for (int i = 0; i < args.length; ++i)
		{
			RollInfo ri = new RollInfo(args[i]);
			System.out.println(ri + ": " + RollInfo.roll());
		}
	}
}
