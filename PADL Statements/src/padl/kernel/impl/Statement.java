package padl.kernel.impl;

import padl.kernel.IBlock;
import padl.kernel.IStatement;

/**
 * @author tagmouty
 * @author Yann
 */
class Statement extends Element implements IStatement {
	private static final long serialVersionUID = 6348055409249029367L;

	private IBlock elseBlock;
	private boolean hasElseBlock = false;
	private IBlock ifblock;

	public Statement(final char[] anID) {
		super(anID);
	}
	public IBlock Geteleblock() {
		return this.elseBlock;
	}
	public IBlock getIfBlock() {
		return this.ifblock;
	}
	public boolean hasBlock() {
		return this.hasElseBlock;
	}
	public void setElseBlock(final IBlock aElseBlock) {
		this.hasElseBlock = true;
		this.elseBlock = aElseBlock;
	}
	public void setIfBlock(final IBlock aIfBlock) {
		this.ifblock = aIfBlock;
	}
}
