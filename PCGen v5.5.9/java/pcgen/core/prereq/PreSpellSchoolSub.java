/*
 * PreSpellSchoolSub.java
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

import pcgen.core.PlayerCharacter;
import java.util.*;
import pcgen.util.*;

/**
 * @author wardc
 *
 */
public class PreSpellSchoolSub
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		int number;
		final boolean flag;

		// e.g. PRESPELLSCHOOLSUB:Shadow,7,1
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		if (aTok.countTokens() < 3)
		{
			Logging.errorPrint("Badly formed PreSpellSchoolSub tag: " + getParameters());
			return false;
		}

		final String subSchool = aTok.nextToken();
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed PreSpellSchoolSub attribute: " + tok);
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
			Logging.errorPrint("Badly formed PreSpellSchoolSub attribute: " + tok);
			minlevel = 0;
		}
		final List aArrayList = character.aggregateSpellList("Any", "No-Match", subSchool, minlevel, 20);
		flag = (aArrayList.size() >= number);
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"SPELLSCHOOLSUB"};
	}

}
