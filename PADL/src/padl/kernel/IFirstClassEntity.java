/*
 * (c) Copyright 2001-2003 Yann-Gaël Guéhéneuc, École des Mines de Nantes and
 * Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works based
 * upon this software are permitted. Any copy of this software or of any
 * derivative work must include the above copyright notice of the author, this
 * paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND NOT
 * WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY LIABILITY FOR DAMAGES
 * RESULTING FROM THE SOFTWARE OR ITS USE IS EXPRESSLY DISCLAIMED, WHETHER
 * ARISING IN CONTRACT, TORT (INCLUDING NEGLIGENCE) OR STRICT LIABILITY, EVEN
 * IF THE AUTHOR IS ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel;

import java.util.Iterator;
import padl.kernel.exception.ModelDeclarationException;

/**
 * @author Yann-Gaël Guéhéneuc
 */
public interface IFirstClassEntity extends IContainer, IEntity {
	void addConstituent(final IConstituentOfEntity anElement)
			throws ModelDeclarationException;
	void addInheritedEntity(final IFirstClassEntity anEntity)
			throws ModelDeclarationException;
	// Yann 2005/05/21: Usage!
	// The addition now happens in the addInheritedEntity() method.
	//	/**
	//	 * This method add a new entity to the list of entities inheriting from
	//	 * this entity.
	//	 * 
	//	 * @param anEntity
	//	 * @throws ModelDeclarationException
	//	 */
	//	void addInheritingEntity(final IEntity anEntity)
	//		throws ModelDeclarationException;
	IFirstClassEntity getInheritedEntity(final char[] anEntityName);
	Iterator getIteratorOnInheritedEntities();
	Iterator getIteratorOnInheritingEntities();
	/**
	 * This method returns the list of all entities inheriting from this
	 * entity.
	 * 
	 * @return list of inheriting actors
	 */
	int getNumberOfInheritedEntities();
	int getNumberOfInheritingEntities();
	String getPurpose();
	boolean isAboveInHierarchy(final IFirstClassEntity anEntity);
	void removeInheritedEntity(final IFirstClassEntity anEntity);
	void setPurpose(final String aPurpose);
}