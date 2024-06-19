/**
 * NMA - Number of Methods A******  Ask Yann
 * 
 * @author Farouk ZAIDI
 * @since  2004/01/31 
 * 
 * @author Duc-Loc Huynh
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */
package pom.metrics.impl;

import java.util.Collection;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NMA extends AbstractMetric implements IMetric, IUnaryMetric {
	public NMA(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return listOfElements(firstClassEntity).size();
	}

	public String getDefinition() {
		String def = "Returns the NMA (Number of New Methods) of an entity.";
		System.out.println(def);
		return def;
	}

	private Collection listOfElements(IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfNewMethods(firstClassEntity);
	}
}
