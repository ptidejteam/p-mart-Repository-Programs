

/*
 * Created on Dec 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Ceki
 *
 */

  public class StackPatternConverter extends PatternConverter {
    
    // We assume that each PatternConveter instance is unique within a layout, 
    // which is unique within an appender. We further assume that calls to the 
    // appender method are serialized (per appender).
    StringBuffer buf;

    public StackPatternConverter() {
      super();
      this.buf = new StringBuffer(9);
    }

    public StringBuffer convert(LoggingEvent event) {
      buf.setLength(0);
      buf.append(Long.toString(event.getSequenceNumber()));
      return buf;
    }
    
    public String getName() {
        return "Sequence Number";
    }
    
    public String getStyleClass(LoggingEvent e) {
      return "sn";
    }
    
  }

