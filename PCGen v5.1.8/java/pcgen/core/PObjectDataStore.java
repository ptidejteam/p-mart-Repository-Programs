/*
 * PObjectDataStore.java
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
 * $Id: PObjectDataStore.java,v 1.1 2006/02/21 01:10:50 vauchers Exp $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import pcgen.util.Logging;

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
public class PObjectDataStore
{
	private String containedType;
	private TreeMap byUpperName = new TreeMap();
	private HashMap byKey = new HashMap(100);
	private HashMap byType = new HashMap(4);
	private HashSet nameSet;

	/** This should perhaps be protected, but I wanted composition to be allowed.*/
	public PObjectDataStore(String inContainedType)
	{
		containedType = inContainedType;
	}

	/** Clear the data store.*/
	public void clear()
	{
		byKey.clear();
		byType.clear();
		byUpperName.clear();
		clearVariableNameCache();
	}

	/** Retrieve by the key.  This is a caseless compare.
	 *
	 * @param aKey key to seek.  This will be compared in uppercase.
	 * @return PObject satisfying caseless .equals with the key
	 */
	public PObject getKeyed(final String aKey)
	{
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Seeking " + containedType + " keyed " + aKey);
		}
		PObject object = (PObject) byKey.get(aKey.toUpperCase());
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Found " + containedType + " " + object);
		}
		return object;
	}

	/** Add a PObject to the list.
	 *
	 * We do not report an error if duplicate names or keys are sent.  This should be checked.
	 * @param obj object to add to the list.
	 * @todo if the object already exists in the list, it should be removed.
	 */
	public void add(PObject obj)
	{
		Logging.debugPrint("Adding " + containedType + " " + obj);
		String key = obj.getKeyName().toUpperCase();
		Object was = byKey.put(key, obj);
		Logging.debugPrintIf(was != null, "Replacing " + containedType + " keyed " + key);
		String upperName = obj.getName().toUpperCase();
		was = byUpperName.put(upperName, obj);
		Logging.debugPrintIf(was != null, "Replacing " + containedType + " upper named " + upperName);
		String upperType = obj.getType().toUpperCase();
		TreeMap typedByName = (TreeMap) byType.get(upperType);
		if (typedByName == null)
		{
			typedByName = new TreeMap();
			byType.put(upperType, typedByName);
		}
		was = typedByName.put(upperName, obj);
		Logging.debugPrintIf(was != null, "Replacing " + upperType + " " + containedType + " named " + upperName);
		clearVariableNameCache();
	}

	/**
	 * Remove the object with the stated (compared without case) name.
	 *
	 * @param name name to be looked up
	 * @todo change to a caseless lookup
	 */
	public void removeNamed(String name)
	{
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Removing " + containedType + " " + name);
		}
		PObject object = getNamed(name);
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Result of find: " + object);
		}
		byUpperName.remove(object.getName().toUpperCase());
		byKey.remove(object.getKeyName().toUpperCase());
		TreeMap typedByName = (TreeMap) byType.get(object.getType().toUpperCase());
		typedByName.remove(object.getName().toUpperCase());
		clearVariableNameCache();
	}

	/**
	 * Get a sorted list of the contents.
	 *
	 * @return list of PObjects, sorted by name.
	 */
	public List getArrayCopy()
	{
		List list = new ArrayList(byUpperName.values());
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Returning array " + list);
		}
		return list;
	}

	/**
	 * Get the number of elements contained.
	 *
	 * @return the number of PObjects in the keyed lists.
	 */
	public int size()
	{
		if (Logging.isDebugMode())
		{
			Logging.debugPrint(containedType + " size: " + byUpperName.size());
		}
		return byUpperName.size();
	}

	/**
	 * Retrieve the names from the list, with optional delimiters.
	 *
	 * @param delim the delimiter to seperate the items
	 * @param addArrayMarkers if true, will add [ and ] to the output
	 * @return objects in name order
	 */
	public String getNames(String delim, boolean addArrayMarkers)
	{
		StringBuffer ret = new StringBuffer();
		boolean first = true;
		if (addArrayMarkers)
		{
			ret.append("[");
		}
		for (Iterator ii = byUpperName.values().iterator(); ii.hasNext();)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				ret.append(delim);
			}

			PObject object = (PObject) ii.next();
			ret.append(object.getName());
		}
		if (addArrayMarkers)
		{
			ret.append("]");
		}
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("" + containedType + "s: " + ret.toString());
		}
		return ret.toString();
	}

	/**
	 * Add any items to this array which are not already there.
	 *
	 * @param dest array to be augmented by the WeaponProfs
	 */
	public void addUniqueAsStringTo(List dest)
	{
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Adding " + containedType + "s to: " + dest.toString());
		}
		for (Iterator ii = byUpperName.values().iterator(); ii.hasNext();)
		{
			PObject object = (PObject) ii.next();
			if (!dest.contains(object.toString()))
			{
				dest.add(object.toString());
			}
		}
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Now equals: " + dest.toString());
		}
		clearVariableNameCache();
	}

	/**
	 * Get every entry for a given type.
	 *
	 * @param type value for the type field to be sought out.  Caseless compare.
	 * @return Collection of all values from the list that meet the requirements.
	 * @todo convert to an unmodifiable collection, so that we do not need the extra array copy.
	 */
	public Collection getAllOfType(final String type)
	{
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Getting all " + containedType + "s of type: " + type);
		}

		String upperType = type.toUpperCase();
		List list = null;

		TreeMap typedByName = (TreeMap) byType.get(upperType);
		if (typedByName != null)
		{
			list = new ArrayList(typedByName.values());
		}

		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Returning: " + list);
		}
		return list;
	}

	/**
	 * Find the variable with the requested name
	 *
	 * @param name Name to seek.  Compare is caseless.
	 * @return PObject meeting this requirement.
	 */
	public PObject getNamed(final String name)
	{
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Seeking " + containedType + " : " + name);
		}
		String upperName = name.toUpperCase();
		PObject object = (PObject) byUpperName.get(upperName);
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Returning: " + object);
		}
		return object;
	}

	/**
	 * Return true if any PObject in this list whose name is
	 * in collectionOfNames has a variable with the desired name
	 *
	 * @param collectionOfNames collection of names to seek in the list
	 * @param variableName variable to seek in the list
	 * @return true if any PObject in this list whose name is
	 *		in collectionOfNames has a variable with the desired name
	 */
	public boolean hasVariableNamed(Collection collectionOfNames, String variableName)
	{
		if (collectionOfNames.isEmpty())
		{
			return false;
		}

		// See if _any_ variable in this list has the requested variable before checking the specific list
		if (!hasVariableNamed(variableName))
		{
			return false;
		}

		for (Iterator e = collectionOfNames.iterator(); e.hasNext();)
		{
			final String wpName = (String) e.next();
			final PObject obj = getNamed(wpName);
			if (obj == null)
			{
				if (Logging.isDebugMode())
				{
					Logging.debugPrint("No PObject for " + containedType + " " + wpName);
				}
				continue;
			}
			if (obj.hasVariableNamed(variableName))
			{
				return true;
			}
		}
		return false;
	}

	private boolean hasVariableNamed(String variableName)
	{
		String upperName = variableName.toUpperCase();
		if (nameSet == null)
		{
			cacheVariableNames();
		}
		return nameSet.contains(upperName);
	}

	private final void clearVariableNameCache()
	{
		nameSet = null;
	}

	private final void cacheVariableNames()
	{
		nameSet = new HashSet();
		for (Iterator i = byUpperName.values().iterator(); i.hasNext();)
		{
			PObject object = (PObject) i.next();
			Set variableNames = object.getVariableNamesAsUnmodifiableSet();
			if (variableNames != null)
			{
				nameSet.addAll(variableNames);
			}
		}
		if (Logging.isDebugMode())
		{
			Logging.debugPrint("Final variable name cache: " + nameSet);
		}
	}
}
