/*
 * InfoFeat.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied waarranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on December 29, 2001, 6:57 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:27 $
 *
 */

package pcgen.gui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
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
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.PrereqHandler;
import pcgen.gui.GuiConstants;
import pcgen.gui.PCGen_Frame1;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
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
import pcgen.util.Logging;

/**
 * <code>InfoFeats</code>.
 * This class is responsible for drawing the feat related window - including
 * indicating what feats are available, which ones are selected, and handling
 * the selection/de-selection of feats
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public final class InfoFeats extends FilterAdapterPanel
{
	private static final String NO_QUALIFY_MESSAGE = "You do not meet the prerequisites required to take this feat.";
	private static final String DUPLICATE_MESSAGE = "You already have that feat.";
	private static final String FEAT_FULL_MESSAGE = "You cannot select any more feats.";

	private FeatModel availableModel = null; // Model for the JTreeTable.
	private FeatModel selectedModel = null;  // Model for the JTreeTable.
	private JTreeTable availableTable;	 // the available Feats
	private JTreeTable selectedTable;	 // the selected Feats
	private JTextField numFeatsField = new JTextField();
	private JComboBoxEx viewAvailComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private static PlayerCharacter aPC = null;
	private JButton leftButton;
	private JButton addButton;
	private JLabelPane infoLabel = new JLabelPane();
	private JScrollPane infoScroll = new JScrollPane();
	private static boolean needsUpdate = true;
	private static PObjectNode typeRoot = new PObjectNode();
	private static PObjectNode preReqTreeRoot = null;
	private FlippingSplitPane splitTopLeftRight;
	private FlippingSplitPane splitTopBot;
	private FlippingSplitPane splitBotLeftRight;
	private JPanel topPane = new JPanel();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;
	private boolean hasBeenSized = false;
	private Border etched;
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	// keep track of what view mode we're in for Available
	private static int viewAvailMode = GuiConstants.INFOFEATS_VIEW_TYPENAME;
	// default to "Name" in Selection table viewmode
	private static int viewSelectMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;

	private static final int HASFEAT_NO = 0;
	private static final int HASFEAT_CHOSEN = 1;
	private static final int HASFEAT_AUTOMATIC = 2;
	private static final int HASFEAT_VIRTUAL = 4;

	private JMenuItem addMenu;
	private JMenuItem removeMenu;
	private TreePath selPath;

	static final int FEAT_OK = 0;
	static final int FEAT_DUPLICATE = 1;
	static final int FEAT_NOT_QUALIFIED = 2;
	static final int FEAT_FULL_FEAT = 3;

	private int checkFeatQualify(Feat aFeat)
	{
		String aString = aFeat.getName();
		aFeat = aPC.getFeatNonAggregateNamed(aString);
		final boolean pcHasIt = (aFeat != null);

		if (pcHasIt && !aFeat.isMultiples())
		{
			return FEAT_DUPLICATE;
		}

		if (!pcHasIt)
		{
			aFeat = Globals.getFeatNamed(aString);
			if ((aFeat != null) && !aFeat.passesPreReqToGain())
			{
				return FEAT_NOT_QUALIFIED;
			}
		}

		if ((aFeat != null) && (aFeat.getCost(aPC) > aPC.getFeats()) && !pcHasIt)
		{
			return FEAT_FULL_FEAT;
		}

		return FEAT_OK;
	}

	private class FeatPopupMenu extends JPopupMenu
	{
		private JTreeTable treeTable;
		private JMenuItem noQualifyMenuItem, duplicateMenuItem, featFullMenuItem;

		private FeatPopupMenu(JTreeTable treeTable)
		{
			if ((this.treeTable = treeTable) == availableTable)
			{
				FeatPopupMenu.this.add(addMenu = Utility.createMenuItem("Add Feat", new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						addFeat();
					}
				}, "infoFeats.addFeat", (char) 0, "shortcut EQUALS", "Add Feat", "Add16.gif", true));

				// Build menus now since expert settings
				// could get changed while we are running
				noQualifyMenuItem = Utility.createMenuItem(NO_QUALIFY_MESSAGE, null, null, (char) 0, null, null, null, false);
				duplicateMenuItem = Utility.createMenuItem(DUPLICATE_MESSAGE, null, null, (char) 0, null, null, null, false);
				featFullMenuItem = Utility.createMenuItem(FEAT_FULL_MESSAGE, null, null, (char) 0, null, null, null, false);
			}
			else // selectedTable
			{
				FeatPopupMenu.this.add(removeMenu = Utility.createMenuItem("Remove Feat", new ActionListener()
				{
					public void actionPerformed(ActionEvent evt)
					{
						removeFeat();
					}
				}, "infoFeats.removeFeat", (char) 0, "shortcut MINUS", "Remove Feat", "Remove16.gif", true));
			}
		}

		public void show(Component source, int x, int y)
		{
			Feat aFeat = (Feat) ((PObjectNode) treeTable.getTree().getLastSelectedPathComponent()).getItem();

			if (treeTable == availableTable)
			{
				int ok = checkFeatQualify(aFeat);

				if (ok == FEAT_OK)
				{
					FeatPopupMenu.this.removeAll();
					FeatPopupMenu.this.add(addMenu);
					super.show(source, x, y);
					return;
				}

				if (SettingsHandler.isExpertGUI())
				{
					return;
				}

				switch (ok)
				{
					case InfoFeats.FEAT_NOT_QUALIFIED:
						FeatPopupMenu.this.removeAll();
						FeatPopupMenu.this.add(noQualifyMenuItem);
						super.show(source, x, y);
						return;
					case InfoFeats.FEAT_DUPLICATE:
						FeatPopupMenu.this.removeAll();
						FeatPopupMenu.this.add(duplicateMenuItem);
						super.show(source, x, y);
						return;
					case InfoFeats.FEAT_FULL_FEAT:
						FeatPopupMenu.this.removeAll();
						FeatPopupMenu.this.add(featFullMenuItem);
						super.show(source, x, y);
						return;
					default:
						Logging.errorPrint("Feat " + aFeat.getName() + " is somehow in state " + ok + " which is not handled" +
							" in InfoFeats.FeatPopupMenu.show()");
						break;
				}
			}
			else
			{
				super.show(source, x, y);
			}
		}
	}

	private class FeatPopupListener extends MouseAdapter
	{
		private JTreeTable aTreeTable;
		private JTree tree;
		private FeatPopupMenu menu;

		private FeatPopupListener(JTreeTable treeTable, FeatPopupMenu aMenu)
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

				// Walk through the list of accelerators
				// to see if the user has pressed a sequence
				// used by the popup. This would not
				// happen unless the popup was showing
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
		treeTable.addMouseListener(new FeatPopupListener(treeTable, new FeatPopupMenu(treeTable)));
	}

	/**
	 * Translate internal feat type to Feat-defined type
	 * TODO Method unused.
	 */
	private static int xlatInternalType(int featType)
	{
		int iRetVal;
		switch (featType)
		{
			case HASFEAT_CHOSEN:
				iRetVal = Feat.FEAT_NORMAL;
				break;
			case HASFEAT_AUTOMATIC:
				iRetVal = Feat.FEAT_AUTOMATIC;
				break;
			case HASFEAT_VIRTUAL:
				iRetVal = Feat.FEAT_VIRTUAL;
				break;
			default:
				iRetVal = -1;
				break;
		}
		return iRetVal;
	}

	public static void forceUpdate()
	{
		needsUpdate = true;
		PObjectNode.resetPC();
	}

	public InfoFeats()
	{
		// we will use the name to save component
		// specific settings in the options.ini file
		setName(Constants.tabNames[Constants.TAB_FEATS]);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				aPC = Globals.getCurrentPC();
				initComponents();
				initActionListeners();
			}
		});

	}

	public static void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	private void initComponents()
	{
		//
		// Sanity check
		//
		int iView = SettingsHandler.getFeatTab_AvailableListMode();
		if ((iView >= GuiConstants.INFOFEATS_VIEW_TYPENAME) &&
			(iView <= GuiConstants.INFOFEATS_VIEW_PREREQTREE))
		{
			viewAvailMode = iView;
		}
		SettingsHandler.setFeatTab_AvailableListMode(viewAvailMode);
		iView = SettingsHandler.getFeatTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOFEATS_VIEW_TYPENAME) &&
			(iView <= GuiConstants.INFOFEATS_VIEW_PREREQTREE))
		{
			viewSelectMode = iView;
		}
		SettingsHandler.setFeatTab_SelectedListMode(viewSelectMode);

		viewAvailComboBox.addItem("Type/Name");
		viewAvailComboBox.addItem("Name");
		viewAvailComboBox.addItem("Pre-Req Tree");
		Utility.setDescription(viewAvailComboBox, "You can change how the feats in the Available and Selected Tables are listed - either by name or in a directory-like structure.");
		viewAvailComboBox.setSelectedIndex(viewAvailMode);

		viewSelectComboBox.addItem("Type/Name");
		viewSelectComboBox.addItem("Name");
		viewSelectComboBox.addItem("Pre-Req Tree");
		Utility.setDescription(viewSelectComboBox, "You can change how the feats in the Selected Tables are listed - either by name or in a directory-like structure.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);
		List aList = new ArrayList();
		for (int i = 0; i < Globals.getFeatList().size(); ++i)
		{
			final Feat bFeat = Globals.getFeatListFeat(i);
			if (!((bFeat.isVisible() == Feat.VISIBILITY_DEFAULT) ||
				(bFeat.isVisible() == Feat.VISIBILITY_DISPLAY_ONLY)))
			{
				continue;
			}
			final StringTokenizer aTok =
				new StringTokenizer(bFeat.getType(), ".");
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (!aList.contains(aString))
				{
					aList.add(aString);
				}
			}
		}
		Collections.sort(aList);
		PObjectNode[] cc = new PObjectNode[aList.size()];
		for (int i = 0; i < aList.size(); ++i)
		{
			cc[i] = new PObjectNode();
			cc[i].setItem(aList.get(i).toString());
			cc[i].setParent(typeRoot);
		}
		typeRoot.setChildren(cc);

		// initialize Models
		createModels();

		// create available table of feats
		createTreeTables();

		topPane.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		//-------------------------------------------------------------
		// Top Pane - Left Available, Right Selected
		//
		JPanel tLeftPane = new JPanel();
		JPanel tRightPane = new JPanel();

		splitTopLeftRight =
			new FlippingSplitPane(splitOrientation, tLeftPane, tRightPane);
		splitTopLeftRight.setOneTouchExpandable(true);
		splitTopLeftRight.setDividerSize(10);
		// splitTopLeftRight.setDividerLocation(350);

		topPane.add(splitTopLeftRight, BorderLayout.CENTER);

		// Top Left - Available
		tLeftPane.setLayout(gridbag);
		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		JLabel avaLabel = new JLabel("Available: ");
		aPanel.add(avaLabel);
		aPanel.add(viewAvailComboBox);
		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		addButton = new JButton(newImage);
		Utility.setDescription(addButton, "Click to add the selected feat from the Available list of feats");
		addButton.setEnabled(false);
		aPanel.add(addButton);
		tLeftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		tLeftPane.add(scrollPane);

		// Right Pane - Selected
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		tRightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		JLabel selLabel = new JLabel("Selected: ");
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove the selected feat from the Selected list of feats");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		tRightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		tRightPane.add(scrollPane);

		//-------------------------------------------------------------
		// Bottom Pane - Left Info, Right Options / Data
		//
		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		JPanel bLeftPane = new JPanel();
		JPanel bRightPane = new JPanel();

		splitBotLeftRight =
			new FlippingSplitPane(splitOrientation, bLeftPane, bRightPane);
		splitBotLeftRight.setOneTouchExpandable(true);
		splitBotLeftRight.setDividerSize(10);
		//splitBotLeftRight.setDividerLocation(450);

		botPane.add(splitBotLeftRight, BorderLayout.CENTER);

		// Left - Feat Info
		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		bLeftPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(infoScroll, c);

		TitledBorder title1 =
			BorderFactory.createTitledBorder(etched, "Feat Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		infoScroll.setBorder(title1);
		infoLabel.setBackground(bLeftPane.getBackground());
		infoScroll.setViewportView(infoLabel);
		Utility.setDescription(infoScroll, "Any requirements you don't meet are in italics.");
		bLeftPane.add(infoScroll);

		// Right - Feat Options / Data
		// - feats remaining...
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new FlowLayout());
		cPanel.add(new JLabel("Feats remaining: "));
		if (aPC != null)
		{
			numFeatsField.setText(String.valueOf(aPC.getFeats()));
			numFeatsField.setColumns(3);
		}
		cPanel.add(numFeatsField);
		Utility.setDescription(numFeatsField, "How many feats you have left to choose (editable).");
		bRightPane.add(cPanel);

		//----------------------------------------------------------------------
		// Split Top and Bottom
		splitTopBot = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT,
			topPane, botPane);
		splitTopBot.setOneTouchExpandable(true);
		splitTopBot.setDividerSize(10);
		// splitTopBot.setDividerLocation(300);

		this.setLayout(new BorderLayout());
		this.add(splitTopBot, BorderLayout.CENTER);
		availableSort =
			new JTreeTableSorter(availableTable,
				(PObjectNode) availableModel.getRoot(),
				availableModel);
		selectedSort =
			new JTreeTableSorter(selectedTable,
				(PObjectNode) selectedModel.getRoot(),
				selectedModel);
	}

	private void initActionListeners()
	{
		numFeatsField.addFocusListener(new FocusAdapter()
		{
			public void focusLost(FocusEvent evt)
			{
				if (numFeatsField.getText().length() > 0)
				{
					if (aPC != null)
					{
						aPC.setDirty(true);
						aPC.setFeats(Double.parseDouble(numFeatsField.getText()));
					}
				}
				else if (aPC != null)
				{
					numFeatsField.setText(String.valueOf(aPC.getFeats()));
				}
			}
		});
		viewAvailComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewAvailComboBoxActionPerformed();
			}
		});
		viewSelectComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSelectComboBoxActionPerformed();
			}
		});
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown();
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeFeat();
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addFeat();
			}
		});

		FilterFactory.restoreFilterSettings(this);
	}

	private void viewAvailComboBoxActionPerformed()
	{
		final int index = viewAvailComboBox.getSelectedIndex();
		if (index != viewAvailMode)
		{
			viewAvailMode = index;
			SettingsHandler.setFeatTab_AvailableListMode(viewAvailMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setFeatTab_SelectedListMode(viewSelectMode);
			updateSelectedModel();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Feats are color coded: Red = Character does not qualify; " + "Yellow = Automatic Feat; Magenta = Virtual Feat");
		updateCharacterInfo();
		int width;
		int s = splitTopLeftRight.getDividerLocation();
		int t = splitTopBot.getDividerLocation();
		int u = splitBotLeftRight.getDividerLocation();
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption(
				"InfoFeats.splitTopLeftRight",
				(int) (this.getSize().getWidth() * 6 / 10));
			t = SettingsHandler.getPCGenOption(
				"InfoFeats.splitTopBot",
				(int) (this.getSize().getHeight() * 75 / 100));
			u = SettingsHandler.getPCGenOption(
				"InfoFeats.splitBotLeftRight",
				(int) (this.getSize().getWidth() * 6 / 10));
			// set the prefered width on selectedTable
			final TableColumnModel selectedTableColumnModel =
				selectedTable.getColumnModel();
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("FeatSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable,
					"FeatSel", i));
			}
			// set the prefered width on availableTable
			final TableColumnModel availableTableColumnModel =
				availableTable.getColumnModel();
			for (int i = 0; i < availableTable.getColumnCount(); ++i)
			{
				TableColumn aCol = availableTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("FeatAva", i);
				if (width != 0)
				{
					aCol.setPreferredWidth(width);
				}
				aCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "FeatAva", i));
			}
		}
		if (s > 0)
		{
			splitTopLeftRight.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoFeats.splitTopLeftRight", s);
		}
		if (t > 0)
		{
			splitTopBot.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoFeats.splitTopBot", t);
		}
		if (u > 0)
		{
			splitBotLeftRight.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoFeats.splitBotLeftRight", u);
		}
	}

	private static int getEventSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model =
			(DefaultListSelectionModel) e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	private void setAddEnabled(boolean enabled)
	{
		addButton.setEnabled(enabled);
		addMenu.setEnabled(enabled);
	}

	private void setRemoveEnabled(boolean enabled)
	{
		leftButton.setEnabled(enabled);
		removeMenu.setEnabled(enabled);
	}

	/**
	 * This creates the JTreeTables for the available and selected feats
	 * It also creates the actions associated with the objects.
	 */
	private void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					//final String aString =
					//availableTable.getTree().getLastSelectedPathComponent().toString();
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect info when use cursor keys to
					// navigate the tree
					//
					//Object temp =
					//  availableTable.getTree().getLastSelectedPathComponent();
					final int idx = getEventSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null)
					{
						GuiFacade.showMessageDialog(null, "Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						return;
					}

					Feat aFeat = null;
					if (temp instanceof PObjectNode)
					{
						temp = ((PObjectNode) temp).getItem();
						if (temp instanceof Feat)
						{
							aFeat = (Feat) temp;
						}
					}
					if (SettingsHandler.isExpertGUI())
					{
						setAddEnabled(aFeat != null && checkFeatQualify(aFeat) == FEAT_OK);
					}
					else
					{
						setAddEnabled(aFeat != null);
					}
					if (aFeat != null)
					{
						StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.piSubString()).append("</b> &nbsp;TYPE:").append(aFeat.getType());
						if (aFeat.isMultiples())
						{
							bString.append(" &nbsp;Can be taken more than once");
						}
						if (aFeat.isStacks())
						{
							bString.append(" &nbsp;Stacks");
						}
						final String cString = aFeat.preReqHTMLStrings(false);
						if (cString.length() > 0)
						{
							bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
						}
						bString.append(" &nbsp;<b>Description</b>:").append(aFeat.piDescSubString()).append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
						infoLabel.setText(bString.toString());
					}
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
				final TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1 && selPath != null)
					{
						tree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								addFeat();
							}
						});
					}
				}
			}
		};
		tree.addMouseListener(ml);

		selectedTable = new JTreeTable(selectedModel);
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
					// fix bug with displaying incorrect info when use cursor
					// keys to navigate the tree
					//
					final int idx = getEventSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					Object temp = btree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						return;
					}
					/////////////////////////
					boolean removeAllowed = false;
					Feat aFeat = null;
					if (temp instanceof PObjectNode)
					{
						temp = ((PObjectNode) temp).getItem();
						if (temp instanceof Feat)
						{
							aFeat = (Feat) temp;
							removeAllowed = aFeat.getFeatType() == Feat.FEAT_NORMAL;
							//final Feat autoFeat =
							//  aPC.getFeatAutomaticNamed(aFeat.getName());
							//removeAllowed = !Globals.featsMatch(aFeat, autoFeat);
						}
					}
					setRemoveEnabled(removeAllowed);

					if (aFeat != null)
					{
						StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.piSubString()).append("</b> &nbsp;TYPE:").append(aFeat.getType());
						if (aFeat.isMultiples())
						{
							bString.append(" &nbsp;Can be taken more than once");
						}
						if (aFeat.isStacks())
						{
							bString.append(" &nbsp;Stacks");
						}
						final String cString = aFeat.preReqHTMLStrings(false);
						if (cString.length() > 0)
						{
							bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
						}
						bString.append(" &nbsp;<b>Description</b>:").append(aFeat.piDescSubString()).append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
						infoLabel.setText(bString.toString());
					}
				}
			}
		});
		ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int selRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (e.getClickCount() == 1)
					{
						btree.setSelectionPath(selPath);
					}
					else if ((e.getClickCount() == 2) && leftButton.isEnabled())
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								removeFeat();
							}
						});
					}
				}
			}
		};
		btree.addMouseListener(ml);

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character.
	 */
	private void updateCharacterInfo()
	{
		aPC = Globals.getCurrentPC();
		PObjectNode.resetPC();
		if ((aPC == null) || (aPC.isAggregateFeatsStable() && !needsUpdate))
		{
			return;
		}
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);
		aPC.aggregateFeatList();
		JViewport aPort = infoScroll.getColumnHeader();
		if (aPort != null)
		{
			aPort.setVisible(false);
		}
		//showWeaponProfList();

		updateAvailableModel();
		updateSelectedModel();

		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats (" + aPC.getUsedFeatCount() + ")");
		//selectedTable.getTableHeader().resizeAndRepaint();
		numFeatsField.setText(String.valueOf(aPC.getFeats()));
		needsUpdate = false;
	}

	/**
	 * Creates the FeatModel that will be used.
	 */
	private void createModels()
	{
		createModelAvailable();
		createModelSelected();
	}

	private void createModelAvailable()
	{
		if (availableModel == null)
		{
			availableModel = new FeatModel(viewAvailMode, true);
		}
		else
		{
			availableModel.resetModel(viewAvailMode, true);
		}
		if (availableSort != null)
		{
			availableSort.setRoot((PObjectNode) availableModel.getRoot());
		}
	}

	private void createModelSelected()
	{
		if (selectedModel == null)
		{
			selectedModel = new FeatModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false);
		}
		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
		}
	}

	private void updateAvailableModel()
	{
		List pathList = availableTable.getExpandedPaths();
		createModelAvailable();
//		if (availableSort != null)
//			availableSort.sortNodeOnColumn();
		availableTable.updateUI();
		availableTable.expandPathList(pathList);
	}

	private void updateSelectedModel()
	{
		List pathList = selectedTable.getExpandedPaths();
		createModelSelected();
//		if (selectedSort != null)
//			selectedSort.sortNodeOnColumn();
		selectedTable.updateUI();
		selectedTable.expandPathList(pathList);
	}

	private void addFeat()
	{
		//final String aString =
		// availableTable.getTree().getLastSelectedPathComponent().toString();
		String aString = null;
		Object temp = availableTable.getTree().getLastSelectedPathComponent();
		if (temp == null)
		{
			GuiFacade.showMessageDialog(null, "Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();
			if (temp instanceof Feat)
			{
				aString = ((Feat) temp).getName();
			}
		}

		//final Feat aFeat = aPC.getFeatNamed(aString);
		final int fq = checkFeatQualify((Feat) temp);
		switch (fq)
		{
			case FEAT_NOT_QUALIFIED:
				GuiFacade.showMessageDialog(null, NO_QUALIFY_MESSAGE, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return;
			case FEAT_DUPLICATE:
				GuiFacade.showMessageDialog(null, DUPLICATE_MESSAGE, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return;
			case FEAT_FULL_FEAT:
				GuiFacade.showMessageDialog(null, FEAT_FULL_MESSAGE, Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return;
			case FEAT_OK:
				// Feat is OK, so do nothing
				break;
			default:
				Logging.errorPrint("Feat " + ((Feat) temp).getName() + " is somehow in state " + fq + " which is not handled" +
					" in InfoFeats.addFeat()");
				break;
		}

		// we can only be here if the PC can add the feat
		try
		{
			aPC.setDirty(true);
			// modFeat(featName, adding_feat, adding_all_selections)
			aPC.modFeat(aString, true, false);
		}
		catch (Exception exc)
		{
			GuiFacade.showMessageDialog(null, "InfoFeats1: " + exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}

		// update the skills tab, as feats could effect totals
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoInventory();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();

		aPC.aggregateFeatList();
		updateAvailableModel();
		updateSelectedModel();

		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats (" + aPC.getUsedFeatCount() + ")");
		numFeatsField.setText(String.valueOf(aPC.getFeats()));
		setAddEnabled(false);
		aPC.calcActiveBonuses();
	}

	private void removeFeat()
	{
		String aString = null;
		Object temp = selectedTable.getTree().getLastSelectedPathComponent();
		if (temp == null)
		{
			GuiFacade.showMessageDialog(null, "Somehow, no feat was selected.  Try again.", pcgen.core.Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}
		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();
			if (temp instanceof Feat)
			{
				aString = ((Feat) temp).getName();
			}
		}

		try
		{
			aPC.setDirty(true);
			// modFeat(featName, adding_feat, adding_all_selections)
			aPC.modFeat(aString, false, false);
		}
		catch (Exception exc)
		{
			GuiFacade.showMessageDialog(null, "InfoFeats2: " + exc.getMessage(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
		}

		// update the skills tab, as feats could effect totals
		PCGen_Frame1.forceUpdate_InfoSkills();
		PCGen_Frame1.forceUpdate_InfoInventory();
		PCGen_Frame1.forceUpdate_InfoSpells();
		PCGen_Frame1.forceUpdate_InfoSummary();

		aPC.aggregateFeatList();
		updateAvailableModel();
		updateSelectedModel();

		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats (" + aPC.getUsedFeatCount() + ")");
		numFeatsField.setText(String.valueOf(aPC.getFeats()));
		setRemoveEnabled(false);
	}

	/**
	 * Extends AbstractTreeTableModel to build an available or
	 * selected feats tree for this tab.
	 * <p/>
	 * The basic idea of the TreeTableModel is that there is a
	 * single <code>root</code> object. This root object has a null
	 * <code>parent</code>.	All other objects have a parent which
	 * points to a non-null object.	parent objects contain a list of
	 * <code>children</code>, which are all the objects that point
	 * to it as their parent.
	 * objects (or <code>nodes</code>) which have 0 children
	 * are leafs (the end of that linked list).
	 * nodes which have at least 1 child are not leafs.
	 * Leafs are like files and non-leafs are like directories.
	 */
	private final class FeatModel extends AbstractTreeTableModel
	{
		// Names of the columns.
		private String[] cNames = {"Name", "Modified"};

		// Types of the columns.
		private Class[] cTypes = {TreeTableModel.class, String.class};
		private int modelType = 0; // availableModel

		/**
		 * Creates a FeatModel
		 */
		private FeatModel(int mode, boolean available)
		{
			super(null);
			resetModel(mode, available);
		}

		/**
		 * This assumes the FeatModel exists but needs to be repopulated
		 * Calls the various <code>buildTreeXXX</code> methods based on the
		 * <code>mode</code> parameter.
		 *
		 * @param mode      View mode for this tree, one of <code>GuiConstants.INFOFEATS_VIEW_NAMEONLY</code>,
		 *                  <code>GuiConstants.INFOFEATS_VIEW_TYPENAME</code>, or <code>GuiConstants.INFOFEATS_VIEW_PREREQTREE</code>.
		 * @param available <code>true</code> if this is the available feats tree,
		 *                  <code>false</code> if this is the selected feats tree.
		 */
		private void resetModel(int mode, boolean available)
		{
			if (!available)
			{
				modelType = 1;
			}
			switch (mode)
			{
				//NOTE:  I moved the code here into private methods
				//to make it more intelligible
				case GuiConstants.INFOFEATS_VIEW_TYPENAME:
					buildTreeTypeName(available);
					break;

				case GuiConstants.INFOFEATS_VIEW_NAMEONLY:
					buildTreeNameOnly(available);
					break;

				case GuiConstants.INFOFEATS_VIEW_PREREQTREE:
					buildTreePrereqTree(available);
					break;

/*
				case InfoFeats.VIEW_SOURCENAME:
					break;
				case InfoFeats.VIEW_SOURCETYPENAME:
					break;
				case InfoFeats.VIEW_TYPESOURCENAME:
					break;
*/

				default:
					Logging.errorPrint("In InfoFeats.resetModel the mode " + mode + " is not supported.");
					break;

			}

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
		}

		/**
		 * Populates the model with feats in a prerequisite tree.  It retrieves
		 * all feats, then places the feats with no prerequisites under the root
		 * node.  It then iterates the remaining feats and places them under
		 * their appropriate prerequisite feats, creating a node called "Other" at
		 * the end if the prerequisites were not met.
		 *
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 */
		private void buildTreePrereqTree(boolean available)
		{
			if (preReqTreeRoot == null && available)
			{
				preReqTreeRoot = new PObjectNode();
			}
			if (available)
			{
				setRoot(preReqTreeRoot);
			}
			else
			{
				setRoot(new PObjectNode());
			}
			List aList = new ArrayList();
			List fList = new ArrayList();
			if (available)
			{
				// fList = (ArrayList)Globals.getFeatList().clone();
				// make filters work ;-)
				Feat aFeat;
				for (Iterator it = Globals.getFeatList().iterator(); it.hasNext();)
				{
					aFeat = (Feat) it.next();
					if (accept(aFeat))
					{
						if ((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT) ||
							(aFeat.isVisible() == Feat.VISIBILITY_DISPLAY_ONLY))
						{
							fList.add(aFeat);
						}
					}
				}
			}
			else
			{
				// fList = (ArrayList)aPC.aggregateFeatList().clone();
				// make filters work ;-)
				Feat aFeat;
				//My concern here in using buildPCFeatList() instead
				//of aPC.aggregateFeatList() is what duplicates would doo
				//to the tree.  I THINK that the code will find
				//the first prerequisite feat and add the feats to that
				//This may not be perfect, but I don't think it will blow up.
				for (Iterator it = buildPCFeatList().iterator();
						 it.hasNext();)
				{
					aFeat = (Feat) it.next();
					if (accept(aFeat))
					{
						if ((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT) ||
							(aFeat.isVisible() == Feat.VISIBILITY_DISPLAY_ONLY))
						{
							fList.add(aFeat);
						}
					}
				}
			}

			for (int i = 0; i < fList.size(); ++i)
			{
				final Feat aFeat = (Feat) fList.get(i);
				if (!aFeat.hasPreReqOf(1, "PREFEAT:"))
				{
					fList.remove(aFeat);
					aList.add(aFeat);
					--i;	// to counter increment
				}
			}

			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();

			PObjectNode[] cc = new PObjectNode[aList.size()];
			for (int i = 0; i < aList.size(); ++i)
			{
				cc[i] = new PObjectNode();
				cc[i].setItem(aList.get(i));
				cc[i].setParent(rootAsPObjectNode);
				if (!available)
				{
					cc[i].setCheckFeatState(PObjectNode.CAN_USE_FEAT);
				}
			}
			rootAsPObjectNode.setChildren(cc);
			int loopmax = 6; // only go 6 levels...
			while (fList.size() > 0 && loopmax-- > 0)
			{
				for (int i = 0; i < fList.size(); ++i)
				{
					final Feat aFeat = (Feat) fList.get(i);
					int placed = 0;
					for (int j = 0; j < rootAsPObjectNode.getChildCount(); ++j)
					{
						final PObjectNode po = rootAsPObjectNode.getChild(j);
						//
						// Need to make a copy of the prereq list so we don't destroy
						// the other prereqs
						//
						List preReqList = new ArrayList();
						for (int pi = aFeat.getPreReqCount() - 1; pi >= 0; --pi)
						{
							final String pString = aFeat.getPreReq(pi);
							if (pString.startsWith("PREFEAT:"))
							{
								preReqList.add(pString);
							}
						}
						placed = placedThisFeatInThisTree(aFeat, po, PrereqHandler.preReqStringsForList(preReqList), 0, available);
						if (placed > 0)
						{
							break;
						}
					}
					if (placed == 2) // i.e. tree match
					{
						fList.remove(aFeat);
						--i;	// since we're incrementing in the for loop
					}
				}
			}
			if (fList.size() > 0)
			{
				PObjectNode po = new PObjectNode();
				po.setItem("Other");
				cc = new PObjectNode[fList.size()];
				for (int i = 0; i < fList.size(); ++i)
				{
					cc[i] = new PObjectNode();
					cc[i].setItem(fList.get(i));
					cc[i].setParent(po);
					cc[i].setCheckFeatState(modelType == 1 ? (available ? PObjectNode.CAN_GAIN_FEAT : PObjectNode.CAN_USE_FEAT) : PObjectNode.NOT_A_FEAT);
				}
				po.setChildren(cc);
				rootAsPObjectNode.addChild(po);
			}
		}

		/**
		 * Populates the list of feats as a type->name tree.  It sets the root
		 * of the tree to <code>InfoFeats.typeRoot</code>, which contains
		 * the types.  It then iterates the feat list and adds each feat to
		 * all applicable types.
		 *
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 */
		private void buildTreeTypeName(boolean available)
		{
			setRoot(InfoFeats.typeRoot);
			Iterator fI;
			if (available)
			{
				fI = Globals.getFeatList().iterator();
			}
			else
			{
				fI = buildPCFeatList().iterator();
			}

			while (fI.hasNext())
			{
				final Feat aFeat = (Feat) fI.next();
				// in the availableTable, if filtering out unqualified feats
				// ignore any feats the PC doesn't qualify for
				/*
				* update for new filtering
				* author: Thomas Behr 09-02-02
				*/
				if (!accept(aFeat))
				{
					continue;
				}
				if (!((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT)
					|| (aFeat.isVisible() == Feat.VISIBILITY_DISPLAY_ONLY)))
				{
					continue;
				}

				int hasIt = HASFEAT_NO;
				final String featName = aFeat.getName();
				if (available)
				{
					if (aPC.hasFeat(featName))
					{
						hasIt = HASFEAT_CHOSEN;
					}
					else if (aPC.hasFeatAutomatic(featName))
					{
						hasIt = HASFEAT_AUTOMATIC;
					}
					else if (aPC.hasFeatVirtual(featName))
					{
						hasIt = HASFEAT_VIRTUAL;
					}
				}

				// if putting together availableModel, use virtual or non-acquired
				// feats for selectedModel, use virtual, auto and chosen
				PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
				if ((available
					&& ((hasIt == HASFEAT_VIRTUAL)
					|| (hasIt == HASFEAT_NO)
					|| aFeat.isMultiples()))
					|| (!available))
				{
					for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
					{
						if (aFeat.isType(rootAsPObjectNode.getChild(i).toString()))
						{
							PObjectNode aFN = new PObjectNode();
							if (!available)
							{
								aFN.setCheckFeatState(PObjectNode.CAN_USE_FEAT);
							}
							aFN.setParent(rootAsPObjectNode.getChild(i));
							aFN.setItem(aFeat);
							aFN.setIsValid(aFeat.passesPreReqToGain());
							rootAsPObjectNode.getChild(i).addChild(aFN);
						}
					}
				}
			}
		}

		/**
		 * Populates the tree with a list of feats by name only (not much of a tree).
		 * Simply adds feats to the root node.
		 *
		 * @param available <code>true</code> if this is the list of feats available
		 *                  for selection, <code>false</code> if this is the selected feats
		 */
		private void buildTreeNameOnly(boolean available)
		{
			super.setRoot(new PObjectNode());
			Iterator fI;
			if (available)
			{
				fI = Globals.getFeatList().iterator();
			}
			else
			{
				fI = buildPCFeatList().iterator();
			}
			while (fI.hasNext())
			{
				final Feat aFeat = (Feat) fI.next();
				/*
				* update for new filtering
				* author: Thomas Behr 09-02-02
				*/
				if (!accept(aFeat))
				{
					continue;
				}
				if (!((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT) ||
					(aFeat.isVisible() == Feat.VISIBILITY_DISPLAY_ONLY)))
				{
					continue;
				}

				int hasIt = HASFEAT_NO;
				final String featName = aFeat.getName();
				if (available)
				{
					if (aPC.hasFeat(featName))
					{
						hasIt = HASFEAT_CHOSEN;
					}
					else if (aPC.hasFeatAutomatic(featName))
					{
						hasIt = HASFEAT_AUTOMATIC;
					}
					else if (aPC.hasFeatVirtual(featName))
					{
						hasIt = HASFEAT_VIRTUAL;
					}
				}

				// if putting together availableModel, use virtual or non-acquired
				// feats for selectedModel, use virtual, auto and chosen
				if ((available
					&& ((hasIt == HASFEAT_VIRTUAL)
					|| (hasIt == HASFEAT_NO)
					|| aFeat.isMultiples()))
					|| (!available))
				{
					PObjectNode aFN = new PObjectNode();
					aFN.setParent((PObjectNode) super.getRoot());

					if (!available)
					{
						aFN.setCheckFeatState(PObjectNode.CAN_USE_FEAT);
					}

					aFN.setItem(aFeat);
					//Does anyone know why we don't call
					//aFN.setIsValid(aFeat.passesPreReqToGain()) here?
					((PObjectNode) super.getRoot()).addChild(aFN);
				}
			}
		}

		/**
		 * This method gets the feat list from the current PC by calling
		 * <code>aPC.aggregateFeatList()</code>.  Because <code>aggregateFeatList()</code>
		 * (correctly) returns chosen/auto/virtual feats aggregated together, this
		 * elimiates duplicate feats.  However, since we want to display feats with
		 * multiple choices (e.g. Weapon Focus) separately if they are chosen/auto/etc.,
		 * we add back the chosen, virtual, and automatic feats when the <code>isMultiples()</code>
		 * returns <code>true</code>.  Note that this <b>may</b> cause problems for
		 * the prerequisite tree, although the code there <b>appears</b> robust enough
		 * to handle it.
		 * The list is sorted before it is returned.
		 *
		 * @return A list of the current PCs feats.
		 */
		private List buildPCFeatList()
		{
			ArrayList returnValue = new ArrayList(aPC.aggregateFeatList().size());
			for (Iterator pcFeats = aPC.aggregateFeatList().iterator(); pcFeats.hasNext();)
			{
				final Feat aFeat = (Feat) pcFeats.next();
				if (aFeat.isMultiples())
				{
					final String featName = aFeat.getName();
					if (aPC.hasFeat(featName))
					{
						returnValue.add(aPC.getFeatNonAggregateNamed(featName));
					}
					if (aPC.hasFeatAutomatic(featName))
					{
						returnValue.add(aPC.getFeatAutomaticNamed(featName));
					}
					if (aPC.hasFeatVirtual(featName))
					{
						returnValue.add(PlayerCharacter.getFeatNamedInList(aPC.getVirtualFeatList(), featName));
					}
				}
				else
				{
					returnValue.add(aFeat);
				}
			}
			//Need to sort the list.
			return Globals.sortPObjectList(returnValue);
		}

		/**
		 * There must be a root object, though it can be hidden
		 * to make it's existence basically a convenient way to
		 * keep track of the objects
		 */
		private void setRoot(PObjectNode aNode)
		{
			setRoot(aNode.clone());
		}

		public Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		private int placedThisFeatInThisTree(final Feat aFeat, PObjectNode po, String preReqString, int level, boolean available)
		{
			final Feat bFeat = (Feat) po.getItem(); // must be a Feat
			boolean trychildren = false;
			boolean thisisit = true;
			while (true)
			{
				final int a = preReqString.indexOf("FEAT:");
				if (a > -1)
				{
					String pString = preReqString.substring(a + 5);
					final int d = pString.indexOf("\t");
					if (d > -1)
					{
						pString = preReqString.substring(0, d);
					}
					final StringTokenizer aTok =
						new StringTokenizer(pString, ",\t");
					aTok.nextToken(); // first element is number, discard it
					while (aTok.hasMoreTokens())
					{
						final String bString = aTok.nextToken();
						if (bString.equalsIgnoreCase(bFeat.getName()))
						{
							trychildren = true; // might be a child
						}
						else
						{
							thisisit = false;
						}
					}
					if (thisisit)
					{
						PObjectNode p = new PObjectNode();
						p.setItem(aFeat);
						p.setParent(po);
						po.addChild(p);
						p.setCheckFeatState(modelType == 1 ? (available ? PObjectNode.CAN_GAIN_FEAT : PObjectNode.CAN_USE_FEAT) : PObjectNode.NOT_A_FEAT);
						return 2; // successfully added
					}
					else if (trychildren)
					{
						int b = a + 5 +
							preReqString.substring(a + 5).indexOf(bFeat.getName());
						int len = bFeat.getName().length() + 1;
						if (preReqString.length() > b + len &&
							preReqString.substring(b + len).charAt(0) == ',')
						{
							++len;
						}
						StringBuffer aString =
							new StringBuffer(preReqString.substring(0, b));
						if (preReqString.length() > b + len)
						{
							aString.append(preReqString.substring(b + len));
						}
						for (int i = 0; i < po.getChildCount(); ++i)
						{
							int j = placedThisFeatInThisTree(aFeat, po.getChild(i), aString.toString(), level + 1, available);
							if (j == 2)
							{
								return 2;
							}
						}
						return 1;
					}
					else
						return 0; // not here
				}
				else
				{
					// this shouldn't happen!
					Logging.errorPrint("Impossible error in InfoFeats.placedThisFeatInThisTree, prereq is not a feat, it's " + preReqString);
				}
			}
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			return cNames.length;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				if (modelType == 0)
				{
					return "Feat";
				}
				return "Feat (" + aPC.getUsedFeatCount() + ")";
			}
			if (modelType == 0)
			{
				return "Source";
			}
			return "Choices";
		}

		/**
		 * Returns Class for the column.
		 */
		public Class getColumnClass(int column)
		{
			return cTypes[column];
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			PObjectNode fn = (PObjectNode) node;
			switch (column)
			{
				case 0:
					return fn.toString();
				case 1:
					if (modelType == 0)
					{
						return fn.getSource();
					}
					return fn.getChoices();
				case -1:
					return fn.getItem();
				default:
					Logging.errorPrint("In InfoFeats.getValueAt the column " + column + " is not supported.");
					break;

			}
			return null;
		}

	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllFeatFilters(this);

		setKitFilter("FEAT");
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

}
