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

package org.apache.xerces.impl;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
/**
 * DOM Revalidation handler adds additional functionality to XMLDocumentHandler
 * 
 * @xerces.internal
 * @author Elena Litani, IBM
 * @version $Id: RevalidationHandler.java,v 1.1 2006/02/02 00:59:17 vauchers Exp $
 */
public interface RevalidationHandler extends XMLDocumentFilter {

    /**
     * Character content.
     * 
     * @param data   The character data.
     * @param augs   Augmentations
     * @return True if data is whitespace only
     */
    public boolean characterData(String data, Augmentations augs);
    

} // interface DOMRevalidationHandler
