/*
 * Copyright 2001-2004,2006 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.xs.XSObjectList;
import org.w3c.dom.Element;

/**
 * The wildcard schema component traverser.
 *
 * <any
 *   id = ID
 *   maxOccurs = (nonNegativeInteger | unbounded)  : 1
 *   minOccurs = nonNegativeInteger : 1
 *   namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )  : ##any
 *   processContents = (lax | skip | strict) : strict
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?)
 * </any>
 *
 * <anyAttribute
 *   id = ID
 *   namespace = ((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )  : ##any
 *   processContents = (lax | skip | strict) : strict
 *   {any attributes with non-schema namespace . . .}>
 *   Content: (annotation?)
 * </anyAttribute>
 *
 * @xerces.internal 
 *
 * @author Rahul Srivastava, Sun Microsystems Inc.
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSDWildcardTraverser.java 379204 2006-02-20 19:21:23Z nddelima $
 */
class XSDWildcardTraverser extends XSDAbstractTraverser {
    
    /**
     * constructor
     *
     * @param  handler
     * @param  errorReporter
     * @param  gAttrCheck
     */
    XSDWildcardTraverser (XSDHandler handler,
            XSAttributeChecker gAttrCheck) {
        super(handler, gAttrCheck);
    }
    
    
    /**
     * Traverse <any>
     *
     * @param  elmNode
     * @param  schemaDoc
     * @param  grammar
     * @return the wildcard node index
     */
    XSParticleDecl traverseAny(Element elmNode,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar) {
        
        // General Attribute Checking for elmNode
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        XSWildcardDecl wildcard = traverseWildcardDecl(elmNode, attrValues, schemaDoc, grammar);
        
        // for <any>, need to create a new particle to reflect the min/max values
        XSParticleDecl particle = null;
        if (wildcard != null) {
            int min = ((XInt)attrValues[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
            int max = ((XInt)attrValues[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
            if (max != 0) {
                if (fSchemaHandler.fDeclPool !=null) {
                    particle = fSchemaHandler.fDeclPool.getParticleDecl();
                } else {        
                    particle = new XSParticleDecl();
                }
                particle.fType = XSParticleDecl.PARTICLE_WILDCARD;
                particle.fValue = wildcard;
                particle.fMinOccurs = min;
                particle.fMaxOccurs = max;
                particle.fAnnotations = wildcard.fAnnotations;
            }
        }
        
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        
        return particle;
    }
    
    
    /**
     * Traverse <anyAttribute>
     *
     * @param  elmNode
     * @param  schemaDoc
     * @param  grammar
     * @return the wildcard node index
     */
    XSWildcardDecl traverseAnyAttribute(Element elmNode,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar) {
        
        // General Attribute Checking for elmNode
        Object[] attrValues = fAttrChecker.checkAttributes(elmNode, false, schemaDoc);
        XSWildcardDecl wildcard = traverseWildcardDecl(elmNode, attrValues, schemaDoc, grammar);
        fAttrChecker.returnAttrArray(attrValues, schemaDoc);
        
        return wildcard;
    }
    
    
    /**
     *
     * @param  elmNode
     * @param  attrValues
     * @param  schemaDoc
     * @param  grammar
     * @return the wildcard node index
     */
    XSWildcardDecl traverseWildcardDecl(Element elmNode,
            Object[] attrValues,
            XSDocumentInfo schemaDoc,
            SchemaGrammar grammar) {
        
        //get all attributes
        XSWildcardDecl wildcard = new XSWildcardDecl();
        // namespace type
        XInt namespaceTypeAttr = (XInt) attrValues[XSAttributeChecker.ATTIDX_NAMESPACE];
        wildcard.fType = namespaceTypeAttr.shortValue();
        // namespace list
        wildcard.fNamespaceList = (String[])attrValues[XSAttributeChecker.ATTIDX_NAMESPACE_LIST];
        // process contents
        XInt processContentsAttr = (XInt) attrValues[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
        wildcard.fProcessContents = processContentsAttr.shortValue();
        
        //check content
        Element child = DOMUtil.getFirstChildElement(elmNode);
        XSAnnotationImpl annotation = null;
        if (child != null)
        {
            if (DOMUtil.getLocalName(child).equals(SchemaSymbols.ELT_ANNOTATION)) {
                annotation = traverseAnnotationDecl(child, attrValues, false, schemaDoc);
                child = DOMUtil.getNextSiblingElement(child);
            }
            else {
                String text = DOMUtil.getSyntheticAnnotation(elmNode);
                if (text != null) {
                    annotation = traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
                }
            }
            
            if (child != null) {
                reportSchemaError("s4s-elt-must-match.1", new Object[]{"wildcard", "(annotation?)", DOMUtil.getLocalName(child)}, elmNode);
            }
        }
        else {
            String text = DOMUtil.getSyntheticAnnotation(elmNode);
            if (text != null) {
                annotation = traverseSyntheticAnnotation(elmNode, text, attrValues, false, schemaDoc);
            }
        }
        XSObjectList annotations;
        if (annotation != null) {
            annotations = new XSObjectListImpl();
            ((XSObjectListImpl) annotations).add(annotation);
        } else {
            annotations = XSObjectListImpl.EMPTY_LIST;
        }
        wildcard.fAnnotations = annotations;
        
        return wildcard;
        
    } // traverseWildcardDecl
    
} // XSDWildcardTraverser
