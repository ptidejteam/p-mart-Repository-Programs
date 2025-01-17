/*
 * Copyright 1999-2004 The Apache Software Foundation.
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
 * $Id: ProcessorUnknown.java,v 1.1 2006/03/01 21:15:23 vauchers Exp $
 */
package org.apache.xalan.processor;

import org.xml.sax.Attributes;

/**
 * This class processes an unknown template element.  It is used both 
 * for unknown top-level elements, and for elements in the 
 * xslt namespace when the version is higher than the version 
 * of XSLT that we are set up to process.
 * @xsl.usage internal
 */
public class ProcessorUnknown extends ProcessorLRE
{
    static final long serialVersionUID = 600521151487682248L;

}
