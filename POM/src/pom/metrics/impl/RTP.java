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

/**
 * RTP - Number of Class References To Other Packages
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
public class RTP extends AbstractMetric implements IMetric, IUnaryMetric {
	public RTP(
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

		final Collection allOtherEntities =
			super.operators.difference(super.classPrimitives
				.getIteratorOnTopLevelEntities(), entitiesOfAnalysedPackage);

		double result = 0;
		for (int i = 0; i < entitiesOfAnalysedPackage.size(); i++) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) entitiesOfAnalysedPackage.get(i);
			final Iterator iteratorOnOtherEntities =
				allOtherEntities.iterator();
			while (iteratorOnOtherEntities.hasNext()) {
				final IFirstClassEntity otherEntity =
					(IFirstClassEntity) iteratorOnOtherEntities.next();
				if (!(otherEntity instanceof IClass)
						&& !(otherEntity instanceof IInterface)) {
					continue;
				}
				if (!(super.classPrimitives.extractPackageName(otherEntity)
					.equals(packageName))) {

					if (super.methodPrimitives.numberOfUsesByFieldsOrMethods(
						firstClassEntity,
						otherEntity) > 0) {

						result++;
					}
				}
			}
		}

		return result;
	}
	public String getDefinition() {
		final String def =
			"Number of class references from classes in the package containing entity to classes in other packages";
		return def;
	}
}
