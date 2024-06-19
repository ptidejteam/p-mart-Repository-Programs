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
import org.apache.xalan.xpath.xml.QName;

/**
 * <meta name="usage" content="advanced"/>
 * Implement a text literal.
 */
public class ElemTextLiteral extends ElemTemplateElement
{
  public boolean m_isCData;
  public boolean m_preserveSpace;
  public char m_ch[];
  
  /**
   * Tells if this element should disable escaping.
   */
  boolean m_disableOutputEscaping = false;

  public int getXSLToken()
  {
    return Constants.ELEMNAME_TEXTLITERALRESULT;
  }
  ElemTextLiteral(XSLTEngineImpl processor,
                  Stylesheet stylesheetTree,
                  char ch[], int start, int length, 
                  boolean isCData, boolean preserveSpace,
                  boolean disableOutputEscaping,
                  int lineNumber, int columnNumber)
    throws SAXException
  {
    super(processor, stylesheetTree, "#text", null, lineNumber, columnNumber);

	// TODO: These tend to be small strings, so we might store these all in one big buffer, keeping track of
	// the offset and length for each string.

    m_ch = new char[length];
	
	System.arraycopy(ch,start,m_ch,0,length);

    m_isCData = isCData;
    m_preserveSpace = preserveSpace;
    m_disableOutputEscaping = disableOutputEscaping;
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
    if(!m_disableOutputEscaping)
    {
      processor.m_resultTreeHandler.characters(m_ch, 0, m_ch.length);
    }
    else
    {
      processor.m_resultTreeHandler.charactersRaw(m_ch, 0, m_ch.length);
    }
  }
}
