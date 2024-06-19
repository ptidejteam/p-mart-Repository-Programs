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
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 */

/**
 * This class is responsible for drawing the equipment related window - including
 * indicating what items are available, which ones are selected, and handling
 * the selection/de-selection of both.


 /**
 * @author  Mario Bonassin
 * @version $Revision: 1.1 $
 */
package pcgen.gui;

import java.awt.*;
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
import java.util.EventObject;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

public class InfoInventory extends FilterAdapterPanel
{
	final private JLabel avaLabel = new JLabel("Available");
	final private JLabel selLabel = new JLabel("Selected");
	final private JLabel goldLabel = new JLabel("Gold: ");
	private JTextField gold = new JTextField();
	final private JLabel capLabel = new JLabel("Capacity: ");
	private JTextField capacity = new JTextField();
	final private JLabel valueLabel = new JLabel("Total Value: ");
	private JTextField totalValue = new JTextField("Temp");
	final private JLabel loadLabel = new JLabel("Load: ");
	private JTextField load = new JTextField();
	final private JLabel weightLabel = new JLabel("Total Weight: ");
	JTextField totalWeight = new JTextField();
	JCheckBox costBox = new JCheckBox("Ignore Cost");
	private JButton leftButton;
	private JButton rightButton;
	JScrollPane eqScroll = new JScrollPane();
	JScrollPane scrollPane;
	/*
	 * initializing the editor pane with default HTML tags;
	 * this fixes a bug which causes NPEs to be thrown on updateUI()
	 * with no HTML tags present
	 *
	 * author: Thomas Behr 13-03-03
	 */
	JEditorPane infoLabel = new JEditorPane("text/html", "<html></html>");
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
	private boolean needsUpdate = true;
	protected PlayerCharacter aPC = null;
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


	private static final int COL_NAME = 0;
	private static final int COL_COST = 1;
	private static final int COL_QTY_SRC = 2;
	private static final int COL_CARRIED = 3;
	private static final int COL_LOCATION = 4;
	private static final int COL_HAND = 5;

	private static final int EQUIPMENT_NOTCARRIED = 0;
	private static final int EQUIPMENT_CARRIED = 1;
	private static final int EQUIPMENT_EQUIPPED = 2;
	private static final int EQUIPMENT_CONTAINED = 3;

	// Right-click inventory item
	private int selRow;
	private TreePath selPath;
	private boolean hasBeenSized = false;

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
						//
						// Get a number from the user via a popup
						//
						int sellQty = qty;
						if (sellQty == -1)
						{
							Object selectedValue = JOptionPane.showInputDialog(null,
								"Enter Quantity",
								Constants.s_APPNAME,
								JOptionPane.QUESTION_MESSAGE);
							if (selectedValue != null)
							{
								try
								{
									sellQty = Integer.parseInt(((String)selectedValue).trim());
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

						final Float newFloat = new Float(((Equipment)pe).qty().floatValue() - sellQty);
						if (newFloat.floatValue() <= 0.0f)
						{
							removeEquipment(evt);
						}
						else
						{
							selectedModel.setValueForItemInNodes(null, (Equipment)pe, newFloat.floatValue(), COL_QTY_SRC);
//							selectedModel.setValueAt(newFloat, po, COL_QTY_SRC);
							int[] selectedRows = thisTree.getSelectionRows();
							selectedTable.updateUI();
							thisTree.setSelectionRows(selectedRows);
						}
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
				InventoryPopupMenu.this.add(Utility.createMenuItem("Find item",
					new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							searchButtonClick(selectedTable);
						}
					}
					, "searchItem", (char)0, "control F", "Find item", null, true));
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


	EQFrame eqFrame = null;

	private final static String[] loadTypes =
		{
			"LIGHT",
			"MEDIUM",
			"HEAVY",
			"OVERLOADED"
		};

	private final static String[] handTypes =
		{
			"NEITHER",
			"PRIMARY",
			"OFF-HAND",
			"BOTH",
			"Two-Weapons"
		};

	/* typeSubtypeRoot is the base structure used by both the available and selected tables; no
   * need to generate this same list twice.
   */
	public static Object typeSubtypeRoot;
	public static Object typeRoot;
	public static Object allTypeRoot;


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
		int iView = Globals.getInventoryTab_AvailableListMode();
		if ((iView >= VIEW_TYPE_SUBTYPE_NAME) && (iView <= VIEW_ALL_TYPES))
		{
			viewMode = iView;
		}
		Globals.setInventoryTab_AvailableListMode(viewMode);
		iView = Globals.getInventoryTab_SelectedListMode();
		if ((iView >= VIEW_TYPE_SUBTYPE_NAME) && (iView <= VIEW_ALL_TYPES))
		{
			viewSelectMode = iView;
		}
		Globals.setInventoryTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem("Type/SubType/Name");
		viewComboBox.addItem("Type/Name");
		viewComboBox.addItem("Name");
		viewComboBox.addItem("All Types");
		viewComboBox.setToolTipText("You can change how the Equipment in the Tables are listed.");
		viewComboBox.setSelectedIndex(viewMode);			// must be done before createModels call

		viewSelectComboBox.addItem("Type/SubType/Name");
		viewSelectComboBox.addItem("Type/Name");
		viewSelectComboBox.addItem("Name");
		viewSelectComboBox.addItem("All Types");
		viewSelectComboBox.setToolTipText("You can change how the Equipment in the Tables are listed.");
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
		rightButton = new JButton(newImage);
		rightButton.setToolTipText("Click to add the selected item from the Available list of equipment");
		rightButton.setEnabled(false);
		aPanel.add(rightButton);
		leftPane.add(aPanel);
		newImage = new ImageIcon(getClass().getResource("resource/Refresh16.gif"));
		JButton sButton = new JButton(newImage);
		sButton.setToolTipText("Click to change orientation of tables");
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
		leftButton = new JButton(newImage);
		leftButton.setToolTipText("Click to add the selected item from the Selected list of equipment");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
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
		eqScroll.setToolTipText("Any requirements you don't meet are in italics.");

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
		costBox.setSelected(Globals.getInventoryTab_IgnoreCost());

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
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeEquipment(evt);
			}
		});
		rightButton.addActionListener(new ActionListener()
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
				Globals.setInventoryTab_IgnoreCost(costBox.isSelected());
			}
		});
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			Globals.setInventoryTab_AvailableListMode(viewMode);
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
			Globals.setInventoryTab_SelectedListMode(viewSelectMode);
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
		TableColumn acol[] = new TableColumn[3];
		TableColumn scol[] = new TableColumn[6];
		int awidth[] = new int[3];
		int swidth[] = new int[6];
		for(int i=0;i<3;i++)
		{
			acol[i] = availableTable.getColumnModel().getColumn(i);
			awidth[i] = acol[i].getWidth();
		}
		for(int i=0;i<6;i++)
		{
			scol[i] = selectedTable.getColumnModel().getColumn(i);
			swidth[i] = scol[i].getWidth();
		}
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = Globals.getPCGenOption("InfoInventory.splitPane", (int)(this.getSize().getWidth()*6/10));
			t = Globals.getPCGenOption("InfoInventory.bsplit", (int)(this.getSize().getHeight() - 81));
			u = Globals.getPCGenOption("InfoInventory.asplit", (int)(this.getSize().getWidth() - 295));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("InvSel", i);
				if(width == 0)
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
				if(width == 0)
					sCol.setPreferredWidth(swidth[i]);
				else
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "InvAva", i));
			}
		}
		if (s>0)
		{
			splitPane.setDividerLocation(s);
			Globals.setPCGenOption("InfoInventory.splitPane", s);
		}
		if (t>0)
		{
			bsplit.setDividerLocation(t);
			Globals.setPCGenOption("InfoInventory.bsplit", t);
		}
		if (u>0)
		{
			asplit.setDividerLocation(u);
			Globals.setPCGenOption("InfoInventory.asplit", u);
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
		final PObjectNode a = new PObjectNode();
		a.resetPC();
		aPC = Globals.getCurrentPC();
		if (aPC == null || needsUpdate == false)
			return;
		aPC.aggregateFeatList();
		createModels();
		availableTable.updateUI();
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
			for (Iterator pcIterator = Globals.getPcList().iterator(); pcIterator.hasNext();)
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

	private void customizeButtonClick()
	{
		if (!rightButton.isEnabled())
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
				b.append(" &nbsp;<b>Properties</b>:").append(aEq.getInterestingDisplayString());
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
			a = aEq.spellFailure();
			if (aEq.isArmor() || aEq.isShield() || (a.intValue() != 0))
			{
				b.append(" <b>Arcane Failure</b>:").append(a.toString());
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
			bString = aEq.getInterestingDisplayString();
			if (bString.length() > 0)
				b.append(" <b>Bonuses</b>:").append(bString);
			bString = aEq.getContainerCapacityString();
			if (bString.length() > 0)
				b.append(" <b>Container</b>:").append(bString);
			bString = aEq.getContainerContentsString();
			if (bString.length() > 0)
				b.append(" <b>Currently Contains</b>:").append(bString);
			bString = aEq.getSource();
			if (bString.length() > 0)
				b.append(" <b>SOURCE</b>:").append(bString);
			b.append("</html>");
			infoLabel.setText(b.toString());
			infoLabel.setCaretPosition(0);
		}
		else
		{
			/*
			 * this fixes a bug which causes NPEs to be thrown on updateUI()
			 * with no HTML tags present
			 *
			 * author: Thomas Behr 13-03-03
			 */
			infoLabel.setText("<html></html>");
		}
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
					rightButton.setEnabled(aEq != null);
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
					{
						/*
						 * this fixes a bug which causes NPEs to be thrown on updateUI()
						 * with no HTML tags present
						 *
						 * author: Thomas Behr 13-03-03
						 */
						infoLabel.setText(Globals.html_NONESELECTED);
						infoLabel.setCaretPosition(0);
						return;
					}

					final Equipment aEq = aPC.getEquipmentNamed(aString.substring(aString.lastIndexOf("|") + 1));
					leftButton.setEnabled(aEq != null);
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
					if (e.getClickCount() == 1)
						btree.setSelectionPath(selPath);
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

	/**
	 * Carry some stuff, absolute value.  Use <code>0</code> for
	 * none, <code>Integer.MAX_VALUE</code> for all.
	 *
	 * @param path TreePath what equipment?
	 * @param qty int how much to carry?
	 */
/*	private void setCarryEquipment(TreePath path, int qty)
	{
		PObjectNode pon = (PObjectNode)path.getLastPathComponent();
		Object o = pon.getItem();
		if (!(o instanceof Equipment))
			return;
		Equipment eq = (Equipment)o;
		Equipment selEq = (Equipment)aPC.getEquipmentList().get(eq.getKeyName());
		if (selEq == null) return;

		float f = selEq.qty().floatValue();
		if (qty < 0)
			qty = 0;
		else if (qty > f)
			qty = (int)f % 1;

		selEq.setNumberCarried(new Float(qty));
		selectedTable.updateUI();
	}
*/
	/**
	 * Carry some stuff, relative value.
	 *
	 * @param path TreePath what equipment?
	 * @param adj int how much to carry?
	 */
/*	private void adjustCarriedEquipment(TreePath path, int adj)
	{
		PObjectNode pon = (PObjectNode)path.getLastPathComponent();
		Object o = pon.getItem();
		if (!(o instanceof Equipment))
			return;
		Equipment eq = (Equipment)o;
		Equipment selEq = (Equipment)aPC.getEquipmentList().get(eq.getKeyName());
		if (selEq == null) return;

		float f = selEq.qty().floatValue(), qty = selEq.numberCarried().floatValue();
		qty += adj;
		if (qty < 0.0f)
			qty = 0.0f;
		else if (qty > f) qty = f;

		selEq.setNumberCarried(new Float(qty));
		selectedTable.updateUI();
	}
*/
	private void removeEquipment(ActionEvent evt)
	{
		final int row = selectedTable.getSelectionModel().getAnchorSelectionIndex();
		final TreePath treePath = selectedTable.tree.getPathForRow(row);
		final Object eo = treePath.getLastPathComponent();
		final PObjectNode e = (PObjectNode)eo;
		Object item = e.getItem();
		if (item instanceof Equipment)
		{
			if (((Equipment)item).countHeaderChildren() == 0)
			{
				if (((Equipment)item).getChildCount() == 0)
				{
					((Equipment)item).setNumberCarried(new Float(0));
					((Equipment)item).setIsEquipped(false);
					((Equipment)item).setHand(Equipment.NEITHER_HAND);
					final Equipment eqParent = (Equipment)((Equipment)item).getParent();
					if (eqParent != null)
					{
						eqParent.removeChild(item);
					}

					adjustGold((Equipment)item, (((Equipment)item).qty()).floatValue());
					aPC.equipmentListRemove((Equipment)item);
					selectedModel.removeItemFromNodes(null, item);
					//
					// Get rid of item from parent
					//
					Equipment headerParent = ((Equipment)item).getHeaderParent();
					if (headerParent != null)
					{
						//
						// Remove all children from equipment list, remove the item, re-key the rest and then re-add to list
						//
						// There's probably a prettier solution to this, but I'll be damned if I can find it.
						// Containers can still become unsorted in selected list when > 10 involved.
						//
						for (int x = 0; x < headerParent.countHeaderChildren(); x++)
						{
							final Equipment aChild = headerParent.getHeaderChild(x);
							aPC.equipmentListRemove(aChild);
						}

						headerParent.removeHeaderChild(item);	// will re-key children

						if (headerParent.countHeaderChildren() == 1)
						{
							item = headerParent.getHeaderChild(0);
							headerParent.collapseHeaderParent();
							aPC.equipmentListRemove((Equipment)item);
							selectedModel.removeItemFromNodes(null, item);
						}

						for (int x = 0; x < headerParent.countHeaderChildren(); x++)
						{
							final Equipment aChild = headerParent.getHeaderChild(x);
							aPC.equipmentListAdd(aChild);
						}
					}
					prepareAvailableContainers();
					final TableColumn col_location = selectedTable.getColumnModel().getColumn(COL_LOCATION);
					col_location.setCellRenderer(new LocationRenderer(locationChoices()));
					col_location.setCellEditor(new LocationEditor(locationChoices()));
					selectedTable.updateUI();
					updateTotalWeight();
					updateTotalValue();
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
		else
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
			Equipment selectedEquipment = Globals.getEquipmentNamed(aString.substring(aString.lastIndexOf("|") + 1));
			Equipment baseEquipment = (Equipment)aPC.getEquipmentList().get(selectedEquipment.getKeyName());

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
						aPC.getEquipmentList().put(selectedEquipment.getKeyName(), baseEquipment);
					}

					if (baseEquipment != null)
					{
						if ((oldQty + newQty) > 1)
						{
							int numHeaderChildren = baseEquipment.countHeaderChildren();
							for (int i = numHeaderChildren; i < (oldQty + newQty); i++)
							{
								final Equipment newHeaderChild = (Equipment)baseEquipment.createHeaderParent();
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
								aPC.equipmentListAdd(newHeaderChild);
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
			JOptionPane.showMessageDialog(null, "Exception:" + exc.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
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
					aPC.getEquipmentList().remove(selected.getKeyName());
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
						aPC.getEquipmentList().put(selected.getKeyName(), selected);
					}
				}
				if (selected != null)
				{
					//This was added to keep jlint happy.
					selected.setQty(new Float(newQty));
					selectedModel.setValueForItemInNodes(null, selected, newQty, COL_QTY_SRC);
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
	}

	protected void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new EquipmentModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
		if (selectedSort != null)
			selectedSort.setRoot((PObjectNode)selectedModel.getRoot());
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
			for (Iterator e = aPC.getEquipmentList().values().iterator(); e.hasNext();)
			{
				final Equipment anEquip = (Equipment)e.next();

				if (anEquip.acceptsChildren() && !(anEquip.isHeaderParent()))
					d_containersAvailable.add(anEquip);
			}
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
				setSelectedIndex(((Integer)value).intValue());
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

		public boolean isCellEditable(java.util.EventObject eventObject)
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
			if (aEq != null)
			{
				if (!shouldDisplayThis(aEq))
				{
					return;
				}

				PObjectNode[] c;
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
					c = ((PObjectNode)typeSubtypeRoot).getChildren();
					for (Iterator e = aEq.typeList().iterator(); e.hasNext();)
					{
						type = (String)e.next();
						for (int i = 0; i < c.length; i++)
						{
							final String treeType = c[i].toString();
							if ((c[i].getItem() instanceof PObject) || !aEq.isType(treeType) || type.equals(treeType))
							{
								continue;
							}
							addChild(type, c[i], true);
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
							c = ((PObjectNode)root).getChildren();
							for (Iterator e = aEq.typeList().iterator(); e.hasNext();)
							{
								type = (String)e.next();
								for (int i = 0; i < c.length; i++)
								{
									final String treeType = c[i].toString();
									if ((c[i].getItem() instanceof PObject) || !aEq.isType(treeType) || type.equals(treeType))
									{
										continue;
									}
									bInserted |= addChild(type, c[i], true);
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
					}

					if (bInserted)
					{
						//fireTreeStructureChanged(root, ((PObjectNode)root).getChildren(), null, null);
						fireTreeNodesInserted(root, ((PObjectNode)root).getChildren(), null, null);
					}

				}

				switch (currentMode)
				{
					case VIEW_TYPE_SUBTYPE_NAME: // Type/SubType/Name
						c = ((PObjectNode)root).getChildren();
						for (int i = 0; i < c.length; i++)
						{
							if (aEq.isType(c[i].toString()))
							{
								//
								// Items with only 1 type will not show up unless we do this
								//
								PObjectNode[] d;
								if (aEq.typeList().size() == 1)
								{
									d = new PObjectNode[1];
									d[0] = c[i];
								}
								else
								{
									d = c[i].getChildren();
								}

								for (int k = 0; k < d.length; k++)
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
						c = ((PObjectNode)root).getChildren();
						for (int i = 0; i < c.length; i++)
						{
							if (aEq.isType(c[i].toString()))
							{
								addChild(aEq, c[i], fireEvent);
							}
						}
						break;

					case VIEW_NAME: // Name
						addChild(aEq, root, fireEvent);
						break;

					case VIEW_ALL_TYPES: // All Types... every unique TYPE is listed
						c = ((PObjectNode)root).getChildren();
						for (int i = 0; i < c.length; i++)
						{
							if (aEq.isType(c[i].toString()))
							{
								addChild(aEq, c[i], fireEvent);
							}
						}
						break;
				}
			}
			if (fireEvent)
			{
				fireTreeNodesChanged(root, ((PObjectNode)root).getChildren(), null, null);
			}
		}

		/**
		 * This assumes the EquipmentModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			Iterator fI;
			if (available == true)
				fI = Globals.getEquipmentList().iterator();
			else
				fI = aPC.getEquipmentList().values().iterator();

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
			}
			for (; fI.hasNext();)
			{
				final Equipment aEq = (Equipment)fI.next();
				addItemToModel(aEq, false);
			}
			if (!newCall && ((PObjectNode)root).getChildren().length > 0)
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

			PObjectNode c[] = p.getChildren();
			// if no children, remove it and update parent
			if (c.length == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < c.length; i++)
				{
					removeItemFromNodes(c[i], e);
				}
			}
		}

		public void setValueForItemInNodes(PObjectNode p, Equipment e, float f, int column)
		{
			if (p == null)
			{
				p = (PObjectNode)root;
			}

			PObjectNode c[] = p.getChildren();

			final Equipment pe = (Equipment)p.getItem();
			// if no children, remove it and update parent
			if ((c == null) || ((c.length == 0) && (pe != null) && pe.equals(e)))
			{
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
				}
			}
			else
			{
				for (int i = 0; i < c.length; i++)
				{
					setValueForItemInNodes(c[i], e, f, column);
				}
			}
		}

		private void pruneChildless()
		{
			PObjectNode[] oldkids = ((PObjectNode)root).getChildren();
			ArrayList aList = new ArrayList();
			for (int i = 0; i < oldkids.length; i++)
				if (oldkids[i].getChildren().length > 0)
					aList.add(oldkids[i]);
			PObjectNode[] newkids = new PObjectNode[aList.size()];
			for (int i = 0; i < aList.size(); i++)
				newkids[i] = (PObjectNode)aList.get(i);
			((PObjectNode)root).setChildren(newkids, false);
		}

		/**
		 * Returns int number of children for <code>node</code>.
		 */
		public int getChildCount(Object node)
		{
			Object[] children = getChildren(node);
			return (children == null) ? 0 : children.length;
		}

		/**
		 * Returns Object child for <code>node</code> at index <code>i</code>.
		 */
		public Object getChild(Object node, int i)
		{
			return getChildren(node)[i];
		}

		/**
		 * Returns true if <code>node</node> is a leaf, otherwise false.
		 */
		public boolean isLeaf(Object node)
		{
			return ((PObjectNode)node).isLeaf();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			if (modelType == 0)
				return 3;
			return 6;
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
					return Float.class;
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
				|| (column == COL_HAND))));
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

			final Float a = new Float(0);
			final Double b = new Double(0);
			final Integer c = new Integer(0);
			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.debugErrorPrint("Somehow we have no active node when doing getValueAt in InfoInventory.");
						return "";
					}
				case COL_COST: // Cost
					if (aEq != null)
						return aEq.getCost();
					return a;
				case COL_QTY_SRC: // Source or Qty
					if (fn != null && modelType == 0)
						return fn.getSource();
					if (aEq != null)
						return aEq.qty();
					return a;
				case COL_CARRIED: // number carried
					if (aEq != null)
						return aEq.numberCarried();
					return b;
				case COL_LOCATION: // location
					if (aEq != null && aEq.isEquipped())
						return new Integer(2);
					if (aEq != null && aEq.getParent() != null)
					{
						final Equipment containedIn = (Equipment)aEq.getParent();
						final int aInt = indexLocationChoice(containedIn.toString());
						return new Integer(aInt);
					}
					if (aEq != null && aEq.numberCarried().floatValue() > 0)
						return new Integer(1);
					return c;
				case COL_HAND: // hand
					if (aEq != null)
						return new Integer(aEq.whatHand());
					return c;
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.debugErrorPrint("Somehow we have no active node when doing getValueAt in InfoInventory.");
						return null;
					}

			}
			return null;
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
			Equipment baseEquipment = Globals.getEquipmentNamed(selectedEquipment.getKeyName());
			if (baseEquipment == null)
			{
				baseEquipment = selectedEquipment.getHeaderParent();
				if (baseEquipment != null)
				{
					baseEquipment = Globals.getEquipmentNamed(baseEquipment.getKeyName());
				}
				if (baseEquipment == null)
				{
					return;
				}
			}
			switch (column)
			{
				case COL_QTY_SRC:
					{
						final float newQty = ((((Float)value).floatValue() > 0)
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
									aPC.getEquipmentList().put(baseEquipment.getKeyName(), (Equipment)baseEquipment.clone());
									selectedEquipment = (Equipment)aPC.getEquipmentList().get(baseEquipment.getKeyName());
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
										aPC.equipmentListAdd(newHeaderChild);
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
									aPC.getEquipmentList().remove(hChild.getKeyName());

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
					}

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

		protected Object[] getChildren(Object node)
		{
			PObjectNode featNode = ((PObjectNode)node);
			return featNode.getChildren();
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
