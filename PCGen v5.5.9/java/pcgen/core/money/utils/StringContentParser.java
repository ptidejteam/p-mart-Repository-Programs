/*
 * StringContentParser.java
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
 */

package pcgen.core.money.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * StringContentParser.java
 * Provides services for parsing a string that has nested tokens
 * defined by more than one set of delimiters.
 *
 * It assumes the the clients using it have implemented the
 * iParsingClient interface.
 *
 *  -- parsing doesn't belong in the core; this
 * 	will be removed as part of the persistence refactor
 * 	sage_sam 10 Oct 2003
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version $Revision: 1.1 $
 */

public class StringContentParser
{

	private iParsingClient client = null;
	private String[] delimiters = null;
	private String content = null;

	private int currentDelimiter = 0;
	private int delimiterLimit = 0;

	private List parsedItems = null;

	/**
	 * A helper function to avoid doing the same work in more
	 * than one place.  Called by the constructor and one of
	 * the parse variants, it sets the object's fields.
	 *
	 * @param argContent     the string to be parsed
	 * @param argDelimiters  an array of argDelimiters to using when parsing
	 *                    various elements of the string
	 * @param argClient      and object that will receive the last
	 *                    parsed item in the tree.
	 */
	private final void setParms(String argContent, String[] argDelimiters, iParsingClient argClient)
	{
		delimiters = argDelimiters;
		client = argClient;
		content = argContent;
	}

	/**
	 * A constructor that accepts all the details needed to
	 * parse a string.  I did it this way mostly out of laziness.
	 * I don't want to have to deal with checking to see if any
	 * items are missing, and it fits in with my philosphy of
	 * not letting an object get into a state where it can't
	 * process.  Not that this is bulletproof, but...
	 *
	 * These parameters are merely passed on the the setParms
	 * method.
	 *
	 * @param argContent     the string to be parsed
	 * @param argDelimiters  an array of argDelimiters to using when parsing
	 *                    various elements of the string
	 * @param argClient      and object that will receive the last
	 *                    parsed item in the tree.
	 */
	public StringContentParser(String argContent, String[] argDelimiters, iParsingClient argClient)
	{
		setParms(argContent, argDelimiters, argClient);
	}

	/**
	 * The main function visible to clients, this version accepts
	 * the same information as the constructor.  This is to allow
	 * reusing the object for another string or object without having
	 * to endure the overhead of object creation and deletion.
	 *
	 * These parameters are merely passed on the the setParms
	 * method.
	 *
	 * @param argContent     the string to be parsed
	 * @param argDelimiters  an array of argDelimiters to using when parsing
	 *                    various elements of the string
	 * @param argClient      and object that will receive the last
	 *                    parsed item in the tree.
	 * Left alone for a while, will remove in a month unless it is used.
	 * @deprecated on 2003-08-08 as it is unused, will be removed sometime after 2003-09-22 unless this tag is replaced with an explanation for why this method should not be deleted.
	 */
	public void parse(String argContent, String[] argDelimiters, iParsingClient argClient)
	{
		setParms(argContent, argDelimiters, argClient);
		parseTheContent();
	}

	/**
	 * Parses the string using the fields in the object.
	 *
	 */
	public final void parse()
	{
		parseTheContent();
	}

	/**
	 * Initiates the process of actually parsing the string.
	 * This is protected because it's used only internally.
	 */
	private final void parseTheContent()
	{
		delimiterLimit = delimiters.length - 1;
		currentDelimiter = 0;

		parsedItems = new ArrayList();

//    Globals.debugPrint("In parseTheContent(): \n\tdelimiterLimit: " + delimiterLimit +
//                                          "\n\tcurrentDelimiter: " + currentDelimiter);

		parseItem(content, currentDelimiter);

	}

	/**
	 * A recursive function which parses each element as defined
	 * by the passed in delimiters.  When it reaches the point past which
	 * it can no longer tokenize, it passes the current token back to
	 * the client for processing.  This allows virutally unlimited
	 * depth in terms of nested tokens, but has the disadvantage of
	 * being recursive.  Defining too many levels of tokens in too
	 * large a string could prove to be prohibitive in terms of resource
	 * usage.
	 *
	 * The first time this is called, from the parseTheContent method,
	 * it will be passed the entire contents of the string and an index
	 * pointing to the first delimiter in the array.  It will tokenize
	 * that using the first delimiter.
	 *
	 * If the current delimiter is the last one in the list, instead
	 * of further tokenizing, this method will pass each of the
	 * tokens obtained here back to the client for processing.
	 *
	 * If there are more delimiters, it will pass each token to a
	 * recursive copy of the method, along with a number that
	 * corresponds to the next delimiter in the array.
	 *
	 * @param itemToParse  the individiual item to parse
	 * @param delimiter    a pointer to the delimiter to use in parsing
	 *                     this item.
	 *
	 */
	private final void parseItem(String itemToParse, int delimiter)
	{

//    Globals.debugPrint("\nIn parseItem(): \nitemToParse: \n" + itemToParse + " \n\tdelimiter: " + delimiter);
		final StringTokenizer token = new StringTokenizer(itemToParse, delimiters[delimiter], false);

		while (token.hasMoreTokens())
		{
			final String item = token.nextToken();
			if (delimiter == delimiterLimit)
			{
				parsedItems.add(item);
			}
			else
			{
				parseItem(item, delimiter + 1);
			}
		}

		if (delimiter == delimiterLimit)
		{
			client.parsedTokens(parsedItems);
			parsedItems.clear();
		}

	}
}
