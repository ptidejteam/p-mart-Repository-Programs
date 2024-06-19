package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * LCOM1 - Lack of COhesion in Methods Version 1
 * 
 * @author Farouk ZAIDI
 * @since  2004/01/31 
 * 
 * @author Duc-Loc Huynh
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */
public class LCOM1 extends AbstractLCOM implements IMetric, IUnaryMetric {
	public LCOM1(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		return super.pairsOfMethodNotSharingFields(anEntity);
	}
	public String getDefinition() {
		String def = "Lack of COhesion in Methods of an entity";
		return def;
	}
}
