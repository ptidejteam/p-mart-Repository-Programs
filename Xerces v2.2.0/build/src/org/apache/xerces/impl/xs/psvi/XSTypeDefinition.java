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
 * This interface represents a complex or simple type definition.
 *
 * @author Elena Litani, IBM
 * @version $Id: XSTypeDefinition.java,v 1.1 2006/02/02 01:38:39 vauchers Exp $
 */
public interface XSTypeDefinition extends XSObject {

    /**
     * This constant value signifies a complex type.
     */
    public static final short COMPLEX_TYPE   = XSConstants.ANNOTATION+1;
    /**
     * This constant value signifies a simple type.
     */
    public static final short SIMPLE_TYPE    = XSConstants.ANNOTATION+2;

    /**
     * Return whether this type definition is a simple type or complex type.
     */
    public short getTypeCategory();
    
    /**
     * {base type definition} Either a simple type definition or a complex
     * type definition.
     */
    public XSTypeDefinition getBaseType();

    /**
     * {final} For complex type definition it is a subset of {extension,
     * restriction}. For simple type definition it is a subset of
     * {extension, list, restriction, union}.
     * @param derivation  Extension, restriction, list, union constants
     *   (defined in <code>XSConstants</code>).
     * @return True if derivation is in the final set, otherwise false.
     */
    public boolean getIsFinal(short derivation);

    /**
     * {final} For complex type definition it is a subset of {extension,
     * restriction}. For simple type definition it is a subset of
     * {extension, list, restriction, union}.
     * @return A bit flag that represents:
     *         {extension, restriction) or none for complexTypes;
     *         {extension, list, restriction, union} or none for simpleTypes;
     */
    public short getFinal();

    /**
     * A boolean that specifies if the type definition is anonymous.
     * Convenience attribute. This is a field is not part of
     * XML Schema component model.
     */
    public boolean getIsAnonymous();

}
