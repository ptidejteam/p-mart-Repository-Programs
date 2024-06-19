/*
 * CharacterFeat.java
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
 */

/*
 * $Header: /home/cvs/cvsgelo/cvsroot/guehene-dpl-pcgen\040v3.0.0/java/pcgen/core/CharacterFeat.java,v 1.1 2006/02/21 00:02:22 vauchers Exp $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <code>CharacterFeat</code>.
 *
 * A feat that a character posesses, including any character
 * specific details. Lightweight object that links to the full
 * Feat definition.
 *
 * @author ???
 * @version $Revision: 1.1 $
 */
public class CharacterFeat
{
	// extends PObject implements Serializable

	///////////////////////////////////////
	// Fields - Attributes

	private ArrayList associatedList = new ArrayList();

	///////////////////////////////////////
	// Fields - Associations
	private Feat feat;

	///////////////////////////////////////
	// Constructors

	/**
	 * A character feat can only be constructed with a
	 * reference to the parent Feat.
	 */
	public CharacterFeat(Feat _feat)
	{
		feat = _feat;
	}

	///////////////////////////////////////
	// Methods - Accessors

	public String getAssociated(int i)
	{
		return (String)associatedList.get(i);
	}

	public int getAssociatedCount()
	{
		if (associatedList == null)
			return 0;
		return associatedList.size();
	}

	public void addAssociated(String s)
	{
		if (associatedList == null)
			associatedList = new ArrayList();
		associatedList.add(s);
	}

	public boolean containsAssociated(String s)
	{
		if (associatedList == null)
			return false;
		return associatedList.contains(s);
	}

	public boolean removeAssociated(String s)
	{
		if (associatedList == null) return false;
		return associatedList.remove(s);
	}

	// Take values from full Feat, as necessary.
	public String getName()
	{
		return feat.getName();
	}

	///////////////////////////////////////
	// Methods - Associations

	public Feat getFeat()
	{
		return feat;
	}

	///////////////////////////////////////
	// Methods - Operations

	// Take values from full Feat, as necessary.
	public String toString()
	{
		return feat.toString();
	}

	/**
	 * Name of the full Feat, plus any sub-choices made for this
	 * character. Starts with the name of the feat, and then
	 * (for types other than weapon proficiencies), either appends
	 * a count of the times the feat is applied e.g. " (3x)",
	 * or a list of the sub-choices e.g. " (Sub1, Sub2, ...)".
	 */
	public String qualifiedName()
	{
		// start with the name of the feat
		// don't do for Weapon Profs
		StringBuffer aStrBuf = new StringBuffer(feat.getName());
		if (getAssociatedCount() > 0 && !feat.getName().endsWith("Weapon Proficiency"))
		{
			// has a sub-detail
			aStrBuf.append(" (");
			int i = 0;
			if (feat.getChoiceString().length() == 0 || (feat.isMultiples() && feat.isStacks()))
			{
				// number of items only (ie stacking), e.g. " (1x)"
				aStrBuf.append(Integer.toString((int)(getAssociatedCount() * feat.getCost())));
				aStrBuf.append("x)");
			}
			else
			{
				// list of items in getAssociatedList, e.g. " (Sub1, Sub2, ...)"
				for (int e = 0; e < getAssociatedCount(); e++)
				{
					if (i > 0)
						aStrBuf.append(", ");
					aStrBuf.append(getAssociated(e));
					i++;
				}
				aStrBuf.append(')');
			}
		}

		return aStrBuf.toString();
	}


	/**
	 * Enhanced containsAssociated, which parses the input
	 * parameter for "=", "+num" and "-num" to extract the value to look
	 * for.
	 */
	public int numberInList(String aType)
	{
		if (aType.lastIndexOf("=") > -1)
			aType = aType.substring(aType.lastIndexOf("=") + 1);
		if (aType.lastIndexOf("+") > -1) // truncate at + sign if following character is a number
		{
			final String numString = "0123456789";
			final String aString = aType.substring(aType.lastIndexOf("+") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("+"));
		}
		if (aType.lastIndexOf("-") > -1) // truncate at - sign if following character is a number
		{
			final String numString = "0123456789";
			final String aString = aType.substring(aType.lastIndexOf("-") + 1);
			if (numString.lastIndexOf(aString.substring(0, 1)) > 0)
				aType = aType.substring(0, aType.lastIndexOf("-"));
		}

		int iCount = 0;
		for (int i = 0; i < getAssociatedCount(); i++)
		{
			if (getAssociated(i).equalsIgnoreCase(aType))
			{
				iCount += 1;
			}
		}
		return iCount;
	}


	/**
	 * Opens a Chooser to allow sub-choices for this feat.
	 * The actual items allowed to choose from are based on
	 * choiceString, as applied to current character. Choices
	 * already made (getAssociatedList) are indicated in list B.
	 */
	public boolean modChoices(boolean addIt)
	{
		return Utility.modChoices(feat, new ArrayList(), new ArrayList(), true);
	}


	// ??? related to getAssociatedList
	// doesn't appear to be used anywhere!
	public int addStatBonuses(String aString)
	{
		int retVal = 0;
		final PlayerCharacter aPC = Globals.getCurrentPC();
		for (int e = 0; e < getAssociatedCount(); e++)
		{
			PCClass aClass = aPC.getClassNamed(getAssociated(e));
			if (aClass != null && aClass.getSpellBaseStat().equals(aString))
				retVal++;
		}
		return retVal;
	}

	// ??? related to getAssociatedList
	// used in Skill
	public boolean hasCSkill(String aName)
	{
		if (feat.getCSkillList().contains(aName))
			return true;
		if (feat.getCSkillList().contains("LIST"))
		{
			for (int e = 0; e < getAssociatedCount(); e++)
			{
				String aString = getAssociated(e);
				if (aName.startsWith(aString) || aString.startsWith(aName))
					return true;
			}
		}
		for (Iterator e = feat.getCSkillList().iterator(); e.hasNext();)
		{
			String aString = (String)e.next();
			if (aString.lastIndexOf("%") > -1)
			{
				aString = aString.substring(0, aString.length() - 1);
				if (aName.startsWith(aString))
					return true;
			}
		}
		return false;
	}

}
