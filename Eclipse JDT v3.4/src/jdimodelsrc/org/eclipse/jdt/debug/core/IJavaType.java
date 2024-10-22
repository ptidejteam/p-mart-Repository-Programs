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
import org.eclipse.debug.core.model.IDebugElement;
 
/**
 * The type of a value on a Java debug target - a primitive
 * data type, class, interface, or array.
 * <p>
 * Since 3.2, an <code>IJavaType</code> is also a debug element
 * </p>
 * @see IJavaValue
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaType extends IDebugElement {
	/**
	 * Returns the JNI-style signature for this type.
	 *
	 * @return signature
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public String getSignature() throws DebugException;
	
	/**
	 * Returns the name of this type. For example, <code>"java.lang.String"</code>.
	 * 
	 * @return the name of this type
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public String getName() throws DebugException;
}

