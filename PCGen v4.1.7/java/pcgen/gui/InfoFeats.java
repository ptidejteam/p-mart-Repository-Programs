/*
 * InfoFeat.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied waarranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on December 29, 2001, 6:57 PM
 */

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;
import pcgen.core.Constants;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;

/**
 * <code>InfoFeats</code>.
 * This class is responsible for drawing the feat related window - including
 * indicating what feats are available, which ones are selected, and handling
 * the selection/de-selection of feats.	This class will also handle displaying
 * weapon profs and allowing the choice of any optional weapon profs since feats
 * are what most impact which weapon profs you have.
 *
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

final class InfoFeats extends FilterAdapterPanel
{

	private FeatModel availableModel = null;	// Model for the JTreeTable.
	private FeatModel selectedModel = null;	// Model for the JTreeTable.
	private JTreeTable availableTable;	// the available Feats
	private JTreeTable selectedTable;	// the selected Feats
	private WholeNumberField numFeatsField = new WholeNumberField(0, 3);
	private JComboBox viewComboBox = new JComboBox();
	private JComboBox viewSelectComboBox = new JComboBox();
	private JTextArea infoText = new JTextArea();
	//protected JTextArea weaponText = new JTextArea();
	private static PlayerCharacter aPC = null;
	private JButton leftButton;
	private JButton rightButton;
	private JLabelPane infoLabel = new JLabelPane();
	private JScrollPane cScroll = new JScrollPane();
	private static boolean needsUpdate = true;
	private static PObjectNode typeRoot = new PObjectNode();
	private static PObjectNode preReqTreeRoot = null;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	//private FlippingSplitPane asplit;
	private JPanel center = new JPanel();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;
	//private JPanel jPanel1 = new JPanel();
	private boolean hasBeenSized = false;
	private Border etched;
	private TitledBorder titled;
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	private static int viewMode = GuiConstants.INFOFEATS_VIEW_TYPENAME;		// keep track of what view mode we're in for Available
	private static int viewSelectMode = GuiConstants.INFOFEATS_VIEW_NAMEONLY;	// default to "Name" in Selection table viewmode

	private static final int HASFEAT_NO = 0;
	private static final int HASFEAT_CHOSEN = 1;
	private static final int HASFEAT_AUTOMATIC = 2;
	private static final int HASFEAT_VIRTUAL = 4;

	//
	// Translate internal feat type to Feat-defined type
	//
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
		typeRoot.resetPC();
	}

	InfoFeats()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Feats");

		aPC = Globals.getCurrentPC();
		Globals.debugPrint("FName=", aPC.getDisplayName());
		typeRoot.resetPC();
		initComponents();
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
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
		if ((iView >= GuiConstants.INFOFEATS_VIEW_TYPENAME) && (iView <= GuiConstants.INFOFEATS_VIEW_PREREQTREE))
		{
			viewMode = iView;
		}
		SettingsHandler.setFeatTab_AvailableListMode(viewMode);
		iView = SettingsHandler.getFeatTab_SelectedListMode();
		if ((iView >= GuiConstants.INFOFEATS_VIEW_TYPENAME) && (iView <= GuiConstants.INFOFEATS_VIEW_PREREQTREE))
		{
			viewSelectMode = iView;
		}
		SettingsHandler.setFeatTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem("Type/Name");
		viewComboBox.addItem("Name");
		viewComboBox.addItem("Pre-Req Tree");
		Utility.setDescription(viewComboBox, "You can change how the feats in the Available and Selected Tables are listed - either by name or in a directory-like structure.");
		viewComboBox.setSelectedIndex(viewMode);

		viewSelectComboBox.addItem("Type/Name");
		viewSelectComboBox.addItem("Name");
		viewSelectComboBox.addItem("Pre-Req Tree");
		Utility.setDescription(viewSelectComboBox, "You can change how the feats in the Selected Tables are listed - either by name or in a directory-like structure.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);
		ArrayList aList = new ArrayList();
		for (int i = 0; i < Globals.getFeatList().size(); ++i)
		{
			final Feat bFeat = Globals.getFeatListFeat(i);
			if (!(bFeat.isVisible() == 1 || bFeat.isVisible() == 3))
				continue;
			final StringTokenizer aTok = new StringTokenizer(bFeat.getType(), ".", false);
			while (aTok.hasMoreTokens())
			{
				String aString = aTok.nextToken();
				if (!aList.contains(aString))
					aList.add(aString);
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
		typeRoot.setChildren(cc, false);
		if (Globals.isDebugMode())
		{
			Globals.debugPrint("chil=", cc[0].getChildCount());
		}
		createModels();
// create available table of feats
		createTreeTables();

		center.setLayout(new BorderLayout());

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(gridbag);
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		// splitPane.setDividerLocation(350);

		center.add(splitPane, BorderLayout.CENTER);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		JLabel avaLabel = new JLabel("Available: ");
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		rightButton = new JButton(newImage);
		Utility.setDescription(rightButton, "Click to add the selected feat from the Available list of feats");
		rightButton.setEnabled(false);
		aPanel.add(rightButton);
		leftPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 5);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new FlowLayout());
		cPanel.add(new JLabel("Feats remaining: "));
		if (aPC != null)
			numFeatsField.setValue(aPC.getFeats());
		cPanel.add(numFeatsField);
		Utility.setDescription(numFeatsField, "How many feats you have left to choose (editable).");
		gridbag.setConstraints(cPanel, c);
		leftPane.add(cPanel);

		Utility.buildConstraints(c, 0, 2, 1, 1, 0, 90);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
		gridbag.setConstraints(scrollPane, c);
		leftPane.add(scrollPane);

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		JLabel selLabel = new JLabel("Selected: ");
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove the selected feat from the Selected list of feats");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Feat Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		cScroll.setBorder(title1);
		infoLabel.setBackground(rightPane.getBackground());
		cScroll.setViewportView(infoLabel);
		Utility.setDescription(cScroll, "Any requirements you don't meet are in italics.");
/*
		jPanel1.setLayout(new BorderLayout());

		JPanel bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout());
		JLabel aLabel = new JLabel(" Weapon Profs");
		bPanel.add(aLabel);
		JButton aButton = new JButton("Optional Profs");
		Utility.setDescription(aButton, "Click to select any optional weapon proficiencies.");
		aButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				weaponSelectPressed(evt);
			}
		});
		bPanel.add(aButton);
		jPanel1.add(bPanel, BorderLayout.NORTH);

		weaponText.setLineWrap(true);
		weaponText.setWrapStyleWord(true);
		weaponText.setEditable(false);
		JScrollPane weaponScroll = new JScrollPane();
		weaponScroll.setViewportView(weaponText);
		jPanel1.add(weaponScroll, BorderLayout.CENTER);

		asplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);
		// asplit.setDividerLocation(300);
*/
		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(cScroll, BorderLayout.CENTER);
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);
		// bsplit.setDividerLocation(300);

		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel.getRoot(), selectedModel);
	}

	private void initActionListeners()
	{
		numFeatsField.addFocusListener(new java.awt.event.FocusAdapter()
		{
			public void focusLost(java.awt.event.FocusEvent evt)
			{
				if (numFeatsField.getText().length() > 0)
				{
					if (aPC != null)
					{
						aPC.setDirty(true);
						aPC.setFeats(numFeatsField.getValue());
					}
				}
				else if (aPC != null)
				{
					numFeatsField.setValue(aPC.getFeats());
				}
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
		addComponentListener(new ComponentAdapter()
		{
			public void componentShown(ComponentEvent evt)
			{
				formComponentShown(evt);
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeFeat(evt);
			}
		});
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addFeat(evt);
			}
		});
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setFeatTab_AvailableListMode(viewMode);
			createModelAvailable();
			availableTable.updateUI();
		}
	}

	private void viewSelectComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setFeatTab_SelectedListMode(viewSelectMode);
			createModelSelected();
			selectedTable.updateUI();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Feats are color coded: Red = Character does not qualify; " +
			"Yellow = Automatic Feat; Magenta = Virtual Feat");
		updateCharacterInfo();
		int width;
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		//int u = asplit.getDividerLocation();
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			s = SettingsHandler.getPCGenOption("InfoFeats.splitPane", (int) (this.getSize().getWidth() * 6 / 10));
			t = SettingsHandler.getPCGenOption("InfoFeats.bsplit", (int) (this.getSize().getHeight() * 75 / 100));
			//u = SettingsHandler.getPCGenOption("InfoFeats.asplit", (int)(this.getSize().getWidth() * 5 / 10));
			// set the prefered width on selectedTable
			final TableColumnModel selectedTableColumnModel = selectedTable.getColumnModel();
			for (int i = 0; i < selectedTable.getColumnCount(); ++i)
			{
				TableColumn sCol = selectedTableColumnModel.getColumn(i);
				width = Globals.getCustColumnWidth("FeatSel", i);
				if (width != 0)
				{
					sCol.setPreferredWidth(width);
				}
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "FeatSel", i));
			}
			// set the prefered width on availableTable
			final TableColumnModel availableTableColumnModel = availableTable.getColumnModel();
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
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoFeats.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoFeats.bsplit", t);
		}
/*
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoFeats.asplit", u);
		}
*/
	}

	private static int getEventSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel) e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	/**
	 * This creates the JTreeTables for the available feats and selected feats.
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
					//final String aString = availableTable.getTree().getLastSelectedPathComponent().toString();
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect info when use cursor keys to navigate the tree
					//
					//Object temp = availableTable.getTree().getLastSelectedPathComponent();
					final int idx = getEventSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null)
					{
						JOptionPane.showMessageDialog(null,
							"Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
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
					rightButton.setEnabled(aFeat != null);
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
						bString.append(" &nbsp;<b>Description</b>:").append(aFeat.getDescription()).append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
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
						tree.setSelectionPath(selPath);
					else if (e.getClickCount() == 2)
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								addFeat(null);
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
					// fix bug with displaying incorrect info when use cursor keys to navigate the tree
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
							//final Feat autoFeat = aPC.getFeatAutomaticNamed(aFeat.getName());
							//removeAllowed = !Globals.featsMatch(aFeat, autoFeat);
						}
					}
					leftButton.setEnabled(removeAllowed);

					if (aFeat != null)
					{
						StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.piSubString()).append("</b> &nbsp;TYPE:").append(aFeat.getType());
						if (aFeat.isMultiples())
							bString.append(" &nbsp;Can be taken more than once");
						if (aFeat.isStacks())
							bString.append(" &nbsp;Stacks");
						final String cString = aFeat.preReqHTMLStrings(false);
						if (cString.length() > 0)
							bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
						bString.append(" &nbsp;<b>Description</b>:").append(aFeat.getDescription()).append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
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
						btree.setSelectionPath(selPath);
					else if ((e.getClickCount() == 2) && leftButton.isEnabled())
					{
						// We run this after the event has been processed so that
						// we don't confuse the table when we change its contents
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								removeFeat(null);
							}
						});
					}
				}
			}
		};
		btree.addMouseListener(ml);
	}
/*
	private void showWeaponProfList()
	{
		SortedSet weaponProfs = aPC.getWeaponProfList();
		if (weaponProfs.size() > 0)
			weaponText.setText
			  (pcgen.core.Utility.commaDelimit(weaponProfs));
		else
			weaponText.setText(Constants.s_NONE);
		weaponText.setCaretPosition(0);
	}
*/
	// This recalculates the states of everything based upon the currently selected
	// character.
	private void updateCharacterInfo()
	{
		aPC = Globals.getCurrentPC();
		typeRoot.resetPC();
		if ((aPC == null) || (aPC.isAggregateFeatsStable() && !needsUpdate))
			return;
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);
		aPC.aggregateFeatList();
		JViewport aPort = cScroll.getColumnHeader();
		if (aPort != null)
			aPort.setVisible(false);
		//showWeaponProfList();

		updateModelAvailable();
		updateModelSelected();

		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats (" + aPC.getUsedFeatCount() + ")");
		selectedTable.getTableHeader().updateUI();
		numFeatsField.setValue(aPC.getFeats());
		needsUpdate = false;
	}

	/* *
	 * This method is run when the weapon proficiency button is pressed.
	 * /
	private void weaponSelectPressed(ActionEvent evt)
	{
		if (aPC != null)
		{
			//
			// Get a list of the race/class(es) that have a bonus weapon allowed
			//
			ArrayList bonusCategory = new ArrayList();
			final Race pcRace = aPC.getRace();
			if (pcRace != null)
			{
				if (pcRace.getWeaponProfBonus().size() != 0)
				{
					bonusCategory.add(pcRace);
				}
			}
			for (Iterator e = aPC.getClassList().iterator(); e.hasNext();)
			{
				final PCClass aClass = (PCClass)e.next();
				if (aClass.getWeaponProfBonus().size() != 0)
				{
					bonusCategory.add(aClass);
				}
			}
			for (Iterator e = aPC.getTemplateList().iterator(); e.hasNext();)
			{
				final PCTemplate aTemplate = (PCTemplate)e.next();
				if (aTemplate.getWeaponProfBonus().size() != 0)
				{
					bonusCategory.add(aTemplate);
					Globals.debugPrint("TEMP WEAP=" + aTemplate.getWeaponProfBonus());
				}
			}

			final ArrayList pcDomains = aPC.getCharacterDomainList();
			for (Iterator e = pcDomains.iterator(); e.hasNext();)
			{
				final CharacterDomain aCD = (CharacterDomain)e.next();
				final String sourceType = aCD.getSourceType().toLowerCase();
				if ((sourceType.equals("pcclass") || sourceType.equals("feat")) &&
				  (aCD.toString().length() != 0) && aCD.getDomain().getChoiceString().startsWith("WEAPONPROF|"))
				{
					bonusCategory.add(aCD);
				}
			}

			if (bonusCategory.size() == 0)
			{
				JOptionPane.showMessageDialog(null, "You have no optional weapons proficiencies.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			int selIdx = 0;
			for (; ;)
			{
				//
				// If there is only one set of choices allowed, then use it
				//
				Object profBonusObject;
				if (bonusCategory.size() == 1)
				{
					profBonusObject = bonusCategory.get(0);
				}
				else
				{
					for (; ;)
					{
						Object selectedValue = JOptionPane.showInputDialog(null,
						  "You have more than one bonus selection available.\n" +
						  "Please choose a category from the list below.",
						  Constants.s_APPNAME,
						  JOptionPane.INFORMATION_MESSAGE,
						  null,
						  bonusCategory.toArray(),
						  bonusCategory.get(selIdx));
						if (selectedValue != null)
						{
							profBonusObject = selectedValue;
							selIdx = bonusCategory.indexOf(selectedValue);
							break;
						}
						return;
					}
				}
				if (profBonusObject instanceof CharacterDomain)
				{
					final Domain aDomain = ((CharacterDomain)profBonusObject).getDomain();
					aDomain.getChoices(aDomain.getChoiceString(), new ArrayList());
				}
				else
				{
					ArrayList profWeapons = new ArrayList();
					if (profBonusObject instanceof PCClass)
					{
						profWeapons = ((PCClass)profBonusObject).getWeaponProfBonus();
						((PCClass)profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.Utility.unSplit(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
					else if (profBonusObject instanceof Race)
					{
						profWeapons = ((Race)profBonusObject).getWeaponProfBonus();
						((Race)profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.Utility.unSplit(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
					else if (profBonusObject instanceof PCTemplate)
					{
						profWeapons = ((PCTemplate)profBonusObject).getWeaponProfBonus();
						((PCTemplate)profBonusObject).getChoices("WEAPONPROF|1|" + pcgen.core.Utility.unSplit(profWeapons, "[WEAPONPROF]|") + "[WEAPONPROF]", new ArrayList());
					}
				}

				aPC.setDirty(true);

//				aPC.setAutomaticFeatsStable(false);
				aPC.aggregateFeatList();
				updateModelSelected();

				showWeaponProfList();
				if (bonusCategory.size() == 1)
				{
					break;
				}
			}
			return;
		}
	}
*/
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
			availableModel = new FeatModel(viewMode, true);
		}
		else
		{
			availableModel.resetModel(viewMode, true, false);
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
			selectedModel.resetModel(viewSelectMode, false, false);
		}
		if (selectedSort != null)
		{
			selectedSort.setRoot((PObjectNode) selectedModel.getRoot());
		}
	}

	private void updateModelAvailable()
	{
		int[] expandedRows = availableTable.getExpandedRows();
		createModelAvailable();
//		if (availableSort != null)
//			availableSort.sortNodeOnColumn();
		availableTable.updateUI();
		availableTable.setExpandedRows(expandedRows);
	}

	private void updateModelSelected()
	{
		int[] expandedRows = selectedTable.getExpandedRows();
		createModelSelected();
//		if (selectedSort != null)
//			selectedSort.sortNodeOnColumn();
		selectedTable.updateUI();
		selectedTable.setExpandedRows(expandedRows);
	}

	private void addFeat(ActionEvent evt)
	{
		//final String aString = availableTable.getTree().getLastSelectedPathComponent().toString();
		String aString = null;
		Object temp = availableTable.getTree().getLastSelectedPathComponent();
		if (temp == null)
		{
			JOptionPane.showMessageDialog(null,
				"Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
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

//		final Feat aFeat = aPC.getFeatNamed(aString);
		Feat aFeat = aPC.getFeatNonAggregateNamed(aString);
		final boolean pcHasIt = (aFeat != null);

		if (pcHasIt && !aFeat.isMultiples())
		{
			JOptionPane.showMessageDialog(null, "You already have that feat.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (!pcHasIt)
		{
			aFeat = Globals.getFeatNamed(aString);
			if ((aFeat != null) && !aFeat.passesPreReqTests())
			{
				JOptionPane.showMessageDialog(null, "You do no not meet the prerequisites required to take this feat.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		if (numFeatsField.getValue() == 0 && !pcHasIt)
		{
			JOptionPane.showMessageDialog(null, "You cannot select any more feats.", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			return;
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
			JOptionPane.showMessageDialog(null, "InfoFeats1: " + exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}

		// update the skills tab, as feats could effect totals
		Globals.getRootFrame().forceUpdate_InfoSkills();

		aPC.aggregateFeatList();
		updateModelAvailable();
		updateModelSelected();

		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats (" + aPC.getUsedFeatCount() + ")");
		numFeatsField.setValue(aPC.getFeats());
		rightButton.setEnabled(false);
	}

	private void removeFeat(ActionEvent evt)
	{
		String aString = null;
		Object temp = selectedTable.getTree().getLastSelectedPathComponent();
		if (temp == null)
		{
			JOptionPane.showMessageDialog(null,
				"Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (temp instanceof PObjectNode)
		{
			temp = ((PObjectNode) temp).getItem();
			if (temp instanceof Feat)
				aString = ((Feat) temp).getName();
		}

		try
		{
			aPC.setDirty(true);
			// modFeat(featName, adding_feat, adding_all_selections)
			aPC.modFeat(aString, false, false);
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(null, "InfoFeats2: " + exc.getMessage(), Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
		}

		// update the skills tab, as feats could effect totals
		Globals.getRootFrame().forceUpdate_InfoSkills();

		aPC.aggregateFeatList();
		updateModelAvailable();
		updateModelSelected();

		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats (" + aPC.getUsedFeatCount() + ")");
		numFeatsField.setValue(aPC.getFeats());
		leftButton.setEnabled(false);
	}


	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *	object.	This root object has a null <code>parent</code>.	All other objects
	 *	have a parent which points to a non-null object.	parent objects contain a list of
	 *	<code>children</code>, which are all the objects that point to it as their parent.
	 *	objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *	linked list).	nodes which have at least 1 child are not leafs. Leafs are like files
	 *	and non-leafs are like directories.
	 */
	final private class FeatModel extends AbstractTreeTableModel implements TreeTableModel
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
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the FeatModel exists but needs to be repopulated
		 */
		private void resetModel(int mode, boolean available, boolean newCall)
		{
			if (!available)
			{
				modelType = 1;
			}
			switch (mode)
			{
				case GuiConstants.INFOFEATS_VIEW_TYPENAME:
					if (Globals.isDebugMode())
					{
						Globals.debugPrint(modelType + " children=" + InfoFeats.typeRoot.getChild(0).getChildCount());
					}
					setRoot(InfoFeats.typeRoot);
					if (Globals.isDebugMode())
					{
						Globals.debugPrint(modelType + " children=" + ((PObjectNode) root).getChild(0).getChildCount());
					}
					for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
					{
						final Feat aFeat = (Feat) fI.next();
						// in the availableTable, if filtering out unqualified feats
						// ignore any feats the PC doesn't qualify for
						/*
						* update for new filtering
						* author: Thomas Behr 09-02-02
						*/
						if (available && !accept(aFeat))
						{
							continue;
						}
						if (!((aFeat.isVisible() == Feat.VISIBILITY_DEFAULT) || (aFeat.isVisible() == Feat.VISIBILITY_DISPLAY_ONLY)))
						{
							continue;
						}

						int hasIt = HASFEAT_NO;
						final String featName = aFeat.getName();
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

						// if putting together availableModel, use virtual or non-acquired feats
						// for selectedModel, use virtual, auto and chosen
						PObjectNode rootAsPObjectNode = (PObjectNode) root;
						if (available && ((hasIt == HASFEAT_VIRTUAL) || (hasIt == HASFEAT_NO) || aFeat.isMultiples()) ||
							(!available && (hasIt != HASFEAT_NO) && accept(aFeat)))
						{
							for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
							{
								if (aFeat.isType(rootAsPObjectNode.getChild(i).toString()))
								{
									PObjectNode aFN = new PObjectNode();
									if (!available)
									{
										aFN.setCheckFeatState(true);
									}
									aFN.setParent(rootAsPObjectNode.getChild(i));
									if (!available)
									{
										//final Feat bFeat = aPC.getFeatNamed(featName);
										final Feat bFeat = aPC.getFeatNamed(featName, xlatInternalType(hasIt));
										if (bFeat != null)
										{
											aFN.setItem(bFeat);
											aFN.setIsValid(bFeat.passesPreReqTests());
										}
									}
									if (aFN.getItem() == null)
									{
										aFN.setItem(aFeat);
										aFN.setIsValid(aFeat.passesPreReqTests());
									}
									rootAsPObjectNode.getChild(i).addChild(aFN);
								}
							}
						}
					}
					break;

				case GuiConstants.INFOFEATS_VIEW_NAMEONLY:
					root = new PObjectNode();
					for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
					{
						final Feat aFeat = (Feat) fI.next();
						// in the availableTable, if filtering out unqualified feats
						// ignore any feats the PC doesn't qualify for
						/*
						* update for new filtering
						* author: Thomas Behr 09-02-02
						*/
						if (available && !accept(aFeat))
							continue;
						if (!(aFeat.isVisible() == 1 || aFeat.isVisible() == 3))
							continue;

						int hasIt = HASFEAT_NO;
						final String featName = aFeat.getName();
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

						// if putting together availableModel, use virtual or non-acquired feats
						// for selectedModel, use virtual, auto and chosen
						if (available && ((hasIt == HASFEAT_VIRTUAL) || (hasIt == HASFEAT_NO) || aFeat.isMultiples()) ||
							(!available && (hasIt != HASFEAT_NO) && accept(aFeat)))
						{
							PObjectNode aFN = new PObjectNode();
							aFN.setParent((PObjectNode) root);

							if (!available)
							{
								aFN.setCheckFeatState(true);
								//Feat bFeat = aPC.getFeatNamed(featName);
								Feat bFeat = aPC.getFeatNamed(featName, xlatInternalType(hasIt));
								if (bFeat != null)
								{
									if (hasIt == HASFEAT_CHOSEN)
									{
										//
										// Remove all auto choices from aggregate
										//
										final Feat autoFeat = aPC.getFeatAutomaticNamed(featName);
										if (autoFeat != null)
										{
											bFeat = aPC.getFeatNonAggregateNamed(featName);
											if (bFeat != null)
											{
												aFN.setItem(autoFeat);
												((PObjectNode) root).addChild(aFN);

												aFN = new PObjectNode();
												aFN.setParent((PObjectNode) root);
												aFN.setCheckFeatState(true);
											}
											else
											{
												bFeat = autoFeat;
											}
										}
									}
									aFN.setItem(bFeat);
								}
							}

							if (aFN.getItem() == null)
							{
								aFN.setItem(aFeat);
							}
							((PObjectNode) root).addChild(aFN);
						}
					}
					break;

				case GuiConstants.INFOFEATS_VIEW_PREREQTREE:
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
					ArrayList aList = new ArrayList();
					ArrayList fList = new ArrayList();
					if (available)
					{
//  						fList = (ArrayList)Globals.getFeatList().clone();
						// make filters work ;-)
						Feat aFeat;
						for (Iterator it = Globals.getFeatList().iterator(); it.hasNext();)
						{
							aFeat = (Feat) it.next();
							if (accept(aFeat))
							{
								fList.add(aFeat);
							}
						}
					}
					else
					{
//  						fList = (ArrayList)aPC.aggregateFeatList().clone();
						// make filters work ;-)
						Feat aFeat;
						for (Iterator it = aPC.aggregateFeatList().iterator(); it.hasNext();)
						{
							aFeat = (Feat) it.next();
							if (accept(aFeat))
							{
								fList.add(aFeat);
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

					PObjectNode rootAsPObjectNode = (PObjectNode) root;

					PObjectNode[] cc = new PObjectNode[aList.size()];
					for (int i = 0; i < aList.size(); ++i)
					{
						cc[i] = new PObjectNode();
						cc[i].setItem(aList.get(i));
						cc[i].setParent(rootAsPObjectNode);
						if (!available)
						{
							cc[i].setCheckFeatState(true);
						}
					}
					rootAsPObjectNode.setChildren(cc, false);
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
								// Need to make a copy of the prereq list so we don't destroy the
								// other prereqs
								//
								ArrayList preReqList = new ArrayList();
								for (int pi = aFeat.getPreReqCount() - 1; pi >= 0; --pi)
								{
									final String pString = aFeat.getPreReq(pi);
									if (pString.startsWith("PREFEAT:"))
									{
										preReqList.add(pString);
									}
								}
								placed = placedThisFeatInThisTree(aFeat, po, aFeat.preReqStringsForList(preReqList), 0);
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
							if (modelType == 1)
								cc[i].setCheckFeatState(true);
						}
						po.setChildren(cc, false);
						rootAsPObjectNode.addChild(po);
					}
					break;

/*				case InfoFeats.VIEW_SOURCENAME:
					break;
				case InfoFeats.VIEW_SOURCETYPENAME:
					break;
				case InfoFeats.VIEW_TYPESOURCENAME:
					break;
*/

				default:
					Globals.errorPrint("In InfoFeats.resetModel the mode " + mode + " is not supported.");
					break;

			}
			if (!newCall)
			{
				PObjectNode rootAsPObjectNode = (PObjectNode) root;
				if (rootAsPObjectNode.getChildCount() > 0)
					fireTreeNodesChanged(root, rootAsPObjectNode.getChildren(), null, null);
			}
			if (Globals.isDebugMode())
			{
				Globals.debugPrint(modelType + " children=" + InfoFeats.typeRoot.getChild(0).getChildCount());
			}
		}

		/** "There can be only one!" There must be a root object, though it can be hidden
		 to make it's existence basically a convenient way to keep track of the objects
		 */
		private void setRoot(PObjectNode aNode)
		{
			root = aNode.clone();
		}

		public Object getRoot()
		{
			return (PObjectNode) root;
		}

		private int placedThisFeatInThisTree(final Feat aFeat, PObjectNode po, String preReqString, int level)
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
						pString = preReqString.substring(0, d);
					final StringTokenizer aTok = new StringTokenizer(pString, ",\t", false);
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
						if (modelType == 1)
							p.setCheckFeatState(true);
						return 2; // successfully added
					}
					else if (trychildren)
					{
						int b = a + 5 + preReqString.substring(a + 5).indexOf(bFeat.getName());
						int len = bFeat.getName().length() + 1;
						if (preReqString.length() > b + len && preReqString.substring(b + len).startsWith(","))
						{
							++len;
						}
						StringBuffer aString = new StringBuffer(preReqString.substring(0, b));
						if (preReqString.length() > b + len)
						{
							aString.append(preReqString.substring(b + len));
						}
						for (int i = 0; i < po.getChildCount(); ++i)
						{
							int j = placedThisFeatInThisTree(aFeat, po.getChild(i), aString.toString(), level + 1);
							if (j == 2)
								return 2;
						}
						return 1;
					}
					else
						return 0; // not here
				}
				else
				{
					// this shouldn't happen!

				}
			}
		}

		private void pruneChildless()
		{
			PObjectNode rootAsPObjectNode = (PObjectNode) root;
			ArrayList aList = new ArrayList();
			for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
				if (rootAsPObjectNode.getChild(i).getChildCount() > 0)
					aList.add(rootAsPObjectNode.getChild(i));
			PObjectNode[] newkids = new PObjectNode[aList.size()];
			for (int i = 0; i < aList.size(); ++i)
				newkids[i] = (PObjectNode) aList.get(i);
			((PObjectNode) root).setChildren(newkids, false);
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
					return "Feat";
				return "Feat (" + aPC.getUsedFeatCount() + ")";
			}
			if (modelType == 0)
				return "Source";
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
						return fn.getSource();
					return fn.getChoices();
				case -1:
					return fn.getItem();
				default:
					Globals.errorPrint("In InfoFeats.getValueAt the column " + column + " is not supported.");
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
		return MULTI_MULTI_MODE;
	}

}
