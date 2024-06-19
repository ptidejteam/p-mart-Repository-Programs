/*
 * (c) Copyright 2001-2003 Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN,
 * ANY LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
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
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfModel;
import padl.kernel.IDesignMotifModel;
import padl.kernel.IDetector;
import padl.kernel.IFactory;
import padl.kernel.IFilter;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IGenerator;
import padl.kernel.IVisitor;
import padl.kernel.IWalker;
import padl.kernel.exception.ModelDeclarationException;
import padl.path.IConstants;
import util.io.Output;
import util.multilingual.MultilingualManager;

class DesignMotifModel extends Constituent implements IDesignMotifModel {
	private static final long serialVersionUID = 5943218392258815564L;

	private int classification;
	private GenericContainerOfConstituents constituentContainer =
		new GenericContainerOfNaturallyOrderedConstituents(this);
	private IDetector defaultDetector;
	private IFactory factory;
	private String intent;
	private GenericContainerOfTopLevelEntities topLevelEntitiesContainer =
		new GenericContainerOfTopLevelEntities(this);

	public DesignMotifModel(final char[] anID) {
		super(anID);

		this.addModelListener(this.topLevelEntitiesContainer.getListener());
	}
	public void accept(final IVisitor visitor) {
		visitor.open(this);
		final Iterator iterator = this.getConcurrentIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			// System.out.println(constituent.toString());
			constituent.accept(visitor);
		}
		visitor.close(this);
	}
	public final void addConstituent(final IConstituentOfModel aConstituent)
			throws ModelDeclarationException {

		if (aConstituent instanceof IFirstClassEntity) {
			this.constituentContainer
				.addConstituent((IFirstClassEntity) aConstituent);
			aConstituent.addModelListener(this.topLevelEntitiesContainer
				.getListener());
		}
		else {
			throw new ModelDeclarationException(MultilingualManager.getString(
				"ENT_ADD_MODEL",
				DesignMotifModel.class));
		}
	}
	public void addModelListener(final IModelListener aModelListener) {
		this.constituentContainer.addModelListener(aModelListener);
	}
	public void addModelListeners(final List someModelListeners) {
		this.constituentContainer.addModelListeners(someModelListeners);
	}
	public final Object clone() throws CloneNotSupportedException {
		// Yann: Here is the new clone protocole. This is subject
		// to discussion!! The idea is that a pattern may be cloned,
		// but a constituent of a pattern (Element or Entity) may
		// not be cloned individually, it does not make sense to clone
		// a Method if the rest of the pattern is not cloned to. I
		// believe this is actually a risk: to be able to clone a single
		// constituent on an individual basis may prove to create
		// duplicates with references on current objects...
		// Thus, only the AbstractLevelModel class implements the
		// Cloneable interface.
		// The constituents of the pattern implements the
		// ICloneable interface. This pattern provides
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

		final DesignMotifModel clonedMotifModel =
			(DesignMotifModel) super.clone();
		// Yann 2002/04/08: Clone!
		// The cloned pattern must have its own instance
		// of class ArrayList for the list or PEntities.
		// Yann 2010/10/03: Objects!
		// The "container" is now an instance of a class
		// and must be assigned a new instance independently.
		//	clonedPattern.constituentContainer.resetListOfConstituents();
		clonedMotifModel.constituentContainer =
			new GenericContainerOfNaturallyOrderedConstituents(clonedMotifModel);
		clonedMotifModel.topLevelEntitiesContainer =
			new GenericContainerOfTopLevelEntities(clonedMotifModel);
		clonedMotifModel
			.addModelListener(clonedMotifModel.topLevelEntitiesContainer
				.getListener());

		// First, I make a shallow copy of all the entities.
		Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			final IConstituent constituent = (IConstituent) iterator.next();
			constituent.startCloneSession();
			try {
				clonedMotifModel
					.addConstituent((IConstituentOfModel) constituent
						.getClone());
			}
			catch (final ModelDeclarationException pde) {
				pde.printStackTrace(Output.getInstance().errorOutput());
			}
		}

		// Second, I update the links among entities (deep copy).
		iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).performCloneSession();
		}

		// Finally, I clean up all temporary instance variable.
		iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).endCloneSession();
		}

		// Yann 2011/06/20: Top-level entities...
		this.topLevelEntitiesContainer
			.createMapOfIDsEntities(Constants.FORBIDDEN_ID);

		return clonedMotifModel;
	}
	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.constituentContainer.doesContainConstituentWithID(anID);
	}
	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.constituentContainer.doesContainConstituentWithName(aName);
	}
	public void fireModelChange(final String anEventType, final IEvent anEvent) {
		this.constituentContainer.fireModelChange(anEventType, anEvent);
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
	public int getClassification() {
		return this.classification;
	}
	public Iterator getConcurrentIteratorOnConstituents() {
		return this.constituentContainer.getConcurrentIteratorOnConstituents();
	}
	public Iterator getConcurrentIteratorOnConstituents(final IFilter filter) {
		return this.constituentContainer.getConcurrentIteratorOnConstituents();
	}
	public Iterator getConcurrentIteratorOnConstituents(
		final java.lang.Class aConstituentType) {

		return this.constituentContainer
			.getConcurrentIteratorOnConstituents(aConstituentType);
	}
	public IConstituent getConstituentFromID(final char[] anID) {
		return this.constituentContainer.getConstituentFromID(anID);
	}
	public IConstituent getConstituentFromID(final String anID) {
		return this.getConstituentFromID(anID.toCharArray());
	}
	public IConstituent getConstituentFromName(final char[] aName) {
		return this.constituentContainer.getConstituentFromName(aName);
	}
	public IConstituent getConstituentFromName(final String aName) {
		return this.getConstituentFromName(aName.toCharArray());
	}
	/**
	 * This method returns a list of hashtable. Each hashtable represents a solution
	 * found. A solution is a set of keys representing the entities, associated to the
	 * real entities found in the given source code.
	 */
	//	public List compare(final IAbstractModel model) {
	//		final List solutions = new ArrayList();
	//		Map partialSolutions;
	//
	//		if (this.getDetector() == null) {
	//			OutputManager.getCurrentOutputManager().getErrorOutput().println(
	//				MultilanguageManager.getStrResource(
	//					"Err_INIT_ALMD",
	//					resourceBaseName
	//				)
	//			);
	//			return new ArrayList();
	//		}
	//		this.getDetector().setPattern(this);
	//		partialSolutions =
	//			((Detector) this.getDetector()).buildPartialSolution(model);
	//		if (partialSolutions.size() > 0) {
	//			partialSolutions =
	//				((Detector) this.getDetector()).applyCriterias(
	//					partialSolutions,
	//					Detector.AllCriterias);
	//			if (partialSolutions.size() > 0) {
	//				solutions.add(partialSolutions);
	//			}
	//		}
	//		return solutions;
	//	}
	public final IDetector getDetector() {
		return this.defaultDetector;
	}
	public IFactory getFactory() {
		return this.factory;
	}
	public String getIntent() {
		return this.intent;
	}
	public Iterator getIteratorOnConstituents() {
		return this.constituentContainer.getIteratorOnConstituents();
	}
	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		return this.constituentContainer.getIteratorOnConstituents(aFilter);
	}
	public Iterator getIteratorOnConstituents(java.lang.Class aConstituentType) {
		return this.constituentContainer
			.getIteratorOnConstituents(aConstituentType);
	}
	public Iterator getIteratorOnModelListeners() {
		return this.constituentContainer.getIteratorOnModelListeners();
	}
	public Iterator getIteratorOnTopLevelEntities() {
		return this.topLevelEntitiesContainer.getIteratorOnTopLevelEntities();
	}
	public int getNumberOfConstituents() {
		return this.constituentContainer.getNumberOfConstituents();
	}
	public int getNumberOfConstituents(final java.lang.Class aConstituentType) {
		return this.constituentContainer
			.getNumberOfConstituents(aConstituentType);
	}
	public int getNumberOfTopLevelEntities() {
		return this.topLevelEntitiesContainer.getNumberOfTopLevelEntities();
	}
	public int getNumberOfTopLevelEntities(final java.lang.Class anEntityType) {
		return this.topLevelEntitiesContainer
			.getNumberOfTopLevelEntities(anEntityType);
	}
	public char[] getPath() {
		return this.getID();
	}
	protected char getPathSymbol() {
		return IConstants.DESIGN_MOTIF_MODEL_SYMBOL;
	}
	public IFirstClassEntity getTopLevelEntityFromID(final char[] anID) {
		return this.topLevelEntitiesContainer.getTopLevelEntityFromID(anID);
	}
	public IFirstClassEntity getTopLevelEntityFromID(final String anID) {
		return this.topLevelEntitiesContainer.getTopLevelEntityFromID(anID
			.toCharArray());
	}
	public void removeAllConstituent() {
		this.constituentContainer.removeAllConstituent();
	}
	public void removeConstituentFromID(final char[] anID) {
		this.constituentContainer.removeConstituentFromID(anID);
	}
	public void removeModelListener(final IModelListener aModelListener) {
		this.constituentContainer.removeModelListener(aModelListener);
	}
	public void removeModelListeners(final List aListOfModelListeners) {
		this.constituentContainer.removeModelListeners(aListOfModelListeners);
	}
	public void removeTopLevelEntityFromID(final char[] anID) {
		this.topLevelEntitiesContainer.removeTopLevelEntityFromID(anID);
	}
	public void setClassification(final int aClassification) {
		this.classification = aClassification;
	}
	public final void setDetector(final IDetector aPatternDetector) {
		this.defaultDetector = aPatternDetector;
	}
	public void setFactory(final IFactory aFactory) {
		this.factory = aFactory;
	}
	public void setIntent(final String anIntent) {
		this.intent = anIntent;
	}
	public String toString() {
		return this.toString(0);
	}
	public String toString(final int tab) {
		final StringBuffer codeEq = new StringBuffer();
		codeEq.append("Model of design motif ");
		codeEq.append(this.getName());
		return codeEq.toString();
	}
	public final Object walk(final IWalker walker) {
		walker.open(this);
		final Iterator iterator = this.getIteratorOnConstituents();
		while (iterator.hasNext()) {
			((IConstituent) iterator.next()).accept(walker);
		}

		walker.close(this);
		return walker.getResult();
	}
}
