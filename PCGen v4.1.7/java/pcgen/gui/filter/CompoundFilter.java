/*
 * CompoundFilter.java
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
 * Created on February 23, 2002, 9:00 PM
 */
package pcgen.gui.filter;

import pcgen.core.PObject;
import pcgen.util.PropertyFactory;

/**
 * <code>CompoundFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

final class CompoundFilter extends AbstractPObjectFilter implements CustomFilter, FilterConstants
{
	private String connect;
	private PObjectFilter filter1;
	private PObjectFilter filter2;

	CompoundFilter(PObjectFilter filter1, PObjectFilter filter2, String connect)
	{
		super();
		this.connect = connect.trim().toUpperCase();
		this.filter1 = filter1;
		this.filter2 = filter2;

		setCategory(PropertyFactory.getString("in_custom"));
		setName("(" + this.filter1.getName() +
			" " + this.connect.toLowerCase() +
			" " + this.filter2.getName() +
			")");
	}

	public String toString()
	{
		return "((" + this.filter1.toString() +
			") " + this.connect.toLowerCase() +
			" (" + this.filter2.toString() +
			"))";
	}

	public boolean accept(PObject pObject)
	{
		if ("AND".equals(connect))
		{
			return filter1.accept(pObject) && filter2.accept(pObject);
		}
		else if ("OR".equals(connect))
		{
			return filter1.accept(pObject) || filter2.accept(pObject);
		}
		else
		{
			return false;
		}
	}
}
