/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/tiles/DefinitionsFactory.java,v 1.4 2004/03/14 06:23:43 sraeburn Exp $
 * $Revision: 1.4 $
 * $Date: 2004/03/14 06:23:43 $
 *
 * Copyright 1999-2004 The Apache Software Foundation.
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


package org.apache.struts.tiles;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/**
 * Tiles Definition factory.
 * This interface replace old ComponentDefinitionsFactory.
 * Main method getDefinition() is exactly the same. Initialization method change.
 * This interface allows to retrieve a definition by its name, independently of
 * the factory implementation.
 * Object life cycle is as follow:
 * <ul>
 * <li>Constructor: create object</li>
 * <li>setConfig: set config and initialize factory. After first call to this
 * method, factory is operational.</li>
 * <li>destroy: factory is being shutdown.</li>
 * </ul>
 * Implementation must be Serializable, in order to be compliant with web Container
 * having this constraint (Weblogic 6.x).
 */
public interface DefinitionsFactory extends Serializable
{

   /**
     * Get a definition by its name.
     * @param name Name of requested definition.
     * @param request Current servelet request
     * @param servletContext current servlet context
     * @throws DefinitionsFactoryException An error occur while getting definition.
     * @throws NoSuchDefinitionException No definition found for specified name
     * Implementation can throw more accurate exception as a subclass of this exception
   */
   public ComponentDefinition getDefinition(String name, ServletRequest request, ServletContext servletContext)
     throws NoSuchDefinitionException,DefinitionsFactoryException;

   /**
    * Init definition factory.
    * This method is called immediately after factory creation, and prior any call
    * to setConfig().
    *
    * @param config Configuration object used to set factory configuration.
    * @param servletContext Servlet Context passed to factory.
    * @throws DefinitionsFactoryException An error occur during initialization.
    */
   public void init(DefinitionsFactoryConfig config, ServletContext servletContext)
     throws DefinitionsFactoryException;

    /**
     * <p>Receive notification that the factory is being
     * shut down.</p>
     */
    public void destroy();

   /**
    * Set factory configuration.
    * This method is used to change factory configuration.
    * This method is optional, and can send an exception if implementation
    * doesn't allow change in configuration.
    *
    * @param config Configuration object used to set factory configuration.
    * @param servletContext Servlet Context passed to factory.
    * @throws DefinitionsFactoryException An error occur during initialization.
    */
   public void setConfig(DefinitionsFactoryConfig config, ServletContext servletContext)
     throws DefinitionsFactoryException;

   /**
    * Get factory configuration.
    * @return TilesConfig
    */
   public DefinitionsFactoryConfig getConfig();


}
