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
package org.apache.log4j.pattern;

import org.apache.log4j.spi.LoggingEvent;

/*
 * Return the sequence number for the event.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class SequenceNumberPatternConverter extends PatternConverter {
	
	// We assume that each PatternConveter instance is unique within a layout, 
	// which is unique within an appender. We further assume that calls to the 
	// appender method are serialized (per appender).
  StringBuffer buf;

  public SequenceNumberPatternConverter() {
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
