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
 * Implement xsl:processing-instruction.
 */
public class ElemPI extends ElemTemplateElement
{
  public AVT m_name_atv = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_PI;
  }

  public ElemPI (XSLTEngineImpl processor,
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
        m_name_atv = new AVT(aname, atts.getType(i), atts.getValue(i),
                             this, m_stylesheet, processor);
      }
      else if(!(isAttrOK(aname, atts, i) || 
                processSpaceAttr(aname, atts, i)))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(null == m_name_atv)
    {
      processor.error(XSLTErrorResources.ER_NO_NAME_ATTRIB, new Object[] {name}); //name+" must have a name attribute.");
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
    String piName = m_name_atv.evaluate(processor.getExecContext(), sourceNode, this, 
                                        new StringBuffer());
    if(piName.equalsIgnoreCase("xml"))
    {
      error(XSLTErrorResources.ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML); //"processing-instruction name can not be 'xml'");
    }
    else if(!isValidNCName(piName))
    {
      error(XSLTErrorResources.ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME, new Object[] {piName}); //"processing-instruction name must be a valid NCName: "+piName);
    }
    
    // Note the content model is:
    // <!ENTITY % instructions "
    // %char-instructions;
    // | xsl:processing-instruction
    // | xsl:comment
    // | xsl:element
    // | xsl:attribute
    // ">
    String data = childrenToString(processor, sourceTree, 
                                   sourceNode, mode);
    processor.m_resultTreeHandler.processingInstruction(piName, data);
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
      // char-instructions 
    case Constants.ELEMNAME_TEXTLITERALRESULT:
    case Constants.ELEMNAME_APPLY_TEMPLATES:
    case Constants.ELEMNAME_APPLY_IMPORTS:
    case Constants.ELEMNAME_CALLTEMPLATE:
    case Constants.ELEMNAME_FOREACH:
    case Constants.ELEMNAME_VALUEOF:
    case Constants.ELEMNAME_COPY_OF:
    case Constants.ELEMNAME_NUMBER:
    case Constants.ELEMNAME_CHOOSE:
    case Constants.ELEMNAME_IF:
    case Constants.ELEMNAME_TEXT:
    case Constants.ELEMNAME_COPY:
    case Constants.ELEMNAME_VARIABLE:
    case Constants.ELEMNAME_MESSAGE:
      
      // instructions 
      // case Constants.ELEMNAME_PI:
      // case Constants.ELEMNAME_COMMENT:
      // case Constants.ELEMNAME_ELEMENT:
      // case Constants.ELEMNAME_ATTRIBUTE:

      break;
      
    default:
      error(XSLTErrorResources.ER_CANNOT_ADD, new Object[] {((ElemTemplateElement)newChild).m_elemName, this.m_elemName}); //"Can not add " +((ElemTemplateElement)newChild).m_elemName +
            //" to " + this.m_elemName);
    }
    return super.appendChild(newChild);
  }

}
