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

/**
 * <code>WeaponProf</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class WeaponProf extends PObject implements Comparable
{
	private String size = "S";
	private int hands = 1;

	public String toString()
	{
		return name;
	}

	public void setWPType(String aString)
	{
		setType(aString);
		Globals.addWeaponType(aString);
		Globals.addWeaponType(aString.toUpperCase());
	}

	/**
	 * @return the weapon's size as a one character String.
	 */
	private String getSize()
	{
		return size;
	}

	/**
	 * @return the weapon's size as an integer (from 0 and up.)
	 */
	private int sizeInt()
	{
		return Globals.sizeInt(getSize());
	}

	public void setHands(String aString)
	{
		hands = Integer.parseInt(aString);
	}

	int getHands()
	{
		return hands;
	}

	/**
	 * @return true if the weapon is light for the current pc
	 */
	boolean isWpLight()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() > sizeInt());
	}

	/**
	 * Should probably be changed to take a pc as a parameter.
	 * @return true if the weapon can be used one handed by the current pc.
	 */
	boolean isWpOneHanded()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && pc.sizeInt() >= sizeInt() && hands == 1);
	}

	/**
	 * @return true if the weapon must be used two-handed.
	 */
	boolean isWpTwoHanded()
	{
		final PlayerCharacter pc = Globals.getCurrentPC();
		return (pc != null && (pc.sizeInt() == sizeInt() - 1)) || hands == 2;
	}

	public void setSize(String aString)
	{
		size = aString;
	}

	/**
	 * Compares keyName only
	 */
	public int compareTo(Object o1)
	{
		return keyName.compareTo(((WeaponProf) o1).keyName);
	}

	/**
	 * Compares keyName only
	 */
	public boolean equals(Object o1)
	{
		return keyName.equals(((WeaponProf) o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 */
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
