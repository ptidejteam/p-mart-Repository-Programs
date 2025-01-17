/*
 * @(#)$Id: Stylesheet.java,v 1.1 2006/03/01 21:12:07 vauchers Exp $
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
 *    the documentation and/or other makterials provided with the
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.util.InstructionFinder;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.apache.xml.dtm.DTM;

public final class Stylesheet extends SyntaxTreeNode {

    private String       _version;
    private QName        _name;
    private String       _systemId;
    private Stylesheet   _parentStylesheet;
	
    // Contains global variables and parameters defined in the stylesheet
    private Vector _globals = new Vector();

    // Used to cache the result returned by <code>hasLocalParams()</code>.
    private Boolean _hasLocalParams = null;

    //  The name of the class being generated.
    private String _className;
    
    // Contains all templates defined in this stylesheet
    private final Vector _templates = new Vector();

    private int _nextModeSerial = 1;
    private final Hashtable _modes = new Hashtable();
    private final Hashtable _extensions = new Hashtable();

    public  Stylesheet _importedFrom = null;
    public  Stylesheet _includedFrom = null;
    private int _importPrecedence = 1;
    private Mode _defaultMode;
    private boolean _multiDocument = false;
    private boolean _callsNodeset = false;

    // All named key elements (needed by Key/IdPattern)
    private Hashtable _keys = new Hashtable();

    private boolean _numberFormattingUsed = false;

    private boolean _simplified = false;

    private SourceLoader _loader = null;

    private boolean _templateInlining = true;

    private boolean _forwardReference = false;
    
    private boolean _hasIdCall = false;

    private Properties _outputProperties = null;

    public void setForwardReference() {
	_forwardReference = true;
    }

    public boolean getTemplateInlining() {
	return _templateInlining;
    }

    public void setTemplateInlining(boolean flag) {
	_templateInlining = flag;
    }

    public boolean isSimplified() {
	return(_simplified);
    }

    public void setSimplified() {
	_simplified = true;
    }
    
    public void setHasIdCall(boolean flag) {
        _hasIdCall = flag;
    }
    
    public void setOutputProperty(String key, String value) {
	if (_outputProperties == null) {
	    _outputProperties = new Properties();
	}
	_outputProperties.setProperty(key, value);
    }

    public void setOutputProperties(Properties props) {
	_outputProperties = props;
    }

    public Properties getOutputProperties() {
	return _outputProperties;
    }

    public void setMultiDocument(boolean flag) {	
	_multiDocument = flag;
    }

    public boolean isMultiDocument() {
	return _multiDocument;
    }

    public void setCallsNodeset(boolean flag) {
	if (flag) setMultiDocument(flag);
	_callsNodeset = flag;
    }

    public boolean callsNodeset() {
	return _callsNodeset;
    }

    public void numberFormattingUsed() {
	_numberFormattingUsed = true;
    }

    public void setImportPrecedence(final int precedence) {
	// Set import precedence for this stylesheet
	_importPrecedence = precedence;

	// Set import precedence for all included stylesheets
	final Enumeration elements = elements();
	while (elements.hasMoreElements()) {
	    SyntaxTreeNode child = (SyntaxTreeNode)elements.nextElement();
	    if (child instanceof Include) {
		Stylesheet included = ((Include)child).getIncludedStylesheet();
		if (included != null && included._includedFrom == this) {
		    included.setImportPrecedence(precedence);
		}
	    }
	}

	// Set import precedence for the stylesheet that imported this one
	if (_importedFrom != null) {
	    if (_importedFrom.getImportPrecedence() < precedence) {
		final Parser parser = getParser();
		final int nextPrecedence = parser.getNextImportPrecedence();
		_importedFrom.setImportPrecedence(nextPrecedence);
	    }
	}
	// Set import precedence for the stylesheet that included this one
	else if (_includedFrom != null) {
	    if (_includedFrom.getImportPrecedence() != precedence)
		_includedFrom.setImportPrecedence(precedence);
	}
    }
    
    public int getImportPrecedence() {
	return _importPrecedence;
    }

    public boolean checkForLoop(String systemId) {
	// Return true if this stylesheet includes/imports itself
	if (_systemId != null && _systemId.equals(systemId)) {
	    return true;
	}
	// Then check with any stylesheets that included/imported this one
	if (_parentStylesheet != null) 
	    return _parentStylesheet.checkForLoop(systemId);
	// Otherwise OK
	return false;
    }
    
    public void setParser(Parser parser) {
	super.setParser(parser);
	_name = makeStylesheetName("__stylesheet_");
    }
    
    public void setParentStylesheet(Stylesheet parent) {
	_parentStylesheet = parent;
    }
    
    public Stylesheet getParentStylesheet() {
	return _parentStylesheet;
    }

    public void setImportingStylesheet(Stylesheet parent) {
	_importedFrom = parent;
    }

    public void setIncludingStylesheet(Stylesheet parent) {
	_includedFrom = parent;
    }

    public void setSystemId(String systemId) {
	_systemId = systemId;
    }
    
    public String getSystemId() {
	return _systemId;
    }

    public void setSourceLoader(SourceLoader loader) {
	_loader = loader;
    }
    
    public SourceLoader getSourceLoader() {
	return _loader;
    }

    private QName makeStylesheetName(String prefix) {
	return getParser().getQName(prefix+getXSLTC().nextStylesheetSerial());
    }

    /**
     * Returns true if this stylesheet has global vars or params.
     */
    public boolean hasGlobals() {
	return _globals.size() > 0;
    }

    /**
     * Returns true if at least one template in the stylesheet has params
     * defined. Uses the variable <code>_hasLocalParams</code> to cache the
     * result.
     */
    public boolean hasLocalParams() {
	if (_hasLocalParams == null) {
	    final int n = _templates.size();
	    for (int i = 0; i < n; i++) {
		final Template template = (Template)_templates.elementAt(i);
		if (template.hasParams()) {
		    _hasLocalParams = new Boolean(true);
		    return true;
		}
	    }
	    _hasLocalParams = new Boolean(false);
	    return false;
	}
	else {
	    return _hasLocalParams.booleanValue();
	}
    }

    /**
     * Adds a single prefix mapping to this syntax tree node.
     * @param prefix Namespace prefix.
     * @param uri Namespace URI.
     */
    protected void addPrefixMapping(String prefix, String uri) {
	if (prefix.equals(EMPTYSTRING) && uri.equals(XHTML_URI)) return;
	super.addPrefixMapping(prefix, uri);
    }

    /**
     * Store extension URIs
     */
    private void extensionURI(String prefixes, SymbolTable stable) {
	if (prefixes != null) {
	    StringTokenizer tokens = new StringTokenizer(prefixes);
	    while (tokens.hasMoreTokens()) {
		final String prefix = tokens.nextToken();
		final String uri = lookupNamespace(prefix);
		if (uri != null) {
		    _extensions.put(uri, prefix);
		}
	    }
	}
    }

    public boolean isExtension(String uri) {
	return (_extensions.get(uri) != null);
    }

    public void excludeExtensionPrefixes(Parser parser) {
	final SymbolTable stable = parser.getSymbolTable();
    	final String excludePrefixes = getAttribute("exclude-result-prefixes");
	final String extensionPrefixes = getAttribute("extension-element-prefixes");
	
	// Exclude XSLT uri 
	stable.excludeURI(Constants.XSLT_URI);
	stable.excludeNamespaces(excludePrefixes);
	stable.excludeNamespaces(extensionPrefixes);
	extensionURI(extensionPrefixes, stable);
    }

    /**
     * Parse the version and uri fields of the stylesheet and add an
     * entry to the symbol table mapping the name <tt>__stylesheet_</tt>
     * to an instance of this class.
     */
    public void parseContents(Parser parser) {
	final SymbolTable stable = parser.getSymbolTable();

	/*
	// Make sure the XSL version set in this stylesheet
	if ((_version == null) || (_version.equals(EMPTYSTRING))) {
	    reportError(this, parser, ErrorMsg.REQUIRED_ATTR_ERR,"version");
	}
	// Verify that the version is 1.0 and nothing else
	else if (!_version.equals("1.0")) {
	    reportError(this, parser, ErrorMsg.XSL_VERSION_ERR, _version);
	}
	*/

	// Add the implicit mapping of 'xml' to the XML namespace URI
	addPrefixMapping("xml", "http://www.w3.org/XML/1998/namespace");

	// Report and error if more than one stylesheet defined
	final Stylesheet sheet = stable.addStylesheet(_name, this);
	if (sheet != null) {
	    // Error: more that one stylesheet defined
	    ErrorMsg err = new ErrorMsg(ErrorMsg.MULTIPLE_STYLESHEET_ERR,this);
	    parser.reportError(Constants.ERROR, err);
	}

	// If this is a simplified stylesheet we must create a template that
	// grabs the root node of the input doc ( <xsl:template match="/"/> ).
	// This template needs the current element (the one passed to this
	// method) as its only child, so the Template class has a special
	// method that handles this (parseSimplified()).
	if (_simplified) {
	    stable.excludeURI(XSLT_URI);
	    Template template = new Template();
	    template.parseSimplified(this, parser);
	}
	// Parse the children of this node
	else {
	    parseOwnChildren(parser);
	}
    }

    /**
     * Parse all direct children of the <xsl:stylesheet/> element.
     */
    public final void parseOwnChildren(Parser parser) {
	final Vector contents = getContents();
	final int count = contents.size();

	// We have to scan the stylesheet element's top-level elements for
	// variables and/or parameters before we parse the other elements
	for (int i = 0; i < count; i++) {
	    SyntaxTreeNode child = (SyntaxTreeNode)contents.elementAt(i);
	    if ((child instanceof VariableBase) ||
		(child instanceof NamespaceAlias)) {
		parser.getSymbolTable().setCurrentNode(child);
		child.parseContents(parser);
	    }
	}

	// Now go through all the other top-level elements...
	for (int i = 0; i < count; i++) {
	    SyntaxTreeNode child = (SyntaxTreeNode)contents.elementAt(i);
	    if (!(child instanceof VariableBase) && 
		!(child instanceof NamespaceAlias)) {
		parser.getSymbolTable().setCurrentNode(child);
		child.parseContents(parser);
	    }

	    // All template code should be compiled as methods if the
	    // <xsl:apply-imports/> element was ever used in this stylesheet
	    if (!_templateInlining && (child instanceof Template)) {
		Template template = (Template)child;
		String name = "template$dot$" + template.getPosition();
		template.setName(parser.getQName(name));
	    }
	}
    }

    public void processModes() {
	if (_defaultMode == null)
	    _defaultMode = new Mode(null, this, Constants.EMPTYSTRING);
	_defaultMode.processPatterns(_keys);
	final Enumeration modes = _modes.elements();
	while (modes.hasMoreElements()) {
	    final Mode mode = (Mode)modes.nextElement();
	    mode.processPatterns(_keys);
	}
    }
	
    private void compileModes(ClassGenerator classGen) {
	_defaultMode.compileApplyTemplates(classGen);
	final Enumeration modes = _modes.elements();
	while (modes.hasMoreElements()) {
	    final Mode mode = (Mode)modes.nextElement();
	    mode.compileApplyTemplates(classGen);
	}
    }

    public Mode getMode(QName modeName) {
	if (modeName == null) {
	    if (_defaultMode == null) {
		_defaultMode = new Mode(null, this, Constants.EMPTYSTRING);
	    }
	    return _defaultMode;
	}
	else {
	    Mode mode = (Mode)_modes.get(modeName);
	    if (mode == null) {
		final String suffix = Integer.toString(_nextModeSerial++);
		_modes.put(modeName, mode = new Mode(modeName, this, suffix));
	    }
	    return mode;
	}
    }

    /**
     * Type check all the children of this node.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final int count = _globals.size();
	for (int i = 0; i < count; i++) {
	    final VariableBase var = (VariableBase)_globals.elementAt(i);
	    var.typeCheck(stable);
	}
	return typeCheckContents(stable);
    }

    /**
     * Translate the stylesheet into JVM bytecodes. 
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	translate();
    }

    private void addDOMField(ClassGenerator classGen) {
	final FieldGen fgen = new FieldGen(ACC_PUBLIC,
					   Util.getJCRefType(DOM_INTF_SIG),
					   DOM_FIELD,
					   classGen.getConstantPool());
	classGen.addField(fgen.getField());
    }

    /**
     * Translate the stylesheet into JVM bytecodes. 
     */
    public void translate() {
	Output lastOutputElement = null;
	_className = getXSLTC().getClassName();

	// Define a new class by extending TRANSLET_CLASS
	final ClassGenerator classGen =
	    new ClassGenerator(_className,
			       TRANSLET_CLASS,
			       Constants.EMPTYSTRING,
			       ACC_PUBLIC | ACC_SUPER,
			       null, this);
	
	addDOMField(classGen);

	// Compile transform() to initialize parameters, globals & output
	// and run the transformation
	compileTransform(classGen);

	// Translate all non-template elements and filter out all templates
	final Enumeration elements = elements();
	while (elements.hasMoreElements()) {
	    Object element = elements.nextElement();
	    // xsl:template
	    if (element instanceof Template) {
		// Separate templates by modes
		final Template template = (Template)element;
		_templates.addElement(template);
		getMode(template.getModeName()).addTemplate(template);
	    }
	    // xsl:attribute-set
	    else if (element instanceof AttributeSet) {
		((AttributeSet)element).translate(classGen, null);
	    }
	    else if (element instanceof Output) {
		// save the element for later to pass to compileConstructor 
		Output output = (Output)element;
		if (output.enabled()) lastOutputElement = output;
	    }
	    else {
		// Global variables and parameters are handled elsewhere.
		// Other top-level non-template elements are ignored. Literal
		// elements outside of templates will never be output.
	    }
	}

	processModes();
	compileModes(classGen);
	compileConstructor(classGen, lastOutputElement);

	if (!getParser().errorsFound()) {
	    getXSLTC().dumpClass(classGen.getJavaClass());
	}
    }

    /**
     * Compile the translet's constructor
     */
    private void compileConstructor(ClassGenerator classGen, Output output) {

	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = new InstructionList();

	final MethodGenerator constructor =
	    new MethodGenerator(ACC_PUBLIC,
				org.apache.bcel.generic.Type.VOID, 
				null, null, "<init>", 
				_className, il, cpg);

	// Call the constructor in the AbstractTranslet superclass
	il.append(classGen.loadTranslet());
	il.append(new INVOKESPECIAL(cpg.addMethodref(TRANSLET_CLASS,
						     "<init>", "()V")));

	// Put the names array into the translet - used for dom/translet mapping
	final Vector names = getXSLTC().getNamesIndex();
	il.append(classGen.loadTranslet());
	il.append(new PUSH(cpg, names.size()));
	il.append(new ANEWARRAY(cpg.addClass(STRING)));		

	for (int i = 0; i < names.size(); i++) {
	    final String name = (String)names.elementAt(i);
	    il.append(DUP);
	    il.append(new PUSH(cpg, i));
	    il.append(new PUSH(cpg, name));
	    il.append(AASTORE);
	}
	il.append(new PUTFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       NAMES_INDEX,
					       NAMES_INDEX_SIG)));

	// Put the namespace names array into the translet
	final Vector namespaces = getXSLTC().getNamespaceIndex();
	il.append(classGen.loadTranslet());
	il.append(new PUSH(cpg, namespaces.size()));
	il.append(new ANEWARRAY(cpg.addClass(STRING)));		

	for (int i = 0; i < namespaces.size(); i++) {
	    final String ns = (String)namespaces.elementAt(i);
	    il.append(DUP);
	    il.append(new PUSH(cpg, i));
	    il.append(new PUSH(cpg, ns));
	    il.append(AASTORE);
	}
	il.append(new PUTFIELD(cpg.addFieldref(TRANSLET_CLASS,
					       NAMESPACE_INDEX,
					       NAMESPACE_INDEX_SIG)));

	if (_hasIdCall) {
	    il.append(classGen.loadTranslet());
	    il.append(new PUSH(cpg, Boolean.TRUE));
	    il.append(new PUTFIELD(cpg.addFieldref(TRANSLET_CLASS,
					           HASIDCALL_INDEX,
					           HASIDCALL_INDEX_SIG)));
	}
	
	// Compile in code to set the output configuration from <xsl:output>
	if (output != null) {
	    // Set all the output settings files in the translet
	    output.translate(classGen, constructor);
	}

	// Compile default decimal formatting symbols.
	// This is an implicit, nameless xsl:decimal-format top-level element.
	if (_numberFormattingUsed)
	    DecimalFormatting.translateDefaultDFS(classGen, constructor);

	il.append(RETURN);

	constructor.stripAttributes(true);
	constructor.setMaxLocals();
	constructor.setMaxStack();
	classGen.addMethod(constructor.getMethod());
    }

    /**
     * Compile a topLevel() method into the output class. This method is 
     * called from transform() to handle all non-template top-level elemtents.
     * Returns the signature of the topLevel() method.
     */
    private String compileTopLevel(ClassGenerator classGen,
				   Enumeration elements) {

	final ConstantPoolGen cpg = classGen.getConstantPool();

	final org.apache.bcel.generic.Type[] argTypes = {
	    Util.getJCRefType(DOM_INTF_SIG),
	    Util.getJCRefType(NODE_ITERATOR_SIG),
	    Util.getJCRefType(TRANSLET_OUTPUT_SIG)
	};

	final String[] argNames = {
	    DOCUMENT_PNAME, ITERATOR_PNAME, TRANSLET_OUTPUT_PNAME
	};

	final InstructionList il = new InstructionList();

	final MethodGenerator toplevel =
	    new MethodGenerator(ACC_PUBLIC,
				org.apache.bcel.generic.Type.VOID,
				argTypes, argNames,
				"topLevel", _className, il,
				classGen.getConstantPool());

	toplevel.addException("org.apache.xalan.xsltc.TransletException");

	// Define and initialize 'current' variable with the root node
	final LocalVariableGen current = 
	    toplevel.addLocalVariable("current",
				    org.apache.bcel.generic.Type.INT,
				    il.getEnd(), null);

	final int setFilter = cpg.addInterfaceMethodref(DOM_INTF,
			       "setFilter",
			       "(Lorg/apache/xalan/xsltc/StripFilter;)V");

	il.append(new PUSH(cpg, DTM.ROOT_NODE));
	il.append(new ISTORE(current.getIndex()));

	// Resolve any forward referenes and translate global variables/params
	_globals = resolveReferences(_globals);
	final int count = _globals.size();
	for (int i = 0; i < count; i++) {
	    final VariableBase var = (VariableBase)_globals.elementAt(i);
	    var.translate(classGen,toplevel);
	}

	// Compile code for other top-level elements
	Vector whitespaceRules = new Vector();
	while (elements.hasMoreElements()) {
	    final Object element = elements.nextElement();
	    // xsl:decimal-format
	    if (element instanceof DecimalFormatting) {
		((DecimalFormatting)element).translate(classGen,toplevel);
	    }
	    // xsl:strip/preserve-space
	    else if (element instanceof Whitespace) {
		whitespaceRules.addAll(((Whitespace)element).getRules());
	    }
	}

	// Translate all whitespace strip/preserve rules
	if (whitespaceRules.size() > 0) {
	    Whitespace.translateRules(whitespaceRules,classGen);
	}

	if (classGen.containsMethod(STRIP_SPACE, STRIP_SPACE_PARAMS) != null) {
	    il.append(toplevel.loadDOM());
	    il.append(classGen.loadTranslet());
	    il.append(new INVOKEINTERFACE(setFilter, 2));
	}

	il.append(RETURN);

	// Compute max locals + stack and add method to class
	toplevel.stripAttributes(true);
	toplevel.setMaxLocals();
	toplevel.setMaxStack();
	toplevel.removeNOPs();

	classGen.addMethod(toplevel.getMethod());
	
	return("("+DOM_INTF_SIG+NODE_ITERATOR_SIG+TRANSLET_OUTPUT_SIG+")V");
    }

    /**
     * This method returns a vector with variables in the order in 
     * which they are to be compiled. The order is determined by the 
     * dependencies between them and the order in which they were defined 
     * in the stylesheet. The first step is to close the input vector under
     * the dependence relation (this is usually needed when variables are
     * defined inside other variables in a RTF).
     */
    private Vector resolveReferences(Vector input) {

	// Make sure that the vector 'input' is closed
	for (int i = 0; i < input.size(); i++) {
	    final VariableBase var = (VariableBase) input.elementAt(i);
	    final Vector dep  = var.getDependencies();
	    final int depSize = (dep != null) ? dep.size() : 0;

	    for (int j = 0; j < depSize; j++) {
		final VariableBase depVar = (VariableBase) dep.elementAt(j);
		if (!input.contains(depVar)) {
		    input.addElement(depVar);
		}
	    }
	}

	/* DEBUG CODE - INGORE
	for (int i = 0; i < input.size(); i++) {
	    final VariableBase var = (VariableBase) input.elementAt(i);
	    System.out.println("var = " + var);
	}
	System.out.println("=================================");
	*/

	Vector result = new Vector();
	while (input.size() > 0) {
	    boolean changed = false;
	    for (int i = 0; i < input.size(); ) {
		final VariableBase var = (VariableBase)input.elementAt(i);
		final Vector dep = var.getDependencies();
		if (dep == null || result.containsAll(dep)) {
		    result.addElement(var);
		    input.remove(i);
		    changed = true;
		}
		else {
		    i++;
		}
	    }

	    // If nothing was changed in this pass then we have a circular ref
	    if (!changed) {
		ErrorMsg err = new ErrorMsg(ErrorMsg.CIRCULAR_VARIABLE_ERR,
					    input.toString(), this);
		getParser().reportError(Constants.ERROR, err);
		return(result);
	    }
	}

	/* DEBUG CODE - INGORE
	System.out.println("=================================");
	for (int i = 0; i < result.size(); i++) {
	    final VariableBase var = (VariableBase) result.elementAt(i);
	    System.out.println("var = " + var);
	}
	*/

	return result;
    }

    /**
     * Compile a buildKeys() method into the output class. This method is 
     * called from transform() to handle build all indexes needed by key().
     */
    private String compileBuildKeys(ClassGenerator classGen) {

	final ConstantPoolGen cpg = classGen.getConstantPool();

	final org.apache.bcel.generic.Type[] argTypes = {
	    Util.getJCRefType(DOM_INTF_SIG),
	    Util.getJCRefType(NODE_ITERATOR_SIG),
	    Util.getJCRefType(TRANSLET_OUTPUT_SIG),
	    org.apache.bcel.generic.Type.INT
	};

	final String[] argNames = {
	    DOCUMENT_PNAME, ITERATOR_PNAME, TRANSLET_OUTPUT_PNAME, "current"
	};

	final InstructionList il = new InstructionList();

	final MethodGenerator buildKeys =
	    new MethodGenerator(ACC_PUBLIC,
				org.apache.bcel.generic.Type.VOID,
				argTypes, argNames,
				"buildKeys", _className, il,
				classGen.getConstantPool());

	buildKeys.addException("org.apache.xalan.xsltc.TransletException");
	
	final Enumeration elements = elements();
	// Compile code for other top-level elements
	while (elements.hasMoreElements()) {
	    // xsl:key
	    final Object element = elements.nextElement();
	    if (element instanceof Key) {
		final Key key = (Key)element;
		key.translate(classGen, buildKeys);
		_keys.put(key.getName(),key);
	    }
	}
	
	il.append(RETURN);
	
	// Compute max locals + stack and add method to class
	buildKeys.stripAttributes(true);
	buildKeys.setMaxLocals();
	buildKeys.setMaxStack();
	buildKeys.removeNOPs();

	classGen.addMethod(buildKeys.getMethod());
	
	return("("+DOM_INTF_SIG+NODE_ITERATOR_SIG+TRANSLET_OUTPUT_SIG+"I)V");
    }

    /**
     * Compile transform() into the output class. This method is used to 
     * initialize global variables and global parameters. The current node
     * is set to be the document's root node.
     */
    private void compileTransform(ClassGenerator classGen) {
	final ConstantPoolGen cpg = classGen.getConstantPool();

	/* 
	 * Define the the method transform with the following signature:
	 * void transform(DOM, NodeIterator, HandlerBase)
	 */
	final org.apache.bcel.generic.Type[] argTypes = 
	    new org.apache.bcel.generic.Type[3];
	argTypes[0] = Util.getJCRefType(DOM_INTF_SIG);
	argTypes[1] = Util.getJCRefType(NODE_ITERATOR_SIG);
	argTypes[2] = Util.getJCRefType(TRANSLET_OUTPUT_SIG);

	final String[] argNames = new String[3];
	argNames[0] = DOCUMENT_PNAME;
	argNames[1] = ITERATOR_PNAME;
	argNames[2] = TRANSLET_OUTPUT_PNAME;

	final InstructionList il = new InstructionList();
	final MethodGenerator transf =
	    new MethodGenerator(ACC_PUBLIC,
				org.apache.bcel.generic.Type.VOID,
				argTypes, argNames,
				"transform",
				_className,
				il,
				classGen.getConstantPool());
	transf.addException("org.apache.xalan.xsltc.TransletException");

	// Define and initialize current with the root node
	final LocalVariableGen current = 
	    transf.addLocalVariable("current",
				    org.apache.bcel.generic.Type.INT,
				    il.getEnd(), null);
	final String applyTemplatesSig = classGen.getApplyTemplatesSig();
	final int applyTemplates = cpg.addMethodref(getClassName(),
						    "applyTemplates",
						    applyTemplatesSig);
	final int domField = cpg.addFieldref(getClassName(),
					     DOM_FIELD,
					     DOM_INTF_SIG);

	// push translet for PUTFIELD
	il.append(classGen.loadTranslet());
	// prepare appropriate DOM implementation
	
	if (isMultiDocument()) {
	    il.append(new NEW(cpg.addClass(MULTI_DOM_CLASS)));
	    il.append(DUP);
	}
	
	il.append(classGen.loadTranslet());
	il.append(transf.loadDOM());
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(TRANSLET_CLASS,
						     "makeDOMAdapter",
						     "("+DOM_INTF_SIG+")"+
						     DOM_ADAPTER_SIG)));
	// DOMAdapter is on the stack

	if (isMultiDocument()) {
	    final int init = cpg.addMethodref(MULTI_DOM_CLASS,
					      "<init>",
					      "("+DOM_INTF_SIG+")V");
	    il.append(new INVOKESPECIAL(init));
	    // MultiDOM is on the stack
	}
	
	//store to _dom variable
	il.append(new PUTFIELD(domField));

	// continue with globals initialization
	il.append(new PUSH(cpg, DTM.ROOT_NODE));
	il.append(new ISTORE(current.getIndex()));

	// Transfer the output settings to the output post-processor
	il.append(classGen.loadTranslet());
	il.append(transf.loadHandler());
	final int index = cpg.addMethodref(TRANSLET_CLASS,
					   "transferOutputSettings",
					   "("+OUTPUT_HANDLER_SIG+")V");
	il.append(new INVOKEVIRTUAL(index));

    // Look for top-level elements that need handling
	final Enumeration toplevel = elements();
	if ((_globals.size() > 0) || (toplevel.hasMoreElements())) {
	    // Compile method for handling top-level elements
	    final String topLevelSig = compileTopLevel(classGen, toplevel);
	    // Get a reference to that method
	    final int topLevelIdx = cpg.addMethodref(getClassName(),
						     "topLevel",
						     topLevelSig);
	    // Push all parameters on the stack and call topLevel()
	    il.append(classGen.loadTranslet()); // The 'this' pointer
	    il.append(classGen.loadTranslet());
	    il.append(new GETFIELD(domField));  // The DOM reference
	    il.append(transf.loadIterator());
	    il.append(transf.loadHandler());    // The output handler
	    il.append(new INVOKEVIRTUAL(topLevelIdx));
	}
	
	// Compile buildKeys -- TODO: omit if not needed		
	
	final String keySig = compileBuildKeys(classGen);
	final int    keyIdx = cpg.addMethodref(getClassName(),
					       "buildKeys", keySig);
	il.append(classGen.loadTranslet());     // The 'this' pointer
	il.append(classGen.loadTranslet());
	il.append(new GETFIELD(domField));      // The DOM reference
	il.append(transf.loadIterator());       // Not really used, but...
	il.append(transf.loadHandler());        // The output handler
	il.append(new PUSH(cpg, DTM.ROOT_NODE)); // Start with the root node
	il.append(new INVOKEVIRTUAL(keyIdx));



	// start document
	il.append(transf.loadHandler());
	il.append(transf.startDocument());

	// push first arg for applyTemplates
	il.append(classGen.loadTranslet());
	// push translet for GETFIELD to get DOM arg
	il.append(classGen.loadTranslet());
	il.append(new GETFIELD(domField));
	// push remaining 2 args
	il.append(transf.loadIterator());
	il.append(transf.loadHandler());
	il.append(new INVOKEVIRTUAL(applyTemplates));
	// endDocument
	il.append(transf.loadHandler());
	il.append(transf.endDocument());

	il.append(RETURN);

	// Compute max locals + stack and add method to class
	transf.stripAttributes(true);
	transf.setMaxLocals();
	transf.setMaxStack();
	transf.removeNOPs();

	classGen.addMethod(transf.getMethod());
    }

    /**
     * Peephole optimization: Remove sequences of [ALOAD, POP].
     */
    private void peepHoleOptimization(MethodGenerator methodGen) {
	final String pattern = "`ALOAD'`POP'`Instruction'";
	final InstructionList il = methodGen.getInstructionList();
	final InstructionFinder find = new InstructionFinder(il);
	for(Iterator iter=find.search(pattern); iter.hasNext(); ) {
	    InstructionHandle[] match = (InstructionHandle[])iter.next();
	    try {
		il.delete(match[0], match[1]);
	    } 
	    catch (TargetLostException e) {
            	// TODO: move target down into the list
            }
	}
    }

    public int addParam(Param param) {
	_globals.addElement(param);
	return _globals.size() - 1;
    }

    public int addVariable(Variable global) {
	_globals.addElement(global);
	return _globals.size() - 1;
    }

    public void display(int indent) {
	indent(indent);
	Util.println("Stylesheet");
	displayContents(indent + IndentIncrement);
    }

    // do we need this wrapper ?????
    public String getNamespace(String prefix) {
	return lookupNamespace(prefix);
    }

    public String getClassName() {
	return _className;
    }

    public Vector getTemplates() {
	return _templates;
    }
}
