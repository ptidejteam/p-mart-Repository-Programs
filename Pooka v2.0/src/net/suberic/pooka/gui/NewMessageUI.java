package net.suberic.pooka.gui;

public interface NewMessageUI extends MessageUI {
  public void setModified(boolean newValue);

  public boolean isModified();
  
  public String getMessageText();
  
  public String getMessageContentType();
  
  public java.io.File[] getFiles(String Title, String buttonText);
  
  public void attachmentAdded(int index);
  
  public void attachmentRemoved(int index);
  
  public javax.mail.internet.InternetHeaders getMessageHeaders() throws javax.mail.MessagingException;
  
  public net.suberic.pooka.UserProfile getSelectedProfile();
  
  public void setSelectedProfile(net.suberic.pooka.UserProfile newProfile);

  public int promptSaveDraft();

  public void showAddressWindow(AddressEntryTextArea aeta);

  public SendFailedDialog showSendFailedDialog(net.suberic.pooka.OutgoingMailServer pServer, javax.mail.MessagingException me);

}
