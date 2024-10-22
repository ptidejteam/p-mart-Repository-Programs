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
 * $Id: UnresolvedRef.java,v 1.1 2006/03/01 21:14:42 vauchers Exp $
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

/**
 * @author Morten Jorgensen
 */
final class UnresolvedRef extends VariableRefBase {

    private QName           _variableName = null;
    private VariableRefBase _ref = null;
    private VariableBase    _var = null;
    private Stylesheet      _sheet = null;

    public UnresolvedRef(QName name) {
	super();
	_variableName = name;
	_sheet = getStylesheet();
    }

    public QName getName() {
	return(_variableName);
    }

    private ErrorMsg reportError() {
	ErrorMsg err = new ErrorMsg(ErrorMsg.VARIABLE_UNDEF_ERR,
				    _variableName, this);
	getParser().reportError(Constants.ERROR, err);
	return(err);
    }

    private VariableRefBase resolve(Parser parser, SymbolTable stable) {
	// At this point the AST is already built and we should be able to
	// find any declared global variable or parameter
	VariableBase ref = parser.lookupVariable(_variableName);
	if (ref == null) ref = (VariableBase)stable.lookupName(_variableName);
	if (ref == null) {
	    reportError();
	    return null;
	}
	
	// Insert the referenced variable as something the parent variable
	// is dependent of (this class should only be used under variables)
	if ((_var = findParentVariable()) != null) _var.addDependency(ref);

	// Instanciate a true variable/parameter ref
	if (ref instanceof Variable)
	    return(new VariableRef((Variable)ref));
	else if (ref instanceof Param)
	    return(new ParameterRef((Param)ref));
	else
	    return null;
    }

    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	if (_ref != null) {
	    final String name = _variableName.toString();
	    ErrorMsg err = new ErrorMsg(ErrorMsg.CIRCULAR_VARIABLE_ERR,
					name, this);
	}
	if ((_ref = resolve(getParser(), stable)) != null) {
	    return (_type = _ref.typeCheck(stable));
	}
	throw new TypeCheckError(reportError());
    }

    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	if (_ref != null)
	    _ref.translate(classGen, methodGen);
	else
	    reportError();
    }

    public String toString() {
	return "unresolved-ref()";
    }

}
