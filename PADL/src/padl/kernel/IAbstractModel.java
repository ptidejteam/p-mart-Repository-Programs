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
package padl.kernel;

import java.io.Serializable;
import java.util.Iterator;
import padl.kernel.exception.ModelDeclarationException;

/**
 * @author Yann-Gaël Guéhéneuc
 */
public interface IAbstractModel extends IContainer, INavigable, IObservable,
		Serializable {

	void addConstituent(final IConstituentOfModel aConstituent)
			throws ModelDeclarationException;
	Object clone() throws CloneNotSupportedException;
	String generate(final IGenerator aBuilder);
	String getDisplayName();
	IFactory getFactory();
	Iterator getIteratorOnTopLevelEntities();
	char[] getName();
	int getNumberOfTopLevelEntities();
	int getNumberOfTopLevelEntities(final java.lang.Class aClass);
	IFirstClassEntity getTopLevelEntityFromID(final char[] anID);
	IFirstClassEntity getTopLevelEntityFromID(final String anID);
	void removeTopLevelEntityFromID(final char[] anID);
	void setFactory(final IFactory aFactory);
	Object walk(final IWalker aWalker);
}
