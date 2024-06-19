/*
 * PreMult.java
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
public class PreMult extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		StringTokenizer aTok = new StringTokenizer(getParameters(), ",");
		int qtdMult;
		try
		{
			qtdMult = Integer.parseInt(aTok.nextToken());
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Incorrect PREMULT parameter. Assuming 1");
			qtdMult = 1;
		}
		int aIndex = getParameters().indexOf(",");
		if (aIndex < 0)
		{
			Logging.errorPrint("PREMULT formatted incorrectly.");
			return false;
		}
		String aList = getParameters().substring(aIndex + 1);
		List preList = new ArrayList();
		int qtdPreList = 1;
		for (int i = 0, nesting = 0, start = 0; i < aList.length(); i++)
		{
			if (aList.charAt(i) == '[')
			{
				++nesting;
			}
			else if (aList.charAt(i) == ']')
			{
				--nesting;
			}
			if (nesting == 0 && aList.charAt(i) == ',')
			{
				++qtdPreList;
				preList.add(aList.substring(start, i));
				start = i + 1;
			}
			if (nesting == 0 && i == aList.length() - 1)
			{
				preList.add(aList.substring(start));
			}
			if (nesting < 0 || (nesting > 0 && i == aList.length() - 1))
			{
				Logging.errorPrint("PREMULT Incorrect [] nesting.");
				return false;
			}
		}
		for (Iterator i = preList.iterator(); i.hasNext();)
		{
			String aString = (String) i.next();
			aString = aString.substring(1, aString.length() - 1);
			final List argList = new ArrayList();
			argList.add(aString);
			if (PrereqHandler.passesPreReqToGainForList(null, character, null, argList))
			{
				--qtdMult;
			}
			--qtdPreList;
			if (qtdMult == 0)
			{
				return true;
			}
			if (qtdMult > qtdPreList)
			{
				return false;
			}
		}
		return false;	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"MULT"};
	}

}
