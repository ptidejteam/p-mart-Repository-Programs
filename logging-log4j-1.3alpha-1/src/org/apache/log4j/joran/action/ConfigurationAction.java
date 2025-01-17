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

import java.util.List;

import org.apache.joran.ExecutionContext;

import org.apache.log4j.Logger;
import org.apache.log4j.config.ConfiguratorBase;
import org.apache.log4j.spi.LoggerRepository;
import org.xml.sax.Attributes;


public class ConfigurationAction extends ActionBase {
  static final Logger logger = Logger.getLogger(ConfigurationAction.class);
  static final String INTERNAL_DEBUG_ATTR = "debug";
  boolean attachment = false;

  public void begin(ExecutionContext ec, String name, Attributes attributes) {
    String debugAttrib = attributes.getValue(INTERNAL_DEBUG_ATTR);

    if (
      (debugAttrib == null) || debugAttrib.equals("")
        || debugAttrib.equals("null")) {
      getLogger().debug("Ignoring " + INTERNAL_DEBUG_ATTR + " attribute.");
    } else {
      LoggerRepository repository = (LoggerRepository) ec.getObject(0);
      ConfiguratorBase.attachTemporaryConsoleAppender(repository);
      getLogger().debug("Starting internal logs on console.");
      attachment = true;
    }
  }

  public void end(ExecutionContext ec, String name) {
    if (attachment) {
      getLogger().debug("Will stop writing internal logs on console.");
      LoggerRepository repository = (LoggerRepository) ec.getObject(0);
      List errorList = ec.getErrorList();
      ConfiguratorBase.detachTemporaryConsoleAppender(repository, errorList);
    }
  }
}
