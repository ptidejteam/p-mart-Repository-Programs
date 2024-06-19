package net.suberic.pooka.ssl;

import java.io.*;
import java.net.*;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyStore;
import java.io.File;
import java.io.FileInputStream;

import javax.net.ssl.*;

import net.suberic.pooka.Pooka;

/**
 * An SSLSocketFactory that uses the PookaTrustManager in order to 
 * allow users to manually choose to accpet otherwise untrusted certificates.
 */
public class PookaSSLSocketFactory extends SSLSocketFactory {

  SSLSocketFactory wrappedFactory = null;

  public static Object sLock = new Object();

  /**
   * Creates a PookaSSLSocketFactory.
   */
  public PookaSSLSocketFactory() {
    getLogger().fine("PookaSSLSocketFactory created.");

    try {

      SSLContext sslc = SSLContext.getInstance("TLS");

      KeyStore defaultKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      // load the KeyStore.
      String java_home = System.getProperty("java.home");
      String library_file = java_home + File.separator + "lib" + File.separator + "security" + File.separator + "cacerts"; 
      String passwd = "changeit";
      
      defaultKeyStore.load(new FileInputStream(library_file), passwd.toCharArray());

      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmf.init(defaultKeyStore, passwd.toCharArray());

      KeyManager[] keyManagers = kmf.getKeyManagers();

      TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmFactory.init(defaultKeyStore);
      
      PookaTrustManager ptm = Pooka.getTrustManager();
      if (ptm == null) {
        synchronized (sLock) {
          ptm = Pooka.getTrustManager();
          if (ptm == null) {
            TrustManager[] trustManagers = tmFactory.getTrustManagers();
            
            String fileName = Pooka.getProperty("Pooka.sslCertFile", "");
            ptm = Pooka.getResourceManager().createPookaTrustManager(trustManagers, fileName);
            Pooka.setTrustManager(ptm);
          }
        }
      }
      
      TrustManager[] pookaTrustManagers = new TrustManager[1];
      pookaTrustManagers[0] = ptm;
      
      java.security.SecureRandom secureRandomGenerator = new java.security.SecureRandom();
      if (Pooka.getProperty("Pooka.SSL.useSecureRandom", "true").equalsIgnoreCase("false")) {
	seed(secureRandomGenerator);
      }
      sslc.init(keyManagers, pookaTrustManagers, secureRandomGenerator);
      wrappedFactory = (SSLSocketFactory) sslc.getSocketFactory();
      
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Gets a default PookaSSLSocketFactory.
   */
  public static SocketFactory getDefault() {
    return new PookaSSLSocketFactory();
  }
  
  /**
   * Creates an SSL Socket.
   */
  public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
    getLogger().fine("PookaSSLSocketFactory:  create socket.");
    return wrappedFactory.createSocket(s, host, port, autoClose);
  }
  
  /**
   * Creates an SSL Socket.
   */
  public Socket createSocket(InetAddress host, int port) throws IOException {
    getLogger().fine("PookaSSLSocketFactory:  create socket.");
    return wrappedFactory.createSocket(host, port);
  }
  
  /**
   * Creates an SSL Socket.
   */
  public Socket createSocket(InetAddress address, int port, InetAddress clientAddress, int clientPort) throws IOException {
    getLogger().fine("PookaSSLSocketFactory:  create socket.");
    return wrappedFactory.createSocket(address, port, clientAddress, clientPort);
  }
  
  /**
   * Creates an SSL Socket.
   */
  public Socket createSocket(String host, int port) throws IOException {
    getLogger().fine("PookaSSLSocketFactory:  create socket.");
    return wrappedFactory.createSocket(host, port);
  }
  
  /**
   * Creates an SSL Socket.
   */
  public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException {
    getLogger().fine("PookaSSLSocketFactory:  create socket.");
    return wrappedFactory.createSocket(host, port, clientHost, clientPort);
  }
  
  /**
   * Creates an SSL Socket.
   */
  public Socket createSocket() throws IOException {
    getLogger().fine("PookaSSLSocketFactory:  create socket.");
    return wrappedFactory.createSocket();
  }
  
  /**
   * Retuns the default cipher suites.
   */
  public String[] getDefaultCipherSuites() {
    return wrappedFactory.getSupportedCipherSuites();
  }

  /**
   * Retuns the supported cipher suites.
   */
  public String[] getSupportedCipherSuites() {
    return wrappedFactory.getSupportedCipherSuites();
  }

  /**
   * Generates a random seed.  Useful because the default SecureRandom
   * seed generation system is very very slow.
   */
  public void seed(java.security.SecureRandom random) {
    // check for /dev/urandom if we're on unix.
    if (File.separatorChar == '/') {
      File f = new File("/dev/urandom");
      if (f.exists()) {
        try {
          FileInputStream fis = new FileInputStream(f);
          byte[] seed = new byte[8];
          fis.read(seed);
          random.setSeed(seed);
          return;
        } catch (java.io.IOException ioe) {
          long newSeed = new java.util.Random(System.currentTimeMillis()).nextLong();
          random.setSeed(newSeed);
          
        }
      }
      
    } 

    // if not...
    long newSeed = new java.util.Random(System.currentTimeMillis()).nextLong();
    random.setSeed(newSeed);
  }

  /**
   * Returns the logger for this class.
   */
  java.util.logging.Logger getLogger() {
    return java.util.logging.Logger.getLogger("Pooka.debug.sslFactory");
  }
}

