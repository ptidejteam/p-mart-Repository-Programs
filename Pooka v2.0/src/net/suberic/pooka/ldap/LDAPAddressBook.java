package net.suberic.pooka.ldap;
import net.suberic.pooka.*;
import net.suberic.util.VariableBundle;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.naming.*;
import javax.naming.directory.*;


/**
 * An Address Book that is accessed through LDAP.
 */
public class LDAPAddressBook implements AddressBook, AddressMatcher {

  String addressBookID;

  InitialDirContext initialContext;
  Hashtable env;

  String ldapUrl;
  String username;
  String password;

  /**
   * Creates a new LDAPAddressBook.
   */
  public LDAPAddressBook() {

  }

  /**
   * Configures the LDAPAddressBook using the given ID.
   */
  public void configureAddressBook(String newAddressBookID) {
    addressBookID = newAddressBookID;
    ldapUrl = Pooka.getProperty("AddressBook." + addressBookID + ".url", "");
    username = Pooka.getProperty("AddressBook." + addressBookID + ".username", "");
    password = Pooka.getProperty("AddressBook." + addressBookID + ".password", "");
    if (!password.equals(""))
      password = net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(password);

    // make the connection.

    env = new Hashtable(5, 0.75f);
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

    /* Specify host and port to use for directory service */
    env.put(Context.PROVIDER_URL, ldapUrl);

    try {
      /* get a handle to an Initial DirContext */
      initialContext = new InitialDirContext(env);
    } catch (NamingException ne) {
      Pooka.getUIFactory().showError(Pooka.getProperty("error.AddressBook.ldap.gettingInitialContext", "Failed to connect to LDAP server:  ") + ne.getMessage());
    }
  }

  // AddressMatcher

  /**
   * Returns all of the AddressBookEntries which match the given String.
   */
  public AddressBookEntry[] match(String matchString) {
    // create the search string.
    String searchString = "(sn=" + matchString + ")";

    try {
      return doSearch(searchString);
    } catch (NamingException ne) {
      ne.printStackTrace();

      return new AddressBookEntry[0];
    }
  }

  /**
   * Returns all of the AddressBookEntries which match the given String.
   */
  public AddressBookEntry[] matchExactly(String matchString) {
    return match(matchString);
  }

  /**
   * Returns all of the AddressBookEntries whose FirstName matches the given
   * String.
   */
  public AddressBookEntry[] matchFirstName(String matchString) {
    // create the search string.
    String searchString = "(sn=" + matchString + ")";

    try {
      return doSearch(searchString);
    } catch (NamingException ne) {
      ne.printStackTrace();

      return new AddressBookEntry[0];
    }
  }

  /**
   * Returns all of the AddressBookEntries whose LastName matches the given
   * String.
   */
  public AddressBookEntry[] matchLastName(String matchString) {
    // create the search string.
    String searchString = "(sn=" + matchString + ")";

    try {
      return doSearch(searchString);
    } catch (NamingException ne) {
      ne.printStackTrace();

      return new AddressBookEntry[0];
    }
  }

  /**
   * Returns all of the AddressBookEntries whose email addresses match the
   * given String.
   */
  public AddressBookEntry[] matchEmailAddress(String matchString) {
    // create the search string.
    String searchString = "(sn=" + matchString + ")";

    try {
      return doSearch(searchString);
    } catch (NamingException ne) {
      ne.printStackTrace();

      return new AddressBookEntry[0];
    }
  }

  /**
   * Returns the AddressBookEntry which follows the given String alphabetically.
   */
  public AddressBookEntry getNextMatch(String matchString) {
    return null;
  }

  /**
   * Returns the AddressBookEntry which precedes the given String
   * alphabetically.
   */
  public AddressBookEntry getPreviousMatch(String matchString) {
    return null;
  }

  // AddressBook

  /**
   * Gets and appropriate AddressMatcher.
   */
  public AddressMatcher getAddressMatcher() {
    return this;
  }

  /**
   * Adds an AddressBookEntry to the AddressBook.
   */
  public void addAddress(AddressBookEntry newEntry) {

  }

  /**
   * Removes an AddressBookEntry from the AddressBook.
   */
  public void removeAddress(AddressBookEntry removeEntry) {

  }

  /**
   * Gets the ID for this address book.
   */
  public String getAddressBookID() {
    return addressBookID;
  }

  /**
   * Loads the AddressBook.
   */
  public void loadAddressBook() throws java.io.IOException, java.text.ParseException {

  }

  /**
   * Saves the AddressBook.
   */
  public void saveAddressBook() throws java.io.IOException {

  }

  public AddressBookEntry[] doSearch(String searchString) throws NamingException {

    /* specify search constraints to search subtree */
    SearchControls constraints = new SearchControls();
    constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

    NamingEnumeration results
      = initialContext.search("o=Ace Industry, c=US", "(sn=" + searchString + ")", constraints);

    LinkedList resultList = new LinkedList();

    while (results != null && results.hasMore()) {
      SearchResult si = (SearchResult)results.next();

      Attributes att = si.getAttributes();

      LDAPAddressEntry entry = new LDAPAddressEntry(att);

      resultList.add(entry);
    }

    AddressBookEntry[] returnValue = new AddressBookEntry[resultList.size()];

    resultList.toArray(returnValue);

    return returnValue;

  }

  /**
   * Creates a new, empty AddressBookEntry.
   */
  public AddressBookEntry newAddressBookEntry() {
    return new LDAPAddressEntry(null);
  }

}
