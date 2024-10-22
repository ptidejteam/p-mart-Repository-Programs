/*
 * @(#)$Id: XslAttribute.java,v 1.1 2006/03/01 20:51:33 vauchers Exp $
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

import org.w3c.dom.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import de.fub.bytecode.generic.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class XslAttribute extends Instruction {
    private AttributeValue _name; // name treated as AVT (7.1.3)
    private AttributeValueTemplate _namespace = null;
    private String _prefix;
    private boolean _ignore = false;

    /**
     *
     */
    public AttributeValue getName() {
	return _name;
    }

    /**
     *
     */
    public void display(int indent) {
	indent(indent);
	Util.println("Attribute " + _name);
	displayContents(indent + IndentIncrement);
    }
		
    /**
     * Parses the attribute's contents. Special care taken for namespaces.
     */
    public void parseContents(Element element, Parser parser) {

	final SymbolTable stable = parser.getSymbolTable();
	String namespace = element.getAttribute("namespace");
	String name = element.getAttribute("name");
	QName qname = parser.getQName(name);
	final String prefix = qname.getPrefix();
	boolean generated = false;

	if ((prefix != null) && (prefix.equals("xmlns"))) {
	    reportError(element, parser, ErrorMsg.ILL_ATTR_ERR, name);
	    return;
	}

	// Ignore attribute if preceeded by some other type of element
	final SyntaxTreeNode parent = getParent();
	final Vector siblings = parent.getContents();
	for (int i = 0; i < parent.elementCount(); i++) {
	    SyntaxTreeNode item = (SyntaxTreeNode)siblings.elementAt(i);
	    if (item == this) break;
	    if (!(item instanceof XslAttribute) &&
		!(item instanceof UseAttributeSets) &&
		!(item instanceof LiteralAttribute)) {
		_ignore = true;
		reportWarning(element, parser, ErrorMsg.ATTROUTS_ERR, name);
		return;
	    }
	}

	// Get namespace from namespace attribute?
	if (namespace != "") {
	    // Prefix could be in symbol table
	    _prefix = stable.lookupPrefix(namespace);
	    _namespace = new AttributeValueTemplate(namespace, parser);
	}
	// Get namespace from prefix in name attribute?
	else if (prefix != "") {
	    _prefix = prefix;
	    namespace = stable.lookupNamespace(prefix);
	    if (namespace != null)
		_namespace = new AttributeValueTemplate(namespace, parser);
	}
	
	// Common handling for namespaces:
	if (_namespace != null) {

	    // Generate prefix if we have none
	    if (_prefix == null) {
		if (prefix != null) {
		    _prefix = prefix;
		}
		else {
		    _prefix = stable.generateNamespacePrefix();
		    generated = true;
		}
	    }

	    if (_prefix.equals("")) {
		name = qname.getLocalPart();
	    }
	    else {
		name = _prefix+":"+qname.getLocalPart();
		// PROBLEM:
		// The namespace URI must be passed to the parent element,
		// but we don't yet know what the actual URI is (as we only
		// know it as an attribute value template). New design needed.
		if ((parent instanceof LiteralElement) && (!generated)) {
		    ((LiteralElement)parent).registerNamespace(_prefix,
							       namespace,
							       stable,false);
		}
	    }
	}


	_name = AttributeValue.create(this, name, parser);
	parseChildren(element, parser);
    }
	
    /**
     *
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_ignore) return(Type.Void);
	_name.typeCheck(stable);
	if (_namespace != null)
	    _namespace.typeCheck(stable);
	typeCheckContents(stable);
	return Type.Void;
    }

    /**
     *
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (_ignore) return;

	// Compile code that emits any needed namespace declaration
	if (_namespace != null) {
	    // public void attribute(final String name, final String value)
	    il.append(methodGen.loadHandler());
	    il.append(new PUSH(cpg,_prefix));
	    _namespace.translate(classGen,methodGen);
	    il.append(methodGen.namespace());
	}

	// Save the current handler base on the stack
	il.append(methodGen.loadHandler());
	il.append(DUP);		// first arg to "attributes" call
	
	// push attribute name
	_name.translate(classGen, methodGen);// 2nd arg

	il.append(classGen.loadTranslet());
	il.append(new GETFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       "stringValueHandler",
					       STRING_VALUE_HANDLER_SIG)));
	il.append(DUP);
	il.append(methodGen.storeHandler());

	// translate contents with substituted handler
	translateContents(classGen, methodGen);

	// get String out of the handler
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(STRING_VALUE_HANDLER,
						     "getValue",
						     "()" + STRING_SIG)));
	// call "attribute"
	il.append(methodGen.attribute());
	// Restore old handler base from stack
	il.append(methodGen.storeHandler());
    }
}
