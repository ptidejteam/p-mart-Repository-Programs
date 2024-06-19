package net.suberic.pooka.gui;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.StoreInfo;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountEvent;
import javax.help.*;
import java.util.logging.Logger;

import net.suberic.pooka.MailQueue;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.pooka.UserProfile;
import net.suberic.util.gui.*;

/**
 * The main panel for PookaMail
 *
 * @author  Allen Petersen
 * @version $Id$
 */

public class MainPanel extends JPanel implements net.suberic.pooka.UserProfileContainer, ActionContainer {
  private JSplitPane splitPane;
  private ConfigurableMenuBar mainMenu;
  private ConfigurableToolbar mainToolbar;
  private FolderPanel folderPanel;
  private ContentPanel contentPanel;
  private InfoPanel infoPanel;
  private Session session;
  private MailQueue mailQueue;
  private UserProfile currentUser = null;
  private ConfigurableKeyBinding keyBindings;

  protected PookaFocusManager focusManager;

  // status
  private static int CONTENT_LAST = 0;
  private static int FOLDER_LAST = 5;

  public MainPanel(JFrame frame) {
    session = Pooka.getDefaultSession();

    mailQueue = new MailQueue(Pooka.getDefaultSession());

  }

  /**
   * This actually sets up the main panel.
   */
  public void configureMainPanel() {
    // set supported actions
    // this.setLayout(new BorderLayout());
    // create the menu bar.

    this.setLayout(new BorderLayout());

    contentPanel = Pooka.getUIFactory().createContentPanel();
    folderPanel = new FolderPanel(this);
    infoPanel = new InfoPanel();
    infoPanel.setMessage("Pooka");

    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    splitPane.setLeftComponent(folderPanel);
    splitPane.setRightComponent(contentPanel.getUIComponent());
    splitPane.setDividerLocation(folderPanel.getPreferredSize().width + 1);

    this.add("Center", splitPane);

    mainMenu = new ConfigurableMenuBar("MenuBar", Pooka.getResources());
    mainToolbar = Pooka.getUIFactory().createMainToolbar();

    if (mainToolbar != null)
      this.add("North", mainToolbar);

    keyBindings = new ConfigurableKeyBinding(this, "MainPanel.keyBindings", Pooka.getResources());
    //keyBindings.setCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);
    keyBindings.setCondition(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    Pooka.getHelpBroker().enableHelpKey(this, "pooka.intro", Pooka.getHelpBroker().getHelpSet());

    getParentFrame().addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          exitPooka(true);
        }
      });

    focusManager = new PookaFocusManager();

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          // we never want focus.
          focusManager.passFocus();
        }
      });

    // set the initial currentUser
    refreshCurrentUser();

    // set up the MessageNotificationManager.
    if (Pooka.getUIFactory().getMessageNotificationManager() != null) {
      Pooka.getUIFactory().getMessageNotificationManager().setMainPanel(this);
    }
    // select the content panel.
    contentPanel.getUIComponent().requestFocusInWindow();

  }

  /**
   * This gets all the actions associated with this panel.  Useful for
   * populating the MenuBar and Toolbar.
   *
   * The method actually returns the Panel's defaultActions plus the
   * actions of the folderPanel and/or contentPanel, depending on which
   * one currently has the focus.
   */
  public Action[] getActions() {
    Action[] actions = getDefaultActions();
    Component focusedComponent = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

    boolean foundParent = false;

    if (focusedComponent != null) {
      if (contentPanel != null) {
        if (SwingUtilities.isDescendingFrom(focusedComponent, contentPanel.getUIComponent())) {
          foundParent = true;
          focusManager.setLastStatus(CONTENT_LAST);
          if (contentPanel.getActions() != null) {
            actions = TextAction.augmentList(contentPanel.getActions(), actions);
          }
        }
      }

      if (! foundParent && folderPanel != null) {
        if (SwingUtilities.isDescendingFrom(focusedComponent, folderPanel)) {
          foundParent = true;
          focusManager.setLastStatus(FOLDER_LAST);
          if (folderPanel.getActions() != null) {
            actions = TextAction.augmentList(folderPanel.getActions(), actions);
          }
        }
      }
    }

    if (! foundParent) {
      // if no parent is found, get the actions from the last selected
      // item.
      int lastStatus = focusManager.getLastStatus();
      if (lastStatus == CONTENT_LAST && contentPanel != null) {
        if (contentPanel.getActions() != null) {
          actions = TextAction.augmentList(contentPanel.getActions(), actions);
        }
      } else if (lastStatus == FOLDER_LAST && folderPanel != null) {
        if (folderPanel.getActions() != null) {
          actions = TextAction.augmentList(folderPanel.getActions(), actions);
        }

      }
    }
    return actions;
  }


  /**
   * Called by ExtendedDesktopManager every time the focus on the windows
   * changes.  Resets the Actions associated with the menu items and toolbar
   * to the ones in the active window.
   *
   * Also called when the selected message in a FolderWindow is changed.
   */

  public void refreshActiveMenus() {
    Action[] currentActions = getActions();
    mainMenu.setActive(currentActions);
    if (mainToolbar != null)
      mainToolbar.setActive(currentActions);
    contentPanel.refreshActiveMenus();
    keyBindings.setActive(currentActions);
    if (Pooka.getUIFactory().getMessageNotificationManager() != null)
      Pooka.getUIFactory().getMessageNotificationManager().clearNewMessageFlag();
  }

  /**
   * refreshCurrentUser() is called to get a new value for the currently
   * selected item.  In MainPanel, all it does is tries to get a
   * UserProfile from the currently selected object in the ContentPanel.
   * If there is no object in the ContentPanel which gives a default
   * UserProfile, it then checks the FolderPanel.  If neither of these
   * returns a UserProfile, then the default UserProfile is returned.
   */
  protected void refreshCurrentUser() {
    UserProfile selectedProfile = getDefaultProfile();
    if (selectedProfile != null) {
      currentUser = selectedProfile;
    } else {
      currentUser = Pooka.getPookaManager().getUserProfileManager().getDefaultProfile();
    }
  }

  /**
   * As defined in net.suberic.pooka.UserProfileContainer.
   *
   * Note that this method can return null, and is primarily used to
   * get the currentUser.  If you want to get the current default
   * profile, use getCurrentUser() instead.
   */
  public UserProfile getDefaultProfile() {
    UserProfile returnValue = null;

    if (contentPanel != null) {
      returnValue = contentPanel.getDefaultProfile();
    }

    if (returnValue != null)
      return returnValue;

    if (folderPanel != null)
      returnValue = folderPanel.getDefaultProfile();

    return returnValue;

  }

  public UserProfile getCurrentUser() {
    return currentUser;
  }

  /**
   * This exits Pooka.
   */
  public void exitPooka(boolean exitToIcon) {
    if (! processUnsentMessages())
      return;

    if (contentPanel instanceof MessagePanel &&
        ((MessagePanel)contentPanel).isSavingWindowLocations()) {
      ((MessagePanel)contentPanel).saveWindowLocations();
    }

    Pooka.setProperty("Pooka.hsize", Integer.toString(this.getParentFrame().getWidth()));
    Pooka.setProperty("Pooka.vsize", Integer.toString(this.getParentFrame().getHeight()));
    Pooka.setProperty("Pooka.folderPanel.hsize", Integer.toString(folderPanel.getWidth()));
    Pooka.setProperty("Pooka.folderPanel.vsize", Integer.toString(folderPanel.getHeight()));
    Pooka.setProperty("Pooka.lastX", Integer.toString(this.getParentFrame().getX()));
    Pooka.setProperty("Pooka.lastY", Integer.toString(this.getParentFrame().getY()));
    contentPanel.savePanelSize();

    if (contentPanel.isSavingOpenFolders()) {
      contentPanel.saveOpenFolders();
    }

    if (exitToIcon && Pooka.getProperty("Pooka.exitToIcon", "false").equalsIgnoreCase("true") && Pooka.getUIFactory().getMessageNotificationManager().getTrayIcon() != null) {
      Pooka.sStartupManager.stopPookaToTray(this);
    } else {
      Pooka.exitPooka(0, this);
    }
  }

  /**
   * Checks to see if there are any unsent messages.  If there are,
   * then find out if we want to save them as drafts, send them, or
   * forget them.
   *
   * Returns true if all of the messages are processed, false if the
   * user cancels out.
   */
  public boolean processUnsentMessages() {
    Vector unsentMessages = NewMessageProxy.getUnsentProxies();
    boolean cancel = false;
    Vector unsentCopy = new Vector(unsentMessages);
    for (int i = 0; !cancel && i < unsentCopy.size(); i++) {
      NewMessageProxy current = (NewMessageProxy)unsentCopy.get(i);
      if (current.promptForClose()) {
        NewMessageUI nmui = current.getNewMessageUI();
        // FIXME
        if (nmui != null) {
          nmui.openMessageUI();
          int saveDraft = nmui.promptSaveDraft();
          switch (saveDraft) {
          case JOptionPane.YES_OPTION:
            current.saveDraft();
            break;
          case JOptionPane.NO_OPTION:
            nmui.setModified(false);
            nmui.closeMessageUI();
            break;
          case JOptionPane.CANCEL_OPTION:
            cancel = true;
          }
        }
      }
    }

    return ! cancel;
  }

  // Accessor methods.
  // These shouldn't all be public.

  public ConfigurableMenuBar getMainMenu() {
    return mainMenu;
  }

  public InfoPanel getInfoPanel() {
    return infoPanel;
  }

  public void setMainMenu(ConfigurableMenuBar newMainMenu) {
    mainMenu=newMainMenu;
  }

  public ConfigurableToolbar getMainToolbar() {
    return mainToolbar;
  }

  public ConfigurableKeyBinding getKeyBindings() {
    return keyBindings;
  }

  public void setKeyBindings(ConfigurableKeyBinding newKeyBindings) {
    keyBindings = newKeyBindings;
  }


  public void setMainToolbar(ConfigurableToolbar newMainToolbar) {
    if (mainToolbar != null)
      this.remove(mainToolbar);

    mainToolbar = newMainToolbar;

    if (mainToolbar != null)
      this.add("North", mainToolbar);

  }

  public ContentPanel getContentPanel() {
    return contentPanel;
  }

  public void setContentPanel(ContentPanel newCp) {
    contentPanel = newCp;
    splitPane.setRightComponent(newCp.getUIComponent());
    this.repaint();
  }

  public FolderPanel getFolderPanel() {
    return folderPanel;
  }

  public Session getSession() {
    return session;
  }

  public MailQueue getMailQueue() {
    return mailQueue;
  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  public PookaFocusManager getFocusManager() {
    return focusManager;
  }

  /**
   * Find the hosting frame, for the file-chooser dialog.
   */
  public JFrame getParentFrame() {
    return (JFrame) getTopLevelAncestor();
  }

  //-----------actions----------------
  // Actions supported by the main Panel itself.  These should always
  // be available, even when no documents are open.

  private Action[] defaultActions = {
    new ExitAction(),
    new EditUserConfigAction(),
    new EditStoreConfigAction(),
    new EditPreferencesAction(),
    new EditAddressBookAction(),
    new EditOutgoingServerAction(),
    new EditConnectionAction(),
    new EditInterfaceAction(),
    new EditCryptoAction(),
    new EditAllPreferencesAction(),
    new HelpAboutAction(),
    new HelpLicenseAction(),
    new HelpAction(),
    new HelpKeyBindingsAction(),
    new SelectMessagePanelAction(),
    new SelectFolderPanelAction(),
    new NewMessageAction(),
    new ExportConfigAction()
  };


  /*
   * TODO:  This really needs to check and ask if you want to save any
   * modified documents.  Of course, we don't check to see if the docs
   * are modified yet, so this will do for now.
   */
  class ExitAction extends AbstractAction {

    ExitAction() {
      super("file-exit");
    }

    public void actionPerformed(ActionEvent e) {
      exitPooka(false);
    }
  }

  class ActivateWindowAction extends AbstractAction {

    ActivateWindowAction() {
      super("activate-window");
    }

    public void actionPerformed(ActionEvent e) {
      try {
        ((JInternalFrame)(((MessagePanel)contentPanel).getComponent(Integer.parseInt(e.getActionCommand())))).setSelected(true);
      } catch (java.beans.PropertyVetoException pve) {
      } catch (NumberFormatException nfe) {
      }
    }
  }

  class EditUserConfigAction extends AbstractAction {

    EditUserConfigAction() {
      super("cfg-users");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.userConfig", "Edit User Information"), "UserProfile.editor");
    }
  }


  class EditStoreConfigAction extends AbstractAction {

    EditStoreConfigAction() {
      super("cfg-stores");
    }

    public void actionPerformed(ActionEvent e) {

      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.storeConfig", "Edit Mailbox Information"), "Store");
    }
  }

  class EditPreferencesAction extends AbstractAction {

    EditPreferencesAction() {
      super("cfg-prefs");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.preferences", "Edit Preferences"), "Preferences");
    }
  }

  class EditAddressBookAction extends AbstractAction {

    EditAddressBookAction() {
      super("cfg-address-book");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.addressBook", "Address Book Editor"), "AddressBook.editor");
    }
  }

  class EditOutgoingServerAction extends AbstractAction {

    EditOutgoingServerAction() {
      super("cfg-outgoing");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.outgoingServer", "Outgoing Mail Editor"), "OutgoingServer.editor");
    }
  }

  class EditConnectionAction extends AbstractAction {

    EditConnectionAction() {
      super("cfg-connection");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.connectionEditor", "Connection Editor"), "Connection.editor");
    }
  }

  class EditCryptoAction extends AbstractAction {

    EditCryptoAction() {
      super("cfg-crypto");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.cryptoEditor", "Encryption Editor"), "EncryptionManager");
    }
  }

  class EditInterfaceAction extends AbstractAction {

    EditInterfaceAction() {
      super("cfg-interface-style");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.interfaceEditor", "User Interface Editor"), "Pooka.interface");
    }
  }

  class EditAllPreferencesAction extends AbstractAction {

    EditAllPreferencesAction() {
      super("cfg-all-prefs");
    }

    public void actionPerformed(ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("title.preferences", "Edit Preferences"), "Pooka.allPreferences");
    }
  }

  class HelpAboutAction extends AbstractAction {

    HelpAboutAction() {
      super("help-about");
    }

    public void actionPerformed(ActionEvent e) {
      String fileName="About.html";
      String dir="/net/suberic/pooka/doc";
      contentPanel.showHelpScreen(Pooka.getProperty("MenuBar.Help.About.Label", "About Pooka"), this.getClass().getResource(dir + "/" + java.util.Locale.getDefault().getLanguage() + "/" + fileName));

    }
  }

  class HelpLicenseAction extends AbstractAction {

    HelpLicenseAction() {
      super("help-license");
    }

    public void actionPerformed(ActionEvent e) {
      String fileName="COPYING";
      String dir="/net/suberic/pooka";
      contentPanel.showHelpScreen(Pooka.getProperty("MenuBar.Help.License.Label", "License"), this.getClass().getResource(dir + "/" + fileName));
    }
  }

  class HelpAction extends AbstractAction {

    HelpAction() {
      super("help");
    }

    public void actionPerformed(ActionEvent e) {
      new CSH.DisplayHelpFromSource(Pooka.getHelpBroker()).actionPerformed(e);
    }
  }

  class HelpKeyBindingsAction extends AbstractAction {

    HelpKeyBindingsAction() {
      super("help-keybindings");
    }

    public void actionPerformed(ActionEvent e) {
      /*
      String fileName="KeyBindings.html";
      String dir="/net/suberic/pooka/doc";
      contentPanel.showHelpScreen(Pooka.getProperty("MenuBar.Help.KeyBindings.Label", "Pooka KeyBindings"), this.getClass().getResource(dir + "/" + java.util.Locale.getDefault().getLanguage() + "/" + fileName));
      */
      Pooka.getHelpBroker().setCurrentID("keybindings");
      Pooka.getHelpBroker().setDisplayed(true);
    }
  }

  class SelectMessagePanelAction extends AbstractAction {

    SelectMessagePanelAction() {
      super("select-message-panel");
    }

    public void actionPerformed(ActionEvent e) {
      contentPanel.getUIComponent().requestFocusInWindow();
    }
  }

  class SelectFolderPanelAction extends AbstractAction {

    SelectFolderPanelAction() {
      super("select-folder-panel");
    }

    public void actionPerformed(ActionEvent e) {
      folderPanel.requestFocusInWindow();
    }
  }

  public class NewMessageAction extends AbstractAction {
    NewMessageAction() {
      super("message-new");
    }

    public void actionPerformed(ActionEvent e) {
      try {
        MessageUI nmu = Pooka.getUIFactory().createMessageUI(new NewMessageProxy(new net.suberic.pooka.NewMessageInfo(new javax.mail.internet.MimeMessage(getSession()))));
        nmu.openMessageUI();
      } catch (OperationCancelledException oce) {
      } catch (MessagingException me) {
        Pooka.getUIFactory().showError(Pooka.getProperty("error.NewMessage.errorLoadingMessage", "Error creating new message:  ") + "\n" + me.getMessage(), Pooka.getProperty("error.NewMessage.errorLoadingMessage.title", "Error creating new message."), me);
      }

    }

  }

  public class ExportConfigAction extends AbstractAction {
    ExportConfigAction() {
      super("cfg-export");
    }

    public void actionPerformed(ActionEvent e) {
      JFileChooser jfc;
      String currentDirectoryPath = Pooka.getProperty("Pooka.tmp.currentDirectory", "");
      if (currentDirectoryPath == "")
        jfc = new JFileChooser();
      else
        jfc = new JFileChooser(currentDirectoryPath);

      jfc.setDialogTitle("Choose Export File");
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jfc.setMultiSelectionEnabled(false);
      int a = jfc.showDialog(MainPanel.this, "Save");

      Pooka.getResources().setProperty("Pooka.tmp.currentDirectory", jfc.getCurrentDirectory().getPath(), true);

      if (a == JFileChooser.APPROVE_OPTION) {
        File f = jfc.getSelectedFile();
        try {
          net.suberic.pooka.resource.DisklessResourceManager.exportResources(f, false);
          Pooka.getUIFactory().showMessage("Resources exported successfully", "Export complete");
        } catch (Exception exc) {
          Pooka.getUIFactory().showError("Error exporting resources", exc);
        }

      }
    }

  }


  /**
   * Keeps the menus and configured Actions current by following the
   * keyboard focus.
   */
  public class PookaFocusManager implements PropertyChangeListener {

    int lastStatus = CONTENT_LAST;

    /**
     * Creates a new PookaFocusManager.
     */
    public PookaFocusManager() {
      KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
      kfm.addPropertyChangeListener("permanentFocusOwner", this);
    }

    /**
     * Called when the focus changes.
     */
    public void propertyChange(java.beans.PropertyChangeEvent pce) {
      // make sure that we're not doing anything stupid like saying that
      // either the JFrame or nothing has the keyboard focus.
      Object newValue = pce.getNewValue();
      Object oldValue = pce.getOldValue();
      // if seems like it, pass it on to a default.
      if ((newValue == null && oldValue == null) || newValue instanceof JFrame) {
        passFocus();
      } else {
        // see if the new value is actually part of this frame.
        if (newValue != null && newValue instanceof Component) {
          Window parentWindow = SwingUtilities.getWindowAncestor(MainPanel.this);
          Window componentParentWindow = SwingUtilities.getWindowAncestor((Component) newValue);
          if (parentWindow == componentParentWindow || componentParentWindow == null) {
            refreshActiveMenus();
            refreshCurrentUser();
          } else {

            java.util.logging.Logger.getLogger("Pooka.debug.gui.focus").fine("component " + newValue + " got focus, but it's not part of the main window.  Ignoring.");


            java.util.logging.Logger.getLogger("Pooka.debug.gui.focus").fine("main window = " + parentWindow  + "; component's parent = " + componentParentWindow);
          }
        }
      }
    }

    /**
     * Passes the focus to the correct subcomponent.
     */
    public void passFocus() {
      java.util.logging.Logger.getLogger("Pooka.debug.gui.focus").fine("passing focus to subcomponent.");
      if (lastStatus == CONTENT_LAST && contentPanel != null) {
        if (contentPanel instanceof JComponent)
          ((JComponent)contentPanel).requestFocusInWindow();
        else
          contentPanel.getUIComponent().requestFocusInWindow();
      } else if (lastStatus == FOLDER_LAST && folderPanel != null) {
        folderPanel.requestFocusInWindow();
      }
    }

    /**
     * Returns the last panel that had focus.
     */
    public int getLastStatus() {
      return lastStatus;
    }

    /**
     * Sets the last panel that had focus.
     */
    public void setLastStatus(int newStatus) {
      lastStatus = newStatus;
    }
  }
}
