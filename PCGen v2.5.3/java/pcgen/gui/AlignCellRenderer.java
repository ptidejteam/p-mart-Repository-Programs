/*
 * InfoSkills.java
 * Copyright 2002 (C) Bryan McRoberts
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
 * Created on Mar 21, 2002 5:45 PM
 */


/**
 * @author  jaymecox@sourceforge.net
 * @version $Revision: 1.1 $
 */

package pcgen.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class AlignCellRenderer extends DefaultTableCellRenderer
{

	/**
	 * All this does is align the text in the row
	 * align is one of:
	 * SwingConstants.LEFT
	 * SwingConstants.CENTER
	 * SwingConstants.RIGHT
	 * But it could do sooo much more....
	 */

	protected int align;

	public AlignCellRenderer(int align)
	{
		this.align = align;
	}

	public Component getTableCellRendererComponent(JTable table,
		Object value,
		boolean isSelected, boolean hasFocus,
		int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setEnabled(table == null || table.isEnabled());

		setHorizontalAlignment(align);
		
		return this;
	}

}
