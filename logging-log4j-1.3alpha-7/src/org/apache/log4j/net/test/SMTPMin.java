/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.net.test;

import org.apache.log4j.*;

public class SMTPMin {

  final static Logger logger = Logger.getLogger(SMTPMin.class);
  
  public
  static
  void main(String argv[]) {
    if(argv.length == 1)
      init(argv[0]);
    else
      usage("Wrong number of arguments.");

    NDC.push("some context");
    test();
  }

  static
  void usage(String msg) {
    System.err.println(msg);
    System.err.println("Usage: java " + SMTPMin.class.getName()
		       + " configFile");
    System.exit(1);
  }

  static
  void init(String configFile) {
    PropertyConfigurator.configure(configFile);
  }


  static
  void test() {
    int i  = 0;
    logger.debug( "Message " + i++);
    logger.debug("Message " + i++,  new Exception("Just testing."));
    logger.info( "Message " + i++);
    logger.warn( "Message " + i++);
    logger.error( "Message " + i++);
    logger.log(Level.FATAL, "Message " + i++);
    LogManager.shutdown();
    Thread.currentThread().getThreadGroup().list();
  }

}
