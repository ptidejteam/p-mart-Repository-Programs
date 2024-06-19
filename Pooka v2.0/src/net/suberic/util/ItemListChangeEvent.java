package net.suberic.util;

/**
 * 
 */
public class ItemListChangeEvent {

  Item[] added;
  Item[] removed;
  ItemManager manager;

  /**
   * Creates an ItemListChangeEvent.
   */
  public ItemListChangeEvent(ItemManager mgr, Item[] itemsAdded, Item[] itemsRemoved) {
    manager=mgr;
    added=itemsAdded;
    removed=itemsRemoved;
  }
  
  /**
   * Gets the Items that have been added.
   */
  public Item[] getAdded() {
    return added;
  }

  /**
   * Gets the Items that have been removed.
   */
  public Item[] getRemoved() {
    return removed;
  }
}
