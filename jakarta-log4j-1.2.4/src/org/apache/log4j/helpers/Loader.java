/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software
 * License version 1.1, a copy of which has been included with this
 * distribution in the LICENSE.txt file.  */

package org.apache.log4j.helpers;

import java.net.URL;
import java.lang.IllegalAccessException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

//import java.awt.Image;
//import java.awt.Toolkit;

/**
   Load resources (or images) from various sources.
 
  @author Ceki G&uuml;lc&uuml;
 */

public class Loader  { 

  static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";

  // We conservatively assume that we are running under Java 1.x
  static private boolean java1 = true;

  static {
    String prop = OptionConverter.getSystemProperty("java.version", null);
    
    if(prop != null) {
      int i = prop.indexOf('.');
      if(i != -1) {	
	if(prop.charAt(i+1) != '1')
	  java1 = false;
      } 
    }
  }

  /* A cache for 
  private static Method GET_TCL_METHOD;

  /**
     This method will search for <code>resource</code> in different
     places. The rearch order is as follows:

     <ol>

     <p><li>Search for <code>resource</code> using the thread context
     class loader under Java2. If that fails, search for
     <code>resource</code> using the class loader that loaded this
     class (<code>Loader</code>). Under JDK 1.1, only the the class
     loader that loaded this class (<code>Loader</code>) is used.

     <p><li>Try one last time with
     <code>ClassLoader.getSystemResource(resource)</code>, that is is
     using the system class loader in JDK 1.2 and virtual machine's
     built-in class loader in JDK 1.1.

     </ol>
     
  */
  static
  public
  URL getResource(String resource) {
    ClassLoader classLoader = null;
    URL url = null;
    
    try {
      if(!java1) {
	classLoader = getTCL();
	if(classLoader != null) {
	  LogLog.debug("Trying to find ["+resource+"] using context classloader "
		       +classLoader+".");
	  url = classLoader.getResource(resource);      
	  if(url != null) {
	    return url;
	  }
	}
      }
      
      // We could not find resource. Ler us now try with the
      // classloader that loaded this class.
      classLoader = Loader.class.getClassLoader(); 
      if(classLoader != null) {
	LogLog.debug("Trying to find ["+resource+"] using "+classLoader
		     +" class loader.");
	url = classLoader.getResource(resource);
	if(url != null) {
	  return url;
	}
      }
    } catch(Throwable t) {
      LogLog.warn(TSTR, t);
    }
    
    // Last ditch attempt: get the resource from the class path. It
    // may be the case that clazz was loaded by the Extentsion class
    // loader which the parent of the system class loader. Hence the
    // code below.
    LogLog.debug("Trying to find ["+resource+
		 "] using ClassLoader.getSystemResource().");
    return ClassLoader.getSystemResource(resource);
  } 
  
  /**
     Are we running under JDK 1.x?        
  */
  public
  static
  boolean isJava1() {
    return java1;
  }
  
  /**
    * Get the Thread Context Loader which is a JDK 1.2 feature. If we
    * are running under JDK 1.1 or anything else goes wrong the method
    * returns <code>null<code>.
    *
    *  */
  private static ClassLoader getTCL() throws IllegalAccessException, 
    InvocationTargetException {

    // Are we running on a JDK 1.2 or later system?
    Method method = null;
    try {
      method = Thread.class.getMethod("getContextClassLoader", null);
    } catch (NoSuchMethodException e) {
      // We are running on JDK 1.1
      return null;
    }
    
    return (ClassLoader) method.invoke(Thread.currentThread(), null);
  }


  
  /**
   * If running under JDK 1.2 load the specified class using the
   *  <code>Thread</code> <code>contextClassLoader</code> if that
   *  fails try Class.forname. Under JDK 1.1 only Class.forName is
   *  used.
   *
   */
  static public Class loadClass (String clazz) throws ClassNotFoundException {
    if(java1) {
      return Class.forName(clazz);
    } else {
      try {
	return getTCL().loadClass(clazz);
      } catch(Throwable e) {
	// we reached here because tcl was null or because of a
	// security exception, or because clazz could not be loaded...
	// In any case we now try one more time
	return Class.forName(clazz);
      }
    }
  } 
}
