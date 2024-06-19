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
import org.apache.xalan.xslt.res.XSLTErrorResources;
import org.apache.xalan.xpath.xml.QName;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:copy-of.
 */
public class ElemCopyOf extends ElemTemplateElement
{
  public XPath m_selectPattern = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_COPY_OF;
  }

  public ElemCopyOf (XSLTEngineImpl processor,
                      Stylesheet stylesheetTree,
                      String name, 
                  AttributeList atts,
                  int lineNumber, int columnNumber)
    throws SAXException
  {
    super(processor, stylesheetTree, name, atts, lineNumber, columnNumber);
    int nAttrs = atts.getLength();
    if (!(nAttrs > 0))
      processor.error(XSLTErrorResources.ER_NEED_SELECT_ATTRIB, new Object[] {name});
    for(int i = 0; i < nAttrs; i++)
    {
      String aname = atts.getName(i);
      if(aname.equals(Constants.ATTRNAME_SELECT))
      {
        m_selectPattern 
          = m_stylesheet.createXPath(atts.getValue(i), this);
      }
      else if(!isAttrOK(aname, atts, i))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
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
    XObject value = m_selectPattern.execute(execContext, sourceNode, this);
    
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
      String s;
      switch(type)
      {
      case XObject.CLASS_BOOLEAN:
      case XObject.CLASS_NUMBER:
      case XObject.CLASS_STRING:
        s = value.str();
        processor.m_resultTreeHandler.characters(s.toCharArray(), 0, s.length());
        break;
        
      case XObject.CLASS_NODESET:
        // System.out.println(value);
        NodeList nl = value.nodeset();
        int nChildren = nl.getLength();
        
        // Copy the tree.
        org.apache.xalan.xpath.xml.TreeWalker tw 
          = new TreeWalker2Result(processor, this);
        for(int i = 0; i < nChildren; i++)
        {
          Node pos = (Node)nl.item(i);
          int t = pos.getNodeType();
          // If we just copy the whole document, a startDoc and endDoc get 
          // generated, so we need to only walk the child nodes.
          if(t == Node.DOCUMENT_NODE)
          {
            for(Node child = pos.getFirstChild(); child != null; child = child.getNextSibling())
            {
              tw.traverse(child);
            }
          }
          else if(t == Node.ATTRIBUTE_NODE)
          {
            Attr attr = (Attr)pos;
            processor.m_pendingAttributes.addAttribute(attr.getName(), "CDATA", attr.getValue());
          }
          else
          {
            tw.traverse(pos);
          }
        }
        break;
        
      case XObject.CLASS_RTREEFRAG:
        processor.outputResultTreeFragment(value, processor.getExecContext());
        break;
        
      default:
        s = value.str();
        processor.m_resultTreeHandler.characters(s.toCharArray(), 0, s.length());
        break;
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
