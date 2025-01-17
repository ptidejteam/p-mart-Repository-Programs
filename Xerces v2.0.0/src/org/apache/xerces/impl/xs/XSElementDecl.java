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

import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.dv.ValidatedInfo;

/**
 * The XML representation for an element declaration
 * schema component is an <element> element information item
 *
 * @author Elena Litani, IBM
 * @author Sandy Gao, IBM
 * @version $Id: XSElementDecl.java,v 1.1 2006/02/02 01:45:04 vauchers Exp $
 */
public class XSElementDecl {

    // types of value constraint
    public final static short     NO_CONSTRAINT       = 0;
    public final static short     DEFAULT_VALUE       = 1;
    public final static short     FIXED_VALUE         = 2;

    // name of the element
    public String fName = null;
    // target namespace of the element
    public String fTargetNamespace = null;
    // type of the element
    public XSTypeDecl fType = null;
    // misc flag of the element: nillable/abstract/fixed
    short fMiscFlags = 0;
    // block set (disallowed substitutions) of the element
    public short fBlock = SchemaSymbols.EMPTY_SET;
    // final set (substitution group exclusions) of the element
    public short fFinal = SchemaSymbols.EMPTY_SET;
    // value constraint value
    public ValidatedInfo fDefault = null;
    // the substitution group affiliation of the element
    public XSElementDecl fSubGroup = null;
    // identity constraints
    static final int INITIAL_SIZE = 2;
    int fIDCPos = 0;
    IdentityConstraint[] fIDConstraints = new IdentityConstraint[INITIAL_SIZE];

    private static final short CONSTRAINT_MASK = 3;
    private static final short NILLABLE        = 4;
    private static final short ABSTRACT        = 8;
    private static final short GLOBAL          = 16;

    // methods to get/set misc flag

    public short getConstraintType() {
        return (short)(fMiscFlags & CONSTRAINT_MASK);
    }
    public boolean isNillable() {
        return ((fMiscFlags & NILLABLE) != 0);
    }
    public boolean isAbstract() {
        return ((fMiscFlags & ABSTRACT) != 0);
    }
    public boolean isGlobal() {
        return ((fMiscFlags & GLOBAL) != 0);
    }

    public void setConstraintType(short constraintType) {
        // first clear the bits
        fMiscFlags ^= (fMiscFlags & CONSTRAINT_MASK);
        // then set the proper one
        fMiscFlags |= (constraintType & CONSTRAINT_MASK);
    }
    public void setIsNillable() {
        fMiscFlags |= NILLABLE;
    }
    public void setIsAbstract() {
        fMiscFlags |= ABSTRACT;
    }
    public void setIsGlobal() {
        fMiscFlags |= GLOBAL;
    }

    public void addIDConstaint(IdentityConstraint idc) {
        if (fIDCPos == fIDConstraints.length) {
            fIDConstraints = resize(fIDConstraints, fIDCPos*2);
        }
        fIDConstraints[fIDCPos++] = idc;
    }

    public IdentityConstraint[] getIDConstraints() {
        if (fIDCPos < fIDConstraints.length) {
            fIDConstraints = resize(fIDConstraints, fIDCPos);
        }
        return fIDConstraints;
    }

    static final IdentityConstraint[] resize(IdentityConstraint[] oldArray, int newSize) {
        IdentityConstraint[] newArray = new IdentityConstraint[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    /**
     * get the string description of this element
     */
    private String fDescription = null;
    public String toString() {
        if (fDescription == null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("\"");
            if (fTargetNamespace != null)
                buffer.append(fTargetNamespace);
            buffer.append("\"");
            buffer.append(":");
            buffer.append(fName);
            fDescription = buffer.toString();
        }
        return fDescription;
    }

    /**
     * get the hash code
     */
    public int hashCode() {
        int code = fName.hashCode();
        if (fTargetNamespace != null)
            code = (code<<16)+fTargetNamespace.hashCode();
        return code;
    }

    /**
     * whether two decls are the same
     */
    public boolean equals(Object o) {
        return o == this;
    }
} // class XMLElementDecl
