/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.JavaModelException;

/**
 * This operation renames resources (Package fragments and compilation units).
 *
 * <p>Notes:<ul>
 * <li>When a compilation unit is renamed, its main type and the constructors of the 
 * 		main type are renamed.
 * </ul>
 */
public class RenameResourceElementsOperation extends MoveResourceElementsOperation {
/**
 * When executed, this operation will rename the specified elements with the given names in the
 * corresponding destinations.
 */
public RenameResourceElementsOperation(IJavaElement[] elements, IJavaElement[] destinations, String[] newNames, boolean force) {
	//a rename is a move to the same parent with a new name specified
	//these elements are from different parents
	super(elements, destinations, force);
	setRenamings(newNames);
}
/**
 * @see MultiOperation
 */
protected String getMainTaskName() {
	return Util.bind("operation.renameResourceProgress"); //$NON-NLS-1$
}
/**
 * @see CopyResourceElementsOperation#isRename()
 */
protected boolean isRename() {
	return true;
}
/**
 * @see MultiOperation
 */
protected void verify(IJavaElement element) throws JavaModelException {
	super.verify(element);

	int elementType = element.getElementType();
	
	if (!(elementType == IJavaElement.COMPILATION_UNIT || elementType == IJavaElement.PACKAGE_FRAGMENT)) {
		error(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, element);
	}
	if (elementType == IJavaElement.COMPILATION_UNIT) {
		if (((ICompilationUnit) element).isWorkingCopy()) {
			error(IJavaModelStatusConstants.INVALID_ELEMENT_TYPES, element);
		}
	}
	verifyRenaming(element);
}
}
