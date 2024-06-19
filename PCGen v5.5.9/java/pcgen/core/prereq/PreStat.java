/*
 * PreStat.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 * Copyright 2003 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	   See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:27:55 $
 *
 */
package pcgen.core.prereq;

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreStat extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character)
	{
		StringTokenizer aTok = new StringTokenizer(getParameters(), ",|");
		String aString = aTok.nextToken();
		int matchesNeeded = 1;

		try
		{
			matchesNeeded = Integer.parseInt(aString);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreStat attribute: " + aString);
		}

		String compType = getKind().substring(4);

		if (compType.length() == 0)
		{
			compType = "GTEQ";
		}

		while (aTok.hasMoreTokens())
		{
			aString = aTok.nextToken();
			if (aString.length() < 3)
			{
				Logging.errorPrint("Badly formed PRESTAT token: " + getParameters());
				return false;
			}
			try
			{
				final int iStat = character.getStatList().getTotalStatFor(aString.substring(0, 3));
				final int iVal = Integer.parseInt(aString.substring(aString.lastIndexOf('=') + 1));
				if (PrereqHandler.doComparison(compType, iStat, iVal))
				{
					if (--matchesNeeded <= 0)
					{
						return true;
					}
				}
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed PRESTAT token: " + e);
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[] {"STAT", "STATEQ", "STATGT", "STATGTEQ", "STATLT", "STATLTEQ", "STATNEQ"};
	}

}
