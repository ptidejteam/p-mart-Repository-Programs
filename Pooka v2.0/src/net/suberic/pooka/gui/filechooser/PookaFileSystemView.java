package net.suberic.pooka.gui.filechooser;
import javax.swing.*;
import java.io.*;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.StoreInfo;
import net.suberic.pooka.FolderInfo;
import java.util.Vector;

/**
 * This class implements a FileSystemView based on the available Folders in
 * a the Pooka StoreInfo hierarchy.
 */

public class PookaFileSystemView
  extends javax.swing.filechooser.FileSystemView {
  
  StoreInfo[] storeList;
  FolderInfoFileWrapper[] roots = null;
  
  /**
   * This creates a new PookaFileSystemView at the top of the Store list; 
   * all available Stores will be listed, with their folders under them.
   */
  public PookaFileSystemView() {
    Vector v = Pooka.getStoreManager().getStoreList();
    
    storeList = new StoreInfo[v.size()];
    for (int i = 0; i < v.size(); i++) {
      storeList[i] = (StoreInfo) v.elementAt(i);
    }
    
    getRoots();
  }
  
  /**
   * This creates a PookaFileSystemView out of a Store object.
   */
  public PookaFileSystemView(StoreInfo newStore) {
    storeList = new StoreInfo[] { newStore };
    getLogger().info("creating new PookaFileSystemView for store " + newStore.getStoreID());
    
    getRoots();
  }
  
  /**
   * This creates a new Folder and FolderInfoFileWrapper in the Folder 
   * corresponding to the directory dir with the name filename.
   * 
   * @dir a FolderInfoFileWrapper representing either a FolderInfo or
   *      a StoreInfo
   * @filename a string representing a FolderInfo name.
   */
  public File createFileObject(File dir, String filename) {
    if (dir != null)
      getLogger().info("calling createFileObject on directory " + dir.getName() + " (" + dir.getPath() + "), filename " + filename);
    else
      getLogger().info("calling createFileObject on directory null, filename " + filename);
    
    if (dir != null && dir instanceof FolderInfoFileWrapper)
      return ((FolderInfoFileWrapper)dir).getFileByName(filename);
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
    
    getLogger().info("store name is " + filename + ", file name is " + filePart);

    FolderInfoFileWrapper currentRoot = findRoot(storeName);
    if (currentRoot == null) {
      getLogger().info("found no matching store root for " + storeName + ".");
      return new File(filename);
    }
    
    File returnValue = currentRoot.getFileByName(filePart);

    getLogger().info("returning " + returnValue + " for " + currentRoot + ".getFileByName(\"" + filePart + "\")");
    
    return returnValue;
    
  }
  
  /**
   * Creates a new File object for f with correct behavior for a file system 
   * root directory.
   */
  /*
  protected File createFileSystemRoot(File f) {
    
  }
  */

  /**
   * Creates a new Folder under the containingDir.
   */
  public File createNewFolder(File containingDir) throws java.io.IOException {
    throw new IOException (Pooka.getProperty("error.folderinfofilewrapper.cantcreate", "Cannot create new Folders here.  Use Subscribe instead."));

  }

  /**
   * Gets the child for the file.
   */
  public File getChild(File parent, String filename) {
    if (parent instanceof FolderInfoFileWrapper) {
      return ((FolderInfoFileWrapper) parent).getChildFile(filename);
    } else {
      return new File(parent, filename);
    }
  }

  /**
   * Gets the default starting directory for the file chooser.
   */
  public File getDefaultDirectory() {
    getLogger().info("calling getDefaultDirectory()");

    return getDefaultRoot();
  }

  /**
   * Returns all of the files under a particular directory.
   */
  public File[] getFiles(File dir, boolean useFileHiding) {
    getLogger().info("running getFiles " + dir + ", " + useFileHiding + ".");
    
    if (dir instanceof FolderInfoFileWrapper) {
      getLogger().info("getFiles:  returning dir.listFiles()");
      return ((FolderInfoFileWrapper)dir).listFiles();
    } else {
      getLogger().info("getFiles:  dir isn't a FFW.");
      if (dir == null) {
	getLogger().info("getFiles:  dir is null; returning null.");
	return null; // FIXME: or set dir to root?
      }
      
      // FIXME: ugly?
      
      getLogger().info("getFiles:  just returning the root.");
      
      File f = ((FolderInfoFileWrapper)getDefaultRoot()).getFileByName(dir.getAbsolutePath());
      
      if (f == null) {
	getLogger().info("getFiles:  tried returning the root, but got null.  returning the root itself instead.");
	return new FolderInfoFileWrapper[0];
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

    if (! (dir instanceof FolderInfoFileWrapper)) {
      if (roots != null && roots.length > 0) {
	dir = createFileObject(dir.getPath());
      } else 
	return null; // FIXME error?
      
    }
    if (dir == null)
      return null; // at root
    File returnValue = dir.getParentFile();

    return dir.getParentFile();
  }
  
  /**
   * Gets all the roots for this PookaFileSystemView.
   */
    public File[] getRoots() {
      getLogger().info("calling getRoots() on PookaFileSystemView.");

      if (roots != null) {
	getLogger().info
	  ("root has already been set.");
	return roots;
      }

      getLogger().info("setting folder f to store.getDefaultFolder().");
      roots = new FolderInfoFileWrapper[storeList.length];
      for (int i = 0; i < storeList.length; i++) {
	roots[i] = new FolderInfoFileWrapper(storeList[i], null, storeList[i].getStoreID());
      }
      return roots;
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
    if (f.getParentFile() == null || f.getParentFile() == f)
      return true;
    else
      return false;
  }
  
  
  /**
   * Returns true if the directory is traversable.
   */
  public Boolean isTraversable(File f) {
    getLogger().info("pfsv:  checking isTraversable on file " + f);
    
    if (f != null && f instanceof FolderInfoFileWrapper) {
      if (((FolderInfoFileWrapper) f).isDirectory())
	return new Boolean(true);
      else
	return new Boolean(false);
    } else {
      return new Boolean(false);
    }
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
    getLogger().info("running getDefaultRoot().");

    if (roots == null) {
      File[] localRoots = getRoots();
      if (localRoots != null && localRoots.length > 0)
	return localRoots[0];
      else
	return null;
    }
    
    getLogger().info("returning " + roots[0] + " as default root.");

    return roots[0];
  }
  
  /**
   * This finds the Root with the given name, if any.
   */
  public FolderInfoFileWrapper findRoot(String name) {
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
