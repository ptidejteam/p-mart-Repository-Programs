package net.suberic.pooka.ssl;

import java.io.*;

import java.security.cert.*;
import javax.mail.internet.MimeUtility;
import javax.net.ssl.*;


/**
 * This wraps the default X509TrustManager so that we can handle untrusted
 * certificate chains.
 */
public class PookaTrustManager implements X509TrustManager {

  X509TrustManager wrappedManager = null;

  String certificateRepositoryFile = null;

  java.util.Set rejectedCerts = new java.util.HashSet();

  java.util.Set trustedCerts = new java.util.HashSet();

  boolean mUseCertFile = true;

  /**
   * Creates a new TrustManager that wraps the given manager.
   */
  public PookaTrustManager(TrustManager[] pWrappedManagers, String pCertFile) {
    this(pWrappedManagers, pCertFile, true);
  }

  /**
   * Creates a new TrustManager that wraps the given manager.
   */
  public PookaTrustManager(TrustManager[] pWrappedManagers, String pCertFile, boolean pUseCertFile) {
    super();

    mUseCertFile = pUseCertFile;
    if (mUseCertFile)
      certificateRepositoryFile = pCertFile;

    for (int i = 0; i < pWrappedManagers.length; i++) {
      if (pWrappedManagers[i] instanceof X509TrustManager)
        wrappedManager = (X509TrustManager) pWrappedManagers[i];
    }

    loadAccepted();
  }

  /**
   * Returns whether or not the client with the given certificates is
   * trusted or not.
   */
  public void checkClientTrusted(X509Certificate[] cert, String authType) throws CertificateException {
    if (wrappedManager != null) {
      CertificateException trustException = null;
      try {
        wrappedManager.checkClientTrusted(cert, authType);
      } catch (CertificateException e) {
        trustException = e;
      }
      if (trustException != null) {
        if (localIsTrusted(cert))
          return;
        else
          throw trustException;
      }
    }
    // if the respones from the wrappedManager was false, or if there is no
    // wrappedManager, then check out local db.
    else if (! localIsTrusted(cert))
      throw new CertificateException("Certificate not trusted.");
  }

  /**
   * Returns whether or not the server with the given certificates is
   * trusted or not.
   */
  public void checkServerTrusted(X509Certificate[] cert, String authType) throws CertificateException {
    if (wrappedManager != null) {
      CertificateException trustException = null;
      try {
        wrappedManager.checkServerTrusted(cert, authType);
      }
      catch (CertificateException e) {
        trustException = e;
      }

      if (trustException != null) {
        // if this isn't acceptable by default, ask.
        if (localIsTrusted(cert))
          return;
        else
          throw trustException;
      }
    } else
      if (! localIsTrusted(cert))
        throw new CertificateException("Certificate not trusted.");
  }

  /**
   * Return an array of certificate authority certificates
   * which are trusted for authenticating peers.
   */
  public X509Certificate[] getAcceptedIssuers() {
    return wrappedManager.getAcceptedIssuers();
  }

  /**
   * Checks to see if this certificate is in the local certificate store.
   * If it's not, then we ask if it should be.
   */
  public boolean localIsTrusted(X509Certificate[] cert) {
    if (cert == null || cert.length < 1)
      return false;

    boolean found = false;

    boolean rejected = false;

    for (int i = 0; ! found && ! rejected && i < cert.length; i++) {
      if (trustedCerts.contains(cert[i]))
        found = true;
      else if (rejectedCerts.contains(cert[i]))
        rejected = true;
    }

    if (found)
      return true;
    else if (rejected)
      return false;

    // if it hasn't been checked already, then ask.

    boolean response = askIsTrusted(cert);

    if (response) {
      addToTrusted(cert);
    } else {
      addToRejected(cert);
    }

    return response;
  }

  /**
   * Interactively figures out whether or not we want to trust this
   * (default untrusted) certificate.
   */
  public boolean askIsTrusted(X509Certificate[] cert) {
    X509Certificate certToPrint = null;
    for (int i = 0; i < cert.length && certToPrint == null; i++) {
      if (cert[i] != null)
        certToPrint = cert[i];
    }

    int response = -1;
    if (certToPrint != null) {
      StringBuffer msg = new StringBuffer("The following certificates are not trusted.  Accpet them anyway?\n\n");
      msg.append("Issuer:  ");
      msg.append(certToPrint.getIssuerDN().getName());
      msg.append("\n");
      response = net.suberic.pooka.Pooka.getUIFactory().showConfirmDialog(msg.toString(), "Accpet SSL certificate?", javax.swing.JOptionPane.YES_NO_OPTION);
    } else {
      response = net.suberic.pooka.Pooka.getUIFactory().showConfirmDialog("The certificate(s) for this server are not trusted.  Accpet them anyway?", "Accpet SSL certificate?", javax.swing.JOptionPane.YES_NO_OPTION);
    }

    if (response == javax.swing.JOptionPane.YES_OPTION)
      return true;
    else
      return false;
  }

  /**
   * Adds the given certificate(s) to the local trusted store.
   */
  public void addToTrusted(X509Certificate[] cert) {
    if (cert != null) {
      BufferedWriter fw = null;

      if (mUseCertFile) {
        if (certificateRepositoryFile != null && ! certificateRepositoryFile.equals("")) {
          try {
            // see if we can open the file.
            fw = new BufferedWriter(new FileWriter(certificateRepositoryFile, true));
          } catch (IOException ioe) {
            final Exception e = ioe;
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  net.suberic.pooka.Pooka.getUIFactory().showError("Error opening SSL certificate file:  " + certificateRepositoryFile, e);
                }
              });
          }
        } else {
          // don't give warning if we're not using a local certificate file.
          javax.swing.SwingUtilities.invokeLater(new Runnable() {
              public void run() {
                net.suberic.pooka.Pooka.getUIFactory().showError("Warning:  no certificate file set.\nCertificate will only be accepted for this session.\nGo to Configuation->Preferences->SSL to set a certificate file.");
              }
            });
        }
      }

      try {
        for (int i = 0; i < cert.length; i++) {
          if (cert[i] != null) {
            trustedCerts.add(cert[i]);

            if (fw != null) {
              fw.write("-----BEGIN CERTIFICATE-----");
              fw.newLine();
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              OutputStream encOutputStream = MimeUtility.encode(baos, "base64");
              encOutputStream.write(cert[i].getEncoded());
              fw.write(baos.toString());
              fw.newLine();
              fw.write("-----END CERTIFICATE-----");
              fw.newLine();
            }
          }
        }

        fw.flush();
      } catch (Exception e) {
        // FIXME
      } finally {
        if (fw != null) {
          try {
            fw.close();
          } catch (Exception e) {
          }
        }
      }
    }

  }

  /**
   * Adds the given certificate(s) to the rejected list.
   */
  public void addToRejected(X509Certificate[] cert) {
    if (cert != null) {
      for (int i = 0; i < cert.length; i++) {
        if (cert[i] != null)
          rejectedCerts.add(cert[i]);
      }
    }
  }

  /**
   * Loads the already-accepted certificated from the local file.
   */
  public void loadAccepted() {
    FileInputStream fis = null;
    if (mUseCertFile && certificateRepositoryFile != null) {
      try {
        fis = new FileInputStream(certificateRepositoryFile);
        DataInputStream dis = new DataInputStream(fis);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        while (bais.available() > 0) {
          Certificate cert = cf.generateCertificate(bais);

          trustedCerts.add(cert);
        }
      } catch (Exception ioe) {
        // FIXME -- nothing for now.
      } finally {
        try {
          if (fis != null)
            fis.close();
        } catch (Exception e) {

        }
      }
    }

  }
}

