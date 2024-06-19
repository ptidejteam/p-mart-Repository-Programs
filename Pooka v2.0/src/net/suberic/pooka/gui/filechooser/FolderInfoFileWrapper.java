package net.suberic.pooka.gui.filechooser;
import javax.swing.*;
import java.io.*;
import java.util.Vector;
import net.suberic.pooka.*;

/**
 * This wraps a Folder or Store in a File object.
 */
public class FolderInfoFileWrapper extends File {
  private FolderInfo folder;
  private StoreInfo store;
  private FolderInfoFileWrapper parent;
  private FolderInfoFileWrapper[] children = null;
  private String path;

  /**
   * Creates a new FolderInfoFileWrapper from a FolderInfo.  This should only
   * be used for direct children of this FolderInfo.
   */
  public FolderInfoFileWrapper(FolderInfo f, FolderInfoFileWrapper p) {
    super(f.getFolderName());
    folder = f;
    parent = p;
    if (parent == null)
      parent = this;

    path = f.getFolderName();

    if (p != null)
      getLogger().info("creating FolderInfoFileWrapper from parent '" + p.getAbsolutePath() + "' from folder '" + f.getFolderName() + "'");
    else
      getLogger().info("creating FolderInfoFileWrapper from parent 'null' from folder '" + f.getFolderName() + "'");
  }

  /**
   * Creates a new FolderInfoFileWrapper from a StoreInfo.  This probably
   * really shouldn't be used.
   */
  public FolderInfoFileWrapper(StoreInfo s, FolderInfoFileWrapper p) {
    super(s.getStoreID());
    store = s;
    parent = p;
    path = s.getStoreID();

    if (p != null)
      getLogger().info("creating FolderInfoFileWrapper from parent '" + p.getAbsolutePath() + "' from store '" + s.getStoreID() + "'");
    else
      getLogger().info("creating FolderInfoFileWrapper from parent 'null' from store '" + s.getStoreID() + "'");
  }

  /**
   * Creates a new FolderInfoFileWrapper from a FolderInfo with the given path
   * and parent.  This is used for making relative paths to files, i.e.
   * a child of '/foo' called 'bar/baz'.
   */
  public FolderInfoFileWrapper(FolderInfo f, FolderInfoFileWrapper p, String filePath) {
    super(f.getFolderName());
    folder = f;
    parent = p;
    path = filePath;

    if (p != null)
      getLogger().info("creating FolderInfoFileWrapper from parent '" + p.getAbsolutePath() + "' called '" + filePath + "'");
    else
      getLogger().info("creating FolderInfoFileWrapper from parent 'null' called '" + filePath + "'");
  }

  /**
   * Creates a new FolderInfoFileWrapper from a StoreInfo with the given path
   * and parent.  This is used for making relative paths to files, i.e.
   * a child of '/foo' called 'bar/baz'.  For StoreInfos, the parent should
   * really always be null.
   */
  public FolderInfoFileWrapper(StoreInfo s, FolderInfoFileWrapper p, String filePath) {
    super(s.getStoreID());
    store = s;
    parent = p;
    path = filePath;

    if (p != null)
      getLogger().info("creating FolderInfoFileWrapper from parent '" + p.getAbsolutePath() + "' called '" + filePath + "'");
    else
      getLogger().info("creating FolderInfoFileWrapper from parent 'null' called '" + filePath + "'");
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
   * Throws an IOException; use the folder subscription mechanism to
   * subscribe to new folders.
   */
  public boolean createNewFile() throws IOException {
    throw new IOException (Pooka.getProperty("error.folderinfofilewrapper.cantcreate", "Cannot create new Folders here.  Use Subscribe instead."));
  }


  /**
   * Attempts to delete the Folder.  And fails--returns false always.
   */
  public boolean delete() {
    return false;
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
    if (obj instanceof FolderInfoFileWrapper) {
      FolderInfoFileWrapper wrapper = ((FolderInfoFileWrapper) obj);
      if (folder != null && wrapper.folder != null)
        return (folder == wrapper.folder);
      else if (store != null && wrapper.store != null)
        return (store == wrapper.store);
    }

    return false;
  }

  /**
   * Always true for our purposes.
   */
  public boolean exists() {
    return true;
  }

  /**
   * Returns this object.
   */
  public File getAbsoluteFile() {

    getLogger().info("calling getAbsoluteFile() on " + getAbsolutePath());
    if (this.isAbsolute())
      return this;
    else {
      if (store != null)
        return new FolderInfoFileWrapper(store, getRoot(), getAbsolutePath());
      else
        return new FolderInfoFileWrapper(folder, getRoot(), getAbsolutePath());
    }
  }

  /**
   * returns the root of this tree.
   */
  private FolderInfoFileWrapper getRoot() {
    FolderInfoFileWrapper tmpParent = this;
    while (tmpParent != null && tmpParent.getParent() != null && tmpParent.getParentFile() != tmpParent) {
      tmpParent = (FolderInfoFileWrapper)tmpParent.getParentFile();
    }
    return tmpParent;
  }

  /**
   * Returns the Folder's full name.  It does this recursively, by calling
   * the this on the parent and then appending this name.
   */
  public String getAbsolutePath() {

    getLogger().info("calling getAbsolutePath() on " + path);

    if (isAbsolute()) {

      getLogger().info("returning " + getPath());
      return getPath();
    }
    else {
      if (parent != null) {
        String parentPath = parent.getAbsolutePath();
        if (parentPath != "/") {

          getLogger().info("returning parentPath + slash + getPath() (" + parentPath + "/" + getPath() + ")");

          return parentPath + "/" + getPath();
        } else {

          getLogger().info("returning just slash + getPath() (/" + getPath() + ")");
          return "/" + getPath();
        }
      } else {

        getLogger().info("returning just /.");
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
    if (folder != null)
      return folder.getFolderName();
    else if (store != null)
      return store.getStoreID();

    return "null";
  }

  /**
   * Returns the parent's name.
   */
  public String getParent() {
    if (parent != null)
      return parent.getAbsolutePath();
    else
      return this.getAbsolutePath();
  }

  /**
   * This returns the parent Folder as a FolderInfoFileWrapper.
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
    return (parent == null || parent == this);
  }

  /**
   * Tests to see if this can act as a directory.
   */
  public boolean isDirectory() {
    if (store != null)
      return true;

    if (folder != null) {
      Vector v = folder.getChildren();
      if (v != null)
        return (v.size() > 0);

    }

    return false;
  }

  /**
   * Tests to see if we should call this a File.
   */
  public boolean isFile() {
    if (store != null)
      return false;

    return (folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0;
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
    {
      getLogger().info("Running listFiles() on '" + getAbsolutePath() + "'");
    }

    if (isDirectory()) {
      if (children == null) {
        {
          getLogger().info("about to load children.");
        }
        loadChildren();
      }

      if (children != null)
        return children;
    }

    return new FolderInfoFileWrapper[0];
  }

  public File[] listFiles(FileFilter filter) {

    getLogger().info("Running listFiles(FileFilter) on '" + getAbsolutePath() + "'");

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

    getLogger().info("Running listFiles(FilenameFilter) on '" + getAbsolutePath() + "'");

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
   * This fails to create a new directory.  (return false)
   */
  public boolean mkdir() {
    return false;
  }

  /**
   * This fails to created a new directory.  (return false)
   */
  public boolean mkdirs() {
    return false;
  }


  /**
   * Returns false.
   */
  public boolean renameTo(File dest) {
    return false;
  }

  /**
   * This returns the wrapped FolderInfo.
   */
  public FolderInfo getFolderInfo() {
    return folder;
  }

  /**
   * This returns the wrapped StoreInfo.
   */
  public StoreInfo getStoreInfo() {
    return store;
  }

  /**
   * This loads the children of the Folder.
   */
  private synchronized void loadChildren() {
    {
      getLogger().info(Thread.currentThread().getName() + ":  calling loadChildren() on " + getAbsolutePath());
    }

    if (children == null) {
      {
        getLogger().info(Thread.currentThread().getName() + ":  children is null on " + getAbsolutePath() + ".  loading children.");
      }

      if (isDirectory()) {

        getLogger().info(Thread.currentThread().getName() + ":  calling folder.list()");

        Vector origChildren = null;
        if (store != null)
          origChildren = new Vector (store.getChildren());
        else if (folder != null)
          origChildren = new Vector (folder.getChildren());

        if (origChildren == null)
          origChildren = new Vector();

        FolderInfoFileWrapper[] tmpChildren = new FolderInfoFileWrapper[origChildren.size()];
        for (int i = 0; i < origChildren.size(); i++) {

          FolderInfo fi = (FolderInfo) origChildren.get(i);
          tmpChildren[i] = new FolderInfoFileWrapper(fi, this);
        }

        children = tmpChildren;
      }
    }

  }

  /**
   * This refreshes the children of the Folder.
   */
  public synchronized void refreshChildren() {
    {
      getLogger().info(Thread.currentThread().getName() + ":  calling refreshChildren() on " + getAbsolutePath());
      //Thread.dumpStack();
    }

    if (children == null) {
      {
        getLogger().info(Thread.currentThread().getName() + ":  children is null on " + getAbsolutePath() + ".  calling loadChildren().");
      }
      loadChildren();
    } else {
      if (isDirectory()) {

        Vector origChildren = null;
        if (store != null)
          origChildren = new Vector (store.getChildren());
        else if (folder != null)
          origChildren = new Vector (folder.getChildren());

        if (origChildren == null)
          origChildren = new Vector();

        FolderInfoFileWrapper[] tmpChildren = new FolderInfoFileWrapper[origChildren.size()];
        for (int i = 0; i < origChildren.size(); i++) {

          // yeah, this is n! or something like that. shouldn't matter--
          // if we have somebody running with that many folders, or with
          // that slow of a machine, we're in trouble anyway.
          FolderInfo fi = (FolderInfo) origChildren.get(i);

          //try to get a match.
          boolean found = false;
          for (int j = 0; ! found && j < children.length; j++) {
            if (children[j].getName().equals(fi.getFolderName())) {
              tmpChildren[i] = children[j];
              found = true;
            }
          }

          if (! found) {
            tmpChildren[i] = new FolderInfoFileWrapper(fi, this);
          }
        }

        children = tmpChildren;
      }
    }

  }

  /* Only accepts relative filenames. */
  public FolderInfoFileWrapper getFileByName(String filename) {


    getLogger().info("calling getFileByName(" + filename + ") on folder " + getName() + " (" + getPath() + ") (abs " + getAbsolutePath() + ")");

    String origFilename = new String(filename);
    if (filename == null || filename.length() < 1 || (filename.equals("/") && getParentFile() == null) || (filename.equals("/") && getParentFile() == this)) {

      getLogger().info("returning this for getFileByName()");
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

    FolderInfoFileWrapper currentFile = getChildFile(filename);
    if (currentFile != null && subdirFile != null) {
      // recurse with rest of components
      FolderInfoFileWrapper tmp = currentFile.getFileByName(subdirFile);
      //    tmp.path = origFilename;


      getLogger().info("created file " + tmp.getName() + " (" + tmp.getPath() + ") (abs " + tmp.getAbsolutePath() + ") from string " + origFilename + " on folder " + getName() + " (" + getPath() + ") (abs " + getAbsolutePath() + ")");

      return tmp;

    } else {
      return currentFile;
    }

  }

  FolderInfoFileWrapper getChildFile(String filename) {

    getLogger().info("calling getChildFile on " + getName() + " with filename " + filename);

    if (children == null)
      loadChildren();

    if (children != null) {
      for (int i = 0; i < children.length; i++) {
        if (children[i].getName().equals(filename))
          return children[i];
      }

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
   * Returns the Logger object for this class.
   */
  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getLogger("Pooka.debug.gui.filechooser");
  }

}
