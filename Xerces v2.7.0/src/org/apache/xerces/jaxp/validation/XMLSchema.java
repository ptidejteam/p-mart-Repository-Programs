/*
 * Copyright 2005 The Apache Software Foundation.
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

package org.apache.xerces.jaxp.validation;

import org.apache.xerces.xni.grammars.XMLGrammarPool;

/**
 * <p>Implementation of Schema for W3C XML Schemas.</p>
 * 
 * @author Michael Glavassevich, IBM
 * @version $Id: XMLSchema.java,v 1.1 2006/02/02 01:00:07 vauchers Exp $
 */
final class XMLSchema extends AbstractXMLSchema {
    
    /** The grammar pool is immutable */
    private final XMLGrammarPool fGrammarPool;  
    
    /** Constructor */
    public XMLSchema(XMLGrammarPool grammarPool) {
        fGrammarPool = grammarPool;
    }
    
    /*
     * XSGrammarPoolContainer methods
     */
    
    /**
     * <p>Returns the grammar pool contained inside the container.</p>
     * 
     * @return the grammar pool contained inside the container
     */
    public XMLGrammarPool getGrammarPool() {
        return fGrammarPool;
    }

    /**
     * <p>Returns whether the schema components contained in this object
     * can be considered to be a fully composed schema and should be
     * used to exclusion of other schema components which may be
     * present elsewhere.</p>
     * 
     * @return whether the schema components contained in this object
     * can be considered to be a fully composed schema
     */
    public boolean isFullyComposed() {
        return true;
    }
    
} // XMLSchema
