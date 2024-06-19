/*
 * InfoEquipment.java
 * Copyright 2001 (C) Thomas G. W. Epperly
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import java.awt.Graphics;


/**
 * This provides the basic form for users to view, purchase and sell equipment.
 * The main part of the panel is a table listing the available equipment.
 * @author  Tom Epperly <tomepperly@home.com>
 * @version $Revision: 1.1 $
 */
public class InfoEquipment extends JPanel
{
    PlayerCharacter aPC = null;  
	/** Creates new form InfoEquipment */
	public InfoEquipment()
	{
		initComponents();
	}
    public void paint(Graphics g)
    {
      super.paint(g);      pcChanged();
    }        public void pcChanged()
    {
      if(Globals.currentPC == aPC)
        return;
      String[] handTypes = null;      if(Globals.currentPC.race().hands() == 0)      {
        handTypes = new String[1];
        handTypes[0] = "NEITHER";
      }      if(Globals.currentPC.race().hands() == 1)      {
        handTypes = new String[2];        handTypes[0] = "NEITHER";        handTypes[1] = "PRIMARY";      }
      if(Globals.currentPC.race().hands() == 2)
        handTypes = s_handTypes;      if(Globals.currentPC.race().hands() > 2)      {
        handTypes = new String[5 + Globals.currentPC.race().hands() - 2];        int count = 0;
        for(count = 0; count < 5; count++)
          handTypes[count] = s_handTypes[count];        int x = 5;
        for(count = 2; count < Globals.currentPC.race().hands(); count++)
          handTypes[x++] = (count + 1) + "-Weapons";      }
      d_equipmentGrid.getColumnModel().getColumn(8).setCellRenderer(new HandRenderer(handTypes));
      d_equipmentGrid.getColumnModel().getColumn(8).setCellEditor(new HandEditor(handTypes));
    }    
	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		d_equipmentArea = new JScrollPane();
		d_sortedEquipment.setModel(d_equipmentModel);
		d_equipmentGrid = new JTableEx();
		d_equipLabel = new JLabel();
		d_equipType = new JComboBox();
		d_costChoice = new JCheckBox();
		d_goldLabel = new JLabel();
		d_goldAmount = new JTextField();
		d_loadLabel = new JLabel();
		d_capacityLabel = new JLabel();
		d_loadType = new JTextField();
		d_weightCapacity = new JTextField();
		d_totalWeight = new JTextField();
		d_totalWeightLabel = new JLabel();

		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		setPreferredSize(new Dimension(550, 350));
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				prepareEquipment(evt);
			}

			public void componentHidden(ComponentEvent evt)
			{
				hideEquipment(evt);
			}
		});

		d_equipmentArea.setMinimumSize(new Dimension(300, 100));
		d_equipmentGrid.setModel(d_sortedEquipment);
		d_equipmentGrid.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		d_equipmentGrid.setRowHeight(18);
		d_equipmentGrid.setName("EquipmentTable");
		for (int column = 0; column < d_equipmentModel.getColumnCount(); ++column)
		{
			d_equipmentGrid.getColumnModel().getColumn(column).
			setPreferredWidth(d_equipmentModel.getColumnWidth(column));
		}                		d_equipmentGrid.getColumnModel().getColumn(8).
			setCellRenderer(new HandRenderer(s_handTypes));
		d_equipmentGrid.getColumnModel().getColumn(8).
			setCellEditor(new HandEditor(s_handTypes));
		d_equipmentGrid.getColumnModel().getColumn(0).
			setCellEditor(new QuantityEditor());
		d_equipmentGrid.getColumnModel().getColumn(6).
			setCellEditor(new QuantityEditor());
		d_sortedEquipment.addMouseListenerToHeaderInTable(d_equipmentGrid);
		d_equipmentArea.setViewportView(d_equipmentGrid);

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.gridwidth = 5;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 10.0;
		add(d_equipmentArea, gridBagConstraints1);

		d_equipLabel.setText("View Equipment of Type");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints1.weighty = 1.0;
		add(d_equipLabel, gridBagConstraints1);

		d_equipType.setModel(new DefaultComboBoxModel(new String[]{ "ALL", "PC EQUIPMENT LIST", "PROFICIENT LIST", "EQUIPPED LIST" }));
		d_equipType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				comboBoxChanged(evt);
			}
		});

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_equipType, gridBagConstraints1);

		d_costChoice.setText("Ignore Cost");
		d_costChoice.setSelected(Globals.ignoreEquipmentCost);
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_costChoice, gridBagConstraints1);

		d_goldLabel.setText("Gold:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_goldLabel, gridBagConstraints1);

		d_goldAmount.setColumns(4);
		d_goldAmount.setText("0.0");
		d_goldAmount.setPreferredSize(new Dimension(44, 20));
		d_goldAmount.setMinimumSize(new Dimension(32, 22));
		d_goldAmount.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				goldReturnPressed(evt);
			}
		});

		d_goldAmount.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				goldLostFocus(evt);
			}
		});

		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new Insets(0, 6, 0, 6);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		add(d_goldAmount, gridBagConstraints1);

		d_loadLabel.setText("Load:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_loadLabel, gridBagConstraints1);

		d_capacityLabel.setText("Capacity:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 3;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_capacityLabel, gridBagConstraints1);

		d_loadType.setEditable(false);
		d_loadType.setColumns(8);
		d_loadType.setText("LIGHT");
		d_loadType.setBorder(null);
		d_loadType.setOpaque(false);
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.insets = new Insets(0, 6, 0, 6);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_loadType, gridBagConstraints1);

		d_weightCapacity.setEditable(false);
		d_weightCapacity.setColumns(8);
		d_weightCapacity.setText("100.0 lbs");
		d_weightCapacity.setBorder(null);
		d_weightCapacity.setMinimumSize(new Dimension(32, 22));
		d_weightCapacity.setOpaque(false);
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 4;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.ipadx = 1;
		gridBagConstraints1.ipady = 1;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		add(d_weightCapacity, gridBagConstraints1);

		d_totalWeight.setEditable(false);
		d_totalWeight.setColumns(8);
		d_totalWeight.setText("25.26 lbs.");
		d_totalWeight.setBorder(null);
		d_totalWeight.setMinimumSize(new Dimension(32, 22));
		d_totalWeight.setOpaque(false);
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 4;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		add(d_totalWeight, gridBagConstraints1);

		d_totalWeightLabel.setText("Total Weight:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 3;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 7);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_totalWeightLabel, gridBagConstraints1);

	}

	private void hideEquipment(ComponentEvent evt)
	{
		if (d_shown)
		{
			updateCharacterGold();
		}
	}

	private void goldReturnPressed(ActionEvent evt)
	{
		updateCharacterGold();
	}

	private void goldLostFocus(FocusEvent evt)
	{
		updateCharacterGold();
	}

	private void prepareEquipment(ComponentEvent evt)
	{
		requestDefaultFocus();
		Object selectedItem = d_equipType.getSelectedItem();
		SortedSet equipmentTypes = Equipment.getEquipmentTypes();
		if (Globals.currentPC != null)
		{
			d_goldAmount.setText(Globals.currentPC.gold().toString());
			d_totalWeight.setText(Globals.currentPC.totalWeight().toString());
			d_weightCapacity.setText(Globals.maxLoadForStrengthAndSize(
				Globals.currentPC.adjStats(0), Globals.currentPC.size()).
				toString());
		}
		else
		{
			d_goldAmount.setText("0");
			d_totalWeight.setText("0");
		}
		try
		{
			d_equipType.setEnabled(false);
			d_equipType.removeAllItems();
			for (int i = 0; i < s_basicGroups.length; ++i)
				d_equipType.addItem(s_basicGroups[i]);
			Iterator j = equipmentTypes.iterator();
			while (j.hasNext())
			{
				d_equipType.addItem(j.next());
			}
			j = null;
			d_equipType.setSelectedItem(selectedItem);
			changeEquipmentList();
		}
		finally
		{
			d_equipType.setEnabled(true);
			d_shown = true;
		}
	}

	private void comboBoxChanged(ActionEvent evt)
	{
		if (d_equipType.isEnabled())
			changeEquipmentList();
	}

	/**
	 * This internal class is provides the equipment table with the data it needs
	 * to operate. It has column header names, column widths, the row count, the
	 * and the column count. For the actual data, this class relies on the global
	 * equipment list from <code>Globals</code>.
	 *
	 * @author Tom Epperly <tomepperly@home.com>
	 * @version $Revision: 1.1 $
	 * @see pcgen.core.Globals
	 */
	public final class GlobalEquipmentModel
		extends AbstractTableModel
	{

		private ArrayList d_equipmentList = new ArrayList();


		private boolean canAfford(Equipment table,
			Equipment player,
			float newQty)
		{
			final float currentFunds =
				((Globals.currentPC != null) ? Globals.currentPC.gold().floatValue()
				: 0);
			final float previousQty =
				((player != null) ? player.qty().floatValue() : 0);
			return
				d_costChoice.isSelected() || // ignore cost
				(((newQty - previousQty) * table.cost().floatValue()) <= currentFunds);
		}

		private void adjustGold(Equipment table,
			Equipment player,
			float diffQty)
		{
			PlayerCharacter aPC = Globals.currentPC;
			if (!d_costChoice.isSelected() &&
				aPC != null)
			{
				aPC.setDirty(true);
				aPC.adjustGold(((diffQty) *
					table.cost().floatValue()));
				d_goldAmount.setText(aPC.gold().toString());
			}
		}

		private void adjustWeight(Equipment table,
			Equipment player,
			float carriedDiff)
		{
			PlayerCharacter aPC = Globals.currentPC;
			if (aPC != null &&
				player != null &&
				player.numberCarried().compareTo(new Float(0)) > 0)
			{
				aPC.setDirty(true);
				float weight =
					Float.parseFloat(d_totalWeight.getText());
				weight += carriedDiff * table.weight().floatValue();
				d_totalWeight.setText(Float.toString(weight));
				d_loadType.setText
					(s_loadTypes[Globals.loadTypeForStrength
					(aPC.adjStats(0),
						new Float(weight))]);
			}
		}

		private void adjustBelongings(Equipment table,
			Equipment player,
			float newQty,
			int row)
		{
			PlayerCharacter aPC = Globals.currentPC;
			if (aPC != null)
			{
				aPC.setDirty(true);
				if (newQty <= 0)
				{
					if (player != null)
					{

						aPC.equipmentList().remove(player.keyName());
						if (d_equipType.getSelectedIndex() == 1)
						{
							d_equipmentList.remove(row);
							fireTableRowsDeleted(row, row);
						}
					}
				}
				else
				{
					if (player == null)
					{
						player = (Equipment)table.clone();
						if (player != null)
						{
							//This was added to keep jlint happy.
							aPC.equipmentList().put(player.keyName(), player);
						}
					}
					if (player != null)
					{
						//This was added to keep jlint happy.
						player.setQty(new Float(newQty));
					}
				}
			}
		}

		/**
		 * Change the value of a grid cell.
		 */
		public void setValueAt(Object value, int row, int column)
		{
			if (Globals.currentPC == null) return;
			Equipment e = (Equipment)d_equipmentList.get(row);
			Equipment player = (Equipment)Globals.currentPC.equipmentList().get(e.keyName());
			switch (column)
			{
				case 0:
					{
						final float newQty = ((((Float)value).floatValue() > 0)
							? (((Float)value).floatValue()) : 0.0f);
						final float oldQty = (player == null) ? 0.0f : player.qty().floatValue();
						final float oldCarried = (player == null) ? 0.0f : player.numberCarried().floatValue();
						final float newCarried = (newQty < oldCarried) ? newQty : oldCarried;
						if (canAfford(e, player, newQty))
						{
							adjustBelongings(e, player, newQty, row);
							adjustWeight(e, player, newCarried - oldCarried);
							adjustGold(e, player, oldQty - newQty);

						}
						else
						{
							JOptionPane.showMessageDialog
								(null,
									"Insufficient funds for purchase of " +
								newQty + " " + e.name());
						}
					}
					break;
				case 6:
					if (player != null)
					{
						float newValue = ((Float)value).floatValue();
						if (newValue > player.qty().floatValue())
						{
							JOptionPane.showMessageDialog
								(null,
									"Cannot carry more than you have of " + e.name());
							newValue = player.qty().floatValue();
						}
						if (newValue != player.numberCarried().floatValue())
						{
							float weight = Float.parseFloat(d_totalWeight.getText());
							weight += (newValue - player.numberCarried().floatValue()) *
								(player.weight().floatValue());
							player.setNumberCarried(new Float(newValue));
							d_totalWeight.setText(Float.toString(weight));
							d_loadType.setText(s_loadTypes[Globals.
								loadTypeForStrength(Globals.currentPC.adjStats(0),
									new Float(weight))]);
						}
					}
					break;
				case 7:
					if (player != null)
					{
						player.setIsEquipped(((Boolean)value).booleanValue());
						if (player.isEquipped() && player.numberCarried().compareTo(new Float(0)) == 0)
						{
							setValueAt(player.qty(), row, 6);
							fireTableCellUpdated(row, 6);
						}
					}
					break;
				case 8:
					if (player != null)
					{
					  	int hand = ((Integer)value).intValue();
                        if(hand >= 4)
                        {                          player.setHand(hand);                          player.setNumberEquipped(hand - 2);                        }                        else                           player.setHand(hand);
					}
					break;
			}
		}

		/**
		 * Return the value of a grid cell by using the information from the global
		 * equipment list.
		 *
		 * @see Globals
		 */
		public Object getValueAt(int row, int column)
		{
			Equipment e = (Equipment)d_equipmentList.get(row);
			switch (column)
			{
				case 0:
					if (Globals.currentPC != null)
					{
						Equipment player = (Equipment)
							Globals.currentPC.equipmentList().get(e.keyName());
						if (player != null)
							return player.qty();
					}
					return e.qty();
				case 1:
					return e.name();
				case 2:
					return
						((Globals.currentPC != null) &&
						Globals.currentPC.isProficientWith(e) && e.meetsPreReqs()) ? "Y" : "N";
				case 3:
					return e.typeIndex(0);
				case 4:
					return e.typeIndex(1);
				case 5:
					return e.cost();
				case 6:
					if (Globals.currentPC != null)
					{
						Equipment player = (Equipment)
							Globals.currentPC.equipmentList().get(e.keyName());
						if (player != null)
							return (player.numberCarried());
					}
					return (e.numberCarried());
				case 7:
					if (Globals.currentPC != null)
					{
						Equipment player = (Equipment)
							Globals.currentPC.equipmentList().get(e.keyName());
						if (player != null)
							return (player.isEquipped()) ? Boolean.TRUE : Boolean.FALSE;
					}
					return (e.isEquipped()) ? Boolean.TRUE : Boolean.FALSE;
				case 8:
					if (Globals.currentPC != null)
					{
						Equipment player = (Equipment)
							Globals.currentPC.equipmentList().get(e.keyName());
						if (player != null)
							return new Integer(player.whatHand());
					}
					return new Integer(e.whatHand());
				case 9:
					return e.preReqString();
				case 10:
					return e.weight();
				case 11:
					return e.acMod();
				case 12:
					return e.maxDex();
				case 13:
					return e.acCheck();
				case 14:
					return e.spellFailure();
				case 15:
					return e.moveString();
				case 16:
					return e.size();
				case 17:
					return e.damage();
				case 18:
					return e.critRange();
				case 19:
					return e.critMult();
				case 20:
					return e.range();
				default:
					return null;
			}
		}

		/**
		 * Return the current number of rows in the table based on the value from
		 * the global equipment list.
		 *
		 * @return the number of rows
		 */
		public int getRowCount()
		{
			return d_equipmentList.size();
		}

		/**
		 * Return the column name.
		 *
		 * @param column the number of the column 0...getColumnCount()-1.
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
		 * Only the quantity is editable.
		 *
		 * @return <code>false</code> except for column 0.
		 */
		public boolean isCellEditable(int row, int column)
		{
			return (column == 0) || ((column >= 6) && (column <= 8));
		}

		public Class getColumnClass(int column)
		{
			return s_columnTypes[column];
		}

		public final int getColumnWidth(int column)
		{
			return s_columnWidths[column];
		}

		public final void changeEquipmentList(ArrayList list)
		{
			d_equipmentList = list;
			fireTableChanged(new TableModelEvent(this));
		}
	}

	public static final class HandRenderer extends JComboBox implements TableCellRenderer
	{

		public HandRenderer(String[] choices)
		{
			super(choices);
		}

		public Component getTableCellRendererComponent(JTable jTable,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column)
		{
			if (value instanceof Integer)
			{
				setSelectedIndex(((Integer)value).intValue());
			}
			return this;
		}

	}

	private static final class HandEditor extends JComboBox implements TableCellEditor
	{

		private final transient Vector d_listeners = new Vector();
		private transient int d_originalValue = 0;

		public HandEditor(String[] choices)
		{
			super(choices);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			}
			);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.addElement(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null) return this;
			d_originalValue = getSelectedIndex();
			if (value instanceof Integer)
			{
				setSelectedIndex(((Integer)value).intValue());
			}
			else
			{
				setSelectedIndex(0);
			}
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(java.util.EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.removeElement(cellEditorListener);
		}

		public java.lang.Object getCellEditorValue()
		{
			return new Integer(getSelectedIndex());
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(java.util.EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.elementAt(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.elementAt(i)).editingStopped(ce);
			}
		}
	}

	private final class QuantityEditor extends JTextField implements TableCellEditor
	{
		private final transient Vector d_listeners = new Vector();
		private transient String d_originalValue = "";

		public QuantityEditor()
		{
			super();
			this.setAlignmentX(QuantityEditor.RIGHT_ALIGNMENT);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.addElement(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable,
			Object obj,
			boolean isSelected,
			int row,
			int column)
		{
			if (obj instanceof Number &&
				((Number)obj).intValue() == ((Number)obj).floatValue())
			{
				setText(Integer.toString(((Number)obj).intValue()));
			}
			else
			{
				if (obj != null)
				{
					setText(obj.toString());
				}
				else
				{
					setText("0");
				}
			}
			d_originalValue = getText();
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			this.setAlignmentX(RIGHT_ALIGNMENT);
			selectAll();
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(java.util.EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.removeElement(cellEditorListener);
		}

		public Object getCellEditorValue()
		{
			try
			{
				return new Float(getText());
			}
			catch (NumberFormatException nfe)
			{
				return new Float(d_originalValue);
			}
		}

		public boolean stopCellEditing()
		{
			fireEditingStopped();
			return true;
		}

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setText(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.elementAt(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.elementAt(i)).editingStopped(ce);
			}
		}
	}

	private final void changeEquipmentList()
	{
		if (d_equipType.getSelectedIndex() < 0)
		{
			d_equipType.setSelectedIndex(0);
		}
		switch (d_equipType.getSelectedIndex())
		{
			case -1:
				System.out.println("Bad equipment type selected");
				break;
			case 0:
				d_equipmentModel.
					changeEquipmentList((ArrayList)Globals.equipmentList.clone());
				break;
			case 1:
				if (Globals.currentPC != null)
				{
					d_equipmentModel.changeEquipmentList(new ArrayList
						(Globals.currentPC.equipmentList().values()));
				}
				else
				{
					d_equipmentModel.changeEquipmentList(new ArrayList());
				}
				break;
			case 2:
				if (Globals.currentPC != null)
				{
					d_equipmentModel.changeEquipmentList(Equipment.
						selectProficientWeapons(Globals.equipmentList));
				}
				else
				{
					d_equipmentModel.changeEquipmentList(new ArrayList());
				}
				break;
			case 3:
				if (Globals.currentPC != null)
				{
					d_equipmentModel.changeEquipmentList(Equipment.
						selectEquipped(Globals.currentPC.equipmentList().values()));
				}
				else
				{
					d_equipmentModel.changeEquipmentList(new ArrayList());
				}
				break;
			default:
				String type = (String)d_equipType.getSelectedItem();
				d_equipmentModel.changeEquipmentList(Equipment.
					selectEquipment(Globals.equipmentList, type));
				break;
		}
	}

	private final void updateCharacterGold()
	{
		PlayerCharacter aPC = Globals.currentPC;
		if (aPC != null)
		{
			try
			{
				float goldAmt = Float.parseFloat(d_goldAmount.getText());
				if (goldAmt != aPC.gold().floatValue())
				{
					aPC.setGold(d_goldAmount.getText());
					aPC.setDirty(true);
				}
			}
			catch (NumberFormatException nfe)
			{
				d_goldAmount.setText(aPC.gold().toString());
			}
		}
	}

	private boolean d_shown = false;

	// Variables declaration
	private JScrollPane d_equipmentArea;
	private JTableEx d_equipmentGrid;
	private JLabel d_equipLabel;
	private JComboBox d_equipType;
	private JCheckBox d_costChoice;
	private JLabel d_goldLabel;
	private JTextField d_goldAmount;
	private JLabel d_loadLabel;
	private JLabel d_capacityLabel;
	private JTextField d_loadType;
	private JTextField d_weightCapacity;
	private JTextField d_totalWeight;
	private JLabel d_totalWeightLabel;

	private InfoEquipment.GlobalEquipmentModel d_equipmentModel = new InfoEquipment.GlobalEquipmentModel();
	private TableSorter d_sortedEquipment = new TableSorter();

	private static final String[] s_basicGroups =
		{
			"ALL",
			"PC EQUIPMENT LIST",
			"PROFICIENT LIST",
			"EQUIPPED LIST"
		};

	/**
	 * This array stores the preferred column width for each column. This data is
	 * not required by the TableModel, but it seemed like the best place to store
	 * it. These values look okay on a 75 dpi X server.
	 */
	private static final int[] s_columnWidths =
		{
			40,
			220,
			50,
			70,
			70,
			40,
			45,
			45,
			80,
			60,
			60,
			35,
			70,
			70,
			100,
			50,
			50,
			65,
			75,
			65,
			50
		};


	/**
	 * Store the class type of each column in this static array;
	 */
	private static final Class[] s_columnTypes = new Class[]
	{
		java.lang.Float.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.Float.class,
		java.lang.Double.class,
		java.lang.Boolean.class,
		java.lang.Integer.class,
		java.lang.String.class,
		java.lang.Float.class,
		java.lang.Integer.class,
		java.lang.Integer.class,
		java.lang.Integer.class,
		java.lang.Integer.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.String.class,
		java.lang.Integer.class
	};

	/**
	 * Store the column heading names here.
	 */
	private static final String[] s_columnNames =
		{
			"Qty",
			"Name",
			"Prof",
			"Type",
			"SubType",
			"Cost",
			"Carry",
			"Equip",
			"Hand",
			"PreReqs",
			"Weight",
			"AC",
			"MaxDex",
			"ACCheck",
			"Arcane Failure",
			"Move",
			"Size",
			"Damage",
			"CritRange",
			"CritMult",
			"Range"
		};

	private final static String[] s_loadTypes =
		{
			"LIGHT",
			"MEDIUM",
			"HEAVY",
			"OVERLOADED"
		};

	private final static String[] s_handTypes =
		{
			"NEITHER",
			"PRIMARY",
			"OFF-HAND",
			"BOTH",
                  "Two-Weapons"
		};
}
