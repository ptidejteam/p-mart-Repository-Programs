/**
 * NCM - Number of Changed Methods
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

import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class NCM extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public NCM(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return super.getUnaryMetricInstance("NMA").compute(firstClassEntity)
				+ super.getUnaryMetricInstance("NMO").compute(firstClassEntity)
				+ super.getUnaryMetricInstance("NMI").compute(firstClassEntity);
	}

	public String getDefinition() {
		String def =
			"Returns the NCM (Number of Changed Methods) of an entity.";
		System.out.println(def);
		return def;
	}

	//	public List listOfElements(IEntity entity) {
	//		List list = new ArrayList();
	//
	//		list.addAll(super.getInstanceOfUnaryMetric("NMA").listOfElements(
	//				entity));
	//		list.addAll(super.getInstanceOfUnaryMetric("NMO").listOfElements(
	//				entity));
	//		list.addAll(super.getInstanceOfUnaryMetric("NMI").listOfElements(
	//				entity));
	//
	//		return list;
	//	}
}
