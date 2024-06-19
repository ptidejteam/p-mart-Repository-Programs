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
package org.apache.xalan.xslt;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.StringTokenizer;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xpath.xml.NameSpace;
import org.apache.xalan.xpath.xml.StringToStringTable;
import org.apache.xalan.xpath.xml.XMLParserLiaisonDefault;
import org.apache.xalan.xpath.xml.MutableAttrListImpl;
import org.apache.xalan.xpath.xml.XSLMessages;
import org.apache.xalan.xpath.XPathSupport;
import org.apache.xalan.xslt.res.XSLTErrorResources;

import java.io.*;
import java.util.*;

/**
 * <meta name="usage" content="advanced"/>
 * Implement an extension element.
 */
public class ElemExtensionCall extends ElemLiteralResult
{
  // ExtensionNSHandler nsh;
  String m_extns;
  String m_extHandlerLookup;
  String localPart;
  AttributeList m_attrs;
  // public Vector m_avts = null;
  transient boolean isAvailable = false;
  String m_lang;
  String m_srcURL;
  String m_scriptSrc;
  Class m_javaClass = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_EXTENSIONCALL;
  }

  ElemExtensionCall(XSLTEngineImpl processor, 
                    Stylesheet stylesheetTree,
                    // ExtensionNSHandler nsh,
                    String extns,
                    String lang, String srcURL, String scriptSrc,
                    String name, 
                    String localPart,
                    AttributeList atts,
                    int lineNumber, int columnNumber)
    throws SAXException
  {
    super(processor, stylesheetTree, name, atts, lineNumber, columnNumber);
        
    m_extHandlerLookup = new String("ElemExtensionCall:"+extns);
    m_extns = extns;
    m_lang = lang;
    m_srcURL = srcURL;
    m_scriptSrc = scriptSrc;
    
    if (m_lang.equals ("javaclass") && (m_srcURL != null)) 
    {
      try 
      {
        String cname = m_srcURL;
        boolean isClass = false;
        if (cname.startsWith ("class:")) 
        {
          cname = cname.substring (6);
          isClass = true;
        }
        // m_javaClass = Class.forName (cname);
      }
      catch (Exception e) 
      {
        // System.out.println("Extension error: "+e.getMessage ());
        throw new XSLProcessorException (e.getMessage (), e);
      }
    }
    
    // Make a copy of the attributes, so we can give them to the 
    // extension in raw form.  The alternative to doing this is to 
    // reconstruct the list in raw form.
    m_attrs = new MutableAttrListImpl(atts);

    // this.nsh = nsh;
    // processor.getXMLProcessorLiaison().addExtensionNamespace(m_extHandlerLookup, nsh);
    this.localPart = localPart;
    m_attrs = new MutableAttrListImpl(atts);
  }
  
  /**
   * Tell if this extension element is available for execution.
   */
  public boolean elementIsAvailable()
  {
    return isAvailable;
  }

  public void execute(XSLTEngineImpl processor, 
                      Node sourceTree, 
                      Node sourceNode,
                      QName mode)
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {
    try
    {
      processor.flushPending();
      XMLParserLiaisonDefault liaison = ((XMLParserLiaisonDefault)processor.getXMLProcessorLiaison());
      ExtensionNSHandler nsh 
        = (ExtensionNSHandler)liaison.m_extensionFunctionNamespaces.get(m_extns);

      if(null == nsh)
      {
        nsh = new ExtensionNSHandler (processor, m_extns);
        nsh.setScript (m_lang, m_srcURL, m_scriptSrc);
        liaison.addExtensionElementNamespace(m_extns, nsh);
      }
      nsh.processElement (localPart, this,
                          processor, 
                          m_stylesheet,
                          sourceTree, sourceNode, mode, m_javaClass, this);
    }
    catch(Exception e)
    {
      String msg = e.getMessage();
      if(null != msg)
      {
        if(msg.startsWith("Stopping after fatal error:"))
        {
          msg = msg.substring("Stopping after fatal error:".length());
        }
        processor.message(XSLMessages.createMessage(XSLTErrorResources.ER_CALL_TO_EXT_FAILED, new Object[]{msg})); //"Call to extension element failed: "+msg);
        // e.printStackTrace();
        // System.exit(-1);
      }
      // processor.message(msg);
      isAvailable = false; 
      for (ElemTemplateElement child = m_firstChild; child != null; child = child.m_nextSibling) 
      {
        if(child.getXSLToken() == Constants.ELEMNAME_FALLBACK)
        {
          child.execute(processor, sourceTree, sourceNode, mode);
        }
      }
    }
  }
  
  /**
   * Return the raw value of the attribute.
   */
  public String getAttribute(String name)
  {
    String value = m_attrs.getValue(name);  
    return value;
  }

  /**
   * Return the value of the attribute interpreted as an Attribute 
   * Value Template (in other words, you can use curly expressions 
   * such as href="http://{website}".
   */
  public String getAttribute(String name, Node sourceNode, XSLTEngineImpl processor)
    throws SAXException
  {
    if(null != m_avts)
    {
      int nAttrs = m_avts.size();
      for(int i = (nAttrs-1); i >= 0; i--)
      {
        AVT avt = (AVT)m_avts.elementAt(i);
        if(avt.m_name.equals(name))
        {
          XPathSupport execContext = processor.getXMLProcessorLiaison();
          return avt.evaluate(execContext, sourceNode, this,
                              new StringBuffer());        
        }
      } // end for
    }
    return null;  
  }

}
