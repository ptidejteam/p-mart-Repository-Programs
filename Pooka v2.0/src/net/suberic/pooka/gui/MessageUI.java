package net.suberic.pooka.gui;
import net.suberic.pooka.*;

public interface MessageUI extends UserProfileContainer, ErrorHandler, ActionContainer {

  public void openMessageUI();

  public void closeMessageUI();

  public MessageProxy getMessageProxy();

  public String showInputDialog(String inputMessage, String title);

  public String showInputDialog(Object[] inputPanels, String title);

  public int showConfirmDialog(String message, String title, int optionType, int messageType);

  public void showMessageDialog(String message, String title);

  public net.suberic.util.swing.ProgressDialog createProgressDialog(int min, int max, int initialValue, String title, String content);

  public void setBusy(boolean newValue);

  public void setEnabled(boolean newValue);

  public void refreshDisplay() throws javax.mail.MessagingException, net.suberic.pooka.OperationCancelledException;

  /**
   * Shows the current display of the encryption status.
   */
  public net.suberic.pooka.gui.crypto.CryptoStatusDisplay getCryptoStatusDisplay();


}
