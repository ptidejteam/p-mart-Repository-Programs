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
package org.eclipse.jdt.internal.corext.refactoring.rename;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.text.edits.TextEdit;

import org.eclipse.jface.text.IRegion;

import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusContext;
import org.eclipse.ltk.core.refactoring.TextChange;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclaration;

import org.eclipse.jdt.internal.corext.SourceRange;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.base.JavaStringStatusContext;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;

public class RefactoringAnalyzeUtil {
	
	private RefactoringAnalyzeUtil() {
		//no instances
	}
	
	public static IRegion[] getNewRanges(TextEdit[] edits, TextChange change){
		IRegion[] result= new IRegion[edits.length];
		for (int i= 0; i < edits.length; i++) {
			result[i]= RefactoringAnalyzeUtil.getNewTextRange(edits[i], change);
		}
		return result;
	}

	public static RefactoringStatus reportProblemNodes(String modifiedWorkingCopySource, SimpleName[] problemNodes) {
		RefactoringStatus result= new RefactoringStatus();
		for (int i= 0; i < problemNodes.length; i++) {
			RefactoringStatusContext context= new JavaStringStatusContext(modifiedWorkingCopySource, new SourceRange(problemNodes[i]));
			result.addError(Messages.format(RefactoringCoreMessages.RefactoringAnalyzeUtil_name_collision, BasicElementLabels.getJavaElementName(problemNodes[i].getIdentifier())), context); 
		}
		return result;
	}
	

	public static MethodDeclaration getMethodDeclaration(TextEdit edit, TextChange change, CompilationUnit cuNode){
		ASTNode decl= RefactoringAnalyzeUtil.findSimpleNameNode(RefactoringAnalyzeUtil.getNewTextRange(edit, change), cuNode);
		return ((MethodDeclaration)ASTNodes.getParent(decl, MethodDeclaration.class));
	}

	public static Block getBlock(TextEdit edit, TextChange change, CompilationUnit cuNode){
		ASTNode decl= RefactoringAnalyzeUtil.findSimpleNameNode(RefactoringAnalyzeUtil.getNewTextRange(edit, change), cuNode);
		return ((Block)ASTNodes.getParent(decl, Block.class));
	}
	
	public static IProblem[] getIntroducedCompileProblems(CompilationUnit newCUNode, CompilationUnit oldCuNode) {
		Set subResult= new HashSet();				
		Set oldProblems= getOldProblems(oldCuNode);
		IProblem[] newProblems= ASTNodes.getProblems(newCUNode, ASTNodes.INCLUDE_ALL_PARENTS, ASTNodes.PROBLEMS);
		for (int i= 0; i < newProblems.length; i++) {
			IProblem correspondingOld= findCorrespondingProblem(oldProblems, newProblems[i]);
			if (correspondingOld == null)
				subResult.add(newProblems[i]);
		}
		return (IProblem[]) subResult.toArray(new IProblem[subResult.size()]);
	}
	
	public static IRegion getNewTextRange(TextEdit edit, TextChange change){
		return change.getPreviewEdit(edit).getRegion();
	}
	
	private static IProblem findCorrespondingProblem(Set oldProblems, IProblem iProblem) {
		for (Iterator iter= oldProblems.iterator(); iter.hasNext();) {
			IProblem oldProblem= (IProblem) iter.next();
			if (isCorresponding(oldProblem, iProblem))
				return oldProblem;
		}
		return null;
	}
	
	private static boolean isCorresponding(IProblem oldProblem, IProblem iProblem) {
		if (oldProblem.getID() != iProblem.getID())		
			return false;
		if (! oldProblem.getMessage().equals(iProblem.getMessage()))	
			return false;
		return true;
	}

	private static SimpleName getSimpleName(ASTNode node){
		if (node instanceof SimpleName)
			return (SimpleName)node;
		if (node instanceof VariableDeclaration)
			return ((VariableDeclaration)node).getName();
		return null;	
	}

	private static SimpleName findSimpleNameNode(IRegion range, CompilationUnit cuNode) {
		ASTNode node= NodeFinder.perform(cuNode, range.getOffset(), range.getLength());
		return getSimpleName(node);
	}

	private static Set getOldProblems(CompilationUnit oldCuNode) {
		return new HashSet(Arrays.asList(ASTNodes.getProblems(oldCuNode, ASTNodes.INCLUDE_ALL_PARENTS, ASTNodes.PROBLEMS)));
	}
}
