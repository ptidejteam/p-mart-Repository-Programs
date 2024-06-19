/*
 * WeaponProfDataStore.java
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
 * $Id: WeaponProfDataStore.java,v 1.1 2006/02/21 01:16:13 vauchers Exp $
 */
package pcgen.core;

import java.util.Collection;
import java.util.List;

/**
 * Stores WeaponProfs in ways that make it easy to retrieve by name, caseless name
 * key, and type.  It assumes that the name, key, and type will not change - if
 * this assumption is violated, we will need an API to change the data store.<p>
 *
 * Consider PropertyChangeListener as an API for notifications.
 *
 * @author ???
 * @version $Revision: 1.1 $
 *
 */
public class WeaponProfDataStore
{
	private PObjectDataStore store = new PObjectDataStore("WeaponProf");

	public final void clear()
	{
		store.clear();
	}

	public final WeaponProf getKeyed(final String aKey)
	{
		return (WeaponProf) store.getKeyed(aKey);
	}

	public final void add(PObject wp)
	{
		store.add(wp);
	}

	public final void removeNamed(String name)
	{
		store.removeNamed(name);
	}

	public final List getArrayCopy()
	{
		return store.getArrayCopy();
	}

	public final int size()
	{
		return store.size();
	}

	public final String getNames(String delim, boolean addArrayMarkers)
	{
		return store.getNames(delim, addArrayMarkers);
	}

	public final void addUniqueAsStringTo(List dest)
	{
		store.addUniqueAsStringTo(dest);
	}

	public final Collection getAllOfType(final String type)
	{
		return store.getAllOfType(type);
	}

	/**
	 *  searches for an exact name match
	 */
	public final WeaponProf getNamed(final String name)
	{
		return (WeaponProf) store.getNamed(name);
	}

	public final boolean hasVariableNamed(Collection collectionOfNames, String variableString)
	{
		return store.hasVariableNamed(collectionOfNames, variableString);
	}
}
