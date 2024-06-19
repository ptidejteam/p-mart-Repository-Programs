package net.suberic.pooka;
import javax.mail.*;
import javax.mail.event.MessageCountEvent;
import javax.mail.internet.MimeMessage;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.ConnectionEvent;
import net.suberic.pooka.*;
import java.util.Vector;
import java.util.List;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import net.suberic.pooka.gui.MessageProxy;
import net.suberic.pooka.gui.FolderTableModel;

/**
 * A FolderInfo which keeps track of its messages' UIDs.  This allows
 * it to recover if the connection to the server is lost.
 * @author Allen Petersen
 * @version $Revision$
 */

public class UIDFolderInfo extends FolderInfo {
  protected HashMap uidToInfoTable = new HashMap();
  protected long uidValidity = -1000;

  // the resource for the folder disconnected message
  protected static String disconnectedMessage = "error.UIDFolder.disconnected";

  public UIDFolderInfo(StoreInfo parent, String fname) {
    super(parent, fname);
  }

  public UIDFolderInfo(FolderInfo parent, String fname) {
    super(parent, fname);
  }

  /**
   * Loads the MessageInfos and MesageProxies.  Returns a List of
   * newly created MessageProxies.
   */
  protected List createInfosAndProxies() throws MessagingException {
    int fetchBatchSize = 50;
    try {
      fetchBatchSize = Integer.parseInt(Pooka.getProperty("Pooka.fetchBatchSize", "50"));
    } catch (NumberFormatException nfe) {
    }

    Vector messageProxies = new Vector();

    Folder f = getFolder();
    if (f == null)
      throw new MessagingException("Folder does not exist or is unavailable.");

    Message[] msgs = getFolder().getMessages();

    // get the UIDs first.
    FetchProfile uidProfile = new FetchProfile();
    uidProfile.add(UIDFolder.FetchProfileItem.UID);
    // adding FLAGS to make getFirstUnreadMessage() more efficient
    uidProfile.add(FetchProfile.Item.FLAGS);

    getFolder().fetch(msgs, uidProfile);

    Message[] toFetch = msgs;

    // go ahead and fetch the first set of messages; the rest will be
    // taken care of by the loaderThread.
    if (msgs.length > fetchBatchSize) {
      toFetch = new Message[fetchBatchSize];
      System.arraycopy(msgs, msgs.length - fetchBatchSize, toFetch, 0, fetchBatchSize);
    }

    getFolder().fetch(toFetch, fetchProfile);

    int firstFetched = Math.max(msgs.length - fetchBatchSize, 0);

    MessageInfo mi;

    for (int i = 0; i < msgs.length; i++) {
      long uid = getUID(msgs[i]);
      UIDMimeMessage newMessage = new UIDMimeMessage(this, uid);
      mi = new MessageInfo(newMessage, this);

      if ( i >= firstFetched)
        mi.setFetched(true);

      messageProxies.add(new MessageProxy(getColumnValues() , mi));
      messageToInfoTable.put(newMessage, mi);
      uidToInfoTable.put(new Long(uid), mi);
    }

    return messageProxies;
  }

  /**
   * This just checks to see if we can get a NewMessageCount from the
   * folder.  As a brute force method, it also accesses the folder
   * at every check.  It's nasty, but it _should_ keep the Folder open..
   */
  public void checkFolder() throws javax.mail.MessagingException, OperationCancelledException {
    getLogger().log(Level.FINE, "checking folder " + getFolderName());

    // i'm taking this almost directly from ICEMail; i don't know how
    // to keep the stores/folders open, either.  :)

    StoreInfo s = null;
    if (isConnected()) {
      Folder current = getFolder();
      if (current != null && current.isOpen()) {
        current.getNewMessageCount();
        current.getUnreadMessageCount();
        resetMessageCounts();
      }
    } else if (isAvailable() && (status == PASSIVE || status == LOST_CONNECTION)) {
      s = getParentStore();
      if (! s.isConnected())
        s.connectStore();

      openFolder(Folder.READ_WRITE);

      resetMessageCounts();

      if (isAvailable() && preferredStatus == PASSIVE)
        closeFolder(false);
    }
  }

  protected void updateFolderOpenStatus(boolean isNowOpen) {
    if (isNowOpen) {
      setStatus(CONNECTED);
      try {
        if (uidValidity == -1000) {
          uidValidity = ((UIDFolder) getFolder()).getUIDValidity();
        }
        if (getFolderTableModel() != null)
          synchronizeCache();
      } catch (Exception e) { }

    } else
      setStatus(CLOSED);
  }

  /**
   * This synchronizes the cache with the new information from the
   * Folder.
   */
  public void synchronizeCache() throws MessagingException, OperationCancelledException {
    getLogger().log(Level.FINE, "synchronizing cache.");

    if (getFolderDisplayUI() != null)
      getFolderDisplayUI().showStatusMessage(Pooka.getProperty("message.UIDFolder.synchronizing", "Re-synchronizing with folder..."));

    long newValidity = ((UIDFolder)getFolder()).getUIDValidity();
    if (uidValidity != newValidity) {
      if (getFolderDisplayUI() != null)
        getFolderDisplayUI().showStatusMessage(Pooka.getProperty("error.UIDFolder.validityMismatch", "Error:  validity not correct.  reloading..."));

      folderTableModel = null;
      loadAllMessages();
      if (getFolderDisplayUI() != null)
        getFolderDisplayUI().resetFolderTableModel(folderTableModel);

      if (getFolderDisplayUI() != null)
        getFolderDisplayUI().clearStatusMessage();

    } else {
      if (getFolderDisplayUI() != null)
        getFolderDisplayUI().showStatusMessage(Pooka.getProperty("message.UIDFolder.synchronizing.loading", "Loading messages from folder..."));
      FetchProfile fp = new FetchProfile();
      //fp.add(FetchProfile.Item.ENVELOPE);
      //fp.add(FetchProfile.Item.FLAGS);
      fp.add(UIDFolder.FetchProfileItem.UID);
      Message[] messages = getFolder().getMessages();
      getFolder().fetch(messages, fp);

      if (getFolderDisplayUI() != null)
        getFolderDisplayUI().showStatusMessage(Pooka.getProperty("message.UIDFolder.synchronizing", "Comparing new messages to current list..."));

      long[] uids = new long[messages.length];

      for (int i = 0; i < messages.length; i++) {
        uids[i] = getUID(messages[i]);
      }

      getLogger().log(Level.FINE, "synchronizing--uids.length = " + uids.length);

      long[] addedUids = getAddedMessages(uids, uidValidity);
      getLogger().log(Level.FINE, "synchronizing--addedUids.length = " + addedUids.length);

      if (addedUids.length > 0) {
        Message[] addedMsgs = ((UIDFolder)getFolder()).getMessagesByUID(addedUids);
        MessageCountEvent mce = new MessageCountEvent(getFolder(), MessageCountEvent.ADDED, false, addedMsgs);
        messagesAdded(mce);
      }

      long[] removedUids = getRemovedMessages(uids, uidValidity);
      getLogger().log(Level.FINE, "synchronizing--removedUids.length = " + removedUids.length);

      if (removedUids.length > 0) {
        Message[] removedMsgs = new Message[removedUids.length];
        for (int i = 0 ; i < removedUids.length; i++) {
          // messagesRemoved() will handle moving between UIDMimeMessages
          // and real messages.
          removedMsgs[i] = getMessageInfoByUid(removedUids[i]).getMessage();
        }
        MessageCountEvent mce = new MessageCountEvent(getFolder(), MessageCountEvent.REMOVED, false, removedMsgs);
        messagesRemoved(mce);

      }

      updateFlags(uids, messages, uidValidity);

      if (getFolderDisplayUI() != null)
        getFolderDisplayUI().clearStatusMessage();
    }
  }

  /**
   * Gets the added UIDs.
   */
  protected long[] getAddedMessages(long[] newUids, long uidValidity) {
    long[] added = new long[newUids.length];
    int addedCount = 0;
    Set currentUids = uidToInfoTable.keySet();

    for (int i = 0; i < newUids.length; i++) {
      if (! currentUids.contains(new Long(newUids[i]))) {
        added[addedCount++]=newUids[i];
      }
    }

    long[] returnValue = new long[addedCount];
    if (addedCount > 0)
      System.arraycopy(added, 0, returnValue, 0, addedCount);

    return returnValue;

  }

  /**
   * Gets the removed UIDs.
   */
  protected long[] getRemovedMessages(long[] newUids, long uidValidity) {
    Vector remainders = new Vector(uidToInfoTable.keySet());

    for (int i = 0; i < newUids.length; i++) {
      remainders.remove(new Long(newUids[i]));
    }

    long[] returnValue = new long[remainders.size()];
    for (int i = 0; i < remainders.size(); i++)
      returnValue[i] = ((Long) remainders.elementAt(i)).longValue();

    return returnValue;
  }

  protected void updateFlags(long[] uids, Message[] messages, long uidValidity) throws MessagingException {
    // sigh

    Vector proxies = new Vector();
    for (int i = 0; i < messages.length; i++) {
      // FIXME
      MessageInfo mi =  getMessageInfo(messages[i]);
      MessageProxy mp = mi.getMessageProxy();
      mi.setFetched(false);
      mp.setRefresh(true);
      proxies.add(mp);
    }

    getLogger().log(Level.FINE, "updating flags for " + proxies.size() + " messages.");

    //loaderThread.loadMessages(proxies);
    mMessageLoader.loadMessages(proxies);

  }

  protected void runMessagesAdded(MessageCountEvent mce)  {
    if (folderTableModel != null) {
      try {
        Message[] addedMessages = mce.getMessages();
        /*
          FetchProfile fp = new FetchProfile();
          fp.add(FetchProfile.Item.ENVELOPE);
          fp.add(FetchProfile.Item.FLAGS);
          fp.add(UIDFolder.FetchProfileItem.UID);
        */

        showStatusMessage(getFolderDisplayUI(), Pooka.getProperty("message.UIDFolder.synchronizing.fetchingMessages", "Fetching") + " " + addedMessages.length + " " + Pooka.getProperty("message.UIDFolder.synchronizing.messages", "messages."));
        getLogger().log(Level.FINE, "UIDFolderInfo:  runMessagesAdded().  getting " + addedMessages.length + " messages.");

        if (fetchProfile != null) {
          FetchProfile fp = null;
          if (! fetchProfile.contains(UIDFolder.FetchProfileItem.UID)) {
            // clone it.  we could cache this, but i doubt it's a problem.
            fp = new FetchProfile();
            FetchProfile.Item[] items = fetchProfile.getItems();
            String[] headers = fetchProfile.getHeaderNames();
            if (items != null) {
              for (int i = 0; i < items.length; i++) {
                fp.add(items[i]);
              }
            }

            if (headers != null) {
              for (int i = 0; i < headers.length; i++) {
                fp.add(headers[i]);
              }
            }

            fp.add(UIDFolder.FetchProfileItem.UID);

          } else {
            fp = fetchProfile;
          }
          getFolder().fetch(addedMessages, fp);
        } else {
          FetchProfile fp = new FetchProfile();
          fp.add(FetchProfile.Item.ENVELOPE);
          fp.add(FetchProfile.Item.FLAGS);
          fp.add(UIDFolder.FetchProfileItem.UID);

          getFolder().fetch(addedMessages, fp);
        }

        MessageInfo mi;
        Vector addedProxies = new Vector();
        for (int i = 0; i < addedMessages.length; i++) {
          UIDMimeMessage newMsg = getUIDMimeMessage(addedMessages[i]);
          long uid = newMsg.getUID();
          if (getMessageInfoByUid(uid) != null) {
            getLogger().log(Level.FINE, getFolderID() + ":  this is a duplicate.  not making a new messageinfo for it.");
          } else {
            mi = new MessageInfo(newMsg, this);
            // this has already been fetched; no need to do so again.
            mi.setFetched(true);

            addedProxies.add(new MessageProxy(getColumnValues(), mi));
            messageToInfoTable.put(newMsg, mi);
            uidToInfoTable.put(new Long(uid), mi);
          }
        }

        getLogger().log(Level.FINE, "filtering proxies.");
        addedProxies.removeAll(applyFilters(addedProxies));

        getLogger().log(Level.FINE, "filters run; adding " + addedProxies.size() + " messages.");
        if (addedProxies.size() > 0) {
          getFolderTableModel().addRows(addedProxies);
          setNewMessages(true);
          resetMessageCounts();

          // notify the message loaded thread.
          MessageProxy[] addedArray = (MessageProxy[]) addedProxies.toArray(new MessageProxy[0]);
          //loaderThread.loadMessages(addedArray, net.suberic.pooka.thread.LoadMessageThread.HIGH);
          mMessageLoader.loadMessages(addedArray, net.suberic.pooka.thread.MessageLoader.HIGH);

          // change the Message objects in the MessageCountEvent to
          // our UIDMimeMessages.
          Message[] newMsgs = new Message[addedProxies.size()];
          for (int i = 0; i < addedProxies.size(); i++) {
            newMsgs[i] = ((MessageProxy)addedProxies.elementAt(i)).getMessageInfo().getMessage();
          }
          MessageCountEvent newMce = new MessageCountEvent(getFolder(), mce.getType(), mce.isRemoved(), newMsgs);
          fireMessageCountEvent(newMce);
        }
      } catch (MessagingException me) {
        if (getFolderDisplayUI() != null)
          getFolderDisplayUI().showError(Pooka.getProperty("error.handlingMessages", "Error handling messages."), Pooka.getProperty("error.handlingMessages.title", "Error handling messages."), me);
      } finally {
        clearStatusMessage(getFolderDisplayUI());
      }
    }

  }

  /**
   * This does the real work when messages are removed.
   */
  protected void runMessagesRemoved(MessageCountEvent mce) {
    getLogger().log(Level.FINE, "running MessagesRemoved on " + getFolderID());

    MessageCountEvent newMce = null;
    if (folderTableModel != null) {
      Message[] removedMessages = mce.getMessages();
      Message[] uidRemovedMessages = new Message[removedMessages.length];

      getLogger().log(Level.FINE, "removedMessages was of size " + removedMessages.length);

      MessageInfo mi;
      Vector removedProxies=new Vector();
      for (int i = 0; i < removedMessages.length; i++) {
        getLogger().log(Level.FINE, "checking for existence of message " + removedMessages[i]);

        try {
          UIDMimeMessage removedMsg = getUIDMimeMessage(removedMessages[i]);

          if (removedMsg != null)
            uidRemovedMessages[i] = removedMsg;
          else
            uidRemovedMessages[i] = removedMessages[i];

          mi = getMessageInfo(removedMsg);
          if (mi != null) {
            if (mi.getMessageProxy() != null)
              mi.getMessageProxy().close();

            getLogger().log(Level.FINE, "message exists--removing");
            removedProxies.add(mi.getMessageProxy());
            messageToInfoTable.remove(mi);
            uidToInfoTable.remove(new Long(removedMsg.getUID()));
          } else {
            getLogger().log(Level.FINE, "message with uid " + removedMessages[i] + " not found; not removing.");
          }
        } catch (MessagingException me) {
          getLogger().log(Level.FINE, "caught exception running messagesRemoved on " + removedMessages[i] + ":  " + me.getMessage());
        }
      }
      newMce = new MessageCountEvent(getFolder(), mce.getType(), mce.isRemoved(), uidRemovedMessages);
      if (getFolderDisplayUI() != null) {
        if (removedProxies.size() > 0)
          getFolderDisplayUI().removeRows(removedProxies);
        resetMessageCounts();
        fireMessageCountEvent(newMce);
      } else {
        resetMessageCounts();
        fireMessageCountEvent(newMce);
        if (removedProxies.size() > 0)
          getFolderTableModel().removeRows(removedProxies);
      }
    } else {
      resetMessageCounts();
      fireMessageCountEvent(mce);
    }
  }

  protected void runMessageChanged(MessageChangedEvent mce) {
    // if the message is getting deleted, then we don't
    // really need to update the table info.  for that
    // matter, it's likely that we'll get MessagingExceptions
    // if we do, anyway.
    boolean updateInfo = false;
    try {
      updateInfo = (!mce.getMessage().isSet(Flags.Flag.DELETED) || ! Pooka.getProperty("Pooka.autoExpunge", "true").equalsIgnoreCase("true"));
    } catch (MessagingException me) {
      // if we catch a MessagingException, it just means
      // that the message has already been expunged.  in
      // that case, assume it's ok if we don't update; it'll
      // happen in the messagesRemoved().
    }

    if (updateInfo) {
      try {
        Message msg = mce.getMessage();
        UIDMimeMessage changedMsg = getUIDMimeMessage(msg);
        long uid = changedMsg.getUID();

        MessageInfo mi = getMessageInfoByUid(uid);
        if (mi != null) {
          MessageProxy mp = mi.getMessageProxy();
          if (mp != null) {
            mp.unloadTableInfo();
            mp.loadTableInfo();
          }
        }
      } catch (MessagingException me) {
        // if we catch a MessagingException, it just means
        // that the message has already been expunged.
      }

      // if we're not just a tableinfochanged event, do a resetmessagecouts.
      // don't do this if we're just a delete.
      if (! (mce instanceof net.suberic.pooka.event.MessageTableInfoChangedEvent)) {
        resetMessageCounts();
      }
    }

    // now let's go ahead and get the UIDMimeMessage for the event so
    // that we can fire that instead.

    try {
      Message msg = mce.getMessage();
      UIDMimeMessage changedMsg = getUIDMimeMessage(msg);
      if (changedMsg != null) {
        MessageChangedEvent newMce = new MessageChangedEvent(mce.getSource(), mce.getMessageChangeType(), changedMsg);
        fireMessageChangedEvent(newMce);
      } else
        fireMessageChangedEvent(mce);
    } catch (MessagingException me) {
      // if we catch a MessagingException, then we can just fire the
      // original mce.
      fireMessageChangedEvent(mce);

    }
  }

  /**
   * Creates a child folder.
   */
  protected FolderInfo createChildFolder(String newFolderName) {
    return new UIDFolderInfo(this, newFolderName);
  }

  /**
   * Fetches the information for the given messages using the given
   * FetchProfile.
   */
  public void fetch(MessageInfo[] messages, FetchProfile profile) throws MessagingException  {
    if (messages == null)
      getLogger().log(Level.FINE, "UIDFolderInfo:  fetching with null messages.");
    else
      getLogger().log(Level.FINE, "UIDFolderInfo:  fetching " + messages.length + " messages.");

    // check the messages first; make sure we're just fetching 'real'
    // messages.
    java.util.ArrayList realMsgList = new java.util.ArrayList();
    for (int i = 0; i < messages.length; i++) {
      Message currentMsg = messages[i].getRealMessage();
      if (currentMsg != null && currentMsg instanceof UIDMimeMessage) {
        currentMsg = ((UIDMimeMessage)currentMsg).getMessage();
      }
      if (currentMsg != null)
        realMsgList.add(currentMsg);
    }

    Message[] realMsgs = (Message[]) realMsgList.toArray(new Message[0]);

    if (realMsgs == null)
      getLogger().log(Level.FINE, "UIDFolderInfo:  running fetch with null real messages.");
    else
      getLogger().log(Level.FINE, "UIDFolderInfo:  fetching " + realMsgs.length + " messages.");

    getFolder().fetch(realMsgs, profile);

    for (int i = 0 ; i < messages.length; i++) {
      messages[i].setFetched(true);
    }
  }

  /**
   * Unloads all messages.  This should be run if ever the current message
   * information becomes out of date, as can happen when the connection
   * to the folder goes down.
   *
   * Note that for this implementation, we just keep everything; we only
   * need to worry when we do the cache synchronization.
   */
  public void unloadAllMessages() {
    //folderTableModel = null;
  }

  /**
   * This method closes the Folder.  If you open the Folder using
   * openFolder (which you should), then you should use this method
   * instead of calling getFolder.close().  If you don't, then the
   * FolderInfo will try to reopen the folder.
   */
  public void closeFolder(boolean expunge, boolean closeDisplay) throws MessagingException {

    if (closeDisplay && getFolderDisplayUI() != null)
      getFolderDisplayUI().closeFolderDisplay();

    /*
    // should this be here?  should we remove closed folders from
    // the FolderTracker?
    if (getFolderTracker() != null) {
    getFolderTracker().removeFolder(this);
    setFolderTracker(null);
    }
    */

    if (isLoaded() && isAvailable()) {
      if (isConnected()) {
        try {
          getFolder().close(expunge);
        } catch (java.lang.IllegalStateException ise) {
          throw new MessagingException(ise.getMessage(), ise);
        }
      }
      setStatus(CLOSED);
    }


  }

  // UID / UIDMimeMessage / etc. methods.

  /**
   * Returns the UIDMimeMessage for the given Message.
   */
  public UIDMimeMessage getUIDMimeMessage(Message m) throws MessagingException {
    if (m instanceof UIDMimeMessage)
      return (UIDMimeMessage) m;

    // it's not a UIDMimeMessage, so it must be a 'real' message.
    long uid = getUID(m);
    MessageInfo mi = getMessageInfoByUid(uid);
    if (mi != null)
      return (UIDMimeMessage) mi.getMessage();

    // doesn't already exist.  just create a new one.
    return new UIDMimeMessage(this, uid);
  }

  /**
   * gets the 'real' message for the given MessageInfo.
   */
  public Message getRealMessage(MessageInfo mi) throws MessagingException {
    Message wrappingMessage = mi.getMessage();
    if (wrappingMessage instanceof UIDMimeMessage)
      return ((UIDMimeMessage)wrappingMessage).getMessage();
    else
      return wrappingMessage;
  }

  /**
   * Returns the "real" message from the underlying folder that matches up
   * to the given UID.  If no such message exists, returns null.
   */
  public javax.mail.internet.MimeMessage getRealMessageById(long uid) throws MessagingException {
    Folder f = getFolder();
    if (f != null && f instanceof UIDFolder) {
      javax.mail.internet.MimeMessage m = null;
      try {
        m = (javax.mail.internet.MimeMessage) ((UIDFolder) f).getMessageByUID(uid);
        return m;
      } catch (IllegalStateException ise) {
        throw new MessagingException(ise.getMessage());
      }
    } else {
      throw new MessagingException("Error:  Folder unavailable or is not a UIDFolder");
    }
  }

  /**
   * gets the MessageInfo for the given Message.
   */
  public MessageInfo getMessageInfo(Message m) {
    if (m instanceof UIDMimeMessage)
      return (MessageInfo) messageToInfoTable.get(m);
    else {
      try {
        long uid = getUID(m);
        return getMessageInfoByUid(uid);
      } catch (MessagingException me) {
        return null;
      }
    }
  }

  /**
   * Returns the MessageInfo associated with the given uid.
   */
  public MessageInfo getMessageInfoByUid(long uid) {
    return (MessageInfo) uidToInfoTable.get(new Long(uid));
  }


  /**
   * Gets the UID for the given Message.
   */
  public long getUID(Message m) throws MessagingException {
    if (m instanceof UIDMimeMessage)
      return ((UIDMimeMessage)m).getUID();
    else {
      return ((UIDFolder)getFolder()).getUID(m);
    }
  }

  public long getUIDValidity() {
    return uidValidity;
  }

}

