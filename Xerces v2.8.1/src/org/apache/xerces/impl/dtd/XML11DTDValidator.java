/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

import org.apache.xerces.impl.Constants;
import org.apache.xerces.xni.parser.XMLComponentManager;

/**
 * This allows the validator to correctlyhandle XML 1.1
 * documents.
 * 
 * @xerces.internal
 *
 * @author Neil Graham
 * @version $Id: XML11DTDValidator.java 320090 2004-10-04 22:00:42Z mrglavas $
 */
public class XML11DTDValidator extends XMLDTDValidator {

    //
    // Constants
    //

    protected final static String DTD_VALIDATOR_PROPERTY =
        Constants.XERCES_PROPERTY_PREFIX+Constants.DTD_VALIDATOR_PROPERTY;

    //
    // Constructors
    //

    /** Default constructor. */
    public XML11DTDValidator() {

        super();
    } // <init>()

    // overridden so that this class has access to the same
    // grammarBucket as the corresponding DTDProcessor
    // will try and use...
    public void reset(XMLComponentManager manager) {
        XMLDTDValidator curr = null;
        if((curr = (XMLDTDValidator)manager.getProperty(DTD_VALIDATOR_PROPERTY)) != null &&
                curr != this) {
            fGrammarBucket = curr.getGrammarBucket();
        }
        super.reset(manager);
    } //reset(XMLComponentManager)

    protected void init() {
        if(fValidation || fDynamicValidation) {
            super.init();
            // now overwrite some entries in parent:

            try {
                fValID       = fDatatypeValidatorFactory.getBuiltInDV("XML11ID");
                fValIDRef    = fDatatypeValidatorFactory.getBuiltInDV("XML11IDREF");
                fValIDRefs   = fDatatypeValidatorFactory.getBuiltInDV("XML11IDREFS");
                fValNMTOKEN  = fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKEN");
                fValNMTOKENS = fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKENS");

            }
            catch (Exception e) {
                // should never happen
                e.printStackTrace(System.err);
            }
        }
    } // init()

} // class XML11DTDValidator
