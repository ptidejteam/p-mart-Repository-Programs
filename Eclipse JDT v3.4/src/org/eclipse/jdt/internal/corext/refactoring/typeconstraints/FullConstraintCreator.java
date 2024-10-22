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
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.refactoring.rename.MethodChecks;

/**
 * Default implementation of the creator. Creates all or nearly all constraints for program constructs.
 * Subclasses can provide additional checks to avoid creating constraints that are not useful for their purposes.
 */
public class FullConstraintCreator extends ConstraintCreator{

	private final IConstraintVariableFactory fConstraintVariableFactory;
	private final ITypeConstraintFactory fTypeConstraintFactory;
	private IContext fContext;

	public FullConstraintCreator(){
		this(new ConstraintVariableFactory(), new TypeConstraintFactory());
	}
	
	public FullConstraintCreator(IConstraintVariableFactory cFactory,
								 ITypeConstraintFactory tFactory) {
		Assert.isTrue(cFactory != null);
		fConstraintVariableFactory= cFactory;
		fTypeConstraintFactory= tFactory;
		fContext= new NullContext();
	}

	public IContext getContext() {
		return fContext;
	}
	
	public void setContext(IContext context) {
		fContext= context;
	}
	
	public ITypeConstraintFactory getConstraintFactory(){
		return fTypeConstraintFactory;
	}
	
	public IConstraintVariableFactory getConstraintVariableFactory(){
		return fConstraintVariableFactory;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ArrayInitializer)
	 */
	public ITypeConstraint[] create(ArrayInitializer arrayInitializer){
		ITypeBinding arrayBinding= arrayInitializer.resolveTypeBinding();
		Assert.isTrue(arrayBinding.isArray());
		List expressions= arrayInitializer.expressions();
		List/*<ITypeConstraint>*/ constraints= new ArrayList();
		Type type= getTypeParent(arrayInitializer);
		ConstraintVariable typeVariable= fConstraintVariableFactory.makeTypeVariable(type);
		for (int i= 0; i < expressions.size(); i++) {
			Expression each= (Expression) expressions.get(i);
			ITypeConstraint[] c= fTypeConstraintFactory.createSubtypeConstraint(
					fConstraintVariableFactory.makeExpressionOrTypeVariable(each, getContext()),
					typeVariable);
			constraints.addAll(Arrays.asList(c));
		}		
		return (ITypeConstraint[])constraints.toArray(new ITypeConstraint[constraints.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Assignment)
	 */
	public ITypeConstraint[] create(Assignment assignment){
		return fTypeConstraintFactory.createSubtypeConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(assignment.getRightHandSide(), getContext()), 
				fConstraintVariableFactory.makeExpressionOrTypeVariable(assignment.getLeftHandSide(), getContext()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.CastExpression)
	 */
	public ITypeConstraint[] create(CastExpression castExpression){
		Expression expression= castExpression.getExpression();
		Type type= castExpression.getType();
		ITypeConstraint[] definesConstraint= fTypeConstraintFactory.createDefinesConstraint(fConstraintVariableFactory.makeExpressionOrTypeVariable(castExpression, getContext()), 
				                                                                        fConstraintVariableFactory.makeTypeVariable(castExpression.getType()));
		if (isClassBinding(expression.resolveTypeBinding()) && isClassBinding(type.resolveBinding())){
			ConstraintVariable expressionVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(expression, getContext());
			ConstraintVariable castExpressionVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(castExpression, getContext());
			ITypeConstraint[] c2 = createOrOrSubtypeConstraint(expressionVariable, castExpressionVariable);
			if (definesConstraint.length == 0){
				return c2;
			} else {
				ITypeConstraint c1 = definesConstraint[0];
				Collection/*<ITypeConstraint>*/ constraints= new ArrayList();
				constraints.add(c1);
				constraints.addAll(Arrays.asList(c2));
				return (ITypeConstraint[])constraints.toArray(new ITypeConstraint[constraints.size()]);
			}
		} else 
			return definesConstraint;
	}
	
	public ITypeConstraint[] create(CatchClause node) {
		SingleVariableDeclaration exception= node.getException();
		ConstraintVariable nameVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(exception.getName(), getContext());
		
		ITypeConstraint[] defines= fTypeConstraintFactory.createDefinesConstraint(
				nameVariable,
				fConstraintVariableFactory.makeTypeVariable(exception.getType()));
		
		ITypeBinding throwable= node.getAST().resolveWellKnownType("java.lang.Throwable"); //$NON-NLS-1$
		ITypeConstraint[] catchBound= fTypeConstraintFactory.createSubtypeConstraint(
				nameVariable,
				fConstraintVariableFactory.makeRawBindingVariable(throwable));
		
		ArrayList result= new ArrayList();
		result.addAll(Arrays.asList(defines));
		result.addAll(Arrays.asList(catchBound));
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);		
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ClassInstanceCreation)
	 */
	public ITypeConstraint[] create(ClassInstanceCreation instanceCreation){
		List arguments= instanceCreation.arguments();
		List result= new ArrayList(arguments.size());
		IMethodBinding methodBinding= instanceCreation.resolveConstructorBinding();
		result.addAll(Arrays.asList(getArgumentConstraints(arguments, methodBinding)));
		if (instanceCreation.getAnonymousClassDeclaration() == null){
			ConstraintVariable constructorVar= fConstraintVariableFactory.makeExpressionOrTypeVariable(instanceCreation, getContext());
			ConstraintVariable typeVar= fConstraintVariableFactory.makeRawBindingVariable(instanceCreation.resolveTypeBinding());
			result.addAll(Arrays.asList(fTypeConstraintFactory.createDefinesConstraint(constructorVar, typeVar)));
		}
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ConstructorInvocation)
	 */
	public ITypeConstraint[] create(ConstructorInvocation invocation){
		List arguments= invocation.arguments();
		List result= new ArrayList(arguments.size());
		IMethodBinding methodBinding= invocation.resolveConstructorBinding();
		result.addAll(Arrays.asList(getArgumentConstraints(arguments, methodBinding)));
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldAccess)
	 */
	public ITypeConstraint[] create(FieldAccess access){
		Expression expression= access.getExpression();
		SimpleName name= access.getName();
		IBinding binding= name.resolveBinding();
		if (! (binding instanceof IVariableBinding))
			return new ITypeConstraint[0];	
		IVariableBinding vb= (IVariableBinding)binding;
		return createConstraintsForAccessToField(vb, expression, access);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
	 */
	public ITypeConstraint[] create(FieldDeclaration fd){
		List result= new ArrayList();
		result.addAll(Arrays.asList(getConstraintsFromFragmentList(fd.fragments(), fd.getType())));
		result.addAll(getConstraintsForHiding(fd));
		result.addAll(getConstraintsForFieldDeclaringTypes(fd));
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.InstanceofExpression)
	 */
	public ITypeConstraint[] create(InstanceofExpression instanceofExpression){
		Expression expression= instanceofExpression.getLeftOperand();
		Type type= instanceofExpression.getRightOperand();
		if (isClassBinding(expression.resolveTypeBinding()) && isClassBinding(type.resolveBinding())) {
			ConstraintVariable expressionVar= fConstraintVariableFactory.makeExpressionOrTypeVariable(expression, getContext());
			ConstraintVariable typeVariable= fConstraintVariableFactory.makeTypeVariable(type);
			return createOrOrSubtypeConstraint(expressionVar, typeVariable);
		} else
			return new ITypeConstraint[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ConditionalExpression)
	 */
	public ITypeConstraint[] create(ConditionalExpression node) {
		List result= new ArrayList();
		Expression thenExpression= node.getThenExpression();
		Expression elseExpression= node.getElseExpression();
		ConstraintVariable whole= fConstraintVariableFactory.makeExpressionOrTypeVariable(node, getContext());
		ConstraintVariable ev1= fConstraintVariableFactory.makeExpressionOrTypeVariable(thenExpression, getContext());
		ConstraintVariable ev2= fConstraintVariableFactory.makeExpressionOrTypeVariable(elseExpression, getContext());
		ITypeConstraint[] constraints1= fTypeConstraintFactory.createEqualsConstraint(ev1, ev2);
		ITypeConstraint[] constraints2= fTypeConstraintFactory.createSubtypeConstraint(ev1, whole);
		ITypeConstraint[] constraints3= fTypeConstraintFactory.createSubtypeConstraint(ev2, whole);
		result.addAll(Arrays.asList(constraints1));
		result.addAll(Arrays.asList(constraints2));
		result.addAll(Arrays.asList(constraints3));
		return (ITypeConstraint[])result.toArray(new ITypeConstraint[result.size()]);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	public ITypeConstraint[] create(MethodDeclaration declaration){
		List result= new ArrayList();
		IMethodBinding methodBinding= declaration.resolveBinding();
		if (methodBinding == null)
			return new ITypeConstraint[0];
		ITypeConstraint[] constraints = fTypeConstraintFactory.createDefinesConstraint(
				fConstraintVariableFactory.makeDeclaringTypeVariable(methodBinding),
				fConstraintVariableFactory.makeRawBindingVariable(methodBinding.getDeclaringClass()));
		result.addAll(Arrays.asList(constraints));
		if (! methodBinding.isConstructor() && ! methodBinding.getReturnType().isPrimitive()){
			ConstraintVariable returnTypeBindingVariable= fConstraintVariableFactory.makeReturnTypeVariable(methodBinding);
			ConstraintVariable returnTypeVariable= fConstraintVariableFactory.makeTypeVariable(declaration.getReturnType2());
			ITypeConstraint[] defines= fTypeConstraintFactory.createDefinesConstraint(
					returnTypeBindingVariable, returnTypeVariable);
			result.addAll(Arrays.asList(defines));
		}
		for (int i= 0, n= declaration.parameters().size(); i < n; i++) {
			SingleVariableDeclaration paramDecl= (SingleVariableDeclaration)declaration.parameters().get(i);
			ConstraintVariable parameterTypeVariable= fConstraintVariableFactory.makeParameterTypeVariable(methodBinding, i);
			ConstraintVariable parameterNameVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(paramDecl.getName(), getContext());
			ITypeConstraint[] constraint= fTypeConstraintFactory.createDefinesConstraint(
					parameterTypeVariable, parameterNameVariable);
			result.addAll(Arrays.asList(constraint));
		}
		if (MethodChecks.isVirtual(methodBinding)){
			Collection constraintsForOverriding = getConstraintsForOverriding(methodBinding);
			result.addAll(constraintsForOverriding);
		}
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.ConstraintCreator#create(org.eclipse.jdt.core.dom.ParenthesizedExpression)
	 */
	public ITypeConstraint[] create(ParenthesizedExpression node) {
		ConstraintVariable v1= fConstraintVariableFactory.makeExpressionOrTypeVariable(node, getContext());
		ConstraintVariable v2= fConstraintVariableFactory.makeExpressionOrTypeVariable(node.getExpression(), getContext());
		ITypeConstraint[] equal= fTypeConstraintFactory.createEqualsConstraint(v1, v2);
		return equal;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodInvocation)
	 */
	public ITypeConstraint[] create(MethodInvocation invocation){
		List arguments= invocation.arguments();
		List result= new ArrayList(arguments.size());
		IMethodBinding methodBinding= invocation.resolveMethodBinding();
		if (methodBinding == null)
			return new ITypeConstraint[0];
		ITypeConstraint[] returnTypeConstraint= getReturnTypeConstraint(invocation, methodBinding);
		result.addAll(Arrays.asList(returnTypeConstraint));
		result.addAll(Arrays.asList(getArgumentConstraints(arguments, methodBinding)));
		if (invocation.getExpression() != null){
			if(MethodChecks.isVirtual(methodBinding)){
				IMethodBinding[] rootDefs= getRootDefs(methodBinding);		
				Assert.isTrue(rootDefs.length > 0);
				ConstraintVariable expressionVar= fConstraintVariableFactory.makeExpressionOrTypeVariable(invocation.getExpression(), getContext());
				if (rootDefs.length == 1){
					result.addAll(Arrays.asList(fTypeConstraintFactory.createSubtypeConstraint(expressionVar, fConstraintVariableFactory.makeDeclaringTypeVariable(rootDefs[0]))));
				}else{	
					Collection/*<ITypeConstraint>*/ constraints= new ArrayList(); 
					for (int i= 0; i < rootDefs.length; i++) {
						ConstraintVariable rootDefTypeVar= fConstraintVariableFactory.makeDeclaringTypeVariable(rootDefs[i]);
						ITypeConstraint[] tc= fTypeConstraintFactory.createSubtypeConstraint(expressionVar, rootDefTypeVar);
						constraints.addAll(Arrays.asList(tc));
					}
					ITypeConstraint[] constraintsArray= (ITypeConstraint[])constraints.toArray(new ITypeConstraint[constraints.size()]);
					if (constraintsArray.length > 0){
						result.add(fTypeConstraintFactory.createCompositeOrTypeConstraint(constraintsArray));
					}
				}
			} else {
				ConstraintVariable typeVar= fConstraintVariableFactory.makeDeclaringTypeVariable(methodBinding);
				ConstraintVariable expressionVar= fConstraintVariableFactory.makeExpressionOrTypeVariable(invocation.getExpression(), getContext());
				result.addAll(Arrays.asList(fTypeConstraintFactory.createSubtypeConstraint(expressionVar, typeVar)));
			}
		}
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.QualifiedName)
	 */
	public ITypeConstraint[] create(QualifiedName qualifiedName){
		SimpleName name= qualifiedName.getName();
		Name qualifier= qualifiedName.getQualifier();
		IBinding nameBinding= name.resolveBinding();
		if (nameBinding instanceof IVariableBinding){
			IVariableBinding vb= (IVariableBinding)nameBinding;
			if (vb.isField())
				return createConstraintsForAccessToField(vb, qualifier, qualifiedName);
		} //TODO other bindings 
		return new ITypeConstraint[0];			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ReturnStatement)
	 */
	public ITypeConstraint[] create(ReturnStatement returnStatement){
		if (returnStatement.getExpression() == null)
			return new ITypeConstraint[0];
		
		ConstraintVariable returnTypeVariable= fConstraintVariableFactory.makeReturnTypeVariable(returnStatement);
		return fTypeConstraintFactory.createSubtypeConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(returnStatement.getExpression(), getContext()),
				returnTypeVariable);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SingleVariableDeclaration)
	 */
	public ITypeConstraint[] create(SingleVariableDeclaration svd){
		ITypeConstraint[] defines= fTypeConstraintFactory.createDefinesConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(svd.getName(), getContext()),
				fConstraintVariableFactory.makeTypeVariable(svd.getType()));
		if (svd.getInitializer() == null)
			return defines;	
		ITypeConstraint[] constraints = fTypeConstraintFactory.createSubtypeConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(svd.getInitializer(), getContext()),
				fConstraintVariableFactory.makeExpressionOrTypeVariable(svd.getName(), getContext()));
		if (defines.length == 0 && constraints.length == 0){
			return new ITypeConstraint[0];
		} else if (defines.length == 0){
			return constraints;
		} else if (constraints.length == 0){
			return defines;
		} else {
			List all= new ArrayList();
			all.addAll(Arrays.asList(defines));
			all.addAll(Arrays.asList(constraints));
			return (ITypeConstraint[])all.toArray();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperConstructorInvocation)
	 */
	public ITypeConstraint[] create(SuperConstructorInvocation invocation){
		List arguments= invocation.arguments();
		List result= new ArrayList(arguments.size());
		IMethodBinding methodBinding= invocation.resolveConstructorBinding();
		result.addAll(Arrays.asList(getArgumentConstraints(arguments, methodBinding)));
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperFieldAccess)
	 */
	public ITypeConstraint[] create(SuperFieldAccess access){
		SimpleName name= access.getName();
		IBinding binding= name.resolveBinding();
		if (! (binding instanceof IVariableBinding))
			return new ITypeConstraint[0];	
		IVariableBinding vb= (IVariableBinding)binding;
		return createConstraintsForAccessToField(vb, null, access);
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.SuperMethodInvocation)
	 */
	public ITypeConstraint[] create(SuperMethodInvocation invocation){
		List arguments= invocation.arguments();
		List result= new ArrayList(arguments.size());
		IMethodBinding methodBinding= invocation.resolveMethodBinding();
		ITypeConstraint[] returnTypeConstraint= getReturnTypeConstraint(invocation, methodBinding);
		result.addAll(Arrays.asList(returnTypeConstraint));
		result.addAll(Arrays.asList(getArgumentConstraints(arguments, methodBinding)));
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.ThisExpression)
	 */
	public ITypeConstraint[] create(ThisExpression expression){
		ConstraintVariable thisExpression= fConstraintVariableFactory.makeExpressionOrTypeVariable(expression, getContext());
		ConstraintVariable declaringType= fConstraintVariableFactory.makeRawBindingVariable(expression.resolveTypeBinding());//TODO fix this - can't use Decl(M) because 'this' can live outside of methods
		return fTypeConstraintFactory.createDefinesConstraint(thisExpression, declaringType);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationExpression)
	 */
	public ITypeConstraint[] create(VariableDeclarationExpression vde){
		return getConstraintsFromFragmentList(vde.fragments(), vde.getType());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationFragment)
	 */
	public ITypeConstraint[] create(VariableDeclarationFragment vdf){
		if (vdf.getInitializer() == null)
			return new ITypeConstraint[0];	
		return fTypeConstraintFactory.createSubtypeConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(vdf.getInitializer(), getContext()),
				fConstraintVariableFactory.makeExpressionOrTypeVariable(vdf.getName(), getContext()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.VariableDeclarationStatement)
	 */
	public ITypeConstraint[] create(VariableDeclarationStatement vds){
		return getConstraintsFromFragmentList(vds.fragments(), vds.getType());
	}


	//--------- private helpers ----------------//
	
	private Collection getConstraintsForFieldDeclaringTypes(FieldDeclaration fd) {
		Collection result= new ArrayList(fd.fragments().size());
		for (Iterator iter= fd.fragments().iterator(); iter.hasNext();) {
			VariableDeclarationFragment varDecl= (VariableDeclarationFragment) iter.next();
			IVariableBinding binding= varDecl.resolveBinding();
			Assert.isTrue(binding.isField());
			result.addAll(Arrays.asList(fTypeConstraintFactory.createDefinesConstraint(
					fConstraintVariableFactory.makeDeclaringTypeVariable(binding),
					fConstraintVariableFactory.makeRawBindingVariable(binding.getDeclaringClass()))));
		}
		return result;
	}
	
	private Collection getConstraintsForHiding(FieldDeclaration fd) {
		Collection result= new ArrayList();
		for (Iterator iter= fd.fragments().iterator(); iter.hasNext();) {
			result.addAll(getConstraintsForHiding((VariableDeclarationFragment) iter.next()));
		}
		return result;
	}
	
	private Collection getConstraintsForHiding(VariableDeclarationFragment fragment) {
		Collection result= new ArrayList();
		IVariableBinding fieldBinding= fragment.resolveBinding();
		Assert.isTrue(fieldBinding.isField());
		Set declaringTypes= getDeclaringSuperTypes(fieldBinding);
		ConstraintVariable hiddingFieldVar= fConstraintVariableFactory.makeDeclaringTypeVariable(fieldBinding);
		for (Iterator iter= declaringTypes.iterator(); iter.hasNext();) {
			ITypeBinding declaringSuperType= (ITypeBinding) iter.next();
			IVariableBinding hiddenField= findField(fieldBinding, declaringSuperType);
			Assert.isTrue(hiddenField.isField());
			ConstraintVariable hiddenFieldVar= fConstraintVariableFactory.makeDeclaringTypeVariable(hiddenField);
			result.addAll(Arrays.asList(fTypeConstraintFactory.createStrictSubtypeConstraint(hiddingFieldVar, hiddenFieldVar)));
		}
		return result;
	}
	
	private ITypeConstraint[] getConstraintsFromFragmentList(List fragments, Type type) {
		int size= fragments.size();
		ConstraintVariable typeVariable= fConstraintVariableFactory.makeTypeVariable(type);
		List result= new ArrayList((size * (size - 1))/2);
		for (int i= 0; i < size; i++) {
			VariableDeclarationFragment fragment1= (VariableDeclarationFragment) fragments.get(i);
			SimpleName fragment1Name= fragment1.getName();
			result.addAll(Arrays.asList(fTypeConstraintFactory.createDefinesConstraint(
					fConstraintVariableFactory.makeExpressionOrTypeVariable(fragment1Name, getContext()),
					typeVariable)));
			for (int j= i + 1; j < size; j++) {
				VariableDeclarationFragment fragment2= (VariableDeclarationFragment) fragments.get(j);
				result.addAll(Arrays.asList(fTypeConstraintFactory.createEqualsConstraint(
						fConstraintVariableFactory.makeExpressionOrTypeVariable(fragment1Name, getContext()),
						fConstraintVariableFactory.makeExpressionOrTypeVariable(fragment2.getName(), getContext()))));
			}
		}
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);
	}
	
	private Collection getConstraintsForOverriding(IMethodBinding overriddingMethod) {
		Collection result= new ArrayList();
		Set declaringSupertypes= getDeclaringSuperTypes(overriddingMethod);
		for (Iterator iter= declaringSupertypes.iterator(); iter.hasNext();) {
			ITypeBinding superType= (ITypeBinding) iter.next();
			IMethodBinding overriddenMethod= findMethod(overriddingMethod, superType);
			Assert.isNotNull(overriddenMethod);//because we asked for declaring types
			if (Bindings.equals(overriddingMethod, overriddenMethod)) continue;
			ITypeConstraint[] returnTypeConstraint= fTypeConstraintFactory.createEqualsConstraint(
					fConstraintVariableFactory.makeReturnTypeVariable(overriddenMethod),
					fConstraintVariableFactory.makeReturnTypeVariable(overriddingMethod));
			result.addAll(Arrays.asList(returnTypeConstraint));
			Assert.isTrue(overriddenMethod.getParameterTypes().length == overriddingMethod.getParameterTypes().length);
			for (int i= 0, n= overriddenMethod.getParameterTypes().length; i < n; i++) {
				ITypeConstraint[] parameterTypeConstraint= fTypeConstraintFactory.createEqualsConstraint(
						fConstraintVariableFactory.makeParameterTypeVariable(overriddenMethod, i),
						fConstraintVariableFactory.makeParameterTypeVariable(overriddingMethod, i));
				result.addAll(Arrays.asList(parameterTypeConstraint));
			}
			ITypeConstraint[] declaringTypeConstraint= fTypeConstraintFactory.createStrictSubtypeConstraint(
					fConstraintVariableFactory.makeDeclaringTypeVariable(overriddingMethod),
					fConstraintVariableFactory.makeDeclaringTypeVariable(overriddenMethod));
			result.addAll(Arrays.asList(declaringTypeConstraint));
		}
		return result;
	}
	
	private ITypeConstraint[] getReturnTypeConstraint(Expression invocation, IMethodBinding methodBinding){
		if (methodBinding == null || methodBinding.isConstructor() || methodBinding.getReturnType().isPrimitive())
			return new ITypeConstraint[0];
		ConstraintVariable returnTypeVariable= fConstraintVariableFactory.makeReturnTypeVariable(methodBinding);
		ConstraintVariable invocationVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(invocation, getContext());
		return fTypeConstraintFactory.createDefinesConstraint(invocationVariable, returnTypeVariable);
	}
	
	private ITypeConstraint[] getArgumentConstraints(List arguments, IMethodBinding methodBinding) {
		List result= new ArrayList(arguments.size());
		
		if (methodBinding.isVarargs()) {
			ITypeBinding[] parameterTypes= methodBinding.getParameterTypes();
			final int nParams= parameterTypes.length;
			final int nArgs= arguments.size();
			Assert.isTrue(nArgs >= nParams - 1); // there may be zero args for the vararg parameter
			Assert.isTrue(nParams >= 1); // at least one parameter for a vararg method
			
			int i= 0;
			// add the normal argument constraints up to the last one
			for (; i < nParams - 1; i++) {
				Expression argument= (Expression) arguments.get(i);
				ConstraintVariable expressionVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(argument, getContext());
				ConstraintVariable parameterTypeVariable= fConstraintVariableFactory.makeParameterTypeVariable(methodBinding, i);
				ITypeConstraint[] argConstraint= fTypeConstraintFactory.createSubtypeConstraint(expressionVariable, parameterTypeVariable);
				result.addAll(Arrays.asList(argConstraint));
			}
			
			// create argument constraints for all arguments wrapped into the vararg parameter
			boolean directArray= false;
			
			// a) there is exactly one remaining argument -> be careful as it may be a direct array param

// This is currently not used by Generalize Type Declaration as it does not support array types:
//			if (i == nArgs - 1) {
//				Expression argument= (Expression) arguments.get(i);
//				if (TypeRules.canAssign(parameterTypes[nParams - 1], argument.resolveTypeBinding())) {
//					ConstraintVariable parameterTypeVariable= fConstraintVariableFactory.makeParameterTypeVariable(methodBinding, nParams - 1);
//					ConstraintVariable expressionVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(argument, getContext());
//					ITypeConstraint[] argConstraint= fTypeConstraintFactory.createSubtypeConstraint(expressionVariable, parameterTypeVariable);
//					result.addAll(Arrays.asList(argConstraint));
//					directArray= true;
//					//XXX there should be a constraint that logically ORs the direct array and element type constraints
//				}
//			}
			
			// b) there are zero ore more than one arguments remaining
			if (!directArray && i < nArgs) {
				// get the component type of the vararg-array
				ITypeBinding binding= methodBinding.getParameterTypes()[nParams - 1];
				ITypeBinding componentBinding= binding.getComponentType();
				Assert.isNotNull(componentBinding);
				ConstraintVariable parameterTypeVariable= fConstraintVariableFactory.makeRawBindingVariable(componentBinding);
				for (; i < nArgs; i++) {
					Expression argument= (Expression) arguments.get(i);
					ConstraintVariable expressionVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(argument, getContext());
					ITypeConstraint[] argConstraint= fTypeConstraintFactory.createSubtypeConstraint(expressionVariable, parameterTypeVariable);
					result.addAll(Arrays.asList(argConstraint));
				}
			}
			
		} else {
			for (int i= 0, n= arguments.size(); i < n; i++) {
				Expression argument= (Expression) arguments.get(i);
				ConstraintVariable expressionVariable= fConstraintVariableFactory.makeExpressionOrTypeVariable(argument, getContext());
				ConstraintVariable parameterTypeVariable= fConstraintVariableFactory.makeParameterTypeVariable(methodBinding, i);
				ITypeConstraint[] argConstraint= fTypeConstraintFactory.createSubtypeConstraint(expressionVariable, parameterTypeVariable);
				result.addAll(Arrays.asList(argConstraint));
			}
		}
		
		return (ITypeConstraint[]) result.toArray(new ITypeConstraint[result.size()]);		
	}
	
	private static Type getTypeParent(ArrayInitializer arrayInitializer) {
		if (arrayInitializer.getParent() instanceof ArrayCreation){
			return ((ArrayCreation)arrayInitializer.getParent()).getType().getElementType();
		} else if (arrayInitializer.getParent() instanceof ArrayInitializer){
			return getTypeParent((ArrayInitializer) arrayInitializer.getParent());
		} else if (arrayInitializer.getParent() instanceof VariableDeclaration){
			VariableDeclaration parent= (VariableDeclaration)arrayInitializer.getParent();
			
			if (parent.getParent() instanceof VariableDeclarationStatement){
				Type type= ((VariableDeclarationStatement)parent.getParent()).getType();
				return ASTNodes.getElementType(type);
			} else if (parent.getParent() instanceof VariableDeclarationExpression){
				Type type= ((VariableDeclarationExpression)parent.getParent()).getType();
				return ASTNodes.getElementType(type);
			} else if (parent.getParent() instanceof FieldDeclaration){
				Type type= ((FieldDeclaration)parent.getParent()).getType();
				return ASTNodes.getElementType(type);
			}
		}
		Assert.isTrue(false);//array initializers are allowed in only 2 places
		return null;
	}
	
	private ITypeConstraint[] createOrOrSubtypeConstraint(ConstraintVariable var1, ConstraintVariable var2){
		ITypeConstraint[] c1= fTypeConstraintFactory.createSubtypeConstraint(var1, var2);
		ITypeConstraint[] c2= fTypeConstraintFactory.createSubtypeConstraint(var2, var1);
		if (c1.length == 0 && c2.length == 0){
			return new ITypeConstraint[0];
		}
		return new ITypeConstraint[]{ fTypeConstraintFactory.createCompositeOrTypeConstraint(new ITypeConstraint[]{c1[0], c2[0]}) };
	}
	
	private ITypeConstraint[] createConstraintsForAccessToField(IVariableBinding fieldBinding, Expression qualifier, Expression accessExpression){
		Assert.isTrue(fieldBinding.isField());
		ITypeConstraint[] defines= fTypeConstraintFactory.createDefinesConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(accessExpression, getContext()),
				fConstraintVariableFactory.makeRawBindingVariable(fieldBinding.getType()));
		if (qualifier == null)
			return defines;
		ITypeConstraint[] subType= fTypeConstraintFactory.createSubtypeConstraint(
				fConstraintVariableFactory.makeExpressionOrTypeVariable(qualifier, getContext()),
				fConstraintVariableFactory.makeDeclaringTypeVariable(fieldBinding));
		
		if (defines.length == 0){
			return subType;
		} else if (subType.length == 0){
			return defines;
		} else {
			return new ITypeConstraint[]{defines[0], subType[0]};
		}
	}
	
	private static IVariableBinding findField(IVariableBinding fieldBinding, ITypeBinding type) {
		if (fieldBinding.getDeclaringClass().equals(type))
			return fieldBinding;
		return Bindings.findFieldInType(type, fieldBinding.getName());
	}
	
	/* 
	 * return Set of ITypeBindings
	 */
	private static Set getDeclaringSuperTypes(IVariableBinding fieldBinding) {
		ITypeBinding[] allSuperTypes= Bindings.getAllSuperTypes(fieldBinding.getDeclaringClass());
		Set result= new HashSet();
		for (int i= 0; i < allSuperTypes.length; i++) {
			ITypeBinding type= allSuperTypes[i];
			if (findField(fieldBinding, type) != null)
				result.add(type);
		}
		return result;
	}
	
	//--- RootDef ----//
	protected static IMethodBinding[] getRootDefs(IMethodBinding methodBinding) {
		Set/*<ITypeBinding>*/ declaringSuperTypes= getDeclaringSuperTypes(methodBinding);
		Set/*<IMethodBinding>*/ result= new LinkedHashSet();
		for (Iterator iter= declaringSuperTypes.iterator(); iter.hasNext();) {
			ITypeBinding type= (ITypeBinding) iter.next();
			if (! containsASuperType(type, declaringSuperTypes))
				result.add(findMethod(methodBinding, type));
		}
		
		if (result.size() == 0){
			result.add(methodBinding);
		}
		return (IMethodBinding[]) result.toArray(new IMethodBinding[result.size()]);
	}
	
	/* 
	 * @param declaringSuperTypes Set of ITypeBindings
	 * @return <code>true</code> iff <code>declaringSuperTypes</code> contains a type
	 * 		which is a strict supertype of <code>type</code>
	 */
	private static boolean containsASuperType(ITypeBinding type, Set declaringSuperTypes) {
		for (Iterator iter= declaringSuperTypes.iterator(); iter.hasNext();) {
			ITypeBinding maybeSuperType= (ITypeBinding) iter.next();
			if (! Bindings.equals(maybeSuperType, type) && Bindings.isSuperType(maybeSuperType, type))
				return true;
		}
		return false;
	}
	
	/* 
	 * return Set of ITypeBindings
	 */
	protected static Set getDeclaringSuperTypes(IMethodBinding methodBinding) {
		ITypeBinding superClass = methodBinding.getDeclaringClass();
		Set allSuperTypes= new LinkedHashSet();
		allSuperTypes.addAll(Arrays.asList(Bindings.getAllSuperTypes(superClass)));
		if (allSuperTypes.isEmpty())
			allSuperTypes.add(methodBinding.getDeclaringClass()); //TODO: Why only iff empty? The declaring class is not a supertype ...
		Set result= new HashSet();
		for (Iterator iter= allSuperTypes.iterator(); iter.hasNext();) {
			ITypeBinding type= (ITypeBinding) iter.next();
			if (findMethod(methodBinding, type) != null)
				result.add(type);
		}
		return result;
	}
	
	protected static IMethodBinding findMethod(IMethodBinding methodBinding, ITypeBinding type) {
		if (methodBinding.getDeclaringClass().equals(type))
			return methodBinding;
		return Bindings.findOverriddenMethodInType(type, methodBinding);
	}

	private static boolean isClassBinding(ITypeBinding typeBinding){
		return typeBinding != null && typeBinding.isClass();
	}
	
}
