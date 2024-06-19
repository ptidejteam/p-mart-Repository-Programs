package net.suberic.pooka.messaging;

import java.net.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.logging.*;
import java.nio.channels.SocketChannel;

import net.suberic.pooka.Pooka;

/**
 * This class sends messages to a Pooka network client.
 */
public class PookaMessageSender {
  
  /** the channel that's connected to a PookaMessageListener. */
  SocketChannel mChannel = null;

  /** whether or not we have a connection open. */
  boolean mConnected = false;

  /**
   * This opens a connection to the server port of the running Pooka
   * instance.
   */
  public void openConnection() throws java.net.UnknownHostException,
				      java.io.IOException, 
				      SecurityException {
    int port; 
    try {
      port = Integer.parseInt(Pooka.getProperty("Pooka.messaging.port", ""));
    } catch (Exception e) {
      port = PookaMessagingConstants.S_PORT;
    }
    getLogger().log(Level.FINE, "opening port " + port);
    SocketAddress address = new InetSocketAddress("localhost",port);
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(false);
    if (! channel.connect(address)) {
      // we're willing to wait for about a second.
      for (int i = 0; (! channel.finishConnect()) && i < 4; i++) {
	try {
	  getLogger().log(Level.FINE, "not connected; sleeping (" + i + ").");
	  Thread.currentThread().sleep(250);
	} catch (Exception e) {
	}
      }
    }
    if (channel.isConnected()) {
      getLogger().log(Level.FINE, "got connection.");
      mChannel = channel;
      mChannel.configureBlocking(true);

      mConnected = true;
    } else {
      getLogger().log(Level.FINE, "failed to get connection.");
      throw new SocketTimeoutException("Unable to connect to server localhost at port " + port);
    }
  }

  /**
   * Sends a new message message to the server.
   */
  public void openNewEmail(String pAddress, String pUserProfile) throws java.io.IOException {
    StringBuffer sendBuffer = new StringBuffer();
    sendBuffer.append(PookaMessagingConstants.S_NEW_MESSAGE);
    if (pAddress != null && pAddress.length() > 0) {
      sendBuffer.append(" ");
      sendBuffer.append(pAddress);
      if (pUserProfile != null && pUserProfile.length() > 0) {
	sendBuffer.append(" ");
	sendBuffer.append(pUserProfile);
      }
    }
    
    getLogger().log(Level.FINE, "sending message " + sendBuffer.toString());
    sendMessage(sendBuffer.toString());
  }

  /**
   * Checks the version running on the server with this client to make
   * sure they're both on the same page.
   */
  public boolean checkVersion() throws java.io.IOException {
    sendMessage(PookaMessagingConstants.S_CHECK_VERSION);

    String response = retrieveResponse();
    getLogger().log(Level.FINE, "got response " + response);
    
    return (response != null && response.equals(Pooka.getPookaManager().getLocalrc()));
  }

  /**
   * Starts an instance of Pooka.
   */
  public void sendStartPookaMessage() throws java.io.IOException {
    getLogger().log(Level.FINE, "sending message " + PookaMessagingConstants.S_START_POOKA);
    sendMessage(PookaMessagingConstants.S_START_POOKA);
  }

  /**
   * Closes the connection.
   */
  public void closeConnection() {
    if (mConnected || mChannel != null) {
      try {
	getLogger().log(Level.FINE, "sending message " + PookaMessagingConstants.S_BYE);
	sendMessage(PookaMessagingConstants.S_BYE);
	mChannel.close();
      } catch (java.io.IOException ioe) {
	// ignore -- we're closing anyway.
      } finally {
	mChannel = null;
	mConnected = false;
      }
    }
  }
  
  /**
   * Sends a message.
   */
  public void sendMessage(String pMessage) throws java.io.IOException {
    BufferedWriter writer = new BufferedWriter(Channels.newWriter(mChannel, "UTF-8"));
    getLogger().log(Level.FINE, "sending message '" + pMessage);
    writer.write(pMessage);
    writer.newLine();
    writer.flush();
  }
      
  /**
   * Gets a response from the (already open) connection.
   */
  public String retrieveResponse() throws java.io.IOException {
    BufferedReader reader = new BufferedReader(Channels.newReader(mChannel,"UTF-8"));
    String returnValue = reader.readLine();
    getLogger().log(Level.FINE, "got response '" + returnValue + "'");
    return returnValue;
  }

  /**
   * Returns if this is connected or not.
   */
  public boolean isConnected() {
    return mConnected;
  }

  /**
   * Gets the logger for this class.
   */
  public Logger getLogger() {
    return Logger.getLogger("Pooka.debug.messaging");
  }

}
