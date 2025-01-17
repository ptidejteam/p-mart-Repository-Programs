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

package org.eclipse.jdt.core.util;

import java.util.Comparator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.SortElementsOperation;

/**
 * Operation for sorting members within a compilation unit.
 * <p>
 * This class provides all functionality via static members; it is not
 * intended to be instantiated or subclassed.
 * </p>
 * 
 * @since 2.1
 */
public final class CompilationUnitSorter {
	
 	/**
 	 * Private constructor to prevent instantiation.
 	 */
	private CompilationUnitSorter() {
	} 
	
	/**
	 * Name of auxillary property whose value can be used to determine the
	 * original relative order of two body declarations. This allows a
	 * comparator to preserve the relative positions of certain kinds of
	 * body declarations when required.
	 * <p>
	 * All body declarations passed to the comparator's <code>compare</code>
	 * method by <code>CompilationUnitSorter.sort</code> carry an
	 * Integer-valued property. The body declaration with the lower value
	 * comes before the one with the higher value. The exact numeric value
	 * of these properties is unspecified.
	 * </p>
	 * <p>
	 * Example usage:
	 * <pre>
	 * BodyDeclaration b1 = (BodyDeclaration) object1;
	 * BodyDeclaration b2 = (BodyDeclaration) object2;
	 * Integer i1 = (Integer) b1.getProperty(RELATIVE_ORDER);
	 * Integer i2 = (Integer) b2.getProperty(RELATIVE_ORDER);
	 * return i1.intValue() - i2.intValue(); // preserve original order
	 * </pre>
	 * </p>
	 * 
	 * @see #sort
	 * @see org.eclipse.jdt.core.dom.BodyDeclaration
	 */
	public static final String RELATIVE_ORDER = "relativeOrder"; //$NON-NLS-1$

	/**
	 * Reorders the declarations in the given compilation unit. The caller is
	 * responsible for arranging in advance that the given compilation unit is
	 * a working copy, and for saving the changes afterwards.
	 * <p>
	 * <b>Note:</b> Reordering the members within a type declaration might be
	 * more than a cosmetic change and could have potentially serious
	 * repercussions. Firstly, the order in which the fields of a type are
	 * initialized is significant in the Java language; reordering fields
	 * and initializers may result in compilation errors or change the execution
	 * behavior of the code. Secondly, reordering a class's members may affect
	 * how its instances are serialized. This operation should therefore be used
	 * with caution and due concern for potential negative side effects.
	 * </p>
	 * <p>
	 * The optional <code>positions</code> array contains a non-decreasing 
	 * ordered list of character-based source positions within the compilation
	 * unit's source code string. Upon return from this method, the positions in
	 * the array reflect the corresponding new locations in the modified source
	 * code string, Note that this operation modifies the given array in place.
	 * </p>
	 * <p>
	 * The <code>compare</code> method of the given comparator is passed pairs
	 * of AST body declarations (subclasses of <code>BodyDeclaration</code>) 
	 * representing body declarations at the same level. The comparator is
	 * called on body declarations of nested classes, including anonymous and
	 * local classes, but always at the same level. Clients need to provide
	 * a comparator implementation (there is no standard comparator). The
	 * <code>RELATIVE_ORDER</code> property attached to these AST nodes afforts
	 * the comparator a way to preserve the original relative order.
	 * </p>
	 * <p>
	 * The body declarations passed as parameters to the comparator
	 * always carry at least the following minimal signature information:
	 * <br>
	 * <table border="1" width="80%" cellpadding="5">
	 *	  <tr>
	 *	    <td width="20%"><code>TypeDeclaration</code></td>
	 *	    <td width="50%"><code>modifiers, isInterface, name, superclass,
	 *	      superInterfaces<br>
     *		  RELATIVE_ORDER property</code></td>
	 *	  </tr>
	 *	  <tr>
	 *	    <td width="20%"><code>FieldDeclaration</code></td>
	 *	    <td width="50%"><code>modifiers, type, fragments
	 *        (VariableDeclarationFragments
	 *	      with name only)<br>
     *		  RELATIVE_ORDER property</code></td>
	 *	  </tr>
	 *	  <tr>
	 *	    <td width="20%"><code>MethodDeclaration</code></td>
	 *	    <td width="50%"><code>modifiers, isConstructor, returnType, name,
	 *		  parameters
	 *	      (SingleVariableDeclarations with name and type only),
	 *		  thrownExceptions<br>
     *		  RELATIVE_ORDER property</code></td>
	 *	  </tr>
	 *	  <tr>
	 *	    <td width="20%"><code>Initializer</code></td>
	 *	    <td width="50%"><code>modifiers<br>
     *		  RELATIVE_ORDER property</code></td>
	 *	  </tr>
	 * </table>
	 * Clients should not rely on the AST nodes being properly parented or on
	 * having source range information. (Future releases may provide options
	 * for requesting additional information like source positions, full ASTs,
	 * non-recursive sorting, etc.)
	 * </p>
	 *
	 * @param compilationUnit the given compilation unit, which must be a 
	 * working copy
	 * @param positions an array of source positions to map, or 
	 * <code>null</code> if none. If supplied, the positions must 
	 * character-based source positions within the original source code for
	 * the given compilation unit, arranged in non-decreasing order.
	 * The array is updated in place when this method returns to reflect the
	 * corresponding source positions in the permuted source code string
	 * (but not necessarily any longer in non-decreasing order).
	 * @param comparator the comparator capable of ordering 
	 *   <code>BodyDeclaration</code>s
	 * @param options bitwise-or of option flags; <code>0</code> for default
	 * behavior (reserved for future growth)
	 * @param monitor the progress monitor to notify, or <code>null</code> if
	 * none
	 * @exception JavaModelException if the compilation unit could not be
	 * sorted. Reasons include:
	 * <ul>
	 * <li> The given compilation unit does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> The given compilation unit is not a working copy (INVALID_ELEMENT_TYPES)</li>
	 * <li> A <code>CoreException</code> occurred while accessing the underlying
	 * resource
	 * </ul>
	 * @exception IllegalArgumentException if the given compilation unit is null
	 * or if the given comparator is null.
	 * @see org.eclipse.jdt.core.dom.BodyDeclaration
	 * @see #RELATIVE_ORDER
	 */
	public static void sort(ICompilationUnit compilationUnit,
	        int[] positions,
	        Comparator comparator,
	        int options,
	        IProgressMonitor monitor) throws JavaModelException {
		if (compilationUnit == null || comparator == null) {
			throw new IllegalArgumentException();
		}
		ICompilationUnit[] compilationUnits = new ICompilationUnit[] { compilationUnit };
		SortElementsOperation operation = new SortElementsOperation(compilationUnits, positions, comparator);
		JavaElement.runOperation(operation, monitor);
	}
}
