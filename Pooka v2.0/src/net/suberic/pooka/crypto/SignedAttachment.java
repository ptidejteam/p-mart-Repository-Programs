package net.suberic.pooka.crypto;

import java.security.Key;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import net.suberic.crypto.EncryptionUtils;
import net.suberic.crypto.PGPEncryptionUtils;
import net.suberic.pooka.Attachment;

/**
 * A signed attachment.
 */
public class SignedAttachment extends Attachment {

  boolean parsed = false;

  /**
   * Creates a SignedAttachment out of a MimePart.
   */
  public SignedAttachment(MimePart mp) throws MessagingException {
    super(mp);
  }

  /**
   * Returns if the signature matches.
   */
  public boolean checkSignature(EncryptionUtils utils, Key key)
    throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {

    Object content = getDataHandler().getContent();
    if (content instanceof MimeMultipart) {
      MimeMultipart mp = (MimeMultipart) content;
      return utils.checkSignature(mp, key);
    } /* else if((utils instanceof PGPEncryptionUtils) && (content instanceof String)){
      String s = (String) content;
      if (s.indexOf(PGPEncryptionUtils.BEGIN_PGP_SIGNED_MESSAGE) == 0) {
        PGPEncryptionUtils pgpUtils = (PGPEncryptionUtils) utils;
        return pgpUtils.checkSignature(s, key);
      }
    }
      */
    return false;
  }

  /**
   * Returns the content part of the signed attachment.
   */
  public MimeBodyPart getSignedPart() throws javax.mail.MessagingException,
                                             java.io.IOException {
    Object content = getDataHandler().getContent();
    if (content instanceof MimeMultipart) {
      MimeMultipart mm = (MimeMultipart) content;

      // this should be exactly two parts, one the content, the other the
      // signature.
      for (int i = 0; i < mm.getCount(); i++) {
        // return the first one found.
        MimeBodyPart mbp = (MimeBodyPart) mm.getBodyPart(i);
        ContentType ct = new ContentType(mbp.getContentType());
        if (! ct.getSubType().toLowerCase().endsWith("signature")) {
          return mbp;
        }
      }
    } /*
    else if(content instanceof String){
      String s = (String) content;
      if(s.startsWith(PGPEncryptionUtils.BEGIN_PGP_SIGNED_MESSAGE)){
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setText(PGPEncryptionUtils.getSignedContent(s));
        return mbp;
      }
    }
      */
    return null;
  }

  /**
   * Returns the DataHandler for this Attachment.
   */
  public DataHandler getDataHandler() {
    return super.getDataHandler();
  }

  public boolean isPlainText() {
    return false;
  }

  public boolean isText() {
    return false;
  }


  /**
   * Returns the MimeType.
   */
  /*
    public ContentType getMimeType() {
    try {
    return new ContentType("text/plain");
    } catch (javax.mail.internet.ParseException pe) {
    return null;
    }
    }
  */



}
