package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.gui.*;
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

public class ReadMessageInternalFrame extends MessageInternalFrame {

  public boolean firstShow = true;

  /**
   * Creates a ReadMessageInternalFrame from the given Message.
   */

  public ReadMessageInternalFrame(MessagePanel newParentContainer, MessageProxy newMsgProxy) {
    super(newParentContainer, newMsgProxy);

    this.addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosed(InternalFrameEvent e) {
          if (getMessageProxy().getMessageUI() == ReadMessageInternalFrame.this)
            getMessageProxy().setMessageUI(null);
        }
      });

  }

  public ReadMessageInternalFrame(MessagePanel newParentContainer, ReadMessageFrame source) {
    parentContainer = newParentContainer;
    messageDisplay = source.getMessageDisplay();
    messageDisplay.setMessageUI(this);
    msg = source.getMessageProxy();
    toolbar = source.getToolbar();
    keyBindings = source.getKeyBindings();

    try {
      this.setTitle((String)msg.getMessageInfo().getMessageProperty("Subject"));
    } catch (MessagingException me) {
      this.setTitle(Pooka.getProperty("Pooka.messageFrame.messageTitle.noSubject", "<no subject>"));
    }

    this.getContentPane().add("North", toolbar);
    this.getContentPane().add("Center", messageDisplay);

    toolbar.setActive(this.getActions());

    Point loc = source.getLocationOnScreen();
    SwingUtilities.convertPointFromScreen(loc, parentContainer);
    this.setLocation(loc);

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

    this.addInternalFrameListener(new InternalFrameAdapter() {
        public void internalFrameClosed(InternalFrameEvent e) {
          if (getMessageProxy().getMessageUI() == ReadMessageInternalFrame.this)
            getMessageProxy().setMessageUI(null);
        }
      });

    configureInterfaceStyle();
  }

  /**
   * Configures the MessageInteralFrame.
   */
  public void configureMessageInternalFrame() throws MessagingException, OperationCancelledException {
    try {
      this.setTitle((String)msg.getMessageInfo().getMessageProperty("Subject"));
    } catch (MessagingException me) {
      this.setTitle(Pooka.getProperty("Pooka.messageInternalFrame.messageTitle.noSubject", "<no subject>"));
    }

    messageDisplay = new ReadMessageDisplayPanel(this);
    messageDisplay.configureMessageDisplay();

    toolbar = new ConfigurableToolbar("MessageWindowToolbar", Pooka.getResources());

    this.getContentPane().add("North", toolbar);
    this.getContentPane().add("Center", messageDisplay);

    toolbar.setActive(this.getActions());

    // check to see if there are any DisplayStyleComboBoxes
    // in the toolbar
    java.awt.Component[] toolbarComponents = toolbar.getComponents();
    for (int i = 0; i < toolbarComponents.length; i++) {
      if (toolbarComponents[i] instanceof DisplayStyleComboBox) {
        DisplayStyleComboBox dscb = (DisplayStyleComboBox) toolbarComponents[i];
        if (dscb.displayStyle)
          ((ReadMessageDisplayPanel)messageDisplay).setDisplayCombo(dscb);

        if (dscb.headerStyle)
          ((ReadMessageDisplayPanel)messageDisplay).setHeaderCombo(dscb);

        dscb.styleUpdated(getMessageProxy().getDisplayMode(), getMessageProxy().getHeaderMode());
      }
    }

    keyBindings = new ConfigurableKeyBinding(this, "ReadMessageWindow.keyBindings", Pooka.getResources());
    keyBindings.setActive(getActions());

    configureInterfaceStyle();

  }

  /**
   * Gets the Theme object from the ThemeManager which is appropriate
   * for this UI.
   */
  public MetalTheme getTheme(ThemeManager tm) {
    MessageProxy mp = getMessageProxy();
    if (mp == null)
      return null;

    MessageInfo mi = mp.getMessageInfo();
    if (mi == null)
      return null;

    FolderInfo fi = mi.getFolderInfo();
    if (fi != null) {
      String id = Pooka.getProperty(fi.getFolderProperty() + ".theme", "");
      if (id != null && ! id.equals("")) {
        return tm.getTheme(id);
      }
    }

    return tm.getDefaultTheme();
  }

  /**
   * Detaches this window from the desktop panel.  Actually creates a new
   * top-level frame and copies the contents to that frame.
   */
  public void detachWindow() {
    ReadMessageFrame rmf = new ReadMessageFrame(this);
    getMessageProxy().setMessageUI(rmf);

    rmf.setVisible(true);
    try {
      this.setClosed(true);
    } catch (java.beans.PropertyVetoException pve) {
    }
  }

  /**
   * Overrides JComponent.addNotify().
   *
   * We override addNotify() here to call resizeByWidth() to set
   * the correct width, and, if there is a splitPane with an attachment
   * panel, to set the correct divider location on the split pane.
   */
  public void addNotify() {
    super.addNotify();
    if (firstShow) {
      resizeByWidth();
      getMessageDisplay().sizeToDefault();
      firstShow = false;
    }
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
    super.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);

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
    super.unregisterKeyboardAction(aKeyStroke);

    if (messageDisplay != null)
      messageDisplay.unregisterKeyboardAction(aKeyStroke);
    toolbar.unregisterKeyboardAction(aKeyStroke);
  }

  //------- Actions ----------//

  public Action[] getActions() {

    Action[] actionList;

    Action[] currentDefault = getDefaultActions();

    if (messageDisplay.getActions() != null) {
      Action[] displayActions = messageDisplay.getActions();
      actionList = TextAction.augmentList(displayActions, currentDefault);
    } else {
      actionList = currentDefault;
    }

    return actionList;
  }

}





