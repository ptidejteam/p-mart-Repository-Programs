package net.suberic.pooka.crypto;

import net.suberic.pooka.*;
import net.suberic.crypto.*;

import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.DataHandler;

import java.security.Key;

import java.io.*;

/**
 * An encrypted attachment.
 */
public class CryptoAttachment extends Attachment {

  boolean parsed = false;

  boolean encrypted = false;

  boolean signed = false;

  BodyPart decryptedBodyPart = null;

  DataHandler msgDataHandler = null;

  /**
   * Creates a CryptoAttachment out of a MimePart.
   */
  public CryptoAttachment(MimePart mp) throws MessagingException {
    super(mp);
    ContentType ct = new ContentType(mp.getContentType());
    if (ct.getSubType().equalsIgnoreCase("encrypted"))
      encrypted = true;
    else if (ct.getSubType().equalsIgnoreCase("signed"))
      signed = true;
    else if (ct.getPrimaryType().equalsIgnoreCase("application") && ct.getSubType().equalsIgnoreCase("pkcs7-mime")) {
      encrypted = true;
    }/* else {
      try {
        Object content = mp.getContent();
        if (content instanceof String){
          if (((String) content).indexOf(PGPEncryptionUtils.BEGIN_PGP_MESSAGE) == 0){
            encrypted = true;
          }
        }
      }catch(IOException ie){
        ;
      }
      } */
  }

  /**
   * Tries to decrypt this Attachment.
   */
  public BodyPart decryptAttachment(EncryptionUtils utils, Key key)
    throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {

    if (decryptedBodyPart != null)
      return decryptedBodyPart;
    else {
      MimeBodyPart mbp = new MimeBodyPart();
      mbp.setDataHandler(super.getDataHandler());
      mbp.setHeader("Content-Type", super.getDataHandler().getContentType());

      decryptedBodyPart = utils.decryptBodyPart(mbp, key);

      return decryptedBodyPart;
    }

  }

  // accessor methods.

  /**
   * Returns the text of the Attachment, up to maxLength bytes.  If
   * the content is truncated, then append the truncationMessage at the
   * end of the content displayed.
   *
   * If withHeaders is set, then show the Headers to go with this message.
   * If showFullHeaders is also set, then show all the headers.
   */
  public String getText(boolean withHeaders, boolean showFullHeaders, int maxLength, String truncationMessage) throws java.io.IOException {
    StringBuffer retVal = new StringBuffer();
    if (withHeaders)
      retVal.append(getHeaderInformation(showFullHeaders));

    retVal.append(Pooka.getProperty("Pooka.crypto.encryptedMessage", "******  This is an encrypted message.  Click on the 'encryption' button or go to Encrypt->Decrypt message to read it. ******"));

    return retVal.toString();
  }

  /**
   * Returns if we have already decrypted this attachment successfully.
   */
  public boolean decryptedSuccessfully() {
    return (decryptedBodyPart != null);
  }

  public boolean isPlainText() {
    return false;
  }

  public boolean isText() {
    return false;
  }
}
