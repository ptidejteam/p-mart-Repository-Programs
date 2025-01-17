/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.
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
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.dom.EntityReferenceImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.NotationImpl;
import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.impl.Constants;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

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
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

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
 * @version $Id: AbstractDOMParser.java,v 1.22 2001/12/17 19:03:21 lehors Exp $
 */
public abstract class AbstractDOMParser
    extends AbstractXMLDocumentParser {

    //
    // Constants
    //

    // feature ids

    /** Feature id: create entity ref nodes. */
    protected static final String CREATE_ENTITY_REF_NODES =
        "http://apache.org/xml/features/dom/create-entity-ref-nodes";

    /** Feature id: namespace. */
    protected static final String NAMESPACES =
        Constants.SAX_FEATURE_PREFIX+Constants.NAMESPACES_FEATURE;

    /** Feature id: include comments. */
    protected static final String INCLUDE_COMMENTS_FEATURE = Constants.INCLUDE_COMMENTS_FEATURE;

    /** Feature id: create cdata nodes. */
    protected static final String CREATE_CDATA_NODES_FEATURE = Constants.CREATE_CDATA_NODES_FEATURE;

    /** Feature id: include ignorable whitespace. */
    protected static final String INCLUDE_IGNORABLE_WHITESPACE =
        "http://apache.org/xml/features/dom/include-ignorable-whitespace";

    /** Feature id: defer node expansion. */
    protected static final String DEFER_NODE_EXPANSION =
        "http://apache.org/xml/features/dom/defer-node-expansion";

    // property ids

    /** Property id: document class name. */
    protected static final String DOCUMENT_CLASS_NAME =
        "http://apache.org/xml/properties/dom/document-class-name";

    // other

    /** Default document class name. */
    protected static final String DEFAULT_DOCUMENT_CLASS_NAME =
        "org.apache.xerces.dom.DocumentImpl";

    // debugging

    /** Set to true and recompile to debug entity references. */
    private static final boolean DEBUG_ENTITY_REF = false;


    //
    // Data
    //

    // features

    /** Create entity reference nodes. */
    protected boolean fCreateEntityRefNodes;

    /** Include ignorable whitespace. */
    protected boolean fIncludeIgnorableWhitespace;

    /** Include Comments. */
    protected boolean fIncludeComments;

    /** Create cdata nodes. */
    protected boolean fCreateCDATANodes;

    // dom information

    /** The document. */
    protected Document fDocument;

    /** The default Xerces document implementation, if used. */
    protected DocumentImpl fDocumentImpl;

    /** The document class name to use. */
    protected String  fDocumentClassName;

    /** The document type node. */
    protected DocumentType fDocumentType;

    /** Current node. */
    protected Node fCurrentNode;
    protected CDATASection fCurrentCDATASection;


    // deferred expansion data

    protected boolean              fDeferNodeExpansion;
    protected boolean              fNamespaceAware;
    protected DeferredDocumentImpl fDeferredDocumentImpl;
    protected int                  fDocumentIndex;
    protected int                  fDocumentTypeIndex;
    protected int                  fCurrentNodeIndex;
    protected int                  fCurrentCDATASectionIndex;

    // state

    /** True if inside document. */
    protected boolean fInDocument;

    /** True if inside CDATA section. */
    protected boolean fInCDATASection;

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
        final String[] recognizedFeatures = {
            CREATE_ENTITY_REF_NODES,
            INCLUDE_IGNORABLE_WHITESPACE,
            DEFER_NODE_EXPANSION,
            INCLUDE_COMMENTS_FEATURE,
            CREATE_CDATA_NODES_FEATURE
        };
        fConfiguration.addRecognizedFeatures(recognizedFeatures);

        // set default values
        fConfiguration.setFeature(CREATE_ENTITY_REF_NODES, true);
        fConfiguration.setFeature(INCLUDE_IGNORABLE_WHITESPACE, true);
        fConfiguration.setFeature(DEFER_NODE_EXPANSION, true);
        fConfiguration.setFeature(INCLUDE_COMMENTS_FEATURE, true);
        fConfiguration.setFeature(CREATE_CDATA_NODES_FEATURE, true);

        // add recognized properties
        final String[] recognizedProperties = {
            DOCUMENT_CLASS_NAME
        };
        fConfiguration.addRecognizedProperties(recognizedProperties);

        // set default values
        fConfiguration.setProperty(DOCUMENT_CLASS_NAME,
                                   DEFAULT_DOCUMENT_CLASS_NAME);

    } // <init>(XMLParserConfiguration)

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
     * @see #setDeferNodeExpansion
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

        // reset state information
        fInDocument = false;
        fInDTD = false;
        fInCDATASection = false;
        fCurrentCDATASection = null;
        fCurrentCDATASectionIndex = -1;

    } // reset()

    //
    // XMLDocumentHandler methods
    //

    /**
     * This method notifies of the start of an entity. The DTD has the
     * pseudo-name of "[dtd]" parameter entity names start with '%'; and
     * general entity names are just the entity name.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     *
     * @param name     The name of the entity.
     * @param publicId The public identifier of the entity if the entity
     *                 is external, null otherwise.
     * @param systemId The system identifier of the entity if the entity
     *                 is external, null otherwise.
     * @param encoding The auto-detected IANA encoding name of the entity
     *                 stream. This value will be null in those situations
     *                 where the entity encoding is not auto-detected (e.g.
     *                 internal parameter entities).
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startEntity(String name, String publicId, String systemId,
                            String baseSystemId,
                            String encoding, 
                            Augmentations augs) throws XNIException {

        // REVISIT: investigate fInDTD & fInDocument flags
        // this method now only called by DocumentHandler
        // comment(), endEntity(), processingInstruction(), textDecl()
        // REVISIT: need to set the Entity.actualEncoding somehow
        if (fInDocument && !fInDTD && fCreateEntityRefNodes ) {
            if (!fDeferNodeExpansion) {
                EntityReference er = fDocument.createEntityReference(name);
                fCurrentNode.appendChild(er);
                fCurrentNode = er;
            }
            else {
                int er =
                    fDeferredDocumentImpl.createDeferredEntityReference(name);
                fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, er);
                fCurrentNodeIndex = er;
            }
        }

    } // startEntity(String,String,String,String)

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
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void textDecl(String version, String encoding) throws XNIException {
        if (!fDeferNodeExpansion) {
            // REVISIT: when DOM Level 3 is REC rely on Document.support
            //          instead of specific class
            if (fDocumentType != null) {
                NamedNodeMap entities = fDocumentType.getEntities();
                String name = fCurrentNode.getNodeName();
                EntityImpl entity = (EntityImpl) entities.getNamedItem(name);
                if (entity != null) {
                    entity.setVersion(version);
                    entity.setEncoding(encoding);
                }
            }
        }
        else {
            String name = fDeferredDocumentImpl.getNodeName(fCurrentNodeIndex, false);
            if (fDocumentTypeIndex != -1 && name != null) {
                // find corresponding Entity decl
                boolean found = false;
                int node = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
                while (node != -1) {
                    short nodeType = fDeferredDocumentImpl.getNodeType(node, false);
                    if (nodeType == Node.ENTITY_NODE) {
                        String nodeName =
                            fDeferredDocumentImpl.getNodeName(node, false);
                        if (nodeName.equals(name)) {
                            found = true;
                            break;
                        }
                    }
                    node = fDeferredDocumentImpl.getRealPrevSibling(node, false);
                }
                if (found) {
                    fDeferredDocumentImpl.setEntityInfo(node, version, encoding);
                }
            }
        }
    } // textDecl(String,String)

    /**
     * A comment.
     *
     * @param text The text in the comment.
     *
     * @throws XNIException Thrown by application to signal an error.
     */
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        
        if (!fIncludeComments) {
              return;
        }
        if (!fDeferNodeExpansion) {
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
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void processingInstruction(String target, XMLString data, Augmentations augs)
        throws XNIException {

        if (!fDeferNodeExpansion) {
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
     * @param systemId The system identifier of the entity if the entity
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
                fDocumentImpl = (DocumentImpl)fDocument;
                // REVISIT: when DOM Level 3 is REC rely on Document.support
                //          instead of specific class
                // set DOM error checking off
                fDocumentImpl.setStrictErrorChecking(false);
                // set actual encoding
                fDocumentImpl.setActualEncoding(encoding);
            }
            else {
                // use specified document class
                try {
                    Class documentClass = Class.forName(fDocumentClassName);
                    fDocument = (Document)documentClass.newInstance();
                    // if subclass of our own class that's cool too
                    Class defaultDocClass =
                        Class.forName(DEFAULT_DOCUMENT_CLASS_NAME);
                    if (defaultDocClass.isAssignableFrom(documentClass)) {
                        fDocumentImpl = (DocumentImpl)fDocument;
                        // REVISIT: when DOM Level 3 is REC rely on
                        //          Document.support instead of specific class
                        // set DOM error checking off
                        fDocumentImpl.setStrictErrorChecking(false);
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
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void xmlDecl(String version, String encoding, String standalone)
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
        if (!fDeferNodeExpansion) {
            Element el;
            if (fNamespaceAware) {
                el = fDocument.createElementNS(element.uri, element.rawname);
            }
            else {
                el = fDocument.createElement(element.rawname);
            } 
            
            int attrCount = attributes.getLength();
            for (int i = 0; i < attrCount; i++) {
                attributes.getName(i, fAttrQName);
                // DOM Level 2 wants all namespace declaration attributes
                // to be bound to "http://www.w3.org/2000/xmlns/"
                String attributeName = fAttrQName.rawname;
                if (attributeName !=null && (attributeName.startsWith("xmlns:") ||
                    attributeName.equals("xmlns"))) {
                    fAttrQName.uri = NamespaceContext.XMLNS_URI;
                }
                Attr attr;
                if (fNamespaceAware) {
                    attr = fDocument.createAttributeNS(fAttrQName.uri,
                                                   fAttrQName.rawname);
                }
                else {
                    attr = fDocument.createAttribute(fAttrQName.rawname);
                }
                String attrValue = attributes.getValue(i);
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
                }
                // REVISIT: Handle entities in attribute value.
            }
            fCurrentNode.appendChild(el);
            fCurrentNode = el;

            // identifier registration
            for (int i = 0; i < attrCount; i++) {
                 if (attributes.getType(i).equals("ID")) {
                        String identifier = attributes.getValue(i);
                        fDocumentImpl.putIdentifier(identifier, el);
                    }
            }
        }
        else {
            int el = fDeferredDocumentImpl.
                createDeferredElement(fNamespaceAware ?
                                      element.uri : null,
                                      element.rawname, attributes);

            fDeferredDocumentImpl.appendChild(fCurrentNodeIndex, el);
            fCurrentNodeIndex = el;

            // identifier registration
            int attrCount = attributes.getLength();
            for (int i = 0; i < attrCount; i++) {
                 if (attributes.getType(i).equals("ID")) {
                        String identifier = attributes.getValue(i);
                        fDeferredDocumentImpl.putIdentifier(identifier, el);
                 }
                   
            }
            
        }
    } // startElement(QName,XMLAttributes)

    /**
     * Character content.
     *
     * @param text The content.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void characters(XMLString text, Augmentations augs) throws XNIException {
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
                Node child = fCurrentNode.getLastChild();
                if (child != null && child.getNodeType() == Node.TEXT_NODE) {
                    Text textNode = (Text)child;
                    textNode.appendData(text.toString());
                }
                else {
                    Text textNode = fDocument.createTextNode(text.toString());
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
                int txt = fDeferredDocumentImpl.
                    createDeferredTextNode(text.toString(), false);
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
        if (!fDeferNodeExpansion) {
            fCurrentNode = fCurrentNode.getParentNode();
        }
        else {
            fCurrentNodeIndex =
                fDeferredDocumentImpl.getParentNode(fCurrentNodeIndex, false);
        }


    } // endElement(QName)

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
     * The start of a CDATA section.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startCDATA(Augmentations augs) throws XNIException {

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
     * This method notifies the end of an entity. The DTD has the pseudo-name
     * of "[dtd]" parameter entity names start with '%'; and general entity
     * names are just the entity name.
     * <p>
     * <strong>Note:</strong> This method is not called for entity references
     * appearing as part of attribute values.
     *
     * @param name The name of the entity.
     * @param augs     Additional information that may include infoset augmentations
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endEntity(String name, Augmentations augs) throws XNIException {

        if (fInDocument && !fInDTD && fCreateEntityRefNodes) {
            if (!fDeferNodeExpansion) {
                if (fDocumentType != null) {
                    NamedNodeMap entities = fDocumentType.getEntities();
                    NodeImpl entity = (NodeImpl)entities.getNamedItem(name);
                    if (entity != null && entity.getFirstChild() == null) {
                        entity.setReadOnly(false, true);
                        Node child = fCurrentNode.getFirstChild();
                        while (child != null) {
                            Node copy = child.cloneNode(true);
                            entity.appendChild(copy);
                            child = child.getNextSibling();
                        }
                        entity.setReadOnly(true, true);
                        entities.setNamedItem(entity);
                    }
                }
                fCurrentNode = fCurrentNode.getParentNode();
            }
            else {
                int entityIndex = -1;
                int dtChildIndex = fDeferredDocumentImpl.getLastChild(fDocumentTypeIndex, false);
                while (dtChildIndex != -1) {
                    short nodeType = fDeferredDocumentImpl.getNodeType(dtChildIndex, false);
                    if (nodeType == Node.ENTITY_NODE) {
                        String nodeName = fDeferredDocumentImpl.getNodeName(dtChildIndex, false);
                        if (nodeName.equals(name)) {
                            if (fDeferredDocumentImpl.getLastChild(dtChildIndex, false) == -1) {
                                entityIndex = dtChildIndex;
                            }
                            break;
                        }
                    }
                    dtChildIndex = fDeferredDocumentImpl.getRealPrevSibling(dtChildIndex, false);
                }
                if (entityIndex != -1) {
                    int prevIndex = -1;
                    int childIndex = fDeferredDocumentImpl.getLastChild(fCurrentNodeIndex, false);
                    while (childIndex != -1) {
                        int cloneIndex = fDeferredDocumentImpl.cloneNode(childIndex, true);
                        fDeferredDocumentImpl.insertBefore(entityIndex, cloneIndex, prevIndex);
                        prevIndex = cloneIndex;
                        childIndex = fDeferredDocumentImpl.getRealPrevSibling(childIndex, false);
                    }
                }
                fCurrentNodeIndex =
                    fDeferredDocumentImpl.getParentNode(fCurrentNodeIndex,
                                                        false);
            }
        }

    } // endEntity(String)

    //
    // XMLDTDHandler methods
    //

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
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void internalEntityDecl(String name, XMLString text, 
                                   XMLString nonNormalizedText) 
        throws XNIException {

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
                    fDeferredDocumentImpl.createDeferredEntity(name, null, null, null);
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
     * @param publicId The public identifier of the entity or null if the
     *                 the entity was specified with SYSTEM.
     * @param systemId The system identifier of the entity.
     * @param baseSystemId The base system identifier where this entity
     *                     is declared.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void externalEntityDecl(String name, 
                                   String publicId, String systemId,
                                   String baseSystemId) throws XNIException {

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
                entity.setSystemId(systemId);
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
                                    name, publicId, systemId, null);
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, entityIndex);
            }
        }
    
    } // externalEntityDecl(String,String,String,String)

    /**
     * An unparsed entity declaration.
     * 
     * @param name     The name of the entity.
     * @param publicId The public identifier of the entity, or null if not
     *                 specified.
     * @param systemId The system identifier of the entity, or null if not
     *                 specified.
     * @param notation The name of the notation.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void unparsedEntityDecl(String name, 
                                   String publicId, String systemId, 
                                   String notation) throws XNIException {

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
                entity.setSystemId(systemId);
                entity.setNotationName(notation);
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
                                    name, publicId, systemId, notation);
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, entityIndex);
            }
        }
    
    } // unparsedEntityDecl(String,String,String,String)

    /**
     * A notation declaration
     * 
     * @param name     The name of the notation.
     * @param publicId The public identifier of the notation, or null if not
     *                 specified.
     * @param systemId The system identifier of the notation, or null if not
     *                 specified.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void notationDecl(String name, String publicId, String systemId)
        throws XNIException {

        // NOTE: We only know how to create these nodes for the Xerces
        //       DOM implementation because DOM Level 2 does not specify 
        //       that functionality. -Ac

        // create full node
        if (fDocumentType != null) {
            NamedNodeMap notations = fDocumentType.getNotations();
            if (notations.getNamedItem(name) == null) {
                NotationImpl notation = (NotationImpl)fDocumentImpl.createNotation(name);
                notation.setPublicId(publicId);
                notation.setSystemId(systemId);
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
                                        name, publicId, systemId);
                fDeferredDocumentImpl.appendChild(fDocumentTypeIndex, notationIndex);
            }
        }

    } // notationDecl(String,String,String)

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
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void attributeDecl(String elementName, String attributeName, 
                              String type, String[] enumeration, 
                              String defaultType, XMLString defaultValue)
        throws XNIException {

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

                // add default attribute to element definition
                if (nsEnabled){
                    elementDef.getAttributes().setNamedItemNS(attr);
                }
                else {
                    elementDef.getAttributes().setNamedItem(attr);
                }
            }

        } // if NOT defer-node-expansion

    } // attributeDecl(String,String,String,String[],String,XMLString)

} // class AbstractDOMParser
