package pom.metrics.impl;

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IMethodInvocation;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * WMC - Weight of Methods Computed
 * 
 * @author Farouk ZAIDI
 * @since  2004/01/31 
 * 
 * @author Duc-Loc Huynh
 * @since  2005/08/18
 * 
 * Modifications made to fit the new architecture
 */
public class WMC extends AbstractMetric implements IMetric, IUnaryMetric {
	public WMC(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		//JYves 2006/02/24:
		// weight initialisé à 0 car une classe sans aucune 
		// méthodes (ni constructeur par défaut) a un WMC de 0.
		//		double weight = 1;
		double weight = 0;

		final Iterator iterAbstractMethod =
			anEntity.getIteratorOnConstituents(IOperation.class);
		while (iterAbstractMethod.hasNext()) {
			final IOperation method =
				(IOperation) iterAbstractMethod.next();
			weight +=
				method.getNumberOfConstituents(IMethodInvocation.class) + 1;
		}

		return weight;
	}

	public String getDefinition() {
		final String def =
			"Weight of an entity as the number of method invocations in each method";
		return def;
	}
}
