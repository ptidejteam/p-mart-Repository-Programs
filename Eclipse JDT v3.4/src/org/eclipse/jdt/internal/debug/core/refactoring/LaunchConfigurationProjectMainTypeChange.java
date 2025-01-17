/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.core.refactoring;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.launching.JavaMigrationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.ibm.icu.text.MessageFormat;

/**
 * The change for the main type project change of a launch configuration
 */
public class LaunchConfigurationProjectMainTypeChange extends Change {
	
	private ILaunchConfiguration fLaunchConfiguration;
	private String fNewMainTypeName;
	private String fNewProjectName;
	private String fNewLaunchConfigurationName;
	private String fOldMainTypeName;
	private String fOldProjectName;
    private String fNewConfigContainerName;
	
    /**
	 * LaunchConfigurationProjectMainTypeChange constructor.
	 * @param launchConfiguration the launch configuration to modify
	 * @param newMainTypeName the name of the new main type, or <code>null</code> if not modified.
	 * @param newProjectName the name of the project, or <code>null</code> if not modified.
	 */
	public LaunchConfigurationProjectMainTypeChange(ILaunchConfiguration launchConfiguration, String newMainTypeName, String newProjectName) throws CoreException {
		fLaunchConfiguration = launchConfiguration;
		fNewMainTypeName = newMainTypeName;
        fNewProjectName = newProjectName;
		fOldMainTypeName = fLaunchConfiguration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String) null);
		fOldProjectName = fLaunchConfiguration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		if (fNewMainTypeName != null) {
			// generate the new configuration name
			String oldName = Signature.getSimpleName(fOldMainTypeName);
			String newName = Signature.getSimpleName(fNewMainTypeName);
			String lcname = fLaunchConfiguration.getName();
			fNewLaunchConfigurationName = lcname.replaceAll(oldName, newName);
			if (lcname.equals(fNewLaunchConfigurationName) || DebugPlugin.getDefault().getLaunchManager().isExistingLaunchConfigurationName(fNewLaunchConfigurationName)) {
				fNewLaunchConfigurationName = null;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
	 */
	public Object getModifiedElement() {
		return fLaunchConfiguration;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	public String getName() {
		if (fNewLaunchConfigurationName != null) {
			return MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_0, new String[] {fLaunchConfiguration.getName(), fNewLaunchConfigurationName}); 
		} 
		if (fNewProjectName == null) {
			return MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_1, new String[] {fLaunchConfiguration.getName()}); 
		}
		if (fNewMainTypeName == null) {
			return MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_2, new String[] {fLaunchConfiguration.getName()}); 
		}
		return MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_3, new String[] {fLaunchConfiguration.getName()}); 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void initializeValidationData(IProgressMonitor pm) {}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (fLaunchConfiguration.exists()) {
			String typeName = fLaunchConfiguration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null);
			String projectName = fLaunchConfiguration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
			if(fOldMainTypeName != null) {
				if (fOldMainTypeName.equals(typeName)) {
					if (fOldProjectName.equals(projectName)) {
						return new RefactoringStatus();
					}
					return RefactoringStatus.createWarningStatus(MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_4, new String[] {fLaunchConfiguration.getName(), fOldProjectName})); 
				}
				return RefactoringStatus.createWarningStatus(MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_5, new String[] {fLaunchConfiguration.getName(), fOldMainTypeName}));
			}
			else {
				//need to catch the case for remote java LC's, they have no maintype
				if (fOldProjectName.equals(projectName)) {
					return new RefactoringStatus();
				}
				return RefactoringStatus.createWarningStatus(MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_4, new String[] {fLaunchConfiguration.getName(), fOldProjectName}));
			}
		} 
		return RefactoringStatus.createFatalErrorStatus(MessageFormat.format(RefactoringMessages.LaunchConfigurationProjectMainTypeChange_6, new String[] {fLaunchConfiguration.getName()})); 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {    
		final ILaunchConfigurationWorkingCopy wc = fLaunchConfiguration.getWorkingCopy();
        if (fNewConfigContainerName != null) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IWorkspaceRoot root = workspace.getRoot();
            IProject project = root.getProject(fNewProjectName);
            IContainer container = (IContainer) project.findMember(fNewConfigContainerName);
            wc.setContainer(container);
        }
		String oldMainTypeName;
		String oldProjectName;
		if (fNewMainTypeName != null) {
			oldMainTypeName = fOldMainTypeName;
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, fNewMainTypeName);
		} 
		else {
			oldMainTypeName = null;
		}
		if (fNewProjectName != null) {
			oldProjectName = fOldProjectName;
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, fNewProjectName);
		} 
		else {
			oldProjectName = null;
		}
		if (fNewLaunchConfigurationName != null) {
			wc.rename(fNewLaunchConfigurationName);
		}
		// update resource mapping
		JavaMigrationDelegate.updateResourceMapping(wc);
		if (wc.isDirty()) {
			fLaunchConfiguration = wc.doSave();
		}
		// create the undo change
		return new LaunchConfigurationProjectMainTypeChange(wc, oldMainTypeName, oldProjectName);
	}
	
	/**
     * Sets the new container name
     * @param newContainerName the new name for the container
     */
    public void setNewContainerName(String newContainerName) {
        fNewConfigContainerName = newContainerName;
    }
}
