/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.text.correction.proposals;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;

import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;
import org.eclipse.jdt.internal.ui.text.correction.ModifierCorrectionSubProcessor;

public class NewMethodCorrectionProposal extends AbstractMethodCorrectionProposal {

	private static final String KEY_NAME= "name"; //$NON-NLS-1$
	private static final String KEY_TYPE= "type"; //$NON-NLS-1$

	private List fArguments;

	//	invocationNode is MethodInvocation, ConstructorInvocation, SuperConstructorInvocation, ClassInstanceCreation, SuperMethodInvocation
	public NewMethodCorrectionProposal(String label, ICompilationUnit targetCU, ASTNode invocationNode,  List arguments, ITypeBinding binding, int relevance, Image image) {
		super(label, targetCU, invocationNode, binding, relevance, image);
		fArguments= arguments;
	}

	private int evaluateModifiers(ASTNode targetTypeDecl) {
		if (getSenderBinding().isAnnotation()) {
			return 0;
		}
		if (getSenderBinding().isInterface()) {
			// for interface and annotation members copy the modifiers from an existing field
			MethodDeclaration[] methodDecls= ((TypeDeclaration) targetTypeDecl).getMethods();
			if (methodDecls.length > 0) {
				return methodDecls[0].getModifiers();
			}
			return 0;
		}
		ASTNode invocationNode= getInvocationNode();
		if (invocationNode instanceof MethodInvocation) {
			int modifiers= 0;
			Expression expression= ((MethodInvocation)invocationNode).getExpression();
			if (expression != null) {
				if (expression instanceof Name && ((Name) expression).resolveBinding().getKind() == IBinding.TYPE) {
					modifiers |= Modifier.STATIC;
				}
			} else if (ASTResolving.isInStaticContext(invocationNode)) {
				modifiers |= Modifier.STATIC;
			}
			ASTNode node= ASTResolving.findParentType(invocationNode);
			if (targetTypeDecl.equals(node)) {
				modifiers |= Modifier.PRIVATE;
			} else if (node instanceof AnonymousClassDeclaration && ASTNodes.isParent(node, targetTypeDecl)) {
				modifiers |= Modifier.PROTECTED;
				if (ASTResolving.isInStaticContext(node)) {
					modifiers |= Modifier.STATIC;
				}
			} else {
				modifiers |= Modifier.PUBLIC;
			}
			return modifiers;
		}
		return Modifier.PUBLIC;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#addNewModifiers(org.eclipse.jdt.core.dom.rewrite.ASTRewrite, java.util.List)
	 */
	protected void addNewModifiers(ASTRewrite rewrite, ASTNode targetTypeDecl, List modifiers) {
		modifiers.addAll(rewrite.getAST().newModifiers(evaluateModifiers(targetTypeDecl)));
		ModifierCorrectionSubProcessor.installLinkedVisibilityProposals(getLinkedProposalModel(), rewrite, modifiers, getSenderBinding().isInterface());
	}

	/*(non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#isConstructor()
	 */
	protected boolean isConstructor() {
		ASTNode node= getInvocationNode();

		return node.getNodeType() != ASTNode.METHOD_INVOCATION && node.getNodeType() != ASTNode.SUPER_METHOD_INVOCATION;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#getNewName(org.eclipse.jdt.core.dom.rewrite.ASTRewrite)
	 */
	protected SimpleName getNewName(ASTRewrite rewrite) {
		ASTNode invocationNode= getInvocationNode();
		String name;
		if (invocationNode instanceof MethodInvocation) {
			name= ((MethodInvocation)invocationNode).getName().getIdentifier();
		} else if (invocationNode instanceof SuperMethodInvocation) {
			name= ((SuperMethodInvocation)invocationNode).getName().getIdentifier();
		} else {
			name= getSenderBinding().getName(); // name of the class
		}
		AST ast= rewrite.getAST();
		SimpleName newNameNode= ast.newSimpleName(name);
		addLinkedPosition(rewrite.track(newNameNode), false, KEY_NAME);

		ASTNode invocationName= getInvocationNameNode();
		if (invocationName != null && invocationName.getAST() == ast) { // in the same CU
			addLinkedPosition(rewrite.track(invocationName), true, KEY_NAME);
		}
		return newNameNode;
	}

	private ASTNode getInvocationNameNode() {
		ASTNode node= getInvocationNode();
		if (node instanceof MethodInvocation) {
			return ((MethodInvocation)node).getName();
		} else if (node instanceof SuperMethodInvocation) {
			return ((SuperMethodInvocation)node).getName();
		} else if (node instanceof ClassInstanceCreation) {
			Type type= ((ClassInstanceCreation)node).getType();
			while (type instanceof ParameterizedType) {
				type= ((ParameterizedType) type).getType();
			}
			return type;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#getNewMethodType(org.eclipse.jdt.core.dom.rewrite.ASTRewrite)
	 */
	protected Type getNewMethodType(ASTRewrite rewrite) throws CoreException {
		ASTNode node= getInvocationNode();
		AST ast= rewrite.getAST();

		Type newTypeNode= null;
		ITypeBinding[] otherProposals= null;

		if (node.getParent() instanceof MethodInvocation) {
			MethodInvocation parent= (MethodInvocation) node.getParent();
			if (parent.getExpression() == node) {
				ITypeBinding[] bindings= ASTResolving.getQualifierGuess(node.getRoot(), parent.getName().getIdentifier(), parent.arguments(), getSenderBinding());
				if (bindings.length > 0) {
					newTypeNode= getImportRewrite().addImport(bindings[0], ast);
					otherProposals= bindings;
				}
			}
		}
		if (newTypeNode == null) {
			ITypeBinding binding= ASTResolving.guessBindingForReference(node);
			if (binding != null && binding.isWildcardType()) {
				binding= ASTResolving.normalizeWildcardType(binding, false, ast);
			}
			if (binding != null) {
				newTypeNode= getImportRewrite().addImport(binding, ast);
			} else {
				ASTNode parent= node.getParent();
				if (parent instanceof ExpressionStatement) {
					return null;
				}
				newTypeNode= ASTResolving.guessTypeForReference(ast, node);
				if (newTypeNode == null) {
					newTypeNode= ast.newSimpleType(ast.newSimpleName("Object")); //$NON-NLS-1$
				}
			}
		}

		addLinkedPosition(rewrite.track(newTypeNode), false, KEY_TYPE);
		if (otherProposals != null) {
			for (int i= 0; i < otherProposals.length; i++) {
				addLinkedPositionProposal(KEY_TYPE, otherProposals[i]);
			}
		}

		return newTypeNode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#addNewParameters(org.eclipse.jdt.core.dom.rewrite.ASTRewrite, java.util.List, java.util.List)
	 */
	protected void addNewParameters(ASTRewrite rewrite, List takenNames, List params) throws CoreException {
		AST ast= rewrite.getAST();

		List arguments= fArguments;

		for (int i= 0; i < arguments.size(); i++) {
			Expression elem= (Expression) arguments.get(i);
			SingleVariableDeclaration param= ast.newSingleVariableDeclaration();

			// argument type
			String argTypeKey= "arg_type_" + i; //$NON-NLS-1$
			Type type= evaluateParameterType(ast, elem, argTypeKey);
			param.setType(type);

			// argument name
			String argNameKey= "arg_name_" + i; //$NON-NLS-1$
			String name= evaluateParameterName(takenNames, elem, type, argNameKey);
			param.setName(ast.newSimpleName(name));

			params.add(param);

			addLinkedPosition(rewrite.track(param.getType()), false, argTypeKey);
			addLinkedPosition(rewrite.track(param.getName()), false, argNameKey);
		}
	}

	private Type evaluateParameterType(AST ast, Expression elem, String key) {
		ITypeBinding binding= Bindings.normalizeTypeBinding(elem.resolveTypeBinding());
		if (binding != null && binding.isWildcardType()) {
			binding= ASTResolving.normalizeWildcardType(binding, true, ast);
		}
		if (binding != null) {
			ITypeBinding[] typeProposals= ASTResolving.getRelaxingTypes(ast, binding);
			for (int i= 0; i < typeProposals.length; i++) {
				addLinkedPositionProposal(key, typeProposals[i]);
			}
			return getImportRewrite().addImport(binding, ast);
		}
		return ast.newSimpleType(ast.newSimpleName("Object")); //$NON-NLS-1$
	}

	private String evaluateParameterName(List takenNames, Expression argNode, Type type, String key) {
		IJavaProject project= getCompilationUnit().getJavaProject();
		String[] names= StubUtility.getVariableNameSuggestions(StubUtility.PARAMETER, project, type, argNode, takenNames);
		for (int i= 0; i < names.length; i++) {
			addLinkedPositionProposal(key, names[i], null);
		}
		String favourite= names[0];
		takenNames.add(favourite);
		return favourite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#addNewExceptions(org.eclipse.jdt.core.dom.rewrite.ASTRewrite, java.util.List)
	 */
	protected void addNewExceptions(ASTRewrite rewrite, List exceptions) throws CoreException {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.correction.AbstractMethodCompletionProposal#addNewTypeParameters(org.eclipse.jdt.core.dom.rewrite.ASTRewrite, java.util.List, java.util.List)
	 */
	protected void addNewTypeParameters(ASTRewrite rewrite, List takenNames, List params) throws CoreException {
	}
}
