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

import org.w3c.dom.*;
import java.util.Vector;
import java.util.Properties;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.ClassLoader;
import org.apache.xalan.xpath.xml.PrefixResolver;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the SystemProperty() function.
 */
public class FuncSystemProperty extends Function
{
  Properties xsltInfo = new Properties ();
  static String XSLT_PROPERTIES = "/org/apache/xalan/xpath/xml/XSLTInfo.properties";
  /**
   * Execute the function.  The function must return 
   * a valid object.
   * @param path The executing xpath.
   * @param context The current context.
   * @param opPos The current op position.
   * @param args A list of XObject arguments.
   * @return A valid XObject.
   */
  public XObject execute(XPath path, XPathSupport execContext, Node context, int opPos, Vector args) 
    throws org.xml.sax.SAXException
  {    
    String fullName = ((XObject)args.elementAt(0)).str();
    int indexOfNSSep = fullName.indexOf(':');
    String result;
    String propName= "";
    loadPropertyFile(XSLT_PROPERTIES, xsltInfo);
    if(indexOfNSSep > 0)
    {
      String prefix = (indexOfNSSep >= 0) ? fullName.substring(0, indexOfNSSep) : "";
      String namespace;
      namespace = execContext.getNamespaceContext().getNamespaceForPrefix(prefix);
      propName = (indexOfNSSep < 0) ? fullName 
                                             : fullName.substring(indexOfNSSep+1);
      
      if(namespace.startsWith("http://www.w3.org/XSL/Transform") ||
         namespace.equals("http://www.w3.org/1999/XSL/Transform"))
      {
        result = xsltInfo.getProperty(propName);
        if (null == result)
        {
			    path.warn(XPATHErrorResources.WG_PROPERTY_NOT_SUPPORTED, new Object[] {fullName}); //"XSL Property not supported: "+fullName);
          result = "";
        }
      }
      else
      {
        path.warn(XPATHErrorResources.WG_DONT_DO_ANYTHING_WITH_NS, new Object[] {namespace, fullName}); //"Don't currently do anything with namespace "+namespace+" in property: "+fullName);
        try
        {
          result = System.getProperty(propName);
          if(null == result)
          {
            // result = System.getenv(propName);
            result = "";
          }
        }
        catch(SecurityException se)
        {
          path.warn(XPATHErrorResources.WG_SECURITY_EXCEPTION, new Object[] {fullName}); //"SecurityException when trying to access XSL system property: "+fullName);
          result = "";
        }
      }
    }
    else
    {
      try
      {
        result = System.getProperty(fullName);
        if(null == result)
        {
          // result = System.getenv(fullName);
          result = "";
        }
      }
      catch(SecurityException se)
      {
        path.warn(XPATHErrorResources.WG_SECURITY_EXCEPTION, new Object[] {fullName}); //"SecurityException when trying to access XSL system property: "+fullName);
        result = "";
      }
    }
    if (propName.equals("version") && result.length()>0)
    {  
      try
      {
        return new XNumber(new Double(result).doubleValue());
      }
      catch (Exception ex)
      {
        return new XString(result);
      }    
    }      
    else  
      return new XString(result);
  }
  
/*
 * Retrieve a propery bundle from a specified file
 * @param file The string name of the property file.  The file is loaded from the workplace base directory
 * @param target The target property bag the file will be placed into.
 */
  public void loadPropertyFile (String file, Properties target) 
  {
    InputStream is;
    try
    {   		   
      is= getClass().getResourceAsStream(file);      
                                                           // get a buffered version
      BufferedInputStream bis = new BufferedInputStream (is);
      target.load (bis);                                     // and load up the property bag from this
      bis.close ();                                          // close out after reading
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }  
  }
}
