package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * RRFP - Relative Number of Class References From Other Packages
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
 * @author Yann
 * 
 * Modifications made to fit the new architecture
 */
public class RRFP extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public RRFP(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		final List entitiesOfAnalysedPackage = new ArrayList();
		final String packageName =
			super.classPrimitives.extractPackageName(anEntity);

		final Iterator iterator =
			super.classPrimitives.getIteratorOnTopLevelEntities();
		while (iterator.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) iterator.next();
			if (!(firstClassEntity instanceof IClass) && !(firstClassEntity instanceof IInterface)) {
				continue;
			}
			if (super.classPrimitives.extractPackageName(firstClassEntity).equals(
				packageName)) {

				entitiesOfAnalysedPackage.add(firstClassEntity);
			}
		}

		int result = 0;
		for (int i = 0; i < entitiesOfAnalysedPackage.size(); i++) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) entitiesOfAnalysedPackage.get(i);
			for (int j = 0; j < entitiesOfAnalysedPackage.size(); j++) {
				final IFirstClassEntity otherEntity =
					(IFirstClassEntity) entitiesOfAnalysedPackage.get(j);
				if (firstClassEntity.equals(otherEntity)) {
					continue;
				}
				if (super.methodPrimitives.numberOfUsesByFieldsOrMethods(
					firstClassEntity,
					otherEntity) > 0) {

					result++;
				}
			}
		}

		final double rfp =
			super.getUnaryMetricInstance("RFP").compute(anEntity);
		if (rfp + result == 0) {
			return 0;
		}
		else {
			return rfp / (rfp + result);
		}
	}
	public String getDefinition() {
		final String def =
			"RFP divided by the sum of RFP and the number of internal class references";
		return def;
	}
}
