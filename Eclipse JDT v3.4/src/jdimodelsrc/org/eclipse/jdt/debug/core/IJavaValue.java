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


import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

/**
 * An object, primitive data type, or array, on a Java virtual machine.
 * @see org.eclipse.debug.core.model.IValue
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaValue extends IValue {
	/**
	 * Returns the JNI-style signature for the type of this
	 * value, or <code>null</code> if the value is <code>null</code>.
	 *
	 * @return signature, or <code>null</code> if signature is <code>null</code>
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li>
	 * <li>The type associated with the signature is not yet loaded</li></ul>
	 */
	public String getSignature() throws DebugException;
		
	/**
	 * Returns the generic signature as defined in the JVM
	 * specification for the type of this value.
	 * Returns <code>null</code> if the value is <code>null</code>,
	 * or if the type of this value is not a generic type.
	 *
	 * @return signature, or <code>null</code> if generic signature not available
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li>
	 * <li>The type associated with the signature is not yet loaded</li></ul>
	 * @since 3.1
	 */
	public String getGenericSignature() throws DebugException;
		
	/**
	 * Returns the type of this value, or <code>null</code>
	 * if this value represents the <code>null</code> value
	 * 
	 * @return the type of this value, or <code>null</code>
	 * if this value represents the <code>null</code> value
	 * 
	 * @since 2.0
	 */
	public IJavaType getJavaType() throws DebugException;
	
}


