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
 * Last Edited: $Date: 2006/02/20 23:57:43 $
 */

package pcgen.core.character;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.spell.Spell;

/**
 * <code>PCClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class CharacterSpell implements Comparable
{
	private PObject owner = null; // either a PCClass or Domain
	private PObject owningClass = null; // either a PCClass or Domain
	private int times = 1;
	private int maxTimes = 0;
	private String spellBook = "";
	private List featList = null; //Lazy initialization. Far rarer than non-feated spells.
	private int featListSize = 0; //To help the lazy initialization stay lazy.
	private int level = 0;
	private int adjustedLevel = 0; // taking feats into account
	private Spell spell = null;

	///////////////////////////////////////////////////////////////////////
	// Constructor(s)
	///////////////////////////////////////////////////////////////////////
	public CharacterSpell(PObject theOwner, PObject theClass, Spell aSpell)
	{
		super();
		owner = theOwner;
		owningClass = theClass;
		spell = aSpell;
		spellBook = Globals.getDefaultSpellBook();
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
		String rv = spell.getName();
		if (featListSize > 0)
		{
			StringBuffer aBuf = new StringBuffer(rv);
			aBuf = aBuf.append(" [");
			for (int i = 0; i < featListSize; i++)
			{
				if (i > 0)
					aBuf = aBuf.append(",");
				aBuf = aBuf.append(getFeatList().get(i).toString());
			}
			rv = aBuf.append("]").toString();
		}
		return rv;
	}

	/**
	 * Returns the name of the spell for this Character Spell
	 **/
	public String getName()
	{
		if (spell == null)
			return "";
		else
			return spell.getName();
	}

	private String ownerString()
	{
		if (owner == null)
			return toString();
		return new StringBuffer(spell.getName()).append(".").append(owner.getName()).toString();
	}

	public int compareTo(Object obj)
	{
		// this should throw a ClassCastException for non-CharacterSpell
		// just like the Comparable interface calls for
		// BUT IT DOESN'T!!!
		return ownerString().compareTo(((CharacterSpell)obj).ownerString());
	}

	/**
	 * returns true if
	 * obj.getName() equals this.getName()
	 * or obj == this
	 **/
	public boolean equals(Object obj)
	{
		return (obj != null) && (obj instanceof CharacterSpell) &&
			((CharacterSpell)obj).getName().equals(getName()) &&
			((CharacterSpell)obj).getSpellBook().equals(spellBook) &&
			((CharacterSpell)obj).getAdjustedLevel() == adjustedLevel;
	}

	/**
	 * this method is used the same as equals() but for hash tables
	 **/
	public int hashCode()
	{
		return getName().hashCode();
	}


	/**
	 * Returns the owner of the spell
	 * The owner is either the {@link pcgen.core.Domain Domain} or
	 * the {@link pcgen.core.PCClass PCClass} to which the spell is tied
	 **/
	public PObject getOwner()
	{
		return owner;
	}

	public void setOwner(PObject a)
	{
		owner = a;
	}

	/** Returns the owner class of the spell.  In the Global list this may be a either the
	 * {@link pcgen.core.Domain Domain}
	 * or the {@link pcgen.core.PCClass PCClass}.
	 * In a PC list this should always be a {@link pcgen.core.PCClass PCClass} that the spell
	 * is associated with.
	 */
	public PObject getOwningClass()
	{
		return owningClass;
	}

	public void setOwningClass(PObject aClass)
	{
		owningClass = aClass;
	}

	public String getDescriptor(String delimiter)
	{
		String d = spell.getDescriptor(delimiter);
		if (!(owner instanceof Domain))
			return d;
		if (d.length() > 0)
			return owner.getName() + delimiter + d;
		return owner.getName();
	}

	public int getTimes()
	{
		return times;
	}

	public void adjustTimes(int adjustment)
	{
		times += adjustment;
		// now check to make sure this adjustment is valid, blah blah
	}

	public void setTimes(int time)
	{
		times = time;
		// now check to make sure this adjustment is valid, blah blah
	}

	public int getMaxTimes()
	{
		return maxTimes;
	}

	public void setMaxTimes(int a)
	{
		maxTimes = a;
	}

	public String getSpellBook()
	{
		return spellBook;
	}

	public void setSpellBook(String book)
	{
		spellBook = book;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int l)
	{
		level = l;
		calcAdjustedLevel();
	}

	public int getAdjustedLevel()
	{
		return adjustedLevel;
	}

	public void setAdjustedLevel(int l)
	{
		adjustedLevel = l;
	}

	public Spell getSpell()
	{
		return spell;
	}

	public void setSpell(Spell aSpell)
	{
		spell = aSpell;
	}

	public ArrayList getFeatList()
	{
		if (featList == null)
		{
			featList = new ArrayList();
		}
		return (ArrayList)featList;
	}

	public void setFeatList(ArrayList a)
	{
		featList = a;
		featListSize = a.size();
		calcAdjustedLevel();
	}


	public boolean isInSpecialty()
	{
		if (owner == null || owningClass == null)
			return false;
		PCClass aClass = null;
//		if ((owningClass instanceof PCClass) && (adjustedLevel>0 || ((PCClass)owningClass).getSpecialtyKnownList().size()>0))
		if ((owningClass instanceof PCClass))
			aClass = (PCClass)owningClass;
		if (((owner instanceof Domain) && aClass != null && aClass.getSpellType().equalsIgnoreCase("DIVINE")) ||
			(aClass != null && aClass.getSpecialtyList().contains(spell.getSchool())) ||
			(aClass != null && aClass.getSpecialtyList().contains(spell.getSubschool())))
			return true;
		for (Iterator i = spell.getDescriptorList().iterator(); i.hasNext();)
		{
			final String aString = (String)i.next();
			if (aClass != null && aClass.getSpecialtyList().contains(aString))
				return true;
		}
		return false;
	}

	///////////////////////////////////////////////////////////////////////
	// Private method(s)
	///////////////////////////////////////////////////////////////////////
	private void calcAdjustedLevel()
	{
		adjustedLevel = level;
		if (featListSize > 0)
		{
			for (Iterator i = featList.iterator(); i.hasNext();)
			{
				Feat aFeat = (Feat)i.next();
				adjustedLevel += aFeat.getAddSpellLevel();
			}
		}
	}
}
