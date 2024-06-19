/*
 * (c) Copyright 2001, 2002 Hervé Albin-Amiot and Yann-Gaël Guéhéneuc,
 * Ecole des Mines de Nantes
 * Object Technology International, Inc.
 * Soft-Maint S.A.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * the authors, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF THE AUTHORS ARE ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 */
package padl.kernel;

public interface IVisitor {
	void close(final IAbstractLevelModel anAbstractLevelModel);
	void close(final IClass aClass);
	void close(final IConstructor aConstructor);
	void close(final IDelegatingMethod aDelegatingMethod);
	void close(final IDesignMotifModel aPatternModel);
	void close(final IGetter aGetter);
	void close(final IGhost aGhost);
	void close(final IInterface anInterface);
	void close(final IMemberClass aMemberClass);
	void close(final IMemberGhost aMemberGhost);
	void close(final IMemberInterface aMemberInterface);
	void close(final IMethod aMethod);
	void close(final IPackage aPackage);
	// TODO: Add close(final IPackageGhost aPackageGhost);
	// void close(final IPackageGhost aPackageGhost);
	void close(final ISetter aSetter);
	String getName();
	void open(final IAbstractLevelModel anAbstractLevelModel);
	void open(final IClass aClass);
	void open(final IConstructor aConstructor);
	void open(final IDelegatingMethod aDelegatingMethod);
	void open(final IDesignMotifModel aPatternModel);
	void open(final IGetter aGetter);
	void open(final IGhost aGhost);
	void open(final IInterface anInterface);
	void open(final IMemberClass aMemberClass);
	void open(final IMemberGhost aMemberGhost);
	void open(final IMemberInterface aMemberInterface);
	void open(final IMethod aMethod);
	void open(final IPackage aPackage);
	// TODO: Add open(final IPackageGhost aPackageGhost);
	// void open(final IPackageGhost aPackageGhost);
	void open(final ISetter aSetter);
	void reset();
	void visit(final IAggregation anAggregation);
	void visit(final IAssociation anAssociation);
	void visit(final IComposition aComposition);
	void visit(final IContainerAggregation aContainerAggregation);
	void visit(final IContainerComposition aContainerComposition);
	void visit(final ICreation aCreation);
	void visit(final IField aField);
	void visit(final IMethodInvocation aMethodInvocation);
	void visit(final IParameter aParameter);
	void visit(final IUseRelationship aUse);
	void unknownConstituentHandler(
		final String aCalledMethodName,
		final IConstituent aConstituent);
}
