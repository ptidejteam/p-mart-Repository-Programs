package net.suberic.pooka.gui.propedit;
import java.io.*;
import java.util.*;
import net.suberic.pooka.Pooka;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;

/**
 * The controller class for the AddressBookWizard.
 */
public class AddressBookWizardController extends WizardController {

  /**
   * Creates a AddressBookWizardController.
   */
  public AddressBookWizardController(String sourceTemplate, WizardEditorPane wep) {
    super(sourceTemplate, wep);
  }

  /**
   * Checks the state transition to make sure that we can move from
   * state to state.
   */
  public void checkStateTransition(String oldState, String newState) throws PropertyValueVetoException {
    getEditorPane().validateProperty(oldState);
    if (newState.equals("name")) {
      // set the address book name
      PropertyEditorUI addressBookNameEditor = getManager().getPropertyEditor("AddressBook._newValueWizard.name.bookName");

      String addressBookName = "AddressBook";
      setUniqueProperty(addressBookNameEditor, addressBookName, "AddressBook._newValueWizard.name.bookName");
    }
  }

  /**
   * Saves all of the properties for this wizard.
   */
  protected void saveProperties() throws PropertyValueVetoException {
    Properties addressBookProperties = createAddressBookProperties();
    //getManager().clearValues();

    addAll(addressBookProperties);

    String addressBookName = getManager().getCurrentProperty("AddressBook._newValueWizard.name.bookName", "");
    MultiEditorPane mep = (MultiEditorPane) getManager().getPropertyEditor("AddressBook");
    if (mep != null) {
      mep.addNewValue(addressBookName);
    } else {
      // if there's no editor, then set the value itself.
      appendProperty("AddressBook", addressBookName);
    }
  }

  /**
   * Finsihes the wizard.
   */
  public void finishWizard() throws PropertyValueVetoException {
    if (getManager().getCurrentProperty("AddressBook._newValueWizard.type.bookType", "file").equalsIgnoreCase("file")) {
      // configure file address book.

      // check to make sure that the new file is valid.
      String filename = Pooka.getResourceManager().translateName(getManager().getCurrentProperty("AddressBook._newValueWizard.config.filename", ""));

      File file = new File(filename);
      try {
        if (! file.exists()) {
          file.createNewFile();
        }
        if (! file.canRead()) {
          throw new PropertyValueVetoException("AddressBook._newValueWizard.config.filename", filename, getManager().getProperty("error.cannotReadFile", "Can not read file."), null);
        }
      } catch (java.io.IOException ioe) {
        throw new PropertyValueVetoException("AddressBook._newValueWizard.config.filename", filename, ioe.getMessage(), null);
      }
      saveProperties();
      getEditorPane().getWizardContainer().closeWizard();
    } else if (getManager().getCurrentProperty("AddressBook._newValueWizard.type.bookType", "file").equalsIgnoreCase("jdbc")) {
      // configure jdbc address book.

      // FIXME just save it for now.
      saveProperties();
      getEditorPane().getWizardContainer().closeWizard();

    }
  }

  /**
   * Creates the addressBookProperties from the wizard values.
   */
  public Properties createAddressBookProperties() {
    Properties returnValue = new Properties();

    String addressBookName = getManager().getCurrentProperty("AddressBook._newValueWizard.name.bookName", "");

    String type = getManager().getCurrentProperty("AddressBook._newValueWizard.type.bookType", "");
    returnValue.setProperty("AddressBook." + addressBookName + ".type", type);
    if ("file".equalsIgnoreCase(type)) {
      String filename = getManager().getCurrentProperty("AddressBook._newValueWizard.config.filename", "");

      returnValue.setProperty("AddressBook." + addressBookName + ".filename", filename);
    } else if ("jdbc".equalsIgnoreCase(type)) {
      returnValue.setProperty("AddressBook." + addressBookName + ".jdbc.driver", getManager().getCurrentProperty("AddressBook._newValueWizard.config.jdbc.driver", ""));
      returnValue.setProperty("AddressBook." + addressBookName + ".jdbc.url", getManager().getCurrentProperty("AddressBook._newValueWizard.config.jdbc.url", ""));
      returnValue.setProperty("AddressBook." + addressBookName + ".jdbc.user", getManager().getCurrentProperty("AddressBook._newValueWizard.config.jdbc.user", ""));
      returnValue.setProperty("AddressBook." + addressBookName + ".jdbc.password", getManager().getCurrentProperty("AddressBook._newValueWizard.config.jdbc.password", ""));
    }

    return returnValue;
  }

  /**
   * Adds all of the values from the given Properties to the
   * PropertyEditorManager.
   */
  void addAll(Properties props) {
    Set<String> names = props.stringPropertyNames();
    for (String name: names) {
      getManager().setProperty(name, props.getProperty(name));
    }
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
   * Appends the given value to the property.
   */
  public void appendProperty(String property, String value) {
    List<String> current = getManager().getPropertyAsList(property, "");
    current.add(value);
    getManager().setProperty(property, VariableBundle.convertToString(current));
  }
}
