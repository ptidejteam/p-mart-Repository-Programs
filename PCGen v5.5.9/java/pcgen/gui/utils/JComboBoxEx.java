/*
 * JComboBoxEx.java
 * Copyright 2003 (C) B. K. Oxley (binkley) <binkley@alumni.rice.edu>
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
 * Created on July 30, 2003, 8:34 AM
 */

package pcgen.gui.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import pcgen.util.StringIgnoreCaseComparator;

/**
 * Sorted <code>JComboBox</code>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @version $Revision: 1.1 $
 */
public class JComboBoxEx extends JComboBox
{
	/**
	 * The <code>Comparator</code>.  The default is
	 * <code>StringIgnoreCaseComparator</code> (since combo boxes
	 * display string items to the user).
	 */
	private Comparator comparator = new StringIgnoreCaseComparator();

	/**
	 * Should we sort anytime the items are changed?
	 */
	private boolean autoSort = false;

	/**
	 * Creates a <code>JComboBoxEx</code> with a default data
	 * model.
	 */
	public JComboBoxEx()
	{
		super();
	}

	/**
	 * Creates a <code>JComboBoxEx</code> that takes it's items
	 * from an existing <code>ComboBoxModel</code>.
	 *
	 * @param aModel the <code>ComboBoxModel</code> that provides
	 * the displayed list of items
	 */
	public JComboBoxEx(ComboBoxModel aModel)
	{
		super(aModel);
	}

	/**
	 * Creates a <code>JComboBoxEx</code> that contains the
	 * elements in the specified array. By default the first item
	 * in the array (and therefore the data model) becomes
	 * selected.
	 *
	 * @param items an array of objects to insert into the combo
	 * box
	 */
	public JComboBoxEx(Object[] items)
	{
		super(items);
	}

	/**
	 * Creates a <code>JComboBoxEx</code> that contains the
	 * elements in the specified <code>Vector</code>. By default
	 * the first item in the vector and therefore the data model)
	 * becomes selected.
	 *
	 * @param items an array of vectors to insert into the combo
	 * box
	 */
	public JComboBoxEx(Vector items)
	{
		super(items);
	}

	/**
	 * Returns the <code>Comparator</code> used to sort items.
	 * The default is <code>StringIgnoreCaseComparator</code>
	 * (since combo boxes display string items to the user).
	 *
	 * @return the <code>Comparator</code> used to sort items
	 */
	public Comparator getComparator()
	{
		return comparator;
	}

	/**
	 * Sets the <code>Comparator</code> used to sort items.  The
	 * default is <code>StringIgnoreCaseComparator</code> (since
	 * combo boxes display string items to the user).
	 *
	 * @param comparator the <code>Comparator</code> used to sort
	 * items
	 */
	public void setComparator(Comparator comparator)
	{
		this.comparator = comparator;
	}

	/**
	 * Returns <code>true</code> if the combo box automatically
	 * sorts when items change.  If <code>false</code>, then call
	 * {@link #sortItems()} to sort items.  This is an
	 * optimization choice.  The default is <code>false</code>.
	 *
	 * <strong>This only affects combo box methods.</strong> If
	 * you modify what an item returns for <code>toString()</code>
	 * by manipulating the item, you need to call
	 * <code>sortItems()</code> manually.
	 *
	 * @return <code>true</code> if the combo box automatically
	 * sorts when items change.
	 */
	public boolean getAutoSort()
	{
		return autoSort;
	}

	/**
	 * Set <code>true</code> if the combo box automatically should
	 * sort when items change.  If <code>false</code>, then
	 * require a call to {@link #sortItems()} to sort items.  This
	 * is an optimization choice.  The default is
	 * <code>false</code>.
	 *
	 * <strong>This only affects combo box methods.</strong> If
	 * you modify what an item returns for <code>toString()</code>
	 * by manipulating the item, you need to call
	 * <code>sortItems()</code> manually.
	 *
	 * @param autoSort automatically sort when items change?
	 */
	public void setAutoSort(boolean autoSort)
	{
		this.autoSort = autoSort;
	}

	/**
	 * Gets all the items.
	 *
	 * @return an array of objects in the combo box
	 */
	public Object[] getAllItems()
	{
		int count = getItemCount();
		Object[] items = new Object[count];

		for (int i = 0; i < count; ++i)
		{
			items[i] = getItemAt(i);
		}

		return items;
	}

	/**
	 * Sets all the items.
	 *
	 * @param items an array of objects to insert into the combo
	 * box
	 */
	public void setAllItems(Object[] items)
	{
		// setModel(getModel().getClass().getDeclaredConstructor(new Class[] {Object[].class}).newInstance(new Object[] {items}));

		removeAllItems();

		for (int i = 0; i < items.length; ++i)
		{
			super.addItem(items[i]);
		}
	}

	/**
	 * Sorts the combo box items using the comparator for this
	 * combo box.
	 *
	 * @see #setComparator(Comparator)
	 */
	public void sortItems()
	{
		sortItems(comparator);
	}

	/**
	 * Sorts the combo box items using <var>comparator</var>.
	 *
	 * @param comparator the <code>Comparator</code> used to sort
	 * items
	 */
	public void sortItems(Comparator comparator)
	{
		// Keep the same item selected after sorting
		Object selected = getSelectedItem();
		Object[] items = getAllItems();

		Arrays.sort(items, comparator);
		setAllItems(items);
		setSelectedItem(selected);
	}

	/** {@inheritDoc} */
	public void addItem(Object item)
	{
		super.addItem(item);

		if (autoSort)
		{
			sortItems();
		}
	}
}
