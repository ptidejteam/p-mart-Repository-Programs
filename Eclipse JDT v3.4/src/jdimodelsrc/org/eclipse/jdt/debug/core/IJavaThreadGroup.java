/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.core;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugElement;

/**
 * Represents a thread group in the target VM.
 * 
 * @since 3.2
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaThreadGroup extends IDebugElement {

	/**
	 * Returns the threads in this thread group. Does not include
	 * threads in subgroups.
	 * 
	 * @return threads in this group
	 * @throws DebugException
	 */
	public IJavaThread[] getThreads() throws DebugException;
	
	/**
	 * Returns whether this group contains any threads.
	 * 
	 * @return whether this group contains any threads
	 * @throws DebugException
	 */
	public boolean hasThreads() throws DebugException;
	
	/**
	 * Returns the thread group this thread group is contained in or <code>null</code>
	 * if none.
	 * 
	 * @return parent thread group or <code>null</code>
	 * @throws DebugException
	 */
	public IJavaThreadGroup getThreadGroup() throws DebugException;
	
	/**
	 * Returns whether this thread group contains subgroups.
	 * 
	 * @return whether this thread group contains subgroups
	 * @throws DebugException
	 */
	public boolean hasThreadGroups() throws DebugException;
	
	/**
	 * Returns immediate thread groups contained in this thread. Does not include
	 * subgroups of immediate groups.
	 * 
	 * @return thread groups contained in this group
	 * @throws DebugException
	 */
	public IJavaThreadGroup[] getThreadGroups() throws DebugException;
	
	/**
	 * Returns the name of this thread group.
	 * 
	 * @return thread group name
	 * @throws DebugException
	 */
	public String getName() throws DebugException;
}
