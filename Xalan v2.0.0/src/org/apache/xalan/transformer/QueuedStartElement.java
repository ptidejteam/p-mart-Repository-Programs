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
package org.apache.xalan.transformer;

import java.util.Vector;

import org.xml.sax.ContentHandler;
import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

import org.apache.xml.utils.MutableAttrListImpl;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xml.utils.NameSpace;

/**
 * Tracks the state of a queued element event.
 */
public class QueuedStartElement extends QueuedSAXEvent
{

  /**
   * Constructor QueuedStartElement
   *
   */
  public QueuedStartElement()
  {
    super(ELEM);
  }

  /**
   * The pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   */
  private MutableAttrListImpl m_attributes = new MutableAttrListImpl();

  /**
   * Flag to try and get the xmlns decls to the attribute list
   * before other attributes are added.
   */
  private boolean m_nsDeclsHaveBeenAdded = false;

  /**
   * The pending element, namespace, and local name.
   */
  private String m_name;

  /** Namespace URL of the element          */
  private String m_url;

  /** Local part of qualified name of the element           */
  private String m_localName;

  /**
   * Set the pending element names.
   *
   * @param ns Namespace URI of the element
   * @param localName Local part of qName of element
   * @param name Name of element
   * @param atts List of attributes of the element 
   */
  void setPending(String ns, String localName, String name, Attributes atts)
  {

    m_name = name;
    m_url = ns;
    m_localName = localName;

    if (null != atts)
      m_attributes.addAttributes(atts);

    setPending(true);
  }

  /**
   * Get the list of pending attributes.
   *
   * @return the list of pending attributes. 
   */
  MutableAttrListImpl getAttrs()
  {
    return m_attributes;
  }

  /**
   * Set an attribute in the pending attributes list.
   *
   * @param uri Namespace URI of attribute
   * @param localName Local part of qname of the attribute 
   * @param qName Qualified name of attribute
   * @param type The attribute type as a string.
   * @param value Attribute value 
   */
  void addAttribute(String uri, String localName, String qName, String type,
                    String value)
  {
    m_attributes.addAttribute(uri, localName, qName, type, value);
  }

  /**
   * Return whether the given element matches this pending element' name 
   *
   *
   * @param ns Namespace URI of given element
   * @param localName Local part of qname of given element 
   *
   * @return True if the given element matches this.
   */
  boolean isElement(String ns, String localName)
  {

    if ((null != m_localName) && m_localName.equals(localName))
    {
      if ((null == ns) && (null == m_url))
        return true;

      if ((null != ns) && (null != m_url))
        return ns.equals(m_url);
    }

    return false;
  }

  /**
   * Get the pending element name.
   *
   * @return the pending element name.
   */
  String getName()
  {
    return m_name;
  }

  /**
   * Get the pending element namespace URI.
   *
   * @return the pending element namespace URI.
   */
  String getURL()
  {
    return m_url;
  }

  /**
   * Get the the local name.
   *
   * @return the pending element local name.
   */
  String getLocalName()
  {
    return m_localName;
  }

  /**
   * Return whether Namespace declarations have been added to 
   * this element
   *
   *
   * @return whether Namespace declarations have been added to 
   * this element
   */
  boolean nsDeclsHaveBeenAdded()
  {
    return m_nsDeclsHaveBeenAdded;
  }

  /**
   * Set whether Namespace declarations have been added to 
   * this element
   *
   *
   * @param b Flag indicating whether Namespace declarations 
   * have been added to this element
   */
  void setNSDeclsHaveBeenAdded(boolean b)
  {
    m_nsDeclsHaveBeenAdded = b;
  }

  /**
   * Reset this pending element
   *
   */
  void reset()
  {

    super.reset();
    m_attributes.clear();

    m_nsDeclsHaveBeenAdded = false;
    m_name = null;
    m_url = null;
    m_localName = null;
    m_namespaces = null;
  }

  /** Vector of namespaces for this element          */
  Vector m_namespaces = null;

  /**
   * Start Prefix mapping for this element
   *
   *
   * @param prefix Prefix to map
   * @param uri Namespace URI for the given prefix
   */
  void startPrefixMapping(String prefix, String uri)
  {

    if (null == m_namespaces)
      m_namespaces = new Vector();

    NameSpace ns = new NameSpace(prefix, uri);

    m_namespaces.addElement(ns);
  }

  /**
   * Flush the event.
   *
   * @throws SAXException
   */
  void flush() throws org.xml.sax.SAXException
  {

    if (isPending)
    {
      if (null != m_name)
      {
        m_contentHandler.startElement(m_url, m_localName, m_name,
                                      m_attributes);
        if(null != m_traceManager)
        {
          fireGenerateEvent(GenerateEvent.EVENTTYPE_STARTELEMENT, m_name,
                            m_attributes);
        }
      }

      reset();

      // super.flush();
    }
  }
}
