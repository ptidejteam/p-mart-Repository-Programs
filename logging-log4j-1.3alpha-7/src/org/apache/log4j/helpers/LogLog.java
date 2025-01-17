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

package org.apache.log4j.helpers;


/**This class used to output log statements from within the log4j package.

   <p>Log4j components cannot make log4j logging calls. However, it is
   sometimes useful for the user to learn about what log4j is
   doing. You can enable log4j internal logging by defining the
   <b>log4j.configDebug</b> variable.

   <p>All log4j internal debug calls go to <code>System.out</code>
   where as internal error messages are sent to
   <code>System.err</code>. All internal messages are prepended with
   the string "log4j: ".

   @since 0.8.2
   @author Ceki G&uuml;lc&uuml;
*/
public class LogLog {
  /**
     Defining this value makes log4j print log4j-internal debug
     statements to <code>System.out</code>.

    <p> The value of this string is <b>log4j.debug</b>.

    <p>Note that the search for all option names is case sensitive.  */
  public static final String CORE_DEBUG_KEY = "log4j.coreDebug";

  protected static boolean debugEnabled = false;

  private static final String PREFIX = "log4j: ";
  private static final String ERR_PREFIX = "log4j:ERROR ";
  private static final String INFO_PREFIX = "log4j:INFO ";
  private static final String WARN_PREFIX = "log4j:WARN ";

  static {
    String key = OptionConverter.getSystemProperty(CORE_DEBUG_KEY, null);

    if (key != null) {
      debugEnabled = OptionConverter.toBoolean(key, true);
    }
  }

  /**
     Allows to enable/disable log4j internal logging.
   */
  public static void setInternalDebugging(boolean enabled) {
    debugEnabled = enabled;
  }

  /**
     This method is used to output log4j internal debug
     statements. Output goes to <code>System.out</code>.
  */
  public static void debug(String msg) {
    if (debugEnabled) {
      System.out.println(PREFIX + msg);
    }
  }

  public static void info(String msg) {
    System.out.println(INFO_PREFIX + msg);
  }
  
  /**
     This method is used to output log4j internal debug
     statements. Output goes to <code>System.out</code>.
  */
  public static void debug(String msg, Throwable t) {
    if (debugEnabled) {
      System.out.println(PREFIX + msg);

      if (t != null) {
        t.printStackTrace(System.out);
      }
    }
  }

  /**
   * This method is used to output log4j internal error statements. There is no 
   * way to disable error statements. Output goes to <code>System.err</code>.
   * @deprecated Use {@link org.apache.log4j.Logger} instead.
  */
  public static void error(String msg) {
    System.err.println(ERR_PREFIX + msg);
  }

  /**
   * This method is used to output log4j internal error statements. There is no 
   * way to disable error statements. Output goes to <code>System.err</code>.
   * @deprecated Use {@link org.apache.log4j.Logger} instead.
  **/
  public static void error(String msg, Throwable t) {
    System.err.println(ERR_PREFIX + msg);

    if (t != null) {
      t.printStackTrace();
    }
  }

  /**
   * In quite mode no LogLog generates strictly no output, not even 
   * for errors.
   * @param quietMode A true for not
   * @deprecated with no replacement
  */
  public static void setQuietMode(boolean quietMode) {
    // nothing to do
  }

  /**
   * This method is used to output log4j internal warning statements. There is 
   * no way to disable warning statements. Output goes to 
   * <code>System.err</code>.  
   * 
   * @deprecated Use {@link org.apache.log4j.Logger} instead.
   * */
  public static void warn(String msg) {
    System.err.println(WARN_PREFIX + msg);
  }

  /**
   * This method is used to output log4j internal warnings. There is no way to 
   * disable warning statements.  Output goes to <code>System.err</code>. 
   * 
   * @deprecated Use {@link org.apache.log4j.Logger} instead.
   *  */
  public static void warn(String msg, Throwable t) {
    System.err.println(WARN_PREFIX + msg);

    if (t != null) {
      t.printStackTrace();
    }
  }
}
