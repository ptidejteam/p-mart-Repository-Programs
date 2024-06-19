/*
 * PCGen_Frame1.java
 * Copyright 2001 (C) Bryan McRoberts <mocha@mcs.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 01:28:08 $
 *
 */

package pcgen.gui;

import gmgen.plugin.InitHolder;
import gmgen.plugin.InitHolderList;
import gmgen.plugin.PcgCombatant;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.messages.InitHolderListSendMessage;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import gmgen.pluginmgr.messages.PCClosedMessage;
import gmgen.pluginmgr.messages.PCLoadedMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import pcgen.core.Constants;
import pcgen.core.CustomData;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.core.character.Follower;
import pcgen.core.party.Party;
import pcgen.gui.filter.FilterDialogFactory;
import pcgen.gui.filter.FilterFactory;
import pcgen.gui.filter.Filterable;
import pcgen.gui.tabs.InfoClasses;
import pcgen.gui.tabs.InfoDomain;
import pcgen.gui.tabs.InfoFeats;
import pcgen.gui.tabs.InfoSkills;
import pcgen.gui.tabs.InfoSummary;
import pcgen.gui.utils.BrowserLauncher;
import pcgen.gui.utils.GuiFacade;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.JOpenRecentMenu;
import pcgen.gui.utils.LinkableHtmlMessage;
import pcgen.gui.utils.Utility;
import pcgen.io.PCGIOHandler;
import pcgen.util.FOPResourceChecker;
import pcgen.util.JEPResourceChecker;
import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

/**
 * Main screen of the application. Some of the custom JPanels created
 * here also help intialise, for example
 * {@link pcgen.gui.MainSource} also loads any
 * default campaigns.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 1.1 $
 */

public class PCGen_Frame1 extends JFrame implements gmgen.pluginmgr.GMBComponent
{
	static final long serialVersionUID = 1042236188732008819L;
	/** number of the first character tab */
	public static final int FIRST_CHAR_TAB = 1;

	private static PCGen_Frame1 inst;
	// Our automagic mouse cursor when wait > 3/4 second
	private static WaitCursorEventQueue waitQueue = new WaitCursorEventQueue(750);
	private static boolean usingWaitCursor = false;
	private pcGenGUI mainClass;
	private static CharacterInfo characterPane = null;

	private static LstConverter lstConverter = null;

	private static JLabel statusBar = new JLabel();

	/**
	 * Main tabbed panel of the application.
	 * The first tab contains the {@link #mainSource}
	 * ("Campaign") panel.
	 * Additional {@link CharacterInfo} panel tabs are added
	 * for each created character.
	 */
	private static JTabbedPane baseTabbedPane = new JTabbedPane();

	private final PcgFilter filter = new PcgFilter();
	private final PcpFilter partyFilter = new PcpFilter();
	private String partyFileName = "";  //used to keep track of last .pcp file used

	/**
	 * Contains the source screen.
	 *
	 * @see MainSource
	 */
	private MainSource mainSource = new MainSource();

	/** Menubar and toolbar actions. */
	FrameActionListener frameActionListener = new FrameActionListener(this);
	private MainPopupMenu mainPopupMenu = new MainPopupMenu(frameActionListener);
	private PCPopupMenu pcPopupMenu = new PCPopupMenu(frameActionListener);
	private PopupListener popupListener = new PopupListener(baseTabbedPane, mainPopupMenu, pcPopupMenu);

	/** Menubar for the main application. */
	public MenuItems menuBar = new MenuItems(); // NOT private

	/** ToolBar for the main application. */
	private PToolBar toolBar = PToolBar.createToolBar(this);

	// GUI stuff
	private JPanel panelSouth = new JPanel();
	private JPanel panelSouthEast = new JPanel();
	private JPanel panelSouthCenter = new JPanel();

	// the panel that contains all but the status bar,
	// this allows the tool bar to be floated and not
	// overlap with the status bar
	private JPanel panelMain = new JPanel();

	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();

	private FlowLayout flowLayout1 = new FlowLayout();
	private FlowLayout flowLayout2 = new FlowLayout();

	// Characters / core stuff
	private int newPCNameCount = 0;

	private KitSelector kitSelector = null;

	/**
	 * Screen initialization. Override close.
	 * <p/>
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 * and creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 */
	public PCGen_Frame1()
	{
		inst = this;
		Globals.setRootFrame(this);

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		try
		{
			jbInit();
		}
		catch (Exception e) //This is what jbInit throws...
		{
			Logging.errorPrint("jbInit", e);
		}

		GMBus.addToBus(this);
	}

	public MainSource getMainSource()
	{
		return mainSource;
	}

	public static PCGen_Frame1 getInst()
	{
		return inst;
	}

	public static JLabel getStatusBar()
	{
		return statusBar;
	}

	private void showToolBar()
	{
		toolBar.setVisible(SettingsHandler.isToolBarShown());
	}

	public void setGameModeTitle()
	{
		String modeName;
		pcgen.core.GameMode gameMode = SettingsHandler.getGame();
		if (gameMode == null)
		{
			modeName = "???";
		}
		else
		{
			modeName = gameMode.getName();
		}
		setTitle("PCGen v. " + PCGenProp.getVersionNumber()
			+ " - " + modeName + " Campaign");
	}

	public static JTabbedPane getBaseTabbedPane()
	{
		return baseTabbedPane;
	}

	public static CharacterInfo getCharacterPane()
	{
		return characterPane;
	}

	/**
	 * Real screen initialization is done here. Sets up all
	 * the window properties (icon, title, size).
	 * <p/>
	 * Creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 *
	 * @throws Exception Any Exception
	 */
	private void jbInit() throws Exception
	{
		IconUtilitities.maybeSetIcon(this, "PcgenIcon.gif");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(borderLayout1);
		setSize(new Dimension(700, 600));
		setGameModeTitle();

		setJMenuBar(menuBar);

		panelMain.add(toolBar, BorderLayout.NORTH);
		showToolBar();

		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		statusBar.setDoubleBuffered(true);
		statusBar.setMaximumSize(new Dimension(32767, 17));
		statusBar.setMinimumSize(new Dimension(620, 17));
		statusBar.setOpaque(true);
		statusBar.setPreferredSize(new Dimension(620, 17));
		statusBar.setHorizontalTextPosition(SwingConstants.LEFT);
		statusBar.setText(" ");
		baseTabbedPane.setTabPlacement(SettingsHandler.getTabPlacement());
		baseTabbedPane.setDoubleBuffered(true);
		baseTabbedPane.setMinimumSize(new Dimension(620, 350));
		baseTabbedPane.setPreferredSize(new Dimension(620, 350));
		baseTabbedPane.addMouseListener(popupListener);

		panelSouth.setLayout(borderLayout2);
		panelSouthEast.setLayout(flowLayout1);
		flowLayout1.setAlignment(FlowLayout.RIGHT);
		panelSouthCenter.setLayout(flowLayout2);
		flowLayout2.setAlignment(FlowLayout.LEFT);
		//toolsSetup.setLayout(borderLayout3);
		panelMain.add(baseTabbedPane, BorderLayout.CENTER);

		pcgen.core.GameMode game = SettingsHandler.getGame();
		if ((game != null) && (game.getTabShown(Constants.TAB_SOURCES)))
		{
			baseTabbedPane.addTab(game.getTabName(Constants.TAB_SOURCES), mainSource);
			baseTabbedPane.setToolTipTextAt(0, SettingsHandler.isToolTipTextShown() ? MainSource.SOURCE_MATERIALS_TAB : null);
		}

		this.getContentPane().add(panelSouth, BorderLayout.SOUTH);
		panelSouth.add(statusBar, BorderLayout.SOUTH);
		baseTabbedPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent c)
			{
				baseTabbedPane_changePanel();
			}
		});

		mainSource.addComponentListener(toolBar.getComponentListener());

		/*TODO: uncomment when we move to 1.4
		addWindowFocusListener(
			new java.awt.event.WindowFocusListener() {
				public void windowGainedFocus(java.awt.event.WindowEvent e) {
					stateUpdate(e);
				}
				public void windowLostFocus(java.awt.event.WindowEvent e) {
				}
			}
		);*/
	}

	public void setMainClass(pcGenGUI owner)
	{
		mainClass = owner;
	}

	/**
	 * Enable/disable the new item
	 */
	public void enableNew(boolean itemState)
	{
		menuBar.newItem.setEnabled(itemState);
		toolBar.newItem.setEnabled(itemState);
		mainPopupMenu.newItem.setEnabled(itemState);
		pcPopupMenu.getNewItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the open item
	 */
	public void enableOpen(boolean itemState)
	{
		menuBar.openItem.setEnabled(itemState);
		toolBar.openItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the close item
	 */
	private void enableClose(boolean itemState)
	{
		menuBar.closeItem.setEnabled(itemState);
		toolBar.closeItem.setEnabled(itemState);
		pcPopupMenu.getCloseItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the closeAll item
	 */
	private void enableCloseAll(boolean itemState)
	{
		menuBar.closeAllItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the save item
	 */
	private void enableSave(boolean itemState)
	{
		menuBar.saveItem.setEnabled(itemState);
		toolBar.saveItem.setEnabled(itemState);
		pcPopupMenu.getSaveItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the saveAs item
	 */
	private void enableSaveAs(boolean itemState)
	{
		menuBar.saveAsItem.setEnabled(itemState);
		pcPopupMenu.getSaveAsItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the saveAll item
	 */
	private void enableSaveAll(boolean itemState)
	{
		menuBar.saveAllItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the revertToSaved item
	 */
	private void enableRevertToSaved(boolean itemState)
	{
		menuBar.revertToSavedItem.setEnabled(itemState);
		pcPopupMenu.getRevertToSavedItem().setEnabled(itemState);
	}

	/**
	 * Enable/disable the printPreview item
	 */
	private void enablePrintPreview(boolean itemState)
	{
		menuBar.printPreviewItem.setEnabled(itemState);
		toolBar.printPreviewItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the print item
	 */
	private void enablePrint(boolean itemState)
	{
		menuBar.printItem.setEnabled(itemState);
		toolBar.printItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partySave item
	 */
	private void enablePartySave(boolean itemState)
	{
		menuBar.partySaveItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partySaveAs item
	 */
	private void enablePartySaveAs(boolean itemState)
	{
		menuBar.partySaveAsItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partyClose item
	 */
	private void enablePartyClose(boolean itemState)
	{
		menuBar.partyCloseItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the export menu
	 */
	private void enableExport(boolean itemState)
	{
		menuBar.exportMenu.setEnabled(itemState);
	}

	private void enableKit(boolean itemState)
	{
		menuBar.addKit.setEnabled(itemState);
		toolBar.addKit.setEnabled(itemState);
	}

	public void enableLstEditors(boolean itemState)
	{
		menuBar.listEditor.setEnabled(itemState);
	}

	/**
	 * Enable/disable all items intelligently.
	 * This method probably does too much.
	 */
	public static void enableDisableMenuItems()
	{
		if (!Globals.getUseGUI())
		{
			return;
		}
		PCGen_Frame1 frame = getInst();

		frame.enableNew(true);
		frame.enableOpen(false);
		frame.enableClose(false);
		frame.enableCloseAll(false);
		frame.enableSave(false);
		frame.enableSaveAs(false);
		frame.enableSaveAll(false);
		frame.enableRevertToSaved(false);
		frame.enableSave(false);
		frame.enablePrintPreview(false);
		frame.enablePrint(false);
		frame.enablePartySave(false);
		frame.enablePartySaveAs(false);
		frame.enableExport(false);
		frame.enableKit(false);
		frame.enableLstEditors(false);

		/* No campaigns open */
		if (!Globals.displayListsHappy())
		{
			// If you can autoload a campaign, you can open a PC
			if (SettingsHandler.isExpertGUI())
			{
				frame.enableNew(false);
			}

			// If you can autoload a campaign, you can open a PC
			if (SettingsHandler.isLoadCampaignsWithPC())
			{
				frame.enableOpen(true);
			}

			return;
		}

		frame.enableOpen(true);
		frame.enableLstEditors(true);

		PlayerCharacter aPC = frame.getCurrentPC();
		/* No PCs open */
		if (aPC == null)
		{
			return;
		}
		frame.enableClose(true);
		frame.enableSaveAs(true);
		frame.enablePrintPreview(true);
		frame.enablePrint(true);
		// How can you tell if a party file is clean?  XXX
		//frame.enablePartyClose(true);
		frame.enablePartySave(true);
		frame.enablePartySaveAs(true);
		frame.enableExport(true);
		frame.enableKit(true);

		List allPCs = Globals.getPCList();
		int pcCount = allPCs.size();
		if (pcCount > 1)
		{
			frame.enableCloseAll(true);
		}
		/* Changes to any PC? */
		for (int i = 0; i < pcCount; ++i)
		{
			if (((PlayerCharacter) allPCs.get(i)).isDirty())
			{
				frame.enableSaveAll(true);
				break;
			}
		}

		/* No changes to current PC */
		if (!aPC.isDirty())
		{
			return;
		}
		frame.enableSave(true);

		/* No saved file yet */
		if (!aPC.wasEverSaved())
		{
			return;
		}
		frame.enableRevertToSaved(true);
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
	private void exitItem_actionPerformed()
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
	private void handleQuit()
	{
		if (SettingsHandler.getLeftUpperCorner() == null)
		{
			SettingsHandler.setLeftUpperCorner(new Point(0, 0));
		}

		if (getState() != Frame.ICONIFIED)
		{
			SettingsHandler.getLeftUpperCorner().setLocation(getLocationOnScreen().getX(), getLocationOnScreen().getY());
		}

		int tabCount = baseTabbedPane.getTabCount();
		while (tabCount > FIRST_CHAR_TAB)
		{
			if (closePCTabAt(tabCount - 1, true))
			{
				tabCount = baseTabbedPane.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}
		GMBus.send(new WindowClosedMessage(this));

		SettingsHandler.storeFilterSettings(mainSource);

		// Need to (possibly) write customEquipment.lst
		if (SettingsHandler.getSaveCustomEquipment())
		{
			CustomData.writeCustomItems();
		}
		//
		// Clean up our temporary files
		//
		removeTemporaryFiles();

		SettingsHandler.writeOptionsProperties();

		this.dispose();
		System.exit(0);
	}

	private void removeTemporaryFiles()
	{
		final int cleanMode = SettingsHandler.getCleanupTempFiles();
		if (cleanMode < 0)
		{
			return;
		}

		final String aDirectory = SettingsHandler.getTempPath() + File.separator;
		new File(aDirectory).list(new FilenameFilter()
		{
			int myCleanMode = cleanMode;

			public boolean accept(File aFile, String aString)
			{
				try
				{
					if (aString.startsWith(Constants.s_TempFileName))
					{
						if (myCleanMode == 0)
						{
							if (!getUserChoice())
							{
								myCleanMode = -1;
							}
							else
							{
								myCleanMode = 1;
							}
						}
						if (myCleanMode > 0)
						{
							final File tf = new File(aFile, aString);
							tf.delete();
						}
					}
				}
				catch (Exception e)
				{
					Logging.errorPrint("removeTemporaryFiles", e);
				}
				return false;
			}
		});
	}

	private class AskUserPopup extends JDialog
	{
		static final long serialVersionUID = 1042236188732008819L;
		private boolean doDelete = false;

		public AskUserPopup(JFrame owner, String title, boolean modal)
		{
			super(owner, title, modal);
			initComponents();
			setLocationRelativeTo(owner);
		}

		private void initComponents()
		{
			final JButton btnYes = new JButton("Yes");
			final JButton btnNo = new JButton("No");
			final JPanel jPanel = new JPanel();
			final JCheckBox chkDontAsk = new JCheckBox("Don't ask again");
			jPanel.add(chkDontAsk);
			jPanel.add(btnYes);
			jPanel.add(btnNo);
			btnYes.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if (chkDontAsk.isSelected())
					{
						SettingsHandler.setCleanupTempFiles(1);
					}
					setDelete(true);
					dispose();
				}
			});
			btnNo.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					if (chkDontAsk.isSelected())
					{
						SettingsHandler.setCleanupTempFiles(-1);
					}
					dispose();
				}
			});
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(jPanel, BorderLayout.SOUTH);
			pack();
		}

		private void setDelete(boolean argDoDelete)
		{
			doDelete = argDoDelete;
		}

		public boolean getDelete()
		{
			return doDelete;
		}
	}

	private boolean getUserChoice()
	{
		try
		{
			final AskUserPopup dlg = new AskUserPopup(this, "Remove temporary files?", true);
			dlg.setVisible(true);
			return dlg.getDelete();
		}
		catch (Exception e)
		{
			Logging.errorPrint("Error in PCGen_Frame1::getUserChoice", e);
		}
		return false;
	}

	private void addPCTab(PlayerCharacter aPC)
	{
		if (characterPane == null)
		{
			characterPane = new CharacterInfo();
		}
		else
		{
			resetCharacterTabs();
		}

		characterPane.resetToSummaryTab();

		baseTabbedPane.addTab(aPC.getDisplayName(), null, characterPane, SettingsHandler.isToolTipTextShown() ? aPC.getFullDisplayName() : null);
		baseTabbedPane.setSelectedIndex(baseTabbedPane.getTabCount() - 1);
	}

	private void doNewItem()
	{
		if (!Globals.displayListsHappy())
		{
			GuiFacade.showMessageDialog(null, PropertyFactory.getString("in_newCharNoSources"), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			return;
		}

		final PlayerCharacter aPC = new PlayerCharacter();
		Globals.getPCList().add(aPC);
		++newPCNameCount;
		aPC.setName("New" + newPCNameCount);
		aPC.setDirty(true);

		addPCTab(aPC);
		GMBus.send(new PCLoadedMessage(this, aPC));
	}

	/**
	 * Creates a new {@link PlayerCharacter} model, and a corresponding
	 * {@link CharacterInfo} panel. The <code>PlayerCharacter</code> is
	 * added to the <code>Globals.getPCList()</code>. The <code>CharacterInfo</code>
	 * is added to both the <code>characterList</code>, and adds it to the main
	 * program frame, <code>baseTabbedPane</code> as a new tab.
	 */
	void newItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		// How does this work with the toolbar button?? --bko XXX
		toolBar.newItem.requestFocus();
		menuBar.newItem.requestFocus();

		doNewItem(); // selects new tab for us
	}

	/**
	 * Launches GMGen.
	 */
	void openGMGen_actionPerformed()
	{
		//TODO: remove the version check when pcgen goes to 1.4 java.
		StringTokenizer tok = new StringTokenizer(System.getProperty("java.version"), ".");
		String ver = tok.nextToken() + "." + tok.nextToken();
		try
		{
			double dver = Double.parseDouble(ver);
			if (dver > 1.3)
			{
				//end remove
				if (gmgen.GMGenSystem.inst == null)
				{
					new gmgen.GMGenSystem().show();
					for (int i = 0; i < Globals.getPCList().size(); i++)
					{
						GMBus.send(new PCLoadedMessage(this, (PlayerCharacter) Globals.getPCList().get(i)));
					}
				}
				else
				{
					gmgen.GMGenSystem.inst.show();
				}
				//remove
			}
			else
			{
				JOptionPane.showMessageDialog(this, "GMGen requires java 1.4 or later");
			}
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this, "GMGen requires java 1.4 or later");
		}
		//end remove
	}

	/**
	 * Load a party metafile, including campaign info and characters.
	 * Campaigns are loaded as from the Campaign tab.
	 * Characters are loaded into a new <code>PlayerCharacter</code> model
	 * and a corresponding <code>CharacterInfo</code> panel is created.
	 */
	void partyOpenItem_actionPerformed()
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Opening party...");
		statusBar.revalidate();

		// seize the focus to cause focus listeners to fire
		menuBar.partyOpenItem.requestFocus();

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());
		fc.setFileFilter(partyFilter);
		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			SettingsHandler.setPcgPath(file.getParentFile());
			loadPartyFromFile(file);
		}
		statusBar.setText(oldStatus);
	}

	boolean loadPartyFromFile(File file)
	{
		Party party = Party.makePartyFromFile(file);
		if (mainClass != null && party.load(this))
		{
			//if everything loaded successfully, then this file becomes the "current" party file
			partyFileName = file.getAbsolutePath();
			String displayName = party.getDisplayName();

			menuBar.openRecentPartyMenu.add(displayName, file);
			enablePartyClose(true);
		}
		else
		{
			//todo: i18n these messages
			GuiFacade.showMessageDialog(null, "Problems occurred while loading the party.", "Error", GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("PCGen_Frame1: Error in loadPartyFromFile");
			return false;
		}

		return true;
	}

	/**
	 * Saves a party metafile, including campaign info and characters.
	 */
	void partySaveItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		menuBar.partySaveItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.	 --bko
		saveAllPCs();
		partySaveItem(false);
	}

	void partySaveAsItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		menuBar.partySaveAsItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.	 --bko
		saveAllPCs();
		partySaveItem(true);
	}

	private void partySaveItem(boolean saveas)
	{
		boolean newParty = false;
		File prevFile;
		File file;
		if (partyFileName.length() == 0)
		{
			prevFile = new File(SettingsHandler.getPcgPath(), "Party" + Constants.s_PCGEN_PARTY_EXTENSION);
			partyFileName = prevFile.getAbsolutePath();
			newParty = true;
		}
		else
		{
			prevFile = new File(partyFileName);
		}

		file = prevFile;

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
					GuiFacade.showMessageDialog(null, "You cannot overwrite a directory with a party.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					return;
				}

				if (file.exists() && (newParty || prevFile == null || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose = GuiFacade.showConfirmDialog(this, "The file " + file.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + file.getName(), GuiFacade.YES_NO_OPTION);
					if (reallyClose != GuiFacade.YES_OPTION)
					{
						return;
					}
				}
			}
			else
			{
				return;
			}
		}

		try
		{
			partyFileName = file.getAbsolutePath();
			Party party = Party.makePartyFromFile(file);
			party.addAllOpenCharacters();
			party.save();
			enablePartyClose(true);
		}
		catch (IOException ex)
		{
			GuiFacade.showMessageDialog(null, "Could not save " + partyFileName, Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("Could not save" + partyFileName, ex);
			return;
		}
		menuBar.openRecentPartyMenu.add(partyFileName, file);
	}

	/**
	 * Close a party metafile, including campaign info and characters.
	 */
	void partyCloseItem_actionPerformed()
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Closing party...");
		statusBar.revalidate();

		// close all PCs
		closeAllPCs();

		// was closing all PCs successful?
		int tabCount = baseTabbedPane.getTabCount();
		if (tabCount == FIRST_CHAR_TAB)
		{

			// seize the focus to cause focus listeners to fire
			menuBar.partyCloseItem.requestFocus();
			enablePartyClose(false);

		}

		statusBar.setText(oldStatus);
	}

	/**
	 * Show the preferences pane.
	 */
	void preferencesItem_actionPerformed()
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Preferences...");
		//statusBar.updateUI();
		statusBar.revalidate();

		PreferencesDialog.show(this);

		statusBar.setText(oldStatus);
	}

	void addKit_actionPerformed()
	{
		toolBar.addKit.requestFocus();
		menuBar.addKit.requestFocus();

		PlayerCharacter aPC = getCurrentPC();
		if (aPC == null)
		{
			return;
		}

		final int currTab = baseTabbedPane.getSelectedIndex();
		if (currTab >= FIRST_CHAR_TAB)
		{
			final String kitFilter = getCharacterPane().getKitFilter();
			if (kitSelector == null)
			{
				kitSelector = new KitSelector(aPC);
			}
			kitSelector.show();
			kitSelector.setFilter(kitFilter);
		}
	}

	void newPopupItem_actionPerformed()
	{
		doNewItem();
	}

	void closePopupItem_actionPerformed()
	{
		int index = popupListener.getTabIndex();
		closePCTabAt(index, true);
		// Try not to jump the tab focus around
		baseTabbedPane.setSelectedIndex(index < baseTabbedPane.getTabCount() ? index : index - 1);
	}

	void savePopupItem_actionPerformed()
	{
		savePC(getPCForTabAt(popupListener.getTabIndex()), false);
	}

	void saveAsPopupItem_actionPerformed()
	{
		savePC(getPCForTabAt(popupListener.getTabIndex()), true);
	}


	private void moveTab(int oldIndex, int newIndex)
	{
		// Because the tabs are "fake", we need to reorder the
		// PCList in Globals, then simply refresh.
		List pcList = Globals.getPCList();
		PlayerCharacter aPC = (PlayerCharacter) pcList.get(oldIndex - FIRST_CHAR_TAB);
		pcList.remove(oldIndex - FIRST_CHAR_TAB);
		pcList.add(newIndex - FIRST_CHAR_TAB, aPC);
		Globals.setPCList(pcList);

		forceUpdate_PlayerTabs();
		baseTabbedPane.setSelectedIndex(newIndex);
	}

	void shiftLeftPopupItem_actionPerformed()
	{
		int index = popupListener.getTabIndex();
		moveTab(index, index == FIRST_CHAR_TAB ? (baseTabbedPane.getTabCount() - 1) : (index - 1));
	}

	void shiftRightPopupItem_actionPerformed()
	{
		int index = popupListener.getTabIndex();
		moveTab(index, index == (baseTabbedPane.getTabCount() - 1) ? FIRST_CHAR_TAB : (index + 1));
	}

	/**
	 * Load a character into a new <code>PlayerCharacter</code> model, and
	 * create a corresponding <code>CharacterInfo</code> panel.
	 *
	 * @see #newItem_actionPerformed
	 */
	void openItem_actionPerformed()
	{
		toolBar.openItem.requestFocus();
		menuBar.openItem.requestFocus();
		String oldStatus = statusBar.getText();
		statusBar.setText("Opening character.  Please wait...");
		//statusBar.updateUI();
		statusBar.revalidate();

		// seize the focus to cause focus listeners to fire
		menuBar.openItem.requestFocus();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(true);

		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File[] pcFiles = fc.getSelectedFiles();
			for (int i = 0; i < pcFiles.length; i++)
			{
				SettingsHandler.setPcgPath(pcFiles[i].getParentFile());
				fc.setMultiSelectionEnabled(false);
				loadPCFromFile(pcFiles[i]);
			}
			Globals.sortCampaigns();
		}
		statusBar.setText(oldStatus);
	}

	public boolean loadPCFromFile(File file)
	{
		PlayerCharacter aPC;
		Party party = Party.makeSingleCharacterParty(file);
		if (mainClass != null && party.load(null))
		{
			aPC = Globals.getCurrentPC();
			addPCTab(aPC);
			menuBar.openRecentPCMenu.add(aPC.getDisplayName(), file);
		}
		else
		{
			//todo: i18n these messages
			GuiFacade.showMessageDialog(null, "Unrecoverable problems occurred while loading the character.", "Error", GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("Error in loadPCFromFile");
			return false;
		}

		// Check to see if we should auto load companions
		if (aPC.getLoadCompanion() && !aPC.getFollowerList().isEmpty())
		{
			for (Iterator aF = aPC.getFollowerList().iterator(); aF.hasNext();)
			{
				Follower nPC = (Follower) aF.next();
				boolean aLoaded = false;
				// is this companion already loaded?
				for (Iterator p = Globals.getPCList().iterator(); p.hasNext();)
				{
					PlayerCharacter testPC = (PlayerCharacter) p.next();
					if (nPC.getFileName().equals(testPC.getFileName()))
					{
						aLoaded = true;
					}
				}
				if (!aLoaded)
				{
					// not loaded, so load this file
					final File aFile = new File(nPC.getFileName());
					Party followerParty = Party.makeSingleCharacterParty(aFile);
					if (mainClass != null && followerParty.load(null))
					{
						aPC = Globals.getCurrentPC();
						addPCTab(aPC);
					}
					else
					{
						//todo: i18n these messages
						GuiFacade.showMessageDialog(null, "Unrecoverable problems occurred while loading a companion or follower.", "Error", GuiFacade.ERROR_MESSAGE);
					}
				}
			}
		}
		GMBus.send(new PCLoadedMessage(this, aPC));
		return true;
	}

	private static PlayerCharacter getPCForTabAt(int index)
	{
		final int idx = index - FIRST_CHAR_TAB;
		if ((idx >= 0) && (idx < Globals.getPCList().size()))
		{
			return (PlayerCharacter) Globals.getPCList().get(idx);
		}
		else
		{
			return null;
		}
	}

	void updateByKludge()
	{
		// What is this bit of oddness?	 XXX
		final KitSelector ks = kitSelector;
		kitSelector = null;
		final int idx = baseTabbedPane.getSelectedIndex();
		baseTabbedPane.setSelectedIndex(0);
		baseTabbedPane.setSelectedIndex(idx);
		kitSelector = ks;
	}

	/**
	 * Shows different menus for different main tabs.
	 * When one of the first two panes (Campaign or GM) is shown,
	 * there are different menu items enabled (such as Save),
	 * than for character tabs.
	 */
	private void baseTabbedPane_changePanel()
	{
		// call requestFocus to prevent open edits
		// from applying to the wrong PC
		baseTabbedPane.requestFocus();

		enableDisableMenuItems();

		killKitSelector();

		final int currentPanel = baseTabbedPane.getSelectedIndex();
		if (currentPanel < FIRST_CHAR_TAB)
		{
			PToolBar.displayHelpPanel(false);
		}
		else
		{
			Globals.setCurrentPC(getCurrentPC());

			if (Globals.getPCList().size() > 1)
			{
				resetCharacterTabs();
			}
			baseTabbedPane.setComponentAt(currentPanel, characterPane);

			featList_Changed();

			final JTabbedPane aPane = (JTabbedPane) characterPane.getComponent(0);
			final int si = aPane.getSelectedIndex();
			if (si >= 0)
			{
				aPane.getComponent(si).requestFocus();
				// force component to get componentShown message
				final ComponentEvent ce = new ComponentEvent(aPane.getComponent(si), ComponentEvent.COMPONENT_SHOWN);
				aPane.getComponent(si).dispatchEvent(ce);
			}
		}
		// change focus to force new focus listeners to fire
		baseTabbedPane.requestFocus();
	}

	private void resetCharacterTabs()
	{
		if (characterPane == null)
		{
			return;
		}
		for (int i = FIRST_CHAR_TAB; i < baseTabbedPane.getTabCount(); ++i)
		{
			baseTabbedPane.setComponentAt(i, new JPanel());
		}
	}

	/**
	 * Closes the currently selected character tab.
	 */
	void closeItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.closeItem.requestFocus();
		final int currTab = baseTabbedPane.getSelectedIndex();
		if (currTab >= FIRST_CHAR_TAB)
		{
			// Check if reverting instead
			final String command = e.getActionCommand();
			if (!closePCTabAt(currTab, command.equals("file.close")))
			{
				return;
			}

			// Reset the "New1" counter if you close all tabs
			if (baseTabbedPane.getTabCount() <= FIRST_CHAR_TAB)
			{
				newPCNameCount = 0;
			}
		}
	}

	/**
	 * Close all open character tabs
	 */
	void closeAllItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		menuBar.closeAllItem.requestFocus();
		closeAllPCs();
	}

	/**
	 * What PC's tab is on top?
	 */
	private PlayerCharacter getCurrentPC()
	{
		final int currTab = baseTabbedPane.getSelectedIndex();

		if (currTab < FIRST_CHAR_TAB)
		{
			return null;
		}

		return getPCForTabAt(currTab);
	}

	/**
	 * Reverts to the previous version of this PC's saved file.
	 */
	public void revertToSavedItem_actionPerformed(ActionEvent e)
	{
		PlayerCharacter aPC = getCurrentPC();
		if (aPC == null)
		{
			return;
		}

		if (!aPC.isDirty())
		{
			// do nothing if clean
			return;
		}

		int reallyClose = GuiFacade.showConfirmDialog(this, aPC.getDisplayName() + " changed.	 Discard changes?", "Revert " + aPC.getDisplayName() + "?", GuiFacade.YES_NO_OPTION);
		if (reallyClose != GuiFacade.YES_OPTION)
		{
			return;
		}

		String oldStatus = statusBar.getText();
		statusBar.setText("Reverting character to saved...");
		statusBar.revalidate();

		// seize the focus to cause focus listeners to fire
		menuBar.revertToSavedItem.requestFocus();

		String fileName = aPC.getFileName(); // full path
		if (fileName.equals(""))
		{
			statusBar.setText(oldStatus);
			return;
		}

		closeItem_actionPerformed(e);

		File pcFile = new File(fileName);
		if (pcFile.exists())
		{
			loadPCFromFile(pcFile);
		}

		statusBar.setText(oldStatus);
	}

	public void closeAllPCs()
	{
		int tabCount = baseTabbedPane.getTabCount();
		while (tabCount > FIRST_CHAR_TAB)
		{
			if (closePCTabAt(tabCount - 1, true))
			{
				tabCount = baseTabbedPane.getTabCount();
			}
			else
			{
				//Stop closing if user wants to save an unsaved tab.
				return;
			}
		}

		enableClose(false);
		enableCloseAll(false);
		enableSave(false);
		enableSaveAs(false);
		enableRevertToSaved(false);
		enablePartySave(false);
		enablePartySaveAs(false);
		enablePartyClose(false);
		enablePrintPreview(false);
		enablePrint(false);
		enableExport(false);
		enableKit(false);
		newPCNameCount = 0;
	}

	/**
	 * Saves the character corresponding to the
	 * currently selected tab. The current character is
	 * worked out by taking the <code>(tab position - FIRST_CHAR_TAB)</code>,
	 * and taking the corresponding character from the
	 * <code>Globals.getPCList()</code>.
	 */
	void saveItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveItem.requestFocus();
		final PlayerCharacter aPC = getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		savePC(aPC, false);
	}

	/**
	 * Saves the character corresponding to the
	 * currently selected tab. The current character is
	 * worked out by taking the <code>(tab position - FIRST_CHAR_TAB)</code>,
	 * and taking the corresponding character from the
	 * <code>Globals.getPCList()</code>.
	 */
	void saveAsItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveAsItem.requestFocus();
		final PlayerCharacter aPC = getCurrentPC();
		if (aPC == null)
		{
			return;
		}
		savePC(aPC, true);
	}

	/**
	 * Property change listener for the event "selected file
	 * changed".  Ensures that the filename doesn't get changed
	 * when a directory is selected.
	 *
	 * @author Dmitry Jemerov <yole@spb.cityline.ru>
	 */

	static final class FilenameChangeListener implements PropertyChangeListener
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
			{
				onSelectedFileChange(evt);
			}
			else if (propName.equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY))
			{
				onDirectoryChange();
			}
		}

		private void onDirectoryChange()
		{
			fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), lastSelName));
		}

		private void onSelectedFileChange(PropertyChangeEvent evt)
		{
			File newSelFile = (File) evt.getNewValue();
			if (newSelFile != null && !newSelFile.isDirectory())
			{
				lastSelName = newSelFile.getName();
			}
		}
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's <code>save</code> method.
	 *
	 * @param aPC    The PlayerCharacter to save
	 * @param saveas boolean if <code>true</code>, ask for file name
	 * @return <code>true</code> if saved; <code>false</code> if saveas cancelled
	 */
	public boolean savePC(PlayerCharacter aPC, boolean saveas)
	{
		boolean newPC = false;
		File prevFile, file = null;
		String aPCFileName = aPC.getFileName();

		if (aPCFileName.equals(""))
		{
			prevFile = new File(SettingsHandler.getPcgPath(), aPC.getDisplayName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
			aPCFileName = prevFile.getAbsolutePath();
			newPC = true;
		}
		else
		{
			prevFile = new File(aPCFileName);
		}

		if (saveas || newPC)
		{
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(filter);
			fc.setSelectedFile(prevFile);
			FilenameChangeListener listener = new FilenameChangeListener(aPCFileName, fc);

			fc.addPropertyChangeListener(listener);
			int returnVal = fc.showSaveDialog(PCGen_Frame1.this);
			fc.removePropertyChangeListener(listener);

			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				file = fc.getSelectedFile();

				if (!file.getName().endsWith(Constants.s_PCGEN_CHARACTER_EXTENSION))
				{
					file = new File(file.getParent(), file.getName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
				}

				if (file.isDirectory())
				{
					GuiFacade.showMessageDialog(null, "You cannot overwrite a directory with a character.", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					return false;
				}

				if (file.exists() && (newPC || prevFile == null || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose = GuiFacade.showConfirmDialog(this, "The file " + file.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + file.getName(), GuiFacade.YES_NO_OPTION);

					if (reallyClose != GuiFacade.YES_OPTION)
					{
						return false;
					}
				}
				aPC.setFileName(file.getAbsolutePath());
			}
			else // not saving
			{
				return false;
			}
		}

		else // simple save
		{
			file = prevFile;
		}

		try
		{
			(new PCGIOHandler()).write(aPC, file.getAbsolutePath());

			SettingsHandler.setPcgPath(file.getParentFile());
		}
		catch (Exception ex)
		{
			GuiFacade.showMessageDialog(null, "Could not save " + aPC.getDisplayName(), Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
			Logging.errorPrint("Could not save " + aPC.getDisplayName(), ex);
			return false;
		}

		menuBar.openRecentPCMenu.add(aPC.getDisplayName(), file);
		return true;
	}

	private void saveAllPCs()
	{
		for (int i = 0, x = Globals.getPCList().size(); i < x; ++i)
		{
			PlayerCharacter aPC = (PlayerCharacter) Globals.getPCList().get(i);
			if (aPC.isDirty())
			{
				savePC(aPC, false);
			}
		}
	}

	/**
	 * Saves all open characters. Iterates through
	 * <code>Globals.getPCList()</code> (not the tabs) and runs
	 * <code>savePC</code> for each one.
	 */
	void saveAllItem_actionPerformed()
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveAllItem.requestFocus();
		saveAllPCs();
	}

	public String[] getOpenRecentPCs()
	{
		return menuBar.openRecentPCMenu.getEntriesAsStrings();
	}

	public String[] getOpenRecentParties()
	{
		return menuBar.openRecentPartyMenu.getEntriesAsStrings();
	}

	public void setOpenRecentPCs(String[] strings)
	{
		menuBar.openRecentPCMenu.setEntriesAsStrings(strings);
	}

	public void setOpenRecentParties(String[] strings)
	{
		menuBar.openRecentPartyMenu.setEntriesAsStrings(strings);
	}

	private void disposePCTabAt(int index)
	{
		final PlayerCharacter oldPC = getPCForTabAt(index);
		Globals.setCurrentPC(oldPC);
		int newIndex = (index == (baseTabbedPane.getTabCount() - 1) ? (index - 1) : index);

		//This should dispose of the character objects.
		baseTabbedPane.removeTabAt(index);
		Globals.getPCList().remove(index - FIRST_CHAR_TAB);
		// Go to the source tab, not the dm tools, if no pc tabs
		baseTabbedPane.setSelectedIndex(newIndex == (FIRST_CHAR_TAB - 1) ? 0 : newIndex);
		//
		// Need to fire this manually
		//
		if (index == newIndex)
		{
			baseTabbedPane_changePanel();
		}

		// This will free up resources, which are locked
		//PlayerCharacter.dispose();

		killKitSelector();

		// now set the PC to something else
		final PlayerCharacter aPC = getPCForTabAt(newIndex);
		Globals.setCurrentPC(aPC);
	}

	/**
	 * Close a tab by tab number, not pc number.
	 *
	 * @param index     Tab the character is on (not PC number)
	 * @param isClosing boolean <code>true</code> if closing, <code>false</code> if reverting
	 * @return <code>true</code> if closed; <code>false</code> if still open
	 */
	private boolean closePCTabAt(int index, boolean isClosing)
	{
		boolean bSave = true;
		final PlayerCharacter aPC = getPCForTabAt(index);
		Globals.setCurrentPC(aPC);
		if ((aPC != null) && (aPC.isDirty()))
		{
			int reallyClose = GuiFacade.YES_OPTION;

			if (isClosing)
			{
				reallyClose = GuiFacade.showConfirmDialog(this, aPC.getDisplayName() + " changed.  Save changes before closing?", "Save " + aPC.getDisplayName() + " before closing?", GuiFacade.YES_NO_CANCEL_OPTION);
			}
			else // reverting
			{
				bSave = false;
			}

			if (reallyClose == GuiFacade.CANCEL_OPTION
				|| reallyClose == GuiFacade.CLOSED_OPTION)
			{
				return false; // don't quit/close
			}

			else if (reallyClose == GuiFacade.NO_OPTION)
			{
				bSave = false;
			}
		}
		else
		{
			bSave = false;
		}

		// make sure that all the spell tables are reset
		// for when the next character get's loaded/viewed
		forceUpdate_InfoSpells();

		if (index >= FIRST_CHAR_TAB)
		{
			// save filter settings
			characterPane.storeFilterSettings();

			if (bSave)
			{
				// Quick hack: blank filename means never saved before
				final String fileName = aPC.getFileName();
				if (!savePC(aPC, fileName.equals("")))
				{
					return false;
				}
			}
		}
		GMBus.send(new PCClosedMessage(this, aPC));

		disposePCTabAt(index);

		return true;
	}

	private static void setTabName(int index, String aName)
	{
		getBaseTabbedPane().setTitleAt(index, aName);
	}

	void printPreviewItem_actionPerformed()
	{
		// show the preview in the browser
		Utility.previewInBrowser();
	}

	private void warnAboutMissingResource()
	{
		new LinkableHtmlMessage(this, FOPResourceChecker.getMissingResourceMessage(), Constants.s_APPNAME).show();
	}

	private void checkResources()
	{

		if ((JEPResourceChecker.getMissingResourceCount() != 0))
		{
			new LinkableHtmlMessage(this, JEPResourceChecker.getMissingResourceMessage(), Constants.s_APPNAME).show();
		}
	}

	void printItem_actionPerformed()
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
		menuBar.printFrame.show();
	}

	public void exportToStandardItem_actionPerformed()
	{
		if (menuBar.exportPopup == null)
		{
			menuBar.exportPopup = new ExportPopup(baseTabbedPane);
		}
		menuBar.exportPopup.setCurrentPCSelectionByTab();
	}

	public void exportToPDFItem_actionPerformed()
	{
		if (!menuBar.enablePDF)
		{
			warnAboutMissingResource();
			return;
		}

		if (menuBar.exportPDFPopup == null)
		{
			menuBar.exportPDFPopup = new ExportPDFPopup(baseTabbedPane);
		}
		menuBar.exportPDFPopup.setCurrentPCSelectionByTab();
	}

	/**
	 * update/restore filter settings from globally saved settings
	 * <p/>
	 * <br>author: Thomas Behr 24-02-02
	 *
	 * @param filterableName the name of the Filterable;<br>
	 *                       if <code>null</code> then filters for all
	 *                       Filterables will be updated
	 */
	public static void restoreFilterSettings(String filterableName)
	{
		if (characterPane != null)
		{
			characterPane.restoreFilterSettings(filterableName);
		}
		else
		{
			FilterFactory.clearFilterCache();
		}
	}

	/**
	 * Accessor to get selected Filterable component of active tab
	 * <p/>
	 * <br>author: Thomas Behr
	 */
	public static Filterable getCurrentFilterable()
	{
		int index = getBaseTabbedPane().getSelectedIndex();
		if (index == 0)
		{
			return (Filterable) getBaseTabbedPane().getComponentAt(index);
		}
		else if (index >= FIRST_CHAR_TAB)
		{
			return characterPane.getSelectedFilterable();
		}

		return null;
	}

	/**
	 * Accessor to get CharacterInfo component of active tab.
	 * This is especially needed to change/update a character's name.
	 */
	public static CharacterInfo getCurrentCharacterInfo()
	{
		int index = getBaseTabbedPane().getSelectedIndex();
		if (index >= FIRST_CHAR_TAB)
		{
			/**
			 * I would have preferred code like this
			 *    return (CharacterInfo) characterList.elementAt(index - FIRST_CHAR_TAB);
			 * but characterList is no class field (i.e. not static),
			 * so I use the static JTabbedPane.
			 * Maybe this whole method should not be static,
			 * but then we will be in need of a method to access the actual
			 * instance of PCGen_Frame1, so that NameGUI can access the
			 * current CharacterInfo for setting a new (random) name.
			 *
			 * author: Thomas Behr 20-12-01
			 */
			return characterPane;
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
	 * <p/>
	 * The File menus is created internally.
	 * Items in the File menu call methods of this class
	 * such as <code>newItem_actionPerformed</code> to
	 * process the click events.
	 * <p/>
	 * The Debug and Help menus are also created here,
	 * and load the {@link DebugFrame} and {@link AboutFrame}
	 * respectively.
	 * <p/>
	 * The {@link Options}
	 * and {@link GameModes} are all created externally.
	 */
	final class MenuItems extends JMenuBar
	{
		static final long serialVersionUID = 1042236188732008819L;
		JOpenRecentMenu openRecentPCMenu;
		JMenu importMenu;
		JMenu exportMenu;
		JMenu helpMenu;
		JMenu filtersMenu;
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
		JMenuItem preferencesItem;
		JMenuItem addKit;
		JMenuItem listEditor;

		/** Instantiated popup frame {@link AboutFrame}. */
		AboutFrame aboutFrame = null;

		/** Instantiated popup frame {@link DebugFrame}. */
		DebugFrame debugFrame = null;

		/** Instantiated popup frame {@link PrintFrame}. */
		PrintFrame printFrame = null;

		void checkPrintFrame()
		{
			if (printFrame == null)
			{
				printFrame = new MenuItems.PrintFrame();
			}
		}

		/** Instantiated popup frame {@link ExportPopup}. */
		public ExportPopup exportPopup = null;

		/** Instantiated popup frame {@link ExportPDFPopup}. */
		ExportPDFPopup exportPDFPopup = null;

		JCheckBoxMenuItem debugMode;

		private boolean enablePDF;

		public void separateHelpMenu(boolean b)
		{
			if (helpMenu == null) // broken!
			{
				throw new IllegalStateException();
			}
			int i = getComponentIndex(helpMenu);
			if (i == -1) // not found!
			{
				throw new IllegalStateException();
			}
			Object o = this.getComponent(i - 1);
			// If help menu is preceded by a menu, it isn't the
			// glue; otherwise, it's the horizontal glue.
			boolean hasGlue = !(o instanceof JMenu);

			if (b && hasGlue)
			{
				return;
			}
			if (!b && !hasGlue)
			{
				return;
			}

			if (b)
			{
				this.add(Box.createHorizontalGlue(), i);
			}
			else
			{
				this.remove(i - 1);
			}
		}

		public MenuItems()
		{

			// check for resources needed by FOP
			enablePDF = (FOPResourceChecker.getMissingResourceCount() == 0);
			checkResources();

			//FileMenu

			JMenu fileMenu = Utility.createMenu("mnuFile", null, true);

			newItem = Utility.createMenuItem("mnuFileNew", frameActionListener.newActionListener, "file.new", "shortcut N", "New16.gif", false);
			fileMenu.add(newItem);

			openItem = Utility.createMenuItem("mnuFileOpen", frameActionListener.openActionListener, "file.open", "shortcut O", "Open16.gif", true);
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

			closeItem = Utility.createMenuItem("mnuFileClose", frameActionListener.closeActionListener, "file.close", "shortcut W", "Close16.gif", false);
			fileMenu.add(closeItem);

			closeAllItem = Utility.createMenuItem("mnuFileCloseAll", frameActionListener.closeAllActionListener, "file.closeall", null, "CloseAll16.gif", false);
			// Special so that Close A_l_l, not C_l_ose All
			//closeAllItem.setDisplayedMnemonicIndex(7); // JDK 1.4
			fileMenu.add(closeAllItem);

			saveItem = Utility.createMenuItem("mnuFileSave", frameActionListener.saveActionListener, "file.save", "shortcut S", "Save16.gif", false);
			fileMenu.add(saveItem);

			saveAsItem = Utility.createMenuItem("mnuFileSaveAs", frameActionListener.saveAsActionListener, "file.saveas", "shift-shortcut S", "SaveAs16.gif", false);
			// Special so that Save _A_s..., not S_a_ve As...
			//saveAsItem.setDisplayedMnemonicIndex(5); // JDK 1.4
			fileMenu.add(saveAsItem);

			saveAllItem = Utility.createMenuItem("mnuFileSaveAll", frameActionListener.saveAllActionListener, "file.saveall", null, "SaveAll16.gif", false);
			fileMenu.add(saveAllItem);

			revertToSavedItem = Utility.createMenuItem("mnuFileRevertToSaved", frameActionListener.revertToSavedActionListener, "file.reverttosaved", "shortcut R", null, false);
			fileMenu.add(revertToSavedItem);

			fileMenu.addSeparator();

			JMenu partyMenu = Utility.createMenu("mnuFileParty", null, true);
			fileMenu.add(partyMenu);

			partyOpenItem = Utility.createMenuItem("mnuFilePartyOpen", frameActionListener.partyOpenActionListener, "file.party.open", null, "Open16.gif", true);
			partyMenu.add(partyOpenItem);

			openRecentPartyMenu = new JOpenRecentMenu(new JOpenRecentMenu.OpenRecentCallback()
			{
				public void openRecentPerformed(ActionEvent e, File file)
				{
					loadPartyFromFile(file);
				}
			});
			partyMenu.add(openRecentPartyMenu);

			partyCloseItem = Utility.createMenuItem("mnuFilePartyClose", frameActionListener.partyCloseActionListener, "file.party.close", null, "Close16.gif", false);
			partyMenu.addSeparator();

			partyMenu.add(partyCloseItem);

			partySaveItem = Utility.createMenuItem("mnuFilePartySave", frameActionListener.partySaveActionListener, "file.party.save", null, "Save16.gif", false);
			partyMenu.add(partySaveItem);

			partySaveAsItem = Utility.createMenuItem("mnuFilePartySaveAs", frameActionListener.partySaveAsActionListener, "file.party.saveas", null, "SaveAs16.gif", false);
			partyMenu.add(partySaveAsItem);

			fileMenu.addSeparator();

			printPreviewItem = Utility.createMenuItem("mnuFilePrintPreview", frameActionListener.printPreviewActionListener, "file.printpreview", null, "PrintPreview16.gif", false);
			fileMenu.add(printPreviewItem);

			printItem = Utility.createMenuItem("mnuFilePrint", frameActionListener.printActionListener, "file.print", "shortcut P", "Print16.gif", false);
			fileMenu.add(printItem);

			fileMenu.addSeparator();

			importMenu = Utility.createMenu("Import", 'I', "Import from other file formats", "Import16.gif", true);
			// Do not add until we get some formats to
			// import.  --bko XXX
			//fileMenu.add(importMenu);

			exportMenu = Utility.createMenu("mnuFileExport", "Export16.gif", false);
			fileMenu.add(exportMenu);

			exportItem = Utility.createMenuItem("mnuFileExportStandard", frameActionListener.exportToStandardActionListener, "file.export.standard", null, null, true);
			exportMenu.add(exportItem);

			/**
			 * changed this, so a warning will popopup,
			 * if user tries to print without having the needed
			 * libraries installed
			 *
			 * author: Thomas Behr 03-01-02
			 */
			exportPDFItem = Utility.createMenuItem("mnuFileExportPDF", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (enablePDF)
					{
						if (exportPDFPopup == null)
						{
							exportPDFPopup = new ExportPDFPopup(baseTabbedPane);
						}
						exportPDFPopup.setCurrentPCSelectionByTab();
					}
					else
					{
						warnAboutMissingResource();
					}
				}
			}, "file.export.pdf", null, null, true);
			exportMenu.add(exportPDFItem);

			fileMenu.addSeparator();

			addKit = Utility.createMenuItem("mnuFileAddKit", frameActionListener.addKitActionListener, "assign.kit", "shortcut K", "Information16.gif", false);
			fileMenu.add(addKit);

			fileMenu.addSeparator();

			exitItem = Utility.createMenuItem("mnuFileExit", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PCGen_Frame1.this.exitItem_actionPerformed();
				}
			}, "file.exit", "shortcut Q", null, true);
			fileMenu.add(exitItem);
			MenuItems.this.add(fileMenu);

			//Options Menu
			Options optionMenu = new Options();
			MenuItems.this.add(optionMenu);

			//Tools Menu
			JMenu toolsMenu = Utility.createMenu("mnuTools", "wrench.gif", true);
			MenuItems.this.add(toolsMenu);

			JMenuItem converterItem = Utility.createMenuItem("mnuToolsLstConverter", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String basePath = null;

					if (lstConverter == null)
					{
						if (SettingsHandler.getPccFilesLocation() != null)
						{
							basePath = SettingsHandler.getPccFilesLocation().toString();
						}
					}
					else
					{
						basePath = lstConverter.getBasePath();
					}

					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setDialogTitle("Select base directory to convert");
					if (System.getProperty("os.name").startsWith("Mac OS"))
					{
						// On MacOS X, do not traverse file bundles
						fc.putClientProperty("JFileChooser.appBundleIsTraversable", "never");
					}
					if (basePath != null)
					{
						final File baseFile = new File(basePath);
						fc.setCurrentDirectory(baseFile.getParentFile());
						fc.setSelectedFile(baseFile);
					}
					final int returnVal = fc.showOpenDialog(getParent().getParent()); //ugly, but it works
					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						final File file = fc.getSelectedFile();
						if ((lstConverter == null) || basePath == null || !basePath.equals(file.toString()))
						{
							lstConverter = new LstConverter(file.toString());
						}
						lstConverter.show();
					}
				}
			}, "tools.converter", null, "wrench.gif", true);
			toolsMenu.add(converterItem);

			filtersMenu = Utility.createMenu("mnuToolsFilters", "Zoom16.gif", true);
			toolsMenu.add(filtersMenu);

			JMenuItem openFiltersItem = Utility.createMenuItem("mnuToolsFiltersOpen", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterSelectDialog();
				}
			}, "tools.filters.open", null, "Zoom16.gif", true);
			filtersMenu.add(openFiltersItem);

			JMenuItem clearFiltersItem = Utility.createMenuItem("mnuToolsFiltersClear", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.clearSelectedFiltersForSelectedFilterable();
				}
			}, "tools.filters.clear", null, "RemoveZoom16.gif", true);
			filtersMenu.add(clearFiltersItem);

			JMenuItem customFiltersItem = Utility.createMenuItem("mnuToolsFiltersCustom", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterCustomDialog();
				}
			}, "tools.filters.custom", null, "CustomZoom16.gif", true);
			filtersMenu.add(customFiltersItem);

			JMenuItem editFiltersItem = Utility.createMenuItem("mnuToolsFiltersEdit", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterEditorDialog();
				}
			}, "tools.filters.edit", null, "EditZoom16.gif", true);
			filtersMenu.add(editFiltersItem);

			toolsMenu.addSeparator();

			JMenuItem gmgenItem = Utility.createMenuItem("mnuToolsGMGen", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					openGMGen_actionPerformed();
				}
			}, "tools.gmgen", null, "gmgen_icon.png", true);
			toolsMenu.add(gmgenItem);

			toolsMenu.addSeparator();
			//
			// List Editors
			//
			listEditor = Utility.createMenuItem("mnuToolsListEditors", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new LstEditorMain().show();
				}
			}, "tools.editors", null, null, true);
			toolsMenu.add(listEditor);



			//Debug Menu
			JMenu debugMenu = Utility.createMenu("mnuDebug", null, true);

			debugMode = new JCheckBoxMenuItem();
			debugMode.setText(PropertyFactory.getString("in_mnuDebugMode"));
			debugMode.setMnemonic(PropertyFactory.getMnemonic("in_mn_mnuDebugMode"));
			Utility.setDescription(debugMode, PropertyFactory.getString("in_mnuDebugModeTip"));
			debugMode.setSelected(Logging.isDebugMode());
			debugMode.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Logging.setDebugMode(debugMode.isSelected());
					if (exportPopup != null)
					{
						exportPopup.refreshTemplates();
					}
				}
			});
			debugMenu.add(debugMode);

			JMenuItem consoleMenuItem = Utility.createMenuItem("mnuDebugConsole", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (debugFrame == null)
					{
						debugFrame = new DebugFrame();
					}
					debugFrame.setVisible(true);
				}
			}, "debug.console", null, null, true);
			debugMenu.add(consoleMenuItem);

			MenuItems.this.add(debugMenu);

			//Help Menu
			helpMenu = Utility.createMenu("mnuHelp", "Help16.gif", true);

			JMenuItem contextHelpItem = Utility.createMenuItem("mnuHelpContext", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PToolBar.displayHelpPanel(true);
				}
			}, "help.context", null, "ContextualHelp16.gif", true);
			helpMenu.add(contextHelpItem);

			JMenuItem docsItem = Utility.createMenuItem("mnuHelpDocumentation", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new DocsFrame();
				}
			}, "help.docs", "F1", "Help16.gif", true);
			helpMenu.add(docsItem);

			helpMenu.addSeparator();

			JMenuItem oglItem = Utility.createMenuItem("mnuHelpOGL", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					pcGenGUI.showLicense();
				}
			}, "help.ogl", null, null, true);
			helpMenu.add(oglItem);

/*			JMenuItem d20Item = Utility.createMenuItem("mnuHelpD20", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					pcGenGUI.showMandatoryD20Info();
				}
			}, "help.d20", null, null, true);
			helpMenu.add(d20Item);
*/

			JMenuItem todItem = Utility.createMenuItem("mnuHelpTipOfTheDay", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					pcGenGUI.showTipOfTheDay();
				}
			}, "help.tod", null, "TipOfTheDay16.gif", true);
			helpMenu.add(todItem);

			helpMenu.addSeparator();

			JMenuItem aboutItem = Utility.createMenuItem("mnuHelpAbout", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (aboutFrame == null)
					{
						aboutFrame = new AboutFrame();
					}
					aboutFrame.setVisible(true);
				}
			}, "help.about", null, "About16.gif", true);
			helpMenu.add(aboutItem);
			this.add(helpMenu);
			// Scootch the Help menu over to the right
			separateHelpMenu(!UIFactory.isWindowsUI());
		}

		/**
		 * Popup frame with about info
		 */
		final class AboutFrame extends PCGenPopup
		{
			public AboutFrame()
			{
				super("About PCGen", new MainAbout());
			}
		}

		/**
		 * Popup frame with debug console
		 */
		final class DebugFrame extends PCGenPopup
		{
			public DebugFrame()
			{
				super("Debug Console", new MainDebug());
			}
		}

		/**
		 * Popup frame with print options
		 * <p/>
		 * author: Thomas Behr 16-12-01
		 */
		final class PrintFrame extends PCGenPopup
		{
			MainPrint mainPrint = null;

			public PrintFrame()
			{
				super("Print a PC or Party");
				mainPrint = new MainPrint(this, MainPrint.PRINT_MODE);
				setPanel(mainPrint);
				pack();
				setVisible(true);
			}

			public void setCurrentPCSelectionByTab()
			{
				if (mainPrint != null)
				{
					mainPrint.setCurrentPCSelection(baseTabbedPane.getSelectedIndex());
				}
			}
		}//end PrintFrame

		/**
		 * Pop up frame with Documentation.
		 */
		private final class DocsFrame extends JFrame
		{
			public DocsFrame()
			{
				try
				{
					BrowserLauncher.openURL(SettingsHandler.getPcgenDocsDir().getAbsolutePath() + File.separator + "index.html");
				}
				catch (IOException ex)
				{
					GuiFacade.showMessageDialog(null, "Could not open docs in external browser. " + "Have you set your default browser in the " + "Preference menu? Sorry...", Constants.s_APPNAME, GuiFacade.ERROR_MESSAGE);
					Logging.errorPrint("Could not open docs in external browser", ex);
				}
			}
		}

	}

	/**
	 * get the root frame that the given panel resides within
	 * @param child the panel to find the root frame of
	 * @return the root frame, or <code>null</code> if the given
	 * panel is not within a frame
	 */
	public static PCGen_Frame1 getRealParentFrame(JPanel child)
	{
		PCGen_Frame1 parent = (PCGen_Frame1)child.getTopLevelAncestor();
//		//There *is* a better way to do this. I just haven't thought of it yet.
//		PCGen_Frame1 parent = (PCGen_Frame1) child.getParent().getParent().getParent().getParent().getParent();
		return parent;
	}

	//
	// Update the available equipment list for all character's loaded, but only purchase
	// for the active one.
	//
	public void eqList_Changed(Equipment newEq, boolean purchase)
	{
		if (characterPane != null)
		{
			characterPane.infoInventory().getInfoGear().refreshAvailableList(newEq, purchase, true);
		}
	}

	public void hpTotal_Changed()
	{
		if (characterPane != null)
		{
			characterPane.infoClasses().updateHP();
			characterPane.infoSummary().updateHP();
		}
	}

	public void featList_Changed()
	{
		if (characterPane != null)
		{
			InfoFeats.forceUpdate();
		}
	}

	public static void forceUpdate_InfoAbilities()
	{
		if ((characterPane != null) && (characterPane.infoAbilities() != null))
		{
			characterPane.infoAbilities().updateCharacterInfo();
		}
	}

	public static void forceUpdate_InfoSummary()
	{
		if (characterPane != null)
		{
			InfoSummary.setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_PlayerTabs()
	{
		boolean tips = SettingsHandler.isToolTipTextShown();

		for (int i = FIRST_CHAR_TAB, x = baseTabbedPane.getTabCount(); i < x; ++i)
		{
			PlayerCharacter aPC = getPCForTabAt(i);
			setTabName(i, aPC.getDisplayName());
			baseTabbedPane.setToolTipTextAt(i, tips ? aPC.getFullDisplayName() : null);
		}
	}

	public static void forceUpdate_InfoRace()
	{
		if (characterPane != null)
		{
			characterPane.infoRace().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoClasses()
	{
		if (characterPane != null)
		{
			InfoClasses.setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoDomain()
	{
		if (characterPane != null)
		{
			InfoDomain.setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoFeats()
	{
		if (characterPane != null)
		{
			InfoFeats.setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoSkills()
	{
		if (characterPane != null)
		{
			InfoSkills.setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoSpells()
	{
		if (characterPane != null)
		{
			characterPane.infoSpells().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoInventory()
	{
		if (characterPane != null)
		{
			characterPane.infoInventory().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoDescription()
	{
		if (characterPane != null)
		{
			characterPane.infoDescription().setNeedsUpdate(true);
		}
	}

	public static void addMonsterHD(int direction)
	{
		if (characterPane != null)
		{
			characterPane.infoSummary().addMonsterHD(direction);
		}
	}

	public static void useWaitCursor(boolean b)
	{
		if (b)
		{
			if (!usingWaitCursor)
			{
				usingWaitCursor = true;
				Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQueue);
			}
		}
		else
		{
			if (usingWaitCursor)
			{
				try
				{
					waitQueue.doPop();
				}
				catch (EmptyStackException e)
				{
					//TODO: Should we really ignore this?
				}

				usingWaitCursor = false;
			}
		}

	}

	/**
	 * From http://www.javaworld.com/javaworld/javatips/jw-javatip87.html
	 */
	private static final class WaitCursorEventQueue extends EventQueue
	{
		private int delay;
		private WaitCursorTimer waitTimer;

		public WaitCursorEventQueue(int delay)
		{
			this.delay = delay;
			waitTimer = new WaitCursorTimer();
			waitTimer.setDaemon(true);
			waitTimer.start();
		}

		protected void dispatchEvent(AWTEvent event)
		{
			waitTimer.startTimer(event.getSource());
			try
			{
				super.dispatchEvent(event);
			}
			finally
			{
				waitTimer.stopTimer();
			}
		}

		private final class WaitCursorTimer extends Thread
		{
			private Object source;
			private Component parent;

			synchronized void startTimer(Object argSource)
			{
				this.source = argSource;
				this.notify();
			}

			synchronized void stopTimer()
			{
				if (parent == null)
				{
					interrupt();
				}
				else
				{
					parent.setCursor(null);
					parent = null;
				}
			}

			public synchronized void run()
			{
				while (true)
				{
					try
					{
						//wait for notification from
						//startTimer()
						this.wait();

						//wait for event processing to
						//reach the threshold, or
						//interruption from
						//stopTimer()
						this.wait(delay);

						if (source instanceof Component)
						{
							parent = SwingUtilities.getRoot((Component) source);
						}
						else if (source instanceof MenuComponent)
						{
							MenuContainer mParent = ((MenuComponent) source).getParent();
							if (mParent instanceof Component)
							{
								parent = SwingUtilities.getRoot((Component) mParent);
							}
						}

						if (parent != null && parent.isShowing())
						{
							parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						}
					}
					catch (InterruptedException ie)
					{
					}
				}
			}
		}

		public void doPop()
		{
			pop();
		}
	}

	/**
	 * Support for popup menus on player tabs.  This is too easy.
	 */
	static final class PopupListener extends MouseAdapter
	{
		JTabbedPane tabbedPane;
		MainPopupMenu mainPopupMenu;
		PCPopupMenu pcPopupMenu;
		int index;

		PopupListener(JTabbedPane tabbedPane, MainPopupMenu mainPopupMenu, PCPopupMenu pcPopupMenu)
		{
			this.tabbedPane = tabbedPane;
			this.mainPopupMenu = mainPopupMenu;
			this.pcPopupMenu = pcPopupMenu;
		}

		// Missing from JTabbedPane < 1.4
		private int indexAtLocation(int x, int y)
		{
			TabbedPaneUI ui = tabbedPane.getUI();
			if (ui != null)
			{
				return ui.tabForCoordinate(tabbedPane, x, y);
			}
			return -1;
		}

		public void mousePressed(MouseEvent e)
		{
			// Work-around: W32 returns false even on
			// right-mouse clicks
			if (!(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)))
			{
				return;
			}

			index = indexAtLocation(e.getX(), e.getY());
			// Clicked somewhere besides a tab
			if (index < 0)
			{
				return;
			}

			enableDisableMenuItems();

			if (index < FIRST_CHAR_TAB)
			{
				mainPopupMenu.show(e.getComponent(), e.getX(), e.getY());

				return;
			}

			int tabCount = tabbedPane.getTabCount();

			switch (tabCount)
			{
				case FIRST_CHAR_TAB + 1: // one PC tab only -- no shifting
					pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_NONE);
					break;
				case FIRST_CHAR_TAB + 2: // two PC tabs -- support swapping
					pcPopupMenu.setShiftType(index == 1 ? PCPopupMenu.SHIFT_RIGHT : PCPopupMenu.SHIFT_LEFT);
					break;
				default: // many PC tabs -- support cycling
					if (index == FIRST_CHAR_TAB)
					{
						pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_END_RIGHT);
					}
					else if (index == (tabCount - 1))
					{
						pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_LEFT_BEGINNING);
					}
					else
					{
						pcPopupMenu.setShiftType(PCPopupMenu.SHIFT_LEFT_RIGHT);
					}
			}

			pcPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		}

		public int getTabIndex()
		{
			return index;
		}
	}

	private void killKitSelector()
	{
		if (kitSelector != null)
		{
			if (kitSelector.isVisible())
			{
				kitSelector.closeDialog();
			}
			kitSelector = null;
		}
	}

	public void stateUpdate(EventObject e) {
		GMBus.send(new StateChangedMessage(this, null));
	}

	public void handleMessage(GMBMessage message)
	{
		if (message instanceof OpenPCGRequestMessage)
		{
			handleOpenPCGRequestMessage((OpenPCGRequestMessage)message);
		}
		// This should only be used until GMGen can use PCGen to generate it's
		// Random encounter beasties.
		else if (message instanceof InitHolderListSendMessage)
		{
			handleInitHolderListSendMessage((InitHolderListSendMessage)message);
		}
		else if(message instanceof StateChangedMessage) {
			handleStateChangedMessage((StateChangedMessage)message);
		}
	}

	private void handleOpenPCGRequestMessage(OpenPCGRequestMessage message)
	{
		File pcFile = message.getFile();
		if (pcFile.toString().endsWith(".pcg"))
		{
			loadPCFromFile(pcFile);
		}
		else if (pcFile.toString().endsWith(".pcp"))
		{
			loadPartyFromFile(pcFile);
		}
	}

	private void handleInitHolderListSendMessage(InitHolderListSendMessage message)
	{
		InitHolderList list = message.getInitHolderList();
		for (int i = 0; i < list.size(); i++)
		{
			InitHolder iH = (InitHolder) list.get(i);
			if (iH instanceof PcgCombatant)
			{
				PcgCombatant pcg = (PcgCombatant) iH;
				PlayerCharacter aPC = pcg.getPC();
				Globals.getPCList().add(aPC);
				aPC.setDirty(true);
				addPCTab(aPC);
			}
		}
	}

	private void handleStateChangedMessage(StateChangedMessage message)
	{
		forceUpdate_InfoAbilities();
		forceUpdate_InfoSummary();
		forceUpdate_PlayerTabs();
		forceUpdate_InfoRace();
		forceUpdate_InfoClasses();
		forceUpdate_InfoDomain();
		forceUpdate_InfoFeats();
		forceUpdate_InfoSkills();
		forceUpdate_InfoSpells();
		forceUpdate_InfoInventory();
		forceUpdate_InfoDescription();
	}
}
