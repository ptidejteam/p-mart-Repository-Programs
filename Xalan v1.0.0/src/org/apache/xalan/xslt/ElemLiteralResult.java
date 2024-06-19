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
import org.apache.xalan.xpath.XPathSupport;
import org.apache.xalan.xpath.xml.StringToStringTable;
import org.apache.xalan.xpath.xml.NameSpace;
import org.apache.xalan.xslt.res.XSLTErrorResources;

import java.io.*;
import java.util.*;


/**
 * <meta name="usage" content="advanced"/>
 * Implement a Literal Result Element.
 */
public class ElemLiteralResult extends ElemUse
{
  public Vector m_avts = null;
  public String m_qname;
  public String m_extensionElementPrefixes[] = null;
  
  /**
   * This is in support of the exclude-result-prefixes 
   * attribute.  It is really needed only at construction 
   * time, and so should probably go somewhere else.
   */
  protected StringToStringTable m_excludeResultPrefixes = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_LITERALRESULT;
  }

  ElemLiteralResult(XSLTEngineImpl processor,
                    Stylesheet stylesheetTree,
                    String name, 
                    AttributeList atts,
                    int lineNumber, int columnNumber)
    throws SAXException
  {
    super(processor, stylesheetTree, name, atts, lineNumber, columnNumber);
    m_qname = name;
    // SerializableAttrListImpl resultAttrs = new SerializableAttrListImpl();
    // m_atts = resultAttrs;
    int nAttrs = atts.getLength();
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      boolean needToProcess = true;
      int indexOfNSSep = aname.indexOf(':');
      String prefix;
      if(indexOfNSSep > 0)
      {
        prefix = aname.substring(0, indexOfNSSep);
        if(!prefix.equals("xmlns"))
        {
          String ns = getNamespaceForPrefix(prefix);
          
          if((null != ns) 
             && ns.equals( m_stylesheet.m_XSLNameSpaceURL ))
          {
            // process xsl:extension-element-prefixes - Stripped from result tree
            String localName = aname.substring(indexOfNSSep+1);
            m_excludeResultPrefixes = processPrefixControl(localName, atts.getValue(i), 
                                                         m_excludeResultPrefixes);
            if(null != m_excludeResultPrefixes)
              needToProcess = false;
            // process xsl:version
            else if (localName.equals(Constants.ATTRNAME_VERSION))
            {
              String versionStr = atts.getValue(i);
              m_stylesheet.m_XSLTVerDeclared = Double.valueOf(versionStr).doubleValue();
            }
          }       
        }
        else
        {
          // don't process namespace decls
          needToProcess = false;
        }
      }
      if(needToProcess)
      {
        boolean _processUseAttributeSets = processUseAttributeSets(aname, atts, i);
        boolean _processSpaceAttr = processSpaceAttr(aname, atts, i);
        // Add xmlns attribute(except xmlns:xsl), xml:space, etc... 
        // Ignore anything with xsl:xxx 
        if(!_processUseAttributeSets && isAttrOK(aname, atts, i))
        {
          if(null == m_avts)
            m_avts = new Vector(nAttrs);
          // resultAttrs.addAttribute(atts.getName(i), atts.getType(i), atts.getValue(i));
          m_avts.addElement(new AVT(aname, atts.getType(i), atts.getValue(i),
                                    this, m_stylesheet, processor));
        }
      }
      removeExcludedPrefixes(m_excludeResultPrefixes);

    }
  }
  
  /**
   * Override superclass's isAttrOK, to flag null namespace elements 
   * as OK, and XSLT namespace as OK.
   *
   * @param attrName Qualified name of attribute.
   * @param atts The attribute list where the element comes from (not used at
   *      this time).
   * @param which The index into the attribute list (not used at this time).
   * @return True if this attribute should not be flagged as an error.
   */
  boolean isAttrOK(String attrName, AttributeList atts, int which)
  {
    boolean isAttrOK = attrName.equals("xmlns") ||
                       attrName.startsWith("xmlns:");
    if(!isAttrOK)
    {
      int indexOfNSSep = attrName.indexOf(':');
      if(indexOfNSSep >= 0)
      {
        String prefix = attrName.substring(0, indexOfNSSep);
        String ns = m_stylesheet.getNamespaceForPrefixFromStack(prefix);
        isAttrOK = (!ns.equals(XSLTEngineImpl.m_XSLNameSpaceURL));
      }
      else
      {
        isAttrOK = true; // null namespace, flag it as normally OK.
      }
    }

    // TODO: Well, process it...
    return isAttrOK;
  }

        
  /**
   * Execute a Literal Result Element.
   */
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
    processor.m_resultTreeHandler.startElement (m_qname);
    // Process any possible attributes from xsl:use-attribute-sets first
    super.execute(processor, sourceTree, sourceNode, mode);
    
    // Process the list of avts next
    if(null != m_avts)
    {
      int nAttrs = m_avts.size();
      for(int i = (nAttrs-1); i >= 0; i--)
      {
        AVT avt = (AVT)m_avts.elementAt(i);
        XPathSupport execContext = processor.getXMLProcessorLiaison();
        String stringedValue = avt.evaluate(execContext, sourceNode, this,
                                            new StringBuffer());        
        
        if(null != stringedValue)
        {
          // Handle if the avt was a namespace definition         
          String nsprefix = null;
          if (avt.m_name.startsWith("xmlns:"))
          {
            // get the namespace prefix 
            nsprefix = avt.m_name.substring(avt.m_name.indexOf(":") + 1);
          }
          // Make sure namespace is not in the excluded list then
          // add to result tree
          if(nsprefix == null ||!(shouldExcludeResultNamespaceNode(this, nsprefix, stringedValue)))
          {            
            //processor.m_pendingAttributes.removeAttribute(avt.m_name);
            processor.m_pendingAttributes.addAttribute(avt.m_name, avt.m_type, 
                                                              stringedValue);
          }
            
        }
      } // end for
    }        
    
    // Handle namespaces(including those on the ancestor chain 
    // and stylesheet root declarations).
    processResultNS(processor);           

    // Now process all the elements in this subtree
    // TODO: Process m_extensionElementPrefixes && m_attributeSetsNames
    executeChildren(processor, sourceTree, sourceNode, mode);
    processor.m_resultTreeHandler.endElement (m_qname);
  }
  
}
