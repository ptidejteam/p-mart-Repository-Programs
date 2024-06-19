/**
 * oneWayCoupling - One Way Coupling
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

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IBinaryMetric;
import pom.metrics.IMetric;

public class oneWayCoupling extends AbstractMetric implements IMetric,
		IBinaryMetric {

	public oneWayCoupling(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		double result = 0;
		final Iterator iterator =
			this.abstractLevelModel.getIteratorOnTopLevelEntities();
		while (iterator.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iterator.next();
			result += this.concretelyCompute(anEntity, firstClassEntity);
		}
		return result;
	}
	protected double concretelyCompute(
		final IFirstClassEntity anEntityA,
		final IFirstClassEntity anEntityB) {
		double value =
			super.methodPrimitives.numberOfUsesByFieldsOrMethods(
				anEntityA,
				anEntityB);
		return value;
	}
	public String getDefinition() {
		final String def =
			"Coupling Between Objects of two entities in one way only: \"a\" is coupled with \"b\".";
		return def;
	}
}