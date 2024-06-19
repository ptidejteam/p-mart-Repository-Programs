package net.suberic.pooka;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * This class represents a folder which stores outgoing messages until they
 * can connect to the SMTP server.
 */
public class OutgoingFolderInfo extends FolderInfo {
  URLName transportURL;
  Thread sendMailThread = new net.suberic.util.thread.ActionThread(Pooka.getProperty("SendMailThread.name", "Send Mail Thread"));
  boolean online = true;

  /**
   * Creates a new OutgoingFolderInfo for the given URLName.
   */
  public OutgoingFolderInfo(StoreInfo parent, String fname, URLName outgoingURL) {
    super(parent, fname);
    transportURL = outgoingURL;
  }

  /**
   * Sends all available messages.
   */
  public void sendAll() throws javax.mail.MessagingException {

    Transport sendTransport = Pooka.getDefaultSession().getTransport(transportURL);
    try {
      sendTransport.connect();

      Message[] msgs = getFolder().getMessages();

      try {
  for (int i = 0; i < msgs.length; i++) {
    Message m = msgs[i];
    if (! m.isSet(Flags.Flag.DRAFT)) {
      sendTransport.sendMessage(m, m.getAllRecipients());
      m.setFlag(Flags.Flag.DELETED, true);
    }
  }
      } finally {
  getFolder().expunge();
      }
    } finally {
      sendTransport.close();
    }
  }

  /**
   * Virtually sends a message.  If the current status is connected, then
   * the message will actually be sent now.  If not, and the
   * Message.sendImmediately setting is true, then we'll attempt to send
   * the message anyway.
   */
  public void sendMessage(NewMessageInfo nmi, boolean connect) throws javax.mail.MessagingException, OperationCancelledException {
    this.appendMessages(new MessageInfo[] { nmi });
    if (online)
      sendAll();
    else if (connect || Pooka.getProperty("Message.sendImmediately", "false").equalsIgnoreCase("true")) {
      try {
  connect();
  if (online)
    sendAll();
      } catch (MessagingException me) {
  System.err.println("me is a " + me);
  online = false;
      }
    }
  }

  /**
   * Virtually sends a message.  If the current status is connected, then
   * the message will actually be sent now.
   */
  public void sendMessage(NewMessageInfo nmi) throws javax.mail.MessagingException, OperationCancelledException {
    sendMessage(nmi, false);
  }

  /**
   * Attempts to connect this OutgoingFolderInfo.
   */
  public void connect() {
    // does nothing right now.
    online=true;
  }
}
