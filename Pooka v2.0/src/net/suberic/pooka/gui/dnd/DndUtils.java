package net.suberic.pooka.gui.dnd;

import java.awt.datatransfer.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.gui.*;

/**
 * A set of utility methods to use with drag and drop.
 */
public class DndUtils {

  /**
   * Indicates if it is safe to access the system clipboard. Once false,
   * access will never be checked again.
   */
  private static boolean canAccessSystemClipboard = true;

  /**
   * Key used in app context to lookup Clipboard to use if access to
   * System clipboard is denied.
   */
  private static Object SandboxClipboardKey = new Object();

  static boolean isLinux = System.getProperty("os.name").startsWith("Linux");

  /**
   * Returns true if this set of DataFlavors might include a FileFlavor.
   */
  public static boolean hasFileFlavor(DataFlavor[] flavors) {
    // first see if we're on linux.
    if (flavors != null) {
      for (int i = 0; i < flavors.length; i++) {
        if (flavors[i]!= null && flavors[i].isFlavorJavaFileListType())
          return true;
        else if (isLinux && flavors[i] != null && flavors[i].isFlavorTextType())
          return true;
      }
    }

    return false;
  }

  /**
   * Extracts a List of File objects from a Transferable.
   */
  public static List extractFileList(Transferable t) throws UnsupportedFlavorException, java.io.IOException {
    DataFlavor[] availableFlavors = t.getTransferDataFlavors();
    DataFlavor match = matchDataFlavor(new DataFlavor[] { DataFlavor.javaFileListFlavor }, availableFlavors);
    if (match != null) {
      return (java.util.List) t.getTransferData(DataFlavor.javaFileListFlavor);
    } else if (isLinux) {
      match = matchDataFlavor(new DataFlavor[] { DataFlavor.stringFlavor }, availableFlavors);
      if (match != null) {
        ArrayList returnValue = new ArrayList();
        Reader urlReader = match.getReaderForText(t);
        BufferedReader br = new BufferedReader(urlReader);
        for (String line = br.readLine(); line != null && line.length() > 0; line = br.readLine()) {
          try {
            java.net.URI fileUri = new java.net.URI(line);
            File currentFile = new File(fileUri);
            returnValue.add(currentFile);
          } catch (java.net.URISyntaxException e) {
            e.printStackTrace();
          }
        }

        return returnValue;
      }
    }

    return null;
  }

  /**
   * Finds the first acceptable DataFlavor match and returns it,
   * or null if no match is found.
   */
  public static DataFlavor matchDataFlavor(DataFlavor[] acceptableFlavors, DataFlavor[] availableFlavors) {
    if (acceptableFlavors != null && availableFlavors != null) {
      for (int i = 0; i < availableFlavors.length; i++) {
        for (int j = 0; j < acceptableFlavors.length; j++) {
          if (availableFlavors[i] != null && availableFlavors[i].match(acceptableFlavors[j]))
            return availableFlavors[i];
        }
      }
    }

    return null;
  }

  /**
   * Creates a temporary file with the given name.
   */
  public static File createTemporaryFile(String fileName) throws java.io.IOException {
    File returnValue = null;
    File tmpDir = null;
    String tmpDirString = System.getProperty("java.io.tmpdir");
    if (tmpDirString != null && tmpDirString.length() > 0) {
      File firstType = new File(tmpDirString);
      if (firstType != null && firstType.exists() && firstType.isDirectory() && firstType.canWrite()) {
        tmpDir = firstType;
      }
    }

    if (tmpDir == null) {
      // try creating a temporary file.
      File tempfile = File.createTempFile("pooka", "tmp");
      tmpDir = tempfile.getParentFile();
    }

    if (tmpDir != null) {
      File testMe = new File(tmpDir, fileName);
      if (! testMe.exists()) {
        returnValue = testMe;
      }
    }

    if (returnValue == null) {
      // oh well.  just create a normal temporary file.
      returnValue = File.createTempFile(fileName, null);
    }

    returnValue.deleteOnExit();

    return returnValue;
  }

  /**
   * Gets the FolderInfo from the given Component.
   */
  public static FolderInfo getFolderInfo(JComponent c) {
    try {
      if (c instanceof FolderDisplayPanel) {
        return ((FolderDisplayPanel) c).getFolderInfo();
      }

      Object o = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.FolderDisplayPanel"), c);
      if (o != null) {
        return ((net.suberic.pooka.gui.FolderDisplayPanel) o).getFolderInfo();
      }

      // check for the folder tree.
      o = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.FolderPanel"), c);
      if (o != null) {
        Object selected = ((net.suberic.pooka.gui.FolderPanel) o).getSelectedNode();
        if (selected instanceof FolderNode) {
          return ((FolderNode) selected).getFolderInfo();
        }
      }

      return null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Returns the clipboard to use for cut/copy/paste.
   */
  public static Clipboard getClipboard(JComponent c) {
    if (canAccessSystemClipboard()) {
      return c.getToolkit().getSystemClipboard();
    }
    return null;
    /*
      Clipboard clipboard = (Clipboard)sun.awt.AppContext.getAppContext().get(SandboxClipboardKey);
      if (clipboard == null) {
      clipboard = new Clipboard("Sandboxed Component Clipboard");
      sun.awt.AppContext.getAppContext().put(SandboxClipboardKey, clipboard);
      }
      return clipboard;
    */
  }

  /**
   * Returns true if it is safe to access the system Clipboard.
   * If the environment is headless or the security manager
   * does not allow access to the system clipboard, a private
   * clipboard is used.
   */
  public static boolean canAccessSystemClipboard() {
    if (canAccessSystemClipboard) {
      if (GraphicsEnvironment.isHeadless()) {
        canAccessSystemClipboard = false;
        return false;
      }

      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
        try {
          sm.checkSystemClipboardAccess();
          return true;
        } catch (SecurityException se) {
          canAccessSystemClipboard = false;
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Clears the clipboard of its current selection.
   */
  public static void clearClipboard(JComponent c) {
    Clipboard cb = getClipboard(c);
    if (cb != null) {
      cb.setContents(new StringSelection(""),null);
    }
  }
}
