package net.suberic.pooka.gui;
import net.suberic.util.gui.propedit.PropertyEditorFactory;
import net.suberic.util.gui.ConfigurableToolbar;
import net.suberic.util.gui.IconManager;
import net.suberic.util.swing.*;
import net.suberic.pooka.*;
import net.suberic.pooka.gui.search.*;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.*;

/**
 * This is an implementation of PookaUIFactory which creates a single
 * panel which shows the list of messages in the folder and a preview
 * pane which shows the message itself.  You should also be able to
 * open messages in individual Frames.  New messages go into individual
 * Frames, also.
 */
public class PookaPreviewPaneUIFactory extends SwingUIFactory {

  PreviewContentPanel contentPanel = null;

  /**
   * Constructor.
   */
  public PookaPreviewPaneUIFactory(PookaUIFactory pSource) {
    if (pSource != null) {
      editorFactory = new PropertyEditorFactory(Pooka.getResources(), pSource.getIconManager(), Pooka.getPookaManager().getHelpBroker());
      pookaThemeManager = new ThemeManager("Pooka.theme", Pooka.getResources());
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
  public PookaPreviewPaneUIFactory() {
    this(null);
  }

  /**
   * Creates an appropriate MessageUI object for the given MessageProxy,
   * using the provided MessageUI as a guideline.
   *
   * Note that this implementation ignores the mui component.
   */
  public MessageUI createMessageUI(MessageProxy mp, MessageUI templateMui) {
    // each MessageProxy can have exactly one MessageUI.
    if (mp.getMessageUI() != null)
      return mp.getMessageUI();

    MessageUI mui;
    if (mp instanceof NewMessageProxy) {
      mui = new NewMessageFrame((NewMessageProxy) mp);
    } else
      mui = new ReadMessageFrame(mp);

    mp.setMessageUI(mui);

    applyNewWindowLocation((JFrame)mui);
    return mui;
  }

  /**
   * Opens the given MessageProxy in the default manner for this UI.
   * Usually this will just be callen createMessageUI() and openMessageUI()
   * on it.  However, in some cases (Preview Panel without auto display)
   * it may be necessary to act differently.
   *
   */
  public void doDefaultOpen(MessageProxy mp) {
    if (contentPanel.getAutoPreview()) {
      if (mp != null)
        mp.openWindow();
    } else {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            contentPanel.refreshCurrentMessage();
          }
        });
    }
  }

  /**
   * Creates an appropriate FolderDisplayUI object for the given
   * FolderInfo.
   */
  public FolderDisplayUI createFolderDisplayUI(net.suberic.pooka.FolderInfo fi) {
    // a FolderInfo can only have one FolderDisplayUI.

    if (fi.getFolderDisplayUI() != null)
      return fi.getFolderDisplayUI();

    PreviewFolderPanel fw = new PreviewFolderPanel(contentPanel, fi);
    contentPanel.addPreviewPanel(fw, fi.getFolderID());
    return fw;

  }

  /**
   * Creates a JPanel which will be used to show messages and folders.
   *
   * This implementation creates an instance of PreviewContentPanel.
   */
  public ContentPanel createContentPanel() {
    contentPanel = new PreviewContentPanel();
    contentPanel.setSize(1000,1000);

    return contentPanel;
  }

  /**
   * Creates a Toolbar for the MainPanel.
   */
  public ConfigurableToolbar createMainToolbar() {    return new ConfigurableToolbar("MainToolbar", Pooka.getResources());
  }

  /**
   * Creates a Toolbar for the FolderPanel.
   */
  public ConfigurableToolbar createFolderPanelToolbar() {
    return null;
  }

  /**
   * Creates a JPanel which will be used to show messages and folders.
   *
   * This implementation creates an instance PreviewConentPanel from a
   * given MessagePanel.
   */
  public ContentPanel createContentPanel(MessagePanel mp) {
    contentPanel = new PreviewContentPanel(mp);
    contentPanel.setSize(1000,1000);

    return contentPanel;
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
  }

  /**
   * This shows an Confirm Dialog window.  We include this so that
   * the MessageProxy can call the method without caring abou the
   * actual implementation of the Dialog.
   */
  public int showConfirmDialog(String messageText, String title, int type) {
    String displayMessage = formatMessage(messageText);
    final ResponseWrapper fResponseWrapper = new ResponseWrapper();
    final String fDisplayMessage = displayMessage;
    final String fTitle = title;
    final int fType = type;
    Runnable runMe = new Runnable() {
        public void run() {
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
   * Shows a Confirm dialog with the given Object[] as the Message.
   */
  public int showConfirmDialog(Object[] messageComponents, String title, int type) {
    final ResponseWrapper fResponseWrapper = new ResponseWrapper();
    final Object[] fMessageComponents = messageComponents;
    final String fTitle = title;
    final int fType = type;
    Runnable runMe = new Runnable() {
        public void run() {
          fResponseWrapper.setInt(JOptionPane.showConfirmDialog(contentPanel.getUIComponent(), fMessageComponents, fTitle, fType));
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
   * the MessageProxy can call the method without caring abou the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage) {
    showError(errorMessage, Pooka.getProperty("Error", "Error"));
  }

  /**
   * This shows an Error Message window.  We include this so that
   * the MessageProxy can call the method without caring abou the
   * actual implementation of the Dialog.
   */
  public void showError(String errorMessage, Exception e) {
    showError(errorMessage, Pooka.getProperty("Error", "Error"), e);
  }

  /**
   * Creates a ProgressDialog using the given values.
   */
  public ProgressDialog createProgressDialog(int min, int max, int initialValue, String title, String content) {
    return new ProgressDialogImpl(min, max, initialValue, title, content);
  }

  /**
   * Shows an Address Selection form for the given AddressEntryTextArea.
   */
  public void showAddressWindow(AddressEntryTextArea aeta) {
    JFrame jf = new JFrame(Pooka.getProperty("AddressBookTable.title", "Choose Address"));
    jf.getContentPane().add(new AddressBookSelectionPanel(aeta, jf));
    jf.pack();
    applyNewWindowLocation(jf);
    jf.setVisible(true);
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

  int lastX = 20;
  int lastY = 20;
  boolean firstPlacement = true;

  /**
   * Determines the location for new windows.
   */
  public Point getNewWindowLocation(Window f) throws Exception {
    if (firstPlacement) {
      Point location = Pooka.getMainPanel().getParentFrame().getLocation();
      lastX = location.x;
      lastY = location.y;
      firstPlacement = false;
    }
    GraphicsConfiguration conf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

    Rectangle bounds = conf.getBounds();

    int baseDelta = 20;

    Dimension componentSize = f.getSize();

    int currentX = lastX + baseDelta;
    int currentY = lastY + baseDelta;
    if (currentX + componentSize.width > bounds.x + bounds.width) {
      currentX = bounds.x;
    }

    if (currentY + componentSize.height > bounds.y + bounds.height) {
      currentY = bounds.y;
    }

    lastX = currentX;
    lastY = currentY;

    return new Point(currentX, currentY);
  }

}
