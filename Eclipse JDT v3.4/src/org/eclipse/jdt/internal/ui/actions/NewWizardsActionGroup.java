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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.NewWizardMenu;

import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.jdt.ui.IContextMenuConstants;

import org.eclipse.jdt.internal.ui.workingsets.JavaWorkingSetUpdater;
import org.eclipse.jdt.internal.ui.workingsets.OthersWorkingSetUpdater;


/**
 * Action group that adds the 'new' menu to a context menu.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.1
 */
public class NewWizardsActionGroup extends ActionGroup {

	private IWorkbenchSite fSite;
	
	/**
	 * Creates a new <code>NewWizardsActionGroup</code>. The group requires
	 * that the selection provided by the part's selection provider is of type <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the view part that owns this action group
	 */
	public NewWizardsActionGroup(IWorkbenchSite site) {
		fSite= site;
	}
	

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		
		ISelection selection= getContext().getSelection();
		if (selection instanceof IStructuredSelection) {
			if (canEnable((IStructuredSelection)selection)) {
		        MenuManager newMenu = new MenuManager(ActionMessages.NewWizardsActionGroup_new);
		        menu.appendToGroup(IContextMenuConstants.GROUP_NEW, newMenu);
		        newMenu.add(new NewWizardMenu(fSite.getWorkbenchWindow()));
			}
		}		
		
	}
	
	private boolean canEnable(IStructuredSelection sel) {
		if (sel.size() == 0)
			return true;
		
		List list= sel.toList();
		for (Iterator iterator= list.iterator(); iterator.hasNext();) {
			if (!isNewTarget(iterator.next()))
				return false;
		}

		return true;
	}
	
	private boolean isNewTarget(Object element) {
		if (element == null)
			return true;
		if (element instanceof IResource) {
			return true;
		}
		if (element instanceof IJavaElement) {
			int type= ((IJavaElement)element).getElementType();
			return type == IJavaElement.JAVA_PROJECT ||
				type == IJavaElement.PACKAGE_FRAGMENT_ROOT || 
				type == IJavaElement.PACKAGE_FRAGMENT ||
				type == IJavaElement.COMPILATION_UNIT ||
				type == IJavaElement.TYPE;
		}
		if (element instanceof IWorkingSet) {
			String workingSetId= ((IWorkingSet)element).getId();
			return 
				JavaWorkingSetUpdater.ID.equals(workingSetId) || 
				"org.eclipse.ui.resourceWorkingSetPage".equals(workingSetId) || //$NON-NLS-1$
				OthersWorkingSetUpdater.ID.equals(workingSetId);
		}
		return false;
	}	

}
