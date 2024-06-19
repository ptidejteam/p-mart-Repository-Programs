package net.suberic.pooka;

/**
 * An Address Book Entry.
 */
public interface AddressBookEntry {

  /**
   * Gets a property on the AddressBookEntry.
   */
  public String getProperty(String propertyName);


  /**
   * Sets a property on the AddressBookEntry.
   */
  public void setProperty(String propertyName, String value);

  /**
   * Gets the InternetAddresses associated with this AddressBookEntry.
   */
  public javax.mail.internet.InternetAddress[] getAddresses();

  /**
   * Gets the String that's a proper representation of the address(es)
   * in this AddressBookEntry.
   */
  public String getAddressString();

  /**
   * Gets the PersonalName property associated with this AddressBookEntry.
   */
  public String getPersonalName();

  /**
   * Gets the FirstName property associated with this AddressBookEntry.
   */
  public String getFirstName();

  /**
   * Gets the LastName property associated with this AddressBookEntry.
   */
  public String getLastName();

  /**
   * sets the InternetAddress associated with this AddressBookEntry.
   */
  public void setAddress(javax.mail.internet.InternetAddress newAddress);

  /**
   * sets the InternetAddresses associated with this AddressBookEntry.
   */
  public void setAddresses(javax.mail.internet.InternetAddress[] newAddress);

  /**
   * Gets the PersonalName property associated with this AddressBookEntry.
   */
  public void setPersonalName(String newName);

  /**
   * Gets the FirstName property associated with this AddressBookEntry.
   */
  public void setFirstName(String newName);

  /**
   * Gets the LastName property associated with this AddressBookEntry.
   */
  public void setLastName(String newName);

  /**
   * Gets a Properties representation of the values in the AddressBookEntry.
   */
  public java.util.Properties getProperties();

  /**
   * <p>Gets the ID of this AddressBookEntry.  This is the ID that will
   * be searched by default, that can be entered into the To: field, etc.</p>
   */
  public String getID();

  /**
   * <p>Sets the ID of this AddressBookEntry.  This is the ID that will
   * be searched by default, that can be entered into the To: field, etc.</p>
   */
  public void setID(String newID);
  
}
