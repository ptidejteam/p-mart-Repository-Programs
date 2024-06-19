/*
 * InfoInventory.java
 * Copyright 2001 (C) Mario Bonassin
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
 * @author  Mario Bonassin
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:02:25 $
 *
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

/**
 *
 * This class is responsible for drawing the equipment related window
 * including indicating what items are available, which ones are selected
 * and handling the selection/de-selection of both.
 *
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */

public class InfoInventory extends FilterAdapterPanel
{
	private final JLabel avaLabel = new JLabel("Available");
	private final JLabel selLabel = new JLabel("Selected");
	private final JLabel goldLabel = new JLabel("Gold: ");
	private JTextField gold = new JTextField();
	private final JLabel capLabel = new JLabel("Capacity: ");
	private JTextField capacity = new JTextField();
	private final JLabel valueLabel = new JLabel("Total Value: ");
	private JTextField totalValue = new JTextField("Temp");
	private final JLabel loadLabel = new JLabel("Load: ");
	private JTextField load = new JTextField();
	private final JLabel weightLabel = new JLabel("Total Weight: ");
	JTextField totalWeight = new JTextField();
	JCheckBox costBox = new JCheckBox("Ignore Cost");
	JCheckBox autoSort = new JCheckBox("Auto-sort output", true);
	private JButton removeButton;
	private JButton addButton;
	JScrollPane eqScroll = new JScrollPane();
	JScrollPane scrollPane;
	JLabelPane infoLabel = new JLabelPane();
	JPanel center = new JPanel();
	JPanel south = new JPanel();
	Border etched;
	TitledBorder titled;
	JSplitPane splitPane;
	JSplitPane bsplit;
	JSplitPane asplit;
	protected EquipmentModel availableModel = null;  // Model for the JTreeTable.
	protected EquipmentModel selectedModel = null;   // Model for the JTreeTable.
	protected JTreeTable availableTable;  // the available Equipment
	protected JTreeTable selectedTable;   // the selected Equipment
	private static boolean needsUpdate = true;
	protected static PlayerCharacter aPC = null;
	private ArrayList d_containersAvailable = new ArrayList();
	private int origLocation = 0;
	protected JComboBox viewComboBox = new JComboBox();
	protected JComboBox viewSelectComboBox = new JComboBox();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private boolean d_shown = false;


	public static final int VIEW_TYPE_SUBTYPE_NAME = 0;		// Type/SubType/Name
	public static final int VIEW_TYPE_NAME = 1;			// Type/Name
	public static final int VIEW_NAME = 2;				// Name
	public static final int VIEW_ALL_TYPES = 3;			// All Types
	static int viewMode = VIEW_TYPE_SUBTYPE_NAME;		// keep track of what view mode we're in for Available
	static int viewSelectMode = VIEW_NAME;			// keep track of what view mode we're in for Selected. defaults to "Name"


	private static final int NUM_COL_AVAILABLE = 3;
	private static final int NUM_COL_SELECTED = 7;

	private static final int COL_NAME = 0;
	private static final int COL_COST = 1;
	private static final int COL_QTY_SRC = 2;
	private static final int COL_CARRIED = 3;
	private static final int COL_LOCATION = 4;
	private static final int COL_HAND = 5;
	private static final int COL_INDEX = 6;

	private static final int EQUIPMENT_NOTCARRIED = 0;
	private static final int EQUIPMENT_CARRIED = 1;
	private static final int EQUIPMENT_EQUIPPED = 2;
	private static final int EQUIPMENT_CONTAINED = 3;

	// Right-click inventory item
	private int selRow;
	private TreePath selPath;
	private boolean hasBeenSized = false;

	EQFrame eqFrame = null;

	private static final String[] loadTypes =
		{
			"LIGHT",
			"MEDIUM",
			"HEAVY",
			"OVERLOADED"
		};

	private static final String[] handTypes =
		{
			"NEITHER",
			"PRIMARY",
			"OFF-HAND",
			"BOTH",
			"Two-Weapons"
		};

	/** typeSubtypeRoot is the base structure used by both the available and selected tables; no
   * need to generate this same list twice.
   */
	public static Object typeSubtypeRoot;
	public static Object typeRoot;
	public static Object allTypeRoot;


	public void forceUpdate()
	{
		needsUpdate = true;
	}

	private class InventoryPopupMenu extends JPopupMenu
	{
		private class InventoryActionListener implements ActionListener
		{
			protected int qty = 0;

			protected InventoryActionListener(int aQty)
			{
				qty = aQty;
			}

			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddInventoryActionListener extends InventoryActionListener
		{
			AddInventoryActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				addEquipment(evt, qty);
			}
		}

		private class RemoveInventoryActionListener extends InventoryActionListener
		{
			RemoveInventoryActionListener(int qty)
			{
				super(qty);
			}

			public void actionPerformed(ActionEvent evt)
			{
				final JTree thisTree = selectedTable.getTree();
				TreePath thisPath = thisTree.getAnchorSelectionPath();
				Object thisObj = null;
				if (thisPath != null)
					thisObj = thisPath.getLastPathComponent();
				Object po = null;
				if (selPath != null)
					po = selPath.getLastPathComponent();
				if (thisObj == null || po == null || !thisObj.equals(po))
					return;
				if (po instanceof PObjectNode)
				{
					Object pe = ((PObjectNode)po).getItem();
					if (pe instanceof Equipment)
					{
						sellSpecifiedEquipment((Equipment)pe, qty);
					}
				}
			}
		}

		private JMenuItem createAddMenuItem(String label, int qty, String accelerator)
		{
			return Utility.createMenuItem(label, new AddInventoryActionListener(qty), "add" + qty, (char)0, accelerator, "Add " + (qty < 0 ? "n" : Integer.toString(qty)) + " to your inventory", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, int qty, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveInventoryActionListener(qty), "remove" + qty, (char)0, accelerator, "Remove " + (qty < 0 ? "n" : Integer.toString(qty)) + " from your inventory", "Remove16.gif", true);
		}

		InventoryPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				InventoryPopupMenu.this.add(createAddMenuItem("Add  1", 1, "control EQUALS"));
				InventoryPopupMenu.this.add(createAddMenuItem("Add  5", 5, null));
				InventoryPopupMenu.this.add(createAddMenuItem("Add 10", 10, null));
				InventoryPopupMenu.this.add(createAddMenuItem("Add 20", 20, null));
				InventoryPopupMenu.this.add(createAddMenuItem("Add  n", -1, "alt N"));
				this.addSeparator();
				InventoryPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							searchButtonClick(availableTable);
						}
					}
					, "searchItem", (char)0, "control F", "Find item", null, true));


				this.addSeparator();
				InventoryPopupMenu.this.add(Utility.createMenuItem("Create custom item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							customizeButtonClick();
						}
					}
					, "newCustomItem", (char)0, "alt C", "Create new customized item", null, true));
				InventoryPopupMenu.this.add(Utility.createMenuItem("Delete custom item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							deleteCustomButtonClick();
						}
					}
					, "deleteItem", (char)0, "DELETE", "Delete custom item", null, true));
/*				InventoryPopupMenu.this.add(Utility.createMenuItem("Create custom item from scratch",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							createGenericItemButtonClick();
						}
					}
					, "createGenericItem", (char)0, "GENERIC", "Create custom item from scratch", null, true));
*/
			}

			else // selectedTable
			{
				/*
				 * jikes says:
				 *   "Ambiguous reference to member 'add' inherited from
				 *    type 'javax/swing/JPopupMenu' but also declared or
				 *    inherited in the enclosing type 'pcgen/gui/InfoInventory'.
				 *    Explicit qualification is required."
				 * Well, let's do what jikes wants us to do ;-)
				 *
				 * author: Thomas Behr 08-02-02
				 */
				InventoryPopupMenu.this.add(createRemoveMenuItem("Remove  1", 1, "control MINUS"));
				InventoryPopupMenu.this.add(createRemoveMenuItem("Remove  5", 5, null));
				InventoryPopupMenu.this.add(createRemoveMenuItem("Remove 10", 10, null));
				InventoryPopupMenu.this.add(createRemoveMenuItem("Remove 20", 20, null));
				InventoryPopupMenu.this.add(createRemoveMenuItem("Remove  n", -1, null));
				this.addSeparator();
				InventoryPopupMenu.this.add(Utility.createMenuItem("Modify Charges",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							editChargesButtonClicked();
						}
					}
					, "editCharges", (char)0, "control ?", "Edit charges", null, true));
				this.addSeparator();
				InventoryPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							searchButtonClick(selectedTable);
						}
					}
					, "searchItem", (char)0, "control F", "Find item", null, true));
				this.addSeparator();
				InventoryPopupMenu.this.add(Utility.createMenuItem("Re-sort output by name",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							int nextOutputIndex = 1;
							ArrayList sortedEquipList = (ArrayList)aPC.getEquipmentList().clone();
							Collections.sort(sortedEquipList, new Comparator()
							{
								// Comparator will be specific to Equipment objects
								public int compare(Object obj1, Object obj2)
								{
									return ((Equipment)obj1).getName().compareTo(((Equipment)obj2).getName());
								}

								// this method isn't used so don't bother
								public boolean equals(Object obj)
								{
									return false;
								}
							});

							Iterator allSelectedEquip = sortedEquipList.iterator();
							for (; allSelectedEquip.hasNext();)
							{
								final Equipment item = (Equipment)allSelectedEquip.next();
								if (item.getOutputIndex() >= 0)
									item.setOutputIndex(nextOutputIndex++);
							}
							selectedTable.updateUI();
						}
					}
					, "sortOutput", (char)0, null, "Sort equipment list for output in alphabetical order", null, true));
			}
		}
	}

	private class InventoryPopupListener extends MouseAdapter
	{
		private JTree tree;
		private InventoryPopupMenu menu;

		InventoryPopupListener(JTreeTable treeTable, InventoryPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				//
				// Walk through the list of accelerators to see if the user has
				// pressed a sequence used by the popup.
				// This would not otherwise happen unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final Component menuComponent = menu.getComponent(i);
							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks = ((JMenuItem)menuComponent).getAccelerator();
								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									((JMenuItem)menuComponent).doClick(2);
									return;
								}
							}
						}
					}
					dispatchEvent(e);
				}

				public void keyReleased(KeyEvent e)
				{
					dispatchEvent(e);
				}
			};
			treeTable.addKeyListener(myKeyListener);
		}

		public void mousePressed(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		public void mouseReleased(MouseEvent evt)
		{
			maybeShowPopup(evt);
		}

		private void maybeShowPopup(MouseEvent evt)
		{
			if (evt.isPopupTrigger())
			{
				selRow = tree.getRowForLocation(evt.getX(), evt.getY());
				if (selRow == -1) return;
				selPath = tree.getPathForLocation(evt.getX(), evt.getY());
				if (selPath == null) return;
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new InventoryPopupListener(treeTable, new InventoryPopupMenu(treeTable)));
	}

	public InfoInventory()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Inventory");

		initComponents();
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	//Set up GridBag Constraints
	void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		//
		// Sanity check
		//
		int iView = SettingsHandler.getInventoryTab_AvailableListMode();
		if ((iView >= VIEW_TYPE_SUBTYPE_NAME) && (iView <= VIEW_ALL_TYPES))
		{
			viewMode = iView;
		}
		SettingsHandler.setInventoryTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getInventoryTab_SelectedListMode();
		if ((iView >= VIEW_TYPE_SUBTYPE_NAME) && (iView <= VIEW_ALL_TYPES))
		{
			viewSelectMode = iView;
		}
		SettingsHandler.setInventoryTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem("Type/SubType/Name");
		viewComboBox.addItem("Type/Name");
		viewComboBox.addItem("Name");
		viewComboBox.addItem("All Types");
		Utility.setDescription(viewComboBox, "You can change how the Equipment in the Tables are listed.");
		viewComboBox.setSelectedIndex(viewMode);			// must be done before createModels call

		viewSelectComboBox.addItem("Type/SubType/Name");
		viewSelectComboBox.addItem("Type/Name");
		viewSelectComboBox.addItem("Name");
		viewSelectComboBox.addItem("All Types");
		Utility.setDescription(viewSelectComboBox, "You can change how the Equipment in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);	// must be done before createModels call

		boolean customExists = Equipment.getEquipmentTypes().contains(Constants.s_CUSTOM);

		typeSubtypeRoot = new PObjectNode();
		typeRoot = new PObjectNode();
		allTypeRoot = new PObjectNode();
		ArrayList aList = new ArrayList();
		ArrayList bList = new ArrayList();
		if (customExists)
		{
			aList.add(Constants.s_CUSTOM);
			bList.add(Constants.s_CUSTOM);
		}
		for (int i = 0; i < Globals.getEquipmentList().size(); i++)
		{
			final Equipment bEq = (Equipment)Globals.getEquipmentList().get(i);
			final StringTokenizer aTok = new StringTokenizer(bEq.getType(), ".", false);
			// we only want the first TYPE to be in the top-level
			if (!aTok.hasMoreTokens())
			{
				continue;
			}
			String aString = aTok.nextToken();
			if (!aList.contains(aString))
				aList.add(aString);
			if (!bList.contains(aString))
				bList.add(aString);
			while (aTok.hasMoreTokens())
			{
				aString = aTok.nextToken();
				if (!bList.contains(aString))
					bList.add(aString);
			}
		}
		Collections.sort(aList);
		Collections.sort(bList);
		PObjectNode[] cc = new PObjectNode[aList.size()];
		PObjectNode[] dc = new PObjectNode[aList.size()];
		for (int i = 0; i < aList.size(); i++)
		{
			cc[i] = new PObjectNode();
			cc[i].setItem(aList.get(i).toString());
			cc[i].setParent((PObjectNode)typeSubtypeRoot);
			dc[i] = new PObjectNode();
			dc[i].setItem(aList.get(i).toString());
			dc[i].setParent((PObjectNode)typeRoot);
		}
		((PObjectNode)typeSubtypeRoot).setChildren(cc, false);
		((PObjectNode)typeRoot).setChildren(dc, false);

		for (int i = 0; i < cc.length; i++)
		{
			aList.clear();
			for (int j = 0; j < Globals.getEquipmentList().size(); j++)
			{
				final Equipment bEq = (Equipment)Globals.getEquipmentList().get(j);
				final String topType = cc[i].toString();
				if (!bEq.isType(topType))
				{
					continue;
				}

				final StringTokenizer aTok = new StringTokenizer(bEq.getType(), ".", false);
				//String aString = aTok.nextToken(); // skip first one, already in top-level
				while (aTok.hasMoreTokens())
				{
					final String aString = aTok.nextToken();
					if (!aString.equals(topType) && !aList.contains(aString))
					{
						aList.add(aString);
					}
				}
			}
			Collections.sort(aList);
			for (Iterator lI = aList.iterator(); lI.hasNext();)
			{
				String aString = (String)lI.next();
				PObjectNode d = new PObjectNode();
				d.setParent(cc[i]);
				cc[i].addChild(d);
				d.setItem(aString);
			}
		}

		PObjectNode[] ec = new PObjectNode[bList.size()];
		for (int i = 0; i < bList.size(); i++)
		{
			ec[i] = new PObjectNode();
			ec[i].setItem(bList.get(i).toString());
			ec[i].setParent((PObjectNode)allTypeRoot);
		}
		((PObjectNode)allTypeRoot).setChildren(ec, false);

		/*
		 * Setup GUI
	       */
		aPC = Globals.getCurrentPC();
		createModels();
// create available table of equipment
		createTreeTables();

		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new JSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		addButton = new JButton(newImage);
		Utility.setDescription(addButton, "Click to add the selected item from the Available list of equipment");
		addButton.setEnabled(false);
		aPanel.add(addButton);
		leftPane.add(aPanel);
		newImage = new ImageIcon(getClass().getResource("resource/Refresh16.gif"));
		JButton sButton = new JButton(newImage);
		Utility.setDescription(sButton, "Click to change orientation of tables");
		sButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (splitOrientation == JSplitPane.VERTICAL_SPLIT)
					splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
				else
					splitOrientation = JSplitPane.VERTICAL_SPLIT;
				splitPane.setOrientation(splitOrientation);
				splitPane.setDividerLocation(.5);
			}
		});
		aPanel.add(sButton);


		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);


		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		removeButton = new JButton(newImage);
		Utility.setDescription(removeButton, "Click to add the selected item from the Selected list of equipment");
		removeButton.setEnabled(false);
		aPanel.add(removeButton);
		aPanel.add(autoSort);
		rightPane.add(aPanel);

		availableTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		availableTable.getColumnModel().getColumn(1).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(0).setPreferredWidth(60);
		selectedTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(1).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(2).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(2).setPreferredWidth(10);
		selectedTable.getColumnModel().getColumn(3).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		selectedTable.getColumnModel().getColumn(3).setPreferredWidth(15);
		selectedTable.getColumnModel().getColumn(COL_INDEX).setCellRenderer(new OutputOrderRenderer());
		selectedTable.getColumnModel().getColumn(COL_INDEX).setPreferredWidth(20);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Equipment Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		eqScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		eqScroll.setViewportView(infoLabel);
		Utility.setDescription(eqScroll, "Any requirements you don't meet are in italics.");

		GridBagLayout gridbag2 = new GridBagLayout();
		GridBagConstraints c2 = new GridBagConstraints();
		south.setLayout(gridbag2);
		south.setMinimumSize(new Dimension(280, 66));

		capacity.setEditable(false);
		capacity.setColumns(4);
		capacity.setBorder(null);
		capacity.setOpaque(false);

		load.setEditable(false);
		load.setColumns(8);
		load.setBorder(null);
		load.setOpaque(false);

		totalWeight.setEditable(false);
		totalWeight.setColumns(4);
		totalWeight.setBorder(null);
		totalWeight.setOpaque(false);

		totalValue.setEditable(false);
		totalValue.setColumns(8);
		totalValue.setBorder(null);
		totalValue.setOpaque(false);

		gold.setColumns(5);

		JPanel fPanel = new JPanel();
		fPanel.setLayout(new BorderLayout());
		JPanel f1Panel = new JPanel();
		f1Panel.setLayout(new BorderLayout());
		f1Panel.add(capLabel, BorderLayout.WEST);
		f1Panel.add(capacity, BorderLayout.EAST);
		fPanel.add(f1Panel, BorderLayout.WEST);
		JPanel f2Panel = new JPanel();
		f2Panel.setLayout(new BorderLayout());
		f2Panel.add(goldLabel, BorderLayout.WEST);
		f2Panel.add(gold, BorderLayout.EAST);
		fPanel.add(f2Panel, BorderLayout.EAST);
		buildConstraints(c2, 0, 0, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(fPanel, c2);
		south.add(fPanel);

		JPanel gPanel = new JPanel();
		gPanel.setLayout(new BorderLayout());
		JPanel g1Panel = new JPanel();
		g1Panel.setLayout(new BorderLayout());
		g1Panel.add(loadLabel, BorderLayout.WEST);
		g1Panel.add(load, BorderLayout.EAST);
		gPanel.add(g1Panel, BorderLayout.WEST);
		JPanel g2Panel = new JPanel();
		g2Panel.setLayout(new BorderLayout());
		g2Panel.add(valueLabel, BorderLayout.WEST);
		g2Panel.add(totalValue, BorderLayout.EAST);
		gPanel.add(g2Panel, BorderLayout.EAST);
		buildConstraints(c2, 0, 1, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(gPanel, c2);
		south.add(gPanel);

		JPanel hPanel = new JPanel();
		hPanel.setLayout(new BorderLayout());
		hPanel.add(weightLabel, BorderLayout.WEST);
		hPanel.add(totalWeight, BorderLayout.CENTER);
		hPanel.add(costBox, BorderLayout.EAST);
		buildConstraints(c2, 0, 2, 1, 1, 0, 0);
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.WEST;
		gridbag2.setConstraints(hPanel, c2);
		south.add(hPanel);
		costBox.setSelected(SettingsHandler.getInventoryTab_IgnoreCost());

		asplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, eqScroll, south);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode)availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode)selectedModel.getRoot(), selectedModel);
	}

	void initActionListeners()
	{
		gold.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				if (gold.getText().length() > 0)
				{
					if (aPC != null)
					{
						aPC.setDirty(true);
						aPC.setGold(gold.getText());
					}
				}
				else if (aPC != null)
				{
					gold.setText(aPC.getGold().toString());
				}
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}

			public void componentResized(ComponentEvent e)
			{
				bsplit.setDividerLocation((int)(InfoInventory.this.getSize().getHeight() - 81));
				asplit.setDividerLocation((int)(InfoInventory.this.getSize().getWidth() - 295));
			}
		});
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeEquipment(evt);
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipment(evt, 1.0f);
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed(evt);
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed(evt);
			}
		});
		costBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				SettingsHandler.setInventoryTab_IgnoreCost(costBox.isSelected());
			}
		});
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setInventoryTab_AvailableListMode(viewMode);
			createAvailableModel();
			availableTable.updateUI();
		}
	}

	private void viewSelectComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setInventoryTab_SelectedListMode(viewSelectMode);
			createSelectedModel();
			selectedTable.updateUI();
		}
	}

	public boolean canAfford(Equipment base,
		Equipment selected,
		float newQty)
	{
		final float currentFunds = ((aPC != null) ? aPC.getGold().floatValue()  : 0);
		final float previousQty = ((selected != null) ? selected.qty().floatValue() : 0);
		return costBox.isSelected() || // ignore cost
			(((newQty - previousQty) * base.getCost().floatValue()) <= currentFunds);
	}


	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Equipment character is not proficient with are in Red.");
		updateCharacterInfo();
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;
		TableColumn acol[] = new TableColumn[NUM_COL_AVAILABLE];
		TableColumn scol[] = new TableColumn[NUM_COL_SELECTED];
		int awidth[] = new int[NUM_COL_AVAILABLE];
		int swidth[] = new int[NUM_COL_SELECTED];
		for (int i = 0; i < NUM_COL_AVAILABLE; i++)
		{
			acol[i] = availableTable.getColumnModel().getColumn(i);
			awidth[i] = acol[i].getWidth();
		}
		for (int i = 0; i < NUM_COL_SELECTED; i++)
		{
			scol[i] = selectedTable.getColumnModel().getColumn(i);
			swidth[i] = scol[i].getWidth();
		}
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoInventory.splitPane", (int)(this.getSize().getWidth() * 6 / 10));
			t = SettingsHandler.getPCGenOption("InfoInventory.bsplit", (int)(this.getSize().getHeight() - 81));
			u = SettingsHandler.getPCGenOption("InfoInventory.asplit", (int)(this.getSize().getWidth() - 295));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("InvSel", i);
				if (width == 0)
					sCol.setPreferredWidth(swidth[i]);
				else
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "InvSel", i));
			}
			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("InvAva", i);
				if (width == 0)
					sCol.setPreferredWidth(swidth[i]);
				else
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "InvAva", i));
			}
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoInventory.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoInventory.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoInventory.asplit", u);
		}

	}

	public void refreshAvailableList(Equipment newEq, boolean purchase, boolean isCurrent)
	{
		//
		// Add new item to available list
		//
		availableModel.addItemToModel(newEq, true);
		availableTable.updateUI();				// Without this, the list doesn't redraw

		//
		// select the item just added
		//
		if (isCurrent)
		{
			if (availableTable.search(newEq.getName(), true) != null)
			{
				availableTable.requestFocus();
			}
		}
		//
		// Attempt to purchase if that was requested
		//
		if (purchase)
		{
			addEquipment(null, 1.0f);
		}
		//
		// TODO: need to resync the selected list to add any types that have just been added
		//
	}

	// This recalculates the states of everything based upon the currently selected
	// character.
	public void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC == null || (!needsUpdate && aPC == bPC))
			return;
		aPC = bPC;
		aPC.aggregateFeatList();
		createModels();
		availableTable.updateUI();
		prepareAvailableContainers();
		final TableColumn col_location = selectedTable.getColumnModel().getColumn(COL_LOCATION);
		col_location.setCellRenderer(new LocationRenderer(locationChoices()));
		col_location.setCellEditor(new LocationEditor(locationChoices()));

		selectedTable.updateUI();
		gold.setText(aPC.getGold().toString());
		updateTotalWeight();
		updateTotalValue();
		capacity.setText(Globals.maxLoadForStrength(
			aPC.adjStats(Constants.STRENGTH)).
			toString());
		needsUpdate = false;
	}

	private void deleteCustomButtonClick()
	{
		final int currentRow = availableTable.getSelectedRow();
		if (currentRow >= 0)
		{
			int row = availableTable.getSelectionModel().getAnchorSelectionIndex();
			TreePath treePath = availableTable.tree.getPathForRow(row);
			Object eo = treePath.getLastPathComponent();
			PObjectNode e = (PObjectNode)eo;
			if (!(e.getItem() instanceof Equipment))
			{
				JOptionPane.showMessageDialog(null, "Cannot delete types.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			Equipment aEq = (Equipment)e.getItem();
			if (!aEq.isType("CUSTOM"))
			{
				JOptionPane.showMessageDialog(null, "Can only delete custom items.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			ArrayList whoHasIt = new ArrayList();
			for (Iterator pcIterator = Globals.getPCList().iterator(); pcIterator.hasNext();)
			{
				final PlayerCharacter aPc = (PlayerCharacter)pcIterator.next();
				if (aPc.getEquipmentNamed(aEq.getName()) != null)
				{
					whoHasIt.add(aPc.getName());
				}
			}
			if (whoHasIt.size() != 0)
			{
				String whose = whoHasIt.toString();
				whose = whose.substring(1, whose.length() - 1);
				JOptionPane.showMessageDialog(null, "Can only delete items that are in no character's possession. " +
					"The following character(s) have this item in their possession:\n" + whose, Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			aEq = Globals.getEquipmentKeyed(aEq.getKeyName());
			if (aEq != null)
			{
				//
				// Give user a chance to bail
				//
				if (JOptionPane.showConfirmDialog(null, "Delete " + aEq.getName() + " from database?", Constants.s_APPNAME, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
				{
					return;
				}

				Globals.getEquipmentList().remove(aEq);
				//
				// This will unexpand all expanded nodes
				// TODO: be a little less draconian and remember what's expanded
				// TODO: sneak onto all other character's Inventory tabs and refresh the available list
				createAvailableModel();
				availableTable.updateUI();
			}
		}
	}

	private void openCustomizer(Equipment aEq)
	{
		if (aEq != null)
		{
			if (eqFrame == null)
			{
				eqFrame = new EQFrame();
			}
			if (eqFrame.setEquipment(aEq))
			{
				eqFrame.setVisible(true);
				eqFrame.toFront();
			}
		}
	}

	private void createGenericItemButtonClick()
	{
		Equipment aEq = new Equipment();
		aEq.setName(Constants.s_GENERIC_ITEM);
		openCustomizer(aEq);
	}

	private void customizeButtonClick()
	{
		if (!addButton.isEnabled())
		{
			return;
		}

		final int currentRow = availableTable.getSelectedRow();
		if (currentRow >= 0)
		{
			int row = availableTable.getSelectionModel().getAnchorSelectionIndex();
			TreePath treePath = availableTable.tree.getPathForRow(row);
			Object eo = treePath.getLastPathComponent();
			PObjectNode e = (PObjectNode)eo;
			if (!(e.getItem() instanceof Equipment))
			{
				JOptionPane.showMessageDialog(null,
					"Can only customise items, not types.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
			final Equipment aEq = (Equipment)e.getItem();
			openCustomizer(aEq);
		}
	}

	private void editChargesButtonClicked()
	{
		final int currentRow = selectedTable.getSelectedRow();
		if (currentRow >= 0)
		{
			int row = selectedTable.getSelectionModel().getAnchorSelectionIndex();
			TreePath treePath = selectedTable.tree.getPathForRow(row);
			Object eo = treePath.getLastPathComponent();
			PObjectNode e = (PObjectNode)eo;
			if (!(e.getItem() instanceof Equipment))
			{
				return;
			}
			final Equipment aEq = (Equipment)e.getItem();

			final int minCharges = aEq.getMinCharges();
			if (minCharges > 0)
			{
				final int maxCharges = aEq.getMaxCharges();
				for (; ;)
				{
					Object selectedValue = JOptionPane.showInputDialog(null,
						"Enter Number of Charges (" + Integer.toString(minCharges) + "-" + Integer.toString(maxCharges) + ")",
						Constants.s_APPNAME,
						JOptionPane.INFORMATION_MESSAGE,
						null,
						null,
						Integer.toString(aEq.getRemainingCharges()));

					if (selectedValue == null)
					{
						return;
					}
					try
					{
						final String aString = ((String)selectedValue).trim();
						int charges = Integer.parseInt(aString);
						if (charges < minCharges)
						{
							continue;
						}
						if (charges > maxCharges)
						{
							continue;
						}
						aEq.setRemainingCharges(charges);
						selectedTable.updateUI();
						return;
					}
					catch (Exception exc)
					{
					}
				}
			}

			JOptionPane.showMessageDialog(null, "This item cannot hold charges.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	private String lastSearch = "";

	private void searchButtonClick(JTreeTable tbl)
	{
		Object selectedValue = JOptionPane.showInputDialog(null,
			"Enter the name of the item to find",
			Constants.s_APPNAME,
			JOptionPane.INFORMATION_MESSAGE,
			null,
			null,
			lastSearch);
		if (selectedValue != null)
		{
			String aString = ((String)selectedValue);
			lastSearch = aString;
			if (aString.length() != 0)
			{
				if (tbl.search(aString, true) != null)
				{
					tbl.requestFocus();
				}
			}
		}
	}

	private void updateTotalWeight()
	{
		if (aPC == null)
		{
			return;
		}
		final Float weight = aPC.totalWeight();
		//Math.round returns an int if you pass it a Float, which creates a max number of ~214K in this calculation
		//passing it a double value causes Math.round to return a long, which preserves enough digits that we have a much higher limit now
		final Float roundedValue = new Float((new Float(Math.round(weight.doubleValue() * 10000))).floatValue() / 10000);
		totalWeight.setText(roundedValue.toString());
		load.setText(loadTypes[Globals.loadTypeForStrength(aPC.adjStats(Constants.STRENGTH), weight)]);
	}

	private void updateTotalValue()
	{
		final Float aFloat = aPC.totalValue();
		//Math.round returns an int if you pass it a Float, which creates a max number of ~214K in this calculation
		//passing it a double value causes Math.round to return a long, which preserves enough digits that we have a much higher limit now
		final Float roundedValue = new Float((new Float(Math.round(aFloat.doubleValue() * 10000))).floatValue() / 10000);
		totalValue.setText(roundedValue.toString() + " " + Globals.getCurrencyDisplay());
	}

	private void setInfoLabelText(Equipment aEq)
	{
		if (aEq != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>").append(aEq.getName()).append("</b>");
			if (!aEq.longName().equals(aEq.getName()))
				b.append("(").append(aEq.longName()).append(")");
			b.append(" &nbsp;<b>TYPE</b>:").append(aEq.getType());
			b.append(" <b>PROFICIENT</b>:").append(((aPC.isProficientWith(aEq) && aEq.meetsPreReqs()) ? "Y" : "<font color='red'>N</font>"));
			final String cString = aEq.preReqHTMLStrings(false);
			if (cString.length() > 0)
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			String IDS = aEq.getInterestingDisplayString();
			if (IDS.length() > 0)
				b.append(" &nbsp;<b>Properties</b>:").append(IDS);
			String bString = aEq.getWeight().toString();
			if (bString.length() > 0)
				b.append(" <b>WT</b>:").append(bString);
			Integer a = aEq.getMaxDex();
			if (a.intValue() != 100)
				b.append(" <b>MAXDEX</b>:").append(a.toString());
			a = aEq.acCheck();
			if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
			{
				b.append(" <b>ACCHECK</b>:").append(a.toString());
			}
			if (!Globals.isStarWarsMode())
			{
				a = aEq.spellFailure();
				if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>Arcane Failure</b>:").append(a.toString());
				}
			}
			if (Globals.isStarWarsMode())
			{
				a = aEq.eDR();
				if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>Damage Resistance</b>:").append(a.toString());
				}
			}
			bString = aEq.moveString();
			if (bString.length() > 0)
			{
				b.append(" <b>Move</b>:").append(bString);
			}
			bString = aEq.getSize();
			if (bString.length() > 0)
				b.append(" <b>Size</b>:").append(bString);
			bString = aEq.getDamage();
			if (bString.length() > 0)
			{
				b.append(" <b>Damage</b>:").append(bString);
				if (aEq.isDouble())
					b.append('/').append(aEq.getAltDamage());
			}
			bString = aEq.getCritRange();
			if (bString.length() > 0)
			{
				b.append(" <b>Crit Range</b>:").append(bString);
				if (aEq.isDouble() && !aEq.getCritRange().equals(aEq.getAltCritRange()))
					b.append('/').append(aEq.getAltCritRange());
			}
			bString = aEq.getCritMult();
			if (bString.length() > 0)
			{
				b.append(" <b>Crit Mult</b>:").append(bString);
				if (aEq.isDouble() && !aEq.getCritMult().equals(aEq.getAltCritMult()))
					b.append('/').append(aEq.getAltCritMult());
			}
			if (aEq.isWeapon())
			{
				bString = aEq.getRange().toString();
				if (bString.length() > 0)
				{
					b.append(" <b>Range</b>:").append(bString);
				}
			}
			//
			// This is EXACTLY the same as Properties
			//
			//bString = aEq.getInterestingDisplayString();
			//if (bString.length() > 0)
			//	b.append(" <b>Bonuses</b>:").append(bString);
			bString = aEq.getContainerCapacityString();
			if (bString.length() > 0)
				b.append(" <b>Container</b>:").append(bString);
			bString = aEq.getContainerContentsString();
			if (bString.length() > 0)
				b.append(" <b>Currently Contains</b>:").append(bString);
			bString = aEq.getSource();
			final int charges = aEq.getRemainingCharges();
			if (charges >= 0)
			{
				b.append(" <b>Charges</b>:").append(charges);
			}
			if (bString.length() > 0)
				b.append(" <b>SOURCE</b>:").append(bString);
			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else
			infoLabel.setText();
	}

	private int getEventSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	protected void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					String aString = null;
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect class when use cursor keys to navigate the tree
					//
					//final Object temp = availableTable.getTree().getLastSelectedPathComponent();
					final int idx = getEventSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp != null)
					{
						//
						// Only display information about equipment, not equipment with
						// same name as type
						//
						final PObjectNode pobjn = (PObjectNode)temp;
						if (!(pobjn.getItem() instanceof Equipment))
						{
							setInfoLabelText(null);
							return;
						}
						aString = temp.toString();
					}
					else
					{
						// This will popup if displaying by name only and we add an item to the available list
						// after customizing, so I've removed it---Byngl
						//
						//JOptionPane.showMessageDialog(null,
						//	"No equipment selected! Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}

					final Equipment aEq = Globals.getEquipmentNamed(aString.substring(aString.lastIndexOf("|") + 1));
					addButton.setEnabled(aEq != null);
					setInfoLabelText(aEq);
				}
			}
		});
		availableTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		final JTree tree = availableTable.getTree();

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());


		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = tree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						tree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						addEquipment(null, 1.0f);
					}
				}
			}
		};
		tree.addMouseListener(ml);

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.getColumnModel().getColumn(5).setCellRenderer(new HandRenderer(handTypes));
		selectedTable.getColumnModel().getColumn(5).setCellEditor(new HandEditor(handTypes));

//Code for Multiple Locations
		prepareAvailableContainers();
		String[] locationsAvailable = locationChoices();
		selectedTable.getColumnModel().getColumn(4).setCellRenderer(new LocationRenderer(locationsAvailable));
		selectedTable.getColumnModel().getColumn(4).setCellEditor(new LocationEditor(locationsAvailable));

		selectedTable.getColumnModel().getColumn(COL_LOCATION).
			setCellRenderer(new LocationRenderer(locationChoices()));
		selectedTable.getColumnModel().getColumn(COL_LOCATION).
			setCellEditor(new LocationEditor(locationChoices()));


		selectedTable.getColumnModel().getColumn(COL_QTY_SRC).
			setCellEditor(new QuantityEditor());
		selectedTable.getColumnModel().getColumn(COL_CARRIED).
			setCellEditor(new QuantityEditor());
		selectedTable.getColumnModel().getColumn(COL_INDEX).
			setCellEditor(new OutputOrderEditor(new String[]{"First", "Last", "Hidden"}));

		final JTree btree = selectedTable.getTree();
		btree.setRootVisible(false);
		btree.setShowsRootHandles(true);
		btree.setCellRenderer(new LabelTreeCellRenderer());
		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					String aString = null;
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect class when use cursor keys to navigate the tree
					//
					//final Object temp = selectedTable.getTree().getLastSelectedPathComponent();
					final int idx = getEventSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp != null)
					{
						//
						// Only display information about equipment, not equipment with
						// same name as type
						//
						final PObjectNode pobjn = (PObjectNode)temp;
						if (!(pobjn.getItem() instanceof Equipment))
						{
							setInfoLabelText(null);
							return;
						}
						aString = temp.toString();
					}
					else
						infoLabel.setText();

					final Equipment aEq = aPC.getEquipmentNamed(aString.substring(aString.lastIndexOf("|") + 1));
					removeButton.setEnabled(aEq != null);
					setInfoLabelText(aEq);
				}
			}
		});
		selectedTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						btree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						removeEquipment(null);
					}
				}
			}
		};
		btree.addMouseListener(ml);

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void removeEquipment(ActionEvent evt)
	{
		final int row = selectedTable.getSelectionModel().getAnchorSelectionIndex();
		final TreePath treePath = selectedTable.tree.getPathForRow(row);
		final Object eo = treePath.getLastPathComponent();
		final PObjectNode e = (PObjectNode)eo;
		Object item = e.getItem();
		if (item instanceof Equipment)
		{
			if (((Equipment)item).getHeaderChildCount() == 0)
			{
				if (((Equipment)item).getChildCount() == 0)
				{
					removeSpecifiedEquipment((Equipment)item);
				}
				else
				{
					JOptionPane.showMessageDialog(null,
						"Cannot remove container unless it is empty.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				final String itemName = ((Equipment)item).getName();
				JOptionPane.showMessageDialog(null,
					"You cannot decrease the number of containers using the header,\nremove them by decreasing the quantity of the sub-container, i.e. \"" + itemName + " -2-\" not \"" + itemName + "\"", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else if (evt != null)
		{
			JOptionPane.showMessageDialog(null,
				"You can only remove equipment - not types", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addEquipment(ActionEvent evt, float newQty)
	{
		String aString = null;
		Object temp = availableTable.getTree().getLastSelectedPathComponent();
		if ((temp != null) && (temp instanceof PObjectNode))
		{
			final PObjectNode e = (PObjectNode)temp;
			if (!(e.getItem() instanceof Equipment))
			{
				return;	// Ignore (dbl-click will cause this, want to expand/unexpand tree)
			}
			aString = temp.toString();
		}
		else
		{
			JOptionPane.showMessageDialog(null,
				"No equipment selected. Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		//
		// Get a number from the user via a popup
		//
		if (newQty == -1.0)
		{
			Object selectedValue = JOptionPane.showInputDialog(null,
				"Enter Quantity",
				Constants.s_APPNAME,
				JOptionPane.QUESTION_MESSAGE);
			if (selectedValue != null)
			{
				try
				{
					newQty = new Float(((String)selectedValue).trim()).floatValue();
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				return;
			}
		}
		try
		{
			final Equipment selectedEquipment = Globals.getEquipmentNamed(aString.substring(aString.lastIndexOf("|") + 1));
			buySpecifiedEquipment(selectedEquipment, newQty);
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(null, "addEquipment: Exception:" + exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	private void removeSpecifiedEquipment(Equipment selectedEquipment)
	{
		selectedEquipment.setNumberCarried(new Float(0));
		selectedEquipment.setIsEquipped(false);
		selectedEquipment.setHand(Equipment.NEITHER_HAND);
		final Equipment eqParent = (Equipment)selectedEquipment.getParent();
		if (eqParent != null)
		{
			eqParent.removeChild(selectedEquipment);
		}

		adjustGold(selectedEquipment, selectedEquipment.qty().floatValue());
		aPC.getEquipmentList().remove(selectedEquipment);
		selectedModel.removeItemFromNodes(null, selectedEquipment);
		//
		// Get rid of item from parent
		//
		Equipment headerParent = selectedEquipment.getHeaderParent();
		if (headerParent != null)
		{
			//
			// Remove all children from equipment list, remove the item, re-key the rest and then re-add to list
			//
			// There's probably a prettier solution to this, but I'll be damned if I can find it.
			// Containers can still become unsorted in selected list when > 10 involved.
			//
			for (int x = 0; x < headerParent.getHeaderChildCount(); x++)
			{
				final Equipment aChild = headerParent.getHeaderChild(x);
				aPC.getEquipmentList().remove(aChild);
			}

			headerParent.removeHeaderChild(selectedEquipment);	// will re-key children

			if (headerParent.getHeaderChildCount() == 1)
			{
				Equipment item = headerParent.getHeaderChild(0);
				headerParent.collapseHeaderParent();
				aPC.getEquipmentList().remove(item);
				selectedModel.removeItemFromNodes(null, item);
			}

			for (int x = 0; x < headerParent.getHeaderChildCount(); x++)
			{
				final Equipment aChild = headerParent.getHeaderChild(x);
				aPC.getEquipmentList().add(aChild);
			}
		}
		prepareAvailableContainers();
		final TableColumn col_location = selectedTable.getColumnModel().getColumn(COL_LOCATION);
		col_location.setCellRenderer(new LocationRenderer(locationChoices()));
		col_location.setCellEditor(new LocationEditor(locationChoices()));
		selectedTable.updateUI();
		updateTotalWeight();
		updateTotalValue();
		// update skills tab as equipment may have had a bonus to skills
		Globals.getRootFrame().forceUpdate_InfoSkills();
		// update Equipping tab to display new equipment
		Globals.getRootFrame().forceUpdate_InfoEquipping();
	}

	private void sellSpecifiedEquipment(Equipment selectedEquipment, float qty)
	{
		//
		// Get a number from the user via a popup
		//
		float sellQty = qty;
		if (sellQty < 0.0f)
		{
			Object selectedValue = JOptionPane.showInputDialog(null,
				"Enter Quantity",
				Constants.s_APPNAME,
				JOptionPane.QUESTION_MESSAGE);
			if (selectedValue != null)
			{
				try
				{
					sellQty = Float.parseFloat(((String)selectedValue).trim());
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				return;
			}
		}


		if (selectedEquipment.getHeaderChildCount() == 0)
		{
			if (selectedEquipment.getChildCount() == 0)
			{
				final float newQty = selectedEquipment.qty().floatValue() - sellQty;
				if (!selectedEquipment.acceptsChildren() || ((newQty % 1) == 0))
				{
					if (newQty <= 0.0f)
					{
						removeSpecifiedEquipment(selectedEquipment);
					}
					else
					{
						selectedModel.setValueForItemInNodes(null, selectedEquipment, newQty, COL_QTY_SRC);
//						selectedModel.setValueAt(newFloat, po, COL_QTY_SRC);
						final JTree thisTree = selectedTable.getTree();
						int[] selectedRows = thisTree.getSelectionRows();
						selectedTable.updateUI();
						thisTree.setSelectionRows(selectedRows);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null,
						"You cannot buy, own or carry non-integral numbers of containers\ni.e. Half a sack is nonsensical.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Cannot remove container unless it is empty.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			final String itemName = selectedEquipment.getName();
			JOptionPane.showMessageDialog(null,
				"You cannot decrease the number of containers using the header,\nremove them by decreasing the quantity of the sub-container, i.e. \"" + itemName + " -2-\" not \"" + itemName + "\"", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private void buySpecifiedEquipment(Equipment selectedEquipment, float newQty)
	{
		try
		{
			Equipment baseEquipment = (Equipment)aPC.getEquipmentNamed(selectedEquipment.getKeyName());

			if (selectedEquipment.getModifiersRequired())
			{
				if ((selectedEquipment.getEqModifierList(true).size() == 0) && (selectedEquipment.getEqModifierList(false).size() == 0))
				{
					JOptionPane.showMessageDialog(null,
						"You cannot buy this item as is; you must \"customize\" it first.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if (selectedEquipment.acceptsChildren() && (newQty % 1) != 0)
			{
				JOptionPane.showMessageDialog(null,
					"You cannot buy, own or carry non-integral numbers of containers\ni.e. Half a sack is nonsensical.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			final float oldQty = (baseEquipment == null) ? 0.0f : baseEquipment.qty().floatValue();
			if (canAfford(selectedEquipment, baseEquipment, newQty))
			{
				//Begin multicontainers code.
				final boolean tempHeaderParent = (baseEquipment == null) ? false : baseEquipment.getHasHeaderParent();
				if (selectedEquipment.acceptsChildren() && !tempHeaderParent)
				{
					//
					// Had none, adding first
					//
					if (oldQty == 0)
					{
						baseEquipment = (Equipment)selectedEquipment.clone();
						baseEquipment.setQty(new Float(1));
						aPC.getEquipmentList().add(baseEquipment);
					}

					if (baseEquipment != null)
					{
						if ((oldQty + newQty) > 1)
						{
							int numHeaderChildren = baseEquipment.getHeaderChildCount();
							for (int i = numHeaderChildren; i < (oldQty + newQty); i++)
							{
								final Equipment newHeaderChild = (Equipment)baseEquipment.createHeaderParent();
								if (i > 0)  //newly added containers are empty, excepting -1-, because it is really the original container.
								{
									newHeaderChild.setNumberCarried(new Float(0));
									newHeaderChild.setIsEquipped(false);
									newHeaderChild.setHand(Equipment.NEITHER_HAND);
									newHeaderChild.clearChildTypes();
									newHeaderChild.setChildType("Total", new Float(0));
									newHeaderChild.clearContainedEquipment();
								}

								newHeaderChild.updateContainerContentsString();
								newHeaderChild.clearHeaderChildren();
								aPC.getEquipmentList().add(newHeaderChild);
								selectedModel.addItemToModel(newHeaderChild, true);
							}
							selectedTable.updateUI();				// Without this, the list doesn't redraw
						}
						prepareAvailableContainers();
						aPC.setDirty(true);
					}
				}
				else if ((baseEquipment != null) && tempHeaderParent)
				{
					JOptionPane.showMessageDialog(null,
						"You cannot increase the number of containers using the sub-container,\nadd them by increasing the quantity of the header, ie \"Backpack\" not \"Backpack -2-\"", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				else
				{
					baseEquipment = adjustBelongings(selectedEquipment, baseEquipment, oldQty + newQty, -1);
				}

				//
				// Add (new) equipment to selected table
				//
				if ((oldQty == 0) && (baseEquipment != null))
				{
					selectedModel.addItemToModel(baseEquipment, true);
					selectedTable.updateUI();				// Without this, the list doesn't redraw
					if (selectedTable.search(baseEquipment.getName(), true) != null)
					{
						selectedTable.requestFocus();
					}
				}

				if (selectedEquipment.acceptsChildren())
				{
					prepareAvailableContainers();
					final TableColumn col_location = selectedTable.getColumnModel().getColumn(COL_LOCATION);
					col_location.setCellRenderer(new LocationRenderer(locationChoices()));
					col_location.setCellEditor(new LocationEditor(locationChoices()));
				}

//End multicontainers code

				updateTotalWeight();
				adjustGold(selectedEquipment, -newQty);
				updateTotalValue();
				// update skills tab because equipment
				// may have a bonus to skills
				Globals.getRootFrame().forceUpdate_InfoSkills();
				// update Equipping tab
				Globals.getRootFrame().forceUpdate_InfoEquipping();
			}
			else
			{
				JOptionPane.showMessageDialog
					(null,
						"Insufficient funds for purchase of " +
					newQty + " " + selectedEquipment.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(null, "buySpecifiedEquipment: Exception:" + exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	private void adjustGold(Equipment base, float diffQty)
	{
		if (!costBox.isSelected() && aPC != null)
		{
			aPC.setDirty(true);
			aPC.adjustGold((diffQty * base.getCost().floatValue()));
			gold.setText(aPC.getGold().toString());
		}
	}

	private Equipment adjustBelongings(Equipment base, Equipment selected, float newQty, int row)
	{
		Iterator allSelectedEquip;
		int nextOutputIndex = 1;

		if (aPC != null)
		{
			aPC.setDirty(true);
			if (newQty <= 0)
			{
				if (selected != null)
				{
					if (selected.getParent() != null)
					{
						final Equipment aParent = (Equipment)selected.getParent();
						aParent.removeChild(selected);
					}
					aPC.getEquipmentList().remove(selected);
					if (newQty == 0.0)
						removeEquipment(null);
				}
			}
			else
			{
				if (selected == null)
				{
					selected = (Equipment)base.clone();
					if (selected != null)
					{
						//This was added to keep jlint happy.
						aPC.getEquipmentList().add(selected);
					}
				}
				if (selected != null)
				{
					//This was added to keep jlint happy.
					selected.setQty(new Float(newQty));
					selectedModel.setValueForItemInNodes(null, selected, newQty, COL_QTY_SRC);

					// Now re calc the output order
					if (autoSort.isSelected())
					{
						allSelectedEquip = aPC.getEquipmentListInOutputOrder().iterator();
						for (; allSelectedEquip.hasNext();)
						{
							final Equipment item = (Equipment)allSelectedEquip.next();
							if (item.getOutputIndex() >= 0)
								item.setOutputIndex(nextOutputIndex++);
						}
					}
					else
					{
						if (selected.getOutputIndex() == 0)
							selected.setOutputIndex(getHighestOutputIndex() + 1);
					}

					selectedTable.updateUI();
				}
			}
		}
		return selected;
	}

	/**
	 * Creates the EquipmentModel that will be used.
	 */
	protected void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	protected void createAvailableModel()
	{
		if (availableModel == null)
			availableModel = new EquipmentModel(viewMode, true);
		else
			availableModel.resetModel(viewMode, true, false);
		if (availableSort != null)
			availableSort.setRoot((PObjectNode)availableModel.getRoot());
		if (availableSort != null)
			availableSort.setRoot((PObjectNode)availableModel.getRoot());
	}

	protected void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new EquipmentModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
		if (selectedSort != null)
			selectedSort.setRoot((PObjectNode)selectedModel.getRoot());
		if (selectedSort != null)
			selectedSort.setRoot((PObjectNode)availableModel.getRoot());
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

	private void prepareAvailableContainers()
	{

		d_containersAvailable.clear();
		if (aPC != null)
		{
			for (Iterator e = aPC.getEquipmentList().iterator(); e.hasNext();)
			{
				final Equipment anEquip = (Equipment)e.next();

				if (anEquip.acceptsChildren() && !(anEquip.isHeaderParent()))
					d_containersAvailable.add(anEquip);
			}
		}
	}


	/**
	 * Retrieve the highest output index used in any of the
	 * character's equipment.
	 */
	private int getHighestOutputIndex()
	{
		int maxOutputIndex = 0;

		if (aPC == null)
			return 0;

		for (Iterator i = aPC.getEquipmentList().iterator(); i.hasNext();)
		{
			final Equipment item = (Equipment)i.next();
			if (item.getOutputIndex() > maxOutputIndex)
			{
				maxOutputIndex = item.getOutputIndex();
			}
		}

		return maxOutputIndex;
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
			if (value == null)
				return null;
			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i < 0 || i >= this.getItemCount())
					i = 0;
				setSelectedIndex(i);
			}
			return this;
		}

	}

	private static final class HandEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
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
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null) return null;
			d_originalValue = getSelectedIndex();
			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i < 0 || i >= this.getItemCount())
					i = 0;
				setSelectedIndex(i);
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

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
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

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
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
				int i = ((Integer)value).intValue();
				if (i < 0 || i >= this.getItemCount())
					i = 0;
				setSelectedIndex(i);
			}
			return this;
		}

	}

	private final class LocationEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
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
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null) return this;
			d_originalValue = getSelectedIndex();
			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i < 0 || i >= this.getItemCount())
					i = 0;
				setSelectedIndex(i);
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

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
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

		public boolean shouldSelectCell(EventObject eventObject)
		{
			return true;
		}

		private void fireEditingCanceled()
		{
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

//End LocationEditor classes



	private final class QuantityEditor extends JTextField implements TableCellEditor
	{
		private final transient ArrayList d_listeners = new ArrayList();
		private transient String d_originalValue = "";

		public QuantityEditor()
		{
			super();
			this.setAlignmentX(QuantityEditor.RIGHT_ALIGNMENT);
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
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

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
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
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	}


	/**
	 * OutputOrderRenderer is a small extension of the standard JLabel based
	 * table cell renderer that allows it to interpret a few special values.
	 * -1 shows as Hidden, and 0 is shown as blank. Any other value is
	 * displayed as is.
	 */
	private final class OutputOrderRenderer extends DefaultTableCellRenderer
	{

		public OutputOrderRenderer()
		{
			super();
			Globals.debugPrint("OOR - cons");
			setHorizontalAlignment(SwingConstants.CENTER);
		}


		public Component getTableCellRendererComponent(JTable jTable,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column)
		{
			JLabel comp = (JLabel)super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i == -1)
					comp.setText("Hidden");
				else if (i == 0)
					comp.setText("");
				else
					comp.setText(String.valueOf(i));
			}
			return comp;
		}

	}


	/**
	 * OutputOrderEditor is a JCombobox based table cell editor. It allows the user
	 * to either enter their own output order index, or to select from hidden, first
	 * or last. If first or last are selected, then special values are returned to
	 * the setValueAt method, which are actioned by that method.
	 */
	private final class OutputOrderEditor extends JComboBox implements TableCellEditor
	{

		private final transient ArrayList d_listeners = new ArrayList();
		private transient int d_originalValue = 0;

		public OutputOrderEditor(String[] choices)
		{
			super(choices);

			setEditable(true);

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
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
		{
			if (value == null) return this;
			d_originalValue = this.getSelectedIndex();
			if (value instanceof Integer)
			{
				int i = ((Integer)value).intValue();
				if (i == -1)
					setSelectedItem("Hidden");
				else
					setSelectedItem(String.valueOf(i));
			}
			else
			{
				setSelectedItem("Hidden");
			}
			jTable.setRowSelectionInterval(row, row);
			jTable.setColumnSelectionInterval(column, column);
			return this;
		}

		public void cancelCellEditing()
		{
			fireEditingCanceled();
		}

		public boolean isCellEditable(EventObject eventObject)
		{
			return true;
		}

		public void removeCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.remove(cellEditorListener);
		}

		public Object getCellEditorValue()
		{
			switch (this.getSelectedIndex())
			{
				case 0: // First
					return new Integer(0);

				case 1: // Last
					return new Integer(1000);

				case 2: // Hidden
					return new Integer(-1);

				default: // A number
					return new Integer((String)getSelectedItem());
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
			setSelectedIndex(d_originalValue);
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener)d_listeners.get(i)).editingStopped(ce);
			}
		}
	} //End OutputOrderEditor classes


	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *  object.  This root object has a null <code>parent</code>.  All other objects
	 *  have a parent which points to a non-null object.  parent objects contain a list of
	 *  <code>children</code>, which are all the objects that point to it as their parent.
	 *  objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *  linked list).  nodes which have at least 1 child are not leafs. Leafs are like files
	 *  and non-leafs are like directories.
	 */
	public class EquipmentModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// Types of the columns.
		protected Class[] cTypes = {TreeTableModel.class, String.class};
		protected int modelType = 0; // availableModel=0,selectedModel=1
		protected int currentMode = VIEW_TYPE_SUBTYPE_NAME;

		/**
		 * Creates a EquipmentModel
		 */
		public EquipmentModel(int mode, boolean available)
		{
			super(null);
			if (!available)
				modelType = 1;
			resetModel(mode, available, true);
		}


		public void addItemToModel(Equipment aEq, boolean fireEvent)
		{
			PObjectNode rootAsPObjectNode = (PObjectNode)root;
			if (aEq != null)
			{
				if (!shouldDisplayThis(aEq))
				{
					return;
				}

				if (fireEvent)
				{

					boolean bInserted = false;
					if (aEq.isType(Constants.s_CUSTOM))
					{
						addChild(Constants.s_CUSTOM, typeSubtypeRoot, true);
						bInserted |= addChild(Constants.s_CUSTOM, typeRoot, true);

						if (currentMode != VIEW_NAME)
						{
							addChild(Constants.s_CUSTOM, root, true);
						}
					}

					//
					// We need to update the base tables so that the next time the model is
					// selected the new types don't disappear
					//
					String type = aEq.typeIndex(0);

					// Type/SubType/Name
					addChild(type, typeSubtypeRoot, true);

					//
					// Now add any missing subtypes to type/subtype/name tree
					//
					PObjectNode typeSubtypeRootAsPObjectNode = (PObjectNode)typeSubtypeRoot;
					for (Iterator e = aEq.typeList().iterator(); e.hasNext();)
					{
						type = (String)e.next();
						for (int i = 0; i < typeSubtypeRootAsPObjectNode.getChildCount(); i++)
						{
							final String treeType = typeSubtypeRootAsPObjectNode.getChild(i).toString();
							if ((typeSubtypeRootAsPObjectNode.getChild(i).getItem() instanceof PObject) || !aEq.isType(treeType) || type.equals(treeType))
							{
								continue;
							}
							addChild(type, typeSubtypeRootAsPObjectNode.getChild(i), true);
						}
					}

					// Type/Name
					addChild(type, typeRoot, true);

					// All types
					for (Iterator e = aEq.typeList().iterator(); e.hasNext();)
					{
						type = (String)e.next();
						addChild(type, allTypeRoot, true);
					}

					switch (currentMode)
					{
						case VIEW_TYPE_SUBTYPE_NAME: // Type/SubType/Name
							bInserted |= addChild(type, root, true);
							for (Iterator e = aEq.typeList().iterator(); e.hasNext();)
							{
								type = (String)e.next();
								for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
								{
									final String treeType = rootAsPObjectNode.getChild(i).toString();
									if ((rootAsPObjectNode.getChild(i).getItem() instanceof PObject) || !aEq.isType(treeType) || type.equals(treeType))
									{
										continue;
									}
									bInserted |= addChild(type, rootAsPObjectNode.getChild(i), true);
								}
							}
							break;

						case VIEW_TYPE_NAME: // Type/Name
							bInserted |= addChild(type, root, true);
							break;

						case VIEW_NAME: // Name
							break;

						case VIEW_ALL_TYPES: // All Types... every unique TYPE is listed
							for (Iterator e = aEq.typeList().iterator(); e.hasNext();)
							{
								type = (String)e.next();
								bInserted |= addChild(type, allTypeRoot, true);
							}
							break;

						default:
							Globals.errorPrint("In InfoInventory.EquipmentModel.addItemToModel the mode " + currentMode + " is not supported.");
							break;

					}

					if (bInserted)
					{
						fireTreeNodesInserted(root, rootAsPObjectNode.getChildren(), null, null);
					}

				}

				switch (currentMode)
				{
					case VIEW_TYPE_SUBTYPE_NAME: // Type/SubType/Name
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (aEq.isType(rootAsPObjectNode.getChild(i).toString()))
							{
								//
								// Items with only 1 type will not show up unless we do this
								//
								PObjectNode[] d;
								if (aEq.typeList().size() == 1)
								{
									d = new PObjectNode[1];
									d[0] = rootAsPObjectNode.getChild(i);
								}
								else
								{
									d = rootAsPObjectNode.getChild(i).getChildren();
								}

								for (int k = 0; d != null && k < d.length; k++)
								{
									//
									// Don't add children to items (those with only 1 type)
									//
									if (!(d[k].getItem() instanceof PObject))
									{
										if (aEq.isType(d[k].toString()))
										{
											addChild(aEq, d[k], fireEvent);
										}
									}
								}
							}
						}
						break;

					case VIEW_TYPE_NAME: // Type/Name
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (aEq.isType(rootAsPObjectNode.getChild(i).toString()))
							{
								addChild(aEq, rootAsPObjectNode.getChild(i), fireEvent);
							}
						}
						break;

					case VIEW_NAME: // Name
						addChild(aEq, root, fireEvent);
						break;

					case VIEW_ALL_TYPES: // All Types... every unique TYPE is listed
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (aEq.isType(rootAsPObjectNode.getChild(i).toString()))
							{
								addChild(aEq, rootAsPObjectNode.getChild(i), fireEvent);
							}
						}
						break;

					default:
						Globals.errorPrint("In InfoInventory.EquipmentModel.addItemToModel (second switch) the mode " + currentMode + " is not supported.");
						break;

				}
			}
			if (fireEvent)
			{
				fireTreeNodesChanged(root, rootAsPObjectNode.getChildren(), null, null);
			}
		}

		/**
		 * This assumes the EquipmentModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			Iterator fI;
			if (available)
				fI = Globals.getEquipmentList().iterator();
			else
				fI = aPC.getEquipmentList().iterator();

			currentMode = mode;

			switch (mode)
			{
				case VIEW_TYPE_SUBTYPE_NAME:	// Type/SubType/Name
					setRoot((PObjectNode)((PObjectNode)InfoInventory.typeSubtypeRoot).clone());
					break;

				case VIEW_TYPE_NAME:		// Type/Name
					setRoot((PObjectNode)((PObjectNode)InfoInventory.typeRoot).clone());
					break;

				case VIEW_NAME:			// Name
					setRoot(new PObjectNode()); // just need a blank one
					break;

				case VIEW_ALL_TYPES:		// All Types... every unique TYPE is listed
					setRoot((PObjectNode)((PObjectNode)InfoInventory.allTypeRoot).clone());
					break;

				default:
					Globals.errorPrint("In InfoInventory.EquipmentModel.resetModel the mode " + mode + " is not supported.");
					break;

			}
			for (; fI.hasNext();)
			{
				final Equipment aEq = (Equipment)fI.next();
				addItemToModel(aEq, false);
			}
			if (!newCall && ((PObjectNode)root).getChildCount() > 0)
			{
				addItemToModel(null, true);
			}
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(Equipment equip)
		{
			/*
			 * update for new filtering
			 * author: Thomas Behr 09-02-02
			 */
			if (modelType == 0)
			{
				return accept(equip);
			}
			return true;
		}

		// "There can be only one!" There must be a root object, though it can be hidden
		// to make it's existence basically a convenient way to keep track of the objects
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}


		public Object getRoot()
		{
			return (PObjectNode)root;
		}

		public void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode)root;

			// if no children, remove it and update parent
			if (p.getChildCount() == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < p.getChildCount(); i++)
				{
					removeItemFromNodes(p.getChild(i), e);
				}
			}
		}

		public void setValueForItemInNodes(PObjectNode p, Equipment e, float f, int column)
		{
			if (p == null)
			{
				p = (PObjectNode)root;
			}

			Object obj = p.getItem();
			// if no children, remove it and update parent
			if ((p.getChildCount() == 0) && (obj != null) && (obj instanceof Equipment) && ((Equipment)obj).equals(e))
			{
				final Equipment pe = (Equipment)obj;
				switch (column)
				{
					case COL_QTY_SRC:
						pe.setQty(new Float(f));
						if (pe.getCarried().floatValue() > f)
							pe.setNumberCarried(new Float(f));
						break;
					case COL_CARRIED:
						pe.setNumberCarried(new Float(f));
						break;
					default:
						Globals.errorPrint("In InfoInventory.EquipmentModel.setValueForItemInNodes the column " + column + " is not supported.");
						break;
				}
			}
			else
			{
				for (int i = 0; i < p.getChildCount(); i++)
				{
					setValueForItemInNodes(p.getChild(i), e, f, column);
				}
			}
		}

		private void pruneChildless()
		{
			PObjectNode rootAsPObjectNode = (PObjectNode)root;
			ArrayList aList = new ArrayList();
			for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
				if (rootAsPObjectNode.getChild(i).getChildCount() > 0)
					aList.add(rootAsPObjectNode.getChild(i));
			PObjectNode[] newkids = new PObjectNode[aList.size()];
			for (int i = 0; i < aList.size(); i++)
				newkids[i] = (PObjectNode)aList.get(i);
			((PObjectNode)root).setChildren(newkids, false);
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			if (modelType == 0)
				return NUM_COL_AVAILABLE;
			return NUM_COL_SELECTED;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return "Item";
				case COL_COST:
					return "Cost";
				case COL_QTY_SRC:
					if (modelType == 0)
						return "Source";
					return "Qty";
				case COL_CARRIED:
					return "Carried";
				case COL_LOCATION:
					return "Location";
				case COL_HAND:
					return "Hand";
				case COL_INDEX:
					return "Order";
				default:
					Globals.errorPrint("In InfoInventory.EquipmentModel.getColumnName the column " + column + " is not supported.");
					break;
			}
			return "";
		}

		/**
		 * Returns Class for the column.
		 */
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;
				case COL_COST:
					return java.math.BigDecimal.class;
//					return Float.class;
				case COL_QTY_SRC:
					if (modelType == 0)
						return String.class;
					return Float.class;
				case COL_CARRIED:
					return Double.class;
				case COL_LOCATION:
					return Integer.class;
				case COL_HAND:
					return Integer.class;
				case COL_INDEX:
					return Integer.class;
				default:
					Globals.errorPrint("In InfoInventory.EquipmentModel.getColumnClass the column " + column + " is not supported.");
					break;
			}
			return String.class;
		}

		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME ||
				(modelType == 1 && (((PObjectNode)node).getItem() instanceof Equipment) &&
				((column == COL_QTY_SRC)
				|| (column == COL_CARRIED)
				|| (column == COL_LOCATION)
				|| (column == COL_HAND)
				|| (column == COL_INDEX))));
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			Equipment aEq = null;
			if (fn != null && (fn.getItem() instanceof Equipment))
				aEq = (Equipment)fn.getItem();

//			final Float a = new Float(0);
//			final Double b = new Double(0);
//			final Integer c = new Integer(0);
			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoInventory.");
						return "";
					}
				case COL_COST: // Cost
					if (aEq != null)
					{
						return Utility.trimBigDecimal(aEq.getCost());
					}
//					return a;
					break;
				case COL_QTY_SRC: // Source or Qty
					if (fn != null && modelType == 0)
						return fn.getSource();
					if (aEq != null)
						return aEq.qty();
//					return a;
					break;
				case COL_CARRIED: // number carried
					if (aEq != null)
						return aEq.numberCarried();
//					return b;
					break;

				case COL_LOCATION: // location
					if (aEq != null)
					{
						if (aEq.isEquipped())
						{
							return new Integer(EQUIPMENT_EQUIPPED);
						}
						if (aEq.getParent() != null)
						{
							final Equipment containedIn = (Equipment)aEq.getParent();
							final int aInt = indexLocationChoice(containedIn.toString());
							return new Integer(aInt);
						}
						if (aEq.numberCarried().floatValue() > 0)
						{
							return new Integer(EQUIPMENT_CARRIED);
						}
					}
					return new Integer(EQUIPMENT_NOTCARRIED);

				case COL_HAND: // hand
					int iHand = Equipment.NEITHER_HAND;
					if (aEq != null)
					{
						iHand = aEq.whatHand();
					}
					return new Integer(iHand);

				case COL_INDEX: // Output index
					int outputIndex = 0;
					if (aEq != null)
					{
						outputIndex = aEq.getOutputIndex();
					}
					return new Integer(outputIndex);

				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoInventory.");
//						return null;
					}
					break;
				default:
					Globals.errorPrint("In InfoInventory.EquipmentModel.getValueAt the column " + column + " is not supported.");
					break;

			}
			return null;
		}


		private Equipment getBaseEquipment(Equipment selectedEquipment)
		{
			String keyName = selectedEquipment.getKeyName();
			Equipment baseEquipment = Globals.getEquipmentNamed(keyName);
			if (baseEquipment == null)
			{
				baseEquipment = aPC.getEquipmentNamed(keyName);
			}
			if (baseEquipment == null)
			{
				baseEquipment = selectedEquipment.getHeaderParent();
				if (baseEquipment != null)
				{
					keyName = baseEquipment.getKeyName();
					baseEquipment = Globals.getEquipmentNamed(keyName);
					if (baseEquipment == null)
					{
						baseEquipment = aPC.getEquipmentNamed(keyName);
					}
				}
			}
			return baseEquipment;
		}


		public void setValueAt(Object value, Object node, int column)
		{
			if (aPC == null)
				return;
			if (modelType != 1)
				return; // can only set values for selectedTableModel
			if (!(((PObjectNode)node).getItem() instanceof Equipment))
				return; // can only use rows with Equipment in them

			Equipment selectedEquipment = (Equipment)((PObjectNode)node).getItem();
//			Equipment baseEquipment = Globals.getEquipmentNamed(selectedEquipment.getKeyName());
//			if (baseEquipment == null)
//			{
//				baseEquipment = selectedEquipment.getHeaderParent();
//				if (baseEquipment != null)
//				{
//					baseEquipment = Globals.getEquipmentNamed(baseEquipment.getKeyName());
//				}
//				if (baseEquipment == null)
//				{
//					return;
//				}
//			}
			Equipment baseEquipment = getBaseEquipment(selectedEquipment);
			if (baseEquipment == null)
			{
				return;
			}

			switch (column)
			{
				case COL_QTY_SRC:
					float qtyToAdd = ((Float)value).floatValue() - selectedEquipment.qty().floatValue();
					if (qtyToAdd > 0.0)
					{
						buySpecifiedEquipment(selectedEquipment, qtyToAdd);
					}
					else if (qtyToAdd < 0.0)
					{
						sellSpecifiedEquipment(selectedEquipment, -qtyToAdd);
					}
/*					final float newQty = ((((Float)value).floatValue() > 0)
						? (((Float)value).floatValue()) : 0.0f);

					if (baseEquipment.acceptsChildren() && (newQty % 1) != 0)
					{
						JOptionPane.showMessageDialog(null,
							"You cannot buy, own or carry non-integral numbers of containers\ni.e. Half a " + baseEquipment.getName() + " is nonsensical.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						break;
					}

					final float oldQty = (selectedEquipment == null) ? 0.0f : selectedEquipment.qty().floatValue();
					if (canAfford(baseEquipment, selectedEquipment, newQty))
					{

//Begin multicontainers code.
						final boolean tempHeaderParent = (selectedEquipment == null) ? false : selectedEquipment.getHasHeaderParent();
						if (baseEquipment.acceptsChildren() && !tempHeaderParent)
						{
							if (oldQty == 0)
							{
								aPC.getEquipmentList().add((Equipment)baseEquipment.clone());
								selectedEquipment = (Equipment)aPC.getEquipmentNamed(baseEquipment.getKeyName());
								if (selectedEquipment != null && newQty == 1)
									selectedEquipment.setQty(new Float(newQty));
							}

							if ((selectedEquipment != null) && (newQty < oldQty) && !baseEquipment.getHasHeaderParent())
							{
								if (baseEquipment.isHeaderParent())
								{
									JOptionPane.showMessageDialog(null,
										"You cannot decrease the number of containers using the header,\nremove them by setting the quantity of the ones you want to remove to 0", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
									break;
								}

								baseEquipment.quickRemoveAllChildren();
								adjustBelongings(baseEquipment, selectedEquipment, newQty, -1);
							}
							else if ((selectedEquipment != null) && (newQty > oldQty) && (newQty > 1))
							{
								int numHeaderChildren = 1;
								if (oldQty > 1)
									numHeaderChildren += selectedEquipment.countHeaderChildren();

								ArrayList anArrayList = new ArrayList((int)newQty - numHeaderChildren);
								for (int i = numHeaderChildren - 1; i < newQty; i++)
								{
									final Equipment newHeaderChild = (Equipment)selectedEquipment.createHeaderParent();
									if (i > 0)  //newly added containers are empty, excepting -1-, because it is really the original container.
									{
										newHeaderChild.setNumberCarried(new Float(0));
										newHeaderChild.setIsEquipped(false);
										newHeaderChild.setHand(Equipment.NEITHER_HAND);
										newHeaderChild.getD_childTypes().clear();
										newHeaderChild.getD_childTypes().put("Total", new Float(0));
										newHeaderChild.clearContainedEquipment();
									}

									newHeaderChild.updateContainerContentsString();
									newHeaderChild.clearHeaderChildren();
									selectedEquipment.setIsEquipped(false);
									anArrayList.add(newHeaderChild);
									aPC.getEquipmentList().add(newHeaderChild);
								}
								prepareAvailableContainers();
								aPC.setDirty(true);

							}
						}
						else if (selectedEquipment != null && selectedEquipment.getHasHeaderParent() && newQty > 1)
						{
							JOptionPane.showMessageDialog(null,
								"You cannot increase the number of containers using the sub-container,\nadd them by increasing the quantity of the header, ie \"Backpack\" not \"Backpack -2-\"", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							break;
						}
						else if (selectedEquipment != null && selectedEquipment.getHasHeaderParent() && newQty == 0)
						{
							//dump the containers contents to carried.
							selectedEquipment.quickRemoveAllChildren();

							//correct the Hchildren of its Hparent
							final Equipment aHParent = (Equipment)selectedEquipment.getHeaderParent();
							aHParent.removeHeaderChild(selectedEquipment);

							//check to see if we need to collapse the headerparent back into a flat container
							if (aHParent.countHeaderChildren() == 1)
							{
								final Equipment hChild = aHParent.getHeaderChild(0);
								aPC.getEquipmentList().remove(hChild);

								aHParent.collapseHeaderParent();
								prepareAvailableContainers();
								adjustBelongings(baseEquipment, selectedEquipment, newQty, -1);
							}
							else
							{
								adjustBelongings(baseEquipment, selectedEquipment, newQty, -1);    //now delete it.
							}

							//and fix the container choices
							//prepareAvailableContainers();

						}
						else
						{
							adjustBelongings(baseEquipment, selectedEquipment, newQty, -1);
						}

						if (newQty != oldQty && baseEquipment.acceptsChildren())
						{
							prepareAvailableContainers();
							final TableColumn col_location = selectedTable.getColumnModel().getColumn(COL_LOCATION);
							col_location.setCellRenderer(new LocationRenderer(locationChoices()));
							col_location.setCellEditor(new LocationEditor(locationChoices()));
						}

//End multicontainers code

						updateTotalWeight();
						if (newQty != 0.0)
							adjustGold(selectedEquipment, oldQty - newQty);
						updateTotalValue();
					}
					else
					{
						JOptionPane.showMessageDialog
							(null,
								"Insufficient funds for purchase of " +
							newQty + " " + baseEquipment.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
					}
*/
					break;
				case COL_CARRIED:
					if (selectedEquipment != null)
					{
						float newValue = ((Float)value).floatValue();

						if (baseEquipment.acceptsChildren() && (newValue % 1) != 0)
						{
							JOptionPane.showMessageDialog(null,
								"You cannot buy, own or carry non-integral numbers of containers\ni.e. Half a sack is nonsensical.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							break;
						}

						if (newValue > selectedEquipment.qty().floatValue())
						{
							JOptionPane.showMessageDialog
								(null,
									"Cannot carry more than you have of " + baseEquipment.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							newValue = selectedEquipment.qty().floatValue();
						}
						if (newValue < 0.0f)
						{
							JOptionPane.showMessageDialog
								(null,
									"Cannot carry negative numbers of " + baseEquipment.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							newValue = selectedEquipment.qty().floatValue();
						}
						if (newValue != selectedEquipment.numberCarried().floatValue())
						{
							if (selectedEquipment.isHeaderParent())
							{
								JOptionPane.showMessageDialog(null,
									"You cannot change the number of containers carried using the header,\ncarry or equip each one separately.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								break;
							}


							//Check to see if the item for which the number carried is being changed has a parent.
							if (selectedEquipment.getParent() == null)
							{
								setValueForItemInNodes(null, selectedEquipment, newValue, COL_CARRIED);
								InfoInventory.this.selectedTable.updateUI();
							}
							else
							{
								final Float origCarried = selectedEquipment.numberCarried();
								final Equipment aParent = (Equipment)selectedEquipment.getParent();


								setValueForItemInNodes(null, selectedEquipment, newValue, COL_CARRIED);
								aParent.removeChild(selectedEquipment);
								if (aParent.canContain(selectedEquipment) != 1)
								{
									String aReason = "";
									switch (aParent.canContain(selectedEquipment))
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

									JOptionPane.showMessageDialog(null, "Container cannot hold that many more items, because " + aReason, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
									selectedEquipment.setNumberCarried(origCarried);
								}
								else
								{
									setValueForItemInNodes(null, selectedEquipment, newValue, COL_CARRIED);
								}
								aParent.insertChild(selectedEquipment);
								InfoInventory.this.selectedTable.updateUI();
							}
							updateTotalWeight();
						}
					}
					break;
				case COL_LOCATION:
					if (selectedEquipment != null)
					{
						if (selectedEquipment.isHeaderParent())  // no editing of Location if it is a header parent
						{
							JOptionPane.showMessageDialog(null,
								"You cannot carry/equip the header object, carry/equip each one separately.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							break;
						}

						int anInt = ((Integer)value).intValue();
						if (anInt >= EQUIPMENT_CONTAINED)
						{
							origLocation = ((Integer)getValueAt(node, COL_LOCATION)).intValue();
							final Equipment aParent = (Equipment)d_containersAvailable.get(anInt - EQUIPMENT_CONTAINED);
							//Before anything, make sure you are not trying to put it in itself.  If so, complain, then reset it to the original value.
							if (selectedEquipment.equals(aParent) || selectedEquipment.equals(aParent.getUberParent()))
							{
								JOptionPane.showMessageDialog(null, "Cannot put an item in itself,\n or into an item contained inside it.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								break;
							}
							else
							{
								selectedEquipment.setIsEquipped(false); //cannot be equipped if inside a container... may want to change this, but requires many more changes.
								//Check if new selection can hold it
								if (aParent.canContain(selectedEquipment) == 1)
								{
									float newValue = selectedEquipment.numberCarried().floatValue();
									removeParentChild(selectedEquipment);
									selectedEquipment.setParent(aParent);
									aParent.insertChild(selectedEquipment);

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
											newValue = selectedEquipment.qty().floatValue();
										}
										//End Hack Alert!
									}

								}
								else
								{
									String aReason = "";
									switch (aParent.canContain(selectedEquipment))
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
									JOptionPane.showMessageDialog(null, "Container cannot hold item, because " + aReason, Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
								}
							}
						}
						else
						{
							if (anInt == EQUIPMENT_EQUIPPED)
							{
								if (!baseEquipment.passesPreReqTests())
								{
									setValueAt(new Integer(EQUIPMENT_CARRIED), node, COL_LOCATION);
									JOptionPane.showMessageDialog(null, "You cannot equip this item as you do not meet the prerequisites", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
									break;
								}
							}

							removeParentChild(selectedEquipment);
							final int inHand = ((Integer)getValueAt(node, COL_HAND)).intValue();
							selectedEquipment.setIsEquipped(anInt == EQUIPMENT_EQUIPPED, inHand);

							if (anInt == EQUIPMENT_CARRIED)
							{
								//Insert Carried Code here, for when stuff doesn't all have to be in one place.
							}
							else if (anInt == EQUIPMENT_NOTCARRIED)
							{
								setValueAt(new Float(0), node, COL_CARRIED);
							}
						}

						if ((anInt != EQUIPMENT_NOTCARRIED) && selectedEquipment.numberCarried().compareTo(new Float(0)) == 0)
						{
							setValueAt(selectedEquipment.qty(), node, COL_CARRIED);
						}

						//Makes sure the headerparent Carried quantity gets updated in the gui.
						Equipment headerParent = (Equipment)selectedEquipment.getHeaderParent();
						if (headerParent != null)
						{
						}

						updateTotalWeight();
						// update skills tab as equipment may have had a bonus to skills
						Globals.getRootFrame().forceUpdate_InfoSkills();
					}
					break;

				case COL_HAND:
					if (selectedEquipment != null)
					{
						if (selectedEquipment.isHeaderParent())  // no editing of Location if it is a header parent
						{
							JOptionPane.showMessageDialog(null,
								"You cannot carry/equip the header object, carry/equip each one separately.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
							break;
						}

						int hand = ((Integer)value).intValue();
						if (hand >= 4)
						{
							selectedEquipment.setHand(hand);
							selectedEquipment.setNumberEquipped(hand - 2);
						}
						else
							selectedEquipment.setHand(hand);
					}
					break;

				case COL_INDEX:
					int outputIndex = ((Integer)value).intValue();
					boolean needRefresh = false;
					if (outputIndex == 1000) // Last
					{
						// Set it to one higher that the highest output index so far
						outputIndex = getHighestOutputIndex() + 1;
					}
					else if (outputIndex == 0) // First
					{
						// Set it to 1 and shuffle everyone up in order
						needRefresh = true;
						outputIndex = 2;
						for (Iterator i = aPC.getEquipmentListInOutputOrder().iterator(); i.hasNext();)
						{
							final Equipment item = (Equipment)i.next();
							if (item.getOutputIndex() > -1 && item != selectedEquipment)
							{
								item.setOutputIndex(outputIndex++);
							}
						}
						outputIndex = 1;
					}
					else if (outputIndex != -1) // A specific value
					{
						int workingIndex = 1;
						// Reorder everything so that we have a proper sequence - its the only way to be sure
						needRefresh = true;

						for (Iterator i = aPC.getEquipmentListInOutputOrder().iterator(); i.hasNext();)
						{
							final Equipment item = (Equipment)i.next();
							if (workingIndex == outputIndex)
								workingIndex++;
							if (item.getOutputIndex() > -1 && item != selectedEquipment)
							{
								item.setOutputIndex(workingIndex++);
							}
						}

					}

					selectedEquipment.setOutputIndex(outputIndex);

					if (needRefresh)
						selectedTable.updateUI();
					break;
				default:
					Globals.errorPrint("In InfoInventory.EquipmentModel.setValueAt the column " + column + " is not supported.");
					break;

			}
		}

		private void removeParentChild(Equipment selected)
		{
			if (selected.getParent() != null) //remove previous parent/child data
			{
				final Equipment aParent = (Equipment)selected.getParent();
				aParent.removeChild(selected);
				selected.setParent(null);
			}
		}

		public void addChild(Object aChild, Object aParent)
		{
			PObjectNode aFN = new PObjectNode();
			aFN.setItem(aChild);
			aFN.setParent((PObjectNode)aParent);
			((PObjectNode)aParent).addChild(aFN);
		}

		public boolean addChild(Object aChild, Object aParent, boolean sort)
		{
			PObjectNode aFN = new PObjectNode();
			aFN.setItem(aChild);
			aFN.setParent((PObjectNode)aParent);
			if (aChild instanceof Equipment)
			{
				aFN.setIsValid(((Equipment)aChild).passesPreReqTests());
			}
			return ((PObjectNode)aParent).addChild(aFN, sort);
		}

		public void addChild(Object aChild, Object aParent, int index)
		{
			PObjectNode aFN = new PObjectNode();
			aFN.setItem(aChild);
			aFN.setParent((PObjectNode)aParent);
			((PObjectNode)aParent).addChild(aFN, index);
		}
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllEquipmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		needsUpdate = true;
		updateCharacterInfo();
	}

	/**
	 * specifies wheter the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies wheter the "negate/reverse" option should be available
	 */
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 */
	public int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}
}
