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
package padl.kernel;

import padl.kernel.exception.ModelDeclarationException;

/**
 * @author Yann-Gaël Guéhéneuc
 * @since  2003/12/05
 */
public interface IFactory {
	IAggregation createAggregationRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
	IAssociation createAssociationRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
	IClass createClass(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	ICodeLevelModel createCodeLevelModel(final char[] aName);
	ICodeLevelModel createCodeLevelModel(final String aName);
	IComposition createCompositionRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
	IComposition createCompositionRelationship(final IAssociation anAssociation)
			throws ModelDeclarationException;
	IConstructor createConstructor(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IContainerAggregation createContainerAggregationRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
	IContainerComposition createContainerCompositionRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
	IContainerComposition createContainerCompositionRelationship(
		final IAssociation anAssociation) throws ModelDeclarationException;
	ICreation createCreationRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
	//	IDelegatingMethod createDelegatingMethod(
	//		final char[] aName,
	//		final IAssociation aTargetAssociation) throws ModelDeclarationException;
	IDelegatingMethod createDelegatingMethod(
		final char[] aName,
		final IAssociation aTargetAssociation,
		final IMethod aSupportMethod) throws ModelDeclarationException;
	IDesignLevelModel createDesignLevelModel(final char[] aName);
	IDesignMotifModel createDesignMotifModel(final char[] aName) throws ModelDeclarationException;
	IField createField(
		final char[] anID,
		final char[] aName,
		final char[] aType,
		final int aCardinality) throws ModelDeclarationException;
	IGetter createGetter(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IGetter createGetter(final IMethod aMethod)
			throws ModelDeclarationException;
	IGhost createGhost(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	public IFirstClassEntity createHierarchyRoot();
	IIdiomLevelModel createIdiomLevelModel(final char[] aName);
	IInterface createInterface(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IMemberClass createMemberClass(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IMemberGhost createMemberGhost(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IMemberInterface createMemberInterface(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IMethod createMethod(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	IMethodInvocation createMethodInvocation(
		final int type,
		final int cardinality,
		final int visibility,
		final IFirstClassEntity targetEntity) throws ModelDeclarationException;
	IMethodInvocation createMethodInvocation(
		final int type,
		final int cardinality,
		final int visibility,
		final IFirstClassEntity targetEntity,
		final IFirstClassEntity entityDeclaringField)
			throws ModelDeclarationException;
	IPackage createPackage(final char[] aName) throws ModelDeclarationException;
	IPackageDefault createPackageDefault() throws ModelDeclarationException;
	IPackageGhost createPackageGhost(final char[] aName)
			throws ModelDeclarationException;
	IParameter createParameter(
		final IEntity aType,
		final char[] aName,
		final int aCardinality) throws ModelDeclarationException;
	IParameter createParameter(final IEntity aType, final int aCardinality)
			throws ModelDeclarationException;
	IPrimitiveEntity createPrimitiveEntity(final char[] aPrimitiveEntityName)
			throws ModelDeclarationException;
	ISetter createSetter(final char[] anID, final char[] aName)
			throws ModelDeclarationException;
	ISetter createSetter(final IMethod aMethod)
			throws ModelDeclarationException;
	IUseRelationship createUseRelationship(
		final char[] aName,
		final IFirstClassEntity aTargetEntity,
		final int aCardinality) throws ModelDeclarationException;
}
