/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.changes;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.eclipse.jdt.core.JavaCore;

import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;

import org.eclipse.jdt.internal.ui.JavaPlugin;

//import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;

public class DynamicValidationStateChange extends CompositeChange implements WorkspaceTracker.Listener {
	
	private boolean fListenerRegistered= false;
	private RefactoringStatus fValidationState= null;
	private long fTimeStamp;
	private ISchedulingRule fSchedulingRule;
	
	// 30 minutes
	private static final long LIFE_TIME= 30 * 60 * 1000;
	
	public DynamicValidationStateChange(Change change) {
		super(change.getName());
		add(change);
		markAsSynthetic();
		fSchedulingRule= ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public DynamicValidationStateChange(String name) {
		super(name);
		markAsSynthetic();
		fSchedulingRule= ResourcesPlugin.getWorkspace().getRoot();
	}
	
	public DynamicValidationStateChange(String name, Change[] changes) {
		super(name, changes);
		markAsSynthetic();
		fSchedulingRule= ResourcesPlugin.getWorkspace().getRoot();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void initializeValidationData(IProgressMonitor pm) {
		super.initializeValidationData(pm);
		WorkspaceTracker.INSTANCE.addListener(this);
		fListenerRegistered= true;
		fTimeStamp= System.currentTimeMillis();
	}
	
	public void dispose() {
		if (fListenerRegistered) {
			WorkspaceTracker.INSTANCE.removeListener(this);
			fListenerRegistered= false;
		}
		super.dispose();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		if (fValidationState == null) {
			return super.isValid(pm);
		}
		return fValidationState;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		final Change[] result= new Change[1];
		IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				result[0]= DynamicValidationStateChange.super.perform(monitor);
			}
		};
		JavaCore.run(runnable, fSchedulingRule, pm);
		return result[0];
	}

	/**
	 * {@inheritDoc}
	 */
	protected Change createUndoChange(Change[] childUndos) {
		DynamicValidationStateChange result= new DynamicValidationStateChange(getName());
		for (int i= 0; i < childUndos.length; i++) {
			result.add(childUndos[i]);
		}
		return result;
	}
	
	public void workspaceChanged() {
		long currentTime= System.currentTimeMillis();
		if (currentTime - fTimeStamp < LIFE_TIME)
			return;
		fValidationState= RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.DynamicValidationStateChange_workspace_changed);
		// remove listener from workspace tracker
		WorkspaceTracker.INSTANCE.removeListener(this);
		fListenerRegistered= false;
		// clear up the children to not hang onto too much memory
		Change[] children= clear();
		for (int i= 0; i < children.length; i++) {
			final Change change= children[i];
			SafeRunner.run(new ISafeRunnable() {
				public void run() throws Exception {
					change.dispose();
				}
				public void handleException(Throwable exception) {
					JavaPlugin.log(exception);
				}
			});
		}
	}
	
	public void setSchedulingRule(ISchedulingRule schedulingRule) {
		fSchedulingRule= schedulingRule;	
	}
	
	public ISchedulingRule getSchedulingRule() {
		return fSchedulingRule;
	}
}
