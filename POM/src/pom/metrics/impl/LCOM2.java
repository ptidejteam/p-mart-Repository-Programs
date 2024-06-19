package pom.metrics.impl;

import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * LCOM2 - Lack of COhesion in Methods Version 2
 * 
 * @author Farouk ZAIDI
 * @since  2004/01/31 
 * 
 * @author Duc-Loc Huynh
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */
public class LCOM2 extends AbstractLCOM implements IMetric, IUnaryMetric {
	public LCOM2(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		final double numberOfCouples =
			super.pairsOfMethodNotSharingFields(anEntity);
		final List methodsOfClass =
			super.classPrimitives.listOfDeclaredMethods(anEntity);
		final double nbPairsOfMethodsSharingField =
			(methodsOfClass.size() * (methodsOfClass.size() - 1) - numberOfCouples) / 2;
		final double nbPairsOfMethodsWithoutSharedField = numberOfCouples / 2;
		double lcom =
			nbPairsOfMethodsWithoutSharedField - nbPairsOfMethodsSharingField;
		if (lcom < 0) {
			lcom = 0;
		}
		return lcom;
	}
	public String getDefinition() {
		final String def =
			"Returns the LCOM (Lack of COhesion in Methods) of an entity.";
		return def;
	}
}
