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
import org.xml.sax.*;
import org.w3c.dom.*;
import java.util.*;
import org.xml.sax.ext.LexicalHandler;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="general"/>
 * This class takes SAX events (in addition to some extra events 
 * that SAX doesn't handle yet) and adds the result to a document 
 * or document fragment.
 */
public class FormatterToDOM implements DocumentHandler, LexicalHandler
{
  Document m_doc;
  Node m_currentNode = null;
  public DocumentFragment m_docFrag = null;
  Stack m_elemStack = new Stack();
    
  /**
   * FormatterToDOM instance constructor... it will add the DOM nodes 
   * to the document fragment.
   */
  public FormatterToDOM(Document doc, Element elem)
  {
    m_doc = doc;
    m_currentNode = elem;
  }

  /**
   * FormatterToDOM instance constructor... it will add the DOM nodes 
   * to the document fragment.
   */
  public FormatterToDOM(Document doc, DocumentFragment docFrag)
  {
    m_doc = doc;
    m_docFrag = docFrag;
  }

  /**
   * FormatterToDOM instance constructor... it will add the DOM nodes 
   * to the document.
   */
  public FormatterToDOM(Document doc)
  {
    m_doc = doc;
  }
  
  /**
   * Get the root node of the DOM being created.  This 
   * is either a Document or a DocumentFragment.
   */
  public Node getRootNode()
  {
    return (null != m_docFrag) ? (Node)m_docFrag : (Node)m_doc;
  }
  
  /**
   * Return null since there is no Writer for this class.
   */
  public java.io.Writer getWriter()
  {
    return null;
  }

  /**
   * Append a node to the current container.
   */
  private void append(Node newNode)
    throws SAXException
  {
    if(null != m_currentNode)
    {
      m_currentNode.appendChild(newNode);
      // System.out.println(newNode.getNodeName());
    }
    else if(null != m_docFrag)
    {
      m_docFrag.appendChild(newNode);
    }
    else
    {
      boolean ok = true;
      int type = newNode.getNodeType();
      if(type == Node.TEXT_NODE)
      {
        String data = newNode.getNodeValue();
        if((null != data) && (data.trim().length() > 0))
        {
          throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_OUTPUT_TEXT_BEFORE_DOC, null)); //"Warning: can't output text before document element!  Ignoring...");
        }
        ok = false;
      }
      else if(type == Node.ELEMENT_NODE)
      {
        if(m_doc.getDocumentElement() != null)
        {
          throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CANT_HAVE_MORE_THAN_ONE_ROOT, null)); //"Can't have more than one root on a DOM!");
        }
      }
      if(ok)
        m_doc.appendChild(newNode);
    }
  }
  
  /**
   * Receive an object for locating the origin of SAX document events.
   *
   * <p>SAX parsers are strongly encouraged (though not absolutely
   * required) to supply a locator: if it does so, it must supply
   * the locator to the application by invoking this method before
   * invoking any of the other methods in the DocumentHandler
   * interface.</p>
   *
   * <p>The locator allows the application to determine the end
   * position of any document-related event, even if the parser is
   * not reporting an error.  Typically, the application will
   * use this information for reporting its own errors (such as
   * character content that does not match an application's
   * business rules).  The information returned by the locator
   * is probably not sufficient for use with a search engine.</p>
   *
   * <p>Note that the locator will return correct information only
   * during the invocation of the events in this interface.  The
   * application should not attempt to use it at any other time.</p>
   *
   * @param locator An object that can return the location of
   *                any SAX document event.
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator (Locator locator)
  {
    // No action for the moment.
  }

  /**
   * Receive notification of the beginning of a document.
   *
   * <p>The SAX parser will invoke this method only once, before any
   * other methods in this interface or in DTDHandler (except for
   * setDocumentLocator).</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void startDocument ()
    throws SAXException
  {
    // No action for the moment.
  }


  /**
   * Receive notification of the end of a document.
   *
   * <p>The SAX parser will invoke this method only once, and it will
   * be the last method invoked during the parse.  The parser shall
   * not invoke this method until it has either abandoned parsing
   * (because of an unrecoverable error) or reached the end of
   * input.</p>
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endDocument ()
    throws SAXException
  {
    // No action for the moment.
  }


  /**
   * Receive notification of the beginning of an element.
   *
   * <p>The Parser will invoke this method at the beginning of every
   * element in the XML document; there will be a corresponding
   * endElement() event for every startElement() event (even when the
   * element is empty). All of the element's content will be
   * reported, in order, before the corresponding endElement()
   * event.</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached.  Note that the attribute list provided will
   * contain only attributes with explicit values (specified or
   * defaulted): #IMPLIED attributes will be omitted.</p>
   *
   * @param name The element type name.
   * @param atts The attributes attached to the element, if any.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #endElement
   * @see org.xml.sax.AttributeList 
   */
  public void startElement (String name, AttributeList atts)
    throws SAXException
  {
    Element elem = m_doc.createElement(name);
    int nAtts = atts.getLength();
    for(int i = 0; i < nAtts; i++)
    {
      elem.setAttribute(atts.getName(i), atts.getValue(i));
    }
    append(elem);
    m_elemStack.push(elem);
    m_currentNode = elem;
  }
  
  /**
   * Receive notification of the end of an element.
   *
   * <p>The SAX parser will invoke this method at the end of every
   * element in the XML document; there will be a corresponding
   * startElement() event for every endElement() event (even when the
   * element is empty).</p>
   *
   * <p>If the element name has a namespace prefix, the prefix will
   * still be attached to the name.</p>
   *
   * @param name The element type name
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void endElement (String name)
    throws SAXException
  {
    m_elemStack.pop();
    if(!m_elemStack.isEmpty())
    {
      m_currentNode = (Element)m_elemStack.peek();
    }
    else
    {
      m_currentNode = null;
    }
  }

  /**
   * Receive notification of character data.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace 
   * @see org.xml.sax.Locator
   */
  public void characters (char ch[], int start, int length)
    throws SAXException
  {
    if(m_inCData)
    {
      cdata (ch, start, length);
      return;
    }

    String s = new String(ch, start, length);
    Text text = m_doc.createTextNode(s);
    append(text);
  }

  /**
   * If available, when the disable-output-escaping attribute is used, 
   * output raw text without escaping.  A PI will be inserted in front 
   * of the node with the name "lotusxsl-next-is-raw" and a value of
   * "formatter-to-dom".
   */
  public void charactersRaw (char ch[], int start, int length)
    throws SAXException
  {
    String s = new String(ch, start, length);
    append(m_doc.createProcessingInstruction("xslt-next-is-raw", "formatter-to-dom"));
    append(m_doc.createTextNode(s));
  }
  
  /**
   * Report the beginning of an entity.
   *
   * The start and end of the document entity are not reported.
   * The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @exception SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.misc.DeclHandler#internalEntityDecl
   * @see org.xml.sax.misc.DeclHandler#externalEntityDecl
   */
  public void startEntity (String name)
    throws SAXException
  {
    // Almost certainly the wrong behavior...
    // entityReference(name);
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @exception SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity (String name)
    throws SAXException
  {
  }

  /**
   * Receive notivication of a entityReference.
   */
  public void entityReference(String name)
    throws SAXException
  {
    append(m_doc.createEntityReference(name));
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>Validating Parsers must use this method to report each chunk
   * of ignorable whitespace (see the W3C XML 1.0 recommendation,
   * section 2.10): non-validating parsers may also use this method
   * if they are capable of parsing and using content models.</p>
   *
   * <p>SAX parsers may return all contiguous whitespace in a single
   * chunk, or they may split it into several chunks; however, all of
   * the characters in any single event must come from the same
   * external entity, so that the Locator provides useful
   * information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #characters
   */
  public void ignorableWhitespace (char ch[], int start, int length)
    throws SAXException
  {
    String s = new String(ch, start, length);
    append(m_doc.createTextNode(s));
  }


  /**
   * Receive notification of a processing instruction.
   *
   * <p>The Parser will invoke this method once for each processing
   * instruction found: note that processing instructions may occur
   * before or after the main document element.</p>
   *
   * <p>A SAX parser should never report an XML declaration (XML 1.0,
   * section 2.8) or a text declaration (XML 1.0, section 4.3.1)
   * using this method.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *        none was supplied.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void processingInstruction (String target, String data)
    throws SAXException
  {
    append(m_doc.createProcessingInstruction(target, data));
 }
  
  /**
   * Report an XML comment anywhere in the document.
   *
   * This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @exception SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length) throws SAXException
  {
    append( m_doc.createComment(new String(ch, start, length)) );
  }  
  
  protected boolean m_inCData = false;
  
  /**
   * Report the start of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA ()
    throws SAXException
  {
    m_inCData = true;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA ()
    throws SAXException
  {
    m_inCData = false;
  }
  
  /**
   * Receive notification of cdata.
   *
   * <p>The Parser will call this method to report each chunk of
   * character data.  SAX parsers may return all contiguous character
   * data in a single chunk, or they may split it into several
   * chunks; however, all of the characters in any single event
   * must come from the same external entity, so that the Locator
   * provides useful information.</p>
   *
   * <p>The application must not attempt to read from the array
   * outside of the specified range.</p>
   *
   * <p>Note that some parsers will report whitespace using the
   * ignorableWhitespace() method rather than this one (validating
   * parsers must do so).</p>
   *
   * @param ch The characters from the XML document.
   * @param start The start position in the array.
   * @param length The number of characters to read from the array.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace 
   * @see org.xml.sax.Locator
   */
  public void cdata (char ch[], int start, int length)
    throws SAXException
  {
    String s = new String(ch, start, length);
    append(m_doc.createCDATASection(s));
  }
  
  /**
   * Report the start of DTD declarations, if any.
   *
   * Any declarations are assumed to be in the internal subset
   * unless otherwise indicated.
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @exception SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD (String name, String publicId,
                                 String systemId)
    throws SAXException
  {
    // Do nothing for now.
  }

  /**
   * Report the end of DTD declarations.
   *
   * @exception SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD ()
    throws SAXException
  {
    // Do nothing for now.
  }


}
