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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class ProblemFieldBinding extends FieldBinding {
	private int problemId;
// NOTE: must only answer the subset of the name related to the problem

public ProblemFieldBinding(ReferenceBinding declaringClass, char[][] compoundName, int problemId) {
	this(declaringClass, CharOperation.concatWith(compoundName, '.'), problemId);
}
public ProblemFieldBinding(ReferenceBinding declaringClass, char[] name, int problemId) {
	this.declaringClass = declaringClass;
	this.name = name;
	this.problemId = problemId;
}
/* API
* Answer the problem id associated with the receiver.
* NoError if the receiver is a valid binding.
*/

public final int problemId() {
	return problemId;
}
}
