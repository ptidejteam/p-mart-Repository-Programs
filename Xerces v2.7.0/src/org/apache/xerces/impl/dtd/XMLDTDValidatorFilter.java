/*
 * Copyright 2001, 2002,2004 The Apache Software Foundation.
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

import org.apache.xerces.xni.parser.XMLDocumentFilter;

/**
 * Defines a DTD Validator filter to allow
 * components to query the DTD validator.
 * 
 * @xerces.internal
 * 
 * @author Elena Litani, IBM
 *
 * @version $Id: XMLDTDValidatorFilter.java,v 1.1 2006/02/02 00:59:49 vauchers Exp $
 */
public interface XMLDTDValidatorFilter 
    extends XMLDocumentFilter {

    /**
     * Returns true if the validator has a DTD grammar
     * 
     * @return true if the validator has a DTD grammar
     */
    public boolean hasGrammar();

    /**
     * Return true if validator must validate the document
     * 
     * @return true if validator must validate the document
     */
    public boolean validate();


} // interface XMLDTDValidatorFilter
