
package com.systemsunion.LoggingServer;

import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Priority;
import org.apache.log4j.NDC;

// Contributors:  Moses Hohman <mmhohman@rainbow.uchicago.edu>

/**
   Read {@link LoggingEvent} objects sent from a remote client using
   Sockets (TCP). These logging events are logged according to local
   policy, as if they were generated locally.

   <p>For example, the socket node might decide to log events to a
   local file and also resent them to a second socket node.

	@author  Ceki G&uuml;lc&uuml;

	@since 0.8.4
*/
public class SocketNode2 implements Runnable {

  Socket socket;
  ObjectInputStream ois;

  static Category cat = Category.getInstance(SocketNode2.class.getName());

  public
  SocketNode2(Socket socket) {
	this.socket = socket;
	try {
	  ois = new ObjectInputStream(socket.getInputStream());
	}
	catch(Exception e) {
	  cat.error("Could not open ObjectInputStream to "+socket, e);
	}
  }

  //public
  //void finalize() {
  //System.err.println("-------------------------Finalize called");
  // System.err.flush();
  //}

  public void run() {
	LoggingEvent event;
	Category remoteCategory;
	String strClientName;

	// Get the client name.
	InetAddress addr = socket.getInetAddress();
	strClientName = addr.getHostName();
	if(strClientName == null || strClientName.length() == 0)
	{
		strClientName = addr.getHostAddress();
	}

	try {
	  while(true) {
	event = (LoggingEvent) ois.readObject();

	if(event.ndc != null)
	{
		event.ndc = strClientName + ":" + event.ndc;
	}
	else
	{
		event.ndc = strClientName;
	}

	remoteCategory = Category.getInstance(event.categoryName);
	remoteCategory.callAppenders(event);
	  }
	}
	catch(java.io.EOFException e) {
	  cat.info("Caught java.io.EOFException will close conneciton.", e);
	}
	catch(java.net.SocketException e) {
	  cat.info("Caught java.net.SocketException, will close conneciton.", e);
	}
	catch(Exception e) {
	  cat.error("Unexpected exception. Closing conneciton.", e);
	}

	try {
	  ois.close();
	}
	catch(Exception e) {
	  cat.info("Could not close connection.", e);
	}
  }
}
