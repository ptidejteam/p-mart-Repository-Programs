/*
 * MainSource.java
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
 * Created on April 21, 2001, 2:15 PM
 * ReCreated on Feb 28, 2002 5:10 PM
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import pcgen.core.Campaign;
import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;

/**
 *  <code>MainSource</code> .
 *
 * @author  Bryan McRoberts (merton_monk@yahoo.com), Jason Buchanan (lonejedi70@hotmail.com)
 * @version $Revision: 1.1 $
 *
 * Campaigns is kind of misleading - it's meant to be a collection of
 * sources from which players can pick their character's
 * options. Campaign is also used by most groups to refer to their
 * game, so Campaign is kind of misleading.  Therefore we've decided
 * to change the "Campaign" tab to "Source Materials" to avoid this
 * confusion.  cu merton_monk 6 Sept, 2001.
 */
public class MainSource extends FilterAdapterPanel
{
	private final JLabel avaLabel = new JLabel("Available");
	private final JLabel selLabel = new JLabel("Selected");
	private JButton leftButton;
	private JButton rightButton;
	private JScrollPane cScroll;
	private JLabelPane infoLabel;
	private JPanel center = new JPanel();
	private Border etched;
	private TitledBorder titled;
	private JSplitPane splitPane;
	private JSplitPane bsplit;
	private JSplitPane asplit;
	protected CampaignModel availableModel = null;  // Model for the TreeTable.
	protected CampaignModel selectedModel = null;   // Model for the JTreeTable.
	protected JTreeTable availableTable;  // the available Campaigns
	protected JTreeTable selectedTable;   // the selected Campaigns
	private PObjectNode lastSelection = null; //keep track of which PObjectNode was last selected from either table
	public static boolean needsUpdate = true;
	protected JComboBox viewComboBox = new JComboBox();
	protected JComboBox viewSelectComboBox = new JComboBox();
	private static int splitOrientation = JSplitPane.HORIZONTAL_SPLIT;

	//column positions for tables
	private static final int COL_NAME = 0;
	private static final int COL_LOADED = 1;

	//view modes for tables
	private static final int VIEW_PRODUCT = 0;
	private static final int VIEW_PUBLISH = 1;
	private static final int VIEW_PUBSET = 2;
	private static final int VIEW_PUBFMTSET = 3;
	static int viewMode = VIEW_PUBFMTSET; // keep track of what view mode we're in for Available
	static int viewSelectMode = VIEW_PRODUCT; // keep track of what view mode we're in for Selected. defaults to "Name"

	//table model modes
	private static final int MODEL_AVAIL = 0;
	private static final int MODEL_SELECT = 1;

	// Right-click table item
	private int selRow;
	private TreePath selPath;
	private boolean hasBeenSized = false;

	private JPanel jPanel1 = new JPanel();
	private JPanel jPanel1n = new JPanel();
	private JPanel jPanel1s = new JPanel();

	//bottom right pane ("Interaction" Pane)
	private JButton loadButton = new JButton();
	private JButton unloadAllButton = new JButton();
	private JButton removeAllButton;
	private JButton websiteButton;

	public static PObjectNode typePubRoot;
	public static PObjectNode typePubSetRoot;
	public static PObjectNode typePubFmtSetRoot;

	private ArrayList selectedCampaigns = new ArrayList();

	public MainSource()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName("Campaigns");

		initComponents();
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
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

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		resetViewNodes();

		viewComboBox.addItem("Name Only");
		viewComboBox.addItem("Company");
		viewComboBox.addItem("Company/Setting");
		viewComboBox.addItem("Comp/Fmt/Setting"); //abbr. here so that all GUI elements will fit when started at default window size
		Utility.setDescription(viewComboBox, "You can change how the Sources in the Tables are listed.");
		viewMode = SettingsHandler.getPCGenOption("pcgen.options.sourceTab.availableListMode", VIEW_PUBFMTSET);
		viewComboBox.setSelectedIndex(viewMode);       // must be done before createModels call
		viewSelectComboBox.addItem("Name Only");
		viewSelectComboBox.addItem("Company");
		viewSelectComboBox.addItem("Company/Setting");
		viewSelectComboBox.addItem("Company/Format/Setting");
		viewSelectMode = SettingsHandler.getPCGenOption("pcgen.options.sourceTab.selectedListMode", VIEW_PRODUCT);
		Utility.setDescription(viewSelectComboBox, "You can change how the Sources in the Tables are listed.");
		viewSelectComboBox.setSelectedIndex(viewSelectMode); // must be done before createModels call

		createModels();
		// create available table of Campaigns
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
		Utility.setDescription(rightButton, "Click to add the source");
		rightButton.setEnabled(false);
		aPanel.add(rightButton);
		leftPane.add(aPanel);
		newImage = new ImageIcon(getClass().getResource("resource/Refresh16.gif"));
		JButton sButton = new JButton(newImage);
		Utility.setDescription(sButton, "Click to change the orientation of the tables");
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
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove the source");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		rightPane.add(aPanel);

		buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		TitledBorder title1 = BorderFactory.createTitledBorder(etched, "Source Info");
		title1.setTitleJustification(TitledBorder.CENTER);
		infoLabel = new JLabelPane();
		infoLabel.setBackground(rightPane.getBackground());
		cScroll = new JScrollPane(infoLabel);
		cScroll.setBorder(title1);

		FlowLayout aFlow = new FlowLayout();
		aFlow.setAlignment(FlowLayout.CENTER);
		jPanel1.setLayout(new BorderLayout());
		jPanel1.add(jPanel1n, BorderLayout.NORTH);
		jPanel1.add(jPanel1s, BorderLayout.CENTER);
		jPanel1n.setLayout(aFlow);
		aFlow = new FlowLayout();
		aFlow.setAlignment(FlowLayout.CENTER);
		jPanel1s.setLayout(aFlow);

		loadButton.setText("Load");
		loadButton.setMnemonic(KeyEvent.VK_L);
		loadButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					loadCampaigns_actionPerformed(e);
				}
			});
		jPanel1n.add(loadButton);
		loadButton.setToolTipText("This loads all the sources listed in the above table");

		unloadAllButton.setText("Unload All");
		unloadAllButton.setMnemonic(KeyEvent.VK_U);
		unloadAllButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					unloadAllCampaigns_actionPerformed(e);
				}
			});
		jPanel1n.add(unloadAllButton);

		removeAllButton = new JButton("Remove All");
		removeAllButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					selectAll_actionPerformed(e, false);
				}
			});
		removeAllButton.setToolTipText("Remove all sources from the above table");
		jPanel1n.add(removeAllButton);

		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					refreshCampaigns();
				}
			});
		jPanel1s.add(refreshButton);
		refreshButton.setToolTipText("Refresh the list of sources");

		websiteButton = new JButton("Website");
		websiteButton.addActionListener(
			new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					launchProductWebsite(e, false);
				}
			});
		websiteButton.setToolTipText("Go to the selected product's website");
		websiteButton.setEnabled(false);
		jPanel1s.add(websiteButton);

		JButton pccButton = new JButton("Customise");
		{
			final MainSource t = this;
			pccButton.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						//Create and display pcccreator popup dialog
						new PCCCreator(t);
					}
				});
		}
		pccButton.setToolTipText("Customise your own source to ease your loading process");
		jPanel1s.add(pccButton);

		asplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
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

		//go ahead and auto-load campaigns now, if that option is set
		if (SettingsHandler.isLoadCampaignsAtStart())
		{
			selectCampaignsByFilename(PersistenceManager.getChosenCampaignSourcefiles());
			if (selectedCampaigns.size() > 0)
			{
				loadCampaigns();
			}
		}
	}

	private void resetViewNodes()
	{
		typePubRoot = new PObjectNode();
		typePubSetRoot = new PObjectNode();
		typePubFmtSetRoot = new PObjectNode();
		ArrayList aList = new ArrayList(); //TYPE list
		for (Iterator i = Globals.getCampaignList().iterator(); i.hasNext();)
		{
			final Campaign aCamp = (Campaign)i.next();
			if (Globals.isInGameMode(aCamp.getGame()))
				if (aCamp.getMyTypeCount() > 0)
					if (!aList.contains(aCamp.getMyType(0)))
						aList.add(aCamp.getMyType(0)); //TYPE[0] = Publisher
		}
		Collections.sort(aList);
		// All non-typed/screwed-up items will end up in an "Other" node
		if (!aList.contains("Other"))
			aList.add("Other");
		PObjectNode p1[] = new PObjectNode[aList.size()];
		PObjectNode p2[] = new PObjectNode[aList.size()];
		PObjectNode p3[] = new PObjectNode[aList.size()];
		for (int i = 0; i < aList.size(); i++)
		{
			p1[i] = new PObjectNode();
			p1[i].setItem(aList.get(i).toString());
			p1[i].setParent(typePubRoot);
			p2[i] = new PObjectNode();
			p2[i].setItem(aList.get(i).toString());
			p2[i].setParent(typePubSetRoot);
			p3[i] = new PObjectNode();
			p3[i].setItem(aList.get(i).toString());
			p3[i].setParent(typePubFmtSetRoot);
		}
		typePubRoot.setChildren(p1, false);
		typePubSetRoot.setChildren(p2, false);
		typePubFmtSetRoot.setChildren(p3, false);

		for (int i = 0; i < p2.length; i++)
		{
			aList.clear();
			for (int j = 0; j < Globals.getCampaignList().size(); j++)
			{
				final Campaign bCamp = (Campaign)Globals.getCampaignList().get(j);
				final String topType = p2[i].toString();
				if (!bCamp.isType(topType))
					continue;

				if (bCamp.getMyTypeCount() > 2)
					if (Globals.isInGameMode(bCamp.getGame()))
						if (!aList.contains(bCamp.getMyType(2)))
							aList.add(bCamp.getMyType(2)); //TYPE[2] = Setting
			}
			Collections.sort(aList);
			for (Iterator lI = aList.iterator(); lI.hasNext();)
			{
				String aString = (String)lI.next();
				PObjectNode d = new PObjectNode();
				d.setParent(p2[i]);
				p2[i].addChild(d);
				d.setItem(aString);
			}
		}

		for (int i = 0; i < p3.length; i++)
		{
			aList.clear();
			for (int j = 0; j < Globals.getCampaignList().size(); j++)
			{
				final Campaign bCamp = (Campaign)Globals.getCampaignList().get(j);
				final String topType = p3[i].toString();
				if (!bCamp.isType(topType))
					continue;

				if (bCamp.getMyTypeCount() > 1)
					if (Globals.isInGameMode(bCamp.getGame()))
						if (!aList.contains(bCamp.getMyType(1)))
							aList.add(bCamp.getMyType(1)); //TYPE[1] = Format
			}
			Collections.sort(aList);
			for (Iterator lI = aList.iterator(); lI.hasNext();)
			{
				String aString = (String)lI.next();
				PObjectNode d = new PObjectNode();
				d.setParent(p3[i]);
				p3[i].addChild(d);
				d.setItem(aString);
			}

			PObjectNode[] p4 = p3[i].getChildren();
			for (int k = 0; p4 != null && k < p4.length; k++)
			{
				aList.clear();
				for (int m = 0; m < Globals.getCampaignList().size(); m++)
				{
					final Campaign cCamp = (Campaign)Globals.getCampaignList().get(m);
					final String pubType = p4[k].getParent().toString();
					final String formatType = p4[k].toString();
					if (!cCamp.isType(pubType) || !cCamp.isType(formatType))
						continue;

					if (cCamp.getMyTypeCount() > 2)
						if (Globals.isInGameMode(cCamp.getGame()))
							if (!aList.contains(cCamp.getMyType(2)))
								aList.add(cCamp.getMyType(2)); //TYPE[2] = Setting
				}
				Collections.sort(aList);
				for (Iterator lI = aList.iterator(); lI.hasNext();)
				{
					String aString = (String)lI.next();
					PObjectNode d = new PObjectNode();
					d.setParent(p4[k]);
					p4[k].addChild(d);
					d.setItem(aString);
				}
			}
		}
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
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(evt, true);
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(evt, false);
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

	private void viewComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			createAvailableModel();
			availableTable.updateUI();
			SettingsHandler.setPCGenOption("pcgen.options.sourceTab.availableListMode", viewMode);
		}
	}

	private void viewSelectComboBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			createSelectedModel();
			selectedTable.updateUI();
			SettingsHandler.setPCGenOption("pcgen.options.sourceTab.selectedListMode", viewSelectMode);
		}
	}

	private class CampaignPopupMenu extends JPopupMenu
	{
		private class AddCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(evt, true);
			}
		}

		private class AddAllCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectAll_actionPerformed(evt, true);
			}
		}

		private class RemoveCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(evt, false);
			}
		}

		private class RemoveAllCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectAll_actionPerformed(evt, false);
			}
		}

		private class WebActionListener implements ActionListener
		{
			protected boolean available = true;

			protected WebActionListener(boolean fromAvail)
			{
				available = fromAvail;
			}

			public void actionPerformed(ActionEvent evt)
			{
				launchProductWebsite(evt, available);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddCampaignActionListener(), "select", (char)0, accelerator, "Select Source material to load", "Add16.gif", true);
		}

		private JMenuItem createAddAllMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddAllCampaignActionListener(), "selectall", (char)0, accelerator, "Select All Source material to load", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveCampaignActionListener(), "deselect", (char)0, accelerator, "Remove Source material from loading", "Remove16.gif", true);
		}

		private JMenuItem createRemoveAllMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveAllCampaignActionListener(), "deselectall", (char)0, accelerator, "Remove All Source material from loading", "Remove16.gif", true);
		}

		private JMenuItem createWebMenuItem(String label, String accelerator, boolean fromAvail)
		{
			return Utility.createMenuItem(label, new WebActionListener(fromAvail), "website", (char)0, accelerator, "Launch browser to product's website", "Bookmarks16.gif", true);
		}

		CampaignPopupMenu(JTreeTable treeTable)
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
				CampaignPopupMenu.this.add(createAddMenuItem("Select", "control EQUALS"));
				CampaignPopupMenu.this.add(createAddAllMenuItem("Select All", "alt A"));
				CampaignPopupMenu.this.add(createWebMenuItem("Product Website...", "alt W", true));
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
				CampaignPopupMenu.this.add(createRemoveMenuItem("Remove", "control MINUS"));
				CampaignPopupMenu.this.add(createRemoveAllMenuItem("Remove All", "alt A"));
				CampaignPopupMenu.this.add(createWebMenuItem("Product Website...", "alt W", false));
			}
		}
	}

	private class CampaignPopupListener extends MouseAdapter
	{
		private JTree tree;
		private CampaignPopupMenu menu;

		CampaignPopupListener(JTreeTable treeTable, CampaignPopupMenu aMenu)
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
							final JMenuItem menuItem = (JMenuItem)menu.getComponent(i);
							javax.swing.KeyStroke ks = menuItem.getAccelerator();
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
		treeTable.addMouseListener(new CampaignPopupListener(treeTable, new CampaignPopupMenu(treeTable)));
	}

	// NOTE: this function adds all sources to the selected list in memory, but does NOT update the GUI itself
	private void selectAllLeaves(PObjectNode node, boolean select)
	{
		for (int count = 0; count < node.getChildCount(); count++)
		{
			// if this child is a leaf, then select it...
			PObjectNode child = node.getChild(count);
			if (child.isLeaf() && child.getItem() instanceof Campaign)
			{
				Campaign aCamp = (Campaign)child.getItem();
				if (aCamp != null)
					if (select)
					{
						if (!selectedCampaigns.contains(aCamp))
							selectedCampaigns.add(aCamp);
					}
					else
					{
						selectedCampaigns.remove(aCamp);
					}
			}
			// ...otherwise recurse, using it as the new parent node
			else
			{
				selectAllLeaves(child, select);
			}
		}
	}

	private void selectAll_actionPerformed(ActionEvent e, boolean select)
	{
		if (select)
		{
			selectAllLeaves((PObjectNode)availableTable.getTree().getModel().getRoot(), true);
		}
		else
		{
			selectedCampaigns.clear();
			unloadAllCampaigns_actionPerformed(e);
		}
		updateModels();

		//Remember what we just did...
		rememberSourceChanges();
	}

	private void rememberSourceChanges()
	{
		ArrayList campaignStrings = new ArrayList(selectedCampaigns.size());
		for (java.util.Iterator campaigns = selectedCampaigns.iterator(); campaigns.hasNext();)
		{
			campaignStrings.add(((Campaign)(campaigns.next())).getSourceFile());
		}
		PersistenceManager.setChosenCampaignSourcefiles(campaignStrings);
	}

	private void loadCampaigns_actionPerformed(ActionEvent e)
	{
		final String oldStatus = PCGen_Frame1.getStatusBar().getText();
		PCGen_Frame1.getStatusBar().setText("Loading Sources...");
		PCGen_Frame1.getStatusBar().updateUI(); //even this doesn't seem to get the UI to update
		// ??? updateUI is for notification of LookAndFeel changes, you probably want to call repaint!
		loadCampaigns();
		PCGen_Frame1.getStatusBar().setText(oldStatus);
		PCGen_Frame1.restoreFilterSettings(null);
		Globals.debugPrint("Loaded: (all must be non-zero to be able to create a new character)");
		Globals.debugPrint("Races=" + Globals.getRaceMap().size());
		Globals.debugPrint("Classes=" + Globals.getClassList().size());
		Globals.debugPrint("Skills=" + Globals.getSkillList().size());
		Globals.debugPrint("Feats=" + Globals.getFeatList().size());
		Globals.debugPrint("Equipment=" + Globals.getEquipmentList().size());
		Globals.debugPrint("WeaponProfs=" + Globals.getWeaponProfList().size());
	}

	//this method will now unload all current sources, but will not remove them from the selected table
	private void unloadAllCampaigns_actionPerformed(ActionEvent e)
	{
		PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
		if (Globals.isDebugMode()) //don't force PC closure if we're in debug mode, per request
		{
			JOptionPane.showMessageDialog(this,
				"PC's are not closed in debug mode.  " +
				"Please be aware that they may not function correctly " +
				"until campaign data is loaded again.",
				Constants.s_APPNAME, JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			parent.closeAllPCs();
			if (parent.getBaseTabbedPane().getTabCount() > 2) // Campaign and DMTools tabs are OK
			{
				JOptionPane.showMessageDialog(this, "Can't unload campaigns until all PC's are closed.",
					Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		Globals.emptyLists();
		PersistenceManager.emptyLists();
		PersistenceManager.setChosenCampaignSourcefiles(new ArrayList());
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			Campaign aCamp = (Campaign)it.next();
			aCamp.setIsLoaded(false);
		}

		parent.enableNew(false);

		updateModels();
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		//requestDefaultFocus();
		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Select sources then Load them.");
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			// DividerSizes need to be reset here because the UIFactory invocation in main()
			// messes them up after initComponents tries to set them
			splitPane.setDividerSize(10);
			splitPane.setDividerLocation(.5);
			bsplit.setDividerSize(10);
			bsplit.setDividerLocation(.75);
			asplit.setDividerSize(10);
			asplit.setDividerLocation(.5);
			setLoadedColMaxWidth();
		}
	}

	public void updateLoadedCampaignsUI()
	{
		if (getParent() != null && Globals.displayListsHappy())
		{
			PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
			parent.enableNew(true);
		}
	}

	private void loadCampaigns()
	{
		if (selectedCampaigns.size() < 1)
			return;

		try
		{
			PersistenceManager.loadCampaigns(selectedCampaigns);
		}
		catch (PersistenceLayerException e)
		{
			JOptionPane.showMessageDialog(this, e.getMessage(), Constants.s_APPNAME, JOptionPane.WARNING_MESSAGE);
		}
		Globals.sortCampaigns();
		if (getParent() != null && Globals.displayListsHappy())
		{
			PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
			parent.enableNew(true);
		}
		updateModels();
	}

	private void doCampaign(ActionEvent evt, boolean select)
	{
		if (lastSelection == null)
			return;

		if (lastSelection.getItem() instanceof Campaign)
		{
			final Campaign theCamp = (Campaign)lastSelection.getItem();
			if (select)
			{
				if (!selectedCampaigns.contains(theCamp))
					selectedCampaigns.add(theCamp);
			}
			else
			{
				selectedCampaigns.remove(theCamp);
			}
		}
		// if we didn't get a Campaign back, then it must be a tree branch
		else
		{
			selectAllLeaves(lastSelection, select);
		}

		Collections.sort(selectedCampaigns);

		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		// Remember which rows are expanded
		for (int i = 0; i < s.length; i++)
		{
			s[i] = selectedTable.getTree().isExpanded(i);
		}
		boolean a[] = new boolean[availableTable.getTree().getRowCount()];
		// Remember which rows are expanded
		for (int i = 0; i < a.length; i++)
		{
			a[i] = availableTable.getTree().isExpanded(i);
		}

		updateModels();

		//re-expand the rows (they were cleared out in the createSelectedModel() call)
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
			{
				selectedTable.getTree().expandRow(i);
			}
		}
		for (int i = 0; i < a.length; i++)
		{
			if (a[i])
			{
				availableTable.getTree().expandRow(i);
			}
		}

		//ensure that the target skill gets displayed in the selectedTable if you've just added a source
		if (select)
		{
			selectedTable.expandByPObjectName(lastSelection.getItem().toString());
		}

		//Remember what we just did...
		rememberSourceChanges();
	}

	private void launchProductWebsite(ActionEvent e, boolean avail)
	{
		JTreeTable treeTable;
		if (avail)
			treeTable = availableTable;
		else
			treeTable = selectedTable;

		final PObjectNode pon = (PObjectNode)treeTable.getTree().getLastSelectedPathComponent();
		if (pon != null)
		{
			if (pon.getItem() instanceof Campaign)
			{
				final Campaign theCamp = (Campaign)pon.getItem();
				final String theURL = theCamp.getSourceWeb();
				if (!theURL.equals(""))
				{
					try
					{
						BrowserLauncher.openURL(theURL);
					}
					catch (IOException ioEx)
					{
						JOptionPane.showMessageDialog(null,
							"Could not open browser to " + theURL,
							Constants.s_APPNAME,
							JOptionPane.ERROR_MESSAGE);
						ioEx.printStackTrace();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null,
						"No web information found for Source: " + theCamp.getName(),
						Constants.s_APPNAME,
						JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null,
				"Please select a source.",
				Constants.s_APPNAME,
				JOptionPane.ERROR_MESSAGE);
		}
	}

	private void setLoadedColMaxWidth()
	{
		//make the "Loaded" checkbox column a reasonable size
		selectedTable.getColumnModel().getColumn(COL_LOADED).setMaxWidth(50);
	}

	public void changedGameMode()
	{
		selectedCampaigns.clear();
		resetViewNodes();
		unloadAllCampaigns_actionPerformed(null);
	}

	private void updateModels()
	{
		availableModel.resetModel(viewMode, true, false);
		availableTable.updateUI();
		selectedModel.resetModel(viewSelectMode, false, false);
		selectedTable.updateUI();
		setLoadedColMaxWidth();

		/* Toggle the Load, et al, buttons */
		if (selectedTable.getTree().getRowCount() == 0)
		{
			loadButton.setEnabled(false);
			unloadAllButton.setEnabled(false);
			removeAllButton.setEnabled(false);
		}
		else
		{
			loadButton.setEnabled(true);
			unloadAllButton.setEnabled(true);
			removeAllButton.setEnabled(true);
		}
	}

	/**
	 *  Pass this a Collection of campaign file names. These will be selected in the
	 *  table.
	 *
	 * @param  campaigns  A Collection of campaign file names.
	 * @since
	 */
	private void selectCampaignsByFilename(Collection campaigns)
	{
		for (Iterator iter = campaigns.iterator(); iter.hasNext();)
		{
			final String element = (String)iter.next();
			final Campaign aCampaign = Globals.getCampaignByFilename(element);
			if (aCampaign != null)
				if (!selectedCampaigns.contains(aCampaign))
				{
					selectedCampaigns.add(aCampaign);
					updateModels();
				}
		}
	}

	private void setInfoLabelText(PObjectNode aNode)
	{
		lastSelection = aNode; //even in the case where this is null

		if (aNode != null)
		{
			if (aNode.getItem() instanceof Campaign)
			{
				final Campaign aCamp = (Campaign)aNode.getItem();
				// We can turn on the website button, since now there is a source, if there is a URL for the campaign
				websiteButton.setEnabled(!aCamp.getSourceWeb().equals(""));


				StringBuffer b = new StringBuffer();
				b.append("<html><b>").append(aCamp.getName()).append("</b><br>");
				b.append("<b>TYPE</b>:").append(aCamp.getType());
				b.append(" &nbsp;<b>RANK</b>:").append(aCamp.getRank());
				b.append(" &nbsp;<b>GAME MODE</b>:").append(aCamp.getGame());
				String bString = aCamp.getSource();
				if (bString.length() > 0)
					b.append(" &nbsp;<b>SOURCE</b>:").append(bString);
				bString = aCamp.getNfo();
				if (bString.length() > 0)
					b.append("<p><b>INFORMATION</b>:<br>").append(bString);

				b.append("</html>");
				infoLabel.setText(b.toString());
			}
			else  //must just be a branch node
			{
				// We off the website button since our source went away
				websiteButton.setEnabled(false);

				PObjectNode pathNode = aNode;
				String path = pathNode.getItem().toString();
				while (pathNode.getParent() != availableTable.getTree().getModel().getRoot() &&
					pathNode.getParent() != selectedTable.getTree().getModel().getRoot())
				{
					pathNode = (PObjectNode)pathNode.getParent();
					path = pathNode.getItem().toString() + "." + path;
				}
//  				final Object[] nodePath = availableTable.getTree().getPathForRow(idx).getPath();
//  				//always start at nodePath[1], because nodePath[0] is the root (unlabeled) node
//  				for (int pathCount = 1; pathCount < nodePath.length; pathCount++)
//  				{
//  					if (!path.equals(""))
//  					{
//  						path = path + ".";
//  					}
//  					path = path + ((PObjectNode)nodePath[pathCount]).getItem().toString();
//  				}

				StringBuffer b = new StringBuffer();
				// enclose the node-path name with the <p> tag so that we can parse for it later
				b.append("<html><b>").append(path).append("</b><br>");
				b.append("</html>");
				infoLabel.setText(b.toString());
			}
		}
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

	protected void createTreeTables()
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
					final Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null || !(temp instanceof PObjectNode))
					{
						JOptionPane.showMessageDialog(null,
							"No campaign selected. Try again.", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
						return;
					}

					setInfoLabelText((PObjectNode)temp);

					rightButton.setEnabled(true);
					leftButton.setEnabled(false);
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
						doCampaign(null, true);
					}
				}
			}
		};
		tree.addMouseListener(ml);

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
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
					final Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null || !(temp instanceof PObjectNode))
					{
						lastSelection = null;
						infoLabel.setText();
						return;
					}

					setInfoLabelText((PObjectNode)temp);

					leftButton.setEnabled(true);
					rightButton.setEnabled(false);
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
						doCampaign(null, false);
					}
				}
			}
		};
		btree.addMouseListener(ml);

		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private Campaign getSelectedCampaign(boolean select)
	{
		JTreeTable treeTable;
		if (select)
			treeTable = availableTable;
		else
			treeTable = selectedTable;

		PObjectNode pon = (PObjectNode)treeTable.getTree().getLastSelectedPathComponent();
		if (pon != null)
		{
			if (pon.getItem() instanceof Campaign)
			{
				return (Campaign)pon.getItem();
			}
		}
		return null;
	}

	/**
	 * Creates the ClassModel that will be used.
	 */
	protected void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	protected void createAvailableModel()
	{
		if (availableModel == null)
			availableModel = new CampaignModel(viewMode, true);
		else
			availableModel.resetModel(viewMode, true, false);
	}

	protected void createSelectedModel()
	{
		if (selectedModel == null)
			selectedModel = new CampaignModel(viewSelectMode, false);
		else
			selectedModel.resetModel(viewSelectMode, false, false);
	}


	/** The basic idea of the TreeTableModel is that there is a single <code>root</code>
	 *  object.  This root object has a null <code>parent</code>.  All other objects
	 *  have a parent which points to a non-null object.  parent objects contain a list of
	 *  <code>children</code>, which are all the objects that point to it as their parent.
	 *  objects (or <code>nodes</code>) which have 0 children are leafs (the end of that
	 *  linked list).  nodes which have at least 1 child are not leafs. Leafs are like files
	 *  and non-leafs are like directories.
	 */
	public class CampaignModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// Types of the columns.
		protected int modelType = MODEL_AVAIL;
		final String[] availNameList = {"Source Material"};
		final String[] selNameList = {"Source Material", "Loaded"};

		/**
		 * Creates a CampaignModel
		 */
		public CampaignModel(int mode, boolean available)
		{
			super(null);
			if (!available)
				modelType = MODEL_SELECT;
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the CampaignModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			Iterator fI;
			if (available)
				fI = Globals.getCampaignList().iterator();
			else
				fI = selectedCampaigns.iterator();

			switch (mode)
			{
				case VIEW_PUBFMTSET: // by Publisher/Format/Setting/Product Name
					setRoot((PObjectNode)MainSource.typePubFmtSetRoot.clone());
					for (; fI.hasNext();)
					{
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						final Campaign aCamp = (Campaign)fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !Globals.isInGameMode(aCamp.getGame()))
							continue;
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
							continue;
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (aCamp.isType(rootAsPObjectNode.getChild(i).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
							{
								// Items with less than 2 types will not show up unless we do this
								PObjectNode[] d;
								if (aCamp.getMyTypeCount() < 2)
								{
									d = new PObjectNode[1];
									d[0] = rootAsPObjectNode.getChild(i);
								}
								else
								{
									d = rootAsPObjectNode.getChild(i).getChildren();
								}
								// if there's no second level to drill-down to, just add the source here
								if (d != null && d.length == 0)
								{
									PObjectNode aFN = new PObjectNode();
									aFN.setParent(rootAsPObjectNode.getChild(i));
									aFN.setItem(aCamp);
									aFN.setIsValid(aCamp.passesPreReqTests());
									rootAsPObjectNode.getChild(i).addChild(aFN);
									added = true;
								}
								for (int j = 0; d != null && j < d.length; j++)
								{
									if (aCamp.isType(d[j].getItem().toString()) || (!added && j == d.length - 1))
									{
										// Items with less than 3 types will not show up unless we do this
										PObjectNode[] e;
										if (aCamp.getMyTypeCount() == 2)
										{
											e = new PObjectNode[1];
											e[0] = d[j];
										}
										else if (aCamp.getMyTypeCount() < 2)
										{
											e = new PObjectNode[1];
											e[0] = rootAsPObjectNode.getChild(i);
										}
										else
										{
											e = d[j].getChildren();
										}
										for (int k = 0; e != null && k < e.length; k++)
										{
											if (aCamp.isType(e[k].getItem().toString()) || (!added && k == e.length - 1))
											{
												PObjectNode aFN = new PObjectNode();
												aFN.setParent(e[k]);
												aFN.setItem(aCamp);
												aFN.setIsValid(aCamp.passesPreReqTests());
												e[k].addChild(aFN);
												added = true;
											}
										}
									}
								}
							}
						}
					}
					break;
				case VIEW_PUBSET: // by Publisher/Setting/Product Name
					setRoot((PObjectNode)MainSource.typePubSetRoot.clone());
					for (; fI.hasNext();)
					{
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						final Campaign aCamp = (Campaign)fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !Globals.isInGameMode(aCamp.getGame()))
							continue;
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
							continue;
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							if (aCamp.isType(rootAsPObjectNode.getChild(i).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
							{
								// Items with less than 3 types will not show up unless we do this
								PObjectNode[] d;
								if (aCamp.getMyTypeCount() < 3)
								{
									d = new PObjectNode[1];
									d[0] = rootAsPObjectNode.getChild(i);
								}
								else
								{
									d = rootAsPObjectNode.getChild(i).getChildren();
								}
								for (int k = 0; d != null && k < d.length; k++)
								{
									// Don't add children to items (those with only 1 type)
									if (!(d[k].getItem() instanceof PObject))
									{
										if (aCamp.isType(d[k].getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
										{
											PObjectNode aFN = new PObjectNode();
											aFN.setParent(d[k]);
											aFN.setItem(aCamp);
											aFN.setIsValid(aCamp.passesPreReqTests());
											d[k].addChild(aFN);
											added = true;
										}
									}
								}
							}
						}
					}
					break;
				case VIEW_PUBLISH: // by Publisher/Product Name
					setRoot((PObjectNode)MainSource.typePubRoot.clone());
					for (; fI.hasNext();)
					{
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						final Campaign aCamp = (Campaign)fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !Globals.isInGameMode(aCamp.getGame()))
							continue;
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
							continue;
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); i++)
						{
							//if we've matched Publisher, or we've reached the last node ("Other") then add it here
							if (aCamp.isType(rootAsPObjectNode.getChild(i).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
							{
								PObjectNode aFN = new PObjectNode();
								aFN.setParent(rootAsPObjectNode.getChild(i));
								aFN.setItem(aCamp);
								aFN.setIsValid(aCamp.passesPreReqTests());
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;
							}
						}
					}
					break;
				case VIEW_PRODUCT: // by Product Name
					setRoot(new PObjectNode()); // just need a blank one
					for (; fI.hasNext();)
					{
						PObjectNode rootAsPObjectNode = (PObjectNode)root;
						final Campaign aCamp = (Campaign)fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !Globals.isInGameMode(aCamp.getGame()))
							continue;
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
							continue;
						PObjectNode aFN = new PObjectNode();
						aFN.setParent(rootAsPObjectNode);
						aFN.setItem(aCamp);
						aFN.setIsValid(aCamp.passesPreReqTests());
						rootAsPObjectNode.addChild(aFN);
					}
					break;
				default:
					Globals.errorPrint("In MainSource.CampaignlModel.resetModel the mode " + mode + " is not handled.");
					break;

			}
			PObjectNode rootAsPObjectNode = (PObjectNode)root;
			if (!newCall && rootAsPObjectNode.getChildCount() > 0)
				fireTreeNodesChanged(root, rootAsPObjectNode.getChildren(), null, null);
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final Campaign aCamp)
		{
			return (modelType == MODEL_SELECT || accept(aCamp));
		}

		// "There can be only one!" There must be a root object, though it can be hidden
		// to make it's existence basically a convenient way to keep track of the objects
		public void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}


		public void removeItemFromNodes(PObjectNode p, Object e)
		{
			if (p == null)
				p = (PObjectNode)root;

			// if no children, remove it and update parent
			if (p.getChildCount() == 0 && p.getItem().equals(e))
			{
				p.getParent().removeChild(p);
			}
			else
			{
				for (int i = 0; i < p.getChildCount(); i++)
				{
					removeItemFromNodes(p.getChild(i), e);
				}
			}
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			if (modelType == MODEL_AVAIL)
				return availNameList.length;
			else
				return selNameList.length;
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			if (modelType == MODEL_AVAIL)
				return availNameList[column];
			else
				return selNameList[column];
		}

		/**
		 * Returns Campaign for the column.
		 */
		public Class getColumnClass(int column)
		{
			switch (column)
			{
				case COL_LOADED: //is source loaded?
					return String.class;
				case COL_NAME: //source material name
					return TreeTableModel.class;
				default:
					Globals.errorPrint("In MainSource.CampaignModel.getColumnClass the column " + column + " is not handled.");
					break;
			}
			return String.class;
		}

		// true for first column so that it highlights
		public boolean isCellEditable(Object node, int column)
		{
			return (column == 0);
		}

		/**
		 * Returns Object value of the column.
		 */
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode)node;
			Campaign aCamp = null;
			if (fn != null && (fn.getItem() instanceof Campaign))
				aCamp = (Campaign)fn.getItem();

			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in MainSource.");
						return "";
					}
				case COL_LOADED: //is source loaded?
					if (aCamp != null)
					{
						if (aCamp.isLoaded())
							return "Y";
						else
							return "N";
					}
					break;
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Globals.errorPrint("Somehow we have no active node when doing getValueAt in MainSource.");
						return null;
					}
				default:
					Globals.errorPrint("In MainSource.CampaignModel.getValueAt the column " + column + " is not handled.");
					break;
			}
			return null;
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
		FilterFactory.registerAllCampaignFilters(this);
	}

	/**
	 * implementation of Filterable interface
	 */
	public void refreshFiltering()
	{
		/*
		 * this does probably too much ...
		 * but I'm too lazy to get into the details here!
		 *
		 * author: Thomas Behr 04-03-02
		 */
		updateModels();
		selectCampaignsByFilename(PersistenceManager.getChosenCampaignSourcefiles());
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

	/*
	 * currently there are no useful filters for this tab.
	 * maybe we should remove filtering altogether.
	 *
	 * author: Thomas Behr 04-03-02
	 */
	/**
	 * specifies the filter selection mode
	 */
	public int getSelectionMode()
	{
		return DISABLED_MODE;
	}

	public void refreshCampaigns()
	{
		Globals.debugPrint("REFRESHING");
		PersistenceManager.refreshCampaigns();
		updateModels();
	}
}
