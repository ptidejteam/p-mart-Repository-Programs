/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Alex Blewitt - Bug 133277 Allow Sort Members to be performed on package and project levels
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

import org.eclipse.ui.IWorkbenchSite;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.fix.CleanUpRefactoring;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringExecutionStarter;

import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jdt.internal.ui.fix.ICleanUp;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

public class AllCleanUpsAction extends CleanUpAction {

	private IPreferenceChangeListener fPreferenceChangeListener;

	public AllCleanUpsAction(IWorkbenchSite site) {
		super(site);
		setToolTipText(ActionMessages.CleanUpAction_tooltip);
		setDescription(ActionMessages.CleanUpAction_description);
		installPreferenceListener();
		updateActionLabel();

		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.ORGANIZE_IMPORTS_ACTION);
	}

	public AllCleanUpsAction(JavaEditor editor) {
		super(editor);
		setToolTipText(ActionMessages.CleanUpAction_tooltip);
		setDescription(ActionMessages.CleanUpAction_description);
		installPreferenceListener();
		updateActionLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	protected ICleanUp[] createCleanUps(ICompilationUnit[] units) {
		return CleanUpRefactoring.createCleanUps();
	}

	/**
	 * {@inheritDoc}
	 */
	protected String getActionName() {
		return ActionMessages.CleanUpAction_actionName;
	}

	/**
	 * {@inheritDoc}
	 */
	protected void performRefactoring(ICompilationUnit[] cus, ICleanUp[] cleanUps) throws JavaModelException, InvocationTargetException {
		RefactoringExecutionStarter.startCleanupRefactoring(cus, cleanUps, !showWizard(), getShell(), showWizard(), getActionName());
	}

	private boolean showWizard() {
		InstanceScope instanceScope= new InstanceScope();
		IEclipsePreferences instanceNode= instanceScope.getNode(JavaUI.ID_PLUGIN);
		if (instanceNode.get(CleanUpConstants.SHOW_CLEAN_UP_WIZARD, null) != null)
			return instanceNode.getBoolean(CleanUpConstants.SHOW_CLEAN_UP_WIZARD, true);

		DefaultScope defaultScope= new DefaultScope();
		IEclipsePreferences defaultNode= defaultScope.getNode(JavaUI.ID_PLUGIN);
		return defaultNode.getBoolean(CleanUpConstants.SHOW_CLEAN_UP_WIZARD, true);
	}

	private void updateActionLabel() {
		if (showWizard()) {
			setText(ActionMessages.CleanUpAction_labelWizard);
		} else {
			setText(ActionMessages.CleanUpAction_label);
		}
	}
	
	private void installPreferenceListener() {
	    fPreferenceChangeListener= new IPreferenceChangeListener() {
			public void preferenceChange(PreferenceChangeEvent event) {
				if (event.getKey().equals(CleanUpConstants.SHOW_CLEAN_UP_WIZARD)) {
					updateActionLabel();
				}
			}
		};
		new InstanceScope().getNode(JavaUI.ID_PLUGIN).addPreferenceChangeListener(fPreferenceChangeListener);
    }
	
	public void dispose() {
		if (fPreferenceChangeListener != null) {
			new InstanceScope().getNode(JavaUI.ID_PLUGIN).removePreferenceChangeListener(fPreferenceChangeListener);
			fPreferenceChangeListener= null;
		}
	}
}
