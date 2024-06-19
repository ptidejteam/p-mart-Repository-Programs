/*
 * Denomination.java
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

/**
 * Denomination class represents a specific denomination of currency.
 *
 * @author  Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.1 $
 */
package pcgen.core;

public class Denomination implements Comparable
{
	public String name;
	public String abbr;
	public int factor;
	public float weight;

	/*
   * For future expansion of capabilities involving regions.
   * The regions should be it's own object, so that various
   * entities can use it.
   */
	//private Region region;

	/**
	 * Class constructor
	 *
	 * @param name    the name of the denomination
	 * @param abbr    the abbreviation for the denomination
	 * @param factor  the factor that describes this denominations's
	 *                relationship to other denominations
	 * @param weight  the coin's weight
	 */
	public Denomination(String name, String abbr, int factor, float weight)
	{
		this.name = name;
		this.abbr = abbr;
		this.factor = factor;
		this.weight = weight;
	}

	/**
	 * Compares this object to the passed one for equality of the name.
	 *
	 * @param d   An object of type denomination.
	 *
	 * @return    True if the object's names are equal.
	 */
	public boolean equals(Denomination d)
	{
		return (name.equals(d.name));
	}

	/**
	 * Required, as part of the Comparable interface, for sorting
	 * these objects.  Note that this method implements a
	 * descending sort on the factor for denomination objects.
	 *
	 * @param d  an object of type Denomination
	 *
	 * @return   1 if the passed object's factor is less than this
	 *           object's factor, 0 if they are equal and -1 if the
	 *           passed object's factor is greater than this one.
	 */
	public int compareTo(Object d)
	{

		int result = 0;

		if (this.factor > ((Denomination)d).factor)
			result = -1;
		else if (this.factor == ((Denomination)d).factor)
			result = 0;
		else
			result = 1;

		return result;
	}

	public String toString()
	{
		return name + "/" + abbr + "/" + factor + "/" + weight;
	}
}
