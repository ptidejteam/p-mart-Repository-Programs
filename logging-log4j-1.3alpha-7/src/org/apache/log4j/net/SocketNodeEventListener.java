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

package org.apache.log4j.net;

import java.util.EventListener;

/**
  Interface used to listen for {@link SocketNode} related
  events. Clients register an instance of the interface and the
  instance is called back when the various events occur.

  @author Mark Womack
  @author Paul Smith <psmith@apache.org>
  @since 1.3
*/
public interface SocketNodeEventListener extends EventListener{
  
  /**
   * Called when the SocketNode is created and begins awaiting data.
   *
   */
  public void socketOpened(String remoteInfo);

  /**
    Called when the socket the node was given has been closed. */
  public void socketClosedEvent(Exception e);
}
