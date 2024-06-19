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
package padl.kernel.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import padl.event.IEvent;
import padl.event.IModelListener;
import padl.kernel.IConstituent;
import padl.kernel.IPrimitiveEntity;
import padl.kernel.IVisitor;
import padl.kernel.exception.ModelDeclarationException;
import util.lang.Modifier;

public class PrimitiveEntity extends Constituent implements IPrimitiveEntity {
	private static final long serialVersionUID = 3604943383503049188L;

	// Yann 2010/10/10: DB4O
	// Used to be:
	//	private final char[] getName();
	// I removed the final to make DB4O works...
	// TODO: Understand how to keep it final with DB4O!
	public PrimitiveEntity(final char[] aPrimitiveEntityName) {
		super(aPrimitiveEntityName);
	}
	public void accept(final IVisitor visitor) {
	}
	public boolean equals(final Object anotherPrimitiveEntity) {
		if (!(anotherPrimitiveEntity instanceof PrimitiveEntity)) {
			return false;
		}
		else {
			return Arrays.equals(
				this.getName(),
				((PrimitiveEntity) anotherPrimitiveEntity).getName());
		}
	}

	protected char getPathSymbol() {
		return 0;
	}
	public void addModelListener(IModelListener aModelListener) {
		
	}
	public void addModelListeners(List aListOfModelListeners) {
		
	}
	public void fireModelChange(String anEventType, IEvent anEvent) {
		
	}
	public Iterator getIteratorOnModelListeners() {
		return null;
	}
	public void removeModelListener(IModelListener aModelListener) {
		
	}
	public void removeModelListeners(List modelListeners) {
		
	}
	
}
