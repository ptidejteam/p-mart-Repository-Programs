package net.suberic.pooka;
import javax.mail.internet.InternetAddress;

/**
 * Defines the methods used to access Internet Addresses from a given
 * String.
 */
public interface AddressMatcher {

  /**
   * Returns all of the AddressBookEntries which match the given String.
   */
  public AddressBookEntry[] match(String matchString);

  /**
   * <p>Returns the AddressBookEntries that match the given String exactly.
   */
  public AddressBookEntry[] matchExactly(String matchString);

  /**
   * Returns all of the AddressBookEntries whose FirstName matches the given 
   * String.
   */
  public AddressBookEntry[] matchFirstName(String matchString);

  /**
   * Returns all of the AddressBookEntries whose LastName matches the given 
   * String.
   */
  public AddressBookEntry[] matchLastName(String matchString);

  /**
   * Returns all of the AddressBookEntryes whose email addresses match the
   * given String.
   */
  public AddressBookEntry[] matchEmailAddress(String matchString);

  /**
   * Returns the AddressBookEntry which follows the given String alphabetically.
   */
  public AddressBookEntry getNextMatch(String matchString);

  /**
   * Returns the AddressBookEntry which precedes the given String 
   * alphabetically.
   */
  public AddressBookEntry getPreviousMatch(String matchString);
}
