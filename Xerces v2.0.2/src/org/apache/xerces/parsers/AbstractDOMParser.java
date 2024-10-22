/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.
 * All rights reserved.
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.parsers;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.dom.EntityReferenceImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.NotationImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.dom.ProcessingInstructionImpl;
import org.apache.xerces.impl.Constants;
// id types
import org.apache.xerces.xni.psvi.AttributePSVI;
import org.apache.xerces.impl.xs.psvi.XSAttributeDeclaration;
import org.apache.xerces.impl.dv.XSSimpleType;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.psvi.AttributePSVI;
import org.apache.xerces.xni.psvi.ElementPSVI;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import java.util.Stack;

/**
 * This is the base class of all DOM parsers. It implements the XNI
 * callback methods to create the DOM tree. After a successful parse of
 * an XML document, the DOM Document object can be queried using the
 * <code>getDocument</code> method. The actual pipeline is defined in
 * parser configuration.
 *
 * @author Arnaud Le Hors, IBM
 * @author Andy Clark, IBM
 * @author Elena Litani, IBM
 *
 * @version $Id: AbstractDOMParser.java,v 1.60 2002/06/19 16:48:47 elena Exp $
 */
public class AbstractDOMParser extends AbstractXMLDocumentParser{

    //
    // Constants
    //
                         
    // feature ids

    /** Feature id: namespace. */
    protected static final String NAMESPACES =
        Constants.SAX_FEATURE_PREFIX+Constants.NAMESPACES_FEATURE;

    /** Feature id: create entity ref nodes. */
    protected static final String CREATE_ENTITY_REF_NODES =
        Constants.XERCES_FEATURE_PREFIX + Constants.CREATE_ENTITY_REF_NODES_FEATURE;

    /** Feature id: include comments. */
    protected static final String INCLUDE_COMMENTS_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.INCLUDE_COMMENTS_FEATURE;

    /** Feature id: create cdata nodes. */
    protected static final String CREATE_CDATA_NODES_FEATURE =
        Constants.XERCES_FEATURE_PREFIX + Constants.CREATE_CDATA_NODES_FEATURE;

    /** Feature id: include ignorable whitespace. */
    protected static final String INCLUDE_IGNORABLE_WHITESPACE =
        Constants.XERCES_FEATURE_PREFIX + Constants.INCLUDE_IGNORABLE_WHITESPACE;

    /** Feature id: defer node expansion. */
    protected static final String DEFER_NODE_EXPANSION =
        Constants.XERCES_FEATURE_PREFIX + Constants.DEFER_NODE_EXPANSION_FEATURE;
    
    /** Expose XML Schema normalize value */
    protected static final String NORMALIZE_DATA = 
        Constants.XERCES_FEATURE_PREFIX + Constants.SCHEMA_NORMALIZED_VALUE;

    /** Recognized features. */
    private static final String[] RECOGNIZED_FEATURES = {
        NAMESPACES,
        CREATE_ENTITY_REF_NODES,
        INCLUDE_COMMENTS_FEATURE,
        CREATE_CDATA_NODES_FEATURE,
        INCLUDE_IGNORABLE_WHITESPACE,
        DEFER_NODE_EXPANSION,
        NORMALIZE_DATA,
    };

    // property ids

    /** Property id: document class name. */
    protected static final String DOCUMENT_CLASS_NAME =
        Constants.XERCES_PROPERTY_PREFIX + Constants.DOCUMENT_CLASS_NAME_PROPERTY;

    protected static final String  CURRENT_ELEMENT_NODE=  
        Constants.XERCES_PROPERTY_PREFIX + Constants.CURRENT_ELEMENT_NODE_PROPERTY;

    // protected static final String GRAMMAR_POOL = 
    // Constants.XERCES_PROPERTY_PREFIX + Constants.XMLGRAMMAR_POOL_PROPERTY;

    /** Recognized properties. */
    private static final String[] RECOGNIZED_PROPERTIES = {
        DOCUMENT_CLASS_NAME,
        CURRENT_ELEMENT_NODE,
    };

    // other

    /** Default document class name. */
    protected static final String DEFAULT_DOCUMENT_CLASS_NAME =
        "org.apache.xerces.dom.DocumentImpl";

    protected static final String CORE_DOCUMENT_CLASS_NAME =
        "org.apache.xerces.dom.CoreDocumentImpl";

    // debugging

    private static final boolean DEBUG_EVENTS = false;
    private static final boolean DEBUG_BASEURI = false;

    //
    // Data
    //


    /** True if inside DTD. */
    protected boolean fInDTD;

    // features

    /** Create entity reference nodes. */
    protected boolean fCreateEntityRefNodes;

    /** Include ignorable whitespace. */
    protected boolean fIncludeIgnorableWhitespace;

    /** Include Comments. */
    protected boolean fIncludeComments;
                          
    /** Create cdata nodes. */
    protected boolean fCreateCDATANodes;

    /** Expose XML Schema schema_normalize_values via DOM*/
    protected boolean fNormalizeData = true;

    // dom information

    /** The document. */
    protected Document fDocument;

    /** The default Xerces document implementation, if used. */
    protected CoreDocumentImpl fDocumentImpl;

    /** The document class name to use. */
    protected String  fDocumentClassName;

    /** The document type node. */
    protected DocumentType fDocumentType;

    /** Current node. */
    protected Node fCurrentNode;
    protected CDATASection fCurrentCDATASection;
    protected EntityImpl fCurrentEntityDecl;
    protected int fDeferredEntityDecl;

    /** Character buffer */
    protected final StringBuffer fStringBuffer = new StringBuffer(50);

    // internal subset

    /** Internal subset buffer. */
    protected StringBuffer fInternalSubset;

    // deferred expansion data

    protected boolean              fDeferNodeExpansion;
    protected boolean              fNamespaceAware;
    protected DeferredDocumentImpl fDeferredDocumentImpl;
    protected int                  fDocumentIndex;
    protected int                  fDocumentTypeIndex;
    protected int                  fCurrentNodeIndex;
    protected int                  fCurrentCDATASectionIndex;

    // state

    /** True if inside DTD external subset. */
    protected boolean fInDTDExternalSubset;

    /** True if inside document. */
    protected boolean fInDocument;

    /** True if inside CDATA section. */
    protected boolean fInCDATASection;

    /** True if saw the first chunk of characters*/
    protected boolean fFirstChunk = false;


    /** Base uri stack*/
    protected Stack fBaseURIStack = new Stack();

    // data

    /** Attribute QName. */
    private QName fAttrQName = new QName();

    //
    // Constructors
    //

    /** Default constructor. */
    protected AbstractDOMParser(XMLParserConfiguration config) {
        
        super(config);


        // add recognized features
        fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);

        // set default values
        fConfiguration.setFeature(CREATE_ENTITY_REF_NODES, true);
        fConfiguration.setFeature(INCLUDE_IGNORABLE_WHITESPACE, true);
        fConfiguration.setFeature(DEFER_NODE_EXPANSION, true);
        fConfiguration.setFeature(INCLUDE_COMMENTS_FEATURE, true);
        fConfiguration.setFeature(CREATE_CDATA_NODES_FEATURE, true);

        // add recognized properties
        fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);

        // set default values
        fConfiguration.setProperty(DOCUMENT_CLASS_NAME,
                                   DEFAULT_DOCUMENT_CLASS_NAME);

    } // <init>(XMLParserConfiguration)

    /**
     * This method retreives the name of current document class.
     */
    protected String getDocumentClassName() {
        return fDocumentClassName;
    }

    /**
     * This method allows the programmer to decide which document
     * factory to use when constructing the DOM tree. However, doing
     * so will lose the functionality of the default factory. Also,
     * a document class other than the default will lose the ability
     * to defer node expansion on the DOM tree produced.
     *
     * @param documentClassName The fully qualified class name of the
     *                      document factory to use when constructing
     *                      the DOM tree.
     *
     * @see #getDocumentClassName
     * @see #DEFAULT_DOCUMENT_CLASS_NAME
     */
    protected void setDocumentClassName(String documentClassName) {

        // normalize class name
        if (documentClassName == null) {
            documentClassName = DEFAULT_DOCUMENT_CLASS_NAME;
        }

        // verify that this class exists and is of the right type
        try {
            Class _class = Class.forName(documentClassName);
            //if (!_class.isAssignableFrom(Document.class)) {
            if (!Document.class.isAssignableFrom(_class)) {
                // REVISIT: message
                throw new IllegalArgumentException("PAR002 Class, \"" +
                                                   documentClassName +
                                 "\", is not of type org.w3c.dom.Document.\n" +
                                                   documentClassName);
            }
        }
        catch (ClassNotFoundException e) {
            // REVISIT: message
            throw new IllegalArgumentException("PAR003 Class, \"" +
                                               documentClassName +
                                               "\", not found.\n" +
                                               documentClassName);
        }

        // set document class name
        fDocumentClassName = documentClassName;
        if (!documentClassName.equals(DEFAULT_DOCUMENT_CLASS_NAME)) {
            fDeferNodeExpansion = false;
        }

    } // setDocumentClassName(String)

    //
    // Public methods
    //

    /** Returns the DOM document object. */
    public Document getDocument() {
        return fDocument;
    } // getDocument():Document

    //
    // XMLDocumentParser methods
    //

    /**
     * Resets the parser state.
     *
     * @throws SAXException Thrown on initialization error.
     */
    public void reset() throws XNIException {
        super.reset();
        
        // get feature state
        fCreateEntityRefNodes =
            fConfiguration.getFeature(CREATE_ENTITY_REF_NODES);

        fIncludeIgnorableWhitespace =
            fConfiguration.getFeature(INCLUDE_IGNORABLE_WHITESPACE);

        fDeferNodeExpansion =  
            fConfiguration.getFeature(DEFER_NODE_EXPANSION);

        fNamespaceAware = fConfiguration.getFeature(NAMESPACES);

        fIncludeComments = fConfiguration.getFeature(INCLUDE_COMMENTS_FEATURE);

        fCreateCDATANodes = fConfiguration.getFeature(CREATE_CDATA_NODES_FEATURE);

        try {
            fNormalizeData = fConfiguration.getFeature(NORMALIZE_DATA);
        } catch (XMLConfigurationException x) {
            fNormalizeData = false;
        }

        // get property
        setDocumentClassName((String)
                             fConfiguration.getProperty(DOCUMENT_CLASS_NAME));

        // reset dom information
        fDocument = null;
        fDocumentImpl = null;
        fDocumentType = null;
        fDocumentTypeIndex = -1;
        fDeferredDocumentImpl = null;
        fCurrentNode = null;

        // reset string buffer
        fStringBuffer.setLength(0);

        // reset state information
        fInDocument = false;
        fInDTD = false;
        fInDTDExternalSubset = false;
        fInCDATASection = false;
        fFirstChunk = false;
        fCurrentCDATASection = null;
        fCurrentCDATASectionIndex = -1;
    } // reset()

    //
    // XMLDocumentHandler methods
    //

    /**
     * This method notifies the start of a general entity.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     * 
     * @param name     The name of the general entity.
     * @param identifier The resource identifier.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal entities or a document entity that is
     *                 parsed from a java.io.Reader).
     * @param augs     Additional information that may include infoset augmentations
     *                 
     * @exception XNIException Thrown by handler to signal an error.
     */
    public void startGeneralEntity(String name, 
                                   XMLResourceIdentifier identifier,
                                   String encoding, Augmentations augs) 
        throws XNIException {
        if (DEBUG_EVENTS) {        
            System.out.println("==>startGeneralEntity ("+name+")");
            if (DEBUG_BASEURI) {            
                System.out.println("   expandedSystemId( **baseURI): "+identifier.getExpandedSystemId());
                System.out.println("   baseURI:"+ identifier.getBaseSystemId());
            }
        }
        
        // Always create entity reference nodes to be able to recreate
        // entity as a part of doctype
         if (!fDeferNodeExpansion) {
            setCharacterData(true);
            EntityReference er = fDocument.createEntityReference(name);            
            
            if (fDocumentImpl != null) {            
                // REVISIT: baseURI/actualEncoding
                //         remove dependency on our implementation when DOM L3 is REC
                //

                EntityReferenceImpl erImpl =(EntityReferenceImpl)er; 
                // set base uri
                erImpl.setBaseURI(identifier.getExpandedSystemId());

                if (fDocumentType != null) {
                    // set actual encoding
                    NamedNodeMap entities = fDocumentType.getEntities();
                    fCurrentEntityDecl = (EntityImpl) entities.getNamedItem(name);
                    if (fCurrentEntityDecl != null) {
                        fCurrentEntityDecl.setActualEncoding(encoding);
                    }

                }
                // we don't need synchronization now, because entity ref will be
                // expanded anyway. Synch only needed when user creates entityRef node
                erImpl.needsSyncChildren(false);
            }

            fCurrentNode.appendChild(er);
            fCurrentNode = er;
         }
         else {

            int er =
               fDeferredDocumentImpl.createDeferredEntityReference(name, identifier.getExpandedSystemId());
            if (fDocumentTypeIndex != -1) {
                // find corresponding Entity decl
                int node = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
                while (node != -1) {
                    short nodeType = fDeferredDocumentImpl.getNodeType(node, false);
                    if (nodeType == Node.ENTITY_NODE) {
                        String nodeName =
                            fDeferredDocumentImpl.getNodeName(node, false);
                        if (nodeName.equals(name)) {
                            fDeferredEntityDecl = node;
                            fDeferredDocumentImpl.setActualEncoding(node, encoding);
                            break;
                        }
                    }
                    node = fDeferredDocumentImpl.getRealPrevSibling(node, false);
                }
            }
            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, er);
            fCurrentNodeIndex = er;
         }

    } // startGeneralEntity(String,XMLResourceIdentifier, Augmentations)

    /**
     * Notifies of the presence of a TextDecl line in an entity. If present,
     * this method will be called immediately following the startEntity call.
     * <p>
     * <strong>Note:</strong> This method will never be called for the
     * document entity; it is only called for external general entities
     * referenced in document content.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     * 
     * @param version  The XML version, or null if not specified.
     * @param encoding The IANA encoding name of the entity.
     * @param augs       Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (!fDeferNodeExpansion) {
            if (fCurrentEntityDecl != null) {
                fCurrentEntityDecl.setEncoding(encoding);
                fCurrentEntityDecl.setVersion(version);
            }
        }
        else {
            if (fDeferredEntityDecl !=-1) {
               fDeferredDocumentImpl.setEntityInfo(fDeferredEntityDecl, version, encoding);
            }
        }
    } // textDecl(String,String)

    /**
     * A comment.
     *
     * @param text The text in the comment.
     * @param augs       Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by application to signal an error.
     */
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (fInDTD) {        
            if (fInternalSubset != null && !fInDTDExternalSubset) {
                fInternalSubset.append("<!-- ");
                fInternalSubset.append(text.toString());
                fInternalSubset.append(" -->");
            }
            return;
        }
        if (!fIncludeComments) {
              return;
        }
        if (!fDeferNodeExpansion) {
            setCharacterData(false);
            Comment comment = fDocument.createComment(text.toString());
            fCurrentNode.appendChild(comment);
        }
        else {
            int comment =
                fDeferredDocumentImpl.createDeferredComment(text.toString());
            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, comment);
        }

    } // comment(XMLString)

    /**
     * A processing instruction. Processing instructions consist of a
     * target name and, optionally, text data. The data is only meaningful
     * to the application.
     * <p>
     * Typically, a processing instruction's data will contain a series
     * of pseudo-attributes. These pseudo-attributes follow the form of
     * element attributes but are <strong>not</strong> parsed or presented
     * to the application as anything other than text. The application is
     * responsible for parsing the data.
     *
     * @param target The target.
     * @param data   The data or null if none specified.
     * @param augs       Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {

        if (fInDTD) {        
            if (fInternalSubset != null && !fInDTDExternalSubset) {
                fInternalSubset.append("<?");
                fInternalSubset.append(target.toString());
                fInternalSubset.append(' ');
                fInternalSubset.append(data.toString());
                fInternalSubset.append("?>");

            }
            return;
        }

        if (DEBUG_EVENTS) {        
            System.out.println("==>processingInstruction ("+target+")");
        }
        if (!fDeferNodeExpansion) {
            setCharacterData(false);
            ProcessingInstruction pi =
                fDocument.createProcessingInstruction(target, data.toString());
            fCurrentNode.appendChild(pi);
        }
        else {
            int pi = fDeferredDocumentImpl.
                createDeferredProcessingInstruction(target, data.toString());
            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, pi);
        }

    } // processingInstruction(String,XMLString)

    /**
     * The start of the document.
     *
     * @param locator The system identifier of the entity if the entity
     *                 is external, null otherwise.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal entities or a document entity that is
     *                 parsed from a java.io.Reader).
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startDocument(XMLLocator locator, String encoding, Augmentations augs)
        throws XNIException {

        fInDocument = true;
        if (!fDeferNodeExpansion) {
            if (fDocumentClassName.equals(DEFAULT_DOCUMENT_CLASS_NAME)) {
                fDocument = new DocumentImpl();
                fDocumentImpl = (CoreDocumentImpl)fDocument;
                // REVISIT: when DOM Level 3 is REC rely on Document.support
                //          instead of specific class
                // set DOM error checking off
                fDocumentImpl.setStrictErrorChecking(false);
                // set actual encoding
                fDocumentImpl.setActualEncoding(encoding);
                // set documentURI
                fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
            }
            else {
                // use specified document class
                try {
                    Class documentClass = Class.forName(fDocumentClassName);
                    fDocument = (Document)documentClass.newInstance();

                    // if subclass of our own class that's cool too
                    Class defaultDocClass =
                        Class.forName(CORE_DOCUMENT_CLASS_NAME);
                    if (defaultDocClass.isAssignableFrom(documentClass)) {
                        fDocumentImpl = (CoreDocumentImpl)fDocument;
                        // REVISIT: when DOM Level 3 is REC rely on
                        //          Document.support instead of specific class
                        // set DOM error checking off
                        // set actual encoding
                        fDocumentImpl.setActualEncoding(encoding);
                        // set documentURI
                        if (locator != null) {
                            fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
                        }
                    }
                }
                catch (ClassNotFoundException e) {
                    // won't happen we already checked that earlier
                }
                catch (Exception e) {
                    // REVISIT: Localize this message.
                    throw new RuntimeException(
                                 "Failed to create document object of class: "
                                 + fDocumentClassName);
                }
            }
            fCurrentNode = fDocument;
        }
        else {
            fDeferredDocumentImpl = new DeferredDocumentImpl(fNamespaceAware);
            fDocument = fDeferredDocumentImpl;
            fDocumentIndex = fDeferredDocumentImpl.createDeferredDocument();
            // REVISIT: strict error checking is not implemented in deferred dom.
            //          Document.support instead of specific class

            // set actual encoding
            fDeferredDocumentImpl.setActualEncoding(encoding);
            // set documentURI
            fDeferredDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
            fCurrentNodeIndex = fDocumentIndex;

        }

    } // startDocument(String,String)

    /**
     * Notifies of the presence of an XMLDecl line in the document. If
     * present, this method will be called immediately following the
     * startDocument call.
     * 
     * @param version    The XML version.
     * @param encoding   The IANA encoding name of the document, or null if
     *                   not specified.
     * @param standalone The standalone value, or null if not specified.
     * @param augs       Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void xmlDecl(String version, String encoding, String standalone, 
                        Augmentations augs)
        throws XNIException {
        if (!fDeferNodeExpansion) {
            // REVISIT: when DOM Level 3 is REC rely on Document.support
            //          instead of specific class
            if (fDocumentImpl != null) {
                fDocumentImpl.setVersion(version);
                fDocumentImpl.setEncoding(encoding);
                fDocumentImpl.setStandalone("true".equals(standalone));
            }
        }
        else {
            fDeferredDocumentImpl.setVersion(version);
            fDeferredDocumentImpl.setEncoding(encoding);
            fDeferredDocumentImpl.setStandalone("true".equals(standalone));
        }
    } // xmlDecl(String,String,String)

    /**
     * Notifies of the presence of the DOCTYPE line in the document.
     *
     * @param rootElement The name of the root element.
     * @param publicId    The public identifier if an external DTD or null
     *                    if the external DTD is specified using SYSTEM.
     * @param systemId    The system identifier if an external DTD, null
     *                    otherwise.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void doctypeDecl(String rootElement,
                            String publicId, String systemId, Augmentations augs)
        throws XNIException {

        if (!fDeferNodeExpansion) {
            if (fDocumentImpl != null) {
                fDocumentType = fDocumentImpl.createDocumentType(
                                    rootElement, publicId, systemId);
                fCurrentNode.appendChild(fDocumentType);
            }
        }
        else {
            fDocumentTypeIndex = fDeferredDocumentImpl.
                createDeferredDocumentType(rootElement, publicId, systemId);
            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, fDocumentTypeIndex);
        }

    } // doctypeDecl(String,String,String)

    /**
     * The start of a namespace prefix mapping. This method will only be
     * called when namespace processing is enabled.
     * 
     * @param prefix The namespace prefix.
     * @param uri    The URI bound to the prefix.
     * @param augs   Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startPrefixMapping(String prefix, String uri, Augmentations augs)
        throws XNIException {
    } // startPrefixMapping(String,String)


    /**
     * The end of a namespace prefix mapping. This method will only be
     * called when namespace processing is enabled.
     *
     * @param prefix The namespace prefix.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
    } // endPrefixMapping(String)

    /**
     * The start of an element. If the document specifies the start element
     * by using an empty tag, then the startElement method will immediately
     * be followed by the endElement method, with no intervening methods.
     *
     * @param element    The name of the element.
     * @param attributes The element attributes.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>startElement ("+element.rawname+")");
        }
        if (!fDeferNodeExpansion) {
            Element el = createElementNode(element);
            int attrCount = attributes.getLength();
            for (int i = 0; i < attrCount; i++) {
                attributes.getName(i, fAttrQName);
                Attr attr = createAttrNode(fAttrQName);

                String attrValue = attributes.getValue(i);
                // REVISIT: consider moving this code to the XML Schema validator. 
                //          When PSVI and XML Schema component interfaces are finalized
                //          remove dependancy on *Impl class.
                AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem(Constants.ATTRIBUTE_PSVI);
                if (fNormalizeData) {
                    // If validation is not attempted, the SchemaNormalizedValue will be null. 
                    // We shouldn't take the normalized value in this case.
                    if (attrPSVI != null && attrPSVI.getValidationAttempted() == AttributePSVI.VALIDATION_FULL) {
                        attrValue = attrPSVI.getSchemaNormalizedValue();
                    }

                }
                
                attr.setValue(attrValue);
                el.setAttributeNode(attr);
                // NOTE: The specified value MUST be set after you set
                //       the node value because that turns the "specified"
                //       flag to "true" which may overwrite a "false"
                //       value from the attribute list. -Ac
                if (fDocumentImpl != null) {
                    AttrImpl attrImpl = (AttrImpl)attr;
                    boolean specified = attributes.isSpecified(i);
                    attrImpl.setSpecified(specified);
                    // Identifier registration
                    // REVISIT: try to retrieve XML Schema attribute declaration
                    //          we should try to modify psvi API to allows to 
                    //          check if id type 
                    XSAttributeDeclaration xsDecl = (XSAttributeDeclaration)((attrPSVI!=null)?attrPSVI.getAttributeDeclaration():null);
                    if (attributes.getType(i).equals("ID") || 
                        (xsDecl !=null && ((XSSimpleType)xsDecl.getTypeDefinition()).isIDType())) {
                        ((ElementImpl) el).setIdAttributeNode(attr);
                    }
                }
                // REVISIT: Handle entities in attribute value.
            }

            setCharacterData(false);
            fCurrentNode.appendChild(el);
            fCurrentNode = el;
        }
        else {
            int el =
                fDeferredDocumentImpl.createDeferredElement(fNamespaceAware ?
                                                            element.uri : null,
                                                            element.rawname);
            int attrCount = attributes.getLength();
            for (int i = 0; i < attrCount; i++) {
                String attrValue = attributes.getValue(i);
                // REVISIT: consider moving this code to the XML Schema validator. 
                //          When PSVI and XML Schema component interfaces are finalized
                //          remove dependancy on *Impl class.
                AttributePSVI attrPSVI = (AttributePSVI)attributes.getAugmentations(i).getItem(Constants.ATTRIBUTE_PSVI);
                
                if (fNormalizeData) {
                    // If validation is not attempted, the SchemaNormalizedValue will be null. 
                    // We shouldn't take the normalized value in this case.
                    if (attrPSVI != null && attrPSVI.getValidationAttempted() == AttributePSVI.VALIDATION_FULL) {
                        attrValue = attrPSVI.getSchemaNormalizedValue();
                    }

                }
                int attr = fDeferredDocumentImpl.setDeferredAttribute(el,
                                                    attributes.getQName(i),
                                                    attributes.getURI(i),
                                                    attrValue,
                                                    attributes.isSpecified(i));
                // identifier registration
                // REVISIT: try to retrieve XML Schema attribute declaration
                //          we should try to modify psvi API to allows to 
                //          check if id type 
                XSAttributeDeclaration xsDecl = (XSAttributeDeclaration)((attrPSVI!=null)?attrPSVI.getAttributeDeclaration():null);
                
                if (attributes.getType(i).equals("ID") || 
                    (xsDecl !=null && ((XSSimpleType)xsDecl.getTypeDefinition()).isIDType())) {
                    fDeferredDocumentImpl.setIdAttributeNode(el, attr);
                }
            }
            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, el);
            fCurrentNodeIndex = el;
        }
    } // startElement(QName,XMLAttributes)


    /**
     * An empty element.
     * 
     * @param element    The name of the element.
     * @param attributes The element attributes.
     * @param augs   Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
        throws XNIException {

        startElement(element, attributes, augs);
        endElement(element, augs);

    } // emptyElement(QName,XMLAttributes)

    /**
     * Character content.
     *
     * @param text The content.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        
        if (DEBUG_EVENTS) {
            System.out.println("==>characters(): "+text.toString());
        }

        if (!fDeferNodeExpansion) {
            if (fInCDATASection && fCreateCDATANodes) {
                if (fCurrentCDATASection == null) {
                    fCurrentCDATASection =
                        fDocument.createCDATASection(text.toString());
                    fCurrentNode.appendChild(fCurrentCDATASection);
                    fCurrentNode = fCurrentCDATASection;
                }
                else {
                    fCurrentCDATASection.appendData(text.toString());
                }
            }
            else if (!fInDTD) {
                // if type is union (XML Schema) it is possible that we receive
                // character call with empty data
                if (text.length == 0) {
                    return;
                }

                String value = null;
                // normalized value for element is stored in schema_normalize_value property
                // of PSVI element.
                if (fNormalizeData && augs != null) {
                    ElementPSVI elemPSVI = (ElementPSVI)augs.getItem(Constants.ELEMENT_PSVI);
                    if (elemPSVI != null) {
                        value = elemPSVI.getSchemaNormalizedValue();
                    } 
                } 
                if (value == null) {
                     value = text.toString();
                }
                Node child = fCurrentNode.getLastChild();
                if (child != null && child.getNodeType() == Node.TEXT_NODE) {
                    // collect all the data into the string buffer. 
                    if (fFirstChunk) {
                        if (fDocumentImpl != null) {
                            fStringBuffer.append(((TextImpl)child).removeData());
                        } else {
                            fStringBuffer.append(((Text)child).getData());
                            ((Text)child).setNodeValue(null);
                        }
                        fFirstChunk = false;
                    }
                    fStringBuffer.append(value);
                }
                else {
                   fFirstChunk = true;
                   Text textNode = fDocument.createTextNode(value);
                   fCurrentNode.appendChild(textNode);
                }
               
            }
        }
        else {
            // The Text and CDATASection normalization is taken care of within
            // the DOM in the deferred case.
            if (fInCDATASection && fCreateCDATANodes) {
                if (fCurrentCDATASectionIndex == -1) {
                    int cs = fDeferredDocumentImpl.
                        createDeferredCDATASection(text.toString());

                    fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, cs);
                    fCurrentCDATASectionIndex = cs;
                    fCurrentNodeIndex = cs;
                }
                else {
                    int txt = fDeferredDocumentImpl.
                        createDeferredTextNode(text.toString(), false);
                    fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, txt);
                }
            } else if (!fInDTD) {
                // if type is union (XML Schema) it is possible that we receive
                // character call with empty data
                if (text.length == 0) {
                    return;
                }

                String value = null;
                // normalized value for element is stored in schema_normalize_value property
                // of PSVI element.
                if (fNormalizeData && augs != null) {
                    ElementPSVI elemPSVI = (ElementPSVI)augs.getItem(Constants.ELEMENT_PSVI);
                    if (elemPSVI != null) {
                        value = elemPSVI.getSchemaNormalizedValue();
                    } 
                } 

                if (value == null) {
                     value = text.toString();
                }
                int txt = fDeferredDocumentImpl.
                    createDeferredTextNode(value, false);
                fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, txt);

            }
        }
    } // characters(XMLString)

    /**
     * Ignorable whitespace. For this method to be called, the document
     * source must have some way of determining that the text containing
     * only whitespace characters should be considered ignorable. For
     * example, the validator can determine if a length of whitespace
     * characters in the document are ignorable based on the element
     * content model.
     *
     * @param text The ignorable whitespace.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {

        if (!fIncludeIgnorableWhitespace) {
            return;
        }

        if (!fDeferNodeExpansion) {
            Node child = fCurrentNode.getLastChild();
            if (child != null && child.getNodeType() == Node.TEXT_NODE) {
                Text textNode = (Text)child;
                textNode.appendData(text.toString());
            }
            else {
                Text textNode = fDocument.createTextNode(text.toString());
                if (fDocumentImpl != null) {
                    TextImpl textNodeImpl = (TextImpl)textNode;
                    textNodeImpl.setIgnorableWhitespace(true);
                }
                fCurrentNode.appendChild(textNode);
            }
        }
        else {
            // The Text normalization is taken care of within the DOM in the
            // deferred case.
            int txt = fDeferredDocumentImpl.
                createDeferredTextNode(text.toString(), true);
            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, txt);
        }

    } // ignorableWhitespace(XMLString)

    /**
     * The end of an element.
     *
     * @param element The name of the element.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>endElement ("+element.rawname+")");
        }
        if (!fDeferNodeExpansion) {
            setCharacterData(false);
            fCurrentNode = fCurrentNode.getParentNode();
        }
        else {
            fCurrentNodeIndex =
                fDeferredDocumentImpl.getParentNode(fCurrentNodeIndex, false);
        }


    } // endElement(QName)


    /**
     * The start of a CDATA section.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startCDATA(Augmentations augs) throws XNIException {
        setCharacterData(false);
        fInCDATASection = true;
    } // startCDATA()

    /**
     * The end of a CDATA section.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endCDATA(Augmentations augs) throws XNIException {

        fInCDATASection = false;
        if (!fDeferNodeExpansion) {
            if (fCurrentCDATASection !=null) {
                fCurrentNode = fCurrentNode.getParentNode();
                fCurrentCDATASection = null;
            }
        }
        else {
            if (fCurrentCDATASectionIndex !=-1) {            
                fCurrentNodeIndex =
                fDeferredDocumentImpl.getParentNode(fCurrentNodeIndex, false);
                fCurrentCDATASectionIndex = -1;
            }
        } 

    } // endCDATA()

    /**
     * The end of the document.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endDocument(Augmentations augs) throws XNIException {

        fInDocument = false;
        if (!fDeferNodeExpansion) {
            // REVISIT: when DOM Level 3 is REC rely on Document.support
            //          instead of specific class
            // set DOM error checking back on
            if (fDocumentImpl != null) {
                fDocumentImpl.setStrictErrorChecking(true);
            }
            fCurrentNode = null;
        }
        else {
            fCurrentNodeIndex = -1;
        }

    } // endDocument()

    /**
     * This method notifies the end of a general entity.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     * 
     * @param name   The name of the entity.                        
     * @param augs   Additional information that may include infoset augmentations
     *               
     * @exception XNIException
     *                   Thrown by handler to signal an error.
     */
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>endGeneralEntity: ("+name+")");
        }
        if (!fDeferNodeExpansion) {
            setCharacterData(true);                
            if (fCurrentEntityDecl != null) {            
                if (fCurrentEntityDecl != null && fCurrentEntityDecl.getFirstChild() == null) {
                    fCurrentEntityDecl.setReadOnly(false, true);
                    Node child = fCurrentNode.getFirstChild();
                    while (child != null) {
                        Node copy = child.cloneNode(true);
                        fCurrentEntityDecl.appendChild(copy);
                        child = child.getNextSibling();
                     }
                    fCurrentEntityDecl.setReadOnly(true, true);
                    
                    //entities.setNamedItem(fCurrentEntityDecl);
                }
                fCurrentEntityDecl = null;
            }
            if (fCreateEntityRefNodes) {
                if (fDocumentImpl != null) {                
                    // Make entity ref node read only
                    ((NodeImpl)fCurrentNode).setReadOnly(true, true);
                }
                fCurrentNode = fCurrentNode.getParentNode();
            } else {  
                // move entity reference children to the list of 
                // siblings of its parent and remove entity reference
                NodeList children = fCurrentNode.getChildNodes();
                Node parent = fCurrentNode.getParentNode();
                int length = children.getLength();
                // get previous sibling of the entity reference
                Node node = fCurrentNode.getPreviousSibling();
                // normalize text nodes
                Node child = children.item(0);
                if (node != null && node.getNodeType() == Node.TEXT_NODE &&
                    child.getNodeType() == Node.TEXT_NODE) {
                    ((Text)node).appendData(child.getNodeValue());
                    fCurrentNode.removeChild(child);

                } else {
                    node = parent.insertBefore(child, fCurrentNode);
                    handleBaseURI(node);
                }

                for (int i=1;i <length;i++) {
                    node = parent.insertBefore(children.item(0), fCurrentNode);
                    handleBaseURI(node);
                }
                parent.removeChild(fCurrentNode);
                fCurrentNode = parent;
            }
        }
        else {
            if (fDeferredEntityDecl != -1) {
               int prevIndex = -1;
               int childIndex = fDeferredDocumentImpl.getLastChild(fCurrentNodeIndex, false);
               while (childIndex != -1) {
                   int cloneIndex = fDeferredDocumentImpl.cloneNode(childIndex, true);
                   fDeferredDocumentImpl.insertBefore(fDeferredEntityDecl, cloneIndex, prevIndex);
                   prevIndex = cloneIndex;
                   childIndex = fDeferredDocumentImpl.getRealPrevSibling(childIndex, false);
                }
             }
             if (fCreateEntityRefNodes) {            
                fCurrentNodeIndex =
                     fDeferredDocumentImpl.getParentNode(fCurrentNodeIndex,
                                                         false);
             } else { //!fCreateEntityRefNodes
                 // move children of entity ref before the entity ref. 
                 // remove entity ref. 
                 
                 // holds a child of entity ref
                 int childIndex = fDeferredDocumentImpl.getLastChild(fCurrentNodeIndex, false);
                 int parentIndex = 
                     fDeferredDocumentImpl.getParentNode(fCurrentNodeIndex,
                                                         false);
                 
                 int prevIndex = fCurrentNodeIndex;
                 int lastChild = childIndex;
                 int sibling = -1;
                 while (childIndex != -1) {
                     handleBaseURI(childIndex);
                     sibling = fDeferredDocumentImpl.getRealPrevSibling(childIndex, false);
                     fDeferredDocumentImpl.insertBefore(parentIndex, childIndex, prevIndex);
                     prevIndex = childIndex;
                     childIndex = sibling;
                 }
                 fDeferredDocumentImpl.setAsLastChild(parentIndex, lastChild);
                 fCurrentNodeIndex = parentIndex;
             }
             fDeferredEntityDecl = -1;
        }
        

    } // endGeneralEntity(String, Augmentations)

    
    /**
     * Record baseURI information for the Element (by adding xml:base attribute)
     * or for the ProcessingInstruction (by setting a baseURI field)
     * Non deferred DOM.
     * 
     * @param node
     */
    protected void handleBaseURI (Node node){
        if (fDocumentImpl != null) {
            // REVISIT: remove dependency on our implementation when
            //          DOM L3 becomes REC
        
            String baseURI = null;
            short nodeType = node.getNodeType();

            if (nodeType == Node.ELEMENT_NODE) {
                // if an element already has xml:base attribute
                // do nothing
                if (fNamespaceAware && (((Element)node).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace","base")!=null)) { 
                    return;
                } else if (((Element)node).getAttributeNode("xml:base") != null) {
                    return;
                }
                // retrive the baseURI from the entity reference
                baseURI = ((EntityReferenceImpl)fCurrentNode).getBaseURI();
                if (baseURI !=null && !baseURI.equals(fDocumentImpl.getDocumentURI())) {
                    if (fNamespaceAware) {
                        ((Element)node).setAttributeNS("http://www.w3.org/XML/1998/namespace","base", baseURI);
                    } else {
                        ((Element)node).setAttribute("xml:base", baseURI);
                    }
                }
            }
            else if (nodeType == Node.PROCESSING_INSTRUCTION_NODE) {
                baseURI = ((EntityReferenceImpl)fCurrentNode).getBaseURI();
                ((ProcessingInstructionImpl)node).setBaseURI(baseURI);
            }
        }
    }

    /**
     * 
     * Record baseURI information for the Element (by adding xml:base attribute)
     * or for the ProcessingInstruction (by setting a baseURI field)
     * Deferred DOM.
     * 
     * @param node
     */
    protected void handleBaseURI (int node){
        short nodeType = fDeferredDocumentImpl.getNodeType(node, false);
        if (nodeType == Node.ELEMENT_NODE) {
            String baseURI = fDeferredDocumentImpl.getNodeValueString(fCurrentNodeIndex, false);
            if (baseURI == null) {
                baseURI = fDeferredDocumentImpl.getDeferredEntityBaseURI(fDeferredEntityDecl);
            }
            if (baseURI !=null && !baseURI.equals(fDeferredDocumentImpl.getDocumentURI())) { 
                fDeferredDocumentImpl.setDeferredAttribute(node, 
                                                           "xml:base", 
                                                           "http://www.w3.org/XML/1998/namespace", 
                                                           baseURI, 
                                                           true);
            }
        }
        else if (nodeType == Node.PROCESSING_INSTRUCTION_NODE) {

            // retrieve baseURI from the entity reference
            String baseURI = fDeferredDocumentImpl.getNodeValueString(fCurrentNodeIndex, false);

            if (baseURI == null) {
                // try baseURI of the entity declaration
                baseURI = fDeferredDocumentImpl.getDeferredEntityBaseURI(fDeferredEntityDecl);
            }
            fDeferredDocumentImpl.setDeferredPIBaseURI(node, baseURI);
        }
    }

                       
    //
    // XMLDTDHandler methods
    //

    /**
     * The start of the DTD.
     *
     * @param locator  The document locator, or null if the document
     *                 location cannot be reported during the parsing of 
     *                 the document DTD. However, it is <em>strongly</em>
     *                 recommended that a locator be supplied that can 
     *                 at least report the base system identifier of the
     *                 DTD.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startDTD(XMLLocator locator, Augmentations augs) throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>startDTD");
            if (DEBUG_BASEURI) {            
                System.out.println("   expandedSystemId: "+locator.getExpandedSystemId());
                System.out.println("   baseURI:"+ locator.getBaseSystemId());
            }
        }

        fInDTD = true;
        fBaseURIStack.push(locator.getBaseSystemId());
        if (fDeferNodeExpansion || fDocumentImpl != null) {
            fInternalSubset = new StringBuffer(1024);
        }
    } // startDTD(XMLLocator)


    /**
     * The end of the DTD.
     *
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endDTD(Augmentations augs) throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>endDTD()");
        }
        fInDTD = false;
        fBaseURIStack.pop();
        String internalSubset = fInternalSubset != null && fInternalSubset.length() > 0
                              ? fInternalSubset.toString() : null;
        if (fDeferNodeExpansion) {
            if (internalSubset != null) {
                fDeferredDocumentImpl.setInternalSubset(fDocumentTypeIndex, internalSubset);
            }
        }
        else if (fDocumentImpl != null) {
            if (internalSubset != null) {
                ((DocumentTypeImpl)fDocumentType).setInternalSubset(internalSubset);
            }
        }
    } // endDTD()

    /**
     * The start of a conditional section.
     * 
     * @param type The type of the conditional section. This value will
     *             either be CONDITIONAL_INCLUDE or CONDITIONAL_IGNORE.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #CONDITIONAL_INCLUDE
     * @see #CONDITIONAL_IGNORE
     */
    public void startConditional(short type, Augmentations augs) throws XNIException  {
    } // startConditional(short)

    /**
     * The end of a conditional section.
     *
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endConditional(Augmentations augs) throws XNIException {
    } // endConditional()


    /**
     * The start of the DTD external subset.
     *
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startExternalSubset(XMLResourceIdentifier identifier, 
                                    Augmentations augs) throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>startExternalSubset");
            if (DEBUG_BASEURI) {            
                System.out.println("   expandedSystemId: "+identifier.getExpandedSystemId());
                System.out.println("   baseURI:"+ identifier.getBaseSystemId());
            }
        }
        fBaseURIStack.push(identifier.getBaseSystemId());
        fInDTDExternalSubset = true;
    } // startExternalSubset(Augmentations)

    /**
     * The end of the DTD external subset.
     *
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endExternalSubset(Augmentations augs) throws XNIException {
        fInDTDExternalSubset = false;
        fBaseURIStack.pop();
    } // endExternalSubset(Augmentations)

    /**
     * An internal entity declaration.
     * 
     * @param name The name of the entity. Parameter entity names start with
     *             '%', whereas the name of a general entity is just the 
     *             entity name.
     * @param text The value of the entity.
     * @param nonNormalizedText The non-normalized value of the entity. This
     *             value contains the same sequence of characters that was in 
     *             the internal entity declaration, without any entity
     *             references expanded.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void internalEntityDecl(String name, XMLString text, 
                                   XMLString nonNormalizedText,
                                   Augmentations augs) throws XNIException {

        if (DEBUG_EVENTS) {
            System.out.println("==>internalEntityDecl: "+name);
            if (DEBUG_BASEURI) {            
                System.out.println("   baseURI:"+ (String)fBaseURIStack.peek());
            }
        }  
        // internal subset string
        if (fInternalSubset != null && !fInDTDExternalSubset) {
            fInternalSubset.append("<!ENTITY ");
            if (name.startsWith("%")) {
                fInternalSubset.append("% ");
                fInternalSubset.append(name.substring(1));
            }
            else {
                fInternalSubset.append(name);
            }
            fInternalSubset.append(' ');
            String value = nonNormalizedText.toString();
            boolean singleQuote = value.indexOf('\'') == -1;
            fInternalSubset.append(singleQuote ? '\'' : '"');
            fInternalSubset.append(value);
            fInternalSubset.append(singleQuote ? '\'' : '"');
            fInternalSubset.append(">\n");
        }

        // NOTE: We only know how to create these nodes for the Xerces
        //       DOM implementation because DOM Level 2 does not specify 
        //       that functionality. -Ac

        // create full node
        // don't add parameter entities!
        if(name.startsWith("%"))
            return;
        if (fDocumentType != null) {
            NamedNodeMap entities = fDocumentType.getEntities();
            EntityImpl entity = (EntityImpl)entities.getNamedItem(name);
            if (entity == null) {
                entity = (EntityImpl)fDocumentImpl.createEntity(name);
                entity.setBaseURI((String)fBaseURIStack.peek());
                entities.setNamedItem(entity);
            }
        }
            
        // create deferred node        
        if (fDocumentTypeIndex != -1) {
            boolean found = false;
            int node = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
            while (node != -1) {
                short nodeType = fDeferredDocumentImpl.getNodeType(node, false);
                if (nodeType == Node.ENTITY_NODE) {
                    String nodeName = fDeferredDocumentImpl.getNodeName(node, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
                node = fDeferredDocumentImpl.getRealPrevSibling(node, false);
            }
            if (!found) {
                int entityIndex =
                    fDeferredDocumentImpl.createDeferredEntity(name, null, null, null, (String)fBaseURIStack.peek());
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, entityIndex);
            }
        }
    
    } // internalEntityDecl(String,XMLString,XMLString)

    /**
     * An external entity declaration.
     * 
     * @param name     The name of the entity. Parameter entity names start
     *                 with '%', whereas the name of a general entity is just
     *                 the entity name.
     * @param identifier    An object containing all location information 
     *                      pertinent to this notation.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void externalEntityDecl(String name, XMLResourceIdentifier identifier,
                                   Augmentations augs) throws XNIException {


        if (DEBUG_EVENTS) {
            System.out.println("==>externalEntityDecl: "+name);
            if (DEBUG_BASEURI) { 
                System.out.println("   expandedSystemId:"+ identifier.getExpandedSystemId());
                System.out.println("   baseURI:"+ identifier.getBaseSystemId());
            }
        }  
        // internal subset string
        String publicId = identifier.getPublicId();
        String literalSystemId = identifier.getLiteralSystemId();
        if (fInternalSubset != null && !fInDTDExternalSubset) {
            fInternalSubset.append("<!ENTITY ");
            if (name.startsWith("%")) {
                fInternalSubset.append("% ");
                fInternalSubset.append(name.substring(1));
            }
            else {
                fInternalSubset.append(name);
            }
            fInternalSubset.append(' ');
            if (publicId != null) {
                fInternalSubset.append("PUBLIC '");
                fInternalSubset.append(publicId);
                fInternalSubset.append("' '");
            }
            else {
                fInternalSubset.append("SYSTEM '");
            }
            fInternalSubset.append(literalSystemId);
            fInternalSubset.append("'>\n");
        }

        // NOTE: We only know how to create these nodes for the Xerces
        //       DOM implementation because DOM Level 2 does not specify 
        //       that functionality. -Ac

        // create full node
        // don't add parameter entities!
        if(name.startsWith("%"))
            return;
        if (fDocumentType != null) {
            NamedNodeMap entities = fDocumentType.getEntities();
            EntityImpl entity = (EntityImpl)entities.getNamedItem(name);
            if (entity == null) {
                entity = (EntityImpl)fDocumentImpl.createEntity(name);
                entity.setPublicId(publicId);
                entity.setSystemId(literalSystemId);
                entity.setBaseURI(identifier.getBaseSystemId());
                entities.setNamedItem(entity);
            }
        }
            
        // create deferred node
        if (fDocumentTypeIndex != -1) {
            boolean found = false;
            int nodeIndex = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
            while (nodeIndex != -1) {
                short nodeType = fDeferredDocumentImpl.getNodeType(nodeIndex, false);
                if (nodeType == Node.ENTITY_NODE) {
                    String nodeName = fDeferredDocumentImpl.getNodeName(nodeIndex, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
                nodeIndex = fDeferredDocumentImpl.getRealPrevSibling(nodeIndex, false);
            }
            if (!found) {
                int entityIndex = fDeferredDocumentImpl.createDeferredEntity(
                                    name, publicId, literalSystemId, null, identifier.getBaseSystemId());
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, entityIndex);
            }
        }
    
    } // externalEntityDecl(String,XMLResourceIdentifier, Augmentations)


    /**
     * This method notifies of the start of a parameter entity. The parameter
     * entity name start with a '%' character.
     * 
     * @param name     The name of the parameter entity.
     * @param identifier The resource identifier.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal parameter entities).
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startParameterEntity(String name, 
                                     XMLResourceIdentifier identifier,
                                     String encoding,
                                     Augmentations augs) throws XNIException {
        if (DEBUG_EVENTS) {
            System.out.println("==>startParameterEntity: "+name);
            if (DEBUG_BASEURI) {            
                System.out.println("   expandedSystemId: "+identifier.getExpandedSystemId());
                System.out.println("   baseURI:"+ identifier.getBaseSystemId());
            }
        }  
        fBaseURIStack.push(identifier.getExpandedSystemId());
    }


    /**
     * This method notifies the end of a parameter entity. Parameter entity
     * names begin with a '%' character.
     * 
     * @param name The name of the parameter entity.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endParameterEntity(String name, Augmentations augs) throws XNIException {
        
        if (DEBUG_EVENTS) {
            System.out.println("==>endParameterEntity: "+name);
        } 
        fBaseURIStack.pop(); 
    }

    /**
     * An unparsed entity declaration.
     * 
     * @param name     The name of the entity.
     * @param identifier    An object containing all location information 
     *                      pertinent to this entity.
     * @param notation The name of the notation.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void unparsedEntityDecl(String name, XMLResourceIdentifier identifier,
                                   String notation, Augmentations augs) 
        throws XNIException {

        if (DEBUG_EVENTS) {
            System.out.println("==>unparsedEntityDecl: "+name);
            if (DEBUG_BASEURI) { 
                System.out.println("   expandedSystemId:"+ identifier.getExpandedSystemId());
                System.out.println("   baseURI:"+ identifier.getBaseSystemId());
            }
        }  
        // internal subset string
        String publicId = identifier.getPublicId();
        String literalSystemId = identifier.getLiteralSystemId();
        if (fInternalSubset != null && !fInDTDExternalSubset) {
            fInternalSubset.append("<!ENTITY ");
            fInternalSubset.append(name);
            fInternalSubset.append(' ');
            if (publicId != null) {
                fInternalSubset.append("PUBLIC '");
                fInternalSubset.append(publicId);
                if (literalSystemId != null) {
                    fInternalSubset.append("' '");
                    fInternalSubset.append(literalSystemId);
                }
            }
            else {
                fInternalSubset.append("SYSTEM '");
                fInternalSubset.append(literalSystemId);
            }
            fInternalSubset.append("' NDATA ");
            fInternalSubset.append(notation);
            fInternalSubset.append(">\n");
        }

        // NOTE: We only know how to create these nodes for the Xerces
        //       DOM implementation because DOM Level 2 does not specify 
        //       that functionality. -Ac

        // create full node
        if (fDocumentType != null) {
            NamedNodeMap entities = fDocumentType.getEntities();
            EntityImpl entity = (EntityImpl)entities.getNamedItem(name);
            if (entity == null) {
                entity = (EntityImpl)fDocumentImpl.createEntity(name);
                entity.setPublicId(publicId);
                entity.setSystemId(literalSystemId);
                entity.setNotationName(notation);
                entity.setBaseURI(identifier.getBaseSystemId());
                entities.setNamedItem(entity);
            }
        }
            
        // create deferred node        
        if (fDocumentTypeIndex != -1) {
            boolean found = false;
            int nodeIndex = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
            while (nodeIndex != -1) {
                short nodeType = fDeferredDocumentImpl.getNodeType(nodeIndex, false);
                if (nodeType == Node.ENTITY_NODE) {
                    String nodeName = fDeferredDocumentImpl.getNodeName(nodeIndex, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
                nodeIndex = fDeferredDocumentImpl.getRealPrevSibling(nodeIndex, false);
            }
            if (!found) {
                int entityIndex = fDeferredDocumentImpl.createDeferredEntity(
                                    name, publicId, literalSystemId, notation, identifier.getBaseSystemId());
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, entityIndex);
            }
        }
    
    } // unparsedEntityDecl(String,XMLResourceIdentifier, String, Augmentations)

    /**
     * A notation declaration
     * 
     * @param name     The name of the notation.
     * @param identifier    An object containing all location information 
     *                      pertinent to this notation.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augs) throws XNIException {

        // internal subset string
        String publicId = identifier.getPublicId();
        String literalSystemId = identifier.getLiteralSystemId();
        if (fInternalSubset != null && !fInDTDExternalSubset) {
            fInternalSubset.append("<!NOTATION ");
            fInternalSubset.append(name);
            if (publicId != null) {
                fInternalSubset.append(" PUBLIC '");
                fInternalSubset.append(publicId);
                if (literalSystemId != null) {
                    fInternalSubset.append("' '");
                    fInternalSubset.append(literalSystemId);
                }
            }
            else {
                fInternalSubset.append(" SYSTEM '");
                fInternalSubset.append(literalSystemId);
            }
            fInternalSubset.append("'>\n");
        }

        // NOTE: We only know how to create these nodes for the Xerces
        //       DOM implementation because DOM Level 2 does not specify 
        //       that functionality. -Ac

        // create full node
        if (fDocumentImpl !=null && fDocumentType != null) {
            NamedNodeMap notations = fDocumentType.getNotations();
            if (notations.getNamedItem(name) == null) {
                NotationImpl notation = (NotationImpl)fDocumentImpl.createNotation(name);
                notation.setPublicId(publicId);
                notation.setSystemId(literalSystemId);
                notation.setBaseURI(identifier.getBaseSystemId());
                notations.setNamedItem(notation);
            }
        }

        // create deferred node
        if (fDocumentTypeIndex != -1) {
            boolean found = false;
            int nodeIndex = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
            while (nodeIndex != -1) {
                short nodeType = fDeferredDocumentImpl.getNodeType(nodeIndex, false);
                if (nodeType == Node.NOTATION_NODE) {
                    String nodeName = fDeferredDocumentImpl.getNodeName(nodeIndex, false);
                    if (nodeName.equals(name)) {
                        found = true;
                        break;
                    }
                }
                nodeIndex = fDeferredDocumentImpl.getPrevSibling(nodeIndex, false);
            }
            if (!found) {
                int notationIndex = fDeferredDocumentImpl.createDeferredNotation(
                                        name, publicId, literalSystemId, identifier.getBaseSystemId());
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, notationIndex);
            }
        }

    } // notationDecl(String,XMLResourceIdentifier, Augmentations)

    /**
     * Characters within an IGNORE conditional section.
     *
     * @param text The ignored text.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
     public void ignoredCharacters(XMLString text, Augmentations augs) throws XNIException {
     } // ignoredCharacters(XMLString, Augmentations)


    /**
     * An element declaration.
     * 
     * @param name         The name of the element.
     * @param contentModel The element content model.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void elementDecl(String name, String contentModel, Augmentations augs)
        throws XNIException {

        // internal subset string
        if (fInternalSubset != null && !fInDTDExternalSubset) {
            fInternalSubset.append("<!ELEMENT ");
            fInternalSubset.append(name);
            fInternalSubset.append(' ');
            fInternalSubset.append(contentModel);
            fInternalSubset.append(">\n");
        }

    } // elementDecl(String,String)

    /**
     * An attribute declaration.
     * 
     * @param elementName   The name of the element that this attribute
     *                      is associated with.
     * @param attributeName The name of the attribute.
     * @param type          The attribute type. This value will be one of
     *                      the following: "CDATA", "ENTITY", "ENTITIES",
     *                      "ENUMERATION", "ID", "IDREF", "IDREFS", 
     *                      "NMTOKEN", "NMTOKENS", or "NOTATION".
     * @param enumeration   If the type has the value "ENUMERATION" or
     *                      "NOTATION", this array holds the allowed attribute
     *                      values; otherwise, this array is null.
     * @param defaultType   The attribute default type. This value will be
     *                      one of the following: "#FIXED", "#IMPLIED",
     *                      "#REQUIRED", or null.
     * @param defaultValue  The attribute default value, or null if no
     *                      default value is specified.
     * @param nonNormalizedDefaultValue  The attribute default value with no normalization 
     *                      performed, or null if no default value is specified.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void attributeDecl(String elementName, String attributeName, 
                              String type, String[] enumeration, 
                              String defaultType, XMLString defaultValue,
                              XMLString nonNormalizedDefaultValue, Augmentations augs) throws XNIException {

        // internal subset string
        if (fInternalSubset != null && !fInDTDExternalSubset) {
            fInternalSubset.append("<!ATTLIST ");
            fInternalSubset.append(elementName);
            fInternalSubset.append(' ');
            fInternalSubset.append(attributeName);
            fInternalSubset.append(' ');
            if (type.equals("ENUMERATION")) {
                fInternalSubset.append('(');
                for (int i = 0; i < enumeration.length; i++) {
                    if (i > 0) {
                        fInternalSubset.append('|');
                    }
                    fInternalSubset.append(enumeration[i]);
                }
                fInternalSubset.append(')');
            }
            else {
                fInternalSubset.append(type);
            }
            if (defaultType != null) {
                fInternalSubset.append(' ');
                fInternalSubset.append(defaultType);
            }
            if (defaultValue != null) {
                fInternalSubset.append(" '");
                for (int i = 0; i < defaultValue.length; i++) {
                    char c = defaultValue.ch[defaultValue.offset + i];
                    if (c == '\'') {
                        fInternalSubset.append("&apos;");
                    }
                    else {
                        fInternalSubset.append(c);
                    }
                }
                fInternalSubset.append('\'');
            }
            fInternalSubset.append(">\n");
        }

        // deferred expansion
        if (fDeferredDocumentImpl != null) {

            // get the default value
            if (defaultValue != null) {

                // get element definition
                int elementDefIndex  = fDeferredDocumentImpl.lookupElementDefinition(elementName);

                // create element definition if not already there
                if (elementDefIndex == -1) {
                    elementDefIndex = fDeferredDocumentImpl.createDeferredElementDefinition(elementName);
                    fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, elementDefIndex);
                }
                // add default attribute
                int attrIndex = fDeferredDocumentImpl.createDeferredAttribute(
                                    attributeName, defaultValue.toString(), false);
                if (type.equals("ID")) {               
                    fDeferredDocumentImpl.setIdAttribute(attrIndex);
                }
                // REVISIT: set ID type correctly
                fDeferredDocumentImpl.appendChild(elementDefIndex, attrIndex);
            }

        } // if deferred

        // full expansion
        else if (fDocumentImpl != null) {

            // get the default value
            if (defaultValue != null) {

                // get element definition node
                NamedNodeMap elements = ((DocumentTypeImpl)fDocumentType).getElements();
                ElementDefinitionImpl elementDef = (ElementDefinitionImpl)elements.getNamedItem(elementName);
                if (elementDef == null) {
                    elementDef = fDocumentImpl.createElementDefinition(elementName);
                    ((DocumentTypeImpl)fDocumentType).getElements().setNamedItem(elementDef);
                }

                // REVISIT: Check for uniqueness of element name? -Ac

                // create attribute and set properties
                boolean nsEnabled = fNamespaceAware;
                AttrImpl attr;
                if (nsEnabled) {
                    String namespaceURI = null;
                    // DOM Level 2 wants all namespace declaration attributes
                    // to be bound to "http://www.w3.org/2000/xmlns/"
                    // So as long as the XML parser doesn't do it, it needs to
                    // done here.
                    if (attributeName.startsWith("xmlns:") ||
                        attributeName.equals("xmlns")) {
                        namespaceURI = NamespaceContext.XMLNS_URI;
                    }
                    attr = (AttrImpl)fDocumentImpl.createAttributeNS(namespaceURI,
                                                                attributeName);
                }
                else {
                    attr = (AttrImpl)fDocumentImpl.createAttribute(attributeName);
                }
                attr.setValue(defaultValue.toString());
                attr.setSpecified(false);
                attr.setIdAttribute(type.equals("ID"));

                // add default attribute to element definition
                if (nsEnabled){
                    elementDef.getAttributes().setNamedItemNS(attr);
                }
                else {
                    elementDef.getAttributes().setNamedItem(attr);
                }
            }

        } // if NOT defer-node-expansion

    } // attributeDecl(String,String,String,String[],String,XMLString, XMLString, Augmentations)


    /**
     * The start of an attribute list.
     * 
     * @param elementName The name of the element that this attribute
     *                    list is associated with.
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startAttlist(String elementName, Augmentations augs) throws XNIException {
    } // startAttlist(String)


    /**
     * The end of an attribute list.
     *
     * @param augs Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endAttlist(Augmentations augs) throws XNIException {
    } // endAttlist()


    // method to create an element node.
    // subclasses can override this method to create element nodes in other ways.
    protected Element createElementNode(QName element) {
        Element el = null;
        
        if (fNamespaceAware) {
            // if we are using xerces DOM implementation, call our
            // own constructor to reuse the strings we have here.
            if (fDocumentImpl != null) {
                el = fDocumentImpl.createElementNS(element.uri, element.rawname,
                                                   element.localpart);
            }
            else {
                el = fDocument.createElementNS(element.uri, element.rawname);
            }
        }
        else {
            el = fDocument.createElement(element.rawname);
        }
        
        return el;
    }

    // method to create an attribute node.
    // subclasses can override this method to create attribute nodes in other ways.
    protected Attr createAttrNode(QName attrQName) {
        Attr attr = null;
        
        if (fNamespaceAware) {
            if (fDocumentImpl != null) {
                // if we are using xerces DOM implementation, call our
                // own constructor to reuse the strings we have here.
                attr = fDocumentImpl.createAttributeNS(attrQName.uri,
                                                       attrQName.rawname,
                                                       attrQName.localpart);
            }
            else {
                attr = fDocument.createAttributeNS(attrQName.uri,
                                                   attrQName.rawname);
            }
        }
        else {
            attr = fDocument.createAttribute(attrQName.rawname);
        }
        
        return attr;
    }

    // If data rececived in more than one chunk, the data
    // is stored in StringBuffer.      
    // This function is called then the state is changed and the 
    // data needs to be appended to the current node
    protected void  setCharacterData(boolean sawChars){
        // handle character data
        fFirstChunk = sawChars;
        if (fStringBuffer.length() > 0) {
            // if we have data in the buffer we must have created
            // a text node already.
            Node child = fCurrentNode.getLastChild();
            // REVISIT: should this check be performed?
            if (child != null && child.getNodeType() == Node.TEXT_NODE) {
                if (fDocumentImpl != null) {
                    ((TextImpl)child).replaceData(fStringBuffer.toString());
                }
                else {
                    ((Text)child).setData(fStringBuffer.toString());
                }
            }
            // reset string buffer
            fStringBuffer.setLength(0);
        }
    }

    
} // class AbstractDOMParser
