/*
 * PCGen_Frame1.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
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
 */

package pcgen.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui.filter.Filterable;
import pcgen.io.PCGIOHandler;
import pcgen.util.FOPResourceChecker;


/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example
 * {@link pcgen.gui.MainSource} also loads any
 * default campaigns.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
public class PCGen_Frame1 extends JFrame
{
	private static String missingLibMsg;
	private pcGenGUI mainClass;
	private static CharacterInfo character = null;

	/**
	 * author: Thomas Behr 03-01-02
	 */
	static
	{
		ResourceBundle d_properties;
		try
		{
			d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			missingLibMsg = d_properties.getString("MissingLibMessage");
		}
		catch (MissingResourceException mrex)
		{
			missingLibMsg = "This feature requires the download of the above-mentioned file(s) " +
				"(http://sourceforge.net/projects/pcgen/).\n" +
				"Please download and place in the \"lib\" sub-directory of your PCGen installation.\n" +
				"You must then restart PCGen for full functionality.";
			mrex.printStackTrace();
		}
		finally
		{
			d_properties = null;
		}

		missingLibMsg = missingLibMsg.replace('|', '\n');
	}

	private static JLabel statusBar = new JLabel();

	public static JLabel getStatusBar()
	{
		return statusBar;
	}

	/**
	 * Main tabbed panel of the application.
	 * The first two tabs contain the {@link #mainSource}
	 * ("Campaign") and {@link #tabDMTools} ("DM Tools")
	 * panels.
	 * Additional {@link CharacterInfo} panel tabs are added
	 * for each created character.
	 */
	private static JTabbedPane baseTabbedPanel = new JTabbedPane();

	public static JTabbedPane getBaseTabbedPanel()
	{
		return baseTabbedPanel;
	}

	public static CharacterInfo getCharacterTab()
	{
		return character;
	}

	private final PcgFilter filter = new PcgFilter();
	private final PcpFilter partyFilter = new PcpFilter();
	private String partyFileName = "";  //used to keep track of last .pcp file used

	/**
	 * Contains the source screen.
	 *
	 * @see MainSource
	 */
	private MainSource mainSource = new MainSource();

	/**
	 * Menubar and toolbar actions.
	 */
	FrameActionListener frameActionListener = new FrameActionListener(this);

	/**
	 * Menubar for the main application.
	 */
	MenuItems menuBar = new MenuItems(); // NOT private

	/**
	 * ToolBar for the main application.
	 */
	private PToolBar toolBar = PToolBar.createToolBar(this);

	/**
	 * Collection of loaded {@link CharacterInfo} panels;
	 * each is a view of a single character.
	 * <p>
	 * This essentially contains the same things as the additional
	 * tabs in <code>baseTabbedPanel</code>. i.e. <br />
	 * <code>baseTabbedPanel.getComponentAt(x) == characterList.elementAt(x - 2)</code>
	 */
//	private static ArrayList characterList = new ArrayList();

	// GUI stuff
	private JPanel panelSouth = new JPanel();
	private JPanel panelSouthEast = new JPanel();
	private JPanel panelSouthCenter = new JPanel();

	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();

	private FlowLayout flowLayout1 = new FlowLayout();
	private FlowLayout flowLayout2 = new FlowLayout();

	/**
	 * The "DM Tools" tab on the main application screen.
	 * Contains <code>toolsXP, toolsSetup, toolsCombat,
	 * toolsQuick and toolsNotes</code>.
	 */
	private JTabbedPane tabDMTools = new JTabbedPane();

	// DM Tools content panels
	private ToolsXP toolsXP = new ToolsXP();
	//private JPanel toolsSetup = new JPanel();
	private ToolsCombat toolsCombat = new ToolsCombat();
	private ToolsQStats toolsQuick = new ToolsQStats();
	private ToolsNotes toolsNotes = new ToolsNotes();

	/**
	 * the NPCGen on the DMTools tab
	 */
	//private NpcTab npcGen = new NpcTab();

	// Characters / core stuff
	private int count = 0;


	/**
	 * Screen initialization. Override close.
	 * <p>
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 * and creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 */
	public PCGen_Frame1()
	{
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		try
		{
			jbInit();
		}
		catch (Exception e) //This is what jbInit throws...
		{
			e.printStackTrace();
		}
	}

	public MainSource getMainSource()
	{
		return mainSource;
	}

	public void showToolBar()
	{
		toolBar.setVisible(Globals.isToolBarShown());
	}

	public void setGameModeTitle()
	{
		setTitle("PCGen - " + Globals.getGameMode() + " Game Mode");
	}

	/**
	 * Real screen initialization is done here. Sets up all
	 * the window properties (icon, title, size).
	 * <p>
	 * Creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 *
	 * @exception  Exception  Any Exception
	 */
	private void jbInit() throws Exception
	{
		Utility.maybeSetIcon(this, "PcgenIcon.gif");

		getContentPane().setLayout(borderLayout1);
		setSize(new Dimension(640, 400));
		setGameModeTitle();

		setJMenuBar(menuBar);

		getContentPane().add(toolBar, BorderLayout.NORTH);
		showToolBar();

		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBar.setDoubleBuffered(true);
		statusBar.setMaximumSize(new Dimension(32767, 17));
		statusBar.setMinimumSize(new Dimension(620, 17));
		statusBar.setOpaque(true);
		statusBar.setPreferredSize(new Dimension(620, 17));
		statusBar.setHorizontalTextPosition(SwingConstants.LEFT);
		statusBar.setText(" ");
		//Default tabPlacement
		if (Globals.getTabPlacement() == 0)
			baseTabbedPanel.setTabPlacement(JTabbedPane.TOP);
		else if (Globals.getTabPlacement() == 1)
			baseTabbedPanel.setTabPlacement(JTabbedPane.LEFT);
		else if (Globals.getTabPlacement() == 2)
			baseTabbedPanel.setTabPlacement(JTabbedPane.BOTTOM);
		else if (Globals.getTabPlacement() == 3)
			baseTabbedPanel.setTabPlacement(JTabbedPane.RIGHT);
		baseTabbedPanel.setDoubleBuffered(true);
		baseTabbedPanel.setMinimumSize(new Dimension(620, 350));
		baseTabbedPanel.setPreferredSize(new Dimension(620, 350));
		panelSouth.setLayout(borderLayout2);
		panelSouthEast.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		panelSouthCenter.setLayout(flowLayout2);
		flowLayout2.setAlignment(FlowLayout.LEFT);
		//toolsSetup.setLayout(borderLayout3);
		this.getContentPane().add(baseTabbedPanel, BorderLayout.CENTER);
		baseTabbedPanel.add(mainSource, "Source Materials");


		baseTabbedPanel.add(tabDMTools, "DM Tools");
		//tabDMTools.add(toolsSetup, "Setup");
		tabDMTools.add(toolsCombat, "Combat");
		tabDMTools.add(toolsQuick, "Quick Stats");
		tabDMTools.add(toolsXP, "XP Tracker");
		tabDMTools.add(toolsNotes, "Notes");
		//tabDMTools.add(npcGen, "NPCGen");
		tabDMTools.addChangeListener(
			new javax.swing.event.ChangeListener()
			{
				public void stateChanged(javax.swing.event.ChangeEvent e)
				{
					tabDMTools_changePanel(e);
				}
			});

		this.getContentPane().add(panelSouth, BorderLayout.SOUTH);
		panelSouth.add(statusBar, BorderLayout.SOUTH);
		baseTabbedPanel.addChangeListener(
			new javax.swing.event.ChangeListener()
			{
				public void stateChanged(javax.swing.event.ChangeEvent c)
				{
					baseTabbedPanel_changePanel(c);
				}
			});

		mainSource.addComponentListener(toolBar.getComponentListener());
		tabDMTools.addComponentListener(toolBar.getComponentListener());

		if (Globals.displayListsHappy())
		{
			enableNew(true);
		}
	}

	public void setMainClass(pcGenGUI owner)
	{
		mainClass = owner;
	}

	/**
	 * Enable/disable the new button
	 */
	protected void enableNew(boolean itemState)
	{
		menuBar.newItem.setEnabled(itemState);
		toolBar.newButton.setEnabled(itemState);
	}

	/**
	 * Overridden so we can handle exit on System Close
	 * by calling <code>handleQuit</code>.
	 */
	protected void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			handleQuit();
		}
	}

	/**
	 * Closes the program by calling <code>handleQuit</code>
	 */
	void exitItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.exitItem.requestFocus();
		handleQuit();
	}

	/**
	 * Does the real work in closing the program.
	 * Closes each character tab, giving user a chance to save.
	 * Saves options to file, then cleans up and exits.
	 */
	void handleQuit()
	{
		if (Globals.getLeftUpperCorner() == null)
			Globals.setLeftUpperCorner(new Point(0, 0));
		Globals.getLeftUpperCorner().setLocation(getLocationOnScreen().getX(), getLocationOnScreen().getY());

		int tabCount = baseTabbedPanel.getTabCount();
		while (tabCount > 2)
		{
			if (closePCTab(tabCount - 1, true))
			{
				tabCount = baseTabbedPanel.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}

		Globals.storeFilterSettings(mainSource);
		Globals.writeCustomItems();
		Globals.writeOptionsProperties();
		this.dispose();
		System.exit(0);
	}

	/**
	 * Creates a new {@link PlayerCharacter} model, and a corresponding
	 * {@link CharacterInfo} panel. The <code>PlayerCharacter</code> is
	 * added to the <code>Globals.getPcList()</code>. The <code>CharacterInfo</code>
	 * is added to both the <code>characterList</code>, and adds it to the main
	 * program frame, <code>baseTabbedPanel</code> as a new tab.
	 */
	void newItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		// How does this work with the toolbar button?? --bko XXX
		menuBar.newItem.requestFocus();
		final PlayerCharacter aPC = new PlayerCharacter();
		Globals.getPcList().add(aPC);
		count++;
		String tabName = "New" + count;
		aPC.setName(tabName);
		aPC.setDirty(true);
		if (character == null)
			character = new CharacterInfo();
		else
			resetCharacterTabs();
		// The API docs for JTabbedPane seem to indicate the name must
		// come first, then the component. Also, the tutorial seems
		// to prefer addTab (rather than add).
		baseTabbedPanel.add(character, tabName);
		baseTabbedPanel.setSelectedIndex(2);
		baseTabbedPanel.setSelectedIndex(baseTabbedPanel.getTabCount() - 1);
		baseTabbedPanel_changePanel(null);
	}

	/**
	 * Load a party metafile, including campaign info and characters.
	 * Campaigns are loaded as from the Campaign tab.
	 * Characters are loaded into a new <code>PlayerCharacter</code> model, and
	 * a corresponding <code>CharacterInfo</code> panel is created.
	 */
	void partyOpenItem_actionPerformed(ActionEvent event)
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Opening party...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.partyOpenItem.requestFocus();

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(Globals.getPcgPath());
		fc.setFileFilter(partyFilter);
		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Globals.setPcgPath(file.getParentFile());
			loadPartyFromFile(file);
		}
		statusBar.setText(oldStatus);
	}

	void loadPartyFromFile(File file)
	{
		if (mainClass != null && mainClass.loadPartyFromFile(file, this))
		{
			//if everything loaded successfully, then this file becomes the "current" party file
			partyFileName = file.getName();
			// Need a different displayAs --bko XXX
			menuBar.openRecentPartyMenu.add(partyFileName, file);
		}
		else
			System.err.println("Error in loadPartyFromFile");
	}

	/**
	 * Saves a party metafile, including campaign info and characters.
	 */
	void partySaveItem_actionPerformed(ActionEvent event)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.partySaveItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.  --bko

		//save character files here
		saveAllPCs();

		//save party file here
		partySaveItem(false);
	}

	void partySaveAsItem_actionPerformed(ActionEvent event)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.partySaveAsItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.  --bko

		//save character files here
		saveAllPCs();

		//save party file here
		partySaveItem(true);
	}

	void partySaveItem(boolean saveas)
	{
		boolean newParty = false;
		if (partyFileName.length() == 0)
		{
			partyFileName = "Party" + Constants.s_PCGEN_PARTY_EXTENSION;
			newParty = true;
		}

		File prevFile = new File(Globals.getPcgPath(), partyFileName), file = prevFile;

		if (saveas || newParty)
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(partyFilter);
			fc.setSelectedFile(prevFile);
			FilenameChangeListener listener = new FilenameChangeListener(partyFileName, fc);
			fc.addPropertyChangeListener(listener);

			int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!file.getName().endsWith(Constants.s_PCGEN_PARTY_EXTENSION))
				{
					file = new File(file.getParent(), file.getName() + Constants.s_PCGEN_PARTY_EXTENSION);
				}
				if (file.isDirectory())
				{
					JOptionPane.showMessageDialog(null,
						"You cannot overwrite a directory with a party.",
						"PCGen",
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (file.exists() && (newParty || prevFile == null || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose = JOptionPane.showConfirmDialog(this,
						"The file " + file.getName() +
						" already exists, are you sure you want to overwrite it?",
						"Confirm overwriting " + file.getName(),
						JOptionPane.YES_NO_OPTION);
					if (reallyClose != JOptionPane.YES_OPTION)
					{
						return;
					}
				}
			}

			else
				return;
		}

		try
		{
			partyFileName = file.getName();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			// Save party file data here
			// Save version info here (we no longer save campaign/source info in the party file)
			ResourceBundle d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			bw.write("VERSION:");
			bw.write(d_properties.getString("VersionNumber"));
			bw.newLine();
			Iterator anIter = Globals.getPcList().iterator();
			//save PC filenames
			while (anIter.hasNext())
			{
				final PlayerCharacter aPC = (PlayerCharacter)anIter.next();
				bw.write(aPC.getFileName() + ":");
			}
			bw.close();
			Globals.setPcgPath(file.getParentFile()); //still set this, we want .pcp and .pcg files in the same place
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null,
				"Could not save " + partyFileName,
				"PCGen",
				JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	/**
	 * Close a party metafile, including campaign info and characters.
	 */
	void partyCloseItem_actionPerformed(ActionEvent event)
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Closing party...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.partyCloseItem.requestFocus();

		statusBar.setText(oldStatus);
	}

	/**
	 * Load a character into a new <code>PlayerCharacter</code> model, and
	 * create a corresponding <code>CharacterInfo</code> panel.
	 *
	 * @see #newItem_actionPerformed
	 */
	void openItem_actionPerformed(ActionEvent event)
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Opening character.  Please wait...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.openItem.requestFocus();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(Globals.getPcgPath());
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Globals.setPcgPath(file.getParentFile());
			fc.setMultiSelectionEnabled(false);
			loadPCFromFile(file);
			Globals.sortCampaigns();
		}
		statusBar.setText(oldStatus);
	}

	void loadPCFromFile(File file)
	{
		if (mainClass != null && mainClass.loadPCFromFile(file))
		{
			final PlayerCharacter newPC = Globals.getCurrentPC();
			if (character == null)
				character = new CharacterInfo();
			else
				resetCharacterTabs();
			String tabName = newPC.getName();

			baseTabbedPanel.add(character, tabName);
			newPC.setFileName(file.getName());
			menuBar.openRecentPCMenu.remove(tabName, file);
			baseTabbedPanel.setSelectedIndex(2);
			baseTabbedPanel.setSelectedIndex(baseTabbedPanel.getTabCount() - 1);
			baseTabbedPanel_changePanel(null);
		}
		else
			System.err.println("Error in loadPCFromFile");
	}

	/**
	 * Shows different menus for different main tabs.
	 * When one of the first two panes (Campaign or GM) is shown,
	 * there are different menu items enabled (such as Save),
	 * than for character tabs.
	 */
	void baseTabbedPanel_changePanel(ChangeEvent c)
	{
		// change focus to force focus listeners to fire before changing
		baseTabbedPanel.requestFocus();  //reinstate the requestFocus to prevent open edits from applying to the wrong PC

		int currentPanel = baseTabbedPanel.getSelectedIndex();
		if (currentPanel <= 1)
		{
			PToolBar.displayHelpPanel(false);
			menuBar.closeItem.setEnabled(false);
			menuBar.saveItem.setEnabled(false);
			toolBar.saveButton.setEnabled(false);
			menuBar.saveAsItem.setEnabled(false);
			menuBar.revertToSavedItem.setEnabled(false);
			if (baseTabbedPanel.getTabCount() >= 3)
			{
				menuBar.closeAllItem.setEnabled(true);
				menuBar.saveAllItem.setEnabled(true);
			}
			else
			{
				menuBar.closeAllItem.setEnabled(false);
				menuBar.saveAllItem.setEnabled(false);
			}
			menuBar.printPreviewItem.setEnabled(false);
			toolBar.printPreviewButton.setEnabled(false);
			if (baseTabbedPanel.getTabCount() < 3)
			{
				menuBar.partySaveItem.setEnabled(false);
				menuBar.partySaveAsItem.setEnabled(false);
				menuBar.printItem.setEnabled(false);
				toolBar.printButton.setEnabled(false);
				menuBar.exportMenu.setEnabled(false);
			}
			if (currentPanel == 1)
			{
				toolsQuick.refreshTable();
				toolsCombat.refreshTable();
				toolsXP.refreshTable();
			}
		}
		else
		{
			menuBar.closeItem.setEnabled(true);
			menuBar.saveItem.setEnabled(true);
			toolBar.saveButton.setEnabled(true);
			menuBar.saveAsItem.setEnabled(true);
			menuBar.revertToSavedItem.setEnabled(true);
			menuBar.printPreviewItem.setEnabled(true);
			toolBar.printPreviewButton.setEnabled(true);
			Globals.setCurrentPC((PlayerCharacter)Globals.getPcList().get(currentPanel - 2));
			if (Globals.getPcList().size() > 1)
			{
				resetCharacterTabs();
			}
			baseTabbedPanel.setComponentAt(currentPanel, character);

			character.featList_Changed();

			JTabbedPane aPane = (JTabbedPane)character.getComponent(0);
			int si = aPane.getSelectedIndex();
			if (si > -1)
			{
				aPane.getComponent(si).requestFocus();
				// force component to get componentShown message
				ComponentEvent ce = new ComponentEvent(aPane.getComponent(si), ComponentEvent.COMPONENT_SHOWN);
				aPane.getComponent(si).dispatchEvent(ce);
			}
			int x = aPane.getComponentCount();
			// Domains/Spells either 6&7 or last two components
			if (Globals.isStarWarsMode() || Globals.isSidewinderMode())
			{
				if (aPane.getComponent(7) == character.infoSpells() || aPane.getComponent(x - 1) == character.infoSpells())
					aPane.remove(character.infoSpells());
				if (aPane.getComponent(6) == character.infoDomains() || aPane.getComponent(x - 2) == character.infoDomains())
					aPane.remove(character.infoDomains());
			}
			else if (Globals.isDeadlandsMode() || Globals.isWheelMode() || Globals.isSSd20Mode())
			{
				if (aPane.getComponent(6) == character.infoDomains() || aPane.getComponent(x - 2) == character.infoDomains())
					aPane.remove(character.infoDomains());
				if (aPane.getComponent(7) != character.infoSpells())
					aPane.add(character.infoSpells(), "Spells", 7);
			}
			else if (Globals.isWeirdWarsMode())
			{
				if (aPane.getComponent(6) == character.infoDomains() || aPane.getComponent(x - 2) == character.infoDomains())
					aPane.remove(character.infoDomains());
				if (aPane.getComponent(7) != character.infoSpells())
					aPane.add(character.infoSpells(), "Spells", 7);
			}
			else
			{
				if (aPane.getComponent(5) != character.infoDomains())
					aPane.add(character.infoDomains(), "Domains", 5);
				if (aPane.getComponent(6) != character.infoSpells())
					aPane.add(character.infoSpells(), "Spells", 6);
			}

			if (baseTabbedPanel.getTabCount() > 3)
			{
				menuBar.closeAllItem.setEnabled(true);
				menuBar.saveAllItem.setEnabled(true);
			}
			else
			{
				menuBar.closeAllItem.setEnabled(false);
				menuBar.saveAllItem.setEnabled(false);
			}

			if (baseTabbedPanel.getTabCount() > 2)
			{
				menuBar.partySaveItem.setEnabled(true);
				menuBar.partySaveAsItem.setEnabled(true);
				menuBar.printItem.setEnabled(true);
				toolBar.printButton.setEnabled(true);
				menuBar.exportMenu.setEnabled(true);
			}
		}
	}

	void resetCharacterTabs()
	{
		if (character == null)
			return;
		for (int i = 2; i < baseTabbedPanel.getTabCount(); i++)
			baseTabbedPanel.setComponentAt(i, new JPanel());
	}

	/**
	 * Takes effect whenever somebody changes one of the DMTools tabs.
	 * Current purpose is to cause the QuickStats tab to refresh.
	 */
	void tabDMTools_changePanel(ChangeEvent e)
	{
		toolsQuick.refreshTable();
		toolsCombat.refreshTable();
		toolsXP.refreshTable();
		PToolBar.displayHelpPanel(false);
	}


	/**
	 * Closes the currently selected character tab.
	 */
	void closeItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.closeItem.requestFocus();
		final int currTab = baseTabbedPanel.getSelectedIndex();
		if (currTab > 1)
		{
			// Check if reverting instead
			final String command = e.getActionCommand();
			if (!closePCTab(currTab, command.equals("file.close")))
			{
				return;
			}

			final int tabCount = baseTabbedPanel.getTabCount();
			if (tabCount < 4)
			{
				menuBar.closeAllItem.setEnabled(false);
				menuBar.saveAllItem.setEnabled(false);
			}
			if (tabCount < 3)
			{
				count = 0;
				menuBar.partySaveItem.setEnabled(false);
				menuBar.partySaveAsItem.setEnabled(false);
				menuBar.printItem.setEnabled(false);
				toolBar.printButton.setEnabled(false);
				menuBar.exportMenu.setEnabled(false);
			}
		}
	}

	/**
	 * Close all open character tabs
	 */
	void closeAllItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.closeAllItem.requestFocus();
		closeAllPCs();
	}

	/**
	 * What PC's tab is on top?
	 */
	PlayerCharacter getCurrentPC()
	{
		int currTab = baseTabbedPanel.getSelectedIndex();
		return ((PlayerCharacter)Globals.getPcList().get(currTab - 2));
	}

	/**
	 * Reverts to the previous version of this PC's saved file.
	 */
	void revertToSavedItem_actionPerformed(ActionEvent e)
	{
		PlayerCharacter aPC = getCurrentPC();

		if (!aPC.isDirty())
			return; // do nothing if clean

		String oldStatus = statusBar.getText();
		statusBar.setText("Reverting character to saved...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.revertToSavedItem.requestFocus();

		String fileName = aPC.getFileName();
		if (fileName.length() == 0)
		{
			statusBar.setText(oldStatus);
			return;
		}

		closeItem_actionPerformed(e);

		File pcFile = new File(Globals.getPcgPath(), fileName);
		if (pcFile.exists())
			loadPCFromFile(pcFile);

		statusBar.setText(oldStatus);
	}

	public void closeAllPCs()
	{
		int tabCount = baseTabbedPanel.getTabCount();
		while (tabCount > 2)
		{
			if (closePCTab(tabCount - 1, true))
			{
				tabCount = baseTabbedPanel.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}

		menuBar.closeItem.setEnabled(false);
		menuBar.closeAllItem.setEnabled(false);
		menuBar.saveItem.setEnabled(false);
		toolBar.saveButton.setEnabled(false);
		menuBar.saveAsItem.setEnabled(false);
		menuBar.saveAllItem.setEnabled(false);
		menuBar.revertToSavedItem.setEnabled(false);
		menuBar.partySaveItem.setEnabled(false);
		menuBar.partySaveAsItem.setEnabled(false);
		menuBar.printItem.setEnabled(false);
		toolBar.printButton.setEnabled(false);
		menuBar.exportMenu.setEnabled(false);
		menuBar.printPreviewItem.setEnabled(false);
		toolBar.printPreviewButton.setEnabled(false);
		count = 0;
	}

	/**
	 * Saves the character corresponding to the
	 * currently selected tab. The current character is
	 * worked out by taking the <code>(tab position - 2)</code>,
	 * and taking the corresponding character from the
	 * <code>Globals.getPcList()</code>.
	 */
	void saveItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveItem.requestFocus();
		final PlayerCharacter aPC = getCurrentPC();
		savePC(aPC, false);
	}

	/**
	 * Saves the character corresponding to the
	 * currently selected tab. The current character is
	 * worked out by taking the <code>(tab position - 2)</code>,
	 * and taking the corresponding character from the
	 * <code>Globals.getPcList()</code>.
	 */
	void saveAsItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveAsItem.requestFocus();
		final PlayerCharacter aPC = getCurrentPC();
		savePC(aPC, true);
	}


	/**
	 * Property change listener for the event "selected file
	 * changed".  Ensures that the filename doesn't get changed
	 * when a directory is selected.
	 *
	 * @author Dmitry Jemerov <yole@spb.cityline.ru>
	 */

	class FilenameChangeListener implements PropertyChangeListener
	{
		private String lastSelName;
		private JFileChooser fileChooser;

		FilenameChangeListener(String aFileName, JFileChooser aFileChooser)
		{
			lastSelName = aFileName;
			fileChooser = aFileChooser;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();
			if (propName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
				onSelectedFileChange(evt);
			else if (propName.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY))
				onDirectoryChange(evt);
		}

		private void onDirectoryChange(PropertyChangeEvent evt)
		{
			fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), lastSelName));
		}

		private void onSelectedFileChange(PropertyChangeEvent evt)
		{
			File newSelFile = (File)evt.getNewValue();
			if (!newSelFile.isDirectory())
				lastSelName = newSelFile.getName();
		}
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's <code>save</code> method.
	 *
	 * @param aPC The PlayerCharacter to save
	 * @param saveas boolean if <code>true</code>, ask for file name
	 */
	void savePC(PlayerCharacter aPC, boolean saveas)
	{

		boolean newPC = false;
		if (aPC.getFileName().length() == 0)
		{
			aPC.setFileName(aPC.getName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
			newPC = true;
		}

		File prevFile = new File(Globals.getPcgPath(), aPC.getFileName()), file = null;

		if (saveas || newPC)
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(filter);
			fc.setSelectedFile(prevFile);
			FilenameChangeListener listener = new FilenameChangeListener(aPC.getFileName(), fc);

			fc.addPropertyChangeListener(listener);
			int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!file.getName().endsWith(Constants.s_PCGEN_CHARACTER_EXTENSION))
					file = new File(file.getParent(), file.getName() + Constants.s_PCGEN_CHARACTER_EXTENSION);

				if (file.isDirectory())
				{
					JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a character.", "PCGen", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (file.exists() && (newPC || prevFile == null || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose = JOptionPane.showConfirmDialog(this, "The file " + file.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + file.getName(), JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
						return;
				}
				aPC.setFileName(file.getName());
			}
			else // not saving
				return;
		}

		else // simple save
			file = prevFile;

		try
		{
			(new PCGIOHandler()).write(aPC, file.getAbsolutePath());

			Globals.setPcgPath(file.getParentFile());
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null,
				"Could not save " + aPC.getName(),
				"PCGen",
				JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	void saveAllPCs()
	{
		PlayerCharacter aPC = null;
		int pcsToSave = Globals.getPcList().size();

		while (pcsToSave > 0)
		{
			aPC = ((PlayerCharacter)Globals.getPcList().get(pcsToSave - 1));
			if (aPC.isDirty())
				savePC(aPC, false);
			pcsToSave--;
		}
	}

	/**
	 * Saves all open characters. Iterates through
	 * <code>Globals.getPcList()</code> (not the tabs) and runs
	 * <code>savePC</code> for each one.
	 */
	void saveAllItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveAllItem.requestFocus();
		saveAllPCs();
	}

	/**
	 * Close a tab by tab number, not pc number.
	 *
	 * @param  tab  Tab the character is on (not PC number)
	 * @param isClosing boolean <code>true</code> if closing, <code>false</code> if reverting
	 * @return      Not relevant - always <code>true</code>
	 */
	private boolean closePCTab(int tab, boolean isClosing)
	{
		final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(tab - 2);
		Globals.setCurrentPC(aPC);
		if (aPC.isDirty())
		{
			int reallyClose = 0;

			if (isClosing)
				reallyClose = JOptionPane.showConfirmDialog(this,
					"The character " + aPC.getName() + " has not been saved, are you sure you want to close it?",
					"Confirm closing " + aPC.getName() + " without saving it.",
					JOptionPane.YES_NO_OPTION);
			else // reverting
				reallyClose = JOptionPane.showConfirmDialog(this,
					"The character " + aPC.getName() + " has not been saved, are you sure you want to revert and lose your changes?",
					"Confirm reverting " + aPC.getName() + " without saving it.",
					JOptionPane.YES_NO_OPTION);
			if (reallyClose != JOptionPane.YES_OPTION)
			{
				return false;
			}
		}

		if (isClosing)
			menuBar.openRecentPCMenu.add(aPC.getName(), new File(Globals.getPcgPath(), aPC.getFileName())); // move to top

		// save filter settings
		if (tab >= 2)
			character.storeFilterSettings();

		// make sure that all the spell tables are reset
		// for when the next character get's loaded/viewed
		forceUpdate_InfoSpells();

		//be sure to change the selected tab before deleting any others
		baseTabbedPanel.setSelectedIndex(0);     //This tab always exists...

		baseTabbedPanel.removeTabAt(tab);     //This should dispose of the character objects.
		Globals.getPcList().remove(tab - 2);
		Globals.setCurrentPC(null);

		// This will free up resources, which are locked
		// due to the new ACCalculator
		aPC.dispose();

		return true;
	}

	/**
	 * Accessor to change the tab name when the CharacterInfo changes.
	 * Changes the name for the currently selected tab of <code>baseTabbedPanel</code>.
	 *
	 * @param aName The string to set the tab name to
	 */
	public static void setTabName(PlayerCharacter pc, String aName)
	{
		int index = Globals.getPcList().indexOf(pc);
		if (index >= 0)
			getBaseTabbedPanel().setTitleAt(index + 2, aName);
	}

	void printPreviewItem_actionPerformed(ActionEvent e)
	{
		// show the preview in the browser
		InfoPreview.previewInBrowser();
	}

	void warnAboutMissingResource()
	{
		JOptionPane.showMessageDialog(null,
			FOPResourceChecker.getMissingResourceMessage() +
			menuBar.msg,
			"PCGen",
			JOptionPane.WARNING_MESSAGE);
	}

	void printItem_actionPerformed(ActionEvent e)
	{
		/*
		 * changed this, so a warning will popopup, if user
		 * tries to print without having the needed libraries
		 * installed
		 *
		 * author: Thomas Behr 03-01-02
		 */
		if (!menuBar.enablePDF)
		{
			warnAboutMissingResource();
			return;
		}

		menuBar.checkPrintFrame();
		menuBar.printFrame.setCurrentPCSelectionByTab();
		menuBar.printFrame.pack();
		menuBar.printFrame.setVisible(true);
	}

	public void exportToStandardItem_actionPerformed(ActionEvent e)
	{
		if (menuBar.exportPopup == null)
			menuBar.exportPopup = new ExportPopup(baseTabbedPanel);
		menuBar.exportPopup.setCurrentPCSelectionByTab();
	}


	public void exportToPDFItem_actionPerformed(ActionEvent e)
	{
		if (!menuBar.enablePDF)
		{
			warnAboutMissingResource();
			return;
		}

		if (menuBar.exportPDFPopup == null)
			menuBar.exportPDFPopup = new ExportPDFPopup(baseTabbedPanel);
		menuBar.exportPDFPopup.setCurrentPCSelectionByTab();
	}

	/**
	 * update/restore filter settings from globally saved settings
	 *
	 * <br>author: Thomas Behr 24-02-02
	 *
	 * @param filterableName   the name of the Filterable;<br>
	 *                         if <code>null</code> then filters for all
	 *                         Filterables will be updated
	 */
	public static void restoreFilterSettings(String filterableName)
	{
		Component c;
		for (int i = 0; i < getBaseTabbedPanel().getTabCount(); i++)
		{
			c = getBaseTabbedPanel().getComponentAt(i);
			if (i >= 2)
			{
				character.restoreFilterSettings(filterableName);
				break; // only 1 set of filters
			}
//			if (c instanceof CharacterInfo)
//			{
//				((CharacterInfo)c).restoreFilterSettings(filterableName);
//			}
		}
	}

	/**
	 * Accessor to get selected Filterable component of active tab
	 *
	 * <br>author: Thomas Behr
	 */
	public static Filterable getCurrentFilterable()
	{
		int index = getBaseTabbedPanel().getSelectedIndex();
		if (index == 0)
		{
			return (Filterable)getBaseTabbedPanel().getComponentAt(index);
		}
		else if (index > 1)
		{
			return character.getSelectedFilterable();
//			return ((CharacterInfo)getBaseTabbedPanel().getComponentAt(index)).getSelectedFilterable();
		}

		return null;
	}

	/**
	 * Accessor to get CharacterInfo component of active tab.
	 * This is especially needed to change/update a character's name.
	 *
	 */
	public static CharacterInfo getCurrentCharacterInfo()
	{
		int index = getBaseTabbedPanel().getSelectedIndex();
		if (index > 1)
		{
			/**
			 * I would have preferred code like this
			 *    return (CharacterInfo) characterList.elementAt(index - 2);
			 * but characterList is no class field (i.e. not static),
			 * so I use the static JTabbedPane.
			 * Maybe this whole method should not be static,
			 * but then we will be in need of a method to access the actual
			 * instance of PCGen_Frame1, so that NameGUI can access the
			 * current CharacterInfo for setting a new (random) name.
			 *
			 * author: Thomas Behr 20-12-01
			 */
			return character;
//			return (CharacterInfo)getBaseTabbedPanel().getComponentAt(index);
		}
		/**
		 * hope this will not cause any NullPointerExceptions!
		 *
		 * author: Thomas Behr 20-12-01
		 */
		return null;
	}


	/**
	 * Main menu bar of application.
	 * <p>
	 * The File menus is created internally.
	 * Items in the File menu call methods of this class
	 * such as <code>newItem_actionPerformed</code> to
	 * process the click events.
	 * <p>
	 * The Debug and Help menus are also created here,
	 * and load the {@link DebugFrame} and {@link AboutFrame}
	 * respectively.
	 * <p>
	 * The {@link Options}, {@link Preferences}, {@link HomeBrew},
	 * and {@link GameModes} are all created externally.
	 */
	class MenuItems extends JMenuBar
	{
		JOpenRecentMenu openRecentPCMenu;
		JMenu importMenu;
		JMenu exportMenu;
		JOpenRecentMenu openRecentPartyMenu;

		JMenuItem newItem;
		JMenuItem openItem;
		JMenuItem saveItem;
		JMenuItem saveAllItem;
		JMenuItem saveAsItem;
		JMenuItem revertToSavedItem;
		JMenuItem closeItem;
		JMenuItem closeAllItem;
		JMenuItem printItem;
		JMenuItem exportItem;
		JMenuItem exportPDFItem;
		JMenuItem printPreviewItem;
		JMenuItem exitItem;
		JMenuItem partyOpenItem;
		JMenuItem partySaveItem;
		JMenuItem partySaveAsItem;
		JMenuItem partyCloseItem;

		/** Instantiated popup frame {@link AboutFrame}. */
		AboutFrame aboutFrame = null;

		/** Instantiated popup frame {@link DebugFrame}. */
		DebugFrame debugFrame = null;

		/** Instantiated popup frame {@link PrintFrame}. */
		PrintFrame printFrame = null;

		void checkPrintFrame()
		{
			if (printFrame == null)
				printFrame = new MenuItems.PrintFrame();
		}

		/** Instantiated popup frame {@link ExportPopup}. */
		public ExportPopup exportPopup = null;

		/** Instantiated popup frame {@link ExportPDFPopup}. */
		ExportPDFPopup exportPDFPopup = null;

		JCheckBoxMenuItem debugMode = new JCheckBoxMenuItem();

		private boolean enablePDF;
		private final String msg = "\n" + missingLibMsg;

		public MenuItems()
		{
			// check for resources needed by FOP
			enablePDF = (FOPResourceChecker.getMissingResourceCount() == 0);
			Globals.debugPrint(FOPResourceChecker.getMissingResourceMessage());

			//FileMenu
			JMenu fileMenu = Utility.createMenu("File", 'F', "File operations", null, true);

			newItem = Utility.createMenuItem("New", frameActionListener.newActionListener, "file.new", 'N', "control N", "Create a new character", "New16.gif", false);
			fileMenu.add(newItem);

			openItem = Utility.createMenuItem("Open...", frameActionListener.openActionListener, "file.open", 'O', "control O", "Open a character from a .PCG file", "Open16.gif", true);
			fileMenu.add(openItem);

			openRecentPCMenu = new JOpenRecentMenu(new JOpenRecentMenu.OpenRecentCallback()
			{
				public void openRecentPerformed(ActionEvent e, File file)
				{
					loadPCFromFile(file);
				}
			});
			fileMenu.add(openRecentPCMenu);

			fileMenu.addSeparator();

			closeItem = Utility.createMenuItem("Close", frameActionListener.closeActionListener, "file.close", 'C', "control W", "Close the current character", null, false);
			fileMenu.add(closeItem);

			closeAllItem = Utility.createMenuItem("Close All", frameActionListener.closeAllActionListener, "file.closeall", 'L', null, "Close all characters", null, false);
			// Special so that Close A_l_l, not C_l_ose All
			//closeAllItem.setDisplayedMnemonicIndex(7); // JDK 1.4
			fileMenu.add(closeAllItem);

			saveItem = Utility.createMenuItem("Save", frameActionListener.saveActionListener, "file.save", 'S', "control S", "Save the current character to its .PCG file", "Save16.gif", false);
			fileMenu.add(saveItem);

			saveAsItem = Utility.createMenuItem("Save As...", frameActionListener.saveAsActionListener, "file.saveas", 'A', "shift control S", "Save the current character to a new .PCG file", "SaveAs16.gif", false);
			// Special so that Save _A_s..., not S_a_ve As...
			//saveAsItem.setDisplayedMnemonicIndex(5); // JDK 1.4
			fileMenu.add(saveAsItem);

			saveAllItem = Utility.createMenuItem("Save All", frameActionListener.saveAllActionListener, "file.saveall", 'L', null, "Save all characters to their .PCG files", "SaveAll16.gif", false);
			fileMenu.add(saveAllItem);

			revertToSavedItem = Utility.createMenuItem("Revert to Saved", frameActionListener.revertToSavedActionListener, "file.reverttosaved", 'T', "control R", "Reopen the current character from its .PCG file, discarding any changes", null, false);
			fileMenu.add(revertToSavedItem);

			fileMenu.addSeparator();

			printPreviewItem = Utility.createMenuItem("Print Preview", frameActionListener.printPreviewActionListener, "file.printpreview", 'V', null, "Preview the current character in a browser", "PrintPreview16.gif", false);
			fileMenu.add(printPreviewItem);

			printItem = Utility.createMenuItem("Print...", frameActionListener.printActionListener, "file.print", 'P', "control P", "Print the current character", "Print16.gif", false);
			fileMenu.add(printItem);

			fileMenu.addSeparator();

			importMenu = Utility.createMenu("Import", 'I', "Import from other file formats", "Import16.gif", false);
			fileMenu.add(importMenu);

			exportMenu = Utility.createMenu("Export", 'R', "Export to other file formats", "Export16.gif", false);
			fileMenu.add(exportMenu);

			exportItem = Utility.createMenuItem("...standard", frameActionListener.exportToStandardActionListener, "file.export.standard", 'S', null, "Export the current character to templates.", null, true);
			exportMenu.add(exportItem);

			/**
			 * changed this, so a warning will popopup,
			 * if user tries to print without having the needed
			 * libraries installed
			 *
			 * author: Thomas Behr 03-01-02
			 */
			exportPDFItem = Utility.createMenuItem("...to PDF",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (enablePDF)
						{
							if (exportPDFPopup == null)
								exportPDFPopup = new ExportPDFPopup(baseTabbedPanel);
							exportPDFPopup.setCurrentPCSelectionByTab();
						}
						else
						{
							JOptionPane.showMessageDialog(null,
								FOPResourceChecker.getMissingResourceMessage() +
								msg,
								"PCGen",
								JOptionPane.WARNING_MESSAGE);
						}
					}
				},
				"file.export.pdf", 'P', null, "Export the current character to a .PDF file", null, true);
			exportMenu.add(exportPDFItem);

			fileMenu.addSeparator();

			JMenu partyMenu = Utility.createMenu("Party", 'Y', "Party opertions", null, true);
			fileMenu.add(partyMenu);

			partyOpenItem = Utility.createMenuItem("Open...", frameActionListener.partyOpenActionListener, "file.party.open", 'O', null, "Open a party from a .PCC file", "Open16.gif", true);
			partyMenu.add(partyOpenItem);

			openRecentPartyMenu = new JOpenRecentMenu(new JOpenRecentMenu.OpenRecentCallback()
			{
				public void openRecentPerformed(ActionEvent e, File file)
				{
					loadPartyFromFile(file);
				}
			});
			partyMenu.add(openRecentPartyMenu);

			partySaveItem = Utility.createMenuItem("Save", frameActionListener.partySaveActionListener, "file.party.save", 'S', null, "Save the current party to its .PCC file", "Save16.gif", false);
			partyMenu.add(partySaveItem);

			partySaveAsItem = Utility.createMenuItem("Save As...", frameActionListener.partySaveAsActionListener, "file.party.saveas", 'A', null, "Save the current party to a new .PCC file", "SaveAs16.gif", false);
			partyMenu.add(partySaveAsItem);

			partyMenu.addSeparator();

			partyCloseItem = Utility.createMenuItem("Close", frameActionListener.partyCloseActionListener, "file.party.close", 'C', null, "Close the current party", null, false);
			partyMenu.add(partyCloseItem);

			fileMenu.addSeparator();

			//Preferences Menu
			Preferences preferMenu = new Preferences();
			preferMenu.setMnemonic('F');
			fileMenu.add(preferMenu);

			fileMenu.addSeparator();

			exitItem = Utility.createMenuItem("Exit",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.exitItem_actionPerformed(e);
					}
				},
				"file.exit", 'X', "control Q", "Quit PCGen", null, true);
			fileMenu.add(exitItem);
			MenuItems.this.add(fileMenu);

			//Modes menu
			GameModes modesMenu = new GameModes();
			modesMenu.setMnemonic('G');
			MenuItems.this.add(modesMenu);

			//Options Menu
			Options optionMenu = new Options();
			optionMenu.setMnemonic('O');
			MenuItems.this.add(optionMenu);

			//Home brew menu
			HomeBrew homeBrewMenu = new HomeBrew();
			homeBrewMenu.setMnemonic('R');
			MenuItems.this.add(homeBrewMenu);

			//Debug Menu
			JMenu debugMenu = Utility.createMenu("Debug", 'D', "Debug operations", null, true);

			debugMode = new JCheckBoxMenuItem();
			debugMode.setMnemonic('M');
			Utility.setDescription(debugMode, "Show debug messages");
			debugMode.setText("Debug Mode");
			debugMode.setSelected(Globals.isDebugMode());
			debugMode.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						Globals.setDebugMode(debugMode.isSelected());
						if (exportPopup != null)
						{
							exportPopup.refreshTemplates();
						}
					}
				});
			debugMenu.add(debugMode);

			JMenuItem consoleMenuItem = Utility.createMenuItem("Console",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (debugFrame == null)
							debugFrame = new DebugFrame();
						debugFrame.setVisible(true);
					}
				},
				"debug.console", 'C', null, "Send debug output to the Java console", null, true);
			debugMenu.add(consoleMenuItem);
			MenuItems.this.add(debugMenu);

			//Help Menu
			JMenu helpMenu = Utility.createMenu("Help", 'H', "Help operations", "Help16.gif", true);

			JMenuItem contextHelpItem = Utility.createMenuItem("Help on context",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PToolBar.displayHelpPanel (true);
					}
				},
				"help.context", 'C', null, "Help on current context", "ContextualHelp16.gif", true);
			helpMenu.add(contextHelpItem);

			JMenuItem docsItem = Utility.createMenuItem("Documentation",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						new DocsFrame();
					}
				},
				"help.docs", 'D', "F1", "Read the documentation in a browser", "Help16.gif", true);
			helpMenu.add(docsItem);

			helpMenu.addSeparator();

			JMenuItem aboutItem = Utility.createMenuItem("About PCGen",
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (aboutFrame == null)
							aboutFrame = new AboutFrame();
						aboutFrame.setVisible(true);
					}
				},
				"help.about", 'A', null, "About PCGen", "About16.gif", true);
			helpMenu.add(aboutItem);
			MenuItems.this.add(helpMenu);

			// add all top-level menus to menu bar
			//JMenuBar menuBar = new JMenuBar();
			//setJMenuBar(menuBar);
		}

		/**
		 * Popup frame with about info
		 */
		class AboutFrame extends PCGenPopup
		{
			public AboutFrame()
			{
				super("About PCGen", new MainAbout());
			}
		}

		/**
		 * Popup frame with debug console
		 */
		class DebugFrame extends PCGenPopup
		{
			public DebugFrame()
			{
				super("Debug Console", new MainDebug());
			}
		}

		/**
		 * Popup frame with print options
		 *
		 * author: Thomas Behr 16-12-01
		 */
		class PrintFrame extends PCGenPopup
		{
			MainPrint mainPrint = null;

			public PrintFrame()
			{
				super("Print a PC or Party");
				mainPrint = new MainPrint(this, MainPrint.PRINT_MODE);
				setPanel(mainPrint);
			}

			public void setCurrentPCSelectionByTab()
			{
				if (mainPrint != null)
				{
					mainPrint.setCurrentPCSelection(baseTabbedPanel.getSelectedIndex());
				}
			}
		}//end PrintFrame

		/**
		 * Pop up frame with Documentation.

		 */
		private class DocsFrame extends JFrame
		{
			public DocsFrame()
			{
				try
				{
					String homeDir = System.getProperty("user.dir");
					BrowserLauncher.openURL(homeDir + "/doc" + File.separator + "Index.htm");
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(null,
						"Could not open docs in external browser. " +
						"Have you set your default browser in the " +
						"Preference menu? Sorry...",
						"PCGen",
						JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
				/**
				 super("future html docs");
				 ClassLoader loader = getClass().getClassLoader();
				 Toolkit kit = Toolkit.getDefaultToolkit();
				 // according to the API, the following should *ALWAYS* use '/'
				 Image img =
				 kit.getImage(loader.getResource("pcgen/gui/resource/PcgenIcon.gif"));
				 loader = null;
				 this.setIconImage(img);
				 Utility.centerFrame(this, true);
				 setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				 setVisible(true);
				 */
			}
		}//end DocsFrame

	}//MenuItems

	public static PCGen_Frame1 getRealParentFrame(JPanel child)
	{
		//There *is* a better way to do this. I just haven't thought of it yet.
		PCGen_Frame1 parent = (PCGen_Frame1)child.getParent().getParent().getParent().getParent().getParent();
		return parent;
	}

	//
	// Update the available equipment list for all character's loaded, but only purchase
	// for the active one.
	//
	public void eqList_Changed(Equipment newEq, boolean purchase)
	{
		if (character != null)
			character.eqList_Changed(newEq, purchase, true);
	}

	public void hpTotal_Changed()
	{
		if (character != null)
		{
			character.infoClasses().updateHp();
		}
	}

	public void featList_Changed()
	{
		if (character != null)
		{
			character.featList_Changed();
		}
	}

	public static void forceUpdate_InfoEquipping()
	{
		if (character != null)
		{
			character.infoEquipping().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoDomain()
	{
		if (character != null)
		{
			character.infoDomains().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoFeats()
	{
		if (character != null)
		{
			character.infoFeats().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoSkills()
	{
		if (character != null)
		{
			character.infoSkills().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoSpells()
	{
		if (character != null)
		{
			character.infoSpells().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoInventory()
	{
		if (character != null)
		{
			character.infoInventory().setNeedsUpdate(true);
		}
	}


	// This is a convenience method for breaking up the info block, inserting
	// <br> tags so it will scroll properly in the infoLabel.
	public static String breakupString(String text, int pixelWidth)
	{
		int textLength = text.length();
		pixelWidth /= 7;
		StringBuffer wrapped = new StringBuffer(textLength);
		while (textLength > pixelWidth)
		{
			int lastBreak = -1;
			boolean bInHtmlTag = false;
			int displayedCount = 0;
			for (int pos = 0; pos < textLength; pos++)
			{
				if (displayedCount >= pixelWidth)
				{
					break;
				}
				switch (text.charAt(pos))
				{
					case ' ':
						if (!bInHtmlTag)
						{
							lastBreak = pos;
							displayedCount += 1;
						}
						break;

					case '<':
						bInHtmlTag = true;
						break;

					case '>':
						bInHtmlTag = false;
						break;

					default:
						if (!bInHtmlTag)
						{
							displayedCount += 1;
						}
						break;
				}
			}

			if (displayedCount < pixelWidth)
			{
				lastBreak = textLength;
			}
			if (lastBreak == -1)
			{
				lastBreak = pixelWidth;
			}
			if (wrapped.length() != 0)
			{
				wrapped.append("<br>");
			}
			wrapped.append(text.substring(0, lastBreak));
			text = text.substring(lastBreak).trim();
			textLength = text.length();
		}
		if (text.length() != 0)
		{
			if (wrapped.length() != 0)
			{
				wrapped.append("<br>");
			}
			wrapped.append(text);
		}
		if (!wrapped.toString().startsWith("<html>"))
		{
			wrapped.insert(0, "<html>");
			wrapped.append("</html>");
		}
		return wrapped.toString();
	}


}
