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
package org.eclipse.jdt.internal.ui.fix;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.IFix;
import org.eclipse.jdt.internal.corext.fix.ImportsFix;

import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;

public class ImportsCleanUp extends AbstractCleanUp {
	
	private CodeGenerationSettings fCodeGeneratorSettings;
	private RefactoringStatus fStatus;

	public ImportsCleanUp(Map options) {
		super(options);
    }
	
	public ImportsCleanUp() {
		super();
    }

	/**
	 * {@inheritDoc}
	 */
	public CleanUpRequirements getRequirements() {
		boolean isOrganizeImports= isEnabled(CleanUpConstants.ORGANIZE_IMPORTS);
		return new CleanUpRequirements(isOrganizeImports, isOrganizeImports, null);
	}

	/**
     * {@inheritDoc}
     */
    public IFix createFix(CleanUpContext context) throws CoreException {
    	CompilationUnit compilationUnit= context.getAST();
    	if (compilationUnit == null)
    		return null;
    	
		return ImportsFix.createCleanUp(compilationUnit, fCodeGeneratorSettings,
				isEnabled(CleanUpConstants.ORGANIZE_IMPORTS), fStatus);
	}

    /**
     * {@inheritDoc}
     */
    public RefactoringStatus checkPreConditions(IJavaProject project, ICompilationUnit[] compilationUnits, IProgressMonitor monitor) throws CoreException {
    	
		if (isEnabled(CleanUpConstants.ORGANIZE_IMPORTS)) {
    		fCodeGeneratorSettings= JavaPreferencesSettings.getCodeGenerationSettings(project);
    		fStatus= new RefactoringStatus();
		}
		
		return super.checkPreConditions(project, compilationUnits, monitor);
    }
    
    /**
     * {@inheritDoc}
     */
    public RefactoringStatus checkPostConditions(IProgressMonitor monitor) throws CoreException {
    	fCodeGeneratorSettings= null;
    	try {
	    	if (fStatus == null || fStatus.isOK()) {
	    		return super.checkPostConditions(monitor);
	    	} else {
	    		return fStatus;
	    	}
    	} finally {
    		fStatus= null;
    	}
    }

	/**
     * {@inheritDoc}
     */
    public String[] getDescriptions() {
    	if (isEnabled(CleanUpConstants.ORGANIZE_IMPORTS))
    		return new String[] {MultiFixMessages.ImportsCleanUp_OrganizeImports_Description};
    		
	    return null;
    }

	/**
     * {@inheritDoc}
     */
    public String getPreview() {
		StringBuffer buf= new StringBuffer();
		
		if (isEnabled(CleanUpConstants.ORGANIZE_IMPORTS)) {
			buf.append("import org.model.Engine;\n"); //$NON-NLS-1$
		} else {
			buf.append("import org.model.*;\n"); //$NON-NLS-1$
		}
		
		return buf.toString();
    }

}
