/*
 * $Header: /home/cvs/jakarta-struts/src/share/org/apache/struts/tiles/TilesUtilImpl.java,v 1.9 2004/08/10 02:36:15 niallp Exp $
 * $Revision: 1.9 $
 * $Date: 2004/08/10 02:36:15 $
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

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.tiles.definition.ComponentDefinitionsFactoryWrapper;
import org.apache.struts.util.RequestUtils;

/**
 * Default implementation of TilesUtil.
 * This class contains default implementation of utilities. This implementation
 * is intended to be used without Struts.
 */
public class TilesUtilImpl implements Serializable {
    
    /** Commons Logging instance.*/
    protected Log log = LogFactory.getLog(TilesUtil.class);

    /** Constant name used to store factory in servlet context */
    public static final String DEFINITIONS_FACTORY =
        "org.apache.struts.tiles.DEFINITIONS_FACTORY";

    /**
     * Do a forward using request dispatcher.
     *
     * This method is used by the Tiles package anytime a forward is required.
     * @param uri Uri or Definition name to forward.
     * @param request Current page request.
     * @param servletContext Current servlet context.
     */
    public void doForward(
        String uri,
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext)
        throws IOException, ServletException {
            
        request.getRequestDispatcher(uri).forward(request, response);
    }

    /**
     * Do an include using request dispatcher.
     *
     * This method is used by the Tiles package when an include is required.
     * The Tiles package can use indifferently any form of this method.
     * @param uri Uri or Definition name to forward.
     * @param request Current page request.
     * @param response Current page response.
     * @param servletContext Current servlet context.
     */
    public void doInclude(
        String uri,
        HttpServletRequest request,
        HttpServletResponse response,
        ServletContext servletContext)
        throws IOException, ServletException {
            
        request.getRequestDispatcher(uri).include(request, response);
    }

    /**
     * Do an include using PageContext.include().
     *
     * This method is used by the Tiles package when an include is required.
     * The Tiles package can use indifferently any form of this method.
     * @param uri Uri or Definition name to forward.
     * @param pageContext Current page context.
     */
    public void doInclude(String uri, PageContext pageContext)
        throws IOException, ServletException {
            
        pageContext.include(uri);
    }

    /**
     * Get definition factory from appropriate servlet context.
     * @return Definitions factory or <code>null</code> if not found.
     */
    public DefinitionsFactory getDefinitionsFactory(
        ServletRequest request,
        ServletContext servletContext) {
            
        return (DefinitionsFactory) servletContext.getAttribute(DEFINITIONS_FACTORY);
    }

    /**
     * Create Definition factory from specified configuration object.
     * Create an instance of the factory with the class specified in the config
     * object. Then, initialize this factory and finally store the factory in
     * appropriate context by calling
     * {@link #makeDefinitionsFactoryAccessible(DefinitionsFactory, ServletContext)}.
     * Factory creation is done by {@link #createDefinitionFactoryInstance(String)}.
     * <p>
     *
     * @param servletContext Servlet Context passed to newly created factory.
     * @param factoryConfig Configuration object passed to factory.
     * @return newly created factory of type specified in the config object.
     * @throws DefinitionsFactoryException If an error occur while initializing factory
     */
    public DefinitionsFactory createDefinitionsFactory(
        ServletContext servletContext,
        DefinitionsFactoryConfig factoryConfig)
        throws DefinitionsFactoryException {
            
        // Create configurable factory
        DefinitionsFactory factory =
            createDefinitionFactoryInstance(factoryConfig.getFactoryClassname());
            
        factory.init(factoryConfig, servletContext);
        
        // Make factory accessible from jsp tags (push it in appropriate context)
        makeDefinitionsFactoryAccessible(factory, servletContext);
        return factory;
    }

    /**
     * Create Definition factory of specified classname.
     * Factory class must extend the {@link DefinitionsFactory} class.
     * The factory is wrapped appropriately with {@link ComponentDefinitionsFactoryWrapper}
     * if it is an instance of the deprecated ComponentDefinitionsFactory class.
     * @param classname Class name of the factory to create.
     * @return newly created factory.
     * @throws DefinitionsFactoryException If an error occur while initializing factory
     */
    protected DefinitionsFactory createDefinitionFactoryInstance(String classname)
        throws DefinitionsFactoryException {
            
        try {
            Class factoryClass = RequestUtils.applicationClass(classname);
            Object factory = factoryClass.newInstance();

            // Backward compatibility : if factory classes implements old interface,
            // provide appropriate wrapper
            if (factory instanceof ComponentDefinitionsFactory) {
                factory =
                    new ComponentDefinitionsFactoryWrapper(
                        (ComponentDefinitionsFactory) factory);
            }
            return (DefinitionsFactory) factory;
            
        } catch (ClassCastException ex) { // Bad classname
            throw new DefinitionsFactoryException(
                "Error - createDefinitionsFactory : Factory class '"
                    + classname
                    + " must implement 'TilesDefinitionsFactory'.",
                ex);
                
        } catch (ClassNotFoundException ex) { // Bad classname
            throw new DefinitionsFactoryException(
                "Error - createDefinitionsFactory : Bad class name '"
                    + classname
                    + "'.",
                ex);
                
        } catch (InstantiationException ex) { // Bad constructor or error
            throw new DefinitionsFactoryException(ex);
            
        } catch (IllegalAccessException ex) {
            throw new DefinitionsFactoryException(ex);
        }
    }
    
    /**
     * Make definition factory accessible to Tags.
     * Factory is stored in servlet context.
     * @param factory Factory to be made accessible.
     * @param servletContext Current servlet context.
     */
    protected void makeDefinitionsFactoryAccessible(
        DefinitionsFactory factory,
        ServletContext servletContext) {
            
        servletContext.setAttribute(DEFINITIONS_FACTORY, factory);
    }

}