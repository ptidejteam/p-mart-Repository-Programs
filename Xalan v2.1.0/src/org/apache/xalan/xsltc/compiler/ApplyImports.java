/*
 * @(#)$Id: ApplyImports.java,v 1.1 2006/03/01 20:51:33 vauchers Exp $
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
 *
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;
import java.util.Enumeration;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import de.fub.bytecode.generic.*;

import org.apache.xalan.xsltc.compiler.util.*;

final class ApplyImports extends Instruction {

    private Expression _select;
    private QName      _modeName;
    private String     _functionName;
	
    public void display(int indent) {
	indent(indent);
	Util.println("ApplyTemplates");
	indent(indent + IndentIncrement);
	Util.println("select " + _select.toString());
	if (_modeName != null) {
	    indent(indent + IndentIncrement);
	    Util.println("mode " + _modeName);
	}
    }

    public boolean hasWithParams() {
	return hasContents();
    }

    public void parseContents(Element element, Parser parser) {
	final String select = element.getAttribute("select");
	final String mode   = element.getAttribute("mode");
	
	if (select.length() > 0) {
	    _select = parser.parseExpression(this, element, "select");
	}
	
	if (mode.length() > 0) {
	    _modeName = parser.getQName(mode);
	}
	
	// instantiate Mode if needed, cache (apply temp) function name
	_functionName =
	    parser.getTopLevelStylesheet().getMode(_modeName).functionName();
	parseChildren(element, parser);	// with-params
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_select != null) {
	    Type tselect = _select.typeCheck(stable);
	    if (tselect instanceof NodeType ||
		tselect instanceof ReferenceType) {
		_select = new CastExpr(_select, Type.NodeSet);
		tselect = Type.NodeSet;
	    }
	    if (tselect instanceof NodeSetType) {
		typeCheckContents(stable);		// with-params
		return Type.Void;
	    } 
	    throw new TypeCheckError(this);
	}
	else {
	    typeCheckContents(stable);		// with-params
	    return Type.Void;
	}
    }

    /**
     * Translate call-template. A parameter frame is pushed only if
     * some template in the stylesheet uses parameters. 
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Stylesheet stylesheet = classGen.getStylesheet();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	final int current = methodGen.getLocalIndex("current");

	// check if sorting nodes is required
	final Vector sortObjects = new Vector();
	final Enumeration children = elements();
	while (children.hasMoreElements()) {
	    final Object child = children.nextElement();
	    if (child instanceof Sort) {
		sortObjects.addElement(child);
	    }
	}
	
	// Push a new parameter frame
	if (stylesheet.hasLocalParams()) {
	    il.append(classGen.loadTranslet());
	    final int pushFrame = cpg.addMethodref(TRANSLET_CLASS,
						   PUSH_PARAM_FRAME,
						   PUSH_PARAM_FRAME_SIG);
	    il.append(new INVOKEVIRTUAL(pushFrame));
	    // translate with-params
	    translateContents(classGen, methodGen);
	}

	// push arguments for final call to applyTemplates
	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
		
	// compute node iterator for applyTemplates
	if (sortObjects.size() > 0) {
	    Sort.translateSortIterator(classGen, methodGen,
				       _select, sortObjects);
	}
	else {
	    if (_select == null) {
		Mode.compileGetChildren(classGen, methodGen, current);
	    }
	    else {
		_select.translate(classGen, methodGen);
	    }
	}
	if (_select != null) {
	    _select.startResetIterator(classGen, methodGen);
	}
	
	//!!! need to instantiate all needed modes
	final String className = classGen.getStylesheet().getClassName();
	il.append(methodGen.loadHandler());
	final String applyTemplatesSig = classGen.getApplyTemplatesSig();
	final int applyTemplates = cpg.addMethodref(className,
						    _functionName,
						    applyTemplatesSig);
	il.append(new INVOKEVIRTUAL(applyTemplates));
	
	// Pop parameter frame
	if (stylesheet.hasLocalParams()) {
	    il.append(classGen.loadTranslet());
	    final int popFrame = cpg.addMethodref(TRANSLET_CLASS,
						  POP_PARAM_FRAME,
						  POP_PARAM_FRAME_SIG);
	    il.append(new INVOKEVIRTUAL(popFrame));
	}
    }


}
