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

import java.util.Enumeration;

import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xml.utils.MutableAttrListImpl;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.ObjectPool;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.Locator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;

/**
 * This class is a layer between the direct calls to the result
 * tree content handler, and the transformer.  For one thing,
 * we have to delay the call to
 * getContentHandler().startElement(name, atts) because of the
 * xsl:attribute and xsl:copy calls.  In other words,
 * the attributes have to be fully collected before you
 * can call startElement.
 */
public class ResultTreeHandler extends QueuedEvents
        implements ContentHandler, LexicalHandler, TransformState
{

  /** Indicate whether running in Debug mode        */
  private static final boolean DEBUG = false;

  /**
   * Null constructor for object pooling.
   */
  public ResultTreeHandler(){}

  /**
   * Create a new result tree handler.  The real content
   * handler will be the ContentHandler passed as an argument.
   *
   * @param transformer non-null transformer instance
   * @param realHandler Content Handler instance
   */
  public ResultTreeHandler(TransformerImpl transformer,
                           ContentHandler realHandler)
  {
    init(transformer, realHandler);
  }

  /**
   * Initializer method.
   *
   * @param transformer non-null transformer instance
   * @param realHandler Content Handler instance
   */
  public void init(TransformerImpl transformer, ContentHandler realHandler)
  {

    m_transformer = transformer;
    m_startElement.setTransformer(m_transformer);
    m_startDoc.setTransformer(m_transformer);

    TraceManager tracer = transformer.getTraceManager();

    if ((null != tracer) && tracer.hasTraceListeners())
      m_tracer = tracer;
    else
      m_tracer = null;
      
    m_startElement.setTraceManager(m_tracer);
    m_startDoc.setTraceManager(m_tracer);

    m_contentHandler = realHandler;
    m_startElement.setContentHandler(m_contentHandler);
    m_startDoc.setContentHandler(m_contentHandler);

    if (m_contentHandler instanceof LexicalHandler)
      m_lexicalHandler = (LexicalHandler) m_contentHandler;
    else
      m_lexicalHandler = null;
      
    m_startElement.setIsTransformClient(m_contentHandler instanceof TransformerClient);
      
    m_cloner = new ClonerToResultTree(transformer, this);

    // The stylesheet is set at a rather late stage, so I do 
    // this here, though it would probably be better done elsewhere.
    if (null != m_transformer)
      m_stylesheetRoot = m_transformer.getStylesheet();

    pushDocumentEvent();  // not pending yet.
  }

  /**
   * Bottleneck the startDocument event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException{}

  /**
   * Bottleneck the endDocument event.  This may be called
   * more than once in order to make sure the pending start
   * document is called.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {
    flushPending(EVT_ENDDOCUMENT);

    QueuedStartDocument qsd = getQueuedDocAtBottom();

    if (!qsd.isEnded)
    {
      m_contentHandler.endDocument();

      if (null != m_tracer)
      {
        GenerateEvent ge =
          new GenerateEvent(m_transformer,
                            GenerateEvent.EVENTTYPE_ENDDOCUMENT, null);

        m_tracer.fireGenerateEvent(ge);
      }

      qsd.setPending(false);
    }
  }
 
  /**
   * Bottleneck the startElement event.  This is used to "pend" an
   * element, so that attributes can still be added to it before
   * the real "startElement" is called on the result tree listener.
   *
   * @param ns Namespace URI of element
   * @param localName Local part of qname of element
   * @param name Name of element
   * @param atts List of attributes for the element
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String ns, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {

    QueuedStartElement qse = getQueuedElem();

    if (DEBUG)
    {
      if (null != qse && qse.isPending)
        System.out.println("(ResultTreeHandler#startElement - pended: " + qse.getURL() + "#"
                           + qse.getLocalName());

      System.out.println("ResultTreeHandler#startElement: " + ns + "#" + localName);
      if(null == ns)
      {
        (new RuntimeException(localName+" has a null namespace!")).printStackTrace();
      }
    }

    checkForSerializerSwitch(ns, localName);
    flushPending(EVT_STARTELEMENT);

    if (!m_nsContextPushed)
    {
      if (DEBUG) 
        System.out.println("ResultTreeHandler#startElement - push(startElement)");

      m_nsSupport.pushContext();
    }

    ensurePrefixIsDeclared(ns, name);

    // getQueuedElem().setPending(ns, localName, name, atts);
    this.pushElementEvent(ns, localName, name, atts);
  }

  /**
   * Bottleneck the endElement event.
   *
   * @param ns Namespace URI of element
   * @param localName Local part of qname of element
   * @param name Name of element
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String ns, String localName, String name)
          throws org.xml.sax.SAXException
  {
    if (DEBUG)
    {
      QueuedStartElement qse = getQueuedElem();
      if (null != qse && qse.isPending)
        System.out.println("(ResultTreeHandler#endElement - pended: " + qse.getURL() + "#"
                           + qse.getLocalName());

      System.out.println("ResultTreeHandler#endElement: " + ns + "#" + localName);
    }

    flushPending(EVT_ENDELEMENT);
    m_contentHandler.endElement(ns, localName, name);

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_ENDELEMENT,
                                           name);

      m_tracer.fireGenerateEvent(ge);
    }

    sendEndPrefixMappings();
    popEvent();

    if (DEBUG)
      System.out.println("ResultTreeHandler#startElement pop: " + localName);

    m_nsSupport.popContext();
  }

  /** Indicate whether a namespace context was pushed          */
  boolean m_nsContextPushed = false;

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   * <p>The information from this event is not necessary for
   * normal Namespace processing: the SAX XML reader will
   * automatically replace prefixes for element and attribute
   * names when the http://xml.org/sax/features/namespaces
   * feature is true (the default).</p>
   *
   * <p>There are cases, however, when applications need to
   * use prefixes in character data or in attribute values,
   * where they cannot safely be expanded automatically; the
   * start/endPrefixMapping event supplies the information
   * to the application to expand prefixes in those contexts
   * itself, if necessary.</p>
   *
   * <p>Note that start/endPrefixMapping events are not
   * guaranteed to be properly nested relative to each-other:
   * all startPrefixMapping events will occur before the
   * corresponding startElement event, and all endPrefixMapping
   * events will occur after the corresponding endElement event,
   * but their order is not guaranteed.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #endPrefixMapping
   * @see #startElement
   */
  public void startPrefixMapping(String prefix, String uri)
          throws org.xml.sax.SAXException
  {
    startPrefixMapping(prefix, uri, true);
  }

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @param shouldFlush Indicate whether pending events needs
   * to be flushed first  
   *
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   */
  public void startPrefixMapping(
          String prefix, String uri, boolean shouldFlush) throws org.xml.sax.SAXException
  {

    if (shouldFlush)
      flushPending(EVT_STARTPREFIXMAPPING);

    if (!m_nsContextPushed)
    {
      if (DEBUG)
        System.out.println("ResultTreeHandler#startPrefixMapping push(startPrefixMapping: " + prefix + ")");

      m_nsSupport.pushContext();

      m_nsContextPushed = true;
    }

    if (null == prefix)
      prefix = "";  // bit-o-hack, that that's OK

    String existingURI = m_nsSupport.getURI(prefix);
    
    if(null == existingURI)
      existingURI = "";
      
    if(null == uri)
      uri = "";

    if (!existingURI.equals(uri))
    {
      if (DEBUG)
      {
        System.out.println("ResultTreeHandler#startPrefixMapping Prefix: " + prefix);
        System.out.println("ResultTreeHandler#startPrefixMapping uri: " + uri);
      }
        
      m_nsSupport.declarePrefix(prefix, uri);
    }
  }

  /**
   * End the scope of a prefix-URI mapping.
   *
   * <p>See startPrefixMapping for details.  This event will
   * always occur after the corresponding endElement event,
   * but the order of endPrefixMapping events is not otherwise
   * guaranteed.</p>
   *
   * @param prefix The prefix that was being mapping.
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #startPrefixMapping
   * @see #endElement
   */
  public void endPrefixMapping(String prefix) throws org.xml.sax.SAXException{}

  /**
   * Bottleneck the characters event.
   *
   * @param ch Array of characters to process
   * @param start start of characters in the array
   * @param length Number of characters in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char ch[], int start, int length) throws org.xml.sax.SAXException
  {

    // It would be nice to suppress all whitespace before the
    // first element, but this is going to cause potential problems with 
    // text serialization and with text entities (right term?).
    // So this really needs to be done at the serializer level.
    /*if (m_startDoc.isPending
    && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
    return;*/
    
    if(DEBUG)
    {
      System.out.print("ResultTreeHandler#characters: ");
      int n = start+length;
      for (int i = start; i < n; i++) 
      {
        if(Character.isWhitespace(ch[i]))
          System.out.print("\\"+((int)ch[i]));
        else
          System.out.print(ch[i]);
      }   
      System.out.println("");    
    }

    flushPending(EVT_CHARACTERS);
    m_contentHandler.characters(ch, start, length);

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_CHARACTERS,
                                           ch, start, length);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the ignorableWhitespace event.
   *
   * @param ch Array of characters to process
   * @param start start of characters in the array
   * @param length Number of characters in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    QueuedStartDocument qsd = getQueuedDoc();

    if ((null != qsd) && qsd.isPending
            && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
      return;

    flushPending(EVT_IGNORABLEWHITESPACE);
    m_contentHandler.ignorableWhitespace(ch, start, length);

    if (null != m_tracer)
    {
      GenerateEvent ge =
        new GenerateEvent(m_transformer,
                          GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE, ch,
                          start, length);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the processingInstruction event.
   *
   * @param target Processing instruction target name
   * @param data Processing instruction data
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {
    flushPending(EVT_PROCESSINGINSTRUCTION);
    m_contentHandler.processingInstruction(target, data);

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_PI,
                                           target, data);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the comment event.
   *
   * @param data Comment data
   *
   * @throws org.xml.sax.SAXException
   */
  public void comment(String data) throws org.xml.sax.SAXException
  {

    flushPending(EVT_COMMENT);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.comment(data.toCharArray(), 0, data.length());
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_COMMENT,
                                           data);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the comment event.
   *
   * @param ch Character array with comment data
   * @param start start of characters in the array
   * @param length number of characters in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void comment(char ch[], int start, int length) throws org.xml.sax.SAXException
  {

    flushPending(EVT_COMMENT);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.comment(ch, start, length);
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_COMMENT,
                                           new String(ch, start, length));

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Entity reference event.
   *
   * @param name Name of entity
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {

    flushPending(EVT_ENTITYREF);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startEntity(name);
      m_lexicalHandler.endEntity(name);
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_ENTITYREF,
                                           name);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Start an entity.
   *
   * @param name Name of the entity
   *
   * @throws org.xml.sax.SAXException
   */
  public void startEntity(String name) throws org.xml.sax.SAXException
  {

    flushPending(EVT_STARTENTITY);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startEntity(name);
    }
  }

  /**
   * End an entity.
   *
   * @param name Name of the entity
   *
   * @throws org.xml.sax.SAXException
   */
  public void endEntity(String name) throws org.xml.sax.SAXException
  {

    flushPending(EVT_ENDENTITY);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.endEntity(name);
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_ENTITYREF,
                                           name);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Start the DTD.
   *
   * @param s1 The document type name.
   * @param s2 The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param s3 The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDTD(String s1, String s2, String s3) throws org.xml.sax.SAXException
  {

    flushPending(EVT_STARTDTD);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startDTD(s1, s2, s3);
    }
  }

  /**
   * End the DTD.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDTD() throws org.xml.sax.SAXException
  {

    flushPending(EVT_ENDDTD);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.endDTD();
    }
  }

  /**
   * Start the CDATACharacters.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startCDATA() throws org.xml.sax.SAXException
  {

    flushPending(EVT_STARTCDATA);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startCDATA();
    }
  }

  /**
   * End the CDATA characters.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endCDATA() throws org.xml.sax.SAXException
  {

    flushPending(EVT_ENDCDATA);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.endCDATA();
    }
  }

  /**
   * Receive notification of a skipped entity.
   *
   * <p>The Parser will invoke this method once for each entity
   * skipped.  Non-validating processors may skip entities if they
   * have not seen the declarations (because, for example, the
   * entity was declared in an external DTD subset).  All processors
   * may skip external entities, depending on the values of the
   * http://xml.org/sax/features/external-general-entities and the
   * http://xml.org/sax/features/external-parameter-entities
   * properties.</p>
   *
   * @param name The name of the skipped entity.  If it is a
   *        parameter entity, the name will begin with '%'.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void skippedEntity(String name) throws org.xml.sax.SAXException{}

  /**
   * Flush the pending element.
   *
   * @throws org.xml.sax.SAXException
   */
  public void flushPending() throws org.xml.sax.SAXException
  {
    flushPending(EVT_NODE);
  }

  /**
   * Flush the pending element.
   *
   * @param type Event type
   *
   * @throws org.xml.sax.SAXException
   */
  public void flushPending(int type) throws org.xml.sax.SAXException
  {

    QueuedStartElement qe = getQueuedElem();
    QueuedStartDocument qdab = getQueuedDocAtBottom();

    if ((type != EVT_STARTPREFIXMAPPING) && qdab.isPending)
    {
      qdab.flush(this);
    }

    if ((null != qe) && qe.isPending)
    {
      if (!qe.nsDeclsHaveBeenAdded())
        addNSDeclsToAttrs();

      sendStartPrefixMappings();
      
      if(DEBUG)
      {
        System.out.println("ResultTreeHandler#flushPending - start flush: "
                          +qe.getName());
      }

      qe.flush();
      
      if(DEBUG)
      {
        System.out.println("ResultTreeHandler#flushPending - after flush, isPending: "
                          +qe.isPending);
      }
      
      m_nsContextPushed = false;
    }
  }

  /**
   * Given a result tree fragment, walk the tree and
   * output it to the result stream.
   *
   * @param obj Result tree fragment object
   * @param support XPath context for the result tree fragment
   *
   * @throws org.xml.sax.SAXException
   */
  public void outputResultTreeFragment(XObject obj, XPathContext support)
          throws org.xml.sax.SAXException
  {

    DocumentFragment docFrag = obj.rtree(support);
    TreeWalker tw = new TreeWalker(this, support.getDOMHelper());

    Node n;
    for (n = docFrag.getFirstChild(); null != n; n = n.getNextSibling())
    {
      flushPending(EVT_NODE);  // I think.
      tw.traverse(n);
    }
  }

  /**
   * Clone an element with or without children.
   *
   * @param node Element to clone
   * @param shouldCloneAttributes Whether or not to clone with children
   *
   * @throws org.xml.sax.SAXException
   */
  public void cloneToResultTree(Node node, boolean shouldCloneAttributes)
          throws org.xml.sax.SAXException
  {
    try
    {
      m_cloner.cloneToResultTree(node, shouldCloneAttributes);
    }
    catch(TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * To fullfill the FormatterListener interface... no action
   * for the moment.
   *
   * @param locator Document locator
   */
  public void setDocumentLocator(Locator locator){}

  /**
   * This function checks to make sure a given prefix is really
   * declared.  It might not be, because it may be an excluded prefix.
   * If it's not, it still needs to be declared at this point.
   * TODO: This needs to be done at an earlier stage in the game... -sb
   *
   * @param ns Namespace URI of the element 
   * @param rawName Raw name of element (with prefix)
   *
   * @throws org.xml.sax.SAXException
   */
  void ensurePrefixIsDeclared(String ns, String rawName) throws org.xml.sax.SAXException
  {

    if (ns != null && ns.length() > 0)
    {
      int index;
      String prefix = (index = rawName.indexOf(":")) < 0
                      ? "" : rawName.substring(0, index);

      if (null != prefix)
      {
        String foundURI = m_nsSupport.getURI(prefix);

        if ((null == foundURI) ||!foundURI.equals(ns))
          startPrefixMapping(prefix, ns, false);
      }
    }
  }

  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   *
   * @throws org.xml.sax.SAXException
   */
  protected void sendStartPrefixMappings() throws org.xml.sax.SAXException
  {

    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = m_contentHandler;

    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();

      handler.startPrefixMapping(prefix, m_nsSupport.getURI(prefix));
    }
  }

  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   *
   * @throws org.xml.sax.SAXException
   */
  protected void sendEndPrefixMappings() throws org.xml.sax.SAXException
  {

    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = m_contentHandler;

    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();

      handler.endPrefixMapping(prefix);
    }
  }

  /**
   * Check to see if we should switch serializers based on the
   * first output element being an HTML element.
   *
   * @param ns Namespace URI of the element
   * @param localName Local part of name of the element  
   *
   * @throws org.xml.sax.SAXException
   */
  private void checkForSerializerSwitch(String ns, String localName)
          throws org.xml.sax.SAXException
  {

    try
    {
      QueuedStartDocument qdab = getQueuedDocAtBottom();

      if (qdab.isPending)
      {
        SerializerSwitcher.switchSerializerIfHTML(m_transformer, ns, localName);
      }
    }
    catch(TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   */
  protected void addNSDeclsToAttrs()
  {

    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    QueuedStartElement qe = getQueuedElem();

    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();
      boolean isDefault = (prefix.length() == 0);
      String name;

      if (isDefault)
      {

        //prefix = "xml";
        name = "xmlns";
      }
      else
        name = "xmlns:" + prefix;

      String uri = m_nsSupport.getURI(prefix);
      
      if(null == uri)
        uri = "";

      qe.addAttribute("http://www.w3.org/2000/xmlns/", prefix, name, "CDATA",
                      uri);
    }

    qe.setNSDeclsHaveBeenAdded(true);
  }

  /**
   * Copy <KBD>xmlns:</KBD> attributes in if not already in scope.
   *
   * @param src Source Node
   *
   * @throws TransformerException
   */
  public void processNSDecls(Node src) throws TransformerException
  {

    try
    {
      int type;

      // Vector nameValues = null;
      // Vector alreadyProcessedPrefixes = null;
      Node parent;

      if (((type = src.getNodeType()) == Node.ELEMENT_NODE || (type == Node.ENTITY_REFERENCE_NODE))
          && (parent = src.getParentNode()) != null)
      {
        processNSDecls(parent);
      }

      if (type == Node.ELEMENT_NODE)
      {
        NamedNodeMap nnm = src.getAttributes();
        int nAttrs = nnm.getLength();

        for (int i = 0; i < nAttrs; i++)
        {
          Node attr = nnm.item(i);
          String aname = attr.getNodeName();

          if (QName.isXMLNSDecl(aname))
          {
            String prefix = QName.getPrefixFromXMLNSDecl(aname);
            String desturi = getURI(prefix);
            String srcURI = attr.getNodeValue();

            if (!srcURI.equalsIgnoreCase(desturi))
            {
              this.startPrefixMapping(prefix, srcURI, false);
            }
          }
        }
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }

  /**
   * Given a prefix, return the namespace,
   *
   * @param prefix Given prefix name
   *
   * @return Namespace associated with the given prefix, or null
   */
  public String getURI(String prefix)
  {
    return m_nsSupport.getURI(prefix);
  }

  /**
   * Given a namespace, try and find a prefix.
   *
   * @param namespace Given namespace URI
   *
   * @return Prefix name associated with namespace URI 
   */
  public String getPrefix(String namespace)
  {

    // This Enumeration business may be too slow for our purposes...
    Enumeration enum = m_nsSupport.getPrefixes();

    while (enum.hasMoreElements())
    {
      String prefix = (String) enum.nextElement();

      if (m_nsSupport.getURI(prefix).equals(namespace))
        return prefix;
    }

    return null;
  }

  /**
   * Get the NamespaceSupport object.
   *
   * @return NamespaceSupport object.
   */
  public NamespaceSupport getNamespaceSupport()
  {
    return m_nsSupport;
  }

//  /**
//   * Override QueuedEvents#initQSE.
//   *
//   * @param qse Give queued Sax event
//   */
//  protected void initQSE(QueuedSAXEvent qse)
//  {
//
//    // qse.setContentHandler(m_contentHandler);
//    // qse.setTransformer(m_transformer);
//    // qse.setTraceManager(m_tracer);
//  }

  /**
   * Return the current content handler.
   *
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #setContentHandler
   */
  public ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }

  /**
   * Set the current content handler.
   *
   *
   * @param ch Content Handler to be set
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #getContentHandler
   */
  public void setContentHandler(ContentHandler ch)
  {

    m_contentHandler = ch;
    m_startElement.setIsTransformClient(m_contentHandler instanceof TransformerClient);
    m_startElement.setContentHandler(m_contentHandler);
    m_startDoc.setContentHandler(m_contentHandler);

    if (m_contentHandler instanceof LexicalHandler)
      m_lexicalHandler = (LexicalHandler) m_contentHandler;
    else
      m_lexicalHandler = null;

    reInitEvents();
  }

  /**
   * Get a unique namespace value.
   *
   * @return a unique namespace value to be used with a 
   * fabricated prefix
   */
  public int getUniqueNSValue()
  {
    return m_uniqueNSValue++;
  }

  /**
   * Get new unique namespace prefix.
   *
   * @return Unique fabricated prefix.
   */
  public String getNewUniqueNSPrefix()
  {
    return S_NAMESPACEPREFIX + String.valueOf(getUniqueNSValue());
  }

  /**
   * Get the pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   *
   * @return the pending attributes. 
   */
  public MutableAttrListImpl getPendingAttributes()
  {
    return getQueuedElem().getAttrs();
  }

  /**
   * Add an attribute to the end of the list.
   *
   * <p>Do not pass in xmlns decls to this function!
   *
   * <p>For the sake of speed, this method does no checking
   * to see if the attribute is already in the list: that is
   * the responsibility of the application.</p>
   *
   * @param uri The Namespace URI, or the empty string if
   *        none is available or Namespace processing is not
   *        being performed.
   * @param localName The local name, or the empty string if
   *        Namespace processing is not being performed.
   * @param rawName The raw XML 1.0 name, or the empty string
   *        if raw names are not available.
   * @param type The attribute type as a string.
   * @param value The attribute value.
   *
   * @throws TransformerException
   */
  public void addAttribute(
          String uri, String localName, String rawName, String type, String value)
            throws TransformerException
  {

    QueuedStartElement qe = getQueuedElem();

    if (!qe.nsDeclsHaveBeenAdded())
      addNSDeclsToAttrs();
      
    if(null == uri) // defensive, should not really need this.
      uri = "";

    try
    {
      if(!rawName.equals("xmlns")) // don't handle xmlns default namespace.
        ensurePrefixIsDeclared(uri, rawName);
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }

    if (DEBUG)
      System.out.println("ResultTreeHandler#addAttribute Adding attr: " + localName + ", " + uri);

    if(!isDefinedNSDecl(rawName, value))
      qe.addAttribute(uri, localName, rawName, type, value);
  }

  /**
   * Return whether or not a namespace declaration is defined
   *
   *
   * @param rawName Raw name of namespace element
   * @param value URI of given namespace
   *
   * @return True if the namespace is already defined in list of 
   * namespaces 
   */
  public boolean isDefinedNSDecl(String rawName, String value)
  {

    if (rawName.equals("xmlns") || rawName.startsWith("xmlns:"))
    {
      int index;
      String prefix = (index = rawName.indexOf(":")) < 0
                      ? "" : rawName.substring(0, index);
      String definedURI = m_nsSupport.getURI(prefix);

      if (null != definedURI)
      {
        if (definedURI.equals(value))
        {
          return true;
        }
        else
          return false;
      }
      else
        return false;
    }
    else
      return false;
  }

  /**
   * Returns whether a namespace is defined 
   *
   *
   * @param attr Namespace attribute node
   *
   * @return True if the namespace is already defined in 
   * list of namespaces
   */
  public boolean isDefinedNSDecl(Attr attr)
  {

    String rawName = attr.getNodeName();

    if (rawName.equals("xmlns") || rawName.startsWith("xmlns:"))
    {
      int index;
      String prefix = (index = rawName.indexOf(":")) < 0
                      ? "" : rawName.substring(0, index);
      String uri = getURI(prefix);

      if ((null != uri) && uri.equals(attr.getValue()))
        return true;
    }

    return false;
  }

  /**
   * Copy an DOM attribute to the created output element, executing
   * attribute templates as need be, and processing the xsl:use
   * attribute.
   *
   * @param attr Attribute node to add to result tree
   *
   * @throws TransformerException
   */
  public void addAttribute(Attr attr) throws TransformerException
  {

    if (isDefinedNSDecl(attr))
      return;

    DOMHelper helper = m_transformer.getXPathContext().getDOMHelper();
    
    String ns = helper.getNamespaceOfNode(attr);
    if(ns == null)
      ns = "";

    addAttribute(ns,
                 helper.getLocalNameOfNode(attr), attr.getNodeName(),
                 "CDATA", attr.getValue());
  }  // end copyAttributeToTarget method

  /**
   * Copy DOM attributes to the result element.
   *
   * @param src Source node with the attributes
   *
   * @throws TransformerException
   */
  public void addAttributes(Node src) throws TransformerException
  {

    NamedNodeMap nnm = src.getAttributes();
    int nAttrs = nnm.getLength();

    for (int i = 0; i < nAttrs; i++)
    {
      Attr node = (Attr) nnm.item(i);

      addAttribute(node);
    }
  }

  /**
   * Tell if an element is pending, to be output to the result tree.
   *
   * @return True if an element is pending
   */
  public boolean isElementPending()
  {

    QueuedStartElement qse = getQueuedElem();

    return (null != qse) ? qse.isPending : false;
  }
  
  /**
   * Retrieves the stylesheet element that produced
   * the SAX event.
   *
   * <p>Please note that the ElemTemplateElement returned may
   * be in a default template, and thus may not be
   * defined in the stylesheet.</p>
   *
   * @return the stylesheet element that produced the SAX event.
   */
  public ElemTemplateElement getCurrentElement()
  {
    QueuedStartElement qe = getQueuedElem();
    if(null != qe && qe.isPending)
      return qe.getCurrentElement(); 
    else
      return m_transformer.getCurrentElement();
  }

  /**
   * This method retrieves the current context node
   * in the source tree.
   *
   * @return the current context node in the source tree.
   */
  public Node getCurrentNode()
  {
    QueuedStartElement qe = getQueuedElem();
    if(null != qe && qe.isPending)
      return qe.getCurrentNode();
    else
      return m_transformer.getCurrentNode();
  }

  /**
   * This method retrieves the xsl:template
   * that is in effect, which may be a matched template
   * or a named template.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return the xsl:template that is in effect
   */
  public ElemTemplate getCurrentTemplate()
  {
    QueuedStartElement qe = getQueuedElem();
    if(null != qe && qe.isPending)
      return qe.getCurrentTemplate();
    else
      return m_transformer.getCurrentTemplate();
  }

  /**
   * This method retrieves the xsl:template
   * that was matched.  Note that this may not be
   * the same thing as the current template (which
   * may be from getCurrentElement()), since a named
   * template may be in effect.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return the xsl:template that was matched.
   */
  public ElemTemplate getMatchedTemplate()
  {
    QueuedStartElement qe = getQueuedElem();
    if(null != qe && qe.isPending)
      return qe.getMatchedTemplate();
    else
      return m_transformer.getMatchedTemplate();
  }

  /**
   * Retrieves the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   *
   * @return the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   */
  public Node getMatchedNode()
  {
    QueuedStartElement qe = getQueuedElem();
    if(null != qe && qe.isPending)
      return qe.getMatchedNode();
    else
      return m_transformer.getMatchedNode();
  }

  /**
   * Get the current context node list.
   *
   * @return the current context node list.
   */
  public NodeIterator getContextNodeList()
  {
    QueuedStartElement qe = getQueuedElem();
    if(null != qe && qe.isPending)
      return qe.getContextNodeList();
    else
      return m_transformer.getContextNodeList();
  }

  /**
   * Get the TrAX Transformer object in effect.
   *
   * @return the TrAX Transformer object in effect.
   */
  public Transformer getTransformer()
  {
    return m_transformer;
  }

  /**
   * Use the SAX2 helper class to track result namespaces.
   */
  NamespaceSupport m_nsSupport = new NamespaceSupport();

  /**
   * The transformer object.
   */
  private TransformerImpl m_transformer;

  /**
   * The content handler.  May be null, in which
   * case, we'll defer to the content handler in the
   * transformer.
   */
  private ContentHandler m_contentHandler;

  /** The LexicalHandler          */
  private LexicalHandler m_lexicalHandler;

  /**
   * The root of a linked set of stylesheets.
   */
  private StylesheetRoot m_stylesheetRoot = null;

  /**
   * This is used whenever a unique namespace is needed.
   */
  private int m_uniqueNSValue = 0;

  /** Prefix used to create unique prefix names          */
  private static final String S_NAMESPACEPREFIX = "ns";
  
  /**
   * This class clones nodes to the result tree.
   */
  public ClonerToResultTree m_cloner;

  /**
   * Trace manager for debug support.
   */
  private TraceManager m_tracer;

  // These are passed to flushPending, to help it decide if it 
  // should really flush.

  /** SETDOCUMENTLOCATOR event type          */
  private static final int EVT_SETDOCUMENTLOCATOR = 1;

  /** STARTDOCUMENT event type          */
  private static final int EVT_STARTDOCUMENT = 2;

  /** ENDDOCUMENT event type           */
  private static final int EVT_ENDDOCUMENT = 3;

  /** STARTPREFIXMAPPING event type          */
  private static final int EVT_STARTPREFIXMAPPING = 4;

  /** ENDPREFIXMAPPING event type          */
  private static final int EVT_ENDPREFIXMAPPING = 5;

  /** STARTELEMENT event type          */
  private static final int EVT_STARTELEMENT = 6;

  /** ENDELEMENT event type           */
  private static final int EVT_ENDELEMENT = 7;

  /** CHARACTERS event type          */
  private static final int EVT_CHARACTERS = 8;

  /** IGNORABLEWHITESPACE event type           */
  private static final int EVT_IGNORABLEWHITESPACE = 9;

  /** PROCESSINGINSTRUCTION event type          */
  private static final int EVT_PROCESSINGINSTRUCTION = 10;

  /** SKIPPEDENTITY event type          */
  private static final int EVT_SKIPPEDENTITY = 11;

  /** COMMENT event type          */
  private static final int EVT_COMMENT = 12;

  /** ENTITYREF event type          */
  private static final int EVT_ENTITYREF = 13;

  /** STARTENTITY event type          */
  private static final int EVT_STARTENTITY = 14;

  /** ENDENTITY event type          */
  private static final int EVT_ENDENTITY = 15;

  /** STARTDTD event type          */
  private static final int EVT_STARTDTD = 16;

  /** ENDDTD event type         */
  private static final int EVT_ENDDTD = 17;

  /** STARTCDATA event type          */
  private static final int EVT_STARTCDATA = 22;

  /** ENDCDATA event type          */
  private static final int EVT_ENDCDATA = 23;

  /** NODE  event type         */
  private static final int EVT_NODE = 24;
}
