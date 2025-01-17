/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.xpath;

import java.util.*;
import java.io.*;
import java.net.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import com.ibm.bsf.BSFException;
import java.lang.reflect.*;
import com.ibm.cs.util.ReflectionUtils;


/**
 * <meta name="usage" content="internal"/>
 * Class handling an extension namespace for XPath. Provides functions
 * to test a function's existence and call a function
 *
 * @author Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */
public class ExtensionFunctionHandler 
{
  public String namespaceUri;  // uri of the extension namespace
  public String scriptLang = "javaclass";    // scripting language of implementation
  public String scriptSrc;     // script source to run (if any)
  public String scriptSrcURL;  // URL of source of script (if any)
  public Object javaObject = null;    // object for javaclass engine
  protected boolean hasCalledCTor = false;  // we'll be nice and call a ctor if they haven't
  public Class classObject = null;  // class object for javaclass engine
  protected Hashtable functions = new Hashtable (); // functions of namespace
  protected Hashtable elements = new Hashtable (); // elements of namespace
  protected com.ibm.bsf.BSFManager mgr = null; // mgr used to run scripts
  protected boolean componentStarted; // true when the scripts in a
  // component description (if any) have
  // been run

  /////////////////////////////////////////////////////////////////////////
  // Constructors
  /////////////////////////////////////////////////////////////////////////

  /**
   * Construct a new extension namespace handler for a given extension NS.
   * This doesn't do anything - just hang on to the namespace URI.
   * 
   * @param namespaceUri the extension namespace URI that I'm implementing
   */
  public ExtensionFunctionHandler (String namespaceUri) 
  {
    this.namespaceUri = namespaceUri;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Construct a new extension namespace handler given all the information
   * needed. 
   * 
   * @param namespaceUri the extension namespace URI that I'm implementing
   * @param funcNames    string containing list of functions of extension NS
   * @param lang         language of code implementing the extension
   * @param srcURL       value of src attribute (if any) - treated as a URL
   *                     or a classname depending on the value of lang. If
   *                     srcURL is not null, then scriptSrc is ignored.
   * @param scriptSrc    the actual script code (if any)
   */
  public ExtensionFunctionHandler (String namespaceUri, String funcNames, 
                                   String lang, String srcURL, String src) 
  {
    this (namespaceUri);
    setFunctions (funcNames);
    setScript (lang, srcURL, src);
  }

  /////////////////////////////////////////////////////////////////////////
  // Main API
  /////////////////////////////////////////////////////////////////////////

  /**
   * Set function local parts of extension NS.
   *
   * @param functions whitespace separated list of function names defined
   *        by this extension namespace.
   */
  public void setFunctions (String funcNames) 
  {
    if (funcNames == null) 
    {
      return;
    }
    StringTokenizer st = new StringTokenizer (funcNames, " \t\n\r", false);
    Object junk = new Object ();
    while (st.hasMoreTokens ()) 
    {
      String tok = st.nextToken ();
      functions.put (tok, junk); // just stick it in there basically
    }
  }

  /**
   * Set element local parts of extension NS.
   *
   * @param elements whitespace separated list of element names defined
   *        by this extension namespace.
   */
  public void setElements (String elemNames) 
  {
    if (elemNames == null) 
    {
      return;
    }
    StringTokenizer st = new StringTokenizer (elemNames, " \t\n\r", false);
    Object junk = new Object ();
    while (st.hasMoreTokens ()) 
    {
      String tok = st.nextToken ();
      elements.put (tok, junk); // just stick it in there basically
    }
  }
  
  /////////////////////////////////////////////////////////////////////////

  /**
   * Set the script data for this extension NS. If srcURL is !null then
   * the script body is read from that URL. If not the scriptSrc is used
   * as the src. This method does not actually execute anything - that's
   * done when the component is first hit by the user by an element or 
   * a function call.
   *
   * @param lang      language of the script.
   * @param srcURL    value of src attribute (if any) - treated as a URL
   *                  or a classname depending on the value of lang. If
   *                  srcURL is not null, then scriptSrc is ignored.
   * @param scriptSrc the actual script code (if any)
   */
  public void setScript (String lang, String srcURL, String scriptSrc) 
  {
    this.scriptLang = lang;
    this.scriptSrcURL = srcURL;
    this.scriptSrc = scriptSrc;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Tests whether a certain function name is known within this namespace.
   *
   * @param function name of the function being tested
   *
   * @return true if its known, false if not.
   */
  public boolean isFunctionAvailable (String function) 
  {
    return (functions.get (function) != null);
  }
  
  /**
   * Tests whether a certain element name is known within this namespace.
   *
   * @param function name of the function being tested
   *
   * @return true if its known, false if not.
   */
  public boolean isElementAvailable (String element) 
  {
    return (elements.get (element) != null);
  }


  Hashtable m_cachedMethods = null;
  
  /**
   * call the named method on the object that was loaded by eval. 
   * The method selection stuff is very XSLT-specific, hence the
   * custom engine.
   *
   * @param object ignored - should always be null
   */
  public Object callJava (Object object, String method, Object[] args, 
                          Object methodKey) 
    throws BSFException
  {
    // if the function name has a "." in it, then its a static
    // call with the args being arguments to the call. If it 
    // does not, then its a method call on args[0] with the rest
    // of the args as the arguments to the call
    int dotPos = method.lastIndexOf (".");
    Object[] methodArgs = null;
    boolean isNew = false;
    if (dotPos == -1) 
    {
      methodArgs = args;
      object = args[0];
      if (args.length > 1) 
      {
        methodArgs = new Object[args.length-1];
        System.arraycopy (args, 1, methodArgs, 0, args.length - 1);
      }
      else
        methodArgs = null;
    }
    else 
    {
      String className = method.substring (0, dotPos);
      method = method.substring (dotPos+1);
      methodArgs = args;
      isNew = method.equals ("new");
      try 
      {
        if(null == object)
          object = Class.forName (className);
      }
      catch (ClassNotFoundException e) 
      {
        throw new BSFException(1, "unable to load class '" + 
          className + "'", e);
      }
    }
    
    if((null != m_cachedMethods) && !isNew && (null != object))
    {
      try
      {
        Method thisMethod = (Method)m_cachedMethods.get(methodKey);
        if(null != thisMethod)
        {
          return thisMethod.invoke (object, methodArgs);
        }
      }
      catch(Exception e)
      {
        // try again below...
      }
    
    }

    // determine the argument types as they are given
    Class[] argTypes = null;
    if (methodArgs != null) 
    {
      argTypes = new Class[methodArgs.length];
      for (int i = 0; i < argTypes.length; i++) 
      {
        argTypes[i] = (methodArgs[i]!=null) ? methodArgs[i].getClass() : null;
      }
    }

    // try to find the method and run it, taking into account the special
    // type conversions we want. If an arg is a Double, we first try with
    // double and then with Double. Same for Boolean/boolean. This is done
    // wholesale tho - that is, if there are two Double args both are
    // tried double first and then Double.
    boolean done = false;
    boolean toggled = false;
    try 
    {
      while (!done) 
      {
        if (methodArgs == null) 
        {
          done = true; // nothing to retry - do as-is or give up
        }
        else 
        {
          if (!toggled) 
          {
            for (int i = 0; i < argTypes.length; i++) 
            {
              Class cl = argTypes[i];
              if (cl != null) 
              {
                if (cl == Double.class) 
                {
                  cl = double.class;
                }
                if (cl == Float.class) 
                {
                  cl = float.class;
                }
                else if (cl == Boolean.class) 
                {
                  cl = boolean.class;
                }
                else if (cl == Byte.class) 
                {
                  cl = byte.class;
                }
                else if (cl == Character.class) 
                {
                  cl = char.class;
                }
                else if (cl == Short.class) 
                {
                  cl = short.class;
                }
                else if (cl == Integer.class) 
                {
                  cl = int.class;
                }
                else if (cl == Long.class) 
                {
                  cl = long.class;
                }
                argTypes[i] = cl;
              }
            }
            toggled = true;
          }
          else 
          {
            for (int i = 0; i < argTypes.length; i++) 
            {
              Class cl = argTypes[i];
              if (cl != null) 
              {
                if (cl == double.class) 
                {
                  cl = Double.class;
                }
                if (cl == float.class) 
                {
                  cl = Float.class;
                }
                else if (cl == boolean.class) 
                {
                  cl = Boolean.class;
                }
                else if (cl == byte.class) 
                {
                  cl = Byte.class;
                }
                else if (cl == char.class) 
                {
                  cl = Character.class;
                }
                else if (cl == short.class) 
                {
                  cl = Short.class;
                }
                else if (cl == int.class) 
                {
                  cl = Integer.class;
                }
                else if (cl == long.class) 
                {
                  cl = Long.class;
                }
                argTypes[i] = cl;
              }
            }
            done = true;
          }
        }
        
        // now find method with the right signature, call it and return result.
        try 
        {
          if (isNew) 
          {
            // if its a "new" call then need to find and invoke a constructor
            // otherwise find and invoke the appropriate method. The method
            // searching logic is the same of course.
            Constructor c =
                           ReflectionUtils.getConstructor ((Class) object, argTypes);
            Object obj = c.newInstance (methodArgs);
            return obj;
          }
          else 
          {
            Method m = ReflectionUtils.getMethod (object, method, argTypes);
            Object returnObj = m.invoke (object, methodArgs);
            if(!isNew)
            {
              if(null == m_cachedMethods)
                m_cachedMethods = new Hashtable();
              m_cachedMethods.put(methodKey, m);
            }
            return returnObj;
          }
        }
        catch (NoSuchMethodException e) 
        {
          // ignore if not done looking
          if (done) 
          {
            throw e;
          }
        }
      }
    }
    catch (Exception e) 
    {
      Throwable t = (e instanceof InvocationTargetException) ?
                    ((InvocationTargetException)e).getTargetException () :
                    null;
      throw new BSFException (BSFException.REASON_OTHER_ERROR,
        "method call/new failed: " + e +
        ((t==null)?"":(" target exception: "+t)), t);
    }
    // should not get here
    return null;
  }

  /////////////////////////////////////////////////////////////////////////

  /**
   * Process a call to a function.
   *
   * @param funcName Function name.
   * @param args     The arguments of the function call.
   *
   * @return the return value of the function evaluation.
   *
   * @exception XSLProcessorException thrown if something goes wrong 
   *            while running the extension handler.
   * @exception MalformedURLException if loading trouble
   * @exception FileNotFoundException if loading trouble
   * @exception IOException           if loading trouble
   * @exception SAXException          if parsing trouble
   */
  public Object callFunction (String funcName, Vector args, Object methodKey, Class javaClass)
    throws XPathException 
  {
    if (!componentStarted) 
    {
      startupComponent (javaClass);
    }

    boolean isJava = false;
    try 
    {
      com.ibm.bsf.BSFEngine e;
      Object[] argArray;
      int argStart;
      
      if(null == mgr)
      {
        mgr = new com.ibm.bsf.BSFManager ();
      }

      // we want to use the xslt-javaclass engine to handle the javaclass
      // case 'cause of the funky method selection rules. That engine 
      // expects the first arg to be the object on which to make the call.
      boolean isCTorCall = false;
      if (scriptLang.equals ("javaclass")) 
      {
        isJava = true;
        isCTorCall = funcName.equals("new");
        e = mgr.loadScriptingEngine ("xslt-javaclass");
        if(isCTorCall)
        {
          argArray = new Object[args.size ()];
          argStart = 0;
          funcName = this.classObject.getName()+".new";
          javaObject = null;
          hasCalledCTor = true;
        }
        else
        {
          if(!hasCalledCTor)
          {
            if(null == javaObject)
            {
              javaObject = this.classObject.newInstance();
            }
            
            argArray = new Object[args.size () + 1];
            
            argArray[0] = javaObject;
            argStart = 1;
            // argArray = new Object[args.size ()];
            // argStart = 0;
          }
          else
          {
            argArray = new Object[args.size ()];
            argStart = 0;
          }
        }
      }
      else 
      {
        e = ((com.ibm.bsf.BSFManager)mgr).loadScriptingEngine (scriptLang);
        argArray = new Object[args.size ()];
        argStart = 0;
      }

      // convert the xobject args to their object forms
      for (int i = 0; i < args.size (); i++) 
      {
        Object o = args.elementAt (i);
        argArray[i+argStart] = 
                              (o instanceof XObject) ? ((XObject)o).object () : o;
      }
      
      if(isJava)
        return callJava(javaObject, funcName, argArray, methodKey);
      else
        return e.call (null, funcName, argArray);
    }
    catch (Exception e) 
    {
      // throw new XPathException ("Error with extension in callFunction.", e);
      String msg = e.getMessage();
      if(null != msg)
      {
        if(msg.startsWith("Stopping after fatal error:"))
        {
          msg = msg.substring("Stopping after fatal error:".length());
        }
        System.out.println("Call to extension function failed: "+msg);
      }
      else
      {
        throw new XPathProcessorException ("Extension not found");
      }
    }
    return new XNull();
  }

  /////////////////////////////////////////////////////////////////////////
  // Private/Protected Functions
  /////////////////////////////////////////////////////////////////////////
  
  /**
   * Tell if we've already initialized the bsf engine.
   */
  protected static boolean bsfInitialized = false;

  /**
   * Start the component up by executing any script that needs to run
   * at startup time. This needs to happen before any functions can be
   * called on the component. 
   * 
   * @exception XPathProcessorException if something bad happens.
   */
  protected void startupComponent (Class classObj) throws  XPathProcessorException 
  {
    if(!bsfInitialized)
    {
      synchronized(com.ibm.bsf.BSFManager.class)
      {
        bsfInitialized = true;
        com.ibm.bsf.BSFManager.registerScriptingEngine ("xslt-javaclass",
                                                        "org.apache.xalan.xpath.XSLTJavaClassEngine",
                                                        new String[0]);
      }
    }

    // special case the javaclass engine - the scriptSrcURL is 
    // the class name to run. If it starts with class: then use the
    // class object with that name instead of init'ing it as the
    // target of the calls later
    if(null != classObj)
    {
      classObject = classObj;
      if (scriptSrcURL.startsWith ("class:")) 
      {
        javaObject = classObj;
      }
      return;
    }
    else
    {
      if (scriptLang.equals ("javaclass") && (scriptSrcURL != null)) 
      {
        try 
        {
          String cname = scriptSrcURL;
          boolean isClass = false;
          if (scriptSrcURL.startsWith ("class:")) 
          {
            cname = scriptSrcURL.substring (6);
            isClass = true;
          }
          classObject = Class.forName (cname);
          if (isClass) 
          {
            javaObject = classObject;
          }
          else
          {
            // We'll only do this if they haven't called a ctor.
            // javaObject = classObject.newInstance ();
          }
          componentStarted = true;
          return;
        }
        catch (Exception e) 
        {
          // System.out.println("Extension error: "+e.getMessage ());
          throw new XPathProcessorException (e.getMessage (), e);
        }
      }
    }

    // if scriptSrcURL is specified read it off
    if (scriptSrcURL != null) 
    {
      throw new XPathProcessorException ("src attr not supported (yet)");
    }

    if (scriptSrc == null) 
    {
      return;
    }
    
    if(null == mgr)
      mgr = new com.ibm.bsf.BSFManager ();

    // evaluate the src to load whatever content is in that string to 
    // the engines
    try 
    {
      ((com.ibm.bsf.BSFManager)mgr).exec (scriptLang, "LotusXSLScript", -1, -1, scriptSrc);
    }
    catch (com.ibm.bsf.BSFException bsfe) 
    {
      throw new XPathProcessorException (bsfe.getMessage (), bsfe);
    }
    componentStarted = true;
  }
}
