/*
 * PreWeaponProficiency.java
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

import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.WeaponProf;
import pcgen.core.utils.Utility;
import pcgen.util.Logging;


/**
 * @author wardc
 *
 */
public class PreWeaponProficiency
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		boolean flag;

		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		flag = false;
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
					numberRequired = aTok.countTokens() + 1; // I've already taken one out
					Logging.errorPrint("Using \"" + getParameters() + "\" old PREWEAPONPROF syntax. New syntax is PREWEAPONPROF:#,wp1,wp2,...,wpn");
					Logging.errorPrint("Will use default for numberRequired: " + numberRequired);
				}
			}
			final boolean hasIt = !aString.startsWith("[");
			if (!hasIt)
			{
				aString = aString.substring(1, Math.max(aString.length() - 1, aString.lastIndexOf(']')));
			}
			if ("DEITYWEAPON".equals(aString) && character.getDeity() != null)
			{
				for (Iterator weapIter = Utility.split(character.getDeity().getFavoredWeapon(), '|').iterator(); !flag && weapIter.hasNext();)
				{
					flag = character.hasWeaponProfNamed((String) weapIter.next());
				}
			}
			else if (aString.startsWith("TYPE.") || aString.startsWith("TYPE="))
			{
				flag = false; // With TYPE, we do not control by the flag, only by numberRequired
				for (Iterator e = character.getWeaponProfList().iterator(); e.hasNext();)
				{
					String profName = (String) e.next();
					WeaponProf wp = Globals.getWeaponProfNamed(profName);
					if (wp == null)
					{
						continue;
					}
					if (wp.isType(aString.substring(5)))
					{
						--numberRequired;
					}
					else
					{
						Equipment eq = EquipmentList.getEquipmentNamed(profName);
						if (eq != null)
						{
							if (eq.isType(aString.substring(5)))
							{
								--numberRequired;
							}
						}
					}
				}
			}
			else
			{
				flag = character.hasWeaponProfNamed(aString);
			}
			if (!hasIt)
			{
				flag = !flag;
			}
			if (flag)
			{
				--numberRequired;
			}
			if (numberRequired <= 0)
			{
				return true;
			}
		}
		return false;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"WEAPONPROF"};
	}

}
