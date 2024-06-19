package net.suberic.pooka.gui;
import javax.swing.*;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import javax.swing.tree.*;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.MessagingException;
import java.util.*;
import net.suberic.pooka.*;
import net.suberic.util.thread.*;
import javax.mail.FolderNotFoundException;
import net.suberic.pooka.FolderInfo;
import javax.mail.event.*;

public class FolderNode extends MailTreeNode implements MessageChangedListener, UserProfileContainer, ConnectionListener {

  protected FolderInfo folderInfo = null;
  protected boolean hasLoaded = false;

  /**
   * creates a tree node that points to a folder
   *
   * @param newFolderthe store for this node
   * @param newParent the parent component
   */
  public FolderNode(FolderInfo newFolderInfo, JComponent newParent) {
    super(newFolderInfo, newParent);
    folderInfo = newFolderInfo;

    folderInfo.setFolderNode(this);

    commands = new Hashtable();

    defaultActions = new Action[] {
      new ActionWrapper(new OpenAction(), folderInfo.getFolderThread()),
      new ActionWrapper(new ReconnectAction(), folderInfo.getFolderThread()),
      new ActionWrapper(new CloseAction(), folderInfo.getFolderThread()),
      new UnsubscribeAction(),
      new NewFolderAction(),
      new DeleteAction()
    };

    Action[] actions = defaultActions;

    if (actions != null) {
      for (int i = 0; i < actions.length; i++) {
        Action a = actions[i];
        commands.put(a.getValue(Action.NAME), a);
      }
    }

    folderInfo.addMessageCountListener(new MessageCountAdapter() {
        public void messagesAdded(MessageCountEvent e) {
          if ( folderInfo.notifyNewMessagesMain()) {
            Pooka.getUIFactory().getMessageNotificationManager().notifyNewMessagesReceived(e, getFolderInfo().getFolderID());
          }
          updateNode();
        }

        public void messagesRemoved(MessageCountEvent e) {
          updateNode();

        }
      });

    folderInfo.addMessageChangedListener(this);
    folderInfo.addConnectionListener(this);
    loadChildren();

  }


  /**
   * a Folder is a leaf if it cannot contain sub folders
   */
  public boolean isLeaf() {
    if (getChildCount() < 1)
      return true;
    else
      return false;
  }

  /**
   * returns the folder for this node
   */
  public Folder getFolder() {
    return folderInfo.getFolder();
  }

  /**
   * This loads (or reloads) the children of the FolderNode from
   * the list of Children on the FolderInfo.
   *
   * Runs on the event dispatch thread, but is safe to be called from anywhere.
   */
  public void loadChildren() {
    Runnable runMe = new Runnable() {
        public void run() {
          doLoadChildren();
        }
      };

    if (SwingUtilities.isEventDispatchThread())
      doLoadChildren();
    else {
      try {
        SwingUtilities.invokeAndWait(runMe);
      } catch (Exception ie) {
      }
    }
  }

  /**
   * Does the actual work for loading the children.  performed on the swing
   * gui thread.
   */
  private void doLoadChildren() {
    Enumeration origChildren = children();
    Vector origChildrenVector = new Vector();
    while (origChildren.hasMoreElements()) {
      Object origChild = origChildren.nextElement();
      origChildrenVector.add(origChild);
    }
    Vector folderChildren = getFolderInfo().getChildren();

    boolean changed=false;

    if (folderChildren != null) {
      for (int i = 0; i < folderChildren.size(); i++) {
        FolderNode node = popChild(((FolderInfo)folderChildren.elementAt(i)).getFolderName(), origChildrenVector);
        if (node == null) {
          node = new FolderNode((FolderInfo)folderChildren.elementAt(i), getParentContainer());
          // we used insert here, since add() would mak
          // another recursive call to getChildCount();
          insert(node, 0);
        }
      }

    }

    if (origChildrenVector.size() > 0) {
      removeChildren(origChildrenVector);
    }

    hasLoaded=true;

  }

  /**
   * This goes through the Vector of FolderNodes provided and
   * returns the FolderNode for the given childName, if one exists.
   * It will also remove the Found FolderNode from the childrenList
   * Vector.
   *
   * If a FolderNode that corresponds with the given childName does
   * not exist, this returns null.
   *
   */
  public FolderNode popChild(String childName, Vector childrenList) {
    if (children != null) {
      for (int i = 0; i < childrenList.size(); i++)
        if (((FolderNode)childrenList.elementAt(i)).getFolderInfo().getFolderName().equals(childName)) {
          FolderNode fn = (FolderNode)childrenList.elementAt(i);
          childrenList.remove(fn);
          return fn;
        }
    }

    // no match.
    return null;
  }

  /**
   * This  creates the current PopupMenu if there is not one.  It then
   * will configure the PopupMenu with the current actions.
   *
   * Overrides MailTreeNode.configurePopupMenu();
   */

  public void configurePopupMenu() {
    if (popupMenu == null) {
      popupMenu = new net.suberic.util.gui.ConfigurablePopupMenu();
      if (getFolderInfo().isTrashFolder())
        popupMenu.configureComponent("TrashFolderNode.popupMenu", Pooka.getResources());
      else if (getFolderInfo().isOutboxFolder())
        popupMenu.configureComponent("OutboxFolderNode.popupMenu", Pooka.getResources());
      else if (getFolderInfo() instanceof net.suberic.pooka.cache.CachingFolderInfo && ! ((net.suberic.pooka.cache.CachingFolderInfo) getFolderInfo()).getCacheHeadersOnly())
        popupMenu.configureComponent("CachingFolderNode.popupMenu", Pooka.getResources());
      else
        popupMenu.configureComponent("FolderNode.popupMenu", Pooka.getResources());

      updatePopupTheme();

    }

    popupMenu.setActive(getActions());
  }

  /**
   * This removes all the items in removeList from the list of this
   * node's children.
   */
  public void removeChildren(Vector removeList) {
    for (int i = 0; i < removeList.size(); i++) {
      if (removeList.elementAt(i) instanceof javax.swing.tree.MutableTreeNode)
        this.remove((javax.swing.tree.MutableTreeNode)removeList.elementAt(i));
    }
  }

  /**
   * This makes the FolderNode visible in its parent JTree.
   */
  public void makeVisible() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          javax.swing.JTree folderTree = ((FolderPanel)getParentContainer()).getFolderTree();
          TreeNode[] nodeList = ((DefaultTreeModel)folderTree.getModel()).getPathToRoot(FolderNode.this);
          TreePath path = new TreePath(nodeList);
          folderTree.makeVisible(path);
        }
      });
  }

  public void messageChanged(MessageChangedEvent mce) {
    updateNode();
  }

  public void closed(ConnectionEvent e) {
    updateNode();
  }

  public void opened(ConnectionEvent e) {
    updateNode();
  }

  public void disconnected(ConnectionEvent e) {
    updateNode();
  }


  int lastFolderStatus = -1;
  boolean lastUnread = false;
  boolean lastNewMessages = false;

  /**
   * Checks to see if the Folder's status has changed from the last time
   * we redrew this node.  If it has, then we redraw the node.
   */
  public void updateNode() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          FolderInfo fi = getFolderInfo();
          if (fi != null) {
            int currentStatus = fi.getStatus();
            boolean hasUnread = fi.hasUnread();
            boolean hasNewMessages = fi.hasNewMessages();

            lastFolderStatus = currentStatus;
            lastUnread = hasUnread;
            lastNewMessages = hasNewMessages;
            javax.swing.JTree folderTree = ((FolderPanel)getParentContainer()).getFolderTree();
            ((DefaultTreeModel)folderTree.getModel()).nodeChanged(FolderNode.this);
          }
        }
      });
  }

  /**
   * This opens up a dialog asking if the user wants to unsubsribe to
   * the current Folder.  If the user chooses 'yes', then
   * getFolderInfo().unsubscribe() is called.
   */
  public void unsubscribeFolder() {
    String message;
    if (isLeaf())
      message = Pooka.getProperty("Folder.unsubscribeConfirm", "Do you really want to unsubscribe from the following folder?");
    else
      message = Pooka.getProperty("Folder.unsubscribeConfirm.notLeaf", "Do you really want to unsubscribe from \nthis folder and all its children?");

    int response = Pooka.getUIFactory().showConfirmDialog(message + "\n" + getFolderInfo().getFolderName(), Pooka.getProperty("Folder.unsubscribeConfirm.title", "Unsubscribe from Folder"), JOptionPane.YES_NO_OPTION);

    if (response == JOptionPane.YES_OPTION) {
      getFolderInfo().getFolderThread().addToQueue(new javax.swing.AbstractAction() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            getFolderInfo().unsubscribe();
          }
        } , new java.awt.event.ActionEvent(this, 0, "folder-unsubscribe"));
    }
  }

  /**
   * This opens up a dialog asking if the user wants to delete
   * the current Folder.  If the user chooses 'yes', then
   * getFolderInfo().delete() is called.
   */
  public void deleteFolder() {
    String message;
    if (isLeaf())
      message = Pooka.getProperty("Folder.deleteConfirm", "Do you really want to delete from the following folder?");
    else
      message = Pooka.getProperty("Folder.deleteConfirm.notLeaf", "Do you really want to delete \nthis folder and all its children?");

    int response = Pooka.getUIFactory().showConfirmDialog(message + "\n" + getFolderInfo().getFolderName(), Pooka.getProperty("Folder.deleteConfirm.title", "Delete Folder"), JOptionPane.YES_NO_OPTION);

    if (response == JOptionPane.YES_OPTION) {
      message = Pooka.getProperty("Folder.deleteConfirm.secondMessage", "Are you sure?  This will permanently remove the folder and all of its messages");
      int responseTwo = Pooka.getUIFactory().showConfirmDialog(message, Pooka.getProperty("Folder.deleteConfirm.secondMessage.title", "Are you sure?"), JOptionPane.YES_NO_OPTION);
      if (responseTwo == JOptionPane.YES_OPTION) {

        getFolderInfo().getFolderThread().addToQueue(new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                getFolderInfo().delete();
              } catch (OperationCancelledException oce) {
                // don't show a message if we cancel.
              } catch(MessagingException me) {
                final Exception fme = me;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      Pooka.getUIFactory().showError("Error deleting folder:  ", fme);
                    }
                  });
              }
            }
          } , new java.awt.event.ActionEvent(this, 0, "folder-delete"));
      }
    }
  }

  /**
   * This opens up a dialog asking if the user wants to subscribe to a
   * subfolder.
   */
  public void newFolder() {
    if ((getFolderInfo().getType() & Folder.HOLDS_FOLDERS) != 0) {
      String message = Pooka.getProperty("Folder.newFolder", "Subscribe/create new subfolder of") + " " + getFolderInfo().getFolderName();

      JLabel messageLabel = new JLabel(message);

      JPanel typePanel = new JPanel();
      typePanel.setBorder(BorderFactory.createEtchedBorder());

      JRadioButton messagesButton = new JRadioButton(Pooka.getProperty("Folder.new.messages.label", "Contains Messages"), true);
      JRadioButton foldersButton = new JRadioButton(Pooka.getProperty("Folder.new.folders.label", "Contains Folders"));

      ButtonGroup bg = new ButtonGroup();
      bg.add(messagesButton);
      bg.add(foldersButton);

      typePanel.add(messagesButton);
      typePanel.add(foldersButton);

      Object[] inputPanels = new Object[] {
        messageLabel,
        typePanel
      };

      final String response = Pooka.getUIFactory().showInputDialog(inputPanels, Pooka.getProperty("Folder.new.title", "Create new Folder"));

      int type = javax.mail.Folder.HOLDS_MESSAGES;
      if (foldersButton.isSelected()) {
        type = javax.mail.Folder.HOLDS_FOLDERS;
      }

      final int finalType = type;

      if (response != null && response.length() > 0) {
        getFolderInfo().getFolderThread().addToQueue(new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
              try {
                getFolderInfo().createSubFolder(response, finalType);
              } catch (OperationCancelledException oce) {
                // don't show a message if we cancel.
              } catch (MessagingException me) {
                final Exception fme = me;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      Pooka.getUIFactory().showError(fme.getMessage());
                    }
                  });

                me.printStackTrace();
              }
            }
          } , new java.awt.event.ActionEvent(this, 0, "folder-new"));
      }
    }
  }

  /**
   * Opens the folder.
   */
  public void openFolder(boolean pReconnect) {
    openFolder(pReconnect, true);
  }

  /**
   * Opens the folder.
   */
  public void openFolder(boolean pReconnect, boolean pSelectFolder) {
    try {
      if (pReconnect) {
        // in case we've not connected in the past, set the store to open.
        StoreInfo si = getFolderInfo().getParentStore();
        si.setPreferredStatus(FolderInfo.CONNECTED);
      }

      getFolderInfo().loadAllMessages();

      if (! getFolderInfo().isSortaOpen() || (pReconnect && ! getFolderInfo().isConnected())) {
        getFolderInfo().openFolder(javax.mail.Folder.READ_WRITE, pReconnect);
      }

      int firstUnread = -1;
      int messageCount = -1;

      final int folderType = getFolderInfo().getType();

      if (getFolderInfo().isSortaOpen() && (folderType & Folder.HOLDS_MESSAGES) != 0 && getFolderInfo().getFolderDisplayUI() == null) {
        firstUnread = getFolderInfo().getFirstUnreadMessage();
        messageCount = getFolderInfo().getMessageCount();
      }

      final int finalFirstUnread = firstUnread;
      final int finalMessageCount = messageCount;
      final boolean fSelectFolder = pSelectFolder;

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {

            if ((folderType & Folder.HOLDS_MESSAGES) != 0) {
              if (getFolderInfo().getFolderDisplayUI() != null) {
                getFolderInfo().getFolderDisplayUI().openFolderDisplay(fSelectFolder);
              } else {
                getFolderInfo().setFolderDisplayUI(Pooka.getUIFactory().createFolderDisplayUI(getFolderInfo()));
                if (Pooka.getProperty("Pooka.autoSelectFirstUnread", "true").equalsIgnoreCase("true")) {
                  if (finalFirstUnread >= 0)
                    getFolderInfo().getFolderDisplayUI().selectMessage(finalFirstUnread);
                  else
                    getFolderInfo().getFolderDisplayUI().selectMessage(finalMessageCount);
                } else {
                  if (finalFirstUnread >= 0)
                    getFolderInfo().getFolderDisplayUI().makeSelectionVisible(finalFirstUnread);
                  else
                    getFolderInfo().getFolderDisplayUI().makeSelectionVisible(finalMessageCount);

                }
                getFolderInfo().getFolderDisplayUI().openFolderDisplay(fSelectFolder);
              }

            }
            if ((folderType & Folder.HOLDS_FOLDERS) != 0) {
              javax.swing.JTree folderTree = ((FolderPanel)getParentContainer()).getFolderTree();
              folderTree.expandPath(folderTree.getSelectionPath());
            }
          }
        });
    } catch (OperationCancelledException oce) {
      // if we cancelled out, ignore.
    } catch (MessagingException me) {
      final MessagingException newMe = me;
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            Pooka.getUIFactory().showError(Pooka.getProperty("error.Folder.openFailed", "Failed to open folder") + " " + getFolderInfo().getFolderID(), newMe);
          }
        });
    }

  }

  /**
   * Returns the FolderID of the FolderInfo that's defining this FolderNode.
   */
  public String getFolderID() {
    return getFolderInfo().getFolderID();
  }

  /**
   * Returns the FolderInfo that's defining this FolderNode.
   */
  public FolderInfo getFolderInfo() {
    return folderInfo;
  }

  /**
   * override toString() since we only want to display a folder's
   * name, and not the full path of the folder
   */
  public String toString() {
    if (getFolderInfo() != null) {
      String folderName = getFolderInfo().getFolderName();
      if (getFolderInfo().hasUnread()) {
        return folderName + " (" + getFolderInfo().getUnreadCount() + ")";
      } else {
        return folderName;
      }
    } else {
      return "no folder name";
    }
  }


  //As specified by interface net.suberic.pooka.UserProfileContainer
  public UserProfile getDefaultProfile() {
    if (getFolderInfo() != null)
      return getFolderInfo().getDefaultProfile();
    else
      return null;
  }

  public Action[] getActions() {
    if (getFolderInfo().getActions() != null)
      return javax.swing.text.TextAction.augmentList(getFolderInfo().getActions(), defaultActions);
    else
      return defaultActions;
  }

  public Action[] defaultActions;

  class OpenAction extends AbstractAction {

    OpenAction() {
      super("file-open");
    }

    OpenAction(String nm) {
      super(nm);
    }

    public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ((FolderPanel)getParentContainer()).getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          }
        });

      openFolder(false);

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ((FolderPanel)getParentContainer()).getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        });
    }

  }

  class ReconnectAction extends AbstractAction {

    ReconnectAction() {
      super("folder-connect");
    }

    ReconnectAction(String nm) {
      super(nm);
    }

    public void actionPerformed(ActionEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ((FolderPanel)getParentContainer()).getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          }
        });

      openFolder(true);

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ((FolderPanel)getParentContainer()).getMainPanel().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        });
    }

  }

  class UnsubscribeAction extends AbstractAction {

    UnsubscribeAction() {
      super("folder-unsubscribe");
    }

    public void actionPerformed(ActionEvent e) {
      unsubscribeFolder();
    }

  }

  class DeleteAction extends AbstractAction {

    DeleteAction() {
      super("folder-delete");
    }

    public void actionPerformed(ActionEvent e) {
      deleteFolder();
    }

  }

  class NewFolderAction extends AbstractAction {

    NewFolderAction() {
      super("folder-new");
    }

    public void actionPerformed(ActionEvent e) {
      newFolder();
    }

  }

  class CloseAction extends AbstractAction {

    CloseAction() {
      super("folder-close");
    }

    public void actionPerformed(ActionEvent e) {
      try {
        getFolderInfo().closeFolder(false);
      } catch (Exception ex) {
        System.out.println("caught exception:  " + ex.getMessage());
      }
    }

  }
}

