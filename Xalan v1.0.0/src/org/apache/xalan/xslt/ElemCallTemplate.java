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
 * Implement xsl:call-template.
 */
public class ElemCallTemplate extends ElemTemplateElement
{
  public QName m_templateName = null;
  ElemTemplateElement m_template = null;
    
  public int getXSLToken()
  {
    return Constants.ELEMNAME_CALLTEMPLATE;
  }

  ElemCallTemplate(XSLTEngineImpl processor,
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
        m_templateName = new QName(atts.getValue(i), m_stylesheet.m_namespaces);        
      }
      else if(!isAttrOK(aname, atts, i))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE,new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if (null == m_templateName)
    {
      if(null != processor)
        processor.error(XSLTErrorResources.ER_NO_NAME_ATTRIB, new Object[] {name}); //name+" requires a name attribute.");
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
    super.execute(processor, sourceTree, sourceNode, mode);
    XPathSupport execContext = processor.getXMLProcessorLiaison();
    if(null == m_template)
    {
      m_template 
        = processor.m_stylesheetRoot.findNamedTemplate(m_templateName);
    }
    if(null != m_template)
    {
      VariableStack vars = processor.getVarStack();
      int selectStackFrameIndex = vars.getCurrentStackFrameIndex();
      
      vars.pushContextMarker();
      vars.setCurrentStackFrameIndex(selectStackFrameIndex);
      vars.pushParams(processor,
                      m_stylesheet, 
                      this, 
                      sourceTree, 
                      sourceNode, mode);
      vars.setCurrentStackFrameIndex(vars.size());
      try
      {        
        // template.executeChildren(processor, sourceTree, sourceNode, mode);
        m_template.execute(processor, sourceTree, sourceNode, mode);
      }
      finally
      {
        vars.popCurrentContext();
        vars.setCurrentStackFrameIndex(selectStackFrameIndex);
      }
    }
    else
    {
      processor.error(XSLTErrorResources.ER_TEMPLATE_NOT_FOUND, new Object[] {m_templateName}); //"Could not find template named: '"+templateName+"'");
    }
  }
  
  /**
   * Add a child to the child list.
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    int type = ((ElemTemplateElement)newChild).getXSLToken();
    switch(type)
    {
    case Constants.ELEMNAME_WITHPARAM:
      break;
      
    default:
      error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {((ElemTemplateElement)newChild).m_elemName, this.m_elemName}); //"Can not add " +((ElemTemplateElement)newChild).m_elemName +
            //" to " + this.m_elemName);
    }
    return super.appendChild(newChild);
  }

}
