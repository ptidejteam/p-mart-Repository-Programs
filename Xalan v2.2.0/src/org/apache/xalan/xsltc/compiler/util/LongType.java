/*
 * @(#)$Id: LongType.java,v 1.1 2006/03/01 20:54:09 vauchers Exp $
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
 * @author G. Todd Miller 
 *
 */

package org.apache.xalan.xsltc.compiler.util;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.Constants;

public final class LongType extends NumberType {
    protected LongType() {}

    public String toString() {
	return "long";
    }

    public boolean identicalTo(Type other) {
	return this == other;
    }

    public String toSignature() {
	return "J";
    }

    public de.fub.bytecode.generic.Type toJCType() {
	return de.fub.bytecode.generic.Type.LONG;
    }

    /**
     * @see	org.apache.xalan.xsltc.compiler.util.Type#distanceTo
     */
    public int distanceTo(Type type) {
	if (type == this) {
	    return 0;
	}
	else if (type == Type.Int) {
	    return 1;
	}
	else
	    return Integer.MAX_VALUE;
    }
    
    /**
     * Translates an long into an object of internal type <code>type</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    final Type type) {
	if (type == Type.Real) {
	    translateTo(classGen, methodGen, (RealType) type);
	}
	else if (type == Type.Int) {
	    translateTo(classGen, methodGen, (IntType) type);
	}
	else if (type == Type.String) {
	    translateTo(classGen, methodGen, (StringType) type);
	}
	else if (type == Type.Boolean) {
	    translateTo(classGen, methodGen, (BooleanType) type);
	}
	else if (type == Type.Reference) {
	    translateTo(classGen, methodGen, (ReferenceType) type);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), type.toString());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }


    /**
     * Expects an long on the stack and pushes a real.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    RealType type) {
	methodGen.getInstructionList().append(L2D);
    }

    /**
     * Expects an long on the stack and pushes its string value by calling
     * <code>Long.toString(int i)</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    StringType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new INVOKESTATIC(cpg.addMethodref(LONG_CLASS,
						    "toString",
						    "(J)" + STRING_SIG)));
    }

    /**
     * Expects an long on the stack and pushes a 0 if its value is 0 and
     * a 1 otherwise.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    BooleanType type) {
	final InstructionList il = methodGen.getInstructionList();
	final BranchHandle falsec = il.append(new IFEQ(null));
	il.append(LCONST_1);
	final BranchHandle truec = il.append(new GOTO(null));
	falsec.setTarget(il.append(LCONST_0));
	truec.setTarget(il.append(NOP));
    }

    /**
     * Expects an long on the stack and translates it to a non-synthesized
     * boolean. It does not push a 0 or a 1 but instead returns branchhandle 
     * list to be appended to the false list.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateToDesynthesized
     */
    public FlowList translateToDesynthesized(ClassGenerator classGen, 
					     MethodGenerator methodGen, 
					     BooleanType type) {
	final InstructionList il = methodGen.getInstructionList();
	return new FlowList(il.append(new IFEQ(null)));
    }

    /**
     * Expects an long on the stack and pushes a boxed integer.
     * Boxed integers are represented by an instance of
     * <code>java.lang.Integer</code>.
     *
     * @see	org.apache.xalan.xsltc.compiler.util.Type#translateTo
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    ReferenceType type) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new NEW(cpg.addClass(LONG_CLASS)));
	il.append(DUP_X1);
	il.append(SWAP);
	il.append(new INVOKESPECIAL(cpg.addMethodref(LONG_CLASS,
						     "<init>", "(J)V")));
    }

    /**
     * Translates an long into the Java type denoted by <code>clazz</code>. 
     * Expects an long on the stack and pushes a number of the appropriate
     * type after coercion.
     */
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, 
			    Class clazz) {
	final InstructionList il = methodGen.getInstructionList();
	if (clazz == Character.TYPE) {
	    il.append(L2I);
	    il.append(I2C);
	}
	else if (clazz == Byte.TYPE) {
	    il.append(L2I);
	    il.append(I2B);
	}
	else if (clazz == Short.TYPE) {
	    il.append(L2I);
	    il.append(I2S);
	}
	else if (clazz == Integer.TYPE) {
	    il.append(L2I);
	}
	else if (clazz == Long.TYPE) {
	    il.append(NOP);
	}
	else if (clazz == Float.TYPE) {
	    il.append(L2F);
	}
	else if (clazz == Double.TYPE) {
	    il.append(L2D);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.DATA_CONVERSION_ERR,
					toString(), clazz.getName());
	    classGen.getParser().reportError(Constants.FATAL, err);
	}
    }

    /**
     * Translates an object of this type to its boxed representation.
     */ 
    public void translateBox(ClassGenerator classGen,
			     MethodGenerator methodGen) {
	translateTo(classGen, methodGen, Type.Reference);
    }

    /**
     * Translates an object of this type to its unboxed representation.
     */ 
    public void translateUnBox(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	il.append(new CHECKCAST(cpg.addClass(LONG_CLASS)));
	final int index = cpg.addMethodref(LONG_CLASS,
					   LONG_VALUE, 
					   LONG_VALUE_SIG);
	il.append(new INVOKEVIRTUAL(index));
    }

    public Instruction ADD() {
	return InstructionConstants.LADD;
    }

    public Instruction SUB() {
	return InstructionConstants.LSUB;
    }

    public Instruction MUL() {
	return InstructionConstants.LMUL;
    }

    public Instruction DIV() {
	return InstructionConstants.LDIV;
    }

    public Instruction REM() {
	return InstructionConstants.LREM;
    }

    public Instruction NEG() {
	return InstructionConstants.LNEG;
    }

    public Instruction LOAD(int slot) {
	return new LLOAD(slot);
    }
	
    public Instruction STORE(int slot) {
	return new LSTORE(slot);
    }

    public BranchInstruction GT(boolean tozero) {
	// GTM:TBD
	return tozero ? (BranchInstruction) new IFGT(null) : 
	    (BranchInstruction) new IF_ICMPGT(null);
    }

    public BranchInstruction GE(boolean tozero) {
	// GTM:TBD
	return tozero ? (BranchInstruction) new IFGE(null) : 
	    (BranchInstruction) new IF_ICMPGE(null);
    }

    public BranchInstruction LT(boolean tozero) {
	// GTM:TBD
	return tozero ? (BranchInstruction) new IFLT(null) : 
	    (BranchInstruction) new IF_ICMPLT(null);
    }

    public BranchInstruction LE(boolean tozero) {
	// GTM:TBD
	return tozero ? (BranchInstruction) new IFLE(null) : 
	    (BranchInstruction) new IF_ICMPLE(null);
    }
}
