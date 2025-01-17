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
package org.eclipse.jdt.internal.corext.refactoring.structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;

import org.eclipse.jdt.internal.corext.codemanipulation.ImportReferencesCollector;

/**
 * Utility methods to manage static and non-static imports of a compilation unit.
 * 
 * @since 3.1
 */
public final class ImportRewriteUtil {
	
	/**
	 * Adds the necessary imports for an AST node to the specified compilation unit.
	 * 
	 * @param rewrite the compilation unit rewrite whose compilation unit's imports should be updated
	 * @param node the AST node specifying the element for which imports should be added
	 * @param typeImports the map of name nodes to strings (element type: Map <Name, String>).
	 * @param staticImports the map of name nodes to strings (element type: Map <Name, String>).
	 * @param declarations <code>true</code> if method declarations are treated as abstract, <code>false</code> otherwise
	 */
	public static void addImports(final CompilationUnitRewrite rewrite, final ASTNode node, final Map typeImports, final Map staticImports, final boolean declarations) {
		addImports(rewrite, node, typeImports, staticImports, null, declarations);
	}

	/**
	 * Adds the necessary imports for an AST node to the specified compilation unit.
	 * 
	 * @param rewrite the compilation unit rewrite whose compilation unit's imports should be updated
	 * @param node the AST node specifying the element for which imports should be added
	 * @param typeImports the map of name nodes to strings (element type: Map <Name, String>).
	 * @param staticImports the map of name nodes to strings (element type: Map <Name, String>).
	 * @param excludeBindings the set of bindings to exclude (element type: Set <IBinding>).
	 * @param declarations <code>true</code> if method declarations are treated as abstract, <code>false</code> otherwise
	 */
	public static void addImports(final CompilationUnitRewrite rewrite, final ASTNode node, final Map typeImports, final Map staticImports, final Collection excludeBindings, final boolean declarations) {
		Assert.isNotNull(rewrite);
		Assert.isNotNull(node);
		Assert.isNotNull(typeImports);
		Assert.isNotNull(staticImports);
		final Set types= new HashSet();
		final Set members= new HashSet();
		
		ImportReferencesCollector.collect(node, rewrite.getCu().getJavaProject(), null, declarations, types, members);

		final ImportRewrite rewriter= rewrite.getImportRewrite();
		final ImportRemover remover= rewrite.getImportRemover();
		Name name= null;
		IBinding binding= null;
		for (final Iterator iterator= types.iterator(); iterator.hasNext();) {
			name= (Name) iterator.next();
			binding= name.resolveBinding();
			if (binding instanceof ITypeBinding) {
				final ITypeBinding type= (ITypeBinding) binding;
				if (excludeBindings == null || !excludeBindings.contains(type)) {
					typeImports.put(name, rewriter.addImport(type));
					remover.registerAddedImport(((SimpleName)name).getIdentifier());
				}
			}
		}
		for (final Iterator iterator= members.iterator(); iterator.hasNext();) {
			name= (Name) iterator.next();
			binding= name.resolveBinding();
			if (binding instanceof IVariableBinding) {
				final IVariableBinding variable= (IVariableBinding) binding;
				final ITypeBinding declaring= variable.getDeclaringClass();
				if (declaring != null && (excludeBindings == null || !excludeBindings.contains(variable))) {
					staticImports.put(name, rewriter.addStaticImport(variable));
					remover.registerAddedStaticImport(declaring.getQualifiedName(), variable.getName(), true);
				}
			} else if (binding instanceof IMethodBinding) {
				final IMethodBinding method= (IMethodBinding) binding;
				final ITypeBinding declaring= method.getDeclaringClass();
				if (declaring != null && (excludeBindings == null || !excludeBindings.contains(method))) {
					staticImports.put(name, rewriter.addStaticImport(method));
					remover.registerAddedStaticImport(declaring.getQualifiedName(), method.getName(), false);
				}
			}
		}
	}

	/**
	 * Collects the necessary imports for an element represented by the specified AST node.
	 * 
	 * @param project the java project containing the element
	 * @param node the AST node specifying the element for which imports should be collected
	 * @param typeBindings the set of type bindings (element type: Set <ITypeBinding>).
	 * @param staticBindings the set of bindings (element type: Set <IBinding>).
	 * @param declarations <code>true</code> if method declarations are treated as abstract, <code>false</code> otherwise
	 */
	public static void collectImports(final IJavaProject project, final ASTNode node, final Collection typeBindings, final Collection staticBindings, final boolean declarations) {
		collectImports(project, node, typeBindings, staticBindings, null, declarations);
	}

	/**
	 * Collects the necessary imports for an element represented by the specified AST node.
	 * 
	 * @param project the java project containing the element
	 * @param node the AST node specifying the element for which imports should be collected
	 * @param typeBindings the set of type bindings (element type: Set <ITypeBinding>).
	 * @param staticBindings the set of bindings (element type: Set <IBinding>).
	 * @param excludeBindings the set of bindings to exclude (element type: Set <IBinding>).
	 * @param declarations <code>true</code> if method declarations are treated as abstract, <code>false</code> otherwise
	 */
	public static void collectImports(final IJavaProject project, final ASTNode node, final Collection typeBindings, final Collection staticBindings, final Collection excludeBindings, final boolean declarations) {
		Assert.isNotNull(project);
		Assert.isNotNull(node);
		Assert.isNotNull(typeBindings);
		Assert.isNotNull(staticBindings);
		final Set types= new HashSet();
		final Set members= new HashSet();
		
		ImportReferencesCollector.collect(node, project, null, declarations, types, members);
		
		Name name= null;
		IBinding binding= null;
		for (final Iterator iterator= types.iterator(); iterator.hasNext();) {
			name= (Name) iterator.next();
			binding= name.resolveBinding();
			if (binding instanceof ITypeBinding) {
				final ITypeBinding type= (ITypeBinding) binding;
				if (excludeBindings == null || !excludeBindings.contains(type))
					typeBindings.add(type);
			}
		}
		for (final Iterator iterator= members.iterator(); iterator.hasNext();) {
			name= (Name) iterator.next();
			binding= name.resolveBinding();
			if (binding != null && (excludeBindings == null || !excludeBindings.contains(binding)))
				staticBindings.add(binding);
		}
	}

	private ImportRewriteUtil() {
		// Not for instantiation
	}
}
