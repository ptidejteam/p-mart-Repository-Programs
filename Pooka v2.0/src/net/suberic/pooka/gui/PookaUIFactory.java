package net.suberic.pooka.gui;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.util.swing.ProgressDialog;
import net.suberic.util.gui.ConfigurableToolbar;
import net.suberic.util.gui.IconManager;

public interface PookaUIFactory extends ErrorHandler {

  /**
   * Returns the PookaThemeManager for fonts and colors.
   */
  public net.suberic.util.swing.ThemeManager getPookaThemeManager();

  /**
   * Creates an appropriate MessageUI object for the given MessageProxy.
   */
  public MessageUI createMessageUI(MessageProxy mp) throws javax.mail.MessagingException, OperationCancelledException;

  /**
   * Creates an appropriate MessageUI object for the given MessageProxy,
   * using the provided MessageUI as a guideline.
   */
  public MessageUI createMessageUI(MessageProxy mp, MessageUI mui) throws javax.mail.MessagingException, OperationCancelledException;

  /**
   * Opens the given MessageProxy in the default manner for this UI.
   * Usually this will just be callen createMessageUI() and openMessageUI()
   * on it.  However, in some cases (Preview Panel without auto display)
   * it may be necessary to act differently.
   */
  public void doDefaultOpen(MessageProxy mp);

  /**
   * Creates an appropriate FolderDisplayUI object for the given
   * FolderInfo.
   */
  public FolderDisplayUI createFolderDisplayUI(net.suberic.pooka.FolderInfo fi);

  /**
   * Creates a ContentPanel which will be used to show messages and folders.
   */
  public ContentPanel createContentPanel();

  /**
   * Creates a Toolbar for the MainPanel.
   */
  public ConfigurableToolbar createMainToolbar();

  /**
   * Creates a Toolbar for the FolderPanel.
   */
  public ConfigurableToolbar createFolderPanelToolbar();

  /**
   * Shows an Editor Window with the given title, which allows the user
   * to edit the given property.
   */
  public void showEditorWindow(String title, String property);

  /**
   * Shows an Editor Window with the given title, which allows the user
   * to edit the given property, which is in turn defined by the
   * given template.
   */
  public void showEditorWindow(String title, String property, String template);

  /**
   * Returns the PropertyEditorFactory used by this component.
   */
  public net.suberic.util.gui.propedit.PropertyEditorFactory getEditorFactory();
  /**
   * Sets the PropertyEditorFactory used by this component.
   */
  public void setEditorFactory(net.suberic.util.gui.propedit.PropertyEditorFactory pEditorFactory);

  /**
   * Shows a Confirm dialog.
   */
  public int showConfirmDialog(String message, String title, int type);

  /**
   * Shows a Confirm dialog with the given Object[] as the Message.
   */
  public int showConfirmDialog(Object[] messageComponents, String title, int type);

  /**
   * This shows an Input window.
   */
  public String showInputDialog(String inputMessage, String title);

  /**
   * Shows an Input window.
   */
  public String showInputDialog(Object[] inputPanels, String title);

  /**
   * Shows a status message.
   */
  public void showStatusMessage(String newMessage);

  /**
   * Shows a message.
   */
  public void showMessage(String newMessage, String title);

  /**
   * Creates a ProgressDialog using the given values.
   */
  public ProgressDialog createProgressDialog(int min, int max, int initialValue, String title, String content);

  /**
   * Clears the main status message panel.
   */
  public void clearStatus();

  /**
   * Shows a SearchForm with the given FolderInfos selected from the list
   * of the given allowedValues.
   */
  public void showSearchForm(net.suberic.pooka.FolderInfo[] selectedFolders, java.util.Vector allowedValues);

  /**
   * Shows a SearchForm with the given FolderInfos selected.  The allowed
   * values will be the list of all available Folders.
   */
  public void showSearchForm(net.suberic.pooka.FolderInfo[] selectedFolders);

  /**
   * This tells the factory whether or not its ui components are showing
   * yet or not.
   */
  public void setShowing(boolean newValue);

  /**
   * Gets the current MessageNotificationManager.
   */
  public MessageNotificationManager getMessageNotificationManager();

  /**
   * Gets the IconManager for this UI.
   */
  public net.suberic.util.gui.IconManager getIconManager();

  /**
   * Sets the IconManager for this UI.
   */
  public void setIconManager(net.suberic.util.gui.IconManager pIconManager);

  /**
   * Creates an AuthenticatorUI.
   */
  public AuthenticatorUI createAuthenticatorUI();

}
