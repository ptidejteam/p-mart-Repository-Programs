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

import padl.kernel.IFactory;
import padl.kernel.IIfInstruction;
import padl.kernel.ISwitchInstruction;

/**
 * @author Yousra Tagmouti
 * @author Yann-Gaël Guéhéneuc
 */
public class StatementFactory extends Factory implements IFactory {
	private static final long serialVersionUID = -7857386029686909308L;

	private static StatementFactory UniqueInstance;
	public static IFactory getInstance() {
		if (StatementFactory.UniqueInstance == null) {
			StatementFactory.UniqueInstance = new StatementFactory();
		}
		return StatementFactory.UniqueInstance;
	}

	private StatementFactory() {
	}
	//	public IIterator createIteratorS(
	//		final IMethod anmethod,
	//		final String aCondition) throws ModelDeclarationException {
	//
	//		return new IteratorS(anmethod, aCondition);
	//	}
	//	public IIterator createIteratorSI(
	//		final IInterface anInterface,
	//		final String aCondition) throws ModelDeclarationException {
	//
	//		return new IteratorS(anInterface, aCondition);
	//	}
	//	public IIterator createIteratorSF(
	//		final IField anfield,
	//		final String aCondition) throws ModelDeclarationException {
	//
	//		return new IteratorS(anfield, aCondition);
	//
	//	}
	//	public IConditional createConditionelS() throws ModelDeclarationException {
	//		return new OLDConditional();
	//	}
	//	public IConditional createConditionelS(final IMethod aBooleanValueMethod)
	//			throws ModelDeclarationException {
	//
	//		return new OLDConditional(aBooleanValueMethod);
	//	}
	//	public IBlock createBlock() throws ModelDeclarationException {
	//		return new Block();
	//	}
	//	public IStatement createStatement(final String aName, final String aType)
	//			throws ModelDeclarationException {
	//
	//		return new Statement(aName, aType);
	//	}
	//	public IStatement createStatement(final String aType)
	//			throws ModelDeclarationException {
	//
	//		return new Statement(aType);
	//	}
	//	public IUseRelationship createUseRelationship(
	//		final String anID,
	//		final IEntity aTargetEntity,
	//		final int aCardinality) throws ModelDeclarationException {
	//
	//		return new UseRelationship(anID, aTargetEntity, aCardinality);
	//	}
	public IIfInstruction createIfInstruction() {
		return new IfInstruction();
	}
	public ISwitchInstruction createSwitchInstruction(final int aRange) {
		return new SwitchInstruction(aRange);
	}
}
