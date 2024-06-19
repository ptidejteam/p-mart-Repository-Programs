package net.suberic.pooka.gui.propedit;
import java.util.*;
import net.suberic.pooka.*;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;

/**
 * The controller class for the AddressEntry.
 */
public class AddressEntryController extends WizardController {

  AddressBook mBook;
  AddressBookEntry mEntry = null;
  String mOriginalEntryName = null;

  /**
   * Creates an AddressEntryController.
   */
  public AddressEntryController(String sourceTemplate, WizardEditorPane wep) {
    super(sourceTemplate, wep);
  }

  /**
   * Saves all of the properties for this wizard.
   */
  protected void saveProperties() throws PropertyValueVetoException {
    // check to make sure that the properties are valid.
    String name = getManager().getCurrentProperty("AddressBook.editor.addressList._newAddress.personalName", "");
    if (mOriginalEntryName == null || ! name.equalsIgnoreCase(mOriginalEntryName)) {
      if (mBook.getAddressMatcher().matchExactly(name).length > 0) {
        throw new PropertyValueVetoException("AddressBook.editor.addressList._newAddress.personalName", name, "Address already exists", null);
      }
      mEntry = mBook.newAddressBookEntry();
    } else {
      AddressBookEntry[] matches = mBook.getAddressMatcher().matchExactly(name);
      if (matches != null && matches.length > 0) {
        mEntry = matches[0];
      } else {
        mEntry = mBook.newAddressBookEntry();
      }
    }


    mEntry.setPersonalName(name);
    mEntry.setFirstName(getManager().getCurrentProperty("AddressBook.editor.addressList._newAddress.firstName", ""));
    mEntry.setLastName(getManager().getCurrentProperty("AddressBook.editor.addressList._newAddress.lastName", ""));


    try {
      mEntry.setAddress(new javax.mail.internet.InternetAddress (getManager().getCurrentProperty("AddressBook.editor.addressList._newAddress.address", "")));
    } catch (Exception e) {
      throw new PropertyValueVetoException(e.getMessage());
    }

    mBook.addAddress(mEntry);

    // and clear the property.

    getManager().setProperty("AddressBook.editor.addressList._newAddress.personalName", "");
    getManager().setProperty("AddressBook.editor.addressList._newAddress.firstName", "");
    getManager().setProperty("AddressBook.editor.addressList._newAddress.lastName", "");

    getManager().setProperty("AddressBook.editor.addressList._newAddress.address", "");

  }
  /**
   * Finsihes the wizard.
   */
  public void finishWizard() throws PropertyValueVetoException {
    getEditorPane().validateProperty(mState);

    saveProperties();
    getEditorPane().getWizardContainer().closeWizard();
  }

  public void setUniqueProperty(PropertyEditorUI editor, String originalValue, String propertyName) {
    String value = originalValue;
    boolean success = false;
    for (int i = 0 ; ! success &&  i < 10; i++) {
      if (i != 0) {
        value = originalValue + "_" + i;
      }
      try {
        editor.setOriginalValue(value);
        editor.resetDefaultValue();
        getManager().setTemporaryProperty(propertyName, value);
        success = true;
      } catch (PropertyValueVetoException pvve) {
        // on an exception, just start over.
      }
    }
  }

  /**
   * Loads the given entry.
   */
  public void loadEntry(AddressBookEntry pEntry) {
    try {
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.personalName").setOriginalValue(pEntry.getPersonalName());
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.personalName").resetDefaultValue();
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.firstName").setOriginalValue(pEntry.getFirstName());
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.firstName").resetDefaultValue();
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.lastName").setOriginalValue(pEntry.getLastName());
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.lastName").resetDefaultValue();
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.address").setOriginalValue(pEntry.getAddressString() != null ? pEntry.getAddressString() : "");
      getManager().getPropertyEditor("AddressBook.editor.addressList._newAddress.address").resetDefaultValue();
    } catch (Exception e) {
      e.printStackTrace();
    }
    mOriginalEntryName = pEntry.getPersonalName();
  }

  /**
   * Sets the AddressBook.
   */
  public void setAddressBook(AddressBook pBook) {
    mBook = pBook;
  }

  /**
   * Gets the AddressBookEntry created by this controller.
   */
  public AddressBookEntry getEntry() {
    return mEntry;
  }
}
