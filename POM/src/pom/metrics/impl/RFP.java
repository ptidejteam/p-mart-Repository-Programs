/**
 * RFP - Number of Provider Packages
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class RFP extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public RFP(
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
			final IFirstClassEntity otherEntity = (IFirstClassEntity) iterator.next();
			if (!(otherEntity instanceof IClass)
					&& !(otherEntity instanceof IInterface)) {
				continue;
			}
			if (super.classPrimitives.extractPackageName(otherEntity).equals(
				packageName)) {
				entitiesOfAnalysedPackage.add(otherEntity);
			}
		}

		final Collection allOtherEntities =
			super.operators.difference(super.classPrimitives
				.getIteratorOnTopLevelEntities(), entitiesOfAnalysedPackage);

		double result = 0;
		for (int i = 0; i < entitiesOfAnalysedPackage.size(); i++) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) entitiesOfAnalysedPackage.get(i);
			for (Iterator it = allOtherEntities.iterator(); it.hasNext();) {
				final IFirstClassEntity otherEntity = (IFirstClassEntity) it.next();
				if (!(otherEntity instanceof IClass)
						&& !(otherEntity instanceof IInterface)) {
					continue;
				}
				if (!(super.classPrimitives.extractPackageName(otherEntity)
					.equals(packageName))) {

					if (super.methodPrimitives.numberOfUsesByFieldsOrMethods(
						otherEntity,
						firstClassEntity) > 0) {

						result++;
					}
				}
			}
		}

		return result;
	}

	public String getDefinition() {
		final String def =
			"Number of class references from classes belonging to other packages to classes belonging to the package containing entity.";
		return def;
	}
}
