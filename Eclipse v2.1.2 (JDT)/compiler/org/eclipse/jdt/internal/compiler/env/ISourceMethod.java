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
package org.eclipse.jdt.internal.compiler.env;

public interface ISourceMethod extends IGenericMethod {

/**
 * Answer the unresolved names of the argument types
 * or null if the array is empty.
 *
 * A name is a simple name or a qualified, dot separated name.
 * For example, Hashtable or java.util.Hashtable.
 */

char[][] getArgumentTypeNames();
/**
 * Answer the source end position of the method's declaration.
 */

int getDeclarationSourceEnd();
/**
 * Answer the source start position of the method's declaration.
 */

int getDeclarationSourceStart();
/**
 * Answer the unresolved names of the exception types
 * or null if the array is empty.
 *
 * A name is a simple name or a qualified, dot separated name.
 * For example, Hashtable or java.util.Hashtable.
 */

char[][] getExceptionTypeNames();
/**
 * Answer the source end position of the method's selector.
 */

int getNameSourceEnd();
/**
 * Answer the source start position of the method's selector.
 */

int getNameSourceStart();
/**
 * Answer the unresolved name of the return type
 * or null if receiver is a constructor or clinit.
 *
 * The name is a simple name or a qualified, dot separated name.
 * For example, Hashtable or java.util.Hashtable.
 */

char[] getReturnTypeName();
}
