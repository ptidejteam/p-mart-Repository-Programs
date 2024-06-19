package net.suberic.util;

/**
 * This represents an item which can be created from a VariableBundle
 * property.
 */
public interface Item {

  /**
   * The Item ID.  For example, if you were to have a list of users, a
   * given user's itemID may be "defaultUser".
   */
  public String getItemID();

  /**
   * The Item property.  For example, if you were to have a list of users, a
   * given user's itemPropertymay be "Users.defaultUser".
   */
  public String getItemProperty();
}
