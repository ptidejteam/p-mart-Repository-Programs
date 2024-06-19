/*
 * ClassBonus.java
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
 * Created on October 18, 2002, 12:32 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:00:21 $
 *
 */

package pcgen.core.bonus;

import java.io.Serializable;

/**
 * <code>ClassBonus</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
class ClassBonus implements Serializable
{
	private int level = 0;
	private BonusObj bonus = null;

	public ClassBonus(final int argLevel, final String bonusString)
	{
		level = argLevel;
		setBonus(bonusString);
	}

	public ClassBonus(final int argLevel, final Object argBonus)
	{
		level = argLevel;
		bonus = (BonusObj) argBonus;
	}

	public String toString()
	{
		return Integer.toString(level) + ((bonus != null) ? "|" + bonus.toString() : "");
	}

	public void setLevel(final int argLevel)
	{
		level = argLevel;
	}

	public int getLevel()
	{
		return level;
	}

	private void setBonus(final String bonusString)
	{
		bonus = Bonus.newBonus(bonusString);
	}

	public BonusObj getBonus()
	{
		return bonus;
	}
}
