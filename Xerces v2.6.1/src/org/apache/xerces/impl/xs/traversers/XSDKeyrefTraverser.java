/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Element;

/**
 * This class contains code that is used to traverse <keyref>s.
 *
 * @author Neil Graham, IBM
 * @version $Id: XSDKeyrefTraverser.java,v 1.6 2003/05/08 20:11:57 elena Exp $
 */
class XSDKeyrefTraverser extends XSDAbstractIDConstraintTraverser {

    public XSDKeyrefTraverser (XSDHandler handler,
                                  XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }

    void traverse(Element krElem, XSElementDecl element,
            XSDocumentInfo schemaDoc, SchemaGrammar grammar) {

        // General Attribute Checking
        Object[] attrValues = fAttrChecker.checkAttributes(krElem, false, schemaDoc);

        // create identity constraint
        String krName = (String)attrValues[XSAttributeChecker.ATTIDX_NAME];
        if(krName == null){
            reportSchemaError("s4s-att-must-appear", new Object [] {SchemaSymbols.ELT_KEYREF , SchemaSymbols.ATT_NAME }, krElem);
            //return this array back to pool
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }
        QName kName = (QName)attrValues[XSAttributeChecker.ATTIDX_REFER];
        if(kName == null){
            reportSchemaError("s4s-att-must-appear", new Object [] {SchemaSymbols.ELT_KEYREF , SchemaSymbols.ATT_REFER }, krElem);
            //return this array back to pool
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }

        UniqueOrKey key = null;
        IdentityConstraint ret = (IdentityConstraint)fSchemaHandler.getGlobalDecl(schemaDoc, XSDHandler.IDENTITYCONSTRAINT_TYPE, kName, krElem);
        // if ret == null, we've already reported an error in getGlobalDecl
        // we report an error only when ret != null, and the return type keyref
        if (ret != null) {
            if (ret.getCategory() == IdentityConstraint.IC_KEY ||
                ret.getCategory() == IdentityConstraint.IC_UNIQUE) {
                key = (UniqueOrKey)ret;
            } else {
                reportSchemaError("src-resolve", new Object[]{kName.rawname, "identity constraint key/unique"}, krElem);
            }
        }

        if(key == null) {
            fAttrChecker.returnAttrArray(attrValues, schemaDoc);
            return;
        }

        KeyRef keyRef = new KeyRef(schemaDoc.fTargetNamespace, krName, element.fName, key);

        // add to element decl
        traverseIdentityConstraint(keyRef, krElem, schemaDoc, attrValues);

        //Schema Component Constraint: Identity-constraint Definition Properties Correct
        //2 If the {identity-constraint category} is keyref, the cardinality of the {fields} must equal that of the {fields} of the {referenced key}.
        if(key.getFieldCount() != keyRef.getFieldCount()) {
            reportSchemaError("c-props-correct.2" , new Object [] {krName,key.getIdentityConstraintName()}, krElem);
        } else {
            // add key reference to element decl
            // and stuff this in the grammar
            grammar.addIDConstraintDecl(element, keyRef);
        }

        // and put back attributes
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
    } // traverse(Element,int,XSDocumentInfo, SchemaGrammar)
} // XSDKeyrefTraverser

