/*
 * WeaponProf.java
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

package pcgen.core;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 * <code>WeaponProf</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class WeaponProf extends PObject implements Comparable
{
	private String size = "S";
	private int hands = 1;

	public String toString()
	{
		return name;
	}

	private void setWPType(String aString)
	{
		setType(aString);
		Globals.getWeaponTypes().add(aString);
		Globals.getWeaponTypes().add(aString.toUpperCase());
	}

	/**
	 * @return the weapon's size as a one character String.
	 */
	public String getSize()
	{
		return size;
	}

	/**
	 * @return the weapon's size as an integer (from 0 and up.)
	 */
	public int sizeInt()
	{
		return Globals.sizeInt(getSize());
	}

	private void setHands(String aString)
	{
		hands = Integer.parseInt(aString);
	}

	/**
	 * @return true if the weapon is light for the current pc
	 */
	public boolean isLight()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() > sizeInt());
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the weapon can be used one handed by the current pc.
	 */
	public boolean isOneHanded()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() >= sizeInt() && hands == 1);
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the weapon gets a bonus to damage if used two-handed by the current pc
	 */
	public boolean getTwoHandedStrBonus()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() == sizeInt() && hands == 1);
	}

	/**
	 * @return true if the weapon must be used two-handed.
	 */
	public boolean isTwoHanded()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() == sizeInt() - 1 || hands == 2);
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the weapon is too large for the current pc.
	 */
	public boolean isTooLarge()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() < sizeInt() - 1);
	}

	/**
	 *
	 * @return the number of hands needed to use the weapon. Returns 3 if the weapon is neither onehanded nor twohanded.
	 */
	public int handsNeeded()
	{
		if (isOneHanded())
			return 1;
		if (isTwoHanded())
			return 2;
		else
			return 3;
	}

	private void setSize(String aString)
	{
		size = aString;
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the weapon can be used by the current pc
	 */
	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	/**
	 * Parses the weapon prof line in the lst file.
	 */
	public void parseLine(String inputLine, File sourceFile, int lineNum)
	{
		final String tabdelim = "\t";
		final StringTokenizer colToken = new StringTokenizer(inputLine, tabdelim, false);
		final int colMax = colToken.countTokens();
		int col = 0;
		if (colMax == 0)
			return;
		for (col = 0; col < colMax; col++)
		{
			String aString = (String)colToken.nextToken();
			if (super.parseTag(aString))
				continue;
			if (col == 0)
				setName(aString);
			else if (aString.startsWith(Globals.s_TAG_TYPE))
			{
				setWPType(aString.substring(Globals.s_TAG_TYPE.length()));
			}
			else if (aString.startsWith("SIZE"))
				setSize(aString.substring(5));
			else if (aString.startsWith("HANDS"))
				setHands(aString.substring(6));
			else if (aString.startsWith("DEFINE"))
				variableList.add("0|" + aString.substring(7));
			else if (aString.startsWith("PRE"))
				preReqArrayList.add(aString);
			else if (aString.startsWith("QUALIFY:"))
				addToQualifyListing(aString.substring(8));
			else
				JOptionPane.showMessageDialog
					(null, "Illegal weapon proficiency info " +
					sourceFile.getName() + ":" + Integer.toString(lineNum) +
					" \"" + aString + "\"", "PCGen", JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Compares keyName only
	 */
	public int compareTo(Object o1)
	{
		return keyName.compareTo(((WeaponProf)o1).keyName);
	}

	/**
	 * Compares keyName only
	 */
	public boolean equals(Object o1)
	{
		return keyName.equals(((WeaponProf)o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 */
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
