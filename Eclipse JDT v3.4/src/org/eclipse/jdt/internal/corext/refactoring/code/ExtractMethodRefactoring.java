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
package org.eclipse.jdt.internal.corext.refactoring.code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.eclipse.core.resources.IFile;

import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.refactoring.IJavaRefactorings;
import org.eclipse.jdt.core.refactoring.descriptors.ExtractMethodDescriptor;
import org.eclipse.jdt.core.refactoring.descriptors.JavaRefactoringDescriptor;

import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.dom.BodyDeclarationRewrite;
import org.eclipse.jdt.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.internal.corext.dom.Selection;
import org.eclipse.jdt.internal.corext.dom.StatementRewrite;
import org.eclipse.jdt.internal.corext.fix.LinkedProposalModel;
import org.eclipse.jdt.internal.corext.fix.LinkedProposalPositionGroup;
import org.eclipse.jdt.internal.corext.refactoring.Checks;
import org.eclipse.jdt.internal.corext.refactoring.JDTRefactoringDescriptorComment;
import org.eclipse.jdt.internal.corext.refactoring.JavaRefactoringArguments;
import org.eclipse.jdt.internal.corext.refactoring.JavaRefactoringDescriptorUtil;
import org.eclipse.jdt.internal.corext.refactoring.ParameterInfo;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.jdt.internal.corext.refactoring.changes.CompilationUnitChange;
import org.eclipse.jdt.internal.corext.refactoring.util.RefactoringASTParser;
import org.eclipse.jdt.internal.corext.refactoring.util.ResourceUtil;
import org.eclipse.jdt.internal.corext.refactoring.util.SelectionAwareSourceRangeComputer;
import org.eclipse.jdt.internal.corext.util.JdtFlags;
import org.eclipse.jdt.internal.corext.util.Messages;

import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.JavaElementLabels;

import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;
import org.eclipse.jdt.internal.ui.text.correction.ModifierCorrectionSubProcessor;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.viewsupport.BindingLabelProvider;

/**
 * Extracts a method in a compilation unit based on a text selection range.
 */
public class ExtractMethodRefactoring extends Refactoring {

	private static final String ATTRIBUTE_VISIBILITY= "visibility"; //$NON-NLS-1$
	private static final String ATTRIBUTE_DESTINATION= "destination"; //$NON-NLS-1$
	private static final String ATTRIBUTE_COMMENTS= "comments"; //$NON-NLS-1$
	private static final String ATTRIBUTE_REPLACE= "replace"; //$NON-NLS-1$
	private static final String ATTRIBUTE_EXCEPTIONS= "exceptions"; //$NON-NLS-1$

	private ICompilationUnit fCUnit;
	private CompilationUnit fRoot;
	private ImportRewrite fImportRewriter;
	private int fSelectionStart;
	private int fSelectionLength;
	private AST fAST;
	private ASTRewrite fRewriter;
	private ExtractMethodAnalyzer fAnalyzer;
	private int fVisibility;
	private String fMethodName;
	private boolean fThrowRuntimeExceptions;
	private List fParameterInfos;
	private Set fUsedNames;
	private boolean fGenerateJavadoc;
	private boolean fReplaceDuplicates;
	private SnippetFinder.Match[] fDuplicates;
	private int fDestinationIndex= 0;
	// either of type TypeDeclaration or AnonymousClassDeclaration
	private ASTNode fDestination;
	// either of type TypeDeclaration or AnonymousClassDeclaration
	private ASTNode[] fDestinations;
	private LinkedProposalModel fLinkedProposalModel;

	private static final String EMPTY= ""; //$NON-NLS-1$
	
	private static final String KEY_TYPE= "type"; //$NON-NLS-1$
	private static final String KEY_NAME= "name"; //$NON-NLS-1$
	
	private static class UsedNamesCollector extends ASTVisitor {
		private Set result= new HashSet();
		private Set fIgnore= new HashSet();
		public static Set perform(ASTNode[] nodes) {
			UsedNamesCollector collector= new UsedNamesCollector();
			for (int i= 0; i < nodes.length; i++) {
				nodes[i].accept(collector);
			}
			return collector.result;
		}
		public boolean visit(FieldAccess node) {
			Expression exp= node.getExpression();
			if (exp != null)
				fIgnore.add(node.getName());
			return true;
		}
		public void endVisit(FieldAccess node) {
			fIgnore.remove(node.getName());
		}
		public boolean visit(MethodInvocation node) {
			Expression exp= node.getExpression();
			if (exp != null)
				fIgnore.add(node.getName());
			return true;
		}
		public void endVisit(MethodInvocation node) {
			fIgnore.remove(node.getName());
		}
		public boolean visit(QualifiedName node) {
			fIgnore.add(node.getName());
			return true;
		}
		public void endVisit(QualifiedName node) {
			fIgnore.remove(node.getName());
		}
		public boolean visit(SimpleName node) {
			if (!fIgnore.contains(node))
				result.add(node.getIdentifier());
			return true;
		}
		public boolean visit(TypeDeclaration node) {
			return visitType(node);
		}
		public boolean visit(AnnotationTypeDeclaration node) {
			return visitType(node);
		}
		public boolean visit(EnumDeclaration node) {
			return visitType(node);
		}
		private boolean visitType(AbstractTypeDeclaration node) {
			result.add(node.getName().getIdentifier());
			// don't dive into type declaration since they open a new
			// context.
			return false;
		}
	}

	/**
	 * Creates a new extract method refactoring
	 * @param unit the compilation unit, or <code>null</code> if invoked by scripting
	 * @param selectionStart
	 * @param selectionLength
	 */
	public ExtractMethodRefactoring(ICompilationUnit unit, int selectionStart, int selectionLength) {
		fCUnit= unit;
		fRoot= null;
		fMethodName= "extracted"; //$NON-NLS-1$
		fSelectionStart= selectionStart;
		fSelectionLength= selectionLength;
		fVisibility= -1;
	}
	
    public ExtractMethodRefactoring(JavaRefactoringArguments arguments, RefactoringStatus status) {
   		this((ICompilationUnit) null, 0, 0);
   		RefactoringStatus initializeStatus= initialize(arguments);
   		status.merge(initializeStatus);
    }
	
	/**
	 * Creates a new extract method refactoring
	 * @param astRoot the AST root of an AST created from a compilation unit
	 * @param selectionStart
	 * @param selectionLength
	 */
	public ExtractMethodRefactoring(CompilationUnit astRoot, int selectionStart, int selectionLength) {
		this((ICompilationUnit) astRoot.getTypeRoot(), selectionStart, selectionLength);
		fRoot= astRoot;
	}
	
	public void setLinkedProposalModel(LinkedProposalModel linkedProposalModel) {
		fLinkedProposalModel= linkedProposalModel;
	}

	 public String getName() {
	 	return RefactoringCoreMessages.ExtractMethodRefactoring_name; 
	 }

	/**
	 * Checks if the refactoring can be activated. Activation typically means, if a
	 * corresponding menu entry can be added to the UI.
	 *
	 * @param pm a progress monitor to report progress during activation checking.
	 * @return the refactoring status describing the result of the activation check.	 
	 * @throws CoreException 
	 */
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException {
		RefactoringStatus result= new RefactoringStatus();
		pm.beginTask("", 100); //$NON-NLS-1$
		
		if (fSelectionStart < 0 || fSelectionLength == 0)
			return mergeTextSelectionStatus(result);
		
		IFile[] changedFiles= ResourceUtil.getFiles(new ICompilationUnit[]{fCUnit});
		result.merge(Checks.validateModifiesFiles(changedFiles, getValidationContext()));
		if (result.hasFatalError())
			return result;
		result.merge(ResourceChangeChecker.checkFilesToBeChanged(changedFiles, new SubProgressMonitor(pm, 1)));
		
		if (fRoot == null) {
			fRoot= RefactoringASTParser.parseWithASTProvider(fCUnit, true, new SubProgressMonitor(pm, 99));
		}
		fImportRewriter= StubUtility.createImportRewrite(fRoot, true);

		fAST= fRoot.getAST();
		fRoot.accept(createVisitor());
		
		result.merge(fAnalyzer.checkInitialConditions(fImportRewriter));
		if (result.hasFatalError())
			return result;
		if (fVisibility == -1) {
			setVisibility(Modifier.PRIVATE);
		}
		initializeParameterInfos();
		initializeUsedNames();
		initializeDuplicates();
		initializeDestinations();
		return result;
	}
	
	private ASTVisitor createVisitor() throws CoreException {
		fAnalyzer= new ExtractMethodAnalyzer(fCUnit, Selection.createFromStartLength(fSelectionStart, fSelectionLength));
		return fAnalyzer;
	}
	
	/**
	 * Sets the method name to be used for the extracted method.
	 *
	 * @param name the new method name.
	 */	
	public void setMethodName(String name) {
		fMethodName= name;
	}
	
	/**
	 * Returns the method name to be used for the extracted method.
	 * @return the method name to be used for the extracted method.
	 */
	public String getMethodName() {
		return fMethodName;
	} 
	
	/**
	 * Sets the visibility of the new method.
	 * 
	 * @param visibility the visibility of the new method. Valid values are
	 *  "public", "protected", "", and "private"
	 */
	public void setVisibility(int visibility) {
		fVisibility= visibility;
	}
	
	/**
	 * Returns the visibility of the new method.
	 * 
	 * @return the visibility of the new method
	 */
	public int getVisibility() {
		return fVisibility;
	}
	
	/**
	 * Returns the parameter infos.
	 * @return a list of parameter infos.
	 */
	public List getParameterInfos() {
		return fParameterInfos;
	}
	
	/**
	 * Sets whether the new method signature throws runtime exceptions.
	 * 
	 * @param throwRuntimeExceptions flag indicating if the new method
	 * 	throws runtime exceptions
	 */
	public void setThrowRuntimeExceptions(boolean throwRuntimeExceptions) {
		fThrowRuntimeExceptions= throwRuntimeExceptions;
	}
	
	/**
	 * Checks if the new method name is a valid method name. This method doesn't
	 * check if a method with the same name already exists in the hierarchy. This
	 * check is done in <code>checkInput</code> since it is expensive.
	 * @return validation status
	 */
	public RefactoringStatus checkMethodName() {
		return Checks.checkMethodName(fMethodName, fCUnit);
	}
	
	public ASTNode[] getDestinations() {
		return fDestinations;
	}
	
	public void setDestination(int index) {
		fDestination= fDestinations[index];
		fDestinationIndex= index;
	}
	
	/**
	 * Checks if the parameter names are valid.
	 * @return validation status
	 */
	public RefactoringStatus checkParameterNames() {
		RefactoringStatus result= new RefactoringStatus();
		for (Iterator iter= fParameterInfos.iterator(); iter.hasNext();) {
			ParameterInfo parameter= (ParameterInfo)iter.next();
			result.merge(Checks.checkIdentifier(parameter.getNewName(), fCUnit));
			for (Iterator others= fParameterInfos.iterator(); others.hasNext();) {
				ParameterInfo other= (ParameterInfo) others.next();
				if (parameter != other && other.getNewName().equals(parameter.getNewName())) {
					result.addError(Messages.format(
						RefactoringCoreMessages.ExtractMethodRefactoring_error_sameParameter, 
						BasicElementLabels.getJavaElementName(other.getNewName())));
					return result;
				}
			}
			if (parameter.isRenamed() && fUsedNames.contains(parameter.getNewName())) {
				result.addError(Messages.format(
					RefactoringCoreMessages.ExtractMethodRefactoring_error_nameInUse, 
					BasicElementLabels.getJavaElementName(parameter.getNewName())));
				return result;
			}
		}
		return result;
	}
	
	/**
	 * Checks if varargs are ordered correctly.
	 * @return validation status
	 */
	public RefactoringStatus checkVarargOrder() {
		for (Iterator iter= fParameterInfos.iterator(); iter.hasNext();) {
			ParameterInfo info= (ParameterInfo)iter.next();
			if (info.isOldVarargs() && iter.hasNext()) {
				return RefactoringStatus.createFatalErrorStatus(Messages.format(
					 RefactoringCoreMessages.ExtractMethodRefactoring_error_vararg_ordering,
					 BasicElementLabels.getJavaElementName(info.getOldName())));
			}
		}
		return new RefactoringStatus();
	}
	
	/**
	 * Returns the names already in use in the selected statements/expressions.
	 * 
	 * @return names already in use.
	 */
	public Set getUsedNames() {
		return fUsedNames;
	}
	
	/* (non-Javadoc)
	 * Method declared in Refactoring
	 */
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException {
		pm.beginTask(RefactoringCoreMessages.ExtractMethodRefactoring_checking_new_name, 2); 
		pm.subTask(EMPTY);
		
		RefactoringStatus result= checkMethodName();
		result.merge(checkParameterNames());
		result.merge(checkVarargOrder());
		pm.worked(1);
		if (pm.isCanceled())
			throw new OperationCanceledException();

		BodyDeclaration node= fAnalyzer.getEnclosingBodyDeclaration();
		if (node != null) {
			fAnalyzer.checkInput(result, fMethodName);
			pm.worked(1);
		}
		pm.done();
		return result;
	}
	
	/* (non-Javadoc)
	 * Method declared in IRefactoring
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException {
		if (fMethodName == null)
			return null;
		pm.beginTask("", 2); //$NON-NLS-1$
		try {
			fAnalyzer.aboutToCreateChange();
			BodyDeclaration declaration= fAnalyzer.getEnclosingBodyDeclaration();
			fRewriter= ASTRewrite.create(declaration.getAST());
	
			final CompilationUnitChange result= new CompilationUnitChange(RefactoringCoreMessages.ExtractMethodRefactoring_change_name, fCUnit);
			result.setSaveMode(TextFileChange.KEEP_SAVE_STATE);
			result.setDescriptor(new RefactoringChangeDescriptor(getRefactoringDescriptor()));
	
			MultiTextEdit root= new MultiTextEdit();
			result.setEdit(root);
			
			ASTNode[] selectedNodes= fAnalyzer.getSelectedNodes();
			fRewriter.setTargetSourceRangeComputer(new SelectionAwareSourceRangeComputer(selectedNodes, fCUnit.getBuffer(), fSelectionStart, fSelectionLength));
			
			TextEditGroup substituteDesc= new TextEditGroup(Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_substitute_with_call, BasicElementLabels.getJavaElementName(fMethodName))); 
			result.addTextEditGroup(substituteDesc);
			
			MethodDeclaration mm= createNewMethod(selectedNodes, fCUnit.findRecommendedLineSeparator(), substituteDesc);

			if (fLinkedProposalModel != null) {
				LinkedProposalPositionGroup typeGroup= fLinkedProposalModel.getPositionGroup(KEY_TYPE, true);
				typeGroup.addPosition(fRewriter.track(mm.getReturnType2()), false);
				
				ITypeBinding typeBinding= fAnalyzer.getReturnTypeBinding();
				if (typeBinding != null) {
					ITypeBinding[] relaxingTypes= ASTResolving.getNarrowingTypes(fAST, typeBinding);
					for (int i= 0; i < relaxingTypes.length; i++) {
						typeGroup.addProposal(relaxingTypes[i], fCUnit, relaxingTypes.length - i);
					}
				}
				
				LinkedProposalPositionGroup nameGroup= fLinkedProposalModel.getPositionGroup(KEY_NAME, true);
				nameGroup.addPosition(fRewriter.track(mm.getName()), false);
				
				ModifierCorrectionSubProcessor.installLinkedVisibilityProposals(fLinkedProposalModel, fRewriter, mm.modifiers(), false);
			}
			
			TextEditGroup insertDesc= new TextEditGroup(Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_add_method, BasicElementLabels.getJavaElementName(fMethodName))); 
			result.addTextEditGroup(insertDesc);
			
			if (fDestination == fDestinations[0]) {
				ChildListPropertyDescriptor desc= (ChildListPropertyDescriptor)declaration.getLocationInParent();
				ListRewrite container= fRewriter.getListRewrite(declaration.getParent(), desc);
				container.insertAfter(mm, declaration, insertDesc);
			} else {
				BodyDeclarationRewrite container= BodyDeclarationRewrite.create(fRewriter, fDestination);
				container.insert(mm, insertDesc);
			}
			
			replaceDuplicates(result);
		
			if (fImportRewriter.hasRecordedChanges()) {
				TextEdit edit= fImportRewriter.rewriteImports(null);
				root.addChild(edit);
				result.addTextEditGroup(new TextEditGroup(
					RefactoringCoreMessages.ExtractMethodRefactoring_organize_imports, 
					new TextEdit[] {edit}
				));
			}
			root.addChild(fRewriter.rewriteAST());
			return result;
		} finally {
			pm.done();
		}

	}

	private ExtractMethodDescriptor getRefactoringDescriptor() {
		final Map arguments= new HashMap();
		String project= null;
		IJavaProject javaProject= fCUnit.getJavaProject();
		if (javaProject != null)
			project= javaProject.getElementName();
		ITypeBinding type= null;
		if (fDestination instanceof AbstractTypeDeclaration) {
			final AbstractTypeDeclaration decl= (AbstractTypeDeclaration) fDestination;
			type= decl.resolveBinding();
		} else if (fDestination instanceof AnonymousClassDeclaration) {
			final AnonymousClassDeclaration decl= (AnonymousClassDeclaration) fDestination;
			type= decl.resolveBinding();
		}
		IMethodBinding method= null;
		final BodyDeclaration enclosing= fAnalyzer.getEnclosingBodyDeclaration();
		if (enclosing instanceof MethodDeclaration) {
			final MethodDeclaration node= (MethodDeclaration) enclosing;
			method= node.resolveBinding();
		}
		final int flags= RefactoringDescriptor.STRUCTURAL_CHANGE | JavaRefactoringDescriptor.JAR_REFACTORING | JavaRefactoringDescriptor.JAR_SOURCE_ATTACHMENT;
		final String description= Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_descriptor_description_short, BasicElementLabels.getJavaElementName(fMethodName));
		final String label= method != null ? BindingLabelProvider.getBindingLabel(method, JavaElementLabels.ALL_FULLY_QUALIFIED) : '{' + JavaElementLabels.ELLIPSIS_STRING + '}';
		final String header= Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_descriptor_description, new String[] { BasicElementLabels.getJavaElementName(getSignature()), label, BindingLabelProvider.getBindingLabel(type, JavaElementLabels.ALL_FULLY_QUALIFIED)});
		final JDTRefactoringDescriptorComment comment= new JDTRefactoringDescriptorComment(project, this, header);
		comment.addSetting(Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_name_pattern, BasicElementLabels.getJavaElementName(fMethodName)));
		comment.addSetting(Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_destination_pattern, BindingLabelProvider.getBindingLabel(type, JavaElementLabels.ALL_FULLY_QUALIFIED)));
		String visibility= JdtFlags.getVisibilityString(fVisibility);
		if ("".equals(visibility)) //$NON-NLS-1$
			visibility= RefactoringCoreMessages.ExtractMethodRefactoring_default_visibility;
		comment.addSetting(Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_visibility_pattern, visibility));
		if (fThrowRuntimeExceptions)
			comment.addSetting(RefactoringCoreMessages.ExtractMethodRefactoring_declare_thrown_exceptions);
		if (fReplaceDuplicates)
			comment.addSetting(RefactoringCoreMessages.ExtractMethodRefactoring_replace_occurrences);
		if (fGenerateJavadoc)
			comment.addSetting(RefactoringCoreMessages.ExtractMethodRefactoring_generate_comment);
		final ExtractMethodDescriptor descriptor= new ExtractMethodDescriptor( project, description, comment.asString(), arguments, flags);
		arguments.put(JavaRefactoringDescriptorUtil.ATTRIBUTE_INPUT, JavaRefactoringDescriptorUtil.elementToHandle(project, fCUnit));
		arguments.put(JavaRefactoringDescriptorUtil.ATTRIBUTE_NAME, fMethodName);
		arguments.put(JavaRefactoringDescriptorUtil.ATTRIBUTE_SELECTION, new Integer(fSelectionStart).toString() + " " + new Integer(fSelectionLength).toString()); //$NON-NLS-1$
		arguments.put(ATTRIBUTE_VISIBILITY, new Integer(fVisibility).toString());
		arguments.put(ATTRIBUTE_DESTINATION, new Integer(fDestinationIndex).toString());
		arguments.put(ATTRIBUTE_EXCEPTIONS, Boolean.valueOf(fThrowRuntimeExceptions).toString());
		arguments.put(ATTRIBUTE_COMMENTS, Boolean.valueOf(fGenerateJavadoc).toString());
		arguments.put(ATTRIBUTE_REPLACE, Boolean.valueOf(fReplaceDuplicates).toString());
		return descriptor;
	}
	
	/**
	 * Returns the signature of the new method.
	 * 
	 * @return the signature of the extracted method
	 */
	public String getSignature() {
		return getSignature(fMethodName);
	}
	
	/**
	 * Returns the signature of the new method.
	 * 
	 * @param methodName the method name used for the new method
	 * @return the signature of the extracted method
	 */
	public String getSignature(String methodName) {
		MethodDeclaration methodDecl= createNewMethodDeclaration();
		methodDecl.setBody(null);
		String str= ASTNodes.asString(methodDecl);
		return str.substring(0, str.indexOf(';'));
	}
	
	/**
	 * Returns the number of duplicate code snippets found.
	 * 
	 * @return the number of duplicate code fragments
	 */
	public int getNumberOfDuplicates() {
		if (fDuplicates == null)
			return 0;
		int result=0;
		for (int i= 0; i < fDuplicates.length; i++) {
			if (!fDuplicates[i].isMethodBody())
				result++;
		}
		return result;
	}
	
	public boolean getReplaceDuplicates() {
		return fReplaceDuplicates;
	}

	public void setReplaceDuplicates(boolean replace) {
		fReplaceDuplicates= replace;
	}
	
	public void setGenerateJavadoc(boolean generate) {
		fGenerateJavadoc= generate;
	}
	
	public boolean getGenerateJavadoc() {
		return fGenerateJavadoc;
	}
	
	//---- Helper methods ------------------------------------------------------------------------
	
	private void initializeParameterInfos() {
		IVariableBinding[] arguments= fAnalyzer.getArguments();
		fParameterInfos= new ArrayList(arguments.length);
		ASTNode root= fAnalyzer.getEnclosingBodyDeclaration();
		ParameterInfo vararg= null;
		for (int i= 0; i < arguments.length; i++) {
			IVariableBinding argument= arguments[i];
			if (argument == null)
				continue;
			VariableDeclaration declaration= ASTNodes.findVariableDeclaration(argument, root);
			boolean isVarargs= declaration instanceof SingleVariableDeclaration 
				? ((SingleVariableDeclaration)declaration).isVarargs()
				: false;
			ParameterInfo info= new ParameterInfo(argument, getType(declaration, isVarargs), argument.getName(), i);
			if (isVarargs) {
				vararg= info;
			} else {
				fParameterInfos.add(info);
			}
		}
		if (vararg != null) {
			fParameterInfos.add(vararg);
		}
	}
	
	private void initializeUsedNames() {
		fUsedNames= UsedNamesCollector.perform(fAnalyzer.getSelectedNodes());
		for (Iterator iter= fParameterInfos.iterator(); iter.hasNext();) {
			ParameterInfo parameter= (ParameterInfo)iter.next();
			fUsedNames.remove(parameter.getOldName());
		}
	}
	
	private void initializeDuplicates() {
		ASTNode start= fAnalyzer.getEnclosingBodyDeclaration();
		while(!(start instanceof AbstractTypeDeclaration) && !(start instanceof AnonymousClassDeclaration)) {
			start= start.getParent();
		}
		
		fDuplicates= SnippetFinder.perform(start, fAnalyzer.getSelectedNodes());
		fReplaceDuplicates= fDuplicates.length > 0 && ! fAnalyzer.isLiteralNodeSelected();
	}
	
	private void initializeDestinations() {
		List result= new ArrayList();
		BodyDeclaration decl= fAnalyzer.getEnclosingBodyDeclaration();
		ASTNode current= getNextParent(decl);
		result.add(current);
		if (decl instanceof MethodDeclaration || decl instanceof Initializer) {
			ITypeBinding binding= ASTNodes.getEnclosingType(current);
			ASTNode next= getNextParent(current);
			while (next != null && binding != null && binding.isNested() && !Modifier.isStatic(binding.getDeclaredModifiers())) {
				result.add(next);
				current= next;
				binding= ASTNodes.getEnclosingType(current);
				next= getNextParent(next);
			}
		}
		fDestinations= (ASTNode[])result.toArray(new ASTNode[result.size()]);
		fDestination= fDestinations[fDestinationIndex];
	}
	
	private ASTNode getNextParent(ASTNode node) {
		do {
			node= node.getParent();
		} while (node != null && !(node instanceof AbstractTypeDeclaration || node instanceof AnonymousClassDeclaration));
		return node;
	}
		
	private RefactoringStatus mergeTextSelectionStatus(RefactoringStatus status) {
		status.addFatalError(RefactoringCoreMessages.ExtractMethodRefactoring_no_set_of_statements); 
		return status;	
	}
	
	private String getType(VariableDeclaration declaration, boolean isVarargs) {
		String type= ASTNodes.asString(ASTNodeFactory.newType(declaration.getAST(), declaration));
		if (isVarargs)
			return type + ParameterInfo.ELLIPSIS;
		else
			return type;
	}
	
	//---- Code generation -----------------------------------------------------------------------
	
	private ASTNode[] createCallNodes(SnippetFinder.Match duplicate) {
		List result= new ArrayList(2);
		
		IVariableBinding[] locals= fAnalyzer.getCallerLocals();
		for (int i= 0; i < locals.length; i++) {
			result.add(createDeclaration(locals[i], null));
		}
		
		MethodInvocation invocation= fAST.newMethodInvocation();
		invocation.setName(fAST.newSimpleName(fMethodName));
		List arguments= invocation.arguments();
		for (int i= 0; i < fParameterInfos.size(); i++) {
			ParameterInfo parameter= (ParameterInfo)fParameterInfos.get(i);
			arguments.add(ASTNodeFactory.newName(fAST, getMappedName(duplicate, parameter)));
		}
		if (fLinkedProposalModel != null) {
			LinkedProposalPositionGroup nameGroup= fLinkedProposalModel.getPositionGroup(KEY_NAME, true);
			nameGroup.addPosition(fRewriter.track(invocation.getName()), false);
		}
		
		ASTNode call;		
		int returnKind= fAnalyzer.getReturnKind();
		switch (returnKind) {
			case ExtractMethodAnalyzer.ACCESS_TO_LOCAL:
				IVariableBinding binding= fAnalyzer.getReturnLocal();
				if (binding != null) {
					VariableDeclarationStatement decl= createDeclaration(getMappedBinding(duplicate, binding), invocation);
					call= decl;
				} else {
					Assignment assignment= fAST.newAssignment();
					assignment.setLeftHandSide(ASTNodeFactory.newName(fAST, 
							getMappedBinding(duplicate, fAnalyzer.getReturnValue()).getName()));
					assignment.setRightHandSide(invocation);
					call= assignment;
				}
				break;
			case ExtractMethodAnalyzer.RETURN_STATEMENT_VALUE:
				ReturnStatement rs= fAST.newReturnStatement();
				rs.setExpression(invocation);
				call= rs;
				break;
			default:
				call= invocation;
		}
		
		if (call instanceof Expression && !fAnalyzer.isExpressionSelected()) {
			call= fAST.newExpressionStatement((Expression)call);
		}
		result.add(call);
		
		// We have a void return statement. The code looks like
		// extracted();
		// return;	
		if (returnKind == ExtractMethodAnalyzer.RETURN_STATEMENT_VOID && !fAnalyzer.isLastStatementSelected()) {
			result.add(fAST.newReturnStatement());
		}
		return (ASTNode[])result.toArray(new ASTNode[result.size()]);		
	}
	
	private IVariableBinding getMappedBinding(SnippetFinder.Match duplicate, IVariableBinding org) {
		if (duplicate == null)
			return org;
		return duplicate.getMappedBinding(org);
	}
	
	private String getMappedName(SnippetFinder.Match duplicate, ParameterInfo paramter) {
		if (duplicate == null)
			return paramter.getOldName();
		return duplicate.getMappedName(paramter.getOldBinding()).getIdentifier();
	}
	
	private void replaceDuplicates(CompilationUnitChange result) {
		int numberOf= getNumberOfDuplicates();
		if (numberOf == 0 || !fReplaceDuplicates)
			return;
		String label= null;
		if (numberOf == 1)
			label= Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_duplicates_single, BasicElementLabels.getJavaElementName(fMethodName)); 
		else
			label= Messages.format(RefactoringCoreMessages.ExtractMethodRefactoring_duplicates_multi, BasicElementLabels.getJavaElementName(fMethodName)); 
		
		TextEditGroup description= new TextEditGroup(label);
		result.addTextEditGroup(description);
		
		for (int d= 0; d < fDuplicates.length; d++) {
			SnippetFinder.Match duplicate= fDuplicates[d];
			if (!duplicate.isMethodBody()) {
				ASTNode[] callNodes= createCallNodes(duplicate);
				new StatementRewrite(fRewriter, duplicate.getNodes()).replace(callNodes, description);
			}
		}		
	}
	
	private MethodDeclaration createNewMethod(ASTNode[] selectedNodes, String lineDelimiter, TextEditGroup substitute) throws CoreException {
		MethodDeclaration result= createNewMethodDeclaration();
		result.setBody(createMethodBody(selectedNodes, substitute));
		if (fGenerateJavadoc) {
			AbstractTypeDeclaration enclosingType= 
				(AbstractTypeDeclaration)ASTNodes.getParent(fAnalyzer.getEnclosingBodyDeclaration(), AbstractTypeDeclaration.class);
			String string= CodeGeneration.getMethodComment(fCUnit, enclosingType.getName().getIdentifier(), result, null, lineDelimiter);
			if (string != null) {
				Javadoc javadoc= (Javadoc)fRewriter.createStringPlaceholder(string, ASTNode.JAVADOC);
				result.setJavadoc(javadoc);
			}
		}
		return result;
	}

	private MethodDeclaration createNewMethodDeclaration() {
		MethodDeclaration result= fAST.newMethodDeclaration();
		
		int modifiers= fVisibility;
		if (Modifier.isStatic(fAnalyzer.getEnclosingBodyDeclaration().getModifiers()) || fAnalyzer.getForceStatic()) {
			modifiers|= Modifier.STATIC;
		}

		ITypeBinding[] typeVariables= computeLocalTypeVariables();
		List typeParameters= result.typeParameters();
		for (int i= 0; i < typeVariables.length; i++) {
			TypeParameter parameter= fAST.newTypeParameter();
			parameter.setName(fAST.newSimpleName(typeVariables[i].getName()));
			typeParameters.add(parameter);
		}
		
		result.modifiers().addAll(ASTNodeFactory.newModifiers(fAST, modifiers));
		result.setReturnType2((Type)ASTNode.copySubtree(fAST, fAnalyzer.getReturnType()));
		result.setName(fAST.newSimpleName(fMethodName));
		
		List parameters= result.parameters();
		for (int i= 0; i < fParameterInfos.size(); i++) {
			ParameterInfo info= (ParameterInfo)fParameterInfos.get(i);
			VariableDeclaration infoDecl= getVariableDeclaration(info);
			SingleVariableDeclaration parameter= fAST.newSingleVariableDeclaration();
			parameter.modifiers().addAll(ASTNodeFactory.newModifiers(fAST, ASTNodes.getModifiers(infoDecl)));
			parameter.setType(ASTNodeFactory.newType(fAST, infoDecl));
			parameter.setName(fAST.newSimpleName(info.getNewName()));
			parameter.setVarargs(info.isNewVarargs());
			parameters.add(parameter);
		}
		
		List exceptions= result.thrownExceptions();
		ITypeBinding[] exceptionTypes= fAnalyzer.getExceptions(fThrowRuntimeExceptions);
		for (int i= 0; i < exceptionTypes.length; i++) {
			ITypeBinding exceptionType= exceptionTypes[i];
			exceptions.add(ASTNodeFactory.newName(fAST, fImportRewriter.addImport(exceptionType)));
		}
		return result;
	}
	
	private ITypeBinding[] computeLocalTypeVariables() {
		List result= new ArrayList(Arrays.asList(fAnalyzer.getTypeVariables()));
		for (int i= 0; i < fParameterInfos.size(); i++) {
			ParameterInfo info= (ParameterInfo)fParameterInfos.get(i);
			processVariable(result, info.getOldBinding());
		}
		IVariableBinding[] methodLocals= fAnalyzer.getMethodLocals();
		for (int i= 0; i < methodLocals.length; i++) {
			processVariable(result, methodLocals[i]);
		}
		return (ITypeBinding[])result.toArray(new ITypeBinding[result.size()]);
	}

	private void processVariable(List result, IVariableBinding variable) {
		if (variable == null)
			return;
		ITypeBinding binding= variable.getType();
		if (binding != null && binding.isParameterizedType()) {
			ITypeBinding[] typeArgs= binding.getTypeArguments();
			for (int args= 0; args < typeArgs.length; args++) {
				ITypeBinding arg= typeArgs[args];
				if (arg.isTypeVariable() && !result.contains(arg)) {
					ASTNode decl= fRoot.findDeclaringNode(arg);
					if (decl != null && decl.getParent() instanceof MethodDeclaration) {
						result.add(arg);
					}
				}
			}
		}
	}
	
	private Block createMethodBody(ASTNode[] selectedNodes, TextEditGroup substitute) {
		Block result= fAST.newBlock();
		ListRewrite statements= fRewriter.getListRewrite(result, Block.STATEMENTS_PROPERTY);
		
		// Locals that are not passed as an arguments since the extracted method only
		// writes to them
		IVariableBinding[] methodLocals= fAnalyzer.getMethodLocals();
		for (int i= 0; i < methodLocals.length; i++) {
			if (methodLocals[i] != null) {
				result.statements().add(createDeclaration(methodLocals[i], null));
			}
		}

		for (Iterator iter= fParameterInfos.iterator(); iter.hasNext();) {
			ParameterInfo parameter= (ParameterInfo)iter.next();
			if (parameter.isRenamed()) {
				for (int n= 0; n < selectedNodes.length; n++) {
					SimpleName[] oldNames= LinkedNodeFinder.findByBinding(selectedNodes[n], parameter.getOldBinding());
					for (int i= 0; i < oldNames.length; i++) {
						fRewriter.replace(oldNames[i], fAST.newSimpleName(parameter.getNewName()), null);
					}
				}
			}
		}
		
		boolean extractsExpression= fAnalyzer.isExpressionSelected();
		ASTNode[] callNodes= createCallNodes(null);
		ASTNode replacementNode;
		if (callNodes.length == 1) {
			replacementNode= callNodes[0];
		} else {
			replacementNode= fRewriter.createGroupNode(callNodes);
		}
		if (extractsExpression) {
			// if we have an expression then only one node is selected.
			ITypeBinding binding= fAnalyzer.getExpressionBinding();
			if (binding != null && (!binding.isPrimitive() || !"void".equals(binding.getName()))) { //$NON-NLS-1$
				ReturnStatement rs= fAST.newReturnStatement();
				rs.setExpression((Expression)fRewriter.createMoveTarget(selectedNodes[0]));
				statements.insertLast(rs, null);
			} else {
				ExpressionStatement st= fAST.newExpressionStatement((Expression)fRewriter.createMoveTarget(selectedNodes[0]));
				statements.insertLast(st, null);
			}
			fRewriter.replace(selectedNodes[0], replacementNode, substitute);
		} else {
			if (selectedNodes.length == 1) {
				statements.insertLast(fRewriter.createMoveTarget(selectedNodes[0]), substitute);
				fRewriter.replace(selectedNodes[0], replacementNode, substitute);
			} else {
				ListRewrite source= fRewriter.getListRewrite(
					selectedNodes[0].getParent(), 
					(ChildListPropertyDescriptor)selectedNodes[0].getLocationInParent());
				ASTNode toMove= source.createMoveTarget(
					selectedNodes[0], selectedNodes[selectedNodes.length - 1],
					replacementNode, substitute);
				statements.insertLast(toMove, substitute);
			}
			IVariableBinding returnValue= fAnalyzer.getReturnValue();
			if (returnValue != null) {
				ReturnStatement rs= fAST.newReturnStatement();
				rs.setExpression(fAST.newSimpleName(getName(returnValue)));
				statements.insertLast(rs, null);				
			}
		}
		return result;
	}
	
	private String getName(IVariableBinding binding) {
		for (Iterator iter= fParameterInfos.iterator(); iter.hasNext();) {
			ParameterInfo info= (ParameterInfo)iter.next();
			if (Bindings.equals(binding, info.getOldBinding())) {
				return info.getNewName();
			}
		}
		return binding.getName();
	}
	
	private VariableDeclaration getVariableDeclaration(ParameterInfo parameter) {
		return ASTNodes.findVariableDeclaration(parameter.getOldBinding(), fAnalyzer.getEnclosingBodyDeclaration());
	}
	
	private VariableDeclarationStatement createDeclaration(IVariableBinding binding, Expression intilizer) {
		VariableDeclaration original= ASTNodes.findVariableDeclaration(binding, fAnalyzer.getEnclosingBodyDeclaration());
		VariableDeclarationFragment fragment= fAST.newVariableDeclarationFragment();
		fragment.setName((SimpleName)ASTNode.copySubtree(fAST, original.getName()));
		fragment.setInitializer(intilizer);	
		VariableDeclarationStatement result= fAST.newVariableDeclarationStatement(fragment);
		result.modifiers().addAll(ASTNode.copySubtrees(fAST, ASTNodes.getModifiers(original)));
		result.setType(ASTNodeFactory.newType(fAST, original));
		return result;
	}	

	public ICompilationUnit getCompilationUnit() {
		return fCUnit;
	}

	private RefactoringStatus initialize(JavaRefactoringArguments arguments) {
		final String selection= arguments.getAttribute(JavaRefactoringDescriptorUtil.ATTRIBUTE_SELECTION);
		if (selection == null)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, JavaRefactoringDescriptorUtil.ATTRIBUTE_SELECTION));
		
		int offset= -1;
		int length= -1;
		final StringTokenizer tokenizer= new StringTokenizer(selection);
		if (tokenizer.hasMoreTokens())
			offset= Integer.valueOf(tokenizer.nextToken()).intValue();
		if (tokenizer.hasMoreTokens())
			length= Integer.valueOf(tokenizer.nextToken()).intValue();
		if (offset < 0 || length < 0)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_illegal_argument, new Object[] { selection, JavaRefactoringDescriptorUtil.ATTRIBUTE_SELECTION}));

		fSelectionStart= offset;
		fSelectionLength= length;

		final String handle= arguments.getAttribute(JavaRefactoringDescriptorUtil.ATTRIBUTE_INPUT);
		if (handle == null)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, JavaRefactoringDescriptorUtil.ATTRIBUTE_INPUT));
			
		IJavaElement element= JavaRefactoringDescriptorUtil.handleToElement(arguments.getProject(), handle, false);
		if (element == null || !element.exists() || element.getElementType() != IJavaElement.COMPILATION_UNIT)
			return JavaRefactoringDescriptorUtil.createInputFatalStatus(element, getName(), IJavaRefactorings.EXTRACT_METHOD);

		fCUnit= (ICompilationUnit) element;
		final String visibility= arguments.getAttribute(ATTRIBUTE_VISIBILITY);
		if (visibility != null && visibility.length() != 0) {
			int flag= 0;
			try {
				flag= Integer.parseInt(visibility);
			} catch (NumberFormatException exception) {
				return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_VISIBILITY));
			}
			fVisibility= flag;
		}
		final String name= arguments.getAttribute(JavaRefactoringDescriptorUtil.ATTRIBUTE_NAME);
		if (name == null || name.length() == 0)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, JavaRefactoringDescriptorUtil.ATTRIBUTE_NAME));

		fMethodName= name;
		
		final String destination= arguments.getAttribute(ATTRIBUTE_DESTINATION);
		if (destination != null && destination.length() == 0) {
			int index= 0;
			try {
				index= Integer.parseInt(destination);
			} catch (NumberFormatException exception) {
				return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_DESTINATION));
			}
			fDestinationIndex= index;
		}
		final String replace= arguments.getAttribute(ATTRIBUTE_REPLACE);
		if (replace == null)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_REPLACE));

		fReplaceDuplicates= Boolean.valueOf(replace).booleanValue();

		final String comments= arguments.getAttribute(ATTRIBUTE_COMMENTS);
		if (comments == null)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_COMMENTS));

		fGenerateJavadoc= Boolean.valueOf(comments).booleanValue();

		final String exceptions= arguments.getAttribute(ATTRIBUTE_EXCEPTIONS);
		if (exceptions == null)
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ATTRIBUTE_EXCEPTIONS));

		fThrowRuntimeExceptions= Boolean.valueOf(exceptions).booleanValue();

		return new RefactoringStatus();
	}	
}
