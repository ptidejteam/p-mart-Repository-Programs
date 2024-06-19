/**
 * CBO - Coupling Between Objects
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
import pom.metrics.IUnaryMetric;

public class FanOut extends AbstractMetric implements IMetric, IUnaryMetric,
		IBinaryMetric {

	public FanOut(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		double result = 0;
		final Iterator iterator =
			super.abstractLevelModel.getIteratorOnTopLevelEntities();
		while (iterator.hasNext()) {
			final IFirstClassEntity otherEntity = (IFirstClassEntity) iterator.next();

			if (!otherEntity.equals(anEntity)) {
				result += this.compute(anEntity, otherEntity);
			}
		}
		return result;
	}
	protected double concretelyCompute(
		final IFirstClassEntity entityA,
		final IFirstClassEntity entityB) {

		final double cboValue =
			super.methodPrimitives.numberOfUsesByFieldsOrMethods(
				entityB,
				entityA);
		return cboValue;
	}
	public String getDefinition() {
		final String def = "Coupling Between Objects of one entity";
		return def;
	}
}
