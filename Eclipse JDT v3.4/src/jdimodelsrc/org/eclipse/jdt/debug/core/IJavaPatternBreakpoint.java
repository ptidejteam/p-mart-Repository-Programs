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
 * A line breakpoint installed in types associated with a specific source file
 * (based on source file name debug attribute) and whose fully
 * qualified name matches a specified pattern.
 * @since 2.0
 * @deprecated use <code>IJavaStratumLineBreakpoint</code> instead
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaPatternBreakpoint extends IJavaLineBreakpoint {

	/**
	 * Returns the type name pattern this breakpoint uses to identify types
	 * in which to install itself.
	 * 
	 * @return the type name pattern this breakpoint uses to identify types
	 *  in which to install itself
	 * @exception CoreException if unable to access the property from
	 *  this breakpoint's underlying marker
	 */
	public String getPattern() throws CoreException;
	
	/**
	 * Returns the source file name in which this breakpoint is set.
	 * When this breakpoint specifies a source file name, this breakpoint is
	 * only installed in types whose source file name debug attribute
	 * match this value.
	 * 
	 * @return the source file name in which this breakpoint is set
	 * @exception CoreException if unable to access the property from
	 *  this breakpoint's underlying marker
	 */
	public String getSourceName() throws CoreException;	

}

