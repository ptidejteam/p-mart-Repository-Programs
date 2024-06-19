/*
 * IIFNode.java
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
 * Created on November 5, 2001, 8:00 PM
 */

package pcgen.io;

import java.util.ArrayList;

/**
 * <code>IIFNode</code>.
 *
 * @author Mark Hulsman <mark_hulsman@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

class IIFNode
{
	private String expr;
	private ArrayList trueChildren;
	private ArrayList falseChildren;

	public IIFNode(String _expr)
	{
		expr = _expr;
		trueChildren = new ArrayList();
		falseChildren = new ArrayList();
	}

	public void addTrueChild(Object child)
	{
		trueChildren.add(child);
	}

	public ArrayList trueChildren()
	{
		return trueChildren;
	}

	public void addFalseChild(Object child)
	{
		falseChildren.add(child);
	}

	public ArrayList falseChildren()
	{
		return falseChildren;
	}

	public String expr()
	{
		return expr;
	}
}