/* Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j.net;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Test;

import org.apache.log4j.*;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.apache.log4j.util.*;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.PropertyConfigurator;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.NDC;
import org.apache.log4j.xml.XLevel;
import org.apache.log4j.Priority;
import java.io.IOException;
import java.util.Enumeration;

/**
   @author  Ceki G&uuml;lc&uuml;
*/
public class SocketServerTestCase extends TestCase {
  
  static String TEMP = "output/temp";
  static String FILTERED = "output/filtered";

  // %5p %x [%t] %c %m%n
  // DEBUG T1 [main] org.apache.log4j.net.SocketAppenderTestCase Message 1
  static String PAT1 = "^(DEBUG| INFO| WARN|ERROR|FATAL|LETHAL) T1 \\[main]\\ "
                       + ".* Message \\d{1,2}";

  // DEBUG T2 [main] ? (?:?) Message 1
  static String PAT2 = "^(DEBUG| INFO| WARN|ERROR|FATAL|LETHAL) T2 \\[main]\\ "
                       + "\\? \\(\\?:\\?\\) Message \\d{1,2}";


  // DEBUG T3 [main] org.apache.log4j.net.SocketServerTestCase (SocketServerTestCase.java:121) Message 1
  static String PAT3 = "^(DEBUG| INFO| WARN|ERROR|FATAL|LETHAL) T3 \\[main]\\ "
                       + "org.apache.log4j.net.SocketServerTestCase "
                       + "\\(SocketServerTestCase.java:\\d{3}\\) Message \\d{1,2}";


  // DEBUG some T4 MDC-TEST4 [main] SocketAppenderTestCase - Message 1   
  // DEBUG some T4 MDC-TEST4 [main] SocketAppenderTestCase - Message 1 
  static String PAT4 = "^(DEBUG| INFO| WARN|ERROR|FATAL|LETHAL) some T4 MDC-TEST4 \\[main]\\"
                       + " (root|SocketServerTestCase) - Message \\d{1,2}";

  static String PAT5 = "^(DEBUG| INFO| WARN|ERROR|FATAL|LETHAL) some5 T5 MDC-TEST5 \\[main]\\"
                       + " (root|SocketServerTestCase) - Message \\d{1,2}";


  static String EXCEPTION1 = "java.lang.Exception: Just testing";
  static String EXCEPTION2 = "\\s*at .*\\(.*:\\d{1,4}\\)";
  static String EXCEPTION3 = "\\s*at .*\\(Native Method\\)";


  static Logger logger = Logger.getLogger(SocketServerTestCase.class);
  static public final int PORT = 12345;  
  static Logger rootLogger = Logger.getRootLogger();
  SocketAppender socketAppender;

  public SocketServerTestCase(String name) {
    super(name);
  }

  public void setUp() {
    System.out.println("Setting up test case.");
  }
  
  public void tearDown() {
    System.out.println("Tearing down test case.");
    socketAppender = null;
    rootLogger.removeAllAppenders();
  }

  /**
   * The pattern on the server side: %5p %x [%t] %c %m%n     
   *
   * We are testing NDC functionality across the wire.  
   */
  public void test1() throws Exception {
    socketAppender = new SocketAppender("localhost", PORT);
    rootLogger.addAppender(socketAppender);
    common("T1", "key1", "MDC-TEST1");
    delay(1);
    ControlFilter cf = new ControlFilter(new String[]{PAT1, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, new LineNumberFilter()});

    assertTrue(Compare.compare(FILTERED, "witness/socketServer.1"));
  }

  /**
   * The pattern on the server side: %5p %x [%t] %C (%F:%L) %m%n
   *
   * We are testing NDC across the wire. Localization is turned off by
   * default so it is not tested here even if the conversion pattern
   * uses localization. */
  public void test2() throws Exception {
    socketAppender = new SocketAppender("localhost", PORT);
    rootLogger.addAppender(socketAppender);

    common("T2", "key2", "MDC-TEST2");
    delay(1);
    ControlFilter cf = new ControlFilter(new String[]{PAT2, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, new LineNumberFilter()});

    assertTrue(Compare.compare(FILTERED, "witness/socketServer.2"));
  }

  /**
   *  The pattern on the server side: %5p %x [%t] %C (%F:%L) %m%n
   *  meaning that we are testing NDC and locatization functionality
   *  across the wire.  */
  public void test3() throws Exception {
    socketAppender = new SocketAppender("localhost", PORT);
    socketAppender.setLocationInfo(true);
    rootLogger.addAppender(socketAppender);

    common("T3", "key3", "MDC-TEST3");
    delay(1);
    ControlFilter cf = new ControlFilter(new String[]{PAT3, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, new LineNumberFilter()});

    assertTrue(Compare.compare(FILTERED, "witness/socketServer.3"));
  }

  /**
   *  The pattern on the server side: %5p %x %X{key1}%X{key4} [%t] %c{1} - %m%n 
   *  meaning that we are testing NDC, MDC and localization functionality across 
   *  the wire.  
  */
  public void test4() throws Exception {
    socketAppender = new SocketAppender("localhost", PORT);
    socketAppender.setLocationInfo(true);
    rootLogger.addAppender(socketAppender);

    NDC.push("some");
    common("T4", "key4", "MDC-TEST4");
    NDC.pop();
    delay(1);
    ControlFilter cf = new ControlFilter(new String[]{PAT4, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, new LineNumberFilter()});

    assertTrue(Compare.compare(FILTERED, "witness/socketServer.4"));
  }

  /**
   *  The pattern on the server side: %5p %x %X{key1}%X{key5} [%t] %c{1} - %m%n 
   *
   *  The test case uses wraps an AsyncAppender around the
   *  SocketAppender. This tests was written specifically for bug
   *  report #9155.  

   * Prior to the bug fix the output on the server did not contain the
   * MDC-TEST5 string because the MDC clone operation (in getMDCCopy
   * method) operation is performed twice, once from the main thread
   * which is correct, and a second time from the AsyncAppender's
   * dispatch thread which is incrorrect.

   */
  public void test5() throws Exception {
    socketAppender = new SocketAppender("localhost", PORT);
    socketAppender.setLocationInfo(true);
    AsyncAppender asyncAppender = new AsyncAppender();
    asyncAppender.setLocationInfo(true);
    asyncAppender.addAppender(socketAppender);
    rootLogger.addAppender(asyncAppender);

    NDC.push("some5");
    common("T5", "key5", "MDC-TEST5");
    NDC.pop();
    delay(2);
    ControlFilter cf = new ControlFilter(new String[]{PAT5, EXCEPTION1, 
						       EXCEPTION2, EXCEPTION3});
    
    Transformer.transform(TEMP, FILTERED, new Filter[] {cf, new LineNumberFilter()});

    assertTrue(Compare.compare(FILTERED, "witness/socketServer.5"));
  }


  static 
  void common(String dc, String key, Object o) {
    int i = -1; 
    NDC.push(dc); 
    MDC.put(key, o);
    Logger root = Logger.getRootLogger();

    logger.log(XLevel.TRACE, "Message " + ++i);
    logger.debug("Message " + ++i);
    root.debug("Message " + ++i);
    logger.info("Message " + ++i);
    logger.warn("Message " + ++i);
    logger.log(XLevel.LETHAL, "Message " + ++i); //5
    
    Exception e = new Exception("Just testing");
    logger.debug("Message " + ++i, e);
    root.error("Message " + ++i, e);
    NDC.pop();
    MDC.remove(key);
  }

  public void delay(int secs) {
    try {Thread.currentThread().sleep(secs*1000);} catch(Exception e) {}
  }


  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new SocketServerTestCase("test1"));
    suite.addTest(new SocketServerTestCase("test2"));
    suite.addTest(new SocketServerTestCase("test3"));
    suite.addTest(new SocketServerTestCase("test4"));
    suite.addTest(new SocketServerTestCase("test5"));
    return suite;
  }
}
