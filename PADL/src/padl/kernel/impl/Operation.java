/*
 * (c) Copyright 2001-2003 Yann-Gaël Guéhéneuc,
 * École des Mines de Nantes and Object Technology International, Inc.
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

import java.util.Iterator;
import java.util.List;
import padl.event.IEvent;
import padl.event.IModelListener;
import padl.kernel.IOperation;

//Sebastien Colladon 23/04/2012 : Change the visibility to public in order to allow other project to extend from this class in the particular case of eclipse bundle loader (avoid IllegalAccessError).
public abstract class Operation extends Element implements IOperation {
	private static final long serialVersionUID = 8145249048497089055L;

	public Operation(final char[] actorID) {
		super(actorID);
	}

	private GenericObservable observable = new GenericObservable();
	public void addModelListener(final IModelListener aModelListener) {
		this.observable.addModelListener(aModelListener);
	}
	public void addModelListeners(final List aListOfModelListeners) {
		this.observable.addModelListeners(aListOfModelListeners);
	}
	public void removeModelListeners(final List aListOfModelListeners) {
		this.observable.removeModelListeners(aListOfModelListeners);
	}
	public void fireModelChange(final String anEventType, final IEvent anEvent) {
		this.observable.fireModelChange(anEventType, anEvent);
	}
	public Iterator getIteratorOnModelListeners() {
		return this.observable.getIteratorOnModelListeners();
	}
	public void removeModelListener(final IModelListener aModelListener) {
		this.observable.removeModelListener(aModelListener);
	}
}
