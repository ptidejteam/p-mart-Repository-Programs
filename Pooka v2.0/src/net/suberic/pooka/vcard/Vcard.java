package net.suberic.pooka.vcard;
import java.io.*;
import java.util.Properties;
import javax.mail.internet.InternetAddress;

/**
 * A class which represents a vcard address book entry.
 */
public class Vcard implements Comparable, net.suberic.pooka.AddressBookEntry {

  public static final int SORT_BY_ADDRESS = 0;

  public static final int SORT_BY_LAST_NAME = 1;

  public static final int SORT_BY_FIRST_NAME = 2;

  public static final int SORT_BY_PERSONAL_NAME = 3;

  public static final int SORT_BY_ID = 4;

  Properties properties;

  private int sortingMethod = SORT_BY_ID;

  private InternetAddress[] addresses;

  /**
   * Creates a new Vcard.
   */
  public Vcard() {
    properties = new Properties();
  }

  /**
   * Creates a new Vcard from the given properties.
   */
  public Vcard(Properties newProps) {
    properties = newProps;
  }

  /**
   * Gets a property on the Vcard.
   */
  public String getProperty(String propertyName) {
    return properties.getProperty(propertyName);
  }

  /**
   * Sets a property on the Vcard.
   */
  public void setProperty(String propertyName, String newValue) {
    properties.setProperty(propertyName, newValue);

    // if pretty much anything changes on here, then we should probably
    // null out the address objects.
    addresses = null;
  }

  /**
   * Gets a Properties representation of the values in the AddressBookEntry.
   */
  public java.util.Properties getProperties() {
    Properties returnValue = new Properties();
    // we need five settings:  "personalName", "firstName", "lastName",
    // "address", and "id".

    returnValue.setProperty("currentAddress.personalName", getPersonalName());
    returnValue.setProperty("currentAddress.firstName", getFirstName());
    returnValue.setProperty("currentAddress.lastName", getLastName());
    returnValue.setProperty("currentAddress.address", getAddressString());
    returnValue.setProperty("currentAddress.id", getID());
    return returnValue;
  }

  /**
   * Gets the InternetAddress associated with this Vcard.
   */
  public InternetAddress[] getAddresses() {
    try {
      if (addresses == null) {
        addresses = InternetAddress.parse(properties.getProperty("email;internet"), false);
      }
      return addresses;
    } catch (javax.mail.internet.AddressException ae) {
      ae.printStackTrace();
      return null;
    }
  }

  /**
   * Gets the String that's a proper representation of the address(es)
   * in this AddressBookEntry.
   */
  public String getAddressString() {
    String returnValue = properties.getProperty("email;internet");
    if (returnValue != null)
      return returnValue;
    else
      return "";
  }

  /**
   * Sets the InternetAddress associated with this Vcard.
   */
  public void setAddress(InternetAddress newAddress) {
    setAddresses(new InternetAddress[] { newAddress });
  }

  /**
   * Sets the InternetAddress associated with this Vcard.
   */
  public void setAddresses(InternetAddress newAddresses[]) {
    if (newAddresses != null) {
      addresses = newAddresses;
      properties.setProperty("email;internet", InternetAddress.toString(newAddresses));
    }
  }

  /**
   * Gets the PersonalName property associated with this Vcard.
   */
  public String getPersonalName() {
    String returnValue = properties.getProperty("fn");
    if (returnValue != null)
      return returnValue;
    else
      return "";
  }

  /**
   * Gets the PersonalName property associated with this Vcard.
   */
  public void setPersonalName(String newName) {
    properties.setProperty("fn", newName);
  }

  /**
   * Gets the FirstName property associated with this Vcard.
   */
  public String getFirstName() {
    String name = properties.getProperty("n");
    if (name != null) {
      int index = name.indexOf(";");
      if (index >= 0)
        return name.substring(index + 1);
    }

    return "";
  }

  /**
   * Gets the FirstName property associated with this Vcard.
   */
  public void setFirstName(String newName) {
    String oldName = properties.getProperty("n");
    if (oldName != null) {
      int index = oldName.indexOf(";");
      if (index > 0)
        properties.setProperty("n", oldName.substring(0, index) + ";" + newName);
      else if (index == 0) {
        properties.setProperty("n", ";" + newName);

      }
    } else {
      properties.setProperty("n", ";" + newName);
    }
  }

  /**
   * Gets the LastName property associated with this Vcard.
   */
  public String getLastName() {
    String name = properties.getProperty("n");
    if (name != null) {
      int index = name.indexOf(";");
      if (index >= 0)
        return name.substring(0, index);
    }

    return "";
  }

  /**
   * Gets the LastName property associated with this Vcard.
   */
  public void setLastName(String newName) {
    String name = properties.getProperty("n");
    if (name != null) {
      int index = name.indexOf(";");
      if (index >= 0)
        properties.setProperty("n", newName + ";" + ((index + 1 < name.length()) ? name.substring(index + 1) : ""));
    } else {
      properties.setProperty("n", newName + ";");
    }

  }

  /**
   * Gets the user information, last name first.
   */
  public String getLastFirst() {
    return getLastName() + ", " + getFirstName();
  }

  /**
   * Gets the user information, first name first.
   */
  public String getFirstLast() {
    return getFirstName() + " " + getLastName();
  }

  /**
   * Gets the email address (as a string) associated with this Vcard.
   */
  public String getEmailAddress() {
    return getAddressString();
  }

  /**
   * <p>Gets the ID of this AddressBookEntry.  This is the ID that will
   * be searched by default, that can be entered into the To: field, etc.</p>
   */
  public String getID() {
    return getPersonalName();
  }

  /**
   * <p>Sets the ID of this AddressBookEntry.  This is the ID that will
   * be searched by default, that can be entered into the To: field, etc.</p>
   */
  public void setID(String newID) {
    setPersonalName(newID);
  }

  //----  Comparable  ----//

  /**
   * Compares this Vcard either to another Vcard, or a String which matches
   * the Vcard.  This checks the sortingMethod setting to decide how to
   * compare to the Vcard or String.
   *
   * @param   o the Object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this Object.
   */

  public int compareTo(Object o) {
    if (o instanceof Vcard) {
      Vcard target = (Vcard) o;

      switch (sortingMethod) {
      case (SORT_BY_ADDRESS):
        int returnValue = getAddressString().compareTo(target.getAddressString());
        return returnValue;
      case (SORT_BY_LAST_NAME):
        return getLastFirst().compareTo(target.getLastFirst());
      case (SORT_BY_FIRST_NAME):
        return getFirstLast().compareTo(target.getFirstLast());
      case (SORT_BY_PERSONAL_NAME):
        return getPersonalName().compareTo(target.getPersonalName());
      default:
        return getID().compareTo(target.getID());
      }
    } else if (o instanceof String) {
      String compareString = null;
      String matchString = (String) o;

      switch (sortingMethod) {
      case (SORT_BY_ADDRESS):
        compareString = getAddressString();
        break;
      case (SORT_BY_LAST_NAME):
        compareString = getLastFirst();
        break;
      case (SORT_BY_FIRST_NAME):
        compareString = getFirstLast();
        break;
      default:
        compareString = getID();
      }

      // see if the string to be matched is shorter; if so, match
      // with just that length.


      int origSize = compareString.length();
      int matchSize = matchString.length();
      if (matchSize < origSize) {
        int returnValue =  compareString.substring(0,matchSize).compareTo(matchString);
        return returnValue;
      } else {
        int returnValue =  compareString.compareTo(matchString);
        return returnValue;
      }
    }

    return -1;
  }

  //----  parser  ----//

  /**
   * Parses a vcard from a BufferedReader.
   */
  public static Vcard parse(BufferedReader reader) throws java.text.ParseException {

    try {
      Properties newProps = new Properties();

      boolean isDone = false;

      String line = getNextLine(reader);
      if (line != null) {
        String[] current = parseLine(line);
        if (current[0] != null && current[1] != null) {
          if (! (current[0].equalsIgnoreCase("begin") && current[1].equalsIgnoreCase("vcard")))
            throw new java.text.ParseException("No beginning", 0);
        }
        else {
          newProps.put(current[0], current[1]);
        }
      } else {
        return null;
      }

      while (!isDone) {
        line = getNextLine(reader);
        if (line != null) {
          String[] current = parseLine(line);
          if (current[0] != null && current[1] != null) {
            if (current[0].equalsIgnoreCase("end")) {
              if (current[1].equalsIgnoreCase("vcard"))
                isDone = true;
              else
                throw new java.text.ParseException("incorrect end tag", 0);
            } else {
              newProps.put(current[0], current[1]);
            }
          }
        } else {
          isDone = true;
        }
      }
      return new Vcard(newProps);
    } catch (IOException ioe) {
      throw new java.text.ParseException(ioe.getMessage(), 0);
    }
  }

  /**
   * Parses a name/value pair from an rfc2425 stream.
   */
  private static String getNextLine(BufferedReader reader) throws IOException {
    String firstLine = reader.readLine();
    boolean isDone = false;
    if (firstLine != null) {
      while (! isDone) {
        reader.mark(256);
        String nextLine = reader.readLine();
        if (nextLine != null && nextLine.length() > 0) {
          if (! Character.isWhitespace(nextLine.charAt(0))) {
            isDone = true;
            reader.reset();
          } else {
            firstLine = firstLine + nextLine.substring(1);
          }
        } else {
          isDone = true;
        }
      }
    }
    return firstLine;
  }

  private static String[] parseLine(String firstLine) {
    String[] returnValue = new String[2];

    int dividerLoc = firstLine.indexOf(':');
    returnValue[0] = firstLine.substring(0, dividerLoc);
    returnValue[1] = firstLine.substring(dividerLoc + 1);

    return returnValue;
  }

  /**
   * Writes this Vcard out to the given BufferedWriter.
   */
  public void write(BufferedWriter out) throws java.io.IOException {
    out.write("begin:vcard");
    out.newLine();
    java.util.Enumeration propNames = properties.propertyNames();
    while (propNames.hasMoreElements()) {
      String currentName = (String) propNames.nextElement();
      out.write(currentName);
      out.write(":");
      out.write(properties.getProperty(currentName));
      out.newLine();
    }
    out.write("end:vcard");
    out.newLine();
  }
}
