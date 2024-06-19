package net.suberic.pooka;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageChangedEvent;
import java.io.*;
import java.util.Vector;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import net.suberic.pooka.cache.ChangeCache;
import net.suberic.pooka.gui.AuthenticatorUI;
import net.suberic.pooka.gui.MessageProxy;

/**
 * This represents the Inbox of a Pop3 folder.  It has an mbox backend, but
 * uses the pop folder to get new messages.
 */
public class PopInboxFolderInfo extends FolderInfo {

  Store popStore;
  AuthenticatorUI mAuthenticator;
  Folder popInbox;
  ChangeCache changeAdapter;
  String mailHome;

  Set uidsRead = new HashSet();

  public static String UID_HEADER = "X-Pooka-Pop-UID";

  /**
   * Creates a new FolderInfo from a parent StoreInfo and a Folder
   * name.
   */

  public PopInboxFolderInfo(StoreInfo parent, String fname) {
    super(parent, fname);

    // create the pop folder.
    configurePopStore(parent.getStoreID());
  }

  private void configurePopStore(String storeID) {
    String user = Pooka.getProperty("Store." + storeID + ".user", "");
    String password = Pooka.getProperty("Store." + storeID + ".password", "");
    if (!password.equals(""))
      password = net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(password);
    String server = Pooka.getProperty("Store." + storeID + ".server", "");
    String protocol = Pooka.getProperty("Store." + storeID + ".protocol", "");
    if (Pooka.getProperty(getParentStore().getStoreProperty() + ".SSL", "false").equalsIgnoreCase("true")) {
      protocol = "pop3s";
    }

    URLName url = new URLName(protocol, server, -1, "", user, password);

    mailHome = Pooka.getProperty("Store." + storeID + ".mailDir", "");
    if (mailHome.equals("")) {
      mailHome = Pooka.getProperty("Pooka.defaultMailSubDir", "");
      if (mailHome.equals("")) {
        mailHome = Pooka.getPookaManager().getPookaRoot().getAbsoluteFile() + File.separator + ".pooka";
      } else {
        mailHome = Pooka.getResourceManager().translateName(mailHome);
      }
      mailHome = mailHome + File.separator + storeID;
    }
    String inboxFileName = mailHome + File.separator + Pooka.getProperty("Pooka.inboxName", "INBOX");
    String userHomeName = mailHome + File.separator + Pooka.getProperty("Pooka.subFolderName", "folders");

    try {
      File userHomeDir = new File(userHomeName);
      if (! userHomeDir.exists())
        userHomeDir.mkdirs();

      File inboxFile = new File(inboxFileName);
      if (! inboxFile.exists())
        inboxFile.createNewFile();
    } catch (Exception e) {
      Pooka.getUIFactory().showError(Pooka.getProperty("error.cannotCreatePopFolders", "Error:  could not create local folder."), e);
    }

    changeAdapter = new ChangeCache(new File(mailHome));

    try {
      java.util.Properties props = new java.util.Properties(System.getProperties());

      if (Pooka.getProperty(getParentStore().getStoreProperty() + ".SSL", "false").equalsIgnoreCase("true")) {
        //props.setProperty("mail.pop3s.socketFactory.class", "net.suberic.pooka.ssl.PookaSSLSocketFactory");
        props.setProperty("mail.pop3s.socketFactory.fallback", Pooka.getProperty(getParentStore().getStoreProperty() + ".SSL.fallback", "false"));
        //props.setProperty("mail.pop3s.socketFactory.port", Pooka.getProperty(getParentStore().getStoreProperty() + ".SSL.port", "995"));
      }

      mAuthenticator = Pooka.getUIFactory().createAuthenticatorUI();
      Session session = javax.mail.Session.getInstance(props, mAuthenticator);

      if (Pooka.isDebug()) {
        System.out.println("session.getProperty(mail.mbox.inbox) = " + session.getProperty("mail.mbox.inbox"));
        System.out.println("url is " + url);
      }
      popStore = session.getStore(url);
    } catch (NoSuchProviderException nspe) {
      nspe.printStackTrace();
      // available=false;
    }
  }


  /**
   * This method opens the Folder, and sets the FolderInfo to know that
   * the Folder should be open.  You should use this method instead of
   * calling getFolder().open(), because if you use this method, then
   * the FolderInfo will try to keep the Folder open, and will try to
   * reopen the Folder if it gets closed before closeFolder is called.
   *
   * This method can also be used to reset the mode of an already
   * opened folder.
   */
  public void openFolder(int mode, boolean pConnectStore) throws MessagingException, OperationCancelledException {
    // identical to FolderInfo.openFolder() except that we don't check
    // to make sure that the mode matches.

    if (Pooka.isDebug())
      System.out.println(this + ":  checking parent store.");

    if (!getParentStore().isConnected() && pConnectStore) {
      if (Pooka.isDebug())
        System.out.println(this + ":  parent store isn't connected.  trying connection.");
      getParentStore().connectStore();
    }

    if (Pooka.isDebug())
      System.out.println(this + ":  loading folder.");

    if (! isLoaded() && status != CACHE_ONLY)
      loadFolder();

    if (Pooka.isDebug())
      System.out.println(this + ":  folder loaded.  status is " + status);

    if (Pooka.isDebug())
      System.out.println(this + ":  checked on parent store.  trying isLoaded() and isAvailable().");

    if (status == CLOSED || status == LOST_CONNECTION || status == DISCONNECTED) {
      if (Pooka.isDebug())
        System.out.println(this + ":  isLoaded() and isAvailable().");
      if (getFolder().isOpen()) {
        return;
      } else {
        getFolder().open(mode);
        updateFolderOpenStatus(true);
        resetMessageCounts();
      }
    } else if (status == INVALID) {
      throw new MessagingException(Pooka.getProperty("error.folderInvalid", "Error:  folder is invalid.  ") + getFolderID());
    }
  }

  /**
   * <p> Loads all Messages into a new FolderTableModel, sets this
   * FolderTableModel as the current FolderTableModel, and then returns
   * said FolderTableModel.  This is the basic way to populate a new
   * FolderTableModel.</p>
   */
  public synchronized void loadAllMessages() throws MessagingException, OperationCancelledException {
    if (folderTableModel == null) {
      super.loadAllMessages();
      // let's see how bad performance is for the mbox provider.  :)
      populateUidMap();
      checkFolder();
    }
  }

  /**
   * <p>Populates the UID map from the list of MessageProxies.</p>
   */
  void populateUidMap() {
    if (folderTableModel != null) {
      List v = folderTableModel.getAllProxies();
      for (int i = 0; i < v.size(); i++) {
        MessageProxy mp = (MessageProxy) v.get(i);
        try {
          String uid = (String) mp.getMessageInfo().getMessageProperty(UID_HEADER);
          uidsRead.add(uid);
          if (Pooka.isDebug())
            System.out.println("adding " + uid + " to read list.");
        } catch (MessagingException me) {
          Pooka.getUIFactory().showError("Error getting UID for message:  ", me);
        }
      }
    }
  }

  /**
   * Checks the pop folder for new messages.  If deleteOnServerOnLocalDelete
   * is set to true, this will also go through and remove any messages
   * on the server that have been removed on the local client.
   */
  public void checkFolder() throws MessagingException {
    if (Pooka.isDebug())
      System.out.println("checking folder " + getFolderName());

    Folder f = null;

    if (isConnected() && popStore != null) {
      if (Pooka.isDebug())
        System.out.println("checking folder " + getFolderName() + ":  opening pop store.");

      NetworkConnection connection = getParentStore().getConnection();
      int originalStatus = -1;

      if (connection != null) {
        originalStatus = connection.getStatus();
        if (connection.getStatus() == NetworkConnection.DISCONNECTED) {
          connection.connect();
        }

        if (connection.getStatus() != NetworkConnection.CONNECTED) {
          throw new MessagingException(Pooka.getProperty("error.connectionDown", "Connection down for checking folder:  ") + getFolderID());
        }
      }

      try {
        popStore.connect();
        f = popStore.getDefaultFolder().getFolder("INBOX");
        if (f != null) {
          f.open(Folder.READ_WRITE);
          Message[] msgs = getNewMessages(f);

          if (msgs != null && msgs.length > 0) {
            MimeMessage[] msgsToAppend = new MimeMessage[msgs.length];
            Pooka.getUIFactory().showStatusMessage(getFolderID() + ":  loading " + msgs.length + " messages...");
            for (int i = 0; i < msgs.length; i++) {
              msgsToAppend[i] = new MimeMessage((MimeMessage) msgs[i]);
              String uid = getUID(msgs[i], f);
              msgsToAppend[i].addHeader(UID_HEADER, uid);
              msgsToAppend[i].setFlag(Flags.Flag.RECENT, true);
              uidsRead.add(uid);
              if (Pooka.isDebug())
                System.out.println("adding " + uid + " to read list.");
              Pooka.getUIFactory().showStatusMessage(getFolderID() + ":  loading " + i + " of " + msgs.length + " messages...");

            }
            if (Pooka.isDebug())
              System.out.println(Thread.currentThread() + ":  running appendMessages; # of added messages is " + msgsToAppend.length);

            Pooka.getUIFactory().showStatusMessage(getFolderID() + ":  appending " + msgs.length + " messages to local folder...");

            getFolder().appendMessages(msgsToAppend);

            Pooka.getUIFactory().clearStatus();

            if (! leaveMessagesOnServer()) {
              if (Pooka.isDebug())
                System.out.println("removing all messages.");

              for (int i = 0; i < msgs.length; i++) {
                msgs[i].setFlag(Flags.Flag.DELETED, true);
                if (Pooka.isDebug())
                  System.out.println("marked message " + i + " to be deleted.  isDelted = " + msgs[i].isSet(Flags.Flag.DELETED));
              }
            }
          }

          if (isDeletingOnServer()) {
            removeDeletedMessages(f);
          }

          f.close(true);
          popStore.close();
        }
        resetMessageCounts();
      } catch ( MessagingException me ) {
        try {
          if (f != null && f.isOpen())
            f.close(false);
        } catch (Exception e ) {
        }
        throw me;
      } finally {
        try {
          popStore.close();
        } catch (Exception e) {
        }

        if (connection != null) {
          if (originalStatus == NetworkConnection.DISCONNECTED && originalStatus != connection.getStatus())
            connection.disconnect();
        }
      }
    }

  }

  /**
   * This does the real work when messages are removed.
   *
   * This method should always be run on the FolderThread.
   */
  protected void runMessagesRemoved(MessageCountEvent mce) {
    if (folderTableModel != null) {
      Message[] removedMessages = mce.getMessages();
      if (Pooka.isDebug())
        System.out.println("removedMessages was of size " + removedMessages.length);
      MessageInfo mi;
      Vector removedProxies=new Vector();
      for (int i = 0; i < removedMessages.length; i++) {
        if (Pooka.isDebug())
          System.out.println("checking for existence of message.");

        // first, if we're removed from the pop3 folder on deletion,
        // we need to note that this message has been removed.

        if (isDeletingOnServer()) {
          try {
            MimeMessage mm = (MimeMessage) removedMessages[i];
            String uid = mm.getHeader(UID_HEADER, ":");
            if (uid != null)
              getChangeAdapter().setFlags(uid, new Flags(Flags.Flag.DELETED), true);
          } catch (Exception e) {
          }
        }

        mi = getMessageInfo(removedMessages[i]);
        if (mi.getMessageProxy() != null)
          mi.getMessageProxy().close();

        if (mi != null) {
          if (Pooka.isDebug())
            System.out.println("message exists--removing");
          removedProxies.add(mi.getMessageProxy());
          messageToInfoTable.remove(mi);
        }
      }
      if (getFolderDisplayUI() != null) {
        if (removedProxies.size() > 0)
          getFolderDisplayUI().removeRows(removedProxies);
        resetMessageCounts();
        fireMessageCountEvent(mce);
      } else {
        resetMessageCounts();
        fireMessageCountEvent(mce);
        if (removedProxies.size() > 0)
          getFolderTableModel().removeRows(removedProxies);
      }
    } else {
      resetMessageCounts();
      fireMessageCountEvent(mce);
    }
  }

  /**
   * <p>Overrides FolderInfo.fireMessageChangedEvent().</p>
   */
  public void fireMessageChangedEvent(MessageChangedEvent mce) {
    // if this is just from the TableInfo reloading, skip it.
    if (! (mce instanceof net.suberic.pooka.event.MessageTableInfoChangedEvent)) {
      try {
        if (!mce.getMessage().isSet(Flags.Flag.DELETED) || ! Pooka.getProperty("Pooka.autoExpunge", "true").equalsIgnoreCase("true")) {

          MessageInfo mi = getMessageInfo(mce.getMessage());
          MessageProxy mp = mi.getMessageProxy();
          if (mp != null) {
            if (mce.getMessageChangeType() == MessageChangedEvent.FLAGS_CHANGED) {
              mi.refreshFlags();
            } else if (mce.getMessageChangeType() == MessageChangedEvent.ENVELOPE_CHANGED) {
              mi.refreshHeaders();
            }
            mp.unloadTableInfo();
            mp.loadTableInfo();
          }
        }
      } catch (MessagingException me) {
        // if we catch a MessagingException, it just means
        // that the message has already been expunged.
      }
    }

    super.fireMessageChangedEvent(mce);

  }

  /**
   * This retrieves new messages from the pop folder.
   */
  public Message[] getNewMessages(Folder f) throws MessagingException {
    if (Pooka.isDebug())
      System.out.println("getting new messages.");
    Message[] newMessages = f.getMessages();

    if (newMessages.length > 0) {
      // if none of the messages have been read, then lastRead will be -1.
      int lastRead = newMessages.length - 1;
      while (lastRead >=0 && ! alreadyRead(newMessages[lastRead], f)) {
        lastRead--;
      }

      if (Pooka.isDebug())
        System.out.println("final lastRead is " + lastRead + "; for reference, newMessages.length = " + newMessages.length);
      if (newMessages.length - lastRead < 2) {
        // no new messages
        if (Pooka.isDebug())
          System.out.println("no new messages.");
        return new Message[0];
      } else {
        if (Pooka.isDebug())
          System.out.println("returning " + (newMessages.length - lastRead - 1) + " new messages.");
        Message[] returnValue = new Message[newMessages.length - lastRead - 1];
        System.arraycopy(newMessages, lastRead + 1, returnValue, 0, newMessages.length - lastRead - 1);

        return returnValue;
      }
    } else {
      if (Pooka.isDebug())
        System.out.println("no messages in folder.");
      // no messages.
      return newMessages;
    }
  }

  public String readLastUid() throws IOException {
    File uidFile = new File(mailHome + File.separator + ".pooka-lastUid");
    if (uidFile.exists()) {
      BufferedReader br = new BufferedReader(new FileReader(uidFile));
      String lastUid = br.readLine();
      if (Pooka.isDebug())
        System.out.println("lastUid is " + lastUid);

      br.close();

      return lastUid;
    }

    return null;
  }

  public void writeLastUid(String lastUid) throws IOException {
    File uidFile = new File(mailHome + File.separator + ".pooka-lastUid");
    if (uidFile.exists()) {
      uidFile.delete();
    }

    uidFile.createNewFile();

    BufferedWriter bw = new BufferedWriter(new FileWriter(uidFile));

    bw.write(lastUid);
    bw.newLine();

    bw.flush();
    bw.close();

  }

  public String getUID(Message m, Folder f) throws MessagingException {
    return ((com.sun.mail.pop3.POP3Folder)f).getUID(m);
  }

  public void removeDeletedMessages(Folder f) throws MessagingException {
    try {
      getChangeAdapter().writeChanges((com.sun.mail.pop3.POP3Folder)f);
    } catch (java.io.IOException ioe) {
      throw new MessagingException("Error", ioe);
    }
  }

  public boolean isDeletingOnServer() {
    return Pooka.getProperty(getParentStore().getStoreProperty() + ".deleteOnServerOnLocalDelete", "false").equalsIgnoreCase("true");

  }

  public boolean leaveMessagesOnServer() {
    return Pooka.getProperty(getParentStore().getStoreProperty() + ".leaveMessagesOnServer", "false").equalsIgnoreCase("true");
  }

  public ChangeCache getChangeAdapter() {
    return changeAdapter;
  }

  /**
   * <p>Checks to see whether or not we've already read this message.
   * Used when we're leaving messages on the server.
   */
  public boolean alreadyRead(Message m, Folder f) throws javax.mail.MessagingException {
    String newUid = getUID(m, f);
    if (Pooka.isDebug())
      System.out.println("checking to see if message with uid " + newUid + " is new.");

    boolean returnValue = uidsRead.contains(newUid);

    if (Pooka.isDebug())
      System.out.println(newUid + " already read = " + returnValue);

    return returnValue;

  }
}

