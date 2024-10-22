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
import org.apache.log4j.spi.location.LocationInfo;

/**
 * Most of the work is done in the parent class {@link 
 * org.apache.log4j.pattern.NamedPatternConverter NamedPatternConverter}.
 * This class is only responsible of returning the full name name of the caller
 * class. 
 * 
 * @author Ceki G&uuml;lc&uuml;
 */
public class ClassNamePatternConverter extends NamedPatternConverter {
  static private final String NAME = "Class Name";
  static private final String STYLE_CLASS = NAME.toLowerCase();

  public ClassNamePatternConverter() {
    super();
  }
  
  String getFullyQualifiedName(LoggingEvent event) {
    LocationInfo li = event.getLocationInformation();
    if(li == null) {
      return LocationInfo.NA;
    } else {
	    return li.getClassName();
    }
	}
  
  public String getName() {
      return NAME;
  }
  
  public String getStyleClass(LoggingEvent e) {
    return STYLE_CLASS;
  }
}
