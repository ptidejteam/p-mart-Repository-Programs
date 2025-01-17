/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XIntPool;
import org.apache.xerces.impl.dv.xs.*;
import org.apache.xerces.xni.QName;
import org.apache.xerces.util.NamespaceSupport;


import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import java.lang.reflect.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 * The simple type definition schema component traverser.
 *
 * <simpleType
 *   final = (#all | (list | union | restriction))
 *   id = ID
 *   name = NCName
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?, (restriction | list | union))
 * </simpleType>
 *
 * <restriction
 *   base = QName
 *   id = ID
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?, (simpleType?, (minExclusive | minInclusive | maxExclusive | maxInclusive | totalDigits | fractionDigits | length | minLength | maxLength | enumeration | whiteSpace | pattern)*))
 * </restriction>
 *
 * <list
 *   id = ID
 *   itemType = QName
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?, (simpleType?))
 * </list>
 *
 * <union
 *   id = ID
 *   memberTypes = List of QName
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?, (simpleType*))
 * </union>
 *
 * @author Elena Litani, IBM
 * @version $Id: XSDSimpleTypeTraverser.java,v 1.2 2001/10/29 17:09:50 sandygao Exp $
 */
class XSDSimpleTypeTraverser extends XSDAbstractTraverser {

    //private data
    private String fListName = "";

    private int fSimpleTypeAnonCount = 0;
    private final QName fQName = new QName();

    XSDSimpleTypeTraverser (XSDHandler handler,
                            XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }

    //return qualified name of simpleType or empty string if error occured
    DatatypeValidator traverseGlobal(Element elmNode,
                                     XSDocumentInfo schemaDoc,
                                     SchemaGrammar grammar) {
        // General Attribute Checking
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, true, schemaDoc);
        String nameAtt = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        DatatypeValidator type = traverseSimpleTypeDecl(elmNode, attrValues, schemaDoc, grammar, true);
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return type;
    }

    DatatypeValidator traverseLocal(Element elmNode,
                                    XSDocumentInfo schemaDoc,
                                    SchemaGrammar grammar) {
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        DatatypeValidator type = traverseSimpleTypeDecl (elmNode, attrValues, schemaDoc, grammar, false);
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);

        return type;
    }

    private DatatypeValidator traverseSimpleTypeDecl(Element simpleTypeDecl, Object[] attrValues,
                                                     XSDocumentInfo schemaDoc,
                                                     SchemaGrammar grammar, boolean isGlobal) {

        String nameProperty  = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        String qualifiedName = nameProperty;
        Hashtable fFacetData = null;
        //---------------------------------------------------
        // set qualified name
        //---------------------------------------------------
        if (nameProperty == null) { // anonymous simpleType
            qualifiedName = schemaDoc.fTargetNamespace == null?
                ",#s#"+(fSimpleTypeAnonCount++):
                schemaDoc.fTargetNamespace+",#S#"+(fSimpleTypeAnonCount++);
            //REVISIT:
            // add to symbol table?
        }
        else {
            qualifiedName = schemaDoc.fTargetNamespace == null?
                ","+nameProperty:
                schemaDoc.fTargetNamespace+","+nameProperty;
            //REVISIT:
            // add to symbol table?

        }

        //----------------------------------------------------------
        // REVISIT!
        // update _final_ registry
        //----------------------------------------------------------
        XInt finalAttr = (XInt)attrValues[XSAttributeChecker.ATTIDX_FINAL];
        int finalProperty = finalAttr == null ? schemaDoc.fFinalDefault : finalAttr.intValue();

        //----------------------------------------------------------------------
        //annotation?,(list|restriction|union)
        //----------------------------------------------------------------------
        Element content = DOMUtil.getFirstChildElement(simpleTypeDecl);
        if (content != null) {
            // traverse annotation if any
            if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                traverseAnnotationDecl(content, attrValues, false, schemaDoc);
                content = DOMUtil.getNextSiblingElement(content);
            }
        }

        if (content == null) {
            reportSchemaError("dt-simpleType", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, nameProperty, "(annotation?, (restriction | list | union))"});
            return null;
        }

        // General Attribute Checking
        Object[] contentAttrs = fAttrChecker.checkAttributes(content, false, schemaDoc);
        // REVISIT: when to return the array
        fAttrChecker.returnAttrArray(contentAttrs, schemaDoc);

        //----------------------------------------------------------------------
        //use content.getLocalName for the cases there "xsd:" is a prefix, ei. "xsd:list"
        //----------------------------------------------------------------------
        String varietyProperty =  DOMUtil.getLocalName(content);  //content.getLocalName();
        QName baseTypeName = null;
        Vector memberTypes = null;
        Vector dTValidators = null;
        int size = 0;
        boolean list = false;
        boolean union = false;
        boolean restriction = false;
        int numOfTypes = 0; //list/restriction = 1, union = "+"

        if (varietyProperty.equals(SchemaSymbols.ELT_LIST)) { //traverse List
            baseTypeName = (QName)contentAttrs[XSAttributeChecker.ATTIDX_ITEMTYPE];
            list = true;
            if (fListName.length() != 0) { // parent is <list> datatype
                reportCosListOfAtomic(nameProperty);
                return null;
            }
            else {
                fListName = qualifiedName;
            }
        }
        else if (varietyProperty.equals(SchemaSymbols.ELT_RESTRICTION)) { //traverse Restriction
            baseTypeName = (QName)contentAttrs[XSAttributeChecker.ATTIDX_BASE];
            //content.getAttribute( SchemaSymbols.ATT_BASE );
            restriction= true;
        }
        else if (varietyProperty.equals(SchemaSymbols.ELT_UNION)) { //traverse union
            union = true;
            memberTypes = (Vector)contentAttrs[XSAttributeChecker.ATTIDX_MEMBERTYPES];
            //content.getAttribute( SchemaSymbols.ATT_MEMBERTYPES);
            if (memberTypes != null) {
                size = memberTypes.size();
            }
            else {
                size = 1; //at least one must be seen as <simpleType> decl
            }
            dTValidators = new Vector (size, 2);
        }
        else {
            Object[] args = { varietyProperty};
            reportSchemaError("dt-unsupported-derivation", args);
        }
        if (DOMUtil.getNextSiblingElement(content) != null) {
            reportSchemaError("dt-simpleType", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, nameProperty, "(annotation?, (restriction | list | union))"});
        }

        DatatypeValidator baseValidator = null;
        if (baseTypeName == null && memberTypes == null) {
            //---------------------------
            //must 'see' <simpleType>
            //---------------------------

            //content = {annotation?,simpleType?...}
            content = DOMUtil.getFirstChildElement(content);

            //check content (annotation?, ...)
            if (content != null) {
                // traverse annotation if any
                if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    traverseAnnotationDecl(content, attrValues, false, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                }
            }
            if (content == null) {
                reportSchemaError("dt-simpleType", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, nameProperty, "(annotation?, (restriction | list | union))"});
                return null;
            }
            if (DOMUtil.getLocalName(content).equals( SchemaSymbols.ELT_SIMPLETYPE )) {
                baseValidator = traverseLocal(content, schemaDoc, grammar);
                if (baseValidator != null && union) {
                    dTValidators.addElement((DatatypeValidator)baseValidator);
                }
                if (baseValidator == null) {
                    Object[] args = {content.getAttribute( SchemaSymbols.ATT_BASE )};
                    reportSchemaError("dt-unknown-basetype", args);
                    return (DatatypeValidator)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_STRING);
                }
            }
            else {
                Object[] args = { simpleTypeDecl.getAttribute( SchemaSymbols.ATT_NAME )};
                reportSchemaError("dt-simpleType",new Object[]{SchemaSymbols.ELT_SIMPLETYPE, nameProperty, "(annotation?, (restriction | list | union))"});
                return (DatatypeValidator)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_STRING);
            }
        } //end - must see simpleType?
        else {
            //-----------------------------
            //base was provided - get proper validator.
            //-----------------------------
            numOfTypes = 1;
            if (union) {
                numOfTypes= size;
            }
            //--------------------------------------------------------------------
            // this loop is also where we need to find out whether the type being used as
            // a base (or itemType or whatever) allows such things.
            //--------------------------------------------------------------------
            int baseRefContext = (restriction? SchemaSymbols.RESTRICTION:0);
            baseRefContext = baseRefContext | (union? SchemaSymbols.UNION:0);
            baseRefContext = baseRefContext | (list ? SchemaSymbols.LIST:0);
            for (int i=0; i<numOfTypes; i++) {  //find all validators
                if (union) {
                    baseTypeName = (QName)memberTypes.elementAt(i);
                }
                baseValidator = findDTValidator ( simpleTypeDecl, baseTypeName, baseRefContext, schemaDoc);
                if (baseValidator == null) {
                    Object[] args = { content.getAttribute( SchemaSymbols.ATT_BASE ), nameProperty};
                    reportSchemaError("dt-unknown-basetype", args);
                    baseValidator = (DatatypeValidator)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_STRING);
                }
                // ------------------------------
                // (variety is list)cos-list-of-atomic
                // ------------------------------
                if (fListName.length() != 0) {
                    if (baseValidator instanceof ListDatatypeValidator) {
                        reportCosListOfAtomic(nameProperty);
                        return null;
                    }
                    //-----------------------------------------------------
                    // if baseValidator is of type (union) need to look
                    // at Union validators to make sure that List is not one of them
                    //-----------------------------------------------------
                    if (isListDatatype(baseValidator)) {
                        reportCosListOfAtomic(nameProperty);
                        return null;

                    }

                }
                if (union) {
                    dTValidators.addElement((DatatypeValidator)baseValidator); //add validator to structure
                }
            }
        } //end - base is available


        // ------------------------------------------
        // move to next child
        // <base==empty)->[simpleType]->[facets]  OR
        // <base!=empty)->[facets]
        // ------------------------------------------
        if (baseTypeName == null) {
            content = DOMUtil.getNextSiblingElement( content );
        }
        else {
            content = DOMUtil.getFirstChildElement(content);
            if (content != null) {
                // traverse annotation if any
                if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    traverseAnnotationDecl(content, attrValues, false, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                }
            }
        }

        // ------------------------------------------
        //get more types for union if any
        // ------------------------------------------
        if (union) {
            int index=size;
            if (memberTypes != null) {
                if (content != null) {
                    // traverse annotation if any
                    if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                        traverseAnnotationDecl(content, attrValues, false, schemaDoc);
                        content = DOMUtil.getNextSiblingElement(content);
                    }
                }
                if (content !=null) {
                    if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                        Object[] args = {nameProperty};
                        reportSchemaError("dt-union-memberType", args);
                    }
                }
            }
            while (content!=null) {
                baseValidator = traverseLocal(content, schemaDoc, grammar);
                if (baseValidator != null) {
                    if (fListName.length() != 0 && baseValidator instanceof ListDatatypeValidator) {
                        reportCosListOfAtomic(nameProperty);
                        return null;
                    }
                    dTValidators.addElement((DatatypeValidator)baseValidator);
                }
                if (baseValidator == null) {
                    Object[] args = { content.getAttribute( SchemaSymbols.ATT_BASE ), nameProperty};
                    reportSchemaError("dt-unknown-basetype", args);
                    baseValidator = (DatatypeValidator)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_STRING);
                }
                content   = DOMUtil.getNextSiblingElement( content );
            }
        } // end - traverse Union

        if (fListName.length() != 0) {
            // reset fListName, meaning that we are done with
            // traversing <list> and its itemType resolves to atomic value
            if (fListName.equals(qualifiedName)) {
                fListName = SchemaSymbols.EMPTY_STRING;
            }
        }
        if (restriction && content != null) {
            fFacetInfo fi = traverseFacets(content, contentAttrs,nameProperty, baseValidator, schemaDoc, grammar);
            content = fi.nodeAfterFacets;
            if (content != null) {
                content = null;
                reportSchemaError("s4s-elt-must-match", new Object[]{SchemaSymbols.ELT_RESTRICTION, "(annotation?, (simpleType?, (minExclusive | minInclusive | maxExclusive | maxInclusive | totalDigits | fractionDigits | length | minLength | maxLength | enumeration | whiteSpace | pattern)*))"});
            }
            fFacetData = fi.facetdata;
        }
        else if (list && content!=null) {
            // report error - must not have any children!
            if (baseTypeName !=null) {
                // traverse annotation if any
                if (DOMUtil.getLocalName(content).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    traverseAnnotationDecl(content, attrValues, false, schemaDoc);
                    content = DOMUtil.getNextSiblingElement(content);
                }
                if (content !=null) {
                        Object[] args = {nameProperty};
                        reportSchemaError("dt-list-itemType", args);
                }

            }
            else {
                reportSchemaError("s4s-elt-must-match", new Object[]{SchemaSymbols.ELT_LIST, "(annotation?, (simpleType?))"});
            }
        }
        else if (union && content!=null) {
            //report error - must not have any children!
                reportSchemaError("s4s-elt-must-match", new Object[]{SchemaSymbols.ELT_UNION, "(annotation?, (simpleType?))"});
        }


        // REVISIT: constructors in the datatypes rely on the fact that facets hashtable is not null.
        //          (see inheriting base type facets).
        if (fFacetData== null) {
            fFacetData = new Hashtable();
        }
        DatatypeValidator newDV = null;
        if (list) {
                newDV = new ListDatatypeValidator(baseValidator, fFacetData, true, fErrorReporter);
        }
        else if (restriction) {
            newDV = createRestrictedValidator(baseValidator, fFacetData, fErrorReporter);
        }
        else { //union
            newDV = new UnionDatatypeValidator(dTValidators, fErrorReporter);
        }

        // don't add global components without name to the grammar
        if (nameProperty == null && isGlobal) {
            return null;
        }

        if (newDV != null && isGlobal) {
            newDV.setFinalSet(finalProperty);
            ((AbstractDatatypeValidator)newDV).fLocalName = nameProperty;
            grammar.addGlobalTypeDecl(newDV);
        }

        return newDV;
    }


    private void reportCosListOfAtomic (String qualifiedName) {
        reportSchemaError("cos-list-of-atomic", new Object[]{qualifiedName});
        fListName=SchemaSymbols.EMPTY_STRING;
    }

    //@param: elm - top element
    //@param: baseTypeStr - type (base/itemType/memberTypes)
    //@param: baseRefContext:  whether the caller is using this type as a base for restriction, union or list
    //return DatatypeValidator available for the baseTypeStr, null if not found or disallowed.
    // also throws an error if the base type won't allow itself to be used in this context.
    // REVISIT: can this code be re-used?
    private DatatypeValidator findDTValidator (Element elm, QName baseTypeStr, int baseRefContext, XSDocumentInfo schemaDoc) {
        if (baseTypeStr.uri !=null &&  baseTypeStr.uri.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) &&
            baseTypeStr.localpart.equals(SchemaSymbols.ATTVAL_ANYSIMPLETYPE) &&
            baseRefContext == SchemaSymbols.RESTRICTION) {
            String base = baseTypeStr.localpart;
            reportSchemaError("dt-unknown-basetype", new Object[] {
                                       DOMUtil.getAttrValue(elm, SchemaSymbols.ATT_NAME), DOMUtil.getAttrValue(elm, SchemaSymbols.ATT_BASE)});
            return  (DatatypeValidator)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(SchemaSymbols.ATTVAL_STRING);
        }
        DatatypeValidator baseValidator = null;
        baseValidator = (DatatypeValidator)fSchemaHandler.getGlobalDecl(schemaDoc, fSchemaHandler.TYPEDECL_TYPE, baseTypeStr);
        if (baseValidator != null) {
            if ((baseValidator.getFinalSet() & baseRefContext) != 0) {
                reportSchemaError("dt-restiction-final",new Object[] { baseTypeStr.rawname} );
            }
        }
        return baseValidator;
    }

    // find if union datatype validator has list datatype member.
    private boolean isListDatatype (DatatypeValidator validator) {
        if (validator instanceof UnionDatatypeValidator) {
            Vector temp = ((UnionDatatypeValidator)validator).getBaseValidators();
            for (int i=0;i<temp.size();i++) {
                if (temp.elementAt(i) instanceof ListDatatypeValidator) {
                    return true;
                }
                if (temp.elementAt(i) instanceof UnionDatatypeValidator) {
                    if (isListDatatype((DatatypeValidator)temp.elementAt(i))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
