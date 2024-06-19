/*
 * PreLanguage.java
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
import pcgen.util.*;
import java.util.*;

/**
 * @author wardc
 *
 */
public class PreLanguage extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		int number;
		final boolean flag;

		final StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		String tok = aTok.nextToken();
		int storedValue;
		try
		{
			storedValue = Integer.parseInt(tok);
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed PRELANG attribute: " + tok);
			storedValue = 0;
		}
		number = storedValue;
		while (aTok.hasMoreTokens() && number > 0)
		{

			String aString = aTok.nextToken();
			Language aLang = Globals.getLanguageNamed(aString);
			if (aLang != null)
			{
				if (character.getLanguagesList().contains(aLang))
				{
					--number;
				}
			}
			else if (!aString.equals("ANY"))
			{
				Logging.errorPrint("PRELANG: The language " + aString + " does not exist.");
			}
		}
		if (getParameters().lastIndexOf("ANY") >= 0)
		{
			flag = storedValue <= character.getLanguagesList().size();
		}
		else
		{
			flag = (number == 0);
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"LANG"};
	}

}
