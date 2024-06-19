/**
 * EIC - Number of External Inheritance as Client
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
import java.util.Iterator;
import java.util.List;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IClass;
import padl.kernel.IFirstClassEntity;
import padl.kernel.IInterface;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

public class EIC extends AbstractMetric implements IMetric, IUnaryMetric {
	/**
	 * @author Huynh D.L.
	 * @since 24/28/2005
	 * 
	 * Should be declared as protected constructor to make FacadeOfMetrics
	 * really useful.
	 * 
	 * @param anAbstractLevelModel
	 */
	public EIC(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}

	public double concretelyCompute(final IFirstClassEntity firstClassEntity) {
		return this.listOfElements(firstClassEntity).size();
	}

	public String getDefinition() {
		String def =
			"the number of inheritance relationships in which superclasses are in external packages.";
		System.out.println(def);
		return def;
	}

	private List listOfElements(IFirstClassEntity firstClassEntity) {
		List eicList = new ArrayList();
		List classesOfAnalysedPackage = new ArrayList();
		String packageName = super.classPrimitives.extractPackageName(firstClassEntity);
		//		String key = packageName + " " + "EIC";
		/*
		 if (Metrics.cachedResults.containsKey(key))
		 return ((Double) Metrics.cachedResults.get(key)).doubleValue();
		 */

		final Iterator iterator =
			super.classPrimitives.getIteratorOnTopLevelEntities();
		while (iterator.hasNext()) {
			IFirstClassEntity d = (IFirstClassEntity) iterator.next();
			if ((!(d instanceof IClass)) && (!(d instanceof IInterface)))
				continue;
			if (super.classPrimitives.extractPackageName(d).equals(packageName))
				classesOfAnalysedPackage.add(d);
		}

		int nbrInheritanceRel = 0;

		for (int i = 0; i < classesOfAnalysedPackage.size(); i++) {
			final Iterator parents =
				((IFirstClassEntity) classesOfAnalysedPackage.get(i))
					.getIteratorOnInheritedEntities();
			while (parents.hasNext()) {
				final IFirstClassEntity parent = (IFirstClassEntity) parents.next();
				if (!(super.classPrimitives.extractPackageName(parent)
					.equals(packageName))) {

					nbrInheritanceRel++;
					eicList.add(parent);
				}
			}
		}
		//		super.cachedResults.put(key, new Double(nbrInheritanceRel));

		return eicList;
	}
}
