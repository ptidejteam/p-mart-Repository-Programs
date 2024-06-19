/*
 * JTableEx.java
 * Copyright 2001 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
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
 * Created on June 27, 2001, 20:36 PM
 */

package pcgen.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import pcgen.core.Globals;

/**
 *  <code>JTableEx</code> extends JTable to provide auto-tooltips.
 *
 * @author     Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */

public class JTableEx extends JTable
{
	public JTableEx()
	{
		this(null, null, null);
	}

	public JTableEx(TableModel tm)
	{
		this(tm, null, null);
	}

	public JTableEx(TableModel tm, TableColumnModel tcm)
	{
		this(tm, tcm, null);
	}

	public JTableEx(TableModel tm, TableColumnModel tcm, ListSelectionModel lsm)
	{
		super(tm, tcm, lsm);
	}

	public JTableEx(int rows, int cols)
	{
		this(new DefaultTableModel(rows, cols));
	}

	public JTableEx(final Vector rowData, final Vector columnNames)
	{
		super(rowData, columnNames);
	}

	public JTableEx(final Object[][] rowData, final Object[] columnNames)
	{
		super(rowData, columnNames);
	}

	/**
	 * If text is longer than 20 chars, show t
	 */
	private String wrap(String text)
	{
		StringBuffer wrapped = new StringBuffer("<html>");
		int textLength = text.length();
		final int length = 70;
		while (textLength >= 0)
		{
			// XXX correct the line below for Linux
			if (textLength <= length || text.indexOf('\\') >= 0)
				break;
			int pos = length;
			while (pos < textLength && text.charAt(pos) != ' ')
				pos++;
			if (pos == textLength)
			{
				pos = textLength - 2;
				while (pos > 0 && text.charAt(pos) != ' ')
					pos--;
			}
			if (pos == 0)  // no space found
				pos = Math.min(length, textLength);
			wrapped.append(text.substring(0, pos)).append("<br>");
			text = text.substring(pos);
			textLength -= pos;
		}
		wrapped.append(text);
		wrapped.append("</html>");
		return wrapped.toString();
	}

	public String getToolTipText(MouseEvent event)
	{
		if (Globals.isToolTipTextShown())
		{

			final int row = rowAtPoint(event.getPoint());
			final int col = columnAtPoint(event.getPoint());

			//Did we get the event from something that was *over* the table (e.g. a listbox's menu)?
			if (row < 0 || col < 0)
			{
				return null;
			}

			final Object o = getValueAt(row, col);
			if (o == null || o.toString().equals(""))
			{
				return null;
			}
			else
			{
				return wrap(o.toString());
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Calculate 'optimal' width (i.e. minimum to show full text) for the columns in the columns list.
	 */
	public void setOptimalColumnWidths(int[] columns)
	{
		final JTableHeader header = getTableHeader();

		final TableCellRenderer defaultHeaderRenderer = (header != null ? header.getDefaultRenderer() : null);

		final TableColumnModel columnModel = getColumnModel();

		final int columncount = columnModel.getColumnCount();
		if (columns.length <= 0 || columncount < columns.length || columncount < columns[columns.length - 1])
		{
			if (Globals.isDebugMode())
			{
				System.out.println("Bad parameters passed to setOptimalColumnWidth.");
			}
			return;
		}

		final TableModel data = getModel();

		int rowCount = data.getRowCount();
		int totalWidth = 0;

		for (int i = 0; i < columns.length; i++)
		{
			final TableColumn column = columnModel.getColumn(columns[i]);

			final int columnIndex = column.getModelIndex();
			int width = -1;

			TableCellRenderer h = column.getHeaderRenderer();

			if (h == null)
			{
				h = defaultHeaderRenderer;
			}

			if (h != null) // Not explicitly impossible
			{
				Component c = h.getTableCellRendererComponent(
					this, column.getHeaderValue(),
					false, false, -1, i);

				width = c.getPreferredSize().width;
			}

			TableCellRenderer r = column.getCellRenderer();
			if (r == null)
			{
				r = this.getDefaultRenderer(data.getColumnClass(columnIndex));
			}

			if (r != null)
			{

				Component c = null;
				for (int row = rowCount - 1; row >= 0; --row)
				{
					c = r.getTableCellRendererComponent(
						this, data.getValueAt(row, columnIndex),
						false, false, row, i);

					width = Math.max(width, c.getPreferredSize().width);
				}
			}

			if (width >= 0)
			{
				column.setPreferredWidth(width);
			}
			else
			{
				; // Don't know what to do here...
			}

			totalWidth += column.getPreferredWidth();
		}

		totalWidth += columncount * columnModel.getColumnMargin();

		final Dimension size = getPreferredScrollableViewportSize();
		size.width = totalWidth;

		setPreferredScrollableViewportSize(size);
		sizeColumnsToFit(-1);

		if (header != null)
		{
			header.repaint();
		}
	}


	/*
  public Point getToolTipLocation(MouseEvent event)
  {
    int row = rowAtPoint(event.getPoint());
    int col = columnAtPoint(event.getPoint());
    Object o = getValueAt(row, col);
    if (o == null || o.toString().equals(""))
    {
      return null;
    }
    Point pt = getCellRect(row, col, true).getLocation();
    pt.translate(-1, -2);
    return pt;
  }*/
}

