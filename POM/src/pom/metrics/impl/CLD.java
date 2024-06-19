/**
 * CLD - Class to Leaf Depth
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

public class CLD extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public CLD(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		final DIT DITInstance =
			new DIT(this.fileRepository, this.abstractLevelModel);

		final double DITOfEntity = DITInstance.compute(firstClassEntity);

		List list = listOfElements(firstClassEntity);

		return list.size() == 0 ? 0 : DITInstance.maxDIT(list) - DITOfEntity;
	}

	public String getDefinition() {
		String def = "Class to Leaf Depth of an entity.";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		final List descendentsOfEntity =
			super.classPrimitives.listOfDescendents(firstClassEntity);

		return descendentsOfEntity;
	}
}
