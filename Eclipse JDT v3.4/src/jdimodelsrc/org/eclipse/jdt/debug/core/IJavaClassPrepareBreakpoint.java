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
package org.eclipse.jdt.debug.core;

import org.eclipse.core.runtime.CoreException;

/**
 * A breakpoint that suspends execution when a class is prepared in
 * a target VM.
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaClassPrepareBreakpoint extends IJavaBreakpoint {
	
	/**
	 * Constant indicating a class prepare breakpoint is associated with a
	 * class type. 
	 */
	public static final int TYPE_CLASS = 0;
	/**
	 * Constant indicating a class prepare breakpoint is associated with an
	 * interface type. 
	 */	
	public static final int TYPE_INTERFACE = 1;
	
	/**
	 * Returns a constant indicating what kind of type this breakpoint
	 * is associated with.
	 * 
	 * @return one of <code>TYPE_CLASS</code> or <code>TYPE_INTERFACE</code>
	 * @throws CoreException if unable to retrieve the attribute
	 */
	public int getMemberType() throws CoreException;
}
