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
package org.apache.log4j.joran.action;

import org.apache.joran.ExecutionContext;
import org.apache.joran.action.Action;
import org.apache.joran.helper.Option;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorItem;
import org.apache.log4j.spi.OptionHandler;

import org.xml.sax.Attributes;

import java.util.HashMap;


public class AppenderAction extends Action {
  static final Logger logger = Logger.getLogger(AppenderAction.class);
  Appender appender;

  private boolean inError = false;
  
  /**
   * Instantiates an appender of the given class and sets its name.
   *
   * The appender thus generated is placed in the ExecutionContext appender bag.
   */
  public void begin(
    ExecutionContext ec, String localName, Attributes attributes) {
    String className = attributes.getValue(CLASS_ATTRIBUTE);

    try {
      logger.debug(
        "About to instantiate appender of type [" + className + "]");

      Object instance =
        OptionConverter.instantiateByClassName(
          className, org.apache.log4j.Appender.class, null);
      appender = (Appender) instance;

      String appenderName = attributes.getValue(NAME_ATTRIBUTE);

      if (Option.isEmpty(appenderName)) {
        logger.warn(
          "No appender name given for appender of type " + className + "].");
      } else {
        appender.setName(appenderName);
        logger.debug("Appender named as [" + appenderName + "]");
      }

      // The execution context contains a bag which contains the appenders
      // created thus far.
      HashMap appenderBag =
        (HashMap) ec.getObjectMap().get(ActionConst.APPENDER_BAG);
      // add the appender just created to the appender bag.
      appenderBag.put(appenderName, appender);

      logger.debug("Pushing appender on to the object stack.");
      ec.pushObject(appender);
    } catch (Exception oops) {
      inError = true;
      logger.error(
        "Could not create an Appender. Reported error follows.", oops);
      ec.addError(
        new ErrorItem(
          "Could not create appender of type " + className + "]."));
    }
  }

  /**
   * Once the children elements are also parsed, now is the time to activate
   * the appender options.
   */
  public void end(ExecutionContext ec, String name) {
    if (inError) {
      return;
    }

    if (appender instanceof OptionHandler) {
      ((OptionHandler) appender).activateOptions();
    }

    Object o = ec.peekObject();

    if (o != appender) {
      logger.warn(
        "The object at the of the stack is not the appender named ["
        + appender.getName() + "] pushed earlier.");
    } else {
      logger.debug(
        "Popping appender named [" + appender.getName()
        + "] from the object stack");
      ec.popObject();
    }
  }

  public void finish(ExecutionContext ec) {
  }
}
