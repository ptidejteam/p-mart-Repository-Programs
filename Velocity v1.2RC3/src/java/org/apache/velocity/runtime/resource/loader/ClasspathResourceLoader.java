package org.apache.velocity.runtime.resource.loader;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Velocity", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
 
import java.io.InputStream;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.apache.commons.collections.ExtendedProperties;

/**
 *  ClasspathResourceLoader is a simple loader that will load
 *  templates from the classpath.
 *  <br>
 *  <br>
 *  Will load templates from  from multiple instances of 
 *  and arbitrary combinations of :
 *  <ul>
 *  <li> jar files
 *  <li> zip files
 *  <li> template directories (any directory containing templates)
 *  </ul>
 *  This is a configuration-free loader, in that there are no
 *  parameters to be specified in the configuration properties,
 *  other than specifying this as the loader to use.  For example
 *  the following is all that the loader needs to be functional :
 *  <br>
 *  <br>
 *  resource.loader.1.class = 
 *    org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
 *  <br>
 *  <br>
 *  To use, put your template directories, jars
 *  and zip files into the classpath or other mechanisms that make
 *  resources accessable to the classloader.
 *  <br>
 *  <br>
 *  This makes deployment trivial for web applications running in
 *  any Servlet 2.2 compliant servlet runner, such as Tomcat 3.2 
 *  and others.
 *  <br>
 *  <br>
 *  For a Servlet Spec v2.2 servlet runner, 
 *  just drop the jars of template files into the WEB-INF/lib
 *  directory of your webapp, and you won't have to worry about setting
 *  template paths or altering them with the root of the webapp
 *  before initializing.
 *  <br>
 *  <br>
 *  I have also tried it with a WAR deployment, and that seemed to
 *  work just fine.
 *  
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: ClasspathResourceLoader.java,v 1.6 2001/08/07 21:58:18 geirm Exp $
 */
public class ClasspathResourceLoader extends ResourceLoader
{

    /**
     *  This is abstract in the base class, so we need it
     */
    public void init( ExtendedProperties configuration)
    {
        rsvc.info("ClasspathResourceLoader : initialization starting.");
        rsvc.info("ClasspathResourceLoader : initialization complete.");
    }

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param name name of template to get
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *         in  classpath.
     */
    public synchronized InputStream getResourceStream( String name )
        throws ResourceNotFoundException
    {
        InputStream result = null;
        
        if (name == null || name.length() == 0)
        {
            throw new ResourceNotFoundException ("No template name provided");
        }
        
        try 
        {
            ClassLoader classLoader = this.getClass().getClassLoader();
            result= classLoader.getResourceAsStream( name );
        }
        catch( Exception fnfe )
        {
            /*
             *  log and convert to a general Velocity ResourceNotFoundException
             */
            
            throw new ResourceNotFoundException( fnfe.getMessage() );
        }
        
        return result;
    }
    
    /**
     * Defaults to return false.
     */
    public boolean isSourceModified(Resource resource)
    {
        return false;
    }

    /**
     * Defaults to return 0
     */
    public long getLastModified(Resource resource)
    {
        return 0;
    }
}

