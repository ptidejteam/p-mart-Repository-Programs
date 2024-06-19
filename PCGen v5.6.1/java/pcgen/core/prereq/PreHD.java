/*
 * PreHD.java
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
import pcgen.util.Logging;
/**
 * @author wardc
 *
 */
public class PreHD extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {

		boolean flag;

		/*
		 * either PREHD:xxx+ or PREHD:xxx-yyy
		 * with xxx being the minimum requirement
		 * and yyy being the maximum requirement
		 *
		 * author: Thomas Behr 13-03-02
		 */
		final StringTokenizer aTok = new StringTokenizer(getParameters(), "+-");
		String tok = aTok.nextToken();
		int preHDMin;
		try
		{
			preHDMin = (aTok.hasMoreTokens()) ? Integer.parseInt(tok) : Integer.MIN_VALUE;
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreHD attribute: " + tok);
			preHDMin = 1;
		}

		tok = aTok.nextToken();
		int preHDMax;
		try
		{
			preHDMax = (aTok.hasMoreTokens()) ? Integer.parseInt(tok) : Integer.MAX_VALUE;
		}
		catch (NumberFormatException e)
		{
			Logging.errorPrint("Badly formed passesPreHDMax attribute: " + tok);
			preHDMax = 1;
		}

		final int hitDice = character.totalHitDice();
		flag = (hitDice >= preHDMin) && (hitDice <= preHDMax);
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[] {"HD"};
	}

}
