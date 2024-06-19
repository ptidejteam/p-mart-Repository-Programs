/*
 * PreSkill.java
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
 */package pcgen.core.prereq;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.util.Logging;

/**
 * @author wardc
 *
 */
public class PreSkill extends Prerequisite implements PrerequisiteTest {


	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#passes(pcgen.core.PlayerCharacter)
	 */
	public boolean passes(PlayerCharacter character) {
		int number;
		int i = getParameters().lastIndexOf('=');
		int ranks;
		if (i >= 0)
		{
			try
			{
				ranks = Integer.parseInt(getParameters().substring(i + 1));
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Badly formed passesPreSkill attribute: " + getParameters().substring(i + 1));
				ranks = 0;
			}
		}
		else
		{
			Logging.errorPrint("passesPreSkill: bad prereq \"" + getParameters() + "\"");
			return false;
		}

		final StringTokenizer aTok = new StringTokenizer(getParameters().substring(0, i), ",");

		// the number of skills which must match
		String tok = aTok.nextToken();
		try
		{
			number = Integer.parseInt(tok);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Badly formed passesPreSkill/number of skills attribute: " + tok);
			number = 1;
		}

		final List sList = (ArrayList) character.getSkillList().clone();
		final List tList = new ArrayList();
		while (aTok.hasMoreTokens() && (number > 0))
		{
			String aString = aTok.nextToken().toUpperCase();
			int percentageSignPosition = -1;

			String skillName;
			final boolean isType = (aString.startsWith("TYPE.") || aString.startsWith("TYPE="));
			if (isType)
			{
				aString = aString.substring(5).toUpperCase();
			}
			skillName = aString.toUpperCase();
			percentageSignPosition = skillName.lastIndexOf('%');

			Skill aSkill = null;
			for (Iterator e1 = sList.iterator(); e1.hasNext();)
			{
				if (number <= 0)
				{

					break;
				}

				aSkill = (Skill) e1.next();

				String aSkillName = aSkill.getName().toUpperCase();
				if (isType)
				{
					if ((aSkill.getTotalRank().intValue() < ranks) || tList.contains(aSkillName))
					{
						aSkill = null;
						continue;
					}
					if (percentageSignPosition >= 0)
					{
						final int maxCount = aSkill.getMyTypeCount();
						int k;
						for (k = 0; k < maxCount; ++k)
						{
							if (aSkill.getMyType(k).startsWith(skillName.substring(0, percentageSignPosition)))
							{
								break;
							}
						}
						if (k < maxCount)
						{
							break;
						}
					}
					else if (aSkill.isType(skillName))
					{
						break;
					}
				}
				else if (aSkillName.equals(skillName) ||
						((percentageSignPosition >= 0) && aSkillName.startsWith(skillName.substring(0, percentageSignPosition))))
				{
					if ((aSkill.getTotalRank().intValue() < ranks) || tList.contains(aSkillName))
					{
						aSkill = null;
						continue;
					}
					else if ((percentageSignPosition >= 0) && aSkillName.startsWith(skillName.substring(0, percentageSignPosition)))
					{
						break;
					}
					else if (aSkillName.equals(skillName))
					{
						break;
					}
				}
				aSkill = null;
			}
			if (aSkill != null)
			{
				sList.remove(aSkill);
				tList.add(aSkill.getName());
				--number;
			}
		}
		return (number <= 0);	}

	/* (non-Javadoc)
	 * @see pcgen.core.prereq.PrerequisiteTest#kindsHandled()
	 */
	public String[] kindsHandled() {
		return new String[]{"SKILL", "SKILLMULT"};
	}

}
