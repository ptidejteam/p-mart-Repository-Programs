/*
 * PreSpellCast.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;


/**
 * @author wardc
 *
 */
public class PreSpellCast extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		final List classList = (ArrayList) character.getClassList().clone();
		PCClass aClass;
		boolean flag = false;
		while (aTok.hasMoreTokens())
		{

			String aString = aTok.nextToken();
			if (aString.startsWith("MEMORIZE"))
			{
				if (!classList.isEmpty())
				{
					for (Iterator e1 = classList.iterator(); e1.hasNext();)
					{
						aClass = (PCClass) e1.next();
						if ((aClass.getMemorizeSpells() && aString.endsWith("N")) || (!aClass.getMemorizeSpells() && aString.endsWith("Y")))
						{
							e1.remove();
						}
					}
				}
			}
			else if (aString.startsWith("TYPE"))
			{
				if (!classList.isEmpty())
				{
					for (Iterator e1 = classList.iterator(); e1.hasNext();)
					{
						aClass = (PCClass) e1.next();
						if (aString.substring(5).lastIndexOf(aClass.getSpellType()) < 0)
						{
							e1.remove();
						}
					}
				}
			}
			flag = classList.size() > 0;
			if (!flag)
			{
				break;
			}
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"SPELLCAST"};
	}

}
