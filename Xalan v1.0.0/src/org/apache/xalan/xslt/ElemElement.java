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
import org.apache.xalan.xpath.*;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:decimal-format.
 */
public class ElemElement extends ElemUse
{
  public AVT m_name_avt = null;
  public AVT m_namespace_avt = null;
  private String m_prefix;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_ELEMENT;
  }

  public ElemElement (XSLTEngineImpl processor,
                      Stylesheet stylesheetTree,
                      String name,
                  AttributeList atts,
                  int lineNumber, int columnNumber)
    throws SAXException
  {
    super(processor, stylesheetTree, name, atts, lineNumber, columnNumber);
    int nAttrs = atts.getLength();
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_NAME))
      {
        m_name_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                             this, m_stylesheet, processor);
      }
      else if(aname.equals(Constants.ATTRNAME_NAMESPACE))
      {
        m_namespace_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                                  this, m_stylesheet, processor);
      }
      else if(!(processUseAttributeSets(aname, atts, i) || processSpaceAttr(aname, atts, i) ||
                isAttrOK(aname, atts, i)
                ))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(null == m_name_avt)
    {
      processor.error(XSLTErrorResources.ER_NO_NAME_ATTRIB, new Object[] {name}); //name+" must have a name attrbute.");
    }
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
    XPathSupport execContext = processor.getXMLProcessorLiaison();
    String elemName = m_name_avt.evaluate(execContext, sourceNode, this,
                                          new StringBuffer());
	// make sure that if a prefix is specified on the attribute name, it is valid
    int indexOfNSSep = elemName.indexOf(':');
    String ns ="" ;
    if(indexOfNSSep >= 0)
    {
      String nsprefix = elemName.substring(0, indexOfNSSep);
      // Catch the exception this may cause. We don't want to stop processing.
      try{
        ns = getNamespaceForPrefix(nsprefix);
        // Check if valid QName. Assuming that if the prefix is defined,
        // it is valid.
        if ( indexOfNSSep+1 == elemName.length() ||
           !isValidNCName(elemName.substring(indexOfNSSep + 1)))
        {
          processor.warn(XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME, new Object[]{elemName});
          elemName = null;
        }
      }
      catch(Exception ex)
      {
        // Could not resolve prefix
        ns = null;
        processor.warn(XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX, new Object[]{nsprefix});
      }

    }
    // Check if valid QName
    else if (elemName.length() == 0 || !isValidNCName(elemName))
    {
      processor.warn(XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME, new Object[]{elemName});
      elemName = null;
    }
    // Only do this if name is valid
    if(null != elemName && null != ns)
    {
      if(null != m_namespace_avt)
      {
        String elemNameSpace = m_namespace_avt.evaluate(execContext, sourceNode, this,
                                                        new StringBuffer());
        if(null != elemNameSpace && elemNameSpace.length()>0)
        {
          if (m_prefix == null)
          {
            String prefix = processor.getResultPrefixForNamespace(elemNameSpace);
            if(null == prefix)
            {
              prefix = "ns"+String.valueOf(processor.m_uniqueNSValue);
              processor.m_uniqueNSValue++;
              String nsDecl = "xmlns:"+prefix;
              processor.addResultAttribute(
                               processor.m_pendingAttributes,
                               nsDecl, elemNameSpace);
            }
            m_prefix = prefix;
          }
          if(indexOfNSSep >= 0)
            elemName = elemName.substring(indexOfNSSep+1);
          elemName = (m_prefix + ":"+elemName);
        }
      }

      processor.m_resultTreeHandler.startElement(elemName);
    }
    // Instantiate content of xsl:element. Note that if startElement was not
    // called(ie: if invalid element name, the element's attributes will be
    // excluded because processor.m_pendingElementName will be null.
    {
      super.execute(processor, sourceTree, sourceNode, mode);

      // Handle namespaces(including those on the ancestor chain
      // and stylesheet root declarations).
      processResultNS(processor);

      executeChildren(processor, sourceTree,
                      sourceNode, mode);
    }
    // Now end the element if name was valid
    if(null != elemName && null != ns)
     {
      processor.m_resultTreeHandler.endElement(elemName);
    }
  }

}
