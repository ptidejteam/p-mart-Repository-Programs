/*
 *  GMGenSystem.java - main class for GMGen
 *  Copyright (C) 2003 Devon Jones, Emily Smirle
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *  Created on May 24, 2003
 */
package gmgen;

import gmgen.gui.AboutBox;
import gmgen.gui.LogWindow;
import gmgen.gui.PreferencesDialog;
import gmgen.gui.PreferencesRootTreeNode;
import gmgen.gui.SplashScreen;
import gmgen.io.SimpleFileFilter;
import gmgen.pluginmgr.GMBComponent;
import gmgen.pluginmgr.GMBMessage;
import gmgen.pluginmgr.GMBus;
import gmgen.pluginmgr.PluginLoader;
import gmgen.pluginmgr.messages.ClipboardMessage;
import gmgen.pluginmgr.messages.FileOpenMessage;
import gmgen.pluginmgr.messages.HelpMenuItemAddMessage;
import gmgen.pluginmgr.messages.LoadMessage;
import gmgen.pluginmgr.messages.OpenPCGRequestMessage;
import gmgen.pluginmgr.messages.PCLoadedMessage;
import gmgen.pluginmgr.messages.PreferencesPanelAddMessage;
import gmgen.pluginmgr.messages.SaveMessage;
import gmgen.pluginmgr.messages.StateChangedMessage;
import gmgen.pluginmgr.messages.TabAddMessage;
import gmgen.pluginmgr.messages.ToolMenuItemAddMessage;
import gmgen.pluginmgr.messages.WindowClosedMessage;
import gmgen.util.LogUtilities;
import gmgen.util.MiscUtilities;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.EventObject;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

/**
 *  <code>GMGenSystem</code> is the main class of this application. This class
 *  holds the contoller of every section, and the menu bar.
 *
 *@author     Expires 2003
 *@created    May 30, 2003
 *@version    3.3
 *@since      GMGen 3.3
 */
public class GMGenSystem extends JFrame implements ChangeListener, MenuListener, ActionListener, GMBComponent {

	/**
	 *  holds an instance of the top window, so components and windows can get
	 *  Their parent frame
	 */
	public static GMGenSystem inst;

	/**
	 *  Default width for the GUI.
	 */
	public static final int WIDTH = 700;

	/**
	 *  Default height for the GUI.
	 */
	public static final int HEIGHT = 500;

	/**
	 *  Tree for the prefereneces dialog
	 */
	private PreferencesRootTreeNode rootNode = new PreferencesRootTreeNode();

	/**
	 *  Boolean true if this is a Macintosh systems
	 */
	public static boolean MAC_OS_X = (System.getProperty("os.name").equals("Mac OS X"));

	/**
	 *  The main menu bar.
	 */
	private JMenuBar systemMenu;

	/**
	 *  The file menu.
	 */
	private JMenu fileMenu;
	/**
	 *  The new menu item in the file menu.
	 */
	public JMenuItem newFileItem;
	/**
	 *  The open menu item in the file menu.
	 */
	public JMenuItem openFileItem;
	/**
	 *  The file menu separator.
	 */
	private JSeparator fileSeparator1;
	/**
	 *  The save menu item in the file menu.
	 */
	public JMenuItem saveFileItem;
	/**
	 *  The file menu separator.
	 */
	private JSeparator fileSeparator2;
	/**
	 *  The exit menu item in the file menu.
	 */
	private JMenuItem exitFileItem;

	/**
	 *  The edit menu.
	 */
	private JMenu editMenu;
	/**
	 *  The cut menu item in the edit menu.
	 */
	public JMenuItem cutEditItem;
	/**
	 *  The copy menu item in the edit menu.
	 */
	public JMenuItem copyEditItem;
	/**
	 *  The paste menu item in the edit menu.
	 */
	public JMenuItem pasteEditItem;
	/**
	 *  The file menu separator.
	 */
	private JSeparator editSeparator1;
	/**
	 *  The preferences menu item in the edit menu.
	 */
	public JMenuItem preferencesEditItem;

	/**
	 *  The help menu.
	 */
	private JMenu helpMenu;
	/**
	 *  The about menu item in the help menu.
	 */
	private JMenuItem aboutHelpItem;
	/**
	 *  The help menu separator.
	 */
	private JSeparator helpSeparator1;

	/**
	 *  The source loader section.
	 */
	//private SourceView sourceView;

	/**
	 *  The main <code>JPanel</code> view for the system.
	 */
	private GMGenSystemView theView;

	/**
	 *  The tools menu.
	 */
	private JMenu toolsMenu;

	/**
	 *  The tools menu separator.
	 */
	private JSeparator toolsSeparator1;

	/**
	 *  The version menu item in the tools menu.
	 */
	private JMenuItem versionToolsItem;

	/**
	 *  The log menu.
	 */
	private JMenu logMenu;

	/**
	 *  If log mode is on or off
	 */
	private JCheckBoxMenuItem logMode;

	/**
	 *  Bring up log window
	 */
	private JMenuItem logWindowItem;

	/**
	 *  Hold link to LogWindow
	 */
	private LogWindow logWin = null;

	/**
	 *  Plugin loader
	 */
	private PluginLoader pluginLoader;

	/**
	 *  SplashScreen
	 */
	private static SplashScreen splash;

	/**
	 *  Creates an instance of the main application. Does all the core
	 *  initialization
	 *
	 *@since    GMGen 3.3
	 */
	public GMGenSystem() {
		super("GMGen System");
		showSplashScreen();
		// Fixes for Mac OS X look-and-feel menu problems.
		// sk4p 12 Dec 2002
		if (MAC_OS_X) {
			System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
			System.setProperty("com.apple.mrj.application.live-resize", "false");
			System.setProperty("com.apple.macos.smallTabs", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "GMGen");
			macOSXRegistration();
			// Set up our application to respond to the Mac OS X application menu
		}

		inst = this;
		initLogger();
		createMenuBar();
		theView = new GMGenSystemView();
		GMBus.addToBus(this);
		pluginLoader = new PluginLoader(this);
		initComponents();
		initSettings();
		hideSplashScreen();
		GMBus.send(new StateChangedMessage(this, editMenu));
	}


	/**
	 * generic registration with the Mac OS X application menu.  Checks the platform, then attempts
	 * to register with the Apple EAWT.
	 * This method calls OSXAdapter.registerMacOSXApplication() and OSXAdapter.enablePrefs().
	 * See OSXAdapter.java for the signatures of these methods.
	 */
	public void macOSXRegistration() {
		if (MAC_OS_X) {
			try {
				Class osxAdapter = Class.forName("gmgen.util.OSXAdapter");
				Class[] defArgs = {GMGenSystem.class};
				Method registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", defArgs);
				if (registerMethod != null) {
					Object[] args = {this};
					registerMethod.invoke(osxAdapter, args);
				}
				// This is slightly gross.  to reflectively access methods with boolean args,
				// use "boolean.class", then pass a Boolean object in as the arg, which apparently
				// gets converted for you by the reflection system.
				defArgs[0] = boolean.class;
				Method prefsEnableMethod = osxAdapter.getDeclaredMethod("enablePrefs", defArgs);
				if (prefsEnableMethod != null) {
					Object[] args = {Boolean.TRUE};
					prefsEnableMethod.invoke(osxAdapter, args);
				}
			} catch (NoClassDefFoundError e) {
				// This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
				// because OSXAdapter extends ApplicationAdapter in its def
				System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (ClassNotFoundException e) {
				// This shouldn't be reached; if there's a problem with the OSXAdapter we should get the
				// above NoClassDefFoundError first.
				System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
			} catch (Exception e) {
				System.err.println("Exception while loading the OSXAdapter:");
				e.printStackTrace();
			}
		}
	}


	/**
	 *  Calls the appropriate methods depending on the actions that happened on the
	 *  GUI.
	 *
	 *@param  e  event that took place
	 *@since     GMGen 3.3
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == openFileItem) {
			GMBus.send(new FileOpenMessage(this));
		} else if (e.getSource() == exitFileItem) {
			GMBus.send(new WindowClosedMessage(this));
			//System.exit(0);
		} else if (e.getSource() == newFileItem) {
			GMBus.send(new LoadMessage(this));
		} else if (e.getSource() == saveFileItem) {
			GMBus.send(new SaveMessage(this));
		} else if (e.getSource() == cutEditItem) {
			GMBus.send(new ClipboardMessage(this, ClipboardMessage.CUT));
		} else if (e.getSource() == copyEditItem) {
			GMBus.send(new ClipboardMessage(this, ClipboardMessage.COPY));
		} else if (e.getSource() == pasteEditItem) {
			GMBus.send(new ClipboardMessage(this, ClipboardMessage.PASTE));
		/*} else if (e.getSource() == sourceView.getLoadButton()) {
			setTabsEnabled();
		} else if ((e.getSource() == sourceView.getUnloadAllButton()) || (e.getSource() == sourceView.getRemoveAllButton())) {
			setTabsDisabled();*/
		} else if (e.getSource() == logMode) {
			LogUtilities.inst().setLoggingOn(logMode.getState());
		} else if (e.getSource() == logWindowItem) {
			logwinAction();
		}
	}


	/**
	 *  method run to open a character/party file
	 *
	 *@since    GMGen 3.3
	 */
	private void handleOpenFile() {
		File defaultFile = SettingsHandler.getPcgPath();
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(defaultFile);
		String[] pcgs = new String[]{"pcg", "pcp"};
		SimpleFileFilter ff = new SimpleFileFilter(pcgs, "PCGen File");
		chooser.addChoosableFileFilter(ff);
		chooser.setFileFilter(ff);
		chooser.setMultiSelectionEnabled(true);
		java.awt.Cursor saveCursor = MiscUtilities.setBusyCursor(theView);
		int option = chooser.showOpenDialog(theView);
		if (option == JFileChooser.APPROVE_OPTION) {
			File[] pcFiles = chooser.getSelectedFiles();
			for (int i = 0; i < pcFiles.length; i++) {
				Logging.debugPrint("Opening file " + pcFiles[i].toString());
				SettingsHandler.setPcgPath(pcFiles[i].getParentFile());
				if (pcFiles[i].toString().endsWith(".pcg") || pcFiles[i].toString().endsWith(".pcp")) {
					GMBus.send(new OpenPCGRequestMessage(this, pcFiles[i], false));
				}
			}
			/*
			 *  loop through selected files
			 */
			// tell source view to refresh
			//sourceView.updateLoadedCampaignsUI();
			setTabsEnabled();
		}
		MiscUtilities.setCursor(theView, saveCursor);
	}
	// end handleOpenFile


	/**
	 *  function run to pop up a log window
	 *
	 *@since    GMGen 3.3
	 */
	private void logwinAction() {
		if (logWin == null) {
			logWin = new LogWindow();
		} else {
			logWin.setVisible(true);
			logWin.toFront();
		}
	}


	/**
	 *  Creates the MenuBar for the application
	 *
	 *@since    GMGen 3.3
	 */
	private void createMenuBar() {
		systemMenu = new JMenuBar();

		fileMenu = new JMenu();
		newFileItem = new JMenuItem();
		openFileItem = new JMenuItem();
		fileSeparator1 = new JSeparator();
		saveFileItem = new JMenuItem();
		fileSeparator2 = new JSeparator();
		exitFileItem = new JMenuItem();
		editMenu = new JMenu();
		cutEditItem = new JMenuItem();
		copyEditItem = new JMenuItem();
		pasteEditItem = new JMenuItem();
		editSeparator1 = new JSeparator();
		preferencesEditItem = new JMenuItem();

		toolsMenu = new JMenu();
		toolsSeparator1 = new JSeparator();
		versionToolsItem = new JMenuItem();

		logMenu = new JMenu();
		logMode = new JCheckBoxMenuItem("Log Mode", SettingsHandler.getGMGenOption("Logging.On", false));
		logWindowItem = new JMenuItem();

		helpMenu = new JMenu();
		helpSeparator1 = new JSeparator();
		aboutHelpItem = new JMenuItem();

		// FILE MENU
		fileMenu.setText("File");
		fileMenu.setMnemonic('F');
		fileMenu.addMenuListener(this);

		newFileItem.setMnemonic('N');
		newFileItem.setText("New");
		newFileItem.addActionListener(this);
		fileMenu.add(newFileItem);

		openFileItem.setMnemonic('O');
		openFileItem.setText("Open");
		fileMenu.add(openFileItem);
		openFileItem.addActionListener(this);

		fileMenu.add(fileSeparator1);

		saveFileItem.setMnemonic('S');
		saveFileItem.setText("Save");
		fileMenu.add(saveFileItem);
		saveFileItem.addActionListener(this);

		/**
		 *  Exit is quit on the Macintosh is in the application menu. See
		 *  macOSXRegistration()
		 */
		if (!MAC_OS_X) {
			fileMenu.add(fileSeparator2);

			exitFileItem.setMnemonic('x');
			exitFileItem.setText("Exit");
			fileMenu.add(exitFileItem);
			exitFileItem.addActionListener(this);
		}

		systemMenu.add(fileMenu);

		// EDIT MENU
		editMenu.setText("Edit");
		editMenu.setMnemonic('E');
		editMenu.addMenuListener(this);

		cutEditItem.setText("Cut");
		editMenu.add(cutEditItem);

		copyEditItem.setText("Copy");
		editMenu.add(copyEditItem);

		pasteEditItem.setText("Paste");
		editMenu.add(pasteEditItem);

		/**
		 *  Preferences... on the Macintosh is in the application menu. See
		 *  macOSXRegistration()
		 */
		if (!MAC_OS_X) {
			editMenu.add(editSeparator1);

			preferencesEditItem.setText("Preferences");
			editMenu.add(preferencesEditItem);
			preferencesEditItem.setEnabled(true);
			/* TODO: remove this comment when we can use 1.4

			ActionListener[] listenerArray = preferencesEditItem.getActionListeners();
			for (int i = 0; i < listenerArray.length; i++) {
				preferencesEditItem.removeActionListener(listenerArray[i]);
			}
			*/
			preferencesEditItem.addActionListener(
				new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						mPreferencesActionPerformed(evt);
					}
				});
		}

		systemMenu.add(editMenu);

		// TOOLS MENU
		toolsMenu.setText("Tools");
		toolsMenu.setMnemonic('T');
		toolsMenu.addMenuListener(this);

		versionToolsItem.setMnemonic('G');
		versionToolsItem.setText("Get Newest Version");
		toolsMenu.add(versionToolsItem);

		toolsMenu.add(toolsSeparator1);

		systemMenu.add(toolsMenu);

		// LOG MENU
		logMenu.setText("Log");
		logMenu.setMnemonic('L');

		logMode.setText("Log Mode");
		logMode.setMnemonic('M');
		logMenu.add(logMode);
		logMode.addActionListener(this);

		logWindowItem.setText("Log Window");
		logWindowItem.setMnemonic('W');
		logMenu.add(logWindowItem);
		logWindowItem.addActionListener(this);

		systemMenu.add(logMenu);

		// HELP MENU
		helpMenu.setText("Help");
		helpMenu.setMnemonic('H');

		// About on the Macintosh is in the application menu. See
		//  macOSXRegistration()
		if (!MAC_OS_X) {
			aboutHelpItem.setMnemonic('A');
			aboutHelpItem.setText("About");
			aboutHelpItem.addActionListener(
				new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						mAboutActionPerformed(evt);
					}
				});

			helpMenu.add(aboutHelpItem);

			helpMenu.add(helpSeparator1);

		}

		systemMenu.add(helpMenu);

		setJMenuBar(systemMenu);
		openFileItem.setEnabled(true);
		saveFileItem.setEnabled(false);
		newFileItem.setEnabled(false);
		cutEditItem.setEnabled(false);
		copyEditItem.setEnabled(false);
		pasteEditItem.setEnabled(false);
		preferencesEditItem.setEnabled(true);
		versionToolsItem.setEnabled(false);

		pack();
	}


	private void mAboutActionPerformed(java.awt.event.ActionEvent evt) {
		AboutBox dialog = new AboutBox(this, true, getVersion());
		dialog.setVisible(true);
	}


	private void mPreferencesActionPerformed(java.awt.event.ActionEvent evt) {
		PreferencesDialog dialog = new PreferencesDialog(this, true, rootNode);
		dialog.setVisible(true);
	}


	/**
	 *  Closes and exits the application cleanly.
	 *
	 *@param  evt  a window close event
	 *@since       GMGen 3.3
	 */
	private void exitForm(java.awt.event.WindowEvent evt) {
		this.hide();
		//exit();
	}

	private void exit() {
		GMBus.send(new WindowClosedMessage(this));
		//System.exit(0);
	}


	/**
	 *  These three routines will be removed as soon as I can figure out a dummy
	 *  argument to pass to mAboutActionPerformed, mPreferencesActionPerformed, and
	 *  exitForm from OSXAdapter.java
	 */
	public void mAboutActionPerformedMac() {
		AboutBox dialog = new AboutBox(this, true, getVersion());
		dialog.setVisible(true);
	}


	/**
	 * launches the preferences dialog on a mac.
	 */
	public void mPreferencesActionPerformedMac() {
		PreferencesDialog dialog = new PreferencesDialog(this, true, rootNode);
		dialog.setVisible(true);
	}


	/**
	 *  Exits GMGen, the mac way
	 */
	public void exitFormMac() {
		this.hide();
		//exit();
	}


	/**
	 *  clears the edit menu to allow a plugin to populate it
	 */
	public void clearEditMenu() {
		editMenu.removeAll();
		/**
		 *  Preferences... on the Macintosh is in the application menu. See
		 *  macOSXRegistration()
		 */
		if (!MAC_OS_X) {
			editMenu.add(editSeparator1);

			preferencesEditItem.setText("Preferences");
			editMenu.add(preferencesEditItem);
			preferencesEditItem.setEnabled(true);
			/* TODO: uncomment when we can use 1.4
			ActionListener[] listenerArray = preferencesEditItem.getActionListeners();
			for (int i = 0; i < listenerArray.length; i++) {
				preferencesEditItem.removeActionListener(listenerArray[i]);
			}
			preferencesEditItem.addActionListener(
				new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						mPreferencesActionPerformed(evt);
					}
			});
			*/
		}
	}


	/**
	 *  Sets a bunch of properties based on the status of GMGen at close
	 *
	 *@since    GMGen 3.3
	 */
	private void setCloseSettings() {
		SettingsHandler.setGMGenOption("WindowX", this.getX());
		SettingsHandler.setGMGenOption("WindowY", this.getY());
		SettingsHandler.setGMGenOption("WindowWidth", this.getSize().width);
		SettingsHandler.setGMGenOption("WindowHeight", this.getSize().height);

		//Logging Window
		SettingsHandler.setGMGenOption("Logging.On", logMode.getState());
		if (logWin == null) {
			SettingsHandler.setGMGenOption("Logging.WindowOpen", false);
		} else {
			SettingsHandler.setGMGenOption("Logging.WindowOpen", logWin.isVisible());
			SettingsHandler.setGMGenOption("Logging.WindowX", logWin.getX());
			SettingsHandler.setGMGenOption("Logging.WindowY", logWin.getY());
			SettingsHandler.setGMGenOption("Logging.WindowWidth", logWin.getSize().width);
			SettingsHandler.setGMGenOption("Logging.WindowHeight", logWin.getSize().height);
		}
	}


	/**
	 *  Handles the clicking on the file menu.
	 *
	 *@since    GMGen 3.3
	 */
	public void handleFileMenu() {}


	/**
	 *  Handles the clicking on the tool menu.
	 *
	 *@since    GMGen 3.3
	 */
	public void handleToolsMenu() {}


	/**
	 *  handles a menu canceled event
	 *
	 *@param  e  menu canceled event
	 *@since     GMGen 3.3
	 */
	public void menuCanceled(MenuEvent e) {}


	/**
	 *  handles a menu de-selected event
	 *
	 *@param  e  Menu Deselected event
	 *@since     GMGen 3.3
	 */
	public void menuDeselected(MenuEvent e) {}

	/**
	 *  function run to initialize PCGen directory location
	 *
	 *@since    GMGen 3.3
	 */
	private boolean initPcgenLocation() {
		return true;
	}
	// end initPcgenLocation


	/**
	 *  Initializes the Logger component
	 *
	 *@since    GMGen 3.3
	 */
	private void initLogger() {
		LogUtilities.inst().setLoggingOn(SettingsHandler.getGMGenOption("Logging.On", false));
		boolean loggingWin = SettingsHandler.getGMGenOption("Logging.WindowOpen", false);
		if (loggingWin) {
			logwinAction();
		}
	}


	/**
	 *  Initializes the settings, and implements their commands
	 *
	 *@since    GMGen 3.3
	 */
	private void initSettings() {
		int iWinX = SettingsHandler.getGMGenOption("WindowX", 0);
		int iWinY = SettingsHandler.getGMGenOption("WindowY", 0);
		setLocation(iWinX, iWinY);

		int iWinWidth = SettingsHandler.getGMGenOption("WindowWidth", 750);
		int iWinHeight = SettingsHandler.getGMGenOption("WindowHeight", 580);
		setSize(iWinWidth, iWinHeight);
	}


	/**
	 *  Initializes all the GUI components and places them in the correct place on
	 *  the GUI.
	 *
	 *@since    GMGen 3.3
	 */
	private void initComponents() {
		getContentPane().setLayout(new java.awt.BorderLayout());
		setTabbedPanes();

		addWindowListener(
			new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent evt) {
					exitForm(evt);
				}
			});

		/* TODO: Uncomment this when we can use a real API, not java 1.3
		addWindowFocusListener(
			new java.awt.event.WindowFocusListener() {
				public void windowGainedFocus(java.awt.event.WindowEvent e) {
					stateUpdate(e);
				}
				public void windowLostFocus(java.awt.event.WindowEvent e) {
				}
			});
		*/

		//sourceView.getLoadButton().addActionListener(this);
		//sourceView.getUnloadAllButton().addActionListener(this);
		//sourceView.getRemoveAllButton().addActionListener(this);
		GMGenSystemView.getTabPane().addChangeListener(this);
		getContentPane().add(theView, java.awt.BorderLayout.CENTER);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage(getClass().getResource("/pcgen/gui/resource/gmgen_icon.png"));
		setIconImage(img);
	}


	/**
	 *  Listens for menus to be clicked and calls the appropriate handlers.
	 *
	 *@param  e  the menu event that happened.
	 *@since     GMGen 3.3
	 */
	public void menuSelected(MenuEvent e) {
		if (e.getSource() == fileMenu) {
			handleFileMenu();
		} else if (e.getSource() == toolsMenu) {
			handleToolsMenu();
		}
	}


	/**
	 *  Sets certain needed settings to PCGen.
	 *
	 *@since    GMGen 3.3
	 */
	/*public static void setPCGenSettings() {
		String pcgenLocation = SettingsHandler.expandRelativePath(MiscUtilities.getGMGenOption("pcgenDir", System.getProperty("user.dir")));
		System.setProperty("pcgen.options", pcgenLocation);
		System.setProperty("user.dir", pcgenLocation);

		//SettingsHandler.setIsGMGen(true);
		SettingsHandler.readOptionsProperties();
		SettingsHandler.setOptionsProperties();
		SettingsHandler.setRollMethod(pcgen.core.Constants.ROLLINGMETHOD_ALLSAME);
		SettingsHandler.setHPRollMethod(2);

		SettingsHandler.setPcgenFilesDir(new File(pcgenLocation));
		SettingsHandler.setPcgenSystemDir(new File(pcgenLocation + "/system"));

		SettingsHandler.getOptions().setProperty("pcgen.files.pcgenSystemDir", SettingsHandler.getPcgenSystemDir().getAbsolutePath());
		//SettingsHandler.setMonsterDefault(true);
		//setPCGenOption("unlimitedStatPool", isUnlimitedStatPool());
		//SettingsHandler.setUseExperimentalCursor(false);
		SettingsHandler.setLoadCampaignsAtStart(true);
		SettingsHandler.setLoadCampaignsWithPC(true);
		SettingsHandler.setOptionAllowedInSources(true);
		SettingsHandler.setPCGenOption("userdir", pcgenLocation);
		SettingsHandler.setPccFilesLocation(new File(pcgenLocation + File.separator + "data"));

		// initialize selected campaign sources
		String sourceFiles = SettingsHandler.getGMGenOption("chosenCampaignSourcefiles", "");
		PersistenceManager.setChosenCampaignSourcefiles(Utility.split(sourceFiles, ','));

	}*/


	/**
	 *  Sets all the panes on the GUI in the correct order.
	 *
	 *@since    GMGen 3.3
	 */
	private void setTabbedPanes() {
		//setTabsDisabled();
		try {
			GMGenSystemView.getTabPane().setSelectedIndex(0);
			theView.showPane();
		}
		catch(Exception e) {
		}
	}


	/**
	 *  Sets the GUI tabs to be disabled.
	 *
	 *@since    GMGen 3.3
	 */
	private void setTabsDisabled() {
		for (int i = 1; i < GMGenSystemView.getTabPane().getTabCount(); i++) {
			GMGenSystemView.getTabPane().setSelectedIndex(i);
			GMGenSystemView.getTabPane().setEnabled(false);
		}
		toolsMenu.setEnabled(false);
		GMGenSystemView.getTabPane().setSelectedIndex(0);
	}


	/**
	 *  Sets the GUI tabs to be enabled.
	 *
	 *@since    GMGen 3.3
	 */
	private void setTabsEnabled() {
		for (int i = 1; i < GMGenSystemView.getTabPane().getTabCount(); i++) {
			GMGenSystemView.getTabPane().setSelectedIndex(i);
			GMGenSystemView.getTabPane().setEnabled(true);
		}
		toolsMenu.setEnabled(true);
		GMGenSystemView.getTabPane().setSelectedIndex(0);
	}


	/**
	 *  Ensures that the splash screen is not visible. This should be called before
	 *  displaying any dialog boxes or windows at startup.
	 */
	private static void hideSplashScreen() {
		if (splash != null) {
			splash.dispose();
			splash = null;
		}
	}


	/**
	 *  Displays the splashscreen at startup
	 */
	private static void showSplashScreen() {
		splash = new SplashScreen();
	}


	/**
	 *  Calls the necessary methods if an item on the GUI or model has changed.
	 *
	 *@param  e  the event that has happened.
	 *@since     GMGen 3.3
	 */
	public void stateChanged(ChangeEvent e) {
		stateUpdate(e);
	}

	/**
	 *  Calls the necessary methods if an item on the GUI or model has changed.
	 *
	 *@param  e  the event that has happened.
	 *@since     GMGen 3.3
	 */
	public void stateUpdate(EventObject e) {
		newFileItem.setEnabled(false);
		openFileItem.setEnabled(false);
		saveFileItem.setEnabled(false);
		clearEditMenu();
		GMBus.send(new StateChangedMessage(this, editMenu));
	}


	/**
	 *  Gets the build number of the GMGen
	 *
	 *@return    The build number
	 *@since     GMGen 3.3
	 */
	public static String getBuild() {
		return "03.03.99.01.00";
	}


	/**
	 *  Returns the GMGen version as a human-readable string.
	 *
	 *@return    The version
	 *@since     GMGen 3.3
	 */
	public static String getVersion() {
		return MiscUtilities.buildToVersion(getBuild());
	}


	/**
	 *  Message handler for the GMBus
	 *
	 *@param  message  The messge passed in from the bus
	 *@since           GMGen 3.3
	 */
	public void handleMessage(GMBMessage message) {
		//A plugin is asking for the creation of a new tab
		if (message instanceof TabAddMessage) {
			TabAddMessage tmessage = (TabAddMessage) message;
			Logging.debugPrint("Creating Tab " + GMGenSystemView.getTabPane().getTabCount());
			theView.insertPane(tmessage.getName(), tmessage.getPane(), GMGenSystemView.getTabPane().getTabCount());
		} else if (message instanceof PreferencesPanelAddMessage) {
			PreferencesPanelAddMessage pmessage = (PreferencesPanelAddMessage) message;
			Logging.debugPrint("Creating Preferences Panel");
			rootNode.addPanel(pmessage.getName(), pmessage.getPane());
		}
		// A plugin is asking for the creation of a new option in the tool menu
		else if (message instanceof ToolMenuItemAddMessage) {
			ToolMenuItemAddMessage mmessage = (ToolMenuItemAddMessage) message;
			toolsMenu.add(mmessage.getMenuItem());
		}
		// A plugin is askign for the creation of a new option in the help menu
		else if (message instanceof HelpMenuItemAddMessage) {
			//Help is unpublished, so these menu items should be turned off
			//HelpMenuItemAddMessage mmessage = (HelpMenuItemAddMessage) message;
			//helpMenu.add(mmessage.getMenuItem());
		} else if (message instanceof WindowClosedMessage) {
			setCloseSettings();
		} else if (message instanceof PCLoadedMessage) {
			// tell source view to refresh
			//sourceView.updateLoadedCampaignsUI();
		}
	}
}

