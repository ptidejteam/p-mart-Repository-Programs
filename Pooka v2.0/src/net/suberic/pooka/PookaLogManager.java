package net.suberic.pooka;

import net.suberic.util.*;

import java.util.logging.*;
import java.util.*;

/**
 * This class manages logging for Pooka.  It basically provides a bridge
 * between Pooka configuration files and the JDK 1.4 Logging system.
 */

public class PookaLogManager implements ValueChangeListener {
  
  // the logging strings that we're currently monitoring
  Set mMonitoredLogStrings = null;
  
  // the default list of log settings we monitor
  String[] mDefaultLogSettings = new String[] {
    "Pooka.debug",
    "Pooka.debug.session",
    "editors.debug",
    "Pooka.debug.gui",
    "Pooka.debug.gui.focus",
    "Pooka.debug.gui.filechooser",
    "Pooka.debug.folderTracker",
    "Pooka.debug.logManager",
    "Pooka.debug.startupManager",
    "Pooka.debug.messaging",
    "Pooka.debug.sslFactory"
  };
  
  
  /**
   * Constructor.  Sets itself as a valueChangeListener for all of the
   * log settings configured.
   */
  public PookaLogManager() {
    VariableBundle globalBundle = Pooka.getResources();
    mMonitoredLogStrings = new HashSet(Arrays.asList(mDefaultLogSettings));
    
    Iterator it = mMonitoredLogStrings.iterator();
    while (it.hasNext()) {
      globalBundle.addValueChangeListener(this, (String) it.next() + ".logLevel");
    }
    
    refresh();
    
    configureListeners();
    
    // set up logging to log all messages.  stupid.
    Logger global = Logger.getLogger("");
    Handler[] globalHandlers = global.getHandlers();
    for (int i = 0; i < globalHandlers.length; i++) {
      globalHandlers[i].setLevel(Level.ALL);
    }
  }
  
  
  /**
   * Refreshes all logging states from the current configuration.
   */
  public void refresh() {
    Iterator it = mMonitoredLogStrings.iterator();
    while (it.hasNext()) {
      String current = (String) it.next();
      refresh(current);
    }
  }

  /**
   * Refreshes the logging state for the given property.
   */
  public void refresh(String pKey) {
    String levelKey = Pooka.getProperty(pKey + ".logLevel", "DEFAULT");
    Level newLevel = null;
    if (levelKey == null || levelKey.equals("") || levelKey.equalsIgnoreCase("DEFAULT")) {
      newLevel = null;
    } else {
      newLevel = Level.parse(levelKey);
    }
    getLogger().log(Level.FINE, "set log level for " + pKey + " to " + newLevel);
    setLogLevel(pKey, newLevel);
    
  }
  
  /**
   * Sets up additional listeners.
   */
  public void configureListeners() {
    // focus listener
    java.awt.KeyboardFocusManager mgr = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager();
    mgr.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
	public void propertyChange(java.beans.PropertyChangeEvent evt) {
	  Logger logger = Logger.getLogger("Pooka.debug.gui.focus");
	  Level logLevel = Level.FINEST;
	  if (evt.getPropertyName().equalsIgnoreCase("permanentFocusOwner")) {
	    logLevel = Level.FINE;
	  } else if (evt.getPropertyName().equalsIgnoreCase("focusOwner") || evt.getPropertyName().equalsIgnoreCase("focusOwner")) {
	    logLevel = Level.FINER;
	  }
	  String oldValue = "null";
	  String newValue = "null";
	  if (evt.getOldValue() != null) {
	    oldValue = evt.getOldValue().getClass().getName();
	  }
	  if (evt.getNewValue() != null) {
	    newValue = evt.getNewValue().getClass().getName();
	  }
	  logger.log(logLevel, evt.getPropertyName() + ":  oldValue=" + oldValue + "; newValue=" + newValue);

	}
      });
    
  }
  
  /**
   * Sets the appropriate log setting.
   */
  public void setLogLevel(String pName, Level pLogLevel) {
    Logger current = Logger.getLogger(pName);
    if (current.getLevel() != pLogLevel) {
      current.setLevel(pLogLevel);

    }

    if (pName == "Pooka.debug") {
      setLogLevel("", pLogLevel);
    }

  }
  
  // ValueChangeListener
  /**
   * Responds to a change in a configured value.
   */
    public void valueChanged(String pChangedValue) {
      String key = pChangedValue;
      // this should always end with .logLevel.
      if (pChangedValue.endsWith(".logLevel")) {
	key = pChangedValue.substring(0, pChangedValue.length() - 9);
      }
      
      refresh(key);
    }
  
  /**
   * Adds a logger for us to watch over.
   */
  public void addLogger(String pKey) {
    if (! mMonitoredLogStrings.contains(pKey)) {
      mMonitoredLogStrings.add(pKey);
      Pooka.getResources().addValueChangeListener(this, pKey + ".logLevel");
      String levelKey = Pooka.getProperty(pKey + ".logLevel", "DEFAULT");
      Level newLevel = null;
      if (levelKey == null || levelKey.equals("") || levelKey.equalsIgnoreCase("DEFAULT")) {
	newLevel = null;
      } else {
	newLevel = Level.parse(levelKey);
      }
      setLogLevel(pKey, newLevel);
      getLogger().log(Level.FINE, "added key " + pKey + "; set value to " + newLevel);
    }
  }
  
  /**
   * Removes a logger from the monitored list.
   */
  public void removeLogger(String pKey) {
    if (mMonitoredLogStrings.contains(pKey)) {
      mMonitoredLogStrings.remove(pKey);
      Pooka.getResources().removeValueChangeListener(this, pKey + ".logLevel"); 
      getLogger().log(Level.FINE, "removed key " + pKey);
    }
  }
  
  /**
   * Gets the logger for this class.
   */
  public Logger getLogger() {
    return Logger.getLogger("Pooka.debug.logManager");
  }
}
