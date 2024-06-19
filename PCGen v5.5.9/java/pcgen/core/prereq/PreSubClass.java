/*
 * PreSubClass.java
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
import pcgen.core.*;
import pcgen.util.*;


/**
 * @author wardc
 *
 */
public class PreSubClass extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		final StringTokenizer dTok = new StringTokenizer(getParameters(), ",");
		final String tok = dTok.nextToken();
		int num = 0;
		try
		{
			num = Integer.parseInt(tok); // number we must match
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Badly formed PRESUBCLASS attribute: " + tok);
		}
		while (dTok.hasMoreTokens())
		{
			final String thisClass = dTok.nextToken();
			for (Iterator it = character.getClassList().iterator(); it.hasNext();)
			{
				final PCClass aClass = (PCClass) it.next();
				final String subClassName = aClass.getSubClassName();
				if (subClassName.length() != 0)
				{
					if (thisClass.equalsIgnoreCase(subClassName))
					{
						--num;
						break;
					}
				}
			}
		}
		
		
		return  (num <= 0);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"SUBCLASS"};
	}

}
