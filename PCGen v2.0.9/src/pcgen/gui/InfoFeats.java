/*
 * InfoFeats.java
 * Copyright 2001 (C) Mario Bonassin <zebuleon@peoplepc.com>
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
 * Created on April 21, 2001, 2:15 PM
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Feat;
import pcgen.core.Globals;

/**
 * <code>InfoFeats</code>.
 *
 * @author Mario Bonassin <zebuleon@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */
public class InfoFeats extends JPanel
{

	private JTableEx featTable = null;
	private ArrayList featQList = new ArrayList();
	public FeatModel dataModel = new FeatModel();
	private static String[] s_columnNames = {"Q","Name","Type","Selected","PreReqs","Multiple","Stacks","Description","Source"};
	private JPanel featCenter = new JPanel();
	private JPanel featSouth = new JPanel();
	private JPanel featSouthWest = new JPanel();
	private JPanel featSouthEast = new JPanel();
	private JLabel featLabel = new JLabel("Feats: ");
	private WholeNumberField numFeatsField;
	private JLabel filter = new JLabel("Filter: ");
	private JComboBox filters = new JComboBox();
	private boolean ALLOW_ROW_SELECTION = true;
	private final Object[] longValues = {" Q ","Armor Profienciy","General","Selected              ","PreReqs","Multi","Stacks","  Description  ","Source"};
	private int numFeats = Globals.currentPC.feats();

	public InfoFeats()
	{
		initComponents();
	}

	private void initComponents()
	{
		//Setup sorter
		TableSorter sorter = new TableSorter(dataModel);
		featTable = new JTableEx(sorter);
		featTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sorter.addMouseListenerToHeaderInTable(featTable);
		//Set up column sizes.
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		for (int i = 0; i < s_columnNames.length; i++)
		{
			column = featTable.getColumnModel().getColumn(i);
			switch (i)
			{
				case 0:
					column.setPreferredWidth(15);
					break;
				case 5:
				case 6:
					column.setPreferredWidth(50);
					break;
				default:
					comp = featTable.getDefaultRenderer(dataModel.getColumnClass(i)).getTableCellRendererComponent(featTable, longValues[i], false, false, 0, i);
					cellWidth = comp.getPreferredSize().width;
					column.setMinWidth(cellWidth);
					break;
			}

		}
		featTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//set up select column
		String[] answer = {"N","Y","V"};
		TableColumn tableColumn = featTable.getColumnModel().getColumn(3);
		tableColumn.setCellRenderer(new ButtonRenderer());
		tableColumn.setCellEditor(new ButtonEditor(new JCheckBox(), featTable));

		//sets up filter
		filters.addItem("All");
		filters.addItem("Qualified");
		filters.addItem("Selected");
		//sort types
		for (int i = 0; i < Globals.featList.size(); i++)
		{
			Feat bFeat = (Feat)Globals.featList.get(i);
			featQList.add(bFeat.type());
		}
		Object[] objArray = featQList.toArray();
		Arrays.sort(objArray);
		featQList.clear();
		Object lastObj = null;
		for (int i = 0; i < objArray.length; i++)
		{
			if (!objArray[i].equals(lastObj))
				featQList.add(objArray[i]);
			lastObj = objArray[i];
		}
		for (int i = 0; i != featQList.size(); i++)
			filters.addItem(featQList.get(i));
		//select filter type and redo table
		filters.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				dataModel.setFilter(e.getItem().toString(), filters.getSelectedIndex());
			}
		});
		//Number of feats available
		numFeatsField = new WholeNumberField(numFeats, 4);
		numFeatsField.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				if (numFeatsField.getText().length() > 0)
				{
					Globals.currentPC.setDirty(true);
					Globals.currentPC.setFeats(numFeatsField.getValue());
				}
				else
				{
					numFeatsField.setValue(Globals.currentPC.feats());
				}
			}
		});
		numFeatsField.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{

				if (numFeatsField.getText().length() > 0)
				{
					Globals.currentPC.setDirty(true);
					Globals.currentPC.setFeats(numFeatsField.getValue());
				}
				else
				{
					numFeatsField.setValue(Globals.currentPC.feats());
				}
			}
		});


		//Setup GUI
		featCenter.setLayout(new BorderLayout());
		featSouth.setLayout(new GridLayout(1, 2));
		featSouthWest.setLayout(new FlowLayout());
		featSouthEast.setLayout(new FlowLayout());
		JScrollPane scrollPane = new JScrollPane(featTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		scrollPane.setBackground(new Color(255, 255, 255));
		featSouthWest.add(featLabel);
		featSouthWest.add(numFeatsField);
		featSouthEast.add(filter);
		featSouthEast.add(filters);
		featSouth.add(featSouthWest);
		featSouth.add(featSouthEast);
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(featSouth, BorderLayout.SOUTH);
		addComponentListener(new java.awt.event.ComponentAdapter()
		{
			public void componentShown(java.awt.event.ComponentEvent evt)
			{
				requestDefaultFocus();
				numFeatsField.setValue(Globals.currentPC.feats());
				int[] cols = {0,1,2,3,5,6};
				featTable.calcColumnWidths(cols);
			}
		});

	}

	public static final class FeatModel extends AbstractTableModel
	{
		private ArrayList feats = Globals.featList;

		public void setFilter(String filterID, int index)
		{
			Iterator i;
			Feat theFeat;
			if (filterID.equals("All"))
				feats = Globals.featList;
			else if (filterID.equals("Qualified"))
			{
				feats = new ArrayList();
				i = Globals.featList.iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if (theFeat.canBeSelectedBy(Globals.currentPC))
						feats.add(theFeat);
				}
			}
			else if (filterID.equals("Selected"))
			{
				feats = new ArrayList();
				i = Globals.featList.iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if ((Globals.currentPC.hasFeat(theFeat.name())) || (Globals.currentPC.aggregateFeatList().contains(theFeat)))
						feats.add(theFeat);
				}
			}
			else if (index > 2)
			{
				feats = new ArrayList();
				i = Globals.featList.iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if (theFeat.type().equals(filterID))
						feats.add(theFeat);
				}
			}
			fireTableDataChanged();
		}

		/**
		 * Return the value of a grid cell by using the information from the global
		 * feat list.
		 *
		 * @see pcgen.core.Globals
		 */
		public Object getValueAt(int row, int column)
		{
			Object returnValue = new Integer(-1);
			if (feats != null)
			{
				Feat aFeat = (Feat)feats.get(row);
				switch (column)
				{
					case 0:
						return (aFeat.canBeSelectedBy(Globals.currentPC) ? "Y" : "N").toString();
					case 1:
						returnValue = aFeat.name();
						break;
					case 2:
						returnValue = aFeat.type();
						break;
					case 3:
						if (Globals.currentPC.hasFeat(aFeat.name()))
							returnValue = "Yes";
						else if (Globals.currentPC.aggregateFeatList().contains(aFeat))
							returnValue = "Virtual";
						else
							returnValue = "No";
						break;
					case 4:
						returnValue = aFeat.getRequirements();
						break;
					case 5:
						return (aFeat.multiples()  ? "Y" : "N").toString();
					case 6:
						return (aFeat.stacks() ? "Y" : "N").toString();
					case 7:
						returnValue = aFeat.description();
						break;
					case 8:
						returnValue = aFeat.getSourceFile();
				}
			}
			return returnValue;
		}

		/**
		 * Return the current number of rows in the table based on the value from
		 * the global feat list.
		 *
		 * @return the number of rows
		 */
		public int getRowCount()
		{
			if (Globals.featList != null)
			{
				return feats.size();
			}
			else
				return 0;
		}

		/**
		 * Return the column name.
		 *
		 * @return the name of the column
		 */
		public String getColumnName(int column)
		{
			return s_columnNames[column];
		}

		/**
		 * Return the number of columns in the table.
		 *
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return s_columnNames.length;
		}

		/**
		 *
		 *
		 *
		 */
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		/**
		 *
		 *
		 *
		 */
		public boolean isCellEditable(int row, int col)
		{
			if (col == 3)
				return true;
			else
				return false;
		}

		public void setValueAt(Object obj, int r, int c)
		{
			Object returnValue = new Integer(-1);
			if (feats != null)
			{
				Feat aFeat = (Feat)feats.get(r);
				switch (c)
				{
					case 3:
						Globals.currentPC.setDirty(true);
						returnValue = obj;
				}
			}
		}
	}

	public class ButtonRenderer extends JButton implements TableCellRenderer
	{

		public ButtonRenderer()
		{
			this.setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
		{
			if (isSelected)
			{
				this.setForeground(table.getSelectionForeground());
				this.setBackground(table.getSelectionBackground());
			}
			else
			{
				this.setForeground(table.getForeground());
				this.setBackground(UIManager.getColor("Button.background"));
			}
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	public class ButtonEditor extends DefaultCellEditor
	{
		protected JButton button;
		private String label;
		private boolean isPushed;
		JTableEx ownerTable = null;

		public ButtonEditor(JCheckBox checkBox, JTable parentTable)
		{
			super(checkBox);
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					fireEditingStopped();
				}
			});
			ownerTable = (JTableEx)parentTable;
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
		{
			if (isSelected)
			{
				button.setForeground(table.getSelectionForeground());
				button.setBackground(table.getSelectionBackground());
			}
			else
			{
				button.setForeground(table.getForeground());
				button.setBackground(table.getBackground());
			}
//    label = (value =="No") ? "Yes" : value.toString();
//    button.setText( label );
			isPushed = true;
			return button;
		}

		public Object getCellEditorValue()
		{
			Feat aFeat = null;
			if (ownerTable != null)
			{
				int row = ownerTable.getSelectedRow();
				if (row >= 0)
					aFeat = Globals.getFeatNamed((String)ownerTable.getValueAt(row, 1));
			}
			if (aFeat != null)
			{
				boolean pcHasIt = false;
				if (Globals.currentPC.hasFeat(aFeat.name()))
				{
					pcHasIt = true;
					aFeat = Globals.currentPC.getFeatNamed(aFeat.name());
				}
				if (numFeatsField.getValue() == 0 && !pcHasIt)
				{
					JOptionPane.showMessageDialog(null, "You cannot select any more feats.");
					isPushed = false;
					return label;
				}
				if (!pcHasIt || aFeat.multiples())
				{
					if (Globals.currentPC.qualifiesForFeat(aFeat))
						try
						{
							Globals.currentPC.setDirty(true);
							Globals.currentPC.modFeat(aFeat.name(), true, false);
						}
						catch (Exception exc)
						{
							JOptionPane.showMessageDialog(null, exc.getMessage());
						}
				}
				else
				{
					try
					{
						Globals.currentPC.setDirty(true);
						Globals.currentPC.modFeat(aFeat.name(), aFeat.multiples(), false);
					}
					catch (Exception exc)
					{
					}
				}
				numFeatsField.setValue(Globals.currentPC.feats());
				label = "No";
				if (Globals.currentPC.hasFeat(aFeat.name()))
					label = "Yes";
				else if (Globals.currentPC.aggregateFeatList().contains(aFeat))
					label = "Virtual";
			}
			isPushed = false;

			return label;
		}

		public boolean stopCellEditing()
		{
			isPushed = false;
			return super.stopCellEditing();
		}

		protected void fireEditingStopped()
		{
			super.fireEditingStopped();
		}
	}
}
