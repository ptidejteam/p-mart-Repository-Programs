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
package org.apache.xalan.xpath.xml;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import java.net.URL;

import org.apache.xalan.xpath.*; // temp


/**
 * <meta name="usage" content="general"/>
 * An implementation of this interface acts as a liaison 
 * between the XSLT processor and the XML parser. It is 
 * needed in order to support features like included files, 
 * and to cover for deficiencies in the DOM. An implementation 
 * of this interface is a requirement for the XSL processor.
 * 
 * While the XPathSupport methods are intended to just support 
 * XPath, the methods in this class are intended to support 
 * XSLT.
 */
public interface XMLParserLiaison extends XPathSupport, Parser
{
  /**
 * <meta name="usage" content="internal"/>
   * Check node to see if it matches this liaison.
   */
  void checkNode(Node node)
    throws SAXException;
  
  /**
   * Reset for new run.
   */
  void reset();
  
  /**
 * <meta name="usage" content="advanced"/>
   * XPath environment support, which the liaison may aggregate back to 
   * in order to implement the XPathEnvSupport interface.
   */
  void setEnvSupport(XPathEnvSupport envSupport);
  
  /**
   * Returns true if the liaison supports the SAX DocumentHandler 
   * interface.
   */
  boolean supportsSAX();
  
  /** 
   * Returns the document just parsed.
   */
  Document getDocument();

  /**
   * Create an empty DOM Document.  Mainly used for creating an 
   * output document.
   */
  Document createDocument();
  
  /**
   * Return the expanded element name.
   */
  String getExpandedElementName(Element elem);

  /**
   * Returns the attribute name with the namespace expanded.
   */
  String getExpandedAttributeName(Attr attr);

  /**
   * Set special characters for attributes that will be escaped.
   * @deprecated
   */
  void setSpecialCharacters(String str);

  /**
   * Get special characters for attributes that will be escaped.
   * @deprecated
   */
  String getSpecialCharacters();
  
  /**
   * Get the amount to indent when indent-result="yes".
   */
  int getIndent();
  
  /**
   * Set the amount to indent when indent-result="yes".
   */
  void setIndent(int i);

  /**
   * Get whether or not to expand all entity references in the 
   * source and style trees.
   */
  boolean getShouldExpandEntityRefs();
  
  /**
   * Set whether or not to expand all entity references in the 
   * source and style trees.
   */
  void setShouldExpandEntityRefs(boolean b);

  /**
   * Get whether or not validation will be performed.  Validation is off by default.
   */
  boolean getUseValidation();
  
  /**
   * If set to true, validation will be performed.  Validation is off by default.
   */
  void setUseValidation(boolean b);
  
  /**
   * Return a string suitible for telling the user what parser is being used.
   */
  String getParserDescription();
  
  /**
   * Get a factory to create XPaths.
   */
  XPathFactory getDefaultXPathFactory();

  /**
   * Take a user string and try and parse XML, and also return 
   * the url.
   * @exception XSLProcessorException thrown if the active ProblemListener and XMLParserLiaison decide 
   * the error condition is severe enough to halt processing.
   */
  URL getURLFromString(String urlString, String base)
    throws SAXException;
  
  /**
   * Set language variant that should be used, passed from servlet HTTP header.
   */
  void setAcceptLanguage(String acceptLanguage);

  /**
   * Get language variant that should be used, passed from servlet HTTP header.
   */
  String getAcceptLanguage();
  
  /**
   * Set the current problem listener.
   */
  void setProblemListener(ProblemListener listener);
  
  /**
   * Get the current problem listener.
   */
  ProblemListener getProblemListener();
  
  /**
   * Copy attributes from another liaison.
   */
  public void copyFromOtherLiaison(XMLParserLiaisonDefault from)
    throws SAXException;

}

