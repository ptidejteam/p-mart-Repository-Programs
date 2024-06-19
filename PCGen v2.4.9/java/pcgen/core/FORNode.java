/*
 * FORNode.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 * Created on November 1, 2001, 1:00 AM
 */

package pcgen.core;

import java.util.ArrayList;

/**
 * <code>FORNode</code>.
 *
 * @author Mark Hulsman <mark_hulsman@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

class FORNode
{
	private ArrayList children;
	private String var;
	private String min;
	private String max;
	private String step;
	private boolean exists;

	public FORNode(String _var, String _min, String _max, String _step, boolean _exists)
	{
		children = new ArrayList();
		var = _var;
		min = _min;
		max = _max;
		step = _step;
		exists = _exists;
	}

	public void addChild(Object child)
	{
		children.add(child);
	}

	public ArrayList children()
	{
		return children;
	}

	public String var()
	{
		return var;
	}

	public String min()
	{
		return min;
	}

	public String max()
	{
		return max;
	}

	public String step()
	{
		return step;
	}

	public boolean exists()
	{
		return exists;
	}
}