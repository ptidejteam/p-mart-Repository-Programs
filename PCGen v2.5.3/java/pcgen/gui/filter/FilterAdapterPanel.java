/*
 * FilterAdapterPanel.java
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import pcgen.core.PObject;

/**
 * <code>FilterAdapterPanel</code>
 *
 * @author Thomas Behr
 * @version $Revision: 1.1 $
 */

public abstract class FilterAdapterPanel extends JPanel implements Filterable, FilterConstants
{
	private ArrayList availableFilters = new ArrayList(0);
	private ArrayList removedFilters = new ArrayList(0);
	private ArrayList selectedFilters = new ArrayList(0);

	private int filterMode = MATCH_ALL;

	/**
	 * convenience method<br>
	 * adds a filter to the list of available filters for this Filterable
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param filter   the filter to be registered
	 */
	public void registerFilter(PObjectFilter filter)
	{
		if (filter != null)
		{
			availableFilters.add(filter);
		}
	}

	/**
	 * Selector<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return a list with the available filters for this Filterable
	 */
	public List getAvailableFilters()
	{
		return availableFilters;
	}

	/**
	 * Selector
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return a list with the removed filters for this Filterable
	 */
	public List getRemovedFilters()
	{
		return removedFilters;
	}

	/**
	 * Selector
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return a list with the selected filters for this Filterable
	 */
	public List getSelectedFilters()
	{
		return selectedFilters;
	}

	/**
	 * specifies wheter the "match any" option should be available<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return <code>true</code>, if "match any" option is available;<br>
	 *         <code>false</code>, otherwise
	 */
	public boolean isMatchAnyEnabled()
	{
		return false;
	}

	/**
	 * specifies wheter the "negate/reverse" option should be available<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return <code>true</code>, if "negate/reverse" option is available;<br>
	 *         <code>false</code>, otherwise
	 */
	public boolean isNegateEnabled()
	{
		return false;
	}

	/**
	 * returns the selection mode<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return the selection mode for this Filterable
	 */
	public int getSelectionMode()
	{
		return DEMO_MODE;
	}

	/**
	 * returns the filter mode<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @return the filter mode
	 */
	public int getFilterMode()
	{
		return filterMode;
	}


	/**
	 * sets the filter mode<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 *
	 * @param mode   the mode to be set
	 */
	public void setFilterMode(int mode)
	{
		filterMode = mode;
	}

	/**
	 * initializes all available filters for this Filterable<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 */
	public abstract void initializeFilters();

	/**
	 * re-applies the selected filters;
	 * has to be called after changes to the filter selection<br>
	 * implementation of Filterable interface
	 *
	 * <br>author: Thomas Behr
	 */
	public abstract void refreshFiltering();


	/**
	 * apply all selected filters in the chosen mode for a specific PObject
	 *
	 * <br>author: Thomas Behr 10-02-02
	 *
	 * @param pObject - the PObject to test for filter acceptance
	 */
	public boolean accept(PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		int mode = getFilterMode();
		PObjectFilter filter;
		for (Iterator it = getSelectedFilters().iterator(); it.hasNext();)
		{
			filter = (PObjectFilter)it.next();
			if ((mode == MATCH_ALL) && !filter.accept(pObject))
			{
				return false;
			}
			else if ((mode == MATCH_ALL_NEGATE) && !filter.accept(pObject))
			{
				return true;
			}
			else if ((mode == MATCH_ANY) && filter.accept(pObject))
			{
				return true;
			}
			else if ((mode == MATCH_ANY_NEGATE) && filter.accept(pObject))
			{
				return false;
			}
		}

		if ((mode == MATCH_ALL) || (mode == MATCH_ANY_NEGATE))
		{
			return true;
		}
		else if ((mode == MATCH_ANY) || (mode == MATCH_ALL_NEGATE))
		{
			// if no filters at all are selected, we accept of course
			return getSelectedFilters().size() == 0;
		}

		return true;
	}
}


