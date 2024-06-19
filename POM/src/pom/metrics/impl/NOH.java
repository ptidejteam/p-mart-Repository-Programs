package pom.metrics.impl;

import java.util.Iterator;
import padl.IFileRepository;
import padl.kernel.IAbstractLevelModel;
import padl.kernel.IFirstClassEntity;
import pom.metrics.IMetric;
import pom.metrics.IUnaryMetric;

/**
 * @author Foutse Khomh
 * @since  2007/03/01
 */
public class NOH extends AbstractMetric implements IMetric, IUnaryMetric {
	public NOH(
		final IFileRepository aFileRepository,
		final IAbstractLevelModel anAbstractLevelModel) {

		super(aFileRepository, anAbstractLevelModel);
	}
	protected double concretelyCompute(final IFirstClassEntity anEntity) {
		final IFirstClassEntity object =
			(IFirstClassEntity) this.abstractLevelModel
				.getTopLevelEntityFromID("java.lang.Object");
		final Iterator inheritingEntities =
			object.getIteratorOnInheritingEntities();
		double nbh = 0;
		while (inheritingEntities.hasNext()) {
			final IFirstClassEntity firstClassEntity = (IFirstClassEntity) inheritingEntities.next();
			if (super.classPrimitives.getNumberOfChildren(firstClassEntity) > 0) {
				nbh = nbh + 1;
			}
		}
		return nbh;
	}
	public String getDefinition() {
		final String def = "Number of class hierarchies in the design";
		return def;
	}
}
