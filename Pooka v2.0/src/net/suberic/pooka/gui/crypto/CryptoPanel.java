package net.suberic.pooka.gui.crypto;

import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.suberic.pooka.Pooka;
import net.suberic.util.VariableBundle;
import net.suberic.util.gui.IconManager;

/**
 * Displays the current cryptography status for this Message.
 */
public class CryptoPanel extends JPanel implements CryptoStatusDisplay {
  
  JButton encryptionButton;
  JButton signatureButton;
  JButton importKeysButton;

  // the various icons
  static ImageIcon notEncryptedIcon;
  static ImageIcon uncheckedEncryptedIcon;
  static ImageIcon decryptedSuccessfullyIcon;
  static ImageIcon decryptedUnsuccessfullyIcon;
  static ImageIcon notSignedIcon;
  static ImageIcon uncheckedSignedIcon;
  static ImageIcon signatureVerifiedIcon;
  static ImageIcon signatureBadIcon;
  static ImageIcon signatureFailedVerificationIcon;

  static ImageIcon importKeysIcon;

  // the various tooltips
  static String notEncryptedTooltip;
  static String uncheckedEncryptedTooltip;
  static String decryptedSuccessfullyTooltip;
  static String decryptedUnsuccessfullyTooltip;
  static String notSignedTooltip;
  static String uncheckedSignedTooltip;
  static String signatureVerifiedTooltip;
  static String signatureBadTooltip;
  static String signatureFailedVerificationTooltip;

  // the various status colors
  static Color signedEncryptedColor = Color.MAGENTA;
  static Color signedColor = Color.GREEN;
  static Color encryptedColor = Color.BLUE;
  static Color uncheckedColor = Color.YELLOW;
  static Color failedColor = Color.RED;

  static boolean iconsLoaded = false;
  static boolean tooltipsLoaded = false;

  // the current status
  int currentCryptStatus = NOT_ENCRYPTED;
  int currentSigStatus = NOT_SIGNED;

  /**
   * A JPanel that shows the encryption status of this message.
   */
  public CryptoPanel() {
    super();
    if (! iconsLoaded) {
      Class thisClass = this.getClass();
      synchronized(thisClass) {
	if (! iconsLoaded) {
	  loadIcons("CryptoPanel", thisClass, Pooka.getResources());
	  iconsLoaded = true;
	}
	if (! tooltipsLoaded) {
	  loadTooltips("CryptoPanel", Pooka.getResources());
	  tooltipsLoaded = true;
	}
      }
    }

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    encryptionButton = createEncryptionButton();
    signatureButton = createSignatureButton();

    this.add(encryptionButton);
    this.add(signatureButton);
  }

  /**
   * Creates an Encryption Button.
   */
  public JButton createEncryptionButton() {
    JButton returnValue = new JButton();
    if (notEncryptedIcon != null)
      returnValue.setIcon(notEncryptedIcon);
    returnValue.setSize(25,25);
    returnValue.setPreferredSize(new java.awt.Dimension(25,25));
    returnValue.setMaximumSize(new java.awt.Dimension(25,25));
    return returnValue;
  }
  
  /**
   * Creates a Signature Button.
   */
  public JButton createSignatureButton() {
    JButton returnValue = new JButton();
    if (notSignedIcon != null)
      returnValue.setIcon(notSignedIcon);
    returnValue.setPreferredSize(new java.awt.Dimension(25,25));
    returnValue.setMaximumSize(new java.awt.Dimension(25,25));
    returnValue.setSize(25,25);
    return returnValue;
  }

  /**
   * Creates an Import Keys Button.
   */
  public JButton createImportKeysButton() {
    JButton returnValue = new JButton();
    if (importKeysIcon != null)
      returnValue.setIcon(importKeysIcon);
    returnValue.setPreferredSize(new java.awt.Dimension(25,25));
    returnValue.setMaximumSize(new java.awt.Dimension(25,25));
    returnValue.setSize(25,25);
    return returnValue;
  }
  
  /**
   * Updates the action on the given button.
   */
  public void updateAction(JButton button, Action a) {
    ActionListener[] listeners = button.getActionListeners();
    for (int i = 0; i < listeners.length; i++) {
      button.removeActionListener(listeners[i]);
    }
    
    button.addActionListener(a);
  }

  /**
   * Updates the encryption information.
   */
  public void cryptoUpdated(int newSignatureStatus, int newEncryptionStatus) {
    cryptoUpdated(newSignatureStatus, newEncryptionStatus, null);
  }

  /**
   * Updates the encryption information.
   */
  public void cryptoUpdated(int newSignatureStatus, int newEncryptionStatus, net.suberic.pooka.gui.MessageProxy proxy) {
    if (newSignatureStatus != currentSigStatus) {
      currentSigStatus = newSignatureStatus;

      if (currentSigStatus == NOT_SIGNED) {
		signatureButton.setIcon(notSignedIcon);
		signatureButton.setToolTipText(notSignedTooltip);
		if (proxy != null) {
		  Action checkSigAction = proxy.getAction("message-signature-status");
		  if (checkSigAction != null)
		    updateAction(signatureButton, checkSigAction);
		}

      } else if (currentSigStatus == UNCHECKED_SIGNED) {
		if (proxy != null) {
		  Action checkSigAction = proxy.getAction("message-check-signature");
		  if (checkSigAction != null)
		    updateAction(signatureButton, checkSigAction);
		}
		signatureButton.setIcon(uncheckedSignedIcon);
		signatureButton.setToolTipText(uncheckedSignedTooltip);
      } else if (currentSigStatus == SIGNATURE_VERIFIED) {
		signatureButton.setIcon(signatureVerifiedIcon);
		signatureButton.setToolTipText(signatureVerifiedTooltip);
		if (proxy != null) {
		  Action checkSigAction = proxy.getAction("message-signature-status");
		  if (checkSigAction != null)
		    updateAction(signatureButton, checkSigAction);
		}
      } else if (currentSigStatus == SIGNATURE_BAD) {
		signatureButton.setIcon(signatureBadIcon);
		signatureButton.setToolTipText(signatureBadTooltip);
		if (proxy != null) {
		  Action checkSigAction = proxy.getAction("message-signature-status");
		  if (checkSigAction != null)
		    updateAction(signatureButton, checkSigAction);
		}
      }
    }

    if (newEncryptionStatus != currentCryptStatus) {
      currentCryptStatus = newEncryptionStatus;

      if (currentCryptStatus == UNCHECKED_ENCRYPTED) {
	encryptionButton.setIcon(uncheckedEncryptedIcon);
	encryptionButton.setToolTipText(uncheckedEncryptedTooltip);
	if (proxy != null) {
	  Action decryptAction = proxy.getAction("message-decrypt");
	  if (decryptAction != null)
	    updateAction(encryptionButton, decryptAction);
	}
      } else if (currentCryptStatus == DECRYPTED_SUCCESSFULLY) {
	encryptionButton.setIcon(decryptedSuccessfullyIcon);
	encryptionButton.setToolTipText(decryptedSuccessfullyTooltip);
	if (proxy != null) {
	  Action decryptAction = proxy.getAction("message-encryption-status");
	  if (decryptAction != null)
	    updateAction(encryptionButton, decryptAction);
	}
      } else if (currentCryptStatus == DECRYPTED_UNSUCCESSFULLY) {
	encryptionButton.setIcon(decryptedUnsuccessfullyIcon);
	encryptionButton.setToolTipText(decryptedUnsuccessfullyTooltip);
	if (proxy != null) {
	  Action decryptAction = proxy.getAction("message-encryption-status");
	  if (decryptAction != null)
	    updateAction(encryptionButton, decryptAction);
	}
      } else {
	encryptionButton.setIcon(notEncryptedIcon);
	encryptionButton.setToolTipText(notEncryptedTooltip);
	if (proxy != null) {
	  Action decryptAction = proxy.getAction("message-encryption-status");
	  if (decryptAction != null)
	    updateAction(encryptionButton, decryptAction);
	}
      }    
      repaint();
    }
  }

  /**
   * Updates the encryption information.
   */
  public void cryptoUpdated(net.suberic.pooka.MessageCryptoInfo cryptoInfo) {

    try {
      
      int sigStatus = NOT_SIGNED;
      int cryptStatus = NOT_ENCRYPTED;
      
      if (cryptoInfo.isSigned()) {
       if (cryptoInfo.hasCheckedSignature()) {
	     if (cryptoInfo.isSignatureValid()) {
	       sigStatus = SIGNATURE_VERIFIED;
	     } else {
	       sigStatus = SIGNATURE_BAD;
	    }
	   } else {
	    sigStatus = UNCHECKED_SIGNED;
	   }
      }

      if (cryptoInfo.isEncrypted()) {
	if (cryptoInfo.hasTriedDecryption()) {
	  if (cryptoInfo.isDecryptedSuccessfully()) {
	    cryptStatus = DECRYPTED_SUCCESSFULLY;
	  } else {
	    cryptStatus = DECRYPTED_UNSUCCESSFULLY;
	  }
	} else {
	  cryptStatus = UNCHECKED_ENCRYPTED;
	}
      }
      
      net.suberic.pooka.gui.MessageProxy proxy = null;
      net.suberic.pooka.MessageInfo info = cryptoInfo.getMessageInfo();
      if (info != null)
	proxy = info.getMessageProxy();

      cryptoUpdated(sigStatus, cryptStatus, proxy);

    } catch (javax.mail.MessagingException me) {
      // ignore here.
    }
  }

  /**
   * This loads all of the icons for this button.
   */
  static void loadIcons(String key, Class thisClass, VariableBundle vars) {

    /*
     * this is going to have several images:
     * Unchecked Encrypted
     * Decrypted Successfully
     * Decrypted Unsuccessfully
     * Unchecked Signed
     * Signature verified
     * Signature bad
     * Signature failed verification
     * ...and maybe more.
     */

    IconManager iconManager = Pooka.getUIFactory().getIconManager();
    
    notEncryptedIcon = new ImageIcon();
    //notEncryptedIcon = iconManager.getIcon(Pooka.getProperty(key + ".notEncryptedIcon", "UnLock"));
    uncheckedEncryptedIcon = iconManager.getIcon(Pooka.getProperty(key + ".uncheckedEncryptedIcon", "Lock"));
    decryptedSuccessfullyIcon = iconManager.getIcon(Pooka.getProperty(key + ".decryptedSuccessfullyIcon", "OpenLock"));
    decryptedUnsuccessfullyIcon = iconManager.getIcon(Pooka.getProperty(key + ".decryptedUnsuccessfullyIcon", "Bomb"));
    uncheckedSignedIcon = iconManager.getIcon(Pooka.getProperty(key + ".uncheckedSignedIcon", "Draw"));
    
    notSignedIcon = new ImageIcon();
    //notSignedIcon = iconManager.getIcon(Pooka.getProperty(key + ".notSignedIcon", "EnvelopeOpen"));
    signatureVerifiedIcon = iconManager.getIcon(Pooka.getProperty(key + ".signatureVerifiedIcon", "Check"));
    signatureBadIcon = iconManager.getIcon(Pooka.getProperty(key + ".signatureBadIcon", "Caution"));
    signatureFailedVerificationIcon = iconManager.getIcon(Pooka.getProperty(key + ".signatureFailedVerificationIcon", "Caution"));
  }

  /**
   * This loads all of the tooltips for this button.
   */
  static void loadTooltips(String key, VariableBundle vars) {

    /*
     * this is going to have several tooltips:
     * Unchecked Encrypted
     * Decrypted Successfully
     * Decrypted Unsuccessfully
     * Unchecked Signed
     * Signature verified
     * Signature bad
     * Signature failed verification
     * ...and maybe more.
     */


//    notEncryptedTooltip = vars.getProperty(key + ".notEncrypted.Tooltip", "NotEncrypted");
    
    uncheckedEncryptedTooltip = vars.getProperty(key + ".uncheckedEncrypted.Tooltip", "Encrypted Message");
    decryptedSuccessfullyTooltip = vars.getProperty(key + ".decryptedSuccessfully.Tooltip", "Message Decrypted with Key ");
    decryptedUnsuccessfullyTooltip = vars.getProperty(key + ".decryptedUnsuccessfully.Tooltip", "Message Failed Decryption");

//    uncheckedSignedTooltip = vars.getProperty(key + ".uncheckedSigned.Tooltip");
    notSignedTooltip = vars.getProperty(key + ".notSigned.Tooltip", "Not Signed");
    signatureVerifiedTooltip = vars.getProperty(key + ".signatureVerified.Tooltip", "Signature Verified with Key ");
    signatureBadTooltip = vars.getProperty(key + ".signatureBad.Tooltip", "Signature Failed Verification by Key ");
    signatureFailedVerificationTooltip = vars.getProperty(key + ".signatureFailedVerification.Tooltip", "Unable to Verfify Signature");
  }
  
  public int getEncryptionStatus() {
		return currentCryptStatus;
	}

	public int getSignatureStatus() {
		return currentSigStatus;
	}


}
    
