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
package org.apache.xml.dtm.ref.sax2dtm;

import java.util.Hashtable;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import org.apache.xalan.transformer.XalanProperties;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

import org.apache.xml.dtm.*;
import org.apache.xml.dtm.ref.*;
import org.apache.xml.utils.StringVector;
import org.apache.xml.utils.IntVector;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.SuballocatedIntVector;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.xml.sax.*;
import org.xml.sax.ext.*;

/**
 * This class implements a DTM that tends to be optimized more for speed than
 * for compactness, that is constructed via SAX2 ContentHandler events.
 */
public class SAX2DTM extends DTMDefaultBaseIterators
        implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler,
                   DeclHandler, LexicalHandler
{
  /** Set true to monitor SAX events and similar diagnostic info. */
  private static final boolean DEBUG = false;

  /**
   * If we're building the model incrementally on demand, we need to
   * be able to tell the source when to send us more data.
   *
   * Note that if this has not been set, and you attempt to read ahead
   * of the current build point, we'll probably throw a null-pointer
   * exception. We could try to wait-and-retry instead, as a very poor
   * fallback, but that has all the known problems with multithreading
   * on multiprocessors and we Don't Want to Go There.
   *
   * @see setIncrementalSAXSource
   */
  private IncrementalSAXSource m_incrementalSAXSource = null;

  /**
   * All the character content, including attribute values, are stored in
   * this buffer.
   *
   * %REVIEW% Should this have an option of being shared across DTMs?
   * Sequentially only; not threadsafe... Currently, I think not.
   *
   * %REVIEW%  Initial size should be pushed way down to reduce weight of RTFs,
   * pending reduction in number of RTFs.
   * However, I've got a bug in whitespace normalization to fix first.
   */
  //private FastStringBuffer m_chars = new FastStringBuffer(13, 13);
  private FastStringBuffer m_chars = new FastStringBuffer(5, 13);

  /** This vector holds offset and length data. */
  protected SuballocatedIntVector m_data;

  /** The parent stack, needed only for construction. */
  transient private IntStack m_parents = new IntStack();

  /** The current previous node, needed only for construction time. */
  transient private int m_previous = 0;

  /** Namespace support, only relevent at construction time. */
  transient private java.util.Vector m_prefixMappings =
    new java.util.Vector();

  /** Namespace support, only relevent at construction time. */
  transient private IntStack m_contextIndexes = new IntStack();

  /** Type of next characters() event within text block in prgress. */
  transient private int m_textType = DTM.TEXT_NODE;

  /**
   * Type of coalesced text block. See logic in the characters()
   * method.
   */
  transient private int m_coalescedTextType = DTM.TEXT_NODE;

  /** The SAX Document locator */
  transient private Locator m_locator = null;

  /** We are inside the DTD.  This is used for ignoring comments.  */
  transient private boolean m_insideDTD = false;

  /** Tree Walker for dispatchToEvents. */
  protected DTMTreeWalker m_walker = new DTMTreeWalker();

  /** pool of string values that come as strings. */
  private DTMStringPool m_valuesOrPrefixes = new DTMStringPool();

  /** End document has been reached. */
  private boolean m_endDocumentOccured = false;

  /** Data or qualified name values, one array element for each node. */
  protected SuballocatedIntVector m_dataOrQName;

  /**
   * This table holds the ID string to node associations, for
   * XML IDs.
   */
  protected Hashtable m_idAttributes = new Hashtable();

  /**
   * fixed dom-style names.
   */
  static final String[] m_fixednames = { null, null,  // nothing, Element
                                         null, "#text",  // Attr, Text
                                         "#cdata_section", null,  // CDATA, EntityReference
                                         null, null,  // Entity, PI
                                         "#comment", "#document",  // Comment, Document
                                         null, "#document-fragment",  // Doctype, DocumentFragment
                                         null };  // Notation

  /**
   * Vector of entities.  Each record is composed of four Strings:
   *  publicId, systemID, notationName, and name.
   */
  private Vector m_entities = null;

  /** m_entities public ID offset. */
  private static final int ENTITY_FIELD_PUBLICID = 0;

  /** m_entities system ID offset. */
  private static final int ENTITY_FIELD_SYSTEMID = 1;

  /** m_entities notation name offset. */
  private static final int ENTITY_FIELD_NOTATIONNAME = 2;

  /** m_entities name offset. */
  private static final int ENTITY_FIELD_NAME = 3;

  /** Number of entries per record for m_entities. */
  private static final int ENTITY_FIELDS_PER = 4;

  /**
   * The starting offset within m_chars for the text or
   * CDATA_SECTION node currently being acumulated,
   * or -1 if there is no text node in progress
   */
  private int m_textPendingStart = -1;

  /**
   * Describes whether information about document source location
   * should be maintained or not.
   */
  private boolean m_useSourceLocationProperty = false;

  private StringVector m_sourceSystemId;
  private IntVector m_sourceLine;
  private IntVector m_sourceColumn;
  
  /**
   * Construct a SAX2DTM object ready to be constructed from SAX2
   * ContentHandler events.
   *
   * @param mgr The DTMManager who owns this DTM.
   * @param source the JAXP 1.1 Source object for this DTM.
   * @param dtmIdentity The DTM identity ID for this DTM.
   * @param whiteSpaceFilter The white space filter for this DTM, which may
   *                         be null.
   * @param xstringfactory XMLString factory for creating character content.
   * @param doIndexing true if the caller considers it worth it to use 
   *                   indexing schemes.
   */
  public SAX2DTM(DTMManager mgr, Source source, int dtmIdentity,
                 DTMWSFilter whiteSpaceFilter,
                 XMLStringFactory xstringfactory,
                 boolean doIndexing)
  {

    super(mgr, source, dtmIdentity, whiteSpaceFilter, 
          xstringfactory, doIndexing);
          
    // %REVIEW%  Initial size pushed way down to reduce weight of RTFs
    // (I'm not entirely sure 0 would work, so I'm playing it safe for now.)
    //m_data = new SuballocatedIntVector(doIndexing ? (1024*2) : 512, 1024);
    m_data = new SuballocatedIntVector(32, 1024);

    m_data.addElement(0);   // Need placeholder in case index into here must be <0.

    m_dataOrQName = new SuballocatedIntVector(m_initialblocksize);
  }

  /**
   * Get the data or qualified name for the given node identity.
   *
   * @param identity The node identity.
   *
   * @return The data or qualified name, or DTM.NULL.
   */
  protected int _dataOrQName(int identity)
  {

    if (identity < m_size)
      return m_dataOrQName.elementAt(identity);

    // Check to see if the information requested has been processed, and, 
    // if not, advance the iterator until we the information has been 
    // processed.
    while (true)
    {
      boolean isMore = nextNode();

      if (!isMore)
        return NULL;
      else if (identity < m_size)
        return m_dataOrQName.elementAt(identity);
    }
  }

  /**
   * Ask the CoRoutine parser to doTerminate and clear the reference.
   */
  public void clearCoRoutine()
  {
    clearCoRoutine(true);
  }

  /**
   * Ask the CoRoutine parser to doTerminate and clear the reference. If
   * the CoRoutine parser has already been cleared, this will have no effect.
   *
   * @param callDoTerminate true of doTerminate should be called on the
   * coRoutine parser.
   */
  public void clearCoRoutine(boolean callDoTerminate)
  {

    if (null != m_incrementalSAXSource)
    {
      if (callDoTerminate)
        m_incrementalSAXSource.deliverMoreNodes(false);

      m_incrementalSAXSource = null;
    }
  }

  /**
   * Bind a IncrementalSAXSource to this DTM. If we discover we need nodes
   * that have not yet been built, we will ask this object to send us more
   * events, and it will manage interactions with its data sources.
   *
   * Note that we do not actually build the IncrementalSAXSource, since we don't
   * know what source it's reading from, what thread that source will run in,
   * or when it will run.
   *
   * @param incrementalSAXSource The parser that we want to recieve events from
   * on demand.
   * @param appCoRID The CoRoutine ID for the application.
   */
  public void setIncrementalSAXSource(IncrementalSAXSource incrementalSAXSource)
  {

    // Establish coroutine link so we can request more data
    //
    // Note: It's possible that some versions of IncrementalSAXSource may
    // not actually use a CoroutineManager, and hence may not require
    // that we obtain an Application Coroutine ID. (This relies on the
    // coroutine transaction details having been encapsulated in the
    // IncrementalSAXSource.do...() methods.)
    m_incrementalSAXSource = incrementalSAXSource;

    // Establish SAX-stream link so we can receive the requested data
    incrementalSAXSource.setContentHandler(this);
    incrementalSAXSource.setLexicalHandler(this);

    // Are the following really needed? incrementalSAXSource doesn't yet
    // support them, and they're mostly no-ops here...
    //incrementalSAXSource.setErrorHandler(this);
    //incrementalSAXSource.setDTDHandler(this);
    //incrementalSAXSource.setDeclHandler(this);
  }

  /**
   * getContentHandler returns "our SAX builder" -- the thing that
   * someone else should send SAX events to in order to extend this
   * DTM model.
   *
   * %REVIEW% Should this return null if constrution already done/begun?
   *
   * @return null if this model doesn't respond to SAX events,
   * "this" if the DTM object has a built-in SAX ContentHandler,
   * the IncrementalSAXSource if we're bound to one and should receive
   * the SAX stream via it for incremental build purposes...
   */
  public ContentHandler getContentHandler()
  {

    if (m_incrementalSAXSource instanceof IncrementalSAXSource_Filter)
      return (ContentHandler) m_incrementalSAXSource;
    else
      return this;
  }

  /**
   * Return this DTM's lexical handler.
   *
   * %REVIEW% Should this return null if constrution already done/begun?
   *
   * @return null if this model doesn't respond to lexical SAX events,
   * "this" if the DTM object has a built-in SAX ContentHandler,
   * the IncrementalSAXSource if we're bound to one and should receive
   * the SAX stream via it for incremental build purposes...
   */
  public LexicalHandler getLexicalHandler()
  {

    if (m_incrementalSAXSource instanceof IncrementalSAXSource_Filter)
      return (LexicalHandler) m_incrementalSAXSource;
    else
      return this;
  }

  /**
   * Return this DTM's EntityResolver.
   *
   * @return null if this model doesn't respond to SAX entity ref events.
   */
  public EntityResolver getEntityResolver()
  {
    return this;
  }

  /**
   * Return this DTM's DTDHandler.
   *
   * @return null if this model doesn't respond to SAX dtd events.
   */
  public DTDHandler getDTDHandler()
  {
    return this;
  }

  /**
   * Return this DTM's ErrorHandler.
   *
   * @return null if this model doesn't respond to SAX error events.
   */
  public ErrorHandler getErrorHandler()
  {
    return this;
  }

  /**
   * Return this DTM's DeclHandler.
   *
   * @return null if this model doesn't respond to SAX Decl events.
   */
  public DeclHandler getDeclHandler()
  {
    return this;
  }

  /**
   * @return true iff we're building this model incrementally (eg
   * we're partnered with a IncrementalSAXSource) and thus require that the
   * transformation and the parse run simultaneously. Guidance to the
   * DTMManager.
   */
  public boolean needsTwoThreads()
  {
    return null != m_incrementalSAXSource;
  }

  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value of the given node (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value). Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   * @param normalize true if the content should be normalized according to 
   * the rules for the XPath
   * <a href="http://www.w3.org/TR/xpath#function-normalize-space">normalize-space</a>
   * function.
   *
   * @throws SAXException
   */
  public void dispatchCharactersEvents(int nodeHandle, ContentHandler ch, 
                                       boolean normalize)
          throws SAXException
  {

    int identity = makeNodeIdentity(nodeHandle);
    int type = _type(identity);

    if (isTextType(type))
    {
      int dataIndex = m_dataOrQName.elementAt(identity);
      int offset = m_data.elementAt(dataIndex);
      int length = m_data.elementAt(dataIndex + 1);
      
      if(normalize)
        m_chars.sendNormalizedSAXcharacters(ch, offset, length);
      else
        m_chars.sendSAXcharacters(ch, offset, length);
    }
    else
    {
      int firstChild = _firstch(identity);

      if (DTM.NULL != firstChild)
      {
        int offset = -1;
        int length = 0;
        int level = _level(identity);

        identity = firstChild;

        while (DTM.NULL != identity && (_level(identity) > level))
        {
          type = _type(identity);

          if (isTextType(type))
          {
            int dataIndex = _dataOrQName(identity);

            if (-1 == offset)
            {
              offset = m_data.elementAt(dataIndex);
            }

            length += m_data.elementAt(dataIndex + 1);
          }

          identity = getNextNodeIdentity(identity);
        }

        if (length > 0)
        {
          if(normalize)
            m_chars.sendNormalizedSAXcharacters(ch, offset, length);
          else
            m_chars.sendSAXcharacters(ch, offset, length);
        }
      }
      else if(type != DTM.ELEMENT_NODE)
      {
        int dataIndex = _dataOrQName(identity);

        if (dataIndex < 0)
        {
          dataIndex = -dataIndex;
          dataIndex = m_data.elementAt(dataIndex + 1);
        }

        String str = m_valuesOrPrefixes.indexToString(dataIndex);

          if(normalize)
            FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 
                                                         0, str.length(), ch);
          else
            ch.characters(str.toCharArray(), 0, str.length());
      }
    }
  }


  /**
   * Given a node handle, return its DOM-style node name. This will
   * include names such as #text or #document.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   * %REVIEW% Document when empty string is possible...
   * %REVIEW-COMMENT% It should never be empty, should it?
   */
  public String getNodeName(int nodeHandle)
  {

    int expandedTypeID = getExpandedTypeID(nodeHandle);
    // If just testing nonzero, no need to shift...
    //int namespaceID = (expandedTypeID & ExpandedNameTable.MASK_NAMESPACE)
    //                  >> ExpandedNameTable.BITS_PER_LOCALNAME;
    int namespaceID = (expandedTypeID & ExpandedNameTable.MASK_NAMESPACE);

    if (0 == namespaceID)
    {
      // Don't retrieve name until/unless needed
      // String name = m_expandedNameTable.getLocalName(expandedTypeID);
      int type = getNodeType(nodeHandle);

      if (type == DTM.NAMESPACE_NODE)
      {
        if (null == m_expandedNameTable.getLocalName(expandedTypeID))
          return "xmlns";
        else
          return "xmlns:" + m_expandedNameTable.getLocalName(expandedTypeID);
      }
      else if (0 == m_expandedNameTable.getLocalNameID(expandedTypeID))
      {
        return m_fixednames[type];
      }
      else
        return m_expandedNameTable.getLocalName(expandedTypeID);
    }
    else
    {
      int qnameIndex = m_dataOrQName.elementAt(makeNodeIdentity(nodeHandle));

      if (qnameIndex < 0)
      {
        qnameIndex = -qnameIndex;
        qnameIndex = m_data.elementAt(qnameIndex);
      }

      return m_valuesOrPrefixes.indexToString(qnameIndex);
    }
  }

  /**
   * Given a node handle, return the XPath node name.  This should be
   * the name as described by the XPath data model, NOT the DOM-style
   * name.
   *
   * @param nodeHandle the id of the node.
   * @return String Name of this node, which may be an empty string.
   */
  public String getNodeNameX(int nodeHandle)
  {

    int expandedTypeID = getExpandedTypeID(nodeHandle);
    int namespaceID = (expandedTypeID & ExpandedNameTable.MASK_NAMESPACE)
                      >> ExpandedNameTable.BITS_PER_LOCALNAME;

    if (0 == namespaceID)
    {
      String name = m_expandedNameTable.getLocalName(expandedTypeID);

      if (name == null)
        return "";
      else
        return name;
    }
    else
    {
      int qnameIndex = m_dataOrQName.elementAt(makeNodeIdentity(nodeHandle));

      if (qnameIndex < 0)
      {
        qnameIndex = -qnameIndex;
        qnameIndex = m_data.elementAt(qnameIndex);
      }

      return m_valuesOrPrefixes.indexToString(qnameIndex);
    }
  }

  /**
   *     5. [specified] A flag indicating whether this attribute was actually
   *        specified in the start-tag of its element, or was defaulted from the
   *        DTD.
   *
   * @param the attribute handle
   *
   * @param attributeHandle Must be a valid handle to an attribute node.
   * @return <code>true</code> if the attribute was specified;
   *         <code>false</code> if it was defaulted.
   */
  public boolean isAttributeSpecified(int attributeHandle)
  {

    // I'm not sure if I want to do anything with this...
    return true;  // ??
  }

  /**
   *   A document type declaration information item has the following properties:
   *
   *     1. [system identifier] The system identifier of the external subset, if
   *        it exists. Otherwise this property has no value.
   *
   * @return the system identifier String object, or null if there is none.
   */
  public String getDocumentTypeDeclarationSystemIdentifier()
  {

    /** @todo: implement this org.apache.xml.dtm.DTMDefaultBase abstract method */
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));//"Not yet supported!");

    return null;
  }

  /**
   * Get the next node identity value in the list, and call the iterator
   * if it hasn't been added yet.
   *
   * @param identity The node identity (index).
   * @return identity+1, or DTM.NULL.
   */
  protected int getNextNodeIdentity(int identity)
  {

    identity += 1;

    while (identity >= m_size)
    {
      if (null == m_incrementalSAXSource)
        return DTM.NULL;

      nextNode();
    }

    return identity;
  }

  /**
   * Directly create SAX parser events from a subtree.
   *
   * @param nodeHandle The node ID.
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchToEvents(int nodeHandle, org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {

    DTMTreeWalker treeWalker = m_walker;
    ContentHandler prevCH = treeWalker.getcontentHandler();

    if (null != prevCH)
    {
      treeWalker = new DTMTreeWalker();
    }

    treeWalker.setcontentHandler(ch);
    treeWalker.setDTM(this);

    try
    {
      treeWalker.traverse(nodeHandle);
    }
    finally
    {
      treeWalker.setcontentHandler(null);
    }
  }

  /**
   * Get the number of nodes that have been added.
   *
   * @return The number of that are currently in the tree.
   */
  protected int getNumberOfNodes()
  {
    return m_size;
  }

  /**
   * This method should try and build one or more nodes in the table.
   *
   * @return The true if a next node is found or false if
   *         there are no more nodes.
   */
  protected boolean nextNode()
  {

    if (null == m_incrementalSAXSource)
      return false;

    if (m_endDocumentOccured)
    {
      clearCoRoutine();

      return false;
    }

    Object gotMore = m_incrementalSAXSource.deliverMoreNodes(true);

    // gotMore may be a Boolean (TRUE if still parsing, FALSE if
    // EOF) or an exception if IncrementalSAXSource malfunctioned
    // (code error rather than user error).
    //
    // %REVIEW% Currently the ErrorHandlers sketched herein are
    // no-ops, so I'm going to initially leave this also as a
    // no-op.
    if (!(gotMore instanceof Boolean))
    {
      if(gotMore instanceof RuntimeException)
      {
        throw (RuntimeException)gotMore;
      }
      else if(gotMore instanceof Exception)
      {
        throw new WrappedRuntimeException((Exception)gotMore);
      }
      // for now...
      clearCoRoutine();

      return false;

      // %TBD% 
    }

    if (gotMore != Boolean.TRUE)
    {

      // EOF reached without satisfying the request
      clearCoRoutine();  // Drop connection, stop trying

      // %TBD% deregister as its listener?
    }

    return true;
  }

  /**
   * Bottleneck determination of text type.
   *
   * @param type oneof DTM.XXX_NODE.
   *
   * @return true if this is a text or cdata section.
   */
  private final boolean isTextType(int type)
  {
    return (DTM.TEXT_NODE == type || DTM.CDATA_SECTION_NODE == type);
  }

//    /**
//     * Ensure that the size of the information arrays can hold another entry
//     * at the given index.
//     *
//     * @param on exit from this function, the information arrays sizes must be
//     * at least index+1.
//     *
//     * NEEDSDOC @param index
//     */
//    protected void ensureSize(int index)
//    {
//          // dataOrQName is an SuballocatedIntVector and hence self-sizing.
//          // But DTMDefaultBase may need fixup.
//        super.ensureSize(index);
//    }

  /**
   * Construct the node map from the node.
   *
   * @param type raw type ID, one of DTM.XXX_NODE.
   * @param expandedTypeID The expended type ID.
   * @param parentIndex The current parent index.
   * @param previousSibling The previous sibling index.
   * @param dataOrPrefix index into m_data table, or string handle.
   * @param canHaveFirstChild true if the node can have a first child, false
   *                          if it is atomic.
   *
   * @return The index identity of the node that was added.
   */
  protected int addNode(int type, int expandedTypeID,
                        int parentIndex, int previousSibling,
                        int dataOrPrefix, boolean canHaveFirstChild)
  {
    // Common to all nodes:
    int nodeIndex = m_size++;

    // Have we overflowed a DTM Identity's addressing range?
    if(m_dtmIdent.size() == (nodeIndex>>>DTMManager.IDENT_DTM_NODE_BITS))
    {
      try
      {
        if(m_mgr==null)
          throw new ClassCastException();
                                
                                // Handle as Extended Addressing
        DTMManagerDefault mgrD=(DTMManagerDefault)m_mgr;
        int id=mgrD.getFirstFreeDTMID();
        mgrD.addDTM(this,id,nodeIndex);
        m_dtmIdent.addElement(id<<DTMManager.IDENT_DTM_NODE_BITS);
      }
      catch(ClassCastException e)
      {
        // %REVIEW% Wrong error message, but I've been told we're trying
        // not to add messages right not for I18N reasons.
        // %REVIEW% Should this be a Fatal Error?
        error(XSLMessages.createMessage(XSLTErrorResources.ER_NO_DTMIDS_AVAIL, null));//"No more DTM IDs are available";
      }
    }

    m_firstch.addElement(canHaveFirstChild ? NOTPROCESSED : DTM.NULL);
    m_nextsib.addElement(NOTPROCESSED);
    m_prevsib.addElement(previousSibling);
    m_parent.addElement(parentIndex);
    m_exptype.addElement(expandedTypeID);
    m_dataOrQName.addElement(dataOrPrefix);    

    if (DTM.NULL != previousSibling)
      m_nextsib.setElementAt(nodeIndex,previousSibling);

    // Note that nextSibling is not processed until charactersFlush()
    // is called, to handle successive characters() events.

    // Special handling by type: Declare namespaces, attach first child
    switch(type)
    {
    case DTM.NAMESPACE_NODE:
      declareNamespaceInContext(parentIndex,nodeIndex);
      break;
    case DTM.ATTRIBUTE_NODE:
      break;
    default:
      if (DTM.NULL != parentIndex &&
	  NOTPROCESSED == m_firstch.elementAt(parentIndex))
        m_firstch.setElementAt(nodeIndex,parentIndex);
      break;
    }

    return nodeIndex;
  }

  /**
   * Given a node handle, return its node value. This is mostly
   * as defined by the DOM, but may ignore some conveniences.
   * <p>
   *
   * @param nodeHandle The node id.
   * @return String Value of this node, or null if not
   * meaningful for this node type.
   */
  public String getNodeValue(int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);
    int type = _type(identity);

    if (isTextType(type))
    {
      int dataIndex = _dataOrQName(identity);
      int offset = m_data.elementAt(dataIndex);
      int length = m_data.elementAt(dataIndex + 1);

      // %OPT% We should cache this, I guess.
      return m_chars.getString(offset, length);
    }
    else if (DTM.ELEMENT_NODE == type || DTM.DOCUMENT_FRAGMENT_NODE == type
             || DTM.DOCUMENT_NODE == type)
    {
      return null;
    }
    else
    {
      int dataIndex = _dataOrQName(identity);

      if (dataIndex < 0)
      {
        dataIndex = -dataIndex;
        dataIndex = m_data.elementAt(dataIndex + 1);
      }

      return m_valuesOrPrefixes.indexToString(dataIndex);
    }
  }

  /**
   * Given a node handle, return its XPath-style localname.
   * (As defined in Namespaces, this is the portion of the name after any
   * colon character).
   *
   * @param nodeHandle the id of the node.
   * @return String Local name of this node.
   */
  public String getLocalName(int nodeHandle)
  {
    return m_expandedNameTable.getLocalName(_exptype(makeNodeIdentity(nodeHandle)));
  }

  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed
   * entity with the specified name in the same document as the context
   * node (see [3.3 Unparsed Entities]). It returns the empty string if
   * there is no such entity.
   * <p>
   * XML processors may choose to use the System Identifier (if one
   * is provided) to resolve the entity, rather than the URI in the
   * Public Identifier. The details are dependent on the processor, and
   * we would have to support some form of plug-in resolver to handle
   * this properly. Currently, we simply return the System Identifier if
   * present, and hope that it a usable URI or that our caller can
   * map it to one.
   * TODO: Resolve Public Identifiers... or consider changing function name.
   * <p>
   * If we find a relative URI
   * reference, XML expects it to be resolved in terms of the base URI
   * of the document. The DOM doesn't do that for us, and it isn't
   * entirely clear whether that should be done here; currently that's
   * pushed up to a higher level of our application. (Note that DOM Level
   * 1 didn't store the document's base URI.)
   * TODO: Consider resolving Relative URIs.
   * <p>
   * (The DOM's statement that "An XML processor may choose to
   * completely expand entities before the structure model is passed
   * to the DOM" refers only to parsed entities, not unparsed, and hence
   * doesn't affect this function.)
   *
   * @param name A string containing the Entity Name of the unparsed
   * entity.
   *
   * @return String containing the URI of the Unparsed Entity, or an
   * empty string if no such entity exists.
   */
  public String getUnparsedEntityURI(String name)
  {

    String url = "";

    if (null == m_entities)
      return url;

    int n = m_entities.size();

    for (int i = 0; i < n; i += ENTITY_FIELDS_PER)
    {
      String ename = (String) m_entities.elementAt(i + ENTITY_FIELD_NAME);

      if (null != ename && ename.equals(name))
      {
        String nname = (String) m_entities.elementAt(i
                         + ENTITY_FIELD_NOTATIONNAME);

        if (null != nname)
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
          url = (String) m_entities.elementAt(i + ENTITY_FIELD_SYSTEMID);

          if (null == url)
          {
            url = (String) m_entities.elementAt(i + ENTITY_FIELD_PUBLICID);
          }
        }

        break;
      }
    }

    return url;
  }

  /**
   * Given a namespace handle, return the prefix that the namespace decl is
   * mapping.
   * Given a node handle, return the prefix used to map to the namespace.
   *
   * <p> %REVIEW% Are you sure you want "" for no prefix?  </p>
   * <p> %REVIEW-COMMENT% I think so... not totally sure. -sb  </p>
   *
   * @param nodeHandle the id of the node.
   * @return String prefix of this node's name, or "" if no explicit
   * namespace prefix was given.
   */
  public String getPrefix(int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);
    int type = getNodeType(identity);

    if (DTM.ELEMENT_NODE == type)
    {
      int prefixIndex = _dataOrQName(identity);

      if (0 == prefixIndex)
        return "";
      else
      {
        String qname = m_valuesOrPrefixes.indexToString(prefixIndex);

        return getPrefix(qname, null);
      }
    }
    else if (DTM.ATTRIBUTE_NODE == type)
    {
      int prefixIndex = _dataOrQName(identity);

      if (prefixIndex < 0)
      {
        prefixIndex = m_data.elementAt(-prefixIndex);

        String qname = m_valuesOrPrefixes.indexToString(prefixIndex);

        return getPrefix(qname, null);
      }
    }

    return "";
  }

  /**
   * Retrieves an attribute node by by qualified name and namespace URI.
   *
   * @param nodeHandle int Handle of the node upon which to look up this attribute..
   * @param namespaceURI The namespace URI of the attribute to
   *   retrieve, or null.
   * @param name The local name of the attribute to
   *   retrieve.
   * @return The attribute node handle with the specified name (
   *   <code>nodeName</code>) or <code>DTM.NULL</code> if there is no such
   *   attribute.
   */
  public int getAttributeNode(int nodeHandle, String namespaceURI,
                              String name)
  {

    for (int attrH = getFirstAttribute(nodeHandle); DTM.NULL != attrH;
            attrH = getNextAttribute(attrH))
    {
      String attrNS = getNamespaceURI(attrH);
      String attrName = getLocalName(attrH);
      boolean nsMatch = namespaceURI == attrNS
                        || (namespaceURI != null
                            && namespaceURI.equals(attrNS));

      if (nsMatch && name.equals(attrName))
        return attrH;
    }

    return DTM.NULL;
  }

  /**
   * Return the public identifier of the external subset,
   * normalized as described in 4.2.2 External Entities [XML]. If there is
   * no external subset or if it has no public identifier, this property
   * has no value.
   *
   * @param the document type declaration handle
   *
   * @return the public identifier String object, or null if there is none.
   */
  public String getDocumentTypeDeclarationPublicIdentifier()
  {

    /** @todo: implement this org.apache.xml.dtm.DTMDefaultBase abstract method */
    error(XSLMessages.createMessage(XSLTErrorResources.ER_METHOD_NOT_SUPPORTED, null));//"Not yet supported!");

    return null;
  }

  /**
   * Given a node handle, return its DOM-style namespace URI
   * (As defined in Namespaces, this is the declared URI which this node's
   * prefix -- or default in lieu thereof -- was mapped to.)
   *
   * <p>%REVIEW% Null or ""? -sb</p>
   *
   * @param nodeHandle the id of the node.
   * @return String URI value of this node's namespace, or null if no
   * namespace was resolved.
   */
  public String getNamespaceURI(int nodeHandle)
  {

    return m_expandedNameTable.getNamespace(_exptype(makeNodeIdentity(nodeHandle)));
  }

  /**
   * Get the string-value of a node as a String object
   * (see http://www.w3.org/TR/xpath#data-model
   * for the definition of a node's string-value).
   *
   * @param nodeHandle The node ID.
   *
   * @return A string object that represents the string-value of the given node.
   */
  public XMLString getStringValue(int nodeHandle)
  {

    int identity = makeNodeIdentity(nodeHandle);
    int type = _type(identity);

    if (isTextType(type))
    {
      int dataIndex = _dataOrQName(identity);
      int offset = m_data.elementAt(dataIndex);
      int length = m_data.elementAt(dataIndex + 1);

      return m_xstrf.newstr(m_chars, offset, length);
    }
    else
    {
      int firstChild = _firstch(identity);

      if (DTM.NULL != firstChild)
      {
        int offset = -1;
        int length = 0;
        int level = _level(identity);

        identity = firstChild;

        while (DTM.NULL != identity && (_level(identity) > level))
        {
          type = _type(identity);

          if (isTextType(type))
          {
            int dataIndex = _dataOrQName(identity);

            if (-1 == offset)
            {
              offset = m_data.elementAt(dataIndex);
            }

            length += m_data.elementAt(dataIndex + 1);
          }

          identity = getNextNodeIdentity(identity);
        }

        if (length > 0)
        {
          return m_xstrf.newstr(m_chars, offset, length);
        }
      }
      else if(type != DTM.ELEMENT_NODE)
      {
        int dataIndex = _dataOrQName(identity);

        if (dataIndex < 0)
        {
          dataIndex = -dataIndex;
          dataIndex = m_data.elementAt(dataIndex + 1);
        }
        return m_xstrf.newstr(m_valuesOrPrefixes.indexToString(dataIndex));
      }
    }

    return m_xstrf.emptystr();
  }

  /**
   * Returns the <code>Element</code> whose <code>ID</code> is given by
   * <code>elementId</code>. If no such element exists, returns
   * <code>DTM.NULL</code>. Behavior is not defined if more than one element
   * has this <code>ID</code>. Attributes (including those
   * with the name "ID") are not of type ID unless so defined by DTD/Schema
   * information available to the DTM implementation.
   * Implementations that do not know whether attributes are of type ID or
   * not are expected to return <code>DTM.NULL</code>.
   *
   * <p>%REVIEW% Presumably IDs are still scoped to a single document,
   * and this operation searches only within a single document, right?
   * Wouldn't want collisions between DTMs in the same process.</p>
   *
   * @param elementId The unique <code>id</code> value for an element.
   * @return The handle of the matching element.
   */
  public int getElementById(String elementId)
  {

    Integer intObj;
    boolean isMore = true;

    do
    {
      intObj = (Integer) m_idAttributes.get(elementId);

      if (null != intObj)
        return makeNodeHandle(intObj.intValue());

      if (!isMore || m_endDocumentOccured)
        break;

      isMore = nextNode();
    }
    while (null == intObj);

    return DTM.NULL;
  }

  /**
   * Get a prefix either from the qname or from the uri mapping, or just make
   * one up!
   *
   * @param qname The qualified name, which may be null.
   * @param uri The namespace URI, which may be null.
   *
   * @return The prefix if there is one, or null.
   */
  private String getPrefix(String qname, String uri)
  {

    String prefix;
    int uriIndex = -1;

    if (null != uri && uri.length() > 0)
    {

      do
      {
        uriIndex = m_prefixMappings.indexOf(uri, ++uriIndex);
      } while ( (uriIndex & 0x01) == 0);

      if (uriIndex >= 0)
      {
        prefix = (String) m_prefixMappings.elementAt(uriIndex - 1);
      }
      else if (null != qname)
      {
        int indexOfNSSep = qname.indexOf(':');

        if (qname.equals("xmlns"))
          prefix = "";
        else if (qname.startsWith("xmlns:"))
          prefix = qname.substring(indexOfNSSep + 1);
        else
          prefix = (indexOfNSSep > 0)
                   ? qname.substring(0, indexOfNSSep) : null;
      }
      else
      {
        prefix = null;  // ??
      }
    }
    else if (null != qname)
    {
      int indexOfNSSep = qname.indexOf(':');

      if (qname.equals("xmlns"))
        prefix = "";
      else if (qname.startsWith("xmlns:"))
        prefix = qname.substring(indexOfNSSep + 1);
      else
        prefix = (indexOfNSSep > 0) ? qname.substring(0, indexOfNSSep) : null;
    }
    else
    {
      prefix = null;
    }

    return prefix;
  }

  /**
   * Set an ID string to node association in the ID table.
   *
   * @param id The ID string.
   * @param elem The associated element handle.
   */
  public void setIDAttribute(String id, int elem)
  {
    m_idAttributes.put(id, new Integer(elem));
  }

  /**
   * Check whether accumulated text should be stripped; if not,
   * append the appropriate flavor of text/cdata node.
   */
  protected void charactersFlush()
  {

    if (m_textPendingStart >= 0)  // -1 indicates no-text-in-progress
    {
      int length = m_chars.size() - m_textPendingStart;
      boolean doStrip = false;

      if (getShouldStripWhitespace())
      {
        doStrip = m_chars.isWhitespace(m_textPendingStart, length);
      }

      if (doStrip)
        m_chars.setLength(m_textPendingStart);  // Discard accumulated text
      else
      {
        int exName = m_expandedNameTable.getExpandedTypeID(DTM.TEXT_NODE);
        int dataIndex = m_data.size();

        m_previous = addNode(m_coalescedTextType, exName,
                             m_parents.peek(), m_previous, dataIndex, false);

        m_data.addElement(m_textPendingStart);
        m_data.addElement(length);
      }

      // Reset for next text block
      m_textPendingStart = -1;
      m_textType = m_coalescedTextType = DTM.TEXT_NODE;
    }
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the EntityResolver interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Resolve an external entity.
   *
   * <p>Always return null, so that the parser will use the system
   * identifier provided in the XML document.  This method implements
   * the SAX default behaviour: application writers can override it
   * in a subclass to do special translations such as catalog lookups
   * or URI redirection.</p>
   *
   * @param publicId The public identifer, or null if none is
   *                 available.
   * @param systemId The system identifier provided in the XML
   *                 document.
   * @return The new input source, or null to require the
   *         default behaviour.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.EntityResolver#resolveEntity
   *
   * @throws SAXException
   */
  public InputSource resolveEntity(String publicId, String systemId)
          throws SAXException
  {
    return null;
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of DTDHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Receive notification of a notation declaration.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass if they wish to keep track of the notations
   * declared in a document.</p>
   *
   * @param name The notation name.
   * @param publicId The notation public identifier, or null if not
   *                 available.
   * @param systemId The notation system identifier.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.DTDHandler#notationDecl
   *
   * @throws SAXException
   */
  public void notationDecl(String name, String publicId, String systemId)
          throws SAXException
  {

    // no op
  }

  /**
   * Receive notification of an unparsed entity declaration.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to keep track of the unparsed entities
   * declared in a document.</p>
   *
   * @param name The entity name.
   * @param publicId The entity public identifier, or null if not
   *                 available.
   * @param systemId The entity system identifier.
   * @param notationName The name of the associated notation.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   *
   * @throws SAXException
   */
  public void unparsedEntityDecl(
          String name, String publicId, String systemId, String notationName)
            throws SAXException
  {

    if (null == m_entities)
    {
      m_entities = new Vector();
    }

    try
    {
      systemId = SystemIDResolver.getAbsoluteURI(systemId,
                                                 getDocumentBaseURI());
    }
    catch (Exception e)
    {
      throw new org.xml.sax.SAXException(e);
    }

    //  private static final int ENTITY_FIELD_PUBLICID = 0;
    m_entities.addElement(publicId);

    //  private static final int ENTITY_FIELD_SYSTEMID = 1;
    m_entities.addElement(systemId);

    //  private static final int ENTITY_FIELD_NOTATIONNAME = 2;
    m_entities.addElement(notationName);

    //  private static final int ENTITY_FIELD_NAME = 3;
    m_entities.addElement(name);
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of ContentHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Receive a Locator object for document events.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass if they wish to store the locator for use
   * with other document events.</p>
   *
   * @param locator A locator for all SAX document events.
   * @see org.xml.sax.ContentHandler#setDocumentLocator
   * @see org.xml.sax.Locator
   */
  public void setDocumentLocator(Locator locator)
  {
    m_locator = locator;
  }

  /**
   * Receive notification of the beginning of the document.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the beginning
   * of a document (such as allocating the root node of a tree or
   * creating an output file).</p>
   *
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startDocument
   */
  public void startDocument() throws SAXException
  {
    if (DEBUG)
      System.out.println("startDocument");

		
    int doc = addNode(DTM.DOCUMENT_NODE,
                      m_expandedNameTable.getExpandedTypeID(DTM.DOCUMENT_NODE),
                      DTM.NULL, DTM.NULL, 0, true);
		
    m_parents.push(doc);
    m_previous = DTM.NULL;
				
    m_contextIndexes.push(m_prefixMappings.size());  // for the next element.
  }

  /**
   * Receive notification of the end of the document.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the end
   * of a document (such as finalising a tree or closing an output
   * file).</p>
   *
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endDocument
   */
  public void endDocument() throws SAXException
  {
    if (DEBUG)
      System.out.println("endDocument");

		charactersFlush();

    m_nextsib.setElementAt(NULL,0);

    if (m_firstch.elementAt(0) == NOTPROCESSED)
      m_firstch.setElementAt(NULL,0);

    if (DTM.NULL != m_previous)
      m_nextsib.setElementAt(DTM.NULL,m_previous);

    m_parents = null;
    m_prefixMappings = null;
    m_contextIndexes = null;

    m_endDocumentOccured = true;
  }

  /**
   * Receive notification of the start of a Namespace mapping.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the start of
   * each Namespace prefix scope (such as storing the prefix mapping).</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI mapped to the prefix.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startPrefixMapping
   */
  public void startPrefixMapping(String prefix, String uri)
          throws SAXException
  {

    if (DEBUG)
      System.out.println("startPrefixMapping: prefix: " + prefix + ", uri: "
                         + uri);

    if(null == prefix)
      prefix = "";
    m_prefixMappings.addElement(prefix);  // JDK 1.1.x compat -sc
    m_prefixMappings.addElement(uri);  // JDK 1.1.x compat -sc
  }

  /**
   * Receive notification of the end of a Namespace mapping.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the end of
   * each prefix mapping.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endPrefixMapping
   */
  public void endPrefixMapping(String prefix) throws SAXException
  {
    if (DEBUG)
      System.out.println("endPrefixMapping: prefix: " + prefix);

    if(null == prefix)
      prefix = "";

    int index = m_contextIndexes.peek() - 1;

    do
    {
      index = m_prefixMappings.indexOf(prefix, ++index);
    } while ( (index >= 0) && ((index & 0x01) == 0x01) );


    if (index > -1)
    {
      m_prefixMappings.setElementAt("%@$#^@#", index);
      m_prefixMappings.setElementAt("%@$#^@#", index + 1);
    }

    // no op
  }

  /**
   * Check if a declaration has already been made for a given prefix.
   *
   * @param prefix non-null prefix string.
   *
   * @return true if the declaration has already been declared in the
   *         current context.
   */
  protected boolean declAlreadyDeclared(String prefix)
  {

    int startDecls = m_contextIndexes.peek();
    java.util.Vector prefixMappings = m_prefixMappings;
    int nDecls = prefixMappings.size();

    for (int i = startDecls; i < nDecls; i += 2)
    {
      String prefixDecl = (String) prefixMappings.elementAt(i);

      if (prefixDecl == null)
        continue;

      if (prefixDecl.equals(prefix))
        return true;
    }

    return false;
  }
	
	boolean m_pastFirstElement=false;

  /**
   * Receive notification of the start of an element.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the start of
   * each element (such as allocating a new tree node or writing
   * output to a file).</p>
   *
   * @param name The element type name.
   *
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param qName The qualified name (with prefix), or the
   *        empty string if qualified names are not available.
   * @param attributes The specified or defaulted attributes.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#startElement
   */
  public void startElement(
          String uri, String localName, String qName, Attributes attributes)
            throws SAXException
  {
   if (DEBUG)
	 {
      System.out.println("startElement: uri: " + uri + ", localname: "
												 + localName + ", qname: "+qName+", atts: " + attributes);

			boolean DEBUG_ATTRS=true;
			if(DEBUG_ATTRS & attributes!=null)
			{
				int n = attributes.getLength();
				if(n==0)
					System.out.println("\tempty attribute list");
				else for (int i = 0; i < n; i++)
					System.out.println("\t attr: uri: " + attributes.getURI(i) +
														 ", localname: " + attributes.getLocalName(i) +
														 ", qname: " + attributes.getQName(i) +
														 ", type: " + attributes.getType(i) +
														 ", value: " + attributes.getValue(i)
														 );
			}
	 }
		
    charactersFlush();

    int exName = m_expandedNameTable.getExpandedTypeID(uri, localName, DTM.ELEMENT_NODE);
    String prefix = getPrefix(qName, uri);
    int prefixIndex = (null != prefix)
                      ? m_valuesOrPrefixes.stringToIndex(qName) : 0;
    int elemNode = addNode(DTM.ELEMENT_NODE, exName,
                           m_parents.peek(), m_previous, prefixIndex, true);

    indexNode(exName, elemNode);
    
    m_parents.push(elemNode);

    int startDecls = m_contextIndexes.peek();
    int nDecls = m_prefixMappings.size();
    int prev = DTM.NULL;

    if(!m_pastFirstElement)
    {
      // SPECIAL CASE: Implied declaration at root element
      prefix="xml";
      String declURL = "http://www.w3.org/XML/1998/namespace";
      exName = m_expandedNameTable.getExpandedTypeID(null, prefix, DTM.NAMESPACE_NODE);
      int val = m_valuesOrPrefixes.stringToIndex(declURL);
      prev = addNode(DTM.NAMESPACE_NODE, exName, elemNode,
                     prev, val, false);
      m_pastFirstElement=true;
    }
                        
    for (int i = startDecls; i < nDecls; i += 2)
    {
      prefix = (String) m_prefixMappings.elementAt(i);

      if (prefix == null)
        continue;

      String declURL = (String) m_prefixMappings.elementAt(i + 1);

      exName = m_expandedNameTable.getExpandedTypeID(null, prefix, DTM.NAMESPACE_NODE);

      int val = m_valuesOrPrefixes.stringToIndex(declURL);

      prev = addNode(DTM.NAMESPACE_NODE, exName, elemNode,
                     prev, val, false);
    }

    int n = attributes.getLength();

    for (int i = 0; i < n; i++)
    {
      String attrUri = attributes.getURI(i);
      String attrQName = attributes.getQName(i);
      String valString = attributes.getValue(i);

      prefix = getPrefix(attrQName, attrUri);

      int nodeType;

      if ((null != attrQName)
              && (attrQName.equals("xmlns")
                  || attrQName.startsWith("xmlns:")))
      {
        if (declAlreadyDeclared(prefix))
          continue;  // go to the next attribute.

        nodeType = DTM.NAMESPACE_NODE;
      }
      else
      {
        nodeType = DTM.ATTRIBUTE_NODE;

        if (attributes.getType(i).equalsIgnoreCase("ID"))
          setIDAttribute(valString, elemNode);
      }
      
      // Bit of a hack... if somehow valString is null, stringToIndex will 
      // return -1, which will make things very unhappy.
      if(null == valString)
        valString = "";

      int val = m_valuesOrPrefixes.stringToIndex(valString);
      String attrLocalName = attributes.getLocalName(i);

      if (null != prefix)
      {
        
        prefixIndex = m_valuesOrPrefixes.stringToIndex(attrQName);

        int dataIndex = m_data.size();

        m_data.addElement(prefixIndex);
        m_data.addElement(val);

        val = -dataIndex;
      }

      exName = m_expandedNameTable.getExpandedTypeID(attrUri, attrLocalName, nodeType);
      prev = addNode(nodeType, exName, elemNode, prev, val,
                     false);
    }

    if (DTM.NULL != prev)
      m_nextsib.setElementAt(DTM.NULL,prev);

    if (null != m_wsfilter)
    {
      short wsv = m_wsfilter.getShouldStripSpace(makeNodeHandle(elemNode), this);
      boolean shouldStrip = (DTMWSFilter.INHERIT == wsv)
                            ? getShouldStripWhitespace()
                            : (DTMWSFilter.STRIP == wsv);

      pushShouldStripWhitespace(shouldStrip);
    }

    m_previous = DTM.NULL;

    m_contextIndexes.push(m_prefixMappings.size());  // for the children.
  }

  /**
   * Receive notification of the end of an element.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions at the end of
   * each element (such as finalising a tree node or writing
   * output to a file).</p>
   *
   * @param name The element type name.
   * @param attributes The specified or defaulted attributes.
   *
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param qName The qualified XML 1.0 name (with prefix), or the
   *        empty string if qualified names are not available.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#endElement
   */
  public void endElement(String uri, String localName, String qName)
          throws SAXException
  {
   if (DEBUG)
      System.out.println("endElement: uri: " + uri + ", localname: "
												 + localName + ", qname: "+qName);

    charactersFlush();

    // If no one noticed, startPrefixMapping is a drag.
    // Pop the context for the last child (the one pushed by startElement)
    m_prefixMappings.setSize(m_contextIndexes.pop());

    // Do it again for this one (the one pushed by the last endElement).
    m_prefixMappings.setSize(m_contextIndexes.pop());
    m_contextIndexes.push(m_prefixMappings.size());  // for the next element.

    int lastNode = m_previous;

    m_previous = m_parents.pop();

    if (NOTPROCESSED == m_firstch.elementAt(m_previous))
      m_firstch.setElementAt(DTM.NULL,m_previous);
    else if (DTM.NULL != lastNode)
      m_nextsib.setElementAt(DTM.NULL,lastNode);

    popShouldStripWhitespace();
  }

  /**
   * Receive notification of character data inside an element.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method to take specific actions for each chunk of character data
   * (such as adding the data to a node or buffer, or printing it to
   * a file).</p>
   *
   * @param ch The characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the
   *               character array.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#characters
   */
  public void characters(char ch[], int start, int length) throws SAXException
  {
   if (DEBUG)
      System.out.println("characters: " + new String(ch,start,length));

    if (m_textPendingStart == -1)  // First one in this block
    {
      m_textPendingStart = m_chars.size();
      m_coalescedTextType = m_textType;
    }

    m_chars.append(ch, start, length);

    // Type logic: If all adjacent text is CDATASections, the
    // concatentated text is treated as a single CDATASection (see
    // initialization above).  If any were ordinary Text, the whole
    // thing is treated as Text. This may be worth %REVIEW%ing.
    if (m_textType == DTM.TEXT_NODE)
      m_coalescedTextType = DTM.TEXT_NODE;
  }

  /**
   * Receive notification of ignorable whitespace in element content.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method to take specific actions for each chunk of ignorable
   * whitespace (such as adding data to a node or buffer, or printing
   * it to a file).</p>
   *
   * @param ch The whitespace characters.
   * @param start The start position in the character array.
   * @param length The number of characters to use from the
   *               character array.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#ignorableWhitespace
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws SAXException
  {
    // %OPT% We can probably take advantage of the fact that we know this 
    // is whitespace.
    characters(ch, start, length);
  }

  /**
   * Receive notification of a processing instruction.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions for each
   * processing instruction, such as setting status variables or
   * invoking other methods.</p>
   *
   * @param target The processing instruction target.
   * @param data The processing instruction data, or null if
   *             none is supplied.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   */
  public void processingInstruction(String target, String data)
          throws SAXException
  {
    if (DEBUG)
		 System.out.println("processingInstruction: target: " + target +", data: "+data);

    charactersFlush();

    int exName = m_expandedNameTable.getExpandedTypeID(null, target,
                                         DTM.PROCESSING_INSTRUCTION_NODE);
    int dataIndex = m_valuesOrPrefixes.stringToIndex(data);

    m_previous = addNode(DTM.PROCESSING_INSTRUCTION_NODE, exName,
                         m_parents.peek(), m_previous,
                         dataIndex, false);
  }

  /**
   * Receive notification of a skipped entity.
   *
   * <p>By default, do nothing.  Application writers may override this
   * method in a subclass to take specific actions for each
   * processing instruction, such as setting status variables or
   * invoking other methods.</p>
   *
   * @param name The name of the skipped entity.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ContentHandler#processingInstruction
   */
  public void skippedEntity(String name) throws SAXException
  {

    // %REVIEW% What should be done here?
    // no op
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the ErrorHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Receive notification of a parser warning.
   *
   * <p>The default implementation does nothing.  Application writers
   * may override this method in a subclass to take specific actions
   * for each warning, such as inserting the message in a log file or
   * printing it to the console.</p>
   *
   * @param e The warning information encoded as an exception.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ErrorHandler#warning
   * @see org.xml.sax.SAXParseException
   */
  public void warning(SAXParseException e) throws SAXException
  {

    // %REVIEW% Is there anyway to get the JAXP error listener here?
    System.err.println(e.getMessage());
  }

  /**
   * Receive notification of a recoverable parser error.
   *
   * <p>The default implementation does nothing.  Application writers
   * may override this method in a subclass to take specific actions
   * for each error, such as inserting the message in a log file or
   * printing it to the console.</p>
   *
   * @param e The warning information encoded as an exception.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ErrorHandler#warning
   * @see org.xml.sax.SAXParseException
   */
  public void error(SAXParseException e) throws SAXException
  {
    throw e;
  }

  /**
   * Report a fatal XML parsing error.
   *
   * <p>The default implementation throws a SAXParseException.
   * Application writers may override this method in a subclass if
   * they need to take specific actions for each fatal error (such as
   * collecting all of the errors into a single report): in any case,
   * the application must stop all regular processing when this
   * method is invoked, since the document is no longer reliable, and
   * the parser may no longer report parsing events.</p>
   *
   * @param e The error information encoded as an exception.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see org.xml.sax.ErrorHandler#fatalError
   * @see org.xml.sax.SAXParseException
   */
  public void fatalError(SAXParseException e) throws SAXException
  {
    throw e;
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the DeclHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Report an element type declaration.
   *
   * <p>The content model will consist of the string "EMPTY", the
   * string "ANY", or a parenthesised group, optionally followed
   * by an occurrence indicator.  The model will be normalized so
   * that all whitespace is removed,and will include the enclosing
   * parentheses.</p>
   *
   * @param name The element type name.
   * @param model The content model as a normalized string.
   * @throws SAXException The application may raise an exception.
   */
  public void elementDecl(String name, String model) throws SAXException
  {

    // no op
  }

  /**
   * Report an attribute type declaration.
   *
   * <p>Only the effective (first) declaration for an attribute will
   * be reported.  The type will be one of the strings "CDATA",
   * "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY",
   * "ENTITIES", or "NOTATION", or a parenthesized token group with
   * the separator "|" and all whitespace removed.</p>
   *
   * @param eName The name of the associated element.
   * @param aName The name of the attribute.
   * @param type A string representing the attribute type.
   * @param valueDefault A string representing the attribute default
   *        ("#IMPLIED", "#REQUIRED", or "#FIXED") or null if
   *        none of these applies.
   * @param value A string representing the attribute's default value,
   *        or null if there is none.
   * @throws SAXException The application may raise an exception.
   */
  public void attributeDecl(
          String eName, String aName, String type, String valueDefault, String value)
            throws SAXException
  {

    // no op
  }

  /**
   * Report an internal entity declaration.
   *
   * <p>Only the effective (first) declaration for each entity
   * will be reported.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @param value The replacement text of the entity.
   * @throws SAXException The application may raise an exception.
   * @see #externalEntityDecl
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void internalEntityDecl(String name, String value)
          throws SAXException
  {

    // no op
  }

  /**
   * Report a parsed external entity declaration.
   *
   * <p>Only the effective (first) declaration for each entity
   * will be reported.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @param publicId The declared public identifier of the entity, or
   *        null if none was declared.
   * @param systemId The declared system identifier of the entity.
   * @throws SAXException The application may raise an exception.
   * @see #internalEntityDecl
   * @see org.xml.sax.DTDHandler#unparsedEntityDecl
   */
  public void externalEntityDecl(
          String name, String publicId, String systemId) throws SAXException
  {

    // no op
  }

  ////////////////////////////////////////////////////////////////////
  // Implementation of the LexicalHandler interface.
  ////////////////////////////////////////////////////////////////////

  /**
   * Report the start of DTD declarations, if any.
   *
   * <p>Any declarations are assumed to be in the internal subset
   * unless otherwise indicated by a {@link #startEntity startEntity}
   * event.</p>
   *
   * <p>Note that the start/endDTD events will appear within
   * the start/endDocument events from ContentHandler and
   * before the first startElement event.</p>
   *
   * @param name The document type name.
   * @param publicId The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param systemId The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   * @throws SAXException The application may raise an
   *            exception.
   * @see #endDTD
   * @see #startEntity
   */
  public void startDTD(String name, String publicId, String systemId)
          throws SAXException
  {

    m_insideDTD = true;
  }

  /**
   * Report the end of DTD declarations.
   *
   * @throws SAXException The application may raise an exception.
   * @see #startDTD
   */
  public void endDTD() throws SAXException
  {

    m_insideDTD = false;
  }

  /**
   * Report the beginning of an entity in content.
   *
   * <p><strong>NOTE:</entity> entity references in attribute
   * values -- and the start and end of the document entity --
   * are never reported.</p>
   *
   * <p>The start and end of the external DTD subset are reported
   * using the pseudo-name "[dtd]".  All other events must be
   * properly nested within start/end entity events.</p>
   *
   * <p>Note that skipped entities will be reported through the
   * {@link org.xml.sax.ContentHandler#skippedEntity skippedEntity}
   * event, which is part of the ContentHandler interface.</p>
   *
   * @param name The name of the entity.  If it is a parameter
   *        entity, the name will begin with '%'.
   * @throws SAXException The application may raise an exception.
   * @see #endEntity
   * @see org.xml.sax.ext.DeclHandler#internalEntityDecl
   * @see org.xml.sax.ext.DeclHandler#externalEntityDecl
   */
  public void startEntity(String name) throws SAXException
  {

    // no op
  }

  /**
   * Report the end of an entity.
   *
   * @param name The name of the entity that is ending.
   * @throws SAXException The application may raise an exception.
   * @see #startEntity
   */
  public void endEntity(String name) throws SAXException
  {

    // no op
  }

  /**
   * Report the start of a CDATA section.
   *
   * <p>The contents of the CDATA section will be reported through
   * the regular {@link org.xml.sax.ContentHandler#characters
   * characters} event.</p>
   *
   * @throws SAXException The application may raise an exception.
   * @see #endCDATA
   */
  public void startCDATA() throws SAXException
  {
    m_textType = DTM.CDATA_SECTION_NODE;
  }

  /**
   * Report the end of a CDATA section.
   *
   * @throws SAXException The application may raise an exception.
   * @see #startCDATA
   */
  public void endCDATA() throws SAXException
  {
    m_textType = DTM.TEXT_NODE;
  }

  /**
   * Report an XML comment anywhere in the document.
   *
   * <p>This callback will be used for comments inside or outside the
   * document element, including comments in the external DTD
   * subset (if read).</p>
   *
   * @param ch An array holding the characters in the comment.
   * @param start The starting position in the array.
   * @param length The number of characters to use from the array.
   * @throws SAXException The application may raise an exception.
   */
  public void comment(char ch[], int start, int length) throws SAXException
  {

    if (m_insideDTD)      // ignore comments if we're inside the DTD
      return;

    charactersFlush();

    int exName = m_expandedNameTable.getExpandedTypeID(DTM.COMMENT_NODE);

    // For now, treat comments as strings...  I guess we should do a 
    // seperate FSB buffer instead.
    int dataIndex = m_valuesOrPrefixes.stringToIndex(new String(ch, start,
                      length));

    m_previous = addNode(DTM.COMMENT_NODE, exName, 
                         m_parents.peek(), m_previous, dataIndex, false);
  }

  /**
   * Set a run time property for this DTM instance.
   *
   * @param property a <code>String</code> value
   * @param value an <code>Object</code> value
   */
  public void setProperty(String property, Object value)
  {
    if (property.equals(XalanProperties.SOURCE_LOCATION)) {
      if (!(value instanceof Boolean))
        throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_PROPERTY_VALUE_BOOLEAN, new Object[]{XalanProperties.SOURCE_LOCATION})); //"Value for property "
                                  // + XalanProperties.SOURCE_LOCATION
                                  // + " should be a Boolean instance");
      m_useSourceLocationProperty = ((Boolean)value).booleanValue();
      m_sourceSystemId = new StringVector();
      m_sourceLine = new IntVector();
      m_sourceColumn = new IntVector();
    }
  }

  public SourceLocator getSourceLocatorFor(int node)
  {
    if (m_useSourceLocationProperty)
    {
      node = node & ExpandedNameTable.MASK_NODEHANDLE;
      
      return new NodeLocator(null,
                             m_sourceSystemId.elementAt(node),
                             m_sourceLine.elementAt(node),
                             m_sourceColumn.elementAt(node));
    }
    return null;
  }
}
