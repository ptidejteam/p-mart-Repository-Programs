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
import org.apache.xalan.xslt.res.XSLTErrorResources;
 
/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:sort.
 */
public class ElemSort extends ElemTemplateElement
{
  public XPath m_selectPattern = null;
  public AVT lang_avt = null;
  public AVT dataType_avt = null;
  public AVT order_avt = null;
  public AVT caseOrder_avt = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_SORT;
  }
  
  public ElemSort (XSLTEngineImpl processor,
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
      if(aname.equals(Constants.ATTRNAME_SELECT))
      {
        m_selectPattern 
          = m_stylesheet.createXPath(atts.getValue(i), this);
      }
      else if(aname.equals(Constants.ATTRNAME_LANG))
      {
        lang_avt = new AVT(aname, atts.getType(i), atts.getValue(i),
                             this, m_stylesheet, processor);
      }
      else if(aname.equals(Constants.ATTRNAME_DATATYPE))
      {
		  String valType = atts.getValue(i); 
		  if (valType.equalsIgnoreCase(Constants.ATTRVAL_DATATYPE_TEXT) || 
			  valType.equalsIgnoreCase(Constants.ATTRVAL_DATATYPE_NUMBER))
			dataType_avt = new AVT(aname, atts.getType(i), valType,
                             this, m_stylesheet, processor);
		  else
			  processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {aname, valType}); 
      }
      else if(aname.equals(Constants.ATTRNAME_ORDER))
      {
        String valOrder = atts.getValue(i); 
		if (valOrder.equalsIgnoreCase(Constants.ATTRVAL_ORDER_ASCENDING) || 
		    valOrder.equalsIgnoreCase(Constants.ATTRVAL_ORDER_DESCENDING))			
		  order_avt = new AVT(aname, atts.getType(i), valOrder,
                             this, m_stylesheet, processor);
		else
			  processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {aname, valOrder});       
      }
      else if(aname.equals(Constants.ATTRNAME_CASEORDER))
      {
		String valCase = atts.getValue(i); 
		if (valCase.equalsIgnoreCase(Constants.ATTRVAL_CASEORDER_UPPER) || 
		    valCase.equalsIgnoreCase(Constants.ATTRVAL_CASEORDER_LOWER))			
		  
			caseOrder_avt = new AVT(aname, atts.getType(i), valCase,
                             this, m_stylesheet, processor);
		else
			  processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE_VALUE, new Object[] {aname, valCase});             
      }
      else if(!isAttrOK(aname, atts, i))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(null == dataType_avt)
    {
      dataType_avt = new AVT(Constants.ATTRNAME_DATATYPE, "CDATA", "text",
                          this, m_stylesheet, processor);
    }
    if(null == order_avt)
    {
      order_avt = new AVT(Constants.ATTRNAME_ORDER, "CDATA", "ascending",
                          this, m_stylesheet, processor);
    }
	if(null == caseOrder_avt)
    {
      caseOrder_avt = new AVT(Constants.ATTRNAME_CASEORDER, "CDATA", Constants.ATTRVAL_CASEORDER_LOWER,
                          this, m_stylesheet, processor);
    }
    if(null == m_selectPattern)
    {
      m_selectPattern = m_stylesheet.createXPath(".", this);
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
