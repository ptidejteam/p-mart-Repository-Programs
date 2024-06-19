/*
 * PreAlign.java
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

import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SystemCollections;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreAlign extends Prerequisite implements PrerequisiteTest {

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(Equipment equipment)
	{
		PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == null)
		{
			return false;
		}
		return passes(aPC);
	}

	public boolean passes(PlayerCharacter character)
	{

		boolean flag;

		//
		// If game mode doesn't support alignment, then pass the prereq
		//
		if (Globals.getGameModeAlignmentText().length() == 0)
		{
			return true;
		}

		String bList = getParameters();

		final int alignment = character.getAlignment();
		final String alString = String.valueOf(alignment);

		for (; ;)
		{
			// PREALIGN:[VARDEFINED=SuneLG=0],3,6,7
			int idxStart = bList.indexOf('[');
			if (idxStart < 0)
			{
				break;
			}
			int idxEnd = bList.indexOf(']', idxStart);
			if (idxEnd < 0)
			{
				break;
			}
			final String subPre = bList.substring(idxStart + 1, idxEnd);
			final StringTokenizer pTok = new StringTokenizer(subPre, "=");
			if (pTok.countTokens() != 3)
			{
				break;
			}
			final String cond = pTok.nextToken();
			final String vName = pTok.nextToken();
			final String condAlignment = pTok.nextToken();
			boolean hasCond = false;
			if ("VARDEFINED".equals(cond))
			{
				if (character.hasVariable(vName))
				{
					hasCond = true;

				}
			}
			if (hasCond)
			{
				bList = bList.substring(0, idxStart) + condAlignment + bList.substring(idxEnd + 1);
			}
			else
			{
				bList = bList.substring(0, idxStart) + bList.substring(idxEnd + 1);
			}
			if (bList.length() > 0 && bList.charAt(0) == ',')
			{
				bList = bList.substring(1);
			}
		}
		flag = (bList.lastIndexOf(alString) >= 0);
		if (!flag && (bList.lastIndexOf("10") >= 0) && (character.getDeity() != null))
		{
			flag = SystemCollections.getShortAlignmentAtIndex(character.getAlignment()).equals(character.getDeity().getAlignment());
		}
		return flag;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled()
	{
		return new String[]{"ALIGN"};
	}

}
