/*
 * PreEquipped.java
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
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreEquipped extends Prerequisite {
	/**
	 * Process the tokens and return the number that is not passed.
	 *
	 * @param aTok         The tokenizer to use
	 * @param aPC          The pc to use.
	 * @param equippedType The equipped type to look for (e.g. Equipment.EQUIPPED_TWO_HANDS)
	 * @param number
	 * @return
	 */
	public int passesPreEquipHandleTokens(PlayerCharacter character, final int equippedType)
	{
		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		int number;
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed PREEQUIPSECONDARY attribute: " + tok);
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
						if (eq.getLocation() == equippedType)
						{
							--number;
							break;
						}
					}
				}
				else
				{ //not a TYPE string
					if (aString.indexOf('%') >= 0) //handle wildcards (always assume they end the line)
					{
						if ((eq.getName().startsWith(aString.substring(0, aString.indexOf('%')))) && (eq.isEquipped()))
						{
							if (eq.getLocation() == equippedType)
							{
								--number;
								break;
							}
						}
					}
					else if ((eq.getName().equalsIgnoreCase(aString)) && (eq.isEquipped())) //just a straight String compare
					{
						if (eq.getLocation() == equippedType)
						{
							--number;
							break;
						}
					}
				}
			}
			if (number == 0)
			{
				break;
			}
		}
		return number;
	}
	
}
