package net.suberic.pooka.cache;

import javax.mail.*;

import net.suberic.pooka.Pooka;

public class FolderProxy extends javax.mail.Folder {
  String folderName;

  public FolderProxy(String name) {
    super(null);
    folderName = name;
  }
  public String getName() {
    return folderName;
  }
  public String getFullName() {
    return folderName;
  }
  
  public  Folder getParent() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

     public  boolean exists() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

   public  Folder[] list(String pattern) throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

   public  int getType() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); } 

    public  char getSeparator() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }
    public  boolean create(int type) throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

   public  boolean hasNewMessages() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

     public  Folder getFolder(String name)
				throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }
   public  boolean delete(boolean recurse) 
				throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

 public  boolean renameTo(Folder f) throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

public  void open(int mode) throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }
  public  void close(boolean expunge) throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

  public  boolean isOpen() {
    return false;
  }
  public  Flags getPermanentFlags() {
    return null;
  }
  public  int getMessageCount() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }
  public  Message getMessage(int msgnum)
				throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

  public  void appendMessages(Message[] msgs)
    throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

  public  Message[] expunge() throws MessagingException { throw new MessagingException(Pooka.getProperty("error.folderNotAvailable", "Folder not loaded.")); }

}
