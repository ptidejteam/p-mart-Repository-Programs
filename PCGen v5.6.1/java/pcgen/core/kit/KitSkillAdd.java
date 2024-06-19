/*
 * KitSkillAdd.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on September 23, 2002, 8:55 PM
 */

package pcgen.core.kit;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;

/**
 * <code>KitSkillAdd</code>.
 *
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */
public final class KitSkillAdd//implements java.io.Serializable
{
	private Skill aSkill = null;
	private double maxRank = 0.0;
	private double curRank = 0.0;
	private double ttlMod = 0.0;
	private int cost;
	private boolean modified = false;
	private boolean free = false;

	public KitSkillAdd(Skill argSkill, double argMaxRank, int argCost, boolean argFree)
	{
		aSkill = argSkill;
		maxRank = argMaxRank;
		cost = argCost;
		free = argFree;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		final Skill pcSkill = aPC.getSkillNamed(aSkill.getName());
		if (pcSkill != null)
		{
			curRank = pcSkill.getRank().doubleValue();
		}
		if (free)
		{
			ttlMod = maxRank - curRank;
			modified = true;
		}
	}

	public Skill getSkill()
	{
		return aSkill;
	}

	public boolean wasModified()
	{
		return modified;
	}

	public boolean addRank()
	{
		boolean result = false;
		if ((curRank + ttlMod) < maxRank)
		{
			ttlMod += 1.0 / cost;
			modified = true;
			result = true;
		}
		return result;
	}

	public double getDelta()
	{
		return ttlMod;
	}

}
