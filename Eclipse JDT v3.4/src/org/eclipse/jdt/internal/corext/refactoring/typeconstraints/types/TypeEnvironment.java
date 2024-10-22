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
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import org.eclipse.jdt.core.BindingKey;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;



public class TypeEnvironment {
	
	private static class ProjectKeyPair {
		private final IJavaProject fProject;
		private final String fBindingKey;
		
		public ProjectKeyPair(IJavaProject project, String bindingKey) {
			fProject= project;
			fBindingKey= bindingKey;
		}
		
		public boolean equals(Object other) {
			if (this == other)
				return true;
			if (! (other instanceof ProjectKeyPair))
				return false;
			ProjectKeyPair otherPair= (ProjectKeyPair) other;
			return fProject.equals(otherPair.fProject) && fBindingKey.equals(otherPair.fBindingKey);
		}
		
		public int hashCode() {
			return fProject.hashCode() + fBindingKey.hashCode();
		}
	}
	
	/** Type code for the primitive type "int". */
	public final PrimitiveType INT= new PrimitiveType(this, PrimitiveType.INT, BindingKey.createTypeBindingKey("int")); //$NON-NLS-1$
	/** Type code for the primitive type "char". */
	public final PrimitiveType CHAR = new PrimitiveType(this, PrimitiveType.CHAR, BindingKey.createTypeBindingKey("char")); //$NON-NLS-1$
	/** Type code for the primitive type "boolean". */
	public final PrimitiveType BOOLEAN = new PrimitiveType(this, PrimitiveType.BOOLEAN, BindingKey.createTypeBindingKey("boolean")); //$NON-NLS-1$
	/** Type code for the primitive type "short". */
	public final PrimitiveType SHORT = new PrimitiveType(this, PrimitiveType.SHORT, BindingKey.createTypeBindingKey("short")); //$NON-NLS-1$
	/** Type code for the primitive type "long". */
	public final PrimitiveType LONG = new PrimitiveType(this, PrimitiveType.LONG, BindingKey.createTypeBindingKey("long")); //$NON-NLS-1$
	/** Type code for the primitive type "float". */
	public final PrimitiveType FLOAT = new PrimitiveType(this, PrimitiveType.FLOAT, BindingKey.createTypeBindingKey("float")); //$NON-NLS-1$
	/** Type code for the primitive type "double". */
	public final PrimitiveType DOUBLE = new PrimitiveType(this, PrimitiveType.DOUBLE, BindingKey.createTypeBindingKey("double")); //$NON-NLS-1$
	/** Type code for the primitive type "byte". */
	public final PrimitiveType BYTE = new PrimitiveType(this, PrimitiveType.BYTE, BindingKey.createTypeBindingKey("byte")); //$NON-NLS-1$
	
	/** Type code for the primitive type "null". */
	public final NullType NULL= new NullType(this);
	
	public final VoidType VOID= new VoidType(this); 
	
	final PrimitiveType[] PRIMITIVE_TYPES= {INT, CHAR, BOOLEAN, SHORT, LONG, FLOAT, DOUBLE, BYTE};
	
	private static final String[] BOXED_PRIMITIVE_NAMES= new String[] {
		"java.lang.Integer",  //$NON-NLS-1$
		"java.lang.Character",  //$NON-NLS-1$
		"java.lang.Boolean",  //$NON-NLS-1$
		"java.lang.Short",  //$NON-NLS-1$
		"java.lang.Long",  //$NON-NLS-1$
		"java.lang.Float",  //$NON-NLS-1$
		"java.lang.Double",  //$NON-NLS-1$
		"java.lang.Byte"};  //$NON-NLS-1$
	
	private TType OBJECT_TYPE= null;
	
	private Map/*<TType, ArrayType>*/[]          fArrayTypes= new Map[] { new HashMap() };
	private Map/*<IJavaElement, StandardType>*/  fStandardTypes= new HashMap();
	private Map/*<IJavaElement, GenericType>*/   fGenericTypes= new HashMap();
	private Map/*<ProjectKeyPair, ParameterizedType>*/ fParameterizedTypes= new HashMap();
	private Map/*<IJavaElement, RawType>*/       fRawTypes= new HashMap();
	private Map/*<IJavaElement, TypeVariable>*/  fTypeVariables= new HashMap();
	private Map/*<ProjectKeyPair, CaptureType>*/ fCaptureTypes= new HashMap();
	private Map/*<TType, ExtendsWildcardType>*/  fExtendsWildcardTypes= new HashMap();
	private Map/*<TType, SuperWildcardType>*/    fSuperWildcardTypes= new HashMap();
	private UnboundWildcardType fUnboundWildcardType= null;
	
	private static final int MAX_ENTRIES= 1024;
	private Map/*<TypeTuple, Boolean>*/ fSubTypeCache= new LinkedHashMap(50, 0.75f, true) {
		private static final long serialVersionUID= 1L;
		protected boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_ENTRIES;
		}
	};
	
	/**
	 * Map from TType to its known subtypes, or <code>null</code> iff subtype
	 * information was not requested in the constructor.
	 */
	private Map/*<TType, List<TType>>*/ fSubTypes;
	
	public static ITypeBinding[] createTypeBindings(TType[] types, IJavaProject project) {
		final Map mapping= new HashMap();
		List keys= new ArrayList();
		for (int i= 0; i < types.length; i++) {
			TType type= types[i];
			String bindingKey= type.getBindingKey();
			mapping.put(bindingKey, type);
			keys.add(bindingKey);
		}
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		parser.setProject(project);
		parser.setResolveBindings(true);
		parser.createASTs(new ICompilationUnit[0], (String[])keys.toArray(new String[keys.size()]), 
			new ASTRequestor() {
				public void acceptBinding(String bindingKey, IBinding binding) {
					mapping.put(bindingKey, binding);
				}
			}, null);
		ITypeBinding[] result= new ITypeBinding[types.length];
		for (int i= 0; i < types.length; i++) {
			TType type= types[i];
			String bindingKey= type.getBindingKey();
			Object value= mapping.get(bindingKey);
			if (value instanceof ITypeBinding) {
				result[i]= (ITypeBinding)value;
			}
		}
		return result;
	}
	
	public TypeEnvironment() {
		this(false);
	}
	
	public TypeEnvironment(boolean rememberSubtypes) {
		if (rememberSubtypes) {
			fSubTypes= new HashMap();
		}
	}
	
	Map/*<TypeTuple, Boolean>*/ getSubTypeCache() {
		return fSubTypeCache;
	}
	
	public TType create(ITypeBinding binding) {
		if (binding.isPrimitive()) {
			return createPrimitiveType(binding);
		} else if (binding.isArray()) {
			return createArrayType(binding);
		} else if (binding.isRawType()) {
			return createRawType(binding);
		} else if (binding.isGenericType()) {
			return createGenericType(binding);
		} else if (binding.isParameterizedType()) {
			return createParameterizedType(binding);
		} else if (binding.isTypeVariable()) {
			return createTypeVariable(binding);
		} else if (binding.isWildcardType()) {
			if (binding.getBound() == null) {
				return createUnboundWildcardType(binding);
			} else if (binding.isUpperbound()) {
				return createExtendsWildCardType(binding);
			} else {
				return createSuperWildCardType(binding);
			}
		} else if (binding.isCapture()) {
			return createCaptureType(binding);
		}
		if ("null".equals(binding.getName())) //$NON-NLS-1$
			return NULL;
		return createStandardType(binding);
	}
	
	public TType[] create(ITypeBinding[] bindings) {
		TType[] result= new TType[bindings.length];
		for (int i= 0; i < bindings.length; i++) {
			result[i]= create(bindings[i]);
		}
		return result;
	}
	
	/**
	 * Returns the TType for java.lang.Object.
	 * <p>
	 * Warning: currently returns <code>null</code> unless this type environment
	 * has already created its first hierarchy type.
	 * 
	 * @return the TType for java.lang.Object
	 */
	public TType getJavaLangObject() {
		return OBJECT_TYPE;
	}
	
	void initializeJavaLangObject(ITypeBinding object) {
		if (OBJECT_TYPE != null)
			return;
		
		TType objectType= createStandardType(object);
		Assert.isTrue(objectType.isJavaLangObject());
	}
	
	PrimitiveType createUnBoxed(StandardType type) {
		String name= type.getPlainPrettySignature();
		for (int i= 0; i < BOXED_PRIMITIVE_NAMES.length; i++) {
			if (BOXED_PRIMITIVE_NAMES[i].equals(name))
				return PRIMITIVE_TYPES[i];
		}
		return null;
	}
	
	StandardType createBoxed(PrimitiveType type, IJavaProject focus) {
		String fullyQualifiedName= BOXED_PRIMITIVE_NAMES[type.getId()];
		try {
			IType javaElementType= focus.findType(fullyQualifiedName);
			StandardType result= (StandardType)fStandardTypes.get(javaElementType);
			if (result != null)
				return result;
			ASTParser parser= ASTParser.newParser(AST.JLS3);
			parser.setProject(focus);
			IBinding[] bindings= parser.createBindings(new IJavaElement[] {javaElementType} , null);
			return createStandardType((ITypeBinding)bindings[0]);
		} catch (JavaModelException e) {
			// fall through
		}
		return null;
	}
	
	Map/*<TType, List<TType>>*/ getSubTypes() {
		return fSubTypes;
	}
	
	private void cacheSubType(TType supertype, TType result) {
		if (fSubTypes == null)
			return;
		if (supertype == null)
			supertype= OBJECT_TYPE;
		
		ArrayList subtypes= (ArrayList) fSubTypes.get(supertype);
		if (subtypes == null) {
			subtypes= new ArrayList(5);
			fSubTypes.put(supertype, subtypes);
		} else {
			Assert.isTrue(! subtypes.contains(result));
		}
		subtypes.add(result);
	}

	private void cacheSubTypes(TType[] interfaces, TType result) {
		for (int i= 0; i < interfaces.length; i++) {
			cacheSubType(interfaces[i], result);
		}
	}

	private TType createPrimitiveType(ITypeBinding binding) {
		String name= binding.getName();
		String[] names= PrimitiveType.NAMES;
		for (int i= 0; i < names.length; i++) {
			if (name.equals(names[i])) {
				return PRIMITIVE_TYPES[i];
			}
		}
		Assert.isTrue(false, "Primitive type " + name + "unkown");  //$NON-NLS-1$//$NON-NLS-2$
		return null;
	}

	private ArrayType createArrayType(ITypeBinding binding) {
		int index= binding.getDimensions() - 1;
		TType elementType= create(binding.getElementType());
		Map/*<TType, ArrayType>*/ arrayTypes= getArrayTypesMap(index);
		ArrayType result= (ArrayType)arrayTypes.get(elementType);
		if (result != null)
			return result;
		result= new ArrayType(this);
		arrayTypes.put(elementType, result);
		result.initialize(binding, elementType);
		return result;
	}
	
	public ArrayType createArrayType(TType elementType, int dimensions) {
		Assert.isTrue(! elementType.isArrayType());
		Assert.isTrue(! elementType.isAnonymous());
		Assert.isTrue(dimensions > 0);
		
		int index= dimensions - 1;
		Map arrayTypes= getArrayTypesMap(index);
		ArrayType result= (ArrayType)arrayTypes.get(elementType);
		if (result != null)
			return result;
		result= new ArrayType(this, BindingKey.createArrayTypeBindingKey(elementType.getBindingKey(), dimensions));
		arrayTypes.put(elementType, result);
		result.initialize(elementType, dimensions);
		return result;
	}

	private Map/*<TType, ArrayType>*/ getArrayTypesMap(int index) {
		int oldLength= fArrayTypes.length;
		if (index >= oldLength) {
			Map[] newArray= new Map[index + 1];
			System.arraycopy(fArrayTypes, 0, newArray, 0, oldLength);
			fArrayTypes= newArray;
		}
		Map arrayTypes= fArrayTypes[index];
		if (arrayTypes == null) {
			arrayTypes= new HashMap();
			fArrayTypes[index]= arrayTypes;
		}
		return arrayTypes;
	}
	
	private StandardType createStandardType(ITypeBinding binding) {
		IJavaElement javaElement= binding.getJavaElement();
		StandardType result= (StandardType)fStandardTypes.get(javaElement);
		if (result != null)
			return result;
		result= new StandardType(this);
		fStandardTypes.put(javaElement, result);
		result.initialize(binding, (IType)javaElement);
		if (OBJECT_TYPE == null && result.isJavaLangObject())
			OBJECT_TYPE= result;
		return result;
	}
	
	private GenericType createGenericType(ITypeBinding binding) {
		IJavaElement javaElement= binding.getJavaElement();
		GenericType result= (GenericType)fGenericTypes.get(javaElement);
		if (result != null)
			return result;
		result= new GenericType(this);
		fGenericTypes.put(javaElement, result);
		result.initialize(binding, (IType)javaElement);
		cacheSubType(result.getSuperclass(), result);
		cacheSubTypes(result.getInterfaces(), result);
		return result;
	}
	
	private ParameterizedType createParameterizedType(ITypeBinding binding) {
		IJavaProject javaProject= binding.getJavaElement().getJavaProject();
		String bindingKey= binding.getKey();
		ProjectKeyPair pair= new ProjectKeyPair(javaProject, bindingKey);
		ParameterizedType result= (ParameterizedType)fParameterizedTypes.get(pair);
		if (result != null)
			return result;
		result= new ParameterizedType(this);
		fParameterizedTypes.put(pair, result);
		result.initialize(binding, (IType)binding.getJavaElement());
		cacheSubType(result.getSuperclass(), result);
		cacheSubTypes(result.getInterfaces(), result);
		return result;
	}
	
	private RawType createRawType(ITypeBinding binding) {
		IJavaElement javaElement= binding.getJavaElement();
		RawType result= (RawType)fRawTypes.get(javaElement);
		if (result != null)
			return result;
		result= new RawType(this);
		fRawTypes.put(javaElement, result);
		result.initialize(binding, (IType)javaElement);
		cacheSubType(result.getSuperclass(), result);
		cacheSubTypes(result.getInterfaces(), result);
		return result;
	}
	
	private TType createUnboundWildcardType(ITypeBinding binding) {
		if (fUnboundWildcardType == null) {
			fUnboundWildcardType= new UnboundWildcardType(this);
			fUnboundWildcardType.initialize(binding);
		}
		return fUnboundWildcardType;
	}
	
	private TType createExtendsWildCardType(ITypeBinding binding) {
		TType bound= create(binding.getBound());
		ExtendsWildcardType result= (ExtendsWildcardType)fExtendsWildcardTypes.get(bound);
		if (result != null)
			return result;
		result= new ExtendsWildcardType(this);
		fExtendsWildcardTypes.put(bound, result);
		result.initialize(binding);
		return result;
	}	
	
	private TType createSuperWildCardType(ITypeBinding binding) {
		TType bound= create(binding.getBound());
		SuperWildcardType result= (SuperWildcardType)fSuperWildcardTypes.get(bound);
		if (result != null)
			return result;
		result= new SuperWildcardType(this);
		fSuperWildcardTypes.put(bound, result);
		result.initialize(binding);
		return result;
	}	
	
	private TypeVariable createTypeVariable(ITypeBinding binding) {
		IJavaElement javaElement= binding.getJavaElement();
		TypeVariable result= (TypeVariable)fTypeVariables.get(javaElement);
		if (result != null)
			return result;
		result= new TypeVariable(this);
		fTypeVariables.put(javaElement, result);
		result.initialize(binding, (ITypeParameter)javaElement);
		return result;
	}
	
	private CaptureType createCaptureType(ITypeBinding binding) {
		IJavaProject javaProject= binding.getDeclaringClass().getJavaElement().getJavaProject();
		String bindingKey= binding.getKey();
		ProjectKeyPair pair= new ProjectKeyPair(javaProject, bindingKey);
		CaptureType result= (CaptureType)fCaptureTypes.get(pair);
		if (result != null)
			return result;
		result= new CaptureType(this);
		fCaptureTypes.put(pair, result);
		result.initialize(binding, javaProject);
		return result;
	}
}
