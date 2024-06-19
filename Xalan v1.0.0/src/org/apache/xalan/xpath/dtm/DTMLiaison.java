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
package org.apache.xalan.xpath.dtm;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.*;
import org.apache.xerces.framework.*;
import org.xml.sax.*;
import org.w3c.dom.*;

import org.apache.xalan.xpath.xdom.XercesLiaison;
import org.apache.xalan.xpath.XPathEnvSupport;
import org.apache.xalan.xpath.XPathFactory;
import org.apache.xalan.xpath.XLocator;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.xml.XSLMessages;

/**
 * <meta name="usage" content="internal"/>
 * Liaison to Document Table Model (DTM) XML parser -- the default liaison and parser that XSLTProcessor
 * uses to perform transformations.
 * To enhance performance, DTM uses integer arrays to represent a DOM. If you are reading or writing a
 * DOM, use XercesLiaison.
 *
 * @see org.apache.xalan.xslt.XSLTProcessor
 * @see org.apache.xalan.xpath.xdom.XercesLiaison
 */
public class DTMLiaison extends XercesLiaison
{
  /**
   * Flag to tell whether or not the parse is done on a seperate thread,
   * so the transform can occur at the same time.  The default
   * is true.
   */
  private boolean m_doThreading = true;

  /**
   * Set whether or not the parse is done on a seperate thread,
   * so the transform can occur at the same time.  The default
   * is true.
   */
  boolean getDoThreading()
  {
    return m_doThreading;
  }

  /**
   * Set whether or not the parse is done on a seperate thread,
   * so the transform can occur at the same time.  The default
   * is true.
   */
  void setDoThreading(boolean b)
  {
    m_doThreading = b;
  }

  /**
   * Constructor that takes SAX ErrorHandler as an argument. The error handler
   * is registered with the XML Parser. Any XML-related errors will be reported
   * to the calling application using this error handler.
   *
   * @param	errorHandler SAX ErrorHandler instance.
   */
  public DTMLiaison(org.xml.sax.ErrorHandler errorHandler)
  {
    super(errorHandler);
  }

  /**
   * Construct an instance.
   */
  public DTMLiaison(XPathEnvSupport envSupport)
  {
    super(envSupport);
  }

  /**
   * Construct an instance.
   */
  public DTMLiaison()
  {
  }

  /**
   * Set whether or not to expand all entity references in the
   * source and style trees.
   * Not supported for DTM. Entities will be expanded by default.
   */
  public void setShouldExpandEntityRefs(boolean b)
  {
    if(!b)
      warn(XPATHErrorResources.WG_EXPAND_ENTITIES_NOT_SUPPORTED);
    m_shouldExpandEntityRefs = b;
  }


  /**
   * Check node to see if it matches this liaison.
   */
  public void checkNode(Node node)
    throws SAXException
  {
    if(!(node instanceof DTMProxy))
      throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_DTM_CANNOT_HANDLE_NODES, new Object[]{((Object)node).getClass()})); //"DTMLiaison can not handle nodes of type"
        //+((Object)node).getClass());
  }

  /**
   * Parse an XML document.
   *
   * <p>The application can use this method to instruct the SAX parser
   * to begin parsing an XML document from any valid input
   * source (a character stream, a byte stream, or a URI).</p>
   *
   * <p>Applications may not invoke this method while a parse is in
   * progress (they should create a new Parser instead for each
   * additional XML document).  Once a parse is complete, an
   * application may reuse the same Parser object, possibly with a
   * different input source.</p>
   *
   * @param source The input source for the top-level of the
   *        XML document.
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @exception java.io.IOException An IO exception from the parser,
   *            possibly from a byte stream or character stream
   *            supplied by the application.
   * @see org.xml.sax.InputSource
   * @see #parse(java.lang.String)
   * @see #setEntityResolver
   * @see #setDTDHandler
   * @see #setDocumentHandler
   * @see #setErrorHandler
   */
  public void parse (InputSource source)
    throws SAXException, IOException
  {
    if(null == m_docHandler)
    {
      DTM domParser = new DTM(this.getProblemListener());
      DTM parser = domParser;
      Thread parseThread = null;
      {
        if(null != m_errorHandler)
        {
          parser.setErrorHandler(m_errorHandler);
        }
        else
        {
          String ident = (null == source.getSystemId())
                         ? "Input XSL" : source.getSystemId();
          parser.setErrorHandler(new org.apache.xalan.xpath.xml.DefaultErrorHandler(ident));
        }

        if(null != m_entityResolver)
        {
          // System.out.println("Setting the entity resolver.");
          parser.setEntityResolver(m_entityResolver);
        }

        if(null != m_locale)
          parser.setLocale(m_locale);

        if(getUseValidation())
          parser.setFeature("http://xml.org/sax/features/validation", true);

        // Set whether or not to create entity ref nodes
        domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", getShouldExpandEntityRefs());

        if(m_doThreading)
        {
          parser.parse(source);
        }
        else
        {
          parser.setInputSource(source);
          parseThread = new Thread(parser);
          try
          {
            parseThread.start();
          }
          catch(RuntimeException re)
          {
            throw new SAXException(re.getMessage());
          }
        }

        m_document = parser.getDocument();
        if(null != source.getSystemId())
        {
          if(null != getSourceDocsTable())
            getSourceDocsTable().put(source.getSystemId(), m_document);
        }
      }
    }
    else
    {
      super.parse(source);
    }

  }

  /**
   * Returns true if the liaison supports the SAX DocumentHandler
   * interface.  The default is that the parser does not support
   * the SAX interface.
   */
  public boolean supportsSAX()
  {
    return true;
  }

  /**
   * Returns the namespace of the given node.
   */
  public String getNamespaceOfNode(Node n)
  {
    try
    {
      return ((org.apache.xalan.xpath.dtm.DTMProxy)n).getNamespaceURI();
    }
    catch(ClassCastException cce)
    {
      return super.getNamespaceOfNode(n);
    }
  }

  /**
   * Returns the local name of the given node.
   */
  public String getLocalNameOfNode(Node n)
  {
    try
    {
      return ((org.apache.xalan.xpath.dtm.DTMProxy)n).getLocalName();
    }
    catch(ClassCastException cce)
    {
      return super.getLocalNameOfNode(n);
    }
  }

  /**
   * Get the parent of a node.
   */
  public Node getParentOfNode(Node n)
    throws RuntimeException
  {
    try
    {
      return ((org.apache.xalan.xpath.dtm.DTMProxy)n).getOwnerNode();
    }
    catch(ClassCastException cce)
    {
      return super.getParentOfNode(n);
    }
  }

  /**
   * Given an ID, return the element.
   */
  public Element getElementByID(String id, Document doc)
  {
    try
    {
      return ((DTMProxy)doc).getDTM().getIdentifier(id);
    }
    catch(ClassCastException cce)
    {
      return super.getElementByID(id, doc);
    }
  }

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   * (Should this go here or in the XLocator??)
   * Since it states in the DOM draft: "An XML processor may choose to
   * completely expand entities before the structure model is passed
   * to the DOM; in this case, there will be no EntityReferences in the DOM tree."
   * So I'm not sure how well this is going to work.
   */
  public String getUnparsedEntityURI(String name, Document doc)
  {
    try
    {
      String url = null;
      DTMProxy docp = (DTMProxy)doc;
      DTM dtm = docp.dtm;
      int nameindex = dtm.getStringPool().addSymbol(name);
      int entityRefIndex = dtm.m_entities.get(nameindex);
      int entityRef[] = {0, 0, 0, 0};
      dtm.m_entityNodes.readSlot(entityRefIndex, entityRef);
      if((entityRef[3] >> 16) != 0)
      {
        url = dtm.getStringPool().toString(entityRef[3] & 0xFF);
        if(null == url)
        {
          url = dtm.getStringPool().toString(entityRef[2]);
        }
        else
        {
          // This should be resolved to an absolute URL, but that's hard
          // to do from here.
        }
      }

      return url;
    }
    catch(ClassCastException cce)
    {
      return super.getUnparsedEntityURI(name, doc);
    }
    /*
    String url = "";
    DocumentType doctype = doc.getDoctype();
    if(null != doctype)
    {
      NamedNodeMap entities = doctype.getEntities();
      Entity entity = (Entity)entities.getNamedItem(name);
      String notationName = entity.getNotationName();
      if(null != notationName) // then it's unparsed
      {
        // The draft says: "The XSLT processor may use the public
        // identifier to generate a URI for the entity instead of the URI
        // specified in the system identifier. If the XSLT processor does
        // not use the public identifier to generate the URI, it must use
        // the system identifier; if the system identifier is a relative
        // URI, it must be resolved into an absolute URI using the URI of
        // the resource containing the entity declaration as the base
        // URI [RFC2396]."
        // So I'm falling a bit short here.
        url = entity.getSystemId();
        if(null == url)
        {
          url = entity.getPublicId();
        }
        else
        {
          // This should be resolved to an absolute URL, but that's hard
          // to do from here.
        }
      }
    }
    return url;
    */
  }

  /**
   * Get a factory to create XPaths.
   */
  public XPathFactory getDefaultXPathFactory()
  {
    return DTMNodeLocator.factory();
  }

  /**
   * Get an XLocator provider keyed by node.  This get's
   * the association based on the root of the tree that the
   * node is parented by.
   */
  public XLocator getXLocatorFromNode(Node node)
  {
    return DTMNodeLocator.getDefaultLocator();
  }

  /**
   * getXLocatorHandler.
   */
  public XLocator createXLocatorHandler()
  {
    return DTMNodeLocator.getDefaultLocator();
  }

}
