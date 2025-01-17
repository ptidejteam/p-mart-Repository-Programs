/*
 * Copyright 1999-2002,2004 The Apache Software Foundation.
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

package org.apache.xerces.impl.dtd;

import org.apache.xerces.xni.QName;

/**
 * @xerces.internal
 * 
 * @version $Id: XMLAttributeDecl.java,v 1.1 2006/02/02 00:59:49 vauchers Exp $
 */
public class XMLAttributeDecl {

    //
    // Data
    //

    /** name */
    public final QName name = new QName();

    /** simpleType */
    public final XMLSimpleType simpleType = new XMLSimpleType();

    /** optional */
    public boolean optional;

    //
    // Methods
    //

    /**
     * setValues
     * 
     * @param name 
     * @param simpleType 
     * @param optional 
     */
    public void setValues(QName name, XMLSimpleType simpleType, boolean optional) {
        this.name.setValues(name);
        this.simpleType.setValues(simpleType);
        this.optional   = optional;
    } // setValues

    /**
     * clear
     */
    public void clear() {
        this.name.clear();
        this.simpleType.clear();
        this.optional   = false;
    } // clear

} // class XMLAttributeDecl
