package pom.metrics.impl;

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 * @author Yann
 */
public class DCC extends AbstractMetric implements IMetric, IUnaryMetric {
	public DCC(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	public String getDefinition() {
		final String def =
			"Returns the number of classes that a class is directly related to (by attribute declarations and message passing)";
		return def;
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		double result = 0;

		final Iterator iterator =
			super.classPrimitives.getIteratorOnTopLevelEntities();
		while (iterator.hasNext()) {
			final IFirstClassEntity otherEntity = (IFirstClassEntity) iterator.next();

			if (!otherEntity.equals(anEntity)) {
				result +=
					this.methodPrimitives.numberOfUsesByFieldsOrMethods(
						anEntity,
						otherEntity)
							+ this.methodPrimitives
								.numberOfUsesByFieldsOrMethods(
									otherEntity,
									anEntity);
			}
		}

		return result;
	}
}
