/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

package org.apache.log4j;

import org.apache.log4j.spi.LoggingEvent;

/**
   SimpleLayout consists of the level of the log statement,
   followed by " - " and then the log message itself. For example,

   <pre>
           DEBUG - Hello world
   </pre>

   <p>
   @author Ceki G&uuml;lc&uuml;
   @since version 0.7.0

   <p>{@link PatternLayout} offers a much more powerful alternative.
*/
public class SimpleLayout extends Layout {

  StringBuffer sbuf = new StringBuffer(128);

  public SimpleLayout() {
  }

  public
  void activateOptions() {
  }
  
  /**
     Returns the log statement in a format consisting of the
     <code>level</code>, followed by " - " and then the
     <code>message</code>. For example, <pre> INFO - "A message"
     </pre>

     <p>The <code>category</code> parameter is ignored.
     <p>
     @return A byte array in SimpleLayout format.
    */
  public
  String format(LoggingEvent event) {

    sbuf.setLength(0);
    sbuf.append(event.getLevel().toString());
    sbuf.append(" - ");
    sbuf.append(event.getRenderedMessage());
    sbuf.append(LINE_SEP);
    return sbuf.toString();
  }

/**
     The SimpleLayout does not handle the throwable contained within
     {@link LoggingEvent LoggingEvents}. Thus, it returns
     <code>true</code>.

     @since version 0.8.4 */
  public
  boolean ignoresThrowable() {
    return true;
  }
}
