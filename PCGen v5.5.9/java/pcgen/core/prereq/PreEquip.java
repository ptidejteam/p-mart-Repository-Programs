/*
 * PreEquip.java
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
public class PreEquip extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		boolean flag;
		int number;

		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed PREEQUIP attribute: " + tok);
			number = 0;
		}
		while (aTok.hasMoreTokens())
		{
			if (character.getEquipmentList().isEmpty())
			{
				break;
			}

			String aString = aTok.nextToken();
			for (Iterator e1 = character.getEquipmentList().iterator(); e1.hasNext();)
			{
				Equipment eq = (Equipment) e1.next();
				if (aString.startsWith("TYPE=") || aString.startsWith("TYPE."))
				{
					if ((eq.getType().indexOf(aString.substring(5).toUpperCase()) >= 0) && eq.isEquipped())
					{
						--number;
						break;
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							--number;
							break;
						}
					}
					else if ((eq.getName().equalsIgnoreCase(aString)) && (eq.isEquipped())) //just a straight String compare
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
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"EQUIP"};
	}

}
