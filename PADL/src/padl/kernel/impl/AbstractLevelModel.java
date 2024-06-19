/*
 * (c) Copyright 2001, 2002 Hervé Albin-Amiot and Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel.impl;

import java.util.Iterator;
import java.util.List;
import padl.event.IEvent;
import padl.event.IModelListener;
import padl.kernel.Constants;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfModel;
import padl.kernel.IFactory;
import padl.kernel.IFilter;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IGenerator;
import padl.kernel.IWalker;
import padl.kernel.exception.ModelDeclarationException;
import padl.path.IConstants;
import util.io.Output;

abstract class AbstractLevelModel implements IAbstractLevelModel {
	private static final long serialVersionUID = -3222006532486393116L;
	protected GenericContainerOfConstituents container =
		new GenericContainerOfNaturallyOrderedConstituents(this);
	private IFactory factory;

	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final char[] name;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private char[] name;
	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final char[] path;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	private char[] path;

	private GenericContainerOfTopLevelEntities topLevelEntitiesContainer =
		new GenericContainerOfTopLevelEntities(this);

	public AbstractLevelModel(final char[] aName) {
		if (aName.length == 0) {
			// Yann 2011/06/18: Empty name for path...
			// I must deal with the case of a model
			// with an empty ("") name... yes, it does
			// happen more often than not and breaks
			// the Finder for path...
			this.name = Constants.NONAME;
		}
		else {
			this.name = aName;
		}
		this.path = new char[1 + this.name.length];
		this.path[0] = IConstants.ABSTRACT_MODEL_SYMBOL;
		System.arraycopy(this.name, 0, this.path, 1, this.name.length);

		this.addModelListener(this.topLevelEntitiesContainer.getListener());
	}
	//	public void addConstituent(final IConstituent aConstituent)
	//			throws ModelDeclarationException {
	//
	//		// Yann 2008/11/04: No no-package!
	//		// In the process of adding the packages consistently,
	//		// I choose that no entity can be without package. Even
	//		// though it may not make sense for languages like C++.
	//		//	if (aConstituent instanceof IEntity) {
	//		//		this.addConstituent((IEntity) aConstituent);
	//		//	}
	//		//	else 
	//		if (aConstituent instanceof IPackage) {
	//			this.container.addConstituent((IPackage) aConstituent);
	//		}
	//		else {
	//			throw new ModelDeclarationException(MultilingualManager.getString(
	//				"ENT_ADD_ORG_LEVEL",
	//				CodeLevelModel.class));
	//		}
	//	}
	public void addConstituent(final IConstituentOfModel aConstituent)
			throws ModelDeclarationException {

		this.container.addConstituent(aConstituent);
		aConstituent.addModelListener(this.topLevelEntitiesContainer
			.getListener());
	}
	public void addModelListener(final IModelListener aModelListener) {
		this.container.addModelListener(aModelListener);
	}
	public void addModelListeners(final List aListOfModelListeners) {
		this.container.addModelListeners(aListOfModelListeners);
	}
	public void removeModelListeners(final List aListOfModelListeners) {
		this.container.removeModelListeners(aListOfModelListeners);
	}
	public final Object clone() throws CloneNotSupportedException {
		// Yann: Here is the new clone protocole. This is subject
		// to discussion!! The idea is that a model may be cloned,
		// but a constituent of a model (Element or Entity) may
		// not be cloned individually, it does not make sense to clone
		// a Method if the rest of the model is not cloned to. I
		// believe this is actually a risk: to be able to clone a single
		// constituent on an individual basis may prove to create
		// duplicates with references on current objects...
		// Thus, only the AbstractLevelModel class implements the Cloneable interface.
		// The constituents of the model implements the
		// ICloneable interface. This model provides
		// three methods:
		// * startCloneSession() is somewhat equivallent to a shallow
		//   copy of the constituent. After this protocol is executed,
		//   it is guaranteed that all constituents have been
		//   shallow-copied. No assumption is made about the link among
		//   constituents.
		// * performCloneSession() updates the links among constituents,
		//   using the isCloned() and getClone() methods. After this
		//   protocol is executed, it is guarenteed that all the links
		//   have been updated, somewhat like a deep copy;
		// * endCloneSession() finished the updates and cleans the possible
		//   temporary values, mainly it sets to null all the "clone"
		//   instance variable.

		final AbstractLevelModel clonedModel =
			(AbstractLevelModel) super.clone();

		// Yann 2002/04/08: Clone!
		// The cloned model must have its own instance
		// of class ArrayList for the list or Entities.
		//	clonedModel.container.resetListOfConstituents();
		// Yann 2010/10/03: Objects!
		// The "container" is now an instance of a class
		// and must be assigned a new instance independently.
		clonedModel.container =
			new GenericContainerOfNaturallyOrderedConstituents(clonedModel);
		clonedModel.topLevelEntitiesContainer =
			new GenericContainerOfTopLevelEntities(clonedModel);
		clonedModel.addModelListener(clonedModel.topLevelEntitiesContainer
			.getListener());

		this.clone(clonedModel);

		// Yann 2011/06/20: Top-level entities...
		this.topLevelEntitiesContainer
			.createMapOfIDsEntities(Constants.FORBIDDEN_ID);

		return clonedModel;
	}
	protected void clone(final IAbstractLevelModel anAbstractLevelModel) {
		// First, I make a shallow copy of all the entities.
		Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituentOfModel constituent =
				(IConstituentOfModel) iterator.next();
			constituent.startCloneSession();
			try {
				anAbstractLevelModel
					.addConstituent((IConstituentOfModel) constituent
						.getClone());
			}
			catch (final ModelDeclarationException pde) {
				pde.printStackTrace(Output.getInstance().errorOutput());
			}
		}

		// Second, I update the links among entities (deep copy).
		iterator = this.getIteratorOnTopLevelEntities();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).performCloneSession();
		}

		// Finally, I clean up all temporary instance variable.
		iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).endCloneSession();
		}
	}
	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.container.doesContainConstituentWithID(anID);
	}

	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.container.doesContainConstituentWithName(aName);
	}
	public void fireModelChange(final String anEventType, final IEvent anEvent) {
		this.container.fireModelChange(anEventType, anEvent);
	}
	public final String generate(final IGenerator builder) {
		builder.open(this);

		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).accept(builder);
		}

		builder.close(this);
		return builder.getCode();
	}
	public Iterator getConcurrentIteratorOnConstituents() {
		return this.container.getConcurrentIteratorOnConstituents();
	}
	public Iterator getConcurrentIteratorOnConstituents(final IFilter filter) {
		return this.container.getConcurrentIteratorOnConstituents();
	}
	public Iterator getConcurrentIteratorOnConstituents(
		final java.lang.Class aConstituentType) {

		return this.container
			.getConcurrentIteratorOnConstituents(aConstituentType);
	}
	public IConstituent getConstituentFromID(final char[] anID) {
		return this.container.getConstituentFromID(anID);
	}
	public IConstituent getConstituentFromID(final String anID) {
		return this.getConstituentFromID(anID.toCharArray());
	}
	public IConstituent getConstituentFromName(final char[] aName) {
		return this.container.getConstituentFromName(aName);
	}
	public IConstituent getConstituentFromName(final String aName) {
		return this.getConstituentFromName(aName.toCharArray());
	}
	public String getDisplayName() {
		return String.valueOf(this.name);
	}
	public String getDisplayPath() {
		return String.valueOf(this.path);
	}
	public IFactory getFactory() {
		return this.factory;
	}
	public Iterator getIteratorOnConstituents() {
		return this.container.getIteratorOnConstituents();
	}
	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		return this.container.getIteratorOnConstituents(aFilter);
	}
	public Iterator getIteratorOnConstituents(java.lang.Class aConstituentType) {
		return this.container.getIteratorOnConstituents(aConstituentType);
	}
	public Iterator getIteratorOnModelListeners() {
		return this.container.getIteratorOnModelListeners();
	}
	public Iterator getIteratorOnTopLevelEntities() {
		return this.topLevelEntitiesContainer.getIteratorOnTopLevelEntities();
	}
	public char[] getName() {
		return this.name;
	}
	public int getNumberOfConstituents() {
		return this.container.getNumberOfConstituents();
	}
	public int getNumberOfConstituents(final java.lang.Class aConstituentType) {
		return this.container.getNumberOfConstituents(aConstituentType);
	}
	public int getNumberOfTopLevelEntities() {
		return this.topLevelEntitiesContainer.getNumberOfTopLevelEntities();
	}
	public int getNumberOfTopLevelEntities(final java.lang.Class anEntityType) {
		return this.topLevelEntitiesContainer
			.getNumberOfTopLevelEntities(anEntityType);
	}
	public char[] getPath() {
		return this.path;
	}
	public IFirstClassEntity getTopLevelEntityFromID(final char[] anID) {
		return (IFirstClassEntity) this.topLevelEntitiesContainer
			.getTopLevelEntityFromID(anID);
	}
	public IFirstClassEntity getTopLevelEntityFromID(final String anID) {
		return this.getTopLevelEntityFromID(anID.toCharArray());
	}
	public void removeAllConstituent() {
		this.container.removeAllConstituent();
	}
	public void removeConstituentFromID(final char[] anID) {
		this.container.removeConstituentFromID(anID);
	}
	public void removeModelListener(final IModelListener aModelListener) {
		this.container.removeModelListener(aModelListener);
	}
	public void removeTopLevelEntityFromID(final char[] anID) {
		this.topLevelEntitiesContainer.removeTopLevelEntityFromID(anID);
	}
	public void setFactory(final IFactory aFactory) {
		this.factory = aFactory;
	}
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			buffer.append(iterator.next().toString());
			if (iterator.hasNext()) {
				buffer.append('\n');
			}
		}
		return buffer.toString();
	}
	public void updateWithIncomingPath(char[] theIncomingPath) {
	}
	public final Object walk(final IWalker walker) {
		walker.open(this);

		// Yann 2004/12/19: Comodification again...
		// I prevent comodification exceptions by iterating
		// "by hand" over the list of entities.
		//	final Iterator iterator = this.listOfConstituents().iterator();
		//	while (iterator.hasNext()) {
		//		((IConstituent) iterator.next()).accept(walker);
		//	}
		// Yann 2005/10/12: Iterator!
		// I now provide iterators to go through a list of actors
		// rather than using the depracated (and soon to disappear)
		// listOfConstituents() method. So, I also provide an iterator
		// iterating over a copy of the list, thus allowing concurrent
		// modifications.
		//	final List listOfConstituents = this.listOfConstituents();
		//	for (int i = 0; i < listOfConstituents.size(); i++) {
		//		((IConstituent) listOfConstituents.get(i)).accept(walker);
		//	}
		final Iterator iterator = this.getConcurrentIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).accept(walker);
		}

		walker.close(this);
		return walker.getResult();
	}
}
