package net.suberic.pooka.gui;
import net.suberic.util.gui.propedit.PropertyEditorFactory;
//import net.suberic.util.gui.propedit.DesktopPropertyEditorFactory;
import net.suberic.util.gui.ConfigurableToolbar;
import net.suberic.util.gui.IconManager;
import net.suberic.util.swing.*;
import net.suberic.pooka.*;
import net.suberic.pooka.gui.search.*;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JInternalFrame;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.mail.MessagingException;

/**
 * This is an implementation of PookaUIFactory which creates InternalFrame
 * objects on a JDesktopPane.
 */
public class PookaDesktopPaneUIFactory extends SwingUIFactory {

  MessagePanel messagePanel = null;

  /**
   * Constructor.
   */
  public PookaDesktopPaneUIFactory(PookaUIFactory pSource) {
    if (pSource != null) {
      editorFactory = new PropertyEditorFactory(Pooka.getResources(), pSource.getIconManager(), Pooka.getPookaManager().getHelpBroker());
      pookaThemeManager = pSource.getPookaThemeManager();
      mIconManager = pSource.getIconManager();
      mMessageNotificationManager = pSource.getMessageNotificationManager();
    } else {
      pookaThemeManager = new ThemeManager("Pooka.theme", Pooka.getResources());
      mIconManager = IconManager.getIconManager(Pooka.getResources(), "IconManager._default");
      editorFactory = new PropertyEditorFactory(Pooka.getResources(), mIconManager, Pooka.getPookaManager().getHelpBroker());
      mMessageNotificationManager = new MessageNotificationManager();
    }
  }

  /**
   * Constructor.
   */
  public PookaDesktopPaneUIFactory() {
    this(null);
  }

  /**
   * Creates an appropriate MessageUI object for the given MessageProxy,
   * using the provided MessageUI as a guideline.
   */
  public MessageUI createMessageUI(MessageProxy mp, MessageUI templateMui) throws javax.mail.MessagingException, OperationCancelledException {
    // each MessageProxy can have exactly one MessageUI.
    if (mp.getMessageUI() != null)
      return mp.getMessageUI();

    boolean createExternal = (templateMui != null && templateMui instanceof MessageFrame);

    MessageUI mui;
    if (mp instanceof NewMessageProxy) {
      if (createExternal)
        mui = new NewMessageFrame((NewMessageProxy) mp);
      else
        mui = new NewMessageInternalFrame(getMessagePanel(), (NewMessageProxy) mp);
    } else {
      if (createExternal) {
        mui = new ReadMessageFrame(mp);
      } else {
        mui = new ReadMessageInternalFrame(getMessagePanel(), mp);
        ((ReadMessageInternalFrame)mui).configureMessageInternalFrame();
      }
    }

    mp.setMessageUI(mui);
    return mui;
  }

  /**
   * Opens the given MessageProxy in the default manner for this UI.
   * Usually this will just be callen createMessageUI() and openMessageUI()
   * on it.  However, in some cases (Preview Panel without auto display)
   * it may be necessary to act differently.
   *
   * For this implementation, just calls mp.openWindow().
   */
  public void doDefaultOpen(MessageProxy mp) {
    if (mp != null)
      mp.openWindow();
  }

  /**
   * Creates an appropriate FolderDisplayUI object for the given
   * FolderInfo.
   */
  public FolderDisplayUI createFolderDisplayUI(net.suberic.pooka.FolderInfo fi) {
    // a FolderInfo can only have one FolderDisplayUI.

    if (fi.getFolderDisplayUI() != null)
      return fi.getFolderDisplayUI();

    FolderDisplayUI fw = new FolderInternalFrame(fi, getMessagePanel());
    return fw;
  }

  /**
   * Shows an Editor Window with the given title, which allows the user
   * to edit the values in the properties Vector.  The given properties
   * will be shown according to the values in the templates Vector.
   * Note that there should be an entry in the templates Vector for
   * each entry in the properties Vector.
   */
  public void showEditorWindow(String title, String property, String template) {
    JDialog jd = (JDialog)getEditorFactory().createEditorWindow(title, property, template);
    jd.pack();
    applyNewWindowLocation(jd);
    jd.setVisible(true);
  /*
    JInternalFrame jif = (JInternalFrame)getEditorFactory().createEditorWindow(title, property, template);
    getMessagePanel().add(jif);
    jif.setLocation(getMessagePanel().getNewWindowLocation(jif, true));

    jif.setVisible(true);

    try {
      jif.setSelected(true);
    } catch (java.beans.PropertyVetoException pve) {
    }
  */
  }

  /**
   * Determines the location for new windows.
   */
  public void applyNewWindowLocation(Window f) {
    try {
      Point newLocation = getNewWindowLocation(f);
      f.setLocation(newLocation);
    } catch (Exception e) {
    }
  }

  /**
   * Determines the location for new windows.
   */
  public Point getNewWindowLocation(Window f) throws Exception {
    Point location = Pooka.getMainPanel().getParentFrame().getLocation();
    Dimension mainWindowSize = Pooka.getMainPanel().getParentFrame().getSize();
    Dimension windowSize = f.getSize();
    int yValue = ((mainWindowSize.height - windowSize.height) / 2) + location.y;
    int xValue = ((mainWindowSize.width - windowSize.width) / 2) + location.x;
    return new Point(xValue, yValue);
  }


  /**
   * Creates a JPanel which will be used to show messages and folders.
   *
   * This implementation creates an instance of MessagePanel.
   */
  public ContentPanel createContentPanel() {
    messagePanel = new MessagePanel();
    messagePanel.setSize(1000,1000);
    JScrollPane messageScrollPane = new JScrollPane(messagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    messagePanel.setDesktopManager(messagePanel.new ExtendedDesktopManager(messagePanel, messageScrollPane));
    messagePanel.setUIComponent(messageScrollPane);

    //((PropertyEditorFactory) editorFactory).setDesktop(messagePanel);
    return messagePanel;
  }

  /**
   * Creates a JPanel which will be used to show messages and folders.
   *
   * This implementation creates an instance of MessagePanel.
   */
  public ContentPanel createContentPanel(PreviewContentPanel pcp) {
    messagePanel = new MessagePanel(pcp);
    messagePanel.setSize(1000,1000);
    JScrollPane messageScrollPane = new JScrollPane(messagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    messagePanel.setDesktopManager(messagePanel.new ExtendedDesktopManager(messagePanel, messageScrollPane));
    messagePanel.setUIComponent(messageScrollPane);

    //((DesktopPropertyEditorFactory) editorFactory).setDesktop(messagePanel);
    return messagePanel;
  }


  /**
   * Creates a Toolbar for the MainPanel.
   * This implementation returns null.
   */
  public ConfigurableToolbar createMainToolbar() {
    return null;
  }

  /**
   * Creates a Toolbar for the FolderPanel.
   */
  public ConfigurableToolbar createFolderPanelToolbar() {
    return new ConfigurableToolbar("FolderToolbar", Pooka.getResources());
  }

  /**
   * Returns the MessagePanel associated with this Factory.
   */
  public MessagePanel getMessagePanel() {
    return messagePanel;
  }


  /**
   * This shows an Error Message window.
   */
  public void showError(String errorMessage) {
    showError(errorMessage, Pooka.getProperty("Error", "Error"));
  }

  /**
   * This shows an Error Message window.
   */
  public void showError(String errorMessage, Exception e) {
    showError(errorMessage, Pooka.getProperty("Error", "Error"), e);
  }


  /**
   * Creates a ProgressDialog using the given values.
   */
  public ProgressDialog createProgressDialog(int min, int max, int initialValue, String title, String content) {
    return new ProgressInternalDialog(min, max, initialValue, title, content, getMessagePanel());
  }

  /**
   * Checks to see if the given component is in the main Pooka frame.
   */
  public boolean isInMainFrame(java.awt.Component c) {
    java.awt.Window mainWindow = SwingUtilities.getWindowAncestor(messagePanel);
    java.awt.Window componentWindow = SwingUtilities.getWindowAncestor(c);
    return (mainWindow == componentWindow);
  }


}
