/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.core.refactoring;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.debug.core.IJavaWatchpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.ui.BreakpointUtils;
import org.eclipse.ltk.core.refactoring.Change;

import com.ibm.icu.text.MessageFormat;

/**
 * @since 3.2
 *
 */
public class WatchpointFieldChange extends WatchpointChange {
	
	private IField fDestField;
	
	public WatchpointFieldChange(IJavaWatchpoint watchpoint, IField destField) throws CoreException {
		super(watchpoint);
		fDestField = destField;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	public String getName() {
		return MessageFormat.format(RefactoringMessages.WatchpointFieldChange_0,
				new String[] {getBreakpointLabel(getOriginalBreakpoint()), fDestField.getElementName()});
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change perform(IProgressMonitor pm) throws CoreException {
		Map map = new HashMap();
		BreakpointUtils.addJavaBreakpointAttributes(map, fDestField);
		IResource resource = BreakpointUtils.getBreakpointResource(fDestField);
		int[] range = getNewLineNumberAndRange(fDestField);
		IJavaWatchpoint breakpoint = JDIDebugModel.createWatchpoint(
				resource,
				fDestField.getDeclaringType().getFullyQualifiedName(),
				fDestField.getElementName(),
				range[0],
				range[1],
				range[2],
				getHitCount(),
				true,
				map);
		apply(breakpoint);
		getOriginalBreakpoint().delete();
		return new DeleteBreakpointChange(breakpoint);
	}

}
