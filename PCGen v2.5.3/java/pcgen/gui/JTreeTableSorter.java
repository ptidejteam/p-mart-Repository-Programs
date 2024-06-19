/*
 * JTreeTableSorter.java
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
 * Created on March 6, 2001, 1:57 PM
 */


/**
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */
package pcgen.gui;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class JTreeTableSorter
{
	protected AbstractTreeTableModel tableModel;
	PObjectNode root;
	public int mode = 0;

	public JTreeTableSorter(JTreeTable table, PObjectNode obj, AbstractTreeTableModel model)
	{
		tableModel = model;
		final AbstractTreeTableModel tmodel = model;
		root = obj;
		final JTreeTable tableView = table;
		table.setColumnSelectionAllowed(false);
		MouseAdapter listMouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				final TableColumnModel columnModel = tableView.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = tableView.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column > -1)
				{
					if (tmodel.getColumnClass(column).isAssignableFrom(Integer.class))
						JTreeTableSorter.this.mode = 1;
					else if (tmodel.getColumnClass(column).isAssignableFrom(Float.class))
						JTreeTableSorter.this.mode = 2;
					else
						JTreeTableSorter.this.mode = 0;
					sortNodeOnColumn(JTreeTableSorter.this.root, column, e.getModifiers() & InputEvent.SHIFT_MASK);
					tmodel.fireTreeNodesChanged(JTreeTableSorter.this.root, JTreeTableSorter.this.root.getChildren(), null, null);
					tableView.updateUI();
				}
			}
		};
		JTableHeader th = table.getTableHeader();
		th.addMouseListener(listMouseListener);
	}

	public void setRoot(PObjectNode obj)
	{
		root = obj;
	}

	public PObjectNode sortNodeOnColumn(PObjectNode node, int col, int ascending)
	{
		// first sort this object
		for (int i = 0; i < node.getChildren().length - 1; i++)
		{
			PObjectNode pi = node.getChildren()[i];
			for (int j = i + 1; j < node.getChildren().length; j++)
			{
				PObjectNode pj = node.getChildren()[j];
				try
				{
					Object pir = tableModel.getValueAt(pi, col);
					Object pjr = tableModel.getValueAt(pj, col);
					// color coding is done before a pipe |, ignore that for sorting purposes.
					final String pis = pir.toString().substring(pir.toString().lastIndexOf("|") + 1);
					final String pjs = pjr.toString().substring(pjr.toString().lastIndexOf("|") + 1);
					int k = 0;
					float kf = 0;
					switch (mode)
					{
						case 0:
							k = pis.compareTo(pjs);
							break;
						case 1:
							k = Integer.parseInt(pis) - Integer.parseInt(pjs);
							break;
						case 2:
							kf = Float.parseFloat(pis) - Float.parseFloat(pjs);
							break;
					}

					if (ascending != 0)
					{
						k = -k;
						kf = -kf;
					}
					if (k > 0 || kf > 0.0)
					{
						PObjectNode temp = (PObjectNode)pj.clone();
						node.getChildren()[j] = (PObjectNode)pi.clone();
						node.getChildren()[i] = temp;
						pi = node.getChildren()[i];
					}
				}
				catch (Exception exc)
				{
				}
			}

		}

		for (int i = 0; i < node.getChildren().length - 1; i++)
		{
			// then sort its children
			sortNodeOnColumn(node.getChildren()[i], col, ascending);
		}
		return node;
	}
}
