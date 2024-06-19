package net.suberic.pooka.gui.dnd;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

import net.suberic.pooka.*;
import net.suberic.pooka.gui.*;

/**
 * A TransferHandler for a Folder.
 */
public class FolderTransferHandler extends TransferHandler {

  static DataFlavor[] acceptableFlavors = new DataFlavor[] {
    MessageProxyTransferable.sMessageProxyDataFlavor
  };

  public boolean importData(JComponent c, Transferable t) {
    if (!canImport(c, t.getTransferDataFlavors())) {
      return false;
    } else {
      FolderInfo fi = DndUtils.getFolderInfo(c);
      if (fi != null) {
        MessageProxy mp = null;
        try {
          mp = (MessageProxy) t.getTransferData(MessageProxyTransferable.sMessageProxyDataFlavor);
          if (mp != null) {
            // check to see if we're moving to the same folder.  if so,
            // don't allow it.
            if (mp.getFolderInfo() == fi) {
              return false;
            }
            mp.getMessageInfo().copyMessage(fi);
            mp.setImportDone(true);
            if (mp.removeMessageOnCompletion()) {
              DndUtils.clearClipboard(c);
            }
            return true;
          }
        } catch (Exception e) {
          if (mp != null)
            mp.showError( Pooka.getProperty("error.Message.CopyErrorMessage", "Error:  could not copy messages to folder:  ") + fi.toString() +"\n", e);
          if (Pooka.isDebug())
            e.printStackTrace();
          return false;
        }
      } else {
        return false;
      }
    }

    return false;
  }

  protected Transferable createTransferable(JComponent c) {
    if (c instanceof net.suberic.pooka.gui.FolderDisplayPanel) {
      return new MessageProxyTransferable(((FolderDisplayPanel) c).getSelectedMessage());
    } else if (c instanceof JTable) {
      try {
        Object o = SwingUtilities.getAncestorOfClass(Class.forName("net.suberic.pooka.gui.FolderDisplayPanel"), c);
        if (o != null ) {

          Transferable returnValue = new MessageProxyTransferable(((FolderDisplayPanel) o).getSelectedMessage());
          return returnValue;
        } else {
          return null;
        }
      } catch (Exception e) {
        return null;
      }
    } else {
      return null;
    }
  }

  public int getSourceActions(JComponent c) {
    return COPY_OR_MOVE;
  }

  protected void exportDone(JComponent c, Transferable data, int action) {
    if (action == MOVE) {
      try {
        MessageProxy mp = (MessageProxy) data.getTransferData(MessageProxyTransferable.sMessageProxyDataFlavor);
        if (mp != null) {
          mp.setDeleteInProgress(true);
          mp.setActionType(action);
          if (mp.removeMessageOnCompletion()) {
            DndUtils.clearClipboard(c);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    boolean returnValue = (DndUtils.matchDataFlavor(acceptableFlavors, flavors) != null);
    return returnValue;
  }

  /**
   * Causes a transfer from the given component to the
   * given clipboard.  This method is called by the default cut and
   * copy actions registered in a component's action map.
   * <p>
   * The transfer <em>will</em> have been completed at the
   * return of this call.  The transfer will take place using the
   * <code>java.awt.datatransfer</code> mechanism,
   * requiring no further effort from the developer.
   * The <code>exportDone</code> method will be called when the
   * transfer has completed.
   *
   * @param comp  the component holding the data to be transferred; this
   *  argument is provided to enable sharing of <code>TransferHandler</code>s by
   *  multiple components
   * @param clip  the clipboard to transfer the data into
   * @param action the transfer action requested; this should
   *  be a value of either <code>COPY</code> or <code>MOVE</code>;
   *  the operation performed is the intersection  of the transfer
   *  capabilities given by getSourceActions and the requested action;
   *  the intersection may result in an action of <code>NONE</code>
   *  if the requested action isn't supported
   */
  public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
    boolean exportSuccess = false;
    Transferable t = null;

    int clipboardAction = getSourceActions(comp) & action;
    if (clipboardAction != NONE) {
      t = createTransferable(comp);
      if (t != null) {
        if (t instanceof MessageProxyTransferable) {
          clip.setContents(t, ((MessageProxyTransferable) t).getMessageProxy());
        } else {
          clip.setContents(t, null);
        }
        exportSuccess = true;
      }
    }

    if (exportSuccess) {
      exportDone(comp, t, clipboardAction);
    } else {
      exportDone(comp, null, NONE);
    }
  }


  public static Action getCutAction(JComponent pSource) {
    return new FolderTransferAction("cut-to-clipboard", pSource);
  }

  public static Action getCopyAction(JComponent pSource) {
    return new FolderTransferAction("copy-to-clipboard", pSource);
  }

  public static Action getPasteAction(JComponent pSource) {
    return new FolderTransferAction("paste-from-clipboard", pSource);
  }

  static class FolderTransferAction extends AbstractAction implements javax.swing.plaf.UIResource {

    JComponent mSource = null;

    FolderTransferAction(String name, JComponent pSource) {
      super(name);
      mSource = pSource;
      // Will cause the system clipboard state to be updated.

      DndUtils.canAccessSystemClipboard();
    }

    public void actionPerformed(ActionEvent e) {
      Object src = mSource;

      if (src instanceof JComponent) {
        JComponent c = (JComponent) src;
        TransferHandler th = c.getTransferHandler();
        Clipboard clipboard = DndUtils.getClipboard(c);
        String name = (String) getValue(Action.NAME);
        if ((clipboard != null) && (th != null) && (name != null)) {
          if ("cut-to-clipboard".equals(name)) {
            th.exportToClipboard(c, clipboard, MOVE);
          } else if ("copy-to-clipboard".equals(name)) {
            th.exportToClipboard(c, clipboard, COPY);
          } else if ("paste-from-clipboard".equals(name)) {
            Transferable trans = clipboard.getContents(null);
            if (trans != null) {
              th.importData(c, trans);
            }
          }
        }
      }
    }
  }
}
