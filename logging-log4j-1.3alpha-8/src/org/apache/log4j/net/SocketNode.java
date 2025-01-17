/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.plugins.Pauseable;
import org.apache.log4j.plugins.Receiver;
import org.apache.log4j.spi.ComponentBase;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;


// Contributors:  Moses Hohman <mmhohman@rainbow.uchicago.edu>

/**
   Read {@link LoggingEvent} objects sent from a remote client using
   Sockets (TCP). These logging events are logged according to local
   policy, as if they were generated locally.

   <p>For example, the socket node might decide to log events to a
   local file and also resent them to a second socket node.

    @author  Ceki G&uuml;lc&uuml;
    @author  Paul Smith <psmith@apache.org>

    @since 0.8.4
*/
public class SocketNode extends ComponentBase implements Runnable, Pauseable {

  private boolean paused;
  private Socket socket;
  private Receiver receiver;
  private SocketNodeEventListener listener;
  private List listenerList = Collections.synchronizedList(new ArrayList());

  /**
    Constructor for socket and logger repository. */
  public SocketNode(Socket socket, LoggerRepository hierarchy) {
    this.socket = socket;
    this.repository = hierarchy;
  }

  /**
    Constructor for socket and reciever. */
  public SocketNode(Socket socket, Receiver receiver) {
    this.socket = socket;
    this.receiver = receiver;
  }

  /**
   * Set the event listener on this node.
   *
   * @deprecated Now supports mutliple listeners, this method
   * simply invokes the removeSocketNodeEventListener() to remove
   * the listener, and then readds it.
   */
  public void setListener(SocketNodeEventListener _listener) {
    removeSocketNodeEventListener(_listener);
    addSocketNodeEventListener(_listener);
  }

  /**
   * Adds the listener to the list of listeners to be notified of the
   * respective event
   * @param listener the listener to add to the list
   */
  public void addSocketNodeEventListener(SocketNodeEventListener listener) {
    listenerList.add(listener);
  }

  /**
   * Removes the registered Listener from this instances list of
   * listeners.  If the listener has not been registered, then invoking
   * this method has no effect.
   *
   * @param listener the SocketNodeEventListener to remove
   */
  public void removeSocketNodeEventListener(SocketNodeEventListener listener) {
    listenerList.remove(listener);
  }

  public void run() {
    LoggingEvent event;
    Logger remoteLogger;
    Exception listenerException = null;
    ObjectInputStream ois = null;

    try {
      ois =
        new ObjectInputStream(
          new BufferedInputStream(socket.getInputStream()));
    } catch (Exception e) {
      ois = null;
      listenerException = e;
      getLogger().error("Exception opening ObjectInputStream to " + socket, e);
    }

    if (ois != null) {
      String remoteInfo =
        socket.getInetAddress().getHostName() + ":" + socket.getPort();

      /**
       * notify the listener that the socket has been
       * opened and this SocketNode is ready and waiting
       */
      fireSocketOpened(remoteInfo);

      try {
        while (true) {
          // read an event from the wire
          event = (LoggingEvent) ois.readObject();

          // store the known remote info in an event property
          event.setProperty("log4j.remoteSourceInfo", remoteInfo);

          // if configured with a receiver, tell it to post the event
          if (!isPaused()) {
            if ((receiver != null)) {
              receiver.doPost(event);

              // else post it via the hierarchy
            } else {
              // get a logger from the hierarchy. The name of the logger
              // is taken to be the name contained in the event.
              remoteLogger = repository.getLogger(event.getLoggerName());

              //event.logger = remoteLogger;
              // apply the logger-level filter
              if (event
                .getLevel()
                .isGreaterOrEqual(remoteLogger.getEffectiveLevel())) {
                // finally log the event as if was generated locally
                remoteLogger.callAppenders(event);
              }
            }
          } else {
            //we simply discard this event.
          }
        }
      } catch (java.io.EOFException e) {
        getLogger().info("Caught java.io.EOFException closing connection.");
        listenerException = e;
      } catch (java.net.SocketException e) {
        getLogger().info("Caught java.net.SocketException closing connection.");
        listenerException = e;
      } catch (IOException e) {
        getLogger().info("Caught java.io.IOException: " + e);
        getLogger().info("Closing connection.");
        listenerException = e;
      } catch (Exception e) {
        getLogger().error("Unexpected exception. Closing connection.", e);
        listenerException = e;
      }
    }

    // close the socket
    try {
      if (ois != null) {
        ois.close();
      }
    } catch (Exception e) {
      //getLogger().info("Could not close connection.", e);
    }

    // send event to listener, if configured
    if (listener != null || listenerList.size()>0) {
      fireSocketClosedEvent(listenerException);
    }
  }

  /**
   * Notifies all registered listeners regarding the closing of the Socket
   * @param listenerException
   */
  private void fireSocketClosedEvent(Exception listenerException) {
  	synchronized(listenerList){
  		for (Iterator iter = listenerList.iterator(); iter.hasNext();) {
  			SocketNodeEventListener snel = (SocketNodeEventListener) iter.next();
  			if (snel != null) {
  				snel.socketClosedEvent(listenerException);
  			}
  		}
  	}
  }

  /**
   * Notifies all registered listeners regarding the opening of a Socket
   * @param remoteInfo
   */
  private void fireSocketOpened(String remoteInfo) {
  	synchronized(listenerList){
  		for (Iterator iter = listenerList.iterator(); iter.hasNext();) {
  			SocketNodeEventListener snel = (SocketNodeEventListener) iter.next();
  			if (snel != null) {
  				snel.socketOpened(remoteInfo);
  			}
  		}
  	}
  }

  public void setPaused(boolean paused) {
    this.paused = paused;
  }

  public boolean isPaused() {
    return this.paused;
  }
}
