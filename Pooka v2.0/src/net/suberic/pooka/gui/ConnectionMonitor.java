package net.suberic.pooka.gui;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Insets;

import net.suberic.pooka.*;
import net.suberic.util.*;
import net.suberic.util.gui.ConfigurablePopupMenu;
import net.suberic.util.gui.IconManager;

/**
 * This class monitors the status of the network connection(s) that
 * Pooka uses.
 *
 * @author Allen Petersen
 * @version $Revision$
 */
public class ConnectionMonitor extends Box implements NetworkConnectionListener, net.suberic.util.ItemListChangeListener {

  /** the Image for CONNECTED connections. */
  public ImageIcon connectedImage = null;

  /** the Image for DISCONNECTED connections. */
  public ImageIcon disconnectedImage = null;

  /** the Image for UNAVAILABLE connections. */
  public ImageIcon unavailableImage = null;
  
  /** The combo box for selecting which connection to show.  */
  JComboBox comboBox;

  /** The panel where the status is shown. */
  JLabel statusPanel;

  ConfigurablePopupMenu popupMenu;

  /** The default actions supported by this component. */
  Action[] defaultActions = new Action[] {
    new ConnectAction(),
    new DisconnectAction(),
    new UnavailableAction()
      };

  /**
   * Creates a new, empty ConnectionMonitor.
   */
  public ConnectionMonitor() {
    super(BoxLayout.X_AXIS);
    loadImages();
    setupComponents();
  }

  /**
   * Creates the graphical parts of this component.  There are basically 
   * two parts here:  a JComboBox for the list of connections, and a 
   * JPanel to show the status of the current connection.
   */
  private void setupComponents() {
    comboBox = new JComboBox();
    comboBox.addItemListener(new ItemListener() {
	public void itemStateChanged(ItemEvent e) {
	  if (e.getStateChange() == ItemEvent.SELECTED) {
	    updateStatus();
	  }
	}
      });

    statusPanel = new JLabel();
    statusPanel.addMouseListener(new MouseAdapter() {
	
	public void mousePressed(MouseEvent e) {
	  if (e.isPopupTrigger()) {
	    showPopupMenu(e);
	  }
	}

	public void mouseReleased(MouseEvent e) {
	  if (e.isPopupTrigger()) {
	    showPopupMenu(e);
	  }
	}
      });

    statusPanel.setIcon(connectedImage);
    statusPanel.setBorder(BorderFactory.createEmptyBorder());
    comboBox.setBorder(BorderFactory.createEmptyBorder());
    this.setBorder(BorderFactory.createEmptyBorder());

    //statusPanel.getInsets().set(0,0,0,0);
    //comboBox.getInsets().set(0,0,0,0);
    //this.getInsets().set(0,0,0,0);
    this.add(comboBox);
    this.add(statusPanel);
  }

  /**
   * Sets this ConnectionMonitor up to monitor all the connection controlled
   * by the given NetworkConnectionManager.
   */
  public void monitorConnectionManager(NetworkConnectionManager newManager) {
    java.util.Vector currentList = newManager.getConnectionList();
    if (currentList != null && currentList.size() > 0) {
      NetworkConnection[] newConnections = new NetworkConnection[currentList.size()];
      System.arraycopy(currentList.toArray(), 0, newConnections, 0, currentList.size());
      addConnections(newConnections);
    }

    newManager.addItemListChangeListener(this);
  }

  /**
   * Loads the images for the ConnectionMonitor.
   */
  private void loadImages() {
    IconManager iconManager = Pooka.getUIFactory().getIconManager();
    connectedImage = iconManager.getIcon(Pooka.getProperty("ConnectionMonitor.connectedIcon", "ConnectionMonitor.ConnectedIcon"));
    disconnectedImage = iconManager.getIcon(Pooka.getProperty("ConnectionMonitor.disconnectedIcon", "ConnectionMonitor.DisconnectedIcon"));
    unavailableImage = iconManager.getIcon(Pooka.getProperty("ConnectionMonitor.unavailableIcon", "ConnectionMonitor.UnavailableIcon"));
  }

  /**
   * This creates and shows a PopupMenu for this component.  
   */
  public void showPopupMenu(MouseEvent e) {
    if (popupMenu == null) {
      popupMenu = new ConfigurablePopupMenu();
      popupMenu.configureComponent("ConnectionMonitor.popupMenu", Pooka.getResources());	
      popupMenu.setActive(getActions());
    }

    popupMenu.show(this, e.getX(), e.getY());
    
  }

  /**
   * Updates the status for the currently selected Connection.
   */
  public void updateStatus() {
    NetworkConnection selectedConnection = getSelectedConnection();
    if (selectedConnection != null) {
      int status = selectedConnection.getStatus();
      if (status == NetworkConnection.CONNECTED) {
	statusPanel.setIcon(connectedImage);
	statusPanel.setToolTipText("Connected");
      } else if (status == NetworkConnection.DISCONNECTED) {
	statusPanel.setIcon(disconnectedImage);
	statusPanel.setToolTipText("Disonnected");
      } else if (status == NetworkConnection.UNAVAILABLE) {
	statusPanel.setIcon(unavailableImage);
	statusPanel.setToolTipText("Unavailable");
      }
    } else {
      statusPanel.setIcon(connectedImage);
    }
  }

  /**
   * Notifies this component that the state of a network connection has
   * changed.
   */
  public void connectionStatusChanged(NetworkConnection connection, int newStatus) {
    NetworkConnection currentConnection = getSelectedConnection();
    if (connection == currentConnection) {
      updateStatus();
    }
  }

  /**
   * Handles added or removed events from the NetworkConnectionManager.
   */
  public void itemListChanged(ItemListChangeEvent e) {
    Item[] added = e.getAdded();
    if (added != null && added.length > 0) {
      NetworkConnection[] addedConnections = new NetworkConnection[added.length];
      System.arraycopy(added, 0, addedConnections, 0, added.length);
      addConnections(addedConnections);
    }

    Item[] removed = e.getRemoved();
    if (removed != null && removed.length > 0) {
      NetworkConnection[] removedConnections = new NetworkConnection[removed.length];
      System.arraycopy(removed, 0, removedConnections, 0, removed.length);
      removeConnections((NetworkConnection[]) removedConnections);
    }
  }

  /**
   * Adds the given NetworkConnection(s) to the JComboBox list.
   */
  public void addConnections(NetworkConnection[] newConnections) {
    if (newConnections != null && newConnections.length > 0) {
      for (int i = 0 ; i < newConnections.length; i++) {
	if (newConnections[i] != null) {
	  comboBox.addItem(newConnections[i]);
	  newConnections[i].addConnectionListener(this);
	}
      }
    }
  }

  /**
   * Removed the given NetworkConnection(s) from being monitored by
   * this component.
   */
  public void removeConnections(NetworkConnection[] toRemove) {
    if (toRemove != null && toRemove.length > 0) {
      for (int i = 0 ; i < toRemove.length; i++) {
	if (toRemove[i] != null) {
	  comboBox.removeItem(toRemove[i]);
	  toRemove[i].removeConnectionListener(this);
	}
      }
    }
  }

  /**
   * Returns the currently selected NetworkConnection.
   */
  public NetworkConnection getSelectedConnection() {
    return (NetworkConnection) comboBox.getSelectedItem();
  }

  /**
   * Returns the Actions supported by this Component.
   */
  public Action[] getActions() {
    return defaultActions;
  }

  public class ConnectAction extends AbstractAction {

    ConnectAction() {
      super("connection-connect");
    }

    public void actionPerformed(ActionEvent e) {
      NetworkConnection connection = getSelectedConnection();
      if (connection != null && connection.getStatus() != NetworkConnection.CONNECTED) {
	connection.connect(true, true);
      }
    }
  }

  public class DisconnectAction extends AbstractAction {

    DisconnectAction() {
      super("connection-disconnect");
    }

    public void actionPerformed(ActionEvent e) {
      NetworkConnection connection = getSelectedConnection();
      if (connection != null && connection.getStatus() == NetworkConnection.CONNECTED) {
	connection.disconnect();
      }
    }
  }

  public class UnavailableAction extends AbstractAction {

    UnavailableAction() {
      super("connection-unavailable");
    }

    public void actionPerformed(ActionEvent e) {
      NetworkConnection connection = getSelectedConnection();
      if (connection != null) {
	connection.makeUnavailable();
      }
    }
  }


}
