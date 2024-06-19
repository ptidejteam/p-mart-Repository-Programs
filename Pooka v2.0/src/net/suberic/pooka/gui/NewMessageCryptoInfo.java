package net.suberic.pooka.gui;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import net.suberic.crypto.EncryptionKey;
import net.suberic.crypto.EncryptionManager;
import net.suberic.crypto.EncryptionUtils;
import net.suberic.pooka.MessageCryptoInfo;
import net.suberic.pooka.NewMessageInfo;
import net.suberic.pooka.Pooka;
import net.suberic.pooka.PookaEncryptionManager;
import net.suberic.pooka.UserProfile;
import net.suberic.pooka.gui.crypto.CryptoKeySelector;
//import net.suberic.pooka.gui.crypto.NewMessageCryptoDisplay;

/**
 * Encapsulates the encryption info for a new message.
 */
public class NewMessageCryptoInfo extends MessageCryptoInfo {
  int cryptoType = NO_CRYPTO;

  public static final int NO_CRYPTO       = 0;
  public static final int SMIME_SIGN      = 1;
  public static final int SMIME_ENCRYPT   = 2;
  public static final int SMIME_BOTH      = 3;
  public static final int PGP_SIGN    = 4;
  public static final int PGP_ENCRYPT = 5;
  public static final int PGP_BOTH    = 6;

  public void setCryptoType(int type){
    cryptoType = type;
  }

  List mAttachKeys = new LinkedList();

  List mRecipientMatches = new LinkedList();

  public static int CRYPTO_YES = 0;
  public static int CRYPTO_DEFAULT = 5;
  public static int CRYPTO_NO = 10;

  // whether or not we want to encrypt this message.
  int mEncryptMessage = CRYPTO_DEFAULT;

  // whether or not we want to sign this message
  int mSignMessage = CRYPTO_DEFAULT;

  // the configured list of recipients.
  CryptoRecipientsInfo mRecipientsInfo;

  /**
   * Creates a new NewMessageCryptoInfo.
   */
  public NewMessageCryptoInfo(NewMessageInfo nmi) {
    super(nmi);
  }

  // keys

  // the signature key.
  Key mSignatureKey = null;

  // the encryption key
  Key[] mEncryptionKeys = null;

  /**
   * The Signature Key for this set of recipients.
   */
  public Key getSignatureKey() {
    return mSignatureKey;
  }

  /**
   * Sets the encryption key for encrypting this message.
   */
  public void setSignatureKey(Key pSignatureKey) {
    mSignatureKey = pSignatureKey;
  }

  /**
   * Sets the encryption key for encrypting this message.
   */
  public void setEncryptionKeys(Key[] pEncryptionKeys) {
    mEncryptionKeys = pEncryptionKeys;
  }

  /**
   * Gets the encryption key we're using for this message.
   */
  public Key[] getEncryptionKeys() {
    return mEncryptionKeys;
  }

  // sign message.

  /**
   * Returns whether we're planning on signing this message or not.
   */
  public int getSignMessage() {
    return mSignMessage;
  }

  /**
   * Sets whether or not we want to sign this message.
   */
  public void setSignMessage(int pSignMessage) {
    mSignMessage = pSignMessage;
  }

  // encrypt message.

  /**
   * Returns whether we're planning on encrypting this message or not.
   */
  public int getEncryptMessage() {
    return mEncryptMessage;
  }

  /**
   * Sets whether or not we want to encrypt this message.
   */
  public void setEncryptMessage(int pEncryptMessage) {
    mEncryptMessage = pEncryptMessage;
  }

  // attach keys.

  /**
   * Attaches an encryption key to this message.
   */
  public synchronized void attachEncryptionKey(Key key) {
    if (! mAttachKeys.contains(key))
      mAttachKeys.add(key);
  }

  /**
   * Attaches an encryption key to this message.
   */
  public synchronized void removeEncryptionKey(Key key) {
    if (mAttachKeys.contains(key)) {
      mAttachKeys.remove(key);
    }

  }

  /**
   * Returns the keys to be attached.
   */
  public List getAttachKeys() {
    return new LinkedList(mAttachKeys);
  }

  // methods.

  /**
   * Creates the attached key parts for this message.
   */
  public List createAttachedKeyParts() {
    LinkedList keyParts = new LinkedList();
    List attachKeys = getAttachKeys();
    if (attachKeys != null) {
      for (int i = 0; i < attachKeys.size(); i++) {
        EncryptionKey currentKey = (EncryptionKey)attachKeys.get(i);
        try {
          EncryptionUtils utils = currentKey.getEncryptionUtils();
          keyParts.add(utils.createPublicKeyPart(new Key[] { currentKey }));
        } catch (Exception e) {
          // FIXME ignore for now.
          System.out.println("caught exception adding key to message:  " + e);
          e.printStackTrace();
        }
      }
    }

    return keyParts;
  }

  /**
   * Returns the encrypted and/or signed message(s), as appropriate.
   */
  public MimeMessage createEncryptedMessage(UserProfile profile, MimeMessage mm) throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    if (cryptoType == NO_CRYPTO)
      return mm;

    Key signKey = null;

    signKey = getSignatureKey();
    if (signKey == null){
      switch (cryptoType) {
      case SMIME_SIGN:
      case SMIME_BOTH:
        signKey = profile.getEncryptionKey(EncryptionManager.SMIME, true);
        break;
      case PGP_SIGN:
      case PGP_BOTH:
        signKey = profile.getEncryptionKey(EncryptionManager.PGP, true);
        break;
      }
    }

    PookaEncryptionManager cryptoManager = Pooka.getCryptoManager();

    InternetAddress from = (InternetAddress) mm.getFrom()[0];

    // Find the keys to sign and encrypt the messages
    if(signKey == null && (cryptoType == SMIME_SIGN || cryptoType == SMIME_BOTH)){
      Key[] keys = cryptoManager.getPrivateKeysForAddress(from.getAddress(), EncryptionManager.SMIME,  true);
      if (keys == null || keys.length == 0) {
        // show dialog
        signKey = CryptoKeySelector.selectPrivateKey(Pooka.getProperty("Pooka.crypto.privateKey.forSign", "Select key to sign this message."), EncryptionManager.SMIME, true);
      } else {
        signKey = keys[0];
      }

      if (signKey == null) {
        throw new GeneralSecurityException("No signature key selected.");
      }
    }

    if(signKey == null &&  (cryptoType == PGP_SIGN || cryptoType == PGP_BOTH)){
      Key[] keys = cryptoManager.getPrivateKeysForAddress(from.getAddress(), EncryptionManager.PGP,  true);
      if (keys == null || keys.length == 0) {
        // show dialog
        signKey = CryptoKeySelector.selectPrivateKey(Pooka.getProperty("Pooka.crypto.privateKey.forSign", "Select key to sign this message."), EncryptionManager.PGP, true);

      } else {
        signKey = keys[0];
      }

      if (signKey == null) {
        throw new GeneralSecurityException("No signature key selected.");
      }
    }

    List encKeys = new LinkedList();

    //TODO: get the encKey from the available public keys
    if (cryptoType == SMIME_ENCRYPT || cryptoType == SMIME_BOTH || cryptoType == PGP_ENCRYPT || cryptoType == PGP_BOTH) {
      String type = (cryptoType == SMIME_ENCRYPT || cryptoType == SMIME_BOTH) ? EncryptionManager.SMIME : EncryptionManager.PGP;
      // Get the public key of the senders
      Address[] froms = mm.getFrom();
      for (int i = 0; i < froms.length; i++) {
        from = (InternetAddress) froms[i];
        Key[] keys = cryptoManager.getPublicKeys(from.getAddress(), type, false);
        if (keys != null && keys.length > 0) {
          encKeys.add(keys[0]);
        }
      }

      // Get the public key of the receivers
      Address[] receivers = mm.getAllRecipients();
      for (int i = 0; i < receivers.length; i++) {
        InternetAddress rec = (InternetAddress) receivers[i];
        Key[] keys = cryptoManager.getPublicKeys(rec.getAddress(), type, false);
        if (keys != null && keys.length > 0) {
          encKeys.add(keys[0]);
        } else {
          Key key = CryptoKeySelector.selectPublicKey(Pooka.getProperty("Pooka.crypto.publicKey.forEncrypt", "Select key to encrypt this message."), EncryptionManager.PGP, false);
          if (key != null)
            encKeys.add(key);
          else
            throw new GeneralSecurityException("found no certificate for " + rec.getAddress());
        }
      }
      /*Key encKey = CryptoKeySelector.selectPublicKey(
        Pooka.getProperty("Pooka.crypto.publicKey.forEncrypt",
        "Select key to encrypt this message."),
        EncryptionManager.SMIME, false);
      */
    }

    if (encKeys.size() > 0) {
      Key[] encKeysArray = (Key[]) encKeys.toArray(new Key[0]);
      mRecipientsInfo.setEncryptionKeys(encKeysArray);
    }


    if (signKey != null) {
      mRecipientsInfo.setSignatureKey(signKey);
    }

    return mRecipientsInfo.handleMessage(mm);

  }

  /**
   * Returns the configured CryptoRecipientInfos.
   */
  public CryptoRecipientsInfo getCryptoRecipientsInfo() {
    return mRecipientsInfo;
  }

  /**
   * Updates the CryptoRecipientInfos with information from the
   * MessageUI.
   */
  public boolean updateRecipientInfos(UserProfile profile, InternetHeaders headers) throws javax.mail.internet.AddressException, javax.mail.MessagingException {
    // just use the defaults for now.

    String toHeader = headers.getHeader("To", ",");
    if (toHeader == null) {
      throw new MessagingException(Pooka.getProperty("error.NewMessage.noTo", "No To: recipient"));
    }
    InternetAddress[] toAddresses = InternetAddress.parse(headers.getHeader("To", ","), false);
    if (toAddresses == null || toAddresses.length == 0) {
      throw new MessagingException(Pooka.getProperty("error.NewMessage.noTo", "No To: recipient"));
    }

    String ccHeaderLine = headers.getHeader("CC", ",");
    InternetAddress[] ccAddresses;
    if (ccHeaderLine != null && ccHeaderLine.length() > 0) {
      ccAddresses = InternetAddress.parse(ccHeaderLine, false);
    } else {
      ccAddresses = new InternetAddress[0];
    }

    String bccHeaderLine = headers.getHeader("BCC", ",");
    InternetAddress[] bccAddresses;
    if (bccHeaderLine != null && bccHeaderLine.length() > 0) {
      bccAddresses = InternetAddress.parse(bccHeaderLine, false);
    } else {
      bccAddresses = new InternetAddress[0];
    }

    Key[] cryptKeys = null;

    if (getEncryptMessage() != CRYPTO_NO)
      cryptKeys = getEncryptionKeys();

    Key sigKey = null;
    if (getSignMessage() != CRYPTO_NO)
      sigKey = getSignatureKey();

    mRecipientsInfo = new CryptoRecipientsInfo(sigKey, cryptKeys, toAddresses, ccAddresses, bccAddresses);

    return true;
  }

  // Recipient/encryption key matches.
  /**
   * This represents a match between a recipient set and an encryption
   * configuration.  The assumption is that all of the following recipients
   * can receive the same message.
   */
  public class CryptoRecipientsInfo {
    // the signature key.
    Key mSignatureKey = null;

    // the encryption key
    Key[] mEncryptionKeys = null;

    // the recipients
    Address[] toList = null;
    Address[] ccList = null;
    Address[] bccList = null;

    /**
     * Creteas a new CryptoRecipieintInfo.
     */
    public CryptoRecipientsInfo() {

    }

    /**
     * Creates a new CryptoRecipieintInfo with the given signatureKey,
     * encryptionKey, toList, ccList, and bccList.
     */
    public CryptoRecipientsInfo(Key pSignatureKey, Key[] pEncryptionKeys,
                                Address[] pToList, Address[] pCcList, Address[] pBccList) {

      setEncryptionKeys(pEncryptionKeys);
      setSignatureKey(pSignatureKey);

      setRecipients(pToList, Message.RecipientType.TO);
      setRecipients(pCcList, Message.RecipientType.CC);
      setRecipients(pBccList, Message.RecipientType.BCC);
    }


    /**
     * The recipients for this crypto configuration.
     */
    public Address[] getRecipients(Message.RecipientType type) {
      if (type == Message.RecipientType.TO)
        return toList;
      else if (type == Message.RecipientType.CC)
        return ccList;
      else if (type == Message.RecipientType.BCC)
        return bccList;
      else
        return null;
    }

    /**
     * The recipients for this crypto configuration.
     */
    public Address[] getAllRecipients() {
      Address[] returnValue = new Address[0];
      returnValue = appendToArray(returnValue, toList);
      returnValue = appendToArray(returnValue, ccList);
      returnValue = appendToArray(returnValue, bccList);

      return returnValue;
    }

    /**
     * Appends to an array of Addresses.
     */
    private Address[] appendToArray(Address[] original, Address[] toAdd) {
      if (toAdd != null && toAdd.length > 0) {
        int oldSize = original.length;
        Address[] newReturnValue = new Address[original.length + toAdd.length];
        System.arraycopy(original, 0, newReturnValue, 0, original.length);
        System.arraycopy(toAdd, 0, newReturnValue, original.length, toAdd.length);
        return newReturnValue;
      } else {
        return original;
      }
    }

    /**
     * Sets the recipients for the particular type.
     */
    public void setRecipients(Address[] pRecipients, Message.RecipientType type) {
      if (type == Message.RecipientType.TO)
        toList = pRecipients;
      else if (type == Message.RecipientType.CC)
        ccList = pRecipients;
      else if (type == Message.RecipientType.BCC)
        bccList = pRecipients;

    }

    /**
     * The Signature Key for this set of recipients.
     */
    public Key getSignatureKey() {
      return mSignatureKey;
    }

    /**
     * Sets the encryption key for encrypting this message.
     */
    public void setSignatureKey(Key pSignatureKey) {
      mSignatureKey = pSignatureKey;
    }

    /**
     * Sets the encryption key for encrypting this message.
     */
    public void setEncryptionKeys(Key[] pEncryptionKeys) {
      mEncryptionKeys = pEncryptionKeys;
    }

    /**
     * Gets the encryption key we're using for this message.
     */
    public Key[] getEncryptionKeys() {
      return mEncryptionKeys;
    }

    /**
     * Creates a new MimeMessage using the given recipients and encryption.
     */
    public MimeMessage handleMessage(MimeMessage mm)
      throws MessagingException, java.io.IOException, java.security.GeneralSecurityException  {
      //MimeMessage returnValue = new MimeMessage(mm);

      /*
        returnValue.setRecipients(Message.RecipientType.TO, getRecipients(Message.RecipientType.TO));
        returnValue.setRecipients(Message.RecipientType.CC, getRecipients(Message.RecipientType.CC));
        returnValue.setRecipients(Message.RecipientType.BCC, getRecipients(Message.RecipientType.BCC));
      */

      Key sigKey = getSignatureKey();
      Key[] cryptoKeys = getEncryptionKeys();

      /*      if (sigKey instanceof EncryptionKey && cryptoKey instanceof EncryptionKey) {
              if (((EncryptionKey)sigKey).getType() != ((EncryptionKey)cryptoKey).getType()) {
              throw new MessagingException(Pooka.getProperty("error.NewMessage.differentEncryption", "Encryption and Signature Keys must be of same type (PGP or S/MIME)"));
              }
              }
      */
      PookaEncryptionManager cryptoManager = Pooka.getCryptoManager();

      if (getSignatureKey() != null) {
        mm = cryptoManager.signMessage(mm, null, sigKey);
      }

      if (cryptoKeys != null) {
        mm = cryptoManager.encryptMessage(mm,
                                          cryptoKeys);
      }

      return mm;
    }
  }

}
