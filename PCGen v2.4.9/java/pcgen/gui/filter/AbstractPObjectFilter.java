/*
 * AbstractPObjectFilter.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

import pcgen.core.PObject;

/**
 * <code>AbstractPObjectFilter</code>
 * Abstract PObject filter class.<br>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public abstract class AbstractPObjectFilter implements PObjectFilter
{
	private String category;
	private String name;
	private String description;

	public AbstractPObjectFilter()
	{
		this("", "", "");
	}

	public AbstractPObjectFilter(String argName)
	{
		this("", argName, "");
	}

	public AbstractPObjectFilter(String argCategory, String argName)
	{
		this(argCategory, argName, "");
	}

	public AbstractPObjectFilter(String argCategory, String argName, String argDescription)
	{
		setCategory(argCategory);
		setName(argName);
		setDescription(argDescription);
	}

	public String getCategory()
	{
		return category;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String d)
	{
		description = d;
	}

	public String toString()
	{
		return (category.length() > 0)
			? getCategory() + SEPARATOR + getName()
			: getName();
	}


	public boolean equals(Object object)
	{
		if (object instanceof PObjectFilter)
		{
			return equals((PObjectFilter)object);
		}
		return super.equals(object);
	}

	public boolean equals(PObjectFilter filter)
	{
		return filter.toString().equals(toString());
	}

	public abstract boolean accept(PObject pObject);

	protected void setCategory(String c)
	{
		category = c;
	}

	protected void setName(String n)
	{
		name = n;
	}
}
