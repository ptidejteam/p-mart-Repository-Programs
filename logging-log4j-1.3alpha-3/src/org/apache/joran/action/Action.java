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

package org.apache.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.joran.Interpreter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;


/**
 *
 * Most of the work for configuring log4j is done by Actions.
 *
 * Methods of an Action are invoked while an XML file is parsed through.
 *
 * This class is largely copied from the relevant class in the commons-digester
 * project of the Apache Software Foundation.
 *
 * @author Craig McClanahan
 * @author Christopher Lenz
 * @author Ceki G&uuml;lc&uuml;
 *
 */
public abstract class Action {
  public static final String NAME_ATTRIBUTE = "name";
  public static final String VALUE_ATTRIBUTE = "value";
  public static final String FILE_ATTRIBUTE = "file";
  public static final String CLASS_ATTRIBUTE = "class";
  public static final String PATTERN_ATTRIBUTE = "pattern";
  public static final String ACTION_CLASS_ATTRIBUTE = "actionClass";

  /*
   * An inststance specific logger. 
   */
  private Logger logger;
  
  /**
   * Called when the parser first encounters an element.
   *
   * The return value indicates whether child elements should be processed. If
   * the returned value is 'false', then child elements are ignored.
   */
  public abstract void begin(
    ExecutionContext ec, String name, Attributes attributes);

  public abstract void end(ExecutionContext ec, String name);

  public String toString() {
    return this.getClass().getName();
  }

  protected int getColumnNumber(ExecutionContext ec) {
    Interpreter jp = ec.getJoranInterpreter();
    Locator locator = jp.getLocator();
    if (locator != null) {
      return locator.getColumnNumber();
    }
    return -1;
  }

  protected int getLineNumber(ExecutionContext ec) {
    Interpreter jp = ec.getJoranInterpreter();
    Locator locator = jp.getLocator();
    if (locator != null) {
      return locator.getLineNumber();
    }
    return -1;
  }
  
  /**
   * Return an instance specifi logger to be used by the Action itself.
   * This logger is not intended to be used by Mrs. Piggy, our proverbial user,
   * hence the protected keyword.
   * 
   * @return instance specific logger
   */
  protected Logger getLogger() {
    if(logger == null) {
      logger = LogManager.getLogger(this.getClass().getName());
    }
    return logger;
  }
}
