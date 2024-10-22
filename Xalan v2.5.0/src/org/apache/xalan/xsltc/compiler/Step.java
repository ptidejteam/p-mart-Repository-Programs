/*
 * @(#)$Id: Step.java,v 1.1 2006/03/01 21:12:07 vauchers Exp $
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.dom.Axis;
import org.apache.xml.dtm.DTM;

final class Step extends RelativeLocationPath {

    /**
     * This step's axis as defined in class Axis.
     */
    private int _axis;

    /**
     * A vector of predicates (filters) defined on this step - may be null
     */
    private Vector _predicates;

    /**
     * Some simple predicates can be handled by this class (and not by the
     * Predicate class) and will be removed from the above vector as they are
     * handled. We use this boolean to remember if we did have any predicates.
     */
    private boolean _hadPredicates = false;

    /**
     * Type of the node test.
     */
    private int _nodeType;

    public Step(int axis, int nodeType, Vector predicates) {
	_axis = axis;
	_nodeType = nodeType;
	_predicates = predicates;
    }

    /**
     * Set the parser for this element and all child predicates
     */
    public void setParser(Parser parser) {
	super.setParser(parser);
	if (_predicates != null) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Predicate exp = (Predicate)_predicates.elementAt(i);
		exp.setParser(parser);
		exp.setParent(this);
	    }
	}
    }
    
    /**
     * Define the axis (defined in Axis class) for this step
     */
    public int getAxis() {
	return _axis;
    }
	
    /**
     * Get the axis (defined in Axis class) for this step
     */
    public void setAxis(int axis) {
	_axis = axis;
    }

    /**
     * Returns the node-type for this step
     */
    public int getNodeType() {
	return _nodeType;
    }

    /**
     * Returns the vector containing all predicates for this step.
     */
    public Vector getPredicates() {
	return _predicates;
    }

    /**
     * Returns the vector containing all predicates for this step.
     */
    public void addPredicates(Vector predicates) {
	if (_predicates == null) {
	    _predicates = predicates;
	}
	else {
	    _predicates.addAll(predicates);
	}
    }

    /**
     * Returns 'true' if this step has a parent pattern.
     * This method will return 'false' if this step occurs on its own under
     * an element like <xsl:for-each> or <xsl:apply-templates>.
     */
    private boolean hasParentPattern() {
	final SyntaxTreeNode parent = getParent();
	return (parent instanceof ParentPattern ||
		parent instanceof ParentLocationPath ||
		parent instanceof UnionPathExpr ||
		parent instanceof FilterParentPath);
    }
    
    /**
     * Returns 'true' if this step has any predicates
     */
    private boolean hasPredicates() {
	return _predicates != null && _predicates.size() > 0;
    }

    /**
     * Returns 'true' if this step is used within a predicate
     */
    private boolean isPredicate() {
	SyntaxTreeNode parent = this;
	while (parent != null) {
	    parent = parent.getParent();
	    if (parent instanceof Predicate) return true;
	}
	return false;
    }

    /**
     * True if this step is the abbreviated step '.'
     */
    public boolean isAbbreviatedDot() {
	return _nodeType == NodeTest.ANODE && _axis == Axis.SELF;
    }


    /**
     * True if this step is the abbreviated step '..'
     */
    public boolean isAbbreviatedDDot() {
	return _nodeType == NodeTest.ANODE && _axis == Axis.PARENT;
    }

    /**
     * Type check this step. The abbreviated steps '.' and '@attr' are
     * assigned type node if they have no predicates. All other steps 
     * have type node-set.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {

	// Save this value for later - important for testing for special
	// combinations of steps and patterns than can be optimised
	_hadPredicates = hasPredicates();

	// Special case for '.'
 	//   in the case where '.' has a context such as book/. 
	//   or .[false()] we can not optimize the nodeset to a single node. 
	if (isAbbreviatedDot()) {
	    _type =  (hasParentPattern() || hasPredicates() ) ? 
		Type.NodeSet : Type.Node;
	}
	else {
	    _type = Type.NodeSet;
	}

	// Type check all predicates (expressions applied to the step)
	if (_predicates != null) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Expression pred = (Expression)_predicates.elementAt(i);
		pred.typeCheck(stable);
	    }
	}

	// Return either Type.Node or Type.NodeSet
	return _type;
    }

    /**
     * Translate a step by pushing the appropriate iterator onto the stack.
     * The abbreviated steps '.' and '@attr' do not create new iterators
     * if they are not part of a LocationPath and have no filters.
     * In these cases a node index instead of an iterator is pushed
     * onto the stack.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	if (hasPredicates()) {
	    translatePredicates(classGen, methodGen);
	}
	else {
	    // If it is an attribute but not '@*', '@attr' or '@node()' and
	    // has no parent
	    if (_axis == Axis.ATTRIBUTE && _nodeType != NodeTest.ATTRIBUTE &&
		_nodeType != NodeTest.ANODE && !hasParentPattern()) 
	    {
		int iter = cpg.addInterfaceMethodref(DOM_INTF,
						     "getTypedAxisIterator",
						     "(II)"+NODE_ITERATOR_SIG);
		il.append(methodGen.loadDOM());
		il.append(new PUSH(cpg, Axis.ATTRIBUTE));
		il.append(new PUSH(cpg, _nodeType));
		il.append(new INVOKEINTERFACE(iter, 3));
		return;
	    }

	    // Special case for '.'
	    if (isAbbreviatedDot()) {
		if (_type == Type.Node) {
		    // Put context node on stack if using Type.Node
		    il.append(methodGen.loadContextNode());
		}
		else {
		    // Wrap the context node in a singleton iterator if not.
		    int init = cpg.addMethodref(SINGLETON_ITERATOR,
						"<init>", "("+NODE_SIG+")V");
		    il.append(new NEW(cpg.addClass(SINGLETON_ITERATOR)));
		    il.append(DUP);
		    il.append(methodGen.loadContextNode());
		    il.append(new INVOKESPECIAL(init));
		}
		return;
	    }

	    // Special case for /foo/*/bar
	    SyntaxTreeNode parent = getParent();
	    if ((parent instanceof ParentLocationPath) &&
		(parent.getParent() instanceof ParentLocationPath)) {
		if ((_nodeType == NodeTest.ELEMENT) && (!_hadPredicates)) {
		    _nodeType = NodeTest.ANODE;
		}
	    }

	    // "ELEMENT" or "*" or "@*" or ".." or "@attr" with a parent.
	    switch (_nodeType) {
	    case NodeTest.ATTRIBUTE:
		_axis = Axis.ATTRIBUTE;
	    case NodeTest.ANODE:
		// DOM.getAxisIterator(int axis);
		int git = cpg.addInterfaceMethodref(DOM_INTF,
						    "getAxisIterator",
						    "(I)"+NODE_ITERATOR_SIG);
		il.append(methodGen.loadDOM());
		il.append(new PUSH(cpg, _axis));
		il.append(new INVOKEINTERFACE(git, 2));
		break;
	    default:
		final XSLTC xsltc = getParser().getXSLTC();
		final Vector ni = xsltc.getNamesIndex();
		String name = null;
		int star = 0;
		
		if (_nodeType >= DTM.NTYPES) {
		    name = (String)ni.elementAt(_nodeType-DTM.NTYPES);
		    star = name.lastIndexOf('*');
		}
		
		if (star > 1) {
		    final String namespace;
		    if (_axis == Axis.ATTRIBUTE)
			namespace = name.substring(0,star-2);
		    else
			namespace = name.substring(0,star-1);

		    final int nsType = xsltc.registerNamespace(namespace);
		    final int ns = cpg.addInterfaceMethodref(DOM_INTF,
						    "getNamespaceAxisIterator",
						    "(II)"+NODE_ITERATOR_SIG);
		    il.append(methodGen.loadDOM());
		    il.append(new PUSH(cpg, _axis));
		    il.append(new PUSH(cpg, nsType));
		    il.append(new INVOKEINTERFACE(ns, 3));
		    break;
		}
	    case NodeTest.ELEMENT:
		// DOM.getTypedAxisIterator(int axis, int type);
		final int ty = cpg.addInterfaceMethodref(DOM_INTF,
						"getTypedAxisIterator",
						"(II)"+NODE_ITERATOR_SIG);
		// Get the typed iterator we're after
		il.append(methodGen.loadDOM());
		il.append(new PUSH(cpg, _axis));
		il.append(new PUSH(cpg, _nodeType));
		il.append(new INVOKEINTERFACE(ty, 3));

		break;
	    }
	}
    }


    /**
     * Translate a sequence of predicates. Each predicate is translated
     * by constructing an instance of <code>CurrentNodeListIterator</code>
     * which is initialized from another iterator (recursive call),
     * a filter and a closure (call to translate on the predicate) and "this". 
     */
    public void translatePredicates(ClassGenerator classGen,
				    MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	int idx = 0;

	if (_predicates.size() == 0) {
	    translate(classGen, methodGen);
	}
	else {
	    final Predicate predicate = (Predicate)_predicates.lastElement();
	    _predicates.remove(predicate);

	    // Special case for predicates that can use the NodeValueIterator
	    // instead of an auxiliary class. Certain path/predicates pairs
	    // are translated into a base path, on top of which we place a
	    // node value iterator that tests for the desired value:
	    //   foo[@attr = 'str']  ->  foo/@attr + test(value='str')
	    //   foo[bar = 'str']    ->  foo/bar + test(value='str')
	    //   foo/bar[. = 'str']  ->  foo/bar + test(value='str')
	    if (predicate.isNodeValueTest()) {
		Step step = predicate.getStep();

		il.append(methodGen.loadDOM());
		// If the predicate's Step is simply '.' we translate this Step
		// and place the node test on top of the resulting iterator
		if (step.isAbbreviatedDot()) {
		    translate(classGen, methodGen);
		    il.append(new ICONST(DOM.RETURN_CURRENT));
		}
		// Otherwise we create a parent location path with this Step and
		// the predicates Step, and place the node test on top of that
		else {
		    ParentLocationPath path = new ParentLocationPath(this,step);
		    try {
			path.typeCheck(getParser().getSymbolTable());
		    }
		    catch (TypeCheckError e) { }
		    path.translate(classGen, methodGen);
		    il.append(new ICONST(DOM.RETURN_PARENT));
		}
		predicate.translate(classGen, methodGen);
		idx = cpg.addInterfaceMethodref(DOM_INTF,
						GET_NODE_VALUE_ITERATOR,
						GET_NODE_VALUE_ITERATOR_SIG);
		il.append(new INVOKEINTERFACE(idx, 5));
	    }
	    // Handle '//*[n]' expression
	    else if (predicate.isNthDescendant()) {
		il.append(methodGen.loadDOM());
		il.append(new ICONST(NodeTest.ELEMENT));
		predicate.translate(classGen, methodGen);
		il.append(new ICONST(0));
		idx = cpg.addInterfaceMethodref(DOM_INTF,
						"getNthDescendant",
						"(IIZ)"+NODE_ITERATOR_SIG);
		il.append(new INVOKEINTERFACE(idx, 4));
	    }
	    // Handle 'elem[n]' expression
	    else if (predicate.isNthPositionFilter()) {
		idx = cpg.addMethodref(NTH_ITERATOR_CLASS,
				       "<init>",
				       "("+NODE_ITERATOR_SIG+"I)V");
		il.append(new NEW(cpg.addClass(NTH_ITERATOR_CLASS)));
		il.append(DUP);
		translatePredicates(classGen, methodGen); // recursive call
		predicate.translate(classGen, methodGen);
		il.append(new INVOKESPECIAL(idx));
	    }
	    else {
		idx = cpg.addMethodref(CURRENT_NODE_LIST_ITERATOR,
				       "<init>",
				       "("
				       + NODE_ITERATOR_SIG
				       + CURRENT_NODE_LIST_FILTER_SIG
				       + NODE_SIG
				       + TRANSLET_SIG
				       + ")V");
		// create new CurrentNodeListIterator
		il.append(new NEW(cpg.addClass(CURRENT_NODE_LIST_ITERATOR)));
		il.append(DUP);
		translatePredicates(classGen, methodGen); // recursive call
		predicate.translateFilter(classGen, methodGen);
		
		il.append(methodGen.loadCurrentNode());
		il.append(classGen.loadTranslet());
		if (classGen.isExternal()) {
		    final String className = classGen.getClassName();
		    il.append(new CHECKCAST(cpg.addClass(className)));
		}
		il.append(new INVOKESPECIAL(idx));
	    }
	}
    }

    /**
     * Returns a string representation of this step.
     */
    public String toString() {
	final StringBuffer buffer = new StringBuffer("step(\"");
	buffer.append(Axis.names[_axis]).append("\", ").append(_nodeType);
	if (_predicates != null) {
	    final int n = _predicates.size();
	    for (int i = 0; i < n; i++) {
		final Predicate pred = (Predicate)_predicates.elementAt(i);
		buffer.append(", ").append(pred.toString());
	    }
	}
	return buffer.append(')').toString();
    }
}
