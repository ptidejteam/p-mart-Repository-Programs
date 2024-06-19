package net.suberic.pooka.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JComponent;
import javax.mail.*;
import net.suberic.pooka.OperationCancelledException;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.FolderInfo;
import net.suberic.pooka.StoreInfo;
import net.suberic.util.thread.ActionWrapper;
import net.suberic.pooka.gui.search.*;
import net.suberic.pooka.gui.filechooser.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.Cursor;
import javax.mail.event.*;

/**
 * The display in the FolderPanel for a Store.
 */
public class StoreNode extends MailTreeNode {

  protected StoreInfo store = null;
  protected String displayName = null;
  protected boolean hasLoaded = false;

  public StoreNode(StoreInfo newStore, JComponent parent) {
    super(newStore, parent);
    store = newStore;
    newStore.setStoreNode(this);
    displayName=Pooka.getProperty("Store." + store.getStoreID() + ".displayName", store.getStoreID());
    setCommands();
    loadChildren();
    defaultActions = new Action[] {
      new ActionWrapper(new OpenAction(), getStoreInfo().getStoreThread()),
      new ActionWrapper(new SubscribeAction(), getStoreInfo().getStoreThread()),
      new TestAction(),
      new NewFolderAction(),
      new ActionWrapper(new DisconnectAction(), getStoreInfo().getStoreThread()),
      new EditAction(),
      new StatusAction()
    };

  }

  /**
   * this method returns false--a store is never a leaf.
   */
  public boolean isLeaf() {
    return false;
  }


  /**
   * This loads or updates the top-level children of the Store.
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
    Logger logger = Logger.getLogger("Store." + getStoreInfo().getStoreID());
    logger.fine("calling loadChildren() for " + getStoreInfo().getStoreID());

    Enumeration origChildren = super.children();
    Vector origChildrenVector = new Vector();
    while (origChildren.hasMoreElements())
      origChildrenVector.add(origChildren.nextElement());

    boolean changed = false;

    logger.fine(getStoreInfo().getStoreID() + ":  origChildrenVector.size() = " + origChildrenVector.size());

    Vector storeChildren = getStoreInfo().getChildren();

    logger.fine(getStoreInfo().getStoreID() + ":  storeChildren.size() = " + storeChildren.size());

    if (storeChildren != null) {
      for (int i = 0; i < storeChildren.size(); i++) {
        FolderNode node = popChild(((FolderInfo)storeChildren.elementAt(i)).getFolderName(), origChildrenVector);
        if (node == null) {
          node = new FolderNode((FolderInfo)storeChildren.elementAt(i), getParentContainer());
          // we used insert here, since add() would mak
          // another recursive call to getChildCount();
          insert(node, 0);
          changed = true;
        }
      }

    }

    if (origChildrenVector.size() > 0) {
      removeChildren(origChildrenVector);
      changed = true;
    }

    hasLoaded=true;

    javax.swing.JTree folderTree = ((FolderPanel)getParentContainer()).getFolderTree();
    if (changed && folderTree != null && folderTree.getModel() instanceof javax.swing.tree.DefaultTreeModel) {
      ((javax.swing.tree.DefaultTreeModel)folderTree.getModel()).nodeStructureChanged(this);
    }

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
   * This  creates the current PopupMenu if there is not one.  It then
   * will configure the PopupMenu with the current actions.
   *
   * Overrides MailTreeNode.configurePopupMenu();
   */

  public void configurePopupMenu() {
    if (popupMenu == null) {
      popupMenu = new net.suberic.util.gui.ConfigurablePopupMenu();
      popupMenu.configureComponent("StoreNode.popupMenu", Pooka.getResources());
      updatePopupTheme();
    }

    popupMenu.setActive(getActions());

  }

  /**
   * This opens up a dialog asking if the user wants to subscribe to a
   * subfolder.
   */
  public void newFolder() {
    String message = Pooka.getProperty("Folder.newFolder", "Subscribe/create new subfolder of") + " " + getStoreInfo().getStoreID();

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
      getStoreInfo().getStoreThread().addToQueue(new javax.swing.AbstractAction() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
              getStoreInfo().createSubFolder(response, finalType);
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

  /**
   * Sets a busy cursor on this node.
   */
  public void setBusy(boolean newBusy) {
    final boolean newValue = newBusy;

    Runnable runMe = new Runnable() {
        public void run() {
          if (newValue)
            getParentContainer().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          else
            getParentContainer().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      };

    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else
      SwingUtilities.invokeLater(runMe);

  }

  /**
   * Returns the StoreID of this node's folder, or null if no StoreInfo
   * is set.
   */
  public String getStoreID() {
    if (store != null)
      return store.getStoreID();
    else
      return null;
  }

  /**
   * Returns the StoreInfo of this node's folder, or null if no StoreInfo
   * is set.
   */
  public StoreInfo getStoreInfo() {
    return store;
  }

  /**
   * We override toString() so we can display the store URLName
   * without the password.
   */
  public String toString() {
    return displayName;
  }

  public boolean isConnected() {
    if (store != null) {
      return store.isConnected();
    } else
      return false;
  }

  public Action[] defaultActions;

  public Action[] getDefaultActions() {
    return defaultActions;
  }

  class OpenAction extends AbstractAction {

    OpenAction() {
      super("file-open");
      this.putValue(Action.SHORT_DESCRIPTION, "file-open on Store " + getStoreID());
    }

    OpenAction(String nm) {
      super(nm);
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      if (!store.isConnected()) {
        try {
          store.connectStore();

          SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                javax.swing.JTree folderTree = ((FolderPanel)getParentContainer()).getFolderTree();
                folderTree.expandPath(folderTree.getSelectionPath());
              }
            });

        } catch (OperationCancelledException oce) {
          // ignore.
        } catch (MessagingException me) {
          final MessagingException newMe = me;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  Pooka.getUIFactory().showError(Pooka.getResources().formatMessage("error.Store.connecton.failed", getStoreID()), newMe);
                }
              });
        }
      }
    }
  }

  class SubscribeAction extends AbstractAction {

    SubscribeAction() {
      super("folder-subscribe");

    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            Pooka.getUIFactory().showStatusMessage("Connecting to " + getStoreInfo().getStoreID() + " to get list of folders...");
            setBusy(true);
          }
        });

      // this is happening on the store thread.
      final MailFileSystemView mfsv = new MailFileSystemView(getStoreInfo());

      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            final Logger storeLogger = Logger.getLogger("Store." + getStoreInfo().getStoreID());
            final Logger guiLogger = Logger.getLogger("Pooka.debug.gui.filechooser");

            try {
              JFileChooser jfc = new JFileChooser(getStoreInfo().getStoreID(), mfsv);
              jfc.setMultiSelectionEnabled(true);
              jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
              int returnValue = jfc.showDialog(getParentContainer(), Pooka.getProperty("FolderEditorPane.Select", "Select"));

              if (returnValue == JFileChooser.APPROVE_OPTION) {
                guiLogger.fine("got " + jfc.getSelectedFile() + " as a return value.");

                final java.io.File[] selectedFiles = jfc.getSelectedFiles();

                getStoreInfo().getStoreThread().addToQueue(new javax.swing.AbstractAction() {
                    public void actionPerformed(java.awt.event.ActionEvent ae) {
                      for (int i = 0 ; selectedFiles != null && i < selectedFiles.length; i++) {
                        net.suberic.pooka.gui.filechooser.FolderFileWrapper wrapper = (net.suberic.pooka.gui.filechooser.FolderFileWrapper) selectedFiles[i];
                        try {
                          // if it doesn't exist, try to create it.
                          if (! wrapper.exists()) {
                            wrapper.getFolder().create(Folder.HOLDS_MESSAGES);
                          }
                          String absFileName = wrapper.getAbsolutePath();
                          guiLogger.fine("absFileName=" + absFileName);
                          int firstSlash = absFileName.indexOf('/');
                          String normalizedFileName = absFileName;
                          if (firstSlash >= 0)
                            normalizedFileName = absFileName.substring(firstSlash);

                          guiLogger.fine("adding folder " + normalizedFileName + "; absFileName = " + absFileName);
                          storeLogger.fine("adding folder " + normalizedFileName);

                          getStoreInfo().subscribeFolder(normalizedFileName);
                        } catch (MessagingException me) {
                          final String folderName = wrapper.getName();
                          SwingUtilities.invokeLater(new Runnable() {
                              public void run() {
                                Pooka.getUIFactory().showError(Pooka.getProperty("error.creatingFolder", "Error creating folder ") + folderName);
                              }
                            });
                        }
                      }
                    }
                  },  new java.awt.event.ActionEvent(this, 0, "folder-subscribe"));
              }
            } catch (Exception e) {
              Pooka.getUIFactory().showError(Pooka.getProperty("error.subscribingFolder", "Error subscribing to folder."));

            }

            Pooka.getUIFactory().clearStatus();
            setBusy(false);
          }
        });
    }
  }

  class TestAction extends AbstractAction {

    TestAction() {
      super("file-test");
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {

    }

  }

  class DisconnectAction extends AbstractAction {

    DisconnectAction() {
      super("file-close");
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      try {
        getStoreInfo().disconnectStore();
      } catch (Exception ex) {
        System.out.println("caught exception:  " + ex.getMessage());
      }
    }
  }

  class NewFolderAction extends AbstractAction {

    NewFolderAction() {
      super("folder-new");
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      newFolder();
    }

  }

  class EditAction extends AbstractAction {

    EditAction() {
      super("file-edit");
    }

    EditAction(String nm) {
      super(nm);
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      Pooka.getUIFactory().showEditorWindow(getStoreInfo().getStoreProperty(), getStoreInfo().getStoreProperty(), "Store.editor");
    }
  }

  class StatusAction extends AbstractAction {

    StatusAction() {
      super("store-status");
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      getStoreInfo().showStatus();
    }
  }
}

