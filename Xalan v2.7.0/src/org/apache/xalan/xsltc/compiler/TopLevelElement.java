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
 * $Id: TopLevelElement.java,v 1.1 2006/03/01 21:14:42 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

class TopLevelElement extends SyntaxTreeNode {

    /**
     * Type check all the children of this node.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	return typeCheckContents(stable);
    }

    /**
     * Translate this node into JVM bytecodes.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	ErrorMsg msg = new ErrorMsg(ErrorMsg.NOT_IMPLEMENTED_ERR,
				    getClass(), this);
	getParser().reportError(FATAL, msg);
    }
	
    /**
     * Translate this node into a fresh instruction list.
     * The original instruction list is saved and restored.
     */
    public InstructionList compile(ClassGenerator classGen,
				   MethodGenerator methodGen) {
	final InstructionList result, save = methodGen.getInstructionList();
	methodGen.setInstructionList(result = new InstructionList());
	translate(classGen, methodGen);
	methodGen.setInstructionList(save);
	return result;
    }

    public void display(int indent) {
	indent(indent);
	Util.println("TopLevelElement");
	displayContents(indent + IndentIncrement);
    }
}
