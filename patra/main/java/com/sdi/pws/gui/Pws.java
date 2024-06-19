/*
Password Tracker (PATRA). An application to safely store your passwords.
Copyright (C) 2006-2009  Bruno Ranschaert, S.D.I.-Consulting BVBA.

For more information contact: nospam@sdi-consulting.com
Visit our website: http://www.sdi-consulting.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.sdi.pws.gui;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertRed;
import com.sdi.pws.codec.Codec2;
import com.sdi.pws.db.PwsDatabaseImpl;
import com.sdi.pws.db.PwsField;
import com.sdi.pws.db.PwsRecord;
import com.sdi.pws.gui.action.*;
import com.sdi.pws.gui.compo.db.change.ChangeViewDatabase;
import com.sdi.pws.gui.compo.db.change.ChangeViewField;
import com.sdi.pws.gui.compo.db.table.TableViewDatabase;
import com.sdi.pws.gui.compo.db.table.TableViewSelector;
import com.sdi.pws.gui.compo.db.tree.TreeViewDatabase;
import com.sdi.pws.gui.compo.db.tree.TreeViewSelector;
import com.sdi.pws.gui.compo.preferences.change.ChangeViewPreferences;
import com.sdi.pws.gui.dialog.start.Start;
import com.sdi.pws.preferences.PrefStorage;
import com.sdi.pws.preferences.Preferences;
import com.sdi.pws.preferences.PreferencesException;
import com.sdi.pws.preferences.PreferencesImpl;
import com.sdi.pws.util.SwinglibUtil;

import javax.help.CSH;
import javax.help.HelpBroker;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Pws
{
    public static void main(String[] aArgs)
    {
        // Create and show the GUI in the Swing thread.        
        try
        {
            SwingUtilities.invokeAndWait(
                    new Runnable()
                    {
                        public void run()
                        {
                            Pws.startgGui();
                        }
                    }
            );
        }
        catch(Exception e)
        {
            // Do nothing here.
            // We cannot react in a rational way here.
            e.printStackTrace();
        }
    }

    private static void startgGui()
    {
        // Set the look and feel.
        PlasticLookAndFeel.setPlasticTheme(new DesertRed());
        try { UIManager.setLookAndFeel(new PlasticLookAndFeel()); } catch (Exception ignored) {}

        // Change the default JOptionPane icons.
        UIManager.put("OptionPane.okIcon", new ImageIcon(Pws.class.getClassLoader().getResource("assets/go.png")));
        UIManager.put("OptionPane.cancelIcon", new ImageIcon(Pws.class.getClassLoader().getResource("assets/cancel.png")));

        // Load user preferences.
        final Preferences lPrefs = new PreferencesImpl();
        PrefStorage.loadPreferences(lPrefs);
        final ChangeViewPreferences lGlobalPreferences = new ChangeViewPreferences(lPrefs);

        // Application Frame.
        final JFrame lAppFrame = new JFrame();
        lAppFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        final ImageIcon lAppIcon = new ImageIcon(Pws.class.getClassLoader().getResource("assets/pwt-icon.gif"));
        lAppFrame.setIconImage(lAppIcon.getImage());

        // Application Menu Bar.
        final JMenuBar lAppMenu = new JMenuBar();
        lAppFrame.setJMenuBar(lAppMenu);

        // File menu.
        final JMenu lFileMenu = new JMenu(GuiUtil.getText("menu.file"));
        lFileMenu.setMnemonic('F');
        lAppMenu.add(lFileMenu);

        final JMenu lConvertMenu = new JMenu(GuiUtil.getText("menu.convert"));
        lConvertMenu.setMnemonic('C');
        final ImageIcon lConvertIcon = new ImageIcon(Pws.class.getClassLoader().getResource("assets/convert.png"));
        lConvertMenu.setIcon(lConvertIcon);

        final JMenu lImportMenu = new JMenu(GuiUtil.getText("menu.import"));
        lImportMenu.setMnemonic('I');
        // Install the icon.
        final ImageIcon lImportIcon = new ImageIcon(Pws.class.getClassLoader().getResource("assets/import.png"));
        lImportMenu.setIcon(lImportIcon);

        final JMenu lExportMenu = new JMenu(GuiUtil.getText("menu.export"));
        lExportMenu.setMnemonic('E');
        // Install the icon.
        final ImageIcon lExportIcon = new ImageIcon(Pws.class.getClassLoader().getResource("assets/export.png"));
        lExportMenu.setIcon(lExportIcon);

        // Password menu.
        final JMenu lPasswordMenu = new JMenu(GuiUtil.getText("menu.password"));
        lPasswordMenu.setMnemonic('P');
        lAppMenu.add(lPasswordMenu);

        // View menu.
        final JMenu lViewMenu = new JMenu(GuiUtil.getText("menu.view"));
        lViewMenu.setMnemonic('V');
        lAppMenu.add(lViewMenu);

        // Help menu.
        final JMenu lHelpMenu = new JMenu(GuiUtil.getText("menu.help"));
        lHelpMenu.setMnemonic('H');
        lAppMenu.add(lHelpMenu);
        // Listen for help.
        final JMenuItem lHelpItem = new JMenuItem(GuiUtil.getText("menuitem.help"), new ImageIcon(Pws.class.getClassLoader().getResource("assets/help.png")));
        final HelpBroker lBroker = GuiUtil.getHelpBroker();
        lBroker.setCurrentID("intro_html");
        lHelpItem.addActionListener(new CSH.DisplayHelpFromSource(lBroker));
        lHelpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        lHelpMenu.add(lHelpItem);

        // Vertical button panel size.
        final int lButtonPanelWidth = 27;

        // Left button panel.
        final JPanel lLeftButtonPanel = new JPanel();
        lLeftButtonPanel.setLayout(new GridLayout(2, 1));
        lLeftButtonPanel.setMaximumSize(new Dimension(lButtonPanelWidth, 0));
        lLeftButtonPanel.setMinimumSize(new Dimension(lButtonPanelWidth, 0));
        lLeftButtonPanel.setPreferredSize(new Dimension(lButtonPanelWidth, 0));
        lAppFrame.getRootPane().getContentPane().add(lLeftButtonPanel, BorderLayout.WEST);
        // Button panel buttons.
        JButton lLeftCopyUID = new JButton();
        lLeftButtonPanel.add(lLeftCopyUID);
        JButton lLeftCopyPwd = new JButton();
        lLeftButtonPanel.add(lLeftCopyPwd);
        // Set startup visibility.
        try{lLeftButtonPanel.setVisible(lGlobalPreferences.getBoolPref(Preferences.PREF_BUTTONS_LEFT));}catch(PreferencesException ignored){}
        // Set dynamic visibility.
        lGlobalPreferences.addPropertyChangeListener(Preferences.PREF_BUTTONS_LEFT, new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                try{lLeftButtonPanel.setVisible(lGlobalPreferences.getBoolPref(Preferences.PREF_BUTTONS_LEFT));}catch(PreferencesException ignored){}
            }
        });

        // Right button panel.
        final JPanel lRightButtonPanel = new JPanel();
        lRightButtonPanel.setLayout(new GridLayout(2, 1));
        lRightButtonPanel.setMaximumSize(new Dimension(lButtonPanelWidth, 0));
        lRightButtonPanel.setMinimumSize(new Dimension(lButtonPanelWidth, 0));
        lRightButtonPanel.setPreferredSize(new Dimension(lButtonPanelWidth, 0));
        lAppFrame.getRootPane().getContentPane().add(lRightButtonPanel, BorderLayout.EAST);
        // Button panel buttons.
        JButton lRightCopyUID = new JButton();
        lRightButtonPanel.add(lRightCopyUID);
        JButton lRightCopyPWD = new JButton();
        lRightButtonPanel.add(lRightCopyPWD);
        // Set startup visibility.
        try{lRightButtonPanel.setVisible(lGlobalPreferences.getBoolPref(Preferences.PREF_BUTTONS_RIGHT));}catch(PreferencesException ignored){}
        // Set dynamic visibility.
        lGlobalPreferences.addPropertyChangeListener(Preferences.PREF_BUTTONS_RIGHT, new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                try{lRightButtonPanel.setVisible(lGlobalPreferences.getBoolPref(Preferences.PREF_BUTTONS_RIGHT));}catch(PreferencesException eIgnore){}
            }
        });

        // View stack.
        final JPanel lViews = new JPanel();
        final CardLayout lViewLayout = new CardLayout();
        lViews.setLayout(lViewLayout);
        lAppFrame.getRootPane().getContentPane().add(lViews, BorderLayout.CENTER);

        // Table view.
        final JTable lTableView = new JTable();
        lTableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        final JScrollPane lTableViewScroll = new JScrollPane(lTableView);
        final FilteredCellRenderer lFilteredRenderer = new FilteredCellRenderer();
        lTableView.setDefaultRenderer(ChangeViewField.class, lFilteredRenderer);
        final Box lTableBox = Box.createVerticalBox();
        // Create the filter panel.
        final JPanel lFilterPanel = new JPanel();
        lFilterPanel.setLayout(new BorderLayout());
        final JLabel lFilterLabel = new JLabel(new ImageIcon(Start.class.getClassLoader().getResource("assets/loep.png"))) ;
        lFilterLabel.setBorder(new EmptyBorder(1, 1, 1, 5));
        lFilterPanel.setBackground(Color.white);
        lFilterPanel.add(lFilterLabel, BorderLayout.WEST);
        final JTextField lTableFilter = new JTextField();
        lFilterPanel.add(lTableFilter, BorderLayout.CENTER);
        lFilterPanel.setBorder(lTableFilter.getBorder());
        lTableFilter.setBorder(null);
        // Force fixed height filter box. Widht can vary, but height should remain the same.
        lFilterPanel.setMaximumSize(new Dimension(10000, 50));
        lTableBox.add(lFilterPanel);
        lTableBox.add(lTableViewScroll);
        lViews.add(lTableBox, Preferences.VIEW_TABLE);

        // Tree view.
        final JTree lTreeView = new JTree();
        lTreeView.setBorder(new EmptyBorder(3, 3, 3, 3));
        final JScrollPane lTreeViewScroll = new JScrollPane(lTreeView);
        lViews.add(lTreeViewScroll, Preferences.VIEW_TREE);

        // Select current view and initialize view name holder.
        final String lDefaultView = lGlobalPreferences.getPref(Preferences.PREF_DEFAULT_VIEW);
        final StringHolder lViewName = new StringHolderImpl(lDefaultView);
        lViewLayout.show(lViews, lDefaultView);

        // Initialize a default empty database.
        final ChangeViewDatabase lDb = new ChangeViewDatabase(new PwsDatabaseImpl());
        lDb.setCodec(new Codec2());
        lDb.setChanged(false);
        final DatabaseHolder lDbHolder = new DatabaseHolderImpl(lDb);

        // Application title.
        final TitleRenderer lTitlemgr = new TitleRenderer(lAppFrame, lDbHolder);

        // Init the table view. Several wrappers are added for additional table functionality.
        final TableViewDatabase lTableModel = new TableViewDatabase(lDb);
        final SortableTableModel lSortedTableModel = new SortableTableModel(lTableModel);
        final FilteredTableModel lFilteredTableModel = new FilteredTableModel(lSortedTableModel);
        final TreeViewDatabase  lTreeModel = new TreeViewDatabase(lDb);
        lTableView.setModel(lFilteredTableModel);
        lTreeView.setModel(lTreeModel);
        // Add a filter feedback.
        lTableFilter.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e) {doit(); }
            public void keyReleased(KeyEvent e) { doit(); }
            public void keyTyped(KeyEvent e) { }
            private void doit()
            {
                // Apply the search pattern to the table model.
                final String lSearchString = lTableFilter.getText();
                lFilteredRenderer.setSearchString(lSearchString);
                lFilteredTableModel.search(lSearchString);
                // If there is one and only one row left in the table, we automatically select it.
                if(lFilteredTableModel.getRowCount() == 1) lTableView.getSelectionModel().setSelectionInterval(0,0);
            }
        });

        // Selector.
        final TableViewSelector lTableSelector = new TableViewSelector(lFilteredTableModel, lTableView);
        final TreeViewSelector lTreeSelector = new TreeViewSelector(lTreeView);
        final DynamicRecordSelector lRecordSelector = new DynamicRecordSelector((("tree".equals(lViewName))? lTreeSelector : lTableSelector));

        lDbHolder.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent evt)
            {
                // Get the new plain database.
                final ChangeViewDatabase lCurrentDb = lDbHolder.getCurrentDatabase();

                // Give it to the models.
                lTableModel.setDatabase(lCurrentDb);
                lTreeModel.setDatabase(lCurrentDb);
            }
        });

        // Actions.
        final ClipboardMonitor lMonitor = new ClipboardMonitor();
        final Action lCopyUidAction = new CopyUid(lRecordSelector, lMonitor);
        lPasswordMenu.add(lCopyUidAction).setAccelerator(KeyStroke.getKeyStroke('U', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        lLeftCopyUID.setAction(new ActionNoName(lCopyUidAction));
        lRightCopyUID.setAction(new ActionNoName(lCopyUidAction));

        final Action lCopyPwdAction = new CopyPwd(lRecordSelector, lMonitor);
        lPasswordMenu.add(lCopyPwdAction).setAccelerator(KeyStroke.getKeyStroke('P', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        lLeftCopyPwd.setAction(new ActionNoName(lCopyPwdAction));
        lRightCopyPWD.setAction(new ActionNoName(lCopyPwdAction));

        final Action lClearClipboardAction = new ClearClipboard(lMonitor);
        lPasswordMenu.add(lClearClipboardAction).setAccelerator(KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        final Action lOpenFileAction = new FileOpen(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lFileMenu.add(lOpenFileAction).setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        final Action lSaveFileAction = new FileSave(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lFileMenu.add(lSaveFileAction).setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        final Action lSaveAsFileAction = new FileSaveAs(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lFileMenu.add(lSaveAsFileAction).setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        final Action lNewFileAction = new FileNew(lAppFrame.getRootPane(), lDbHolder);
        lFileMenu.add(lNewFileAction).setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        final Action lChangePassphraseAction = new PassphraseChange(lAppFrame, lDbHolder, lGlobalPreferences);
        lFileMenu.add(lChangePassphraseAction);

        lFileMenu.addSeparator();
        lFileMenu.add(lConvertMenu);
        lFileMenu.add(lImportMenu);
        lFileMenu.add(lExportMenu);

        final Action lAppPrefsAction = new AppPrefsEdit(lAppFrame.getRootPane(), lGlobalPreferences);
        lFileMenu.add(lAppPrefsAction);

        final Action lAppExitAction = new AppExit(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lFileMenu.addSeparator();
        lFileMenu.add(lAppExitAction).setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
        lAppFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                lAppExitAction.actionPerformed(new ActionEvent(lAppFrame, 0, null));
            }
        });

        final Action lEditEntryAction = new EntryEdit(lAppFrame.getRootPane(), lRecordSelector, lDbHolder, lGlobalPreferences);
        lPasswordMenu.addSeparator();
        lPasswordMenu.add(lEditEntryAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
        // Force the Enter key on the table and tree to do an edit.
        // If we don't force this, the enter will have the same effect as walking through the list/tree.
        lTableView.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "EDIT");
        lTableView.getActionMap().put("EDIT", lEditEntryAction);
        lTreeView.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "EDIT");
        lTreeView.getActionMap().put("EDIT", lEditEntryAction);

        final Action lNewEntryAction = new EntryNew(lAppFrame.getRootPane(), lTreeModel, lGlobalPreferences);
        lPasswordMenu.add(lNewEntryAction).setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));

        // Force the CTRL-A key on the table and tree to do a new.
        // If we don't force this, the CTRL-A will have the same effect as selecting all entries.
        lTableView.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), "NEW");
        lTableView.getActionMap().put("NEW", lNewEntryAction);
        lTreeView.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), "NEW");
        lTreeView.getActionMap().put("NEW", lNewEntryAction);

        final Action lDeleteEntryAction = new EntryDelete(lAppFrame.getRootPane(), lTreeModel, lRecordSelector);
        lPasswordMenu.add(lDeleteEntryAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));

        final int lTextGap = 3;

        final Action lTableViewAction = new ViewTable(lViews, lRecordSelector, lTableSelector, lViewName);
        final JRadioButton lTableButton = new JRadioButton(lTableViewAction);
        lTableButton.setIconTextGap(lTextGap);
        lTableButton.setSelected(!lTableViewAction.isEnabled());
        lTableViewAction.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent)
            {
                lTableButton.setSelected(!lTableViewAction.isEnabled());
            }
        });
        lViewMenu.add(lTableButton);

        final Action lTreeViewAction = new ViewTree(lViews, lRecordSelector, lTreeSelector, lViewName);
        final JRadioButton lTreeButton = new JRadioButton(lTreeViewAction);
        lTreeButton.setIconTextGap(lTextGap);
        lTreeButton.setSelected(!lTreeViewAction.isEnabled());
        lTreeButton.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent)
            {
                lTreeButton.setSelected(!lTreeViewAction.isEnabled());
            }
        });
        lViewMenu.add(lTreeButton);

        // The stay on top option.
        final JCheckBox lStayPut =  new JCheckBox(GuiUtil.getText("action.stayontop"));
        try { lStayPut.setSelected(lGlobalPreferences.getBoolPref(Preferences.PREF_STAY_ON_TOP)); } catch(PreferencesException e) { }
        lStayPut.setIconTextGap(lTextGap);
        lViewMenu.add(lStayPut);
        lAppFrame.setAlwaysOnTop(lStayPut.isSelected());
        lStayPut.addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent aPropertyChangeEvent)
            {
                lAppFrame.setAlwaysOnTop(lStayPut.isSelected());
            }
        });

        // Add right mouse button popup to the table view.
        lTableView.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e) { checkPopup(e); }
            public void mouseClicked(MouseEvent e) { checkPopup(e); }
            public void mouseReleased(MouseEvent e) { checkPopup(e); }
            private void checkPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    // See if we have a selection.
                    final PwsRecord lRecord = lRecordSelector.getSelectedRecord();

                    // Create an array of URL actions if there any URL's hidden in the notes field.
                    // URL actions are only possible if there is a selected record.
                    Action[] lUrlActions = null;
                    String lNotes = null;
                    if(lRecord != null && lRecord.hasType(PwsField.FIELD_NOTES))
                    {
                        try { lNotes = lRecord.get(PwsField.FIELD_NOTES).getAsString(); } catch(Exception eIgnore) { }
                        java.util.List<String> lUrls = SwinglibUtil.extractUrl(lNotes);
                        if(lUrls.size() > 0)
                        {
                            lUrlActions = new Action[lUrls.size()];
                            for(int i = 0; i < lUrls.size(); i++) lUrlActions[i] = new BrowseUrl(lUrls.get(i));
                        }
                    }
                    // Create the popup menu dynamically.
                    JPopupMenu lPop;

                        lPop = SwinglibUtil.popupBuilder(new Action[]{lCopyUidAction, lCopyPwdAction, lClearClipboardAction},
                                                         new Action[]{lEditEntryAction, lNewEntryAction, lDeleteEntryAction},
                                                         lUrlActions);
                    // Show the popup to the user.
                    lPop.show(lTableView, e.getX(), e.getY());
                }
            }
        });
        // Add right mouse button popup.
        lTreeView.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e) { checkPopup(e); }
            public void mouseClicked(MouseEvent e) { checkPopup(e); }
            public void mouseReleased(MouseEvent e) { checkPopup(e); }
            private void checkPopup(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {

                // See if we have a selection.
                    final PwsRecord lRecord = lRecordSelector.getSelectedRecord();

                    // Create an array of URL actions if there any URL's hidden in the notes field.
                    // URL actions are only possible if there is a selected record.
                    Action[] lUrlActions = null;
                    String lNotes = null;
                    if(lRecord != null && lRecord.hasType(PwsField.FIELD_NOTES))
                    {
                        try { lNotes = lRecord.get(PwsField.FIELD_NOTES).getAsString(); } catch(Exception eIgnore) { }
                        java.util.List<String> lUrls = SwinglibUtil.extractUrl(lNotes);
                        if(lUrls.size() > 0)
                        {
                            lUrlActions = new Action[lUrls.size()];
                            for(int i = 0; i < lUrls.size(); i++) lUrlActions[i] = new BrowseUrl(lUrls.get(i));
                        }
                    }
                    // Create the popup menu dynamically.
                    JPopupMenu lPop;
                    lPop = SwinglibUtil.popupBuilder(new Action[]{lCopyUidAction, lCopyPwdAction, lClearClipboardAction},
                                                     new Action[]{lEditEntryAction, lNewEntryAction, lDeleteEntryAction},
                                                     lUrlActions);

                    // Show the popup to the user.
                    lPop.show(lTreeView, e.getX(), e.getY());
                }
            }
        });

        final Action lFileUpgradeAction = new FileVersionUpgrade(lAppFrame.getRootPane(),lDbHolder, lGlobalPreferences);
        lConvertMenu.add(lFileUpgradeAction);

        final Action lFiledowngradeAction = new FileVersionDowngrade(lAppFrame.getRootPane(),lDbHolder);
        lConvertMenu.add(lFiledowngradeAction);

        final Action lFileExportCsvAction = new FileCsvExport(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lExportMenu.add(lFileExportCsvAction);

        final Action lFileImportCsvAction = new FileCsvImport(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lImportMenu.add(lFileImportCsvAction);

        final Action lFileImportPwsAction = new FilePwsImport(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lImportMenu.add(lFileImportPwsAction);

        final Action lFileExportXmlAction = new FileXmlExport(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lExportMenu.add(lFileExportXmlAction);

        final Action lFileImportXmlAction = new FileXmlImport(lAppFrame.getRootPane(), lDbHolder, lGlobalPreferences);
        lImportMenu.add(lFileImportXmlAction);

        final Action lAboutAction = new AppAbout(lAppFrame.getRootPane());
        lHelpMenu.add(lAboutAction);

        // Start screen.
        Start.startPws(lAppFrame, lDbHolder, lGlobalPreferences);

        // Start the GUI.
        lAppFrame.pack();
        GuiUtil.centerComponent(lAppFrame);
        lAppFrame.setVisible(true);
    }
}
