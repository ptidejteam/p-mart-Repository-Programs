/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.actions;

import org.eclipse.swt.widgets.Shell;

import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.jface.text.ITextSelection;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IEditorStatusLine;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;

import org.eclipse.jdt.ui.JavaUI;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.ui.actions.ActionUtil;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaTextSelection;
import org.eclipse.jdt.internal.ui.search.FindOccurrencesEngine;
import org.eclipse.jdt.internal.ui.search.ImplementOccurrencesFinder;

/**
 * Action to find all implement occurrences of an extended class or an implemented interface.
 * <p> 
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 3.1
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class FindImplementOccurrencesAction extends SelectionDispatchAction {
	
	private JavaEditor fEditor;
	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * 
	 * @param editor the Java editor
	 * 
	 * @noreference This constructor is not intended to be referenced by clients.
	 */
	public FindImplementOccurrencesAction(JavaEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(getEditorInput(editor) != null);
	}
	
	/**
	 * Creates a new <code>FindImplementOccurrencesAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindImplementOccurrencesAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.FindImplementOccurrencesAction_text); 
		setToolTipText(ActionMessages.FindImplementOccurrencesAction_toolTip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.FIND_IMPLEMENT_OCCURRENCES);
	}
	
	//---- Text Selection ----------------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(ITextSelection selection) {
		setEnabled(true);
	}

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param selection
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void selectionChanged(JavaTextSelection selection) {
		CompilationUnit astRoot= selection.resolvePartialAstAtOffset();
		setEnabled(astRoot != null && new ImplementOccurrencesFinder().initialize(astRoot, selection.getOffset(), selection.getLength()) == null);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @since 3.2
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(false);
	}

	/*
	 * @see org.eclipse.jdt.ui.actions.SelectionDispatchAction#run(org.eclipse.jface.text.ITextSelection)
	 */
	public final void run(ITextSelection ts) {
		ITypeRoot input= getEditorInput(fEditor);
		if (!ActionUtil.isProcessable(getShell(), input))
			return;
		FindOccurrencesEngine engine= FindOccurrencesEngine.create(new ImplementOccurrencesFinder());
		try {
			String result= engine.run(input, ts.getOffset(), ts.getLength());
			if (result != null)
				showMessage(getShell(), fEditor, result);
		} catch (JavaModelException e) {
			JavaPlugin.log(e);
		}
	}

	private static ITypeRoot getEditorInput(JavaEditor editor) {
		return JavaUI.getEditorInputTypeRoot(editor.getEditorInput());
	} 
		
	private static void showMessage(Shell shell, JavaEditor editor, String msg) {
		IEditorStatusLine statusLine= (IEditorStatusLine) editor.getAdapter(IEditorStatusLine.class);
		if (statusLine != null) 
			statusLine.setMessage(true, msg, null); 
		shell.getDisplay().beep();
	}
}
