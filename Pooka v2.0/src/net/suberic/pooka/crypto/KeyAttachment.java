package net.suberic.pooka.crypto;

import net.suberic.pooka.*;
import net.suberic.crypto.*;

import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.DataHandler;

import java.security.Key;

import java.io.*;

/**
 * A signed attachment.
 */
public class KeyAttachment extends Attachment {

  boolean parsed = false;

  /**
   * Creates a KeyAttachment out of a MimePart.
   */
  public KeyAttachment(MimePart mp) throws MessagingException {
    super(mp);
  }

  /**
   * Returns the attached keys.
   */
  public Key[] extractKeys(EncryptionUtils utils) throws MessagingException, java.io.IOException, java.security.GeneralSecurityException {
    net.suberic.crypto.UpdatableMBP mbp = new net.suberic.crypto.UpdatableMBP();

    mbp.setContent(getDataHandler().getContent(), getMimeType().toString());
    mbp.updateMyHeaders();
    
    if (utils == null) {
      utils = net.suberic.crypto.EncryptionManager.getEncryptionUtils(mbp);
    }

    if (utils != null) 
      return utils.extractKeys(mbp);
    else
      return null;
  }

}
