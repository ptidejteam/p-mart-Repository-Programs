package net.suberic.pooka.gui.filechooser;
import javax.swing.*;
import java.io.*;
import javax.mail.*;
import net.suberic.pooka.Pooka;

// TODO make StoreFileWrapper for selecting from available stores
// --jphekman


/**
 * This wraps a Folder or Store in a File object.
 */
public class FolderFileWrapper extends File {
  private Folder folder;
  private FolderFileWrapper parent;
  private FolderFileWrapper[] children = null;
  private String path;
  private Object mRunLock = null;

  /**
   * Creates a new FolderFileWrapper from a Folder.  This should only
   * be used for direct children of the Folder.
   */
  public FolderFileWrapper(Folder f, FolderFileWrapper p) {
    super(f.getName());
    folder = f;
    parent = p;
    path = f.getName();
    mRunLock = p.getRunLock();
    if (p != null)
      getLogger().fine("creating FolderFileWrapper from parent '" + p.getAbsolutePath() + "' from folder '" + f.getName() + "'");
    else
      getLogger().fine("creating FolderFileWrapper from parent 'null' from folder '" + f.getName() + "'");
  }

  /**
   * Creates a new FolderFileWrapper from a Folder with the given path
   * and parent.  This is used for making relative paths to files, i.e.
   * a child of '/foo' called 'bar/baz'.
   */
  public FolderFileWrapper(Folder f, FolderFileWrapper p, String filePath) {
    super(f.getName());
    folder = f;
    parent = p;
    mRunLock = p.getRunLock();
    path = filePath;
    if (p != null)
      getLogger().fine("creating FolderFileWrapper from parent '" + p.getAbsolutePath() + "' called '" + filePath + "'");
    else
      getLogger().fine("creating FolderFileWrapper from parent 'null' called '" + filePath + "'");
  }

  /**
   * Creates a new FolderFileWrapper from a Folder with the given path
   * and parent.  This is used for making relative paths to files, i.e.
   * a child of '/foo' called 'bar/baz'.
   */
  public FolderFileWrapper(Folder f, String filePath, Object pRunLock) {
    super(f.getName());
    folder = f;
    parent = null;
    mRunLock = pRunLock;
    path = filePath;

    getLogger().fine("creating FolderFileWrapper from parent 'null' called '" + filePath + "'");
  }

  /**
   * returns true.
   */
  public boolean canRead() {
    return true;
  }

  /**
   * returns true.
   */
  public boolean canWrite() {
    return true;
  }

  /**
   * If the wrapped Folder does not exist, creates the new Folder
   * and returns true.  If it does exist, returns false.  If a
   * MessagingException is thrown, wraps it with an IOException.
   */
  public boolean createNewFile() {
    try {
      if (folder.exists())
        return false;
      else {
        folder.create(Folder.HOLDS_MESSAGES);
        return true;
      }
    } catch (MessagingException me) {
      getLogger().fine("caught exception: " + me.getMessage());
      me.printStackTrace();
      return false;
    }
  }


  /**
   * Attempts to delete the Folder.
   */
  public boolean delete() {
    try {
      return folder.delete(true);
    } catch (MessagingException me) {
      getLogger().fine("caughed exception: " + me.getMessage());
      me.printStackTrace();
      return false;
    }
  }

  /**
   * A no-op; we're not deleting any Mail folders on exit.
   */
  public void deleteOnExit() {
  }

  /**
   * Equals if the underlying Folder objects are equal.
   */
  public boolean equals(Object obj) {
    if (obj instanceof FolderFileWrapper)
      return ( folder == ((FolderFileWrapper)obj).folder );
    else
      return false;
  }

  /**
   * Returns folder.exists().
   */
  public boolean exists() {
    try {
      return folder.exists();
    } catch (MessagingException me) {
      return false;
    }
  }

  /**
   * Returns this object.
   */
  public File getAbsoluteFile() {

    getLogger().fine("calling getAbsoluteFile() on " + getAbsolutePath());
    if (this.isAbsolute())
      return this;
    else
      return new FolderFileWrapper(getFolder(), getRoot(), getAbsolutePath());
  }

  /**
   * returns the root of this tree.
   */
  private FolderFileWrapper getRoot() {
    FolderFileWrapper parent = this;
    while (parent.getParent() != null) {
      parent = (FolderFileWrapper)parent.getParentFile();
    }
    return parent;
  }

  /**
   * Returns the Folder's full name.  It does this recursively, by calling
   * the this on the parent and then appending this name.
   */
  public String getAbsolutePath() {

    getLogger().fine("calling getAbsolutePath() on " + path);

    if (isAbsolute()) {
      getLogger().fine("returning " + getPath());
      return getPath();
    } else {
      if (parent != null) {
        String parentPath = parent.getAbsolutePath();
        if (parentPath != "/") {
          getLogger().fine("returning parentPath + slash + getPath() (" + parentPath + "/" + getPath() + ")");

          return parentPath + "/" + getPath();
        } else {
          getLogger().fine("returning just slash + getPath() (/" + getPath() + ")");
          return "/" + getPath();
        }
      } else {
        getLogger().fine("returning just /.");
        return "/";
      }
    }
  }

  /**
   * returns this.
   */
  public File getCanonicalFile() {
    return this;
  }

  /**
   * returns getAbsolutePath();
   */
  public String getCanonicalPath() {
    return getAbsolutePath();
  }

  /**
   * Returns the Folder's name.
   */
  public String getName() {
    String name = folder.getName();
    if (name == null || name.length() < 1) {
      // this is probably a store.
      return path;
    }
    return name;
  }

  /**
   * Returns the parent's name.
   */
  public String getParent() {
    if (parent != null)
      return parent.getAbsolutePath();
    else
      return null;
  }

  /**
   * This returns the parent Folder as a FolderFileWrapper.
   */
  public File getParentFile() {
    return parent;
  }

  /**
   * Returns the filePath variable.
   */
  public String getPath() {
    return path;
  }

  /**
   * Returns true if this is an absolute reference, false otherwise.
   */
  public boolean isAbsolute() {
    return (parent == null);
  }

  /**
   * Tests to see if this can act as a directory.
   */
  public boolean isDirectory() {
    try {
      return ((folder.getType() & Folder.HOLDS_FOLDERS) != 0);
    } catch (MessagingException me) {
      return false;
    }
  }

  /**
   * Tests to see if we should call this a File.
   */
  public boolean isFile() {
    try {
      return ((folder.getType() & Folder.HOLDS_MESSAGES) != 0);
    } catch (MessagingException me) {
      return false;
    }
  }

  /**
   * Returns false.
   */
  public boolean isHidden() {
    return false;
  }

  /**
   * Returns 0.
   */
  public long lastModified() {
    return 0;
  }

  /**
   * returns the children of the File.
   */
  public String[] list() {
    if (isDirectory()) {
      if (children == null)
        loadChildren();
      if (children != null) {
        String[] returnValue = new String[children.length];
        for (int i = 0; i < children.length; i++) {
          returnValue[i] = children[i].getName();
        }
        return returnValue;
      }
    }

    return null;

  }

  /**
   * Returns the children of the File, filterd by the FilenameFilter.
   */
  public String[] list(FilenameFilter filter) {
    String[] children = list();
    String[] matching = new String[children.length];
    int retValueCounter = 0;
    for (int i = 0; i < children.length; i++) {
      if (filter.accept(this, children[i])) {
        matching[retValueCounter++] = children[i];
      }
    }

    String[] returnValue = new String[retValueCounter];

    for (int i = 0; i < retValueCounter; i++)
      returnValue[i] = matching[i];

    return returnValue;
  }

  /**
   * This returns the children of the File as Files.
   */
  public File[] listFiles() {
    getLogger().fine("Running listFiles() on '" + getAbsolutePath() + "'");

    if (isDirectory()) {
      if (children == null) {
        getLogger().fine("about to load children.");
        loadChildren();
      }

      if (children != null)
        return children;
    }

    return new FolderFileWrapper[0];
  }

  public File[] listFiles(FileFilter filter) {

    getLogger().fine("Running listFiles(FileFilter) on '" + getAbsolutePath() + "'");

    File[] children = listFiles();
    File[] matching = new File[children.length];
    int retValueCounter = 0;
    for (int i = 0; i < children.length; i++) {
      if (filter.accept(children[i])) {
        matching[retValueCounter++] = children[i];
      }
    }

    File[] returnValue = new File[retValueCounter];

    for (int i = 0; i < retValueCounter; i++)
      returnValue[i] = matching[i];

    return returnValue;
  }

  public File[] listFiles(FilenameFilter filter) {

    getLogger().fine("Running listFiles(FilenameFilter) on '" + getAbsolutePath() + "'");

    File[] children = listFiles();
    File[] matching = new File[children.length];
    int retValueCounter = 0;
    for (int i = 0; i < children.length; i++) {
      if (filter.accept(this, children[i].getName())) {
        matching[retValueCounter++] = children[i];
      }
    }

    File[] returnValue = new File[retValueCounter];

    for (int i = 0; i < retValueCounter; i++)
      returnValue[i] = matching[i];

    return returnValue;
  }

  /**
   * This creates a new directory.
   */
  public boolean mkdir() {
    try {
      if (folder.exists())
        return false;
      else {
        folder.create(Folder.HOLDS_FOLDERS);
        return true;
      }
    } catch (MessagingException me) {
      return false;
    }
  }

  /**
   * This creates a new directory, also creating any higher-level
   * directories if needed.
   */
  public boolean mkdirs() {
    try {
      if (folder.exists())
        return false;

      boolean create = true;
      if (!parent.exists())
        create = parent.mkdirs();

      if (create) {
        folder.create(Folder.HOLDS_FOLDERS);
        return true;
      } else
        return false;
    } catch (MessagingException me) {
      return false;
    }
  }


  /**
   * This renames the underlying Folder.
   */
  public boolean renameTo(File dest) {
    try {
      if (dest instanceof FolderFileWrapper) {
        boolean returnValue = folder.renameTo(((FolderFileWrapper)dest).getFolder());
        if (parent != null)
          parent.refreshChildren();
        return returnValue;
      } else
        return false;
    } catch (MessagingException me) {
      Pooka.getUIFactory().showError(Pooka.getProperty("error.renamingFolder", "Error renaming folder ") + getName(), me);
      return false;
    }
  }

  /**
   * This returns the wrapped Folder.
   */
  public Folder getFolder() {
    return folder;
  }

  /**
   * This loads the children of the Folder.
   */
  private synchronized void loadChildren() {
    synchronized(getRunLock()) {
      {
        getLogger().fine(Thread.currentThread().getName() + ":  calling loadChildren() on " + getAbsolutePath());
      }

      if (children == null) {
        getLogger().fine(Thread.currentThread().getName() + ":  children is null on " + getAbsolutePath() + ".  loading children.");
        if (isDirectory() ||  ! exists()) {
          try {

            getLogger().fine(Thread.currentThread().getName() + ":  checking for connection.");

            if (!folder.getStore().isConnected()) {
              getLogger().fine(Thread.currentThread().getName() + ":  parent store of " + getAbsolutePath() + " is not connected.  reconnecting.");
              folder.getStore().connect();
            } else {

              getLogger().fine(Thread.currentThread().getName() + ":  connection is ok.");
            }



            getLogger().fine(Thread.currentThread().getName() + ":  calling folder.list()");
            Folder[] childList = folder.list();

            if (parent == null) {
              // add for namespaces.
              try {
                Folder[] namespaces = folder.getStore().getSharedNamespaces();
                if (namespaces != null && namespaces.length > 0) {
                  Folder[] newChildList = new Folder[childList.length + namespaces.length];
                  System.arraycopy(namespaces, 0, newChildList, 0, namespaces.length);
                  System.arraycopy(childList, 0, newChildList, namespaces.length, childList.length);
                  childList = newChildList;
                }
              } catch (Exception e) {
                // FIXME do nothing for now.
              }
            }

            getLogger().fine(Thread.currentThread().getName() + ":  folder.list() returned " + childList + "; creating new folderFileWrapper.");

            FolderFileWrapper[] tmpChildren = new FolderFileWrapper[childList.length];
            for (int i = 0; i < childList.length; i++) {

              getLogger().fine(Thread.currentThread().getName() + ":  calling new FolderFileWrapper for " + childList[i].getName() + " (entry # " + i);

              tmpChildren[i] = new FolderFileWrapper(childList[i], this);
            }

            children = tmpChildren;
          } catch (MessagingException me) {
            getLogger().fine("caught exception during FolderFileWrapper.loadChildren() on " + getAbsolutePath() + ".");
            me.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * This refreshes the children of the Folder.
   */
  public synchronized void refreshChildren() {
    synchronized(getRunLock()) {
      getLogger().fine(Thread.currentThread().getName() + ":  calling refreshChildren() on " + getAbsolutePath());
      //Thread.dumpStack();

      if (children == null) {
        getLogger().fine(Thread.currentThread().getName() + ":  children is null on " + getAbsolutePath() + ".  calling loadChildren().");
        loadChildren();
      } else {
        if (isDirectory() ||  ! exists()) {
          try {

            getLogger().fine(Thread.currentThread().getName() + ":  checking for connection.");

            if (!folder.getStore().isConnected()) {
              getLogger().fine(Thread.currentThread().getName() + ":  parent store of " + getAbsolutePath() + " is not connected.  reconnecting.");
              folder.getStore().connect();
            } else {

              getLogger().fine(Thread.currentThread().getName() + ":  connection is ok.");
            }



            getLogger().fine(Thread.currentThread().getName() + ":  calling folder.list()");
            Folder[] childList = folder.list();

            getLogger().fine(Thread.currentThread().getName() + ":  folder.list() returned " + childList + "; creating new folderFileWrapper.");

            FolderFileWrapper[] tmpChildren = new FolderFileWrapper[childList.length];
            for (int i = 0; i < childList.length; i++) {

              getLogger().fine(Thread.currentThread().getName() + ":  calling new FolderFileWrapper for " + childList[i].getName() + " (entry # " + i);

              // yeah, this is n! or something like that. shouldn't matter--
              // if we have somebody running with that many folders, or with
              // that slow of a machine, we're in trouble anyway.

              //try to get a match.
              boolean found = false;
              for (int j = 0; ! found && j < children.length; j++) {
                if (children[j].getName().equals(childList[i].getName())) {
                  tmpChildren[i] = children[j];
                  found = true;
                }
              }

              if (! found) {
                tmpChildren[i] = new FolderFileWrapper(childList[i], this);
              }
            }

            children = tmpChildren;
          } catch (MessagingException me) {
            Pooka.getUIFactory().showError(Pooka.getProperty("error.refreshingFolder", "error refreshing folder's children: "), me);
          }
        }
      }
    }

  }

  /* Only accepts relative filenames. */
  public FolderFileWrapper getFileByName(String filename) {


    getLogger().fine("calling getFileByName(" + filename + ") on folder " + getName() + " (" + getPath() + ") (abs " + getAbsolutePath() + ")");

    String origFilename = new String(filename);
    if (filename == null || filename.length() < 1 || (filename.equals("/") && getParentFile() == null)) {

      getLogger().fine("returning this for getFileByName()");
      return this;
    }

    if (this.isAbsolute(filename))
      {
        return null; // FIXME error
      }

    // strip out the /'s

    String subdirFile = null;

    int dirMarker = filename.indexOf('/');
    while (dirMarker == 0) {
      filename = filename.substring(1);
      dirMarker = filename.indexOf('/');
    }

    // divide into first component and rest of components
    if (dirMarker > 0) {
      subdirFile = filename.substring(dirMarker + 1);
      filename=filename.substring(0, dirMarker);
    }

    FolderFileWrapper currentFile = getChildFile(filename);
    if (currentFile != null && subdirFile != null) {
      // recurse with rest of components
      FolderFileWrapper tmp = currentFile.getFileByName(subdirFile);
      //    tmp.path = origFilename;


      getLogger().fine("created file " + tmp.getName() + " (" + tmp.getPath() + ") (abs " + tmp.getAbsolutePath() + ") from string " + origFilename + " on folder " + getName() + " (" + getPath() + ") (abs " + getAbsolutePath() + ")");

      return tmp;

    } else {
      return currentFile;
    }

  }

  FolderFileWrapper getChildFile(String filename) {

    getLogger().fine("calling getChildFile on " + getName() + " with filename " + filename);

    if (children == null)
      loadChildren();

    if (children != null) {
      for (int i = 0; i < children.length; i++) {
        if (children[i].getName().equals(filename))
          return children[i];
      }

      FolderFileWrapper[] newChildren = new FolderFileWrapper[children.length +1];
      for (int i = 0; i < children.length; i++)
        newChildren[i] = children[i];

      try {
        newChildren[children.length] = new FolderFileWrapper(folder.getFolder(filename), this);
      } catch (MessagingException me) {
      }

      children = newChildren;
      return children[children.length -1];
    }

    return this;

  }

  private boolean isAbsolute(String filename) {
    return filename.startsWith("/");
  }

  public String filenameAsRelativeToRoot(String filename) {
    String relative = filename;
    while (relative.length() > 0 & isAbsolute (relative)) {
      relative = relative.substring(1);
    }

    return relative;
  }

  /**
   * Returns the object that we use for a run lock.
   */
  public Object getRunLock() {
    return mRunLock;
  }

  /**
   * Returns the Logger object for this class.
   */
  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getLogger("Pooka.debug.gui.filechooser");
  }


}
