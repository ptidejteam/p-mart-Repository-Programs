/*
 * NoteItem.java
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
 * Created on March 17, 2002, 9:27 PM
 *
 * $Id: NoteItem.java,v 1.1 2006/02/21 01:18:39 vauchers Exp $
 */

package pcgen.core;

/**
 * <code>NoteItem</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class NoteItem
{
	private int id_value = -1;
	private int id_parent = -1;
	private String name = "";
	private String value = "";

	/**
	 * Constructor for NoteItem.
	 * use the public constructor instead
	 */
	private NoteItem()
	{
		super();
	}

	public NoteItem(int my_id, int my_parent, String aName, String aValue)
	{
		id_value = my_id;
		id_parent = my_parent;
		name = aName;
		value = aValue;
	}

	public String toString()
	{
		return name;
	}

	/* this is used to export to character sheets
	 * e.g. getExportString("<b>","</b>,"<br>,"")
	 * would return the name in bold and the value on the next line in html format
	 */
	public String getExportString(String beforeName, String afterName, String beforeValue, String afterValue)
	{
		return beforeName + name + afterName + beforeValue + value + afterValue;
	}

	public int getId()
	{
		return id_value;
	}

	public void setIdValue(int x)
	{
		id_value = x;
	}

	public int getParentId()
	{
		return id_parent;
	}

	public void setParentId(int x)
	{
		id_parent = x;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String x)
	{
		name = x;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String x)
	{
		value = x;
	}
}
