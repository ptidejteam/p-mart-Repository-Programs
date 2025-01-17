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
 * $Id: TransletException.java,v 1.1 2006/03/01 21:15:33 vauchers Exp $
 */

package org.apache.xalan.xsltc;

import org.xml.sax.SAXException;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 */
public final class TransletException extends SAXException {
    static final long serialVersionUID = -878916829521217293L;

    public TransletException() {
	super("Translet error");
    }
    
    public TransletException(Exception e) {
	super(e.toString());
    }
    
    public TransletException(String message) {
	super(message);
    }
}
