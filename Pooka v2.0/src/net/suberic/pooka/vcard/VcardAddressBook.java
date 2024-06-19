package net.suberic.pooka.vcard;
import net.suberic.pooka.*;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.io.*;

/**
 * An AddressBook which uses Vcards.
 */
public class VcardAddressBook implements AddressBook, AddressMatcher {

  String addressBookID;

  String fileName;
  Vcard[] orderedList;
  ArrayList arrayList = new ArrayList();

  int sortingMethod;

  /**
   * Creates a new VcardAddressBook.
   */
  public VcardAddressBook() {

  }

  /**
   * Creates a new VcardAddressBook from the given Vcard.  It uses the
   * file represented by the given pFileName as the source for the
   * addresses.
   */
  public VcardAddressBook(String pFileName) throws java.text.ParseException, java.io.IOException {
    fileName = pFileName;

    loadAddressBook();

  }

  public void configureAddressBook(String newAddressBookID) {
    addressBookID = newAddressBookID;
    fileName = Pooka.getProperty("AddressBook." + addressBookID + ".filename", "");

    try {
      loadAddressBook();
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
    }
  }

  /**
   * Loads the AddressBook from the saved filename.
   */
  public void loadAddressBook() throws java.text.ParseException, java.io.IOException {

    InputStream is = Pooka.getResourceManager().getInputStream(fileName);
    if (is != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      for(Vcard newCard = Vcard.parse(reader); newCard != null; newCard = Vcard.parse(reader)) {
        insertIntoList(newCard);
      }
    }

    /*
      File f = new File(fileName);

      if (f.exists()) {
      BufferedReader reader = new BufferedReader(new FileReader(f));
      for(Vcard newCard = Vcard.parse(reader); newCard != null; newCard = Vcard.parse(reader)) {
      insertIntoList(newCard);
      }
      } else {
      f.createNewFile();
      }
    */


    sortList();
  }

  /**
   * Inserts the given Vcard into the ordered list.
   */
  protected void insertIntoList(Vcard newCard) {
    arrayList.add(newCard);
  }

  /**
   * Adds the given Vcard to the address book.
   */
  public void addAddress(AddressBookEntry newAddress) {
    if (newAddress instanceof Vcard) {
      Vcard newCard = (Vcard) newAddress;
      Vcard[] newList = new Vcard[orderedList.length + 1];
      int searchResult = java.util.Arrays.binarySearch(orderedList, newCard);
      if (searchResult < 0) {
        int insertLocation = (searchResult + 1) * -1;
        if (insertLocation > 0)
          System.arraycopy(orderedList, 0, newList, 0, insertLocation);
        newList[insertLocation] = newCard;
        if (orderedList.length - insertLocation > 0)
          System.arraycopy(orderedList, insertLocation, newList, insertLocation + 1, orderedList.length - insertLocation);

        orderedList = newList;
        try {
          saveAddressBook();
        } catch (java.io.IOException ioe) {
          Pooka.getUIFactory().showError(Pooka.getProperty("error.savingVcard", "Error saving Address Book"), ioe);
        }
      }
    }
  }

  /**
   * Removes the entry from the AddressBook.
   */
  public void removeAddress(AddressBookEntry removeAddress) {
    if (removeAddress instanceof Vcard) {
      Vcard removeCard = (Vcard) removeAddress;
      Vcard[] newList = new Vcard[orderedList.length - 1];
      int searchResult = java.util.Arrays.binarySearch(orderedList, removeCard);
      if (searchResult >= 0) {
        if (searchResult > 0)
          System.arraycopy(orderedList, 0, newList, 0, searchResult);
        if (orderedList.length - searchResult > 1)
          System.arraycopy(orderedList, searchResult + 1, newList, searchResult, orderedList.length - searchResult - 1);

        orderedList = newList;
        try {
          saveAddressBook();
        } catch (java.io.IOException ioe) {
          Pooka.getUIFactory().showError(Pooka.getProperty("error.savingVcard", "Error saving Address Book"), ioe);
        }
      }
    }
  }


  /**
   * Saves the list.
   */
  public void saveAddressBook() throws java.io.IOException {

    /*
      File f = new File(fileName);
      if (f.exists()) {
      BufferedWriter writer = new BufferedWriter(new FileWriter(f));
      for(int i = 0; i < orderedList.length; i++) {
      orderedList[i].write(writer);
      }
      writer.flush();
      writer.close();
      }
    */

    OutputStream os = Pooka.getResourceManager().getOutputStream(fileName);
    if (os != null) {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
      for(int i = 0; i < orderedList.length; i++) {
        orderedList[i].write(writer);
      }
      writer.flush();
      writer.close();
    }

  }

  /**
   * Sorts the list.
   */
  protected void sortList() {
    orderedList = new Vcard[arrayList.size()];
    orderedList = (Vcard[]) arrayList.toArray(orderedList);
    java.util.Arrays.sort(orderedList);
  }

  /**
   * Gets the AddressMatcher for this AddressBook.
   */
  public AddressMatcher getAddressMatcher() {
    return this;
  }

  /**
   * Returns all of the InternetAddresses which match the given String.
   */
  public AddressBookEntry[] match(String matchString) {
    return match(matchString, false);
  }

  /**
   * Returns all of the InternetAddresses which match the given String.
   */
  public AddressBookEntry[] matchExactly(String matchString) {
    return match(matchString, true);
  }

  /**
   * Returns all of the InternetAddresses which match the given String.
   */
  public AddressBookEntry[] match(String matchString, boolean exactly) {
    if (orderedList.length < 1)
      return new AddressBookEntry[0];

    int value = java.util.Arrays.binarySearch(orderedList, matchString);
    // now get all the matches, if any.
    if (value < 0) {
      return new AddressBookEntry[0];
    }

    if (orderedList[value].compareTo(matchString) == 0) {
      if (exactly) {
        // FIXME assume for now only one match.
        String valueExact = orderedList[value].getPersonalName();
        if (valueExact.equalsIgnoreCase(matchString)) {
          return new AddressBookEntry[] { orderedList[value] };
        } else
          return new AddressBookEntry[0];
      }

      // get all the matches.
      int minimum = value;
      while (minimum > 0 && (orderedList[minimum - 1].compareTo(matchString) == 0))
        minimum--;


      int maximum = value;
      while (maximum < orderedList.length -1 && (orderedList[maximum + 1].compareTo(matchString) == 0))
        maximum++;

      AddressBookEntry[] returnValue = new AddressBookEntry[maximum - minimum + 1];

      for(int i = 0; i < returnValue.length; i++) {
        returnValue[i] = orderedList[minimum + i];
      }

      return returnValue;
    } else {
      return new AddressBookEntry[0];
    }
    //return binarySearch(matchString, 0, orderedList.size());
  }

  /**
   * Returns all of the InternetAddresses whose FirstName matches the given
   * String.
   */
  public AddressBookEntry[] matchFirstName(String matchString) {
    return match(matchString);
  }

  /**
   * Returns all of the InternetAddresses whose LastName matches the given
   * String.
   */
  public AddressBookEntry[] matchLastName(String matchString) {
    return match(matchString);
  }

  /**
   * Returns all of the InternetAddresses whose email addresses match the
   * given String.
   */
  public AddressBookEntry[] matchEmailAddress(String matchString) {
    return match(matchString);
  }

  /**
   * Returns the InternetAddress which follows the given String alphabetically.
   */
  public AddressBookEntry getNextMatch(String matchString) {
    if (orderedList.length < 1)
      return null;

    int value = java.util.Arrays.binarySearch(orderedList, matchString);
    // now get all the matches, if any.
    if (value < 0) {
      value = (value + 1) * -1;
    } else {
      // if we got a match, we want to return the next one.
      value = value + 1;
    }
    if (value >= orderedList.length) {
      return orderedList[orderedList.length - 1];
    } else {
      return orderedList[value];
    }
  }

  /**
   * Returns the InternetAddress which precedes the given String
   * alphabetically.
   */
  public AddressBookEntry getPreviousMatch(String matchString) {
    if (orderedList.length < 1)
      return null;

    int value = java.util.Arrays.binarySearch(orderedList, matchString);
    // now get all the matches, if any.
    if (value < 0) {
      value = (value + 2) * -1;
    } else {
      // if we got a match, we want to return the previous one.
      value = value - 1;
    }
    if (value < 0) {
      return orderedList[0];
    } else {
      return orderedList[value];
    }
  }

  /**
   * Returns the AddressBookID.
   */
  public String getAddressBookID() {
    return addressBookID;
  }

  /**
   * Creates a new, empty AddressBookEntry.
   */
  public AddressBookEntry newAddressBookEntry() {
    return new Vcard(new java.util.Properties());
  }

}
