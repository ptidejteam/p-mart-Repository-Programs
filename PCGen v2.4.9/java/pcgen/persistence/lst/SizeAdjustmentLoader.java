/*
 * SizeAdjustmentLoader.java
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
 * Created on February 22, 2002, 10:29 PM
 *
 * $Id: SizeAdjustmentLoader.java,v 1.1 2006/02/20 23:52:37 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class SizeAdjustmentLoader
{

	/** Creates a new instance of SizeAdjustmentLoader */
	private SizeAdjustmentLoader()
	{
	}

	public static void parseLine(SizeAdjustment obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int iCount = 0;
		while (colToken.hasMoreElements())
		{
			String aString = (String)colToken.nextElement();
			switch (iCount)
			{
				case 0:
					obj.setName(aString);
					break;
				case 1:
					obj.setType(aString);
					break;
				case 2:
					obj.setMultiple(aString);
					break;
				default:
					throw new PersistenceLayerException("Illegal size adjustment info " + sourceFile.getName() +
						":" + Integer.toString(lineNum) + " \"" + aString + "\"");
			}
			iCount += 1;
		}
	}
}
