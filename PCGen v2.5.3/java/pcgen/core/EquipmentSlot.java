/*
 *  EquipmentSlot.java
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
 *  <code>EquipmentSlot</code>.
 *
 *@author     Mark Hulsman
 *@created    January 27, 2002
 *@version    $Revision: 1.1 $
 */

public class EquipmentSlot
{
	private String name = "";
	private ArrayList children = new ArrayList();

	public EquipmentSlot(String argName)
	{
		name = argName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String argName)
	{
		name = argName;
	}

	public ArrayList getChildren()
	{
		return children;
	}

	public void addChild(Equipment argEquipment, int argQuantity)
	{
		children.add(new EquippedItem(argEquipment, argQuantity));
	}

	public void remove(String argName, int argQuantity)
	{
		for (int x = 0; x < children.size(); x++)
		{
			if (argName.equals(((EquippedItem)children.get(x)).getEquipment().getName()))
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
		float weight = 0;
		for (int x = 0; x < children.size(); x++)
			weight += ((EquippedItem)children.get(x)).totalWeight();
		return weight;
	}
}