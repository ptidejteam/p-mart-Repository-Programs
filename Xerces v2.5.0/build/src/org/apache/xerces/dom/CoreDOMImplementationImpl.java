/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
package org.apache.xerces.dom;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.parsers.DOMParserImpl;
import org.apache.xerces.util.ObjectFactory;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.DOMSerializerImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMParser;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.DOMInput;
import org.w3c.dom.ls.DOMSerializer;
/**
 * The DOMImplementation class is description of a particular
 * implementation of the Document Object Model. As such its data is
 * static, shared by all instances of this implementation.
 * <P>
 * The DOM API requires that it be a real object rather than static
 * methods. However, there's nothing that says it can't be a singleton,
 * so that's how I've implemented it.
 * <P>
 * This particular class, along with CoreDocumentImpl, supports the DOM
 * Core and Load/Save (Experimental). Optional modules are supported by 
 * the more complete DOMImplementation class along with DocumentImpl.
 * @version $Id: CoreDOMImplementationImpl.java,v 1.1 2006/02/02 01:16:30 vauchers Exp $
 * @since PR-DOM-Level-1-19980818.
 */
public class CoreDOMImplementationImpl
	implements DOMImplementation, DOMImplementationLS {
	//
	// Data
	//
    
    // validators pool
    private static final int SIZE = 2;
    private RevalidationHandler validators[] = new RevalidationHandler[SIZE];
    private int freeValidatorIndex = -1;
    private int currentSize = SIZE;

    // Document and doctype counter.  Used to assign order to documents and 
    // doctypes without owners, on an demand basis.   Used for  
    // compareDocumentPosition
    private int docAndDoctypeCounter = 0;

	// static
	/** Dom implementation singleton. */
	static CoreDOMImplementationImpl singleton =
		new CoreDOMImplementationImpl();
	//
	// Public methods
	//
	/** NON-DOM: Obtain and return the single shared object */
	public static DOMImplementation getDOMImplementation() {
		return singleton;
	}
	//
	// DOMImplementation methods
	//
	/** 
	 * Test if the DOM implementation supports a specific "feature" --
	 * currently meaning language and level thereof.
	 * 
	 * @param feature      The package name of the feature to test.
	 * In Level 1, supported values are "HTML" and "XML" (case-insensitive).
	 * At this writing, org.apache.xerces.dom supports only XML.
	 *
	 * @param version      The version number of the feature being tested.
	 * This is interpreted as "Version of the DOM API supported for the
	 * specified Feature", and in Level 1 should be "1.0"
	 *
	 * @return    true iff this implementation is compatable with the specified
	 * feature and version.
	 */
	public boolean hasFeature(String feature, String version) {
		// Currently, we support only XML Level 1 version 1.0
		boolean anyVersion = version == null || version.length() == 0;
		// check if Xalan implementation is around and if yes report true for supporting 
		// XPath API
		if ((feature.equalsIgnoreCase("XPath") || feature.equalsIgnoreCase("+XPath"))&& version.equals("3.0")){
			try{
				Class xpathClass = ObjectFactory.findProviderClass(
					"org.apache.xpath.domapi.XPathEvaluatorImpl",
					ObjectFactory.findClassLoader(), true);
			}
			catch (Exception e){
				return false;
			}
			return true;
		}
		return (
			feature.equalsIgnoreCase("Core")
				&& (anyVersion || version.equals("1.0") || version.equals("2.0")))
			|| (feature.equalsIgnoreCase("XML")
				&& (anyVersion || version.equals("1.0") || version.equals("2.0")))
			|| (feature.equalsIgnoreCase("LS")
				&& (anyVersion || version.equals("3.0")));
	} // hasFeature(String,String):boolean
    
    
	/**
	 * Introduced in DOM Level 2. <p>
	 * 
	 * Creates an empty DocumentType node.
	 *
	 * @param qualifiedName The qualified name of the document type to be created. 
	 * @param publicID The document type public identifier.
	 * @param systemID The document type system identifier.
	 * @since WD-DOM-Level-2-19990923
	 */
	public DocumentType createDocumentType( String qualifiedName, 
                                    String publicID, String systemID) {
		// REVISIT: this might allow creation of invalid name for DOCTYPE
		//          xmlns prefix.
		//          also there is no way for a user to turn off error checking.
		checkQName(qualifiedName);
		return new DocumentTypeImpl(null, qualifiedName, publicID, systemID);
	}
    
    final void checkQName(String qname){
        int index = qname.indexOf(':');
        int lastIndex = qname.lastIndexOf(':');
        int length = qname.length();

        // it is an error for NCName to have more than one ':'
        // check if it is valid QName [Namespace in XML production 6]
        if (index == 0 || index == length - 1 || lastIndex != index) {
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "NAMESPACE_ERR",
                    null);
            throw new DOMException(DOMException.NAMESPACE_ERR, msg);
        }
        int start = 0;
        // Namespace in XML production [6]
        if (index > 0) {
            // check that prefix is NCName
            if (!XMLChar.isNCNameStart(qname.charAt(start))) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "INVALID_CHARACTER_ERR",
                        null);
                throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
            }
            for (int i = 1; i < index; i++) {
                if (!XMLChar.isNCName(qname.charAt(i))) {
                    String msg =
                        DOMMessageFormatter.formatMessage(
                            DOMMessageFormatter.DOM_DOMAIN,
                            "INVALID_CHARACTER_ERR",
                            null);
                    throw new DOMException(
                        DOMException.INVALID_CHARACTER_ERR,
                        msg);
                }
            }
            start = index + 1;
        }

        // check local part 
        if (!XMLChar.isNCNameStart(qname.charAt(start))) {
            // REVISIT: add qname parameter to the message
            String msg =
                DOMMessageFormatter.formatMessage(
                    DOMMessageFormatter.DOM_DOMAIN,
                    "INVALID_CHARACTER_ERR",
                    null);
            throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
        }
        for (int i = start + 1; i < length; i++) {
            if (!XMLChar.isNCName(qname.charAt(i))) {
                String msg =
                    DOMMessageFormatter.formatMessage(
                        DOMMessageFormatter.DOM_DOMAIN,
                        "INVALID_CHARACTER_ERR",
                        null);
                throw new DOMException(DOMException.INVALID_CHARACTER_ERR, msg);
            }
        }           
    }


	/**
	 * Introduced in DOM Level 2. <p>
	 * 
	 * Creates an XML Document object of the specified type with its document
	 * element.
	 *
	 * @param namespaceURI     The namespace URI of the document
	 *                         element to create, or null. 
	 * @param qualifiedName    The qualified name of the document
	 *                         element to create. 
	 * @param doctype          The type of document to be created or null.<p>
	 *
	 *                         When doctype is not null, its
	 *                         Node.ownerDocument attribute is set to
	 *                         the document being created.
	 * @return Document        A new Document object.
	 * @throws DOMException    WRONG_DOCUMENT_ERR: Raised if doctype has
	 *                         already been used with a different document.
	 * @since WD-DOM-Level-2-19990923
	 */
	public Document createDocument(
		String namespaceURI,
		String qualifiedName,
		DocumentType doctype)
		throws DOMException {
		if (doctype != null && doctype.getOwnerDocument() != null) {
			String msg =
				DOMMessageFormatter.formatMessage(
					DOMMessageFormatter.DOM_DOMAIN,
					"WRONG_DOCUMENT_ERR",
					null);
			throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, msg);
		}
		CoreDocumentImpl doc = new CoreDocumentImpl(doctype);
		Element e = doc.createElementNS(namespaceURI, qualifiedName);
		doc.appendChild(e);
		return doc;
	}
    
	/**
	 * DOM Level 3 WD - Experimental.
     */
	public Object getFeature(String feature, String version) {
		if (singleton.hasFeature(feature, version)){
			return singleton;			
		}
		return null;
        
	}

	// DOM L3 LS

	/**
	 * DOM Level 3 WD - Experimental.
     * Create a new <code>DOMParser</code>. The newly constructed parser may
     * then be configured by means of its <code>DOMConfiguration</code>
     * object, and used to parse documents by means of its <code>parse</code>
     *  method.
     * @param mode  The <code>mode</code> argument is either
     *   <code>MODE_SYNCHRONOUS</code> or <code>MODE_ASYNCHRONOUS</code>, if
     *   <code>mode</code> is <code>MODE_SYNCHRONOUS</code> then the
     *   <code>DOMParser</code> that is created will operate in synchronous
     *   mode, if it's <code>MODE_ASYNCHRONOUS</code> then the
     *   <code>DOMParser</code> that is created will operate in asynchronous
     *   mode.
     * @param schemaType  An absolute URI representing the type of the schema
     *   language used during the load of a <code>Document</code> using the
     *   newly created <code>DOMParser</code>. Note that no lexical checking
     *   is done on the absolute URI. In order to create a
     *   <code>DOMParser</code> for any kind of schema types (i.e. the
     *   DOMParser will be free to use any schema found), use the value
     *   <code>null</code>.
     * <p ><b>Note:</b>    For W3C XML Schema [<a href='http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/'>XML Schema Part 1</a>]
     *   , applications must use the value
     *   <code>"http://www.w3.org/2001/XMLSchema"</code>. For XML DTD [<a href='http://www.w3.org/TR/2000/REC-xml-20001006'>XML 1.0</a>],
     *   applications must use the value
     *   <code>"http://www.w3.org/TR/REC-xml"</code>. Other Schema languages
     *   are outside the scope of the W3C and therefore should recommend an
     *   absolute URI in order to use this method.
     * @return  The newly created <code>DOMParser</code> object. This
     *   <code>DOMParser</code> is either synchronous or asynchronous
     *   depending on the value of the <code>mode</code> argument.
     * <p ><b>Note:</b>    By default, the newly created <code>DOMParser</code>
     *    does not contain a <code>DOMErrorHandler</code>, i.e. the value of
     *   the "<a href='http://www.w3.org/TR/2003/WD-DOM-Level-3-Core-20030609/core.html#parameter-error-handler'>
     *   error-handler</a>" configuration parameter is <code>null</code>. However, implementations
     *   may provide a default error handler at creation time. In that case,
     *   the initial value of the <code>"error-handler"</code> configuration
     *   parameter on the new created <code>DOMParser</code> contains a
     *   reference to the default error handler.
     * @exception DOMException
     *    NOT_SUPPORTED_ERR: Raised if the requested mode or schema type is
     *   not supported.
	 */
        public DOMParser createDOMParser(short mode, String schemaType)
		throws DOMException {
		if (mode == DOMImplementationLS.MODE_ASYNCHRONOUS) {
			String msg =
				DOMMessageFormatter.formatMessage(
					DOMMessageFormatter.DOM_DOMAIN,
					"NOT_SUPPORTED_ERR",
					null);
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, msg);
		}
		if (schemaType != null
			&& schemaType.equals("http://www.w3.org/TR/REC-xml")) {
			return new DOMParserImpl(
				"org.apache.xerces.parsers.DTDConfiguration",
				schemaType);
		}
		else {
			// create default parser configuration validating against XMLSchemas
			return new DOMParserImpl(
				"org.apache.xerces.parsers.XML11Configuration",
				schemaType);
		}
	}
	/**
	 * DOM Level 3 WD - Experimental.
         * Create a new <code>DOMSerializer</code> object.
         * @return The newly created <code>DOMSerializer</code> object.
         * <p ><b>Note:</b>    By default, the newly created
         * <code>DOMSerializer</code> has no <code>DOMErrorHandler</code>,
         * i.e. the value of the <code>"error-handler"</code> configuration
         * parameter is <code>null</code>. However, implementations may
         * provide a default error handler at creation time. In that case, the
         * initial value of the <code>"error-handler"</code> configuration
         * parameter on the new created <code>DOMSerializer</code> contains a
         * reference to the default error handler.
	 */
	public DOMSerializer createDOMSerializer() {
		return new DOMSerializerImpl();
	}
	/**
	 * DOM Level 3 WD - Experimental.
         * Create a new empty input source.
         * @return  The newly created input object.
	 */
	public DOMInput createDOMInput() {
		return new DOMInputImpl();
	}
	//
	// Protected methods
	//
	/** NON-DOM: retrieve validator. */
	synchronized RevalidationHandler getValidator(String schemaType) {
		// REVISIT: implement retrieving DTD validator 
        if (freeValidatorIndex < 0) {
            // create new validator - we should not attempt
            // to restrict the number of validation handlers being 
            // requested
            return (RevalidationHandler) (ObjectFactory
                        .newInstance(
                            "org.apache.xerces.impl.xs.XMLSchemaValidator",
                            ObjectFactory.findClassLoader(),
                            true));

        }
        // return first available validator            
        RevalidationHandler val = validators[freeValidatorIndex];
        validators[freeValidatorIndex--] = null;
        return val;
	}
    
	/** NON-DOM: release validator */
	synchronized void releaseValidator(String schemaType, 
                                         RevalidationHandler validator) {
       // REVISIT: implement support for DTD validators as well
       ++freeValidatorIndex;
       if (validators.length == freeValidatorIndex ){
            // resize size of the validators
            currentSize+=SIZE;
            RevalidationHandler newarray[] =  new RevalidationHandler[currentSize];
            System.arraycopy(validators, 0, newarray, 0, validators.length);
            validators = newarray;
       }
       validators[freeValidatorIndex]=validator;
	}

       /** NON-DOM:  increment document/doctype counter */
       protected synchronized int assignDocumentNumber() {
            return ++docAndDoctypeCounter;
       }
       /** NON-DOM:  increment document/doctype counter */
       protected synchronized int assignDocTypeNumber() {
            return ++docAndDoctypeCounter;
       }

    
} // class DOMImplementationImpl
