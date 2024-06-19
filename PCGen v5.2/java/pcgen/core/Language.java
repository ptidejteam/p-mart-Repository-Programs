/*
 * Language.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on November 18, 2001, 9:15 PM
 */

package pcgen.core;

/**
 * <code>Language</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public final class Language extends PObject implements Comparable
{
	/**
	 * Compares keyName only
	 */
	public int compareTo(Object o1)
	{
		if (o1 instanceof String)
		{
			return keyName.compareTo((String) o1);
		}
		return keyName.compareTo(((Language) o1).keyName);
	}

	/**
	 * Compares keyName only
	 */
	public boolean equals(Object o1)
	{
		if (o1 instanceof String)
		{
			return keyName.equals(o1);
		}
		return keyName.equals(((Language) o1).keyName);
	}

	/**
	 * Hashcode of the keyName
	 */
	public int hashCode()
	{
		return keyName.hashCode();
	}
}
