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

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
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
//	private int lastRow = -1;
//	private int lastCol = -1;
//	private String lastTip = "";
//
//	private void resetTip()
//	{
//		lastRow = -1;
//		lastCol = -1;
//	}
//
//	public void addColumn(TableColumn aColumn)
//	{
//		resetTip();
//		super.addColumn(aColumn);
//	}
//
//	public void columnAdded(javax.swing.event.TableColumnModelEvent e)
//	{
//		resetTip();
//		super.columnAdded(e);
//	}
//
//	public void columnMoved(javax.swing.event.TableColumnModelEvent e)
//	{
//		resetTip();
//		super.columnMoved(e);
//	}
//
//	public void columnRemoved(javax.swing.event.TableColumnModelEvent e)
//	{
//		resetTip();
//		super.columnRemoved(e);
//	}
//
//	public void moveColumn(int column, int targetColumn)
//	{
//		resetTip();
//		super.moveColumn(column, targetColumn);
//	}
//
//	public void removeColumn(TableColumn aColumn)
//	{
//		resetTip();
//		super.removeColumn(aColumn);
//	}
//
//	public void tableChanged(javax.swing.event.TableModelEvent e)
//	{
//		resetTip();
//		super.tableChanged(e);
//	}
//

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

/*	public JTableEx(final Vector rowData, final Vector columnNames)
	{
		super(rowData, columnNames);
	}*/

	public JTableEx(final Object[][] rowData, final Object[] columnNames)
	{
		super(rowData, columnNames);
	}

	/**
	 * If text is longer than 20 chars, show t
	 */
	private String wrap(String text)
	{
		text = text.substring(text.lastIndexOf("|") + 1);
		int textLength = text.length();
		StringBuffer wrapped = new StringBuffer(textLength);
		final int length = 70;
		while (textLength > length)
		{
			// XXX correct the line below for Linux
			if (text.indexOf('\\') >= 0)
			{
				break;
			}

			int pos;
			int lastBreak = -1;
			boolean bInHtmlTag = false;
			int displayedCount = 0;
			for (pos = 0; pos < textLength; pos++)
			{
				if (displayedCount >= length)
				{
					break;
				}
				switch (text.charAt(pos))
				{
					case ' ':
						if (!bInHtmlTag)
						{
							lastBreak = pos;
							displayedCount += 1;
						}
						break;

					case '<':
						bInHtmlTag = true;
						break;

					case '>':
						bInHtmlTag = false;
						break;

					default:
						if (!bInHtmlTag)
						{
							displayedCount += 1;
						}
						break;
				}
			}

			if (displayedCount < length)
			{
				lastBreak = textLength;
			}
			if (lastBreak == -1)
			{
				lastBreak = length;
			}
			if (wrapped.length() != 0)
			{
				wrapped.append("<br>");
			}
			wrapped.append(text.substring(0, lastBreak));
			text = text.substring(lastBreak).trim();
			textLength = text.length();
		}
		if (text.length() != 0)
		{
			if (wrapped.length() != 0)
			{
				wrapped.append("<br>");
			}
			wrapped.append(text);
		}
		if (!wrapped.toString().startsWith("<html>"))
		{
			wrapped.insert(0, "<html>");
			wrapped.append("</html>");
		}
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

//			//
//			// If we are looking at the same row and column, then used cached tooltip
//			//
//			if ((lastRow == row) && (lastCol == col))
//			{
//				return lastTip;
//			}


			final Object o = getValueAt(row, col);
			if (o == null || o.toString().equals(""))
			{
				return null;
			}
			else
			{
//				lastTip = wrap(o.toString());
//				lastRow = row;
//				lastCol = col;
//				return lastTip;
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

		if (columnModel == null)
		{
			return;
		}
		final int columncount = columnModel.getColumnCount();
		if ((columns.length <= 0) || (columncount < columns.length) || (columncount < columns[columns.length - 1]))
		{
			Globals.debugErrorPrint("Bad parameters passed to setOptimalColumnWidth.");
			return;
		}

		final TableModel data = getModel();

		final int rowCount = data.getRowCount();
		int totalWidth = 0;

		for (int i = 0; i < columns.length; i++)
		{
			try
			{
				final TableColumn column = columnModel.getColumn(columns[i]);
				if (column == null)
				{
					continue;
				}
				final int columnIndex = column.getModelIndex();
				int width = -1;

				//
				// Get the width of the header cell
				//
				TableCellRenderer h = column.getHeaderRenderer();
				if (h == null)
				{
					h = defaultHeaderRenderer;
				}
				if (h != null) // Not explicitly impossible
				{
					final Object value = column.getHeaderValue();
					if (value != null)
					{
						final Component c = h.getTableCellRendererComponent(
							this, value, false, false, -1, i);

						if (c != null)
						{
							width = c.getPreferredSize().width;
						}
					}
				}

				//
				// Cycle through entire column to get the largest cell
				//
				TableCellRenderer r = column.getCellRenderer();
				if (r == null)
				{
					r = this.getDefaultRenderer(data.getColumnClass(columnIndex));
				}
				if (r != null)
				{
					for (int row = rowCount - 1; row >= 0; --row)
					{
						final Object value = data.getValueAt(row, columnIndex);
						if (value != null)
						{
							final Component c = r.getTableCellRendererComponent(
								this, value, false, false, row, columnIndex);

							if (c != null)
							{
								width = Math.max(width, c.getPreferredSize().width);
							}
						}
					}
				}

				if (width >= 0)
				{
					column.setPreferredWidth(width + 5); //It seems to get it just a bit too small.
				}
				else
				{
					; // Don't know what to do here...
				}

				totalWidth += column.getPreferredWidth();
			}
			catch (Exception e)
			{
				Globals.debugErrorPrint("Exception JTableEx.setOptimalColumnWidths:" + i + ":" + columns.length + ":" + columncount
					+ "\nException type:" + e.getClass().getName() + "\nMessage:" + e.getMessage()
				);
			}
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
         * fixes a bug which caused the
         * JTableHeaderUI not to be updated
         * correctly on initialization
         *
         * author: Thomas Behr 13-03-02
         */
	public void updateUI()
	{
		super.updateUI();
                getTableHeader().updateUI();
        }
        

//          public Point getToolTipLocation(MouseEvent event)
//          {
//                  int row = rowAtPoint(event.getPoint());
//                  int col = columnAtPoint(event.getPoint());
//                  Object o = getValueAt(row, col);
//                  if (o == null || o.toString().equals(""))
//                  {
//                          return null;
//                  }
//                  Point pt = getCellRect(row, col, true).getLocation();
//                  pt.translate(-1, -2);
//                  return pt;
//          }
}

