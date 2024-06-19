package net.suberic.pooka.gui;
import net.suberic.pooka.*;
import net.suberic.util.*;
import net.suberic.util.swing.*;
import net.suberic.util.gui.ConfigurableKeyBinding;
import net.suberic.util.gui.ConfigurableToolbar;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.mail.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.swing.text.TextAction;
import javax.swing.plaf.metal.MetalTheme;
import net.suberic.pooka.gui.*;
import java.util.Vector;
import java.util.LinkedList;


/**
 * This class displays the Stores and Folders for Pooka.
 */
public class FolderPanel extends JPanel implements ItemListChangeListener, UserProfileContainer, ThemeSupporter, ThemeListener {
  MainPanel mainPanel=null;
  JTree folderTree;
  DefaultTreeModel folderModel;
  Session session;
  ConfigurableKeyBinding keyBindings;
  ConfigurableToolbar folderPanelToolbar;
  MetalTheme currentTheme = null;
  DropTarget mDropTarget = null;
  TransferHandler mTransferHandler = null;

  private Action[] defaultActions = {
    new NewMessageAction()
  };

  /**
   * This creates and configures a new FolderPanel.
   */
  public FolderPanel(MainPanel newMainPanel) {

    this.setLayout(new BorderLayout());

    mainPanel=newMainPanel;

    setPreferredSize(new Dimension(Integer.parseInt(Pooka.getProperty("Pooka.folderPanel.hsize", "200")), Integer.parseInt(Pooka.getProperty("Pooka.folderPanel.vsize", Pooka.getProperty("Pooka.vsize","570")))));

    folderModel = new DefaultTreeModel(createTreeRoot());
    folderTree = new JTree(folderModel);

    JScrollPane jsp = new JScrollPane(folderTree);
    //this.getViewport().add(folderTree);

    folderPanelToolbar = Pooka.getUIFactory().createFolderPanelToolbar();
    //new ConfigurableToolbar("FolderToolbar", Pooka.getResources());
    if (folderPanelToolbar != null) {
      folderPanelToolbar.setActive(getActions());
      this.add("North", folderPanelToolbar);
    }

    this.add("Center", jsp);

    folderTree.addMouseListener(new MouseAdapter() {

        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            MailTreeNode tmpNode = getSelectedNode();
            if (tmpNode != null) {
              String actionCommand = Pooka.getProperty("FolderPanel.2xClickAction", "file-open");
              Action clickAction = getSelectedNode().getAction(actionCommand);
              if (clickAction != null ) {
                clickAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand));
              }

            }
          }
        }

        public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            TreePath path = folderTree.getClosestPathForLocation(e.getX(), e.getY());
            if (folderTree.getPathBounds(path).contains(e.getX(), e.getY())) {
              // this means that we're clicking on a node.  make
              // sure that it's selected.

              if (!folderTree.isPathSelected(path))
                folderTree.setSelectionPath(path);
            }

            MailTreeNode tmpNode = getSelectedNode();
            if (tmpNode != null) {
              tmpNode.showPopupMenu(folderTree, e);

            }
          }
        }

        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
            // see if anything is selected
            TreePath path = folderTree.getClosestPathForLocation(e.getX(), e.getY());
            if (folderTree.getPathBounds(path).contains(e.getX(), e.getY())) {
              // this means that we're clicking on a node.  make
              // sure that it's selected.

              if (!folderTree.isPathSelected(path))
                folderTree.setSelectionPath(path);
            }

            MailTreeNode tmpNode = getSelectedNode();
            if (tmpNode != null) {
              tmpNode.showPopupMenu(folderTree, e);

            }
          }
        }
      });

    folderTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
          getMainPanel().refreshActiveMenus();
          getMainPanel().refreshCurrentUser();
          refreshActiveMenus();
          keyBindings.setActive(getActions());
        }
      });

    folderTree.setCellRenderer(new EnhancedFolderTreeCellRenderer());

    mDropTarget = new FolderTreeDropTarget();
    mTransferHandler = new net.suberic.pooka.gui.dnd.FolderTransferHandler();
    folderTree.setDropTarget(mDropTarget);

    folderTree.setTransferHandler(mTransferHandler);

    keyBindings = new ConfigurableKeyBinding(this, "FolderPanel.keyBindings", Pooka.getResources());
    keyBindings.setActive(getActions());

    // if the FolderPanel itself ever gets focus, pass it on to the
    // folderTree.
    this.addFocusListener(new FocusAdapter() {
        public void focusGained(FocusEvent e) {
          folderTree.requestFocusInWindow();
        }
      });

    Pooka.getHelpBroker().enableHelpKey(this, "ui.folderPanel", Pooka.getHelpBroker().getHelpSet());

    this.configureInterfaceStyle();

  }

  /**
   * Configures the interfaceStyle for this Pane.
   */
  public void configureInterfaceStyle() {
    configureInterfaceStyle(false);
  }

  private void configureInterfaceStyle(boolean pForce) {
    final boolean force = pForce;
    Runnable runMe = new Runnable() {
        public void run() {
          try {
            Pooka.getUIFactory().getPookaThemeManager().updateUI(FolderPanel.this, FolderPanel.this, force);
            LinkedList allNodes = getAllNodes();
            for (int i = 0; i < allNodes.size(); i++) {
              Object currentNode = allNodes.get(i);
              if (currentNode instanceof MailTreeNode) {
                ((MailTreeNode) currentNode).updatePopupTheme();
              }
            }
          } catch (Exception e) {

          }
        }
      };

    if (! SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(runMe);
    } else {
      runMe.run();
    }

  }

  /**
   * Gets the Theme object from the ThemeManager which is appropriate
   * for this UI.
   */
  public MetalTheme getTheme(ThemeManager tm) {
    String id = Pooka.getProperty("Pooka.folderPanel.theme", "");
    if (id != null && ! id.equals("")) {
      return tm.getTheme(id);
    }

    return tm.getDefaultTheme();
  }

  /**
   * Gets the currently configured Theme.
   */
  public MetalTheme getCurrentTheme() {
    return currentTheme;
  }

  /**
   * Sets the Theme that this component is currently using.
   */
  public void setCurrentTheme(MetalTheme newTheme) {
    if (currentTheme != null && currentTheme instanceof ConfigurableMetalTheme) {
      ((ConfigurableMetalTheme)currentTheme).removeThemeListener(this);
    }
    currentTheme = newTheme;

    if (currentTheme != null && currentTheme instanceof ConfigurableMetalTheme) {
      ((ConfigurableMetalTheme)currentTheme).addThemeListener(this);
    }
  }

  /**
   * Called when the specifics of a Theme change.
   */
  public void themeChanged(ConfigurableMetalTheme theme) {
    // we should really only be getting messages from our own current themes,
    // but, hey, it never hurts to check.
    if (currentTheme != null && currentTheme == theme) {
      SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            configureInterfaceStyle(true);
          }
        });
    }
  }

  /**
   * This returns the currently highlighted node on the FolderTree.
   */
  public MailTreeNode getSelectedNode() {
    TreePath tp = folderTree.getSelectionPath();

    if (tp != null) {
      return (MailTreeNode)tp.getLastPathComponent();
    } else {
      return null;
    }
  }

  /**
   * This creates the tree root from the StoreList of the StoreManager.
   */
  private MailTreeNode createTreeRoot() {
    MailTreeNode root = new MailTreeNode("Pooka", this);

    synchronized(root) {
      // Get the stores we have listed.
      String storeID = null;

      Vector allStoreInfos = Pooka.getStoreManager().getStoreList();
      for (int i = 0; i < allStoreInfos.size(); i++) {
        StoreNode storenode = new StoreNode((StoreInfo)allStoreInfos.elementAt(i), this);
        root.add(storenode);
      }

      Pooka.getStoreManager().addItemListChangeListener(this);
    }
    return root;
  }

  /**
   * refreshStores(e) goes through the list of registered stores and
   * compares these to the value of the "Store" property.  If any
   * stores are no longer listed in that property, they are removed
   * from the FolderPanel.  If any new stores are found, they are
   * added to the FolderPanel.
   *
   * This function does not add new subfolders to already existing
   * Stores.  Use refreshStore(Store) for that.
   *
   * This function is usually called in response to a ValueChanged
   * action on the "Store" property.
   */
  public void refreshStores(ItemListChangeEvent ilce) {
    final ItemListChangeEvent e = ilce;
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          Item[] removed = e.getRemoved();
          Item[] added = e.getAdded();
          MailTreeNode root = (MailTreeNode)getFolderTree().getModel().getRoot();
          synchronized(root) {
            if (removed != null && removed.length > 0) {
              int[] removedIndices = new int[removed.length];
              Object[] removedObjects = new Object[removed.length];
              for (int i = 0; removed != null && i < removed.length; i++) {

                StoreInfo currentStore = (StoreInfo) removed[i];

                if (currentStore != null) {
                  StoreNode sn = currentStore.getStoreNode();
                  if (sn != null) {
                    removedObjects[i] = sn;
                    removedIndices[i] = root.getIndex(sn);
                    root.remove(sn);
                  }
                }
              }
              java.util.Arrays.sort(removedIndices);
              ((DefaultTreeModel)getFolderTree().getModel()).nodesWereRemoved(root, removedIndices, removedObjects);
            }

            if (added != null && added.length > 0) {
              int[] addedIndices = new int[added.length];
              for (int i = 0; added != null && i < added.length ; i++) {
                StoreNode storenode = new StoreNode((StoreInfo)added[i], FolderPanel.this);
                root.add(storenode);
                addedIndices[i] = root.getIndex(storenode);
                //addStore((StoreInfo)added[i] , root);
              }
              ((DefaultTreeModel)getFolderTree().getModel()).nodesWereInserted(root, addedIndices);
            }

            currentTheme = null;
            configureInterfaceStyle();
          }
        }
      });
  }


  /**
   * This creates a new StoreNode from the StoreInfo, and adds that
   * StoreNode to the root of the FolderTree.
   */
  /*
  public void addStore(StoreInfo store, MailTreeNode root) {
    synchronzied(root) {
      StoreNode storenode = new StoreNode(store, this);
      root.add(storenode);
    }
  }
  */

  public MainPanel getMainPanel() {
    return mainPanel;
  }

  public JTree getFolderTree() {
    return folderTree;
  }

  /**
   * Specified by interface net.suberic.util.ItemListChangeListener
   *
   */
  public void itemListChanged(ItemListChangeEvent e) {
    refreshStores(e);
  }

  /**
   * Gets all of the children of the tree.
   */
  public LinkedList getAllNodes() {
    Object root = folderModel.getRoot();
    return parseTree(folderModel, root);
  }

  /**
   * parses the tree.
   */
  public LinkedList parseTree(TreeModel tm, Object root) {
    LinkedList returnValue = new LinkedList();
    if (! tm.isLeaf(root)) {
      int numChild = tm.getChildCount(root);
      for (int i = 0; i < numChild; i++) {
        Object child = tm.getChild(root, i);
        returnValue.addAll(parseTree(tm, child));
      }
    }

    returnValue.add(root);
    return returnValue;
  }

  /**
   * Specified by interface net.suberic.pooka.UserProfileContainer
   */
  public UserProfile getDefaultProfile() {
    MailTreeNode selectedNode = getSelectedNode();

    if (selectedNode != null && selectedNode instanceof UserProfileContainer)
      return ((UserProfileContainer)selectedNode).getDefaultProfile();
    else
      return null;
  }

  /**
   * Gets the FolderPanelToolbar.
   */
  public ConfigurableToolbar getFolderPanelToolbar() {
    return folderPanelToolbar;
  }

  /**
   * Sets the FolderPanelToolbar.
   */
  public void setFolderPanelToolbar(ConfigurableToolbar newFolderPanelToolbar) {
    if (folderPanelToolbar != null)
      this.remove(folderPanelToolbar);

    folderPanelToolbar = newFolderPanelToolbar;

    if (folderPanelToolbar != null)
      this.add("North", folderPanelToolbar);

  }

  /**
   * Refreshes the active menus for the FolderPanel.
   */
  public void refreshActiveMenus() {
    if (folderPanelToolbar != null)
      folderPanelToolbar.setActive(getActions());
  }

  public Action[] getActions() {
    if (getSelectedNode() != null) {
      Action[] nodeActions = getSelectedNode().getActions();
      if (nodeActions != null)
        return TextAction.augmentList(nodeActions, defaultActions);
    }

    return defaultActions;
  }

  /**
   * Handles drag and drop events over the JTree.
   */
  class FolderTreeDropTarget extends DropTarget {
    public void dragOver(DropTargetDragEvent dtde) {
      // first see if we accept this drag.
      if (mTransferHandler.canImport(folderTree, dtde.getCurrentDataFlavors())) {

        Point e = dtde.getLocation();

        TreePath path = folderTree.getClosestPathForLocation(e.x, e.y);

        if (folderTree.getPathBounds(path).contains(e.x, e.y)) {
          // check to see if this is a folder node or not.  at the moment,
          // we only support dropping into folder nodes.
          if (path.getLastPathComponent() instanceof FolderNode) {
            // this means that we're over a node.  make it selected.
            if (!folderTree.isPathSelected(path))
              folderTree.setSelectionPath(path);

            // and, since we can support dropping here, offer this as a
            // drop target.


          }
        }
      }
    }

    public void drop(DropTargetDropEvent dtde) {
      boolean accept = false;
      // first see if we accept this drag.
      if (mTransferHandler.canImport(folderTree, dtde.getCurrentDataFlavors())) {

        Point e = dtde.getLocation();

        TreePath path = folderTree.getClosestPathForLocation(e.x, e.y);

        if (folderTree.getPathBounds(path).contains(e.x, e.y)) {
          // check to see if this is a folder node or not.  at the moment,
          // we only support dropping into folder nodes.
          if (path.getLastPathComponent() instanceof FolderNode) {
            // this means that we're over a node.  make it selected.
            if (!folderTree.isPathSelected(path))
              folderTree.setSelectionPath(path);

            // and now drop here.
            accept = true;

            dtde.acceptDrop(dtde.getDropAction());

            try {
              java.awt.datatransfer.Transferable trans = dtde.getTransferable();
              boolean returnValue = mTransferHandler.importData(folderTree, trans);
              dtde.dropComplete(returnValue);
            } catch (RuntimeException re) {
              dtde.dropComplete(false);
            }
          }
        }
      }

      if (! accept) {
        dtde.rejectDrop();
      }
    }
  }

  public class NewMessageAction extends AbstractAction {
    NewMessageAction() {
      super("message-new");
    }

    public void actionPerformed(ActionEvent e) {
      try {
        MessageUI nmu = Pooka.getUIFactory().createMessageUI(new NewMessageProxy(new net.suberic.pooka.NewMessageInfo(new javax.mail.internet.MimeMessage(mainPanel.getSession()))));
        nmu.openMessageUI();
      } catch (OperationCancelledException oce) {
      } catch (MessagingException me) {
        Pooka.getUIFactory().showError(Pooka.getProperty("error.NewMessage.errorLoadingMessage", "Error creating new message:  ") + "\n" + me.getMessage(), Pooka.getProperty("error.NewMessage.errorLoadingMessage.title", "Error creating new message."), me);
      }

    }

  }


}





