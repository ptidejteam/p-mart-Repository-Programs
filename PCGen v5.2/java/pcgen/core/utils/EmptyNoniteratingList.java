/*
 *  Equipment.java
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
 */

package pcgen.core.utils;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A List subclass containing no data that returns a singleton iterator
 *
 * @author Scott Ellsworth
 * @version $Revision: 1.1 $
 */
public class EmptyNoniteratingList extends AbstractList implements Serializable
{
	public static final List EMPTY_LIST = new EmptyNoniteratingList();

	private EmptyNoniteratingList()
	{
	}

	private static final long serialVersionUID = 8842843931221139166L;

	public int size()
	{
		return 0;
	}

	public boolean contains(Object obj)
	{
		return false;
	}

	public Object get(int index)
	{
		throw new NoSuchElementException("Index: " + index);
	}

	public Iterator iterator()
	{
		return EmptyIterator.EMPTY_ITERATOR;
	}
}
