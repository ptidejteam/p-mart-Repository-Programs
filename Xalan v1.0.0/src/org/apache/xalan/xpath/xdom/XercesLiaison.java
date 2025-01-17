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
package org.apache.xalan.xpath.xdom;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.*;
import org.apache.xerces.framework.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import org.apache.xalan.xpath.xml.*;
import org.apache.xalan.xpath.*;
import org.apache.xalan.xpath.res.XPATHErrorResources;


/**
 * <meta name="usage" content="general"/>
 * Provides XSLTProcessor an interface to the Xerces XML parser.  This 
 * liaison should be used if Xerces DOM nodes are being process as 
 * the source tree or as the result tree.
 * @see org.apache.xalan.xslt.XSLTProcessor
 * @see org.apache.xml.parsers
 */
public class XercesLiaison extends XMLParserLiaisonDefault implements XPathSupport
{
  /**
   * Return a string suitible for telling the user what parser is being used.
   */
  public String getParserDescription()
  {
    return "XML4J Version "+ getXML4JVersionString();
  }

  /**
   * Constructor that takes SAX ErrorHandler as an argument. The error handler
   * is registered with the XML Parser. Any XML-related errors will be reported
   * to the calling application using this error handler.
   *
   * @param	errorHandler SAX ErrorHandler instance.
   */
  public XercesLiaison(org.xml.sax.ErrorHandler errorHandler)
  {
    m_errorHandler = errorHandler;
  }

  /**
   * Construct an instance.
   */
  public XercesLiaison(XPathEnvSupport envSupport)
  {
    m_envSupport = envSupport;
  }

  /**
   * Construct an instance.
   */
  public XercesLiaison()
  {
  }

  /**
   * <meta name="usage" content="internal"/>
   * Check node to see if it matches this liaison.
   */
  public void checkNode(Node node)
    throws SAXException
  {
    if(!(node instanceof org.apache.xerces.dom.NodeImpl))
      throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XERCES_CANNOT_HANDLE_NODES, new Object[]{((Object)node).getClass()})); //"XercesLiaison can not handle nodes of type"
        //+((Object)node).getClass());
  }

  /**
   * Returns true that this implementation does support
   * the SAX DocumentHandler interface.
   */
  public boolean supportsSAX()
  {
    return true;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get Xerces version field... we have to do this 'cause
   * the "public static final" fields seem to get bound
   * at compile time otherwise.
   */
  private String getXML4JVersionString()
  {
    // return Version.fVersion;
    String version = "";
    try
    {
      java.lang.reflect.Field versionField = Version.class.getField("fVersion");
      version = (String)versionField.get(null);
    }
    catch(Exception e)
    {
    }
    return version;
  }

  /**
   * <meta name="usage" content="internal"/>
   * Get one of the Xerces version numbers...
   * we have to do this 'cause the "public static final"
   * fields seem to get bound at compile time otherwise.
   */
  private int getXML4JVersionNum(int field)
  {
    int versionNum = 0;
    String ver = getXML4JVersionString();
    StringTokenizer tokenizer = new StringTokenizer(ver, " .");
    for(int i = 0; tokenizer.hasMoreTokens(); i++)
    {
      String tok = tokenizer.nextToken();
      if(field == i)
      {
        versionNum = Integer.parseInt(tok);
      }
    }
    return versionNum;
  }

  /**
   * <meta name="usage" content="internal"/>
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
    XMLParser parser = null;
    boolean isDomParser;
    if(null == m_docHandler)
    {
      isDomParser = true;
      DOMParser domParser = new MyParser();
      domParser.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", getShouldExpandEntityRefs()? false : true);
      if(m_useDOM2getNamespaceURI)
      {
        domParser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
        domParser.setFeature("http://xml.org/sax/features/namespaces", true);
      }
      else
      {
        domParser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
      }
      parser = domParser;
      //((DOMParser)parser).setCreateEntityReferenceNodes(getShouldExpandEntityRefs());
    }
    else
    {
      isDomParser = false;
      SAXParser saxparser = new SAXParser();
      saxparser.setDocumentHandler(m_docHandler);
      m_docHandler = null;
      parser = saxparser;
      if(null != m_DTDHandler)
      {
        saxparser.setDTDHandler(m_DTDHandler);
      }
    }
    if(getUseValidation())
      parser.setFeature("http://xml.org/sax/features/validation", true);
    else
      parser.setFeature("http://xml.org/sax/features/validation", false);

    try
    {
      parser.setFeature("http://apache.org/xml/features/allow-java-encodings", true);

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

      parser.parse(source);
      if(isDomParser)
      {
        m_document = ((DOMParser)parser).getDocument();
        if(null != source.getSystemId())
        {
          getSourceDocsTable().put(source.getSystemId(), m_document);
        }
      }
    }
    catch(SAXException e)
    {
      if(null != parser)
        throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XERCES_PARSE_ERROR_DETAILS, new Object[]{parser.getSystemId(), Integer.toString(parser.getLineNumber())}), e); //"XercesLiaison.parse error: SystemID - "+
          //parser.getSystemId()+" line - "+parser.getLineNumber(), e);
      else
        throw new SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_XERCES_PARSE_ERROR, null), e); //"XercesLiaison.parse error", e);
    }
  }

  /**
   * Create an empty DOM Document.  Mainly used for creating an
   * output document.  Implementation of XMLParserLiaison
   * interface method.
   */
  public Document createDocument()
  {
    org.apache.xerces.dom.DocumentImpl doc = new org.apache.xerces.dom.DocumentImpl();
    return doc;
  }

  /**
   * Given an ID, return the element.
   */
  public Element getElementByID(String id, Document doc)
  {
    return ((DocumentImpl)doc).getIdentifier(id);
  }

  /**
   * Tell if the node is ignorable whitespace.
   * @deprecated
   */
  public boolean isIgnorableWhitespace(Text node)
  {
    boolean isIgnorable;
    if( node instanceof org.apache.xerces.dom.TextImpl)
    {
      isIgnorable = ((org.apache.xerces.dom.TextImpl)node).isIgnorableWhitespace();
    }
    else
    {
      isIgnorable = false;
    }
    return isIgnorable;
  }
  
  private boolean m_useDOM2getNamespaceURI = false;
  
  /**
   * Set whether or not getNamespaceOfNode should use the Xerces/DOM2
   * getNamespaceURI.  This has to be set to true if the 
   * http://xml.org/sax/features/namespaces is set to false, or if 
   * the tree is mutated.
   */
  public void setUseDOM2getNamespaceURI(boolean b)
  {
    m_useDOM2getNamespaceURI = b;
  }

  
  /**
   * Get the namespace of the node.  Calls org.apache.xerces.dom.NodeImpl's
   * getNamespaceURI() if setUseDOM2getNamespaceURI(true) has been called.
   */
  public String getNamespaceOfNode(Node n)
  {
    return(m_useDOM2getNamespaceURI) ?
          ((org.apache.xerces.dom.NodeImpl)n).getNamespaceURI()
          : super.getNamespaceOfNode(n);
  }
  

  /**
  * Returns the local name of the given node.
  */
  // public String getLocalNameOfNode(Node n)
  // {
  //   return ((org.apache.xerces.dom.NodeImpl)n).getLocalName();
  // }

  /**
  * Returns the element name with the namespace expanded.
  */
  // public String getExpandedElementName(Element elem)
  // {
  //  String namespace = getNamespaceOfNode(elem);
  //   return (null != namespace) ? namespace+":"+ getLocalNameOfNode(elem)
  //                                : getLocalNameOfNode(elem);
  // }

  /**
  * Returns the attribute name with the namespace expanded.
  */
  // public String getExpandedAttributeName(Attr attr)
  // {
  //  String namespace = getNamespaceOfNode(attr);
  //   return (null != namespace) ? namespace+":"+ getLocalNameOfNode(attr)
  //                               : getLocalNameOfNode(attr);
  // }

  /**
   * Get the parent of a node.
   */
  public Node getParentOfNode(Node node)
    throws RuntimeException
  {
    return (Node.ATTRIBUTE_NODE == node.getNodeType())
           ? ((AttrImpl)node).getElement() : node.getParentNode();
  }

  /**
   * Override schema validation until they stop trying to resolve a namespace 
   * to the schema.
   */
  class NullSchemaValidator extends org.apache.xerces.validators.schema.XSchemaValidator
  {
    public NullSchemaValidator(org.apache.xerces.utils.StringPool stringPool,
                               org.apache.xerces.framework.XMLErrorReporter errorReporter,
                               org.apache.xerces.readers.XMLEntityHandler entityHandler)
    {
      super(stringPool, errorReporter, entityHandler);
    }

    public final void loadSchema(String uri) {}
  }

  class MyParser extends DOMParser
  {

    /** Returns the XML Schema validator. */
    protected org.apache.xerces.validators.schema.XSchemaValidator getSchemaValidator()
    {
      if(getUseValidation())
      {
        return super.getSchemaValidator();
      }
      else
      {
        if (fSchemaValidator == null)
          fSchemaValidator = new NullSchemaValidator(fStringPool, fErrorReporter, fEntityHandler);
        return fSchemaValidator;
      }
    }
  }

}


