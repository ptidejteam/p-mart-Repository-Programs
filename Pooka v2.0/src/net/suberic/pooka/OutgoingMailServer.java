package net.suberic.pooka;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import net.suberic.util.*;
import net.suberic.util.thread.*;

/**
 * <p>Represents a mail server than can be used to send outgoing messages.
 *
 * @author Allen Petersen
 * @version $Revision$
 */
public class OutgoingMailServer implements net.suberic.util.Item, net.suberic.util.ValueChangeListener, NetworkConnectionListener {

  String id = null;

  String propertyName = null;

  protected URLName sendMailURL = null;

  String connectionID = null;

  String outboxID = null;

  String mProtocol = "smtp://";

  NetworkConnection.ConnectionLock connectionLock = null;

  boolean sending = false;

  boolean mStopped = false;

  ActionThread mailServerThread = null;

  /**
   * <p>Creates a new OutgoingMailServer from the given property.</p>
   */
  public OutgoingMailServer (String newId) {
    id = newId;
    propertyName = "OutgoingServer." + newId;

    configure();
  }

  /**
   * <p>Configures this mail server.</p>
   */
  protected void configure() {
    VariableBundle bundle = Pooka.getResources();

    connectionID = bundle.getProperty(getItemProperty() + ".connection", "");
    NetworkConnection currentConnection = getConnection();
    if (currentConnection != null)
      currentConnection.addConnectionListener(this);


    if (Pooka.getProperty(getItemProperty() + ".authenticated", "false").equalsIgnoreCase("true")) {
      String mProtocol = "smtps://";
    } else {
      String mProtocol = "smtp://";
    }

    sendMailURL = new URLName(mProtocol + bundle.getProperty(getItemProperty() + ".server", "") + ":" + bundle.getProperty(getItemProperty() + ".port", "") + "/");

    outboxID = bundle.getProperty(getItemProperty() + ".outbox", "");

    bundle.addValueChangeListener(this, getItemProperty() + ".connection");
    bundle.addValueChangeListener(this, getItemProperty() + ".server");
    bundle.addValueChangeListener(this, getItemProperty() + ".outbox");

    mailServerThread = new net.suberic.util.thread.ActionThread(getItemID() + " - smtp thread");
    mailServerThread.start();
  }

  /**
   * <p>Called when one of the values that defines this OutgoingMailServer
   * is changed.</p>
   */
  public void valueChanged(String changedValue) {
    VariableBundle bundle = Pooka.getResources();

    if (changedValue != null) {
      if (changedValue.equals(getItemProperty() + ".connection")) {
        NetworkConnection currentConnection = getConnection();
        if (currentConnection != null)
          currentConnection.removeConnectionListener(this);

        connectionID = bundle.getProperty(getItemProperty() + ".connection", "");
        currentConnection = getConnection();
        if (currentConnection != null)
          currentConnection.addConnectionListener(this);

      } else if (changedValue.equals(getItemProperty() + ".server")) {
        sendMailURL = new URLName(mProtocol + bundle.getProperty(getItemProperty() + ".server", "") + ":" + bundle.getProperty(getItemProperty() + ".port", "") + "/");
      } else if (changedValue.equals(getItemProperty() + ".outbox")) {
        String newOutboxID = bundle.getProperty(getItemProperty() + ".outbox", "");
        if (newOutboxID != outboxID) {
          FolderInfo outbox = getOutbox();
          if (outbox != null) {
            outbox.setOutboxFolder(null);
          }

          outboxID = newOutboxID;
          loadOutboxFolder();
        }
      }
    }
  }

  /**
   * Loads the outbox folders.
   */
  public void loadOutboxFolder() {
    FolderInfo outbox = getOutbox();
    if (outbox != null) {
      outbox.setOutboxFolder(this);
    }
  }

  /**
   * Stops the server so that it will continue to send its current
   * message(s), but will not accept any new ones.
   */
  public void stopServer() {
    mStopped = true;
  }


  /**
   * Stops the thread.
   */
  public void stopThread() {
    if (mailServerThread != null) {
      mailServerThread.setStop(true);
      mailServerThread = null;
    }
  }

  /**
   * Sends all available messages in the outbox.
   */
  public void sendAll() {
    mailServerThread.addToQueue(new javax.swing.AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent ae) {
          try {
            internal_sendAll();
          } catch (OperationCancelledException oce) {
          } catch (javax.mail.MessagingException me) {
            Pooka.getUIFactory().showError(Pooka.getProperty("Error.sendingMessage", "Error sending message:  "), me);
          }
        }
      }, new java.awt.event.ActionEvent(this, 0, "message-send-all"));
  }

  /**
   * Sends all available messages in the outbox.
   */
  protected synchronized void internal_sendAll() throws javax.mail.MessagingException, OperationCancelledException {

    try {
      sending = true;

      Transport sendTransport = prepareTransport(true);

      try {
        sendTransport.connect();

        sendAll(sendTransport);
      } finally {
        sendTransport.close();
      }
    } finally {
      sending = false;
      if (connectionLock != null)
        connectionLock.release();
    }
  }

  /**
   * Sends all available messages in the outbox using the given, already
   * open Transport object.  Leaves the Transport object open.
   */
  private void sendAll(Transport sendTransport) throws javax.mail.MessagingException, OperationCancelledException {

    LinkedList exceptionList = new LinkedList();

    FolderInfo outbox = getOutbox();

    if (outbox != null) {
      // we need the thread lock for this folder.
      Object runLock = outbox.getFolderThread().getRunLock();
      synchronized(runLock) {
        if ( ! outbox.isConnected()) {
          outbox.openFolder(Folder.READ_WRITE);
        }

        Folder outboxFolder = outbox.getFolder();

        if (outboxFolder != null) {
          Message[] msgs = outboxFolder.getMessages();

          try {
            for (int i = 0; i < msgs.length; i++) {
              Message m = msgs[i];
              if (! m.isSet(Flags.Flag.DRAFT)) {
                try {
                  sendTransport.sendMessage(m, m.getAllRecipients());
                  m.setFlag(Flags.Flag.DELETED, true);
                } catch (MessagingException me) {
                  exceptionList.add(me);
                }
              }
            }
          } finally {
            if (exceptionList.size() > 0) {
              final int exceptionCount = exceptionList.size();
              javax.swing.SwingUtilities.invokeLater( new Runnable() {
                  public void run() {
                    Pooka.getUIFactory().showError(Pooka.getProperty("error.OutgoingServer.queuedSendFailed", "Failed to send message(s) in the Outbox.  Number of errors:  ") +  exceptionCount );
                  }
                } );
            }
            outboxFolder.expunge();
          }
        }
      }
    }
  }

  /**
   * Virtually sends a message.  If the current status is connected, then
   * the message will actually be sent now.  If not, and the
   * Message.sendImmediately setting is true, then we'll attempt to send
   * the message anyway.
   */
  public synchronized void sendMessage(NewMessageInfo nmi, boolean connect) {
    if (mStopped) {
      throw new IllegalStateException("MailServer " + getItemID() + " has been stopped and is not accepting new messages.");
    }

    final NewMessageInfo nmi_final = nmi;
    final boolean connect_final = connect;

    mailServerThread.addToQueue(new javax.swing.AbstractAction() {
        public void actionPerformed(java.awt.event.ActionEvent ae) {
          internal_sendMessage(nmi_final, connect_final);
        }
      }, new java.awt.event.ActionEvent(this, 0, "message-send-all"));
  }

  private synchronized void internal_sendMessage(NewMessageInfo nmi, boolean connect) {
    sending = true;

    try {
      boolean connected = false;
      Transport sendTransport = null;
      try {
        Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.smtpServer.connecting", "Connecting to SMTP server..."));

        sendTransport = prepareTransport(connect);
        sendTransport.connect();
        connected = true;
      } catch (MessagingException me) {
        if (Pooka.getProperty("Pooka.outbox.autoSave", "false").equalsIgnoreCase("true")) {
          try {
            saveToOutbox(nmi);
          } catch (MessagingException nme) {
            ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendFailed(this, nme);
          } catch (OperationCancelledException oce) {
            ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendFailed(this, new MessagingException("Connection cancelled."));
          }
        } else {
          ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendFailed(this, me);
        }
      }

      // if the connection worked.
      if (connected) {
        try {
          try {
            /*
              Message m = nmi.getMessage();
              sendTransport.sendMessage(m, m.getAllRecipients());
            */
            Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.smtpServer.sending", "Sending message over SMTP."));

            Map sendMessageMap = nmi.getSendMessageMap();
            if (sendMessageMap != null && sendMessageMap.keySet().size() > 0) {
              Set keySet = sendMessageMap.keySet();
              Iterator iter = keySet.iterator();
              while (iter.hasNext()) {
                Message m = (Message) iter.next();
                Address[] recipients = (Address[]) sendMessageMap.get(m);
                if (recipients == null)
                  recipients = m.getAllRecipients();

                sendTransport.sendMessage(m, recipients);
              }
            } else {
              sendTransport.sendMessage(nmi.getMessage(), nmi.getMessage().getAllRecipients());
            }

            ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendSucceeded(true);
          } catch (MessagingException me) {
            ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendFailed(this, me);
          }
          // whether or not the send failed. try sending all the other
          // messages in the queue, too.
          try {
            sendAll(sendTransport);
          } catch (Exception exp) {
            final Exception me = exp;
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  Pooka.getUIFactory().showError(Pooka.getProperty("error.OutgoingServer.outboxSendFailed", "Failed to send all messages in Outbox:  "), me);
                }
              });
          }
        } finally {
          try {
            sendTransport.close();
          } catch (MessagingException me) {
            // we don't care.
          }
        }
      }
    } finally {
      sending = false;
      if (connectionLock != null)
        connectionLock.release();
    }
  }

  /**
   * Virtually sends a message.  If the current status is connected, then
   * the message will actually be sent now.
   */
  public void sendMessage(NewMessageInfo nmi) {
    sendMessage(nmi, false);
  }

  /**
   * Gets a Transport object for this OutgoingMailServer.
   */
  protected Transport prepareTransport(boolean connect) throws javax.mail.MessagingException {

    NetworkConnection connection = getConnection();

    if (connect) {
      if (connection.getStatus() == NetworkConnection.DISCONNECTED) {
        connection.connect();
      }
    }

    if (connection.getStatus() != NetworkConnection.CONNECTED) {
      throw new MessagingException(Pooka.getProperty("error.connectionDown", "Connection down for Mail Server:  ") + getItemID());
    } else {
      connectionLock = connection.getConnectionLock();
    }

    Session session = Pooka.getDefaultSession();


    boolean useAuth = Pooka.getProperty(getItemProperty() + ".authenticated", "false").equalsIgnoreCase("true");

    if (useAuth) {
      java.util.Properties sysProps = new java.util.Properties(System.getProperties());
      sysProps.setProperty("mail.mbox.mailspool", Pooka.getProperty("Pooka.spoolDir", "/var/spool/mail"));
      sysProps.setProperty("mail.smtp.auth", "true");
      String userName = Pooka.getProperty(getItemProperty() + ".user", "");
      if (! userName.equals(""))
        sysProps.setProperty("mail.smtp.user", userName);
      String password = Pooka.getProperty(getItemProperty() + ".password", "");

      if (! password.equals("")) {
        password = net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(password);
        sysProps.setProperty("mail.smtp.password", password);
      }

      sysProps.setProperty("mail.smtp.starttls.enable", "true");

      session = javax.mail.Session.getInstance(sysProps, new FailoverAuthenticator (userName, password));
      if (Pooka.getProperty("Pooka.sessionDebug", "false").equalsIgnoreCase("true"))
        session.setDebug(true);
    }
    Transport sendTransport = session.getTransport(getSendMailURL());
    return sendTransport;
  }

  /**
   * Saves the given message to the Outbox for sending later.
   */
  public void saveToOutbox(NewMessageInfo nmi) throws MessagingException, OperationCancelledException {
    Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.smtpServer.outbox.connecting", "SMTP server not available; getting outbox."));

    FolderInfo outbox = getOutbox();

    if (outbox != null) {
      Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.smtpServer.outbox.waitForLock", "SMTP server not available; waiting for outbox lock."));

      // we need the lock
      Object runLock = outbox.getFolderThread().getRunLock();
      synchronized(runLock) {
        try {
          if ( ! outbox.isConnected()) {
            Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.smtpServer.outbox.opening", "SMTP server not available; opening outbox."));
            outbox.openFolder(Folder.READ_WRITE);
          }

          Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.smtpServer.outbox.appending", "SMTP server not available; appending to outbox."));
          outbox.appendMessages(new MessageInfo[] { nmi });

          if (Pooka.getProperty("Pooka.outbox.autoSave", "false").equalsIgnoreCase("true")) {
            // assume that if we aren't automatically saving to the outbox
            // that we did this explicitly.
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  Pooka.getUIFactory().showError(Pooka.getProperty("error.MessageWindow.sendDelayed", "Connection unavailable.  Message saved to Outbox."));
                }
              });
          }

          ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendSucceeded(true);
        } catch(MessagingException nme) {
          ((net.suberic.pooka.gui.NewMessageProxy)nmi.getMessageProxy()).sendFailed(this, nme);
        }
      }
    } else {
      throw new MessagingException("Error saving to Outbox -- no Outbox specified.");
    }
  }

  /**
   * <p>The NetworkConnection that this OutgoingMailServer depends on.
   */
  public NetworkConnection getConnection() {
    NetworkConnectionManager connectionManager = Pooka.getConnectionManager();
    NetworkConnection returnValue = connectionManager.getConnection(connectionID);
    if (returnValue != null)
      return returnValue;
    else
      return connectionManager.getDefaultConnection();
  }

  /**
   * Notifies this component that the state of a network connection has
   * changed.
   */
  public void connectionStatusChanged(NetworkConnection connection, int newStatus) {
    if (newStatus == NetworkConnection.CONNECTED && ! sending && Pooka.getProperty(getItemProperty() + ".sendOnConnect", "false").equalsIgnoreCase("true")) {
      sendAll();
    }
  }

  /**
   * <p>The FolderInfo where messages for this MailServer are
   * stored until they're ready to be sent.
   */
  public FolderInfo getOutbox() {

    return Pooka.getStoreManager().getFolder(outboxID);
  }

  /**
   * <p>The Item ID for this OutgoingMailServer.</p>
   */
  public String getItemID() {
    return id;
  }

  /**
   * <p>The Item property for this OutgoingMailServer.  This is usually
   * OutgoingMailServer.<i>itemID</i>.</p>
   */
  public String getItemProperty() {
    return propertyName;
  }

  /**
   * Returns the sendMailURL.
   */
  public URLName getSendMailURL() {
    return sendMailURL;
  }

  /**
   * Returns true if this MailServer is currently in the process of sending
   * a message.
   */
  public boolean isSending() {
    return sending;
  }

  /**
   * Returns true if this MailServer has been set not to send any more messages
   * this session.
   */
  public boolean isStopped() {
    return mStopped;
  }

  /**
   * An Authenticator that first tries the configured User and Password,
   * then uses the underlying Authenticator.
   */
  class FailoverAuthenticator extends net.suberic.pooka.gui.SimpleAuthenticator {

    String mUser;
    String mPassword;
    boolean firstTry = true;

    Authenticator mAuth;

    /**
     * Creates an Authenticator that will try using the given username and
     * password, and if that fails, then will try a ui dialog.
     */
    public FailoverAuthenticator(String pUser, String pPassword) {
      super();
      mUser = pUser;
      mPassword = pPassword;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
      if (firstTry) {
        firstTry = false;

        return new PasswordAuthentication(mUser, mPassword);
      } else {
        return super.getPasswordAuthentication();
      }
    }

  }
}
