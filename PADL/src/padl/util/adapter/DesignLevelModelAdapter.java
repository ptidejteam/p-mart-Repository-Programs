/* (c) Copyright 2001 and following years, Yann-Gaël Guéhéneuc,
 * University of Montreal.
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
package padl.util.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import padl.event.IEvent;
import padl.event.IModelListener;
import padl.kernel.IConstituent;
import padl.kernel.IConstituentOfModel;
import padl.kernel.IDesignLevelModel;
import padl.kernel.IDesignMotifModel;
import padl.kernel.IFactory;
import padl.kernel.IFilter;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IGenerator;
import padl.kernel.IIdiomLevelModel;
import padl.kernel.IPackage;
import padl.kernel.IWalker;
import padl.kernel.exception.ModelDeclarationException;
import util.io.Output;

/**
 * 
 * @author Yann
 * @since  2009/03/07
 *
 */

public class DesignLevelModelAdapter implements IDesignLevelModel {
	private static final long serialVersionUID = -265422879837345649L;

	private final IIdiomLevelModel wrappedIdiomLevelModel;
	public DesignLevelModelAdapter(final IIdiomLevelModel anIdiomLevelModel) {
		this.wrappedIdiomLevelModel = anIdiomLevelModel;
	}

	public void addConstituent(final IConstituentOfModel aPackage)
			throws ModelDeclarationException {
		this.wrappedIdiomLevelModel.addConstituent(aPackage);
	}

	public void addConstituent(final IDesignMotifModel aDesignMotifModel)
			throws ModelDeclarationException {

		// Yann 2009/03/07: Parents!
		// I must call the addConstituent() method of the
		// super-class of the wrapped idiom-level model
		// because only this method (invisible to the
		// outside world) can accept a DesignMotideModel.
		final Class thisClass = this.wrappedIdiomLevelModel.getClass();
		final Class abstractLevelModel = thisClass.getSuperclass();
		final Class abstractContainer = abstractLevelModel.getSuperclass();
		try {
			final Method addConstituentMethod =
				abstractContainer.getMethod(
					"addConstituent",
					new Class[] { IConstituent.class });
			addConstituentMethod.invoke(
				this.wrappedIdiomLevelModel,
				new Object[] { aDesignMotifModel });
		}
		catch (final SecurityException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
		catch (final IllegalArgumentException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
		catch (final NoSuchMethodException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
		catch (final IllegalAccessException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
		catch (final InvocationTargetException e) {
			e.printStackTrace(Output.getInstance().errorOutput());
		}
	}

	public void addConstituent(final IPackage aConstituent)
			throws ModelDeclarationException {
		this.wrappedIdiomLevelModel.addConstituent(aConstituent);
	}

	public void addModelListener(final IModelListener aModelListener) {
		this.wrappedIdiomLevelModel.addModelListener(aModelListener);
	}

	public void addModelListeners(final List aListOfModelListeners) {
		this.wrappedIdiomLevelModel.addModelListeners(aListOfModelListeners);
	}

	public Object clone() throws CloneNotSupportedException {
		return this.wrappedIdiomLevelModel.clone();
	}

	public boolean doesContainConstituentWithID(final char[] anID) {
		return this.wrappedIdiomLevelModel.doesContainConstituentWithID(anID);
	}

	public boolean doesContainConstituentWithName(final char[] aName) {
		return this.wrappedIdiomLevelModel
			.doesContainConstituentWithName(aName);
	}

	public void fireModelChange(final String anEventType, final IEvent anEvent) {
		this.wrappedIdiomLevelModel.fireModelChange(anEventType, anEvent);
	}

	public String generate(final IGenerator aBuilder) {
		return this.wrappedIdiomLevelModel.generate(aBuilder);
	}

	public Iterator getConcurrentIteratorOnConstituents() {
		return this.wrappedIdiomLevelModel
			.getConcurrentIteratorOnConstituents();
	}

	public Iterator getConcurrentIteratorOnConstituents(
		final Class aConstituentType) {
		return this.wrappedIdiomLevelModel
			.getConcurrentIteratorOnConstituents(aConstituentType);
	}

	public Iterator getConcurrentIteratorOnConstituents(final IFilter aFilter) {
		return this.wrappedIdiomLevelModel
			.getConcurrentIteratorOnConstituents(aFilter);
	}

	public IConstituent getConstituentFromID(final char[] anID) {
		return this.wrappedIdiomLevelModel.getConstituentFromID(anID);
	}

	public IConstituent getConstituentFromID(final String anID) {
		return this.wrappedIdiomLevelModel.getConstituentFromID(anID);
	}

	public IConstituent getConstituentFromName(final char[] aName) {
		return this.wrappedIdiomLevelModel.getConstituentFromName(aName);
	}

	public IConstituent getConstituentFromName(final String aName) {
		return this.wrappedIdiomLevelModel.getConstituentFromName(aName.toCharArray());
	}

	public String getDisplayName() {
		return this.wrappedIdiomLevelModel.getDisplayName();
	}

	public String getDisplayPath() {
		return this.wrappedIdiomLevelModel.getDisplayPath();
	}

	public IFactory getFactory() {
		return this.wrappedIdiomLevelModel.getFactory();
	}

	public Iterator getIteratorOnConstituents() {
		return this.wrappedIdiomLevelModel.getIteratorOnConstituents();
	}

	public Iterator getIteratorOnConstituents(final Class aConstituentType) {
		return this.wrappedIdiomLevelModel
			.getIteratorOnConstituents(aConstituentType);
	}

	public Iterator getIteratorOnConstituents(final IFilter aFilter) {
		return this.wrappedIdiomLevelModel.getIteratorOnConstituents(aFilter);
	}

	public Iterator getIteratorOnModelListeners() {
		return this.wrappedIdiomLevelModel.getIteratorOnModelListeners();
	}

	public Iterator getIteratorOnTopLevelEntities() {
		return this.wrappedIdiomLevelModel.getIteratorOnTopLevelEntities();
	}

	public char[] getName() {
		return this.wrappedIdiomLevelModel.getName();
	}

	public int getNumberOfConstituents() {
		return this.wrappedIdiomLevelModel.getNumberOfConstituents();
	}

	public int getNumberOfConstituents(final Class aConstituentType) {
		return this.wrappedIdiomLevelModel
			.getNumberOfConstituents(aConstituentType);
	}

	public int getNumberOfTopLevelEntities() {
		return this.wrappedIdiomLevelModel.getNumberOfTopLevelEntities();
	}

	public int getNumberOfTopLevelEntities(final Class aClass) {
		return this.wrappedIdiomLevelModel.getNumberOfTopLevelEntities(aClass);
	}

	public char[] getPath() {
		return this.wrappedIdiomLevelModel.getPath();
	}

	public IFirstClassEntity getTopLevelEntityFromID(final char[] anID) {
		return this.wrappedIdiomLevelModel.getTopLevelEntityFromID(anID);
	}

	public IFirstClassEntity getTopLevelEntityFromID(final String anID) {
		return this.wrappedIdiomLevelModel.getTopLevelEntityFromID(anID);
	}

	public void removeAllConstituent() {
		this.wrappedIdiomLevelModel.removeAllConstituent();
	}

	public void removeConstituentFromID(final char[] anID) {
		this.wrappedIdiomLevelModel.removeConstituentFromID(anID);
	}

	public void removeModelListener(final IModelListener aModelListener) {
		this.wrappedIdiomLevelModel.removeModelListener(aModelListener);
	}

	public void removeModelListeners(final List aListOfModelListeners) {
		this.wrappedIdiomLevelModel.removeModelListeners(aListOfModelListeners);
	}

	public void removeTopLevelEntityFromID(final char[] anID) {
		this.wrappedIdiomLevelModel.removeTopLevelEntityFromID(anID);
	}

	public void setFactory(final IFactory aFactory) {
		this.wrappedIdiomLevelModel.setFactory(aFactory);
	}

	public Object walk(final IWalker aWalker) {
		return this.wrappedIdiomLevelModel.walk(aWalker);
	}
}
