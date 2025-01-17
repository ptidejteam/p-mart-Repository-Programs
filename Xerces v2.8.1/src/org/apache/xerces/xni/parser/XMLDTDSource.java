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

package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XMLDTDHandler;

/**
 * Defines a DTD source. In other words, any object that implements
 * this interface is able to emit DTD "events" to the registered
 * DTD handler. These events could be produced by parsing an XML
 * document's internal or external subset, could be generated from 
 * some other source, or could be created programmatically. This 
 * interface does not say <em>how</em> the events are created, only 
 * that the implementor is able to emit them.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLDTDSource.java 319806 2004-02-24 23:15:58Z mrglavas $
 */
public interface XMLDTDSource {

    //
    // XMLDTDSource methods
    //

    /** Sets the DTD handler. */
    public void setDTDHandler(XMLDTDHandler handler);

    /** Returns the DTD handler. */
    public XMLDTDHandler getDTDHandler();

} // interface XMLDTDSource
