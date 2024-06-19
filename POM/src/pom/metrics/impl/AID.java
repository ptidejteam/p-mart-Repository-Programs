/**
 * AID - Average Inheritance Depth
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
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class AID extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public AID(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final List parentsOfEntity = listOfElements(firstClassEntity);
		return parentsOfEntity.size() == 0 ? 0 : this.average(parentsOfEntity);
	}
	public String getDefinition() {
		final String def =
			"Average Inheritance Depth of an entity. Uses a recursive way to calculate it";
		return def;
	}

	/**
	 * Returns the average of the AID of entities contained in the list.
	 * 
	 * @param parents
	 * @return the average of the AID of entities contained in the list
	 */
	private double average(final List parents) {
		double total = 0;

		for (final Iterator iterEntity = parents.iterator(); iterEntity
			.hasNext();) {

			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iterEntity.next();
			total = total + 1 + this.compute(firstClassEntity);
		}

		return total / parents.size();
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		return super.classPrimitives.listOfAllDirectParents(firstClassEntity);
	}
}
