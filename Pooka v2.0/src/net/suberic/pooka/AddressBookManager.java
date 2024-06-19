package net.suberic.pooka;

import javax.mail.*;
import java.util.*;
import net.suberic.util.*;

/**
 * This class manages the a list of AddressBooks.
 */

public class AddressBookManager implements ValueChangeListener {

  private Vector addressBookList;
  private Vector valueChangeListenerList = new Vector();

  public AddressBookManager() {
    addressBookList = createAddressBookList();
    Pooka.getResources().addValueChangeListener(this, "AddressBook");
  }

  //-----------------------
  // public interface.

  /**
   * As defined in net.suberic.util.ValueChangeListener.
   *
   * This listens for changes to the "AddressBook" property and calls
   * refreshAddressBooks() when it gets one.
   */
  public void valueChanged(String changedValue) {
    if (changedValue.equals("AddressBook")) {
      refreshAddressBooks();
    }
  }

  /**
   * This returns a Vector with all the currently registered AddressBook
   * objects.
   */
  public java.util.Vector getAddressBookList() {
    return new Vector(addressBookList);
  }

  /**
   * This adds the addressBook with the given addressBookName to the
   * allAddressBooks list.
   */
  public void addAddressBook(String addressBookName) {
    if (getAddressBook(addressBookName) == null) {
      appendToAddressBookString(addressBookName);
    }
  }

  /**
   * This adds the addressBooks with the given addressBookNames to the
   * allAddressBooks list.
   */
  public void addAddressBook(String[] addressBookName) {
    if (addressBookName != null && addressBookName.length > 0) {
      StringBuffer addressBookString = new StringBuffer();
      for (int i = 0 ; i < addressBookName.length; i++) {
        if (getAddressBook(addressBookName[i]) == null)
          addressBookString.append(addressBookName[i] + ":");
      }
      if (addressBookString.length() > 0)
        appendToAddressBookString(new String(addressBookString.deleteCharAt(addressBookString.length() -1)));
    }
  }

  /**
   * This removes the addressBook with the given addressBookName.
   */
  public void removeAddressBook(String addressBookName) {
    if (getAddressBook(addressBookName) != null)
      removeFromAddressBookString(new String[] { addressBookName });
  }

  /**
   * This removes the addressBooks with the given addressBookNames.
   */
  public void removeAddressBook(String[] addressBookNames) {
    // this is probably not necessary at all, but what the hell?

    if (addressBookNames == null || addressBookNames.length < 1)
      return;

    Vector matches = new Vector();
    for ( int i = 0; i < addressBookNames.length; i++) {
      if (getAddressBook(addressBookNames[i]) != null)
        matches.add(addressBookNames[i]);

    }

    if (matches.size() < 1)
      return;

    String[] removedAddressBooks = new String[matches.size()];

    for (int i = 0; i < matches.size(); i++)
      removedAddressBooks[i] = (String) matches.elementAt(i);

    removeFromAddressBookString(removedAddressBooks);
  }

  /**
   * This removes the given AddressBook.
   */
  public void removeAddressBook(AddressBook addressBook) {
    if (addressBook != null)
      removeAddressBook(addressBook.getAddressBookID());
  }

  /**
   * This removes the given AddressBooks.
   */
  public void removeAddressBook(AddressBook[] addressBook) {
    if (addressBook != null && addressBook.length > 0) {
      String[] addressBookNames = new String[addressBook.length];
      for (int i = 0; i < addressBook.length; i++) {
        if (addressBook[i] != null)
          addressBookNames[i] = addressBook[i].getAddressBookID();
      }

      removeAddressBook(addressBookNames);
    }
  }

  /**
   * This compares the addressBookList object with the AddressBook property, and
   * updates the addressBookList appropriately.
   */
  public void refreshAddressBooks() {
    Vector newAddressBookList = new Vector();

    StringTokenizer tokens =  new StringTokenizer(Pooka.getProperty("AddressBook", ""), ":");

    String addressBookID;
    while (tokens.hasMoreTokens()) {
      addressBookID = tokens.nextToken();
      AddressBook currentAddressBook = getAddressBook(addressBookID);
      if (currentAddressBook != null) {
        newAddressBookList.add(currentAddressBook);
      } else {
        currentAddressBook = createAddressBook(addressBookID);
        if (currentAddressBook != null) {
          newAddressBookList.add(currentAddressBook);

          if (Pooka.getProperty("AddressBook._default", "").equalsIgnoreCase(""))
            Pooka.setProperty("AddressBook._default", addressBookID);
        }

      }
    }

    if (! newAddressBookList.equals(addressBookList)) {
      addressBookList = newAddressBookList;
      fireAddressBookListChangedEvent();
    }
  }

  /**
   * Creates an address book.
   */
  public AddressBook createAddressBook(String id) {
    String type = Pooka.getProperty("AddressBook." + id + ".type", "");
    if (type.equalsIgnoreCase("file")) {
      AddressBook returnValue = new net.suberic.pooka.vcard.VcardAddressBook();
      returnValue.configureAddressBook(id);
      return returnValue;
    }

    return null;
  }

  /**
   * This returns the AddressBook which corresponds to the given name.
   */
  public AddressBook getAddressBook(String name) {
    if (addressBookList == null)
      return null;

    for (int i = 0; i < addressBookList.size(); i++) {
      AddressBook currentBook = (AddressBook) addressBookList.elementAt(i);
      if (currentBook != null && currentBook.getAddressBookID().equals(name))
        return currentBook;
    }

    return null;
  }

  /**
   * This adds a ValueChangeListener to the local listener list.
   */
  public void addValueChangeListener(ValueChangeListener vcl) {
    if (! valueChangeListenerList.contains(vcl))
      valueChangeListenerList.add(vcl);
  }

  /**
   * This removes a ValueChangeListener from the local listener list.
   */
  public void removeValueChangeListener(ValueChangeListener vcl) {
    valueChangeListenerList.remove(vcl);
  }

  /**
   * This notifies all listeners that the AddressBookList has changed.
   */

  public void fireAddressBookListChangedEvent() {
    for (int i = 0; i < valueChangeListenerList.size(); i++)
      ((ValueChangeListener)valueChangeListenerList.elementAt(i)).valueChanged("AddressBook");
  }


  //---------------------------
  // the background stuff.

  /**
   * This loads and creates all the AddressBooks using the "AddressBook"
   * property of the main Pooka VariableBundle.
   */
  private Vector createAddressBookList() {
    Vector allAddressBooks = new Vector();
    String addressBookID = null;

    StringTokenizer tokens =  new StringTokenizer(Pooka.getProperty("AddressBook", ""), ":");

    while (tokens.hasMoreTokens()) {
      addressBookID=(String)tokens.nextToken();
      allAddressBooks.add(createAddressBook(addressBookID));
    }

    return allAddressBooks;
  }

  /**
   * This appends the newAddressBookString to the "AddressBook" property.
   */
  private void appendToAddressBookString(String newAddressBookString) {
    String oldValue = Pooka.getProperty("AddressBook");
    String newValue;
    if (oldValue.length() > 0 && oldValue.charAt(oldValue.length() -1) != ':') {
      newValue = oldValue + ":" + newAddressBookString;
    } else {
      newValue = oldValue + newAddressBookString;
    }

    Pooka.setProperty("AddressBook", newValue);
  }

  /**
   * This removes the addressBook names in the addressBookNames array from the
   * "AddressBook" property.
   */
  private void removeFromAddressBookString(String[] addressBookNames) {
    StringTokenizer tokens =  new StringTokenizer(Pooka.getProperty("AddressBook", ""), ":");

    boolean first = true;
    StringBuffer newValue = new StringBuffer();
    String addressBookID;

    while (tokens.hasMoreTokens()) {
      addressBookID=tokens.nextToken();
      boolean keep=true;

      for (int i = 0; keep == true && i < addressBookNames.length; i++) {
        if (addressBookID.equals(addressBookNames[i]))
          keep = false;
      }
      if (keep) {
        if (!first)
          newValue.append(":");

        newValue.append(addressBookID);
        first = false;
      }

    }

    Pooka.setProperty("AddressBook", newValue.toString());
  }

  /**
   * Gets the default Address Book, if there is one.
   */
  public AddressBook getDefault() {
    String defaultName = Pooka.getProperty("AddressBook._default", "");
    if (! defaultName.equals("")) {
      AddressBook defaultBook = getAddressBook(defaultName);
      return defaultBook;
    } else
      return null;

  }


}

