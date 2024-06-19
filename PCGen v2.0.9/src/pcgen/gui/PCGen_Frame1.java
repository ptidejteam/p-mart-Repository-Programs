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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.event.*;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.gui.ToolsNotes;
import pcgen.gui.ToolsQStats;
import pcgen.gui.ToolsCombat;

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
  static JLabel statusBar = new JLabel();

  /** 
   * Main tabbed panel of the application.
   * The first two tabs contain the {@link #mainCampaign}
   * ("Campaign") and {@link #tabDMTools} ("DM Tools")
   * panels.
   * Additional {@link CharacterInfo} panel tabs are added
   * for each created character.
   */
  static JTabbedPane baseTabbedPanel = new JTabbedPane();

  final JFileChooser fc = new JFileChooser();
  final PcgFilter filter = new PcgFilter();

  /**
   * Contains the campaign screen.
   * 
   * @see MainCampaign
   */
  MainCampaign mainCampaign = new MainCampaign();

  /**
   * Menu for the main application.
   */
  MenuItems menuBar = new MenuItems();

  /** 
   * Collection of loaded {@link CharacterInfo} panels; 
   * each is a view of a single character.
   * <p>
   * This essentially contains the same things as the additional
   * tabs in <code>baseTabbedPanel</code>. i.e. <br />
   * <code>baseTabbedPanel.getComponentAt(x) == characterList.elementAt(x - 2)</code>
   */
  Vector characterList = new Vector();

  // GUI stuff
  JPanel panelSouth = new JPanel();
  JPanel panelSouthEast = new JPanel();
  JPanel panelSouthCenter = new JPanel();
  
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
  
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  FlowLayout flowLayout4 = new FlowLayout();
  
  JComboBox jComboBox1 = new JComboBox();
  
  JTabbedPane tabMain = new JTabbedPane();
  
  //JPanel panelNorth = new JPanel();
  //JLabel lblName = new JLabel();
  //JPanel mainAbout = new MainAbout();


  // This used to be a tabbed pane, now
  // created in DebugFrame - no longer used?
  MainDebug mainDebug = new MainDebug();

  /**
   * The "DM Tools" tab on the main application screen.
   * Contains <code>toolsXP, toolsSetup, toolsCombat,
   * toolsQuick and toolsNotes</code>.
   */
  JTabbedPane tabDMTools = new JTabbedPane();

  // DM Tools content panels
  JPanel toolsXP = new JPanel();
  JPanel toolsSetup = new JPanel();
  ToolsCombat toolsCombat = new ToolsCombat();
  ToolsQStats toolsQuick = new ToolsQStats();
  ToolsNotes toolsNotes = new ToolsNotes();
  
  // Characters / core stuff
  int count = 0;


  /**
   * Override close and call <code>{@link #jbInit}</code> 
   * to initialise.
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

    fc.setFileFilter(filter);
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
    if (Globals.tabPlacement == 0)
      baseTabbedPanel.setTabPlacement(JTabbedPane.TOP);
    else if (Globals.tabPlacement == 1)
      baseTabbedPanel.setTabPlacement(JTabbedPane.LEFT);
    else if (Globals.tabPlacement == 2)
      baseTabbedPanel.setTabPlacement(JTabbedPane.BOTTOM);
    baseTabbedPanel.setDoubleBuffered(true);
    baseTabbedPanel.setMinimumSize(new Dimension(620, 350));
    baseTabbedPanel.setPreferredSize(new Dimension(620, 350));
    panelSouth.setLayout(borderLayout2);
    panelSouthEast.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    panelSouthCenter.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    toolsSetup.setLayout(borderLayout3);
    this.getContentPane().add(baseTabbedPanel, BorderLayout.CENTER);
    baseTabbedPanel.add(mainCampaign, "Campaign");

    //   baseTabbedPanel.add(mainDebug, "Debug");

    baseTabbedPanel.add(tabDMTools, "DM Tools");
    tabDMTools.add(toolsSetup, "Setup");
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
      enableImport(true);
    }
  }

  /**
   * Enable/disable the import button
   */
  protected void enableImport(boolean itemState)
  {
    menuBar.openItem.setEnabled(itemState);
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
		if (Globals.leftUpperCorner==null)
      Globals.leftUpperCorner = new Point(0,0);
    Globals.leftUpperCorner.setLocation(getLocationOnScreen().getX(), getLocationOnScreen().getY());

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
   * added to the <code>Globals.pcList</code>. The <code>CharacterInfo</code>
   * is added to both the <code>characterList</code>, and adds it to the main 
   * program frame, <code>baseTabbedPanel</code> as a new tab.
   */
  void newItem_actionPerformed(ActionEvent e)
  {
    // seize the focus to cause focus listeners to fire
    menuBar.newItem.requestFocus();
		try
		{
			PlayerCharacter aPC = new PlayerCharacter();
			Globals.pcList.add(aPC);
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
			JOptionPane.showMessageDialog (null, "Error creating character: " + e1.getMessage());
			e1.printStackTrace();
		}
		// aPC.setFileName(tabName + Globals.s_PCGEN_CHARACTER_EXTENSION);
  }

  /**
   * Load a character into a new <code>PlayerCharacter</code> model, and 
   * create a corresponding <code>CharacterInfo</code> panel. 
   *
   * @see #newItem_actionPerformed
   */
  void openItem_actionPerformed(ActionEvent event)
  {
    // seize the focus to cause focus listeners to fire
    menuBar.openItem.requestFocus();
    PlayerCharacter aPC = new PlayerCharacter();
    fc.setCurrentDirectory(Globals.getPcgPath());


    int returnVal = fc.showOpenDialog(PCGen_Frame1.this);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {
      File file = fc.getSelectedFile();
      Globals.setPcgPath(file.getParentFile());
      fc.setMultiSelectionEnabled(false);
      try
      {
        BufferedReader br = new BufferedReader(new FileReader(file));
        aPC.load(br);
        br.close();
        Globals.pcList.add(aPC);
        CharacterInfo character = new CharacterInfo();
        characterList.addElement(character);
        String tabName = aPC.name();
        baseTabbedPanel.add(character, tabName);
        baseTabbedPanel.setSelectedComponent(character);
        aPC.setFileName(file.getName());
      }
      catch (IOException ex)
      {
        JOptionPane.showMessageDialog(null, "Could not load file.");
        ex.printStackTrace();
      }
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
    // Globals.currentPC

    // txtName.requestFocus();
    int currentPanel = baseTabbedPanel.getSelectedIndex();
    if (currentPanel <= 1)
    {
      menuBar.closeItem.setEnabled(false);
      menuBar.closeItemAll.setEnabled(false);
      menuBar.saveItem.setEnabled(false);
      menuBar.saveItemAll.setEnabled(false);
      toolsQuick.refreshTable();
      toolsCombat.refreshTable();
    }
    else
    {
      menuBar.closeItem.setEnabled(true);
      menuBar.saveItem.setEnabled(true);
      Globals.currentPC = (PlayerCharacter) Globals.pcList.get(currentPanel - 2);
      CharacterInfo ci;
      ci = (CharacterInfo) characterList.elementAt(currentPanel - 2);

      JTabbedPane aPane = (JTabbedPane) ci.getComponent(0);
      int x = aPane.getComponentCount(); 
      // Domains/Spells either 5&6 or last two components
      if (Globals.starWarsMode)
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
    count = 0;
  }

  /**
   * Saves the character corresponding to the 
   * currently selected tab. The current character is
   * worked out by taking the <code>(tab position - 2)</code>,
   * and taking the corresponding character from the
   * <code>Globals.pcList</code>.
   */
  void saveItem_actionPerformed(ActionEvent e)
  {
    // seize the focus to cause focus listeners to fire
    menuBar.saveItem.requestFocus();
    int currTab = baseTabbedPanel.getSelectedIndex();
    fc.setFileFilter(filter);
    PlayerCharacter aPC = ((PlayerCharacter) Globals.pcList.get(currTab - 2));

    savePC(aPC);
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

		FilenameChangeListener (String aFileName)
		{
			lastSelName = aFileName;
		}

		public void propertyChange (PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();
			if (propName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
      	onSelectedFileChange (evt);
			else if (propName.equals (JFileChooser.DIRECTORY_CHANGED_PROPERTY))
				onDirectoryChange (evt);
		}

		private void onDirectoryChange (PropertyChangeEvent evt)
		{
			fc.setSelectedFile (new File (fc.getCurrentDirectory(), lastSelName));
		}

		private void onSelectedFileChange (PropertyChangeEvent evt)
		{
			File newSelFile = (File) evt.getNewValue();
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
      aPC.setFileName(aPC.name() + Globals.s_PCGEN_CHARACTER_EXTENSION);
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
        JOptionPane.showMessageDialog(null, "You cannot overwrite a directory with a character.");
        return;
      }

			if (file.exists() && (newPC || !file.equals (prevFile)))
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
        JOptionPane.showMessageDialog(null, "Could not save " + aPC.name());
        ex.printStackTrace();
      }
    }

  }

  /**
   * Saves all open characters. Iterates through
   * <code>Globals.pcList</code> (not the tabs) and runs
   * <code>savePC</code> for each one.
   */
  void saveItemAll_actionPerformed(ActionEvent e)
  {
    // seize the focus to cause focus listeners to fire
    menuBar.saveItemAll.requestFocus();
    PlayerCharacter aPC = null;
    int pcsToSave = Globals.pcList.size();

    while (pcsToSave > 0)
    {
      aPC = ((PlayerCharacter) Globals.pcList.get(pcsToSave - 1));
      //Should really check and only save pcs who are dirty, but...
      if (aPC.isDirty())
        savePC(aPC);
      pcsToSave--;
    }
  }

  /**
   * Close a tab by tab number, not pc number.
   *
   * @param  tab  Tab the character is on (not PC number)
   * @return      Not relevant - always <code>true</code>
   */
  private boolean closePCTab(int tab)
  {
    PlayerCharacter aPC = (PlayerCharacter) Globals.pcList.get(tab - 2);
    Globals.currentPC = aPC;
    if (aPC.isDirty())
    {
      int reallyClose = JOptionPane.showConfirmDialog(this,
        "The character " + aPC.name() + " has not been saved, are you sure you want to close it?",
        "Confirm closing " + aPC.name() + " without saving it.",
        JOptionPane.YES_NO_OPTION);
      if (reallyClose != JOptionPane.YES_OPTION)
      {
        return false;
      }
    }
    baseTabbedPanel.removeTabAt(tab);     //This should dispose of the character objects.
    Globals.pcList.remove(tab - 2);
    characterList.removeElementAt(tab - 2);     //The pc corresponding to the tab.
    Globals.currentPC = null;

    baseTabbedPanel.setSelectedIndex(0);     //This tab always exists...
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
    JMenuItem exitItem;

    /** Instantiated popup frame {@link AboutFrame}. */
    AboutFrame aboutFrame = null;
    
    /** Instantiated popup frame {@link DebugFrame}. */
    DebugFrame debugFrame = null;

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
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            PCGen_Frame1.this.newItem_actionPerformed(e);
          }
        });
      openItem = new JMenuItem("Open");
      fileMenu.add(openItem);
      openItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_O, InputEvent.CTRL_MASK));
      openItem.addActionListener(
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            PCGen_Frame1.this.openItem_actionPerformed(e);
          }
        });

      fileMenu.addSeparator();
      saveItem = new JMenuItem("Save");
      fileMenu.add(saveItem);
      saveItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_S, InputEvent.CTRL_MASK));
      saveItem.addActionListener(
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            PCGen_Frame1.this.saveItem_actionPerformed(e);
          }
        });
      saveItemAll = new JMenuItem("Save All");
      fileMenu.add(saveItemAll);
      saveItemAll.addActionListener(
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            PCGen_Frame1.this.saveItemAll_actionPerformed(e);
          }
        });
      closeItem = new JMenuItem("Close");
      fileMenu.add(closeItem);
      closeItem.addActionListener(
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            PCGen_Frame1.this.closeItem_actionPerformed(e);
          }
        });
      closeItemAll = new JMenuItem("Close All");
      fileMenu.add(closeItemAll);
      closeItemAll.addActionListener(
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            PCGen_Frame1.this.closeItemAll_actionPerformed(e);
          }
        });
      fileMenu.addSeparator();
      exitItem = new JMenuItem("Exit");
      fileMenu.add(exitItem);
      exitItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_Q, InputEvent.CTRL_MASK));
      exitItem.addActionListener(
        new java.awt.event.ActionListener()
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
      debugMode.setSelected(Globals.debugMode);
      debugMode.addActionListener(
        new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            Globals.debugMode = debugMode.isSelected();
            //baseTabbedPanel.add(mainDebug, "Debug");
            //baseTabbedPanel.setSelectedComponent(mainDebug);
          }
        });      
      debugMenu.add(debugMode);
      JMenuItem debugItem = new JMenuItem("Console");
      debugMenu.add(debugItem);
      debugItem.addActionListener(new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            if (debugFrame==null)
              debugFrame = new DebugFrame();
            debugFrame.setVisible(true);
          }
        });

      //Help Menu
      JMenu helpMenu = new JMenu("Help");
      JMenuItem aboutItem = new JMenuItem("About");
      helpMenu.add(aboutItem);
      aboutItem.addActionListener(new java.awt.event.ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            if (aboutFrame==null)
              aboutFrame= new AboutFrame();
            aboutFrame.setVisible(true);
          }
        });
      JMenuItem docItem = new JMenuItem("Docs");
      helpMenu.add(docItem);
      docItem.setEnabled(false);
      docItem.addActionListener(new java.awt.event.ActionListener()
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
      openItem.setEnabled(false);
      closeItem.setEnabled(false);
      closeItemAll.setEnabled(false);

      //set mnemonics
      fileMenu.setMnemonic('F');
      optionMenu.setMnemonic('O');
      debugMenu.setMnemonic('D');
      debugItem.setMnemonic('C');
      helpMenu.setMnemonic('H');
      aboutItem.setMnemonic('A');
      newItem.setMnemonic('N');
      openItem.setMnemonic('O');
      homeBrewMenu.setMnemonic('R');
      preferMenu.setMnemonic('P');
      modesMenu.setMnemonic('G');
    }

    /**
     * Popup frame with about info
     */
    private class AboutFrame extends JFrame
    {
      public AboutFrame()
      {
        super ("About");
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

        MainAbout mainAbout = new MainAbout();
        Container contentPane = getContentPane();
        contentPane.add(mainAbout);
        setVisible(true);
      }
    }//end AboutFrame

    /**
     * Popup frame with debug console
     */
    private class DebugFrame extends JFrame
    {
      public DebugFrame()
      {
        super ("Debug Console");
        ClassLoader loader = getClass().getClassLoader();
        Toolkit kit = Toolkit.getDefaultToolkit();
        // according to the API, the following should *ALWAYS* use '/'
        loader = null;
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
     * Pop up frame with Documentation.
     */
    private class DocFrame extends JFrame
    {
      public DocFrame()
      {
        super ("future html docs");
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
  
}
