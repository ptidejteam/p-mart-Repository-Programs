/* 
 * Copyright (c) 2001 by Matt Welsh and The Regents of the University of 
 * California. All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice and the following
 * two paragraphs appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 *
 * Author: Matt Welsh <mdw@cs.berkeley.edu>
 * 
 */

package org.gudy.azureus2.ui.web2.stages.http;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.gudy.azureus2.ui.web2.UI;
import org.gudy.azureus2.ui.web2.WebConst;
import org.gudy.azureus2.ui.web2.stages.httpserv.httpConnection;
import org.gudy.azureus2.ui.web2.stages.httpserv.httpResponder;

import seda.sandStorm.api.ConfigDataIF;
import seda.sandStorm.api.EventHandlerIF;
import seda.sandStorm.api.QueueElementIF;
import seda.sandStorm.api.SinkClosedEvent;
import seda.sandStorm.api.SinkClosedException;
import seda.sandStorm.api.SinkIF;

/**
 * This stage simply forwards httpResponders to the appropriate 
 * httpConnection. Note that when ENQUEUE_RESPONSES is false (the
 * default), then this stage is not actually used -- rather, the
 * sendResponse() call directly implements forwarding.
 */
public class HttpSend implements EventHandlerIF, WebConst {

  private static final Logger logger = Logger.getLogger("azureus2.ui.web.stages.http.HttpSend");

  // If true, handle sends from this stage - otherwise inline into
  // sendResponse() call
  private static final boolean ENQUEUE_RESPONSES = true;

  private static SinkIF mysink, cacheSink;
  private static Hashtable respTable;
  private static int maxReqs;

  public HttpSend() {
    if (UI.httpSend != null) {
      throw new Error("HttpSend: More than one HttpSend running?");
    }

    UI.httpSend = this;
  }

  public void init(ConfigDataIF config) throws Exception {
    mysink = config.getStage().getSink();
    cacheSink = config.getManager().getStage(CACHE_STAGE).getSink();
    maxReqs = config.getInt("maxRequests");
    if (maxReqs != -1) respTable = new Hashtable();
  }

  public void destroy() {
  }

  // Library call used to send HTTP response
  public static void sendResponse(httpResponder resp) {
    if (ENQUEUE_RESPONSES) {
      mysink.enqueue_lossy(resp);
    } else {
      // Inline the call
      doResponse(resp);
    }
  }

  private static final void doResponse(httpResponder resp) {
    UI.httpRecv.doneWithReq();

    if (logger.isDebugEnabled()) logger.debug("HttpSend: Got response "+resp);
    if (!resp.getConnection().enqueue_lossy(resp)) {
      // This is OK if we have already closed the connection
      if (logger.isDebugEnabled()) logger.debug("HttpSend: Could not enqueue response "+resp.getResponse()+" to connection "+resp.getConnection());
      return;
    }

    if (resp.shouldClose()) {
      UI.numConnectionsClosed++;
      httpConnection conn = resp.getConnection();
      try {
	conn.close(cacheSink);
	if (maxReqs != -1) respTable.remove(conn);
      } catch (SinkClosedException sce) {
	if (logger.isDebugEnabled()) logger.debug("Warning: Tried to close connection "+conn+" multiple times");
      }
      return;
    }

    if (maxReqs != -1) {
      httpConnection conn = resp.getConnection();
      Integer count = (Integer)respTable.remove(conn);
      if (count == null) {
	respTable.put(conn, new Integer(0));
      } else {
      	int prevConns = UI.numConnectionsEstablished - UI.numConnectionsClosed;
	int c = count.intValue();
	c++;
      	if (c >= maxReqs) {
	  UI.httpRecv.closeConnection(conn);
	} else {
	  respTable.put(conn, new Integer(c));
	}
      }
    }
  }

  public void handleEvent(QueueElementIF item) {
    if (logger.isDebugEnabled()) logger.debug("HttpSend: GOT QEL: "+item);

    if (item instanceof httpResponder) {
      doResponse((httpResponder)item);

    } else if (item instanceof SinkClosedEvent) {
      // Connection closed by remote peer
      SinkClosedEvent sce = (SinkClosedEvent)item;
      httpConnection hc = (httpConnection)sce.sink;
      if (maxReqs != -1) respTable.remove(hc);

    } else {
      logger.info("Got unknown event type: "+item);
    }

  }

  public void handleEvents(QueueElementIF items[]) {
    for(int i=0; i<items.length; i++) {
      handleEvent(items[i]);
    }
  }

  /** 
   * Inner class representing the number of requests on a given
   * connection.
   */
  class requestCount {
    int reqs;
  }

}

