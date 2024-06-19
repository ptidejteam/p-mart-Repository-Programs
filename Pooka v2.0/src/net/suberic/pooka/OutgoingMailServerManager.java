package net.suberic.pooka;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import net.suberic.util.*;

/**
 * <p>An object which manages OutgoingMailServer resources.</p>
 *
 * @author Allen Petersen
 * @version $Revision$
 */
public class OutgoingMailServerManager implements ItemCreator, ItemListChangeListener {
  
  private ItemManager manager;
  private LinkedList listenerList = new LinkedList();

  private DefaultOutgoingMailServer defaultServer = null;

  /**
   * <p>Creates a new OutgoingMailServerManager.</p>
   */
  public OutgoingMailServerManager() {
    createOutgoingMailServerList();
  }

  //-----------------------
  // public interface.
  
  /**
   * This listens for ItemListChangeEvents, which result from changes to the 
   * "OutgoingMailServer" property.  The result is that refreshOutgoingMailServers() is called,
   * and then the event is passed to listeners to this object.
   */
  public void itemListChanged(ItemListChangeEvent e) {
    fireItemListChanged(e);
  }

  /**
   * This returns a Vector with all the currently registered OutgoingMailServer
   * objects.
   */
  public java.util.Vector getOutgoingMailServerList() {
    return manager.getItems();
  }
  
  /**
   * This adds the OutgoingMailServer with the given OutgoingMailServerName to the 
   * allOutgoingMailServers list.
   */
  public void addOutgoingMailServer(String OutgoingMailServerName) {
    manager.addItem(OutgoingMailServerName);
  }
  
  /**
   * This adds the OutgoingMailServers with the given OutgoingMailServerNames to the allOutgoingMailServers list.
   */
  public void addOutgoingMailServer(String[] OutgoingMailServerName) {
    manager.addItem(OutgoingMailServerName);
  }
  
  /**
   * This removes the OutgoingMailServer with the given OutgoingMailServerName.
   */
  public void removeOutgoingMailServer(String OutgoingMailServerName) {
    manager.removeItem(OutgoingMailServerName);
  }

  /**
   * This removes the OutgoingMailServers with the given OutgoingMailServerNames.
   */
  public void removeOutgoingMailServer(String[] OutgoingMailServerNames) {
    manager.removeItem(OutgoingMailServerNames);
  }

  /**
   * This removes the given OutgoingMailServer.
   */
  public void removeOutgoingMailServer(OutgoingMailServer OutgoingMailServer) {
    manager.removeItem(OutgoingMailServer);
  }
  
  /**
   * This removes the given OutgoingMailServers.
   */
  public void removeOutgoingMailServer(OutgoingMailServer[] OutgoingMailServers) {
    manager.removeItem(OutgoingMailServers);
  }

  /**
   * This returns the NetwordOutgoingMailServer with the given OutgoingMailServerName if it 
   * exists; otherwise, returns null.
   */
  public OutgoingMailServer getOutgoingMailServer(String OutgoingMailServerID) {
    return (OutgoingMailServer) manager.getItem(OutgoingMailServerID);
  }

  /**
   * This returns the NetworkOutgoingMailServer with the given 
   * OutgoingMailServerName if it 
   * exists; otherwise, returns null.
   */
  public OutgoingMailServer getDefaultOutgoingMailServer() {
    if (defaultServer == null) {
      String defaultId = Pooka.getProperty("OutgoingServer._default", "");
      if (defaultId != "") {
	defaultServer = new DefaultOutgoingMailServer(defaultId);
      }
    }

    return defaultServer;
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
   * This notifies all listeners that the OutgoingMailServerList has changed.
   */
  public void fireItemListChanged(ItemListChangeEvent e) {
    for (int i = 0; i < listenerList.size(); i++)
      ((ItemListChangeListener)listenerList.get(i)).itemListChanged(e);
  }
  

  /**
   * This creates a new OutgoingMailServer.
   */
  public Item createItem(VariableBundle sourceBundle, String resourceString, String itemID) {
    return new OutgoingMailServer(itemID);
  }

  //---------------------------
  // the background stuff.
  
  /**
   * This loads and creates all the OutgoingMailServers using the "OutgoingServer" 
   * property of the main Pooka VariableBundle.
   */
  private void createOutgoingMailServerList() {
    manager = new ItemManager("OutgoingServer", Pooka.getResources(), this);

    manager.addItemListChangeListener(this);
  }

  /**
   * Tells all of the FolderInfos that are outboxes that they are, in fact,
   * outboxes.
   */
  public void loadOutboxFolders() {
    Vector v = getOutgoingMailServerList();

    for (int i = 0; i < v.size(); i++) {
      OutgoingMailServer oms = (OutgoingMailServer) v.get(i);
      oms.loadOutboxFolder();
    }
  }

  /**
   * Stops all mail server threads.
   */
  public void stopServers() {
    Vector v = getOutgoingMailServerList();
    VariableBundle resources = Pooka.getResources();
    for (int i = 0; i < v.size(); i++) {
      OutgoingMailServer oms = (OutgoingMailServer) v.get(i);
      oms.stopThread();
      resources.removeValueChangeListener(oms);
    }
    resources.removeValueChangeListener(manager);
  }

  /**
   * A special OutgoingMailServer that represents the 'default' value.
   */
  class DefaultOutgoingMailServer extends OutgoingMailServer {
    String defaultServerId = null;
    OutgoingMailServer underlyingServer;
    String outboxID = null;

    /**
     * <p>Creates a new DefaultOutgoingMailServer using the given 
     * String as the default server id.
     */
    public DefaultOutgoingMailServer (String newDefaultServerId) {
      super(newDefaultServerId);
    }
    
    /**
     * <p>Configures this mail server.</p>
     */
    protected void configure() {
      defaultServerId = id;
      id = Pooka.getProperty("OutgoingServer._default.label", "*default*");
      propertyName = "OutgoingServer._default";

      // changing because we get a NPE here for some reason...
      //underlyingServer = getOutgoingMailServer(defaultServerId);

      underlyingServer = Pooka.getOutgoingMailManager().getOutgoingMailServer(defaultServerId);
      outboxID = Pooka.getProperty("OutgoingServer." + defaultServerId + ".outbox", "");

      mailServerThread = new net.suberic.util.thread.ActionThread("default - smtp thread");
      mailServerThread.start();
    }
    
    /**
     * Sends all available messages.
     *
     * What this will do actually is send all of the default server's
     * messages, and then send the underlying server's messages, if any.
     */
    protected void internal_sendAll() throws javax.mail.MessagingException {
      
      NetworkConnection connection = getConnection();
      
      if (connection.getStatus() == NetworkConnection.DISCONNECTED) {
	connection.connect();
      }
      
      if (connection.getStatus() != NetworkConnection.CONNECTED) {
	throw new MessagingException(Pooka.getProperty("error.connectionDown", "Connection down for Mail Server:  ") + getItemID());
      }
      
      Transport sendTransport = Pooka.getDefaultSession().getTransport(underlyingServer.getSendMailURL()); 
      try {
	sendTransport.connect();
	
	FolderInfo outbox = getOutbox();
	
	Message[] msgs; 

	if (outbox != null) {
	  msgs = outbox.getFolder().getMessages();    
	  
	  try {
	    for (int i = 0; i < msgs.length; i++) {
	      Message m = msgs[i];
	      if (! m.isSet(Flags.Flag.DRAFT)) {
		sendTransport.sendMessage(m, m.getAllRecipients());
		m.setFlag(Flags.Flag.DELETED, true);
	      }
	    }
	  } finally {
	    outbox.getFolder().expunge();
	  }
	}

	FolderInfo underlyingOutbox = underlyingServer.getOutbox();
	
	if (underlyingOutbox != null && underlyingOutbox != outbox) {
	  msgs = underlyingOutbox.getFolder().getMessages();    
	  
	  try {
	    for (int i = 0; i < msgs.length; i++) {
	      Message m = msgs[i];
	      if (! m.isSet(Flags.Flag.DRAFT)) {
		sendTransport.sendMessage(m, m.getAllRecipients());
		m.setFlag(Flags.Flag.DELETED, true);
	      }
	    }
	  } finally {
	    underlyingOutbox.getFolder().expunge();
	  }
	}
      } finally {
	sendTransport.close();
      }
    }
    
    /**
     * <p>The NetworkConnection that this OutgoingMailServer depends on.
     */
    public NetworkConnection getConnection() {
      return underlyingServer.getConnection();
    }
    
    /**
     * <p>The FolderInfo where messages for this MailServer are
     * stored until they're ready to be sent.
     */
    public FolderInfo getOutbox() {
      if (outboxID != null && outboxID != "") {
	return Pooka.getStoreManager().getFolder(outboxID);
      } else {
	return null;
      }
    }
    
    /**
     * Returns the sendMailURL.
     */
    public URLName getSendMailURL() {
      return underlyingServer.getSendMailURL();
    }
  }

}
