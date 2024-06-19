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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:18:47 $
 *
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
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
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
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.gui.utils.AbstractTreeTableModel;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JComboBoxEx;
import pcgen.gui.utils.JLabelPane;
import pcgen.gui.utils.JTreeTable;
import pcgen.gui.utils.LabelTreeCellRenderer;
import pcgen.gui.utils.PObjectNode;
import pcgen.gui.utils.TreeTableModel;
import pcgen.gui.utils.Utility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.PersistenceManager;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

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
	// For I18N and so that PCGen_Frame1 can use this as a tooltip
	public static final String SOURCE_MATERIALS_TAB = "Select and load source materials";

	static final long serialVersionUID = -2654080650560664447L;
	private final JLabel avaLabel = new JLabel(/*"Available"*/);
	private final JLabel selLabel = new JLabel(/*"Selected"*/);
	private JButton leftButton;
	private JButton rightButton;
	private JScrollPane cScroll;
	private JLabelPane infoLabel;
	private JPanel center = new JPanel();
	private FlippingSplitPane splitPane;
	private FlippingSplitPane bsplit;
	private FlippingSplitPane asplit;
	private CampaignModel availableModel = null;  // Model for the TreeTable.
	private CampaignModel selectedModel = null;   // Model for the JTreeTable.
	private JTreeTable availableTable;  // the available Campaigns
	private JTreeTable selectedTable;   // the selected Campaigns
	private PObjectNode lastSelection = null; //keep track of which PObjectNode was last selected from either table
	private JComboBoxEx viewComboBox = new JComboBoxEx();
	private JComboBoxEx viewSelectComboBox = new JComboBoxEx();
	private static int splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;

	private boolean sourcesLoaded = false;

	//column positions for tables
	private static final int COL_NAME = 0;
	private static final int COL_LOADED = 1;

	//view modes for tables
	private static final int VIEW_PRODUCT = 0;
	private static final int VIEW_PUBLISH = 1;
	private static final int VIEW_PUBSET = 2;
	private static final int VIEW_PUBFMTSET = 3;
	private static int viewMode = VIEW_PUBFMTSET; // keep track of what view mode we're in for Available
	private static int viewSelectMode = VIEW_PRODUCT; // keep track of what view mode we're in for Selected. defaults to "Name"

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

	private static PObjectNode typePubRoot;
	private static PObjectNode typePubSetRoot;
	private static PObjectNode typePubFmtSetRoot;

	private List selectedCampaigns = new ArrayList();

	public MainSource()
	{
		// do not remove this
		// we will use the component's name to save component specific settings
		setName(Constants.tabNames[Constants.TAB_SOURCES]);

		try
		{
			initComponents();
		}
		catch (Exception e)
		{
			GuiFacade.showMessageDialog(null, "Error in MainSource whilst initialising:\n " + e.toString() + "\n" + "PCGen may not operate correctly as a result of this error. ", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("Error initialising MainSource: " + e.toString(), e);
		}
		initActionListeners();

		FilterFactory.restoreFilterSettings(this);
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
		splitPane = new FlippingSplitPane(splitOrientation, leftPane, rightPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);

		center.add(splitPane, BorderLayout.CENTER);

		Utility.buildConstraints(c, 0, 0, 1, 1, 100, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		JPanel aPanel = new JPanel();
		gridbag.setConstraints(aPanel, c);
		avaLabel.setText(PropertyFactory.getString("in_available"));
		aPanel.add(avaLabel);
		aPanel.add(viewComboBox);
		ImageIcon newImage;
		newImage = IconUtilitities.getImageIcon("Forward16.gif");
		rightButton = new JButton(newImage);
		Utility.setDescription(rightButton, "Click to add the source");
		rightButton.setEnabled(false);
		aPanel.add(rightButton);
		leftPane.add(aPanel);
		newImage = IconUtilitities.getImageIcon("Refresh16.gif");
		JButton sButton = new JButton(newImage);
		Utility.setDescription(sButton, "Click to change the orientation of the tables");
		sButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (splitOrientation == FlippingSplitPane.VERTICAL_SPLIT)
				{
					splitOrientation = FlippingSplitPane.HORIZONTAL_SPLIT;
				}
				else
				{
					splitOrientation = FlippingSplitPane.VERTICAL_SPLIT;
				}
				splitPane.setOrientation(splitOrientation);
				splitPane.setDividerLocation(.5);
			}
		});
		aPanel.add(sButton);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
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
		selLabel.setText(PropertyFactory.getString("in_selected"));
		aPanel.add(selLabel);
		aPanel.add(viewSelectComboBox);
		newImage = IconUtilitities.getImageIcon("Back16.gif");
		leftButton = new JButton(newImage);
		Utility.setDescription(leftButton, "Click to remove the source");
		leftButton.setEnabled(false);
		aPanel.add(leftButton);
		rightPane.add(aPanel);

		Utility.buildConstraints(c, 0, 1, 1, 1, 0, 95);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		scrollPane = new JScrollPane(selectedTable);
		gridbag.setConstraints(scrollPane, c);
		rightPane.add(scrollPane);

		selectedTable.setColAlign(COL_LOADED, SwingConstants.CENTER);

		Border etched = null;
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
		loadButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				loadCampaigns_actionPerformed();
			}
		});
		jPanel1n.add(loadButton);
		loadButton.setToolTipText("This loads all the sources listed in the above table");

		unloadAllButton.setText("Unload All");
		unloadAllButton.setMnemonic(KeyEvent.VK_U);
		unloadAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				unloadAllCampaigns_actionPerformed();
			}
		});
		jPanel1n.add(unloadAllButton);

		removeAllButton = new JButton("Remove All");
		removeAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				selectAll_actionPerformed(false);
			}
		});
		removeAllButton.setToolTipText("Remove all sources from the above table");
		jPanel1n.add(removeAllButton);

		JButton refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshCampaigns();
			}
		});
		jPanel1s.add(refreshButton);
		refreshButton.setToolTipText("Refresh the list of sources");

		websiteButton = new JButton("Website");
		websiteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				launchProductWebsite(false);
			}
		});
		websiteButton.setToolTipText("Go to the selected product's website");
		websiteButton.setEnabled(false);
		jPanel1s.add(websiteButton);

		JButton pccButton = new JButton("Customise");
		{
			final MainSource t = this;
			pccButton.addActionListener(new ActionListener()
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

		asplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT, cScroll, jPanel1);
		asplit.setOneTouchExpandable(true);
		asplit.setDividerSize(10);

		JPanel botPane = new JPanel();
		botPane.setLayout(new BorderLayout());
		botPane.add(asplit, BorderLayout.CENTER);
		bsplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT, center, botPane);
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
		final List allowedModes = Globals.getAllowedGameModes();

		typePubRoot = new PObjectNode();
		typePubSetRoot = new PObjectNode();
		typePubFmtSetRoot = new PObjectNode();
		List aList = new ArrayList(); //TYPE list
		for (Iterator i = Globals.getCampaignList().iterator(); i.hasNext();)
		{
			final Campaign aCamp = (Campaign) i.next();

			if (allowedModes.contains(aCamp.getGame()))
			{
				if (aCamp.getMyTypeCount() > 0)
				{
					if (!aList.contains(aCamp.getMyType(0)))
					{
						aList.add(aCamp.getMyType(0)); //TYPE[0] = Publisher
					}
				}
			}
		}
		Collections.sort(aList);
		// All non-typed/screwed-up items will end up in an "Other" node
		if (!aList.contains("Other"))
		{
			aList.add("Other");
		}
		PObjectNode p1[] = new PObjectNode[aList.size()];
		PObjectNode p2[] = new PObjectNode[aList.size()];
		PObjectNode p3[] = new PObjectNode[aList.size()];
		for (int i = 0; i < aList.size(); ++i)
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
		typePubRoot.setChildren(p1);
		typePubSetRoot.setChildren(p2);
		typePubFmtSetRoot.setChildren(p3);

		for (int i = 0; i < p2.length; ++i)
		{
			aList.clear();
			for (int j = 0; j < Globals.getCampaignList().size(); ++j)
			{
				final Campaign bCamp = (Campaign) Globals.getCampaignList().get(j);
				final String topType = p2[i].toString();
				if (!bCamp.isType(topType))
				{
					continue;
				}

				if (bCamp.getMyTypeCount() > 2)
				{
					if (allowedModes.contains(bCamp.getGame()))
					{
						if (!aList.contains(bCamp.getMyType(2)))
						{
							aList.add(bCamp.getMyType(2)); //TYPE[2] = Setting
						}
					}
				}
			}
			Collections.sort(aList);
			for (Iterator lI = aList.iterator(); lI.hasNext();)
			{
				String aString = (String) lI.next();
				PObjectNode d = new PObjectNode();
				d.setParent(p2[i]);
				p2[i].addChild(d);
				d.setItem(aString);
			}
		}

		for (int i = 0; i < p3.length; ++i)
		{
			aList.clear();
			for (int j = 0; j < Globals.getCampaignList().size(); ++j)
			{
				final Campaign bCamp = (Campaign) Globals.getCampaignList().get(j);
				final String topType = p3[i].toString();
				if (!bCamp.isType(topType))
				{
					continue;
				}

				if (bCamp.getMyTypeCount() > 1)
				{
					if (allowedModes.contains(bCamp.getGame()))
					{
						if (!aList.contains(bCamp.getMyType(1)))
						{
							aList.add(bCamp.getMyType(1)); //TYPE[1] = Format
						}
					}
				}
			}
			Collections.sort(aList);
			for (Iterator lI = aList.iterator(); lI.hasNext();)
			{
				String aString = (String) lI.next();
				PObjectNode d = new PObjectNode(aString);
				p3[i].addChild(d);
			}

			List p4 = p3[i].getChildren();
			for (int k = 0; p4 != null && k < p4.size(); ++k)
			{
				final PObjectNode p4node = (PObjectNode) p4.get(k);
				aList.clear();
				for (int m = 0; m < Globals.getCampaignList().size(); ++m)
				{
					final Campaign cCamp = (Campaign) Globals.getCampaignList().get(m);
					final String pubType = p4node.getParent().toString();
					final String formatType = p4node.toString();
					if (!cCamp.isType(pubType) || !cCamp.isType(formatType))
					{
						continue;
					}

					if (cCamp.getMyTypeCount() > 2)
					{
						if (allowedModes.contains(cCamp.getGame()))
						{
							if (!aList.contains(cCamp.getMyType(2)))
							{
								aList.add(cCamp.getMyType(2)); //TYPE[2] = Setting
							}
						}
					}
				}
				Collections.sort(aList);
				for (Iterator lI = aList.iterator(); lI.hasNext();)
				{
					String aString = (String) lI.next();
					PObjectNode d = new PObjectNode(aString);
					p4node.addChild(d);
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
				formComponentShown();
			}
		});
		rightButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(true);
			}
		});
		leftButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(false);
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
	}

	private void viewComboBoxActionPerformed()
	{
		final int index = viewComboBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			SettingsHandler.setPCGenOption("pcgen.options.sourceTab.availableListMode", viewMode);
			updateAvailableModel();
		}
	}

	private void viewSelectComboBoxActionPerformed()
	{
		final int index = viewSelectComboBox.getSelectedIndex();
		if (index != viewSelectMode)
		{
			viewSelectMode = index;
			SettingsHandler.setPCGenOption("pcgen.options.sourceTab.selectedListMode", viewSelectMode);
			updateSelectedModel();
		}
	}

	private class CampaignPopupMenu extends JPopupMenu
	{
		static final long serialVersionUID = -2654080650560664447L;

		private class AddCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(true);
			}
		}

		private class AddAllCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectAll_actionPerformed(true);
			}
		}

		private class RemoveCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				doCampaign(false);
			}
		}

		private class RemoveAllCampaignActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				selectAll_actionPerformed(false);
			}
		}

		private class WebActionListener implements ActionListener
		{
			boolean available = true;

			WebActionListener(boolean fromAvail)
			{
				available = fromAvail;
			}

			public void actionPerformed(ActionEvent evt)
			{
				launchProductWebsite(available);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddCampaignActionListener(), "select", (char) 0, accelerator, "Select Source material to load", "Add16.gif", true);
		}

		private JMenuItem createAddAllMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddAllCampaignActionListener(), "selectall", (char) 0, accelerator, "Select All Source material to load", "Add16.gif", true);
		}

		private JMenuItem createRemoveMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveCampaignActionListener(), "deselect", (char) 0, accelerator, "Remove Source material from loading", "Remove16.gif", true);
		}

		private JMenuItem createRemoveAllMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new RemoveAllCampaignActionListener(), "deselectall", (char) 0, accelerator, "Remove All Source material from loading", "Remove16.gif", true);
		}

		private JMenuItem createWebMenuItem(String label, String accelerator, boolean fromAvail)
		{
			return Utility.createMenuItem(label, new WebActionListener(fromAvail), "website", (char) 0, accelerator, "Launch browser to product's website", "Bookmarks16.gif", true);
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
				CampaignPopupMenu.this.add(createAddMenuItem("Select", "shortcut EQUALS"));
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
				CampaignPopupMenu.this.add(createRemoveMenuItem("Remove", "shortcut MINUS"));
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
						for (int i = 0; i < menu.getComponentCount(); ++i)
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
				selRow = tree.getRowForLocation(evt.getX(), evt.getY());
				if (selRow == -1)
				{
					return;
				}
				selPath = tree.getPathForLocation(evt.getX(), evt.getY());
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
		treeTable.addMouseListener(new CampaignPopupListener(treeTable, new CampaignPopupMenu(treeTable)));
	}

	/**
	 * NOTE: this function adds all sources to the selected list in memory, but does NOT update the GUI itself
	 * @param node
	 * @param select
	 */
	private void selectAllLeaves(PObjectNode node, boolean select)
	{
		for (int count = 0; count < node.getChildCount(); ++count)
		{
			// if this child is a leaf, then select it...
			PObjectNode child = node.getChild(count);
			if (child.isLeaf() && child.getItem() instanceof Campaign)
			{
				Campaign aCamp = (Campaign) child.getItem();
				if (aCamp != null)
				{
					if (select)
					{
						if (!selectedCampaigns.contains(aCamp))
						{
							selectedCampaigns.add(aCamp);
						}
					}
					else
					{
						selectedCampaigns.remove(aCamp);
					}
				}
			}
			// ...otherwise recurse, using it as the new parent node
			else
			{
				selectAllLeaves(child, select);
			}
		}
	}

	private void selectAll_actionPerformed(boolean select)
	{
		if (select)
		{
			selectAllLeaves((PObjectNode) availableTable.getTree().getModel().getRoot(), true);
		}
		else
		{
			selectedCampaigns.clear();
			unloadAllCampaigns_actionPerformed();
		}
		updateModels();

		//Remember what we just did...
		rememberSourceChanges();
	}

	private void rememberSourceChanges()
	{
		List campaignStrings = new ArrayList(selectedCampaigns.size());
		for (Iterator campaigns = selectedCampaigns.iterator(); campaigns.hasNext();)
		{
			campaignStrings.add(((Campaign) (campaigns.next())).getSourceFile());
		}
		PersistenceManager.setChosenCampaignSourcefiles(campaignStrings);
	}

	private void loadCampaigns_actionPerformed()
	{
		sourcesLoaded = true;
		loadButton.setEnabled(false);

		final String oldStatus = PCGen_Frame1.getStatusBar().getText();
		PCGen_Frame1.getStatusBar().setText("Loading Sources...");
		PCGen_Frame1.getStatusBar().revalidate(); //even this doesn't seem to get the UI to update
		//PCGen_Frame1.getStatusBar().updateUI(); //even this doesn't seem to get the UI to update
		// ??? updateUI is for notification of LookAndFeel changes, you probably want to call repaint!
		loadCampaigns();
		PCGen_Frame1.enableDisableMenuItems();
		PCGen_Frame1.getStatusBar().setText(oldStatus);
		PCGen_Frame1.restoreFilterSettings(null);
	}

	//this method will now unload all current sources, but will not remove them from the selected table
	private void unloadAllCampaigns_actionPerformed()
	{
		PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
		if (Logging.isDebugMode()) //don't force PC closure if we're in debug mode, per request
		{
			GuiFacade.showMessageDialog(this, "PC's are not closed in debug mode.  " + "Please be aware that they may not function correctly " + "until campaign data is loaded again.", Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
		}
		else
		{
			parent.closeAllPCs();
			if (PCGen_Frame1.getBaseTabbedPane().getTabCount() > PCGen_Frame1.FIRST_CHAR_TAB) // All non-player tabs will be first
			{
				GuiFacade.showMessageDialog(this, "Can't unload campaigns until all PC's are closed.", Constants.s_APPNAME, GuiFacade.INFORMATION_MESSAGE);
				return;
			}
		}

		Globals.emptyLists();
		PersistenceManager.emptyLists();
		PersistenceManager.setChosenCampaignSourcefiles(new ArrayList());
		for (Iterator it = Globals.getCampaignList().iterator(); it.hasNext();)
		{
			Campaign aCamp = (Campaign) it.next();
			aCamp.setIsLoaded(false);
		}

		parent.enableLstEditors(false);

		sourcesLoaded = false;
		PCGen_Frame1.enableDisableMenuItems();
		updateModels();
	}

	/**
	 * This is called when the tab is shown.
	 */
	private void formComponentShown()
	{
		requestFocus();
		PCGen_Frame1.getStatusBar().setText(SOURCE_MATERIALS_TAB);
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
		//The original intent of this method was to allow MainSource to
		// resync it's list of selected campaigns (and refresh the UI)
		// in the event that an external class updated them
		//That code has since dissappeared, so I'll reinstate it
		// along with the code that came to take its place
		// -Lone Jedi (Aug. 14, 2002)

		selectedCampaigns.clear();
		Iterator campIter = Globals.getCampaignList().iterator();
		while (campIter.hasNext())
		{
			Campaign aCamp = (Campaign) campIter.next();
			if (aCamp.isLoaded())
			{
				selectedCampaigns.add(aCamp);
			}
		}
		refreshCampaigns();

		if ((getParent() != null) && Globals.displayListsHappy())
		{
			PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
			parent.enableLstEditors(true);
		}

	}

	private void loadCampaigns()
	{
		if (selectedCampaigns.size() < 1)
		{
			return;
		}

		try
		{
			PersistenceManager.loadCampaigns(selectedCampaigns);
		}
		catch (PersistenceLayerException e)
		{
			GuiFacade.showMessageDialog(this, e.getMessage(), Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
		}
		Globals.sortCampaigns();

		if ((getParent() != null) && Globals.displayListsHappy())
		{
			PCGen_Frame1 parent = PCGen_Frame1.getRealParentFrame(this);
			parent.enableLstEditors(true);
		}

		updateModels();
	}

	private void doCampaign(boolean select)
	{
		if (lastSelection == null)
		{
			return;
		}

		if (lastSelection.getItem() instanceof Campaign)
		{
			final Campaign theCamp = (Campaign) lastSelection.getItem();
			if (select)
			{
				if (!selectedCampaigns.contains(theCamp))
				{
					selectedCampaigns.add(theCamp);
				}
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

		updateModels();

		//ensure that the target skill gets displayed in the selectedTable if you've just added a source
		if (select)
		{
			selectedTable.expandByPObjectName(lastSelection.getItem().toString());
		}

		//Remember what we just did...
		rememberSourceChanges();
	}

	private void launchProductWebsite(boolean avail)
	{
		JTreeTable treeTable;
		if (avail)
		{
			treeTable = availableTable;
		}
		else
		{
			treeTable = selectedTable;
		}

		final PObjectNode pon = (PObjectNode) treeTable.getTree().getLastSelectedPathComponent();
		if (pon != null)
		{
			if (pon.getItem() instanceof Campaign)
			{
				final Campaign theCamp = (Campaign) pon.getItem();
				final String theURL = theCamp.getSourceWeb();
				if (!theURL.equals(""))
				{
					try
					{
						BrowserLauncher.openURL(theURL);
					}
					catch (IOException ioEx)
					{
						Logging.errorPrint("Could not open browser to " + theURL, ioEx);
						GuiFacade.showMessageDialog(null, "Could not open browser to " + theURL, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					}
				}
				else
				{
					GuiFacade.showMessageDialog(null, "No web information found for Source: " + theCamp.getName(), Constants.s_APPNAME, GuiFacade.WARNING_MESSAGE);
				}
			}
		}
		else
		{
			GuiFacade.showMessageDialog(null, "Please select a source.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
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
		unloadAllCampaigns_actionPerformed();
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

	private void updateModels()
	{
		updateAvailableModel();
		updateSelectedModel();

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
			loadButton.setEnabled(!sourcesLoaded);
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
			final String element = (String) iter.next();
			final Campaign aCampaign = Globals.getCampaignByFilename(element);
			if (aCampaign != null)
			{
				if (!selectedCampaigns.contains(aCampaign))
				{
					selectedCampaigns.add(aCampaign);
					updateModels();
				}
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
				final Campaign aCamp = (Campaign) aNode.getItem();
				// We can turn on the website button, since now there is a source, if there is a URL for the campaign
				websiteButton.setEnabled(!aCamp.getSourceWeb().equals(""));

				StringBuffer b = new StringBuffer();
				b.append("<html><b>").append(aCamp.getName()).append("</b><br>");
				b.append("<b>TYPE</b>: ").append(aCamp.getType());
				b.append("&nbsp; <b>RANK</b>: ").append(aCamp.getRank());
				b.append("&nbsp; <b>GAME MODE</b>: ").append(aCamp.getGame());
				String bString = aCamp.getSource();
				if (bString.length() > 0)
				{
					b.append("&nbsp; <b>SOURCE</b>: ").append(bString);
				}

				boolean infoDisplayed = false;
				bString = aCamp.getInfoText();
				if (bString.length() > 0)
				{
					b.append("<p><b>INFORMATION</b>:<br>").append(bString).append("</p>");
					infoDisplayed = true;
				}

				bString = aCamp.getSection15Info();
				if (bString.length() != 0)
				{
					if (!infoDisplayed)
					{
						b.append("<br");
					}
					b.append("<b>COPYRIGHT</b>:<br>").append(bString);
				}
				b.append("</html>");
				infoLabel.setText(b.toString());
			}
			else  //must just be a branch node
			{
				// We off the website button since our source went away
				websiteButton.setEnabled(false);

				PObjectNode pathNode = aNode;
				String path = pathNode.getItem().toString();
				while (pathNode.getParent() != availableTable.getTree().getModel().getRoot() && pathNode.getParent() != selectedTable.getTree().getModel().getRoot())
				{
					pathNode = pathNode.getParent();
					path = pathNode.getItem().toString() + "." + path;
				}
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
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = availableTable.getTree().getPathForRow(idx).getLastPathComponent();
					/////////////////////////
					if (temp == null || !(temp instanceof PObjectNode))
					{
						GuiFacade.showMessageDialog(null, "No campaign selected. Try again.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
						return;
					}

					setInfoLabelText((PObjectNode) temp);

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
				final int mlSelRow = tree.getRowForLocation(e.getX(), e.getY());
				final TreePath mlSelPath = tree.getPathForLocation(e.getX(), e.getY());
				if (mlSelRow != -1)
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
								doCampaign(true);
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
					final int idx = getSelectedIndex(e);
					if (idx < 0)
					{
						return;
					}
					final Object temp = selectedTable.getTree().getPathForRow(idx).getLastPathComponent();
					if (temp == null || !(temp instanceof PObjectNode))
					{
						lastSelection = null;
						infoLabel.setText();
						return;
					}

					setInfoLabelText((PObjectNode) temp);

					leftButton.setEnabled(true);
					rightButton.setEnabled(false);
				}
			}
		});
		ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				final int mlSelRow = btree.getRowForLocation(e.getX(), e.getY());
				final TreePath mlSelPath = btree.getPathForLocation(e.getX(), e.getY());
				if (mlSelRow != -1)
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
								doCampaign(false);
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

	/**
	 * Creates the ClassModel that will be used.
	 */
	private void createModels()
	{
		createAvailableModel();
		createSelectedModel();
	}

	private void createAvailableModel()
	{
		if (availableModel == null)
		{
			availableModel = new CampaignModel(viewMode, true);
		}
		else
		{
			availableModel.resetModel(viewMode, true, false);
		}
	}

	private void createSelectedModel()
	{
		if (selectedModel == null)
		{
			selectedModel = new CampaignModel(viewSelectMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSelectMode, false, false);
		}
	}

	/**
	 * The basic idea of the TreeTableModel is that there is a single
	 * <code>root</code> object. This root object has a null
	 * <code>parent</code>.  All other objects have a parent which
	 * points to a non-null object.  parent objects contain a list of
	 * <code>children</code>, which are all the objects that point
	 * to it as their parent.
	 * objects (or <code>nodes</code>) which have 0 children
	 * are leafs (the end of that linked list).
	 * nodes which have at least 1 child are not leafs.
	 * Leafs are like files and non-leafs are like directories.
	 **/
	final class CampaignModel extends AbstractTreeTableModel
	{
		// Types of the columns.
		int modelType = MODEL_AVAIL;
		final String[] availNameList = {"Source Material"};
		final String[] selNameList = {"Source Material", "Loaded"};

		/**
		 * Creates a CampaignModel
		 */
		public CampaignModel(int mode, boolean available)
		{
			super(null);
			if (!available)
			{
				modelType = MODEL_SELECT;
			}
			resetModel(mode, available, true);
		}

		/**
		 * This assumes the CampaignModel exists but needs to be repopulated
		 */
		public void resetModel(int mode, boolean available, boolean newCall)
		{
			final List allowedModes = Globals.getAllowedGameModes();

			Iterator fI;
			if (available)
			{
				fI = Globals.getCampaignList().iterator();
			}
			else
			{
				fI = selectedCampaigns.iterator();
			}

			switch (mode)
			{
				case VIEW_PUBFMTSET: // by Publisher/Format/Setting/Product Name
					setRoot((PObjectNode) MainSource.typePubFmtSetRoot.clone());
					while (fI.hasNext())
					{
						PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
						final Campaign aCamp = (Campaign) fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !allowedModes.contains(aCamp.getGame()))
						{
							continue;
						}
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
						{
							continue;
						}
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
						{
							if (aCamp.isType(rootAsPObjectNode.getChild(i).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
							{
								// Items with less than 2 types will not show up unless we do this
								List d;
								if (aCamp.getMyTypeCount() < 2)
								{
									d = new ArrayList(1);
									d.add(rootAsPObjectNode.getChild(i));
								}
								else
								{
									d = rootAsPObjectNode.getChild(i).getChildren();
								}
								// if there's no second level to drill-down to, just add the source here
								if (d != null && d.size() == 0)
								{
									PObjectNode aFN = new PObjectNode(aCamp);
									aFN.setIsValid(aCamp.passesPreReqToGain());
									rootAsPObjectNode.getChild(i).addChild(aFN);
									added = true;
								}
								for (int j = 0; d != null && j < d.size(); ++j)
								{
									if (aCamp.isType(((PObjectNode) d.get(j)).getItem().toString()) || (!added && j == d.size() - 1))
									{
										// Items with less than 3 types will not show up unless we do this
										List e;
										if (aCamp.getMyTypeCount() == 2)
										{
											e = new ArrayList(1);
											e.add(d.get(j));
										}
										else if (aCamp.getMyTypeCount() < 2)
										{
											e = new ArrayList(1);
											e.add(rootAsPObjectNode.getChild(i));
										}
										else
										{
											e = ((PObjectNode) d.get(j)).getChildren();
										}
										for (int k = 0; e != null && k < e.size(); ++k)
										{
											if (aCamp.isType(((PObjectNode) e.get(k)).getItem().toString()) || (!added && k == e.size() - 1))
											{
												PObjectNode aFN = new PObjectNode(aCamp);
												aFN.setIsValid(aCamp.passesPreReqToGain());
												((PObjectNode) e.get(k)).addChild(aFN);
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
					setRoot((PObjectNode) MainSource.typePubSetRoot.clone());
					while (fI.hasNext())
					{
						PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
						final Campaign aCamp = (Campaign) fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !allowedModes.contains(aCamp.getGame()))
						{
							continue;
						}
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
						{
							continue;
						}
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
						{
							if (aCamp.isType(rootAsPObjectNode.getChild(i).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
							{
								// Items with less than 3 types will not show up unless we do this
								List d;
								if (aCamp.getMyTypeCount() < 3)
								{
									d = new ArrayList(1);
									d.add(rootAsPObjectNode.getChild(i));
								}
								else
								{
									d = rootAsPObjectNode.getChild(i).getChildren();
								}
								for (int k = 0; d != null && k < d.size(); ++k)
								{
									// Don't add children to items (those with only 1 type)
									if (!(((PObjectNode) d.get(k)).getItem() instanceof PObject))
									{
										if (aCamp.isType(((PObjectNode) d.get(k)).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
										{
											PObjectNode aFN = new PObjectNode(aCamp);
											aFN.setIsValid(aCamp.passesPreReqToGain());
											((PObjectNode) d.get(k)).addChild(aFN);
											added = true;
										}
									}
								}
							}
						}
					}
					break;
				case VIEW_PUBLISH: // by Publisher/Product Name
					setRoot((PObjectNode) MainSource.typePubRoot.clone());
					while (fI.hasNext())
					{
						PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
						final Campaign aCamp = (Campaign) fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !allowedModes.contains(aCamp.getGame()))
						{
							continue;
						}
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
						{
							continue;
						}
						boolean added = false;
						for (int i = 0; i < rootAsPObjectNode.getChildCount(); ++i)
						{
							//if we've matched Publisher, or we've reached the last node ("Other") then add it here
							if (aCamp.isType(rootAsPObjectNode.getChild(i).getItem().toString()) || (!added && i == rootAsPObjectNode.getChildCount() - 1))
							{
								PObjectNode aFN = new PObjectNode();
								aFN.setParent(rootAsPObjectNode.getChild(i));
								aFN.setItem(aCamp);
								aFN.setIsValid(aCamp.passesPreReqToGain());
								rootAsPObjectNode.getChild(i).addChild(aFN);
								added = true;
							}
						}
					}
					break;
				case VIEW_PRODUCT: // by Product Name
					setRoot(new PObjectNode()); // just need a blank one
					while (fI.hasNext())
					{
						PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
						final Campaign aCamp = (Campaign) fI.next();
						// filter out campaigns here
						if (!shouldDisplayThis(aCamp) || !allowedModes.contains(aCamp.getGame()))
						{
							continue;
						}
						//don't display selected campaigns in the available table
						if (available && selectedCampaigns.contains(aCamp))
						{
							continue;
						}
						PObjectNode aFN = new PObjectNode();
						aFN.setParent(rootAsPObjectNode);
						aFN.setItem(aCamp);
						aFN.setIsValid(aCamp.passesPreReqToGain());
						rootAsPObjectNode.addChild(aFN);
					}
					break;
				default:
					Logging.errorPrint("In MainSource.CampaignlModel.resetModel the mode " + mode + " is not handled.");
					break;

			}
			PObjectNode rootAsPObjectNode = (PObjectNode) super.getRoot();
			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeNodesChanged(super.getRoot(), new TreePath(super.getRoot()));
			}
		}

		/**
		 * return a boolean to indicate if the item should be included in the list.
		 * Only Weapon, Armor and Shield type items should be checked for proficiency.
		 */
		private boolean shouldDisplayThis(final Campaign aCamp)
		{
			if (aCamp.getName().length() == 0)
			{
				return false;
			}
			return (modelType == MODEL_SELECT || accept(aCamp));
		}

		// There must be a root object, but we keep it "hidden"
		public void setRoot(PObjectNode aNode)
		{
			super.setRoot(aNode);
		}

		// return the root node
		public final Object getRoot()
		{
			return (PObjectNode) super.getRoot();
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns.
		 */
		public int getColumnCount()
		{
			if (modelType == MODEL_AVAIL)
			{
				return availNameList.length;
			}
			else
			{
				return selNameList.length;
			}
		}

		/**
		 * Returns String name of a column.
		 */
		public String getColumnName(int column)
		{
			if (modelType == MODEL_AVAIL)
			{
				return availNameList[column];
			}
			else
			{
				return selNameList[column];
			}
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
					Logging.errorPrint("In MainSource.CampaignModel.getColumnClass the column " + column + " is not handled.");
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
			final PObjectNode fn = (PObjectNode) node;
			Campaign aCamp = null;
			if (fn != null && (fn.getItem() instanceof Campaign))
			{
				aCamp = (Campaign) fn.getItem();
			}

			switch (column)
			{
				case COL_NAME: // Name
					if (fn != null)
					{
						return fn.toString();
					}
					else
					{
						Logging.errorPrint("Somehow we have no active node when doing getValueAt in MainSource.");
						return "";
					}
				case COL_LOADED: //is source loaded?
					if (aCamp != null)
					{
						if (aCamp.isLoaded())
						{
							return "Y";
						}
						else
						{
							return "N";
						}
					}
					break;
				case -1:
					if (fn != null)
					{
						return fn.getItem();
					}
					else
					{
						Logging.errorPrint("Somehow we have no active node when doing getValueAt in MainSource.");
						return null;
					}
				default:
					Logging.errorPrint("In MainSource.CampaignModel.getValueAt the column " + column + " is not handled.");
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
		/*
		 * quick and dirty fix to prevent Game Mode filters
		 * to be registered and subsequently saved to filter.ini
		 *
		 * this tab does not support filtering anymore,
		 * so no harm done
		 *
		 * author Thomas Behr 08-09-02
		 */
//  		FilterFactory.registerAllCampaignFilters(this);
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
		PersistenceManager.refreshCampaigns();
		updateModels();
	}
}
