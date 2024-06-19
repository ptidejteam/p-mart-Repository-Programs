/*
 * CharacterSpell.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:00:39 $
 */

package pcgen.core.character;

import java.util.ArrayList;
import java.util.Iterator;
import pcgen.core.Domain;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class CharacterSpell implements Comparable
{
	private Spell spell = null;
	private final ArrayList infoList = new ArrayList();
	private PObject owner; // PCClass/Race/etc. in whose list this object resides

	///////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////
	public CharacterSpell(PObject o, Spell aSpell)
	{
		owner = o;
		spell = aSpell;
	}

	///////////////////////////////////////////////////////////////////////
	// Accessor(s) and Mutator(s)
	///////////////////////////////////////////////////////////////////////

	/*
	 * Returns the Spell's Name for Tree's display
	 */
	public String toString()
	{
		if (spell == null)
		{
			return "";
		}
		return spell.getName();
	}

	public int compareTo(Object obj)
	{
		// this should throw a ClassCastException for non-CharacterSpell
		// just like the Comparable interface calls for
		// BUT IT DOESN'T!!!
		return spell.compareTo(((CharacterSpell) obj).getSpell());
	}

	/** * returns true if
	 * obj.getName() equals this.getName()
	 * or obj == this
	 **/
	public boolean equals(Object obj)
	{
		return (obj != null) && (obj instanceof CharacterSpell) && ((CharacterSpell) obj).getName().equals(toString());
	}

	/**
	 * this method is used the same as equals() but for hash tables
	 **/
	public int hashCode()
	{
		return toString().hashCode();
	}

	public PObject getOwner()
	{
		return owner;
	}

	/**
	 * Returns the name of the spell for this Character Spell
	 **/
	private String getName()
	{
		if (spell == null)
		{
			return "";
		}
		else
		{
			return spell.getName();
		}
	}

	public Spell getSpell()
	{
		return spell;
	}

	public SpellInfo addInfo(final int level, final int times, final String book)
	{
		return addInfo(level, times, book, null);
	}

	public SpellInfo addInfo(final int level, final int times, final String book, final ArrayList featList)
	{
		final SpellInfo si = new SpellInfo(this, level, times, book);
		if (featList != null)
		{
			si.addFeatsToList(featList);
		}
		infoList.add(si);
		return si;
	}

	public void removeSpellInfo(SpellInfo x)
	{
		if (x != null)
		{
			infoList.remove(x);
		}
	}

	public ArrayList getInfoList()
	{
		return infoList;
	}

	/**
	 * bookName = name of spellbook/list
	 * level = actual level of spell (adjusted by feats)
	 * specialty: -1 = inSpecialty insensitive
	 * specialty: 0 = inSpecialty==false
	 * specialty: 1 = inSpecialty==true
	 * Returns index of SpellInfo in infoList, or -1 if it doesn't exist
	 **/
	public int getInfoIndexFor(String bookName, int level, int specialty)
	{
		if (infoList.isEmpty())
		{
			return -1;
		}
		boolean sp = (specialty == 1);
		if (sp && (specialty != -1))
		{
			sp = isSpecialtySpell();
		}
		int i = 0;
		final Iterator x = infoList.iterator();
		while (x.hasNext())
		{
			final SpellInfo s = (SpellInfo) x.next();
			if ((("").equals(bookName) || bookName.equals(s.getBook())) && (level == -1 || s.getActualLevel() == level) && (specialty == -1 || sp))
			{
				return i;
			}
			i++;
		}
		return -1;
	}

	public SpellInfo getSpellInfoFor(String bookName, int level, int specialty)
	{
		return getSpellInfoFor(bookName, level, specialty, null);
	}

	public SpellInfo getSpellInfoFor(String bookName, int level, int specialty, ArrayList featList)
	{
		if (infoList.isEmpty())
		{
			return null;
		}
		boolean sp = (specialty == 1);
		if (sp && specialty != -1)
		{
			sp = isSpecialtySpell();
		}
		final Iterator x = infoList.iterator();
		while (x.hasNext())
		{
			final SpellInfo s = (SpellInfo) x.next();
			if ((("").equals(bookName) || bookName.equals(s.getBook())) && (level == -1 || s.getActualLevel() == level) && (specialty == -1 || sp) && (featList == null || (featList.isEmpty() && (s.getFeatList() == null || s.getFeatList().isEmpty())) || featList.toString().equals(s.getFeatList().toString())))
			{
				return s;
			}
		}
		return null;
	}

	public boolean isSpecialtySpell()
	{
		if (spell == null)
		{
			return false;
		}
		if (owner instanceof Domain)
		{
			return true;
		}
		if (owner instanceof PCClass)
		{
			final PCClass a = (PCClass) owner;
			return a.isSpecialtySpell(spell);
		}
		return false;
	}

}
