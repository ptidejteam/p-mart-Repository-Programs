package net.suberic.pooka;
import net.suberic.pooka.gui.crypto.CryptoKeySelector;
import net.suberic.util.*;

import java.security.UnrecoverableKeyException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.xml.crypto.KeySelector;

public class UserProfile extends Object implements ValueChangeListener, Item {
  Properties mailProperties;
  String name;
  //URLName sendMailURL;
  String mailServerName;
  String defaultDomain;
  String defaultDefaultDomain;
  OutgoingMailServer mailServer;
  OutgoingMailServer mTempMailServer = null;
  //String sendPrecommand;
  String sentFolderName;
  FolderInfo sentFolder;
  public boolean autoAddSignature = true;
  public boolean signatureFirst = true;
  private SignatureGenerator sigGenerator;
  private AddressBook addressBook;

  private String smimeEncryptionKeyId;
  private String pgpEncryptionKeyId;
  private boolean signAsDefault = false;
  private String cryptoDefaultType = "PGP";

  private Vector excludeAddresses;

  // default value
  public static String S_DEFAULT_PROFILE_KEY = "__default";

  public UserProfile(String newName, VariableBundle mainProperties) {
    name = newName;

    registerChangeListeners();
  }

  /**
   * This populates the UserProfile from the Pooka properties list.
   * Useful on creation as well as when any of the properties change.
   */
  public void initializeFromProperties(VariableBundle mainProperties, List mailPropertiesList) {
    mailProperties = new Properties();
    String profileKey;

    for (int i = 0; i < mailPropertiesList.size(); i++) {
      profileKey = (String)mailPropertiesList.get(i);
      mailProperties.put(profileKey, mainProperties.getProperty("UserProfile." + name + ".mailHeaders." + profileKey, ""));
    }

    setSentFolderName(mainProperties.getProperty("UserProfile." + name + ".sentFolder", ""));

    mailServerName = mainProperties.getProperty("UserProfile." + name + ".mailServer", "_default");
    mailServer = null; // reload it next time it's requested.

    //sendMailURL=new URLName(mainProperties.getProperty("UserProfile." + name + ".sendMailURL", ""));
    //sendPrecommand=(String)mainProperties.getProperty("UserProfile." + name + ".sendPrecommand", "");
    sigGenerator=createSignatureGenerator();

    setDefaultDomain(mainProperties.getProperty("UserProfile." + name + ".defaultDomain", ""));

    String fromAddr = (String)mailProperties.get("From");
    excludeAddresses = new Vector();
    excludeAddresses.add(fromAddr);
    if (fromAddr.lastIndexOf('@') >0) {
      defaultDefaultDomain = fromAddr.substring(fromAddr.lastIndexOf('@') + 1);
    }
    Vector excludeProp = mainProperties.getPropertyAsVector("UserProfile." + name + ".excludeAddresses", "");
    excludeAddresses.addAll(excludeProp);

    pgpEncryptionKeyId = mainProperties.getProperty("UserProfile." + name + ".pgpKey", "");

    smimeEncryptionKeyId = mainProperties.getProperty("UserProfile." + name + ".smimeKey", "");

    signAsDefault = (! mainProperties.getProperty("UserProfile." + name + ".sigPolicy", "").equalsIgnoreCase("manual"));

    if (! signAsDefault) {
      if ( mainProperties.getProperty("UserProfile." + name + ".sigPolicy", "").equalsIgnoreCase("smime")) {
        cryptoDefaultType="SMIME";
      }
    }

    String addressBookId = mainProperties.getProperty("UserProfile." + name + ".addressBook", "");
    if (!addressBookId.equals("")) {
      addressBook = Pooka.getAddressBookManager().getAddressBook(addressBookId);
    } else
      addressBook = null;
  }

  /**
   * Registers this UserProfile as a ValueChangeListener for all of its
   * source properties.
   */
  private void registerChangeListeners() {
    VariableBundle resources = Pooka.getResources();
    resources.addValueChangeListener(this, "UserProfile." + name + ".*");
  }

  /**
   * Modifies the UserProfile if any of its source values change.
   *
   * As specified in net.suberic.util.ValueChangeListener.
   */
  public void valueChanged(String changedValue) {
    VariableBundle bundle = Pooka.getResources();
    UserProfileManager manager = Pooka.getPookaManager().getUserProfileManager();
    initializeFromProperties(bundle, manager.getMailPropertiesList());
  }

  /**
   * Populates the given message with the headers for this UserProfile.
   */
  public void populateMessage(MimeMessage mMsg) throws MessagingException {
    // I hate this.  I hate having to grab half of these headers on my
    // own.

    Enumeration keys = mailProperties.propertyNames();
    String fromAddr = null, fromPersonal = null, replyAddr = null, replyPersonal = null;

    while (keys.hasMoreElements()) {
      String key = (String)(keys.nextElement());

      if (key.equals("FromPersonal")) {
        fromPersonal = mailProperties.getProperty(key);
      } else if (key.equals("From")) {
        fromAddr = mailProperties.getProperty(key);
      } else if (key.equals("ReplyTo")) {
        replyAddr = mailProperties.getProperty(key);
      } else if (key.equals("ReplyToPersonal")) {
        replyPersonal = mailProperties.getProperty(key);
      } else {
        mMsg.setHeader(key, mailProperties.getProperty(key));
      }

      try {
        if (fromAddr != null)
          if (fromPersonal != null && !(fromPersonal.equals("")))
            mMsg.setFrom(new InternetAddress(fromAddr, fromPersonal));
          else
            mMsg.setFrom(new InternetAddress(fromAddr));

        if (replyAddr != null && !(replyAddr.equals("")))
          if (replyPersonal != null)
            mMsg.setReplyTo(new InternetAddress[] {new InternetAddress(replyAddr, replyPersonal)});
          else
            mMsg.setReplyTo(new InternetAddress[] {new InternetAddress(replyAddr)});

      } catch (java.io.UnsupportedEncodingException uee) {
        throw new MessagingException("", uee);
      }
    }
  }

  /**
   * Populates the given InternetHeaders object with the headers for this
   * UserProfile.
   */
  public void populateHeaders(InternetHeaders pHeaders) throws MessagingException {
    // I hate this.  I hate having to grab half of these headers on my
    // own.

    Enumeration keys = mailProperties.propertyNames();
    String fromAddr = null, fromPersonal = null, replyAddr = null, replyPersonal = null;

    while (keys.hasMoreElements()) {
      String key = (String)(keys.nextElement());

      if (key.equals("FromPersonal")) {
        fromPersonal = mailProperties.getProperty(key);
      } else if (key.equals("From")) {
        fromAddr = mailProperties.getProperty(key);
      } else if (key.equals("ReplyTo")) {
        replyAddr = mailProperties.getProperty(key);
      } else if (key.equals("ReplyToPersonal")) {
        replyPersonal = mailProperties.getProperty(key);
      } else {
        pHeaders.setHeader(key, mailProperties.getProperty(key));
      }

      try {
        if (fromAddr != null)
          if (fromPersonal != null && !(fromPersonal.equals("")))
            pHeaders.setHeader("From", new InternetAddress(fromAddr, fromPersonal).toString());
          else
            pHeaders.setHeader("From", new InternetAddress(fromAddr).toString());

        if (replyAddr != null && !(replyAddr.equals("")))
          if (replyPersonal != null)
            pHeaders.setHeader("Reply-To", new InternetAddress(replyAddr, replyPersonal).toString());
          else
            pHeaders.setHeader("Reply-To", new InternetAddress(replyAddr).toString());

      } catch (java.io.UnsupportedEncodingException uee) {
        throw new MessagingException("", uee);
      }
    }
  }

  /**
   * This removes the email addresses that define this user from the
   * given message's to fields.
   */
  public void removeFromAddress(Message m) {
    try {
      Address[] toRecs = m.getRecipients(Message.RecipientType.TO);
      Address[] ccRecs = m.getRecipients(Message.RecipientType.CC);
      Address[] bccRecs = m.getRecipients(Message.RecipientType.BCC);
      toRecs = filterAddressArray(toRecs);
      ccRecs = filterAddressArray(ccRecs);
      bccRecs = filterAddressArray(bccRecs);

      m.setRecipients(Message.RecipientType.TO, toRecs);
      m.setRecipients(Message.RecipientType.CC, ccRecs);
      m.setRecipients(Message.RecipientType.BCC, bccRecs);
    } catch (MessagingException me) {
    }
  }

  /**
   * Filters out this address from the given array of addresses.
   */
  private Address[] filterAddressArray(Address[] addresses) {
    if (addresses != null && addresses.length > 0) {
      Vector returnVector = new Vector();
      for (int i = 0; i < addresses.length; i++) {
        String currentAddress = ((InternetAddress) addresses[i]).getAddress();
        boolean found = false;
        for (int j = 0; j < excludeAddresses.size() && found == false ; j++) {
          String excludeAddr = (String)excludeAddresses.elementAt(j);
          if (currentAddress.equalsIgnoreCase(excludeAddr))
            found = true;
        }
        if (!found)
          returnVector.add(addresses[i]);
      }

      Object[] retArr = returnVector.toArray();
      Address[] returnValue = new Address[retArr.length];
      for (int i = 0; i < retArr.length; i++)
        returnValue[i] = (Address) retArr[i];

      return returnValue;
    } else
      return addresses;
  }

  public String getName() {
    return name;
  }

  public String getUserProperty() {
    return "UserProfile." + getName();
  }

  public String getItemID() {
    return getName();
  }

  public String getItemProperty() {
    return getUserProperty();
  }

  public Properties getMailProperties() {
    return mailProperties;
  }

  public String toString() {
    return name;
  }

  /*
    public URLName getSendMailURL() {
    return sendMailURL;
    }

    public String getSendPrecommand() {
    return sendPrecommand;
    }
  */

  public FolderInfo getSentFolder() {
    if (sentFolder == null)
      loadSentFolder();

    return sentFolder;
  }

  public void setSentFolderName(String newValue) {
    sentFolderName = newValue;

    loadSentFolder();
  }

  public OutgoingMailServer getMailServer() {
    if (mailServer == null)
      loadMailServer();

    return mailServer;
  }

  public void setMailServerName(String newValue) {
    mailServerName = newValue;

    loadMailServer();
  }

  /**
   * Sets a mail server for this session.
   */
  public void setTemporaryMailServer(OutgoingMailServer pTempServer) {
    mTempMailServer = pTempServer;

    loadMailServer();
  }

  /**
   * Returns the default domain.  This will be appended to any email
   * address which doesn't include a domain.
   */
  public String getDefaultDomain() {
    if (defaultDomain == null || defaultDomain.equals(""))
      return defaultDefaultDomain;
    else
      return defaultDomain;
  }

  /**
   * Sets the default domain.  This is what will be added to email
   * addresses that don't include a domain.
   */
  public void setDefaultDomain(String newDomain) {
    defaultDomain = newDomain;
  }

  /**
   * Loads the sent folder from the UserProfile.username.sentFolder
   * property.
   */
  public void loadSentFolder() {
    StoreManager sm = Pooka.getStoreManager();
    if (sm != null) {
      sentFolder = Pooka.getStoreManager().getFolder(sentFolderName);

      if (sentFolder != null) {
        sentFolder.setSentFolder(true);
      }
    }
  }

  /**
   * Loads the MailServer from the UserProfile.username.mailServer
   * property.
   */
  public void loadMailServer() {
    if (mTempMailServer != null) {
      mailServer = mTempMailServer;

      return;
    }

    mailServer = Pooka.getOutgoingMailManager().getOutgoingMailServer(mailServerName);
    if (mailServer == null) {
      mailServer = Pooka.getOutgoingMailManager().getDefaultOutgoingMailServer();
    }
  }


  /**
   * Creates the signatureGenerator for this Profile.
   */
  public SignatureGenerator createSignatureGenerator() {
    try {
      String classname = Pooka.getProperty("UserProfile." + name + ".sigClass", Pooka.getProperty("Pooka.defaultSigGenerator", "net.suberic.pooka.FileSignatureGenerator"));
      Class sigClass = Class.forName(classname);
      SignatureGenerator sigGen = (SignatureGenerator) sigClass.newInstance();
      sigGen.setProfile(this);
      return sigGen;
    } catch (Exception e) {
      SignatureGenerator sigGen = new StringSignatureGenerator();
      sigGen.setProfile(this);
      return sigGen;
    }
  }

  /**
   * This returns a signature appropriate for the given text.
   */
  public String getSignature(String text) {
    if (sigGenerator != null)
      return sigGenerator.generateSignature(text);
    else
      return null;
  }

  /**
   * Returns the default signature for this UserProfile.  Use
   * getSignature(String text) instead.
   */
  public String getSignature() {
    if (sigGenerator != null)
      return sigGenerator.generateSignature(null);
    else
      return null;
    //return (Pooka.getProperty("UserProfile." + name + ".signature", null));
  }

  public void setSignature(String newValue) {
    Pooka.setProperty("UserProfile." + name + ".signature", newValue);
  }

  /**
   * Returns the default encryption key for this profile, if any.
   */
  public java.security.Key getEncryptionKey(boolean forSignature) {
    return getEncryptionKey(cryptoDefaultType, forSignature);
  }

  /**
   * Returns the default encryption key for this profile, if any.
   */
  public java.security.Key getEncryptionKey(String type, boolean forSignature) {
    String keyAlias = pgpEncryptionKeyId;
    if (net.suberic.crypto.EncryptionManager.SMIME.equalsIgnoreCase(type)) {
      keyAlias = smimeEncryptionKeyId;
    }

    if (keyAlias != null && keyAlias.length() > 0) {
      try {
        if (Pooka.getCryptoManager().privateKeyAliases(type, forSignature).contains(keyAlias)) {
          try{
            return Pooka.getCryptoManager().getPrivateKey(keyAlias, type);
          }catch(UnrecoverableKeyException uke){
            char[] passphrase = CryptoKeySelector.showPassphraseDialog(keyAlias);
            return Pooka.getCryptoManager().getPrivateKey(keyAlias, type, passphrase);
          }
        }
      } catch (Exception ee) {
        ee.printStackTrace();
      }
    }

    return null;
  }

  /**
   * Returns whether or not we want to sign messages as default for this
   * UserProfile.
   */
  public boolean getSignAsDefault() {
    return signAsDefault;
  }

  /**
   * Returns the default AddressMatcher for this UserProfile.
   */
  public AddressMatcher getAddressMatcher() {
    if (addressBook != null)
      return addressBook.getAddressMatcher();
    else {
      AddressBook defaultBook = Pooka.getAddressBookManager().getDefault();
      if (defaultBook != null)
        return defaultBook.getAddressMatcher();
      else
        return null;
    }
  }

  public AddressBook getAddressBook() {
    return addressBook;
  }
}
