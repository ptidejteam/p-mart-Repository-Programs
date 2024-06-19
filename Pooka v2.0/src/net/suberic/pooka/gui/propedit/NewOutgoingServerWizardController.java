package net.suberic.pooka.gui.propedit;
import java.util.*;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;

/**
 * The controller class for the NewOutgoingServerWizard.
 */
public class NewOutgoingServerWizardController extends WizardController {

  /**
   * Creates a NewOutgoingServerWizardController.
   */
  public NewOutgoingServerWizardController(String sourceTemplate, WizardEditorPane wep) {
    super(sourceTemplate, wep);
  }

  /**
   * Checks the state transition to make sure that we can move from
   * state to state.
   */
  public void checkStateTransition(String oldState, String newState) throws PropertyValueVetoException {
    getEditorPane().validateProperty(oldState);
    if (newState.equals("name")) {

      String smtpServerName = "";

      // set the smtp servername
      PropertyEditorUI smtpServerNameEditor = getManager().getPropertyEditor("OutgoingServer._newValueWizard.name.smtpServerName");

      smtpServerName = getManager().getCurrentProperty("OutgoingServer._newValueWizard.serverInfo.server", "");
      setUniqueProperty(smtpServerNameEditor, smtpServerName, "OutgoingServer._newValueWizard.name.smtpServerName");
    }
  }

  /**
   * Gets the next state.
   */
  /*
    public String getNextState(String currentState) {
    int current = mStateList.indexOf(mState);
    if (current > -1 && current < (mStateList.size() -1)) {
    String newState = mStateList.get(current + 1);
    return newState;
    } else {
    return null;
    }
    }

  */
  /**
   * Gets the state that should be displayed next from a back request.
   */
  /*
    public String getBackState(String currentState) {
    int current = mStateList.indexOf(currentState);
    if (current >= 1) {
    String newState = mStateList.get(current - 1);
    return newState;
    } else {
    return null;
    }
    }
  */


  /**
   * Saves all of the properties for this wizard.
   */
  protected void saveProperties() throws PropertyValueVetoException {
    Properties smtpProperties = createSmtpProperties();
    //getManager().clearValues();

    addAll(smtpProperties);

    String smtpServerName = getManager().getCurrentProperty("OutgoingServer._newValueWizard.name.smtpServerName", "");
    MultiEditorPane mep = (MultiEditorPane) getManager().getPropertyEditor("OutgoingServer");
    if (mep != null) {
      mep.addNewValue(smtpServerName);
    } else {
      // if there's no editor, then set the value itself.
      appendProperty("OutgoingServer", smtpServerName);
    }
  }

  /**
   * Finsihes the wizard.
   */
  public void finishWizard() throws PropertyValueVetoException {
    saveProperties();
    getEditorPane().getWizardContainer().closeWizard();
  }

  /**
   * Creates the smtpProperties from the wizard values.
   */
  public Properties createSmtpProperties() {
    Properties returnValue = new Properties();

    String serverName = getManager().getCurrentProperty("OutgoingServer._newValueWizard.name.smtpServerName", "");

    String server = getManager().getCurrentProperty("OutgoingServer._newValueWizard.serverInfo.server", "");
    String port = getManager().getCurrentProperty("OutgoingServer._newValueWizard.serverInfo.port", "");
    String authenticated = getManager().getCurrentProperty("OutgoingServer._newValueWizard.serverInfo.authenticated", "");
    String user = getManager().getCurrentProperty("OutgoingServer._newValueWizard.serverInfo.user", "");
    String password = getManager().getCurrentProperty("OutgoingServer._newValueWizard.serverInfo.password", "");

    returnValue.setProperty("OutgoingServer." + serverName + ".server", server);
    returnValue.setProperty("OutgoingServer." + serverName + ".port", port);
    returnValue.setProperty("OutgoingServer." + serverName + ".authenticated", authenticated);
    if (authenticated.equalsIgnoreCase("true")) {

      returnValue.setProperty("OutgoingServer." + serverName + ".user", user );
      returnValue.setProperty("OutgoingServer." + serverName + ".password", password);
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
