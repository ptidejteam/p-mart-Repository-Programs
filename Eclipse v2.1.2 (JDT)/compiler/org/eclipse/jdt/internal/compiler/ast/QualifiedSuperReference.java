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

import org.eclipse.jdt.internal.compiler.IAbstractSyntaxTreeVisitor;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class QualifiedSuperReference extends QualifiedThisReference {
	
	public QualifiedSuperReference(TypeReference name, int pos, int sourceEnd) {
		super(name, pos, sourceEnd);
	}

	public boolean isSuper() {

		return true;
	}

	public boolean isThis() {

		return false;
	}

	public TypeBinding resolveType(BlockScope scope) {

		if ((this.bits & ParenthesizedMASK) != 0) {
			scope.problemReporter().invalidParenthesizedExpression(this);
			return null;
		}
		super.resolveType(scope);
		if (currentCompatibleType == null)
			return null; // error case

		if (scope.isJavaLangObject(currentCompatibleType)) {
			scope.problemReporter().cannotUseSuperInJavaLangObject(this);
			return null;
		}
		return this.resolvedType = currentCompatibleType.superclass();
	}

	public String toStringExpression() {

		return qualification.toString(0) + ".super"; //$NON-NLS-1$
	}

	public void traverse(
		IAbstractSyntaxTreeVisitor visitor,
		BlockScope blockScope) {

		if (visitor.visit(this, blockScope)) {
			qualification.traverse(visitor, blockScope);
		}
		visitor.endVisit(this, blockScope);
	}
}
