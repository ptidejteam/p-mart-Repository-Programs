/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.mapReduce;

import org.apache.nutch.io.*;
import org.apache.nutch.ipc.*;
import org.apache.nutch.util.*;

import org.mortbay.util.*;
import org.mortbay.http.*;
import org.mortbay.http.handler.*;
import org.mortbay.jetty.servlet.*;
import org.mortbay.jetty.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;

/*******************************************************
 * JobTrackerInfoServer provides stats about the JobTracker
 * via HTTP.  It's useful for clients that want to track
 * their jobs' progress.
 *
 * @author Mike Cafarella
 *******************************************************/
public class JobTrackerInfoServer {

    public static class RedirectHandler extends AbstractHttpHandler {
        public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
            response.sendRedirect("/jobtracker");
            request.setHandled(true);
        }
    }

    /////////////////////////////////////
    // The actual JobTrackerInfoServer
    /////////////////////////////////////
    static JobTracker jobTracker;
    org.mortbay.jetty.Server server;

    /**
     * We need the jobTracker to grab stats, and the port to 
     * know where to listen.
     */
    public JobTrackerInfoServer(JobTracker jobTracker, int port) throws IOException {
        this.jobTracker = jobTracker;
        this.server = new org.mortbay.jetty.Server();
        WebApplicationContext context = server.addWebApplication(null, "/", "src/webapps/jobtracker");

        SocketListener socketListener = new SocketListener();
        socketListener.setPort(port);
        this.server.addListener(socketListener);

        //
        // REMIND - mjc - I can't figure out how to get request redirect to work.
        // I've tried adding an additional default handler to the context, but
        // it doesn't seem to work.  The handler has its handle() function called
        // even when the JSP is processed correctly!  I just want to add a redirect
        // page, when the URL is incorrectly typed.
        //
        // context.addHandler(new LocalNotFoundHandler());
    }

    /**
     * The thread class we need to kick off the HTTP server async-style.
     */
    class HTTPStarter implements Runnable {
        public void run() {
            try {
                server.start();
            } catch (Exception me) {
                me.printStackTrace();
            }
        }
    }

    /**
     * Launch the HTTP server
     */
    public void start() throws IOException {
        new Thread(new HTTPStarter()).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }
        if (! server.isStarted()) {
            throw new IOException("Could not start HTTP server");
        }
    }

    /**
     * Stop the HTTP server
     */
    public void stop() {
        try {
            this.server.stop();
        } catch (InterruptedException ie) {
        }
    }
}
