/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000,2001 The Apache Software Foundation.
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.xni.psvi.AttributePSVI;

import java.util.Vector;


/**
 * Attribute PSV infoset augmentations implementation.
 * The PSVI information for attributes will be available at the startElement call.
 *
 * @author Elena Litani IBM
 */
public class AttributePSVImpl implements AttributePSVI {

    /** attribute declaration */
    protected XSAttributeDecl fDeclaration = null;

    /** type of attribute, simpleType */
    protected XSTypeDecl fTypeDecl = null;

    /** If this attribute was explicitly given a 
     * value in the original document, this is true; otherwise, it is false  */
    protected boolean fSpecified = true;

    /** schema normalized value property */
    protected String fNormalizedValue = null;

    /** member type definition against which attribute was validated */
    protected XSSimpleType fMemberType = null;

    /** validation attempted: none, partial, full */
    protected short fValidationAttempted = AttributePSVI.NO_VALIDATION;

    /** validity: valid, invalid, unknown */
    protected short fValidity = AttributePSVI.UNKNOWN_VALIDITY;

    /** error codes */
    protected Vector fErrorCodes = new Vector(10);

    /** validation context: could be QName or XPath expression*/
    protected String fValidationContext = null;


    //
    // AttributePSVI methods
    //

    /**
     * [member type definition anonymous]
     * @ see http://www.w3.org/TR/xmlschema-1/#e-member_type_definition_anonymous
     * @return true if the {name} of the actual member type definition is absent,
     *         otherwise false.
     */
    public boolean  isMemberTypeAnonymous() {
        return (fMemberType !=null)? fMemberType.isAnonymous():false;
    }


    /**
     * [member type definition name]
     * @see http://www.w3.org/TR/xmlschema-1/#e-member_type_definition_name
     * @return The {name} of the actual member type definition, if it is not absent.
     *         If it is absent, schema processors may, but need not, provide a
     *         value unique to the definition.
     */
    public String   getMemberTypeName() {
        return (fMemberType !=null)? fMemberType.getTypeName():null;
    }

    /**
     * [member type definition namespace]
     * @see http://www.w3.org/TR/xmlschema-1/#e-member_type_definition_namespace
     * @return The {target namespace} of the actual member type definition.
     */
    public String   getMemberTypeNamespace() {
        return (fMemberType !=null)? fMemberType.getTargetNamespace():null;
    }

    /**
     * [schema default]
     *
     * @return The canonical lexical representation of the declaration's {value constraint} value.
     * @see http://www.w3.org/TR/xmlschema-1/#e-schema_default
     */
    public String   schemaDefault() {
        Object dValue = null;
        if( fDeclaration !=null ) {
            dValue = fDeclaration.fDefault;
        }
        return(dValue != null)?dValue.toString():null;
    }

    /**
     * [schema normalized value]
     *
     *
     * @see http://www.w3.org/TR/xmlschema-1/#e-schema_normalized_value
     * @return
     */
    public String schemaNormalizedValue() {
        return fNormalizedValue;
    }

    /**
     * [schema specified]
     * 
     * @return if return is true - schema, otherwise - infoset
     * @see http://www.w3.org/TR/xmlschema-1/#e-schema_specified
     */
    public boolean schemaSpecified() {
        return fSpecified;
    }


    /**
     * [type definition anonymous]
     * @see http://www.w3.org/TR/xmlschema-1/#e-type_definition_anonymous
     * @return true if the {name} of the type definition is absent, otherwise false.
     */
    public boolean isTypeAnonymous() {
        return (fTypeDecl !=null)? fTypeDecl.isAnonymous():false;
    }

    /**
     * [type definition name]
     * @see http://www.w3.org/TR/xmlschema-1/#e-type_definition_name
     * @return The {name} of the type definition, if it is not absent.
     *         If it is absent, schema processors may, but need not,
     *         provide a value unique to the definition.
     */
    public String getTypeName() {
        return (fTypeDecl !=null)? fTypeDecl.getTypeName():null;
    }

    /**
     * [type definition namespace]
     * @see http://www.w3.org/TR/xmlschema-1/#e-member_type_definition_namespace
     * @return The {target namespace} of the type definition.
     */
    public String getTypeNamespace() {
        return (fTypeDecl !=null)? fTypeDecl.getTargetNamespace():null;
    }

    /**
     * [type definition type]
     *
     *  @see http://www.w3.org/TR/xmlschema-1/#a-type_definition_type
     *  @see http://www.w3.org/TR/xmlschema-1/#e-type_definition_type
     *  @return simple or complex, depending on the type definition.
     */
    public short getTypeDefinitionType() {
        return XSTypeDecl.SIMPLE_TYPE;
    }

    /**
     * Determines the extent to which the document has been validated
     *
     * @return return the [validation attempted] property. The possible values are
     *         NO_VALIDATION, PARTIAL_VALIDATION and FULL_VALIDATION
     */
    public short getValidationAttempted() {
        return fValidationAttempted;
    }

    /**
     * Determine the validity of the node with respect
     * to the validation being attempted
     *
     * @return return the [validity] property. Possible values are:
     *         UNKNOWN_VALIDITY, INVALID_VALIDITY, VALID_VALIDITY
     */
    public short getValidity() {
        return fValidity;
    }

    /**
     * A list of error codes generated from validation attempts.
     * Need to find all the possible subclause reports that need reporting
     *
     * @return Array of error codes
     */
    public String[] getErrorCodes() {
        // REVISIT: can we make it more efficient?
        int size = fErrorCodes.size();
        // copy errors from the list to an string array
        String[] errors = new String[size];
        for (int i = 0; i < size; i++) {
            errors[i] = (String)fErrorCodes.elementAt(i);
        }
        return errors;

    }


    // This is the only information we can provide in a pipeline.
    public String getValidationContext() {
        return fValidationContext;
    }


    /**
     * Reset() 
     */
    public void reset() {
        fDeclaration = null;
        fTypeDecl = null;
        fSpecified = true;
        fMemberType = null;
        fValidationAttempted = AttributePSVI.NO_VALIDATION;
        fValidity = AttributePSVI.UNKNOWN_VALIDITY;
        fErrorCodes.setSize(0);
        fValidationContext = null;
    }

    public void addErrorCode(String key){
        fErrorCodes.addElement(key);
    }
}
