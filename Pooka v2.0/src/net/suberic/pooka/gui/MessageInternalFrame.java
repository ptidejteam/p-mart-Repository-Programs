package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.gui.*;
import net.suberic.util.swing.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.TextAction;
import java.util.*;
import javax.swing.text.JTextComponent;
import javax.swing.event.*;
import java.io.File;
import javax.swing.plaf.metal.MetalTheme;

/**
 * An InternalFrame which can display messages.
 *
 * This class should be used in conjunction with a MessagePanel.
 */
public abstract class MessageInternalFrame extends JInternalFrame implements MessageUI, ThemeSupporter, ThemeListener {

  protected MessagePanel parentContainer;

  protected MessageProxy msg;
  protected MessageDisplayPanel messageDisplay;

  protected ConfigurableToolbar toolbar;
  protected ConfigurableKeyBinding keyBindings;
  protected boolean addedToDesktop = false;

  protected PookaUIFactory uiFactory;

  protected javax.swing.plaf.metal.MetalTheme currentTheme = null;

  /**
   * Creates a MessageInternalFrame from the given Message.
   */
  public MessageInternalFrame(MessagePanel newParentContainer, MessageProxy newMsgProxy) {
    super(Pooka.getProperty("Pooka.messageInternalFrame.messageTitle.newMessage", "New Message"), true, true, true, true);

    parentContainer = newParentContainer;
    msg=newMsgProxy;

    this.getContentPane().setLayout(new BorderLayout());

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          if (getMessageDisplay() != null)
            getMessageDisplay().requestFocusInWindow();
        }
      });

    FocusTraversalPolicy ftp = new LayoutFocusTraversalPolicy() {
        public Component getInitialComponent(JInternalFrame jif) {
          if (jif instanceof MessageInternalFrame) {
            return ((MessageInternalFrame) jif).getMessageDisplay();
          }

          return super.getInitialComponent(jif);
        }
      };
    this.setFocusTraversalPolicy(ftp);

    if (getUI() instanceof BasicInternalFrameUI) {
      ((BasicInternalFrameUI) getUI()).getNorthPane().addMouseListener(new MouseAdapter() {

          public void mousePressed(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON2) {
              try {
                Object messagePanel = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.MessagePanel"), MessageInternalFrame.this);
                if (messagePanel != null) {
                  ((MessagePanel) messagePanel).unselectAndMoveToBack(MessageInternalFrame.this);
                  evt.consume();
                }
              } catch (Exception e) {
                getLogger().log(java.util.logging.Level.FINE, "exception lowering MessageInternalFrame", e);
              }
            }
          }
        });
    }
  }

  /**
   * Creates a MessageInternalFrame from the given Message.
   */

  protected MessageInternalFrame() {
    super(Pooka.getProperty("Pooka.messageInternalFrame.messageTitle.newMessage", "New Message"), true, true, true, true);
    this.getContentPane().setLayout(new BorderLayout());

    if (getUI() instanceof BasicInternalFrameUI) {
      ((BasicInternalFrameUI) getUI()).getNorthPane().addMouseListener(new MouseAdapter() {

          public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON2) {
              try {
                Object messagePanel = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.MessagePanel"), MessageInternalFrame.this);
                if (messagePanel != null) {
                  ((MessagePanel) messagePanel).unselectAndMoveToBack(MessageInternalFrame.this);
                  evt.consume();
                }
              } catch (Exception e) {
                getLogger().log(java.util.logging.Level.FINE, "exception lowering MessageInternalFrame", e);
              }
            }
          }

        });
    }
  }

  /**
   * this method is expected to do all the implementation-specific
   * duties.
   */

  protected abstract void configureMessageInternalFrame() throws MessagingException, OperationCancelledException;

  /**
   * Configures the InterfaceStyle for this component.
   */
  public void configureInterfaceStyle() {
    Runnable runMe = new Runnable() {
        public void run() {
          try {
            Pooka.getUIFactory().getPookaThemeManager().updateUI(MessageInternalFrame.this, MessageInternalFrame.this);
            getMessageDisplay().setDefaultFont();
            getMessageDisplay().sizeToDefault();
            MessageInternalFrame.this.setSize(MessageInternalFrame.this.getPreferredSize());
            if (getUI() instanceof BasicInternalFrameUI) {
              ((BasicInternalFrameUI) getUI()).getNorthPane().addMouseListener(new MouseAdapter() {

                  public void mouseClicked(MouseEvent evt) {
                    if (evt.getButton() == MouseEvent.BUTTON2) {
                      try {
                        Object messagePanel = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.MessagePanel"), MessageInternalFrame.this);
                        if (messagePanel != null) {
                          ((MessagePanel) messagePanel).unselectAndMoveToBack(MessageInternalFrame.this);
                          evt.consume();
                        }
                      } catch (Exception e) {
                        getLogger().log(java.util.logging.Level.FINE, "exception lowering MessageInternalFrame", e);
                      }
                    }
                  }

                });
            }
          } catch (Exception e) {
          }
        }
      };

    if (! SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(runMe);
    } else {
      runMe.run();
    }
  }

  /**
   * Gets the currently configured Theme.
   */
  public MetalTheme getCurrentTheme() {
    return currentTheme;
  }

  /**
   * Sets the Theme that this component is currently using.
   */
  public void setCurrentTheme(MetalTheme newTheme) {
    if (currentTheme != null && currentTheme instanceof ConfigurableMetalTheme) {
      ((ConfigurableMetalTheme)currentTheme).removeThemeListener(this);
    }
    currentTheme = newTheme;

    if (currentTheme != null && currentTheme instanceof ConfigurableMetalTheme) {
      ((ConfigurableMetalTheme)currentTheme).addThemeListener(this);
    }
  }

  /**
   * Called when the specifics of a Theme change.
   */
  public void themeChanged(ConfigurableMetalTheme theme) {
    // we should really only be getting messages from our own current themes,
    // but, hey, it never hurts to check.
    if (currentTheme != null && currentTheme == theme) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            try {
              Pooka.getUIFactory().getPookaThemeManager().updateUI(MessageInternalFrame.this, MessageInternalFrame.this, true);
              getMessageDisplay().setDefaultFont();
              getMessageDisplay().sizeToDefault();
              MessageInternalFrame.this.setSize(MessageInternalFrame.this.getPreferredSize());

            } catch (Exception e) {
            }

          }
        });
    }
  }

  /**
   * This opens the MessageInternalFrame by calling
   * getParentContainer().openMessageInternalFrame(getMessageProxy());
   */
  public void openMessageUI() {
    getParentContainer().openMessageWindow(getMessageProxy(), !addedToDesktop);
    addedToDesktop = true;
  }

  /**
   * This closes the MessageInternalFrame.
   */
  public void closeMessageUI() {
    try {
      this.setClosed(true);
    } catch (java.beans.PropertyVetoException e) {
    }
  }

  /**
   * This detaches this window from the MessagePanel, instead making it
   * a top-level MessageFrame.
   */
  public abstract void  detachWindow();


  /**
   * This shows an Confirm Dialog window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public int showConfirmDialog(String pMessageText, String pTitle, int pType) {
    final String messageText = pMessageText;
    final String title = pTitle;
    final int type = pType;
    final ResponseWrapper fResponseWrapper = new ResponseWrapper();

    Runnable runMe = new Runnable() {
        public void run() {
          fResponseWrapper.setInt(JOptionPane.showInternalConfirmDialog((JDesktopPane)Pooka.getMainPanel().getContentPanel(), messageText, title, type));
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

    return fResponseWrapper.getInt();
  }

  /**
   * This shows an Confirm Dialog window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public int showConfirmDialog(String pMessageText, String pTitle, int pOptionType, int pIconType) {
    final String messageText = pMessageText;
    final String title = pTitle;
    final int optionType = pOptionType;
    final int iconType = pIconType;
    final ResponseWrapper fResponseWrapper = new ResponseWrapper();

    Runnable runMe = new Runnable() {
        public void run() {
          fResponseWrapper.setInt(JOptionPane.showInternalConfirmDialog((JDesktopPane)Pooka.getMainPanel().getContentPanel(), messageText, title, optionType));
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

    return fResponseWrapper.getInt();
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, String title) {
    Pooka.getUIFactory().showError(errorMessage, title);
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage) {
    Pooka.getUIFactory().showError(errorMessage);
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, Exception e) {
    Pooka.getUIFactory().showError(errorMessage, e);
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, String title, Exception e) {
    Pooka.getUIFactory().showError(errorMessage, title, e);
  }

  /**
   * This formats a display message.
   */
  public String formatMessage(String message) {
    return Pooka.getUIFactory().formatMessage(message);
  }

  /**
   * This shows a Message window.  We include this so that
   * the MessageProxy can call the method without caring about the
   * actual implementation of the Dialog.
   */
  public void showMessageDialog(String pMessage, String pTitle) {
    final String message = pMessage;
    final String title = pTitle;

    Runnable runMe = new Runnable() {
        public void run() {
          JOptionPane.showInternalMessageDialog((JDesktopPane)Pooka.getMainPanel().getContentPanel(), message, title, JOptionPane.PLAIN_MESSAGE);
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
  }

  /**
   * This shows an Input window.  We include this so that the
   * MessageProxy can call the method without caring about the actual
   * implementation of the dialog.
   */
  public String showInputDialog(String inputMessage, String title) {
    return Pooka.getUIFactory().showInputDialog(inputMessage, title);
  }

  /**
   * This shows an Input window.  We include this so that the
   * MessageProxy can call the method without caring about the actual
   * implementation of the dialog.
   */
  public String showInputDialog(Object[] pInputPanes, String pTitle) {
    final Object[] inputPanes = pInputPanes;
    final String title = pTitle;
    final ResponseWrapper fResponseWrapper = new ResponseWrapper();

    Runnable runMe = new Runnable() {
        public void run() {
          fResponseWrapper.setString(JOptionPane.showInternalInputDialog((MessagePanel)Pooka.getMainPanel().getContentPanel(), inputPanes, title, JOptionPane.QUESTION_MESSAGE));

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
   * A convenience method to set the PreferredSize and Size of the
   * component to that of the current preferred width.
   */
  public void resizeByWidth() {
    /*
      int width = (int)messageDisplay.getPreferredSize().getWidth();
      this.setPreferredSize(new Dimension(width, width));
    */
    this.setSize(this.getPreferredSize());
  }

  /**
   * Creates a ProgressDialog using the given values.
   */
  public ProgressDialog createProgressDialog(int min, int max, int initialValue, String title, String content) {
    return new ProgressInternalDialog(min, max, initialValue, title, content, getParentContainer());
  }

  /**
   * Shows the current display of the encryption status.
   */
  public net.suberic.pooka.gui.crypto.CryptoStatusDisplay getCryptoStatusDisplay() {
    return getMessageDisplay().getCryptoStatusDisplay();
  }



  /**
   * As specified by interface net.suberic.pooka.gui.MessageUI.
   *
   * This implementation sets the cursor to either Cursor.WAIT_CURSOR
   * if busy, or Cursor.DEFAULT_CURSOR if not busy.
   */
  public void setBusy(boolean newValue) {
    final boolean fNewValue = newValue;
    Runnable runMe = new Runnable() {
        public void run() {
          if (fNewValue)
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          else
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      };

    if (SwingUtilities.isEventDispatchThread()) {
      runMe.run();
    } else {
      SwingUtilities.invokeLater(runMe);
    }
  }

  /**
   * Refreshes the display.
   */
  public void refreshDisplay() throws MessagingException, OperationCancelledException {
    if (messageDisplay instanceof ReadMessageDisplayPanel)
      ((ReadMessageDisplayPanel)messageDisplay).resetEditorText();
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
    return getMessageProxy().getDefaultProfile();
  }

  public MessageDisplayPanel getMessageDisplay() {
    return messageDisplay;
  }

  public MessageProxy getMessageProxy() {
    return msg;
  }

  public void setMessageProxy(MessageProxy newValue) {
    msg = newValue;
  }

  public String getMessageText() {
    return getMessageDisplay().getMessageText();
  }

  public String getMessageContentType() {
    return getMessageDisplay().getMessageContentType();
  }

  public AttachmentPane getAttachmentPanel() {
    return getMessageDisplay().getAttachmentPanel();
  }

  public MessagePanel getParentContainer() {
    return parentContainer;
  }

  public ConfigurableToolbar getToolbar() {
    return toolbar;
  }

  public ConfigurableKeyBinding getKeyBindings() {
    return keyBindings;
  }

  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getLogger("Pooka.debug.gui");
  }

  //------- Actions ----------//

  public Action[] getActions() {
    return defaultActions;
  }

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  //-----------actions----------------

  // The actions supported by the window itself.

  public Action[] defaultActions = {
    new CloseAction(),
    new DetachAction()
  };

  class CloseAction extends AbstractAction {

    CloseAction() {
      super("file-close");
    }

    public void actionPerformed(ActionEvent e) {
      closeMessageUI();
    }
  }

  class DetachAction extends AbstractAction {
    DetachAction() {
      super("window-detach");
    }

    public void actionPerformed(ActionEvent e) {
      detachWindow();
    }
  }
}





