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

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;

import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.fix.CleanUpOptions;
import org.eclipse.jdt.internal.ui.fix.ICleanUp;
import org.eclipse.jdt.internal.ui.fix.ImportsCleanUp;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;

public class MultiOrganizeImportAction extends CleanUpAction {

	public MultiOrganizeImportAction(IWorkbenchSite site) {
		super(site);

		setText(ActionMessages.OrganizeImportsAction_label);
		setToolTipText(ActionMessages.OrganizeImportsAction_tooltip);
		setDescription(ActionMessages.OrganizeImportsAction_description);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.ORGANIZE_IMPORTS_ACTION);
	}

	public MultiOrganizeImportAction(JavaEditor editor) {
		super(editor);

		setText(ActionMessages.OrganizeImportsAction_label);
		setToolTipText(ActionMessages.OrganizeImportsAction_tooltip);
		setDescription(ActionMessages.OrganizeImportsAction_description);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.ORGANIZE_IMPORTS_ACTION);
	}

	protected ICleanUp[] createCleanUps(ICompilationUnit[] units) {
		Map settings= new Hashtable();
		settings.put(CleanUpConstants.ORGANIZE_IMPORTS, CleanUpOptions.TRUE);
		ImportsCleanUp importsCleanUp= new ImportsCleanUp(settings);

		return new ICleanUp[] {
			importsCleanUp
		};
	}

	protected String getActionName() {
		return ActionMessages.OrganizeImportsAction_error_title;
	}
}
