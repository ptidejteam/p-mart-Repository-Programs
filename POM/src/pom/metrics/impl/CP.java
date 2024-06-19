package pom.metrics.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * CP - Number of Client Packages
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
public class CP extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * TODO: Rewrite by taking into account new IPackage constituent!
	 * 
	 * @param anAbstractLevelModel
	 */
	public CP(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity anEntity) {
		final Set packages = new HashSet();
		final List entitiesOfAnalysedPackage = new ArrayList();
		final String packageName =
			super.classPrimitives.extractPackageName(anEntity);

		final Iterator iteratorOnEntities =
			super.classPrimitives.getIteratorOnTopLevelEntities();
		while (iteratorOnEntities.hasNext()) {
			final IFirstClassEntity otherEntity = (IFirstClassEntity) iteratorOnEntities.next();

			if ((!(otherEntity instanceof IClass))
					&& (!(otherEntity instanceof IInterface))) {
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
				if (super.methodPrimitives.numberOfUsesByFieldsOrMethods(
					otherEntity,
					firstClassEntity) > 0) {
					packages.add(super.classPrimitives
						.extractPackageName(otherEntity));
				}
			}
		}

		packages.remove(super.classPrimitives.extractPackageName(anEntity));

		return packages.size();
	}
	public String getDefinition() {
		final String def =
			"the number of packages that depend on the package containing entity.";
		return def;
	}
}
