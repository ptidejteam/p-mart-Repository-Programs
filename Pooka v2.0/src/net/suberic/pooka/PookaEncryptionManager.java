package net.suberic.pooka;

import java.io.File;
import java.io.FileInputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import net.suberic.crypto.EncryptionKey;
import net.suberic.crypto.EncryptionKeyManager;
import net.suberic.crypto.EncryptionManager;
import net.suberic.crypto.EncryptionUtils;
import net.suberic.crypto.bouncycastle.BouncySMIMEEncryptionKey;
import net.suberic.crypto.bouncycastlepgp.BCPGPEncryptionKey;
import net.suberic.pooka.resource.ResourceManager;
import net.suberic.util.ValueChangeListener;
import net.suberic.util.VariableBundle;

/**
 * The EncryptionManager manages Pooka's encryption facilities.  It's 
 * basically one-stop shopping for all of your email encryption needs.
 */
public class PookaEncryptionManager implements ValueChangeListener {
  char[] pgpPassphrase;
  char[] smimePassphrase;
	
  String key;
  VariableBundle sourceBundle;

  EncryptionKeyManager pgpKeyMgr = null;

  EncryptionKeyManager smimeKeyMgr = null;

  char[] keyMgrPasswd = null;

  Map cachedPGPPrivateKeys = new HashMap();
  Map cachedSMIMEPrivateKeys = new HashMap();

  Map cachedPGPPublicKeys = new HashMap();
  Map cachedSMIMEPublicKeys = new HashMap();

  Map addressToPGPPublicKeyMap = null;
  Map addressToSMIMEPublicKeyMap = null;

  boolean savePasswordsForSession = false;

  boolean needsReload = false;

  /**
   * Creates an EncryptionManager using the given VariableBundle and
   * key property.
   */
  public PookaEncryptionManager(VariableBundle pSourceBundle, String pKey) {
    sourceBundle = pSourceBundle;
    key = pKey;

    // register this for listening to changes to the store filenames and the
    // store passwords.
    sourceBundle.addValueChangeListener(this, key + ".pgp.keyStore.private.filename");
    sourceBundle.addValueChangeListener(this, key + ".pgp.keyStore.private.password");
    sourceBundle.addValueChangeListener(this, key + ".pgp.keyStore.public.filename");

    sourceBundle.addValueChangeListener(this, key + ".smime.keyStore.public.filename");
    sourceBundle.addValueChangeListener(this, key + ".smime.keyStore.private.filename");
    sourceBundle.addValueChangeListener(this, key + ".smime.keyStore.private.password");

    sourceBundle.addValueChangeListener(this, key + ".savePasswordsForSession");

    final VariableBundle fBundle = sourceBundle;
    final String fKey = key;
    Thread storeLoadingThread = new Thread(new Runnable() {
	public void run() {
	  // load the given pgp and smime stores.
	  loadStores(fBundle, fKey);
	}
      });

    storeLoadingThread.start();
  }

  /**
   * Loads the stores.
   */
  public void loadStores(VariableBundle sourceBundle, String key) {
	ResourceManager rm = Pooka.getPookaManager().getResourceManager();
	  
    String pgpPublicFilename = sourceBundle.getProperty(key + ".pgp.keyStore.public.filename", "");
    if(!pgpPublicFilename.equals(""))
    	pgpPublicFilename = rm.translateName(pgpPublicFilename);
    
    String pgpPrivateFilename = sourceBundle.getProperty(key + ".pgp.keyStore.private.filename", "");
    if(!pgpPrivateFilename.equals(""))
    	pgpPrivateFilename = rm.translateName(pgpPrivateFilename);
    
    String pgpPrivatePwString = sourceBundle.getProperty(key + ".pgp.keyStore.private.password", "");
    if (!pgpPrivatePwString.equals("")){
      pgpPrivatePwString = net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(pgpPrivatePwString);
      pgpPassphrase = pgpPrivatePwString.toCharArray();
    }

    // if either store is configured, try loading.
    if (! (pgpPrivateFilename.equals("") && pgpPublicFilename.equals(""))) {
      try {
		EncryptionUtils pgpUtils = EncryptionManager.getEncryptionUtils(EncryptionManager.PGP);
		if (pgpUtils != null) {
		  pgpKeyMgr = pgpUtils.createKeyManager();
		  try {
		      pgpKeyMgr.loadPrivateKeystore(new FileInputStream(new File(pgpPrivateFilename)), null);
		  } catch (java.io.IOException fnfe) {
		      System.out.println("Error loading PGP private keystore from file " + pgpPrivateFilename + ":  " + fnfe.getMessage());
		  } catch (java.security.GeneralSecurityException gse) {
		      System.out.println("Error loading PGP private keystore from file " + pgpPrivateFilename + ":  " + gse.getMessage());
		  }
		  try {
		    pgpKeyMgr.loadPublicKeystore(new FileInputStream(new File(pgpPublicFilename)), null);
		  } catch (java.io.IOException fnfe) {
		    System.out.println("Error loading PGP public keystore from file " + pgpPublicFilename + ":  " + fnfe.getMessage());
		  } catch (java.security.GeneralSecurityException gse) {
		    System.out.println("Error loading PGP public keystore from file " + pgpPublicFilename + ":  " + gse.getMessage());
		  }
		}
      } catch (java.security.NoSuchProviderException nspe) {
    	  System.out.println("Error loading PGP key store:  " + nspe.getMessage());
      } catch (Exception e) {
    	  System.out.println("Error loading PGP key store:  " + e.getMessage());
      }
    }

    String smimePublicFilename = sourceBundle.getProperty(key + ".smime.keyStore.public.filename", "");
    if(!smimePublicFilename.equals(""))
    	smimePublicFilename = rm.translateName(smimePublicFilename);

    String smimePrivateFilename = sourceBundle.getProperty(key + ".smime.keyStore.private.filename", "");
    if(!smimePrivateFilename.equals(""))
    	smimePrivateFilename = rm.translateName(smimePrivateFilename);
    
    String smimePrivatePwString = sourceBundle.getProperty(key + ".smime.keyStore.private.password", "");
    if (!smimePrivatePwString.equals("")){
      smimePrivatePwString = net.suberic.util.gui.propedit.PasswordEditorPane.descrambleString(smimePrivatePwString);
      smimePassphrase = smimePrivatePwString.toCharArray();
    }

    // if either store is configured, try loading.
    if (! (smimePrivateFilename.equals("") && smimePublicFilename.equals(""))) {
      try {
	EncryptionUtils smimeUtils = EncryptionManager.getEncryptionUtils(EncryptionManager.SMIME);
	if (smimeUtils != null) {
	  smimeKeyMgr = smimeUtils.createKeyManager();
	  try {
	    smimeKeyMgr.loadPrivateKeystore(new FileInputStream(new File(smimePrivateFilename)), smimePrivatePwString.toCharArray());
	  } catch (java.security.GeneralSecurityException gse) {
	    System.out.println("Error loading S/MIME private keystore from file " + smimePrivateFilename + ":  " + gse.getMessage());
	  } catch (java.io.IOException fnfe) {
	    System.out.println("Error loading S/MIME private keystore from file " + smimePrivateFilename + ":  " + fnfe.getMessage());
	  }
	  
	  try {
	    smimeKeyMgr.loadPublicKeystore(new FileInputStream(new File(smimePublicFilename)), smimePrivatePwString.toCharArray());
	  } catch (java.io.IOException fnfe) {
	    System.out.println("Error loading S/MIME public keystore from file " + smimePublicFilename + ":  " + fnfe.getMessage());
	  } catch (java.security.GeneralSecurityException gse) {
	    System.out.println("Error loading S/MIME private keystore from file " + smimePublicFilename + ":  " + gse.getMessage());
	  }      
	}
      } catch (java.security.NoSuchProviderException nspe) {
	System.out.println("Error loading S/MIME key store:  " + nspe.getMessage());
      } catch (Exception e) {
	System.out.println("Error loading S/MIME key store:  " + e.getMessage());
      }
    }

    savePasswordsForSession = Pooka.getProperty(key + ".savePasswordsForSession", "false").equalsIgnoreCase("true");
    
    cachedPGPPrivateKeys = new HashMap();
    cachedSMIMEPrivateKeys = new HashMap();

    cachedPGPPublicKeys = new HashMap();
    cachedSMIMEPublicKeys = new HashMap();

    addressToPGPPublicKeyMap = null;
    addressToSMIMEPublicKeyMap = null;

  }

  /**
   * As defined in net.suberic.util.ValueChangeListener.
   * 
   */
  public void valueChanged(String changedValue) {
    if (changedValue.equals(key + ".savePasswordsForSession")) {
      savePasswordsForSession = Pooka.getProperty(key + ".savePasswordsForSession", "false").equalsIgnoreCase("true");
    } else {
      // this is crazy.
      needsReload = true;
      javax.swing.SwingUtilities.invokeLater(new Runnable() {

	  public void run() {
	    if (needsReload) {
	      needsReload = false;
	      
	      Thread updateThread = new Thread(new Runnable() {
		  public void run() {
		    loadStores(sourceBundle, key);
		  }
		});
	      
	      updateThread.start();
	    }
	  }
	});
    }
  }
  
  
  /**
   * Adds the private key to the store.
   */
  public void addPrivateKey(String alias, Key privateKey, char[] passphrase, String type) throws GeneralSecurityException {
    EncryptionKeyManager currentMgr = getKeyMgr(type);
    if (currentMgr != null) {
      currentMgr.setPrivateKeyEntry(alias, privateKey, passphrase);
    } else {
      throw new KeyStoreException(type + " KeyStore not initialized.");
    }
  }

  /**
   * Adds the public key to the store.
   */
  public void addPublicKey(String alias, Key publicKey, String type) 
  throws GeneralSecurityException {
    
    EncryptionKeyManager currentMgr = getKeyMgr(type);
    if (currentMgr != null) {
      currentMgr.setPublicKeyEntry(alias, publicKey);
    } else {
      throw new KeyStoreException(type + " KeyStore not initialized.");
    }
  }

  /**
   * Returns the private key(s) for the given email address and 
   * the given encryption type, or all matching keys if type == null.
   */
  public Key[] getPrivateKeysForAddress(String address, String type, boolean forSignature) {
	  //return null;
	  //Liao-
	  ArrayList keys = new ArrayList();
	  Set aliases;
	  Iterator it;
	  String alias;
	  
	  try {
		aliases = privateKeyAliases(type, forSignature);
      } catch (KeyStoreException e) {
    	  return null;
	  }
      
	  it = aliases.iterator();
	  while(it.hasNext()){
		 alias = (String) it.next();
		 Key key = null;
		try {
			key = this.getPrivateKey(alias, type);
		} catch (Exception e) {
			continue;
		}
		
		 if(key != null){
			 String[] addresses = ((EncryptionKey) key).getAssociatedAddresses();
			 
			 if(addresses != null){
				 for (int i = 0; i < addresses.length; i++) {					 
					if(addresses[i].length() > 0 && address.equalsIgnoreCase(addresses[i])){
						keys.add(key);
					}
				}
			 }
		 }
	  }

	  Key[] _keys = new Key[keys.size()];
	  for (int i = 0; i < _keys.length; i++) {
		_keys[i] = (Key) keys.get(i);
	}
	  
	  return _keys;
	  //Liao+
  }
  

  /**
   * Returns all private keys that have been cached.
   */
  public Key[] getCachedPrivateKeys(String type) {
	  if(EncryptionManager.PGP.equalsIgnoreCase(type))
		  return (Key[])cachedPGPPrivateKeys.values().toArray(new Key[0]);
	  else if(EncryptionManager.SMIME.equalsIgnoreCase(type))
		  return (Key[])cachedSMIMEPrivateKeys.values().toArray(new Key[0]);
	  else
	      return null;
  }

  /**
   * Returns all available private key aliases for the give EncryptionType,
   * or all available aliases if type is null.
   */
  public Set privateKeyAliases(String encryptionType, boolean forSignature) 
  throws java.security.KeyStoreException {
    if (EncryptionManager.PGP.equalsIgnoreCase(encryptionType)) {
      if (pgpKeyMgr != null)
		return new HashSet(pgpKeyMgr.privateKeyAliases(forSignature));
      } 
      else if (EncryptionManager.SMIME.equalsIgnoreCase(encryptionType)) {
	      if (smimeKeyMgr != null) {
		    return new HashSet(smimeKeyMgr.privateKeyAliases(forSignature));
          }
      }

    return new HashSet();
  }

  public Key getPrivateKey(String alias, String type) 
  throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException {
	  if(EncryptionManager.SMIME.equalsIgnoreCase(type)){
		  return getPrivateKey(alias, type, smimePassphrase);
	  } else if(EncryptionManager.PGP.equalsIgnoreCase(type)){
		  return getPrivateKey(alias, type, pgpPassphrase);
	  } else{
		 return null; 
	  }  
  }
  /**
   * Returns the Private key for the given alias.
   */
  public Key getPrivateKey(String alias, String type, char[] passphrase) 
  throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException {
	 Map cachedPrivateKeys = null;
	 if(EncryptionManager.PGP.equalsIgnoreCase(type)){
		 cachedPrivateKeys = cachedPGPPrivateKeys;
	 }else if(EncryptionManager.SMIME.equalsIgnoreCase(type)){
		 cachedPrivateKeys = cachedSMIMEPrivateKeys;
	 }else{
		 return null;
	 }
	 
    // first check to see if this is in the cache.
    Key cachedKey = (Key) cachedPrivateKeys.get(alias);
    if (cachedKey != null){
    	return cachedKey;
	}
    
    KeyStoreException caughtException = null;
    if (pgpKeyMgr != null || smimeKeyMgr != null) {
      // check to see if this exists anywhere.
      if (pgpKeyMgr != null && EncryptionManager.PGP.equalsIgnoreCase(type)) {
		try {
		  if (pgpKeyMgr.containsPrivateKeyAlias(alias)) {
		    Key returnValue = pgpKeyMgr.getPrivateKey(alias, passphrase);

		    cachedPrivateKeys.put(alias, returnValue);
		    return returnValue;
		  }
		} catch (KeyStoreException kse) {
		  caughtException = kse;
		}
      }
      
      if (smimeKeyMgr!= null && EncryptionManager.SMIME.equalsIgnoreCase(type)) {
		try {
		  if (smimeKeyMgr.containsPrivateKeyAlias(alias)) {
		    Key returnValue = smimeKeyMgr.getPrivateKey(alias, passphrase);
		    cachedPrivateKeys.put(alias, returnValue);
		    return returnValue;
		  }
		} catch (KeyStoreException kse) {
		  if (caughtException == null)
		    caughtException = kse;
		}
      }
    }
    
    if (caughtException != null)
      throw caughtException;
    
    return null;
  }
  //Liao+

  /**
   * Returns the Public key for the given alias.
   */
  public Key getPublicKey(String alias, String type) 
  throws java.security.KeyStoreException, 
         java.security.NoSuchAlgorithmException, 
         java.security.UnrecoverableKeyException 
  {
	  EncryptionKeyManager keymanager;
	  if(EncryptionManager.PGP.equalsIgnoreCase(type)){
		 keymanager = pgpKeyMgr;
	  } else if(EncryptionManager.SMIME.equalsIgnoreCase(type)){
		 keymanager = smimeKeyMgr;
	  } else{
		  return null;
	  }
	  
      try {
	     return keymanager.getPublicKey(alias);
      } catch (KeyStoreException kse) {
    	  return null;
      }
  }
  
  /**
   * Returns the public key(s) for the given email address that match
   * the given encryption type, or all matching keys if type == null.
   */
  public Key[] getPublicKeys(String address, String type, boolean forSignature) 
  throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException 
  {	 
    sortPublicKeys(type);

    Map addressToPublicKeyMap;
	 if(EncryptionManager.PGP.equalsIgnoreCase(type)){
		 addressToPublicKeyMap = addressToPGPPublicKeyMap;
	 }else if(EncryptionManager.SMIME.equalsIgnoreCase(type)){
		 addressToPublicKeyMap = addressToSMIMEPublicKeyMap;
	 }else{
		 return new Key[0];
	 }

    ArrayList list = (ArrayList) addressToPublicKeyMap.get(address.toLowerCase());
    if (list == null)
      return new Key[0];
    else if (type == null) {
      return (Key[]) list.toArray(new Key[0]);
    } else {
      ArrayList sortedList = new ArrayList();
      java.util.Iterator iter = list.iterator();      
      
      while (iter.hasNext()) {
		EncryptionKey current = (EncryptionKey) iter.next();
		try {
		  if (current.getEncryptionUtils().getType().equalsIgnoreCase(type)) 
		  {
			  if(forSignature && current.isForSignature()){
				  sortedList.add(current);
			  }else if(!forSignature && current.isForEncryption()){
				  sortedList.add(current);
			  }
		  }
		} catch (Exception e) {
		}
      }

      return (Key[]) sortedList.toArray(new Key[0]);
    }
  }

  /**
   * Sorts all available public keys by associated address.
   */
  private synchronized void sortPublicKeys(String type) 
  throws java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.UnrecoverableKeyException 
  {
	if(! (EncryptionManager.PGP.equalsIgnoreCase(type) || EncryptionManager.SMIME.equalsIgnoreCase(type))){
		return;
	}
	
	Map addressToPublicKeyMap;
	
	if(EncryptionManager.PGP.equalsIgnoreCase(type)){
		if(addressToPGPPublicKeyMap == null){
			addressToPGPPublicKeyMap = new HashMap();
			addressToPublicKeyMap = addressToPGPPublicKeyMap;
		}
		else
			return;
	}else if(EncryptionManager.SMIME.equalsIgnoreCase(type)){
		if(addressToSMIMEPublicKeyMap == null){
			addressToSMIMEPublicKeyMap = new HashMap();
			addressToPublicKeyMap = addressToSMIMEPublicKeyMap;
		}
		else
			return;
	}else{
		return;
	}
	

      Set aliases1 = publicKeyAliases(type, true);
      Set aliases2 = publicKeyAliases(type, false);
 
      Set aliases3 = privateKeyAliases(type, true);
      Set aliases4 = privateKeyAliases(type, false);

      java.util.Iterator iter2 = aliases2.iterator();
      while(iter2.hasNext()){
    	String s = (String) iter2.next();
    	if(!aliases1.contains(s)){
    		aliases1.add(s);
    	}
      }
      
      iter2 = aliases4.iterator();
      while(iter2.hasNext()){
    	String s = (String) iter2.next();
    	if(!aliases3.contains(s)){
    		aliases3.add(s);
    	}
      }
      
      java.util.Iterator iter = aliases1.iterator();
      java.util.Iterator iter3 = aliases3.iterator();
      while (iter.hasNext() || iter3.hasNext()) {
    	  Key current = null;
    	  
    	  if(iter.hasNext()){
    		  String alias = (String) iter.next();
    		  current = getPublicKey(alias, type);
    	  }else{
    		  String alias = (String) iter3.next();
    		  try{
    		    current = getPrivateKey(alias, type);
    		  }catch(UnrecoverableKeyException uke){
    			  ;
    		  }
    	  }
	
		if (current instanceof EncryptionKey) {
		  String[] assocAddresses = ((EncryptionKey) current).getAssociatedAddresses();
		  for (int i = 0; assocAddresses != null && i < assocAddresses.length; i++) {
		    String address = assocAddresses[i];
		    ArrayList matches = (ArrayList) addressToPublicKeyMap.get(address);
		    if (matches != null) {
		      if (! matches.contains(current))
		      matches.add(current);
		    } else {
		      matches = new ArrayList();
		      matches.add(current);
		      addressToPublicKeyMap.put(address.toLowerCase(), matches);
		    }
		  }
		}
      }    
  }

  /**
   * Returns available public key aliases for the given encryption type, or
   * all available aliases if null.
   */
  public Set publicKeyAliases(String encryptionType, boolean forSignature) throws java.security.KeyStoreException {

    if (EncryptionManager.PGP.equalsIgnoreCase(encryptionType)) {
      if (pgpKeyMgr != null)
	    return new HashSet(pgpKeyMgr.publicKeyAliases(forSignature));
    } else if (EncryptionManager.SMIME.equalsIgnoreCase(encryptionType)) {
      if (smimeKeyMgr != null) {
	    return new HashSet(smimeKeyMgr.publicKeyAliases(forSignature));
      }
    }

    return new HashSet();

  }

  
  /**
   * Encrypts the given message.  If there's no key, return null.
   */
  public MimeMessage encryptMessage(MimeMessage mMsg, Key[] keys)
    throws MessagingException, java.security.GeneralSecurityException, java.io.IOException {
    if (keys != null && keys.length > 0) {
   	  return ((EncryptionKey) keys[0]).getEncryptionUtils().encryptMessage(
		Pooka.getDefaultSession(), mMsg, keys);      
    }
    
    return mMsg;
  }

  /**
   * Signs the given message.
   */
  public MimeMessage signMessage(MimeMessage mMsg, UserProfile profile, Key key) 
    throws MessagingException, java.io.IOException, java.security.GeneralSecurityException  {
	  boolean forSignature = true;
    if (key == null && profile != null) {
      key = profile.getEncryptionKey(forSignature);
    }
    
    if (key == null) {
      // get user input
    }

    if (key != null) {
      if (key instanceof net.suberic.crypto.EncryptionKey) {
	    return ((EncryptionKey) key).getEncryptionUtils().signMessage(
			Pooka.getDefaultSession(), mMsg, key);
      } else {
	    return EncryptionManager.getEncryptionUtils(EncryptionManager.PGP).signMessage(
			Pooka.getDefaultSession(), mMsg, key);
      }
    } else {
      return mMsg;
    }
  }

  /**
   * Returns the EncryptionKeyManager for this type.
   */
  EncryptionKeyManager getKeyMgr(String type) {
    if (EncryptionManager.PGP.equalsIgnoreCase(type)) 
      return pgpKeyMgr;
    else if (EncryptionManager.SMIME.equalsIgnoreCase(type))
      return smimeKeyMgr;
    else
      return null;
  }
}
