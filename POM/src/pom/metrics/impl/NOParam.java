package pom.metrics.impl;

import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IOperation;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IParameter;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Alban Tiberghien
 * @since 2008//08/04
 */
public class NOParam extends AbstractMetric implements IMetric, IUnaryMetric {
	public NOParam(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public String getDefinition() {
		String def = "Compute the number of parameters of a method";
		System.out.println(def);
		return def;
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		int max = 0;
		final List methods =
			super.classPrimitives.listOfDeclaredMethods(firstClassEntity);
		final Iterator iteratorOnMethods = methods.iterator();
		while (iteratorOnMethods.hasNext()) {
			final IOperation m =
				(IOperation) iteratorOnMethods.next();
			max = Math.max(max, m.getNumberOfConstituents(IParameter.class));
		}

		return max;
	}
}
