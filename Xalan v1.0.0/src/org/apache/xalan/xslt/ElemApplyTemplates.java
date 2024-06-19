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
import java.util.Vector;
import org.apache.xalan.xslt.trace.TracerEvent;
import org.apache.xalan.xpath.xml.QName;
import org.apache.xalan.xslt.res.XSLTErrorResources;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:apply-templates.
 */
public class ElemApplyTemplates extends ElemForEach
{
  public QName m_mode = null;
  public boolean m_isDefaultTemplate = false;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_APPLY_TEMPLATES;
  }

  public ElemApplyTemplates (XSLTEngineImpl processor, // MAY BE NULL!!
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
        m_selectPattern 
          = stylesheetTree.createXPath(atts.getValue(i), this);
        break;
      case Constants.TATTRNAME_MODE:
        m_mode = new QName( atts.getValue(i), m_stylesheet.m_namespaces);
        break;
      default:
        if(!isAttrOK(aname, atts, i))
        {
          if(null != processor)
            processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
        }
      }
    }
    if(null == m_selectPattern)
    {
      if(true)
      {
        if(null == m_stylesheet.m_defaultATXpath)
        {
          m_stylesheet.m_defaultATXpath 
            = m_stylesheet.createXPath("node()", this);
        }
        m_selectPattern = m_stylesheet.m_defaultATXpath;
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
	processor.m_currentTemplateRuleIsNull.push(false);

	try
	{

	    if(null != m_stylesheet.m_stylesheetRoot.m_traceListeners)
	    {
	      m_stylesheet.m_stylesheetRoot.fireTraceEvent(new TracerEvent(processor, 
	                                                                   sourceTree,
	                                                                   sourceNode,
	                                                                   mode,
	                                                                   this));
	    }
	    if(null != sourceNode)
	    {      
	      boolean needToTurnOffInfiniteLoopCheck = false;
	      
	      if(!m_isDefaultTemplate)
	      {
	        mode = m_mode;
	      }

        // Dragons here.  Push the params & stack frame, but then 
        // execute the select statement inside transformSelectedChildren, 
        // which must be executed in the stack frame before the 
        // new stack frame.  Because of depth-first searching, this 
        // gets worse.
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
          transformSelectedChildren(m_stylesheet, 
                                    this,
                                    null, 
                                    sourceTree, 
                                    sourceNode, mode,
                                    m_selectPattern, 
                                    Constants.ELEMNAME_APPLY_TEMPLATES,
                                    processor,
                                    selectStackFrameIndex);
        }
        finally
        {
          if(true == needToTurnOffInfiniteLoopCheck)
          {
            processor.m_needToCheckForInfiniteLoops = false;
          }
          vars.popCurrentContext();
          vars.setCurrentStackFrameIndex(selectStackFrameIndex);
        }
	    }
	    else // if(null == sourceNode)
	    {
	      processor.error(XSLTErrorResources.ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES);//"sourceNode is null in handleApplyTemplatesInstruction!");
	    }
	}

	finally
	{
		processor.m_currentTemplateRuleIsNull.pop();
	}

  }
  
  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
   * <!ATTLIST xsl:apply-templates
   *   select %expr; "node()"
   *   mode %qname; #IMPLIED
   * >
   */
  public Node               appendChild(Node newChild)
    throws DOMException
  {
    int type = ((ElemTemplateElement)newChild).getXSLToken();
    switch(type)
    {
      // char-instructions 
    case Constants.ELEMNAME_SORT:
    case Constants.ELEMNAME_WITHPARAM:
      break;
      
    default:
      error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {((ElemTemplateElement)newChild).m_elemName, this.m_elemName}); //"Can not add " +((ElemTemplateElement)newChild).m_elemName +
      // " to " + this.m_elemName);
    }
    return super.appendChild(newChild);
  }
  
}
