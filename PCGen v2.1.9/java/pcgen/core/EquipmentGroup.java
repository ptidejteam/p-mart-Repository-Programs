/*
 * EquipmentCollection.java
 * Copyright 2001 (C) Thomas G. W. Epperly
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
 * Created on April 7, 2001, 12:14 PM
 */

package pcgen.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An implementation of the equipment collection interface.
 */

public class EquipmentGroup implements EquipmentCollection, Serializable
{
	private String d_name = "";
	private ArrayList d_equipment = new ArrayList(0);
	private EquipmentCollection d_parent = null;
	private Class d_class = Object.class;


	public EquipmentGroup()
	{
		// no action required
	}

	public EquipmentGroup(String name)
	{
		d_name = name;
	}

	public EquipmentGroup(String name, Class containsClass)
	{
		d_name = name;
		d_class = containsClass;
	}

	public final String getName()
	{
		return d_name;
	}

	public final void setName(String name)
	{
		d_name = name;
	}

	public int getChildCount()
	{
		return d_equipment.size();
	}

	public Object getChild(int i)
	{
		return d_equipment.get(i);
	}

	public void removeChild(int i)
	{
		d_equipment.remove(i);
	}

	public int indexOfChild(Object child)
	{
		return d_equipment.indexOf(child);
	}

	public void setParent(EquipmentCollection parent)
	{
		d_parent = parent;
	}

	public EquipmentCollection getParent()
	{
		return d_parent;
	}

	public void insertChild(Object child)
	{
		d_equipment.add(child);
	}

	public boolean acceptsChildren()
	{
		return true;
	}

	public int canContain(Object o)
	{
		if (d_class.isInstance(o))
			return 1;
		return 0;
	}

	public boolean canHold(HashMap properties)
	{
		return true;
	}

	public void updateProperties(HashMap properties, boolean additive)
	{
		// no action
	}

	public Class getGroupClass()
	{
		return d_class;
	}

	public String toString()
	{
		return d_name;
	}

	public boolean equals(Object o)
	{
		return
			(o instanceof EquipmentGroup) &&
			d_name.equals(((EquipmentGroup)o).getName());
	}

	public int hashCode()
	{
		return d_name.hashCode();
	}
}
