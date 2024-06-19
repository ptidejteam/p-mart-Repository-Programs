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

/* This class is responsible for drawing the feat related window - including
 * indicating what feats are available, which ones are selected, and handling
 * the selection/de-selection of feats.	This class will also handle displaying
 * weapon profs and allowing the choice of any optional weapon profs since feats
 * are what most impact which weapon profs you have.
 */

/**
 * <code>InfoFeats</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

package pcgen.gui;

import java.awt.*;
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
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.TreePath;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;

public class InfoFeats extends FilterAdapterPanel
{

	protected FeatModel availableModel = null;	// Model for the JTreeTable.
	protected FeatModel selectedModel = null;	// Model for the JTreeTable.
	protected JTreeTable availableTable;	// the available Feats
	protected JTreeTable selectedTable;	// the selected Feats
	protected WholeNumberField numFeatsField = new WholeNumberField(0, 3);
	protected JComboBox viewComboBox = new JComboBox();
	protected JComboBox viewSelectComboBox = new JComboBox();
	protected JTextArea infoText = new JTextArea();
	protected JTextArea weaponText = new JTextArea();
	protected PlayerCharacter aPC = null;
	JButton leftButton;
	JButton rightButton;
        /*
         * initializing the editor pane with default HTML tags;
         * this fixes a bug which causes NPEs to be thrown on updateUI()
         * with no HTML tags present
         *
         * author: Thomas Behr 13-03-03
         */
	JEditorPane infoLabel = new JEditorPane("text/html", "<html></html>");
	JScrollPane cScroll = new JScrollPane();
	static boolean needsUpdate = true;
	public static PObjectNode typeRoot = new PObjectNode();
	public static PObjectNode preReqTreeRoot = null;
	JSplitPane splitPane;
	JSplitPane bsplit;
	JSplitPane asplit;
	JPanel center = new JPanel();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;
	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel2 = new JPanel();
	private boolean hasBeenSized = false;
	Border etched;
	TitledBorder titled;
	private JTreeTableSorter availableSort=null;
	private JTreeTableSorter selectedSort=null;

	public final static int VIEW_TYPENAME = 0;		// view mode for Type->Name
	public final static int VIEW_NAMEONLY = 1;		// view mode for Name (essentially a JTable)
	public final static int VIEW_PREREQTREE = 2;		// view in requirement tree mode
	//public final static int VIEW_SOURCENAME = 3;		// view mode for Source->Name
	//public final static int VIEW_SOURCETYPENAME = 4;	// view mode for Source->Type->Name
	//public final static int VIEW_TYPESOURCENAME = 5;	// view mode for Type->Source->Name
	private static int viewMode = VIEW_TYPENAME;		// keep track of what view mode we're in for Available
	private static int viewSelectMode = VIEW_NAMEONLY;	// default to "Name" in Selection table viewmode


	public void forceUpdate()
	{
		needsUpdate = true;
	}

	//Set up GridBag Constraints
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	public InfoFeats()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Feats");

		aPC = Globals.getCurrentPC();
		final PObjectNode a = new PObjectNode();
		Globals.debugPrint("FName=" + aPC.getName());
		a.resetPC();
		initComponents();
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
	}

	void initComponents()
	{
		//
		// Sanity check
		//
		int iView = Globals.getFeatTab_AvailableListMode();
		if ((iView >= VIEW_TYPENAME) && (iView <= VIEW_PREREQTREE))
		{
			viewMode = iView;
		}
		Globals.setFeatTab_AvailableListMode(viewMode);
		iView = Globals.getFeatTab_SelectedListMode();
		if ((iView >= VIEW_TYPENAME) && (iView <= VIEW_PREREQTREE))
		{
			viewSelectMode = iView;
		}
		Globals.setFeatTab_SelectedListMode(viewSelectMode);

		viewComboBox.addItem("Type/Name");
		viewComboBox.addItem("Name");
		viewComboBox.addItem("Pre-Req Tree");
		viewComboBox.setToolTipText("You can change how the feats in the Available and Selected Tables are listed - either by name or in a directory-like structure.");
		viewComboBox.setSelectedIndex(viewMode);

		viewSelectComboBox.addItem("Type/Name");
		viewSelectComboBox.addItem("Name");
		viewSelectComboBox.addItem("Pre-Req Tree");
		viewSelectComboBox.setToolTipText("You can change how the feats in the Selected Tables are listed - either by name or in a directory-like structure.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode);
		ArrayList aList = new ArrayList();
		for (int i = 0; i < Globals.getFeatList().size(); i++)
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
		for (int i = 0; i < aList.size(); i++)
		{
			cc[i] = new PObjectNode();
			cc[i].setItem(aList.get(i).toString());
			cc[i].setParent(typeRoot);
		}
		typeRoot.setChildren(cc, false);
		Globals.debugPrint("chil=" + cc[0].getChildren().length);
		createModels();
// create available table of feats
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
		// splitPane.setDividerLocation(350);

		center.add(splitPane, BorderLayout.CENTER);

		buildConstraints(c, 0, 0, 1, 1, 100, 5);
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
		rightButton.setToolTipText("Click to add the selected feat from the Available list of feats");
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

		buildConstraints(c, 0, 1, 1, 1, 0, 5);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JPanel cPanel = new JPanel();
		cPanel.setLayout(new FlowLayout());
		cPanel.add(new JLabel("Feats remaining: "));
		if (aPC != null)
			numFeatsField.setValue(aPC.getFeats());
		cPanel.add(numFeatsField);
		numFeatsField.setToolTipText("How many feats you have left to choose (editable).");
		gridbag.setConstraints(cPanel, c);
		leftPane.add(cPanel);


		buildConstraints(c, 0, 2, 1, 1, 0, 90);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		JScrollPane scrollPane = new JScrollPane(availableTable);
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
		JLabel selLabel = new JLabel("Selected: ");
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		leftButton.setToolTipText("Click to remove the selected feat from the Selected list of feats");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		rightPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
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
		cScroll.setToolTipText("Any requirements you don't meet are in italics.");

		jPanel1.setLayout(new BorderLayout());

		JPanel bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout());
		JLabel aLabel = new JLabel(" Weapon Profs");
		bPanel.add(aLabel);
		JButton aButton = new JButton("Optional Profs");
		aButton.setToolTipText("Click to select any optional weapon proficiencies.");
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


		asplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);
		// asplit.setDividerLocation(300);



		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, center, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);
		// bsplit.setDividerLocation(300);



		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode)availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode)selectedModel.getRoot(), selectedModel);
	}

	void initActionListeners()
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
			Globals.setFeatTab_AvailableListMode(viewMode);
			createModels();
			availableTable.updateUI();
			//selectedTable.updateUI();
		}
	}

	private void viewSelectComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			Globals.setFeatTab_SelectedListMode(viewSelectMode);
			createModels();
			//availableTable.updateUI();
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
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			splitPane.setDividerLocation(.4);
			bsplit.setDividerLocation(.75);
			asplit.setDividerLocation(.5);
		}
	}

	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, double wx, double wy)
	{
		gbc.gridx = gx; // x cols from left (left-most col for multi-column cell)
		gbc.gridy = gy; // y rows from top (top-most row for multi-row cell)
		gbc.gridwidth = gw; // cols wide
		gbc.gridheight = gh; // rows high
		gbc.weightx = wx; // weight of x, I typically put in percentile, only need to
		// specify this once for each column, other values in same
		// column are 0.0
		gbc.weighty = wy; // weight of y, same as weight for cols, just specify a
		// non-zero value for one cell in each row.
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

	/**
	 * This creates the JTreeTables for the available feats and selected feats.
	 * It also creates the actions associated with the objects.
	 */
	protected void createTreeTables()
	{
		availableTable = new JTreeTable(availableModel);
		availableTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting())
				{
					//final String aString = availableTable.getTree().getLastSelectedPathComponent().toString();
					String aString = null;
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
					final Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp != null)
					{
						aString = temp.toString();
					}
					else
					{
						JOptionPane.showMessageDialog(null,
							"Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}

					final Feat aFeat = Globals.getFeatNamed(aString.substring(aString.lastIndexOf("|") + 1));
					rightButton.setEnabled(!aString.startsWith("|") && aFeat != null);
					if (aFeat != null)
					{
						StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.getName()).append("</b> &nbsp;TYPE:").append(aFeat.getType());
						if (aFeat.isMultiples())
							bString.append(" &nbsp;Can be taken more than once");
						if (aFeat.isStacks())
							bString.append(" &nbsp;Stacks");
						final String cString = aFeat.preReqHTMLStrings(false);
						if (cString.length() > 0)
							bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
						bString.append(" &nbsp;<b>Description</b>:").append(aFeat.getDescription()).append("</html>");
						infoLabel.setText(bString.toString());
						infoLabel.setCaretPosition(0);
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
						addFeat(null);
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
					//String aString = btree.getLastSelectedPathComponent().toString();
					String aString = null;
					/////////////////////////
					// Byngl Feb 20/2002
					// fix bug with displaying incorrect info when use cursor keys to navigate the tree
					//
					final int idx = getEventSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = btree.getPathForRow(idx).getLastPathComponent();
					if (temp != null)
					{
						aString = temp.toString();
					}
					else
					{
						return;
					}
					/////////////////////////
					aString = aString.substring(aString.lastIndexOf("|") + 1);
					final Feat aFeat = Globals.getFeatNamed(aString);
					final boolean flag = (aFeat != null && aPC.hasFeatAutomatic(aString));
					leftButton.setEnabled(aFeat != null && !flag);
					if (aFeat != null)
					{
						StringBuffer bString = new StringBuffer().append("<html><b>").append(aFeat.getName()).append("</b> &nbsp;TYPE:").append(aFeat.getType());
						if (aFeat.isMultiples())
							bString.append(" &nbsp;Can be taken more than once");
						if (aFeat.isStacks())
							bString.append(" &nbsp;Stacks");
						final String cString = aFeat.preReqHTMLStrings(false);
						if (cString.length() > 0)
							bString.append(" &nbsp;<b>Requirements</b>:").append(cString);
						bString.append(" &nbsp;<b>Description</b>:").append(aFeat.getDescription()).append(" &nbsp;<b>Source</b>:").append(aFeat.getSource()).append("</html>");
						infoLabel.setText(bString.toString());
						infoLabel.setCaretPosition(0);
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
					else if (e.getClickCount() == 2)
					{
						removeFeat(null);
					}
				}
			}
		};
		btree.addMouseListener(ml);
	}

	// This recalculates the states of everything based upon the currently selected
	// character.
	public void updateCharacterInfo()
	{
		final PObjectNode a = new PObjectNode();
		a.resetPC();
		if (aPC == null || needsUpdate == false)
			return;
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);
		aPC.aggregateFeatList();
		JViewport aPort = cScroll.getColumnHeader();
		if (aPort != null)
			aPort.setVisible(false);
		SortedSet autoprofs = (SortedSet)aPC.getAutoWeaponProfs();
		SortedSet weaponProfs = aPC.getWeaponProfList();
		if (weaponProfs.size() > 0)
			weaponText.setText
				(pcgen.core.Utility.stringForList(weaponProfs.iterator(), ", "));
		else
			weaponText.setText("None");
		createModels();
		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats ("+aPC.getFeatList().size()+")");
		availableTable.updateUI();
		selectedTable.updateUI();
		selectedTable.getTableHeader().updateUI();
		numFeatsField.setValue(aPC.getFeats());
		needsUpdate = false;
	}

	/**
	 * This method is run when the weapon proficiency button is pressed.
	 */
	private void weaponSelectPressed(ActionEvent evt)
	{
		if (aPC != null)
		{
			aPC.setDirty(true);
			Chooser lc = new Chooser();
			lc.setVisible(false);
			SortedSet autoprofs = (SortedSet)aPC.getAutoWeaponProfs();
			SortedSet profs = (SortedSet)aPC.getWeaponProfList();
			ArrayList selected = new ArrayList(profs.size());
			int bonuschoices = aPC.getBonusWeaponChoices();
			SortedSet bonusProfs = new TreeSet();
			for (Iterator i = aPC.getBonusWeaponProfs().iterator(); i.hasNext();)
			{
				String bprof = i.next().toString();
				if (!profs.contains(bprof))
					bonusProfs.add(bprof);
				if (profs.contains(bprof))
				{
					if (!autoprofs.contains(bprof))
						selected.add(bprof);
				}
			}
			Collections.sort(selected);
			lc.setSelectedList(selected);
			final int poolsize = (bonuschoices - selected.size());
			lc.setPool(poolsize);
			lc.setAvailableList(new ArrayList(bonusProfs));
			lc.show();

			aPC.getWeaponProfList();
			profs.addAll(lc.getSelectedList());
			aPC.getWeaponProfList().addAll(profs);
			updateCharacterInfo();
		}
	}

	/**
	 * Creates the FeatModel that will be used.
	 */
	protected void createModels()
	{
		if (availableModel == null)
			availableModel = new FeatModel(viewMode, true);
		else
			availableModel.resetModel(viewMode, true, false);
		if (availableSort!=null)
			availableSort.setRoot((PObjectNode)availableModel.getRoot());

		if (selectedModel == null)
			selectedModel = new FeatModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
		if (selectedSort!=null)
			selectedSort.setRoot((PObjectNode)selectedModel.getRoot());
	}

	private void addFeat(ActionEvent evt)
	{
		//final String aString = availableTable.getTree().getLastSelectedPathComponent().toString();
		String aString = null;
		Object temp = availableTable.getTree().getLastSelectedPathComponent();
		if (temp != null)
		{
			aString = temp.toString();
		}
		else
		{
			JOptionPane.showMessageDialog(null,
				"Somehow, no feat was selected. Try again.", pcgen.core.Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		final Feat aFeat = aPC.getFeatNamed(aString);
		final boolean pcHasIt = (aFeat != null);

		if (numFeatsField.getValue() == 0 && !pcHasIt)
		{
			JOptionPane.showMessageDialog(null, "You cannot select any more feats.", "PCGen", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
// we can only be here if the PC can add the feat
		try
		{
			aPC.setDirty(true);
			// modFeat(featName, adding_feat, adding_all_selections)
			aPC.modFeat(aString, true, false);
			aPC.setAggregateFeatsStable(false);
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(null, exc.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
		}

		boolean r[] = new boolean[availableTable.getTree().getRowCount()];
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
// Remember which rows are expanded
		for (int i = 0; i < r.length; i++)
			r[i] = availableTable.getTree().isExpanded(i);
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		createModels();
		// calling the following 2 functions causes an exception as
		// mousePressed returns, everything works ok and I'm not sure
		// what's causing the exception.	1/3/01 merton_monk@yahoo.com
		selectedTable.getColumnModel().getColumn(0).setHeaderValue("Feats ("+aPC.getFeatList().size()+")");
		availableTable.updateUI();
		selectedTable.updateUI();
//re-expand the rows (they were cleared out in the createModels() call)
		for (int i = 0; i < r.length; i++)
			if (r[i])
				availableTable.getTree().expandRow(i);
		for (int i = 0; i < s.length; i++)
			if (s[i])
				selectedTable.getTree().expandRow(i);
		numFeatsField.setValue(aPC.getFeats());
		rightButton.setEnabled(false);
	}

	private void removeFeat(ActionEvent evt)
	{
		String aString = selectedTable.getTree().getLastSelectedPathComponent().toString();
		aString = aString.substring(aString.lastIndexOf("|") + 1);

		try
		{
			aPC.setDirty(true);
			// modFeat(featName, adding_feat, adding_all_selections)
			aPC.modFeat(aString, false, false);
			aPC.setAggregateFeatsStable(false);
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(null, exc.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
		}
		boolean r[] = new boolean[availableTable.getTree().getRowCount()];
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
// Remember which rows are expanded
		for (int i = 0; i < r.length; i++)
			r[i] = availableTable.getTree().isExpanded(i);
		for (int i = 0; i < s.length; i++)
			s[i] = selectedTable.getTree().isExpanded(i);

		createModels();
		// calling the following 2 functions causes an exception as
		// mousePressed returns, everything works ok and I'm not sure
		// what's causing the exception.	1/3/01 merton_monk@yahoo.com
		availableTable.updateUI();
		selectedTable.updateUI();
//re-expand the rows (they were cleared out in the createModels() call)
		for (int i = 0; i < r.length; i++)
			if (r[i])
				availableTable.getTree().expandRow(i);
		for (int i = 0; i < s.length; i++)
			if (s[i])
				selectedTable.getTree().expandRow(i);
		numFeatsField.setValue(aPC.getFeats());
		leftButton.setEnabled(false);
	}


	//
	// TreeExpansionListener
	//

	/**
	 * Invoked when the tree has expanded.
	 */
	public void treeExpanded(TreeExpansionEvent te)
	{
	}

	/**
	 * Invoked when the tree has collapsed.
	 */
	public void treeCollapsed(TreeExpansionEvent te)
	{
	}

	/**
	 * Updates the reloadRow and path, this does not genernate a
	 * change event.
	 */
	protected void updateRow()
	{
	}


	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *	object.	This root object has a null <code>parent</code>.	All other objects
	 *	have a parent which points to a non-null object.	parent objects contain a list of
	 *	<code>children</code>, which are all the objects that point to it as their parent.
	 *	objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *	linked list).	nodes which have at least 1 child are not leafs. Leafs are like files
	 *	and non-leafs are like directories.
	 */
	public class FeatModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// Names of the columns.
		protected String[] cNames = {"Name", "Modified"};

		// Types of the columns.
		protected Class[] cTypes = {TreeTableModel.class, String.class};
		protected int modelType = 0; // availableModel

		/**
		 * Creates a FeatModel
		 */
		public FeatModel(int mode, boolean available)
		{
			super(null);
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the FeatModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			if (!available)
				modelType = 1;
			switch (mode)
			{
				case InfoFeats.VIEW_TYPENAME:
					Globals.debugPrint(modelType + " children=" + InfoFeats.typeRoot.getChildren()[0].getChildren().length);
					setRoot(InfoFeats.typeRoot);
					Globals.debugPrint(modelType + " children=" + ((PObjectNode)root).getChildren()[0].getChildren().length);
					for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
					{
						final Feat aFeat = (Feat)fI.next();
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
						int hasIt = 0;
						if (aPC.hasFeat(aFeat.getName()))
							hasIt = 1; // in PC's chosen list
						else if (aPC.hasFeatAutomatic(aFeat.getName()))
							hasIt = 2; // PC has it automatically
						else if (aPC.hasFeatVirtual(aFeat.getName()))
							hasIt = 3; // PC has it virtually
						// if putting together availableModel, use virtual or non-acquired feats
						// for selectedModel, use virtual, auto and chosen
						PObjectNode c[] = ((PObjectNode)root).getChildren();
						if (available && (hasIt == 3 || hasIt == 0 || aFeat.isMultiples()) ||
							(!available && hasIt > 0 && accept(aFeat)))
						{
							for (int i = 0; i < c.length; i++)
							{
								if (aFeat.isType(c[i].toString()))
								{
									PObjectNode aFN = new PObjectNode();
									if (modelType == 1)
										aFN.setCheckFeatState(true);
									aFN.setParent(c[i]);
									if (!available)
									{
										final Feat bFeat = aPC.getFeatNamed(aFeat.getName());
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
									c[i].addChild(aFN);
								}
							}
						}
					}
					break;
				case InfoFeats.VIEW_NAMEONLY:
					root = new PObjectNode();
					for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
					{
						final Feat aFeat = (Feat)fI.next();
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
						int hasIt = 0;
						if (aPC.hasFeat(aFeat.getName()))
							hasIt = 1;
						else if (aPC.hasFeatAutomatic(aFeat.getName()))
							hasIt = 2;
						else if (aPC.hasFeatVirtual(aFeat.getName()))
							hasIt = 3;
						// if putting together availableModel, use virtual or non-acquired feats
						// for selectedModel, use virtual, auto and chosen
						if (available && (hasIt == 3 || hasIt == 0) ||
							(!available && hasIt > 0 && accept(aFeat)))
						{
							PObjectNode aFN = new PObjectNode();
							aFN.setParent((PObjectNode)root);
							if (modelType == 1)
								aFN.setCheckFeatState(true);
							if (!available)
							{
								final Feat bFeat = aPC.getFeatNamed(aFeat.getName());
								if (bFeat != null)
									aFN.setItem(bFeat);
							}
							if (aFN.getItem() == null)
								aFN.setItem(aFeat);
							((PObjectNode)root).addChild(aFN);
						}
					}
					break;
				case InfoFeats.VIEW_PREREQTREE:
					if (preReqTreeRoot == null && available)
					{
						preReqTreeRoot = new PObjectNode();
					}
					if (available)
					{
						setRoot(preReqTreeRoot);
//						System.out.println(((PObjectNode)preReqTreeRoot).getChildren().length);
					}
					else
						setRoot(new PObjectNode());
					ArrayList aList = new ArrayList();
					ArrayList fList = new ArrayList();
					if (available)
					{
//  						fList = (ArrayList)Globals.getFeatList().clone();
						// make filters work ;-)
						Feat aFeat;
						for (Iterator it = Globals.getFeatList().iterator(); it.hasNext();)
						{
							aFeat = (Feat)it.next();
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
							aFeat = (Feat)it.next();
							if (accept(aFeat))
							{
								fList.add(aFeat);
							}
						}
					}

					for (int i = 0; i < fList.size(); i++)
					{
						final Feat aFeat = (Feat)fList.get(i);
						if (!aFeat.hasPreReqOf(1, "PREFEAT:"))
						{
							fList.remove(aFeat);
							aList.add(aFeat);
							i--; // to counter increment
						}
					}

					PObjectNode[] cc = new PObjectNode[aList.size()];
					for (int i = 0; i < aList.size(); i++)
					{
						cc[i] = new PObjectNode();
						cc[i].setItem(aList.get(i));
						cc[i].setParent((PObjectNode)root);
						if (modelType == 1)
							cc[i].setCheckFeatState(true);
					}
					((PObjectNode)root).setChildren(cc, false);
					int loopmax = 6; // only go 6 levels...
					while (fList.size() > 0 && loopmax-- > 0)
					{
						for (int i = 0; i < fList.size(); i++)
						{
							final Feat aFeat = (Feat)fList.get(i);
							int placed = 0;
							for (int j = 0; j < ((PObjectNode)root).getChildren().length; j++)
							{
								final PObjectNode po = (PObjectNode)((PObjectNode)root).getChildren()[j];
								ArrayList pList = aFeat.getPreReqs();
								for (int pi = pList.size() - 1; pi >= 0; pi--)
								{
									String pString = (String)pList.get(pi);
									if (!pString.startsWith("PREFEAT:"))
										pList.remove(pString);
								}
								placed = placedThisFeatInThisTree(aFeat, po, aFeat.preReqStringsForList(pList), 0);
								if (placed > 0)
									break;
							}
							if (placed == 2) // i.e. tree match
							{
								fList.remove(aFeat);
								i--; // since we're incrementing in the for loop
							}
						}
					}
					if (fList.size() > 0)
					{
						PObjectNode po = new PObjectNode();
						po.setItem("Other");
						cc = new PObjectNode[fList.size()];
						for (int i = 0; i < fList.size(); i++)
						{
							cc[i] = new PObjectNode();
							cc[i].setItem(fList.get(i));
							cc[i].setParent(po);
							if (modelType == 1)
								cc[i].setCheckFeatState(true);
						}
						po.setChildren(cc, false);
						((PObjectNode)root).addChild(po);
					}
					break;

/*				case InfoFeats.VIEW_SOURCENAME:
					break;
				case InfoFeats.VIEW_SOURCETYPENAME:
					break;
				case InfoFeats.VIEW_TYPESOURCENAME:
					break;
*/
			}
			if (!newCall && ((PObjectNode)root).getChildren().length > 0)
				fireTreeNodesChanged(root, ((PObjectNode)root).getChildren(), null, null);
			Globals.debugPrint(modelType + " children=" + InfoFeats.typeRoot.getChildren()[0].getChildren().length);
		}

		/** "There can be only one!" There must be a root object, though it can be hidden
		 to make it's existence basically a convenient way to keep track of the objects
		 */
		public void setRoot(PObjectNode aNode)
		{
			root = aNode.clone();
		}

		public Object getRoot()
		{
			return (PObjectNode)root;
		}

		int placedThisFeatInThisTree(final Feat aFeat, PObjectNode po, String preReqString, int level)
		{
			int placed = 0; // assume we don't belong in this tree
			final Feat bFeat = (Feat)po.getItem(); // must be a Feat
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
					if (thisisit == true)
					{
						PObjectNode p = new PObjectNode();
						p.setItem(aFeat);
						p.setParent(po);
						po.addChild(p);
						if (modelType == 1)
							p.setCheckFeatState(true);
						return 2; // successfully added
					}
					else if (trychildren == true)
					{
						int b = a + 5 + preReqString.substring(a + 5).indexOf(bFeat.getName());
						int len = bFeat.getName().length() + 1;
						if (preReqString.length() > b + len && preReqString.substring(b + len).startsWith(","))
							len++;
						String aString = preReqString.substring(0, b);
						if (preReqString.length() > b + len)
							aString = aString + preReqString.substring(b + len);
						for (int i = 0; i < po.getChildren().length; i++)
						{
							int j = placedThisFeatInThisTree(aFeat, po.getChildren()[i], aString, level + 1);
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
			return cNames.length;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			if (column == 0)
			{
				if (modelType==0)
					return "Feat";
				return "Feat ("+aPC.getFeatList().size()+")";
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
			PObjectNode fn = (PObjectNode)node;
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
			}
			return null;
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

	}

	/**
	 * implementation of Filterable interface
	 */
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllFeatFilters(this);
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
