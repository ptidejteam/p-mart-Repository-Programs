/*
 * PreShieldProficiency.java
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

import pcgen.core.*;
import java.util.*;
import pcgen.util.*;

/**
 * @author wardc
 *
 */
public class PreShieldProficiency
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		int numberRequired = -1;
		while (aTok.hasMoreTokens())
		{
			String aString = aTok.nextToken();
			if (numberRequired == -1)
			{
				try
				{
					numberRequired = Integer.parseInt(aString);
					aString = aTok.nextToken();
				}
				catch (NumberFormatException nfe)
				{
					numberRequired = aTok.countTokens() + 1;
					Logging.errorPrint("Wrong PRESHIELDPROF syntax: " + getParameters());
					Logging.errorPrint("Correct syntax is: PRESHIELDPROF:#,sp1,sp2,...,spn");
					Logging.errorPrint("Will use default for numberRequired: " + numberRequired);
				}
			}
			for (Iterator e = character.getShieldProfList().iterator(); e.hasNext();)
			{
				String profName = (String) e.next();
				if (profName.equalsIgnoreCase(aString))
				{
					--numberRequired;
				}
				else if (profName.substring(5).equalsIgnoreCase(aString.substring(5)))
				{
					--numberRequired;
				}
			}
			if (numberRequired <= 0)
			{
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"SHIELDPROF"};
	}

}
