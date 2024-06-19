/*
 * SkillLoader.java
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
 * $Id: SkillLoader.java,v 1.1 2006/02/21 00:02:33 vauchers Exp $
 */

package pcgen.persistence.lst;

import java.io.File;
import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.1 $
 */
public class SkillLoader
{

	/** Creates a new instance of SkillLoader */
	private SkillLoader()
	{
	}

	public static void parseLine(Skill obj, String inputLine, File sourceFile, int lineNum) throws PersistenceLayerException
	{
		final StringTokenizer colToken = new StringTokenizer(inputLine, LstSystemLoader.TAB_DELIM, false);
		String colString = null;
		int col = 0;
		if (!obj.isNewItem())
		{
			col = 4; // .MOD skip all 4 required fields
			colToken.nextToken(); // skip name;
		}
		while (colToken.hasMoreTokens())
		{
			colString = colToken.nextToken().trim();

			if (PObjectLoader.parseTag(obj, colString))
				continue;

			if (col == 1 && colString.indexOf(":") > -1)
				col = 5;
			if (col == 0)
				obj.setName(colString);
			else if (colString.startsWith("ACHECK:"))
				obj.setACheck(colString.substring(7));
			else if (colString.startsWith("CHOOSE:"))
				obj.addChoiceList(colString.substring(7));
			else if (colString.startsWith("CLASSES:"))
				obj.addClassList(colString.substring(8));
			else if (colString.startsWith("EXCLUSIVE:"))
				obj.setIsExclusive(colString.substring(10));
			else if (colString.startsWith("KEYSTAT:"))
				obj.setKeyStat(colString.substring(8));
			else if (colString.startsWith("QUALIFY:"))
				obj.setQualifyString(colString.substring(8));
			else if (colString.equals("REQ"))
				obj.setRequired(true);
			else if (colString.startsWith("ROOT:"))
				obj.setRootName(colString.substring(5));
			else if (colString.startsWith("SYNERGY:"))
				obj.addSynergyList(colString.substring(8));
			else if (colString.startsWith("USEUNTRAINED:"))
				obj.setUntrained(colString.substring(13));
			else if (colString.startsWith(Constants.s_TAG_TYPE))
				obj.setType(colString.substring(Constants.s_TAG_TYPE.length()));
			else if (col == 1)
				obj.setKeyStat(colString);
			else if (col == 2)
				obj.setIsExclusive(colString);
			else if (col == 3)
				obj.setUntrained(colString);
			else if (col > 4)
				throw new PersistenceLayerException("Illegal skill info " + sourceFile.getName() +
					":" + Integer.toString(lineNum) + " \"" + colString + "\"");
			col++;
		}
	}
}
