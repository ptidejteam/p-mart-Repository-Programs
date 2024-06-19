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


public class ReadMessageFrame extends MessageFrame {

  public boolean firstShow = true;

  /**
   * Creates a ReadMessageFrame from the given Message.
   */

  public ReadMessageFrame(MessageProxy newMsgProxy) {
    super(newMsgProxy);

    configureMessageFrame();

  }

  /**
   * Creates a ReadMessageFrameFrame from an existing ReadMessageInternalFrame.
   */
  ReadMessageFrame(ReadMessageInternalFrame source) {
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

    this.setLocation(source.getLocationOnScreen());

    configureInterfaceStyle();

    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          if (getMessageProxy().getMessageUI() == ReadMessageFrame.this) {
            getMessageProxy().setMessageUI(null);
          }
        }
      });
  }

  protected void configureMessageFrame() {
    try {
      try {
        this.setTitle((String)msg.getMessageInfo().getMessageProperty("Subject"));
      } catch (MessagingException me) {
        this.setTitle(Pooka.getProperty("Pooka.messageFrame.messageTitle.noSubject", "<no subject>"));
      }

      messageDisplay = new ReadMessageDisplayPanel(this);
      messageDisplay.configureMessageDisplay();

      toolbar = new ConfigurableToolbar("MessageWindowToolbar", Pooka.getResources());

      this.getContentPane().add("North", toolbar);
      this.getContentPane().add("Center", messageDisplay);

      toolbar.setActive(this.getActions());

      keyBindings = new ConfigurableKeyBinding(getMessageDisplay(), "ReadMessageWindow.keyBindings", Pooka.getResources());

      //keyBindings.setCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);
      keyBindings.setCondition(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

      keyBindings.setActive(getActions());

    } catch (OperationCancelledException oce) {
    } catch (MessagingException me) {
      showError(Pooka.getProperty("error.MessageFrame.errorLoadingMessage", "Error loading Message:  ") + "\n" + me.getMessage(), Pooka.getProperty("error.MessageFrame.errorLoadingMessage.title", "Error loading message."));
      me.printStackTrace();
    }

    configureInterfaceStyle();

    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          if (getMessageProxy().getMessageUI() == ReadMessageFrame.this) {
            getMessageProxy().setMessageUI(null);
          }
        }
      });

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
   * Attaches the window to a MessagePanel.
   */
  public void attachWindow() {
    if (Pooka.getMainPanel().getContentPanel() instanceof MessagePanel) {
      MessagePanel mp = (MessagePanel) Pooka.getMainPanel().getContentPanel();
      ReadMessageInternalFrame rmif = new ReadMessageInternalFrame(mp, this);
      getMessageProxy().setMessageUI(rmif);
      rmif.openMessageUI();
      this.dispose();
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
      getMessageDisplay().sizeToDefault();
      resizeByWidth();
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


  //------- Actions ----------//

  public Action[] getActions() {

    Action[] actionList;

    if (messageDisplay.getActions() != null) {
      actionList = TextAction.augmentList(messageDisplay.getActions(), getDefaultActions());
    } else
      actionList = getDefaultActions();

    return actionList;
  }


}





