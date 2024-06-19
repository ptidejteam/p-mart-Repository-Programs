package net.suberic.pooka.gui.crypto;

public interface CryptoStatusDisplay {

  public static int NOT_ENCRYPTED = 0;
  public static int UNCHECKED_ENCRYPTED = 1;
  public static int DECRYPTED_SUCCESSFULLY = 5;
  public static int DECRYPTED_UNSUCCESSFULLY = 10;
  public static int UNCHECKED_SIGNED = 15;
  public static int NOT_SIGNED = 18;
  public static int SIGNATURE_VERIFIED = 20;
  public static int SIGNATURE_BAD = 25;
  public static int SIGNATURE_FAILED_VERIFICATION = 30;
  
  /**
   * Updates the encryption information.
   */
  public void cryptoUpdated(int newSignatureStatus, int newEncryptionStatus);

  /**
   * Updates the encryption information.
   */
  public void cryptoUpdated(net.suberic.pooka.MessageCryptoInfo cryptoInfo);
  
  /** 
   * Liao: Return the signature status
   */
  public int getSignatureStatus();

  /** 
   * Liao: Return the encryption status
   */
  public int getEncryptionStatus();
  

}
    
