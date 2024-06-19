/*
 *  EquippedItem.java
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
 *  Created on April 21, 2001, 2:15 PM
 */
package pcgen.core;

import java.util.ArrayList;

/**
 *  <code>EquippedItem</code>.
 *
 *@author     Mark Hulsman
 *@created    January 27, 2002
 *@version    $Revision: 1.1 $
 */
public class EquippedItem
{
	private ArrayList children = new ArrayList();
	private Equipment equipment = null;
	private int quantity = 0;

	public EquippedItem(Equipment e, int q)
	{
		equipment = e;
		quantity = q;
	}

	public Equipment getEquipment()
	{
		return equipment;
	}

	public void setEquipment(Equipment e)
	{
		equipment = e;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int q)
	{
		quantity = q;
	}

	public ArrayList getChildren()
	{
		return children;
	}

	public void addChild(EquippedItem item)
	{
		children.add(item);
		sortChildren();
	}

	private void sortChildren()
	{
		ArrayList newChildren = new ArrayList();
		int first = 0;
		while (children.size() > 0)
		{
			for (int x = 1; x < children.size(); x++)
				if (((EquippedItem)children.get(x)).getEquipment().getName().compareTo(((EquippedItem)children.get(first)).getEquipment().getName()) < 0)
					first = x;
			newChildren.add(children.get(first));
			children.remove(first);
		}
		children = newChildren;
	}

	public void remove(String name, int argQuantity)
	{
		for (int x = 0; x < children.size(); x++)
		{
			if (name.equals(((EquippedItem)children.get(x)).getEquipment().getName()))
			{
				((EquippedItem)children.get(x)).setQuantity(((EquippedItem)children.get(x)).getQuantity() - argQuantity);
				if (((EquippedItem)children.get(x)).getQuantity() <= 0)
					children.remove(x);
				return;
			}
		}
	}

	public float totalWeight()
	{
		float weight = equipment.getWeight().floatValue() * quantity;
		for (int x = 0; x < children.size(); x++)
			weight += ((EquippedItem)children.get(x)).totalWeight();
		return weight;
	}
}
