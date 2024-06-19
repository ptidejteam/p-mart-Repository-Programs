package net.suberic.pooka.gui.crypto;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import net.suberic.util.VariableBundle;
import net.suberic.util.gui.ConfigurableUI;

/**
 * This defines a UI component which may be built dynamically using a 
 * set of properties in a VariableBundle, and then may have the Actions
 * associated with the individual buttons/menu items/whatevers updated
 * dynamically to reflect the new values.
 *
 * In general, the format for the properties which define a ConfigurableUI
 * component are as follows:
 * 
 * MenuBar=File:Edit:Mail:Window:Help
 *
 * MenuBar.File=NewFolder:NewMail:OpenFolder:OpenMessage:Close:SaveAs:Print:Exit
 * MenuBar.File.Label=File
 * 
 * MenuBar.File.NewFolder.Action=folder-new
 * MenuBar.File.NewFolder.Image=images/New.gif
 * MenuBar.File.NewFolder.KeyBinding=F
 * MenuBar.File.NewFolder.Label=New Folder
 *
 * where MenuBar would be the name of the 'root' configuration property,
 * 'MenuBar.File' is the first submenu, and 'MenuBar.File.NewFolder' is 
 * the first actual 'button' configured.  On the NewFolder MenuItem, the
 * 'Action' is the name of the Action which will be run, and is the 
 * central part of the configuration.  The rest (Image, KeyBinding, and 
 * Label) just control how the item is displayed and invoked.  The
 * 'KeyBinding' and 'Label' items should probably be put in a localized
 * file if you want to internationalize your application.
 */

public class CryptoButton extends JButton implements ConfigurableUI, CryptoStatusDisplay {

  // the various icons
  ImageIcon notEncryptedIcon;
  ImageIcon uncheckedEncryptedIcon;
  ImageIcon decryptedSuccessfullyIcon;
  ImageIcon decryptedUnsuccessfullyIcon;
  ImageIcon uncheckedSignedIcon;
  ImageIcon signatureVerifiedIcon;
  ImageIcon signatureBadIcon;
  ImageIcon signatureFailedVerificationIcon;

  // the various status colors
  Color signedEncryptedColor = Color.MAGENTA;
  Color signedColor = Color.GREEN;
  Color encryptedColor = Color.BLUE;
  Color uncheckedColor = Color.YELLOW;
  Color failedColor = Color.RED;

  // the current status
  int currentCryptStatus = NOT_ENCRYPTED;
  int currentSigStatus = NOT_SIGNED;

  Action currentAction = null;

  public CryptoButton () {
    super();
  }

  /**
   * This creates a new CryptoButton using the buttonID as the
   * configuration key, and vars as the source for the values of all the
   * properties.
   *
   * If buttonID doesn't exist in vars, then this returns an empty 
   * Button.
   */
  public CryptoButton(String buttonID, VariableBundle vars) {
    super();
    
    configureComponent(buttonID, vars);
  }

  /**
   * This configures the UI Component with the given ID and 
   * VariableBundle.
   */ 
  public void configureComponent(String key, VariableBundle vars) {

    loadIcons(key, vars);

    try {
      this.setToolTipText(vars.getProperty(key+ ".ToolTip"));
    } catch (java.util.MissingResourceException mre) {
    }
    
    String cmd = vars.getProperty(key + ".Action", key);
    
    setActionCommand(cmd);
    
    cryptoUpdated(NOT_SIGNED, NOT_ENCRYPTED);
  }

  /**
   * This loads all of the icons for this button.
   */
  public void loadIcons(String key, VariableBundle vars) {

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

    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".notEncrypted.Image"));
      if (url != null)
	notEncryptedIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }

    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".uncheckedEncrypted.Image"));
      if (url != null)
	uncheckedEncryptedIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".decryptedSuccessfully.Image"));
      if (url != null)
	decryptedSuccessfullyIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".decryptedUnsuccessfully.Image"));
      if (url != null)
	decryptedUnsuccessfullyIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".uncheckedSigned.Image"));
      if (url != null)
	uncheckedSignedIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".signatureVerified.Image"));
      if (url != null)
	signatureVerifiedIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".signatureBad.Image"));
      if (url != null)
	signatureBadIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
    try {
      java.net.URL url =this.getClass().getResource(vars.getProperty(key + ".signatureFailedVerification.Image"));
      if (url != null)
	signatureFailedVerificationIcon = new ImageIcon(url);
    } catch (java.util.MissingResourceException mre) {
      return;
    }
    
  }

  /**
   * This updates the Actions on the UI Component.
   *
   * The commands Hashtable is expected to be a table with the Action
   * names as keys, and the Actions themselves as values.
   */
  public void setActive(Hashtable commands) {
    if (currentAction != null) {
      removeActionListener(currentAction);
    }

    try {
      currentAction = (Action)commands.get(getActionCommand());
    } catch (ClassCastException cce) {
      currentAction = null;
    }
    
    if (currentAction != null) {
      addActionListener(currentAction);
      setEnabled(true);
    } else {
      setEnabled(false);
    }
  }
  

  /**
   * This updates the Actions on the UI Component.
   *
   */
  public void setActive(Action[] newActions) {
    Hashtable tmpHash = new Hashtable();
    if (newActions != null && newActions.length > 0) {
      for (int i = 0; i < newActions.length; i++) {
	String cmdName = (String)newActions[i].getValue(Action.NAME);
	tmpHash.put(cmdName, newActions[i]);
      }
    }
    
    setActive(tmpHash);
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
      
      
      cryptoUpdated(sigStatus, cryptStatus);

    } catch (javax.mail.MessagingException me) {
      // ignore here.
    }
  }

  /**
   * Updates the encryption information.
   */
  public void cryptoUpdated(int newSignatureStatus, int newEncryptionStatus) {
    currentCryptStatus = newEncryptionStatus;
    currentSigStatus = newSignatureStatus;

    if (currentCryptStatus == NOT_ENCRYPTED || currentCryptStatus == DECRYPTED_SUCCESSFULLY) {
      if (currentSigStatus == NOT_SIGNED) {
	if (currentCryptStatus == NOT_ENCRYPTED) {
	  setIcon(notEncryptedIcon);
	} else {
	  setIcon(decryptedSuccessfullyIcon);
	  setToolBarColor(encryptedColor);
	}
      } else if (currentSigStatus == UNCHECKED_SIGNED) {
	setIcon(uncheckedSignedIcon);
	  setToolBarColor(uncheckedColor);
      } else if (currentSigStatus == SIGNATURE_VERIFIED) {
	setIcon(signatureVerifiedIcon);
	if (currentCryptStatus == NOT_ENCRYPTED)
	  setToolBarColor(signedColor);
	else 
	  setToolBarColor(signedEncryptedColor);
      } else if (currentSigStatus == SIGNATURE_BAD) {
	setIcon(signatureBadIcon);
	setToolBarColor(failedColor);
      } else if (currentSigStatus == SIGNATURE_FAILED_VERIFICATION) {
	setIcon(signatureFailedVerificationIcon);
	setToolBarColor(failedColor);
      }
    } else if (currentCryptStatus == UNCHECKED_ENCRYPTED) {
      setIcon(uncheckedEncryptedIcon);
      setToolBarColor(uncheckedColor);
    } else if (currentCryptStatus == DECRYPTED_SUCCESSFULLY) {
      setIcon(decryptedSuccessfullyIcon);
      setToolBarColor(encryptedColor);
    } else if (currentCryptStatus == DECRYPTED_UNSUCCESSFULLY) {
      setIcon(decryptedUnsuccessfullyIcon);
      setToolBarColor(failedColor);
    } else {
      setIcon(notEncryptedIcon);
    }

    repaint();
  }

  /**
   * Sets the toolbar color.
   */
  public void setToolBarColor(Color newColor) {
    /*
    JToolBar tb = getToolBar();
    if (tb != null) {
      tb.setBackground(newColor);
    } else {
      System.err.println("toolbar = null.");
    }
    */
    this.setBackground(newColor);
  }

  /**
   * Gets the toolbar this Button belongs to.
   */
  public JToolBar getToolBar() {
    try {
      return (JToolBar) getAncestorOfClass(Class.forName("javax.swing.JToolBar"), this);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Workaround for jdk 1.5 weirdness.
   */
  private java.awt.Container getAncestorOfClass(Class c, java.awt.Component comp) {
    if(comp == null || c == null)
      return null;
    java.awt.Container parent = comp.getParent();
    while(parent != null && !(c.isInstance(parent)))
      parent = parent.getParent();
    return parent;
  }

public int getEncryptionStatus() {
	return currentCryptStatus;
}

public int getSignatureStatus() {
	return currentSigStatus;
}
}
    
