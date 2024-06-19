package net.suberic.util;

/**
 * This interface defines an object that can create items given a 
 * sourceBundle, resourceString, and itemID.
 */

public interface ItemCreator {

  /**
   * Creates an item from the given sourceBundle, resourceString, and itemID.
   */
  public Item createItem(VariableBundle sourceBundle, String resourceString, String itemID);
}
