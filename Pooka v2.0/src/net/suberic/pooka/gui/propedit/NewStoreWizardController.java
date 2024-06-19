package net.suberic.pooka.gui.propedit;
import java.util.*;
import net.suberic.util.gui.propedit.*;
import net.suberic.util.VariableBundle;

/**
 * The controller class for the NewStoreWizard.
 */
public class NewStoreWizardController extends WizardController {

  /**
   * Creates a NewStoreWizardController.
   */
  public NewStoreWizardController(String sourceTemplate, WizardEditorPane wep) {
    super(sourceTemplate, wep);
  }

  /**
   * Checks the state transition to make sure that we can move from
   * state to state.
   */
  public void checkStateTransition(String oldState, String newState) throws PropertyValueVetoException {
    getEditorPane().validateProperty(oldState);
    if (newState.equals("userInfo") && oldState.equals("storeConfig")) {
      // load default values into the user configuration.
      //System.err.println("moving to userInfo; setting default values.");
      String protocol = getManager().getCurrentProperty("NewStoreWizard.editors.store.protocol", "imap");
      //System.err.println("protocol = " + protocol);
      if (protocol.equalsIgnoreCase("imap") || protocol.equalsIgnoreCase("pop3")) {
        String user = getManager().getCurrentProperty("NewStoreWizard.editors.store.user", "");
        String server = getManager().getCurrentProperty("NewStoreWizard.editors.store.server", "");
        //System.err.println("setting username to " + user + "@" + server);
        getManager().setTemporaryProperty("NewStoreWizard.editors.user.from", user + "@" + server);
        PropertyEditorUI fromEditor = getManager().getPropertyEditor("NewStoreWizard.editors.user.from");
        //System.err.println("got fromEditor " + fromEditor);
        fromEditor.setOriginalValue(user + "@" + server);
        fromEditor.resetDefaultValue();

      } else {
        //System.err.println("local store");
        String username = System.getProperty("user.name");
        String hostname = "localhost";
        try {
          java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
          hostname = localMachine.getHostName();

        } catch(java.net.UnknownHostException uhe) {
          // just use 'localhost'
        }
        String address = username + "@" + hostname;
        getManager().setTemporaryProperty("NewStoreWizard.editors.user.from", address);
        PropertyEditorUI fromEditor = getManager().getPropertyEditor("NewStoreWizard.editors.user.from");
        //System.err.println("got fromEditor " + fromEditor);
        fromEditor.setOriginalValue(address);
        fromEditor.resetDefaultValue();
      }
    } else if (newState.equals("outgoingServer") && oldState.equals("userInfo")) {
      // load default values into the smtp configuration.
      String protocol = getManager().getCurrentProperty("NewStoreWizard.editors.store.protocol", "imap");
      //System.err.println("protocol = " + protocol);
      if (protocol.equalsIgnoreCase("imap") || protocol.equalsIgnoreCase("pop3")) {
        String user = getManager().getCurrentProperty("NewStoreWizard.editors.store.user", "");
        String server = getManager().getCurrentProperty("NewStoreWizard.editors.store.server", "");
        getManager().setTemporaryProperty("NewStoreWizard.editors.smtp.user", user);
        PropertyEditorUI userEditor = getManager().getPropertyEditor("NewStoreWizard.editors.smtp.user");
        userEditor.setOriginalValue(user);
        userEditor.resetDefaultValue();

        getManager().setTemporaryProperty("NewStoreWizard.editors.smtp.server", server);
        PropertyEditorUI serverEditor = getManager().getPropertyEditor("NewStoreWizard.editors.smtp.server");
        serverEditor.setOriginalValue(server);
        serverEditor.resetDefaultValue();
      } else {
        getManager().setTemporaryProperty("NewStoreWizard.editors.smtp.server", "localhost");
        PropertyEditorUI serverEditor = getManager().getPropertyEditor("NewStoreWizard.editors.smtp.server");
        serverEditor.setOriginalValue("localhost");
        serverEditor.resetDefaultValue();

      }
    } else if (newState.equals("storeName")) {
      String user = getManager().getCurrentProperty("NewStoreWizard.editors.store.user", "");
      String server = getManager().getCurrentProperty("NewStoreWizard.editors.store.server", "");
      String storeName = user + "@" + server;
      PropertyEditorUI storeNameEditor = getManager().getPropertyEditor("NewStoreWizard.editors.store.storeName");

      setUniqueProperty(storeNameEditor, storeName, "NewStoreWizard.editors.store.storeName");

      String smtpServerName = "";

      // set the username
      String userProfileName= getManager().getCurrentProperty("NewStoreWizard.editors.user.userProfile", "__default");
      if (userProfileName.equalsIgnoreCase("__new")) {
        userProfileName = getManager().getCurrentProperty("NewStoreWizard.editors.user.from", "");
        // set the smtp server name only for new users
        smtpServerName= getManager().getCurrentProperty("NewStoreWizard.editors.smtp.outgoingServer", "__default");
        if (smtpServerName.equalsIgnoreCase("__new")) {
          smtpServerName = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.server", "");
        } else if (smtpServerName.equalsIgnoreCase("__default")) {
          smtpServerName = getManager().getProperty("NewStoreWizard.editors.smtp.outgoingServer.listMapping.__default.label", "< Global Default SMTP Server >");
        }
      } else if (userProfileName.equalsIgnoreCase("__default")) {
        userProfileName = getManager().getProperty("NewStoreWizard.editors.user.userProfile.listMapping.__default.label", "< Global Default Profile >");
      }
      PropertyEditorUI userProfileNameEditor = getManager().getPropertyEditor("NewStoreWizard.editors.user.userName");
      setUniqueProperty(userProfileNameEditor, userProfileName, "NewStoreWizard.editors.user.userName");

      PropertyEditorUI smtpServerNameEditor = getManager().getPropertyEditor("NewStoreWizard.editors.smtp.smtpServerName");
      setUniqueProperty(smtpServerNameEditor, smtpServerName, "NewStoreWizard.editors.smtp.smtpServerName");
    }
  }

  /**
   * Gets the next state.
   */
  public String getNextState(String currentState) {
    int current = mStateList.indexOf(mState);
    if (current > -1 && current < (mStateList.size() -1)) {
      String newState = mStateList.get(current + 1);
      // if we're not creating a new user, skip the smtp server step.
      if (newState.equals("outgoingServer")) {
        String newUser = getManager().getCurrentProperty("NewStoreWizard.editors.user.userProfile", ListEditorPane.SELECTION_DEFAULT);
        if (! newUser.equalsIgnoreCase(ListEditorPane.SELECTION_NEW)) {
          if (current < (mStateList.size() -2)) {
            newState = mStateList.get(current + 2);
          }
        }
      }
      return newState;
    } else {
      return null;
    }
  }

  /**
   * Gets the state that should be displayed next from a back request.
   */
  public String getBackState(String currentState) {
    int current = mStateList.indexOf(currentState);
    if (current >= 1) {
      String newState = mStateList.get(current - 1);
      // if we're not creating a new user, skip the smtp server step.
      if (newState.equals("outgoingServer")) {
        String newUser = getManager().getCurrentProperty("NewStoreWizard.editors.user.userProfile", ListEditorPane.SELECTION_DEFAULT);
        if (! newUser.equalsIgnoreCase(ListEditorPane.SELECTION_NEW)) {
          if (current >= 2) {
            newState = mStateList.get(current - 2);
          }
        }
      }
      return newState;
    } else {
      return null;
    }
  }


  /**
   * Saves all of the properties for this wizard.
   */
  protected void saveProperties() throws PropertyValueVetoException {
    Properties storeProperties = createStoreProperties();
    Properties userProperties = createUserProperties();
    Properties smtpProperties = createSmtpProperties();
    //getManager().clearValues();

    addAll(storeProperties);
    addAll(userProperties);
    addAll(smtpProperties);

    // now add the values to the store, user, and smtp server editors,
    // if necessary.
    String accountName = getManager().getCurrentProperty("NewStoreWizard.editors.store.storeName", "testStore");
    MultiEditorPane mep = (MultiEditorPane) getManager().getPropertyEditor("Store");
    if (mep != null) {
      mep.addNewValue(accountName);
    } else {
      appendProperty("Store", accountName);
    }

    String defaultUser = getManager().getCurrentProperty("NewStoreWizard.editors.user.userProfile", "__default");
    if (defaultUser.equals("__new")) {
      String userName = getManager().getCurrentProperty("NewStoreWizard.editors.user.userName", "");
      mep = (MultiEditorPane) getManager().getPropertyEditor("UserProfile");
      if (mep != null) {
        mep.addNewValue(userName);
      } else {
        appendProperty("UserProfile", userName);
      }

      String defaultSmtpServer = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.outgoingServer", "__default");
      if (defaultSmtpServer.equals("__new")) {
        String smtpServerName = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.smtpServerName", "");
        mep = (MultiEditorPane) getManager().getPropertyEditor("OutgoingServer");
        if (mep != null) {
          mep.addNewValue(smtpServerName);
        } else {
          // if there's no editor, then set the value itself.
          appendProperty("OutgoingServer", smtpServerName);
        }
      }
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
   * Creates the storeProperties from the wizard values.
   */
  public Properties createStoreProperties() {
    Properties returnValue = new Properties();

    String accountName = getManager().getCurrentProperty("NewStoreWizard.editors.store.storeName", "testStore");
    String protocol = getManager().getCurrentProperty("NewStoreWizard.editors.store.protocol", "imap");
    returnValue.setProperty("Store." + accountName + ".protocol", protocol);

    if (protocol.equalsIgnoreCase("imap")) {
      returnValue.setProperty("Store." + accountName + ".server", getManager().getCurrentProperty("NewStoreWizard.editors.store.server", ""));
      returnValue.setProperty("Store." + accountName + ".user", getManager().getCurrentProperty("NewStoreWizard.editors.store.user", ""));
      returnValue.setProperty("Store." + accountName + ".password", getManager().getCurrentProperty("NewStoreWizard.editors.store.password", ""));
      returnValue.setProperty("Store." + accountName + ".port", getManager().getCurrentProperty("NewStoreWizard.editors.store.port", ""));

      returnValue.setProperty("Store." + accountName + ".useSubscribed", "true");
      returnValue.setProperty("Store." + accountName + ".SSL", getManager().getCurrentProperty("NewStoreWizard.editors.store.SSL", "none"));
      returnValue.setProperty("Store." + accountName + ".cacheMode", getManager().getCurrentProperty("NewStoreWizard.editors.store.cacheMode", "headersOnly"));
    } else if (protocol.equalsIgnoreCase("pop3")) {
      returnValue.setProperty("Store." + accountName + ".server", getManager().getCurrentProperty("NewStoreWizard.editors.store.server", ""));
      returnValue.setProperty("Store." + accountName + ".user", getManager().getCurrentProperty("NewStoreWizard.editors.store.user", ""));
      returnValue.setProperty("Store." + accountName + ".password", getManager().getCurrentProperty("NewStoreWizard.editors.store.password", ""));
      returnValue.setProperty("Store." + accountName + ".port", getManager().getCurrentProperty("NewStoreWizard.editors.store.port", ""));

      returnValue.setProperty("Store." + accountName + ".SSL", getManager().getCurrentProperty("NewStoreWizard.editors.store.SSL", "none"));
      returnValue.setProperty("Store." + accountName + ".useMaildir", "true");
      /*
      returnValue.setProperty("Store." + accountName + ".leaveMessagesOnServer", getManager().getCurrentProperty("NewStoreWizard.editors.store.leaveOnServer", "true"));
      if (manager.getCurrentProperty("NewStoreWizard.editors.store.leaveOnServer", "true").equalsIgnoreCase("true")) {
        returnValue.setProperty("Store." + accountName + ".deleteOnServerOnLocalDelete", "true");
      }
      */
    } else if (protocol.equalsIgnoreCase("mbox")) {
      returnValue.setProperty("Store." + accountName + ".inboxLocation", getManager().getCurrentProperty("NewStoreWizard.editors.store.inboxLocation", "/var/spool/mail/" + System.getProperty("user.name")));
      returnValue.setProperty("Store." + accountName + ".mailDirectory", getManager().getCurrentProperty("NewStoreWizard.editors.store.mailDirectory", getManager().getCurrentProperty("Pooka.cacheDirectory", "${pooka.root}" + java.io.File.separator + ".pooka")));
    } else if (protocol.equalsIgnoreCase("maildir")) {
      returnValue.setProperty("Store." + accountName + ".mailDir", getManager().getCurrentProperty("NewStoreWizard.editors.store.mailDir", "${pooka.root}" + java.io.File.separator + "Maildir"));
    }

    /*
    List<String> storeList = getManager().getPropertyAsList("Store", "");
    storeList.add(accountName);

    returnValue.setProperty("Store", net.suberic.util.VariableBundle.convertToString(storeList));
    */

    return returnValue;
  }

  /**
   * Creates the userProperties from the wizard values.
   */
  public Properties createUserProperties() {
    Properties returnValue = new Properties();

    String storeName = getManager().getCurrentProperty("NewStoreWizard.editors.store.storeName", "testStore");

    String defaultUser = getManager().getCurrentProperty("NewStoreWizard.editors.user.userProfile", "__default");
    if (defaultUser.equals("__new")) {
      String from = getManager().getCurrentProperty("NewStoreWizard.editors.user.from", "test@example.com");
      String fromPersonal = getManager().getCurrentProperty("NewStoreWizard.editors.user.fromPersonal", "");
      String replyTo = getManager().getCurrentProperty("NewStoreWizard.editors.user.replyTo", "");
      String replyToPersonal = getManager().getCurrentProperty("NewStoreWizard.editors.user.replyToPersonal", "");

      String userName = getManager().getCurrentProperty("NewStoreWizard.editors.user.userName", from);

      returnValue.setProperty("UserProfile." + userName + ".mailHeaders.From", from );
      returnValue.setProperty("UserProfile." + userName + ".mailHeaders.FromPersonal", fromPersonal);
      returnValue.setProperty("UserProfile." + userName + ".mailHeaders.ReplyTo", replyTo);
      returnValue.setProperty("UserProfile." + userName + ".mailHeaders.ReplyToPersonal", replyToPersonal);

      returnValue.setProperty("Store." + storeName + ".defaultProfile", userName);
    } else {
      returnValue.setProperty("Store." + storeName + ".defaultProfile", defaultUser);
    }
    return returnValue;
  }

  /**
   * Creates the smtpProperties from the wizard values.
   */
  public Properties createSmtpProperties() {
    Properties returnValue = new Properties();

    String defaultUser = getManager().getCurrentProperty("NewStoreWizard.editors.user.userProfile", "__default");
    // only make smtp server changes if there's a new user.
    if (defaultUser.equals("__new")) {
      String userName = getManager().getCurrentProperty("NewStoreWizard.editors.user.userName", "newuser");

      String defaultSmtpServer = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.outgoingServer", "__default");
      if (defaultSmtpServer.equals("__new")) {
        String serverName = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.smtpServerName", "newSmtpServer");
        String server = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.server", "");
        String port = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.port", "");
        String authenticated = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.authenticated", "");
        String user = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.user", "");
        String password = getManager().getCurrentProperty("NewStoreWizard.editors.smtp.password", "");

        returnValue.setProperty("OutgoingServer." + serverName + ".server", server);
        returnValue.setProperty("OutgoingServer." + serverName + ".port", port);
        returnValue.setProperty("OutgoingServer." + serverName + ".authenticated", authenticated);
        if (authenticated.equalsIgnoreCase("true")) {

          returnValue.setProperty("OutgoingServer." + serverName + ".user", user );
          returnValue.setProperty("OutgoingServer." + serverName + ".password", password);
        }

        returnValue.setProperty("UserProfile." + userName + ".mailServer", serverName);
      } else {
        returnValue.setProperty("UserProfile." + userName + ".mailServer", defaultSmtpServer);
      }
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
