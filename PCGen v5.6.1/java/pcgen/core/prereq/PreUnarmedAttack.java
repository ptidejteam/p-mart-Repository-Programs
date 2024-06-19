/*
 * PreUnarmedAttack.java
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
import java.util.StringTokenizer;

import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreUnarmedAttack
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		final boolean flag;
		int requiredValue;
		try
		{
			requiredValue = Integer.parseInt(getParameters());
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreUAtt attribute: " + getParameters());
			requiredValue = 0;
		}
		int att = 0;
		if (!character.getClassList().isEmpty())
		{
			for (Iterator e2 = character.getClassList().iterator(); e2.hasNext();)
			{

				final PCClass aClass = (PCClass) e2.next();
				String s = aClass.getUattForLevel(aClass.getLevel());
				if (s.length() == 0 || "0".equals(s))
				{
					att = Math.max(att, aClass.baseAttackBonus());
				}
				else
				{

					final StringTokenizer bTok = new StringTokenizer(s, ",");
					s = bTok.nextToken();
					try
					{
						att = Math.max(att, Integer.parseInt(s));
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Badly formed passesPreUAtt attribute: " + s);
					}
				}
			}
		}
		flag = att >= requiredValue;
		return flag;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"UATT"};
	}

}
