/*
 * JListModel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on October 8, 2002, 5:01 PM
 *
 * @(#) $Id: JListModel.java,v 1.1 2006/02/21 00:57:49 vauchers Exp $
 */

/**
 * <code>JListModel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 */

package pcgen.gui.editor;

import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractListModel;

final class JListModel extends AbstractListModel
{
	private ArrayList listData = null;
	private boolean sort = false;

	JListModel(ArrayList listdata, boolean argSort)
	{
		initModel(listdata, argSort);
	}

	final void setSort(boolean argSort)
	{
		sort = argSort;
	}

	private void initModel(ArrayList listdata, boolean argSort)
	{
		listData = (ArrayList) listdata.clone();

		sort = argSort;
		if (sort)
		{
			Collections.sort(listData);
		}
	}

	public int getSize()
	{
		if (listData != null)
		{
			return listData.size();
		}
		return 0;
	}

	Object[] getElements()
	{
		return listData.toArray();
	}

	public Object getElementAt(int i)
	{
		if ((listData != null) && (i < listData.size()))
		{
			return listData.get(i);
		}
		return null;
	}

	void addElement(Object obj)
	{
		listData.add(obj);
		if (sort)
		{
			Collections.sort(listData);
		}
		fireIntervalAdded(this, 0, listData.size());
	}

	boolean removeElement(Object obj)
	{
		final int idx = listData.indexOf(obj);
		listData.remove(idx);
		fireIntervalRemoved(this, idx, idx);
		return idx >= 0;
	}
}
