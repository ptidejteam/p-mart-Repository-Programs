/*
 *  VariableList.java
 *  Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author Scott Ellsworth
 */

package pcgen.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * ???
 * @author Scott Ellsworth
 * @version $Revision: 1.1 $
 */


public class VariableList implements Cloneable
{
	private ArrayList list = new ArrayList();
	private List unmodifiableList = Collections.unmodifiableList(list);

	private HashSet nameSet = null;

	public Object clone() throws CloneNotSupportedException
	{
		VariableList retVal = (VariableList) super.clone();
		retVal.list = (ArrayList) list.clone();
		return retVal;
	}

	public final String getDefinition(int i)
	{
		Variable v = (Variable) list.get(i);
		return v.getDefinition();
	}
/* 	public final Variable get(int i) */
/* 	{ */
/* 		return (Variable)list.get(i); */
/* 	} */
	public final void set(int idx, String str)
	{
		Variable v = new Variable(str);
		list.set(idx, v);
		clearNameCache();
	}

	protected final void addAll(VariableList vOther)
	{
		list.addAll(vOther.list);
		clearNameCache();
	}

	public final int size()
	{
		return list.size();
	}

	public final void add(String str)
	{
		Variable v = new Variable(str);
		list.add(v);
		clearNameCache();
	}

	public final Set getVariableNamesAsUnmodifiableSet()
	{
		if (nameSet == null)
		{
			cacheNames();
		}
		return Collections.unmodifiableSet(nameSet);
	}

	public final boolean hasVariableNamed(String variableName)
	{
		String upperName = variableName.toUpperCase();
		if (nameSet == null)
		{
			cacheNames();
		}
		return nameSet.contains(upperName);
	}

	public final Iterator iterator()
	{
		return unmodifiableList.iterator();
	}

	private final void clearNameCache()
	{
		nameSet = null;
	}

	private final void cacheNames()
	{
		nameSet = new HashSet();
		for (Iterator i = list.iterator(); i.hasNext();)
		{
			Variable v = (Variable) i.next();
			nameSet.add(v.getUpperName());
		}
	}
}
