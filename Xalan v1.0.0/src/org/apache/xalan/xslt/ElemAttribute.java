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
import org.apache.xalan.xpath.xml.XSLMessages;

/**
 * <meta name="usage" content="advanced"/>
 * Implement xsl:attribute.
 */
public class ElemAttribute extends ElemTemplateElement
{
  public AVT m_name_avt = null;
  public AVT m_namespace_avt = null;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_ATTRIBUTE;
  }
  
  ElemAttribute(XSLTEngineImpl processor,
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
      else if(!(isAttrOK(aname, atts, i) || 
                processSpaceAttr(aname, atts, i)))
      {
        processor.error(XSLTErrorResources.ER_ILLEGAL_ATTRIBUTE, new Object[] {name, aname}); //name+" has an illegal attribute: "+aname);
      }
    }
    if(null == m_name_avt)
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
    XPathSupport execContext = processor.getXMLProcessorLiaison();
    String attrName = m_name_avt.evaluate(execContext, sourceNode, this,
                                          new StringBuffer());
    String origAttrName = attrName;      // save original attribute name
    int indexOfNSSep = 0;
    if(null != attrName)
    {
      String attrNameSpace = null;
      if(null != m_namespace_avt)
      {
        attrNameSpace = m_namespace_avt.evaluate(execContext, sourceNode, this,
                                                        new StringBuffer());
        if(null != attrNameSpace && attrNameSpace.length()>0)
        {
          String prefix = processor.getResultPrefixForNamespace(attrNameSpace);
          if(null == prefix)
          {
            prefix = "ns"+String.valueOf(processor.m_uniqueNSValue);
            processor.m_uniqueNSValue++;
            String nsDecl = "xmlns:"+prefix;
            
            // Not sure this should go through the aliasing comment.
            //addResultAttribute(processor.m_resultNameSpaces,
            processor.addResultAttribute(
                               processor.m_pendingAttributes, 
                               nsDecl, attrNameSpace);
          }
          indexOfNSSep = origAttrName.indexOf(':');
          if(indexOfNSSep >= 0)          
            attrName = attrName.substring(indexOfNSSep+1);
          attrName = (prefix + ":"+attrName);       // add prefix to attribute name
        }
      }
      // Note we are using original attribute name for these tests. 
      else if(null != processor.m_pendingElementName && !origAttrName.equals("xmlns"))
      {
        // make sure that if a prefix is specified on the attribute name, it is valid
        indexOfNSSep = origAttrName.indexOf(':');
        if(indexOfNSSep >= 0)
        {
          String nsprefix = origAttrName.substring(0, indexOfNSSep);
          // Catch the exception this may cause. We don't want to stop processing.
          try{
            attrNameSpace = getNamespaceForPrefix(nsprefix);
          }
          catch(Exception ex) 
          {
            // Could not resolve prefix
            attrNameSpace = null;
            processor.warn(XSLTErrorResources.WG_COULD_NOT_RESOLVE_PREFIX, new Object[]{nsprefix});            
          }
        }
      }
      else
      {
        processor.warn(XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_NAME, new Object[]{origAttrName}); 
        // warn(templateChild, sourceNode, "Trying to add attribute after element child has been added, ignoring...");
      }
      if (indexOfNSSep<0 || attrNameSpace != null)
      {  
        String val = childrenToString(processor, sourceTree, sourceNode, mode);
        
        addResultAttribute(processor.m_resultNameSpaces, 
                           processor.m_pendingAttributes, 
                           attrName, val);
      }  
      
      
    }
  }
  
  /**
   * Add a child to the child list.
   * <!ELEMENT xsl:attribute %char-template;>
   * <!ATTLIST xsl:attribute 
   *   name %avt; #REQUIRED
   *   namespace %avt; #IMPLIED
   *   %space-att;
   * >
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
