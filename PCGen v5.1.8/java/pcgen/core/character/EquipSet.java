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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on April 29th, 2002, 11:26 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:11:07 $
 *
 */

package pcgen.core.character;

import java.util.StringTokenizer;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.util.GuiFacade;

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

public final class EquipSet implements Comparable, Cloneable
{

	/*
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
	private String note = "";
	private Equipment eq_item = null;
	private Float qty = new Float(1);

	/**
	 * Constructor for EquipSet.
	 **/
	EquipSet()
	{
		super();
	}

	public EquipSet(String id, String aName)
	{
		id_path = id;
		name = aName;
	}

	public EquipSet(String id, String aName, String aValue, Equipment item)
	{
		id_path = id;
		name = aName;
		value = aValue;
		eq_item = item;
	}

	public int compareTo(final Object obj)
	{
		final EquipSet es = (EquipSet) obj;
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

	/**
	 * our Id is the last number on the id_path
	 * if id_path is "0.2.8.15", our id is 15
	 **/
	public int getId()
	{
		int id = 0;
		try
		{
			final StringTokenizer aTok = new StringTokenizer(id_path, ".", false);
			while (aTok.hasMoreTokens())
			{
				id = Integer.valueOf(aTok.nextToken()).intValue();
			}
		}
		catch (NullPointerException e)
		{
			//Really ignore? TODO
		}
		finally
		{
			return id;
		}
	}

	/**
	 * the Parent Id Path is everything except our Id
	 * if id_path is "0.2.8.15", our Parent Id is "0.2.8"
	 **/
	public String getParentIdPath()
	{
		final StringBuffer buf = new StringBuffer(50);

		// get all tokens and include the delimiter
		try
		{
			final StringTokenizer aTok = new StringTokenizer(id_path, ".", true);
			// get all tokens (and delimiters) except last two
			for (int i = aTok.countTokens() - 2; i > 0; i--)
			{
				buf.append(aTok.nextToken());
			}
		}
		catch (NullPointerException e)
		{
			//Really ignore? TODO
		}
		finally
		{
			return buf.toString();
		}
	}

	/**
	 * return the root id of the EquipSet
	 * If our id_path is "0.2.8.15", the root would be "0.2"
	 **/
	public String getRootIdPath()
	{
		final StringBuffer buf = new StringBuffer(50);
		final StringTokenizer aTok = new StringTokenizer(id_path, ".", false);
		if (aTok.countTokens() < 2)
		{
			return "";
		}

		// get first two tokens and delimiter
		buf.append(aTok.nextToken());
		buf.append(".");
		buf.append(aTok.nextToken());

		return buf.toString();
	}

	/**
	 * name is our EquipSet name if we are a root node
	 * or it is the name of the location for the equipment we are holding
	 **/
	public String getName()
	{
		return name;
	}

	public void setName(String x)
	{
		name = x;
	}

	/**
	 * value is null for root nodes or
	 * it is the name of the piece of equipment we are holding
	 **/
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

	public String getNote()
	{
		return note;
	}

	/**
	 * Sets the player added note to aString
	 * @param String	aString
	 **/
	public void setNote(final String aString)
	{
		note = aString;
	}

	/**
	 * Creates a duplicate of this equip set. Note that this is
	 * a deep clone - all equipment associated with this EquipSet
	 * will also be cloned.
	 *
	 * @return A new equip set, identical to this one.
	 */
	public Object clone()
	{
		EquipSet eqSet = null;

		try
		{
			eqSet = (EquipSet) super.clone();
			if (getItem() != null)
			{
				eqSet.setItem((Equipment) getItem().clone());
			}
			if (getQty() != null)
			{
				eqSet.setQty(new Float(getQty().floatValue()));
			}
		}
		catch (CloneNotSupportedException exc)
		{
			GuiFacade.showMessageDialog(null, exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}

		return eqSet;
	}
}

