/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.IAbstractSyntaxTreeVisitor;

public class EmptyStatement extends Statement {

	public EmptyStatement(int startPosition, int endPosition) {
		this.sourceStart = startPosition;
		this.sourceEnd = endPosition;
	}

	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		return flowInfo;
	}

	public void generateCode(BlockScope currentScope, CodeStream codeStream){
		// no bytecode, no need to check for reachability or recording source positions
	}
	
	public void resolve(BlockScope scope) {
	}
	
	public void traverse(IAbstractSyntaxTreeVisitor visitor, BlockScope scope) {
		visitor.visit(this, scope);
		visitor.endVisit(this, scope);
	}
	
	public String toString(int tab) {
		return tabString(tab) + ";"; //$NON-NLS-1$ 
	}
}

