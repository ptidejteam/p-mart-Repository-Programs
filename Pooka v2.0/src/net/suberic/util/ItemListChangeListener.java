package net.suberic.util;

/**
 * A listener that listens for ItemListChangeEvents.
 */
public interface ItemListChangeListener {

  /**
   * Indicates that an ItemList has changed.
   */
  public void itemListChanged(ItemListChangeEvent e);
}
