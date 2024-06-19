/*
 * SpecialAbilityLoader.java
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
 * $Id: SpecialAbilityLoader.java,v 1.1 2006/02/21 01:16:12 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.SpecialAbility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SpecialAbilityLoader
{

	/** Creates a new instance of SpecialAbilityLoader */
	private SpecialAbilityLoader()
	{
	}

	public static void parseLine(SpecialAbility sa, String inputLine, URL sourceURL, int lineNum) throws PersistenceLayerException
	{
		if (sa == null)
		{
			return;
		}

		final StringTokenizer aTok = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		int i = 0;
		while (aTok.hasMoreElements())
		{
			final String colString = (String) aTok.nextElement();
			if (i == 0)
			{
				sa.setName(colString);
			}
			else if (i == 1)
			{
				sa.setSADesc(colString);
			}
			else
			{
				throw new PersistenceLayerException("Illegal special ability info " + sourceURL.toString() +
					":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			i++;
		}
	}

}
