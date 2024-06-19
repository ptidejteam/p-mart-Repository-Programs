/*************************************************************************
 *
 * InfoResources.java
 *
 *************************************************************************
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
 *************************************************************************
 *
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:00:22 $
 *
 *************************************************************************/

package pcgen.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.character.CompanionMod;
import pcgen.core.character.Follower;
import pcgen.gui.filter.FilterAdapterPanel;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.panes.FlippingSplitPane;
import pcgen.io.PCGIOHandler;

/**
 *
 *  <code>InfoResources</code> creates a new tabbed panel that is used to
 *  allow creating/adding familiars, cohorts, companions, intelligent items
 *  vehicles and buildings

 * @author Jayme Cox <jaymecox@netscape.net>
 * @version  $Revision: 1.1 $
 *
 **/
class InfoResources extends FilterAdapterPanel
{

	private FollowerModel availableModel = null;  // available Model
	private FollowerModel selectedModel = null;   // selected Model
	private JTreeTable availableTable;       // available table
	private JTreeTable selectedTable;        // selected table
	private JTreeTableSorter availableSort = null;
	private JTreeTableSorter selectedSort = null;

	private JLabelPane infoLabel = new JLabelPane();
	private JLabelPane followerInfo = new JLabelPane();

	private JButton addButton = new JButton();
	private JButton delButton = new JButton();
	private JButton loadButton = new JButton();
	private JButton updateButton = new JButton();
	private JButton addModButton = new JButton();
	private JButton delModButton = new JButton();

	private JPanel masterPane = new JPanel();
	private JPanel followerPane = new JPanel();
	private JPanel modePane = new JPanel();
	private JPanel topPane = new JPanel();
	private JPanel botPane = new JPanel();

	private FlippingSplitPane topSplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT);
	private FlippingSplitPane botSplit = new FlippingSplitPane(FlippingSplitPane.HORIZONTAL_SPLIT);
	private FlippingSplitPane centerSplit = new FlippingSplitPane(FlippingSplitPane.VERTICAL_SPLIT);

	private Border etched;
	private static PlayerCharacter aPC = null;

	private TreePath selPath;
	private int selRow;

	private boolean needsUpdate = true;
	private boolean hasBeenSized = false;

	private final JLabel modeLabel = new JLabel("Select Type of Resource:");
	private JComboBox viewModeBox = new JComboBox();
	private final JLabel sortLabel = new JLabel("Sort");
	private JComboBox viewSortBox = new JComboBox();

	private int viewMode = 0;
	private int viewSortMode = 0;

	// table model modes
	private static final int MODEL_AVAILABLE = 0;
	private static final int MODEL_SELECTED = 1;

	/**
	 * create right click menus and listeners
	 **/
	private class resPopupMenu extends JPopupMenu
	{
		private class resActionListener implements ActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
			}
		}

		private class AddActionListener extends resActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addButton(evt);
			}
		}

		private class DelActionListener extends resActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				delButton(evt);
			}
		}

		private class LoadActionListener extends resActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				loadButton(evt);
			}
		}

		private class AddFileActionListener extends resActionListener
		{
			public void actionPerformed(ActionEvent evt)
			{
				addFileButton(evt);
			}
		}

		private JMenuItem createAddMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddActionListener(), "add 1", (char) 0, accelerator, "Add to List", "Add16.gif", true);
		}

		private JMenuItem createDelMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new DelActionListener(), "remove 1", (char) 0, accelerator, "Remove from List", "Remove16.gif", true);
		}

		private JMenuItem createAddFileMenuItem(String label, String accelerator)
		{
			return Utility.createMenuItem(label, new AddFileActionListener(), "add 1", (char) 0, accelerator, "Add Existing File", "Add16.gif", true);
		}

		resPopupMenu(JTreeTable treeTable)
		{
			if (treeTable == availableTable)
			{
				resPopupMenu.this.add(createAddMenuItem("Add New to List", "control EQUALS"));
			}
			else // selectedTable
			{
				resPopupMenu.this.add(createDelMenuItem("Remove from List", "control MINUS"));
				resPopupMenu.this.add(createAddFileMenuItem("Add from existing File", "control PLUS"));
			}
		}
	}

	private class resPopupListener extends MouseAdapter
	{
		private JTree tree;
		private resPopupMenu menu;

		resPopupListener(JTreeTable treeTable, resPopupMenu aMenu)
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
				tree.setSelectionPath(selPath);
				menu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	private void hookupPopupMenu(JTreeTable treeTable)
	{
		treeTable.addMouseListener(new resPopupListener(treeTable, new resPopupMenu(treeTable)));
	}

	/**
	 *  Constructor for the InfoEquips object
	 **/
	InfoResources()
	{
		// do not remove this as we will use the component's name
		// to save component specific settings
		setName("Resources");

		initComponents();

		initActionListeners();

		//FilterFactory.restoreFilterSettings(this);
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
	 **/
	private void initComponents()
	{
		// make sure the current PC is set
		aPC = Globals.getCurrentPC();
		// flesh out all the tree views
		createModels();

		// create tables associated with the above trees
		createTreeTables();

		viewModeBox.addItem("Followers   ");
		//viewModeBox.addItem("Artifacts    ");
		//viewModeBox.addItem("Construction ");
		Utility.setDescription(viewModeBox, "Choose View Mode");

		viewSortBox.addItem("Type ");
		viewSortBox.addItem("Name ");
		Utility.setDescription(viewSortBox, "Sort Sort");
		//viewSelectComboBox.setSelectedIndex(viewSelectMode);

		// create both versions of the GUI
		this.setLayout(new BorderLayout());
		createMasterView();
		createFollowerView();

		// Make sure it updates when switching tabs
		this.addFocusListener(new FocusAdapter()
		{
			public void focusGained(FocusEvent evt)
			{
//Globals.errorPrint("--start-- focusGained: "+aPC.getName());
//				updateCharacterInfo();
//Globals.errorPrint("--done-- focusGained: "+aPC.getName());
			}
		});

		// add the sorter tables to that clicking
		// on the TableHeader does something
		availableSort = new JTreeTableSorter(availableTable, (PObjectNode) availableModel.getRoot(), availableModel);
		selectedSort = new JTreeTableSorter(selectedTable, (PObjectNode) selectedModel.getRoot(), selectedModel);

	}

	/**
	 * This creates the GUI pane that a "master" sees to allow the PC
	 * to add new followers, familiars, artifacts, etc
	 **/
	private void createMasterView()
	{
		masterPane.setLayout(new BorderLayout());
		masterPane.setBorder(BorderFactory.createEtchedBorder());

		// build topPane which will contain leftPane and rightPane
		// leftPane will have two panels and a scrollregion
		// rightPane will have one panel and a scrollregion

		topPane.setLayout(new BorderLayout());

		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		leftPane.setLayout(new BorderLayout());
		rightPane.setLayout(new BorderLayout());

		// split the left and right panes
		topSplit.setLeftComponent(leftPane);
		topSplit.setRightComponent(rightPane);
		topSplit.setOneTouchExpandable(true);
		topSplit.setDividerSize(10);
		topSplit.setBorder(BorderFactory.createEtchedBorder());

		// build the left pane
		// for the available table
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 0));
		aPanel.add(sortLabel);
		aPanel.add(viewSortBox);
		ImageIcon newImage;
		newImage = new ImageIcon(getClass().getResource("resource/Forward16.gif"));
		addButton.setIcon(newImage);
		Utility.setDescription(addButton, "Click to add");
		addButton.setEnabled(false);
		aPanel.add(addButton);

		leftPane.add(aPanel, BorderLayout.NORTH);

		// the available table panel
		JScrollPane scrollPane = new JScrollPane(availableTable);
		Utility.setDescription(scrollPane, "Right click to add");
		leftPane.add(scrollPane, BorderLayout.CENTER);

		availableTable.getColumnModel().getColumn(1).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));
		availableTable.getColumnModel().getColumn(4).setCellRenderer(new AlignCellRenderer(SwingConstants.CENTER));

		// now build the right pane
		// for the selected table
		aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 0));

		newImage = new ImageIcon(getClass().getResource("resource/Back16.gif"));
		delButton.setIcon(newImage);
		Utility.setDescription(delButton, "Click to remove from List");
		delButton.setEnabled(false);
		aPanel.add(delButton);
		loadButton.setText("Load...");
		loadButton.setEnabled(false);
		aPanel.add(loadButton);
		rightPane.add(aPanel, BorderLayout.NORTH);

		scrollPane = new JScrollPane(selectedTable);
		Utility.setDescription(scrollPane, "Right click to remove");
		rightPane.add(scrollPane, BorderLayout.CENTER);

		// add the split pane to the top panel
		topPane.add(topSplit, BorderLayout.CENTER);

		// ---------- build Bottom Panel ----------------
		// botPane will contain a bLeftPane and a bRightPane
		// bLeftPane will contain a scrollregion (current object info)
		// bRightPane will contain a panel and buttons

		botPane.setLayout(new BorderLayout());

		//JPanel bLeftPane = new JPanel();
		JScrollPane bLeftPane = new JScrollPane(infoLabel);
		JPanel bRightPane = new JPanel();

		botSplit.setLeftComponent(bLeftPane);
		botSplit.setRightComponent(bRightPane);
		botSplit.setOneTouchExpandable(true);
		botSplit.setDividerSize(10);

		botPane.add(botSplit, BorderLayout.CENTER);

		// Bottom left panel

		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Information");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		bLeftPane.setBorder(sTitle);
		infoLabel.setBackground(topPane.getBackground());
		bLeftPane.setViewportView(infoLabel);

		// Bottom right panel
		// create a template select and view panel
		JPanel iPanel = new JPanel();
		iPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		addModButton.setText("Add Modifier");
		addModButton.setEnabled(false);
		iPanel.add(addModButton);
		delModButton.setText("Delete Modifier");
		delModButton.setEnabled(false);
		iPanel.add(delModButton);
		bRightPane.add(iPanel);

		//
		// now split the top and bottom Panels
		centerSplit.setTopComponent(topPane);
		centerSplit.setBottomComponent(botPane);
		centerSplit.setOneTouchExpandable(true);
		centerSplit.setDividerSize(10);

		// first add a combobox to select mode to view
		modePane.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		modePane.add(modeLabel, null);
		modePane.add(viewModeBox, null);

		masterPane.add(modePane, BorderLayout.NORTH);

		// Now add centerSplit (which has top and bottom splits)
		masterPane.add(centerSplit, BorderLayout.CENTER);

		// now add the entire mess (centered of course)
		//this.setLayout(new BorderLayout());
		//this.add(masterPane, BorderLayout.CENTER);
	}

	/**
	 * This creates the GUI pane that a "follower" sees
	 * displays the followers stats and let's them update from master
	 **/
	private void createFollowerView()
	{
		followerPane.setLayout(new BorderLayout());

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		updateButton.setText("Update from Master");
		updateButton.setEnabled(true);
		aPanel.add(updateButton);
		followerPane.add(aPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(followerInfo);
		TitledBorder sTitle = BorderFactory.createTitledBorder(etched, "Follower Information");
		sTitle.setTitleJustification(TitledBorder.CENTER);
		scrollPane.setBorder(sTitle);
		followerInfo.setBackground(topPane.getBackground());
		scrollPane.setViewportView(followerInfo);
		scrollPane.setVisible(true);

		followerPane.add(scrollPane, BorderLayout.CENTER);
		followerPane.setVisible(true);

		// now add the entire mess (centered of course)
		//this.setLayout(new BorderLayout());
		//this.add(followerPane, BorderLayout.CENTER);
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
				int i = topSplit.getDividerLocation();
				if (i > 0)
				{
					SettingsHandler.setPCGenOption("InfoResources.topSplit", i);
				}
				i = botSplit.getDividerLocation();
				if (i > 0)
				{
					SettingsHandler.setPCGenOption("InfoResources.botSplit", i);
				}
				i = centerSplit.getDividerLocation();
				if (i > 0)
				{
					SettingsHandler.setPCGenOption("InfoResources.centerSplit", i);
				}
			}
		});
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addButton(evt);
			}
		});
		delButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delButton(evt);
			}
		});
		loadButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				loadButton(evt);
			}
		});
		updateButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				updateButton(evt);
			}
		});
		/*
		addModButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				addModButton(evt);
			}
		});
		delModButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				delModButton(evt);
			}
		});
		*/
		viewModeBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewModeBoxActionPerformed(evt);
			}
		});
		viewSortBox.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				viewSortBoxActionPerformed(evt);
			}
		});
	}

	public final void display()
	{
		formComponentShown(null);
	}

	/**
	 * set the Follower Info text in the FollowerInfo panel
	 **/
	private void setFollowerInfo(Object obj)
	{
		if (obj == null)
		{
			return;
		}
		if (obj instanceof PlayerCharacter)
		{
			//Globals.errorPrint("setFI: " + aPC.getName());
			Follower aF = aPC.getMaster();
			PlayerCharacter mPC = null;
			for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
			{
				PlayerCharacter nPC = (PlayerCharacter) p.next();
				if (aF.getFileName().equals(nPC.getFileName()))
					mPC = nPC;
			}
			if (mPC == null)
			{
				followerInfo.setText("NOTICE: Load Master from File to display info");
				return;
			}
			StringBuffer b = new StringBuffer();
			b.append("<html>");
			b.append(followerStatBlock(aF, aPC));
			//
			// add some of the Master's stats
			//
			b.append("<p>");
			b.append("<font size=+1><b>Master Information</b></font><br>");
			b.append("<b>PC Name:</b> ").append(mPC.getName());
			b.append("<br>");
			b.append("<b>File:</b> ").append(mPC.getFileName());
			b.append("<br>");
			b.append("<b>Race:</b> ").append(mPC.getRace());
			b.append("<br>");
			b.append("<b>").append(Globals.getGameModeHPAbbrev()).append("</b>: ").append(mPC.hitPoints());
			b.append("<br>");
			int bonus = (int) mPC.getBonus(PlayerCharacter.ATTACKBONUS, true);
			b.append("<b>BAB</b>: ").append((bonus >= 0) ? "+" : "").append(bonus);
			b.append("<br>");

			b.append("</html>");

			followerInfo.setText(b.toString());
			followerInfo.setVisible(true);
			followerInfo.repaint();
		}
	}

	/**
	 * Given a Follower and PC, return string of vital stats
	 **/
	private static String followerStatBlock(Follower aF, PlayerCharacter newPC)
	{
		StringBuffer b = new StringBuffer();
		b.append("<font size=+1><b>Name:</b> ");
		b.append(newPC.getName()).append("</font>");
		b.append("<br>");
		b.append("<b>Type:</b> ").append(aF.getType());
		b.append("<br>");
		b.append("<b>Race:</b> ").append(newPC.getRace());
		b.append("<br>");
		for (int i = 0; i < Globals.s_ATTRIBSHORT.length; i++)
		{
			b.append("<b>").append(Globals.s_ATTRIBSHORT[i]).append("</b>: ").append(newPC.getStatList().getTotalStatFor(Globals.s_ATTRIBSHORT[i])).append(" ");
		}
		b.append("<br>");

		if (Globals.getGameModeDefenseText().length() != 0)
		{
			b.append("<b>").append(Globals.getGameModeDefenseText()).append("</b> ");
			b.append("<i>Total</i>: ").append(aPC.getDefenseTotal());
			b.append(" <i>Flatfooted</i>: ").append(aPC.flatfootedDefense());
			b.append(" <i>Touch</i>: ").append(aPC.touchDefense());
			b.append("<br>");
		}
		else
		{
			b.append("<b>AC</b> ");
			b.append("<i>Total</i>: ").append((int) aPC.getTotalBonusTo("COMBAT", "AC", true));
			b.append("<br>");
		}

		final int initMod = newPC.initiativeMod();
		b.append("<b>Init</b>: ").append((initMod >= 0) ? "+" : "").append(initMod);
		b.append("<br>");
		int bonus = (int) newPC.getBonus(PlayerCharacter.ATTACKBONUS, true);
		b.append(" <b>BAB</b>: ").append((bonus >= 0) ? "+" : "").append(bonus);
		b.append("<br>");
		b.append(" <b>").append(Globals.getGameModeHPAbbrev()).append("</b>: ").append(newPC.hitPoints());
		if (Globals.getGameModeWoundPointsText().length() != 0)
		{
			b.append(" <b>").append(Globals.getGameModeWoundPointsAbbrev()).append("</b>: ").append(newPC.woundPoints());
		}
		b.append("<br>");

		b.append("<b>Saves</b>: ");
		for (int z = 0; z < Globals.getCheckList().size(); z++)
		{
			bonus = (int) aPC.getBonus(z + 1, true);
			b.append(" <i>").append(Globals.getCheckList().get(z).toString()).append("</i>: ").append((bonus >= 0) ? "+" : "").append(bonus);
		}

		if (Globals.isStarWarsMode())
		{
			b.append("<br>");
			b.append("<b>Force Points</b>: ").append(newPC.getStrFPoints());
			b.append(" <b>Darkside Points</b>: ").append(newPC.getDPoints());
			b.append(" <b>Reputation</b>: ").append(newPC.reputation());
		}
		b.append("<br>");
		bonus = newPC.getSR();
		if (bonus > 0)
		{
			b.append("<b>Spell Resistance</b>: ").append(newPC.getSR());
		}
		b.append("<br>");
		b.append("<b>Special Abilities:</b>");
		b.append("<ul>");
		for (Iterator ii = newPC.getSpecialAbilityListStrings().iterator(); ii.hasNext();)
		{
			String sa = (String) ii.next();
			b.append("<li>").append(sa).append("</li>");
		}
		b.append("</ul>");
		b.append("<br>");
		return b.toString();
	}

	/*
	 * set the Info text in the Info panel
	 * to the currently selected object
	 */
	private void setInfoText(Object obj)
	{
		if (obj == null)
		{
			return;
		}
		if (obj instanceof Race)
		{
			Race aRace = (Race) obj;
			if (aRace.getName().startsWith("<none"))
			{
				return;
			}
			StringBuffer b = new StringBuffer();
			String bString = "";
			b.append("<html><font size=+1><b>").append(aRace.getName()).append("</b></font>");
			b.append("  <b>Type:</b>").append(aRace.getType());
			if (aRace.getMovements() != null)
			{
				final StringBuffer movelabel = new StringBuffer(aRace.getMovementType(0)).append(" ").append(aRace.getMovement(0)).append(Globals.getAbbrMovementDisplay()).append(" ");
				for (int i = 1; i < aRace.getMovements().length; i++)
				{
					movelabel.append(aRace.getMovementType(i)).append(" ").append(aRace.getMovement(i)).append(Globals.getAbbrMovementDisplay()).append(" ");
				}
				bString = movelabel.toString();
			}
			if (bString.length() > 0)
			{
				b.append(" <b>Move</b>:").append(bString);
			}
			bString = aRace.getSize();
			if (bString.length() > 0)
			{
				b.append(" <b>Size</b>:").append(bString);
			}
			bString = aRace.getSource();
			if (bString.length() > 0)
			{
				b.append(" <b>SOURCE:</b> ").append(bString);
			}
			b.append("</html>");
			infoLabel.setText(b.toString());
		}
		else if (obj instanceof Follower)
		{
			Follower aF = (Follower) obj;
			PlayerCharacter newPC = null;
			for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
			{
				PlayerCharacter nPC = (PlayerCharacter) p.next();
				if (aF.getFileName().equals(nPC.getFileName()))
					newPC = nPC;
			}
			if (newPC == null)
			{
				infoLabel.setText("NOTICE: Load from File to display info");
				return;
			}
			StringBuffer b = new StringBuffer();
			b.append("<html>");
			b.append(followerStatBlock(aF, newPC));
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
		availableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
						infoLabel.setText();
						return;
					}

					PObjectNode fNode = (PObjectNode) temp;
					if (fNode.getItem() != null)
					{
						addButton.setEnabled(true);
						setInfoText(fNode.getItem());
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
						addButton(null);
					}
				}
			}
		};
		atree.addMouseListener(aml);


		// now do the selectedTable and selectedTree

		selectedTable = new JTreeTable(selectedModel);
		selectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
					final Object temp = stree.getPathForRow(idx).getLastPathComponent();
					if (temp == null)
					{
						return;
					}

					PObjectNode fN = (PObjectNode) temp;
					if ((fN.getItem() != null) && !(fN.getItem() instanceof String))
					{
						delButton.setEnabled(true);
						loadButton.setEnabled(true);
						setInfoText(fN.getItem());
						return;
					}
					else
					{
						infoLabel.setText("None");
						loadButton.setEnabled(false);
						return;
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
						stree.setSelectionPath(selPath);
					}
					else if (e.getClickCount() == 2)
					{
						addFileButton(null);
					}
				}
			}
		};
		stree.addMouseListener(sml);

		// create the rightclick popup menus
		hookupPopupMenu(availableTable);
		hookupPopupMenu(selectedTable);
	}

	private void viewModeBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewModeBox.getSelectedIndex();
		if (index != viewMode)
		{
			viewMode = index;
			//SettingsHandler.setResourceTab_Mode(viewMode);
			createModels();
			selectedTable.updateUI();
			availableTable.updateUI();
		}
	}

	private void viewSortBoxActionPerformed(ActionEvent evt)
	{
		final int index = viewSortBox.getSelectedIndex();
		if (index != viewSortMode)
		{
			viewSortMode = index;
			//SettingsHandler.setResourceTab_SortMode(viewSelectMode);
			createAvailableModel();
			availableTable.updateUI();
		}
	}

	// This is called when the tab is shown.
	private void formComponentShown(ComponentEvent evt)
	{
		//Globals.errorPrint("IR--start-- formComponentShown");
		updateCharacterInfo();

		requestFocus();
		PCGen_Frame1.getStatusBar().setText("Add followers/cohorts, animal companions, special mounts or familiars");

		if (aPC.getMaster() != null)
		{
			this.remove(masterPane);
			this.add(followerPane, BorderLayout.CENTER);
			followerPane.setVisible(true);
			masterPane.setVisible(false);
			followerPane.repaint();
		}
		else
		{
			this.remove(followerPane);
			this.add(masterPane, BorderLayout.CENTER);
			masterPane.setVisible(true);
			followerPane.setVisible(false);
			masterPane.repaint();
		}

		int top = topSplit.getDividerLocation();
		int bot = botSplit.getDividerLocation();
		int cent = centerSplit.getDividerLocation();
		int width;
		if (!hasBeenSized)
		{
			hasBeenSized = true;
			Component c = getParent();
			top = SettingsHandler.getPCGenOption("InfoResources.topSplit", (int) (c.getWidth() * 7 / 10));
			bot = SettingsHandler.getPCGenOption("InfoResources.botSplit", (int) (c.getWidth() - 300));
			cent = SettingsHandler.getPCGenOption("InfoResources.centerSplit", (int) (c.getHeight() - 100));

			// set the prefered width on selectedTable
			for (int i = 0; i < selectedTable.getColumnCount(); i++)
			{
				TableColumn sCol = selectedTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ResSel", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(selectedTable, "ResSel", i));
			}

			// set the prefered width on availableTable
			for (int i = 0; i < availableTable.getColumnCount(); i++)
			{
				TableColumn sCol = availableTable.getColumnModel().getColumn(i);
				width = Globals.getCustColumnWidth("ResAva", i);
				if (width != 0)
					sCol.setPreferredWidth(width);
				sCol.addPropertyChangeListener(new ResizeColumnListener(availableTable, "ResAva", i));
			}
		}
		if (top > 0)
		{
			topSplit.setDividerLocation(top);
			SettingsHandler.setPCGenOption("InfoResources.topSplit", top);
		}
		if (bot > 0)
		{
			botSplit.setDividerLocation(bot);
			SettingsHandler.setPCGenOption("InfoResources.botSplit", bot);
		}
		if (cent > 0)
		{
			centerSplit.setDividerLocation(cent);
			SettingsHandler.setPCGenOption("InfoResources.centerSplit", cent);
		}

		availableTable.updateUI();
		selectedTable.updateUI();

		//Globals.errorPrint("IR--done-- formComponentShown");
	}

	// This recalculates the states of everything based
	// upon the currently selected character.
	public final void updateCharacterInfo()
	{
//Globals.errorPrint("IR  --start-- updateCharacterInfo");
		final PlayerCharacter bPC = Globals.getCurrentPC();
		if (bPC == null)
		{
			return;
		}
		if (bPC != aPC)
		{
			needsUpdate = true;
		}
		aPC = bPC;
		if (aPC == null || !needsUpdate)
		{
			return;
		}

		if (aPC.getMaster() != null)
		{
			setFollowerInfo(aPC);
		}
		else
		{
			createModels();
		}

		needsUpdate = false;
//Globals.errorPrint("IR  --done-- updateCharacterInfo");
	}

	private void addFileButton(ActionEvent evt)
	{
		if ("".equals(aPC.getFileName()))
		{
			JOptionPane.showMessageDialog(null, "You must save the current character first", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String target = "";

		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null, "First select destination", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
		{
			target = selCPath.getPathComponent(1).toString();
		}

		String aType = "";

		File file = null;
		file = findPCGFile(file);
		if (file == null || !file.exists())
		{
			return;
		}

		PlayerCharacter newPC = null;
		PlayerCharacter oldPC = aPC;
		int oldIndex = Globals.getRootFrame().getBaseTabbedPane().getSelectedIndex();
		int newIndex = Globals.getRootFrame().FIRST_CHAR_TAB;
		for (Iterator i = Globals.getPCList().iterator(); i.hasNext();)
		{
			PlayerCharacter iPC = (PlayerCharacter) i.next();
			if (iPC.getFileName().equals(file.toString()))
			{
				System.err.println("already open");
				Globals.getRootFrame().getBaseTabbedPane().setSelectedIndex(newIndex);
				newPC = iPC;
				break;
			}
			newIndex++;
		}
		if (newPC == null && Globals.getRootFrame().loadPCFromFile(file))
		{
			newPC = Globals.getCurrentPC();
		}
		else if (newPC == null)
		{
			Globals.errorPrint("Unable to load " + file.toString());
			Globals.setCurrentPC(oldPC);
			return;
		}

		aType = target;

		Follower newMaster = new Follower(oldPC.getFileName(), oldPC.getName(), aType);
		newPC.setMaster(newMaster);
		Follower newFollower = new Follower(file.getAbsolutePath(), newPC.getName(), aType);
		oldPC.addFollower(newFollower);
		oldPC.setDirty(true);
		newPC.setDirty(true);
		Globals.getRootFrame().savePC(oldPC, false);
		Globals.getRootFrame().savePC(newPC, false);
		Globals.getRootFrame().getBaseTabbedPane().setSelectedIndex(oldIndex);
		Globals.getRootFrame().revertToSavedItem_actionPerformed(null);

		// must force an Update before switching tabs
		setNeedsUpdate(true);
		InfoResources.this.selectedTable.updateUI();
	}

	private void addButton(ActionEvent evt)
	{
		//Globals.errorPrint("-------------addButton------------- ");

		if ("".equals(aPC.getFileName()))
		{
			JOptionPane.showMessageDialog(null, "You must save the current character first", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		TreePath avaCPath = availableTable.getTree().getSelectionPath();
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		String target = "";

		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null, "First select destination", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
		{
			target = selCPath.getPathComponent(1).toString();
		}

		Object endComp = avaCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		// Different operations depending on the type of the object
		if ((fNode.getItem() instanceof Race))
		{
			// we are adding a familiar, animal companion, etc
			Race aRace = (Race) fNode.getItem();
			if (aRace == null)
			{
				return;
			}

			String nName = "";
			String aType = "";

			Globals.errorPrint("addButton:race: " + aRace.getName() + " -> " + target);
			// first ask for the name of the new object
			Object nValue = JOptionPane.showInputDialog(null, "Please enter a name for new " + target + ":", Constants.s_APPNAME, JOptionPane.QUESTION_MESSAGE);
			if (nValue != null)
			{
				nName = ((String) nValue).trim();
			}
			else
			{
				return;
			}

			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save new " + target + " named: " + nName);
			fc.setSelectedFile(new File(SettingsHandler.getPcgPath(), nName + Constants.s_PCGEN_CHARACTER_EXTENSION));
			fc.setCurrentDirectory(SettingsHandler.getPcgPath());

			if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			{
				return;
			}

			File file = fc.getSelectedFile();
			if (!file.getName().endsWith(Constants.s_PCGEN_CHARACTER_EXTENSION))
			{
				file = new File(file.getParent(), file.getName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
			}
			if (file.exists())
			{
				int iConfirm = JOptionPane.showConfirmDialog(null, "The file " + file.getName() + " already exists. Are you sure you want to overwrite it?", "Confirm OverWrite", JOptionPane.YES_NO_OPTION);
				if (iConfirm != JOptionPane.YES_OPTION)
					return;
			}

			PlayerCharacter newPC = new PlayerCharacter();
			newPC.setName(nName);
			newPC.setFileName(file.getAbsolutePath());
			for (Iterator i = newPC.getStatList().getStats().iterator(); i.hasNext();)
			{
				final PCStat aStat = (PCStat) i.next();
				aStat.setBaseScore(10);
			}
			newPC.setAlignment(aPC.getAlignment(), true, true);
			newPC.setRace(aRace);
			if (newPC.getRace().hitDice() != 0)
			{
				newPC.getRace().rollHP();
			}
			newPC.setDirty(true);

			aType = target;

			Follower newMaster = new Follower(aPC.getFileName(), aPC.getName(), aType);
			newPC.setMaster(newMaster);
			Follower newFollower = new Follower(file.getAbsolutePath(), nName, aType);
			aPC.addFollower(newFollower);
			aPC.setDirty(true);

			JOptionPane.showMessageDialog(null, "Saving " + nName + " and switching tabs", Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
			// save the new Follower to a file
			(new PCGIOHandler()).write(newPC, file.getAbsolutePath());
			// must force an Update before switching tabs
			setNeedsUpdate(true);

			// now load the new Follower from the file
			// and switch tabs
			Globals.getRootFrame().loadPCFromFile(file);
			Globals.getRootFrame().forceUpdate_InfoSummary();

		}
		else if ((fNode.getItem() instanceof Equipment))
		{
			Equipment eqI = (Equipment) fNode.getItem();
			if (eqI == null)
				return;

			Globals.errorPrint("addButton:item: " + eqI.getName() + " -> " + target);

			// now do all the GUI update stuff
			// Remember which rows are expanded
			boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
			for (int i = 0; i < s.length; i++)
			{
				s[i] = selectedTable.getTree().isExpanded(i);
			}

			// reset EquipSet model to get the new equipment
			// added into the selectedTable tree
			aPC.setDirty(true);
			createSelectedModel();
			selectedTable.updateUI();

			//re-expand the rows
			for (int i = 0; i < s.length; i++)
			{
				if (s[i])
					selectedTable.getTree().expandRow(i);
			}

		}

		return;

	}

	/**
 	* load Follower from .pcg file and create new tab
 	**/
	private void loadButton(ActionEvent evt)
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();
		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null,
			"Select the object to load",
			Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}
		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;
		if (fNode.getItem() instanceof Follower)
		{
			Follower aF = (Follower) fNode.getItem();
			if (aF == null)
				return;
			// now search the list of PC's to make sure we are
			// not already loaded
			for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
			{
				PlayerCharacter nPC = (PlayerCharacter) p.next();
				if (aF.getFileName().equals(nPC.getFileName()))
				{
					JOptionPane.showMessageDialog(null,
						aF.getName() + " is already loaded",
						Constants.s_APPNAME,
						JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			// Get the .pcg filename to load
			File file = new File(aF.getFileName());
			// Make sure file exists
			if (!file.exists())
			{
				JOptionPane.showMessageDialog(null,
					aF.getFileName() + " has moved/changed. Please select the new .pcg filename ",
					Constants.s_APPNAME,
					JOptionPane.INFORMATION_MESSAGE);
				// not there, so see if the user can find it
				Globals.errorPrint("b File: " + file.getAbsolutePath());
				file = findPCGFile(file);
				// still not found, just bail
				if (file == null)
					return;
				Globals.errorPrint("a File: " + file.getAbsolutePath());
			}
			// Followers .pcg filename/location may
			// have changed so make sure to update
			aF.setFileName(file.getAbsolutePath());
			JOptionPane.showMessageDialog(null,
				"Loading " + aF.getName() + " from " + aF.getFileName() + " and switching tabs",
				Constants.s_APPNAME,
				JOptionPane.INFORMATION_MESSAGE);
			// now load the Follower from
			// the file and switch tabs
			(Globals.getRootFrame()).loadPCFromFile(file);
			// must force an Update after switching tabs
			setNeedsUpdate(true);
			Globals.getRootFrame().forceUpdate_InfoSummary();
			return;
		}
		else if (fNode.getItem() instanceof Equipment)
		{
			//aPC.getEquipment((Equipment)fNode.getItem());
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
	 * removes an item from the selected table
	 **/
	private void delButton(ActionEvent evt)
	{
		TreePath selCPath = selectedTable.getTree().getSelectionPath();

		if (selCPath == null)
		{
			JOptionPane.showMessageDialog(null, "Select the object to remove", Constants.s_APPNAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		Object endComp = selCPath.getLastPathComponent();
		PObjectNode fNode = (PObjectNode) endComp;

		int iConfirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
		if (iConfirm != JOptionPane.YES_OPTION)
		{
			return;
		}

		if (fNode.getItem() instanceof Follower)
		{
			aPC.delFollower((Follower) fNode.getItem());
		}
		else if (fNode.getItem() instanceof Equipment)
		{
			aPC.removeEquipment((Equipment) fNode.getItem());
		}
		else
		{
			return;
		}


		// now do all the GUI update stuff
		// Remember which rows are expanded
		boolean s[] = new boolean[selectedTable.getTree().getRowCount()];
		for (int i = 0; i < s.length; i++)
		{
			s[i] = selectedTable.getTree().isExpanded(i);
		}

		aPC.setDirty(true);
		createSelectedModel();
		selectedTable.updateUI();

		//re-expand the rows
		for (int i = 0; i < s.length; i++)
		{
			if (s[i])
			{
				selectedTable.getTree().expandRow(i);
			}
		}

		return;
	}

	/**
	 * Update Follower if master has been loaded
	 **/
	private void updateButton(ActionEvent evt)
	{
		if (aPC.getMaster() != null)
		{
			aPC.setMaster(aPC.getMaster());
			Globals.getRootFrame().forceUpdate_InfoSkills();
			Globals.getRootFrame().forceUpdate_InfoSummary();
			Globals.getRootFrame().forceUpdate_InfoClasses();
			Globals.getRootFrame().forceUpdate_InfoSpells();
			Globals.getRootFrame().forceUpdate_InfoGear();
			JOptionPane.showMessageDialog(null, "Done updating " + aPC.getName(), Constants.s_APPNAME, JOptionPane.INFORMATION_MESSAGE);
		}
		aPC.setDirty(true);
		setFollowerInfo(aPC);
	}

	/**
	 * Prompt the user to find the Followers .pcg file
	 **/
	private File findPCGFile(File file)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Find file");
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());
		if (fc.showOpenDialog(InfoResources.this) != JFileChooser.APPROVE_OPTION)
		{
			return null;
		}
		file = fc.getSelectedFile();
		if (file.exists() && file.canWrite())
		{
			return file;
		}
		return null;
	}

	/**
	 * Creates the FollowerModels
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
			availableModel = new FollowerModel(viewSortMode, true);
		}
		else
		{
			availableModel.resetModel(viewSortMode, true);
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
			selectedModel = new FollowerModel(viewSortMode, false);
		}
		else
		{
			selectedModel.resetModel(viewSortMode, false);
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
	 *  The leafs contain an Object that we want to know about (Equipment)
	 **/
	private final class FollowerModel extends AbstractTreeTableModel implements TreeTableModel
	{
		// there are two roots. One for available equipment
		// and one for selected equipment profiles
		private PObjectNode avaRoot;
		private PObjectNode selRoot;

		// list of columns names
		private String[] avaNameList = {""};
		private String[] selNameList = {""};

		// Types of the columns.
		private int modelType = MODEL_AVAILABLE;

		// column positions for Famliar tables
		// if you change these, you also have to change
		// the case statement in the FollowerModel declaration
		private final int COL_NAME = 0;
		private final int COL_SIZE = 1;
		private final int COL_MOVE = 2;
		private final int COL_VISION = 3;
		private final int COL_PRE = 4;
		private final int COL_TYPE = 5;
		private final int COL_SOURCE = 6;
		private final int SEL_COL_TYPE = 11;
		private final int SEL_COL_FILE = 12;
		// view modes for Famliar tables
		private final int VIEW_TYPE = 0;
		private final int VIEW_NAME = 1;

		// view modes for Item tables
		private final int ITEM_VIEW_TYPE = 0;
		private final int ITEM_VIEW_NAME = 1;

		// view modes for Vehicle tables
		private final int VEHICLE_VIEW_TYPE = 0;
		private final int VEHICLE_VIEW_NAME = 1;

		/**
		 * Creates a FollowerModel
		 **/
		private FollowerModel(int mode, boolean available)
		{
			super(null);

			//
			// if you change/add/remove entries to nameList
			// you also need to change the static COL_XXX defines
			// at the begining of this file
			//
			avaNameList = new String[]{"Type/Name", "Size", "Speed", "Vision", "Alignment", "Type/Race", "Source"};

			selNameList = new String[]{"Type/Name", "Type/Race", "File Name"};

			if (!available)
			{
				modelType = MODEL_SELECTED;
			}
			resetModel(mode, available);
		}

		/**
		 * This assumes the FollowerModel exists but
		 * needs branches and nodes to be repopulated
		 **/
		private void resetModel(int mode, boolean available)
		{
			// array of all the races
			ArrayList raceList = new ArrayList();
			// This is the array of all the types
			ArrayList typeList = new ArrayList();
			// array of current companions
			ArrayList selectedList = new ArrayList();

			// build the list of races and types
			for (Iterator iRace = Globals.getRaceMap().values().iterator(); iRace.hasNext();)
			{
				final Race aRace = (Race) iRace.next();
				if (!raceList.contains(aRace))
				{
					raceList.add(aRace);
				}
				if (!typeList.contains(aRace.getType()))
				{
					typeList.add(aRace.getType());
				}
			}
			Collections.sort(raceList);
			Collections.sort(typeList);

			// build the list of current companions types
			selectedList.add("Animal Companions");
			selectedList.add("Followers");
			if (aPC.hasSpecialAbility("Summon Familiar") && !selectedList.contains("Familiar"))
			{
				selectedList.add("Familiar");
			}
			for (Iterator iClass = aPC.getClassList().iterator(); iClass.hasNext();)
			{
				final PCClass aClass = (PCClass) iClass.next();
				if (Globals.getCompanionModList().isEmpty())
				{
					Globals.debugPrint("gCML is empty");
					continue;
				}
				for (Iterator iComp = Globals.getCompanionModList().iterator(); iComp.hasNext();)
				{
					CompanionMod aComp = (CompanionMod) iComp.next();
					if ((aComp.getClassMap().containsKey(aClass.getName())) && (!selectedList.contains(aComp.getType())))
					{
						selectedList.add(aComp.getType());
					}
				}
			}

			//
			// build availableTable
			//
			if (available)
			{
				// this is the root node
				avaRoot = new PObjectNode();
				setRoot(avaRoot);

				switch (mode)
				{
					// races by name
					case VIEW_NAME:
						PObjectNode rn[] = new PObjectNode[raceList.size()];
						// iterate through the names
						// and fill out the tree
						for (int iName = 0; iName < raceList.size(); iName++)
						{
							final Race aRace = (Race) raceList.get(iName);
							if (aRace != null)
							{
								rn[iName] = new PObjectNode();
								rn[iName].setItem(aRace);
								rn[iName].setParent(avaRoot);
							}
						}
						// now add to the root node
						avaRoot.setChildren(rn, true);
						break; // end VIEW_NAME

					case VIEW_TYPE:
						//build the TYPE root nodes
						PObjectNode rt[] = new PObjectNode[typeList.size()];
						// iterate through the types
						// and fill out the tree
						for (int iType = 0; iType < typeList.size(); iType++)
						{
							final String aType = (String) typeList.get(iType);
							rt[iType] = new PObjectNode();
							rt[iType].setItem(aType);
							for (Iterator fI = Globals.getRaceMap().values().iterator(); fI.hasNext();)
							{
								final Race aRace = (Race) fI.next();
								if (aRace == null)
								{
									continue;
								}
								if (!aRace.getType().equals(aType))
								{
									continue;
								}
								PObjectNode aFN = new PObjectNode();
								aFN.setItem(aRace);
								aFN.setParent(rt[iType]);
								rt[iType].addChild(aFN);
							}
							// if it's not empty, add it
							if (!rt[iType].isLeaf())
							{
								rt[iType].setParent(avaRoot);
							}
						}
						// now add to the root node
						avaRoot.setChildren(rt, true);

						break; // end VIEW_TYPE
					default:
						Globals.errorPrint("In InfoResources.FollowerModel.resetModel the mode " + mode + " is not handled.");
						break;

				} // end of switch(mode)
			} // end of availableTable builder

			else

			{ // selectedTable builder

				// this is the root node
				selRoot = new PObjectNode();
				setRoot(selRoot);

				PObjectNode sl[] = new PObjectNode[selectedList.size()];
				for (int iSel = 0; iSel < selectedList.size(); iSel++)
				{
					String sString = (String) selectedList.get(iSel);
					sl[iSel] = new PObjectNode();
					sl[iSel].setItem(sString);
					sl[iSel].setParent(selRoot);
					for (Iterator fList = aPC.getFollowerList().iterator(); fList.hasNext();)
					{
						Follower aF = (Follower) fList.next();
						if (!sString.startsWith(aF.getType()))
						{
							continue;
						}
						PObjectNode aFN = new PObjectNode();
						aFN.setItem(aF);
						aFN.setParent(sl[iSel]);
						sl[iSel].addChild(aFN);
					}
				}
				// new add to the root node
				selRoot.setChildren(sl, false);

			} // end if else

			PObjectNode rootAsPObjectNode = (PObjectNode) root;
			if (rootAsPObjectNode.getChildCount() > 0)
			{
				fireTreeStructureChanged(root, rootAsPObjectNode.getChildren(), null, null);
			}

		}

		/**
		 * There must be a root node, but we keep it hidden
		 **/
		private final void setRoot(PObjectNode aNode)
		{
			root = aNode;
		}

		/**
		 * return the root node
		 **/
		public Object getRoot()
		{
			return (PObjectNode) root;
		}

		/* The JTreeTableNode interface. */

		/**
		 * Returns int number of columns
		 **/
		public int getColumnCount()
		{
			return modelType == 0 ? avaNameList.length : selNameList.length;
		}

		/**
		 * Returns String name of a column
		 **/
		public String getColumnName(int column)
		{
			return modelType == MODEL_AVAILABLE ? avaNameList[column] : selNameList[column];
		}

		/**
		 * Returns Class for the column
		 **/
		public Class getColumnClass(int column)
		{
			return column == COL_NAME ? TreeTableModel.class : String.class;
		}

		/**
		 * Returns boolean if can edit a cell.
		 **/
		public boolean isCellEditable(Object node, int column)
		{
			return (column == COL_NAME);
		}

		/**
		 * changes the column order sequence and/or number of
		 * columns based on modelType (0=available, 1=selected)
		 **/
		private int adjustColumnConst(int column)
		{
			// available table
			if (modelType == MODEL_SELECTED)
			{
				switch (column)
				{
					case COL_SIZE:
						return SEL_COL_TYPE;
					case COL_MOVE:
						return SEL_COL_FILE;
					default:
						return column;
				}
			}
			return column;
		}

		/**
		 * return the value of a column
		 **/
		public Object getValueAt(Object node, int column)
		{
			final PObjectNode fn = (PObjectNode) node;
			Race race = null;
			Follower fObj = null;
			String sRet = "";

			if (fn == null)
			{
				Globals.errorPrint("No active node when doing getValueAt in InfoRace");
				return null;
			}

			if (fn.getItem() instanceof Race)
			{
				race = (Race) fn.getItem();
			}
			else if (fn.getItem() instanceof Follower)
			{
				fObj = (Follower) fn.getItem();
			}

			column = adjustColumnConst(column);

			switch (column)
			{
				case COL_NAME:
					if (race != null)
					{
						return race.getName();
					}
					else
					{
						return fn.toString();
					}

				case COL_SIZE:
					if (race != null)
					{
						sRet = race.getSize();
					}
					break;

				case COL_MOVE:
					if (race == null)
					{
						return null;
					}
					if (race.getMovements() != null)
					{
						final StringBuffer movelabel = new StringBuffer(race.getMovementType(0)).append(" ").append(race.getMovement(0)).append(Globals.getAbbrMovementDisplay()).append(" ");
						for (int i = 1; i < race.getMovements().length; i++)
						{
							movelabel.append(race.getMovementType(i)).append(" ").append(race.getMovement(i)).append(Globals.getAbbrMovementDisplay()).append(" ");
						}
						sRet = movelabel.toString();
					}
					break;

				case COL_VISION:
					if (race != null)
					{
						sRet = race.getDisplayVision();
					}
					break;

				case COL_PRE:
					if (race != null)
					{
						sRet = race.preReqHTMLStrings();
					}
					break;

				case COL_TYPE:
					if (race != null)
					{
						if ("None".equals(race.getDisplayName()))
						{
							sRet = race.getType();
						}
						else
						{
							sRet = race.getDisplayName();
						}
					}
					break;

				case COL_SOURCE:
					if (race != null)
					{
						sRet = race.getSource();
					}
					break;

				case SEL_COL_TYPE:
					if (fObj != null)
					{
						sRet = fObj.getType();
					}
					break;

				case SEL_COL_FILE:
					if (fObj != null)
					{
						sRet = fObj.getFileName();
					}
					break;

				default:
					Globals.errorPrint("In InfoResources.FollowerModel.getValueAt the column " + column + " is not handled.");
					break;

			}
			return sRet;
		}

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
		//availableTable.updateUI();
		//selectedTable.updateUI();
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
