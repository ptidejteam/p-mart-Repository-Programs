/*
 * Created on Jul 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.taursys.util;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * @author marty
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EmptyMessageFormat extends MessageFormat {

  /**
   * @param pattern
   */
  public EmptyMessageFormat() {
    super("");
  }

  /**
   * @param pattern
   */
  public EmptyMessageFormat(String pattern) {
    super(pattern);
  }

  /**
   * @param pattern
   * @param locale
   */
  public EmptyMessageFormat(String pattern, Locale locale) {
    super(pattern, locale);
  }

}
