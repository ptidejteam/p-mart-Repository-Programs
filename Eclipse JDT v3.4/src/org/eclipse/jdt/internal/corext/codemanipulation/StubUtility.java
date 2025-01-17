/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     John Kaplan, johnkaplantech@gmail.com - 108071 [code templates] template for body of newly created class
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.codemanipulation;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.formatter.IndentManipulation;

import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.template.java.CodeTemplateContext;
import org.eclipse.jdt.internal.corext.template.java.CodeTemplateContextType;
import org.eclipse.jdt.internal.corext.util.Strings;

import org.eclipse.jdt.ui.CodeStyleConfiguration;
import org.eclipse.jdt.ui.PreferenceConstants;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaUIStatus;
import org.eclipse.jdt.internal.ui.text.correction.ASTResolving;
import org.eclipse.jdt.internal.ui.viewsupport.ProjectTemplateStore;

public class StubUtility {
	
	private static final String[] EMPTY= new String[0];
	
	private static final Set VALID_TYPE_BODY_TEMPLATES;
	static {
		VALID_TYPE_BODY_TEMPLATES= new HashSet();
		VALID_TYPE_BODY_TEMPLATES.add(CodeTemplateContextType.CLASSBODY_ID);
		VALID_TYPE_BODY_TEMPLATES.add(CodeTemplateContextType.INTERFACEBODY_ID);
		VALID_TYPE_BODY_TEMPLATES.add(CodeTemplateContextType.ENUMBODY_ID);
		VALID_TYPE_BODY_TEMPLATES.add(CodeTemplateContextType.ANNOTATIONBODY_ID);
	}
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 */
	public static String getMethodBodyContent(boolean isConstructor, IJavaProject project, String destTypeName, String methodName, String bodyStatement, String lineDelimiter) throws CoreException {
		String templateName= isConstructor ? CodeTemplateContextType.CONSTRUCTORSTUB_ID : CodeTemplateContextType.METHODSTUB_ID;
		Template template= getCodeTemplate(templateName, project);
		if (template == null) {
			return bodyStatement;
		}
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), project, lineDelimiter);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, destTypeName);
		context.setVariable(CodeTemplateContextType.BODY_STATEMENT, bodyStatement);
		String str= evaluateTemplate(context, template, new String[] { CodeTemplateContextType.BODY_STATEMENT });
		if (str == null && !Strings.containsOnlyWhitespaces(bodyStatement)) {
			return bodyStatement;
		}
		return str;
	}
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 */
	public static String getGetterMethodBodyContent(IJavaProject project, String destTypeName, String methodName, String fieldName, String lineDelimiter) throws CoreException {
		String templateName= CodeTemplateContextType.GETTERSTUB_ID;
		Template template= getCodeTemplate(templateName, project);
		if (template == null) {
			return null;
		}
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), project, lineDelimiter);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, destTypeName);
		context.setVariable(CodeTemplateContextType.FIELD, fieldName);
		
		return evaluateTemplate(context, template);
	}
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 */
	public static String getSetterMethodBodyContent(IJavaProject project, String destTypeName, String methodName, String fieldName, String paramName, String lineDelimiter) throws CoreException {
		String templateName= CodeTemplateContextType.SETTERSTUB_ID;
		Template template= getCodeTemplate(templateName, project);
		if (template == null) {
			return null;
		}
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), project, lineDelimiter);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, destTypeName);
		context.setVariable(CodeTemplateContextType.FIELD, fieldName);
		context.setVariable(CodeTemplateContextType.FIELD_TYPE, fieldName);
		context.setVariable(CodeTemplateContextType.PARAM, paramName);
		
		return evaluateTemplate(context, template);
	}
	
	public static String getCatchBodyContent(ICompilationUnit cu, String exceptionType, String variableName, ASTNode locationInAST, String lineDelimiter) throws CoreException {
		String enclosingType= ""; //$NON-NLS-1$
		String enclosingMethod= ""; //$NON-NLS-1$
			
		if (locationInAST != null) {
			MethodDeclaration parentMethod= ASTResolving.findParentMethodDeclaration(locationInAST);
			if (parentMethod != null) {
				enclosingMethod= parentMethod.getName().getIdentifier();
				locationInAST= parentMethod;
			}
			ASTNode parentType= ASTResolving.findParentType(locationInAST);
			if (parentType instanceof AbstractTypeDeclaration) {
				enclosingType= ((AbstractTypeDeclaration) parentType).getName().getIdentifier();
			}
		}
		return getCatchBodyContent(cu, exceptionType, variableName, enclosingType, enclosingMethod, lineDelimiter);
	}
	
	
	public static String getCatchBodyContent(ICompilationUnit cu, String exceptionType, String variableName, String enclosingType, String enclosingMethod, String lineDelimiter) throws CoreException {
		Template template= getCodeTemplate(CodeTemplateContextType.CATCHBLOCK_ID, cu.getJavaProject());
		if (template == null) {
			return null;
		}

		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, enclosingType);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, enclosingMethod); 
		context.setVariable(CodeTemplateContextType.EXCEPTION_TYPE, exceptionType);
		context.setVariable(CodeTemplateContextType.EXCEPTION_VAR, variableName); 
		return evaluateTemplate(context, template);
	}
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getCompilationUnitContent(ICompilationUnit, String, String, String, String)
	 */	
	public static String getCompilationUnitContent(ICompilationUnit cu, String fileComment, String typeComment, String typeContent, String lineDelimiter) throws CoreException {
		IPackageFragment pack= (IPackageFragment) cu.getParent();
		String packDecl= pack.isDefaultPackage() ? "" : "package " + pack.getElementName() + ';'; //$NON-NLS-1$ //$NON-NLS-2$
		return getCompilationUnitContent(cu, packDecl, fileComment, typeComment, typeContent, lineDelimiter);
	}
	
	public static String getCompilationUnitContent(ICompilationUnit cu, String packDecl, String fileComment, String typeComment, String typeContent, String lineDelimiter) throws CoreException {
		Template template= getCodeTemplate(CodeTemplateContextType.NEWTYPE_ID, cu.getJavaProject());
		if (template == null) {
			return null;
		}
		
		IJavaProject project= cu.getJavaProject();
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), project, lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.PACKAGE_DECLARATION, packDecl);
		context.setVariable(CodeTemplateContextType.TYPE_COMMENT, typeComment != null ? typeComment : ""); //$NON-NLS-1$
		context.setVariable(CodeTemplateContextType.FILE_COMMENT, fileComment != null ? fileComment : ""); //$NON-NLS-1$
		context.setVariable(CodeTemplateContextType.TYPE_DECLARATION, typeContent);
		context.setVariable(CodeTemplateContextType.TYPENAME, JavaCore.removeJavaLikeExtension(cu.getElementName()));
		
		String[] fullLine= { CodeTemplateContextType.PACKAGE_DECLARATION, CodeTemplateContextType.FILE_COMMENT, CodeTemplateContextType.TYPE_COMMENT };
		return evaluateTemplate(context, template, fullLine);
	}
	
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getFileComment(ICompilationUnit, String)
	 */	
	public static String getFileComment(ICompilationUnit cu, String lineDelimiter) throws CoreException {
		Template template= getCodeTemplate(CodeTemplateContextType.FILECOMMENT_ID, cu.getJavaProject());
		if (template == null) {
			return null;
		}
		
		IJavaProject project= cu.getJavaProject();
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), project, lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.TYPENAME, JavaCore.removeJavaLikeExtension(cu.getElementName()));
		return evaluateTemplate(context, template);
	}	

	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getTypeComment(ICompilationUnit, String, String[], String)
	 */		
	public static String getTypeComment(ICompilationUnit cu, String typeQualifiedName, String[] typeParameterNames, String lineDelim) throws CoreException {
		Template template= getCodeTemplate(CodeTemplateContextType.TYPECOMMENT_ID, cu.getJavaProject());
		if (template == null) {
			return null;
		}
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelim);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, Signature.getQualifier(typeQualifiedName));
		context.setVariable(CodeTemplateContextType.TYPENAME, Signature.getSimpleName(typeQualifiedName));

		TemplateBuffer buffer;
		try {
			buffer= context.evaluate(template);
		} catch (BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		String str= buffer.getString();
		if (Strings.containsOnlyWhitespaces(str)) {
			return null;
		}
		
		TemplateVariable position= findVariable(buffer, CodeTemplateContextType.TAGS); // look if Javadoc tags have to be added
		if (position == null) {
			return str;
		}
		
		IDocument document= new Document(str);
		int[] tagOffsets= position.getOffsets();
		for (int i= tagOffsets.length - 1; i >= 0; i--) { // from last to first
			try {
				insertTag(document, tagOffsets[i], position.getLength(), EMPTY, EMPTY, null, typeParameterNames, false, lineDelim);
			} catch (BadLocationException e) {
				throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
			}
		}
		return document.get();
	}

	/*
	 * Returns the parameters type names used in see tags. Currently, these are always fully qualified.
	 */
	public static String[] getParameterTypeNamesForSeeTag(IMethodBinding binding) {
		ITypeBinding[] typeBindings= binding.getParameterTypes();
		String[] result= new String[typeBindings.length];
		for (int i= 0; i < result.length; i++) {
			ITypeBinding curr= typeBindings[i];
			if (curr.isTypeVariable()) {
				curr= curr.getErasure(); // in Javadoc only use type variable erasure
			}
			curr= curr.getTypeDeclaration(); // no parameterized types
			result[i]= curr.getQualifiedName();
		}
		return result;
	}
	
	/*
	 * Returns the parameters type names used in see tags. Currently, these are always fully qualified.
	 */
	private static String[] getParameterTypeNamesForSeeTag(IMethod overridden) {
		try {
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setProject(overridden.getJavaProject());
			IBinding[] bindings= parser.createBindings(new IJavaElement[] { overridden }, null);
			if (bindings.length == 1 && bindings[0] instanceof IMethodBinding) {
				return getParameterTypeNamesForSeeTag((IMethodBinding) bindings[0]);
			}
		} catch (IllegalStateException e) {
			// method does not exist
		}
		// fall back code. Not good for generic methods!
		String[] paramTypes= overridden.getParameterTypes();
		String[] paramTypeNames= new String[paramTypes.length];
		for (int i= 0; i < paramTypes.length; i++) {
			paramTypeNames[i]= Signature.toString(Signature.getTypeErasure(paramTypes[i]));
		}
		return paramTypeNames;
	}

	private static String getSeeTag(String declaringClassQualifiedName, String methodName, String[] parameterTypesQualifiedNames) {
		StringBuffer buf= new StringBuffer();
		buf.append("@see "); //$NON-NLS-1$
		buf.append(declaringClassQualifiedName);
		buf.append('#'); 
		buf.append(methodName);
		buf.append('(');
		for (int i= 0; i < parameterTypesQualifiedNames.length; i++) {
			if (i > 0) {
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append(parameterTypesQualifiedNames[i]);
		}
		buf.append(')');
		return buf.toString();
	}
	
	public static String[] getTypeParameterNames(ITypeParameter[] typeParameters) {
		String[] typeParametersNames= new String[typeParameters.length];
		for (int i= 0; i < typeParameters.length; i++) {
			typeParametersNames[i]= typeParameters[i].getElementName();
		}
		return typeParametersNames;
	}

	/**
     * Don't use this method directly, use CodeGeneration.
	 * @param templateID the template id of the type body to get. Valid id's are {@link CodeTemplateContextType#CLASSBODY_ID},
	 * {@link CodeTemplateContextType#INTERFACEBODY_ID}, {@link CodeTemplateContextType#ENUMBODY_ID},{@link CodeTemplateContextType#ANNOTATIONBODY_ID},
	 * @param cu the compilation unit to which the template is added
	 * @param typeName the type name
	 * @param lineDelim the line delimiter to use
	 * @return return the type body template or <code>null</code>
	 * @throws CoreException thrown if the template could not be evaluated
	 * @see org.eclipse.jdt.ui.CodeGeneration#getTypeBody(String, ICompilationUnit, String, String)
	 */		
	public static String getTypeBody(String templateID, ICompilationUnit cu, String typeName, String lineDelim) throws CoreException {
		if ( !VALID_TYPE_BODY_TEMPLATES.contains(templateID)) {
			throw new IllegalArgumentException("Invalid code template ID: " + templateID);  //$NON-NLS-1$
		}
		
		Template template= getCodeTemplate(templateID, cu.getJavaProject());
		if (template == null) {
			return null;
		}
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelim);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.TYPENAME, typeName);

		return evaluateTemplate(context, template);
	}
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getMethodComment(ICompilationUnit, String, String, String[], String[], String, String[], IMethod, String)
	 */
	public static String getMethodComment(ICompilationUnit cu, String typeName, String methodName, String[] paramNames, String[] excTypeSig, String retTypeSig, String[] typeParameterNames, IMethod target, boolean delegate, String lineDelimiter) throws CoreException {
		String templateName= CodeTemplateContextType.METHODCOMMENT_ID;
		if (retTypeSig == null) {
			templateName= CodeTemplateContextType.CONSTRUCTORCOMMENT_ID;
		} else if (target != null) {
			if (delegate)
				templateName= CodeTemplateContextType.DELEGATECOMMENT_ID;
			else
				templateName= CodeTemplateContextType.OVERRIDECOMMENT_ID;
		}
		Template template= getCodeTemplate(templateName, cu.getJavaProject());
		if (template == null) {
			return null;
		}		
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, typeName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
				
		if (retTypeSig != null) {
			context.setVariable(CodeTemplateContextType.RETURN_TYPE, Signature.toString(retTypeSig));
		}
		if (target != null) {
			String targetTypeName= target.getDeclaringType().getFullyQualifiedName('.');
			String[] targetParamTypeNames= getParameterTypeNamesForSeeTag(target);
			if (delegate)
				context.setVariable(CodeTemplateContextType.SEE_TO_TARGET_TAG, getSeeTag(targetTypeName, methodName, targetParamTypeNames));
			else
				context.setVariable(CodeTemplateContextType.SEE_TO_OVERRIDDEN_TAG, getSeeTag(targetTypeName, methodName, targetParamTypeNames));
		}
		TemplateBuffer buffer;
		try {
			buffer= context.evaluate(template);
		} catch (BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		if (buffer == null) {
			return null;
		}
		
		String str= buffer.getString();
		if (Strings.containsOnlyWhitespaces(str)) {
			return null;
		}
		TemplateVariable position= findVariable(buffer, CodeTemplateContextType.TAGS); // look if Javadoc tags have to be added
		if (position == null) {
			return str;
		}
			
		IDocument document= new Document(str);
		String[] exceptionNames= new String[excTypeSig.length];
		for (int i= 0; i < excTypeSig.length; i++) {
			exceptionNames[i]= Signature.toString(excTypeSig[i]);
		}
		String returnType= retTypeSig != null ? Signature.toString(retTypeSig) : null;
		int[] tagOffsets= position.getOffsets();
		for (int i= tagOffsets.length - 1; i >= 0; i--) { // from last to first
			try {
				insertTag(document, tagOffsets[i], position.getLength(), paramNames, exceptionNames, returnType, typeParameterNames, false, lineDelimiter);
			} catch (BadLocationException e) {
				throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
			}
		}
		return document.get();
	}
	
	// remove lines for empty variables
	private static String fixEmptyVariables(TemplateBuffer buffer, String[] variables) throws MalformedTreeException, BadLocationException {
		IDocument doc= new Document(buffer.getString());
		int nLines= doc.getNumberOfLines();
		MultiTextEdit edit= new MultiTextEdit();
		HashSet removedLines= new HashSet();
		for (int i= 0; i < variables.length; i++) {
			TemplateVariable position= findVariable(buffer, variables[i]); // look if Javadoc tags have to be added
			if (position == null || position.getLength() > 0) {
				continue;
			}
			int[] offsets= position.getOffsets();
			for (int k= 0; k < offsets.length; k++) {
				int line= doc.getLineOfOffset(offsets[k]);
				IRegion lineInfo= doc.getLineInformation(line);
				int offset= lineInfo.getOffset();
				String str= doc.get(offset, lineInfo.getLength());
				if (Strings.containsOnlyWhitespaces(str) && nLines > line + 1 && removedLines.add(new Integer(line))) {
					int nextStart= doc.getLineOffset(line + 1);
					edit.addChild(new DeleteEdit(offset, nextStart - offset));
				}
			}
		}
		edit.apply(doc, 0);
		return doc.get();
	}
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 */
	public static String getFieldComment(ICompilationUnit cu, String typeName, String fieldName, String lineDelimiter) throws CoreException {
		Template template= getCodeTemplate(CodeTemplateContextType.FIELDCOMMENT_ID, cu.getJavaProject());
		if (template == null) {
			return null;
		}
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.FIELD_TYPE, typeName);
		context.setVariable(CodeTemplateContextType.FIELD, fieldName);
		
		return evaluateTemplate(context, template);
	}	
	
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getSetterComment(ICompilationUnit, String, String, String, String, String, String, String)
	 */
	public static String getSetterComment(ICompilationUnit cu, String typeName, String methodName, String fieldName, String fieldType, String paramName, String bareFieldName, String lineDelimiter) throws CoreException {
		String templateName= CodeTemplateContextType.SETTERCOMMENT_ID;
		Template template= getCodeTemplate(templateName, cu.getJavaProject());
		if (template == null) {
			return null;
		}
		
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, typeName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
		context.setVariable(CodeTemplateContextType.FIELD, fieldName);
		context.setVariable(CodeTemplateContextType.FIELD_TYPE, fieldType);
		context.setVariable(CodeTemplateContextType.BARE_FIELD_NAME, bareFieldName);
		context.setVariable(CodeTemplateContextType.PARAM, paramName);

		return evaluateTemplate(context, template);
	}	
	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getGetterComment(ICompilationUnit, String, String, String, String, String, String)
	 */
	public static String getGetterComment(ICompilationUnit cu, String typeName, String methodName, String fieldName, String fieldType, String bareFieldName, String lineDelimiter) throws CoreException {
		String templateName= CodeTemplateContextType.GETTERCOMMENT_ID;
		Template template= getCodeTemplate(templateName, cu.getJavaProject());
		if (template == null) {
			return null;
		}		
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, typeName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, methodName);
		context.setVariable(CodeTemplateContextType.FIELD, fieldName);
		context.setVariable(CodeTemplateContextType.FIELD_TYPE, fieldType);
		context.setVariable(CodeTemplateContextType.BARE_FIELD_NAME, bareFieldName);

		return evaluateTemplate(context, template);
	}
	
	private static String evaluateTemplate(CodeTemplateContext context, Template template) throws CoreException {
		TemplateBuffer buffer;
		try {
			buffer= context.evaluate(template);
		} catch (BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		if (buffer == null)
			return null;
		String str= buffer.getString();
		if (Strings.containsOnlyWhitespaces(str)) {
			return null;
		}
		return str;
	}
	
	private static String evaluateTemplate(CodeTemplateContext context, Template template, String[] fullLineVariables) throws CoreException {
		TemplateBuffer buffer;
		try {
			buffer= context.evaluate(template);
			if (buffer == null)
				return null;
			String str= fixEmptyVariables(buffer, fullLineVariables);
			if (Strings.containsOnlyWhitespaces(str)) {
				return null;
			}
			return str;
		} catch (BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
	}

	
	/*
	 * Don't use this method directly, use CodeGeneration.
	 * @see org.eclipse.jdt.ui.CodeGeneration#getMethodComment(ICompilationUnit, String, MethodDeclaration, boolean, String, String[], String)
	 */
	public static String getMethodComment(ICompilationUnit cu, String typeName, MethodDeclaration decl, boolean isDeprecated, String targetName, String targetMethodDeclaringTypeName, String[] targetMethodParameterTypeNames, boolean delegate, String lineDelimiter) throws CoreException {
		boolean needsTarget= targetMethodDeclaringTypeName != null && targetMethodParameterTypeNames != null;
		String templateName= CodeTemplateContextType.METHODCOMMENT_ID;
		if (decl.isConstructor()) {
			templateName= CodeTemplateContextType.CONSTRUCTORCOMMENT_ID;
		} else if (needsTarget) {
			if (delegate)
				templateName= CodeTemplateContextType.DELEGATECOMMENT_ID;
			else
				templateName= CodeTemplateContextType.OVERRIDECOMMENT_ID;
		}
		Template template= getCodeTemplate(templateName, cu.getJavaProject());
		if (template == null) {
			return null;
		}		
		CodeTemplateContext context= new CodeTemplateContext(template.getContextTypeId(), cu.getJavaProject(), lineDelimiter);
		context.setCompilationUnitVariables(cu);
		context.setVariable(CodeTemplateContextType.ENCLOSING_TYPE, typeName);
		context.setVariable(CodeTemplateContextType.ENCLOSING_METHOD, decl.getName().getIdentifier());
		if (!decl.isConstructor()) {
			context.setVariable(CodeTemplateContextType.RETURN_TYPE, ASTNodes.asString(getReturnType(decl)));
		}
		if (needsTarget) {
			if (delegate)
				context.setVariable(CodeTemplateContextType.SEE_TO_TARGET_TAG, getSeeTag(targetMethodDeclaringTypeName, targetName, targetMethodParameterTypeNames));
			else
				context.setVariable(CodeTemplateContextType.SEE_TO_OVERRIDDEN_TAG, getSeeTag(targetMethodDeclaringTypeName, targetName, targetMethodParameterTypeNames));
		}
		
		TemplateBuffer buffer;
		try {
			buffer= context.evaluate(template);
		} catch (BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		if (buffer == null)
			return null;
		String str= buffer.getString();
		if (Strings.containsOnlyWhitespaces(str)) {
			return null;
		}
		TemplateVariable position= findVariable(buffer, CodeTemplateContextType.TAGS);  // look if Javadoc tags have to be added
		if (position == null) {
			return str;
		}
			
		IDocument textBuffer= new Document(str);
		List typeParams= decl.typeParameters();
		String[] typeParamNames= new String[typeParams.size()];
		for (int i= 0; i < typeParamNames.length; i++) {
			TypeParameter elem= (TypeParameter) typeParams.get(i);
			typeParamNames[i]= elem.getName().getIdentifier();
		}
		List params= decl.parameters();
		String[] paramNames= new String[params.size()];
		for (int i= 0; i < paramNames.length; i++) {
			SingleVariableDeclaration elem= (SingleVariableDeclaration) params.get(i);
			paramNames[i]= elem.getName().getIdentifier();
		}
		List exceptions= decl.thrownExceptions();
		String[] exceptionNames= new String[exceptions.size()];
		for (int i= 0; i < exceptionNames.length; i++) {
			exceptionNames[i]= ASTNodes.getSimpleNameIdentifier((Name) exceptions.get(i));
		}
		
		String returnType= null;
		if (!decl.isConstructor()) {
			returnType= ASTNodes.asString(getReturnType(decl));
		}
		int[] tagOffsets= position.getOffsets();
		for (int i= tagOffsets.length - 1; i >= 0; i--) { // from last to first
			try {
				insertTag(textBuffer, tagOffsets[i], position.getLength(), paramNames, exceptionNames, returnType, typeParamNames, isDeprecated, lineDelimiter);
			} catch (BadLocationException e) {
				throw new CoreException(JavaUIStatus.createError(IStatus.ERROR, e));
			}
		}
		return textBuffer.get();
	}
	
	/**
	 * @param decl the method declaration
	 * @return the return type
	 * @deprecated Deprecated to avoid deprecated warnings
	 */
	private static ASTNode getReturnType(MethodDeclaration decl) {
		// used from API, can't eliminate
		return decl.getAST().apiLevel() == AST.JLS2 ? decl.getReturnType() : decl.getReturnType2();
	}
	
	
	private static TemplateVariable findVariable(TemplateBuffer buffer, String variable) {
		TemplateVariable[] positions= buffer.getVariables();
		for (int i= 0; i < positions.length; i++) {
			TemplateVariable curr= positions[i];
			if (variable.equals(curr.getType())) {
				return curr;
			}
		}
		return null;		
	}	
	
	private static void insertTag(IDocument textBuffer, int offset, int length, String[] paramNames, String[] exceptionNames, String returnType, String[] typeParameterNames, boolean isDeprecated, String lineDelimiter) throws BadLocationException {
		IRegion region= textBuffer.getLineInformationOfOffset(offset);
		if (region == null) {
			return;
		}
		String lineStart= textBuffer.get(region.getOffset(), offset - region.getOffset());
		
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < typeParameterNames.length; i++) {
			if (buf.length() > 0) {
				buf.append(lineDelimiter).append(lineStart);
			}
			buf.append("@param <").append(typeParameterNames[i]).append('>'); //$NON-NLS-1$
		}
		for (int i= 0; i < paramNames.length; i++) {
			if (buf.length() > 0) {
				buf.append(lineDelimiter).append(lineStart);
			}
			buf.append("@param ").append(paramNames[i]); //$NON-NLS-1$
		}
		if (returnType != null && !returnType.equals("void")) { //$NON-NLS-1$
			if (buf.length() > 0) {
				buf.append(lineDelimiter).append(lineStart);
			}			
			buf.append("@return"); //$NON-NLS-1$
		}
		if (exceptionNames != null) {
			for (int i= 0; i < exceptionNames.length; i++) {
				if (buf.length() > 0) {
					buf.append(lineDelimiter).append(lineStart);
				}
				buf.append("@throws ").append(exceptionNames[i]); //$NON-NLS-1$
			}
		}		
		if (isDeprecated) {
			if (buf.length() > 0) {
				buf.append(lineDelimiter).append(lineStart);
			}
			buf.append("@deprecated"); //$NON-NLS-1$
		}
		if (buf.length() == 0 && isAllCommentWhitespace(lineStart)) {
			int prevLine= textBuffer.getLineOfOffset(offset) -1;
			if (prevLine > 0) {
				IRegion prevRegion= textBuffer.getLineInformation(prevLine);
				int prevLineEnd= prevRegion.getOffset() + prevRegion.getLength();
				// clear full line
				textBuffer.replace(prevLineEnd, offset + length - prevLineEnd, ""); //$NON-NLS-1$
				return;
			}
		}
		textBuffer.replace(offset, length, buf.toString());
	}
	
	private static boolean isAllCommentWhitespace(String lineStart) {
		for (int i= 0; i < lineStart.length(); i++) {
			char ch= lineStart.charAt(i);
			if (!Character.isWhitespace(ch) && ch != '*') {
				return false;
			}
		}
		return true;
	}
		
	/**
	 * Returns the line delimiter which is used in the specified project.
	 * 
	 * @param project the java project, or <code>null</code>
	 * @return the used line delimiter
	 */
	public static String getLineDelimiterUsed(IJavaProject project) {
		return getProjectLineDelimiter(project);
	}

	private static String getProjectLineDelimiter(IJavaProject javaProject) {
		IProject project= null;
		if (javaProject != null)
			project= javaProject.getProject();
		
		String lineDelimiter= getLineDelimiterPreference(project);
		if (lineDelimiter != null)
			return lineDelimiter;
		
		return System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getLineDelimiterPreference(IProject project) {
		IScopeContext[] scopeContext;
		if (project != null) {
			// project preference
			scopeContext= new IScopeContext[] { new ProjectScope(project) };
			String lineDelimiter= Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);
			if (lineDelimiter != null)
				return lineDelimiter;
		}
		// workspace preference
		scopeContext= new IScopeContext[] { new InstanceScope() };
		String platformDefault= System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		return Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, platformDefault, scopeContext);
	}
	
	/**
	 * Examines a string and returns the first line delimiter found.
	 * @param elem the element
	 * @return the line delimiter used for the element
	 */
	public static String getLineDelimiterUsed(IJavaElement elem) {
		IOpenable openable= elem.getOpenable();
		if (openable instanceof ITypeRoot) {
			try {
				return openable.findRecommendedLineSeparator();
			} catch (JavaModelException exception) {
				// Use project setting
			}
		}
		IJavaProject project= elem.getJavaProject();
		return getProjectLineDelimiter(project.exists() ? project : null);
	}

	/**
	 * Evaluates the indentation used by a Java element. (in tabulators)
	 * @param elem the element to get the indent of
	 * @return return the indent unit
	 * @throws JavaModelException thrown if the element could not be accessed
	 */	
	public static int getIndentUsed(IJavaElement elem) throws JavaModelException {
		IOpenable openable= elem.getOpenable();
		if (openable instanceof ITypeRoot) {
			IBuffer buf= openable.getBuffer();
			if (buf != null) {
				int offset= ((ISourceReference)elem).getSourceRange().getOffset();
				return getIndentUsed(buf, offset, elem.getJavaProject());
			}
		}
		return 0;
	}
	
	public static int getIndentUsed(IBuffer buffer, int offset, IJavaProject project) {
		int i= offset;
		// find beginning of line
		while (i > 0 && !IndentManipulation.isLineDelimiterChar(buffer.getChar(i - 1)) ){
			i--;
		}
		return Strings.computeIndentUnits(buffer.getText(i, offset - i), project);
	}
	
	
		
	/**
	 * Returns the element after the give element.
	 * @param member a Java element
	 * @return  the next sibling of the given element or <code>null</code>
	 * @throws JavaModelException thrown if the element could not be accessed
	 */
	public static IJavaElement findNextSibling(IJavaElement member) throws JavaModelException {
		IJavaElement parent= member.getParent();
		if (parent instanceof IParent) {
			IJavaElement[] elements= ((IParent)parent).getChildren();
			for (int i= elements.length - 2; i >= 0 ; i--) {
				if (member.equals(elements[i])) {
					return elements[i+1];
				}
			}
		}
		return null;
	}
	
	public static String getTodoTaskTag(IJavaProject project) {
		String markers= null;
		if (project == null) {
			markers= JavaCore.getOption(JavaCore.COMPILER_TASK_TAGS);
		} else {
			markers= project.getOption(JavaCore.COMPILER_TASK_TAGS, true);
		}
		
		if (markers != null && markers.length() > 0) {
			int idx= markers.indexOf(',');
			if (idx == -1) {
				return markers;
			} else {
				return markers.substring(0, idx);
			}
		}
		return null;
	}
	
	private static String removeTypeArguments(String baseName) {
		int idx= baseName.indexOf('<');
		if (idx != -1) {
			return baseName.substring(0, idx);
		}
		return baseName;
	}
	
	
	// --------------------------- name suggestions --------------------------
	
	public static final int STATIC_FIELD= 1;
	public static final int INSTANCE_FIELD= 2;
	public static final int CONSTANT_FIELD= 3;
	public static final int PARAMETER= 4;
	public static final int LOCAL= 5;
	
	public static String[] getVariableNameSuggestions(int variableKind, IJavaProject project, ITypeBinding expectedType, Expression assignedExpression, Collection excluded) {
		LinkedHashSet res= new LinkedHashSet(); // avoid duplicates but keep order

		if (assignedExpression != null) {
			String nameFromExpression= getBaseNameFromExpression(project, assignedExpression, variableKind);
			if (nameFromExpression != null) {
				add(getVariableNameSuggestions(variableKind, project, nameFromExpression, 0, excluded, false), res); // pass 0 as dimension, base name already contains plural.
			}
			
			String nameFromParent= getBaseNameFromLocationInParent(assignedExpression);
			if (nameFromParent != null) {
				add(getVariableNameSuggestions(variableKind, project, nameFromParent, 0, excluded, false), res); // pass 0 as dimension, base name already contains plural.
			}
		}
		if (expectedType != null) {
			expectedType= Bindings.normalizeTypeBinding(expectedType);
			if (expectedType != null) {
				int dim= 0;
				if (expectedType.isArray()) {
					dim= expectedType.getDimensions();
					expectedType= expectedType.getElementType();
				}
				if (expectedType.isParameterizedType()) {
					expectedType= expectedType.getTypeDeclaration();
				}
				String typeName= expectedType.getQualifiedName();
				if (typeName.length() > 0) {
					String[] names= getVariableNameSuggestions(variableKind, project, typeName, dim, excluded, false);
					for (int i= 0; i < names.length; i++) {
						res.add(names[i]);
					}
				}
			}
		}
		if (res.isEmpty()) {
			return getDefaultVariableNameSuggestions(variableKind, excluded);
		}
		return (String[]) res.toArray(new String[res.size()]);
	}
	
	public static String[] getVariableNameSuggestions(int variableKind, IJavaProject project, Type expectedType, Expression assignedExpression, Collection excluded) {
		LinkedHashSet res= new LinkedHashSet(); // avoid duplicates but keep order

		if (assignedExpression != null) {
			String nameFromExpression= getBaseNameFromExpression(project, assignedExpression, variableKind);
			if (nameFromExpression != null) {
				add(getVariableNameSuggestions(variableKind, project, nameFromExpression, 0, excluded, false), res); // pass 0 as dimension, base name already contains plural.
			}
			
			String nameFromParent= getBaseNameFromLocationInParent(assignedExpression);
			if (nameFromParent != null) {
				add(getVariableNameSuggestions(variableKind, project, nameFromParent, 0, excluded, false), res); // pass 0 as dimension, base name already contains plural.
			}
		}
		if (expectedType != null) {			
			int dim= 0;
			if (expectedType.isArrayType()) {
				ArrayType arrayType= (ArrayType) expectedType;
				dim= arrayType.getDimensions();
				expectedType= arrayType.getElementType();
			}
			if (expectedType.isParameterizedType()) {
				expectedType= ((ParameterizedType) expectedType).getType();
			}
			String typeName= ASTNodes.asString(expectedType);
			
			if (typeName.length() > 0) {
				String[] names= getVariableNameSuggestions(variableKind, project, typeName, dim, excluded, false);
				for (int i= 0; i < names.length; i++) {
					res.add(names[i]);
				}
			}
		}
		if (res.isEmpty()) {
			return getDefaultVariableNameSuggestions(variableKind, excluded);
		}
		return (String[]) res.toArray(new String[res.size()]);
	}
	
	private static String[] getDefaultVariableNameSuggestions(int variableKind, Collection excluded) {
		String prop= variableKind == CONSTANT_FIELD ? "X" : "x";  //$NON-NLS-1$//$NON-NLS-2$
		String name= prop;
		int i= 1;
		while (excluded.contains(name)) {
			name= prop + i++;
		}
		return new String[] { name };
	}
	
	/**
	 * Returns variable name suggestions for the given base name. This is a layer over the JDT.Core NamingConventions API to fix its shortcomings. JDT UI code should only use this
	 * API.
	 * @param variableKind Specifies what type the variable is: {@link #LOCAL}, {@link #PARAMETER}, {@link #STATIC_FIELD}, {@link #INSTANCE_FIELD} or {@link #CONSTANT_FIELD}.
 	 * @param project the current project
 	 * @param baseName the base name to make a suggestion on. the base name is expected to be a name without any pre- or suffixes in singular form. Type name are accepted as well.
 	 * @param dimensions if greater than 0, the resulting name will be in plural form
 	 * @param excluded a collection containing all excluded names or <code>null</code> if no names are excluded
 	 * @param evaluateDefault if set, the result is guaranteed to contain at least one result. If not, the result can be an empty array. 
	 * 
	 * @return returns the name suggestions sorted by relevance (best proposal first). If <code>evaluateDefault</code> is set to true, the returned array is never empty.
	 * If <code>evaluateDefault</code> is set to false, an empty array is returned if there is no good suggestion for the given base name.
	 */
	public static String[] getVariableNameSuggestions(int variableKind, IJavaProject project, String baseName, int dimensions, Collection excluded, boolean evaluateDefault) {
		String name= workaround38111(baseName);
		name= removeTypeArguments(name);
		String packageName= new String(); // not used, so don't compute for now
		String[] result= null;
		
		switch (variableKind) {
			case CONSTANT_FIELD:
				result= getConstantSuggestions(project, name, excluded);
				break;
			case STATIC_FIELD:
				result= sortByLength(NamingConventions.suggestFieldNames(project, packageName, name, dimensions, Flags.AccStatic, getExcludedArray(excluded)));
				break;
			case INSTANCE_FIELD:
				result= sortByLength(NamingConventions.suggestFieldNames(project, packageName, name, dimensions, 0, getExcludedArray(excluded)));
				break;
			case PARAMETER:
				result= sortByLength(NamingConventions.suggestArgumentNames(project, packageName, name, dimensions, getExcludedArray(excluded)));
				break;
			case LOCAL:
				result= sortByLength(NamingConventions.suggestLocalVariableNames(project, packageName, name, dimensions, getExcludedArray(excluded)));
				break;
		}
		if (evaluateDefault) {
			if (result.length == 0) {
				result= getDefaultVariableNameSuggestions(variableKind, excluded);
			}
		} else if (variableKind != CONSTANT_FIELD) {
			 // see 166464 API DCR: specify if naming convention should return default value or not
			String defaultValue= "NAME"; // default as chosen by jdt.core //$NON-NLS-1$
			if (!name.toUpperCase().endsWith(defaultValue) && result[0].toUpperCase().endsWith(defaultValue)) {
				return new String[0];
			}
		}
		return result;
	}
	
	private static String[] getExcludedArray(Collection excluded) {
		if (excluded == null) {
			return null;
		} else if (excluded instanceof ExcludedCollection) {
			return ((ExcludedCollection) excluded).getExcludedArray();
		}
		return (String[]) excluded.toArray(new String[excluded.size()]);
	}
	
	
	private static final String[] KNOWN_METHOD_NAME_PREFIXES= { "get", "is", "to"}; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$
	
	
	private static void add(String[] names, Set result) {
		for (int i= 0; i < names.length; i++) {
			result.add(names[i]);
		}
	}
	
	private static String getBaseNameFromExpression(IJavaProject project, Expression assignedExpression, int variableKind) {
		String name= null;
		if (assignedExpression instanceof CastExpression) {
			assignedExpression= ((CastExpression) assignedExpression).getExpression();
		}
		if (assignedExpression instanceof Name) {
			Name simpleNode= (Name) assignedExpression;
			IBinding binding= simpleNode.resolveBinding();
			if (binding instanceof IVariableBinding)
				return removePrefixAndSuffixForVariable(project, (IVariableBinding) binding);
			
			return ASTNodes.getSimpleNameIdentifier(simpleNode);
		} else if (assignedExpression instanceof MethodInvocation) {
			name= ((MethodInvocation) assignedExpression).getName().getIdentifier();
		} else if (assignedExpression instanceof SuperMethodInvocation) {
			name= ((SuperMethodInvocation) assignedExpression).getName().getIdentifier();
		} else if (assignedExpression instanceof FieldAccess) {
			return ((FieldAccess) assignedExpression).getName().getIdentifier();
		} else if (variableKind == CONSTANT_FIELD && (assignedExpression instanceof StringLiteral || assignedExpression instanceof NumberLiteral)) {
			String string= assignedExpression instanceof StringLiteral ? ((StringLiteral) assignedExpression).getLiteralValue() : ((NumberLiteral) assignedExpression).getToken();
			StringBuffer res= new StringBuffer();
			boolean needsUnderscore= false;
			for (int i= 0; i < string.length(); i++) {
				char ch= string.charAt(i);
				if (Character.isJavaIdentifierPart(ch)) {
					if (res.length() == 0 && !Character.isJavaIdentifierStart(ch) || needsUnderscore) {
						res.append('_');
					}
					res.append(ch);
					needsUnderscore= false;
				} else {
					needsUnderscore= res.length() > 0;
				}
			}
			if (res.length() > 0) {
				return res.toString();
			}
		}
		if (name != null) {
			for (int i= 0; i < KNOWN_METHOD_NAME_PREFIXES.length; i++) {
				String curr= KNOWN_METHOD_NAME_PREFIXES[i];
				if (name.startsWith(curr)) {
					if (name.equals(curr)) {
						return null; // don't suggest 'get' as variable name
					} else if (Character.isUpperCase(name.charAt(curr.length()))) {
						return name.substring(curr.length());
					}
				}
			}
		}
		return name;
	}
	
	private static String getBaseNameFromLocationInParent(Expression assignedExpression, List arguments, IMethodBinding binding) {
		if (binding == null)
			return null;
		
		ITypeBinding[] parameterTypes= binding.getParameterTypes();
		if (parameterTypes.length != arguments.size()) // beware of guessed method bindings
			return null;

		int index= arguments.indexOf(assignedExpression);
		if (index == -1)
			return null;

		ITypeBinding expressionBinding= assignedExpression.resolveTypeBinding();
		if (expressionBinding != null && !expressionBinding.isAssignmentCompatible(parameterTypes[index]))
			return null;

		try {
			IJavaElement javaElement= binding.getJavaElement();
			if (javaElement instanceof IMethod) {
				IMethod method= (IMethod) javaElement;
				if (method.getOpenable().getBuffer() != null) { // avoid dummy names and lookup from Javadoc
					String[] parameterNames= method.getParameterNames();
					if (index < parameterNames.length) {
						return NamingConventions.removePrefixAndSuffixForArgumentName(method.getJavaProject(), parameterNames[index]);
					}
				}
			}
		} catch (JavaModelException e) {
			// ignore
		}
		return null;
	}
	
	
	private static String getBaseNameFromLocationInParent(Expression assignedExpression) {
		StructuralPropertyDescriptor location= assignedExpression.getLocationInParent();
		if (location == MethodInvocation.ARGUMENTS_PROPERTY) {
			MethodInvocation parent= (MethodInvocation) assignedExpression.getParent();
			return getBaseNameFromLocationInParent(assignedExpression, parent.arguments(), parent.resolveMethodBinding());
		} else if (location == ClassInstanceCreation.ARGUMENTS_PROPERTY) {
			ClassInstanceCreation parent= (ClassInstanceCreation) assignedExpression.getParent();
			return getBaseNameFromLocationInParent(assignedExpression, parent.arguments(), parent.resolveConstructorBinding());
		} else if (location == SuperMethodInvocation.ARGUMENTS_PROPERTY) {
			SuperMethodInvocation parent= (SuperMethodInvocation) assignedExpression.getParent();
			return getBaseNameFromLocationInParent(assignedExpression, parent.arguments(), parent.resolveMethodBinding());
		} else if (location == ConstructorInvocation.ARGUMENTS_PROPERTY) {
			ConstructorInvocation parent= (ConstructorInvocation) assignedExpression.getParent();
			return getBaseNameFromLocationInParent(assignedExpression, parent.arguments(), parent.resolveConstructorBinding());
		} else if (location == SuperConstructorInvocation.ARGUMENTS_PROPERTY) {
			SuperConstructorInvocation parent= (SuperConstructorInvocation) assignedExpression.getParent();
			return getBaseNameFromLocationInParent(assignedExpression, parent.arguments(), parent.resolveConstructorBinding());
		}
		return null;
	}
		
	public static String[] getArgumentNameSuggestions(IType type, String[] excluded) {
		return getVariableNameSuggestions(PARAMETER, type.getJavaProject(), type.getFullyQualifiedName('.'), 0, new ExcludedCollection(excluded), true);
	}
	
	public static String[] getArgumentNameSuggestions(IJavaProject project, Type type, String[] excluded) {
		int dim= 0;
		if (type.isArrayType()) {
			ArrayType arrayType= (ArrayType) type;
			dim= arrayType.getDimensions();
			type= arrayType.getElementType();
		}
		if (type.isParameterizedType()) {
			type= ((ParameterizedType) type).getType();
		}
		return getVariableNameSuggestions(PARAMETER, project, ASTNodes.asString(type), dim, new ExcludedCollection(excluded), true);
	}
	
	public static String[] getArgumentNameSuggestions(IJavaProject project, ITypeBinding binding, String[] excluded) {
		return getVariableNameSuggestions(PARAMETER, project, binding, null, new ExcludedCollection(excluded));
	}
		
	public static String[] getArgumentNameSuggestions(IJavaProject project, String baseName, int dimensions, String[] excluded) {
		return getVariableNameSuggestions(PARAMETER, project, baseName, dimensions, new ExcludedCollection(excluded), true);
	}
	
	public static String[] getFieldNameSuggestions(IType type, int fieldModifiers, String[] excluded) {		
		return getFieldNameSuggestions(type.getJavaProject(), type.getFullyQualifiedName('.'), 0, fieldModifiers, excluded);
	}
		 
	public static String[] getFieldNameSuggestions(IJavaProject project, String baseName, int dimensions, int modifiers, String[] excluded) {
		if (Flags.isFinal(modifiers) && Flags.isStatic(modifiers)) {
			return getVariableNameSuggestions(CONSTANT_FIELD, project, baseName, dimensions, new ExcludedCollection(excluded), true);
		} else if (Flags.isStatic(modifiers)) {
			return getVariableNameSuggestions(STATIC_FIELD, project, baseName, dimensions, new ExcludedCollection(excluded), true);
		}
		return getVariableNameSuggestions(INSTANCE_FIELD, project, baseName, dimensions, new ExcludedCollection(excluded), true);
	}

	private static String[] getConstantSuggestions(IJavaProject project, String typeName, Collection excluded) {
		//TODO: workaround JDT/Core bug 85946
		
		String string= Signature.getSimpleName(typeName);

		StringBuffer buf= new StringBuffer();
		boolean wasUpperCase= true;
		for (int i= 0; i < string.length() ; i++) {
			char ch= string.charAt(i);
			if (Character.isUpperCase(ch)) {
				if (!wasUpperCase) {
					buf.append('_');
				}
				buf.append(ch);
			} else {
				buf.append(Character.toUpperCase(ch));
				wasUpperCase= ch == '_'; // avoid duplicate underscores
			}
		}
		ArrayList res= new ArrayList();
		String sourceLevel= project.getOption(JavaCore.COMPILER_SOURCE, true);
		String complianceLevel= project.getOption(JavaCore.COMPILER_COMPLIANCE, true);
		
		boolean nameStarts= true;
		for (int i= 0; i < buf.length(); i++) {
			if (nameStarts) {
				String prop= buf.substring(i);
				if (!excluded.contains(prop) && JavaConventions.validateFieldName(prop, sourceLevel, complianceLevel).isOK()) {
					res.add(prop);
				}
			}
			char ch= buf.charAt(i);
			nameStarts= ch == '_';
		}
		return (String[]) res.toArray(new String[res.size()]);
	}
	
	private static String getCamelCaseFromUpper(String string) {
		StringBuffer result= new StringBuffer();
		boolean lastWasUnderscore= false;
		for (int i= 0; i < string.length(); i++) {
			char ch= string.charAt(i);
			if (Character.isUpperCase(ch)) {
				if (!lastWasUnderscore) {
					ch= Character.toLowerCase(ch);
				}
				result.append(ch);
				lastWasUnderscore= false;
			} else if (ch == '_') {
				lastWasUnderscore= true;
			} else {
				return string; // abort
			}
		}
		return result.toString();
	}
	
	public static String[] getLocalNameSuggestions(IJavaProject project, String baseName, int dimensions, String[] excluded) {
		return getVariableNameSuggestions(LOCAL, project, baseName, dimensions, new ExcludedCollection(excluded), true);
	}
	
	private static String[] sortByLength(String[] proposals) {
		Arrays.sort(proposals, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((String) o2).length() - ((String) o1).length();
			}
		});
		return proposals;
	}
	
	private static String workaround38111(String baseName) {
		if (BASE_TYPES.contains(baseName))
			return baseName;
		return Character.toUpperCase(baseName.charAt(0)) + baseName.substring(1);
	}
	
	private static final List BASE_TYPES= Arrays.asList(
			new String[] {"boolean", "byte", "char", "double", "float", "int", "long", "short"});  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

	public static String suggestArgumentName(IJavaProject project, String baseName, String[] excluded) {
		return suggestVariableName(PARAMETER, project, baseName, 0, excluded);
	}
	
	private static String suggestVariableName(int varKind, IJavaProject project, String baseName, int dimension, String[] excluded) {
		return getVariableNameSuggestions(varKind, project, baseName, dimension, new ExcludedCollection(excluded), true)[0];
	}
	
	
	public static String[][] suggestArgumentNamesWithProposals(IJavaProject project, String[] paramNames) {
		String[][] newNames= new String[paramNames.length][];
		ArrayList takenNames= new ArrayList();
		
		// Ensure that the code generation preferences are respected
		for (int i= 0; i < paramNames.length; i++) {
			String curr= paramNames[i];
			String baseName= NamingConventions.removePrefixAndSuffixForArgumentName(project, curr);

			String[] proposedNames= getVariableNameSuggestions(PARAMETER, project, curr, 0, takenNames, true);
			if (!curr.equals(baseName)) {
				// make the existing name to favourite
				LinkedHashSet updatedNames= new LinkedHashSet();
				updatedNames.add(curr);
				for (int k= 0; k < proposedNames.length; k++) {
					updatedNames.add(proposedNames[k]);
				}
				proposedNames= (String[]) updatedNames.toArray(new String[updatedNames.size()]);
			}
			newNames[i]= proposedNames;
			takenNames.add(proposedNames[0]);
		}
		return newNames;
	}
	
	public static String[][] suggestArgumentNamesWithProposals(IJavaProject project, IMethodBinding binding) {
		int nParams= binding.getParameterTypes().length;
		if (nParams > 0) {
			try {
				IMethod method= (IMethod) binding.getMethodDeclaration().getJavaElement();
				if (method != null) {
					String[] parameterNames= method.getParameterNames();
					if (parameterNames.length == nParams) {
						return suggestArgumentNamesWithProposals(project, parameterNames);
					}
				}
			} catch (JavaModelException e) {
				// ignore
			}
		}
		String[][] names= new String[nParams][];
		for (int i= 0; i < names.length; i++) {
			names[i]= new String[] { "arg" + i }; //$NON-NLS-1$
		}
		return names;
	}
	
	
	public static String[] suggestArgumentNames(IJavaProject project, IMethodBinding binding) {
		int nParams= binding.getParameterTypes().length;

		if (nParams > 0) {
			try {
				IMethod method= (IMethod) binding.getMethodDeclaration().getJavaElement();
				if (method != null) {
					String[] paramNames= method.getParameterNames();
					if (paramNames.length == nParams) {
						String[] namesArray= new String[0];
						ArrayList newNames= new ArrayList(paramNames.length);
						// Ensure that the code generation preferences are respected
						for (int i= 0; i < paramNames.length; i++) {
							String curr= paramNames[i];
							String baseName= NamingConventions.removePrefixAndSuffixForArgumentName(project, curr);
							if (!curr.equals(baseName)) {
								// make the existing name the favourite
								newNames.add(curr);
							} else {
								newNames.add(suggestArgumentName(project, curr, namesArray));
							}
							namesArray= (String[]) newNames.toArray(new String[newNames.size()]);
						}
						return namesArray;
					}
				}
			} catch (JavaModelException e) {
				// ignore
			}
		}
		String[] names= new String[nParams];
		for (int i= 0; i < names.length; i++) {
			names[i]= "arg" + i; //$NON-NLS-1$
		}
		return names;
	}
	
	public static String removePrefixAndSuffixForVariable(IJavaProject project, IVariableBinding binding) {
		if (binding.isEnumConstant()) {
			return binding.getName();
		} else if (binding.isField()) {
			if (Modifier.isStatic(binding.getModifiers()) && Modifier.isFinal(binding.getModifiers())) {
				return getCamelCaseFromUpper(binding.getName());
			} else {
				return NamingConventions.removePrefixAndSuffixForFieldName(project, binding.getName(), binding.getModifiers());
			}
		} else if (binding.isParameter()) {
			return NamingConventions.removePrefixAndSuffixForArgumentName(project, binding.getName());
		} else {
			return NamingConventions.removePrefixAndSuffixForLocalVariableName(project, binding.getName());
		}
	}
	
    private static class ExcludedCollection extends AbstractList {
		private String[] fExcluded;
		public ExcludedCollection(String[] excluded) {
			fExcluded = excluded;
		}
		public String[] getExcludedArray() {
			return fExcluded;
		}
		public int size() {
			return fExcluded.length;
		}
		public Object get(int index) {
			return fExcluded[index];
		}
		public int indexOf(Object o) {
			if (o instanceof String) {
				for (int i= 0; i < fExcluded.length; i++) {
			         if (o.equals(fExcluded[i]))
			             return i;
				}
			}
			return -1;
		}
		public boolean contains(Object o) {
			return indexOf(o) != -1;
		}
    }
	
	
	public static boolean hasFieldName(IJavaProject project, String name) {
		String prefixes= project.getOption(JavaCore.CODEASSIST_FIELD_PREFIXES, true);
		String suffixes= project.getOption(JavaCore.CODEASSIST_FIELD_SUFFIXES, true);
		String staticPrefixes= project.getOption(JavaCore.CODEASSIST_STATIC_FIELD_PREFIXES, true);
		String staticSuffixes= project.getOption(JavaCore.CODEASSIST_STATIC_FIELD_SUFFIXES, true);
		
		
		return hasPrefixOrSuffix(prefixes, suffixes, name) 
			|| hasPrefixOrSuffix(staticPrefixes, staticSuffixes, name);
	}
	
	public static boolean hasParameterName(IJavaProject project, String name) {
		String prefixes= project.getOption(JavaCore.CODEASSIST_ARGUMENT_PREFIXES, true);
		String suffixes= project.getOption(JavaCore.CODEASSIST_ARGUMENT_SUFFIXES, true);
		return hasPrefixOrSuffix(prefixes, suffixes, name);
	}
	
	public static boolean hasLocalVariableName(IJavaProject project, String name) {
		String prefixes= project.getOption(JavaCore.CODEASSIST_LOCAL_PREFIXES, true);
		String suffixes= project.getOption(JavaCore.CODEASSIST_LOCAL_SUFFIXES, true);
		return hasPrefixOrSuffix(prefixes, suffixes, name);
	}
	
	public static boolean hasConstantName(String name) {
		return Character.isUpperCase(name.charAt(0));
	}
	
	
	private static boolean hasPrefixOrSuffix(String prefixes, String suffixes, String name) {
		final String listSeparartor= ","; //$NON-NLS-1$

		StringTokenizer tok= new StringTokenizer(prefixes, listSeparartor);
		while (tok.hasMoreTokens()) {
			String curr= tok.nextToken();
			if (name.startsWith(curr)) {
				return true;
			}
		}

		tok= new StringTokenizer(suffixes, listSeparartor);
		while (tok.hasMoreTokens()) {
			String curr= tok.nextToken();
			if (name.endsWith(curr)) {
				return true;
			}
		}
		return false;
	}
	
	// -------------------- preference access -----------------------
	
	public static boolean useThisForFieldAccess(IJavaProject project) {
		return Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_KEYWORD_THIS, project)).booleanValue(); 
	}
	
	public static boolean useIsForBooleanGetters(IJavaProject project) {
		return Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_IS_FOR_GETTERS, project)).booleanValue(); 
	}
	
	public static String getExceptionVariableName(IJavaProject project) {
		return PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_EXCEPTION_VAR_NAME, project); 
	}
	
	public static boolean doAddComments(IJavaProject project) {
		return Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_ADD_COMMENTS, project)).booleanValue(); 
	}
	
	/**
	 * Only to be used by tests
	 * @param templateId the template id
	 * @param pattern the new pattern
	 * @param project not used
	 */
	public static void setCodeTemplate(String templateId, String pattern, IJavaProject project) {
		TemplateStore codeTemplateStore= JavaPlugin.getDefault().getCodeTemplateStore();
		TemplatePersistenceData data= codeTemplateStore.getTemplateData(templateId);
		Template orig= data.getTemplate();
		Template copy= new Template(orig.getName(), orig.getDescription(), orig.getContextTypeId(), pattern, true);
		data.setTemplate(copy);
	}
	
	private static Template getCodeTemplate(String id, IJavaProject project) {
		if (project == null)
			return JavaPlugin.getDefault().getCodeTemplateStore().findTemplateById(id);
		ProjectTemplateStore projectStore= new ProjectTemplateStore(project.getProject());
		try {
			projectStore.load();
		} catch (IOException e) {
			JavaPlugin.log(e);
		}
		return projectStore.findTemplateById(id);
	}
	

	public static ImportRewrite createImportRewrite(ICompilationUnit cu, boolean restoreExistingImports) throws JavaModelException {
		return CodeStyleConfiguration.createImportRewrite(cu, restoreExistingImports);
	}
	
	public static ImportRewrite createImportRewrite(CompilationUnit astRoot, boolean restoreExistingImports) {
		return CodeStyleConfiguration.createImportRewrite(astRoot, restoreExistingImports);
	}
	
}
