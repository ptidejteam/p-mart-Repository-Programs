package padl.kernel;

import java.util.Iterator;
import padl.kernel.exception.ModelDeclarationException;

/**
 * @author Yousra
 */
public interface IBlock extends IStatement {
	void addConsituent(final IStatement anStatement)
			throws ModelDeclarationException;
	Iterator getIteratorOnConstituents();
}
