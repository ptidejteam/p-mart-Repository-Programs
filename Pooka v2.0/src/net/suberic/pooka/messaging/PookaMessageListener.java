package net.suberic.pooka.messaging;

import java.net.*;
import java.nio.channels.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import javax.mail.*;
import javax.mail.internet.MimeMessage;

import net.suberic.pooka.*;
import net.suberic.pooka.gui.NewMessageProxy;
import net.suberic.pooka.gui.NewMessageFrame;
import net.suberic.pooka.gui.MessageUI;

/** 
 * This class listens on a socket for messages from other Pooka clients.
 */
public class PookaMessageListener extends Thread {
  
  ServerSocket mSocket = null;
  boolean mStopped = false;
  java.util.List mActiveHandlers = new java.util.LinkedList();
  
  /**
   * Creates a new PookaMessageListener.
   */
  public PookaMessageListener() {
    super("PookaMessageListener Thread");
    getLogger().log(Level.FINE, "creating new PookaMessageListener.");
    start();
  }

  /**
   * Opens the socket and listens to it.
   */
  public void run() {
    try {
      getLogger().log(Level.FINE, "creating socket.");
      createSocket();
      getLogger().log(Level.FINE, "socket created.");
      while (! mStopped) {
	getLogger().log(Level.FINE, "accepting connection.");
	Socket currentSocket = mSocket.accept();
	if (! mStopped) {
	  getLogger().log(Level.FINE, "got connection.");
	  PookaMessageHandler handler = new PookaMessageHandler(this, currentSocket);
	  mActiveHandlers.add(handler);
	  handler.start();
	}
      }
    } catch (Exception e) {
      System.out.println("error in MessagingListener -- exiting.");
      e.printStackTrace();
    }
  }


  /**
   * Creats the socket to listen to.
   */
  public void createSocket() throws Exception {
    getLogger().log(Level.FINE, "creating new PookaMessageListener socket.");
    Exception throwMe = null;
    boolean success = false;
    for (int port = PookaMessagingConstants.S_PORT; (! success) && port <  PookaMessagingConstants.S_PORT + 25; port++) {
      try {
	mSocket = new ServerSocket(port);
	success = true;
	String propertiesPort = Pooka.getProperty("Pooka.messaging.port", Integer.toString(port));
	Pooka.setProperty("Pooka.messaging.port", Integer.toString(port));
	if (! Integer.toString(port).equals(propertiesPort)) {
	  Pooka.getResources().saveProperties();
	}
      } catch (Exception e) {
	throwMe = e;
      }
    }

    if (! success)
      throw throwMe;
  }

  /**
   * Stops this MessageListener.
   */
  public void stopMessageListener() {
    mStopped = true;

    try {
      closeServerSocket();
    } catch (Exception e) {
      // ignore--we're stopping.
    }

    Iterator iter = mActiveHandlers.iterator();
    while(iter.hasNext()) {
      ((PookaMessageHandler) iter.next()).stopHandler();
    }
  }

  /**
   * Closes the server socket.
   */
  void closeServerSocket() throws java.io.IOException {
    if (mSocket != null) {
      mSocket.close();
    }
  }

  /**
   * Removes the handler from the active list.
   */
  public void removeHandler(PookaMessageHandler pHandler) {
    mActiveHandlers.remove(pHandler);
  }

  /**
   * Gets the logger for this class.
   */
  public Logger getLogger() {
    return Logger.getLogger("Pooka.debug.messaging");
  }
}
