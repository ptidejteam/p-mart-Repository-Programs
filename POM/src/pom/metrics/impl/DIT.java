/**
 * DIT - Depth of inheritance tree
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

import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class DIT extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public DIT(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final List parentsOfEntity = this.listOfElements(firstClassEntity);
		return parentsOfEntity.size() == 0 ? 0 : 1 + maxDIT(parentsOfEntity);
	}

	public String getDefinition() {
		String def =
			"Returns the DIT (Depth of inheritance tree) of the entity. Uses a recursive way to calculate it";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		return this.classPrimitives.listOfAllDirectParents(firstClassEntity);
	}

	/**
	 * Returns the highest DIT in the list of entities This method is used by
	 * the DIT method to get the maximum DIT of the parents of an entity.
	 * 
	 * @param list
	 * @return the highest DIT in the list of entities
	 */
	protected double maxDIT(final List list) {
		final int size = list.size();
		final double[] resultDITs = new double[size];
		for (int i = 0; i < size; i++) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) list.get(i);
			resultDITs[i] = compute(firstClassEntity);
		}
		return this.maxValue(resultDITs);
	}

	/**
	 * Returns the max value in an array of integers If the array is null, the
	 * returned value is -1.
	 * 
	 * @param array
	 * @return the max value in an array
	 */
	private double maxValue(final double[] array) {
		if (array == null) {
			return -1;
		}
		double maxValue = -1;
		for (int i = 0; i < array.length; i++) {
			if (maxValue < array[i]) {
				maxValue = array[i];
			}
		}
		return maxValue;
	}
}
