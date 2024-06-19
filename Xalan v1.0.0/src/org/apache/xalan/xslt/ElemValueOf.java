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
import org.apache.xalan.xslt.trace.SelectionEvent;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:value-of.
 */
public class ElemValueOf extends ElemTemplateElement
{
  /**
   * The select pattern used to locate the value.
   */
  public XPath m_selectPattern = null;
  boolean isDot = false;

  /**
   * Tells if this element should disable escaping.
   */
  boolean m_disableOutputEscaping = false;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_VALUEOF;
  }
  
  public ElemValueOf (XSLTEngineImpl processor, // MAY BE NULL!!
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
        {
          String val = atts.getValue(i);
          isDot = val.equals(".");
          m_selectPattern 
            = m_stylesheet.createXPath(val, this);
        }
        break;
      case Constants.TATTRNAME_DISABLE_OUTPUT_ESCAPING:
        m_disableOutputEscaping = m_stylesheet.getYesOrNo(aname, atts.getValue(i));
        break;
      case Constants.TATTRNAME_XMLSPACE:
        processSpaceAttr(atts, i);
        break;
      default:
        if(!isAttrOK(aname, atts, i))
        {
          if(null != processor)
            processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
        }
      }
    }
    if((null == m_selectPattern) && (null != processor))
    {
      processor.error(XSLTErrorResources.ER_NEED_SELECT_ATTRIB, new Object[] {name}); //name+" requires a select attribute.");
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
    if(isDot)
    {
      String s;
      
      int t = sourceNode.getNodeType();
      if(((Node.COMMENT_NODE ==t) || 
          (Node.PROCESSING_INSTRUCTION_NODE == t)))
      {
        s = sourceNode.getNodeValue();
      }
      else
      {
        s = org.apache.xalan.xpath.xml.XMLParserLiaisonDefault.getNodeData(sourceNode);
      }

      if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
      {
        m_stylesheet.m_stylesheetRoot.fireSelectedEvent(new SelectionEvent(processor, 
                                                                           sourceNode,
                                                                           this, 
                                                                           "select",
                                                                           m_selectPattern,
                                                                           new XString(s)));
      }
      
      if(null != s)
      {
        int len = s.length();
        if(len > 0)
        {
          if(!m_disableOutputEscaping)
          {
            processor.m_resultTreeHandler.characters(s.toCharArray(), 0, s.length());
          }
          else
          {
            processor.m_resultTreeHandler.charactersRaw(s.toCharArray(), 0, s.length());
          }
        }
      }

    }
    else
    {
      XObject value = m_selectPattern.execute(processor.getExecContext(), sourceNode, this,
                                              null, null, true);
      
      if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
      {
        m_stylesheet.m_stylesheetRoot.fireSelectedEvent(new SelectionEvent(processor, 
                                                                           sourceNode,
                                                                           this, 
                                                                           "select",
                                                                           m_selectPattern,
                                                                           value));
      }
      if(null != value)
      {
        int type = value.getType();
        if(XObject.CLASS_NULL != type)
        {
          String s = value.str();
          if(null != s)
          {
            int len = s.length();
            if(len > 0)
            {
              if(!m_disableOutputEscaping)
              {
                processor.m_resultTreeHandler.characters(s.toCharArray(), 0, s.length());
              }
              else
              {
                processor.m_resultTreeHandler.charactersRaw(s.toCharArray(), 0, s.length());
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * Add a child to the child list.
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {((ElemTemplateElement)newChild).m_elemName, this.m_elemName}); //"Can not add " +((ElemTemplateElement)newChild).m_elemName +
    //" to " + this.m_elemName);
    return null;
  }

}
