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
 * $Id: NotCall.java,v 1.1 2006/03/09 00:07:01 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 */
final class NotCall extends FunctionCall {
    public NotCall(QName fname, Vector arguments) {
	super(fname, arguments);
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();
	argument().translate(classGen, methodGen);
	il.append(ICONST_1);
	il.append(IXOR);
    }

    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) {
	final InstructionList il = methodGen.getInstructionList();
	final Expression exp = argument();
	exp.translateDesynthesized(classGen, methodGen);
	final BranchHandle gotoh = il.append(new GOTO(null));
	_trueList = exp._falseList; 	// swap flow lists
	_falseList = exp._trueList;
	_falseList.add(gotoh);
    }
}
