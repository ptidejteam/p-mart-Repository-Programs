package net.suberic.pooka;
import net.suberic.pooka.gui.*;
import net.suberic.util.VariableBundle;
import net.suberic.pooka.resource.*;
import net.suberic.pooka.messaging.PookaMessageListener;

import java.awt.*;
import javax.swing.*;
import java.io.File;
import java.util.Vector;
import javax.help.*;
import java.util.logging.*;

/**
 * This keeps track of all of the various managers and settings for an
 * instance of Pooka.
 */
public class PookaManager {

  // the resources for Pooka

  /** The Resources for this instance of Pooka.*/
  net.suberic.util.VariableBundle mResources;
  /** Gets the Resources for this instance of Pooka.*/
  public VariableBundle getResources() { return mResources; }
  /**  Sets the Resources for this instance of Pooka.*/
  public void setResources(VariableBundle pResources) { mResources = pResources; }

  /** the log manager. */
  PookaLogManager mLogManager;
  /** Gets the logManager for this instance of Pooka.*/
  public PookaLogManager getLogManager() { return mLogManager; }
  /**  Sets the logManager for this instance of Pooka.*/
  public void setLogManager(PookaLogManager pLogManager) { mLogManager = pLogManager; }


  // the startup/configuration file
  String mLocalrc = null;
  /** Gets the localrc for this instance of Pooka.*/
  public String getLocalrc() { return mLocalrc; }
  /**  Sets the localrc for this instance of Pooka.*/
  public void setLocalrc(String pLocalrc) { mLocalrc = pLocalrc; }

  // the root directory
  File mPookaRoot = null;
  /** Gets the localrc for this instance of Pooka.*/
  public File getPookaRoot() { return mPookaRoot; }
  /**  Sets the localrc for this instance of Pooka.*/
  public void setPookaRoot(File pPookaRoot) { mPookaRoot = pPookaRoot; }

  // mail globals
  /** The default mail Session for Pooka. */
  javax.mail.Session mDefaultSession;
  /** Gets the default mail Session for Pooka. */
  public javax.mail.Session getDefaultSession() { return mDefaultSession; }
  /** Sets the default mail Session for Pooka. */
  public void setDefaultSession(javax.mail.Session pDefaultSession) { mDefaultSession = pDefaultSession; }

  /**
   * The mailcap command map.  This is what is used to determine
   * which external programs are used to handle files of various MIME
   * types.
   */
  javax.activation.CommandMap mMailcap;
  /**
   * Returns the mailcap command map.  This is what is used to determine
   * which external programs are used to handle files of various MIME
   * types.
   */
  public javax.activation.CommandMap getMailcap() { return mMailcap; }
  /**
   * Sets the mailcap command map.  This is what is used to determine
   * which external programs are used to handle files of various MIME
   * types.
   */
  public void setMailcap(javax.activation.CommandMap pMailcap) { mMailcap = pMailcap; }


  /** The Mime Types map.  This is used to map file extensions to MIME types. */
  javax.activation.MimetypesFileTypeMap mMimeTypesMap = new javax.activation.MimetypesFileTypeMap();
  /**
   * Returns the Mime Types map.  This is used to map file extensions to
   * MIME types.
   */
  public javax.activation.MimetypesFileTypeMap getMimeTypesMap() { return mMimeTypesMap; }
  /** Set the Mime Types map.  This is used to map file extensions to
      MIME types. */
  public void setMimeTypesMap(javax.activation.MimetypesFileTypeMap pMimeTypesMap) { mMimeTypesMap = pMimeTypesMap; }

  // the DateFormatter, which we cache for convenience.
  DateFormatter mDateFormatter;
  /** Gets the dateFormatter for this instance of Pooka.*/
  public DateFormatter getDateFormatter() { return mDateFormatter; }
  /**  Sets the dateFormatter for this instance of Pooka.*/
  public void setDateFormatter(DateFormatter pDateFormatter) { mDateFormatter = pDateFormatter; }


  // threads

  /** The Search Thread.  This is the thread that folder searches are done on.
   */
  net.suberic.util.thread.ActionThread mSearchThread = null;
  /** Returns the thread that folder searches are done on.*/
  public net.suberic.util.thread.ActionThread getSearchThread() { return mSearchThread; }
  /** Sets the thread that folder searches are done on.*/
  public void setSearchThread(net.suberic.util.thread.ActionThread pSearchThread) { mSearchThread = pSearchThread; }

  /**
   * The Folder Tracker thread.  This is the thread that monitors the
   * individual folders and checks to make sure that they stay connected,
   * checks for new email, etc.
   */
  net.suberic.pooka.thread.FolderTracker mFolderTracker;
  /** Gets the Folder Tracker thread.  */
  public net.suberic.pooka.thread.FolderTracker getFolderTracker() { return mFolderTracker; }
  /**
   * Sets the Folder Tracker thread. */
  public void setFolderTracker(net.suberic.pooka.thread.FolderTracker pFolderTracker) { mFolderTracker = pFolderTracker; }

  // Pooka managers and factories
  /**
   * The Address Book Manager keeps track of all of the configured Address
   * Books.
   */
  AddressBookManager mAddressBookManager = null;
  /** Returns the Address Book Manager. */
  public AddressBookManager getAddressBookManager() {
    return mAddressBookManager;
  }
  /** Sets the Address Book Manager. */
  public void setAddressBookManager(AddressBookManager pAddressBookManager) { mAddressBookManager = pAddressBookManager; }

  /** The Store Manager tracks all of the Mail Stores that Pooka knows about.*/
  StoreManager mStoreManager;
  /** Returns the Store Manager. */
  public StoreManager getStoreManager() {
    return mStoreManager;
  }
  /** Sets the Store Manager. */
  public void setStoreManager(StoreManager pStoreManager) { mStoreManager = pStoreManager; }

  /** The UserProfile Manager tracks all of the User Profiles that Pooka knows about.*/
  UserProfileManager mUserProfileManager;
  /** Returns the UserProfile Manager. */
  public UserProfileManager getUserProfileManager() {
    return mUserProfileManager;
  }
  /** Sets the UserProfile Manager. */
  public void setUserProfileManager(UserProfileManager pUserProfileManager) { mUserProfileManager = pUserProfileManager; }

  /**
   * The UIFactory for Pooka.  This is used to create just about all of the
   * graphical UI components for Pooka.  Usually this is either an instance
   * of PookaDesktopPaneUIFactory or PookaPreviewPaneUIFactory, for the
   * Desktop and Preview UI styles, respectively.
   */
  PookaUIFactory mUiFactory;
  /**
   * The UIFactory for Pooka.  This is used to create just about all of the
   * graphical UI components for Pooka.  Usually this is either an instance
   * of PookaDesktopPaneUIFactory or PookaPreviewPaneUIFactory, for the
   * Desktop and Preview UI styles, respectively.
   */
  public PookaUIFactory getUIFactory() { return mUiFactory; }
  /**
   * The UIFactory for Pooka.  This is used to create just about all of the
   * graphical UI components for Pooka.  Usually this is either an instance
   * of PookaDesktopPaneUIFactory or PookaPreviewPaneUIFactory, for the
   * Desktop and Preview UI styles, respectively.
   */
  public void setUIFactory(PookaUIFactory pUIFactory) { mUiFactory = pUIFactory; }

  /**
   * The Search Manager.  This manages the Search Terms that Pooka knows
   * about, and also can be used to construct Search queries from sets
   * of properties.
   */
  SearchTermManager mSearchManager;
  /**
   * The Search Manager.  This manages the Search Terms that Pooka knows
   * about, and also can be used to construct Search queries from sets
   * of properties.
   */
  public SearchTermManager getSearchManager() { return mSearchManager; }
  /**
   * The Search Manager.  This manages the Search Terms that Pooka knows
   * about, and also can be used to construct Search queries from sets
   * of properties.
   */
  public void setSearchManager(SearchTermManager pSearchTermManager) { mSearchManager = pSearchTermManager; }

  /** The ConnectionManager tracks the configured Network Connections. */
  NetworkConnectionManager mConnectionManager;
  /** The ConnectionManager tracks the configured Network Connections. */
  public NetworkConnectionManager getConnectionManager() {return mConnectionManager; }
  /** The ConnectionManager tracks the configured Network Connections. */
  public void setConnectionManager(NetworkConnectionManager pConnectionManager) { mConnectionManager= pConnectionManager; }

  /**
   * The OutgoingMailManager tracks the various SMTP server that Pooka can
   * use to send mail.
   */
  OutgoingMailServerManager mOutgoingMailManager;
  /**
   * The OutgoingMailManager tracks the various SMTP server that Pooka can
   * use to send mail.
   */
  public OutgoingMailServerManager getOutgoingMailManager() { return mOutgoingMailManager; }
  /**
   * The OutgoingMailManager tracks the various SMTP server that Pooka can
   * use to send mail.
   */
  public void setOutgoingMailManager(OutgoingMailServerManager pOutgoingMailManager) { mOutgoingMailManager = pOutgoingMailManager; }

  /**
   * The EncryptionManager, not surprisingly, manages Pooka's encryption
   * facilities.
   */
  PookaEncryptionManager mCryptoManager;
  /**
   * The EncryptionManager, not surprisingly, manages Pooka's encryption
   * facilities.
   */
  public PookaEncryptionManager getCryptoManager() { return mCryptoManager; }
  /**
   * The EncryptionManager, not surprisingly, manages Pooka's encryption
   * facilities.
   */
  public void setCryptoManager(PookaEncryptionManager pCryptoManager) { mCryptoManager = pCryptoManager; }

  net.suberic.pooka.resource.ResourceManager mResourceManager;
  /** The ResourceManager controls access to resource files. */
  public ResourceManager getResourceManager() { return mResourceManager; }
  /** The ResourceManager controls access to resource files. */
  public void setResourceManager(net.suberic.pooka.resource.ResourceManager pResourceManager) { mResourceManager = pResourceManager; }

  net.suberic.pooka.ssl.PookaTrustManager mTrustManager = null;
  /** Gets the SSL Trust Manager. */
  public net.suberic.pooka.ssl.PookaTrustManager getTrustManager() { return mTrustManager; }
  /** Sets the SSL Trust Manager. */
  public void setTrustManager(net.suberic.pooka.ssl.PookaTrustManager pTrustManager) { mTrustManager = pTrustManager; }

  // the main Pooka panel.
  net.suberic.pooka.gui.MainPanel mPanel;
  /** Gets the Pooka Main Panel.  This is the root of the entire Pooka UI.*/
  public MainPanel getMainPanel() { return mPanel; }
  /** Sets the Pooka Main Panel.  This is the root of the entire Pooka UI.*/
  public void setMainPanel(MainPanel pPanel) { mPanel = pPanel; }

  String mPookaHome = null;
  /** Gets the pookaHome for this instance of Pooka.*/
  public String getPookaHome() { return mPookaHome; }
  /**  Sets the pookaHome for this instance of Pooka.*/
  public void setPookaHome(String pPookaHome) { mPookaHome = pPookaHome; }


  /** The HelpBroker is used to bring up the Pooka help system. */
  HelpBroker mHelpBroker;
  /** The HelpBroker is used to bring up the Pooka help system. */
  public HelpBroker getHelpBroker() { return mHelpBroker; }
  /** The HelpBroker is used to bring up the Pooka help system. */
  public void setHelpBroker(HelpBroker pHelpBroker) { mHelpBroker = pHelpBroker; }

  /** The MessageListener for this server. */
  PookaMessageListener mMessageListener = null;
  /** The MessageListener for this server. */
  public PookaMessageListener getMessageListener() { return mMessageListener; }
  /** The MessageListener for this server. */
  public void setMessageListener(PookaMessageListener pMessageListener) { mMessageListener = pMessageListener; }

}

