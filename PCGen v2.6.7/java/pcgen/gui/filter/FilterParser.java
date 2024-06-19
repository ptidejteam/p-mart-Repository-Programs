/*
 * FilterParser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 20, 2002, 5:30 PM
 */
package pcgen.gui.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>FilterParser</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public class FilterParser
{
	private List[] filterList;

	/**
	 * Constructor
	 */
	public FilterParser(List filterList)
	{
		this(new List[]{filterList});
	}

	/**
	 * Constructor
	 */
	public FilterParser(List[] filterList)
	{
		this.filterList = filterList;
	}

	/**
	 * parse a filter definition String to create a PObjectFilter
	 *
	 * @return the created PObjectFilter
	 * author: Thomas Behr 20-02-02
	 */
	public PObjectFilter parse(String filterDefinition) throws FilterParseException
	{
		List tokenList = createTokenList(normalize(filterDefinition));
		checkTokens(tokenList);

//                  System.out.print(" enforceStrongAssociationForNOT :\n");
//                  for (Iterator it = enforceStrongAssociationForNOT(tokenList).iterator(); it.hasNext();) {
//                          System.out.print(" >>>" + (String)it.next() + "<<<\n");
//                  }

		return parseTokenList(enforceStrongAssociationForNOT(tokenList));
	}

	/**
	 * break String down to usable tokens
	 *
	 * author: Thomas Behr 20-02-02
	 */
	private List createTokenList(String parseString) throws FilterParseException
	{
		int braceCount = 0;
		int bracketCount = 0;

		ArrayList list = new ArrayList();

		boolean filterName = false;

		String token;
		StringBuffer name = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(parseString, " []()", true);
		while (tokens.hasMoreTokens())
		{
			token = tokens.nextToken();

			if (token.equals("("))
			{
				braceCount++;
			}
			else if (token.equals(")"))
			{
				braceCount--;
			}
			else if (token.equals("["))
			{
				name.delete(0, name.length());
				name.append(token);
				token = "";
				filterName = true;
				bracketCount++;
			}
			else if (token.equals("]"))
			{
				name.append(token);
				token = name.toString();
				filterName = false;
				bracketCount--;
			}
			else if (!filterName)
			{
				token = token.toUpperCase();
			}

			if (!filterName)
			{

				token = token.trim();
				if (token.length() > 0)
				{
					list.add(token);
				}

			}
			else
			{
				name.append(token);
			}

		} // end while (tokens.hasMoreTokens())

		if (braceCount > 0)
		{
			throw new FilterParseException("Missing ')'.");
		}
		else if (braceCount < 0)
		{
			throw new FilterParseException("Missing '('.");
		}

		if (bracketCount > 0)
		{
			throw new FilterParseException("Missing ']'.");
		}
		else if (bracketCount < 0)
		{
			throw new FilterParseException("Missing '['.");
		}

		return list;
	}

	/**
	 * author: Thomas Behr 20-02-02
	 */
	private List enforceStrongAssociationForNOT(List tokenList)
	{
		ArrayList newTokenList = new ArrayList();

		int index = tokenList.indexOf("NOT");
		if (index == -1)
		{
			return tokenList;
		}

		newTokenList.addAll(tokenList.subList(0, index));
		newTokenList.add("(");
		newTokenList.add("NOT");

		ArrayList restList = new ArrayList(tokenList.subList(index + 1, tokenList.size()));

		String firstRestToken = (String)restList.get(0);
		if (firstRestToken.equals("("))
		{

			int i = 0;
			int braceCount = 1;
			String token;

			restList.remove(i);
			for (Iterator it = restList.iterator(); it.hasNext(); i++)
			{
				token = (String)it.next();
				if (token.equals("("))
				{
					braceCount++;
				}
				else if (token.equals(")"))
				{
					braceCount--;
				}

				if (braceCount == 0)
				{
					break;
				}
			}
			restList.remove(i);

			if (i == restList.size())
			{
				newTokenList.add("(");
				newTokenList.addAll(enforceStrongAssociationForNOT(restList));
				newTokenList.add(")");
				newTokenList.add(")");
			}
			else
			{
				newTokenList.add("(");
				newTokenList.addAll(enforceStrongAssociationForNOT(restList.subList(0, i)));
				newTokenList.add(")");
				newTokenList.add(")");
				newTokenList.addAll(enforceStrongAssociationForNOT(restList.subList(i, restList.size())));
			}

		}
		else if (firstRestToken.startsWith("["))
		{
			restList.remove(0);
			newTokenList.add(firstRestToken);
			newTokenList.add(")");
			newTokenList.addAll(enforceStrongAssociationForNOT(restList));
		}

		return newTokenList;
	}

	/**
	 * recursivly build coumpound filter
	 *
	 * author: Thomas Behr 20-02-02
	 */
	private PObjectFilter parseTokenList(List tokenList) throws FilterParseException
	{
		PObjectFilter filter = null;

		String firstToken;
		firstToken = (String)tokenList.get(0);

		if (firstToken.equals("("))
		{

			int i = 0;
			int braceCount = 1;
			String token;

			tokenList.remove(i);
			for (Iterator it = tokenList.iterator(); it.hasNext(); i++)
			{
				token = (String)it.next();
				if (token.equals("("))
				{
					braceCount++;
				}
				else if (token.equals(")"))
				{
					braceCount--;
				}

				if (braceCount == 0)
				{
					break;
				}
			}
			tokenList.remove(i);

			if (i == tokenList.size())
			{
				filter = parseTokenList(tokenList);
			}
			else
			{
				ArrayList tokenList1 = new ArrayList(tokenList.subList(0, i));
				ArrayList tokenList2 = new ArrayList(tokenList.subList(i + 1, tokenList.size()));
				filter = FilterFactory.createCompoundFilter(parseTokenList(tokenList1),
					parseTokenList(tokenList2),
					(String)tokenList.get(i));
			}

		}
		else if (firstToken.equals("NOT"))
		{
			/*
			 * this means 'NOT' is weak associative
			 */
			tokenList.remove(0);
			filter = FilterFactory.createInverseFilter(parseTokenList(tokenList));
		}
		else if (firstToken.startsWith("["))
		{
			String filterName = (String)tokenList.remove(0);
			if (tokenList.size() > 0)
			{
				String operand = (String)tokenList.remove(0);
				filter = FilterFactory.createCompoundFilter(retrieveFilter(filterName),
					parseTokenList(tokenList),
					operand);
			}
			else
			{
				filter = retrieveFilter(filterName);
			}
		}
		else
		{
			throw new FilterParseException("Malformed token " + firstToken + ".");
		}

		return filter;
	}

	/**
	 * author: Thomas Behr 20-02-02
	 */
	private void checkTokens(List tokenList) throws FilterParseException
	{
		String token;
		String lastToken = "";
		for (Iterator it = tokenList.iterator(); it.hasNext();)
		{
			token = (String)it.next();

			if (!isLegalToken(token))
			{
				throw new FilterParseException("Malformed token " + token + ".");
			}

			int sequenceError = isLegalTokenSequence(lastToken, token);
			if (sequenceError == -1)
			{
				throw new FilterParseException("Empty statement.");
			}
			else if (sequenceError == -2)
			{
				throw new FilterParseException("Missing operand.");
			}
			else if (sequenceError == -3)
			{
				throw new FilterParseException("Illegal token sequence '" + lastToken + " " + token + "'.");
			}
			else if (sequenceError == -4)
			{
				throw new FilterParseException("Illegal first token '" + token + "'.");
			}

			lastToken = token;
		}


		if (isLegalTokenSequence(lastToken, "") != 0)
		{
			throw new FilterParseException("Illegal last token '" + lastToken + "'.");
		}
	}

	/**
	 * author: Thomas Behr 20-02-02
	 */
	protected boolean isLegalToken(String token)
	{
		String s = token.toUpperCase();
		return s.equals("(") || s.equals(")") ||
			s.equals("AND") || s.equals("NOT") || s.equals("OR") ||
			(s.startsWith("[") && s.endsWith("]") && (s.length() > 2));
	}

	/**
	 * @return  0 if legal sequence
	 *         -1 if empty statement
	 *         -2 if missing operand
	 *         -3 if illegal sequence
	 *         -4 if illegal first token
	 *         -5 if illegal last token
	 * author: Thomas Behr 21-02-02
	 */
	private int isLegalTokenSequence(String token1, String token2)
	{
		String s1 = token1.toUpperCase();
		String s2 = token2.toUpperCase();

		// empty statement
		if (s1.endsWith("(") && s2.startsWith(")"))
		{
			return -1;
		}
		// missing operand
		if (s1.endsWith(")") && (s2.startsWith("(") || s2.startsWith("[")))
		{
			return -2;
		}
		// missing operand
		if (s1.endsWith("]") && (s2.startsWith("(") || s2.startsWith("[")))
		{
			return -2;
		}
		// illegal sequence
		if (s1.equals("AND") && !(s2.equals("NOT") || s2.startsWith("(") || s2.startsWith("[")))
		{
			return -3;
		}
		// illegal sequence
		if (s1.equals("NOT") && !(s2.startsWith("(") || s2.startsWith("[")))
		{
			return -3;
		}
		// illegal sequence
		if (s1.equals("OR") && !(s2.equals("NOT") || s2.startsWith("(") || s2.startsWith("[")))
		{
			return -3;
		}
		// illegal first token
		if (s1.equals("") && !(s2.equals("NOT") || s2.startsWith("(") || s2.startsWith("[")))
		{
			return -4;
		}
		// illegal last token
		if (s2.equals("") && !(s1.endsWith(")") || s1.endsWith("]")))
		{
			return -5;
		}

		return 0;
	}

	/**
	 * retrieve filter according to category and name
	 *
	 * author: Thomas Behr 20-02-02
	 */
	private PObjectFilter retrieveFilter(String filterName) throws FilterParseException
	{
		PObjectFilter filter;
		for (int i = 0; i < filterList.length; i++)
		{
			for (Iterator it = filterList[i].iterator(); it.hasNext();)
			{
				filter = (PObjectFilter)it.next();
				if (filterName.equals("[" + filter.getCategory() + PObjectFilter.SEPARATOR + filter.getName() + "]"))
				{
					return filter;
				}
			}
		}

		throw new FilterParseException("Could not find filter " + filterName + ".");
	}

	/**
	 * replace all whitespace characters with " "
	 *
	 * author: Thomas Behr 20-02-02
	 */
	private String normalize(String s)
	{
		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(s, "\t\n\r\f");
		while (tokens.hasMoreTokens())
		{
			buffer.append(tokens.nextToken()).append(" ");
		}

		return buffer.toString().trim();
	}
}

class FilterParseException extends Exception
{
	/**
	 * Constructor
	 */
	public FilterParseException()
	{
		super();
	}

	/**
	 * Constructor
	 */
	public FilterParseException(String message)
	{
		super(message);
	}
}
