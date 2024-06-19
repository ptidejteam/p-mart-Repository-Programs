/*
 * StatList.java
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
 * Created on August 10, 2002, 11:45 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:10:50 $
 *
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>StatList</code>.
 *
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * @version $Revision: 1.1 $
 */

public final class StatList extends Object
{
	private List stats = new ArrayList();

	public List getStats()
	{
		return stats;
	}

	private int getIndexOfStatFor(String aStat)
	{
		// see if it starts with STATx where x is a number
		if (aStat.startsWith("STAT"))
		{
			final int x = Integer.parseInt(aStat.substring(4));
			if ((x < 0) || (x >= stats.size()))
			{
				return -1;
			}
			return x;
		}
		// otherwise it must be an abbreviation
		return Globals.getStatFromAbbrev(aStat);
	}

	public int getBaseStatFor(String aStat)
	{
		final int x = getIndexOfStatFor(aStat);
		if (x == -1)
		{
			return 0;
		}
		final PCStat stat = (PCStat) stats.get(x);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		int z = aPC.getVariableValue("LOCK." + stat.getAbb(), "").intValue();
		if (z != 0 || (z == 0 && aPC.hasVariable("LOCK." + stat.getAbb())))
		{
			return z;
		}
		z = aPC.getVariableValue("BASE." + stat.getAbb(), "").intValue();
		if (z != 0)
		{
			return z;
		}
		return stat.getBaseScore();
	}

	public int getTotalStatFor(String aStat)
	{
		int y = getBaseStatFor(aStat);
		int x = getIndexOfStatFor(aStat);
		if (x == -1)
		{
			return y;
		}
		final PCStat stat = (PCStat) stats.get(x);
		final PlayerCharacter aPC = Globals.getCurrentPC();
		x = aPC.getVariableValue("LOCK." + stat.getAbb(), "").intValue();
		if (x != 0 || (x == 0 && aPC.hasVariable("LOCK." + stat.getAbb())))
		{
			return x;
		}

		y += aPC.getTotalBonusTo("STAT", stat.getAbb(), true);

		return y;
	}

	public int getStatModFor(String aStat)
	{
		final int x = getIndexOfStatFor(aStat);
		if (x == -1)
		{
			return 0;
		}
		final PCStat stat = (PCStat) stats.get(x);
		return Globals.getCurrentPC().getVariableValue(stat.getStatMod(), "STAT:" + stat.getAbb()).intValue();
	}

	double getBonusTo(String type, String aName)
	{
		double bonus = 0;
		PCStat aStat;
		for (Iterator e = stats.iterator(); e.hasNext();)
		{
			aStat = (PCStat) e.next();
			bonus += aStat.bonusTo(type, aName);
		}
		return bonus;
	}

}
