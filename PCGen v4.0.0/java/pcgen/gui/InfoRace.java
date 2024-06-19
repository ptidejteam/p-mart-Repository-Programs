/**
 * InfoRace.java
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
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * Created on May 1, 2001, 5:57 PM
 * ReCreated on Feb 22, 2002 7:45 AM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:47:12 $
 *
 **/

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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import pcgen.core.Globals;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;

/**
 *  <code>InfoRace</code> creates a new tabbed panel
 *  with all the race and template information on it
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com)
 * @version $Revision: 1.1 $
 **/
class InfoRace extends FilterAdapterPanel
{
	protected RaceModel raceModel = null;	// Model for JTreeTable
	private JTreeTable raceTable;		// Races
	private JTreeTableSorter raceSort = null;

	private JLabel raceText = new JLabel();
	private JLabel raceTextLabel = new JLabel("Selected Race:");
	private JLabel sortLabel = new JLabel("Sort Races");
	private JLabelPane infoLabel = new JLabelPane();

	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();
	private Border etched;
	private TitledBorder titled;
	private FlippingSplitPane splitPane;
	private FlippingSplitPane asplit;
	private FlippingSplitPane bsplit;

	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;
	protected JComboBox viewComboBox = new JComboBox();
	private int viewMode = 0;

	// if you change these, you also have to change
	// the case statement in the RaceModel declaration
	private static final int COL_NAME = 0;
	private static final int COL_STAT = 1;
	private static final int COL_PRE = 2;
	private static final int COL_SIZE = 3;
	private static final int COL_MOVE = 4;
	private static final int COL_VISION = 5;
	private static final int COL_CLASS = 6;
	private static final int COL_LEVEL = 7;

	private final JLabel avaLabel = new JLabel("Available Templates");
	private final JLabel selLabel = new JLabel("Selected Templates");
	private JLabel jLabelFilterPCs;
	private JTableEx allTemplatesTable;
	private JTableEx currentTemplatesTable;
	private JScrollPane allTemplatesPane;
	private JScrollPane currentTemplatesPane;
	private JPanel jPanelCtrl;
	private JButton selButton = new JButton("Select");
	private JButton leftButton;
	private JButton rightButton;
	private JComboBox jcbFilterPCs;
	protected AllTemplatesTableModel allTemplatesDataModel = new AllTemplatesTableModel();
	protected PCTemplatesTableModel currentTemplatesDataModel = new PCTemplatesTableModel();
	protected TableSorter sortedAllTemplatesModel = new TableSorter();
	protected TableSorter sortedCurrentTemplatesModel = new TableSorter();

	// the list from which to pull the templates to use
	private ArrayList currentPCdisplayTemplates = new ArrayList(0);

	private TreePath selPath;
	private int selRow;
	private boolean hasBeenSized = false;
	private boolean needsUpdate = true;
	protected static PlayerCharacter aPC = null;

	private class RacePopupMenu extends JPopupMenu
	{
		private class RaceActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddRaceActionListener extends RaceActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selButton(evt);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddRaceActionListener(), "Select", (char)0, accelerator, "Select Race", "Add16.gif", true);
		}

		RacePopupMenu()
		{
			RacePopupMenu.this.add(createAddMenuItem("Select", "control EQUALS"));
		}
	}

	private class RacePopupListener extends MouseAdapter
	{
		private JTree tree;
		private RacePopupMenu menu;

		RacePopupListener(JTreeTable treeTable, RacePopupMenu aMenu)
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
		treeTable.addMouseListener(new RacePopupListener(treeTable, new RacePopupMenu()));
	}

	public InfoRace()
	{
		// do not change/remove this as we use the component's name
		// to save component specific settings
		setName("Race");

		// get the current PC
		aPC = Globals.getCurrentPC();

		initComponents();

		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	public void setNeedsUpdate(boolean flag)
	{
		needsUpdate = flag;
	}

	/**
	 * Set up GridBag Constraints
	 **/
	private void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy)
	{
		gbc.gridx = gx;
		gbc.gridy = gy;
		gbc.gridwidth = gw;
		gbc.gridheight = gh;
		gbc.weightx = wx;
		gbc.weighty = wy;
	}

	/**
	 * This method is called from within the
	 * constructor to initialize the form
	 **/
	private void initComponents()
	{
		//
		// View List Sanity check
		//
		int iView = SettingsHandler.getRaceTab_ListMode();
		if ((iView >= GuiConstants.INFORACE_VIEW_NAME) && (iView <= GuiConstants.INFORACE_VIEW_SOURCE))
			viewMode = iView;
		SettingsHandler.setRaceTab_ListMode(viewMode);

		// make sure the current PC is set
		aPC = Globals.getCurrentPC();

		viewComboBox.addItem("Name   ");
		viewComboBox.addItem("Type   ");
		viewComboBox.addItem("Source ");
		viewComboBox.setSelectedIndex(viewMode);

		// initialize the raceModel
		raceModel = new RaceModel(viewMode);

		// create the tree's from the raceModel
		createTreeTables();

		//
		// build the topPane which will contain leftPane and rightPane
		// leftPane will have a panel and a scrollregion
		// rightPane will have a single panel
		//
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

		// first build the left pane
		// it will have all the races

		buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		aPanel.setBorder(BorderFactory.createEtchedBorder());

		aPanel.add(sortLabel);
		aPanel.add(viewComboBox);

		raceText.setPreferredSize(new Dimension(120, 25));
		raceText.setBorder(BorderFactory.createEtchedBorder());
		raceText.setHorizontalAlignment(JTextField.CENTER);
		raceText.setBackground(Color.black);
		aPanel.add(raceTextLabel);
		aPanel.add(raceText);
		aPanel.add(selButton);
		leftPane.add(aPanel);

		// build the available races panel
		buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.ipadx = 1;
		JScrollPane scrollPane = new JScrollPane(raceTable);
		gridbag.setConstraints(scrollPane, c);

		raceTable.getColumnModel().getColumn(3).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		raceTable.getColumnModel().getColumn(7).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));

		leftPane.add(scrollPane);

		//
		// now do the right pane
		//

		gridbag = new GridBagLayout();
		c = new GridBagConstraints();
		rightPane.setLayout(gridbag);

		buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.EAST;
		JScrollPane rScroll = new JScrollPane();
		gridbag.setConstraints(rScroll, c);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Race Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		rScroll.setBorder(title1);
		infoLabel.setBackground(topPane.getBackground());
		rScroll.setViewportView(infoLabel);
		rightPane.add(rScroll);

		//
		// ------- build bottom panel -------
		// this has all the Template stuff in it
		//
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

		// create left hand pane (all templates)
		buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);

		avaLabel.setPreferredSize(new Dimension(120, 20));
		aPanel.add(avaLabel);

		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		rightButton = new JButton(newImage);
		Utility.setDescription(rightButton, "Click to add selected Template");
		rightButton.setEnabled(true);
		aPanel.add(rightButton);

		selLabel.setPreferredSize(new Dimension(100, 20));

		bLeftPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		allTemplatesTable = new JTableEx();
		allTemplatesPane = new JScrollPane(allTemplatesTable);
		gridbag.setConstraints(allTemplatesPane, c);

		sortedAllTemplatesModel.setModel(allTemplatesDataModel);
		allTemplatesTable.setModel(sortedAllTemplatesModel);
		allTemplatesPane.setViewportView(allTemplatesTable);
		allTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		bLeftPane.add(allTemplatesPane);

		allTemplatesTable.getColumnModel().getColumn(0).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		allTemplatesTable.getColumnModel().getColumn(2).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));


		// now build the right hand pane (selected templates)
		buildConstraints(c, 0, 0, 1, 1, 1, 1);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		JPanel bPanel = new JPanel();
		gridbag.setConstraints(bPanel, c);

		bPanel.add(selLabel);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove selected Template");
		leftButton.setEnabled(true);
		bPanel.add(leftButton);
		bRightPane.add(bPanel);

		buildConstraints(c, 0, 1, 1, 1, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		currentTemplatesPane = new JScrollPane();
		gridbag.setConstraints(currentTemplatesPane, c);

		currentTemplatesTable = new JTableEx();
		sortedCurrentTemplatesModel.setModel(currentTemplatesDataModel);
		sortedCurrentTemplatesModel.addMouseListenerToHeaderInTable(currentTemplatesTable);

		currentTemplatesTable.setModel(sortedCurrentTemplatesModel);
		currentTemplatesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		currentTemplatesTable.setDoubleBuffered(false);
		currentTemplatesPane.setViewportView(currentTemplatesTable);
		bRightPane.add(currentTemplatesPane);

		// now split the top and bottom panels
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, topPane, botPane);
		bsplit.setOneTouchExpandable(true);
		bsplit.setDividerSize(10);

		// now add all the panes (centered of course)
		this.setLayout(new BorderLayout());
		this.add(bsplit, BorderLayout.CENTER);

		// add the sorter so that clicking on the TableHeader
		// actualy does something
		raceSort = new JTreeTableSorter(raceTable, (PObjectNode)raceModel.getRoot(), raceModel);
	}

	protected void createTreeTables()
	{
		raceTable = new JTreeTable(raceModel);
		raceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JTree atree = raceTable.getTree();
		atree.setRootVisible(false);
		atree.setShowsRootHandles(true);
		atree.setCellRenderer(new LabelTreeCellRenderer());

		raceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
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
						return;
					}

					PObjectNode fNode = (PObjectNode)temp;
					if (fNode.getItem() instanceof Race)
					{
						Race aRace = (Race)fNode.getItem();
						setInfoLabelText(aRace);
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
						atree.setSelectionPath(avaPath);
					}
					else if (e.getClickCount() == 2)
					{
						selButton(null);
					}
				}
			}
		};
		atree.addMouseListener(aml);

		// create the rightclick popup menus
		hookupPopupMenu(raceTable);
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
					SettingsHandler.setPCGenOption("InfoRace.splitPane", s);
				s = asplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoRace.asplit", s);
				s = bsplit.getDividerLocation();
				if (s > 0)
					SettingsHandler.setPCGenOption("InfoRace.bsplit", s);
			}
		});
		selButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				selButton(evt);
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				removeTemplate(evt);
			}
		});
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addTemplate(evt);
			}
		});
		viewComboBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewComboBoxActionPerformed(evt);
			}
		});
	}

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setRaceTab_ListMode(viewMode);
			createModel();
			raceTable.updateUI();
		}
	}

	/**
	 * selectes the race
	 **/
	private void selButton(ActionEvent evt)
	{
		TreePath aPath = raceTable.getTree().getSelectionPath();

		if (aPath == null)
			return;

		Object endComp = aPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode)endComp;

		if (!(fNode.getItem() instanceof Race))
			return;

		Race aRace = (Race)fNode.getItem();
		if (aRace == null)
			return;

		if (!aRace.equals(aPC.getRace()))
		{
			aPC.setRace(aRace);
			Globals.getRootFrame().forceUpdate_PlayerTabs();
			Globals.getRootFrame().forceUpdate_InfoClasses();
			Globals.getRootFrame().forceUpdate_InfoFeats();
			Globals.getRootFrame().forceUpdate_InfoSkills();
			Globals.getRootFrame().forceUpdate_InfoSpells();
			if (aPC.getRace().hitDice() != 0)
			{
				aPC.getRace().rollHP();
			}
			allTemplatesDataModel.updateModel();
			currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
			raceText.setText(aPC.getRace().piString());
			raceText.setMinimumSize(new Dimension(120, 25));
			setInfoLabelText(aPC.getRace());
		}
	}

	/**
	 * This is called when the tab is shown
	 **/
	private void formComponentShown(ComponentEvent evt)
	{
		requestFocus();
		Globals.getRootFrame().getStatusBar().setText("Select a Race and desired Templates.");
		updateCharacterInfo();
		int width;
		int s = splitPane.getDividerLocation();
		int t = bsplit.getDividerLocation();
		int u = asplit.getDividerLocation();
		if (!hasBeenSized)
		{
			s = SettingsHandler.getPCGenOption("InfoRace.splitPane", (int)(InfoRace.this.getSize().getWidth() * 75.0 / 100.0));
			t = SettingsHandler.getPCGenOption("InfoRace.bsplit", (int)(InfoRace.this.getSize().getHeight() - 120));
			u = SettingsHandler.getPCGenOption("InfoRace.asplit", (int)(InfoRace.this.getSize().getWidth() * 75.0 / 100.0));

			// set the prefered width on raceTable
			for (int i = 0; i < raceTable.getColumnCount(); i++)
			{
				TableColumn sCol = raceTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Race", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(raceTable, "Race", i));
			}
			// set the prefered width on allTemplatesTable
			for (int i = 0; i < allTemplatesTable.getColumnCount(); i++)
			{
				TableColumn sCol = allTemplatesTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("Tamplate", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(allTemplatesTable, "Tamplate", i));
			}
			if (s > 0)
				hasBeenSized = true;
		}
		if (s > 0)
		{
			splitPane.setDividerLocation(s);
			SettingsHandler.setPCGenOption("InfoRace.splitPane", s);
		}
		if (t > 0)
		{
			bsplit.setDividerLocation(t);
			SettingsHandler.setPCGenOption("InfoRace.bsplit", t);
		}
		if (u > 0)
		{
			asplit.setDividerLocation(u);
			SettingsHandler.setPCGenOption("InfoRace.asplit", u);
		}
	}

	public void forceUpdate()
	{
		final PCGen_Frame1 rootFrame = (PCGen_Frame1)Globals.getRootFrame();
		rootFrame.featList_Changed();
		rootFrame.hpTotal_Changed();
		Globals.getRootFrame().forceUpdate_PlayerTabs();
		Globals.getRootFrame().forceUpdate_InfoSkills();
		Globals.getRootFrame().forceUpdate_InfoSpells();
		Globals.getRootFrame().forceUpdate_InfoDomain();
		Globals.getRootFrame().forceUpdate_InfoInventory();
	}

	/**
	 * This recalculates the states of everything based
	 * upon the currently selected character
	 **/
	public void updateCharacterInfo()
	{
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC != aPC)
			needsUpdate = true;
		aPC = bPC;
		if (aPC == null || !needsUpdate)
			return;

		try
		{
			raceText.setText(aPC.getRace().piString());
			raceText.setMinimumSize(new Dimension(120, 25));
			setInfoLabelText(aPC.getRace());
		}
		catch (Exception exc)
		{
			raceText.setText("");
			setInfoLabelText(null);
		}
		allTemplatesDataModel.updateModel();
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
		createModel();
		raceTable.updateUI();

		needsUpdate = false;
	}

	private void addTemplate(ActionEvent evt)
	{
		if (allTemplatesTable.getSelectedRowCount() <= 0)
			return;
		aPC.setDirty(true);
		PCTemplate theTmpl = allTemplatesDataModel.get(sortedAllTemplatesModel.getRowTranslated(allTemplatesTable.getSelectedRow()));
		if ((theTmpl != null) && theTmpl.isQualified())
		{
			PCTemplate aTmpl = aPC.getTemplateNamed(theTmpl.getName());
			if (aTmpl == null)
			{
				aPC.addTemplate(theTmpl);
				forceUpdate();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Already have that template.");
			}
		}
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	private void removeTemplate(ActionEvent evt)
	{
		if (currentTemplatesTable.getSelectedRowCount() <= 0)
			return;
		aPC.setDirty(true);
		PCTemplate theTmpl = (PCTemplate)currentPCdisplayTemplates.get(sortedCurrentTemplatesModel.getRowTranslated(currentTemplatesTable.getSelectedRow()));
		if (!theTmpl.isRemovable())
		{
			JOptionPane.showMessageDialog(null, "That Template is not Removable", "PCGen", JOptionPane.ERROR_MESSAGE);
			return;
		}
		aPC.removeTemplate(theTmpl);
		forceUpdate();
		currentTemplatesDataModel.setFilter(currentTemplatesDataModel.curFilter);
	}

	private void setInfoLabelText(Race aRace)
	{
		StringBuffer b = new StringBuffer();
		b.append("<html>");
		if ((aRace != null) && !aRace.getName().startsWith("<none"))
		{
			b.append("<b>").append(aRace.piSubString()).append("</b>");
			b.append(" &nbsp;<b>TYPE</b>:").append(aRace.getType());
			String bString = aRace.getSource();
			if (bString.length() > 0)
			{
				b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
			}

		}
		b.append("</html>");
		infoLabel.setText(b.toString());
	}

	private int getSelectedIndex(ListSelectionEvent e)
	{
		final DefaultListSelectionModel model = (DefaultListSelectionModel)e.getSource();
		if (model == null)
		{
			return -1;
		}
		return model.getMinSelectionIndex();
	}

	/**
	 * creates the RaceModel that will be used
	 **/
	protected void createModel()
	{
		if (raceModel == null)
		{
			raceModel = new RaceModel(viewMode);
		}
		else
		{
			raceModel.resetModel(viewMode);
		}
		if (raceSort != null)
		{
			raceSort.setRoot((PObjectNode)raceModel.getRoot());
		}
		if (raceSort != null)
		{
			raceSort.sortNodeOnColumn();
		}
	}

	/**
	 * The RaceModel has a single root node
	 * This root node has a null parent
	 * all other nodes have a parent which points to a non-null node
	 * Parent nodes must have a list of all children
	 * Children must point to their parent
	 * nodes which have 0 children are leafs (the end of the linked list)
	 * most leafs contain an Object (in this case, it's a race object)
	 **/
	class RaceModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// this is the root node
		private PObjectNode raceRoot;

		// list of column names
		private final String[] raceNameList =
		  {
			  "Name", "Stat Adjustments", "PreReqs", "Size", "Speed", "Vision", "Favored Class", "Lvl Adj"
		  };

		private ArrayList raceList = new ArrayList();
		private ArrayList typeList = new ArrayList();
		private ArrayList sourceList = new ArrayList();

		public RaceModel(int mode)
		{
			super(null);
			resetModel(mode);
		}

		public void resetModel(int mode)
		{
			// first clear out the raceList
			raceList.clear();

			// now loop through all the races and
			// see which ones are not filtered out
			for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
			{
				final Race aRace = (Race)it.next();
				if (accept(aRace))
				{
					if (!raceList.contains(aRace))
						raceList.add(aRace);
					if (!typeList.contains(aRace.getType()))
						typeList.add(aRace.getType());
					if (!sourceList.contains(aRace.getSourceNoPage()) && (aRace.getSourceNoPage().length() > 0))
						sourceList.add(aRace.getSourceNoPage());
				}

			}

			// set the root node
			raceRoot = new PObjectNode();
			setRoot(raceRoot);

			switch (mode)
			{
				// races by name
				case GuiConstants.INFORACE_VIEW_NAME:
					PObjectNode rn[] = new PObjectNode[raceList.size()];
					// iterate through the race names
					// and fill out the tree
					for (int iName = 0; iName < raceList.size(); iName++)
					{
						final Race aRace = (Race)raceList.get(iName);
						if (aRace != null)
						{
							rn[iName] = new PObjectNode();
							rn[iName].setItem(aRace);
							rn[iName].setParent(raceRoot);
						}
					}
					// now add to the root node
					raceRoot.setChildren(rn, true);
					break; // end VIEW_NAME

				case GuiConstants.INFORACE_VIEW_TYPE:
					//build the TYPE root nodes
					PObjectNode rt[] = new PObjectNode[typeList.size()];
					// iterate through the race types
					// and fill out the tree
					for (int iType = 0; iType < typeList.size(); iType++)
					{
						final String aType = (String)typeList.get(iType);
						rt[iType] = new PObjectNode();
						rt[iType].setItem(aType);
						for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext();)
						{
							final Race aRace = (Race)fI.next();
							if (aRace == null)
								continue;
							if (!aRace.getType().equals(aType))
								continue;
							PObjectNode aFN = new PObjectNode();
							aFN.setItem(aRace);
							aFN.setParent(rt[iType]);
							rt[iType].addChild(aFN);
						}
						// if it's not empty, add it
						if (!rt[iType].isLeaf())
						{
							rt[iType].setParent(raceRoot);
						}
					}
					// now add to the root node
					raceRoot.setChildren(rt, true);

					break; // end VIEW_TYPE

				case GuiConstants.INFORACE_VIEW_SOURCE:
					//build the SOURCE root nodes
					PObjectNode rs[] = new PObjectNode[sourceList.size()];
					// iterate through the race sources
					// and fill out the tree
					for (int iSource = 0; iSource < sourceList.size(); iSource++)
					{
						final String aSource = (String)sourceList.get(iSource);
						rs[iSource] = new PObjectNode();
						rs[iSource].setItem(aSource);
						for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext();)
						{
							final Race aRace = (Race)fI.next();
							if (aRace == null)
								continue;
							if (!aRace.getSourceNoPage().equals(aSource))
								continue;
							PObjectNode aFN = new PObjectNode();
							aFN.setItem(aRace);
							aFN.setParent(rs[iSource]);
							rs[iSource].addChild(aFN);
						}
						// if it's not empty, add it
						if (!rs[iSource].isLeaf())
						{
							rs[iSource].setParent(raceRoot);
						}
					}
					// now add to the root node
					raceRoot.setChildren(rs, true);

					break; // end VIEW_SOURCE
				default:
					Globals.errorPrint("In InfoRace.RaceModel.resetModel the mode " + mode + " is not supported.");
					break;

			} // end of switch(mode)

			PObjectNode rootAsPObjectNode = (PObjectNode)root;
			if (rootAsPObjectNode.getChildCount() > 0)
				fireTreeNodesChanged(root, rootAsPObjectNode.getChildren(), null, null);

		}

		/**
		 * There must be a root node, but we keep it hidden
		 **/
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		/**
		 * return the root node
		 **/
		public Object getRoot()
		{
			return (PObjectNode)root;
		}

		/**
		 * the number of columns
		 **/
		public int getColumnCount()
		{
			return raceNameList.length;
		}

		/**
		 * the name of each column (for the headers)
		 **/
		public String getColumnName(int column)
		{
			return raceNameList[column];
		}

		/**
		 * return the Class for a column
		 **/
		public Class getColumnClass(int column)
		{
			if (column == COL_NAME)
			{
				return TreeTableModel.class;
			}
			return String.class;
		}

		public int getRowCount()
		{
			return raceList.size();
		}

		/**
		 * return the value of a column
		 **/
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			Race race = null;
			String sRet = "";

			if (fn == null)
			{
				Globals.errorPrint("No active node when doing getValueAt in InfoRace");
				return null;
			}

			if (fn.getItem() instanceof Race)
			{
				race = (Race)fn.getItem();
			}

			switch (column)
			{
				case COL_NAME:
					return fn.toString();

				case COL_STAT:
					if (race == null)
					{
						return null;
					}
					final StringBuffer aString = new StringBuffer();
					for (int i = 0; i < Globals.s_ATTRIBSHORT.length; i++)
					{
						if (race.isNonability(i))
						{
							if (aString.length() > 0)
							{
								aString.append(" ");
							}
							aString.append(Globals.s_ATTRIBSHORT[i]).append(":Nonability");
						}
						else
						{
							if (race.getStatMod(i) != 0)
							{
								if (aString.length() > 0)
								{
									aString.append(" ");
								}
								aString.append(Globals.s_ATTRIBSHORT[i]).append(":").append(race.getStatMod(i));
							}
						}
					}
					sRet = aString.toString();
					break;

				case COL_PRE:
					if (race == null)
					{
						return null;
					}
					sRet = race.preReqHTMLStrings();
					break;

				case COL_SIZE:
					if (race == null)
					{
						return null;
					}
					sRet = race.getSize();
					break;

				case COL_MOVE:
					if (race == null)
					{
						return null;
					}
					if (race.getMovements() != null)
					{
						final StringBuffer movelabel = new StringBuffer(race.getMovementType(0)).append(" ").append(race.getMovement(0)).append(Globals.getAbbrMovementDisplay());
						for (int i = 1; i < race.getMovements().length; i++)
						{
							movelabel.append(race.getMovementType(i)).append(" ").append(race.getMovement(i)).append(Globals.getAbbrMovementDisplay());
						}
						sRet = movelabel.toString();
					}
					break;

				case COL_VISION:
					if (race == null)
					{
						return null;
					}
					sRet = race.getDisplayVision();
					break;

				case COL_CLASS:
					if (race == null)
					{
						return null;
					}
					sRet = (!race.getFavoredClass().equals("."))
					  ? race.getFavoredClass()
					  : "Various";
					break;

				case COL_LEVEL:
					if (race == null)
					{
						return null;
					}
					return new Integer(race.getLevelAdjustment());

				default:
					Globals.errorPrint("In InfoRace.RaceModel.getValueAt the column " + column + " is not supported.");
					break;
			}
			return sRet;
		}

		public void addChild(Object aChild, Object aParent)
		{
			PObjectNode aSN = new PObjectNode();
			aSN.setItem(aChild);
			aSN.setParent((PObjectNode)aParent);
			((PObjectNode)aParent).addChild(aSN);
		}
	}

	/**
	 *
	 * A TableModel to handle the full list of templates.
	 * It pulls its data straight from Globals.getTemplateList()
	 *
	 **/
	class AllTemplatesTableModel extends AbstractTableModel
	{
		public int curFilter;
		private int prevGlobalTemplateCount;
		private ArrayList displayTemplates = new ArrayList();

		private final String[] ALL_TEMPLATES_COLUMN_NAMES = new String[]
		{
			"Q", "Name", "Lvl Adj", "Modifiers", "Prereqs", "Source"
		};

		public AllTemplatesTableModel()
		{
			updateModel();
		}

		public PCTemplate get(int index)
		{
			return (PCTemplate)displayTemplates.get(index);
		}

		public void updateModel()
		{
			displayTemplates.clear();

			for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
			{
				final PCTemplate aPCTemplate = (PCTemplate)it.next();
				if ((aPCTemplate.isVisible() % 2 == 1) && accept(aPCTemplate))
				{
					displayTemplates.add(aPCTemplate);
				}
			}

			fireTableDataChanged();
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list of filters
		 * will require a modification of this method.
		 * at the moment:
		 * 0: All
		 * 1: Qualified
		 * @param filterID the filter type
		 */
		public void setFilter(int filterID)
		{
			prevGlobalTemplateCount = Globals.getTemplateList().size();
			displayTemplates = new ArrayList();
			switch (filterID)
			{
				case 0: // All
					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if (pcTmpl.isVisible() == 1 || pcTmpl.isVisible() == 3)
							displayTemplates.add(pcTmpl);
					}
					break;
				case 1: // Qualified
					for (Iterator it = Globals.getTemplateList().iterator(); it.hasNext();)
					{
						PCTemplate pcTmpl = (PCTemplate)it.next();
						if ((pcTmpl.isVisible() == 1 || pcTmpl.isVisible() == 3) && pcTmpl.isQualified())
							displayTemplates.add(pcTmpl);
					}
					break;
				default:
					Globals.errorPrint("In InfoRace.AllTemplatesTableModel.setFilter the filter ID " + filterID + " is not supported.");
					break;

			}
			fireTableDataChanged();
			curFilter = filterID;
		}

		/**
		 * Re-fetches and re-filters the data from the global template list.
		 */
		public void updateFilter()
		{
			setFilter(curFilter);
		}

		/**
		 * @return the number of columns
		 */
		public int getColumnCount()
		{
			return ALL_TEMPLATES_COLUMN_NAMES.length;
		}

		/**
		 * @param columnIndex the index of the column to retrieve
		 * @return the type of the specified column
		 */
		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		/**
		 * @return the number of rows in the model
		 */
		public int getRowCount()
		{
			if (prevGlobalTemplateCount != Globals.getTemplateList().size())
				updateFilter();
			return (displayTemplates != null) ? displayTemplates.size() : 0;
		}

		/**
		 * @param columnIndex the index of the column name to retrieve
		 * @return the name.. of the specified column
		 */
		public String getColumnName(int columnIndex)
		{
			return (columnIndex >= 0 && columnIndex < ALL_TEMPLATES_COLUMN_NAMES.length) ?
			  ALL_TEMPLATES_COLUMN_NAMES[columnIndex] : "Out Of Bounds";
		}

		/**
		 * @param rowIndex the row of the cell to retrieve
		 * @param columnIndex the column of the cell to retrieve
		 * @return the value of the cell
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			if (displayTemplates != null)
			{
				PCTemplate selectedTemplate = (PCTemplate)displayTemplates.get(rowIndex);
				final PlayerCharacter aPC = Globals.getCurrentPC();
				final PCTemplate pcTemplate = aPC.getTemplateNamed(selectedTemplate.toString());
				if (pcTemplate != null)
				{
					selectedTemplate = pcTemplate;
				}
				switch (columnIndex)
				{
					case 0:
						return selectedTemplate.isQualified() ? "Y" : "N";
					case 1:
						return selectedTemplate.toString();
					case 2:
						return "" + selectedTemplate.getLevelAdjustment();
					case 3:
						return selectedTemplate.modifierString();
					case 4:
						return selectedTemplate.preReqStrings();
					case 5:
						return selectedTemplate.getSource();
					default:
						Globals.errorPrint("In InfoRace.AllTemplatesTableModel.getValueAt the column " + columnIndex + " is not supported.");
						break;
				}
			}
			return null;
		}
	}

	/**
	 * This is the model for currently selected templates
	 **/
	class PCTemplatesTableModel extends AbstractTableModel
	{
		public int curFilter;
		private int prevGlobalTemplateCount;

		public int getColumnCount()
		{
			return 2;
		}

		public Class getColumnClass(int columnIndex)
		{
			return String.class;
		}

		public int getRowCount()
		{
			return currentPCdisplayTemplates.size();
		}

		public String getColumnName(int columnIndex)
		{
			switch (columnIndex)
			{
				case 0:
					return "Template";
				case 1:
					return "Removable";
				default:
					Globals.errorPrint("In InfoRace.PCTemplatesTableModel.getColumnName the column " + columnIndex + " is not supported.");
					break;
			}
			return "Out Of Bounds";
		}

		public Object getValueAt(int rowIndex, int columnIndex)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if ((aPC != null) && (aPC.getTemplateList() != null))
			{
				PCTemplate t = (PCTemplate)currentPCdisplayTemplates.get(rowIndex);
				switch (columnIndex)
				{
					case 0:
						return t.toString();
					case 1:
						return (t.isRemovable() ? "Yes" : "No");
					default:
						Globals.errorPrint("In InfoRace.PCTemplatesTableModel.getValueAt the column " + columnIndex + " is not supported.");
						break;
				}
			}
			return null;
		}

		/**
		 * Uses the ID from the jcbFilter, so any change to the list
		 * of filters will require a modification of this method.
		 * at the moment:
		 * 0: Visible
		 * 1: Invisible
		 * 2: All
		 * @param filterID the filter type
		 */
		public void setFilter(int filterID)
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			if (aPC == null)
			{
				currentPCdisplayTemplates = new ArrayList(0);
			}
			else
			{
				prevGlobalTemplateCount = aPC.getTemplateList().size();
				currentPCdisplayTemplates = new ArrayList(prevGlobalTemplateCount);
				switch (filterID)
				{
					case 0:
						for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
						{
							final PCTemplate pcTmpl = (PCTemplate)it.next();
							if (pcTmpl.isVisible() == 1 || pcTmpl.isVisible() == 3)
								currentPCdisplayTemplates.add(pcTmpl);
						}
						break;
					case 1:
						for (Iterator it = aPC.getTemplateList().iterator(); it.hasNext();)
						{
							final PCTemplate pcTmpl = (PCTemplate)it.next();
							if (pcTmpl.isVisible() == 0 || pcTmpl.isVisible() == 2)
								currentPCdisplayTemplates.add(pcTmpl);
						}
						break;
					case 2:
						currentPCdisplayTemplates.addAll(aPC.getTemplateList());
						break;
					default:
						Globals.errorPrint("In InfoRace.PCTemplatesTableModel.setFilter the filter ID " + filterID + " is not supported.");
						break;
				}
			}
			currentPCdisplayTemplates.trimToSize();
			fireTableDataChanged();
			curFilter = filterID;
		}
	}

	/**
	 * implementation of Filterable interface
	 **/
	public void initializeFilters()
	{
		FilterFactory.registerAllSourceFilters(this);
		FilterFactory.registerAllSizeFilters(this);
		FilterFactory.registerAllRaceFilters(this);
		FilterFactory.registerAllPrereqAlignmentFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 **/
	public void refreshFiltering()
	{
		createModel();
		raceTable.updateUI();
		allTemplatesDataModel.updateModel();
	}

	/**
	 * specifies wheter the "match any" option should be available
	 **/
	public boolean isMatchAnyEnabled()
	{
		return true;
	}

	/**
	 * specifies wheter the "negate/reverse" option should be available
	 **/
	public boolean isNegateEnabled()
	{
		return true;
	}

	/**
	 * specifies the filter selection mode
	 **/
	public int getSelectionMode()
	{
		return MULTI_MULTI_MODE;
	}
}

