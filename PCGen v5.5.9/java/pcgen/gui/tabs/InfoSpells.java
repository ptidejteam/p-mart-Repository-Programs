/*
 * InfoSpells.java
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
 * Written by Bryan McRoberts <merton_monk@users.sourceforge.net>,
 * Re-written by Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on April 21, 2001, 2:15 PM
 * Re-created on April 1st, 2002, 2:15 am
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:27 $
 *
 */

package pcgen.gui.tabs;

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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.CharacterDomain;
import pcgen.core.Constants;
import pcgen.core.Domain;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellInfo;
import pcgen.core.spell.Spell;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.ChooserFactory;
import pcgen.gui.utils.ChooserInterface;
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
import pcgen.util.FOPHandler;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 *  <code>InfoSpells</code> creates a new tabbed panel.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>, Jayme Cox <jaymecox@netscape.net>
 * created    den 11 maj 2001
 * @version    $Revision: 1.1 $
 */
public class InfoSpells extends FilterAdapterPanel
{
	static final long serialVersionUID = 755097384157285101L;
	private SpellModel availableModel = null;  // Model for JTreeTable
	private SpellModel selectedModel = null;   // Model for JTreeTable
	private JTreeTable availableTable;         // available Spells
	private JTreeTable selectedTable;	   // spellbook Spells
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;
	private static List bookList = new ArrayList();

	private JLabelPane infoLabel = new JLabelPane();
	private JLabelPane classLabel = new JLabelPane();
	private final JLabel avaLabel = new JLabel("Sort Spells By:");
	private final JLabel selLabel = new JLabel("Sort SpellBooks By:");

	private JCheckBox shouldAutoSpells = new JCheckBox("Add auto known spells on level/load");
	private JTextField spellBookNameText = new JTextField();
	private static String currSpellBook = Globals.getDefaultSpellBook();

	private SpellFeatModel featModel = new SpellFeatModel();
	private JButton addSpellButton;
	private JButton delSpellButton;
	private JButton addBookButton;
	private JButton delBookButton;
	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();
	private Border etched;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;

	private JButton selectSpellSheetButton = new JButton("Select Spellsheet");
	private JButton printHtml;
	private JButton printPdf;
	private JTextField selectSpellSheetField = new JTextField();

	private JMenuItem addMenu;
	private JMenuItem addMMMenu;

	private static PlayerCharacter aPC = null;

	private TreePath selPath;
	private Spell lastSpell = null;
	private String lastClass = "";

	private boolean needsUpdate = true;
	private boolean hasBeenSized = false;

	private JComboBoxEx primaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewComboBox = new JComboBoxEx();
	private JComboBoxEx primaryViewSelectComboBox = new JComboBoxEx();
	private JComboBoxEx secondaryViewSelectComboBox = new JComboBoxEx();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;

	private static int primaryViewMode = 0;
	private static int secondaryViewMode = 1;
	private static int primaryViewSelectMode = 0;
	private static int secondaryViewSelectMode = 1;

	// table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECTED = 1;

	//column positions for tables
	// if you change these, you also need to change
	// the selNameList array in the SpellModel class
	private static final int COL_NAME = 0;
	private static final int COL_SCHOOL = 1;
	private static final int COL_SUBSCHOOL = 2;
	private static final int COL_DESCRIPTOR = 3;
	private static final int COL_COMPONENT = 4;
	private static final int COL_CASTTIME = 5;
	private static final int COL_RANGE = 6;
	private static final int COL_DESCRIPTION = 7;
	private static final int COL_TARGET = 8;
	private static final int COL_DURATION = 9;
	private static final int COL_SAVE = 10;
	private static final int COL_SR = 11;
	private static final int COL_SRC = 12;

	private class SpellPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = 755097384157285101L;

		private class SpellActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton();
			}
		}

		private class AddMMSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				metamagicButton();
			}
		}

		private class DelSpellActionListener extends SpellActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton();
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			addMenu = Utility.createMenuItem(label, new AddSpellActionListener(), "add 1", (char) 0, accelerator, "Add Spell to Spellbook", "Add16.gif", true);
			return addMenu;
		}

		private JMenuItem createAddMMMenuItem(String label, String accelerator)
		{
			addMMMenu = Utility.createMenuItem(label, new AddMMSpellActionListener(), "add 1", (char) 0, accelerator, "Add Spell with Metamagic Feats", "Add16.gif", true);
			return addMMMenu;
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new DelSpellActionListener(), "remove 1", (char) 0, accelerator, "Remove Spell from Spellbook", "Remove16.gif", true);
		}

		private SpellPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				SpellPopupMenu.this.add(createAddMenuItem("Add  Spell to Spellbook", "shortcut EQUALS"));
				this.addSeparator();
				SpellPopupMenu.this.add(createAddMMMenuItem("Add Spell with Metamagic Feats", "alt C"));
			}
			else // selectedTable
			{
				SpellPopupMenu.this.add(createDelMenuItem("Remove Spell from Spellbook", "shortcut MINUS"));
			}
		}
	}

	private class SpellPopupListener extends MouseAdapter
	{
		private JTreeTable aTreeTable;
		private JTree tree;
		private SpellPopupMenu menu;

		private SpellPopupListener(JTreeTable treeTable, SpellPopupMenu aMenu)
		{
			aTreeTable = treeTable;
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
		treeTable.addMouseListener(new SpellPopupListener(treeTable, new SpellPopupMenu(treeTable)));
	}

	/**
	 *  Constructor for the InfoSpells object
	 *
	 */
	public InfoSpells()
	{
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName(Constants.tabNames[Constants.TAB_SPELLS]);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				initComponents();
				initActionListeners();
			}
		});

	}

	public final void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 **/
	private void initComponents()
	{
		//
		// View List Sanity check
		//
		int iView = SettingsHandler.getSpellsTab_AvailableListMode();
		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS) && (iView <= GuiConstants.INFOSPELLS_VIEW_TYPE))
		{
			primaryViewMode = iView;
		}
		while (secondaryViewMode == primaryViewMode)
		{
			if (secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR)
				secondaryViewMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
			else
				secondaryViewMode++;
		}
		SettingsHandler.setSpellsTab_AvailableListMode(primaryViewMode);
		iView = SettingsHandler.getSpellsTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOSPELLS_VIEW_CLASS) && (iView <= GuiConstants.INFOSPELLS_VIEW_TYPE))
		{
			primaryViewSelectMode = iView;
		}
		while (secondaryViewSelectMode == primaryViewSelectMode)
		{
			if (secondaryViewSelectMode == GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR)
				secondaryViewSelectMode = GuiConstants.INFOSPELLS_VIEW_CLASS;
			else
				secondaryViewSelectMode++;
		}
		SettingsHandler.setSpellsTab_SelectedListMode(primaryViewSelectMode);

		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		primaryViewComboBox.addItem("Class");
		primaryViewComboBox.addItem("Level");
		primaryViewComboBox.addItem("Descriptor");
		primaryViewComboBox.setSelectedIndex(primaryViewMode);
		Utility.setDescription(primaryViewComboBox, "You can change how the Spells in the Tables are listed.");
		secondaryViewComboBox.addItem("Class");
		secondaryViewComboBox.addItem("Level");
		secondaryViewComboBox.addItem("Descriptor");
		secondaryViewComboBox.setSelectedIndex(secondaryViewMode);
		primaryViewSelectComboBox.addItem("Class");
		primaryViewSelectComboBox.addItem("Level");
		primaryViewSelectComboBox.addItem("Descriptor");
		primaryViewSelectComboBox.setSelectedIndex(primaryViewSelectMode);
		Utility.setDescription(primaryViewSelectComboBox, "You can change how the Spells in the Tables are listed.");
		secondaryViewSelectComboBox.addItem("Class");
		secondaryViewSelectComboBox.addItem("Level");
		secondaryViewSelectComboBox.addItem("Descriptor");
		Utility.setDescription(secondaryViewSelectComboBox, "You can change how the Spells in the Tables are listed.");
		secondaryViewSelectComboBox.setSelectedIndex(secondaryViewSelectMode);

		bookList.add(Globals.getDefaultSpellBook());

		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addSpellButton = new JButton(newImage);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		delSpellButton = new JButton(newImage);

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

		//
		// first build the left pane
		// for the availabe spells table and info
		//

		Utility.buildConstraints(c, 0, 0, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.add(avaLabel);
		aPanel.add(primaryViewComboBox);
		aPanel.add(secondaryViewComboBox);
		Utility.setDescription(addSpellButton, "Click to add selected spell to your selected spellbook");
		addSpellButton.setEnabled(false);
		aPanel.add(addSpellButton);

		Utility.setDescription(aPanel, "Right click to add spells to your spellbooks");
		leftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);
		shouldAutoSpells.setSelected(aPC.getAutoSpells());
		bPanel.add(shouldAutoSpells);
		leftPane.add(bPanel);

		// the available spells panel
		Utility.buildConstraints(c, 0, 2, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		//
		// now build the right pane
		// for the selected (SpellBooks) table
		//

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		// Buttons above spellbooks and known spells
		Utility.buildConstraints(c, 0, 0, 1, 1, 2, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel iPanel = new JPanel();
		gridbag.setConstraints(iPanel, c);
		Utility.setDescription(delSpellButton, "Click to remove selected spell from this spellbook");
		delSpellButton.setEnabled(false);
		iPanel.add(delSpellButton);
		rightPane.add(iPanel);

		Utility.buildConstraints(c, 1, 0, 1, 1, 1, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		JPanel sPanel = new JPanel();
		gridbag.setConstraints(sPanel, c);
		sPanel.add(selLabel);
		sPanel.add(primaryViewSelectComboBox);
		sPanel.add(secondaryViewSelectComboBox);
		rightPane.add(sPanel);

		Utility.buildConstraints(c, 2, 0, 1, 1, 1, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		JLabel spellBookLabel = new JLabel("SpellBook:");
		aPanel.add(spellBookLabel);
		spellBookNameText.setEditable(true);
		spellBookNameText.setPreferredSize(new Dimension(100, 20));
		aPanel.add(spellBookNameText);
		addBookButton = new JButton("Add");
		aPanel.add(addBookButton);
		delBookButton = new JButton("Del");
		aPanel.add(delBookButton);
		rightPane.add(aPanel);

		selectSpellSheetField.setEditable(false);
		Utility.setDescription(selectSpellSheetField, "Display only");
		selectSpellSheetField.setBackground(Color.lightGray);
		selectSpellSheetField.setText(SettingsHandler.getSelectedSpellSheetName());

		JPanel ssPanel = new JPanel();
		Utility.buildConstraints(c, 0, 1, 3, 1, 1, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		gridbag.setConstraints(ssPanel, c);
		final PCGen_Frame1 pcFrame = PCGen_Frame1.getInst();
		printHtml = new JButton();
		//printHtml.addActionListener(pcFrame.getPrintPreviewListener());
		//printHtml.setActionCommand("file.printpreview");
		IconUtilitities.maybeSetIcon(printHtml, "PrintPreview16.gif");
		printHtml.setEnabled(true);

		printPdf = new JButton();
		//printPdf.addActionListener(pcFrame.getPrintListener());
		//printPdf.setActionCommand("file.print");
		IconUtilitities.maybeSetIcon(printPdf, "Print16.gif");
		printPdf.setEnabled(true);

		ssPanel.add(selectSpellSheetButton);
		ssPanel.add(selectSpellSheetField);
		ssPanel.add(printHtml);
		ssPanel.add(printPdf);
		rightPane.add(ssPanel);

		// List of spellbooks and known spells Panel
		Utility.buildConstraints(c, 0, 2, 3, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		selectedTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		selectedTable.setShowHorizontalLines(true);
		rightPane.add(scrollPane);

		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (spell info)
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

		// create a spell info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane sScroll = new JScrollPane();
		gridbag.setConstraints(sScroll, c);

		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Spell Info");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		sScroll.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		sScroll.setViewportView(infoLabel);
		bLeftPane.add(sScroll);

		// create a class info scroll area
		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		JScrollPane iScroll = new JScrollPane();

		TitledBorder iTitle = BorderFactory.createTitledBorder(etched, "Class Info");
		iTitle.setTitleJustification(TitledBorder.CENTER);
		iScroll.setBorder(iTitle);
		classLabel.setBackground(topPane.getBackground());
		iScroll.setViewportView(classLabel);
		iScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gridbag.setConstraints(iScroll, c);
		bRightPane.add(iScroll);


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

		// add the sorter tables so that clicking on the TableHeader
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
					SettingsHandler.setPCGenOption("InfoSpells.splitPane", s);
				}
				s = asplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoSpells.asplit", s);
				}
				s = bsplit.getDividerLocation();
				if (s > 0)
				{
					SettingsHandler.setPCGenOption("InfoSpells.bsplit", s);
				}
			}
		});
		shouldAutoSpells.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				aPC.setAutoSpells(shouldAutoSpells.isSelected());
			}
		});
		addSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addSpellButton();
			}
		});
		delSpellButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delSpellButton();
			}
		});
		spellBookNameText.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				spellBookNameTextActionPerformed();
			}
		});
		addBookButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addBookButton();
			}
		});
		delBookButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delBookButton();
			}
		});
		primaryViewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				primaryViewComboBoxActionPerformed();
			}
		});
		secondaryViewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				secondaryViewComboBoxActionPerformed();
			}
		});
		primaryViewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				primaryViewSelectComboBoxActionPerformed();
			}
		});
		secondaryViewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				secondaryViewSelectComboBoxActionPerformed();
			}
		});
		selectSpellSheetButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectSpellSheetButton();
			}
		});
		printHtml.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				boolean aBool = SettingsHandler.getPrintSpellsWithPC();
				SettingsHandler.setPrintSpellsWithPC(true);
				Utility.previewInBrowser(SettingsHandler.getSelectedSpellSheet());
				SettingsHandler.setPrintSpellsWithPC(aBool);
			}
		});
		printPdf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				boolean aBool = SettingsHandler.getPrintSpellsWithPC();
				PCGen_Frame1 pcFrame = PCGen_Frame1.getInst();
				SettingsHandler.setPrintSpellsWithPC(true);
				exportSpellsToFile();
				SettingsHandler.setPrintSpellsWithPC(aBool);
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	/*
	 * set the spell Info text in the Spell Info panel to the
	 * currently selected spell
	 */
	private void setInfoLabelText(SpellInfo si)
	{
		if (si == null)
		{
			return;
		}
		CharacterSpell cs = si.getOwner();
		lastSpell = cs.getSpell(); //even if that's null
		Spell aSpell = lastSpell;
		if (aSpell != null)
		{
			String typeName = "CLASS";
			if (cs.getOwner() instanceof Domain)
			{
				typeName = "DOMAIN";
			}
			StringBuffer b = new StringBuffer();
			b.append("<html><font size=+1><b>").append(aSpell.piSubString()).append("</b></font>");
			final String addString = si.toString(); // would add [featList]
			if (addString.length() > 0)
			{
				b.append(" &nbsp;").append(addString);
			}
			b.append(" &nbsp;<b>Level:</b>&nbsp; ");
			int[] levels = new int[0];
			if (cs.getOwner() != null)
				levels = aSpell.levelForKey(cs.getOwner().getSpellKey());
			for (int index = 0; index < levels.length; ++index)
			{
				if (index > 0)
				{
					b.append(',');
				}
				b.append(levels[index]);
			}
			b.append(" &nbsp;<b>School:</b>&nbsp; ").append(aSpell.getSchool());
			b.append(" &nbsp;<b>SubSchool:</b>&nbsp; ").append(aSpell.getSubschool());
			b.append(" &nbsp;<b>Descriptor:</b>&nbsp; ").append(aSpell.descriptor());
			b.append(" &nbsp;<b>Components:</b>&nbsp; ").append(aSpell.getComponentList());
			b.append(" &nbsp;<b>Casting Time:</b>&nbsp; ").append(aSpell.getCastingTime());
			b.append(" &nbsp;<b>Duration:</b>&nbsp; ").append(aPC.parseSpellString(aSpell.getDuration(), cs.getOwner()));
			b.append(" &nbsp;<b>Range:</b>&nbsp; ").append(aSpell.getRange());
			b.append(" &nbsp;<b>Target:</b>&nbsp; ").append(aPC.parseSpellString(aSpell.getTarget(), cs.getOwner()));
			b.append(" &nbsp;<b>Saving Throw:</b>&nbsp; ").append(aSpell.getSaveInfo());
			b.append(" &nbsp;<b>Spell Resistance:</b>&nbsp; ").append(aSpell.getSpellResistance());
			b.append(" &nbsp;<b>Description:</b>&nbsp; ").append(aPC.parseSpellString(aSpell.getDescription(), cs.getOwner()));
			String spellSource = aSpell.getSource();
			if (spellSource.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE:</b>&nbsp;").append(spellSource);
			}

			b.append("</html>");
			infoLabel.setText(b.toString());
		}
	}

	/**
	 * set the class info text in the Class Info panel
	 * to the currently selected Character Class
	 */
	private void setClassLabelText(PCClass aClass)
	{
		if (aClass != null)
		{
			lastClass = aClass.getName();

			StringBuffer b = new StringBuffer();
			b.append("<html><table border=1><tr><td><font size=-2><b>").append(aClass.piSubString()).append(" [").append(String.valueOf(aClass.getLevel() + (int) aPC.getTotalBonusTo("PCLEVEL", aClass.getName()))).append("]</b></font></td>");
			for (int i = 0; i <= 9; ++i)
			{
				b.append("<td><font size=-2><b><center>&nbsp;").append(i).append("&nbsp;</b></center></font></td>");
			}
			b.append("</tr>");
			b.append("<tr><td><font size=-1><b>Cast</b></font></td>");
			for (int i = 0; i <= 9; ++i)
			{
				b.append("<td><font size=-1><center>").append(getNumCast(aClass, i)).append("</center></font></td>");
			}
			if (aClass.hasKnownSpells())
			{
				b.append("<tr><td><font size=-1><b>Known</b></font></td>");
				for (int i = 0; i <= 9; ++i)
				{
					final int a = aClass.getKnownForLevel(aClass.getLevel(), i);
					final int bonus = aClass.getSpecialtyKnownForLevel(aClass.getLevel(), i);
					StringBuffer bString = new StringBuffer();
					if (bonus > 0)
					{
						bString.append('+').append(bonus);
					}

					b.append("<td><font size=-1><center>").append(a).append(bString).append("</center></font></td>");
				}
			}
			b.append("<tr><td><font size=-1><b>DC</b></font></td>");
			for (int i = 0; i <= 9; ++i)
			{
				b.append("<td><font size=-1><center>").append(getDC(aClass, i)).append("</center></font></td>");
			}
			b.append("</tr></table>");

			b.append("Spell Caster Type: <b>").append(aClass.getSpellType()).append("</b><br>");
			b.append("Primary Stat Bonus: <b>").append(aClass.getSpellBaseStat()).append("</b><br>");
			if (aClass.getSpecialtyListString().length() != 0)
			{
				b.append("School: <b>").append(aClass.getSpecialtyListString()).append("</b><br>");
			}
			if (aClass.getProhibitedString().length() != 0)
			{
				b.append("Prohibited School: <b>").append(aClass.getProhibitedString()).append("</b><br>");
			}

			String bString = aClass.getSource();
			if (bString.length() > 0)
			{
				b.append("<b>SOURCE</b>:").append(bString);
			}

			b.append("</html>");
			classLabel.setText(b.toString());
		}
	}

	private static String getNumCast(PCClass aClass, int level)
	{
		int cLevel = aClass.getLevel();
		String sbook = Globals.getDefaultSpellBook();
		final String cast = aClass.getCastForLevel(cLevel, level, sbook) + aClass.getBonusCastForLevelString(cLevel, level, sbook);
		return cast;
	}

	private static int getDC(PCClass aClass, int level)
	{
		Spell aSpell = new Spell();
		int DC = aSpell.getDCForPlayerCharacter(aPC, null, aClass, level);
		return DC;
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
		availableTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
					{
						return;
					}

					if (!atree.isSelectionEmpty())
					{
						TreePath avaCPath = atree.getSelectionPath();
						String className = "";
						if (primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS)
						{
							className = avaCPath.getPathComponent(1).toString();
						}
						else if ((secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL) && (avaCPath.getPathCount() > 2))
						{
							className = avaCPath.getPathComponent(2).toString();
						}
						else if (lastClass != null)
						{
							className = lastClass;
						}
						//className may have HTML encoding, so get rid of it
						className = Utility.stripHTML(className);
						PCClass aClass = aPC.getClassNamed(className);

						if (!className.equalsIgnoreCase(lastClass) && className.length() > 0 && aClass != null)
						{
							setClassLabelText(aClass);
						}
					}

					final Object temp = atree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastSpell = null;
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if (fNode.getItem() instanceof SpellInfo)
					{
						CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();
						if (spellA.getSpell() != null)
						{
							addSpellButton.setEnabled(true);
							addMenu.setEnabled(true);
							addMMMenu.setEnabled(true);
							setInfoLabelText((SpellInfo) fNode.getItem());
						}
					}
					else
					{
						addSpellButton.setEnabled(false);
						addMenu.setEnabled(false);
						addMMMenu.setEnabled(false);
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
						addSpellButton();
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
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JTree selectedTree = selectedTable.getTree();
		selectedTree.setRootVisible(false);
		selectedTree.setShowsRootHandles(true);
		selectedTree.setCellRenderer(new LabelTreeCellRenderer());

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
					TreePath selCPath = selectedTree.getSelectionPath();
					if (!selectedTree.isSelectionEmpty())
					{
						spellBookNameText.setText(selCPath.getPathComponent(1).toString());
						spellBookNameTextActionPerformed();
					}

					final Object temp = selectedTree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						lastSpell = null;
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if (fNode.getItem() instanceof SpellInfo)
					{
						CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();
						if (spellA.getSpell() != null)
						{
							delSpellButton.setEnabled(true);
							setInfoLabelText((SpellInfo) fNode.getItem());
						}
					}
				}
			}
		});

		MouseListener sml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final TreePath mlSelPath = selectedTree.getPathForLocation(e.getX(), e.getY());
				if (mlSelPath != null)
				{
					if (e.getClickCount() == 2)
					{
						delSpellButton();
					}
					else if ((e.getClickCount() == 1) && e.isControlDown())
					{
						if (selectedTree.isPathSelected(mlSelPath))
						{
							selectedTree.removeSelectionPath(mlSelPath);
						}
						else if (!selectedTree.isPathSelected(mlSelPath))
						{
							selectedTree.addSelectionPath(mlSelPath);
						}
					}
				}
			}
		};
		selectedTree.addMouseListener(sml);

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void primaryViewComboBoxActionPerformed()
	{
		final int index = primaryViewComboBox.getSelectedIndex();
		if (index == secondaryViewComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			return;
		}
		if (index != primaryViewMode)
		{
			primaryViewMode = index;
			SettingsHandler.setSpellsTab_AvailableListMode(primaryViewMode);
			updateAvailableModel();
		}
	}

	private void secondaryViewComboBoxActionPerformed()
	{
		final int index = secondaryViewComboBox.getSelectedIndex();
		if (index == primaryViewComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			return;
		}
		if (index != secondaryViewMode)
		{
			secondaryViewMode = index;
//			SettingsHandler.setSpellsTab_AvailableListMode(secondaryViewMode);
			updateAvailableModel();
		}
	}

	private void primaryViewSelectComboBoxActionPerformed()
	{
		final int index = primaryViewSelectComboBox.getSelectedIndex();
		if (index == secondaryViewSelectComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			return;
		}
		if (index != primaryViewSelectMode)
		{
			primaryViewSelectMode = index;
			SettingsHandler.setSpellsTab_SelectedListMode(primaryViewSelectMode);
			updateSelectedModel();
		}
	}

	private void secondaryViewSelectComboBoxActionPerformed()
	{
		final int index = secondaryViewSelectComboBox.getSelectedIndex();
		if (index == primaryViewSelectComboBox.getSelectedIndex())
		{
			// give error about not having same selection twice
			return;
		}
		if (index != secondaryViewSelectMode)
		{
			secondaryViewSelectMode = index;
//			SettingsHandler.setSpellsTab_SelectedListMode(secondaryViewSelectMode);
			updateSelectedModel();
		}
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("");

		updateCharacterInfo();

		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoSpells.splitPane", (int) (this.getSize().getWidth() * 2 / 10));
			t = SettingsHandler.getPCGenOption("InfoSpells.bsplit", (int) (this.getSize().getHeight() - 101));
			u = SettingsHandler.getPCGenOption("InfoSpells.asplit", (int) (this.getSize().getWidth() - 408));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "SpellSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("SpellAva", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "SpellAva", i));
			}
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoSpells.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoSpells.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoSpells.asplit", u);
		}

	}

	/**
	 * 	This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	private final void updateCharacterInfo()
	{
		lastClass = "";
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

		aPC.getSpellList();
		shouldAutoSpells.setSelected(aPC.getAutoSpells());

		updateAvailableModel();
		updateSelectedModel();

		createFeatList();
		classLabel.setText("");

		needsUpdate = false;
	}

	private void addSpellButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String bookName;

		if (selCPath == null)
		{
			bookName = spellBookNameText.getText();
		}
		else
		{
			bookName = selCPath.getPathComponent(1).toString();
		}

		if (bookName.length() <= 0)
		{
			GuiFacade.showMessageDialog(null, "First select a spellbook to add the spell to", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to select a spellbook
		}

		if (!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS) ||
			!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			GuiFacade.showMessageDialog(null, "Can only add spells if sorted by class and level", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to select class/level or level/class as sorters
		}

		currSpellBook = bookName;

		TreePath[] avaCPaths = availableTable.getTree().getSelectionPaths();
		for (int index = avaCPaths.length - 1; index >= 0; --index)
		{
			Object aComp = avaCPaths[index].getLastPathComponent();
			PObjectNode fNode = (PObjectNode) aComp;

			addSpellToTarget(fNode, bookName);
		}

		aPC.setDirty(true);

		// reset selected spellbook model
		updateSelectedModel();

	}

	/**
	 * adds spell contained in fNode to PC's spellbook named bookName
	 */
	private void addSpellToTarget(PObjectNode fNode, String bookName)
	{
		Spell aSpell;
		String className = "";
		int spLevel;

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			return;
		}

		CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();
		if (spellA.getOwner() instanceof Race)
		{
			return;
		}

		CharacterSpell cs = null;
		PCClass aClass;

		if (!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS) ||
			!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			GuiFacade.showMessageDialog(null, "Can only add spells if sorted by class and level", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to select class/level or level/class as sorters
		}
		spLevel = ((SpellInfo) fNode.getItem()).getActualLevel();
		if (spellA.getSpell() instanceof Spell)
		{
			aSpell = spellA.getSpell();
			while (fNode != availableModel.getRoot())
			{
				className = fNode.getItem().toString();
				aClass = aPC.getClassNamed(className);
				if (aClass != null)
				{
					List aList = aClass.getCharacterSpell(aSpell, bookName, spLevel);
					for (Iterator ai = aList.iterator(); ai.hasNext();)
					{
						cs = (CharacterSpell) ai.next();
						if (cs == spellA)
						{
							break;
						}
						if (!spellA.getOwner().equals(cs.getOwner()))
						{
							continue;
						}
						spellA = cs;
						break;
					}
					if (cs == null)
					{
						cs = new CharacterSpell(spellA.getOwner(), aSpell);
						cs.addInfo(spLevel, 1, bookName);
						spellA = cs;
					}
					else
					{
						aClass = null;
					}
					break;
				}
				fNode = fNode.getParent();
			}
			if (className.length() == 0)
			{
				className = spellA.getOwner().getName();
			}
		}
		else
		{
			return;
		}

		List featList = new ArrayList();
		final String aString = aPC.addSpell(spellA, featList, className, bookName, spLevel, spLevel);

		if (aString.length() > 0)
		{
			GuiFacade.showMessageDialog(null, aString, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 * memorize a spell with metamagic feats applied.
	 */
	private void metamagicButton()
	{
		TreePath avaCPath = availableTable.getTree().getSelectionPath();
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		String bookName;
		if (selCPath == null)
		{
			bookName = spellBookNameText.getText();
		}
		else
		{
			bookName = selCPath.getPathComponent(1).toString();
		}

		if (bookName.length() <= 0)
		{
			GuiFacade.showMessageDialog(null, "First select a spellbook to add the spell to", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to selected a spellbook
		}

		// no adding metamagic'ed spells to the default spellbook
		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			GuiFacade.showMessageDialog(null, "No memorized spells in " + bookName, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to selected a spellbook
		}

		if (!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_CLASS) ||
			!(primaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			GuiFacade.showMessageDialog(null, "Can only metamagic spells if sorted by class and level", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to select class/level or level/class as sorters
		}

		currSpellBook = bookName;

		String className = "";
		int spLevel;

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			return;
		}

		CharacterSpell spellA = ((SpellInfo) fNode.getItem()).getOwner();
		if (bookName.equals(Globals.getDefaultSpellBook()))
		{
			spellA = new CharacterSpell(spellA.getOwner(), spellA.getSpell());
		}
		if (spellA.getSpell() != null)
		{
			PCClass aClass;
			spLevel = ((SpellInfo) fNode.getItem()).getActualLevel();
			while (fNode != availableModel.getRoot())
			{
				className = fNode.getItem().toString();
				aClass = aPC.getClassNamed(className);
				if (aClass != null)
				{
					break;
				}
				fNode = fNode.getParent();
			}
			if (className.length() == 0)
			{
				className = spellA.getOwner().getName();
			}
		}
		else
		{
			return;
		}

		// make sure all the feats are set
		createFeatList();

		final List featList = featModel.getData();

		ChooserInterface c = ChooserFactory.getChooserInstance();
		c.setAvailableList(featList);
		c.setVisible(false);
		c.setPoolFlag(false);
		c.setAllowsDups(true);
		c.setTitle("Add Spell with Metamagic Feats");
		c.setMessageText("Select the Metamagic feats to memorize this spell with");
		c.setPool(99);
		c.show();

		final List fList = c.getSelectedList();
		List selFeatList = new ArrayList();
		int realLevel = spLevel;

		for (int i = 0; i < fList.size(); ++i)
		{
			Feat aFeat = aPC.getFeatNamed(fList.get(i).toString());
			realLevel += aFeat.getAddSpellLevel();
			selFeatList.add(aFeat);
		}

System.err.println("adding "+spellA.toString()+" with "+selFeatList.toString()+" to "+className+"/"+bookName+"/"+realLevel+"/"+spLevel);
		final String aString = aPC.addSpell(spellA, selFeatList, className, bookName, realLevel, spLevel);

		if (aString.length() > 0)
		{
			GuiFacade.showMessageDialog(null, aString, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		aPC.setDirty(true);

		updateSelectedModel();

		spellBookNameText.setText(bookName);

	}

	private void delSpellButton()
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			GuiFacade.showMessageDialog(null, "Select the spell to remove from your spellbook.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to selected a spellbook
		}

		if (!(primaryViewSelectMode == GuiConstants.INFOSPELLS_VIEW_CLASS || secondaryViewSelectMode == GuiConstants.INFOSPELLS_VIEW_CLASS) ||
			!(primaryViewSelectMode == GuiConstants.INFOSPELLS_VIEW_LEVEL || secondaryViewSelectMode == GuiConstants.INFOSPELLS_VIEW_LEVEL))
		{
			GuiFacade.showMessageDialog(null, "Can only add spells if sorted by class and level", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;		// need to select class/level or level/class as sorters
		}

		PCClass aClass = null;
		String className;
		int spLevel;
		String bookName;

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		if (!(fNode.getItem() instanceof SpellInfo))
		{
			return;
		}
		SpellInfo si = (SpellInfo) fNode.getItem();

		CharacterSpell spellA = si.getOwner();

		// if it's a race spell, we can't delete it
		if (spellA.getOwner() instanceof Race)
		{
			return;
		}

		if (spellA.getSpell() != null)
		{
			spLevel = ((SpellInfo) fNode.getItem()).getActualLevel();
			while (fNode != availableModel.getRoot())
			{
				className = fNode.getItem().toString();
				aClass = aPC.getClassNamed(className);
				if (aClass != null)
				{
					break;
				}
				fNode = fNode.getParent();
			}
		}
		else
		{
			return;
		}

		bookName = selCPath.getPathComponent(1).toString();
		currSpellBook = bookName;

		final String aString = PlayerCharacter.delSpell(si, aClass, bookName);

		if (aString.length() > 0)
		{
			GuiFacade.showMessageDialog(null, aString, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		aPC.setDirty(true);
		updateSelectedModel();

	}

	/**
	 * This is used when selecting a new spellbook
	 **/
	private void spellBookNameTextActionPerformed()
	{
		final String aString = spellBookNameText.getText();
		if ((aString == null) || aString.equals(currSpellBook))
		{
			return;
		}

		// if the user selects a new spellbook, we have to refresh
		// the available table because it could change the spells
		// known and memorizable
		currSpellBook = aString;
		spellBookNameText.setText(aString);
		if (!bookList.contains(aString))
		{

			bookList.add(aString);
		}

		updateAvailableModel();

	}

	/*****  **  **  **   **
	 **     **  **  ***  **
	 ***This is used to add new spellbooks when the
	 ***spellBookNameText JTextField is edited
	 **     **  **  **  ***
	 **     ******  **   **
	 */
	private void addBookButton()
	{
		final String aString = spellBookNameText.getText();
		if (aString.equals(currSpellBook))
		{
			return;
		}
		// added to prevent spellbooks being given the same name as a class
		for (Iterator i = Globals.getClassList().iterator(); i.hasNext();)
		{
			PCClass current = (PCClass) i.next();
			if ((aString.equals(current.getName())))
			{
				JOptionPane.showMessageDialog(null, PropertyFactory.getString("in_spellbook_name_error"), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
				spellBookNameText.setText("");
				return;
			}

		}

		if (aPC.addSpellBook(aString))
		{
			aPC.setDirty(true);
			spellBookNameText.setText(aString);
			spellBookNameTextActionPerformed();
			updateSelectedModel();

		}
		else
		{
			Logging.errorPrint("addBookButton:failed");
			return;
		}
	}

	private void delBookButton()
	{
		String aString = spellBookNameText.getText();
		if (aString.equalsIgnoreCase(Globals.getDefaultSpellBook()))
		{
			Logging.errorPrint("You may not delete the default spellbook");
			return;
		}
		if (aPC.delSpellBook(aString))
		{
			aPC.setDirty(true);
			currSpellBook = Globals.getDefaultSpellBook();

			updateAvailableModel();
			updateSelectedModel();
		}
		else
		{
			Logging.errorPrint("delBookButton:failed ");
			return;
		}

	}

	/**
	 *  Select a spell output sheet
	 **/
	private void selectSpellSheetButton()
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find and select your Spell output sheet");
		fc.setCurrentDirectory(SettingsHandler.getPcgenOutputSheetDir());
		fc.setSelectedFile(new File(SettingsHandler.getSelectedSpellSheet()));
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			SettingsHandler.setSelectedSpellSheet(fc.getSelectedFile().getAbsolutePath());
			selectSpellSheetField.setText(SettingsHandler.getSelectedSpellSheetName());
		}
	}

	/**
	 *
	 * add all metamagic feats to arrayList
	 *
	 **/
	private void createFeatList()
	{
		//Calculate the aggregate feat list
		aPC.aggregateFeatList();
		aPC.setAggregateFeatsStable(true);
		aPC.setAutomaticFeatsStable(true);
		aPC.setVirtualFeatsStable(true);

		// get the list of metamagic feats for the PC
		List featList = new ArrayList();
		List feats = aPC.aggregateFeatList();
		Globals.sortPObjectList(feats);
		for (Iterator i = feats.iterator(); i.hasNext();)
		{
			Feat aFeat = (Feat) i.next();
			if (aFeat.isType("Metamagic"))
			{
				featList.add(aFeat.getName());
			}
		}
		featModel.setData(featList);
	}

	/**
	 *  Model for the JTable containing the spell feats.
	 *
	 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
	 * created    den 11 maj 2001
	 */
	private static final class SpellFeatModel extends AbstractTableModel
	{
		static final long serialVersionUID = 755097384157285101L;
		/**
		 *  Contains the feats
		 *
		 * @since
		 */
		private List data = new ArrayList();
		private final String[] nameList = {"Metamagic Feats"};

		/**
		 *  Sets the Data attribute of the SpellFeatModel object
		 *
		 * @param  aArrayList  The new Data value
		 * @since
		 */
		private void setData(List aArrayList)
		{
			data = aArrayList;
		}

		/**
		 *  gets the ArrayList data
		 *
		 */
		private List getData()
		{
			return data;
		}

		/**
		 *  Gets the ColumnCount attribute of the SpellFeatModel object
		 *
		 * @return    The ColumnCount value
		 * @since
		 */
		public int getColumnCount()
		{
			return nameList.length;
		}

		/**
		 *
		 */
		public Class getColumnClass(int c)
		{
			return String.class;
		}

		/**
		 *  Gets the RowCount attribute of the SpellFeatModel object
		 *
		 * @return    The RowCount value
		 * @since
		 */
		public int getRowCount()
		{
			return data.size();
		}

		/**
		 *  Gets the ColumnName attribute of the SpellFeatModel object
		 *
		 * @param  col  Description of Parameter
		 * @return      The ColumnName value
		 * @since
		 */
		public String getColumnName(int col)
		{
			return nameList[col];
		}

		/**
		 *  Gets the ValueAt attribute of the SpellFeatModel object
		 *
		 * @param  row  Description of Parameter
		 * @param  col  Description of Parameter
		 * @return      The ValueAt value
		 * @since
		 */
		public Object getValueAt(int row, int col)
		{
			return data.get(row);
		}
	}

	private void setSelectedIndex(JTreeTable aTable, int idx)
	{
		aTable.setRowSelectionInterval(idx, idx);
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

		TreePath modelSelPath = selectedTable.getTree().getSelectionPath();
		int idx = selectedTable.getTree().getRowForPath(modelSelPath);

		createSelectedModel();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);

		selectedTable.getTree().setSelectionPath(modelSelPath);
		selectedTable.getTree().expandPath(modelSelPath);
		int count = selectedTable.getTree().getRowCount();
		if ((idx >= 0) && (idx < count))
		{
			setSelectedIndex(selectedTable, idx);
		}
	}

	/**
	 * Creates the SpellModel that will be used.
	 **/
	private final void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private final void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new SpellModel(primaryViewMode, secondaryViewMode, true);
		}
		else
		{
			availableModel.resetModel(primaryViewMode, secondaryViewMode, true);
		}
		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
		}
	}

	private final void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new SpellModel(primaryViewSelectMode, secondaryViewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(primaryViewSelectMode, secondaryViewSelectMode, false);
		}
		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
		}
	}

	/**
	 *  The TreeTableModel has a single <code>root</code> node
	 *  This root node has a null <code>parent</code>.
	 *  All other nodes have a parent which points to a non-null node.
	 *  Parent nodes contain a list of  <code>children</code>, which
	 *  are all the nodes that point to it as their parent.
	 *  <code>nodes</code> which have 0 children are leafs (the end of
	 *  that linked list).  nodes which have at least 1 child are not leafs
	 *  Leafs are like files and non-leafs are like directories.
	 *  The leafs contain an Object that we want to know about (Spells)
	 **/
	private final class SpellModel extends AbstractTreeTableModel
	{
		// there are two roots. One for available spells
		// and one for selected spells (spellbooks)
		private PObjectNode theRoot;

		// list of columns names
		private String[] availNameList = {""};
		private String[] selNameList = {""};

		// Types of the columns.
		private int modelType = MODEL_AVAIL;

		/**
		 * Creates a SpellModel
		 */
		private SpellModel(int primaryMode, int secondaryMode, boolean available)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			availNameList = new String[]{"Name", "School", "Descriptor", "Source"};
			selNameList = new String[]{"Name", "School", "SubSchool", "Descriptor", "Components", "Casting Time", "Range", "Description", "Target Area", "Duration", "Save Info", "SR", "Source File"};

			if (!available)
			{
				modelType = MODEL_SELECTED;
			}
			resetModel(primaryMode, secondaryMode, available);
		}

		/**
		 * This assumes the SpellModel exists but
		 * needs branches and nodes to be repopulated
		 */
		private void resetModel(int primaryMode, int secondaryMode, boolean available)
		{
			List classList = new ArrayList();
			List spellList = new ArrayList();

			bookList.clear();
			PObjectNode [] primaryNodes = null;
			PObjectNode [] secondaryNodes = null;
			PObjectNode [] bookNodes = null;

			theRoot = new PObjectNode();
			setRoot(theRoot);

			// build list of spellbooks the PC already has
			if (!available) // !available = selected list of spells (right-side of tab)
			{
				for (Iterator iBook = aPC.getSpellBooks().iterator(); iBook.hasNext();)
				{
					// build spell book list
					String sBook = (String) iBook.next();
					if (!bookList.contains(sBook))
					{
						bookList.add(sBook);
					}
				}
				bookNodes = new PObjectNode [bookList.size()];
				int ix = 0;
				for (Iterator iBook = bookList.iterator(); iBook.hasNext();)
				{
					bookNodes[ix] = new PObjectNode();
					bookNodes[ix].setItem((String)iBook.next());
					bookNodes[ix++].setParent(theRoot);
				}
				theRoot.setChildren(bookNodes);
			}


			// get the list of spell casting Classes
			for (Iterator iClass = aPC.getClassList().iterator(); iClass.hasNext();)
			{
				PCClass aClass = (PCClass) iClass.next();
				if (!aClass.getSpellType().equals(Constants.s_NONE))
				{
					if (aClass.zeroCastSpells() && aClass.getKnownList().isEmpty())
					{
						continue;
					}

					classList.add(aClass);
					if (available && currSpellBook.equals(Globals.getDefaultSpellBook()))
					{
						aClass = Globals.getClassNamed(aClass.getCastAs());
						spellList.addAll(Globals.getSpellsIn(-1, aClass.getSpellKey(), ""));
					}
					else if (available)
					{
						final List aList = aClass.getCharacterSpell(null, Globals.getDefaultSpellBook(), -1);
						if (aList != null)
						{
							for (Iterator si = aList.iterator(); si.hasNext();)
							{
								Object s = si.next();
								if (!spellList.contains(s))
									spellList.addAll(aList);
							}
						}
					}
					else
					{
						for (Iterator bi = bookList.iterator(); bi.hasNext();)
						{
							final String bookName = (String)bi.next();
							final List aList = aClass.getCharacterSpell(null, bookName, -1);
							if (aList != null)
							{
								for (Iterator si = aList.iterator(); si.hasNext();)
								{
									Object s = si.next();
									if (!spellList.contains(s))
										spellList.add(s);
								}
							}
						}
					}
				}
			}





			// the structure will be
			// root
			//   (book names) bookNodes (only for right-side tab, the !available e.g. selected spells)
			//     primary nodes  (the first "sort by" selection)
			//       secondary nodes (the second "sort by" selection)
			// the first time (e.g. firstPass==true) through the loop, make sure all nodes are created and attached
			boolean firstPass = true;
			for (Iterator spelli = spellList.iterator(); spelli.hasNext();)
			{
				Object sp = spelli.next();
				Spell spell = null;
				CharacterSpell cs = null;
				if (sp instanceof CharacterSpell)
				{
					cs = (CharacterSpell)sp;
					spell = cs.getSpell();
				}
				if (sp instanceof Spell)
				{
					spell = (Spell)sp;
				}
					
				// for each spellbook, ignored for "available" left-side of tab
				// the <= bookList.size() is intended, so it will be processed once
				// when ix==0 for the !available (selected) model
				for (int ix = 0; ix <= bookList.size(); ix++)
				{
					if (!available && ix == bookList.size())
						break;
					String bookName = currSpellBook; // default to currSpellBook, the currently selected spellbook name
					if (!available)
						bookName = bookList.get(ix).toString();
					if (firstPass)
					{
						switch (primaryMode)
						{
							case GuiConstants.INFOSPELLS_VIEW_CLASS:     	// By Class
								primaryNodes = getClassNameNodes(classList);
							break;
							case GuiConstants.INFOSPELLS_VIEW_LEVEL:     	// By Level
								primaryNodes = getLevelNodes();
							break;
							case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR:   // By Descriptor
								primaryNodes = getDescriptorNodes();
							break;
						}
					}
					else if (!available)
					{
						// get the primaryNodes, which are the specified bookNode's children
						bookNodes[ix].getChildren().toArray(primaryNodes);
					}
					else
						// get the primaryNodes, which are the root's children
						theRoot.getChildren().toArray(primaryNodes);
					for (int pindex = 0 ; pindex < primaryNodes.length; pindex++)
					{
						boolean primaryMatch = false; // spell match's primaryNode's criteria
						boolean spellMatch = false; // spell match's primaryNode and secondaryNode criteria
						SpellInfo si = null;
						PCClass aClass = null;
						int iLev = -1;

						switch (primaryMode)
						{
							case GuiConstants.INFOSPELLS_VIEW_CLASS:     	// By Class
								aClass = (PCClass)classList.get(pindex);
								primaryMatch = (spell.getFirstLevelForKey(aClass.getSpellKey()) >= 0);
							break;
							case GuiConstants.INFOSPELLS_VIEW_LEVEL:     	// By Level
								iLev = pindex;
								primaryMatch = true;
								si = null;
								if (primaryMatch && cs != null)
								{
									si = cs.getSpellInfoFor(bookName, iLev, -1);
								}
								if (si == null)
									primaryMatch = spell.isLevel(iLev);
							
							break;
							case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR:   // By Descriptor
								primaryMatch = spell.getDescriptorList().contains(primaryNodes[pindex].toString());
							break;
						}
						if (firstPass)
						{
							switch (secondaryMode)
							{
								case GuiConstants.INFOSPELLS_VIEW_CLASS:     	// By Class
									secondaryNodes = getClassNameNodes(classList);
								break;
								case GuiConstants.INFOSPELLS_VIEW_LEVEL:     	// By Level
									secondaryNodes = getLevelNodes();
								break;
								case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR:   // By Descriptor
									secondaryNodes = getDescriptorNodes();
								break;
							}
						}
						else
						{
							 if (!primaryMatch)
								continue;
							primaryNodes[pindex].getChildren().toArray(secondaryNodes);
						}
						for (int sindex = 0 ; sindex < secondaryNodes.length; sindex++)
						{
							switch (secondaryMode)
							{
								case GuiConstants.INFOSPELLS_VIEW_CLASS:     	// By Class
									aClass = (PCClass)classList.get(sindex);
									spellMatch = primaryMatch && (spell.getFirstLevelForKey(aClass.getSpellKey()) >= 0);
								break;
								case GuiConstants.INFOSPELLS_VIEW_LEVEL:     	// By Level
									iLev = sindex;
									spellMatch = primaryMatch;
									si = null;
									if (spellMatch && cs != null)
									{
										si = cs.getSpellInfoFor(bookName, iLev, -1);
									}
									if (si == null && primaryMatch)
										spellMatch = spell.isLevel(iLev);
								break;
								case GuiConstants.INFOSPELLS_VIEW_DESCRIPTOR:   // By Descriptor
									spellMatch = primaryMatch && spell.getDescriptorList().contains(secondaryNodes[sindex].toString());
								break;
							}
							if (firstPass)
							{
								secondaryNodes[sindex].setParent(primaryNodes[pindex]);
								if (available && aClass != null && iLev > -1)
								{
									addDomainSpellsForClass(aClass.getCastAs(), secondaryNodes[sindex], iLev);
								}
							}
							if (spellMatch && si == null && available)
							{
								cs = new CharacterSpell(aClass, spell);
								si = cs.addInfo(iLev, 1, bookName);
							}
							// didn't find a match, so continue
							if (!spellMatch || si == null)
							{
								continue;
							}
							PObjectNode spellNode = new PObjectNode();
							spellNode.setItem(si);
							spellNode.setParent(secondaryNodes[sindex]);
							secondaryNodes[sindex].addChild(spellNode);
						}
						primaryNodes[pindex].setChildren(secondaryNodes);
						if (available)
							primaryNodes[pindex].setParent(theRoot);
						else
							primaryNodes[pindex].setParent(bookNodes[ix]);
					} // end primaryNodes
					if (!available)
						bookNodes[ix].setChildren(primaryNodes);
					else
						theRoot.setChildren(primaryNodes);
				} // end bookNodes
				firstPass = false;
			} // end spell list

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
			if (currSpellBook.equals(""))
			{
				currSpellBook = Globals.getDefaultSpellBook();
			}
			spellBookNameText.setText(currSpellBook);
		}

		private PObjectNode [] getClassNameNodes(List classList)
		{
			PObjectNode [] tempNodes = new PObjectNode[classList.size()];
			for (int ix = 0; ix < classList.size(); ++ix)
			{
				PCClass aClass = (PCClass) classList.get(ix);
				aClass.getSpellKey();
				String className = aClass.piString();
				tempNodes[ix] = new PObjectNode();
				tempNodes[ix].setItem(className);
			}
			return tempNodes;
		}

		private PObjectNode [] getLevelNodes()
		{
			PObjectNode [] tempNodes = new PObjectNode[20];
			for (int ix = 0; ix < 20; ++ix)
			{
				tempNodes[ix] = new PObjectNode();
				tempNodes[ix].setItem("level "+ix);
			}
			return tempNodes;
		}

		private PObjectNode [] getDescriptorNodes()
		{
			PObjectNode [] tempNodes = new PObjectNode[Globals.getDescriptorSet().size()];
			int ix = 0;
			for (Iterator ti = Globals.getDescriptorSet().iterator(); ti.hasNext(); )
			{
				tempNodes[ix] = new PObjectNode();
				tempNodes[ix++].setItem((String)ti.next());
			}
			return tempNodes;
		}

		/**
		 * this method looks for any domains associated with this class
		 * and finds any spells associated with this domain
		 * and then adds this list to a "Domains" directory
		 * to keep the Domain list in an easily distinguished place
		 **/
		private void addDomainSpellsForClass(String className, PObjectNode theParent, int iLev)
		{
			int iMax = aPC.getCharacterDomainList().size();
			if (iMax == 0)
			{
				return;
			}
			PObjectNode p = new PObjectNode();
			p.setItem("Domains");
			boolean dom = false;
			for (int iDom = 0; iDom < aPC.getCharacterDomainList().size(); ++iDom)
			{
				CharacterDomain aCD = (CharacterDomain) aPC.getCharacterDomainList().get(iDom);
				Domain aDom = aCD.getDomain();
				// if any domains have this class as a source
				// and is a valid domain, add them
				if ((aDom != null) && aCD.isFromPCClass(className))
				{
					List domainSpells = Globals.getSpellsIn(iLev, "", aDom.getName());
					p.setParent(theParent);
					if (!dom)
					{
						theParent.addChild(p);
					}
					dom = true;
					setNodeSpells(domainSpells, p, iLev, aDom, Globals.getDefaultSpellBook());
				}
			}
		}

		/**
		 * There must be a root node, but we keep it hidden
		 * @param aNode
		 */
		private void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		/**
		 * return the root node
		 * @return
		 */
		public Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns. (SpellModel)
		 **/
		public int getColumnCount()
		{
			return modelType == MODEL_AVAIL ? availNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column. (SpellModel)
		 **/
		public String getColumnName(int column)
		{
			return modelType == MODEL_AVAIL ? availNameList[column] : selNameList[column];
		}

		/**
		 * Returns Class for the column. (SpellModel)
		 **/
		public Class getColumnClass(int column)
		{
			return column == COL_NAME ? TreeTableModel.class : String.class;
		}

		/**
		 * Returns boolean if can edit a cell. (SpellModel)
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * changes the column order sequence and/or number of
		 * columns based on modelType (0=available, 1=selected)
		 **/
		private int adjustAvailColumnConst(int column)
		{

			if (modelType == MODEL_AVAIL)
			{
				if (column == COL_SUBSCHOOL)
				{
					column = COL_DESCRIPTOR;
				}
				else if (column >= COL_DESCRIPTOR)
				{
					column = COL_SRC;
				}
			}

			return column;
		}

		/**
		 * Returns Object value of the column. (SpellModel)
		 **/
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;
			Spell aSpell = null;
			CharacterSpell spellA = null;
			int spLevel = 0;

			if (fn == null)
			{
				Logging.errorPrint("Somehow we have no active node when doing getValueAt in InfoSpells.");
				return null;
			}

			if (fn.getItem() instanceof SpellInfo)
			{
				spellA = ((SpellInfo) fn.getItem()).getOwner();
				aSpell = spellA.getSpell();
				spLevel = ((SpellInfo) fn.getItem()).getActualLevel();
			}
			column = adjustAvailColumnConst(column);

			switch (column)
			{
				case COL_NAME:
					return fn.toString();
				case COL_SCHOOL:
					return aSpell != null ? aSpell.getSchool() : null;
				case COL_SUBSCHOOL:
					return aSpell != null ? aSpell.getSubschool() : null;
				case COL_DESCRIPTOR:
					return aSpell != null ? aSpell.descriptor() : null;
				case COL_COMPONENT:
					return aSpell != null ? aSpell.getComponentList() : null;
				case COL_CASTTIME:
					return aSpell != null ? aSpell.getCastingTime() : null;
				case COL_RANGE:
					return aSpell != null ? aSpell.getRange() : null;
				case COL_DESCRIPTION:
					if ((aSpell != null) && (spellA != null))
					{
						return aPC.parseSpellString(aSpell.getDescription(), spellA.getOwner());
					}
					return aSpell != null ? aSpell.piDescString() : null;
				case COL_TARGET:
					if ((aSpell != null) && (spellA != null))
					{
						return aPC.parseSpellString(aSpell.getTarget(), spellA.getOwner());
					}
					return aSpell != null ? aSpell.getTarget() : null;
				case COL_DURATION:
					if ((aSpell != null) && (spellA != null))
					{
						return aPC.parseSpellString(aSpell.getDuration(), spellA.getOwner());
					}
					return aSpell != null ? aSpell.getDuration() : null;
				case COL_SAVE:
					return aSpell != null ? aSpell.getSaveInfo() : null;
				case COL_SR:
					return aSpell != null ? aSpell.getSpellResistance() : null;
				case COL_SRC:
					return aSpell != null ? aSpell.getSource() : null;
				default:
					return fn.getItem();
			}
			// return null;
		}

	}

	/**
	 * This function takes a branch and adds the spells to it.
	 * @param charSpells
	 * @param tNode
	 * @param iLev
	 * @param obj
	 * @param book
	 */
	private static void setNodeSpells(List charSpells, PObjectNode tNode, int iLev, PObject obj, String book)
	{
		for (Iterator fI = charSpells.iterator(); fI.hasNext();)
		{
			Object o = fI.next();
			PObjectNode fCN;
			if (o instanceof CharacterSpell)
			{
				final CharacterSpell cs = (CharacterSpell) o;
				final SpellInfo si = cs.getSpellInfoFor(book, iLev, -1);
				if (si == null)
				{
					continue;
				}
				fCN = new PObjectNode();
				fCN.setItem(si);
			}
			else
			{
				Spell aSpell = (Spell) o;
				if (!aSpell.levelForKeyContains(obj.getSpellKey(), iLev))
				{
					continue;
				}
				CharacterSpell cs = new CharacterSpell(obj, aSpell);
				SpellInfo si = cs.addInfo(iLev, 1, book);
				fCN = new PObjectNode();
				fCN.setItem(si);
			}
			fCN.setParent(tNode);
			tNode.addChild(fCN);
		} // end spells loop
	}

	/**
	 * implementation of Filterable interface
	 **/
	public final void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSpellFilters(this);

		setKitFilter("SPELL");
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
		return FilterConstants.DISABLED_MODE;
	}

	/**
	 * Exports Spell through selected output sheet to a file
	 **/
	private void exportSpellsToFile()
	{
		final String template = SettingsHandler.getSelectedSpellSheet();
		String ext = template.substring(template.lastIndexOf('.'));

		JFileChooser fcExport = new JFileChooser();
		fcExport.setCurrentDirectory(SettingsHandler.getPcgPath());

		fcExport.setDialogTitle("Export Spells for " + aPC.getDisplayName());

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
				Utility.printToWriter(w, template);
			}
			else if (ext.equalsIgnoreCase(".fo") || ext.equalsIgnoreCase(".pdf"))
			{
				File tmpFile = File.createTempFile("tempSpells_", ".fo");
				BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), "UTF-8"));
				Utility.printToWriter(w, template);

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

}

