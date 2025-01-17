/*
 * @(#)$Id: EqualityExpr.java,v 1.1 2006/03/01 20:57:44 vauchers Exp $
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
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.runtime.Operators;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class EqualityExpr extends Expression implements Operators {
    private final int _op;
    private Expression _left;
    private Expression _right;
		
    public EqualityExpr(int op, Expression left, Expression right) {
	_op = op;
	(_left = left).setParent(this);
	(_right = right).setParent(this);
    }

    public void setParser(Parser parser) {
	super.setParser(parser);
	_left.setParser(parser);
	_right.setParser(parser);
    }
    
    public String toString() {
	return Operators.names[_op] + '(' + _left + ", " + _right + ')';
    }

    public Expression getLeft() {
	return _left;
    }

    public Expression getRight() {
	return _right;
    }

    public boolean getOp() {
	if (_op == Operators.NE)
	    return false;
	else
	    return true;
    }

    /**
     * Returns true if this expressions contains a call to position(). This is
     * needed for context changes in node steps containing multiple predicates.
     */
    public boolean hasPositionCall() {
	if (_left.hasPositionCall()) return true;
	if (_right.hasPositionCall()) return true;
	return false;
    }

    public boolean hasLastCall() {
	if (_left.hasLastCall()) return true;
	if (_right.hasLastCall()) return true;
	return false;
    }

    private void swapArguments() {
	final Expression temp = _left;
	_left = _right;
	_right = temp;
    }

    /**
     * Typing rules: see XSLT Reference by M. Kay page 345.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type tleft = _left.typeCheck(stable); 
	final Type tright = _right.typeCheck(stable);

	if (tleft.isSimple() && tright.isSimple()) {
	    if (tleft != tright) {
		if (tleft instanceof BooleanType) {
		    _right = new CastExpr(_right, Type.Boolean);
		}
		else if (tright instanceof BooleanType) {
		    _left = new CastExpr(_left, Type.Boolean);
		}
		else if (tleft instanceof NumberType || 
			 tright instanceof NumberType) {
		    _left = new CastExpr(_left, Type.Real);
		    _right = new CastExpr(_right, Type.Real);
		}
		else {		// both compared as strings
		    _left = new CastExpr(_left,   Type.String);
		    _right = new CastExpr(_right, Type.String);
		}
	    }
	}
	else if (tleft instanceof ReferenceType) {
	    _right = new CastExpr(_right, Type.Reference);
	}
	else if (tright instanceof ReferenceType) {
	    _left = new CastExpr(_left, Type.Reference);
	}
	// the following 2 cases optimize @attr|.|.. = 'string'
	else if (tleft instanceof NodeType && tright == Type.String) {
	    _left = new CastExpr(_left, Type.String);
	}
	else if (tleft == Type.String && tright instanceof NodeType) {
	    _right = new CastExpr(_right, Type.String);
	}
	// optimize node/node
	else if (tleft instanceof NodeType && tright instanceof NodeType) {
	    _left = new CastExpr(_left, Type.String);
	    _right = new CastExpr(_right, Type.String);
	}
	else if (tleft instanceof NodeType && tright instanceof NodeSetType) {
	    // compare(Node, NodeSet) will be invoked
	}
	else if (tleft instanceof NodeSetType && tright instanceof NodeType) {
	    swapArguments();	// for compare(Node, NodeSet)
	}
	else {	
	    // At least one argument is of type node, node-set or result-tree

	    // Promote an expression of type node to node-set
	    if (tleft instanceof NodeType) {
		_left = new CastExpr(_left, Type.NodeSet);
	    }
	    if (tright instanceof NodeType) {
		_right = new CastExpr(_right, Type.NodeSet);
	    }

	    // If one arg is a node-set then make it the left one
	    if (tleft.isSimple() ||
		tleft instanceof ResultTreeType &&
		tright instanceof NodeSetType) {
		swapArguments();
	    }

	    // Promote integers to doubles to have fewer compares
	    if (_right.getType() instanceof IntType) {
		_right = new CastExpr(_right, Type.Real);
	    }
	}
	return _type = Type.Boolean;
    }

    public void translateDesynthesized(ClassGenerator classGen,
				       MethodGenerator methodGen) {
	final Type tleft = _left.getType();
	final InstructionList il = methodGen.getInstructionList();

	if (tleft instanceof BooleanType) {
	    _left.translate(classGen, methodGen);
	    _right.translate(classGen, methodGen);
	    _falseList.add(il.append(_op == Operators.EQ ? 
				     (BranchInstruction)new IF_ICMPNE(null) :
				     (BranchInstruction)new IF_ICMPEQ(null)));
	}
	else if (tleft instanceof NumberType) {
	    _left.translate(classGen, methodGen);
	    _right.translate(classGen, methodGen);

	    if (tleft instanceof RealType) {
		il.append(DCMPG);
		_falseList.add(il.append(_op == Operators.EQ ? 
					 (BranchInstruction)new IFNE(null) : 
					 (BranchInstruction)new IFEQ(null)));
	    }
	    else {
		_falseList.add(il.append(_op == Operators.EQ ? 
					 (BranchInstruction)new IF_ICMPNE(null) :
					 (BranchInstruction)new IF_ICMPEQ(null)));
	    }
	}
	else {
	    translate(classGen, methodGen);
	    desynthesize(classGen, methodGen);
	}
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	final Type tleft = _left.getType();
	Type tright = _right.getType();

	if (tleft instanceof BooleanType || tleft instanceof NumberType) {
	    translateDesynthesized(classGen, methodGen);
	    synthesize(classGen, methodGen);
	    return;
	}

	if (tleft instanceof StringType) {
	    final int equals = cpg.addMethodref(STRING_CLASS,
						"equals",
						"(" + OBJECT_SIG +")Z");
	    _left.translate(classGen, methodGen);
	    _right.translate(classGen, methodGen);
	    il.append(new INVOKEVIRTUAL(equals));

	    if (_op == Operators.NE) {
		il.append(ICONST_1);
		il.append(IXOR);			// not x <-> x xor 1
	    }
	    return;
	}

	BranchHandle truec, falsec;
	
	if (tleft instanceof ResultTreeType) {
	    if (tright instanceof BooleanType) {
		_right.translate(classGen, methodGen);
		if (_op == Operators.NE) {
		    il.append(ICONST_1);
		    il.append(IXOR); // not x <-> x xor 1
		}
		return;
	    }

	    if (tright instanceof RealType) {
		_left.translate(classGen, methodGen);
		tleft.translateTo(classGen, methodGen, Type.Real);
		_right.translate(classGen, methodGen);

		il.append(DCMPG);
		falsec = il.append(_op == Operators.EQ ? 
				   (BranchInstruction) new IFNE(null) : 
				   (BranchInstruction) new IFEQ(null));
		il.append(ICONST_1);
		truec = il.append(new GOTO(null));
		falsec.setTarget(il.append(ICONST_0));
		truec.setTarget(il.append(NOP));
		return;
	    }

	    // Next, result-tree/string and result-tree/result-tree comparisons

	    _left.translate(classGen, methodGen);
	    tleft.translateTo(classGen, methodGen, Type.String);
	    _right.translate(classGen, methodGen);

	    if (tright instanceof ResultTreeType) {
		tright.translateTo(classGen, methodGen, Type.String);
	    }

	    final int equals = cpg.addMethodref(STRING_CLASS,
						"equals",
						"(" +OBJECT_SIG+ ")Z");
	    il.append(new INVOKEVIRTUAL(equals));

	    if (_op == Operators.NE) {
		il.append(ICONST_1);
		il.append(IXOR);			// not x <-> x xor 1
	    }
	    return;
	}

	if (tleft instanceof NodeSetType && tright instanceof BooleanType) {
	    _left.translate(classGen, methodGen);
	    _left.startResetIterator(classGen, methodGen);
	    Type.NodeSet.translateTo(classGen, methodGen, Type.Boolean);
	    _right.translate(classGen, methodGen);

	    il.append(IXOR); // x != y <-> x xor y
	    if (_op == EQ) {
		il.append(ICONST_1);
		il.append(IXOR); // not x <-> x xor 1
	    }
	    return;
	}

	if (tleft instanceof NodeSetType && tright instanceof StringType) {
	    _left.translate(classGen, methodGen);
	    _left.startResetIterator(classGen, methodGen); // needed ?
	    _right.translate(classGen, methodGen);
	    il.append(new PUSH(cpg, _op));
	    il.append(methodGen.loadDOM());
	    final int cmp = cpg.addMethodref(BASIS_LIBRARY_CLASS,
					     "compare",
					     "("
					     + tleft.toSignature() 
					     + tright.toSignature()
					     + "I"
					     + DOM_INTF_SIG
					     + ")Z");
	    il.append(new INVOKESTATIC(cmp));
	    return;
	}

	// Next, node-set/t for t in {real, string, node-set, result-tree}
	_left.translate(classGen, methodGen);
	_left.startResetIterator(classGen, methodGen);
	_right.translate(classGen, methodGen);
	_right.startResetIterator(classGen, methodGen);

	// Cast a result tree to a string to use an existing compare
	if (tright instanceof ResultTreeType) {
	    tright.translateTo(classGen, methodGen, Type.String);	
	    tright = Type.String;
	}

	// Call the appropriate compare() from the BasisLibrary
	il.append(new PUSH(cpg, _op));
	il.append(methodGen.loadContextNode());
	il.append(methodGen.loadDOM());

	final int compare = cpg.addMethodref(BASIS_LIBRARY_CLASS,
					     "compare",
					     "("
					     + tleft.toSignature() 
					     + tright.toSignature()
					     + "I"
					     + NODE_SIG
					     + DOM_INTF_SIG
					     + ")Z");
	il.append(new INVOKESTATIC(compare));
    }
}
