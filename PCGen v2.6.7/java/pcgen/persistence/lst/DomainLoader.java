/*
 * DomainLoader.java
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
 * $Id: DomainLoader.java,v 1.1 2006/02/20 23:57:40 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class DomainLoader
{

	/** Creates a new instance of DomainLoader */
	private DomainLoader()
	{
	}

	public static void parseLine(Domain obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		int col = 0;
		if (!obj.isNewItem())
		{
			col=2; // .MOD skip required fields
			colToken.nextToken();
		}
		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();
			if (PObjectLoader.parseTag(obj, colString))
				continue;
			final int aLen = colString.length();
			if (col == 0)
			{
				obj.setName(colString);
			}
			else if (colString.startsWith("DESC:"))
				obj.setGrantedPower(colString.substring(5));
			else if ((aLen > 6) && colString.startsWith("SKILL"))
			{
				obj.setSkillList(colString.substring(6));
			}
			else if ((aLen > 5) && colString.startsWith("FEAT"))
			{
				obj.setFeatList(colString.substring(5));
			}
			else if ((aLen > 6) && colString.startsWith("SPELL"))
			{
				obj.setSpellList(colString.substring(6));
			}
			else if ((aLen > 7) && colString.startsWith("CHOOSE:"))
			{
				obj.setChoiceString(colString.substring(7));
			}
			else if ((aLen > 3) && colString.startsWith("SA:"))
			{
				obj.addSpecialAbility(colString.substring(3));
			}
			else if (colString.startsWith("QUALIFY:"))
			{
				obj.setQualifyString(colString.substring(8));
			}
			else if (col == 1)
			{
				obj.setGrantedPower(colString);
			}
			else
			{
				throw new PersistenceLayerException("Illegal obj info " + sourceFile.getName() + ":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			}
			col++;
		}
	}
}
