package net.suberic.util;

import javax.mail.*;
import java.util.*;

/**
 * This class manages a list of Items.
 */

public class ItemManager implements ValueChangeListener {

  // an  ordered list of item id's
  private List itemIdList;

  // the mapping from the item id's to the items themselves
  private HashMap itemIdMap = new HashMap();

  // for convenience, the itemList itself.
  private List itemList;

  //the ItemListChange listeners
  private List listenerList = new LinkedList();

  // the resource which defines this ItemList
  private String resourceString;

  // the current value.
  private String currentValue;

  // the VariableBundle which contains the Item information.
  private VariableBundle sourceBundle;

  // the ItemCreator which is used to create new items.
  private ItemCreator itemCreator;

  /**
   * Create an ItemManager which loads information from the given
   * VariableBundle using the given newResourceString, and creates new
   * Items using the given ItemCreator.
   */
  public ItemManager(String newResourceString, VariableBundle newSourceBundle, ItemCreator newItemCreator) {
    resourceString = newResourceString;
    sourceBundle = newSourceBundle;
    itemCreator = newItemCreator;

    createItemList();
    sourceBundle.addValueChangeListener(this, resourceString);
  }

  //-----------------------
  // public interface.

  /**
   * This returns a Vector with all the currently registered Item
   * objects.
   */
  public synchronized java.util.Vector getItems() {
    return new Vector(itemList);
  }

  /**
   * This returns the Item with the given itemName if it exists; otherwise,
   * returns null.
   */
  public synchronized Item getItem(String itemID) {
    if (itemID != null && itemIdList.contains(itemID))
      return (Item)itemIdMap.get(itemID);
    else
      return null;
  }

  /**
   * This adds the item with the given name to the item list.
   */
  public synchronized void addItem(String itemName) {
    addItem(new String[] { itemName });
  }

  /**
   * This adds the items with the given itemNames to the items list.
   */
  public synchronized void addItem(String[] itemName) {
    if (itemName != null && itemName.length > 0) {
      StringBuffer itemString = new StringBuffer();
      for (int i = 0 ; i < itemName.length; i++) {
  if (! itemIdList.contains(itemName[i]))
    itemString.append(itemName[i] + ":");
      }
      if (itemString.length() > 0)
  appendToItemString(new String(itemString.deleteCharAt(itemString.length() -1)));
    }
  }

  /**
   * This adds the given item to the items list.
   */
  public synchronized void addItem(Item newItem) {
    addItem(new Item[] { newItem });
  }

  /**
   * This adds the given items to the items list.
   */
  public synchronized void addItem(Item[] newItem) {
    if (newItem != null) {
      String[] itemNames = new String[newItem.length];
      for (int i = 0; i < itemNames.length; i++) {
  itemNames[i] = newItem[i].getItemID();
  // we'll go ahead and add this here.  this will make it so, later
  // on, when we add the item to the main list, we get this Item,
  // rather than creating a new one.
  if (getItem(itemNames[i]) == null)
    itemIdMap.put(itemNames[i], newItem[i]);
      }

      addItem(itemNames);
    }
  }

  /**
   * This removes the item with the given itemName.
   */
  public synchronized void removeItem(String itemName) {
    removeFromItemString(new String[] { itemName });
  }

  /**
   * This removes the items with the given itemNames.
   */
  public synchronized void removeItem(String[] itemNames) {
    // this is probably not necessary at all, but what the hell?

    if (itemNames == null || itemNames.length < 1)
      return;

    Vector matches = new Vector();
    for ( int i = 0; i < itemNames.length; i++) {
      if (itemIdList.contains(itemNames[i]))
  matches.add(itemNames[i]);

    }

    if (matches.size() < 1)
      return;

    String[] removedItems = new String[matches.size()];

    matches.toArray(removedItems);

    removeFromItemString(removedItems);
  }

  /**
   * This removes the given Item.
   */
  public synchronized void removeItem(Item item) {
    if (item != null)
      removeItem(item.getItemID());
  }

  /**
   * This removes the given Items.
   */
  public synchronized void removeItem(Item[] item) {
    if (item != null && item.length > 0) {
      String[] itemNames = new String[item.length];
      for (int i = 0; i < item.length; i++) {
  if (item[i] != null)
    itemNames[i] = item[i].getItemID();
      }

      removeItem(itemNames);
    }
  }

  /**
   * This compares the itemList object with the resourceString property, and
   * updates the itemList appropriately.
   *
   * This method is called from valueChanged() when the underlying resource
   * changes.  It actually goes through and updates the objects on the
   * ItemManager, and then notifies its ItemListChangedListeners by calling
   * fireItemListChangeEvent().
   */
  public synchronized void refreshItems() {
    if (! sourceBundle.getProperty(resourceString, "").equals(currentValue)) {
      currentValue = sourceBundle.getProperty(resourceString, "");

      LinkedList newIdList = new LinkedList();
      LinkedList newItemList = new LinkedList();

      Vector addedItemList = new Vector();
      Vector removedIdList = new Vector(itemIdList);

      StringTokenizer tokens =  new StringTokenizer(sourceBundle.getProperty(resourceString, ""), ":");

      String itemID;

      // at the end of this loop, we should end up with a newIdList which is a
      // list of the currently valid item id's, a newItemList which is a list
      // of the currently valid items, an addedItemList which is a list of added
      // items, and a removedIdList which is a list of removed id's.
      while (tokens.hasMoreTokens()) {
  itemID = tokens.nextToken();
  newIdList.add(itemID);

  if (itemIdList.contains(itemID)) {
    removedIdList.remove(itemID);
  } else {
    // this is being added.
    Item currentItem = (Item) itemIdMap.get(itemID);
    if (currentItem == null) {
      itemIdMap.put(itemID, itemCreator.createItem(sourceBundle, resourceString, itemID));
    }
    addedItemList.add(itemIdMap.get(itemID));
  }
  newItemList.add(itemIdMap.get(itemID));
      }

      Item[] removedItems = new Item[removedIdList.size()];
      for (int i = 0 ; i < removedIdList.size(); i++) {
  Item currentItem = (Item) itemIdMap.get(removedIdList.get(i));
  if (currentItem != null) {
    itemIdMap.remove(removedIdList.get(i));
    removedItems[i] = currentItem;
  }
      }

      Item[] addedItems = new Item[addedItemList.size()];
      addedItemList.toArray(addedItems);

      itemList = newItemList;
      itemIdList = newIdList;

      fireItemListChangeEvent(new ItemListChangeEvent(this, addedItems, removedItems));
    }
  }

  /**
   * As defined in net.suberic.util.ValueChangeListener.
   *
   * This listens for changes to the source property and calls
   * refreshItems() when it gets one.
   */
  public void valueChanged(String changedValue) {
    if (changedValue.equals(resourceString))
      refreshItems();
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
   * This notifies all listeners that the ItemList has changed.
   */
  public void fireItemListChangeEvent(ItemListChangeEvent e) {
    for (int i = 0; i < listenerList.size(); i++)
      ((ItemListChangeListener)listenerList.get(i)).itemListChanged(e);
  }


    //---------------------------
    // the background stuff.

  /**
   * This loads and creates all the Items using the resourceString property
   * of the sourceBundle.
   */
  private void createItemList() {
    itemList = new LinkedList();
    itemIdList = new LinkedList();
    String itemID = null;

    currentValue = sourceBundle.getProperty(resourceString, "");
    StringTokenizer tokens =  new StringTokenizer(currentValue, ":");

    while (tokens.hasMoreTokens()) {
      itemID=(String)tokens.nextToken();
      Item newItem = itemCreator.createItem(sourceBundle, resourceString, itemID);
      itemList.add(newItem);
      itemIdList.add(itemID);
      itemIdMap.put(itemID, newItem);
    }

  }

  /**
   * This appends the newItemString to the "Item" property.
   */
  private void appendToItemString(String newItemString) {
    String oldValue = sourceBundle.getProperty("Item", "");
    String newValue;
    if (oldValue.length() > 0 && oldValue.charAt(oldValue.length() -1) != ':') {
      newValue = oldValue + ":" + newItemString;
    } else {
      newValue = oldValue + newItemString;
    }

    sourceBundle.setProperty(resourceString, newValue);
  }

  /**
   * This removes the item names in the itemNames array from the
   * "Item" property.
   */
  private void removeFromItemString(String[] itemNames) {
    StringTokenizer tokens =  new StringTokenizer(sourceBundle.getProperty(resourceString, ""), ":");

    boolean first = true;
    StringBuffer newValue = new StringBuffer();
    String itemID;

    while (tokens.hasMoreTokens()) {
      itemID=tokens.nextToken();
      boolean keep=true;

      for (int i = 0; keep == true && i < itemNames.length; i++) {
  if (itemID.equals(itemNames[i]))
    keep = false;
      }
      if (keep) {
  if (!first)
    newValue.append(":");

  newValue.append(itemID);
  first = false;
      }

    }

    sourceBundle.setProperty(resourceString, newValue.toString());
  }


}

