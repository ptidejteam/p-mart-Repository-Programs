/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
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

package org.apache.xerces.validators.common;

import org.apache.xerces.utils.QName;
import org.apache.xerces.validators.datatype.DatatypeValidator;

/**
 * @version $Id: XMLAttributeDecl.java,v 1.1 2006/02/02 02:30:36 vauchers Exp $
 */
public class XMLAttributeDecl {

    //
    // Constants
    //

    // dtd

    public static final int TYPE_CDATA = 0;
    public static final int TYPE_ENTITY = 1;
    public static final int TYPE_ENUMERATION = 2;
    public static final int TYPE_ID = 3;
    public static final int TYPE_IDREF = 4;
    public static final int TYPE_NMTOKEN = 5;
    public static final int TYPE_NOTATION = 6;

    // schema

    public static final int TYPE_SIMPLE = 7;

    public static final int TYPE_ANY_ANY = 8;
    public static final int TYPE_ANY_OTHER = 9;
    //"Local" is treated as a member of the list
    //public static final int TYPE_ANY_LOCAL = 10;
    public static final int TYPE_ANY_LIST = 11;

    // default types in DTD
    public static final int DEFAULT_TYPE_IMPLIED = 1;
    public static final int DEFAULT_TYPE_REQUIRED = 2;
    public static final int DEFAULT_TYPE_PROHIBITED = 4;
    public static final int DEFAULT_TYPE_DEFAULT = 8;
    public static final int DEFAULT_TYPE_FIXED = 16;
    public static final int DEFAULT_TYPE_REQUIRED_AND_FIXED = DEFAULT_TYPE_REQUIRED | DEFAULT_TYPE_FIXED;

    // "use" of Schema attributes
    public static final int USE_TYPE_OPTIONAL = DEFAULT_TYPE_IMPLIED;
    public static final int USE_TYPE_REQUIRED = DEFAULT_TYPE_REQUIRED;
    public static final int USE_TYPE_PROHIBITED = DEFAULT_TYPE_PROHIBITED;
    // value constraints of Schema attributes
    public static final int VALUE_CONSTRAINT_DEFAULT = DEFAULT_TYPE_DEFAULT;
    public static final int VALUE_CONSTRAINT_FIXED = DEFAULT_TYPE_FIXED;

    // schema: attribte wildcard processContents property, share the defaultType field
    public static final int PROCESSCONTENTS_STRICT = 1024;
    public static final int PROCESSCONTENTS_LAX = 2048;
    public static final int PROCESSCONTENTS_SKIP = 4096;


    //
    // Data
    //

    // basic information

    public QName name = new QName();

    // simple types

    public DatatypeValidator datatypeValidator;

    // Att types, e.g. ID, IDREF, NOTATION, NMTOKEN,

    public int type;

    public boolean list;

    // values
    public int enumeration;

    // For DTD, this variable stores "#IMPLIED", "#REQUIRED" and "#FIXED" etc.
    // For Schema, this variable stored the options among "use", "default" and "fixed"
    //    via binary '|'
    public int defaultType;

    public String defaultValue;

    //
    // Constructors
    //

    public XMLAttributeDecl() {
        clear();
    }

    public XMLAttributeDecl(XMLAttributeDecl attributeDecl) {
        setValues(attributeDecl);
    }

    //
    // Public methods
    //

    public void clear() {
        name.clear();
        datatypeValidator = null;
        type = -1;
        list = false;
        enumeration = -1;
        defaultType = DEFAULT_TYPE_IMPLIED;
        defaultValue = null;
    }

    public void setValues(XMLAttributeDecl attributeDecl) {
        name.setValues(attributeDecl.name);
        datatypeValidator = attributeDecl.datatypeValidator;
        type = attributeDecl.type;
        list = attributeDecl.list;
        enumeration = attributeDecl.enumeration;
        defaultType = attributeDecl.defaultType;
        defaultValue = attributeDecl.defaultValue;
    }

    //
    // Object methods
    //

    public int hashCode() {
        // TODO
        return super.hashCode();
    }

    public boolean equals(Object object) {
        // TODO
        return super.equals(object);
    }

} // class XMLAttributeDecl
