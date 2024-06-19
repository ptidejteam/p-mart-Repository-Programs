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
import org.apache.xalan.xslt.trace.SelectionEvent;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:variable.
 */
public class ElemVariable extends ElemTemplateElement
{
  public XPath m_selectPattern = null;
  public QName m_qname = null;
  public boolean m_isTopLevel = false;
  // public transient XObject m_value = null;
  // public transient Node m_varContext = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_VARIABLE;
  }
  
  /**
   * Copy constructor.
   */
  public ElemVariable (ElemVariable param)
    throws SAXException
  {
    super(null, 
          param.m_stylesheet, param.m_elemName, 
          null, param.m_lineNumber, param.m_columnNumber);
    m_selectPattern = param.m_selectPattern;
    m_qname = param.m_qname;
    m_isTopLevel = param.m_isTopLevel;
    // m_value = param.m_value;
    // m_varContext = param.m_varContext;
  }
  
  public ElemVariable (XSLTEngineImpl processor,
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
      int tok = getAttrTok(aname);
      switch(tok)
      {
      case Constants.TATTRNAME_SELECT:
        m_selectPattern = m_stylesheet.createXPath(atts.getValue(i), this);
        break;
      case Constants.TATTRNAME_NAME:
        m_qname = new QName(atts.getValue(i), stylesheetTree.m_namespaces);
        break;
      case Constants.TATTRNAME_XMLSPACE:
        processSpaceAttr(atts, i);
        break;
      default:
        if(!isAttrOK(aname, atts, i))
        {
          processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
        }
      }
    }
    if(null == m_qname)
      processor.error(XSLTErrorResources.ER_NO_NAME_ATTRIB, new Object[] {name}); //name+" must have a 'name' attribute.");
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
    super.execute(processor, sourceTree, sourceNode, mode);
    // System.out.println("Calling getValue for variable named: "+m_qname.m_localpart);
    XObject var = getValue(processor, sourceTree, sourceNode);
    processor.getVarStack().pushVariable(m_qname, var);
  }
  
  /**
   * Get the XObject representation of the variable.
   */
  public XObject getValue(XSLTEngineImpl processor, 
                     Node sourceTree, 
                     Node sourceNode)
    throws XSLProcessorException, 
           java.net.MalformedURLException, 
           java.io.FileNotFoundException, 
           java.io.IOException,
           SAXException
  {
    XObject var;
    if(null != m_selectPattern)
    {
      XPathSupport execContext = processor.getXMLProcessorLiaison();
      var = m_selectPattern.execute(execContext, sourceNode, this);
      if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
      {
        m_stylesheet.m_stylesheetRoot.fireSelectedEvent(new SelectionEvent(processor, 
                                                                           sourceNode,
                                                                           this, 
                                                                           "select",
                                                                           m_selectPattern,
                                                                           var));
      }
    }
    else
    {
      // Use result tree fragment
      DocumentFragment df = processor.createResultTreeFrag(m_stylesheet, this, 
                                                           sourceTree, 
                                                           sourceNode, null);
      var = new XRTreeFrag(df);
    }
    return var;
  }
}
