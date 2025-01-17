/*
 * ArmorProf.java
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

/**
 * <code>ArmorProf</code>.
 *
 * @author Thomas Clegg <arknight@swbell.net>
 * @version
 */
public class ArmorProf extends PObject implements Comparable
{
	private String size = "S";

	public String toString()
	{
		return name;
	}

	public void setAPType(String aString)
	{
		setType(aString);
		Globals.getArmorTypes().add(aString);
		Globals.getArmorTypes().add(aString.toUpperCase());
	}

	/**
	 * @return the armor's size as a one character String.
	 */
	private String getSize()
	{
		return size;
	}

	/**
	 * @return the armor's size as an integer (from 0 and up.)
	 */
	private int sizeInt()
	{
		return Globals.sizeInt(getSize());
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the armor is too large for the current pc.
	 */
	public boolean isApTooLarge()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() < sizeInt() - 1);
	}

	public void setSize(String aString)
	{
		size = aString;
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the armor can be used by the current pc
	 */
	public boolean meetsPreReqs()
	{
		return passesPreReqTests();
	}

	/**
	 * Compares keyName only
	 */
	public int compareTo(Object o1)
	{
		return keyName.compareTo(((ArmorProf)o1).keyName);
	}

	/**
	 * Compares keyName only
	 */
	public boolean equals(Object o1)
	{
		return keyName.equals(((ArmorProf)o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 */
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
