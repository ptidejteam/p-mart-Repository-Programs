/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
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

import org.eclipse.jdt.core.ICompilationUnit;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;

import org.eclipse.jdt.internal.ui.fix.CleanUpOptions;
import org.eclipse.jdt.internal.ui.fix.CodeFormatCleanUp;
import org.eclipse.jdt.internal.ui.fix.ICleanUp;

/**
 * @since 3.4
 */
public class MultiFormatAction extends CleanUpAction {

	public MultiFormatAction(IWorkbenchSite site) {
		super(site);
	}

	/* 
	 * @see org.eclipse.jdt.internal.ui.actions.CleanUpAction#createCleanUps(org.eclipse.jdt.core.ICompilationUnit[])
	 */
	protected ICleanUp[] createCleanUps(ICompilationUnit[] units) {
		Map settings= new Hashtable();
		settings.put(CleanUpConstants.FORMAT_SOURCE_CODE, CleanUpOptions.TRUE);
		
		return new ICleanUp[] { 
				new CodeFormatCleanUp(settings) 
		};
	}

	/* 
	 * @see org.eclipse.jdt.internal.ui.actions.CleanUpAction#getActionName()
	 */
	protected String getActionName() {
		return ActionMessages.FormatAllAction_label;
	}

}
