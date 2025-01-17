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
import org.apache.xerces.impl.dv.ValidatedInfo;

/**
 * The XML representation for an attribute declaration
 * schema component is an <attribute> element information item
 *
 * @author Elena Litani, IBM
 * @author Sandy Gao, IBM
 * @version $Id: XSAttributeDecl.java,v 1.5 2002/01/29 01:15:14 lehors Exp $
 */
public class XSAttributeDecl {

    // types of value constraint
    public final static short     NO_CONSTRAINT       = 0;
    public final static short     DEFAULT_VALUE       = 1;
    public final static short     FIXED_VALUE         = 2;

    // the name of the attribute
    public String fName = null;
    // the target namespace of the attribute
    public String fTargetNamespace = null;
    // the simple type of the attribute
    public XSSimpleType fType = null;
    // value constraint type: default, fixed or !specified
    short fMiscFlags = 0;
    // value constraint value
    public ValidatedInfo fDefault = null;

    private static final short CONSTRAINT_MASK = 3;
    private static final short GLOBAL          = 4;

    // methods to get/set misc flag

    public short getConstraintType() {
        return (short)(fMiscFlags & CONSTRAINT_MASK);
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
    public void setIsGlobal() {
        fMiscFlags |= GLOBAL;
    }

} // class XSAttributeDecl
