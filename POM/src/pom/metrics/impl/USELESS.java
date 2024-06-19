package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Alban Tiberghien
 * @since 2008/09/26
 */
public class USELESS extends AbstractMetric implements IMetric, IUnaryMetric {
	public USELESS(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public String getDefinition() {
		String def = "Useless metric : return the same value for all entity";
		System.out.println(def);
		return def;
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return 1;
	}
}