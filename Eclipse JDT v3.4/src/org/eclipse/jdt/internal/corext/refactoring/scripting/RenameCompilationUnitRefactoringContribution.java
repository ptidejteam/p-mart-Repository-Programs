/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.scripting;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringContribution;
import org.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.RenameJavaElementDescriptor;

import org.eclipse.jdt.internal.corext.refactoring.JavaRefactoringArguments;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameCompilationUnitProcessor;

/**
 * Refactoring contribution for the rename compilation unit refactoring.
 * 
 * @since 3.2
 */
public final class RenameCompilationUnitRefactoringContribution extends JavaRefactoringContribution {

	/**
	 * {@inheritDoc}
	 */
	public Refactoring createRefactoring(JavaRefactoringDescriptor descriptor, RefactoringStatus status) throws CoreException {
		JavaRefactoringArguments arguments= new JavaRefactoringArguments(descriptor.getProject(), retrieveArgumentMap(descriptor));
		RenameCompilationUnitProcessor processor= new RenameCompilationUnitProcessor(arguments, status);
		return new RenameRefactoring(processor);
	}

	public RefactoringDescriptor createDescriptor() {
		return new RenameJavaElementDescriptor(IJavaRefactorings.RENAME_COMPILATION_UNIT);
	}

	public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment, Map arguments, int flags) {
		return new RenameJavaElementDescriptor(id, project, description, comment, arguments, flags);
	}
}
