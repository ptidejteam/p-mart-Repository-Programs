package padl.kernel.impl;

import java.util.Iterator;
import padl.kernel.IBlock;
import padl.kernel.IStatement;
import padl.kernel.exception.ModelDeclarationException;

/**
 * @author tagmouty
 */
class Block extends Element implements IBlock {
	private static final long serialVersionUID = 788273358202402182L;

	private final GenericContainerOfConstituents container =
		new GenericContainerOfInsertionOrderedConstituents(
			this,
			GenericContainerConstants.INITIAL_SIZE_ENTITIES);
	public Block(final char[] anID) {
		super(anID);
	}
	public void addConsituent(final IStatement anStatement)
			throws ModelDeclarationException {

		this.container.directlyAddConstituentWithUniqueID(anStatement);
	}
	public Iterator getIteratorOnConstituents() {
		return this.container.getIteratorOnConstituents();
	}
}