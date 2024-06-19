/**
 * REIP - Relative Number of External Inheritance as Provider
 * 
 * The following metric is related to packages, and is based
 * on the paper "Butterflies: A Visual Approach to Characterize Packages",
 * by Ducasse, Lanza and Ponisio.
 * 
 * @author Karim DHAMBRI
 * @since  2005/??/?? 
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

public class REIP extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public REIP(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		double eip = super.getUnaryMetricInstance("EIP").compute(firstClassEntity);
		double piir = super.getUnaryMetricInstance("PIIR").compute(firstClassEntity);
		if (piir + eip == 0)
			return 0;
		else
			return eip / (piir + eip);
	}

	public String getDefinition() {
		String def = "EIP divided by the sum of PIIR and EIP.";
		System.out.println(def);
		return def;
	}

	//	public List listOfElements(IEntity entity) {
	//		List list = new ArrayList();
	//
	//		list.addAll(super.getInstanceOfUnaryMetric("EIP").listOfElements(
	//				entity));
	//		list.addAll(super.getInstanceOfUnaryMetric("PIIR").listOfElements(
	//				entity));
	//
	//		return list;
	//	}
}
