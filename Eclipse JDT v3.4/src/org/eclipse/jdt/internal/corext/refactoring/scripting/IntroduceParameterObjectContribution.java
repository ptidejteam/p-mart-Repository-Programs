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
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;

import org.eclipse.jdt.core.refactoring.descriptors.IntroduceParameterObjectDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringContribution;
import org.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringDescriptor;

import org.eclipse.jdt.internal.corext.refactoring.structure.IntroduceParameterObjectProcessor;

public class IntroduceParameterObjectContribution extends JavaRefactoringContribution {

	public IntroduceParameterObjectContribution() {
	}

	public Refactoring createRefactoring(JavaRefactoringDescriptor descriptor, RefactoringStatus status) throws CoreException {
		if (descriptor instanceof IntroduceParameterObjectDescriptor) {
			IntroduceParameterObjectProcessor processor= new IntroduceParameterObjectProcessor((IntroduceParameterObjectDescriptor) descriptor);
			return new ProcessorBasedRefactoring(processor);
		}
		return null;
	}
	
	public RefactoringDescriptor createDescriptor() {
		return new IntroduceParameterObjectDescriptor();
	}

	public RefactoringDescriptor createDescriptor(String id, String project, String description, String comment, Map arguments, int flags) {
		return new IntroduceParameterObjectDescriptor(project, description, comment, arguments, flags);
	}

}
