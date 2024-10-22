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
package org.eclipse.jdt.internal.core;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.hierarchy.RegionBasedTypeHierarchy;
import org.eclipse.jdt.internal.core.hierarchy.TypeHierarchy;

/**
 * This operation creates an <code>ITypeHierarchy</code> for a specific type within
 * a specified region, or for all types within a region. The specified
 * region limits the number of resolved subtypes (to the subset of
 * types in the specified region). The resolved supertypes may go outside
 * of the specified region in order to reach the root(s) of the type
 * hierarchy. A Java Project is required to provide a context (classpath)
 * to use while resolving supertypes and subtypes.
 *
 * @see ITypeHierarchy
 */

public class CreateTypeHierarchyOperation extends JavaModelOperation {
	/**
	 * The generated type hierarchy
	 */
	protected TypeHierarchy typeHierarchy;
	
	public IWorkingCopy[] workingCopies;
	
/**
 * Constructs an operation to create a type hierarchy for the
 * given type within the specified region, in the context of
 * the given project.
 */
public CreateTypeHierarchyOperation(IType element, IRegion region, IJavaProject project, boolean computeSubtypes) throws JavaModelException {
	super(element);
	this.typeHierarchy = new RegionBasedTypeHierarchy(region, project, element, computeSubtypes);
}
/**
 * Constructs an operation to create a type hierarchy for the
 * given type and working copies.
 */
public CreateTypeHierarchyOperation(IType element, IWorkingCopy[] workingCopies, IJavaSearchScope scope, boolean computeSubtypes) throws JavaModelException {
	super(element);
	this.typeHierarchy = new TypeHierarchy(element, scope, computeSubtypes);
	this.workingCopies = workingCopies;
}
/**
 * Constructs an operation to create a type hierarchy for the
 * given type and working copies.
 */
public CreateTypeHierarchyOperation(IType element, IWorkingCopy[] workingCopies, IJavaProject project, boolean computeSubtypes) throws JavaModelException {
	super(element);
	this.typeHierarchy = new TypeHierarchy(element, project, computeSubtypes);
	this.workingCopies = workingCopies;
}
/**
 * Performs the operation - creates the type hierarchy
 * @exception JavaModelException The operation has failed.
 */
protected void executeOperation() throws JavaModelException {
	this.typeHierarchy.refresh(this);
}
/**
 * Returns the generated type hierarchy.
 */
public ITypeHierarchy getResult() {
	return this.typeHierarchy;
}
/**
 * @see JavaModelOperation
 */
public boolean isReadOnly() {
	return true;
}
/**
 * Possible failures: <ul>
 *	<li>NO_ELEMENTS_TO_PROCESS - at least one of a type or region must
 *			be provided to generate a type hierarchy.
 *	<li>ELEMENT_NOT_PRESENT - the provided type or type's project does not exist
 * </ul>
 */
public IJavaModelStatus verify() {
	IJavaElement elementToProcess= getElementToProcess();
	if (elementToProcess == null && !(this.typeHierarchy instanceof RegionBasedTypeHierarchy)) {
		return new JavaModelStatus(IJavaModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
	}
	if (elementToProcess != null && !elementToProcess.exists()) {
		return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST, elementToProcess);
	}
	IJavaProject project = this.typeHierarchy.javaProject();
	if (project != null && !project.exists()) {
		return new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_DOES_NOT_EXIST, project);
	}
	return JavaModelStatus.VERIFIED_OK;
}
}
