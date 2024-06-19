package net.suberic.pooka;

import java.util.*;

import net.suberic.util.*;

/**
 * <p>An object which manages NetworkConnection resources.</p>
 *
 * @author Allen Petersen
 * @version $Revision$
 */
public class NetworkConnectionManager implements ItemCreator, ItemListChangeListener {

  private ItemManager manager;
  private LinkedList listenerList = new LinkedList();

  /**
   * <p>Creates a new NetworkConnectionManager.</p>
   */
  public NetworkConnectionManager() {
    createConnectionList();
  }

  //-----------------------
  // public interface.

  /**
   * This listens for ItemListChangeEvents, which result from changes to the
   * "Connection" property.  The result is that refreshConnections() is called,
   * and then the event is passed to listeners to this object.
   */
  public void itemListChanged(ItemListChangeEvent e) {
    fireItemListChanged(e);
  }

  /**
   * This returns a Vector with all the currently registered NetworkConnection
   * objects.
   */
  public java.util.Vector getConnectionList() {
    return manager.getItems();
  }

  /**
   * This adds the connection with the given connectionName to the
   * allConnections list.
   */
  public void addConnection(String connectionName) {
    manager.addItem(connectionName);
  }

  /**
   * This adds the connections with the given connectionNames to the allConnections list.
   */
  public void addConnection(String[] connectionName) {
    manager.addItem(connectionName);
  }

  /**
   * This removes the connection with the given connectionName.
   */
  public void removeConnection(String connectionName) {
    manager.removeItem(connectionName);
  }

  /**
   * This removes the connections with the given connectionNames.
   */
  public void removeConnection(String[] connectionNames) {
    manager.removeItem(connectionNames);
  }

  /**
   * This removes the given NetworkConnection.
   */
  public void removeConnection(NetworkConnection connection) {
    manager.removeItem(connection);
  }

  /**
   * This removes the given NetworkConnections.
   */
  public void removeConnection(NetworkConnection[] connections) {
    manager.removeItem(connections);
  }

  /**
   * This returns the NetwordConnection with the given connectionName if it
   * exists; otherwise, returns null.
   */
  public NetworkConnection getConnection(String connectionID) {
    return (NetworkConnection) manager.getItem(connectionID);
  }

  /**
   * This returns the NetwordConnection with the given connectionName if it
   * exists; otherwise, returns null.
   */
  public NetworkConnection getDefaultConnection() {
    String defaultName = Pooka.getProperty("Connection._default", "_default");
    return (NetworkConnection) manager.getItem(defaultName);
  }

  /**
   * This adds a ItemListChangeListener to the local listener list.
   */
  public void addItemListChangeListener(ItemListChangeListener ilcl) {
    if (! listenerList.contains(ilcl))
      listenerList.add(ilcl);
  }

  /**
   * This removes a ItemListChangeListener from the local listener list.
   */
  public void removeItemListChangeListener(ItemListChangeListener ilcl) {
    listenerList.remove(ilcl);
  }

  /**
   * This notifies all listeners that the ConnectionList has changed.
   */
  public void fireItemListChanged(ItemListChangeEvent e) {
    for (int i = 0; i < listenerList.size(); i++)
      ((ItemListChangeListener)listenerList.get(i)).itemListChanged(e);
  }


  /**
   * This creates a new NetworkConnection.
   */
  public Item createItem(VariableBundle sourceBundle, String resourceString, String itemID) {
    NetworkConnection returnValue = new NetworkConnection(itemID);
    returnValue.configure();
    return returnValue;
  }

  //---------------------------
  // the background stuff.

  /**
   * This loads and creates all the NetworkConnections using the "Connection"
   * property of the main Pooka VariableBundle.
   */
  private void createConnectionList() {
    manager = new ItemManager("Connection", Pooka.getResources(), this);
    List items = manager.getItems();
    if (items.isEmpty()) {
      // should always be a connection.
      Pooka.setProperty("Connection", "Default");
      Pooka.setProperty("Connection._default", "Default");
      Pooka.setProperty("Connection.Default.valueOnStartup", "Connected");
      manager = new ItemManager("Connection", Pooka.getResources(), this);
    }
    manager.addItemListChangeListener(this);
  }
}
