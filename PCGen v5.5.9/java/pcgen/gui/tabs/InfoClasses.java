/*
 * InfoClasses.java
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
 * Created on Feb 16, 2002 11:15 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:27 $
 *
 */

package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.SubClass;
import pcgen.core.SystemCollections;
import pcgen.core.EquipmentList;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.pcGenGUI;
import pcgen.gui.utils.AbstractTreeTableModel;
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
import pcgen.gui.utils.WholeNumberField;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * ???
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 */

public final class InfoClasses extends FilterAdapterPanel
{
	static final long serialVersionUID = 9141488354194857537L;
	private final JLabel avaLabel = new JLabel(PropertyFactory.getString("in_available"));
	private final JLabel selLabel = new JLabel(PropertyFactory.getString("in_selected"));
	private JButton removeButton;
	private JButton addButton;
	private JButton adjXP = new JButton(PropertyFactory.getString("in_adjXP"));
	private JScrollPane cScroll = new JScrollPane();
	private JLabelPane infoLabel = new JLabelPane();
	private JPanel center = new JPanel();
	private Border etched;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;
	private ClassModel availableModel = null;  // Model for the JTreeTable.
	private ClassModel selectedModel = null;   // Model for the JTreeTable.
	private JTreeTable availableTable;  // the available Class
	private JTreeTableSorter availableSort = null;

	private JTreeTable selectedTable;   // the selected Class
	private PCClass lastClass = null; //keep track of which PCClass was last selected from either table
	private static boolean needsUpdate = true;
	private static PlayerCharacter aPC = null;
	private static int viewMode = GuiConstants.INFOCLASS_VIEW_NAME; // keep track of what view mode we're in for Available
	private static int viewSelectMode = GuiConstants.INFOCLASS_VIEW_NAME; // keep track of what view mode we're in for Selected. defaults to PropertyFactory.getString("in_nameLabel")
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;

	private static final int COL_NAME = 0;
	private static final int COL_REQ_LEVEL = 1;
	private static final int COL_SRC = 2;

	// Right-click inventory item
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private JPanel jPanel1 = new JPanel();
	private JLabel lblFeats = new JLabel();
	private JLabel lblSkills = new JLabel();
	private JLabel lblBAB = new JLabel();
	private JLabel featCount = new JLabel();
	private JLabel skillCount = new JLabel();
	private JLabel lBAB = new JLabel();
	private JLabel lblHP = new JLabel();
	private JLabel lblExperience = new JLabel();
	private WholeNumberField experience = new WholeNumberField();
	private JButton jButtonHP = new JButton();
	private JLabel lblNextLevel = new JLabel();
	private WholeNumberField txtNextLevel = new WholeNumberField();

	private JLabel lblAltHP = new JLabel();
	private JLabel lAHP = new JLabel();
	private JLabel lblVariableDisplay = new JLabel();
	private JLabel lVariableDisplay = new JLabel();
	private JLabel lblVariableDisplay2 = new JLabel();
	private JLabel lVariableDisplay2 = new JLabel();
	private JLabel lblVariableDisplay3 = new JLabel();
	private JLabel lVariableDisplay3 = new JLabel();
	private JLabel lblDefense = new JLabel();
	private JLabel lDefense = new JLabel();

	private JPanel[] pnlCheck;
	private JLabel[] lCheck;
	private JLabel[] lblCheck;

	private JPanel pnlBAB = new JPanel();
	private JPanel pnlDefense = new JPanel();
	private JPanel pnlEast = new JPanel();
	private JPanel pnlFeats = new JPanel();
	private JPanel pnlFillerEast = new JPanel();
	private JPanel pnlFillerSouth = new JPanel();
	private JPanel pnlFillerWest = new JPanel();
	private JPanel pnlHP = new JPanel();
	private JPanel pnlVariableDisplay = new JPanel();
	private JPanel pnlVariableDisplay2 = new JPanel();
	private JPanel pnlVariableDisplay3 = new JPanel();
	private JPanel pnlSkills = new JPanel();
	private JPanel pnlWest = new JPanel();
	private JPanel pnlAltHP = new JPanel();
	private JPanel pnlXP = new JPanel();

	private static PObjectNode typeRoot;

	/**
	 * The basic idea of the TreeTableModel is that there is a single
	 * <code>root</code> object.  This root object has a null
	 * <code>parent</code>.  All other objects have a parent which
	 * points to a non-null object.  parent objects contain a list of
	 * <code>children</code>, which are all the objects that point
	 * to it as their parent.
	 * objects (or <code>nodes</code>) which have 0 children
	 * are leafs (the end of that linked list).
	 * nodes which have at least 1 child are not leafs.
	 * Leafs are like files and non-leafs are like directories.
	 **/
	private final class ClassModel extends AbstractTreeTableModel
	{
		// Types of the columns.
		private int modelType = 0; // availableModel=0,selectedModel=1

		/**
		 * Creates a ClassModel
		 **/
		private ClassModel(int mode, boolean available)
		{
			super(null);
			if (!available)
			{
				modelType = 1;
			}
			resetModel(mode, available);
		}

		/**
		 * This assumes the ClassModel exists
		 * but needs to be repopulated
		 **/
		private void resetModel(int mode, boolean available)
		{
			Iterator fI;
			if (available)
			{
				fI = Globals.getClassList().iterator();
			}
			else
			{
				fI = aPC.getClassList().iterator();
			}
			switch (mode)
			{
				case GuiConstants.INFOCLASS_VIEW_NAME: // Name
					setRoot(new PObjectNode()); // just need a blank one
					while (fI.hasNext())
					{
						final PCClass aClass = (PCClass) fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
						{
							continue;
						}
						PObjectNode aFN = new PObjectNode();
						aFN.setParent((PObjectNode) super.getRoot());
						aFN.setItem(aClass);
						aFN.setIsValid(aClass.passesPreReqToGain());
						((PObjectNode) super.getRoot()).addChild(aFN);
						if (available)
						{
							addSubClassesTo(aFN, aClass);
						}
					}
					break;
				case GuiConstants.INFOCLASS_VIEW_TYPE_NAME: // type/name
					setRoot((PObjectNode) InfoClasses.typeRoot.clone());
					while (fI.hasNext())
					{
						final PCClass aClass = (PCClass) fI.next();
						// in the availableTable, if filtering out unqualified items
						// ignore any class the PC doesn't qualify for
						if (!shouldDisplayThis(aClass))
						{
							continue;
						}
						PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if ((!added && i == rootAsPObjectNode.getChildCount() - 1) || aClass.isType(((PObjectNode) rootAsPObjectNode.getChildren().get(i)).getItem().toString()))
							{
								PObjectNode aFN = new PObjectNode();
								aFN.setParent(rootAsPObjectNode.getChild(i));
								aFN.setItem(aClass);
								aFN.setIsValid(aClass.passesPreReqToGain());
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;
								if (available)
								{
									addSubClassesTo(aFN, aClass);
								}
							}
						}
					}
					break;

				default:
					Logging.errorPrint(PropertyFactory.getString("in_clICEr1") + " " + mode + " " + PropertyFactory.getString("in_clICEr2"));
					break;
			}
			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
		}

		private void addSubClassesTo(PObjectNode aFN, PCClass aClass)
		{
			if (aClass.getSubClassList() != null && !aClass.getSubClassList().isEmpty())
			{
				for (Iterator sI = aClass.getSubClassList().iterator(); sI.hasNext();)
				{
					SubClass sClass = (SubClass) sI.next();
					PObjectNode aSN = new PObjectNode();
					aSN.setParent(aFN);
					aSN.setItem(sClass);
					aSN.setIsValid(sClass.passesPreReqToGain());
					aFN.addChild(aSN);
				}
			}
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final PCClass aClass)
		{
			if (SettingsHandler.hideMonsterClasses() && aClass.isMonster())
			{
				return false;
			}
			return ((modelType == 1) || (aClass.isVisible() && accept(aClass)));
		}

		// There must be a root object, though it can be hidden
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		// return the root node
		public Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			return 3;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return PropertyFactory.getString("in_nameLabel");
				case COL_REQ_LEVEL:
					if (modelType == 0)
					{
						return PropertyFactory.getString("in_preReqs");
					}
					return PropertyFactory.getString("in_level");
				case COL_SRC:
					return PropertyFactory.getString("in_sourceLabel");
				default:
					Logging.errorPrint(PropertyFactory.getString("in_clICEr3") + " " + column + " " + PropertyFactory.getString("in_clICEr2"));
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
				case COL_REQ_LEVEL:
					if (modelType == 0)
					{
						return String.class;
					}
					return Integer.class;
				case COL_SRC:
					return String.class;
				default:
					Logging.errorPrint(PropertyFactory.getString("in_clICEr4") + " " + column + " " + PropertyFactory.getString("in_clICEr2"));
					break;
			}
			return String.class;
		}

		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;
			PCClass aClass = null;
			if (fn != null && (fn.getItem() instanceof PCClass))
			{
				aClass = (PCClass) fn.getItem();
			}

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
						Logging.errorPrint(PropertyFactory.getString("in_clICEr5"));
						return "";
					}
				case COL_REQ_LEVEL: // Cost
					if (modelType == 0)
					{
						if (aClass != null)
						{
							return aClass.preReqHTMLStrings();
						}
						return "";
					}
					if (aClass != null)
					{
						return new Integer(aClass.getLevel());
					}
					return c;

				case COL_SRC: // Source or Qty
					if (fn != null)
					{
						return fn.getSource();
					}
					return "";
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Logging.errorPrint(PropertyFactory.getString("in_clICEr5"));
						return null;
					}
				default:
					Logging.errorPrint(PropertyFactory.getString("in_clICEr6") + " " + column + " " + PropertyFactory.getString("in_ICEr2"));
					break;

			}
			return null;
		}

	}

	private class ClassPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 9141488354194857537L;

		private class ClassActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(1);
			}
		}

		private class RemoveClassActionListener extends ClassActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(-1);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddClassActionListener(), PropertyFactory.getString("in_add1"), (char) 0, accelerator, PropertyFactory.getString("in_add1lvl"), "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveClassActionListener(), PropertyFactory.getString("in_remove1"), (char) 0, accelerator, PropertyFactory.getString("in_remove1lvl"), "Remove16.gif", true);
		}

		ClassPopupMenu(JTreeTable treeTable)
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
                                 *
				 */
				ClassPopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_add1"), "shortcut EQUALS"));
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
				 *
				 * changed accelerator from "control PLUS" to "control EQUALS" as cannot
				 * get "control PLUS" to function on standard US keyboard with Windows 98
				 */
				ClassPopupMenu.this.add(createAddMenuItem(PropertyFactory.getString("in_add1"), "shortcut EQUALS"));
				ClassPopupMenu.this.add(createRemoveMenuItem(PropertyFactory.getString("in_remove1"), "shortcut MINUS"));
			}
		}
	}

	private class ClassPopupListener extends MouseAdapter
	{
		private JTree tree;
		private ClassPopupMenu menu;

		ClassPopupListener(JTreeTable treeTable, ClassPopupMenu aMenu)
		{
			tree = treeTable.getTree();
			menu = aMenu;
			KeyListener myKeyListener = new KeyListener()
			{
				public void keyTyped(KeyEvent e)
				{
					dispatchEvent(e);
				}

				public void keyPressed(KeyEvent e)
				{
					final int keyCode = e.getKeyCode();
					if (keyCode != KeyEvent.VK_UNDEFINED)
					{
						final KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
						for (int i = 0; i < menu.getComponentCount(); i++)
						{
							final JMenuItem menuItem = (JMenuItem) menu.getComponent(i);
							KeyStroke ks = menuItem.getAccelerator();
							if ((ks != null) && keyStroke.equals(ks))
							{
								selPath = tree.getSelectionPath();
								menuItem.doClick(2);
								return;
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
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new ClassPopupListener(treeTable, new ClassPopupMenu(treeTable)));
	}


	/* typeSubtypeRoot is the base structure used by both the available and selected tables; no
	 * need to generate this same list twice.
	 */

	public InfoClasses()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Constants.tabNames[Constants.TAB_CLASSES]);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});

	}

	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		typeRoot = new PObjectNode();
		List tList = new ArrayList();
		for (Iterator i = Globals.getClassList().iterator(); i.hasNext();)
		{
			final PCClass aClass = (PCClass) i.next();
			for (int ii = 0; ii < aClass.getMyTypeCount(); ++ii)
			{
				final String aString = aClass.getMyType(ii);
				if (!tList.contains(aString))
				{
					tList.add(aString);
				}
			}
		}
		Collections.sort(tList);
		if (!tList.contains(PropertyFactory.getString("in_other")))
		{
			tList.add(PropertyFactory.getString("in_other"));
		}
		PObjectNode[] p = new PObjectNode[tList.size()];
		for (int i = 0; i < p.length; i++)
		{
			p[i] = new PObjectNode();
			p[i].setItem(tList.get(i).toString());
			p[i].setParent(typeRoot);
		}
		typeRoot.setChildren(p);

		int iView = SettingsHandler.getClassTab_AvailableListMode();
		if ((iView >= GuiConstants.INFOCLASS_VIEW_NAME) && (iView <= GuiConstants.INFOCLASS_VIEW_TYPE_NAME))
		{
			viewMode = iView;
		}
		SettingsHandler.setClassTab_AvailableListMode(viewMode);
		viewComboBox.addItem(PropertyFactory.getString("in_nameLabel"));
		viewComboBox.addItem(PropertyFactory.getString("in_typeName"));
		Utility.setDescription(viewComboBox, PropertyFactory.getString("in_clChangCl"));
		viewComboBox.setSelectedIndex(viewMode);       // must be done before createModels call

		iView = SettingsHandler.getClassTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOCLASS_VIEW_NAME) && (iView <= GuiConstants.INFOCLASS_VIEW_TYPE_NAME))
		{
			viewSelectMode = iView;
		}
		SettingsHandler.setClassTab_SelectedListMode(viewSelectMode);
		viewSelectComboBox.addItem(PropertyFactory.getString("in_nameLabel"));
		viewSelectComboBox.addItem(PropertyFactory.getString("in_typeName"));
		Utility.setDescription(viewSelectComboBox, PropertyFactory.getString("in_clChangCl"));
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		aPC = Globals.getCurrentPC();

		createModels();
		createTreeTables();

		//  Base Panel, Contains left and right panels
		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		//  Top Left Pane - Available Classes
		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addButton = new JButton(newImage);
		Utility.setDescription(addButton, PropertyFactory.getString("in_clAddTip"));
		addButton.setEnabled(false);
		aPanel.add(addButton);
		leftPane.add(aPanel);

		selectedTable.setColAlign(1, SwingConstants.CENTER);
		selectedTable.getColumnModel().getColumn(1).setPreferredWidth(15);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);


		//  Top Right Pane - Selected Classes
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		removeButton = new JButton(newImage);
		Utility.setDescription(removeButton, PropertyFactory.getString("in_clRemoveTip"));
		removeButton.setEnabled(false);
		aPanel.add(removeButton);
		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		//  Bottom Left Pane - Class Info
		TitledBorder title1 = BorderFactory.createTitledBorder(etched, PropertyFactory.getString("in_clInfo"));
		title1.setTitleJustification(TitledBorder.CENTER);
		cScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		cScroll.setViewportView(infoLabel);
		Utility.setDescription(cScroll, PropertyFactory.getString("in_infoScrollTip"));

		//  Bottom Right Pane - Character Info
		initSEPanel(jPanel1);

		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
				updateCharacterInfo();
			}
		});

		experience.addFocusListener(new FocusAdapter()
		{
			private boolean isProcessingExperienceFocusLost = false;

			public void focusLost(FocusEvent evt)
			{
				//
				// for some reason this gets processed twice, want to ignore the second (and subsequent)
				//
				if (!isProcessingExperienceFocusLost)
				{
					isProcessingExperienceFocusLost = true;
					experienceFocusLost();
					isProcessingExperienceFocusLost = false;
				}
			}
		});

		//  Split the Pane
		asplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		//  Add the Bottom Pane
		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// add the sorter so that clicking on the TableHeader actually does something
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);

	}

	private void updateChecks()
	{
		final List checkList = SystemCollections.getUnmodifiableCheckList();
		final int countChecks = checkList.size();
		if ((lCheck == null) || (countChecks != lCheck.length))
		{
			buildEastPanel();
		}

		if (countChecks != 0)
		{
			for (int i = 0; i < countChecks; ++i)
			{
				final PObject obj = (PObject) checkList.get(i);
				lblCheck[i].setText(obj.getName());
				lCheck[i].setText(Delta.toString((int) aPC.getBonus(i + 1, true)));
			}
		}
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
				bsplit.setDividerLocation((int) (InfoClasses.this.getSize().getHeight() - 140));
				asplit.setDividerLocation((int) (InfoClasses.this.getSize().getWidth() - 334));
			}
		});
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(-1);
			}
		});
		adjXP.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				String selectedValue = GuiFacade.showInputDialog(null, PropertyFactory.getString("in_clEnterXP"), Constants.s_APPNAME, GuiFacade.QUESTION_MESSAGE);
				if (selectedValue != null)
				{
					try
					{
						int x = Integer.parseInt(selectedValue) + aPC.getXP();
						if (maybeSetExperience(x))
						{
							experience.setValue(x);
						}
//						experienceFocusLost(null); // force xp messages as neccessary
					}
					catch (NumberFormatException e)
					{
						GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_clInvalidNum"), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						return;
					}
				}
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addClass(1);
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed();
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed();
			}
		});

		jButtonHP.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				pcGenGUI.showHpFrame();
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	private static boolean maybeSetExperience(int xp)
	{
		final PlayerCharacter currentPC = Globals.getCurrentPC();
		currentPC.setXP(xp);
		if (xp >= currentPC.minXPForNextECL())
		{
			GuiFacade.showMessageDialog(null, SettingsHandler.getGame().getLevelUpMessage(), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
		}
		return true;
	}

	private void experienceFocusLost()
	{
		maybeSetExperience(experience.getValue());
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setClassTab_AvailableListMode(viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setClassTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText(PropertyFactory.getString("in_clNotQualify"));
		updateCharacterInfo();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;

		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoClasses.splitPane", (int) (this.getSize().getWidth() * 7 / 10));
			t = SettingsHandler.getPCGenOption("InfoClasses.bsplit", (int) (this.getSize().getHeight() - 140));
			u = SettingsHandler.getPCGenOption("InfoClasses.asplit", (int) (this.getSize().getWidth() - 334));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ClassSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "ClassSel", i));
			}
			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ClassAva", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "ClassAva", i));
			}
		}

		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoClasses.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoClasses.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoClasses.asplit", u);
		}
		//System.out.println("fcs-end");
	}

	// This recalculates the states of everything based upon the
	// currently selected character.
	private void updateCharacterInfo()
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

		String aString = Globals.getGameModeHPAbbrev();
		jButtonHP.setText(aString);

		aString = Globals.getGameModeACText();
		if (Globals.getGameModeShowClassDefense())
		{
			lblDefense.setText(aString + " (Class)");
			pnlDefense.setVisible(true);
		}
		else
		{
			pnlDefense.setVisible(false);
		}

		aString = Globals.getGameModeAltHPText();
		if (aString.length() != 0)
		{
			lblAltHP.setText(aString);
			pnlAltHP.setVisible(true);
		}
		else
		{
			pnlAltHP.setVisible(false);
		}

		aString = Globals.getGameModeVariableDisplayText();
		if (aString.length() != 0)
		{
			lblVariableDisplay.setText(aString);
			pnlVariableDisplay.setVisible(true);
		}
		else
		{
			pnlVariableDisplay.setVisible(false);
		}

		aString = Globals.getGameModeVariableDisplay2Text();
		if (aString.length() != 0)
		{
			lblVariableDisplay2.setText(aString);
			pnlVariableDisplay2.setVisible(true);
		}
		else
		{
			pnlVariableDisplay2.setVisible(false);
		}

		aString = Globals.getGameModeVariableDisplay3Text();
		if (aString.length() != 0)
		{
			lblVariableDisplay3.setText(aString);
			pnlVariableDisplay3.setVisible(true);
		}
		else
		{
			pnlVariableDisplay3.setVisible(false);
		}

		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);

		updateAvailableModel();
		updateSelectedModel();

		//Calculate the aggregate feat list
		aPC.aggregateFeatList();
		updateHP();
		featCount.setText(Double.toString(aPC.getFeats()));
		skillCount.setText(Integer.toString(aPC.getSkillPoints()));
		lBAB.setText(Integer.toString(aPC.baseAttackBonus()));
		lDefense.setText(Integer.toString(aPC.classAC()));

		updateChecks();

		lAHP.setText(Integer.toString(aPC.altHP()));
		updateXP(aPC); // race changes effective XP
		int mytempvar = (int) aPC.getTotalBonusTo("VAR", Globals.getGameModeVariableDisplayName());
		lVariableDisplay.setText(Integer.toString(mytempvar));
		int mytempvar2 = (int) aPC.getTotalBonusTo("VAR", Globals.getGameModeVariableDisplay2Name());
		lVariableDisplay2.setText(Integer.toString(mytempvar2));
		int mytempvar3 = (int) aPC.getTotalBonusTo("VAR", Globals.getGameModeVariableDisplay3Name());
		lVariableDisplay3.setText(Integer.toString(mytempvar3));

		needsUpdate = false;
	}

	private void setInfoLabelText(PCClass aClass, PObjectNode pn)
	{
		String aString;
		boolean isSubClass = false;
		lastClass = aClass; //even if that's null
		if (lastClass instanceof SubClass)
		{
			lastClass = (PCClass) pn.getParent().getItem();
			isSubClass = true;
		}
		if (aClass != null)
		{
			StringBuffer b = new StringBuffer();
			b.append("<html><b>").append(aClass.piSubString()).append("</b>");

			//
			// Type
			//
			aString = aClass.getType();
			if (isSubClass && (aString.length() == 0))
			{
				aString = lastClass.getType();
			}
			b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_type")).append("</b>:").append(aString);

			//
			// Prereqs
			//
			aString = aClass.preReqHTMLStrings(false);
			if (isSubClass && (aString.length() == 0))
			{
				aString = lastClass.preReqHTMLStrings(false);
			}
			if (aString.length() > 0)
			{
				b.append(" &nbsp;<b>").append(PropertyFactory.getString("in_requirements")).append("</b>:").append(aString);
			}

			//
			// Source
			//
			aString = aClass.getSource();
			if (isSubClass && (aString.length() == 0))
			{
				aString = lastClass.getSource();
			}
			if (aString.length() > 0)
			{
				b.append(" <b>").append(PropertyFactory.getString("in_sourceLabel")).append("</b>:").append(aString);
			}

			b.append(" <b>BAB:</b>:").append(aClass.getAttackBonusType());

			//
			// Hit Die
			//
			int hitDie = aClass.getHitDie();
			if (isSubClass && (hitDie == 0))
			{
				hitDie = lastClass.getHitDie();
			}
			b.append(" <b>HD:</b>:1d").append(hitDie);

			if (Globals.getGameModeShowSpellTab())
			{
				aString = aClass.getSpellType();
				if (isSubClass && ((aString.length() == 0) || aString.equalsIgnoreCase(Constants.s_NONE)))
				{
					aString = lastClass.getSpellType();
				}
				b.append(" <b>").append(PropertyFactory.getString("in_spellType")).append("</b>:").append(aString);

				aString = aClass.getSpellBaseStat();
				if (isSubClass && ((aString == null) || (aString.length() == 0)))
				{
					aString = lastClass.getSpellBaseStat();
				}
				b.append(" <b>").append(PropertyFactory.getString("in_baseStat")).append("</b>:").append(aString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	private void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect class when use cursor keys to navigate the tree
					//
					//final Object temp = availableTable.getTree().getLastSelectedPathComponent();
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null)
					{
						lastClass = null;
						GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_clNoClass"), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						return;
					}

					PCClass aClass = null;
					PObjectNode pn = null;
					if (temp instanceof PObjectNode)
					{
						pn = (PObjectNode) temp;
						temp = ((PObjectNode) temp).getItem();
						if (temp instanceof PCClass)
						{
							aClass = (PCClass) temp;
						}
					}
					addButton.setEnabled(aClass != null);
					setInfoLabelText(aClass, pn);
				}
			}
		});
		final JTree tree = availableTable.getTree();

		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new LabelTreeCellRenderer());

		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = tree.getRowForLocation(e.getX(), e.getY());
				final TreePath mlSelPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && mlSelPath != null)
					{
						tree.setSelectionPath(mlSelPath);
					}
					else if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								addClass(1);
							}
						});
					}
				}
			}
		};
		tree.addMouseListener(ml);

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect class when use cursor keys to navigate the tree
					//
					//final Object temp = selectedTable.getTree().getLastSelectedPathComponent();
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null)
					{
						lastClass = null;
						infoLabel.setText();
						return;
					}

					PCClass aClass = null;
					PObjectNode pn = null;
					if (temp instanceof PObjectNode)
					{
						pn = (PObjectNode) temp;
						Object t = pn.getItem();
						if (t instanceof PCClass)
						{
							aClass = (PCClass) t;
						}
					}
					removeButton.setEnabled(aClass != null);
					setInfoLabelText(aClass, pn);
				}
			}
		});
		ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath mlSelPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1)
					{
						btree.setSelectionPath(mlSelPath);
					}
					else if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								addClass(-1);
							}
						});
					}
				}
			}
		};
		btree.addMouseListener(ml);

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private PCClass getSelectedClass()
	{
		if (lastClass == null)
		{
			GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_clNoClass"), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}
		return lastClass;
	}

	private void addClass(int levels)
	{
		if (Globals.getGameModeAlignmentText().length() != 0)
		{
			if ((levels > 0) && (aPC.getAlignment() == SystemCollections.getIndexOfAlignment(Constants.s_NONE)))
			{
				GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_clSelAlign"), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
				return;
			}
		}
		PCClass theClass = getSelectedClass();
		if ((theClass == null) || !theClass.isQualified())
		{
			return;
		}

		aPC.setDirty(true);

		final PCClass aClass = aPC.getClassNamed(theClass.getName());

		//
		// TODO:
		// If attempting to add a different subclass
		// (eg. Evoker to an Illusionist) warn the user
		// However, adding a level of the base class
		// (i.e. Wizard to Illusionist) should still be okay
		//

		// Fix this logic -- it looks like you might slip past
		// the 20 cap with a monster PC?  XXX --bko
		if (levels < 0 || aClass == null || Globals.checkRule("LEVELCAP") || (!Globals.checkRule("LEVELCAP") && aClass.getLevel() < aClass.getMaxLevel()))
		{
			aPC.incrementClassLevel(levels, theClass);
			PCGen_Frame1.forceUpdate_InfoInventory();
		}
		else
		{
			GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_clMaxLvl"), Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
			return;
		}

		PCGen_Frame1.forceUpdate_PlayerTabs();
		PCGen_Frame1.forceUpdate_InfoFeats();
		PCGen_Frame1.forceUpdate_InfoDomain();
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoSpells();

		//
		// If we've just added the first non-monster level,
		// ask to choose free item of clothing if haven't already
		//
		if (levels > 0)
		{
			if (Globals.checkRule("FREECLOTHES") && ((aPC.totalNonMonsterLevels()) == 1))
			{
				//
				// See what the PC is already carrying
				//
				List clothes = EquipmentList.getEquipmentOfType(aPC.getEquipmentList(), "Clothing.Resizable", "Magic");
				//
				// Check to see if any of the clothing the PC
				// is carrying will actually fit and
				// has a zero price attached
				//
				boolean hasClothes = false;
				final String pcSize = aPC.getSize();
				if (clothes.size() != 0)
				{
					for (Iterator e = clothes.iterator(); e.hasNext();)
					{
						final Equipment eq = (Equipment) e.next();
						if ((pcgen.core.utils.Utility.doublesEqual(eq.getCost().doubleValue(), 0.0)) && pcSize.equals(eq.getSize()))
						{
							hasClothes = true;
							break;
						}
					}
				}
				//
				// If the PC has no clothing items, or none that
				// are sized to fit, then allow them to pick
				// a free set
				//
				if (!hasClothes)
				{
					clothes = EquipmentList.getEquipmentOfType(EquipmentList.getEquipmentList(), "Clothing.Resizable", "Magic.Custom.Auto_Gen");
					List selectedClothes = new ArrayList();
					Globals.chooseFromList(PropertyFactory.getString("in_clCloSet"), clothes, selectedClothes, 1);
					if (selectedClothes.size() != 0)
					{
						String aString = (String) selectedClothes.get(0);
						Equipment eq = EquipmentList.getEquipmentNamed(aString);
						if (eq != null)
						{
							eq = (Equipment) eq.clone();
							eq.setQty(new Float(1));
							//
							// Need to resize to fit?
							//
							if (!pcSize.equals(eq.getSize()))
							{
								eq.resizeItem(pcSize);
							}
							eq.setCostMod("-" + eq.getCost().toString());		// make cost 0
							//
							// Can't add if already own one with this name.
							//
							if (aPC.getEquipmentNamed(eq.nameItemFromModifiers()) == null)
							{
								aPC.addEquipment(eq);
							}
							else
							{
								Logging.errorPrint(PropertyFactory.getString("in_clEqEr"));
							}
						}
					}
				}
			}
		}

		aPC.setDirty(true);
		needsUpdate = true;
		updateCharacterInfo();
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
		List pathList = selectedTable.getExpandedPaths();
		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	private void createModels()
	{
		createSelectedModel();
		createAvailableModel();
	}

	private void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new ClassModel(viewMode, true);
		}
		else
		{
			availableModel.resetModel(viewMode, true);
		}
		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
			// removed by sage_sam 18 sept 2003 for bug #797574 --
			// IP items get sorted to the top due to HTML content
			// availableSort.sortNodeOnColumn();
		}

	}

	private void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new ClassModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false);
		}
	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllClassFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
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
	 * specifies whether the "match any" option should be available
	 */
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies whether the "negate/reverse" option should be available
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
		return FilterConstants.MULTI_MULTI_MODE;
	}

	public void updateHP()
	{
		final PlayerCharacter thisPC = Globals.getCurrentPC();
		if (thisPC == null)
		{
			return;
		}

		lblHP.setText(String.valueOf(thisPC.hitPoints()));
	}

	private void updateXP(PlayerCharacter currentPC)
	{
		if (currentPC == null)
		{
			return;
		}

		experience.setValue(currentPC.getXP());

		txtNextLevel.setValue(currentPC.minXPForNextECL());
	}

	/**
	 * Populate the lower right-hand panel's right panel (the one with the checks in it).
	 */
	private void buildEastPanel()
	{
		GridBagConstraints gbc;
		String aString;

		pnlEast.setLayout(new GridBagLayout());

		final List checkList = SystemCollections.getUnmodifiableCheckList();
		final int countChecks = checkList.size();
		if (countChecks != 0)
		{
			pnlCheck = new JPanel[countChecks];
			lCheck = new JLabel[countChecks];
			lblCheck = new JLabel[countChecks];
			for (int i = 0; i < countChecks; ++i)
			{
				pnlCheck[i] = new JPanel();
				pnlCheck[i].setLayout(new BorderLayout(5, 5));

				lblCheck[i] = new JLabel();
				lCheck[i] = new JLabel();
				pnlCheck[i].add(lblCheck[i], BorderLayout.WEST);
				pnlCheck[i].add(lCheck[i], BorderLayout.EAST);

				gbc = new GridBagConstraints();
				gbc.gridx = 0;
				if (i == 0)
				{
					gbc.gridy = 0;
				}
				gbc.fill = GridBagConstraints.BOTH;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.weightx = 1.0;
				pnlEast.add(pnlCheck[i], gbc);
			}
		}

		pnlVariableDisplay.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeVariableDisplayText();
		lblVariableDisplay.setText(aString);
		pnlVariableDisplay.add(lblVariableDisplay, BorderLayout.WEST);
		pnlVariableDisplay.setVisible(aString.length() != 0);
		pnlVariableDisplay.add(lVariableDisplay, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlVariableDisplay, gbc);

		pnlVariableDisplay2.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeVariableDisplay2Text();
		lblVariableDisplay2.setText(aString);
		pnlVariableDisplay2.add(lblVariableDisplay2, BorderLayout.WEST);
		pnlVariableDisplay2.setVisible(aString.length() != 0);
		pnlVariableDisplay2.add(lVariableDisplay2, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlVariableDisplay2, gbc);

		pnlVariableDisplay3.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeVariableDisplay3Text();
		lblVariableDisplay3.setText(aString);
		pnlVariableDisplay3.add(lblVariableDisplay3, BorderLayout.WEST);
		pnlVariableDisplay3.setVisible(aString.length() != 0);
		pnlVariableDisplay3.add(lVariableDisplay3, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlVariableDisplay3, gbc);

		pnlAltHP.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeAltHPText();
		lblAltHP.setText(aString);
		pnlAltHP.add(lblAltHP, BorderLayout.WEST);
		pnlAltHP.setVisible(aString.length() != 0);
		pnlAltHP.add(lAHP, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlEast.add(pnlAltHP, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		pnlEast.add(pnlFillerEast, gbc);
		pnlEast.setVisible(true);
		pnlEast.updateUI();
	}

	/**
	 * Populate the lower right-hand panel
	 * @param sep
	 */
	private void initSEPanel(JPanel sep)
	{
		GridBagConstraints gbc;
		String aString;

		sep.setLayout(new GridBagLayout());

		jButtonHP.setText(Globals.getGameModeHPAbbrev());
		jButtonHP.setAlignmentY(0.0F);
		jButtonHP.setHorizontalAlignment(SwingConstants.LEFT);
		pnlHP.add(jButtonHP);

		lblHP.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlHP.add(lblHP);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		sep.add(pnlHP, gbc);

		pnlWest.setLayout(new GridBagLayout());

		pnlFeats.setLayout(new BorderLayout(5, 5));

		lblFeats.setText(PropertyFactory.getString("in_feats"));
		pnlFeats.add(lblFeats, BorderLayout.WEST);

		featCount.setText("0");
		pnlFeats.add(featCount, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlFeats, gbc);

		pnlSkills.setLayout(new BorderLayout(5, 5));

		lblSkills.setText(PropertyFactory.getString("in_skills"));
		pnlSkills.add(lblSkills, BorderLayout.WEST);

		skillCount.setText("0");
		pnlSkills.add(skillCount, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlSkills, gbc);

		pnlBAB.setLayout(new BorderLayout(5, 5));

		lblBAB.setText("BAB");
		pnlBAB.add(lblBAB, BorderLayout.WEST);

		lBAB.setText("0");
		pnlBAB.add(lBAB, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlBAB, gbc);

		pnlDefense.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeACText();
		lblDefense.setText(aString);
		pnlDefense.add(lblDefense, BorderLayout.WEST);
		pnlDefense.setVisible(aString.length() != 0);
		lDefense.setText("0");
		pnlDefense.add(lDefense, BorderLayout.EAST);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlDefense, gbc);

		pnlAltHP.setLayout(new BorderLayout(5, 5));
		aString = Globals.getGameModeAltHPText();
		lblAltHP.setText(aString);
		pnlAltHP.add(lblAltHP, BorderLayout.WEST);
		pnlAltHP.setVisible(aString.length() != 0);
		pnlAltHP.add(lAHP, BorderLayout.EAST);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		pnlWest.add(pnlAltHP, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		pnlWest.add(pnlFillerWest, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 6, 0, 6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		sep.add(pnlWest, gbc);

		buildEastPanel();

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 1.0;
		sep.add(pnlEast, gbc);

		//pnlXP.setLayout(new BorderLayout(5, 5));
		pnlXP.setLayout(new GridBagLayout());

		lblExperience.setText(PropertyFactory.getString("in_experience"));
		//lblExperience.setPreferredSize(new Dimension(64, 16));
		//pnlXP.add(lblExperience, BorderLayout.WEST);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		pnlXP.add(lblExperience, gbc);

		experience.setHorizontalAlignment(JTextField.TRAILING);
		experience.setText("0");
		//experience.setPreferredSize(new Dimension(11, 20));
		//pnlXP.add(experience, BorderLayout.CENTER);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 3, 0, 3);
		gbc.weightx = 1.0;
		pnlXP.add(experience, gbc);

		pnlXP.add(adjXP, new GridBagConstraints());

		lblNextLevel.setText("Next Level");
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		pnlXP.add(lblNextLevel, gbc);

		txtNextLevel.setHorizontalAlignment(JTextField.TRAILING);
		txtNextLevel.setBorder(BorderFactory.createEtchedBorder(Color.lightGray, Color.lightGray));
		txtNextLevel.setBackground(Color.lightGray);
		txtNextLevel.setEditable(false);
		txtNextLevel.setValue(0);
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 3, 0, 3);
		gbc.weightx = 1.0;
		pnlXP.add(txtNextLevel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		sep.add(pnlXP, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		sep.add(pnlFillerSouth, gbc);
	}
}
