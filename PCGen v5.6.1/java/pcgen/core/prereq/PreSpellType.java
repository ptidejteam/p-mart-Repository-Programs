/*
 * PreSpellType.java
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
 * Last Edited: $Date: 2006/02/21 01:33:20 $
 *
 */
package pcgen.core.prereq;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreSpellType extends Prerequisite implements PrerequisiteTest {

	/**
	 * Method checks to see if the current character meets the
	 * prerequisite number of spells at the required level and also
	 * checks to see if the character can actually cast it.
	 * <p/>
	 * PRESPELLTYPE:<type>{|<type2>},<number>,<min level>
	 * <p/>
	 * If two types are passes in then only one type is needed
	 * to return as true.
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		if (aTok.countTokens() < 3)
		{
			Logging.errorPrint("Badly formed PRESPELLTYPE: " + getParameters());
			return false;
		}
		final String typeList = aTok.nextToken();
		String tok = aTok.nextToken();
		int number;
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreSpellType/number attribute: " + tok);
			number = 0;
		}
		tok = aTok.nextToken();
		int minlevel;
		try
		{
			minlevel = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreSpellType/minLevel attribute: " + tok);
			minlevel = 0;
		}

		final StringTokenizer bTok = new StringTokenizer(typeList, "|");

		// Go through types
		while (bTok.hasMoreTokens())
		{
			String castingType = bTok.nextToken();

			// Perform quick check to see if the character
			// has required spell level in their spell book
			List aArrayList = character.aggregateSpellList(castingType, "", "", minlevel, 20);
			if (aArrayList.size() >= number)
			{
				// Make sure character can actually
				// cast spells of this level
				if (character.canCastSpellTypeLevel(castingType, minlevel, number))
				{
					return true;
				}
			}
		}
		return false;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"SPELLTYPE"};
	}

}
