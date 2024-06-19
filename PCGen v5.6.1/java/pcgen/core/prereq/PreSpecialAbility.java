/*
 * PreSpecialAbility.java
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

import pcgen.core.*;
import java.util.*;
import pcgen.util.*;

/**
 * @author wardc
 *
 */
public class PreSpecialAbility
	extends Prerequisite
	implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		int number;
		final boolean flag;
		setParameters(getParameters().toUpperCase());

		StringTokenizer aTok = new StringTokenizer(getParameters(), ",");

		// wrap this in a try catch to make sure
		// that the first Token is a number
		try
		{
			number = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException exceptn)
		{
			Logging.errorPrint("Exception in PRESA:" + getParameters() + Constants.s_LINE_SEP + "Assuming 1 required", exceptn);
			number = 1;
			aTok = new StringTokenizer(getParameters(), ",");
		}

		while (aTok.hasMoreTokens())
		{

			final String aString = aTok.nextToken();
			boolean bFound = false;
			if (!character.getSpecialAbilityList().isEmpty())
			{
				for (Iterator e1 = character.getSpecialAbilityList().iterator(); e1.hasNext();)
				{
					//final String e1String = ((SpecialAbility)e1.next()).getName();
					final Object obj = e1.next();
					String e1String = ((SpecialAbility) obj).getName();
					e1String = e1String.toUpperCase();
					if (e1String.startsWith(aString))
					{
						--number;
						bFound = true;
						break;
					}
				}
			}

			//
			// Now check any templates
			//
			if (!bFound)
			{
				if (!character.getTemplateList().isEmpty())
				{
					for (Iterator e1 = character.getTemplateList().iterator(); e1.hasNext();)
					{

						final PCTemplate aTempl = (PCTemplate) e1.next();
						final List SAs = aTempl.getSpecialAbilityList(character.getTotalLevels(), character.totalHitDice());

						if (SAs != null)
						{
							for (Iterator e2 = SAs.iterator(); e2.hasNext();)
							{
								final Object obj = e2.next();
								String e1String;
								if (obj instanceof String)
								{
									e1String = (String) obj;
								}
								else
								{
									e1String = ((SpecialAbility) obj).getName();
								}
								e1String = e1String.toUpperCase();
								if (e1String.startsWith(aString))
								{
									--number;
									break;
								}
							}
						}
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
		return new String[] {"SA"};
	}

}
