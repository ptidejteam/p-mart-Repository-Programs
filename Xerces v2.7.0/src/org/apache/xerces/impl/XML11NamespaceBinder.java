/*
 * Copyright 2000-2002,2004 The Apache Software Foundation.
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

package org.apache.xerces.impl;


/**
 * This class performs namespace binding on the startElement and endElement
 * method calls in accordance with Namespaces in XML 1.1.  It extends the standard,
 * Namespace-1.0-compliant binder in order to do this.
 * 
 * @xerces.internal
 *
 * @author Neil Graham, IBM
 *
 * @version $Id: XML11NamespaceBinder.java,v 1.1 2006/02/02 00:59:17 vauchers Exp $
 */
public class XML11NamespaceBinder extends XMLNamespaceBinder {

    //
    // Constants
    //

    //
    // Data
    //

    //
    // Constructors
    //

    /** Default constructor. */
    public XML11NamespaceBinder() {
    } // <init>()
    //
    // Public methods
    //

    //
    // Protected methods
    //

    // returns true iff the given prefix is bound to "" *and*
    // this is disallowed by the version of XML namespaces in use.
    protected boolean prefixBoundToNullURI(String uri, String localpart) {
        return false;
    } // prefixBoundToNullURI(String, String):  boolean

} // class XML11NamespaceBinder
