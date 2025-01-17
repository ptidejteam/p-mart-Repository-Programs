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

package org.apache.log4j.rolling.helper;

import java.text.SimpleDateFormat;

import java.util.Date;


/**
 *
 * @author Ceki G&uuml;c&uuml;
 *
 */
public class DateTokenConverter extends TokenConverter {
 
  final String datePattern;
  SimpleDateFormat sdf;

  public DateTokenConverter(String datePattern) {
    super(TokenConverter.DATE);
    if("".equals(datePattern)) {
      // In the absence of a valid option assume daily rollover
      this.datePattern = "yyyy-MM-dd";
    } else {
      this.datePattern = datePattern;
    }
    sdf = new SimpleDateFormat(datePattern);
  }

  public String convert(Date date) {
    return sdf.format(date);
  }
  
  /**
   * Return the date pattern.
   */
  public String getDatePattern() {
    return datePattern;
  }

  /**
   * Set the date pattern.
   */
  //public void setDatePattern(String string) {
  //  datePattern = string;
  //}

}
