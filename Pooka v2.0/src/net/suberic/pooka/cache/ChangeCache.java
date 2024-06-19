package net.suberic.pooka.cache;
import javax.mail.*;
import java.io.*;
import java.util.ArrayList;

/**
 * A utility class that saves the changes made to messages into a file.
 * Then when we reconnect to the server, we can make all of the changes
 * in batch.
 */
public class ChangeCache {

  File cacheFile = null;
  File cacheDir = null;
  File tmpFile = null;
  
  public static String EXPUNGE_MSG = "FOLDER EXPUNGE";
  public static String DONE_MSG = "INPUT DONE";
  public static String APPEND_MSG = "APPEND";

  boolean lock = false;
  
  /**
   * Creates a new ChangeCache object based out of the given directory.
   */
  public ChangeCache(File dir) {
    cacheDir = dir;
    cacheFile = new File(dir, "changeLog");
  }
  
  /**
   * Opens the cache file.  Actually creates a temporary file, copies the
   * contents of the original cache file into there, and returns a writer
   * to that file.
   */
  private BufferedWriter openCacheFile() throws IOException {
    boolean hasLock = false;
    while (! hasLock) {
      synchronized(this) {
	if (! lock ) {
	  lock = true;
	  hasLock = true;
	}
      }
      
      if (! hasLock )
	try {
	  Thread.sleep(1000);
	} catch (Exception e) { }
    }
    tmpFile = new File(cacheDir, "changeLog.tmp");
    
    // if there's a temp file, assume it's an old one left
    // over from before.
    if (tmpFile.exists())
      tmpFile.delete();
    tmpFile.createNewFile();
    
    BufferedWriter out = new BufferedWriter(new FileWriter(tmpFile));
    if (cacheFile.exists()) {
      BufferedReader in = new BufferedReader(new FileReader(cacheFile));
      String nextLine = in.readLine();
      while (nextLine != null) {
	out.write(nextLine);
	out.newLine();
	nextLine = in.readLine();
      }
      
      in.close();
    }
    return out;
  }
  
  private void closeCacheFile(BufferedWriter out) throws IOException {
    try {
      out.flush();
      out.close();
      
      if (cacheFile.exists())
	cacheFile.delete();
      
      tmpFile.renameTo(cacheFile);
      tmpFile = null;
    } finally {
      lock = false;
    }
  }
  
  /**
   * Writes out the given flags to be set to value value on the message with
   * the given uid.
   */
  public void setFlags(long uid, Flags f, boolean value) throws IOException {
    BufferedWriter out = null;
    try {
      out = openCacheFile();
      
      out.write(Long.toString(uid));
      out.newLine();
      
      Flags.Flag[] systemFlags = f.getSystemFlags();
      for (int i = 0; i < systemFlags.length; i++) {
	if (systemFlags[i] == Flags.Flag.ANSWERED) {
	  out.write("Answered");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.DELETED) {
	  out.write("Deleted");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.DRAFT) {
	  out.write("Draft");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.FLAGGED) {
	  out.write("Flagged");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.RECENT) {
	  out.write("Recent");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.SEEN) {
	  out.write("Seen");
	  out.newLine();
	}
	
      }
      
      String[] userFlags = f.getUserFlags();
      for (int i = 0; i < userFlags.length; i++) {
	out.write(userFlags[i]);
	out.newLine();
      }
      
      out.write(DONE_MSG);
      out.newLine();
      
      if (value) 
	out.write("true");
      else
	out.write("false");
      
      out.newLine();
      
      out.write(DONE_MSG);
      out.newLine();
      
    } finally {
      if (out != null)
	closeCacheFile(out);
      else
	lock = false;
    }
    
  }
  
  
  /**
   * Notes the given uid's as needing to be appended to the server.
   */
  public void appendMessages(long[] uids) throws IOException {
    BufferedWriter out = null;
    try {
      out = openCacheFile();
      if (uids.length > 0) {
	out.write(APPEND_MSG);
	out.newLine();
	for (int i = 0; i < uids.length; i++) {
	  out.write(Long.toString(uids[i]));
	  out.newLine();
	}
	out.write(DONE_MSG);
	out.newLine();
      }
    } finally {
      if (out != null)
	closeCacheFile(out);
      else
	lock = false;
    }
    
  }
	
  public void expunge() throws IOException {
    BufferedWriter out = null;
    try {
      out = openCacheFile();
      
      out.write(EXPUNGE_MSG);
      out.newLine();
	    
    } finally {
      if (out != null) 
	closeCacheFile(out);
      else
	lock = false;
    }
  }
  
  /**
   * Invalidates the changes to the server's cache.  Basically removes
   * the change file.
   */
  public void invalidate() {
    boolean hasLock = false;
    while (! hasLock) {
      synchronized(this) {
	if (! lock ) {
	  lock = true;
	  hasLock = true;
	}
      }
      
      if (! hasLock )
	try {
	  Thread.sleep(1000);
	} catch (Exception e ) { }
    }
    
    try {
      if (cacheFile.exists())
	cacheFile.delete();
    } finally {
      lock = false;
    }
  }

  /**
   * Writes the changes in the file back to the server.
   */
  public void writeChanges(UIDFolder f, CachingFolderInfo cfi) throws IOException, MessagingException {
    boolean hasLock = false;
    while (! hasLock) {
      synchronized(this) {
	if (! lock ) {
	  lock = true;
	  hasLock = true;
	}
      }
      
      if (! hasLock )
	try {
	  Thread.sleep(1000);
	} catch (Exception e ) { }
    }
    
    try {
      if (cacheFile.exists()) {
	BufferedReader in = new BufferedReader(new FileReader(cacheFile));
	
	String nextLine = in.readLine();
	while (nextLine != null) {
	  if (nextLine.equalsIgnoreCase(APPEND_MSG)) {
	    // get the UID's we want to append.
	    nextLine = in.readLine();
	    ArrayList messageList = new ArrayList();
	    ArrayList uidList = new ArrayList();
	    while (! nextLine.equalsIgnoreCase("DONE_MSG")) {
	      long uid = Long.parseLong(nextLine);
	      uidList.add(new Long(uid));
	      net.suberic.pooka.MessageInfo m = cfi.getMessageInfoByUid(uid);
	      if (m != null)
		messageList.add(m.getMessage());
	      nextLine = in.readLine();
	    }
	    Message[] messages = new Message[messageList.size()];
	    for (int i = 0; i < messageList.size(); i++) {
	      messages[i] = (Message)messageList.get(i);
	    }
	    
	    ((javax.mail.Folder)f).appendMessages(messages);
	    
	    long[] invalidatedUids = new long[uidList.size()];;
	    for (int i = 0; i < uidList.size(); i++) {
	      invalidatedUids[i] = ((Long) uidList.get(i)).longValue();
	    }
	    cfi.getCache().invalidateCache(invalidatedUids, MessageCache.MESSAGE);
	    
	    nextLine = in.readLine();
	  } else if (nextLine.equalsIgnoreCase(EXPUNGE_MSG))
	    try {
	      
	      ((Folder) f).expunge();
	    } catch (MessagingException me) { }
	  else if (nextLine.length() > 0) {
	    // adding flags
	    
	    boolean value;
	    Flags newFlags = new Flags();
	    
	    long uid = Long.parseLong(nextLine);
	    nextLine = in.readLine();
	    while (nextLine != null && ! nextLine.equals(DONE_MSG)) {
	      
	      if (nextLine.equalsIgnoreCase("Deleted")) {
		newFlags.add(Flags.Flag.DELETED);
	      } else if (nextLine.equalsIgnoreCase("Answered"))
		newFlags.add(Flags.Flag.ANSWERED);
	      else if (nextLine.equalsIgnoreCase("Draft"))
		newFlags.add(Flags.Flag.DRAFT);
	      else if (nextLine.equalsIgnoreCase("Flagged"))
		newFlags.add(Flags.Flag.FLAGGED);
	      else if (nextLine.equalsIgnoreCase("Recent"))
		newFlags.add(Flags.Flag.RECENT);
	      else if (nextLine.equalsIgnoreCase("SEEN"))
		newFlags.add(Flags.Flag.SEEN);
	      else 
		newFlags.add(new Flags(nextLine));
	      
	      nextLine = in.readLine();
	    }
	    
	    nextLine = in.readLine();
	    if (nextLine.equalsIgnoreCase("true")) {
	      value = true;
	    } else
	      value = false;
	    
	    try {
	      Message m = f.getMessageByUID(uid);
	      if (m != null) {
		m.setFlags(newFlags, value);
	      }
	      // should be a done
	      nextLine = in.readLine(); 
	    } catch (MessagingException me) { }
	  }
	  
	  nextLine = in.readLine();
	}
	
	in.close();
	cacheFile.delete();
      }
    } finally {
      lock = false;
    }
  }
  
  
  /**
   * Writes out the given flags to be set to value value on the message with
   * the given uid.
   */
  public void setFlags(String uid, Flags f, boolean value) throws IOException {
    BufferedWriter out = null;
    try {
      out = openCacheFile();
      
      out.write(uid);
      out.newLine();
      
      Flags.Flag[] systemFlags = f.getSystemFlags();
      for (int i = 0; i < systemFlags.length; i++) {
	if (systemFlags[i] == Flags.Flag.ANSWERED) {
	  out.write("Answered");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.DELETED) {
	  out.write("Deleted");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.DRAFT) {
	  out.write("Draft");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.FLAGGED) {
	  out.write("Flagged");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.RECENT) {
	  out.write("Recent");
	  out.newLine();
	} else if (systemFlags[i] == Flags.Flag.SEEN) {
	  out.write("Seen");
	  out.newLine();
	}
	
      }
      
      String[] userFlags = f.getUserFlags();
      for (int i = 0; i < userFlags.length; i++) {
	out.write(userFlags[i]);
	out.newLine();
      }
      
      out.write(DONE_MSG);
      out.newLine();
      
      if (value) 
	out.write("true");
      else
	out.write("false");
      
      out.newLine();
      
      out.write(DONE_MSG);
      out.newLine();
      
    } finally {
      if (out != null)
	closeCacheFile(out);
      else
	lock = false;
    }
    
  }
  
  /**
   * Writes the changes in the file back to the server.
   */
  public void writeChanges(com.sun.mail.pop3.POP3Folder f) throws IOException, MessagingException {
    boolean hasLock = false;
    while (! hasLock) {
      synchronized(this) {
	if (! lock ) {
	  lock = true;
	  hasLock = true;
	}
      }
      
      if (! hasLock )
	try {
	  Thread.sleep(1000);
	} catch (Exception e ) { }
    }
    
    try {
      if (cacheFile.exists()) {
	
	Message[] msgs = f.getMessages();
	
	BufferedReader in = new BufferedReader(new FileReader(cacheFile));
	String nextLine = in.readLine();
	while (nextLine != null) {
	  if (nextLine.equalsIgnoreCase(EXPUNGE_MSG))
	    try {
	      
	      ((Folder) f).expunge();
	    } catch (MessagingException me) { }
	  else if (nextLine.length() > 0) {
	    // adding flags
	    
	    boolean value;
	    Flags newFlags = new Flags();
	    
	    String uid = nextLine;
	    nextLine = in.readLine();
	    while (nextLine != null && ! nextLine.equals(DONE_MSG)) {
	      
	      if (nextLine.equalsIgnoreCase("Deleted")) {
		newFlags.add(Flags.Flag.DELETED);
	      } else if (nextLine.equalsIgnoreCase("Answered"))
		newFlags.add(Flags.Flag.ANSWERED);
	      else if (nextLine.equalsIgnoreCase("Draft"))
		newFlags.add(Flags.Flag.DRAFT);
	      else if (nextLine.equalsIgnoreCase("Flagged"))
		newFlags.add(Flags.Flag.FLAGGED);
	      else if (nextLine.equalsIgnoreCase("Recent"))
		newFlags.add(Flags.Flag.RECENT);
	      else if (nextLine.equalsIgnoreCase("SEEN"))
		newFlags.add(Flags.Flag.SEEN);
	      else 
		newFlags.add(new Flags(nextLine));
	      
	      nextLine = in.readLine();
	    }
	    
	    nextLine = in.readLine();
	    if (nextLine.equalsIgnoreCase("true")) {
	      value = true;
	    } else
	      value = false;
	    
	    try {
	      Message m = getMessageByPopUID(uid, f, msgs);
	      if (m != null) {
		m.setFlags(newFlags, value);
	      }
	      // should be a done
	      nextLine = in.readLine(); 
	    } catch (MessagingException me) { }
	  }
	  
	  nextLine = in.readLine();
	}
	
	in.close();
	cacheFile.delete();
      }
    } finally {
      lock = false;
    }
  }
  
  /**
   * Gets the message from the pop folder indicated by the given uid.
   */
  public Message getMessageByPopUID(String uid, com.sun.mail.pop3.POP3Folder f, Message[] msgs) throws MessagingException {
    // this is a really dumb algorithm.  we can do much better, especially
    // if you consider that pop uids are sequential.  well, except for
    // the fact that they're not.  :)
    for (int i = msgs.length - 1; i >=0; i--) {
      String currentUid = f.getUID(msgs[i]);
      if (f.getUID(msgs[i]).equals(uid))
	return msgs[i];
    }
    
    return null;
  }
  
}

