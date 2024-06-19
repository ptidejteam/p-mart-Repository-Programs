/*
 * SpellInfo.java
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
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * Created on July 10, 2002, 11:26 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:08:02 $
 */

package pcgen.core.character;

import java.util.ArrayList;
import java.util.List;
import pcgen.core.Globals;

/**
 * <code>SpellInfo</code>
 * this is a helper-class for CharacterSpell
 * meant to contain the book, whether or not this spell
 * is in the specialtySlot for characters which have them,
 * and the list of meta-magic feats which have been applied.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class SpellInfo
{
	private String book = Globals.getDefaultSpellBook(); // name of book
	private int actualLevel = -1;
	private List featList = null; // a List of Feat objects
	private int times = 0; // times the spell is in this list
	private CharacterSpell owner = null;

	//added package-private constructor to enforce usage of public constructor
	SpellInfo()
	{
		super();
	}

	SpellInfo(final CharacterSpell o, final int l, final int t, final String b)
	{
		owner = o;
		actualLevel = l;
		times = t;

		//
		// use the default book
		//
		if (b != null)
		{
			book = b;
		}
	}

	public String toString()
	{
		if (featList == null || featList.isEmpty())
		{
			return "";
		}
		final StringBuffer aBuf = new StringBuffer(" [" + featList.get(0).toString());
		for (int i = 1; i < featList.size(); i++)
		{
			aBuf.append(", ").append(featList.get(i).toString());
		}
		aBuf.append("] ");
		return aBuf.toString();
	}

	public CharacterSpell getOwner()
	{
		return owner;
	}

	public String getBook()
	{
		return book;
	}

	public int getActualLevel()
	{
		return actualLevel;
	}

	public List getFeatList()
	{
		return featList;
	}

	public void addFeatsToList(List aList)
	{
		if (featList == null)
		{
			featList = new ArrayList();
		}
		featList.addAll(aList);
	}

	public int getTimes()
	{
		return times;
	}

	public void setTimes(final int a)
	{
		times = a;
	}
}

