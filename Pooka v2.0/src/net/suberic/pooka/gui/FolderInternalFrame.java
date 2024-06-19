package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.pooka.gui.search.SearchForm;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.event.*;
import javax.mail.search.SearchTerm;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;
import javax.swing.text.TextAction;
import java.util.*;
import net.suberic.pooka.event.MessageLoadedEvent;
import net.suberic.util.gui.*;
import net.suberic.util.event.*;
import net.suberic.util.thread.*;
import net.suberic.util.swing.*;

/**
 * This basically is just the GUI representation of the Messages in
 * a Folder.  Most of the real work is done by the FolderInfo
 * class.  Also, most of the display is done by the FolderDisplayPanel.
 */

public class FolderInternalFrame extends JInternalFrame implements FolderDisplayUI {
  FolderInfo folderInfo = null;
  FolderDisplayPanel folderDisplay = null;
  FolderStatusBar folderStatusBar = null;
  MessagePanel messagePanel = null;
  ConfigurableToolbar toolbar;
  ConfigurableKeyBinding keyBindings;
  boolean enabled = true;

  /**
   * Creates a Folder window from the given Folder.
   */
  public FolderInternalFrame(FolderInfo newFolderInfo, MessagePanel newMessagePanel) {
    super(newFolderInfo.getFolderDisplayName(), true, true, true, true);

    this.getContentPane().setLayout(new BorderLayout());

    messagePanel = newMessagePanel;

    setFolderInfo(newFolderInfo);

    getFolderInfo().setFolderDisplayUI(this);

    defaultActions = new Action[] {
      new CloseAction(),
      new ActionWrapper(new ExpungeAction(), getFolderInfo().getFolderThread()),
      new NextMessageAction(),
      new PreviousMessageAction(),
      new NextUnreadMessageAction(),
      new GotoMessageAction(),
      new SearchAction(),
      new SelectAllAction()
    };

    // note:  you have to set the Status Bar before you create the
    // FolderDisplayPanel, or else you'll get a null pointer exception
    // from the LoadMessageThread.

    setFolderStatusBar(new FolderStatusBar(this.getFolderInfo()));

    folderDisplay = new FolderDisplayPanel(getFolderInfo());

    if (getFolderInfo() != null && getFolderInfo().isOutboxFolder())
      toolbar = new ConfigurableToolbar("OutboxWindowToolbar", Pooka.getResources());
    else
      toolbar = new ConfigurableToolbar("FolderWindowToolbar", Pooka.getResources());
    this.getContentPane().add("North", toolbar);
    this.getContentPane().add("Center", folderDisplay);
    this.getContentPane().add("South", getFolderStatusBar());

    int height = Integer.parseInt(Pooka.getProperty(getFolderInfo().getFolderProperty() + ".windowLocation.height", Pooka.getProperty("FolderWindow.height", "380")));
    int width = Integer.parseInt(Pooka.getProperty(getFolderInfo().getFolderProperty() + ".windowLocation.width", Pooka.getProperty("FolderWindow.width","570")));

    this.setPreferredSize(new Dimension(width, height));

    this.setSize(this.getPreferredSize());

    keyBindings = new ConfigurableKeyBinding(this, "FolderWindow.keyBindings", Pooka.getResources());

    keyBindings.setActive(getActions());
    toolbar.setActive(getActions());

    // if the FolderInternalFrame itself gets the focus, pass it on to
    // the folderDisplay

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          folderDisplay.requestFocusInWindow();
        }
      });

    FocusTraversalPolicy ftp = new LayoutFocusTraversalPolicy() {
        public Component getInitialComponent(JInternalFrame jif) {
          if (jif instanceof FolderInternalFrame) {
            return ((FolderInternalFrame) jif).getFolderDisplay();
          }

          return super.getInitialComponent(jif);
        }
      };
    this.setFocusTraversalPolicy(ftp);

    getFolderDisplay().getMessageTable().getSelectionModel().addListSelectionListener(new SelectionListener());

    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
        public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
          saveWindowSettings();
          getFolderInfo().setFolderDisplayUI(null);
        }
      });

    if (getUI() instanceof BasicInternalFrameUI) {
      ((BasicInternalFrameUI) getUI()).getNorthPane().addMouseListener(new MouseAdapter() {

          public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON2) {
              try {
                Object messagePanel = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.MessagePanel"), FolderInternalFrame.this);
                if (messagePanel != null) {
                  ((MessagePanel) messagePanel).unselectAndMoveToBack(FolderInternalFrame.this);
                  evt.consume();
                }
              } catch (Exception e) {
                getLogger().log(java.util.logging.Level.FINE, "exception lowering FolderInternalFrame", e);
              }
            }
          }

        });
    }
  }


  public FolderInternalFrame(PreviewFolderPanel pfp, MessagePanel newMessagePanel) {
    super(pfp.getFolderInfo().getFolderDisplayName(), true, true, true, true);

    FolderInfo newFolderInfo = pfp.getFolderInfo();

    this.getContentPane().setLayout(new BorderLayout());

    messagePanel = newMessagePanel;

    setFolderInfo(newFolderInfo);

    defaultActions = new Action[] {
      new CloseAction(),
      new ActionWrapper(new ExpungeAction(), getFolderInfo().getFolderThread()),
      new NextMessageAction(),
      new PreviousMessageAction(),
      new GotoMessageAction(),
      new SearchAction()
    };

    // note:  you have to set the Status Bar before you create the
    // FolderDisplayPanel, or else you'll get a null pointer exception
    // from the LoadMessageThread.

    setFolderStatusBar(new FolderStatusBar(this.getFolderInfo()));

    folderDisplay = pfp.getFolderDisplay();
    toolbar = new ConfigurableToolbar("FolderWindowToolbar", Pooka.getResources());
    this.getContentPane().add("North", toolbar);
    this.getContentPane().add("Center", folderDisplay);
    this.getContentPane().add("South", getFolderStatusBar());

    int height = Integer.parseInt(Pooka.getProperty(getFolderInfo().getFolderProperty() + ".windowLocation.height", Pooka.getProperty("FolderWindow.height", "380")));
    int width = Integer.parseInt(Pooka.getProperty(getFolderInfo().getFolderProperty() + ".windowLocation.width", Pooka.getProperty("FolderWindow.width","570")));

    this.setPreferredSize(new Dimension(width, height));

    this.setSize(this.getPreferredSize());

    keyBindings = new ConfigurableKeyBinding(this, "FolderWindow.keyBindings", Pooka.getResources());

    keyBindings.setActive(getActions());
    toolbar.setActive(getActions());

    // if the FolderInternalFrame itself gets the focus, pass it on to
    // the folderDisplay

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          folderDisplay.requestFocusInWindow();
        }
      });

    getFolderDisplay().getMessageTable().getSelectionModel().addListSelectionListener(new SelectionListener());

    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    this.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
        public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
          saveWindowSettings();
          getFolderInfo().setFolderDisplayUI(null);
        }
      });

    if (getUI() instanceof BasicInternalFrameUI) {
      ((BasicInternalFrameUI) getUI()).getNorthPane().addMouseListener(new MouseAdapter() {

          public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON2) {
              try {
                Object messagePanel = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.MessagePanel"), FolderInternalFrame.this);
                if (messagePanel != null) {
                  ((MessagePanel) messagePanel).unselectAndMoveToBack(FolderInternalFrame.this);
                  evt.consume();
                }
              } catch (Exception e) {
                getLogger().log(java.util.logging.Level.FINE, "exception lowering FolderInternalFrame", e);
              }
            }
          }

        });
    }
  }

  /**
   * Saves the FolderInternalFrame's current settings.
   */
  public void saveWindowSettings() {
    String folderProperty = getFolderInfo().getFolderProperty();

    // we have to do these as absolute values.
    MessagePanel mp = getMessagePanel();

    int x = getX() + ((JScrollPane)mp.getUIComponent()).getHorizontalScrollBar().getValue();
    int y = getY() + ((JScrollPane)mp.getUIComponent()).getVerticalScrollBar().getValue();

    Pooka.setProperty(folderProperty + ".windowLocation.x", Integer.toString(x));
    Pooka.setProperty(folderProperty + ".windowLocation.y", Integer.toString(y));

    Pooka.setProperty(folderProperty + ".windowLocation.height", Integer.toString(getHeight()));
    Pooka.setProperty(folderProperty + ".windowLocation.width", Integer.toString(getWidth()));

    getFolderDisplay().saveTableSettings();
  }

  /**
   * Searches the underlying FolderInfo's messages for messages matching
   * the search term.
   */
  public void searchFolder() {
    getFolderInfo().showSearchFolder();
  }

  /**
   * This method takes the currently selected row(s) and returns the
   * appropriate MessageProxy object.
   *
   * If no rows are selected, null is returned.
   */
  public MessageProxy getSelectedMessage() {
    return getFolderDisplay().getSelectedMessage();
  }

  /**
   * This resets the size to that of the parent component.
   */
  public void resize() {
    this.setSize(getParent().getSize());
  }

  /**
   * This opens the FolderInternalFrame.
   */
  public void openFolderDisplay() {
    openFolderDisplay(true);
  }

  /**
   * This opens the FolderInternalFrame.
   */
  public void openFolderDisplay(boolean selected) {
    final boolean fSelected = selected;
    Runnable runMe = new Runnable() {
        public void run() {
          getMessagePanel().openFolderWindow(FolderInternalFrame.this, fSelected);
        }
      };
    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else
      SwingUtilities.invokeLater(runMe);
  }

  /**
   * This closes the FolderInternalFrame.
   */
  public void closeFolderDisplay(){
    Runnable runMe = new Runnable() {
        public void run() {

          try {
            saveWindowSettings();
            setClosed(true);
          } catch (java.beans.PropertyVetoException e) {
          }
        }
      };
    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }


  }

  /**
   * This expunges all the messages marked as deleted in the folder.
   */
  public void expungeMessages() {
    try {
      getFolderInfo().getFolder().expunge();
    } catch (MessagingException me) {
      showError(Pooka.getProperty("error.Message.ExpungeErrorMessage", "Error:  could not expunge messages.") +"\n" + me.getMessage());
    }
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, String title) {
    final String errorMsg = errorMessage;
    final String realTitle = title;
    Runnable runMe = new Runnable() {
        public void run() {
          JOptionPane.showInternalMessageDialog(getMessagePanel(), errorMsg, realTitle, JOptionPane.ERROR_MESSAGE);
        }
      };

    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage) {
    showError(errorMessage, Pooka.getProperty("Error", "Error"));
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, Exception e) {
    showError(errorMessage, Pooka.getProperty("Error", "Error"), e);
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, String title, Exception e) {
    showError(errorMessage + e.getMessage(), title);
    e.printStackTrace();
  }

  /**
   * This formats a display message.
   */
  public String formatMessage(String message) {
    return Pooka.getUIFactory().formatMessage(message);
  }

  /**
   * This shows an Input window.  We include this so that the
   * MessageProxy can call the method without caring about the actual
   * implementation of the dialog.
   */
  public String showInputDialog(String pInputMessage, String pTitle) {
    final String inputMessage = pInputMessage;
    final String title = pTitle;
    final ResponseWrapper fResponseWrapper = new ResponseWrapper();

    Runnable runMe = new Runnable() {
        public void run() {
          fResponseWrapper.setString(JOptionPane.showInternalInputDialog(getMessagePanel(), inputMessage, title, JOptionPane.QUESTION_MESSAGE));
        }
      };

    if (! SwingUtilities.isEventDispatchThread()) {
      try {
        SwingUtilities.invokeAndWait(runMe);
      } catch (Exception e) {
      }
    } else {
      runMe.run();
    }

    return fResponseWrapper.getString();
  }

  /**
   * As specified by interface net.suberic.pooka.gui.FolderDisplayUI.
   *
   * This skips to the given message.
   */
  public int selectMessage(int messageNumber) {
    return getFolderDisplay().selectMessage(messageNumber);
  }

  /**
   * As specified by interface net.suberic.pooka.gui.FolderDisplayUI.
   *
   * This makes the given row visible.
   */
  public void makeSelectionVisible(int messageNumber) {
    getFolderDisplay().makeSelectionVisible(messageNumber);
  }

  public int selectNextMessage() {
    return getFolderDisplay().selectNextMessage();
  }

  public int selectPreviousMessage() {
    return getFolderDisplay().selectPreviousMessage();
  }

  public int selectNextUnreadMessage() {
    return getFolderDisplay().selectNextUnreadMessage();
  }

  /**
   * As specified by interface net.suberic.pooka.gui.FolderDisplayUI.
   *
   * This resets the FolderTableModel in the MessageTable.
   */
  public void resetFolderTableModel(FolderTableModel ftm) {
    getFolderDisplay().resetFolderTableModel(ftm);
  }

  /**
   * As specified by interface net.suberic.pooka.gui.FolderDisplayUI.
   *
   */
  public void showStatusMessage(String msg) {
    Pooka.getUIFactory().showStatusMessage(getFolderInfo().getFolderID() + ":  " + msg);
  }

  /**
   * As specified by interface net.suberic.pooka.gui.FolderDisplayUI.
   *
   */
  public void clearStatusMessage() {
    Pooka.getUIFactory().clearStatus();
  }

  /**
   * As specified by interface net.suberic.pooka.gui.FolderDisplayUI.
   *
   * This implementation sets the cursor to either Cursor.WAIT_CURSOR
   * if busy, or Cursor.DEFAULT_CURSOR if not busy.
   */
  public void setBusy(boolean newBusy) {
    final boolean newValue = newBusy;

    Runnable runMe = new Runnable() {
        public void run() {
          if (newValue)
            FolderInternalFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          else
            FolderInternalFrame.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      };

    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else
      SwingUtilities.invokeLater(runMe);
  }

  /**
   * Displays a status message for the folder.
   */
  public void updateStatus(String message) {
    Pooka.getUIFactory().showStatusMessage(message);
  }

  /**
   * Displays a status message for the folder.
   */
  public void updateStatus(Event e, String message) {
    if (message != null)
      updateStatus(message);

  }

  // Accessor methods.

  public MessagePanel getMessagePanel() {
    if (messagePanel != null)
      return messagePanel;
    else {
      ContentPanel cp = Pooka.getMainPanel().getContentPanel();
      if (cp instanceof MessagePanel)
        return (MessagePanel) cp;
      else
        return null;
    }
  }

  public FolderDisplayPanel getFolderDisplay() {
    return folderDisplay;
  }

  public void setFolderInfo(FolderInfo newValue) {
    folderInfo=newValue;
  }

  public FolderInfo getFolderInfo() {
    return folderInfo;
  }

  public FolderStatusBar getFolderStatusBar() {
    return folderStatusBar;
  }

  public void setFolderStatusBar(FolderStatusBar newValue) {
    folderStatusBar = newValue;
  }

  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getLogger("Pooka.debug.gui");
  }

  /**
   * gets the actions handled both by the FolderInternalFrame and the
   * selected Message(s).
   */

  public class SelectionListener implements javax.swing.event.ListSelectionListener {
    SelectionListener() {
    }

    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
      // the main menus are handled by the FolderDisplayPanel itself.
      if (toolbar != null)
        toolbar.setActive(getActions());
      if (keyBindings != null)
        keyBindings.setActive(getActions());
    }
  }

  /**
   * This registers the Keyboard action not only for the FolderInternalFrame
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.registerKeyboardAction(ActionListener anAction,
   *            String aCommand, KeyStroke aKeyStroke, int aCondition)
   */

  public void registerKeyboardAction(ActionListener anAction,
                                     String aCommand, KeyStroke aKeyStroke, int aCondition) {
    super.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

    getFolderDisplay().registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    folderStatusBar.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
    toolbar.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
  }

  /**
   * This unregisters the Keyboard action not only for the FolderInternalFrame
   * itself, but also for pretty much all of its children, also.  This
   * is to work around something which I think is a bug in jdk 1.2.
   * (this is not really necessary in jdk 1.3.)
   *
   * Overrides JComponent.unregisterKeyboardAction(KeyStroke aKeyStroke)
   */

  public void unregisterKeyboardAction(KeyStroke aKeyStroke) {
    super.unregisterKeyboardAction(aKeyStroke);

    getFolderDisplay().unregisterKeyboardAction(aKeyStroke);
    folderStatusBar.unregisterKeyboardAction(aKeyStroke);
    toolbar.unregisterKeyboardAction(aKeyStroke);
  }

  /**
   * As specified by net.suberic.pooka.UserProfileContainer
   */

  public UserProfile getDefaultProfile() {
    if (getFolderInfo() != null) {
      return getFolderInfo().getDefaultProfile();
    }
    else {
      return null;
    }
  }

  /**
   * Returns whether or not this window is enabled.  This should be true
   * just about all of the time.  The only time it won't be true is if
   * the Folder is closed or disconnected, and the mail store isn't set
   * up to work in disconnected mode.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * This sets whether or not the window is enabled.  This should only
   * be set to false when the Folder is no longer available.
   */
  public void setEnabled(boolean newValue) {
    enabled = newValue;
  }

  // MessageLoadedListener

  /**
   * Displays that a message has been loaded.
   *
   * Defined in net.suberic.pooka.event.MessageLoadedListener.
   */
  public void handleMessageLoaded(MessageLoadedEvent e) {
    final MessageLoadedEvent event = e;

    Runnable runMe = new Runnable() {

        public void run() {
          if (event.getType() == MessageLoadedEvent.LOADING_STARTING) {
            if (getFolderStatusBar().getTracker() != null) {
              getFolderStatusBar().setTracker(new LoadMessageTracker(event.getLoadedMessageCount(), 0, event.getNumMessages()));
              getFolderStatusBar().getLoaderPanel().add(getFolderStatusBar().getTracker());
            }
          } else if (event.getType() == MessageLoadedEvent.LOADING_COMPLETE) {

            if (getFolderStatusBar().getTracker() != null) {
              getFolderStatusBar().getLoaderPanel().remove(getFolderStatusBar().getTracker());
              getFolderStatusBar().setTracker(null);
            }
          } else if (event.getType() == MessageLoadedEvent.MESSAGES_LOADED) {
            if (getFolderStatusBar().getTracker() != null)
              getFolderStatusBar().getTracker().handleMessageLoaded(event);
          }
          getFolderStatusBar().repaint();
        }
      };

    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(runMe);
    } else {
      runMe.run();
    }
  }

  // ConnectionListener

  /**
   *
   */
  public void closed(ConnectionEvent e) {

  }

  /**
   *
   */
  public void disconnected(ConnectionEvent e) {

  }

  /**
   *
   */
  public void opened(ConnectionEvent e) {

  }

  // MessageCountListener
  /**
   *
   */
  public void messagesAdded(MessageCountEvent e) {
    if (getFolderStatusBar() != null)
      getFolderStatusBar().messagesAdded(e);
  }

  /**
   * Called in response to a messagesRemoved event.  Should always be
   * called on the parent FolderThread.
   */
  public void messagesRemoved(MessageCountEvent e) {
    if (getFolderStatusBar() != null)
      getFolderStatusBar().messagesRemoved(e);

    Runnable updateAdapter = new Runnable() {
        public void run() {
          //getMessagePanel().getMainPanel().refreshActiveMenus();
          if (toolbar != null)
            toolbar.setActive(getActions());
          if (keyBindings != null)
            keyBindings.setActive(getActions());
        }
      };
    if (SwingUtilities.isEventDispatchThread())
      updateAdapter.run();
    else
      SwingUtilities.invokeLater(updateAdapter);

  }

  // MessageChangedListener
  public void messageChanged(MessageChangedEvent e) {
    if (getFolderStatusBar() != null)
      getFolderStatusBar().messageChanged(e);
    if (getFolderDisplay() != null)
      getFolderDisplay().moveSelectionOnRemoval(e);

    final MessageInfo mi = getFolderInfo().getMessageInfo(e.getMessage());
    if (mi != null) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            // really, all we should do here is update the individual
            // row.
            // getFolderDisplay().repaint();
            if (mi.getMessageProxy() != null)
              getFolderDisplay().repaintMessage(mi.getMessageProxy());
          }
        });
    }
  }

  /**
   * This checks to see if the message which has been removed is
   * currently selected.  If so, we unselect it and select the next
   * row.
   */
  private void moveSelectionOnRemoval(MessageChangedEvent e) {
    try {
      // don't bother if we're just going to autoexpunge it...
      if ((!Pooka.getProperty("Pooka.autoExpunge", "true").equalsIgnoreCase("true")) && e.getMessageChangeType() == MessageChangedEvent.FLAGS_CHANGED && (e.getMessage().isExpunged() || e.getMessage().getFlags().contains(Flags.Flag.DELETED))) {
        MessageProxy selectedProxy = getSelectedMessage();
        if ( selectedProxy != null && selectedProxy.getMessageInfo().getMessage().equals(e.getMessage())) {
          SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                selectNextMessage();
              }
            });
        }
      }
    } catch (MessagingException me) {
    }
  }

  /**
   * This checks to see if the message which has been removed is
   * currently selected.  If so, we unselect it and select the next
   * row.
   */
  private void moveSelectionOnRemoval(MessageCountEvent e) {
    MessageProxy selectedProxy = getSelectedMessage();
    Message[] removedMsgs = e.getMessages();
    if (selectedProxy != null)  {
      boolean found = false;
      Message currentMsg = selectedProxy.getMessageInfo().getMessage();
      for (int i = 0; (found == false && i < removedMsgs.length); i++) {
        if (currentMsg.equals(removedMsgs[i])) {
          found = true;
        }
      }

      if (found) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              selectNextMessage();
            }
          });
      }
    }
  }

  /**
   * Calls getFolderDisplay().removeRows(removedProxies).
   * This is the preferred way to remove rows from the FolderTableModel.
   */
  public void removeRows(java.util.Vector removedProxies) {
    getFolderDisplay().removeRows(removedProxies);
  }

  /**
   * Gets the Actions for this component.
   */
  public Action[] getActions() {
    if (isEnabled()) {
      Action[] returnValue = defaultActions;

      if (getFolderDisplay() != null) {
        returnValue = TextAction.augmentList(getFolderDisplay().getActions(), returnValue);
      }

      return returnValue;
    } else {
      return null;
    }

    /*
      if (isEnabled()) {
      Action[] returnValue;
      MessageProxy m = getSelectedMessage();

      if (m != null)
      returnValue = TextAction.augmentList(m.getActions(), getDefaultActions());
      else
      returnValue = getDefaultActions();

      if (folderInfo.getActions() != null)
      returnValue = TextAction.augmentList(folderInfo.getActions(), returnValue);

      return returnValue;
      } else {
      return null;
      }
    */


  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  //-----------actions----------------

  // The actions supported by the window itself.

  private Action[] defaultActions;

  class CloseAction extends AbstractAction {

    CloseAction() {
      super("file-close");
    }

    public void actionPerformed(ActionEvent e) {
      closeFolderDisplay();
    }
  }

  public class ExpungeAction extends AbstractAction {

    ExpungeAction() {
      super("message-expunge");
    }

    public void actionPerformed(ActionEvent e) {
      expungeMessages();
    }
  }


  public class NextMessageAction extends AbstractAction {

    NextMessageAction() {
      super("message-next");
    }

    public void actionPerformed(ActionEvent e) {
      selectNextMessage();
    }
  }

  public class PreviousMessageAction extends AbstractAction {

    PreviousMessageAction() {
      super("message-previous");
    }

    public void actionPerformed(ActionEvent e) {
      selectPreviousMessage();
    }
  }

  public class NextUnreadMessageAction extends AbstractAction {

    NextUnreadMessageAction() {
      super("message-next-unread");
    }

    public void actionPerformed(ActionEvent e) {
      selectNextUnreadMessage();
    }
  }

  public class GotoMessageAction extends AbstractAction {

    GotoMessageAction() {
      super("message-goto");
    }

    public void actionPerformed(ActionEvent e) {
      getFolderStatusBar().activateGotoDialog();
    }
  }

  public class SearchAction extends AbstractAction {

    SearchAction() {
      super("folder-search");
    }

    public void actionPerformed(ActionEvent e) {
      searchFolder();
    }
  }

  public class SelectAllAction extends AbstractAction {

    SelectAllAction() {
      super("select-all");
    }

    public void actionPerformed(ActionEvent e) {
      getFolderDisplay().selectAll();
    }
  }

  // FIXME - working around JDK bug; remove when fixed.

  public void restoreSubcomponentFocus() {
    Component lLastFocusOwner = getMostRecentFocusOwner();
    if (lLastFocusOwner == null) {
      // Make sure focus is restored somewhere, so that
      // we don't leave a focused component in another frame while
      // this frame is selected.
      lLastFocusOwner = getContentPane();
    }
    lLastFocusOwner.requestFocusInWindow();
  }

}





