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
 * $Id: NamespaceUriCall.java,v 1.1 2006/03/01 21:14:42 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

/**
 * @author Morten Jorgensen
 */
final class NamespaceUriCall extends NameBase {

    /**
     * Handles calls with no parameter (current node is implicit parameter).
     */
    public NamespaceUriCall(QName fname) {
	super(fname);
    }

    /**
     * Handles calls with one parameter (either node or node-set).
     */
    public NamespaceUriCall(QName fname, Vector arguments) {
	super(fname, arguments);
    }

    /**
     * Translate code that leaves a node's namespace URI (as a String)
     * on the stack
     */
    public void translate(ClassGenerator classGen,
			  MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// Returns the string value for a node in the DOM
	final int getNamespace = cpg.addInterfaceMethodref(DOM_INTF,
							   "getNamespaceName",
							   "(I)"+STRING_SIG);
	super.translate(classGen, methodGen);
	il.append(new INVOKEINTERFACE(getNamespace, 2));
    }
}
