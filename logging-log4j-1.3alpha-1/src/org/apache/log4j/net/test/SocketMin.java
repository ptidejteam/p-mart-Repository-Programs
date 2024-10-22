/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net.test;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.net.SocketAppender;
import org.apache.log4j.NDC;
import java.io.InputStreamReader;

public class SocketMin {

  static Logger logger = Logger.getLogger(SyslogMin.class);
  static SocketAppender s;

  public
  static
  void main(String argv[]) {
    if(argv.length == 3)
      init(argv[0], argv[1]);
    else
      usage("Wrong number of arguments.");

    NDC.push("some context");
    if(argv[2].equals("true"))
      loop();
    else
      test();

    s.close();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + SocketMin.class
		       + " host port true|false");
    System.exit(1);
  }

  static
  void init(String host, String portStr) {
    Logger root = Logger.getRootLogger();
    BasicConfigurator.configure();
    try {
      int port   = Integer.parseInt(portStr);
      logger.info("Creating socket appender ("+host+","+port+").");
      s = new SocketAppender(host, port);
      s.setName("S");
      root.addAppender(s);
    }
    catch(java.lang.NumberFormatException e) {
      e.printStackTrace();
      usage("Could not interpret port number ["+ portStr +"].");
    }
    catch(Exception e) {
      System.err.println("Could not start!");
      e.printStackTrace();
      System.exit(1);
    }
  }

  static
  void loop() {
    Logger root = Logger.getRootLogger();
    InputStreamReader in = new InputStreamReader(System.in);
    System.out.println("Type 'q' to quit");
    int i;
    int k = 0;
    while (true) {
      logger.debug("Message " + k++);
      logger.info("Message " + k++);
      logger.warn("Message " + k++);
      logger.error("Message " + k++, new Exception("Just testing"));
      try {i = in.read(); }
      catch(Exception e) { return; }
      if(i == -1) break;
      if(i == 'q') break;
      if(i == 'r') {
	System.out.println("Removing appender S");
	root.removeAppender("S");
      }
    }
  }

  static
  void test() {
    int i  = 0;
    logger.debug( "Message " + i++);
    logger.info( "Message " + i++);
    logger.warn( "Message " + i++);
    logger.error( "Message " + i++);
    logger.log(Level.FATAL, "Message " + i++);
    logger.debug("Message " + i++,  new Exception("Just testing."));
  }
}
