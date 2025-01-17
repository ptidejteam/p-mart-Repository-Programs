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
 * 3.10.1 The Wildcard Schema Component.
 *
 * @author Elena Litani, IBM
 * @version $Id: XSWildcard.java,v 1.2 2002/06/17 18:41:46 elena Exp $
 */
public interface XSWildcard extends XSTerm {

    /**
     * Process content strict. There must be a top-level declaration for
     * the item available, or the item must have an xsi:type, and the item
     * must be valid as appropriate.
     */
    public static final short PC_STRICT             = 1;
    /**
     * Process content skip. No constraints at all: the item must simply
     * be well-formed XML.
     */
    public static final short PC_SKIP               = 2;
    /**
     * Process content lax. If the item, or any items among its [children] if
     * it's an element information item, has a uniquely
     * determined declaration available, it must be valid
     * with respect to that definition, that is, validate
     *  where you can, don't worry when you can't.
     */
    public static final short PC_LAX                = 3;

    /**
     * Namespace Constraint: any namespace is allowed
     */
    public static final short NSCONSTRAINT_ANY      = 1;
    /**
     * Namespace Constraint: namespaces in the list are not allowed
     */
    public static final short NSCONSTRAINT_NOT      = 2;
    /**
     * Namespace Constraint: namespaces in the liast are allowed
     */
    public static final short NSCONSTRAINT_LIST     = 3;

    /**
     * Namespace constraint: A constraint type: any, not, list.
     */
    public short getConstraintType();

    /**
     * Namespace constraint. For <code>constraintType</code>
     * LIST_NSCONSTRAINT, the list contains allowed namespaces. For
     * <code>constraintType</code> NOT_NSCONSTRAINT, the list contains
     * disallowed namespaces.
     */
    public StringList getNSConstraintList();

    /**
     * {process contents} One of skip, lax or strict. Valid constants values
     * are: SKIP_PROCESS, LAX_PROCESS, STRING_PROCESS.
     */
    public short getProcessContents();

    /**
     * Optional. Annotation.
     */
    public XSAnnotation getAnnotation();

}
