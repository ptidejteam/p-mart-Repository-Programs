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
 * $Id: RollInfo.java,v 1.1 2006/02/20 23:54:34 vauchers Exp $
 */

package pcgen.core;

import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Structure representing dice rolls
 */
public class RollInfo
{
	/** Number of dice to roll. */
	int times = 0;
	/** What shape dice to roll. */
	int sides = 0;
	/** Amount to add to the final roll. */
	int modifier = 0;
	/** Which specific rolls to keep after rolls have been sorted
	 * in ascending order.  <code>null</code> means to keep all
	 * rolls.  Example, [1,3] means to keep the first and third
	 * lowest rolls, which would be {true, false true} for 3 dice.
	 * keepTop and keepBottom are implemented as special kinds of
	 * this array. */
	boolean[] keepList = null;
	/** Rerolls rolls below this amount. */
	int rerollBelow = Integer.MIN_VALUE;
	/** Rerolls rolls above this amount. */
	int rerollAbove = Integer.MAX_VALUE;
	/** Total result never less than this. */
	int totalFloor = Integer.MIN_VALUE;
	/** Total result never greater than this. */
	int totalCeiling = Integer.MAX_VALUE;

	/** Construct a blank <code>RollInfo</code>. */
	public RollInfo()
	{
	}

	/** Wrap string tokenizing with debugging. */
	private String debugTokenize(StringTokenizer st, String delim, String rollString)
	{
		if (!st.hasMoreTokens())
		{
			Globals.debugErrorPrint("Surprise!  Out of tokens in '" + rollString + "'");
			return null;
		}

		String tok;

		try
		{
			tok = st.nextToken(delim);
		}

		catch (Exception ex)
		{
			Globals.debugErrorPrint("Darn--tokenizer barfed in '" + rollString + "': " + ex, ex);
			return null;
		}

		Globals.debugPrint("token: " + tok);

		return tok;
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
	public RollInfo(String rollString)
	{
		// To really do this right, we change the token string
		// as we go along so that we maintain parser state by
		// means of the tokens rather than something more
		// explicit.  In truth, this is an ideal application
		// of flex and friends for a "mini-language" whose
		// statements evaluate to dice rolls.  Too much LISP
		// on the brain.  --bko
		StringTokenizer st = new StringTokenizer(rollString, " ", true);

		try
		{
			//String tok = debugTokenize(st, "d", rollString);
			String tok = st.nextToken("d");

			if (tok.equals("d"))
				times = 1;
			else
			{
				times = Integer.parseInt(tok);
				//tok = debugTokenize(st, "d", rollString); // discard the 'd'
				tok = st.nextToken("d"); // discard the 'd'
				if (!tok.equals("d"))
				{
					Globals.debugErrorPrint("Bad roll parsing in '" + rollString + "': missing 'd'");
					return;
				}
			}

			String parseChars = "/\\|mM+-tT";
			//sides = Integer.parseInt(debugTokenize(st, parseChars, rollString));
			sides = Integer.parseInt(st.nextToken(parseChars));

			if (sides < 1)
			{
				Globals.debugErrorPrint("Bad roll parsing in '" + rollString + "': sides < 1: " + sides);
				return;
			}

			while (st.hasMoreTokens())
			{
				//tok = debugTokenize(st, parseChars, rollString);
				tok = st.nextToken(parseChars);
				switch (tok.charAt(0))
				{
					case '/':
						parseChars = "mM+-tT";
						//int keepTop = Integer.parseInt(debugTokenize(st, parseChars, rollString));
						int keepTop = Integer.parseInt(st.nextToken(parseChars));
						if (keepTop > times)
						{
							Globals.debugErrorPrint("Bad keepTop in '" + rollString + "': times: " + times + "; keepTop: " + keepTop);
							return;
						}
						keepList = new boolean[times];
						// Rely on fact boolean is false by default.  --bko
						for (int i = times - keepTop; i < times; ++i)
							keepList[i] = true;
						break;
					case '\\':
						parseChars = "mM+-tT";
						//int keepBottom = Integer.parseInt(debugTokenize(st, parseChars, rollString));
						int keepBottom = Integer.parseInt(st.nextToken(parseChars));
						if (keepBottom > times)
						{
							Globals.debugErrorPrint("Bad keepBottom in '" + rollString + "': times: " + times + "; keepBottom: " + keepBottom);
							return;
						}
						keepList = new boolean[times];
						// Rely on fact boolean is false by default.  --bko
						for (int i = 0; i < keepBottom; ++i)
							keepList[i] = true;
						break;
					case '|':
						parseChars = "mM+-tT";
						//tok = debugTokenize(st, parseChars, rollString);
						tok = st.nextToken(parseChars);
						keepList = new boolean[times];
						StringTokenizer keepSt = new StringTokenizer(tok, ",");
						while (keepSt.hasMoreTokens())
							//keepList[Integer.parseInt(debugTokenize(keepSt, ",", rollString)) - 1] = true;
							keepList[Integer.parseInt(keepSt.nextToken(",")) - 1] = true;
						break;
					case 'm':
						parseChars = "M+-tT";
						//rerollBelow = Integer.parseInt(debugTokenize(st, parseChars, rollString));
						rerollBelow = Integer.parseInt(st.nextToken(parseChars));
						break;
					case 'M':
						parseChars = "m+-tT";
						//rerollAbove = Integer.parseInt(debugTokenize(st, parseChars, rollString));
						rerollAbove = Integer.parseInt(st.nextToken(parseChars));
						break;
					case '+':
						parseChars = "tT";
						//modifier = Integer.parseInt(debugTokenize(st, " ", rollString));
						modifier = Integer.parseInt(st.nextToken(" "));
						break;
					case '-':
						parseChars = "tT";
						//modifier = -Integer.parseInt(debugTokenize(st, " ", rollString));
						modifier = -Integer.parseInt(st.nextToken(" "));
						break;
					case 't':
						parseChars = "T";
						//totalFloor = Integer.parseInt(debugTokenize(st, " ", rollString));
						totalFloor = Integer.parseInt(st.nextToken(" "));
						break;
					case 'T':
						parseChars = "t";
						//totalCeiling = Integer.parseInt(debugTokenize(st, " ", rollString));
						totalCeiling = Integer.parseInt(st.nextToken(" "));
						break;
					default:
						Globals.debugErrorPrint("Bizarre dice parser error in '" + rollString + "': not a valid delimiter");
				}
			}
		}

		catch (Exception ex)
		{
			Globals.debugErrorPrint("Bad roll string in '" + rollString + "': " + ex, ex);
		}
	}

	public String toString()
	{
		String s = "";
		if (times > 1) s += times;

		s += "d" + sides;

		while (keepList != null) // let break work
		{
			int p = 0, i = 0;

			for (i = 0; i < times; ++i)
				if (keepList[i] == true) break;
			if (i == times) // all false
			{
				Globals.debugErrorPrint("Bad rolls: nothing to keep!");
				return null;

			}

			// Note the ordering: by testing for bottom
			// first, we can also test if all the dice are
			// all to be kept, and drop the
			// top/bottom/list specification completely.

			// First test for bottom
			for (i = 0; i < times; ++i)
				if (keepList[i] == false) break;
			if (i == times) break; // all true
			p = i;
			for (; i < times; ++i)
				if (keepList[i] == true) break;
			if (p > 0 && i == times)
			{
				s += "\\" + p;
				break;
			}

			// Second test for top
			for (i = 0; i < times; ++i)
				if (keepList[i] == true) break;
			p = i;
			for (; i < times; ++i)
				if (keepList[i] == false) break;
			if (p > 0 && i == times)
			{
				s += "/" + (times - p);
				break;
			}

			// Finally, we have a list
			s += "|";
			boolean first = true;
			for (i = 0; i < times; ++i)
			{
				if (!keepList[i]) continue;
				if (first)
					first = false;
				else
					s += ",";
				s += i + 1;
			}
		}

		if (rerollBelow != Integer.MIN_VALUE) s += "m" + rerollBelow;
		if (rerollAbove != Integer.MAX_VALUE) s += "M" + rerollAbove;

		if (modifier > 0)
			s += "+" + modifier;
		else if (modifier < 0) s += "-" + -modifier;

		if (totalFloor != Integer.MIN_VALUE) s += "t" + totalFloor;
		if (totalCeiling != Integer.MAX_VALUE) s += "T" + totalCeiling;

		return s;
	}

	// An alternative parser, ala the red dragon book.
	public class CIS
	{
		String expr;
		StringTokenizer st;

		public CIS(String s)
		{
			expr = s.toLowerCase(); // case-insensitive
			st = new StringTokenizer(expr, " \t\n()+=*/%d", true);
		}

		Stack stack = new Stack();

		static final int NONE = -1;
		static final int NUM = 256;
		static final int DONE = 267;

		int tokenval = NONE;
		int lookahead;

		int result;

		int scan()
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
							Globals.debugErrorPrint("not looking at a number: " + ex, ex);
						}
						return NUM;

					default:
						tokenval = NONE;
						return t;
				}
			}

			return DONE;
		}

		void parse()
		{
			lookahead = scan();

			while (lookahead != DONE)
				expr();

			System.out.println(result);
		}

		void expr()
		{
			term();

			while (true)
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

		void term()
		{
			roll();

			while (true)
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

		void roll()
		{
			factor();

			while (true)
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

		void factor()
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

		void match(int t)
		{
			if (lookahead == t)
				lookahead = scan();

			else
			{
				char t1 = (char)lookahead;
				char t2 = (char)t;

				error("syntax error: " + lookahead + " (" + t1 + ") != "
					+ t + " (" + t2 + ")");
			}
		}

		void emit(int t, int tval)
		{
			char c;
			int a, b;

			switch (t)
			{
				case '+':
					c = (char)t;
					b = ((Integer)stack.pop()).intValue();
					a = ((Integer)stack.pop()).intValue();
					result = a + b;
					stack.push(new Integer(result));
					break;

				case '-':
					c = (char)t;
					b = ((Integer)stack.pop()).intValue();
					a = ((Integer)stack.pop()).intValue();
					result = a - b;
					stack.push(new Integer(result));
					break;

				case '*':
					c = (char)t;
					b = ((Integer)stack.pop()).intValue();
					a = ((Integer)stack.pop()).intValue();
					result = a * b;
					stack.push(new Integer(result));
					break;

				case '/':
					c = (char)t;
					b = ((Integer)stack.pop()).intValue();
					a = ((Integer)stack.pop()).intValue();
					result = a / b;
					stack.push(new Integer(result));
					break;

				case '%':
					c = (char)t;
					b = ((Integer)stack.pop()).intValue();
					a = ((Integer)stack.pop()).intValue();
					result = a % b;
					stack.push(new Integer(result));
					break;

				case 'd':
					c = (char)t;
					b = ((Integer)stack.pop()).intValue();
					a = ((Integer)stack.pop()).intValue();
					result = RollingMethods.roll(a, b);
					stack.push(new Integer(result));
					break;

				case NUM:
					stack.push(new Integer(tval));
					break;

				default:
					System.out.println("token " + t + ", value " + tval);
			}
		}

		void error(String s)
		{
			System.err.println(s);
			System.exit(1);
		}

//     public static void main (String[] args) {
// 	for (int i = 0, x = args.length; i < x; ++i)
// 	    (new CIS (args[i])).parse ( );
//     }
	}

	/**
	 * Roll the dice.  UNIMPLEMENTED FOR NOW!
	 *
	 * @return int the results
	 */
	public int roll()
	{
		int result = 0;

		return result;
	}

	// Boy, does this need testing!
	public static void main(String[] args)
	{
		Globals.setDebugMode(true);
		for (int i = 0; i < args.length; ++i)
		{
			RollInfo ri = new RollInfo(args[i]);
			System.out.println(ri + ": " + ri.roll());
		}
	}
}
