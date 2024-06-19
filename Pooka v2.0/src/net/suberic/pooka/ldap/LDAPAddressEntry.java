package net.suberic.pooka.ldap;
import net.suberic.pooka.*;

import javax.naming.*;
import javax.naming.directory.*;

/**
 * An Address Book Entry.
 */
public class LDAPAddressEntry implements AddressBookEntry {
 
  /**
   * Creates an LDAPAddressEntry from a given Attribute set.
   */
  public LDAPAddressEntry(Attributes newAttr) {

  }

  /**
   * Gets a property on the LDAPAddressEntry.
   */
  public String getProperty(String propertyName) {
    return null;
  }


  /**
   * Sets a property on the LDAPAddressEntry.
   */
  public void setProperty(String propertyName, String value) {

  }

  /**
   * Gets the InternetAddresses associated with this LDAPAddressEntry.
   */
  public javax.mail.internet.InternetAddress[] getAddresses() {
    return null;
  }

  /**
   * Gets the String that's a proper representation of the address(es)
   * in this AddressBookEntry.
   */
  public String getAddressString() {
    return "";
  }


  /**
   * Gets the PersonalName property associated with this LDAPAddressEntry.
   */
  public String getPersonalName() {
    return null;
  }

  /**
   * Gets the FirstName property associated with this LDAPAddressEntry.
   */
  public String getFirstName() {
    return null;
  }

  /**
   * Gets the LastName property associated with this LDAPAddressEntry.
   */
  public String getLastName() {
    return null;
  }

  /**
   * sets the InternetAddress associated with this LDAPAddressEntry.
   */
  public void setAddresses(javax.mail.internet.InternetAddress[] newAddress) {

  }

  /**
   * sets the InternetAddress associated with this LDAPAddressEntry.
   */
  public void setAddress(javax.mail.internet.InternetAddress newAddress) {

  }

  /**
   * Gets the PersonalName property associated with this LDAPAddressEntry.
   */
  public void setPersonalName(String newName) {

  }

  /**
   * Gets the FirstName property associated with this LDAPAddressEntry.
   */
  public void setFirstName(String newName) {

  }

  /**
   * Gets the LastName property associated with this LDAPAddressEntry.
   */
  public void setLastName(String newName) {

  }

  /**
   * Gets a Properties representation of the values in the LDAPAddressEntry.
   */
  public java.util.Properties getProperties() {
    return null;
  }

  /**
   * <p>Gets the ID of this AddressBookEntry.  This is the ID that will
   * be searched by default, that can be entered into the To: field, etc.</p>
   */
  public String getID() {
    return "";
  }

  /**
   * <p>Sets the ID of this AddressBookEntry.  This is the ID that will
   * be searched by default, that can be entered into the To: field, etc.</p>
   */
  public void setID(String newID) {

  }


}
