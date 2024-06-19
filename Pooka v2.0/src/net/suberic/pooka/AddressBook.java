package net.suberic.pooka;

/**
 * Defines the methods used to store Addresses.
 */
public interface AddressBook {

  /**
   * Configures the given address book.
   */
  public void configureAddressBook(String id);

  /**
   * Gets and appropriate AddressMatcher.
   */
  public AddressMatcher getAddressMatcher();

  /**
   * Adds an AddressBookEntry to the AddressBook.
   */
  public void addAddress(AddressBookEntry newEntry);

  /**
   * Removes an AddressBookEntry from the AddressBook.
   */
  public void removeAddress(AddressBookEntry removeEntry);

  /**
   * Gets the ID for this address book.
   */
  public String getAddressBookID();

  /**
   * Loads the AddressBook.
   */
  public void loadAddressBook() throws java.io.IOException, java.text.ParseException;

  /**
   * Saves the AddressBook.
   */
  public void saveAddressBook() throws java.io.IOException;

  /**
   * Creates a new, empty AddressBookEntry.
   */
  public AddressBookEntry newAddressBookEntry();

}
