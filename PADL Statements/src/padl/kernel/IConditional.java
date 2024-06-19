package padl.kernel;

import padl.kernel.IMethod;
import padl.kernel.exception.ModelDeclarationException;

/**
 * @author tagmouty
 */
public interface IConditional extends IStatement {
	String LOGO = "\"IC\"";
	IMethod getCondition();
	// IBlock getElseBlock();
	// IBlock getIfBlock();
	// Iterator getIteratorOnEntities();
	// boolean hasBlock();
	void setCondition(final IMethod m) throws ModelDeclarationException;
	// void setEleseBlock(final IBlock c);
	// void setIfBlock(final IBlock b);
}
