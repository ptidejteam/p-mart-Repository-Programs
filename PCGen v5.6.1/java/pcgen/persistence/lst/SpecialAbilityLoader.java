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
 * $Id: SpecialAbilityLoader.java,v 1.1 2006/02/21 01:33:26 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.net.URL;
import java.util.StringTokenizer;
import pcgen.core.SpecialAbility;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
final class SpecialAbilityLoader extends LstLineFileLoader
{

	/** Creates a new instance of SpecialAbilityLoader */
	public SpecialAbilityLoader()
	{
	}

	/**
	 * @see pcgen.persistence.lst.LstLineFileLoader#parseLine(java.lang.String, java.net.URL)
	 */
	public void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException
	{
		SpecialAbility sa = new SpecialAbility();

		final StringTokenizer aTok = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
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
				Logging.errorPrint("Illegal special ability info '" + lstLine
					+ "' in " + sourceURL.toString());
			}
			i++;
		}

		if( !SystemCollections.getUnmodifiableSpecialsList().contains(sa) )
		{
			SystemCollections.addToSpecialsList(sa);
		}
	}
}
