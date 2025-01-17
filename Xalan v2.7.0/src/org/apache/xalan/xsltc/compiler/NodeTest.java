/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: NodeTest.java,v 1.1 2006/03/01 21:14:43 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.DOM;
import org.apache.xml.dtm.DTM;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
public interface NodeTest {
    public static final int TEXT      = DTM.TEXT_NODE;
    public static final int COMMENT   = DTM.COMMENT_NODE;
    public static final int PI        = DTM.PROCESSING_INSTRUCTION_NODE;
    public static final int ROOT      = DTM.DOCUMENT_NODE;
    public static final int ELEMENT   = DTM.ELEMENT_NODE;
    public static final int ATTRIBUTE = DTM.ATTRIBUTE_NODE;
    
    // generalized type
    public static final int GTYPE     = DTM.NTYPES;
    
    public static final int ANODE     = DOM.FIRST_TYPE - 1;
}
