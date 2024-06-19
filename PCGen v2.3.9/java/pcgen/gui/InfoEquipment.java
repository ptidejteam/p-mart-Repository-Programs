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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;


/**
 * This provides the basic form for users to view, purchase and sell equipment.
 * The main part of the panel is a table listing the available equipment.
 * @author  Tom Epperly <tomepperly@home.com>
 * @version $Revision: 1.1 $
 */
public class InfoEquipment extends JPanel
{
	private PlayerCharacter equipCurrentPC = null;
	private ArrayList d_containersAvailable = new ArrayList();
	private int origLocation = 0;

	private boolean d_shown = false;

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
	private JTextField d_totalValue;
	private JLabel d_totalValueLabel;
	private JPopupMenu d_equipmentPopupMenu;
	private JMenu d_popupHand;
	private JCheckBoxMenuItem d_popupIsEquipped;
	private JButton jButtonModifyEq;
//	private JButton jButtonBuyAll;

	private InfoEquipment.GlobalEquipmentModel d_equipmentModel = new InfoEquipment.GlobalEquipmentModel();
	private TableSorter d_sortedEquipment = new TableSorter();

	EQFrame eqFrame = null;

	public static final int EQUIPMENT_NOTCARRIED = 0;
	public static final int EQUIPMENT_CARRIED = 1;
	public static final int EQUIPMENT_EQUIPPED = 2;
	public static final int EQUIPMENT_CONTAINED = 3;

	private static final String[] s_basicGroups =
		{
			"ALL",
			"PC EQUIPMENT LIST",
			"PROFICIENT LIST",
			"EQUIPPED LIST"
		};

	/**
	 * Store the class type of each column in this static array;
	 */

	// Store the class/heading as a pair for each column so that
	// the type and names are kept together to decrease bugs and
	// make it easier to extend the column list.
	private static final int COLUMN_TYPE = 0; // array index
	private static final int COLUMN_NAME = 1; // array index
	private static final Object[][] s_columnTypeNames = new Object[][]
	{
		{ Float.class, "Qty" },
		{ String.class, "Name" },
		{ String.class, "Prof" },
		{ String.class, "Type" },
		{ String.class, "SubType" },
		{ Float.class, "Cost" },
		{ Double.class, "Carried" },
		{ Integer.class, "Location" },
		{ Integer.class, "Hand" },
		{ String.class, "PreReqs" },
		{ Float.class, "Weight" },
		{ Integer.class, "AC" },
		{ Integer.class, "MaxDex" },
		{ Integer.class, "ACCheck" },
		{ Integer.class, "Arcane Failure" },
		{ String.class, "Move" },
		{ String.class, "Size" },
		{ String.class, "Damage" },
		{ String.class, "CritRange" },
		{ String.class, "CritMult" },
		{ Integer.class, "Range" },
		{ String.class, "All Bonuses" },
		{ String.class, "Container Capacity" },
		{ String.class, "Currently Contains" },
		{ String.class, "Source" },
	};
	// Second part: nmemonics for all the columns, instead of
	// hard-coded numbers.
	private static final int COL_QTY = 0;
	private static final int COL_NAME = 1;
	private static final int COL_PROF = 2;
	private static final int COL_TYPE = 3;
	private static final int COL_SUBTYPE = 4;
	private static final int COL_COST = 5;
	private static final int COL_CARRIED = 6;
	private static final int COL_LOCATION = 7;
	private static final int COL_HAND = 8;
	private static final int COL_PREREQS = 9;
	private static final int COL_WEIGHT = 10;
	private static final int COL_AC = 11;
	private static final int COL_MAXDEX = 12;
	private static final int COL_ACCHECK = 13;
	private static final int COL_ARCANE_FAILURE = 14;
	private static final int COL_MOVE = 15;
	private static final int COL_SIZE = 16;
	private static final int COL_DAMAGE = 17;
	private static final int COL_CRITRANGE = 18;
	private static final int COL_CRITMULT = 19;
	private static final int COL_RANGE = 20;
	private static final int COL_ALL_BONUSES = 21;
	private static final int COL_CONTAINER_CAPACITY = 22;
	private static final int COL_CURRENTLY_CONTAINS = 23;
	private static final int COL_SOURCE = 24;

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

	/** Creates new form InfoEquipment */
	public InfoEquipment()
	{
		initComponents();
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		pcChanged();
	}

	public void pcChanged()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC == equipCurrentPC)
			return;

// Code for Multiple Hands
		String[] handTypes = null;
		final int hands = aPC.getRace().getHands();
		if (hands == 0)
		{
			handTypes = new String[1];
			handTypes[0] = s_handTypes[0];
		}
		if (hands == 1)
		{
			handTypes = new String[2];
			handTypes[0] = s_handTypes[0];
			handTypes[1] = s_handTypes[1];
		}
		if (hands == 2)
			handTypes = s_handTypes;
		if (hands > 2)
		{
			handTypes = new String[5 + hands - 2];
			int count = 0;
			for (count = 0; count < 5; count++)
			{
				handTypes[count] = s_handTypes[count];
			}
			int x = 5;
			for (count = 2; count < hands; count++)
			{
				handTypes[x++] = (count + 1) + "-Weapons";
			}
		}
		//  Remove the old Hands popup menu.
		int pumHandIndex = d_equipmentPopupMenu.getComponentIndex(d_popupHand);
		d_equipmentPopupMenu.remove(pumHandIndex);
		// Rebuild the Hands popup menu
		d_popupHand = new JMenu("Hand");
		d_popupHand.setEnabled(false);
		d_equipmentPopupMenu.add(d_popupHand);
		d_equipmentGrid.getColumnModel().getColumn(COL_HAND).setCellRenderer(new HandRenderer(handTypes));
		d_equipmentGrid.getColumnModel().getColumn(COL_HAND).setCellEditor(new HandEditor(handTypes));

//Code for Multiple Locations
		prepareAvailableContainers();
		String[] locationsAvailable = locationChoices();
		d_equipmentGrid.getColumnModel().getColumn(COL_LOCATION).setCellRenderer(new LocationRenderer(locationsAvailable));
		d_equipmentGrid.getColumnModel().getColumn(COL_LOCATION).setCellEditor(new LocationEditor(locationsAvailable));

	}


//	private void oneOfEverythingClick()
//	{
//		final PlayerCharacter aPC = Globals.getCurrentPC();
//		if (aPC == null)
//		{
//			return;
//		}
//		for (Iterator e = Globals.getEquipmentList().iterator(); e.hasNext();)
//		{
//			Equipment eq = (Equipment)e.next();
//			if (aPC.getEquipmentNamed(eq.getName()) == null)
//			{
//				eq = (Equipment)eq.clone();
//				eq.setQty("1");
//				aPC.equipmentListAdd(eq);
//			}
//		}
//		d_equipmentModel.fireTableDataChanged();
//	}

	private void customizeButtonClick()
	{
		if (!jButtonModifyEq.isEnabled())
		{
			return;
		}
		jButtonModifyEq.setEnabled(false);

		final int currentRow = d_equipmentGrid.getSelectedRow();
		if (currentRow >= 0)
		{
			final String aName = (String)d_sortedEquipment.getValueAt(currentRow, COL_NAME);
			final Equipment aEq = Globals.getEquipmentNamed(aName);
			if (aEq != null)
			{
				if (eqFrame == null)
				{
					eqFrame = new EQFrame();
				}
				if (eqFrame.setEquipment(aEq))
				{
					eqFrame.pack();
					eqFrame.setVisible(true);
				}
			}
		}
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
		d_totalValue = new JTextField();
		d_totalValueLabel = new JLabel();
		jButtonModifyEq = new JButton();
//		jButtonBuyAll = new JButton();

		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		setPreferredSize(new Dimension(550, 350));
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				final PlayerCharacter aPC = Globals.getCurrentPC();
				aPC.setAggregateFeatsStable(false);
				aPC.setAutomaticFeatsStable(true);
				aPC.setVirtualFeatsStable(false);
				formComponentShown(evt);

				prepareEquipment(evt);

				final int[] cols = {
				    COL_NAME,
				    COL_PROF,
				    COL_TYPE,
				    COL_SUBTYPE,
				    COL_COST,
				    COL_CARRIED,
				    COL_LOCATION,
				    COL_HAND,
				    COL_WEIGHT,
				    COL_AC,
				    COL_MAXDEX,
				    COL_ACCHECK,
				    COL_ARCANE_FAILURE,
				    COL_MOVE,
				    COL_SIZE,
				    COL_DAMAGE,
				    COL_CRITRANGE,
				    COL_CRITMULT,
				    COL_RANGE,
				    COL_ALL_BONUSES,
				    COL_CURRENTLY_CONTAINS,
				    COL_SOURCE,
				};

				d_equipmentGrid.setOptimalColumnWidths(cols);
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

		d_equipmentGrid.getColumnModel().getColumn(COL_HAND).
			setCellRenderer(new HandRenderer(s_handTypes));
		d_equipmentGrid.getColumnModel().getColumn(COL_HAND).
			setCellEditor(new HandEditor(s_handTypes));
//LocationRend/Edit
		d_equipmentGrid.getColumnModel().getColumn(COL_LOCATION).
			setCellRenderer(new LocationRenderer(locationChoices()));
		d_equipmentGrid.getColumnModel().getColumn(COL_LOCATION).
			setCellEditor(new LocationEditor(locationChoices()));


		d_equipmentGrid.getColumnModel().getColumn(COL_QTY).
			setCellEditor(new QuantityEditor());
		d_equipmentGrid.getColumnModel().getColumn(COL_CARRIED).
			setCellEditor(new QuantityEditor());
		d_sortedEquipment.addMouseListenerToHeaderInTable(d_equipmentGrid);
		d_equipmentArea.setViewportView(d_equipmentGrid);


		javax.swing.ListSelectionModel rowSM = d_equipmentGrid.getSelectionModel();
		rowSM.addListSelectionListener(new javax.swing.event.ListSelectionListener()
		{
			public int selectedRow;
			public javax.swing.ListSelectionModel lsm;

			public void valueChanged(javax.swing.event.ListSelectionEvent e)
			{
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) return;
				lsm = (javax.swing.ListSelectionModel)e.getSource();
				if (!lsm.isSelectionEmpty())
				{
					d_equipmentGrid.addMouseListener(new MouseAdapter()
					{
						public void mouseClicked(MouseEvent f)
						{
							if (f.getClickCount() == 2)
							{
								customizeButtonClick();
							}
							else if (f.getClickCount() == 1)
							{
								jButtonModifyEq.setEnabled(true);
							}
						}
					});
				}
			}
		});


		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.gridwidth = 7;
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

//		d_equipType.setModel(new DefaultComboBoxModel(new String[]{ "ALL", "PC EQUIPMENT LIST", "PROFICIENT LIST", "EQUIPPED LIST" }));
		d_equipType.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				d_equipmentModel.fireTableDataChanged();
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

		jButtonModifyEq.setText("Customize");
		jButtonModifyEq.setEnabled(false);
		jButtonModifyEq.setMnemonic('C');
		jButtonModifyEq.setAlignmentY(0.0F);
		jButtonModifyEq.setHorizontalAlignment(SwingConstants.RIGHT);
		jButtonModifyEq.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				customizeButtonClick();
			}
		});
		add(jButtonModifyEq, new GridBagConstraints());



//
// Easy way to test loading of equipment. Uncomment out this code. Run PCGen. Load all PCC's. Click the button
// (wait quite a bit) and then save the character.
//
//		jButtonBuyAll.setText("Give All");
//		jButtonBuyAll.setMnemonic('G');
//		jButtonBuyAll.setAlignmentY(0.0F);
//		jButtonBuyAll.setHorizontalAlignment(SwingConstants.RIGHT);
//		jButtonBuyAll.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent evt)
//			{
//				oneOfEverythingClick();
//			}
//		});
//		add(jButtonBuyAll, new GridBagConstraints());


		d_costChoice.setText("Ignore Cost");
		d_costChoice.setSelected(Globals.isEquipmentCostIgnored());
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_costChoice, gridBagConstraints1);

		d_goldLabel.setText(Globals.getLongCurrencyDisplay() + ":");
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
		d_weightCapacity.setText("100.0");
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
		d_totalWeight.setText("25.26");
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

		d_totalValue.setEditable(false);
		d_totalValue.setColumns(8);
		d_totalValue.setText("0.0");
		d_totalValue.setBorder(null);
		d_totalValue.setMinimumSize(new Dimension(32, 22));
		d_totalValue.setOpaque(false);
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 6;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		add(d_totalValue, gridBagConstraints1);

		d_totalValueLabel.setText("Total Value:");
		gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 5;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.insets = new Insets(0, 0, 0, 7);
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.weighty = 1.0;
		add(d_totalValueLabel, gridBagConstraints1);

		// Create the popup menu
		JMenu pu_menu;
		JMenuItem pu_menuItem;

		d_equipmentPopupMenu = new JPopupMenu();

		pu_menu = new JMenu("Qty");
		pu_menuItem = new JMenuItem("Add  1");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentQty(1.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Add  5");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentQty(5.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Add 10");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentQty(10.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menu.addSeparator();
		pu_menuItem = new JMenuItem("Remove 1");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentQty(-1.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Remove 5");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentQty(-5.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Remove 10");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentQty(-10.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);

		d_equipmentPopupMenu.add(pu_menu);

		pu_menu = new JMenu("Carried");
		pu_menuItem = new JMenuItem("All");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int currentRow = d_equipmentGrid.getSelectedRow();
				Float currentQty = (Float)d_equipmentModel.getValueAt(currentRow, COL_QTY);  // Get current Qty.
				d_equipmentModel.setValueAt(currentQty, currentRow, COL_CARRIED);  // Set new number carried.
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("None");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int currentRow = d_equipmentGrid.getSelectedRow();
				Float currentQty = new Float(0);
				d_equipmentModel.setValueAt(currentQty, currentRow, COL_CARRIED);  // Set new number carried.
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menu.addSeparator();
		pu_menuItem = new JMenuItem("Add  1");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentCarried(1.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Add  5");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentCarried(5.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Add 10");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentCarried(10.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menu.addSeparator();
		pu_menuItem = new JMenuItem("Remove 1");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentCarried(-1.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Remove 5");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentCarried(-5.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		pu_menuItem = new JMenuItem("Remove 10");
		pu_menuItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addToCurrentCarried(-10.0f);
				d_equipmentGrid.repaint();
			}
		});
		pu_menu.add(pu_menuItem);
		d_equipmentPopupMenu.add(pu_menu);


		d_equipmentPopupMenu.addSeparator();

		d_popupHand = new JMenu("Hand");
		d_equipmentPopupMenu.add(d_popupHand);

		d_popupIsEquipped = new JCheckBoxMenuItem("Equipped");
		d_popupIsEquipped.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				int currentRow = d_equipmentGrid.getSelectedRow();
				boolean isEquipped = d_popupIsEquipped.isSelected();
				if (isEquipped)
					d_equipmentModel.setValueAt(new Integer(2), currentRow, COL_LOCATION);  // Equip It.
				else
					d_equipmentModel.setValueAt(new Integer(1), currentRow, COL_LOCATION);  // de-equip it, carry it.
				d_equipmentGrid.repaint();
			}
		});
		d_equipmentPopupMenu.add(d_popupIsEquipped);


		MouseListener popupListener = new PopupListener();
		d_equipmentGrid.addMouseListener(popupListener);
	}

	private void addToCurrentQty(float delta)
	{
		// Change the Qty of the currently selected item in the equipmentGrid by delta.
		final int currentRow = d_equipmentGrid.getSelectedRow();
		final Float currentQty = (Float)d_sortedEquipment.getValueAt(currentRow, COL_QTY);  // Get current Qty.
		final Float newQty = new Float(((Float)currentQty).floatValue() + delta);
		d_sortedEquipment.setValueAt(newQty, currentRow, COL_QTY);  // Set new Qty.
	}

	private void addToCurrentCarried(float delta)
	{
		// Change the how many of the currently selected item are carried.
		final int currentRow = d_equipmentGrid.getSelectedRow();
		final Float currentQty = (Float)d_sortedEquipment.getValueAt(currentRow, COL_CARRIED);  // Get current qty carried.
		final Float newQty = new Float(((Float)currentQty).floatValue() + delta);
		d_sortedEquipment.setValueAt(newQty, currentRow, COL_CARRIED);  // Set new amount carried.
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

	private void prepareAvailableContainers()
	{

		d_containersAvailable.clear();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			for (Iterator e = aPC.getEquipmentList().values().iterator(); e.hasNext();)
			{
				final Equipment anEquip = (Equipment)e.next();

				if (anEquip.acceptsChildren() && !(anEquip.isHeaderParent()))
					d_containersAvailable.add(anEquip);
			}
		}
	}

	private void prepareEquipment(ComponentEvent evt)
	{
		requestDefaultFocus();
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			d_goldAmount.setText(aPC.getGold().toString());
			updateTotalWeight(aPC.totalWeight());
			final Float weight = aPC.totalWeight();
			d_loadType.setText(s_loadTypes[Globals.loadTypeForStrength(aPC.adjStats(Globals.STRENGTH), weight)]);
			updateTotalValue(aPC.totalValue());
			d_weightCapacity.setText(Globals.maxLoadForStrengthAndSize(
				aPC.adjStats(Globals.STRENGTH), aPC.getSize()).
				toString());
			prepareAvailableContainers();
		}
		else
		{
			d_goldAmount.setText("0");
			d_totalWeight.setText("0");
			d_totalValue.setText("0");
		}
		fillEquipTypeCombo();
	}


	public void fillEquipTypeCombo()
	{
		Object selectedItem = d_equipType.getSelectedItem();
		SortedSet equipmentTypes = Equipment.getEquipmentTypes();

		try
		{
			d_equipType.setEnabled(false);
			d_equipType.removeAllItems();
			Vector tempEquips = new Vector(s_basicGroups.length);
			for (int i = 0; i < s_basicGroups.length; ++i)
			{
				tempEquips.add(s_basicGroups[i]);
			}
			tempEquips.addAll(equipmentTypes);
			/*Iterator j = equipmentTypes.iterator();
			while (j.hasNext())
			{
				tempEquips.add(j.next());
			}
			j = null;*/
			ComboBoxModel model = new DefaultComboBoxModel(tempEquips);
			d_equipType.setModel(model);

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
	 * @see Globals
	 */
	public final class GlobalEquipmentModel
		extends AbstractTableModel
	{

		private ArrayList d_equipmentList = new ArrayList();


		private boolean canAfford(Equipment table,
			Equipment player,
			float newQty)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			final float currentFunds = ((aPC != null) ? aPC.getGold().floatValue()	: 0);
			final float previousQty = ((player != null) ? player.qty().floatValue() : 0);
			return
				d_costChoice.isSelected() || // ignore cost
				(((newQty - previousQty) * table.getCost().floatValue()) <= currentFunds);
		}

		private void adjustGold(Equipment table,
			Equipment player,
			float diffQty)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (!d_costChoice.isSelected() &&
				aPC != null)
			{
				aPC.setDirty(true);
				aPC.adjustGold(((diffQty) *
					table.getCost().floatValue()));
				d_goldAmount.setText(aPC.getGold().toString());
			}
		}

		private void adjustBelongings(Equipment table,
			Equipment player,
			float newQty,
			int row)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC != null)
			{
				aPC.setDirty(true);
				if (newQty <= 0)
				{
					if (player != null)
					{
						if (player.getParent() != null)
						{
							final Equipment aParent = (Equipment)player.getParent();
							aParent.removeChild(player);
						}
						aPC.getEquipmentList().remove(player.getKeyName());
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
							aPC.getEquipmentList().put(player.getKeyName(), player);
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
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC == null) return;

			Equipment e = (Equipment)d_equipmentList.get(row);
			Equipment player = (Equipment)aPC.getEquipmentList().get(e.getKeyName());
			switch (column)
			{
				case COL_QTY:
					{
						final float newQty = ((((Float)value).floatValue() > 0)
							? (((Float)value).floatValue()) : 0.0f);

						if (e.acceptsChildren() && (newQty % 1) != 0)
						{
							JOptionPane.showMessageDialog(null,
								"You cannot buy, own or carry non-integral numbers of containers\ni.e. Half a sack is nonsensical.", Globals.s_APPNAME, JOptionPane.ERROR_MESSAGE);
							break;
						}

						final float oldQty = (player == null) ? 0.0f : player.qty().floatValue();
						final float oldCarried = (player == null) ? 0.0f : player.numberCarried().floatValue();
						final float newCarried = (newQty < oldCarried) ? newQty : oldCarried;
						if (canAfford(e, player, newQty))
						{

//Begin multicontainers code.
							final boolean tempHeaderParent = (player == null) ? false : player.getHasHeaderParent();
							if (e.acceptsChildren() && !tempHeaderParent)
							{
								if (oldQty == 0)
								{
									aPC.getEquipmentList().put(e.getKeyName(), (Equipment)e.clone());
									player = (Equipment)aPC.getEquipmentList().get(e.getKeyName());
									if (player != null && newQty == 1)
										player.setQty(new Float(newQty));
								}

								if (player != null && newQty < oldQty && !e.getHasHeaderParent())
								{
									if (e.isHeaderParent())
									{
										JOptionPane.showMessageDialog(null,
											"You cannot decrease the number of containers using the header,\nremove them by setting the quantity of the ones you want to remove to 0", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
										break;
									}

									e.quickRemoveAllChildren();
									adjustBelongings(e, player, newQty, row);
									changeEquipmentListVoid();
									fireTableDataChanged();

								}
								else if (player != null && newQty > oldQty && newQty > 1)
								{
									int numHeaderChildren = 1;
									if (oldQty > 1)
										numHeaderChildren += player.countHeaderChildren();

									player.setQty(new Float(newQty));

									ArrayList anArrayList = new ArrayList((int)newQty - numHeaderChildren);
									for (int i = numHeaderChildren - 1; i < newQty; i++)
									{
										final Equipment newHeaderChild = (Equipment)player.createHeaderParent();
										if (i > 0)	//newly added containers are empty, excepting -1-, because it is really the original container.
										{
											newHeaderChild.setNumberCarried(new Float(0));
											newHeaderChild.setIsEquipped(false);
											newHeaderChild.getD_childTypes().clear();
											newHeaderChild.getD_childTypes().put("Total", new Float(0));
											newHeaderChild.clearContainedEquipment();
										}

										newHeaderChild.updateContainerContentsString();
										newHeaderChild.clearHeaderChildren();
										player.setIsEquipped(false);
										anArrayList.add(newHeaderChild);
										aPC.equipmentListAdd(newHeaderChild);
									}
									d_equipmentList.addAll(row + numHeaderChildren, anArrayList);
									prepareAvailableContainers();
									aPC.setDirty(true);

									fireTableRowsInserted(row + 1, row + 1 + (int)newQty);  //Prolly not the right command, but lacking documentation, this works
								}
							}
							else if (player != null && player.getHasHeaderParent() && newQty > 1)
							{
								JOptionPane.showMessageDialog(null,
									"You cannot increase the number of containers using the sub-container,\nadd them by increasing the quantity of the header, ie \"Backpack\" not \"Backpack -2-\"", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								break;
							}
							else if (player != null && player.getHasHeaderParent() && newQty == 0)
							{
								//dump the containers contents to carried.
								player.quickRemoveAllChildren();

								//correct the Hchildren of its Hparent
								final Equipment aHParent = (Equipment)player.getHeaderParent();
								aHParent.removeHeaderChild(player);

								//check to see if we need to collapse the headerparent back into a flat container
								if (aHParent.countHeaderChildren() == 1)
								{
									final Equipment hChild = aHParent.getHeaderChild(0);
									aPC.getEquipmentList().remove(hChild.getKeyName());

									aHParent.collapseHeaderParent();
									prepareAvailableContainers();
									adjustBelongings(e, player, newQty, row);
									changeEquipmentListVoid();
									fireTableDataChanged();
								}
								else
								{
									adjustBelongings(e, player, newQty, row);		//now delete it.
								}

								//and fix the container choices
								//prepareAvailableContainers();

							}
							else
							{
								adjustBelongings(e, player, newQty, row);
							}

							if (newQty != oldQty && e.acceptsChildren())
							{
								prepareAvailableContainers();
								final TableColumn col_location = d_equipmentGrid.getColumnModel().getColumn(COL_LOCATION);
								col_location.setCellRenderer(new LocationRenderer(locationChoices()));
								col_location.setCellEditor(new LocationEditor(locationChoices()));
							}

//End multicontainers code

							final Float weight = aPC.totalWeight();
							updateTotalWeight(weight);
							d_loadType.setText(s_loadTypes[Globals.loadTypeForStrength(aPC.adjStats(Globals.STRENGTH), weight)]);
							adjustGold(e, player, oldQty - newQty);
							updateTotalValue(aPC.totalValue());
						}
						else
						{
							JOptionPane.showMessageDialog
								(null,
									"Insufficient funds for purchase of " +
								newQty + " " + e.getName(), Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
						}
					}

					break;
				case 6:
					if (player != null)
					{
						float newValue = ((Float)value).floatValue();

						if (e.acceptsChildren() && (newValue % 1) != 0)
						{
							JOptionPane.showMessageDialog(null,
								"You cannot buy, own or carry non-integral numbers of containers\ni.e. Half a sack is nonsensical.", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							break;
						}

						if (newValue > player.qty().floatValue())
						{
							JOptionPane.showMessageDialog
								(null,
									"Cannot carry more than you have of " + e.getName(), Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							newValue = player.qty().floatValue();
						}
						if (newValue < 0.0f)
						{
							JOptionPane.showMessageDialog
								(null,
									"Cannot carry negative numbers of " + e.getName(), Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							newValue = player.qty().floatValue();
						}
						if (newValue != player.numberCarried().floatValue())
						{
							if (player.isHeaderParent())
							{
								JOptionPane.showMessageDialog(null,
									"You cannot chang the number of containers carried using the header,\ncarry or equip each one seperately.", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								break;
							}


							//Check to see if the item for which the number carried is being changed has a parent.
							if (player.getParent() == null)
								player.setNumberCarried(new Float(newValue));
							else
							{
								final Float origCarried = player.numberCarried();
								final Equipment aParent = (Equipment)player.getParent();


								player.setNumberCarried(new Float(newValue));
								aParent.removeChild(player);
								if (aParent.canContain(player) != 1)
								{
									String aReason = "";
									switch (aParent.canContain(player))
									{
										case 0:
											aReason = "of a Program Error.";
											break;
										case 2:
											aReason = "adding it exceeds the maximum weight capacity of the container.";
											break;
										case 3:
											aReason = "their properties do not mesh (unimplented).";
											break;
										case 4:
											aReason = "the container cannot hold any more items of this type.";
											break;
									}

									JOptionPane.showMessageDialog(null, "Container cannot hold that many more items, because " + aReason, Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
									player.setNumberCarried(origCarried);
								}
								else
								{
									player.setNumberCarried(new Float(newValue));
								}
								aParent.insertChild(player);
								fireTableCellUpdated(row, COL_SOURCE);
							}
							final Float weight = aPC.totalWeight();
							updateTotalWeight(weight);
							d_loadType.setText(s_loadTypes[Globals.loadTypeForStrength
								(aPC.adjStats(Globals.STRENGTH), weight)]);
						}
					}
					break;
				case 7:
					if (player != null)
					{
						if (player.isHeaderParent())  // no editing of Location if it is a header parent
							break;

						int anInt = ((Integer)value).intValue();
						if (anInt >= EQUIPMENT_CONTAINED)
						{
							origLocation = ((Integer)getValueAt(row, COL_LOCATION)).intValue();
							final Equipment aParent = (Equipment)d_containersAvailable.get(anInt - EQUIPMENT_CONTAINED);
							//Before anything, make sure you are not trying to put it in itself.  If so, complain, then reset it to the original value.
							if (player.equals(aParent) || player.equals(aParent.getUberParent()))
							{
								JOptionPane.showMessageDialog(null, "Cannot put an item in itself,\n or into an item contained inside it.", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								break;
							}
							else
							{
								player.setIsEquipped(false); //cannot be equipped if inside a container... may want to change this, but requires many more changes.
								//Check if new selection can hold it
								if (aParent.canContain(player) == 1)
								{
									float newValue = player.numberCarried().floatValue();
									removeParentChild(player);
									player.setParent(aParent);
									aParent.insertChild(player);

									//update weight info....
									Equipment iterParent = aParent;
									while (iterParent.getParent() != null)  //possible recursive, be careful, wont work when I redo the containers to allow > 1
									{
										iterParent = (Equipment)iterParent.getParent();
									}

									if (iterParent.numberCarried().floatValue() != 0)
									{

										//Hack Alert!
										if (newValue == 0)
										{
											newValue = player.qty().floatValue();
										}
										//End Hack Alert!
									}

								}
								else
								{
									String aReason = "";
									switch (aParent.canContain(player))
									{
										case 0:
											aReason = "of a Program Error.";
											break;
										case 2:
											aReason = "adding it exceeds the maximum weight capacity of the container.";
											break;
										case 3:
											aReason = "their properties do not mesh (unimplented).";
											break;
										case 4:
											aReason = "the container cannot hold any more items of this type.";
											break;
									}
									JOptionPane.showMessageDialog(null, "Container cannot hold item, because " + aReason, Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								}
							}
						}
						else
						{
							if (anInt == EQUIPMENT_EQUIPPED)
							{
								if (!e.passesPreReqTests())
								{
									setValueAt(new Integer(EQUIPMENT_CARRIED), row, COL_LOCATION);
									JOptionPane.showMessageDialog(null, "You cannot equip this item as you do not meet the prerequisites", Globals.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
									break;
								}
							}

							removeParentChild(player);
							final int inHand = ((Integer)getValueAt(row, COL_HAND)).intValue();
							player.setIsEquipped(anInt == EQUIPMENT_EQUIPPED, inHand);

							if (anInt == EQUIPMENT_CARRIED)
							{
								//Insert Carried Code here, for when stuff doesn't all have to be in one place.
							}
							else if (anInt == EQUIPMENT_NOTCARRIED)
							{
								setValueAt(new Float(0), row, COL_CARRIED);
								fireTableCellUpdated(row, COL_CARRIED);
							}
						}

						if ((anInt != EQUIPMENT_NOTCARRIED) && player.numberCarried().compareTo(new Float(0)) == 0)
						{
							setValueAt(player.qty(), row, COL_CARRIED);
							fireTableCellUpdated(row, COL_CARRIED);
						}

						//Makes sure the headerparent Carried quantity gets updated in the gui.
						Equipment headerParent = (Equipment)player.getHeaderParent();
						if (headerParent != null)
						{
							fireTableCellUpdated(d_equipmentList.indexOf(headerParent), COL_CARRIED);
						}

						Float weight = aPC.totalWeight();
						updateTotalWeight(weight);
						d_loadType.setText(s_loadTypes[Globals.loadTypeForStrength
							(aPC.adjStats(Globals.STRENGTH), weight)]);
					}
					break;
				case 8:
					if (player != null)
					{
						int hand = ((Integer)value).intValue();
						if (hand >= 4)
						{
							player.setHand(hand);
							player.setNumberEquipped(hand - 2);
						}
						else
							player.setHand(hand);
					}
					break;
			}
		}

		private void removeParentChild(Equipment player)
		{
			if (player.getParent() != null) //remove previous parent/child data
			{
				final Equipment aParent = (Equipment)player.getParent();
				aParent.removeChild(player);
				player.setParent(null);
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
			final Equipment e = (Equipment)d_equipmentList.get(row);
			final PlayerCharacter aPC = Globals.getCurrentPC();
			switch (column)
			{
				case COL_QTY:
					if (aPC != null)
					{
						final Equipment player = (Equipment)aPC.getEquipmentList().get(e.getKeyName());
						if (player != null)
						{
							return player.qty();
						}
					}
					return e.qty();
				case COL_NAME:
					if (e.getHasHeaderParent())
					{
						return "     " + e.toString();
					}
					return e.toString();
				case COL_PROF:
					return
						((aPC != null) &&
						aPC.isProficientWith(e) && e.meetsPreReqs()) ? "Y" : "N";
				case COL_TYPE:
					return e.typeIndex(0);
				case COL_SUBTYPE:
					return e.typeIndex(1);
				case COL_COST:
					return e.getCost();
				case COL_CARRIED:
					if (aPC != null)
					{
						Equipment player = (Equipment)aPC.getEquipmentList().get(e.getKeyName());
						if (player != null)
						{
							return (player.numberCarried());
						}
					}
					return (e.numberCarried());
				case COL_LOCATION:
					if (aPC != null)
					{
						Equipment player = (Equipment)
							aPC.getEquipmentList().get(e.getKeyName());
						if (player != null)
						{
							if (player.isEquipped())
							{
								return new Integer(2);
							}
							if (player.getParent() != null)
							{

								final Equipment containedIn = (Equipment)player.getParent();
								final int aInt = indexLocationChoice(containedIn.toString());

								return new Integer(aInt);
							}
							else if (player.numberCarried().floatValue() > 0)
							{
								return new Integer(1);
							}
							else
							{
								return new Integer(0);
							}
						}
					}
					return new Integer(0);
				case COL_HAND:
					if (aPC != null)
					{
						final Equipment player = (Equipment)aPC.getEquipmentList().get(e.getKeyName());
						if (player != null)
						{
							return new Integer(player.whatHand());
						}
					}
					return new Integer(e.whatHand());
				case COL_PREREQS:
					return e.preReqHTMLStrings();
					//return e.preReqString();
				case COL_WEIGHT:
					return e.getWeight();
				case COL_AC:
					return e.getAcMod();
				case COL_MAXDEX:
					Integer maxdex = e.getMaxDex();
					if (maxdex.intValue() == 100)
					{
						maxdex = null;
					}
					return maxdex;
				case COL_ACCHECK:
					return e.acCheck();
				case COL_ARCANE_FAILURE:
					return e.spellFailure();
				case COL_MOVE:
					return e.moveString();
				case COL_SIZE:
					return e.getSize();
				case COL_DAMAGE:
					String dmg = e.getDamage();
					if (e.isDouble())
					{
						dmg += "/" + e.getAltDamage();
					}
					return dmg;

				case COL_CRITRANGE:
					String critR = e.getCritRange();
					if (e.isDouble())
					{
						final String altCritR = e.getAltCritRange();
						if (!critR.equals(altCritR))
						{
							critR += "/" + altCritR;
						}
					}
					return critR;

				case COL_CRITMULT:
					String critM = e.getCritMult();
					if (e.isDouble())
					{
						final String altCritM = e.getAltCritMult();
						if (!critM.equals(altCritM))
						{
							critM += "/" + altCritM;
						}
					}
					return critM;

				case COL_RANGE:
					return e.getRange();
				case COL_ALL_BONUSES:
					return e.getBonusListString();
				case COL_CONTAINER_CAPACITY:
					return e.getContainerCapacityString();
				case COL_CURRENTLY_CONTAINS:
					return e.getContainerContentsString();
				case COL_SOURCE:
					return e.getSource();
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
			return (String) s_columnTypeNames[column][COLUMN_NAME];
		}

		/**
		 * Return the number of columns in the table.
		 *
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return s_columnTypeNames.length;
		}

		/**
		 * Only the quantity is editable.  BOGUS! --bko
		 *
		 * @return <code>false</code> except for column 0.
		 */
		public boolean isCellEditable(int row, int column)
		{
			return (column == COL_QTY)
			    || (column == COL_CARRIED)
			    || (column == COL_LOCATION)
			    || (column == COL_HAND);
		}

		public Class getColumnClass(int column)
		{
			return (Class) s_columnTypeNames[column][COLUMN_TYPE];
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

		public Object getCellEditorValue()
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

//Begin LocationEditor classes

	public String[] locationChoices()
	{
		String[] choices = new String[d_containersAvailable.size() + 3];
		choices[0] = "not Carried";
		choices[1] = "Carried";
		choices[2] = "Equipped";
		int i = 3;
		for (Iterator e = d_containersAvailable.iterator(); e.hasNext();)
		{
			final Equipment anEquip = (Equipment)e.next();
			choices[i++] = anEquip.toString();
		}

		return choices;
	}

	public int indexLocationChoice(String aString)
	{
		String[] choices = locationChoices();
		for (int i = 0; i < choices.length; i++)
		{
			if (aString.equals(choices[i]))
				return i;
		}
		return -2;
	}

	public final class LocationRenderer extends JComboBox implements TableCellRenderer
	{

		public LocationRenderer(String[] choices)
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

	private final class LocationEditor extends JComboBox implements TableCellEditor
	{

		private final transient Vector d_listeners = new Vector();
		private transient int d_originalValue = 0;

		public LocationEditor(String[] choices)
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

		public Object getCellEditorValue()
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

//End LocationEditor classes



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

	public void changeEquipmentListVoid()
	{
		changeEquipmentList();
	}

	private final void changeEquipmentList()
	{
		if (d_equipType.getSelectedIndex() < 0)
		{
			d_equipType.setSelectedIndex(0);
		}
		final PlayerCharacter aPC = Globals.getCurrentPC();
		switch (d_equipType.getSelectedIndex())
		{
			case -1:
				System.out.println("Bad equipment type selected");
				break;
			case 0:
				if (aPC != null)
				{
					ArrayList tempArray = (ArrayList)Globals.getEquipmentList().clone();
					ArrayList anArray = new ArrayList(0);
					for (int i = 0; i < tempArray.size(); i++)
					{
						anArray.add(tempArray.get(i));
						final Equipment anEquip = (Equipment)aPC.getEquipmentList().get(((Equipment)anArray.get(i)).getKeyName());
						if (anEquip != null)
						{
							if (anEquip.isHeaderParent())
							{
								anArray.addAll(anEquip.getHeaderChildren());
							}
						}
					}
					d_equipmentModel.changeEquipmentList(anArray);


				}
				else
					d_equipmentModel.
						changeEquipmentList((ArrayList)Globals.getEquipmentList().clone());
				break;
			case COL_NAME:
				if (aPC != null)
				{
					ArrayList anArray = new ArrayList(0);
					for (Iterator e = aPC.getEquipmentList().values().iterator(); e.hasNext();)
					{
						final Equipment anEquip = (Equipment)e.next();	//Hide natural weaponry items so they can't be edited.  The current editing functionality was left in.
						if (!anEquip.getType().startsWith("WEAPON.NATURAL."))
							anArray.add(anEquip);
					}
					d_equipmentModel.changeEquipmentList(anArray);
					//d_equipmentModel.changeEquipmentList(Globals.getCurrentPC().equipmentList().values());

				}
				else
				{
					d_equipmentModel.changeEquipmentList(new ArrayList());
				}
				break;
			case COL_PROF:
				if (aPC != null)
				{
					d_equipmentModel.changeEquipmentList(Equipment.
						selectProficientWeapons(Globals.getEquipmentList()));
				}
				else
				{
					d_equipmentModel.changeEquipmentList(new ArrayList());
				}
				break;
			case COL_TYPE:
				if (aPC != null)
				{
					d_equipmentModel.changeEquipmentList(Equipment.
						selectEquipped(aPC.getEquipmentList().values()));
				}
				else
				{
					d_equipmentModel.changeEquipmentList(new ArrayList());
				}
				break;
			default:
				String type = (String)d_equipType.getSelectedItem();
				if (aPC != null)
				{
					ArrayList anArray = new ArrayList(0);
					for (int i = 0; i < Globals.getEquipmentList().size(); i++)
					{
						anArray.add(Globals.getEquipmentList().get(i));
						final Equipment anEquip = (Equipment)aPC.getEquipmentList().get(((Equipment)anArray.get(i)).getKeyName());
						if (anEquip != null)
							if (anEquip.isHeaderParent())
								anArray.addAll(anEquip.getHeaderChildren());
					}
					d_equipmentModel.changeEquipmentList(Equipment.
						selectEquipment(anArray, type));
				}
				else
					d_equipmentModel.changeEquipmentList(Equipment.
						selectEquipment(Globals.getEquipmentList(), type));
				break;
		}
	}

	private final void updateCharacterGold()
	{
		final PlayerCharacter aPC = Globals.getCurrentPC();
		if (aPC != null)
		{
			try
			{
				final float goldAmt = Float.parseFloat(d_goldAmount.getText());
				if (goldAmt != aPC.getGold().floatValue())
				{
					aPC.setGold(d_goldAmount.getText());
					aPC.setDirty(true);
				}
			}
			catch (NumberFormatException nfe)
			{
				d_goldAmount.setText(aPC.getGold().toString());
			}
		}
	}

	class PopupListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				final int row = d_equipmentGrid.rowAtPoint(e.getPoint());
				final int col = d_equipmentGrid.columnAtPoint(e.getPoint());
				d_equipmentGrid.changeSelection(row, col, false, false);
				//  Set the state of the isEquipped checkbox menu item before displaying.
				final boolean isSelected = (((Integer)d_equipmentModel.getValueAt(row, COL_LOCATION)).intValue() == 2);
				d_popupIsEquipped.setSelected(isSelected);
				d_equipmentPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void updateTotalWeight(Float aFloat)
	{
		final Float roundedValue = new Float((new Float(Math.round(aFloat.floatValue() * 10000))).floatValue() / 10000);
		d_totalWeight.setText(roundedValue.toString());
	}

	private void updateTotalValue(Float aFloat)
	{
		final Float roundedValue = new Float((new Float(Math.round(aFloat.floatValue() * 10000))).floatValue() / 10000);
		d_totalValue.setText(roundedValue.toString() + " " + Globals.getCurrencyDisplay());
	}

	private void formComponentShown(ComponentEvent evt)
	{
		d_goldLabel.setText(Globals.getLongCurrencyDisplay() + ":");
	}
}


/**
 * Popup frame with export options
 */
class EQFrame extends JFrame
{
	EqBuilder mainEq = null;

	public EQFrame()
	{
		super("Item Customizer--NOT FULLY IMPLEMENTED TESTING ONLY");
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img = kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
		loader = null;
		this.setIconImage(img);
		Dimension screenSize = kit.getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		// center frame in screen
		setSize(screenWidth / 2, screenHeight / 2);
		setLocation(screenWidth / 4, screenHeight / 4);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		mainEq = new EqBuilder();
		Container contentPane = getContentPane();
		contentPane.add(mainEq);
	}


	public boolean setEquipment(Equipment aEq)
	{
		if (mainEq != null)
		{
			return mainEq.setEquipment(aEq);
		}
		return false;
	}

}//end EQFrame
