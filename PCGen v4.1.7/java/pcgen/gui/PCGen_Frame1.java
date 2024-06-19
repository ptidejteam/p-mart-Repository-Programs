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
 *
 * Current Ver: $Revision: 1.1 $
 * Last Editor: $Author: vauchers $
 * Last Edited: $Date: 2006/02/21 00:57:40 $
 *
 */

package pcgen.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.TabbedPaneUI;
import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.filter.FilterDialogFactory;
import pcgen.gui.filter.Filterable;
import pcgen.io.PCGIOHandler;
import pcgen.util.FOPResourceChecker;
import pcgen.util.PropertyFactory;

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
	//
	// number of the first character tab
	//
	public static final int FIRST_CHAR_TAB = 1;

	// Our automagic mouse cursor when wait > 3/4 second
	private static WaitCursorEventQueue waitQueue = new WaitCursorEventQueue(750);
	private static boolean usingExperimentalCursor = false;
	private static String missingLibMsg;
	private pcGenGUI mainClass;
	private static CharacterInfo characterPane = null;

	/** Needs to be public */
	public static LstConverter lstConverter = null;

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
	 **/
	private MainSource mainSource = new MainSource();

	/**
	 * Menubar and toolbar actions.
	 **/
	FrameActionListener frameActionListener = new FrameActionListener(this);
	private MainPopupMenu mainPopupMenu = new MainPopupMenu(frameActionListener);
	private PCPopupMenu pcPopupMenu = new PCPopupMenu(frameActionListener);
	private PopupListener popupListener = new PopupListener(baseTabbedPane, mainPopupMenu, pcPopupMenu);

	/**
	 * Menubar for the main application.
	 **/
	MenuItems menuBar = new MenuItems(); // NOT private

	/**
	 * ToolBar for the main application.
	 **/
	private PToolBar toolBar = PToolBar.createToolBar(this);

	// GUI stuff
	private JPanel panelSouth = new JPanel();
	private JPanel panelSouthEast = new JPanel();
	private JPanel panelSouthCenter = new JPanel();

	private BorderLayout borderLayout1 = new BorderLayout();
	private BorderLayout borderLayout2 = new BorderLayout();

	private FlowLayout flowLayout1 = new FlowLayout();
	private FlowLayout flowLayout2 = new FlowLayout();

	// Characters / core stuff
	private int newPCNameCount = 0;

	KitSelector kitSelector = null;

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
			Globals.errorPrint(missingLibMsg, mrex);
		}
		finally
		{
			d_properties = null;
		}

		missingLibMsg = missingLibMsg.replace('|', '\n');
	}

	/**
	 * Screen initialization. Override close.
	 * <p>
	 * Calls private <code>jbInit()</code> which does real screen
	 * initialization: Sets up all the window properties (icon,
	 * title, size);
	 * and creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 **/
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
			Globals.errorPrint("jbInit", e);
		}
	}

	public MainSource getMainSource()
	{
		return mainSource;
	}

	public static JLabel getStatusBar()
	{
		return statusBar;
	}

	public void showToolBar()
	{
		toolBar.setVisible(SettingsHandler.isToolBarShown());
	}

	public void setGameModeTitle()
	{
		setTitle("PCGen - " + SettingsHandler.getGame().getName() + " Campaign");
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
	 * <p>
	 * Creates the campaign and DM tools tabs, along with all
	 * the sub-panes of DM tools.
	 *
	 * @exception  Exception  Any Exception
	 **/
	private void jbInit() throws Exception
	{
		Utility.maybeSetIcon(this, "PcgenIcon.gif");

		getContentPane().setLayout(borderLayout1);
		setSize(new Dimension(700, 600));
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
		this.getContentPane().add(baseTabbedPane, BorderLayout.CENTER);
		baseTabbedPane.addTab("Source Materials", mainSource);

		this.getContentPane().add(panelSouth, BorderLayout.SOUTH);
		panelSouth.add(statusBar, BorderLayout.SOUTH);
		baseTabbedPane.addChangeListener(new javax.swing.event.ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent c)
			{
				baseTabbedPane_changePanel(c);
			}
		});

		mainSource.addComponentListener(toolBar.getComponentListener());
	}

	public void setMainClass(pcGenGUI owner)
	{
		mainClass = owner;
	}

	/**
	 * Enable/disable the new item
	 */
	protected void enableNew(boolean itemState)
	{
		menuBar.newItem.setEnabled(itemState);
		toolBar.newItem.setEnabled(itemState);
		mainPopupMenu.newItem.setEnabled(itemState);
		pcPopupMenu.newItem.setEnabled(itemState);
	}

	protected void enableLstEditors(boolean itemState)
	{
		menuBar.listEditor.setEnabled(itemState);
	}

	/**
	 * Enable/disable the close item
	 */
	protected void enableClose(boolean itemState)
	{
		menuBar.closeItem.setEnabled(itemState);
		toolBar.closeItem.setEnabled(itemState);
		pcPopupMenu.closeItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the closeAll item
	 */
	protected void enableCloseAll(boolean itemState)
	{
		menuBar.closeAllItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the save item
	 */
	protected void enableSave(boolean itemState)
	{
		menuBar.saveItem.setEnabled(itemState);
		toolBar.saveItem.setEnabled(itemState);
		pcPopupMenu.saveItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the saveAs item
	 */
	protected void enableSaveAs(boolean itemState)
	{
		menuBar.saveAsItem.setEnabled(itemState);
		pcPopupMenu.saveAsItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the saveAll item
	 */
	protected void enableSaveAll(boolean itemState)
	{
		menuBar.saveAllItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the revertToSaved item
	 */
	protected void enableRevertToSaved(boolean itemState)
	{
		menuBar.revertToSavedItem.setEnabled(itemState);
		pcPopupMenu.revertToSavedItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the printPreview item
	 */
	protected void enablePrintPreview(boolean itemState)
	{
		menuBar.printPreviewItem.setEnabled(itemState);
		toolBar.printPreviewItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the print item
	 */
	protected void enablePrint(boolean itemState)
	{
		menuBar.printItem.setEnabled(itemState);
		toolBar.printItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partySave item
	 */
	protected void enablePartySave(boolean itemState)
	{
		menuBar.partySaveItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partySaveAs item
	 */
	protected void enablePartySaveAs(boolean itemState)
	{
		menuBar.partySaveAsItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the partyClose item
	 */
	protected void enablePartyClose(boolean itemState)
	{
		menuBar.partyCloseItem.setEnabled(itemState);
	}

	/**
	 * Enable/disable the export menu
	 */
	protected void enableExport(boolean itemState)
	{
		menuBar.exportMenu.setEnabled(itemState);
	}

	protected void enableKit(boolean itemState)
	{
		menuBar.addKit.setEnabled(itemState);
		toolBar.addKit.setEnabled(itemState);
	}

	/**
	 * Enable/disable all items intelligently.
	 */
	public static void enableMenuItems()
	{
		if (!Globals.getUseGUI())
		{
			return;
		}
		PCGen_Frame1 frame = Globals.getRootFrame();

		frame.enableNew(false);
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
		frame.enableNew(true);
		if (!Globals.displayListsHappy())
		{
			return;
		}

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
	 **/
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
	 **/
	void handleQuit()
	{
		if (SettingsHandler.getLeftUpperCorner() == null)
		{
			SettingsHandler.setLeftUpperCorner(new Point(0, 0));
		}
		SettingsHandler.getLeftUpperCorner().setLocation(getLocationOnScreen().getX(), getLocationOnScreen().getY());

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

		// This is a screwey place for this, but because parties
		// aren't treated as first-class objects the way PCs are,
		// we have to do this sort of business.  XXX
		updateRecentPartyMenu();

		SettingsHandler.storeFilterSettings(mainSource);
		Globals.writeCustomFiles();
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
					Globals.errorPrint("removeTemporaryFiles", e);
				}
				return false;
			}
		});
	}

	private class AskUserPopup extends javax.swing.JDialog
	{
		private boolean doDelete = false;

		public AskUserPopup(JFrame owner, String title, boolean modal)
		{
			super(owner, title, modal);
			initComponents();
			setLocationRelativeTo(owner);
		}

		private void initComponents()
		{
			final javax.swing.JButton btnYes = new javax.swing.JButton("Yes");
			final javax.swing.JButton btnNo = new javax.swing.JButton("No");
			final JPanel jPanel = new javax.swing.JPanel();
			final javax.swing.JCheckBox chkDontAsk = new javax.swing.JCheckBox("Don't ask again");
			jPanel.add(chkDontAsk);
			jPanel.add(btnYes);
			jPanel.add(btnNo);
			btnYes.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					if (chkDontAsk.isSelected())
					{
						SettingsHandler.setCleanupTempFiles(1);
					}
					setDelete(true);
					dispose();
				}
			});
			btnNo.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
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
			e.printStackTrace();
		}
		return false;
	}

	void addPCTab(PlayerCharacter aPC)
	{
		if (characterPane == null)
		{
			characterPane = new CharacterInfo();
		}
		else
		{
			resetCharacterTabs();
		}

		baseTabbedPane.addTab(aPC.getDisplayName(), null, characterPane, SettingsHandler.isToolTipTextShown() ? aPC.getFullDisplayName() : null);
		baseTabbedPane.setSelectedIndex(baseTabbedPane.getTabCount() - 1);
	}

	private void doNewItem()
	{
		if (!Globals.displayListsHappy())
		{
			JOptionPane.showMessageDialog(null,
				PropertyFactory.getString("in_newCharNoSources"),
				"PCGen",
				JOptionPane.ERROR_MESSAGE);
			return;
		}

		final PlayerCharacter aPC = new PlayerCharacter();
		Globals.getPCList().add(aPC);
		++newPCNameCount;
		aPC.setName("New" + newPCNameCount);
		aPC.setDirty(true);

		addPCTab(aPC);
	}

	/**
	 * Creates a new {@link PlayerCharacter} model, and a corresponding
	 * {@link CharacterInfo} panel. The <code>PlayerCharacter</code> is
	 * added to the <code>Globals.getPCList()</code>. The <code>CharacterInfo</code>
	 * is added to both the <code>characterList</code>, and adds it to the main
	 * program frame, <code>baseTabbedPane</code> as a new tab.
	 **/
	void newItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		// How does this work with the toolbar button?? --bko XXX
		toolBar.newItem.requestFocus();
		menuBar.newItem.requestFocus();

		doNewItem(); // selects new tab for us
	}

	/**
	 * Load a party metafile, including campaign info and characters.
	 * Campaigns are loaded as from the Campaign tab.
	 * Characters are loaded into a new <code>PlayerCharacter</code> model
	 * and a corresponding <code>CharacterInfo</code> panel is created.
	 **/
	void partyOpenItem_actionPerformed(ActionEvent event)
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Opening party...");
		statusBar.updateUI();

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

	String getCurrentPartyDisplayName()
	{
		File file = new File(partyFileName);
		String displayName = file.getName();
		int lastDot = displayName.lastIndexOf('.');
		if (lastDot != -1)
		{
			displayName = displayName.substring(0, lastDot);
		}

		return displayName;
	}

	boolean loadPartyFromFile(File file)
	{
		if (mainClass != null && mainClass.loadPartyFromFile(file, this))
		{
			//if everything loaded successfully, then this file becomes the "current" party file
			partyFileName = file.getAbsolutePath();
			String displayName = getCurrentPartyDisplayName();

			// You REMOVE items when they are opened, then
			// ADD them when closed.
			menuBar.openRecentPartyMenu.remove(displayName, file);
			enablePartyClose(true);
		}
		else
		{
			Globals.errorPrint("Error in loadPartyFromFile");
			return false;
		}

		return true;
	}

	/**
	 * Saves a party metafile, including campaign info and characters.
	 **/
	void partySaveItem_actionPerformed(ActionEvent event)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.partySaveItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.  --bko
		saveAllPCs();
		partySaveItem(false);
	}

	void partySaveAsItem_actionPerformed(ActionEvent event)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.partySaveAsItem.requestFocus();

		// Save PCs first so that if you change their names, the party has a chance to know about that.  --bko
		saveAllPCs();
		partySaveItem(true);
	}

	void partySaveItem(boolean saveas)
	{
		boolean newParty = false;
		File prevFile = null, file = null;
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
			{
				return;
			}
		}

		try
		{
			partyFileName = file.getAbsolutePath();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			// Save party file data here
			// Save version info here (we no longer save campaign/source info in the party file)
			ResourceBundle d_properties = ResourceBundle.getBundle("pcgen/gui/prop/PCGenProp");
			bw.write("VERSION:");
			bw.write(d_properties.getString("VersionNumber"));
			bw.newLine();
			Iterator anIter = Globals.getPCList().iterator();
			//save PC filenames
			while (anIter.hasNext())
			{
				final PlayerCharacter aPC = (PlayerCharacter) anIter.next();
				bw.write(aPC.getFileName() + ",");
			}
			bw.newLine(); // don't write files without terminators.
			bw.close();
			SettingsHandler.setPcgPath(file.getParentFile()); //still set this, we want .pcp and .pcg files in the same place
			enablePartyClose(true);
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null,
				"Could not save " + partyFileName,
				"PCGen",
				JOptionPane.ERROR_MESSAGE);
			Globals.errorPrint("Could not save" + partyFileName, ex);
		}
	}

	/**
	 * Update Recent Party menu.
	 */
	void updateRecentPartyMenu()
	{
		if (partyFileName.length() > 0)
		{
			File partyFile = new File(partyFileName);
			menuBar.openRecentPartyMenu.add(getCurrentPartyDisplayName(), partyFile); // move to top
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

		updateRecentPartyMenu();

		statusBar.setText(oldStatus);
	}

	/**
	 * Show the preferences pane.
	 */
	void preferencesItem_actionPerformed(ActionEvent event)
	{
		String oldStatus = statusBar.getText();
		statusBar.setText("Preferences...");
		statusBar.updateUI();

		PreferencesDialog.show(this);

		statusBar.setText(oldStatus);
	}

	void addKit_actionPerformed(ActionEvent event)
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
			final String filter = getCharacterPane().getKitFilter();
			if (kitSelector == null)
			{
				kitSelector = new KitSelector(aPC);
			}
			kitSelector.show();
			kitSelector.setFilter(filter);
		}
	}

	void newPopupItem_actionPerformed(ActionEvent event)
	{
		doNewItem();
	}

	void closePopupItem_actionPerformed(ActionEvent event)
	{
		int index = popupListener.getTabIndex();
		closePCTabAt(index, true);
		// Try not to jump the tab focus around
		baseTabbedPane.setSelectedIndex(index < baseTabbedPane.getTabCount() ? index : index - 1);
	}

	void savePopupItem_actionPerformed(ActionEvent event)
	{
		savePC(getPCForTabAt(popupListener.getTabIndex()), false);
	}

	void saveAsPopupItem_actionPerformed(ActionEvent event)
	{
		savePC(getPCForTabAt(popupListener.getTabIndex()), true);
	}

	void revertToSavedPopupItem_actionPerformed(ActionEvent event)
	{
		closePCTabAt(popupListener.getTabIndex(), false);
	}

	void moveTab(int oldIndex, int newIndex)
	{
		// Because the tabs are "fake", we need to reorder the
		// PCList in Globals, then simply refresh.
		ArrayList pcList = Globals.getPCList();
		PlayerCharacter aPC = (PlayerCharacter) pcList.get(oldIndex - FIRST_CHAR_TAB);
		pcList.remove(oldIndex - FIRST_CHAR_TAB);
		pcList.add(newIndex - FIRST_CHAR_TAB, aPC);
		Globals.setPCList(pcList);

		forceUpdate_PlayerTabs();
		baseTabbedPane.setSelectedIndex(newIndex);
	}

	void shiftLeftPopupItem_actionPerformed(ActionEvent event)
	{
		int index = popupListener.getTabIndex();
		moveTab(index, index == FIRST_CHAR_TAB ? (baseTabbedPane.getTabCount() - 1) : (index - 1));
	}

	void shiftRightPopupItem_actionPerformed(ActionEvent event)
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
	void openItem_actionPerformed(ActionEvent event)
	{
		toolBar.openItem.requestFocus();
		menuBar.openItem.requestFocus();
		String oldStatus = statusBar.getText();
		statusBar.setText("Opening character.  Please wait...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.openItem.requestFocus();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(SettingsHandler.getPcgPath());
		fc.setFileFilter(filter);

		int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			SettingsHandler.setPcgPath(file.getParentFile());
			fc.setMultiSelectionEnabled(false);
			loadPCFromFile(file);
			Globals.sortCampaigns();
		}
		statusBar.setText(oldStatus);
	}

	public boolean loadPCFromFile(File file)
	{
		if (mainClass != null && mainClass.loadPCFromFile(file))
		{
			final PlayerCharacter aPC = Globals.getCurrentPC();
			aPC.setFileName(file.getAbsolutePath());

			addPCTab(aPC);
			menuBar.openRecentPCMenu.remove(aPC.getDisplayName(), file);

		}
		else
		{
			Globals.errorPrint("Error in loadPCFromFile");
			return false;
		}

		return true;
	}

	public static PlayerCharacter getPCForTabAt(int index)
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
	void baseTabbedPane_changePanel(ChangeEvent c)
	{
		// call requestFocus to prevent open edits
		// from applying to the wrong PC
		baseTabbedPane.requestFocus();

		enableMenuItems();

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

	void resetCharacterTabs()
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
	void revertToSavedItem_actionPerformed(ActionEvent e)
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

		int reallyClose = JOptionPane.showConfirmDialog(this, aPC.getDisplayName() + " changed.  Discard changes?", "Revert " + aPC.getDisplayName() + "?", JOptionPane.YES_NO_OPTION);
		if (reallyClose != JOptionPane.YES_OPTION)
		{
			return;
		}

		String oldStatus = statusBar.getText();
		statusBar.setText("Reverting character to saved...");
		statusBar.updateUI();

		// seize the focus to cause focus listeners to fire
		menuBar.revertToSavedItem.requestFocus();

		String fileName = aPC.getFileName();
		if (fileName.equals(""))
		{
			statusBar.setText(oldStatus);
			return;
		}

		closeItem_actionPerformed(e);

		File pcFile = new File(SettingsHandler.getPcgPath(), fileName);
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
	void saveItem_actionPerformed(ActionEvent e)
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
	void saveAsItem_actionPerformed(ActionEvent e)
	{
		// seize the focus to cause focus listeners to fire
		menuBar.saveAsItem.requestFocus();
		final PlayerCharacter aPC = getCurrentPC();
		if (aPC == null) return;
		savePC(aPC, true);
	}

	/**
	 * Property change listener for the event "selected file
	 * changed".  Ensures that the filename doesn't get changed
	 * when a directory is selected.
	 *
	 * @author Dmitry Jemerov <yole@spb.cityline.ru>
	 */

	final class FilenameChangeListener implements PropertyChangeListener
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
			File newSelFile = (File) evt.getNewValue();
			if (newSelFile != null && !newSelFile.isDirectory())
				lastSelName = newSelFile.getName();
		}
	}

	/**
	 * Checks whether a character can be saved, and if so, calls
	 * it's <code>save</code> method.
	 *
	 * @param aPC The PlayerCharacter to save
	 * @param saveas boolean if <code>true</code>, ask for file name
	 *
	 * @return <code>true</code> if saved; <code>false</code> if saveas cancelled
	 */
	boolean savePC(PlayerCharacter aPC, boolean saveas)
	{
		boolean newPC = false;
		File prevFile, file = null;
		String aPCFileName = aPC.getFileName();

		if (aPCFileName.equals(""))
		{
			prevFile = new
				File(SettingsHandler.getPcgPath(), aPC.getDisplayName() + Constants.s_PCGEN_CHARACTER_EXTENSION);
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
					JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a character.", "PCGen", JOptionPane.ERROR_MESSAGE);
					return false;
				}

				if (file.exists() && (newPC || prevFile == null || !file.getName().equals(prevFile.getName())))
				{
					int reallyClose = JOptionPane.showConfirmDialog(this, "The file " + file.getName() + " already exists, are you sure you want to overwrite it?", "Confirm overwriting " + file.getName(), JOptionPane.YES_NO_OPTION);

					if (reallyClose != JOptionPane.YES_OPTION)
						return false;
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
			JOptionPane.showMessageDialog(null,
				"Could not save " + aPC.getDisplayName(),
				"PCGen",
				JOptionPane.ERROR_MESSAGE);
			Globals.errorPrint("Could not save " + aPC.getDisplayName(), ex);
		}

		return true;
	}

	void saveAllPCs()
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
	void saveAllItem_actionPerformed(ActionEvent e)
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
			baseTabbedPane_changePanel(null);
		}

		// This will free up resources, which are locked
		oldPC.dispose();

		killKitSelector();

		// now set the PC to something else
		final PlayerCharacter aPC = getPCForTabAt(newIndex);
		Globals.setCurrentPC(aPC);
	}

	/**
	 * Close a tab by tab number, not pc number.
	 *
	 * @param  index Tab the character is on (not PC number)
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
			int reallyClose = 0;

			if (isClosing)
			{
				reallyClose = JOptionPane.showConfirmDialog(this,
					aPC.getDisplayName() + " changed.  Save changes before closing?",
					"Save " + aPC.getDisplayName() + " before closing?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			}
			else // reverting
			{
				reallyClose = JOptionPane.showConfirmDialog(this,
					aPC.getDisplayName() + " changed.  Save changes before reverting?",
					"Save " + aPC.getDisplayName() + " before reverting?",
					JOptionPane.YES_NO_CANCEL_OPTION);
			}

			if (reallyClose == JOptionPane.CANCEL_OPTION)
			{
				return false; // don't quit/revert
			}

			else if (reallyClose == JOptionPane.NO_OPTION)
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

		// save filter settings
		if (bSave && (index >= FIRST_CHAR_TAB))
		{
			characterPane.storeFilterSettings();

			// Quick hack: blank filename means never saved before
			final String fileName = aPC.getFileName();
			if (!savePC(aPC, fileName.equals("")))
			{
				return false;
			}

			// Only put on menu if was saved to a file
			if (isClosing)
			{
				menuBar.openRecentPCMenu.add(aPC.getDisplayName(), new File(aPC.getFileName())); // move to top
			}
		}

		disposePCTabAt(index);

		return true;
	}

	/**
	 * Accessor to change the tab name when the CharacterInfo changes.
	 * Changes the name for the currently selected tab of <code>baseTabbedPane</code>.
	 *
	 * @param aName The string to set the tab name to
	 */
	public static void setTabName(PlayerCharacter pc, String aName)
	{
		int index = Globals.getPCList().indexOf(pc);
		if (index >= 0)
		{
			getBaseTabbedPane().setTitleAt(index + FIRST_CHAR_TAB, aName);
		}
	}

	public static void setTabName(int index, String aName)
	{
		getBaseTabbedPane().setTitleAt(index, aName);
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
		menuBar.printFrame.show();
	}

	public void exportToStandardItem_actionPerformed(ActionEvent e)
	{
		if (menuBar.exportPopup == null)
		{
			menuBar.exportPopup = new ExportPopup(baseTabbedPane);
		}
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
		{
			menuBar.exportPDFPopup = new ExportPDFPopup(baseTabbedPane);
		}
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
// 		Component c;
		for (int i = 0; i < getBaseTabbedPane().getTabCount(); i++)
		{
			if (i >= FIRST_CHAR_TAB)
			{
				characterPane.restoreFilterSettings(filterableName);
				break; // only 1 set of filters
			}
// 			c = getBaseTabbedPane().getComponentAt(i);
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
	 *
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
//			return (CharacterInfo)getBaseTabbedPane().getComponentAt(index);
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
	 * The {@link Options}
	 * and {@link GameModes} are all created externally.
	 */
	final class MenuItems extends JMenuBar
	{
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
		//		JCheckBoxMenuItem experimentalCursor;

		private boolean enablePDF;
		private final String msg = "\n" + missingLibMsg;

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

		final class FileMenu extends JMenu implements MouseListener
		{
			FileMenu()
			{
				super("File");

				setMnemonic('F');

				if (SettingsHandler.isToolTipTextShown())
				{
					Utility.setDescription(this, "File operations");
				}

				this.setEnabled(true);

				this.addMouseListener(this);
			}

			public void mousePressed(MouseEvent e)
			{
				enableMenuItems();
			}

			/** Empty implementation so this class isn't abstract */
			public void mouseReleased(MouseEvent e)
			{
			}

			/** Empty implementation so this class isn't abstract */
			public void mouseEntered(MouseEvent e)
			{
			}

			/** Empty implementation so this class isn't abstract */
			public void mouseExited(MouseEvent e)
			{
			}

			/** Empty implementation so this class isn't abstract */
			public void mouseClicked(MouseEvent e)
			{
			}
		}

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

			closeItem = Utility.createMenuItem("Close", frameActionListener.closeActionListener, "file.close", 'C', "control W", "Close the current character", "Close16.gif", false);
			fileMenu.add(closeItem);

			closeAllItem = Utility.createMenuItem("Close All", frameActionListener.closeAllActionListener, "file.closeall", 'L', null, "Close all characters", "CloseAll16.gif", false);
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

			JMenu partyMenu = Utility.createMenu("Party", 'Y', "Party operations", null, true);
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

			partyCloseItem = Utility.createMenuItem("Close", frameActionListener.partyCloseActionListener, "file.party.close", 'C', null, "Close the current party", "Close16.gif", false);
			partyMenu.addSeparator();

			partyMenu.add(partyCloseItem);

			partySaveItem = Utility.createMenuItem("Save", frameActionListener.partySaveActionListener, "file.party.save", 'S', null, "Save the current party to its .PCC file", "Save16.gif", false);
			partyMenu.add(partySaveItem);

			partySaveAsItem = Utility.createMenuItem("Save As...", frameActionListener.partySaveAsActionListener, "file.party.saveas", 'A', null, "Save the current party to a new .PCC file", "SaveAs16.gif", false);
			partyMenu.add(partySaveAsItem);

			fileMenu.addSeparator();

			printPreviewItem = Utility.createMenuItem("Print Preview", frameActionListener.printPreviewActionListener, "file.printpreview", 'V', null, "Preview the current character in a browser", "PrintPreview16.gif", false);
			fileMenu.add(printPreviewItem);

			printItem = Utility.createMenuItem("Print...", frameActionListener.printActionListener, "file.print", 'P', "control P", "Print the current character", "Print16.gif", false);
			fileMenu.add(printItem);

			fileMenu.addSeparator();

			importMenu = Utility.createMenu("Import", 'I', "Import from other file formats", "Import16.gif", true);
			// Do not add until we get some formats to
			// import.  --bko XXX
			//fileMenu.add(importMenu);

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
			exportPDFItem = Utility.createMenuItem("...to PDF", new ActionListener()
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
						JOptionPane.showMessageDialog(null,
							FOPResourceChecker.getMissingResourceMessage() +
							msg,
							"PCGen",
							JOptionPane.WARNING_MESSAGE);
					}
				}
			},
				"file.export.pdf",
				'P',
				null,
				"Export the current character to a .PDF file",
				null,
				true);
			exportMenu.add(exportPDFItem);

			fileMenu.addSeparator();
			addKit = Utility.createMenuItem("Add Kit", frameActionListener.addKitActionListener, "assign.kit", 'K', "control K", PropertyFactory.getString("in_addKits"), "Information16.gif", false);
			fileMenu.add(addKit);

			fileMenu.addSeparator();
			fileMenu.addSeparator();

			exitItem = Utility.createMenuItem("Exit", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PCGen_Frame1.this.exitItem_actionPerformed(e);
				}
			},
				"file.exit",
				'X',
				"control Q",
				"Quit the best PC generator on the planet",
				null,
				true);
			fileMenu.add(exitItem);
			MenuItems.this.add(fileMenu);

			//Modes menu - to the Settings menu
			//GameModes modesMenu = new GameModes();
			//modesMenu.setMnemonic('C'); // renamed Campaign
			//MenuItems.this.add(modesMenu);

			//Options Menu
			Options optionMenu = new Options();
			optionMenu.setMnemonic('O');
			MenuItems.this.add(optionMenu);

			//Tools Menu
			JMenu toolsMenu = Utility.createMenu("Tools", 'T', "Tools you can use", "wrench.gif", true);
			MenuItems.this.add(toolsMenu);

			JMenuItem converterItem = Utility.createMenuItem("Lst Converter", new ActionListener()
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
						if ((lstConverter == null) || !basePath.equals(file.toString()))
						{
							lstConverter = new LstConverter(file.toString());
						}
						lstConverter.show();
					}
				}
			},
				"tools.converter",
				'C',
				null,
				PropertyFactory.getString("in_contextConvertTip"),
				"wrench.gif",
				true);
			toolsMenu.add(converterItem);

			filtersMenu = Utility.createMenu("Filters", 'F', "Filters, filters everywhere", "Zoom16.gif", true);
			toolsMenu.add(filtersMenu);

			JMenuItem openFiltersItem = Utility.createMenuItem("Open", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterSelectDialog();
				}
			},
				"tools.filters.open",
				'O',
				null,
				PropertyFactory.getString("in_openFilters"),
				"Zoom16.gif",
				true);
			filtersMenu.add(openFiltersItem);

			JMenuItem clearFiltersItem = Utility.createMenuItem("Clear", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.clearSelectedFiltersForSelectedFilterable();
				}
			},
				"tools.filters.clear",
				'C',
				null,
				PropertyFactory.getString("in_removeFilters"),
				"RemoveZoom16.gif",
				true);
			filtersMenu.add(clearFiltersItem);

			JMenuItem customFiltersItem = Utility.createMenuItem("Custom", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterCustomDialog();
				}
			},
				"tools.filters.custom",
				'U',
				null,
				PropertyFactory.getString("in_customFilters"),
				"CustomZoom16.gif",
				true);
			filtersMenu.add(customFiltersItem);

			JMenuItem editFiltersItem = Utility.createMenuItem("Edit", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterEditorDialog();
				}
			},
				"tools.filters.edit",
				'E',
				null,
				PropertyFactory.getString("in_compoundFilters"),
				"EditZoom16.gif",
				true);
			filtersMenu.add(editFiltersItem);

			toolsMenu.addSeparator();


			//
			// List Editors
			//
			listEditor = Utility.createMenuItem("List Editors", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new LstEditorMain().show();
				}
			},
				"tools.editors",
				'E',
				null,
				"Edit your data",
				null,
				true);
			toolsMenu.add(listEditor);



			//Debug Menu
			JMenu debugMenu = Utility.createMenu("Debug", 'D', "Debug operations", null, true);

			debugMode = new JCheckBoxMenuItem();
			debugMode.setMnemonic('M');
			Utility.setDescription(debugMode, "Show debug messages");
			debugMode.setText("Debug Mode");
			debugMode.setSelected(Globals.isDebugMode());
			debugMode.addActionListener(new ActionListener()
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

			JMenuItem consoleMenuItem = Utility.createMenuItem("Console", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (debugFrame == null)
					{
						debugFrame = new DebugFrame();
					}
					debugFrame.setVisible(true);
				}
			},
				"debug.console",
				'C',
				null,
				"Send debug output to the Java console",
				null,
				true);
			debugMenu.add(consoleMenuItem);

//			debugMenu.addSeparator();
//
//			experimentalCursor = new JCheckBoxMenuItem();
//			experimentalCursor.setMnemonic('X');
//			Utility.setDescription(experimentalCursor, "Warning: may cause excess flakiness");
//			experimentalCursor.setText("<html><i>Test <b>experimental</b> wait cursor</i></html>");
//			experimentalCursor.setSelected(SettingsHandler.getUseExperimentalCursor());
//			experimentalCursor.addActionListener(
//				new ActionListener()
//				{
//					public void actionPerformed(ActionEvent e)
//					{
//						SettingsHandler.setUseExperimentalCursor(experimentalCursor.isSelected());
//					}
//				});
//			debugMenu.add(experimentalCursor);


			MenuItems.this.add(debugMenu);

			//Help Menu
			helpMenu = Utility.createMenu("Help", 'H', "Help operations", "Help16.gif", true);

			JMenuItem contextHelpItem = Utility.createMenuItem("Help on context", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					PToolBar.displayHelpPanel(true);
				}
			},
				"help.context",
				'C',
				null,
				"Help on current context",
				"ContextualHelp16.gif",
				true);
			helpMenu.add(contextHelpItem);

			JMenuItem docsItem = Utility.createMenuItem("Documentation", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					new DocsFrame();
				}
			},
				"help.docs",
				'D',
				"F1",
				"Read the documentation in a browser",
				"Help16.gif",
				true);
			helpMenu.add(docsItem);

			helpMenu.addSeparator();

			JMenuItem oglItem = Utility.createMenuItem("Display OGL License", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					pcGenGUI.showLicense();
				}
			},
				"help.ogl",
				'G',
				null,
				"Read the Open Gaming License",
				null,
				true);
			helpMenu.add(oglItem);

			JMenuItem d20Item = Utility.createMenuItem("Display Required d20 Information", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					pcGenGUI.showMandatoryD20Info();
				}
			},
				"help.d20",
				'2',
				null,
				"Read the Required d20 Information",
				null,
				true);
			helpMenu.add(d20Item);

			helpMenu.addSeparator();

			JMenuItem aboutItem = Utility.createMenuItem("About PCGen", new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					if (aboutFrame == null)
					{
						aboutFrame = new AboutFrame();
					}
					aboutFrame.setVisible(true);
				}
			},
				"help.about",
				'A',
				null,
				"About PCGen",
				"About16.gif",
				true);
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
		 *
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
					BrowserLauncher.openURL(SettingsHandler.getPcgenDocsDir().getAbsolutePath() + File.separator + "indexmain.htm");
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(null,
						"Could not open docs in external browser. " +
						"Have you set your default browser in the " +
						"Preference menu? Sorry...",
						"PCGen",
						JOptionPane.ERROR_MESSAGE);
					Globals.errorPrint("Could not open docs in external browser", ex);
				}
			}
		}

	}

	public static PCGen_Frame1 getRealParentFrame(JPanel child)
	{
		//There *is* a better way to do this. I just haven't thought of it yet.
		PCGen_Frame1 parent = (PCGen_Frame1) child.getParent().getParent().getParent().getParent().getParent();
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
			characterPane.infoGear().getInfoInventory().refreshAvailableList(newEq, purchase, true);
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
			characterPane.infoFeats().forceUpdate();
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
			characterPane.infoSummary().setNeedsUpdate(true);
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
			characterPane.infoClasses().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoDomain()
	{
		if (characterPane != null)
		{
			characterPane.infoDomains().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoFeats()
	{
		if (characterPane != null)
		{
			characterPane.infoFeats().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoSkills()
	{
		if (characterPane != null)
		{
			characterPane.infoSkills().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoSpells()
	{
		if (characterPane != null)
		{
			characterPane.infoSpells().setNeedsUpdate(true);
		}
	}

	public static void forceUpdate_InfoGear()
	{
		if (characterPane != null)
		{
			characterPane.infoGear().setNeedsUpdate(true);
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

	public static void useExperimentalCursor(boolean b)
	{
		if (b)
		{
			if (!usingExperimentalCursor)
			{
				usingExperimentalCursor = true;
				Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQueue);
			}
		}
		else
		{
			if (usingExperimentalCursor)
			{
				try
				{
					waitQueue.doPop();
				}
				catch (EmptyStackException e)
				{
				}

				usingExperimentalCursor = false;
			}
		}

	}

	// From http://www.javaworld.com/javaworld/javatips/jw-javatip87.html
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

			synchronized void startTimer(Object source)
			{
				this.source = source;
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
			if (!(e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e))) return;

			index = indexAtLocation(e.getX(), e.getY());
			// Clicked somewhere besides a tab
			if (index < 0)
			{
				return;
			}

			enableMenuItems();

			if (index < FIRST_CHAR_TAB)
			{
				mainPopupMenu.setLoaded(Globals.displayListsHappy());
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
				kitSelector.closeDialog(null);
			}
			kitSelector = null;
		}
	}

}
