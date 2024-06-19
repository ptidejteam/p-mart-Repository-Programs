/*
 * KitSpells.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 9:29 PM
 *
 * $Id: KitSpells.java,v 1.1 2006/02/21 01:10:52 vauchers Exp $
 */

package pcgen.core.kit;

import java.io.Serializable;
import java.util.ArrayList;
import pcgen.core.utils.Utility;

/**
 * <code>KitSchool</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.1 $
 */
public final class KitSpells extends BaseKit implements Serializable
{
	private final ArrayList spellList = new ArrayList();
	private String countFormula = "";
	private static final long serialVersionUID = 1;

	public KitSpells()
	{
	}

	public String toString()
	{
		final int maxSize = spellList.size();
		final StringBuffer info = new StringBuffer(maxSize * 10);
		if (countFormula.length() != 0)
		{
			info.append(countFormula).append(" of ");
		}
		info.append(Utility.joinToStringBuffer(spellList, ", "));
		return info.toString();
	}

	public void addSpell(String argSpell)
	{
		if (!spellList.contains(argSpell))
		{
			spellList.add(argSpell);
		}
	}

	public ArrayList getSpellList()
	{
		return spellList;
	}

	public void setCountFormula(String argCountFormula)
	{
		countFormula = argCountFormula;
	}

	public String getCountFormula()
	{
		return countFormula;
	}

}
