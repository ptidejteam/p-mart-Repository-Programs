package net.suberic.pooka;
import net.suberic.pooka.gui.*;
import net.suberic.pooka.resource.*;
import net.suberic.pooka.messaging.*;
import net.suberic.util.VariableBundle;
import net.suberic.util.gui.IconManager;
import net.suberic.util.gui.propedit.PropertyEditorFactory;
import net.suberic.util.gui.propedit.PropertyEditorManager;

import java.awt.*;
import javax.swing.*;
import javax.help.*;
import java.io.File;
import java.util.logging.*;

/**
 * This manages all startup options for Pooka.
 */
public class StartupManager {

  // the PookaManager that we're using to startup.  for convenience.
  PookaManager mPookaManager = null;

  JFrame mFrame = null;

  // settings
  public boolean mOpenFolders = true;
  public boolean mUseHttp = false;
  public boolean mUseLocalFiles = true;
  public boolean mUseJdbc = false;
  public boolean mFullStartup = true;
  String mToAddress = null;
  String mFromProfile = null;

  boolean mShuttingDown = false;

  /**
   * Creates a new StartupManager.
   */
  public StartupManager(PookaManager pPookaManager) {
    mPookaManager = pPookaManager;
  }

  /**
   * Runs Pooka.
   */
  public void runPooka(String argv[]) {
    mStartTime = System.currentTimeMillis();

    Pooka.loadInitialResources();

    updateTime("intial resources parsed.");

    parseArgs(argv);

    updateTime("args parsed.");

    loadResources(mUseLocalFiles, mUseHttp, mUseJdbc);

    mPookaManager.setLogManager(new PookaLogManager());

    updateTime("resources loaded.");

    if (! checkJavaVersion()) {
      versionError();
      System.exit(-1);
    }

    System.setProperty("swing.aatext", "true");

    // check to see if there's already a Pooka instance running.
    if (!checkRunningInstance()) {
      if (mFullStartup) {
        startupPooka();
      } else {
        startupMinimal();
      }
    }
  }


 /**
   * Loads all the resources for Pooka.
   */
  public void loadResources(boolean pUseLocalFiles, boolean pUseHttp, boolean pUseJdbc) {
    PookaManager manager = Pooka.getPookaManager();

    if (manager == null || manager.getResources() == null) {
      System.err.println("Error starting up Pooka:  No system resource files found.");
      System.exit(-1);
    }

    try {
      net.suberic.util.VariableBundle pookaDefaultBundle = manager.getResources();
      ResourceManager resourceManager = null;

      //
      if (! pUseLocalFiles || pookaDefaultBundle.getProperty("Pooka.useLocalFiles", "true").equalsIgnoreCase("false")) {
        resourceManager = new DisklessResourceManager();
      } else if (pUseJdbc) {
        // preset the jdbc options if they have been provided.
        Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.driver", System.getProperty("JDBCPreferences.driverName"));
        Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.url", System.getProperty("JDBCPreferences.url"));
        Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.user", System.getProperty("JDBCPreferences.user"));
        Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.password", net.suberic.util.gui.propedit.PasswordEditorPane.scrambleString(System.getProperty("JDBCPreferences.password")));

        resourceManager = configureJDBCManager();
      } else {
        resourceManager = new FileResourceManager();

        String localRc = resourceManager.getDefaultLocalrc(resourceManager.getDefaultPookaRoot() == null ? resourceManager.getDefaultPookaRoot() : manager.getPookaRoot());

        VariableBundle tmpVarBundle = resourceManager.createVariableBundle(localRc, pookaDefaultBundle);

        if (tmpVarBundle.getProperty("Pooka.useJdbc", "false").equalsIgnoreCase("true")) {
          Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.driver", tmpVarBundle.getProperty("Pooka._jdbcWizard.selection.driver", ""));
          Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.url",  tmpVarBundle.getProperty("Pooka._jdbcWizard.selection.url", ""));
          Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.user",  tmpVarBundle.getProperty("Pooka._jdbcWizard.selection.user", ""));
          Pooka.getResources().setProperty("Pooka._jdbcWizard.selection.password", tmpVarBundle.getProperty("Pooka._jdbcWizard.selection.password", ""));

          resourceManager = configureJDBCManager();
        }
      }

      manager.setResourceManager(resourceManager);

      // the PookaRoot hasn't been set, use the user's home directory.

      if (manager.getPookaRoot() == null) {
        manager.setPookaRoot(resourceManager.getDefaultPookaRoot());
      }

      if (manager.getLocalrc() == null) {
        manager.setLocalrc(resourceManager.getDefaultLocalrc(manager.getPookaRoot()));
      }
      manager.setResources(manager.getResourceManager().createVariableBundle(manager.getLocalrc(), pookaDefaultBundle));
    } catch (Exception e) {
      System.err.println("caught exception:  " + e);
      e.printStackTrace();
    }

    if (pUseHttp || manager.getResources().getProperty("Pooka.httpConfig", "false").equalsIgnoreCase("true")) {
      net.suberic.pooka.gui.LoadHttpConfigPooka configPooka = new net.suberic.pooka.gui.LoadHttpConfigPooka();
      configPooka.start();
    }

  }

  /**
   * Configures a JDBC manager.
   *
   */
  private ResourceManager configureJDBCManager() throws InterruptedException, java.lang.reflect.InvocationTargetException  {
    SwingUtilities.invokeAndWait(new Runnable() {
        public void run() {
          System.setProperty("java.util.prefs.PreferencesFactory", "net.suberic.util.prefs.JDBCPreferencesFactory");

          PookaUIFactory tmpFactory = new PookaMinimalUIFactory();

          tmpFactory.showEditorWindow(Pooka.getProperty("Pooka._jdbcWizard.label", "Load Settings"), "Pooka._jdbcWizard");

          if (! "true".equals(System.getProperty("useJdbcConnection"))) {
            System.exit(1);
          }
          mPookaManager.setUIFactory(tmpFactory);

        }
      });

    return new JDBCResourceManager();
  }

  /**
   * Does a full startup of Pooka.
   */
  public void startupPooka() {
    net.suberic.pooka.gui.PookaStartup startup = new net.suberic.pooka.gui.PookaStartup();
    startup.show();

    updateTime("startup invoked.");

    loadManagers(startup);

    startupMainPookaWindow(startup);
  }

  /**
   * Starts up the main Pooka window.
   */
  public void startupMainPookaWindow(net.suberic.pooka.gui.PookaStartup pStartup) {

    final net.suberic.pooka.gui.PookaStartup startup = pStartup;
    mFrame = new JFrame("Pooka");
    final JFrame finalFrame = mFrame;

    mPookaManager.setFolderTracker(new net.suberic.pooka.thread.FolderTracker());
    mPookaManager.getFolderTracker().start();
    updateTime("started folderTracker");

    mPookaManager.setSearchThread(new net.suberic.util.thread.ActionThread(Pooka.getProperty("thread.searchThread", "Search Thread ")));
    mPookaManager.getSearchThread().start();
    updateTime("started search thread");

    if (Pooka.getUIFactory() == null) {
      if (Pooka.getProperty("Pooka.guiType", "Desktop").equalsIgnoreCase("Preview"))
        mPookaManager.setUIFactory(new PookaPreviewPaneUIFactory());
      else
        mPookaManager.setUIFactory(new PookaDesktopPaneUIFactory());
    } else if (Pooka.getUIFactory() instanceof net.suberic.pooka.gui.PookaMinimalUIFactory) {

      if (Pooka.getProperty("Pooka.guiType", "Desktop").equalsIgnoreCase("Preview"))
        mPookaManager.setUIFactory(new PookaPreviewPaneUIFactory(Pooka.getUIFactory()));
      else
        mPookaManager.setUIFactory(new PookaDesktopPaneUIFactory(Pooka.getUIFactory()));
    }

    if (startup != null)
      startup.setStatus("Pooka.startup.configuringWindow");

    // do all of this on the awt event thread.
    Runnable createPookaUI = new Runnable() {
        public void run() {
          finalFrame.setBackground(Color.lightGray);
          finalFrame.getContentPane().setLayout(new BorderLayout());
          MainPanel panel = new MainPanel(finalFrame);
          mPookaManager.setMainPanel(panel);
          finalFrame.getContentPane().add("Center", panel);

          updateTime("created main panel");
          if (startup != null)
            startup.setStatus("Pooka.startup.starting");

          panel.configureMainPanel();

          updateTime("configured main panel");


          //finalFrame.getContentPane().add("North", panel.getMainToolbar());

          finalFrame.setJMenuBar(panel.getMainMenu());
          finalFrame.getContentPane().add("South", panel.getInfoPanel());
          finalFrame.pack();
          finalFrame.setSize(Integer.parseInt(Pooka.getProperty("Pooka.hsize", "800")), Integer.parseInt(Pooka.getProperty("Pooka.vsize", "600")));

          int x = Integer.parseInt(Pooka.getProperty("Pooka.lastX", "10"));
          int y = Integer.parseInt(Pooka.getProperty("Pooka.lastY", "10"));

          finalFrame.setLocation(x, y);
          updateTime("configured frame");
          if (startup != null)
            startup.hide();
          finalFrame.setVisible(true);
          updateTime("showed frame");

          mPookaManager.getUIFactory().setShowing(true);

          if (Pooka.getProperty("Store", "").equals("")) {
            if (panel.getContentPanel() instanceof MessagePanel) {
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    Pooka.getUIFactory().showEditorWindow(Pooka.getProperty("Pooka._firstRunWizard.label", "Configure Pooka"), "Pooka._firstRunWizard");
                  }
                });
            }
          } else if (mOpenFolders && Pooka.getProperty("Pooka.openSavedFoldersOnStartup", "false").equalsIgnoreCase("true")) {
            panel.getContentPanel().openSavedFolders(mPookaManager.getResources().getPropertyAsVector("Pooka.openFolderList", ""));
          }
          panel.refreshActiveMenus();
        }
      };

    try {
      javax.swing.SwingUtilities.invokeAndWait(createPookaUI);
    } catch (Exception e) {
      System.err.println("caught exception creating ui:  " + e);
      e.printStackTrace();
    }
  }

  /**
   * Stops the main Pooka window.
   */
  public void stopMainPookaWindow(Object pSource) {
    checkUnsentMessages();

    //checkUncachedMessages();

    net.suberic.pooka.thread.FolderTracker ft = mPookaManager.getFolderTracker();
    if (ft != null)
      ft.setStopped(true);

    net.suberic.util.thread.ActionThread searchThread = mPookaManager.getSearchThread();
    if (searchThread != null)
      mPookaManager.getSearchThread().setStop(true);

    closeAllStores(pSource);

    // wait to set the foldertracker as null until after we've closed all
    // the stores.
    mPookaManager.setFolderTracker(null);

    Pooka.getResources().saveProperties();

    try {
      SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            Pooka.getMainPanel().getParentFrame().setVisible(false);
            Pooka.getMainPanel().getParentFrame().dispose();
          }
        });
    } catch (Exception e) {
    }

  }

  /**
   * Checks for any unsent messages.
   */
  void checkUnsentMessages() {
    java.util.List allServers = mPookaManager.getOutgoingMailManager().getOutgoingMailServerList();
    // first stop all the servers.
    java.util.Iterator iter = allServers.iterator();
    while (iter.hasNext()) {
      OutgoingMailServer oms = (OutgoingMailServer) iter.next();
      oms.stopServer();
    }

    int counter = 0;
    iter = allServers.iterator();
    String waitingMessage = Pooka.getProperty("info.exit.waiting.send", "Waiting {0,number} seconds to send unsent messages...");

    while (iter.hasNext()) {
      OutgoingMailServer oms = (OutgoingMailServer) iter.next();
      /*
        while (oms.isSending() && counter < 5) {
        Object[] args = new Object[] { new Integer(5 - counter) };
        Pooka.getUIFactory().showStatusMessage(java.text.MessageFormat.format(waitingMessage, args));
        // wait for 5 seconds for all threads to exit.
        try {
        Thread.currentThread().sleep(1000);
        } catch (Exception e) { }
        counter++;
        }
      */
      while (oms.isSending()) {
        Pooka.getUIFactory().showStatusMessage(Pooka.getProperty("info.exit.waiting.send.noCounter", "Waiting to finish sending unsent messages..."));
        try {
          Thread.currentThread().sleep(1000);
        } catch (Exception e) { }
      }
    }


  }

  /**
   * Closes all stores.
   */
  void closeAllStores(Object pSource) {
    mPookaManager.getStoreManager().cleanup();
    java.util.Vector v = mPookaManager.getStoreManager().getStoreList();
    final java.util.HashMap doneMap = new java.util.HashMap();

    for (int i = 0; i < v.size(); i++) {
      // FIXME:  we should check to see if there are any messages
      // to be deleted, and ask the user if they want to expunge the
      // deleted messages.
      final StoreInfo currentStore = (StoreInfo)v.elementAt(i);
      net.suberic.util.thread.ActionThread storeThread = currentStore.getStoreThread();
      if (storeThread != null) {
        doneMap.put(currentStore, new Boolean(false));
        storeThread.addToQueue(new net.suberic.util.thread.ActionWrapper(new javax.swing.AbstractAction() {

            public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
              try {
                if (currentStore.isConnected()) {
                  currentStore.closeAllFolders(false, true);
                  currentStore.disconnectStore();
                } else {
                  doneMap.put(currentStore, new Boolean(true));
                }
              } catch (Exception e) {
                // ignore.  just say done and exit.
              } finally {
                currentStore.stopStoreThread();
                currentStore.cleanup();
                doneMap.put(currentStore, new Boolean(true));
              }
            }
          }, storeThread), new java.awt.event.ActionEvent(pSource, 1, "store-close"), net.suberic.util.thread.ActionThread.PRIORITY_HIGH);
      }
    }
    long sleepTime = 30000;
    try {
      sleepTime = Long.parseLong(Pooka.getProperty("Pooka.exitTimeout", "30000"));
    } catch (Exception e) {
    }
    long currentTime = System.currentTimeMillis();
    boolean done = false;

    String closingMessage = Pooka.getProperty("info.exit.closing", "Closing Store {0}");
    String waitingMessage = Pooka.getProperty("info.exit.waiting", "Closing Store {0}... Waiting {1,number} seconds.");
    String waitingMultipleMessage = Pooka.getProperty("info.exit.waiting.multiple", "Waiting {0,number} seconds for {1,number} Stores to close.");

    while (! done && System.currentTimeMillis() - currentTime < sleepTime) {
      String waitingStoreName = null;
      int waitingStoreCount = 0;
      try {
        Thread.currentThread().sleep(1000);
      } catch (InterruptedException ie) {
      }
      done = true;
      for (int i = 0; i < v.size(); i++) {
        Object key = v.get(i);
        Boolean value = (Boolean) doneMap.get(key);
        if (value != null && ! value.booleanValue()) {
          done = false;
          waitingStoreCount++;
          if (waitingStoreName == null)
            waitingStoreName = ((StoreInfo) key).getStoreID();
        }
      }

      if (! done) {
        int secondsWaiting = (int) (sleepTime - (System.currentTimeMillis() - currentTime)) / 1000;
        String message = closingMessage;
        Object[] args;
        if (secondsWaiting > 20) {
          args = new Object[] { waitingStoreName };
          message = closingMessage;
        } else if (waitingStoreCount == 1) {
          args = new Object[] { waitingStoreName, new Integer(secondsWaiting) };
          message = waitingMessage;
        } else {
          args = new Object[] { new Integer(secondsWaiting), new Integer(waitingStoreCount) };
          message = waitingMultipleMessage;
        }
        Pooka.getUIFactory().showStatusMessage(java.text.MessageFormat.format(message, args));
      }
    }

  }

  /**
   * Moves Pooka over to a Minimal startup.
   */
  public void stopPookaToTray(Object pSource) {
    final Object fSource = pSource;
    Runnable runMe = new Runnable() {
        public void run() {
          try {
            mShuttingDown = true;

            stopMainPookaWindow(fSource);
            mFrame = null;
            if (mPookaManager.getMainPanel() != null) {
              KeyboardFocusManager.getCurrentKeyboardFocusManager().removePropertyChangeListener(mPookaManager.getMainPanel().getFocusManager());
              mPookaManager.getMainPanel().getInfoPanel().stopThread();
            }
            mPookaManager.setMainPanel(null);
            mPookaManager.getUIFactory().setShowing(false);
            /*
              mPookaManager.setStoreManager(new StoreManager());
              updateTime("created store manager.");

              mPookaManager.getStoreManager().loadAllSentFolders();
              mPookaManager.getOutgoingMailManager().loadOutboxFolders();
            updateTime("loaded sent/outbox");
            */
            mPookaManager.setStoreManager(null);
            mPookaManager.getUserProfileManager().shutdownManager();
            mPookaManager.setUserProfileManager(null);
            mPookaManager.getOutgoingMailManager().stopServers();

            /*
              java.util.Map allListeners = mPookaManager.getResources().getAllListeners();
              java.util.Iterator keys = allListeners.keySet().iterator();
              while (keys.hasNext()) {
              Object o = keys.next();
              Object value = allListeners.get(o);
              if (value instanceof java.util.List) {
              java.util.Iterator values = ((java.util.List) value).iterator();
              while (values.hasNext()) {
              System.err.println("key " + o + ", value " + values.next());
              }
              } else {
              System.err.println("key " + o + ", value " + allListeners.get(o));
              }
              }
            */
          } finally {
            mShuttingDown = false;
          }
          PookaUIFactory newFactory = new PookaMinimalUIFactory(Pooka.getUIFactory());

          mFullStartup=false;

          loadManagers(null);
          mPookaManager.setUIFactory(newFactory);
          if (mPookaManager.getResources().getProperty("Pooka.exitToIcon.notify", "true").equalsIgnoreCase("true")) {
            MessageNotificationManager mnm = newFactory.getMessageNotificationManager();
            if (mnm != null)
              mnm.displayMessage(mPookaManager.getResources().getProperty("info.exitToIcon.title", "System Tray Notification"), mPookaManager.getResources().getProperty("info.exitToIcon", "Pooka has disconnected from you mail servers, but is still running in the System Tray.  To exit Pooka completely, use File->Exit from the toolbar or right-click on the Tray Icon and choose Exit."), MessageNotificationManager.INFO_MESSAGE_TYPE);

          }
        }
      };
    if (Pooka.getMainPanel() != null)
      Pooka.getMainPanel().setCursor(java.awt.Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    Thread stopWindowThread = new Thread(runMe);
    stopWindowThread.start();
  }

  /**
   * Does a minimal startup of Pooka.
   */
  public void startupMinimal() {
    updateTime("startup invoked.");

    loadManagers(null);

    mPookaManager.setUIFactory(new PookaMinimalUIFactory());

    if (mToAddress != null) {
      if (!sendMessageTo(mToAddress, mFromProfile))
        System.err.println("send failed.");
    }
  }

  /**
   * Checks to see if there's already a running instance of Pooka that
   * we can use.
   */
  boolean checkRunningInstance() {
    // first see if there's already a pooka instance running.
    net.suberic.pooka.messaging.PookaMessageSender sender = new net.suberic.pooka.messaging.PookaMessageSender();
    try {
      sender.openConnection();
      // check to make sure that we're connected to a correct version of Pooka.
      if (sender.checkVersion()) {
        // ok, there is one.  try either sending a message or starting
        // up Pooka, whichever we're trying to do.
        if (mFullStartup) {
          sender.sendStartPookaMessage();
          sender.closeConnection();
          System.out.println("contacted already running instance of Pooka.");
          return true;
        } else {
          return sendMessageTo(mToAddress, mFromProfile);
        }
      }
    } catch (Exception e) {

    }

    return false;
  }

  /**
   * This loads all of the background managers that Pooka uses.
   */
  public void loadManagers(net.suberic.pooka.gui.PookaStartup startup) {
    if (startup != null)
      startup.setStatus("Pooka.startup.ssl");
    updateTime("loading ssl");
    StoreManager.setupSSL();
    updateTime("ssl loaded.");

    try {
      UIManager.setLookAndFeel(Pooka.getProperty("Pooka.looknfeel", UIManager.getCrossPlatformLookAndFeelClassName()));
    } catch (Exception e) { System.out.println("Cannot set look and feel...");
    }
    updateTime("set looknfeel");

    if (startup != null)
      startup.setStatus("Pooka.startup.addressBook");
    mPookaManager.setAddressBookManager(new AddressBookManager());
    updateTime("loaded address book");

    mPookaManager.setConnectionManager(new NetworkConnectionManager());
    updateTime("loaded connections");

    mPookaManager.setOutgoingMailManager(new OutgoingMailServerManager());
    updateTime("loaded mailservers");

    mPookaManager.setDateFormatter(new DateFormatter());

    if (startup != null)
      startup.setStatus("Pooka.startup.profiles");
    UserProfileManager profileManager = new UserProfileManager(mPookaManager.getResources());
    mPookaManager.setUserProfileManager(profileManager);
    updateTime("created profiles");

    String mailcapSource = null;
    if (System.getProperty("file.separator").equals("\\")) {
      mailcapSource = mPookaManager.getPookaRoot().getAbsolutePath() + "\\pooka_mailcap.txt";
    } else {
      mailcapSource = mPookaManager.getPookaRoot().getAbsolutePath() + System.getProperty("file.separator") + ".pooka_mailcap";
    }
    try {
      mPookaManager.setMailcap(Pooka.getResourceManager().createMailcap(mailcapSource));
    } catch (java.io.IOException ioe) {
      System.err.println("exception loading mailcap:  " + ioe);
    }

    updateTime("created mailcaps");

    javax.activation.CommandMap.setDefaultCommandMap(mPookaManager.getMailcap());
    javax.activation.FileTypeMap.setDefaultFileTypeMap(mPookaManager.getMimeTypesMap());
    updateTime("set command/file maps");

    if (startup != null)
      startup.setStatus("Pooka.startup.crypto");
    mPookaManager.setCryptoManager(new PookaEncryptionManager(mPookaManager.getResources(), "EncryptionManager"));
    updateTime("loaded encryption manager");

    mPookaManager.setSearchManager(new SearchTermManager("Search"));
    updateTime("created search manager");

    // set up help
    if (startup != null)
      startup.setStatus("Pooka.startup.help");
    try {
      ClassLoader cl = new Pooka().getClass().getClassLoader();
      java.net.URL hsURL = HelpSet.findHelpSet(cl, "net/suberic/pooka/doc/en/help/Master.hs");
      HelpSet hs = new HelpSet(cl, hsURL);
      mPookaManager.setHelpBroker(hs.createHelpBroker());
    } catch (Exception ee) {
      System.out.println("HelpSet net/suberic/pooka/doc/en/help/merge/Master.hs not found:  " + ee);
      ee.printStackTrace();
    }
    updateTime("loaded help");

    if (mFullStartup) {
      // if there's already a MinimalUIFactory, then copy it.
      if (mPookaManager.getUIFactory() != null) {
        if (Pooka.getProperty("Pooka.guiType", "Desktop").equalsIgnoreCase("Preview"))
          mPookaManager.setUIFactory(new PookaPreviewPaneUIFactory(mPookaManager.getUIFactory()));
        else
          mPookaManager.setUIFactory(new PookaDesktopPaneUIFactory(mPookaManager.getUIFactory()));
      } else {
        if (Pooka.getProperty("Pooka.guiType", "Desktop").equalsIgnoreCase("Preview"))
          mPookaManager.setUIFactory(new PookaPreviewPaneUIFactory());
        else
          mPookaManager.setUIFactory(new PookaDesktopPaneUIFactory());
      }

      updateTime("created ui factory");
    }

    mPookaManager.getResources().addValueChangeListener(new net.suberic.util.ValueChangeListener() {

        public void valueChanged(String changedValue) {
          if (Pooka.getProperty("Pooka.guiType", "Desktop").equalsIgnoreCase("Preview")) {
            MessagePanel mp = (MessagePanel) Pooka.getMainPanel().getContentPanel();
            PookaPreviewPaneUIFactory newFactory = new PookaPreviewPaneUIFactory(Pooka.getUIFactory());
            mPookaManager.setUIFactory(newFactory);

            ContentPanel cp = newFactory.createContentPanel(mp);
            Pooka.getMainPanel().setContentPanel(cp);
            Pooka.getMainPanel().setMainToolbar(newFactory.createMainToolbar());
            Pooka.getMainPanel().getFolderPanel().setFolderPanelToolbar(newFactory.createFolderPanelToolbar());
          } else {
            PreviewContentPanel pcp = (PreviewContentPanel) Pooka.getMainPanel().getContentPanel();
            PookaDesktopPaneUIFactory newFactory = new PookaDesktopPaneUIFactory(Pooka.getUIFactory());
            mPookaManager.setUIFactory(newFactory);
            ContentPanel mp = newFactory.createContentPanel(pcp);
            Pooka.getMainPanel().setContentPanel(mp);
            Pooka.getMainPanel().setMainToolbar(newFactory.createMainToolbar());
            Pooka.getMainPanel().getFolderPanel().setFolderPanelToolbar(newFactory.createFolderPanelToolbar());
          }
        }
      }, "Pooka.guiType");

    mPookaManager.getResources().addValueChangeListener(new net.suberic.util.ValueChangeListener() {
        public void valueChanged(String changedValue) {
          PookaUIFactory factory = Pooka.getUIFactory();
          IconManager iconManager = IconManager.getIconManager(Pooka.getResources(), "IconManager._default");
          factory.setIconManager(iconManager);
          factory.setEditorFactory(new PropertyEditorFactory(Pooka.getResources(), iconManager, Pooka.getPookaManager().getHelpBroker()));
        }
      }, "IconManager._default");

    mPookaManager.getResources().addValueChangeListener(new net.suberic.util.ValueChangeListener() {
        public void valueChanged(String changedValue) {
          try {
            UIManager.setLookAndFeel(Pooka.getProperty("Pooka.looknfeel", UIManager.getCrossPlatformLookAndFeelClassName()));
            javax.swing.SwingUtilities.updateComponentTreeUI(javax.swing.SwingUtilities.windowForComponent(Pooka.getMainPanel()));
          } catch (Exception e) {
            System.out.println("Cannot set look and feel..."); }
        }
      }, "Pooka.looknfeel");

    updateTime("created resource listeners");

    // create the MessageListener.
    if (mPookaManager.getMessageListener() == null) {
      PookaMessageListener pmlistener= new PookaMessageListener();
      mPookaManager.setMessageListener(pmlistener);
    }

    /*
      mFrame = new JFrame("Pooka");
      updateTime("created frame");
    */

    java.util.Properties sysProps = System.getProperties();
    sysProps.setProperty("mail.mbox.mailspool", mPookaManager.getResources().getProperty("Pooka.spoolDir", "/var/spool/mail"));
    mPookaManager.setDefaultSession (javax.mail.Session.getDefaultInstance(sysProps, null));
    if (Pooka.getProperty("Pooka.sessionDebug", "false").equalsIgnoreCase("true"))
      mPookaManager.getDefaultSession().setDebug(true);

    updateTime("created session.");
    if (startup != null)
      startup.setStatus("Pooka.startup.mailboxInfo");
    mPookaManager.setStoreManager(new StoreManager());
    updateTime("created store manager.");

    mPookaManager.getStoreManager().loadAllSentFolders();
    mPookaManager.getOutgoingMailManager().loadOutboxFolders();
    updateTime("loaded sent/outbox");

  }

  /**
   * This parses any command line arguments, and makes the appropriate
   * changes.
   */
  public void parseArgs(String[] argv) {
    if (argv == null || argv.length < 1)
      return;

    String mailAddress = null;
    String selectedProfile = null;

    for (int i = 0; i < argv.length; i++) {
      if (argv[i] != null) {
        if (argv[i].equals("-nf") || argv[i].equals("--noOpenSavedFolders")) {
          mOpenFolders = false;
        } else if (argv[i].equals("-rc") || argv[i].equals("--rcfile")) {
          String filename = argv[++i];
          if (filename == null) {
            System.err.println("error:  no startup file specified.");
            printUsage();
            System.exit(-1);
          }

          mPookaManager.setLocalrc(filename);
        } else if (argv[i].equals("--http")) {
          mUseHttp = true;
          mUseLocalFiles = false;
        } else if (argv[i].equals("--jdbc")) {
          mUseJdbc = true;
        } else if (argv[i].equals("--jdriver")) {
          if (argv.length < i + 2) {
            System.err.println("error:  no jdbc driver specified.");
            printUsage();
            System.exit(-1);
          }
          System.setProperty("JDBCPreferences.driverName", argv[++i]);
        } else if (argv[i].equals("--jurl")) {
          if (argv.length < i + 2) {
            System.err.println("error:  no jdbc url specified.");
            printUsage();
            System.exit(-1);
          }
          System.setProperty("JDBCPreferences.url", argv[++i]);
        } else if (argv[i].equals("--juser")) {
          if (argv.length < i + 2) {
            System.err.println("error:  no jdbc user specified.");
            printUsage();
            System.exit(-1);
          }
          System.setProperty("JDBCPreferences.user", argv[++i]);
        } else if (argv[i].equals("--jpassword")) {
          if (argv.length < i + 2) {
            System.err.println("error:  no jdbc password specified.");
            printUsage();
            System.exit(-1);
          }
          System.setProperty("JDBCPreferences.password", argv[++i]);
        } else if (argv[i].equals("-open")) {
          if (argv.length < i + 2) {
            System.err.println("error:  no address specified.");
            printUsage();
            System.exit(-1);
          }
          mToAddress = argv[++i];
          mFullStartup = false;
        } else if (argv[i].equals("--minimal")) {
          mFullStartup = false;
        } else if (argv[i].equals("--from")) {
          mFromProfile = argv[++i];
          if (mFromProfile == null) {
            System.err.println("error:  no from profile specified.");
            printUsage();
            System.exit(-1);
          }
          mFullStartup = false;
        } else if (argv[i].equals("--help")) {
          printUsage();
          System.exit(0);
        } else if (argv[i].equals("-r") || argv[i].equals("--root")) {
          String filename = argv[++i];
          if (filename == null) {
            System.err.println("error:  no root directory specified.");
            printUsage();
            System.exit(-1);
          }

          try {
            String error = null;
            File f = new File(filename);
            if (! f.exists()) {
              error = "error:  root directory " + filename + " does not exist.";
            } else if (! f.canRead()) {
              error = "error:  root directory " + filename + " cannot be read.";
            } else if (! f.isDirectory()) {
              error = "error:  root directory " + filename + " cannot be read.";
            }
            if (error != null) {
              System.err.println(error);
              printUsage();
              System.exit(-1);
            }
            mPookaManager.setPookaRoot(f);
          } catch (Exception e) {
            System.err.println("error:  no startup file specified.");
            printUsage();
            System.exit(-1);
          }
        } else {
          // if invalid arguments are specified
          printUsage();
          System.exit(0);
        }
      }
    }
  }

  /**
   * Prints the usage information.
   */
  public void printUsage() {
    System.out.println(Pooka.getProperty("info.startup.help", "\nUsage:  net.suberic.pooka.Pooka [OPTIONS]\n\n  -nf, --noOpenSavedFolders    don\'t open saved folders on startup.\n  -r, --root DIRECTORY         use the given directory as the pooka home.\n  -rc, --rcfile FILE           use the given file as the pooka startup file.\n  --http                       runs with a configuration file loaded via http\n  -open ADDRESS                sends a new message to ADDRESS.\n       [--from USER]           [from user USER].\n  --minimal                    startup to system tray only.\n  --help                       shows these options.\n"));
  }

  /**
   * Checks to make sure that the Java version is valid.
   */
  public boolean checkJavaVersion() {
    // Pooka 1.1 only runs on JDK 1.4 or higher.
    String javaVersion = System.getProperty("java.version");
    if (javaVersion.compareTo("1.6") >= 0) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Called if an incorrect version of Java is being used.
   */
  private void versionError() {
    Runnable runMe = new Runnable() {
        public void run() {
          String errorString = Pooka.getProperty("error.incorrectJavaVersion", "Error running Pooka.  This version (2.0) \nof Pooka requires a 1.6 JDK.  \n\nFor JDK 1.4, please use a release of Pooka 1.1.\n\nPooka can be downloaded from\nhttp://pooka.sourceforge.net/\n\nYour JDK version:  ");
          javax.swing.JOptionPane.showMessageDialog(null, errorString + System.getProperty("java.version"));
        }
      };

    if (SwingUtilities.isEventDispatchThread())
      runMe.run();
    else {
      try {
        SwingUtilities.invokeAndWait(runMe);
      } catch (Exception ie) {
      }
    }
  }

  /**
   * Sends a message to the given mail address on startup.
   */
  public boolean sendMessageTo(String pAddress, String pProfile) {
    // first see if there's already a pooka instance running.
    net.suberic.pooka.messaging.PookaMessageSender sender = new net.suberic.pooka.messaging.PookaMessageSender();
    try {
      sender.openConnection();
      // check to make sure that we're connected to a correct version of Pooka.
      if (sender.checkVersion()) {
        sender.openNewEmail(pAddress, pProfile);
      } else
        return false;
    } catch (Exception e) {
      return false;
    } finally {
      if (sender.isConnected())
        sender.closeConnection();
    }

    mToAddress = null;
    mFromProfile = null;

    return true;
  }

  private long mStartTime = 0;
  private long mLastUpdate = 0;
  /**
   * debug.
   */
  public void updateTime(String message) {
    if (mPookaManager.getResources() != null && Pooka.isDebug()) {
      long current = System.currentTimeMillis();
      System.err.println(message + ", time " + (current - mLastUpdate) + ", total " + (current - mStartTime));
      mLastUpdate = current;
    }
  }

  /**
   * Returns true if we're in the process of shutting down.
   */
  public boolean isShuttingDown() {
    return mShuttingDown;
  }

  /**
   * Gets the logger for this class.
   */
  public Logger getLogger() {
    return Logger.getLogger("Pooka.debug.startupManager");
  }

}
