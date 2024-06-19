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
 * Created on April 29th, 2002, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:57:39 $
 *
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
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
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.character.EquipSet;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.io.ExportHandler;
import pcgen.util.FOPHandler;

/**
 *
 *  <code>InfoEquipping</code> creates a new tabbed panel that is used to
 *  allow different combinations of equipment for printing on csheets
 * @author  Jayme Cox <jaymecox@netscape.net>
 * @version $Revision: 1.1 $
 **/
class InfoEquipping extends FilterAdapterPanel
{

	private EquipModel availableModel = null;  // Model for JTreeTable
	private EquipModel selectedModel = null;   // Model for JTreeTable
	private JTreeTable availableTable;         // available Equipment
	private JTreeTable selectedTable;	     // Equipment Sets
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	private static ArrayList equipSetList = new ArrayList();
	private static ArrayList tempSetList = new ArrayList();

	private JLabelPane infoLabel = new JLabelPane();
	private JLabelPane combatLabel = new JLabelPane();
	private final JLabel avaLabel = new JLabel("Sort Equipment");
	private final JLabel selLabel = new JLabel("Sort Equipment");

	private JTextField equipSetTextField = new JTextField();
	private JTextField templateTextField = new JTextField();
	private static final String defaultEquipSet = "Default Set";
	private String selectedEquipSet = "";

	private JButton addEquipButton;
	private JButton delEquipButton;
	private JButton addEquipSetButton;
	private JButton delEquipSetButton;
	private JButton setQtyButton;

	private JButton viewEqSetButton;
	private JButton exportEqSetButton;
	private JButton selectTemplateButton;

	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();

	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;

	private Border etched;
	private TitledBorder titled;

	private static PlayerCharacter aPC = null;

	private TreePath selPath;
	private int selRow;
	private Equipment lastEquip = null;

	private boolean needsUpdate = true;
	private boolean hasBeenSized = false;

	private JComboBox viewComboBox = new JComboBox();
	private JComboBox viewSelectComboBox = new JComboBox();
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
	private static final int COL_SRC = 4;

	// Static definitions of Equipment location strings
	private static final String S_CARRIED = "Carried";
	private static final String S_EQUIPPED = "Equipped";
	private static final String S_PRIMARY = "Primary Hand";
	private static final String S_SECONDARY = "Secondary Hand";
	private static final String S_BOTH = "Both Hands";
	private static final String S_DOUBLE = "Double Weapon";
	private static final String S_UNARMED = "Unarmed";

	/**
	 * create right click menus and listeners
	 **/
	private class EquipPopupMenu extends JPopupMenu
	{
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
				addEquipButton(evt);
			}
		}

		private class DelEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipButton(evt);
			}
		}

		private class SetQtyActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				setQtyButton();
			}
		}

		private class AddAllEquipActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addAllEquipButton();
			}
		}

		private class CopyEquipSetActionListener extends EquipActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				copyEquipSetButton();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddEquipActionListener(), "add 1", (char) 0, accelerator, "Add Equipment", "Add16.gif", true);
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new DelEquipActionListener(), "remove 1", (char) 0, accelerator, "Remove Equipment", "Remove16.gif", true);
		}

		private JMenuItem createSetQtyMenuItem(String label)
		{
			return Utility.createMenuItem(label, new SetQtyActionListener(), "Set QTY", (char) 0, null, "Set Quantity", "", true);
		}

		private JMenuItem createAddAllMenuItem(String label)
		{
			return Utility.createMenuItem(label, new AddAllEquipActionListener(), "Add all", (char) 0, null, "Add All Equipment", "", true);
		}

		private JMenuItem createCopyEquipSetMenuItem(String label)
		{
			return Utility.createMenuItem(label, new CopyEquipSetActionListener(), "Copy Equip Set", (char) 0, null, "Duplicate this Equip Set", "", true);
		}

		EquipPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				EquipPopupMenu.this.add(createAddMenuItem("Add  Equipment", "control EQUALS"));
			}
			else // selectedTable
			{
				EquipPopupMenu.this.add(createDelMenuItem("Remove Equipment", "control MINUS"));
				EquipPopupMenu.this.add(createSetQtyMenuItem("Set Quantity"));
				EquipPopupMenu.this.add(createCopyEquipSetMenuItem("Copy Equip Set"));
			}
			EquipPopupMenu.this.add(createAddAllMenuItem("Add all Equipment"));
		}
	}

	private class EquipPopupListener extends MouseAdapter
	{
		private JTree tree;
		private EquipPopupMenu menu;

		EquipPopupListener(JTreeTable treeTable, EquipPopupMenu aMenu)
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
						for (int i = 0; i < menu.getComponentCount(); i++)
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
				selRow = tree.getRowForLocation(evt.getX(), evt.getY());
				if (selRow == -1) return;
				selPath = tree.getPathForLocation(evt.getX(), evt.getY());
				if (selPath == null) return;

				Globals.debugPrint("setting selection path.");
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
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
		setName("Equipping");

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
			viewMode = iView;
		SettingsHandler.setEquipTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getEquipTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOEQUIPPING_VIEW_TYPE) && (iView <= GuiConstants.INFOEQUIPPING_VIEW_LOCATION))
			viewSelectMode = iView;
		SettingsHandler.setEquipTab_SelectedListMode(viewSelectMode);

		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		viewComboBox.addItem("Type     ");
		viewComboBox.addItem("Location ");
		viewComboBox.addItem("Name     ");
		Utility.setDescription(viewComboBox, "Blah Blah");
		viewComboBox.setSelectedIndex(viewMode);
		viewSelectComboBox.addItem("Type ");
		viewSelectComboBox.addItem("Name ");
		Utility.setDescription(viewSelectComboBox, "Blah Blah");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);

		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

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
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		addEquipButton = new JButton(newImage);
		Utility.setDescription(addEquipButton, "Click to add selected eqipment to your selected set");
		addEquipButton.setEnabled(false);
		aPanel.add(addEquipButton);

		Utility.setDescription(aPanel, "Right click to add equipment to your set");
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

		JLabel selProfileLabel = new JLabel("Equip Set:");
		aPanel.add(selProfileLabel);

		equipSetTextField.setPreferredSize(new Dimension(100, 20));
		aPanel.add(equipSetTextField);

		addEquipSetButton = new JButton("Add");
		//addEquipSetButton.setPreferredSize(new Dimension(60, 20));
		aPanel.add(addEquipSetButton);

		delEquipSetButton = new JButton("Del");
		//delEquipSetButton.setPreferredSize(new Dimension(60, 20));
		aPanel.add(delEquipSetButton);

		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);
		//bPanel.setBorder(BorderFactory.createEtchedBorder());

		setQtyButton = new JButton("Set Qty");
		Utility.setDescription(setQtyButton, "Click to change number of items");
		setQtyButton.setEnabled(false);
		bPanel.add(setQtyButton);

		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		delEquipButton = new JButton(newImage);
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

		availableTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));
		availableTable.getColumnModel().getColumn(2).setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));
		selectedTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));
		selectedTable.getColumnModel().getColumn(2).setCellRenderer(new AlignCellRenderer(SwingConstants.LEFT));


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
		// create an equipment info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Equipment Info");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);
		bLeftPane.add(sScroll);

		// Bottom right panel
		// create a template select and view panel
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JPanel iPanel = new JPanel();
		gridbag.setConstraints(iPanel, c);

		viewEqSetButton = new JButton("View in Browser");
		Utility.setDescription(viewEqSetButton, "Launches a browser and displays Equipment Sets");
		viewEqSetButton.setEnabled(true);
		iPanel.add(viewEqSetButton);

		exportEqSetButton = new JButton("Export to FIle");
		Utility.setDescription(exportEqSetButton, "Export Equipment Sets to a File");
		exportEqSetButton.setEnabled(true);
		iPanel.add(exportEqSetButton);

		Utility.buildConstraints(c, 0, 1, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JPanel iiPanel = new JPanel();
		gridbag.setConstraints(iiPanel, c);

		templateTextField.setEditable(false);
		Utility.setDescription(templateTextField, "Display only");
		templateTextField.setBackground(Color.lightGray);
		templateTextField.setText(SettingsHandler.getSelectedEqSetTemplate());
		selectTemplateButton = new JButton("Select template");
		Utility.setDescription(selectTemplateButton, "Select an EquipSet output template");
		iiPanel.add(selectTemplateButton);
		iiPanel.add(templateTextField);

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
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel.getRoot(), selectedModel);

	}

	private void initActionListeners()
	{
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				int s = splitPane.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoEquipping.splitPane", s);
				s = asplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoEquipping.asplit", s);
				s = bsplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoEquipping.bsplit", s);
			}
		});
		viewEqSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				viewEqSetButton(e);
			}
		});
		exportEqSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				exportEqSetButton(e);
			}
		});
		selectTemplateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectTemplateButton(e);
			}
		});
		addEquipButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipButton(evt);
			}
		});
		delEquipButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipButton(evt);
			}
		});
		setQtyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				setQtyButton();
			}
		});
		addEquipSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addEquipSetButton(evt);
			}
		});
		delEquipSetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delEquipSetButton(evt);
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
	}

	/*
	 * set the equipment Info text in the Equipment Info
	 * panel to the currently selected equipment
	 */
	private void setInfoLabelText(Equipment eqI)
	{
//Globals.debugPrint("setInfoLabelText: "+eqI.getName()+":");
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
			b.append(" &nbsp;<b>Type:</b>&nbsp; ").append(eqI.getType());
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
				b.append(" &nbsp;<b>Requirements</b>:").append(cString);
			}

			String IDS = eqI.getInterestingDisplayString();
			if (IDS.length() > 0)
			{
				b.append(" &nbsp;<b>Properties</b>:").append(eqI.getInterestingDisplayString());
			}

			String bString = eqI.getWeight().toString();
			if (bString.length() > 0)
			{
				b.append(" <b>WT</b>:").append(bString);
			}

			Integer a = eqI.getMaxDex();
			if (a.intValue() != 100)
			{
				b.append(" <b>MAXDEX</b>:").append(a.toString());
			}
			a = eqI.acCheck();
			if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
			{
				b.append(" <b>ACCHECK</b>:").append(a.toString());
			}
			if (!Globals.isStarWarsMode() && !Globals.isSpycraftMode())
			{
				a = eqI.spellFailure();
				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>Arcane Failure</b>:").append(a.toString());
				}
			}
			if (Globals.isStarWarsMode() || Globals.isSpycraftMode())
			{
				a = eqI.eDR();
				if (eqI.isArmor() || eqI.isShield() || (a.intValue() != 0))
				{
					b.append(" <b>Damage Resistance</b>:").append(a.toString());
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
			bString = eqI.getInterestingDisplayString();
			if (bString.length() > 0)
			{
				b.append(" <b>Bonuses</b>:").append(bString);
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
			}
			final int charges = eqI.getRemainingCharges();
			if (charges >= 0)
			{
				b.append(" <b>Charges</b>:").append(charges);
			}

			bString = eqI.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE:</b>&nbsp;").append(bString);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	private static int getSelectedIndex(ListSelectionEvent e)
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

		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					final int idx = getSelectedIndex(e);
					if (idx < 0)
						return;

					final Object temp = atree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastEquip = null;
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if (fNode.getItem() instanceof Equipment)
					{
						Equipment eqI = (Equipment) fNode.getItem();
						if (eqI != null)
						{
							addEquipButton.setEnabled(true);
							setInfoLabelText(eqI);
						}
					}
				}
			}
		});

		MouseListener aml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int avaRow = atree.getRowForLocation(e.getX(), e.getY());
				final TreePath avaPath = atree.getPathForLocation(e.getX(), e.getY());
				if (avaRow != -1)
				{
					if (e.getClickCount() == 1 && avaPath != null)
					{
						// Breaks multi-select
						//atree.setSelectionPath(avaPath);
					}
					else if (e.getClickCount() == 2)
					{
						addEquipButton(null);
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

		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					TreePath selCPath = stree.getSelectionPath();
					if (!stree.isSelectionEmpty())
						equipSetTextField.setText(selCPath.getPathComponent(1).toString());

					final Object temp = stree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastEquip = null;
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if (fNode.getItem() instanceof EquipSet)
					{
						EquipSet eSet = (EquipSet) fNode.getItem();
						Equipment eqI = eSet.getItem();
						if (eqI != null)
						{
							delEquipButton.setEnabled(true);
							setQtyButton.setEnabled(true);
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
				final int selRow = stree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = stree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						// Breaks multi-select
						//stree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								delEquipButton(null);
							}
						});
					}
				}
			}
		};
		stree.addMouseListener(sml);

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setEquipTab_AvailableListMode(viewMode);
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
			SettingsHandler.setEquipTab_SelectedListMode(viewSelectMode);
			createSelectedModel();
			selectedTable.updateUI();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
//Globals.debugPrint("--start-- formComponentShown");
//		requestFocus();
		PCGen_Frame1.getStatusBar().setText("");

//Globals.debugPrint("fCS:before:updateCharacterInfo");

		updateCharacterInfo();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			Component c = getParent();
			s = SettingsHandler.getPCGenOption("InfoEquipping.splitPane", (int) (c.getWidth() * 7 / 10));
			t = SettingsHandler.getPCGenOption("InfoEquipping.bsplit", (int) (c.getHeight() - 101));
			u = SettingsHandler.getPCGenOption("InfoEquipping.asplit", (int) (c.getWidth() - 408));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("EquipSel", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "EquipSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("EquipAva", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "EquipAva", i));
			}
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

		availableTable.updateUI();
		selectedTable.updateUI();

//Globals.debugPrint("--done-- formComponentShown");
	}

	public final void display()
	{
		formComponentShown(null);
	}

	// This recalculates the states of everything based
	// upon the currently selected character.
	public final void updateCharacterInfo()
	{
//Globals.debugPrint("  --start-- uCI: needsUpdate: "+needsUpdate);
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != aPC)
			needsUpdate = true;
		aPC = bPC;
		if (aPC == null || !needsUpdate)
			return;

		createModels();

		availableTable.updateUI();
		selectedTable.updateUI();

		needsUpdate = false;

//Globals.debugPrint("  --done-- uCI: needsUpdate: "+needsUpdate);
	}

	private void addEquipButton(ActionEvent evt)
	{
//Globals.debugPrint("------------------addEquipButton---------------- ");

		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String equipSetName = "";
		String locName = "";
		Equipment eqTarget = null;
		Equipment eqI = null;
		Equipment eq = null;
		EquipSet eSet = null;

		if (selCPath == null)
		{
			equipSetName = equipSetTextField.getText();
			eSet = aPC.getEquipSetByName(equipSetName);
		}
		else
		{
			Object endComp = selCPath.getLastPathComponent();
			PObjectNode fNode = (PObjectNode) endComp;
			if ((fNode.getItem() instanceof EquipSet))
			{
				equipSetName = selCPath.getPathComponent(1).toString();
				eSet = (EquipSet) fNode.getItem();
				if (!"".equals(eSet.getValue()))
				{
					eqTarget = eSet.getItem();
					if (eqTarget == null)
						eqTarget = Globals.getEquipmentNamed(eSet.getValue());
					if (!eqTarget.isContainer())
						eqTarget = null;
				}
			}
			if (eqTarget == null)
				eSet = aPC.getEquipSetByName(equipSetName);
		}

		if (eSet == null)
		{
			JOptionPane.showMessageDialog(null,
				"First select an Equip Set to add the item to",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		TreePath avaCPaths[] = availableTable.getTree().getSelectionPaths();
		for (int index = 0; index < avaCPaths.length; index++)
		{
			Object endComp = avaCPaths[index].getLastPathComponent();
			PObjectNode fNode = (PObjectNode) endComp;

			if (!(fNode.getItem() instanceof Equipment))
				return;

			// get the equipment Item from the available Table
			eqI = (Equipment) fNode.getItem();

			// check for natural weapons before cloning
			if (eqI.isNatural())
			{
				// if it's a natural weapon, just clone it
				eqI = (Equipment) eqI.clone();
			}
			else
			{
				// Can't clone eqI because it might be a container
				// with stuff in it already, or a weapon that is
				// already set as equipped, etc, etc
				eq = Globals.getEquipmentNamed(eqI.getName());
				eqI = (Equipment) eq.clone();
			}

			Globals.debugPrint("addEB: eqI: " + eqI.getName() + "  equipSetName:" + equipSetName);

			// Add the item of equipment, remembering the selected target for the next item.
			addEquipToTarget(eSet, eqTarget, locName, eqI);
		}

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		// reset EquipSet model to get the new equipment
		// added into the selectedTable tree
		selectedEquipSet = equipSetName;
		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				selectedTable.getTree().expandRow(i);
		}

		//selectedTable.expandByPObjectName(aSet.getName());

		return;

	}

	/**
	 * Handle a user request to make all equipment set as carried.
	 */
	private void addAllEquipButton()
	{
		Equipment eqI = null;
		Equipment eq = null;
		EquipSet eSet = null;
		String locName = S_CARRIED;

		eSet = getCurrentEquipSet();
		if (eSet == null)
		{
			JOptionPane.showMessageDialog(null,
				"First select an Equip Set to add the item to",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// We need to loop through the entire tree here!
		// iterate thru all PC's equip
		// and fill out the tree
		for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
		{
			eqI = (Equipment) fI.next();

			// check for natural weapons before cloning
			if (eqI.isNatural())
			{
				// if it's a natural weapon, just clone it
				eqI = (Equipment) eqI.clone();
			}
			else
			{
				// Can't clone eqI because it might be a container
				// with stuff in it already, or a weapon that is
				// already set as equipped, etc, etc
				eq = Globals.getEquipmentNamed(eqI.getName());
				eqI = (Equipment) eq.clone();
			}

			if (Globals.isDebugMode())
			{
				Globals.debugPrint("addEB: eqI: " + eqI.getName() + "  equipSetName:" + eSet.getName());
			}

			// Add the item of equipment
			addEquipToTarget(eSet, null, locName, eqI);
		}

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		// reset EquipSet model to get the new equipment
		// added into the selectedTable tree
		selectedEquipSet = eSet.getName();
		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				selectedTable.getTree().expandRow(i);
		}

		return;

	}

	/**
	 * Add the specified item of equipment to the provided equipset. The location
	 * in which the item is added is based on the eqTarget and locName fields. If
	 * eqTarget is set, it will override the locName setting.
	 */
	private String addEquipToTarget(EquipSet eSet, Equipment eqTarget, String locName, Equipment eqI)
	{

		// check to see if the target item is a container
		// and if so, insert the Equipment into it
		if (eqTarget != null)
		{
			// Make sure the container accepts items
			// of this type and is not full
			if ((eqTarget.canContain(eqI) == 1) && eqTarget.isContainer())
			{
				eqTarget.insertChild(eqI);
				eqI.setParent(eqTarget);
				locName = eqTarget.getName();
			}
		}
/*
			else
			{
				JOptionPane.showMessageDialog(null,
				  "You may not put " + eqI.getName() + " into " + eqTarget.getName(),
				  Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return null;
			}
*/
		if ("".equals(locName) || locName.length() == 0)
		{
			// get the possible locations for this item
			ArrayList aList = locationChoices(eqI);
			locName = getSingleLocation(eqI);

			if (locName.length() == 0)
			{
				// let them choose where to put the item
				ChooserInterface c = ChooserFactory.getChooserInstance();
				c.setAvailableList(aList);
				c.setVisible(false);
				c.setPoolFlag(false);
				c.setAllowsDups(false);
				c.setTitle("Choose Location for " + eqI.getName());
				c.setMessageText("Select the location for this item");
				c.setPool(1);
				c.show();

				aList = c.getSelectedList();
				if (c.getSelectedList().size() > 0)
					locName = (String) aList.get(0);
			}
		}

		if ("".equals(locName) || locName.length() == 0)
			return null;

		// make sure we can add item to that slot in this EquipSet
		if (!canAddEquip(eSet, locName, eqI, eqTarget))
		{
			JOptionPane.showMessageDialog(null,
				"Can not equip " + eqI.getName() + " to " + locName,
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return null;
		}

		// construct the new IdPath
		// new id is one larger than any other id at this path level
		String id = getNewIdPath(eSet);

		if (Globals.isDebugMode())
		{
			Globals.debugPrint("--addEB-- IdPath:" + id + "  Parent:" + eSet.getIdPath() + " Location:" + locName + " eqName:" + eqI.getName() + "  eSet:" + eSet.getName());
		}

		// now create a new EquipSet to add this Equipment item to
		EquipSet newSet = new EquipSet(id, locName, eqI.getName(), eqI);

		aPC.addEquipSet(newSet);

		return locName;
	}

	/**
	 * removes an item from the selected EquipSet
	 **/
	private void delEquipButton(ActionEvent evt)
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null,
				"Select the Equipment to remove from this set",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String equipSetName = "";

		TreePath selCPaths[] = selectedTable.getTree().getSelectionPaths();
		for (int index = 0; index < selCPaths.length; index++)
		{

			Object endComp = selCPaths[index].getLastPathComponent();
			PObjectNode fNode = (PObjectNode) endComp;

			if (!(fNode.getItem() instanceof EquipSet))
				return;

			equipSetName = selCPaths[index].getPathComponent(1).toString();
			equipSetTextField.setText(equipSetName);

			EquipSet eSet = (EquipSet) fNode.getItem();

			// only allow this button to delete equipment
			// not the root EquipSet node
			if (eSet.getItem() == null)
			{
				JOptionPane.showMessageDialog(null,
					"Use this to remove equipment, not the Equipment Set itself",
					Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				return;
			}

			Equipment eqI = (Equipment) eSet.getItem();

			if (Globals.isDebugMode())
			{
				Globals.debugPrint("--delEB--  getId:" + eSet.getId() + " Parent:" + eSet.getParentIdPath() + " eqName:" + eqI.getName() + " aSet:" + eSet.getName());
			}

			// remove it from EquipSet
			aPC.delEquipSet(eSet);
		}

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				selectedTable.getTree().expandRow(i);
		}

		return;
	}

	/**
	 * sets the quantity carried
	 **/
	private void setQtyButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null,
				"Select the Equipment first",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		String equipSetName = "";

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof EquipSet))
			return;

		equipSetName = selCPath.getPathComponent(1).toString();
		equipSetTextField.setText(equipSetName);

		EquipSet eSet = (EquipSet) fNode.getItem();
		// now make sure we have this PC's EquipSet
		equipSetName = eSet.getIdPath();
		eSet = aPC.getEquipSetByIdPath(equipSetName);
		Equipment eqI = (Equipment) eSet.getItem();

		if (eqI == null)
		{
			JOptionPane.showMessageDialog(null,
				"Select the Equipment first",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (eqI.isContainer())
		{
			JOptionPane.showMessageDialog(null,
				"Can not change quantity on containers",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		// only allow this button to change the quantity
		// of carried items or items inside a container
		String aLoc = eSet.getName();
		Float num = eSet.getQty();
		StringTokenizer aTok = new StringTokenizer(eSet.getIdPath(), ".", false);
		float newNum = num.floatValue();

		// if the eSet.getIdPath() is longer than 3 it's inside
		// a container, so allow them to set the quantity
		if ((aTok.countTokens() > 3) || aLoc.startsWith("Carried"))
		{
			Object selectedValue = JOptionPane.showInputDialog(null,
				"Enter new Quantity",
				Constants.s_APPNAME,
				JOptionPane.QUESTION_MESSAGE);
			if (selectedValue != null)
			{
				try
				{
					newNum = Float.parseFloat(((String) selectedValue).trim());
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null,
						"Invalid number!",
						Constants.s_APPNAME,
						JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{
				// canceled, so just return
				return;
			}
		}

		// if the new number is the same as the old number,
		// just return as there is nothing to do
		if (newNum == num.floatValue())
			return;

		//Globals.debugPrint("--setQty--  eSet:"+eSet.getName()+" eqName:"+eqI.getName()+" num:"+eSet.getQty()+" newNum:"+newNum);

		// set the new quantity
		eSet.setQty(new Float(newNum));

		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
				selectedTable.getTree().expandRow(i);
		}

		return;
	}

	/*
	 *****  **  **  **   **  **  ** **    **
	 **     **  **  ***  **  ** **   **  **
	 ***This is used to add new equipment Sets when
	 ***the equipSetTextField JTextField is edited
	 **     **  **  **  ***  ** **     **
	 **     ******  **   **  **  **    **
	 */
	private void addEquipSetButton(ActionEvent evt)
	{
		String equipSetFieldText = equipSetTextField.getText();

		if (Globals.isDebugMode())
		{
			Globals.debugPrint("addEquipSetButton:equipSetFieldText: ", equipSetFieldText);
		}

		if (equipSetFieldText.equals(selectedEquipSet))
		{
			Globals.debugPrint("addESB: Set already exists: equipSetFieldText: " + equipSetFieldText + " == " + selectedEquipSet);
			return;
		}
		EquipSet pcSet = aPC.getEquipSetByName(equipSetFieldText);
		if (pcSet != null)
		{
			Globals.debugPrint("addESB: Set already exists: ", equipSetFieldText);
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
		createSelectedModel();
		selectedTable.updateUI();

		return;
	}

	/*
	 *****  **  **  **   **  **   ** **    **
	 **  ** **  **  ***  **  ***  **  **  **
	 **This deletes the EquipSet and all "children" of the set
	 **the children all have the same parent Id as this EquipSet
	 **  ** **  **  **  ***  **  ***    **
	 *****  ******  **   **  **   **    **
	 */
	private void delEquipSetButton(ActionEvent evt)
	{
		String equipSetFieldText = equipSetTextField.getText();
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("delEquipSetButton:equipSetFieldText: ", equipSetFieldText);
		}

		EquipSet eSet = aPC.getEquipSetByName(equipSetFieldText);

		if (eSet == null)
		{
			Globals.debugPrint("delEquipSetButton: No EquipSet named: ", equipSetFieldText);
			return;
		}

		int iConfirm = JOptionPane.showConfirmDialog(null,
			"Are you sure you want to delete?",
			"Confirm Remove",
			JOptionPane.YES_NO_OPTION);
		if (iConfirm != JOptionPane.YES_OPTION)
			return;

		if (aPC.delEquipSet(eSet))
		{
			aPC.setDirty(true);
			selectedEquipSet = "";
			createSelectedModel();
			selectedTable.updateUI();
		}
		else
		{
			Globals.debugPrint("delEquipSetButton:failed ");
			return;
		}

		return;
	}

	/**
	 * Process a request to duplicate the current equipment set.
	 */
	private void copyEquipSetButton()
	{
		EquipSet eSet;
		EquipSet equipItem;
		String equipSetName = "";
		String pid;
		ArrayList newEquipSet = new ArrayList();

		eSet = getCurrentEquipSet();
		if (eSet == null)
		{
			JOptionPane.showMessageDialog(null,
				"First select an Equip Set to duplicate.",
				Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		pid = eSet.getIdPath();

		// Get a new name
		equipSetName = JOptionPane.showInputDialog(null,
			"Enter name for new Equip Set",
			Constants.s_APPNAME,
			JOptionPane.QUESTION_MESSAGE);
		if (equipSetName != null && equipSetName.length() > 0)
		{
			// get an new unique id that is one higher than any
			// other EquipSet attached to the root node
			String id = getNewIdPath(null);

			eSet = (EquipSet) eSet.clone();
			eSet.setIdPath(id);
			eSet.setName(equipSetName);

			selectedEquipSet = equipSetName;
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
			createSelectedModel();
			selectedTable.updateUI();
		}
	}

	private EquipSet getCurrentEquipSet()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String equipSetName = "";
		EquipSet eSet = null;

		if (selCPath == null)
		{
			equipSetName = equipSetTextField.getText();
			eSet = aPC.getEquipSetByName(equipSetName);
		}
		else
		{
			Object endComp = selCPath.getLastPathComponent();
			PObjectNode fNode = (PObjectNode) endComp;
			if ((fNode.getItem() instanceof EquipSet))
			{
				equipSetName = selCPath.getPathComponent(1).toString();
			}
			eSet = aPC.getEquipSetByName(equipSetName);
		}

		return eSet;
	}

	/*
	 * If an item can only go in one location, return the name of that
	 * location to add to an EquipSet
	 */
	private static String getSingleLocation(Equipment eqI)
	{
		String locName = "";

		if (eqI.isNatural())
		{
			if (eqI.isOnlyNaturalWeapon())
				locName = S_PRIMARY;
			else if (eqI.modifiedName().endsWith("Secondary"))
				locName = S_SECONDARY;
			else if (eqI.modifiedName().endsWith("Primary"))
				locName = S_BOTH;
		}
		else if (eqI.isSuit() || eqI.isType("ROBE"))
		{
			// body
			locName = "Body";
		}
		else if (eqI.isType("HEADGEAR"))
		{
			// head
			locName = "Head";
		}
		else if (eqI.isType("EYEGEAR"))
		{
			// eyes
			locName = "Eyes";
		}
		else if (eqI.isType("MASK"))
		{
			// Face
			locName = "Face";
		}
		else if (eqI.isType("AMULET") || eqI.isType("NECKLACE"))
		{
			// neck
			locName = "Neck";
		}
		else if (eqI.isType("CAPE") || eqI.isType("CLOAK"))
		{
			// torso back
			locName = "Back";
		}
		else if (eqI.isType("CLOTHING"))
		{
			// torso
			locName = "Torso";
		}
		else if (eqI.isType("SHIRT") || eqI.isType("VEST"))
		{
			// torso front (chest?)
			locName = "Chest";
		}
		else if (eqI.isType("BRACER") || eqI.isType("ARMWEAR"))
		{
			// could be both arms or just one?
			locName = "Arms";
		}
		else if (eqI.isType("GLOVE"))
		{
			// could be both hands or just one
			// but none of the .lst files have this info
			// stored in them right now... So just use both
			locName = "Hands";
		}
		else if (eqI.isType("BELT"))
		{
			// waist
			locName = "Waist";
		}
		else if (eqI.isType("BOOT"))
		{
			// both feet
			locName = "Feet";
		}
		return locName;
	}

	/*
	 * returns true if you can put Equipment into a location in EquipSet
	 */
	private static boolean canAddEquip(EquipSet eSet, String locName, Equipment eqI, Equipment eqTarget)
	{
		String idPath = eSet.getIdPath();

		// If Carried/Equipped slot
		if (locName.startsWith(S_CARRIED) ||
			locName.startsWith(S_EQUIPPED))
			return true;

		// If target is a container, allow it
		if (eqTarget != null)
			if (eqTarget.isContainer())
				return true;

		// allow as many unarmed items as you'd like
		if (eqI.isUnarmed())
			return true;

		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();

			// check to make sure that we do not already
			// have an item in that particular location
			if (es.getParentIdPath().startsWith(idPath) &&
				es.getName().equals(locName))
				return false;

			// if it's a weapon we have to do some
			// checks for hands already in use
			if (es.getParentIdPath().startsWith(idPath) &&
				eqI.isWeapon())
			{
				// if Double Weapon or Both Hands, then no
				// other weapon slots can be occupied
				if ((locName.equals(S_BOTH) || locName.equals(S_DOUBLE)) &&
					(es.getName().equals(S_PRIMARY) ||
					es.getName().equals(S_SECONDARY) ||
					es.getName().equals(S_BOTH) ||
					es.getName().equals(S_DOUBLE)))
				{
					return false;
				}
				// inverse of above case
				if ((locName.equals(S_PRIMARY) || locName.equals(S_SECONDARY)) &&
					(es.getName().equals(S_BOTH) ||
					es.getName().equals(S_DOUBLE)))
				{
					return false;
				}
			}
		}
		return true;
	}

	/*
	 *
	 * returns the primary location Name an an equipment item
	 *
	 */
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
			locTypeName = S_UNARMED;
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
			locTypeName = "Clothing";
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
			pid = eSet.getIdPath();
		for (Iterator e = aPC.getEquipSet().iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().equals(pid) && es.getId() > newID)
				newID = es.getId();
		}
		newID++;
		return pid + '.' + newID;
	}

	/*
	 * Returns and array of locations where the equipment can
	 * be placed. This array is different based on what type
	 * of equipment is passed to this function
	 */
	private static final ArrayList locationChoices(Equipment eqI)
	{
		final String[] a_weapon = {
			S_PRIMARY,
			S_SECONDARY,
			S_BOTH,
			S_CARRIED
		};
		final String[] s_weapon = {
			S_PRIMARY,
			S_SECONDARY,
			S_CARRIED
		};
		final String[] fingers = {
			"Left Fingers",
			"Right Fingers"
		};
		final String[] both = {
			S_BOTH,
			S_CARRIED
		};
		final String[] dweapon = {
			S_PRIMARY,
			S_SECONDARY,
			S_DOUBLE,
			S_CARRIED
		};
		final String[] shield = {
			"Shield",
			S_CARRIED
		};
		final String[] unarmed = {
			S_UNARMED
		};
		final String[] other = {
			S_EQUIPPED,
			S_CARRIED
		};

		ArrayList aList = null;

		if (eqI.isWeapon())
		{
			if (eqI.isUnarmed())
			{
				aList = new ArrayList(Arrays.asList(unarmed));
			}
			// This is commented out until I can figure out
			// a way to reliable decide if a weapon can only
			// be used two-handed or if it can be wielded with
			// just one hand (like monkey-grip, Bastard sword, etc)
			/***************************************************
			 ** START COMMENT **
			 else if (Globals.isWeaponTwoHanded(aPC, eqI, wp))
			 {
			 aList = new ArrayList(Arrays.asList(both));
			 }
			 else if (Globals.isWeaponLightForPC(aPC, eqI) && !Globals.isWeaponTwoHanded(aPC, eqI, wp))
			 {
			 aList = new ArrayList(Arrays.asList(s_weapon));
			 }
			 ** END COMMENT **
			 ****************************************************/
			else if (eqI.isDouble())
			{
				aList = new ArrayList(Arrays.asList(dweapon));
			}
			else
			{
				aList = new ArrayList(Arrays.asList(a_weapon));
			}
		}
		else if (eqI.isShield())
		{
			aList = new ArrayList(Arrays.asList(shield));
		}
		else if (eqI.isType("RING"))
		{
			aList = new ArrayList(Arrays.asList(fingers));
		}
		else
		{
			aList = new ArrayList(Arrays.asList(other));
		}
		return aList;
	}

	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
			availableModel = new EquipModel(viewMode, true);
		else
			availableModel.resetModel(viewMode, true, false);
		if (availableSort != null)
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new EquipModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
		if (selectedSort != null)
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
	}

	/** The TreeTableModel has a single <code>root</code> node
	 *  This root node has a null <code>parent</code>.
	 *  All other nodes have a parent which points to a non-null node.
	 *  Parent nodes contain a list of  <code>children</code>, which
	 *  are all the nodes that point to it as their parent.
	 *  <code>nodes</code> which have 0 children are leafs (the end of
	 *  that linked list).  nodes which have at least 1 child are not leafs
	 *  Leafs are like files and non-leafs are like directories.
	 *  The leafs contain an Object that we want to know about (Equipment)
	 */
	private final class EquipModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// there are two roots. One for available equipment
		// and one for selected equipment profiles
		private PObjectNode avaRoot;
		private PObjectNode selRoot;

		// list of columns names
		private String[] avaNameList = {""};
		private String[] selNameList = {""};

		// Types of the columns.
		private int modelType = MODEL_AVAIL;

		/**
		 * Creates a EquipModel
		 */
		private EquipModel(int mode, boolean available)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			avaNameList = new String[]{"Name", "Type", "Source"};
			selNameList = new String[]{"Location", "Type", "Qty", "Item"};

			if (!available)
				modelType = MODEL_SELECTED;
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the EquipModel exists but
		 * needs branches and nodes to be repopulated
		 */
		private void resetModel(int mode, boolean available, boolean newCall)
		{
			// This is the array of all equipment types
			ArrayList typeList = new ArrayList();
			ArrayList locList = new ArrayList();

			// build the list of all equipment types
			typeList.add(Constants.s_CUSTOM);
			for (Iterator iSet = aPC.getEquipmentList().iterator(); iSet.hasNext();)
			{
				final Equipment bEq = (Equipment) iSet.next();
				final StringTokenizer aTok = new StringTokenizer(bEq.getType(), ".", false);
				String aString = aTok.nextToken();
				while (aTok.hasMoreTokens())
				{
					aString = aTok.nextToken();
					if (!typeList.contains(aString))
						typeList.add(aString);
				}
				aString = getSingleLocation(bEq);
				if (!locList.contains(aString) && aString.length() > 0)
					locList.add(aString);
			}
			locList.add("Weapon");
			locList.add("Shield");
			locList.add("Fingers");
			locList.add("Other");
			Collections.sort(typeList);
			Collections.sort(locList);

			// Setup the default EquipSet if not already present
			if (aPC.getEquipSet().size() == 0)
			{
				String id = getNewIdPath(null);
				EquipSet eSet = new EquipSet(id, defaultEquipSet);
				aPC.addEquipSet(eSet);
				if (Globals.isDebugMode())
				{
					Globals.debugPrint("Adding EquipSet: ", defaultEquipSet);
				}
				selectedEquipSet = defaultEquipSet;
			}

			//
			// build availableTable (list of all equipment)
			//
			if (available)
			{
				// this is the root node
				avaRoot = new PObjectNode();

				switch (mode)
				{
					// Equipment Type Tree
					case GuiConstants.INFOEQUIPPING_VIEW_TYPE:

						setRoot(avaRoot);

						// build the Type root nodes
						PObjectNode eq[] = new PObjectNode[typeList.size()];
						// iterate thru the equipment
						// type and fill out the tree
						for (int iType = 0; iType < typeList.size(); iType++)
						{
							String aType = (String) typeList.get(iType);
							eq[iType] = new PObjectNode();
							eq[iType].setItem(aType);
							for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
							{
								final Equipment aEq = (Equipment) fI.next();
								if (!aEq.isType(aType))
									continue;
								PObjectNode aFN = new PObjectNode();
								aFN.setItem(aEq);
								aFN.setParent(eq[iType]);
								eq[iType].addChild(aFN);
							}
							if (!eq[iType].isLeaf())
							{
								eq[iType].setParent(avaRoot);
							}
						} // end type loop
						// now add to the root node
						avaRoot.setChildren(eq, true);

						break; // end VIEW_TYPE

						// Equipment Location Tree
					case GuiConstants.INFOEQUIPPING_VIEW_LOCATION:

						setRoot(avaRoot);

						// build the Location root nodes
						PObjectNode loc[] = new PObjectNode[locList.size()];
						// iterate thru the equipment
						// type and fill out the tree
						for (int iLoc = 0; iLoc < locList.size(); iLoc++)
						{
							String aLoc = (String) locList.get(iLoc);
							loc[iLoc] = new PObjectNode();
							loc[iLoc].setItem(aLoc);
							for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
							{
								final Equipment aEq = (Equipment) fI.next();
								String aString = getSingleLocation(aEq);
								if (aEq.isWeapon())
									aString = "Weapon";
								else if (aEq.isShield())
									aString = "Shield";
								else if (aEq.isType("RING"))
									aString = "Fingers";
								if (aString.length() == 0)
									aString = "Other";
								if (!aLoc.equals(aString))
									continue;
								PObjectNode aFN = new PObjectNode();
								aFN.setItem(aEq);
								aFN.setParent(loc[iLoc]);
								loc[iLoc].addChild(aFN);
							}
							if (!loc[iLoc].isLeaf())
							{
								loc[iLoc].setParent(avaRoot);
							}
						} // end location loop
						// now add to the root node
						avaRoot.setChildren(loc, true);

						break; // end VIEW_LOCATION

						// just by equipment name
					case GuiConstants.INFOEQUIPPING_VIEW_NAME:

						setRoot(avaRoot);

						// iterate thru all PC's equip
						// and fill out the tree
						for (Iterator fI = aPC.getEquipmentList().iterator(); fI.hasNext();)
						{
							final Equipment aEq = (Equipment) fI.next();
							PObjectNode aFN = new PObjectNode();
							aFN.setItem(aEq);
							aFN.setParent(avaRoot);
							avaRoot.addChild(aFN, true);
						}

						break; // end VIEW_NAME
					default:
						Globals.errorPrint("In InfoEquipping.resetModel the mode " + mode + " is not supported.");
						break;

				} // end of switch(mode)
			} // end of availableTable builder

			else

			{ // selectedTable builder (it's a list of Equip sets)

				// this is the root node
				selRoot = new PObjectNode();

				// get the current EquiSet's
				equipSetList = aPC.getEquipSet();

				// Make sure it's sorted by pathId
				Collections.sort(equipSetList);

				// create a clone to manipulate
				tempSetList = (ArrayList) equipSetList.clone();

				// EquipSet tree
				addEquipTreeNodes(selRoot, null);
				setRoot(selRoot);

			} // end if else

			PObjectNode rootAsPObjectNode = (PObjectNode) root;
			if (rootAsPObjectNode.getChildCount() > 0)
				fireTreeStructureChanged(root, rootAsPObjectNode.getChildren(), null, null);

			if ("".equals(selectedEquipSet))
				selectedEquipSet = defaultEquipSet;
			// make sure the JTextField is correct
			equipSetTextField.setText(selectedEquipSet);

		}

		// There must be a root node, but we keep it hidden
		private void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		// return the root node
		public final Object getRoot()
		{
			return (PObjectNode) root;
		}

		private void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode) root;

			PObjectNode pAsPObjectNode = p;
			// if no children, remove it and update parent
			if (pAsPObjectNode.getChildCount() == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < pAsPObjectNode.getChildCount(); i++)
				{
					removeItemFromNodes(pAsPObjectNode.getChild(i), e);
				}
			}
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (EquipModel)
		 */
		public int getColumnCount()
		{
			return modelType == 0 ? avaNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column. (EquipModel)
		 */
		public String getColumnName(int column)
		{
			return modelType == MODEL_AVAIL ? avaNameList[column] : selNameList[column];
		}

		/**
		 * Returns Class for the column. (EquipModel)
		 */
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_NAME:
					return TreeTableModel.class;

				case COL_TYPE:
				case COL_QTY:
				case COL_LOCATION:
				case COL_SRC:
					break;

				default:
					Globals.errorPrint("In InfoEquipping.getColumnClass the column " + column + " is not supported.");
					break;
			}
			return String.class;
		}

		/**
		 * Returns boolean if can edit a cell. (EquipModel)
		 */
		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME || column == COL_LOCATION);
		}

		/**
		 * changes the column order sequence and/or number of
		 * columns based on modelType (0=available, 1=selected)
		 **/
		private int adjustAvailColumnConst(int column)
		{
			// available table
			if ((modelType == MODEL_AVAIL) && (column >= COL_QTY))
				column = COL_SRC;
			return column;
		}

		/**
		 * Returns Object value of the column. (EquipModel)
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;
			EquipSet eSet = null;
			Equipment eqI = null;

			if (fn == null)
			{
				Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoEquipping.");
				return null;
			}

			column = adjustAvailColumnConst(column);

//Globals.debugPrint("getValueAt:before eqI  model("+modelType+")("+column+") "+fn.toString());

			if (fn.getItem() instanceof Equipment)
				eqI = (Equipment) fn.getItem();

			if (fn.getItem() instanceof EquipSet)
			{
				eSet = (EquipSet) fn.getItem();
				//eqI = Globals.getEquipmentNamed(eSet.getValue());
				eqI = eSet.getItem();
			}

			switch (column)
			{
				case COL_NAME:
					return fn != null ? fn.toString() : null;
				case COL_TYPE:
					if (eqI != null)
					{
						String type = getEqTypeName(eqI);
						if ("".equals(type))
						{
							StringTokenizer aTok = new StringTokenizer(eqI.getType(), ".", false);
							type = aTok.nextToken();
						}
						return type;
					}
					else
						return null;
				case COL_QTY:
					return (eSet != null) && (eSet.getValue().length() > 0) ? eSet.getQty().toString() : null;
				case COL_LOCATION:
					// return the equipment item's PI-formatted name first, then fall back to eSet name if needed
					if (eqI != null)
						return eqI.piString();
					else if (eSet != null)
						return eSet.getValue();
					else
						return null;
				case COL_SRC:
					return eqI != null ? eqI.getSource() : null;
				default:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in InfoEquip.");
						return null;
					}

			}
			// return null;
		}

	}

	private void addEquipTreeNodes(PObjectNode aNode, EquipSet aSet)
	{
		// create a temporary list of EquipSets to pass to this
		// function when we recursivly call it for child nodes
		ArrayList aList = new ArrayList();

		String idPath = "0";

		if (aSet != null)
		{
			idPath = aSet.getIdPath();

//Globals.debugPrint("aETN: aSet:"+aSet.getIdPath()+"  name: "+aSet.getName()+"  value:"+aSet.getValue()+"  getPId:"+aSet.getParentIdPath());
		}

		// process all EquipSet Items
		for (int iSet = 0; iSet < tempSetList.size(); iSet++)
		{
			EquipSet es = (EquipSet) tempSetList.get(iSet);
			if (es.getParentIdPath().equals(idPath))
			{
				PObjectNode fN = new PObjectNode();
				fN.setItem(es);
				fN.setParent(aNode);
				aNode.addChild(fN);

				// add to list for recursive calls
				aList.add(es);
				// and remove from tempSetList so
				// it won't get processed again
				tempSetList.remove(es);
				iSet--;

//Globals.debugPrint("aETN: es:"+es.getIdPath()+"  name: "+es.getName()+"  value:"+es.getValue()+"  getPId:"+es.getParentIdPath());
			}

		}
		// recursivly call addEquipTreeNodes to get all
		// the child EquipSet items added to each root node
		for (int i = 0; i < aList.size(); i++)
		{
			addEquipTreeNodes(aNode.getChild(i), (EquipSet) aList.get(i));
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
	private void selectTemplateButton(ActionEvent e)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your EquipSet template");
		fc.setCurrentDirectory(SettingsHandler.getTemplatePath());
		fc.setSelectedFile(new File(SettingsHandler.getSelectedEqSetTemplate()));
		if (fc.showOpenDialog(InfoEquipping.this) == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedEqSetTemplate(fc.getSelectedFile().getAbsolutePath());
			SettingsHandler.setTemplatePath(fc.getSelectedFile().getParentFile());
			templateTextField.setText(SettingsHandler.getSelectedEqSetTemplate());
		}
	}

	/*
	 *
	 * Previews the EquipSets through selected template in the Browser
	 *
	 */
	private void viewEqSetButton(ActionEvent e)
	{
		File outFile = getTempPreviewFile();

		// ensure we've got something
		if (outFile == null)
		{
			// message will have been displayed already
			return;
		}

		try
		{
			FileWriter w = new FileWriter(outFile);
			printToWriter(w);
			w.flush();
			w.close();
			final String osName = System.getProperty("os.name");
			//
			// Windows tends to lock up or not actually display
			// anything unless we've specified a default browser,
			// so at least make the user aware that (s)he needs one.
			// If they don't pick one and it doesn't work, at least
			// they might know enough to try selecting one next time
			//
			if (osName.startsWith("Windows ") && (SettingsHandler.getBrowserPath() == null))
			{
				Utility.selectDefaultBrowser(null);
			}

			if (osName.startsWith("Mac OS"))
				BrowserLauncher.openURL(outFile.toString());
			else
			{
				java.net.URL url = outFile.toURL();
				BrowserLauncher.openURL(url.toString());
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Could not preview file in external browser. Sorry...", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}

	}

	/*
	 *
	 * Exports the EquipSets through selected template to a file
	 *
	 */
	private void exportEqSetButton(ActionEvent e)
	{
		final String template = SettingsHandler.getSelectedEqSetTemplate();
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getHtmlOutputPath());

		fcExport.setSelectedFile(new File(SettingsHandler.getHtmlOutputPath() + File.separator + "eqset_" + aPC.getDisplayName() + ext));
		fcExport.setDialogTitle("Export EquipSet " + aPC.getDisplayName());

		if (fcExport.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;

		final String aFileName = fcExport.getSelectedFile().getAbsolutePath();
		SettingsHandler.setHtmlOutputPath(fcExport.getSelectedFile().getParentFile());
		if (aFileName.length() < 1)
		{
			JOptionPane.showMessageDialog(null, "You must set a filename.", "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			final File outFile = new File(aFileName);
			if (outFile.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a file.", "PCGen", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (outFile.exists())
			{
				int reallyClose = JOptionPane.showConfirmDialog(this, "The file " + outFile.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + outFile.getName(), JOptionPane.YES_NO_OPTION);
				if (reallyClose != JOptionPane.YES_OPTION)
					return;
			}

			if (ext.equalsIgnoreCase(".htm") || ext.equalsIgnoreCase(".html"))
			{
				FileWriter w = new FileWriter(outFile);
				printToWriter(w);
				w.flush();
				w.close();
			}
			else if (ext.equalsIgnoreCase(".fo") || ext.equalsIgnoreCase(".pdf"))
			{

				File tmpFile = File.createTempFile("equipSet_", ".fo");
				FileWriter w = new FileWriter(tmpFile);
				printToWriter(w);
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
					JOptionPane.showMessageDialog(null,
						errMessage,
						"PCGen",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not export " + aPC.getDisplayName() + ". Try another filename", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	/**
	 * Creates a temporary preview file for display.
	 **/
	private static File getTempPreviewFile()
	{
		final String template = SettingsHandler.getSelectedEqSetTemplate();

		// include . in extension
		String ext = template.substring(template.lastIndexOf('.'));
		File tempFile = null;

		try
		{
			// create a temporary file to view the character output
			tempFile = File.createTempFile("equipSets", ext, SettingsHandler.getTempPath());
		}
		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Could not create temporary equip sets preview file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace();
		}
		return tempFile;
	}

	/**
	 * Prints the characters EquipSet's to the writer specified.
	 *
	 * @param w  The writer to print the data to.
	 * @throws IOException  If any problems occur in writing the data
	 */
	private void printToWriter(Writer w) throws IOException
	{
		File esTemplate = new File(SettingsHandler.getSelectedEqSetTemplate());
		String name = esTemplate.getName().toLowerCase();

		/*
		if (!name.startsWith(Constants.s_EQSET_TEMPLATE_START))
		{
			throw new IOException(name + " is not a valid EquipSet template file name.");
		}
		*/

		int tests[] = new int[]{0, 0};
		int length = 0;

		InputStream in = new FileInputStream(esTemplate);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		while (br.readLine() != null)
			length++;

		BufferedWriter bw = new BufferedWriter(w);

		File reParse = File.createTempFile("eqTTemp_", ".tmp");
		name = reParse.getPath();
		BufferedWriter rpW = new BufferedWriter(new FileWriter(name, true));

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
				String aLine = "";
				in = new FileInputStream(temp);
				br = new BufferedReader(new InputStreamReader(in));
				while ((aLine = br.readLine()) != null)
				{
					rpW.write(aLine);
					rpW.newLine();
				}
				rpW.flush();
				in.close();
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
		String aLine = "";
		InputStream in = null;
		BufferedWriter output = null;
		try
		{

			FileWriter w = new FileWriter(tmpFile);
			output = new BufferedWriter(w);

			// read in the eqsheet template file
			in = new FileInputStream(template);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			ArrayList lines = new ArrayList();

			while ((aLine = br.readLine()) != null)
			{
				lines.add(aLine);
			}
			String line = "";

			// parse each line and look for EQSET delimiter
			while (!done && (lineNum < lines.size()))
			{
				line = (String) lines.get(lineNum);
				if (line.indexOf("|EQSET.START|") > -1)
				{
					lineNum++;
					done = true;
				}
				else if (line.indexOf("|EQSET.END|") > -1)
				{
					lineNum++;
					done = true;
					eqset = true;
				}
				else
				{
					output.write(line);
					output.newLine();
					lineNum++;
				}
			}
		} // end of try

		catch (IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Could not create temporary equip sets preview file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ioe.printStackTrace();
		}
		finally 
		{
			try {
				in.close();
				output.flush();
				output.close();
			} 
			catch (IOException e) {}
			catch (NullPointerException e) {}	
		}
		// return the last line we've parsed through
		ret[0] = lineNum;
		// if we should process an EQSET loop
		if (eqset)
			ret[1] = 1;
		else
			ret[1] = 0;
		return ret;
	}

	/**
	 *
	 * takes a template file as input
	 * File template is the original template file
	 * that has been parsed and seperated by
	 * |EQSET.START| and
	 * |EQSET.END| tags
	 *
	 * Loops through all EquipSet's, sets equipped, carried, etc
	 * status on the equipment in each EquipSet and the sends
	 * template file to ExportHandler to get parsed.
	 * The output from ExportHandler gets appended to: out
	 *
	 **/
	private static void equipItch(File template, BufferedWriter out)
	{
		// Array containing the id's of root EquipSet's
		ArrayList eqRootList = new ArrayList();

		// we count all EquipSet with parent of 0
		for (Iterator e = equipSetList.iterator(); e.hasNext();)
		{
			EquipSet es = (EquipSet) e.next();
			if (es.getParentIdPath().equals("0"))
			{
				eqRootList.add(es);
			}
		}

		// First, we have to save off the current equipmentList
		ArrayList oldEqList = aPC.getEquipmentList();

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

			if (Globals.isDebugMode())
			{
				Globals.debugPrint("Preview:(" + pid + ") " + esRL.getName());
			}

			// create a new equipmentList
			ArrayList eqList = new ArrayList();
			// set the PC's equipmentList to the new one
			aPC.setEquipmentList(eqList);

			// loop through all the EquipSet's and create equipment
			// then set status to equipped and add to PC's eq list
			for (Iterator e = equipSetList.iterator(); e.hasNext();)
			{
				EquipSet es = (EquipSet) e.next();
				if (!es.getParentIdPath().startsWith(pid))
				{
					continue;
				}

				Equipment eqI = es.getItem();
				if (eqI == null)
				{
					continue;
				}

				Equipment eq = es.getItem();
				String aLoc = es.getName();
				Float num = es.getQty();
				StringTokenizer aTok = new StringTokenizer(es.getIdPath(), ".", false);

				// if the eSet.getIdPath() is longer than 3
				// it's inside a container, don't try to equip
				if (aTok.countTokens() > 3)
				{
					eq.setIsEquipped(false);
					eq.setNumberCarried(num);
					eq.setQty(num);
					// get parent EquipSet
					//EquipSet cs = aPC.getEquipSetByIdPath(es.getParentIdPath());
					// get the container
					//Equipment eqT = cs.getItem();
					// add the child to container
					//eqT.insertChild(eq);
					//eq.setParent(eqT);
				}
				else if (aLoc.startsWith(S_CARRIED))
				{
					eq.setIsEquipped(false);
					eq.setNumberCarried(num);
					eq.setQty(num);
				}
				else if (eq.isWeapon())
				{
					if (aLoc.equals(S_PRIMARY))
					{
						eq.setQty(num);
						eq.setNumberCarried(num);
						eq.setNumberEquipped(num.intValue());
						eq.setHand(Equipment.PRIMARY_HAND);
						eq.setIsEquipped(true);
					}
					else if (aLoc.equals(S_SECONDARY))
					{
						eq.setQty(num);
						eq.setNumberCarried(num);
						eq.setNumberEquipped(num.intValue());
						eq.setHand(Equipment.SECONDARY_HAND);
						eq.setIsEquipped(true);
					}
					else if (aLoc.equals(S_BOTH))
					{
						eq.setQty(num);
						eq.setNumberCarried(num);
						eq.setNumberEquipped(num.intValue());
						eq.setHand(Equipment.BOTH_HANDS);
						eq.setIsEquipped(true);
					}
					else if (aLoc.equals(S_DOUBLE))
					{
						eq.setQty(num);
						eq.setNumberCarried(num);
						eq.setNumberEquipped(2);
						eq.setHand(Equipment.TWOWEAPON_HANDS);
						eq.setIsEquipped(true);
					}
					else if (aLoc.equals(S_UNARMED))
					{
						eq.setIsEquipped(true);
						eq.setNumberEquipped(num.intValue());
					}
				}
				else
				{
					eq.setIsEquipped(true);
					eq.setNumberCarried(num);
					eq.setQty(num);
				}

				aPC.getEquipmentList().add(eq);
			}

			// loop through all equipment and make sure that
			// containers contents are updated
			for (Iterator e = aPC.getEquipmentList().iterator(); e.hasNext();)
			{
				Equipment eq = (Equipment) e.next();
				if (eq.isContainer())
				{
					eq.updateContainerContentsString();
				}
			}

			// using the split eqsheet template
			// print out the current EquipSet to file
			(new ExportHandler(template)).write(aPC, out);

			// then loop again for the next EquipSet
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

		// Last, set the PC's equipmentList back to the old one
		aPC.setEquipmentList(oldEqList);

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
		createModels();
		availableTable.updateUI();
		selectedTable.updateUI();
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
