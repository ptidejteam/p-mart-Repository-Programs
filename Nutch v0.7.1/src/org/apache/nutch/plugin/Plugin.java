/*
/**
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nutch.plugin;
/**
 * A nutch-plugin is an container for a set of custom logic that provide
 * extensions to the nutch core functionality or another plugin that provides an
 * API for extending. A plugin can provide one or a set of extensions.
 * Extensions are components that can be dynamically installed as a kind of
 * listener to extension points. Extension points are a kind of publisher that
 * provide a API and invoke one or a set of installed extensions.
 * 
 * Each plugin may extend the base <code>Plugin</code>. <code>Plugin</code>
 * instances are used as the point of life cycle managemet of plugin related
 * functionality.
 * 
 * The <code>Plugin</code> will be startuped and shutdown by the nutch plugin
 * management system.
 * 
 * A possible usecase of the <code>Plugin</code> implementation is to create
 * or close a database connection.
 * 
 * @author joa23
 */
public class Plugin {
  private PluginDescriptor fDescriptor;
  /**
   * Constructor
   *  
   */
  public Plugin(PluginDescriptor pDescriptor) {
    setDescriptor(pDescriptor);
  }
  /**
   * Will be invoked until plugin start up. Since the nutch-plugin system use
   * lazy loading the start up is invoked until the first time a extension is
   * used.
   * 
   * @throws PluginRuntimeException
   *             If the startup was without successs.
   */
  public void startUp() throws PluginRuntimeException {}
  /**
   * Shutdown the plugin. This happens until nutch will be stopped.
   * 
   * @throws PluginRuntimeException
   *             if a problems occurs until shutdown the plugin.
   */
  public void shutDown() throws PluginRuntimeException {}
  /**
   * Returns the plugin descriptor
   * 
   * @return PluginDescriptor
   */
  public PluginDescriptor getDescriptor() {
    return fDescriptor;
  }
  /**
   * @param descriptor
   *            The descriptor to set
   */
  private void setDescriptor(PluginDescriptor descriptor) {
    fDescriptor = descriptor;
  }
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#finalize()
   */
  protected void finalize() throws Throwable {
    super.finalize();
    shutDown();
  }
}
