/*
 * JComboBoxRenderer.java
 * Copyright 2003 (C) Jonas Karlsson
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
 */

package pcgen.gui.utils;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Handles rendering of a jcombobox in a table.
 *
 * @author  Jonas Karlsson
 * @version $Revision: 1.1 $
 */
public class JComboBoxRenderer extends JComboBoxEx implements TableCellRenderer
{
	public JComboBoxRenderer(Object[] objects)
	{
		super(objects);
	}

	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		if (value == null)
		{
			return null;
		}
		int i = -1;
		if (value instanceof String)
		{
			i = Integer.parseInt((String) value);
		}
		else if (value instanceof Integer)
		{
			i = ((Integer) value).intValue();
		}
		if (i < 0 || i >= this.getItemCount())
		{
			i = 0;
		}
		setSelectedIndex(i);
		return this;
	}
}
