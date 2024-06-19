/*
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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:02:22 $
 */

package pcgen.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** This class is a wrapper around a simple HashMap which will contain
 *  keys that are Strings and values that are ints.  Most methods are named
 *  similarly to methods in HashMap, functionality is nearly as you would
 *  expect.  Differences are noted.
 *
 * @author ???
 * @version $Revision: 1.1 $
 */
public class ArmorClassBonusMap
{
	private Map acMap = new HashMap();

	/**
	 *  clears the underlying HashMap
	 */
	public void clear()
	{
		acMap.clear();
	}

	/**
	 * @param toGet the key to retrieve
	 * @return the int value associated with the key, if the key
	 *   doesn't exist, 0 is returned
	 */
	public int get(String toGet)
	{
		if (acMap.get(toGet) == null)
		{
			return 0;
		}
		return ((Integer)acMap.get(toGet)).intValue();
	}

	/**
	 * @param toSet The key to set
	 * @param valueToSet The Integer value to set, if null new Integer(0) will be set
	 */
	public void put(String toSet, Integer valueToSet)
	{
		if (valueToSet == null)
		{
			valueToSet = new Integer(0);
		}
		acMap.put(toSet, valueToSet);
	}

	/**
	 * @param toSet The key to set
	 * @param valueToSet The int value to set
	 */
	public void put(String toSet, int valueToSet)
	{
		put(toSet, new Integer(valueToSet));
	}

	/**
	 * Note that since get always returns a value
	 * this method will insert a new key into the set
	 * with the value(valueToAdd) if the key doesn't
	 * already exist
	 *
	 * @param toModify The key to modify
	 * @param valueToAdd The int value to add
	 */
	public void add(String toModify, int valueToAdd)
	{
		int valueToSet = get(toModify) + valueToAdd;
		put(toModify, valueToSet);
	}

	/**
	 * Note that since get always returns a value
	 * this method will insert a new key into the set
	 * with the value(valueToAdd) if the key doesn't
	 * already exist
	 *
	 * @param toModify The key to modify
	 * @param valueToAdd The Integer value to add
	 */
	public void add(String toModify, Integer valueToAdd)
	{
		int intValueToAdd = (valueToAdd == null) ? 0 : valueToAdd.intValue();
		add(toModify, intValueToAdd);
	}

	/**
	 * Returns the underlying keySet
	 *
	 * @return The Set of keys
	 */
	public Set keySet()
	{
		return acMap.keySet();
	}

	/**
	 * @param key The key to check
	 * @return true if the map contains the key, false otherwise
	 */
	public boolean containsKey(String key)
	{
		return acMap.containsKey(key);
	}

	/**
	 * Removes the key from the Map
	 *
	 * @param key the key to remove
	 * @return The int value of the key removed.  If the key didn't exist
	 *     then 0 is returned
	 */
	public int remove(String key)
	{
		int toReturn = get(key);
		acMap.remove(key);
		return toReturn;
	}
}
