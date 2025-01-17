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

/**
 * Modifiers common to Java debug elements that have  associated Java
 * member declarations. For example, the method associated with a stack frame,
 * or the field associated with a variable.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJavaModifiers {		

	/**
	 * Returns whether the associated Java construct is declared as public.
	 *
	 * @return whether the associated Java construct is declared as public
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isPublic() throws DebugException;
	/**
	 * Returns whether the associated Java construct is declared as private.
	 *
	 * @return whether the associated Java construct is declared as private
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isPrivate() throws DebugException;
	/**
	 * Returns whether the associated Java construct is declared as protected.
	 *
	 * @return whether the associated Java construct is declared as protected
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isProtected() throws DebugException;
	/**
	 * Returns whether the associated Java construct is declared with
	 * no protection modifier (package private protection).
	 *
	 * @return whether the associated Java construct is declared as package private
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isPackagePrivate() throws DebugException;
	/**
	 * Returns whether the associated Java construct is declared as final.
	 * 
	 * @return whether the associated Java construct is declared as final
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isFinal() throws DebugException;
	/**
	 * Returns whether the associated Java construct is declared as static.
	 * 
	 * @return whether the associated Java construct is declared as static
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isStatic() throws DebugException;
	/**
	 * Returns whether the associated Java construct is synthetic. 
	 * Synthetic members are generated by the compiler
	 * and are not present in source code.
	 *
	 * @return whether the associated Java construct is synthetic
	 * @exception DebugException if this method fails.  Reasons include:
	 * <ul><li>Failure communicating with the VM.  The DebugException's
	 * status code contains the underlying exception responsible for
	 * the failure.</li></ul>
	 */
	public boolean isSynthetic() throws DebugException;


}


