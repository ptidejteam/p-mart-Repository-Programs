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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
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
 * the selection/de-selection of feats.  This class will also handle displaying
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

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.swing.BorderFactory;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.TreePath;
import pcgen.core.Feat;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

public class InfoFeats extends JPanel
{


	protected FeatModel availableModel = null;  // Model for the JTreeTable.
	protected FeatModel selectedModel = null;   // Model for the JTreeTable.
	protected JTreeTable availableTable;  // the available Feats
	protected JTreeTable selectedTable;   // the selected Feats
	protected WholeNumberField numFeatsField = new WholeNumberField(0, 3);
	protected JComboBox viewComboBox = new JComboBox();
	protected JTextArea infoText = new JTextArea();
	protected JTextArea weaponText = new JTextArea();
	protected PlayerCharacter aPC = null;
	final static int TYPENAME = 0; // view mode for Type->Name
	final static int NAMEONLY = 1; // view mode for Name (essentially a JTable)
	final static int SOURCENAME = 2; // view mode for Source->Name
	final static int SOURCETYPENAME = 3; // view mode for Source->Type->Name
	final static int TYPESOURCENAME = 4; // view mode for Type->Source->Name
	static int viewMode = 0; // keep track of what view mode we're in
	JButton leftButton;
	JButton rightButton;
	JLabel infoLabel = new JLabel();
	JScrollPane infoScroll = new JScrollPane();
	static boolean needsUpdate = true; 

	public InfoFeats()
	{

		aPC = Globals.getCurrentPC();
		initComponents();
		initActionListeners();
	}

	void initComponents()
	{
		createModels();
// create available table of feats
		createTreeTables();

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gb);

// Row #0
		JLabel aLabel = new JLabel("Available");
// buildConstraints(c, x, y, colums_wide, rows_high, weightx, weighty)
		buildConstraints(c, 0, 0, 1, 1, 0.48, 0.05);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		gb.setConstraints(aLabel, c);
		this.add(aLabel);
		aLabel = new JLabel("");
		buildConstraints(c, 1, 0, 1, 1, 0.04, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gb.setConstraints(aLabel, c);
		this.add(aLabel);
		aLabel = new JLabel("Selected");
		buildConstraints(c, 2, 0, 1, 1, 0.48, 0.0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		gb.setConstraints(aLabel, c);
		this.add(aLabel);

// Row #1
		JPanel aPanel = new JPanel();
		buildConstraints(c, 0, 1, 1, 3, 0.0, 0.0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		JScrollPane jsp = new JScrollPane(availableTable);
		availableTable.setToolTipText("Feats in red are those for which you don't qualify.");
		gb.setConstraints(aPanel, c);
		aPanel.setLayout(new BorderLayout());
		aPanel.add(jsp);
		JPanel bPanel = new JPanel();

// begin View/Feats Panel
		bPanel.setLayout(new FlowLayout());
		aPanel.add(bPanel,BorderLayout.SOUTH);
		viewComboBox = new JComboBox();
		viewComboBox.addItem("Type/Name");
		viewComboBox.addItem("Name");
		viewComboBox.setToolTipText("You can change how the feats in the Available and Selected Tables are listed - either by name or in a directory-like structure.");
		viewComboBox.setSelectedIndex(0);
		bPanel.add(viewComboBox);
		bPanel.add(new JLabel("Feats remaining"));
		if (aPC != null)
			numFeatsField.setValue(aPC.getFeats());
		bPanel.add(numFeatsField);
		numFeatsField.setToolTipText("How many feats you have left to choose (editable).");
// end View/Feats Panel
		this.add(aPanel);

		aPanel = new JPanel();
		buildConstraints(c, 1, 1, 1, 1, 0.0, 0.45);
		gb.setConstraints(aPanel, c);
		this.add(aPanel);

// begin panel with left and right buttons on it
		GridBagLayout gbl = new GridBagLayout();
		aPanel.setLayout(gbl);
		buildConstraints(c, 0, 0, 3, 1, 0.0, 0.2);
		aLabel = new JLabel("");
		gbl.setConstraints(aLabel, c);
		aPanel.add(aLabel);
		aLabel = new JLabel("");
		buildConstraints(c, 0, 1, 1, 4, 0.2, 0.8);
		gbl.setConstraints(aLabel, c);
		aPanel.add(aLabel);


		ImageIcon newImage = new ImageIcon(getClass().getResource("left.gif"));
		leftButton = new JButton(newImage);
		leftButton.setOpaque(true);
		leftButton.setToolTipText("Click to remove the selected feat from the Selected list of feats");
		leftButton.setEnabled(false);
		buildConstraints(c, 1, 1, 1, 1, .6, .2);
		gbl.setConstraints(leftButton, c);
		aPanel.add(leftButton);
		aLabel = new JLabel("");
		buildConstraints(c, 2, 1, 1, 4, 0.2, 0.8);
		gbl.setConstraints(aLabel, c);
		aPanel.add(aLabel);

		aLabel = new JLabel("");
		buildConstraints(c, 1, 2, 1, 1, .2, .2);
		gbl.setConstraints(aLabel, c);
		aPanel.add(aLabel);
		newImage = new ImageIcon(getClass().getResource("right.gif"));
		rightButton = new JButton(newImage);
		rightButton.setToolTipText("Click to add the selected feat from the Available list of feats");
		rightButton.setEnabled(false);
		buildConstraints(c, 1, 3, 1, 1, 0.6, 0.2);
		gbl.setConstraints(rightButton, c);
		aPanel.add(rightButton);
		aLabel = new JLabel("");
		buildConstraints(c, 1, 4, 1, 1, 0.2, .2);
		gbl.setConstraints(aLabel, c);
		aPanel.add(aLabel);
// end panel with left and right buttons on it

// completing Row #1
		buildConstraints(c, 2, 1, 1, 1, 0.0, 0.0);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		jsp = new JScrollPane(selectedTable);
		selectedTable.setToolTipText("Feats in red are those for which you don't qualify. Yellow are automatic feats and magenta feats are virtual feats.");
		gb.setConstraints(jsp, c);
		this.add(jsp);

// Row #2
		aLabel = new JLabel("");
		buildConstraints(c, 1, 2, 1, 3, 0.0, 0.0);
		c.anchor = GridBagConstraints.SOUTH;
		gb.setConstraints(aLabel, c);
		this.add(aLabel);
		aPanel = new JPanel();
		buildConstraints(c, 2, 2, 1, 2, 0.0, 0.2);
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		gb.setConstraints(aPanel, c);
		aPanel.setLayout(new BorderLayout());
		this.add(aPanel);

// begin WeaponProfs panel
		bPanel = new JPanel();
		bPanel.setLayout(new FlowLayout());
		aLabel = new JLabel(" Weapon Profs");
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
		aPanel.add(bPanel,BorderLayout.NORTH);
// end WeaponProfs panel

// Row #3
		weaponText.setLineWrap(true);
		weaponText.setWrapStyleWord(true);
		weaponText.setEditable(false);
		JScrollPane weaponScroll = new JScrollPane();
		weaponScroll.setViewportView(weaponText);
//		buildConstraints(c, 2, 3, 1, 2, 0.0, 0.0);
//		c.fill = GridBagConstraints.BOTH;
//		gb.setConstraints(weaponScroll, c);
//		this.add(weaponScroll);
		aPanel.add(weaponScroll);

// Row #4 - not needed
//		c.fill = GridBagConstraints.NONE;
//		c.anchor = GridBagConstraints.CENTER;
//		aLabel = new JLabel("");
//		buildConstraints(c, 0, 4, 1, 1, 0.0, 0.1);
//		gb.setConstraints(aLabel, c);
//		this.add(aLabel);

// Row #4
		TitledBorder title = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Info");
		title.setTitleJustification(TitledBorder.CENTER);
		infoScroll.setBorder(title);


		infoScroll.setViewportView(infoLabel);
		infoScroll.setToolTipText("Any requirements you don't meet are in italics.");
		buildConstraints(c, 0, 4, 3, 1, 0.0, 0.2);
		c.fill = GridBagConstraints.BOTH;
		gb.setConstraints(infoScroll, c);
		this.add(infoScroll);
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
			createModels();
			availableTable.updateUI();
			selectedTable.updateUI();
		}
	}


	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		requestDefaultFocus();
		PCGen_Frame1.getStatusBar().setText("Feats are color coded: Red = Character does not qualify; Yellow = Automatic Feat;" +
			" Magenta = Virtual Feat");
		updateCharacterInfo();
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

	/**
	 * The creates the JTreeTables for the available feats and selected feats.
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
					final String aString = availableTable.getTree().getLastSelectedPathComponent().toString();
					final Feat aFeat = Globals.getFeatNamed(aString.substring(aString.lastIndexOf("|") + 1));
					rightButton.setEnabled(!aString.startsWith("|") && aFeat != null);
					if (aFeat != null)
					{
						String bString = "<html><b>" + aFeat.getName() + "</b> &nbsp;TYPE:" + aFeat.getType();
						if (aFeat.isMultiples())
							bString = bString+" &nbsp;Can be taken more than once";
						if (aFeat.isStacks())
							bString = bString+" &nbsp;Stacks";
						final String cString =  aFeat.preReqHTMLStrings(false);
						if (cString.length()>0)
							bString = bString+" &nbsp;<b>Requirements</b>:"+cString;
						bString = bString + " &nbsp;<b>Description</b>:" + aFeat.getDescription() + "</html>";
						bString = PCGen_Frame1.breakupString(bString,(int)infoScroll.getSize().getWidth());
						infoLabel.setText(bString);
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
					if (e.getClickCount() == 1 && selPath!=null)
						tree.setSelectionPath(selPath);
					else if (e.getClickCount() == 2) {
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
		selectedTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String aString = btree.getLastSelectedPathComponent().toString();
					aString = aString.substring(aString.lastIndexOf("|") + 1);
					final Feat aFeat = Globals.getFeatNamed(aString);
					final boolean flag = (aFeat != null && aPC.hasFeatAutomatic(aString));
					leftButton.setEnabled(aFeat != null && !flag);
					if (aFeat != null) {
						String bString = "<html><b>" + aFeat.getName() + "</b> &nbsp;TYPE:" + aFeat.getType();
						if (aFeat.isMultiples())
							bString = bString+" &nbsp;Can be taken more than once";
						if (aFeat.isStacks())
							bString = bString+" &nbsp;Stacks";
						final String cString =  aFeat.preReqHTMLStrings(false);
						if (cString.length()>0)
							bString = bString+" &nbsp;<b>Requirements</b>:"+cString;
						bString = bString + " &nbsp;<b>Description</b>:" + aFeat.getDescription() +
						 " &nbsp;<b>Source</b>:"+aFeat.getSource()+"</html>";
						bString = PCGen_Frame1.breakupString(bString,(int)infoScroll.getSize().getWidth());
						infoLabel.setText(bString);
					}
				}
			}
		});
		ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				final int selRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath selPath = btree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 1)
						btree.setSelectionPath(selPath);
					else if (e.getClickCount() == 2) {
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
		if (aPC == null || needsUpdate==false)
			return;
		aPC.setAggregateFeatsStable(false);
		aPC.setAutomaticFeatsStable(false);
		aPC.setVirtualFeatsStable(false);
		JViewport aPort = infoScroll.getColumnHeader();
		if (aPort != null)
			aPort.setVisible(false);
		aPC.getAutoWeaponProfs();
		SortedSet weaponProfs = aPC.getWeaponProfList();
		if (weaponProfs.size() > 0)
			weaponText.setText
				(Globals.stringForList(weaponProfs.iterator(), ", "));
		else
			weaponText.setText("None");
		createModels();
		availableTable.updateUI();
		selectedTable.updateUI();
		numFeatsField.setValue(aPC.getFeats());
		needsUpdate=false;
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
			// System.out.println(poolsize);
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
		if (selectedModel == null)
			selectedModel = new FeatModel(viewMode, false);
		else
			selectedModel.resetModel(viewMode, false, false);
	}

	private void addFeat(ActionEvent evt)
	{
		final String aString = availableTable.getTree().getLastSelectedPathComponent().toString();
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
		// what's causing the exception.  1/3/01 merton_monk@yahoo.com
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
		final Feat aFeat = aPC.getFeatNamed(aString);

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
		// what's causing the exception.  1/3/01 merton_monk@yahoo.com
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
	 *  object.  This root object has a null <code>parent</code>.  All other objects
	 *  have a parent which points to a non-null object.  parent objects contain a list of
	 *  <code>children</code>, which are all the objects that point to it as their parent.
	 *  objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *  linked list).  nodes which have at least 1 child are not leafs. Leafs are like files
	 *  and non-leafs are like directories.
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
			root = new PObjectNode();
			if (!available)
				modelType = 1;
			switch (mode)
			{
				case InfoFeats.TYPENAME:
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
					PObjectNode[] c = new PObjectNode[aList.size()];
					for (int i = 0; i < aList.size(); i++)
					{
						c[i] = new PObjectNode();
						c[i].setItem(aList.get(i).toString());
						c[i].setParent((PObjectNode)root);
					}
					((PObjectNode)root).setChildren(c, false);
					for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
					{
						final Feat aFeat = (Feat)fI.next();
						// in the availableTable, if filtering out unqualified feats
						// ignore any feats the PC doesn't qualify for
						if (available && Filter.showQualifiedOnly == true && !aFeat.passesPreReqTests())
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
							(!available && hasIt > 0))
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
//					pruneChildless();
					break;
				case InfoFeats.NAMEONLY:
					for (Iterator fI = Globals.getFeatList().iterator(); fI.hasNext();)
					{
						final Feat aFeat = (Feat)fI.next();
						// in the availableTable, if filtering out unqualified feats
						// ignore any feats the PC doesn't qualify for
						if (available && Filter.showQualifiedOnly == true && !aFeat.passesPreReqTests())
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
							(!available && hasIt > 0))
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
				case InfoFeats.SOURCENAME:
					break;
				case InfoFeats.SOURCETYPENAME:
					break;
				case InfoFeats.TYPESOURCENAME:
					break;
			}
			if (!newCall && ((PObjectNode)root).getChildren().length > 0)
				fireTreeNodesChanged(root, ((PObjectNode)root).getChildren(), null, null);
		}

		// "There can be only one!" There must be a root object, though it can be hidden
		// to make it's existence basically a convenient way to keep track of the objects
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		private void pruneChildless() {
			PObjectNode[] oldkids = ((PObjectNode)root).getChildren();
			ArrayList aList = new ArrayList();
			for(int i=0;i<oldkids.length;i++)
				if (oldkids[i].getChildren().length>0)
					aList.add(oldkids[i]);
			PObjectNode[] newkids = new PObjectNode[aList.size()];
			for(int i=0;i<aList.size();i++)
				newkids [i]=(PObjectNode)aList.get(i);
			((PObjectNode)root).setChildren(newkids,false);
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
				return "Feat";
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
}