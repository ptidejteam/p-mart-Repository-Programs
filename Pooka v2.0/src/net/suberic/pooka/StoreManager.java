package net.suberic.pooka;

import javax.mail.*;
import java.util.*;
import net.suberic.util.*;

/**
 * This class manages the a list of StoreInfos.  It also provides some
 * convenience methods for accessing FolderInfos within the StoreInfos,
 * and for adding and removing StoreInfos.
 */

public class StoreManager implements ItemCreator, ItemListChangeListener {

  private ItemManager manager;
  private LinkedList listenerList = new LinkedList();

  public StoreManager() {
    createStoreList();
  }

  //-----------------------
  // public interface.

  /**
   * As defined in net.suberic.util.ValueChangeListener.
   *
   * This listens for ItemListChangeEvents, which result from changes to the
   * "Store" property.  The result is that refrestStoreInfos() is called,
   * and then the event is passed to listeners to this object.
   */
  public void itemListChanged(ItemListChangeEvent e) {
    fireItemListChanged(e);
    refreshStoreInfos(e);
  }

  /**
   * This returns a Vector with all the currently registered StoreInfo
   * objects.
   */
  public java.util.Vector getStoreList() {
    return manager.getItems();
  }

  /**
   * This adds the store with the given storeName to the allStores list.
   */
  public void addStore(String storeName) {
    manager.addItem(storeName);
  }

  /**
   * This adds the stores with the given storeNames to the allStores list.
   */
  public void addStore(String[] storeName) {
    manager.addItem(storeName);
  }

  /**
   * This removes the store with the given storeName.
   */
  public void removeStore(String storeName) {
    manager.removeItem(storeName);
  }

  /**
   * This removes the stores with the given storeNames.
   */
  public void removeStore(String[] storeNames) {
    manager.removeItem(storeNames);
  }

  /**
   * This removes the given StoreInfo.
   */
  public void removeStore(StoreInfo store) {
    manager.removeItem(store);
  }

  /**
   * This removes the given StoreInfos.
   */
  public void removeStore(StoreInfo[] stores) {
    manager.removeItem(stores);
  }

  /**
   * This compares the storeList object with the Store property, and
   * updates the storeList appropriately.
   */
  public void refreshStoreInfos(ItemListChangeEvent e) {
    Item[] removedStores = e.getRemoved();
    for (int i = 0; removedStores != null && i < removedStores.length; i++) {
      ((StoreInfo) removedStores[i]).remove();
    }
  }

  /**
   * This returns the FolderInfo which corresponds to the given folderName.
   * The folderName should be in the form "/storename/folder/subfolder".
   */
  public FolderInfo getFolder(String folderName) {
    if (folderName != null && folderName.length() >= 1) {
      int divider = folderName.indexOf('/', 1);
      while (divider == 0) {
  folderName = folderName.substring(1);
  divider = folderName.indexOf('/');
      }

      if (divider > 0) {
  String storeName = folderName.substring(0, divider);
  StoreInfo store = getStoreInfo(storeName);
  if (store != null) {
    return store.getChild(folderName.substring(divider +1));
  }
      }
    }

    return null;
  }

  /**
   * This returns the FolderInfo which corresponds to the given folderID.
   * The folderName should be in the form "storename.folderID.folderID".
   */
  public FolderInfo getFolderById(String folderID) {
    // hurm.  the problem here is that '.' is a legal value in a name...

    java.util.Vector allStores = getStoreList();

    for (int i = 0; i < allStores.size(); i++) {
      StoreInfo currentStore = (StoreInfo) allStores.elementAt(i);
      if (folderID.startsWith(currentStore.getStoreID())) {
  FolderInfo possibleMatch = currentStore.getFolderById(folderID);
  if (possibleMatch != null) {
    return possibleMatch;
  }
      }
    }

    return null;
  }

  /**
   * Gets all of the open and available folders known by the system.
   */
  public Vector getAllOpenFolders() {
    Vector returnValue = new Vector();
    Vector currentStores = getStoreList();
    for (int i = 0; i < currentStores.size(); i++) {
      returnValue.addAll(((StoreInfo) currentStores.elementAt(i)).getAllFolders());
    }

    return returnValue;
  }

  /**
   * This returns the StoreInfo with the given storeName if it exists
   * in the allStores Vector; otherwise, returns null.
   */
  public StoreInfo getStoreInfo(String storeID) {
    return (StoreInfo) manager.getItem(storeID);
  }

  /**
   * This loads all the Sent Folders on the UserProfile object.  This must
   * be called separately because UserProfiles have references to StoreInfos
   * and StoreInfos have references to UserProfiles.
   */
  public void loadAllSentFolders() {
    List profileList = Pooka.getPookaManager().getUserProfileManager().getUserProfileList();

    for (int i = 0; i < profileList.size(); i++) {
      ((UserProfile)profileList.get(i)).loadSentFolder();
    }
  }

  /**
   * This adds a ItemListChangeListener to the local listener list.
   */
  public void addItemListChangeListener(ItemListChangeListener ilcl) {
    if (! listenerList.contains(ilcl))
      listenerList.add(ilcl);
  }

  /**
   * This removes a ItemListChangeListener from the local listener list.
   */
  public void removeItemListChangeListener(ItemListChangeListener ilcl) {
    listenerList.remove(ilcl);
  }

  /**
   * This notifies all listeners that the StoreList has changed.
   */
  public void fireItemListChanged(ItemListChangeEvent e) {
    for (int i = 0; i < listenerList.size(); i++)
      ((ItemListChangeListener)listenerList.get(i)).itemListChanged(e);
  }


  /**
   * This creates a new StoreInfo.
   *
   * As defined by interface net.suberic.util.ItemCreator.
   */
  public Item createItem(VariableBundle sourceBundle, String resourceString, String itemID) {
    return new StoreInfo(itemID);
  }

  //---------------------------
  // the background stuff.

  /**
   * This loads and creates all the Stores using the "Store" property
   * of the main Pooka VariableBundle.
   */
  private void createStoreList() {
    manager = new ItemManager("Store", Pooka.getResources(), this);
    manager.addItemListChangeListener(this);
  }

  /**
   * Sets up SSL connections.
   */
  public static void setupSSL() {
    // set up the SSL socket factory.

    // we have to configure this or the sun jdks will fail.  however, this
    // also will make ibm jdks fail since they don't have the com.sun
    // classes.  so we have to be sneaky.

    try {
      Object provider = Class.forName("com.sun.net.ssl.internal.ssl.Provider").newInstance();

      java.security.Security.addProvider((java.security.Provider) provider);

      //java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    } catch (Exception e) {
      // if we catch an exception for this then we're probably running with
      // a non-sun jdk, in which case we shouldn't need to add a provider
      // explicitly
    }
    //java.security.Security.setProperty("ssl.SocketFactory.provider","net.suberic.pooka.ssl.PookaSSLSocketFactory");

  }

  /**
   * Cleans up the StoreManager.
   */
  public void cleanup() {
    Pooka.getResources().removeValueChangeListener(manager);
  }
}

