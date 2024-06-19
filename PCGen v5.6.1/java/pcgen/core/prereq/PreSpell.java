/*
 * PreSpell.java
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

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.CharacterDomain;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreSpell extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {

		int number = 0;
		final boolean flag;

		// e.g. PRESPELL:3,
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed PreSpell attribute: " + tok);
		}

		final List aArrayList = character.aggregateSpellList("Any", "", "", 0, 20);

		//Needs to add domain spells as well
		for (Iterator domains = character.getCharacterDomainList().iterator(); domains.hasNext();)
		{
			CharacterDomain aCD = (CharacterDomain) domains.next();
			if ((aCD != null) && (aCD.getDomain() != null))
			{
				aArrayList.addAll(Globals.getSpellsIn(-1, "", aCD.getDomain().toString()));
			}
		}

		while (aTok.hasMoreTokens())
		{

			String bString = aTok.nextToken();
			if (!aArrayList.isEmpty())
			{
				for (Iterator e1 = aArrayList.iterator(); e1.hasNext();)
				{

					final Spell aSpell = (Spell) e1.next();
					if (aSpell.getName().equalsIgnoreCase(bString))
					{
						--number;
						break;
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		flag = (number <= 0);
		return flag;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"SPELL"};
	}

}
