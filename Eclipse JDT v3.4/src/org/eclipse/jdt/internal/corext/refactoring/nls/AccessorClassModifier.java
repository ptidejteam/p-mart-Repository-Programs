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
package org.eclipse.jdt.internal.corext.refactoring.nls;


import com.ibm.icu.text.Collator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextChange;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import org.eclipse.jdt.internal.corext.dom.GenericVisitor;
import org.eclipse.jdt.internal.corext.refactoring.changes.CompilationUnitChange;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.SharedASTProvider;

import org.eclipse.jdt.internal.ui.IJavaStatusConstants;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;

public class AccessorClassModifier {

	private CompilationUnit fRoot;
	private AST fAst;
	private ASTRewrite fASTRewrite;
	private ListRewrite fListRewrite;
	private ICompilationUnit fCU;
	private List fFields;

	private AccessorClassModifier(ICompilationUnit cu) throws CoreException {

		fCU= cu;
		
		fRoot= SharedASTProvider.getAST(cu, SharedASTProvider.WAIT_YES, null);
		fAst= fRoot.getAST();
		fASTRewrite= ASTRewrite.create(fAst);
		
		AbstractTypeDeclaration parent= null;
		if (fRoot.types().size() > 0) {
			parent= (AbstractTypeDeclaration)fRoot.types().get(0);
			fFields= new ArrayList();
			parent.accept(new GenericVisitor() {
				/**
				 * {@inheritDoc}
				 */
				public boolean visit(FieldDeclaration node) {
					int modifiers= node.getModifiers();
					if (!Modifier.isPublic(modifiers))
						return false;
					
					if (!Modifier.isStatic(modifiers))
						return false;
					
					List fragments= node.fragments();
					if (fragments.size() != 1)
						return false;
					
					VariableDeclarationFragment fragment= (VariableDeclarationFragment)fragments.get(0);
					if (fragment.getInitializer() != null)
						return false;
					
					fFields.add(node);
					return false;
				}
			});
			fListRewrite= fASTRewrite.getListRewrite(parent, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
		} else {
			IStatus status= new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IJavaStatusConstants.INTERNAL_ERROR, NLSMessages.AccessorClassModifier_missingType, null); 
			throw new CoreException(status);
		}
	}
	
	private TextEdit getTextEdit() throws CoreException {
		IDocument document= null;
		
		ITextFileBufferManager manager= FileBuffers.getTextFileBufferManager();
		IPath path= fCU.getPath();
		
		if (manager != null && path != null) {
			manager.connect(path, LocationKind.NORMALIZE, null);
			try {
				ITextFileBuffer buffer= manager.getTextFileBuffer(path, LocationKind.NORMALIZE);
				if (buffer != null)
					document= buffer.getDocument();
			} finally {
				manager.disconnect(path, LocationKind.NORMALIZE, null);
			}
		}
		
		if (document == null)
			document= new Document(fCU.getSource());
		 
		return fASTRewrite.rewriteAST(document, fCU.getJavaProject().getOptions(true));
	}

	public static Change create(ICompilationUnit cu, NLSSubstitution[] substitutions) throws CoreException {
		
		Map newKeyToSubstMap= NLSPropertyFileModifier.getNewKeyToSubstitutionMap(substitutions);
		Map oldKeyToSubstMap= NLSPropertyFileModifier.getOldKeyToSubstitutionMap(substitutions);

		AccessorClassModifier sourceModification= new AccessorClassModifier(cu);

		String message= Messages.format(NLSMessages.NLSSourceModifier_change_description, BasicElementLabels.getFileName(cu)); 

		TextChange change= new CompilationUnitChange(message, cu);
		MultiTextEdit multiTextEdit= new MultiTextEdit();
		change.setEdit(multiTextEdit);
		
		for (int i= 0; i < substitutions.length; i++) {
			NLSSubstitution substitution= substitutions[i];
			if (NLSPropertyFileModifier.doRemove(substitution, newKeyToSubstMap, oldKeyToSubstMap)) {
				sourceModification.removeKey(substitution, change);
			}
		}
		for (int i= 0; i < substitutions.length; i++) {
			NLSSubstitution substitution= substitutions[i];
			if (substitution.isKeyRename() && NLSPropertyFileModifier.doReplace(substitution, newKeyToSubstMap, oldKeyToSubstMap)) {
				sourceModification.renameKey(substitution, change);
			}
		}
		for (int i= 0; i < substitutions.length; i++) {
			NLSSubstitution substitution= substitutions[i];
			if (NLSPropertyFileModifier.doInsert(substitution, newKeyToSubstMap, oldKeyToSubstMap)) {
				sourceModification.addKey(substitution, change);
			}
		}
		
		if (change.getChangeGroups().length == 0)
			return null;
		
		change.addEdit(sourceModification.getTextEdit());
		
		return change;
	}
	
	private void removeKey(NLSSubstitution sub, TextChange change) {
		ASTNode node= findField(fRoot, sub.getInitialKey());
		if (node == null)
			return;
		
		String name= Messages.format(NLSMessages.AccessorClassModifier_remove_entry, BasicElementLabels.getJavaElementName(sub.getKey())); 
		TextEditGroup editGroup= new TextEditGroup(name);
		fListRewrite.remove(node, editGroup);
		change.addTextEditGroup(editGroup);
		fFields.remove(node);
	}
	
	private void renameKey(NLSSubstitution sub, TextChange change) {
		ASTNode node= findField(fRoot, sub.getInitialKey());
		if (node == null)
			return;
		
		String name= Messages.format(NLSMessages.AccessorClassModifier_replace_entry, BasicElementLabels.getJavaElementName(sub.getKey())); 
		TextEditGroup editGroup= new TextEditGroup(name);
		fListRewrite.remove(node, editGroup);
		fFields.remove(node);
		
		addKey(sub, editGroup);
		
		change.addTextEditGroup(editGroup);
	}
	
	private ASTNode findField(ASTNode astRoot, final String name) {
		
		class STOP_VISITING extends RuntimeException {
			private static final long serialVersionUID= 1L;
		}
		
		final ASTNode[] result= new ASTNode[1];
		
		try {
			astRoot.accept(new ASTVisitor() {
				
				public boolean visit(VariableDeclarationFragment node) {
					if (name.equals(node.getName().getFullyQualifiedName())) {
						result[0]= node.getParent();
						throw new STOP_VISITING();
					}
					return true;	
				}
			});
		} catch (STOP_VISITING ex) {
			// stop visiting AST
		}
		
		return result[0];
	}
	
	private void addKey(NLSSubstitution sub, TextChange change) {		
		String name= Messages.format(NLSMessages.AccessorClassModifier_add_entry, BasicElementLabels.getJavaElementName(sub.getKey())); 
		TextEditGroup editGroup= new TextEditGroup(name);
		change.addTextEditGroup(editGroup);
		addKey(sub, editGroup);
	}
		
	private void addKey(NLSSubstitution sub, TextEditGroup editGroup) {	
		
		if (fListRewrite == null)
			return;
		
		String key= sub.getKey();
		FieldDeclaration fieldDeclaration= getNewFinalStringFieldDeclaration(key);

		if (fFields.size() == 0) {
			fListRewrite.insertLast(fieldDeclaration, editGroup);
			fFields.add(fieldDeclaration);
		} else {
			ArrayList identifiers= new ArrayList();
			for (Iterator iterator= fFields.iterator(); iterator.hasNext();) {
				FieldDeclaration field= (FieldDeclaration) iterator.next();
				VariableDeclarationFragment fragment= (VariableDeclarationFragment) field.fragments().get(0);
				identifiers.add(fragment.getName().getIdentifier());
			}
			
			int insertionPosition= NLSUtil.getInsertionPosition(key, identifiers);
			if (insertionPosition < 0) {
				fListRewrite.insertBefore(fieldDeclaration, (ASTNode) fFields.get(0), editGroup);
				fFields.add(0, fieldDeclaration);
			} else {
				if (identifiers.size() == insertionPosition + 1) {
					fListRewrite.insertAfter(fieldDeclaration, (ASTNode) fFields.get(insertionPosition), editGroup);
				} else {
					String beforeKey= (String) identifiers.get(insertionPosition);
					String afterKey= (String) identifiers.get(insertionPosition + 1);
					int distBefore= NLSUtil.invertDistance(key, beforeKey);
					int distAfter= NLSUtil.invertDistance(key, afterKey);
					if (distBefore > distAfter) {
						fListRewrite.insertAfter(fieldDeclaration, (ASTNode) fFields.get(insertionPosition), editGroup);
					} else if (distBefore == distAfter && Collator.getInstance().compare(beforeKey, afterKey) < 0) {
						fListRewrite.insertAfter(fieldDeclaration, (ASTNode) fFields.get(insertionPosition), editGroup);
					} else {
						fListRewrite.insertBefore(fieldDeclaration, (ASTNode) fFields.get(insertionPosition + 1), editGroup);
					}
				}
				fFields.add(insertionPosition + 1, fieldDeclaration);
			}
		}
	}

	private FieldDeclaration getNewFinalStringFieldDeclaration(String name) {
		VariableDeclarationFragment variableDeclarationFragment= fAst.newVariableDeclarationFragment();
		variableDeclarationFragment.setName(fAst.newSimpleName(name));
		
		FieldDeclaration fieldDeclaration= fAst.newFieldDeclaration(variableDeclarationFragment);
		fieldDeclaration.setType(fAst.newSimpleType(fAst.newSimpleName("String"))); //$NON-NLS-1$
		fieldDeclaration.modifiers().add(fAst.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		fieldDeclaration.modifiers().add(fAst.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		
		return fieldDeclaration;
	}

}
