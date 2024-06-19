package net.suberic.pooka;

import java.security.Key;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import net.suberic.crypto.EncryptionUtils;
import net.suberic.pooka.crypto.CryptoAttachment;
import net.suberic.pooka.crypto.KeyAttachment;
import net.suberic.pooka.crypto.SignedAttachment;

/**
 * This stores the encyrption information about a particular MessageInfo.
 */
public class MessageCryptoInfo {
  // the MessageInfo that we're analyzing.
  MessageInfo mMsgInfo;

  // the type of encryption (s/mime, pgp)
  String mEncryptionType;

  // whether or not we've checked to see if this is encrypted at all
  boolean mCheckedEncryption = false;

  // whether we've checked the signature yet
  boolean mCheckedSignature = false;

  // whether we've tried decrypting the message yet.
  boolean mCheckedDecryption = false;

  // whether or not the decryption was successful
  boolean mDecryptSuccessful = false;

  // whether the signature matches or not
  boolean mSignatureValid = false;  
  

  /**
   * Creates a MessageCryptoInfo for this given Message.
   */
  public MessageCryptoInfo(MessageInfo sourceMsg) {
    mMsgInfo = sourceMsg;
  }

  /**
   * Returns the EncryptionUtils to use with this MessageCryptoInfo.
   */
  public EncryptionUtils getEncryptionUtils() throws MessagingException {

    checkEncryptionType();

    if (mEncryptionType != null) {
      try {
        return net.suberic.crypto.EncryptionManager.getEncryptionUtils(mEncryptionType);
      } catch (java.security.NoSuchProviderException nspe) {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * Checks the encryption of this message.
   */
  void checkEncryptionType() throws MessagingException {
    synchronized(this) {
      if (! mCheckedEncryption) {
        mEncryptionType = net.suberic.crypto.EncryptionManager.checkEncryptionType((MimeMessage) mMsgInfo.getMessage());
        mCheckedEncryption = true;
      }
    }

  }

  /**
   * Returns the encryption type of this message.
   */
  public String getEncryptionType() throws MessagingException {
    checkEncryptionType();

    return mEncryptionType;
  }


  /**
   * Returns whether or not this message is signed.
   */
  public boolean isSigned() throws MessagingException {

    if (mMsgInfo.hasLoadedAttachments()) {
      //List attachments = mMsgInfo.getAttachments();
      List attachments = mMsgInfo.getAttachmentBundle().getAttachmentsAndTextPart();

      for (int i = 0 ; i < attachments.size(); i++) {
        if (attachments.get(i) instanceof SignedAttachment) {
          return true;
        }
      }

      return false;
    } else {
      EncryptionUtils utils = getEncryptionUtils();
      if (utils != null) {
        return (utils.getEncryptionStatus((MimeMessage) mMsgInfo.getMessage()) == EncryptionUtils.SIGNED);
      } else
        return false;
    }
  }

  /**
   * Returns whether or not this message is encrypted.
   */
  public boolean isEncrypted() throws MessagingException {

    if (mMsgInfo.hasLoadedAttachments()) {
      //List attachments = mMsgInfo.getAttachments();
      List attachments = mMsgInfo.getAttachmentBundle().getAttachmentsAndTextPart();
    	
      for (int i = 0 ; i < attachments.size(); i++) {
        if (attachments.get(i) instanceof CryptoAttachment) {
          return true;
        }
      }
      return false;
    } else {
      EncryptionUtils utils = getEncryptionUtils();
      if (utils != null) {
        return (utils.getEncryptionStatus((MimeMessage) mMsgInfo.getMessage()) == EncryptionUtils.ENCRYPTED);
      } else
        return false;
    }
  }

  /**
   * Returns whether or not this message has had its signature checked.
   * Returns false if the message is not signed in the first place.
   */
  public boolean hasCheckedSignature() throws MessagingException {
    if (! isSigned())
      return false;

    return mCheckedSignature;
  }

  /**
   * Returns whether or not this message has had a decryption attempt.
   * Returns false if the message is not encrypted in the first place.
   */
  public boolean hasTriedDecryption() throws MessagingException {
    if (! isEncrypted())
      return false;

    return mCheckedDecryption;
  }

  /**
   * Returns whether or not the signature is valid.  If the signature has not
   * been checked yet, returns false.
   */
  public boolean isSignatureValid() throws MessagingException {
    if (hasCheckedSignature())
      return mSignatureValid;
    else
      return false;
  }

  /**
   * Returns whether or not the signature is valid.  If <code>recheck</code>
   * is set to <code>true</code>, then checks again with the latest keys.
   */
  public boolean checkSignature(java.security.Key key, boolean recheck) throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    return checkSignature(key, recheck, true);
  }

  /**
   * Returns whether or not the signature is valid.  If <code>recheck</code>
   * is set to <code>true</code>, then checks again with the latest keys.
   */
  public boolean checkSignature(
		  java.security.Key key, boolean recheck, boolean changeStatusOnFailure) 
  throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    if (recheck || ! hasCheckedSignature()) {
      EncryptionUtils cryptoUtils = getEncryptionUtils();

      //mSignatureValid =  cryptoUtils.checkSignature((MimeMessage)mMsgInfo.getMessage(), key);
      // List attachments = mMsgInfo.getAttachments();
      List attachments = mMsgInfo.getAttachmentBundle().getAttachmentsAndTextPart();

      for (int i = 0; i < attachments.size(); i++) {
        Attachment current = (Attachment) attachments.get(i);
        if (current instanceof SignedAttachment) {
        	mSignatureValid = ((SignedAttachment) current).checkSignature(
        			cryptoUtils, key);
        }
      }     
      
      if (mSignatureValid || changeStatusOnFailure)
        mCheckedSignature = true;
    }
    
    return mSignatureValid;
  }

  /**
   * Tries to decrypt the message using the given Key.
   */
  public boolean decryptMessage(java.security.Key key, boolean recheck)
    throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    return decryptMessage(key, recheck, true);
  }

  /**
   * Tries to decrypt the message using the given Key.
   */
  public boolean decryptMessage(java.security.Key key, boolean recheck, boolean changeStatusOnFailure)
    throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    synchronized(this) {
      if (mCheckedDecryption && ! recheck) {
        return mDecryptSuccessful;
      } else {
        if (changeStatusOnFailure)
          mCheckedDecryption = true;

        // run through all of the attachments and decrypt them.
        AttachmentBundle bundle = mMsgInfo.getAttachmentBundle();
        List attachmentList = bundle.getAttachmentsAndTextPart();
        for (int i = 0; i < attachmentList.size(); i++) {
          Object o = attachmentList.get(i);
          if (o instanceof CryptoAttachment) {
            CryptoAttachment ca = (CryptoAttachment) o;

            if (! ca.decryptedSuccessfully()) {
              // FIXME
              EncryptionUtils cryptoUtils = getEncryptionUtils();

              BodyPart bp = ca.decryptAttachment(cryptoUtils, key);
              MailUtilities.handlePart((MimeBodyPart) bp, bundle);
            }
          }
        }

        mDecryptSuccessful = true;
        mCheckedDecryption = true;
      }
    }

    return mDecryptSuccessful;
  }

  /**
   * Tries to decrypt the Message using all available cached keys.
   */
  public boolean autoDecrypt(UserProfile defaultProfile) {
    try {
      String cryptType = getEncryptionType();

      // why not just try all of the private keys?  at least, all the
      // ones we have available.
      //java.security.Key[] privateKeys = Pooka.getCryptoManager().getCachedPrivateKeys(cryptType);
      PookaEncryptionManager encManager = Pooka.getCryptoManager();
      
      Address[] recipients = this.getMessageInfo().getMessage().getAllRecipients();
      
      boolean forSignature = false;
      // Try first the recipients' private key
      for (int i = 0; i < recipients.length; i++) {
          Key[] keys = encManager.getPrivateKeysForAddress(
        		  ((InternetAddress) recipients[i]).getAddress(), 
        		  getEncryptionType(),
        		  forSignature);
          for (int j = 0; j < keys.length; j++) {
        	  try{
                if (decryptMessage(keys[j], true, false))
                  return true;
        	  }catch(Exception e){
        		  ;//Do nothing
        	  }
          }
      }
      
      // Try the sender's private key
      Message msg = this.getMessageInfo().getMessage();
      Address[] senders = msg.getFrom();
      Address[] receivers = msg.getAllRecipients();
      
      // Try first the recipients' private key
      for (int i = 0; i < senders.length + receivers.length; i++) {
    	  Address address = (i < senders.length)?
    			  senders[i] : receivers[i-senders.length];
    	  
          Key[] keys = encManager.getPrivateKeysForAddress(
        		  ((InternetAddress) address).getAddress(), 
        		  getEncryptionType(),
        		  forSignature);
          if(keys != null){
	          for (int j = 0; j < keys.length; j++) {
	        	try{
	              if (decryptMessage(keys[j], true, false))
	                  return true;
        	    }catch(Exception e){
        		  ;//Do nothing
        	    }
	          }
          }
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return false;
  }

  /**
   * Checks the signature of the given message as compared to the
   * given from address.
   */
  public boolean autoCheckSignature(InternetAddress sender) {
    try {
      String senderAddress = sender.getAddress();
      Key[] matchingKeys = Pooka.getCryptoManager().getPublicKeys(
    		  senderAddress,getEncryptionType(), true);
     
      for (int i = 0 ; i < matchingKeys.length; i++) {
    	  mSignatureValid = checkSignature(matchingKeys[i], true, true); 
        if (mSignatureValid) {
          return mSignatureValid;
        }
      }
    } catch (Exception e) {
    	e.printStackTrace();
    }
    
    return false;
  }

  /**
   * Extracts the (public) keys from the message.
   */
  public Key[] extractKeys() throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    synchronized(this) {
      AttachmentBundle bundle = mMsgInfo.getAttachmentBundle();
      List attachmentList = bundle.getAttachmentsAndTextPart();
      for (int i = 0; i < attachmentList.size(); i++) {
        Object o = attachmentList.get(i);
        if (o instanceof KeyAttachment) {
          EncryptionUtils utils = getEncryptionUtils();
          return ((KeyAttachment) o).extractKeys(utils);
        }
      }
    }

    return null;
  }

  /**
   * Returns true if this has been decrypted successfully.
   */
  public boolean isDecryptedSuccessfully() {
    return mDecryptSuccessful;
  }

  /**
   * Returns the MessageInfo.
   */
  public MessageInfo getMessageInfo() {
    return mMsgInfo;
  }
}
