package pom.metrics.impl;

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class NOM extends AbstractMetric implements IMetric, IUnaryMetric {
	public NOM(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfDeclaredMethods(firstClassEntity).size();
	}
	public String getDefinition() {
		String def = "Counts all methods defined in a class";
		System.out.println(def);
		return def;
	}
}
