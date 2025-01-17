/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.
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
 * originally based on software copyright (c) 2002, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.impl.xs.psvi;

/**
 * This interface defines common constants for XML Schema APIs.
 *
 * @author Elena Litani, IBM
 * @version $Id: XSConstants.java,v 1.1 2006/02/02 01:41:55 vauchers Exp $
 */
public interface XSConstants {

    // Various types of schema components
    /**
     * An attribute declaration <code>XSAttributeDecl</code>.
     */
    public static final short ATTRIBUTE_DECLARATION     = 1;
    /**
     * An element declaration <code>XSElementDecl</code>.
     */
    public static final short ELEMENT_DECLARATION       = 2;
    /**
     * A complex type definition
     */
    public static final short TYPE_DEFINITION           = 3;
    /**
     * An attribute use definition
     */
    public static final short ATTRIBUTE_USE             = 4;
    /**
     * An attribute group
     */
    public static final short ATTRIBUTE_GROUP           = 5;
    /**
     * A model group definition
     */
    public static final short MODEL_GROUP_DEFINITION    = 6;
    /**
     * A model group.
     */
    public static final short MODEL_GROUP               = 7;
    /**
     * A particle.
     */
    public static final short PARTICLE                  = 8;
    /**
     * A wildcard.
     */
    public static final short WILDCARD                  = 9;
    /**
     * Identity constraint definition.
     */
    public static final short IDENTITY_CONSTRAINT       = 10;
    /**
     * The object describes a notation declaration.
     */
    public static final short NOTATION_DECLARATION      = 11;
    /**
     * An annotation.
     */
    public static final short ANNOTATION                = 12;

    // Derivation methods (for block and final values)
    /**
     * <code>XSTypeDefinition</code> final set or <code>XSElementDecl</code>
     * disallowed substitution group.
     */
    public static final short DERIVATION_NONE           = 0;
    /**
     * <code>XSTypeDefinition</code> final set or <code>XSElementDecl</code>
     * disallowed substitution group.
     */
    public static final short DERIVATION_EXTENSION      = 1<<0;
    /**
     * <code>XSTypeDefinition</code> final set or <code>XSElementDecl</code>
     * disallowed substitution group.
     */
    public static final short DERIVATION_RESTRICTION    = 1<<1;
    /**
     * <code>XSTypeDefinition</code> final set
     */
    public static final short DERIVATION_SUBSTITUTION   = 1<<2;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    public static final short DERIVATION_UNION          = 1<<3;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    public static final short DERIVATION_LIST           = 1<<4;

    // Value constraint types
    /**
     * No value constraint
     */
    public static final short VC_NONE                   = 0;
    /**
     * Indicates that there is a default value constraint.
     */
    public static final short VC_DEFAULT                = 1;
    /**
     * Indicates that there is a fixed value constraint for this attribute.
     */
    public static final short VC_FIXED                  = 2;

    // Scopes
    /**
     * The scope has value of absent in the case of declarations within named
     * model groups or attribute groups: their scope is determined when they
     * are used in the construction of complex type definitions.
     */
    public static final short SCOPE_ABSENT              = 0;
    /**
     * A {scope} of global identifies top-level declarations.
     */
    public static final short SCOPE_GLOBAL              = 1;
    /**
     * Locally scoped declarations are available for use only within the
     * complex type identified by the {scope} property.
     */
    public static final short SCOPE_LOCAL               = 2;

}
