/*
 * Skill.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 13, 2002, 9:19 AM
 *
 * @(#) $Id: Skill.java,v 1.1 2006/02/21 01:10:54 vauchers Exp $
 */

/**
 * <code>Skill</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.core.bonus;

import pcgen.core.Globals;
import pcgen.core.PCStat;

final class Skill extends BonusObj
{
	private static final String[] bonusTags = {"LIST"};

	Skill()
	{
		super();
	}

	private static class MissingSkill
	{
		String skillName = "";

		MissingSkill(final String argSkillName)
		{
			skillName = argSkillName;
		}
	}

	boolean parseToken(String token)
	{
		for (int i = 0; i < bonusTags.length; ++i)
		{
			if (bonusTags[i].equals(token))
			{
				addBonusInfo(new Integer(i));
				return true;
			}
		}
		if (token.startsWith("STAT="))
		{
			final int iStat = Globals.getStatFromAbbrev(token.substring(5));
			if (iStat >= 0)
			{
				addBonusInfo(Globals.getStatList().get(iStat));
				return true;
			}
		}
		else if (token.startsWith("TYPE="))
		{
			addBonusInfo(token.substring(5));
			return true;
		}
		final pcgen.core.Skill aSkill = Globals.getSkillNamed(token);
		if (aSkill != null)
		{
			addBonusInfo(aSkill);
		}
		else
		{
			addBonusInfo(new MissingSkill(token));
		}
		return true;
	}

	String unparseToken(Object obj)
	{
		if (obj instanceof Integer)
		{
			return bonusTags[((Integer) obj).intValue()];
		}
		else if (obj instanceof String)
		{
			return "TYPE=" + obj.toString();
		}
		else if (obj instanceof PCStat)
		{
			return "STAT=" + ((PCStat) obj).getAbb();
		}
		if (obj instanceof MissingSkill)
		{
			return ((MissingSkill) obj).skillName;
		}
		return ((pcgen.core.Skill) obj).getName();
	}
}

