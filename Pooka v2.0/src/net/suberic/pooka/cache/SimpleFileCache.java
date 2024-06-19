package net.suberic.pooka.cache;
import javax.mail.internet.*;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.MessageInfo;
import net.suberic.pooka.OperationCancelledException;
import java.util.HashMap;
import java.util.Vector;
import java.io.*;
import javax.mail.*;
import javax.mail.event.*;
import javax.activation.DataHandler;

/**
 * A simple cache.
 *
 */
public class SimpleFileCache implements MessageCache {

  // FIXME:  why isn't anything synchronized?

  public static int ADDED = 10;
  public static int REMOVED = 11;

  public static String DELIMETER = "_";
  public static String CONTENT_EXT = "msg";
  public static String HEADER_EXT = "hdr";
  public static String FLAG_EXT = "flag";

  protected long uidValidity;

  // the source FolderInfo.
  private CachingFolderInfo folderInfo;

  // the directory in which the cache is stored.
  private File cacheDir;

  // the UIDValidity
  private long newUidValidity;

  // the currently cached uid's
  private Vector cachedMessages;

  // the currently cached Flags.
  private HashMap cachedFlags;

  // the currently cached Headers.
  private HashMap cachedHeaders;

  // the place where we store changes to happen later...
  private ChangeCache changes = null;

  // the last local UID used.
  long lastLocalUID = -1;

  /**
   * Creates a new SimpleFileCache for the given FolderInfo, in the
   * directory provided.
   */
  public SimpleFileCache(CachingFolderInfo folder, String directoryName) throws IOException {
    folderInfo = folder;
    cacheDir = new File(directoryName);
    if ( ! cacheDir.exists() )
      cacheDir.mkdirs();
    else if (! cacheDir.isDirectory())
      throw new IOException("not a directory.");

    changes = new ChangeCache(cacheDir);

    loadCache();
  }

  /**
   * Returns the datahandler for the given message uid.
   */
  public DataHandler getDataHandler(long uid, long newUidValidity, boolean saveToCache) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }

    DataHandler h = getHandlerFromCache(uid);
    if (h != null) {
      return h;
    } else {
      if (getFolderInfo().shouldBeConnected()) {
        MimeMessage m = getFolderInfo().getRealMessageById(uid);
        if (m != null) {
          h = m.getDataHandler();
          if (saveToCache)
            cacheMessage(m, uid, newUidValidity, MESSAGE);
          return h;
        } else
          throw new MessageRemovedException("No such message:  " + uid);
      } else {
        throw new NotCachedException("Message is not cached, and folder is not available.");
      }
    }
  }

  /**
   * Returns the datahandler for the given message uid.
   */
  public DataHandler getDataHandler(long uid, long newUidValidity) throws MessagingException {
    return getDataHandler(uid, newUidValidity, true);
  }

  /**
   * Returns a non-mutable Message representation of the given Message.
   */
  public MimeMessage getMessageRepresentation(long uid, long newUidValidity) throws MessagingException {
    return getMessageRepresentation(uid, newUidValidity, true);
  }

  /**
   * Returns a non-mutable Message representation of the given Message.
   */
  public MimeMessage getMessageRepresentation(long uid, long newUidValidity, boolean saveToCache) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }

    DataHandler h = getHandlerFromCache(uid);

    File f = new File(cacheDir, uid + DELIMETER + CONTENT_EXT);
    if (f.exists()) {
      try {
        FileInputStream fis = new FileInputStream(f);
        MimeMessage mm = new MimeMessage(net.suberic.pooka.Pooka.getDefaultSession(), fis);
        return mm;
      } catch (Exception e) {
        return null;
      }
    } else {
      if (getFolderInfo().shouldBeConnected()) {
        MimeMessage m = getFolderInfo().getRealMessageById(uid);
        if (m != null) {
          if (saveToCache)
            cacheMessage(m, uid, newUidValidity, MESSAGE);

          return m;
        } else
          throw new MessageRemovedException("No such message:  " + uid);
      } else {
        throw new NotCachedException("Message is not cached, and folder is not available.");
      }
    }
  }


  /**
   * Adds the given Flags to the message with the given uid.
   *
   * This affects both the client cache as well as the message on the
   * server, if the server is available.
   */
  public void addFlag(long uid, long newUidValidity, Flags flag) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }

    Flags f = getFlags(uid, newUidValidity);
    if (f != null) {
      f.add(flag);
    } else {
      f = flag;
    }

    if (getFolderInfo().shouldBeConnected()) {
      MimeMessage m = getFolderInfo().getRealMessageById(uid);
      if (m != null)
        m.setFlags(flag, true);

      saveFlags(uid, uidValidity, f);

    } else {
      writeToChangeLog(uid, flag, ADDED);

      saveFlags(uid, uidValidity, f);
      final long fUid = uid;
      getFolderInfo().getFolderThread().addToQueue(new net.suberic.util.thread.ActionWrapper(new javax.swing.AbstractAction() {
          public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            if (getFolderInfo() != null) {
              MessageInfo mInfo = getFolderInfo().getMessageInfoByUid(fUid);
              if (mInfo != null) {
                getFolderInfo().messageChanged(new MessageChangedEvent(SimpleFileCache.this, MessageChangedEvent.FLAGS_CHANGED, mInfo.getMessage()));
              }
            }
          }
        }, getFolderInfo().getFolderThread()), new java.awt.event.ActionEvent(SimpleFileCache.this, 1, "message-changed"));

    }

  }

  /**
   * Removes the given Flags from the message with the given uid.
   *
   * This affects both the client cache as well as the message on the
   * server, if the server is available.
   */
  public void removeFlag(long uid, long newUidValidity, Flags flag) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }
    Flags f = getFlags(uid, newUidValidity);
    if (f != null) {
      f.remove(flag);

      if (getFolderInfo().shouldBeConnected()) {
        MimeMessage m = getFolderInfo().getRealMessageById(uid);
        if (m != null)
          m.setFlags(flag, false);
        saveFlags(uid, uidValidity, f);
      } else {
        saveFlags(uid, uidValidity, f);
        writeToChangeLog(uid, flag, REMOVED);
      }

    }
  }

  /**
   * Returns the InternetHeaders object for the given uid.
   */
  public InternetHeaders getHeaders(long uid, long newUidValidity, boolean saveToCache) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }
    InternetHeaders h = getHeadersFromCache(uid);
    if (h != null) {
      return h;
    } else {
      if (getFolderInfo().shouldBeConnected()) {
        MimeMessage m = getFolderInfo().getRealMessageById(uid);
        if (m != null) {
          java.util.Enumeration headerLines = m.getAllHeaderLines();
          h = new InternetHeaders();
          while (headerLines.hasMoreElements()) {
            h.addHeaderLine((String) headerLines.nextElement());
          }
          if (saveToCache)
            cacheMessage(m, uid, newUidValidity, HEADERS);
          return h;
        } else
          throw new MessageRemovedException("No such message:  " + uid);
      } else {
        throw new NotCachedException("Message is not cached, and folder is not available.");
      }
    }
  }

  public InternetHeaders getHeaders(long uid, long uidValidity) throws MessagingException {
    return getHeaders(uid, uidValidity, true);
  }

  /**
   * Returns the Flags object for the given uid.
   */
  public Flags getFlags(long uid, long newUidValidity, boolean saveToCache) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }
    Flags f = getFlagsFromCache(uid);

    if (f != null) {
      return f;
    } else {
      if (getFolderInfo().shouldBeConnected()) {
        MimeMessage m = getFolderInfo().getRealMessageById(uid);
        if (m != null) {
          f = m.getFlags();
          if (saveToCache)
            cacheMessage(m, uid, newUidValidity, FLAGS);
          return f;
        } else
          throw new MessageRemovedException("No such message:  " + uid);
      } else {
        throw new NotCachedException("Message is not cached, and folder is not available.");
      }
    }

  }

  /**
   * Returns the Flags object for the given uid.
   */
  public Flags getFlags(long uid, long uidValidity) throws MessagingException {
    return getFlags(uid, uidValidity, true);
  }

  /**
   * Adds a message to the cache.  Note that status is only used to
   * determine whether or not the entire message is cached, or just
   * the headers and flags.
   *
   * This does not affect the server, nor does it affect message
   * count on the client.
   */
  public boolean cacheMessage(MimeMessage m, long uid, long newUidValidity, int status) throws MessagingException {
    return cacheMessage(m, uid, newUidValidity, status, true);
  }

  /**
   * Adds a message to the cache.  Note that status is only used to
   * determine whether or not the entire message is cached, or just
   * the headers and flags.
   *
   * This does not affect the server, nor does it affect message
   * count on the client.
   */
  public boolean cacheMessage(MimeMessage m, long uid, long newUidValidity, int status, boolean writeMsgFile) throws MessagingException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }

    if (m == null)
      return false;

    try {
      if (status == CONTENT || status == MESSAGE) {
        // we have to reset the seen flag if it's not set, since getting
        // the message from the server sets the flag.

        Flags flags = m.getFlags();
        boolean resetSeen = (! flags.contains(Flags.Flag.SEEN));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        m.writeTo(baos);

        if (resetSeen) {
          m.setFlag(Flags.Flag.SEEN, false);
        }

        File outFile = new File(cacheDir, uid + DELIMETER + CONTENT_EXT);
        if (outFile.exists())
          outFile.delete();

        FileOutputStream fos = new FileOutputStream(outFile);
        //m.writeTo(fos);
        fos.write(baos.toByteArray());

        fos.flush();
        fos.close();
      }

      if (status == MESSAGE || status == FLAGS || status == FLAGS_AND_HEADERS) {
        Flags flags = m.getFlags();
        saveFlags(uid, uidValidity, flags);
      }


      if (status == MESSAGE || status == HEADERS || status == FLAGS_AND_HEADERS) {

        StringWriter outString = new StringWriter();

        java.util.Enumeration headerLines = m.getAllHeaderLines();
        BufferedWriter bos = new BufferedWriter(outString);

        int foo = 0;
        while (headerLines.hasMoreElements()) {
          bos.write((String) headerLines.nextElement());
          bos.newLine();
        }

        bos.newLine();
        bos.flush();
        bos.close();

        File outFile = new File(cacheDir, uid + DELIMETER + HEADER_EXT);
        if (outFile.exists())
          outFile.delete();

        outFile.createNewFile();

        FileWriter fos = new FileWriter(outFile);
        bos = new BufferedWriter(fos);
        /*
          java.util.Enumeration enum = m.getAllHeaderLines();
          BufferedWriter bos = new BufferedWriter(fos);

          int foo = 0;
          while (enum.hasMoreElements()) {
          bos.write((String) enum.nextElement());
          bos.newLine();
          }
        */
        bos.write(outString.toString());
        bos.flush();
        bos.close();
        fos.close();
      }

      if (! cachedMessages.contains(new Long(uid))) {
        cachedMessages.add(new Long(uid));
        if (writeMsgFile)
          writeMsgFile();
      }
    } catch (IOException ioe) {
      throw new MessagingException(ioe.getMessage(), ioe);
    }

    return true;
  }

  /**
   * Removes a message from the cache only.  This has no effect on the
   * server.
   */
  public boolean invalidateCache(long uid, int status) {
    invalidateCache(new long[] { uid }, status);

    return true;
  }

  /**
   * Invalidates all of the messages in the uids array in the cache.
   */
  public boolean invalidateCache(long[] uids, int status) {
    for (int i = 0; i < uids.length; i++) {
      FilenameFilter filter = new CacheFilenameFilter(uids[i], status);
      File[] matchingFiles = cacheDir.listFiles(filter);
      for (int j = 0; j < matchingFiles.length; j++)
        matchingFiles[j].delete();

      Long l = new Long(uids[i]);
      if (status == MESSAGE || status == FLAGS_AND_HEADERS || status == FLAGS) {
        cachedFlags.remove(l);
      }
      if (status == MESSAGE || status == FLAGS_AND_HEADERS || status == HEADERS) {
        cachedHeaders.remove(l);
      }

      if (status == MESSAGE) {
        cachedMessages.remove(l);
        writeMsgFile();
      }
    }

    return true;
  }

  /**
   * Invalidates the entire cache.  Usually called when the uidValidity
   * is changed.
   */
  public void invalidateCache() {
    File[] matchingFiles = cacheDir.listFiles();
    if (matchingFiles != null)
      for (int j = 0; j < matchingFiles.length; j++) {
        if (matchingFiles[j].isFile())
          matchingFiles[j].delete();
      }

    cachedMessages = new Vector();
    cachedFlags = new HashMap();
    cachedHeaders = new HashMap();

    getChangeAdapter().invalidate();
  }


  /**
   * Adds the messages to the given folder.  Returns the uids for the
   * message.
   *
   * This method changes both the client cache as well as the server, if
   * the server is available.
   */
  public long[] appendMessages(MessageInfo[] msgs) throws MessagingException {
    if (getFolderInfo().shouldBeConnected()) {
      try {
        getFolderInfo().appendMessages(msgs);
      } catch (OperationCancelledException oce) {
        throw new MessagingException("Append cancelled.");
      }
    } else {
      LocalMimeMessage[] localMsgs = new LocalMimeMessage[msgs.length];
      for (int i = 0; i < localMsgs.length; i++) {
        Message m = msgs[i].getMessage();
        localMsgs[i] = new LocalMimeMessage((MimeMessage)m);
      }
      MessageCountEvent mce = new MessageCountEvent(getFolderInfo().getFolder(), MessageCountEvent.ADDED, false, localMsgs);
      getFolderInfo().messagesAdded(mce);
    }

    return new long[] {};
  }

  /**
   * Removes all messages marked as 'DELETED'  from the given folder.
   * Returns the uids of all the removed messages.
   *
   * Note that if any message fails to be removed, then the ones
   * that have succeeded should be returned in the long[].
   *
   * This method changes both the client cache as well as the server, if
   * the server is available.
   */
  public void expungeMessages() throws MessagingException {
    try {
      getChangeAdapter().expunge();
      Vector removedMessages = new Vector();
      for (int i = cachedMessages.size() -1; i >= 0; i--) {
        long uid = ((Long) cachedMessages.elementAt(i)).longValue();
        Flags f = getFlagsFromCache(uid);
        if (f.contains(Flags.Flag.DELETED)) {
          Message m = getFolderInfo().getMessageInfoByUid(uid).getMessage();
          ((CachingMimeMessage)m).setExpungedValue(true);
          removedMessages.add(m);
        }
      }

      if (removedMessages.size() > 0) {
        Message[] rmMsg = new Message[removedMessages.size()];
        for (int i = 0; i < removedMessages.size(); i++)
          rmMsg[i] = (Message) removedMessages.elementAt(i);

        MessageCountEvent mce = new MessageCountEvent(getFolderInfo().getFolder(), MessageCountEvent.REMOVED, true, rmMsg);
        getFolderInfo().messagesRemoved(mce);
      }
    } catch (IOException ioe) {
      throw new MessagingException(ioe.getMessage(), ioe);
    }
  }

  /**
   * This returns the uid's of the message which exist in updatedUids, but
   * not in the current list of messsages.
   */
  public long[] getAddedMessages(long[] uids, long newUidValidity) throws StaleCacheException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }
    long[] added = new long[uids.length];
    int addedCount = 0;

    for (int i = 0; i < uids.length; i++) {
      if (! cachedMessages.contains(new Long(uids[i]))) {
        added[addedCount++]=uids[i];
      }
    }

    long[] returnValue = new long[addedCount];
    if (addedCount > 0)
      System.arraycopy(added, 0, returnValue, 0, addedCount);

    return returnValue;
  }

  /**
   * This returns the uid's of the message which exist in the current
   * list of messages, but no longer exist in the updatedUids.
   */
  public long[] getRemovedMessages(long[] uids, long newUidValidity) throws StaleCacheException {
    if (newUidValidity != uidValidity) {
      throw new StaleCacheException(uidValidity, newUidValidity);
    }
    Vector remainders = new Vector(cachedMessages);

    for (int i = 0; i < uids.length; i++) {
      remainders.remove(new Long(uids[i]));
    }

    long[] returnValue = new long[remainders.size()];
    for (int i = 0; i < remainders.size(); i++)
      returnValue[i] = ((Long) remainders.elementAt(i)).longValue();

    return returnValue;
  }

  /**
   * This returns the message id's of all the currently cached messages.
   * Note that only the headers and flags of the message need to be
   * cached for a message to be considered in the cache.
   */
  public long[] getMessageUids() {
    long[] returnValue = new long[cachedMessages.size()];
    for (int i = 0; i < cachedMessages.size(); i++)
      returnValue[i] = ((Long) cachedMessages.elementAt(i)).longValue();

    return returnValue;
  }

  /**
   * Gets a DataHandler from the cache.  Returns null if no handler is
   * available in the cache.
   */
  protected DataHandler getHandlerFromCache(long uid) {
    File f = new File(cacheDir, uid + DELIMETER + CONTENT_EXT);
    if (f.exists()) {
      try {
        FileInputStream fis = new FileInputStream(f);
        MimeMessage mm = new MimeMessage(net.suberic.pooka.Pooka.getDefaultSession(), fis);
        javax.activation.DataSource source = new WrappedMimePartDataSource (mm, this, uid);
        DataHandler dh = new DataHandler(source);
        return dh;
      } catch (Exception e) {
        return null;
      }
      //return new DataHandler(new FileDataSource(f));
    } else
      return null;
  }

  /**
   * Gets the InternetHeaders from the cache.  Returns null if no headers are
   * available in the cache.
   */
  InternetHeaders getHeadersFromCache(long uid) throws MessagingException {
    InternetHeaders returnValue = (InternetHeaders) cachedHeaders.get(new Long(uid));
    if (returnValue != null) {
      return returnValue;
    } else {

      File f = new File(cacheDir, uid +DELIMETER + HEADER_EXT);
      if (f.exists())
        try {
          FileInputStream fis = new FileInputStream(f);
          returnValue = new InternetHeaders(fis);
          cachedHeaders.put(new Long(uid), returnValue);
          try {
            fis.close();
          } catch (java.io.IOException ioe) {
          }
          return returnValue;
        } catch (FileNotFoundException fnfe) {
          throw new MessagingException(fnfe.getMessage(), fnfe);
        }
      else
        return null;
    }
  }

  /**
   * Gets the Flags from the cache.  Returns null if no flagss are
   * available in the cache.
   */
  Flags getFlagsFromCache(long uid) {
    Flags returnValue = (Flags) cachedFlags.get(new Long(uid));
    if (returnValue != null) {
      return new Flags(returnValue);
    } else {
      File f = new File(cacheDir, uid + DELIMETER + FLAG_EXT);
      if (f.exists()) {
        try {
          Flags newFlags = new Flags();
          BufferedReader in = new BufferedReader(new FileReader(f));
          for (String currentLine = in.readLine(); currentLine != null; currentLine = in.readLine()) {

            if (currentLine.equalsIgnoreCase("Deleted"))
              newFlags.add(Flags.Flag.DELETED);
            else if (currentLine.equalsIgnoreCase("Answered"))
              newFlags.add(Flags.Flag.ANSWERED);
            else if (currentLine.equalsIgnoreCase("Draft"))
              newFlags.add(Flags.Flag.DRAFT);
            else if (currentLine.equalsIgnoreCase("Flagged"))
              newFlags.add(Flags.Flag.FLAGGED);
            else if (currentLine.equalsIgnoreCase("Recent"))
              newFlags.add(Flags.Flag.RECENT);
            else if (currentLine.equalsIgnoreCase("SEEN"))
              newFlags.add(Flags.Flag.SEEN);
            else
              newFlags.add(new Flags(currentLine));
          }

          cachedFlags.put(new Long(uid), newFlags);
          return newFlags;
        } catch (FileNotFoundException fnfe) {
          System.out.println("caught filenotfoundexception.");
          return null;
        } catch (IOException ioe) {
          System.out.println("caught ioexception.");
          return null;
        }
      }

      return null;
    }
  }

  /**
   * Saves the given flags to the cache.
   */
  protected void saveFlags(long uid, long newUidValidity, Flags f) throws MessagingException {
    Flags oldFlags = getFlagsFromCache(uid);
    if (oldFlags == null || ! oldFlags.equals(f)) {
      cachedFlags.put(new Long(uid), f);
      try {
        File outFile = new File(cacheDir, uid + DELIMETER + FLAG_EXT);
        if (outFile.exists())
          outFile.delete();

        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);

        Flags.Flag[] systemFlags = f.getSystemFlags();
        for (int i = 0; i < systemFlags.length; i++) {
          if (systemFlags[i] == Flags.Flag.ANSWERED) {
            bw.write("Answered");
            bw.newLine();
          } else if (systemFlags[i] == Flags.Flag.DELETED) {
            bw.write("Deleted");
            bw.newLine();
          } else if (systemFlags[i] == Flags.Flag.DRAFT) {
            bw.write("Draft");
            bw.newLine();
          } else if (systemFlags[i] == Flags.Flag.FLAGGED) {
            bw.write("Flagged");
            bw.newLine();
          } else if (systemFlags[i] == Flags.Flag.RECENT) {
            // let's not cache the recent flag, eh?
          } else if (systemFlags[i] == Flags.Flag.SEEN) {
            bw.write("Seen");
            bw.newLine();
          }
        }

        String[] userFlags = f.getUserFlags();
        for (int i = 0; i < userFlags.length; i++) {
          bw.write(userFlags[i]);
          bw.newLine();
        }

        bw.flush();
        bw.close();
      } catch (IOException ioe) {
        throw new MessagingException (ioe.getMessage(), ioe);
      }
    }
  }

  protected void writeToChangeLog(long uid, Flags flags, int status) throws MessagingException {
    try {
      if (status == REMOVED)
        getChangeAdapter().setFlags(uid, flags, false);
      else
        getChangeAdapter().setFlags(uid, flags, true);
    } catch (IOException ioe) {
      throw new MessagingException (ioe.getMessage(), ioe);
    }
  }

  /**
   * Initializes the cache from the file system.
   */
  public void loadCache() {
    cachedMessages = new Vector();
    cachedFlags = new HashMap();
    cachedHeaders = new HashMap();

    File msgListFile = new File(cacheDir, "messageList");
    if (msgListFile.exists()) {
      try {
        BufferedReader in = new BufferedReader(new FileReader(msgListFile));
        for (String nextLine = in.readLine(); nextLine != null; nextLine = in.readLine()) {
          Long l = new Long(nextLine);
          cachedMessages.add(l);
          // this has the side effect of loading the cached flags
          // to the cachedFlags HashMap. -- i think we do that now when
          // we fetch?
          //getFlagsFromCache(l.longValue());
          //getHeadersFromCache(l.longValue());
        }
      } catch (Exception e) { }
    }

    File validityFile = new File(cacheDir, "validity");
    if (validityFile.exists()) {
      try {
        BufferedReader in = new BufferedReader(new FileReader(validityFile));
        uidValidity = Long.parseLong(in.readLine());
      } catch (Exception e) {
      }
    }

    File localMsgFile = new File(cacheDir, "lastLocal");
    if (localMsgFile.exists()) {
      try {
        BufferedReader in = new BufferedReader(new FileReader(localMsgFile));
        lastLocalUID = Long.parseLong(in.readLine());
      } catch (Exception e) {
      }
    }

  }

  public void writeMsgFile() {
    try {
      File msgListFile = new File(cacheDir, "messageList");
      if (! msgListFile.exists()) {
        msgListFile.createNewFile();
      }
      BufferedWriter out = new BufferedWriter(new FileWriter(msgListFile));
      for (int i = 0; i < cachedMessages.size(); i++) {
        out.write(((Long) cachedMessages.elementAt(i)).toString());
        out.newLine();
      }
      out.flush();
      out.close();
    } catch (Exception e) {
    }
  }

  /**
   * Writes any offline changes made back to the server.
   */
  public void writeChangesToServer(Folder f) throws MessagingException {
    try {
      getChangeAdapter().writeChanges((UIDFolder) f, getFolderInfo());
    } catch (IOException ioe) {
      throw new MessagingException(net.suberic.pooka.Pooka.getProperty("error.couldNotGetChanges", "Error:  could not get cached changes."), ioe);
    }
  }

  public CachingFolderInfo getFolderInfo() {
    return folderInfo;
  }

  /**
   * Returns the size of the given message, or -1 if the message is
   * not cached.
   */
  public int getSize(long uid) {
    File f = new File(cacheDir, uid + DELIMETER + CONTENT_EXT);
    if (! f.exists()) {
      return (int)f.length();
    } else
      return -1;

  }

  private class CacheID {
    long id;
    long lastAccessed;
    long size;

    CacheID(long newId, long newLastAccessed, long newSize) {
      id = newId;
      lastAccessed = newLastAccessed;
      size = newSize;
    }
  }

  private class CacheFilenameFilter implements FilenameFilter {
    long uid;
    int status;

    public CacheFilenameFilter(long newUid, int newStatus) {
      uid = newUid;
      status = newStatus;
    }

    public boolean accept(File dir, String name) {
      if (status == MESSAGE || status == CONTENT) {
        if (name.startsWith(uid + DELIMETER + CONTENT_EXT))
          return true;
      }

      if (status == FLAGS || status == FLAGS_AND_HEADERS || status == MESSAGE) {
        if (name.startsWith(uid + DELIMETER + FLAG_EXT))
          return true;
      }

      if (status == HEADERS || status == FLAGS_AND_HEADERS || status == MESSAGE) {
        if (name.startsWith(uid + DELIMETER + HEADER_EXT))
          return true;
      }

      return false;
    }
  }

  /**
   * This returns the number of messages in the cache.
   */
  public int getMessageCount() {
    return cachedMessages.size();
  }

  /**
   * This returns the number of unread messages in the cache.
   */
  public int getUnreadMessageCount() throws MessagingException {
    // sigh.
    int unreadCount = 0;
    for (int i = 0; i < cachedMessages.size(); i++) {
      Flags f = getFlags(((Long) cachedMessages.elementAt(i)).longValue(), uidValidity, false);
      if (! f.contains(Flags.Flag.SEEN)) {
        unreadCount++;
      }
    }

    return unreadCount;
  }

  /**
   * Returns whether a given uid exists fully in the cache or not.
   */
  public boolean isFullyCached(long uid) {
    DataHandler dh = getHandlerFromCache(uid);
    return (dh != null);
  }

  /**
   * Returns the status of the given uid.
   */
  public int getCacheStatus(long uid) throws MessagingException {
    if (isFullyCached(uid))
      return CONTENT;
    else {
      InternetHeaders ih = getHeadersFromCache(uid);
      Flags f = getFlagsFromCache(uid);
      if (ih != null && f != null)
        return FLAGS_AND_HEADERS;
      else if (ih != null)
        return HEADERS;
      else if (f != null)
        return FLAGS;
      else
        return NOT_CACHED;
    }

  }

  public long getUIDValidity() {
    return uidValidity;
  }

  public void setUIDValidity(long newValidity) {
    try {
      File f = new File(cacheDir, "validity");
      if (f.exists())
        f.delete();

      f.createNewFile();

      BufferedWriter out = new BufferedWriter(new FileWriter(f));
      out.write(Long.toString(newValidity));
      out.flush();
      out.close();
    } catch (Exception e) {
    }

    uidValidity = newValidity;
  }

  public ChangeCache getChangeAdapter() {
    return changes;
  }

  /**
   * Searches all of the cached messages and returns those which match
   * the given SearchTerm.
   */
  public MessageInfo[] search(javax.mail.search.SearchTerm term) throws
    javax.mail.MessagingException {
    Vector matches = new Vector();

    for (int i = 0; i < cachedMessages.size(); i++) {
      MessageInfo info = getFolderInfo().getMessageInfoByUid(((Long)cachedMessages.elementAt(i)).longValue());
      Message m = info.getMessage();
      if (term.match(m))
        matches.add(info);
    }

    MessageInfo[] returnValue = new MessageInfo[matches.size()];
    for (int i = 0; i < matches.size(); i++) {
      returnValue[i] = (MessageInfo) matches.elementAt(i);
    }

    return returnValue;
  }

  /**
   * A class representing a local, cache-only message.
   */
  public class LocalMimeMessage extends javax.mail.internet.MimeMessage {

    long uid;

    public LocalMimeMessage(MimeMessage m) throws MessagingException {
      super(m);
      uid = generateLocalUID();
    }

    public long getUID() {
      return uid;
    }
  }

  /**
   * Generates a local UID.
   */
  public synchronized long generateLocalUID() {
    lastLocalUID--;
    try {
      File f = new File(cacheDir, "lastLocal");
      if (f.exists())
        f.delete();

      f.createNewFile();

      BufferedWriter out = new BufferedWriter(new FileWriter(f));
      out.write(Long.toString(lastLocalUID));
      out.flush();
      out.close();
    } catch (Exception e) {
    }
    return lastLocalUID;
  }
}


