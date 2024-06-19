/*
 * (c) Copyright 2001-2004 Yann-Ga? Gu??euc,
 * University of Montr?l.
 *
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the author, this paragraph and the one after it.
 *
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHOR IS ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * All Rights Reserved.
 */
package padl.kernel.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang.ArrayUtils;
import padl.event.ElementEvent;
import padl.event.EntityEvent;
import padl.event.IModelListener;
import padl.kernel.Constants;
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfEntity;
import padl.kernel.IConstituentOfModel;
import padl.kernel.IContainer;
import padl.kernel.IFilter;
import padl.kernel.INavigable;
import padl.kernel.IObservable;
import padl.kernel.exception.ModelDeclarationException;
import padl.path.IConstants;
import padl.util.Util;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since 2004/04/09
 */
// Sebastien Colladon 23/04/2012 : Change the visibility to public in order to
// allow other project to extend from this class in the particular case of
// eclipse bundle loader (avoid IllegalAccessError).
public abstract class GenericContainerOfConstituents extends GenericObservable
		implements IContainer, Serializable {

	protected static class ConstituentIterator implements Iterator,
			Serializable {

		private static final long serialVersionUID = 1209788683542287364L;

		private final IConstituent[] constituents;
		private int index = 0;
		private final int maxSize;

		public ConstituentIterator(final IConstituent[] someConstituents,
				final int aSize) {

			this.constituents = someConstituents;
			this.maxSize = aSize;
		}

		public boolean hasNext() {
			return this.index < this.maxSize;
		}

		public Object next() {
			try {
				final Object next = this.constituents[this.index];
				this.index++;
				return next;
			} catch (final IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void reset() {
			this.index = 0;
		}
	}

	private static final long serialVersionUID = -2006341987649472498L;

	// Yann 2004/12/13: Performances
	// When loading huge AOL file, it seems that:
	// this.doesContainConstituentWithID(aConstituent.getConstituentID())
	// is a performance bottleneck because of the many (maaany!)
	// instances of Iterator built. I cannot replace the list by a
	// hashtable which key is the actor ID because the list must be
	// sorted to ensure similar behavior on different platforms.
	// So, I cache the actor in a second list.
	//
	// Yann 2009/04/28: Performances (bis)
	// I replaced these caches by using maps of constituents.
	// private List actorsIDCache = new ArrayList();
	// private List actorsNameCache = new ArrayList();
	//
	// Yann 2009/04/28: Performances (bis)
	// I use a TreeMap that is an ordered HashMap...
	// to make it easier to test the code and to
	// guarantee that, whatever the insertion, the
	// constituent will be ordered.
	//
	// Yann 2009/05/03: Performances (ter)
	// I now manage my own array of constituents,
	// I make it grow from prime number to prime
	// number using an indice.
	protected IConstituent[] constituents;
	// protected ConstituentIterator constituentIterator;
	// Yann 2010/10/10: DB4O
	// Used to be:
	// protected final INavigable containerConsitituent;
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	protected INavigable containerConsitituent;
	private int indexInPrimeNumbersArray;
	protected int size;
	private int uniqueID;

	public GenericContainerOfConstituents(final INavigable aContainerConstituent) {
		this.containerConsitituent = aContainerConstituent;
		this.resetListOfConstituents();
	}

	public GenericContainerOfConstituents(
			final INavigable aContainerConstituent, final int anInitialCapacity) {

		this.containerConsitituent = aContainerConstituent;
		this.resetListOfConstituents(anInitialCapacity);
	}

	public void addConstituent(final IConstituent aConstituent)
			throws ModelDeclarationException {

		// Yann 2009/05/06: Path
		// After having removed a constituent from a model,
		// I make sure that I "reset" its path so that I can
		// add it to another model. Also, I will test when
		// adding a constituent if it does not include path
		// information to prevent two models to share
		// constituents, maybe the clone process should be
		// involved too... I'll think about it later.
		// Yann 2010/04/29: Path!
		// I share the same default package across
		// models but it does not matter because the
		// model name does not matter.
		if (ArrayUtils.contains(aConstituent.getPath(),
				IConstants.ABSTRACT_MODEL_SYMBOL)
				&& !Arrays.equals(aConstituent.getID(),
						Constants.DEFAULT_PACKAGE_ID)) {

			final StringBuffer buffer = new StringBuffer();
			buffer.append("Shared constituent: ");
			buffer.append(aConstituent.getID());
			buffer.append(" (");
			buffer.append(aConstituent.getClass());
			buffer.append(") in ");
			buffer.append(this.getClass());
			buffer.append("\nRemove this consituent from its origin model first before adding it to the current model.");
			throw new ModelDeclarationException(buffer.toString());
		}

		if (this.doesContainConstituentWithID(aConstituent.getID())) {
			final StringBuffer buffer = new StringBuffer();
			buffer.append("Duplicate constituent: ");
			buffer.append(aConstituent.getID());
			buffer.append(" (");
			buffer.append(aConstituent.getClass());
			buffer.append(") in ");
			buffer.append(this.getClass());
			throw new ModelDeclarationException(buffer.toString());
		}

		this.directlyAddConstituent(aConstituent);
	}

	private void addUniqueIDToEnclosedConstituent(
			final IConstituent aConstituent) {
		// Yann 2006/02/24: UniqueID...
		// I need a UniqueID for IMethodInvocation and IParemeter in IMethod
		// and for any IRelationship in IEntity. So, when using the method
		// concretelyAddConstituent(), I take care of the unique ID.

		this.uniqueID++;
		if (this instanceof GenericContainerOfConstituents
				&& aConstituent instanceof Constituent) {

			final char[] uniqueID = String.valueOf(this.uniqueID).toCharArray();
			final char[] newID = new char[aConstituent.getID().length + 1
					+ uniqueID.length];
			System.arraycopy(aConstituent.getID(), 0, newID, 0,
					aConstituent.getID().length);
			newID[aConstituent.getID().length] = '_';
			System.arraycopy(uniqueID, 0, newID,
					aConstituent.getID().length + 1, uniqueID.length);
			((Constituent) aConstituent).setID(newID);

			// Yann 2009/06/06: Path!
			// I don't forget to update the path accordingly...
			((Constituent) aConstituent).setPath(newID);
		}
	}

	private void broadcastAdditionOfConstituent(final IConstituent aConstituent) {
		// Yann 2004/04/09: Listener!
		// It is now the responsibility of this class to manage
		// the listeners and to send the appropriate events when
		// a change occurs.
		// Yann 2006/03/08: Listeners...
		// I recursively add the listeners to the constituent that I add...
		if (aConstituent instanceof IObservable) {
			((IObservable) aConstituent).addModelListeners(this
					.getModelListeners());
		}

		// Yann 2005/10/07: Packages!
		// I should distinguish among entities and packages...
		if (aConstituent instanceof IConstituentOfModel) {
			this.fireModelChange(IModelListener.ENTITY_ADDED, new EntityEvent(
					(IContainer) this.containerConsitituent,
					(IConstituentOfModel) aConstituent));
		} else if (aConstituent instanceof IConstituentOfEntity) {
			this.fireModelChange(IModelListener.ELEMENT_ADDED,
					new ElementEvent((IContainer) this.containerConsitituent,
							(IConstituentOfEntity) aConstituent));
		}
	}

	private void broadcastRemovalOfConstituent(final IConstituent aConstituent) {
		// Yann 2004/04/09: Listener!
		// It is now the responsibility of this class to manage
		// the listeners and to send the appropriate events when
		// a change occurs.
		// Yann 2006/03/08: Listeners...
		// I recursively remove the listeners to the constituent that I
		// remove...
		if (aConstituent instanceof IObservable) {
			((IObservable) aConstituent).removeModelListeners(this
					.getModelListeners());
		}

		// Yann 2005/10/07: Packages!
		// I should distinguish among entities and packages...
		if (aConstituent instanceof IConstituentOfModel) {
			this.fireModelChange(IModelListener.ENTITY_REMOVED,
					new EntityEvent((IContainer) this.containerConsitituent,
							(IConstituentOfModel) aConstituent));
		} else if (aConstituent instanceof IConstituentOfEntity) {
			this.fireModelChange(IModelListener.ELEMENT_REMOVED,
					new ElementEvent((IContainer) this.containerConsitituent,
							(IConstituentOfEntity) aConstituent));
		}
	}

	protected final void directlyAddConstituent(final IConstituent aConstituent) {
		// Yann 2004/12/20: Test and order II!
		// This method is used to add method invocations to
		// constructors and methods only because method
		// invocations must not be sorted.

		final int minCapacity = this.size + 1;
		final int oldCapacity = this.constituents.length;
		if (minCapacity > oldCapacity) {

			this.indexInPrimeNumbersArray++;
			int newCapacity;
			if (this.indexInPrimeNumbersArray < GenericContainerConstants.NUMBER_OF_PRIME_NUMBERS) {
				newCapacity = GenericContainerConstants.PRIME_NUMBERS[this.indexInPrimeNumbersArray];
			} else {
				newCapacity = oldCapacity
						+ GenericContainerConstants.PRIME_NUMBERS[GenericContainerConstants.NUMBER_OF_PRIME_NUMBERS / 2];
			}
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}

			final IConstituent[] oldData = this.constituents;
			this.constituents = new IConstituent[newCapacity];
			System.arraycopy(oldData, 0, this.constituents, 0, this.size);
		}
		this.constituents[this.size] = aConstituent;
		this.size++;

		this.directlyAddConstituentExtra();

		// int index = 0;
		// final int size = this.listOfConstituents.size();
		// final String constituentID = aConstituent.getID();
		// while (index < size
		// && ((IConstituent) this.listOfConstituents.get(index))
		// .getID()
		// .compareTo(constituentID) < 0) {
		// index++;
		// }
		// this.listOfConstituents.add(index, aConstituent);

		((Constituent) aConstituent)
				.updateWithIncomingPath(this.containerConsitituent.getPath());

		this.broadcastAdditionOfConstituent(aConstituent);
	}

	protected void directlyAddConstituentExtra() {
		// Default behaviour: do nothing.
	}

	// Sébastien Colladon 24/04/2012 : Visibility set to public...If you have
	// more than one ClassLoader then the only methods you can invoke on classes
	// from other ClassLoaders are public ones because private, package and
	// protected methods are all inaccessible due to being in a different
	// package (even when their package has the same name).
	public final void directlyAddConstituentWithUniqueID(
			final IConstituent aConstituent) {
		// Yann 2006/02/24: UniqueID...
		// I need a UniqueID for IMethodInvocation and IParemeter in IMethod
		// and for any IRelationship in IEntity. So, when using the method
		// concretelyAddConstituent(), I take care of the unique ID.
		this.addUniqueIDToEnclosedConstituent(aConstituent);
		this.directlyAddConstituent(aConstituent);
	}

	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.getConstituentFromID(anID) != null;
	}

	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.getConstituentFromName(aName) != null;
	}

	public Iterator getConcurrentIteratorOnConstituents() {
		// Yann 2005/10/12: Iterator!
		// I replace the list with an iterator, but this
		// is a major bottleneck in some specific case!
		// TODO: Implement my own "smart" iterator which
		// could be tightly linked with the AbstractContainer
		// class to prevent too many memory allocation (Singleton).
		// (The name of the method is deliberate for consistency
		// and also as a joke :-).)
		final IConstituent[] copyOfConstituents = new IConstituent[this.size];
		System.arraycopy(this.constituents, 0, copyOfConstituents, 0, this.size);
		return Arrays.asList(copyOfConstituents).iterator();
	}

	public Iterator getConcurrentIteratorOnConstituents(final IFilter aFilter) {
		// Yann 2005/10/12: Iterator!
		// I replace the list with an iterator, but this
		// is a major bottleneck in some specific case!
		// TODO: Implement my own "smart" iterator which
		// could be tightly linked with the AbstractContainer
		// class to prevent too many memory allocation (Singleton).
		final IConstituent[] copyOfConstituents = new IConstituent[this.size];
		System.arraycopy(this.constituents, 0, copyOfConstituents, 0, this.size);
		return Util.getFilteredConstituentsIterator(copyOfConstituents,
				this.size, aFilter);
	}

	public Iterator getConcurrentIteratorOnConstituents(
			final java.lang.Class aConstituentType) {
		// Yann 2005/10/12: Iterator!
		// I replace the list with an iterator, but this
		// is a major bottleneck in some specific case!
		// TODO: Implement my own "smart" iterator which
		// could be tightly linked with the AbstractContainer
		// class to prevent too many memory allocation (Singleton).
		// (The name of the method is deliberate for consistency
		// and also as a joke :-).)
		final IConstituent[] copyOfConstituents = new IConstituent[this.size];
		System.arraycopy(this.constituents, 0, copyOfConstituents, 0, this.size);
		return Util.getTypedConstituentsIterator(copyOfConstituents, this.size,
				aConstituentType);
	}

	public IConstituent getConstituentFromID(final char[] anID) {
		// Yann 2004/12/13: Performances
		// I cached the actorID to improve performance
		// (to avoid creating instances of Iterator over and over again).
		// I can now you the cached value to give me the index of the
		// constituent in the list of actors.
		// Yann 2009/04/29: Dirty!
		// I went back to this solution of using an iterator because
		// I am now storing the iterator :-)
		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			if (Arrays.equals(constituent.getID(), anID)) {
				return constituent;
			}
		}
		return null;
	}

	public IConstituent getConstituentFromID(final String anID) {
		return this.getConstituentFromID(anID.toCharArray());
	}

	public IConstituent getConstituentFromName(final char[] aName) {
		// Yann 2007/11/21: Performances
		// I cached the actorName to improve performance
		// (to avoid creating instances of Iterator over and over again).
		// I can now you the cached value to give me the index of the
		// constituent in the list of actors.
		// Yann 2009/04/29: Dirty!
		// I went back to this solution of using an iterator because
		// I am now storing the iterator :-)
		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			if (Arrays.equals(constituent.getName(), aName)) {
				return constituent;
			}
		}
		return null;
	}

	public IConstituent getConstituentFromName(final String aName) {
		return this.getConstituentFromName(aName.toCharArray());
	}

	public abstract Iterator getIteratorOnConstituents();

	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		// Yann 2005/10/12: Iterator!
		// I replace the list with an iterator, but this
		// is a major bottleneck in some specific case!
		// TODO: Implement my own "smart" iterator which
		// could be tightly linked with the AbstractContainer
		// class to prevent too many memory allocation (Singleton).
		// TODO: When using an array, the following implementation
		// is terribly inefficient!! Include directly the filter here.
		return Util.getFilteredConstituentsIterator(this.constituents,
				this.size, aFilter);
	}

	public Iterator getIteratorOnConstituents(
			final java.lang.Class aConstituentType) {

		// Yann 2005/10/12: Iterator!
		// I replace the list with an iterator, but this
		// is a major bottleneck in some specific case!
		// TODO: Implement my own "smart" iterator which
		// could be tightly linked with the AbstractContainer
		// class to prevent too many memory allocation (Singleton).
		// TODO: When using an array, the following implementation
		// is terribly inefficient!! Include directly the filter here.
		return Util.getTypedConstituentsIterator(this.constituents, this.size,
				aConstituentType);
	}

	public int getNumberOfConstituents() {
		// Yann 2005/10/12: Iterator!
		// I replace the list with an iterator, but this
		// is a major bottleneck in some specific case!
		return this.size;
	}

	public int getNumberOfConstituents(final java.lang.Class aConstituentType) {
		// Duc-Loc 2006/01/25: Consistency
		final Iterator iterator = Util.getTypedConstituentsIterator(
				this.constituents, this.size, aConstituentType);

		int size = 0;
		while (iterator.hasNext()) {
			iterator.next();
			size++;
		}

		return size;
		// Ask Yann why getTypedConstituentsList() is set to private arrrggg!!!
		// Because, we don't want to give access to sensible information :-)
	}

	public void removeAllConstituent() {
		// Yann 2002/04/06: Java collection library...
		// The Collection implementated in the JDK is "fail-fast".
		// It means that you cannot use an iterator on a collection
		// and modify the collection as you use the iterator.
		// Thus, I cannot iterate (using an iterator) over the
		// collection of PEntities to remove all them!
		this.size = 0;
	}

	public void removeConstituentFromID(final char[] anID) {
		this.removeConstituentFromID(this.getConstituentFromID(anID));
	}

	// /**
	// * This method returns a list of all the
	// * actors (instances of Entity) added to
	// *
	// * this model.
	// *
	// * @deprecated This method is dangerous because it exposes the
	// * inner working of all the constituents that are able to contains
	// * other constituents. Thus, it is now replaced with the two
	// * getIterator() and getIterator(Class aConstituentType) methods
	// * (and related methods).
	// */
	// public List listOfConstituents() {
	// return this.listOfConstituents;
	// }
	protected final void removeConstituentFromID(final IConstituent aConstituent) {
		// Salima and Yann 2009/08/07: Null constituent and improvement
		// Why is this method protected when it is called only from
		// removeConstituentFromID(final char[] anID)
		// TODO: Check protection
		// We now check for a null value for the parameter, in case someone
		// tries to remove a constituent that does not exist in the model.
		if (aConstituent != null) {
			int index = -1;
			for (int i = 0; i < this.size; i++) {
				if (this.constituents[i].equals(aConstituent)) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				if (index < this.size - 1) {
					System.arraycopy(this.constituents, index + 1,
							this.constituents, index, this.size - index - 1);
				}
				this.size--;
			}

			// Yann 2004/04/09: Listener!
			// It is now the responsibility of this class to manage
			// the listeners and to send the appropriate events when
			// a change occurs.
			// if (this instanceof IAbstractLevelModel) {
			// this.fireModelChange(
			// IModelListener.ENTITY_REMOVED,
			// new EntityEvent(
			// (IAbstractLevelModel) this,
			// (IFirstClassEntity) aConstituent));
			// }
			// else if (this instanceof IFirstClassEntity) {
			// this.fireModelChange(
			// IModelListener.ELEMENT_REMOVED,
			// new ElementEvent(
			// (IFirstClassEntity) this,
			// (IElement) aConstituent));
			// }
			// Yann 2011/06/11: Top-level entities...
			this.broadcastRemovalOfConstituent(aConstituent);

			// Yann 2009/05/06: Path
			// After having removed a constituent from a model,
			// I make sure that I "reset" its path so that I can
			// add it to another model. Also, I will test when
			// adding a constituent if it does not include path
			// information to prevent two models to share
			// constituents, maybe the clone process should be
			// involved too... I'll think about it later.
			final int startIndexInclusive = ArrayUtils.lastIndexOf(
					aConstituent.getPath(), IConstants.ELEMENT_SYMBOL) + 1;
			final int endIndexExclusive = aConstituent.getPath().length;
			((Constituent) aConstituent).setPath(ArrayUtils.subarray(
					aConstituent.getPath(), startIndexInclusive,
					endIndexExclusive));
		}
	}

	protected void resetListOfConstituents() {
		this.resetListOfConstituents(4);
	}

	protected void resetListOfConstituents(final int anInitialCapacity) {
		// Yann 2004/04/09: Clone!
		// I make sure that a clone of an abstract level
		// model gets a new instance of an array list.
		// (See also method AbstractLevelModel.clone().)
		this.constituents = new IConstituent[GenericContainerConstants.PRIME_NUMBERS[this.indexInPrimeNumbersArray]];
		this.size = 0;
	}
}
