package net.suberic.pooka;
import net.suberic.pooka.gui.*;
import net.suberic.util.VariableBundle;
import net.suberic.pooka.resource.*;

import java.awt.*;
import javax.swing.*;
import javax.help.*;
import java.io.File;
import java.util.logging.*;

public class Pooka {

  /** The configuration for this instance of Pooka. */
  public static PookaManager sManager;

  /**
   * Runs Pooka.  Takes the following arguments:
   *
   * -nf
   * --noOpenSavedFolders    don't open saved folders on startup.
   *
   * -rc [FILE]
   * --rcfile [FILE]    use the given file as the pooka startup file.
   *
   * --http [URL]   runs with a configuration file loaded via http
   *
   * --help shows these options.
   */
  static public void main(String argv[]) {
    sManager = new PookaManager();
    sStartupManager = new StartupManager(sManager);
    sStartupManager.runPooka(argv);
  }
  public static StartupManager sStartupManager = null;

  /**
   * Loads the initial resources for Pooka.  These are used during startup.
   */
  public static void loadInitialResources() {
    try {
      ClassLoader cl = new Pooka().getClass().getClassLoader();
      java.net.URL url;
      if (cl == null) {
        url = ClassLoader.getSystemResource("net/suberic/pooka/Pookarc");
      } else {
        url = cl.getResource("net/suberic/pooka/Pookarc");
      }

      if (url == null) {
        //sigh
        url = new Pooka().getClass().getResource("/net/suberic/pooka/Pookarc");
      }

      java.io.InputStream is = url.openStream();
      VariableBundle resources = new net.suberic.util.FileVariableBundle(is, "net.suberic.pooka.Pooka");
      sManager.setResources(resources);
    } catch (Exception e) {
      System.err.println("caught exception loading system resources:  " + e);
      e.printStackTrace();
      System.exit(-1);
    }
  }


  /**
   * Exits Pooka.  Attempts to close all stores first.
   */
  public static void exitPooka(int exitValue, Object pSource) {
    final int fExitValue = exitValue;
    final Object fSource = pSource;
    Runnable runMe = new Runnable() {
        public void run() {
          sStartupManager.stopMainPookaWindow(fSource);
          System.exit(fExitValue);
        }
      };

    if (Pooka.getMainPanel() != null)
      Pooka.getMainPanel().setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    Thread shutdownThread = new Thread(runMe);
    shutdownThread.start();
  }

  /**
   * Convenience method for getting Pooka configuration properties.  Calls
   * getResources().getProperty(propName, defVal).
   */
  static public String getProperty(String propName, String defVal) {
    return (getResources().getProperty(propName, defVal));
  }

  /**
   * Convenience method for getting Pooka configuration properties.  Calls
   * getResources().getProperty(propName).
   */
  static public String getProperty(String propName) {
    return (getResources().getProperty(propName));
  }

  /**
   * Convenience method for setting Pooka configuration properties.  Calls
   * getResources().setProperty(propName, propValue).
   */
  static public void setProperty(String propName, String propValue) {
    getResources().setProperty(propName, propValue);
  }

  /**
   * Returns the VariableBundle which provides all of the Pooka resources.
   */
  static public net.suberic.util.VariableBundle getResources() {
    return sManager.getResources();
  }
  /**
   * Sets the VariableBundle which provides all of the Pooka resources.
   */
  static public void setResources(net.suberic.util.VariableBundle pResources) {
    sManager.setResources(pResources);
  }


  /**
   * Returns whether or not debug is enabled for this Pooka instance.
   *
   */
  static public boolean isDebug() {
    if (getResources().getProperty("Pooka.debug", "true").equals("true"))
      return true;
    else if (Logger.getLogger("Pooka.debug").isLoggable(Level.FINE))
      return true;
    else
      return false;
  }

  /**
   * Returns the DateFormatter used by Pooka.
   */
  static public DateFormatter getDateFormatter() {
    return sManager.getDateFormatter();
  }

  /**
   * Returns the mailcap command map.  This is what is used to determine
   * which external programs are used to handle files of various MIME
   * types.
   */
  static public javax.activation.CommandMap getMailcap() {
    return sManager.getMailcap();
  }

  /**
   * Returns the Mime Types map.  This is used to map file extensions to
   * MIME types.
   */
  static public javax.activation.MimetypesFileTypeMap getMimeTypesMap() {
    return sManager.getMimeTypesMap();
  }

  /**
   * Gets the default mail Session for Pooka.
   */
  static public javax.mail.Session getDefaultSession() {
    return sManager.getDefaultSession();
  }

  /**
   * Gets the Folder Tracker thread.  This is the thread that monitors the
   * individual folders and checks to make sure that they stay connected,
   * checks for new email, etc.
   */
  static public net.suberic.pooka.thread.FolderTracker getFolderTracker() {
    return sManager.getFolderTracker();
  }

  /**
   * Gets the Pooka Main Panel.  This is the root of the entire Pooka UI.
   */
  static public MainPanel getMainPanel() {
    return sManager.getMainPanel();
  }

  /**
   * The Store Manager.  This tracks all of the Mail Stores that Pooka knows
   * about.
   */
  static public StoreManager getStoreManager() {
    return sManager.getStoreManager();
  }

  /**
   * The Search Manager.  This manages the Search Terms that Pooka knows
   * about, and also can be used to construct Search queries from sets
   * of properties.
   */
  static public SearchTermManager getSearchManager() {
    return sManager.getSearchManager();
  }

  /**
   * The UIFactory for Pooka.  This is used to create just about all of the
   * graphical UI components for Pooka.  Usually this is either an instance
   * of PookaDesktopPaneUIFactory or PookaPreviewPaneUIFactory, for the
   * Desktop and Preview UI styles, respectively.
   */
  static public PookaUIFactory getUIFactory() {
    return sManager.getUIFactory();
  }

  /**
   * The Search Thread.  This is the thread that folder searches are done
   * on.
   */
  static public net.suberic.util.thread.ActionThread getSearchThread() {
    return sManager.getSearchThread();
  }

  /**
   * The Address Book Manager keeps track of all of the configured Address
   * Books.
   */
  static public AddressBookManager getAddressBookManager() {
    return sManager.getAddressBookManager();
  }

  /**
   * The ConnectionManager tracks the configured Network Connections.
   */
  static public NetworkConnectionManager getConnectionManager() {
    return sManager.getConnectionManager();
  }

  /**
   * The OutgoingMailManager tracks the various SMTP server that Pooka can
   * use to send mail.
   */
  static public OutgoingMailServerManager getOutgoingMailManager() {
    return sManager.getOutgoingMailManager();
  }

  /**
   * The EncryptionManager, not surprisingly, manages Pooka's encryption
   * facilities.
   */
  public static PookaEncryptionManager getCryptoManager() {
    return sManager.getCryptoManager();
  }

  /**
   * The HelpBroker is used to bring up the Pooka help system.
   */
  static public HelpBroker getHelpBroker() {
    return sManager.getHelpBroker();
  }

  /**
   * The ResourceManager controls access to resource files.
   */
  static public ResourceManager getResourceManager() {
    return sManager.getResourceManager();
  }

  /**
   * The SSL Trust Manager.
   */
  static public net.suberic.pooka.ssl.PookaTrustManager getTrustManager() {
    return sManager.getTrustManager();
  }

  /**
   * The SSL Trust Manager.
   */
  static public void setTrustManager(net.suberic.pooka.ssl.PookaTrustManager pTrustManager) {
    sManager.setTrustManager(pTrustManager);
  }

  /**
   * The Log Manager.
   */
  static public PookaLogManager getLogManager() {
    return sManager.getLogManager();
  }

  /**
   * The Pooka configuration manager itself.
   */
  static public PookaManager getPookaManager() {
    return sManager;
  }

}


