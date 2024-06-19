package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.gui.*;
import net.suberic.util.swing.*;
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
import javax.swing.plaf.metal.MetalTheme;


/**
 * A top-level window for displaying a message.
 */
public abstract class MessageFrame extends JFrame implements MessageUI, ThemeSupporter, ThemeListener {

  protected MessageProxy msg;
  protected MessageDisplayPanel messageDisplay;

  protected ConfigurableToolbar toolbar;
  protected ConfigurableKeyBinding keyBindings;
  protected ConfigurableMenuBar menuBar;

  protected javax.swing.plaf.metal.MetalTheme currentTheme = null;

  /**
   * Creates a MessageFrame from the given Message.
   */

  public MessageFrame(MessageProxy newMsgProxy) {
    super(Pooka.getProperty("Pooka.messageInternalFrame.messageTitle.newMessage", "New Message"));

    msg=newMsgProxy;

    this.getContentPane().setLayout(new BorderLayout());

    java.net.URL standardUrl = this.getClass().getResource(Pooka.getProperty("Pooka.standardIcon", "images/PookaIcon.gif"));
    if (standardUrl != null) {
      ImageIcon standardIcon = new ImageIcon(standardUrl);
      setIconImage(standardIcon.getImage());
    }

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          if (getMessageDisplay() != null)
            getMessageDisplay().requestFocusInWindow();
        }
      });


  }

  protected MessageFrame() {
    this.getContentPane().setLayout(new BorderLayout());

    java.net.URL standardUrl = this.getClass().getResource(Pooka.getProperty("Pooka.standardIcon", "images/PookaIcon.gif"));
    if (standardUrl != null) {
      ImageIcon standardIcon = new ImageIcon(standardUrl);
      setIconImage(standardIcon.getImage());
    }

    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          if (getMessageDisplay() != null)
            getMessageDisplay().requestFocusInWindow();
        }
      });

  }

  /**
   * this method is expected to do all the implementation-specific
   * duties.
   */

  protected abstract void configureMessageFrame();

  /**
   * Configures the InterfaceStyle for this component.
   */
  public void configureInterfaceStyle() {
    Runnable runMe = new Runnable() {
        public void run() {
          try {
            Pooka.getUIFactory().getPookaThemeManager().updateUI(MessageFrame.this, MessageFrame.this);
            getMessageDisplay().setDefaultFont();
            getMessageDisplay().sizeToDefault();
            MessageFrame.this.setSize(MessageFrame.this.getPreferredSize());
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
              Pooka.getUIFactory().getPookaThemeManager().updateUI(MessageFrame.this, MessageFrame.this, true);
              getMessageDisplay().setDefaultFont();
              getMessageDisplay().sizeToDefault();
              MessageFrame.this.setSize(MessageFrame.this.getPreferredSize());
            } catch (Exception e) {
            }

          }
        });
    }
  }

  /**
   * This opens the MessageFrame.
   */
  public void openMessageUI() {
    this.setVisible(true);
  }

  /**
   * This closes the MessageFrame.
   */
  public void closeMessageUI() {
    this.dispose();
  }

  /**
   * Attaches the window to a MessagePanel.
   */
  public abstract void attachWindow();

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
          fResponseWrapper.setInt(JOptionPane.showConfirmDialog(MessageFrame.this, messageText, title, type));
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
          fResponseWrapper.setInt(JOptionPane.showConfirmDialog(MessageFrame.this, messageText, title, optionType, iconType));
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
  public void showError(String pErrorMessage, String pTitle) {
    final String errorMessage = pErrorMessage;
    final String title = pTitle;

    Runnable runMe = new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(MessageFrame.this, errorMessage, title, JOptionPane.ERROR_MESSAGE);
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
          fResponseWrapper.setString(JOptionPane.showInputDialog(MessageFrame.this, inputMessage, title, JOptionPane.QUESTION_MESSAGE));
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
          fResponseWrapper.setString(JOptionPane.showInputDialog(MessageFrame.this, inputPanes, title, JOptionPane.QUESTION_MESSAGE));
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
   * This shows a Message window.  We include this so that the
   * MessageProxy can call the method without caring about the actual
   * implementation of the dialog.
   */
  public void showMessageDialog(String pErrorMessage, String pTitle) {
    final String errorMessage = pErrorMessage;
    final String title = pTitle;

    Runnable runMe = new Runnable() {
        public void run() {
          JOptionPane.showMessageDialog(MessageFrame.this, errorMessage, title, JOptionPane.PLAIN_MESSAGE);
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
   * A convenience method to set the PreferredSize and Size of the
   * component to that of the current preferred width.
   */
  public void resizeByWidth() {
    //int width = (int)messageDisplay.getPreferredSize().getWidth();
    //this.setPreferredSize(new Dimension(width, width));
    this.setSize(this.getPreferredSize());
  }

  /**
   * Creates a ProgressDialog using the given values.
   */
  public ProgressDialog createProgressDialog(int min, int max, int initialValue, String title, String content) {
    return new ProgressDialogImpl(min, max, initialValue, title, content);
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
   * MessageProxy if the MessageFrame is not editable.  If the
   * MessageFrame is editable, it returns the currently selected
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

  public ConfigurableToolbar getToolbar() {
    return toolbar;
  }

  public ConfigurableKeyBinding getKeyBindings() {
    return keyBindings;
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
    new AttachAction()
  };

  class CloseAction extends AbstractAction {

    CloseAction() {
      super("file-close");
    }

    public void actionPerformed(ActionEvent e) {
      closeMessageUI();
    }
  }

  public class AttachAction extends AbstractAction {
    AttachAction() {
      super("window-detach");
    }

    public void actionPerformed(ActionEvent e) {
      attachWindow();
    }
  }
}





