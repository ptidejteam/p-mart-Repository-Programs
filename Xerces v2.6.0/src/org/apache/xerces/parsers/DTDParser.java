/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
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

import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.util.SymbolTable;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.XMLResourceIdentifier;

/**
 * @version $Id: DTDParser.java,v 1.1 2006/02/02 01:09:17 vauchers Exp $
 */
public abstract class DTDParser
    extends XMLGrammarParser
    implements XMLDTDHandler, XMLDTDContentModelHandler {

    //
    // Data
    //

    /** fDTDScanner */
    protected XMLDTDScanner fDTDScanner;

    //
    // Constructors
    //

    /**
     * 
     * 
     * @param symbolTable 
     */
    public DTDParser(SymbolTable symbolTable) {
        super(symbolTable);
    }

    //
    // Methods
    //

    /**
     * getDTDGrammar
     * 
     * @return the grammar created by this parser
     */
    public DTDGrammar getDTDGrammar() {
        return null;
    } // getDTDGrammar

    //
    // XMLDTDHandler methods
    //

    /**
     * This method notifies of the start of an entity. The DTD has the 
     * pseudo-name of "[dtd]" and parameter entity names start with '%'.
     * <p>
     * <strong>Note:</strong> Since the DTD is an entity, the handler
     * will be notified of the start of the DTD entity by calling the
     * startEntity method with the entity name "[dtd]" <em>before</em> calling
     * the startDTD method.
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
                            String encoding) throws XNIException {
    }

    /**
     * Notifies of the presence of a TextDecl line in an entity. If present,
     * this method will be called immediately following the startEntity call.
     * <p>
     * <strong>Note:</strong> This method is only called for external
     * parameter entities referenced in the DTD.
     * 
     * @param version  The XML version, or null if not specified.
     * @param encoding The IANA encoding name of the entity.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void textDecl(String version, String encoding) throws XNIException {
    }

    /**
     * The start of the DTD.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startDTD(XMLLocator locator, Augmentations augmentations) 
                         throws XNIException {
    } 

    /**
     * A comment.
     * 
     * @param text The text in the comment.
     *
     * @throws XNIException Thrown by application to signal an error.
     */
    public void comment(XMLString text, Augmentations augmentations) throws XNIException {
    } // comment

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
    public void processingInstruction(String target, XMLString data, 
                                      Augmentations augmentations)
        throws XNIException {
    } // processingInstruction

    /**
     * The start of the external subset.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startExternalSubset(XMLResourceIdentifier identifier, 
                                    Augmentations augmentations)  throws XNIException {
    } // startExternalSubset

    /**
     * The end of the external subset.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
    } // endExternalSubset

    /**
     * An element declaration.
     * 
     * @param name         The name of the element.
     * @param contentModel The element content model.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void elementDecl(String name, String contentModel, 
                            Augmentations augmentations)
        throws XNIException {
    } // elementDecl

    /**
     * The start of an attribute list.
     * 
     * @param elementName The name of the element that this attribute
     *                    list is associated with.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startAttlist(String elementName,
                             Augmentations augmentations) throws XNIException {
    } // startAttlist

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
     * @param enumeration   If the type has the value "ENUMERATION", this
     *                      array holds the allowed attribute values;
     *                      otherwise, this array is null.
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
                              String defaultType, XMLString defaultValue,
                              XMLString nonNormalizedDefaultValue, Augmentations augmentations)
        throws XNIException {
    } // attributeDecl

    /**
     * The end of an attribute list.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endAttlist(Augmentations augmentations) throws XNIException {
    } // endAttlist

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
                                   XMLString nonNormalizedText,
                                   Augmentations augmentations)
        throws XNIException {
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
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void externalEntityDecl(String name, 
                                   XMLResourceIdentifier identifier,
                                   Augmentations augmentations)
        throws XNIException {
    } // externalEntityDecl

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
                                   XMLResourceIdentifier identifier, 
                                   String notation, Augmentations augmentations)
        throws XNIException {
    } // unparsedEntityDecl

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
    public void notationDecl(String name, XMLResourceIdentifier identifier,
                             Augmentations augmentations)
        throws XNIException {
    } // notationDecl

    /**
     * The start of a conditional section.
     * 
     * @param type The type of the conditional section. This value will
     *             either be CONDITIONAL_INCLUDE or CONDITIONAL_IGNORE.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see XMLDTDHandler#CONDITIONAL_INCLUDE
     * @see XMLDTDHandler#CONDITIONAL_IGNORE
     */
    public void startConditional(short type, Augmentations augmentations) throws XNIException {
    } // startConditional

    /**
     * The end of a conditional section.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endConditional(Augmentations augmentations) throws XNIException {
    } // endConditional

    /**
     * The end of the DTD.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endDTD(Augmentations augmentations) throws XNIException {
    } // endDTD

    /**
     * This method notifies the end of an entity. The DTD has the pseudo-name
     * of "[dtd]" and parameter entity names start with '%'.
     * <p>
     * <strong>Note:</strong> Since the DTD is an entity, the handler
     * will be notified of the end of the DTD entity by calling the
     * endEntity method with the entity name "[dtd]" <em>after</em> calling
     * the endDTD method.
     * 
     * @param name The name of the entity.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endEntity(String name, Augmentations augmentations) throws XNIException {
    }

    //
    // XMLDTDContentModelHandler methods
    //

    /**
     * The start of a content model. Depending on the type of the content
     * model, specific methods may be called between the call to the
     * startContentModel method and the call to the endContentModel method.
     * 
     * @param elementName The name of the element.
     * @param type        The content model type.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_EMPTY
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_ANY
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_MIXED
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_CHILDREN
     */
    public void startContentModel(String elementName, short type)
        throws XNIException {
    } // startContentModel

    /**
     * A referenced element in a mixed content model. If the mixed content 
     * model only allows text content, then this method will not be called
     * for that model. However, if this method is called for a mixed
     * content model, then the zero or more occurrence count is implied.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to 
     * the startContentModel method where the type is TYPE_MIXED.
     * 
     * @param elementName The name of the referenced element. 
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_MIXED
     */
    public void mixedElement(String elementName) throws XNIException {
    } // mixedElement

    /**
     * The start of a children group.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     * <p>
     * <strong>Note:</strong> Children groups can be nested and have
     * associated occurrence counts.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_CHILDREN
     */
    public void childrenStartGroup() throws XNIException {
    } // childrenStartGroup

    /**
     * A referenced element in a children content model.
     * 
     * @param elementName The name of the referenced element.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_CHILDREN
     */
    public void childrenElement(String elementName) throws XNIException {
    } // childrenElement

    /**
     * The separator between choices or sequences of a children content
     * model.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     * 
     * @param separator The type of children separator.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see XMLDTDContentModelHandler#SEPARATOR_CHOICE
     * @see XMLDTDContentModelHandler#SEPARATOR_SEQUENCE
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_CHILDREN
     */
    public void childrenSeparator(short separator) throws XNIException {
    } // childrenSeparator

    /**
     * The occurrence count for a child in a children content model.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     * 
     * @param occurrence The occurrence count for the last children element
     *                   or children group.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see XMLDTDContentModelHandler#OCCURS_ZERO_OR_ONE
     * @see XMLDTDContentModelHandler#OCCURS_ZERO_OR_MORE
     * @see XMLDTDContentModelHandler#OCCURS_ONE_OR_MORE
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_CHILDREN
     */
    public void childrenOccurrence(short occurrence) throws XNIException {
    } // childrenOccurrence

    /**
     * The end of a children group.
     * <p>
     * <strong>Note:</strong> This method is only called after a call to
     * the startContentModel method where the type is TYPE_CHILDREN.
     *
     * @see org.apache.xerces.impl.dtd.XMLElementDecl#TYPE_CHILDREN
     */
    public void childrenEndGroup() throws XNIException {
    } // childrenEndGroup

    /**
     * The end of a content model.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endContentModel() throws XNIException {
    } // endContentModel

} // class DTDParser
