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

package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.impl.xs.models.CMBuilder;

/**
 * The XML representation for a complexType
 * schema component is a <complexType> element information item
 *
 * @author Elena Litani, IBM
 * @author Sandy Gao, IBM
 * @version $Id: XSComplexTypeDecl.java,v 1.1 2006/02/02 01:45:03 vauchers Exp $
 */
public class XSComplexTypeDecl implements XSTypeDecl {

    // content types of complextype
    public static final short CONTENTTYPE_EMPTY   = 0;
    public static final short CONTENTTYPE_SIMPLE  = 1;
    public static final short CONTENTTYPE_MIXED   = 2;
    public static final short CONTENTTYPE_ELEMENT = 3;

    // name of the complexType
    public String fName = null;

    // target namespace of the complexType
    public String fTargetNamespace = null;

    // base type of the complexType
    public XSTypeDecl fBaseType = null;

    // derivation method of the complexType
    public short fDerivedBy = SchemaSymbols.RESTRICTION;

    // final set of the complexType
    public short fFinal = SchemaSymbols.EMPTY_SET;

    // block set (prohibited substitution) of the complexType
    public short fBlock = SchemaSymbols.EMPTY_SET;

    // flags: whether is abstract; whether contains ID type;
    //        whether it's an anonymous tpye
    public short fMiscFlags = 0;

    // the attribute group that holds the attribute uses and attribute wildcard
    public XSAttributeGroupDecl fAttrGrp = new XSAttributeGroupDecl();

    // the content type of the complexType
    public short fContentType = CONTENTTYPE_EMPTY;

    // if the content type is simple, then the corresponding simpleType
    public XSSimpleType fXSSimpleType = null;

    // if the content type is element or mixed, the particle
    public XSParticleDecl fParticle = null;

    // if there is a particle, the content model corresponding to that particle
    public XSCMValidator fCMValidator = null;

    // REVISIT: when XSTypeDecl becomes a class, remove this method
    public short getXSType () {
        return COMPLEX_TYPE;
    }

    // REVISIT: when XSTypeDecl becomes a class, remove this method
    public String getTypeName() {
        return fName;
    }

    public short getFinalSet(){
        return fFinal;
    }

    public String getTargetNamespace(){
        return fTargetNamespace;
    }

    public XSTypeDecl getBaseType(){
        return fBaseType;
    }

    // flags for the misc flag
    private static final short CT_IS_ABSTRACT = 1;
    private static final short CT_HAS_TYPE_ID = 2;
    private static final short CT_IS_ANONYMOUS = 4;

    // methods to get/set misc flag

    public boolean isAbstractType() {
        return ((fMiscFlags & CT_IS_ABSTRACT) != 0);
    }
    public boolean containsTypeID () {
        return ((fMiscFlags & CT_HAS_TYPE_ID) != 0);
    }
    public boolean isAnonymous() {
        return ((fMiscFlags & CT_IS_ANONYMOUS) != 0);
    }

    public void setIsAbstractType() {
        fMiscFlags |= CT_IS_ABSTRACT;
    }
    public void setContainsTypeID() {
        fMiscFlags |= CT_HAS_TYPE_ID;
    }
    public void setIsAnonymous() {
        fMiscFlags |= CT_IS_ANONYMOUS;
    }

    public XSCMValidator getContentModel(CMBuilder cmBuilder) {
        if (fCMValidator != null)
            return fCMValidator;
        fCMValidator = cmBuilder.getContentModel(this);

        return fCMValidator;
    }

    public String toString() {

        StringBuffer str = new StringBuffer();
        appendTypeInfo(str);
        return str.toString();
    }

    void appendTypeInfo(StringBuffer str) {
        String contentType[] = {"EMPTY", "SIMPLE", "MIXED", "ELEMENT"};
        String derivedBy[] = {"EMPTY", "EXTENSION", "RESTRICTION"};

        str.append("Complex type name='" + fTargetNamespace + "," + getTypeName() + "', ");
        if (fBaseType != null)
          str.append(" base type name='" + fBaseType.getTypeName() + "', ");

        str.append(" content type='" + contentType[fContentType] + "', ");
        str.append(" isAbstract='" + isAbstractType() + "', ");
        str.append(" hasTypeId='" + containsTypeID() + "', ");
        str.append(" final='" + fFinal + "', ");
        str.append(" block='" + fBlock + "', ");
        if (fParticle != null)
          str.append(" particle='" + fParticle.toString() + "', ");
        str.append(" derivedBy='" + derivedBy[fDerivedBy] + "'. ");

    }

} // class XSComplexTypeDecl
