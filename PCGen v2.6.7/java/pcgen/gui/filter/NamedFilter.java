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
 * Created on March 10, 2002, 2:20 PM
 */
package pcgen.gui.filter;

import java.util.StringTokenizer;
import pcgen.core.PObject;

/**
 * <code>NamedFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public class NamedFilter extends AbstractPObjectFilter implements CustomFilter
{
	private PObjectFilter filter;

	public NamedFilter(PObjectFilter filter,
		String name,
		String description)
	{
		super();
		this.filter = filter;

		setCategory("Custom");
		setName(name);
		setDescription(description);
	}

	public void setName(String n)
	{
		if (n.length() > 0)
		{
			super.setName(n);
		}
		else
		{
			super.setName((this.filter != null) ? this.filter.getName() : "");
		}
	}

	public void setDescription(String d)
	{
		if (d.length() > 0)
		{
			super.setDescription(normalize(d));
		}
		else
		{
			super.setDescription((this.filter != null) ? this.filter.getName() : "");
		}
	}

	public String toString()
	{
		return this.filter.toString() + "|" + getName() + "|" + getDescription();
	}

	public boolean accept(PObject pObject)
	{
		return filter.accept(pObject);
	}

	private String normalize(String s)
	{
		StringBuffer work = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(s);

		while (tokens.hasMoreTokens())
		{
			work.append(tokens.nextToken()).append(" ");
		}

		return work.toString().trim();
	}
}
