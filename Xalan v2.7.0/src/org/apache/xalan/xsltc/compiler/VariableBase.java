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
 * $Id: VariableBase.java,v 1.1 2006/03/01 21:14:42 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler;

import java.util.Vector;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NodeSetType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.utils.XML11Char;

/**
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Morten Jorgensen
 * @author Erwin Bolwidt <ejb@klomp.org>
 * @author John Howard <JohnH@schemasoft.com>
 */
class VariableBase extends TopLevelElement {

    protected QName       _name;            // The name of the variable.
    protected String      _escapedName;        // The escaped qname of the variable.
    protected Type        _type;            // The type of this variable.
    protected boolean     _isLocal;         // True if the variable is local.
    protected LocalVariableGen _local;      // Reference to JVM variable
    protected Instruction _loadInstruction; // Instruction to load JVM variable
    protected Instruction _storeInstruction; // Instruction to load JVM variable
    protected Expression  _select;          // Reference to variable expression
    protected String      select;           // Textual repr. of variable expr.

    // References to this variable (when local)
    protected Vector      _refs = new Vector(2); 

    // Dependencies to other variables/parameters (for globals only)
    protected Vector      _dependencies = null;

    // Used to make sure parameter field is not added twice
    protected boolean    _ignore = false;

    // Used to order top-level variables so that there are no forward references
    protected int        _weight = 0;

    /**
     * Disable this variable/parameter
     */
    public void disable() {
	_ignore = true;
    }

    /**
     * Add a reference to this variable. Called by VariableRef when an
     * expression contains a reference to this variable.
     */
    public void addReference(VariableRefBase vref) {
	_refs.addElement(vref);
    }

    /**
     * Remove a reference to this variable. Called by VariableRef when this
     * variable goes out of scope.
     */
    public void removeReference(VariableRefBase vref) {
	_refs.remove(vref);
    }

    /**
     *
     */
    public void addDependency(VariableBase other) {
	if (_dependencies == null) {
	    _dependencies = new Vector();
	}
	if (!_dependencies.contains(other)) {
	    _dependencies.addElement(other);
	}
    }

    /**
     *
     */
    public Vector getDependencies() {
	return _dependencies;
    }

    /**
     * Map this variable to a register
     */
    public void mapRegister(MethodGenerator methodGen) {
        if (_local == null) {
            final InstructionList il = methodGen.getInstructionList();
	    final String name = getEscapedName(); // TODO: namespace ?
	    final org.apache.bcel.generic.Type varType = _type.toJCType();
            _local = methodGen.addLocalVariable2(name, varType, il.getEnd());
        }
    }

    /**
     * Remove the mapping of this variable to a register.
     * Called when we leave the AST scope of the variable's declaration
     */
    public void unmapRegister(MethodGenerator methodGen) {
	if (_refs.isEmpty() && (_local != null)) {
	    _local.setEnd(methodGen.getInstructionList().getEnd());
	    methodGen.removeLocalVariable(_local);
	    _refs = null;
	    _local = null;
	}
    }

    /**
     * Returns an instruction for loading the value of this variable onto 
     * the JVM stack.
     */
    public Instruction loadInstruction() {
	final Instruction instr = _loadInstruction;
	if (_loadInstruction == null) {
	    _loadInstruction = _type.LOAD(_local.getIndex());
        }
	return _loadInstruction;
    }

    /**
     * Returns an instruction for storing a value from the JVM stack
     * into this variable.
     */
    public Instruction storeInstruction() {
	final Instruction instr = _storeInstruction;
	if (_storeInstruction == null) {
	    _storeInstruction = _type.STORE(_local.getIndex());
        }
	return _storeInstruction;
    }

    /**
     * Returns the expression from this variable's select attribute (if any)
     */
    public Expression getExpression() {
	return(_select);
    }

    /**
     * Display variable as single string
     */
    public String toString() {
	return("variable("+_name+")");
    }

    /**
     * Display variable in a full AST dump
     */
    public void display(int indent) {
	indent(indent);
	System.out.println("Variable " + _name);
	if (_select != null) { 
	    indent(indent + IndentIncrement);
	    System.out.println("select " + _select.toString());
	}
	displayContents(indent + IndentIncrement);
    }

    /**
     * Returns the type of the variable
     */
    public Type getType() {
	return _type;
    }

    /**
     * Returns the name of the variable or parameter as it will occur in the
     * compiled translet.
     */
    public QName getName() {
	return _name;
    }

    /**
     * Returns the escaped qname of the variable or parameter 
     */
    public String getEscapedName() {
	return _escapedName;
    }

    /**
     * Set the name of the variable or paremeter. Escape all special chars.
     */
    public void setName(QName name) {
	_name = name;
	_escapedName = Util.escape(name.getStringRep());
    }

    /**
     * Returns the true if the variable is local
     */
    public boolean isLocal() {
	return _isLocal;
    }

    /**
     * Parse the contents of the <xsl:decimal-format> element.
     */
    public void parseContents(Parser parser) {
	// Get the 'name attribute
	String name = getAttribute("name");

        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                ErrorMsg err = new ErrorMsg(ErrorMsg.INVALID_QNAME_ERR, name, this);
                parser.reportError(Constants.ERROR, err);           
            }   
	    setName(parser.getQNameIgnoreDefaultNs(name));
        }
        else
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "name");

	// Check whether variable/param of the same name is already in scope
	VariableBase other = parser.lookupVariable(_name);
	if ((other != null) && (other.getParent() == getParent())) {
	    reportError(this, parser, ErrorMsg.VARIABLE_REDEF_ERR, name);
	}
	
	select = getAttribute("select");
	if (select.length() > 0) {
	    _select = getParser().parseExpression(this, "select", null);
	    if (_select.isDummy()) {
		reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR, "select");
		return;
	    }
	}

	// Children must be parsed first -> static scoping
	parseChildren(parser);
    }

    /**
     * Compile the value of the variable, which is either in an expression in
     * a 'select' attribute, or in the variable elements body
     */
    public void translateValue(ClassGenerator classGen,
			       MethodGenerator methodGen) {
	// Compile expression is 'select' attribute if present
	if (_select != null) {
	    _select.translate(classGen, methodGen);
	    // Create a CachedNodeListIterator for select expressions
	    // in a variable or parameter.
	    if (_select.getType() instanceof NodeSetType) {
	        final ConstantPoolGen cpg = classGen.getConstantPool();
	        final InstructionList il = methodGen.getInstructionList();
	    	
	        final int initCNI = cpg.addMethodref(CACHED_NODE_LIST_ITERATOR_CLASS,
					    "<init>",
					    "("
					    +NODE_ITERATOR_SIG
					    +")V");
	        il.append(new NEW(cpg.addClass(CACHED_NODE_LIST_ITERATOR_CLASS)));
	        il.append(DUP_X1);
	        il.append(SWAP);

	        il.append(new INVOKESPECIAL(initCNI));
	    }
	    _select.startIterator(classGen, methodGen);
	}
	// If not, compile result tree from parameter body if present.
	else if (hasContents()) {
	    compileResultTree(classGen, methodGen);
	}
	// If neither are present then store empty string in variable
	else {
	    final ConstantPoolGen cpg = classGen.getConstantPool();
	    final InstructionList il = methodGen.getInstructionList();
	    il.append(new PUSH(cpg, Constants.EMPTYSTRING));
	}
    }

}
