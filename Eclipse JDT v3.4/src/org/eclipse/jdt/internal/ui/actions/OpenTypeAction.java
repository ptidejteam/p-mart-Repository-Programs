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
package org.eclipse.jdt.internal.ui.actions;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.NewProjectAction;
import org.eclipse.ui.dialogs.SelectionDialog;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;

import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;

public class OpenTypeAction extends Action implements IWorkbenchWindowActionDelegate, IActionDelegate2 {

	public OpenTypeAction() {
		super();
		setText(JavaUIMessages.OpenTypeAction_label);
		setDescription(JavaUIMessages.OpenTypeAction_description);
		setToolTipText(JavaUIMessages.OpenTypeAction_tooltip);
		setImageDescriptor(JavaPluginImages.DESC_TOOL_OPENTYPE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.OPEN_TYPE_ACTION);
	}

	public void run() {
		runWithEvent(null);
	}
	
	public void runWithEvent(Event e) {
		Shell parent= JavaPlugin.getActiveWorkbenchShell();
		if (! doCreateProjectFirstOnEmptyWorkspace(parent)) {
			return;
		}
		
		SelectionDialog dialog;
		if (e != null && e.stateMask == SWT.MOD1) {
			// use old open type dialog when MOD1 (but no other modifier) is down:
			dialog= createOpenTypeSelectionDialog2(parent);
		} else {
			dialog= new OpenTypeSelectionDialog(parent, true, PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
		}
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);

		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IType type= null;
			for (int i= 0; i < types.length; i++) {
				type= (IType) types[i];
				try {
					JavaUI.openInEditor(type, true, true);
				} catch (CoreException x) {
					ExceptionHandler.handle(x, JavaUIMessages.OpenTypeAction_errorTitle, JavaUIMessages.OpenTypeAction_errorMessage);
				}
			}
		}
	}

	/**
	 * @deprecated
	 * @param parent
	 * @return the dialog
	 */
	private SelectionDialog createOpenTypeSelectionDialog2(Shell parent) {
		return new org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog2(parent, false, PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
	}

	/**
	 * Opens the new project dialog if the workspace is empty.
	 * @param parent the parent shell
	 * @return returns <code>true</code> when a project has been created, or <code>false</code> when the
	 * new project has been canceled.
	 */
	protected boolean doCreateProjectFirstOnEmptyWorkspace(Shell parent) {
		IWorkspaceRoot workspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
		if (workspaceRoot.getProjects().length == 0) {
			String title= JavaUIMessages.OpenTypeAction_dialogTitle; 
			String message= JavaUIMessages.OpenTypeAction_createProjectFirst; 
			if (MessageDialog.openQuestion(parent, title, message)) {
				new NewProjectAction().run();
				return workspaceRoot.getProjects().length != 0;
			}
			return false;
		}
		return true;
	}
	
	// ---- IWorkbenchWindowActionDelegate
	// ------------------------------------------------

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		// do nothing.
	}

	public void init(IWorkbenchWindow window) {
		// do nothing.
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
	
	// ---- IActionDelegate2
	// ------------------------------------------------

	public void runWithEvent(IAction action, Event event) {
		runWithEvent(event);
	}
	
	public void init(IAction action) {
		// do nothing.
	}
}
