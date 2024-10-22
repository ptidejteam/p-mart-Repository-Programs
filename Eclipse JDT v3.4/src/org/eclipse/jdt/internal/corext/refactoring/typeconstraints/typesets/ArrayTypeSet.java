/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Robert M. Fuhrer (rfuhrer@watson.ibm.com), IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets;

import java.util.Iterator;

import org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types.ArrayType;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints.types.TType;
import org.eclipse.jdt.internal.corext.refactoring.typeconstraints2.TTypes;

/**
 * Represents the set of array types whose element types are in a given TypeSet.
 * I.e., ArrayTypeSet(S) = { x[] | x \in S }
 */
public class ArrayTypeSet extends TypeSet {
	protected TypeSet fElemTypeSet;

	protected ArrayTypeSet(TypeSetEnvironment typeSetEnvironment) {
		super(typeSetEnvironment);
	}

	public ArrayTypeSet(TypeSet s) {
		super(s.getTypeSetEnvironment());
		fElemTypeSet= s;
	}

	/**
	 * @return Returns the element TypeSet.
	 */
	public TypeSet getElemTypeSet() {
		return fElemTypeSet;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#isUniverse()
	 */
	public boolean isUniverse() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#makeClone()
	 */
	public TypeSet makeClone() {
		return new ArrayTypeSet(fElemTypeSet);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#isEmpty()
	 */
	public boolean isEmpty() {
		return fElemTypeSet.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#upperBound()
	 */
	public TypeSet upperBound() {
		return new ArrayTypeSet(fElemTypeSet.upperBound());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#lowerBound()
	 */
	public TypeSet lowerBound() {
		return new ArrayTypeSet(fElemTypeSet.lowerBound());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#hasUniqueLowerBound()
	 */
	public boolean hasUniqueLowerBound() {
		return fElemTypeSet.hasUniqueLowerBound();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#hasUniqueUpperBound()
	 */
	public boolean hasUniqueUpperBound() {
		return fElemTypeSet.hasUniqueUpperBound();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#uniqueLowerBound()
	 */
	public TType uniqueLowerBound() {
		return TTypes.createArrayType(fElemTypeSet.uniqueLowerBound(), 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#uniqueUpperBound()
	 */
	public TType uniqueUpperBound() {
		return TTypes.createArrayType(fElemTypeSet.uniqueUpperBound(), 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#contains(TType)
	 */
	public boolean contains(TType t) {
		if (!(t instanceof ArrayType))
			return false;
		ArrayType at= (ArrayType) t;
		return fElemTypeSet.contains(at.getComponentType());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#containsAll(org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet)
	 */
	public boolean containsAll(TypeSet s) {
		if (s instanceof ArrayTypeSet && !(s instanceof ArraySuperTypeSet)) {
			ArrayTypeSet ats= (ArrayTypeSet) s;

			return fElemTypeSet.containsAll(ats.fElemTypeSet);
		}
		for(Iterator iter= s.iterator(); iter.hasNext(); ) {
			TType t= (TType) iter.next();
			if (!contains(t))
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#iterator()
	 */
	public Iterator iterator() {
		if (fEnumCache != null) return fEnumCache.iterator();

		return new Iterator() {
			Iterator fElemIter= fElemTypeSet.iterator();
			public boolean hasNext() {
				return fElemIter.hasNext();
			}
			public Object next() {
				return TTypes.createArrayType(((TType) fElemIter.next()), 1);
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private EnumeratedTypeSet fEnumCache= null;

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#enumerate()
	 */
	public EnumeratedTypeSet enumerate() {
		if (fEnumCache == null) {
			fEnumCache= new EnumeratedTypeSet(getTypeSetEnvironment());

			for(Iterator iter= fElemTypeSet.iterator(); iter.hasNext(); ) {
				TType t= (TType) iter.next();
				fEnumCache.add(TTypes.createArrayType(t, 1));
			}
			fEnumCache.initComplete();
		}
		return fEnumCache;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#isSingleton()
	 */
	public boolean isSingleton() {
		return fElemTypeSet.isSingleton();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#anyMember()
	 */
	public TType anyMember() {
		return TTypes.createArrayType(fElemTypeSet.anyMember(), 1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.typesets.TypeSet#superTypes()
	 */
	public TypeSet superTypes() {
		return new ArraySuperTypeSet(fElemTypeSet);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof ArrayTypeSet) {
			ArrayTypeSet other= (ArrayTypeSet) obj;

			return fElemTypeSet.equals(other.fElemTypeSet);
		}
		return false;
	}

	public String toString() {
		return "{" + fID + ": array(" + fElemTypeSet + ")}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
