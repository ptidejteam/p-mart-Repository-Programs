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

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Return the event's level in a StringBuffer.
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class LevelPatternConverter extends PatternConverter {
  // We assume that each PatternConveter instance is unique within a layout, 
  // which is unique within an appender. We further assume that callas to the 
  // appender method are serialized (per appender).
  StringBuffer buf;

  public LevelPatternConverter() {
    super();
    this.buf = new StringBuffer(5);
  }

  public StringBuffer convert(LoggingEvent event) {
    buf.setLength(0);
    buf.append(event.getLevel().toString());

    return buf;
  }
  
  public String getName() {
      return "Level";
  }
  
  public String getStyleClass(LoggingEvent e) {
    if(e == null) {
      return "level";
    } else {
      int lint = e.getLevel().toInt();
      switch(lint) {
      case Level.TRACE_INT: return "level trace";
      case Level.DEBUG_INT: return "level debug";
      case Level.INFO_INT: return "level info";
      case Level.WARN_INT: return "level warn";
      case Level.ERROR_INT: return "level error";
      case Level.FATAL_INT: return "level fatal";
      default: return "level "+e.getLevel().toString();
      }
    }
  }
  
}
