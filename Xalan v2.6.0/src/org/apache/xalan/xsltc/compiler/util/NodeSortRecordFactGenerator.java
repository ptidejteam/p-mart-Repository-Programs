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
 * $Id: NodeSortRecordFactGenerator.java,v 1.1 2006/03/09 00:07:37 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.Stylesheet;

/**
 * Generator for subclasses of NodeSortRecordFactory.
 * @author Santiago Pericas-Geertsen
 */
public final class NodeSortRecordFactGenerator extends ClassGenerator {

    public NodeSortRecordFactGenerator(String className, String superClassName,
				   String fileName,
				   int accessFlags, String[] interfaces,
				   Stylesheet stylesheet) {
	super(className, superClassName, fileName,
	      accessFlags, interfaces, stylesheet);
    }
    
    /**
     * Returns <tt>true</tt> since this class is external to the
     * translet.
     */
    public boolean isExternal() {
	return true;
    }
}
