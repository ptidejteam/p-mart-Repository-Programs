/*
 * @(#)$Id: TestGenerator.java,v 1.1 2006/03/01 21:08:42 vauchers Exp $
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

package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.*;
import org.apache.xalan.xsltc.compiler.Template;

public final class TestGenerator extends MethodGenerator {
    private static int CONTEXT_NODE_INDEX = 1;
    private static int CURRENT_NODE_INDEX = 4;
    private static int ITERATOR_INDEX = 6;

    private Instruction _aloadDom;
    private final Instruction _iloadCurrent;
    private final Instruction _iloadContext;
    private final Instruction _istoreCurrent;
    private final Instruction _istoreContext;
    private final Instruction _astoreIterator;
    private final Instruction _aloadIterator;

    public TestGenerator(int access_flags, Type return_type,
			 Type[] arg_types, String[] arg_names,
			 String method_name, String class_name,
			 InstructionList il, ConstantPoolGen cp) {
	super(access_flags, return_type, arg_types, arg_names, method_name, 
	      class_name, il, cp);
	
	_iloadCurrent  = new ILOAD(CURRENT_NODE_INDEX);
	_istoreCurrent = new ISTORE(CURRENT_NODE_INDEX);
	_iloadContext  = new ILOAD(CONTEXT_NODE_INDEX);
	_istoreContext  = new ILOAD(CONTEXT_NODE_INDEX);
	_astoreIterator = new ASTORE(ITERATOR_INDEX);
	_aloadIterator  = new ALOAD(ITERATOR_INDEX);
    }

    public int getHandlerIndex() {
	return INVALID_INDEX;		// not available
    }

    public int getIteratorIndex() {
	return ITERATOR_INDEX;		// not available
    }

    public void setDomIndex(int domIndex) {
	_aloadDom = new ALOAD(domIndex);
    }

    public Instruction loadDOM() {
	return _aloadDom;
    }

    public Instruction loadCurrentNode() {
	return _iloadCurrent;
    }

    /** by default context node is the same as current node. MK437 */
    public Instruction loadContextNode() {
	return _iloadContext;
    }

    public Instruction storeContextNode() {
	return _istoreContext;
    }

    public Instruction storeCurrentNode() {
	return _istoreCurrent;
    }

    public Instruction storeIterator() {
	return _astoreIterator;
    }
    
    public Instruction loadIterator() {
	return _aloadIterator;
    }

    public int getLocalIndex(String name) {
	if (name.equals("current")) {
	    return CURRENT_NODE_INDEX;
	}
	else {
	    return super.getLocalIndex(name);
	}
    }
}
