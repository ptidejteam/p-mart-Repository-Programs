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

/**
 * @xerces.internal
 * 
 * @version $Id: XMLEntityDecl.java 320090 2004-10-04 22:00:42Z mrglavas $
 */
public class XMLEntityDecl {

    //
    // Data
    //

    /** name */
    public String name;

    /** publicId */
    public String publicId;

    /** systemId */
    public String systemId;

    /** baseSystemId */
    public String baseSystemId;

    /** notation */
    public String notation;

    /** isPE */
    public boolean isPE;

    /** inExternal */
    /** <strong>Note:</strong> flag of where the entity is defined, not whether it is a external entity */
    public boolean inExternal;

    /** Value. */
    public String value;

    //
    // Methods
    //

    /**
     * setValues
     * 
     * @param name 
     * @param publicId 
     * @param systemId 
     * @param baseSystemId 
     * @param notation 
     * @param isPE 
     * @param inExternal
     */
    public void setValues(String name, String publicId, String systemId, 
                          String baseSystemId, String notation, 
                          boolean isPE, boolean inExternal) {
        setValues(name, publicId, systemId, baseSystemId, notation, null, isPE, inExternal);
    }

    /**
     * setValues
     * 
     * @param name 
     * @param publicId 
     * @param systemId 
     * @param baseSystemId 
     * @param value
     * @param notation 
     * @param isPE 
     * @param inExternal
     */
    public void setValues(String name, String publicId, String systemId, 
                          String baseSystemId, String notation, 
                          String value, boolean isPE, boolean inExternal) {
        this.name         = name;
        this.publicId     = publicId;
        this.systemId     = systemId;
        this.baseSystemId = baseSystemId;
        this.notation     = notation;
        this.value        = value;
        this.isPE         = isPE;
        this.inExternal   = inExternal;
    } // setValues(String,String,String,String,String,boolean,boolean)

    /**
     * clear
     */
    public void clear() {
       this.name         = null;
       this.publicId     = null;
       this.systemId     = null;
       this.baseSystemId = null;
       this.notation     = null;
       this.value        = null;
       this.isPE         = false;
       this.inExternal   = false;

    } // clear

} // class XMLEntityDecl
