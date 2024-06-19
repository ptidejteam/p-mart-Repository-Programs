package net.suberic.pooka.gui.filechooser;
import javax.swing.*;
import javax.mail.*;
import java.io.*;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.StoreInfo;
import java.util.Vector;

/**
 * This class implements a FileSystemView based on the available Folders in
 * a Store.
 */

public class MailFileSystemView
  extends javax.swing.filechooser.FileSystemView {

  StoreInfo[] storeList;
  FolderFileWrapper[] roots = null;

  /**
   * This creates a new MailFileSystemView at the top of the Store list;
   * all available Stores will be listed, with their folders under them.
   */
  public MailFileSystemView() {
    Vector v = Pooka.getStoreManager().getStoreList();
    for (int i = v.size() - 1; i >= 0; i--) {
      StoreInfo current = (StoreInfo) v.elementAt(i);
      if (! current.isConnected())
        v.removeElementAt(i);
    }

    storeList = new StoreInfo[v.size()];
    for (int i = 0; i < v.size(); i++) {
      storeList[i] = (StoreInfo) v.elementAt(i);
    }

    getRoots();
  }

  /**
   * This creates a MailFileSystemView out of a Store object.
   */
  public MailFileSystemView(StoreInfo newStore) {
    storeList = new StoreInfo[] { newStore };

    getLogger().info("creating new MailFileSystemView for store " + newStore.getStoreID());

    try {
      if (!newStore.isConnected()) {

        getLogger().info("store is not connected.  connecting.");
        newStore.connectStore();
      }
      getRoots();
    } catch (OperationCancelledException oce) {
    } catch (MessagingException me) {
      getLogger().info("caught messagingException : " + me.getMessage());
      me.printStackTrace();
    }
  }

  /**
   * This creates a new Folder and FolderFileWrapper in the Folder
   * corresponding to the directory dir with the name filename.
   *
   * @dir a FolderFileWrapper representing an IMAP folder.
   * @filename a string representing an IMAP folder name.
   */
  public File createFileObject(File dir, String filename) {
    if (dir != null)
      getLogger().info("calling createFileObject on directory " + dir.getName() + " (" + dir.getPath() + "), filename " + filename);
    else
      getLogger().info("calling createFileObject on directory null, filename " + filename);


    if (dir != null && dir instanceof FolderFileWrapper)
      return ((FolderFileWrapper)dir).getFileByName(filename);
    else
      return null;
  }

  /**
   * @filename is an IMAP folder name.
   */
  public File createFileObject(String filename) {
    // todo jph:  strip off any leading directoy separators.  we
    // want to call getFileByName with a relative path (in this case
    // to the root directory) always.

    getLogger().info("running createFileObject2 on filename '" + filename + "'");

    if (roots == null || roots.length == 0) {
      getLogger().info("root == null");
      return null;
    }


    getLogger().info("root != null");

    if (filename.equals("/") || filename.equals("")) {
      return roots[0];
    }

    int firstSlash = filename.indexOf('/');
    String storeName = null;
    String filePart = "";
    if (firstSlash > -1) {
      storeName = filename.substring(0, firstSlash);

      getLogger().info("store name is " + storeName);
      if (firstSlash < filename.length()) {
        filePart = filename.substring(firstSlash + 1);

        getLogger().info("file name is " + filePart);
      }
    } else {
      getLogger().info("store name is " + filename);

      storeName = filename;
    }

    FolderFileWrapper currentRoot = findRoot(storeName);
    if (currentRoot == null) {
      getLogger().info("found no matching store root for " + storeName + ".");
      return new File(filename);
    }

    File returnValue = currentRoot.getFileByName(filePart);
    return returnValue;
  }

  /**
   * Creates a new Folder under the containingDir.
   */
  public File createNewFolder(File containingDir) {

    getLogger().info("running createNewFolder.");

    try {
      Folder parentFolder = null;
      if (containingDir instanceof FolderFileWrapper) {
        parentFolder = ((FolderFileWrapper)containingDir).getFolder();

        Folder newFolder = parentFolder.getFolder("New_folder");
        for (int i = 1; newFolder.exists(); i++) {
          newFolder=parentFolder.getFolder("New_folder_" + i);
        }

        newFolder.create(Folder.HOLDS_FOLDERS);

        ((FolderFileWrapper) containingDir).refreshChildren();

        //return new FolderFileWrapper(newFolder, (FolderFileWrapper)containingDir);
        return ((FolderFileWrapper)containingDir).getFileByName(newFolder.getName());
      } else {
        return null;

        //parentFolder = store.getFolder(containingDir.getAbsolutePath());
      }
    } catch (MessagingException me) {
      Pooka.getUIFactory().showError(Pooka.getProperty("error.creatingFolder", "Error creating folder:  "), me);
    }

    return null;
  }

  /**
   * Gets the child for the file.
   */
  public File getChild(File parent, String filename) {
    getLogger().fine("getting child of file " + parent + ", filename " + filename);
    if (parent instanceof FolderFileWrapper) {
    // check for degenerate case.
      if (parent != null && filename != null && filename.equalsIgnoreCase(parent.getAbsolutePath())) {
        getLogger().fine("parent matches child; returning parent.");

        return parent;
      } else {
        return ((FolderFileWrapper) parent).getChildFile(filename);
      }
    } else {
      return new File(parent, filename);
    }
  }

  /**
   * Gets the default starting directory for the file chooser.
   */
  public File getDefaultDirectory() {
    return getDefaultRoot();
  }

  /**
   * Returns all of the files under a particular directory.
   */
  public File[] getFiles(File dir, boolean useFileHiding) {

    getLogger().info("running getFiles " + dir + ", " + useFileHiding + ".");

    if (dir instanceof FolderFileWrapper) {

      getLogger().info("getFiles:  returning dir.listFiles()");
      return ((FolderFileWrapper)dir).listFiles();
    } else {

      getLogger().info("getFiles:  dir isn't a FFW.");
      if (dir == null) {

        getLogger().info("getFiles:  dir is null; returning null.");
        return null; // FIXME: or set dir to root?
      }

      // FIXME: ugly?


      getLogger().info("getFiles:  just returning the root.");

      File f = ((FolderFileWrapper)getDefaultRoot()).getFileByName(dir.getAbsolutePath());

      if (f == null) {

        getLogger().info("getFiles:  tried returning the root, but got null.  returning the root itself instead.");
        return new FolderFileWrapper[0];
      }


      getLogger().info("getFiles:  returning " + f + ".listFiles() for getFiles()");
      return f.listFiles();
    }
  }

  /**
   * Returns the user's home directory.  Kind of a strange thing
   * on a mail system...
   */
  public File getHomeDirectory() {

    getLogger().info("running getHomeDirectory().");

    return getDefaultRoot();
  }

  /**
   * Returns the parent directory of the current File.
   */
  public File getParentDirectory(File dir) {

    getLogger().info("running getParentDirectory on " + dir);

    if (dir == null)
      return null; // at root

    if (! (dir instanceof FolderFileWrapper)) {
      if (roots != null && roots.length > 0) {
        dir = createFileObject(dir.getPath());
      } else
        return null; // FIXME error?

    }
    if (dir == null)
      return null; // at root

    return dir.getParentFile();
  }

  /**
   * Gets all the roots for this MailFileSystemView.
   */
  public File[] getRoots() {

    getLogger().info("calling getRoots() on MailFileSystemView.");

    if (roots != null) {

      getLogger().info("root has already been set.");
      return roots;
    }
    try {

      getLogger().info("setting folder f to store.getDefaultFolder().");
      roots = new FolderFileWrapper[storeList.length];
      for (int i = 0; i < storeList.length; i++) {
        synchronized(storeList[i].getStoreThread().getRunLock()) {
          Folder f = storeList[i].getStore().getDefaultFolder();
          roots[i] = new FolderFileWrapper(f, storeList[i].getStoreID(), storeList[i].getStoreThread().getRunLock());
        }
      }
      return roots;
    } catch (MessagingException me) {
      return null; // FIXME: throw this on
    }
  }

  /**
   * always returns false for now.
   */
  public boolean isHiddenFile(File f) {
    return false;
  }

  /**
   * returns true for all files in the roots array.
   */
  public boolean isRoot(File f) {
    if (f.getParentFile() == null)
      return true;
    else
      return false;
  }

  /**
   * Returns true if the directory is traversable.
   */
  public Boolean isTraversable(File f) {
    if (f != null && f instanceof FolderFileWrapper) {
      if (((FolderFileWrapper) f).isDirectory())
        return new Boolean(true);
      else
        return new Boolean(false);
    } else
      return new Boolean(false);
  }

  /*
   * Checks if <code>f</code> represents a real directory or file as opposed to a
   * special folder such as <code>"Desktop"</code>. Used by UI classes to decide if
   * a folder is selectable when doing directory choosing.
   *
   * @param f a <code>File</code> object
   * @return <code>true</code> if <code>f</code> is a real file or directory.
   */
  public boolean isFileSystem(File f) {
    return true;
  }

  /*
   * Is dir the root of a tree in the file system, such as a drive
   * or partition. Example: Returns true for "C:\" on Windows 98.
   *
   * @See also isRoot
   */
  public boolean isFileSystemRoot(File dir) {
    return isRoot(dir);
  }

  /*
   * Used by UI classes to decide whether to display a special icon
   * for drives or partitions, e.g. a "hard disk" icon.
   *
   * The default implementation has no way of knowing, so always returns false.
   *
   * @param dir a directory
   * @return <code>false</code> always
   */
  public boolean isDrive(File dir) {
    return false;
  }

  /*
   * Used by UI classes to decide whether to display a special icon
   * for a floppy disk. Implies isDrive(dir).
   *
   * The default implementation has no way of knowing, so always returns false.
   *
   * @param dir a directory
   * @return <code>false</code> always
   */
  public boolean isFloppyDrive(File dir) {
    return false;
  }

  /*
   * Used by UI classes to decide whether to display a special icon
   * for a computer node, e.g. "My Computer" or a network server.
   *
   * The default implementation has no way of knowing, so always returns false.
   *
   * @param dir a directory
   * @return <code>false</code> always
   */
  public boolean isComputerNode(File dir) {
    return false;
  }


  /**
   * On Windows, a file can appear in multiple folders, other than its
   * parent directory in the filesystem. Folder could for example be the
   * "Desktop" folder which is not the same as file.getParentFile().
   *
   * @param folder a <code>File</code> object repesenting a directory or special folder
   * @param file a <code>File</code> object
   * @return <code>true</code> if <code>folder</code> is a directory or special folder and contains <code>file</code>.
   */
  public boolean isParent(File folder, File file) {
    if (folder == null || file == null) {
      return false;
    } else {
      return folder.equals(file.getParentFile());
    }
  }

  /**
   * Type description for a file, directory, or folder as it would be displayed in
   * a system file browser. Example from Windows: the "Desktop" folder
   * is desribed as "Desktop".
   *
   * The Windows implementation gets information from the ShellFolder class.
   */
  public String getSystemTypeDescription(File f) {
    if (f != null) {
      return ("mail folder");
    } else {
      return null;
    }
  }

  /**
   * Name of a file, directory, or folder as it would be displayed in
   * a system file browser. Example from Windows: the "M:\" directory
   * displays as "CD-ROM (M:)"
   *
   * The default implementation gets information from the ShellFolder class.
   *
   * @param f a <code>File</code> object
   * @return the file name as it would be displayed by a native file chooser
   * @see JFileChooser#getName
   */
  public String getSystemDisplayName(File f) {
    String name = null;
    if (f != null) {
      name = f.getName();
    }
    return name;
  }

  /**
   * Icon for a file, directory, or folder as it would be displayed in
   * a system file browser. Example from Windows: the "M:\" directory
   * displays a CD-ROM icon.
   *
   * The default implementation gets information from the ShellFolder class.
   *
   * @param f a <code>File</code> object
   * @return an icon as it would be displayed by a native file chooser
   * @see JFileChooser#getIcon
   */
  public Icon getSystemIcon(File f) {
    if (f != null) {
      return UIManager.getIcon(f.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
    } else {
      return null;
    }
  }


  /* Not inherited. */

  public File getDefaultRoot() {
    if (roots == null)
      {
        File[] localRoots = getRoots();
        if (localRoots != null && localRoots.length > 0)
          return localRoots[0];
        else
          return null;
      }
    return roots[0];
  }

  /**
   * This finds the Root with the given name, if any.
   */
  public FolderFileWrapper findRoot(String name) {
    for (int i = 0; i < roots.length; i++) {
      if (roots[i].getPath().equals(name))
        return roots[i];
    }
    return null;
  }

  /**
   * Returns the Logger object for this class.
   */
  public java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getLogger("Pooka.debug.gui.filechooser");
  }

}

