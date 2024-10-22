/*
 * @(#)$Id: Predicate.java,v 1.1 2006/03/01 20:51:33 vauchers Exp $
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import de.fub.bytecode.classfile.JavaClass;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class Predicate extends Expression {

    private Expression _exp = null; // Expression to be compiled inside pred.
    private String  _className;     // Name of filter to generate
    private boolean _nthPositionFilter = false;
    private boolean _nthDescendant = false;
    private int     _ptype = -1;

    public Predicate(Expression exp) {
	(_exp = exp).setParent(this);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_exp.setParser(parser);
    }

    public boolean isNthDescendant() {
	return _nthDescendant;
    }

    public boolean isNthPositionFilter() {
	return _nthPositionFilter;
    }
    
    protected final boolean isClosureBoundary() {
	return true;
    }
    
    public int getPosType() {
	if (_ptype == -1) {
	    SyntaxTreeNode parent = getParent();
	    if (parent instanceof StepPattern) {
		_ptype = ((StepPattern)parent).getNodeType();
	    }
	    else if (parent instanceof AbsoluteLocationPath) {
		AbsoluteLocationPath path = (AbsoluteLocationPath)parent;
		Expression exp = path.getPath();
		if (exp instanceof Step) {
		    _ptype = ((Step)exp).getNodeType();
		}
	    }
	    else if (parent instanceof VariableRef) {
		Variable var = ((VariableRef)parent).getVariable();
		Expression exp = var.getExpression();
		if (exp instanceof Step) {
		    _ptype = ((Step)exp).getNodeType();
		}
	    }
	    else if (parent instanceof Step) {
		_ptype = ((Step)parent).getNodeType();
	    }
	}
	return _ptype;
    }

    public boolean parentIsPattern() {
	return (getParent() instanceof Pattern);
    }

    public Expression getExpr() {
	return _exp;
    }

    public String toString() {
	if (isNthPositionFilter())
	    return "pred([" + _exp + "],"+getPosType()+")";
	else
	    return "pred(" + _exp + ')';
    }
	
    /**
     * Type check a predicate expression. If the type of the expression is 
     * number convert it to boolean by adding a comparison with position().
     * Note that if the expression is a parameter, we cannot distinguish
     * at compile time if its type is number or not. Hence, expressions of 
     * reference type are always converted to booleans.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	Type texp = _exp.typeCheck(stable);

	if (texp instanceof ReferenceType) {
	    // TODO: print a warning indicating that no expansion is
	    // possible in this case due to lack of type info
	    throw new TypeCheckError(this);
	}

	if (texp instanceof NumberType) {
	    if (texp instanceof IntType == false) {
		_exp = new CastExpr(_exp, Type.Int);
	    }

	    // Expand [last()] into [position() = last()]
	    if ((_exp instanceof LastCall) ||
		(getParent() instanceof Pattern) ||
		(getParent() instanceof FilterExpr)) {
		final QName position = getParser().getQName("position");
		final PositionCall positionCall;
		positionCall = new PositionCall(position, -1);
		positionCall.setParser(getParser());
		positionCall.setParent(this);
		_exp = new EqualityExpr(EqualityExpr.EQ, positionCall, _exp);

		if (_exp.typeCheck(stable) != Type.Boolean) {
		    _exp = new CastExpr(_exp, Type.Boolean);
		}
		_nthPositionFilter = false;
		return _type = Type.Boolean;
	    }
	    // Use NthPositionIterator to handle [position()] or [a]
	    else {
		SyntaxTreeNode parent = getParent();
		if ((parent != null) && (parent instanceof Step)) {
		    parent = parent.getParent();
		    if ((parent != null) &&
			(parent instanceof AbsoluteLocationPath)) {
			// TODO: Special case for "//*[n]" pattern....
			_nthDescendant = true;
			return _type = Type.NodeSet;
		    }
		}
		_nthPositionFilter = true;
		return _type = Type.NodeSet;
	    }
	}
	else if (texp instanceof BooleanType == false) {
	    _exp = new CastExpr(_exp, Type.Boolean);
	}

	_nthPositionFilter = false;
	return _type = Type.Boolean;
    }
	
    /**
     * Create a new "Filter" class implementing
     * <code>CurrentNodeListFilter</code>. Allocate registers for local 
     * variables and local parameters passed in the closure to test().
     * Notice that local variables need to be "unboxed".
     */
    private void compileFilter(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	TestGenerator testGen;
	LocalVariableGen local;
	FilterGenerator filterGen;

	_className = getXSLTC().getHelperClassName();
	filterGen = new FilterGenerator(_className,
					"java.lang.Object",
					toString(), 
					ACC_PUBLIC | ACC_SUPER,
					new String[] {
					    CURRENT_NODE_LIST_FILTER
					},
					classGen.getStylesheet());	

	final InstructionList il = new InstructionList();
	final ConstantPoolGen cpg = filterGen.getConstantPool();
	final String DOM_SIG = classGen.getDOMClassSig();

	testGen = new TestGenerator(ACC_PUBLIC | ACC_FINAL,
				    de.fub.bytecode.generic.Type.BOOLEAN, 
				    new de.fub.bytecode.generic.Type[] {
					de.fub.bytecode.generic.Type.INT,
					de.fub.bytecode.generic.Type.INT,
					de.fub.bytecode.generic.Type.INT,
					de.fub.bytecode.generic.Type.INT,
					Util.getJCRefType(TRANSLET_SIG),
					Util.getJCRefType(NODE_ITERATOR_SIG)
				    },
				    new String[] {
					"node",
					"position",
					"last",
					"current",
					"translet",
					"iterator"
				    },
				    "test", _className, il, cpg);
		
	// Store the dom in a local variable
	local = testGen.addLocalVariable("document",
					 Util.getJCRefType(DOM_SIG),
					 null, null);
	final String className = classGen.getClassName();
	il.append(filterGen.loadTranslet());
	il.append(new CHECKCAST(cpg.addClass(className)));
	il.append(new GETFIELD(cpg.addFieldref(className,
					       DOM_FIELD, DOM_SIG)));
	il.append(new ASTORE(local.getIndex()));

	// Store the dom index in the test generator
	testGen.setDomIndex(local.getIndex());

	_exp.translate(filterGen, testGen);
	il.append(IRETURN);
	
	testGen.stripAttributes(true);
	testGen.setMaxLocals();
	testGen.setMaxStack();
	testGen.removeNOPs();

	filterGen.addEmptyConstructor(ACC_PUBLIC);
	filterGen.addMethod(testGen.getMethod());
		
	getXSLTC().dumpClass(filterGen.getJavaClass());
    }

    /**
     * Translate a predicate expression. This translation pushes
     * two references on the stack: a reference to a newly created
     * filter object and a reference to the predicate's closure.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {

	if (_nthPositionFilter || _nthDescendant) {
	    _exp.translate(classGen, methodGen);
	}
	else {
	    compileFilter(classGen, methodGen);
	
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();
	    il.append(new NEW(cpg.addClass(_className)));
	    il.append(DUP);
	    il.append(new INVOKESPECIAL(cpg.addMethodref(_className,
							 "<init>", "()V")));
	}
    }
}
