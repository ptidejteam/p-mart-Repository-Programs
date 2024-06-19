/*
 * PaperInfoLoader.java
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
 * Created on February 27, 2002, 7:42 PM
 *
 * $Id: PaperInfoLoader.java,v 1.1 2006/02/21 01:00:36 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.PaperInfo;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class PaperInfoLoader
{

	/** Creates a new instance of PaperInfoLoader */
	private PaperInfoLoader()
	{
	}

	public static void parseLine(PaperInfo obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer aTok = new StringTokenizer(inputLine, "\t", false);
		int iCount = 0;
		while (aTok.hasMoreElements())
		{
			String colString = (String) aTok.nextElement();
			try
			{
				obj.setPaperInfo(iCount, colString);
			}
			catch (IndexOutOfBoundsException e)
			{
				throw new PersistenceLayerException("Illegal paper size info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			iCount += 1;
		}
	}

}
