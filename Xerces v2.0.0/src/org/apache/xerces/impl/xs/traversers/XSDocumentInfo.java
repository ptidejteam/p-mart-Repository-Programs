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

package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XIntPool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Vector;
import java.util.Stack;

/*
 * Objects of this class hold all information pecular to a
 * particular XML Schema document.  This is needed because
 * namespace bindings and other settings on the <schema/> element
 * affect the contents of that schema document alone.
 *
 * @author Neil Graham, IBM
 * @version $Id: XSDocumentInfo.java,v 1.1 2006/02/02 01:45:12 vauchers Exp $
 */
class XSDocumentInfo {

    // Data
    protected SchemaNamespaceSupport fNamespaceSupport;
    protected SchemaNamespaceSupport fNamespaceSupportRoot;
    protected Stack SchemaNamespaceSupportStack = new Stack();

    // schema's attributeFormDefault
    protected boolean fAreLocalAttributesQualified;

    // elementFormDefault
    protected boolean fAreLocalElementsQualified;

    // [block | final]Default
    protected short fBlockDefault;
    protected short fFinalDefault;

    // targetNamespace
    protected String fTargetNamespace;

    // represents whether this is a chameleon schema (i.e., whether its TNS is natural or comes from without)
    protected boolean fIsChameleonSchema;

    // the root of the schema Document tree itself
    protected Document fSchemaDoc;

    // all namespaces that this document can refer to
    Vector fImportedNS = new Vector();
    
    protected ValidationState fValidationContext = new ValidationState();

    SymbolTable fSymbolTable = null;

    XSDocumentInfo (Document schemaDoc, XSAttributeChecker attrChecker, SymbolTable symbolTable)
                    throws XMLSchemaException {
        fSchemaDoc = schemaDoc;
        fNamespaceSupport = new SchemaNamespaceSupport();
        fIsChameleonSchema = false;

        fSymbolTable = symbolTable;
        // During XML Schema traversal bind "xml" prefix to
        // "http://www.w3.org/XML/1998/namespace"
        // per Namespace Constraint: Prefix Declared (Namespaces in XML REC)
        fNamespaceSupport.declarePrefix(symbolTable.addSymbol("xml"), symbolTable.addSymbol("http://www.w3.org/XML/1998/namespace"));

        if(schemaDoc != null) {
            Element root = DOMUtil.getRoot(schemaDoc);
            Object[] schemaAttrs = attrChecker.checkAttributes(root, true, this);
            // schemaAttrs == null means it's not an <xsd:schema> element
            // throw an exception, but we don't know the document systemId,
            // so we leave that to the caller.
            if (schemaAttrs == null) {
                throw new XMLSchemaException(null, null);
            }
            fAreLocalAttributesQualified =
                ((XInt)schemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == SchemaSymbols.FORM_QUALIFIED;
            fAreLocalElementsQualified =
                ((XInt)schemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == SchemaSymbols.FORM_QUALIFIED;
            fBlockDefault =
                ((XInt)schemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
            fFinalDefault =
                ((XInt)schemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
            fTargetNamespace =
                (String)schemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
            if (fTargetNamespace != null)
                fTargetNamespace = symbolTable.addSymbol(fTargetNamespace);

            fNamespaceSupportRoot = new SchemaNamespaceSupport(fNamespaceSupport);

            //set namespace support
            fValidationContext.setNamespaceSupport(fNamespaceSupport);
            fValidationContext.setSymbolTable(symbolTable);
            // REVISIT: we can't return, becaues we can't pop fNamespaceSupport
            //attrChecker.returnAttrArray(schemaAttrs, this);
        }
    }

    void backupNSSupport() {
        SchemaNamespaceSupportStack.push(fNamespaceSupport);
        fNamespaceSupport = new SchemaNamespaceSupport(fNamespaceSupportRoot);

        // bind "xml" prefix to "http://www.w3.org/XML/1998/namespace"
        // per Namespace Constraint: Prefix Declared (Namespaces in XML REC)
        fNamespaceSupport.declarePrefix(fSymbolTable.addSymbol("xml"), fSymbolTable.addSymbol("http://www.w3.org/XML/1998/namespace"));

        fValidationContext.setNamespaceSupport(fNamespaceSupport);
    }

    void restoreNSSupport() {
        fNamespaceSupport = (SchemaNamespaceSupport)SchemaNamespaceSupportStack.pop();
        fValidationContext.setNamespaceSupport(fNamespaceSupport);
    }

    // some Object methods
    public String toString() {
        return fTargetNamespace == null?"no targetNamspace":"targetNamespace is " + fTargetNamespace;
    }

    public void addAllowedNS(String namespace) {
        fImportedNS.addElement(namespace == null ? "" : namespace);
    }
    
    public boolean isAllowedNS(String namespace) {
        return fImportedNS.contains(namespace == null ? "" : namespace);
    }
    
} // XSDocumentInfo
