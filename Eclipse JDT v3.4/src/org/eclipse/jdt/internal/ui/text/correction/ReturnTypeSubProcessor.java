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
package org.eclipse.jdt.internal.ui.text.correction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;

import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IProblemLocation;

import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.text.correction.proposals.ASTRewriteCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.MissingReturnTypeCorrectionProposal;
import org.eclipse.jdt.internal.ui.text.correction.proposals.ReplaceCorrectionProposal;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.viewsupport.BindingLabelProvider;

/**
  */
public class ReturnTypeSubProcessor {

	private static class ReturnStatementCollector extends ASTVisitor {
		private ArrayList fResult= new ArrayList();

		public Iterator returnStatements() {
			return fResult.iterator();
		}

		public ITypeBinding getTypeBinding(AST ast) {
			boolean couldBeObject= false;
			for (int i= 0; i < fResult.size(); i++) {
				ReturnStatement node= (ReturnStatement) fResult.get(i);
				Expression expr= node.getExpression();
				if (expr != null) {
					ITypeBinding binding= Bindings.normalizeTypeBinding(expr.resolveTypeBinding());
					if (binding != null) {
						return binding;
					} else {
						couldBeObject= true;
					}
				} else {
					return ast.resolveWellKnownType("void"); //$NON-NLS-1$
				}
			}
			if (couldBeObject) {
				return ast.resolveWellKnownType("java.lang.Object"); //$NON-NLS-1$
			}
			return ast.resolveWellKnownType("void"); //$NON-NLS-1$
		}

		public boolean visit(ReturnStatement node) {
			fResult.add(node);
			return false;
		}

		public boolean visit(AnonymousClassDeclaration node) {
			return false;
		}

		public boolean visit(TypeDeclaration node) {
			return false;
		}

		public boolean visit(EnumDeclaration node) {
			return false;
		}

		public boolean visit(AnnotationTypeDeclaration node) {
			return false;
		}

	}


	public static void addMethodWithConstrNameProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) {
		ICompilationUnit cu= context.getCompilationUnit();

		ASTNode selectedNode= problem.getCoveringNode(context.getASTRoot());
		if (selectedNode instanceof MethodDeclaration) {
			MethodDeclaration declaration= (MethodDeclaration) selectedNode;

			ASTRewrite rewrite= ASTRewrite.create(declaration.getAST());
			rewrite.set(declaration, MethodDeclaration.CONSTRUCTOR_PROPERTY, Boolean.TRUE, null);

			String label= CorrectionMessages.ReturnTypeSubProcessor_constrnamemethod_description;
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
			ASTRewriteCorrectionProposal proposal= new ASTRewriteCorrectionProposal(label, cu, rewrite, 5, image);
			proposals.add(proposal);
		}

	}

	public static void addVoidMethodReturnsProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) {
		ICompilationUnit cu= context.getCompilationUnit();

		CompilationUnit astRoot= context.getASTRoot();
		ASTNode selectedNode= problem.getCoveringNode(astRoot);
		if (selectedNode == null) {
			return;
		}

		BodyDeclaration decl= ASTResolving.findParentBodyDeclaration(selectedNode);
		if (decl instanceof MethodDeclaration && selectedNode.getNodeType() == ASTNode.RETURN_STATEMENT) {
			ReturnStatement returnStatement= (ReturnStatement) selectedNode;
			Expression expr= returnStatement.getExpression();
			if (expr != null) {
				AST ast= astRoot.getAST();
				
				ITypeBinding binding= Bindings.normalizeTypeBinding(expr.resolveTypeBinding());
				if (binding == null) {
					binding= ast.resolveWellKnownType("java.lang.Object"); //$NON-NLS-1$
				}
				if (binding.isWildcardType()) {
					binding= ASTResolving.normalizeWildcardType(binding, true, ast);
				}
				
				MethodDeclaration methodDeclaration= (MethodDeclaration) decl;

				ASTRewrite rewrite= ASTRewrite.create(ast);

				String label= Messages.format(CorrectionMessages.ReturnTypeSubProcessor_voidmethodreturns_description, BindingLabelProvider.getBindingLabel(binding, BindingLabelProvider.DEFAULT_TEXTFLAGS));
				Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
				LinkedCorrectionProposal proposal= new LinkedCorrectionProposal(label, cu, rewrite, 6, image);
				ImportRewrite imports= proposal.createImportRewrite(astRoot);
				Type newReturnType= imports.addImport(binding, ast);

				if (methodDeclaration.isConstructor()) {
					rewrite.set(methodDeclaration, MethodDeclaration.CONSTRUCTOR_PROPERTY, Boolean.FALSE, null);
					rewrite.set(methodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, newReturnType, null);
				} else {
					rewrite.replace(methodDeclaration.getReturnType2(), newReturnType, null);
				}
				String key= "return_type"; //$NON-NLS-1$
				proposal.addLinkedPosition(rewrite.track(newReturnType), true, key);
				ITypeBinding[] bindings= ASTResolving.getRelaxingTypes(ast, binding);
				for (int i= 0; i < bindings.length; i++) {
					proposal.addLinkedPositionProposal(key, bindings[i]);
				}

				Javadoc javadoc= methodDeclaration.getJavadoc();
				if (javadoc != null) {
					TagElement newTag= ast.newTagElement();
					newTag.setTagName(TagElement.TAG_RETURN);
					TextElement commentStart= ast.newTextElement();
					newTag.fragments().add(commentStart);

					JavadocTagsSubProcessor.insertTag(rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY), newTag, null);
					proposal.addLinkedPosition(rewrite.track(commentStart), false, "comment_start"); //$NON-NLS-1$

				}
				proposals.add(proposal);
			}
			ASTRewrite rewrite= ASTRewrite.create(decl.getAST());
			rewrite.remove(returnStatement.getExpression(), null);

			String label= CorrectionMessages.ReturnTypeSubProcessor_removereturn_description;
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
			ASTRewriteCorrectionProposal proposal= new ASTRewriteCorrectionProposal(label, cu, rewrite, 5, image);
			proposals.add(proposal);
		}
	}



	public static void addMissingReturnTypeProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) {
		ICompilationUnit cu= context.getCompilationUnit();

		CompilationUnit astRoot= context.getASTRoot();
		ASTNode selectedNode= problem.getCoveringNode(astRoot);
		if (selectedNode == null) {
			return;
		}
		BodyDeclaration decl= ASTResolving.findParentBodyDeclaration(selectedNode);
		if (decl instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration= (MethodDeclaration) decl;

			ReturnStatementCollector eval= new ReturnStatementCollector();
			decl.accept(eval);

			AST ast= astRoot.getAST();
			
			ITypeBinding typeBinding= eval.getTypeBinding(decl.getAST());
			typeBinding= Bindings.normalizeTypeBinding(typeBinding);
			if (typeBinding == null) {
				typeBinding= ast.resolveWellKnownType("void"); //$NON-NLS-1$
			}
			if (typeBinding.isWildcardType()) {
				typeBinding= ASTResolving.normalizeWildcardType(typeBinding, true, ast);
			}
			
			ASTRewrite rewrite= ASTRewrite.create(ast);

			String label= Messages.format(CorrectionMessages.ReturnTypeSubProcessor_missingreturntype_description, BindingLabelProvider.getBindingLabel(typeBinding, BindingLabelProvider.DEFAULT_TEXTFLAGS));
			Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
			LinkedCorrectionProposal proposal= new LinkedCorrectionProposal(label, cu, rewrite, 6, image);
			
			ImportRewrite imports= proposal.createImportRewrite(astRoot);

			Type type= imports.addImport(typeBinding, ast);

			rewrite.set(methodDeclaration, MethodDeclaration.RETURN_TYPE2_PROPERTY, type, null);
			rewrite.set(methodDeclaration, MethodDeclaration.CONSTRUCTOR_PROPERTY, Boolean.FALSE, null);

			Javadoc javadoc= methodDeclaration.getJavadoc();
			if (javadoc != null && typeBinding != null) {
				TagElement newTag= ast.newTagElement();
				newTag.setTagName(TagElement.TAG_RETURN);
				TextElement commentStart= ast.newTextElement();
				newTag.fragments().add(commentStart);

				JavadocTagsSubProcessor.insertTag(rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY), newTag, null);
				proposal.addLinkedPosition(rewrite.track(commentStart), false, "comment_start"); //$NON-NLS-1$
			}

			String key= "return_type"; //$NON-NLS-1$
			proposal.addLinkedPosition(rewrite.track(type), true, key);
			if (typeBinding != null) {
				ITypeBinding[] bindings= ASTResolving.getRelaxingTypes(ast, typeBinding);
				for (int i= 0; i < bindings.length; i++) {
					proposal.addLinkedPositionProposal(key, bindings[i]);
				}
			}

			proposals.add(proposal);

			// change to constructor
			ASTNode parentType= ASTResolving.findParentType(decl);
			if (parentType instanceof AbstractTypeDeclaration) {
				boolean isInterface= parentType instanceof TypeDeclaration && ((TypeDeclaration) parentType).isInterface();
				if (!isInterface) {
					String constructorName= ((TypeDeclaration) parentType).getName().getIdentifier();
					ASTNode nameNode= methodDeclaration.getName();
					label= Messages.format(CorrectionMessages.ReturnTypeSubProcessor_wrongconstructorname_description, BasicElementLabels.getJavaElementName(constructorName));
					proposals.add(new ReplaceCorrectionProposal(label, cu, nameNode.getStartPosition(), nameNode.getLength(), constructorName, 5));
				}
			}
		}
	}

	public static void addMissingReturnStatementProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) {
		ICompilationUnit cu= context.getCompilationUnit();

		ASTNode selectedNode= problem.getCoveringNode(context.getASTRoot());
		if (selectedNode == null) {
			return;
		}
		BodyDeclaration decl= ASTResolving.findParentBodyDeclaration(selectedNode);
		if (decl instanceof MethodDeclaration) {
			MethodDeclaration methodDecl= (MethodDeclaration) decl;
			Block block= methodDecl.getBody();
			if (block == null) {
				return;
			}
			ReturnStatement existingStatement= (selectedNode instanceof ReturnStatement) ? (ReturnStatement) selectedNode : null;
			proposals.add( new MissingReturnTypeCorrectionProposal(cu, methodDecl, existingStatement, 6));

			Type returnType= methodDecl.getReturnType2();
			if (returnType != null && !"void".equals(ASTNodes.asString(returnType))) { //$NON-NLS-1$
				AST ast= methodDecl.getAST();
				ASTRewrite rewrite= ASTRewrite.create(ast);
				rewrite.replace(returnType, ast.newPrimitiveType(PrimitiveType.VOID), null);
				Javadoc javadoc= methodDecl.getJavadoc();
				if (javadoc != null) {
					TagElement tagElement= JavadocTagsSubProcessor.findTag(javadoc, TagElement.TAG_RETURN, null);
					if (tagElement != null) {
						rewrite.remove(tagElement, null);
					}
				}

				String label= CorrectionMessages.ReturnTypeSubProcessor_changetovoid_description;
				Image image= JavaPluginImages.get(JavaPluginImages.IMG_CORRECTION_CHANGE);
				ASTRewriteCorrectionProposal proposal= new ASTRewriteCorrectionProposal(label, cu, rewrite, 5, image);
				proposals.add(proposal);
			}
		}
	}

	public static void addMethodRetunsVoidProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) throws JavaModelException {
		CompilationUnit astRoot= context.getASTRoot();
		ASTNode selectedNode= problem.getCoveringNode(astRoot);
		if (!(selectedNode instanceof ReturnStatement)) {
			return;
		}
		ReturnStatement returnStatement= (ReturnStatement) selectedNode;
		Expression expression= returnStatement.getExpression();
		if (expression == null) {
			return;
		}
		BodyDeclaration decl= ASTResolving.findParentBodyDeclaration(selectedNode);
		if (decl instanceof MethodDeclaration) {
			MethodDeclaration methDecl= (MethodDeclaration) decl;
			Type retType= methDecl.getReturnType2();
			if (retType == null || retType.resolveBinding() == null) {
				return;
			}
			TypeMismatchSubProcessor.addChangeSenderTypeProposals(context, expression, retType.resolveBinding(), false, 4, proposals);
		}
	}
}
