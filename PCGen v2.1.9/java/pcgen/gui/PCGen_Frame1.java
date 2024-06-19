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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import pcgen.core.Campaign;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;

/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example
 * {@link pcgen.gui.MainCampaign} also loads any
 * default campaigns.
 *
 * @author     Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version    $Revision: 1.1 $
 */
public class PCGen_Frame1 extends JFrame
{
	private static JLabel statusBar = new JLabel();

	public static JLabel getStatusBar()
	{
		return statusBar;
	}

	/**
	 * Main tabbed panel of the application.
	 * The first two tabs contain the {@link #mainCampaign}
	 * ("Campaign") and {@link #tabDMTools} ("DM Tools")
	 * panels.
	 * Additional {@link CharacterInfo} panel tabs are added
	 * for each created character.
	 */
	private static JTabbedPane baseTabbedPanel = new JTabbedPane();

	public static javax.swing.JTabbedPane getBaseTabbedPanel()
	{
		return baseTabbedPanel;
	}

	private final JFileChooser fc = new JFileChooser();
	private final PcgFilter filter = new PcgFilter();
	private final PcpFilter partyFilter = new PcpFilter();
	private String partyFileName = "";  //used to keep track of last .pcp file used

	/**
	 * Contains the campaign screen.
	 *
	 * @see MainCampaign
	 */
	private MainCampaign mainCampaign = new MainCampaign();

	/**
	 * Menu for the main application.
	 */
	private MenuItems menuBar = new MenuItems();

	/**
	 * Collection of loaded {@link CharacterInfo} panels;
	 * each is a view of a single character.
	 * <p>
	 * This essentially contains the same things as the additional
	 * tabs in <code>baseTabbedPanel</code>. i.e. <br />
	 * <code>baseTabbedPanel.getComponentAt(x) == characterList.elementAt(x - 2)</code>
	 */
	private Vector characterList = new Vector();

	// GUI stuff
	private JPanel panelSouth = new JPanel();
	private JPanel panelSouthEast = new JPanel();
	private JPanel panelSouthCenter = new JPanel();

	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();
	private BorderLayout borderLayout3 = new BorderLayout();

	private FlowLayout flowLayout1 = new FlowLayout();
	private FlowLayout flowLayout2 = new FlowLayout();
	private FlowLayout flowLayout4 = new FlowLayout();

	private JComboBox jComboBox1 = new JComboBox();

	private JTabbedPane tabMain = new JTabbedPane();

	//JPanel panelNorth = new JPanel();
	//JLabel lblName = new JLabel();
	//JPanel mainAbout = new MainAbout();


	// This used to be a tabbed pane, now
	// created in DebugFrame - no longer used?
	private MainDebug mainDebug = new MainDebug();

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
		catch (Exception e)
		{
			e.printStackTrace();
		}

		fc.setFileFilter(partyFilter);
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
		ClassLoader loader = getClass().getClassLoader();
		Toolkit kit = Toolkit.getDefaultToolkit();
		// according to the API, the following should *ALWAYS* use '/'
		Image img =
			kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
		loader = null;
		this.setIconImage(img);

		this.getContentPane().setLayout(borderLayout1);
		this.setSize(new Dimension(640, 400));
		this.setTitle("PCGen");

		this.setJMenuBar(menuBar);

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
		baseTabbedPanel.add(mainCampaign, "Source Materials");

		//   baseTabbedPanel.add(mainDebug, "Debug");

		baseTabbedPanel.add(tabDMTools, "DM Tools");
		//tabDMTools.add(toolsSetup, "Setup");
		tabDMTools.add(toolsCombat, "Combat");
		tabDMTools.add(toolsQuick, "Quick Stats");
		tabDMTools.add(toolsXP, "XP Tracker");
		tabDMTools.add(toolsNotes, "Notes");
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

		if (Globals.displayListsHappy())
		{
			enableNew(true);
		}
	}

	/**
	 * Enable/disable the new button
	 */
	protected void enableNew(boolean itemState)
	{
		menuBar.newItem.setEnabled(itemState);
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
			if (closePCTab(tabCount - 1))
			{
				tabCount = baseTabbedPanel.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}

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
		menuBar.newItem.requestFocus();
		try
		{
			final PlayerCharacter aPC = new PlayerCharacter();
			Globals.getPcList().add(aPC);
			count++;
			String tabName = "New" + count;
			aPC.setName(tabName);
			aPC.setDirty(true);
			CharacterInfo character = new CharacterInfo();
			characterList.addElement(character);
			// The API docs for JTabbedPane seem to indicate the name must
			// come first, then the component. Also, the tutorial seems
			// to prefer addTab (rather than add).
			baseTabbedPanel.add(character, tabName);
			baseTabbedPanel.setSelectedComponent(character);
		}
		catch (Exception e1)
		{
			JOptionPane.showMessageDialog(null, "Error creating character: " + e1.getMessage(), "PCGen", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}
		// aPC.setFileName(tabName + Globals.s_PCGEN_CHARACTER_EXTENSION);
	}

	/**
	 * Load a party metafile, including campaign info and characters.
	 * Campaigns are loaded as from the Campaign tab.
	 * Characters are loaded into a new <code>PlayerCharacter</code> model, and
	 * a corresponding <code>CharacterInfo</code> panel is created.
	 */
	void openParty_actionPerformed(ActionEvent event)
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Loading Party.  Please wait...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.openParty.requestFocus();

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

		fc.setFileFilter(filter);
	}

	void loadPartyFromFile(File file)
	{
		ArrayList campList = new ArrayList();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			//load campaign data
			String campaignFiles = br.readLine();
			//load character filename data
			String charFiles = br.readLine();
			br.close();

			//parse campaign data and load the listed campaigns
			StringTokenizer campTok = new StringTokenizer(campaignFiles, ":");
			while (campTok.hasMoreTokens())
			{
				Campaign aCamp = Globals.getCampaignNamed(campTok.nextToken());
				if (aCamp != null)
				{
					if (!aCamp.isLoaded())
					{
						campList.add(aCamp);
					}
				}
			}
			if (campList.size() > 0)
			{
				Globals.loadCampaigns(campList);
				mainCampaign.updateLoadedCampaignsUI();
			}

			//parse PC data and load the listed PC's
			StringTokenizer pcTok = new StringTokenizer(charFiles, ":");
			while (pcTok.hasMoreTokens())
			{
				File pcFile = new File(Globals.getPcgPath(), pcTok.nextToken());
				if (pcFile.exists())
				{
					loadPCFromFile(pcFile);
				}
			}
			//if everything loaded successfully, then this file becomes the "current" party file
			partyFileName = file.getName();
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not load party file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	/**
	 * Saves a party metafile, including campaign info and characters.
	 */
	void saveParty_actionPerformed(ActionEvent event)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveParty.requestFocus();

		//save party file here
		fc.setFileFilter(partyFilter);
		saveParty();

		//save character files here
		fc.setFileFilter(filter);
		saveAllPCs();
	}

	void saveParty()
	{
		boolean newParty = false;
		if (partyFileName.length() == 0)
		{
			partyFileName = "Party" + Globals.s_PCGEN_PARTY_EXTENSION;
			newParty = true;
		}

		FilenameChangeListener listener = new FilenameChangeListener(partyFileName);

		File prevFile = new File(Globals.getPcgPath(), partyFileName);
		fc.setSelectedFile(prevFile);
		fc.addPropertyChangeListener(listener);
		int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
		fc.removePropertyChangeListener(listener);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			if (file.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a party.", "PCGen", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (file.exists() && (newParty || !file.equals(prevFile)))
			{
				int reallyClose = JOptionPane.showConfirmDialog(this,
					"The file " + file.getName() + " already exists, are you sure you want to overwrite it?",
					"Confirm overwriting " + file.getName(),
					JOptionPane.YES_NO_OPTION);
				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			try
			{
				if (!file.getName().endsWith(Globals.s_PCGEN_PARTY_EXTENSION))
				{
					file = new File(file.getName() + Globals.s_PCGEN_PARTY_EXTENSION);
				}
				partyFileName = file.getName();
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				// Save party file data here
				Iterator anIter = Globals.getCampaignList().iterator();
				//save campaign file info
				while (anIter.hasNext())
				{
					Campaign aCampaign = (Campaign)anIter.next();
					if (aCampaign.isLoaded())
						bw.write(aCampaign.getName() + ":");
				}
				bw.newLine();
				anIter = Globals.getPcList().iterator();
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
				JOptionPane.showMessageDialog(null, "Could not save " + partyFileName, "PCGen", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		}
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
		statusBar.setText("Loading Character.  Please wait...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.openItem.requestFocus();
		fc.setCurrentDirectory(Globals.getPcgPath());
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			Globals.setPcgPath(file.getParentFile());
			fc.setMultiSelectionEnabled(false);
			loadPCFromFile(file);
		}
		statusBar.setText(oldStatus);
	}

	void loadPCFromFile(File file)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			loadCampaignsForPC(br);
			if (!Globals.displayListsHappy())
			{
				JOptionPane.showMessageDialog(null, "Insufficient campaign information to load character file.", "PCGen", JOptionPane.ERROR_MESSAGE);
				return;
			}
			final PlayerCharacter aPC = new PlayerCharacter();
			aPC.load(br);
			br.close();
			Globals.getPcList().add(aPC);
			CharacterInfo character = new CharacterInfo();
			characterList.addElement(character);
			String tabName = aPC.getName();
			baseTabbedPanel.add(character, tabName);
			baseTabbedPanel.setSelectedComponent(character);
			aPC.setFileName(file.getName());
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not load character file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private void loadCampaignsForPC(BufferedReader br)
	{
		try
		{
			br.mark(1024);  //set a mark so we can reset in the event of an old .pcg file
			String lastLineParsed = br.readLine();
			StringTokenizer aTok = new StringTokenizer(lastLineParsed, ":", false);
			String sCamp = aTok.nextToken();
			//if the pcg file starts with CAMPAIGN data then lets process it
			if (sCamp.equals("CAMPAIGNS"))
			{
				ArrayList campList = new ArrayList();
				while (aTok.hasMoreTokens())
				{
					Campaign aCamp = Globals.getCampaignNamed(aTok.nextToken());
					if (aCamp != null)
					{
						if (!aCamp.isLoaded())
						{
							campList.add(aCamp);
						}
					}
				}
				if (campList.size() > 0)
				{
					Globals.loadCampaigns(campList);
					mainCampaign.updateLoadedCampaignsUI();
				}
			}
			else
			{
				//this is an old .pcg file (no campaign data) so just reset the input stream
				br.reset();
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Could not load campaign data from character file.", "PCGen", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
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
		// Globals.getCurrentPC()
		baseTabbedPanel.requestFocus();  //reinstate the requestForcus to prevent open edits from applying to the wrong PC

		int currentPanel = baseTabbedPanel.getSelectedIndex();
		if (currentPanel <= 1)
		{
			menuBar.closeItem.setEnabled(false);
			menuBar.closeItemAll.setEnabled(false);
			menuBar.saveItem.setEnabled(false);
			menuBar.saveItemAll.setEnabled(false);
			menuBar.previewItem.setEnabled(false);
			if (baseTabbedPanel.getTabCount() < 3)
			{
				menuBar.saveParty.setEnabled(false);
				menuBar.exportItem.setEnabled(false);
			}
			toolsQuick.refreshTable();
			toolsCombat.refreshTable();
			toolsXP.refreshTable();
		}
		else
		{
			menuBar.closeItem.setEnabled(true);
			menuBar.saveItem.setEnabled(true);
			menuBar.previewItem.setEnabled(true);
			Globals.setCurrentPC((PlayerCharacter)Globals.getPcList().get(currentPanel - 2));
			CharacterInfo ci;
			ci = (CharacterInfo)characterList.elementAt(currentPanel - 2);


			JTabbedPane aPane = (JTabbedPane)ci.getComponent(0);
			int x = aPane.getComponentCount();
			// Domains/Spells either 5&6 or last two components
			if (Globals.isStarWarsMode())
			{
				if (aPane.getComponent(6) == ci.infoSpells() || aPane.getComponent(x - 1) == ci.infoSpells())
					aPane.remove(ci.infoSpells());
				if (aPane.getComponent(5) == ci.infoDomains() || aPane.getComponent(x - 2) == ci.infoDomains())
					aPane.remove(ci.infoDomains());
			}
			else
			{
				if (aPane.getComponent(5) != ci.infoDomains())
					aPane.add(ci.infoDomains(), "Domains", 5);
				if (aPane.getComponent(6) != ci.infoSpells())
					aPane.add(ci.infoSpells(), "Spells", 6);
			}

			if (baseTabbedPanel.getTabCount() > 3)
			{
				menuBar.closeItemAll.setEnabled(true);
				menuBar.saveItemAll.setEnabled(true);
			}
			if (baseTabbedPanel.getTabCount() > 2)
			{
				menuBar.saveParty.setEnabled(true);
				menuBar.exportItem.setEnabled(true);
			}
		}
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
	}


	/**
	 * Closes the currently selected character tab.
	 */
	void closeItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.closeItem.requestFocus();
		int currTab = baseTabbedPanel.getSelectedIndex();
		if (currTab > 1)
		{

			if (!closePCTab(currTab))
			{
				return;
			}

			int tabCount = baseTabbedPanel.getTabCount();
			if (tabCount < 4)
			{
				menuBar.closeItemAll.setEnabled(false);
				menuBar.saveItemAll.setEnabled(false);
			}
			if (tabCount < 3)
			{
				count = 0;
				menuBar.saveParty.setEnabled(false);
				menuBar.exportItem.setEnabled(false);
			}
		}
	}

	/**
	 * Close all open character tabs
	 */
	void closeItemAll_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.closeItemAll.requestFocus();
		closeAllPCs();
	}

	public void closeAllPCs()
	{
		int tabCount = baseTabbedPanel.getTabCount();
		while (tabCount > 2)
		{
			if (closePCTab(tabCount - 1))
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
		menuBar.closeItemAll.setEnabled(false);
		menuBar.saveItem.setEnabled(false);
		menuBar.saveItemAll.setEnabled(false);
		menuBar.saveParty.setEnabled(false);
		menuBar.exportItem.setEnabled(false);
		menuBar.previewItem.setEnabled(false);
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
		int currTab = baseTabbedPanel.getSelectedIndex();
		fc.setFileFilter(filter);
		final PlayerCharacter aPC = ((PlayerCharacter)Globals.getPcList().get(currTab - 2));

		savePC(aPC);
	}

	/**
	 * Relocated preview code to work off the File|Preview... menu item right here
	 *
	 * @author Jason Buchanan <lonejedi70@hotmail.com>
	 */
	void browserPreview(String aFileName)
	{
		try
		{
			File outFile = new File(aFileName);

			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			File template = new File(Globals.getSelectedTemplate());
			String name = template.getName().toLowerCase();
			Globals.getCurrentPC().print(template, bw);
			bw.close();

			java.net.URL url = new java.net.URL("file://" + aFileName);
			try
			{
				BrowserLauncher.openURL(url.toString());
			}
			catch (IOException ioe)
			{
				System.out.println("IOException launching browser: " + ioe);
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not preview file in external browser. Sorry...", "PCGen", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	/**
	 * Property change listener for the event "selected file changed".
	 * Ensures that the filename doesn't get changed when a directory is selected.
	 *
	 * @author Dmitry Jemerov <yole@spb.cityline.ru>
	 */

	class FilenameChangeListener implements PropertyChangeListener
	{
		private String lastSelName;

		FilenameChangeListener(String aFileName)
		{
			lastSelName = aFileName;
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
			fc.setSelectedFile(new File(fc.getCurrentDirectory(), lastSelName));
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
	 * @param  aPC  The <code>PlayerCharacter<code> to save.
	 */
	void savePC(PlayerCharacter aPC)
	{

		boolean newPC = false;
		if (aPC.getFileName().length() == 0)
		{
			aPC.setFileName(aPC.getName() + Globals.s_PCGEN_CHARACTER_EXTENSION);
			newPC = true;
		}

		FilenameChangeListener listener = new FilenameChangeListener(aPC.getFileName());

		File prevFile = new File(Globals.getPcgPath(), aPC.getFileName());
		fc.setSelectedFile(prevFile);
		fc.addPropertyChangeListener(listener);
		int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
		fc.removePropertyChangeListener(listener);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();

			if (file.isDirectory())
			{
				JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a character.", "PCGen", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (file.exists() && (newPC || !file.equals(prevFile)))
			{
				int reallyClose = JOptionPane.showConfirmDialog(this,
					"The file " + file.getName() + " already exists, are you sure you want to overwrite it?",
					"Confirm overwriting " + file.getName(),
					JOptionPane.YES_NO_OPTION);
				if (reallyClose != JOptionPane.YES_OPTION)
				{
					return;
				}
			}

			try
			{
				if (!file.getName().endsWith(Globals.s_PCGEN_CHARACTER_EXTENSION))
				{
					file = new File(file.getName() + Globals.s_PCGEN_CHARACTER_EXTENSION);
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(file));
				aPC.save(bw);
				bw.close();

				Globals.setPcgPath(file.getParentFile());

			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(null, "Could not save " + aPC.getName(), "PCGen", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
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
				savePC(aPC);
			pcsToSave--;
		}
	}

	/**
	 * Saves all open characters. Iterates through
	 * <code>Globals.getPcList()</code> (not the tabs) and runs
	 * <code>savePC</code> for each one.
	 */
	void saveItemAll_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveItemAll.requestFocus();
		fc.setFileFilter(filter);
		saveAllPCs();
	}

	/**
	 * Close a tab by tab number, not pc number.
	 *
	 * @param  tab  Tab the character is on (not PC number)
	 * @return      Not relevant - always <code>true</code>
	 */
	private boolean closePCTab(int tab)
	{
		final PlayerCharacter aPC = (PlayerCharacter)Globals.getPcList().get(tab - 2);
		Globals.setCurrentPC(aPC);
		if (aPC.isDirty())
		{
			int reallyClose = JOptionPane.showConfirmDialog(this,
				"The character " + aPC.getName() + " has not been saved, are you sure you want to close it?",
				"Confirm closing " + aPC.getName() + " without saving it.",
				JOptionPane.YES_NO_OPTION);
			if (reallyClose != JOptionPane.YES_OPTION)
			{
				return false;
			}
		}

		//be sure to change the selected tab before deleting any others
		baseTabbedPanel.setSelectedIndex(0);     //This tab always exists...

		baseTabbedPanel.removeTabAt(tab);     //This should dispose of the character objects.
		Globals.getPcList().remove(tab - 2);
		characterList.removeElementAt(tab - 2);     //The pc corresponding to the tab.
		Globals.setCurrentPC(null);

		return true;
	}

	/**
	 * Accessor to change the tab name when the CharacterInfo changes.
	 * Changes the name for the currently selected tab of <code>baseTabbedPanel</code>.
	 *
	 * @param aName The string to set the tab name to
	 */
	public static void setTabName(String aName)
	{
		if (baseTabbedPanel.getSelectedIndex() > 1)
			baseTabbedPanel.setTitleAt(baseTabbedPanel.getSelectedIndex(), aName);
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
		JMenuItem newItem;
		JMenuItem openItem;
		JMenuItem saveItem;
		JMenuItem saveItemAll;
		JMenuItem closeItem;
		JMenuItem closeItemAll;
		JMenuItem exportItem;
		JMenuItem previewItem;
		JMenuItem exitItem;
		JMenuItem openParty;
		JMenuItem saveParty;

		/** Instantiated popup frame {@link AboutFrame}. */
		AboutFrame aboutFrame = null;

		/** Instantiated popup frame {@link DebugFrame}. */
		DebugFrame debugFrame = null;

		/** Instantiated popup frame {@link ExportFrame}. */
		ExportFrame exportFrame = null;

		JCheckBoxMenuItem debugMode = new JCheckBoxMenuItem();

		public MenuItems()
		{
			//FileMenu
			JMenu fileMenu = new JMenu("File");
			newItem = new JMenuItem("New");
			fileMenu.add(newItem);
			newItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, InputEvent.CTRL_MASK));
			newItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.newItem_actionPerformed(e);
					}
				});
			openItem = new JMenuItem("Open");
			fileMenu.add(openItem);
			openItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.openItem_actionPerformed(e);
					}
				});

			fileMenu.addSeparator();
			saveItem = new JMenuItem("Save");
			fileMenu.add(saveItem);
			saveItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.saveItem_actionPerformed(e);
					}
				});
			saveItemAll = new JMenuItem("Save All");
			fileMenu.add(saveItemAll);
			saveItemAll.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.saveItemAll_actionPerformed(e);
					}
				});
			closeItem = new JMenuItem("Close");
			fileMenu.add(closeItem);
			closeItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.closeItem_actionPerformed(e);
					}
				});
			closeItemAll = new JMenuItem("Close All");
			fileMenu.add(closeItemAll);
			closeItemAll.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.closeItemAll_actionPerformed(e);
					}
				});
			fileMenu.addSeparator();
			exportItem = new JMenuItem("Export...");
			fileMenu.add(exportItem);
			exportItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if (exportFrame == null)
							exportFrame = new ExportFrame();
						exportFrame.setCurrentPCSelectionByTab();
						exportFrame.pack();
						exportFrame.setVisible(true);
					}
				});
			previewItem = new JMenuItem("Preview...");
			fileMenu.add(previewItem);
			previewItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final String template = Globals.getSelectedTemplate();
						String extension = template.substring(template.indexOf('.') + 1);
						PCGen_Frame1.this.browserPreview(Globals.getHtmlOutputPath() + File.separator + "currentPC." + extension);
					}
				});
			fileMenu.addSeparator();
			JMenu partyMenu = new JMenu("Party");
			openParty = new JMenuItem("Load");
			openParty.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.openParty_actionPerformed(e);
					}
				});
			saveParty = new JMenuItem("Save");
			saveParty.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.saveParty_actionPerformed(e);
					}
				});
			partyMenu.add(openParty);
			partyMenu.add(saveParty);
			fileMenu.add(partyMenu);
			fileMenu.addSeparator();
			exitItem = new JMenuItem("Exit");
			fileMenu.add(exitItem);
			exitItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, InputEvent.CTRL_MASK));
			exitItem.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						PCGen_Frame1.this.exitItem_actionPerformed(e);
					}
				});

			//Options Menu
			Options optionMenu = new Options();

			//Preferences Menu
			Preferences preferMenu = new Preferences();

			//Home brew menu
			HomeBrew homeBrewMenu = new HomeBrew();

			//Modes menu
			GameModes modesMenu = new GameModes();

			//Debug Menu
			JMenu debugMenu = new JMenu("Debug");
			debugMode = new JCheckBoxMenuItem();
			debugMode.setToolTipText("Show debug messages");
			debugMode.setText("Debug Mode");
			debugMode.setSelected(Globals.isDebugMode());
			debugMode.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						Globals.setDebugMode(debugMode.isSelected());
						//baseTabbedPanel.add(mainDebug, "Debug");
						//baseTabbedPanel.setSelectedComponent(mainDebug);
					}
				});
			debugMenu.add(debugMode);
			JMenuItem debugItem = new JMenuItem("Console");
			debugMenu.add(debugItem);
			debugItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (debugFrame == null)
						debugFrame = new DebugFrame();
					debugFrame.setVisible(true);
				}
			});

			//Help Menu
			JMenu helpMenu = new JMenu("Help");
			JMenuItem aboutItem = new JMenuItem("About");
			helpMenu.add(aboutItem);
			aboutItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (aboutFrame == null)
						aboutFrame = new AboutFrame();
					aboutFrame.setVisible(true);
				}
			});
			JMenuItem docItem = new JMenuItem("Docs");
			helpMenu.add(docItem);
			docItem.setEnabled(false);
			docItem.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					DocFrame frame = new DocFrame();
				}
			});

			// add all top-level menus to menu bar
			//JMenuBar menuBar = new JMenuBar();
			//setJMenuBar(menuBar);

			this.add(fileMenu);
			this.add(preferMenu);
			this.add(modesMenu);
			this.add(optionMenu);
			this.add(homeBrewMenu);
			this.add(debugMenu);
			this.add(helpMenu);

			newItem.setEnabled(false);
			saveItem.setEnabled(false);
			saveItemAll.setEnabled(false);
			closeItem.setEnabled(false);
			closeItemAll.setEnabled(false);
			saveParty.setEnabled(false);
			exportItem.setEnabled(false);
			previewItem.setEnabled(false);

			//set mnemonics
			fileMenu.setMnemonic('F');
			optionMenu.setMnemonic('O');
			debugMenu.setMnemonic('D');
			debugItem.setMnemonic('C');
			helpMenu.setMnemonic('H');
			aboutItem.setMnemonic('A');
			newItem.setMnemonic('N');
			openItem.setMnemonic('O');
			saveItem.setMnemonic('S');
			saveItemAll.setMnemonic('A');
			closeItem.setMnemonic('E');
			closeItemAll.setMnemonic('C');
			openParty.setMnemonic('L');
			saveParty.setMnemonic('S');
			exportItem.setMnemonic('X');
			previewItem.setMnemonic('V');
			homeBrewMenu.setMnemonic('H');
			preferMenu.setMnemonic('P');
			modesMenu.setMnemonic('G');
		}

		/**
		 * Popup frame with about info
		 */
		class AboutFrame extends JFrame
		{
			public AboutFrame()
			{
				super("About");
				ClassLoader loader = getClass().getClassLoader();
				Toolkit kit = Toolkit.getDefaultToolkit();
				// according to the API, the following should *ALWAYS* use '/'
				Image img = kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
				loader = null;
				this.setIconImage(img);
				Dimension screenSize = kit.getScreenSize();
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;

				// center frame in screen
				setSize(screenWidth / 2, screenHeight / 2);
				setLocation(screenWidth / 4, screenHeight / 4);

				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				MainAbout mainAbout = new MainAbout();
				Container contentPane = getContentPane();
				contentPane.add(mainAbout);
				setVisible(true);
			}
		}//end AboutFrame

		/**
		 * Popup frame with debug console
		 */
		class DebugFrame extends JFrame
		{
			public DebugFrame()
			{
				super("Debug Console");
				ClassLoader loader = getClass().getClassLoader();
				Toolkit kit = Toolkit.getDefaultToolkit();
				// according to the API, the following should *ALWAYS* use '/'
				Image img = kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
				loader = null;
				this.setIconImage(img);
				Dimension screenSize = kit.getScreenSize();
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;

				// center frame in screen
				setSize(screenWidth / 2, screenHeight / 2);
				setLocation(screenWidth / 4, screenHeight / 4);

				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				MainDebug mainDebug = new MainDebug();
				Container contentPane = getContentPane();
				contentPane.add(mainDebug);
				setVisible(true);
			}
		}//end DebugFrame

		/**
		 * Popup frame with export options
		 */
		class ExportFrame extends JFrame
		{
			MainExport mainExport = null;

			public ExportFrame()
			{
				super("Export a PC or Party");
				ClassLoader loader = getClass().getClassLoader();
				Toolkit kit = Toolkit.getDefaultToolkit();
				// according to the API, the following should *ALWAYS* use '/'
				Image img = kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
				loader = null;
				this.setIconImage(img);
				Dimension screenSize = kit.getScreenSize();
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;

				// center frame in screen
				setSize(screenWidth / 2, screenHeight / 2);
				setLocation(screenWidth / 4, screenHeight / 4);

				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				mainExport = new MainExport();
				Container contentPane = getContentPane();
				contentPane.add(mainExport);
				setVisible(true);
			}

			public void setCurrentPCSelectionByTab()
			{
				if (mainExport != null)
				{
					mainExport.setCurrentPCSelection(baseTabbedPanel.getSelectedIndex());
				}
			}
		}//end ExportFrame

		/**
		 * Pop up frame with Documentation.
		 */
		private class DocFrame extends JFrame
		{
			public DocFrame()
			{
				super("future html docs");
				ClassLoader loader = getClass().getClassLoader();
				Toolkit kit = Toolkit.getDefaultToolkit();
				// according to the API, the following should *ALWAYS* use '/'
				Image img =
					kit.getImage(loader.getResource("pcgen/gui/PcgenIcon.gif"));
				loader = null;
				this.setIconImage(img);
				Dimension screenSize = kit.getScreenSize();
				int screenHeight = screenSize.height;
				int screenWidth = screenSize.width;

				// center frame in screen
				setSize(screenWidth / 2, screenHeight / 2);
				setLocation(screenWidth / 4, screenHeight / 4);
				setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				setVisible(true);
			}
		}//end DocFrame

	}//MenuItems

	public static PCGen_Frame1 getRealParentFrame(JPanel child)
	{
		//There *is* a better way to do this. I just haven't thought of it yet.
		PCGen_Frame1 parent = (PCGen_Frame1)child.getParent().getParent().getParent().getParent().getParent();
		return parent;
	}


}
