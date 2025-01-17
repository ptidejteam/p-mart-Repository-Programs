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

package org.apache.log4j.spi;


/**
  Interface used to listen for {@link LoggerRepository} related
  events such as startup, reset, and shutdown.  Clients register
  an instance of the interface and the instance is called back
  when the various events occur.

  {@link LoggerRepository} provides methods for adding and removing
  LoggerEventListener instances.

  @author Ceki G&uuml;lc&uuml;
  @author Mark Womack
  @since 1.3
*/
public interface LoggerRepositoryEventListener {
  /**
    Called when the repository configuration is reset. */
  public void configurationResetEvent(LoggerRepository repository);

  /**
    Called when the repository configuration is changed. */
  public void configurationChangedEvent(LoggerRepository repository);

  /**
    Called when the repository is shutdown. When this method is
    invoked, the repository is still valid (ie it has not been
    shutdown, but will be after this method returns). */
  public void shutdownEvent(LoggerRepository repository);
}
