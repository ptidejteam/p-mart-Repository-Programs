package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.gui.IconManager;
import javax.swing.tree.*;
import java.awt.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;

/**
 * This is basically an extension of DefaultFolderTreeCellRenderer that has
 * icon support added.
 */
public class EnhancedFolderTreeCellRenderer extends DefaultFolderTreeCellRenderer {

  protected boolean hasFocus;

  Icon rootIcon;
  Icon connectedIcon;
  Icon disconnectedIcon;
  Icon closedFolderIcon;
  Icon unavailableIcon;
  Icon connectedStoreIcon;
  Icon disconnectedStoreIcon;
  Icon closedStoreIcon;
  Icon subfolderIcon;
  Icon subfolderWithNewIcon;
  Icon subfolderClosedIcon;
  Icon connectedWithNewIcon;
  Icon disconnectedWithNewIcon;

  /**
   * Creates the EnhancedFolderTreeCellRenderer.
   */
  public EnhancedFolderTreeCellRenderer() {
    super();
  }

  /**
   * gets the renderer component.
   */
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
    TreePath tp = tree.getPathForRow(row);

    if (tp != null) {
      Object lastPath = tp.getLastPathComponent();
      if (lastPath instanceof FolderNode) {
        FolderNode node = (FolderNode)lastPath;

        FolderInfo fi = node.getFolderInfo();

        if (isSpecial(node)) {
          setFontToSpecial();
        } else {
          setFontToDefault();
        }

        FolderInfo folderInfo = ((FolderNode)node).getFolderInfo();

        if (folderInfo == null){
          setIconToClosedFolder();
        } else {
          //System.out.println("folderInfo is " + folderInfo.getFolderID() + "; hasNewMessages is " + folderInfo.hasNewMessages() + "; notifyNewMessagesNode is "+ folderInfo.notifyNewMessagesNode());
          if (!((FolderNode)node).isLeaf()) {
            //System.out.println("folderInfo is " + folderInfo.getFolderID() + "; hasNewMessages is " + folderInfo.hasNewMessages());
            if (folderInfo.hasNewMessages() && folderInfo.notifyNewMessagesNode())
              setIconToSubfolderWithNew();
            else if ((folderInfo.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
              // folder that don't hold messages are always disconnected,
              // so show their status by their store.
              if ( folderInfo.isConnected()) {
                setIconToSubfolder();
              } else {
                setIconToSubfolderClosed();
              }
            } else {
              if ( folderInfo.getParentStore().isConnected()) {
                setIconToSubfolder();
              } else {
                setIconToSubfolderClosed();
              }
            }
          } else if (folderInfo.isConnected()) {
            if (folderInfo.notifyNewMessagesNode() && folderInfo.hasNewMessages()) {
              setIconToOpenWithNew();
            } else
              setIconToOpen();
          } else if (folderInfo.isSortaOpen()) {
            if (folderInfo.notifyNewMessagesNode() && folderInfo.hasNewMessages()) {
              setIconToDisconnectedWithNew();
            } else
              setIconToDisconnected();
          } else if (!folderInfo.isValid()) {
            setIconToUnavailable();
          } else {
            setIconToClosedFolder();
          }
        }
      } else if (lastPath instanceof StoreNode) {
        StoreInfo storeInfo = ((StoreNode)lastPath).getStoreInfo();
        if (storeInfo.isConnected())
          setIconToConnectedStore();
        else
          setIconToDisconnectedStore();

        setFontToDefault();
      } else {
        setIconToRoot();
      }
    } else {
      setIconToDisconnected();
    }

    String stringValue = tree.convertValueToText(value, sel, expanded, leaf, row, hasFocus);
    setText(stringValue);
    return this;
  }

  /**
   * Sets the icon to the unavailable icon.
   */
  public void setIconToUnavailable() {
    if (getUnavailableIcon() != null)
      setIcon(getUnavailableIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.Unavailable");

      if (icon != null) {
        setUnavailableIcon(icon);
        setIcon(getUnavailableIcon());
      }
    }
  }

  /**
   * Sets the icon to the open icon.
   */
  public void setIconToOpen() {
    if (getConnectedIcon() != null)
      setIcon(getConnectedIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.Connected");

      if (icon != null) {
        setConnectedIcon(icon);
        setIcon(getConnectedIcon());
      }
    }
  }

  /**
   * Sets the icon to open with new.
   */
  public void setIconToOpenWithNew() {
    if (getConnectedWithNewIcon() != null)
      setIcon(getConnectedWithNewIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.ConnectedNew");

      if (icon != null) {
        setConnectedWithNewIcon(icon);
        setIcon(getConnectedWithNewIcon());
      }
    }
  }

  /**
   * Sets the icon to the disconnected icon.
   */
  public void setIconToDisconnected() {
    if (getDisconnectedIcon() != null)
      setIcon(getDisconnectedIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.Disconnected");

      if (icon != null) {
        setDisconnectedIcon(icon);
        setIcon(getDisconnectedIcon());
      }
    }
  }

  /**
   * Sets the icon to disconnected with new.
   */
  public void setIconToDisconnectedWithNew() {
    if (getDisconnectedWithNewIcon() != null)
      setIcon(getDisconnectedWithNewIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.DisconnectedNew");

      if (icon != null) {
        setDisconnectedWithNewIcon(icon);
        setIcon(getDisconnectedWithNewIcon());
      }
    }
  }

  public void setIconToClosedFolder() {
    if (getClosedFolderIcon() != null)
      setIcon(getClosedFolderIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.Closed");

      if (icon != null) {
        setClosedFolderIcon(icon);
        setIcon(getClosedFolderIcon());
      }
    }
  }

  public void setIconToDisconnectedStore() {
    if (getDisconnectedStoreIcon() != null)
      setIcon(getDisconnectedStoreIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.StoreDisconnected");

      if (icon != null) {
        setDisconnectedStoreIcon(icon);
        setIcon(getDisconnectedStoreIcon());
      }
    }
  }

  public void setIconToConnectedStore() {
    if (getConnectedStoreIcon() != null)
      setIcon(getConnectedStoreIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.StoreConnected");

      if (icon != null) {
        // create the new Icon.
        setConnectedStoreIcon(icon);
        setIcon(getConnectedStoreIcon());
      }
    }
  }

  public void setIconToSubfolderClosed() {
    if (getSubfolderClosedIcon() != null)
      setIcon(getSubfolderClosedIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.SubFolderClosed");

      if (icon != null) {
        setSubfolderClosedIcon(icon);
        setIcon(getSubfolderClosedIcon());
      }
    }
  }

  public void setIconToSubfolder() {
    if (getSubfolderIcon() != null)
      setIcon(getSubfolderIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.SubFolder");

      if (icon != null) {
        setSubfolderIcon(icon);
        setIcon(getSubfolderIcon());
      }
    }
  }

  public void setIconToSubfolderWithNew() {
    if (getSubfolderWithNewIcon() != null)
      setIcon(getSubfolderWithNewIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.SubFolderNew");

      if (icon != null) {
        setSubfolderWithNewIcon(icon);
        setIcon(getSubfolderWithNewIcon());
      }
    }
  }

  public void setIconToRoot() {
    if (getRootIcon() != null)
      setIcon(getRootIcon());
    else {
      // create the new Icon.
      ImageIcon icon = Pooka.getUIFactory().getIconManager().getIcon("FolderTree.Root");

      if (icon != null) {
        setRootIcon(icon);
        setIcon(getRootIcon());
      }
    }
  }

  public Icon getConnectedIcon() {
    return connectedIcon;
  }

  public void setConnectedIcon(Icon newIcon) {
    connectedIcon = newIcon;
  }

  public Icon getConnectedWithNewIcon() {
    return connectedWithNewIcon;
  }

  public void setConnectedWithNewIcon(Icon newIcon) {
    connectedWithNewIcon = newIcon;
  }

  public void setDisconnectedIcon(Icon newIcon) {
    disconnectedIcon = newIcon;
  }

  public Icon getDisconnectedIcon() {
    return disconnectedIcon;
  }
  public Icon getDisconnectedWithNewIcon() {
    return disconnectedWithNewIcon;
  }

  public Icon getSubfolderWithNewIcon() {
    return subfolderWithNewIcon;
  }

  public void setSubfolderWithNewIcon(Icon newIcon) {
    subfolderWithNewIcon = newIcon;
  }

  public void setDisconnectedWithNewIcon(Icon newIcon) {
    disconnectedWithNewIcon = newIcon;
  }

  public Icon getClosedFolderIcon() {
    return closedFolderIcon;
  }

  public void setClosedFolderIcon(Icon newIcon) {
    closedFolderIcon = newIcon;
  }

  public Icon getUnavailableIcon() {
    return unavailableIcon;
  }

  public void setUnavailableIcon(Icon newIcon) {
    unavailableIcon = newIcon;
  }

  public Icon getConnectedStoreIcon() {
    return connectedStoreIcon;
  }

  public void setConnectedStoreIcon(Icon newIcon) {
    connectedStoreIcon = newIcon;
  }

  public Icon getDisconnectedStoreIcon() {
    return disconnectedStoreIcon;
  }

  public void setDisconnectedStoreIcon(Icon newIcon) {
    disconnectedStoreIcon = newIcon;
  }

  public Icon getSubfolderIcon() {
    return subfolderIcon;
  }

  public void setSubfolderIcon(Icon newIcon) {
    subfolderIcon = newIcon;
  }

  public Icon getSubfolderClosedIcon() {
    return subfolderClosedIcon;
  }

  public void setSubfolderClosedIcon(Icon newIcon) {
    subfolderClosedIcon = newIcon;
  }

  public Icon getRootIcon() {
    return rootIcon;
  }

  public void setRootIcon(Icon newIcon) {
    rootIcon = newIcon;
  }
}


