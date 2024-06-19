/*
 * PreVision.java
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

import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.utils.Utility;
import pcgen.util.Logging;


/**
 * @author wardc
 *
 */
public class PreVision extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		int number;
		boolean flag;
		int i = getParameters().lastIndexOf('=');
		int range;
		if (i >= 0)
		{
			try
			{
				range = Integer.parseInt(getParameters().substring(i + 1));
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed passesPreVision attribute: " + getParameters().substring(i + 1));
				range = 0;
			}
		}
		else
		{
			range = 0;
			i = getParameters().length();
		}
		final StringTokenizer aTok = new StringTokenizer(getParameters().substring(0, i), ",");

		// the number of visions which must match
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Badly formed passesPreVision/number of visions attribute: " + tok);
			number = 1;
		}

		final String sString = character.getVision();
		while (aTok.hasMoreTokens() && number > 0)
		{
			String aString = aTok.nextToken();
			final StringTokenizer bTok = new StringTokenizer(aString, "(");
			bTok.nextToken(); //Should this value be thrown away?
			final StringTokenizer vTok = new StringTokenizer(sString, ",");
			while (vTok.hasMoreTokens())
			{
				String vString = vTok.nextToken();
				vString = Utility.replaceAll(vString, " ", "");
				vString = Utility.replaceAll(vString, "'", "");

				if (vString.startsWith(aString))
				{
					int wRange = 0;
					if (vString.indexOf("(") > 0)
					{
						String wString = vString.substring(vString.indexOf("(") + 1);
						wString = wString.substring(0, wString.length() - 1);
						try
						{
							wRange = Integer.parseInt(wString);
						}
						catch (NumberFormatException e)
						{
							wRange = 0;
						}
					}
					if (wRange >= range)
					{
						--number;
					}
				}
			}
		}
		flag = (number == 0);
		return flag;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"VISION"};
	}

}
