/*
 * @(#)$Id: Sort.java,v 1.1 2006/03/01 20:51:33 vauchers Exp $
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
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.text.Collator;

import org.w3c.dom.*;

import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;

import de.fub.bytecode.classfile.JavaClass;
import de.fub.bytecode.classfile.Field;
import de.fub.bytecode.classfile.Method;
import de.fub.bytecode.generic.*;
import de.fub.bytecode.Constants;

import org.apache.xalan.xsltc.dom.*;
import org.apache.xalan.xsltc.compiler.util.*;

final class Sort extends Instruction {
    private Expression     _select;
    private AttributeValue _order;
    private AttributeValue _caseOrder;
    private AttributeValue _dataType;

    public  String         _lang;
    public  String         _country;

    /**
     * Parse the attributes of the xsl:sort element
     */
    public void parseContents(Element element, Parser parser) {
	// Parse the select expression (node string value if no expression)
	_select = parser.parseExpression(this, element, "select", "string(.)");

	// Get the sort order; default is 'ascending'
	String val = element.getAttribute("order");
	_order = AttributeValue
	    .create(this, val.length() > 0 ? val : "ascending", parser);

	// Get the case order; default is language dependant
	val = element.getAttribute("case-order");
	_caseOrder = AttributeValue
	    .create(this, val.length() > 0 ? val : "upper-first", parser);

	// Get the sort data type; default is text
	val = element.getAttribute("data-type");
	_dataType = AttributeValue
	    .create(this, val.length() > 0 ? val : "text", parser);

	// Get the language whose sort rules we will use; default is env.dep.
	if ((val = element.getAttribute("lang")) != null) {
	    try {
		StringTokenizer st = new StringTokenizer(val,"-",false);
		_lang = st.nextToken();
		_country = st.nextToken();
	    }
	    catch (NoSuchElementException e) { // ignore
	    }
	}
    }
    
    /**
     * Run type checks on the attributes; expression must return a string
     * which we will use as a sort key
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Type tselect = _select.typeCheck(stable);
	if (tselect instanceof StringType == false) {
	    _select = new CastExpr(_select, Type.String);
	}
	_order.typeCheck(stable);
	_caseOrder.typeCheck(stable);
	_dataType.typeCheck(stable);
	return Type.Void;
    }

    /**
     * These two methods are needed in the static methods that compile the
     * overloaded NodeSortRecord.compareType() and NodeSortRecord.sortOrder()
     */
    public String getSortType() {
	return _dataType.toString();
    }
    
    public String getSortOrder() {
	return _order.toString();
    }
    
    /**
     * This method compiles code for the select expression for this
     * xsl:sort element. The method is called from the static code-generating
     * methods in this class.
     */
    public void translateSelect(ClassGenerator classGen,
				MethodGenerator methodGen) {
	_select.translate(classGen,methodGen);
    }

    /**
     * This method should not produce any code
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	// empty
    }

    /**
     * Compiles code that instantiates a SortingIterator object.
     * This object's constructor needs referencdes to the current iterator
     * and a node sort record producing objects as its parameters.
     */
    public static void translateSortIterator(ClassGenerator classGen,
					     MethodGenerator methodGen,
					     Expression nodeSet,
					     Vector sortObjects) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();

	// SortingIterator.SortingIterator(NodeIterator,NodeSortRecordFactory);
	final int init = cpg.addMethodref(SORT_ITERATOR, "<init>",
					  "("
					  + NODE_ITERATOR_SIG
					  + NODE_SORT_FACTORY_SIG
					  + ")V");	

	final int setStartNode = cpg.addInterfaceMethodref(NODE_ITERATOR,
							   SET_START_NODE,
							   "(I)"+
							   NODE_ITERATOR_SIG);
	
	il.append(new NEW(cpg.addClass(SORT_ITERATOR)));
	il.append(DUP);

	// Get the current node iterator
	if (nodeSet == null) {	// apply-templates default
	    Mode.compileGetChildren(classGen, methodGen,
				    methodGen.getLocalIndex("current"));
	}
	else {
	    nodeSet.translate(classGen, methodGen);
	    il.append(new PUSH(cpg,methodGen.getLocalIndex("current")));
	    il.append(new INVOKEINTERFACE(setStartNode,2));
	}
	
	// Compile the code for the NodeSortRecord producing class and pass
	// that as the last argument to the SortingIterator constructor.
	compileSortRecordFactory(sortObjects, classGen, methodGen);
	il.append(new INVOKESPECIAL(init));
    }


    /**
     * Compiles code that instantiates a NodeSortRecordFactory object which
     * will produce NodeSortRecord objects of a specific type.
     */
    public static void compileSortRecordFactory(Vector sortObjects,
						ClassGenerator classGen,
						MethodGenerator methodGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();
	
	// NodeSortRecordFactory.NodeSortRecordFactory(dom,class,levels,trlet);
	final String initParams = "("
	    + DOM_INTF_SIG
	    + STRING_SIG
	    + "I"
	    + TRANSLET_INTF_SIG
	    + ")V";
	final int init = cpg.addMethodref(NODE_SORT_FACTORY,
					  "<init>", initParams);

	// Compile the object that will encapsulate each sort object (node).
	// NodeSortRecordFactory needs the name of the new class.
	String className = compileSortRecord(sortObjects, classGen, methodGen);

	// The constructor for the NodeSortRecord generating class takes no
	// parameters so we must to pass initialization params to other methods
	il.append(new NEW(cpg.addClass(NODE_SORT_FACTORY)));
	il.append(DUP);
	il.append(methodGen.loadDOM());
	il.append(new PUSH(cpg, className));
	il.append(new PUSH(cpg, sortObjects.size()));
	il.append(classGen.loadTranslet());
	il.append(new INVOKESPECIAL(init));
    }

    /**
     * Create a new auxillary class extending NodeSortRecord.
     */
    private static String compileSortRecord(Vector sortObjects,
					    ClassGenerator classGen,
					    MethodGenerator methodGen) {
	final XSLTC  xsltc = ((Sort)sortObjects.firstElement()).getXSLTC();
	final String className = xsltc.getHelperClassName();

	// This generates a new class for handling this specific sort
	final NodeSortRecordGenerator sortRecord =
	    new NodeSortRecordGenerator(className,
					NODE_SORT_RECORD,
					"sort$0.java",
					ACC_PUBLIC | ACC_SUPER | ACC_FINAL,
					new String[] {},
					classGen.getStylesheet());
	
	final ConstantPoolGen cpg = sortRecord.getConstantPool();	
	
	Method clinit = compileClassInit(sortObjects, sortRecord,
					 cpg, className);
	Method extract = compileExtract(sortObjects, sortRecord,
					cpg, className);
	Method sortType = compileSortType(sortObjects, sortRecord,
					  cpg, className);
	Method sortOrder = compileSortOrder(sortObjects, sortRecord,
					    cpg, className);

	sortRecord.addMethod(clinit);
	sortRecord.addEmptyConstructor(ACC_PUBLIC);
	sortRecord.addMethod(extract);
	sortRecord.addMethod(sortType);
	sortRecord.addMethod(sortOrder);

	// Overload NodeSortRecord.getCollator() only if needed
	for (int i = 0; i < sortObjects.size(); i++) {
	    if (((Sort)(sortObjects.elementAt(i)))._lang != null) {
		sortRecord.addMethod(compileGetCollator(sortObjects,
							sortRecord,
							cpg,
							className));
		i = sortObjects.size();
	    }
	}
	
	xsltc.dumpClass(sortRecord.getJavaClass());
	return className;
    }

    /**
     * Create a class constructor for the new class. All this constructor does
     * is to initialize a couple of tables that contain information on sort
     * order and sort type. These static tables cannot be in the parent class.
     */
    private static Method compileClassInit(Vector sortObjects,
					   NodeSortRecordGenerator sortRecord,
					   ConstantPoolGen cpg,
					   String className) {
	// Class initializer - void NodeSortRecord.<clinit>();
	final InstructionList il = new InstructionList();
	final CompareGenerator classInit =
	    new CompareGenerator(ACC_PUBLIC | ACC_FINAL,
				 de.fub.bytecode.generic.Type.VOID, 
				 new de.fub.bytecode.generic.Type[] { },
				 new String[] { },
				 "<clinit>", className, il, cpg);

	final int initLocale =  cpg.addMethodref("java/util/Locale",
						 "<init>",
						 "(Ljava/lang/String;"+
						 "Ljava/lang/String;)V");
	
	final int getCollator = cpg.addMethodref(COLLATOR_CLASS,
						 "getInstance",
						 "(Ljava/util/Locale;)"+
						 COLLATOR_SIG);

	final int setStrength = cpg.addMethodref(COLLATOR_CLASS,
						 "setStrength", "(I)V");

	// GTM: BCEL Changes:
	// GTM: Chged Field.ACC_PRIVATE --> Constants.ACC_PRIVATE
	//	Chged Field.ACC_STATIC  --> Constants.ACC_STATIC	
	sortRecord.addField(new Field(
				Constants.ACC_PRIVATE | Constants.ACC_STATIC,
				cpg.addUtf8("_compareType"),
				cpg.addUtf8("[I"),
				null, cpg.getConstantPool()));
	// GTM: BCEL Changes:
	// GTM: Chged Field.ACC_PRIVATE --> Constants.ACC_PRIVATE
	//	Chged Field.ACC_STATIC  --> Constants.ACC_STATIC	
	sortRecord.addField(new Field(
				Constants.ACC_PRIVATE | Constants.ACC_STATIC,
				cpg.addUtf8("_sortOrder"),
				cpg.addUtf8("[I"),
				null, cpg.getConstantPool()));

	final int levels = sortObjects.size();

	// Compile code that initializes the static _compareType array
	final int ctype = cpg.addFieldref(className, "_compareType", "[I");
	il.append(new PUSH(cpg,levels));
	il.append(new NEWARRAY(de.fub.bytecode.Constants.T_INT));
	for (int level = 0; level < levels; level++) {
	    final Sort sort = (Sort)sortObjects.elementAt(level);
	    il.append(DUP);
	    il.append(new PUSH(cpg, level));
	    il.append(new PUSH(cpg, sort.getSortType().equals("number")
			       ? NodeSortRecord.COMPARE_NUMERIC
			       : NodeSortRecord.COMPARE_STRING));
	    il.append(IASTORE);
	}
	il.append(new PUTSTATIC(ctype));
	
	// Compile code that initializes the static _sortOrder
	final int corder = cpg.addFieldref(className, "_sortOrder", "[I");
	il.append(new PUSH(cpg, levels));
	il.append(new NEWARRAY(de.fub.bytecode.Constants.T_INT));
	for (int level = 0; level < levels; level++) {
	    final Sort sort = (Sort)sortObjects.elementAt(level);
	    il.append(DUP);
	    il.append(new PUSH(cpg, level));
	    il.append(new PUSH(cpg, sort.getSortOrder().equals("descending")
			       ? NodeSortRecord.COMPARE_DESCENDING
			       : NodeSortRecord.COMPARE_ASCENDING));
	    il.append(IASTORE);
	}
	il.append(new PUTSTATIC(corder));

	// Compile code that initializes the locale
	String language = null;
	String country = null;
	Sort sort = (Sort)sortObjects.elementAt(0);

	for (int level = 0; level < levels; level++) {
	    if (language == null && sort._lang != null)
		language = sort._lang;
	    if (country == null && sort._country != null)
		country = sort._country;
	}

	// Get index to private static reference in NodeSortRecrd
	final int collator =
	    cpg.addFieldref(className, "_collator", COLLATOR_SIG);

	if (language != null) {
	    // Create new Locale object on stack
	    il.append(new NEW(cpg.addClass("java/util/Locale")));
	    il.append(DUP);
	    il.append(new PUSH(cpg, language));
	    il.append(new PUSH(cpg, country != null ? country : ""));
	    il.append(new INVOKESPECIAL(initLocale));
	    
	    // Use that Locale object to get the required Collator object
	    il.append(new INVOKESTATIC(getCollator));
	    il.append(new PUTSTATIC(collator));
	}

	il.append(new GETSTATIC(collator));
	il.append(new ICONST(Collator.TERTIARY));
	il.append(new INVOKEVIRTUAL(setStrength));

	il.append(RETURN);

	classInit.stripAttributes(true);
	classInit.setMaxLocals();
	classInit.setMaxStack();
	classInit.removeNOPs();

	return classInit.getMethod();
    }


    /**
     * Compiles a method that overloads NodeSortRecord.extractValueFromDOM()
     */
    private static Method compileExtract(Vector sortObjects,
					 NodeSortRecordGenerator sortRecord,
					 ConstantPoolGen cpg,
					 String className) {
	final InstructionList il = new InstructionList();
	final String DOM_CLASS = sortRecord.getDOMClass();
	
	// String NodeSortRecord.extractValueFromDOM(dom,node,level);
	final CompareGenerator extractMethod =
	    new CompareGenerator(ACC_PUBLIC | ACC_FINAL,
				 de.fub.bytecode.generic.Type.STRING, 
				 new de.fub.bytecode.generic.Type[] {
		                     Util.getJCRefType(DOM_INTF_SIG),
				     de.fub.bytecode.generic.Type.INT,
				     de.fub.bytecode.generic.Type.INT,
				     Util.getJCRefType(TRANSLET_INTF_SIG),
				     de.fub.bytecode.generic.Type.INT
				 },
				 new String[] { "dom",
						"current",
						"level",
						"translet",
						"last"
				 },
				 "extractValueFromDOM", className, il, cpg);

	// String DOM.getNodeValue(int node);
	final int getNodeValue = cpg.addMethodref(DOM_CLASS, "getNodeValue",
						  "(I)" + STRING_SIG);
	// Values needed for the switch statement
	final int levels = sortObjects.size();
	final int match[] = new int[levels];
	final InstructionHandle target[] = new InstructionHandle[levels];
	InstructionHandle tblswitch = null;

	il.append(ALOAD_1);	// DOM arg
	il.append(new CHECKCAST(cpg.addClass(DOM_CLASS)));
	il.append(ASTORE_1);

	// Compile switch statement only if the key has multiple levels
	if (levels > 1) {
	    // Put the parameter to the swtich statement on the stack
	    il.append(new ILOAD(extractMethod.getLocalIndex("level")));
	    // Append the switch statement here later on
	    tblswitch = il.append(new NOP());
	}

	// Append all the cases for the switch statment
	for (int level = 0; level < levels; level++) {
	    match[level] = level;
	    final Sort sort = (Sort)sortObjects.elementAt(level);
	    target[level] = il.append(NOP);
	    sort.translateSelect(sortRecord, extractMethod);
	    il.append(ARETURN);
	}
	
	// Compile def. target for switch statement if key has multiple levels
	if (levels > 1) {
	    // Append the default target - it will _NEVER_ be reached
	    InstructionHandle defaultTarget = il.append(new PUSH(cpg,""));
	    il.insert(tblswitch,new TABLESWITCH(match, target, defaultTarget));
	    il.append(ARETURN);
	}

	extractMethod.stripAttributes(true);
	extractMethod.setMaxLocals();
	extractMethod.setMaxStack();
	extractMethod.removeNOPs();

	return extractMethod.getMethod();
    }


    /**
     * Compiles a method that overloads NodeSortRecord.compareType()
     */
    private static Method compileSortType(Vector sortObjects,
					  NodeSortRecordGenerator sortRecord,
					  ConstantPoolGen cpg,
					  String className) {
	final InstructionList il = new InstructionList();
	
	// int NodeSortRecord.compareType(level);
	final MethodGenerator sortType =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL,
				de.fub.bytecode.generic.Type.INT, 
				new de.fub.bytecode.generic.Type[] {
				    de.fub.bytecode.generic.Type.INT
				},
				new String[] { "level" },
				"compareType", className, il, cpg);
	final int idx = cpg.addFieldref(className, "_compareType", "[I");
	il.append(new GETSTATIC(idx));
	il.append(new ILOAD(sortType.getLocalIndex("level")));
	il.append(IALOAD);
	il.append(IRETURN);

	sortType.stripAttributes(true);
	sortType.setMaxLocals();
	sortType.setMaxStack();
	sortType.removeNOPs();

	return sortType.getMethod();
    }


    /**
     * Compiles a method that overloads NodeSortRecord
     */
    private static Method compileSortOrder(Vector sortObjects,
					   NodeSortRecordGenerator sortRecord,
					   ConstantPoolGen cpg,
					   String className) {
	final InstructionList il = new InstructionList();
	
	// int NodeSortRecord.sortOrder(level);
	final MethodGenerator sortOrder =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL,
				de.fub.bytecode.generic.Type.INT, 
				new de.fub.bytecode.generic.Type[] {
				    de.fub.bytecode.generic.Type.INT
				},
				new String[] { "level" },
				"sortOrder", className, il, cpg);
	final int idx = cpg.addFieldref(className, "_sortOrder", "[I");
	il.append(new GETSTATIC(idx));
	il.append(new ILOAD(sortOrder.getLocalIndex("level")));
	il.append(IALOAD);
	il.append(IRETURN);

	sortOrder.stripAttributes(true);
	sortOrder.setMaxLocals();
	sortOrder.setMaxStack();
	sortOrder.removeNOPs();

	return sortOrder.getMethod();
    }

    /**
     * Compiles a method that overloads NodeSortRecord.getCollator()
     * This method is only compiled if the "lang" attribute is used.
     */
    private static Method compileGetCollator(Vector sortObjects,
					     NodeSortRecordGenerator sortRecord,
					     ConstantPoolGen cpg,
					     String className) {
	final InstructionList il = new InstructionList();
	// Collator NodeSortRecord.getCollator();
	final MethodGenerator getCollator =
	    new MethodGenerator(ACC_PUBLIC | ACC_FINAL,
				Util.getJCRefType(COLLATOR_SIG),
				new de.fub.bytecode.generic.Type[] {},
				new String[] { },
				"getCollator", className, il, cpg);

	// Get index to private static reference in NodeSortRecrd
	final int collator =
	    cpg.addFieldref(className, "collator", COLLATOR_SIG);
	// Feck the Collator object on the stack and return it
	il.append(new GETSTATIC(collator));
	il.append(ARETURN);

	getCollator.stripAttributes(true);
	getCollator.setMaxLocals();
	getCollator.setMaxStack();
	getCollator.removeNOPs();

	return getCollator.getMethod();
    }
}
