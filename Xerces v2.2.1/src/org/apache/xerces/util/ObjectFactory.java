/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights 
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The name "Apache Software Foundation" must not be used to endorse or
 *    promote products derived from this software without prior written
 *    permission. For written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
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
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999-2001, Sun Microsystems,
 * Inc., http://www.sun.com.  For more information on the Apache Software
 * Foundation, please see <http://www.apache.org/>.
 */

package org.apache.xerces.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is duplicated for each JAXP subpackage so keep it in sync.
 * It is package private and therefore is not exposed as part of the JAXP
 * API.
 * <p>
 * This code is designed to implement the JAXP 1.1 spec pluggability
 * feature and is designed to run on JDK version 1.1 and
 * later, and to compile on JDK 1.2 and onward.  
 * The code also runs both as part of an unbundled jar file and
 * when bundled as part of the JDK.
 * <p>
 * This class was moved from the <code>javax.xml.parsers.ObjectFactory</code>
 * class and modified to be used as a general utility for creating objects 
 * dynamically.
 *
 * @version $Id: ObjectFactory.java,v 1.8 2002/10/09 17:39:36 neilg Exp $
 */
public class ObjectFactory {

    //
    // Constants
    //

    // name of default properties file to look for in JDK's jre/lib directory
    private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";

    /** Set to true for debugging */
    private static final boolean DEBUG = false;

    //
    // Public static methods
    //

    /**
     * Finds the implementation Class object in the specified order.  The
     * specified order is the following:
     * <ol>
     *  <li>query the system property using <code>System.getProperty</code>
     *  <li>read <code>META-INF/services/<i>factoryId</i></code> file
     *  <li>use fallback classname
     * </ol>
     *
     * @return Class object of factory, never null
     *
     * @param factoryId             Name of the factory to find, same as
     *                              a property name
     * @param fallbackClassName     Implementation class name, if nothing else
     *                              is found.  Use null to mean no fallback.
     *
     * @exception ObjectFactory.ConfigurationError
     */
    public static Object createObject(String factoryId, String fallbackClassName)
        throws ConfigurationError {
        return createObject(factoryId, null, fallbackClassName);
    } // createObject(String,String):Object

    /**
     * Finds the implementation Class object in the specified order.  The
     * specified order is the following:
     * <ol>
     *  <li>query the system property using <code>System.getProperty</code>
     *  <li>read <code>$java.home/lib/<i>propertiesFilename</i></code> file
     *  <li>read <code>META-INF/services/<i>factoryId</i></code> file
     *  <li>use fallback classname
     * </ol>
     *
     * @return Class object of factory, never null
     *
     * @param factoryId             Name of the factory to find, same as
     *                              a property name
     * @param propertiesFilename The filename in the $java.home/lib directory
     *                           of the properties file.  If none specified,
     *                           ${java.home}/lib/xerces.properties will be used.
     * @param fallbackClassName     Implementation class name, if nothing else
     *                              is found.  Use null to mean no fallback.
     *
     * @exception ObjectFactory.ConfigurationError
     */
    public static Object createObject(String factoryId, 
                                      String propertiesFilename,
                                      String fallbackClassName)
        throws ConfigurationError
    {
        debugPrintln("debug is on");

        SecuritySupport ss = SecuritySupport.getInstance();
        ClassLoader cl = findClassLoader();

        // Use the system property first
        try {
            String systemProp = ss.getSystemProperty(factoryId);
            if (systemProp != null) {
                debugPrintln("found system property, value=" + systemProp);
                return newInstance(systemProp, cl, true);
            }
        } catch (SecurityException se) {
            // Ignore and continue w/ next location
        }

        // Try to read from propertiesFilename, or $java.home/lib/xerces.properties
        try {
            if(propertiesFilename ==null) {
            String javah = ss.getSystemProperty("java.home");
                propertiesFilename = javah + File.separator +
                    "lib" + File.separator + DEFAULT_PROPERTIES_FILENAME;
            }
            FileInputStream fis = ss.getFileInputStream(new File(propertiesFilename));
            Properties props = new Properties();
            props.load(fis);
            String factoryClassName = props.getProperty(factoryId);
            if (factoryClassName != null) {
                debugPrintln("found in " + propertiesFilename + ", value=" + factoryClassName);
                return newInstance(factoryClassName, cl, true);
            }
        } catch (Exception x) {
            // assert(x instanceof FileNotFoundException
            //        || x instanceof SecurityException)
            // In both cases, ignore and continue w/ next location
        }

        // Try Jar Service Provider Mechanism
        Object provider = findJarServiceProvider(factoryId);
        if (provider != null) {
            return provider;
        }

        if (fallbackClassName == null) {
            throw new ConfigurationError(
                "Provider for " + factoryId + " cannot be found", null);
        }

        debugPrintln("using fallback, value=" + fallbackClassName);
        return newInstance(fallbackClassName, cl, true);
    } // createObject(String,String,String):Object

    //
    // Private static methods
    //

    /** Prints a message to standard error if debugging is enabled. */
    private static void debugPrintln(String msg) {
        if (DEBUG) {
            System.err.println("JAXP: " + msg);
        }
    } // debugPrintln(String)

    /**
     * Figure out which ClassLoader to use.  For JDK 1.2 and later use
     * the context ClassLoader.
     */           
    public static ClassLoader findClassLoader()
        throws ConfigurationError
    { 
        SecuritySupport ss = SecuritySupport.getInstance();

        // Figure out which ClassLoader to use for loading the provider
        // class.  If there is a Context ClassLoader then use it.
        ClassLoader cl = ss.getContextClassLoader();
        if (cl == null) {
            // Assert: we are on JDK 1.1 or we have no Context ClassLoader
            // so use the current ClassLoader
            cl = ObjectFactory.class.getClassLoader();
        }
        return cl;

    } // findClassLoader():ClassLoader

    /**
     * Create an instance of a class using the specified ClassLoader
     */ 
    public static Object newInstance(String className, ClassLoader cl,
                                      boolean doFallback)
        throws ConfigurationError
    {
        // assert(className != null);
        try{
            Class providerClass = findProviderClass(className, cl, doFallback);
            Object instance = providerClass.newInstance();
            debugPrintln("created new instance of " + providerClass +
                   " using ClassLoader: " + cl);
            return instance;
        } catch (ClassNotFoundException x) {
            throw new ConfigurationError(
                "Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new ConfigurationError(
                "Provider " + className + " could not be instantiated: " + x,
                x);
        }
    }

    /**
     * Find a Class using the specified ClassLoader
     */ 
    public static Class findProviderClass(String className, ClassLoader cl,
                                      boolean doFallback)
        throws ClassNotFoundException, ConfigurationError
    {
        Class providerClass;
        if (cl == null) {
            // XXX Use the bootstrap ClassLoader.  There is no way to
            // load a class using the bootstrap ClassLoader that works
            // in both JDK 1.1 and Java 2.  However, this should still
            // work b/c the following should be true:
            //
            // (cl == null) iff current ClassLoader == null
            //
            // Thus Class.forName(String) will use the current
            // ClassLoader which will be the bootstrap ClassLoader.
            providerClass = Class.forName(className);
        } else {
            try {
                providerClass = cl.loadClass(className);
            } catch (ClassNotFoundException x) {
                if (doFallback) {
                    // Fall back to current classloader
                    cl = ObjectFactory.class.getClassLoader();
                    providerClass = cl.loadClass(className);
                } else {
                    throw x;
                }
            }
        }
        return providerClass;
    }

    /*
     * Try to find provider using Jar Service Provider Mechanism
     *
     * @return instance of provider class if found or null
     */
    private static Object findJarServiceProvider(String factoryId)
        throws ConfigurationError
    {
        SecuritySupport ss = SecuritySupport.getInstance();
        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;

        // First try the Context ClassLoader
        ClassLoader cl = ss.getContextClassLoader();
        if (cl != null) {
            is = ss.getResourceAsStream(cl, serviceId);

            // If no provider found then try the current ClassLoader
            if (is == null) {
                cl = ObjectFactory.class.getClassLoader();
                is = ss.getResourceAsStream(cl, serviceId);
            }
        } else {
            // No Context ClassLoader or JDK 1.1 so try the current
            // ClassLoader
            cl = ObjectFactory.class.getClassLoader();
            is = ss.getResourceAsStream(cl, serviceId);
        }

        if (is == null) {
            // No provider found
            return null;
        }

        debugPrintln("found jar resource=" + serviceId +
               " using ClassLoader: " + cl);

        // Read the service provider name in UTF-8 as specified in
        // the jar spec.  Unfortunately this fails in Microsoft
        // VJ++, which does not implement the UTF-8
        // encoding. Theoretically, we should simply let it fail in
        // that case, since the JVM is obviously broken if it
        // doesn't support such a basic standard.  But since there
        // are still some users attempting to use VJ++ for
        // development, we have dropped in a fallback which makes a
        // second attempt using the platform's default encoding. In
        // VJ++ this is apparently ASCII, which is a subset of
        // UTF-8... and since the strings we'll be reading here are
        // also primarily limited to the 7-bit ASCII range (at
        // least, in English versions), this should work well
        // enough to keep us on the air until we're ready to
        // officially decommit from VJ++. [Edited comment from
        // jkesselm]
        BufferedReader rd;
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (java.io.UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is));
        }
        
        String factoryClassName = null;
        try {
            // XXX Does not handle all possible input as specified by the
            // Jar Service Provider specification
            factoryClassName = rd.readLine();
            rd.close();
        } catch (IOException x) {
            // No provider found
            return null;
        }

        if (factoryClassName != null &&
            ! "".equals(factoryClassName)) {
            debugPrintln("found in resource, value="
                   + factoryClassName);

            // Note: here we do not want to fall back to the current
            // ClassLoader because we want to avoid the case where the
            // resource file was found using one ClassLoader and the
            // provider class was instantiated using a different one.
            return newInstance(factoryClassName, cl, false);
        }

        // No provider found
        return null;
    }

    //
    // Classes
    //

    /**
     * A configuration error.
     */
    public static class ConfigurationError 
        extends Error {

        //
        // Data
        //

        /** Exception. */
        private Exception exception;

        //
        // Constructors
        //

        /**
         * Construct a new instance with the specified detail string and
         * exception.
         */
        public ConfigurationError(String msg, Exception x) {
            super(msg);
            this.exception = x;
        } // <init>(String,Exception)

        //
        // Public methods
        //

        /** Returns the exception associated to this error. */
        public Exception getException() {
            return exception;
        } // getException():Exception

    } // class ConfigurationError

} // class ObjectFactory
