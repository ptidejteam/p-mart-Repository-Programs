/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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

package org.apache.xerces.impl.msg;

import java.util.ListResourceBundle;

/**
 * This file contains error and warning messages for the Schema validator
 * The messages are arranged in key and value tuples in a ListResourceBundle.
 *
 * @version $Id: SchemaMessages.java,v 1.1.2.2 2000/10/02 00:40:05 lehors Exp $
 */
public class SchemaMessages extends ListResourceBundle {
    /** The list resource bundle contents. */
    public static final Object CONTENTS[][] = {
// Internal message formatter messages
        { "BadMajorCode", "The majorCode parameter to createMessage was out of bounds." },
        { "FormatFailed", "An internal error occurred while formatting the following message:\n  " },
        { "NoValidatorFor", "No validator for datatype {0}" },
        { "IncorrectDatatype", "Incorrect datatype: {0}" },
        { "NotADatatype", "{0} is not a datatype." },
        { "TextOnlyContentWithType", "The content attribute must be 'textOnly' if you specify a type attribute." },
        { "FeatureUnsupported", "{0} is unsupported" },
        { "NestedOnlyInElemOnly", "Nested Element decls only allowed in elementOnly content" },
        { "EltRefOnlyInMixedElemOnly", "Element references only allowed in mixed or elementOnly content"},
        { "OnlyInEltContent", "{0} only allowed in elementOnly content."},
        { "OrderIsAll", "{0} not allowed if the order is all."},
        { "DatatypeWithType", "Datatype qualifiers can only be used if you specify a type attribute."},
        { "DatatypeQualUnsupported", "The datatype qualifier {0} is not supported."},
        { "GroupContentRestricted", "Error: {0} content must be one of element, group, modelGroupRef.  Saw {1}"},
        { "UnknownBaseDatatype", "Unknown base type {0} for type {1}." },
        { "OneOfTypeRefArchRef", "Can only have one of type and ref attributes."},
        { "NoContentForRef", "Cannot have child content for an element declaration that has a ref attribute" },
        { "IncorrectDefaultType", "Incorrect type for {0}'s default value: {1}" },
        { "IllegalAttContent", "Illegal content {0} in attribute group" },
        { "ValueNotInteger", "Value of {0} is not an integer." },
        { "DatatypeError", "Datatype error: {0}." },
		{ "TypeAlreadySet", "The type of the element has already been declared." },
		{ "GenericError", "Schema error: {0}." },
		{ "UnexpectedError", "UnexpectedError" },
   };
    
    /** Returns the list resource bundle contents. */
    public Object[][] getContents() {
        return CONTENTS;
    }

}
