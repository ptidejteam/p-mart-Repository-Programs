/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.reorg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.core.resources.IResource;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.refactoring.JavaRefactoringArguments;
import org.eclipse.jdt.internal.corext.refactoring.tagging.IQualifiedNameUpdating;
import org.eclipse.jdt.internal.corext.refactoring.tagging.IReferenceUpdating;

import org.eclipse.jdt.internal.ui.refactoring.RefactoringSaveHelper;

public interface IReorgPolicy extends IReorgDestinationValidator {
	
	/**
	 * @return the unique id of this policy
	 */
	public String getPolicyId();
	
	/**
	 * @return the source resources to reorg
	 */
	public IResource[] getResources();
	
	/**
	 * @return the source java elements to reorg
	 */
	public IJavaElement[] getJavaElements();
	
	/**
	 * @return true if this policy can handle the source elements
	 * @throws JavaModelException
	 */
	public boolean canEnable() throws JavaModelException;
	
	/**
	 * @return the save mode required for this reorg policy
	 * 
	 * @see RefactoringSaveHelper#SAVE_ALL
	 * @see RefactoringSaveHelper#SAVE_JAVA_ONLY_UPDATES
	 * @see RefactoringSaveHelper#SAVE_NON_JAVA_UPDATES
	 */
	public int getSaveMode();
	
	/**
	 * Can destination be a target for the given source elements?
	 * 
	 * @param destination the destination to verify
	 * @return OK status if valid destination
	 * @throws JavaModelException
	 */
	public RefactoringStatus verifyDestination(IReorgDestination destination) throws JavaModelException;

	/**
	 * @param destination the destination for this reorg
	 */
	public void setDestination(IReorgDestination destination);
	
	/**
	 * @return the destination of this reorg or null if not a resource
	 */
	public IResource getResourceDestination();
	
	/**
	 * @return the destination of this reorg or null if not a java element
	 */
	public IJavaElement getJavaElementDestination();
	
	/**
	 * @return a descriptor describing a reorg from source to target
	 */
	public ChangeDescriptor getDescriptor();

	
	/**
	 * Initializes the reorg policy with arguments from a script.
	 * 
	 * @param arguments
	 *            the arguments
	 * @return an object describing the status of the initialization. If the
	 *         status has severity <code>FATAL_ERROR</code>, the refactoring
	 *         will not be executed.
	 */
	public RefactoringStatus initialize(JavaRefactoringArguments arguments);
	
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor, CheckConditionsContext context, IReorgQueries queries) throws CoreException;
	
	public RefactoringParticipant[] loadParticipants(RefactoringStatus status, RefactoringProcessor processor, String[] natures, SharableParticipants shared) throws CoreException;
	
	public static interface ICopyPolicy extends IReorgPolicy{
		public Change createChange(IProgressMonitor monitor, INewNameQueries queries) throws JavaModelException;
		public ReorgExecutionLog getReorgExecutionLog();
	}
	
	public static interface IMovePolicy extends IReferenceUpdating, IQualifiedNameUpdating, IReorgPolicy{
		public Change createChange(IProgressMonitor monitor) throws JavaModelException;
		public Change postCreateChange(Change[] participantChanges, IProgressMonitor monitor) throws CoreException;
		public ICreateTargetQuery getCreateTargetQuery(ICreateTargetQueries createQueries);
		public boolean isTextualMove();
		public CreateTargetExecutionLog getCreateTargetExecutionLog();
		public void setDestinationCheck(boolean check);
		public boolean hasAllInputSet();
		public boolean canUpdateReferences();
		public boolean canUpdateQualifiedNames();
	}
}
