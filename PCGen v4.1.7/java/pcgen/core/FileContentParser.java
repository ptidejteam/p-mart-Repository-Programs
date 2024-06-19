/*
 * FileContentParser.java
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
 * $Id: FileContentParser.java,v 1.1 2006/02/21 00:57:42 vauchers Exp $
 */

package pcgen.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * FileContentParser.java
 * Provides services for parsing a file that has nested tokens
 * defined by more than one set of delimiters.
 *
 * It assumes the the clients using it have implemented the
 * iParsingClient interface.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version $Revision: 1.1 $*
 */

final class FileContentParser extends StringContentParser
{
	/**
	 * Returns the complete contents of a file as a String
	 *
	 * @param fileName   the name of the file to read.
	 *
	 * @return  the contents of the file as a String object
	 */
	private static String getFileContents(String fileName)
	{
		FileInputStream aStream = null;

		try
		{
			final File aFile = new File(fileName);
			aStream = new FileInputStream(aFile);
			final int length = (int) aFile.length();
			final byte[] inputLine = new byte[length];

			aStream.read(inputLine, 0, length);
			return new String(inputLine);
		}
		catch (IOException exception)
		{
			Globals.errorPrint("ERROR:" + fileName, exception);
			return "";
		}
		finally
		{
			try
			{
				if (aStream != null)
				{
					aStream.close();
				}
			}
			catch (IOException e)
			{
				Globals.errorPrint("ERROR:" + fileName, e);
			}
		}
	}

	/**
	 * A constructor that accepts all the details needed to
	 * parse a file.  I did it this way mostly out of laziness.
	 * I don't want to have to deal with checking to see if any
	 * items are missing, and it fits in with my philosphy of
	 * not letting an object get into a state where it can't
	 * process.
	 *
	 * These parameters are merely passed on the the setParms
	 * method.
	 *
	 * @param fileName    the name of a file to read
	 * @param delimiters  an array of delimiters to using when parsing
	 *                    various elements of the file
	 * @param client      and object that will receive the last
	 *                    parsed item in the tree.
	 */
	FileContentParser(String fileName, String[] delimiters, iParsingClient client)
	{
		super(getFileContents(fileName), delimiters, client);
	}

	/**
	 * The main function visible to clients, this version accepts
	 * the same information as the constructor.  This is to allow
	 * reusing the object for another file or object without having
	 * to endure the overhead of object creation and deletion.
	 *
	 * These parameters are merely passed on the the setParms
	 * method.
	 *
	 * @param fileName    the name of a file to read
	 * @param delimiters  an array of delimiters to using when parsing
	 *                    various elements of the file
	 * @param client      and object that will receive the last
	 *                    parsed item in the tree.
	 */
	public void parse(String fileName, String[] delimiters, iParsingClient client)
	{
		super.parse(getFileContents(fileName), delimiters, client);
	}

}
