package net.suberic.pooka.gui;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.MessageInfo;
import net.suberic.util.gui.ConfigurableAwtPopupMenu;
import net.suberic.util.gui.IconManager;
import net.suberic.util.*;

import java.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.PopupMenu;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.mail.event.MessageCountEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;

import java.awt.TrayIcon;
import java.awt.SystemTray;

/**
 * This manages the display of new message notifications.
 */
public class MessageNotificationManager implements ValueChangeListener {

  public static java.awt.TrayIcon.MessageType WARNING_MESSAGE_TYPE = TrayIcon.MessageType.WARNING;
  public static java.awt.TrayIcon.MessageType INFO_MESSAGE_TYPE = TrayIcon.MessageType.INFO;

  private MainPanel mPanel;
  private boolean mNewMessageFlag = false;
  private TrayIcon mTrayIcon = null;
  private Map<String, List<MessageInfo>> mNewMessageMap;
  private int mNewMessageCount = 0;
  private RecentMessageMenu mRecentMessageMenu;

  private Action[] mOfflineActions;
  private Action[] mOnlineActions;

  // icons and displays
  private String mStandardTitle = Pooka.getProperty("Title", "Pooka");
  private String mNewMessageTitle = Pooka.getProperty("Title.withNewMessages", "* Pooka *");

  private ImageIcon mStandardIcon = null;
  private ImageIcon mNewMessageIcon = null;
  private ImageIcon mStandardTrayIcon = null;
  private ImageIcon mNewMessageTrayIcon = null;

  private boolean mShowNewMailMessage = true;
  private boolean mAlwaysDisplay = true;
  private boolean mIconShowing = false;
  private boolean mBlinkNewMail = false;

  private boolean messageDisplaying = false;
  private Thread messageDisplayThread = null;

  private boolean available = true;

  /**
   * Creates a new MessageNotificationManager.
   */
  public MessageNotificationManager() {
    mNewMessageMap = new HashMap<String, List<MessageInfo>>();

    mOfflineActions = new Action[] {
      new NewMessageAction(),
      new PreferencesAction(),
      new ExitPookaAction(),
      new StartPookaAction()
    };

    mOnlineActions = new Action[] {
      new NewMessageAction(),
      new PreferencesAction(),
      new ExitPookaAction(),
      new ClearStatusAction()
    };

    // set up the images to use.
    setupImages();

    // create the tray icon.
    configureTrayIcon();

    // add a listener so we can add/remove the tray icon if the setting
    // changes.
    Pooka.getResources().addValueChangeListener(this, "Pooka.trayIcon.enabled");
    Pooka.getResources().addValueChangeListener(this, "Pooka.trayIcon.showNewMailMessage");
    Pooka.getResources().addValueChangeListener(this, "Pooka.trayIcon.alwaysDisplay");
    Pooka.getResources().addValueChangeListener(this, "Pooka.trayIcon.show");
  }

  /**
   * Creates the SystemTrayIcon, if configured to do so.
   */
  void configureTrayIcon() {
    if (Pooka.getProperty("Pooka.trayIcon.enabled", "true").equalsIgnoreCase("true")) {
      try {
        mTrayIcon = new TrayIcon(mStandardTrayIcon.getImage());
        mTrayIcon.setImageAutoSize(true);
        //mTrayIcon.setTimeout(5000);

        mTrayIcon.setPopupMenu(createPopupMenu());
        mTrayIcon.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              //System.err.println("got action " + e);
              if (getMainPanel() != null) {
                mTrayIcon.displayMessage("Pooka", createStatusMessage(), INFO_MESSAGE_TYPE);
                //bringToFront();
              } else {
                startMainWindow();
              }
            }
          });
        mTrayIcon.addMouseListener(new MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent me) {
              if ((!me.isPopupTrigger()) && ! messageDisplaying) {
                JDialog dialog = createMessageDialog();
                //System.err.println("x,y= " + me.getX() + ", " + me.getY());
                java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
                java.awt.GraphicsDevice[] gs = ge.getScreenDevices();
                for (int i = 0; i < gs.length; i++) {
                  //System.err.println("graphics:  " + gs[i].getDisplayMode().getWidth() + ", " +  gs[i].getDisplayMode().getHeight());
                }
                dialog.setLocation(me.getX(), me.getY());
                dialog.setVisible(true);
                messageDisplaying = true;
              }
            }
          });
        /*
        mTrayIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
              System.err.println("mouseclicked:  " + e);
            }
          });
        */
        if (mAlwaysDisplay) {
          SystemTray.getSystemTray().add(mTrayIcon);
          mIconShowing = true;
        }
        available = true;
      } catch (Error e) {
        available = false;
        System.err.println("Error starting up tray icon:  " + e.getMessage());
        e.printStackTrace();
      } catch (Exception exc) {
        available = false;
        System.err.println("Error starting up tray icon:  " + exc.getMessage());
        exc.printStackTrace();
      }
      setShowNewMailMessage(Pooka.getProperty("Pooka.trayIcon.showNewMailMessage", "true").equalsIgnoreCase("true"));
      setAlwaysDisplay(Pooka.getProperty("Pooka.trayIcon.alwaysDisplay", "true").equalsIgnoreCase("true"));

    } else if (mTrayIcon != null) {
      // remove the tray icon.
      SystemTray.getSystemTray().remove(mTrayIcon);
      mTrayIcon = null;
      mIconShowing = false;
      available = false;
    } else {
      available = false;
    }
  }

  /**
   * Sets up the images to use for the tray icon and for the main window.
   */
  void setupImages() {
    IconManager iconManager;
    if (Pooka.getUIFactory() != null) {
      iconManager = Pooka.getUIFactory().getIconManager();
    } else {
      iconManager = IconManager.getIconManager(Pooka.getResources(), "IconManager._default");
    }


    mStandardIcon = iconManager.getIcon(Pooka.getProperty("Pooka.standardIcon", "PookaIcon"));
    setCurrentIcon(mStandardIcon);

    mStandardTrayIcon = iconManager.getIcon(Pooka.getProperty("Pooka.standardTrayIcon", "PookaTrayIcon"));

    mNewMessageIcon = iconManager.getIcon(Pooka.getProperty("Pooka.newMessageIcon", "EnvelopeOpen"));

    mNewMessageTrayIcon = iconManager.getIcon(Pooka.getProperty("Pooka.newMessageIcon", "NewMessageTray"));

  }

  /**
   * This handles the changes if the source property is modified.
   *
   * As defined in net.suberic.util.ValueChangeListener.
   */

  public void valueChanged(String pChangedValue) {
    if (pChangedValue.equals("Pooka.trayIcon.enabled")) {
      configureTrayIcon();
    } else if (pChangedValue.equals("Pooka.trayIcon.showNewMailMessage")) {
      setShowNewMailMessage(Pooka.getProperty("Pooka.trayIcon.showNewMailMessage", "true").equalsIgnoreCase("true"));
    } else if (pChangedValue.equals("Pooka.trayIcon.alwaysDisplay")) {
      setAlwaysDisplay(Pooka.getProperty("Pooka.trayIcon.alwaysDisplay", "true").equalsIgnoreCase("true"));
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            updateStatus();
          }
        });
    }
  }

  /**
   * This resets the title of the main Frame to have the newMessageFlag
   * or not, depending on if there are any new messages or not.
   */
  protected void updateStatus() {
    synchronized(this) {
      if (getNewMessageFlag()) {
        //System.err.println("updating status.");
        if (getMainPanel() != null) {
          getMainPanel().getParentFrame().setTitle(mNewMessageTitle);
        }
        setCurrentIcon(getNewMessageIcon());
        if (getTrayIcon() != null && available) {
          getTrayIcon().setImage(mNewMessageTrayIcon.getImage());
          if (! mIconShowing) {
            try {
              SystemTray.getSystemTray().add(mTrayIcon);
              mIconShowing = true;
            } catch (Exception exc) {
              System.err.println("Error starting up tray icon:  " + exc.getMessage());
              exc.printStackTrace();
            }
          }
        }
      } else {
        if (getMainPanel() != null) {
          getMainPanel().getParentFrame().setTitle(mStandardTitle);
        }

        if (available) {
          if (mAlwaysDisplay) {
            if (! mIconShowing) {
              try {
                SystemTray.getSystemTray().add(mTrayIcon);
                mIconShowing = true;
              } catch (Exception exc) {
                System.err.println("Error starting up tray icon:  " + exc.getMessage());
                exc.printStackTrace();
              }
            }
        } else {
          if (mIconShowing) {
            SystemTray.getSystemTray().remove(mTrayIcon);
            mIconShowing = false;
          }
        }

        if (getTrayIcon() != null)
          getTrayIcon().setImage(mStandardTrayIcon.getImage());
        }
        setCurrentIcon(getStandardIcon());
      }
    } //synchronized
  }

  /**
   * Brings the main window to the front.
   *
   */
  void bringToFront() {
    //System.err.println("should be trying to bring frame to front, but not implemented yet.");
    //Pooka.getMainPanel().getParentFrame().setExtendedState(java.awt.Frame.ICONIFIED);
    //Pooka.getMainPanel().getParentFrame().setExtendedState(java.awt.Frame.NORMAL);
    Pooka.getMainPanel().getParentFrame().toFront();
    //Pooka.getUIFactory().bringToFront();
  }

  /**
   * Clears the status.
   */
  public synchronized void clearNewMessageFlag() {
    boolean doUpdate = mNewMessageFlag;
    mNewMessageFlag = false;
    mNewMessageCount = 0;
    if (getRecentMessageMenu() != null)
      getRecentMessageMenu().reset();

    mNewMessageMap = new HashMap<String, List<MessageInfo>>();
    if (mTrayIcon != null) {
      mTrayIcon.setToolTip("Pooka: No new messages.");
    }
    if (doUpdate) {
      //Thread.currentThread().dumpStack();
      updateStatus();
    }
  }

  /**
   * Called when a Folder that's being watched gets a messagesAdded event.
   */
  public synchronized void notifyNewMessagesReceived(MessageCountEvent e, String pFolderId) {
    // note:  called on the FolderThread that produced this event.
    mNewMessageCount+= e.getMessages().length;
    List<MessageInfo> newMessageList = mNewMessageMap.get(pFolderId);
    if (newMessageList == null) {
      newMessageList = new ArrayList<MessageInfo>();
    }

    // get the MessageInfo for each of the added messages and add it to
    // the newMessageList.  oh, and while we're at it, add the string info
    // for the first three, also.
    StringBuffer infoLines = new StringBuffer();
    try {
      FolderInfo folder = Pooka.getStoreManager().getFolderById(pFolderId);
      if (folder != null) {
        for (int i = 0; i < e.getMessages().length; i++) {
          MessageInfo current = folder.getMessageInfo(e.getMessages()[i]);
          newMessageList.add(current);
          if (i < 3)
            infoLines.append("From: " + current.getMessageProperty("From") + ", Subj: " + current.getMessageProperty("Subject") + "\r\n\r\n");
          else if (i == 3)
            infoLines.append("...");
        }
      }
    } catch (javax.mail.MessagingException me) {
      // FIXME handle this better.
      me.printStackTrace();
    }
    //newMessageList.addAll(Arrays.asList(e.getMessages()));
    mNewMessageMap.put(pFolderId, newMessageList);

    // build the message
    final String fDisplayMessage = new String(e.getMessages().length + " messages received in " + pFolderId + "\r\n\r\n" + infoLines.toString());
    final String fToolTip = "Pooka: " + mNewMessageCount + " new messages.";
    boolean doUpdateStatus = false;
    if (! mNewMessageFlag) {
      mNewMessageFlag = true;
      doUpdateStatus = true;
    }

    final boolean fUpdateStatus = doUpdateStatus;

    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (fUpdateStatus)
            updateStatus();

          if (mTrayIcon != null) {
            mTrayIcon.setToolTip(fToolTip);
            if (mShowNewMailMessage) {
              mTrayIcon.displayMessage("New Messages", fDisplayMessage, INFO_MESSAGE_TYPE);
            }
            if (mBlinkNewMail) {
              Runnable runMe = new Runnable() {
                  public void run() {
                    Runnable removeMe = new Runnable() {
                        public void run() {
                          synchronized(MessageNotificationManager.this) {
                            if (mNewMessageFlag) {
                              if (getTrayIcon() != null)
                                getTrayIcon().setImage(null);
                            }
                          }
                        }
                      };

                    Runnable showMe = new Runnable() {
                        public void run() {
                          synchronized(MessageNotificationManager.this) {
                            if (getTrayIcon() != null)
                              getTrayIcon().setImage(getNewMessageIcon().getImage());
                          }
                        }
                      };

                    try {
                      for (int i = 0; i < 3; i++) {
                        SwingUtilities.invokeLater(removeMe);
                        Thread.currentThread().sleep(1000);
                        SwingUtilities.invokeLater(showMe);
                        Thread.currentThread().sleep(1000);
                      }
                    } catch (Exception e) {

                    }
                  }
                };

              Thread blinkThread = new Thread(runMe);
              blinkThread.setPriority(Thread.NORM_PRIORITY);
              blinkThread.start();
            }
          }
        }
      });

    if (getRecentMessageMenu() != null)
      getRecentMessageMenu().reset();
  }

  /**
   * Removes a message from the new messages list.
   */
  public synchronized void removeFromNewMessages(MessageInfo pMessageInfo) {
    String folderId = pMessageInfo.getFolderInfo().getFolderID();
    List<MessageInfo> newMessageList = mNewMessageMap.get(folderId);
    if (newMessageList != null) {
      newMessageList.remove(pMessageInfo);
      mNewMessageCount --;
      if (mNewMessageCount == 0)
        clearNewMessageFlag();
    }

    if (getRecentMessageMenu() != null)
      getRecentMessageMenu().reset();

  }

  /**
   * Creates the JPopupMenu for this component.
   */
  public PopupMenu createPopupMenu() {
    ConfigurableAwtPopupMenu popupMenu = new ConfigurableAwtPopupMenu();
    popupMenu.configureComponent("MessageNotificationManager.popupMenu", Pooka.getResources());
    popupMenu.setActive(getActions());
    return popupMenu;
  }

  /**
   * Constructs a status message for the current state of new messages.
   */
  public String createStatusMessage() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Pooka\n");
    if (mNewMessageMap.isEmpty()) {
      buffer.append("No new messages.");
    } else {
      Iterator<String> folders = mNewMessageMap.keySet().iterator();
      while (folders.hasNext()) {
        String current = folders.next();
        buffer.append(current + ":  " + mNewMessageMap.get(current).size() + " new messages.\n");
      }
    }

    return buffer.toString();
  }

  /**
   * Starts up the pooka main window.
   */
  void startMainWindow() {
    net.suberic.pooka.messaging.PookaMessageSender sender =  new net.suberic.pooka.messaging.PookaMessageSender();
    try {
      sender.openConnection();
      if (sender.checkVersion()) {
        sender.sendStartPookaMessage();
      }
    } catch (Exception exc) {
      if (mTrayIcon != null)
        mTrayIcon.displayMessage("Error", "Error sending new message:  " + exc, WARNING_MESSAGE_TYPE);
    } finally {
      if (sender != null && sender.isConnected())
        sender.closeConnection();
    }
  }

  /**
   * Returns the actions for this component.
   */
  public Action[] getActions() {
    if (getMainPanel() == null)
      return mOfflineActions;
    else
      return mOnlineActions;
  }

  /**
   * Get the standard icon for Pooka.
   */
  public ImageIcon getStandardIcon() {
    return mStandardIcon;
  }

  /**
   * Get the new message icon for Pooka.
   */
  public ImageIcon getNewMessageIcon() {
    return mNewMessageIcon;
  }

  /**
   * Sets the current icon for the frame.
   */
  public void setCurrentIcon(ImageIcon newIcon) {
    //System.err.println("setting icon to " + newIcon.getImage() + " on " + getMainPanel());
    //if (getMainPanel() != null)
    //System.err.println("setting icon to " + newIcon.getImage() + " on " + getMainPanel().getParentFrame());
    if (getMainPanel() != null)
      getMainPanel().getParentFrame().setIconImage(newIcon.getImage());
  }

  /**
   * Gets the tray icon.
   */
  public TrayIcon getTrayIcon() {
    return mTrayIcon;
  }

  /**
   * Returns the MainPanel for this MNM.
   */
  public MainPanel getMainPanel() {
    return mPanel;
  }

  WindowAdapter mAdapter = null;
  /**
   * Sets the MainPanel for this MNM.
   */
  public void setMainPanel(MainPanel pPanel) {
    if (mPanel != pPanel) {
      if (pPanel != null) {
        pPanel.getParentFrame().removeWindowListener(mAdapter);
        mAdapter = null;
      }
      mPanel = pPanel;

      if (mPanel != null) {
        mAdapter = new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
              clearNewMessageFlag();
            }
          };
        mPanel.getParentFrame().addWindowListener(mAdapter);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              updateStatus();
            }
          });
      }
    }

    if (mTrayIcon != null)
      mTrayIcon.setPopupMenu(createPopupMenu());

    //System.err.println("mainPanel now = " + mPanel);
  }

  /**
   * Displays a message.
   *
   * @param pMessage the message to display
   * @param pTitle the title of the display window
   * @param pType the type of message to display
   * @return true if the message is displayed, false otherwise.
   */
  public boolean displayMessage(String pTitle, String pMessage, TrayIcon.MessageType pType) {
    if (mTrayIcon != null && mShowNewMailMessage) {
      mTrayIcon.displayMessage(pTitle, pMessage, pType);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Creates a display component to show the available messages.
   */
  public JDialog createMessageDialog() {
    HashMap<String, MessageInfo> messageMap = new HashMap<String, MessageInfo>();
    Box messageBox = new Box(BoxLayout.Y_AXIS);
    Box headerBox = new Box(BoxLayout.X_AXIS);

    // header
    JTextPane headerPane = new JTextPane();
    headerPane.setContentType("text/html");
    headerPane.setText("<html><body style=\" font-size: 9px; \"><b>Pooka</b></body></html> ");
    headerPane.setEditable(false);

    headerBox.add(headerPane);
    JButton closeButton = new JButton(javax.swing.plaf.metal.MetalIconFactory.getInternalFrameCloseIcon(5));
    closeButton.setMinimumSize(new java.awt.Dimension(15,15));
    closeButton.setPreferredSize(new java.awt.Dimension(15,15));
    closeButton.setMaximumSize(new java.awt.Dimension(15,15));
    headerBox.add(closeButton);

    messageBox.add(headerBox);

    StringBuffer textBuffer = new StringBuffer();
    //textBuffer.append("<html><body style=\" font-size: 9px; \"><b>Status</b><box style=\" position: absolute; margin: 0 0 0 90%; \"><a href = \"close://\">x</a></box>");
    textBuffer.append("<html><body style=\" font-size: 9px; \">");
    if (! mNewMessageMap.isEmpty()) {
      textBuffer.append("<ul>");
      Iterator<String> folderIds = mNewMessageMap.keySet().iterator();
      while (folderIds.hasNext()) {
        textBuffer.append("<li>");
        String folderId = folderIds.next();
        textBuffer.append(folderId);
        textBuffer.append("<ul>");
        Iterator<MessageInfo> messageIter = mNewMessageMap.get(folderId).iterator();
        int counter = 0;
        while (messageIter.hasNext()) {
          try {
            MessageInfo messageInfo = messageIter.next();
            String messageUri = "mailopen://" + folderId + "/" + counter++;
            textBuffer.append("<li><a href = \"" + messageUri + "\">");
            messageMap.put(messageUri, messageInfo);
            textBuffer.append("From: " + messageInfo.getMessageProperty("From") + ", Subj: " + messageInfo.getMessageProperty("Subject"));
            textBuffer.append("</a></li>");
          } catch (Exception e) {
            textBuffer.append("<li>Error:  " + e.getMessage() + "</li>");
          }
        }
        textBuffer.append("</ul>");
        textBuffer.append("</li>");
      }

      textBuffer.append("</ul>");
    } else {
      textBuffer.append("No new messages.");
    }
    textBuffer.append("</body></html>");

    final HashMap<String, MessageInfo> fMessageMap = messageMap;

    //JTextArea pookaMessage = new JTextArea(createStatusMessage());
    JTextPane pookaMessage = new JTextPane();
    pookaMessage.setContentType("text/html");
    pookaMessage.setText(textBuffer.toString());

    messageBox.add(pookaMessage);

    final JDialog dialog = new JDialog();
    pookaMessage.setEditable(false);
    //dialog.add(pookaMessage);
    dialog.add(messageBox);
    dialog.setUndecorated(true);
    dialog.pack();
    /*
    pookaMessage.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent me) {
          dialog.setVisible(false);
          dialog.dispose();
          messageDisplaying = false;
        }
      });
    */
    closeButton.addActionListener(new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          dialog.setVisible(false);
          dialog.dispose();
          messageDisplaying = false;
        }
      });

    pookaMessage.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            //System.err.println("hyperlinkEvent:  " + e);
            java.net.URL url = e.getURL();
            //System.err.println("url = " + url);
            //System.err.println("description = " + e.getDescription());
            if (e.getDescription().startsWith("close")) {
              dialog.setVisible(false);
              dialog.dispose();
              messageDisplaying = false;
            } else if (e.getDescription().startsWith("mailopen://")) {
              MessageInfo openMessage = fMessageMap.get(e.getDescription());
              try {
                MessageProxy proxy = openMessage.getMessageProxy();
                MessageUI mui = Pooka.getUIFactory().createMessageUI(proxy, new NewMessageFrame(new NewMessageProxy(new net.suberic.pooka.NewMessageInfo(new javax.mail.internet.MimeMessage(Pooka.getDefaultSession())))));
                mui.openMessageUI();
                // and if that works, remove it from the new message map.
                removeFromNewMessages(openMessage);
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }
        }
      });

    messageDisplayThread = new Thread(new Runnable() {
        public void run() {
          try {
            Thread.currentThread().sleep(5000);
          } catch(Exception e) {
          }
          if (messageDisplaying) {
            dialog.setVisible(false);
            dialog.dispose();
            messageDisplaying = false;
          }
        }
      });

    messageDisplayThread.start();

    return dialog;

  }

  /**
   * Returns the newMessageFlag.
   */
  public boolean getNewMessageFlag() {
    return mNewMessageFlag;
  }

  /**
   * Returns whether or not we show new message notifications on the
   * TrayIcon.
   */
  public boolean getShowNewMailMessage() {
    return mShowNewMailMessage;
  }

  /**
   * Sets whether or not we show new message notifications on the
   * TrayIcon.
   */
  public void setShowNewMailMessage(boolean pShowNewMailMessage) {
    mShowNewMailMessage = pShowNewMailMessage;
  }

  /**
   * Returns whether or not the icon is always displayed, or just when there
   * are new messages.
   */
  public boolean getAlwaysDisplay() {
    return mAlwaysDisplay;
  }

  /**
   * Sets whether or not the icon is always displayed, or just when there
   * are new messages.
   */
  public void setAlwaysDisplay(boolean pAlwaysDisplay) {
    mAlwaysDisplay = pAlwaysDisplay;
  }

  /**
   * Returns the current new message map.
   */
  public Map<String, List<MessageInfo>> getNewMessageMap() {
    return mNewMessageMap;
  }

  /**
   * Sets the RecentMessageMenu
   */
  void setRecentMessageMenu(RecentMessageMenu pRecentMessageMenu) {
    mRecentMessageMenu = pRecentMessageMenu;
  }

  /**
   * gets the RecentMessageMenu
   */
  RecentMessageMenu getRecentMessageMenu() {
    return mRecentMessageMenu;
  }

    //-----------actions----------------

  class NewMessageAction extends AbstractAction {

    NewMessageAction() {
      super("message-new");
    }

    public void actionPerformed(ActionEvent e) {
      //System.err.println("sending new message.");
      net.suberic.pooka.messaging.PookaMessageSender sender =  new net.suberic.pooka.messaging.PookaMessageSender();
      try {
        sender.openConnection();
        if (sender.checkVersion()) {
          sender.openNewEmail(null, null);
        }
      } catch (Exception exc) {
        if (mTrayIcon != null)
          mTrayIcon.displayMessage("Error", "Error sending new message:  " + exc, WARNING_MESSAGE_TYPE);
      } finally {
        if (sender != null && sender.isConnected())
          sender.closeConnection();
      }
    }
  }

  class OpenMessageAction extends AbstractAction {

    MessageInfo mMessageInfo;

    OpenMessageAction(MessageInfo pMessageInfo) {
      super("message-new");

      mMessageInfo = pMessageInfo;
    }

    public void actionPerformed(ActionEvent e) {
      //System.err.println("opening message.");

      try {
        MessageProxy proxy = mMessageInfo.getMessageProxy();
        MessageUI mui = Pooka.getUIFactory().createMessageUI(proxy, new NewMessageFrame(new NewMessageProxy(new net.suberic.pooka.NewMessageInfo(new javax.mail.internet.MimeMessage(Pooka.getDefaultSession())))));
        mui.openMessageUI();
        // and if that works, remove it from the new message map.
        removeFromNewMessages(mMessageInfo);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  class ClearStatusAction extends AbstractAction {

    ClearStatusAction() {
      super("status-clear");
    }

    public void actionPerformed(ActionEvent e) {
      clearNewMessageFlag();
    }
  }

  class PreferencesAction extends AbstractAction {

    PreferencesAction() {
      super("file-preferences");
    }

    public void actionPerformed(ActionEvent e) {
      //System.err.println("show preferences here.  :)");
    }
  }

  class StartPookaAction extends AbstractAction {

    StartPookaAction() {
      super("file-start");
    }

    public void actionPerformed(ActionEvent e) {
      if (getMainPanel() != null)
        bringToFront();
      else {
        startMainWindow();
      }
    }
  }

  class ExitPookaAction extends AbstractAction {

    ExitPookaAction() {
      super("file-exit");
    }

    public void actionPerformed(ActionEvent e) {
      if (getMainPanel() != null)
        getMainPanel().exitPooka(false);
      else
        Pooka.exitPooka(0, this);
    }
  }

}
