/*
 * EquipSet.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author Jayme Cox <jaymecox@netscape.net>
 * Code ripped from Bryan McRoberts <merton_monk@users.sourceforge.net>
 * Created on April 29th, 2002, 11:26 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:05:39 $
 */

package pcgen.core.character;

import java.util.StringTokenizer;
import pcgen.core.Equipment;

/*
 ******    ***   ******    ******   ****   *****
 **   **  ** **  **   **   **   ** **  ** **
 ******  **   *<code>EquipSet</code>*  ** **
 **   ** ******* **   **   **   ** **  ** **  ***
 **   ** **   ** **   **   **   ** **  ** **   **
 ******  **   ** ******    ******   ****   ******
 *
 */

/**
 * <code>EquipSet.java</code>
 * @author Jayme Cox <jaymecox@excite.com>
 * @version $Revision: 1.1 $
 */

public class EquipSet implements Comparable
{

	/*
	 *
	 * the Structure of each EQUIPSET is as follows:
	 *
	 * EQUIPSET: id_path : name : value : item
	 *
	 * id_path = a . delimited string that denotes parent/child relationship
	 * name = name of EquipSet or item this represents
			(and is used to define uniquiness for compareTo)
	 * value = Name of the Equipment stored in this item
	 * item = Equipment item stored (optional)
	 * qty = number of items this equipset contains (all same item)
	 *
	 */

	//
	// id_path for a "root" EquipSet looks like: 0.1
	// where
	// 0 == my parent (none)
	// 1 == my Id
	//
	// a Child id_path looks like this: 0.1.3
	// where
	// 0 == root
	// 1 == my parent
	// 3 == my Id
	//
	private String id_path = "";
	private String name = "";
	private String value = "";
	private Equipment eq_item = null;
	private Float qty = new Float(1);

	public EquipSet(String id, String aName)
	{
		id_path = id;
		name = aName;
	}

	public EquipSet(String id, String aName, String aValue)
	{
		id_path = id;
		name = aName;
		value = aValue;
	}

	public EquipSet(String id, String aName, String aValue, Equipment item)
	{
		id_path = id;
		name = aName;
		value = aValue;
		eq_item = item;
	}

	public EquipSet(String id, String aName, String aValue, Equipment item, Float numItems)
	{
		id_path = id;
		name = aName;
		value = aValue;
		eq_item = item;
		qty = numItems;
	}

	public int compareTo(Object obj)
	{
		EquipSet es = (EquipSet)obj;
		return this.id_path.compareTo(es.getIdPath());
	}

	public String toString()
	{
		return name;
	}

	public String getIdPath()
	{
		return id_path;
	}

	public void setIdPath(String x)
	{
		id_path = x;
	}

	// our Id is the last number on the id_path
	// if id_path is "0.2.15", our id is 15
	public int getId()
	{
		int id = 0;
		StringTokenizer aTok = new StringTokenizer(id_path, ".", false);
		while (aTok.hasMoreTokens())
			id = Integer.valueOf(aTok.nextToken()).intValue();
		return id;
	}

	// the Parent Id Path is everything except our Id
	public String getParentIdPath()
	{
		StringBuffer buf = new StringBuffer();

		// get all tokens and include the delimiter
		StringTokenizer aTok = new StringTokenizer(id_path, ".", true);

		// get all tokens (and delimiters) except last two
		for (int i = aTok.countTokens() - 2; i > 0; i--)
			buf.append(aTok.nextToken());
		return buf.toString();
	}

	// name is our EquipSet name if we are a root node
	// or it is the name of the location for the equipment we are holding
	public String getName()
	{
		return name;
	}

	public void setName(String x)
	{
		name = x;
	}

	// value is null for root nodes or
	// it is the name of the piece of equipment we are holding
	public String getValue()
	{
		return value;
	}

	public void setValue(String x)
	{
		value = x;
	}

	public Equipment getItem()
	{
		return eq_item;
	}

	public void setItem(Equipment item)
	{
		eq_item = item;
	}

	public void setQty(Float x)
	{
		qty = x;
	}

	public Float getQty()
	{
		return qty;
	}
}

