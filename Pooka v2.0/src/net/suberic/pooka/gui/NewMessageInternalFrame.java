package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.gui.*;
import net.suberic.util.swing.EntryTextArea;
import net.suberic.util.swing.*;
import javax.swing.plaf.metal.MetalTheme;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.TextAction;
import java.util.*;
import javax.swing.text.JTextComponent;
import javax.swing.event.*;
import java.io.File;

/**
 * A window for entering new messages.
 */
public class NewMessageInternalFrame extends MessageInternalFrame implements NewMessageUI {

  public boolean firstShow = true;

  /**
   * Creates a NewMessageInternalFrame from the given Message.
   */
  public NewMessageInternalFrame(MessagePanel newParentContainer, NewMessageProxy newMsgProxy) {
    super(newParentContainer, newMsgProxy);

    configureMessageInternalFrame();

    /*
      this.addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
      if (getMessageDisplay() != null)
      getMessageDisplay().requestFocusInWindow();
      }
      });
    */

    FocusTraversalPolicy ftp = new LayoutFocusTraversalPolicy() {
        public Component getInitialComponent(JInternalFrame jif) {
          if (jif instanceof MessageInternalFrame) {
            return ((MessageInternalFrame) jif).getMessageDisplay();
          }

          return super.getInitialComponent(jif);
        }
      };
    this.setFocusTraversalPolicy(ftp);

    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent ife) {
          handleClose();
        }
      });
  }

  public NewMessageInternalFrame(MessagePanel newParentContainer, NewMessageFrame source) {
    parentContainer = newParentContainer;
    messageDisplay = source.getMessageDisplay();
    messageDisplay.setMessageUI(this);
    msg = source.getMessageProxy();
    toolbar = source.getToolbar();
    keyBindings = source.getKeyBindings();

    this.getContentPane().add("North", toolbar);
    this.getContentPane().add("Center", messageDisplay);

    toolbar.setActive(this.getActions());

    Point loc = source.getLocationOnScreen();
    SwingUtilities.convertPointFromScreen(loc, parentContainer);
    this.setLocation(loc);

    configureInterfaceStyle();

    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosing(InternalFrameEvent ife) {
          handleClose();
        }
      });

  }

  /**
   * This configures the MessageInternalFrame.  This means that here is
   * where we create the headerPanel and editorPane and add them to the
   * splitPane.
   */
  protected void configureMessageInternalFrame() {

    try {
      this.createDefaultActions();

      this.setTitle(Pooka.getProperty("Pooka.messageWindow.messageTitle.newMessage", "New Message"));

      messageDisplay = new NewMessageDisplayPanel(this);
      messageDisplay.configureMessageDisplay();

      toolbar = new ConfigurableToolbar("NewMessageWindowToolbar", Pooka.getResources());

      this.getContentPane().add("North", toolbar);
      this.getContentPane().add("Center", messageDisplay);

      toolbar.setActive(this.getActions());

      keyBindings = new ConfigurableKeyBinding(this, "NewMessageWindow.keyBindings", Pooka.getResources());
      keyBindings.setActive(getActions());
    } catch (OperationCancelledException oce) {
    } catch (MessagingException me) {
      showError(Pooka.getProperty("error.MessageInternalFrame.errorLoadingMessage", "Error loading Message:  ") + "\n" + me.getMessage(), Pooka.getProperty("error.MessageInternalFrame.errorLoadingMessage.title", "Error loading message."));
      me.printStackTrace();
    }

    configureInterfaceStyle();

  }

  /**
   * Gets the Theme object from the ThemeManager which is appropriate
   * for this UI.
   */
  public MetalTheme getTheme(ThemeManager tm) {
    UserProfile up = getSelectedProfile();
    if (up != null) {
      String id = Pooka.getProperty(up.getUserProperty() + ".theme", "");
      if (id != null && ! id.equals("")) {
        return tm.getTheme(id);
      }
    }

    return tm.getDefaultTheme();
  }

  /**
   * Closes the message window.  This checks to see if the underlying
   * message is modified, and if so, pops up a dialog to make sure that
   * you really want to close the window.
   *
   * Currently, saveDraft isn't implemented, so 'yes' acts as 'cancel'.
   */
  public void closeMessageUI() {
    try {
      this.setClosed(true);
    } catch (java.beans.PropertyVetoException e) {
    }

  }

  /**
   * Handles what happens when someone tries to close this window.
   */
  private void handleClose() {
    // first, make sure this is still a valid NewMessageUI.
    NewMessageProxy nmp = (NewMessageProxy)getMessageProxy();
    if (nmp == null || nmp.getNewMessageUI() != this) {
      setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
    } else {
      setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
      if (((NewMessageProxy)getMessageProxy()).promptForClose()) {
        int saveDraft = promptSaveDraft();
        switch (saveDraft) {
        case JOptionPane.YES_OPTION:
          ((NewMessageProxy)getMessageProxy()).saveDraft();
          break;
        case JOptionPane.NO_OPTION:
          NewMessageProxy.getUnsentProxies().remove(getMessageProxy());
          setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        default:
          return;
        }
      } else {
        NewMessageProxy.getUnsentProxies().remove(getMessageProxy());
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
      }

    }
  }

  /**
   * Prompts the user to see if she wants to save this message as a draft.
   *
   * If the message is not modified, returns JOptionPane.NO_OPTION.
   */
  public int promptSaveDraft() {
    if (isModified()) {
      return  showConfirmDialog(Pooka.getProperty("error.saveDraft.message", "This message has unsaved changes.  Would you like to save a draft copy?"), Pooka.getProperty("error.saveDraft.title", "Save Draft"), JOptionPane.YES_NO_CANCEL_OPTION);
    } else {
      return JOptionPane.NO_OPTION;
    }

  }

  public void detachWindow() {
    NewMessageFrame nmf = new NewMessageFrame(this);

    getMessageProxy().setMessageUI(nmf);

    nmf.setVisible(true);
    try {
      this.setClosed(true);
    } catch (java.beans.PropertyVetoException pve) {
    }
  }

  /**
   * This returns the values in the MesssageWindow as a set of
   * InternetHeaders.
   */
  public InternetHeaders getMessageHeaders() throws MessagingException {
    return getNewMessageDisplay().getMessageHeaders();
  }

  /**
   * This registers the Keyboard action not only for the FolderWindow
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.registerKeyboardAction(ActionListener anAction,
   *            String aCommand, KeyStroke aKeyStroke, int aCondition)
   */

  public void registerKeyboardAction(ActionListener anAction,
                                     String aCommand, KeyStroke aKeyStroke, int aCondition) {
    if (messageDisplay != null)
      messageDisplay.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    toolbar.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
  }

  /**
   * This unregisters the Keyboard action not only for the FolderWindow
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.unregisterKeyboardAction(KeyStroke aKeyStroke)
   */

  public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
    if (messageDisplay != null)
      messageDisplay.unregisterKeyboardAction(aKeyStroke);
    toolbar.unregisterKeyboardAction(aKeyStroke);
  }

  /**
   * This notifies the NewMessageUI that the attachment at the
   * provided index has been removed.  This does not actually remove
   * the attachment, but rather should be called by the MessageProxy
   * when an attachment has been removed.
   */
  public void attachmentRemoved(int index) {
    getNewMessageDisplay().attachmentRemoved(index);
  }

  /**
   * This notifies the NewMessageUI that an attachment has been added
   * at the provided index.  This does not actually add an attachment,
   * but rather should be called by the MessageProxy when an attachment
   * has been added.
   */
  public void attachmentAdded(int index) {
    getNewMessageDisplay().attachmentAdded(index);
  }

  /**
   * Pops up a JFileChooser and returns the results.
   *
   * Note:  i'd like to have this working so that you can attach multiple
   * files at once, but it seems that the JFileChooser really doesn't
   * want to return an array with anything in it for getSelectedFiles().
   * So for now, I'll leave the Pooka API as is, but only ever return a
   * single entry in the File array.
   */
  public File[] getFiles(String title, String buttonText) {
    JFileChooser jfc;
    String currentDirectoryPath = Pooka.getProperty("Pooka.tmp.currentDirectory", "");
    if (currentDirectoryPath == "")
      jfc = new JFileChooser();
    else
      jfc = new JFileChooser(currentDirectoryPath);

    jfc.setDialogTitle(title);
    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    jfc.setMultiSelectionEnabled(true);
    int a = jfc.showDialog(this, buttonText);

    Pooka.getResources().setProperty("Pooka.tmp.currentDirectory", jfc.getCurrentDirectory().getPath(), true);

    if (a == JFileChooser.APPROVE_OPTION)
      return jfc.getSelectedFiles();
    else
      return null;
  }

  /**
   * As specified by interface net.suberic.pooka.UserProfileContainer.
   *
   * This implementation returns the DefaultProfile of the associated
   * MessageProxy if the MessageInternalFrame is not editable.  If the
   * MessageInternalFrame is editable, it returns the currently selected
   * UserProfile object.
   */

  public UserProfile getDefaultProfile() {
    if (isEditable())
      return getSelectedProfile();
    else
      return getMessageProxy().getDefaultProfile();
  }

  /**
   * Shows an Address Selection form for the given AddressEntryTextArea.
   */
  public void showAddressWindow(AddressEntryTextArea aeta) {
    JInternalFrame jif = new JInternalFrame(Pooka.getProperty("AddressBookTable.title", "Choose Address"), true, true, true, true);
    jif.getContentPane().add(new AddressBookSelectionPanel(aeta, jif));
    jif.pack();

    getParentContainer().add(jif);
    jif.setLocation(getParentContainer().getNewWindowLocation(jif, true));

    jif.setVisible(true);
    try {
      jif.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) {
    }

  }

  /**
   * This method returns the UserProfile currently selected in the
   * drop-down menu.
   */

  public UserProfile getSelectedProfile() {
    return getNewMessageDisplay().getSelectedProfile();
  }

  /**
   * sets the currently selected Profile.
   */
  public void setSelectedProfile(UserProfile newProfile) {
    getNewMessageDisplay().setSelectedProfile(newProfile);
  }

  /**
   * Shows a SendFailedDialog.
   */
  public SendFailedDialog showSendFailedDialog(OutgoingMailServer server, javax.mail. MessagingException sfe) {
    // note that this should always be invoked on the AWTEventThread.
    SendFailedDialog sfd = new SendFailedDialog(server, sfe);
    sfd.configureComponent();
    //Pooka.getUIFactory().showConfirmDialog(new Object[] { sfd }, "Error sending message", 1);
    JOptionPane.showInternalMessageDialog((MessagePanel)Pooka.getMainPanel().getContentPanel(), new Object[] { sfd }, "Error sending message", JOptionPane.QUESTION_MESSAGE);
    return sfd;
  }

  /**
   * Overrides JComponent.addNotify().
   *
   * We override addNotify() here to set the proper splitPane location.
   */

  public void addNotify() {
    super.addNotify();

    if (firstShow) {
      getMessageDisplay().sizeToDefault();
      resizeByWidth();
      firstShow = false;
    }
  }

  public boolean isEditable() {
    return true;
  }

  public boolean isModified() {
    return getNewMessageDisplay().isModified();
  }

  public void setModified(boolean mod) {
    getNewMessageDisplay().setModified(mod);
  }

  /**
   * Sets this as busy or not busy.
   */
  public void setBusy(boolean newValue) {
    super.setBusy(newValue);

    final boolean fNewValue = newValue;
    Runnable runMe = new Runnable() {
        public void run() {
          setEnabled(! fNewValue);
        }
      };

    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }

  }

  /**
   * Sets this editor as enabled or disabled.
   */
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    toolbar.setEnabled(enabled);
    getNewMessageDisplay().setEnabled(enabled);
  }

  public NewMessageDisplayPanel getNewMessageDisplay() {
    return (NewMessageDisplayPanel) messageDisplay;
  }

  //------- Actions ----------//

  /**
   * performTextAction grabs the focused component on the
   * MessageInternalFrame and, if it is a JTextComponent, tries to get it
   * to perform the appropriate ActionEvent.
   */
  public void performTextAction(String name, ActionEvent e) {
    getNewMessageDisplay().performTextAction(name, e);
  }

  public Action[] getActions() {
    Action[] returnValue = getDefaultActions();

    if (getMessageDisplay() != null && getMessageDisplay().getActions() != null)
      returnValue = TextAction.augmentList(getMessageDisplay().getActions(), returnValue);

    return returnValue;
  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }


  private void createDefaultActions() {
    // The actions supported by the window itself.

    /*defaultActions = new Action[] {
      new CloseAction(),
      new CutAction(),
      new CopyAction(),
      new PasteAction(),
      new TestAction()
      };

      defaultActions = new Action[] {
      new CloseAction(),
      };
    */
  }

  //-----------actions----------------


  class CloseAction extends AbstractAction {

    CloseAction() {
      super("file-close");
    }

    public void actionPerformed(ActionEvent e) {
      closeMessageUI();
    }
  }

  class CutAction extends AbstractAction {

    CutAction() {
      super("cut-to-clipboard");
    }

    public void actionPerformed(ActionEvent e) {
      performTextAction((String)getValue(Action.NAME), e);
    }
  }

  class CopyAction extends AbstractAction {

    CopyAction() {
      super("copy-to-clipboard");
    }

    public void actionPerformed(ActionEvent e) {
      performTextAction((String)getValue(Action.NAME), e);
    }
  }

  class PasteAction extends AbstractAction {

    PasteAction() {
      super("paste-from-clipboard");
    }

    public void actionPerformed(ActionEvent e) {
      performTextAction((String)getValue(Action.NAME), e);
    }
  }

  class TestAction extends AbstractAction {

    TestAction() {
      super("test");
    }

    public void actionPerformed(ActionEvent e) {
      System.out.println(net.suberic.pooka.MailUtilities.wrapText(getMessageText()));
    }
  }

}





