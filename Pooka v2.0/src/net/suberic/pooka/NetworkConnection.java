package net.suberic.pooka;

import java.net.*;
import net.suberic.util.*;

/**
 * <p>Represents a Network connection.  This is primarily used to keep
 * track of connections, so that Pooka will know whether or not to attempt
 * to contact a particular server or not.</p>
 *
 * @author Allen Petersen
 * @version $Revision$
 */
public class NetworkConnection implements net.suberic.util.Item {

  String id = null;

  String propertyName = null;

  String connectCommand = null;

  String disconnectCommand = null;

  InetAddress testAddress = null;

  int testPort = -1;

  boolean disconnectRequested = false;

  boolean disconnectCommandRequested = false;

  int status = DISCONNECTED;

  public static int CONNECTED = 0;

  public static int DISCONNECTED = 5;

  public static int UNAVAILABLE = 10;

  private java.util.LinkedList listenerList = new java.util.LinkedList();

  private java.util.LinkedList lockList = new java.util.LinkedList();

  /**
   * <p>Creates a new NetworkConnection from the given property.</p>
   */
  public NetworkConnection (String newId) {
    id = newId;
    propertyName = "Connection." + newId;

  }

  /**
   * <p>A command that should be run when this network connection is
   * brought up.</p>
   *
   * <p>Returns <code>null</code> if no command needs to be run on
   * connection.</p>
   */
  public String getConnectCommand() {
    return connectCommand;
  }

  /**
   * <p>Configures this conection.</p>
   */
  protected void configure() {
    VariableBundle bundle = Pooka.getResources();

    connectCommand = bundle.getProperty(getItemProperty() + ".connectCommand", "");
    disconnectCommand = bundle.getProperty(getItemProperty() + ".disconnectCommand", "");

    String testAddressString = bundle.getProperty(getItemProperty() + ".testAddress", "");
    String testPortString = bundle.getProperty(getItemProperty() + ".testPort", "");
    if (testAddressString != "" && testPortString != "") {
      try {
        testAddress = InetAddress.getByName(testAddressString);
        testPort = Integer.parseInt(testPortString);
      } catch (Exception e) {
        testAddress = null;
        testPort = -1;
      }
    }

    String onStartup = bundle.getProperty(getItemProperty() + ".valueOnStartup", "Unavailable");

    if (onStartup.equalsIgnoreCase("Connected")) {
      this.connect();
    } else if (onStartup.equalsIgnoreCase("Unavailable")) {
      status = UNAVAILABLE;
    }

  }

  /**
   * <p>A command that should be run when this network connection is
   * brought down.</p>
   *
   * <p>Returns <code>null</code> if no command needs to be run on
   * disconnection.</p>
   */
  public String getDisconnectCommand() {
    return disconnectCommand;
  }

  /**
   * <p>Connect to this network service.</p>
   *
   * @param runConnectCommand whether or not we should run the
   *        <code>connectCommand</code>, if there is one.
   * @return the new status of the server.
   */
  public int connect(boolean runConnectCommand) {
    return connect(runConnectCommand, false);
  }

  /**
   * <p>Connect to this network service.</p>
   *
   * @param runConnectCommand whether or not we should run the
   *        <code>connectCommand</code>, if there is one.
   * @param isInteractive whether or not we should prompt the user if
   *        the connection test fails after the connection.
   * @return the new status of the server.
   */
  public int connect(boolean runConnectCommand, boolean isInteractive) {

    try {
      if (runConnectCommand) {
        String preCommand = getConnectCommand();
        if (preCommand != null && preCommand.length() > 0) {
          Process p = Runtime.getRuntime().exec(preCommand);
          p.waitFor();
        }
      }

      if (status != CONNECTED) {
        boolean connectionSucceeded = checkConnection();
        if (! connectionSucceeded && isInteractive) {
          // check to see if we want to mark this as connected anyway.
          int response = Pooka.getUIFactory().showConfirmDialog("Connection to test port " + testAddress.getHostAddress() + ":" + testPort + " failed.  Mark this connection as unavailable?", "Test of " + getItemID() + " failed.", javax.swing.JOptionPane.YES_NO_OPTION);
          if (response == javax.swing.JOptionPane.NO_OPTION)
            connectionSucceeded = true;
          else
            status = UNAVAILABLE;
        }

        if (connectionSucceeded) {
          status = CONNECTED;
          fireConnectionEvent();
        }
      }
    } catch (Exception ex) {
      System.out.println("Could not run connect command:");
      ex.printStackTrace();
    }

    return status;
  }

  /**
   * <p>Connect to this network service.</p>
   *
   * @return the new status of the server.
   */
  public int connect() {
    return connect(true);
  }

  /**
   * <p>Disconnect from this network service.</p>
   *
   * @param runDisonnectCommand whether or not we should run the
   *        <code>disconnectCommand</code>, if there is one.
   * @return the new status of the server.
   */
  public int disconnect(boolean runDisconnectCommand) {
    synchronized(this) {
      if (lockList.isEmpty()) {
        return doDisconnect(runDisconnectCommand);
      } else {
        disconnectRequested = true;
        if (runDisconnectCommand)
          disconnectCommandRequested = true;

        return status;
      }

    }
  }

  private int doDisconnect(boolean runDisconnectCommand) {
    try {
      if (status != DISCONNECTED) {
        if (runDisconnectCommand) {
          String postCommand = getDisconnectCommand();
          if (postCommand != null && postCommand.length() > 0) {
            Process p = Runtime.getRuntime().exec(postCommand);
            p.waitFor();
          }
        }

        status = DISCONNECTED;
        fireConnectionEvent();
      } else {
      }
    } catch (Exception ex) {
      System.out.println("Could not run disconnect command:");
      ex.printStackTrace();
    }

    return status;
  }

  /**
   * <p>Disconnect from this network service.</p>
   *
   * @return the new status of the server.
   */
  public int disconnect() {
    return disconnect(true);
  }

  /**
   * <p>Checks this connection to see if it's actually up.  This
   * implementation uses the testAddress and testPort settings to open
   * a TCP connection.  This returns whether or not the connection
   * succeeds.
   */
  public boolean checkConnection() {
    if (testAddress != null && testPort > -1) {
      try {
        Socket testSocket = new Socket(testAddress, testPort);
        testSocket.close();
        return true;
      } catch (Exception e) {
        return false;
      }
    }

    // if there's no test case, assume that we're ok.
    return true;
  }

  /**
   * <p>Mark this network service as unavailable.  Note that if there
   * is a disconnectCommand, this does <em>not</em> run it.</p>
   *
   * @return the new status of the server.
   */
  public int makeUnavailable() {
    if (status != UNAVAILABLE) {
      status = UNAVAILABLE;
      fireConnectionEvent();
    }
    return status;
  }

  /**
   * <p>Returns the current status of this NetworkConnection.</p>
   */
  public int getStatus() {
    return status;
  }

  /**
   * <p>Gets an ConnectionLock for this NetworkConnection.
   */
  public synchronized ConnectionLock getConnectionLock() {
    if (getStatus() == CONNECTED) {
      ConnectionLock cl = new ConnectionLock();
      lockList.add(cl);
      return cl;
    } else
      return null;
  }

  /**
   * <p>Releases the given ConnectionLock.
   */
  public synchronized void releaseLock(ConnectionLock cl) {
    lockList.remove(cl);
    if (lockList.isEmpty() && disconnectRequested) {
      doDisconnect(disconnectCommandRequested);
      disconnectRequested = false;
      disconnectRequested = false;
    }
  }

  /**
   * Notifies all listeners that the Network connection status has
   * changed.
   */
  public void fireConnectionEvent() {
    for (int i = 0; i < listenerList.size(); i++) {
      ((NetworkConnectionListener) listenerList.get(i)).connectionStatusChanged(this, getStatus());
    }
  }

  /**
   * Adds a NetworkConnectionListener to the listener list.
   */
  public void addConnectionListener(NetworkConnectionListener newListener) {
    if (!listenerList.contains(newListener))
      listenerList.add(newListener);
  }

  /**
   * Removes a NetworkConnectionListener from the listener list.
   */
  public void removeConnectionListener(NetworkConnectionListener oldListener) {
    if (listenerList.contains(oldListener))
      listenerList.remove(oldListener);
  }

  /**
   * <p>The Item ID for this NetworkConnection.</p>
   */
  public String getItemID() {
    return id;
  }

  /**
   * <p>The Item property for this NetworkConnection.  This is usually
   * Connection.<i>itemID</i>.</p>
   */
  public String getItemProperty() {
    return propertyName;
  }

  /**
   * Returns the ItemID of this NetworkConnection.
   */
  public String toString() {
    return getItemID();
  }

  /**
   * an object that represents a lock on this connection.  useful if you
   * have several threads depending on having this connection stay open.
   */
  public class ConnectionLock {
    public ConnectionLock() {

    }

    /**
     * Releases this lock.
     */
    public void release() {
      releaseLock(this);
    }
  }
}
