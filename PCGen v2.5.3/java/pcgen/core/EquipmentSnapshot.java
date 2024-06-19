/*
 *  EquipmentSnapshot.java
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
 *  <code>EquipmentSnapshot</code>.
 *
 *@author     Mark Hulsman
 *@created    January 27, 2002
 *@version    $Revision: 1.1 $
 */

public class EquipmentSnapshot
{
	private String name = "";
	private ArrayList slots = new ArrayList();

	public EquipmentSnapshot(String n)
	{
		name = n;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String n)
	{
		name = n;
	}

	public ArrayList getSlots()
	{
		return slots;
	}

	public void addSlot(EquipmentSlot slot)
	{
		slots.add(slot);
	}

	public float totalWeight()
	{
		float weight = 0;
		for (int x = 0; x < slots.size(); x++)
			weight += ((EquipmentSlot)slots.get(x)).totalWeight();
		return weight;
	}
}