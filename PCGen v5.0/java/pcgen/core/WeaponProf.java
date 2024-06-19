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
	private int hands = 1;

	static final int HANDS_SIZEDEPENDENT = -1;

	protected void doGlobalUpdate(final String aString)
	{
		Globals.addWeaponType(aString);
		//Globals.addWeaponType(aString.toUpperCase());
	}

	//public void setWPType(String aString)
	//{
	//	setTypeInfo(aString);
	//}

	public void setHands(String aString)
	{
		if (aString.equals("1IFLARGERTHANWEAPON"))
		{
			hands = HANDS_SIZEDEPENDENT;
		}
		else
		{
			hands = Integer.parseInt(aString);
		}
	}

	int getHands()
	{
		return hands;
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
