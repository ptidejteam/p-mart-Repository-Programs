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

import org.eclipse.jdt.core.IJavaElement;

/**
 * This operation moves elements from their current
 * container to a specified destination container, optionally renaming the
 * elements.
 * A move operation is equivalent to a copy operation, where
 * the source elements are deleted after the copy.
 * <p>This operation can be used for reorganizing elements within the same container.
 *
 * @see CopyElementsOperation
 */
public class MoveElementsOperation extends CopyElementsOperation {
/**
 * When executed, this operation will move the given elements to the given containers.
 */
public MoveElementsOperation(IJavaElement[] elementsToMove, IJavaElement[] destContainers, boolean force) {
	super(elementsToMove, destContainers, force);
}
/**
 * Returns the <code>String</code> to use as the main task name
 * for progress monitoring.
 */
protected String getMainTaskName() {
	return Util.bind("operation.moveElementProgress"); //$NON-NLS-1$
}
/**
 * @see CopyElementsOperation#isMove()
 */
protected boolean isMove() {
	return true;
}
}
