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
public class NewMessageFrame extends MessageFrame implements NewMessageUI {

  public boolean firstShow = true;

  /**
   * Creates a NewMessageFrame from the given Message.
   */

  public NewMessageFrame(NewMessageProxy newMsgProxy) {
    super(newMsgProxy);

    configureMessageFrame();

    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
          handleClose();
        }
      });

    FocusTraversalPolicy ftp = new LayoutFocusTraversalPolicy() {
        public Component getInitialComponent(Window w) {
          if (w instanceof MessageFrame) {
            return ((MessageFrame) w).getMessageDisplay();
          }

          return super.getInitialComponent(w);
        }
      };
    this.setFocusTraversalPolicy(ftp);

  }

  public NewMessageFrame(NewMessageInternalFrame source) {
    this.setTitle(Pooka.getProperty("Pooka.messageWindow.messageTitle.newMessage", "New Message"));
    messageDisplay = source.getMessageDisplay();
    messageDisplay.setMessageUI(this);
    msg = source.getMessageProxy();
    toolbar = source.getToolbar();
    keyBindings = source.getKeyBindings();

    this.getContentPane().add("North", toolbar);
    this.getContentPane().add("Center", messageDisplay);

    toolbar.setActive(this.getActions());

    configureInterfaceStyle();

    this.setLocation(source.getLocationOnScreen());

    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent we) {
          handleClose();
        }
      });

    FocusTraversalPolicy ftp = new LayoutFocusTraversalPolicy() {
        public Component getInitialComponent(Window w) {
          if (w instanceof MessageFrame) {
            return ((MessageFrame) w).getMessageDisplay();
          }

          return super.getInitialComponent(w);
        }
      };
    this.setFocusTraversalPolicy(ftp);

  }

  /**
   * This configures the MessageFrame.  This means that here is
   * where we create the headerPanel and editorPane and add them to the
   * splitPane.
   */
  protected void configureMessageFrame() {

    try {
      this.createDefaultActions();

      this.setTitle(Pooka.getProperty("Pooka.messageWindow.messageTitle.newMessage", "New Message"));

      messageDisplay = new NewMessageDisplayPanel(this);
      messageDisplay.configureMessageDisplay();

      toolbar = new ConfigurableToolbar("NewMessageWindowToolbar", Pooka.getResources());

      this.getContentPane().add("North", toolbar);
      this.getContentPane().add("Center", messageDisplay);

      toolbar.setActive(this.getActions());

      keyBindings = new ConfigurableKeyBinding(getMessageDisplay(), "NewMessageWindow.keyBindings", Pooka.getResources());
      //keyBindings.setCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);

      keyBindings.setActive(getActions());
    } catch (OperationCancelledException oce) {

    } catch (MessagingException me) {
      showError(Pooka.getProperty("error.MessageFrame.errorLoadingMessage", "Error loading Message:  ") + "\n" + me.getMessage(), Pooka.getProperty("error.MessageFrame.errorLoadingMessage.title", "Error loading message."));
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
    System.err.println("running closeMessageUI().");
    handleClose();
  }

  private void handleClose() {
    System.err.println("running handleClose().");
    // first, make sure this is still a valid NewMessageUI.
    NewMessageProxy nmp = (NewMessageProxy)getMessageProxy();
    if (nmp == null)
      dispose();

    if (nmp.getNewMessageUI() == this) {
      if (((NewMessageProxy)getMessageProxy()).promptForClose()) {
        int saveDraft = promptSaveDraft();
        switch (saveDraft) {
        case JOptionPane.YES_OPTION:
          ((NewMessageProxy)getMessageProxy()).saveDraft();
          break;
        case JOptionPane.NO_OPTION:
          NewMessageProxy.getUnsentProxies().remove(getMessageProxy());
          dispose();
          break;
        default:
          return;
        }
      } else {
        NewMessageProxy.getUnsentProxies().remove(getMessageProxy());
        dispose();
      }
    } else {
      dispose();
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

  /**
   * Reattaches the window to the MessagePanel, if there is one.
   */
  public void attachWindow() {
    if (Pooka.getMainPanel().getContentPanel() instanceof MessagePanel) {
      MessagePanel mp = (MessagePanel) Pooka.getMainPanel().getContentPanel();
      NewMessageInternalFrame nmif = new NewMessageInternalFrame(mp, this);
      getMessageProxy().setMessageUI(nmif);
      nmif.openMessageUI();
      this.setModified(false);
      this.dispose();
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
   * Shows an Address Selection form for the given AddressEntryTextArea.
   */
  public void showAddressWindow(AddressEntryTextArea aeta) {
    JFrame jf = new JFrame(Pooka.getProperty("AddressBookTable.title", "Choose Address"));
    jf.getContentPane().add(new AddressBookSelectionPanel(aeta, jf));
    jf.pack();
    jf.setVisible(true);
  }

  /**
   * As specified by interface net.suberic.pooka.UserProfileContainer.
   *
   * This implementation returns the DefaultProfile of the associated
   * MessageProxy if the MessageFrame is not editable.  If the
   * MessageFrame is editable, it returns the currently selected
   * UserProfile object.
   */
  public UserProfile getDefaultProfile() {
    if (isEditable())
      return getSelectedProfile();
    else
      return getMessageProxy().getDefaultProfile();
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
   * Overrides JComponent.addNotify().
   *
   * We override addNotify() here to set the proper splitPane location.
   */
  public void addNotify() {
    super.addNotify();

    if (firstShow) {
      messageDisplay.sizeToDefault();
      resizeByWidth();
      firstShow = false;
    }
  }

  /**
   * Shows a SendFailedDialog.
   */
  public SendFailedDialog showSendFailedDialog(OutgoingMailServer server, javax.mail. MessagingException sfe) {
    // note this should always be called on the AWTEventThread.
    SendFailedDialog sfd = new SendFailedDialog(server, sfe);
    sfd.configureComponent();
    JOptionPane.showMessageDialog(Pooka.getMainPanel(), new Object[] { sfd }, "Error sending message", JOptionPane.QUESTION_MESSAGE);
    //Pooka.getUIFactory().showConfirmDialog(new Object[] { sfd }, "Error sending message", 1);
    return sfd;
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
   * MessageFrame and, if it is a JTextComponent, tries to get it
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
      };*/

    defaultActions = new Action[] {
      new CloseAction(),
    };
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





