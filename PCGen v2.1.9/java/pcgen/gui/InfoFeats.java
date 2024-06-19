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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

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
	private FeatModel dataModel = new FeatModel();
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
	private final Object[] longValues = {" Q ","Armor Proficiency","General","Selected              ","PreReqs","Multi","Stacks","  Description  ","Source"};
	private int numFeats = Globals.getCurrentPC().getFeats();
	private static TableSorter sorter;

	public InfoFeats()
	{
		initComponents();
	}

	private static String getFeatButtonLabel(final PlayerCharacter aPC, Feat aFeat)
	{
		String returnValue = null;
		final String featName = aFeat.getName();
		if (aPC.hasFeat(featName))
		{
			returnValue = "Yes";
		}
		else if (aPC.hasFeatAutomatic(featName))
		{
			returnValue = "Automatic";
		}
		else if (aPC.hasFeatVirtual(featName))
		{
			returnValue = "Virtual";
		}
		else
		{
			returnValue = "No";
		}
		return returnValue;
	}


	private void initComponents()
	{
		//Setup sorter
		sorter = new TableSorter(dataModel);
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
		filters.addItem("Qualified");
		filters.addItem("All");
		filters.addItem("Selected");
		//sort types
		for (int i = 0; i < Globals.getFeatList().size(); i++)
		{
			Feat bFeat = (Feat)Globals.getFeatList().get(i);
			featQList.add(bFeat.getType());
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
					Globals.getCurrentPC().setDirty(true);
					Globals.getCurrentPC().setFeats(numFeatsField.getValue());
				}
				else
				{
					numFeatsField.setValue(Globals.getCurrentPC().getFeats());
				}
			}
		});
		numFeatsField.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{

				if (numFeatsField.getText().length() > 0)
				{
					Globals.getCurrentPC().setDirty(true);
					Globals.getCurrentPC().setFeats(numFeatsField.getValue());
				}
				else
				{
					numFeatsField.setValue(Globals.getCurrentPC().getFeats());
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
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(false);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(true);

				requestDefaultFocus();
				numFeatsField.setValue(Globals.getCurrentPC().getFeats());
				final int[] cols = {0,1,2,3,5,6};
				featTable.setOptimalColumnWidths(cols);
			}
		});

		dataModel.setFilter("Qualified",1);
	}

	public static final class FeatModel extends AbstractTableModel
	{
		private ArrayList feats = Globals.getFeatList();

		public void setFilter(String filterID, int index)
		{
			Iterator i;
			Feat theFeat;

			if (filterID.equals("All"))
			{
				feats = new ArrayList();
				i = Globals.getFeatList().iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if (theFeat.isVisible() == 1 || theFeat.isVisible() == 3)
						feats.add(theFeat);
				}
			}
//				feats = Globals.getFeatList();
			else if (filterID.equals("Qualified"))
			{
				feats = new ArrayList();
				i = Globals.getFeatList().iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if (theFeat.canBeSelectedBy(Globals.getCurrentPC()) && (theFeat.isVisible() == 1 || theFeat.isVisible() == 3))
						feats.add(theFeat);
				}
			}
			else if (filterID.equals("Selected"))
			{
				feats = new ArrayList();
				i = Globals.getFeatList().iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if (Globals.getCurrentPC().hasFeatAtAll(theFeat.getName()) && (theFeat.isVisible() == 1 || theFeat.isVisible() == 3))
						feats.add(theFeat);
				}
			}
			else if (index > 2)
			{
				feats = new ArrayList();
				i = Globals.getFeatList().iterator();
				while (i.hasNext())
				{
					theFeat = (Feat)i.next();
					if (theFeat.getType().equals(filterID) && (theFeat.isVisible() == 1 || theFeat.isVisible() == 3))
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
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (feats != null)
			{
				Feat aFeat = (Feat)feats.get(row);
				switch (column)
				{
					case 0:
						return (aFeat.canBeSelectedBy(aPC) ? "Y" : "N").toString();
					case 1:
						returnValue = aFeat.getName();
						break;
					case 2:
						returnValue = aFeat.getType();
						break;
					case 3:
						returnValue = getFeatButtonLabel(aPC, aFeat);
						break;
					case 4:
						returnValue = aFeat.getRequirements();
						break;
					case 5:
						return (aFeat.isMultiples()  ? "Y" : "N").toString();
					case 6:
						return (aFeat.isStacks() ? "Y" : "N").toString();
					case 7:
						returnValue = aFeat.getDescription();
						break;
					case 8:
						returnValue = aFeat.getSource();
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
			if (Globals.getFeatList() != null)
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
			/**			boolean rv = false;
			 rv = (col == 3);
			 row = sorter.getRowTranslated(row);
			 if(rv)
			 rv = rv && ((Globals.isAutoFeatsRefundable() ||
			 (!getValueAt(row,col).equals("Automatic"))));
			 return rv;*/

			//Look at this ugly return statement!
			return ((col == 3) && (
				(Globals.isAutoFeatsRefundable() ||
				(
				(!getValueAt(row, col).equals("Automatic"))
				|| (
				((Feat)Globals.getFeatNamed((String)getValueAt(row, 1))).isMultiples()
				&& !((String)getValueAt(row, 1)).endsWith("Proficiency")
				)
				|| ((Feat)Globals.getFeatNamed((String)getValueAt(row, 1))).isStacks()
				)
				)
				));
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
						Globals.getCurrentPC().setDirty(true);
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
				String featName = aFeat.getName();
				if (Globals.getCurrentPC().hasFeatAtAll(featName))
				{
					pcHasIt = true;
					aFeat = Globals.getCurrentPC().getFeatNamed(featName);
					featName = aFeat.getName();
				}
				if (numFeatsField.getValue() == 0 && !pcHasIt)
				{
					JOptionPane.showMessageDialog(null, "You cannot select any more feats.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
					isPushed = false;
					return label;
				}
				if (!pcHasIt || aFeat.isMultiples())
				{
					if (Globals.getCurrentPC().qualifiesForFeat(aFeat))
						try
						{
							Globals.getCurrentPC().setDirty(true);
							Globals.getCurrentPC().modFeat(featName, true, false);
						}
						catch (Exception exc)
						{
							JOptionPane.showMessageDialog(null, exc.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
						}
				}
				else
				{
					try
					{
						Globals.getCurrentPC().setDirty(true);
						Globals.getCurrentPC().modFeat(featName, aFeat.isMultiples(), false);
					}
					catch (Exception exc)
					{
					}
				}
				numFeatsField.setValue(Globals.getCurrentPC().getFeats());
				label = getFeatButtonLabel(Globals.getCurrentPC(), aFeat);
/*				label = "No";
				if (Globals.getCurrentPC().hasFeat(aFeat.name()))
					label = "Yes";
				else if (Globals.getCurrentPC().aggregateFeatList().contains(aFeat))
					label = "Virtual";*/
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
