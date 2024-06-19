/*
 * InfoEquipping.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on April 29th, 2002, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:57 $
 *
 */

package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SystemCollections;
import pcgen.core.WeaponProf;
import pcgen.core.character.EquipSet;
import pcgen.core.character.EquipSlot;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserRadio;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.JTreeTableSorter;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.ResizeColumnListener;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.io.ExportHandler;
import pcgen.util.BigDecimalHelper;
import pcgen.util.FOPHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * <code>InfoEquipping</code> creates a new tabbed panel that is used to
 * allow different combinations of equipment for printing on csheets
 *
 * @author  Jayme Cox <jaymecox@users.sourceforge.net>
 * @version $Revision: 1.1 $
 **/
public class InfoEquipping extends FilterAdapterPanel
{
	static final long serialVersionUID = 6988134124127535195L;
	private EquipModel availableModel = null;  // Model for JTreeTable
	private EquipModel selectedModel = null;   // Model for JTreeTable
	private JTreeTable availableTable;         // available Equipment
	private JTreeTable selectedTable;	   // Equipment Sets
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	private static List equipSetList = new ArrayList();
	private static List tempSetList = new ArrayList();

	private JLabelPane infoLabel = new JLabelPane();
	private final JLabel avaLabel = new JLabel(PropertyFactory.getString("in_ieSort"));
	private final JLabel calcLabel = new JLabel(PropertyFactory.getString("in_ieCalc"));

	private final JLabel weightLabel = new JLabel(PropertyFactory.getString("in_weight") + ": ");
	private final JTextField totalWeight = new JTextField();
	private final JLabel loadLabel = new JLabel(PropertyFactory.getString("in_load") + ": ");
	private final JTextField loadWeight = new JTextField();
	private static final String[] loadTypes = {"LIGHT", "MEDIUM", "HEAVY", "OVERLOADED"};

	private JTextField equipSetTextField = new JTextField();
	private JTextField templateTextField = new JTextField();
	private static final String defaultEquipSet = PropertyFactory.getString("in_ieDefault");
	private static final String nameAdded = PropertyFactory.getString("in_ieAddEqSet");
	private static final String nameNotAdded = PropertyFactory.getString("in_ieNotAdd");
	private String selectedEquipSet = "";

	private JButton addEquipButton;
	private JButton delEquipButton;
	private JButton addEquipSetButton;
	private JButton delEquipSetButton;
	private JButton setQtyButton;
	private JButton setNoteButton;

	private JMenuItem AddMenu;
	private JMenuItem AddNumMenu;
	private JMenuItem AddAllMenu;
	private JMenuItem DelMenu;
	private JMenuItem SetQtyMenu;
	private JMenuItem SetNoteMenu;
	private JMenuItem SetLocationMenu;
	private JMenuItem CopyEquipSetMenu;
	private JMenuItem RenameEquipSetMenu;

	private JButton viewEqSetButton;
	private JButton exportEqSetButton;
	private JButton selectTemplateButton;

	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();

	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;

	private Border etched;

	private static PlayerCharacter aPC = null;

	private TreePath selPath;
	private Equipment lastEquip = null;

	private boolean needsUpdate = true;
	private boolean hasBeenSized = false;

	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx calcComboBox = new JComboBoxEx();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;

	private int viewMode = 0;
	private int viewSelectMode = 0;

	// table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECTED = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the EquipModel class
	private static final int COL_NAME = 0;
	private static final int COL_TYPE = 1;
	private static final int COL_QTY = 2;
	private static final int COL_LOCATION = 3;
	private static final int COL_COST = 4;
	private static final int COL_WEIGHT = 5;
	private static final int COL_BONUS = 6;

	/**
	 * create right click menus and listeners
	 **/
	private class EquipPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 6988134124127535195L;

		private class EquipActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipButton(new Float(1));
			}
		}

		private class AddNumEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addNumEquipButton();
			}
		}

		private class AddAllEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addAllEquipButton();
			}
		}

		private class DelEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipButton();
			}
		}

		private class SetQtyActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setQtyButton(new Float(0));
			}
		}

		private class SetLocationActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setLocationButton();
			}
		}

		private class SetNoteActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setNoteButton();
			}
		}

		private class CopyEquipSetActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				copyEquipSetButton();
			}
		}

		private class RenameEquipSetActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				renameEquipSetButton();
			}
		}

		private class RefreshActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				refreshButton();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			AddMenu = Utility.createMenuItem(label, new AddEquipActionListener(), PropertyFactory.getString("in_add") + " 1", (char) 0, accelerator, PropertyFactory.getString("in_ieAddEq"), "", true);
			return AddMenu;
		}

		private JMenuItem createAddNumMenuItem(String label)
		{
			AddNumMenu = Utility.createMenuItem(label, new AddNumEquipActionListener(), PropertyFactory.getString("in_add") + " #", (char) 0, null, PropertyFactory.getString("in_ieAddItem"), "", true);
			return AddNumMenu;
		}

		private JMenuItem createAddAllMenuItem(String label)
		{
			AddAllMenu = Utility.createMenuItem(label, new AddAllEquipActionListener(), PropertyFactory.getString("in_ieAddAll"), (char) 0, null, PropertyFactory.getString("in_ieAddAllItem"), "", true);
			return AddAllMenu;
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			DelMenu = Utility.createMenuItem(label, new DelEquipActionListener(), PropertyFactory.getString("in_remove") + " 1", (char) 0, accelerator, PropertyFactory.getString("in_ieRemEq"), "", true);
			return DelMenu;
		}

		private JMenuItem createSetQtyMenuItem(String label)
		{
			SetQtyMenu = Utility.createMenuItem(label, new SetQtyActionListener(), PropertyFactory.getString("in_ieSetQt"), (char) 0, null, PropertyFactory.getString("in_ieSetQtfull"), "", true);
			return SetQtyMenu;
		}

		private JMenuItem createSetLocationMenuItem(String label)
		{
			SetLocationMenu = Utility.createMenuItem(label, new SetLocationActionListener(), PropertyFactory.getString("in_ieChangeLoc"), (char) 0, null, PropertyFactory.getString("in_ieChangeLoc"), "", true);
			return SetLocationMenu;
		}

		private JMenuItem createSetNoteMenuItem(String label)
		{
			SetNoteMenu = Utility.createMenuItem(label, new SetNoteActionListener(), PropertyFactory.getString("in_ieSetNote"), (char) 0, null, PropertyFactory.getString("in_ieSetNotefull"), "", true);
			return SetNoteMenu;
		}

		private JMenuItem createCopyEquipSetMenuItem(String label)
		{
			CopyEquipSetMenu = Utility.createMenuItem(label, new CopyEquipSetActionListener(), PropertyFactory.getString("in_ieCopyEq"), (char) 0, null, PropertyFactory.getString("in_ieDupEq"), "", true);
			return CopyEquipSetMenu;
		}

		private JMenuItem createRenameEquipSetMenuItem(String label)
		{
			RenameEquipSetMenu = Utility.createMenuItem(label, new RenameEquipSetActionListener(), PropertyFactory.getString("in_ieRenameEq"), (char) 0, null, PropertyFactory.getString("in_ieRenameEqThis"), "", true);
			return RenameEquipSetMenu;
		}

		private JMenuItem createRefreshMenuItem(String label)
		{
			return Utility.createMenuItem(label, new RefreshActionListener(), "Redraw/Recalc Panel", (char) 0, null, "Redraw/Recalc this panels info", "", true);
		}

		EquipPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				EquipPopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_ieAddItem2"), "shortcut EQUALS"));
				EquipPopupMenu.this.add(createAddNumMenuItem(PropertyFactory.getString("in_ieAddItem")));
				EquipPopupMenu.this.add(createAddAllMenuItem(PropertyFactory.getString("in_ieAddAllItem")));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this.add(createRefreshMenuItem("Redraw/recalc Panel"));
			}
			else // selectedTable
			{
				EquipPopupMenu.this.add(createDelMenuItem(PropertyFactory.getString("in_ieRemEq"), "shortcut MINUS"));
				EquipPopupMenu.this.add(createSetQtyMenuItem(PropertyFactory.getString("in_ieSetQt")));
				EquipPopupMenu.this.add(createSetLocationMenuItem(PropertyFactory.getString("in_ieChangeLoc")));
				EquipPopupMenu.this.add(createSetNoteMenuItem(PropertyFactory.getString("in_ieSetNote")));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this.add(createCopyEquipSetMenuItem(PropertyFactory.getString("in_ieCopyEq")));
				EquipPopupMenu.this.add(createRenameEquipSetMenuItem(PropertyFactory.getString("in_ieRenameEq")));
				EquipPopupMenu.this.addSeparator();
				EquipPopupMenu.this.add(createRefreshMenuItem("Redraw/recalc Panel"));

			}
		}
	}

	private class EquipPopupListener extends MouseAdapter
	{
		//private JTreeTable aTreeTable;
		private JTree tree;
		private EquipPopupMenu menu;

		EquipPopupListener(JTreeTable treeTable, EquipPopupMenu aMenu)
		{
			//aTreeTable = treeTable;
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				//
				// Walk through the list of accelerators to see
				// if the user has pressed a sequence used by
				// the popup. This would not otherwise happen
				// unless the popup was showing
				//
				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); ++i)
						{
							final Component menuComponent = menu.getComponent(i);
							if (menuComponent instanceof JMenuItem)
							{
								KeyStroke ks = ((JMenuItem) menuComponent).getAccelerator();
								if ((ks != null) && keyStroke.equals(ks))
								{
									selPath = tree.getSelectionPath();
									((JMenuItem) menuComponent).doClick(2);
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
				selPath = tree.getClosestPathForLocation(evt.getX(), evt.getY());
				if (selPath == null)
				{
					return;
				}

				if (tree.isSelectionEmpty())
				{
					tree.setSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else if (!tree.isPathSelected(selPath))
				{
					tree.setSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
				else
				{
					tree.addSelectionPath(selPath);
					menu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new EquipPopupListener(treeTable, new EquipPopupMenu(treeTable)));
	}

	/**
	 *  Constructor for the InfoEquips object
	 */
	InfoEquipping()
	{
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(Constants.tabNames[Constants.TAB_EQUIPPING]);

		initComponents();

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	/**
	 * Sets the update flag for this tab
	 * It's a lazy update and will only occur
	 * on other status change
	 **/
	public final void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		//
		// View List Sanity check
		//
		int iView = SettingsHandler.getEquipTab_AvailableListMode();
		if ((iView >= GuiConstants.INFOEQUIPPING_VIEW_TYPE) && (iView <= GuiConstants.INFOEQUIPPING_VIEW_NAME))
		{
			viewMode = iView;
		}
		SettingsHandler.setEquipTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getEquipTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOEQUIPPING_VIEW_TYPE) && (iView <= GuiConstants.INFOEQUIPPING_VIEW_LOCATION))
		{
			viewSelectMode = iView;
		}
		SettingsHandler.setEquipTab_SelectedListMode(viewSelectMode);

		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		viewComboBox.addItem(PropertyFactory.getString("in_type") + "     ");
		viewComboBox.addItem(PropertyFactory.getString("in_ieLoc") + " ");
		viewComboBox.addItem(PropertyFactory.getString("in_ieEquipped") + " ");
		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel") + "     ");
		Utility.setDescription(viewComboBox, "Blah Blah");
		viewComboBox.setSelectedIndex(viewMode);

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addEquipButton = new JButton(newImage);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		delEquipButton = new JButton(newImage);
		setQtyButton = new JButton(PropertyFactory.getString("in_ieSetQt"));
		setNoteButton = new JButton(PropertyFactory.getString("in_ieSetNote"));

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		// fill the ComboBox
		calcComboBoxFill();
		final EquipSet es = (EquipSet) calcComboBox.getSelectedItem();
		if (es != null)
		{
			equipSetTextField.setText(es.getName());
			selectedEquipSet = equipSetTextField.getText();
			TreePath initSelPath = selectedTable.getTree().getPathForRow(0);
			if (initSelPath != null)
			{
				selectedTable.getTree().setSelectionPath(initSelPath);
			}
		}

		// build topPane which will contain leftPane and rightPane
		// leftPane will have two panels and a scrollregion
		// rightPane will have one panel and a scrollregion

		topPane.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		rightPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		topPane.add(splitPane, BorderLayout.CENTER);

		// build the left pane
		// for the availabe spells table and info

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		//aPanel.setBorder(BorderFactory.createEtchedBorder());
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);

		Utility.setDescription(addEquipButton, PropertyFactory.getString("in_ieAddEqBut"));
		addEquipButton.setEnabled(false);
		aPanel.add(addEquipButton);

		Utility.setDescription(aPanel, PropertyFactory.getString("in_ieAddEqRight"));
		leftPane.add(aPanel);


		// the available equipment sets panel
		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		// now build the right pane
		// for the selected (equipment) table

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		//aPanel.setBorder(BorderFactory.createEtchedBorder());

		JLabel selProfileLabel = new JLabel(PropertyFactory.getString("in_ieEquipSet") + ":");
		aPanel.add(selProfileLabel);

		equipSetTextField.setPreferredSize(new Dimension(100, 20));
		aPanel.add(equipSetTextField);

		addEquipSetButton = new JButton(PropertyFactory.getString("in_add"));
		//addEquipSetButton.setPreferredSize(new Dimension(60, 20));
		aPanel.add(addEquipSetButton);

		delEquipSetButton = new JButton(PropertyFactory.getString("in_ieDel"));
		//delEquipSetButton.setPreferredSize(new Dimension(60, 20));
		aPanel.add(delEquipSetButton);

		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);
		//bPanel.setBorder(BorderFactory.createEtchedBorder());

		/******
		 ******
		 ****** need to continue I18N from here on
		 ******
		 ******
		 ******/

		Utility.setDescription(setNoteButton, "Add additional info to this item");
		setNoteButton.setEnabled(false);
		bPanel.add(setNoteButton);

		Utility.setDescription(setQtyButton, "Click to change number of items");
		setQtyButton.setEnabled(false);
		bPanel.add(setQtyButton);

		Utility.setDescription(delEquipButton, "Click to remove selected equipment from this set");
		delEquipButton.setEnabled(false);
		bPanel.add(delEquipButton);
		rightPane.add(bPanel);

		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		//selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		availableTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		availableTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		availableTable.setColAlign(COL_COST, SwingConstants.RIGHT);
		availableTable.setColAlign(COL_LOCATION, SwingConstants.RIGHT);
		selectedTable.setColAlign(COL_TYPE, SwingConstants.RIGHT);
		selectedTable.setColAlign(COL_QTY, SwingConstants.CENTER);
		selectedTable.getColumnModel().getColumn(COL_TYPE).setCellRenderer(new ColorRenderer());
		selectedTable.getColumnModel().getColumn(COL_LOCATION).setCellRenderer(new ColorRenderer());
		selectedTable.getColumnModel().getColumn(COL_QTY).setCellEditor(new QuantityEditor());
		selectedTable.getColumnModel().getColumn(COL_COST).setCellEditor(new BonusEditor());


		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (equipment info)
		// bRightPane will contain a scrollregion (character Info)

		botPane.setLayout(new BorderLayout());

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();
		bLeftPane.setLayout(gridbag);
		bRightPane.setLayout(gridbag);

		asplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, bLeftPane, bRightPane);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		botPane.add(asplit, BorderLayout.CENTER);

		// Bottom left panel


		// Create a Weight Panel
		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JPanel wPanel = new JPanel();
		gridbag.setConstraints(wPanel, c);
		totalWeight.setEditable(false);
		totalWeight.setOpaque(false);
		totalWeight.setBorder(null);
		totalWeight.setBackground(Color.lightGray);
		loadWeight.setEditable(false);
		loadWeight.setOpaque(false);
		loadWeight.setBorder(null);
		loadWeight.setBackground(Color.lightGray);
		wPanel.add(weightLabel);
		wPanel.add(totalWeight);
		wPanel.add(loadLabel);
		wPanel.add(loadWeight);

		// create an equipment info scroll area
		Utility.buildConstraints(c, 0, 1, 1, 1, 2, 2);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Equipment Info");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);

		bLeftPane.add(wPanel);
		bLeftPane.add(sScroll);

		// Bottom right panel
		// create a template select and view panel
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JPanel ePanel = new JPanel();
		gridbag.setConstraints(ePanel, c);
		ePanel.add(calcLabel);
		ePanel.add(calcComboBox);

		Utility.buildConstraints(c, 0, 1, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JPanel iPanel = new JPanel();
		gridbag.setConstraints(iPanel, c);

		viewEqSetButton = new JButton("View in Browser");
		Utility.setDescription(viewEqSetButton, "Launches a browser and displays Equipment Sets");
		viewEqSetButton.setEnabled(true);
		iPanel.add(viewEqSetButton);

		exportEqSetButton = new JButton("Export to File");
		Utility.setDescription(exportEqSetButton, "Export Equipment Sets to a File");
		exportEqSetButton.setEnabled(true);
		iPanel.add(exportEqSetButton);

		Utility.buildConstraints(c, 0, 2, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JPanel iiPanel = new JPanel();
		gridbag.setConstraints(iiPanel, c);

		templateTextField.setEditable(false);
		Utility.setDescription(templateTextField, "Display only");
		templateTextField.setBackground(Color.lightGray);
		templateTextField.setText(SettingsHandler.getSelectedEqSetTemplateName());
		selectTemplateButton = new JButton("Select template");
		Utility.setDescription(selectTemplateButton, "Select an EquipSet output template");
		iiPanel.add(selectTemplateButton);
		iiPanel.add(templateTextField);

		bRightPane.add(ePanel);
		bRightPane.add(iPanel);
		bRightPane.add(iiPanel);

		// now split the top and bottom Panels
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, topPane, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add the entire mess (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// make sure we update when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				updateCharacterInfo();
			}
		});

		// add the sorter tables to that clicking on the TableHeader
		// actualy does something (gawd damn it's slow!)
		availableSort = new JTreeTableSorter(availableTable, (myPONode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (myPONode) selectedModel.getRoot(), selectedModel);

	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				int s = splitPane.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoEquipping.splitPane", s);
				}
				s = asplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoEquipping.asplit", s);
				}
				s = bsplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoEquipping.bsplit", s);
				}
			}
		});
		viewEqSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				viewEqSetButton();
			}
		});
		exportEqSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				exportEqSetButton();
			}
		});
		selectTemplateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectTemplateButton();
			}
		});
		addEquipButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipButton(new Float(1));
			}
		});
		delEquipButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipButton();
			}
		});
		setQtyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setQtyButton(new Float(0));
			}
		});
		setNoteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setNoteButton();
			}
		});
		addEquipSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipSetButton();
			}
		});
		delEquipSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipSetButton();
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed();
			}
		});
	}

	/**
	 * Calculate the weight carried for this EquipSet
	 **/
	private void updateTotalWeight()
	{
		if (aPC == null)
		{
			return;
		}
		aPC.setCalcEquipmentList();
		final Float weight = aPC.totalWeight();
		final Float roundedValue = new Float((new Float(Math.round(weight.doubleValue() * 10000))).floatValue() / 10000);
		totalWeight.setText(roundedValue.toString());
		loadWeight.setText(loadTypes[Globals.loadTypeForLoadScore(aPC.getVariableValue("LOADSCORE", "").intValue(), weight)]);
	}

	/**
	 * set the equipment Info text in the Equipment Info
	 * panel to the currently selected equipment
	 **/
	private void setInfoLabelText(Equipment eqI)
	{
		lastEquip = eqI; //even if that's null
		if (eqI != null)
		{
			//TODO:gorm optimize the initial capacity
			StringBuffer b = new StringBuffer(300);
			b.append("<html><font size=+1><b>").append(eqI.piSubString()).append("</b></font>");
			if (!eqI.longName().equals(eqI.getName()))
			{
				b.append("(").append(eqI.longName()).append(")");
			}
			b.append(" <b>Type:</b> ").append(eqI.getType());
			//
			// Only meaningful for weapons, armor and shields
			//
			if (eqI.isWeapon() || eqI.isArmor() || eqI.isShield())
			{
				b.append(" <b>PROFICIENT</b>:").append(((aPC.isProficientWith(eqI) && eqI.meetsPreReqs()) ? "Y" : SettingsHandler.getPrereqFailColorAsHtml() + "N</font>"));
			}

			final String cString = eqI.preReqHTMLStrings(false);
			if (cString.length() > 0)
			{
				b.append(" <b>Requirements</b>:").append(cString);
			}

			String IDS = eqI.getInterestingDisplayString();
			if (IDS.length() > 0)
			{
				b.append(" <b>Properties</b>:").append(eqI.getInterestingDisplayString());
			}

			String bString = eqI.getWeight().toString();
			if (bString.length() > 0)
			{
				b.append(" <b>WT</b>:").append(bString);
			}

			Integer a = eqI.getACBonus();
			if (a.intValue() > 0)
			{
				b.append(" <b>AC</b>:").append(a.toString());
			}
			if (eqI.isArmor() || eqI.isShield())
			{
				a = eqI.getMaxDex();
				b.append(" <b>MAXDEX</b>:").append(a.toString());
				a = eqI.acCheck();
				b.append(" <b>ACCHECK</b>:").append(a.toString());
			}
			if (Globals.getGameModeShowSpellTab())
			{
				a = eqI.spellFailure();
				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>Arcane Failure</b>:").append(a.toString());
				}
			}
			bString = Globals.getGameModeDamageResistanceText();
			if (bString.length() != 0)
			{
				a = eqI.eDR();
				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>").append(bString).append("</b>:").append(a.toString());
				}
			}

			bString = eqI.moveString();
			if (bString.length() > 0)
			{
				b.append(" <b>Move</b>:").append(bString);
			}
			bString = eqI.getSize();
			if (bString.length() > 0)
			{
				b.append(" <b>Size</b>:").append(bString);
			}
			bString = eqI.getDamage();
			if (bString.length() > 0)
			{
				b.append(" <b>Damage</b>:").append(bString);
				if (eqI.isDouble())
				{
					b.append('/').append(eqI.getAltDamage());
				}
			}
			bString = eqI.getCritRange();
			if (bString.length() > 0)
			{
				b.append(" <b>Crit Range</b>:").append(bString);
				if (eqI.isDouble() && !eqI.getCritRange().equals(eqI.getAltCritRange()))
				{
					b.append('/').append(eqI.getAltCritRange());
				}
			}
			bString = eqI.getCritMult();
			if (bString.length() > 0)
			{
				b.append(" <b>Crit Mult</b>:").append(bString);
				if (eqI.isDouble() && !eqI.getCritMult().equals(eqI.getAltCritMult()))
				{
					b.append('/').append(eqI.getAltCritMult());
				}
			}
			if (eqI.isWeapon())
			{
				bString = eqI.getRange().toString();
				if (bString.length() > 0)
				{
					b.append(" <b>Range</b>:").append(bString);
				}
			}
			bString = eqI.getContainerCapacityString();
			if (bString.length() > 0)
			{
				b.append(" <b>Container</b>:").append(bString);
			}
			bString = eqI.getContainerContentsString();
			if (bString.length() > 0)
			{
				b.append(" <b>Currently Contains</b>:").append(bString);
				BigDecimal d = new BigDecimal(String.valueOf(eqI.getContainedValue()));
				String aVal = BigDecimalHelper.formatBigDecimal(d, 2).toString();
				b.append(" <b>Contained Value</b>:").append(aVal).append("gp");
			}
			final int charges = eqI.getRemainingCharges();
			if (charges >= 0)
			{
				b.append(" <b>Charges</b>:").append(charges);
			}

			b.append(" <b>Cost</b>:").append(eqI.getCost());

			bString = eqI.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE:</b> ").append(bString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	private void setTableSelectedIndex(JTreeTable aTable, int idx)
	{
		aTable.setRowSelectionInterval(idx, idx);
	}

	private static int getTableSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	private final void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		final JTree atree = availableTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());
		atree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		availableTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					int iRow = getTableSelectedIndex(e);
					TreePath avaPath = atree.getPathForRow(iRow);
					if (iRow < 0)
					{
						avaPath = atree.getSelectionPath();
					}
					if (avaPath == null)
					{
						return;
					}

					Object temp = avaPath.getLastPathComponent();
					if (temp == null)
					{
						lastEquip = null;
						infoLabel.setText();
						return;
					}

					myPONode fNode = (myPONode) temp;
					if (fNode.getItem() instanceof Equipment)
					{
						Equipment eqI = (Equipment) fNode.getItem();
						if (eqI != null)
						{
							AddMenu.setEnabled(true);
							AddNumMenu.setEnabled(true);
							AddAllMenu.setEnabled(true);
							addEquipButton.setEnabled(true);
							setInfoLabelText(eqI);
						}
					}
					else
					{
						AddMenu.setEnabled(false);
						AddNumMenu.setEnabled(false);
						AddAllMenu.setEnabled(false);
						addEquipButton.setEnabled(false);
					}
				}
			}
		});

		MouseListener aml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final TreePath avaPath = atree.getPathForLocation(e.getX(), e.getY());
				if (avaPath != null)
				{
					if (e.getClickCount() == 2)
					{
						addEquipButton(new Float(1));
					}
					else if ((e.getClickCount() == 1) && e.isControlDown())
					{
						if (atree.isPathSelected(avaPath))
						{
							atree.removeSelectionPath(avaPath);
						}
						else if (!atree.isPathSelected(avaPath))
						{
							atree.addSelectionPath(avaPath);
						}
					}
				}
			}
		};
		atree.addMouseListener(aml);


		// now do the selectedTable and selectedTree

		selectedTable = new JTreeTable(selectedModel);
		final JTree stree = selectedTable.getTree();
		stree.setRootVisible(false);
		stree.setShowsRootHandles(true);
		stree.setCellRenderer(new LabelTreeCellRenderer());
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					int iRow = getTableSelectedIndex(e);
					TreePath vcSelPath = stree.getPathForRow(iRow);
					if (iRow < 0)
					{
						vcSelPath = stree.getSelectionPath();
					}
					if (vcSelPath == null)
					{
						return;
					}
					if (!stree.isSelectionEmpty())
					{
						final myPONode fn = (myPONode) vcSelPath.getPathComponent(1);
						final EquipSet eSet = (EquipSet) fn.getItem();
						if ((eSet != null) && (!eSet.getName().equals(equipSetTextField.getText())))
						{
							equipSetTextField.setText(eSet.getName());
							selectedEquipSet = eSet.getName();
							final int index = viewComboBox.getSelectedIndex();
							if (index == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
							{
								updateAvailableModel();
							}
						}
					}

					final Object temp = vcSelPath.getLastPathComponent();
					if (temp == null)
					{
						lastEquip = null;
						infoLabel.setText();
						return;
					}

					myPONode fNode = (myPONode) temp;
					if (fNode.getItem() instanceof EquipSet)
					{
						EquipSet eSet = (EquipSet) fNode.getItem();
						Equipment eqI = eSet.getItem();
						if (eqI == null)
						{
							CopyEquipSetMenu.setEnabled(true);
							RenameEquipSetMenu.setEnabled(true);
							DelMenu.setEnabled(false);
							SetQtyMenu.setEnabled(false);
							SetLocationMenu.setEnabled(false);
							SetNoteMenu.setEnabled(false);
							delEquipButton.setEnabled(false);
							setQtyButton.setEnabled(false);
							setNoteButton.setEnabled(false);
						}
						if (eqI != null)
						{
							CopyEquipSetMenu.setEnabled(false);
							RenameEquipSetMenu.setEnabled(false);
							DelMenu.setEnabled(true);
							SetQtyMenu.setEnabled(true);
							SetLocationMenu.setEnabled(true);
							SetNoteMenu.setEnabled(true);
							delEquipButton.setEnabled(true);
							setQtyButton.setEnabled(true);
							setNoteButton.setEnabled(true);
							setInfoLabelText(eqI);
						}
					}
				}
			}
		});

		MouseListener sml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final TreePath mlSelPath = stree.getPathForLocation(e.getX(), e.getY());
				if (mlSelPath != null)
				{
					if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								delEquipButton();
							}
						});
					}
					else if ((e.getClickCount() == 1) && e.isControlDown())
					{
						if (stree.isPathSelected(mlSelPath))
						{
							stree.removeSelectionPath(mlSelPath);
						}
						else if (!stree.isPathSelected(mlSelPath))
						{
							stree.addSelectionPath(mlSelPath);
						}
					}
				}
			}
		};
		stree.addMouseListener(sml);

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	/**
	 * Redraw/recalc everything
	 **/
	private void refreshButton()
	{
		needsUpdate = true;
		updateCharacterInfo();
	}

	/**
	 * Changed the view Sort for available Equipment
	 **/
	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setEquipTab_AvailableListMode(viewMode);
			updateAvailableModel();
		}
	}

	/**
	 * Changes the EquipSet used to calculate/output to Output sheets
	 **/
	private void calcComboBoxActionPerformed()
	{
		final EquipSet eSet = (EquipSet) calcComboBox.getSelectedItem();
		if (eSet != null)
		{
			final String eqSetId = eSet.getIdPath();
			if (!eqSetId.equals(aPC.getCalcEquipSetId()))
			{
				aPC.setCalcEquipSetId(eqSetId);
				aPC.setCalcEquipmentList();
				aPC.setDirty(true);
				updateTotalWeight();
				// now Update all the other tabs
				PCGen_Frame1.forceUpdate_InfoAbilities();
				PCGen_Frame1.forceUpdate_InfoClasses();
				PCGen_Frame1.forceUpdate_InfoFeats();
				PCGen_Frame1.forceUpdate_InfoSkills();
				PCGen_Frame1.forceUpdate_InfoSpells();
				PCGen_Frame1.forceUpdate_InfoSummary();
			}
		}
	}

	/**
	 * Load the EquipSet Calc dropdown with all EquipSets
	 **/
	private void calcComboBoxFill()
	{
		List calcList = new ArrayList(1);
		calcComboBox.removeAllItems();
		equipSetList = aPC.getEquipSet();

		// loop through all root EquipSet's and add
		// to calcComboBox list
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().equals("0") &&
				!calcList.contains(es.getIdPath()))
			{
				calcList.add(es.getIdPath());
				calcComboBox.addItem(es);
			}
		}

		EquipSet cES = aPC.getEquipSetByIdPath(aPC.getCalcEquipSetId());
		if (cES == null)
		{
			return;
		}
		for (int i = 0; i <= calcComboBox.getItemCount(); i++)
		{
			EquipSet es = (EquipSet) calcComboBox.getItemAt(i);
			if ((es != null) && es.getIdPath().equals(cES.getIdPath()))
			{
				calcComboBox.setSelectedIndex(i);
			}
		}
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown()
	{
		PCGen_Frame1.getStatusBar().setText("Select containers to add equipment to them");

		updateCharacterInfo();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			Component c = getParent();
			s = SettingsHandler.getPCGenOption("InfoEquipping.splitPane", (c.getWidth() * 7 / 10));
			t = SettingsHandler.getPCGenOption("InfoEquipping.bsplit", (c.getHeight() - 101));
			u = SettingsHandler.getPCGenOption("InfoEquipping.asplit", (c.getWidth() - 408));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("EquipSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "EquipSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("EquipAva", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "EquipAva", i));
			}

			// have to add this here otherwise it fires before
			// we have everything setup correctly
			calcComboBox.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					calcComboBoxActionPerformed();
				}
			});
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoEquipping.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoEquipping.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoEquipping.asplit", u);
		}

	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 * But first test to see if we need to do anything
	 **/
	public final void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != aPC)
		{
			needsUpdate = true;
		}
		aPC = bPC;
		if (aPC == null || !needsUpdate)
		{
			return;
		}

		EquipSet cES = aPC.getEquipSetByIdPath(aPC.getCalcEquipSetId());
		if (cES != null)
		{
			selectedEquipSet = cES.getName();
			equipSetTextField.setText(selectedEquipSet);
		}

		calcComboBoxFill();
		updateTotalWeight();

		updateAvailableModel();
		updateSelectedModel();

		needsUpdate = false;
	}

	/**
	 * Adds iQty of equipment to an EquipSet
	 **/
	private void addNumEquipButton()
	{
		Float newQty;
		Object selectedValue = GuiFacade.showInputDialog(null, "Enter Quantity", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);
		if (selectedValue != null)
		{
			try
			{
				newQty = new Float(((String) selectedValue).trim());
			}
			catch (Exception e)
			{
				GuiFacade.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return;
			}
			addEquipButton(newQty);
		}
	}

	/**
	 * Adds equipment to an EquipSet
	 **/
	private void addEquipButton(Float newQty)
	{
		if (selectedTable.getTree().isSelectionEmpty())
		{
			GuiFacade.showMessageDialog(null, "First select an Equip Set to add the item to", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		TreePath aeSelPath = selectedTable.getTree().getSelectionPath();
		String equipSetName = equipSetTextField.getText();
		String locName = "";
		myPONode parentNode;
		Equipment eqTarget = null;
		Equipment eqI;
		Equipment eq;
		EquipSet eSet = null;
		EquipSet newSet;

		Object endComp = aeSelPath.getLastPathComponent();
		parentNode = (myPONode) endComp;
		if ((parentNode.getItem() instanceof EquipSet))
		{
			eSet = (EquipSet) parentNode.getItem();
			if (!"".equals(eSet.getValue()))
			{
				eqTarget = eSet.getItem();
				if (eqTarget == null)
				{
					eqTarget = Globals.getEquipmentNamed(eSet.getValue());
				}
				if (!eqTarget.isContainer())
				{
					eSet = getCurrentEquipSet();
					parentNode = (myPONode) aeSelPath.getPathComponent(1);
				}
			}
		}

		if (eSet == null || parentNode == null)
		{
			GuiFacade.showMessageDialog(null, "First select an Equip Set to add the item to", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		TreePath avaCPaths[] = availableTable.getTree().getSelectionPaths();
		for (int index = 0; index < avaCPaths.length; ++index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			myPONode fNode = (myPONode) aComp;

			if (!(fNode.getItem() instanceof Equipment))
			{
				return;
			}

			// get the equipment Item from the available Table
			eq = (Equipment) fNode.getItem();
			int outIndex = eq.getOutputIndex();
			eqI = (Equipment) eq.clone();
			eqI.setOutputIndex(outIndex);

			// Add the item of equipment
			newSet = addEquipToTarget(eSet, eqTarget, locName, eqI, newQty);

			// add EquipSet into the selectedTable tree
			if (newSet != null)
			{
				myPONode fN = new myPONode();
				fN.setItem(newSet);
				fN.setParent(parentNode);
				parentNode.addChild(fN);
			}
			else
			{
				Logging.errorPrint("Could not add Equipment");
				return;
			}
		}

		selectedEquipSet = equipSetName;
		updateTotalWeight();
		updateSelectedModel();

		final int index = viewComboBox.getSelectedIndex();
		if (index == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
		{
			updateAvailableModel();
		}

		// Make sure equipment based bonuses get recalculated
		aPC.calcActiveBonuses();

	}

	/**
	 * Change Location of equipment in an EquipSet
	 **/
	private void setLocationButton()
	{
		TreePath slSelPath = selectedTable.getTree().getSelectionPath();
		EquipSet eSet = null;
		EquipSet rootSet = null;
		String locName = "";
		Equipment eqI = null;

		if (slSelPath == null)
		{
			GuiFacade.showMessageDialog(null, "First select an Item to change location", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		else
		{
			Object endComp = slSelPath.getLastPathComponent();
			myPONode fNode = (myPONode) endComp;
			if ((fNode.getItem() instanceof EquipSet))
			{
				eSet = (EquipSet) fNode.getItem();
				rootSet = aPC.getEquipSetByIdPath(eSet.getRootIdPath());
				eqI = eSet.getItem();
			}
		}

		if (eqI == null)
		{
			GuiFacade.showMessageDialog(null, "First select an Item to change location", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		// if the eSet.getIdPath() is longer than 3
		// it's inside a container, so bail out
		StringTokenizer aTok = new StringTokenizer(eSet.getIdPath(), ".", false);
		if (aTok.countTokens() > 3)
		{
			GuiFacade.showMessageDialog(null, "Can not change item locations inside a container", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		List containers = new ArrayList();
		// get the possible locations for this item
		List aList = locationChoices(eqI, containers);

		// let them choose where to put the item
		ChooserRadio c = ChooserFactory.getRadioInstance();
		if (containers.size() != 0)
		{
			c.setComboData("Container", containers);
		}
		c.setAvailableList(aList);
		c.setVisible(false);
		c.setTitle(eqI.getName());
		c.setMessageText("Select a location for this item");
		c.show();

		Equipment eqTarget = null;
		EquipSet eTargetSet = null;

		aList = c.getSelectedList();
		if (c.getSelectedList().size() > 0)
		{
			final Object loc = aList.get(0);
			if (loc instanceof String)
			{
				locName = (String) loc;
			}
			else
			{
				eTargetSet = ((EqSetWrapper) loc).getEqSet();
				eqTarget = eTargetSet.getItem();
				rootSet = aPC.getEquipSetByIdPath(eTargetSet.getIdPath());
				if (eqTarget.canContain(eqI) == 1)
				{
					locName = eqTarget.getName();
				}
				else
				{
					GuiFacade.showMessageDialog(null, "Container " + eqTarget.getName() + " is full", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					return;
				}
			}
		}
		if ("".equals(locName) || locName.length() == 0)
		{
			return;
		}

		if ((eTargetSet != null) && eSet.getIdPath().equals(eTargetSet.getIdPath()))
		{
			GuiFacade.showMessageDialog(null, "Can not put an item inside itself", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(rootSet, locName, eqI, eqTarget))
		{
			GuiFacade.showMessageDialog(null, "Can not equip " + eqI.getName() + " to " + locName, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		if ((eqTarget != null) && eqTarget.isContainer())
		{
			eqTarget.insertChild(eqI);
			eqI.setParent(eqTarget);
		}

		if (eTargetSet != null)
		{
			final String oldPath = eSet.getIdPath();

			// if we are moving this item into a container
			// we need to construct a new IdPath
			eSet.setIdPath(getNewIdPath(eTargetSet));

			// if the item we are moving is a container
			// need to also move all the items it may contain
			if (eqI.isContainer())
			{
				for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
				{
					EquipSet es = (EquipSet) e.next();
					if (es.getParentIdPath().startsWith(oldPath))
					{
						es.setIdPath(getNewIdPath(eSet));
					}
				}
			}
		}

		// change the location of the equipment
		eSet.setName(locName);

		// reset EquipSet model to get the new equipment
		// added into the selectedTable tree
		aPC.setDirty(true);
		updateSelectedModel();

		// Make sure equipment based bonuses get recalculated
		aPC.calcActiveBonuses();

	}

	/**
	 * Handle a user request to add all of the selected item
	 **/
	private void addAllEquipButton()
	{
		addEquipButton(new Float(-1));
	}

	/**
	 * Add the specified item of equipment to the provided equipset
	 * The location the item is added is either eqTarget and locName
	 * If eqTarget is set, it will override the locName setting
	 **/
	private EquipSet addEquipToTarget(EquipSet eSet, Equipment eqTarget, String locName, Equipment eqI, Float newQty)
	{
		Float tempQty = newQty;
		boolean addAll = false;
		boolean mergeItem = false;
		// if newQty is less than zero, we want to
		// add all of this item to the EquipSet
		// or all remaining items that havn't already
		// been added to the EquipSet
		if (newQty.floatValue() < 0.0f)
		{
			tempQty = diffItemCount(eSet, eqI);
			newQty = new Float(tempQty.floatValue() + existingQty(eSet, eqI).floatValue());
			addAll = true;
		}

		// Check to make sure this EquipSet does not exceed
		// the PC's equipmentList number for this item
		if (tempQty.floatValue() > diffItemCount(eSet, eqI).floatValue())
		{
			GuiFacade.showMessageDialog(null, "You have already added all your " + eqI.getName(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return null;
		}

		// check to see if the target item is a container
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			// set these to newQty just for testing
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);
			// Make sure the container accepts items
			// of this type and is not full
			if (eqTarget.canContain(eqI) == 1)
			{
				locName = eqTarget.getName();
				addAll = true;
				mergeItem = true;
			}
			else
			{
				GuiFacade.showMessageDialog(null, "Container " + eqTarget.getName() + " is full", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return null;
			}
		}
		if ("".equals(locName) || locName.length() == 0)
		{
			List containers = new ArrayList();

			// get the possible locations for this item
			List aList = locationChoices(eqI, containers);
			locName = getSingleLocation(eqI);

			if ((locName.length() != 0) && canAddEquip(eSet, locName, eqI, eqTarget))
			{
				// seems to be the right choice
			}
			else
			{
				// let them choose where to put the item
				ChooserRadio c = ChooserFactory.getRadioInstance();
				if (containers.size() != 0)
				{
					c.setComboData("Container", containers);
				}
				c.setAvailableList(aList);
				c.setVisible(false);
				c.setTitle(eqI.getName());
				c.setMessageText("Select a location for this item");
				c.show();
				aList = c.getSelectedList();
				if (c.getSelectedList().size() > 0)
				{
					Object loc = aList.get(0);
					if (loc instanceof String)
					{
						locName = (String) loc;
						mergeItem = true;
					}
					else
					{
						eSet = ((EqSetWrapper) loc).getEqSet();
						eqTarget = eSet.getItem();
						if (eqTarget.canContain(eqI) == 1)
						{
							locName = eqTarget.getName();
							addAll = true;
							mergeItem = true;
						}
						else
						{
							GuiFacade.showMessageDialog(null, "Container " + eqTarget.getName() + " is full", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
							return null;
						}
					}
				}
			}
		}

		if ("".equals(locName) || locName.length() == 0)
		{
			return null;
		}

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(eSet, locName, eqI, eqTarget))
		{
			GuiFacade.showMessageDialog(null, "Can not equip " + eqI.getName() + " to " + locName, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return null;
		}

		EquipSet existingSet = existingItem(eSet, eqI);
		if (addAll && mergeItem && (existingSet != null))
		{
			newQty = new Float(tempQty.floatValue() + existingQty(eSet, eqI).floatValue());
			existingSet.setQty(newQty);
			eqI.setQty(newQty);
			eqI.setNumberCarried(newQty);
			aPC.setDirty(true);
			if ((eqTarget != null) && eqTarget.isContainer())
			{
				eqTarget.updateContainerContentsString();
			}
			return existingSet;
		}
		else
		{
			if ((eqTarget != null) && eqTarget.isContainer())
			{
				eqTarget.insertChild(eqI);
				eqI.setParent(eqTarget);
			}
			// construct the new IdPath
			// new id is one larger than any
			// other id at this path level
			String id = getNewIdPath(eSet);

			// now create a new EquipSet to add
			// this Equipment item to
			EquipSet newSet = new EquipSet(id, locName, eqI.getName(), eqI);
			// set the Quantity of equipment
			eqI.setQty(newQty);
			newSet.setQty(newQty);

			aPC.addEquipSet(newSet);
			aPC.setDirty(true);

			// now Update all the other tabs
			PCGen_Frame1.forceUpdate_InfoAbilities();
			PCGen_Frame1.forceUpdate_InfoClasses();
			PCGen_Frame1.forceUpdate_InfoFeats();
			PCGen_Frame1.forceUpdate_InfoSkills();
			PCGen_Frame1.forceUpdate_InfoSpells();
			PCGen_Frame1.forceUpdate_InfoSummary();

			return newSet;
		}
	}

	/**
	 * removes an item from the selected EquipSet
	 **/
	private void delEquipButton()
	{
		TreePath delSelPath = selectedTable.getTree().getSelectionPath();

		if (delSelPath == null)
		{
			GuiFacade.showMessageDialog(null, "Select the Equipment to remove from this set", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		TreePath selCPaths[] = selectedTable.getTree().getSelectionPaths();
		for (int index = 0; index < selCPaths.length; ++index)
		{

			Object endComp = selCPaths[index].getLastPathComponent();
			myPONode fNode = (myPONode) endComp;

			if (!(fNode.getItem() instanceof EquipSet))
			{
				return;
			}

			final EquipSet eSet = (EquipSet) fNode.getItem();

			// only allow this button to delete equipment
			// not the root EquipSet node
			if (eSet.getItem() == null)
			{
				GuiFacade.showMessageDialog(null, "Use this to remove equipment, not the Equipment Set itself", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return;
			}

			Equipment eqI = eSet.getItem();
			StringTokenizer aTok = new StringTokenizer(eSet.getIdPath(), ".", false);

			// remove Equipment (via EquipSet) from the PC
			aPC.delEquipSet(eSet);

			// if it was inside a container, make sure to update
			// the container Equipment Object
			if (aTok.countTokens() > 3)
			{
				Equipment eqP = (Equipment) eqI.getParent();
				if (eqP != null)
				{
					eqP.removeChild(eqI);
				}
			}

		}

		aPC.setDirty(true);
		updateTotalWeight();
		updateSelectedModel();

		// Make sure equipment based bonuses get recalculated
		aPC.calcActiveBonuses();

		final int index = viewComboBox.getSelectedIndex();
		if (index == GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED)
		{
			updateAvailableModel();
		}

		// now Update all the other tabs
		PCGen_Frame1.forceUpdate_InfoAbilities();
		PCGen_Frame1.forceUpdate_InfoClasses();
		PCGen_Frame1.forceUpdate_InfoFeats();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();

	}

	/**
	 * add additional information to an equipset
	 **/
	private void setNoteButton()
	{
		TreePath noteSelPath = selectedTable.getTree().getSelectionPath();

		if (noteSelPath == null)
		{
			GuiFacade.showMessageDialog(null, "Select the Equipment first", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		String pid;

		Object endComp = noteSelPath.getLastPathComponent();
		myPONode fNode = (myPONode) endComp;

		if (!(fNode.getItem() instanceof EquipSet))
		{
			return;
		}

		EquipSet eSet = (EquipSet) fNode.getItem();

		// now make sure we have this PC's EquipSet
		pid = eSet.getIdPath();
		eSet = aPC.getEquipSetByIdPath(pid);
		Equipment eqI = eSet.getItem();

		if (eqI == null)
		{
			GuiFacade.showMessageDialog(null, "Select the Equipment first", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		String newNote;

		Object selectedValue = GuiFacade.showInputDialog(null, "Enter new Note", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE, null, null, eSet.getNote());
		if (selectedValue != null)
		{
			newNote = ((String) selectedValue).trim();
		}
		else
		{
			// canceled, so just return
			return;
		}

		if (newNote != null)
		{
			eSet.setNote(newNote);
		}

		aPC.setDirty(true);
		updateSelectedModel();

	}

	/**
	 * sets the quantity carried
	 **/
	private void setQtyButton(Float aQty)
	{
		TreePath qtySelPath = selectedTable.getTree().getSelectionPath();

		if (qtySelPath == null)
		{
			GuiFacade.showMessageDialog(null, "Select the Equipment first", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		String pid;

		Object endComp = qtySelPath.getLastPathComponent();
		myPONode fNode = (myPONode) endComp;

		if (!(fNode.getItem() instanceof EquipSet))
		{
			return;
		}

		EquipSet eSet = (EquipSet) fNode.getItem();

		// now make sure we have this PC's EquipSet
		pid = eSet.getIdPath();
		eSet = aPC.getEquipSetByIdPath(pid);
		Equipment eqI = eSet.getItem();
		StringTokenizer aTok = new StringTokenizer(eSet.getIdPath(), ".", false);

		if (eqI == null)
		{
			GuiFacade.showMessageDialog(null, "Select the Equipment first", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		// only allow this button to change the quantity
		// of carried items or items inside a container
		if (eqI.isContainer())
		{
			GuiFacade.showMessageDialog(null, "Can not change quantity of containers", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		if ((aTok.countTokens() <= 3) && eqI.isEquipped())
		{
			GuiFacade.showMessageDialog(null, "Can not change quantity of Equipped items", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		Float currentNum = eSet.getQty();
		Float numCarried = eqI.getCarried();
		float newNum;

		if (aQty.floatValue() <= 0.0f)
		{
			Object selectedValue = GuiFacade.showInputDialog(null, "Enter new Quantity", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);
			if (selectedValue != null)
			{
				try
				{
					newNum = Float.parseFloat(((String) selectedValue).trim());
				}
				catch (Exception e)
				{
					GuiFacade.showMessageDialog(null, "Invalid number!", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				// canceled, so just return
				return;
			}
		}
		else
		{
			newNum = aQty.floatValue();
		}

		// if the new number is the same as the old number,
		// just return as there is nothing to do
		if (pcgen.core.utils.Utility.doublesEqual(newNum, currentNum.floatValue()))
		{
			return;
		}

		float addNum = newNum;
		// if there are existing items, then subtract that from
		// the desired new total to get the right amount to add
		if (currentNum.floatValue() > 0)
		{
			addNum = newNum - currentNum.floatValue();
		}


		// Check to make sure this EquipSet does not exceed
		// the PC's equipmentList number for this item
		if (addNum > diffItemCount(eSet, eqI).floatValue())
		{
			GuiFacade.showMessageDialog(null, "You do not have " + newNum + " " + eqI.getName(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		// Equipment is inside a container, so we have to check to
		// make sure the container can hold that many
		if (aTok.countTokens() > 3)
		{
			Equipment eqP = (Equipment) eqI.getParent();
			// set these to new values for testing
			eqI.setQty(new Float(addNum));
			eqI.setNumberCarried(new Float(addNum));
			// Make sure the container accepts items
			// of this type and is not full
			if (eqP.canContain(eqI) != 1)
			{
				// set back to old values
				eqI.setQty(currentNum);
				eqI.setNumberCarried(numCarried);
				// Send error message
				GuiFacade.showMessageDialog(null, "Container " + eqP.getName() + " is full", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return;
			}
		}

		// set the new quantity
		eSet.setQty(new Float(newNum));
		eqI.setNumberCarried(new Float(newNum));

		aPC.setDirty(true);
		updateTotalWeight();
		updateSelectedModel();

	}

	/*
	 *****  **  **  **   **  **  ** **    **
	 **     **  **  ***  **  ** **   **  **
	 ***This is used to add new equipment Sets when
	 ***the equipSetTextField JTextField is edited
	 **     **  **  **  ***  ** **     **
	 **     ******  **   **  **  **    **
	 */
	private void addEquipSetButton()
	{
		String equipSetFieldText = equipSetTextField.getText();

		if (equipSetFieldText.equals(selectedEquipSet))
		{
			return;
		}
		EquipSet pcSet = aPC.getEquipSetByName(equipSetFieldText);
		if (pcSet != null)
		{
			return;
		}

		// get an new unique id that is one higher than any
		// other EquipSet attached to the root node
		String id = getNewIdPath(null);

		// Create a new EquipSet and assign to root node
		EquipSet eSet = new EquipSet(id, equipSetFieldText);

		aPC.setDirty(true);
		selectedEquipSet = equipSetFieldText;
		aPC.addEquipSet(eSet);
		calcComboBoxFill();
		updateSelectedModel();

	}

	/*
	 *****  **  **  **   **  **   ** **    **
	 **  ** **  **  ***  **  ***  **  **  **
	 **This deletes the EquipSet and all "children" of the set
	 **the children all have the same parent Id as this EquipSet
	 **  ** **  **  **  ***  **  ***    **
	 *****  ******  **   **  **   **    **
	 */
	private void delEquipSetButton()
	{
		String equipSetFieldText = equipSetTextField.getText();
		EquipSet eSet = aPC.getEquipSetByName(equipSetFieldText);

		if (eSet == null)
		{
			Logging.errorPrint("delEquipSetButton: No EquipSet named: " + equipSetFieldText);
			return;
		}

		int iConfirm = GuiFacade.showConfirmDialog(null, "Are you sure you want to delete?", "Confirm Remove", GuiFacade.YES_NO_OPTION);
		if (iConfirm != GuiFacade.YES_OPTION)
		{
			return;
		}

		if (aPC.delEquipSet(eSet))
		{
			aPC.setDirty(true);
			calcComboBoxFill();
			selectedEquipSet = "";
			updateSelectedModel();
		}
		else
		{
			Logging.errorPrint("delEquipSetButton:failed ");
			return;
		}

	}

	/**
	 * Process a request to rename the current equipment set
	 **/
	private void renameEquipSetButton()
	{
		EquipSet eSet;
		String newName;
		String oldName;

		eSet = getCurrentEquipSet();
		if (eSet == null)
		{
			GuiFacade.showMessageDialog(null, "First select an Equip Set to duplicate.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		oldName = eSet.getName();

		// Get a new name
		newName = GuiFacade.showInputDialog(null, "Enter new name for Equip Set", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);

		// If they are the same, just return
		if (newName.equals(oldName))
		{
			return;
		}

		// First check to make sure there are no other EquipSet's
		// with the same name (as it causes wierd problems)
		if (newName != null && newName.length() > 0)
		{
			EquipSet pcSet = aPC.getEquipSetByName(newName);
			if (pcSet != null)
			{
				GuiFacade.showMessageDialog(null, "An EquipSet all ready exists with that name.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return;
			}

			// everything looks good, so do it
			eSet.setName(newName);
			selectedEquipSet = newName;
			aPC.setDirty(true);
			calcComboBoxFill();
			updateSelectedModel();
		}
	}

	/**
	 * Process a request to duplicate the current equipment set.
	 **/
	private void copyEquipSetButton()
	{
		EquipSet eSet;
		EquipSet equipItem;
		String newName;
		String pid;
		List newEquipSet = new ArrayList();

		eSet = getCurrentEquipSet();
		if (eSet == null)
		{
			GuiFacade.showMessageDialog(null, "First select an Equip Set to duplicate.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		pid = eSet.getIdPath();

		// Get a new name
		newName = GuiFacade.showInputDialog(null, "Enter name for new Equip Set", Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);
		if ((newName == null) || (newName.length() <= 0))
		{
			return;
		}

		// First check to make sure there are no other EquipSet's
		// with the same name (as it causes wierd problems)
		EquipSet pcSet = aPC.getEquipSetByName(newName);
		if (pcSet != null)
		{
			GuiFacade.showMessageDialog(null, "An EquipSet all ready exists with that name.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		// Everything looks good, let's get a new ID and copy EQ
		// get an new unique id that is one higher than any
		// other EquipSet attached to the root node
		String id = getNewIdPath(null);

		eSet = (EquipSet) eSet.clone();
		eSet.setIdPath(id);
		eSet.setName(newName);

		selectedEquipSet = newName;
		aPC.addEquipSet(eSet);

		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getParentIdPath().startsWith(pid))
			{
				continue;
			}

			equipItem = (EquipSet) es.clone();
			equipItem.setIdPath(id + es.getIdPath().substring(pid.length()));

			newEquipSet.add(equipItem);
		}

		for (Iterator e = newEquipSet.iterator(); e.hasNext();)
		{
			aPC.addEquipSet((EquipSet) e.next());
		}
		aPC.setDirty(true);
		calcComboBoxFill();
		updateSelectedModel();
	}

	/**
	 * returns the Parent Node EquipSet
	 **/
	private EquipSet getCurrentEquipSet()
	{
		TreePath ceSelPath = selectedTable.getTree().getSelectionPath();
		String equipSetName;
		EquipSet eSet = null;

		if (ceSelPath == null)
		{
			equipSetName = equipSetTextField.getText();
			eSet = aPC.getEquipSetByName(equipSetName);
		}
		else
		{
			Object endComp = ceSelPath.getPathComponent(1);
			myPONode fNode = (myPONode) endComp;
			if ((fNode.getItem() instanceof EquipSet))
			{
				eSet = (EquipSet) fNode.getItem();
			}
		}

		return eSet;
	}

	/**
	 * If an item can only go in one location, return the name of that
	 * location to add to an EquipSet
	 **/
	private static String getSingleLocation(Equipment eqI)
	{
		// Handle natural weapons
		if (eqI.isNatural())
		{
			if (eqI.getSlots() == 0)
			{
				if (eqI.modifiedName().endsWith("Primary"))
				{
					return Constants.S_NATURAL_PRIMARY;
				}
				else
				{
					return Constants.S_NATURAL_SECONDARY;
				}
			}
		}
		// Always force weapons to go through the chooser dialog
		if (eqI.isWeapon())
		{
			return "";
		}

		List eqSlotList = SystemCollections.getUnmodifiableEquipSlotList();
		if ((eqSlotList == null) || eqSlotList.isEmpty())
		{
			return "";
		}

		for (Iterator eI = eqSlotList.iterator(); eI.hasNext();)
		{
			EquipSlot es = (EquipSlot) eI.next();

			// see if this EquipSlot can contain this item TYPE
			if (es.canContainType(eqI.getType()))
			{
				return es.getSlotName();
			}
		}
		return "";
	}

	/**
	 * Returns the current number of eqI items in eSet
	 **/
	private Float existingQty(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();
		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getIdPath().startsWith(rPath))
			{
				continue;
			}
			if (eqI.getName().equals(es.getValue()))
			{
				return es.getQty();
			}
		}
		return new Float(0);
	}

	/**
	 * Checks to see if Equipment exists in selected EquipSet
	 * and if so, then return the EquipSet containing eqI
	 **/
	private EquipSet existingItem(EquipSet eSet, Equipment eqI)
	{
		final String rPath = eSet.getIdPath();
		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getIdPath().startsWith(rPath))
			{
				continue;
			}
			if (eqI.getName().equals(es.getValue()))
			{
				return es;
			}
		}
		return null;
	}

	/**
	 * Returns the difference between the item count in the master
	 * equipment list and the item count in the EquipSet eSet
	 **/
	private Float diffItemCount(EquipSet eSet, Equipment eqI)
	{
		Float aVal = new Float(Integer.MAX_VALUE);
		final String rPath = eSet.getRootIdPath();
		float cQty = 0.0f;

		Equipment masterEq = aPC.getEquipmentNamed(eqI.getName());
		if (masterEq == null)
		{
			return aVal;
		}
		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getIdPath().startsWith(rPath))
			{
				continue;
			}
			if (eqI.getName().equals(es.getValue()))
			{
				cQty += es.getQty().floatValue();
			}
		}
		if (cQty <= masterEq.getQty().floatValue())
		{
			aVal = new Float(masterEq.getQty().floatValue() - cQty);
		}
		return aVal;
	}

	/**
	 * returns true if you can put Equipment into a location in EquipSet
	 **/
	private static boolean canAddEquip(EquipSet eSet, String locName, Equipment eqI, Equipment eqTarget)
	{
		final String idPath = eSet.getIdPath();

		// If target is a container, allow it
		if ((eqTarget != null) && eqTarget.isContainer())
		{
			return true;
		}

		// If Carried/Equipped/Not Carried slot
		// allow as many as they would like
		if (locName.startsWith(Constants.S_CARRIED) ||
			locName.startsWith(Constants.S_EQUIPPED) ||
			locName.startsWith(Constants.S_NOTCARRIED))
		{
			return true;
		}

		// allow as many unarmed items as you'd like
		if (eqI.isUnarmed())
		{
			return true;
		}
		// allow many Secondary Natural weapons
		if (locName.equals(Constants.S_NATURAL_SECONDARY))
		{
			return true;
		}
		// Don't allow weapons that are too large for PC
		if (eqI.isWeapon() && Globals.isWeaponTooLargeForPC(aPC, eqI))
		{
			return false;
		}

		// make a HashMap to keep track of the number of each
		// item that is already equipped to a slot
		Map slotMap = new HashMap();

		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getParentIdPath().startsWith(idPath))
			{
				continue;
			}
			// check to see if we already have
			// an item in that particular location
			if (es.getName().equals(locName))
			{
				final Equipment eItem = es.getItem();
				final String nString = (String) slotMap.get(locName);
				int existNum = 0;
				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}
				if (eItem != null)
				{
					existNum += eItem.getSlots();
				}
				slotMap.put(locName, String.valueOf(existNum));
			}
		}

		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (!es.getParentIdPath().startsWith(idPath))
			{
				continue;
			}

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (eqI.isWeapon())
			{
				// weapons can never occupy the same slot
				if (es.getName().equals(locName))
				{
					return false;
				}
				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(Constants.S_BOTH) || locName.equals(Constants.S_DOUBLE) || locName.equals(Constants.S_TWOWEAPONS)) &&
					(es.getName().equals(Constants.S_PRIMARY) || es.getName().equals(Constants.S_SECONDARY) || es.getName().equals(Constants.S_BOTH) || es.getName().equals(Constants.S_DOUBLE) || es.getName().equals(Constants.S_TWOWEAPONS)))
				{
					return false;
				}
				// inverse of above case
				if ((locName.equals(Constants.S_PRIMARY) || locName.equals(Constants.S_SECONDARY)) &&
					(es.getName().equals(Constants.S_BOTH) || es.getName().equals(Constants.S_DOUBLE) || es.getName().equals(Constants.S_TWOWEAPONS)))
				{
					return false;
				}
			}

			// If we already have an item in that location
			// check to see how many are allowed in that slot
			if (es.getName().equals(locName))
			{
				final String nString = (String) slotMap.get(locName);
				int existNum = 0;
				if (nString != null)
				{
					existNum = Integer.parseInt(nString);
				}
				existNum += eqI.getSlots();

				EquipSlot eSlot = Globals.getEquipSlotByName(locName);
				if (eSlot == null)
				{
					return true;
				}
				// if the item takes more slots, return false
				if (existNum > eSlot.getSlotCount() + (int) aPC.getTotalBonusTo("SLOTS", eSlot.getContainType()))
				{
					return false;
				}
				return true;
			}
		}
		return true;
	}

	/**
	 * returns the primary location Name an an equipment item
	 **/
	private static String getEqTypeName(Equipment eqI)
	{
		String locTypeName = "";

		if (eqI.isWeapon())
		{
			locTypeName = "Weapon";
		}
		else if (eqI.isArmor())
		{
			locTypeName = "Armor";
		}
		else if (eqI.isShield())
		{
			locTypeName = "Shield";
		}
		else if (eqI.isAmmunition())
		{
			locTypeName = "Ammo";
		}
		else if (eqI.isSuit())
		{
			locTypeName = "Suit";
		}
		else if (eqI.isMonk())
		{
			locTypeName = "Monk";
		}
		else if (eqI.isUnarmed())
		{
			locTypeName = Constants.S_UNARMED;
		}
		else if (eqI.isContainer())
		{
			locTypeName = "Container";
		}
		else if (eqI.isType("ROBE"))
		{
			locTypeName = "Robe";
		}
		else if (eqI.isType("HEADGEAR"))
		{
			locTypeName = "Headgear";
		}
		else if (eqI.isType("EYEGEAR"))
		{
			locTypeName = "Eyegear";
		}
		else if (eqI.isType("MASK"))
		{
			locTypeName = "Mask";
		}
		else if (eqI.isType("AMULET") || eqI.isType("NECKLACE"))
		{
			locTypeName = "Amulet";
		}
		else if (eqI.isType("CAPE") || eqI.isType("CLOAK"))
		{
			locTypeName = "Cape";
		}
		else if (eqI.isType("CLOTHING"))
		{
			locTypeName = "Clothing";
		}
		else if (eqI.isType("SHIRT") || eqI.isType("VEST"))
		{
			locTypeName = "Shirt";
		}
		else if (eqI.isType("BRACER") || eqI.isType("ARMWEAR"))
		{
			locTypeName = "Bracers";
		}
		else if (eqI.isType("GLOVE"))
		{
			locTypeName = "Glove";
		}
		else if (eqI.isType("RING"))
		{
			locTypeName = "Ring";
		}
		else if (eqI.isType("BELT"))
		{
			locTypeName = "Belt";
		}
		else if (eqI.isType("BOOT"))
		{
			locTypeName = "Boot";
		}
		else if (eqI.isType("POTION"))
		{
			locTypeName = "Potion";
		}
		else if (eqI.isType("ROD"))
		{
			locTypeName = "Rod";
		}
		else if (eqI.isType("STAFF"))
		{
			locTypeName = "Staff";
		}
		else if (eqI.isType("WAND"))
		{
			locTypeName = "Wand";
		}
		else if (eqI.isType("INSTRUMENT"))
		{
			locTypeName = "Instrument";
		}
		else if (eqI.isType("BOOK"))
		{
			locTypeName = "Book";
		}
		return locTypeName;
	}

	/**
	 * returns new id_Path with the last id one higher than the current
	 * highest id for EquipSets with the same ParentIdPath
	 **/
	private static String getNewIdPath(EquipSet eSet)
	{
		String pid = "0";
		int newID = 0;
		if (eSet != null)
		{
			pid = eSet.getIdPath();
		}
		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().equals(pid) && es.getId() > newID)
			{
				newID = es.getId();
			}
		}
		++newID;
		return pid + '.' + newID;
	}

	/**
	 * This method gets a list of locations for a weapon
	 **/
	private static List getWeaponLocationChoices(final int hands, final String multiHand)
	{
		final List result = new ArrayList(hands + 2);
		if (hands > 0)
		{
			result.add(Constants.S_PRIMARY);
			for (int i = 1; i < hands; ++i)
			{
				if (i > 1)
				{
					result.add(Constants.S_SECONDARY + " " + i);
				}
				else
				{
					result.add(Constants.S_SECONDARY);
				}
			}
			if (multiHand.length() > 0)
			{
				result.add(multiHand);
			}
		}
		return result;
	}

	private final List locationChoices(Equipment eqI, List containers)
	{
		// Some Equipment locations are based on the number of hands
		final PlayerCharacter currentPC = Globals.getCurrentPC();
		int hands = 0;
		if (currentPC != null)
		{
			final pcgen.core.Race race = currentPC.getRace();
			if (race != null)
			{
				hands = race.getHands();
			}
		}

		List aList = new ArrayList();

		if (eqI.isWeapon())
		{
			if (eqI.isUnarmed())
			{
				aList.add(Constants.S_UNARMED);
			}
			/*
			// Needs to be a special case for Crossbows
			// which can be fired one in each hand with a
			// -6 Primary and -10 secondary
			// -6/-6 with Ambidexterity
			// However, lot's of other .lst and code changes
			// need to happen first  -- JC  Feb 6th 2003
			else if (eqI.isRanged())
			{
				if (eqI.isThrown())
				{
					aList = getWeaponLocationChoices(hands, "");
				}
				else
				{
					aList.add(Constants.S_BOTH);
				}
			}
			*/
			else if (Globals.isWeaponLightForPC(currentPC, eqI))
			{
				aList = getWeaponLocationChoices(hands, "");
				if (eqI.isRanged() && !eqI.isThrown())
				{
					aList.add(Constants.S_BOTH);
				}
				if (eqI.isMelee())
				{
					aList.add(Constants.S_TWOWEAPONS);
				}
			}
			else
			{
				String wpSingle = eqI.profName(Equipment.EQUIPPED_PRIMARY);
				WeaponProf wp = Globals.getWeaponProfNamed(wpSingle);
				if (Globals.handsRequired(currentPC, eqI, wp) == 1)
				{
					aList = getWeaponLocationChoices(hands, Constants.S_BOTH);
					if (eqI.isMelee())
					{
						if (eqI.isDouble())
						{
							aList.add(Constants.S_DOUBLE);
						}
						else
						{
							aList.add(Constants.S_TWOWEAPONS);
						}
					}
				}
				else
				{
					aList.add(Constants.S_BOTH);
					if (eqI.isMelee() && eqI.isDouble())
					{
						aList.add(Constants.S_DOUBLE);
					}
				}
			}
		}
		else
		{
			String locName = getSingleLocation(eqI);
			if (locName.length() != 0)
			{
				aList.add(locName);
			}
			else
			{
				aList.add(Constants.S_EQUIPPED);
			}
		}
		if (!eqI.isUnarmed())
		{
			aList.add(Constants.S_CARRIED);
			aList.add(Constants.S_NOTCARRIED);
		}

		//
		// Generate a list of containers
		//
		if (containers != null)
		{
			EquipSet eqSet = getCurrentEquipSet();
			if (eqSet != null)
			{
				final String idPath = eqSet.getIdPath();
				// process all EquipSet Items
				for (int iSet = 0; iSet < currentPC.getEquipSet().size(); ++iSet)
				{
					EquipSet es = (EquipSet) currentPC.getEquipSet().get(iSet);
					if (es.getParentIdPath().startsWith(idPath))
					{
						if ((es.getItem() != null) && es.getItem().isContainer())
						{
							containers.add(new EqSetWrapper(es));
						}
					}
				}
			}
		}
		return aList;
	}

	private static class EqSetWrapper implements Serializable
	{
		private EquipSet eqSet;

		public EqSetWrapper(EquipSet argEqSet)
		{
			eqSet = argEqSet;
		}

		public String toString()
		{
			return eqSet.getItem().getName();
		}

		public EquipSet getEqSet()
		{
			return eqSet;
		}
	}

	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new EquipModel(viewMode, MODEL_AVAIL);
		}
		else
		{
			availableModel.resetModel(viewMode, MODEL_AVAIL);
		}
		if (availableSort != null)
		{
			availableSort.setRoot((myPONode) availableModel.getRoot());
			// removed by sage_sam 18 sept 2003 for bug #797574 --
			// IP items get sorted to the top due to HTML content
			// availableSort.sortNodeOnColumn();
		}
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new EquipModel(viewSelectMode, MODEL_SELECTED);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, MODEL_SELECTED);
		}
		if (selectedSort != null)
		{
			selectedSort.setRoot((myPONode) selectedModel.getRoot());
			// removed by sage_sam 18 sept 2003 for bug #797574 --
			// IP items get sorted to the top due to HTML content
			// selectedSort.sortNodeOnColumn();
		}
	}

	/**
	 * Updates the Available table
	 **/
	private void updateAvailableModel()
	{
		List pathList = availableTable.getExpandedPaths();
		createAvailableModel();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	/**
	 * Updates the Selected table
	 **/
	private void updateSelectedModel()
	{
		TreePath modelSelPath;
		List pathList = selectedTable.getExpandedPaths();

		modelSelPath = selectedTable.getTree().getSelectionPath();
		int idx = selectedTable.getTree().getRowForPath(modelSelPath);

		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);

		selectedTable.getTree().setSelectionPath(modelSelPath);
		selectedTable.getTree().expandPath(modelSelPath);

		int count = selectedTable.getTree().getRowCount();
		if ((idx >= 0) && (idx < count))
		{
			// set the selected Table row to match the Tree path
			setTableSelectedIndex(selectedTable, idx);
		}
	}

	/**
	 * The TreeTableModel has a single <code>root</code> node
	 * This root node has a null <code>parent</code>
	 * All other nodes have a parent which points to a non-null node
	 * Parent nodes contain a list of  <code>children</code>, which
	 * are all the nodes that point to it as their parent
	 * <code>nodes</code> which have 0 children are leafs (the end of
	 * that linked list)  nodes which have at least 1 child are not leafs
	 * Leafs are like files and non-leafs are like directories
	 * The leafs contain an Object that we want to know about (Equipment)
	 **/
	private final class EquipModel extends AbstractTreeTableModel
	{
		// there are two roots. One for available equipment
		// and one for selected equipment profiles
		private myPONode avaRoot;
		private myPONode selRoot;

		// if you change/add/remove entries to nameList
		// you also need to change the static COL_XXX defines
		// at the begining of this file
		private String[] avaNameList = new String[]{"Name", "Type", "Qty", "Weight", "Cost"};
		private String[] selNameList = new String[]{"Item", "Type", "Qty", "Location", "Temp Bonus"};

		// Types of the columns.
		private int modelType = MODEL_AVAIL;

		/**
		 * Creates a EquipModel
		 **/
		private EquipModel(int mode, int model)
		{
			super(null);


			modelType = model;
			resetModel(mode, model);
		}

		/**
		 * This assumes the EquipModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel(int mode, int model)
		{
			// This is the array of all equipment types
			List typeList = new ArrayList();
			List locList = new ArrayList();

			// build the list of all equipment types
			typeList.add(Constants.s_CUSTOM);
			for (Iterator iSet = aPC.getEquipmentMasterList().iterator(); iSet.hasNext();)
			{
				final Equipment bEq = (Equipment) iSet.next();
				final StringTokenizer aTok = new StringTokenizer(bEq.getType(), ".", false);
				String aString;
				while (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
					if (!typeList.contains(aString))
					{
						typeList.add(aString);
					}
				}
			}
			for (Iterator eI = SystemCollections.getUnmodifiableEquipSlotList().iterator(); eI.hasNext();)
			{
				EquipSlot eSlot = (EquipSlot) eI.next();
				final String aString = eSlot.getSlotName();
				if (!locList.contains(aString) && aString.length() > 0)
				{
					locList.add(aString);
				}
			}
			locList.add("Other");
			Collections.sort(typeList);
			Collections.sort(locList);

			// Setup the default EquipSet if not already present
			if (aPC.getEquipSet().size() == 0)
			{
				String id = getNewIdPath(null);
				EquipSet eSet = new EquipSet(id, defaultEquipSet);
				aPC.addEquipSet(eSet);

				selectedEquipSet = defaultEquipSet;
			}

			if ("".equals(selectedEquipSet))
			{
				EquipSet cES = aPC.getEquipSetByIdPath(aPC.getCalcEquipSetId());
				if (cES != null)
				{
					selectedEquipSet = cES.getName();
					equipSetTextField.setText(selectedEquipSet);
				}
			}

			//
			// build availableTable (list of all equipment)
			//
			if (model == MODEL_AVAIL)
			{
				// this is the root node
				avaRoot = new myPONode();

				switch (mode)
				{
					// Equipment Type Tree
					case GuiConstants.INFOEQUIPPING_VIEW_TYPE:
						setRoot(avaRoot);

						// build the Type root nodes
						myPONode eq[] = new myPONode[typeList.size()];
						// iterate thru the equipment
						// type and fill out the tree
						for (int iType = 0; iType < typeList.size(); ++iType)
						{
							String aType = (String) typeList.get(iType);
							eq[iType] = new myPONode(aType);
							for (Iterator fI = aPC.getEquipmentMasterList().iterator(); fI.hasNext();)
							{
								final Equipment aEq = (Equipment) fI.next();
								if (!aEq.isType(aType))
								{
									continue;
								}
								myPONode aFN = new myPONode(aEq);
								aFN.setParent(eq[iType]);
								eq[iType].addChild(aFN);
							}
							if (!eq[iType].isLeaf())
							{
								eq[iType].setParent(avaRoot);
							}
						} // end type loop
						// now add to the root node
						avaRoot.setChildren(eq);

						break; // end VIEW_TYPE

						// Equipment Location Tree
					case GuiConstants.INFOEQUIPPING_VIEW_LOCATION:
						setRoot(avaRoot);

						// build the Location root nodes
						myPONode loc[] = new myPONode[locList.size()];
						// iterate thru the equipment
						// type and fill out the tree
						for (int iLoc = 0; iLoc < locList.size(); ++iLoc)
						{
							String aLoc = (String) locList.get(iLoc);
							loc[iLoc] = new myPONode(aLoc);
							for (Iterator fI = aPC.getEquipmentMasterList().iterator(); fI.hasNext();)
							{
								final Equipment aEq = (Equipment) fI.next();
								String aString = getSingleLocation(aEq);
								if (aEq.isWeapon())
								{
									aString = "Weapon";
								}
								else if (aEq.isShield())
								{
									aString = "Shield";
								}
								else if (aEq.isType("RING"))
								{
									aString = "Fingers";
								}
								if (aString.length() == 0)
								{
									aString = "Other";
								}
								if (!aLoc.equals(aString))
								{
									continue;
								}
								myPONode aFN = new myPONode(aEq);
								aFN.setParent(loc[iLoc]);
								loc[iLoc].addChild(aFN);
							}
							if (!loc[iLoc].isLeaf())
							{
								loc[iLoc].setParent(avaRoot);
							}
						} // end location loop
						// now add to the root node
						avaRoot.setChildren(loc);

						break; // end VIEW_LOCATION

						// just by equipment name
					case GuiConstants.INFOEQUIPPING_VIEW_NAME:
						setRoot(avaRoot);

						// iterate thru all PC's equip
						// and fill out the tree
						for (Iterator fI = aPC.getEquipmentMasterList().iterator(); fI.hasNext();)
						{
							final Equipment aEq = (Equipment) fI.next();
							myPONode aFN = new myPONode(aEq);
							aFN.setParent(avaRoot);
							avaRoot.addChild(aFN, true);
						}

						break; // end VIEW_NAME

						// Equipment Added/not added Tree
					case GuiConstants.INFOEQUIPPING_VIEW_EQUIPPED:
						setRoot(avaRoot);
						// get current EquipSet
						String esN = equipSetTextField.getText();
						EquipSet pSet = aPC.getEquipSetByName(esN);
						if (pSet == null)
						{
							break;
						}

						List eqList = aPC.getEquipSet();
						String pId = pSet.getIdPath();
						myPONode add[] = new myPONode[2];
						add[0] = new myPONode(nameAdded);
						add[1] = new myPONode(nameNotAdded);
						// iterate thru all PC's equip
						// and try to find matches in
						// the current EquipSet
						for (Iterator fI = aPC.getEquipmentMasterList().iterator(); fI.hasNext();)
						{
							final Equipment aEq = (Equipment) fI.next();
							boolean found = false;
							for (Iterator es = eqList.iterator(); es.hasNext();)
							{
								EquipSet eSet = (EquipSet) es.next();
								if (found)
								{
									continue;
								}
								if (eSet.getIdPath().startsWith(pId))
								{
									Equipment eqI = eSet.getItem();
									// check to see if the names match
									if ((eqI != null) && (eqI.getName().equals(aEq.getName())))
									{
										// Found a match, add to already added Node
										myPONode aFN = new myPONode(aEq);
										aFN.setParent(add[0]);
										add[0].addChild(aFN);
										add[0].setParent(avaRoot);
										found = true;
									}
								}
							}
							if (!found)
							{
								// no match, add to NOT added Node
								myPONode aFN = new myPONode(aEq);
								aFN.setParent(add[1]);
								add[1].addChild(aFN);
								add[1].setParent(avaRoot);
							}
						}
						avaRoot.setChildren(add);

						break; // end VIEW_EQUIPPED

					default:
						Logging.errorPrint("In InfoEquipping.resetModel the mode " + mode + " is not supported.");
						break;

				} // end of switch(mode)
			} // end of availableTable builder

			else

			{ // selectedTable builder (it's a list of Equip sets)

				// this is the root node
				selRoot = new myPONode();

				// get the current EquiSet's
				equipSetList = aPC.getEquipSet();

				// Make sure it's sorted by pathId
				Collections.sort(equipSetList);

				// create a clone to manipulate
				tempSetList = (ArrayList) ((ArrayList) equipSetList).clone();

				// EquipSet tree
				addEquipTreeNodes(selRoot, null);
				setRoot(selRoot);
			} // end if else

			myPONode rootAsmyPONode = (myPONode) super.getRoot();
			if (rootAsmyPONode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}

		}

		// There must be a root node, but we keep it hidden
		private void setRoot(myPONode aNode)
		{
			super.setRoot(aNode);
		}

		// return the root node
		public Object getRoot()
		{
			return (myPONode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (EquipModel)
		 **/
		public int getColumnCount()
		{
			return modelType == MODEL_AVAIL ? avaNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column. (EquipModel)
		 **/
		public String getColumnName(int column)
		{
			return modelType == MODEL_AVAIL ? avaNameList[column] : selNameList[column];
		}

		/**
		 * Returns Class for the column. (EquipModel)
		 **/
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;

				case COL_TYPE:
				case COL_QTY:
				case COL_LOCATION:
				case COL_WEIGHT:
				case COL_COST:
				case COL_BONUS:
					break;

				default:
					Logging.errorPrint("In InfoEquipping.getColumnClass the column " + column + " is not supported.");
					break;
			}
			return String.class;
		}

		/**
		 * Returns boolean if can edit a cell. (EquipModel)
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			if (column == COL_NAME)
			{
				return true;
			}
			if ((modelType == MODEL_SELECTED) && (column == COL_COST))
			{
				return true;
			}
			else if (column == COL_QTY)
			{
				final myPONode fn = (myPONode) node;
				if ((fn != null) && (fn.getItem() instanceof EquipSet))
				{
					EquipSet eSet = (EquipSet) fn.getItem();
					if (eSet.getItem() != null)
					{
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * changes the column order sequence and/or number of
		 * columns based on modelType (0=available, 1=selected)
		 **/
		private int adjustAvailColumnConst(int column)
		{
			// available table
			if ((modelType == MODEL_AVAIL) && (column == COL_LOCATION))
			{
				return COL_WEIGHT;
			}
			if ((modelType == MODEL_SELECTED) && (column == COL_COST))
			{
				return COL_BONUS;
			}
			return column;
		}

		/**
		 * Returns Object value of the column. (EquipModel)
		 **/
		public Object getValueAt(Object node, int column)
		{
			final myPONode fn = (myPONode) node;
			EquipSet eSet = null;
			Equipment eqI = null;

			if (fn == null)
			{
				Logging.errorPrint("Somehow we have no active node when doing getValueAt in InfoEquipping.");
				return null;
			}

			column = adjustAvailColumnConst(column);

			if (fn.getItem() instanceof Equipment)
			{
				eqI = (Equipment) fn.getItem();
			}

			if (fn.getItem() instanceof EquipSet)
			{
				eSet = (EquipSet) fn.getItem();
				eqI = eSet.getItem();
			}

			switch (column)
			{
				case COL_NAME:
					// This column is a PObjectNode object
					// output is from PObjectNode.tostring()
					return fn != null ? fn.toString() : null;
				case COL_TYPE:
					if (eqI != null)
					{
						String type = getEqTypeName(eqI);
						if ("".equals(type))
						{
							StringTokenizer aTok = new StringTokenizer(eqI.getType(), ".", false);
							if (aTok.hasMoreTokens())
							{
								type = aTok.nextToken();
							}
						}
						if (eqI.isContainer() && (modelType == MODEL_SELECTED))
						{
							StringBuffer b = new StringBuffer();
							b.append("B|");
							b.append(type);
							return b.toString();
						}
						return type;
					}
					else
					{
						return null;
					}
				case COL_QTY:
					if ((eSet != null) && (eSet.getValue().length() > 0))
					{
						return BigDecimalHelper.trimZeros(eSet.getQty().toString());
					}
					else if (eqI != null)
					{
						return BigDecimalHelper.trimZeros(eqI.getQty().toString());
					}
					else
					{
						return null;
					}
				case COL_LOCATION:
					StringBuffer b = new StringBuffer();
					if ((eSet != null) && (eqI != null))
					{
						StringTokenizer aTok = new StringTokenizer(eSet.getIdPath(), ".", false);
						if (aTok.countTokens() > 3)
						{
							b.append("I|");
							b.append(eSet.toString());
						}
						else
						{
							b.append(eSet.toString());
						}
						if (eSet.getNote().length() > 0)
						{
							b.append(" [");
							b.append(eSet.getNote());
							b.append("]");
						}
						return b.toString();
					}
					if (eSet != null)
					{
						b.append(" (");
						b.append(aPC.getEquipSetWeight(eSet.getIdPath()));
						b.append(" ");
						b.append(Globals.getWeightDisplay());
						b.append(")");
						return b.toString();
					}
					return fn != null ? fn.toString() : null;
				case COL_WEIGHT:
					return eqI != null ? BigDecimalHelper.trimZeros(eqI.getWeight().toString()) : null;
				case COL_COST:
					return eqI != null ? BigDecimalHelper.trimZeros(eqI.getCost().toString()) : null;
				case COL_BONUS:
					if ((eqI == null) && (eSet != null))
					{
						// Allow bonuses?
						if (eSet.getUseTempMods())
						{
							return "Yes";
						}
						else
						{
							return "No";
						}
					}
					return null;
				default:
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Logging.errorPrint("Somehow we have no active node when doing getValueAt in InfoEquip");
						return null;
					}

			}
			// return null;
		}

		/**
		 * Used by BonusEditor to set the value of EquipSet.tempBonus
		 **/
		public void setValueAt(Object value, Object node, int column)
		{
			if ((aPC == null) || (modelType != MODEL_SELECTED))
			{
				return;
			}
			final myPONode fn = (myPONode) node;

			if (!(fn.getItem() instanceof EquipSet))
			{
				return;
			}
			EquipSet eSet = (EquipSet) fn.getItem();

			if (eSet == null)
			{
				return;
			}

			column = adjustAvailColumnConst(column);

			switch (column)
			{
				case COL_BONUS:
					int i = ((Integer) value).intValue();
					if (i == 0)
					{
						eSet.setUseTempMods(false);
					}
					else
					{
						eSet.setUseTempMods(true);
					}
					break;
				default:
					// don't do anything
					break;
			}
		}
	}

	/**
	 * This is an extend of PObjectNode so I can overload toString()
	 **/
	private final class myPONode extends PObjectNode
	{
		private myPONode()
		{
		}

		private myPONode(Object anItem)
		{
			super(anItem);
		}

		public String toString()
		{
			final Object item = super.getItem();
			if (item == null)
			{
				return "";
			}
			if (item instanceof EquipSet)
			{
				final EquipSet eSet = (EquipSet) item;
				final Equipment eqI = eSet.getItem();
				if (eSet == null)
				{
					return "";
				}
				if (eqI == null)
				{
					StringBuffer b = new StringBuffer();
					b.append("B|");
					b.append(eSet.toString());
					return b.toString();
				}
				if (eqI != null)
				{
					return eqI.piString();
				}
				return eSet.toString();
			}
			else if (item instanceof Equipment)
			{
				final Equipment eqI = (Equipment) item;
				if (eqI != null)
				{
					return eqI.piString();
				}
			}
			else
			{
				return super.toString();
			}
			return "";
		}
	}

	/**
	 * This is a recursive call to populate the PObjectNode
	 * tree structure from the EquipSet ArrayList
	 **/
	private void addEquipTreeNodes(myPONode aNode, EquipSet aSet)
	{
		// create a temporary list of EquipSets to pass to this
		// function when we recursivly call it for child nodes
		List aList = new ArrayList();

		String idPath = "0";

		if (aSet != null)
		{
			idPath = aSet.getIdPath();
		}

		// process all EquipSet Items
		for (int iSet = 0; iSet < tempSetList.size(); ++iSet)
		{
			EquipSet es = (EquipSet) tempSetList.get(iSet);
			if (es.getParentIdPath().equals(idPath))
			{
				myPONode fN = new myPONode();
				fN.setItem(es);
				fN.setParent(aNode);
				aNode.addChild(fN);

				// add to list for recursive calls
				aList.add(es);
				// and remove from tempSetList so
				// it won't get processed again
				tempSetList.remove(es);
				--iSet;
			}

		}
		// recursivly call addEquipTreeNodes to get all
		// the child EquipSet items added to each root node
		for (int i = 0; i < aList.size(); ++i)
		{
			addEquipTreeNodes((myPONode) aNode.getChild(i), (EquipSet) aList.get(i));
		}
	}

	private final class ColorRenderer extends DefaultTableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			JLabel comp = (JLabel) super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, column);

			if (value instanceof String)
			{
				Font aFont = comp.getFont();
				String fontName = aFont.getName();
				int iSize = aFont.getSize();
				String aString = comp.getText();
				if (aString.indexOf("B|") == 0)
				{
					aString = aString.substring(2, aString.length());
					Font newFont = new Font(fontName, Font.BOLD, iSize);
					comp.setFont(newFont);
				}
				else if (aString.indexOf("I|") == 0)
				{
					aString = aString.substring(2, aString.length());
					Font newFont = new Font(fontName, Font.ITALIC, iSize);
					comp.setFont(newFont);
				}
				comp.setText(aString);
			}
			return comp;
		}
	}

	/**
	 * Allows in-cell editing of a value
	 **/
	private final class QuantityEditor extends JTextField implements TableCellEditor
	{
		private final transient List d_listeners = new ArrayList();
		private transient String d_originalValue = "";

		private QuantityEditor()
		{
			super();
			setEditable(true);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object obj, boolean isSelected, int row, int column)
		{
			if (obj instanceof Number && ((Number) obj).intValue() == ((Number) obj).floatValue())
			{
				setText(Integer.toString(((Number) obj).intValue()));
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
			this.setHorizontalAlignment(SwingConstants.RIGHT);
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
			setQtyButton((Float) getCellEditorValue());
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
				((CellEditorListener) d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener) d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	/*
	 * Allows temporary bonuses on a per EquipSet basis
	 */
	private final class BonusEditor extends JComboBoxEx implements TableCellEditor
	{
		private final transient List d_listeners = new ArrayList();
		private transient int d_originalValue = 0;
		private String[] choices = new String[]{"No", "Yes"};

		private BonusEditor()
		{
			super(new String[]{"No", "Yes"});
			setEditable(true);
			addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					stopCellEditing();
				}
			});
		}

		public void addCellEditorListener(CellEditorListener cellEditorListener)
		{
			d_listeners.add(cellEditorListener);
		}

		public Component getTableCellEditorComponent(JTable jTable, Object obj, boolean isSelected, int row, int column)
		{
			if (obj == null)
			{
				return this;
			}
			d_originalValue = this.getSelectedIndex();
			if (obj instanceof Integer)
			{
				int i = ((Integer) obj).intValue();
				if (i == 0)
				{
					setSelectedItem("No");
				}
				else
				{
					setSelectedItem("Yes");
				}
			}
			else
			{
				setSelectedItem("No");
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
				case 0: // Don't use Temp Bonuses
					return new Integer(0);
				default:
					return new Integer(1);
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
				((CellEditorListener) d_listeners.get(i)).editingCanceled(ce);
			}
		}

		private void fireEditingStopped()
		{
			ChangeEvent ce = new ChangeEvent(this);
			for (int i = d_listeners.size() - 1; i >= 0; --i)
			{
				((CellEditorListener) d_listeners.get(i)).editingStopped(ce);
			}
		}
	}

	/**
	 *
	 * preview/print EquipSet templates
	 *
	 **/

	/*
	 *
	 *  Select a template for parseing the EquipSet's through
	 *
	 */
	private void selectTemplateButton()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your EquipSet template");
		fc.setCurrentDirectory(SettingsHandler.getPcgenOutputSheetDir());
		fc.setSelectedFile(new File(SettingsHandler.getSelectedEqSetTemplate()));
		if (fc.showOpenDialog(InfoEquipping.this) == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedEqSetTemplate(fc.getSelectedFile().getAbsolutePath());
			templateTextField.setText(SettingsHandler.getSelectedEqSetTemplateName());
		}
	}

	/**
	 * Previews the EquipSets through selected template in the Browser
	 **/
	private void viewEqSetButton()
	{
		final String template = SettingsHandler.getSelectedEqSetTemplate();
		File outFile = Utility.getTempPreviewFile();
		if (outFile == null)
		{
			return;
		}
		try
		{
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
			eqSetPrintToWriter(w, template);
			w.flush();
			w.close();
			URL url = outFile.toURL();
			Utility.viewInBrowser(url.toString());
		}
		catch (Exception ex)
		{
			Logging.errorPrint("Could not preview file in external browser.", ex);
		}
	}

	/**
	 * Exports the EquipSets through selected template to a file
	 **/
	private void exportEqSetButton()
	{
		final String template = SettingsHandler.getSelectedEqSetTemplate();
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getPcgPath());

		fcExport.setDialogTitle("Export EquipSet " + aPC.getDisplayName());

		if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		final String aFileName = fcExport.getSelectedFile().getAbsolutePath();
		if (aFileName.length() < 1)
		{
			GuiFacade.showMessageDialog(null, "You must set a filename.", "PCGen", GuiFacade.ERROR_MESSAGE);
			return;
		}
		try
		{
			final File outFile = new File(aFileName);
			if (outFile.isDirectory())
			{
				GuiFacade.showMessageDialog(null, "You cannot overwrite a directory with a file.", "PCGen", GuiFacade.ERROR_MESSAGE);
				return;
			}
			if (outFile.exists())
			{
				int reallyClose = GuiFacade.showConfirmDialog(this, "The file " + outFile.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + outFile.getName(), GuiFacade.YES_NO_OPTION);
				if (reallyClose != GuiFacade.YES_OPTION)
				{
					return;
				}
			}

			if (ext.equalsIgnoreCase(".htm") || ext.equalsIgnoreCase(".html"))
			{
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
				eqSetPrintToWriter(w, template);
				w.flush();
				w.close();
			}
			else if (ext.equalsIgnoreCase(".fo") || ext.equalsIgnoreCase(".pdf"))
			{

				File tmpFile = File.createTempFile("equipSet_", ".fo");
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8"));
				eqSetPrintToWriter(w, template);
				w.flush();
				w.close();

				FOPHandler fh = new FOPHandler();
				// setting up pdf renderer
				fh.setMode(FOPHandler.PDF_MODE);
				fh.setInputFile(tmpFile);
				fh.setOutputFile(outFile);

				// render to awt
				fh.run();

				tmpFile.deleteOnExit();
				String errMessage = fh.getErrorMessage();
				if (errMessage.length() > 0)
				{
					GuiFacade.showMessageDialog(null, errMessage, "PCGen", GuiFacade.ERROR_MESSAGE);
				}
			}
		}
		catch (IOException ex)
		{
			GuiFacade.showMessageDialog(null, "Could not export " + aPC.getDisplayName() + ". Try another filename", "PCGen", GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("Could not export " + aPC.getDisplayName(), ex);
		}
	}

	/**
	 * Prints the characters EquipSet's to the writer specified.
	 *
	 * @param bw  The writer to print the data to.
	 * @throws IOException  If any problems occur in writing the data
	 */
	private void eqSetPrintToWriter(BufferedWriter bw, String template) throws IOException
	{
		File esTemplate = new File(template);

		int tests[] = new int[]{0, 0};
		int length = 0;

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(esTemplate), "UTF-8"));
		while (br.readLine() != null)
		{
			++length;
		}
		br.close();

		File reParse = File.createTempFile("eqTTemp_", ".tmp");
		String reparseName = reParse.getPath();
		BufferedWriter rpW = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reparseName, true), "UTF-8"));

		// The eqsheet file can have multiple instances of the
		// |EQSET.START| |EQSET.END| tags
		// This means we have to parse the file 'til we find one,
		// output everthing to reParse file, read the stuff between
		// the start/end tags, pass it to ExportHandler.write(), take
		// the output and append to reParse file, then continue 'til
		// we find another start/end tag and repeat 'til EOF
		//
		// Once we have done this, we need to pass the entire reParse
		// to ExportHandler.write() one last time to parse the rest of
		// the (non-equipset) tags.

		File temp = File.createTempFile("eqTemp_", ".tmp");

		while (tests[0] < length)
		{
			// Pass the eqsheet template file and a temporary file
			// to the splitter. The temp file will contain part of
			// the esTemplate we need to parse/write for this loop
			int ret[] = eqsheetSplit(esTemplate, temp, tests);
			tests[0] = ret[0];

			// We found an EQSET tag, so parse it
			if (ret[1] == 1)
			{
				// parse/write EquipSet's to the reParse file
				equipItch(temp, rpW);
				rpW.flush();
			}
			else
			{
				// no EQSET tag, so just write to reParse file
				String aLine;
				BufferedReader tempReader = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "UTF-8"));
				while ((aLine = tempReader.readLine()) != null)
				{
					rpW.write(aLine);
					rpW.newLine();
				}
				rpW.flush();
				tempReader.close();
			}
		}
		// we are all done writing to the reParse, so close it
		rpW.close();

		// delete temporary file
		temp.delete();

		// make sure the buffer is ready for writing
		bw.flush();

		// Now pass reParse file to ExportHandler
		// to get reparsed and output to final destination
		(new ExportHandler(reParse)).write(aPC, bw);

		// delete temp file when done
		reParse.deleteOnExit();
	}

	/**
	 *
	 * Takes a template file and looks for delimiter
	 *     |EQSET.START|
	 * and
	 *     |EQSET.END|
	 *
	 * returns an array of int
	 * the first element is the line of the file last parsed
	 * the second element is if we need to process an EQSET loop
	 *
	 **/
	private static int[] eqsheetSplit(File template, File tmpFile, int tests[])
	{
		boolean done = false;
		boolean eqset = false;
		int ret[] = new int[2];
		int lineNum = tests[0];
		String aLine;
		//InputStream in = null;
		BufferedWriter output = null;
		try
		{

			//FileWriter w = new FileWriter(tmpFile);
			//output = new BufferedWriter(w);
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8"));


			// read in the eqsheet template file
			//BufferedReader br = new BufferedReader(new FileReader(template));
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(template), "UTF-8"));
			List lines = new ArrayList();

			while ((aLine = br.readLine()) != null)
			{
				lines.add(aLine);
			}
			br.close();
			String line;

			// parse each line and look for EQSET delimiter
			while (!done && (lineNum < lines.size()))
			{
				line = (String) lines.get(lineNum);
				if (line.indexOf("|EQSET.START|") > -1)
				{
					++lineNum;
					done = true;
				}
				else if (line.indexOf("|EQSET.END|") > -1)
				{
					++lineNum;
					done = true;
					eqset = true;
				}
				else
				{
					output.write(line);
					output.newLine();
					++lineNum;
				}
			}
		} // end of try
		catch (IOException ioe)
		{
			GuiFacade.showMessageDialog(null, "Could not create temporary equip sets preview file.", "PCGen", GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("Could not create temporary equip sets preview file.");
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.flush();
					output.close();
				}
				catch (IOException e)
				{
					//TODO: Should we really ignore this?
				}
				catch (NullPointerException e)
				{
					//TODO: Should we really ignore this?
				}
			}
		}
		// return the last line we've parsed through
		ret[0] = lineNum;
		// if we should process an EQSET loop
		if (eqset)
		{
			ret[1] = 1;
		}
		else
		{
			ret[1] = 0;
		}
		return ret;
	}

	/**
	 * takes a template file as input
	 * File template is the original template file
	 * that has been parsed and seperated by
	 * |EQSET.START| and
	 * |EQSET.END| tags
	 *
	 * Loops through all EquipSet's, sets equipped, carried, etc
	 * status on the equipment in each EquipSet and the sends
	 * template file to ExportHandler to get parsed
	 * The output from ExportHandler gets appended to: out
	 *
	 **/
	private static void equipItch(File template, BufferedWriter out)
	{
		// Array containing the id's of root EquipSet's
		List eqRootList = new ArrayList();

		// we count all EquipSet with parent of 0
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().equals("0"))
			{
				eqRootList.add(es);
			}
		}

		// First, we have to save off the current Calc EquipSet Id
		String oldEqId = aPC.getCalcEquipSetId();

		// make sure EquipSet's are in sorted order
		// (important for Containers contents)
		Collections.sort(equipSetList);

		// Next we loop through all the root EquipSet's, populate
		// the new eqList and print out an iteration of the eqsheet
		for (Iterator i = eqRootList.iterator(); i.hasNext();)
		{
			EquipSet esRL = (EquipSet) i.next();
			String pid = esRL.getIdPath();

			// be sure to set the currently exporting EquipSet
			// so that token EQUIPSET.NAME can be parsed
			aPC.setCurrentEquipSetName(esRL.getName());
			aPC.setCalcEquipSetId(pid);

			// get the current setting for Temp Bonuses
			boolean currentTempBonus = aPC.getUseTempMods();

			// now set the value depending on the EquipSet
			aPC.setUseTempMods(esRL.getUseTempMods());

			// using the split eqsheet template
			// print out the current EquipSet to file
			(new ExportHandler(template)).write(aPC, out);

			// reset the Temp Bonuses check
			aPC.setUseTempMods(currentTempBonus);
		}
		// make sure everything has been written
		try
		{
			out.flush();
		}
		catch (IOException ioe)
		{
			//ignored
		}

		// Last, set the "working" EquipSet Id back to the old one
		// and recalculate everything
		aPC.setCalcEquipSetId(oldEqId);
		aPC.setCalcEquipmentList();
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void refreshFiltering()
	{
		updateAvailableModel();
		updateSelectedModel();
	}

	/**
	 * specifies whether the "match any" option should be available
	 **/
	public final boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
	 **/
	public final boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 **/
	public final int getSelectionMode()
	{
		return DISABLED_MODE;
	}
}
